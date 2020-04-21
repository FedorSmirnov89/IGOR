package optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opt4j.core.Genotype;
import org.opt4j.core.Individual;
import org.opt4j.core.Individual.State;
import org.opt4j.core.IndividualStateListener;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.optimizer.OptimizerIterationListener;
import org.opt4j.core.start.Constant;
import org.opt4j.satdecoding.Model;
import org.opt4j.satdecoding.SATGenotype;
import org.opt4j.satdecoding.SATManager;
import org.opt4j.satdecoding.Solver;
import org.opt4j.satdecoding.TimeoutException;
import org.opt4j.satdecoding.VarOrder;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import blueprint.BluePrintGeneratorAbstract;
import blueprint.BluePrintClassContainer;
import blueprint.BluePrintProvider;
import blueprint.BluePrintSat;
import entity.LabeledSample;
import entity.LabeledSampleFactory;
import python.CommunicationClassContainer;
import python.CommunicationParser;
import python.RequestHandler;

/**
 * Main class for the dynamic adjustment of the variable order. Handles the
 * whole management, i.e. gathering the information about the known individuals,
 * communicating with the machine learning server, and adjusting the order and
 * the genotypes according to the information it receives.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public abstract class VariableOrderManagerAbstract implements IndividualStateListener, OptimizerIterationListener, SATManager {

	protected static final String TIMEOUT_EXCEPTION_MESSAGE = "Timeout during SAT solving";
	// list of important variables, ordered according to their importance
	protected List<Object> importantVariables = new ArrayList<>();
	// interval of the information update
	protected final int iterationInterval;
	// the current iteration
	protected int currentIteration = 0;
	// reference to the SAT solver
	protected final Solver solver;
	// set of the currently gathered solving samples
	protected final Set<LabeledSample> curSampleBatch = new HashSet<>();
	protected final int maxNumberSolvingSamples;
	protected final Map<Genotype, Map<Object, Boolean>> genoToModelMap = new HashMap<>();

	protected final LabeledSampleFactory sampleFactory;
	protected final CommunicationParser commParser;
	protected final RequestHandler requestHandler;
	protected final ImportanceUpdate importanceUpdate;
	protected final BluePrintRepair bluePrintRepair;
	protected final BluePrintCreator bluePrintCreator;
	protected final BluePrintGeneratorAbstract bluePrintGenerator;
	protected final ModelMemory modelMemory;
	protected final BluePrintOrderConfigurator orderConfigurator;
	protected final BluePrintProvider bluePrintProvider;

	@Inject
	public VariableOrderManagerAbstract(BluePrintClassContainer bluePrintContainer,
			CommunicationClassContainer communicationContainer,
			@Constant(value = "maxSolvingSampleNumber", namespace = VariableOrderManagerAbstract.class) int maxNumSolvingSamples,
			@Constant(value = "iterationInterval", namespace = VariableOrderManagerAbstract.class) int iterationInterval,
			Solver solver, ImportanceUpdate importanceUpdate, ModelMemory modelMemory) {
		this.iterationInterval = iterationInterval;
		this.maxNumberSolvingSamples = maxNumSolvingSamples;
		this.solver = solver;
		this.sampleFactory = communicationContainer.getSampleFactory();
		this.commParser = communicationContainer.getCommParser();
		this.requestHandler = communicationContainer.getRequestHandler();
		this.importanceUpdate = importanceUpdate;
		this.bluePrintRepair = bluePrintContainer.getBluePrintRepair();
		this.bluePrintCreator = bluePrintContainer.getBluePrintCreator();
		this.bluePrintGenerator = bluePrintContainer.getBluePrintGenerator();
		this.modelMemory = modelMemory;
		this.orderConfigurator = bluePrintContainer.getOrderConfigurator();
		this.bluePrintProvider = bluePrintContainer.getBluePrintProvider();
	}

	/**
	 * Returns the used solver.
	 * 
	 * @return the used solver
	 */
	@Override
	public Solver getSolver() {
		return solver;
	}

	@Override
	public Genotype createSATGenotype(List<Object> variables, Map<Object, Double> lowerBounds,
			Map<Object, Double> upperBounds, Map<Object, Double> priorities, Map<Object, Boolean> phases) {
		// set the initially encoded variables for the initial blueprint creator
		if (!bluePrintProvider.isInit()) {
			Set<Object> uniqueEncodedVars = new HashSet<>(variables);
			BluePrintSat initialBlueprint = bluePrintGenerator.createInitialBlueprint(uniqueEncodedVars);
			bluePrintProvider.setCurrentBlueprint(initialBlueprint, uniqueEncodedVars);
		}
		return bluePrintCreator.createGenotype();
	}

	@Override
	public Model decodeSATGenotype(List<Object> variables, Genotype genotype) {
		VarOrder varOrder = orderConfigurator.generateVarOrder(genotype);
		// weird solver magic
		varOrder.setVarInc(1.0 / (2.0 * variables.size()));
		varOrder.setVarDecay(1.0 / 0.95);
		// solve the constraints using the specified order
		Model result = null;
		try {
			result = solver.solve(varOrder);
		} catch (TimeoutException timeout) {
			throw new IllegalArgumentException(TIMEOUT_EXCEPTION_MESSAGE);
		}
		if (result == null) {
			throw new IllegalStateException("No solution for the constraint set found.");
		}
		if (!commParser.isInit()) {
			List<Object> satVarList = new ArrayList<>(result.getVars());
			commParser.initSatVariables(satVarList);
			sampleFactory.setSatVariables(satVarList);
		}
		// remember the model for the current genotype
		Map<Object, Boolean> model = new HashMap<>();
		for (Object var : result.getVars()) {
			if (result.get(var) == null) {
				throw new IllegalStateException("No entry for var " + var.toString() + " in the solution model.");
			}
			model.put(var, result.get(var));
		}
		genoToModelMap.put(genotype, model);
		return result;
	}

	/**
	 * Reacts to the given server response by (I) parsing it to an importance map,
	 * (II) updating the variable importance list, and (III) updating the genotypes
	 * of the individuals in the population to the set of the variables currently
	 * considered important.
	 * 
	 * @param serverResponse
	 */
	protected void updateOrderInformation(String serverResponse) {
		// translate the answer
		Map<Object, Double> variableImportanceMapUpdate = commParser.readServerImportanceResponse(serverResponse);
		if (variableImportanceMapUpdate.isEmpty()) {
			// no important variables yet => nothing to update
			return;
		}
		// update the list of the important variables
		importantVariables = importanceUpdate.updateVariableImportance(variableImportanceMapUpdate);
		// repair the genotypes of the current population so that the next
		// generation (built based on the current one) can be correctly
		// decoded using the variable order
		bluePrintRepair.repairIndividualPopulation(importantVariables);
	}

	/**
	 * Processes the given individual. An evaluated individual is processed into a
	 * sample and added to the current batch while a phenotype individual is added
	 * to the {@link ModelMemory}.
	 * 
	 * @param individual
	 *            the evaluated individual
	 */
	@Override
	public void individualStateChanged(Individual individual) {
		if (individual.getState().equals(State.EVALUATED) && individual.getPhenotype() != null) {
			if (curSampleBatch.size() < maxNumberSolvingSamples) {
				LabeledSample sample = sampleFactory.createLabeledSample(individual);
				curSampleBatch.add(sample);
			}
			if (!commParser.isInit()) {
				commParser.initColumnNames(individual.getObjectives().size());
			}
		} else if (individual.getState().equals(State.PHENOTYPED)) {
			// look up the genotype and save it to the model memory
			Genotype geno = individual.getGenotype();
			Genotype satGeno = null;
			if (geno instanceof SATGenotype) {
				satGeno = geno;
			} else {
				if (!(geno instanceof CompositeGenotype<?, ?>)) {
					throw new IllegalArgumentException("Genotype is neither SAT nor composite");
				}
				@SuppressWarnings("unchecked")
				CompositeGenotype<String, Genotype> composite = (CompositeGenotype<String, Genotype>) geno;
				satGeno = composite.get("SAT");
			}
			if (!genoToModelMap.containsKey(satGeno)) {
				throw new IllegalArgumentException("Unknown SAT genotype.");
			}
			modelMemory.rememberModel(individual, genoToModelMap.get(satGeno));
		}
	}
}

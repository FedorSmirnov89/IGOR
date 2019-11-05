package optimization;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opt4j.core.Genotype;
import org.opt4j.core.Individual;
import org.opt4j.core.genotype.BooleanGenotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.genotype.DoubleBounds;
import org.opt4j.core.genotype.DoubleGenotype;
import org.opt4j.core.optimizer.Population;
import org.opt4j.satdecoding.SATGenotype;

import com.google.inject.Inject;

import blueprint.BluePrintProvider;
import blueprint.BluePrintSat;
import blueprint.BluePrintUpdater;

/**
 * 
 * Class responsible for the repair of the genotypes (making them match the
 * constraint solution that they result in).
 * 
 * @author Fedor Smirnov
 *
 */
public class BluePrintRepair {

	protected final ModelMemory modelMemory;
	protected final BluePrintProvider blueprintProvider;
	protected final BluePrintUpdater bluePrintUpdater;
	protected final Population population;

	@Inject
	public BluePrintRepair(ModelMemory modelMemory, BluePrintProvider blueprintProvider,
			BluePrintUpdater bluePrintUpdater, Population population) {
		this.modelMemory = modelMemory;
		this.blueprintProvider = blueprintProvider;
		this.bluePrintUpdater = bluePrintUpdater;
		this.population = population;
	}

	/**
	 * Updates both the population and the current blueprint to match the set of
	 * variables that are currently considered as important.
	 * 
	 * @param importantVariables
	 *            the set of variables currently considered as important
	 */
	public synchronized void repairIndividualPopulation(List<Object> importantVariables) {
		// get the current blueprint
		BluePrintSat curBlueprint = blueprintProvider.getCurrentBlueprint();
		Set<Object> encodedVariables = blueprintProvider.getCurrentlyEncodedVariables();
		// create the updated blueprint
		BluePrintSat updatedBlueprint = bluePrintUpdater.createUpdatedBlueprint(encodedVariables, importantVariables);
		Set<Object> updatedEncodedVariables = bluePrintUpdater.getUpdatedEncodedVariables(encodedVariables,
				importantVariables);
		if (!updatedEncodedVariables.containsAll(encodedVariables)) {
			throw new IllegalArgumentException("number of encoded variables decreased.");
		}
		// iterate the population and repair the indis; remove the indis without
		// phenotype (caused by a timeout)
		Set<Individual> toRemove = new HashSet<>();
		for (Individual indi : population) {
			if (indi.getPhenotype() == null) {
				toRemove.add(indi);
			} else {
				repairIndividual(indi, curBlueprint, updatedBlueprint);
			}
		}
		population.removeAll(toRemove);
		// update the provider with the updated blueprint
		blueprintProvider.setCurrentBlueprint(updatedBlueprint, updatedEncodedVariables);
	}

	/**
	 * Repair the sat geno of the provided indi, which is built according to the
	 * current blueprint, so that is is built in correspondance to the updated
	 * blueprint.
	 * 
	 * @param indi
	 *            the given individual
	 * @param currentBlueprint
	 *            the blueprint considered until now
	 * @param updatedBlueprint
	 *            the new blueprint (active as soon as the repair is done)
	 */
	protected void repairIndividual(Individual indi, BluePrintSat currentBlueprint, BluePrintSat updatedBlueprint) {
		// get the SAT geno of the indi
		Genotype geno = indi.getGenotype();
		boolean composite = true;
		SATGenotype satGeno = null;
		if (geno instanceof SATGenotype) {
			satGeno = (SATGenotype) geno;
			composite = false;
		} else {
			@SuppressWarnings("unchecked")
			CompositeGenotype<String, Genotype> compositeGeno = (CompositeGenotype<String, Genotype>) geno;
			Genotype gen = compositeGeno.get("SAT");
			satGeno = (SATGenotype) gen;
		}
		if (satGeno == null) {
			throw new IllegalArgumentException("the given individual does not have a SAT genotype");
		}
		DoubleGenotype curDoubleVector = satGeno.getDoubleVector();
		// create the updated vectors
		BooleanGenotype updatedBoolVector = new BooleanGenotype();
		DoubleBounds updatedBounds = updatedBlueprint.getBoundsForDoubleGeno();
		DoubleGenotype updatedDoubleVector = new DoubleGenotype(updatedBounds);
		// fill the new bool vector
		Map<Object, Boolean> satAssignmentMap = modelMemory.getModel(indi);
		for (int idx = 0; idx < updatedBlueprint.getBooleanSize(); idx++) {
			Object var = updatedBlueprint.getVarForBoolIdx(idx);
			boolean assignment = satAssignmentMap.get(var);
			updatedBoolVector.add(assignment);
		}
		// fill the new double vector
		for (int idx = 0; idx < updatedBlueprint.getDoubleSize(); idx++) {
			Object var = updatedBlueprint.getVarForDoubleIdx(idx);
			double curOrder = -1.;
			try {
				curOrder = curDoubleVector.get(currentBlueprint.getDoubleIdxForVar(var));
				if (curOrder < updatedBounds.getLowerBound(idx) || curOrder > updatedBounds.getUpperBound(idx)) {
					throw new IllegalStateException("Bounds do not match during population update");
				}
			} catch (IllegalArgumentException illExc) {
				// variable in the updated, but not in the current double list => must be a
				// variable that used to be important, but now is considered unimportant => must
				// currently be in the fixedOrderMap
				curOrder = updatedBlueprint.getBoundsForDoubleGeno()
						.getUpperBound(updatedBlueprint.getDoubleIdxForVar(var));
			}
			updatedDoubleVector.add(curOrder);
		}
		SATGenotype updatedSatGeno = new SATGenotype(updatedBoolVector, updatedDoubleVector);
		// set the repaired geno for the individual
		if (!composite) {
			indi.setGenotype(updatedSatGeno);
		} else {
			@SuppressWarnings("unchecked")
			CompositeGenotype<String, Genotype> compositeGeno = (CompositeGenotype<String, Genotype>) indi
					.getGenotype();
			compositeGeno.put("SAT", updatedSatGeno);
		}
	}
}

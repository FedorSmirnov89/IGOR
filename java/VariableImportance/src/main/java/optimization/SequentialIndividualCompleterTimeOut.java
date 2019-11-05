package optimization;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.core.Genotype;
import org.opt4j.core.Individual;
import org.opt4j.core.Individual.State;
import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.common.completer.SequentialIndividualCompleter;
import org.opt4j.core.optimizer.Control;
import org.opt4j.core.optimizer.TerminationException;
import org.opt4j.core.problem.Decoder;
import org.opt4j.core.problem.Evaluator;
import org.opt4j.satdecoding.TimeoutException;

import com.google.inject.Inject;

/**
 * Extends the default {@link SequentialIndividualCompleter} by the awareness
 * for timeouts during the decoding (in particular during the SAT solving
 * process). A {@link TimeoutException} then does not lead to the termination of
 * the optimization. Instead, the individual is treated as infeasible in all
 * objectives.
 * 
 * @author Fedor Smirnov
 *
 */
public class SequentialIndividualCompleterTimeOut extends SequentialIndividualCompleter {

	protected final Set<Objective> optimizationObjectives = new HashSet<>();

	/**
	 * Returns {@code true} if the objectives are already known, that is, at least
	 * one individual was already processed without timeouts.
	 * 
	 * @return {@code true} if the objectives are already known
	 */
	protected boolean objectivesKnown() {
		return !optimizationObjectives.isEmpty();
	}

	@Inject
	public SequentialIndividualCompleterTimeOut(Control control, Decoder<Genotype, Object> decoder,
			Evaluator<Object> evaluator) {
		super(control, decoder, evaluator);
	}

	@Override
	public void complete(Iterable<? extends Individual> iterable) throws TerminationException {
		for (Individual individual : iterable) {
			if (!individual.isEvaluated()) {
				completeIndividual(individual);
			}
		}
	}

	/**
	 * Completes a single individual
	 * 
	 * @param indi
	 *            the individual to complete
	 */
	protected void completeIndividual(Individual individual) throws TerminationException {
		control.checkpoint();
		boolean timeOut = false;
		try {
			decode(individual);
		} catch (IllegalArgumentException illegalArg) {
			String message = illegalArg.getMessage();
			if (message.equals(VariableOrderManagerAbstract.TIMEOUT_EXCEPTION_MESSAGE)) {
				timeOut = true;
				if (!objectivesKnown()) {
					throw new IllegalArgumentException(
							"Timeout for the very first individual. Consider using a higher timeout value.");
				}
				setIndividualInfeasible(individual);
			} else {
				throw illegalArg;
			}
		}
		if (!timeOut) {
			control.checkpoint();
			evaluate(individual);
			control.checkpoint();
			if (!objectivesKnown()) {
				for (Objective obj : individual.getObjectives().getKeys()) {
					optimizationObjectives.add(obj);
				}
			}
		}
	}

	/**
	 * Marks all objectives of the given individual as infeasible.
	 * 
	 * @param indi
	 */
	protected void setIndividualInfeasible(Individual indi) {
		indi.setState(State.EVALUATED);
		Objectives objectives = new Objectives();
		for (Objective obj : optimizationObjectives) {
			objectives.add(obj, Objective.INFEASIBLE);
		}
		indi.setObjectives(objectives);
	}
}

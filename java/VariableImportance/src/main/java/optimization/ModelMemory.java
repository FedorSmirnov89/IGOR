package optimization;

import java.util.Map;

import org.opt4j.core.Individual;

import com.google.inject.ImplementedBy;

/**
 * Interface for classes that keep the information about the solved model for a
 * certain set of individuals.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(ModelMemoryMap.class)
public interface ModelMemory {

	/**
	 * Remember the model of the given individual.
	 * 
	 * @param indi
	 *            the given individual
	 * @param model
	 *            the given model
	 */
	public void rememberModel(Individual indi, Map<Object, Boolean> model);

	/**
	 * Return the SAT model of the given individual.
	 * 
	 * @param indi
	 *            the given individual
	 * @return the SAT model of the given individual
	 */
	public Map<Object, Boolean> getModel(Individual indi);

}

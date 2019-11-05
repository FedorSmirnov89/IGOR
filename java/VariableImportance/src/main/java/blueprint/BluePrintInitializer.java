package blueprint;

import java.util.Set;

/**
 * The {@link BluePrintInitializer} creates the initial genetic blueprint that is
 * used prior to any learning process.
 * 
 * @author Fedor Smirnov
 *
 */
public interface BluePrintInitializer {

	/**
	 * Create the initial genotype blueprint.
	 * 
	 * @param encodedVariables
	 * @return
	 */
	public BluePrintSat createInitialBlueprint(Set<Object> encodedVariables);
}

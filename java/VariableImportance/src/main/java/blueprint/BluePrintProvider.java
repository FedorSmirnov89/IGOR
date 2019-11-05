package blueprint;

import java.util.Set;

import com.google.inject.ImplementedBy;

/**
 * Interface for all classes that are used for the management of the genotype
 * blueprint used during the optimization.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(BluePrintProviderBasic.class)
public interface BluePrintProvider {

	/**
	 * 
	 * Returns {@code true} if a blueprint is set.
	 * 
	 * @return {@code true} if a blueprint is set, {@code false} otherwise
	 */
	public boolean isInit();

	/**
	 * Returns the currently used blueprint.
	 * 
	 * @return the currently used blueprint
	 */
	public BluePrintSat getCurrentBlueprint();

	/**
	 * Set the currently correct blueprint. Remember the set of variables that are
	 * encoded right now.
	 * 
	 * @param updatedBlueprint
	 *            the new {@link BluePrintSat} that is used from now on
	 * @param encodedVariables
	 *            the set of encoding variables that make up the currently used
	 *            genotype
	 */
	public void setCurrentBlueprint(BluePrintSat updatedBlueprint, Set<Object> encodedVariables);

	/**
	 * Returns the set of the variables encoded in the current genotype.
	 * 
	 * @return the set of the variables encoded in the current genotype
	 */
	public Set<Object> getCurrentlyEncodedVariables();

}

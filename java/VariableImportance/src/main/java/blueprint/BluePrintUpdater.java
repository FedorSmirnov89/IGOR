package blueprint;

import java.util.List;
import java.util.Set;

import com.google.inject.ImplementedBy;

/**
 * The {@link BluePrintUpdater} updates the currently used genetic blueprint.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(BluePrintGeneratorAbstract.class)
public interface BluePrintUpdater {

	/**
	 * Create the current {@link BluePrintSat} based on the provided information
	 * about the variable importance.
	 * 
	 * @param encodedVariables
	 *            the set of all variables that are encoded within the SAT genotype
	 * @param importantVAriables
	 *            the set of variables currently considered important
	 * @return the current {@link BluePrintSat} based on the provided information
	 *         about the variable importance
	 */
	public BluePrintSat createUpdatedBlueprint(Set<Object> encodedVariables, List<Object> importantVariables);

	/**
	 * Return the current set of encoded variables based on the variables considered
	 * to be important.
	 * 
	 * @param encodedVariables
	 *            the set of all variables that are encoded within the SAT genotype
	 * @param importantVariables
	 *            the set of variables currently considered important
	 * @return the current set of encoded variables based on the variables
	 *         considered to be important
	 */
	public Set<Object> getUpdatedEncodedVariables(Set<Object> encodedVariables, List<Object> importantVariables);

}

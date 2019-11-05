package json;

import java.util.List;

/**
 * The {@link ImportanceRanking} captures the information about the current
 * importance ranking of the encoding variables.
 * 
 * @author Fedor Smirnov
 *
 */
public class ImportanceRanking {

	protected final List<String> variableIds;
	protected final List<Double> importanceValues;

	public ImportanceRanking(List<String> variableIds, List<Double> importanceValues) {
		if (variableIds.size() != importanceValues.size()) {
			throw new IllegalArgumentException("the variable list does not match the importance list.");
		}
		this.variableIds = variableIds;
		this.importanceValues = importanceValues;
	}

	/**
	 * Returns a list of the ids (obtained by the toString()) of the variables. The
	 * order hereby matches the order in the double list obtained by the
	 * getImportanceValues().
	 * 
	 * @return a list of the ids (obtained by the toString()) of the variables
	 */
	public List<String> getVariableIds() {
		return variableIds;
	}

	/**
	 * Returns a list of the importance values of the variables.
	 * 
	 * @return a list of the importance values of the variables
	 */
	public List<Double> getImportanceValues() {
		return importanceValues;
	}
}

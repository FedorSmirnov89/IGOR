package json;

import java.util.List;

/**
 * The {@link SolvingSampleBatch} is an information container that is used to
 * summarize the information of a set of solving samples and to generate the
 * java-side request that is then sent to the Python server.
 * 
 * @author Fedor Smirnov
 *
 */
public class SolvingSampleBatch {

	// the column names of the data frame (ids of the variables and the names of the
	// fitness functions) - basically the header row of the df
	private final List<String> columns;
	// names of the rows (could be used to distinguish individuals, just an
	// index for now) - basically the leftmost column
	private final List<Integer> index;
	// the outer list contains the individuals. Each of them is a list
	// consisting of the features (the variable assignments) and the fitness
	// values
	private final List<List<Double>> data;

	public SolvingSampleBatch(List<String> columns, List<Integer> index, List<List<Double>> data) {
		if (data.isEmpty()) {
			throw new IllegalArgumentException("No data provided.");
		}
		if (index.size() != data.size()) {
			throw new IllegalArgumentException("The index does not match the data.");
		}
		if (columns.size() != data.get(0).size()) {
			throw new IllegalArgumentException("The columns do not match the data.");
		}
		this.columns = columns;
		this.index = index;
		this.data = data;
	}

	/**
	 * Returns the column names (variable ids + objective names).
	 * 
	 * @return the column names (variable ids + objective names)
	 */
	public List<String> getColumns() {
		return columns;
	}

	/**
	 * Returns the row index of the df (the leftmost column).
	 * 
	 * @return the row index of the df (the leftmost column)
	 */
	public List<Integer> getIndex() {
		return index;
	}

	/**
	 * Returns the actual data (each list is a row containing the variable
	 * assignments and the objective values).
	 * 
	 * @return the actual data (each list is a row containing the variable
	 *         assignments and the objective values)
	 */
	public List<List<Double>> getData() {
		return data;
	}
}

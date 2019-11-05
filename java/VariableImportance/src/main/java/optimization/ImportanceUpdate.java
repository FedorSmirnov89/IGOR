package optimization;

import java.util.List;
import java.util.Map;

import com.google.inject.ImplementedBy;

/**
 * Interface for all classes that do update the importance ranking of the
 * encoding variables based on the result of the importance measurement
 * performed by the Python server.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(ImportanceUpdateDefault.class)
public interface ImportanceUpdate {

	/**
	 * Returns {@code true} if the class has been initialized.
	 * 
	 * @return {@code true} if the class has been initialized
	 */
	public boolean isInit();

	/**
	 * Take the the importance information about the current batch and return the
	 * current ordered list of the important variables.
	 * 
	 * @param newVariableImportanceMap
	 *            the map containing the importance information obtained based on
	 *            the current sample batch
	 * @return a list of the variables ordered by importance in descending order
	 */
	public List<Object> updateVariableImportance(Map<Object, Double> newVariableImportanceMap);
}

package optimization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;

import entity.VariableImportance;

/**
 * Generates an importance list solely based on the given importance map.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class ImportanceUpdateDefault implements ImportanceUpdate {

	@Override
	public boolean isInit() {
		return true;
	}

	@Override
	public List<Object> updateVariableImportance(Map<Object, Double> newVariableImportanceMap) {
		return generateOrderedList(newVariableImportanceMap);
	}

	/**
	 * Generates a list of the important variables, ordered in descending order
	 * (more important => less important)
	 * 
	 * @param importanceMap
	 *            a map containing the importance values of the encoding variables
	 * @return
	 */
	protected List<Object> generateOrderedList(Map<Object, Double> importanceMap) {
		PriorityQueue<VariableImportance> heap = new PriorityQueue<>();
		for (Entry<Object, Double> entry : importanceMap.entrySet()) {
			Object variable = entry.getKey();
			Double importance = entry.getValue();
			heap.add(new VariableImportance(variable, importance));
		}
		if (heap.isEmpty()) {
			throw new IllegalArgumentException("No important variables present.");
		}
		List<Object> result = new ArrayList<>();
		while (true) {
			VariableImportance imp = heap.poll();
			if (imp == null) {
				break;
			} else {
				result.add(imp.getVariable());
			}
		}
		return Lists.reverse(result);
	}
}

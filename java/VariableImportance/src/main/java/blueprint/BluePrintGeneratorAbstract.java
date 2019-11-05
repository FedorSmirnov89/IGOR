package blueprint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opt4j.core.genotype.DoubleBounds;

import net.sf.opendse.optimization.VariableClassOrder;

/**
 * The {@link BluePrintGeneratorAbstract} is the parent class of all classed
 * that inject information about the importance-based variable resolution order
 * into the genetic blueprint.
 * 
 * @author Fedor Smirnov
 *
 */
public abstract class BluePrintGeneratorAbstract implements BluePrintInitializer, BluePrintUpdater{
	// the bound interval reserved for the variables considered important
	protected final double importanceOrderInterval;
	// the variable order used for the decoding of the SAT genotype
	protected final VariableClassOrder classOrder;

	public BluePrintGeneratorAbstract(double importanceOrderInterval, VariableClassOrder classOrder) {
		this.classOrder = classOrder;
		this.importanceOrderInterval = importanceOrderInterval;
	}

	/**
	 * Returns a map of the variable order that matches the provided variable list
	 * (first var in the list is the first variable set during the decoding).
	 * 
	 * @param importantVarsList
	 *            the list of importance variable (ordered in order of descending
	 *            importance)
	 * @return a map of the variable order that matches the provided variable list
	 */
	protected Map<Object, Double> createFixedOrderMap(List<Object> importantVarsList) {
		double curOrder = 1.0;
		int importantVarNum = importantVarsList.size();
		double orderStep = importanceOrderInterval / importantVarNum;
		Map<Object, Double> result = new HashMap<>();
		for (int idx = 0; idx < importantVarNum; idx++) {
			double nextOrder = curOrder - orderStep;
			double order = (nextOrder + curOrder) / 2;
			Object var = importantVarsList.get(idx);
			result.put(var, order);
			curOrder = nextOrder;
		}
		return result;
	}

	/**
	 * Returns the bounds that are configured for the unimportant variables.
	 * 
	 * @param varDoubleList
	 *            the list of unimportant variables
	 * @return the bounds that are configured for the unimportant variables
	 */
	protected DoubleBounds createBounds(Object[] varDoubleList) {
		double[] lbs = new double[varDoubleList.length];
		double[] ubs = new double[varDoubleList.length];
		int unimportantVarsNum = varDoubleList.length;
		int orderSize = classOrder.getOrderSize();
		if (unimportantVarsNum > 0) {
			double orderStep = (1.0 - importanceOrderInterval) / orderSize;
			for (int idx = 0; idx < varDoubleList.length; idx++) {
				Object var = varDoubleList[idx];
				int orderIndex = classOrder.indexOf(var);
				if (orderIndex == -1) {
					// no specified order
					lbs[idx] = 0.0;
					ubs[idx] = 1. - importanceOrderInterval;
				} else {
					lbs[idx] = (orderSize - orderIndex - 1) * orderStep;
					ubs[idx] = (orderSize - orderIndex) * orderStep;
				}
			}
		}
		return new DoubleBounds(lbs, ubs);
	}
}

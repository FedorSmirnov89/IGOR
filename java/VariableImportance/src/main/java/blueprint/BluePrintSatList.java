package blueprint;

import java.util.HashMap;
import java.util.Map;

import org.opt4j.core.genotype.DoubleBounds;

/**
 * Default implementation of the {@link BluePrintSat}, based on two
 * arrays that actually store the two lists (arrays) for the objectives that are
 * subject to the genotype encoding.
 * 
 * @author Fedor Smirnov
 *
 */
public class BluePrintSatList implements BluePrintSat {
	// array of all variables that are represented by a boolean in the SAT genotype
	private final Object[] varBooleanList;
	// array of all variables that are represented by a double (representing an
	// order that is subject to EA-exploration) in the SAT genotype
	private final Object[] varDoubleList;
	// map that maps variables to their index in the double array (their order is
	// explored)
	private final Map<Object, Integer> varToDoubleIdxMap;
	// the bounds of the double genotype (order matches the order in the double
	// array)
	private final DoubleBounds doubleBounds;
	// map that keeps track of the order of the variables whose order is fixed.
	private final Map<Object, Double> varToFixOrderMap;

	protected BluePrintSatList(Object[] varBooleanList, Object[] varDoubleList, DoubleBounds doubleBounds,
			Map<Object, Double> varToFixedOrderMap) {
		this.varBooleanList = varBooleanList;
		this.varDoubleList = varDoubleList;
		varToDoubleIdxMap = new HashMap<>();
		for (int idx = 0; idx < varDoubleList.length; idx++) {
			varToDoubleIdxMap.put(varDoubleList[idx], idx);
		}
		this.doubleBounds = doubleBounds;
		this.varToFixOrderMap = varToFixedOrderMap;
	}

	@Override
	public int getBooleanSize() {
		return varBooleanList.length;
	}

	@Override
	public int getDoubleSize() {
		return varDoubleList.length;
	}

	@Override
	public Object getVarForBoolIdx(int idx) {
		if (idx <  0 || idx >= varBooleanList.length) {
			throw new IllegalArgumentException("Invalid index requested for boolean genotype.");
		}
		return varBooleanList[idx];
	}

	@Override
	public Object getVarForDoubleIdx(int idx) {
		if (idx <  0 || idx >= varDoubleList.length) {
			throw new IllegalArgumentException("Invalid index requested for double genotype.");
		}
		return varDoubleList[idx];
	}

	@Override
	public int getDoubleIdxForVar(Object var) {
		if (!varToDoubleIdxMap.containsKey(var)) {
			throw new IllegalArgumentException("No double entry for Object " + var.toString());
		}
		return varToDoubleIdxMap.get(var);
	}

	@Override
	public DoubleBounds getBoundsForDoubleGeno() {
		return doubleBounds;
	}

	@Override
	public Map<Object, Double> getFixedOrderMap() {
		return varToFixOrderMap;
	}
}

package blueprint;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.opt4j.core.genotype.DoubleBounds;

public class BluePrintSatListTest {

	protected static Object first = new Object();
	protected static Object second = new Object();
	protected static Object third = new Object();

	protected static Object[] varBooleanList = { first, second, third };
	protected static Object[] varDoubleList = { second, third };

	protected static double[] lowerBound = { 0.05, 0.1 };
	protected static double[] upperBound = { 0.1, 1. };

	protected static DoubleBounds doubleBounds = new DoubleBounds(lowerBound, upperBound);

	protected Map<Object, Double> getFixedOrderMap() {
		Map<Object, Double> result = new HashMap<>();
		result.put(first, 0.025);
		return result;
	}

	@Test
	public void testSizes() {
		BluePrintSatList testObj = new BluePrintSatList(varBooleanList, varDoubleList, doubleBounds,
				getFixedOrderMap());
		assertEquals(3, testObj.getBooleanSize());
		assertEquals(2, testObj.getDoubleSize());
	}

	@Test
	public void testVarForBoolIdx() {
		BluePrintSatList testObj = new BluePrintSatList(varBooleanList, varDoubleList, doubleBounds,
				getFixedOrderMap());
		assertEquals(first, testObj.getVarForBoolIdx(0));
		assertEquals(second, testObj.getVarForBoolIdx(1));
		assertEquals(third, testObj.getVarForBoolIdx(2));
	}

	@Test
	public void testVarForDoubleIdx() {
		BluePrintSatList testObj = new BluePrintSatList(varBooleanList, varDoubleList, doubleBounds,
				getFixedOrderMap());
		assertEquals(second, testObj.getVarForDoubleIdx(0));
		assertEquals(third, testObj.getVarForDoubleIdx(1));
	}

	@Test
	public void testGetBounds() {
		BluePrintSatList testObj = new BluePrintSatList(varBooleanList, varDoubleList, doubleBounds,
				getFixedOrderMap());
		assertEquals(doubleBounds, testObj.getBoundsForDoubleGeno());
	}

	@Test
	public void testGetFixedMap() {
		BluePrintSatList testObj = new BluePrintSatList(varBooleanList, varDoubleList, doubleBounds,
				getFixedOrderMap());
		assertEquals(0.025, testObj.getFixedOrderMap().get(first), 0.0);
	}

	@Test
	public void testGetDoubleIdxToVariable() {
		BluePrintSatList testObj = new BluePrintSatList(varBooleanList, varDoubleList, doubleBounds,
				getFixedOrderMap());
		assertEquals(0, testObj.getDoubleIdxForVar(second));
		assertEquals(1, testObj.getDoubleIdxForVar(third));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testWrongBoolIdx1() {
		BluePrintSatList testObj = new BluePrintSatList(varBooleanList, varDoubleList, doubleBounds,
				getFixedOrderMap());
		testObj.getVarForBoolIdx(-1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testWrongBoolIdx2() {
		BluePrintSatList testObj = new BluePrintSatList(varBooleanList, varDoubleList, doubleBounds,
				getFixedOrderMap());
		testObj.getVarForBoolIdx(testObj.getBooleanSize());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testWrongDoubleIdx1() {
		BluePrintSatList testObj = new BluePrintSatList(varBooleanList, varDoubleList, doubleBounds,
				getFixedOrderMap());
		testObj.getVarForDoubleIdx(-1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testWrongDoubleIdx2() {
		BluePrintSatList testObj = new BluePrintSatList(varBooleanList, varDoubleList, doubleBounds,
				getFixedOrderMap());
		testObj.getVarForDoubleIdx(testObj.getDoubleSize());
	}
	
	@Test(expected= IllegalArgumentException.class)
	public void testWrongVariableDoubleRequest() {
		BluePrintSatList testObj = new BluePrintSatList(varBooleanList, varDoubleList, doubleBounds,
				getFixedOrderMap());
		testObj.getDoubleIdxForVar(first);
	}
}

package blueprint;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.core.genotype.DoubleBounds;

import net.sf.opendse.optimization.VariableClassOrder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BluePrintGeneratorAbstractTest {

	class BluePrintGeneratorMock extends BluePrintGeneratorAbstract {
		public BluePrintGeneratorMock(double importanceOrderInterval) {
			super(importanceOrderInterval, mock(VariableClassOrder.class));
		}

		public BluePrintGeneratorMock(double importanceOrderInterval, VariableClassOrder order) {
			super(importanceOrderInterval, order);
		}

		@Override
		public BluePrintSat createInitialBlueprint(Set<Object> encodedVariables) {
			return null;
		}

		@Override
		public BluePrintSat createUpdatedBlueprint(Set<Object> encodedVariables, List<Object> importantVariables) {
			return null;
		}

		@Override
		public Set<Object> getUpdatedEncodedVariables(Set<Object> encodedVariables, List<Object> importantVariables) {
			return null;
		}
	}

	@Test
	public void testEmptyImportantList() {
		BluePrintGeneratorMock mockGen = new BluePrintGeneratorMock(.2);
		assertTrue(mockGen.createFixedOrderMap(new ArrayList<>()).isEmpty());
	}

	@Test
	public void testCreateImportantMap() {
		BluePrintGeneratorMock mockGen = new BluePrintGeneratorMock(.2);
		Object first = new Object();
		Object second = new Object();
		List<Object> importanceList = new ArrayList<>();
		importanceList.add(first);
		importanceList.add(second);
		Map<Object, Double> orderMap = mockGen.createFixedOrderMap(importanceList);
		assertEquals(.95, orderMap.get(first), 0.000001);
		assertEquals(.85, orderMap.get(second), 0.000001);
	}

	@Test
	public void testCreateBoundsDefinedOrder() {
		Object first = new Object();
		Object second = new Object();
		Object[] varDoubleList = new Object[2];
		varDoubleList[0] = first;
		varDoubleList[1] = second;
		VariableClassOrder order = mock(VariableClassOrder.class);
		when(order.indexOf(second)).thenReturn(1);
		when(order.getOrderSize()).thenReturn(2);
		BluePrintGeneratorMock mockGen = new BluePrintGeneratorMock(.2, order);
		DoubleBounds result = mockGen.createBounds(varDoubleList);
		assertEquals(.4, result.getLowerBound(0), .000001);
		assertEquals(.8, result.getUpperBound(0), .000001);
		assertEquals(.0, result.getLowerBound(1), .000001);
		assertEquals(.4, result.getUpperBound(1), .000001);
	}

	@Test
	public void testCreateBoundsUndefinedOrder() {
		Object first = new Object();
		Object second = new Object();
		Object[] varDoubleList = new Object[2];
		varDoubleList[0] = first;
		varDoubleList[1] = second;
		VariableClassOrder order = mock(VariableClassOrder.class);
		when(order.indexOf(first)).thenReturn(-1);
		when(order.indexOf(second)).thenReturn(-1);
		BluePrintGeneratorMock mockGen = new BluePrintGeneratorMock(.2, order);
		DoubleBounds result = mockGen.createBounds(varDoubleList);
		assertEquals(0.0, result.getLowerBound(0), .000001);
		assertEquals(.8, result.getUpperBound(0), .000001);
		assertEquals(0.0, result.getLowerBound(1), .000001);
		assertEquals(.8, result.getUpperBound(1), .000001);
	}

	@Test
	public void testNoUnimportantVars() {
		Object[] varDoubleList = new Object[2];
		VariableClassOrder order = mock(VariableClassOrder.class);
		BluePrintGeneratorMock mockGen = new BluePrintGeneratorMock(.2, order);
		mockGen.createBounds(varDoubleList);
	}
}

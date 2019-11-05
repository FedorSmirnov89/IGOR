package blueprint;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import net.sf.opendse.optimization.VariableClassOrder;

import static org.mockito.Mockito.mock;

public class BluePrintGeneratorMixedTest {

	@Test
	public void testCreateInitial() {
		Set<Object> encodedVariables = new HashSet<>();
		Object first = new Object();
		Object second = new Object();
		encodedVariables.add(first);
		encodedVariables.add(second);
		BluePrintGeneratorMixed generator = new BluePrintGeneratorMixed(mock(VariableClassOrder.class));
		//generator.importanceOrderInterval = .1;
		BluePrintSat result = generator.createInitialBlueprint(encodedVariables);
		assertEquals(2, result.getBooleanSize());
		assertEquals(2, result.getDoubleSize());
		assertTrue(result.getFixedOrderMap().isEmpty());
	}

	@Test
	public void testUpdate() {
		Set<Object> encodedVariables = new HashSet<>();
		Object first = new Object();
		Object second = new Object();
		Object third = new Object();
		encodedVariables.add(first);
		encodedVariables.add(second);
		encodedVariables.add(third);
		BluePrintGeneratorMixed generator = new BluePrintGeneratorMixed(mock(VariableClassOrder.class));
		BluePrintSat result = generator.createInitialBlueprint(encodedVariables);
		List<Object> important = new ArrayList<>();
		important.add(first);
		important.add(second);
		result = generator.createUpdatedBlueprint(encodedVariables, important);
		assertEquals(3, result.getBooleanSize());
		assertEquals(1, result.getDoubleSize());
		assertEquals(.975, result.getFixedOrderMap().get(first), .000001);
		assertEquals(.925, result.getFixedOrderMap().get(second), .000001);
	}
}

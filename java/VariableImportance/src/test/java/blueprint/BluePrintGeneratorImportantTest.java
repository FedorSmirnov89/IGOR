package blueprint;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import net.sf.opendse.optimization.VariableClassOrder;

import static org.mockito.Mockito.mock;

public class BluePrintGeneratorImportantTest {

	@Test
	public void testCreateInitial() {
		Set<Object> encodedVariables = new HashSet<>();
		Object first = new Object();
		Object second = new Object();
		encodedVariables.add(first);
		encodedVariables.add(second);
		BluePrintGeneratorImportant generator = new BluePrintGeneratorImportant(mock(VariableClassOrder.class));
		BluePrintSat result = generator.createInitialBlueprint(encodedVariables);
		assertEquals(2, result.getBooleanSize());
		assertEquals(2, result.getFixedOrderMap().entrySet().size());
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
		BluePrintGeneratorImportant generator = new BluePrintGeneratorImportant(mock(VariableClassOrder.class));
		// initial creation
		BluePrintSat result = generator.createInitialBlueprint(encodedVariables);
		assertEquals(3, result.getBooleanSize());
		assertEquals(3, result.getFixedOrderMap().entrySet().size());
		// importance update
		List<Object> important = new ArrayList<>();
		important.add(first);
		important.add(second);
		result = generator.createUpdatedBlueprint(encodedVariables, important);
		assertEquals(2, result.getBooleanSize());
		assertEquals(2, result.getFixedOrderMap().entrySet().size());
		assertEquals(.75, result.getFixedOrderMap().get(first), .000001);
		assertEquals(.25, result.getFixedOrderMap().get(second), .000001);
	}
}

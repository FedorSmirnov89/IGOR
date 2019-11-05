package optimization;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ImportanceUpdateDefaultTest {

	@Test
	public void testUpdate() {
		Map<Object, Double> input = new HashMap<>();
		Object first = new Object();
		Object second = new Object();
		Object third = new Object();
		input.put(first, 1.0);
		input.put(second, .1);
		input.put(third, 2.0);
		ImportanceUpdateDefault tested = new ImportanceUpdateDefault();
		assertTrue(tested.isInit());
		List<Object> result = tested.updateVariableImportance(input);
		assertEquals(3, result.size());
		assertEquals(third, result.get(0));
		assertEquals(first, result.get(1));
		assertEquals(second, result.get(2));
	}
	
	@Test
	public void testMapToList() {
		Map<Object, Double> input = new HashMap<>();
		Object first = new Object();
		Object second = new Object();
		Object third = new Object();
		input.put(first, 1.0);
		input.put(second, .1);
		input.put(third, 2.0);
		ImportanceUpdateDefault tested = new ImportanceUpdateDefault();
		List<Object> result = tested.generateOrderedList(input);
		assertEquals(3, result.size());
		assertEquals(third, result.get(0));
		assertEquals(first, result.get(1));
		assertEquals(second, result.get(2));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEmptyMap() {
		ImportanceUpdateDefault tested = new ImportanceUpdateDefault();
		tested.generateOrderedList(new HashMap<>());
	}
}

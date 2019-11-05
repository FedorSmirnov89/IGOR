package json;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SolvingSampleBatchTest {

	protected static List<String> columns;
	protected static List<Integer> index;
	protected static List<List<Double>> data;
	protected static List<Double> firstIndi;
	protected static List<Double> secondIndi;
	
	@Before
	public void initVars() {
		columns = new ArrayList<>();
		columns.add("firstVar");
		columns.add("secondVar");
		columns.add("firstObj");
		columns.add("secondObj");
		index = new ArrayList<>();
		index.add(0);
		index.add(1);
		data = new ArrayList<>();
		firstIndi = new ArrayList<>();
		firstIndi.add(1.0);
		firstIndi.add(0.0);
		firstIndi.add(12.3);
		firstIndi.add(-2.1);
		data.add(firstIndi);
		secondIndi = new ArrayList<>();
		secondIndi.add(1.0);
		secondIndi.add(0.0);
		secondIndi.add(12.3);
		secondIndi.add(-2.1);
		data.add(secondIndi);
	}
	
	@Test
	public void test() {
		SolvingSampleBatch tested = new SolvingSampleBatch(columns, index, data);
		assertEquals(columns, tested.getColumns());
		assertEquals(index, tested.getIndex());
		assertEquals(data, tested.getData());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNoData() {
		columns = new ArrayList<>();
		index = new ArrayList<>();
		data = new ArrayList<>();
		new SolvingSampleBatch(columns, index, data);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testWrongColumns() {
		columns = new ArrayList<>();
		columns.add("firstVar");
		columns.add("secondVar");
		columns.add("fristObj");
		new SolvingSampleBatch(columns, index, data);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testWrongIndex() {
		index = new ArrayList<>();
		index.add(0);
		new SolvingSampleBatch(columns, index, data);
	}
}

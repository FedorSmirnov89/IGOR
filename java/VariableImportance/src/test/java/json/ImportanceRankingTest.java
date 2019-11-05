package json;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ImportanceRankingTest {

	@Test
	public void test() {
		List<String> variables = new ArrayList<>();
		variables.add("first");
		variables.add("second");
		List<Double> importances = new ArrayList<>();
		importances.add(.1);
		importances.add(.2);
		ImportanceRanking tested = new ImportanceRanking(variables, importances);
		assertEquals(variables, tested.getVariableIds());
		assertEquals(importances, tested.getImportanceValues());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testWrong() {
		List<String> variables = new ArrayList<>();
		variables.add("first");
		variables.add("second");
		List<Double> importances = new ArrayList<>();
		importances.add(.1);
		new ImportanceRanking(variables, importances);
	}
	
}

package json;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import entity.LabeledSample;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

public class JsonParserTest {

	@Test
	public void testIsInit() {
		assertFalse(tested.isInit());
		assertFalse(tested.areVarsInit());
	}

	@Test(expected = IllegalStateException.class)
	public void testIllegalState1() {
		tested.initColumnNames(2);
	}

	@Test(expected = IllegalStateException.class)
	public void testIllegalState2() {
		tested.makeSolvingSampleBatch(new HashSet<>());
	}

	@Test(expected = IllegalStateException.class)
	public void testIllegalState3() {
		tested.makeServerRequestString(new HashSet<>());
	}

	@Test
	public void testInitVars() {
		assertFalse(tested.varsInit);
		List<Object> vars = new ArrayList<>();
		vars.add(firstVar);
		vars.add(secondVar);
		tested.initSatVariables(vars);
		assertTrue(tested.varsInit);
		assertEquals(firstVar, tested.toStringToVariableMap.get("first"));
		assertEquals(secondVar, tested.toStringToVariableMap.get("second"));
	}

	@Test
	public void testInitColumnNames() {
		List<Object> vars = new ArrayList<>();
		vars.add(firstVar);
		vars.add(secondVar);
		tested.initSatVariables(vars);
		assertFalse(tested.init);
		tested.initColumnNames(3);
		assertTrue(tested.init);
		assertEquals(5, tested.columnNames.size());
		assertEquals("first", tested.columnNames.get(0));
		assertEquals("second", tested.columnNames.get(1));
		assertEquals(JsonParser.OBJECTIVE_FUNCTION_STRING + JsonParser.OBJECTIVE_FUNCTION_SEPARATOR + "0",
				tested.columnNames.get(2));
		assertEquals(JsonParser.OBJECTIVE_FUNCTION_STRING + JsonParser.OBJECTIVE_FUNCTION_SEPARATOR + "1",
				tested.columnNames.get(3));
		assertEquals(JsonParser.OBJECTIVE_FUNCTION_STRING + JsonParser.OBJECTIVE_FUNCTION_SEPARATOR + "2",
				tested.columnNames.get(4));
	}

	@Before
	public void initDefault() {
		tested = new JsonParser();
		firstVar = mock(Object.class);
		when(firstVar.toString()).thenReturn("first");
		secondVar = mock(Object.class);
		when(secondVar.toString()).thenReturn("second");
	}

	protected static void initTestVars1() {
		vars = new ArrayList<>();
		vars.add(firstVar);
		vars.add(secondVar);
		tested.initSatVariables(vars);
		tested.initColumnNames(2);
	}

	protected static void initTestVars2() {
		vars1 = new ArrayList<>();
		vars1.add(true);
		vars1.add(false);
		objectives1 = new ArrayList<>();
		objectives1.add(.1);
		objectives1.add(-.1);
		sample1 = new LabeledSample(vars1, objectives1);
		samples = new HashSet<>();
		samples.add(sample1);
	}

	protected static JsonParser tested;
	protected static Object firstVar;
	protected static Object secondVar;
	protected static List<Object> vars;

	protected static List<Boolean> vars1;
	protected static List<Double> objectives1;
	protected static LabeledSample sample1;
	protected static Set<LabeledSample> samples;

	@Test
	public void testMakeSolvingSampleBatch() {
		initTestVars1();
		initTestVars2();
		SolvingSampleBatch result = tested.makeSolvingSampleBatch(samples);
		assertEquals(1, result.getData().size());
		assertEquals(1, result.getIndex().size());
		assertEquals(4, result.getColumns().size());
		assertEquals(4, result.getData().get(0).size());
		assertEquals(1, (int) result.getIndex().get(0));
		assertEquals(result.getColumns(), tested.columnNames);
		assertEquals(1.0, result.getData().get(0).get(0), .00001);
		assertEquals(0.0, result.getData().get(0).get(1), .00001);
		assertEquals(.1, result.getData().get(0).get(2), .00001);
		assertEquals(-.1, result.getData().get(0).get(3), .00001);
	}

	@Test
	public void testMakeServerRequestString() {
		initTestVars1();
		initTestVars2();
		assertEquals(
				"{\"columns\":[\"first\",\"second\",\"Objective Function:0\",\"Objective Function:1\"],\"index\":[1],\"data\":[[1.0,0.0,0.1,-0.1]]}",
				tested.makeServerRequestString(samples));
	}

	@Test
	public void testReadResponse() {
		initTestVars1();
		List<String> varIds = new ArrayList<>();
		varIds.add("first");
		varIds.add("second");
		List<Double> importances = new ArrayList<>();
		importances.add(.1);
		importances.add(.11);
		ImportanceRanking rank = new ImportanceRanking(varIds, importances);
		Gson gson = new Gson();
		String str = gson.toJson(rank);
		Map<Object, Double> result = tested.readServerImportanceResponse(str);
		assertTrue(result.containsKey(firstVar));
		assertTrue(result.containsKey(secondVar));
		assertEquals(.1, result.get(firstVar), .000001);
		assertEquals(.11, result.get(secondVar), .000001);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnmatchingResponse() {
		initTestVars1();
		List<String> varIds = new ArrayList<>();
		varIds.add("wrong");
		varIds.add("second");
		List<Double> importances = new ArrayList<>();
		importances.add(.1);
		importances.add(.11);
		ImportanceRanking rank = new ImportanceRanking(varIds, importances);
		Gson gson = new Gson();
		String str = gson.toJson(rank);
		tested.readServerImportanceResponse(str);
	}

	@Test(expected = IllegalStateException.class)
	public void testInitFail() {
		tested.readServerImportanceResponse("bla");
	}

}

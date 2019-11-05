package entity;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.core.Individual;
import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.Objective.Sign;

import optimization.ModelMemory;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

public class LabeledSampleFactoryDefaultTest {

	@Test
	public void testSetVars() {
		ModelMemory memoryMock = mock(ModelMemory.class);
		LabeledSampleFactoryDefault tested = new LabeledSampleFactoryDefault(memoryMock);
		assertFalse(tested.init);
		List<Object> vars = new ArrayList<>();
		vars.add(new Object());
		tested.setSatVariables(vars);
		assertEquals(vars, tested.satVariables);
		assertTrue(tested.init);
	}

	@Test
	public void testIsInit() {
		ModelMemory memoryMock = mock(ModelMemory.class);
		LabeledSampleFactoryDefault tested = new LabeledSampleFactoryDefault(memoryMock);
		assertFalse(tested.isInit());
	}

	@Test
	public void testCreateLabeledSample() {
		ModelMemory memoryMock = mock(ModelMemory.class);
		Individual mockIndi = mock(Individual.class);
		LabeledSampleFactoryDefault tested = new LabeledSampleFactoryDefault(memoryMock);
		List<Object> vars = new ArrayList<>();
		Object satVar = new Object();
		vars.add(satVar);
		tested.setSatVariables(vars);
		Map<Object, Boolean> mockModel = new HashMap<>();
		mockModel.put(satVar, true);
		when(memoryMock.getModel(mockIndi)).thenReturn(mockModel);
		Objectives objs = new Objectives();
		Objective obj = new Objective("obj", Sign.MAX);
		objs.add(obj, .1);
		when(mockIndi.getObjectives()).thenReturn(objs);
		LabeledSample result = tested.createLabeledSample(mockIndi);
		assertEquals(1, result.getObjectiveValues().size());
		assertEquals(.1, result.getObjectiveValues().get(0), .00001);
		assertEquals(1, result.getVariableAssignments().size());
		assertTrue(result.getVariableAssignments().get(0));
	}

	@Test(expected = IllegalStateException.class)
	public void testModelMismatch() {
		ModelMemory memoryMock = mock(ModelMemory.class);
		Individual mockIndi = mock(Individual.class);
		LabeledSampleFactoryDefault tested = new LabeledSampleFactoryDefault(memoryMock);
		List<Object> vars = new ArrayList<>();
		Object satVar = new Object();
		vars.add(satVar);
		vars.add(new Object());
		tested.setSatVariables(vars);
		Map<Object, Boolean> mockModel = new HashMap<>();
		mockModel.put(satVar, true);
		when(memoryMock.getModel(mockIndi)).thenReturn(mockModel);
		Objectives objs = new Objectives();
		Objective obj = new Objective("obj", Sign.MAX);
		objs.add(obj, .1);
		when(mockIndi.getObjectives()).thenReturn(objs);
		tested.createLabeledSample(mockIndi);
	}
}

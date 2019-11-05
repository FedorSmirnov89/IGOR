package optimization;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.core.Genotype;
import org.opt4j.core.Individual;
import org.opt4j.core.genotype.BooleanGenotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.genotype.DoubleBounds;
import org.opt4j.core.genotype.DoubleGenotype;
import org.opt4j.core.optimizer.Population;
import org.opt4j.satdecoding.SATGenotype;

import blueprint.BluePrintProvider;
import blueprint.BluePrintSat;
import blueprint.BluePrintUpdater;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

public class BluePrintRepairTest {

	class MockIndi extends Individual {
		public MockIndi() {
			super();
		}
	}

	/**
	 * Checks the repair from ftf to ttf. At the same time, the second variable
	 * becomes important so that it loses its double geno.
	 * 
	 */
	@Test
	public void testRepairIndividualSATGeno() {

		Individual mockIndi = new MockIndi();

		Object first = new Object();
		Object second = new Object();
		Object third = new Object();

		ModelMemory modelMemoryMock = mock(ModelMemory.class);
		Map<Object, Boolean> modelMock = new HashMap<>();
		modelMock.put(first, true);
		modelMock.put(second, true);
		modelMock.put(third, false);
		when(modelMemoryMock.getModel(mockIndi)).thenReturn(modelMock);

		Population populationMock = mock(Population.class);
		BluePrintProvider providerMock = mock(BluePrintProvider.class);
		BluePrintUpdater updaterMock = mock(BluePrintUpdater.class);

		BluePrintRepair tested = new BluePrintRepair(modelMemoryMock, providerMock, updaterMock, populationMock);

		BluePrintSat currentMock = mock(BluePrintSat.class);
		when(currentMock.getDoubleIdxForVar(third)).thenReturn(1);

		BluePrintSat updatedMock = mock(BluePrintSat.class);
		when(updatedMock.getBooleanSize()).thenReturn(3);
		when(updatedMock.getVarForBoolIdx(0)).thenReturn(first);
		when(updatedMock.getVarForBoolIdx(1)).thenReturn(second);
		when(updatedMock.getVarForBoolIdx(2)).thenReturn(third);
		when(updatedMock.getDoubleSize()).thenReturn(1);
		when(updatedMock.getVarForDoubleIdx(0)).thenReturn(third);
		double[] low = { .1 };
		double[] high = { 1. };
		when(updatedMock.getBoundsForDoubleGeno()).thenReturn(new DoubleBounds(low, high));

		SATGenotype mockGeno = mock(SATGenotype.class);
		mockIndi.setGenotype(mockGeno);
		BooleanGenotype boolGeno = new BooleanGenotype();
		boolGeno.add(false);
		boolGeno.add(true);
		boolGeno.add(false);

		when(mockGeno.getBooleanVector()).thenReturn(boolGeno);
		double[] low2 = { .1, .1 };
		double[] up2 = { 1., 1. };
		DoubleBounds bounds2 = new DoubleBounds(low2, up2);
		DoubleGenotype doubleGeno = new DoubleGenotype(bounds2);
		doubleGeno.add(.3);
		doubleGeno.add(.2);
		when(mockGeno.getDoubleVector()).thenReturn(doubleGeno);

		tested.repairIndividual(mockIndi, currentMock, updatedMock);
		SATGenotype repaired = (SATGenotype) mockIndi.getGenotype();
		assertTrue(repaired.getBooleanVector().get(0));
		assertTrue(repaired.getBooleanVector().get(1));
		assertFalse(repaired.getBooleanVector().get(2));

		assertEquals(1, repaired.getDoubleVector().size());
		assertEquals(.2, repaired.getDoubleVector().get(0), .000001);
	}

	/**
	 * Same check as above, but for the case of the composite genotype
	 * 
	 */
	@Test
	public void testRepairIndividualCompositeGeno() {

		Individual mockIndi = new MockIndi();

		Object first = new Object();
		Object second = new Object();
		Object third = new Object();

		ModelMemory modelMemoryMock = mock(ModelMemory.class);
		Map<Object, Boolean> modelMock = new HashMap<>();
		modelMock.put(first, true);
		modelMock.put(second, true);
		modelMock.put(third, false);
		when(modelMemoryMock.getModel(mockIndi)).thenReturn(modelMock);

		Population populationMock = mock(Population.class);
		BluePrintProvider providerMock = mock(BluePrintProvider.class);
		BluePrintUpdater updaterMock = mock(BluePrintUpdater.class);

		BluePrintRepair tested = new BluePrintRepair(modelMemoryMock, providerMock, updaterMock, populationMock);

		BluePrintSat currentMock = mock(BluePrintSat.class);
		when(currentMock.getDoubleIdxForVar(third)).thenReturn(1);

		BluePrintSat updatedMock = mock(BluePrintSat.class);
		when(updatedMock.getBooleanSize()).thenReturn(3);
		when(updatedMock.getVarForBoolIdx(0)).thenReturn(first);
		when(updatedMock.getVarForBoolIdx(1)).thenReturn(second);
		when(updatedMock.getVarForBoolIdx(2)).thenReturn(third);
		when(updatedMock.getDoubleSize()).thenReturn(1);
		when(updatedMock.getVarForDoubleIdx(0)).thenReturn(third);
		double[] low = { .1 };
		double[] high = { 1. };
		when(updatedMock.getBoundsForDoubleGeno()).thenReturn(new DoubleBounds(low, high));

		CompositeGenotype<String, Genotype> composite = new CompositeGenotype<>();
		SATGenotype mockGeno = mock(SATGenotype.class);
		mockIndi.setGenotype(composite);
		composite.put("SAT", mockGeno);
		BooleanGenotype boolGeno = new BooleanGenotype();
		boolGeno.add(false);
		boolGeno.add(true);
		boolGeno.add(false);

		when(mockGeno.getBooleanVector()).thenReturn(boolGeno);
		double[] low2 = { .1, .1 };
		double[] up2 = { 1., 1. };
		DoubleBounds bounds2 = new DoubleBounds(low2, up2);
		DoubleGenotype doubleGeno = new DoubleGenotype(bounds2);
		doubleGeno.add(.3);
		doubleGeno.add(.2);
		when(mockGeno.getDoubleVector()).thenReturn(doubleGeno);

		tested.repairIndividual(mockIndi, currentMock, updatedMock);
		@SuppressWarnings("unchecked")
		CompositeGenotype<String, Genotype> compositeGeno = (CompositeGenotype<String, Genotype>) mockIndi
				.getGenotype();
		SATGenotype repaired = (SATGenotype) compositeGeno.get("SAT");
		assertTrue(repaired.getBooleanVector().get(0));
		assertTrue(repaired.getBooleanVector().get(1));
		assertFalse(repaired.getBooleanVector().get(2));

		assertEquals(1, repaired.getDoubleVector().size());
		assertEquals(.2, repaired.getDoubleVector().get(0), .000001);
	}

	/**
	 * Pretty much the same repair situation as above. Population with one indi.
	 * Purpose is to check that the blueprint is updated.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testPopulationRepair() {
		Individual mockIndi = new MockIndi();

		Object first = new Object();
		Object second = new Object();
		Object third = new Object();

		List<Object> importantVariables = new ArrayList<>();
		importantVariables.add(first);
		importantVariables.add(second);

		ModelMemory modelMemoryMock = mock(ModelMemory.class);
		Map<Object, Boolean> modelMock = new HashMap<>();
		modelMock.put(first, true);
		modelMock.put(second, true);
		modelMock.put(third, false);
		when(modelMemoryMock.getModel(mockIndi)).thenReturn(modelMock);

		BluePrintProvider providerMock = mock(BluePrintProvider.class);
		BluePrintUpdater updaterMock = mock(BluePrintUpdater.class);

		BluePrintSat currentBluePrint = mock(BluePrintSat.class);
		when(currentBluePrint.getDoubleIdxForVar(second)).thenReturn(0);
		when(currentBluePrint.getDoubleIdxForVar(third)).thenReturn(1);
		when(providerMock.getCurrentBlueprint()).thenReturn(currentBluePrint);
		when(currentBluePrint.getBooleanSize()).thenReturn(3);

		BluePrintSat updatedBluePrint = mock(BluePrintSat.class);
		double[] low = { .1 };
		double[] up = { 1. };
		DoubleBounds updatedBounds = new DoubleBounds(low, up);
		when(updatedBluePrint.getBoundsForDoubleGeno()).thenReturn(updatedBounds);
		when(updatedBluePrint.getBooleanSize()).thenReturn(3);
		when(updatedBluePrint.getVarForBoolIdx(0)).thenReturn(first);
		when(updatedBluePrint.getVarForBoolIdx(1)).thenReturn(second);
		when(updatedBluePrint.getVarForBoolIdx(2)).thenReturn(third);
		when(updatedBluePrint.getDoubleSize()).thenReturn(1);
		when(updatedBluePrint.getVarForDoubleIdx(0)).thenReturn(third);

		Set<Object> encodedVariables = new HashSet<>();
		encodedVariables.add(first);
		encodedVariables.add(second);
		encodedVariables.add(third);

		when(updaterMock.createUpdatedBlueprint(anySet(), anyList())).thenReturn(updatedBluePrint);
		when(updaterMock.getUpdatedEncodedVariables(anySet(), anyList())).thenReturn(encodedVariables);

		SATGenotype mockGeno = mock(SATGenotype.class);
		mockIndi.setGenotype(mockGeno);
		BooleanGenotype boolGeno = new BooleanGenotype();
		boolGeno.add(false);
		boolGeno.add(true);
		boolGeno.add(false);

		when(mockGeno.getBooleanVector()).thenReturn(boolGeno);
		double[] low2 = { .1, .1 };
		double[] up2 = { 1., 1. };
		DoubleBounds bounds2 = new DoubleBounds(low2, up2);
		DoubleGenotype doubleGeno = new DoubleGenotype(bounds2);
		doubleGeno.add(.3);
		doubleGeno.add(.2);
		when(mockGeno.getDoubleVector()).thenReturn(doubleGeno);

		Population populationMock = new Population();
		populationMock.add(mockIndi);

		BluePrintRepair tested = new BluePrintRepair(modelMemoryMock, providerMock, updaterMock, populationMock);

		tested.repairIndividualPopulation(importantVariables);
		verify(providerMock).setCurrentBlueprint(updatedBluePrint, encodedVariables);
	}
}

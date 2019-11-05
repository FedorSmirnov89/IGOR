package optimization;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.core.Individual;
import org.opt4j.core.optimizer.Population;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class ModelMemoryMapTest {

	@Test
	public void test() {
		Population population = new Population();
		ModelMemoryMap tested = new ModelMemoryMap(population);
		Individual mockIndi = mock(Individual.class);
		when(mockIndi.getPhenotype()).thenReturn(new Object());
		population.add(mockIndi);
		Map<Object, Boolean> mockModel = new HashMap<>();
		tested.rememberModel(mockIndi, mockModel);
		assertEquals(mockModel, tested.getModel(mockIndi));
		population.remove(mockIndi);
		assertTrue(tested.indiToModelMap.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void wrong1() {
		Population population = new Population();
		ModelMemoryMap tested = new ModelMemoryMap(population);
		Individual mockIndi = mock(Individual.class);
		when(mockIndi.getPhenotype()).thenReturn(new Object());
		tested.getModel(mockIndi);
	}

	@Test(expected = IllegalArgumentException.class)
	public void wrong2() {
		Population population = new Population();
		Individual mockIndi = mock(Individual.class);
		when(mockIndi.getPhenotype()).thenReturn(new Object());
		population.add(mockIndi);
		@SuppressWarnings("unused")
		ModelMemoryMap tested = new ModelMemoryMap(population);
		population.remove(mockIndi);
	}

}

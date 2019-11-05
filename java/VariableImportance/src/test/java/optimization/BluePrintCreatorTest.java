package optimization;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.core.genotype.BooleanGenotype;
import org.opt4j.core.genotype.DoubleBounds;
import org.opt4j.core.genotype.DoubleGenotype;
import org.opt4j.satdecoding.SATGenotype;

import blueprint.BluePrintProvider;
import blueprint.BluePrintSat;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

import java.util.Random;

public class BluePrintCreatorTest {

	@Test
	public void test() {
		BluePrintProvider mockProvider = mock(BluePrintProvider.class);
		BluePrintSat blueprint = mock(BluePrintSat.class);
		when(mockProvider.getCurrentBlueprint()).thenReturn(blueprint);
		Random mockRandom = mock(Random.class);
		when(mockRandom.nextBoolean()).thenReturn(false);
		when(mockRandom.nextDouble()).thenReturn(.5);
		when(blueprint.getBooleanSize()).thenReturn(3);
		when(blueprint.getDoubleSize()).thenReturn(1);
		double[] lower = {.1};
		double[] upper = {1.};
		DoubleBounds bounds = new DoubleBounds(lower, upper);
		when(blueprint.getBoundsForDoubleGeno()).thenReturn(bounds);
		
		BluePrintCreator creator = new BluePrintCreator(mockProvider, mockRandom);
		SATGenotype geno = (SATGenotype) creator.createGenotype();
		BooleanGenotype boolGeno = geno.getBooleanVector();
		DoubleGenotype doubleGeno = geno.getDoubleVector();
		assertEquals(3, boolGeno.size());
		assertFalse(boolGeno.get(0));
		assertFalse(boolGeno.get(1));
		assertFalse(boolGeno.get(2));
		assertEquals(1, doubleGeno.size());
		assertEquals(.55, doubleGeno.get(0),.000001);
	}
}

package optimization;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.core.genotype.BooleanGenotype;
import org.opt4j.core.genotype.DoubleBounds;
import org.opt4j.core.genotype.DoubleGenotype;
import org.opt4j.satdecoding.SATGenotype;
import org.opt4j.satdecoding.VarOrder;

import blueprint.BluePrintProvider;
import blueprint.BluePrintSat;
import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

public class BluePrintOrderConfiguratorTest {

	protected static Object firstVar = new Object();
	protected static Object secondVar = new Object();
	protected static Object thirdVar = new Object();
	protected static Object fourthVar = new Object();

	protected class wrongGeno1 extends BooleanGenotype {
		private static final long serialVersionUID = 1L;
	}

	protected class wrongGeno2 extends SATGenotype {
		@Override
		public BooleanGenotype getBooleanVector() {
			BooleanGenotype result = new BooleanGenotype();
			result.add(true);
			result.add(false);
			result.add(true);
			return result;
		}

		@Override
		public DoubleGenotype getDoubleVector() {
			DoubleGenotype result = new DoubleGenotype();
			result.add(.3);
			result.add(.4);
			return result;
		}

	}

	protected class wrongGeno3 extends SATGenotype {
		@Override
		public BooleanGenotype getBooleanVector() {
			BooleanGenotype result = new BooleanGenotype();
			result.add(true);
			result.add(false);
			result.add(true);
			result.add(false);
			return result;
		}

		@Override
		public DoubleGenotype getDoubleVector() {
			DoubleGenotype result = new DoubleGenotype();
			result.add(.3);
			return result;
		}

	}

	protected class mockSatGeno extends SATGenotype {
		@Override
		public BooleanGenotype getBooleanVector() {
			BooleanGenotype result = new BooleanGenotype();
			result.add(true);
			result.add(false);
			result.add(true);
			result.add(false);
			return result;
		}

		@Override
		public DoubleGenotype getDoubleVector() {
			DoubleGenotype result = new DoubleGenotype();
			result.add(.3);
			result.add(.4);
			return result;
		}
	}

	/**
	 * Mocks a blueprint where first and second are important, while third and
	 * fourth are not.
	 * 
	 * @author Fedor Smirnov
	 *
	 */
	protected class mockBluePrint implements BluePrintSat {
		@Override
		public int getBooleanSize() {
			return 4;
		}

		@Override
		public int getDoubleSize() {
			return 2;
		}

		@Override
		public Object getVarForBoolIdx(int idx) {
			switch (idx) {
			case 0:
				return firstVar;
			case 1:
				return secondVar;

			case 2:
				return thirdVar;

			case 3:
				return fourthVar;
			default:
				return null;
			}
		}

		@Override
		public Object getVarForDoubleIdx(int idx) {
			switch (idx) {
			case 0:
				return thirdVar;
			case 1:
				return fourthVar;
			default:
				return null;
			}
		}

		@Override
		public int getDoubleIdxForVar(Object var) {
			if (var.equals(thirdVar)) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public DoubleBounds getBoundsForDoubleGeno() {
			double[] lower = { .1, .1 };
			double[] upper = { 1., 1. };
			return new DoubleBounds(lower, upper);
		}

		@Override
		public Map<Object, Double> getFixedOrderMap() {
			Map<Object, Double> result = new HashMap<>();
			result.put(firstVar, .025);
			result.put(secondVar, .075);
			return result;
		}

	}

	@Test
	public void test() {
		BluePrintProvider mockProvider = mock(BluePrintProvider.class);
		BluePrintSat mockBluePrint = new mockBluePrint();
		when(mockProvider.getCurrentBlueprint()).thenReturn(mockBluePrint);
		BluePrintOrderConfigurator tested = new BluePrintOrderConfigurator(mockProvider);
		VarOrder order = tested.generateVarOrder(new mockSatGeno());
		assertEquals(.025, order.getActivity(firstVar), .000001);
		assertEquals(.075, order.getActivity(secondVar), .000001);
		assertEquals(.3, order.getActivity(thirdVar), .000001);
		assertEquals(.4, order.getActivity(fourthVar), .000001);
		assertTrue(order.getPhase(firstVar));
		assertFalse(order.getPhase(secondVar));
		assertTrue(order.getPhase(thirdVar));
		assertFalse(order.getPhase(fourthVar));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongGeno1() {
		BluePrintProvider mockProvider = mock(BluePrintProvider.class);
		BluePrintSat mockBluePrint = new mockBluePrint();
		when(mockProvider.getCurrentBlueprint()).thenReturn(mockBluePrint);
		BluePrintOrderConfigurator tested = new BluePrintOrderConfigurator(mockProvider);
		tested.generateVarOrder(new wrongGeno1());
	}

	@Test(expected = IllegalStateException.class)
	public void testWrongGeno2() {
		BluePrintProvider mockProvider = mock(BluePrintProvider.class);
		BluePrintSat mockBluePrint = new mockBluePrint();
		when(mockProvider.getCurrentBlueprint()).thenReturn(mockBluePrint);
		BluePrintOrderConfigurator tested = new BluePrintOrderConfigurator(mockProvider);
		tested.generateVarOrder(new wrongGeno2());
	}

	@Test(expected = IllegalStateException.class)
	public void testWrongGeno3() {
		BluePrintProvider mockProvider = mock(BluePrintProvider.class);
		BluePrintSat mockBluePrint = new mockBluePrint();
		when(mockProvider.getCurrentBlueprint()).thenReturn(mockBluePrint);
		BluePrintOrderConfigurator tested = new BluePrintOrderConfigurator(mockProvider);
		tested.generateVarOrder(new wrongGeno3());
	}
}

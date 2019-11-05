package optimization;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.core.common.random.Rand;
import org.opt4j.core.optimizer.Control;
import org.opt4j.satdecoding.SATManager;

import net.sf.opendse.optimization.SATConstraints;
import net.sf.opendse.optimization.SpecificationWrapper;
import net.sf.opendse.optimization.VariableClassOrder;
import net.sf.opendse.optimization.encoding.Interpreter;

import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SatCreatorDecoderImportanceTest {

	@Test
	public void test() {
		VariableClassOrder order = mock(VariableClassOrder.class);
		SATManager manager = mock(SATManager.class);
		Rand random = mock(Rand.class);
		SATConstraints constraints = mock(SATConstraints.class);
		SpecificationWrapper specificationWrapper = mock(SpecificationWrapper.class);
		Interpreter interpreter = mock(Interpreter.class);
		Control control = mock(Control.class);
		// check the constructor
		SatCreatorDecoderImportance tested = new SatCreatorDecoderImportance(order, manager, random, constraints,
				specificationWrapper, interpreter, control, true);
		Object first = new Object();
		Object second = new Object();
		Set<Object> variables = new HashSet<>();
		variables.add(first);
		variables.add(second);
		Map<Object, Boolean> phases = new HashMap<>();
		phases.put(first, true);
		phases.put(second, false);
		Map<Object, Double> activity = new HashMap<>();
		activity.put(first, .1);
		activity.put(second, 1.);
		Map<Object, Double> lower = new HashMap<>();
		lower.put(first, 0.);
		lower.put(second, 0.);
		Map<Object, Double> upper = new HashMap<>();
		upper.put(first, 2.);
		upper.put(second, 2.);
		tested.randomize(variables, lower, upper, activity, phases);
		assertTrue(phases.get(first));
		assertFalse(phases.get(second));
		assertEquals(.1, activity.get(first), .000001);
		assertEquals(1., activity.get(second), .000001);
	}
}

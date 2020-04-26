package optimization;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.core.Genotype;
import org.opt4j.core.Individual;
import org.opt4j.core.Individual.State;
import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.ObjectivesWrapper;
import org.opt4j.core.optimizer.Control;
import org.opt4j.core.optimizer.TerminationException;
import org.opt4j.core.problem.Decoder;
import org.opt4j.core.problem.Evaluator;
import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

public class SequentialIndividualCompleterTimeOutTest {

	protected class mockIndi extends Individual {

	}

	@Test
	public void testObjectivesKnown() {
		Control controlMock = mock(Control.class);
		@SuppressWarnings("unchecked")
		Decoder<Genotype, Object> decoderMock = mock(Decoder.class);
		@SuppressWarnings("unchecked")
		Evaluator<Object> evaluatorMock = mock(Evaluator.class);
		ObjectivesWrapper mockWrapper = mock(ObjectivesWrapper.class);
		SequentialIndividualCompleterTimeOut tested = new SequentialIndividualCompleterTimeOut(controlMock, decoderMock,
				evaluatorMock, mockWrapper);
		assertFalse(tested.objectivesKnown());
		tested.optimizationObjectives.add(mock(Objective.class));
		assertTrue(tested.objectivesKnown());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCompleteTimeOut() {
		Control controlMock = mock(Control.class);
		Decoder<Genotype, Object> decoderMock = mock(Decoder.class);
		Evaluator<Object> evaluatorMock = mock(Evaluator.class);
		ObjectivesWrapper mockWrapper = mock(ObjectivesWrapper.class);
		SequentialIndividualCompleterTimeOut tested = new SequentialIndividualCompleterTimeOut(controlMock, decoderMock,
				evaluatorMock, mockWrapper);
		Objective first = new Objective("first");
		Objective second = new Objective("second");
		tested.optimizationObjectives.add(first);
		tested.optimizationObjectives.add(second);
		Set<Individual> iterable = new HashSet<>();
		Individual indi = new mockIndi();
		// set up the mock decoding
		Genotype mockGeno = mock(Genotype.class);
		indi.setGenotype(mockGeno);
		when(decoderMock.decode(mockGeno))
				.thenThrow(new IllegalArgumentException(VariableOrderManagerAbstract.TIMEOUT_EXCEPTION_MESSAGE));
		indi.setState(State.GENOTYPED);
		iterable.add(indi);
		try {
			tested.complete(iterable);
		} catch (TerminationException exc) {
			fail("termination exception");
		}
		assertEquals(State.EVALUATED, indi.getState());
		assertEquals(Objective.INFEASIBLE, indi.getObjectives().get(first).getValue());
		assertEquals(Objective.INFEASIBLE, indi.getObjectives().get(second).getValue());
	}

	@Test
	public void testCompleteNoTimeOut() {
		Control controlMock = mock(Control.class);
		@SuppressWarnings("unchecked")
		Decoder<Genotype, Object> decoderMock = mock(Decoder.class);
		@SuppressWarnings("unchecked")
		Evaluator<Object> evaluatorMock = mock(Evaluator.class);
		ObjectivesWrapper mockWrapper = mock(ObjectivesWrapper.class);
		SequentialIndividualCompleterTimeOut tested = new SequentialIndividualCompleterTimeOut(controlMock, decoderMock,
				evaluatorMock, mockWrapper);
		Set<Individual> iterable = new HashSet<>();
		Individual indi = new mockIndi();
		indi.setState(State.GENOTYPED);
		iterable.add(indi);
		// set up the mock decoding
		Genotype mockGeno = mock(Genotype.class);
		indi.setGenotype(mockGeno);
		Object pheno = new Object();
		when(decoderMock.decode(mockGeno)).thenReturn(pheno);
		// set up the mock evaluation
		Objectives objs = new Objectives();
		Objective obj1 = new Objective("first");
		Objective obj2 = new Objective("second");
		objs.add(obj1, 1.0);
		objs.add(obj2, .1);
		when(evaluatorMock.evaluate(pheno)).thenReturn(objs);
		try {
			tested.complete(iterable);
		} catch (TerminationException exc) {
			fail("termination exception");
		}
		assertEquals(objs, indi.getObjectives());
		assertTrue(tested.optimizationObjectives.contains(obj1));
		assertTrue(tested.optimizationObjectives.contains(obj2));
		assertEquals(1.0, indi.getObjectives().get(obj1).getDouble(), .000001);
		assertEquals(.1, indi.getObjectives().get(obj2).getDouble(), .000001);
	}

}

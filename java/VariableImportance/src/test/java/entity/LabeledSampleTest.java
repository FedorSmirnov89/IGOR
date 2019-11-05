package entity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class LabeledSampleTest {

	@Test
	public void testGetters() {
		List<Boolean> variableAssignments = new ArrayList<>();
		variableAssignments.add(true);
		variableAssignments.add(false);
		List<Double> objectiveValues = new ArrayList<>();
		objectiveValues.add(-1.3);
		objectiveValues.add(1000.);
		LabeledSample tested = new LabeledSample(variableAssignments, objectiveValues);
		assertEquals(variableAssignments, tested.getVariableAssignments());
		assertEquals(objectiveValues, tested.getObjectiveValues());
	}

	@Test
	public void testEquals() {
		EqualsVerifier.forClass(LabeledSample.class).verify();
	}

}

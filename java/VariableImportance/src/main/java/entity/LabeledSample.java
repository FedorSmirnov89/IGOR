package entity;

import java.util.List;

/**
 * The {@link LabeledSample} represents an input to the ML structure used for
 * the evaluation of variable importance. It contains information about the
 * (true, as opposed to the genetic) variable assignment and the objectives of
 * the corresponding individual.
 * 
 * @author Fedor Smirnov
 *
 */
public final class LabeledSample {

	private final List<Boolean> variableAssignments;
	private final List<Double> objectiveValues;

	public LabeledSample(List<Boolean> variableAssignments, List<Double> objectiveValues) {
		this.variableAssignments = variableAssignments;
		this.objectiveValues = objectiveValues;
	}

	public List<Boolean> getVariableAssignments() {
		return variableAssignments;
	}

	public List<Double> getObjectiveValues() {
		return objectiveValues;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objectiveValues == null) ? 0 : objectiveValues.hashCode());
		result = prime * result + ((variableAssignments == null) ? 0 : variableAssignments.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LabeledSample other = (LabeledSample) obj;
		if (objectiveValues == null) {
			if (other.objectiveValues != null)
				return false;
		} else if (!objectiveValues.equals(other.objectiveValues))
			{return false;}
		if (variableAssignments == null) {
			if (other.variableAssignments != null)
				return false;
		} else if (!variableAssignments.equals(other.variableAssignments))
			{return false;}
		return true;
	}
}

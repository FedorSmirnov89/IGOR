package entity;

/**
 * 
 * Class containing the variables together with their importances that is usable
 * for an ordering with a heap structure.
 * 
 * @author Fedor Smirnov
 *
 */
public final class VariableImportance implements Comparable<VariableImportance> {
	// the variable
	private final Object variable;
	// the importance of the given variable (bigger means more important)
	private final Double importance;

	public VariableImportance(Object variable, double importance) {
		this.variable = variable;
		this.importance = importance;
	}

	public Object getVariable() {
		return variable;
	}

	public Double getImportance() {
		return importance;
	}

	@Override
	public int compareTo(VariableImportance other) {
		if (importance.doubleValue() != other.getImportance().doubleValue()) {
			if (importance < other.getImportance()) {
				return -1;
			} else {
				return 1;
			}
		} else {
			// go for the id as tie breaker
			return variable.toString().compareTo(other.getVariable().toString());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((importance == null) ? 0 : importance.hashCode());
		result = prime * result + ((variable == null) ? 0 : variable.hashCode());
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
		VariableImportance other = (VariableImportance) obj;
		if (importance == null) {
			if (other.importance != null)
				return false;
		} else if (!importance.equals(other.importance)) {
			return false;
		}
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable)) {
			return false;
		}
		return true;
	}
}

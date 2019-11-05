package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opt4j.core.Individual;
import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import optimization.ModelMemory;

@Singleton
public class LabeledSampleFactoryDefault implements LabeledSampleFactory {

	protected boolean init = false;
	// variables used in the constraints
	protected List<Object> satVariables;
	// keeps track of the models of the current population
	protected final ModelMemory modelMemory;

	@Inject
	public LabeledSampleFactoryDefault(ModelMemory modelMemory) {
		this.modelMemory = modelMemory;
	}

	@Override
	public boolean isInit() {
		return init;
	}

	@Override
	public void setSatVariables(List<Object> satVariables) {
		this.satVariables = satVariables;
		init = true;
	}

	@Override
	public LabeledSample createLabeledSample(Individual indi) {
		// extract the assignment of the variables
		Map<Object, Boolean> varAssignment = modelMemory.getModel(indi);
		if (varAssignment.size() != satVariables.size()) {
			throw new IllegalStateException("the model does not match the known variables");
		}
		// make the var assignment list
		List<Boolean> variableAssignments = new ArrayList<>();
		for (Object var : satVariables) {
			variableAssignments.add(varAssignment.get(var));
		}
		// make the objective list
		List<Double> objectiveValues = new ArrayList<>();
		Objectives objectives = indi.getObjectives();
		for (Objective obj : objectives.getKeys()) {
			objectiveValues.add(objectives.get(obj).getDouble());
		}
		return new LabeledSample(variableAssignments, objectiveValues);
	}
}

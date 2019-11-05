package json;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.inject.Singleton;

import entity.LabeledSample;
import python.CommunicationParser;

/**
 * The {@link CommunicationParser} for the json-based communication.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class JsonParser implements CommunicationParser {

	protected static final String OBJECTIVE_FUNCTION_STRING = "Objective Function";
	protected static final String OBJECTIVE_FUNCTION_SEPARATOR = ":";

	protected final Gson gson = new Gson();
	protected List<Object> variables;
	protected Map<String, Object> toStringToVariableMap;
	protected List<String> objectiveColumnNames;
	protected List<String> columnNames;
	protected boolean init = false;
	protected boolean varsInit = false;

	@Override
	public boolean isInit() {
		return init;
	}

	@Override
	public boolean areVarsInit() {
		return varsInit;
	}

	@Override
	public void initSatVariables(List<Object> satVariables) {
		this.variables = new ArrayList<>(satVariables);
		toStringToVariableMap = new HashMap<>();
		for (Object var : variables) {
			toStringToVariableMap.put(var.toString(), var);
		}
		varsInit = true;
	}

	@Override
	public void initColumnNames(int objNumber) {
		if (!varsInit) {
			throw new IllegalStateException("The variables have not yet been initialized.");
		}
		// create the objective string
		objectiveColumnNames = new ArrayList<>();
		for (int i = 0; i < objNumber; i++) {
			objectiveColumnNames.add(OBJECTIVE_FUNCTION_STRING + OBJECTIVE_FUNCTION_SEPARATOR + i);
		}
		// make the column names
		columnNames = new ArrayList<>();
		// add the variables
		for (Object var : variables) {
			columnNames.add(var.toString());
		}
		// add the objectives
		for (String objectiveName : objectiveColumnNames) {
			columnNames.add(objectiveName);
		}
		init = true;
	}

	@Override
	public String makeServerRequestString(Set<LabeledSample> samples) {
		if (!init) {
			throw new IllegalStateException("Json Translator not initialized");
		}
		SolvingSampleBatch sampleBatch = makeSolvingSampleBatch(samples);
		return gson.toJson(sampleBatch);
	}
	
	/**
	 * Based on the provided labeled samples, create a solving sample batch object
	 * that can be used for a direct json generation.
	 * 
	 * @param samples
	 *            : set of the solving samples
	 * @return : a solving sample batch object
	 */
	protected SolvingSampleBatch makeSolvingSampleBatch(Set<LabeledSample> samples) {
		if (!init) {
			throw new IllegalStateException("Json Translator not initialized");
		}
		// bring an order into the set
		List<LabeledSample> orderedSamples = new ArrayList<>(samples);
		List<Integer> index = new ArrayList<>();
		List<List<Double>> data = new ArrayList<>();
		int idx = 1;
		for (LabeledSample sample : orderedSamples) {
			index.add(idx++);
			// iterate the variable
			List<Double> sampleData = new ArrayList<>();
			for (Boolean var : sample.getVariableAssignments()) {
				sampleData.add(var ? 1.0 : 0.0);
			}
			// iterate the obj functions
			for (Double objFunc : sample.getObjectiveValues()) {
				sampleData.add(objFunc);
			}
			data.add(sampleData);
		}
		return new SolvingSampleBatch(columnNames, index, data);
	}

	@Override
	public Map<Object, Double> readServerImportanceResponse(String serverResponse) {
		if (!varsInit) {
			throw new IllegalStateException("The variables have not yet been initialized.");
		}
		JsonReader reader = new JsonReader(new StringReader(serverResponse));
		reader.setLenient(true);
		ImportanceRanking response = gson.fromJson(reader, ImportanceRanking.class);
		Map<Object, Double> result = new HashMap<>();
		if (response.getVariableIds() == null) {
			return result;
		}
		for (int idx = 0; idx < response.getVariableIds().size(); idx++) {
			String id = response.getVariableIds().get(idx);
			double importance = response.getImportanceValues().get(idx);
			if (!toStringToVariableMap.containsKey(id)) {
				throw new IllegalArgumentException("The json response does not match the known variable names.");
			}
			Object var = toStringToVariableMap.get(id);
			result.put(var, importance);
		}
		return result;
	}
}

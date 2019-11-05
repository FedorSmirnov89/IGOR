package python;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.ImplementedBy;

import entity.LabeledSample;
import json.JsonParser;

/**
 * Interface for the classes used to write and reead the messages used to
 * exchange variable importance information between the Python ML server and the
 * Java-based optimization.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(JsonParser.class)
public interface CommunicationParser {

	/**
	 * Returns {@code true} if the parser was already initialized.
	 * 
	 * @return {@code true} if the parser was already initialized
	 */
	public boolean isInit();
	
	/**
	 * Returns {@code true} if the encoding variables was already initialized.
	 * 
	 * @return {@code true} if the encoding variables was already initialized
	 */
	public boolean areVarsInit();
	
	/**
	 * Set the list of ALL variables that are used in the current constraint set.
	 * 
	 * @param satVariables
	 */
	public void initSatVariables(List<Object> satVariables);
	
	/**
	 * Initializes the objectives by creating the corresponding part of the df header
	 * 
	 * @param objNumber number of the objectives (name does not matter)
	 */
	public void initColumnNames(int objNumber);
	
	/**
	 * Communication direction: Java => Python
	 * 
	 * @param samples
	 *            : Set of the gathered solving samples
	 * @return : String that can be used within http requests
	 */
	public String makeServerRequestString(Set<LabeledSample> samples);

	/**
	 * 
	 * Communication direction : Python => Java
	 * 
	 * @param serverResponse:
	 *            String returned by the MLS
	 * @return : Importance ranking of the most important variables.
	 */
	public Map<Object, Double> readServerImportanceResponse(String serverResponse);
	
}

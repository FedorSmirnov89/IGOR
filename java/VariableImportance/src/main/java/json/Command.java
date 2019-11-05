package json;

import java.util.Map;

/**
 * Class used for the generation of all json objects used to issue a direct command to the Python server.
 * 
 * @author Fedor Smirnov
 *
 */
public class Command {

	private final Map<String, Object> commandParameters;
	
	public Command(Map<String, Object> commandParamenters) {
		this.commandParameters = commandParamenters;
	}

	public Map<String, Object> getCommandParameters() {
		return commandParameters;
	}
}

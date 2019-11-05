package json;

/**
 * Class used for the creation json Objects used to receive a server response to
 * a direct command.
 * 
 * @author Fedor Smirnov
 *
 */
public class CommandResponse {

	protected final boolean success;

	public CommandResponse(boolean success) {
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

}

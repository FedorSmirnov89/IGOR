package python;

import org.apache.http.client.fluent.Content;
import org.apache.http.concurrent.FutureCallback;

/**
 * Interface for classes handling the communication with the Python server,
 * i.e., sending requests and receiving teh response.
 * 
 * @author Fedor Smirnov
 *
 */
public interface RequestHandler {

	/**
	 * Send a request to the server, wait for the response, and return the response
	 * string.
	 * 
	 * @param request
	 *            : string containing the request information
	 * @return : string containing the server response
	 */
	public String sendServerRequest(String request);

	/**
	 * Send an asynchronous request with the specified content. Execute the callback
	 * upon response.
	 * 
	 * @param requestContent
	 * @param callback
	 */
	public void sendAsyncCallbackRequest(String requestContent, FutureCallback<Content> callback);

	/**
	 * Close the connections when the optimization is terminated.
	 * 
	 */
	public void shutDown();
}

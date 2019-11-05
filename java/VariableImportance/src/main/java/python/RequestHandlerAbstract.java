package python;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Parent class for all request classes. Contains all the http overhead, so that
 * the children classes can focus solely on the content and the response.
 * 
 * @author Fedor Smirnov
 *
 */
public abstract class RequestHandlerAbstract implements RequestHandler {

	// the URL to address the Python server, probably good idea to get this from a
	// config file somewhere
	protected static final String SERVER_URL = "http://127.0.0.1:5000/";
	private ExecutorService threadpool= Executors.newFixedThreadPool(2);
	private HttpClient client= HttpClientBuilder.create().build();

	@Override
	public final String sendServerRequest(String request) {
		if (isAsync()) {
			throw new IllegalStateException("Method is intended for synchronous Requests only.");
		}
		String serverResponse = null;
		// make the string entity
		StringEntity requestEntity = new StringEntity(request, ContentType.APPLICATION_JSON);
		HttpPost postMethod = new HttpPost(getCommandUrl());
		postMethod.setEntity(requestEntity);
		// execute the request
		HttpResponse response;
		try {
			response = client.execute(postMethod);
			serverResponse = responseToString(response);
		} catch (ClientProtocolException e) {
			throw new IllegalArgumentException("Client protocol exception during HTTP request");
		} catch (IOException e) {
			throw new IllegalArgumentException("IO Exception during HTTP request");
		}
		return serverResponse;
	}

	@Override
	public final void sendAsyncCallbackRequest(String requestContent, FutureCallback<Content> callback) {
		if (!isAsync()) {
			throw new IllegalStateException("Method is intended for asynchronous Requests only.");
		}
		Async async = Async.newInstance().use(threadpool);
		final Request request = Request.Post(getCommandUrl()).bodyString(requestContent, ContentType.APPLICATION_JSON);
		async.execute(request, callback);
	}

	/**
	 * Returns the URL suffix used to address the server command represented by the
	 * concrete RequestHandler instance.
	 * 
	 * @return the URL suffix used to address the server command represented by the
	 *         concrete RequestHandler instance
	 */
	protected abstract String getCommandSuffix();

	/**
	 * Returns {@code true} if the handler uses asynchronous requests.
	 * 
	 * @return {@code true} if the handler uses asynchronous requests
	 */
	protected abstract boolean isAsync();

	/**
	 * Returns the URL used to access the command represented by the concrete
	 * RequestHandler instance.
	 * 
	 * @return the URL used to access the command represented by the concrete
	 *         RequestHandler instance
	 */
	protected String getCommandUrl() {
		return SERVER_URL + getCommandSuffix();
	}

	@Override
	public void shutDown() {
		if (threadpool != null) {
			threadpool.shutdown();
		}
	}

	/**
	 * Reads the response and returns a string
	 * 
	 * @param response
	 * @return
	 */
	protected String responseToString(HttpResponse response) throws IOException {
		StringBuilder result = new StringBuilder();
		try(BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))){
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
		}
		return result.toString();
	}
}

package optimization;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.fluent.Content;
import org.apache.http.concurrent.FutureCallback;
import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.optimizer.OptimizerStateListener;
import org.opt4j.core.start.Constant;
import org.opt4j.satdecoding.SatSolvingListener;
import org.opt4j.satdecoding.Solver;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import blueprint.BluePrintClassContainer;
import json.Command;
import json.CommandResponse;
import modules.VarOrderDynamicModule;
import python.CommunicationClassContainer;
import python.RequestHandlerInit;
import python.RequestHandlerShutDown;

/**
 * The {@link VariableOrderManagerAsync} has the same functionality as its
 * parent class, but operates based on asynchronous calls of the python server.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class VariableOrderManagerAsync extends VariableOrderManagerAbstract implements OptimizerStateListener {

	/**
	 * The states of the manager: WAITING_FOR_RESPONSE : a server request was sent,
	 * but the answer is not yet here; NEW_INFORMATION: new information is
	 * available, but was not yet used to update the optimization; READY: basic
	 * operating mode - the optimization follows the currently known information and
	 * a request can be sent at any time.
	 * 
	 * @author Fedor Smirnov
	 *
	 */
	public enum OrderManagerState {
		WAITING_FOR_RESPONSE, NEW_INFORMATION, READY
	}
	
	protected String lastServerResponse = null;
	protected OrderManagerState currentState = OrderManagerState.READY;
	protected int iterationOfLastUpdate = 0;
	protected final FutureCallback<Content> callback;
	protected final double importanceThreshold;
	protected final boolean importanceMemory;
	protected final String summaryParam;
	protected final String calcParam;
	protected static final String IMPORTANCE_THRESHOLD_PARAMETER_STRING = "importance_threshold";
	protected static final String IMPORTANCE_MEMORY_PARAMETER_STRING = "importance_memory";
	protected static final String IMPORTANCE_SUMMARY_PARAMETER_STRING = "importance_summary";
	protected static final String IMPORTANCE_CALCULATION_PARAMETER_STRING = "importance_calc";
	protected final RequestHandlerInit requestHandlerInit;
	protected final RequestHandlerShutDown requestHandlerShutDown;

	@Inject
	public VariableOrderManagerAsync(BluePrintClassContainer bluePrintContainer,
			CommunicationClassContainer communicationContainer,
			@Constant(value = "maxSolvingSampleNumber", namespace = VariableOrderManagerAbstract.class) int maxNumSolvingSamples,
			@Constant(value = "iterationInterval", namespace = VariableOrderManagerAbstract.class) int iterationInterval,
			@Constant(value = "importanceThreshold", namespace = VariableOrderManagerAbstract.class) double importanceThreshold,
			@Constant(value = "importanceMemory", namespace = VariableOrderManagerAbstract.class) boolean importanceMemory,
			@Constant(value = "importanceSummary", namespace = VarOrderDynamicModule.class) String summaryParam,
			@Constant(value = "importanceCalculation", namespace = VarOrderDynamicModule.class) String calcParam,
			Solver solver, ImportanceUpdate importanceUpdate, ModelMemory modelMemory) {
		super(bluePrintContainer, communicationContainer, maxNumSolvingSamples, iterationInterval, solver,
				importanceUpdate, modelMemory);
		this.importanceMemory = importanceMemory;
		this.importanceThreshold = importanceThreshold;
		this.requestHandlerInit = communicationContainer.getRequestHandlerInit();
		this.requestHandlerShutDown = communicationContainer.getRequestHandlerShutDown();
		this.summaryParam = summaryParam;
		this.calcParam = calcParam;
		this.callback = new FutureCallback<Content>() {
			@Override
			public void completed(Content result) {
				if (!currentState.equals(OrderManagerState.WAITING_FOR_RESPONSE)) {
					throw new IllegalStateException("Received answer while not waiting.");
				}
				lastServerResponse = result.asString();
				currentState = OrderManagerState.NEW_INFORMATION;
			}

			@Override
			public void failed(Exception ex) {
				throw new IllegalStateException("Request to python failed. " + ex.getMessage());
			}

			@Override
			public void cancelled() {
				throw new IllegalStateException("Request to python cancelled.");
			}
		};
	}

	@Override
	public void iterationComplete(int iteration) {
		currentIteration++;
		genoToModelMap.clear();
		switch (currentState) {
		case READY:
			// send an async request if enough time has passed
			if (currentIteration - iterationOfLastUpdate >= iterationInterval) {
				iterationOfLastUpdate = currentIteration;
				String requestString = commParser.makeServerRequestString(curSampleBatch);
				curSampleBatch.clear();
				currentState = OrderManagerState.WAITING_FOR_RESPONSE;
				requestHandler.sendAsyncCallbackRequest(requestString, callback);
			}
			break;
		case NEW_INFORMATION:
			// update the order with the new information
			updateOrderInformation(lastServerResponse);
			currentState = OrderManagerState.READY;
			break;
		default:
			// no action otherwise
			break;
		}
	}

	@Override
	public void optimizationStarted(Optimizer optimizer) {
		// send a synchronous request to initialize the Python server
		Map<String, Object> commandParameters = new HashMap<>();
		commandParameters.put(IMPORTANCE_THRESHOLD_PARAMETER_STRING, importanceThreshold);
		commandParameters.put(IMPORTANCE_MEMORY_PARAMETER_STRING, importanceMemory);
		commandParameters.put(IMPORTANCE_SUMMARY_PARAMETER_STRING, summaryParam);
		commandParameters.put(IMPORTANCE_CALCULATION_PARAMETER_STRING, calcParam);
		Command initCommand = new Command(commandParameters);
		Gson gson = new Gson();
		String jsonString = gson.toJson(initCommand);
		String response = requestHandlerInit.sendServerRequest(jsonString);
		JsonReader reader = new JsonReader(new StringReader(response));
		reader.setLenient(true);
		CommandResponse commandResponse = gson.fromJson(reader, CommandResponse.class);
		if (!commandResponse.isSuccess()) {
			throw new IllegalArgumentException("Server Initialization failed.");
		}
	}

	@Override
	public void optimizationStopped(Optimizer optimizer) {
		// close the communication threads
		requestHandler.shutDown();
	}

	@Override
	public boolean registerSolvingListener(SatSolvingListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unregisterSolvingListener(SatSolvingListener listener) {
		// TODO Auto-generated method stub
		return false;
	}
}

package python;

import com.google.inject.Inject;

import entity.LabeledSampleFactory;

/**
 * Container for the classes that handle the communication with the Python
 * server.
 * 
 * @author Fedor Smirnov
 *
 */
public class CommunicationClassContainer {

	protected final LabeledSampleFactory sampleFactory;
	protected final RequestHandler requestHandlerImportance = new RequestHandlerImportanceCalc();
	protected final RequestHandlerInit requestHandlerInit = new RequestHandlerInit();
	protected final RequestHandlerShutDown requestHandlerShutDown = new RequestHandlerShutDown();
	protected final CommunicationParser commParser;

	@Inject
	public CommunicationClassContainer(LabeledSampleFactory sampleFactory, CommunicationParser commParser) {
		this.sampleFactory = sampleFactory;
		this.commParser = commParser;
	}

	public LabeledSampleFactory getSampleFactory() {
		return sampleFactory;
	}

	public RequestHandler getRequestHandler() {
		return requestHandlerImportance;
	}

	public CommunicationParser getCommParser() {
		return commParser;
	}

	public RequestHandlerInit getRequestHandlerInit() {
		return requestHandlerInit;
	}

	public RequestHandlerShutDown getRequestHandlerShutDown() {
		return requestHandlerShutDown;
	}
}

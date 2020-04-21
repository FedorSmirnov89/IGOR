package optimization;

import org.opt4j.satdecoding.SatSolvingListener;
import org.opt4j.satdecoding.Solver;

import blueprint.BluePrintClassContainer;
import python.CommunicationClassContainer;

public class VariableOrderManagerSync extends VariableOrderManagerAbstract {

	public VariableOrderManagerSync(BluePrintClassContainer bluePrintContainer,
			CommunicationClassContainer communicationContainer, int maxNumSolvingSamples, int iterationInterval,
			Solver solver, ImportanceUpdate importanceUpdate, ModelMemory modelMemory) {
		super(bluePrintContainer, communicationContainer, maxNumSolvingSamples, iterationInterval, solver, importanceUpdate,
				modelMemory);
	}
	
	/**
	 * Keeps track of the current iteration and triggers an update at the end of an
	 * iteration interval.
	 */
	@Override
	public void iterationComplete(int iteration) {
		currentIteration++;
		genoToModelMap.clear();
		if (currentIteration % iterationInterval == 0) {
			String serverResponse = getServerResponse();
			updateOrderInformation(serverResponse);
		}
	}
	
	/**
	 * Executes an update: formulates the server request based on the current batch,
	 * waits for and returns the response, and clears the sample batch.
	 * 
	 * @return the response from the server
	 */
	protected String getServerResponse() {
		String requestString = commParser.makeServerRequestString(curSampleBatch);
		// make the request
		String serverResponse = requestHandler.sendServerRequest(requestString);
		// empty the set for the solving samples
		curSampleBatch.clear();
		return serverResponse;
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

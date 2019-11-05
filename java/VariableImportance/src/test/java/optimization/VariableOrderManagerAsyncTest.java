package optimization;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.opt4j.satdecoding.Solver;

import blueprint.BluePrintClassContainer;
import blueprint.BluePrintGeneratorAbstract;
import blueprint.BluePrintProvider;
import entity.LabeledSampleFactory;
import optimization.VariableOrderManagerAsync.OrderManagerState;
import python.CommunicationClassContainer;
import python.CommunicationParser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;

import org.apache.http.client.fluent.Content;

public class VariableOrderManagerAsyncTest {

	protected static BluePrintOrderConfigurator orderConfigurator;
	protected static BluePrintProvider bluePrintProvider;
	protected static BluePrintRepair bluePrintRepair;
	protected static BluePrintCreator bluePrintCreator;
	protected static BluePrintGeneratorAbstract bluePrintGenerator;

	protected static BluePrintClassContainer bluePrintContainer;

	protected static LabeledSampleFactory sampleFactory;
	protected static CommunicationParser commParser;

	protected static CommunicationClassContainer communicationContainer;

	protected static Solver solver;
	protected static ImportanceUpdate importanceUpdate;
	protected static ModelMemory modelMemory;

	@Before
	public void init() {
		orderConfigurator = mock(BluePrintOrderConfigurator.class);
		bluePrintProvider = mock(BluePrintProvider.class);
		bluePrintRepair = mock(BluePrintRepair.class);
		bluePrintCreator = mock(BluePrintCreator.class);
		bluePrintGenerator = mock(BluePrintGeneratorAbstract.class);

		bluePrintContainer = new BluePrintClassContainer(orderConfigurator, bluePrintProvider, bluePrintRepair,
				bluePrintCreator, bluePrintGenerator);

		sampleFactory = mock(LabeledSampleFactory.class);
		commParser = mock(CommunicationParser.class);

		communicationContainer = new CommunicationClassContainer(sampleFactory, commParser);

		solver = mock(Solver.class);
		importanceUpdate = mock(ImportanceUpdate.class);
		modelMemory = mock(ModelMemory.class);
	}

	@Test
	public void testNextIterationNewInformation() {
		VariableOrderManagerAsync tested = spy(new VariableOrderManagerAsync(bluePrintContainer, communicationContainer,
				100, 2, .001, true, "avg", solver, importanceUpdate, modelMemory));
		tested.currentState = OrderManagerState.NEW_INFORMATION;
		tested.lastServerResponse = "last";
		tested.iterationComplete(1);
		assertEquals(OrderManagerState.READY, tested.currentState);
		verify(tested).updateOrderInformation("last");
	}

	@Test
	public void testNextIterationReady() {
		when(commParser.makeServerRequestString(any())).thenReturn("request");
		VariableOrderManagerAsync tested = new VariableOrderManagerAsync(bluePrintContainer, communicationContainer,
				100, 2, .001, true, "avg", solver, importanceUpdate, modelMemory);
		tested.iterationComplete(1);
		assertEquals(1, tested.currentIteration);
		assertEquals(OrderManagerState.READY, tested.currentState);
		tested.iterationComplete(1);
		assertEquals(2, tested.currentIteration);
		assertEquals(2, tested.iterationOfLastUpdate);
		assertEquals(OrderManagerState.WAITING_FOR_RESPONSE, tested.currentState);
	}

	@Test
	public void testCompletedRequest() {
		VariableOrderManagerAsync tested = new VariableOrderManagerAsync(bluePrintContainer, communicationContainer,
				100, 1, .001, true, "avg", solver, importanceUpdate, modelMemory);
		assertNull(tested.lastServerResponse);
		assertEquals(OrderManagerState.READY, tested.currentState);
		Content mockContent = mock(Content.class);
		when(mockContent.asString()).thenReturn("content");
		tested.currentState = OrderManagerState.WAITING_FOR_RESPONSE;
		tested.callback.completed(mockContent);
		assertEquals("content", tested.lastServerResponse);
		assertEquals(OrderManagerState.NEW_INFORMATION, tested.currentState);
	}

	@Test(expected = IllegalStateException.class)
	public void testCompletedRequestWrongState() {
		VariableOrderManagerAsync tested = new VariableOrderManagerAsync(bluePrintContainer, communicationContainer,
				100, 1, .001, true, "avg", solver, importanceUpdate, modelMemory);
		Content mockContent = mock(Content.class);
		tested.callback.completed(mockContent);
	}

	@Test(expected = IllegalStateException.class)
	public void testCancelledRequest() {
		VariableOrderManagerAsync tested = new VariableOrderManagerAsync(bluePrintContainer, communicationContainer,
				100, 1, .001, true, "avg", solver, importanceUpdate, modelMemory);
		tested.callback.cancelled();
	}

	@Test(expected = IllegalStateException.class)
	public void testFailedRequest() {
		VariableOrderManagerAsync tested = new VariableOrderManagerAsync(bluePrintContainer, communicationContainer,
				100, 1, .001, true, "avg", solver, importanceUpdate, modelMemory);
		tested.callback.failed(new Exception());
	}
}

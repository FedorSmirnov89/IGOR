package blueprint;

import static org.junit.Assert.*;

import org.junit.Test;

import optimization.BluePrintCreator;
import optimization.BluePrintOrderConfigurator;
import optimization.BluePrintRepair;

import static org.mockito.Mockito.mock;

public class BluePrintClassContainerTest {

	@Test
	public void test() {
		BluePrintGeneratorAbstract bluePrintGenerator = mock(BluePrintGeneratorAbstract.class);
		BluePrintProvider bluePrintProvider = mock(BluePrintProvider.class);
		BluePrintRepair bluePrintRepair = mock(BluePrintRepair.class);
		BluePrintCreator bluePrintCreator = mock(BluePrintCreator.class);
		BluePrintOrderConfigurator orderConfigurator = mock(BluePrintOrderConfigurator.class);
		BluePrintClassContainer tested = new BluePrintClassContainer(orderConfigurator, bluePrintProvider,
				bluePrintRepair, bluePrintCreator, bluePrintGenerator);
		assertEquals(bluePrintGenerator, tested.getBluePrintGenerator());
		assertEquals(bluePrintProvider, tested.getBluePrintProvider());
		assertEquals(bluePrintRepair, tested.getBluePrintRepair());
		assertEquals(bluePrintCreator, tested.getBluePrintCreator());
		assertEquals(orderConfigurator, tested.getOrderConfigurator());
	}
}

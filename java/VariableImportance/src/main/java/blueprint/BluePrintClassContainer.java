package blueprint;

import com.google.inject.Inject;

import optimization.BluePrintCreator;
import optimization.BluePrintOrderConfigurator;
import optimization.BluePrintRepair;

/**
 * Container that summarizes the classes that maintain the genotype blueprint.
 * 
 * @author Fedor Smirnov
 *
 */
public class BluePrintClassContainer {

	protected final BluePrintOrderConfigurator orderConfigurator;
	protected final BluePrintProvider bluePrintProvider;
	protected final BluePrintRepair bluePrintRepair;
	protected final BluePrintCreator bluePrintCreator;
	protected final BluePrintGeneratorAbstract bluePrintGenerator;

	@Inject
	public BluePrintClassContainer(BluePrintOrderConfigurator orderConfigurator, BluePrintProvider bluePrintProvider,
			BluePrintRepair bluePrintRepair, BluePrintCreator bluePrintCreator,
			BluePrintGeneratorAbstract bluePrintGenerator) {
		this.orderConfigurator = orderConfigurator;
		this.bluePrintProvider = bluePrintProvider;
		this.bluePrintRepair = bluePrintRepair;
		this.bluePrintCreator = bluePrintCreator;
		this.bluePrintGenerator = bluePrintGenerator;
	}

	public BluePrintOrderConfigurator getOrderConfigurator() {
		return orderConfigurator;
	}

	public BluePrintProvider getBluePrintProvider() {
		return bluePrintProvider;
	}

	public BluePrintRepair getBluePrintRepair() {
		return bluePrintRepair;
	}

	public BluePrintCreator getBluePrintCreator() {
		return bluePrintCreator;
	}

	public BluePrintGeneratorAbstract getBluePrintGenerator() {
		return bluePrintGenerator;
	}
}

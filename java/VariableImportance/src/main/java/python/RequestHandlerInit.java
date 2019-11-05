package python;

/**
 * 
 * Used at the start of each optimization run to initialize the classes used to
 * learn the variable importance.
 * 
 * @author Fedor Smirnov
 *
 */
public class RequestHandlerInit extends RequestHandlerAbstract {

	@Override
	protected String getCommandSuffix() {
		return "/initialize_optimization_run";
	}

	@Override
	protected boolean isAsync() {
		return false;
	}

}

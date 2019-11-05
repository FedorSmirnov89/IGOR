package python;

/**
 * 
 * Handler used to asynchronously request the calculation of the variable
 * importance.
 * 
 * @author Fedor Smirnov
 *
 */
public class RequestHandlerImportanceCalc extends RequestHandlerAbstract {

	@Override
	protected String getCommandSuffix() {
		return "calculate_importance";
	}

	@Override
	protected boolean isAsync() {
		return true;
	}
}

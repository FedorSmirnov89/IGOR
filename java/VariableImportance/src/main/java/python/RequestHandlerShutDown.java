package python;

public class RequestHandlerShutDown extends RequestHandlerAbstract {

	@Override
	protected String getCommandSuffix() {
		return "/shut_down_server";
	}

	@Override
	protected boolean isAsync() {
		return false;
	}

}

package blueprint;

import java.util.Set;

import com.google.inject.Singleton;

/**
 * The standard implementation of the {@link BluePrintProvider}.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class BluePrintProviderBasic implements BluePrintProvider {

	private BluePrintSat currentBlueprint;
	private Set<Object> encodedVariables;

	@Override
	public boolean isInit() {
		return (currentBlueprint != null) && (encodedVariables != null);
	}

	@Override
	public BluePrintSat getCurrentBlueprint() {
		checkInit();
		return currentBlueprint;
	}

	@Override
	public void setCurrentBlueprint(BluePrintSat updatedBlueprint, Set<Object> encodedVariables) {
		this.currentBlueprint = updatedBlueprint;
		this.encodedVariables = encodedVariables;
	}

	@Override
	public Set<Object> getCurrentlyEncodedVariables() {
		checkInit();
		return encodedVariables;
	}
	
	protected void checkInit() {
		if (!isInit()) {
			throw new IllegalArgumentException("Blueprint not yet initialized.");
		}
	}
}

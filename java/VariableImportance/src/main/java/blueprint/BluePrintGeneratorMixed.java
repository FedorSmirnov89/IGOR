package blueprint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opt4j.core.genotype.DoubleBounds;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.sf.opendse.optimization.VariableClassOrder;

/**
 * Generator for a mixed blueprint containing (a) the boolean genes for the
 * important vars and (b) the boolean and the double genes for the unimportant
 * vars.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class BluePrintGeneratorMixed extends BluePrintGeneratorAbstract{

	@Inject
	public BluePrintGeneratorMixed(VariableClassOrder classOrder) {
		super(.1, classOrder);
	}

	@Override
	public BluePrintSat createUpdatedBlueprint(Set<Object> encodedVariables, List<Object> importantVariables) {
		// get the set of the variables that will be encoded until the next
		// update
		Set<Object> encodedNext = getUpdatedEncodedVariables(encodedVariables, importantVariables);
		List<Object> booleanEncodedVars = new ArrayList<>(encodedNext);
		Set<Object> unimportantEncoded = new HashSet<>(encodedNext);
		unimportantEncoded.removeAll(importantVariables);
		List<Object> doubleEncodedVars = new ArrayList<>(unimportantEncoded);
		// create the boolean blueprint
		Object[] varBooleanList = new Object[booleanEncodedVars.size()];
		for (int idx = 0; idx < booleanEncodedVars.size(); idx++) {
			varBooleanList[idx] = booleanEncodedVars.get(idx);
		}
		// create the double blueprint
		Object[] varDoubleList = new Object[doubleEncodedVars.size()];
		for (int idx = 0; idx < doubleEncodedVars.size(); idx++) {
			varDoubleList[idx] = doubleEncodedVars.get(idx);
		}
		// create the bounds and the order map
		DoubleBounds doubleBounds = createBounds(varDoubleList);
		Map<Object, Double> varToFixedOrderMap = createFixedOrderMap(importantVariables);
		return new BluePrintSatList(varBooleanList, varDoubleList, doubleBounds, varToFixedOrderMap);
	}

	@Override
	public Set<Object> getUpdatedEncodedVariables(Set<Object> encodedVariables, List<Object> importantVariables) {
		Set<Object> result = new HashSet<>(encodedVariables);
		result.addAll(importantVariables);
		return result;
	}

	@Override
	public BluePrintSat createInitialBlueprint(Set<Object> encodedVariables) {
		return createUpdatedBlueprint(encodedVariables, new ArrayList<>());
	}
}

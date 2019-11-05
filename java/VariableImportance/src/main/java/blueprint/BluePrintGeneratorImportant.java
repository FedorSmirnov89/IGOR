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
 * Blueprint generator for the case where only the important variables are
 * present in the SAT genotype (except immediately after the initialization,
 * when no importance information is available. During this time, all variable
 * are in the genotype to enable an importance exploration).
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class BluePrintGeneratorImportant extends BluePrintGeneratorAbstract{

	@Inject
	public BluePrintGeneratorImportant(VariableClassOrder classOrder) {
		super(1., classOrder);
	}

	@Override
	public BluePrintSat createUpdatedBlueprint(Set<Object> encodedVariables, List<Object> importantVariables) {
		Set<Object> updatedEncodedVars = getUpdatedEncodedVariables(encodedVariables, importantVariables);
		List<Object> varList = new ArrayList<>(updatedEncodedVars);
		Object[] varBooleanList = new Object[varList.size()];
		for (int idx = 0; idx < varList.size(); idx++) {
			varBooleanList[idx] = varList.get(idx);
		}
		Object[] varDoubleList = new Object[0];
		DoubleBounds doubleBounds = createBounds(varDoubleList);
		Map<Object, Double> varToFixedOrderMap = createFixedOrderMap(
				importantVariables.isEmpty() ? new ArrayList<>(encodedVariables) : importantVariables);
		return new BluePrintSatList(varBooleanList, varDoubleList, doubleBounds, varToFixedOrderMap);
	}

	@Override
	public Set<Object> getUpdatedEncodedVariables(Set<Object> encodedVariables, List<Object> importantVariables) {
		if (importantVariables.isEmpty()) {
			return encodedVariables;
		} else {
			return new HashSet<>(importantVariables);
		}
	}

	@Override
	public BluePrintSat createInitialBlueprint(Set<Object> encodedVariables) {
		return createUpdatedBlueprint(encodedVariables, new ArrayList<>());
	}
}

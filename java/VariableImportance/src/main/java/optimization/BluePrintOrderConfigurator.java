package optimization;

import java.util.Map;
import java.util.Map.Entry;

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.BooleanGenotype;
import org.opt4j.core.genotype.DoubleGenotype;
import org.opt4j.satdecoding.SATGenotype;
import org.opt4j.satdecoding.VarOrder;

import com.google.inject.Inject;

import blueprint.BluePrintProvider;
import blueprint.BluePrintSat;

/**
 * The {@link BluePrintOrderConfigurator} reads the information contained in the
 * genotype, interprets it according to the current blueprint, and creates a
 * {@link VarOrder} that reflects the variable order.
 * 
 * @author Fedor Smirnov
 *
 */
public class BluePrintOrderConfigurator {

	protected final BluePrintProvider blueprintProvider;

	@Inject
	public BluePrintOrderConfigurator(BluePrintProvider blueprintProvider) {
		this.blueprintProvider = blueprintProvider;
	}

	public VarOrder generateVarOrder(Genotype genotype) {
		if (!(genotype instanceof SATGenotype)) {
			throw new IllegalArgumentException("The given genotype is not a SAT genotype");
		}
		SATGenotype satGeno = (SATGenotype) genotype;
		// get the vectors and the current blueprint
		BluePrintSat curBlueprint = blueprintProvider.getCurrentBlueprint();
		BooleanGenotype booleanVector = satGeno.getBooleanVector();
		DoubleGenotype doubleVector = satGeno.getDoubleVector();
		if (booleanVector.size() != curBlueprint.getBooleanSize()) {
			throw new IllegalStateException("The boolean part of the genotype does not match the blueprint.");
		}
		if (doubleVector.size() != curBlueprint.getDoubleSize()) {
			throw new IllegalStateException("The double part of the genotype does not match the blueprint.");
		}
		// Set the phases according to the boolean vector and the blueprint
		VarOrder result = new VarOrder();
		for (int idx = 0; idx < curBlueprint.getBooleanSize(); idx++) {
			Object var = curBlueprint.getVarForBoolIdx(idx);
			boolean phase = booleanVector.get(idx);
			result.setPhase(var, phase);
		}
		// Set the fix orders according to the blueprint
		Map<Object, Double> fixOrderMap = curBlueprint.getFixedOrderMap();
		for (Entry<Object, Double> entry : fixOrderMap.entrySet()) {
			Object var = entry.getKey();
			double order = fixOrderMap.get(var);
			result.setActivity(var, order);
		}
		// Set the explores orders according to the double geno and the blueprint
		for (int idx = 0; idx < curBlueprint.getDoubleSize(); idx++) {
			Object var = curBlueprint.getVarForDoubleIdx(idx);
			double order = doubleVector.get(idx);
			result.setActivity(var, order);
		}
		return result;
	}
}

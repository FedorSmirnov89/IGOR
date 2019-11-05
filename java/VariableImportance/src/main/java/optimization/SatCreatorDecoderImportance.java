package optimization;

import java.util.Collection;
import java.util.Map;

import org.opt4j.core.common.random.Rand;
import org.opt4j.core.optimizer.Control;
import org.opt4j.core.start.Constant;
import org.opt4j.satdecoding.SATManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.sf.opendse.optimization.SATConstraints;
import net.sf.opendse.optimization.SATCreatorDecoder;
import net.sf.opendse.optimization.SpecificationWrapper;
import net.sf.opendse.optimization.VariableClassOrder;
import net.sf.opendse.optimization.encoding.Interpreter;

/**
 * Same functionality as the original {@link SATCreatorDecoder}, but no
 * randomize (taken care of by the {@link BluePrintCreator}).
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class SatCreatorDecoderImportance extends SATCreatorDecoder {

	@Inject
	public SatCreatorDecoderImportance(VariableClassOrder order, SATManager manager, Rand random,
			SATConstraints constraints, SpecificationWrapper specificationWrapper, Interpreter interpreter,
			Control control,
			@Constant(value = "variableorder", namespace = SATCreatorDecoder.class) boolean useVariableOrder) {
		super(order, manager, random, constraints, specificationWrapper, interpreter, control, useVariableOrder);
	}

	@Override
	public void randomize(Collection<Object> variables, Map<Object, Double> lowerBounds,
			Map<Object, Double> upperBounds, Map<Object, Double> priorities, Map<Object, Boolean> phases) {
		// this should be not be necessary - the randomization is performed when
		// creating the genotype
	}
}

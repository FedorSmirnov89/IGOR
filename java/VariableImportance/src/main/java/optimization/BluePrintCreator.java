package optimization;

import java.util.Random;

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.BooleanGenotype;
import org.opt4j.core.genotype.DoubleBounds;
import org.opt4j.core.genotype.DoubleGenotype;
import org.opt4j.satdecoding.SATGenotype;

import com.google.inject.Inject;

import blueprint.BluePrintProvider;
import blueprint.BluePrintSat;

/**
 * The {@link BluePrintCreator} creates random genotypes according to the
 * currently used {@link BluePrintSat}.
 * 
 * @author Fedor Smirnov
 *
 */
public class BluePrintCreator {

	protected final BluePrintProvider blueprintProvider;
	protected final Random rand;
	
	@Inject
	public BluePrintCreator(BluePrintProvider blueprintProvider, Random rand) {
		this.blueprintProvider = blueprintProvider;
		this.rand = rand;
	}
	
	public Genotype createGenotype(){
		BluePrintSat curBlueprint = blueprintProvider.getCurrentBlueprint();
		// create the boolean part
		BooleanGenotype booleanVector = new BooleanGenotype();
		for (int idx = 0; idx < curBlueprint.getBooleanSize(); idx++){
			booleanVector.add(rand.nextBoolean());
		}
		// create the double part
		DoubleBounds bounds = curBlueprint.getBoundsForDoubleGeno();
		DoubleGenotype doubleVector = new DoubleGenotype(bounds);
		for (int idx = 0; idx < curBlueprint.getDoubleSize(); idx++){
			double lb = bounds.getLowerBound(idx);
			double ub = bounds.getUpperBound(idx);
			double order = (ub - lb) * rand.nextDouble() + lb;
			doubleVector.add(order);
		}
		// merge them into a SAT geno
		return new SATGenotype(booleanVector, doubleVector);
	}
}

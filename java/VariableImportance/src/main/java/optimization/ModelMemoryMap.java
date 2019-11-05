package optimization;

import java.util.HashMap;
import java.util.Map;

import org.opt4j.core.Individual;
import org.opt4j.core.IndividualSet;
import org.opt4j.core.IndividualSetListener;
import org.opt4j.core.optimizer.Population;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A {@link ModelMemory} that is based on a simple map and that keeps track of
 * the models for the current population.y
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class ModelMemoryMap implements ModelMemory, IndividualSetListener {

	protected final Map<Individual, Map<Object, Boolean>> indiToModelMap;

	@Inject
	public ModelMemoryMap(Population population) {
		indiToModelMap = new HashMap<>();
		population.addListener(this);
	}

	@Override
	public void rememberModel(Individual indi, Map<Object, Boolean> model) {
		if (indiToModelMap.containsKey(indi)) {
			throw new IllegalArgumentException("indi already in the map");
		}
		indiToModelMap.put(indi, model);
	}

	@Override
	public Map<Object, Boolean> getModel(Individual indi) {
		if (!indiToModelMap.containsKey(indi) && indi.getPhenotype() != null) {
			throw new IllegalArgumentException("Model for given individual not in the map");
		}
		return indiToModelMap.get(indi);
	}

	@Override
	public void individualAdded(IndividualSet collection, Individual individual) {
		// nothing to do
	}

	@Override
	public void individualRemoved(IndividualSet collection, Individual individual) {
		if (individual.getPhenotype() != null) {
			if (!indiToModelMap.containsKey(individual)) {
				throw new IllegalArgumentException("Model for given individual not in the map");
			}
			indiToModelMap.remove(individual);
		}
	}
}

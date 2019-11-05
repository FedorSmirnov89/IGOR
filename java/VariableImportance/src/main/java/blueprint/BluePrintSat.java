package blueprint;

import java.util.Map;

import org.opt4j.core.genotype.DoubleBounds;

import com.google.inject.ImplementedBy;


/**
 * Interface for the classes containing the blueprints for the sat genotypes
 * used throughout the exploration.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(BluePrintSatList.class)
public interface BluePrintSat {

	/**
	 * Returns the size of the boolean genotype containing the phases of the encoded
	 * variables.
	 * 
	 * @return the size of the boolean genotype containing the phases of the encoded
	 *         variables
	 */
	public int getBooleanSize();

	/**
	 * Returns the size of the double genotype containing the orders of the encoded
	 * variables.
	 * 
	 * @return the size of the double genotype containing the orders of the encoded
	 *         variables
	 */
	public int getDoubleSize();

	/**
	 * Returns the variable that is currently written in the boolean genotype at the
	 * given index.
	 * 
	 * @param idx
	 * @return
	 */
	public Object getVarForBoolIdx(int idx);

	/**
	 * Returns the variable that is currently written in the double genotype at the
	 * given index.
	 * 
	 * @param idx
	 *            the given index
	 * @return the variable that is currently written in the double genotype at the
	 *         given index
	 */
	public Object getVarForDoubleIdx(int idx);

	/**
	 * Returns the idx of the given var in the double genotype.
	 * 
	 * @param var
	 *            the given variable object
	 * @return the idx of the given var in the double genotype
	 */
	public int getDoubleIdxForVar(Object var);

	/**
	 * Return the double bounds that can be used for the current blueprint of the
	 * double geno.
	 * 
	 * @return the double bounds that can be used for the current blueprint of the
	 *         double geno
	 */
	public DoubleBounds getBoundsForDoubleGeno();

	/**
	 * Get a map mapping the vars with a fixed order to their order.
	 * 
	 * @return a map mapping the vars with a fixed order to their order
	 */
	public Map<Object, Double> getFixedOrderMap();

}

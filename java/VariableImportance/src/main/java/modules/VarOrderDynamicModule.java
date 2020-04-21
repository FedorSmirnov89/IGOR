package modules;

import org.opt4j.core.common.completer.SequentialIndividualCompleter;
import org.opt4j.core.config.annotations.File;
import org.opt4j.core.start.Constant;
import org.opt4j.satdecoding.SATManager;

import blueprint.BluePrintGeneratorAbstract;
import blueprint.BluePrintGeneratorImportant;
import blueprint.BluePrintGeneratorMixed;
import net.sf.opendse.optimization.DesignSpaceExplorationModule;
import net.sf.opendse.optimization.SATCreatorDecoder;
import optimization.SatCreatorDecoderImportance;
import optimization.SequentialIndividualCompleterTimeOut;
import optimization.VariableOrderManagerAbstract;
import optimization.VariableOrderManagerAsync;

/**
 * Module that integrates the complete infrastructure necessary for the
 * communication with the Python ML server and the update based on the
 * importance information.
 * 
 * @author Fedor Smirnov
 */
public class VarOrderDynamicModule extends DesignSpaceExplorationModule {

	@Constant(value = "maxSolvingSampleNumber", namespace = VariableOrderManagerAbstract.class)
	protected int maximalSampleNumber = 100;
	@Constant(value = "iterationInterval", namespace = VariableOrderManagerAbstract.class)
	protected int importanceUpdatePeriod = 5;
	@Constant(value = "importanceMemory", namespace = VariableOrderManagerAbstract.class)
	protected boolean importanceMemory = true;
	@Constant(value = "importanceThreshold", namespace = VariableOrderManagerAbstract.class)
	protected double importanceThreshold = .001;
	@File(folder = true)
	@Constant(value = "pathToPythonDir", namespace = VariableOrderManagerAbstract.class)
	protected String pathToPythonDirectory = "";

	protected CalcTypes calcType = CalcTypes.ROOT;

	protected GenotypeType genotypeType = GenotypeType.MIXED;

	protected ImportanceSummary importanceSummary = ImportanceSummary.AVG;

	public enum GenotypeType {
		IMPORTANT_ONLY, MIXED
	}

	/**
	 * Enum for the different ways to calculate the importance.
	 * 
	 * @author fedor
	 *
	 *         ROOT: importance of each var according to InfGain when used as root
	 *         of tree TREE_POS: importance of each var according to InfGain in its
	 *         postion in best tree
	 *
	 */
	public enum CalcTypes {
		ROOT, TREE_POS
	}

	public CalcTypes getCalcType() {
		return calcType;
	}

	public void setCalcType(CalcTypes calcType) {
		this.calcType = calcType;
	}

	public enum ImportanceSummary {
		AVG("avg"), MAX("max");

		protected final String paramName;

		private ImportanceSummary(String string) {
			this.paramName = string;
		}
	}

	public ImportanceSummary getImportanceSummary() {
		return importanceSummary;
	}

	public void setImportanceSummary(ImportanceSummary importanceSummary) {
		this.importanceSummary = importanceSummary;
	}

	public String getPathToPythonDirectory() {
		return pathToPythonDirectory;
	}

	public void setPathToPythonDirectory(String pathToPythonDirectory) {
		this.pathToPythonDirectory = pathToPythonDirectory;
	}

	public boolean isImportanceMemory() {
		return importanceMemory;
	}

	public void setImportanceMemory(boolean importanceMemory) {
		this.importanceMemory = importanceMemory;
	}

	public double getImportanceThreshold() {
		return importanceThreshold;
	}

	public void setImportanceThreshold(double importanceThreshold) {
		this.importanceThreshold = importanceThreshold;
	}

	public GenotypeType getGenotypeType() {
		return genotypeType;
	}

	public void setGenotypeType(GenotypeType genotypeType) {
		this.genotypeType = genotypeType;
	}

	public int getMaximalSampleNumber() {
		return maximalSampleNumber;
	}

	public void setMaximalSampleNumber(int maximalSampleNumber) {
		this.maximalSampleNumber = maximalSampleNumber;
	}

	public int getImportanceUpdatePeriod() {
		return importanceUpdatePeriod;
	}

	public void setImportanceUpdatePeriod(int importanceUpdatePeriod) {
		this.importanceUpdatePeriod = importanceUpdatePeriod;
	}

	@Override
	protected void config() {
		bind(SATCreatorDecoder.class).to(SatCreatorDecoderImportance.class);
		bind(SATManager.class).to(VariableOrderManagerAsync.class);
		addIndividualStateListener(VariableOrderManagerAsync.class);
		addOptimizerIterationListener(VariableOrderManagerAsync.class);
		addOptimizerStateListener(VariableOrderManagerAsync.class);
		bind(SequentialIndividualCompleter.class).to(SequentialIndividualCompleterTimeOut.class);
		bindConstant("importanceSummary", VarOrderDynamicModule.class).to(importanceSummary.paramName);
		bindConstant("importanceCalculation", VarOrderDynamicModule.class).to(calcType.name());
		if (genotypeType.equals(GenotypeType.MIXED)) {
			bind(BluePrintGeneratorAbstract.class).to(BluePrintGeneratorMixed.class);
		} else {
			bind(BluePrintGeneratorAbstract.class).to(BluePrintGeneratorImportant.class);
		}
	}
}

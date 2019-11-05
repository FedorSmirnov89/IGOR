package entity;

import java.util.List;

import org.opt4j.core.Individual;

import com.google.inject.ImplementedBy;

/**
 * Interface for the classes responsible to create the {@link LabeledSample}s
 * out of indi.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(LabeledSampleFactoryDefault.class)
public interface LabeledSampleFactory {

	/**
	 * Returns {@code true} if the factory has been initialized.
	 * 
	 * @return {@code true} if the factory has been initialized
	 */
	public boolean isInit();

	/**
	 * Sets the reference to a set of the encoding variables used throughout the
	 * current exploration.
	 * 
	 * @param satVariables
	 *            set of the encoding variables used throughout the current
	 *            exploration
	 */
	public void setSatVariables(List<Object> satVariables);

	/**
	 * Creates the solving sample matching the given {@link Individual}.
	 * 
	 * @param indi
	 *            the given individual
	 * @return the solving sample matching the given {@link Individual}
	 */
	public LabeledSample createLabeledSample(Individual indi);
}

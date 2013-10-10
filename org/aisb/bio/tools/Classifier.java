/*
 * Created on May 16, 2005 by Doug DeJulio
 */
package org.aisb.bio.tools;

/**
 * <p>
 * This is the abstract class to be subclassed by arbitrary data classifiers.
 * Individual classifiers may need more methods than these to be
 * configured, but once one is up and running we should be able to <em>use</em>
 * it via this interface.
 * </p><p>
 * This is for supervised learning algorithms, as the data is tagged with a label
 * when it's added.
 * </p><p>
 * To use objects that implement this: create an object, call the "startTraining()" method,
 * call "train(label, data)" with each labeled piece of data, and then call the "finishTraining()" method.
 * Once you've done that, call "classify(data)" and you'll get back whichever of the labels
 * you used is the best fit for the new data.
 * </p>
 * 
 * @author Doug DeJulio
 *
 */
public abstract class Classifier {
	boolean trainingHasBegun = false;
	boolean trainingHasEnded = false;
	
	public void startTraining() {
		// By default, do nothing.  Individual classifiers may need to
		// override this method, for example to initialize data structures.
		// We're just going to make sure the object is being used correctly.
		if (trainingHasBegun) {
			throw new IllegalStateException("Attemped to begin trianing more than once.");
		}
		trainingHasBegun = true;
	}
	
	public void finishTraining() {
		// By default, do nothing.  Individual classifiers may need to
		// override this method, for example if they can't be trained
		// until they have all the training data.
		if (trainingHasEnded) {
			throw new IllegalStateException("Attempted to finish training more than once.");
		}
		if (!trainingHasBegun) {
			throw new IllegalStateException("Attempted to finish training without starting it first.");
		}
		trainingHasEnded = true;
	}
	
	/**
	 * Feed training data to the classifier.
	 * 
	 * @param label The label that this unit of data has.
	 * @param data The data to be used for classification.
	 */
	public abstract void train(String label, Object data);	
	
	/**
	 * Given a trained classifier, use it to classify a piece
	 * of data.
	 * 
	 * @param data The data to be classified.
	 * @return The label the classifier assigns to this data.
	 */
	public abstract String classify(Object data);
}

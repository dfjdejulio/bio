/*
 * Created on May 16, 2005 by Doug DeJulio
 */
package org.aisb.bio.tools;

import java.util.*;

/**
 * <p>
 * This classifier just figures out the mean of all the features in the training
 * data, and then classifies data by calculating the Euclidean distance between
 * the data to be classified and the means for the categories.  Pretty simple stuff.
 * </p><p>
 * In this particular case, the "data" must be a vector (array) of doubles.  Each
 * elemenet of data must be of the same length, but we don't need to care what that
 * length is.
 * </p>
 * 
 * @author Doug DeJulio
 *
 */
public class EuclideanMeanClassifier extends Classifier {
	Map trainingDatasets;
	Map means;
	int arraySize;
	
	/* (non-Javadoc)
	 * @see org.aisb.bio.tools.Classifier#startTraining()
	 */
	public void startTraining() {
		// Just do some simple allocation here.
		trainingDatasets = new HashMap();
		means = new HashMap();
		arraySize = 0;
		super.startTraining();
	}
	
	/* 
	 * There's a more efficient way to do this.  The means can be calculated without
	 * first storing every piece of data individually (statistical calculators do
	 * this).  But I don't remember the math off the top of my head, so we'll do it
	 * this way for now.  We can reduce the computational cost later.
	 *
	 * (non-Javadoc)
	 * @see org.aisb.bio.tools.Classifier#finishTraining()
	 */
	public void finishTraining() {
		// For each label...
		Set labelSet = trainingDatasets.keySet();
		Iterator labels = labelSet.iterator();
		while (labels.hasNext()) {
			// Get the label.
			String label = (String) labels.next();
			// Get ready to calculate and store the means.
			double[] theseMeans = new double[arraySize];
			// Get the data.
			Vector thisDataset = (Vector) trainingDatasets.get(label);
			Iterator dataset = thisDataset.iterator();
			while (dataset.hasNext()) {
				double[] data = (double[]) dataset.next();
				if (arraySize == 0) {
					// If we don't know how big our data is supposed to be, figure it out.
					arraySize = data.length;
					// Allocate the space for the means.
					theseMeans = new double[arraySize];
				} else {
					// If we do, check it and complain if anything doesn't match.
					if (arraySize != data.length) {
						throw new IllegalStateException("Not all training data is of the same size.");
					}
				}
				// We're ready to train on this piece of data.
				for (int i=0; i < theseMeans.length; i++) {
					theseMeans[i] += data[i];
				}
				// We've added this piece of data to the means array.
			}
			// We've added all the data to the means array.  Let's divide by the number
			// of pieces of data to get the real means.
			for (int i=0; i < theseMeans.length; i++) {
				theseMeans[i] /= thisDataset.size();
			}
			// Now we've got the means vector.  Store it under this label!
			means.put(label, theseMeans);
		}
		// Now we've done this for all the training data.  We're done training!
		super.finishTraining();
	}
	
	/* (non-Javadoc)
	 * @see org.aisb.bio.tools.Classifier#train(java.lang.String, java.lang.Object)
	 */
	public void train(String label, Object trainingDatum) {
		double[] data;
		try {
			// Did we get an array of doubles?
			data = (double[])trainingDatum;
		} catch (ClassCastException e) {
			// If not, we've been fed illegal arguments.
			throw new IllegalArgumentException("The training data must be an array of doubles.");
		}
		// Also barf if the label is null.
		if (label == null) {
			throw new IllegalArgumentException("The label cannot be null.");
		}
		
		// Try to fetch the dataset for this label.
		Vector dataset = (Vector) trainingDatasets.get(label);
		// If we didn't get one, make one and put it in the map.
		if (dataset == null) {
			// Create a new Vector.
			dataset = new Vector();
			// Associate it with this label.
			trainingDatasets.put(label, dataset);
		}
		// Assertion: "dataset" is a vector (possibly empty) of training data for this label.
		// Just add this data to it!
		dataset.add(data);
		// Done!
	}

	/* (non-Javadoc)
	 * @see org.aisb.bio.tools.Classifier#classify(java.lang.Object)
	 */
	public String classify(Object testData) {
		double bestDistance;
		double[] data;
		String bestLabel = null;
		
		// See if we've got legal data.
		try {
			data = (double[])testData;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("The data to be classified must be an array of doubles.");
		}
		
		// We have to test each label.
		Set labelSet = means.keySet();
		Iterator labels = labelSet.iterator();

		// Start with the first one.
		if (labels.hasNext()) {
			// Initially, the first category is the best fit.
			bestLabel = (String) labels.next();
			double[] mean = (double[])means.get(bestLabel);
			bestDistance = distance(mean, data);
		} else {
			// If we got here, there isn't even one single category we've been
			// trained with.
			throw new IllegalStateException("The classifier has not been trained with even one category.");
		}
		
		// Now, check all the other categories.
		while (labels.hasNext()) {
			// Get the next category.
			String label = (String) labels.next();
			double[] mean = (double[])means.get(label);
			double thisDistance = distance(mean, data);
			// Are we closer to this one?
			if (thisDistance < bestDistance) {
				// If so, record the new best distance and best label.
				bestDistance = thisDistance;
				bestLabel = label;
			}
		}
		// We've now gone through every label.
		// One more sanity check, to prevent bugs...
		if (bestLabel == null) {
			throw new IllegalStateException("There should be no way we conclude the best label is 'null'.  The program has a bug.");
		}
		
		return bestLabel;
	}
	
	/**
	 * <p>
	 * Calculate the distance between two points.
	 * </p><p>
	 * Pass in two equal-sized arrays of doubles.  Treating them as Euclidean coordinates,
	 * calculate the distance between them.
	 * </p>
	 * 
	 * @param point1 The first point.
	 * @param point2 The second point.
	 * @return
	 */
	private double distance(double[] point1, double[] point2) {
		double distance;
		distance = 0;
		if (point1.length != point2.length) {
			throw new IllegalArgumentException("The two points must be of the same dimensionality.");
		}
		// Sum the squares of the differences...
		for (int i=0; i < point1.length; i++) {
			double difference = point1[i] - point2[i];
			distance += (difference * difference);
		}
		// ...and then take the square root.
		distance = Math.sqrt(distance);
		return distance;
	}

}

/*
 * Created on May 13, 2005 by Doug DeJulio
 */
package org.aisb.bio.assignments;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import org.aisb.bio.tools.Classifier;
import org.aisb.bio.tools.EuclideanMeanClassifier;
import org.aisb.bio.tools.images.WaveletFeatureExtractor;

/**
 * <p>
 * This implements assignment B4.
 * </p><p>
 * </p>
 * <hr/>
 * <p>
 * Note that as per discussion with Professor Murphy, I've made use of an
 * implementation of the Wavelet transform that someone else wrote.  I found
 * it at <a href="http://www.bearcave.com/misl/misl_tech/wavelets/daubechies/daub.java">http://www.bearcave.com/misl/misl_tech/wavelets/daubechies/daub.java</a>,
 * along with a discussion at <a href="http://www.bearcave.com/misl/misl_tech/wavelets/daubechies/">http://www.bearcave.com/misl/misl_tech/wavelets/daubechies/</a>.
 * </p><p>
 * My only change to that code has been to give it a package declaration so that it works better with
 * more modern versions of Java, and to remove an extraneous import statement that was causing my
 * development environment to flag it with warnings.
 * </p>
 * 
 * @author Doug DeJulio
 *
 */
public class HomeworkB4 {
	static PrintStream out = System.out;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	// We have two categories.  So, our confusion matrix is 2x2.
	static int[][] confusionMatrix = new int[2][2];

	public static void main(String[] args) throws Exception {
		// We need to set this property.  We use AWT for its image manipulation, but we don't
		// actually do any graphic displays, so we need to say so.
		System.setProperty("java.awt.headless", "true");
		// Prompt for two paths.
		out.print("Enter the name of a directory containing images: ");
		String firstImageDir = in.readLine();
		//String firstImageDir = "data/images/one"; out.println(firstImageDir);

		File dir1 = new File(firstImageDir);
		if (!dir1.isDirectory()) {
			throw new IllegalArgumentException("Error: " + firstImageDir + " is not a directory.");
		}

		out.print("Enter the name of another directory containing images: ");
		String secondImageDir = in.readLine();
		//String secondImageDir = "data/images/two"; out.println(secondImageDir);
		File dir2 = new File(secondImageDir);
		if (!dir2.isDirectory()) {
			throw new IllegalArgumentException("Error: " + secondImageDir + " is not a directory.");
		}
		
		out.print("So, how many levels of decomposition do you want? ");
		String decompLevelsInput = in.readLine();
		int decompLevels = Integer.parseInt(decompLevelsInput);
		//int decompLevels = 3; out.println(decompLevels);
		
		out.print("And how many folds of cross-validation? ");
		String foldsInput = in.readLine();
		int folds = Integer.parseInt(foldsInput);
		//int folds=3; out.println(folds);
		
		// Get sets of files.
		File[] imageFileSetOne = dir1.listFiles();
		File[] imageFileSetTwo = dir2.listFiles();

		// Print out the text file.
		PrintStream featureFile = new PrintStream(new FileOutputStream("tmp/features.txt"));
		
		// Read 'em, calculate the features, and write them out.
		// (Note that while we're required to write the features out to a text file,
		// it's more convenient for us to then ignore that text file as we do our work.)
		double[] features;
		List imageSetOne = new Vector();
		for (int i=0; i < imageFileSetOne.length; i++) {
			try {
				BufferedImage image = ImageIO.read(imageFileSetOne[i]);
				WaveletFeatureExtractor extractor = new WaveletFeatureExtractor();
				extractor.setImage(image.getData());
				extractor.setLevel(decompLevels);
				extractor.execute();
				features = extractor.getFeatures();
				imageSetOne.add(features);
				featureFile.print(imageFileSetOne[i].getName());
				for (int n=0; n < features.length; n++) {
					featureFile.print(", " + features[n]);
				}
				featureFile.println();
			} catch (Exception e) {
				// If there's any error reading this file, just don't add it to the set.
				// But say something...
				System.err.println(e);
			}
		}
		List imageSetTwo = new Vector();
		for (int i=0; i < imageFileSetTwo.length; i++) {
			try {
				BufferedImage image = ImageIO.read(imageFileSetTwo[i]);
				WaveletFeatureExtractor extractor = new WaveletFeatureExtractor();
				extractor.setImage(image.getData());
				extractor.setLevel(decompLevels);
				extractor.execute();
				features = extractor.getFeatures();
				imageSetTwo.add(features);
				featureFile.print(imageFileSetTwo[i].getName());
				for (int n=0; n < features.length; n++) {
					featureFile.print(", " + features[n]);
				}
				featureFile.println();
			} catch (Exception e) {
				// As above, so below.
				System.err.println(e);
			}
		}
		featureFile.close();
		out.println("The featuers have been extracted, and a copy has been stored in 'features.txt'");
		out.println("(in the 'tmp/' subdirectory).  This is for information only; this file will not");
		out.println("be used in our further processing.");
		
		// Okay.  Now divide the data into training sets and test sets.
		int imageSetOneTrialSize = imageSetOne.size() / folds;
		int imageSetTwoTrialSize = imageSetTwo.size() / folds;
		// Let's clear out our confusion matrix.
		Arrays.fill(confusionMatrix[0], 0);
		Arrays.fill(confusionMatrix[1], 0);
		// Our test set will be setSize/trials big, rounded down.  If we have 14 images
		// and are doing 5 trials, each test set will be of size 2.
		for (int trial=0; trial < folds; trial++) {
			List testingSetOne = new Vector();
			List testingSetTwo = new Vector();
			out.println("Running trial " + (trial+1) + "...");
			// Get a brand spanking new classifier.
			out.println("Training.");
			Classifier c = new EuclideanMeanClassifier();
			// Start training it.
			c.startTraining();
			for (int i=0; i < imageSetOne.size(); i++) {
				double[]data = (double[])imageSetOne.get(i);
				if ((i / imageSetOneTrialSize) == trial) {
					// This is testing data.  Put it in the testing set.
					testingSetOne.add(data);
				} else {
					// This is training data.  Train with it!
					c.train("one", data);
				}
			}
			for (int i=0; i < imageSetTwo.size(); i++) {
				double[] data = (double[])imageSetTwo.get(i);
				if ((i / imageSetOneTrialSize) == trial) {
					// This is testing data.  Put it in the testing set.
					testingSetTwo.add(data);
				} else {
					// This is training data.  Train with it!
					c.train("two", data);
				}
			}
			// We've trained with everything we'll train with.  Wrap it up.
			c.finishTraining();
			// Now, test.
			out.println("Testing.");
			Iterator i;
			i = testingSetOne.iterator();
			while (i.hasNext()) {
				double[] data = (double[]) i.next();
				String category = c.classify(data);
				if (category.equals("one")) {
					// We're in category one, and we tested as in category one.
					confusionMatrix[0][0]++;
				} else {
					// We're in category one, and we tested as in category two.
					confusionMatrix[0][1]++;
				}
			}
			i = testingSetTwo.iterator();
			while (i.hasNext()) {
				double[] data = (double[]) i.next();
				String category = c.classify(data);
				if (category.equals("two")) {
					// We're in category two, and we tested as in category two.
					confusionMatrix[1][1]++;
				} else {
					// We're in category two, and we tested as in category one.
					confusionMatrix[1][0]++;
				}
			}
		}
		out.println("\nResults after all trials:");
		out.println("\tone\ttwo");
		out.println("one\t" + confusionMatrix[0][0] + "\t" + confusionMatrix[0][1]);
		out.println("two\t" + confusionMatrix[1][0] + "\t" + confusionMatrix[1][1]);

	}
}

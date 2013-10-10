/*
 * Created on May 13, 2005 by Doug DeJulio
 */
package org.aisb.bio.tests;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

import junit.framework.TestCase;

import org.aisb.bio.tools.Classifier;
import org.aisb.bio.tools.EuclideanMeanClassifier;
import org.aisb.bio.tools.images.WaveletFeatureExtractor;

/**
 * <p>
 * This is the testing framework for assignment B4, the image classification assignment.
 * </p>
 * 
 * @author Doug DeJulio
 *
 */
public class TestB4 extends TestCase {
	// Toggle the following as appropriate.
	static boolean headless = false;
	// How many times will we do "a bunch" of things?
	int aBunch = 10;
	Random rnd;
	
	/**
	 * Test to see if we can read a single image shipped with the application
	 * in a known location.
	 * 
	 * @throws Exception Any exception thrown by the underlying frameworks.
	 */
	public void testCanReadAnImage() throws Exception {
		// Open a known image.
		File imageFile = new File("data/images/CryBaby.gif");
		BufferedImage image = ImageIO.read(imageFile);
		Raster raster = image.getData();
		// Read a single pixel out.
		int[] values = raster.getPixel(0, 0, (int[])null);
		// Test some known properties of that pixel.
		assertEquals(1, values.length);
		assertEquals(255, values[0]);
		
	}
	
	/**
	 * <p>
	 * Test to see if we can extract features from a known image.
	 * </p><p>
	 * Note that I haven't done the corresponding calculations by hand
	 * without a computer, as the underlying math is a bit much for that.
	 * So we're really only testing whether the feature getter is honoring
	 * the contract it has with the other routines.  More data about what's
	 * going on can be obtained by looking at the assignment of the "features"
	 * array under a debugger.
	 * </p>
	 * 
	 * @throws Exception Any excpetion thrown by the underlying frameworks.
	 */
	public void testCanExtractFeatures() throws Exception {
		// Open a known image.
		File imageFile = new File("data/images/sample.png");
		BufferedImage image = ImageIO.read(imageFile);
		Raster raster = image.getData();
		// Get a feature extractor.
		WaveletFeatureExtractor extractor = new WaveletFeatureExtractor();
		extractor.setImage(raster);
		extractor.setLevel(5);
		extractor.execute();
		double[] features = extractor.getFeatures();
		assertEquals(features.length, 5);
	}
	
	/**
	 * <p>Test our Euclidean mean classifier with one-dimensional data.</p>
	 * <p>
	 * This is a trivial test.  Generate a bunch of random floating point values
	 * between 10 and 11, and train them with the label "positive".  Then, generate
	 * a bunch of values between -10 and -11, and train them with the label "negative".
	 * Then, generate a bunch of values between 5 and 6, and classify them.  The answer
	 * had better be "positive".  Then do the same for values between -5 and -6.  The
	 * answer had better be "negative".
	 * </p>
	 */
	public void testEuclideanMeanClassifierWithOneDimension() {
		Classifier c = new EuclideanMeanClassifier();
		c.startTraining();
		// Create a bunch of training data with each feature between 10 and 11, and
		// another bunch with each feature between -10 and -11.
		for (int i=0; i < aBunch; i++) {
			double[] data;
			data = new double[1];
			data[0] = 10.0 + rnd.nextDouble();
			c.train("positive", data);
			data = new double[1];
			data[0] = -10.0 - rnd.nextDouble();
			c.train("negative", data);
		}
		c.finishTraining();
		// Test a bunch of values between 5 and 6.
		for (int i=0; i < aBunch; i++) {
			double[] data = new double[1];
			data[0] = 5.0 + rnd.nextDouble();
			String category = c.classify(data);
			assertEquals("positive", category);
		}
		// Now, test a bunch of values between -5 and -6.
		for (int i=0; i < aBunch; i++) {
			double[] data = new double[1];
			data[0] = -5.0 - rnd.nextDouble();
			String category = c.classify(data);
			assertEquals("negative", category);
		}
		// If we got here, the classifier is working at least somewhat.
	}
	
	/**
	 * <p>
	 * Test our Euclidean mean classifier with four-dimensional data.
	 * </p><p>
	 * This is precisely like the one-dimensional test, except that the
	 * training and testing data is four-dimensional.  Each dimension is
	 * in the same range.  So, our training data is going to be inside a 
	 * pair of little tiny hypercubes, and our testing data is going to be inside
	 * another pair of little tiny hypercubes.
	 * </p><p>
	 * Coupled with the one-dimensional test, this verifies that our classifier
	 * does not care about the dimensionality of the data being fed to it.  All
	 * the data has to be of the same dimensionality, but we don't care if it's
	 * one-dimensional or nintey-three dimensional.  The pair of tests taken
	 * together should confirm that.
	 * </p>
	 */
	public void testEuclideanMeanClassifierWithFourDimensions() {
		Classifier c = new EuclideanMeanClassifier();
		c.startTraining();
		// Create a bunch of four-dimensional test cases where each coordinate is
		// betweeen 10 and 11 and another bunch between -10 and -11.
		for (int i=0; i < aBunch; i++) {
			double[] data;
			data = new double[4];
			for (int j=0; j < 4; j++) {
				data[j] = 10.0 + rnd.nextDouble();
			}
			c.train("positive", data);
			data = new double[4];
			for (int j=0; j < 4; j++) {
				data[j] = -10.0 - rnd.nextDouble();
			}
			c.train("negative", data);
		}
		c.finishTraining();
		// Test a bunch of data where each coodrinate is between 5 and 6.
		for (int i=0; i < aBunch; i++) {
			double[] data = new double[4];
			for (int j=0; j < 4; j++) {
				data[j] = 5.0 + rnd.nextDouble();
			}
			String category = c.classify(data);
			assertEquals("positive", category);
			for (int j=0; j < 4; j++) {
				data[j] = -5.0 - rnd.nextDouble();
			}
			category = c.classify(data);
			assertEquals("negative", category);
		}
		
	}
	
	protected void setUp() {
		// Change the following if we want to always use the same seed.
		rnd = new Random();
		if (headless) {
			System.setProperty("java.awt.headless", "true");
		}
	}

	public static void main(String[] args) {
		if (headless) {
			junit.textui.TestRunner.run(TestB4.class);
		} else {
			junit.swingui.TestRunner.run(TestB4.class);
		}
	}

}

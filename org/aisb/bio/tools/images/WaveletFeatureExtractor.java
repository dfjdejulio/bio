/*
 * Created on May 13, 2005 by Doug DeJulio
 */
package org.aisb.bio.tools.images;

import java.awt.image.Raster;

/**
 * <p>
 * Use wavelet transforms to generate an array of features for an image.
 * </p><p>
 * We use the command bean design pattern.  Put in the (raster from the) image and desired level
 * of decomposition.  Then call the "execute" method.  Then fetch an array of features the length
 * of which is equal to the decomposition level you specified.
 * </p><p>
 * Note that the wavelet transform I'm using can produce negative values at a given "pixel".
 * This may produce odd results.
 * </p><p>
 * Here are the nitty-gritty details.  First, we're going to take the image
 * along one direction and take all the rows or columns, treat them as a vector,
 * and feed them into a one-dimensional transform.
 * </p><p>
 * The result is going to be a matrix with the same "area" as the original matrix.
 * For convenience of calculation, we'll transpose its two dimensions.
 * </p><p>
 * Then, we're going to take the <em>output</em> from that step and process them along
 * the <em>opposite</em> direction (columns or rows) and feed <em>them</em> into a one-dimensional
 * transform.
 * </p><p>
 * I can't see how this can produce anything but the correct transformation with regard to
 * the low-pass filtered data, and the other methods I've thought of don't have that
 * property.  I'm just going to hope the same is also true of the high-pass filtered
 * data...
 * </p><p>
 * One assertion was that it's easy to take 1D wavelet transforms and apply them
 * to 2D data.  Another assertion was also that the process is completely reversable.
 * And another assertion was that the result would have a little "mini-image" consisting
 * of the original image basically at half resolution in one corner of the result.  This
 * method should have all three of these properties, and no other simple method I've come up with
 * does, so I think my hope is at least somewhat founded.
 * </p>
 * 
 * @author Doug DeJulio
 *
 */
public class WaveletFeatureExtractor {
	WaveletTransform wavelet = new WaveletTransform();
	Raster image;
	double[][] subImage;
	double[] features;
	int level;
	
	/**
	 * @return Returns the image.
	 */
	public Raster getImage() {
		return image;
	}
	/**
	 * @param image The image to set.
	 */
	public void setImage(Raster image) {
		this.image = image;
	}
	/**
	 * @return Returns the level.
	 */
	public int getLevel() {
		return level;
	}
	/**
	 * @param level The level to set.
	 */
	public void setLevel(int level) {
		this.level = level;
		features = new double[level];
	}
	/**
	 * @return Returns the features.
	 */
	public double[] getFeatures() {
		return features;
	}
	
	public void execute() {
		int width = image.getWidth();
		int height = image.getHeight();
		
		double[][] phaseOneOutput = new double[height][width];
		double[][] phaseTwoOutput = new double[width][height];
		double[] data;
		// For the quadrant averages...
		double[] averages = new double[4];
		// First, the columns.
		for (int x=0; x < width; x++) {
			// Get a column.
			data = image.getPixels(x, 0, 1, height-1, new double[height]);
			// Transfrom it.
			wavelet.daubTrans(data);
			// Store the result.
			for (int y=0; y < height; y++) {
				phaseOneOutput[y][x] = data[y];
			}
		}
		// Now, the rows.
		for (int y=0; y < height; y++) {
			// Get a row.
			data = phaseOneOutput[y];
			// Transform it.
			wavelet.daubTrans(data);
			// Store the result.
			for (int x=0; x < width; x++) {
				phaseTwoOutput[x][y] = data[x];
			}
		}
		// I *think* I've done the 2d wavelet transform now.
		
		// We should have four quadrants.  The "northwest" one should contain the
		// lower-fidelity image.  The other three should contain the high frequency data.
		// So the northwest one becomes our new image for further iterations, and the
		// other three get their intensity-sums divided by their areas to produce
		// average energy level.

		double feature = 0;
		subImage = new double[width/2][height/2];
		for (int x=0; x < width/2; x++) {
			for (int y=0; y < height/2; y++) {
				subImage[x][y] = phaseTwoOutput[x][y]; // Northwest
				feature += phaseTwoOutput[x+(width/2)][y]; // Northeast
				feature += phaseTwoOutput[x][y+(height/2)];  // Southwest
				feature += phaseTwoOutput[x+(width/2)][y+(height/2)]; // Southeast.
			}
		}
		feature /= (width*height/12.0);
		features[0] = feature;
		keepGettingFeatures(1);
	}
	
	private void keepGettingFeatures(int count) {
		// If we're done, don't do anything else.
		if (count >= level) {
			return;
		}
		double data[];
		int width = subImage.length;
		int height = subImage[0].length;
		// We're doing the same thing as above, but we're not working with a Raster anymore.
		double[][] firstPhaseOutput = new double[width][height];
		double[][] secondPhaseOutput = new double[height][width];
		double[][] thirdPhaseOutput = new double[width][height];
		double[][] newSubImage = new double[width/2][height/2];
		// Copy the array.
		// I thought java.util.Arrays would handle this, but I guess not...
		for (int x=0; x < width; x++) {
			for (int y=0; y < height; y++) {
				firstPhaseOutput[x][y] = subImage[x][y];
			}
		}

		// First the columns.
		for (int x=0; x < width; x++) {
			data = firstPhaseOutput[x];
			wavelet.daubTrans(data);
			for (int y=0; y < height; y++) {
				secondPhaseOutput[y][x] = data[y];
			}
		}
		for (int y=0; y < height; y++) {
			data = secondPhaseOutput[y];
			wavelet.daubTrans(data);
			for (int x=0; x < width; x++) {
				thirdPhaseOutput[x][y] += data[x];
			}
		}
		subImage = new double[width/2][height/2];
		double feature = 0;
		for (int x=0; x < width/2; x++) {
			for (int y=0; y < height/2; y++) {
				newSubImage[x][y] = thirdPhaseOutput[x][y];
				feature += thirdPhaseOutput[x+(width/2)][y];
				feature += thirdPhaseOutput[x][y+(height/2)];
				feature += thirdPhaseOutput[x+(width/2)][y+(height/2)];
			}
		}
		// Store the average energy level at this level of decomposition.
		feature /= (width * height/12.0);
		features[count] = feature;
		// Store the image to be used if we're going to do more levels of decomposition.
		subImage = newSubImage;
		// Do another level of deomposition if needed.
		keepGettingFeatures(count+1);
	}
}

/*
 * Created on Apr 28, 2005 by Doug DeJulio
 */
package org.aisb.bio.tools;

import org.aisb.bio.things.*;
import java.io.*;

/**
 * <p>
 * This class implements a fitness function based on a PSSM.
 * </p><p>
 * The PSSM must be in a plain text file formatted as discussed in assignment B3.
 * </p>
 * 
 * @author Doug DeJulio
 *
 */
public class PSSMFitnessFunction implements FitnessFunction {
	private int[][] pssm;
	private String[] nucleotides = { "A", "C", "G", "T" };
	
	/* (non-Javadoc)
	 * @see org.aisb.bio.tools.FitnessFunction#fitness(org.aisb.bio.things.Sequence)
	 */
	public int fitness(Sequence inputSequence) {
		int fitness = 0;
		Monomer[] sequence = (Monomer[]) inputSequence.toArray(new Monomer[0]);
		for (int i=0; i < sequence.length; i++) {
			// Get the location at this position for this nucleotide.
			fitness += pssm[i][sequence[i].getNumber()-1];
		}
		return fitness;
	}
	
	/**
	 * Calculate and return the maximum score.
	 * 
	 * @return The maximum score.
	 */
	public int getMaximumScore() {
		int sum=0;
		for (int i=0; i < pssm.length; i++) {
			int max=pssm[i][0];
			for (int j=1; j < 4; j++) {
				max = pssm[i][j] > max ? pssm[i][j] : max;
			}
			sum += max;
		}
		return sum;
	}

	/**
	 * Load the PSSM from a file.
	 * 
	 * @param pssmFileName The name of the PSSM file.
	 */
	public void loadFromFile(String pssmFileName) throws IOException {
		loadFromFile(new File(pssmFileName));
	}
	
	public void loadFromFile(File pssmFile) throws IOException {
		BufferedReader in;
		String line;
		String[] values;
		in = new BufferedReader(new FileReader(pssmFile));
		// There are four lines: A, C, G, and T.

		for (int j=0; j < 4; j++) {
			// Each line has the same number of elements.
			line = in.readLine();		// Read the current line.
			if (line == null) {
				throw new IllegalStateException("The PSSM file " + pssmFile + " didn't have a line for nucleotide '" + nucleotides[j] + "'.");
			}
			// Split the line on commas, with or without whitespace.  We should get
			// an array of string representations of the integers on the line.
			values = line.split("\\s*,\\s*");
			// Now we know how long the PSSM is.
			if (pssm == null) {
				pssm = new int[values.length][4];
			}
			for (int i=0; i < values.length; i++) {
				pssm[i][j] = Integer.parseInt(values[i]);
			}
		}
				
	}

}

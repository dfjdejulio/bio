/*
 * Created on Jan 31, 2005 by Doug DeJulio
 */
package org.aisb.bio.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.aisb.bio.things.*;

/**
 * This is a simple implementation of a similarity matrix for
 * comparing monomers.  Today, it only works for amino acids.
 * 
 * @author Doug DeJulio
 *
 */
public class SimilarityMatrixComparator extends MonomerComparator {
	Class type = AminoAcid.class;				// Default to working on amino acids for now.
	public int[][] value = new int[26][26];	// Another shortcut -- we know the similarity matrix will be 26x26.
	
	/**
	 * Initialize the matrix from a given file.
	 * 
	 * @param file The file from which to load.
	 */
	public void loadFromFile(File file) throws IOException {
		BufferedReader in;
		String line;
		in = new BufferedReader(new FileReader(file));

		int i=0;
		while ((line = in.readLine())!=null && (i < value.length)) {
			// The following should split the line up wherever there's any amount
			// (including none) of whitespace, a comma, and any amount (including none)
			// of whitespace.  So if we start with a comma-separated list of integers,
			// we should end with an array of the strings of those integers.
			String[] values = line.split("\\s*,\\s*");
			// Load up one row of the array.
			for (int j=0; j < values.length; j++) {
				value[i][j] = Integer.parseInt(values[j]);
			}
			// Get ready for the next row of the array.
			i++;
		}
	}
	
	/**
	 * For convenience, initialize the matrix from a file with the given name.
	 * 
	 * @param fileName The name of the file from which to load.
	 */
	public void loadFromFile(String fileName) throws IOException {
		loadFromFile(new File(fileName));
	}
	
	/* (non-Javadoc)
	 * @see org.aisb.bio.tools.MonomerComparator#compare(org.aisb.bio.things.Monomer, org.aisb.bio.things.Monomer)
	 */
	public int compare(Monomer m1, Monomer m2) {
		return value[m1.getNumber()-1][m2.getNumber()-1];
	}
	
}

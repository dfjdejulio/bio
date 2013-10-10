/*
 * Created on Feb 1, 2005 by Doug DeJulio
 */
package org.aisb.bio.tools;

import org.aisb.bio.things.Monomer;

/**
 * This implements the Needleman/Wunsch algorithm, with gap penalties,
 * including end gap penalties.  This version gets the gap penalties correct.
 * Another recent change is in the traceback matrix.  Now, instead of each
 * cell holding "0" or "1", it essentially holds the number of alignments known
 * as of that location in the matrix.
 * 
 * Known problems:
 * 
 * I did not have time to implement the options I wanted to
 * for printing out the sequences alignments.  They're all
 * calculated, in the "traceback" matrix.  Generating one
 * string from that matrix is easy (and done); generating the full
 * set turned out to take more time than I had allotted for
 * this task. 
 * 
 * @author Doug DeJulio
 *
 */
public class Needleman extends SequenceAligner {
	// We're going to set defaults for this, so it's easy to use this
	// class without writing much code.  When used in the simplest way,
	// it does little more than the equivalent of a dot matrix plot.
	int gapCreationPenalty = 0;			// Penalty for introducing a gap; default none.
	int gapExtensionPenalty = 0;			// Penalty for extending a gap; default none.
	// Similarity function to use; default is mere identity.
	MonomerComparator comparator = new IdentityComparator();
	
	// These are used in the calculations.
	int[][] matrix;	// The matrix of initial scores.
	int[][] finalScores; // The matrix of final scores.
	int[][] traceback; // A boolean matrix to calculate and hold the actual traceback.
	int score;		// The highest current score in the whole matrix.
	int numberOfAlignments;
	
	/**
	 * This constructor is a little cleaner to use outside of contexts in which
	 * the command bean is really useful.
	 * 
	 * @param comparator The comparison function to use.
	 * @param gapCreationPenalty The penalty for creating a new gap.
	 * @param gapExtensionPenalty The penalty for extending an existing gap.
	 */
	public Needleman(MonomerComparator comparator, int gapCreationPenalty, int gapExtensionPenalty) {
		this.gapCreationPenalty = gapCreationPenalty;
		this.gapExtensionPenalty = gapExtensionPenalty;
		this.comparator = comparator;
	}

	/* (non-Javadoc)
	 * @see org.aisb.bio.tools.SequenceAligner#execute()
	 */
	public void execute() {
		// Initialize the matrix.
		int firstSize, secondSize;
		firstSize = s1.size();
		secondSize = s2.size();
		
		// Let's see if we're just being silly...
		if (firstSize < 2 || secondSize < 2) {
			throw new IllegalArgumentException("Oh come on, do you really need a computer to align those sequences...?");
		}
		
		// We'll use multiple matrices, because it's easier to debug our work
		// if we don't throw away data.  A simple optimization would be to
		// just use one matrix.
		matrix = new int[firstSize][secondSize];
		finalScores = new int[firstSize][secondSize];
		traceback = new int[firstSize][secondSize];
		// Fill with comparison values.
		for (int i=0; i < firstSize; i++) {
			for (int j=0; j < secondSize; j++) {
				matrix[i][j] = comparator.compare((Monomer)s1.get(i),(Monomer)s2.get(j));
			}
		}
		// We've already got something interesting, and printing it out would let us
		// eyeball the final solution.  Now for the actual dynamic programming.

		// Uncomment one of the following to dump a matrix to stdout, for debugging.
		System.out.println("Original matrix:"); dumpMatrix(matrix);
		
		// The edges are pretty easy.
		for (int i=0; i < matrix.length; i++) {
			finalScores[i][0] = matrix[i][0] - gapPenalty(i);
		}
		for (int j=1; j < matrix[0].length; j++) {
			finalScores[0][j] = matrix[0][j] - gapPenalty(j);
		}
		// Now do the math for the interior spaces.
		for (int i=1; i < matrix.length; i++) {
			for (int j=1; j < matrix[0].length; j++) {
				int thisScore = calculateScoreAt(i,j);
				finalScores[i][j] = thisScore;
			}
		}
		// The penalties at the other end are handled by the doTracebackAt method.

		// Uncomment the following if you want more debugging.
		System.out.println("Final scores (except along lower/right edges):"); dumpMatrix(finalScores);
		// Now, do the traceback and find the best score.
		// There will always be at least one alignment.
		numberOfAlignments = 1;
		// If a traceback turns up a branch, it'll increase the number of alignments.
		score = doTracebackAt(29,29);
		
		// Uncomment this to see the traceback matrix.
		//dumpMatrix(traceback);
	}
	
	/**
	 * @return
	 */
	private int doTraceback() {
		int score;
		// First, do the actual traceback.
		score =  doTracebackAt(matrix.length-1, matrix[0].length-1);
if (false) {		
		int diag = matrix.length < matrix[0].length ? matrix.length : matrix[0].length;
		int count = 0;
		for (int slice = 0; slice < diag; slice++) {
			int localCount = 0;
			if (traceback[slice][slice] > 0) {
				traceback[slice][slice] += count + localCount;
				localCount++;
			}
			for (int i=slice+1; i < matrix.length; i++) {
				if (traceback[i][slice] > 0) {
					traceback[i][slice] += count + localCount;
					localCount++;
				}
			}
			for (int j=slice+1; j < matrix[0].length; j++) {
				if (traceback[slice][j] > 0) {
					traceback[slice][j] += count + localCount;
					localCount++;
				}
			}
			count += localCount - 1;
		}
}		
		return score;
	}

	/* (non-Javadoc)
	 * @see org.aisb.bio.tools.SequenceAligner#getNumberOfAlignments()
	 */
	public int getNumberOfAlignments() {
		return numberOfAlignments;
	}
	
	private int doTracebackAt(int firstPos, int secondPos) {
		// First, if we fell off the edge, give up.
		if (firstPos < 0 || secondPos < 0) {
			// The key thing here is that we're not recursing anymore.
			return 0;
		}
		// Start at the far corner.
		int bestScore = finalScores[firstPos][secondPos];
		int countOfBestScore = 0;	// How many times does it appear?
		// Can we do better along one edge?
		for (int i=0; i < firstPos; i++) {
			int thisScore = finalScores[i][secondPos] - gapPenalty(firstPos - i);
			if (thisScore > bestScore) {
				bestScore = thisScore;
			}
		}
		for (int j=0; j < secondPos; j++) {
			int thisScore = finalScores[firstPos][j] - gapPenalty(secondPos - j);
			if (thisScore > bestScore) {
				bestScore = thisScore;
			}
		}
		// We know the best score. It could happen more than once.
		// Mark that score, and at each location, continue the traceback.
		if (finalScores[firstPos][secondPos] == bestScore) {
			traceback[firstPos][secondPos] = 1;
			countOfBestScore++;
			doTracebackAt(firstPos-1, secondPos-1);
		}
		for (int i=0; i < firstPos; i++) {
			if (finalScores[i][secondPos] - gapPenalty(firstPos - i) == bestScore) {
				traceback[i][secondPos] = 1;
				countOfBestScore++;
				doTracebackAt(i-1, secondPos-1);
			}
		}
		for (int j=0; j < secondPos; j++) {
			if (finalScores[firstPos][j] - gapPenalty(secondPos - j) == bestScore) {
				traceback[firstPos][j] = 1 + countOfBestScore;
				countOfBestScore++;
				doTracebackAt(firstPos-1, j-1);
			}
		}
		// If we found more than one alignment at this level, that's a branch.
		// Each branch at any level increases the total number of alignments by one.
		numberOfAlignments += (countOfBestScore-1);
		
		// ...then return the best score at *this* level, 
		return bestScore;
	}
	
	private int calculateScoreAt(int firstPos, int secondPos) {
		// Seed with the one directly "diagonally above" us.
		int maximumSoFar = finalScores[firstPos-1][secondPos-1];
		// See if there's a higher value along one direction.
		if (firstPos > 1) {
			for (int i=0; i < firstPos-1; i++) {
				int gapLength = (firstPos - i) - 1;	// Calculate the gap length.
				// Figure out the score if this slot is used, including gap penalties.
				int thisValue = finalScores[i][secondPos-1] - gapPenalty(gapLength);
				// See if this is the best path we've found so far.
				maximumSoFar = max(maximumSoFar,thisValue);
			}
		}

		// See if there's a higher value along the other direction.
		if (secondPos > 1) {
			for (int j=0; j < secondPos-1; j++) {
				int gapLength = (secondPos - j) - 1;	// Calculate the gap length.
				// Figure out the score if this slot is used, including gap penalties.
				int thisValue = finalScores[firstPos-1][j] - gapPenalty(gapLength);
				// See if this is the best path we've found so far.
				maximumSoFar = max(maximumSoFar,thisValue);
			}
		}
		return matrix[firstPos][secondPos] + maximumSoFar;
	}
	
	// For convenience and readability.
	private int gapPenalty(int length) {
		if (length < 0) {
			throw new IllegalArgumentException("Negative length " + Integer.toString(length)  + " passed to gapPenalty(lenght).");
		}
		if (length == 0) {
			return 0;
		} else {
			return gapCreationPenalty + (length-1 * gapExtensionPenalty);
		}
	}
	
	// For convenience and readability.
	private int max(int i, int j) {
		return i>j?i:j;
	}
	
	// This is for debugging only.
	private void dumpMatrix(int[][] matrix) {
		//for (int i = 0; i < matrix.length; i++) {
		for (int i = 0; i < 29; i++) {
			//for (int j=0; j < matrix[0].length; j++) {
			for (int j=0; j < 29; j++) {
				//System.out.print("(" + ((Monomer)s1.get(i)).getCode() + "," + ((Monomer)s2.get(j)).getCode() + "," + matrix[i][j] + ")");
				System.out.print("\t" + matrix[i][j]);
				//System.out.print(matrix[i][j]);
			}
			System.out.println();
		}
	}

	/**
	 * Set the comparator to use to match each pair of elements.
	 * 
	 * @return Returns the comparator.
	 */
	public MonomerComparator getComparator() {
		return comparator;
	}
	/**
	 * Returns the comparator currently in use.
	 * 
	 * @param comparator The comparator to set.
	 */
	public void setComparator(MonomerComparator comparator) {
		this.comparator = comparator;
	}
	/**
	 * @return Returns the gapCreationPenalty.
	 */
	public int getGapCreationPenalty() {
		return gapCreationPenalty;
	}
	/**
	 * @param gapCreationPenalty The gapCreationPenalty to set.
	 */
	public void setGapCreationPenalty(int gapCreationPenalty) {
		this.gapCreationPenalty = gapCreationPenalty;
	}
	/**
	 * @return Returns the gapExtensionPenalty.
	 */
	public int getGapExtensionPenalty() {
		return gapExtensionPenalty;
	}
	/**
	 * @param gapExtensionPenalty The gapExtensionPenalty to set.
	 */
	public void setGapExtensionPenalty(int gapExtensionPenalty) {
		this.gapExtensionPenalty = gapExtensionPenalty;
	}
	/* (non-Javadoc)
	 * @see org.aisb.bio.tools.SequenceAligner#getBestAlignmentScore()
	 */
	public int getBestAlignmentScore() {
		return score;
	}

	/* (non-Javadoc)
	 * @see org.aisb.bio.tools.SequenceAligner#getBestAlignmentAsString()
	 */
	public String getBestAlignmentAsString() {
		return getAlignmentAsString(1);
	}
	
	public String getAlignmentAsString(int n) {
		// Find one, any one.
		String sequenceLineOne = new String();
		String sequenceLineTwo = new String();

		int i=0, j=0, oldi=0, oldj=0;
		outerLoop: while (i < matrix.length && j < matrix[0].length) {
			// Is there a match at this spot?
			if (traceback[i][j] >= n) {
				sequenceLineOne = sequenceLineOne + ((Monomer)s1.get(i)).getCode();
				sequenceLineTwo = sequenceLineTwo + ((Monomer)s2.get(j)).getCode();
				// Now go "southeast" by one.
				i++;
				j++;
				oldi = i;
				oldj = j;
				continue outerLoop;
			}
			// There's a gap.  Is there a match in sequence 1?
			i = oldi;
			for (j = oldj; j < matrix[0].length; j++) {
				if (traceback[i][j] >= n) {
					// A match!  Fill in the blanks...
					for (int count = 0; count < j - oldj; count++) {
						sequenceLineOne = sequenceLineOne + "-";
						sequenceLineTwo = sequenceLineTwo + ((Monomer)s2.get(oldj + count)).getCode();
					}
					oldi = i;
					oldj = j;
					continue outerLoop;
				}
			}
			// There's no match in sequence 1.  How about 2?
			j = oldj;
			for (i = oldi; i < matrix.length; i++) {
				if (traceback[i][j] >= n) {
					// A match!  Fill in the blanks...
					for (int count = 0; count < i - oldi; count++) {
						sequenceLineOne = sequenceLineOne + ((Monomer)s1.get(oldi + count)).getCode();
						sequenceLineTwo = sequenceLineTwo + "-";
					}
					oldi = i;
					oldj = j;
					continue outerLoop;
				}
			}

		}
		
		// Exactly one sequence had to finish.  The other may not have.
		// Did sequence 1 finish?  If not...
		if (i < matrix.length) {
			for (i = oldi; i < matrix.length; i++) {
				sequenceLineOne = sequenceLineOne + ((Monomer)s1.get(i)).getCode();
				sequenceLineTwo = sequenceLineTwo + "-";
			}
		}
		// Did sequence 2 finish?  If not...
		if (j < matrix[0].length) {
			for (j = oldj; j < matrix[0].length; j++) {
				sequenceLineOne = sequenceLineOne + "-";
				sequenceLineTwo = sequenceLineTwo + ((Monomer)s2.get(j)).getCode();
			}
		}
		
		return sequenceLineOne + "\n" + sequenceLineTwo + "\n";
	}
	
	/* (non-Javadoc)
	 * @see org.aisb.bio.tools.SequenceAligner#getBestAlignmentsAsStrings()
	 */
	public String[] getBestAlignmentsAsStrings() {
		String[] alignments = new String[numberOfAlignments];
		for (int i=0; i < numberOfAlignments; i++) {
			alignments[i] = this.getAlignmentAsString(i+1);
		}
		return alignments;
	}
	
	/* (non-Javadoc)
	 * @see org.aisb.bio.tools.SequenceAligner#getBestAlignmentsAsStrings()
	 */
	public Character[][] getBestAlignmentsAsMatrix() {
		Character yes = new Character('*');	// If there's a match, put a star.
		Character no = new Character('+');	// If no match, put a '+', to help people trace lines.
		// Make a matrix one bigger in each dimension than our traceback matrix.
		//int s1size=s1.size(); int s2size=s2.size();
		int s1size=30; int s2size=30;
		Character[][] alignmentMatrix = new Character[s1size+1][s2size+1];
		// Label the edges.
		alignmentMatrix[0][0] = new Character(' ');
		for (int i=1; i <= s1size; i++) {
			alignmentMatrix[i][0] = ((Monomer)s1.get(i-1)).getCode();
		}
		for (int j=1; j <= s2size; j++) {
			alignmentMatrix[0][j] = ((Monomer)s2.get(j-1)).getCode();
		}
		// Now, fill in the matrix so it's visually useful.
		for (int i=0; i < s1size; i++) {
			for (int j=0; j < s2size; j++) {
				if (traceback[i][j] > 0) {
					alignmentMatrix[i+1][j+1] = new Character((char) (traceback[i][j] + '0'));
				} else {
					alignmentMatrix[i+1][j+1] = no;
				}
			}
		}
		return alignmentMatrix;
	}

}

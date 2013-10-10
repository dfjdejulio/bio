/*
 * Created on Feb 1, 2005 by Doug DeJulio
 */
package org.aisb.bio.tools;

import org.aisb.bio.things.*;

/**
 * This interface describes any algorithm for doing pair-wise alignment
 * of sequences.  It's implemented via the command bean design pattern.
 * This makes it simple to use from a variety of environments, such as
 * JSP pages using JSTL or the JSP2 expression language.
 * 
 * @author Doug DeJulio
 *
 */
public abstract class SequenceAligner {
	Sequence s1;
	Sequence s2;

	/**
	 * Set the first sequence to align.
	 * 
	 * @param s1 The first sequence.
	 */
	public void setFirstSequence(Sequence s1) {
		this.s1 = s1;
	}
	
	/**
	 * Get the first sequence to align.
	 * 
	 * @return The first sequence.
	 */
	public Sequence getFirstSequence() {
		return s1;
	}
	
	/**
	 * Set the second sequence to align.
	 * 
	 * @param s2 The second sequence.
	 */
	public void setSecondSequence(Sequence s2) {
		this.s2 = s2;
	}

	/**
	 * Get the second sequence to align.
	 * 
	 * @return The second sequence.
	 */
	public Sequence getSecondSequence() {
		return s2;
	}
	
	/**
	 * Get the best alignment score, as defined by this algorithm.
	 * 
	 * @return The score.
	 */
	public abstract int getBestAlignmentScore();
	
	/**
	 * Get the number of alignments that have the best score.
	 * 
	 * @return The number of alignments.
	 */
	public abstract int getNumberOfAlignments();
	
	/**
	 * Get the actual best alignment, in the form of a string consisting of
	 * tuples representing positions in the alignment.
	 * 
	 * @return The best alignment.
	 */
	public abstract String getBestAlignmentAsString();
	
	/**
	 * Get the set of best alignments, in the form of an array of strings
	 * consisting of tuples representing positions in the alignment.
	 * 
	 * @return The best alignments.
	 */
	public abstract String[] getBestAlignmentsAsStrings();
	
	/**
	 * Get the set of best alignments, in the form of a matrix.
	 * 
	 * @return The matrix showing the alignments.
	 */
	public abstract Character[][] getBestAlignmentsAsMatrix();
	
	/**
	 * Perform the operation.
	 */
	public abstract void execute();
}

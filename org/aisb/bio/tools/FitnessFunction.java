/*
 * Created on Apr 28, 2005 by Doug DeJulio
 */
package org.aisb.bio.tools;

import org.aisb.bio.things.Sequence;

/**
 * This interface defines the invocation of a fitness function.  It does
 * not assume any particular implementation.
 * 
 * @author Doug DeJulio
 *
 */
public interface FitnessFunction {
	
	/**
	 * Calculate the fitness of this sequence.
	 * 
	 * @param seq The sequence under consideration.
	 * @return An integer representing the fitness.
	 */
	public int fitness(Sequence seq);
}

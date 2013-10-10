/*
 * Created on May 13, 2005 by Doug DeJulio
 */
package org.aisb.bio.things.evolution;

import java.util.Random;

import org.aisb.bio.things.*;

/**
 * <p>
 * Turn one nucleotide into another, maybe.
 * </p><p>
 * This implements a very simple Jukes-Cantor model of mutation.  Configure it with a
 * mutation rate.  Feed in a nucleotide.  If there's no mutation, you'll get the same
 * nucleotide out.  If there's a mutaiton, you'll get a different nucleotide, with each
 * possibility having equal odds.
 * </p>
 * 
 * @author Doug DeJulio
 *
 */
public class Mutator {
	private double mu;	// Chance of mutation.
	Random rng = new Random(0); // Use a constant seed so we always get the same sequence.
	// Random rnd = new Random();	// Dont' use a constnat seed, so it's not predictable.
	
	public Mutator(double probability) {
		super();
		mu = probability;
	}
	
	/**
	 * Perform the mutation.
	 * 
	 * @param oldNucleotide The nucleotide to (perhaps) be mutated.
	 * @return The resulting nucleotide.
	 */
	public Monomer mutate(Monomer oldNucleotide) {
		Monomer newNucleotide = null;
		// Get the current nucleotide's number, (A=1, et cetera).
		int number = oldNucleotide.getNumber();
		// Subtract one from it.  We have a number from 0 to 3 now.
		number--;
		// Why?  We're going to add 0, 1, 2, or 3 to it and then do modulo arithmetic.
		double dieRoll = rng.nextDouble();
		// If we're above rnd, do nothing.
		// If we're below rnd, add one.
		if (dieRoll < mu) {
			number++;
		}
		// If we're below 2/3 rnd, add another one (two total).
		if (dieRoll < (mu*2/3)) {
			number++;
		}
		// If we're below 1/3 rnd, add another one (three total).
		if (dieRoll < (mu*1/3)) {
			number++;
		}
		// Take the mod 4 value, to turn that into a rotate.
		number = number % 4;
		// Add one to turn it back into a nucleotide number.
		number++;
		// Fetch the nucleotide!
		newNucleotide = Monomer.getByNumber(number);
		return newNucleotide;
	}
	
	/**
	 * @return Returns the mutation probability.
	 */
	public double getMu() {
		return mu;
	}
	/**
	 * @param mu The mutation probability to set.
	 */
	public void setMu(double mu) {
		this.mu = mu;
	}
}

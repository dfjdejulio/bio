/*
 * Created on Apr 28, 2005 by Doug DeJulio
 */
package org.aisb.bio.things.evolution;

import org.aisb.bio.things.*;

import java.io.*;

/**
 * <p>
 * This is an "organism" for the purpose of running evolutionary experiments.
 * There's a {@link org.aisb.bio.things.Sequence} representing the "genotype",
 * and a unitless floating point number representing how much time until the
 * organisim reproduces.  There's also an integer value representing "fitness".
 * </p><p>
 * For convenience, there's also a floating point number representing "birthday",
 * so we can fit the organism's entire lifespan into a timeline if we wish.
 * </p><p>
 * There's also a "parent" of type {@link org.aisb.bio.things.evolution.AsexualOrganism}, so
 * we can trace the organism's family tree if we wish.
 * </p>
 *
 * @author Doug DeJulio
 *
 */
public class AsexualOrganism {
	double timeToReproduce = 0;
	double birthday = 0;
	int fitness = 0;
	Sequence genotype = null;
	Mutator mutator;
	AsexualOrganism parent = null;

	/**
	 * @return Returns the birthday.
	 */
	public double getBirthday() {
		return birthday;
	}
	/**
	 * @param birthday The birthday to set.
	 */
	public void setBirthday(double birthday) {
		this.birthday = birthday;
	}
	/**
	 * @return Returns the fitness.
	 */
	public int getFitness() {
		return fitness;
	}
	/**
	 * @param fitness The fitness to set.
	 */
	public void setFitness(int fitness) {
		this.fitness = fitness;
	}
	/**
	 * @return Returns the genotype.
	 */
	public Sequence getGenotype() {
		return genotype;
	}
	/**
	 * @param genotype The genotype to set.
	 */
	public void setGenotype(Sequence genotype) {
		this.genotype = genotype;
	}
	/**
	 * @return Returns the timeToReproduce.
	 */
	public double getTimeToReproduce() {
		return timeToReproduce;
	}
	/**
	 * @param timeToReproduce The timeToReproduce to set.
	 */
	public void setTimeToReproduce(double timeToReproduce) {
		this.timeToReproduce = timeToReproduce;
	}
	/**
	 * @return Returns the parent.
	 */
	public AsexualOrganism getParent() {
		return parent;
	}
	/**
	 * @param parent The parent to set.
	 */
	public void setParent(AsexualOrganism parent) {
		this.parent = parent;
	}
	
	/**
	 * <p>
	 * Create a new {@link AsexualOrganism} with a specified birthday.  The offspring
	 * will be mutated as per the mutator assigned to the parent, and will inherit that
	 * mutator.
	 * </p><p>
	 * To simulate the budding of an organism, keep the parent around and call this method once.
	 * To simulate cell division, call this method twice on the parent and then throw the parent away.
	 * </p>
	 * 
	 * @param now The offspring's "birthday".
	 * @return The child organism.
	 */
	public AsexualOrganism haveMutantBaby(double now) {
		// Make a new baby.
		AsexualOrganism baby = new AsexualOrganism();
		// This is the parent.
		baby.setParent(this);
		// Birthday is now.
		baby.setBirthday(now);
		// Use the same mutator.
		baby.setMutator(this.mutator);
		// Start with parent's genotype.
		Monomer[] babyGeneArray = (Monomer[]) genotype.toArray(new Monomer[0]);
		Sequence babyGene = new Sequence(Nucleotide.class);
		for (int i=0; i < babyGeneArray.length; i++) {
			// Mutate!
			babyGene.add(mutator.mutate(babyGeneArray[i]));
		}
		baby.setGenotype(babyGene);
		return baby;
	}
	
	/**
	 * Load the organism's gene sequence from a file.
	 * 
	 * @param file A file containing exactly one line consisting only of nucleotide code letters.
	 * @throws IOException
	 */
	public void loadFromFile(File file) throws IOException {
		BufferedReader in;
		String line;
		in = new BufferedReader(new FileReader(file));
		line = in.readLine();
		genotype = new Sequence(Nucleotide.class, line.toUpperCase());
	}
	
	/**
	 * Load the organism's gene sequence from a file.
	 * 
	 * @param fileName The name of a file containing exactly one line consisting only of nucleotide code letters.
	 * @throws IOException
	 */
	public void loadFromFile(String fileName) throws IOException {
		loadFromFile(new File(fileName));
	}
	/**
	 * @return Returns the mutator.
	 */
	public Mutator getMutator() {
		return mutator;
	}
	/**
	 * @param mutator The mutator to use.
	 */
	public void setMutator(Mutator mutator) {
		this.mutator = mutator;
	}
	
	public String toString() {
		String value = super.toString();
		value = "[" + value + " : " + this.getGenotype() + "]";
		return value;
		
	}
	
}

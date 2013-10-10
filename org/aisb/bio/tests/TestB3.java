/*
 * Created on May 12, 2005 by Doug DeJulio
 */
package org.aisb.bio.tests;

import java.io.IOException;

import org.aisb.bio.tools.*;
import org.aisb.bio.things.*;
import org.aisb.bio.things.evolution.AsexualOrganism;
import org.aisb.bio.things.evolution.Mutator;

import junit.framework.TestCase;

/**
 * This is a set of unit tests for assignment B3.  It can be used as a JUnit TestCase,
 * or run from the command line.  When it's run from the command line, every test in it
 * will be executed exactly once.  See the documentation for the individual test methods
 * to see all the tests.
 * 
 * @author Doug DeJulio
 *
 */
public class TestB3 extends TestCase {
	static boolean initialized = false;
	static PSSMFitnessFunction fitness;
	
	/**
	 * <p>
	 * Test the creation of a simple nucleotide sequence from a string.
	 * </p><p>
	 * We create a sequence from the string "GATTACA".  Then we fetch
	 * the first three nucleotides and test to make sure they're really
	 * "G", "A", and "T".  This tests the sequence-building code.
	 * </p>
	 */
	public void testCreateASequence() {
		Sequence testSequence = new Sequence(Nucleotide.class, "GATTACA");
		assertSame(Monomer.getByCode('G'), testSequence.get(0));
		assertSame(Monomer.getByCode('A'), testSequence.get(1));
		assertSame(Monomer.getByCode('T'), testSequence.get(2));
		// That's enough...
	}
	
	/**
	 * <p>
	 * Calculate the fitness of a couple of fixed sequences for which the
	 * fitness is already known.
	 * </p><p>
	 * We've calculated the fitness of a sequence of all "A" and of all "G"
	 * ahead of time.  We construct these sequences and calculate their fitness.
	 * This tests the PSSM fitness function code.
	 * </p>
	 */
	public void testSomeKnownSequences() {
		Sequence a = new Sequence(Nucleotide.class, "AAAAAAAAAAAAAAAA");
		// With the given PSSM, that should have a fitness of 96.
		assertEquals(96, fitness.fitness(a));
		Sequence g = new Sequence(Nucleotide.class, "GGGGGGGGGGGGGGGG");
		assertEquals(64, fitness.fitness(g));
		// That'll do.
	}
	
	/**
	 * <p>
	 * Test loading an AsexualOrganism from a simple text file.
	 * </p><p>
	 * We've got a sample organism file in "data/test_organism.txt".  We know
	 * its nucleotide sequence starts with "GAT".  We load the organism from the
	 * file, fetch the sequence from the organism, and fetch the first three nucleotides
	 * from the sequence.  This tests the organism data model, the file I/O, and the
	 * sequence and nucleotide fetching functionalities.
	 * 
	 * @throws IOException
	 */
	public void testLoadingAnOrganism() throws IOException {
		AsexualOrganism moe = new AsexualOrganism();
		// Let's load up Moe's genotype.
		moe.loadFromFile("data/test_organism.txt");
		Sequence moeGene = moe.getGenotype();
		// Moe's DNA starts with "GATTACA".
		assertSame(Monomer.getByCode('G'), moeGene.get(0));
		assertSame(Monomer.getByCode('A'), moeGene.get(1));
		assertSame(Monomer.getByCode('T'), moeGene.get(2));
		// That'll do.
	}
	
	/**
	 * <p>
	 * Test that our mutation function is actually mutating.
	 * </p><p>
	 * We start with the nucleotide 'A'.  Then we configure our Mutator so
	 * that it has a probability of 1 of causting mutation.  Then we run it
	 * on 'A' 100 times, making sure we never get an 'A' out.  This is a crude
	 * test of our mutator function.  By single-stepping through the process with
	 * a debugger, we can do a fine-grained test of it.  We can't code up a complete
	 * test of it with certain results, since it has a random element and we will want to stop using a
	 * fixed random seed at some point.
	 * </p>
	 */
	public void testMutationFunction() {
		// Pick a mutation probability of 1 -- always mutate.
		Mutator m = new Mutator(1);
		Monomer oldNucleotide = Monomer.getByCode('A');
		// Do this 100 times.
		for (int i=0; i < 100; i++) {
			Monomer newNucleotide = m.mutate(oldNucleotide);
			// Since the mutation probability is 1, it will always mutate, so...
			assertNotSame(oldNucleotide,newNucleotide);
		}
		// Now we know that after 100 trials, it never crashed and always mutated.
	}
	
	/**
	 * <p>
	 * Test that our PSSM object can really calculate the maximum score of a PSSM.
	 * </p><p>
	 * We've calcualted the maximum score from our test PSSM file elsewhere.  We have
	 * our code run this calculation here and compare it to the known result.  This tests
	 * our PSSM data object and its ability to calculate the maximum score.
	 * </p>
	 */
	public void testMaxScore() {
		int score = fitness.getMaximumScore();
		// Compare with known maximum score for this PSSM.
		assertEquals(170, score);
}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws IOException {
		if (!initialized) {
			DarwinsCrucible.initialize();
			fitness = new PSSMFitnessFunction();
			fitness.loadFromFile("data/ProbB3ExamplePSSM.txt");
			initialized = true;
		}
	}

	/**
	 * Run the tests from the command line.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		//junit.textui.TestRunner.run(TestB3.class);
		junit.swingui.TestRunner.run(TestB3.class);
	}

}

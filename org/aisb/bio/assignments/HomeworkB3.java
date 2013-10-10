/*
 * Created on Apr 27, 2005 by Doug DeJulio
 */
package org.aisb.bio.assignments;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.aisb.bio.things.Sequence;
import org.aisb.bio.things.evolution.AsexualOrganism;
import org.aisb.bio.things.evolution.Mutator;
import org.aisb.bio.tools.DarwinsCrucible;
import org.aisb.bio.tools.PSSMFitnessFunction;

/**
 * <p>
 * This implements the exact behavior called for by assignment B3,
 * "Simulating Promoter Mutation and Selection".
 * </p><p>
 * The individual organisms are represented by {@link org.aisb.bio.things.evolution.AsexualOrganism} objects.
 * The mutation model is captured in the {@link org.aisb.bio.things.evolution.Mutator} object.
 * The interface for fitness functions in general is defined by {@link org.aisb.bio.tools.FitnessFunction},
 * and the specific implementation via a PSSM is {@link org.aisb.bio.tools.PSSMFitnessFunction}.
 * </p><p>
 * This program is meant to be invoked by an accompanying shell script.  That shell
 * script will only work when run from the directory it's distributed in, and only
 * if gnuplot is in the user's current path.
 * </p>
 * 
 * @author Doug DeJulio
 *
 */
public class HomeworkB3 {
	static PrintStream out = System.out;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	static AsexualOrganism eve;
	static Set future = new HashSet();
	static Set current = new HashSet();
	static Set past = new HashSet();
	static double now = 0;
	static double lowestTimeToNextDivision = 0;
	static int maxScore = 0;

	public static void main(String[] args) throws Exception {
		// Initialize.
		DarwinsCrucible.initialize();
		
		// Read a sequence from a text file.
		out.print("Enter the name of a file containing a sequence: ");
		String sequenceFileName = in.readLine();
		//String sequenceFileName="data/test_organism.txt"; out.println(sequenceFileName);
		
		// Turn that into an organism.
		eve = new AsexualOrganism();
		eve.loadFromFile(sequenceFileName);
		
		// Read a PSSM from a text file.
		out.print("Enter the name of a file containing a PSSM: ");
		String pssmFileName = in.readLine();
		//String pssmFileName = "data/ProbB3ExamplePSSM.txt"; out.println(pssmFileName);
		PSSMFitnessFunction pssm = new PSSMFitnessFunction();
		pssm.loadFromFile(pssmFileName);
		maxScore = pssm.getMaximumScore();
		
		// Query the user for the initial number of organisms to be used.
		out.print("How many organisms shall we start with? ");
		String numberOfOrganisms = in.readLine();
		int startingOrganisms = Integer.parseInt(numberOfOrganisms);
		//int startingOrganisms = 10; out.println(startingOrganisms);
		// This is "N0".
		
		// Query the user for the number of organisms after which to stop.
		out.print("We should stop after we have how many organisms? ");
		String finalNumberOfOrganisms = in.readLine();
		int finalOrganisms = Integer.parseInt(finalNumberOfOrganisms);
		//int finalOrganisms = 10000; out.println(finalOrganisms);
		// This is "Nmax".
		
		// Query the user for the mutation frequency.
		out.print("What's the mutation frequency? ");
		String mutationFrequency = in.readLine();
		double mu = Double.parseDouble(mutationFrequency);
		//double mu = 0.1; out.println(mu);
		// Create a Mutator using that frequency.
		Mutator m = new Mutator(mu);
		// Tell Eve to use it.
		eve.setMutator(m);
		// Have to calculate her fitness, even though we won't use it.
		eve.setFitness(pssm.fitness(eve.getGenotype()));

		/*
		 * Create a structure to hold the "current sequence", the "current promotor strength",
		 * and the "time to next division" for a bunch of cells and initialize it to start with
		 * the quantity that the user selected.
		 */
		// Seed the first generation.
		lowestTimeToNextDivision = maxScore; // Theoretical maximum.
		for (int i=0; i < startingOrganisms; i++) {
			AsexualOrganism cain = eve.haveMutantBaby(now);
			Sequence g = cain.getGenotype();
			int f = pssm.fitness(g);
			cain.setFitness(f);
			double timeToReproduce = (double)maxScore / (double)f;
			cain.setTimeToReproduce(timeToReproduce);
			lowestTimeToNextDivision = Math.min(lowestTimeToNextDivision, timeToReproduce);
			cain.setBirthday(now);
			future.add(cain);
		}
		// Now the data structure "future" holds "N0" cells.
		// The data structure "past" is empty right now.

		// Keep going until we've hit our target population size.
		while (past.size() + future.size() < finalOrganisms) {
			if ((past.size() + future.size()) % 100 == 0) {
				// Print a little summary every 100 organisms.
				out.println("Time: " + now + " organisms: " + (past.size() + future.size()));
			}
			now += lowestTimeToNextDivision;
			Iterator i;
			// Since we're removing things, have to clone the set first.
			i = new HashSet(future).iterator();
			while (i.hasNext()) {
				// Get an organism.
				AsexualOrganism pat = (AsexualOrganism) i.next();
				// Fast forward by the minimum time interval.
				double t = pat.getTimeToReproduce();
				t -= lowestTimeToNextDivision;
				pat.setTimeToReproduce(t);
				// Is it time for this thing to reproduce?
				if (t == 0) {
					// If so, remove it from the "future" set and put it into the "current" set.
					future.remove(pat);
					current.add(pat);
				}
			}
			// So, now all organisms have jumped to the future and
			// the "current" set includes all those that are breeding right now.
			// Since we're removing things, have to clone the set first.
			i = new HashSet(current).iterator();
			while (i.hasNext()) {
				// Get a breeding organism.
				AsexualOrganism mom = (AsexualOrganism) i.next();
				// Produce first offspring.
				AsexualOrganism cain = mom.haveMutantBaby(now);
				Sequence s = cain.getGenotype();
				int f = pssm.fitness(s);
				cain.setFitness(f);
				double timeToReproduce = (double)maxScore / (double)f;
				cain.setTimeToReproduce(timeToReproduce);
				future.add(cain);
				// Produce second offspring.
				AsexualOrganism abel = mom.haveMutantBaby(now);
				s = abel.getGenotype();
				f = pssm.fitness(s);
				abel.setFitness(f);
				timeToReproduce = (double)maxScore / (double)f;
				abel.setTimeToReproduce(timeToReproduce);
				future.add(abel);
				// Remove the non-muated parent.  Put it in our "archival" set.
				current.remove(mom);
				past.add(mom);
			}
			// We've done one breeding cycle.  Figure out when the next is.
			i = future.iterator();
			lowestTimeToNextDivision = maxScore; // Theoretical maximum if actual score is just "1".
			while (i.hasNext()) {
				AsexualOrganism pat = (AsexualOrganism) i.next();
				double t = pat.getTimeToReproduce();
				lowestTimeToNextDivision = Math.min(lowestTimeToNextDivision, t);
			}
			// We know when the next breeding cycle is.
		}
		
		// Stop the breeding!  I feel like a Republican...
		// Now "archive" everyone who hasn't bred yet.
		past.addAll(future);
		// Be aware: we may have overshot "Nmax".  We allow the last breeding cycle
		// to continue even if more than one organism breeds at that time.  This prevents
		// the implementation from introducing a bias.
		
		// Now, spit out the output for gnuplot or something.
		// We're going to create a file called "tmp/data.csv".
		PrintStream data = new PrintStream(new FileOutputStream("tmp/data.csv"));
		Iterator i = past.iterator();
		int initialPromotorStrength, maxPromotorStrength, minPromotorStrength;
		initialPromotorStrength = eve.getFitness();
		maxPromotorStrength = minPromotorStrength = initialPromotorStrength;
		while (i.hasNext()) {
			AsexualOrganism pat = (AsexualOrganism) i.next();
			data.print(pat.getBirthday());
			data.print(", ");
			int f = pat.getFitness(); 
			data.println(f);
			minPromotorStrength = Math.min(minPromotorStrength, f);
			maxPromotorStrength = Math.max(maxPromotorStrength, f);
		}
		out.println("Promotor strength: ");
		out.println("  initial: " + initialPromotorStrength + ", max: " + maxPromotorStrength + ", min: " + minPromotorStrength);
	}
}

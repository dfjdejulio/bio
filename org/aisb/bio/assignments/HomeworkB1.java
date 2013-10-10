/*
 * Created on Jan 29, 2005
 */
package org.aisb.bio.assignments;

import org.aisb.bio.tools.*;
import org.aisb.bio.things.*;

/**
 *
 * This class implements the exact behavior called for in assignment one.
 * Each assignment will get its own class of this sort, so they can all be
 * contained within the same JAR file.  This makes it much easier to manage
 * the source code and reuse useful code.  At least, that's the intent.
 * 
 * @author Doug DeJulio
 */
public class HomeworkB1 {
	static java.io.PrintStream out = System.out;
	static java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
	
	public static void main(String[] args) throws Exception {
		// First, initialize the framework.
		DynamicDuo.initialize();
		
		/*
		 * Requirement 1: asks the user for Genbank identifiers (gi) for two proteins
		 * of interest.
		 */
		
		// Prompt for two proteins of interest.
		out.print("Enter a genbank ID (only type the digits): ");
		//String firstGi = in.readLine();
		// During development, comment out the two lines above and
		// uncomment the following one; this is alpha 1 globin from Homo Sapiens.
		//String firstGi = "4504347";
		out.print("Enter another genbank ID: ");
		//String secondGi = in.readLine();
		// During development, comment out the two lines above and
		// uncomment the following one; this is beta globin from Homo Sapiens.
		//String secondGi = "4504349";
		// Another pair you can use for debugging.
		String firstGi = "7706511"; String secondGi = "7706519"; 
		
		/*
		 * Requirement 4: Asks the user for the name of a similarity matrix file.
		 */
		
		// Prompt for a similarity matrix.
		out.print("Enter the name of a similarity matrix file: ");
		//String similarityFileName = in.readLine();
		// During development, comment out the two lines above and
		// uncomment the following one; this is the PAM250 matrix.
		out.println("data/PAM250.csv"); String similarityFileName = "data/PAM250.csv";
		
		/*
		 * Requirement 6: Asks the user for a gap opening penalty
		 * and a gap extension penalty.
		 */
		
		// Gap penalties.
		out.print("Enter the penalty for creating a gap (positive integer): ");
		//int gapPenalty = Integer.parseInt(in.readLine());
		out.print("Enter the penalty for extending an existing gap's length by one (positive integer): ");
		//int gapExtensionPenalty = Integer.parseInt(in.readLine());
		// During development, comment out the four lines above
		// and uncomment the following two.  Gap penalty is 9+length.
		//int gapPenalty = 10;
		//int gapExtensionPenalty = 1;
		// Another pair you can use for testing.
		int gapPenalty = 15, gapExtensionPenalty = 2;
		
		/*
		 * Requirement 5: Reads the similarity matrix.  The files PAM250.csv
		 * and identity_matrix.csv in the "data" directory were both used to
		 * test this.
		 */
		
		// Initialize the similarity matrix comparator.
		SimilarityMatrixComparator comparator = new SimilarityMatrixComparator();
		comparator.loadFromFile(similarityFileName);
		
		/*
		 * Requirement 2: Downloads the nucleotide sequences in the format of our choice.
		 * Note that the rest of the assignment referred to amino acids, so I assumed we
		 * were actually to download the protein sequences.
		 * 
		 * I used FASTA format.  The heart of this is implemented in
		 * org.aisb.bio.tools.SequenceFetcher.
		 */
		
		// Get the fetcher ready to use.
		SequenceFetcher fetcher = new SequenceFetcher();
		
		// Fetch the first sequence.
		fetcher.setGenbankIdentifier(firstGi);
		out.println("Fetching: " + fetcher.getName());
		fetcher.execute();
		Sequence firstSequence = fetcher.getSequence();
		// Fetch the second sequence.
		fetcher.setGenbankIdentifier(secondGi);
		out.println("Fetching: " + fetcher.getName());
		fetcher.execute();
		Sequence secondSequence = fetcher.getSequence();
		
		/*
		 * Requirement 3: Reads the sequences into internal character strings and prints
		 * the total number of amino acids in each.
		 * 
		 * My internal representation isn't as character strings (see the "Monomer", "AminoAcid",
		 * and "Sequence" classes for deep details), but is easily convertable to and from such.
		 * The "SequenceFetcher" object above has already created "Sequence" objects containing
		 * an ordered list of "AminoAcid" objects.  (They're actually references to objects; only
		 * one object is created for each possible amino acid, so we're not doing a bajillioin object
		 * creations.)
		 * 
		 * Anyhow.  Here's a printout of the lengths.
		 */
		out.println("First sequence FASTA comment:");
		out.println(firstSequence.getComment());
		out.print("First sequence length:");
		out.println(firstSequence.size());
		out.println("Second sequence FASTA comment:");
		out.println(secondSequence.getComment());
		out.print("Second sequence length:");
		out.println(secondSequence.size());
		
		/*
		 * Requirement 7: Carries out global alignment with gap penalties.
		 */ 
		
		// Create the aligner object.
		SequenceAligner aligner = new Needleman(comparator, gapPenalty, gapExtensionPenalty);
		
		/* The following code will work for any alignment algorithm.  The
		 * selection of "global alignment" and "with gap penalties" is determined
		 * by our use of the org.aisb.bio.tools.Needleman class as our aligner.
		 * We could use anything else that implemenets the SequenceAligner interface
		 * and the following code fragment wouldn't need any change.
		 */
		
		// At this point, both sequences are ready for use.
		// We've also got a comparator ready to use.
		// Time to do the deed!
		// Feed the sequences into the aligner we've set up.
		aligner.setFirstSequence(firstSequence);
		aligner.setSecondSequence(secondSequence);
		// Actually do the alignment.
		aligner.execute();
		
		/*
		 * Requirement 8: Print out the maximum score and the aligned sequences.
		 * 
		 */
		
		// Print out information about the alignment.
		out.println("Score: " + aligner.getBestAlignmentScore());
		out.println("Number of alignments: " + aligner.getNumberOfAlignments());
		out.println("The alignments:\n");
		
		

		String alignment = aligner.getBestAlignmentAsString();
		out.println("First alignmnet: ");
		out.println(alignment);
		
		out.println("\nThe rest of the alignments are implicit in this modified dot plot:");
		if (true) {
			Character[][] bestAlignments = aligner.getBestAlignmentsAsMatrix();
			for (int i=0; i < bestAlignments.length; i++) {
				for (int j=0; j < bestAlignments[0].length; j++) {
					out.print(bestAlignments[i][j]);
				}
				out.println();
			}
		}
		
	}
}

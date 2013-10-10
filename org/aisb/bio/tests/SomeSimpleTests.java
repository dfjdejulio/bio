/*
 * Created on Jan 29, 2005
 */
package org.aisb.bio.tests;


import org.aisb.bio.things.*;
import org.aisb.bio.tools.*;

/**
 * This class performs some simple unit tests of the underlying frameworks.
 * 
 * @author Doug DeJulio
 *
 */
public class SomeSimpleTests {
	static java.io.PrintStream out = System.out;

	public static void main(String[] args) throws Exception {
		DynamicDuo.initialize();
		// This is the simplest way to use the Sequence class, for small amounts of data.
		Sequence ittyBittyPeptide = new Sequence(AminoAcid.class, "SEQENCE");
		/* Uncommenting the following line will throw an exception, since
		 * "U" is not a valid character in a protein sequence.
		 */
		//Sequence badPeptide = new Sequence(AminoAcid.class, "SEQUENCE");
		// This is a bit more interesting, if you're building a sequence from a data stream.
		Sequence otherPeptide = new Sequence();
		otherPeptide.setType(AminoAcid.class);
		otherPeptide.appendFromString("SEQ");
		otherPeptide.appendFromString("SEQ");
		out.println(ittyBittyPeptide);
		out.println(otherPeptide);
		MonomerComparator identity = new IdentityComparator();
		out.print("Comparison: ");
		for (int i=0; i < 5; i++) {
			out.print(' ');
			out.print(identity.compare((Monomer)ittyBittyPeptide.get(i), (Monomer)otherPeptide.get(i)));
		}
		out.println('.');
		// Test the URL fetcher.
		SequenceFetcher fetcher = new SequenceFetcher();
		fetcher.setGenbankIdentifier("46361980");
		fetcher.execute();
		Sequence result = fetcher.getSequence();
		out.println(result);
	}
}

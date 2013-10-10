/*
 * Created on Jan 29, 2005
 */
package org.aisb.bio.things;

/**
 * This class describes the set of amino acids that make up protein sequences.
 * You use it by for example calling AminoAcid.getByName("Tyr"), or
 * AminoAcid.getByCode('T').
 *  
 * @author Doug DeJulio
 */
public class AminoAcid extends Monomer {

	public AminoAcid(Class type, String name, Character code, int number) {
		super(type, name, code, number);
	}
	
	public static void initialize() {
		// Save room for 26 entries.  We'll make the numeric code equal the location of the
		// letter code in the alphabet (A=1, B=2...).
		initialize(26);
		// Now, add all the amino acids we'll use.
		// These values are taken directly from the Mount text, page 43, table 2.2.
		addMonomer(AminoAcid.class, "Ala", 'A', 1);
		//addMonomer(AminoAcid.class, "Nothing01", 'B', 2);
		addMonomer(AminoAcid.class, "Cys", 'C', 3);
		addMonomer(AminoAcid.class, "Asp", 'D', 4);
		addMonomer(AminoAcid.class, "Glu", 'E', 5);
		addMonomer(AminoAcid.class, "Phe", 'F', 6);
		addMonomer(AminoAcid.class, "Gly", 'G', 7);
		addMonomer(AminoAcid.class, "His", 'H', 8);
		addMonomer(AminoAcid.class, "Ile", 'I', 9);
		//addMonomer(AminoAcid.class, "Nothing02", 'J', 10);
		addMonomer(AminoAcid.class, "Lys", 'K', 11);
		addMonomer(AminoAcid.class, "Leu", 'L', 12);
		addMonomer(AminoAcid.class, "Met", 'M', 13);
		addMonomer(AminoAcid.class, "Asn", 'N', 14);
		//addMonomer(AminoAcid.class, "Nothing03", 'O', 15);
		addMonomer(AminoAcid.class, "Pro", 'P', 16);
		addMonomer(AminoAcid.class, "Gln", 'Q', 17);
		addMonomer(AminoAcid.class, "Arg", 'R', 18);
		addMonomer(AminoAcid.class, "Ser", 'S', 19);
		addMonomer(AminoAcid.class, "Thr", 'T', 20);
		addMonomer(AminoAcid.class, "Nothing04", 'U', 21);
		addMonomer(AminoAcid.class, "Val", 'V', 22);
		addMonomer(AminoAcid.class, "Trp", 'W', 23);
		addMonomer(AminoAcid.class, "Xxx", 'X', 24);
		addMonomer(AminoAcid.class, "Tyr", 'Y', 25);
		addMonomer(AminoAcid.class, "Glx", 'Z', 26);
		
		// Now, indicate we're done.
		finishInitializing();
	}

}

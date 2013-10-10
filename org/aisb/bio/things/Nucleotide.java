/*
 * Created on Feb 17, 2005 by Doug DeJulio
 */
package org.aisb.bio.things;

/**
 * This subclass of monomer specifically encodes nucleotides.
 * You use it by for example calling AminoAcid.getByCode('A'), or
 * AminoAcid.getByNumber(1).
 * 
 * @author Doug DeJulio
 *
 */
public class Nucleotide extends Monomer {

	/**
	 * @param type
	 * @param name
	 * @param code
	 * @param number
	 */
	public Nucleotide(Class type, String name, Character code, int number) {
		super(type, name, code, number);
	}
	
	public static void initialize() {
		// Save room for 4 entries.  We'll make the numeric code equal "1" for "A",
		// "2" for "C", "3" for "G", and "4" for "T", for compatibility with our PSSM
		// routine.
		initialize(4);
		
		// Now add all the nucleotides we'll use.
		addMonomer(Nucleotide.class, "A", 'A', 1);
		addMonomer(Nucleotide.class, "C", 'C', 2);
		addMonomer(Nucleotide.class, "G", 'G', 3);
		addMonomer(Nucleotide.class, "T", 'T', 4);
		
		// Now, indicate we're done.
		finishInitializing();
	}

}

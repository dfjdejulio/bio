/*
 * Created on Jan 29, 2005
 */
package org.aisb.bio.tools;

import java.util.Map;
import java.util.HashMap;

import org.aisb.bio.things.AminoAcid;

/**
 * This is the framework for doing pairwise alignments.
 *
 * This class is used to manage the framework as a whole.  To start using the framework, call
 * the static "initialize()" method exactly once.
 * 
 * @author Doug DeJulio
 */
public class DynamicDuo {
	static Map monomers = new HashMap();
	static Map aligners = new HashMap();
	
	/**
	 * Initialize all the data structures used by this framework. 
	 */
	public static void initialize() {
		AminoAcid.initialize();
		monomers.put("AminoAcid", AminoAcid.class);
	}

}

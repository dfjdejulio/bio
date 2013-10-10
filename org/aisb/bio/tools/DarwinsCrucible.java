/*
 * Created on Apr 28, 2005 by Doug DeJulio
 */
package org.aisb.bio.tools;

import java.util.HashMap;
import java.util.Map;

import org.aisb.bio.things.Nucleotide;

/**
 * <p>
 * This is a framework for running simulations of successive mutation and natural selection.
 * </p><p>
 * This class is used to manage the framework as a whole.  To start using the framework,
 * call the static "initialize()" method exactly once.
 * </p>
 * 
 * @author Doug DeJulio
 *
 */
public class DarwinsCrucible {
	static Map monomers = new HashMap();
	static Map selectors = new HashMap();
	
	public static void initialize() {
		Nucleotide.initialize();
		monomers.put("Nucleotide", Nucleotide.class);
	}
}

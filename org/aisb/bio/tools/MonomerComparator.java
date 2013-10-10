/*
 * Created on Jan 31, 2005
 *
 */
package org.aisb.bio.tools;

import org.aisb.bio.things.Monomer;

/**
 * This is the base implementation for comparing two monomers.
 * A dynamic programming implementation could call anything
 * based on this.  Some implementations might be algorithmic,
 * others might be based on similarity tables, and the implementation
 * would not have to care.
 * 
 * @author Doug DeJulio
 *
 */
public abstract class MonomerComparator {
	abstract public int compare(Monomer m1, Monomer m2);
}

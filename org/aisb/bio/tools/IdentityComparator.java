/*
 * Created on Jan 31, 2005 by Doug DeJulio
 */
package org.aisb.bio.tools;

import org.aisb.bio.things.Monomer;

/**
 * This trivial comparison function returns "1" if the elements are
 * completely identical, and "0" if they are not.
 * 
 * @author Doug DeJulio
 *
 */
public class IdentityComparator extends MonomerComparator {

	/* (non-Javadoc)
	 * @see org.aisb.bio.tools.MonomerComparator#compare(org.aisb.bio.things.Monomer, org.aisb.bio.things.Monomer)
	 */
	public int compare(Monomer m1, Monomer m2) {
		if (m1 == m2) {
			return 1;
		} else {
			return 0;
		}
	}

}

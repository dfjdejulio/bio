/*
 * Created on Jan 31, 2005 by Doug DeJulio
 */
package org.aisb.bio.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.aisb.bio.things.AminoAcid;
import org.aisb.bio.things.Sequence;

/**
 * Give this class the name of a sequence, and it will fetch it via the web.
 * 
 * This class implements the "command bean" design pattern, which just means
 * that it's a command implemented as a JavaBean.  This makes it easier to use
 * from a variety of environments, such as JSP pages using JSTL.
 * 
 * @author Doug DeJulio
 *
 */
public class SequenceFetcher {
	String name = null;
	Sequence sequence;
	Class type = AminoAcid.class; // Default to operating on amino acids.
	String urlBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=protein&retmode=text&rettype=fasta&id=";

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * This is a "magic" setter, that builds the name correctly from a given genbank id.
	 * 
	 * @param gi
	 */
	public void setGenbankIdentifier(String gi) {
		this.name = "gi|" + gi;
	}
	
	
	
	/**
	 * @return Returns the type.
	 */
	public Class getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(Class type) {
		this.type = type;
	}
	/**
	 * @return Returns the sequence.
	 */
	public Sequence getSequence() {
		return sequence;
	}
	
	/**
	 * Perform the retreival.  For the moment, this method is
	 * very sloppy; we'll tidy it up over the course of the
	 * semester.  This should be good enough for the first
	 * project.
	 * 
	 * @throws Exception
	 */
	public void execute() throws Exception {
		if (name == null) {
			throw new IllegalStateException("Cannot execute until a sequence name has been set.");
		}
		/**
		 * The documentation for Entrez says not to submit queries more than once every
		 * three seconds.  So, let's make sure we enforce that.
		 */
		Thread.sleep(3000);
		// Construct the URL and get ready to read it.
		URL seqUrl = new URL(urlBase + name);
		InputStream in = (InputStream) seqUrl.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		// Construct the object to hold the sequence.
		sequence = new Sequence();
		sequence.setType(this.type);
		
		// Get ready to do the processing.
		String line;
		// With FASTA format, the first line is a comment.
		sequence.setComment(reader.readLine());
		while ((line = reader.readLine()) != null) {
			sequence.appendFromString(line);
		}
	}
}

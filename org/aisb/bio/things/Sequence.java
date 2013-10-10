/*
 * Created on Jan 29, 2005
 */
package org.aisb.bio.things;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * <p>
 * This is the basis for any implementation of any sequence.
 * Classes and methods that work on sequences should refer to this,
 * and not directly to the classes that extend it.  It is an implementation of
 * the {@link java.util.List} interface, which is an extension of the {@link java.util.Collection} interface.  This
 * means there are already a lot of sophisticated Java tools that will work directly
 * on it.  It also means that you ought not assume the implementations are thread safe.
 * </p><p>
 * There is a lot of "junk code" here.  We create a {@link java.util.ArrayList}
 * and wrap it with a bunch of methods that validate the inputs against the type of sequence
 * we have, for inputs that would modify the contents.  Very little of that code is worth
 * looking at.  However, doing this allows us to focus on adding interesting methods while
 * preserving the {@link java.util.List} and {@link java.util.Collection} interfaces.
 * </p><p>
 * Of particular note is the <code>loadFromString</code> method.  This takes a string of characters
 * representing a sequence.  It then generates an actual Sequence of the Monomers represented
 * by those characters.
 * </p><p>
 * We can tighten this up a lot later on.  This was just the easiest implementation
 * for me to blast out quickly.
 * </p>
 * @author Doug DeJulio
 */
public class Sequence implements List {
	List data = new ArrayList();	// This contains the real data.
	Method factory; // This turns a 'char' into an object that makes sense in this Sequence.
	String comment; // This is just some useful comment.  Not meaningful for the algorithms.
	
	// What is this a sequence *of*?
	public Class type;
	
	/**
	 * @return Returns the type.
	 */
	public Class getType() {
		return type;
	}

	public void setType(String type) {
		try {
			setType(Class.forName(type));
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Invalid type: " + type);
		}
	}
	
	/**
	 * Set the type.  This is used to validate all the elements that make up this
	 * sequence.  For example, if the type is set to {@link org.aisb.bio.things.AminoAcid}, attempts to add an
	 * object of type {@link org.aisb.bio.things.Nucleotide} will fail.  However, attempts to add elements of a
	 * subclass of <code>AminoAcid</code>, such as a class describing some constrained set of amino
	 * acids, will succeed.
	 * 
	 * Every class used must be descended from {@link org.aisb.bio.things.Monomer}.
	 * 
	 * @param type The type to set.
	 */
	public void setType(Class type) {
		// Make sure the thing is a subclass of Monomer.
		if (!Monomer.class.isAssignableFrom(type)) {
			throw new IllegalArgumentException("Type must be a sublcass of Monomer.");
		}

		// Remember it.
		this.type = type;

		// Now, get the factory method we'll use.
		Class[] arguments = { Character.class };
		try {
			factory = type.getMethod("getByCode", arguments);
		} catch (Exception e) {
			throw new IllegalStateException("Type not conforming to the Monomer contract: " + type);
		}
	}
	
	/**
	 * Return this sequence in FASTA format.
	 * 
	 * @return The FASTA format version of the sequence.
	 */
	public String getFasta() {
		int linelength = 72;	// Change this to change the output line length.
		StringWriter fasta = new StringWriter();
		fasta.write(this.getComment());	// Put the comment there.
		int i;
		for (i=0; i < this.size(); i++) {
			if (i % linelength == 0) {
				fasta.write('\r');
			}
			Monomer m = (Monomer) this.get(i);
			fasta.write(m.getCode().charValue());
		}
		if (i % linelength != 0) {
			fasta.write('\r');
		}
		return fasta.toString();
	}
	
	/**
	 * @return Returns the comment.
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment The comment to set.
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * This method validates every object added to the sequence.  This prevents us
	 * from accidentally creating a sequence that contains both amino acids and
	 * nucleotides for example. 
	 * 
	 * @param element The element to be added to the sequence.
	 */
	public void validate(Object element) {
		// TODO make this actually work...
		//if (!type.isInstance(element)) {
		//	throw new IllegalArgumentException("Element must be of type" + type);
		//}
	}
	
	public void validate(Collection c) {
		Iterator i = c.iterator();
		while (i.hasNext()) {
			validate(i.next());
		}
	}
	
	/**
	 * Take a string that makes sense given the type of sequence this is.  For example,
	 * if we have a Sequence in which the type has been set to Nucleotide, the string
	 * "GATTACA" makes sense.  The string is parsed and an ordered Collection of the
	 * appropriate object is created and stuffed into this Sequence's data field.
	 * 
	 * In other words, we can take any sequence file and convert it into one very long string,
	 * create a new Sequence, call <code>setType</code> on that new Sequence to teach it what it's
	 * a sequence of, and pass that string into the <code>loadFromString</code> method, and we're done. 
	 * 
	 * @param input
	 */
	public void loadFromString(String input) {
		data.clear();
		appendFromString(input);
	}
	
	public void appendFromString(String input) {
		int size = input.length();  // How long is this batch?
		for (int i=0; i < size; i++) {
			Object element = null;
			Character code;
			// Get the nth character in the string.
			code = new Character(input.charAt(i));
			// Turn it into an object of the appropriate type...
			Object[] arguments = new Object[1];
			arguments[0] = code;
			try {
				// By calling that objects "getByCode" method.
				element = factory.invoke(type, arguments);
			} catch (Exception e) {
				throw new IllegalStateException("Type not honoring Monomer contract: " + type);
			}
			// If the letter isn't in our code list, element will be null here.
			if (element == null) {
				throw new IllegalArgumentException("Invalid element at position " + Integer.toString(i+1) + " : " + code);
			}
			// Set put the object in the nth position of the list.
			data.add(element);
			
		}
	}
	
	/* Now, some interesting constructors. */
	
	public Sequence(Class monomer) {
		this.setType(monomer);
	}
	
	public Sequence(Class monomer, String sequence) {
		this.setType(monomer);
		this.loadFromString(sequence);
	}
	
	/* And, an actually useful toString method. */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		Iterator i;
		buf.append("[Sequence: ");
		buf.append(type.getName());
		buf.append(" : ");
		i = data.iterator(); {
			while (i.hasNext()) {
				Monomer m = (Monomer) i.next();
				buf.append(m.getCode());
			}
		}
		buf.append(']');
		return buf.toString();
	}
	
	/************************************************/
	/*** Nothing below here is really interesting ***/
	/************************************************/
	
	/*
	 * Wrap some constructors from ArrayList.  We do not
	 * wrap ArrayList(Collection) because we want to make sure
	 * everything goes through the validator before it's added.
	 */
	
	public Sequence(int count) {
		data = new ArrayList(count);
	}
	
	public Sequence() {
		data = new ArrayList();
	}
	
	/* (non-Javadoc)
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, Object element) {
		validate(element);
		data.add(index, element);
	}
	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Object o) {
		validate(o);
		return data.add(o);
	}
	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection c) {
		validate(c);
		return data.addAll(c);
	}
	/* (non-Javadoc)
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection c) {
		validate(c);
		return data.addAll(index, c);
	}
	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		data.clear();
	}
	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return data.contains(o);
	}
	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection c) {
		return data.containsAll(c);
	}
	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	public Object get(int index) {
		return data.get(index);
	}
	/* (non-Javadoc)
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		return data.indexOf(o);
	}
	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}
	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	public Iterator iterator() {
		return data.iterator();
	}
	/* (non-Javadoc)
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) {
		return data.lastIndexOf(o);
	}
	/* (non-Javadoc)
	 * @see java.util.List#listIterator()
	 */
	public ListIterator listIterator() {
		return data.listIterator();
	}
	/* (non-Javadoc)
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator listIterator(int index) {
		return data.listIterator(index);
	}
	/* (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	public Object remove(int index) {
		return data.remove(index);
	}
	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return data.remove(o);
	}
	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection c) {
		return data.removeAll(c);
	}
	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection c) {
		return data.retainAll(c);
	}
	/* (non-Javadoc)
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public Object set(int index, Object element) {
		validate(element);
		return data.set(index, element);
	}
	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return data.size();
	}
	/* (non-Javadoc)
	 * @see java.util.List#subList(int, int)
	 */
	public List subList(int fromIndex, int toIndex) {
		return data.subList(fromIndex, toIndex);
	}
	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		return data.toArray();
	}
	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(java.lang.Object[])
	 */
	public Object[] toArray(Object[] a) {
		return data.toArray(a);
	}
}

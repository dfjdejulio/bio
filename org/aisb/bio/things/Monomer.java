package org.aisb.bio.things;

import java.util.HashMap;
import java.util.Map;

/*
 * Created on Jan 29, 2005
 */ 

/**
 * <p>
 * This abstract class defines monomers.  Typical examples include amino acids, bases,
 * or monosacharides.
 * </p><p>
 * The basic idea is that you never create the objects yourself.  Exactly one object is
 * created for each monomer, and references to it are handed out upon request.  This allows
 * for a very small number of object creations, and allows the "=" operator to actually do
 * the right thing when these objects are used.
 * </p><p>
 * Once we're using Java 1.5, we can use typesafe enumerations and the special collection
 * implementations that work on them to make this blazingly efficient; for the moment we'll
 * concentrate on clarity instead of performance.
 * </p><p>
 * (This isn't working quite right when I've got multiple subclasses yet.  Works just fine when
 * there's only one subclass, and that's all we need for the first assignment, so I'll worry
 * about fixing that aspect of it later.  Might have to make a distinct object factory class.
 * I'll figure it out, but not by 2005-02-01.)
 * </p>
 * @author Doug DeJulio
 */
public class Monomer {
	// Static fields.
	static int quantity = 0;
	static Monomer[] monomers = null;
	static Map monomersByName = null;
	static Map monomersByCode = null;
	static boolean doneInitializing = false;
	// Fields.
	String name;
	Character code;
	int number;
	Class type;
	Map properties = new HashMap();

	/**
	 * This should only be invoked by subclasses.
	 * 
	 * @param quantity The highest numbered element in this set of monomers.  Note that gaps
	 * are allowed, but if present they'll cause inefficiencies in memory.
	 */
	static void initialize(int quantity) {
		if (quantity == 0) {
			// You can only do this once...
			throw new IllegalStateException("Illegal attempt to initialize more than once.");
		}
		Monomer.quantity = quantity;
		monomers = new Monomer[quantity]; // Make an array to hold one of each monomer.
		monomersByName = new HashMap(quantity); // Make a map of exactly the right size.
		monomersByCode = new HashMap(quantity); // Ditto.
	}
	
	static void finishInitializing() {
		doneInitializing = true;
	}
	
	/**
	 * This should only be invoked by subclasses.
	 * 
	 * @param name A human-readable name for the monomer, like "Adenine".
	 * @param code A code for the monomer, like "A".
	 * @param number An integer representing the monomer, like "7", "0x10", or "0210".
	 */
	public Monomer(Class type, String name, Character code, int number) {
		// NEVER NEVER NEVER call this outside of classes that implement Monomers.
		if (doneInitializing) {
			throw new IllegalStateException("Illegal attempt to use constructor directly.");
		}
		this.type = type;	// What sort of thing am I?
		this.name = name;	// Learn my name.
		this.code = code;	// Learn my code.
		this.number = number;	// Learn my number.
	}
	
	static void addMonomer(Class type, String name, char code, int number) {
		addMonomer(type, name, new Character(code), number);
	}
	
	static void addMonomer(Class type, String name, Character code, int number) {
		// The numbers must all be positive integers.
		if (number < 1) {
			throw new IllegalArgumentException("The number must be a positive integer.");
		}
		Monomer newMonomer = new Monomer(type, name, code, number); // Make the object.
		monomers[number-1] = newMonomer; // Stuff it in the array.
		monomersByName.put(name, newMonomer); // Stuff it in the name map.
		monomersByCode.put(code, newMonomer); // Stuff it in the code map.
	}


	/**
	 * Assign a named property to this monomer.  This can be used for things like
	 * hydrophobic moment, molecular weight, whatever.  It's application specific.
	 * 
	 * @param name The name of the property.
	 * @param value The value of the property.
	 */
	public void setProperty(String name, Object value) {
		properties.put(name, value);
	}

	/**
	 * Get a property from a monomer by name.  It'll only work if the property has
	 * been set.
	 * 
	 * @param name The name of the property.
	 * @return The value of the property.
	 */
	public Object getProperty(String name) {
		return properties.get(name);
	}

	
	/**
	 * Given the name of a monomer, perhaps such as "tyrosine" or "fructose",
	 * return the representation of that monomer. 
	 * 
	 * @param name The name of the monomer.
	 * @return The object representing the monomer.
	 */
	public static Monomer getByName(String name) {
		return (Monomer) monomersByName.get(name);
	}

	/**
	 * Given the one-character code of a monomer, return the representation of that monomer.
	 * 
	 * @param code The code for the monomer.
	 * @return The object representing the monomer.
	 */
	public static Monomer getByCode(char code) {
		return (Monomer) monomersByCode.get(new Character(code));
	}
	
	/**
	 * Given the one-character code of a monomer, return the representation of that monomer.
	 * 
	 * @param code The code for the monomer.
	 * @return The object representing the monomer.
	 */
	public static Monomer getByCode(Character code) {
		return (Monomer) monomersByCode.get(code);
	}

	/**
	 * Given the numberic code for a monomer, return the representation of that monomer.
	 * 
	 * @param number The code for the monomer.
	 * @return The object representing the monomer.
	 */
	public static Monomer getByNumber(int number) {
		return monomers[number-1];
	}
	
	/**
	 * Given the numberic code for a monomer, return the representation of that monomer.
	 * 
	 * @param number The code for the monomer.
	 * @return The object representing the monomer.
	 */
	public static Monomer getByNumber(Integer number) {
		return monomers[number.intValue()-1];
	}
	
	public static int getQuantity() {
		return quantity;
	}

	/**
	 * @return The monomer's name.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return The monomer's one-character code.
	 */
	public Character getCode() {
		return this.code;
	}
	
	/**
	 * @return The monomer's numeric code.
	 */
	public int getNumber() {
		return this.number;
	}
	
	/**
	 * @return The monomer's type (eg. nucleotide, amino acid), coded by
	 * the reference to the Class for that monomer.
	 */
	public Class getType() {
		return this.type;
	}
}

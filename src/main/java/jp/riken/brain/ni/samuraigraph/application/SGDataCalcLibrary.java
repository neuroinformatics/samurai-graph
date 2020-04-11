package jp.riken.brain.ni.samuraigraph.application;

import com.sun.jna.Pointer;

/**
 * Wrapper class of data plug-in.
 *
 */
public class SGDataCalcLibrary {
	
	private SGIDataCalcLibrary mLibrary;

	public SGDataCalcLibrary(SGIDataCalcLibrary lib) {
		super();
		if (lib == null) {
			throw new IllegalArgumentException("lib == null");
		}
		this.mLibrary = lib;
	}
	
	/**
	 * Returns a text string of identifier.
	 * 
	 * @return a text string of identifier
	 */
	public String getId() {
		return Integer.toString(this.mLibrary.hashCode());
	}
	
	/**
	 * Processes given data and returns the result.
	 * 
	 * @param input
	 *           pointer for input data
	 * @return pointer for the result of calculation
	 */
	public Pointer calc(Pointer input) {
		return this.mLibrary.calc(input);
	}

	/**
	 * Frees the memory of given data buffer.
	 * 
	 * @param buffer
	 *           data buffer
	 */
	public void freeData(Pointer buffer) {
		this.mLibrary.freeData(buffer);
	}

}

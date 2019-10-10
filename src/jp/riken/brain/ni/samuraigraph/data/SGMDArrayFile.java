package jp.riken.brain.ni.samuraigraph.data;


/**
 * The base class for the multidimensional data file.
 *
 */
public abstract class SGMDArrayFile extends SGDataSource {

	/**
	 * The list of the name of variables.
	 * 
	 */
	protected SGMDArrayVariable[] mVariables = null;

	/**
	 * The default constructor.
	 */
	public SGMDArrayFile() {
		super();
	}
	
	/**
	 * Disposes of this object.
	 */
	@Override
	public void dispose() {
    	super.dispose();
    	for (SGMDArrayVariable var : this.mVariables) {
    		var.dispose();
    	}
		this.mVariables = null;
	}

	/**
	 * Returns true if the reader object is equal.
	 * 
	 * @param obj
	 *          an object to be compared
	 * @return true if the reader object e is equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (!(obj instanceof SGMDArrayFile)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Finds and returns a variable of given name.
	 * 
	 * @param name
	 *           the name of variable
	 * @return a variable if it is found
	 */
	public SGMDArrayVariable findVariable(final String name) {
		SGMDArrayVariable ret = null;
		for (SGMDArrayVariable var : this.mVariables) {
			if (var.getName().equals(name)) {
				ret = var;
				break;
			}
		}
		return ret;
	}
	
	/**
	 * Returns the array of variables.
	 * 
	 * @return the array of variables
	 */
	public SGMDArrayVariable[] getVariables() {
		return this.mVariables.clone();
	}
	
	/**
	 * Finds a variable of a given name if it exists and returns its array index.
	 * 
	 * @param name
	 *           the name of variable
	 * @return the array index of found variable or -1 if it is not found
	 */
	public int getVariableIndex(final String name) {
		SGMDArrayVariable[] vars = this.getVariables();
		for (int ii = 0; ii < vars.length; ii++) {
			SGMDArrayVariable var = vars[ii];
			if (name.equals(var.getName())) {
				return ii;
			}
		}
		return -1;
	}

	/**
	 * Returns the origins.
	 * 
	 * @param name
	 *            the name of variable
	 * @return the origins
	 */
	public int[] getOrigins(final String name) {
		SGMDArrayVariable var = this.findVariable(name);
		if (var == null) {
			throw new IllegalArgumentException("Data Set is not found: " + name);
		}
		return var.getOrigins().clone();
	}
}

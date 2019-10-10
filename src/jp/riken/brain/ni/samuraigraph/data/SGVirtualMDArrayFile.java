package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGAttribute;

/**
 * The class of a virtual file with multidimensional number arrays.
 *
 */
public class SGVirtualMDArrayFile extends SGMDArrayFile {

	/**
	 * Builds a virtual file with an array of variables.
	 * 
	 * @param vars
	 *           an array of variables
	 */
	public SGVirtualMDArrayFile(SGVirtualMDArrayVariable[] vars) {
		super();
		if (vars == null) {
			throw new IllegalArgumentException("vars == null");
		}
		this.mVariables = new SGMDArrayVariable[vars.length];
		for (int ii = 0; ii < vars.length; ii++) {
			this.mVariables[ii] = (SGMDArrayVariable) vars[ii].clone();
		}
	}
	
	@Override
	public String getPath() {
		// always returns null
		return null;
	}

	@Override
	public List<SGAttribute> getAttributes() {
		// always returns an empty list
		return new ArrayList<SGAttribute>();
	}
}

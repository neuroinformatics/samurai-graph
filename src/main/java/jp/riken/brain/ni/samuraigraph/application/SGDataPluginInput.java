package jp.riken.brain.ni.samuraigraph.application;

import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;

/**
 * The input of data plug-in.
 *
 */
public class SGDataPluginInput implements Cloneable {

	// The array of data buffers.
	private SGDataBuffer[] mDataBuffers = null;

	// The map of input parameters.
	private String[] mParameters = null;

	public SGDataPluginInput(SGDataBuffer[] dataBuffers, String[] parameters) {
		super();
		if (dataBuffers == null) {
			throw new IllegalArgumentException("dataBuffers == null");
		}
		if (parameters == null) {
			throw new IllegalArgumentException("parameters == null");
		}
		this.mDataBuffers = dataBuffers.clone();
		this.mParameters = parameters.clone();
	}
	
    /**
     * Clones this object.
     * 
     * @return the copy of this object
     */
	@Override
    public Object clone() {
        try {
        	SGDataPluginInput input = (SGDataPluginInput) super.clone();
        	input.mParameters = this.mParameters.clone();
        	return input;
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

	public SGDataBuffer[] getDataBufffers() {
		return this.mDataBuffers.clone();
	}

	public String[] getParameters() {
		return this.mParameters.clone();
	}
	
}

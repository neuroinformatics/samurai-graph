package jp.riken.brain.ni.samuraigraph.application;

import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;

/**
 * The output of data plug-in.
 *
 */
public class SGDataPluginOutput implements Cloneable {

	// The array of data buffers.
	private SGDataBuffer[] mDataBuffers = null;

	// The list of error messages.
	private String[] mErrorMessages = null;

	public SGDataPluginOutput(SGDataBuffer[] dataBuffers, String[] errorMessages) {
		super();
		if (dataBuffers == null) {
			throw new IllegalArgumentException("dataBuffers == null");
		}
		if (errorMessages == null) {
			throw new IllegalArgumentException("errorMessages == null");
		}
		this.mDataBuffers = dataBuffers.clone();
		this.mErrorMessages = errorMessages.clone();
	}
	
    /**
     * Clones this object.
     * 
     * @return the copy of this object
     */
	@Override
    public Object clone() {
        try {
        	SGDataPluginOutput input = (SGDataPluginOutput) super.clone();
        	input.mErrorMessages = this.getErrorMessages();
        	return input;
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

	public SGDataBuffer[] getDataBufffers() {
		return this.mDataBuffers.clone();
	}

	public String[] getErrorMessages() {
		return this.mErrorMessages.clone();
	}
	
}

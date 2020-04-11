package jp.riken.brain.ni.samuraigraph.base;

import javax.swing.JPanel;

/**
 * The base class of panels used for the property setting.
 */
public class SGPropertyPanel extends JPanel implements SGIDisposable {

    /**
     * 
     */
    private static final long serialVersionUID = -7160509846826366407L;

    private String mInputErrorMessage;

    /**
     * 
     */
    public SGPropertyPanel() {
        super();
    }

    /**
     * @return
     */
    public String getInputErrorMessage() {
        return this.mInputErrorMessage;
    }

    /**
     * @param string
     */
    public void setInputErrorMessage(final String string) {
        this.mInputErrorMessage = string;
    }

    /**
     * Disposes of this object.
     * 
     */
    public void dispose() {
        this.mInputErrorMessage = null;
    }

    // The flag whether this object is already disposed of.
    private boolean mDisposed = false;

    /**
     * Returns whether this object is already disposed of.
     * 
     * @return true if this object is already disposed of
     */
    public boolean isDisposed() {
    	return this.mDisposed;
    }

}

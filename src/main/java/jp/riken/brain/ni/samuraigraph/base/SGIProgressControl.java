package jp.riken.brain.ni.samuraigraph.base;

/**
 * The interface for the components that displays the progress.
 * 
 */
public interface SGIProgressControl {

    /**
     * Starts the progress.
     *
     */
    public void startProgress();

    /**
     * Stops the progress.
     * 
     */
    public void endProgress();

    /**
     * Starts the progress in indeterminate mode.
     * 
     */
    public void startIndeterminateProgress();

    /**
     * Sets the value of progress between 0 and 1.
     * 
     * @param ratio
     *          progress ratio
     */
    public void setProgressValue(final float ratio);

    /**
     * Sets the messsage of the progress.
     * 
     * @param msg
     *          progress messasge
     */
    public void setProgressMessage(final String msg);
}

package jp.riken.brain.ni.samuraigraph.base;

/**
 * An interface for the data source of the animation.
 * 
 */
public interface SGIAnimation {

    /**
     * Returns the number of frames.
     * 
     * @return the number of frames
     */
    public int getFrameNumber();

    /**
     * Returns the current frame index.
     * 
     * @return the current frame index
     */
    public int getCurrentFrameIndex();

    /**
     * Sets the current frame index.
     * 
     * @param index the frame index to be set
     */
    public void setCurrentFrameIndex(int index);

    /**
     * Returns the array section for animation
     *
     * @return the array section for animation
     */
    public SGIntegerSeriesSet getAnimationArraySection();

    /**
     * Sets the array section for animation
     *
     * @param stride
     *           the array section for animation
     */
    public void setFrameIndices(SGIntegerSeriesSet arraySection);

    /**
     * Returns whether loop play back is available.
     * 
     * @return true if loop play back is available
     */
    public boolean isLoopPlaybackAvailable();

    /**
     * Sets whether loop play back is available.
     * 
     * @param b
     *          the flag to set
     */
    public void setLoopPlaybackAvailable(final boolean b);

    /**
     * Returns the frame rate.
     * 
     * @return the frame rate
     */
    public double getFrameRate();
    
    /**
     * Sets the frame rate.
     * 
     * @param rate
     *           the frame rate to set
     */
    public void setFrameRate(final double rate);

}

package jp.riken.brain.ni.samuraigraph.base;

/**
 * An interface for the zoomable objects.
 * 
 */
public interface SGIZoomable {

    /**
     * Zoom the object.
     * 
     * @param mag -
     *            magnification
     */
    public boolean zoom(final float mag);

    /**
     * Enables the auto zooming
     * 
     * @param b -
     *            true enables and false disables the auto zooming
     */
    public void setAutoZoom(final boolean b);

}

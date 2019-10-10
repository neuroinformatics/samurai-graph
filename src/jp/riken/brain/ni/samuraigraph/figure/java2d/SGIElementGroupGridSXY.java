package jp.riken.brain.ni.samuraigraph.figure.java2d;


public interface SGIElementGroupGridSXY {

    /**
     * Initializes the drawing elements with given coordinates.
     * 
     * @param xArray
     *           an array of x-coordinates
     * @param yArray
     *           an array of y-coordinates
     * @return true if succeeded
     */
    public boolean initDrawingElement(final float[] xArray, 
            final float[] yArray);
}

package jp.riken.brain.ni.samuraigraph.base;


/**
 * The base class of drawing elements.
 */
public abstract class SGDrawingElement implements SGIDrawingElementConstants,
        SGIVisible, SGIDisposable {

	/**
     * The visibility flag.
	 */
    private boolean mVisibleFlag = true;

    /**
     * The default constructor.
     */
    public SGDrawingElement() {
        super();
    }

    /**
     * Returns whether this drawing element "contains" the given point.
     */
    public abstract boolean contains(final int x, final int y);

    /**
     * 
     * @return
     */
    public final boolean isVisible() {
        return this.mVisibleFlag;
    }

    /**
     * 
     * @param b
     */
    public void setVisible(final boolean b) {
        this.mVisibleFlag = b;
    }

    /**
     * Sets the magnification.
     * 
     * @param mag
     *          the magnification
     * @return true if succeeded
     */
    public abstract boolean setMagnification(final float mag);

    /**
     * Returns the magnification.
     * 
     * @return the magnification
     */
    public abstract float getMagnification();
    
    /**
     * Sets the given properties to this object.
     * 
     * @param p
     *          the properties
     * @return true if succeeded
     */
    public boolean setProperties(SGProperties p) {
        if ((p instanceof DrawingElementProperties) == false) {
			return false;
		}
		DrawingElementProperties dp = (DrawingElementProperties) p;
		final Boolean b = dp.isVisible();
		if (b == null) {
			return false;
		}
		this.setVisible(b.booleanValue());
		return true;
    }

    /**
	 * Returns the properties of this object.
	 * 
	 * @return the properties
	 */
    public SGProperties getProperties() {
        final DrawingElementProperties p = new DrawingElementProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    /**
     * Sets the properties of this object to a given property object.
     * 
     * @param p
     *           the properties
     * @return true if succeeded
     */
    public boolean getProperties(final SGProperties p) {
        if ((p instanceof DrawingElementProperties) == false) {
        	return false;
        }
        final DrawingElementProperties dp = (DrawingElementProperties) p;
        dp.setVisible(this.isVisible());
        return true;
    }

    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;
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
    
    /**
     * Properties of the drawing element.
     */
    public static class DrawingElementProperties extends SGProperties {
    	
    	/**
    	 * The visibility flag.
    	 */
        private boolean mVisible;

        /**
         * The default constructor.
         */
        public DrawingElementProperties() {
            super();
        }

        /**
         * Disposes this object.
         */
        public void dispose() {
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof DrawingElementProperties) == false) {
                return false;
            }
            DrawingElementProperties p = (DrawingElementProperties) obj;
            if (this.mVisible != p.mVisible) {
                return false;
            }
            return true;
        }

        public Boolean isVisible() {
            return Boolean.valueOf(this.mVisible);
        }

        public void setVisible(final boolean b) {
            this.mVisible = b;
        }
    }

}

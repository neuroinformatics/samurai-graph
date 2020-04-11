package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGIDisposable;
import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIVisible;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGIElementGroupConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base class for the group of the drawing elements. This object has an array of
 * drawing elements.
 */
public abstract class SGElementGroup implements SGIElementGroupConstants,
        SGIDrawingElementConstants, SGIVisible, SGIDisposable {

    /**
     * The array of drawing elements.
     */
    protected SGDrawingElement[] mDrawingElementArray = null;

    /**
     * The magnification.
     */
    protected float mMagnification = 1.0f;

    /**
     * The flag of visibility.
     */
    protected boolean mVisibleFlag = true;

    /**
     * The default constructor.
     * 
     */
    public SGElementGroup() {
        super();
    }

    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;
        if (this.mDrawingElementArray != null) {
            SGDrawingElement[] array = this.mDrawingElementArray;
            for (int ii = 0; ii < array.length; ii++) {
                array[ii].dispose();
            }
            this.mDrawingElementArray = null;
        }
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
     * 
     * @return
     */
    public boolean initDrawingElement(final int num) {
    	if (this.mDrawingElementArray != null) {
    		if (this.mDrawingElementArray.length == num) {
    			// do nothing
    			return true;
    		}
    	}
        SGDrawingElement[] array = new SGDrawingElement[num];
        for (int ii = 0; ii < num; ii++) {
            array[ii] = this.createDrawingElementInstance(ii);
        }
        this.mDrawingElementArray = array;
        return true;
    }

    /**
     * 
     * @return
     */
    protected abstract SGDrawingElement createDrawingElementInstance(final int index);

    /**
     * 
     * @return
     */
    public SGDrawingElement[] getDrawingElementArray() {
        return this.mDrawingElementArray;
    }

    /**
     * Returns the magnification.
     * 
     * @return the magnification
     */
    public float getMagnification() {
        return this.mMagnification;
    }

    /**
     * Sets the magnification.
     * 
     * @param mag
     *           the magnification to set
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        if (mag <= 0.0) {
            throw new IllegalArgumentException("mag <= 0.0");
        }
        this.mMagnification = mag;
        return true;
    }

    /**
     * Sets the visibility of this group.
     * 
     * @param b
     *          true to set visible
     */
    public void setVisible(final boolean b) {
        this.mVisibleFlag = b;
    }

    /**
     * Returns whether this group is visible.
     * 
     * @return true if this group is visible
     */
    public boolean isVisible() {
        return this.mVisibleFlag;
    }

    /**
     * Returns whether this group set contains the given point.
     * 
     * @param x
     *          x coordinate
     * @param y
     *          y coordinate
     * @return true if this element group contains the given point
     */
    public boolean contains(final int x, final int y) {
        if (this.mDrawingElementArray != null) {
            for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
            	SGDrawingElement el = this.mDrawingElementArray[ii];
                if (el.isVisible() == false) {
                    continue;
                }
                if (el.contains(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     */
    public boolean paintElement(final Graphics2D g2d) {
        return this.paintElement(g2d, null);
    }

    /**
     * 
     */
    public abstract boolean paintElement(final Graphics2D g2d,
            final Rectangle2D clipRect);

    /**
     * 
     * @return
     */
    public abstract String getTagName();

    /**
     * 
     * @param document
     * @return
     */
    public Element createElement(final Document document) {
        Element el = document.createElement(this.getTagName());
        if (this.writeProperty(el) == false) {
            return null;
        }
        return el;
    }

    /**
     * 
     */
    public abstract boolean writeProperty(final Element el);

    /**
     * 
     */
    public abstract boolean readProperty(final Element el);

    /**
     * Returns the properties of this object.
     * 
     * @return the properties
     */
    public SGProperties getProperties() {
        ElementGroupProperties p = new ElementGroupProperties();
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
    public boolean getProperties(SGProperties p) {
        if (p == null) {
            return false;
        }
        if ((p instanceof ElementGroupProperties) == false) {
        	return false;
        }
        ElementGroupProperties ep = (ElementGroupProperties) p;
        ep.setVisible(this.isVisible());
        return true;
    }

    /**
     * Sets the given properties to this object.
     * 
     * @param p
     *          the properties
     * @return true if succeeded
     */
    public boolean setProperties(SGProperties p) {
        if ((p instanceof ElementGroupProperties) == false) {
        	return false;
        }
        ElementGroupProperties ep = (ElementGroupProperties) p;
        final Boolean b = ep.isVisible();
        if (b == null) {
            return false;
        }
        this.setVisible(b.booleanValue());
        return true;
    }

    /**
     * Properties of a element group.
     */
    public static class ElementGroupProperties extends SGProperties {
    	
    	/**
    	 * The visibility flag.
    	 */
        private boolean mVisible;

        /**
         * 
         */
        public ElementGroupProperties() {
            super();
        }

        /**
         * Copy this object.
         * @return a copied object
         */
        public Object copy() {
            Object obj = super.copy();
            ElementGroupProperties p = (ElementGroupProperties) obj;
            return p;
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof ElementGroupProperties) == false) {
                return false;
            }
            ElementGroupProperties p = (ElementGroupProperties) obj;
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

    protected SGDrawingElement getElement(final int index) {
    	return this.mDrawingElementArray[index];
    }

}

package jp.riken.brain.ni.samuraigraph.base;

/**
 * A generic 2 element tuple that is represented by two double numbers.
 * 
 */
public class SGTuple2d implements Cloneable {
	
    /**
     * The first component.
     */
    public double x;

    /**
     * The second component.
     */
    public double y;

    /**
     * The default constructor.
     */
    public SGTuple2d() {
    	super();
    	this.clear();
    }

    /**
     * Constructs and initializes a SGTuple2d from the specified xy coordinates.
     * 
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     */
    public SGTuple2d(final double X, final double Y) {
    	super();
        this.x = X;
        this.y = Y;
    }

    /**
     * Constructs and initializes a SGTuple2d with a given tuple.
     * 
     * @param tuple
     *            a tuple
     */
    public SGTuple2d(final SGTuple2d tuple) {
    	super();
        this.x = tuple.x;
        this.y = tuple.y;
    }

    /**
     * Sets a value to the first component, x.
     * 
     * @param x
     *          a value to set to the first component.
     */
    public void setX(final double x) {
        this.x = x;
    }

    /**
     * Sets a value to the second component, y.
     * 
     * @param y
     *          a value to set to the second component.
     */
    public void setY(final double y) {
        this.y = y;
    }

    /**
     * Sets the values to both components.
     * 
     * @param x
     *          a value to set to the first component.
     * @param y
     *          a value to set to the second component.
     */
    public void setValues(final double X, final double Y) {
        this.x = X;
        this.y = Y;
    }

    /**
     * Sets the values to both components from a given tuple.
     * 
     * @param tuple
     *          a tuple
     */
    public void setValues(final SGTuple2d tuple) {
        if (tuple == null) {
            throw new IllegalArgumentException("tuple==null");
        }
        this.setValues(tuple.x, tuple.y);
    }

    /**
     * Returns a text string that represents this object.
     * 
     * @return a text string that represents this object
     */
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append('(');
    	sb.append(this.x);
    	sb.append(',');
    	sb.append(this.y);
    	sb.append(')');
        return sb.toString();
    }

    /**
     * Checks whether a given object is equal to this object.
     * Both of two components are equal to those of this object, a given object 
     * is equal to this.
     * 
     * @param obj
     *           an object to compare
     * @return true if a given object is equal to this
     */
    public boolean equals(final Object obj) {
        if ((obj instanceof SGTuple2d) == false) {
            return false;
        }
        SGTuple2d t = (SGTuple2d) obj;
        if (t.x != this.x || t.y != this.y) {
            return false;
        }
        return true;
    }

    /**
     * Clear both components.
     * 
     */
    public void clear() {
        this.x = 0.0;
        this.y = 0.0;
    }

    /**
     * Returns true if one or both of two components are infinite.
     * 
     * @return true if one or both of two components are not a number
     */
    public boolean isInfinite() {
        return Double.isInfinite(this.x) || Double.isInfinite(this.y);
    }

    /**
     * Returns true if one or both of two components are not a number.
     * 
     * @return true if one or both of two components are not a number
     */
    public boolean isNaN() {
        return Double.isNaN(this.x) || Double.isNaN(this.y);
    }

    /**
     * Returns true if both of two components are equal to zero.
     * 
     * @return true if both of two components are equal to zero
     */
    public boolean isZero() {
        return (this.x == 0.0) && (this.y == 0.0);
    }

    /**
     * Clones this object.
     * 
     * @return the copy of this object
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

}

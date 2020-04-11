package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.util.Iterator;

import org.w3c.dom.Element;

/**
 * A class that provides mapping between a double value and a color.
 *
 */
public abstract class SGColorMap implements Cloneable, SGIDisposable {

    /**
     * The axis.
     */
    protected SGAxis mAxis = null;

    /**
     * An array of colors.
     */
    protected Color[] mColors = null;
    
    /**
     * A flag whether the order of colors is revered.
     */
    protected boolean mReversedOrderFlag = false;

    public static final String KEY_COLOR_1 = "Color1";

    public static final String KEY_COLOR_2 = "Color2";

    public static final String KEY_REVERSED_ORDER = "ReversedOrder";
    
    public static final String COM_COLOR_MAP_COLOR_1 = "Color1";

    public static final String COM_COLOR_MAP_COLOR_2 = "Color2";

    public static final String COM_COLOR_MAP_REVERSED = "ReversedOrder";

    /**
     * The default constructor.
     *
     */
    public SGColorMap() {
        super();
        this.init();
    }
    
    /**
     * Builds a color map. 
     * 
     * @param colors
     *          an array of colors
     */
    public SGColorMap(final Color[] colors) {
        
        super();

        if (colors == null) {
            throw new IllegalArgumentException("Color array is null.");
        }
        if (colors.length == 0) {
            throw new IllegalArgumentException("Length of a given color array is zero.");
        }
        for (int ii = 0; ii < colors.length; ii++) {
            if (colors[ii] == null) {
                throw new IllegalArgumentException("Null value is included in a given array: " + ii);
            }
        }
        
        // set to attributes
        this.mColors = (Color[]) colors.clone();
        
        // initialize
        this.init();
    }
    
    private void init() {
        this.mReversedOrderFlag = false;
        this.mAxis = new SGAxis(0.0, 1.0);
    }
    
    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;
        this.mColors = null;
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
     * Clones this color bar model.
     * 
     * @return
     *         a copy of this object
     */
    public final Object clone() {
        try {
            SGColorMap model = (SGColorMap) super.clone();
            model.mAxis = (SGAxis) this.mAxis.clone();
            if (this.mColors != null) {
                model.mColors = (Color[]) this.mColors.clone();
            }
            return model;
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (!(obj instanceof SGColorMap)) {
    		return false;
    	}
    	SGColorMap map = (SGColorMap) obj;
    	if (!SGUtility.equals(this.mColors, map.mColors)) {
    		return false;
    	}
    	return true;
    }

    /**
     * Returns the axis.
     * 
     * @return the axis
     */
    public SGAxis getAxis() {
        return this.mAxis;
    }

    /**
     * Returns the minimum value.
     * 
     * @return the minimum value
     */
    public double getMinValue() {
        return this.mAxis.getMinDoubleValue();
    }
    
    /**
     * Returns the maximum value.
     * 
     * @return the maximum value
     */
    public double getMaxValue() {
        return this.mAxis.getMaxDoubleValue();
    }

    /**
     * Returns the scale type.
     * 
     * @return the scale type
     */
    public int getScaleType() {
        return this.mAxis.getScaleType();
    }
    
    /**
     * Returns whether the coordinate is inverted.
     * 
     * @return true if the coordinate is inverted
     */
    public boolean isInvertCoordinates() {
        return this.mAxis.isInvertCoordinates();
    }
    
    /**
     * Sets the value range. The minimum value corresponds to the first element 
     * and the maximum value corresponds to the last element respectively of given color array.
     * 
     * @param min
     *           the minimum value
     * @param max
     *           the maximum value
     */
    public void setValueRange(final double min, final double max) {
        this.setValueRange(min, max, this.mAxis.getScaleType());
    }
    
    /**
     * Sets the value range and the scale type.
     * 
     * @param min
     *           the minimum value
     * @param max
     *           the maximum value
     * @param scaleType
     *           the scale type
     */
    public void setValueRange(final double min, final double max, 
            final int scaleType) {
        this.mAxis.setScale(min, max, scaleType);
    }

    /**
     * Sets whether the coordinate is inverted.
     * 
     * @param b
     *          true to be inverted
     */
    public void setInvertCoordinates(final boolean b) {
        this.mAxis.setInvertCoordinates(b);
    }
    
    /**
     * Returns an array of colors.
     * 
     * @return an array of colors
     */
    public Color[] getColors() {
        if (this.mColors != null) {
            return (Color[]) this.mColors.clone();
        } else {
            return null;
        }
    }
    
    /**
     * Returns whether the order of colors is reversed.
     * 
     * @return whether the order of colors is reversed
     */
    public boolean isReversedOrder() {
        return this.mReversedOrderFlag;
    }
    
    /**
     * Sets whether the order of colors is reversed.
     * 
     * @param b
     *          true to reverse the order of colors
     */
    public void setReversedOrder(final boolean b) {
        this.mReversedOrderFlag = b;
    }

    /**
     * Evaluate a given value with the color map and returns a corresponding color.
     * 
     * @param value
     *           a value to evaluate
     * @return corresponding color
     */
    public Color evaluate(final double value) {
        final int scaleType = this.getScaleType();
        return this.evaluate(value, scaleType);
    }
    
    /**
     * Evaluate a given value with the color map and returns a corresponding color.
     * 
     * @param value
     *           a value to evaluate
     * @param scaleType
     *           the scale type
     * @return a corresponding color
     */
    public Color evaluate(final double value, final int scaleType) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Invalid input value: " + value);
        }
        final double minValue = this.getMinValue();
        final double maxValue = this.getMaxValue();
        final double valueInScale, minValueInScale, maxValueInScale;
        switch (scaleType) {
        case SGAxis.LINEAR_SCALE :
            valueInScale = value;
            minValueInScale = minValue;
            maxValueInScale = maxValue;
            break;
        case SGAxis.LOG_SCALE :
            if (value <= 0.0) {
                throw new IllegalArgumentException("Not positive value for log scale: " + value);
            }
            valueInScale = Math.log10(value);
            minValueInScale = Math.log10(minValue);
            maxValueInScale = Math.log10(maxValue);
            break;
        default :
            throw new IllegalArgumentException("Invalid scale type: " + scaleType);
        }
        final double ratioAll = (valueInScale - minValueInScale)
                / (maxValueInScale - minValueInScale);
        return this.eval(ratioAll);
    }
    
    /**
     * Returns the color in an array of colors of attribute.
     * 
     * @param index
     *           the array index
     * @return the array element
     */
    protected Color getColor(final int index) {
        if (this.mReversedOrderFlag) {
            // return the last element
            return this.mColors[this.mColors.length - 1 - index];
        } else {
            // returns the first element
            return this.mColors[index];
        }
    }
    
    /**
     * Returns the corresponding color for a given value.
     * The input value should be in a scale that the minimum value and the maximum value
     * in the current scale is given by 0.0 and 1.0, respectively.
     * 
     * @param value
     *           a value
     * @return the corresponding color
     */
    public abstract Color eval(final double value);
    
    /**
     * Sets the colors.
     * 
     * @param colors
     *           the colors to set
     */
    public void setColors(Color[] colors) {
    	this.mColors = colors.clone();
    }

    /**
     * The base class of the properties of the color map.
     *
     */
    public static class ColorMapProperties extends SGProperties {
    	
    	Color[] colors = null;
    	
    	boolean reversedOrder = false;
    	
    	public ColorMapProperties() {
    		super();
    	}

    	@Override
    	public boolean equals(Object obj) {
    		if (!(obj instanceof ColorMapProperties)) {
    			return false;
    		}
    		ColorMapProperties cp = (ColorMapProperties) obj;
    		if (!SGUtility.equals(this.colors, cp.colors)) {
    			return false;
    		}
    		if (this.reversedOrder != cp.reversedOrder) {
    			return false;
    		}
    		return true;
    	}
    	
    	@Override
    	public Object copy() {
    		ColorMapProperties cp = (ColorMapProperties) super.copy();
    		cp.colors = (this.colors != null) ? this.colors.clone() : null;
    		return cp;
    	}
    	
    	public boolean isReversedOrder() {
    		return this.reversedOrder;
    	}
    }

    /**
     * Returns the properties.
     * 
     * @return the properties
     */
    public SGProperties getProperties() {
    	ColorMapProperties p = new ColorMapProperties();
    	if (!this.getProperties(p)) {
    		return null;
    	}
    	return p;
    }

    /**
     * Retrieves the properties and places them into given property object.
     *  
     * @param p
     *          the properties
     * @return true if succeeded
     */
    public boolean getProperties(SGProperties p) {
    	if (!(p instanceof ColorMapProperties)) {
    		return false;
    	}
    	ColorMapProperties cp = (ColorMapProperties) p;
    	cp.colors = (this.mColors != null) ? this.mColors.clone() : null;
    	cp.reversedOrder = this.isReversedOrder();
    	return true;
    }

    /**
     * Sets the properties to this object.
     * 
     * @param p
     *           the properties
     * @return true if succeeded
     */
    public boolean setProperties(SGProperties p) {
    	if (!(p instanceof ColorMapProperties)) {
    		return false;
    	}
    	ColorMapProperties cp = (ColorMapProperties) p;
    	this.mColors = (cp.colors != null) ? cp.colors.clone() : null;
    	this.mReversedOrderFlag = cp.reversedOrder;
    	return true;
    }
    
    /**
     * Writes properties of this color map to a given Element.
     * 
     * @param el
     *          an Element
     * @return true if succeeded
     */
    public boolean writeProperty(Element el, SGExportParameter params) {
    	SGPropertyMap map = this.getPropertyFileMap(params);
    	map.setToElement(el);
    	return true;
    }

    /**
     * Reads properties from given Element and set to this object.
     * 
     * @param el
     *           an Element object
     * @return true if succeeded
     */
    public boolean readProperty(Element el) {
    	String str = null;
    	str = el.getAttribute(SGColorMap.KEY_REVERSED_ORDER);
    	if (str.length() != 0) {
    		Boolean reversed = SGUtilityText.getBoolean(str);
    		if (reversed == null) {
    			return false;
    		}
    		this.setReversedOrder(reversed.booleanValue());
    	}
    	return true;
    }
    
    /**
     * Sets the properties.
     *
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    public final SGPropertyResults setProperties(SGPropertyMap map) {
        return this.setProperties(map, new SGPropertyResults());
    }
    
    /**
	 * Sets the properties.
	 * 
	 * @param map
	 *            a map of properties
	 * @param iResult
	 *            the input result
	 * @return the updated result of setting properties
	 */
    public SGPropertyResults setProperties(SGPropertyMap map,
    		SGPropertyResults iResult) {
        SGPropertyResults result = (SGPropertyResults) iResult.clone();
        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);
            if (COM_COLOR_MAP_REVERSED.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_COLOR_MAP_REVERSED,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                this.setReversedOrder(b.booleanValue());
                result.putResult(COM_COLOR_MAP_REVERSED,
                        SGPropertyResults.SUCCEEDED);
            }
        }
        return result;
    }

    /**
     * Creates and returns the map of properties.
     * 
     * @return the map of properties
     */
    public SGPropertyMap getPropertyMap() {
    	SGPropertyMap map = new SGPropertyMap();
    	SGPropertyUtility.addProperty(map, COM_COLOR_MAP_REVERSED, this.isReversedOrder());
    	return map;
    }
    
    /**
     * Creates and returns the map of properties for the property file.
     * 
     * @return the map of properties
     */
    public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	SGPropertyUtility.addProperty(map, KEY_REVERSED_ORDER, this.isReversedOrder());
    	return map;
    }
}

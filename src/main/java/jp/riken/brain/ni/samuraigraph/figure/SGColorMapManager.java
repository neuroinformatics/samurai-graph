package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class that manages color maps.
 * 
 */
public abstract class SGColorMapManager {

    /**
     * A map of color maps.
     */
    protected Map<String, SGColorMap> mColorMaps = new HashMap<String, SGColorMap>();

    /**
     * An array of colors in order of hue.
     */
    public static Color[] HUE_COLOR_ARRAY = { Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED };

    public static final String TAG_NAME_COLOR_STYLES = "ColorStyles";

    public static final String TAG_NAME_COLOR_STYLE = "ColorStyle";

    public static final String KEY_COLOR_STYLE_NAME = "Name";
    
    /**
     * The default constructor.
     *
     */
    public SGColorMapManager() {
        super();
    }
    
    /**
     * Returns the color map.
     * 
     * @param name
     *          the name of color map
     * @return a color map of given name if it exists
     */
    public SGColorMap getColorMap(final String name) {
    	if (name == null) {
    		return null;
    	}
    	Set<String> keys = this.mColorMaps.keySet();
    	for (String key : keys) {
    		if (SGUtilityText.isEqualString(key, name)) {
    	        return this.mColorMaps.get(key);
    		}
    	}
    	return null;
    }

    /**
     * Adds a color map. If a color map of the same name already
     * exists, it is removed.
     * 
     * @param name
     *            the name of color map
     * @param map
     *            the color map
     * @return true if succeeded
     */
    public boolean addColorMap(final String name,
            final SGColorMap map) {

        // checks the input values
        if (name == null || map == null) {
            throw new IllegalArgumentException("name == null || model == null");
        }
        
        // sets to an attribute
        this.mColorMaps.put(name, map);

        return true;
    }
    
    /**
     * Returns the name of the color map.
     * 
     * @param map
     *           the color map
     * @return the name of a given color map if it exists
     */
    public String getColorMapName(final SGColorMap map) {
        String name = null;
        Set<String> keys = this.mColorMaps.keySet();
        for (String key : keys) {
            SGColorMap m = this.mColorMaps.get(key);
            if (m.equals(map)) {
                name = key;
                break;
            }
        }
        return name;
    }

    /**
     * Sets whether the order of colors is reversed.
     * 
     * @param b
     *          true to reverse the order of colors
     */
    public void setReversedOrder(final boolean b) {
        Iterator<SGColorMap> itr = this.mColorMaps.values().iterator();
        while (itr.hasNext()) {
            itr.next().setReversedOrder(b);
        }
    }

    public void setReversedOrder(final String name, final boolean b) {
    	SGColorMap colorMap = this.mColorMaps.get(name);
    	colorMap.setReversedOrder(b);
    }

    /**
     * A color bar model that gives discretely varying colors.
     * 
     */
    public static class DiscreteColorMap extends SGColorMap {
        public DiscreteColorMap(final Color[] colors) {
            super(colors);
        }
        
        @Override
        public Color eval(final double value) {
            if (value <= 0.0) {
                return this.getColor(0);
            } else if (value >= 1.0) {
                return this.getColor(this.mColors.length - 1);
            } else {
                final double indexValue = value * (this.mColors.length - 1) + 0.50;
                final int index = (int) (indexValue);
                return this.getColor(index);
            }
        }
        
        @Override
        public boolean equals(Object obj) {
        	if (!(obj instanceof DiscreteColorMap)) {
        		return false;
        	}
        	if (!super.equals(obj)) {
        		return false;
        	}
        	return true;
        }
    }

    /**
     * A color bar model that gives gradationally varying colors.
     * 
     */
    public static class GradationalColorMap extends SGColorMap {
        public GradationalColorMap(final Color[] colors) {
            super(colors);
        }
        
        @Override
        public Color eval(final double value) {
            if (value <= 0.0) {
                return this.getColor(0);
            } else if (value >= 1.0) {
                return this.getColor(this.mColors.length - 1);
            } else {
                final double indexValue = value * (this.mColors.length - 1) + 0.50;
                final int index = (int) (indexValue);
                final double ratioSingle = indexValue - index;
                if (index == 0 && ratioSingle < 0.50) {
                    return this.getColor(0);
                } else if (index == this.mColors.length - 1 && ratioSingle >= 0.50) {
                    return this.getColor(this.mColors.length - 1);
                } else {
                    // linear interpolation
                    final int index1, index2;
                    final double r;
                    if (ratioSingle < 0.50) {
                        index1 = index - 1;
                        index2 = index;
                        r = 0.50 + ratioSingle;
                    } else {
                        index1 = index;
                        index2 = index + 1;
                        r = ratioSingle - 0.50;
                    }
                    final Color col1 = this.getColor(index1);
                    final Color col2 = this.getColor(index2);
                    final double red = this.interpolate(col1.getRed(), col2.getRed(), r);
                    final double green = this.interpolate(col1.getGreen(), col2.getGreen(), r);
                    final double blue = this.interpolate(col1.getBlue(), col2.getBlue(), r);
                    final double alpha = this.interpolate(col1.getAlpha(), col2.getAlpha(), r);
                    Color col = new Color((int) red, (int) green, (int) blue, (int) alpha);
                    return col;
                }
            }
        }
        
        private double interpolate(final double value1, final double value2, final double r) {
        	return (1.0 - r) * value1 + r * value2;
        }
        
        @Override
        public boolean equals(Object obj) {
        	if (!(obj instanceof GradationalColorMap)) {
        		return false;
        	}
        	if (!super.equals(obj)) {
        		return false;
        	}
        	return true;
        }
        
        public static final String KEY_COLOR_1 = "Color1";

        public static final String KEY_COLOR_2 = "Color2";

        /**
         * Creates and returns the map of properties for the property file.
         * 
         * @return the map of properties
         */
        @Override
        public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
        	SGPropertyMap map = super.getPropertyFileMap(params);
        	Color[] colors = this.getColors();
        	if (colors != null) {
            	SGPropertyUtility.addProperty(map, KEY_COLOR_1, colors[0]);
            	SGPropertyUtility.addProperty(map, KEY_COLOR_2, colors[1]);
        	}
        	return map;
        }

        /**
         * Creates and returns the map of properties.
         * 
         * @return the map of properties
         */
        @Override
        public SGPropertyMap getPropertyMap() {
        	SGPropertyMap map = super.getPropertyMap();
        	Color[] colors = this.getColors();
        	if (colors != null) {
            	SGPropertyUtility.addProperty(map, COM_COLOR_MAP_COLOR_1, colors[0]);
            	SGPropertyUtility.addProperty(map, COM_COLOR_MAP_COLOR_2, colors[1]);
        	}
        	return map;
        }

        /**
         * Reads properties from given Element and set to this object.
         * 
         * @param el
         *           an Element object
         * @return true if succeeded
         */
        @Override
        public boolean readProperty(Element el) {
        	if (super.readProperty(el) == false) {
        		return false;
        	}
        	String str = null;
        	Color color = null;
        	str = el.getAttribute(KEY_COLOR_1);
        	if (str.length() != 0) {
        		color = SGUtilityText.parseColor(str);
        		if (color == null) {
        			return false;
        		}
        		this.mColors[0] = color;
        	}
        	str = el.getAttribute(KEY_COLOR_2);
        	if (str.length() != 0) {
        		color = SGUtilityText.parseColor(str);
        		if (color == null) {
        			return false;
        		}
        		this.mColors[1] = color;
        	}
        	return true;
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
        @Override
        public SGPropertyResults setProperties(SGPropertyMap map,
        		SGPropertyResults iResult) {
            SGPropertyResults result = super.setProperties(map, iResult);
            Iterator<String> itr = map.getKeyIterator();
            while (itr.hasNext()) {
                String key = itr.next();
                String value = map.getValueString(key);
                if (COM_COLOR_MAP_COLOR_1.equalsIgnoreCase(key)) {
	                Color cl = SGUtilityText.parseColorText(value);
	                if (cl == null) {
    					result.putResult(COM_COLOR_MAP_COLOR_1,
    							SGPropertyResults.INVALID_INPUT_VALUE);
    					continue;
	                }
	                this.mColors[0] = cl;
	                result.putResult(COM_COLOR_MAP_COLOR_1, SGPropertyResults.SUCCEEDED);
                } else if (COM_COLOR_MAP_COLOR_2.equalsIgnoreCase(key)) {
	                Color cl = SGUtilityText.parseColorText(value);
	                if (cl == null) {
    					result.putResult(COM_COLOR_MAP_COLOR_2,
    							SGPropertyResults.INVALID_INPUT_VALUE);
    					continue;
	                }
	                this.mColors[1] = cl;
	                result.putResult(COM_COLOR_MAP_COLOR_2, SGPropertyResults.SUCCEEDED);
                }
            }
            return result;
        }
    }
    
    /**
     * A color bar model that gives gradationally varying colors as hue changes
     * from blue to red.
     * 
     */
    public static class HueColorMap extends SGColorMap {
        public HueColorMap() {
            super();
        }

        @Override
        public Color eval(final double value) {
            final double valueNew;
            if (value <= 0.0) {
                valueNew = 0.0;
            } else if (value >= 1.0) {
                valueNew = 1.0;
            } else {
                valueNew = value;
            }
            final double v = this.mReversedOrderFlag ? valueNew : (1.0 - valueNew);
            final float r = (float) ((2.0 / 3.0) * v);
            Color cl = new Color(Color.HSBtoRGB(r, 1.0f, 1.0f));
            return cl;
        }
        
        @Override
        public boolean equals(Object obj) {
        	if (!(obj instanceof HueColorMap)) {
        		return false;
        	}
        	if (!super.equals(obj)) {
        		return false;
        	}
        	return true;
        }
    }
    
    /**
     * A color bar model that gives repeated varying colors.
     * 
     */
    public abstract static class RepeatedColorMap extends SGColorMap {

        /**
         * The number of repetition.
         * 
         */
        protected int mRepeatedNum;
        
        /**
         * A color bar model.
         */
        protected SGColorMap mModel;
        
        /**
         * Builds this color bar model.
         * 
         * @param colors
         *           an array of colors
         * @param repeatNum
         *           the number of repetition
         */
        public RepeatedColorMap(final Color[] colors, final int repeatNum) {
            super();
            this.mRepeatedNum = repeatNum;
            this.mModel = this.createInstance(colors);
        }
        
        @Override
        public Color eval(final double value) {
            final int digit = (int) (value);
            final float valueNew = (float) (value - digit);
            final int digitNew = (int) (valueNew * mRepeatedNum);
            final float valueNew2 = (float) (valueNew * mRepeatedNum - digitNew);
            return this.mModel.eval(valueNew2);
        }
        
        @Override
        public final boolean equals(Object obj) {
        	if (!(obj instanceof RepeatedColorMap)) {
        		return false;
        	}
        	if (!super.equals(obj)) {
        		return false;
        	}
        	RepeatedColorMap map = (RepeatedColorMap) obj;
        	if (!SGUtility.equals(this.mModel, map.mModel)) {
        		return false;
        	}
        	if (this.mRepeatedNum != map.mRepeatedNum) {
        		return false;
        	}
        	return true;
        }
        
        @Override
        public boolean isReversedOrder() {
        	return this.mModel.isReversedOrder();
        }

        @Override
        public void setReversedOrder(final boolean b) {
            super.setReversedOrder(b);
            this.mModel.setReversedOrder(b);
        }
        
        /**
         * Overrode to get colors from the model in an attribute.
         * 
         * @return an array of colors
         */
        @Override
        public Color[] getColors() {
        	return this.mModel.getColors();
        }
        
        /**
         * Overrode to set colors to the model in an attribute.
         * 
         * @param colors
         *           an array of colors
         */
        @Override
        public void setColors(Color[] colors) {
        	this.mModel.setColors(colors);
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
        @Override
        public SGPropertyResults setProperties(SGPropertyMap map,
        		SGPropertyResults iResult) {
            SGPropertyResults result = super.setProperties(map, iResult);
            return this.mModel.setProperties(map, result);
        }

        protected abstract SGColorMap createInstance(final Color[] colors);
        
        public static class RepeatedColorMapProperties extends ColorMapProperties {
        	
        	int repeatedNum;
        	
        	ColorMapProperties colorMapProperties = null;
        	
        	public RepeatedColorMapProperties() {
        		super();
        	}

        	@Override
        	public boolean equals(Object obj) {
        		if (!(obj instanceof RepeatedColorMapProperties)) {
        			return false;
        		}
        		if (!super.equals(obj)) {
        			return false;
        		}
        		RepeatedColorMapProperties cp = (RepeatedColorMapProperties) obj;
        		if (!SGUtility.equals(this.colorMapProperties, cp.colorMapProperties)) {
        			return false;
        		}
        		if (this.repeatedNum != cp.repeatedNum) {
        			return false;
        		}
        		return true;
        	}
        	
        	@Override
        	public Object copy() {
        		RepeatedColorMapProperties cp = (RepeatedColorMapProperties) super.copy();
				cp.colorMapProperties = (this.colorMapProperties != null) ? (ColorMapProperties) this.colorMapProperties
						.clone() : null;
        		return cp;
        	}
        }
        
        /**
         * Returns the properties.
         * 
         * @return the properties
         */
        @Override
        public SGProperties getProperties() {
        	RepeatedColorMapProperties p = new RepeatedColorMapProperties();
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
        @Override
        public boolean getProperties(SGProperties p) {
        	if (!(p instanceof RepeatedColorMapProperties)) {
        		return false;
        	}
        	if (!super.getProperties(p)) {
        		return false;
        	}
        	RepeatedColorMapProperties rp = (RepeatedColorMapProperties) p;
        	rp.colorMapProperties = (ColorMapProperties) this.mModel.getProperties();
        	rp.repeatedNum = this.mRepeatedNum;
        	return true;
        }

        /**
         * Sets the properties to this object.
         * 
         * @param p
         *           the properties
         * @return true if succeeded
         */
        @Override
        public boolean setProperties(SGProperties p) {
        	if (!(p instanceof RepeatedColorMapProperties)) {
        		return false;
        	}
        	if (super.setProperties(p) == false) {
        		return false;
        	}
        	RepeatedColorMapProperties rp = (RepeatedColorMapProperties) p;
        	if (!this.mModel.setProperties(rp.colorMapProperties)) {
        		return false;
        	}
        	this.mRepeatedNum = rp.repeatedNum;
        	return true;
        }

        /**
         * Creates and returns the map of properties for the property file.
         * 
         * @return the map of properties
         */
        @Override
        public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
        	SGPropertyMap map = super.getPropertyFileMap(params);
        	map.putAll(this.mModel.getPropertyFileMap(params));
        	return map;
        }

        /**
         * Creates and returns the map of properties.
         * 
         * @return the map of properties
         */
        @Override
        public SGPropertyMap getPropertyMap() {
        	SGPropertyMap map = super.getPropertyMap();
        	map.putAll(this.mModel.getPropertyMap());
        	return map;
        }

        /**
         * Reads properties from given Element and set to this object.
         * 
         * @param el
         *           an Element object
         * @return true if succeeded
         */
        @Override
        public boolean readProperty(Element el) {
        	if (super.readProperty(el) == false) {
        		return false;
        	}
        	if (this.mModel.readProperty(el) == false) {
        		return false;
        	}
        	return true;
        }
    }
    
    /**
     * A color bar model that gives repeated gradationally varying colors.
     * 
     */
    public static class RepeatedGradationalColorMap extends RepeatedColorMap {
        public RepeatedGradationalColorMap(final Color[] colors, final int repeatNum) {
            super(colors, repeatNum);
        }
        
        @Override
        protected SGColorMap createInstance(final Color[] colors) {
            return new GradationalColorMap(colors);
        }
    }
    
    /**
     * A color bar model that contains multiple color bar models.
     * 
     */
    public abstract static class MultipleColorMap extends SGColorMap {
        /**
         * Color bar models.
         */
        protected SGColorMap[] mModelArray = null;
        
        public MultipleColorMap(final Color[] startColors, final Color endColor) {
            super();
            Color[][] colors = new Color[startColors.length][];
            for (int ii = 0; ii < colors.length; ii++) {
            	colors[ii] = new Color[] {startColors[ii], endColor};
            }
            this.mModelArray = new SGColorMap[colors.length];
            for (int i = 0; i < colors.length; i++) {
                this.mModelArray[i] = this.createInstance(colors[i]);
            }
        }
        
        @Override
		public Color eval(final double value) {
			if (value <= 0.0) {
				if (!isReversedOrder()) {
					return this.mModelArray[0].eval(0);
				} else {
					return this.mModelArray[this.mModelArray.length - 1].eval(0.0);
				}
			} else if (value >= 1.0) {
				if (!isReversedOrder()) {
					return this.mModelArray[this.mModelArray.length - 1].eval(1.0);
				} else {
					return this.mModelArray[0].eval(1.0);
				}
			} else {
				final int digit = (int) (value);
				final double valueNew = (value - digit);
				final int digitNew = (int) (valueNew * this.mModelArray.length);
				final double valueNew2 = (valueNew * this.mModelArray.length - digitNew);
				if (!isReversedOrder()) {
					return this.mModelArray[digitNew].eval(valueNew2);
				} else {
					return this.mModelArray[this.mModelArray.length - 1 - digitNew].eval(valueNew2);
				}
			}
		}
        
        @Override
        public final boolean equals(Object obj) {
        	if (!(obj instanceof MultipleColorMap)) {
        		return false;
        	}
        	if (!super.equals(obj)) {
        		return false;
        	}
        	MultipleColorMap map = (MultipleColorMap) obj;
    		if (!SGUtility.equals(this.mModelArray, map.mModelArray)) {
    			return false;
    		}
        	return true;
        }
        
        @Override
        public boolean isReversedOrder() {
        	return this.mModelArray[0].isReversedOrder();
        }
        
        @Override
        public void setReversedOrder(final boolean b) {
            super.setReversedOrder(b);
            for (int i = 0; i < this.mModelArray.length; i++) {
                this.mModelArray[i].setReversedOrder(b);
            }
        }
        
        protected abstract SGColorMap createInstance(final Color[] colors);
        
        public static class MultipleColorMapProperties extends ColorMapProperties {
        	
        	ColorMapProperties[] colorMapProperties = null;
        	
        	public MultipleColorMapProperties() {
        		super();
        	}

        	@Override
        	public boolean equals(Object obj) {
        		if (!(obj instanceof MultipleColorMapProperties)) {
        			return false;
        		}
        		if (!super.equals(obj)) {
        			return false;
        		}
        		MultipleColorMapProperties cp = (MultipleColorMapProperties) obj;
        		if (!SGUtility.equals(this.colorMapProperties, cp.colorMapProperties)) {
        			return false;
        		}
        		return true;
        	}
        	
        	@Override
        	public Object copy() {
        		MultipleColorMapProperties cp = (MultipleColorMapProperties) super.copy();
        		ColorMapProperties[] colorMaps = null;
        		if (this.colorMapProperties != null) {
        			colorMaps = new ColorMapProperties[this.colorMapProperties.length];
        			for (int ii = 0; ii < colorMaps.length; ii++) {
        				ColorMapProperties p = this.colorMapProperties[ii];
        				colorMaps[ii] = (p != null) ? (ColorMapProperties) p.clone() : null;
        			}
        		}
        		cp.colorMapProperties = colorMaps;
        		return cp;
        	}
        }
        
        /**
         * Returns the properties.
         * 
         * @return the properties
         */
        @Override
        public SGProperties getProperties() {
        	MultipleColorMapProperties p = new MultipleColorMapProperties();
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
        @Override
        public boolean getProperties(SGProperties p) {
        	if (!(p instanceof MultipleColorMapProperties)) {
        		return false;
        	}
        	if (!super.getProperties(p)) {
        		return false;
        	}
        	MultipleColorMapProperties mp = (MultipleColorMapProperties) p;
        	mp.colorMapProperties = new ColorMapProperties[this.mModelArray.length];
        	for (int ii = 0; ii < this.mModelArray.length; ii++) {
        		mp.colorMapProperties[ii] = (ColorMapProperties) this.mModelArray[ii].getProperties();
        	}
        	return true;
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
        @Override
        public SGPropertyResults setProperties(SGPropertyMap map,
        		SGPropertyResults iResult) {
            SGPropertyResults supResult = super.setProperties(map, iResult);
            SGPropertyResults ret = null;
            for (int ii = 0; ii < this.mModelArray.length; ii++) {
            	SGPropertyResults result = this.mModelArray[ii].setProperties(map, supResult);
            	if (ii == 0) {
            		ret = result;
            	}
            }
            return ret;
        }

        /**
         * Sets the properties to this object.
         * 
         * @param p
         *           the properties
         * @return true if succeeded
         */
        @Override
        public boolean setProperties(SGProperties p) {
        	if (!(p instanceof MultipleColorMapProperties)) {
        		return false;
        	}
        	if (super.setProperties(p) == false) {
        		return false;
        	}
        	MultipleColorMapProperties mp = (MultipleColorMapProperties) p;
        	for (int ii = 0; ii < this.mModelArray.length; ii++) {
        		if (!this.mModelArray[ii].setProperties(mp.colorMapProperties[ii])) {
        			return false;
        		}
        	}
        	return true;
        }

    }
    
    /**
     * A color bar model that contains multiple color bar models
     * which give gradationally varying colors.
     * 
     */
    public static class MultipleGradationalColorMap extends MultipleColorMap {
        public MultipleGradationalColorMap(final Color[] startColors, final Color endColor) {
            super(startColors, endColor);
        }
        
        @Override
        protected SGColorMap createInstance(final Color[] colors) {
            return new GradationalColorMap(colors);
        }
    }
    
    /**
     * A color bar model that gives repeated gradationally varying colors 
     * as hue changes from blue to red.
     *
     */
    public static class RepeatedHueColorMap extends RepeatedColorMap {
        public RepeatedHueColorMap(final int repeatNum) {
            super(null, repeatNum);
        }

        @Override
        protected SGColorMap createInstance(final Color[] colors) {
            return new HueColorMap();
        }
    }

    /**
     * A color bar model that gives repeated discretely varying colors.
     * 
     */
    public static class RepeatedDiscreteColorMap extends RepeatedColorMap {
        public RepeatedDiscreteColorMap(final Color[] colors, final int repeatNum) {
            super(colors, repeatNum);
        }
        
        @Override
        protected SGColorMap createInstance(final Color[] colors) {
            return new DiscreteColorMap(colors);
        }
    }

    /**
     * Returns the map of color maps.
     * 
     * @return the map of color maps
     */
    public Map<String, SGColorMap> getColorMaps() {
    	return new HashMap<String, SGColorMap>(this.mColorMaps);
    }
    
    public boolean setColorMapProperties(Map<String, SGProperties> colorMapProperties) {
    	Iterator<Entry<String, SGProperties>> itr = colorMapProperties.entrySet().iterator();
    	while (itr.hasNext()) {
    		Entry<String, SGProperties> entry = itr.next();
    		String name = entry.getKey();
    		SGProperties p = entry.getValue();
    		SGColorMap colorMap = this.getColorMap(name);
    		if (!colorMap.setProperties(p)) {
    			return false;
    		}
    	}
    	return true;
    }
    
    public Map<String, SGProperties> getColorMapProperties() {
    	Map<String, SGProperties> colorMapProperties = new HashMap<String, SGProperties>();
    	Iterator<Entry<String, SGColorMap>> itr = this.mColorMaps.entrySet().iterator();
    	while (itr.hasNext()) {
    		Entry<String, SGColorMap> entry = itr.next();
    		String name = entry.getKey();
    		SGColorMap colorMap = entry.getValue();
    		colorMapProperties.put(name, colorMap.getProperties());
    	}
    	return colorMapProperties;
    }

    public Element createElement(Document document, SGExportParameter params) {
        Element elColorMaps = document.createElement(SGColorMapManager.TAG_NAME_COLOR_STYLES);
        if (this.writeProperty(elColorMaps, params) == false) {
        	return null;
        }
        String[] names = this.getColorMapNameArray();
        Map<String, SGColorMap> colorMaps = this.getColorMaps();
        for (String name : names) {
            Element elColorMap = document.createElement(SGColorMapManager.TAG_NAME_COLOR_STYLE);
        	SGColorMap colorMap = colorMaps.get(name);
            elColorMap.setAttribute(SGColorMapManager.KEY_COLOR_STYLE_NAME, name);
            if (colorMap.writeProperty(elColorMap, params) == false) {
            	return null;
            }
            elColorMaps.appendChild(elColorMap);
        }
        return elColorMaps;
    }
    
    protected abstract String[] getColorMapNameArray();
    
    /**
     * Writes properties of this color map to a given Element.
     * 
     * @param el
     *          an Element
     * @return true if succeeded
     */
    public boolean writeProperty(Element el, SGExportParameter params) {
    	// do nothing by default
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
    	NodeList colorMapNodes = el.getChildNodes();
    	for (int ii = 0; ii < colorMapNodes.getLength(); ii++) {
    		Node node = colorMapNodes.item(ii);
    		if (node instanceof Element) {
    			Element elColorMap = (Element) node;
    			String style = elColorMap.getAttribute(KEY_COLOR_STYLE_NAME);
    			SGColorMap colorMap = this.mColorMaps.get(style);
    			if (colorMap != null) {
    				if (colorMap.readProperty(elColorMap) == false) {
    					return false;
    				}
    			}
    		}
    	}
    	
    	return true;
    }

    /**
     * Returns whether it is available to assign colors to the color map of given name.
     * 
     * @param name
     *           the name of the color map
     * @return true if it is available to assign colors to the color map of given name
     */
	public abstract boolean isColorAssignable(final String name);

	/**
	 * Returns whether the color map of given name can accept given colors.
	 * 
	 * @param name
     *           the name of the color map
	 * @param colors
	 *           an array of colors
	 * @return 
	 */
	public boolean canAcceptColors(final String name, final Color[] colors) {
		if (name == null) {
			throw new IllegalArgumentException("name == null");
		}
		if (colors == null) {
			throw new IllegalArgumentException("colors == null");
		}
		if (this.isColorAssignable(name) == false) {
			return false;
		}
		return true;
	}
}

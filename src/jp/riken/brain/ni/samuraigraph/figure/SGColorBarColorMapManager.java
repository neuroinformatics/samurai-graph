package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

import org.w3c.dom.Element;

/**
 * A class that manages color maps of the color bar.
 * 
 */
public class SGColorBarColorMapManager extends SGColorMapManager {

    public static final String COLOR_MAP_NAME_COLORS = "Colors";
    
    public static final String COLOR_MAP_NAME_HUE_GRADATION = "Hue Gradation";
    
    public static final String COLOR_MAP_NAME_TWO_COLORS = "Two Colors";

    public static final String COLOR_MAP_NAME_COLOR_GRAY_SCALE = "Color/Gray Scale";
    
    public static final String COLOR_MAP_NAME_COLOR_SAW = "Color Saw";
    
    public static final String COLOR_MAP_NAME_COLORS_SCALE_WHITE = "Colors Scale White";
    
    public static final String COLOR_MAP_NAME_COLORS_SCALE_BLACK = "Colors Scale Black";
    
    public static final String COLOR_MAP_NAME_HUE_GRADATION_SAW = "Hue Gradation Saw";
    
    // for backward compatibility for version <= 2.0.0
    public static final String COLOR_MAP_NAME_GRADIENT_COLORS = "Gradient Colors";

    // for backward compatibility for version <= 2.0.0
    public static final String COLOR_MAP_NAME_GRAY_SCALE = "Gray Scale";

    // for backward compatibility for version <= 2.0.0
    public static final String COLOR_MAP_NAME_BLACK_AND_WHITE = "Black and White";

    // for backward compatibility for version <= 2.0.0
    public static final String COLOR_MAP_NAME_GRAY_SCALE_SAW = "Gray Scale Saw";
    
    // for backward compatibility for version <= 2.0.0
    public static final String COLOR_MAP_NAME_GRADIENT_COLORS_SAW = "Gradient Colors Saw";

    /**
     * The default color bar names.
     */
    public static final String[] DEFAULT_COLOR_MAP_NAMES = {
            COLOR_MAP_NAME_COLORS,
            COLOR_MAP_NAME_HUE_GRADATION,		// COLOR_MAP_NAME_GRADIENT_COLORS,
            COLOR_MAP_NAME_TWO_COLORS,			// COLOR_MAP_NAME_BLACK_AND_WHITE,
            COLOR_MAP_NAME_COLOR_GRAY_SCALE,	// COLOR_MAP_NAME_GRAY_SCALE,
            COLOR_MAP_NAME_COLOR_SAW,			// COLOR_MAP_NAME_GRAY_SCALE_SAW,
            COLOR_MAP_NAME_COLORS_SCALE_WHITE,
            COLOR_MAP_NAME_COLORS_SCALE_BLACK,
            COLOR_MAP_NAME_HUE_GRADATION_SAW	// COLOR_MAP_NAME_GRADIENT_COLORS_SAW
    };
    
    private static Map<String, String> OLD_COLOR_MAP_NAME_TABLE = new HashMap<String, String>();
    
    static {
    	OLD_COLOR_MAP_NAME_TABLE.put(COLOR_MAP_NAME_GRADIENT_COLORS, COLOR_MAP_NAME_HUE_GRADATION);
    	OLD_COLOR_MAP_NAME_TABLE.put(COLOR_MAP_NAME_BLACK_AND_WHITE, COLOR_MAP_NAME_TWO_COLORS);
    	OLD_COLOR_MAP_NAME_TABLE.put(COLOR_MAP_NAME_GRAY_SCALE, COLOR_MAP_NAME_COLOR_GRAY_SCALE);
    	OLD_COLOR_MAP_NAME_TABLE.put(COLOR_MAP_NAME_GRAY_SCALE_SAW, COLOR_MAP_NAME_COLOR_SAW);
    	OLD_COLOR_MAP_NAME_TABLE.put(COLOR_MAP_NAME_GRADIENT_COLORS_SAW, COLOR_MAP_NAME_HUE_GRADATION_SAW);
    }

    /**
     * The default constructor.
     */
	public SGColorBarColorMapManager() {
		super();

        // create default color bar models
		Color[] base1 = HUE_COLOR_ARRAY;
        Color[] base2 = { Color.BLACK, Color.LIGHT_GRAY };
		SGColorMap[] colorMaps;
        colorMaps = new SGColorMap[DEFAULT_COLOR_MAP_NAMES.length];
        colorMaps[0] = new DiscreteColorMap(base1);
        colorMaps[1] = new HueColorMap();
        colorMaps[2] = new TwoColorMap(base2);
        colorMaps[3] = new GradationalColorMap(base2);
        colorMaps[4] = new RepeatedGradationalColorMap(base2, 3);
        colorMaps[5] = new MultipleGradationalColorMap(base1, Color.WHITE);
        colorMaps[6] = new MultipleGradationalColorMap(base1, Color.BLACK);
        colorMaps[7] = new RepeatedHueColorMap(3);

        // initializes the map
        for (int ii = 0; ii < DEFAULT_COLOR_MAP_NAMES.length; ii++) {
            this.mColorMaps.put(DEFAULT_COLOR_MAP_NAMES[ii],
                    (SGColorMap) colorMaps[ii].clone());
        }
	}
	
	public static boolean isValidColorMapName(String name) {
		for (int ii = 0; ii < DEFAULT_COLOR_MAP_NAMES.length; ii++) {
			if (SGUtilityText.isEqualString(DEFAULT_COLOR_MAP_NAMES[ii], name)) {
				return true;
			}
		}
		return false;
	}

	@Override
    protected String[] getColorMapNameArray() {
    	return DEFAULT_COLOR_MAP_NAMES;
    }

    /**
     * A color bar model that has two colors.
     * 
     */
    public static class TwoColorMap extends DiscreteColorMap {
    	
        public TwoColorMap(final Color[] colors) {
            super(colors);
            if (colors.length != 2) {
            	throw new IllegalArgumentException("colors.length != 2");
            }
        }
        
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
    
    // Overrode for backward compatibility for version <= 2.0.0
    @Override
    public SGColorMap getColorMap(final String name) {
    	SGColorMap colorMap = super.getColorMap(name);
    	if (colorMap == null) {
    		Iterator<Entry<String, String>> itr = OLD_COLOR_MAP_NAME_TABLE.entrySet().iterator();
    		String newKey = null;
    		while (itr.hasNext()) {
    			Entry<String, String> entry = itr.next();
    			String oldKey = entry.getKey();
    			if (SGUtilityText.isEqualString(oldKey, name)) {
    				newKey = entry.getValue();
    				break;
    			}
    		}
    		if (newKey == null) {
    			return null;
    		} else {
    			colorMap = super.getColorMap(newKey);
    		}
    	}
    	return colorMap;
    }

    /**
     * Returns whether it is available to assign colors to the color map of given name.
     * 
     * @param name
     *           the name of the color map
     * @return true if it is available to assign colors to the color map of given name
     */
	public boolean isColorAssignable(final String name) {
		return isColorAssignableType(name);
	}
	
	public static boolean isColorAssignableType(final String name) {
		if (name == null) {
			return false;
		}
		String[] names = { SGColorBarColorMapManager.COLOR_MAP_NAME_COLOR_GRAY_SCALE,
				SGColorBarColorMapManager.COLOR_MAP_NAME_COLOR_SAW,
				SGColorBarColorMapManager.COLOR_MAP_NAME_TWO_COLORS };
		for (int ii = 0; ii < names.length; ii++) {
			if (SGUtilityText.isEqualString(names[ii], name)) {
				return true;
			}
		}
		return false;
	}

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
		if (super.canAcceptColors(name, colors) == false) {
			return false;
		}
		if (colors.length != 2) {
			return false;
		}
		return true;
	}

}

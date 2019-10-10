package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;

/**
 * A class that manages color maps of the line style.
 * 
 */
public class SGLineStyleColorMapManager extends SGColorMapManager implements SGIDataCommandConstants {

    public static final String COLOR_MAP_NAME_HUE_GRADATION = "Hue Gradation";

    public static final String COLOR_MAP_NAME_COLOR_GRAY_SCALE = "Color/Gray Scale";

    /**
     * The default color bar names.
     */
    public static final String[] DEFAULT_COLOR_MAP_NAMES = {
    		COLOR_MAP_NAME_HUE_GRADATION,
    		COLOR_MAP_NAME_COLOR_GRAY_SCALE
    };

    /**
     * The default constructor.
     */
	public SGLineStyleColorMapManager() {
		super();

        // create default color bar models
		SGColorMap[] colorMaps;
		Color[] base1 = { Color.BLACK, Color.LIGHT_GRAY };
        colorMaps = new SGColorMap[DEFAULT_COLOR_MAP_NAMES.length];
        colorMaps[0] = new HueColorMap();
        colorMaps[1] = new GradationalColorMap(base1);

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
		return SGUtilityText.isEqualString(SGLineStyleColorMapManager.COLOR_MAP_NAME_COLOR_GRAY_SCALE, name);
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

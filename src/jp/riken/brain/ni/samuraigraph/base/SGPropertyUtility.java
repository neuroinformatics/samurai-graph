package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;


public class SGPropertyUtility {

	private static void putValue(final SGPropertyMap map,
			final String key, final String value) {
		if (map == null || key == null) {
			return;
		}
		map.putValue(key, value);
	}

	public static void addProperty(final SGPropertyMap map, final String key, 
			final boolean value) {
		putValue(map, key, Boolean.toString(value));
	}

	public static void addProperty(final SGPropertyMap map, final String key, 
			final Number value) {
		putValue(map, key, value.toString());
	}

	public static void addQuotedStringProperty(final SGPropertyMap map, 
			final String key, final String value) {
		StringBuffer sb = new StringBuffer();
		sb.append('"');
		if (value != null) {
			sb.append(value);
		}
		sb.append('"');
		putValue(map, key, sb.toString());
	}

	public static void addProperty(final SGPropertyMap map, 
			final String key, final String value) {
		putValue(map, key, value);
	}

	public static void addProperty(final SGPropertyMap map, final String key, 
			final Number len, 
			final String unit) {
		StringBuffer sb = new StringBuffer();
		sb.append(len);
		sb.append(unit);
		putValue(map, key, sb.toString());
	}

	public static void addProperty(final SGPropertyMap map, final String key, 
			final Color value) {
		putValue(map, key, SGUtilityText.getColorString(value));
	}

	public static void addProperty(final SGPropertyMap map, final String key, 
			final SGAxisValue value) {
		putValue(map, key, value.toString());
	}

	public static void addProperty(final SGPropertyMap map, final String key, 
			final SGAxisStepValue value) {
		putValue(map, key, value.toString());
	}

}

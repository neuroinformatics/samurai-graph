package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;

import org.w3c.dom.Element;

public class SGPropertyFileUtility {

	public static void setAttribute(Element el, String key, 
			final String value) {
		el.setAttribute(key, value);
	}

	public static void setAttribute(Element el, String key, 
			final Color value) {
		String str = SGUtilityText.getColorString(value);
		el.setAttribute(key, str);
	}

	public static void setAttribute(Element el, String key, 
			final boolean value) {
		setAttribute(el, key, Boolean.toString(value));
	}

	public static void setAttribute(Element el, String key, 
			final Number value) {
		SGPropertyFileUtility.setAttribute(el, key, value, null);
	}

	public static void setAttribute(Element el, String key, 
			final Number len, String unit) {
		StringBuffer sb = new StringBuffer();
		sb.append(len);
		if (unit != null) {
			sb.append(unit);
		}
		setAttribute(el, key, sb.toString());
	}

}

package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGColorMap;


/**
 * This class provides utility methods for SGFigureElement objects.
 */
public class SGUtilityForFigureElement {

    /**
     * Returns a list of element group of given class.
     * 
     * @param cl
     *           the class
     * @param dList
     *           the list of element groups
     * @return the list of element groups of given class
     */
	public static List<SGElementGroup> getGroups(Class<?> cl, List<SGElementGroup> dList) {
        List<SGElementGroup> list = new ArrayList<SGElementGroup>();
        for (int ii = 0; ii < dList.size(); ii++) {
            Object obj = dList.get(ii);
            if (cl.isAssignableFrom(obj.getClass())) {
                list.add((SGElementGroup) obj);
            }
        }
        return list;
    }

    /**
     * Returns an element group of given class.
     * 
     * @param cl
     *           the class
     * @param dList
     *           the list of element groups
     * @return an element group of given class
     */
    public static SGElementGroup getGroup(Class<?> cl, List<SGElementGroup> dList) {
        if (null==dList) {
            return null;
        }
        for (int ii = 0; ii < dList.size(); ii++) {
            Object obj = dList.get(ii);
            if (cl.isAssignableFrom(obj.getClass())) {
                return (SGElementGroup) obj;
            }
        }
        return null;
    }

	/**
	 * Creates and returns the list of line style.
	 *
	 * @param colorMap
	 *          the color map
	 * @param dataNum
	 *          the number of data
	 * @return the list of line style
	 */
	public static List<SGLineStyle> createLineStyleList(final SGColorMap colorMap,
			final int dataNum) {
		List<SGLineStyle> lineStyleList = new ArrayList<SGLineStyle>();
		final Color color0 = colorMap.eval(0.0);
	    for (int ii = 0; ii < dataNum; ii++) {
	        final Color cl = (dataNum > 1) ? colorMap.eval((double) ii / (dataNum - 1)) : color0;
	        SGLineStyle style = new SGLineStyle(SGISXYDataConstants.DEFAULT_LINE_TYPE, cl, 
	        		SGISXYDataConstants.DEFAULT_LINE_WIDTH);
	        lineStyleList.add(style);
	    }
	    return lineStyleList;
	}
}

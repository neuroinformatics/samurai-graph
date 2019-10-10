package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyle;

public class SGMultipleSXYUtility {
	
	public static List<SGElementGroupLine> getLineGroups(
			SGIElementGroupSetMultipleSXY gs) {
		return getLineGroupsSub(gs, false);
	}

	public static List<SGElementGroupLine> getLineGroupsIgnoreNull(
			SGIElementGroupSetMultipleSXY gs) {
		return getLineGroupsSub(gs, true);
	}

	private static List<SGElementGroupLine> getLineGroupsSub(
			SGIElementGroupSetMultipleSXY gs, final boolean ignoreNull) {
		List<SGElementGroupLine> list = new ArrayList<SGElementGroupLine>();
		list.add(gs.getLineGroup());
		SGIElementGroupSetForData[] array = gs.getChildGroupSetArray();
		for (int ii = 0; ii < array.length; ii++) {
			SGIElementGroupSetSXY sxy = (SGIElementGroupSetSXY) array[ii];
			if (ignoreNull) {
				if (sxy.getData() != null) {
					list.add(sxy.getLineGroup());
				}
			} else {
				list.add(sxy.getLineGroup());
			}
		}
		return list;
	}

	public static List<SGElementGroupSymbol> getSymbolGroups(
			SGIElementGroupSetMultipleSXY gs) {
		return getSymbolGroupsSub(gs, false);
	}

	public static List<SGElementGroupSymbol> getSymbolGroupsIgnoreNull(
			SGIElementGroupSetMultipleSXY gs) {
		return getSymbolGroupsSub(gs, true);
	}

	public static List<SGElementGroupSymbol> getSymbolGroupsSub(
			SGIElementGroupSetMultipleSXY gs, final boolean ignoreNull) {
		List<SGElementGroupSymbol> list = new ArrayList<SGElementGroupSymbol>();
		list.add(gs.getSymbolGroup());
		SGIElementGroupSetForData[] array = gs.getChildGroupSetArray();
		for (int ii = 0; ii < array.length; ii++) {
			SGIElementGroupSetSXY sxy = (SGIElementGroupSetSXY) array[ii];
			if (ignoreNull) {
				if (sxy.getData() != null) {
					list.add(sxy.getSymbolGroup());
				}
			} else {
				list.add(sxy.getSymbolGroup());
			}
		}
		return list;
	}

	public static List<SGElementGroupBar> getBarGroups(
			SGIElementGroupSetMultipleSXY gs) {
		return getBarGroupsSub(gs, false);
	}

	public static List<SGElementGroupBar> getBarGroupsIgnoreNull(
			SGIElementGroupSetMultipleSXY gs) {
		return getBarGroupsSub(gs, true);
	}

	public static List<SGElementGroupBar> getBarGroupsSub(
			SGIElementGroupSetMultipleSXY gs, final boolean ignoreNull) {
		List<SGElementGroupBar> list = new ArrayList<SGElementGroupBar>();
		list.add(gs.getBarGroup());
		SGIElementGroupSetForData[] array = gs.getChildGroupSetArray();
		for (int ii = 0; ii < array.length; ii++) {
			SGIElementGroupSetSXY sxy = (SGIElementGroupSetSXY) array[ii];
			if (ignoreNull) {
				if (sxy.getData() != null) {
					list.add(sxy.getBarGroup());
				}
			} else {
                                list.add(sxy.getBarGroup());
			}
		}
		return list;
	}

	public static List<SGElementGroupErrorBar> getErrorBarGroups(
			SGIElementGroupSetMultipleSXY gs) {
		return getErrorBarGroupsSub(gs, false);
	}

	public static List<SGElementGroupErrorBar> getErrorBarGroupsIgnoreNull(
			SGIElementGroupSetMultipleSXY gs) {
		return getErrorBarGroupsSub(gs, true);
	}

	public static List<SGElementGroupErrorBar> getErrorBarGroupsSub(
			SGIElementGroupSetMultipleSXY gs, final boolean ignoreNull) {
		List<SGElementGroupErrorBar> list = new ArrayList<SGElementGroupErrorBar>();
		list.add(gs.getErrorBarGroup());
		SGIElementGroupSetForData[] array = gs.getChildGroupSetArray();
		for (int ii = 0; ii < array.length; ii++) {
			SGIElementGroupSetSXY sxy = (SGIElementGroupSetSXY) array[ii];
			if (ignoreNull) {
				if (sxy.getData() != null) {
					list.add(sxy.getErrorBarGroup());
				}
			} else {
				list.add(sxy.getErrorBarGroup());
			}
		}
		return list;
	}

	public static List<SGElementGroupTickLabel> getTickLabelGroups(
			SGIElementGroupSetMultipleSXY gs) {
		return getTickLabelGroupsSub(gs, false);
	}

	public static List<SGElementGroupTickLabel> getTickLabelGroupsIgnoreNull(
			SGIElementGroupSetMultipleSXY gs) {
		return getTickLabelGroupsSub(gs, true);
	}

	public static List<SGElementGroupTickLabel> getTickLabelGroupsSub(
			SGIElementGroupSetMultipleSXY gs, final boolean ignoreNull) {
		List<SGElementGroupTickLabel> list = new ArrayList<SGElementGroupTickLabel>();
		list.add(gs.getTickLabelGroup());
		SGIElementGroupSetForData[] array = gs.getChildGroupSetArray();
		for (int ii = 0; ii < array.length; ii++) {
			SGIElementGroupSetSXY sxy = (SGIElementGroupSetSXY) array[ii];
			if (ignoreNull) {
				if (sxy.getData() != null) {
					list.add(sxy.getTickLabelGroup());
				}
			} else {
				list.add(sxy.getTickLabelGroup());
			}
		}
		return list;
	}

	public static boolean setLineVisible(List<SGElementGroupLine> list,
			final boolean b) {
		for (int ii = 0; ii < list.size(); ii++) {
			SGElementGroupLine group = (SGElementGroupLine) list.get(ii);
			group.setVisible(b);
		}
		return true;
	}

	public static boolean setLineWidth(List<SGElementGroupLine> list,
			final float width) {
		for (int ii = 0; ii < list.size(); ii++) {
			SGElementGroupLine group = (SGElementGroupLine) list.get(ii);
			group.setLineWidth(width);
		}
		return true;
	}

	public static boolean setLineWidth(List<SGElementGroupLine> list,
			final float width, final String unit) {
		final float value = (float) SGUtilityText.convertToPoint(width, unit);
		return setLineWidth(list, value);
	}
	
	public static boolean setLineWidth(SGIElementGroupSetMultipleSXY gs,
			final float width, final String unit) {
		final Float value = (float) SGUtility.getLineWidth(width, unit);
		if (value == null) {
			return false;
		}
		SGElementGroupLine lineGroup = gs.getLineGroup();
		SGLineStyle style = lineGroup.getLineStyle();
		style.setLineWidth(value);
		lineGroup.setStyle(style);
		final int num = gs.getChildNumber();
		for (int ii = 0; ii < num; ii++) {
			SGLineStyle lineStyle = gs.getLineStyle(ii);
			lineStyle.setLineWidth(value);
			gs.setLineStyle(lineStyle, ii);
		}
		return true;
	}

	public static boolean setLineWidth(SGIElementGroupSetMultipleSXY gs,
			final float width, final String unit, final int index) {
		final Float value = (float) SGUtility.getLineWidth(width, unit);
		if (value == null) {
			return false;
		}
		SGElementGroupLine lineGroup = gs.getLineGroup();
		SGLineStyle style = lineGroup.getLineStyle();
		style.setLineWidth(value);
		lineGroup.setStyle(style);
		final int num = gs.getChildNumber();
		if (index < 0 || index >= num) {
			return false;
		}
		SGLineStyle lineStyle = gs.getLineStyle(index);
		lineStyle.setLineWidth(value);
		gs.setLineStyle(lineStyle, index);
		return true;
	}

	public static boolean setLineType(List<SGElementGroupLine> list,
			final int type) {
		for (int ii = 0; ii < list.size(); ii++) {
			SGElementGroupLine group = (SGElementGroupLine) list.get(ii);
			group.setLineType(type);
		}
		return true;
	}

	public static boolean setLineType(SGIElementGroupSetMultipleSXY gs,
			final int type) {
		SGElementGroupLine lineGroup = gs.getLineGroup();
		SGLineStyle style = lineGroup.getLineStyle();
		style.setLineType(type);
		lineGroup.setStyle(style);
		final int num = gs.getChildNumber();
		for (int ii = 0; ii < num; ii++) {
			SGLineStyle lineStyle = gs.getLineStyle(ii);
			lineStyle.setLineType(type);
			gs.setLineStyle(lineStyle, ii);
		}
		return true;
	}

	public static boolean setLineType(SGIElementGroupSetMultipleSXY gs,
			final int type, final int index) {
		SGElementGroupLine lineGroup = gs.getLineGroup();
		SGLineStyle style = lineGroup.getLineStyle();
		style.setLineType(type);
		lineGroup.setStyle(style);
		final int num = gs.getChildNumber();
		if (index < 0 || index >= num) {
			return false;
		}
		SGLineStyle lineStyle = gs.getLineStyle(index);
		lineStyle.setLineType(type);
		gs.setLineStyle(lineStyle, index);
		return true;
	}

	public static boolean setLineColor(List<SGElementGroupLine> list,
			final Color cl) {
		for (int ii = 0; ii < list.size(); ii++) {
			SGElementGroupLine group = (SGElementGroupLine) list.get(ii);
			group.setColor(cl);
		}
		return true;
	}

	public static boolean setLineColor(SGIElementGroupSetMultipleSXY gs,
			final Color cl) {
		SGElementGroupLine lineGroup = gs.getLineGroup();
		SGLineStyle style = lineGroup.getLineStyle();
		style.setColor(cl);
		lineGroup.setStyle(style);
		final int num = gs.getChildNumber();
		for (int ii = 0; ii < num; ii++) {
			SGLineStyle lineStyle = gs.getLineStyle(ii);
			lineStyle.setColor(cl);
			gs.setLineStyle(lineStyle, ii);
		}
		return true;
	}

	public static boolean setLineColor(SGIElementGroupSetMultipleSXY gs,
			final Color cl, final int index) {
		SGElementGroupLine lineGroup = gs.getLineGroup();
		SGLineStyle style = lineGroup.getLineStyle();
		style.setColor(cl);
		lineGroup.setStyle(style);
		final int num = gs.getChildNumber();
		if (index < 0 || index >= num) {
			return false;
		}
		SGLineStyle lineStyle = gs.getLineStyle(index);
		lineStyle.setColor(cl);
		gs.setLineStyle(lineStyle, index);
		return true;
	}

	public static boolean setLineConnectingAll(List<SGElementGroupLine> list,
			final boolean b) {
		for (int ii = 0; ii < list.size(); ii++) {
			SGElementGroupLine group = (SGElementGroupLine) list.get(ii);
			group.setLineConnectingAll(b);
		}
		return true;
	}
	
    public static boolean setSymbolVisible(List<SGElementGroupSymbol> list,
    		final boolean b) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupSymbol group = (SGElementGroupSymbol) list.get(ii);
            group.setVisible(b);
        }
        return true;
    }

    public static boolean setSymbolType(List<SGElementGroupSymbol> list, 
    		final int type) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupSymbol group = (SGElementGroupSymbol) list.get(ii);
            group.setType(type);
        }
        return true;
    }

    public static boolean setSymbolSize(List<SGElementGroupSymbol> list, 
    		final float size) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupSymbol group = (SGElementGroupSymbol) list.get(ii);
            group.setSize(size);
        }
        return true;
    }

    public static boolean setSymbolSize(List<SGElementGroupSymbol> list, 
    		final float size, final String unit) {
        final float value = (float) SGUtilityText.convertToPoint(size, unit);
        return setSymbolSize(list, value);
    }

    public static boolean setSymbolLineWidth(List<SGElementGroupSymbol> list,
    		final float width) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupSymbol group = (SGElementGroupSymbol) list.get(ii);
            group.setLineWidth(width);
        }
        return true;
    }

    public static boolean setSymbolLineWidth(List<SGElementGroupSymbol> list, 
    		final float width, final String unit) {
        final float value = (float) SGUtilityText.convertToPoint(width, unit);
        return setSymbolLineWidth(list, value);
    }
    
    public static boolean setSymbolInnerPaint(List<SGElementGroupSymbol> list, 
    		final SGIPaint paint) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupSymbol group = (SGElementGroupSymbol) list.get(ii);
            group.setInnerPaint(paint);
        }
        return true;
    }

    public static boolean setSymbolLineColor(List<SGElementGroupSymbol> list, 
    		final Color cl) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupSymbol group = (SGElementGroupSymbol) list.get(ii);
            group.setLineColor(cl);
        }
        return true;
    }
    
    public static boolean setSymbolLineVisible(List<SGElementGroupSymbol> list, 
                final boolean b) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupSymbol group = (SGElementGroupSymbol) list.get(ii);
            group.setLineVisible(b);
        }
        return true;
    }

    public static boolean setBarVisible(List<SGElementGroupBar> list, 
    		final boolean b) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupBar group = (SGElementGroupBar) list.get(ii);
            group.setVisible(b);
        }
        return true;
    }

    public static boolean setBarBaselineValue(List<SGElementGroupBar> list, 
    		final double value) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupBar group = (SGElementGroupBar) list.get(ii);
            group.setBaselineValue(value);
        }
        return true;
    }

    public static boolean setBarWidth(List<SGElementGroupBar> list, 
    		final float width) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupBar group = (SGElementGroupBar) list.get(ii);
            group.setRectangleWidth(width);
        }
        return true;
    }

    public static boolean setBarWidthValue(List<SGElementGroupBar> list, 
    		final double value) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupBar group = (SGElementGroupBar) list.get(ii);
            group.setWidthValue(value);
        }
        return true;
    }

    public static boolean setBarEdgeLineWidth(List<SGElementGroupBar> list, 
    		final float width) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupBar group = (SGElementGroupBar) list.get(ii);
            group.setEdgeLineWidth(width);
        }
        return true;
    }

    public static boolean setBarEdgeLineWidth(List<SGElementGroupBar> list, 
    		final float width, final String unit) {
        final float value = (float) SGUtilityText.convertToPoint(width, unit);
        return setBarEdgeLineWidth(list, value);
    }

    public static boolean setBarInnerPaint(List<SGElementGroupBar> list, 
            final SGIPaint paint) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupBar group = (SGElementGroupBar) list.get(ii);
            group.setInnerPaint(paint);
        }
        return true;
    }

    public static boolean setBarEdgeLineColor(List<SGElementGroupBar> list, 
    		final Color cl) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupBar group = (SGElementGroupBar) list.get(ii);
            group.setEdgeLineColor(cl);
        }
        return true;
    }
    
    public static boolean setBarEdgeLineVisible(List<SGElementGroupBar> list, 
            final boolean visible) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupBar group = (SGElementGroupBar) list.get(ii);
            group.setEdgeLineVisible(visible);
        }
        return true;
    }

    public static boolean setBarVertical(List<SGElementGroupBar> list, boolean b) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupBar group = (SGElementGroupBar) list.get(ii);
            group.setVertical(b);
        }
        return true;
    }

    public static boolean setBarOffsetX(List<SGElementGroupBar> list,
            final double shift) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupBar group = (SGElementGroupBar) list.get(ii);
            group.setOffsetX(shift);
        }
        return true;
    }

    public static boolean setBarOffsetY(List<SGElementGroupBar> list,
            final double shift) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupBar group = (SGElementGroupBar) list.get(ii);
            group.setOffsetY(shift);
        }
        return true;
    }

    public static boolean setBarInterval(List<SGElementGroupBar> list,
            final double interval) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupBar group = (SGElementGroupBar) list.get(ii);
            group.setInterval(interval);
        }
        return true;
    }

    public static boolean setErrorBarVisible(List<SGElementGroupErrorBar> list, 
    		final boolean b) {
        for (int ii = 0; ii < list.size(); ii++) {
        	SGElementGroupErrorBar group = (SGElementGroupErrorBar) list.get(ii);
            group.setVisible(b);
        }
        return true;
    }

    public static boolean setErrorBarHeadType(List<SGElementGroupErrorBar> list, 
    		final int type) {
        for (int ii = 0; ii < list.size(); ii++) {
        	SGElementGroupErrorBar group = (SGElementGroupErrorBar) list.get(ii);
            group.setHeadType(type);
        }
        return true;
    }

    public static boolean setErrorBarHeadSize(List<SGElementGroupErrorBar> list,
    		final float size) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupErrorBar group = (SGElementGroupErrorBar) list.get(ii);
            group.setHeadSize(size);
        }
        return true;
    }

    public static boolean setErrorBarHeadSize(List<SGElementGroupErrorBar> list,
    		final float size, final String unit) {
        final float value = (float) SGUtilityText.convertToPoint(size, unit);
        return setErrorBarHeadSize(list, value);
    }

    public static boolean setErrorBarColor(List<SGElementGroupErrorBar> list,
    		final Color cl) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupErrorBar group = (SGElementGroupErrorBar) list.get(ii);
            group.setColor(cl);
        }
        return true;
    }

    public static boolean setErrorBarLineWidth(List<SGElementGroupErrorBar> list,
    		final float width) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupErrorBar group = (SGElementGroupErrorBar) list.get(ii);
            group.setLineWidth(width);
        }
        return true;
    }

    public static boolean setErrorBarLineWidth(List<SGElementGroupErrorBar> list,
    		final float width, final String unit) {
        final float value = (float) SGUtilityText.convertToPoint(width, unit);
        return setErrorBarLineWidth(list, value);
    }

    public static boolean setErrorBarStyle(List<SGElementGroupErrorBar> list,
    		final int style) {
        for (int ii = 0; ii < list.size(); ii++) {
        	SGElementGroupErrorBar group = (SGElementGroupErrorBar) list.get(ii);
            group.setErrorBarStyle(style);
        }
        return true;
    }
    
    public static boolean setErrorBarVertical(List<SGElementGroupErrorBar> list,
    		final boolean b) {
        for (int ii = 0; ii < list.size(); ii++) {
        	SGElementGroupErrorBar group = (SGElementGroupErrorBar) list.get(ii);
            group.setVertical(b);
        }
    	return true;
    }
    
    
    public static boolean setErrorBarOnLinePosition(List<SGElementGroupErrorBar> list,
                final boolean b) {
        for (int ii = 0; ii < list.size(); ii++) {
                SGElementGroupErrorBar group = (SGElementGroupErrorBar) list.get(ii);
            group.setPositionOnLine(b);
        }
        return true;
    }

    public static boolean setTickLabelVisible(List<SGElementGroupTickLabel> list,
    		final boolean b) {
        for (int ii = 0; ii < list.size(); ii++) {
        	SGElementGroupTickLabel group = (SGElementGroupTickLabel) list.get(ii);
            group.setVisible(b);
        }
        return true;
    }

    public static boolean setTickLabelFontName(List<SGElementGroupTickLabel> list,
    		final String name) {
        for (int ii = 0; ii < list.size(); ii++) {
        	SGElementGroupTickLabel group = (SGElementGroupTickLabel) list.get(ii);
            group.setFontName(name);
        }
        return true;
    }

    public static boolean setTickLabelFontStyle(List<SGElementGroupTickLabel> list,
    		final int style) {
        for (int ii = 0; ii < list.size(); ii++) {
        	SGElementGroupTickLabel group = (SGElementGroupTickLabel) list.get(ii);
            group.setFontStyle(style);
        }
        return true;
    }

    public static boolean setTickLabelFontSize(List<SGElementGroupTickLabel> list,
    		final float size) {
        for (int ii = 0; ii < list.size(); ii++) {
        	SGElementGroupTickLabel group = (SGElementGroupTickLabel) list.get(ii);
            group.setFontSize(size);
        }
        return true;
    }

    public static boolean setTickLabelFontSize(List<SGElementGroupTickLabel> list,
    		final float size, final String unit) {
        final float value = (float) SGUtilityText.convertToPoint(size, unit);
        return setTickLabelFontSize(list, value);
    }

    public static boolean setTickLabelColor(List<SGElementGroupTickLabel> list,
    		final Color cl) {
        for (int ii = 0; ii < list.size(); ii++) {
        	SGElementGroupTickLabel group = (SGElementGroupTickLabel) list.get(ii);
            group.setColor(cl);
        }
        return true;
    }

    public static boolean setTickLabelAngle(List<SGElementGroupTickLabel> list,
            final float angle) {
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupTickLabel group = (SGElementGroupTickLabel) list.get(ii);
            group.setAngle(angle);
        }
        return true;
    }

    public static boolean setTickLabelDecimalPlaces(List<SGElementGroupTickLabel> list,
    		final int dp) {
        for (int ii = 0; ii < list.size(); ii++) {
        	SGElementGroupTickLabel group = (SGElementGroupTickLabel) list.get(ii);
            group.setDecimalPlaces(dp);
        }
        return true;
    }

    public static boolean setTickLabelExponent(List<SGElementGroupTickLabel> list,
    		final int exp) {
        for (int ii = 0; ii < list.size(); ii++) {
        	SGElementGroupTickLabel group = (SGElementGroupTickLabel) list.get(ii);
            group.setExponent(exp);
        }
        return true;
    }

    public static boolean setTickLabelDateFormat(List<SGElementGroupTickLabel> list,
    		final String format) {
        for (int ii = 0; ii < list.size(); ii++) {
        	SGElementGroupTickLabel group = (SGElementGroupTickLabel) list.get(ii);
            group.setDateFormat(format);
        }
        return true;
    }

    public static boolean setTickLabelHorizontalAlignment(List<SGElementGroupTickLabel> list,
    		final boolean b) {
        for (int ii = 0; ii < list.size(); ii++) {
        	SGElementGroupTickLabel group = (SGElementGroupTickLabel) list.get(ii);
            group.setHorizontalAlignment(b);
        }
        return true;
    }

    public static boolean setColumnInfo(
    		SGIElementGroupSetMultipleSXY groupSet,
    		SGFigureElementForData element) {

    	// disposes of old data
    	if (removeAllChildData(groupSet) == false) {
    		return false;
    	}

    	// update child group sets
    	if (!groupSet.updateChild()) {
    		return false;
    	}
    	return true;
    }
    
    public static boolean removeAllChildData(SGIElementGroupSetMultipleSXY groupSet) {
    	SGIElementGroupSetForData[] cArray = groupSet.getChildGroupSetArray();
    	for (int ii = 0; ii < cArray.length; ii++) {
    		SGData dOld = cArray[ii].getData();
    		if (dOld != null) {
        		dOld.dispose();
    		}
    	}
    	return true;
    }

}

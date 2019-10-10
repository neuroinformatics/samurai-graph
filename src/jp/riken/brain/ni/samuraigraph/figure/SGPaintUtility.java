package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGGradationPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIPaintConstant;
import jp.riken.brain.ni.samuraigraph.base.SGPatternPaint;
import jp.riken.brain.ni.samuraigraph.base.SGSelectablePaint;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

import org.w3c.dom.Element;

public class SGPaintUtility implements SGIPaintConstant {
    
//	public static SGPropertyMap getPropertyFileMap(SGIPaint paint) {
//    	SGPropertyMap map = new SGPropertyMap();
//    	SGPropertyUtility.addProperty(map, KEY_TRANSPARENT, 
//    			((SGTransparentPaint) paint).getTransparencyPercent(), TRANSPARENCY_UNIT);
//    	map.putAll(paint.getPropertyFileMap());
//    	return map;
//	}
//
//    public static boolean writeProperty(final Element el, final SGIPaint paint) {
////        el.setAttribute(KEY_TRANSPARENT,
////                Integer.toString(((SGTransparentPaint)paint).getTransparencyPercent())+TRANSPARENCY_UNIT);
////        if (paint instanceof SGFillPaint) {
////            return writePropertyFillPaint(el, (SGFillPaint)paint);
////        } else if (paint instanceof SGPatternPaint) {
////            return writePropertyPatternPaint(el, (SGPatternPaint)paint);
////        } else if (paint instanceof SGGradationPaint) {
////            return writePropertyGradationPaint(el, (SGGradationPaint)paint);
////        } else if (paint instanceof SGSelectablePaint) {
////            return writePropertySelectablePaint(el, (SGSelectablePaint)paint);
////        } else {
////            return false;
////        }
//    	SGPropertyMap map = getPropertyFileMap(paint);
//    	map.setToElement(el);
//    	return true;
//    }
    
//    public static boolean writePropertyFillPaint(final Element el, final SGFillPaint paint) {
//        el.setAttribute(KEY_FILL_COLOR, SGUtilityText.getColorString((Color)paint.getPaint(null)));
//        return true;
//    }
//    
//    public static boolean writePropertyPatternPaint(final Element el, final SGPatternPaint paint) {
//        el.setAttribute(KEY_PATTERN_COLOR, SGUtilityText.getColorString(paint.getColor()));
//        el.setAttribute(KEY_PATTERN_TYPE, SGPatternPaint.getTypeName(paint.getTypeIndex()));
//        return true;
//    }
//    
//    public static boolean writePropertyGradationPaint(final Element el, final SGGradationPaint paint) {
//        Color[] colors = paint.getColors();
//        if (colors.length!=2) {
//            return false;
//        }
//        el.setAttribute(KEY_GRADATION_COLOR1, SGUtilityText.getColorString(colors[0]));
//        el.setAttribute(KEY_GRADATION_COLOR2, SGUtilityText.getColorString(colors[1]));
//        el.setAttribute(KEY_GRADATION_DIRECTION, SGGradationPaint.getDirectionName(paint.getDirectionIndex()));
//        el.setAttribute(KEY_GRADATION_ORDER, SGGradationPaint.getOrderName(paint.getOrderIndex()));
//        return true;
//    }
//    
//    public static boolean writePropertySelectablePaint(final Element el, final SGSelectablePaint paint) {
//        el.setAttribute(KEY_PAINT_STYLE, SGSelectablePaint.getStyleName(paint.getSelectedStyle()));
//        el.setAttribute(KEY_FILL_COLOR, SGUtilityText.getColorString(paint.getFillColor()));
//        el.setAttribute(KEY_PATTERN_COLOR, SGUtilityText.getColorString(paint.getPatternColor()));
//        el.setAttribute(KEY_PATTERN_TYPE, SGPatternPaint.getTypeName(paint.getPatternIndex()));
//        if (! writePropertyGradationPaint(el, paint.getGradationPaint())) {
//            return false;
//        }
//        return true;
//    }
    
    public static SGIPaint readProperty(final Element el) {
        
        Number transparent = null;
        Boolean transparentFlag = null;
        String str = null;
        Color fillColor = null;
        Color patternColor = null;
        String patternType = null;
        Color gradationColor1 = null;
        Color gradationColor2 = null;
        String gradationDirection = null;
        String gradationOrder = null;
        String paintStyle = null;
        
        // transparent
        str = el.getAttribute(KEY_TRANSPARENT);
        if (str.length() != 0) {
            transparent = SGUtilityText.getInteger(str, TRANSPARENCY_UNIT);
            transparentFlag = SGUtilityText.getBoolean(str);
        }
        
        // fill paint
        str = el.getAttribute(KEY_FILL_COLOR);
        if (str.length() != 0) {
            fillColor = SGUtilityText.parseColor(str);
        }
        
        // pattern paint
        str = el.getAttribute(KEY_PATTERN_COLOR);
        if (str.length() != 0) {
            patternColor = SGUtilityText.parseColor(str);
        }
        str = el.getAttribute(KEY_PATTERN_TYPE);
        if (str.length() != 0) {
            patternType = str.trim();
        }
        
        // gradation paint
        str = el.getAttribute(KEY_GRADATION_COLOR1);
        if (str.length() != 0) {
            gradationColor1 = SGUtilityText.parseColor(str);
        }
        str = el.getAttribute(KEY_GRADATION_COLOR2);
        if (str.length() != 0) {
            gradationColor2 = SGUtilityText.parseColor(str);
        }
        str = el.getAttribute(KEY_GRADATION_DIRECTION);
        if (str.length() != 0) {
            gradationDirection = str.trim();
        }
        str = el.getAttribute(KEY_GRADATION_ORDER);
        if (str.length() != 0) {
            gradationOrder = str.trim();
        }
        
        // selectable paint
        str = el.getAttribute(KEY_PAINT_STYLE);
        if (str.length() != 0) {
            paintStyle = str.trim();
        }
        
        if (null != paintStyle) {
            SGSelectablePaint paint = new SGSelectablePaint();
            if (null != transparentFlag) {
                if (transparentFlag.booleanValue() == false) {
                    paint.setTransparency(SGSelectablePaint.ALL_OPAQUE_VALUE);
                } else {
                    paint.setTransparency(SGSelectablePaint.ALL_TRANSPARENT_VALUE);
                }
            } else {
                if (null != transparent) {
                    paint.setTransparency(transparent.intValue());
                }
            }
            if (null != fillColor) {
                paint.setFillColor(fillColor);
            }
            if (null != patternType) {
                if (null != patternColor) {
                    paint.setPatternColor(patternColor);
                }
                Integer typeIndex = SGPatternPaint.getTypeFromName(patternType);
                if (null != typeIndex) {
                    paint.setPatternIndex(typeIndex.intValue());
                }
            }
            SGGradationPaint gpaint = new SGGradationPaint();
            if (null != gradationColor1 && null != gradationColor2) {
                gpaint.setColors(new Color[] { gradationColor1, gradationColor2 });
            }
            if (null != gradationDirection) {
                Integer directionIndex = SGGradationPaint.getDirectionIndex(gradationDirection);
                if (null != directionIndex) {
                    gpaint.setDirection(directionIndex.intValue());
                }
            }
            if (null != gradationOrder) {
                Integer orderIndex = SGGradationPaint.getOrderIndex(gradationOrder);
                if (null != orderIndex) {
                    gpaint.setOrder(orderIndex.intValue());
                }
            }
            paint.setGradationPaint(gpaint);
            
            Integer paintStyleIndex = SGSelectablePaint.getStyleIndex(paintStyle);
            if (null != paintStyleIndex) {
                paint.setSelectedPaintStyle(paintStyleIndex.intValue());
                return paint;
            }
        } else {
            if (null != fillColor) {
                SGFillPaint paint = new SGFillPaint();
                if (null != transparent) {
                    paint.setTransparency(transparent.intValue());
                }
                if (! paint.setColor(fillColor)) {
                    return paint;
                }
            } else if (null != patternType) {
                SGPatternPaint paint = new SGPatternPaint();
                if (null != transparent) {
                    paint.setTransparency(transparent.intValue());
                }
                if (null != patternColor) {
                    paint.setColor(patternColor);
                }
                Integer typeIndex = SGPatternPaint.getTypeFromName(patternType);
                if (null != typeIndex) {
                    paint.setTypeIndex(typeIndex.intValue());
                    return paint;
                }
            } else if (null != gradationColor1 && null != gradationColor2 &&
                    null != gradationDirection && null != gradationOrder) {
                SGGradationPaint paint = new SGGradationPaint();
                if (null != transparent) {
                    paint.setTransparency(transparent.intValue());
                }
                paint.setColors(new Color[] { gradationColor1, gradationColor2 });
                Integer directionIndex = SGGradationPaint.getDirectionIndex(gradationDirection);
                if (null != directionIndex) {
                    paint.setDirection(directionIndex.intValue());
                }
                Integer orderIndex = SGGradationPaint.getOrderIndex(gradationOrder);
                if (null != orderIndex) {
                    paint.setOrder(orderIndex.intValue());
                }
                return paint;
            }
        }
        
        return null;
    }
}

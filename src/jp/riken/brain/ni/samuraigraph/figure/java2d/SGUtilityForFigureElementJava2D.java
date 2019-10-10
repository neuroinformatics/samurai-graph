package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGFigureElement;
import jp.riken.brain.ni.samuraigraph.figure.SGILineConstants;

/**
 * This class has utility methods for the subclasses of SGFigureElement in this
 * package.
 * 
 */
public class SGUtilityForFigureElementJava2D implements
        SGIFigureElementConstants, SGIDataCommandConstants, SGILineConstants {

    /**
     * Draw an anchor around a focused object.
     * 
     * @param pos
     *            location of an anchor
     * @param g2d
     *            graphic context
     */
    public static void drawAnchorAsFocusedObject(final Point2D pos,
            final Graphics2D g2d) {
        drawAnchorAsFocusedObject((float) pos.getX(), (float) pos.getY(), g2d);
    }

    /**
     * Draw an anchor around a focused object.
     * 
     * @param pos
     *            location of an anchor
     * @param g2d
     *            graphic context
     */
    public static void drawAnchorAsFocusedObject(final float x, final float y,
            final Graphics2D g2d) {
        drawCircle(x, y, ANCHOR_SIZE_FOR_FOCUSED_OBJECTS,
                ANCHOR_EDGE_LINE_WIDTH, ANCHOR_EDGE_LINE_COLOR,
                ANCHOR_INNER_COLOR, g2d);
    }

    /**
     * Draw anchors around focused objects.
     * 
     * @param pointList
     *            list of the location of anchors
     * @param g2d
     *            graphic context
     */
    public static void drawAnchorAsFocusedObject(final List<Point2D> pointList,
            final Graphics2D g2d) {
        for (int ii = 0; ii < pointList.size(); ii++) {
            Point2D pos = (Point2D) pointList.get(ii);
            drawAnchorAsFocusedObject(pos, g2d);
        }
    }
    
    /**
     * Draw anchors around focused objects which are anchored.
     * 
     * @param pointList
     *            list of the location of anchors
     * @param g2d
     *            graphic context
     */
    public static void drawAnchorPointsAsAnchoredForcusObject(
            final List<Point2D> pointList, final Graphics2D g2d) {
        for (int ii = 0; ii < pointList.size(); ii++) {
            Point2D pos = (Point2D) pointList.get(ii);
            drawTriangle(pos,
                    ANCHOR_SIZE_FOR_FOCUSED_OBJECTS,
                    ANCHOR_EDGE_LINE_WIDTH,
                    ANCHOR_EDGE_LINE_COLOR,
                    ANCHOR_INNER_COLOR,
                    g2d);
        }
    }

    /**
     * Draw an anchor around child objects.
     * 
     * @param pos
     *            location of an anchor
     * @param g2d
     *            graphic context
     */
    public static void drawAnchorAsChildObject(final Point2D pos,
            final Graphics2D g2d) {
        drawSquare(pos, ANCHOR_SIZE_FOR_CHILD_OBJECTS,
                ANCHOR_EDGE_LINE_WIDTH, ANCHOR_EDGE_LINE_COLOR,
                ANCHOR_INNER_COLOR_FOR_CHILD_OBJECTS, g2d);
    }

    /**
     * Draw anchors around child objects.
     * 
     * @param pointList
     *            list of the location of anchors
     * @param g2d
     *            graphic context
     */
    public static void drawAnchorAsChildObject(final List<Point2D> pointList,
            final Graphics2D g2d) {
        for (int ii = 0; ii < pointList.size(); ii++) {
            Point2D pos = (Point2D) pointList.get(ii);
            drawAnchorAsChildObject(pos, g2d);
        }
    }
    
    /**
     * Draw anchors around child objects which are anchored.
     * 
     * @param pointList
     *            list of the location of anchors
     * @param g2d
     *            graphic context
     */
    public static void drawAnchorPointsAsAnchoredChildObject(
            final List<Point2D> pointList, final Graphics2D g2d) {
        for (int ii = 0; ii < pointList.size(); ii++) {
            Point2D pos = (Point2D) pointList.get(ii);
            drawTriangle(pos,
                    ANCHOR_SIZE_FOR_FOCUSED_OBJECTS,
                    ANCHOR_EDGE_LINE_WIDTH,
                    ANCHOR_EDGE_LINE_COLOR,
                    ANCHOR_INNER_COLOR_FOR_CHILD_OBJECTS,
                    g2d);
        }
    }

    /**
     * Clip the graph rectangle of figure element.
     * 
     * @param el -
     *            figure element
     * @param g2d -
     *            graphic context to be clipped
     * @return
     */
    public static boolean clipGraphRect(final SGFigureElement el,
            final Graphics2D g2d) {
        Rectangle2D vBounds = el.getViewBounds();
        if (vBounds == null) {
            return false;
        }
        final Rectangle2D rectGraph = el.getGraphRect();
        final Rectangle2D rectCommon = new Rectangle2D.Float();
        Rectangle2D.intersect(rectGraph, vBounds, rectCommon);
        g2d.setClip(rectCommon);
        return true;
    }

    /**
     * 
     * @param g2d
     * @param symbol
     * @return
     */
    public static boolean drawAnchorsOnRectangle(final Rectangle2D rect,
            final Graphics2D g2d) {
        final float x = (float) rect.getX();
        final float y = (float) rect.getY();
        final float w = (float) rect.getWidth();
        final float h = (float) rect.getHeight();
        Point2D pos = new Point2D.Float();
        pos.setLocation(x, y);
        drawAnchorAsFocusedObject(pos, g2d);
        pos.setLocation(x + w, y);
        drawAnchorAsFocusedObject(pos, g2d);
        pos.setLocation(x, y + h);
        drawAnchorAsFocusedObject(pos, g2d);
        pos.setLocation(x + w, y + h);
        drawAnchorAsFocusedObject(pos, g2d);
        return true;
    }
    
    
    static SGPropertyResults checkDataElementGroupVisibility(SGIElementGroupSetXY obs,
    		SGPropertyMap map, SGPropertyResults iResult) {
    	
        SGPropertyResults result = (SGPropertyResults) iResult.clone();

        Boolean lineVisible = null;
        Boolean symbolVisible = null;
        Boolean barVisible = null;

        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);
            if (SGUtilityText.isEqualString(COM_DATA_LINE_VISIBLE, key)) {
                final Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_DATA_LINE_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                lineVisible = b;
            } else if (SGUtilityText.isEqualString(COM_DATA_SYMBOL_VISIBLE, key)) {
                final Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_DATA_SYMBOL_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
            	symbolVisible = b;
            } else if (SGUtilityText.isEqualString(COM_DATA_BAR_VISIBLE, key)) {
                final Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_DATA_BAR_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
            	barVisible = b;
            }
        }
        
        // current values
        boolean curLineVisible = obs.isLineVisible();
        boolean curSymbolVisible = obs.isSymbolVisible();
        boolean curBarVisible = obs.isBarVisible();

        // new values
        boolean lineVisibleNew = (lineVisible != null) ? lineVisible.booleanValue() : curLineVisible;
        boolean symbolVisibleNew = (symbolVisible != null) ? symbolVisible.booleanValue() : curSymbolVisible;
        boolean barVisibleNew = (barVisible != null) ? barVisible.booleanValue() : curBarVisible;

        // Enabled to hide all types of drawing elements at the same time.
        /*
        if (!lineVisibleNew && !symbolVisibleNew && !barVisibleNew) {
        	// if all elements are set invisible, returns error status
        	if (lineVisible != null) {
                result.putResult(COM_DATA_LINE_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
        	}
        	if (symbolVisible != null) {
                result.putResult(COM_DATA_SYMBOL_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
        	}
        	if (barVisible != null) {
                result.putResult(COM_DATA_BAR_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
        	}
        } else {
        	if (lineVisible != null) {
                result.putResult(COM_DATA_LINE_VISIBLE, SGPropertyResults.SUCCEEDED);
                obs.setLineVisible(lineVisibleNew);
        	}
        	if (symbolVisible != null) {
                result.putResult(COM_DATA_SYMBOL_VISIBLE, SGPropertyResults.SUCCEEDED);
                obs.setSymbolVisible(symbolVisibleNew);
        	}
        	if (barVisible != null) {
                result.putResult(COM_DATA_BAR_VISIBLE, SGPropertyResults.SUCCEEDED);
                obs.setBarVisible(barVisibleNew);
        	}
        }
        */

    	if (lineVisible != null) {
            result.putResult(COM_DATA_LINE_VISIBLE, SGPropertyResults.SUCCEEDED);
            obs.setLineVisible(lineVisibleNew);
    	}
    	if (symbolVisible != null) {
            result.putResult(COM_DATA_SYMBOL_VISIBLE, SGPropertyResults.SUCCEEDED);
            obs.setSymbolVisible(symbolVisibleNew);
    	}
    	if (barVisible != null) {
            result.putResult(COM_DATA_BAR_VISIBLE, SGPropertyResults.SUCCEEDED);
            obs.setBarVisible(barVisibleNew);
    	}

    	return result;
    }
    
    static double calcSizeValue(final double[] valueArray, SGAxis axis, 
    		final boolean horizontal) {
    	
        final double sizeMinFactor = 0.010;
        final double minValue = axis.getMinDoubleValue();
        final double maxValue = axis.getMaxDoubleValue();
        final double minWidth = (maxValue - minValue) * sizeMinFactor;
        
    	final double[] values = valueArray.clone();
        Arrays.sort(values);
        double minDiff = Double.MAX_VALUE;
        for (int ii = 0; ii < values.length - 1; ii++) {
            final double diff = Math.abs(values[ii + 1] - values[ii]);
            if (diff < minWidth) {
                continue;
            }
            if (diff < minDiff) {
                minDiff = diff;
            }
        }
        
        final double size;
        if (minDiff == Double.MAX_VALUE) {
            size = 1.0;
        } else {
            minDiff = SGUtilityNumber.getNumberInRangeOrder(minDiff, axis, 
            		SGIConstants.AXIS_SCALE_EFFECTIVE_DIGIT, BigDecimal.ROUND_HALF_UP);
            size = minDiff;
        }
        
        return size;
    }

    static void showGraphToolTip(SGFigureElementGraph graph, String str) {
    	graph.getComponent().setToolTipText(str);
    }
    
    /**
    *
    * @param list
    * @return
    */
   public static Rectangle2D getBoundingBox(final ArrayList list) {
       ArrayList rectList = new ArrayList();
       for (int ii = 0; ii < list.size(); ii++) {
           Object obj = list.get(ii);
           if (obj == null) {
               throw new NullPointerException("obj==null");
           }
           if (!(obj instanceof SGIDrawingElementJava2D)) {
               throw new IllegalArgumentException(
                       "!(obj instanceof SGIDrawingElementJava2D)");
           }

           SGIDrawingElementJava2D el = (SGIDrawingElementJava2D) obj;
           rectList.add(el.getElementBounds());
       }

       Rectangle2D rectAll = SGUtility.createUnion(rectList);

       return rectAll;
   }

   /**
    * Calculates and returns the bounding box for given points.
    *
    * @param pointList
    *           the list of points
    * @return the bounding box for given points
    */
   public static Rectangle2D getPointsBoundingBox(final List<Point2D> pointList) {
   	Rectangle2D rect = new Rectangle2D.Double();
   	if (pointList.size() == 0) {
   		return null;
   	}
   	double xMin = 0.0;
   	double xMax = 0.0;
   	double yMin = 0.0;
   	double yMax = 0.0;
   	for (int ii = 0; ii < pointList.size(); ii++) {
   		Point2D pos = pointList.get(ii);
   		final double x = pos.getX();
   		final double y = pos.getY();
   		if (ii == 0) {
   			xMin = x;
   			xMax = x;
   			yMin = y;
   			yMax = y;
   		} else {
   			if (x < xMin) {
   				xMin = x;
   			}
   			if (x > xMax) {
   				xMax = x;
   			}
   			if (y < yMin) {
   				yMin = y;
   			}
   			if (y > yMax) {
   				yMax = y;
   			}
   		}
   	}
   	final double width = xMax - xMin;
   	final double height = yMax - yMin;
   	rect.setRect(xMin, yMin, width, height);
   	return rect;
   }

   /**
    *
    * @param itr
    * @return
    */
   public static ArrayList getSegmentList(final PathIterator itr) {
       ArrayList list = new ArrayList();
       while (!itr.isDone()) {
           final float[] array = new float[6];
           final int type = itr.currentSegment(array);

           if (type == PathIterator.SEG_MOVETO
                   || type == PathIterator.SEG_LINETO) {
               list.add(new Point2D.Float(array[0], array[1]));
           } else if (type == PathIterator.SEG_QUADTO) {
               list.add(new Point2D.Float(array[0], array[1]));
               list.add(new Point2D.Float(array[2], array[3]));
           } else if (type == PathIterator.SEG_CUBICTO) {
               list.add(new Point2D.Float(array[0], array[1]));
               list.add(new Point2D.Float(array[2], array[3]));
               list.add(new Point2D.Float(array[4], array[5]));
           }

           itr.next();
       }

       return list;
   }

   /**
    * Draw a circle.
    *
    * @param x
    *            the x-coordinate
    * @param y
    *            the y-coordinate
    * @param size
    *            size of circle
    * @param lineWidth
    *            width of line
    * @param lineColor
    *            color of line
    * @param color
    *            color of body
    * @param g2d
    *            graphics context
    */
   public static void drawCircle(final float x, final float y, final float size,
           final float lineWidth, final Color lineColor, final Color color,
           final Graphics2D g2d) {
       Shape circle = new Ellipse2D.Float(x - 0.5f * size, y - 0.5f * size,
               size, size);
       drawSymbol(circle, lineWidth, lineColor, color, g2d);
   }

   /**
    * Draw a circle.
    *
    * @param pos
    *            location
    * @param size
    *            size of circle
    * @param lineWidth
    *            width of line
    * @param lineColor
    *            color of line
    * @param color
    *            color of body
    * @param g2d
    *            graphics context
    */
   public static void drawCircle(final Point2D pos, final float size,
           final float lineWidth, final Color lineColor, final Color color,
           final Graphics2D g2d) {
       final float x = (float) pos.getX();
       final float y = (float) pos.getY();
       drawCircle(x, y, size, lineWidth, lineColor, color, g2d);
   }

   /**
    * Draw a square.
    *
    * @param pos
    *            location
    * @param size
    *            size of square
    * @param lineWidth
    *            width of line
    * @param lineColor
    *            color of line
    * @param color
    *            color of body
    * @param g2d
    *            graphics context
    */
   public static void drawSquare(final Point2D pos, final float size,
           final float lineWidth, final Color lineColor, final Color color,
           final Graphics2D g2d) {
       final float x = (float) pos.getX();
       final float y = (float) pos.getY();
       Shape circle = new Rectangle2D.Float(x - 0.5f * size, y - 0.5f * size,
               size, size);
       drawSymbol(circle, lineWidth, lineColor, color, g2d);
   }

   /**
    * Draw a triangle.
    *
    * @param pos
    *            location
    * @param size
    *            size of triangle
    * @param lineWidth
    *            width of line
    * @param lineColor
    *            color of line
    * @param color
    *            color of body
    * @param g2d
    *            graphics context
    */
   public static void drawTriangle(final Point2D pos, final float size,
           final float lineWidth, final Color lineColor, final Color color,
           final Graphics2D g2d) {
       final float x = (float) pos.getX();
       final float y = (float) pos.getY();
       final float half = 0.5f*size;
       int x1 = (int)Math.floor(x - 0.95f*half);
       int x2 = (int)x;
       int x3 = (int)Math.ceil(x + 0.95f*half);
       int y1 = (int)Math.floor(y - 0.55f*half);
       int y2 = (int)Math.ceil(y + 1.10f*half);
       int y3 = y1;
       int[] xpoints = { x1, x2, x3 };
       int[] ypoints = { y1, y2, y3 };
       int npoints = 3;
       Shape triangle = new Polygon(xpoints, ypoints, npoints);
       drawSymbol(triangle, lineWidth, lineColor, color, g2d);
   }

   /**
    * Draw a symbol.
    *
    * @param sh
    * @param lineWidth
    * @param lineColor
    * @param color
    * @param g2d
    */
   public static void drawSymbol(final Shape sh, final float lineWidth,
           final Color lineColor, final Color color, final Graphics2D g2d) {
       g2d.setPaint(color);
       g2d.fill(sh);
       g2d.setPaint(lineColor);
       g2d.setStroke(new BasicStroke(lineWidth));
       g2d.draw(sh);
   }

   /**
    * Returns a new Stroke object.
    *
    * @param type -
    *            the line type of a BasicStroke
    * @param width -
    *            the width of a BasicStroke
    * @param cap -
    *            the decoration of the ends of a BasicStroke
    * @param join -
    *            the decoration applied where path segments meet
    * @param miterLimit -
    *            the limit to trim the miter join. The miterlimit must be
    *            greater than or equal to 1.0f.
    * @param dashPhase -
    *            the offset to start the dashing pattern
    * @return a BasicStroke object
    */
   public static BasicStroke getBasicStroke(final int type, final float width,
           final int cap, final int join, final float miterLimit,
           final float dashPhase) {
       BasicStroke bs = null;
       switch (type) {
       // solid line
       case LINE_TYPE_SOLID: {
           bs = new BasicStroke(width, cap, join);
           break;
       }

       // broken line
       case LINE_TYPE_BROKEN: {
           final float[] dash = { 2.0f * width, width };
           bs = new BasicStroke(width, cap, join, miterLimit, dash, dashPhase);
           break;
       }

       // dotted line
       case LINE_TYPE_DOTTED: {
           final float[] dash = { width };
           bs = new BasicStroke(width, cap, join, miterLimit, dash, dashPhase);
           break;
       }

       // dashed line
       case LINE_TYPE_DASHED: {
           final float[] dash = { 4.0f * width, width, width, width };
           bs = new BasicStroke(width, cap, join, miterLimit, dash, dashPhase);
           break;
       }

       // dashed line
       case LINE_TYPE_DOUBLE_DASHED: {
           final float[] dash = { 4.0f * width, width, width, width, width,
                   width };
           bs = new BasicStroke(width, cap, join, miterLimit, dash, dashPhase);
           break;
       }

       default: {
           throw new IllegalArgumentException("Line type is invalid.");
       }
       }

       return bs;
   }

   /**
    * Returns the gradient of a segment between given two points.
    *
    * @return the gradient in units of radian
    */
   public static float getGradient(final float startX, final float startY,
           final float endX, final float endY) {
       final float dx = endX - startX;
       final float dy = endY - startY;
       final float adx = Math.abs(dx);
       final float ady = Math.abs(dy);
       if (adx < Float.MIN_VALUE && ady < Float.MIN_VALUE) {
           return 0.0f;
       }

       float grad;
       if (adx < Float.MIN_VALUE) {
           if (dy > 0.0f) {
               grad = 0.50f * (float) Math.PI;
           } else {
               grad = 1.50f * (float) Math.PI;
           }
       } else if (ady < Float.MIN_VALUE) {
           if (dx > 0.0f) {
               grad = 0.0f;
           } else {
               grad = (float) Math.PI;
           }
       } else {
           final float tangent = dy / dx;
           if (dx > 0.0f) {
               if (dy > 0.0f) {
                   grad = (float) Math.atan(tangent);
               } else {
                   grad = (float) (2.0 * Math.PI + Math.atan(tangent));
               }
           } else {
               grad = (float) (Math.PI + Math.atan(tangent));
           }
       }

       return grad;
   }

	/**
	 * Modifies an input text string for the compatibility with previous
	 * releases older than 2_0_0.
	 *
	 * @param str
	 *            a text string
	 * @param versionNumber
	 *            version number
	 * @return modified text string
	 */
	public static String modifyStringForCompatibility(final String str, final String versionNumber) {
		if (SGUtility.isVersionNumberEqualOrSmallerThanPermittingEmptyString(versionNumber, "2.0.0")) {
			// when given version number is smaller than or equal to 2.0.0, returns a modified text string
			SGStringBraceModifier mod = new SGStringBraceModifier();
			return mod.modify(str);
		} else {
			// otherwise, returns a given text string
			return str;
		}
	}
	
   public static Map<String, SGProperties> getElementGroupPropertiesMap(
   		SGIElementGroupSetXY gs) {
       Map<String, SGProperties> pMap = new HashMap<String, SGProperties>();
       pMap.put(SGIElementGroupSetXY.ELEMENT_GROUP_LINE, gs.getLineGroup().getProperties());
       pMap.put(SGIElementGroupSetXY.ELEMENT_GROUP_SYMBOL, gs.getSymbolGroup().getProperties());
       pMap.put(SGIElementGroupSetXY.ELEMENT_GROUP_BAR, gs.getBarGroup().getProperties());
       if (gs.isErrorBarAvailable()) {
       	pMap.put(SGIElementGroupSetXY.ELEMENT_GROUP_ERROR_BAR, gs.getErrorBarGroup().getProperties());
       }
       if (gs.isTickLabelAvailable()) {
       	pMap.put(SGIElementGroupSetXY.ELEMENT_GROUP_TICK_LABEL, gs.getTickLabelGroup().getProperties());
       }
       return pMap;
   }
   
   public static boolean setElementGroupPropertiesMap(
   		SGIElementGroupSetXY gs, Map<String, SGProperties> pMap) {
   	gs.getLineGroup().setProperties(pMap.get(SGIElementGroupSetXY.ELEMENT_GROUP_LINE));
   	gs.getSymbolGroup().setProperties(pMap.get(SGIElementGroupSetXY.ELEMENT_GROUP_SYMBOL));
   	gs.getBarGroup().setProperties(pMap.get(SGIElementGroupSetXY.ELEMENT_GROUP_BAR));
       if (gs.isErrorBarAvailable()) {
       	gs.getErrorBarGroup().setProperties(pMap.get(SGIElementGroupSetXY.ELEMENT_GROUP_ERROR_BAR));
       }
       if (gs.isTickLabelAvailable()) {
       	gs.getTickLabelGroup().setProperties(pMap.get(SGIElementGroupSetXY.ELEMENT_GROUP_TICK_LABEL));
       }
       return true;
   }

   public static Map<String, SGProperties> getElementGroupPropertiesMap(
   		SGIElementGroupSetSXYZ gs) {
       Map<String, SGProperties> pMap = new HashMap<String, SGProperties>();
       pMap.put(SGIElementGroupSetSXYZ.ELEMENT_GROUP_COLOR_MAP, gs.getColorMap().getProperties());
       return pMap;
   }

   public static boolean setElementGroupPropertiesMap(
   		SGIElementGroupSetSXYZ gs, Map<String, SGProperties> pMap) {
   	gs.getColorMap().setProperties(pMap.get(SGIElementGroupSetSXYZ.ELEMENT_GROUP_COLOR_MAP));
       return true;
   }

   public static Map<String, SGProperties> getElementGroupPropertiesMap(
   		SGIElementGroupSetVXY gs) {
       Map<String, SGProperties> pMap = new HashMap<String, SGProperties>();
       pMap.put(SGIElementGroupSetVXY.ELEMENT_GROUP_ARROW, gs.getArrowGroup().getProperties());
       return pMap;
   }

   public static boolean setElementGroupPropertiesMap(
   		SGIElementGroupSetVXY gs, Map<String, SGProperties> pMap) {
   	gs.getArrowGroup().setProperties(pMap.get(SGIElementGroupSetVXY.ELEMENT_GROUP_ARROW));
       return true;
   }

	public static List<Point2D> getAnchorPointList(Rectangle2D rect) {
		List<Point2D> list = new ArrayList<Point2D>();
		final float x = (float) rect.getX();
		final float y = (float) rect.getY();
		final float w = (float) rect.getWidth();
		final float h = (float) rect.getHeight();
		Point2D pos0 = new Point2D.Float(x, y);
		Point2D pos1 = new Point2D.Float(x + w, y);
		Point2D pos2 = new Point2D.Float(x, y + h);
		Point2D pos3 = new Point2D.Float(x + w, y + h);
		list.add(pos0);
		list.add(pos1);
		list.add(pos2);
		list.add(pos3);
		return list;
	}

}

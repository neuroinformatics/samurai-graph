package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.util.Iterator;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIVXYTypeData;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementArrow;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGIVXYDataConstants;

public class SGArrowUtility implements SGIVXYDataConstants, SGIDataCommandConstants {

    /**
     * Sets the line width with a given unit.
     * 
     * @param lw
     *           the line width to set
     * @param unit
     *           the unit for line width
     * @param arrows
     *           a group of arrows
     * @return true if succeeded
     */
    public static boolean setLineWidth(final float lw, final String unit, 
    		final SGElementGroupArrow arrows) {
        final Float lwNew = SGUtility.getLineWidth(lw, unit);
        if (lwNew == null) {
            return false;
        }
        if (arrows.setLineWidth(lwNew) == false) {
            return false;
        }
        return true;
    }
    
    /**
     * Sets the head size with a given unit.
     * @param size
     *           the head size to set
     * @param unit
     *           the unit for a given head size
     * @param arrows
     *           a group of arrows
     * @return true if succeeded
     */
    public static boolean setHeadSize(final float size, final String unit,
    		final SGElementGroupArrow arrows) {
        final Float sNew = SGUtility.calcPropertyValue(size, unit, ARROW_HEAD_SIZE_UNIT, 
                ARROW_HEAD_SIZE_MIN, ARROW_HEAD_SIZE_MAX, ARROW_HEAD_SIZE_MINIMAL_ORDER);
        if (sNew == null) {
            return false;
        }
        if (arrows.setHeadSize(sNew) == false) {
            return false;
        }
        return true;
    }

    /**
     * Sets the head open and close angle.
     * 
     * @param openAngle
     *           the head open angle to set in units of degree
     * @param closeAngle
     *           the head close angle to set in units of degree
     * @param arrows
     *           a group of arrows
     * @return true if succeeded
     */
    public static boolean setHeadAngle(final Float openAngle, final Float closeAngle,
    		final SGElementGroupArrow arrows) {
        final Float oNew = (openAngle != null) ? SGUtility.calcPropertyValue(openAngle, 
                null, null, ARROW_HEAD_OPEN_ANGLE_MIN, ARROW_HEAD_OPEN_ANGLE_MAX, 
                ARROW_HEAD_ANGLE_MINIMAL_ORDER) : Float.valueOf(arrows.mHeadOpenAngle);
        if (oNew == null) {
            return false;
        }
        final Float cNew = (closeAngle != null) ? SGUtility.calcPropertyValue(closeAngle, 
                null, null, ARROW_HEAD_CLOSE_ANGLE_MIN, ARROW_HEAD_CLOSE_ANGLE_MAX, 
                ARROW_HEAD_ANGLE_MINIMAL_ORDER) : Float.valueOf(arrows.mHeadCloseAngle);
        if (cNew == null) {
            return false;
        }
        if (cNew <= oNew) {
            return false;
        }
        arrows.mHeadOpenAngle = oNew;
        arrows.mHeadCloseAngle = cNew;
        return true;
    }

    /**
     * Sets the head open and close angle.
     * 
     * @param openAngle
     *           the head open angle to set in units of degree
     * @param closeAngle
     *           the head close angle to set in units of degree
     * @param arrows
     *           a group of arrows
     * @return true if succeeded
     */
    public static boolean setHeadAngle(final float openAngle, final float closeAngle,
    		final SGElementGroupArrow arrows) {
        final Float oNew = SGUtility.calcPropertyValue(openAngle, null, null, 
                ARROW_HEAD_OPEN_ANGLE_MIN, ARROW_HEAD_OPEN_ANGLE_MAX, 
                ARROW_HEAD_ANGLE_MINIMAL_ORDER);
        if (oNew == null) {
            return false;
        }
        final Float cNew = SGUtility.calcPropertyValue(closeAngle, null, null, 
                ARROW_HEAD_CLOSE_ANGLE_MIN, ARROW_HEAD_CLOSE_ANGLE_MAX, 
                ARROW_HEAD_ANGLE_MINIMAL_ORDER);
        if (cNew == null) {
            return false;
        }
        if (cNew <= oNew) {
            return false;
        }
        arrows.mHeadOpenAngle = oNew;
        arrows.mHeadCloseAngle = cNew;
        return true;
    }

    protected static SGPropertyResults setProperties(SGPropertyMap map,
            SGPropertyResults iResult, SGElementGroupArrow arrows) {

        SGPropertyResults result = (SGPropertyResults) iResult.clone();

        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);
            
            if (COM_DATA_ARROW_LINE_WIDTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_DATA_ARROW_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (arrows.setLineWidth(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_DATA_ARROW_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ARROW_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ARROW_LINE_TYPE.equalsIgnoreCase(key)) {
                final Integer type = SGDrawingElementLine.getLineTypeFromName(value);
                if (type == null) {
                    result.putResult(COM_DATA_ARROW_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (arrows.setLineType(type) == false) {
                    result.putResult(COM_DATA_ARROW_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ARROW_LINE_TYPE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ARROW_HEAD_SIZE.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_DATA_ARROW_HEAD_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (arrows.setHeadSize(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_DATA_ARROW_HEAD_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ARROW_HEAD_SIZE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ARROW_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (arrows.setColor(cl) == false) {
                        result.putResult(COM_DATA_ARROW_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(COM_DATA_ARROW_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (arrows.setColor(cl) == false) {
						result.putResult(COM_DATA_ARROW_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
                }
                result.putResult(COM_DATA_ARROW_COLOR, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ARROW_START_TYPE.equalsIgnoreCase(key)) {
                final Integer type = SGDrawingElementArrow.getArrowHeadTypeFromName(value);
                if (type == null) {
                    result.putResult(COM_DATA_ARROW_START_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (arrows.setStartHeadType(type) == false) {
                    result.putResult(COM_DATA_ARROW_START_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ARROW_START_TYPE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ARROW_END_TYPE.equalsIgnoreCase(key)) {
                final Integer type = SGDrawingElementArrow.getArrowHeadTypeFromName(value);
                if (type == null) {
                    result.putResult(COM_DATA_ARROW_END_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (arrows.setEndHeadType(type) == false) {
                    result.putResult(COM_DATA_ARROW_END_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ARROW_END_TYPE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ARROW_HEAD_OPEN_ANGLE.equalsIgnoreCase(key)) {
                Float openAngle = SGUtilityText.getFloat(value);
                if (openAngle == null) {
                    result.putResult(COM_DATA_ARROW_HEAD_OPEN_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (setHeadAngle(openAngle, null, arrows) == false) {
                    result.putResult(COM_DATA_ARROW_HEAD_OPEN_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ARROW_HEAD_OPEN_ANGLE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ARROW_HEAD_CLOSE_ANGLE.equalsIgnoreCase(key)) {
                Float closeAngle = SGUtilityText.getFloat(value);
                if (closeAngle == null) {
                    result.putResult(COM_DATA_ARROW_HEAD_CLOSE_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (setHeadAngle(null, closeAngle, arrows) == false) {
                    result.putResult(COM_DATA_ARROW_HEAD_CLOSE_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ARROW_HEAD_CLOSE_ANGLE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ARROW_HEAD_ANGLE.equalsIgnoreCase(key)) {
            	float[] angles = SGUtilityText.getFloatArray(value);
            	if (angles == null) {
                    result.putResult(COM_DATA_ARROW_HEAD_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
            	if (angles.length != 2) {
                    result.putResult(COM_DATA_ARROW_HEAD_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                if (arrows.setHeadAngle(angles[0], angles[1]) == false) {
                    result.putResult(COM_DATA_ARROW_HEAD_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ARROW_HEAD_ANGLE, SGPropertyResults.SUCCEEDED);
            }
        }            

        return result;
    }

    /*
    static boolean calcEndLocation(SGIVXYTypeData dataVXY, SGFigureElementGraph graph, 
    		SGElementGroupSetInGraph groupSet, final float magPerCM, final boolean invariant,
    		float[] startXArray, float[] startYArray, SGTuple2f[] endArray) {

        final double xyRatio = calcXYRatio(dataVXY, graph, groupSet, invariant);
        final float factor = groupSet.getMagnification() / (SGIConstants.CM_POINT_RATIO * magPerCM);
        
        final double[] xComponentArray = dataVXY.getXComponentArray();
        final double[] yComponentArray = dataVXY.getYComponentArray();
        final double[] magnitudeArray = dataVXY.getMagnitudeArray();
        
        if (invariant) {
    		final int xNum = startXArray.length;
    		final int yNum = startYArray.length;
    		for (int yy = 0; yy < yNum; yy++) {
            	if (Double.isNaN(startYArray[yy])) {
            		for (int xx = 0; xx < xNum; xx++) {
            			final int index = yy * xNum + xx;
                		endArray[index].setValues(Float.NaN, Float.NaN);
            		}
            	} else {
            		for (int xx = 0; xx < xNum; xx++) {
            			final int index = yy * xNum + xx;
            			final float endX;
            			final float endY;
                    	if (Double.isNaN(startXArray[xx])) {
                    		endX = Float.NaN;
                    		endY = Float.NaN;
                    	} else {
                            endX = startXArray[xx] + factor * (float) (xComponentArray[index]);
                            endY = startYArray[yy] - factor * (float) (yComponentArray[index]);
                    	}
                        endArray[index].setValues(endX, endY);
            		}
            	}
    		}
        } else {
    		final int xNum = startXArray.length;
    		final int yNum = startYArray.length;
    		for (int yy = 0; yy < yNum; yy++) {
            	if (Double.isNaN(startYArray[yy])) {
            		for (int xx = 0; xx < xNum; xx++) {
            			final int index = yy * xNum + xx;
                		endArray[index].setValues(Float.NaN, Float.NaN);
            		}
            	} else {
            		for (int xx = 0; xx < xNum; xx++) {
            			final int index = yy * xNum + xx;
                    	if (Double.isNaN(startXArray[xx])) {
                            endArray[index].setValues(Float.NaN, Float.NaN);
                    	} else {
                    		if (magnitudeArray[index] == 0.0) {
                    			endArray[index].setValues(startXArray[xx], startYArray[yy]);
                    		} else {
                        		calcEnd(startXArray[xx], startYArray[yy],
										xComponentArray[index],
										yComponentArray[index],
										magnitudeArray[index], factor, xyRatio,
										endArray[index]);
                    		}
                    	}
            		}
            	}
    		}
        }
        
        return true;
    }
    */
    
    static double calcXYRatio(SGIVXYTypeData dataVXY, SGFigureElementGraph graph, 
    		SGElementGroupSetInGraph groupSet, final boolean invariant) {
    	final SGAxis xAxis = groupSet.getXAxis();
    	final SGAxis yAxis = groupSet.getYAxis();
        double xyRatio = 0.0;
        if (!invariant) {
            final double xRange = xAxis.getMaxDoubleValue() - xAxis.getMinDoubleValue();
            final double yRange = yAxis.getMaxDoubleValue() - yAxis.getMinDoubleValue();
            xyRatio = (graph.getGraphRectHeight() / yRange)
                    / (graph.getGraphRectWidth() / xRange);
        }
        return xyRatio;
    }

    private static SGTuple2f calcOrthogonalEnd(final float startX, final float startY,
			final double xComponent, final double yComponent, final boolean invariant,
			final float factor, final double xyRatio) {
    	final SGTuple2f end = new SGTuple2f();
    	if (invariant) {
            final float endX = startX + factor * (float) (xComponent);
            final float endY = startY - factor * (float) (yComponent);
            end.setValues(endX, endY);
    	} else {
        	final double numerator = xyRatio * yComponent;
    		final double magnitude = Math.sqrt(xComponent * xComponent 
    				+ yComponent * yComponent);
    		double angle;
    		if (SGUtilityNumber.isMinusZero(xComponent)) {
    			if (numerator > 0) {
    				angle = Math.PI / 2;
    			} else {
    				angle = - Math.PI / 2;
    			}
    		} else {
    			final double tanValue = numerator / xComponent;
    			angle = Math.atan(tanValue);
    			if (xComponent < 0.0) {
    				angle += Math.PI;
    			}
    		}
    		end.setValues(getPolarEnd(startX, startY, magnitude, angle, factor));
    	}
    	return end;
	}

    private static SGTuple2f calcPolarEnd(final float startX, final float startY,
			final double magnitude, final double angle, final boolean invariant,
			final float factor, final double xyRatio) {
    	final SGTuple2f end = new SGTuple2f();
    	if (invariant) {
    		end.setValues(getPolarEnd(startX, startY, magnitude, angle, factor));
    	} else {
    		final double xComponent = magnitude * Math.cos(angle);
    		final double yComponent = magnitude * Math.sin(angle);
			end.setValues(getOrthogonalEnd(startX, startY, xComponent,
					yComponent, magnitude, factor, xyRatio));
    	}
    	return end;
	}
    
    private static SGTuple2f getOrthogonalEnd(final float startX, final float startY,
			final double xComponent, final double yComponent,
			final double magnitude, final float factor, final double xyRatio) {
    	SGTuple2f end = new SGTuple2f();
		final double tanValue = xyRatio * yComponent / xComponent;
		double angle = Math.atan(tanValue);
		if (xComponent < 0.0) {
			angle += Math.PI;
		}
		final double cosValue = Math.cos(angle);
		final double sinValue = Math.sin(angle);
		final float vx = factor * (float) (magnitude * cosValue);
		final float vy = -factor * (float) (magnitude * sinValue);
		final float endX = startX + vx;
		final float endY = startY + vy;
		end.setValues(endX, endY);
		return end;
	}

    private static SGTuple2f getPolarEnd(final float startX, final float startY,
    		final double magnitude, final double angle, final float factor) {
    	final SGTuple2f end = new SGTuple2f();
		final double cosValue = Math.cos(angle);
		final double sinValue = Math.sin(angle);
		final float endX = startX + factor * (float) (magnitude * cosValue);
		final float endY = startY - factor * (float) (magnitude * sinValue);
		end.setValues(endX, endY);
		return end;
    }

    static boolean calcEndLocation(SGIVXYTypeData dataVXY, SGFigureElementGraph graph, 
    		SGElementGroupSetInGraph groupSet, final float magPerCM, final boolean invariant,
    		double[] firstValueArray, double[] secondValueArray,
    		SGTuple2f[] startArray, SGTuple2f[] endArray) {
    	final boolean polar = dataVXY.isPolar();
        final float factor = groupSet.getMagnification() / (SGIConstants.CM_POINT_RATIO * magPerCM);
        final double xyRatio = calcXYRatio(dataVXY, graph, groupSet, invariant);
        if (polar) {
            final double[] magnitudeArray = firstValueArray;
            final double[] angleArray = secondValueArray;
            for (int ii = 0; ii < startArray.length; ii++) {
	        	if (Double.isNaN(magnitudeArray[ii]) || Double.isNaN(angleArray[ii]) 
	        			|| magnitudeArray[ii] < 0.0) {
	        		endArray[ii].setValues(Float.NaN, Float.NaN);
	        	} else if (magnitudeArray[ii] == 0.0) {
	        		endArray[ii].setValues(startArray[ii]);
	        	} else {
	            	SGTuple2f end = calcPolarEnd(startArray[ii].x, startArray[ii].y,
							magnitudeArray[ii], angleArray[ii], invariant, factor, xyRatio);
	                endArray[ii].setValues(end);
	        	}
            }
        } else {
            final double[] xComponentArray = firstValueArray;
            final double[] yComponentArray = secondValueArray;
            for (int ii = 0; ii < startArray.length; ii++) {
            	if (Double.isNaN(xComponentArray[ii]) || Double.isNaN(yComponentArray[ii])) {
            		endArray[ii].setValues(Float.NaN, Float.NaN);
            	} else {
            		SGTuple2f end = calcOrthogonalEnd(startArray[ii].x, startArray[ii].y,
    						xComponentArray[ii], yComponentArray[ii], invariant, factor, xyRatio);
            		endArray[ii].setValues(end);
            	}
            }
        }
        return true;
    }

    static SGTuple2f calcEndLocation(final float startX, final float startY,
    		final double fValue, final double sValue, final boolean polar,
    		final float magPerCM, final boolean invariant,
    		final float factor, final double xyRatio) {
    	final SGTuple2f endPoint = new SGTuple2f();
    	if (Double.isNaN(startX) || Double.isNaN(startY)
    			|| Double.isNaN(fValue) || Double.isNaN(sValue)) {
        	endPoint.setValues(Float.NaN, Float.NaN);
    	} else {
            if (polar) {
            	final double magnitude = sValue;
            	if (magnitude < 0.0) {
                	endPoint.setValues(Float.NaN, Float.NaN);
            	} else if (magnitude == 0.0) {
            		endPoint.setValues(startX, startY);
            	} else {
    				endPoint.setValues(SGArrowUtility.calcPolarEnd(startX, startY, 
    						fValue, sValue, invariant, factor, xyRatio));
            	}
            } else {
				endPoint.setValues(SGArrowUtility.calcOrthogonalEnd(startX, startY, 
						fValue, sValue, invariant, factor, xyRatio));
            }
    	}
        return endPoint;
    }


}

package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGGradationPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGPatternPaint;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGSelectablePaint;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFMultipleData;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;

public abstract class SGElementGroupBarForData extends SGElementGroupBar implements 
        SGISXYDataConstants, SGIDataCommandConstants {

    /**
     * The default constructor.
     *
     */
    public SGElementGroupBarForData(SGISXYTypeData data) {
        super();

        // set default properties
        this.setVisible(DEFAULT_BAR_VISIBLE);
        
        SGSelectablePaint paint = new SGSelectablePaint();
        paint.setFillColor(DEFAULT_BAR_COLOR);
        paint.setSelectedPaintStyle(SGSelectablePaint.STYLE_INDEX_FILL);
        paint.setMagnification(this.mMagnification);
        this.setInnerPaint(paint);
        
        this.setEdgeLineWidth(DEFAULT_BAR_EDGE_LINE_WIDTH, LINE_WIDTH_UNIT);
        this.setEdgeLineColor(DEFAULT_BAR_EDGE_LINE_COLOR);
        this.setEdgeLineVisible(DEFAULT_BAR_EDGE_LINE_VISIBLE);
        this.mBaselineValue = DEFAULT_BAR_BASELINE_VALUE;
        this.setOffsetX(DEFAULT_BAR_SHIFT_X);
        this.setOffsetY(DEFAULT_BAR_SHIFT_Y);
        this.setInterval(DEFAULT_BAR_INTERVAL);
        
        if (data != null) {
            if (SGDataUtility.isNetCDFData(data.getDataType())==false) {
                final boolean vertical;
                if (data.isErrorBarAvailable()) {
                    vertical = data.isErrorBarVertical();
                } else if (data.isTickLabelAvailable()) {
                    vertical = data.isTickLabelHorizontal();
                } else {
                    vertical = true;
                }
                this.setVertical(vertical);
            } else {
                if (data instanceof SGSXYNetCDFData) {
                    SGSXYNetCDFData ndata = (SGSXYNetCDFData)data;
                    final boolean vertical = ndata.isXVariableCoordinate();
                    this.setVertical(vertical);
                } else if (data instanceof SGSXYNetCDFMultipleData) {
                    SGSXYNetCDFMultipleData ndata = (SGSXYNetCDFMultipleData)data;
                    final boolean vertical = ndata.isXVariableCoordinate();
                    this.setVertical(vertical);
                }
                
            }
        }
    }

    /**
     * Sets the edge line width in a given unit.
     * 
     * @param lw
     *           the edge line width to set
     * @param unit
     *           the unit for given line width
     * @return true if succeeded
     */
    public boolean setEdgeLineWidth(final float lw, final String unit) {
        final Float lwNew = SGUtility.getLineWidth(lw, unit);
        if (lwNew == null) {
            return false;
        }
        if (this.setEdgeLineWidth(lwNew) == false) {
            return false;
        }
        return true;
    }

    protected SGPropertyResults setProperties(SGPropertyMap map,
            SGPropertyResults iResult) {

        SGPropertyResults result = (SGPropertyResults) iResult.clone();

        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);
            
            if (COM_DATA_BAR_BASELINE_VALUE.equalsIgnoreCase(key)) {
                final Number baseline = SGUtilityText.getDouble(value);
                if (baseline == null) {
                    result.putResult(COM_DATA_BAR_BASELINE_VALUE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (SGUtility.isValidPropertyValue(baseline.doubleValue()) == false) {
                    result.putResult(COM_DATA_BAR_BASELINE_VALUE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setBaselineValue(baseline.doubleValue()) == false) {
                    result.putResult(COM_DATA_BAR_BASELINE_VALUE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_BASELINE_VALUE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_BODY_WIDTH_VALUE.equalsIgnoreCase(key)) {
                final Number width = SGUtilityText.getDouble(value);
                if (width == null) {
                    result.putResult(COM_DATA_BAR_BODY_WIDTH_VALUE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (SGUtility.isValidPropertyValue(width.doubleValue()) == false) {
                    result.putResult(COM_DATA_BAR_BODY_WIDTH_VALUE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setWidthValue(width.doubleValue()) == false) {
                    result.putResult(COM_DATA_BAR_BODY_WIDTH_VALUE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_BODY_WIDTH_VALUE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_BODY_PAINT_STYLE.equalsIgnoreCase(key)) {
                final Integer index = SGSelectablePaint.getStyleIndex(value);
                if (index == null) {
                    result.putResult(COM_DATA_BAR_BODY_PAINT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setInnerPaintStyle(index) == false) {
                    result.putResult(COM_DATA_BAR_BODY_PAINT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_BODY_PAINT_STYLE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_BODY_FILL_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.parseColorString(value);
                if (cl == null) {
                    result.putResult(COM_DATA_BAR_BODY_FILL_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setInnerFillColor(cl) == false) {
                    result.putResult(COM_DATA_BAR_BODY_FILL_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_BODY_FILL_COLOR, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_BODY_PATTERN_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.parseColorString(value);
                if (cl == null) {
                    result.putResult(COM_DATA_BAR_BODY_PATTERN_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setInnerPatternColor(cl) == false) {
                    result.putResult(COM_DATA_BAR_BODY_PATTERN_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_BODY_PATTERN_COLOR, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_BODY_PATTERN_TYPE.equalsIgnoreCase(key)) {
                final Integer type = SGPatternPaint.getTypeFromName(value);
                if (type == null) {
                    result.putResult(COM_DATA_BAR_BODY_PATTERN_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setInnerPatternType(type) == false) {
                    result.putResult(COM_DATA_BAR_BODY_PATTERN_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_BODY_PATTERN_TYPE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_BODY_GRADATION_COLOR1.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.parseColorString(value);
                if (cl == null) {
                    result.putResult(COM_DATA_BAR_BODY_GRADATION_COLOR1, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setInnerGradationColor1(cl) == false) {
                    result.putResult(COM_DATA_BAR_BODY_GRADATION_COLOR1, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_BODY_GRADATION_COLOR1, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_BODY_GRADATION_COLOR2.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.parseColorString(value);
                if (cl == null) {
                    result.putResult(COM_DATA_BAR_BODY_GRADATION_COLOR2, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setInnerGradationColor2(cl) == false) {
                    result.putResult(COM_DATA_BAR_BODY_GRADATION_COLOR2, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_BODY_GRADATION_COLOR2, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_BODY_GRADATION_DIRECTION.equalsIgnoreCase(key)) {
                final Integer index = SGGradationPaint.getDirectionIndex(value);
                if (index == null) {
                    result.putResult(COM_DATA_BAR_BODY_GRADATION_DIRECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setInnerGradationDirection(index) == false) {
                    result.putResult(COM_DATA_BAR_BODY_GRADATION_DIRECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_BODY_GRADATION_DIRECTION, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_BODY_GRADATION_ORDER.equalsIgnoreCase(key)) {
                final Integer index = SGGradationPaint.getOrderIndex(value);
                if (index == null) {
                    result.putResult(COM_DATA_BAR_BODY_GRADATION_ORDER, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setInnerGradationOrder(index) == false) {
                    result.putResult(COM_DATA_BAR_BODY_GRADATION_ORDER, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_BODY_GRADATION_ORDER, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_BODY_TRANSPARENCY.equalsIgnoreCase(key)) {
                final Integer num = SGUtilityText.getInteger(value, SGIConstants.percent);
                if (num == null) {
                    result.putResult(COM_DATA_BAR_BODY_TRANSPARENCY, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setTransparentPercent(num.intValue()) == false) {
                    result.putResult(COM_DATA_BAR_BODY_TRANSPARENCY, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_BODY_TRANSPARENCY, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_LINE_WIDTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_DATA_BAR_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setEdgeLineWidth(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_DATA_BAR_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_LINE_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.parseColorString(value);
                if (cl == null) {
                    result.putResult(COM_DATA_BAR_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setEdgeLineColor(cl) == false) {
                    result.putResult(COM_DATA_BAR_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_LINE_COLOR, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_LINE_VISIBLE.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_DATA_BAR_LINE_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setEdgeLineVisible(b.booleanValue()) == false) {
                    result.putResult(COM_DATA_BAR_LINE_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_LINE_VISIBLE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_VERTICAL.equalsIgnoreCase(key)) {
                final Boolean vertical = SGUtilityText.getBoolean(value);
                if (vertical == null) {
                    result.putResult(COM_DATA_BAR_VERTICAL, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setVertical(vertical.booleanValue()) == false) {
                    result.putResult(COM_DATA_BAR_VERTICAL, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_VERTICAL, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_INTERVAL.equalsIgnoreCase(key)) {
                Number num = SGUtilityText.getDouble(value);
                if (num == null) {
                    result.putResult(COM_DATA_BAR_INTERVAL, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                    result.putResult(COM_DATA_BAR_INTERVAL, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setInterval(num.doubleValue()) == false) {
                    result.putResult(COM_DATA_BAR_INTERVAL, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_INTERVAL, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_OFFSET_X.equalsIgnoreCase(key)) {
                Number num = SGUtilityText.getDouble(value);
                if (num == null) {
                    result.putResult(COM_DATA_BAR_OFFSET_X, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                    result.putResult(COM_DATA_BAR_OFFSET_X, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setOffsetX(num.doubleValue()) == false) {
                    result.putResult(COM_DATA_BAR_OFFSET_X, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_OFFSET_X, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_BAR_OFFSET_Y.equalsIgnoreCase(key)) {
                Number num = SGUtilityText.getDouble(value);
                if (num == null) {
                    result.putResult(COM_DATA_BAR_OFFSET_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                    result.putResult(COM_DATA_BAR_OFFSET_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setOffsetY(num.doubleValue()) == false) {
                    result.putResult(COM_DATA_BAR_OFFSET_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_BAR_OFFSET_Y, SGPropertyResults.SUCCEEDED);
            }
        }            

        return result;
    }

    protected boolean getProperties(SGPropertyMap map) {
    	SGPropertyUtility.addProperty(map, COM_DATA_BAR_VISIBLE, 
    			this.isVisible());
    	SGPropertyUtility.addProperty(map, COM_DATA_BAR_BASELINE_VALUE, 
    			this.getBaselineValue());
    	SGPropertyUtility.addProperty(map, COM_DATA_BAR_BODY_WIDTH_VALUE, 
    			this.getWidthValue());
    	String[] barKeys = {
    			COM_DATA_BAR_BODY_PAINT_STYLE,
    			COM_DATA_BAR_BODY_FILL_COLOR,
    			COM_DATA_BAR_BODY_PATTERN_COLOR,
    			COM_DATA_BAR_BODY_PATTERN_TYPE,
    			COM_DATA_BAR_BODY_GRADATION_COLOR1,
    			COM_DATA_BAR_BODY_GRADATION_COLOR2,
    			COM_DATA_BAR_BODY_GRADATION_DIRECTION,
    			COM_DATA_BAR_BODY_GRADATION_ORDER,
    			COM_DATA_BAR_BODY_TRANSPARENCY
    	};
    	SGSelectablePaint.COMMAND_KEYS[] comKeys = SGSelectablePaint.COMMAND_KEYS.values();
    	Map<SGSelectablePaint.COMMAND_KEYS, String> keyMap 
    			= new HashMap<SGSelectablePaint.COMMAND_KEYS, String>();
    	for (int ii = 0; ii < comKeys.length; ii++) {
    		keyMap.put(comKeys[ii], barKeys[ii]);
    	}
    	SGSelectablePaint paint = (SGSelectablePaint) this.getInnerPaint();
    	paint.getProperties(map, keyMap);
    	
    	SGPropertyUtility.addProperty(map, COM_DATA_BAR_LINE_WIDTH, 
    			SGUtility.getExportLineWidth(this.getEdgeLineWidth(LINE_WIDTH_UNIT)), 
    			LINE_WIDTH_UNIT);
    	SGPropertyUtility.addProperty(map, COM_DATA_BAR_LINE_COLOR, 
    			this.getEdgeLineColor());
    	SGPropertyUtility.addProperty(map, COM_DATA_BAR_VERTICAL, 
    			this.isVertical());
    	SGPropertyUtility.addProperty(map, COM_DATA_BAR_INTERVAL, 
    			this.getInterval());
    	SGPropertyUtility.addProperty(map, COM_DATA_BAR_OFFSET_X, 
    			this.getOffsetX());
    	SGPropertyUtility.addProperty(map, COM_DATA_BAR_OFFSET_Y, 
    			this.getOffsetY());
    	return true;
    }

}

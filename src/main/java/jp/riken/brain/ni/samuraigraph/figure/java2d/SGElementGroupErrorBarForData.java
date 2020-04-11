package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.util.Iterator;

import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementErrorBar;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;

public abstract class SGElementGroupErrorBarForData extends
        SGElementGroupErrorBar implements SGISXYDataConstants, SGIDataCommandConstants {

    /**
     * The default constructor.
     *
     */
    public SGElementGroupErrorBarForData(SGISXYTypeData data) {
        super();

        // set default properties
        this.setVisible(DEFAULT_ERROR_BAR_VISIBLE);
        this.setLineWidth(DEFAULT_ERROR_BAR_LINE_WIDTH, LINE_WIDTH_UNIT);
        this.setHeadSize(DEFAULT_ERROR_BAR_SYMBOL_SIZE,
                ERROR_BAR_HEAD_SIZE_UNIT);
        this.setHeadType(DEFAULT_ERROR_BAR_SYMBOL_TYPE);
        this.setLineWidth(DEFAULT_ERROR_BAR_LINE_WIDTH, LINE_WIDTH_UNIT);
        this.setErrorBarStyle(DEFAULT_ERROR_BAR_STYLE);
        this.setColor(DEFAULT_ERROR_BAR_COLOR);
        this.setVertical(true);
        this.setPositionOnLine(DEFAULT_ERROR_BAR_POSITION_IS_LINE);
    }

    /**
     * Sets the line width in a given unit.
     * 
     * @param lw
     *           the line width to set
     * @param unit
     *           the unit for given line width
     * @return true if succeeded
     */
    public boolean setLineWidth(final float lw, final String unit) {
        final Float lwNew = SGUtility.getLineWidth(lw, unit);
        if (lwNew == null) {
            return false;
        }
        if (this.setLineWidth(lwNew) == false) {
            return false;
        }
        return true;
    }

    /**
     * Sets the head size in a given unit.
     * 
     * @param size
     *          the head size to set
     * @param unit
     *          the unit for given head size
     * @return true if succeeded
     */
    public boolean setHeadSize(final float size, final String unit) {
        final Float sNew = SGUtility.calcPropertyValue(size, unit,
                ERROR_BAR_HEAD_SIZE_UNIT, ERROR_BAR_HEAD_SIZE_MIN,
                ERROR_BAR_HEAD_SIZE_MAX, ERROR_BAR_HEAD_SIZE_MINIMAL_ORDER);
        if (sNew == null) {
            return false;
        }
        if (this.setHeadSize(sNew) == false) {
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
            
            if (COM_DATA_ERROR_BAR_VISIBLE.equalsIgnoreCase(key)) {
                final Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_DATA_ERROR_BAR_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                this.setVisible(b.booleanValue());
                result.putResult(COM_DATA_ERROR_BAR_VISIBLE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ERROR_BAR_SYMBOL_TYPE.equalsIgnoreCase(key)) {
                final Integer type = SGDrawingElementErrorBar.getHeadTypeFromName(value);
                if (type == null) {
                    result.putResult(COM_DATA_ERROR_BAR_SYMBOL_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setHeadType(type) == false) {
                    result.putResult(COM_DATA_ERROR_BAR_SYMBOL_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ERROR_BAR_SYMBOL_TYPE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ERROR_BAR_SYMBOL_SIZE.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_DATA_ERROR_BAR_SYMBOL_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setHeadSize(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_DATA_ERROR_BAR_SYMBOL_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ERROR_BAR_SYMBOL_SIZE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ERROR_BAR_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setColor(cl) == false) {
                        result.putResult(COM_DATA_ERROR_BAR_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(COM_DATA_ERROR_BAR_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setColor(cl) == false) {
						result.putResult(COM_DATA_ERROR_BAR_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
                }
                result.putResult(COM_DATA_ERROR_BAR_COLOR, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ERROR_BAR_LINE_WIDTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_DATA_ERROR_BAR_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setLineWidth(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_DATA_ERROR_BAR_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ERROR_BAR_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ERROR_BAR_STYLE.equalsIgnoreCase(key)) {
                final Integer style = SGDrawingElementErrorBar.getErrorBarStyleFromName(value);
                if (style == null) {
                    result.putResult(COM_DATA_ERROR_BAR_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setErrorBarStyle(style) == false) {
                    result.putResult(COM_DATA_ERROR_BAR_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ERROR_BAR_STYLE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ERROR_BAR_POSITION.equalsIgnoreCase(key)) {
                final Integer position = getErrorBarPositionFromName(value);
                if (position == null) {
                    result.putResult(COM_DATA_ERROR_BAR_POSITION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (isValidErrorBarPosition(position)==false) {
                    result.putResult(COM_DATA_ERROR_BAR_POSITION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setPositionOnLine(position==ERROR_BAR_ON_LINE) == false) {
                    result.putResult(COM_DATA_ERROR_BAR_POSITION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ERROR_BAR_POSITION, SGPropertyResults.SUCCEEDED);
            }
        }            

        return result;
    }

    protected boolean getProperties(SGPropertyMap map) {
    	SGPropertyUtility.addProperty(map, COM_DATA_ERROR_BAR_VISIBLE, 
    			this.isVisible());
    	SGPropertyUtility.addProperty(map, COM_DATA_ERROR_BAR_SYMBOL_TYPE, 
    			SGDrawingElementErrorBar.getHeadTypeName(this.getHeadType()));
    	SGPropertyUtility.addProperty(map, COM_DATA_ERROR_BAR_SYMBOL_SIZE, 
    			SGUtility.getExportValue(this.getHeadSize(ERROR_BAR_HEAD_SIZE_UNIT), 
    					ERROR_BAR_HEAD_SIZE_MINIMAL_ORDER), ERROR_BAR_HEAD_SIZE_UNIT);
    	SGPropertyUtility.addProperty(map, COM_DATA_ERROR_BAR_COLOR, 
    			this.getColor());
    	SGPropertyUtility.addProperty(map, COM_DATA_ERROR_BAR_LINE_WIDTH, 
    			SGUtility.getExportLineWidth(this.getLineWidth(LINE_WIDTH_UNIT)), 
    			LINE_WIDTH_UNIT);
    	SGPropertyUtility.addProperty(map, COM_DATA_ERROR_BAR_STYLE, 
    			SGDrawingElementErrorBar.getErrorBarStyleName(this.getErrorBarStyle()));
    	SGPropertyUtility.addProperty(map, COM_DATA_ERROR_BAR_POSITION, 
    			getErrorBarPositionName(this.mPositionOnLine ? ERROR_BAR_ON_LINE : ERROR_BAR_ON_BAR));
    	return true;
    }

}

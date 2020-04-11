package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.util.Iterator;

import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementSymbol;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;


public abstract class SGElementGroupSymbolForData extends SGElementGroupSymbol implements 
        SGISXYDataConstants, SGIDataCommandConstants {

    /**
     * The default constructor.
     * 
     */
    public SGElementGroupSymbolForData(SGISXYTypeData data) {
        super();
        
        // set default properties
        this.setVisible(DEFAULT_SYMBOL_VISIBLE);
        this.setType(DEFAULT_SYMBOL_TYPE);
        this.setSize(DEFAULT_SYMBOL_SIZE, SYMBOL_SIZE_UNIT);
        this.setInnerColor(DEFAULT_SYMBOL_BODY_COLOR);
        this.setInnerTransparency(DEFAULT_SYMBOL_BODY_TRANSPARENCY);
        this.setLineWidth(DEFAULT_SYMBOL_LINE_WIDTH, LINE_WIDTH_UNIT);
        this.setLineColor(DEFAULT_SYMBOL_LINE_COLOR);
        this.setLineVisible(DEFAULT_SYMBOL_LINE_VISIBLE);
    }

    /**
     * Sets the size in a given unit.
     * 
     * @param size
     *           the symbol size to set
     * @param unit
     *           the unit for given symbol size
     * @return true if succeeded
     */
    public boolean setSize(final float size, final String unit) {
        final Float sNew = SGUtility.calcPropertyValue(size, unit, SYMBOL_SIZE_UNIT, 
                SYMBOL_SIZE_MIN, SYMBOL_SIZE_MAX, SYMBOL_SIZE_MINIMAL_ORDER);
        if (sNew == null) {
            return false;
        }
        if (this.setSize(sNew) == false) {
            return false;
        }
        return true;
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

    protected SGPropertyResults setProperties(SGPropertyMap map,
            SGPropertyResults iResult) {

        SGPropertyResults result = (SGPropertyResults) iResult.clone();

        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);
            
            if (COM_DATA_SYMBOL_TYPE.equalsIgnoreCase(key)) {
                final Integer type = SGDrawingElementSymbol.getSymbolTypeFromName(value);
                if (type == null) {
                    result.putResult(COM_DATA_SYMBOL_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setType(type) == false) {
                    result.putResult(COM_DATA_SYMBOL_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_SYMBOL_TYPE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_SYMBOL_SIZE.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_DATA_SYMBOL_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setSize(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_DATA_SYMBOL_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_SYMBOL_SIZE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_SYMBOL_BODY_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setInnerColor(cl) == false) {
                        result.putResult(COM_DATA_SYMBOL_BODY_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                    cl = SGUtilityText.parseColor(value);
                    if (cl == null) {
                        result.putResult(COM_DATA_SYMBOL_BODY_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setInnerColor(cl) == false) {
                        result.putResult(COM_DATA_SYMBOL_BODY_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                }
                result.putResult(COM_DATA_SYMBOL_BODY_COLOR, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_SYMBOL_BODY_TRANSPARENCY.equalsIgnoreCase(key)) {
                final Integer num = SGUtilityText.getInteger(value, SGIConstants.percent);
                if (num == null) {
                    result.putResult(COM_DATA_SYMBOL_BODY_TRANSPARENCY, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setInnerTransparency(num.intValue()) == false) {
                    result.putResult(COM_DATA_SYMBOL_BODY_TRANSPARENCY, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_SYMBOL_BODY_TRANSPARENCY, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_SYMBOL_LINE_WIDTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_DATA_SYMBOL_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setLineWidth(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_DATA_SYMBOL_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_SYMBOL_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_SYMBOL_LINE_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setLineColor(cl) == false) {
                        result.putResult(COM_DATA_SYMBOL_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                    cl = SGUtilityText.parseColor(value);
                    if (cl == null) {
                        result.putResult(COM_DATA_SYMBOL_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setLineColor(cl) == false) {
                        result.putResult(COM_DATA_SYMBOL_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                }
                result.putResult(COM_DATA_SYMBOL_LINE_COLOR, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_SYMBOL_LINE_VISIBLE.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_DATA_SYMBOL_LINE_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setLineVisible(b.booleanValue()) == false) {
                    result.putResult(COM_DATA_SYMBOL_LINE_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_SYMBOL_LINE_VISIBLE, SGPropertyResults.SUCCEEDED);
            }
        }            

        return result;
    }

    protected boolean getProperties(SGPropertyMap map) {
    	SGPropertyUtility.addProperty(map, COM_DATA_SYMBOL_VISIBLE, 
    			this.isVisible());
    	SGPropertyUtility.addProperty(map, COM_DATA_SYMBOL_TYPE, 
    			SGDrawingElementSymbol.getSymbolTypeName(this.getType()));
    	SGPropertyUtility.addProperty(map, COM_DATA_SYMBOL_SIZE, 
    			SGUtility.getExportValue(this.getSize(SYMBOL_SIZE_UNIT), 
    					SYMBOL_SIZE_MINIMAL_ORDER), SYMBOL_SIZE_UNIT);
    	SGPropertyUtility.addProperty(map, COM_DATA_SYMBOL_BODY_COLOR, 
    			this.getInnerColor());
    	SGPropertyUtility.addProperty(map, COM_DATA_SYMBOL_BODY_TRANSPARENCY, 
    			this.getInnerTransparency());
    	SGPropertyUtility.addProperty(map, COM_DATA_SYMBOL_LINE_VISIBLE, 
    			this.isLineVisible());
    	SGPropertyUtility.addProperty(map, COM_DATA_SYMBOL_LINE_WIDTH, 
    			SGUtility.getExportLineWidth(this.getLineWidth(LINE_WIDTH_UNIT)), 
    			LINE_WIDTH_UNIT);
    	SGPropertyUtility.addProperty(map, COM_DATA_SYMBOL_LINE_COLOR, 
    			this.getLineColor());
    	return true;
    }

}

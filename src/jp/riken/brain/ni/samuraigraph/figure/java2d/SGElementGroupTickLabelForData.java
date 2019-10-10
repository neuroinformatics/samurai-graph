package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.util.Iterator;

import jp.riken.brain.ni.samuraigraph.base.SGDateUtility;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;

public abstract class SGElementGroupTickLabelForData extends
        SGElementGroupTickLabel implements SGISXYDataConstants, SGIDataCommandConstants {

    /**
     * The default constructor.
     *
     */
    public SGElementGroupTickLabelForData(SGISXYTypeData data) {
        super();

        // set default properties
        this.setVisible(DEFAULT_TICK_LABEL_VISIBLE);
        this.setFontSize(DEFAULT_TICK_LABEL_FONT_SIZE, FONT_SIZE_UNIT);
        this.setFontName(DEFAULT_TICK_LABEL_FONT_NAME);
        this.setFontStyle(DEFAULT_TICK_LABEL_FONT_STYLE);
        this.setAngle(DEFAULT_TICK_LABEL_ANGLE);
        this.setColor(DEFAULT_TICK_LABEL_FONT_COLOR);
    	this.setHorizontalAlignment(true);
    }

    /**
     * Sets the font size in a given unit.
     * 
     * @param size
     *           the font size to set
     * @param unit
     *           the unit for given font size
     * @return true if succeeded
     */
    public boolean setFontSize(final float size, final String unit) {
        final Float sNew = SGUtility.getFontSize(size, unit);
        if (sNew == null) {
            return false;
        }
        if (this.setFontSize(sNew) == false) {
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
            
            if (COM_DATA_TICK_LABEL_VISIBLE.equalsIgnoreCase(key)) {
                final Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_DATA_TICK_LABEL_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                this.setVisible(b.booleanValue());
                result.putResult(COM_DATA_TICK_LABEL_VISIBLE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_TICK_LABEL_FONT_NAME.equalsIgnoreCase(key)) {
            	final String name = SGUtility.findFontFamilyName(value);
            	if (name == null) {
                    result.putResult(COM_DATA_TICK_LABEL_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                if (this.setFontName(name) == false) {
                    result.putResult(COM_DATA_TICK_LABEL_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_TICK_LABEL_FONT_NAME, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_TICK_LABEL_FONT_STYLE.equalsIgnoreCase(key)) {
                Integer style = SGUtilityText.getFontStyle(value);
                if (style == null) {
                    result.putResult(COM_DATA_TICK_LABEL_FONT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setFontStyle(style.intValue()) == false) {
                    result.putResult(COM_DATA_TICK_LABEL_FONT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_TICK_LABEL_FONT_STYLE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_TICK_LABEL_FONT_SIZE.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_DATA_TICK_LABEL_FONT_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setFontSize(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_DATA_TICK_LABEL_FONT_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_TICK_LABEL_FONT_SIZE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_TICK_LABEL_FONT_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setColor(cl) == false) {
                        result.putResult(COM_DATA_TICK_LABEL_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(COM_DATA_TICK_LABEL_FONT_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setColor(cl) == false) {
						result.putResult(COM_DATA_TICK_LABEL_FONT_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
                }
                result.putResult(COM_DATA_TICK_LABEL_FONT_COLOR, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_TICK_LABEL_ANGLE.equalsIgnoreCase(key)) {
                Float num = SGUtilityText.getFloat(value);
                if (num == null) {
                    result.putResult(COM_DATA_TICK_LABEL_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setAngle(num.floatValue()) == false) {
                    result.putResult(COM_DATA_TICK_LABEL_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_TICK_LABEL_ANGLE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_TICK_LABEL_DECIMAL_PLACES.equalsIgnoreCase(key)) {
                Integer num = SGUtilityText.getInteger(value);
                if (num == null) {
                    result.putResult(COM_DATA_TICK_LABEL_DECIMAL_PLACES, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setDecimalPlaces(num.intValue()) == false) {
                    result.putResult(COM_DATA_TICK_LABEL_DECIMAL_PLACES, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_TICK_LABEL_DECIMAL_PLACES, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_TICK_LABEL_EXPONENT.equalsIgnoreCase(key)) {
                Integer num = SGUtilityText.getInteger(value);
                if (num == null) {
                    result.putResult(COM_DATA_TICK_LABEL_EXPONENT, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setExponent(num.intValue()) == false) {
                    result.putResult(COM_DATA_TICK_LABEL_EXPONENT, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_TICK_LABEL_EXPONENT, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_TICK_LABEL_DATE_FORMAT.equalsIgnoreCase(key)) {
            	if (map.isDoubleQuoted(key) == false) {
                    result.putResult(COM_DATA_TICK_LABEL_DATE_FORMAT, 
                    		SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
            	if (!"".equals(value)) {
    				if (SGDateUtility.isValidDateFormat(value) == false) {
    					result.putResult(COM_DATA_TICK_LABEL_DATE_FORMAT,
    							SGPropertyResults.INVALID_INPUT_VALUE);
    					continue;
    				}
            	}
                if (this.setDateFormat(value) == false) {
                    result.putResult(COM_DATA_TICK_LABEL_DATE_FORMAT, 
                    		SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_TICK_LABEL_DATE_FORMAT, SGPropertyResults.SUCCEEDED);
            }
        }            

        return result;
    }

    protected boolean getProperties(SGPropertyMap map) {
    	SGPropertyUtility.addProperty(map, COM_DATA_TICK_LABEL_VISIBLE, 
    			this.isVisible());
    	SGPropertyUtility.addProperty(map, COM_DATA_TICK_LABEL_FONT_NAME, 
    			this.getFontName());
    	SGPropertyUtility.addProperty(map, COM_DATA_TICK_LABEL_FONT_STYLE, 
    			SGUtilityText.getFontStyleName(this.getFontStyle()));
    	SGPropertyUtility.addProperty(map, COM_DATA_TICK_LABEL_FONT_SIZE, 
    			SGUtility.getExportFontSize(this.getFontSize(FONT_SIZE_UNIT)), 
    			FONT_SIZE_UNIT);
    	SGPropertyUtility.addProperty(map, COM_DATA_TICK_LABEL_FONT_COLOR, 
    			this.getColor());
    	SGPropertyUtility.addProperty(map, COM_DATA_TICK_LABEL_ANGLE, 
    			SGUtility.getExportValue(this.getAngle(), 
    					TICK_LABEL_TEXT_ANGLE_MINIMAL_ORDER));
    	SGPropertyUtility.addProperty(map, COM_DATA_TICK_LABEL_DECIMAL_PLACES, 
    			this.getDecimalPlaces());
    	SGPropertyUtility.addProperty(map, COM_DATA_TICK_LABEL_EXPONENT, 
    			this.getExponent());
    	SGPropertyUtility.addQuotedStringProperty(map,
    			COM_DATA_TICK_LABEL_DATE_FORMAT, this.getDateFormat());
    	return true;
    }

}

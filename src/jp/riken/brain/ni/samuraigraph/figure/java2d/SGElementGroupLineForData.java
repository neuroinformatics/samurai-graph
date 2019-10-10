package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Iterator;

import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;

public abstract class SGElementGroupLineForData extends SGElementGroupLine
        implements SGISXYDataConstants, SGIDataCommandConstants {

    /**
     * Create a group of lines.
     */
    public SGElementGroupLineForData(SGISXYTypeData data) {
        super(DEFAULT_LINE_TYPE, DEFAULT_LINE_COLOR, DEFAULT_LINE_WIDTH);
        
        // set default properties
        this.setVisible(DEFAULT_LINE_VISIBLE);

        // setup the stroke object
        this.mStroke.setEndCap(BasicStroke.CAP_BUTT);
        this.mStroke.setJoin(BasicStroke.JOIN_ROUND);
    }

    /**
     * Set the line width in a given unit.
     * 
     * @param lw
     *            the line width to set
     * @param unit
     *            the unit for the given line width
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
            if (COM_DATA_LINE_WIDTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_DATA_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setLineWidth(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_DATA_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_LINE_TYPE.equalsIgnoreCase(key)) {
                final Integer type = SGDrawingElementLine.getLineTypeFromName(value);
                if (type == null) {
                    result.putResult(COM_DATA_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setLineType(type) == false) {
                    result.putResult(COM_DATA_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_LINE_TYPE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_LINE_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setColor(cl) == false) {
                        result.putResult(COM_DATA_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(COM_DATA_LINE_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setColor(cl) == false) {
						result.putResult(COM_DATA_LINE_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
                }
                result.putResult(COM_DATA_LINE_COLOR, SGPropertyResults.SUCCEEDED);
            }
            if (COM_DATA_LINE_CONNECT_ALL.equalsIgnoreCase(key)) {
            	Boolean b = SGUtilityText.getBoolean(value);
            	if (b == null) {
                    result.putResult(COM_DATA_LINE_CONNECT_ALL, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
            	if (this.setLineConnectingAll(b.booleanValue()) == false) {
                    result.putResult(COM_DATA_LINE_CONNECT_ALL, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                result.putResult(COM_DATA_LINE_CONNECT_ALL, SGPropertyResults.SUCCEEDED);
            }
        }

        return result;
    }
    
    protected boolean getProperties(SGPropertyMap map) {
    	SGPropertyUtility.addProperty(map, COM_DATA_LINE_VISIBLE, 
    			this.isVisible());
//    	SGCommandUtility.addProperty(map, COM_DATA_LINE_WIDTH, 
//    			this.getLineWidth(LINE_WIDTH_UNIT), LINE_WIDTH_UNIT);
//    	SGCommandUtility.addProperty(map, COM_DATA_LINE_TYPE, 
//    			SGDrawingElementLine.getLineTypeName(this.getLineType()));
//    	SGCommandUtility.addProperty(map, COM_DATA_LINE_COLOR, 
//    			this.getColor());
    	SGPropertyUtility.addProperty(map, COM_DATA_LINE_CONNECT_ALL, 
    			this.isLineConnectingAll());
    	return true;
    }
    
}

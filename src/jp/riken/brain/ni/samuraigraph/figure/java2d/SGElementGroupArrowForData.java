package jp.riken.brain.ni.samuraigraph.figure.java2d;

import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementArrow;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGIVXYDataConstants;

public abstract class SGElementGroupArrowForData extends SGElementGroupArrow implements 
        SGIVXYDataConstants, SGIDataCommandConstants {

    /**
     * The default constructor.
     *
     */
    public SGElementGroupArrowForData() {
        super();
        
        // set default properties
        this.setLineWidth(DEFAULT_LINE_WIDTH, LINE_WIDTH_UNIT);
        this.setLineType(DEFAULT_LINE_TYPE);
        this.setColor(DEFAULT_COLOR);
        this.setStartHeadType(DEFAULT_START_HEAD_TYPE);
        this.setEndHeadType(DEFAULT_END_HEAD_TYPE);
        this.setHeadSize(DEFAULT_HEAD_SIZE, ARROW_HEAD_SIZE_UNIT);
        this.setHeadAngle(DEFAULT_HEAD_OPEN_ANGLE, DEFAULT_HEAD_CLOSE_ANGLE);
    }

    /**
     * Sets the head size with a given unit.
     * @param size
     *           the head size to set
     * @param unit
     *           the unit for a given head size
     * @return true if succeeded
     */
    public boolean setHeadSize(final float size, final String unit) {
    	if (SGArrowUtility.setHeadSize(size, unit, this) == false) {
    		return false;
    	}
        this.updateHeadShape();
    	return true;
    }

    /**
     * Sets the line width with a given unit.
     * 
     * @param lw
     *           the line width to set
     * @param unit
     *           the unit for line width
     * @return true if succeeded
     */
    public boolean setLineWidth(final float lw, final String unit) {
    	if (SGArrowUtility.setLineWidth(lw, unit, this) == false) {
    		return false;
    	}
        this.updateHeadShape();
        return true;
    }

    /**
     * Sets the head open and close angle.
     * 
     * @param openAngle
     *           the head open angle to set in units of degree
     * @param closeAngle
     *           the head close angle to set in units of degree
     * @return true if succeeded
     */
    public boolean setHeadAngle(final Float openAngle, final Float closeAngle) {
        if (SGArrowUtility.setHeadAngle(openAngle, closeAngle, this) == false) {
        	return false;
        }
        this.updateHeadShape();
        return true;
    }

    /**
     * Sets the head open and close angle.
     * 
     * @param openAngle
     *           the head open angle to set in units of degree
     * @param closeAngle
     *           the head close angle to set in units of degree
     * @return true if succeeded
     */
    public boolean setHeadAngle(final float openAngle, final float closeAngle) {
        if (SGArrowUtility.setHeadAngle(openAngle, closeAngle, this) == false) {
        	return false;
        }
        this.updateHeadShape();
        return true;
    }

    protected SGPropertyResults setProperties(SGPropertyMap map,
            SGPropertyResults iResult) {
    	SGPropertyResults result = SGArrowUtility.setProperties(map, iResult, this);
    	if (result == null) {
    		return null;
    	}
        this.updateHeadShape();
        return result;
    }

    protected boolean getProperties(SGPropertyMap map) {
    	SGPropertyUtility.addProperty(map, COM_DATA_ARROW_LINE_WIDTH, 
    			SGUtility.getExportLineWidth(this.getLineWidth(LINE_WIDTH_UNIT)),
    			LINE_WIDTH_UNIT);
    	SGPropertyUtility.addProperty(map, COM_DATA_ARROW_LINE_TYPE, 
    			SGDrawingElementLine.getLineTypeName(this.getLineType()));
    	SGPropertyUtility.addProperty(map, COM_DATA_ARROW_HEAD_SIZE, 
    			SGUtility.getExportValue(this.getHeadSize(ARROW_HEAD_SIZE_UNIT), 
    					ARROW_HEAD_SIZE_MINIMAL_ORDER), ARROW_HEAD_SIZE_UNIT);
    	SGPropertyUtility.addProperty(map, COM_DATA_ARROW_COLOR, 
    			this.getColor());
    	SGPropertyUtility.addProperty(map, COM_DATA_ARROW_START_TYPE, 
    			SGDrawingElementArrow.getArrowHeadTypeName(this.getStartHeadType()));
    	SGPropertyUtility.addProperty(map, COM_DATA_ARROW_END_TYPE, 
    			SGDrawingElementArrow.getArrowHeadTypeName(this.getEndHeadType()));
    	StringBuffer sbAngle = new StringBuffer();
    	sbAngle.append('(');
    	sbAngle.append(SGUtility.getExportValue(this.getHeadOpenAngle(), 
    			ARROW_HEAD_ANGLE_MINIMAL_ORDER));
    	sbAngle.append(',');
    	sbAngle.append(SGUtility.getExportValue(this.getHeadCloseAngle(), 
    			ARROW_HEAD_ANGLE_MINIMAL_ORDER));
    	sbAngle.append(')');
    	SGPropertyUtility.addProperty(map, COM_DATA_ARROW_HEAD_ANGLE, sbAngle.toString());
    	return true;
    }

}

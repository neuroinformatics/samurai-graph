package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.util.Iterator;

import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYZDataConstants;

public abstract class SGElementGroupPseudocolorMapForData extends
		SGElementGroupPseudocolorMap implements SGISXYZDataConstants, SGIDataCommandConstants {

    /**
     * The default constructor.
     *
     */
	public SGElementGroupPseudocolorMapForData() {
		super();
		
        // initialize
        this.setVisible(DEFAULT_COLOR_MAP_VISIBLE);
	}

    protected SGPropertyResults setProperties(SGPropertyMap map,
            SGPropertyResults iResult) {

        SGPropertyResults result = (SGPropertyResults) iResult.clone();

        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);
            
            if (COM_DATA_RECTANGLE_WIDTH.equalsIgnoreCase(key)) {
                Number num = SGUtilityText.getDouble(value);
                if (num == null) {
                    result.putResult(COM_DATA_RECTANGLE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                    result.putResult(COM_DATA_RECTANGLE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setWidthValue(num.doubleValue()) == false) {
                    result.putResult(COM_DATA_RECTANGLE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_RECTANGLE_WIDTH, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_RECTANGLE_HEIGHT.equalsIgnoreCase(key)) {
                Number num = SGUtilityText.getDouble(value);
                if (num == null) {
                    result.putResult(COM_DATA_RECTANGLE_HEIGHT, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                    result.putResult(COM_DATA_RECTANGLE_HEIGHT, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setHeightValue(num.doubleValue()) == false) {
                    result.putResult(COM_DATA_RECTANGLE_HEIGHT, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_RECTANGLE_HEIGHT, SGPropertyResults.SUCCEEDED);
            }
        }            

        return result;
    }

    protected boolean getProperties(SGPropertyMap map) {
    	if (!this.isGridMode()) {
        	SGPropertyUtility.addProperty(map, COM_DATA_RECTANGLE_WIDTH, 
        			this.getWidthValue());
        	SGPropertyUtility.addProperty(map, COM_DATA_RECTANGLE_HEIGHT, 
        			this.getHeightValue());
    	}
    	return true;
    }

}

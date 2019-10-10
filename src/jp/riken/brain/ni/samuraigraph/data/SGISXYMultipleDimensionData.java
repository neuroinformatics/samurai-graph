package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;


public interface SGISXYMultipleDimensionData {

	
    public SGPickUpDimensionInfo getPickUpDimensionInfo();

    public boolean setPickUpDimensionInfo(SGPickUpDimensionInfo info);

	public SGIntegerSeriesSet getIndices();

}

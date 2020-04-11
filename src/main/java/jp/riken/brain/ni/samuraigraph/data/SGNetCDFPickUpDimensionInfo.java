package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;

public class SGNetCDFPickUpDimensionInfo extends SGPickUpDimensionInfo {

	private String mDimensionName = null;

	public SGNetCDFPickUpDimensionInfo() {
		super();
	}

	public SGNetCDFPickUpDimensionInfo(final String name, final SGIntegerSeriesSet indices) {
		super(indices);
		this.mDimensionName = name;
	}
	
	public String getDimensionName() {
		return this.mDimensionName;
	}

	@Override
    public boolean equals(Object obj) {
    	if (!super.equals(obj)) {
    		return false;
    	}
    	if (!(obj instanceof SGNetCDFPickUpDimensionInfo)) {
    		return false;
    	}
    	SGNetCDFPickUpDimensionInfo info = (SGNetCDFPickUpDimensionInfo) obj;
    	if (!SGUtility.equals(this.mDimensionName, info.mDimensionName)) {
    		return false;
    	}
    	return true;
	}
}

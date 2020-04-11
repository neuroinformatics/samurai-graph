package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;

public class SGMDArrayPickUpDimensionInfo extends SGPickUpDimensionInfo {

	private Map<String, Integer> mDimensionMap = new HashMap<String, Integer>();
	
	public SGMDArrayPickUpDimensionInfo() {
		super();
	}
	
	public SGMDArrayPickUpDimensionInfo(Map<String, Integer> dimensionMap,
			final SGIntegerSeriesSet indices) {
		super(indices);
		this.mDimensionMap = new HashMap<String, Integer>(dimensionMap);
	}
	
	public Integer getDimension(String name) {
		return this.mDimensionMap.get(name);
	}
	
	public Map<String, Integer> getDimensionMap() {
		return new HashMap<String, Integer>(this.mDimensionMap);
	}
	
	public List<String> getVariableNames() {
		return new ArrayList<String>(this.mDimensionMap.keySet());
	}

	@Override
    public boolean equals(Object obj) {
    	if (!super.equals(obj)) {
    		return false;
    	}
    	if (!(obj instanceof SGMDArrayPickUpDimensionInfo)) {
    		return false;
    	}
    	SGMDArrayPickUpDimensionInfo info = (SGMDArrayPickUpDimensionInfo) obj;
    	if (!SGUtility.equals(this.mDimensionMap, info.mDimensionMap)) {
    		return false;
    	}
    	return true;
    }
}

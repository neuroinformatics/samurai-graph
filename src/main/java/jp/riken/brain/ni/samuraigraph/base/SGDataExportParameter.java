package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.List;


public class SGDataExportParameter extends SGExportParameter implements SGIConstants {
	
	private List<SGData> mExportedDataList = new ArrayList<SGData>();

	public SGDataExportParameter(OPERATION type, List<SGData> dataList) {
		super(type);
		if (dataList == null) {
			throw new IllegalArgumentException("dataList == null");
		}
		this.mExportedDataList = new ArrayList<SGData>(dataList);
	}
	
	public boolean canExport(SGData data) {
		return this.mExportedDataList.contains(data);
	}
}

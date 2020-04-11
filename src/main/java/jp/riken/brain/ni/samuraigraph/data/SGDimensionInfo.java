package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import ucar.nc2.Dimension;

/**
 * The information class for netCDF Dimension.
 *
 */
public class SGDimensionInfo {
	
	/**
	 * The name.
	 */
	private String mName = null;

	/**
	 * The length.
	 */
	private int mLength = 0;

	/**
	 * Builds an object.
	 * 
	 * @param dim
	 *           a netCDF dimension
	 */
	public SGDimensionInfo(final Dimension dim) {
		super();
		this.mName = dim.getName();
		this.mLength = dim.getLength();
	}
	
	/**
	 * Returns the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.mName;
	}

	/**
	 * Returns the length.
	 * 
	 * @return the length
	 */
	public int getLength() {
		return this.mLength;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SGDimensionInfo)) {
			return false;
		}
		SGDimensionInfo info = (SGDimensionInfo) obj;
		if (!SGUtility.equals(this.mName, info.mName)) {
			return false;
		}
		if (this.mLength != info.mLength) {
			return false;
		}
		return true;
	}
	
	
}

package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGUtility;


/**
 * The buffer for scalar-XYZ type data.
 *
 */
public class SGSXYZDataBuffer extends SGOneDimensionalDataBuffer {
	
	// The array of x values.
	private double[] mXValues = null;
	
	// The array of y values.
	private double[] mYValues = null;
	
	// The array of z values.
	private double[] mZValues = null;
	
	public SGSXYZDataBuffer(double[] xValues, double[] yValues, double[] zValues) {
		super();
		if (xValues == null || yValues == null || zValues == null) {
			throw new IllegalArgumentException("xValues == null || yValues == null || zValues == null");
		}
		if (xValues.length != yValues.length) {
			throw new IllegalArgumentException("xValues.length != yValues.length");
		}
		if (xValues.length != zValues.length) {
			throw new IllegalArgumentException("xValues.length != zValues.length");
		}
		this.mLength = xValues.length;
		this.mXValues = SGUtility.copyDoubleArray(xValues);
		this.mYValues = SGUtility.copyDoubleArray(yValues);
		this.mZValues = SGUtility.copyDoubleArray(zValues);
	}
	
	public double[] getXValues() {
		return SGUtility.copyDoubleArray(this.mXValues);
	}
	
	public double[] getYValues() {
		return SGUtility.copyDoubleArray(this.mYValues);
	}
	
	public double[] getZValues() {
		return SGUtility.copyDoubleArray(this.mZValues);
	}
	
    /**
     * Clones this object.
     * 
     * @return the copy of this object
     */
	@Override
    public Object clone() {
		SGSXYZDataBuffer obj = (SGSXYZDataBuffer) super.clone();
		obj.mXValues = this.getXValues();
		obj.mYValues = this.getYValues();
		obj.mZValues = this.getZValues();
		return obj;
	}
	
	/**
	 * Returns a text string for the information key of grid type.
	 * 
	 * @return a text string for the information key of grid type
	 */
	@Override
	public String getGridTypeKey() {
		return SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG;
	}

	/**
	 * Returns the data type.
	 * 
	 * @return the data type
	 */
	@Override
	public String getDataType() {
		return SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA;
	}

}

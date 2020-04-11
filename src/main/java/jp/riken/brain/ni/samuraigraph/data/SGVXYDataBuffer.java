package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGUtility;


/**
 * The buffer for vector-XY type data.
 *
 */
public class SGVXYDataBuffer extends SGOneDimensionalDataBuffer {
	
	// The array of x values.
	private double[] mXValues = null;
	
	// The array of y values.
	private double[] mYValues = null;
	
	// The array of the first component values.
	private double[] mFirstComponentValues = null;
	
	// The array of the second component values.
	private double[] mSecondComponentValues = null;

	// The polar flag.
	private boolean mPolarFlag = false;
	
	public SGVXYDataBuffer(double[] xValues, double[] yValues, double[] fValues, double[] sValues,
			final boolean polar) {
		super();
		if (xValues == null || yValues == null || fValues == null || sValues == null) {
			throw new IllegalArgumentException(
					"xValues == null || yValues == null || fValues == null || sValues == null");
		}
		if (xValues.length != yValues.length) {
			throw new IllegalArgumentException("xValues.length != yValues.length");
		}
		if (xValues.length != fValues.length) {
			throw new IllegalArgumentException("xValues.length != fValues.length");
		}
		if (xValues.length != sValues.length) {
			throw new IllegalArgumentException("xValues.length != sValues.length");
		}
		this.mLength = xValues.length;
		this.mXValues = SGUtility.copyDoubleArray(xValues);
		this.mYValues = SGUtility.copyDoubleArray(yValues);
		this.mFirstComponentValues = SGUtility.copyDoubleArray(fValues);
		this.mSecondComponentValues = SGUtility.copyDoubleArray(sValues);
		this.mPolarFlag = polar;
	}
	
	public double[] getXValues() {
		return SGUtility.copyDoubleArray(this.mXValues);
	}
	
	public double[] getYValues() {
		return SGUtility.copyDoubleArray(this.mYValues);
	}
	
	public double[] getFirstComponentValues() {
		return SGUtility.copyDoubleArray(this.mFirstComponentValues);
	}
	
	public double[] getSecondComponentValues() {
		return SGUtility.copyDoubleArray(this.mSecondComponentValues);
	}
	
	public boolean isPolar() {
		return this.mPolarFlag;
	}
	
    /**
     * Clones this object.
     * 
     * @return the copy of this object
     */
	@Override
    public Object clone() {
		SGVXYDataBuffer obj = (SGVXYDataBuffer) super.clone();
		obj.mXValues = this.getXValues();
		obj.mYValues = this.getYValues();
		obj.mFirstComponentValues = this.getFirstComponentValues();
		obj.mSecondComponentValues = this.getSecondComponentValues();
		return obj;
	}
	
	/**
	 * Returns a text string for the information key of grid type.
	 * 
	 * @return a text string for the information key of grid type
	 */
	@Override
	public String getGridTypeKey() {
		return SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG;
	}

	/**
	 * Returns the data type.
	 * 
	 * @return the data type
	 */
	@Override
	public String getDataType() {
		return SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA;
	}

}

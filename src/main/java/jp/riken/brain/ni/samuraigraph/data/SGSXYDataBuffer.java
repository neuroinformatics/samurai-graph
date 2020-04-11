package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;



/**
 * The buffer for single scalar-XY type data.
 *
 */
public class SGSXYDataBuffer extends SGOneDimensionalDataBuffer {
	
	private SGSXYMultipleDataBuffer mMultipleBuffer = null;
	
	SGSXYDataBuffer() {
		super();
	}

	public SGSXYDataBuffer(double[] xValues, double[] yValues) {
		this(xValues, yValues, false, null);
	}

	public SGSXYDataBuffer(double[] xValues, double[] yValues, final Boolean dateFlag,
			SGDate[] dateArray) {
		this(xValues, yValues, dateFlag, dateArray, null, null, null, false, null);
	}

	public SGSXYDataBuffer(double[] xValues, double[] yValues, final Boolean dateFlag,
			SGDate[] dateArray, final Boolean yHolderFlag,
			double[] lowerErrorValues, double[] upperErrorValues, 
			Boolean sameErrorValuesFlag, String[] tickLabels) {
		this();
		if (xValues == null || yValues == null) {
			throw new IllegalArgumentException("xValues == null || yValues == null");
		}
		if (xValues.length != yValues.length) {
			throw new IllegalArgumentException("xValues.length != yValues.length");
		}
		this.mLength = xValues.length;
		double[][] xValues2dim = new double[][] { SGUtility.copyDoubleArray(xValues) };
		double[][] yValues2dim = new double[][] { SGUtility.copyDoubleArray(yValues) };
		SGDate[] dateValues = (dateArray != null) ? dateArray.clone() : null;
		double[][] leValues = (lowerErrorValues != null) ? new double[][] { SGUtility.copyDoubleArray(lowerErrorValues) } : null;
		double[][] ueValues = (upperErrorValues != null) ? new double[][] { SGUtility.copyDoubleArray(upperErrorValues) } : null;
		Boolean[] sameErrorVariableFlags = new Boolean[] { sameErrorValuesFlag };
		String[][] tickLabelValues = (tickLabels != null) ? new String[][] { SGUtility.copyStringArray(tickLabels) } : null;
		this.mMultipleBuffer = new SGSXYMultipleDataBuffer(xValues2dim, yValues2dim,
				dateFlag, dateValues, yHolderFlag, leValues, ueValues, sameErrorVariableFlags, 
				tickLabelValues);
	}

	public double[] getXValues() {
		return this.mMultipleBuffer.getXValues()[0];
	}
	
	public double[] getYValues() {
		return this.mMultipleBuffer.getYValues()[0];
	}

	public double[] getLowerErrorValues() {
		double[][] values = this.mMultipleBuffer.getLowerErrorValues();
		return (values != null) ? values[0] : null;
	}
	
	public double[] getUpperErrorValues() {
		double[][] values = this.mMultipleBuffer.getUpperErrorValues();
		return (values != null) ? values[0] : null;
	}
	
	public String[] getTickLabels() {
		String[][] values = this.mMultipleBuffer.getTickLabels();
		return (values != null) ? values[0] : null;
	}
	
	@Override
	public String getDataType() {
		return SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA;
	}

	public Boolean getXDateFlag() {
		return this.mMultipleBuffer.getDateFlag();
	}
	
	public Boolean hasSameErrorVariableFlag() {
		Boolean[] values = this.mMultipleBuffer.hasSameErrorVariable();
		return (values != null) ? values[0] : null;
	}
	
	public SGDate[] getDateArray() {
		return this.mMultipleBuffer.getDateArray();
	}

	public SGSXYMultipleDataBuffer getMultipleDataBuffer() {
		if (this.mMultipleBuffer == null) {
			return null;
		}
		return (SGSXYMultipleDataBuffer) this.mMultipleBuffer.clone();
	}
}

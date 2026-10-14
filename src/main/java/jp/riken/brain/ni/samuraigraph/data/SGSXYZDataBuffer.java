package jp.riken.brain.ni.samuraigraph.data;

/** The buffer for scalar-XYZ type data. */
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
    this.mXValues = SGDataMiscUtility.copyDoubleArray(xValues);
    this.mYValues = SGDataMiscUtility.copyDoubleArray(yValues);
    this.mZValues = SGDataMiscUtility.copyDoubleArray(zValues);
  }

  public double[] getXValues() {
    return SGDataMiscUtility.copyDoubleArray(this.mXValues);
  }

  public double[] getYValues() {
    return SGDataMiscUtility.copyDoubleArray(this.mYValues);
  }

  public double[] getZValues() {
    return SGDataMiscUtility.copyDoubleArray(this.mZValues);
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
    return SGDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG;
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

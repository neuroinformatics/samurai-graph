package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGUtility;


/**
 * The buffer for scalar-XYZ type data on grid.
 *
 */
public class SGSXYZGridDataBuffer extends SGTwoDimensionalDataBuffer {

	// The list of blocks of z values.
	private List<SGXYSimpleDoubleValueIndexBlock> mZValueBlocks = null;
	
	public SGSXYZGridDataBuffer(double[] xValues, double[] yValues, 
			List<SGXYSimpleDoubleValueIndexBlock> zValueBlocks) {
		super(xValues, yValues);
		if (zValueBlocks == null) {
			throw new IllegalArgumentException("zValueBlocks == null");
		}
		this.checkBlocks(xValues, yValues, zValueBlocks, "zValueBlocks");
		this.mXValues = SGUtility.copyDoubleArray(xValues);
		this.mYValues = SGUtility.copyDoubleArray(yValues);
		this.mZValueBlocks = this.copyBlocks(zValueBlocks);
	}

	public SGSXYZGridDataBuffer(double[] xValues, double[] yValues, 
			double[][] zValues) {
		super(xValues, yValues);
		this.checkTwoDimensionalArray(xValues, yValues, zValues, "zValues");
		SGXYSimpleDoubleValueIndexBlock block = this.createBlock(xValues, yValues, zValues);
		this.mZValueBlocks = new ArrayList<SGXYSimpleDoubleValueIndexBlock>();
		this.mZValueBlocks.add(block);
	}
	
	public double[] getXValues() {
		return SGUtility.copyDoubleArray(this.mXValues);
	}
	
	public double[] getYValues() {
		return SGUtility.copyDoubleArray(this.mYValues);
	}
	
	public double[][] getZValues() {
		return this.getToDimensionalValues(this.mZValueBlocks);
	}
	
	public List<SGXYSimpleDoubleValueIndexBlock> getZValueBlocks() {
		return this.copyBlocks(this.mZValueBlocks);
	}
	
    /**
     * Clones this object.
     * 
     * @return the copy of this object
     */
	@Override
    public Object clone() {
		SGSXYZGridDataBuffer obj = (SGSXYZGridDataBuffer) super.clone();
		obj.mZValueBlocks = this.copyBlocks(this.mZValueBlocks);
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

package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGUtility;


/**
 * The buffer for vector-XY type data on grid.
 *
 */
public class SGVXYGridDataBuffer extends SGTwoDimensionalDataBuffer {

	// The list of blocks of first component values.
	private List<SGXYSimpleDoubleValueIndexBlock> mFirstComponentValueBlocks = null;

	// The list of blocks of second component values.
	private List<SGXYSimpleDoubleValueIndexBlock> mSecondComponentValueBlocks = null;
	
	// The polar flag.
	private boolean mPolarFlag = false;

	public SGVXYGridDataBuffer(double[] xValues, double[] yValues, 
			List<SGXYSimpleDoubleValueIndexBlock> fValueBlocks, 
			List<SGXYSimpleDoubleValueIndexBlock> sValueBlocks, final boolean polar) {
		super(xValues, yValues);
		if (fValueBlocks == null || sValueBlocks == null) {
			throw new IllegalArgumentException("fValueBlocks == null || sValueBlocks == null");
		}
		this.checkBlocks(xValues, yValues, fValueBlocks, "fValueBlocks");
		this.checkBlocks(xValues, yValues, sValueBlocks, "sValueBlocks");
		this.mXValues = SGUtility.copyDoubleArray(xValues);
		this.mYValues = SGUtility.copyDoubleArray(yValues);
		this.mFirstComponentValueBlocks = this.copyBlocks(fValueBlocks);
		this.mSecondComponentValueBlocks = this.copyBlocks(sValueBlocks);
		this.mPolarFlag = polar;
	}

	public SGVXYGridDataBuffer(double[] xValues, double[] yValues, double[][] fValues,
			double[][] sValues, final boolean polar) {
		super(xValues, yValues);
		this.checkTwoDimensionalArray(xValues, yValues, fValues, "fValues");
		this.checkTwoDimensionalArray(xValues, yValues, sValues, "sValues");
		SGXYSimpleDoubleValueIndexBlock fBlock = this.createBlock(xValues, yValues, fValues);
		this.mFirstComponentValueBlocks = new ArrayList<SGXYSimpleDoubleValueIndexBlock>();
		this.mFirstComponentValueBlocks.add(fBlock);
		SGXYSimpleDoubleValueIndexBlock sBlock = this.createBlock(xValues, yValues, sValues);
		this.mSecondComponentValueBlocks = new ArrayList<SGXYSimpleDoubleValueIndexBlock>();
		this.mSecondComponentValueBlocks.add(sBlock);
		this.mPolarFlag = polar;
	}
	
	public double[] getXValues() {
		return SGUtility.copyDoubleArray(this.mXValues);
	}
	
	public double[] getYValues() {
		return SGUtility.copyDoubleArray(this.mYValues);
	}

	public List<SGXYSimpleDoubleValueIndexBlock> getFirstComponentValueBlocks() {
		return this.copyBlocks(this.mFirstComponentValueBlocks);
	}

	public List<SGXYSimpleDoubleValueIndexBlock> getSecondComponentValueBlocks() {
		return this.copyBlocks(this.mSecondComponentValueBlocks);
	}

	public double[][] getFirstComponentValues() {
		return this.getToDimensionalValues(this.mFirstComponentValueBlocks);
	}
	
	public double[][] getSecondComponentValues() {
		return this.getToDimensionalValues(this.mSecondComponentValueBlocks);
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
		SGVXYGridDataBuffer obj = (SGVXYGridDataBuffer) super.clone();
		obj.mFirstComponentValueBlocks = this.copyBlocks(this.mFirstComponentValueBlocks);
		obj.mSecondComponentValueBlocks = this.copyBlocks(this.mSecondComponentValueBlocks);
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

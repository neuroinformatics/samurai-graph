package jp.riken.brain.ni.samuraigraph.data;

import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;

public class SGXYSimpleDoubleValueIndexBlock extends SGSimpleDoubleValueIndexBlock {

	public SGXYSimpleDoubleValueIndexBlock(final double[] values, SGIntegerSeries colSeries,
			SGIntegerSeries rowSeries) {
		super(values, new SGIntegerSeries[] { colSeries, rowSeries });
	}
	
	public SGIntegerSeries getXSeries() {
		return this.mSeriesArray[0];
	}
	
	public SGIntegerSeries getYSeries() {
		return this.mSeriesArray[1];
	}
	
	public boolean setValue(final double value, final int col, final int row) {
		SGIntegerSeries xSeries = this.getXSeries();
		final int xIndex = xSeries.search(col);
		if (xIndex < 0) {
			return false;
		}
		SGIntegerSeries ySeries = this.getYSeries();
		final int yIndex = ySeries.search(row);
		if (yIndex < 0) {
			return false;
		}
		final int colNum = xSeries.getLength();
		final int index = yIndex * colNum + xIndex;
		if (this.mValues[index] == value) {
			return false;
		}
		this.mValues[index] = value;
		return true;
	}

	public Double getValue(final int col, final int row) {
		SGIntegerSeries xSeries = this.getXSeries();
		final int xIndex = xSeries.search(col);
		if (xIndex < 0) {
			return null;
		}
		SGIntegerSeries ySeries = this.getYSeries();
		final int yIndex = ySeries.search(row);
		if (yIndex < 0) {
			return null;
		}
		final int colNum = xSeries.getLength();
		final int index = yIndex * colNum + xIndex;
		return this.mValues[index];
	}
	
	public static Double getDataViewerValue(List<SGXYSimpleDoubleValueIndexBlock> blockList,
			final int row, final int col) {
		for (SGXYSimpleDoubleValueIndexBlock block : blockList) {
			Double d = block.getValue(col, row);
			if (d != null) {
				return d;
			}
		}
		return null;
	}

}

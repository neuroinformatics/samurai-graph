package jp.riken.brain.ni.samuraigraph.base;


public class SGXYSimpleIndexBlock extends SGSimpleIndexBlock {

	public SGXYSimpleIndexBlock(SGIntegerSeries colSeries,
			SGIntegerSeries rowSeries) {
		super(new SGIntegerSeries[] { colSeries, rowSeries });
	}

	public SGIntegerSeries getXSeries() {
		return this.mSeriesArray[0];
	}
	
	public SGIntegerSeries getYSeries() {
		return this.mSeriesArray[1];
	}
}

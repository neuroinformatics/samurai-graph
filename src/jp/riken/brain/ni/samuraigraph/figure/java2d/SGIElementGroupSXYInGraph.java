package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;

public interface SGIElementGroupSXYInGraph extends SGIElementGroupInGraph {

	public boolean contains(final int x, final int y, final SGTuple2f[] pointsArray,
			final double[] xValues, final double[] yValues);
	
	public List<Integer> getIndicesAt(final int x, final int y,
			final SGTuple2f[] pointsArray, final double[] xValues,
			final double[] yValues);
	
	public String getToolTipTextSpatiallyVaried(List<Integer> indices);

}

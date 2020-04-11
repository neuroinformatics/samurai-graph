package jp.riken.brain.ni.samuraigraph.figure.java2d;

import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyle;

public interface SGIElementGroupSetSXY extends
		SGIElementGroupSetForData, SGIElementGroupSetXY {

	/**
	 * Creates drawing elements of error bars.
	 *
	 * @param dataXY
	 *             XY type data
	 * @return the group of error bars
	 */
	public SGElementGroupErrorBar createErrorBars(SGISXYTypeSingleData dataXY);

	/**
	 * Creates drawing elements of tick labels.
	 *
	 * @param dataXY
	 *             XY type data
	 * @return the group of tick labels
	 */
	public SGElementGroupTickLabel createTickLabels(SGISXYTypeSingleData dataXY);

	/**
	 * Update the text strings of tick labels.
	 */
	public void updateTickLabelStrings();

	/**
	 * Sets the direction of error bars.
	 *
	 * @param vertical
	 *           true to set vertical
	 * @return true if succeeded
	 */
	public boolean setErrorBarDirection(final boolean vertical);

	/**
	 * Sets the alignment of tick label.
	 *
	 * @param horizontal
	 *           true to align horizontally
	 * @return true if succeeded
	 */
	public boolean setTickLabelAlignment(final boolean horizontal);

    public int getSeriesIndex();

    public int getNumberOfSeries();

    public void setSeriesIndexAndNumber(final int index, final int number);

	public void setLineStyle(SGLineStyle style);
	
	public SGLineStyle getLineStyle();

}

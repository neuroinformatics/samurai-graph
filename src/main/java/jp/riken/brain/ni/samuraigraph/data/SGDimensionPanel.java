package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationTextConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGAnimationConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDateConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGPaintConstant.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataInformationKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataPropertyKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGNetCDFConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGArrowConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGFigureDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSXYDataConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGShapeConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGStringConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSymbolConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGTimingLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGVXYDataConstants.*;

import javax.swing.JPanel;

/** The base class of dimension panel. */
public abstract class SGDimensionPanel extends JPanel {

  private static final long serialVersionUID = 2695872968969587858L;

  /** The default constructor. */
  public SGDimensionPanel() {
    super();
  }

  /**
   * Sets the components enabled.
   *
   * @param enabled true to enable
   */
  public abstract void setComponentsEnabled(final boolean enabled);

  /** Returns the slider panel. */
  protected abstract SGSliderPanel getSliderPanel();

  /**
   * Sets the values for the slider.
   *
   * @param values the values
   * @param initIndex initial index of values
   */
  public void setValues(final double[] values, final int initIndex) {
    final int min = 0;
    final int max = (values.length == 0) ? 0 : (values.length - 1);

    // set to the slider
    this.getSliderPanel().setRange(min, max, initIndex);
  }
}

package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGIConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGILegendConstants.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

class BarInLegend extends SGElementGroupBar.BarInGroup {
  /**
   * Builds a rectangle in a group of rectangles.
   *
   * @param group a group of rectangles
   */
  public BarInLegend(ElementGroupBar group, final int index) {
    super(group, index);
  }

  public float getWidth() {
    return this.mGroup.getRectangleWidth();
  }

  public float getHeight() {
    return this.mGroup.getRectangleHeight();
  }

  public boolean setWidth(final float w) {
    return this.mGroup.setRectangleWidth(w);
  }

  public boolean setHeight(final float h) {
    return this.mGroup.setRectangleHeight(h);
  }
}

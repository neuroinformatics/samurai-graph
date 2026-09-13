package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

class PseudocolorMapRectangleInLegend extends SGElementGroupPseudocolorMap.PseudocolorMapRectangle {
  /**
   * Builds a rectangle in a color map.
   *
   * @param index the index parameter
   * @param group a color map
   */
  public PseudocolorMapRectangleInLegend(SGElementGroupPseudocolorMap group, final int index) {
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

package jp.riken.brain.ni.samuraigraph.figure;

import jp.riken.brain.ni.samuraigraph.figure.SGAxisElement.AxisProperties;

/** Properties for a single axis. */
class FigureAxisProperties extends AxisProperties {

  float shift;

  /**
   * Returns whether a given object is equal to this.
   *
   * @param obj an object
   * @return true if a given object is equals to this
   */
  public boolean equals(final Object obj) {
    if ((obj instanceof FigureAxisProperties) == false) {
      return false;
    }
    if (super.equals(obj) == false) {
      return false;
    }
    FigureAxisProperties p = (FigureAxisProperties) obj;
    if (p.shift != this.shift) {
      return false;
    }
    return true;
  }
}

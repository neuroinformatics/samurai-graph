package jp.riken.brain.ni.samuraigraph.base;

/** An interface for the objects with visibility. */
public interface SGIVisible {
  /** Returns whether this object is visible. */
  public boolean isVisible();

  /**
   * Set visible or invisible.
   *
   * @param b - true sets visible and false sets invisible
   */
  public void setVisible(final boolean b);
}

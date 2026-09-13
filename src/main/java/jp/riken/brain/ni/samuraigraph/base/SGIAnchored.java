package jp.riken.brain.ni.samuraigraph.base;

public interface SGIAnchored {

  /**
   * @return true if this object is anchored.
   */
  public boolean isAnchored();

  /**
   * @param anchored
   * @return true if succeeds.
   */
  public boolean setAnchored(final boolean anchored);
}

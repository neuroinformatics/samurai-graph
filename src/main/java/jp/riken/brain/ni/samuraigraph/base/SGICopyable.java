package jp.riken.brain.ni.samuraigraph.base;

/** An interface for the copyable objects. */
public interface SGICopyable {

  /**
   * Copy the object. (Deep Copy)
   *
   * @return A copied object.
   */
  public Object copy();
}

package jp.riken.brain.ni.samuraigraph.data;

abstract class SGDataCache implements Cloneable {

  SGDataCache() {
    super();
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new InternalError();
    }
  }
}

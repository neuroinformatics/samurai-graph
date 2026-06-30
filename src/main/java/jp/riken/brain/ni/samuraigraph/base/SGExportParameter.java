package jp.riken.brain.ni.samuraigraph.base;

import java.util.HashMap;
import java.util.Map;

/** Parameters for an operation. */
public class SGExportParameter {

  // The type of operation.
  private SGIConstants.OPERATION mType = null;

  // The map of parameters.
  private Map<String, Object> mParamMap = new HashMap<String, Object>();

  /**
   * Builds an object.
   *
   * @param type the type of operation
   */
  public SGExportParameter(SGIConstants.OPERATION type) {
    super();
    if (type == null) {
      throw new IllegalArgumentException("type == null");
    }
    this.mType = type;
  }

  /**
   * Returns the type.
   *
   * @return the type
   */
  public SGIConstants.OPERATION getType() {
    return this.mType;
  }

  /**
   * Adds a parameter.
   *
   * @param name the name
   * @param param the parameter
   */
  public void addParam(String name, Object param) {
    this.mParamMap.put(name, param);
  }

  /**
   * Returns the parameter of given name.
   *
   * @param name the name of a parameter
   * @return the parameter
   */
  public Object getParameter(String name) {
    return this.mParamMap.get(name);
  }
}

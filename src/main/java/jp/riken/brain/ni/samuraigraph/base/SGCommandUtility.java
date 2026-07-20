package jp.riken.brain.ni.samuraigraph.base;

public class SGCommandUtility {

  public static String createCommandString(
      final String cmd, final String id, final SGPropertyMap map) {
    StringBuilder sb = new StringBuilder();
    sb.append(cmd);
    sb.append('(');
    if (id != null) {
      sb.append(id);
      sb.append(", ");
    }
    sb.append(map.toString());
    sb.append(")\n");
    return sb.toString();
  }
}

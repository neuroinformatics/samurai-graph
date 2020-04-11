package jp.riken.brain.ni.samuraigraph.base;



public class SGCommandUtility {

    public static String createCommandString(final String cmd, final String id,
    		final SGPropertyMap map) {
		StringBuffer sb = new StringBuffer();
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

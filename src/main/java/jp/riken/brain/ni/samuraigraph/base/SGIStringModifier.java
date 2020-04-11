package jp.riken.brain.ni.samuraigraph.base;

/**
 * The class to modify a text string.
 *
 */
public interface SGIStringModifier {
	
	/**
	 * Modifies a given text string.
	 * 
	 * @param str
	 *           a text string
	 * @return modified result
	 */
	public String modify(String str);

}

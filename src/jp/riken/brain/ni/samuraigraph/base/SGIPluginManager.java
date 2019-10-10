package jp.riken.brain.ni.samuraigraph.base;

public interface SGIPluginManager {
	
	public static final String NO_PLUGIN = "(no plug-in files)";
	
	/**
	 * Executes the command for given window.
	 * 
	 * @param cmd
	 *           a text string of command
	 * @param wnd
	 *           a window
	 */
	public void execCommand(String cmd, SGDrawingWindow wnd);

	/**
	 * Executes the command for given window.
	 * 
	 * @param cmd
	 *           a text string of command
	 * @param wnd
	 *           a window
	 * @param dataArray
	 *           an array of data
	 * @param dataNameArray
	 *           an array of data name
	 */
	public void execCommand(String cmd, SGDrawingWindow wnd, SGData[] dataArray,
			String[] dataNameArray);

	/**
	 * Shows a dialog of the detail of plug-in files.
	 * 
	 * @param wnd
	 *           a window
	 */
	public void showPluginInfoDialog(SGDrawingWindow wnd);
	
}

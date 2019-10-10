package jp.riken.brain.ni.samuraigraph.base;

public interface SGIPlugin {
	
	/**
	 * Returns the name.
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Returns the description for the developer.
	 * 
	 * @return the description for the developer
	 */
	public String getDeveloper();
	
	/**
	 * Returns the description for the version.
	 * 
	 * @return the description for the version
	 */
	public String getVersion();

	/**
	 * Returns the description of the plug-in.
	 * 
	 * @return the description of the plug-in
	 */
	public String getDesc();

	/**
	 * Returns the file path.
	 * 
	 * @return the file path
	 */
	public String getPath();
	
	/**
	 * Returns a text string of the command.
	 * 
	 * @return a text string of command
	 */
	public String getCommand();

}

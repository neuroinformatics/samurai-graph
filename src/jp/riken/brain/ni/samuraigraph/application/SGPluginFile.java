package jp.riken.brain.ni.samuraigraph.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGIPlugin;

/**
 * Wrapper class for a plug-in file.
 *
 */
public class SGPluginFile implements SGIPlugin, SGIDataPluginConstants {
	
	private SGDataCalcLibrary mLibrary;
	
	private String mFilePath;
	
	private Map<String, String> mAttributeMap = new HashMap<String, String>();
	
	private List<Parameter> mParameterList = new ArrayList<Parameter>();
	
	public SGPluginFile(SGIDataCalcLibrary lib, String path) throws IOException {
		super();
		this.mLibrary = new SGDataCalcLibrary(lib);
		this.mFilePath = path;
	}
	
	public String getAttribute(String key) {
		return this.mAttributeMap.get(key);
	}
	
	public void setAttribute(String key, String value) {
		this.mAttributeMap.put(key, value);
	}
	
	public int getParameterNumber() {
		return this.mParameterList.size();
	}
	
	public Parameter getParameter(final int index) {
		return this.mParameterList.get(index);
	}
	
	public void addParameter(Parameter param) {
		this.mParameterList.add(param);
	}
	
	public SGDataCalcLibrary getLibrary() {
		return this.mLibrary;
	}
	
	/**
	 * Returns the name.
	 * 
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.mAttributeMap.get(KEY_PLUGIN_NAME);
	}

	/**
	 * Returns the file path.
	 * 
	 * @return the file path
	 */
	@Override
	public String getPath() {
		return this.mFilePath;
	}
	
	/**
	 * Returns a text string of the command.
	 * 
	 * @return a text string of command
	 */
	@Override
	public String getCommand() {
		// returns the file path as the command
		return this.getPath();
	}
	
	/**
	 * Returns the description of the plug-in.
	 * 
	 * @return the description of the plug-in
	 */
	@Override
	public String getDesc() {
		return this.mAttributeMap.get(KEY_PLUGIN_DESC);
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	/**
	 * Returns the description for the developer.
	 * 
	 * @return the description for the developer
	 */
	@Override
	public String getDeveloper() {
		return this.mAttributeMap.get(KEY_PLUGIN_DEVELOPER);
	}
	
	/**
	 * Returns the description for the version.
	 * 
	 * @return the description for the version
	 */
	@Override
	public String getVersion() {
		return this.mAttributeMap.get(KEY_PLUGIN_VERSION);
	}
	
    static class Parameter {
    	private String name;
    	private String def;
    	public Parameter(String name, String def) {
    		super();
    		this.name = (name != null) ? name : "";
    		this.def = (def != null) ? def : "";
    	}
    	public String getName() {
    		return this.name;
    	}
    	public String getDefaultValue() {
    		return this.def;
    	}
    }

}

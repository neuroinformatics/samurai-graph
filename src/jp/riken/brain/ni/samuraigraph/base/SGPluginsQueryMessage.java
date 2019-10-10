package jp.riken.brain.ni.samuraigraph.base;

public class SGPluginsQueryMessage {
    
    public final static String MENUCMD_OUTPUT_TO_FILE_IS_ENABLED = "OutputToFileIsEnabled";
    
    public final static String MENUCMD_EXEC_OUTPUT_TO_FILE = "ExecOutputToFile";
    
    private final String mMessage;
    
    private Object mValue = null;
    
    public SGPluginsQueryMessage(final String message) {
        this.mMessage = message;
    }
    
    public String getMessage() {
        return this.mMessage;
    }
    
    public void set(Object value) {
        this.mValue = value;
    }
    
    public Object get() {
        return this.mValue;
    }

}

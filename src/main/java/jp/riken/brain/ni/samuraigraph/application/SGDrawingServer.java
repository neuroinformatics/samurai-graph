package jp.riken.brain.ni.samuraigraph.application;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;

/**
 * The main class of this application.
 */
public class SGDrawingServer implements SGIApplicationConstants,
        SGIApplicationTextConstants {

    // the name of look and feel
    private static String mLookAndFeel = null;

    /**
     * main thread
     */
    private static SGMainFunctions mAppMain = null;

    private static SGApplicationProperties mAppProp = null;

    /**
     * The main method.
     * 
     * @param args
     *            An array of arguments
     */
    public static void main(String args[]) {
        // interpret command lines
        Map<String, Object> paramMap = interpretCommands(args);

        // load dynamic constant values from property file
        mAppProp = new SGApplicationProperties();
        if (mAppProp.getStatus() == false) {
            System.exit(1); // fatal error
        }

        // set look and feel
        if (setLookAndFeel() == false) {
            System.exit(1); // fatal error
        }

        // create a Main thread
        SGMainFunctions m = new SGMainFunctions(mAppProp, paramMap);
        
        try {
            // wait till the end of Main thread
            m.waitInit();
            mAppMain = m;

            // register event handler for macos x
            MacOSXRegistration();
        } catch (Exception ex) {
            JOptionPane.showOptionDialog(null,
                    "Failed to start up Samurai Graph.", SGIConstants.TITLE_ERROR,
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, null, null);
        }
    }

    // interpret the command lines
    private static Map<String, Object> interpretCommands(String[] commands) {

    	Map<String, Object> paramMap = new HashMap<String, Object>();
    	
    	String propertyFileName = null;
    	String dataSetFileName = null;
    	String scriptFileName = null;
    	boolean commandFlag = false;
    	boolean devFlag = false;
    	boolean preprocessFlag = false;
    	List<String> dataFileNameList = new ArrayList<String>();
        for (int ii = 0; ii < commands.length; ii++) {
        	if (commands[ii].equals("-prop") || commands[ii].equals("-p")) {
                if (ii < commands.length - 1) {
                    propertyFileName = commands[ii + 1];
                    ii++;
                }
            } else if (commands[ii].equals("-dataset") || commands[ii].equals("-a")) {
                if (ii < commands.length - 1) {
                	dataSetFileName = commands[ii + 1];
                    ii++;
                }
            } else if (commands[ii].equals("-i")) {
            	commandFlag = true;
            } else if (commands[ii].equals("-s")) {
                if (ii < commands.length - 1) {
                	commandFlag = true;
            		scriptFileName = commands[ii + 1];
                    ii++;
                }
            } else if (commands[ii].equals("-dev")) {
            	devFlag = true;
            } else {
            	dataFileNameList.add(commands[ii]);
            }
        }
        
        // set values to the map
        String fileName = null;
        FILE_TYPE fileType = null;
        if (propertyFileName != null) {
        	fileName = propertyFileName;
        	fileType = FILE_TYPE.PROPERTY;
        }
        if (dataSetFileName != null) {
        	fileName = dataSetFileName;
        	fileType = FILE_TYPE.DATASET;
        }
        if (dataFileNameList.size() != 0) {
        	fileName = dataFileNameList.get(0);
        	fileType = FILE_TYPE.TXT_DATA;
        }
        if (scriptFileName != null) {
        	fileName = scriptFileName;
        	fileType = FILE_TYPE.SCRIPT;
        }
        if (fileName != null) {
            paramMap.put(SGMainFunctions.KEY_FILE_NAME, fileName);
            paramMap.put(SGMainFunctions.KEY_FILE_TYPE, fileType);
        }
        paramMap.put(SGMainFunctions.KEY_COMMAND_MODE_FLAG, Boolean.valueOf(commandFlag));
        paramMap.put(SGMainFunctions.KEY_DEVELOPER_MODE_FLAG, Boolean.valueOf(devFlag));

        return paramMap;
    }
    
    // set look and feel
    private static boolean setLookAndFeel() {
        try {
            if (mLookAndFeel == null) {
                // avoid NPE at non en_US locale for JRE 1.6
                UIManager.getInstalledLookAndFeels();
                UIManager.setLookAndFeel(UIManager
                        .getSystemLookAndFeelClassName());
            } else {
                UIManager.setLookAndFeel(mLookAndFeel);
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    private static void MacOSXRegistration() {
        boolean isMacOSX = (System.getProperty("os.name").toLowerCase()
                .startsWith("mac os x"));
        if (isMacOSX) {
            try {
                Class<?> osxAdapter = Class
                        .forName("jp.riken.brain.ni.samuraigraph.platform.macosx.SGMacOSXAdapter");
                Method registerMethod = osxAdapter.getMethod(
                        "registerMacOSXApplication", (Class[]) null);
                if (registerMethod != null) {
                    registerMethod.invoke(osxAdapter, (Object[]) null);
                }
            } catch (NoClassDefFoundError e) {
                // This will be thrown first if the OSXAdapter is loaded on a
                // system without the EAWT
                // because OSXAdapter extends ApplicationAdapter in its def
                System.err
                        .println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled ("
                                + e + ")");
            } catch (ClassNotFoundException e) {
                // This shouldn't be reached; if there's a problem with the
                // OSXAdapter we should get the
                // above NoClassDefFoundError first.
                System.err
                        .println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled ("
                                + e + ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * quit handler for application adapter
     * 
     */
    public static boolean quitHandler() {
        if (mAppMain == null) {
            return false;
        }
        mAppMain.exit();
        return true;
    }

    /**
     * about handler for application adapter
     * 
     */
    public static boolean aboutHandler() {
        if (mAppMain == null) {
            return false;
        }
        SGDrawingWindow wnd = mAppMain.getActiveWindow();
        if (wnd == null) {
            return false;
        }
        return mAppMain.showAboutDialog(wnd);
    }

    /**
     * openFile handler for application adapter
     * 
     */
    public static boolean openFileHandler(final String fname) {
        if (mAppMain == null) {
            return false;
        }
        SGDrawingWindow wnd = mAppMain.getActiveWindow();
        if (wnd == null) {
            return false;
        }
        return mAppMain.openFile(fname, wnd);
    }
}

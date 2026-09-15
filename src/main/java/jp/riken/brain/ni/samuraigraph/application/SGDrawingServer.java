package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/** The main class of this application. */
public class SGDrawingServer {

  private static final org.apache.logging.log4j.Logger logger =
      org.apache.logging.log4j.LogManager.getLogger(SGDrawingServer.class);

  /** The main method. */
  public static void main(String args[]) {
    // interpret command lines
    Map<String, Object> paramMap = interpretCommands(args);

    // load dynamic constant values from property file
    SGApplicationProperties appProp = new SGApplicationProperties();
    if (appProp.getStatus() == false) {
      System.exit(1); // fatal error
    }

    // set look and feel
    if (setLookAndFeel() == false) {
      System.exit(1); // fatal error
    }

    // create a Main thread
    SGMainFunctions m = new SGMainFunctions(appProp, paramMap);

    try {
      // wait till the end of Main thread
      m.waitInit();

      // register event handler for macos x
      MacOSXRegistration(m);
    } catch (Exception ex) {
      JOptionPane.showOptionDialog(
          null,
          "Failed to start up Samurai Graph.",
          TITLE_ERROR,
          JOptionPane.DEFAULT_OPTION,
          JOptionPane.WARNING_MESSAGE,
          null,
          null,
          null);
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
      // avoid NPE at non en_US locale for JRE 1.6
      UIManager.getInstalledLookAndFeels();
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex) {
      return false;
    }
    return true;
  }

  private static void MacOSXRegistration(final SGMainFunctions main) {
    boolean isMacOSX = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));
    if (isMacOSX) {
      try {
        SGApplicationAdapter.registerApplication(main);
      } catch (Exception e) {
        logger.warn("Error in drawing server", e);
      }
    }
  }
}

package jp.riken.brain.ni.samuraigraph.application;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5Exception;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer;
import com.jmatio.io.MatFileReader;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import jp.riken.brain.ni.samuraigraph.application.SGDataCreator.CreatedData;
import jp.riken.brain.ni.samuraigraph.application.SGDataCreator.CreatedDataSet;
import jp.riken.brain.ni.samuraigraph.base.SGAsyncWorker;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGDataExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDialog;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIProgressControl;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyFileConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIRootObjectConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGPlotTypeConstants;
import jp.riken.brain.ni.samuraigraph.base.SGPluginsQueryMessage;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUserProperties;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataDuplicationDialog;
import jp.riken.brain.ni.samuraigraph.data.SGDataTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGDataViewerDialog;
import jp.riken.brain.ni.samuraigraph.data.SGHDF5File;
import jp.riken.brain.ni.samuraigraph.data.SGIDataAnimation;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIMDArrayConstants;
import jp.riken.brain.ni.samuraigraph.data.SGINetCDFConstants;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYZTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGIVXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGMATLABFile;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataDuplicationDialog;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayPickUpDimensionInfo;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataDuplicationDialog;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFPickUpDimensionInfo;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataDuplicationDialog;
import jp.riken.brain.ni.samuraigraph.data.SGSXYMDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGVirtualMDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGVirtualMDArrayVariable;
import jp.riken.brain.ni.samuraigraph.data.SGXYSimpleDoubleValueIndexBlock;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroupSetInGraph;
import jp.riken.brain.ni.samuraigraph.figure.SGIElementGroupSetForData;
import jp.riken.brain.ni.samuraigraph.figure.SGIElementGroupSetMultipleSXY;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureTypeConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGILineStylePropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyleColorMapManager;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStylePropertyDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;

/** The main thread. */
class SGMainFunctions
    implements ActionListener,
        SGIUpgradeConstants,
        SGIApplicationCommandConstants,
        SGIApplicationConstants,
        SGIPropertyFileConstants,
        SGIPreferencesConstants,
        SGIApplicationTextConstants,
        SGIImageConstants,
        SGIArchiveFileConstants,
        SGIDataColumnTypeConstants,
        WindowListener,
        SGINetCDFConstants {

  private static final Logger logger = LogManager.getLogger(SGMainFunctions.class);

  // only used for debug
  static boolean USE_FOXTROT = true;

  /** Virtual bounds. */
  private static Rectangle virtualBounds = null;

  /** Application Properties */
  SGApplicationProperties mAppProp = null;

  /** Data set manager */
  SGDataSetManager mDataSetManager;

  /** Proxy manager. */
  SGProxyManager mProxyManager;

  /** Upgrade manager. */
  SGUpgradeManager mUpgradeManager;

  /** Data creator. */
  SGDataCreator mDataCreator;

  /** Window manager */
  SGWindowManager mWindowManager = null;

  /** Clip Board manager. */
  SGClipBoardManager mClipBoardManager = null;

  /** Command manager. */
  SGCommandManager mCommandManager = null;

  /** Property file manager. */
  SGPropertyFileManager mPropertyFileManager = null;

  /** Data file exporter. */
  SGDataFileExporter mDataFileExporter = null;

  /** Command script manager. */
  SGCommandScriptManager mCommandScriptManager = null;

  /** Figure creator. */
  SGFigureCreator mFigureCreator = null;

  private SGDataAdditionHandler mDataAdditionHandler = null;

  SGDataAdditionHandler getDataAdditionHandler() {
    if (this.mDataAdditionHandler == null) {
      this.mDataAdditionHandler = new SGDataAdditionHandler(this);
    }
    return this.mDataAdditionHandler;
  }

  private SGConsoleRunner mConsoleRunner = null;

  SGConsoleRunner getConsoleRunner() {
    if (this.mConsoleRunner == null) {
      this.mConsoleRunner = new SGConsoleRunner(this);
    }
    return this.mConsoleRunner;
  }

  private SGDataWizardTransition mWizardTransition = null;

  SGDataWizardTransition getWizardTransition() {
    if (this.mWizardTransition == null) {
      this.mWizardTransition = new SGDataWizardTransition(this);
    }
    return this.mWizardTransition;
  }

  private SGPropertyFileDataHandler mPropertyFileHandler = null;

  SGPropertyFileDataHandler getPropertyFileHandler() {
    if (this.mPropertyFileHandler == null) {
      this.mPropertyFileHandler =
          new SGPropertyFileDataHandler(this.mDataCreator, this.mFigureCreator);
    }
    return this.mPropertyFileHandler;
  }

  /** Plug-in manager. */
  SGPluginManager mPluginManager = null;

  /** Native plug-in manager. */
  SGNativePluginManager mNativePluginManager = null;

  /** Initializer. */
  private Initializer mInit = null;

  /** The standard output stream. */

  /** The standard input stream. */
  static final int DATA_ADDITION_TOOL_BAR = 0;

  static final int DATA_ADDITION_DRAG_AND_DROP = 1;

  static final int DATA_ADDITION_VIRTUAL = 2;

  static final String KEY_FILE_TYPE = "fileType";

  static final String KEY_FILE_NAME = "fileName";

  static final String KEY_COMMAND_MODE_FLAG = "commandMode";

  static final String KEY_DEVELOPER_MODE_FLAG = "developerMode";

  private static final String TEMP_DATA_FILE_DIR_NAME = "SamuraiGraphData";

  /** A file drag and dropped into a window. */
  DroppedDataFile mDroppedDataFile = null;

  /** A transformed data object. */
  private TransformedData mTransformedData = null;

  /** A virtual data of multidimensional array. */
  VirtualDataInfo mVirtualMDArrayData = null;

  /** The flag of command mode. */
  private boolean mCommandModeFlag = false;

  /** Create a thread object. */
  public SGMainFunctions(final SGApplicationProperties prop, final Map<String, Object> paramMap) {
    super();
    this.mAppProp = prop;
    this.mInit = new Initializer(paramMap);
    this.mInit.start();
  }

  /** SGMainFunctions :: Initializer class */
  private class Initializer extends Thread {

    private String mStartupFilePath = null;

    private FILE_TYPE mStartupFileType = null;

    private Initializer(final Map<String, Object> paramMap) {
      super();
      Set<String> keys = paramMap.keySet();

      // command mode
      if (keys.contains(KEY_COMMAND_MODE_FLAG)) {
        Boolean b = (Boolean) paramMap.get(KEY_COMMAND_MODE_FLAG);
        mCommandModeFlag = b.booleanValue();
      }

      // input file
      if (keys.contains(KEY_FILE_TYPE) && keys.contains(KEY_FILE_NAME)) {
        FILE_TYPE fileType = (FILE_TYPE) paramMap.get(KEY_FILE_TYPE);
        if (fileType != null) {
          String fileName = (String) paramMap.get(KEY_FILE_NAME);
          this.mStartupFilePath = fileName;
        }
        this.mStartupFileType = fileType;
      }

      // developer mode
      boolean devMode = false;
      if (keys.contains(KEY_DEVELOPER_MODE_FLAG)) {
        Boolean b = (Boolean) paramMap.get(KEY_DEVELOPER_MODE_FLAG);
        devMode = b.booleanValue();
      }

      // set user properties
      SGUserProperties p = SGUserProperties.getInstance();
      p.setProperty("dev", Boolean.toString(devMode));
    }

    private void removeHDF5TemporaryFiles() {
      File tempDir = getTemporaryDirectory();
      if (tempDir != null) {
        Pattern pattern1 = Pattern.compile("jhdf5\\d+\\.so");
        Pattern pattern2 = Pattern.compile("nativedata\\d+\\.so");
        File[] files = tempDir.listFiles();
        for (File f : files) {
          if (!f.isFile()) {
            continue;
          }
          String name = f.getName().toLowerCase();
          if (pattern1.matcher(name).matches() || pattern2.matcher(name).matches()) {
            f.delete();
          }
        }
      }
    }

    private void removeTemporaryDataFiles() {
      File tempDir = getTemporaryDirectory();
      if (tempDir != null) {
        File[] files = tempDir.listFiles();
        for (File f : files) {
          if (f.isFile()) {
            continue;
          }
          String name = f.getName();
          if (name.startsWith(TEMP_DATA_FILE_DIR_NAME)) {
            File[] subFiles = f.listFiles();
            for (File sf : subFiles) {
              sf.delete();
            }
            f.delete();
          }
        }
      }
    }

    public void run() {

      // create and show the splash window
      final SGSplashWindow sw = this.createSplashWindow();
      if (sw == null) {
        exitApplication(1);
        return;
      }
      sw.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      sw.setProgressValue(0.0f);
      sw.setVisible(true);

      // take the procedure below to get rid of warning messages as follows:
      // "log4j:WARN No appenders could be found for logger (ucar.nc2.NetcdfFile)."
      // "log4j:WARN Please initialize the log4j system properly."
      org.apache.logging.log4j.core.config.Configurator.initialize(
          new org.apache.logging.log4j.core.config.DefaultConfiguration());
      org.apache.logging.log4j.core.config.Configurator.setRootLevel(
          org.apache.logging.log4j.Level.OFF);

      // removes temporary files for created by JHDF5 library
      if (SGUtility.identifyOS(OS_NAME_WINDOWS)) {
        // only for Windows
        this.removeHDF5TemporaryFiles();
      }

      // remove temporary data file
      this.removeTemporaryDataFiles();

      // gets virtual bounds
      Rectangle[] virtualBoundsArray = SGApplicationUtility.getVirtualBoundsArray();

      // sets to the dialog
      Rectangle virtualBounds = SGApplicationUtility.getVirtualBounds(virtualBoundsArray);
      SGDialog.setVirtualBounds(virtualBounds);

      // sets virtual bounds to the main function
      Rectangle leftTopBounds = null;
      int rectX = Integer.MAX_VALUE;
      int rectY = Integer.MAX_VALUE;
      for (Rectangle rect : virtualBoundsArray) {
        final int x = rect.x;
        final int y = rect.y;
        if (x <= rectX && y <= rectY) {
          rectX = x;
          rectY = y;
          leftTopBounds = rect;
        }
      }
      SGMainFunctions.setVirtualBounds(leftTopBounds);

      // set up the properties of tooltip text
      ToolTipManager toolTipMan = ToolTipManager.sharedInstance();
      toolTipMan.setInitialDelay(0);
      toolTipMan.setDismissDelay(Integer.MAX_VALUE);

      // create instances in attributes
      SGMainFunctions.this.mDataCreator = new SGDataCreator();
      sw.setProgressValue(0.15f);
      SGMainFunctions.this.mPropertyFileManager = new SGPropertyFileManager(SGMainFunctions.this);
      SGMainFunctions.this.mDataSetManager =
          new SGDataSetManager(SGMainFunctions.this, sw, 0.15f, 0.50f);
      sw.setProgressValue(0.50f);
      SGMainFunctions.this.mProxyManager = new SGProxyManager();
      sw.setProgressValue(0.60f);
      SGMainFunctions.this.mUpgradeManager =
          new SGUpgradeManager(SGMainFunctions.this.mProxyManager, SGMainFunctions.this.mAppProp);
      SGMainFunctions.this.mWindowManager =
          new SGWindowManager(SGMainFunctions.this, sw, 0.60f, 0.80f);
      sw.setProgressValue(0.80f);
      SGMainFunctions.this.mCommandManager = new SGCommandManager(SGMainFunctions.this);
      SGMainFunctions.this.mClipBoardManager = new SGClipBoardManager(SGMainFunctions.this);
      SGMainFunctions.this.mDataFileExporter = new SGDataFileExporter();
      SGMainFunctions.this.mCommandScriptManager = new SGCommandScriptManager(SGMainFunctions.this);

      // create the figure element
      SGMainFunctions.this.mFigureCreator = new SGFigureCreator(true);
      if (!SGMainFunctions.this.mFigureCreator.validateClasses()) {
        exitApplication(1);
      }
      sw.setProgressValue(0.9f);

      // loads the plug-in files
      SGMainFunctions.this.mPluginManager = new SGPluginManager(SGMainFunctions.this);
      boolean result =
          SGMainFunctions.this.mPluginManager.loadPlugins(
              SGIApplicationConstants.APPLICATION_PLUGIN_DIRECTORY);
      if (result == false) {
        String filename = SGMainFunctions.this.mPluginManager.getFirstExceptionJarFilename();
        SGUtility.showErrorMessageDialog(
            null, "Plugins load error.\n" + filename, SGIConstants.TITLE_ERROR);
      }
      SGMainFunctions.this.mNativePluginManager = new SGNativePluginManager(SGMainFunctions.this);
      SGMainFunctions.this.mNativePluginManager.loadPlugins(APPLICATION_PLUGIN_DIRECTORY);

      sw.setProgressValue(1.0f);

      // creates and shows the window
      final SGDrawingWindow wnd = SGMainFunctions.this.mWindowManager.createNewWindow();
      SGMainFunctions.this.mWindowManager.setCurrentWindow(wnd);
      wnd.setVisible(true);

      // hide the splash window
      sw.setVisible(false);

      // sets the default cursor
      sw.setCursor(Cursor.getDefaultCursor());

      final FILE_TYPE type = this.mStartupFileType;
      final String path = this.mStartupFilePath;

      if (FILE_TYPE.PROPERTY.equals(type)) {
        final File f = new File(path);
        try {
          SwingUtilities.invokeAndWait(
              new Runnable() {
                public void run() {
                  SGMainFunctions.this.mPropertyFileManager.showMultiDataFileChooserDialog(f, wnd);
                }
              });
        } catch (InterruptedException e) {
          logger.debug("Exception occurred", e);
        } catch (InvocationTargetException e) {
          logger.debug("Exception occurred", e);
        }

      } else if (FILE_TYPE.DATASET.equals(type)) {
        // disable window
        wnd.setWaitCursor(true);

        final File f = new File(path);
        if (f.exists()) {
          try {
            SwingUtilities.invokeAndWait(
                new Runnable() {
                  public void run() {
                    if (SGMainFunctions.this.mDataSetManager.loadDataSetFromEventDispatchThread(
                            wnd, f)
                        == false) {
                      SGUtility.showErrorMessageDialog(
                          wnd, MSG_DATA_SET_FILE_INVALID, SGIConstants.TITLE_ERROR);
                    }
                  }
                });
          } catch (InterruptedException e) {
            logger.debug("Exception occurred", e);
          } catch (InvocationTargetException e) {
            logger.debug("Exception occurred", e);
          }
        } else {
          // file not found
          SGUtility.showErrorMessageDialog(wnd, MSG_FILE_OPEN_FAILURE, TITLE_FILE_OPEN_FAILURE);
        }

        // enable window
        wnd.setWaitCursor(false);
      } else if (FILE_TYPE.TXT_DATA.equals(type)
          || FILE_TYPE.NETCDF_DATA.equals(type)
          || FILE_TYPE.HDF5_DATA.equals(type)
          || FILE_TYPE.MATLAB_DATA.equals(type)) {

        File dataFile = new File(path);
        final ArrayList<File> dataFileList = new ArrayList<File>();
        dataFileList.add(dataFile);
        try {
          SwingUtilities.invokeAndWait(
              new Runnable() {
                public void run() {
                  FILE_TYPE type = SGApplicationUtility.identifyDataFileType(path);
                  if (FILE_TYPE.HDF5_DATA.equals(type)) {
                    onHDF5DataFilesDropped(dataFileList, wnd, null);
                  } else if (FILE_TYPE.MATLAB_DATA.equals(type)) {
                    onMATLABDataFilesDropped(dataFileList, wnd, null);
                  } else if (FILE_TYPE.NETCDF_DATA.equals(type)) {
                    // add netCDF data
                    onNetCDFDataFilesDropped(dataFileList, wnd, null);
                  } else {
                    // add text data
                    onTextDataFilesDropped(dataFileList, wnd, null);
                  }
                }
              });
        } catch (InterruptedException e) {
          logger.debug("Exception occurred", e);
        } catch (InvocationTargetException e) {
          logger.debug("Exception occurred", e);
        }

      } else if (this.mStartupFileType == FILE_TYPE.SCRIPT) {
        // setup standard output stream
        SGMainFunctions.this
            .getConsoleRunner()
            .setOutputStream(new BufferedWriter(new OutputStreamWriter(System.out)));

        // load the command script file
        SGMainFunctions.this.getConsoleRunner().loadCommandScriptFile(path);

        // set up standard input stream
        SGMainFunctions.this
            .getConsoleRunner()
            .setInputStream(new BufferedReader(new InputStreamReader(System.in)));
        try {
          // read input recursively
          SGMainFunctions.this.getConsoleRunner().startReadingInput();
        } catch (IOException ex) {
          return;
        }
      }

      // initializes the preferences
      this.initPreferences();

      // decide whether to upgrade
      SGMainFunctions.this.mUpgradeManager.upgradeWithCheckDate(wnd);

      if (mCommandModeFlag && !FILE_TYPE.SCRIPT.equals(this.mStartupFileType)) {
        // setup standard output stream
        SGMainFunctions.this
            .getConsoleRunner()
            .setOutputStream(new BufferedWriter(new OutputStreamWriter(System.out)));

        // setup standard input stream
        SGMainFunctions.this
            .getConsoleRunner()
            .setInputStream(new BufferedReader(new InputStreamReader(System.in)));
        try {
          // read input recursively
          SGMainFunctions.this.getConsoleRunner().startReadingInput();
        } catch (IOException ex) {
          return;
        }
      }
    }

    /**
     * create splash window
     *
     * @return
     */
    private SGSplashWindow createSplashWindow() {
      SGSplashWindow sw =
          new SGSplashWindow("Splash.png", SGMainFunctions.this.mAppProp.getVersionString());
      return sw;
    }

    private void initPreferences() {
      Preferences pref = Preferences.userNodeForPackage(this.getClass());

      // set the upgrade cycle if not exist
      final int cycle = pref.getInt(PREF_KEY_UPGRADE_CYCLE, -1);
      if (cycle == -1) {
        final int upgradeCycle = mAppProp.getUpgradeCycle();
        pref.putInt(PREF_KEY_UPGRADE_CYCLE, upgradeCycle);
      }

      // set the current date if not exist
      final long date = pref.getLong(PREF_KEY_DATE, 0L);
      if (date == 0L) {
        pref.putLong(PREF_KEY_DATE, System.currentTimeMillis());
      }

      // remove old keys
      pref.remove(PREF_KEY_MAJOR_VERSION_NUMBER);
      pref.remove(PREF_KEY_MINOR_VERSION_NUMBER);
      pref.remove(PREF_KEY_MICRO_VERSION_NUMBER);
    }
  }

  public boolean waitInit() {
    try {
      this.mInit.join();
    } catch (InterruptedException e) {
      return false;
    }
    this.mInit = null;
    return true;
  }

  /**
   * Closes the window without confirmation.
   *
   * @param id the window ID of a window to close
   * @param true if succeeded
   */
  public boolean closeWindowWithoutConfirmation(final int id) {
    return this.mWindowManager.closeWindowWithoutConfirmation(id);
  }

  /**
   * Execute a command.
   *
   * @param line the command line
   * @return the status
   */
  public int exec(final String line) {
    return this.mCommandManager.exec(line);
  }

  // Checks whether a modal dialog is shown.
  boolean isDialogOpen() {
    List<SGDrawingWindow> wndList = this.mWindowManager.getWindowList();
    for (SGDrawingWindow wnd : wndList) {
      if (wnd.isModalDialogShown()) {
        return true;
      }
    }
    return false;
  }

  // Checks whether a text field is shown.
  boolean closeTextField() {
    List<SGDrawingWindow> wndList = this.mWindowManager.getWindowList();
    for (SGDrawingWindow wnd : wndList) {
      if (wnd.closeTextField() == false) {
        return false;
      }
    }
    return true;
  }

  /** A wizard dialog to select the figure ID. */
  SGFigureIDSelectionWizardDialog mFigureIDSelectionWizardDialog = null;

  /** A wizard dialog to select a single data file. */
  SGSingleDataFileChooserWizardDialog mSingleDataFileChooserWizardDialog = null;

  /** A wizard dialog to select the file type. */
  SGFileTypeSelectionWizardDialog mFileTypeSelectionWizardDialog = null;

  /** A wizard dialog to select the data type. */
  SGDataTypeWizardDialog mDataTypeWizardDialog = null;

  /** A wizard dialog to setup single-dimensional array data. */
  SGSDArrayDataSetupWizardDialog mSDArrayDataSetupWizardDialog = null;

  /** A wizard dialog to setup NetCDF data. */
  SGNetCDFDataSetupWizardDialog mNetCDFDataSetupWizardDialog = null;

  /** A wizard dialog to setup multidimensional array data. */
  SGMDArrayDataSetupWizardDialog mMDArrayDataSetupWizardDialog = null;

  /** A wizard dialog to select the plot type if the data type is scalar-xy. */
  SGPlotTypeSelectionWizardDialog mPlotTypeSelectionWizardDialog = null;

  /** Map for the current file name. */
  private Map<FILE_TYPE, String> mCurrentFileNameMap = new HashMap<FILE_TYPE, String>();

  /**
   * Returns the current file name of given file type.
   *
   * @param fType file type
   * @return the current file name
   */
  String getCurrentFileName(FILE_TYPE fType) {
    return this.mCurrentFileNameMap.get(fType);
  }

  /**
   * Updates the current file.
   *
   * @param f a file
   * @param type the file type
   */
  void updateCurrentFile(File f, FILE_TYPE type) {
    String parent = f.getParent();
    if (parent != null) {
      this.setCurrentFileDirectory(parent);
      String path = f.getPath();
      String name = path.substring(parent.length() + 1);
      this.mCurrentFileNameMap.put(type, name);
    }
  }

  /** Returns the current file directory. */
  String getCurrentFileDirectory() {
    Preferences pref = Preferences.userNodeForPackage(this.getClass());
    String currentDir = pref.get(PREF_KEY_CURRENT_DIRECTORY, null);

    boolean curDirFlag = true;
    if (currentDir == null) {
      curDirFlag = false;
    } else {
      File f = new File(currentDir);
      curDirFlag = f.exists();
    }

    if (!curDirFlag) {
      StringBuilder sb = new StringBuilder();
      sb.append(USER_HOME);
      if (SGUtility.identifyOS(OS_NAME_WINDOWS)) {
        sb.append(FILE_SEPARATOR);
        sb.append(MY_DOCUMENTS);
      }
      String path = sb.toString();
      this.setCurrentFileDirectory(path);
      currentDir = path;
    }

    return currentDir;
  }

  /**
   * Sets the current file directory.
   *
   * @param path path of the current file directory
   */
  void setCurrentFileDirectory(final String path) {
    Preferences pref = Preferences.userNodeForPackage(this.getClass());
    pref.put(PREF_KEY_CURRENT_DIRECTORY, path);
  }

  /**
   * Show the about dialog.
   *
   * @param wnd
   */
  public boolean showAboutDialog(SGDrawingWindow wnd) {
    if (wnd == null) {
      return false;
    }

    SGAboutDialog dg = new SGAboutDialog(wnd, true, this.mAppProp.getVersionString());

    final int width = dg.getWidth();
    final int height = dg.getHeight();

    final int x = wnd.getX() + wnd.getWidth() / 2 - width / 2;
    final int y = wnd.getY() + wnd.getHeight() / 2 - height / 2;

    dg.setLocation(x, y);

    // show a modal dialog
    dg.setVisible(true);

    // dispose
    dg.dispose();
    return true;
  }

  /**
   * Duplicate the focused data object.
   *
   * @return true if succeeded
   */
  boolean duplicateFocusedData(SGDrawingWindow wnd) {
    List<SGFigure> fList = wnd.getVisibleFigureList();
    for (int ii = 0; ii < fList.size(); ii++) {
      SGFigure f = fList.get(ii);

      // get figure elements
      SGIFigureElement[] elArray = f.getIFigureElementArray();
      SGIFigureElementGraph gElement = f.getGraphElement();

      // get focused data objects
      List<SGData> dataList = gElement.getFocusedDataList();

      List<SGData> dListOriginal = new ArrayList<SGData>();
      List<SGData> dListDuplicated = new ArrayList<SGData>();
      for (int jj = 0; jj < dataList.size(); jj++) {
        SGData data = dataList.get(jj);

        // get properties of data before duplication
        //                String dataType = data.getDataType();
        String name = gElement.getDataName(data);
        SGDataColumnInfo[] cols = gElement.getDataColumnInfoArray(data);
        Map<String, Object> infoMap = gElement.getInfoMap(data);
        infoMap.put(
            SGIDataInformationKeyConstants.KEY_FIGURE_SIZE,
            new SGTuple2f(f.getFigureWidth(), f.getFigureHeight()));
        SGDataColumnInfoSet colInfoSet = new SGDataColumnInfoSet(cols);

        // create a new name
        String nameNew = f.getNewDataName(name);

        // create a dialog and set data
        SGDataDuplicationDialog dg = null;
        if (SGDataDataTypeUtility.isSDArrayData(data)) {
          SGSDArrayData sData = (SGSDArrayData) data;
          SGSDArrayDataDuplicationDialog adg = new SGSDArrayDataDuplicationDialog(wnd, true);
          if (adg.setData(nameNew, sData, colInfoSet, infoMap) == false) {
            return false;
          }
          dg = adg;
        } else if (SGDataDataTypeUtility.isNetCDFData(data)) {
          SGNetCDFData nData = (SGNetCDFData) data;
          SGNetCDFDataDuplicationDialog ndg = new SGNetCDFDataDuplicationDialog(wnd, true);
          if (ndg.setData(nameNew, nData, colInfoSet, infoMap) == false) {
            return false;
          }
          dg = ndg;
        } else if (SGDataDataTypeUtility.isMDArrayData(data)) {
          SGMDArrayData mdData = (SGMDArrayData) data;
          SGMDArrayDataDuplicationDialog mdg = new SGMDArrayDataDuplicationDialog(wnd, true);
          if (mdg.setData(nameNew, mdData, colInfoSet, infoMap) == false) {
            return false;
          }
          dg = mdg;
        } else {
          throw new Error("Invalid data type: " + data.getDataType());
        }

        // show the dialog
        dg.setLocation(wnd.getX() + 20, wnd.getY() + 20);
        dg.setVisible(true);

        // after the dialog is closed
        SGData dCopy = null;
        if (dg.getCloseOption() == SGDialog.OK_OPTION) {
          // if OK button is pressed

          // get the results
          SGDataColumnInfo[] result = dg.getDataColumnTypes();

          dCopy = (SGData) data.copy();
          if (SGDataDataTypeUtility.isArrayData(dCopy)) {
            SGArrayData adCopy = (SGArrayData) dCopy;

            // copy the data and set columns
            if (adCopy.setColumnInfo(result) == false) {
              return false;
            }

            // sets the stride of data arrays
            adCopy.setStrideMap(dg.getStrideMap());
            adCopy.setStrideAvailable(dg.isStrideAvailable());

            if (data instanceof SGSXYNetCDFMultipleData) {
              SGSXYNetCDFMultipleData ncData = (SGSXYNetCDFMultipleData) dCopy;
              SGNetCDFDataDuplicationDialog ndg = (SGNetCDFDataDuplicationDialog) dg;
              // sets pick up information
              String pickUpName = ndg.getSXYPickUpDimensionName();
              SGIntegerSeriesSet pickUpIndices = ndg.getSXYPickUpIndices();
              if (pickUpName != null && pickUpIndices != null) {
                SGNetCDFPickUpDimensionInfo info =
                    new SGNetCDFPickUpDimensionInfo(pickUpName, pickUpIndices);
                ncData.setPickUpDimensionInfo(info);
              }
            } else if (data instanceof SGSXYMDArrayMultipleData) {
              SGSXYMDArrayMultipleData mdData = (SGSXYMDArrayMultipleData) dCopy;
              SGMDArrayDataDuplicationDialog mdg = (SGMDArrayDataDuplicationDialog) dg;
              // sets pick up information
              List<String> pickUpNames = mdg.getSXYDataPickUpDatasetName();
              Map<String, Integer> dimMap = mdg.getSXYDataPickUpDimensionIndexMap();
              Map<String, Integer> pickUpDimMap = new HashMap<String, Integer>();
              for (String pickUpName : pickUpNames) {
                Integer pickUpDim = dimMap.get(pickUpName);
                pickUpDimMap.put(pickUpName, pickUpDim);
              }
              SGIntegerSeriesSet pickUpIndices = mdg.getSXYPickUpIndices();
              if (pickUpNames != null && pickUpIndices != null) {
                SGMDArrayPickUpDimensionInfo info =
                    new SGMDArrayPickUpDimensionInfo(pickUpDimMap, pickUpIndices);
                mdData.setPickUpDimensionInfo(info);
              }
            }
          }

        } else {
          continue;
        }

        // get the input name
        String nameInput = dg.getDataName();

        // get data properties
        SGProperties dp = dCopy.getProperties();

        Map<Class<? extends SGIFigureElement>, SGProperties> propertiesMap =
            new HashMap<Class<? extends SGIFigureElement>, SGProperties>();
        for (int kk = 0; kk < elArray.length; kk++) {
          // get properties of the original data from figure elements
          SGProperties p = elArray[kk].getDataProperties(data);

          // synchronize the properties for duplicated data object
          SGProperties sp = elArray[kk].synchronizeDataProperties(p, dp);
          if (sp == null) {
            continue;
          }

          // put into the map
          propertiesMap.put(elArray[kk].getClass(), sp);
        }

        // add data to the figure
        if (f.addData(dCopy, nameInput, propertiesMap) == false) {
          return false;
        }

        dListOriginal.add(data);
        dListDuplicated.add(dCopy);
      }

      for (SGData data : dListOriginal) {
        // set unfocused the selected data
        gElement.setDataFocused(data, false);
      }
      for (SGData data : dListDuplicated) {
        // set focused the new data
        gElement.setDataFocused(data, true);
      }
    }
    return true;
  }

  /**
   * @return
   */
  boolean duplicateFocusedFigures(SGDrawingWindow wnd) {

    DOMImplementation domImpl = SGApplicationUtility.getDOMImplementation();
    if (domImpl == null) {
      return false;
    }

    // create a Document object
    Document document = domImpl.createDocument("", TAG_NAME_FOCUSED_FIGURES, null);

    // get the root element
    Element property = document.getDocumentElement();
    property.setAttribute(KEY_VERSION_NUMBER, this.mAppProp.getVersionString());

    // create a DOM tree
    if (wnd.createDOMTree(
            document,
            SGIRootObjectConstants.FOCUSED_FIGURES_FOR_DUPLICATION,
            new SGExportParameter(OPERATION.DUPLICATE_OBJECT))
        == false) {
      return false;
    }

    // create an array of wrapped data objects
    List<WrappedData> wdList = new ArrayList<WrappedData>();
    List<SGFigure> fList = wnd.getFocusedFigureList();
    for (int ii = 0; ii < fList.size(); ii++) {
      SGFigure figure = fList.get(ii);
      List<SGData> dList = new ArrayList<SGData>(figure.getVisibleDataList());
      for (int jj = 0; jj < dList.size(); jj++) {
        SGData data = dList.get(jj);
        FigureData fd = new FigureData(data, ii);
        WrappedData wd = new WrappedData(fd);
        wdList.add(wd);
      }
    }
    WrappedData[] wDataArray = new WrappedData[wdList.size()];
    wdList.toArray(wDataArray);

    // get root element - property
    Element root = document.getDocumentElement();

    // get version number
    String versionNumber = root.getAttribute(KEY_VERSION_NUMBER);

    // get the node of window
    NodeList wList = root.getElementsByTagName(SGIRootObjectConstants.TAG_NAME_WINDOW);
    if (wList.getLength() == 0) {
      return false;
    }
    Element elWnd = (Element) wList.item(0);

    int before = wnd.getFigureList().size();

    final int ret =
        this.getPropertyFileHandler()
            .createFiguresFromPropertyFile(
                elWnd, wnd, wDataArray, true, versionNumber, LOAD_PROPERTIES_IN_DUPLICATION);
    if (ret != SGIConstants.SUCCESSFUL_COMPLETION) {
      return false;
    }

    int after = wnd.getFigureList().size();

    wnd.setChanged(before != after);

    return true;
  }

  // call before discarded a window
  int beforeDiscard(final SGDrawingWindow wnd) {
    final int ret = this.confirmBeforeDiscard(wnd);
    if (ret == JOptionPane.YES_OPTION) {

    } else if (ret == JOptionPane.NO_OPTION || ret == JOptionPane.CLOSED_OPTION) {
      // canceled and there is nothing to do
      return CANCEL_OPTION;
    } else if (ret == JOptionPane.CANCEL_OPTION) {
      // save the properties
      final int retSave = this.mPropertyFileManager.savePropertiesByDialog(wnd);
      return retSave;
    }

    return OK_OPTION;
  }

  static final String MSG_SAVE = "Save";

  // show the confirmation dialog for saving properties of the window
  int confirmBeforeClosing(final SGDrawingWindow wnd) {
    final Object[] options = {MSG_CLOSE_WITHOUT_SAVING, SGDialog.CANCEL_BUTTON_TEXT, MSG_SAVE};
    return this.showConfirmationDialog(wnd, options, new SGCloseWindowConfirmPanel());
  }

  // show the confirmation dialog for saving properties of the window
  int confirmBeforeDiscard(final SGDrawingWindow wnd) {
    final Object[] options = {
      // MSG_DISCARD_WITHOUT_SAVING,
      MSG_CLOSE_WITHOUT_SAVING, SGDialog.CANCEL_BUTTON_TEXT, MSG_SAVE
    };
    return this.showConfirmationDialog(wnd, options, new SGCloseWindowConfirmPanel());
  }

  /** Shows the confirmation dialog. */
  private int showConfirmationDialog(Component component, Object[] options, Object message) {
    // beep
    Toolkit.getDefaultToolkit().beep();

    // show a dialog
    final int ret =
        JOptionPane.showOptionDialog(
            component,
            message,
            SGIConstants.TITLE_CONFIRMATION,
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[2]);

    return ret;
  }

  /**
   * @return
   */
  boolean showChangeLogDialog(Frame owner) {

    // create and set a dialog object
    SGChangeLogDialog dg = new SGChangeLogDialog(owner, true);
    dg.addActionListener(this);
    dg.setCenter(owner);

    // set a message
    String msg = "Change Log of Samurai Graph.";
    dg.setMessage(msg);

    // get the file of change log
    Class<?> inClass = getClass();
    URL url = inClass.getResource(RESOURCES_DIRNAME + "ChangeLog.html");
    if (url == null) {
      JOptionPane.showMessageDialog(owner, "Failed to get log information.");
      return false;
    }

    // set the html file
    dg.setPage(url);
    dg.pack();

    // show
    dg.setVisible(true);

    // dispose
    dg.dispose();

    return true;
  }

  static class DataSourceInfo {
    String path = null;
    SGIDataSource src = null;

    DataSourceInfo(String path) {
      super();
      this.path = path;
    }

    DataSourceInfo(SGIDataSource src) {
      super();
      this.src = src;
    }
  }

  /** error message that it is failed to get the data type. */
  public static final String ERRMSG_TO_GET_DATA_TYPE = "Failed to get the data type.";

  public static final String ERRMSG_TO_DRAW_GRAPH = "Failed to draw the graph.";

  public static final String ERRMSG_TO_LOAD_DATASET = "Failed to load dataset file.";

  private static final String ERRMSG_SCRIPT_START =
      "To use Samurai Graph script, the application must be started in the command mode.";

  static final String ERRMSG_DATA_ADDITION =
      "Failed to add data.\n" + "Valid data was not obtained with input values.";

  static final String ERRMSG_URL_OF_NETCDF = "Failed to add NetCDF file from input URL.";

  // Sets the default value of dimension origin and step.

  // Sets the default value of dimension origin and step.

  private SGStatus drawGraphSub(
      final SGDrawingWindow wnd,
      final int figureID,
      final SGDataColumnInfoSet colInfoSet,
      final Map<String, Object> infoMap,
      final DataSourceInfo dataSource,
      final Integer[] dataIdArray,
      final boolean showDialog,
      final Point figureLocation) {

    if (wnd == null || infoMap == null || dataSource == null) {
      return new SGStatus(false);
    }

    if (infoMap.size() == 0) {
      return new SGStatus(false);
    }

    // get or create data name base
    //        String dataNameBase = null;
    //        Object obj = infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_NAME);
    //        if (obj != null) {
    //            dataNameBase = (String) obj;
    //        } else {
    //            dataNameBase = SGUtility.createDataNameBase(pathName);
    //            if (dataNameBase == null) {
    //                return new SGStatus(false);
    //            }
    //        }
    String dataNameBase = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_NAME);

    final SGFigure fig = wnd.getFigure(figureID);

    // create a data object
    SGDataCreator.CreatedDataSet cdSet = null;
    try {
      cdSet = this.mDataCreator.create(dataSource, colInfoSet, infoMap, wnd, null, -1);
    } catch (FileNotFoundException ex) {
      if (showDialog) {
        SGUtility.showFileNotFoundMessageDialog(wnd);
      }
      return new SGStatus(false);
    }
    if (cdSet == null) {
      return new SGStatus(false);
    }

    try {
      if (this.addDataToDrawGraph(
              wnd,
              figureID,
              dataIdArray,
              showDialog,
              infoMap,
              figureLocation,
              fig,
              cdSet,
              dataNameBase,
              false)
          == false) {
        return new SGStatus(false);
      }
    } catch (Exception e) {
      if (e.getClass().getName().endsWith("HDF5DatatypeInterfaceException")) {
        // Failed to read the HDF5 data.
        wnd.endProgress();
        return new SGStatus(false, MSG_HDF5_VALUES_OUT_OF_RANGE);
      }
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
    }

    return new SGStatus(true);
  }

  /**
   * @param wnd
   * @param figureID
   * @param dataIdArray
   * @param showDialog
   * @param infoMap
   * @param figureLocation
   * @param fig
   * @param cdSet
   * @param dataNameBase
   * @param fitAxisToFocused
   * @return true if succeeds
   */
  boolean addDataToDrawGraph(
      final SGDrawingWindow wnd,
      final int figureID,
      final Integer[] dataIdArray,
      final boolean showDialog,
      final Map<String, Object> infoMap,
      final Point figureLocation,
      final SGFigure fig,
      final SGDataCreator.CreatedDataSet cdSet,
      final String dataNameBase,
      final boolean fitAxisToFocused) {

    final int len = cdSet.getDataLength();
    if (len == 0) {
      if (showDialog) {
        SGApplicationUtility.showDataFileInvalidMessageDialog(wnd);
      }
      return Boolean.FALSE;
    }
    String[] nameArray = new String[len];
    SGData[] dataArray = new SGData[len];
    for (int ii = 0; ii < len; ii++) {
      SGDataCreator.CreatedData cd = cdSet.getData(ii);
      dataArray[ii] = cd.getData();
      StringBuilder sb = new StringBuilder();
      sb.append(dataNameBase);
      String title = cd.getTitle();
      if (title != null) {
        sb.append(cdSet.getMiddleString());
        sb.append(title);
      }
      nameArray[ii] = sb.toString();
    }

    // add data
    if (this.addData(
            wnd,
            figureID,
            dataIdArray,
            showDialog,
            figureLocation,
            fig,
            dataArray,
            nameArray,
            infoMap)
        == false) {
      return Boolean.FALSE;
    }

    if (fitAxisToFocused) {
      // fit axes
      SGIFigureElementGraph gElement = fig.getGraphElement();
      for (int ii = 0; ii < dataArray.length; ii++) {
        gElement.setDataFocused(dataArray[ii], true);
      }
      fig.fitAxisRangeToFocusedData(false);
    }

    return Boolean.TRUE;
  }

  private boolean addData(
      final SGDrawingWindow wnd,
      final int figureID,
      final Integer[] dataIdArray,
      final boolean showDialog,
      final Point figureLocation,
      SGFigure fig,
      SGData[] dataArray,
      String[] nameArray,
      final Map<String, Object> infoMap) {

    boolean createFigureFlag = (fig == null);
    if (fig != null) {
      if (fig.isVisible() == false) {
        createFigureFlag = true;
      }
    }

    if (createFigureFlag) {
      // create new figure and add data.
      if (!SGMainFunctions.this.mFigureCreator.createNewFigure(
          wnd, figureID, figureLocation, dataIdArray, dataArray, nameArray, infoMap)) {
        return Boolean.FALSE;
      }
      Object obj = infoMap.get(SGPlotTypeConstants.KEY_PLOT_TYPE_BAR);
      if (obj instanceof Boolean) {
        if (((Boolean) obj).booleanValue()) {
          SGFigure figure = wnd.getFigure(figureID);
          figure.fitAxisRangeToVisibleData(false);
        }
      }
    } else {
      if (fig == null) {
        return false;
      }
      // add data to figure already exists.
      SGIProgressControl progress = (SGIProgressControl) wnd;
      progress.setProgressMessage("Add Data");
      progress.startProgress();
      if (!fig.addData(dataArray, dataIdArray, nameArray, progress, 0.0f, 1.0f, infoMap)) {
        progress.endProgress();
        if (showDialog) {
          SGApplicationUtility.showDataFileInvalidMessageDialog(wnd);
        }
      }
      if (fig.getClassType() == null || fig.getClassType().equals("")) {
        String type = "";
        SGData dataFirst = dataArray[0];
        if (dataFirst instanceof SGISXYTypeSingleData
            || dataFirst instanceof SGISXYTypeMultipleData) {
          type = SGIFigureTypeConstants.FIGURE_TYPE_SXY;
        } else if (dataFirst instanceof SGIVXYTypeData) {
          type = SGIFigureTypeConstants.FIGURE_TYPE_VXY;
        } else if (dataFirst instanceof SGISXYZTypeData) {
          type = SGIFigureTypeConstants.FIGURE_TYPE_SXY;
        }
        fig.setClassType(type);
      }
      progress.endProgress();
    }

    return true;
  }

  /**
   * Draw a new graph.
   *
   * @param wnd a window that the graph belongs
   * @param figureID the ID of figure to draw the graph
   * @param colInfo an array of data column information
   * @param infoMap a map for data information
   * @param pathName path name of data
   * @param dataId the ID of data object to add
   * @param showDialog true if error and warning dialog is shown
   * @param figureLocation The location of new figure if it is created. If data is added to a figure
   *     that already exists, this value is neglected.
   * @return true if succeeded
   */
  SGStatus drawGraph(
      final SGDrawingWindow wnd,
      final int figureID,
      final SGDataColumnInfoSet colInfoSet,
      final Map<String, Object> infoMap,
      final DataSourceInfo dataSource,
      final Integer[] dataIdArray,
      final boolean showDialog,
      final Point figureLocation) {
    SGStatus result = null;
    wnd.setWaitCursor(true);

    if (!USE_FOXTROT) {
      result =
          drawGraphSub(
              wnd,
              figureID,
              colInfoSet,
              infoMap,
              dataSource,
              dataIdArray,
              showDialog,
              figureLocation);
    } else {
      try {
        result =
            SGAsyncWorker.post(
                () ->
                    drawGraphSub(
                        wnd,
                        figureID,
                        colInfoSet,
                        infoMap,
                        dataSource,
                        dataIdArray,
                        showDialog,
                        figureLocation));
      } catch (Exception ex) {
        result = new SGStatus(false);
        logger.warn("Error in main function operation", ex);
      }
    }

    wnd.setWaitCursor(false);
    return result;
  }

  private List<double[][]> createDoubleValueBlockList(
      List<SGXYSimpleDoubleValueIndexBlock> blockList) {
    List<double[][]> doubleArrayList = new ArrayList<double[][]>();
    for (SGXYSimpleDoubleValueIndexBlock block : blockList) {
      double[] blockValues = block.getValues();
      final int colNum = block.getXSeries().getLength();
      final int rowNum = block.getYSeries().getLength();
      double[][] twoDimArray = new double[colNum][rowNum];
      for (int cc = 0; cc < colNum; cc++) {
        for (int rr = 0; rr < rowNum; rr++) {
          final int index = cc * rowNum + rr;
          twoDimArray[cc][rr] = blockValues[index];
        }
      }
      doubleArrayList.add(twoDimArray);
    }
    return doubleArrayList;
  }

  /** Invoked when an action event is thrown. */
  public void actionPerformed(final ActionEvent e) {
    Object source = e.getSource();
    String command = e.getActionCommand();
    if (this.getDataAdditionHandler().isDataAdditionDialog(source)) {
      if (this.mDroppedDataFile != null) {
        // drag and drop
        this.getDataAdditionHandler().makeTransitionForDragAndDrop(e);
      } else if (this.mTransformedData != null) {
        // transformation
        this.makeTransitionForDataTransformation(e);
      } else if (this.mVirtualMDArrayData != null) {
        // plug-in
        this.getDataAdditionHandler().makeTransitionForPlugin(e);
      } else {
        // tool bar
        this.getDataAdditionHandler().makeTransitionForToolBar(e);
      }
    } else if (source instanceof SGDataViewerDialog) {

      SGDataViewerDialog dgSrc = (SGDataViewerDialog) source;
      SGDrawingWindow wnd = dgSrc.getOwnerWindow();

      if (SGDataViewerDialog.DATA_EDITED.equals(command)) {
        SGData data = dgSrc.getData();
        List<SGFigure> figureList = wnd.getVisibleFigureList();
        for (SGFigure f : figureList) {
          SGElementGroupSetInGraph gs =
              (SGElementGroupSetInGraph) f.getGraphElement().getChild(data);
          if (gs != null) {
            gs.dataEdited(dgSrc);
            break;
          }
        }
        wnd.repaint();
        return;
      } else if (SGDataViewerDialog.CLOSE_BUTTON_TEXT.equals(command)) {
        // do nothing
        return;
      }

      // determine variable names
      List<String> dataNameList = new ArrayList<String>();
      for (int ii = 0; ii < this.mDataViewerDialogArray.length; ii++) {
        SGDataViewerDialog dg = this.mDataViewerDialogArray[ii];
        String dataName = dg.getDataName();
        String unescapedDataName = SGUtility.removeEscapeChar(dataName);
        char[] cArray = unescapedDataName.toCharArray();
        List<Character> cList = new ArrayList<Character>();
        for (char c : cArray) {
          final char uc = Character.toUpperCase(c);
          if (('A' <= uc && uc <= 'Z') || ('0' <= c && c <= '9')) {
            cList.add(c);
          } else {
            cList.add('_');
          }
        }
        char[] cArrayNew = new char[cList.size()];
        for (int jj = 0; jj < cArrayNew.length; jj++) {
          cArrayNew[jj] = cList.get(jj);
        }
        String dataNameNew = new String(cArrayNew);
        dataNameList.add(dataNameNew);
      }
      Map<String, Set<Integer>> overlappingIndexSetMap = new HashMap<String, Set<Integer>>();
      for (int ii = 0; ii < dataNameList.size() - 1; ii++) {
        String dataName0 = dataNameList.get(ii);
        for (int jj = ii + 1; jj < dataNameList.size(); jj++) {
          String dataName1 = dataNameList.get(jj);
          if (dataName1.equals(dataName0)) {
            Set<Integer> indexSet = overlappingIndexSetMap.get(dataName0);
            if (indexSet == null) {
              indexSet = new HashSet<Integer>();
              overlappingIndexSetMap.put(dataName0, indexSet);
            }
            indexSet.add(ii);
            indexSet.add(jj);
          }
        }
      }
      Iterator<Entry<String, Set<Integer>>> indexSetItr =
          overlappingIndexSetMap.entrySet().iterator();
      while (indexSetItr.hasNext()) {
        Entry<String, Set<Integer>> entry = indexSetItr.next();
        String name = entry.getKey();
        Iterator<Integer> indexItr = entry.getValue().iterator();
        int cnt = 0;
        while (indexItr.hasNext()) {
          final int index = indexItr.next();
          String nameNew = name + "_" + cnt;
          dataNameList.set(index, nameNew);
          cnt++;
        }
      }
      String[] dataNameArray = dataNameList.toArray(new String[dataNameList.size()]);

      // create variables
      List<SGVirtualMDArrayVariable> varList = new ArrayList<SGVirtualMDArrayVariable>();
      for (int ii = 0; ii < this.mDataViewerDialogArray.length; ii++) {
        SGDataViewerDialog dg = this.mDataViewerDialogArray[ii];
        List<SGXYSimpleDoubleValueIndexBlock> blockList = dg.getSelectedValues();
        List<double[][]> doubleArrayList = this.createDoubleValueBlockList(blockList);
        String dataName = dataNameArray[ii];
        for (int jj = 0; jj < doubleArrayList.size(); jj++) {
          double[][] twoDimArray = doubleArrayList.get(jj);
          String varName = (doubleArrayList.size() == 1) ? dataName : dataName + "_" + jj;
          SGVirtualMDArrayVariable var = new SGVirtualMDArrayVariable.D2(twoDimArray, varName);
          varList.add(var);
        }
      }

      // create data source
      final int varNum = varList.size();
      SGVirtualMDArrayVariable[] vars = varList.toArray(new SGVirtualMDArrayVariable[varNum]);
      SGVirtualMDArrayFile file = new SGVirtualMDArrayFile(vars);

      if (SGDataViewerDialog.MENUCMD_DRAW_GRAPH.equals(command)) {
        SGMainFunctions.VirtualDataInfo vDataInfo = new SGMainFunctions.VirtualDataInfo();
        vDataInfo.file = file;
        vDataInfo.name = "DataViewer";

        // start the wizard
        this.mVirtualMDArrayData = vDataInfo;
        this.startVirtualMDArrayDataAdditionWizard(wnd);

      } else {

        SGPluginFile pfFile = this.mNativePluginManager.findPlugin(command);
        if (pfFile != null) {
          // plug-in is found

          // create an array of SXYZ grid type data
          String[] varNameArray = new String[varNum];
          SGData[] dataArray = new SGData[varNum];
          for (int ii = 0; ii < varNum; ii++) {
            SGVirtualMDArrayVariable var = vars[ii];
            String varName = var.getName();
            varNameArray[ii] = varName;

            // create information map
            Map<String, Object> infoMap = new HashMap<String, Object>();
            infoMap.put(
                SGIDataInformationKeyConstants.KEY_DATA_TYPE,
                SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA);
            infoMap.put(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, true);

            // create column information set
            SGMDArrayDataColumnInfo col =
                new SGMDArrayDataColumnInfo(
                    var, varName, SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
            col.setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, 0);
            col.setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, 1);
            col.setColumnType(Z_VALUE);
            SGDataColumnInfoSet colInfoSet = new SGDataColumnInfoSet(new SGDataColumnInfo[] {col});

            // create a data object
            CreatedDataSet cDataSet = this.mDataCreator.create(file, colInfoSet, infoMap, wnd);
            CreatedData cData = cDataSet.getData(0);
            dataArray[ii] = cData.getData();
          }

          // execute the command
          this.mNativePluginManager.execCommand(command, wnd, dataArray, varNameArray);
        }
      }
    }
  }

  /** Exit the application normally with confirmation. */
  public void exit() {
    this.mWindowManager.closeAllWindow();
  }

  // Exit the application.
  void exitApplication(final int status) {
    if (status != 0) {
      String msg = "A fatal error has occurred.\n" + "The current application will be terminated.";
      String title = " Forced termination";
      SGUtility.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    // delete all archive files
    this.mDataSetManager.clearDataSetFiles();

    // dispose objects
    this.mClipBoardManager.dispose();
    this.mPluginManager.dispose();

    // sleeps the thread to avoid the crash of JRE of uncertain cause
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      logger.warn("Error in main function operation", e);
    }

    // exit
    System.exit(status);
  }

  /**
   * Opens given file for Mac OS X.
   *
   * @param fname the file name
   * @param wnd the window
   * @return true if succeeded
   */
  public boolean openFile(final String fname, SGDrawingWindow wnd) {
    if (wnd == null) {
      return false;
    }
    List<File> fileList = new ArrayList<File>();
    fileList.add(new File(fname));
    return this.openFile(fileList, wnd, null);
  }

  /**
   * Called when files are dropped onto the window.
   *
   * @param fileList the list of dropped files
   * @param wnd the window
   * @param pos location the files are dropped
   * @return true if succeeded
   */
  boolean onFilesDropped(List<File> fileList, final SGDrawingWindow wnd, Point pos) {
    return this.openFile(fileList, wnd, pos);
  }

  private boolean openFile(List<File> fileList, SGDrawingWindow wnd, Point pos) {
    wnd.setWaitCursor(true);

    try {
      // analyze the file list
      List<File> textDataFileList = new ArrayList<File>();
      List<File> netCDFDataFileList = new ArrayList<File>();
      List<File> hdf5DataFileList = new ArrayList<File>();
      List<File> matlabDataFileList = new ArrayList<File>();
      File propertyFile = null;
      File archiveFile = null;
      File netcdfArchiveFile = null;
      File scriptFile = null;
      File imageFile = null;
      for (File file : fileList) {
        String path = file.getAbsolutePath();

        // property file?
        final boolean propertyFlag =
            SGApplicationUtility.hasExtension(path, PROPERTY_FILE_EXTENSION);
        if (propertyFlag) {
          propertyFile = file;
          continue;
        }

        // archive file?
        final boolean archiveFlag = SGApplicationUtility.hasExtension(path, ARCHIVE_FILE_EXTENSION);
        if (archiveFlag) {
          archiveFile = file;
          continue;
        }

        // script file?
        final boolean scriptFlag = SGApplicationUtility.hasExtension(path, SCRIPT_FILE_EXTENSION);
        if (scriptFlag) {
          scriptFile = file;
          continue;
        }

        // image file?
        boolean imageFlag =
            SGApplicationUtility.hasExtension(path, SGIImageConstants.DRAWABLE_IMAGE_EXTENSIONS);
        if (imageFlag) {
          imageFile = file;
          continue;
        }

        FILE_TYPE type = SGApplicationUtility.identifyDataFileType(path);
        if (FILE_TYPE.POSSIBLY_HDF5_DATA.equals(type)) {
          SGApplicationUtility.showHDF5ReadErrorMessageDialog(wnd, path);
          return false;
        }

        // MATLAB file?
        if (FILE_TYPE.MATLAB_DATA.equals(type)) {
          matlabDataFileList.add(file);
          continue;
        }

        // HDF5 file?
        if (FILE_TYPE.HDF5_DATA.equals(type)) {
          hdf5DataFileList.add(file);
          continue;
        }

        // netCDF file or date set file?
        boolean netcdfArchiveFlag = false;
        boolean netCDFFlag = false;
        if (FILE_TYPE.NETCDF_DATA.equals(type)) {
          if (SGNetCDFDataSetManager.isNetCDFDatasetFile(path)) {
            // confirms whether to load netCDF data set file
            final String message =
                "This NetCDF file has samurai-graph properties. Do you apply them to the graph?";
            final int confirmResult = SGUtility.showYesNoConfirmationDialog(wnd, message);
            if (confirmResult == JOptionPane.OK_OPTION) {
              netcdfArchiveFlag = true;
            } else {
              netCDFFlag = true;
            }
          } else {
            netCDFFlag = true;
          }
        }
        if (netCDFFlag) {
          netCDFDataFileList.add(file);
          continue;
        }
        if (netcdfArchiveFlag) {
          netcdfArchiveFile = file;
          continue;
        }

        // regard the file as a data file
        textDataFileList.add(file);
      }

      if (propertyFile != null) {
        // use an property file
        if (wnd.needsConfirmationBeforeDiscard()) {
          final int ret = this.beforeDiscard(wnd);
          if (ret == CANCEL_OPTION) {
            return true;
          }
        }

        // close all data viewer and animation dialogs
        this.closeAllModelessDialogs(wnd);

        if (this.mPropertyFileManager.showMultiDataFileChooserDialog(propertyFile, wnd) == false) {
          return false;
        }
      } else if (archiveFile != null) {
        // use an archive file
        if (wnd.needsConfirmationBeforeDiscard()) {
          final int ret = this.beforeDiscard(wnd);
          if (ret == CANCEL_OPTION) {
            return true;
          }
        }
        SGUtility.clearMessageDialogVisible();

        // close all data viewer and animation dialogs
        this.closeAllModelessDialogs(wnd);

        final boolean result =
            this.mDataSetManager.loadDataSetFromEventDispatchThread(wnd, archiveFile);
        if (result == false && SGUtility.wasMessageDialogVisible() == false) {
          SGUtility.showErrorMessageDialog(wnd, ERRMSG_TO_LOAD_DATASET, SGIConstants.TITLE_ERROR);
        }
        SGUtility.clearMessageDialogVisible();
        wnd.setSaved(result);
      } else if (netcdfArchiveFile != null) {
        // use an netCDF archive file
        if (wnd.needsConfirmationBeforeDiscard()) {
          final int ret = this.beforeDiscard(wnd);
          if (ret == CANCEL_OPTION) {
            return true;
          }
        }
        SGUtility.clearMessageDialogVisible();

        // close all data viewer and animation dialogs
        this.closeAllModelessDialogs(wnd);

        final boolean result =
            this.mDataSetManager.loadDataSetFromEventDispatchThread(wnd, netcdfArchiveFile);
        if (result == false && SGUtility.wasMessageDialogVisible() == false) {
          SGUtility.showErrorMessageDialog(wnd, ERRMSG_TO_LOAD_DATASET, SGIConstants.TITLE_ERROR);
        }
        SGUtility.clearMessageDialogVisible();
        wnd.setSaved(result);
      } else if (scriptFile != null) {
        if (this.getConsoleRunner().hasStreams()) {
          // sets the current window
          this.mWindowManager.setCurrentWindow(wnd);

          // loads the command script file
          this.getConsoleRunner().loadCommandScriptFile(scriptFile.getPath());
        } else {
          SGUtility.showErrorMessageDialog(wnd, ERRMSG_SCRIPT_START, SGIConstants.TITLE_ERROR);
        }
      } else if (imageFile != null) {
        // use a image file
        byte[] imageByteArray = SGApplicationUtility.toByteArray(imageFile);
        String ext = SGApplicationUtility.getImageExtension(imageFile);
        if (ext == null) {
          return false;
        }
        if (wnd.setImage(imageByteArray, ext, true) == false) {
          return false;
        }
        wnd.setImageFilePath(imageFile.getAbsolutePath());
      } else if (netCDFDataFileList.size() != 0) {
        // add netCDF data
        if (this.onNetCDFDataFilesDropped(netCDFDataFileList, wnd, pos) == false) {
          return false;
        }
      } else if (hdf5DataFileList.size() != 0) {
        // add HDF5 data
        if (this.onHDF5DataFilesDropped(hdf5DataFileList, wnd, pos) == false) {
          return false;
        }
      } else if (matlabDataFileList.size() != 0) {
        // add MATLAB data
        if (this.onMATLABDataFilesDropped(matlabDataFileList, wnd, pos) == false) {
          return false;
        }
      } else {
        if (textDataFileList.size() == 0) {
          return false;
        }
        // add text data
        if (this.onTextDataFilesDropped(textDataFileList, wnd, pos) == false) {
          return false;
        }
      }

    } catch (Exception ex) {
      logger.warn("Error in main function operation", ex);
      return false;
    } finally {
      // set enabled the window
      wnd.setWaitCursor(false);
    }

    return true;
  }

  // Updates the pattern of the tool bar in the preferences.
  void updateToolBarPatternInPreferences(final String[] array) {
    Preferences pref = Preferences.userNodeForPackage(this.getClass());

    // update the tool bar pattern
    StringBuilder sb = new StringBuilder();
    for (int ii = 0; ii < array.length; ii++) {
      sb.append(array[ii]);
      if (ii != array.length - 1) {
        sb.append(',');
      }
    }
    pref.put(PREF_KEY_TOOL_BAR_PATTERN, sb.toString());
  }

  // Updates the information of the window in the preferences.
  void updateWindowInfoInPreferences(final SGDrawingWindow wnd) {
    Preferences pref = Preferences.userNodeForPackage(this.getClass());

    // updates the preferences
    SGTuple2f size = wnd.getViewportSize();
    pref.put(PREF_KEY_VIEWPORT_WIDTH, Float.toString(size.x));
    pref.put(PREF_KEY_VIEWPORT_HEIGHT, Float.toString(size.y));
  }

  // create the data object

  /** The class for dropped data file. */
  static class DroppedDataFile {

    Point pos = null;

    File file = null;

    int figureID = -1;

    DroppedDataFile(Point pos, File file, SGDrawingWindow wnd) {
      super();
      this.pos = pos;
      this.file = file;
      if (pos != null) {
        Object com = wnd.getComponent(pos.x, pos.y);
        if (com instanceof SGFigure) {
          SGFigure figure = (SGFigure) com;
          this.figureID = figure.getID();
        }
      }
    }
  }

  private boolean onTextDataFilesDropped(
      final List<File> fileList, final SGDrawingWindow wnd, final Point pos) {

    // sets up wizard dialogs
    this.getDataAdditionHandler().createDataAdditionWizardDialogs(wnd);

    // setup with given files
    this.onDataFileDropped(
        pos, wnd, fileList, FILE_TYPE.TXT_DATA, this.mMDArrayDataSetupWizardDialog);

    if (this.getDataAdditionHandler()
            .toSDArrayDataTypeDialog(wnd, this.mDataTypeWizardDialog, this.mDroppedDataFile.file)
        == false) {
      return false;
    }

    // set the location of wizard dialog
    this.mDataTypeWizardDialog.setCenter(wnd);

    // show a modal dialog to choose data-type from candidates
    this.mDataTypeWizardDialog.setVisible(true);

    return true;
  }

  private boolean onNetCDFDataFilesDropped(
      final List<File> fileList, final SGDrawingWindow wnd, final Point pos) {

    // sets up wizard dialogs
    this.getDataAdditionHandler().createDataAdditionWizardDialogs(wnd);

    // setup with given files
    this.onDataFileDropped(
        pos, wnd, fileList, FILE_TYPE.NETCDF_DATA, this.mMDArrayDataSetupWizardDialog);

    // get commands from global attributes
    File file = this.mDroppedDataFile.file;
    NetcdfFile ncFile = null;
    try {
      ncFile = SGApplicationUtility.openNetCDF(file.getPath());
    } catch (Exception e) {
      return false;
    }

    // executes commands if they exist
    try {
      if (this.execCommand(ncFile, wnd)) {
        return true;
      }
    } finally {
      try {
        ncFile.close();
      } catch (IOException e) {
        logger.debug("Exception occurred", e);
      }
    }

    // apply embedded properties if they exist
    if (this.applyProperties(ncFile, wnd)) {
      return true;
    }

    if (this.getDataAdditionHandler()
            .toNetCDFOrMDArrayDataTypeDialog(wnd, this.mDataTypeWizardDialog, file)
        == false) {
      return false;
    }

    // set the location of wizard dialog
    this.mDataTypeWizardDialog.setCenter(wnd);

    // show a modal dialog to choose data-type from candidates
    this.mDataTypeWizardDialog.setVisible(true);

    return true;
  }

  private boolean execCommand(NetcdfFile ncFile, SGDrawingWindow wnd) {
    final Attribute attr = ncFile.findGlobalAttribute(ATTR_NAME_SAMURAI_GRAPH_COMMAND);
    if (attr != null) {
      final String commands = attr.getStringValue();
      return this.execCommand(commands, ncFile.getLocation(), wnd);
    }
    return false;
  }

  private boolean applyProperties(NetcdfFile ncFile, SGDrawingWindow wnd) {
    final Attribute attr = ncFile.findGlobalAttribute(ATTR_NAME_SAMURAI_GRAPH_PROPERTIES);
    if (attr != null) {
      final String properties = attr.getStringValue();
      return this.applyProperties(properties, ncFile.getLocation(), wnd);
    }
    return false;
  }

  private boolean execCommand(IHDF5Reader reader, SGDrawingWindow wnd) {
    if (!reader.object().hasAttribute("/", ATTR_NAME_SAMURAI_GRAPH_COMMAND)) {
      return false;
    }
    final String commands = reader.string().getAttr("/", ATTR_NAME_SAMURAI_GRAPH_COMMAND);
    if (commands != null) {
      return this.execCommand(commands, reader.file().getFile().getPath(), wnd);
    }
    return false;
  }

  private boolean applyProperties(IHDF5Reader reader, SGDrawingWindow wnd) {
    if (!reader.object().hasAttribute("/", ATTR_NAME_SAMURAI_GRAPH_PROPERTIES)) {
      return false;
    }
    final String commands = reader.string().getAttr("/", ATTR_NAME_SAMURAI_GRAPH_PROPERTIES);
    if (commands != null) {
      return this.applyProperties(commands, reader.file().getFile().getPath(), wnd);
    }
    return false;
  }

  private boolean execCommand(final String commands, String path, SGDrawingWindow wnd) {
    // sets the current window
    this.mWindowManager.setCurrentWindow(wnd);

    // Confirms whether to read commands.
    final String message = "This NetCDF data has commands. Execute them?";
    final int ret = SGUtility.showYesNoConfirmationDialog(wnd, message);
    if (ret == JOptionPane.OK_OPTION) {
      StringBuilder sbFilePath = new StringBuilder();
      sbFilePath.append('"');
      sbFilePath.append(path);
      sbFilePath.append('"');
      StringBuilder sbAlias = new StringBuilder();
      sbAlias.append('"');
      sbAlias.append(SGIDataCommandConstants.FILE_PATH_NETCDF_ITSELF);
      sbAlias.append('"');
      this.mCommandManager.addAlias(
          SGIDataCommandConstants.COM_DATA,
          SGIDataCommandConstants.COM_DATA_FILE_PATH,
          sbAlias.toString(),
          sbFilePath.toString());
      SwingUtilities.invokeLater(
          new Runnable() {
            public void run() {
              // Creates and starts the thread to execute commands.
              Thread th =
                  new Thread() {
                    public void run() {
                      // setup standard output stream
                      SGMainFunctions.this
                          .getConsoleRunner()
                          .setOutputStream(new BufferedWriter(new OutputStreamWriter(System.out)));
                      // setup standard input stream
                      SGMainFunctions.this
                          .getConsoleRunner()
                          .setInputStream(new BufferedReader(new StringReader(commands)));
                      try {
                        // read input recursively
                        SGMainFunctions.this.getConsoleRunner().startReadingInput();
                        mCommandManager.removeAlias(
                            SGIDataCommandConstants.FILE_PATH_NETCDF_ITSELF);
                      } catch (IOException e1) {
                        return;
                      } finally {
                        try {
                          // Replaces the input stream.
                          SGMainFunctions.this.getConsoleRunner().restoreSystemInput();
                          SGMainFunctions.this.getConsoleRunner().startReadingInput();
                        } catch (IOException e) {
                          logger.debug("Exception occurred", e);
                        }
                      }
                    }
                  };
              th.start();
            }
          });

      return true;
    }
    return false;
  }

  private boolean applyProperties(final String commands, String path, SGDrawingWindow wnd) {
    // sets the current window
    this.mWindowManager.setCurrentWindow(wnd);

    // Confirms whether to read commands.
    final String message = "This NetCDF data has properties. Apply them?";
    final int ret = SGUtility.showYesNoConfirmationDialog(wnd, message);
    if (ret == JOptionPane.OK_OPTION) {
      // create a Document object
      Document doc = SGUtilityText.getDocumentFromString(commands);
      if (doc == null) {
        return false;
      }
      Element root = doc.getDocumentElement();
      String versionNumber = root.getAttribute(KEY_VERSION_NUMBER);

      List<SGPropertyFileData> pfDataList = new ArrayList<SGPropertyFileData>();
      this.mPropertyFileManager.getInfoFromPropertyFile(doc, wnd, pfDataList, true);

      WrappedData[] wDataArray = new WrappedData[pfDataList.size()];
      for (int ii = 0; ii < wDataArray.length; ii++) {
        SGPropertyFileData pfData = pfDataList.get(ii);
        pfData.setFileName(path);
        wDataArray[ii] = new WrappedData(pfData);
      }

      if (this.mPropertyFileManager.setPropertyFile(
          wnd, doc, wDataArray, true, versionNumber, LOAD_PROPERTIES_IN_NETCDF_ATTRIBUTE)) {
        return true;
      }
    }

    return false;
  }

  private void onDataFileDropped(
      final Point pos,
      final SGDrawingWindow wnd,
      final List<File> fileList,
      FILE_TYPE fileType,
      SGDataSetupWizardDialog dg) {

    // setup the wizard dialogs
    this.mDataTypeWizardDialog.setDataFileType(fileType);
    this.mDataTypeWizardDialog.setNext(dg);
    this.getDataAdditionHandler()
        .setupDataAdditionWizardDialogConnection(
            this.mDataTypeWizardDialog, DATA_ADDITION_DRAG_AND_DROP, fileType, false);

    // gets only the first file
    File file = fileList.get(0);
    this.mDroppedDataFile = new DroppedDataFile(pos, file, wnd);
  }

  static SGTuple2f getDefaultFigureSize(SGDrawingWindow wnd, Point pos, String dataType) {
    Object com = (pos != null) ? wnd.getComponent(pos.x, pos.y) : wnd;
    SGTuple2f size = null;
    if (com instanceof SGFigure) {
      // figure
      SGFigure figure = (SGFigure) com;
      final float width = figure.getGraphRectWidth();
      final float height = figure.getGraphRectHeight();
      size = new SGTuple2f(width, height);
    } else {
      // window
      size = SGFigureCreator.getDefaultFigureSize(dataType);
      final String defaultUnit = SGIFigureConstants.FIGURE_SIZE_UNIT;
      final String unitNew = SGIConstants.pt;
      final float mag = wnd.getMagnification();
      size.x = (float) SGUtilityText.convert(mag * size.x, defaultUnit, unitNew);
      size.y = (float) SGUtilityText.convert(mag * size.y, defaultUnit, unitNew);
    }
    return size;
  }

  /**
   * A wrapper class for data object. This class object has one of two attributes: <code>
   * SGPropertyFileData</code> or <code>FigureData</code>. The former is used when a property file
   * is read, and the latter is used in the duplication and copy/paste of figures.
   */
  static class WrappedData {
    FigureData figureData;
    SGPropertyFileData pfData;

    WrappedData(FigureData data) {
      this.figureData = data;
      this.pfData = null;
    }

    WrappedData(SGPropertyFileData pfData) {
      this.pfData = pfData;
      this.figureData = null;
    }

    boolean hasFigureData() {
      return (this.figureData != null);
    }

    FigureData getFigureData() {
      return this.figureData;
    }

    SGPropertyFileData getPropertyFileData() {
      return this.pfData;
    }
  }

  /**
   * A wrapped data in a figure. This class object is used in paste and duplication of figures. The
   * attribute <code>serialFigureNo</code> is not the figure ID but the serial number of copied
   * figures.
   */
  static class FigureData {
    SGData data;
    int serialFigureNo;

    FigureData(SGData data, int no) {
      this.data = data;
      this.serialFigureNo = no;
    }

    SGData getData() {
      return this.data;
    }

    int getSerialFigureNo() {
      return this.serialFigureNo;
    }
  }

  /**
   * Loads the command script file.
   *
   * @param fileName file name
   */

  /**
   * Splits focused SXY type data into multiple data.
   *
   * @param wnd a window
   * @return true if succeeded
   */
  boolean splitSXYData(final SGDrawingWindow wnd) {
    Boolean result = null;
    wnd.setWaitCursor(true);
    if (!USE_FOXTROT) {
      result = SGDataSplitMergeUtility.splitData(wnd);
    } else {
      try {
        result = SGAsyncWorker.post(() -> SGDataSplitMergeUtility.splitData(wnd));
      } catch (Exception ex) {
        result = Boolean.FALSE;
        logger.warn("Error in main function operation", ex);
      }
    }
    wnd.setWaitCursor(false);
    return result.booleanValue();
  }

  /**
   * Merge focused SXY type data which was splitted from multiple graph data into one multiple graph
   * data.
   *
   * @param wnd a window
   * @return true if succeeded
   */
  boolean mergeSXYData(final SGDrawingWindow wnd) {
    Boolean result = null;
    wnd.setWaitCursor(true);
    if (!USE_FOXTROT) {
      result = SGDataSplitMergeUtility.mergeData(wnd);
    } else {
      try {
        result = SGAsyncWorker.post(() -> SGDataSplitMergeUtility.mergeData(wnd));
      } catch (Exception ex) {
        result = Boolean.FALSE;
        logger.warn("Error in main function operation", ex);
      }
    }
    wnd.setWaitCursor(false);
    return result.booleanValue();
  }

  // The color map manager for line style.
  private SGLineStyleColorMapManager mLineStyleColorMapManager = new SGLineStyleColorMapManager();

  // The name of line color map.
  private String mLineColorMapName = SGLineStyleColorMapManager.COLOR_MAP_NAME_HUE_GRADATION;

  /**
   * Shows a property dialog to assign line colors.
   *
   * @param wnd the owner window
   * @return true if succeeded
   */
  boolean assignLineColors(final SGDrawingWindow wnd) {

    // creates a dialog
    final SGLineStylePropertyDialog dg = new SGLineStylePropertyDialog(wnd, true);
    dg.addActionListener(this);

    // sets the properties of color maps
    dg.setSelectedColorMapName(mLineColorMapName);
    dg.setColorMapProperties(mLineStyleColorMapManager.getColorMapProperties());

    // gets observers
    Map<Integer, List<SGData>> dataMap = wnd.getFocusedDataMapInLegendOrder();
    Iterator<Entry<Integer, List<SGData>>> dataItr = dataMap.entrySet().iterator();
    Map<Integer, List<SGILineStylePropertyDialogObserver>> obsListMap =
        new HashMap<Integer, List<SGILineStylePropertyDialogObserver>>();
    int totalChildNum = 0;
    while (dataItr.hasNext()) {
      Entry<Integer, List<SGData>> entry = dataItr.next();
      final int figureId = entry.getKey();
      SGFigure figure = wnd.getFigure(figureId);
      SGIFigureElementGraph gElement = figure.getGraphElement();
      List<SGData> dataList = entry.getValue();
      List<SGILineStylePropertyDialogObserver> obsList =
          new ArrayList<SGILineStylePropertyDialogObserver>();
      for (SGData data : dataList) {
        if (data instanceof SGISXYTypeMultipleData) {
          SGIChildObject child = gElement.getChild(data);
          if (child instanceof SGIElementGroupSetMultipleSXY) {
            SGIElementGroupSetMultipleSXY gs = (SGIElementGroupSetMultipleSXY) child;
            totalChildNum += gs.getChildNumber();
            obsList.add(gs);
          }
        } else {
          gElement.setDataFocused(data, false);
        }
      }

      // puts observers
      obsListMap.put(figureId, obsList);
    }
    if (totalChildNum <= 1) {
      return false;
    }

    // setup the dialog
    Iterator<Entry<Integer, List<SGILineStylePropertyDialogObserver>>> obsListItr =
        obsListMap.entrySet().iterator();
    while (obsListItr.hasNext()) {
      Entry<Integer, List<SGILineStylePropertyDialogObserver>> entry = obsListItr.next();
      final int figureId = entry.getKey();
      final List<SGILineStylePropertyDialogObserver> obsList = entry.getValue();
      if (dg.putLineStylePropertyDialogObserverList(figureId, obsList) == false) {
        return false;
      }

      // prepares to show the property dialog
      for (SGILineStylePropertyDialogObserver obs : obsList) {
        obs.prepare();
      }
    }
    if (dg.setDialogProperty() == false) {
      return false;
    }

    // shows the dialog
    dg.pack();
    dg.setLocation(wnd.getX() + 20, wnd.getY() + 20);
    dg.setVisible(true);
    dg.removeActionListener(this);

    // updates the attribute
    this.mLineStyleColorMapManager.setColorMapProperties(dg.getColorMapProperties());
    this.mLineColorMapName = dg.getLineColorMapName();

    return true;
  }

  SGColorMap getCurrentColorMap() {
    return this.getColorMap(this.mLineColorMapName);
  }

  SGColorMap getColorMap(final String colorMapName) {
    return this.mLineStyleColorMapManager.getColorMap(colorMapName);
  }

  /**
   * Shows the memory information.
   *
   * @param wnd the window
   */
  public void showMemoryInfo(SGDrawingWindow wnd) {
    DecimalFormat fByte = new DecimalFormat("#,###KB");
    DecimalFormat fRatio = new DecimalFormat("##.#");
    final long free = Runtime.getRuntime().freeMemory() / 1024;
    final long total = Runtime.getRuntime().totalMemory() / 1024;
    final long max = Runtime.getRuntime().maxMemory() / 1024;
    final long used = total - free;
    final double ratio = (used * 100 / (double) total);
    StringBuilder sb = new StringBuilder();
    sb.append("total=");
    sb.append(fByte.format(total));
    sb.append(", used=");
    sb.append(fByte.format(used));
    sb.append(" (");
    sb.append(fRatio.format(ratio));
    sb.append("%), max=");
    sb.append(fByte.format(max));
    SGUtility.showMessageDialog(wnd, sb.toString(), "Memory Info", JOptionPane.INFORMATION_MESSAGE);
  }

  static class TransformedData {
    SGData data = null;
    String name = null;
    int figureId = -1;

    TransformedData(SGData data, String name, int figureId) {
      super();
      this.data = data;
      this.name = name;
      this.figureId = figureId;
    }
  }

  /**
   * Transforms data object to the other type data.
   *
   * @param wnd the window
   * @return true if succeeded
   */
  boolean transformData(SGDrawingWindow wnd) {

    this.getDataAdditionHandler().createDataAdditionWizardDialogs(wnd);

    List<SGFigure> fList = wnd.getVisibleFigureList();
    for (SGFigure f : fList) {

      // get focused data objects
      SGIFigureElementGraph gElement = f.getGraphElement();
      List<SGData> dataList = gElement.getFocusedDataList();

      if (dataList.size() > 0) {
        SGData data = dataList.get(0);
        String name = gElement.getDataName(data);
        this.mTransformedData = new TransformedData(data, name, f.getID());
        FILE_TYPE dataFileType;
        if (SGDataDataTypeUtility.isSDArrayData(data)) {
          dataFileType = FILE_TYPE.TXT_DATA;
        } else if (SGDataDataTypeUtility.isNetCDFData(data)) {
          dataFileType = FILE_TYPE.NETCDF_DATA;
        } else if (SGDataDataTypeUtility.isHDF5Data(data)) {
          dataFileType = FILE_TYPE.HDF5_DATA;
        } else if (SGDataDataTypeUtility.isMATLABData(data)) {
          dataFileType = FILE_TYPE.MATLAB_DATA;
        } else if (SGDataDataTypeUtility.isVirtualMDArrayData(data)) {
          dataFileType = FILE_TYPE.VIRTUAL_DATA;
        } else {
          throw new Error("Unsupported data type: " + data.getDataType());
        }

        this.mDataTypeWizardDialog.setDataFileType(dataFileType);
        this.mDataTypeWizardDialog.setPrevious(null);
        this.mDataTypeWizardDialog.pack();

        // set the location of wizard dialog
        this.mDataTypeWizardDialog.setCenter(wnd);

        // show a modal dialog to choose data-type from candidates
        this.mDataTypeWizardDialog.setVisible(true);

        break;
      }
    }

    return true;
  }

  private boolean makeTransitionForDataTransformation(final ActionEvent e) {
    TransformedData transformedData =
        new TransformedData(
            this.mTransformedData.data, this.mTransformedData.name, this.mTransformedData.figureId);
    SGDataTransformationHandler transform =
        new SGDataTransformationHandler(
            this,
            this.mDataTypeWizardDialog,
            this.mSDArrayDataSetupWizardDialog,
            this.mNetCDFDataSetupWizardDialog,
            this.mMDArrayDataSetupWizardDialog,
            this.mPlotTypeSelectionWizardDialog,
            transformedData);
    return transform.addDataByDataTransformation(e);
  }

  public void windowOpened(WindowEvent e) {}

  /** Invoked when a wizard dialog for data addition is closed. */
  public void windowClosing(WindowEvent e) {
    Object source = e.getSource();
    if (this.getDataAdditionHandler().isDataAdditionDialog(source)) {
      this.clearTemporaryData();
    }
  }

  // Clears temporary files.
  void clearTemporaryData() {
    this.mDroppedDataFile = null;
    this.mTransformedData = null;
    this.mVirtualMDArrayData = null;
  }

  public void windowClosed(WindowEvent e) {}

  public void windowIconified(WindowEvent e) {}

  public void windowDeiconified(WindowEvent e) {}

  public void windowActivated(WindowEvent e) {}

  public void windowDeactivated(WindowEvent e) {}

  /**
   * Sets the dimension origin and step.
   *
   * @param infoMap map
   * @param dg data setup dialog
   * @return
   */
  static boolean addDimensionValuesToInfoMap(
      final Map<String, Object> infoMap, final SGNetCDFDataSetupWizardDialog dg) {
    addDimensionValuesToInfoMapSub(infoMap, dg);
    return true;
  }

  static boolean addDimensionValuesToInfoMap(
      final Map<String, Object> infoMap, final SGMDArrayDataSetupWizardDialog dg) {
    addDimensionValuesToInfoMapSub(infoMap, dg);
    infoMap.put(
        SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP,
        dg.getPickupDimensionIndexMap());
    return true;
  }

  static boolean addDimensionValuesToInfoMapSub(
      final Map<String, Object> infoMap, final SGDataSetupWizardDialog dg) {
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, dg.isVariableDataType());
    SGIntegerSeriesSet indices = dg.getSXYPickUpIndices();
    if (indices != null) {
      infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, indices);
    }
    return true;
  }

  /**
   * Add values which are selected in plot type selection dialog into infoMap.
   *
   * @param infoMap
   */
  static void addPlotTypeSelectionValuesToInfoMap(
      final Map<String, Object> infoMap, final SGPlotTypeSelectionWizardDialog dialog) {
    infoMap.put(SGPlotTypeConstants.KEY_PLOT_TYPE_SELECTED, Boolean.TRUE);
    infoMap.put(SGPlotTypeConstants.KEY_PLOT_TYPE_LINE, Boolean.valueOf(dialog.isLineSelected()));
    infoMap.put(
        SGPlotTypeConstants.KEY_PLOT_TYPE_SYMBOL, Boolean.valueOf(dialog.isSymbolSelected()));
    infoMap.put(SGPlotTypeConstants.KEY_PLOT_TYPE_BAR, Boolean.valueOf(dialog.isBarSelected()));
    infoMap.put(
        SGPlotTypeConstants.KEY_PLOT_TYPE_ERRORBAR_ONLINE,
        Boolean.valueOf(dialog.isErrorBarPlaceLineSymbolSelected()));
    infoMap.put(
        SGPlotTypeConstants.KEY_PLOT_TYPE_LINE_COLOR_AUTO_ASSIGNED,
        Boolean.valueOf(dialog.isLineColorAutoAssignmentSelected()));
  }

  /**
   * Proceeds plugin query message.
   *
   * @param message has message string and object of content.
   */
  void updatePluginsMessage(final SGPluginsQueryMessage message) {
    if (SGPluginsQueryMessage.MENUCMD_OUTPUT_TO_FILE_IS_ENABLED.equals(message.getMessage())) {
      message.set(Boolean.valueOf(this.mPluginManager.hasOutputPlugins()));
    } else if (SGPluginsQueryMessage.MENUCMD_EXEC_OUTPUT_TO_FILE.equals(message.getMessage())) {
      SGDrawingWindow wnd = this.getActiveWindow();
      Object obj = message.get();
      if (obj instanceof SGData) {
        try {
          this.mPluginManager.doOutputToFile(wnd, (SGData) obj);
        } catch (Exception e) {
          SGUtility.showErrorMessageDialog(
              wnd, "Failed to output data to file", SGIConstants.TITLE_ERROR);
        }
      }
    }
  }

  /**
   * Reload all data objects in a given window.
   *
   * @param wnd the window
   * @param showStatus true to show the status
   * @return true if succeeded to reload all data
   */
  public boolean reloadData(final SGDrawingWindow wnd, final boolean showStatus) {

    // get all figures and data
    List<SGFigure> figureList = wnd.getVisibleFigureList();
    DataList dl = SGDataDialogUtility.getVisibleDataList(figureList);
    List<SGData> dataList = dl.dataList;
    Map<SGData, SGFigure> dataFigureMap = dl.figureMap;

    // get data path and data source
    Set<String> dataPathSet = new HashSet<String>(); // to prevent duplication
    Map<String, SGIDataSource> srcMap = new HashMap<String, SGIDataSource>();
    for (SGData data : dataList) {
      String path = data.getPath();
      if (path == null) {
        continue;
      }
      dataPathSet.add(path);
      srcMap.put(path, data.getDataSource());
    }

    // creates new data source
    Map<String, SGIDataSource> srcMapNew = new HashMap<String, SGIDataSource>();
    Map<String, RELOAD_DATA_STATUS> resultMap = new HashMap<String, RELOAD_DATA_STATUS>();
    for (String path : dataPathSet) {
      File dataFile = new File(path);
      SGIDataSource srcCur = srcMap.get(path);
      boolean found = true;
      if (srcCur instanceof SGNetCDFFile) {
        SGNetCDFFile ncfile = (SGNetCDFFile) srcCur;
        if (!ncfile.isRemoteFile()) {
          if (!dataFile.exists()) {
            found = false;
          }
        }
      } else {
        if (!dataFile.exists()) {
          found = false;
        }
      }
      if (!found) {
        resultMap.put(path, RELOAD_DATA_STATUS.LOST);
        continue;
      }

      SGIDataSource srcNew = null;
      if (srcCur instanceof SGNetCDFFile) {
        NetcdfFile ncfile;
        try {
          ncfile = SGApplicationUtility.openNetCDF(path);
        } catch (IOException e) {
          resultMap.put(path, RELOAD_DATA_STATUS.INVALID_DATA);
          continue;
        }
        srcNew = new SGNetCDFFile(ncfile);
      } else if (srcCur instanceof SGHDF5File) {
        IHDF5Reader reader = null;
        try {
          reader = SGApplicationUtility.openHDF5(path);
        } catch (HDF5Exception e) {
          resultMap.put(path, RELOAD_DATA_STATUS.INVALID_DATA);
          continue;
        }
        srcNew = new SGHDF5File(reader);
      } else if (srcCur instanceof SGMATLABFile) {
        MatFileReader reader = null;
        try {
          reader = SGApplicationUtility.openMAT(path);
        } catch (IOException e) {
          resultMap.put(path, RELOAD_DATA_STATUS.INVALID_DATA);
          continue;
        }
        srcNew = new SGMATLABFile(path, reader);
      }
      srcMapNew.put(path, srcNew);
      resultMap.put(path, RELOAD_DATA_STATUS.SUCCEEDED);
    }
    // for text data
    for (SGData data : dataList) {
      SGFigure figure = dataFigureMap.get(data);
      if (SGDataDataTypeUtility.isSDArrayData(data)) {
        String path = data.getPath();
        SGIFigureElementGraph gElement = figure.getGraphElement();
        SGDataColumnInfo[] cols = gElement.getDataColumnInfoArray(data);
        SGDataColumnInfoSet colInfoSet = new SGDataColumnInfoSet(cols);
        Map<String, Object> infoMap = data.getInfoMap();
        SGIDataSource srcNew = null;
        try {
          srcNew = SGApplicationUtility.createDataSource(path, colInfoSet, infoMap);
        } catch (FileNotFoundException e) {
          resultMap.put(path, RELOAD_DATA_STATUS.LOST);
          continue;
        }
        if (srcNew == null) {
          resultMap.put(path, RELOAD_DATA_STATUS.INVALID_DATA);
          continue;
        }
        srcMapNew.put(path, srcNew);
        resultMap.put(path, RELOAD_DATA_STATUS.SUCCEEDED);
      }
    }

    SGDataSourceObserver obs = this.mDataCreator.getDataSourceObserver();
    for (SGData data : dataList) {
      String path = data.getPath();
      if (path == null) {
        continue;
      }
      RELOAD_DATA_STATUS result = resultMap.get(path);
      if (RELOAD_DATA_STATUS.LOST.equals(result)
          || RELOAD_DATA_STATUS.INVALID_DATA.equals(result)) {
        continue;
      }
      SGIDataSource srcNew = srcMapNew.get(path);
      SGFigure figure = dataFigureMap.get(data);
      SGIFigureElementGraph gElement = figure.getGraphElement();

      // replaces the data source
      SGIDataSource srcOld = data.getDataSource();
      gElement.replaceDataSource(srcOld, srcNew, obs);

      // updates the drawing elements
      if (!gElement.updateDrawingElementsLocation(data)) {
        resultMap.put(path, RELOAD_DATA_STATUS.INVALID_DATA);
      }
    }

    final DataReloadResultSet resultsSet = new DataReloadResultSet();
    int cntError = 0;
    for (SGData data : dataList) {
      String path = data.getPath();
      if (path == null) {
        continue;
      }
      RELOAD_DATA_STATUS status = resultMap.get(path);
      if (RELOAD_DATA_STATUS.LOST.equals(status)
          || RELOAD_DATA_STATUS.INVALID_DATA.equals(status)) {
        cntError++;
      }
      DataReloadResult result = new DataReloadResult(path, status);
      resultsSet.put(path, result);
    }

    try {
      // shows result on dialog
      if (cntError != 0) {
        if (showStatus) {
          final SGDataReloadResultPanel p = new SGDataReloadResultPanel();
          p.setResult(resultsSet);

          // beep
          Toolkit.getDefaultToolkit().beep();

          // show a dialog
          SGUtility.showMessageDialog(wnd, p, SGIConstants.TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }
        return false;
      }

    } finally {
      // repaint data viewer dialogs
      for (SGDataViewerDialog dg : this.mDataViewerDialogArray) {
        SGData data = dg.getData();
        for (SGFigure f : figureList) {
          SGElementGroupSetInGraph gs =
              (SGElementGroupSetInGraph) f.getGraphElement().getChild(data);
          if (gs != null) {
            gs.setFocusedValueIndices(dg);
            break;
          }
        }
        dg.repaint();
      }
    }

    return true;
  }

  /** The results of reloading data. */
  public static class DataReloadResultSet {
    private Map<String, DataReloadResult> mMap = new TreeMap<String, DataReloadResult>();

    public DataReloadResultSet() {
      super();
    }

    public DataReloadResult get(final String filePath) {
      return this.mMap.get(filePath);
    }

    public void put(final String filePath, DataReloadResult result) {
      this.mMap.put(filePath, result);
    }

    public Map<String, DataReloadResult> getMap() {
      return new TreeMap<String, DataReloadResult>(this.mMap);
    }

    @Override
    public String toString() {
      return this.mMap.toString();
    }
  }

  public static class DataReloadResult {
    private String mPath;
    private RELOAD_DATA_STATUS mStatus;

    public DataReloadResult(final String path, final RELOAD_DATA_STATUS status) {
      super();
      if (path == null || status == null) {
        throw new IllegalArgumentException("path == null || status == null");
      }
      this.mPath = path;
      this.mStatus = status;
    }

    public String getPath() {
      return this.mPath;
    }

    public RELOAD_DATA_STATUS getStatus() {
      return this.mStatus;
    }

    public String toString() {
      return this.mPath + ": " + this.mStatus.toString();
    }
  }

  void outputDataToFile(SGDrawingWindow wnd, String command) {
    List<SGFigure> fList = wnd.getVisibleFigureList();
    SGIElementGroupSetForData groupSet = null;
    for (SGFigure f : fList) {
      SGIFigureElementGraph gElement = f.getGraphElement();
      List<SGData> dataList = gElement.getFocusedDataList();
      for (SGData data : dataList) {
        String dataName = gElement.getDataName(data);
        groupSet = (SGIElementGroupSetForData) gElement.getChild(data);
        this.mDataFileExporter.setCurrentDirectory(this.getCurrentFileDirectory());

        // output to a file
        this.mDataFileExporter.export(wnd, groupSet, dataName, command);

        File selectedFile = this.mDataFileExporter.getCurrentFile();
        FILE_TYPE fileType = this.mDataFileExporter.getCurrentFileType();
        if (selectedFile != null && fileType != null) {
          this.updateCurrentFile(selectedFile, fileType);
        }
      }
    }
  }

  private boolean onHDF5DataFilesDropped(
      final List<File> fileList, final SGDrawingWindow wnd, final Point pos) {

    this.getDataAdditionHandler().createDataAdditionWizardDialogs(wnd);

    // gets only the first file
    File file = fileList.get(0);
    this.mDroppedDataFile = new DroppedDataFile(pos, file, wnd);
    FILE_TYPE initFileType = this.getNetCDF4orHDF5FileType(file);

    String path = file.getPath();
    IHDF5Reader reader = null;
    try {
      reader = SGApplicationUtility.openHDF5(path);
    } catch (HDF5Exception ex) {
      return false;
    }
    try {
      // execute embedded commands if they exist
      if (this.execCommand(reader, wnd)) {
        return true;
      }

      // apply embedded properties if they exist
      if (this.applyProperties(reader, wnd)) {
        return true;
      }
    } finally {
      reader.close();
    }

    String filePath = file.getAbsolutePath();
    String dataName = SGUtility.createDataNameBase(filePath);
    this.mFileTypeSelectionWizardDialog.setDataName(dataName);

    this.mFileTypeSelectionWizardDialog.setSelectedFileType(initFileType);
    this.getDataAdditionHandler()
        .setupDataAdditionWizardDialogConnection(
            this.mDataTypeWizardDialog, DATA_ADDITION_DRAG_AND_DROP, FILE_TYPE.HDF5_DATA, true);

    this.mFileTypeSelectionWizardDialog.setCenter(wnd);
    this.mFileTypeSelectionWizardDialog.setVisible(true);

    return true;
  }

  private boolean onMATLABDataFilesDropped(
      final List<File> fileList, final SGDrawingWindow wnd, final Point pos) {

    this.getDataAdditionHandler().createDataAdditionWizardDialogs(wnd);

    this.onDataFileDropped(
        pos, wnd, fileList, FILE_TYPE.MATLAB_DATA, this.mMDArrayDataSetupWizardDialog);

    if (this.getDataAdditionHandler()
            .toNetCDFOrMDArrayDataTypeDialog(
                wnd, this.mDataTypeWizardDialog, this.mDroppedDataFile.file)
        == false) {
      return false;
    }

    // set the location of wizard dialog
    this.mDataTypeWizardDialog.setCenter(wnd);

    // shows the dialog to select data type
    this.mDataTypeWizardDialog.setVisible(true);

    return true;
  }

  boolean startVirtualMDArrayDataAdditionWizard(final SGDrawingWindow wnd) {

    // sets up wizard dialogs
    this.getDataAdditionHandler().createDataAdditionWizardDialogs(wnd);
    this.mDataTypeWizardDialog.setDataFileType(FILE_TYPE.VIRTUAL_DATA);
    this.mDataTypeWizardDialog.setAllDataTypeButtonsEnabled(true);

    // set figure ID numbers
    final int[] idArray = wnd.getVisibleFigureIDArray();
    this.mFigureIDSelectionWizardDialog.setIDNumbers(idArray);

    // sets the OK button visible
    final boolean figureIdDialogOKVisible = (this.mVirtualMDArrayData.dataType != null);
    this.mFigureIDSelectionWizardDialog.setOKButtonVisible(figureIdDialogOKVisible);

    // sets the name
    String name = this.mVirtualMDArrayData.name;
    this.mFigureIDSelectionWizardDialog.setDataName(name);

    // packs the dialog
    this.mFigureIDSelectionWizardDialog.pack();

    // sets the location of wizard dialog
    this.mFigureIDSelectionWizardDialog.setCenter(wnd);

    // shows a dialog
    this.mFigureIDSelectionWizardDialog.setVisible(true);

    return true;
  }

  /**
   * Returns a text string for the version.
   *
   * @return a text string for the version
   */
  public String getVersionString() {
    return this.mAppProp.getVersionString();
  }

  /**
   * Sets virtual bounds.
   *
   * @param rect a rectangle to set
   */
  public static void setVirtualBounds(Rectangle rect) {
    virtualBounds = rect;
  }

  /**
   * Returns the virtual bounds.
   *
   * @return virtual bounds
   */
  public static Rectangle getVirtualBounds() {
    return virtualBounds;
  }

  // Returns the temporary directory.
  private static File getTemporaryDirectory() {
    File tempFile = null;
    try {
      tempFile = File.createTempFile("SamuraiGraphTemporaryFile", null);
    } catch (IOException e) {
      logger.debug("Exception occurred", e);
    }
    File tempDir = null;
    if (tempFile != null) {
      String tempDirName = tempFile.getParent();
      tempFile.delete();
      tempDir = new File(tempDirName);
    }
    return tempDir;
  }

  static class VirtualDataInfo {
    SGVirtualMDArrayFile file;
    String name;
    String dataType;
    SGDataColumnInfoSet colInfoSet;
    SGDataBuffer buffer;
    Map<String, Object> infoMap;

    VirtualDataInfo() {
      super();
    }
  }

  void showPluginDetailDialog(SGDrawingWindow wnd) {
    this.mNativePluginManager.showPluginInfoDialog(wnd);
  }

  FILE_TYPE getNetCDF4orHDF5FileType(File file) {
    String ext = SGApplicationUtility.getExtension(file);
    FILE_TYPE fileType =
        NETCDF_FILE_EXTENSION.equalsIgnoreCase(ext) ? FILE_TYPE.NETCDF_DATA : FILE_TYPE.HDF5_DATA;
    return fileType;
  }

  /**
   * Returns the active window.
   *
   * @return the active window
   */
  public SGDrawingWindow getActiveWindow() {
    return this.mWindowManager.getActiveWindow();
  }

  enum SAVED_OBJECT_TYPE {
    PROPERTIES,
    COMMAND_SCRIPT,
  };

  /** Saves properties or script into global attributes of data source file of selected data. */
  @SuppressWarnings("deprecation")
  void saveIntoGlobalAttributes(SGDrawingWindow wnd, SAVED_OBJECT_TYPE objType) {

    // get visible figures and focused NetCDF data objects in them
    List<SGFigure> visibleFigureList = wnd.getVisibleFigureList();
    List<SGData> focusedDataList = new ArrayList<SGData>();
    Map<SGData, SGFigure> dataFigureMap = new HashMap<SGData, SGFigure>();
    for (SGFigure figure : visibleFigureList) {
      List<SGData> dataList = figure.getFocusedDataList();
      for (SGData data : dataList) {
        if (SGDataDataTypeUtility.isNetCDFData(data)) {
          focusedDataList.add(data);
          dataFigureMap.put(data, figure);
        }
      }
    }
    if (focusedDataList.size() == 0) {
      SGUtility.showErrorMessageDialog(
          wnd,
          "Data objects originated from NetCDF files are not selected.",
          SGIConstants.TITLE_ERROR);
      return;
    }

    // get file paths without duplication
    Set<String> dataPathSet = new HashSet<String>();
    for (SGData data : focusedDataList) {
      String path = data.getPath();
      dataPathSet.add(path);
    }

    // make a map of data sources
    Map<String, SGIDataSource> dataSrcMap = new HashMap<String, SGIDataSource>();
    for (SGData data : focusedDataList) {
      String path = data.getPath();
      dataSrcMap.put(path, data.getDataSource());
    }

    Map<SGIDataSource, SGIDataSource> srcNewMap = new HashMap<SGIDataSource, SGIDataSource>();
    int cntErr = 0;
    for (String path : dataPathSet) {
      // finds data objects with current data source
      List<SGData> dataList = new ArrayList<SGData>();
      for (SGData data : focusedDataList) {
        if (path.equals(data.getPath())) {
          dataList.add(data);
        }
      }

      // creates parameters
      SGDataExportParameter params =
          new SGDataExportParameter(OPERATION.SAVE_INTO_FILE_ATTRIBUTE, dataList);

      // creates a string to be saved
      String savedString = null;
      String attrName = null;
      if (SAVED_OBJECT_TYPE.PROPERTIES.equals(objType)) {
        try {
          savedString =
              SGApplicationUtility.getPropertyString(wnd, params, this.mAppProp.getVersionString());
        } catch (IOException e) {
          showErrMsgDialogSavingNetCDFAttribute(wnd, path);
          cntErr++;
          continue;
        }
        attrName = ATTR_NAME_SAMURAI_GRAPH_PROPERTIES;
      } else if (SAVED_OBJECT_TYPE.COMMAND_SCRIPT.equals(objType)) {
        savedString = wnd.getCommandString(params);
        attrName = ATTR_NAME_SAMURAI_GRAPH_COMMAND;
      }
      if (savedString == null) {
        showErrMsgDialogSavingNetCDFAttribute(wnd, path);
        cntErr++;
        continue;
      }

      // closes current NetCDF file
      SGNetCDFFile dataSrcCur = (SGNetCDFFile) dataSrcMap.get(path);
      try {
        dataSrcCur.getNetcdfFile().close();
      } catch (IOException e1) {
        logger.warn("Error in main function operation", e1);
      }

      // adds global attributes
      @SuppressWarnings("deprecation")
      NetcdfFileWriter ncWrite = null;
      try {
        ncWrite = NetcdfFileWriter.openExisting(path);
        final Attribute curAttr = ncWrite.findGlobalAttribute(attrName);
        ncWrite.setRedefineMode(true);
        if (curAttr != null) {
          ncWrite.deleteGroupAttribute(null, attrName);
        }
        ncWrite.addGroupAttribute(null, new Attribute(attrName, savedString));
        ncWrite.setRedefineMode(false);
        Attribute attr = new Attribute(attrName, savedString);
        ncWrite.updateAttribute(null, attr);
      } catch (IOException ioe) {
        logger.debug("Exception occurred", ioe);
      } finally {
        if (ncWrite != null) {
          try {
            ncWrite.close();
          } catch (IOException e) {
            logger.debug("Exception occurred", e);
          }
        }
      }
      IHDF5Writer hdfWriter = null;
      if (ncWrite == null) {
        // updates the attribute
        try {
          hdfWriter = HDF5FactoryProvider.get().open(new File(path));
          hdfWriter.string().setAttr("/", attrName, savedString);
        } catch (Exception e) {
          if (e.getClass().getName().contains("HDF5Exception")) {
            logger.warn("Error in main function operation", e);
          } else {
            if (e instanceof RuntimeException) {
              throw (RuntimeException) e;
            }
          }
        } finally {
          if (hdfWriter != null) {
            hdfWriter.close();
          }
        }
      }

      if (ncWrite == null && hdfWriter == null) {
        showErrMsgDialogSavingNetCDFAttribute(wnd, path);
        cntErr++;
        continue;
      }

      // creates new data source
      NetcdfFile ncfile = null;
      try {
        ncfile = SGApplicationUtility.openNetCDF(path);
      } catch (IOException e) {
        showErrMsgDialogSavingNetCDFAttribute(wnd, path);
        cntErr++;
        continue;
      }
      SGNetCDFFile dataSrcNew = new SGNetCDFFile(ncfile);
      for (SGData data : dataList) {
        srcNewMap.put(data.getDataSource(), dataSrcNew);
      }
    }

    // replaces data source
    SGDataSourceObserver obs = this.mDataCreator.getDataSourceObserver();
    List<SGDrawingWindow> windowList = this.mWindowManager.getWindowList();
    for (SGDrawingWindow window : windowList) {
      List<SGFigure> figureList = window.getFigureList();
      for (SGFigure figure : figureList) {
        SGIFigureElement[] elArray = figure.getIFigureElementArray();
        for (SGIFigureElement el : elArray) {
          Iterator<Entry<SGIDataSource, SGIDataSource>> itr = srcNewMap.entrySet().iterator();
          while (itr.hasNext()) {
            Entry<SGIDataSource, SGIDataSource> entry = itr.next();
            SGIDataSource srcOld = entry.getKey();
            SGIDataSource srcNew = entry.getValue();
            el.replaceDataSource(srcOld, srcNew, obs);
          }
        }
      }
    }

    if (cntErr == 0) {
      SGUtility.showMessageDialog(
          wnd,
          "Succeeded to add commands to NetCDF files.",
          SGIConstants.TITLE_MESSAGE,
          JOptionPane.INFORMATION_MESSAGE);
    }
  }

  private static void showErrMsgDialogSavingNetCDFAttribute(SGDrawingWindow wnd, String path) {
    SGUtility.showErrorMessageDialog(
        wnd, "Failed to add commands to a NetCDF file: \n" + path, SGIConstants.TITLE_ERROR);
  }

  /**
   * Returns whether this application is launched in the command mode.
   *
   * @return true if this application is launched in the command mode
   */
  public boolean isCommandMode() {
    return this.mCommandModeFlag;
  }

  SGNetCDFFile getNetCDFFile(String path) {
    return this.mDataCreator.getNetcdfFile(path);
  }

  SGHDF5File getHDF5File(String path) {
    return this.mDataCreator.getHDF5File(path);
  }

  SGMATLABFile getMATLABFile(String path) {
    return this.mDataCreator.getMATLABFile(path);
  }

  void showAnimationDialog(final SGDrawingWindow wnd) {
    // get animation objects
    List<SGIDataAnimation> animationList = new ArrayList<SGIDataAnimation>();
    ArrayList<SGFigure> figureList = wnd.getVisibleFigureList();
    List<SGData> dataList = new ArrayList<SGData>();
    boolean validAll = true;
    for (SGFigure figure : figureList) {
      SGIFigureElementGraph gElement = figure.getGraphElement();
      List<SGData> dList = figure.getFocusedDataList();
      for (SGData data : dList) {
        if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
          validAll = false;
          break;
        }
        SGIChildObject child = gElement.getChild(data);
        if (!(child instanceof SGIDataAnimation)) {
          validAll = false;
          break;
        }
        SGIDataAnimation animation = (SGIDataAnimation) child;
        animationList.add(animation);
        dataList.add(animation.getData());
      }
      if (!validAll) {
        break;
      }
    }
    if (!validAll) {
      SGUtility.showErrorMessageDialog(wnd, "Invalid data is selected for animation.", ERROR);
      return;
    }

    // check the frame number
    int[] frameNumbers = new int[animationList.size()];
    for (int ii = 0; ii < animationList.size(); ii++) {
      SGIDataAnimation animation = animationList.get(ii);
      final int num = animation.getFrameNumber();
      frameNumbers[ii] = num;
    }
    Integer frameNumber = SGUtility.checkEquality(frameNumbers);
    if (frameNumber == null) {
      SGUtility.showErrorMessageDialog(wnd, "Frame number is different.", ERROR);
      return;
    }

    // check overlapping of data objects
    boolean found = false;
    for (SGData data : dataList) {
      for (int ii = 0; ii < this.mDataAnimationDialogArray.length; ii++) {
        SGData[] dataArray = this.mDataAnimationDialogArray[ii].getDataArray();
        if (SGUtility.contains(dataArray, data)) {
          found = true;
          break;
        }
      }
      if (found) {
        break;
      }
    }
    if (found) {
      SGUtility.showErrorMessageDialog(
          wnd, "Selected data is already assigned to an animation dialog.", ERROR);
      return;
    }

    // clears focused objects
    wnd.clearAllFocusedObjectsInFigures();

    // creates and shows the animation dialog
    final SGDataAnimationDialog dg = new SGDataAnimationDialog(wnd, false);
    SGDataAnimationDialog[] dgArray = null;
    dgArray = new SGDataAnimationDialog[this.mDataAnimationDialogArray.length + 1];
    for (int ii = 0; ii < this.mDataAnimationDialogArray.length; ii++) {
      dgArray[ii] = this.mDataAnimationDialogArray[ii];
    }
    dgArray[dgArray.length - 1] = dg;
    this.mDataAnimationDialogArray = dgArray;

    String fileName = SGApplicationUtility.getOutputFileName(wnd);
    dg.setFilePath(this.getCurrentFileDirectory(), fileName);
    SGIDataAnimation[] animations =
        animationList.toArray(new SGIDataAnimation[animationList.size()]);
    for (SGIDataAnimation animation : animations) {
      animation.prepareForChanges();
    }
    dg.setAnimation(animations);
    dg.addWindowListener(
        new WindowAdapter() {

          @Override
          public void windowClosing(WindowEvent e) {
            onAnimationDialogCancelAndClosed(dg);

            // repaint focused symbols
            repaintFocusedSymbols(dg);
          }
        });
    dg.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            final String command = e.getActionCommand();
            if (SGDataAnimationDialog.DIALOG_SAVED_AND_CLOSED.equals(command)) {
              onAnimationDialogSaveAndClosed(dg);
            } else if (SGDataAnimationDialog.DIALOG_CANCELED_AND_CLOSED.equals(command)) {
              onAnimationDialogCancelAndClosed(dg);
            } else if (SGDataAnimationDialog.ANIMATION_FRAME_CHANGED.equals(command)) {
              repaintAllDataViewerDialogs(dg);
            }

            // repaint focused symbols
            repaintFocusedSymbols(dg);
          }
        });
    dg.setCenter(wnd);
    dg.setVisible(true);
  }

  private void repaintFocusedSymbols(SGDataAnimationDialog dg) {
    SGDrawingWindow wnd = dg.getOwnerWindow();
    SGData[] animationDataArray = dg.getDataArray();
    for (SGData animationData : animationDataArray) {
      for (SGDataViewerDialog dataViewer : mDataViewerDialogArray) {
        SGData viewerData = dataViewer.getData();
        if (viewerData.equals(animationData)) {
          List<SGFigure> figureList = wnd.getVisibleFigureList();
          for (SGFigure f : figureList) {
            SGElementGroupSetInGraph gs =
                (SGElementGroupSetInGraph) f.getGraphElement().getChild(viewerData);
            if (gs != null) {
              gs.updateWithData();
              gs.setFocusedValueIndices(dataViewer);
              break;
            }
          }
        }
      }
    }
    wnd.repaint();
  }

  private void repaintAllDataViewerDialogs(SGDataAnimationDialog dataAnimationDialog) {
    if (this.mDataViewerDialogArray.length == 0) {
      return;
    }
    SGData[] animationDataArray = dataAnimationDialog.getDataArray();
    for (SGData animationData : animationDataArray) {
      for (SGDataViewerDialog dataViewer : this.mDataViewerDialogArray) {
        SGData data = dataViewer.getData();
        if (data.equals(animationData)) {
          dataViewer.repaint();
          break;
        }
      }
    }
  }

  private void onAnimationDialogCancelAndClosed(final SGDataAnimationDialog dg) {
    SwingUtilities.invokeLater(
        new Runnable() {

          @Override
          public void run() {

            onDataAnimationDialogClosing(dg);

            // updates the current directory
            setCurrentFileDirectory(dg.getFolderPath());
          }
        });
  }

  private void onAnimationDialogSaveAndClosed(final SGDataAnimationDialog dg) {
    SwingUtilities.invokeLater(
        new Runnable() {

          @Override
          public void run() {

            onDataAnimationDialogClosing(dg);

            // notifies the changes of animation objects
            final SGDrawingWindow wnd = dg.getOwnerWindow();
            wnd.notifyToRoot();

            // updates the current directory
            setCurrentFileDirectory(dg.getFolderPath());
          }
        });
  }

  /** The array of dialogs to setup the animation of data objects. */
  SGDataAnimationDialog[] mDataAnimationDialogArray = new SGDataAnimationDialog[0];

  /** The array of data viewer dialogs. */
  SGDataViewerDialog[] mDataViewerDialogArray = new SGDataViewerDialog[0];

  void showDataViewerDialog(final SGDrawingWindow wnd) {
    // create the map for focused data
    Map<Integer, List<SGData>> dataMap = wnd.getFocusedDataMap();
    Iterator<Entry<Integer, List<SGData>>> itr = dataMap.entrySet().iterator();
    List<SGData> dataList = new ArrayList<SGData>();
    List<String> dataNameList = new ArrayList<String>();
    final Map<SGData, Integer> figureIdMap = new HashMap<SGData, Integer>();
    while (itr.hasNext()) {
      Entry<Integer, List<SGData>> entry = itr.next();
      Integer figureId = entry.getKey();
      SGFigure figure = wnd.getFigure(figureId);
      List<SGData> dList = entry.getValue();
      dataList.addAll(dList);
      for (SGData data : dList) {
        String dataName = figure.getDataName(data);
        dataNameList.add(dataName);
        figureIdMap.put(data, figureId);
      }
    }
    final int offset = this.mDataViewerDialogArray.length;
    SGDataViewerDialog[] dgArray = new SGDataViewerDialog[offset + dataList.size()];
    for (int ii = 0; ii < offset; ii++) {
      dgArray[ii] = this.mDataViewerDialogArray[ii];
    }
    this.mDataViewerDialogArray = dgArray;
    for (int ii = 0; ii < dataList.size(); ii++) {
      final SGData data = dataList.get(ii);
      final String dataName = dataNameList.get(ii);
      final SGDataViewerDialog dg = new SGDataViewerDialog(wnd, false);
      dg.setData(data, dataName);
      dg.addActionListener(this);
      dg.addWindowListener(this);
      dg.addTableSelectionListener(
          new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
              setDataSelectedValues(dg, figureIdMap);
            }
          });
      dg.addTableMouseListener(
          new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
              if (SwingUtilities.isLeftMouseButton(e)) {
                setDataSelectedValues(dg, figureIdMap);
              }
            }
          });
      dg.addTableMouseMotionListener(
          new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
              setDataSelectedValues(dg, figureIdMap);
            }
          });
      dg.addHighlightChangedActionListener(
          new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              setDataSelectedValues(dg, figureIdMap);
            }
          });
      dg.addWindowListener(
          new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
              SwingUtilities.invokeLater(
                  new Runnable() {
                    public void run() {
                      onDataViewerClosing(dg, figureIdMap);
                    }
                  });
            }
          });
      dg.addActionListener(
          new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              SwingUtilities.invokeLater(
                  new Runnable() {

                    @Override
                    public void run() {
                      if (!dg.isVisible()) {
                        onDataViewerClosing(dg, figureIdMap);
                      }
                    }
                  });
            }
          });
      dg.setCenter(wnd);
      dg.setVisible(true);
      this.mDataViewerDialogArray[ii + offset] = dg;
    }
  }

  private void onDataViewerClosing(SGDataViewerDialog dg, Map<SGData, Integer> figureIdMap) {

    SGDrawingWindow wnd = dg.getOwnerWindow();
    SGData data = dg.getData();

    // clear focused values
    Integer figureId = figureIdMap.get(data);
    SGFigure figure = wnd.getFigure(figureId);
    SGElementGroupSetInGraph gs =
        (SGElementGroupSetInGraph) figure.getGraphElement().getChild(data);
    gs.removeFocusedValueList(dg);
    wnd.repaint();

    // update dialog array
    if (this.mDataViewerDialogArray.length == 1) {
      this.mDataViewerDialogArray = new SGDataViewerDialog[0];
    } else {
      SGDataViewerDialog[] array = new SGDataViewerDialog[this.mDataViewerDialogArray.length - 1];
      int cnt = 0;
      for (int ii = 0; ii < this.mDataViewerDialogArray.length; ii++) {
        if (!this.mDataViewerDialogArray[ii].equals(dg)) {
          array[cnt] = this.mDataViewerDialogArray[ii];
          cnt++;
        }
      }
      this.mDataViewerDialogArray = array;
    }
  }

  private void onDataAnimationDialogClosing(SGDataAnimationDialog dg) {

    // repaint data viewer dialogs
    repaintAllDataViewerDialogs(dg);

    // update dialog array
    if (this.mDataAnimationDialogArray.length == 1) {
      this.mDataAnimationDialogArray = new SGDataAnimationDialog[0];
    } else {
      SGDataAnimationDialog[] array =
          new SGDataAnimationDialog[this.mDataAnimationDialogArray.length - 1];
      int cnt = 0;
      for (int ii = 0; ii < this.mDataAnimationDialogArray.length; ii++) {
        if (!this.mDataAnimationDialogArray[ii].equals(dg)) {
          array[cnt] = this.mDataAnimationDialogArray[ii];
          cnt++;
        }
      }
      this.mDataAnimationDialogArray = array;
    }
  }

  private void setDataSelectedValues(
      final SGDataViewerDialog dg, final Map<SGData, Integer> figureIdMap) {
    SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            SGDrawingWindow wnd = dg.getOwnerWindow();
            SGData data = dg.getData();
            Integer figureId = figureIdMap.get(data);
            SGFigure figure = wnd.getFigure(figureId);
            SGElementGroupSetInGraph gs =
                (SGElementGroupSetInGraph) figure.getGraphElement().getChild(data);
            gs.setFocusedValueIndices(dg);
            wnd.repaint();
          }
        });
  }

  /** Fits axis range to the focused data. */
  SGStatus fitAxisRangeToFocusedData(final SGDrawingWindow wnd, final String command) {
    SGStatus result = null;
    if (!USE_FOXTROT) {
      fitAxisRangeToFocusedDataSub(wnd, command);
      result = new SGStatus(true);
    } else {
      try {
        result =
            SGAsyncWorker.post(
                () -> {
                  fitAxisRangeToFocusedDataSub(wnd, command);
                  return new SGStatus(true);
                });
      } catch (Exception ex) {
        result = new SGStatus(false);
        logger.warn("Error in main function operation", ex);
      }
    }
    return result;
  }

  private void fitAxisRangeToFocusedDataSub(SGDrawingWindow wnd, String command) {

    wnd.endProgress();
    wnd.setWaitCursor(true);
    wnd.setProgressMessage("Fit Axes");

    wnd.startIndeterminateProgress();

    List<Integer> axisDirList = new ArrayList<Integer>();
    boolean forAnimationFrames = false;
    if (MENUCMD_FIT_ALL_AXES_TO_DATA.equals(command)
        || MENUCMD_FIT_ALL_AXES_TO_DATA_FOR_ALL_ANIMATION_FRAMES.equals(command)) {
      axisDirList.add(SGIFigureElementAxis.AXIS_DIRECTION_HORIZONTAL);
      axisDirList.add(SGIFigureElementAxis.AXIS_DIRECTION_VERTICAL);
      axisDirList.add(SGIFigureElementAxis.AXIS_DIRECTION_NORMAL);
      if (MENUCMD_FIT_ALL_AXES_TO_DATA_FOR_ALL_ANIMATION_FRAMES.equals(command)) {
        forAnimationFrames = true;
      }
    } else if (MENUCMD_FIT_HORIZONTAL_AXIS_TO_DATA.equals(command)) {
      axisDirList.add(SGIFigureElementAxis.AXIS_DIRECTION_HORIZONTAL);
    } else if (MENUCMD_FIT_VERTICAL_AXIS_TO_DATA.equals(command)) {
      axisDirList.add(SGIFigureElementAxis.AXIS_DIRECTION_VERTICAL);
    } else if (MENUCMD_FIT_COLOR_BAR_TO_DATA.equals(command)) {
      axisDirList.add(SGIFigureElementAxis.AXIS_DIRECTION_NORMAL);
    }

    boolean changed = false;
    List<SGFigure> fList = wnd.getVisibleFigureList();
    for (int dir : axisDirList) {
      for (int ii = 0; ii < fList.size(); ii++) {
        SGFigure f = fList.get(ii);
        f.fitAxisRangeToFocusedData(dir, forAnimationFrames);
        if (f.isChangedRoot()) {
          changed = true;
        }
      }
    }
    if (changed) {
      wnd.notifyToRoot();
    }
    wnd.repaintContentPane();

    wnd.endProgress();
    wnd.setWaitCursor(false);
  }

  void closeAllDataViewerDialogs(SGDrawingWindow wnd) {
    if (this.mDataViewerDialogArray.length == 0) {
      return;
    }
    List<SGFigure> focusedFigureList = wnd.getVisibleFigureList();
    DataList dl = SGDataDialogUtility.getVisibleDataList(focusedFigureList);
    List<SGData> dataList = dl.dataList;
    Map<SGData, SGFigure> figureMap = dl.figureMap;
    this.mDataViewerDialogArray =
        SGDataDialogUtility.closeDataViewerDialogsSub(
            this.mDataViewerDialogArray, dataList, figureMap, wnd);
  }

  void closeDataViewerDialogsOfFocusedData(SGDrawingWindow wnd) {
    if (this.mDataViewerDialogArray.length == 0) {
      return;
    }
    List<SGFigure> figureList = wnd.getVisibleFigureList();
    List<SGData> focusedDataList = new ArrayList<SGData>();
    Map<SGData, SGFigure> figureMap = new HashMap<SGData, SGFigure>();
    for (SGFigure f : figureList) {
      List<SGData> dList = f.getFocusedDataList();
      focusedDataList.addAll(dList);
      for (SGData data : dList) {
        figureMap.put(data, f);
      }
    }
    this.mDataViewerDialogArray =
        SGDataDialogUtility.closeDataViewerDialogsSub(
            this.mDataViewerDialogArray, focusedDataList, figureMap, wnd);
  }

  void closeDataViewerDialogsInFocusedFigures(SGDrawingWindow wnd) {
    if (this.mDataViewerDialogArray.length == 0) {
      return;
    }
    List<SGFigure> focusedFigureList = wnd.getFocusedFigureList();
    DataList dl = SGDataDialogUtility.getVisibleDataList(focusedFigureList);
    List<SGData> dataList = dl.dataList;
    Map<SGData, SGFigure> figureMap = dl.figureMap;
    this.mDataViewerDialogArray =
        SGDataDialogUtility.closeDataViewerDialogsSub(
            this.mDataViewerDialogArray, dataList, figureMap, wnd);
  }

  void closeDataViewerDialogInAllFigures(SGDrawingWindow wnd, final boolean bUndo) {
    if (this.mDataViewerDialogArray.length == 0) {
      return;
    }
    List<SGFigure> focusedFigureList = wnd.getVisibleFigureList();
    DataList dl = SGDataDialogUtility.getVisibleDataList(focusedFigureList);
    List<SGData> dataList = dl.dataList;
    Map<SGData, SGFigure> figureMap = dl.figureMap;
    this.mDataViewerDialogArray =
        SGDataDialogUtility.closeDataViewerDialogsSub(
            this.mDataViewerDialogArray, dataList, figureMap, wnd, bUndo);
  }

  void closeAllDataAnimationDialogs(SGDrawingWindow wnd) {
    if (this.mDataAnimationDialogArray.length == 0) {
      return;
    }
    List<SGFigure> visibleFigureList = wnd.getVisibleFigureList();
    DataList dl = SGDataDialogUtility.getVisibleDataList(visibleFigureList);
    List<SGData> dataList = dl.dataList;
    this.mDataAnimationDialogArray =
        SGDataDialogUtility.closeDataAnimationDialogsSub(this.mDataAnimationDialogArray, dataList);
  }

  void closeDataAnimationDialogsOfFocusedData(SGDrawingWindow wnd) {
    if (this.mDataAnimationDialogArray.length == 0) {
      return;
    }
    List<SGFigure> figureList = wnd.getVisibleFigureList();
    List<SGData> focusedDataList = new ArrayList<SGData>();
    for (SGFigure f : figureList) {
      List<SGData> dList = f.getFocusedDataList();
      focusedDataList.addAll(dList);
    }
    this.mDataAnimationDialogArray =
        SGDataDialogUtility.closeDataAnimationDialogsSub(
            this.mDataAnimationDialogArray, focusedDataList);
  }

  void closeDataAnimationDialogsInFocusedFigures(SGDrawingWindow wnd) {
    if (this.mDataAnimationDialogArray.length == 0) {
      return;
    }
    List<SGFigure> focusedFigureList = wnd.getFocusedFigureList();
    DataList dl = SGDataDialogUtility.getVisibleDataList(focusedFigureList);
    List<SGData> dataList = dl.dataList;
    this.mDataAnimationDialogArray =
        SGDataDialogUtility.closeDataAnimationDialogsSub(this.mDataAnimationDialogArray, dataList);
  }

  void closeDataAnimationDialogInAllFigures(SGDrawingWindow wnd, final boolean bUndo) {
    if (this.mDataAnimationDialogArray.length == 0) {
      return;
    }
    List<SGFigure> focusedFigureList = wnd.getVisibleFigureList();
    DataList dl = SGDataDialogUtility.getVisibleDataList(focusedFigureList);
    List<SGData> dataList = dl.dataList;
    Map<SGData, SGFigure> figureMap = dl.figureMap;
    this.mDataAnimationDialogArray =
        SGDataDialogUtility.closeDataAnimationDialogsSub(
            this.mDataAnimationDialogArray, dataList, figureMap, wnd, bUndo);
  }

  void refreshAllDataViewerDialogs() {
    for (SGDataViewerDialog dg : this.mDataViewerDialogArray) {
      dg.refresh();
    }
  }

  void updateDataTableCellSelection(SGDrawingWindow wnd) {
    if (this.mDataViewerDialogArray.length == 0) {
      return;
    }
    List<SGFigure> focusedFigureList = wnd.getVisibleFigureList();
    DataList dl = SGDataDialogUtility.getVisibleDataList(focusedFigureList);
    List<SGData> dataList = dl.dataList;
    Map<SGData, SGFigure> figureMap = dl.figureMap;
    for (SGDataViewerDialog dg : this.mDataViewerDialogArray) {
      SGData data = dg.getData();
      if (!dataList.contains(data)) {
        continue;
      }
      String columnType = dg.getColumnType();
      SGFigure figure = figureMap.get(data);
      SGIFigureElementGraph gElement = figure.getGraphElement();
      List<SGTwoDimensionalArrayIndex> indexList =
          gElement.getSelectedDataIndexList(data, columnType);
      dg.setSelectedIndices(indexList);
    }
  }

  static class DataList {
    List<SGData> dataList = new ArrayList<SGData>();
    Map<SGData, SGFigure> figureMap = new HashMap<SGData, SGFigure>();
  }

  void closeAllModelessDialogs(SGDrawingWindow wnd) {
    // close all data viewer and animation dialogs
    this.closeAllDataAnimationDialogs(wnd);
    this.closeAllDataViewerDialogs(wnd);
  }
}

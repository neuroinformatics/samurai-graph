package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
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
import java.util.Arrays;
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
import jp.riken.brain.ni.samuraigraph.application.SGDataCreator.FileColumn;
import jp.riken.brain.ni.samuraigraph.application.SGDataCreator.SDArrayFileParseResult;
import jp.riken.brain.ni.samuraigraph.base.SGBufferedFileReader;
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
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementLegend;
import jp.riken.brain.ni.samuraigraph.base.SGIProgressControl;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyFileConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIRootObjectConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGPlotTypeConstants;
import jp.riken.brain.ni.samuraigraph.base.SGPluginsQueryMessage;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUserProperties;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGDataDuplicationDialog;
import jp.riken.brain.ni.samuraigraph.data.SGDataTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataViewerDialog;
import jp.riken.brain.ni.samuraigraph.data.SGDefaultColumnTypeUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDefaultColumnTypeUtility.DefaultMDColumnTypeResult;
import jp.riken.brain.ni.samuraigraph.data.SGHDF5File;
import jp.riken.brain.ni.samuraigraph.data.SGIDataAnimation;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataPropertyKeyConstants;
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
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataSetupPanel;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayPickUpDimensionInfo;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataDuplicationDialog;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataSetupPanel;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFPickUpDimensionInfo;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFVariable;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataDuplicationDialog;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGSXYMDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGVXYGridDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGVXYMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGVirtualMDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGVirtualMDArrayVariable;
import jp.riken.brain.ni.samuraigraph.data.SGXYSimpleDoubleValueIndexBlock;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureTypeConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGILineStylePropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyleColorMapManager;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStylePropertyDialog;
import jp.riken.brain.ni.samuraigraph.figure.SGXYFigure;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupSetInGraph;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGIElementGroupSetForData;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGIElementGroupSetMultipleSXY;
import ncsa.hdf.hdf5lib.exceptions.HDF5DatatypeInterfaceException;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriteable;
import ch.systemsx.cisd.hdf5.HDF5FactoryProvider;
import ch.systemsx.cisd.hdf5.IHDF5Reader;
import ch.systemsx.cisd.hdf5.IHDF5Writer;

import com.jmatio.io.MatFileReader;

import foxtrot.Task;
import foxtrot.Worker;

/**
 * The main thread.
 */
class SGMainFunctions implements ActionListener, SGIConstants,
        SGIUpgradeConstants, SGIApplicationCommandConstants,
        SGIApplicationConstants, SGIPropertyFileConstants,
        SGIPreferencesConstants, SGIApplicationTextConstants,
        SGIRootObjectConstants, SGIImageConstants, SGIArchiveFileConstants,
        SGIDataColumnTypeConstants, WindowListener, SGINetCDFConstants {

    // only used for debug
    static boolean USE_FOXTROT = true;

    /**
     * Virtual bounds.
     */
	private static Rectangle virtualBounds = null;

    /**
     * Application Properties
     */
    SGApplicationProperties mAppProp = null;

    /**
     * Data set manager
     */
    SGDataSetManager mDataSetManager;

    /**
     * Proxy manager.
     */
    SGProxyManager mProxyManager;

    /**
     * Upgrade manager.
     */
    SGUpgradeManager mUpgradeManager;

    /**
     * Data creator.
     */
    SGDataCreator mDataCreator;

    /**
     * Window manager
     */
    SGWindowManager mWindowManager = null;

    /**
     * Clip Board manager.
     */
    SGClipBoardManager mClipBoardManager = null;

    /**
     * Command manager.
     */
    SGCommandManager mCommandManager = null;

    /**
     * Property file manager.
     */
    SGPropertyFileManager mPropertyFileManager = null;

    /**
     * Data file exporter.
     */
    SGDataFileExporter mDataFileExporter = null;

    /**
     * Command script manager.
     */
    SGCommandScriptManager mCommandScriptManager = null;
    
    /**
     * Figure creator.
     */
    SGFigureCreator mFigureCreator = null;

    /**
     * Plug-in manager.
     */
    SGPluginManager mPluginManager = null;
    
    /**
     * Native plug-in manager.
     */
    SGNativePluginManager mNativePluginManager = null;
    
    /**
     * Initializer.
     */
    private Initializer mInit = null;

    /**
     * The standard output stream.
     */
    private BufferedWriter mStdoutWriter = null;

    /**
     * The standard input stream.
     */
    private BufferedReader mStdinReader = null;

    private static final int DATA_ADDITION_TOOL_BAR = 0;

    private static final int DATA_ADDITION_DRAG_AND_DROP = 1;

    private static final int DATA_ADDITION_VIRTUAL = 2;

    static final String KEY_FILE_TYPE = "fileType";

    static final String KEY_FILE_NAME = "fileName";

    static final String KEY_COMMAND_MODE_FLAG = "commandMode";

    static final String KEY_DEVELOPER_MODE_FLAG = "developerMode";

    private static final String TEMP_DATA_FILE_DIR_NAME = "SamuraiGraphData";
    
    /**
     * A file drag and dropped into a window.
     */
    private DroppedDataFile mDroppedDataFile = null;

    /**
     * A transformed data object.
     */
    private TransformedData mTransformedData = null;

    /**
     * A virtual data of multidimensional array.
     */
    VirtualDataInfo mVirtualMDArrayData = null;

    /**
     * The flag of command mode.
     */
	private boolean mCommandModeFlag = false;

    /**
     * Create a thread object.
     *
     */
    public SGMainFunctions(final SGApplicationProperties prop, final Map<String, Object> paramMap) {
        super();
        this.mAppProp = prop;
        this.mInit = new Initializer(paramMap);
        this.mInit.start();
    }

    /**
     * SGMainFunctions :: Initializer class
     *
     */
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
    				if (pattern1.matcher(name).matches()
    						|| pattern2.matcher(name).matches()) {
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
            }
            sw.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            sw.setProgressValue(0.0f);
            sw.setVisible(true);

            // take the procedure below to get rid of warning messages as follows:
            // "log4j:WARN No appenders could be found for logger (ucar.nc2.NetcdfFile)."
            // "log4j:WARN Please initialize the log4j system properly."
            org.apache.log4j.BasicConfigurator.configure();
            org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
            logger.setLevel(org.apache.log4j.Level.OFF);

            // remove temporary files used in upgrade
            this.removeUpdaterTemporaryFiles();

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
            SGMainFunctions.this.mDataSetManager = new SGDataSetManager(SGMainFunctions.this,
            		sw, 0.15f, 0.50f);
            sw.setProgressValue(0.50f);
            SGMainFunctions.this.mProxyManager = new SGProxyManager();
            sw.setProgressValue(0.60f);
            SGMainFunctions.this.mUpgradeManager = new SGUpgradeManager(
                    SGMainFunctions.this.mProxyManager,
                    SGMainFunctions.this.mAppProp);
            SGMainFunctions.this.mWindowManager = new SGWindowManager(SGMainFunctions.this,
            		sw, 0.60f, 0.80f);
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
            boolean result = SGMainFunctions.this.mPluginManager.loadPlugins(
            		SGIApplicationConstants.APPLICATION_PLUGIN_DIRECTORY);
            if (result == false) {
                String filename = SGMainFunctions.this.mPluginManager.getFirstExceptionJarFilename();
                SGUtility.showErrorMessageDialog(null, "Plugins load error.\n" + filename, SGIConstants.TITLE_ERROR);
            }
            SGMainFunctions.this.mNativePluginManager = new SGNativePluginManager(SGMainFunctions.this);
            SGMainFunctions.this.mNativePluginManager.loadPlugins(APPLICATION_PLUGIN_DIRECTORY);
            
            sw.setProgressValue(1.0f);

            // creates and shows the window
            final SGDrawingWindow wnd = SGMainFunctions.this.mWindowManager
                    .createNewWindow();
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
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            SGMainFunctions.this.mPropertyFileManager
                                    .showMultiDataFileChooserDialog(f, wnd);
                        }
                    });
                } catch (InterruptedException e) {
                } catch (InvocationTargetException e) {
                }

            } else if (FILE_TYPE.DATASET.equals(type)) {
                // disable window
                wnd.setWaitCursor(true);

                final File f = new File(path);
                if (f.exists()) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                if (SGMainFunctions.this.mDataSetManager.loadDataSetFromEventDispatchThread(
                                		wnd, f) == false) {
                                    SGUtility.showErrorMessageDialog(wnd, MSG_DATA_SET_FILE_INVALID,
                                            SGIConstants.TITLE_ERROR);
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                    } catch (InvocationTargetException e) {
                    }
                } else {
                    // file not found
                    SGUtility.showErrorMessageDialog(wnd, MSG_FILE_OPEN_FAILURE,
                            TITLE_FILE_OPEN_FAILURE);
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
                    SwingUtilities.invokeAndWait(new Runnable() {
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
                } catch (InvocationTargetException e) {
                }

            } else if (this.mStartupFileType == FILE_TYPE.SCRIPT) {
                // setup standard output stream
                mStdoutWriter = new BufferedWriter(new OutputStreamWriter(
                        System.out));

                // load the command script file
                loadCommandScriptFile(path);

                // set up standard input stream
                mStdinReader = new BufferedReader(new InputStreamReader(
                        System.in));
                try {
                    // read input recursively
                	startReadingInput();
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
                mStdoutWriter = new BufferedWriter(new OutputStreamWriter(
                        System.out));

                // setup standard input stream
                mStdinReader = new BufferedReader(new InputStreamReader(
                        System.in));
                try {
                    // read input recursively
                	startReadingInput();
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
            SGSplashWindow sw = new SGSplashWindow("Splash.png",
                    SGMainFunctions.this.mAppProp.getVersionString());
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

        /**
         * Remove temporary files used in upgrade.
         */
        private void removeUpdaterTemporaryFiles() {
            String filename = SGApplicationUtility.getPathName(TMP_DIR, HELPER_TEMP_DIR_NAME);
            File temp = new File(filename);
            try {
                temp = temp.getCanonicalFile();
            } catch (IOException ex) {
                return;
            }
            if (temp.exists()) {
                SGApplicationUtility.deleteRecursively(temp);
            }
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
     * @param id
     *           the window ID of a window to close
     * @param true if succeeded
     */
    public boolean closeWindowWithoutConfirmation(final int id) {
        return this.mWindowManager.closeWindowWithoutConfirmation(id);
    }

    /**
     * Execute a command.
     *
     * @param line
     *            the command line
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

    /**
     * A wizard dialog to select the figure ID.
     */
    private SGFigureIDSelectionWizardDialog mFigureIDSelectionWizardDialog = null;

    /**
     * A wizard dialog to select a single data file.
     */
    private SGSingleDataFileChooserWizardDialog mSingleDataFileChooserWizardDilaog = null;

    /**
     * A wizard dialog to select the file type.
     */
    private SGFileTypeSelectionWizardDialog mFileTypeSelectionWizardDialog = null;

    /**
     * A wizard dialog to select the data type.
     */
    private SGDataTypeWizardDialog mDataTypeWizardDialog = null;

    /**
     * A wizard dialog to setup single-dimensional array data.
     */
    private SGSDArrayDataSetupWizardDialog mSDArrayDataSetupWizardDialog = null;

    /**
     * A wizard dialog to setup NetCDF data.
     */
    private SGNetCDFDataSetupWizardDialog mNetCDFDataSetupWizardDialog = null;

    /**
     * A wizard dialog to setup multidimensional array data.
     */
    private SGMDArrayDataSetupWizardDialog mMDArrayDataSetupWizardDialog = null;

    /**
     * A wizard dialog to select the plot type if the data type is scalar-xy.
     */
    private SGPlotTypeSelectionWizardDialog mPlotTypeSelectionWizardDialog = null;

    /**
     * Creates the wizard dialogs for data addition.
     *
     * @param owner
     *            the owner of wizard dialogs
     */
    private void createDataAdditionWizardDialogs(
            final SGDrawingWindow owner) {

        // if the owner window is the same, do nothing
        if (this.mFigureIDSelectionWizardDialog != null) {
            SGDrawingWindow curOwner = this.mFigureIDSelectionWizardDialog
                    .getOwnerWindow();
            if (curOwner.equals(owner)) {
                return;
            }
        }

        //
        // creates dialogs
        //

        // common to all data types
        this.mFigureIDSelectionWizardDialog = new SGFigureIDSelectionWizardDialog(
                owner, true);
        this.mSingleDataFileChooserWizardDilaog = new SGSingleDataFileChooserWizardDialog(
                owner, true);
        this.mDataTypeWizardDialog = new SGDataTypeWizardDialog(owner, true);
        this.mPlotTypeSelectionWizardDialog = new SGPlotTypeSelectionWizardDialog(
                owner, true);
        
        // for text data
        this.mSDArrayDataSetupWizardDialog = new SGSDArrayDataSetupWizardDialog(
                owner, true);
        this.mSDArrayDataSetupWizardDialog.setPrevious(this.mDataTypeWizardDialog);

		// for netCDF data
		this.mNetCDFDataSetupWizardDialog = new SGNetCDFDataSetupWizardDialog(
				owner, true);
        this.mNetCDFDataSetupWizardDialog.setPrevious(this.mDataTypeWizardDialog);

		// for multidimensional array data
		this.mMDArrayDataSetupWizardDialog = new SGMDArrayDataSetupWizardDialog(owner,
				true);
        this.mMDArrayDataSetupWizardDialog.setPrevious(this.mDataTypeWizardDialog);
        
        // for HDF5 and NetCDF-4 file
        this.mFileTypeSelectionWizardDialog = new SGFileTypeSelectionWizardDialog(owner, 
        		true);

        // sets the connection between wizard dialogs
		this.mFigureIDSelectionWizardDialog.setPrevious(null);

        //
        // Remaining connections are selected depending on the method of data addition
        //

        // sets the selected file name
        String path = this.getCurrentFileDirectory();
        this.mSingleDataFileChooserWizardDilaog.setCurrentFile(path, null);

        // add action listener
        this.mFigureIDSelectionWizardDialog.addActionListener(this);
        this.mSingleDataFileChooserWizardDilaog.addActionListener(this);
        this.mFileTypeSelectionWizardDialog.addActionListener(this);
        this.mDataTypeWizardDialog.addActionListener(this);
        this.mSDArrayDataSetupWizardDialog.addActionListener(this);
        this.mNetCDFDataSetupWizardDialog.addActionListener(this);
        this.mMDArrayDataSetupWizardDialog.addActionListener(this);
        this.mPlotTypeSelectionWizardDialog.addActionListener(this);

        // add window listener
        this.mFigureIDSelectionWizardDialog.addWindowListener(this);
        this.mSingleDataFileChooserWizardDilaog.addWindowListener(this);
        this.mFileTypeSelectionWizardDialog.addWindowListener(this);
        this.mDataTypeWizardDialog.addWindowListener(this);
        this.mSDArrayDataSetupWizardDialog.addWindowListener(this);
        this.mNetCDFDataSetupWizardDialog.addWindowListener(this);
        this.mMDArrayDataSetupWizardDialog.addWindowListener(this);
        this.mPlotTypeSelectionWizardDialog.addWindowListener(this);

        // packs the dialogs
        this.mFigureIDSelectionWizardDialog.pack();
        this.mSingleDataFileChooserWizardDilaog.pack();
        this.mFileTypeSelectionWizardDialog.pack();
    }

    private void setupDataAdditionWizardDialogConnection(
    		final SGDataTypeWizardDialog dataTypeDialog, final int method,
    		final FILE_TYPE fileType, final boolean isFileTypeDialogPrev) {

        // setup the connection
        switch (method) {
        case DATA_ADDITION_TOOL_BAR:
            this.mFigureIDSelectionWizardDialog.setPrevious(null);
            
            this.mFigureIDSelectionWizardDialog.setNext(this.mSingleDataFileChooserWizardDilaog);
            this.mSingleDataFileChooserWizardDilaog.setPrevious(this.mFigureIDSelectionWizardDialog);
            
            if (dataTypeDialog != null) {
            	if (isFileTypeDialogPrev) {
                    this.mSingleDataFileChooserWizardDilaog.setNext(this.mFileTypeSelectionWizardDialog);
                    this.mFileTypeSelectionWizardDialog.setPrevious(this.mSingleDataFileChooserWizardDilaog);

                    this.mFileTypeSelectionWizardDialog.setNext(dataTypeDialog);
                    dataTypeDialog.setPrevious(this.mFileTypeSelectionWizardDialog);
            	} else {
                    this.mSingleDataFileChooserWizardDilaog.setNext(dataTypeDialog);
                    dataTypeDialog.setPrevious(this.mSingleDataFileChooserWizardDilaog);
            	}
            }

            // packs
            this.mSingleDataFileChooserWizardDilaog.pack();
            if (dataTypeDialog != null) {
            	dataTypeDialog.pack();
            }
            break;
        case DATA_ADDITION_DRAG_AND_DROP:
            if (dataTypeDialog != null) {
            	if (isFileTypeDialogPrev) {
            		this.mFileTypeSelectionWizardDialog.setPrevious(null);
            		
                    this.mFileTypeSelectionWizardDialog.setNext(dataTypeDialog);
                    dataTypeDialog.setPrevious(this.mFileTypeSelectionWizardDialog);
            	} else {
                    dataTypeDialog.setPrevious(null);
            	}
                
                // packs
            	dataTypeDialog.pack();
            }
            break;
        case DATA_ADDITION_VIRTUAL:
            this.mFigureIDSelectionWizardDialog.setPrevious(null);
            
            if (dataTypeDialog != null) {
                this.mFigureIDSelectionWizardDialog.setNext(dataTypeDialog);
                dataTypeDialog.setPrevious(this.mFigureIDSelectionWizardDialog);
                
                // packs
            	dataTypeDialog.pack();
            }
        	break;
        default:
            throw new IllegalArgumentException("Invalid method: " + method);
        }
    }

    /**
     * Sets up the connection of wizard dialogs.
     * If the data type is Scalar-XY, Plot type selection dialog is connected.
     *
     * @param fileType
     *            the file type (array data or netcdf)
     * @param dataType
     *            the method of data addition (tool bar or drag and drop)
     */
    void setupPlotTypeSelectionWizardDialogConnection(final FILE_TYPE fileType,
            final String dataType) {

        // select the data column type wizard dialog
        final SGDataSetupWizardDialog dataSetupDialog;
        switch (fileType) {
        case TXT_DATA:
            dataSetupDialog = this.mSDArrayDataSetupWizardDialog;
            break;
        case NETCDF_DATA:
            dataSetupDialog = this.mNetCDFDataSetupWizardDialog;
            break;
        case HDF5_DATA:
        case MATLAB_DATA:
        case VIRTUAL_DATA:
        	dataSetupDialog = this.mMDArrayDataSetupWizardDialog;
        	break;
        default:
            throw new IllegalArgumentException("Invalid type for data file: "+ fileType);
        }

        if (SGDataUtility.isSXYTypeData(dataType)) {
            this.mPlotTypeSelectionWizardDialog.setDataName(dataSetupDialog.getDataName());
            
            dataSetupDialog.setNext(this.mPlotTypeSelectionWizardDialog);
            this.mPlotTypeSelectionWizardDialog.setPrevious(dataSetupDialog);
            this.mPlotTypeSelectionWizardDialog.setNext(null);
            dataSetupDialog.pack();
            this.mPlotTypeSelectionWizardDialog.pack();

            SGDataColumnInfo[] cols = dataSetupDialog.getDataColumnInfoSet().getDataColumnInfoArray();
            int xCnt = 0;
            int yCnt = 0;
            for (SGDataColumnInfo col : cols) {
            	String columnType = col.getColumnType();
            	if (X_VALUE.equals(columnType)) {
            		xCnt++;
            	} else if (Y_VALUE.equals(columnType)) {
            		yCnt++;
            	}
            }
            boolean enabled = false;
            if (SGDataUtility.isNetCDFData(dataType)) {
            	SGNetCDFDataSetupWizardDialog ncDialog = (SGNetCDFDataSetupWizardDialog) dataSetupDialog;
            	SGNetCDFDataSetupPanel ncPanel = (SGNetCDFDataSetupPanel) ncDialog.getDataSetupPanel();
            	SGIntegerSeriesSet pickUpIndices = ncPanel.getSXYPickUpIndices();
            	if (pickUpIndices != null) {
                	final int len = pickUpIndices.getLength();
                	enabled = len > 1;
            	}
            } else if (SGDataUtility.isMDArrayData(dataType)) {
            	SGMDArrayDataSetupWizardDialog mdDialog = (SGMDArrayDataSetupWizardDialog) dataSetupDialog;
            	SGMDArrayDataSetupPanel mdPanel = (SGMDArrayDataSetupPanel) mdDialog.getDataSetupPanel();
            	SGIntegerSeriesSet pickUpIndices = mdPanel.getSXYPickUpIndices();
            	if (pickUpIndices != null) {
                	final int len = pickUpIndices.getLength();
                	enabled = len > 1;
            	}
            }
            if (!enabled){
                enabled = (xCnt > 1 || yCnt > 1);
            }
            this.mPlotTypeSelectionWizardDialog.setLineColorAutoAssignmentEnabled(enabled);
        }
    }

    /**
     * Map for the current file name.
     */
    private Map<FILE_TYPE, String> mCurrentFileNameMap = new HashMap<FILE_TYPE, String>();
    
    /**
     * Returns the current file name of given file type.
     * 
     * @param fType
     *           file type
     * @return the current file name
     */
    String getCurrentFileName(FILE_TYPE fType) {
    	return this.mCurrentFileNameMap.get(fType);
    }

    /**
     * Updates the current file.
     * @param f
     *           a file
     * @param type
     *           the file type
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

    /**
     * Returns the current file directory.
     *
     */
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
            StringBuffer sb = new StringBuffer();
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
     * @param path
     *           path of the current file directory
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

        SGAboutDialog dg = new SGAboutDialog(wnd, true, this.mAppProp
                .getVersionString());

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
            SGFigure f = (SGFigure) fList.get(ii);

            // get figure elements
            SGIFigureElement[] elArray = f.getIFigureElementArray();
            SGIFigureElementGraph gElement = f.getGraphElement();

            // get focused data objects
            List<SGData> dataList = gElement.getFocusedDataList();

            List<SGData> dListOriginal = new ArrayList<SGData>();
            List<SGData> dListDuplicated = new ArrayList<SGData>();
            for (int jj = 0; jj < dataList.size(); jj++) {
                SGData data = (SGData) dataList.get(jj);

                // get properties of data before duplication
//                String dataType = data.getDataType();
                String name = gElement.getDataName(data);
                SGDataColumnInfo[] cols = gElement.getDataColumnInfoArray(data);
                Map<String, Object> infoMap = gElement.getInfoMap(data);
                infoMap.put(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE,
                		new SGTuple2f(f.getFigureWidth(), f.getFigureHeight()));
                SGDataColumnInfoSet colInfoSet = new SGDataColumnInfoSet(cols);

                // create a new name
                String nameNew = f.getNewDataName(name);

                // create a dialog and set data
                SGDataDuplicationDialog dg = null;
                if (SGDataUtility.isSDArrayData(data)) {
                	SGSDArrayData sData = (SGSDArrayData) data;
                    SGSDArrayDataDuplicationDialog adg = new SGSDArrayDataDuplicationDialog(
                            wnd, true);
                    if (adg.setData(nameNew, sData, colInfoSet, infoMap) == false) {
                        return false;
                    }
                    dg = adg;
                } else if (SGDataUtility.isNetCDFData(data)) {
                    SGNetCDFData nData = (SGNetCDFData) data;
                    SGNetCDFDataDuplicationDialog ndg = new SGNetCDFDataDuplicationDialog(
                            wnd, true);
                    if (ndg.setData(nameNew, nData, colInfoSet, infoMap) == false) {
                        return false;
                    }
                    dg = ndg;
                } else if (SGDataUtility.isMDArrayData(data)) {
                    SGMDArrayData mdData = (SGMDArrayData) data;
                    SGMDArrayDataDuplicationDialog mdg = new SGMDArrayDataDuplicationDialog(
                            wnd, true);
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
                    if (SGDataUtility.isArrayData(dCopy)) {
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
                            	SGNetCDFPickUpDimensionInfo info = new SGNetCDFPickUpDimensionInfo(
                            			pickUpName, pickUpIndices);
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
                            	SGMDArrayPickUpDimensionInfo info = new SGMDArrayPickUpDimensionInfo(
                            			pickUpDimMap, pickUpIndices);
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
     *
     * @return
     */
    boolean duplicateFocusedFigures(SGDrawingWindow wnd) {

        DOMImplementation domImpl = SGApplicationUtility.getDOMImplementation();
        if (domImpl == null) {
            return false;
        }

        // create a Document object
        Document document = domImpl.createDocument("",
                TAG_NAME_FOCUSED_FIGURES, null);

        // get the root element
        Element property = document.getDocumentElement();
        property.setAttribute(KEY_VERSION_NUMBER, this.mAppProp.getVersionString());

        // create a DOM tree
        if (wnd.createDOMTree(document,
                SGIRootObjectConstants.FOCUSED_FIGURES_FOR_DUPLICATION,
                new SGExportParameter(OPERATION.DUPLICATE_OBJECT)) == false) {
            return false;
        }

        // create an array of wrapped data objects
        List<WrappedData> wdList = new ArrayList<WrappedData>();
        List<SGFigure> fList = wnd.getFocusedFigureList();
        for (int ii = 0; ii < fList.size(); ii++) {
            SGFigure figure = fList.get(ii);
            List<SGData> dList = new ArrayList<SGData>(figure.getVisibleDataList());
            for (int jj = 0; jj < dList.size(); jj++) {
                SGData data = (SGData) dList.get(jj);
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
        NodeList wList = root
                .getElementsByTagName(SGIRootObjectConstants.TAG_NAME_WINDOW);
        if (wList.getLength() == 0) {
            return false;
        }
        Element elWnd = (Element) wList.item(0);

        int before = wnd.getFigureList().size();

        final int ret = this.createFiguresFromPropertyFile(elWnd, wnd, wDataArray,
                true, versionNumber, LOAD_PROPERTIES_IN_DUPLICATION);
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

        } else if (ret == JOptionPane.NO_OPTION
                || ret == JOptionPane.CLOSED_OPTION) {
            // canceled and there is nothing to do
            return CANCEL_OPTION;
        } else if (ret == JOptionPane.CANCEL_OPTION) {
            // save the properties
            final int retSave = this.mPropertyFileManager
                    .savePropertiesByDialog(wnd);
            return retSave;
        }

        return OK_OPTION;
    }

    static final String MSG_SAVE = "Save";

    // show the confirmation dialog for saving properties of the window
    int confirmBeforeClosing(final SGDrawingWindow wnd) {
        final Object[] options = { MSG_CLOSE_WITHOUT_SAVING,
                SGDialog.CANCEL_BUTTON_TEXT, MSG_SAVE };
        return this.showConfirmationDialog(wnd, options,
                new SGCloseWindowConfirmPanel());
    }

    // show the confirmation dialog for saving properties of the window
    int confirmBeforeDiscard(final SGDrawingWindow wnd) {
        final Object[] options = {
        // MSG_DISCARD_WITHOUT_SAVING,
                MSG_CLOSE_WITHOUT_SAVING, SGDialog.CANCEL_BUTTON_TEXT, MSG_SAVE };
        return this.showConfirmationDialog(wnd, options,
                new SGCloseWindowConfirmPanel());
    }

    /**
     * Shows the confirmation dialog.
     */
    private int showConfirmationDialog(Component component, Object[] options,
            Object message) {
        // beep
        Toolkit.getDefaultToolkit().beep();

        // show a dialog
        final int ret = JOptionPane.showOptionDialog(component, message,
                SGIConstants.TITLE_CONFIRMATION, JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[2]);

        return ret;
    }

    /**
     *
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
            JOptionPane.showMessageDialog(owner,
                    "Failed to get log information.");
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

    private boolean makeTransitionForDragAndDrop(final ActionEvent e) {
        Object source = e.getSource();
        SGWizardDialog dg = (SGWizardDialog) source;
        String command = e.getActionCommand();

        // cancel or previous
        if (command.equals(SGDialog.CANCEL_BUTTON_TEXT)) {
            dg.setVisible(false);
            this.clearTemporaryData();
        } else if (command.equals(SGDialog.PREVIOUS_BUTTON_TEXT)) {
            dg.showPrevious();
        } else if (command.equals(SGDialog.NEXT_BUTTON_TEXT)) {
        	if (dg.equals(this.mFileTypeSelectionWizardDialog)) {
        		SGDrawingWindow wnd = dg.getOwnerWindow();
        		FILE_TYPE fileType = this.mFileTypeSelectionWizardDialog.getSelectedFileType();
        		
            	// setup the wizard dialogs
            	this.mDataTypeWizardDialog.setDataFileType(fileType);
            	this.mDataTypeWizardDialog.setNext(dg);
            	this.setupDataAdditionWizardDialogConnection(
            			this.mDataTypeWizardDialog, DATA_ADDITION_DRAG_AND_DROP, fileType, true);

                if (this.toNetCDFOrMDArrayDataTypeDialog(wnd,
                        this.mDataTypeWizardDialog,
                        this.mDroppedDataFile.file) == false) {
                    return false;
                }

                // set the location of wizard dialog
                this.mDataTypeWizardDialog.setCenter(wnd);

        		dg.showNext();
        	} else if (dg.equals(this.mDataTypeWizardDialog)) {
        		FILE_TYPE dataFileType = this.mDataTypeWizardDialog.getDataFileType();
                if (FILE_TYPE.TXT_DATA.equals(dataFileType)) {
                	String path = this.mDroppedDataFile.file.getPath();
                    if (this.makeTransition(
    						this.mDataTypeWizardDialog,
    						this.mSDArrayDataSetupWizardDialog,
    						path, this.mDroppedDataFile.figureID,
    						this.mDroppedDataFile.pos) == false) {
                        return false;
                    }
                } else if (FILE_TYPE.NETCDF_DATA.equals(dataFileType)) {
                    if (this.makeTransition(
                            this.mDataTypeWizardDialog,
                            this.mNetCDFDataSetupWizardDialog,
                            this.mDroppedDataFile.file.getPath(),
                            this.mDroppedDataFile.figureID,
    						this.mDroppedDataFile.pos) == false) {
                        return false;
                    }
                } else if (FILE_TYPE.HDF5_DATA.equals(dataFileType)
                		|| FILE_TYPE.MATLAB_DATA.equals(dataFileType)) {
                	String path = this.mDroppedDataFile.file.getPath();
                    String dataName = SGUtility.createDataNameBase(path);
                    if (this.makeTransition(
                            this.mDataTypeWizardDialog,
                            this.mMDArrayDataSetupWizardDialog,
                            path,
                            this.mDroppedDataFile.figureID,
                            this.mDroppedDataFile.pos,
                            dataFileType,
                            dataName,
                            true) == false) {
                        return false;
                    }
                }
        	} else if (dg instanceof SGDataSetupWizardDialog) {
                String dataType = this.mDataTypeWizardDialog.getSelectedDataType();
                FILE_TYPE dataFileType = this.mDataTypeWizardDialog.getDataFileType();
                setupPlotTypeSelectionWizardDialogConnection(dataFileType, dataType);
                dg.showNext();
        	}

        } else if (command.equals(SGDialog.OK_BUTTON_TEXT)) {
            try {
                if (this.addDataByDragAndDropOK(dg) == false) {
                    return false;
                }
            } finally {
                this.clearTemporaryData();
            }
        }

        return true;
    }
    
    private String getDataTypeButtonName(String dataType) {
        // selects the button
    	String btnName = null;
    	if (SGDataUtility.isSXYTypeSingleData(dataType)) {
    		btnName = SGDataTypeWizardDialog.SINGLE_SXY;
    	} else if (SGDataUtility.isSXYTypeMultipleData(dataType)) {
    		btnName = SGDataTypeWizardDialog.MULTIPLE_SXY;
    	} else if (SGDataUtility.isSXYZTypeData(dataType)) {
    		btnName = SGDataTypeWizardDialog.PSEUDOCOLOR_MAP;
    	} else if (SGDataUtility.isVXYTypeData(dataType)) {
    		SGDataBuffer buffer = this.mVirtualMDArrayData.buffer;
    		boolean polar;
    		if (buffer instanceof SGVXYDataBuffer) {
        		SGVXYDataBuffer vxyBuffer = (SGVXYDataBuffer) buffer;
        		polar = vxyBuffer.isPolar();
    		} else if (buffer instanceof SGVXYGridDataBuffer) {
        		SGVXYGridDataBuffer vxyBuffer = (SGVXYGridDataBuffer) buffer;
        		polar = vxyBuffer.isPolar();
    		} else {
                throw new IllegalArgumentException("Invalid data buffer.");
    		}
    		if (polar) {
        		btnName = SGDataTypeWizardDialog.POLAR_VXY;
    		} else {
        		btnName = SGDataTypeWizardDialog.ORTHOGONAL_VXY;
    		}
    	} else {
            throw new IllegalArgumentException("Invalid data type: " + dataType);
    	}
    	return btnName;
    }

    private boolean makeTransitionForPlugin(final ActionEvent e) {
        Object source = e.getSource();
        SGWizardDialog dg = (SGWizardDialog) source;
        String command = e.getActionCommand();

        // cancel or previous
        if (command.equals(SGDialog.CANCEL_BUTTON_TEXT)) {
            dg.setVisible(false);
            this.clearTemporaryData();
        } else if (command.equals(SGDialog.PREVIOUS_BUTTON_TEXT)) {
            dg.showPrevious();
        } else if (command.equals(SGDialog.NEXT_BUTTON_TEXT)) {
        	if (dg.equals(this.mFigureIDSelectionWizardDialog)) {
            	this.setupDataAdditionWizardDialogConnection(
            			this.mDataTypeWizardDialog, DATA_ADDITION_VIRTUAL, FILE_TYPE.VIRTUAL_DATA, false);
            	
                // selects the button
            	if (this.mVirtualMDArrayData.dataType != null) {
                	String btnName = this.getDataTypeButtonName(this.mVirtualMDArrayData.dataType);
                	this.mDataTypeWizardDialog.setSelected(btnName);
            	}

                this.mDataTypeWizardDialog.setDataName(
                		this.mFigureIDSelectionWizardDialog.getDataName());

        		dg.showNext();
        	} else if (dg.equals(this.mDataTypeWizardDialog)) {
        		String selectedBtnName = this.mDataTypeWizardDialog.getSelected();
        		final boolean showDefault;
        		if (this.mVirtualMDArrayData.dataType != null) {
            		String defaultBtnName = this.getDataTypeButtonName(this.mVirtualMDArrayData.dataType);
            		showDefault = !selectedBtnName.equals(defaultBtnName);
        		} else {
        			showDefault = true;
        		}
                if (this.makeTransition(
                        this.mDataTypeWizardDialog,
                        this.mMDArrayDataSetupWizardDialog,
                        null,
                        this.mFigureIDSelectionWizardDialog.getFigureID(),
                        null,
                        FILE_TYPE.VIRTUAL_DATA,
                        this.mDataTypeWizardDialog.getDataName(),
                        showDefault) == false) {
                    return false;
                }
            } else if (dg.equals(this.mMDArrayDataSetupWizardDialog)) {
                String dataType = this.mDataTypeWizardDialog.getSelectedDataType();
                this.mPlotTypeSelectionWizardDialog.setDataName(
                		this.mMDArrayDataSetupWizardDialog.getDataName());
                setupPlotTypeSelectionWizardDialogConnection(FILE_TYPE.VIRTUAL_DATA, dataType);
                dg.showNext();
            }
        } else if (command.equals(SGDialog.OK_BUTTON_TEXT)) {
            try {
                if (this.addDataByPlugin(dg) == false) {
                    return false;
                }
            } finally {
                this.clearTemporaryData();
            }
        }
        return true;
    }

    /**
     * Add data from a dropped file.
     *
     * @param dg
     *            an event source
     * @return true if succeeded
     */
    private boolean addDataByDragAndDropOK(SGWizardDialog dg) {

        SGDrawingWindow wnd = dg.getOwnerWindow();
        String path = this.mDroppedDataFile.file.getAbsolutePath();

        // the location of a figure if it is created
        Point figureLocation;

        Object com;
        if (this.mDroppedDataFile.pos != null) {
            com = wnd.getComponent(this.mDroppedDataFile.pos.x, this.mDroppedDataFile.pos.y);
        } else {
            com = wnd;
        }

        // get the ID of figure
        int figureID;
        if (com instanceof SGDrawingWindow) {
            // get current figure ID
            figureID = wnd.assignFigureId();

            // set the dropped point to the new figure location
            figureLocation = this.mDroppedDataFile.pos;
        } else {
            SGFigure figure = (SGFigure) com;
            figureID = figure.getID();
            figureLocation = null;
        }
        
        FILE_TYPE dataFileType = null;
        if (dg.equals(this.mDataTypeWizardDialog)) {
            dataFileType = this.mDataTypeWizardDialog.getDataFileType();
        }

        if (dg.equals(this.mSDArrayDataSetupWizardDialog)
        		|| dataFileType == FILE_TYPE.TXT_DATA) {
            // text data
            if (this.drawNewGraphOfSDArrayData(wnd, dg, path, figureID, figureLocation) == false) {
                return false;
            }
        } else if (dg.equals(this.mNetCDFDataSetupWizardDialog)
        		|| dataFileType == FILE_TYPE.NETCDF_DATA) {
            // netCDF data
            if (this.drawNewGraphOfNetcdfData(wnd, dg, path, figureID, figureLocation) == false) {
                return false;
            }
        } else if (dg.equals(this.mMDArrayDataSetupWizardDialog)
        		|| dataFileType == FILE_TYPE.HDF5_DATA
        		|| dataFileType == FILE_TYPE.MATLAB_DATA) {
            if (this.drawNewGraphOfMDArrayData(wnd, dg, this.mDataTypeWizardDialog,
            		this.mMDArrayDataSetupWizardDialog, path, figureID, figureLocation) == false) {
                return false;
            }
        } else if (dg.equals(this.mPlotTypeSelectionWizardDialog)) {
            // plot type selection (text, netCDF or multidimensional data)
        	SGWizardDialog prev = dg.getPrevious();
            if (this.mSDArrayDataSetupWizardDialog.equals(prev)) {
                if (this.drawNewGraphOfSDArrayData(wnd, dg, path, figureID, figureLocation) == false) {
                    return false;
                }
            } else if (this.mNetCDFDataSetupWizardDialog.equals(prev)) {
                if (this.drawNewGraphOfNetcdfData(wnd, dg, path, figureID, figureLocation) == false) {
                    return false;
                }
            } else if (this.mMDArrayDataSetupWizardDialog.equals(prev)) {
                if (this.drawNewGraphOfMDArrayData(wnd, dg, this.mDataTypeWizardDialog,
                		this.mMDArrayDataSetupWizardDialog, path, figureID, figureLocation) == false) {
                    return false;
                }
            }
        }

        wnd.notifyToRoot();

        return true;
    }

    private boolean drawNewGraphOfSDArrayData(
            SGDrawingWindow wnd, SGWizardDialog dg, String path, int figureID, Point figureLocation) {

        // get data type
        String dataType = this.mDataTypeWizardDialog.getSelectedDataType();
        if (dataType == null) {
            SGUtility.showErrorMessageDialog(wnd, ERRMSG_TO_GET_DATA_TYPE, SGIConstants.TITLE_ERROR);
            return false;
        }

        SGSDArrayDataSetupWizardDialog setupDialog = this.mSDArrayDataSetupWizardDialog;

        // get information
        Map<String, Object> infoMap =
            createInfoMap(dataType, this.mDataTypeWizardDialog, figureID, figureLocation);

        // get selected column types
        boolean strideAvailable = false;
        SGDataColumnInfoSet colInfoSet = null;
        if (dg.equals(this.mDataTypeWizardDialog)) {
            colInfoSet = this.getSDArrayDefaultDataColumnInfo(path, dataType, infoMap, false, null);
            if (colInfoSet != null) {
                // calculate the stride
                SGDataColumnInfo[] colArray = colInfoSet.getDataColumnInfoArray();
                Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility.calcSDArrayDefaultStride(
                		colArray, infoMap);
                strideMap.put(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE,
                		strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE));
                infoMap.putAll(strideMap);

                final boolean defaultStrideAvailable = (Boolean) infoMap.get(
                		SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE);
                strideAvailable = this.confirmStrideAvailable(strideMap, wnd, defaultStrideAvailable);

                String dataNameBase = SGUtility.createDataNameBase(path);
                infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataNameBase);
            }
        } else if (dg.equals(setupDialog)) {
            colInfoSet = setupDialog.getDataColumnInfoSet();
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, setupDialog.getDataName());
            infoMap.putAll(setupDialog.getStrideMap());
            strideAvailable = setupDialog.isStrideAvailable();
        } else if (dg.equals(this.mPlotTypeSelectionWizardDialog)) {
            colInfoSet = setupDialog.getDataColumnInfoSet();
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, setupDialog.getDataName());
            infoMap.putAll(setupDialog.getStrideMap());
            strideAvailable = setupDialog.isStrideAvailable();
            addPlotTypeSelectionValuesToInfoMap(infoMap, this.mPlotTypeSelectionWizardDialog);
        }
        infoMap.put(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE, strideAvailable);
        
        if (colInfoSet == null) {
            SGUtility.showErrorMessageDialog(wnd, ERRMSG_TO_DRAW_GRAPH, SGIConstants.TITLE_ERROR);
            return false;
        }

        // draw the graph
        // open the file
        DataSourceInfo dataSource = new DataSourceInfo(path);
		SGStatus status = this.drawGraph(wnd, figureID, colInfoSet, infoMap,
				dataSource, null, true, figureLocation);
		if (status.isSucceeded() == false) {
			String msg = status.getMessage();
			if (msg == null) {
				msg = ERRMSG_DATA_ADDITION;
			}
			SGUtility.showErrorMessageDialog(wnd, msg, SGIConstants.TITLE_ERROR);
			return false;
		}

        return true;
    }

    private boolean drawNewGraphOfNetcdfData(
            SGDrawingWindow wnd, SGWizardDialog dg, String path, int figureID, Point figureLocation) {

        // get data type
        String dataType = this.mDataTypeWizardDialog.getSelectedDataType();
        if (dataType == null) {
            SGUtility.showErrorMessageDialog(wnd, ERRMSG_TO_GET_DATA_TYPE, SGIConstants.TITLE_ERROR);
            return false;
        }

        // create a information map
        Map<String, Object> infoMap =
            createInfoMap(dataType, this.mDataTypeWizardDialog, figureID, figureLocation);

        // open the file
        SGNetCDFFile nc = this.getNetCDFFile(path);
		if (nc == null) {
			return false;
		}
        DataSourceInfo dataSource = new DataSourceInfo(nc);

		// put the netCDF file
		infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, nc);

		// get selected column types
        SGDataColumnInfoSet colInfoSet = null;
        boolean strideAvailable = false;
		if (dg.equals(this.mDataTypeWizardDialog)) {
			colInfoSet = this.getNetCDFDefaultDataColumnInfo(nc, dataType,
					infoMap);
			if (colInfoSet == null) {
				SGUtility.showErrorMessageDialog(wnd, MSG_INVALID_DATA_FILE,
						SGIConstants.TITLE_ERROR);
				return false;
			}

			// calculate the stride
			SGDataColumnInfo[] colArray = colInfoSet.getDataColumnInfoArray();
			Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility
					.calcNetCDFDefaultStride(colArray, infoMap);
			strideMap.put(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE,
					strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE));
			infoMap.putAll(strideMap);

			final boolean defaultStrideAvailable = (Boolean) infoMap
					.get(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE);
			strideAvailable = this.confirmStrideAvailable(strideMap, wnd,
					defaultStrideAvailable);

			String dataNameBase = SGUtility.createDataNameBase(path);
			infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataNameBase);
		} else if (dg.equals(this.mNetCDFDataSetupWizardDialog)) {
			colInfoSet = this.mNetCDFDataSetupWizardDialog.getDataColumnInfoSet();
			infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME,
					this.mNetCDFDataSetupWizardDialog.getDataName());
			infoMap.putAll(this.mNetCDFDataSetupWizardDialog.getStrideMap());
			addDimensionValuesToInfoMap(infoMap, this.mNetCDFDataSetupWizardDialog);
			strideAvailable = this.mNetCDFDataSetupWizardDialog.isStrideAvailable();
		} else if (dg.equals(this.mPlotTypeSelectionWizardDialog)) {
			colInfoSet = this.mNetCDFDataSetupWizardDialog.getDataColumnInfoSet();
			infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME,
					this.mNetCDFDataSetupWizardDialog.getDataName());
			infoMap.putAll(this.mNetCDFDataSetupWizardDialog.getStrideMap());
			addDimensionValuesToInfoMap(infoMap, this.mNetCDFDataSetupWizardDialog);
			addPlotTypeSelectionValuesToInfoMap(infoMap, this.mPlotTypeSelectionWizardDialog);
			strideAvailable = this.mNetCDFDataSetupWizardDialog.isStrideAvailable();
		}

        infoMap.put(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE, strideAvailable);

        if (colInfoSet == null) {
            SGUtility.showErrorMessageDialog(wnd, MSG_INVALID_DATA_FILE, SGIConstants.TITLE_ERROR);
            return false;
        }

        if (dg.equals(this.mDataTypeWizardDialog)) {
            // set default value of dimension origin and step
            if (this.setupNetCDFDefaultDimensionValues(dataType, infoMap, this.mNetCDFDataSetupWizardDialog) == false) {
                return false;
            }
        } else if (dg.equals(this.mNetCDFDataSetupWizardDialog) ||
                dg.equals(this.mPlotTypeSelectionWizardDialog)) {
            // set dimension origin and step
            if (addDimensionValuesToInfoMap(infoMap, this.mNetCDFDataSetupWizardDialog) == false) {
                return false;
            }
        }

        // draw the graph
		SGStatus status = this.drawGraph(wnd, figureID, colInfoSet, infoMap,
				dataSource, null, false, figureLocation);
		if (status.isSucceeded() == false) {
			String msg = status.getMessage();
			if (msg == null) {
				msg = ERRMSG_DATA_ADDITION;
			}
			SGUtility.showErrorMessageDialog(wnd, msg, SGIConstants.TITLE_ERROR);
			return false;
		}

        // puts stride flag
        putDataStrideAvailable(strideAvailable);

        return true;
    }
    
    private boolean confirmStrideAvailable(Map<String, SGIntegerSeriesSet> strideMap, SGDrawingWindow wnd,
    		final boolean defaultValue) {
    	boolean strideAvailable = defaultValue;
		Iterator<Entry<String, SGIntegerSeriesSet>> itr = strideMap.entrySet().iterator();
		List<String> strideKeyList = new ArrayList<String>();
		while (itr.hasNext()) {
			Entry<String, SGIntegerSeriesSet> entry = itr.next();
			String key = entry.getKey();
			SGIntegerSeriesSet stride = entry.getValue();
			if (stride != null) {
				if (!"0:end".equals(stride.toString())) {
					strideKeyList.add(key);
				}
			}
		}
		if (strideKeyList.size() > 0) {
			final String message = "Data plot might be too slow due to its size.\nCan you use auto assigned array section?";
	        final int ret = SGUtility.showYesNoConfirmationDialog(wnd, message);
			if (ret == JOptionPane.YES_OPTION) {
				strideAvailable = true;
			}
		}
		return strideAvailable;
    }

    private boolean drawNewGraphOfMDArrayData(
            SGDrawingWindow wnd, SGWizardDialog dg,
            SGDataTypeWizardDialog dataTypeDialog, SGMDArrayDataSetupWizardDialog setupDialog,
            String path, int figureID, Point figureLocation) {

        // get data type
        String dataType = null;
        if (dg.equals(this.mFigureIDSelectionWizardDialog)) {
            if (this.mVirtualMDArrayData != null) {
            	dataType = this.mVirtualMDArrayData.dataType;
            } else {
                SGUtility.showErrorMessageDialog(wnd, ERRMSG_TO_GET_DATA_TYPE, SGIConstants.TITLE_ERROR);
                return false;
            }
        } else {
            dataType = dataTypeDialog.getSelectedDataType();
            if (dataType == null) {
                SGUtility.showErrorMessageDialog(wnd, ERRMSG_TO_GET_DATA_TYPE, SGIConstants.TITLE_ERROR);
                return false;
            }
        }

        // create a information map
        Map<String, Object> infoMap = createInfoMap(dataType, dataTypeDialog, figureID, figureLocation);

        SGDataColumnInfoSet colInfoSet = null;
        SGMDArrayFile file = null;
        DataSourceInfo dataSource = null;
        if (path != null) {
        	FILE_TYPE type = SGApplicationUtility.identifyDataFileType(path);
            if (FILE_TYPE.HDF5_DATA.equals(type)) {
                file = this.getHDF5File(path);
                if (file == null) {
                	return false;
                }
            } else {
        		file = this.getMATLABFile(path);
        		if (file == null) {
        			return false;
        		}
            }
        } else {
        	file = this.mVirtualMDArrayData.file;
        	SGDataBuffer buffer = this.mVirtualMDArrayData.buffer;
        	if (buffer != null) {
            	String gridTypeKey = buffer.getGridTypeKey();
            	if (gridTypeKey != null) {
                	infoMap.put(gridTypeKey, buffer.isGridType());
            	}
        	}
        }
    	dataSource = new DataSourceInfo(file);

        infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);

        // get selected column types
        boolean strideAvailable = false;
        if (dg.equals(dataTypeDialog) || dg.equals(this.mFigureIDSelectionWizardDialog)) {
            if (this.mVirtualMDArrayData != null) {
            	if (this.mVirtualMDArrayData.infoMap != null) {
                	infoMap.putAll(this.mVirtualMDArrayData.infoMap);
            	}
            	colInfoSet = this.mVirtualMDArrayData.colInfoSet;
            	if (colInfoSet == null) {
                    colInfoSet = this.getMDArrayDataDefaultDataColumnInfo(file, dataType, infoMap);
            	}
            } else {
                colInfoSet = this.getMDArrayDataDefaultDataColumnInfo(file, dataType, infoMap);
            }
            if (colInfoSet == null) {
                SGUtility.showErrorMessageDialog(wnd, MSG_INVALID_DATA_FILE, SGIConstants.TITLE_ERROR);
                return false;
            }
            
            // calculate the stride
            SGDataColumnInfo[] colArray = colInfoSet.getDataColumnInfoArray();
            Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility.calcMDArrayDefaultStride(
            		colArray, infoMap);
            strideMap.put(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE,
            		strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE));
            infoMap.putAll(strideMap);

            final boolean defaultStrideAvailable = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE);
            strideAvailable = this.confirmStrideAvailable(strideMap, wnd, defaultStrideAvailable);

            String dataNameBase;
            if (this.mVirtualMDArrayData != null) {
            	dataNameBase = this.mVirtualMDArrayData.name;
            } else {
                dataNameBase = SGUtility.createDataNameBase(path);
            }
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataNameBase);
        } else if (dg.equals(setupDialog)) {
            colInfoSet = setupDialog.getDataColumnInfoSet();
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, setupDialog.getDataName());
            infoMap.putAll(setupDialog.getStrideMap());
            addDimensionValuesToInfoMap(infoMap, setupDialog);
            strideAvailable = setupDialog.isStrideAvailable();
        } else if (dg.equals(this.mPlotTypeSelectionWizardDialog)) {
            colInfoSet = setupDialog.getDataColumnInfoSet();
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, setupDialog.getDataName());
            infoMap.putAll(setupDialog.getStrideMap());
            addDimensionValuesToInfoMap(infoMap, setupDialog);
            strideAvailable = setupDialog.isStrideAvailable();
            addPlotTypeSelectionValuesToInfoMap(infoMap, this.mPlotTypeSelectionWizardDialog);
        }
        infoMap.put(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE, strideAvailable);

        if (colInfoSet == null) {
            SGUtility.showErrorMessageDialog(wnd, MSG_INVALID_DATA_FILE, SGIConstants.TITLE_ERROR);
            return false;
        }

        if (dg.equals(this.mDataTypeWizardDialog)) {
        	SGMDArrayDataSetupWizardDialog dataSetupDialog;
        	boolean showDefault;
        	FILE_TYPE dataFileType = this.mDataTypeWizardDialog.getDataFileType();
        	if (FILE_TYPE.HDF5_DATA.equals(dataFileType)
        			|| FILE_TYPE.MATLAB_DATA.equals(dataFileType)
        			|| FILE_TYPE.VIRTUAL_DATA.equals(dataFileType)) {
        		dataSetupDialog = this.mMDArrayDataSetupWizardDialog;
        		if (FILE_TYPE.VIRTUAL_DATA.equals(dataFileType)) {
        			if (this.mVirtualMDArrayData.colInfoSet != null) {
        				// from the plug-in
            			showDefault = false;
        			} else {
        				// from the data viewer
            			showDefault = true;
        			}
        		} else {
        			showDefault = true;
        		}
        	} else {
        		return false;
        	}
        	
            // set default value of dimension origin and step
            if (this.setupMDArrayDefaultDimensionValues(dataType, infoMap, dataSetupDialog, 
            		showDefault) == false) {
                return false;
            }
        } else if (dg.equals(this.mMDArrayDataSetupWizardDialog)) {
            // set dimension origin and step
            if (addDimensionValuesToInfoMap(infoMap, (SGMDArrayDataSetupWizardDialog) dg) == false) {
                return false;
            }
        } else if (dg.equals(this.mPlotTypeSelectionWizardDialog)) {
        	SGWizardDialog prev = this.mPlotTypeSelectionWizardDialog.getPrevious();
            // set dimension origin and step
            if (addDimensionValuesToInfoMap(infoMap, (SGMDArrayDataSetupWizardDialog) prev) == false) {
                return false;
            }
        }

        // draw the graph
		SGStatus status = this.drawGraph(wnd, figureID, colInfoSet, infoMap,
				dataSource, null, false, figureLocation);
		if (status.isSucceeded() == false) {
			String msg = status.getMessage();
			if (msg == null) {
				msg = ERRMSG_DATA_ADDITION;
			}
			SGUtility.showErrorMessageDialog(wnd, msg, SGIConstants.TITLE_ERROR);
			return false;
		}

        // puts stride flag
        putDataStrideAvailable(strideAvailable);

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

    private static final String ERRMSG_DATA_ADDITION =
        "Failed to add data.\n"
        + "Valid data was not obtained with input values.";

    private static final String ERRMSG_URL_OF_NETCDF = "Failed to add NetCDF file from input URL.";

    private boolean makeTransitionForToolBar(final ActionEvent e) {

        Object source = e.getSource();
        SGWizardDialog dg = (SGWizardDialog) source;
        String command = e.getActionCommand();

        // cancel or previous
        if (command.equals(SGDialog.CANCEL_BUTTON_TEXT)) {
            dg.setVisible(false);
            this.clearTemporaryData();
        } else if (command.equals(SGDialog.PREVIOUS_BUTTON_TEXT)) {
            dg.showPrevious();
        } else if (command.equals(SGDialog.NEXT_BUTTON_TEXT)) {
            if (dg.equals(this.mFigureIDSelectionWizardDialog)) {
            	this.mSingleDataFileChooserWizardDilaog.setCurrentFile(this.getCurrentFileDirectory(), null);
                dg.showNext();
            } else if (dg.equals(this.mSingleDataFileChooserWizardDilaog)) {
                if (this.mSingleDataFileChooserWizardDilaog.isLocalFileSelected()) {
                    String fileName = this.mSingleDataFileChooserWizardDilaog.getFileName();
                    File file = new File(fileName);
                    String filePath = file.getAbsolutePath();
                    FILE_TYPE fileType = SGApplicationUtility.identifyDataFileType(filePath);
                    if (FILE_TYPE.POSSIBLY_HDF5_DATA.equals(fileType)) {
                    	SGApplicationUtility.showHDF5ReadErrorMessageDialog(dg, filePath);
                    	return false;
                    }
                	this.mDataTypeWizardDialog.setDataFileType(fileType);
                	
                	final boolean isHDF5 = FILE_TYPE.HDF5_DATA.equals(fileType);
                    this.setupDataAdditionWizardDialogConnection(
                            this.mDataTypeWizardDialog, DATA_ADDITION_TOOL_BAR, fileType, isHDF5);

                    if (isHDF5) {
                    	// set up the dialog to select HDF5/NetCDF-4
                        String dataName = SGUtility.createDataNameBase(filePath);
                    	this.mFileTypeSelectionWizardDialog.setDataName(dataName);
						FILE_TYPE selectedFileType = this.getNetCDF4orHDF5FileType(file);
                    	this.mFileTypeSelectionWizardDialog.setSelectedFileType(selectedFileType);
                    } else {
                    	// for TXT, NetCDF-3 or MATLAB file
                        if (FILE_TYPE.TXT_DATA.equals(fileType)){
                            if (this.toSDArrayDataTypeDialog(this.mSingleDataFileChooserWizardDilaog,
                                    this.mDataTypeWizardDialog, file) == false) {
                                return false;
                            }
                        } else if (FILE_TYPE.NETCDF_DATA.equals(fileType)
                        		|| FILE_TYPE.MATLAB_DATA.equals(fileType)) {
                            if (this.toNetCDFOrMDArrayDataTypeDialog(this.mSingleDataFileChooserWizardDilaog,
                                    this.mDataTypeWizardDialog, file) == false) {
                                return false;
                            }
                        }
                    }

                    // set to the file chooser because file path is due to be taken from file chooser dialog
                    this.mSingleDataFileChooserWizardDilaog.setSelectedFile(file);

                } else {
                	this.mDataTypeWizardDialog.setDataFileType(FILE_TYPE.NETCDF_DATA);

                	// setup the next dialog
                    this.setupDataAdditionWizardDialogConnection(
                            this.mDataTypeWizardDialog, DATA_ADDITION_TOOL_BAR, FILE_TYPE.NETCDF_DATA, false);
                }

                // show the next dialog
                dg.showNext();
                
            } else if (dg.equals(this.mFileTypeSelectionWizardDialog)) {
            	
            	FILE_TYPE fileType = this.mFileTypeSelectionWizardDialog.getSelectedFileType();
            	if (FILE_TYPE.NETCDF_DATA.equals(fileType) || FILE_TYPE.HDF5_DATA.equals(fileType)) {
                    String fileName = this.mSingleDataFileChooserWizardDilaog.getFileName();
                    if (this.toNetCDFOrMDArrayDataTypeDialog(this.mSingleDataFileChooserWizardDilaog,
                            this.mDataTypeWizardDialog, new File(fileName)) == false) {
                        return false;
                    }
                } else {
                	return false;
                } 
            	this.mDataTypeWizardDialog.setDataFileType(fileType);

                // show the next dialog
                dg.showNext();

            } else if (dg.equals(this.mDataTypeWizardDialog)) {
            	
            	FILE_TYPE dataFileType = this.mDataTypeWizardDialog.getDataFileType();
            	if (FILE_TYPE.TXT_DATA.equals(dataFileType)) {
                	final int figureID = this.mFigureIDSelectionWizardDialog.getFigureID();
                    File f = this.mSingleDataFileChooserWizardDilaog.getSelectedFile();
                    String path = f.getPath();
                    if (this.makeTransition(
                            this.mDataTypeWizardDialog,
                            this.mSDArrayDataSetupWizardDialog, path, figureID, null) == false) {
                        return false;
                    }
            		
            	} else if (FILE_TYPE.NETCDF_DATA.equals(dataFileType)) {
                	final int figureID = this.mFigureIDSelectionWizardDialog.getFigureID();
                    String fileName = this.mSingleDataFileChooserWizardDilaog.getFileName();
                    if (this.mSingleDataFileChooserWizardDilaog.isLocalFileSelected()) {
                        if (this.makeTransition(
                                this.mDataTypeWizardDialog,
                                this.mNetCDFDataSetupWizardDialog,
                                fileName, figureID, null) == false) {
                            return false;
                        }
                    } else {
                        FILE_TYPE type = SGApplicationUtility.identifyDataFileType(fileName);
                        if (FILE_TYPE.NETCDF_DATA.equals(type)) {
                            if (this.makeTransition(
                                    this.mDataTypeWizardDialog,
                                    this.mNetCDFDataSetupWizardDialog,
                                    fileName, figureID, null) == false) {
                                return false;
                            }
                        } else {
                        	SGUtility.showErrorMessageDialog(dg, ERRMSG_URL_OF_NETCDF, SGIConstants.TITLE_ERROR);
                            this.mDataTypeWizardDialog.setVisible(false);
                            this.mSingleDataFileChooserWizardDilaog.setVisible(true);
                        }
                    }
            	} else if (FILE_TYPE.HDF5_DATA.equals(dataFileType)
            			|| FILE_TYPE.MATLAB_DATA.equals(dataFileType)) {
                	final int figureID = this.mFigureIDSelectionWizardDialog.getFigureID();
                    String path = this.mSingleDataFileChooserWizardDilaog.getFileName();
                    String dataName = SGUtility.createDataNameBase(path);
                    if (this.makeTransition(
                            this.mDataTypeWizardDialog,
                            this.mMDArrayDataSetupWizardDialog,
                            path, figureID, null, dataFileType, dataName, true) == false) {
                        return false;
                    }
            	}

        	} else if (dg instanceof SGDataSetupWizardDialog) {
                String dataType = this.mDataTypeWizardDialog.getSelectedDataType();
                FILE_TYPE dataFileType = this.mDataTypeWizardDialog.getDataFileType();
                setupPlotTypeSelectionWizardDialogConnection(dataFileType, dataType);
                dg.showNext();
            }

        } else if (command.equals(SGDialog.OK_BUTTON_TEXT)) {

            try {
                if (this.addDataByToolBar(dg) == false) {
                    return false;
                }
            } finally {
                this.clearTemporaryData();
            }
        }

        return true;
    }

    /**
     * Do add data to figure.
     *
     * @param dg wizard dialog which is source on doing addition.
     * @return
     */
    private boolean addDataByToolBar(SGWizardDialog dg) {

        // set invisible the dialog
        dg.setVisible(false);

        SGDrawingWindow wnd = dg.getOwnerWindow();

        // figure id
        final int figureID = this.mFigureIDSelectionWizardDialog.getFigureID();

        // data path
        String filename = this.mSingleDataFileChooserWizardDilaog.getFileName();
        String path = filename;
        if (this.mSingleDataFileChooserWizardDilaog.isLocalFileSelected()) {
            // path of the local data file
            File f = this.mSingleDataFileChooserWizardDilaog.getSelectedFile();
            path = f.getPath();
        }

        FILE_TYPE dataFileType = this.mDataTypeWizardDialog.getDataFileType();
        
        if (dg.equals(this.mSDArrayDataSetupWizardDialog)
        		|| dataFileType == FILE_TYPE.TXT_DATA) {
            // text data
            if (this.drawNewGraphOfSDArrayData(wnd, dg, path, figureID, null) == false) {
                return false;
            }
        } else if (dg.equals(this.mNetCDFDataSetupWizardDialog)
        		|| dataFileType == FILE_TYPE.NETCDF_DATA) {
            // netCDF data
            if (this.drawNewGraphOfNetcdfData(wnd, dg, path, figureID, null) == false) {
                return false;
            }
        } else if (dg.equals(this.mMDArrayDataSetupWizardDialog)
        		|| dataFileType == FILE_TYPE.HDF5_DATA
        		|| dataFileType == FILE_TYPE.MATLAB_DATA) {
        	// MDArray data
            if (this.drawNewGraphOfMDArrayData(wnd, dg, this.mDataTypeWizardDialog,
            		this.mMDArrayDataSetupWizardDialog, path, figureID, null) == false) {
                return false;
            }
        } else if (dg.equals(this.mPlotTypeSelectionWizardDialog)) {
            if (dg.getPrevious().equals(this.mSDArrayDataSetupWizardDialog)) {
                if (this.drawNewGraphOfSDArrayData(wnd, dg, path, figureID, null) == false) {
                    return false;
                }
            } else {
                if (this.drawNewGraphOfNetcdfData(wnd, dg, path, figureID, null) == false) {
                    return false;
                }
            }
        }

        wnd.notifyToRoot();

        return true;
    }

    // Sets the default value of dimension origin and step.
    boolean setupNetCDFDefaultDimensionValues(
            final String dataType, final Map<String, Object> infoMap, final SGNetCDFDataSetupWizardDialog dg) {
        final boolean multipleVariable = true;
        infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.valueOf(multipleVariable));

        SGNetCDFFile ncSource = (SGNetCDFFile) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_SOURCE);
        NetcdfFile ncFile = null;
        try {
        	String path = ncSource.getNetcdfFile().getLocation();
            ncFile = SGApplicationUtility.openNetCDF(path);
            if (ncFile == null) {
            	return false;
            }
            SGNetCDFFile nc = new SGNetCDFFile(ncFile);
            if (this.setupNetCDFDataSetupDialog(nc, dataType, infoMap, this.mNetCDFDataSetupWizardDialog) == false) {
                return false;
            }
        } catch (IOException e1) {
            return false;
        } finally {
            try {
            	if (ncFile != null) {
                    ncFile.close();
            	}
            } catch (IOException e1) {
            }
        }

        SGIntegerSeriesSet indices = dg.getSXYPickUpIndices();
        infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, indices);

        return true;
    }

    // Sets the default value of dimension origin and step.
    boolean setupMDArrayDefaultDimensionValues(
            final String dataType, final Map<String, Object> infoMap, SGMDArrayDataSetupWizardDialog dg,
            final boolean showDefault) {
        final boolean multipleVariable = true;
        infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.valueOf(multipleVariable));

        SGMDArrayFile dataSource = (SGMDArrayFile) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_SOURCE);
    	SGMDArrayFile mdFile = null;
    	String path = dataSource.getPath();
    	SGDataColumnInfoSet colInfoSet = null;
    	if (path != null) {
        	mdFile = SGApplicationUtility.openMDArrayFile(dataType, path);
    	} else {
    		mdFile = this.mVirtualMDArrayData.file;
    		if (this.mVirtualMDArrayData.colInfoSet != null) {
        		colInfoSet = (SGDataColumnInfoSet) this.mVirtualMDArrayData.colInfoSet.clone();
    		}
    	}
        if (this.setupMDArrayDataSetupDialog(mdFile, dataType, infoMap, dg, colInfoSet, showDefault) == false) {
            return false;
        }

        SGIntegerSeriesSet indices = dg.getSXYPickUpIndices();
        infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, indices);

        return true;
    }

    /**
     * Process to data type selection dialog.
     *
     * @param owner
     *            the owner of dialogs
     * @param next
     *            a dialog for data type selection
     * @param f
     *            a file
     * @return true if succeeded
     */
    private boolean toSDArrayDataTypeDialog(Window owner,
    		SGDataTypeWizardDialog next, File f) {

        if (f.exists() == false) {
            SGUtility.showFileNotFoundMessageDialog(owner);
            return false;
        }

        String path = f.getPath();

        List<String> cList = new ArrayList<String>();
        try {
            if (this.mDataCreator.getDataTypeCandidateList(path, cList) == false) {
                SGApplicationUtility.showDataFileInvalidMessageDialog(owner);
                return false;
            }
        } catch (FileNotFoundException ex) {
            SGUtility.showFileNotFoundMessageDialog(owner);
            return false;
        }
        if (cList.size() == 0) {
            SGApplicationUtility.showDataFileInvalidMessageDialog(owner);
            return false;
        }

        List<String> typeList = new ArrayList<String>();
        for (int i = 0; i < cList.size(); i++) {
            typeList.add(SGApplicationUtility.getArrayDataType(cList.get(i)));
        }

        // set candidate data-type to the dialog
        if (next.setAvailableDataType(typeList) == false) {
            SGApplicationUtility.showDataFileInvalidMessageDialog(owner);
            return false;
        }

        String dataName = SGUtility.createDataNameBase(path);
        next.setDataName(dataName);

        return true;
    }

    /**
     * Process to data type selection dialog.
     *
     * @param owner
     *            the owner of dialogs
     * @param next
     *            a dialog for data type selection
     * @param f
     *            a file
     * @return true if succeeded
     */
    private boolean toNetCDFOrMDArrayDataTypeDialog(Window owner,
            SGDataTypeWizardDialog next, File f) {
        if (f.exists() == false) {
            SGUtility.showFileNotFoundMessageDialog(owner);
            return false;
        }
        
        next.setAllDataTypeButtonsEnabled(true);
        
        String path = f.getPath();
        String dataName = SGUtility.createDataNameBase(path);
        next.setDataName(dataName);
        
        return true;
    }

    /**
     * Makes the transition between wizard dialogs.
     *
     * @param prev
     *            previous dialog to select data type
     * @param next
     *            next dialog to select column
     * @param f
     *            a data file
     * @return true if succeeded
     */
    private boolean makeTransition(
    		SGDataTypeWizardDialog prev,
    		SGSDArrayDataSetupWizardDialog next, String path, int figureID, Point pos) {

        SGDrawingWindow wnd = prev.getOwnerWindow();

        // set invisible the dialog
        prev.setVisible(false);

        // get selected file type
        String dataType = prev.getSelectedDataType();
        if (dataType == null) {
            SGUtility.showErrorMessageDialog(wnd, ERRMSG_TO_GET_DATA_TYPE, SGIConstants.TITLE_ERROR);
            return false;
        }

        // create a map of information
        Map<String, Object> infoMap = createInfoMap(dataType, prev, figureID, pos);

        SGDataColumnInfoSet colInfoSet = this.getSDArrayDefaultDataColumnInfo(path,
        		dataType, infoMap, false, null);
        if (colInfoSet == null) {
            SGApplicationUtility.showDataFileInvalidMessageDialog(wnd);
            return false;
        }

        // create default data name
        String dataName = SGUtility.createDataNameBase(path);

        // put into the infoMap
        infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);

        SGSDArrayFile sdFile;
		try {
			sdFile = (SGSDArrayFile) SGApplicationUtility.createDataSource(path, colInfoSet, infoMap);
		} catch (FileNotFoundException e) {
			return false;
		}

        // set information to the dialog for data column selection
        next.setData(sdFile, dataType, colInfoSet, infoMap, false);

        // show the dialog to select data columns
        next.setCenter(wnd);
        next.setVisible(true);

        return true;
    }

    /**
     * Makes the transition between wizard dialogs of netCDF data.
     *
     * @param prev
     *            previous dialog to select data type
     * @param next
     *            next dialog to select column
     * @param path
     *            a data file path
     * @return true if succeeded
     */
    private boolean makeTransition(
    		SGDataTypeWizardDialog prev,
            SGNetCDFDataSetupWizardDialog next, String path, int figureID, Point pos) {

        SGDrawingWindow wnd = prev.getOwnerWindow();

        // set invisible the dialog
        prev.setVisible(false);

        // get selected file type
        String dataType = prev.getSelectedDataType();
        if (dataType == null) {
            SGUtility.showErrorMessageDialog(wnd, ERRMSG_TO_GET_DATA_TYPE, SGIConstants.TITLE_ERROR);
            return false;
        }

        // create a map of information
        Map<String, Object> infoMap = createInfoMap(dataType, prev, figureID, pos);

        // create default data name
        String dataName = SGUtility.createDataNameBase(path);

        // put into the infoMap
        infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);

        // open the file
        NetcdfFile ncFile = null;
        try {
            ncFile = SGApplicationUtility.openNetCDF(path);
        } catch (Exception e1) {
            SGApplicationUtility.showDataFileInvalidMessageDialog(wnd);
            return false;
        }
        if (ncFile == null) {
            SGApplicationUtility.showDataFileInvalidMessageDialog(wnd);
            return false;
        }
        SGNetCDFFile nc = new SGNetCDFFile(ncFile);
        try {
            if (this.setupNetCDFDataSetupDialog(nc, dataType, infoMap, next) == false) {
                return false;
            }
        } finally {
            try {
                ncFile.close();
            } catch (IOException e1) {
            	return false;
            }
        }

        // show the dialog to select data columns
        next.setCenter(wnd);
        next.setVisible(true);

        return true;
    }

    /**
     * Makes the transition between wizard dialogs of netCDF data.
     *
     * @param prev
     *            previous dialog to select data type
     * @param next
     *            next dialog to select column
     * @param path
     *            a data file path
     * @return true if succeeded
     */
    private boolean makeTransition(
    		SGDataTypeWizardDialog prev,
            SGMDArrayDataSetupWizardDialog next, String path, int figureID, Point pos, 
            final FILE_TYPE fileType, String dataName, final boolean showDefault) {

        SGDrawingWindow wnd = prev.getOwnerWindow();

        // set invisible the dialog
        prev.setVisible(false);

        // get selected file type
        String dataType = prev.getSelectedDataType();
        if (dataType == null) {
            SGUtility.showErrorMessageDialog(wnd, ERRMSG_TO_GET_DATA_TYPE, SGIConstants.TITLE_ERROR);
            return false;
        }

        // create a map of information
        Map<String, Object> infoMap = createInfoMap(dataType, prev, figureID, pos);

        // put into the infoMap
        infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);

        // open the file
        if (fileType == FILE_TYPE.HDF5_DATA) {
            IHDF5Reader reader = null;
            try {
    			reader = SGApplicationUtility.openHDF5(path);
            } catch (HDF5Exception e1) {
        		if (reader != null) {
        			reader.close();
        		}
                return false;
            }
            SGHDF5File hdf5File = new SGHDF5File(reader);
            try {
                if (this.setupMDArrayDataSetupDialog(hdf5File, dataType, infoMap, next, null, showDefault) == false) {
                    return false;
                }
            } finally {
            	reader.close();
            }
        } else if (fileType == FILE_TYPE.MATLAB_DATA) {
    		MatFileReader reader = null;
    		try {
    			reader = SGApplicationUtility.openMAT(path);
    		} catch (IOException e) {
    			return false;
    		}
        	SGMATLABFile matFile = new SGMATLABFile(path, reader);
            if (this.setupMDArrayDataSetupDialog(matFile, dataType, infoMap, next, null, showDefault) == false) {
                return false;
            }
        } else if (fileType == FILE_TYPE.VIRTUAL_DATA) {
        	SGDataColumnInfoSet colInfoSet = null;
        	if (this.mVirtualMDArrayData.colInfoSet != null) {
            	colInfoSet = (SGDataColumnInfoSet) this.mVirtualMDArrayData.colInfoSet.clone();
        	}
        	
        	// puts information for grid plot / scatter plot
        	if (this.mVirtualMDArrayData.buffer != null) {
            	final String key = this.mVirtualMDArrayData.buffer.getGridTypeKey();
            	if (key != null) {
            		infoMap.put(key, this.mVirtualMDArrayData.buffer.isGridType());
            	}
        	}
        	
            if (this.setupMDArrayDataSetupDialog(this.mVirtualMDArrayData.file, dataType, 
            		infoMap, next, colInfoSet, showDefault) == false) {
                return false;
            }
        } else {
        	throw new Error("Invalid file type: " + fileType);
        }

        // show the dialog to select data columns
        next.setCenter(wnd);
        next.setVisible(true);

        return true;
    }

    /**
     * Returns default data column information.
     *
     * @param pathName
     *                the path of data file
     * @param dataType
     *                the data type
     * @param infoMap
     *                the information map
     * @param isPropertyFileData
     *                true if the data is for the property file
     * @param versionNumber
     *                the version number of the property file
     * @return default the data column information
     */
    SGDataColumnInfoSet getSDArrayDefaultDataColumnInfo(String pathName, String dataType,
    		Map<String, Object> infoMap,
            final boolean isPropertyFileData, final String versionNumber) {

        // parse the data file
        FileColumn[] fileColInfo = null;
        int length = -1;
        try {
        	SDArrayFileParseResult result = this.mDataCreator.parseFileComlumnType(pathName, dataType,
            		isPropertyFileData, versionNumber);
        	if (result == null) {
        		return null;
        	}
        	fileColInfo = result.fileColumns;
        	length = result.length;
        } catch (FileNotFoundException e1) {
            return null;
        }

        if (fileColInfo == null) {
            return null;
        }

        // get column information: title and value type
        SGDataColumnInfo[] colInfoArray = new SGDataColumnInfo[fileColInfo.length];
        for (int ii = 0; ii < fileColInfo.length; ii++) {
            colInfoArray[ii] = new SGSDArrayDataColumnInfo(fileColInfo[ii].title,
            		fileColInfo[ii].valueType, length);
        }

        return this.createColumnInfoSet(dataType, infoMap, colInfoArray);
    }

    /**
     * Returns default data column information.
     *
     * @param file
     *                the data source
     * @param dataType
     *                the data type
     * @param infoMap
     *                the information map
     * @param isPropertyFileData
     *                true if the data is for the property file
     * @param versionNumber
     *                the version number of the property file
     * @return default the data column information
     */
    SGDataColumnInfoSet getDefaultDataColumnInfo(SGSDArrayFile file, String dataType,
    		Map<String, Object> infoMap) {

    	SGDataColumn[] columns = file.getDataColumns();
        SGDataColumnInfo[] colInfoArray = new SGDataColumnInfo[columns.length];
        for (int ii = 0; ii < colInfoArray.length; ii++) {
            colInfoArray[ii] = new SGSDArrayDataColumnInfo(columns[ii].getTitle(),
            		columns[ii].getValueType(), columns[ii].getLength());
        }
        return this.createColumnInfoSet(dataType, infoMap, colInfoArray);
    }

    /**
     * Returns an array of the data column information.
     * @param ncFile
     *                netCDF file
     * @param dataType
     *                the data type
     * @param infoMap
     *                the information map
     * @return an array of the data column information
     */
    SGDataColumnInfoSet getNetCDFDefaultDataColumnInfo(SGNetCDFFile ncFile, String dataType,
            Map<String, Object> infoMap) {
        List<SGNetCDFVariable> varList = ncFile.getVariables();
        SGNetCDFDataColumnInfo[] colArray = SGDataUtility.getNetCDFDataColumnInfo(varList, infoMap);
        return this.createColumnInfoSet(dataType, infoMap, colArray);
    }

    SGDataColumnInfoSet getMDArrayDataDefaultDataColumnInfo(SGMDArrayFile mdFile, String dataType,
            Map<String, Object> infoMap) {
		SGMDArrayVariable[] vars = mdFile.getVariables();
        SGMDArrayDataColumnInfo[] colArray = SGDataUtility.getMDArrayDataColumnInfo(mdFile, vars, infoMap);
        if (colArray == null) {
        	return null;
        }
        return this.createColumnInfoSet(dataType, infoMap, colArray);
    }

    /**
     * Create a map of information for each data type.
     *
     * @param dataType
     *            the type of data
     * @param dg
     *            a wizard dialog to select data type
     * @return a map of information
     */
    static Map<String, Object> createInfoMap(String dataType,
    		SGDataTypeWizardDialog dg, int figureID, Point pos) {

        Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, dataType);

        if (SGDataUtility.isSXYTypeData(dataType)) {
            final Boolean multiple = dg.isMultipleSelected();
            infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, multiple);
            if (SGDataUtility.isNetCDFData(dataType)
            		|| SGDataUtility.isMDArrayData(dataType)) {
                final boolean multipleVariable = true;
                infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.valueOf(multipleVariable));
            } else if (SGDataUtility.isSDArrayData(dataType) && SGDataUtility.isSXYTypeData(dataType)) {
                // add the sampling rate to the infoList
                Double d = dg.getSamplingRate();
                if (d != null) {
                    putSamplingRate(infoMap, d);
                }
            }
        } else if (SGDataUtility.isSXYZTypeData(dataType)) {
//        	// set true by default
//        	infoMap.put(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, true);
        } else if (SGDataUtility.isVXYTypeData(dataType)) {
            boolean b = dg.isPolarSelected();
            infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.valueOf(b));
            
//        	// set true by default
//        	infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, true);
        }

        // figure size
        SGDrawingWindow wnd = dg.getOwnerWindow();
        SGFigure figure = wnd.getFigure(figureID);
        SGTuple2f size = null;
        if (figure != null) {
        	size = new SGTuple2f(figure.getGraphRectWidth(), figure.getGraphRectHeight());
        } else {
            size = getDefaultFigureSize(wnd, pos, dataType);
        }
        infoMap.put(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE, size);

        // stride of data arrays
        getDataStrideAvailable(infoMap);

        return infoMap;
    }

    static void getDataStrideAvailable(Map<String, Object> infoMap) {
    	/*
        Preferences pref = Preferences.userNodeForPackage(SGMainFunctions.class);
        String strideAvailable = pref.get(PREF_KEY_DATA_STRIDE_AVAILABLE, null);
        boolean available = false;
        if (strideAvailable != null) {
        	Boolean b = SGUtilityText.getBoolean(strideAvailable);
        	if (b != null) {
        		available = b;
        	}
        }
    	infoMap.put(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE, available);
    	*/
    	// sets false by default
    	infoMap.put(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE, false);
    }

    static void putDataStrideAvailable(final boolean b) {
    	/*
        Preferences pref = Preferences.userNodeForPackage(SGMainFunctions.class);
        pref.putBoolean(PREF_KEY_DATA_STRIDE_AVAILABLE, b);
        */
    	// do nothing
    }

    /**
     * Create a map of information for each data type.
     * <p>
     * from property file.
     *
     * @param dataType
     *            the type of data
     * @param el
     *            an Element object
     * @return a map of information
     */
    static Map<String, Object> createInfoMap(String dataType, Element el) {

    	String str = null;
        Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, dataType);

    	if (SGDataUtility.isSXYTypeData(dataType)) {
            final boolean multiple = SGDataUtility.isMultipleData(dataType);
            infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.valueOf(multiple));

            // picked up dimension
            SGIntegerSeriesSet pickUpIndices = null;
            str = el.getAttribute(SGIDataPropertyKeyConstants.KEY_PICK_UP_DIMENSION_INDICES);
            if (str != null && str.length() != 0) {
            	Map<String, Integer> aliasMap = new HashMap<String, Integer>();
            	aliasMap.put(SGIntegerSeries.ARRAY_INDEX_END, null);
            	pickUpIndices = SGIntegerSeriesSet.parse(str, aliasMap);
            	if (pickUpIndices != null) {
                	infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, pickUpIndices);
            	}
            } else {
                str = el.getAttribute(SGIDataPropertyKeyConstants.KEY_PICKUP_START);
                if (null != str && str.length() != 0) {
                    Integer num = SGUtilityText.getInteger(str.trim());
                    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_START, num);
                }
                str = el.getAttribute(SGIDataPropertyKeyConstants.KEY_PICKUP_END);
                if (null != str && str.length() != 0) {
                    Integer num = SGUtilityText.getInteger(str.trim());
                    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_END, num);
                }
                str = el.getAttribute(SGIDataPropertyKeyConstants.KEY_PICKUP_STEP);
                if (null != str && str.length() != 0) {
                    Integer num = SGUtilityText.getInteger(str.trim());
                    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_STEP, num);
                }
            }

            if (SGDataUtility.isSDArrayData(dataType)) {
                infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
            } else if (SGDataUtility.isNetCDFData(dataType)) {
                if (SGDataUtility.isNetCDFDimensionData(dataType)) {
                    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
                } else {
                    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
                }
                str = el.getAttribute(SGIDataPropertyKeyConstants.KEY_PICKUP_DIMENSION_NAME);
                if (null != str && str.length() != 0) {
                    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
                }

            } else if (SGDataUtility.isMDArrayData(dataType)) {

                String dimensionIndexMapStr = el.getAttribute(SGIDataPropertyKeyConstants.KEY_PICK_UP_DIMENSION);
                if (dimensionIndexMapStr != null && dimensionIndexMapStr.length() != 0) {
                	Map<String, Integer> dimensionIndexMap = new HashMap<String, Integer>();
                	String[] tokens = dimensionIndexMapStr.split(",");
                	for (String token : tokens) {
                		String[] array = token.split(":");
                		if (array.length != 2) {
                			return null;
                		}
                		String name = array[0].trim();
                		String indexStr = array[1].trim();
                		Integer index = SGUtilityText.getInteger(indexStr);
                		if (index == null) {
                			return null;
                		}
                		dimensionIndexMap.put(name, index);
                	}
                	infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP, dimensionIndexMap);
                }
                final boolean multipleVar = (dimensionIndexMapStr == null || dimensionIndexMapStr.length() == 0
                		|| pickUpIndices == null);
                infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, multipleVar);
            }

    	} else if (SGDataUtility.isVXYTypeData(dataType)) {
            str = el.getAttribute(SGIFigureElementGraph.KEY_POLAR);
            if (str.length() == 0) {
                return null;
            }
            Boolean b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return null;
            }
            infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, b);
        }

        String timeIndexMapStr = el.getAttribute(SGIDataPropertyKeyConstants.KEY_ANIMATION_DIMENSION);
        if (timeIndexMapStr != null && timeIndexMapStr.length() != 0) {
        	Map<String, Integer> timeIndexMap = new HashMap<String, Integer>();
        	String[] tokens = timeIndexMapStr.split(",");
        	for (String token : tokens) {
        		String[] array = token.split(":");
        		if (array.length != 2) {
        			return null;
        		}
        		String name = array[0].trim();
        		String indexStr = array[1].trim();
        		Integer index = SGUtilityText.getInteger(indexStr);
        		if (index == null) {
        			return null;
        		}
        		timeIndexMap.put(name, index);
        	}
        	infoMap.put(SGIDataInformationKeyConstants.KEY_TIME_DIMENSION_INDEX_MAP, timeIndexMap);
        }

        // add the flag of availability of stride
        Boolean strideAvailable = null;
        str = el.getAttribute(SGIDataPropertyKeyConstants.KEY_ARRAY_SECTION_AVAILABLE);
        if (str != null && str.length() != 0) {
        	strideAvailable = SGUtilityText.getBoolean(str);
        }
    	if (strideAvailable != null) {
            infoMap.put(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE, strideAvailable);
    	} else {
    		// get from the preferences
            getDataStrideAvailable(infoMap);
    	}

        // add the stride for each data type
    	Map<String, Integer> aliasMap = new HashMap<String, Integer>();
    	aliasMap.put(SGIntegerSeries.ARRAY_INDEX_END, null);
        if (SGDataUtility.isSXYTypeData(dataType)) {
			String strideKey = SGDataUtility.isSDArrayData(dataType) ? SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE
					: SGIDataInformationKeyConstants.KEY_SXY_STRIDE;
        	updateStrideInfo(el, SGIDataPropertyKeyConstants.KEY_ARRAY_SECTION,
        			strideKey, infoMap, aliasMap);
        	
        	// tick label
        	updateStrideInfo(el, SGIDataPropertyKeyConstants.KEY_TICK_LABEL_ARRAY_SECTION,
        			SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE, infoMap, aliasMap);
        	
        	// index
        	updateStrideInfo(el, SGIDataPropertyKeyConstants.KEY_INDEX_ARRAY_SECTION,
        			SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE, infoMap, aliasMap);
        	// for backward compatibility
        	updateStrideInfo(el, SGIDataPropertyKeyConstants.KEY_SERIAL_NUMBER_ARRAY_SECTION,
        			SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE, infoMap, aliasMap);
        } else if (SGDataUtility.isVXYTypeData(dataType)) {
        	// x-direction
        	updateStrideInfo(el, SGIDataPropertyKeyConstants.KEY_X_ARRAY_SECTION,
        			SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X, infoMap, aliasMap);
        	
        	// y-direction
        	updateStrideInfo(el, SGIDataPropertyKeyConstants.KEY_Y_ARRAY_SECTION,
        			SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y, infoMap, aliasMap);

        	// index
        	updateStrideInfo(el, SGIDataPropertyKeyConstants.KEY_ARRAY_SECTION,
        			SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE, infoMap, aliasMap);
        	updateStrideInfo(el, SGIDataPropertyKeyConstants.KEY_INDEX_ARRAY_SECTION,
        			SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE, infoMap, aliasMap);
        	// for backward compatibility
        	updateStrideInfo(el, SGIDataPropertyKeyConstants.KEY_SERIAL_NUMBER_ARRAY_SECTION,
        			SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE, infoMap, aliasMap);
        } else if (SGDataUtility.isSXYZTypeData(dataType)) {
        	// x-direction
        	updateStrideInfo(el, SGIDataPropertyKeyConstants.KEY_X_ARRAY_SECTION,
        			SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X, infoMap, aliasMap);
        	
        	// y-direction
        	updateStrideInfo(el, SGIDataPropertyKeyConstants.KEY_Y_ARRAY_SECTION,
        			SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y, infoMap, aliasMap);
        	
        	// index
        	updateStrideInfo(el, SGIDataPropertyKeyConstants.KEY_ARRAY_SECTION,
        			SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE, infoMap, aliasMap);
        	updateStrideInfo(el, SGIDataPropertyKeyConstants.KEY_INDEX_ARRAY_SECTION,
        			SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE, infoMap, aliasMap);
        	// for backward compatibility
        	updateStrideInfo(el, SGIDataPropertyKeyConstants.KEY_SERIAL_NUMBER_ARRAY_SECTION,
        			SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE, infoMap, aliasMap);
        }

        // add the sampling rate
        str = el.getAttribute(SGIFigureElementGraph.KEY_SAMPLING_RATE);
        if (str != null && str.length() != 0) {
            putSamplingRate(infoMap, str);
        }

        // add the node map
        NamedNodeMap nodeMap = el.getAttributes();
        infoMap.put(SGIFigureElementGraph.KEY_NODE_MAP, nodeMap);

        return infoMap;
    }

    private static void updateStrideInfo(Element el, String propertyKey, String infoKey,
    		Map<String, Object> infoMap, Map<String, Integer> aliasMap) {
    	String str = el.getAttribute(propertyKey);
    	if (str != null && str.length() != 0) {
    		SGIntegerSeriesSet stride = SGIntegerSeriesSet.parse(str, aliasMap);
    		if (stride != null) {
        		infoMap.put(infoKey, stride);
    		}
    	}
    }

    /**
     * Create a map of information for each data type.
     * <p>
     * from Data command.
     * @param dataType
     *            the type of data
     * @param map
     *            a map of properties
     * @return a map of information
     */
    static Map<String, Object> createInfoMap(String dataType, SGPropertyMap map) {
        Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, dataType);

        if (SGDataUtility.isVXYTypeData(dataType)) {
            String str = null;
            Iterator<String> itr = map.getKeyIterator();
            while (itr.hasNext()) {
                String key = itr.next();
                if (SGIDataCommandConstants.COM_DATA_POLAR.equalsIgnoreCase(key)) {
                    str = map.getValueString(key);
                    break;
                }
            }
            if (str == null) {
                return null;
            }
            Boolean b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return null;
            }
            infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, b);
        }

        final boolean multiple = SGDataUtility.isMultipleData(dataType);
        infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.valueOf(multiple));
        if (SGDataUtility.isNetCDFData(dataType)) {
            if (SGDataUtility.isNetCDFDimensionData(dataType)) {
            	// only for backward compatibility
                infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
                Integer start = SGUtilityText.getInteger(map.getValue(SGIDataCommandConstants.COM_DATA_PICKUP_START));
                if (start == null) {
                	return null;
                }
                Integer end = SGUtilityText.getInteger(map.getValue(SGIDataCommandConstants.COM_DATA_PICKUP_END));
                if (end == null) {
                	return null;
                }
                Integer step = SGUtilityText.getInteger(map.getValue(SGIDataCommandConstants.COM_DATA_PICKUP_STEP));
                if (step == null) {
                	return null;
                }
                if (!SGIntegerSeries.isValidSeries(start, end, step)) {
                	return null;
                }
                infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_START, start);
                infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_END, end);
                infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_STEP, step);
            } else {
                infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
            }
        } else if (SGDataUtility.isMDArrayData(dataType)) {
            infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
        }

        // add the sampling rate to the infoList
        if (dataType.equalsIgnoreCase(SGDataTypeConstants.SXY_SAMPLING_DATA) ||
                dataType.equalsIgnoreCase(SGDataTypeConstants.SXY_MULTIPLE_DATA)) {
            String str = null;
            Iterator<String> itr = map.getKeyIterator();
            while (itr.hasNext()) {
                String key = itr.next();
                if (SGIDataCommandConstants.COM_DATA_SAMPLING_RATE.equalsIgnoreCase(key)) {
                    str = map.getValueString(key);
                    break;
                }
            }
            if (str != null) {
                if (putSamplingRate(infoMap, str) == false) {
                    return null;
                }
                if (dataType.equalsIgnoreCase(SGDataTypeConstants.SXY_SAMPLING_DATA)) {
                    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_MULTIPLE_DATA);
                    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, false);
                }
            }
        }

        // stride of data arrays
        getDataStrideAvailable(infoMap);

        return infoMap;
    }

    /** Put sampling rate to the information map.
     * @return false if str is not double or less than 0. Otherwise true.
     */
    private static boolean putSamplingRate(Map<String, Object> infoMap, String str) {
        Double d = SGUtilityText.getDouble(str);
        if (d == null) {
            return false;
        }
    	return putSamplingRate(infoMap, d);
    }

    /** Put sampling rate value to the information map.
     * @return false if d <= 0.0. Otherwise true.
     */
    private static boolean putSamplingRate(Map<String, Object> infoMap, Double d) {
        if (d.doubleValue() <= 0.0) {
            return false;
        }
        infoMap.put(SGIDataInformationKeyConstants.KEY_SAMPLING_RATE, d);
    	return true;
    }

    boolean onToolBarDataAdditionExecuted(final SGDrawingWindow wnd) {
    	
        // create wizard dialogs
        this.createDataAdditionWizardDialogs(wnd);

        // set figure ID numbers
        final int[] idArray = wnd.getVisibleFigureIDArray();
        this.mFigureIDSelectionWizardDialog.setIDNumbers(idArray);

        // sets the OK button invisible
        this.mFigureIDSelectionWizardDialog.setOKButtonVisible(false);

        // sets the name
        this.mFigureIDSelectionWizardDialog.setDataName(null);

        // packs the dialog
        this.mFigureIDSelectionWizardDialog.pack();

        // setup the connection
        this.setupDataAdditionWizardDialogConnection(null, DATA_ADDITION_TOOL_BAR,
        		null, false);

        // set location
        this.mFigureIDSelectionWizardDialog.setCenter(wnd);

        // show the first wizard dialog
        this.mFigureIDSelectionWizardDialog.setVisible(true);

        // update the selected file name
        File f = this.mSingleDataFileChooserWizardDilaog.getSelectedFile();
        if (f == null) {
            return false;
        }
        this.updateCurrentFile(f, FILE_TYPE.TXT_DATA);
        
        return true;
    }

    private SGStatus drawGraphSub(
            final SGDrawingWindow wnd, final int figureID,
            final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap,
            final DataSourceInfo dataSource, final Integer[] dataIdArray,
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
                    wnd, figureID, dataIdArray, showDialog, infoMap,
                    figureLocation, fig, cdSet, dataNameBase, false) == false) {
                return new SGStatus(false);
            }
        } catch (HDF5DatatypeInterfaceException e) {
        	// Failed to read the HDF5 data.
        	wnd.endProgress();
            return new SGStatus(false, MSG_HDF5_VALUES_OUT_OF_RANGE);
        }

        return new SGStatus(true);
    }

    /**
     *
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
        if (len==0) {
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
            StringBuffer sb = new StringBuffer();
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
                wnd, figureID, dataIdArray, showDialog,
                figureLocation, fig, dataArray, nameArray, infoMap) == false) {
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

    private boolean addData(final SGDrawingWindow wnd, final int figureID,
            final Integer[] dataIdArray, final boolean showDialog,
            final Point figureLocation, SGFigure fig, SGData[] dataArray,
            String[] nameArray, final Map<String, Object> infoMap) {

        boolean createFigureFlag = (fig == null);
        if (fig != null) {
            if (fig.isVisible() == false) {
                createFigureFlag = true;
            }
        }

        if (createFigureFlag) {
            // create new figure and add data.
            if (!SGMainFunctions.this.mFigureCreator.createNewFigure(wnd,
                    figureID, figureLocation, dataIdArray, dataArray, nameArray, infoMap)) {
                return Boolean.FALSE;
            }
            Object obj = infoMap.get(SGPlotTypeConstants.KEY_PLOT_TYPE_BAR);
            if (obj instanceof Boolean) {
                if (((Boolean)obj).booleanValue()) {
                    SGFigure figure = wnd.getFigure(figureID);
                    figure.fitAxisRangeToVisibleData(false);
                }
            }
        } else {
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
            if (fig.getClassType()==null || fig.getClassType().equals("")) {
                String type = "";
                SGData dataFirst = dataArray[0];
                if (dataFirst instanceof SGISXYTypeSingleData || dataFirst instanceof SGISXYTypeMultipleData) {
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
     * @param wnd
     *            a window that the graph belongs
     * @param figureID
     *            the ID of figure to draw the graph
     * @param colInfo
     *            an array of data column information
     * @param infoMap
     *            a map for data information
     * @param pathName
     *            path name of data
     * @param dataId
     *            the ID of data object to add
     * @param showDialog
     *            true if error and warning dialog is shown
     * @param figureLocation
     *            The location of new figure if it is created.
     *            If data is added to a figure that already exists,
     *            this value is neglected.
     * @return
     *            true if succeeded
     */
    SGStatus drawGraph(final SGDrawingWindow wnd, final int figureID,
            final SGDataColumnInfoSet colInfoSet, final Map<String, Object> infoMap,
            final DataSourceInfo dataSource, final Integer[] dataIdArray, final boolean showDialog,
            final Point figureLocation) {
    	SGStatus result = null;
        wnd.setWaitCursor(true);

        if (!USE_FOXTROT) {
            result = drawGraphSub(wnd, figureID, colInfoSet, infoMap,
            		dataSource, dataIdArray, showDialog, figureLocation);
        } else {
            try {
                result = (SGStatus) Worker.post(new Task() {
                    public Object run() throws Exception {
                        return drawGraphSub(wnd, figureID, colInfoSet,
                                infoMap, dataSource, dataIdArray, showDialog, figureLocation);
                    }
                });
            } catch (Exception ex) {
            	result = new SGStatus(false);
                ex.printStackTrace();
            }
        }

        wnd.setWaitCursor(false);
        return result;
    }

    /**
     * Returns true if given object is a wizard dialog for data-addition.
     */
    private boolean isDataAdditionDialog(Object obj) {
		return obj.equals(this.mFigureIDSelectionWizardDialog)
				|| obj.equals(this.mSingleDataFileChooserWizardDilaog)
				|| obj.equals(this.mFileTypeSelectionWizardDialog)
				|| obj.equals(this.mDataTypeWizardDialog)
				|| obj.equals(this.mSDArrayDataSetupWizardDialog)
				|| obj.equals(this.mNetCDFDataSetupWizardDialog)
				|| obj.equals(this.mMDArrayDataSetupWizardDialog)
				|| obj.equals(this.mPlotTypeSelectionWizardDialog);
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

    /**
     * Invoked when an action event is thrown.
     */
    public void actionPerformed(final ActionEvent e) {
        Object source = e.getSource();
		String command = e.getActionCommand();
        if (this.isDataAdditionDialog(source)) {
            if (this.mDroppedDataFile != null) {
                // drag and drop
                this.makeTransitionForDragAndDrop(e);
            } else if (this.mTransformedData != null) {
                // transformation
                this.makeTransitionForDataTransformation(e);
            } else if (this.mVirtualMDArrayData != null) {
            	// plug-in
            	this.makeTransitionForPlugin(e);
            } else {
                // tool bar
                this.makeTransitionForToolBar(e);
            }
        } else if (source instanceof SGDataViewerDialog) {
        	
        	SGDataViewerDialog dgSrc = (SGDataViewerDialog) source;
        	SGDrawingWindow wnd = dgSrc.getOwnerWindow();
        	
        	if (SGDataViewerDialog.DATA_EDITED.equals(command)) {
        		SGData data = dgSrc.getData();
        		List<SGFigure> figureList = wnd.getVisibleFigureList();
        		for (SGFigure f : figureList) {
        			SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) f.getGraphElement().getChild(data);
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
        	Iterator<Entry<String, Set<Integer>>> indexSetItr = overlappingIndexSetMap.entrySet().iterator();
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
        				infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, 
        						SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA);
        				infoMap.put(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, true);

        				// create column information set
        				SGMDArrayDataColumnInfo col = new SGMDArrayDataColumnInfo(var, varName, 
    							SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
        				col.setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, 0);
        				col.setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, 1);
        				col.setColumnType(Z_VALUE);
        				SGDataColumnInfoSet colInfoSet = new SGDataColumnInfoSet(
        						new SGDataColumnInfo[] { col });

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

    /**
     * Exit the application normally with confirmation.
     */
    public void exit() {
        this.mWindowManager.closeAllWindow();
    }

    // Exit the application.
    void exitApplication(final int status) {
        if (status != 0) {
            String msg = "A fatal error has occured.\n"
                    + "The current application will be terminated.";
            String title = " Forced termination";
            SGUtility.showMessageDialog(null, msg, title,
                    JOptionPane.ERROR_MESSAGE);
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
			e.printStackTrace();
		}

        // exit
        System.exit(status);
    }

    /**
     * Opens given file for Mac OS X.
     *
     * @param fname
     *          the file name
     * @param wnd
     *          the window
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

    // start to read the input
    private void startReadingInput() throws IOException {
    	this.readRecursively(mStdinReader, mStdoutWriter, 0, true, false);
    }

    /**
     * Called when files are dropped onto the window.
     *
     * @param fileList
     *           the list of dropped files
     * @param wnd
     *           the window
     * @param pos
     *           location the files are dropped
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
                final boolean propertyFlag = SGApplicationUtility.hasExtension(path,
                		PROPERTY_FILE_EXTENSION);
                if (propertyFlag) {
                    propertyFile = file;
                    continue;
                }

                // archive file?
                final boolean archiveFlag = SGApplicationUtility.hasExtension(path,
                        ARCHIVE_FILE_EXTENSION);
                if (archiveFlag) {
                    archiveFile = file;
                    continue;
                }

                // script file?
                final boolean scriptFlag = SGApplicationUtility.hasExtension(path,
                        SCRIPT_FILE_EXTENSION);
                if (scriptFlag) {
                	scriptFile = file;
                	continue;
                }

                // image file?
                boolean imageFlag = SGApplicationUtility.hasExtension(path,
                		SGIImageConstants.DRAWABLE_IMAGE_EXTENSIONS);
                if(imageFlag) {
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
                    	final String message = "This NetCDF file has samurai-graph properties. Do you apply them to the graph?";
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

                if (this.mPropertyFileManager.showMultiDataFileChooserDialog(
                        propertyFile, wnd) == false) {
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
                
                final boolean result = this.mDataSetManager
						.loadDataSetFromEventDispatchThread(wnd, archiveFile);
                if (result==false && SGUtility.wasMessageDialogVisible()==false) {
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

                final boolean result = this.mDataSetManager
                        .loadDataSetFromEventDispatchThread(wnd, netcdfArchiveFile);
                if (result==false && SGUtility.wasMessageDialogVisible()==false) {
                    SGUtility.showErrorMessageDialog(wnd, ERRMSG_TO_LOAD_DATASET, SGIConstants.TITLE_ERROR);
                }
                SGUtility.clearMessageDialogVisible();
                wnd.setSaved(result);
            } else if (scriptFile != null) {
            	if (this.mStdoutWriter != null && this.mStdinReader != null) {
            		// sets the current window
            		this.mWindowManager.setCurrentWindow(wnd);
            		
            		// loads the command script file
                    this.loadCommandScriptFile(scriptFile.getPath());
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
            ex.printStackTrace();
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
        StringBuffer sb = new StringBuffer();
        for (int ii = 0; ii < array.length; ii++) {
            sb.append(array[ii]);
            if (ii != array.length - 1) {
                sb.append(',');
            }
        }
        pref.put(PREF_KEY_TOOL_BAR_PATTERN, sb.toString());
    }

    // Updates the information of the window in the preferences.
    void updatWindowInfoInPreferences(final SGDrawingWindow wnd) {
        Preferences pref = Preferences.userNodeForPackage(this.getClass());

        // updates the preferences
        SGTuple2f size = wnd.getViewportSize();
        pref.put(PREF_KEY_VIEWPORT_WIDTH, Float.toString(size.x));
        pref.put(PREF_KEY_VIEWPORT_HEIGHT, Float.toString(size.y));
    }

    /**
     *
     * @param elFigure
     * @param figure
     * @return
     */
    private int createSingleFigureFromPropertyFile(final Element elFigure,
            final SGDrawingWindow wnd, final ArrayList<WrappedData> dataList,
            final boolean readDataProperty, final String versionNumber, final int mode) {

        final int ic = SGIConstants.PROPERTY_FILE_INCORRECT;

        String str = null;

        str = elFigure.getAttribute( SGFigure.KEY_FIGURE_TYPE );
        if (str.length() == 0) {
            return ic;
        }

        SGFigure figure = null;
        if (SGIFigureTypeConstants.FIGURE_TYPE_SXY.equals(str)
                || SGIFigureTypeConstants.FIGURE_TYPE_VXY.equals(str)
                || SGIFigureTypeConstants.FIGURE_TYPE_XY.equals(str)) {
            figure = new SGXYFigure(wnd);
            figure.setClassType(str);
        } else {
            return ic;
        }

        //
        // create a figure
        //

        final int figureID = wnd.assignFigureId();
        figure.setID(figureID);

        //
        // create SGIFigureElement objects
        //

        if (figure.readProperty(elFigure) == false) {
            return ic;
        }

        if (this.mFigureCreator.createFigureElementFromPropertyFile(figure,
                elFigure, versionNumber) == ic) {
            return ic;
        }

        // create data objects
        final int ret = this.createDataObjectsFromPropertyFile(elFigure,
                figure, dataList, wnd, readDataProperty, versionNumber, mode);
        if (ret != SGIConstants.SUCCESSFUL_COMPLETION) {
            return ret;
        }
        
        // called after data objects are created
        figure.setDataAnchored(figure.isDataAnchored());

        // add the figure to the window
        wnd.addFigure(figure);

        // initialize properties of the figure and figure elements
        figure.initPropertiesHistory();

        return SGIConstants.SUCCESSFUL_COMPLETION;
    }

    // create the data object
    private int createDataObjectsFromPropertyFile(final Element elFigure,
            final SGFigure figure, final ArrayList<WrappedData> dataList,
            final SGIProgressControl progress, final boolean readDataProperty,
            final String versionNumber, final int mode) {

        final int ic = SGIConstants.PROPERTY_FILE_INCORRECT;
        final int di = SGIConstants.DATA_FILE_INVALID;

        NodeList nList = elFigure
                .getElementsByTagName(SGIFigureElementGraph.TAG_NAME_DATA);
        final int len = nList.getLength();
        if (len != dataList.size()) {
            return ic;
        }

        int[] indexArray = new int[len];
        SGData[] dataArray = new SGData[len];
        boolean indexValid = true;
        for (int ii = 0; ii < len; ii++) {

            //
            // get information from the Element object
            //

            Node node = nList.item(ii);
            if ((node instanceof Element) == false) {
                continue;
            }
            Element elData = (Element) node;

            // data type
            String dataType = elData.getAttribute(SGIFigureElement.KEY_DATA_TYPE);
            if (dataType == null) {
                return ic;
            }

            if (SGDataUtility.isSDArrayData(dataType)) {
                // replaces the data type
                // because sampling SXY-data and date SXY-date is to be eliminated at some stage.
                dataType = SGApplicationUtility.getArrayDataType(dataType);
            }

            Class<?> dataClass = null;
            if (SGDataUtility.isSDArrayData(dataType)) {
            	if (SGDataUtility.isSXYTypeData(dataType)) {
            		dataClass = SGSXYSDArrayMultipleData.class;
                } else if (SGDataUtility.isVXYTypeData(dataType)) {
                	dataClass = SGVXYSDArrayData.class;
                } else if (SGDataUtility.isSXYZTypeData(dataType)) {
                	dataClass = SGSXYZSDArrayData.class;
            	}
            } else if (SGDataUtility.isNetCDFData(dataType)) {
            	if (SGDataUtility.isSXYTypeData(dataType)) {
        	        dataClass = SGSXYNetCDFMultipleData.class;
                } else if (SGDataUtility.isVXYTypeData(dataType)) {
                	dataClass = SGVXYNetCDFData.class;
                } else if (SGDataUtility.isSXYZTypeData(dataType)) {
                	dataClass = SGSXYZNetCDFData.class;
            	}
            } else if (SGDataUtility.isMDArrayData(dataType)) {
            	if (SGDataUtility.isSXYTypeData(dataType)) {
        	        dataClass = SGSXYMDArrayMultipleData.class;
                } else if (SGDataUtility.isVXYTypeData(dataType)) {
                	dataClass = SGVXYMDArrayData.class;
                } else if (SGDataUtility.isSXYZTypeData(dataType)) {
                	dataClass = SGSXYZMDArrayData.class;
            	}
            }
            if (dataClass == null) {
            	return ic;
            }

            // create a new SGData object
            SGData data = SGApplicationUtility.createDataInstance(dataClass);
            if (data == null) {
                return ic;
            }

            // set the class type for backward compatibility between 1.0.7
            if (SGIFigureTypeConstants.FIGURE_TYPE_XY.equals(figure.getClassType())) {
                String type = null;
                if (SGDataUtility.isVXYTypeData(dataType)) {
                    type = SGIFigureTypeConstants.FIGURE_TYPE_VXY;
                } else {
                    type = SGIFigureTypeConstants.FIGURE_TYPE_SXY;
                }
                figure.setClassType(type);
            }

            // create information map
            Map<String, Object> infoMap = createInfoMap(dataType, elData);

            // get the data file
            if (dataList.size() == 0) {
                return SGIConstants.DATA_NUMBER_SHORTAGE;
            }

            // get an object from dataList
            WrappedData wData = (WrappedData) dataList.get(ii);
            if (!wData.hasFigureData()) {
                SGPropertyFileData pfData = wData.getPropertyFileData();
                String fileName = pfData.getFileName();

                try {
                    // get the data column information
                    SGDataColumnInfoSet colInfoSet = pfData.getColumnInfoSet();
                    if (colInfoSet == null) {
                        if (SGDataUtility.isSDArrayData(dataType)) {
                        	// get default column information
                        	colInfoSet = this.getSDArrayDefaultDataColumnInfo(fileName, dataType,
                        			infoMap, true, versionNumber);
                        } else if (SGDataUtility.isNetCDFData(dataType)) {
                            NetcdfFile ncFile = null;
                            try {
                                if (NetcdfFile.canOpen(fileName) == false) {
                                    return di;
                                }
                                ncFile = SGApplicationUtility.openNetCDF(fileName);
                                if (ncFile == null) {
                                	return di;
                                }
                                try {
                                    SGNetCDFFile nc = new SGNetCDFFile(ncFile);
                                    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, nc);
                                    colInfoSet = this.getNetCDFDefaultDataColumnInfo(nc, dataType, infoMap);
                                } finally {
                    				try {
                						ncFile.close();
                    				} catch (IOException e) {
                                        return di;
                    				}
                                }
                            } catch (Exception e) {
                                return di;
                            }
                        } else if (SGDataUtility.isMDArrayData(dataType)) {
                        	SGMDArrayFile mdFile = null;
                        	IHDF5Reader hdf5Reader = null;
                        	MatFileReader matReader = null;
                        	try {
                            	if (SGDataUtility.isHDF5FileData(dataType)) {
                            		hdf5Reader = SGApplicationUtility.openHDF5(fileName);
                            		mdFile = new SGHDF5File(hdf5Reader);
                            	} else if (SGDataUtility.isMATLABData(dataType)) {
                            		matReader = SGApplicationUtility.openMAT(fileName);
                            		mdFile = new SGMATLABFile(fileName, matReader);
                            	} else {
                            		return di;
                            	}
                                infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, mdFile);
                                colInfoSet = this.getMDArrayDataDefaultDataColumnInfo(mdFile, dataType, infoMap);
                        	} catch (Exception e) {
                        		return di;
                        	} finally {
                        		if (hdf5Reader != null) {
                        			hdf5Reader.close();
                        		}
                        	}
                        } else {
                            return di;
                        }
                        if (colInfoSet == null) {
                            return di;
                        }
                    }

                    Map<String, Object> pfInfoMap = pfData.getInfoMap();
                    if (pfInfoMap.size() == 0) {
                    	// for the case of Samurai Graph Archive (.sga) file
                    	pfInfoMap = new HashMap<String, Object>(infoMap);
                    }

                    // data type
                    String dataTypeNew = (String) pfInfoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
                    if (dataTypeNew == null) {
                    	return di;
                    }
                    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, dataTypeNew);

                    // updates the information map
                    final int ret = SGApplicationUtility.updateInformationMap(colInfoSet, infoMap, pfInfoMap);
                    if (ret != SGIConstants.SUCCESSFUL_COMPLETION) {
                    	return ret;
                    }

                    // create data object
                    DataSourceInfo dataSource = new DataSourceInfo(fileName);
                    SGDataCreator.CreatedDataSet cdSet = this.mDataCreator.create(dataSource,
                            colInfoSet, infoMap, progress, versionNumber, mode);
                    if (cdSet == null) {
                        return di;
                    }
                    if (cdSet.getDataLength() == 0) {
                    	return di;
                    }
                    SGDataCreator.CreatedData cd = cdSet.getData(0);
                    SGData firstData = cd.getData();
                    try {
                        if (data.setData(firstData) == false) {
                            return di;
                        }
                    } finally {
                    	// disposes data
                    	firstData.dispose();
                    }

                } catch (FileNotFoundException ex) {
                    return SGIConstants.FILE_OPEN_FAILURE;
                }

            } else if (wData.hasFigureData()) {
                if (data.setData(wData.getFigureData().getData()) == false) {
                    return ic;
                }
            } else {
                return ic;
            }

            // get the index in the legend
            String str = elData
                    .getAttribute(SGIFigureElement.KEY_INDEX_IN_LEGEND);
            if (str.length() != 0) {
                Number num = SGUtilityText.getInteger(str);
                if (num == null) {
                    return ic;
                }
                indexArray[ii] = num.intValue();
                dataArray[ii] = data;
            } else {
                indexValid = false;
            }

            // create data objects
            if (figure.createDataObjectFromPropertyFile(elData, data, readDataProperty) == false) {
                return ic;
            }
        }

        // sort the order of data objects in legend
        if (indexValid) {
            SGIFigureElementLegend lElement = figure.getLegendElement();
            lElement.sortLegend(dataArray, indexArray);
        }

        SGIFigureElement[] array = figure.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].initPropertiesHistory();
        }

        return SGIConstants.SUCCESSFUL_COMPLETION;
    }

    /**
     *
     * @param elWnd
     * @param wnd
     * @return
     */
    int createFiguresFromPropertyFile(final Element elWnd,
            final SGDrawingWindow wnd, final WrappedData[] wDataArray,
            final boolean readDataProperty, final String versionNumber, final int mode) {

        NodeList nList = elWnd.getElementsByTagName(SGFigure.TAG_NAME_FIGURE);
        final int len = nList.getLength();

        for (int ii = 0; ii < len; ii++) {
            Node node = nList.item(ii);
            if (node instanceof Element) {
                Element el = (Element) node;
                ArrayList<WrappedData> dataList = new ArrayList<WrappedData>();
                for (int jj = 0; jj < wDataArray.length; jj++) {
                    if (wDataArray[jj].hasFigureData()) {
                        FigureData fd = wDataArray[jj].getFigureData();
                        if (fd.getSerialFigureNo() == ii) {
                            dataList.add(wDataArray[jj]);
                        }
                    } else {
                        SGPropertyFileData pfData = wDataArray[jj].getPropertyFileData();
                        if (pfData.getFigureId() == ii + 1) {
                            String fileName = pfData.getFileName();
                            if (fileName != null && !SGPropertyDataFileChooserWizardDialog.NO_DATA.equals(fileName)) {
                                dataList.add(wDataArray[jj]);
                            }
                        }
                    }
                }
                final int ret = this.createSingleFigureFromPropertyFile(el,
                        wnd, dataList, readDataProperty, versionNumber, mode);
                if (ret != SGIConstants.SUCCESSFUL_COMPLETION) {
                    return ret;
                }
            }
        }

        return SGIConstants.SUCCESSFUL_COMPLETION;
    }

    /**
     * The class for dropped data file.
     */
    private static class DroppedDataFile {

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
    	this.createDataAdditionWizardDialogs(wnd);
    	
    	// setup with given files
    	this.onDataFileDropped(pos, wnd, fileList, FILE_TYPE.TXT_DATA, 
    			this.mMDArrayDataSetupWizardDialog);

        if (this.toSDArrayDataTypeDialog(wnd,
                this.mDataTypeWizardDialog,
                this.mDroppedDataFile.file) == false) {
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
    	this.createDataAdditionWizardDialogs(wnd);
    	
    	// setup with given files
    	this.onDataFileDropped(pos, wnd, fileList, FILE_TYPE.NETCDF_DATA, 
    			this.mMDArrayDataSetupWizardDialog);

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
			}
		}
		
		// apply embedded properties if they exist
		if (this.applyProperties(ncFile, wnd)) {
			return true;
		}

        if (this.toNetCDFOrMDArrayDataTypeDialog(wnd,
                this.mDataTypeWizardDialog, file) == false) {
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
    	if (!reader.hasAttribute("/", ATTR_NAME_SAMURAI_GRAPH_COMMAND)) {
    		return false;
    	}
    	final String commands = reader.getStringAttribute("/", ATTR_NAME_SAMURAI_GRAPH_COMMAND);
		if (commands != null) {
			return this.execCommand(commands, reader.getFile().getPath(), wnd);
		}
		return false;
    }

    private boolean applyProperties(IHDF5Reader reader, SGDrawingWindow wnd) {
    	if (!reader.hasAttribute("/", ATTR_NAME_SAMURAI_GRAPH_PROPERTIES)) {
    		return false;
    	}
    	final String commands = reader.getStringAttribute("/", ATTR_NAME_SAMURAI_GRAPH_PROPERTIES);
		if (commands != null) {
			return this.applyProperties(commands, reader.getFile().getPath(), wnd);
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
    		StringBuffer sbFilePath = new StringBuffer();
    		sbFilePath.append('"');
    		sbFilePath.append(path);
    		sbFilePath.append('"');
    		StringBuffer sbAlias = new StringBuffer();
    		sbAlias.append('"');
    		sbAlias.append(SGIDataCommandConstants.FILE_PATH_NETCDF_ITSELF);
    		sbAlias.append('"');
    		this.mCommandManager.addAlias(SGIDataCommandConstants.COM_DATA, 
    				SGIDataCommandConstants.COM_DATA_FILE_PATH, 
    				sbAlias.toString(), sbFilePath.toString());
        	SwingUtilities.invokeLater(new Runnable() {
        		public void run() {
        			// Creates and starts the thread to execute commands.
        			Thread th = new Thread() {
        				public void run() {
                            // setup standard output stream
                            mStdoutWriter = new BufferedWriter(new OutputStreamWriter(
                                    System.out));
                            // setup standard input stream
                            mStdinReader = new BufferedReader(new StringReader(commands));
                            try {
                                // read input recursively
                            	startReadingInput();
                            	mCommandManager.removeAlias(SGIDataCommandConstants.FILE_PATH_NETCDF_ITSELF);
                			} catch (IOException e1) {
                				return;
                			} finally {
                	        	try {
                	        		// Replaces the input stream.
                	        		mStdinReader.close();
                	                mStdinReader = new BufferedReader(new InputStreamReader(
                	                        System.in));
                                	startReadingInput();
                				} catch (IOException e) {
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

    		if (this.mPropertyFileManager.setPropertyFile(wnd, doc, wDataArray, true, versionNumber, 
    				LOAD_PROPERTIES_IN_NETCDF_ATTRIBUTE)) {
    			return true;
    		}
    	}
    	
		return false;
    }

    private void onDataFileDropped(final Point pos,
            final SGDrawingWindow wnd, final List<File> fileList,
            FILE_TYPE fileType, SGDataSetupWizardDialog dg) {
    	
    	// setup the wizard dialogs
    	this.mDataTypeWizardDialog.setDataFileType(fileType);
    	this.mDataTypeWizardDialog.setNext(dg);
    	this.setupDataAdditionWizardDialogConnection(
    			this.mDataTypeWizardDialog, DATA_ADDITION_DRAG_AND_DROP, fileType, false);
    	
        // gets only the first file
        File file = fileList.get(0);
        this.mDroppedDataFile = new DroppedDataFile(pos, file, wnd);
    }

    private static SGTuple2f getDefaultFigureSize(SGDrawingWindow wnd, Point pos, String dataType) {
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
     * Sets up the the dialog to setup the netCDF data.
     *
     * @param ncFile
     *           a netCDF file
     * @param dataType
     *           the type of data
     * @param dg
     *           the dialog to setup netCDF data
     * @return true if succeeded
     */
    private boolean setupNetCDFDataSetupDialog(SGNetCDFFile ncFile,
            String dataType, Map<String, Object> infoMap, SGNetCDFDataSetupWizardDialog dg) {

        Window wnd = dg.getOwner();

        // create column info
        List<SGNetCDFVariable> varList = ncFile.getVariables();
        final int size = varList.size();
        SGNetCDFDataColumnInfo[] cols = new SGNetCDFDataColumnInfo[size];
        int cnt = 0;
        for (SGNetCDFVariable var : varList) {
            cols[cnt] = new SGNetCDFDataColumnInfo(var, var.getName(), var.getValueType());
            cnt++;
        }
        SGDataColumnInfoSet colInfoSet = new SGDataColumnInfoSet(cols);

        // set the data
        if (dg.setData(ncFile, dataType, colInfoSet, infoMap, true) == false) {
            SGUtility.showErrorMessageDialog(wnd, MSG_INVALID_DATA_FILE,
                    SGIConstants.TITLE_ERROR);
            return false;
        }

        // pack the dialog
        dg.pack();

        return true;
    }

    /**
     * Sets up the the dialog to setup the multidimensional data.
     *
     * @param mdFile
     *           a multidimensional data file
     * @param dataType
     *           the type of data
     * @param dg
     *           the dialog to setup multidimensional data
     * @return true if succeeded
     */
    private boolean setupMDArrayDataSetupDialog(SGMDArrayFile mdFile,
            String dataType, Map<String, Object> infoMap, SGMDArrayDataSetupWizardDialog dg,
            SGDataColumnInfoSet colInfoSet, final boolean showDefault) {

        Window wnd = dg.getOwner();

        // create column info

        SGDataColumnInfoSet colInfoSetNew = null;
        if (colInfoSet != null) {
            colInfoSetNew = (SGDataColumnInfoSet) colInfoSet.clone();
        } else {
            SGMDArrayVariable[] vars = mdFile.getVariables();
            SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[vars.length];
            for (int ii = 0; ii < cols.length; ii++) {
            	SGMDArrayVariable var = vars[ii];
                cols[ii] = new SGMDArrayDataColumnInfo(var, var.getName(), var.getValueType());
            }
        	colInfoSetNew = new SGDataColumnInfoSet(cols);
        }

        // set the data
        if (dg.setData(mdFile, dataType, colInfoSetNew, infoMap, showDefault) == false) {
            SGUtility.showErrorMessageDialog(wnd, MSG_INVALID_DATA_FILE,
                    SGIConstants.TITLE_ERROR);
            return false;
        }

        // pack the dialog
        dg.pack();

        return true;
    }

    /**
     * A wrapper class for data object.
     * This class object has one of two attributes: <code>SGPropertyFileData</code>
     * or <code>FigureData</code>.
     * The former is used when a property file is read, and the latter is used
     * in the duplication and copy/paste of figures.
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
     * A wrapped data in a figure.
     * This class object is used in paste and duplication of fiugres.
     * The attribute <code>serialFigureNo</code> is not the figure ID
     * but the serial number of copied figures.
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


    // the prompt
    private static final String PROMPT = "$ ";

    // a symbol for the file input stream
    private static final String FILE_INPUT = "<<";

    // the maximum number for the recursion of file input
    private static final int FILE_RECURSION_DEPTH_MAX = 10;

    // the header for a comment line
    private static final String COMMENT_HEADER = "#";

    // the header for a comment line
    private static final String COMMENT_HEADER_2 = "//";

    // a text string that represents the start of a block comment
    private static final String BLOCK_COMMENT_START = "/*";

    // a text string that represents the end of a block comment
    private static final String BLOCK_COMMENT_END = "*/";

    // a flag whether the current position is in a block comment
    private boolean mBlockComment = false;

    private void readRecursively(BufferedReader br, BufferedWriter bw,
            int depth, final boolean showPrompt, final boolean inFile
            ) throws IOException {

        // check whether the recursion depth is within range
        depth++;
        if (depth > FILE_RECURSION_DEPTH_MAX) {
            bw.write("Recursion is too deep.\n");
            bw.flush();
            return;
        }

        // infinite loop as a server process
        while (true) {

            if (showPrompt) {
                // display the prompt
                bw.write(PROMPT);
                bw.flush();
            }

            // get a line
            String line = br.readLine();
            if (line == null) {
                break;
            }

            // closes the text field
            this.closeTextField();

            // checks whether a dialog is open
        	if (this.isDialogOpen()) {
                bw.write("Dialog is open.\n");
                bw.flush();
        		continue;
        	}

            // trim the line
            String tLine = line.trim();

            // skip an empty line
            if (tLine.length() == 0) {
                continue;
            }

            // skip the comment line
            if (tLine.startsWith(COMMENT_HEADER) || tLine.startsWith(COMMENT_HEADER_2)) {
                continue;
            }

            // start or end a block comment only when the file input sream is opened
            if (inFile) {
//              if (tLine.startsWith(BLOCK_COMMENT_START)) {
//              this.mBlockComment = true;
//              continue;
//            } else if (tLine.startsWith(BLOCK_COMMENT_END)) {
//              this.mBlockComment = false;
//              continue;
//            }
//
//            // skip the current line if it is in a block comment
//            if (this.mBlockComment) {
//              continue;
//            }

                // remove block comment.
                int spos = tLine.indexOf(BLOCK_COMMENT_START);
                int epos = tLine.indexOf(BLOCK_COMMENT_END);
                if (this.mBlockComment && ((spos<0 && epos>=0) || (epos>=0 && spos>epos+1))) {
                    // "... */" in block comment
                    tLine = tLine.substring(epos+2);
                    this.mBlockComment = false;
                    spos = tLine.indexOf(BLOCK_COMMENT_START);
                    epos = tLine.indexOf(BLOCK_COMMENT_END);
                }

                if (this.mBlockComment) {
                    continue;
                }

                while (spos>=0 && epos>=1 && spos+1<epos) {
                    //   "/* ... */"
                    tLine = tLine.substring(0, spos) + tLine.substring(epos+2);
                    spos = tLine.indexOf(BLOCK_COMMENT_START);
                    epos = tLine.indexOf(BLOCK_COMMENT_END);
                }

                spos = tLine.indexOf(BLOCK_COMMENT_START);
                if (spos>=0) {
                    // "/* ..."
                    this.mBlockComment = true;
                    tLine = tLine.substring(0, spos);
                }
                tLine = tLine.trim();
                if (tLine.length() == 0) {
                    continue;
                }
            }

            // interpret as a file path
            if (tLine.startsWith(FILE_INPUT)) {
                String path = tLine.substring(FILE_INPUT.length());
                path = path.trim();
                if (SGUtilityText.isDoubleQuoted(path)) {
                	path = path.substring(1, path.length() - 1);
                }
                File file = new File(path);
                if (!file.exists()) {
                    String errmsg = getFileNotFoundString(file);
                    bw.write(errmsg);
                    bw.flush();
                    continue;
                }
                SGBufferedFileReader reader = new SGBufferedFileReader(path);
                BufferedReader br2 = reader.getBufferedReader();
                readRecursively(br2, bw, depth, false, true);
                reader.close();
                continue;
            }

            // parse the line and execute the command
            final int ret = this.exec(tLine);

            // output the status
            StringBuffer status = new StringBuffer();
            if (ret == SGMainFunctions.STATUS_FAILED) {
                status.append("failed: ");
            } else if (ret == SGMainFunctions.STATUS_NOT_FOUND) {
                status.append("not found: ");
            } else if (ret == SGMainFunctions.STATUS_SUCCEEDED) {
                status.append("succeeded: ");
            } else if (ret == SGMainFunctions.STATUS_PARTIALLY_FAILED) {
            	status.append("partially failed: ");
            }
            status.append(tLine);
            status.append('\n');
            bw.write(status.toString());
            bw.flush();
        }
    }

    /**
     * Loads the command script file.
     * 
     * @param fileName
     *           file name
     */
    void loadCommandScriptFile(final String fileName) {
    	// creates and starts a thread
    	new CommandThread(fileName);
    }

    class CommandThread extends Thread {

    	private String mScriptFileName = null;

    	CommandThread(final String script) {
    		super();
    		this.mScriptFileName = script;
    		this.start();
    	}

        /**
         * Runs the thread.
         */
        public void run() {

            File sf = new File(this.mScriptFileName);
            SGBufferedFileReader reader = null;
            try {
            	reader = new SGBufferedFileReader(this.mScriptFileName);
                BufferedReader brs = reader.getBufferedReader();
                try {
                    // read the input script file
                    readRecursively(brs, mStdoutWriter, 0, false, true);

                    // show a prompt
                    mStdoutWriter.write(PROMPT);
                    mStdoutWriter.flush();
                } catch (IOException ex) {
                }
            } catch (FileNotFoundException e) {
                String errmsg = getFileNotFoundString(sf);
                try {
                    mStdoutWriter.write(errmsg);
                    mStdoutWriter.flush();
                } catch (IOException e1) {
                }
			} catch (IOException e) {
			} finally {
				if (reader != null) {
					reader.close();
				}
            }
        }
    }

    private String getFileNotFoundString(File file) {
        StringBuffer sb = new StringBuffer();
        sb.append("file not found: ");
        sb.append(file.getPath());
        sb.append('\n');
        return sb.toString();
    }


    /**
     * Splits focused SXY type data into multiple data.
     *
     * @param wnd
     *           a window
     * @return true if succeeded
     */
    boolean splitSXYData(final SGDrawingWindow wnd) {
        Boolean result = null;
        wnd.setWaitCursor(true);
        if (!USE_FOXTROT) {
            result = SGMainFunctionsSplitMerge.splitData(wnd);
        } else {
            try {
                result = (Boolean) Worker.post(new Task() {
                    public Object run() throws Exception {
                        return SGMainFunctionsSplitMerge.splitData(wnd);
                    }
                });
            } catch (Exception ex) {
                result = Boolean.FALSE;
                ex.printStackTrace();
            }
        }
        wnd.setWaitCursor(false);
        return result.booleanValue();
    }

    /**
     * Merge focused SXY type data
     * which was splitted from multiple graph data
     * into one multiple graph data.
     *
     * @param wnd
     *           a window
     * @return true if succeeded
     */
    boolean mergeSXYData(final SGDrawingWindow wnd) {
        Boolean result = null;
        wnd.setWaitCursor(true);
        if (!USE_FOXTROT) {
            result = SGMainFunctionsSplitMerge.mergeData(wnd);
        } else {
            try {
                result = (Boolean) Worker.post(new Task() {
                    public Object run() throws Exception {
                        return SGMainFunctionsSplitMerge.mergeData(wnd);
                    }
                });
            } catch (Exception ex) {
                result = Boolean.FALSE;
                ex.printStackTrace();
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
     * @param wnd
     *            the owner window
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
		Map<Integer, List<SGILineStylePropertyDialogObserver>> obsListMap = new HashMap<Integer, List<SGILineStylePropertyDialogObserver>>();
		int totalChildNum = 0;
		while (dataItr.hasNext()) {
			Entry<Integer, List<SGData>> entry = dataItr.next();
			final int figureId = entry.getKey();
			SGFigure figure = wnd.getFigure(figureId);
			SGIFigureElementGraph gElement = figure.getGraphElement();
			List<SGData> dataList = entry.getValue();
			List<SGILineStylePropertyDialogObserver> obsList = new ArrayList<SGILineStylePropertyDialogObserver>();
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
		Iterator<Entry<Integer, List<SGILineStylePropertyDialogObserver>>> obsListItr = obsListMap.entrySet().iterator();
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
     * @param wnd
     *           the window
     */
    public void showMemoryInfo(SGDrawingWindow wnd) {
        DecimalFormat fByte = new DecimalFormat("#,###KB");
        DecimalFormat fRatio = new DecimalFormat("##.#");
        final long free = Runtime.getRuntime().freeMemory() / 1024;
        final long total = Runtime.getRuntime().totalMemory() / 1024;
        final long max = Runtime.getRuntime().maxMemory() / 1024;
        final long used = total - free;
        final double ratio = (used * 100 / (double)total);
        StringBuffer sb = new StringBuffer();
        sb.append("total=");
        sb.append(fByte.format(total));
        sb.append(", used=");
        sb.append(fByte.format(used));
        sb.append(" (");
        sb.append(fRatio.format(ratio));
        sb.append("%), max=");
        sb.append(fByte.format(max));
        SGUtility.showMessageDialog(wnd, sb.toString(), "Memory Info",
        		JOptionPane.INFORMATION_MESSAGE);
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
     * @param wnd
     *           the window
     * @return true if succeeded
     */
    boolean transformData(SGDrawingWindow wnd) {

        this.createDataAdditionWizardDialogs(wnd);

        List<SGFigure> fList = wnd.getVisibleFigureList();
        for (SGFigure f : fList) {

            // get focused data objects
            SGIFigureElementGraph gElement = f.getGraphElement();
            List<SGData> dataList = gElement.getFocusedDataList();

            if (dataList.size() > 0) {
                SGData data = dataList.get(0);
                String name = gElement.getDataName(data);
                this.mTransformedData = new TransformedData(data, name,
                        f.getID());
                FILE_TYPE dataFileType;
                if (SGDataUtility.isSDArrayData(data)) {
                	dataFileType = FILE_TYPE.TXT_DATA;
                } else if (SGDataUtility.isNetCDFData(data)) {
                	dataFileType = FILE_TYPE.NETCDF_DATA;
                } else if (SGDataUtility.isHDF5Data(data)) {
                	dataFileType = FILE_TYPE.HDF5_DATA;
                } else if (SGDataUtility.isMATLABData(data)) {
                	dataFileType = FILE_TYPE.MATLAB_DATA;
                } else if (SGDataUtility.isVirtualMDArrayData(data)) {
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
                    this.mTransformedData.data,
                    this.mTransformedData.name,
                    this.mTransformedData.figureId);
        SGMainFunctionsTransform transform =
            new SGMainFunctionsTransform(this,
                    this.mDataTypeWizardDialog,
                    this.mSDArrayDataSetupWizardDialog,
                    this.mNetCDFDataSetupWizardDialog,
                    this.mMDArrayDataSetupWizardDialog,
                    this.mPlotTypeSelectionWizardDialog,
                    transformedData);
        return transform.addDataByDataTransformation(e);
    }

    public void windowOpened(WindowEvent e) {
    }

    /**
     * Invoked when a wizard dialog for data addition is closed.
     *
     */
    public void windowClosing(WindowEvent e) {
        Object source = e.getSource();
        if (this.isDataAdditionDialog(source)) {
            this.clearTemporaryData();
        }
    }
    
    // Clears temporary files.
    void clearTemporaryData() {
        this.mDroppedDataFile = null;
        this.mTransformedData = null;
        this.mVirtualMDArrayData = null;
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    /**
     *
     * @param dataType
     * @param infoMap
     * @param colInfoArray
     * @return Set of column information. Return null if failed to get default column types.
     */
    SGDataColumnInfoSet createColumnInfoSet(
            String dataType, Map<String, Object> infoMap,
            SGDataColumnInfo[] colInfoArray) {

        SGDataColumnInfo[] aColInfoArray =
            SGApplicationUtility.getAdditionalInfoArray(dataType, infoMap, colInfoArray);

        SGDataColumnInfo[] allInfoArray = new SGDataColumnInfo[colInfoArray.length + aColInfoArray.length];
        for (int ii = 0; ii < colInfoArray.length; ii++) {
            allInfoArray[ii] = colInfoArray[ii];
        }
        for (int ii = 0; ii < aColInfoArray.length; ii++) {
            allInfoArray[ii + colInfoArray.length] = aColInfoArray[ii];
        }

        List<SGDataColumnInfo> columnInfoList = new ArrayList<SGDataColumnInfo>(Arrays.asList(allInfoArray));

        // get default column types and set the each column information
        SGDefaultColumnTypeUtility.DefaultColumnTypeResult result =
            SGDefaultColumnTypeUtility.getDefaultColumnTypes(dataType, columnInfoList, infoMap);
        if (result.isSucceeded() == false) {
            return null;
        }

        String[] columnTypes = result.getDefaultColumnTypes();
        for (int ii = 0; ii < columnTypes.length; ii++) {
            allInfoArray[ii].setColumnType(columnTypes[ii]);
        }
        if (result instanceof DefaultMDColumnTypeResult) {
        	// for multidimensional result, sets the dimension index
        	DefaultMDColumnTypeResult dResult = (DefaultMDColumnTypeResult) result;
//        	int[] indices = dResult.getIndices();
        	List<Map<String, Integer>> indices = dResult.getAllIndices();
            for (int ii = 0; ii < columnTypes.length; ii++) {
            	SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) allInfoArray[ii];
//            	mdInfo.setDefaultDimensionIndex(indices[ii]);
            	mdInfo.putAllDimensionIndices(indices.get(ii));
            }
        }

        SGDataColumnInfoSet colInfoSet = new SGDataColumnInfoSet(allInfoArray);
        return colInfoSet;
    }

    /**
     * Sets the dimension origin and step.
     *
     * @param infoMap map
     * @param dg data setup dialog
     * @return
     */
    static boolean addDimensionValuesToInfoMap(
            final Map<String, Object> infoMap,
            final SGNetCDFDataSetupWizardDialog dg) {
    	addDimensionValuesToInfoMapSub(infoMap, dg);
        return true;
    }

    static boolean addDimensionValuesToInfoMap(
            final Map<String, Object> infoMap,
            final SGMDArrayDataSetupWizardDialog dg) {
    	addDimensionValuesToInfoMapSub(infoMap, dg);
    	infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP, dg.getPickupDimensionIndexMap());
        return true;
    }

    static boolean addDimensionValuesToInfoMapSub(
            final Map<String, Object> infoMap,
            final SGDataSetupWizardDialog dg) {
        infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE,
                dg.isVariableDataType());
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
            final Map<String, Object> infoMap,
            final SGPlotTypeSelectionWizardDialog dialog) {
        infoMap.put(SGPlotTypeConstants.KEY_PLOT_TYPE_SELECTED,
                Boolean.TRUE);
        infoMap.put(SGPlotTypeConstants.KEY_PLOT_TYPE_LINE,
                Boolean.valueOf(dialog.isLineSelected()));
        infoMap.put(SGPlotTypeConstants.KEY_PLOT_TYPE_SYMBOL,
                Boolean.valueOf(dialog.isSymbolSelected()));
        infoMap.put(SGPlotTypeConstants.KEY_PLOT_TYPE_BAR,
                Boolean.valueOf(dialog.isBarSelected()));
        infoMap.put(SGPlotTypeConstants.KEY_PLOT_TYPE_ERRORBAR_ONLINE,
                Boolean.valueOf(dialog.isErrorBarPlaceLineSymbolSelected()));
        infoMap.put(SGPlotTypeConstants.KEY_PLOT_TYPE_LINE_COLOR_AUTO_ASSIGNED, 
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
                    this.mPluginManager.doOutputToFile(wnd, (SGData)obj);
                } catch (Exception e) {
                    SGUtility.showErrorMessageDialog(wnd, "Failed to output data to file", 
                    		SGIConstants.TITLE_ERROR);
                }
            }
        }
    }

    /**
     * Reload all data objects in a given window.
     *
     * @param wnd
     *           the window
     * @param showStatus
     *           true to show the status
     * @return true if succeeded to reload all data
     */
    public boolean reloadData(final SGDrawingWindow wnd,
    		final boolean showStatus) {

    	// get all figures and data
    	List<SGFigure> figureList = wnd.getVisibleFigureList();
    	DataList dl = this.getVisibleDataList(figureList);
    	List<SGData> dataList = dl.dataList;
    	Map<SGData, SGFigure> dataFigureMap = dl.figureMap;
    	
    	// get data path and data source
		Set<String> dataPathSet = new HashSet<String>();	// to prevent duplication
		Map<String, SGIDataSource> srcMap = new HashMap<String, SGIDataSource>();
		for (SGData data: dataList) {
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
		for (SGData data: dataList) {
			SGFigure figure = dataFigureMap.get(data);
			if (SGDataUtility.isSDArrayData(data)) {
				String path = data.getPath();
				SGIFigureElementGraph gElement = figure.getGraphElement();
		        SGDataColumnInfo[] cols = gElement.getDataColumnInfoArray(data);
		        SGDataColumnInfoSet colInfoSet = new SGDataColumnInfoSet(cols);
				Map<String, Object> infoMap = data.getInfoMap();
				SGIDataSource srcNew = null;
				try {
					srcNew = SGApplicationUtility.createDataSource(path,
							colInfoSet, infoMap);
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
                    SGUtility.showMessageDialog(wnd, p, SGIConstants.TITLE_ERROR, 
                    		JOptionPane.ERROR_MESSAGE);
        		}
    			return false;
        	}
    		
    	} finally {
    		// repaint data viewer dialogs
    		for (SGDataViewerDialog dg : this.mDataViewerDialogArray) {
    			SGData data = dg.getData();
    			for (SGFigure f : figureList) {
    				SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) f.getGraphElement().getChild(data);
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

    /**
     * The results of reloading data.
     *
     */
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

    	this.createDataAdditionWizardDialogs(wnd);

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
        this.setupDataAdditionWizardDialogConnection(this.mDataTypeWizardDialog, 
        		DATA_ADDITION_DRAG_AND_DROP, FILE_TYPE.HDF5_DATA, true);
        
        this.mFileTypeSelectionWizardDialog.setCenter(wnd);
        this.mFileTypeSelectionWizardDialog.setVisible(true);

    	return true;
    }

    private boolean onMATLABDataFilesDropped(
            final List<File> fileList, final SGDrawingWindow wnd, final Point pos) {
    	
    	this.createDataAdditionWizardDialogs(wnd);
    	
    	this.onDataFileDropped(pos, wnd, fileList, FILE_TYPE.MATLAB_DATA, 
    			this.mMDArrayDataSetupWizardDialog);

        if (this.toNetCDFOrMDArrayDataTypeDialog(wnd,
                this.mDataTypeWizardDialog,
                this.mDroppedDataFile.file) == false) {
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
    	this.createDataAdditionWizardDialogs(wnd);
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
     * @param rect
     *           a rectangle to set
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
    
    private boolean addDataByPlugin(SGWizardDialog dg) {

        // set invisible the dialog
        dg.setVisible(false);

        SGDrawingWindow wnd = dg.getOwnerWindow();

        // figure id
        final int figureID = this.mFigureIDSelectionWizardDialog.getFigureID();

		if (this.drawNewGraphOfMDArrayData(wnd, dg,
				this.mDataTypeWizardDialog,
				this.mMDArrayDataSetupWizardDialog, null, figureID, null) == false) {
			return false;
		}

        wnd.notifyToRoot();

        return true;
    }
    
    void showPluginDetailDialog(SGDrawingWindow wnd) {
    	this.mNativePluginManager.showPluginInfoDialog(wnd);
    }

    private FILE_TYPE getNetCDF4orHDF5FileType(File file) {
    	String ext = SGApplicationUtility.getExtension(file);
		FILE_TYPE fileType = NETCDF_FILE_EXTENSION
				.equalsIgnoreCase(ext) ? FILE_TYPE.NETCDF_DATA : FILE_TYPE.HDF5_DATA;
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
    	PROPERTIES, COMMAND_SCRIPT,
    };
    
    /**
     * Saves properties or script into global attributes of data source file of selected data.
     * 
     */
    void saveIntoGlobalAttributes(SGDrawingWindow wnd, SAVED_OBJECT_TYPE objType) {
    	
    	// get visible figures and focused NetCDF data objects in them
    	List<SGFigure> visibleFigureList = wnd.getVisibleFigureList();
    	List<SGData> focusedDataList = new ArrayList<SGData>();
    	Map<SGData, SGFigure> dataFigureMap = new HashMap<SGData, SGFigure>();
    	for (SGFigure figure : visibleFigureList) {
    		List<SGData> dataList = figure.getFocusedDataList();
    		for (SGData data : dataList) {
    			if (SGDataUtility.isNetCDFData(data)) {
        			focusedDataList.add(data);
        			dataFigureMap.put(data, figure);
    			}
    		}
    	}
    	if (focusedDataList.size() == 0) {
            SGUtility.showErrorMessageDialog(wnd, 
            		"Data objects originated from NetCDF files are not selected.", 
            		SGIConstants.TITLE_ERROR);
    		return;
    	}

    	// get file paths without duplication
		Set<String> dataPathSet = new HashSet<String>();
		for (SGData data: focusedDataList) {
			String path = data.getPath();
			dataPathSet.add(path);
		}

		// make a map of data sources
		Map<String, SGIDataSource> dataSrcMap = new HashMap<String, SGIDataSource>();
		for (SGData data: focusedDataList) {
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
			SGDataExportParameter params = new SGDataExportParameter(
					OPERATION.SAVE_INTO_FILE_ATTRIBUTE, dataList);
			
			// creates a string to be saved
			String savedString = null;
			String attrName = null;
			if (SAVED_OBJECT_TYPE.PROPERTIES.equals(objType)) {
				try {
					savedString = SGApplicationUtility.getPropertyString(wnd, params, 
							this.mAppProp.getVersionString());
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
				e1.printStackTrace();
			}
			
			// adds global attributes
			NetcdfFileWriteable ncWrite = null;
	        try {
	        	ncWrite = NetcdfFileWriteable.openExisting(path);
	        	final Attribute curAttr = ncWrite.findGlobalAttribute(attrName);
	        	ncWrite.setRedefineMode(true);
	        	if (curAttr != null) {
	        		ncWrite.deleteGlobalAttribute(attrName);
	        	}
	        	ncWrite.addGlobalAttribute(attrName, savedString);
	        	ncWrite.setRedefineMode(false);
	        	Attribute attr = new Attribute(attrName, savedString);
	        	ncWrite.updateAttribute(null, attr);
	        } catch (IOException ioe) {
			} finally {
	        	if (ncWrite != null) {
		        	try {
						ncWrite.close();
					} catch (IOException e) {
					}
	        	}
	        }
	        IHDF5Writer hdfWriter = null;
			if (ncWrite == null) {
				// updates the attribute
		        try {
			        hdfWriter = HDF5FactoryProvider.get().open(new File(path));
			        hdfWriter.setStringAttribute("/", attrName, savedString);
		        } catch (HDF5Exception e) {
		        	e.printStackTrace();
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
			SGUtility.showMessageDialog(wnd, 
					"Succeeded to add commands to NetCDF files.", 
					SGIConstants.TITLE_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
		}
    }
    
    private static void showErrMsgDialogSavingNetCDFAttribute(
    		SGDrawingWindow wnd, String path) {
        SGUtility.showErrorMessageDialog(wnd, 
        		"Failed to add commands to a NetCDF file: \n" + path, 
        		SGIConstants.TITLE_ERROR);
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
            SGUtility.showErrorMessageDialog(wnd,
                    "Invalid data is selected for animation.", ERROR);
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
            SGUtility.showErrorMessageDialog(wnd,
                    "Frame number is different.", ERROR);
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
            SGUtility.showErrorMessageDialog(wnd,
                    "Selected data is already assigned to an animation dialog.", ERROR);
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
    	SGIDataAnimation[] animations = animationList.toArray(
    			new SGIDataAnimation[animationList.size()]);
    	for (SGIDataAnimation animation : animations) {
    		animation.prepareForChanges();
    	}
        dg.setAnimation(animations);
		dg.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				onAnimationDialogCancelAndClosed(dg);
				
				// repaint focused symbols
				repaintFocusedSymbols(dg);
			}

		});
		dg.addActionListener(new ActionListener() {
			
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
	        			SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) f.getGraphElement().getChild(viewerData);
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
    	SwingUtilities.invokeLater(new Runnable() {
    		
    		@Override
    		public void run() {
    			
				onDataAnimationDialogClosing(dg);
				
		        // updates the current directory
		        setCurrentFileDirectory(dg.getFolderPath());
    		}
    		
    	});
    }

    private void onAnimationDialogSaveAndClosed(final SGDataAnimationDialog dg) {
    	SwingUtilities.invokeLater(new Runnable() {
    		
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

    /**
     * The array of dialogs to setup the animation of data objects.
     */
    SGDataAnimationDialog[] mDataAnimationDialogArray = new SGDataAnimationDialog[0];

    /**
     * The array of data viewer dialogs.
     */
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
    		dg.addTableSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					setDataSelectedValues(dg, figureIdMap);
				}
    			
    		});
    		dg.addTableMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
	                if (SwingUtilities.isLeftMouseButton(e)) {
						setDataSelectedValues(dg, figureIdMap);
	                }
				}
				
    		});
    		dg.addTableMouseMotionListener(new MouseMotionAdapter() {

				@Override
				public void mouseDragged(MouseEvent e) {
					setDataSelectedValues(dg, figureIdMap);
				}

    		});
    		dg.addHilightChangedActionListener(new ActionListener() {

    			@Override
    			public void actionPerformed(ActionEvent e) {
					setDataSelectedValues(dg, figureIdMap);
    			}
    			
    		});
    		dg.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
			    	SwingUtilities.invokeLater(new Runnable() {
			    		public void run() {
		    				onDataViewerClosing(dg, figureIdMap);
			    		}
			    	});
				}

    		});
    		dg.addActionListener(new ActionListener() {
    			
    			@Override
    			public void actionPerformed(ActionEvent e) {
			    	SwingUtilities.invokeLater(new Runnable() {
			    		
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
    
    private void onDataViewerClosing(SGDataViewerDialog dg,
    		Map<SGData, Integer> figureIdMap) {
    	
    	SGDrawingWindow wnd = dg.getOwnerWindow();
    	SGData data = dg.getData();
    	
    	// clear focused values
		Integer figureId = figureIdMap.get(data);
		SGFigure figure = wnd.getFigure(figureId);
		SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) figure.getGraphElement().getChild(data);
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
    		SGDataAnimationDialog[] array = new SGDataAnimationDialog[this.mDataAnimationDialogArray.length - 1];
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
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			SGDrawingWindow wnd = dg.getOwnerWindow();
    			SGData data = dg.getData();
    			Integer figureId = figureIdMap.get(data);
    			SGFigure figure = wnd.getFigure(figureId);
    			SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) figure.getGraphElement().getChild(data);
    			gs.setFocusedValueIndices(dg);
    			wnd.repaint();
    		}
    	});
    }
    
    /**
     * Fits axis range to the focused data.
     */
    SGStatus fitAxisRangeToFocusedData(final SGDrawingWindow wnd, final String command) {
    	SGStatus result = null;
        if (!USE_FOXTROT) {
            fitAxisRangeToFocusedDataSub(wnd, command);
            result = new SGStatus(true);
        } else {
            try {
                result = (SGStatus) Worker.post(new Task() {
                    public Object run() throws Exception {
                        fitAxisRangeToFocusedDataSub(wnd, command);
                        return new SGStatus(true);
                    }
                });
            } catch (Exception ex) {
            	result = new SGStatus(false);
                ex.printStackTrace();
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
        DataList dl = this.getVisibleDataList(focusedFigureList);
        List<SGData> dataList = dl.dataList;
        Map<SGData, SGFigure> figureMap = dl.figureMap;
        this.closeDataViewerDialogsSub(dataList, figureMap, wnd);
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
        this.closeDataViewerDialogsSub(focusedDataList, figureMap, wnd);
    }

    void closeDataViewerDialogsInFocusedFigures(SGDrawingWindow wnd) {
    	if (this.mDataViewerDialogArray.length == 0) {
    		return;
    	}
        List<SGFigure> focusedFigureList = wnd.getFocusedFigureList();
        DataList dl = this.getVisibleDataList(focusedFigureList);
        List<SGData> dataList = dl.dataList;
        Map<SGData, SGFigure> figureMap = dl.figureMap;
        this.closeDataViewerDialogsSub(dataList, figureMap, wnd);
    }
    
    void closeDataViewerDialogInAllFigures(SGDrawingWindow wnd, final boolean bUndo) {
    	if (this.mDataViewerDialogArray.length == 0) {
    		return;
    	}
        List<SGFigure> focusedFigureList = wnd.getVisibleFigureList();
        DataList dl = this.getVisibleDataList(focusedFigureList);
        List<SGData> dataList = dl.dataList;
        Map<SGData, SGFigure> figureMap = dl.figureMap;
        this.closeDataViewerDialogsSub(dataList, figureMap, wnd, bUndo);
    }

    private void closeDataViewerDialogsSub(List<SGData> dataList, 
    		Map<SGData, SGFigure> figureMap, SGDrawingWindow wnd) {
    	if (this.mDataViewerDialogArray.length == 0) {
    		return;
    	}
        List<SGDataViewerDialog> dgList = new ArrayList<SGDataViewerDialog>();
        for (SGDataViewerDialog dg : this.mDataViewerDialogArray) {
        	SGData data = dg.getData();
        	if (dataList.contains(data)) {
        		dg.setVisible(false);
        		SGFigure figure = figureMap.get(data);
        		SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) figure.getGraphElement().getChild(data);
        		gs.removeFocusedValueList(dg);
        	} else {
        		dgList.add(dg);
        	}
        }
        this.mDataViewerDialogArray = dgList.toArray(new SGDataViewerDialog[dgList.size()]);;
    }

    private void closeDataViewerDialogsSub(List<SGData> dataList, 
    		Map<SGData, SGFigure> figureMap, SGDrawingWindow wnd,
    		final boolean bUndo) {
    	if (this.mDataViewerDialogArray.length == 0) {
    		return;
    	}
        List<SGDataViewerDialog> dgList = new ArrayList<SGDataViewerDialog>();
        for (SGDataViewerDialog dg : this.mDataViewerDialogArray) {
        	SGData data = dg.getData();
    		boolean bClose = false;
        	if (dataList.contains(data)) {
        		SGFigure figure = figureMap.get(data);
        		SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) figure.getGraphElement().getChild(data);
        		if (!wnd.existsOnUndo(bUndo, figure, gs.getData())) {
        			bClose = true;
        		}
        		if (bClose) {
            		gs.removeFocusedValueList(dg);
            		dg.setVisible(false);
        		}
        	}
        	if (!bClose) {
        		dgList.add(dg);
        	}
        }
        this.mDataViewerDialogArray = dgList.toArray(new SGDataViewerDialog[dgList.size()]);
    }

    void closeAllDataAnimationDialogs(SGDrawingWindow wnd) {
    	if (this.mDataAnimationDialogArray.length == 0) {
    		return;
    	}
        List<SGFigure> visibleFigureList = wnd.getVisibleFigureList();
        DataList dl = this.getVisibleDataList(visibleFigureList);
        List<SGData> dataList = dl.dataList;
        this.closeDataAnimationDialogsSub(dataList);
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
        this.closeDataAnimationDialogsSub(focusedDataList);
    }

    void closeDataAnimationDialogsInFocusedFigures(SGDrawingWindow wnd) {
    	if (this.mDataAnimationDialogArray.length == 0) {
    		return;
    	}
        List<SGFigure> focusedFigureList = wnd.getFocusedFigureList();
        DataList dl = this.getVisibleDataList(focusedFigureList);
        List<SGData> dataList = dl.dataList;
        this.closeDataAnimationDialogsSub(dataList);
    }

    void closeDataAnimationDialogInAllFigures(SGDrawingWindow wnd, final boolean bUndo) {
    	if (this.mDataAnimationDialogArray.length == 0) {
    		return;
    	}
        List<SGFigure> focusedFigureList = wnd.getVisibleFigureList();
        DataList dl = this.getVisibleDataList(focusedFigureList);
        List<SGData> dataList = dl.dataList;
        Map<SGData, SGFigure> figureMap = dl.figureMap;
        this.closeDataAnimationDialogsSub(dataList, figureMap, wnd, bUndo);
    }

    private void closeDataAnimationDialogsSub(List<SGData> removedDataList) {
    	if (this.mDataAnimationDialogArray.length == 0) {
    		return;
    	}
        List<SGDataAnimationDialog> dgList = new ArrayList<SGDataAnimationDialog>();
        for (SGDataAnimationDialog dg : this.mDataAnimationDialogArray) {
        	SGData[] animationDataArray = dg.getDataArray();
        	boolean found = false;
        	for (SGData animationData : animationDataArray) {
            	if (removedDataList.contains(animationData)) {
            		found = true;
            		break;
            	}
        	}
        	if (found) {
        		dg.close();
        	} else {
        		dgList.add(dg);
        	}
        }
        this.mDataAnimationDialogArray = dgList.toArray(new SGDataAnimationDialog[dgList.size()]);;
    }

    private void closeDataAnimationDialogsSub(List<SGData> removedDataList,
    		Map<SGData, SGFigure> figureMap, SGDrawingWindow wnd,
    		final boolean bUndo) {
    	if (this.mDataAnimationDialogArray.length == 0) {
    		return;
    	}
        List<SGDataAnimationDialog> dgList = new ArrayList<SGDataAnimationDialog>();
        for (SGDataAnimationDialog dg : this.mDataAnimationDialogArray) {
        	SGData[] animationDataArray = dg.getDataArray();
    		boolean bClose = false;
        	SGData rData = null;
        	for (SGData animationData : animationDataArray) {
            	if (removedDataList.contains(animationData)) {
            		rData = animationData;
            		break;
            	}
        	}
        	if (rData != null) {
        		SGFigure figure = figureMap.get(rData);
        		SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) figure.getGraphElement().getChild(rData);
        		if (!wnd.existsOnUndo(bUndo, figure, gs.getData())) {
        			bClose = true;
        		}
        	}
    		if (bClose) {
        		dg.close();
    		} else {
        		dgList.add(dg);
        	}
        }
        this.mDataAnimationDialogArray = dgList.toArray(new SGDataAnimationDialog[dgList.size()]);;
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
        DataList dl = this.getVisibleDataList(focusedFigureList);
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
        	List<SGTwoDimensionalArrayIndex> indexList = gElement.getSelectedDataIndexList(
        			data, columnType);
        	dg.setSelectedIndices(indexList);
        }
    }
    
    static class DataList {
    	List<SGData> dataList = new ArrayList<SGData>();
    	Map<SGData, SGFigure> figureMap = new HashMap<SGData, SGFigure>();
    }
    
    DataList getVisibleDataList(List<SGFigure> figureList) {
    	DataList ret = new DataList();
        List<SGData> dataList = new ArrayList<SGData>();
        Map<SGData, SGFigure> figureMap = new HashMap<SGData, SGFigure>();
        for (SGFigure f : figureList) {
        	List<SGData> dList = f.getVisibleDataList();
        	dataList.addAll(dList);
        	for (SGData data : dList) {
            	figureMap.put(data, f);
        	}
        }
        ret.dataList = dataList;
        ret.figureMap = figureMap;
    	return ret;
    }

    void closeAllModelessDialogs(SGDrawingWindow wnd) {
        // close all data viewer and animation dialogs
        this.closeAllDataAnimationDialogs(wnd);
        this.closeAllDataViewerDialogs(wnd);
    }
}

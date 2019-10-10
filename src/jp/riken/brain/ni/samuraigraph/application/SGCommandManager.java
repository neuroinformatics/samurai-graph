package jp.riken.brain.ni.samuraigraph.application;

import java.awt.EventQueue;
import java.awt.Point;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxisBreak;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementLegend;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementShape;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementSignificantDifference;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementString;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementTimingLine;
import jp.riken.brain.ni.samuraigraph.base.SGIRootObjectConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGStyle;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGHDF5File;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGMATLABFile;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import jp.riken.brain.ni.samuraigraph.figure.SGColorBarColorMapManager;
import jp.riken.brain.ni.samuraigraph.figure.SGIAxisBreakConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIAxisConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIColorBarConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGILegendConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIScaleConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIShapeConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISignificantDifferenceConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIStringConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGITimingLineConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyle;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyleColorMapManager;
import jp.riken.brain.ni.samuraigraph.figure.SGUtilityForFigureElement;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGIElementGroupSetMultipleSXY;

/**
 * The property manager.
 * 
 */
class SGCommandManager implements SGIApplicationCommandConstants,
		SGIDataColumnTypeConstants, SGILegendConstants, SGIStringConstants,
		SGISignificantDifferenceConstants, SGIAxisBreakConstants,
		SGITimingLineConstants, SGIShapeConstants {

	private SGMainFunctions mMain = null;

	private SGWindowManager mWindowManager = null;
	
    SGCommandManager(final SGMainFunctions main) {
    	super();
    	this.mMain = main;
    	this.mWindowManager = main.mWindowManager;
    }

    private SGPropertyMap getPropertiesMap(String command, List<String> strList) {
    	SGPropertyMap map = new SGPropertyMap();
        for (String str : strList) {
            final int eqIndex = str.indexOf('=');
            if (eqIndex == -1 || eqIndex == 0) {
                return null;
            }
            final int len = str.length();
            final String key = str.substring(0, eqIndex);
            String value = (eqIndex == len - 1) ? "" : str
                    .substring(eqIndex + 1, len);
            
            // Searches the alias objects.
        	Alias alias = this.mAliasMap.get(value);
            if (alias != null) {
            	String aCommand = alias.getCommand();
            	if (aCommand == null || command.equalsIgnoreCase(aCommand)) {
            		// When the command of the alias is null, the alias is applied to all commands.
            		String aKey = alias.getKey();
            		if (aKey == null || key.equalsIgnoreCase(aKey)) {
                		// When the property key of the alias is null, the alias is applied to 
            			// all properties.
            			if (alias.getRepr().equals(value)) {
            				// Replaces the value.
            				value = alias.getValue();
            			}
            		}
            	}
            }
            
            map.putValue(key, value);
        }
        return map;
    }
    
    private boolean isCommandBringToFront(final String command) {
        return command.toUpperCase().startsWith(COM_HEADER_BRING_TO_FRONT.toUpperCase());
    }

    private boolean isCommandBringForward(final String command) {
        return command.toUpperCase().startsWith(COM_HEADER_BRING_FORWARD.toUpperCase());
    }

    private boolean isCommandSendBackward(final String command) {
        return command.toUpperCase().startsWith(COM_HEADER_SEND_BACKWARD.toUpperCase());
    }

    private boolean isCommandSendToBack(final String command) {
        return command.toUpperCase().startsWith(COM_HEADER_SEND_TO_BACK.toUpperCase());
    }
    
    private String getCommandEndToMove(final String command, final String start) {
        return command.substring(start.length(), command.length());
    }
    
    private SGIFigureElement getFigureElementToMove(final String name, final SGFigure f) {
        final String uc = name.toUpperCase();
        Class<? extends SGIFigureElement> cl = null;
        if (uc.endsWith(SGIDataCommandConstants.COM_DATA.toUpperCase())) {
            cl = SGIFigureElementGraph.class;
        } else if (uc.endsWith(SGIStringConstants.COM_LABEL.toUpperCase())) {
            cl = SGIFigureElementString.class;
        } else if (uc.endsWith(SGISignificantDifferenceConstants.COM_SIGNIFICANT_DIFFERENCE.toUpperCase())) {
            cl = SGIFigureElementSignificantDifference.class;
        } else if (uc.endsWith(SGIAxisBreakConstants.COM_AXIS_BREAK.toUpperCase())) {
            cl = SGIFigureElementAxisBreak.class;
        } else if (uc.endsWith(SGITimingLineConstants.COM_TIMING_LINE.toUpperCase())) {
            cl = SGIFigureElementTimingLine.class;
        } else if (uc.endsWith(SGIShapeConstants.COM_SHAPE.toUpperCase())) {
            cl = SGIFigureElementShape.class;
        } else {
            return null;
        }
        SGIFigureElement el = f.getIFigureElement(cl);
        return el;
    }

    private Integer getId(final List<String> argsList) {
        if (argsList.size() == 0) {
            return null;
        }
        String str = (String) argsList.get(0);
        final Integer id = SGUtilityText.getInteger(str);
        if (id == null) {
            return null;
        }
        if (id <= 0) {
        	return null;
        }
        if (!this.checkIdUpperRange(str)) {
        	return null;
        }
        return id;
    }
    
    private Integer getNewWindowId(final List<String> argsList, 
    		List<String> paramsList) {
    	final int argn = argsList.size();
    	// Window()
        if (argn == 0) {
        	// returns current window ID
        	SGDrawingWindow cur = this.mWindowManager.getCurrentWindow();
        	if (cur == null) {
        		return null;
        	}
        	return cur.getID();
        }
        String first = (String) argsList.get(0);
        final Integer id = SGUtilityText.getInteger(first);
        if (id == null) {
        	// ex. Window(BackgroundColor=RED, ...)
        	// returns current window ID
        	SGDrawingWindow cur = this.mWindowManager.getCurrentWindow();
        	if (cur == null) {
        		return null;
        	}
        	paramsList.addAll(argsList);
        	return cur.getID();
        }
        if (id == 0) {
        	// ex. Window(0), Window(0, BackgroundColor=RED, ...)
        	final int ret;
        	List<Integer> idList = this.mWindowManager.getWindowIdList();
        	if (idList.size() == 0) {
        		ret = 1;
        	} else {
        		Integer maxId = idList.get(idList.size() - 1);
        		ret = maxId + 1;
        	}
        	if (argn > 1) {
            	paramsList.addAll(argsList.subList(1, argn));
        	}
        	return ret;
        }
        if (id < 0) {
        	return null;
        }
        if (!this.checkIdUpperRange(first)) {
        	return null;
        }
    	// ex. Windows(1), Window(1, BackgroundColor=RED, ...)
    	if (argn > 1) {
        	paramsList.addAll(argsList.subList(1, argn));
    	}
        return id;
    }
    
    private boolean checkIdUpperRange(final String str) {
		BigInteger bIntMax = new BigInteger(Integer.toString(Integer.MAX_VALUE));
		BigInteger bnum = new BigInteger(str);
		if (bnum.compareTo(bIntMax) > 0) {
			return false;
		}
		return true;
    }
    
    // parse given string into the command and arguments
    private String parseCommand(final String line, final List<String> argsList) {
        final int sIndex = line.indexOf('(');
        final int eIndex = line.lastIndexOf(')');
        if (sIndex == -1 || eIndex == -1) {
            return line;
        }
        if (sIndex > eIndex) {
            return line;
        }
        if (sIndex == 0) {
            return line;
        }
        String command = line.substring(0, sIndex);
        String args = line.substring(sIndex + 1, eIndex);
        final int len = args.length();
        if (len == 0) {
        	return command;
        }
        List<String> tokenList = SGUtilityText.tokenizeCommand(args);
        if (tokenList == null) {
        	return null;
        }
        argsList.addAll(tokenList);
        return command;
    }
    
    // A temporary attribute to store the returned value.
    private int mReturnedValue;

    /**
     * Execute a command.
     * 
     * @param line
     *            the command line
     * @return the status
     */
    int exec(final String line) {
    	
    	// parses the command
        final List<String> argsList = new ArrayList<String>();
        final String command = this.parseCommand(line, argsList);
        if (command == null) {
            return STATUS_FAILED;
        }

        // commands for global function
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    mReturnedValue = execGlobalCommand(
                            command, argsList);
                }
            });
        } catch (InterruptedException e1) {
        } catch (InvocationTargetException e1) {
        }
        if (mReturnedValue != STATUS_NOT_FOUND) {
            return mReturnedValue;
        }

        // commands for exporting an image
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    mReturnedValue = execImageCommand(
                            command, argsList);
                }
            });
        } catch (InterruptedException e1) {
        } catch (InvocationTargetException e1) {
        }
        if (mReturnedValue != STATUS_NOT_FOUND) {
            return mReturnedValue;
        }
        
        // commands for the windows
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    mReturnedValue = execWindowCommand(
                            command, argsList);
                }
            });
        } catch (InterruptedException e1) {
        } catch (InvocationTargetException e1) {
        }
        if (mReturnedValue != STATUS_NOT_FOUND) {
            return mReturnedValue;
        }
        
        // commands for the window menu bar
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    mReturnedValue = execMenuBarCommandFile(
                            command, argsList);
                }
            });
        } catch (InterruptedException e1) {
        } catch (InvocationTargetException e1) {
        }
        if (mReturnedValue != STATUS_NOT_FOUND) {
            return mReturnedValue;
        }

        // commands for the figures
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    mReturnedValue = execFigureCommand(
                            command, argsList);
                }
            });
        } catch (InterruptedException e1) {
        } catch (InvocationTargetException e1) {
        }
        if (mReturnedValue != STATUS_NOT_FOUND) {
            return mReturnedValue;
        }
        
        // commands for the figure elements
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    mReturnedValue = execFigureElementCommand(
                            command, argsList);
                }
            });
        } catch (InterruptedException e) {
        } catch (InvocationTargetException e) {
        }
        if (mReturnedValue != STATUS_NOT_FOUND) {
            return mReturnedValue;
        }
        
        return STATUS_NOT_FOUND;
    }

    // commands in the menu-bar File
    private int execMenuBarCommandFile(final String command, final List<String> argsList) {
        final int argn = argsList.size();
        final SGDrawingWindow wnd = this.mWindowManager.getCurrentWindow();
        if (wnd == null) {
            return STATUS_FAILED;
        }
        
/*    
        // load property
        if (COM_LOAD_PROPERTY.equalsIgnoreCase(command)) {
            if (argn < 1) {
                return STATUS_FAILED;
            }
            if (aWnd == null) {
                return STATUS_FAILED;
            }
            String str1 = (String) argsList.get(0); // file path

            File file = new File(str1);
            if (file.exists() == false) {
                return STATUS_FAILED;
            }
//            String path = SGUtility.getCanonicalPath(file.getPath());
//            if (path == null) {
//                return STATUS_FAILED;
//            }
//
//            // check validity of the file
//            URL url = null;
//            try {
//                url = file.toURI().toURL();
//            } catch (MalformedURLException ex) {
//                return STATUS_FAILED;
//            }
//
//            // create a Document object
//            Document doc = SGUtilityText.getDocument(url);
//            if (doc == null) {
//                return STATUS_FAILED;
//            }
//
//            //
//            Element elWnd = this.mPropertyFileManager.getWindowElement(doc);
//            NodeList nListFigure = elWnd
//                    .getElementsByTagName(SGFigure.TAG_NAME_FIGURE);
//            final int figureNum = nListFigure.getLength();
//            int cnt = 0;
//            final int[] dataNumArray = new int[figureNum];
//            for (int ii = 0; ii < figureNum; ii++) {
//                Node node = nListFigure.item(ii);
//                if ((node instanceof Element) == false) {
//                    return STATUS_FAILED;
//                }
//                Element elFigure = (Element) node;
//                NodeList nListData = elFigure
//                        .getElementsByTagName(SGIFigureElementGraph.TAG_NAME_DATA);
//                dataNumArray[ii] = nListData.getLength();
//                cnt += dataNumArray[ii];
//            }
//            if (cnt != argn - 1) {
//                return STATUS_FAILED;
//            }
//
//            // list of path-names of data files
//            ArrayList pathList = new ArrayList(argsList.subList(1, argn));
//
//            // create a data map
//            Map dataMap = new TreeMap();
//            cnt = 0;
//            for (int ii = 0; ii < figureNum; ii++) {
//                final int dataNum = dataNumArray[ii];
//                ArrayList dataList = new ArrayList();
//                for (int jj = 0; jj < dataNum; jj++) {
//                    dataList.add(pathList.get(cnt + jj));
//                }
//                dataMap.put(Integer.valueOf(ii), dataList);
//                cnt += dataNum;
//            }
//
//            // set properties
//            if (!this.mPropertyFileManager
//                    .setPropertyFileFromEventDispatchThread(aWnd, doc, dataMap)) {
//                return STATUS_FAILED;
//            }

//            SGDrawingWindow wnd = this.mWindowManager.getActiveWindow();
//            if (this.mPropertyFileManager.showMultiDataFileChooserDialog(
//                    file, wnd) == false) {
//                return STATUS_FAILED;
//            }

            return STATUS_SUCCEEDED;
        }
        // save property
        else if (COM_SAVE_PROPERTY.equalsIgnoreCase(command)) {
            if (argn < 1) {
                return STATUS_FAILED;
            }
            if (aWnd == null) {
                return STATUS_FAILED;
            }
            if (aWnd.getVisibleFigureList().size() == 0) {
                return STATUS_FAILED;
            }
            final String str1 = (String) argsList.get(0); // path
            final List<Integer> retList = new ArrayList<Integer>();
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        final int ret;
                        if (mPropertyFileManager
                                .saveProperties(aWnd, str1, SAVE_PROPERTIES_TO_PROPERTY_FILE) == OK_OPTION) {
                            ret = STATUS_SUCCEEDED;
                            aWnd.setSaved(true);
                        } else {
                            ret = STATUS_FAILED;
                            aWnd.setSaved(false);
                        }
                        retList.add(ret);
                    }
                });
            } catch (InterruptedException e) {
                return STATUS_FAILED;
            } catch (InvocationTargetException e) {
                return STATUS_FAILED;
            }
            return retList.get(0);
        }
*/
        // load data set
        if (COM_LOAD_DATA_SET.equalsIgnoreCase(command)) {
            if (argn < 1) {
                return STATUS_FAILED;
            }
            String str1 = (String) argsList.get(0); // path
            if (str1.startsWith("\"")) {
                str1 = str1.substring(1);
            }
            if (str1.endsWith("\"")) {
                str1 = str1.substring(0, str1.length()-1);
            }
            final File file = new File(str1);
            if (file.exists() == false) {
                return STATUS_FAILED;
            }
            
            // close all data viewer and animation dialogs
            this.mMain.closeAllModelessDialogs(wnd);

            if (SGCommandManager.this.mMain.mDataSetManager.loadDataSetFromEventDispatchThread(wnd, file) == false) {
                return STATUS_FAILED;
            } else {
                return STATUS_SUCCEEDED;
            }
        }
        // save data set
        else if (COM_SAVE_DATA_SET.equalsIgnoreCase(command)) {
            if (argn < 2) {
                return STATUS_FAILED;
            }
            if (wnd.getVisibleFigureList().size() == 0) {
                return STATUS_FAILED;
            }
            String filePath = null;
            String fileType = null;
            SGPropertyMap map = this.getPropertiesMap(command, argsList);
            if (map == null) {
                return STATUS_FAILED;
            }
            Iterator<String> itr = map.getKeyIterator();
            while (itr.hasNext()) {
                String key = itr.next();
                if (COM_SAVE_FILE_PATH.equalsIgnoreCase(key)) {
                    filePath = map.getValueString(key);
                } else if (COM_SAVE_TYPE.equalsIgnoreCase(key)) {
                    fileType = map.getValueString(key);
                }
            }
            final String[] archiveTypes = {
                    SGIArchiveFileConstants.ARCHIVE_FILE_TYPE_SGA,
                    SGIArchiveFileConstants.ARCHIVE_FILE_TYPE_SGA107,
//                    SGIArchiveFileConstants.ARCHIVE_FILETYPE_NETCDF
            };
            fileType = SGApplicationUtility.getExactTypeString(archiveTypes, fileType);
            if (filePath == null || fileType == null) {
                return STATUS_FAILED;
            }
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                return STATUS_FAILED;
            }
            String name = file.getName();
            name = SGApplicationUtility.removeExtension(name);
            if (!SGApplicationUtility.checkOutputFileName(name)) {
                return STATUS_FAILED;
            }
            if (SGCommandManager.this.mMain.mDataSetManager.saveDataSet(wnd, filePath, fileType) == OK_OPTION) {
                wnd.setSaved(true);
                return STATUS_SUCCEEDED;
            } else {
                wnd.setSaved(false);
                return STATUS_FAILED;
            }
        }
        // reloading data
        else if (COM_RELOAD_DATA.equalsIgnoreCase(command)) {
        	if (this.mMain.reloadData(wnd, false) == false) {
        		return STATUS_FAILED;
        	}
            return STATUS_SUCCEEDED;
        }
        // exit
        else if (COM_EXIT.equalsIgnoreCase(command)) {
//            if (argn != 0) {
//                return STATUS_FAILED;
//            }
            this.mWindowManager.closeAllWindowWithoutConfirmation();
            return STATUS_SUCCEEDED;
        }

        return STATUS_NOT_FOUND;
    }
        
    /**
     * Executes a command for a window.
     * 
     * @param command
     *           a command
     * @param argsList
     *           the list of arguments
     * @return the status
     */
    private int execWindowCommand(final String command, 
            final List<String> argsList) {
        
        if (SGIRootObjectConstants.COM_WINDOW.equalsIgnoreCase(command)) {
            final List<String> paramsList = new ArrayList<String>();
            Integer wndId = this.getNewWindowId(argsList, paramsList);
            if (wndId == null) {
            	return STATUS_FAILED;
            }

            // create a new window if it does not exist
            if (mWindowManager.getWindow(wndId) == null) {
                SGDrawingWindow w = mWindowManager.createNewWindow(wndId);
                if (w == null) {
                    return STATUS_FAILED;
                }
                w.setVisible(true);
            }
            
            // sets the current window
            if (mWindowManager.setCurrentWindow(wndId) == false) {
                return STATUS_FAILED;
            }
            SGDrawingWindow wnd = mWindowManager.getCurrentWindow();
            
            SGPropertyMap map = this.getPropertiesMap(command, paramsList);
            if (map == null) {
                return STATUS_FAILED;
            }

            SGPropertyResults result = wnd.setProperties(map);
            if (result == null) {
                return STATUS_FAILED;
            }
            
            // set the background image
            String imageFilePath = map.getValueString(COM_IMAGE_FILE_PATH);
//            if (SGIConstants.FILE_PATH_CONFIRMATION_SYMBOL.equals(imageFilePath)) {
//            	// set up file chooser dialog
//            	SGImageFileChooserWizardDialog fileChooserDialog
//            			= new SGImageFileChooserWizardDialog(wnd, true);
//            	fileChooserDialog.setCurrentFile(this.mMain.getCurrentFileDirectory(), null);
//            	fileChooserDialog.setCenter(wnd);
//            	fileChooserDialog.setVisible(true);
//            	
//            	// get file path from file chooser dialog
//            	String fileName = fileChooserDialog.getFileName();
//        		this.mMain.updateCurrentFile(new File(fileName), null);
//            	imageFilePath = fileName;
//            }
            if (!"".equals(imageFilePath)) {
            	final int imageStatus = this.setBackgroundImage(imageFilePath, wnd);
            	result.putResult(COM_IMAGE_FILE_PATH, imageStatus);
            }

            // after setting properties
            return this.afterSetProperties(map, result);
            
        } else if (COM_CLOSE_WINDOW.equalsIgnoreCase(command)) {
            Integer wndId = this.getId(argsList);
            if (wndId == null) {
                return STATUS_FAILED;
            }
            if (this.mMain.closeWindowWithoutConfirmation(wndId) == false) {
                return STATUS_FAILED;
            }
            return STATUS_SUCCEEDED;
        }

        return STATUS_NOT_FOUND;
    }
    
    private int setBackgroundImage(String imageFilePath, SGDrawingWindow wnd) {
        // set the background image
        File imageFile = new File(imageFilePath);
        if (!imageFile.exists()) {
            return SGPropertyResults.INVALID_INPUT_VALUE;
        }
        
        // add image to the window
    	byte[] imageByteArray = SGApplicationUtility.toByteArray(imageFile);
    	String ext = SGApplicationUtility.getImageExtension(imageFile);
    	if (ext == null) {
            return SGPropertyResults.INVALID_INPUT_VALUE;
    	}
        if (wnd.setImage(imageByteArray, ext, true) == false) {
            return SGPropertyResults.INVALID_INPUT_VALUE;
        }
        wnd.setImageFilePath(imageFilePath);
        return SGPropertyResults.SUCCEEDED;
    }

    /**
     * Executes a command for a figure.
     * 
     * @param command
     *           a command
     * @param argsList
     *           the list of arguments
     * @return the status
     */
    private int execFigureCommand(final String command, 
            final List<String> argsList) {
        
        final int argn = argsList.size();
        SGDrawingWindow wnd = mWindowManager.getCurrentWindow();
        if (wnd == null) {
            return STATUS_FAILED;
        }

        if (SGIFigureConstants.COM_FIGURE.equalsIgnoreCase(command)) {
            Integer figureId = this.getId(argsList);
            if (figureId == null) {
                return STATUS_FAILED;
            }
            final List<String> paramsList = new ArrayList<String>(
                    argsList.subList(1, argn));

            // create a new figure if it does not exist
            SGFigure figure = wnd.getFigure(figureId);
            if (figure == null) {
                figure = this.mMain.mFigureCreator.createEmptyFigure(figureId, wnd);
                if (figure == null) {
                    wnd.endProgress();
                    return STATUS_FAILED;
                }

                // add a new figure to the window
                if (wnd.addFigure(figure, new Point()) == false) {
                    return STATUS_FAILED;
                }
                figure.setFigureX(DEFAULT_FIGURE_X, FIGURE_LOCATION_UNIT);
                figure.setFigureY(DEFAULT_FIGURE_Y, FIGURE_LOCATION_UNIT);

                // set visible the figure
                figure.setVisible(true);

                // init the history of the properties
                SGIFigureElement[] array = figure.getIFigureElementArray();
                for (int ii = 0; ii < array.length; ii++) {
                    if (array[ii].initPropertiesHistory() == false) {
                        wnd.endProgress();
                        return STATUS_FAILED;
                    }
                }
                figure.initPropertiesHistory();

                // update the items after the figure is set visible
                wnd.updateItemsByFigureNumbers();

                wnd.setChanged(true);
            }
            
            // sets the current figure
            if (mWindowManager.setCurrentFigure(wnd.getID(), figure) == false) {
                return STATUS_FAILED;
            }
            
            SGPropertyMap map = this.getPropertiesMap(command, paramsList);
            if (map == null) {
                return STATUS_FAILED;
            }
            SGPropertyResults result = figure.setProperties(map);
            if (result == null) {
                return STATUS_FAILED;
            }

            // after setting properties
            return this.afterSetProperties(map, result);
            
        } else if (COM_BRING_TO_FRONT_FIGURE.equalsIgnoreCase(command)) {
            Integer figureId = this.getId(argsList);
            if (figureId == null) {
                return STATUS_FAILED;
            }
            if (wnd.moveFigureToEnd(figureId, true) == false) {
                return STATUS_FAILED;
            }
            return STATUS_SUCCEEDED;
        } else if (COM_BRING_FORWARD_FIGURE.equalsIgnoreCase(command)) {
            Integer figureId = this.getId(argsList);
            if (figureId == null) {
                return STATUS_FAILED;
            }
            if (wnd.moveFigure(figureId, true) == false) {
                return STATUS_FAILED;
            }
            return STATUS_SUCCEEDED;
        } else if (COM_SEND_BACKWARD_FIGURE.equalsIgnoreCase(command)) {
            Integer figureId = this.getId(argsList);
            if (figureId == null) {
                return STATUS_FAILED;
            }
            if (wnd.moveFigure(figureId, false) == false) {
                return STATUS_FAILED;
            }
            return STATUS_SUCCEEDED;
        } else if (COM_SEND_TO_BACK_FIGURE.equalsIgnoreCase(command)) {
            Integer figureId = this.getId(argsList);
            if (figureId == null) {
                return STATUS_FAILED;
            }
            if (wnd.moveFigureToEnd(figureId, false) == false) {
                return STATUS_FAILED;
            }
            return STATUS_SUCCEEDED;
        }

        return STATUS_NOT_FOUND;
    }
    
    /**
     * Executes a command for a figure elements.
     * 
     * @param command
     *           a command
     * @param argsList
     *           the list of arguments
     * @return the status
     */
    private int execFigureElementCommand(final String command, 
            List<String> argsList) {
        
        SGFigure figure = mWindowManager.getCurrentFigure();
        if (figure == null) {
            return STATUS_FAILED;
        }
        SGDrawingWindow wnd = figure.getWindow();
        
        if (SGIDataCommandConstants.COM_DATA.equalsIgnoreCase(command)) {
            // create a data object
            if (argsList.size() < 2) {
                return STATUS_FAILED;
            }
            Integer dataId = this.getId(argsList);
            if (dataId == null) {
                return STATUS_FAILED;
            }
            Integer childId = null;
            String colorMapName = null;
            
            final SGPropertyMap map;
            if (!figure.isDataVisible(dataId)) {
            	// if data object does not exist or is invisible, draw the new graph
                final List<String> paramsList = new ArrayList<String>(
                        argsList.subList(1, argsList.size()));
                map = this.getPropertiesMap(command, paramsList);
                if (map == null) {
                    return STATUS_FAILED;
                }
                
                // get data type
                String dataType = map.getValueString(COM_DATA_TYPE);
                final String[] dataTypes = {
                        SGDataTypeConstants.SXY_DATA, 
                        SGDataTypeConstants.SXY_MULTIPLE_DATA, 
                        SGDataTypeConstants.SXY_SAMPLING_DATA,
                        SGDataTypeConstants.VXY_DATA,
                        SGDataTypeConstants.SXY_DATE_DATA,
                        SGDataTypeConstants.SXYZ_DATA,
                        SGDataTypeConstants.SXY_NETCDF_DATA, 
                        SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA, 
                        SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA,
                        SGDataTypeConstants.VXY_NETCDF_DATA,
                        SGDataTypeConstants.SXYZ_NETCDF_DATA,
                        SGDataTypeConstants.SXY_HDF5_DATA, 
                        SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA, 
                        SGDataTypeConstants.VXY_HDF5_DATA,
                        SGDataTypeConstants.SXYZ_HDF5_DATA,
                        SGDataTypeConstants.SXY_MATLAB_DATA, 
                        SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA, 
                        SGDataTypeConstants.VXY_MATLAB_DATA,
                        SGDataTypeConstants.SXYZ_MATLAB_DATA
                };
                dataType = SGApplicationUtility.getExactTypeString(dataTypes, dataType);
                if (dataType == null) {
                    return STATUS_FAILED;
                }

                // check the data type
                if (SGDataUtility.isValidData(dataType) == false) {
                    return STATUS_FAILED;
                }
                final boolean isNetCDF = SGDataUtility.isNetCDFData(dataType);
                final boolean isHDF5 = SGDataUtility.isHDF5Data(dataType);
                final boolean isMATLAB = SGDataUtility.isMATLABData(dataType);

                // get file path
                String path = map.getValueString(COM_DATA_FILE_PATH);
                if (!"".equals(path)) {
                    if (map.isDoubleQuoted(SGIDataCommandConstants.COM_DATA_FILE_PATH) == false) {
                        return STATUS_FAILED;
                    }
                } else {
                	// create wizard dialogs
                	final SGCommandDataFileChooserWizardDialog fileChooserDialog 
                			= new SGCommandDataFileChooserWizardDialog(wnd, true);
            		
                	// initialize file chooser dialog
                	fileChooserDialog.setFigureId(Integer.toString(figure.getID()));
                	String dataName = map.getValueString(COM_DATA_NAME);
                	if (dataName != null) {
                    	fileChooserDialog.setDataName(dataName);
                	}
                	fileChooserDialog.setDataType(dataType);
                	fileChooserDialog.setCurrentFile(this.mMain.getCurrentFileDirectory(), null);
                	
                	// show the file chooser dialog
                	fileChooserDialog.setCenter(wnd);
                	fileChooserDialog.setVisible(true);

                	final int option = fileChooserDialog.getCloseOption();
                	if (option == CANCEL_OPTION) {
                        return STATUS_FAILED;
                	}
                	
                	// get file path from file chooser dialog
                	String fileName = fileChooserDialog.getFileName();
                	if (fileChooserDialog.isLocalFileSelected()) {
                		this.mMain.updateCurrentFile(new File(fileName), null);
                	}
            		path = fileName;
                }

                // get information map
                final Map<String, Object> infoMap = SGMainFunctions.createInfoMap(dataType, map);
                if (infoMap == null) {
                    return STATUS_FAILED;
                }

                // column information
                SGDataColumnInfoSet colInfoSet = null;
                if (isNetCDF) {
                	SGNetCDFFile nc = this.mMain.getNetCDFFile(path);
                    if (nc == null) {
                        return STATUS_FAILED;
                    }
                    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, nc);
                    colInfoSet = this.mMain.getNetCDFDefaultDataColumnInfo(nc,
                            dataType, infoMap);
                } else if (isHDF5) {
                	SGHDF5File hdf5File = this.mMain.getHDF5File(path);
                	if (hdf5File == null) {
                		return STATUS_FAILED;
                	}
                	infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, hdf5File);
                    colInfoSet = this.mMain.getMDArrayDataDefaultDataColumnInfo(hdf5File,
                            dataType, infoMap);
                } else if (isMATLAB) {
                	SGMATLABFile matFile = this.mMain.getMATLABFile(path);
                	if (matFile == null) {
                		return STATUS_FAILED;
                	}
                	infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, matFile);
                    colInfoSet = this.mMain.getMDArrayDataDefaultDataColumnInfo(matFile,
                            dataType, infoMap);
                } else {
                    colInfoSet = this.mMain.getSDArrayDefaultDataColumnInfo(path, dataType,
                            infoMap, false, null);
                }
                if (colInfoSet == null) {
                    return STATUS_FAILED;
                }

                // puts the figure size
                SGTuple2f size = new SGTuple2f(figure.getGraphRectWidth(), figure.getGraphRectHeight());
                infoMap.put(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE, size);

                // puts the grid flag
                if (SGDataUtility.isMDArrayData(dataType)) {
                	if (SGDataUtility.isSXYZTypeData(dataType) || SGDataUtility.isVXYTypeData(dataType)) {
                        SGDataColumnInfo[] colInfoArray = colInfoSet.getDataColumnInfoArray();
                        if (!SGDataUtility.addGridType(colInfoArray, dataType, infoMap)) {
                            return STATUS_FAILED;
                        }
                	}
                }
                
                // puts the default stride
                SGDataColumnInfo[] colArray = colInfoSet.getDataColumnInfoArray();
                Map<String, SGIntegerSeriesSet> strideMap = null;
                if (isNetCDF) {
                    strideMap = SGDataUtility.calcNetCDFDefaultStride(colArray, infoMap);
                } else if (isHDF5 || isMATLAB) {
                    strideMap = SGDataUtility.calcMDArrayDefaultStride(colArray, infoMap);
                } else {
                	strideMap = SGDataUtility.calcSDArrayDefaultStride(colArray, infoMap);
                }
                if (strideMap != null) {
                    infoMap.putAll(strideMap);
                }
                
                // puts the data name
                String dataNameBase = SGUtility.createDataNameBase(path);
                infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataNameBase);

                // draw the graph
                SGMainFunctions.DataSourceInfo dataSource = new SGMainFunctions.DataSourceInfo(path);
                SGStatus status = this.mMain.drawGraph(wnd, figure.getID(), colInfoSet, 
                		infoMap, dataSource, new Integer[] { dataId }, false, null);
                if (status.isSucceeded() == false) {
                    return STATUS_FAILED;
                }
                
                // updates the preferences for stride availability
            	String strStrideAvailable = map.getValue(COM_DATA_ARRAY_SECTION_AVAILABLE);
                if (strStrideAvailable != null) {
                	Boolean strideAvailable = SGUtilityText.getBoolean(strStrideAvailable);
                	if (strideAvailable != null) {
                		SGMainFunctions.putDataStrideAvailable(strideAvailable);
                	}
                }
            	
                // set the changed flag and notify to the root
                wnd.setChanged(true);
                wnd.notifyToRoot();
                
                // remove keys
                map.removeValue(SGIDataCommandConstants.COM_DATA_FILE_PATH);
                map.removeValue(SGIDataCommandConstants.COM_DATA_TYPE);
                map.removeValue(SGIDataCommandConstants.COM_DATA_SAMPLING_RATE);
                map.removeValue(SGIDataCommandConstants.COM_DATA_POLAR);
                
            } else {
            	// Data of given ID already exists.
            	SGData data = figure.getData(dataId);
            	List<String> argsSubList = new ArrayList<String>(argsList.subList(1, argsList.size()));
            	if (data instanceof SGISXYTypeMultipleData && argsSubList.size() > 1) {
            		// checks whether the second parameter is ID of a child data object
            		childId = this.getId(argsSubList);
            		if (childId != null) {
            			// ID of a child data object may be given
            			argsSubList = new ArrayList<String>(argsSubList.subList(1, argsSubList.size()));
            		} else {
            			// checks whether the second parameter is the name of a color map
            			String name = argsSubList.get(0);
            			if (SGLineStyleColorMapManager.isValidColorMapName(name)) {
                			colorMapName = name;
                			argsSubList = new ArrayList<String>(argsSubList.subList(1, argsSubList.size()));
            			}
            		}
            	}
                map = this.getPropertiesMap(command, argsSubList);
                if (map == null) {
                    return STATUS_FAILED;
                }
            }

            // sets data properties
            SGPropertyResults result;
            SGIFigureElementGraph gElement = figure.getGraphElement();
            if (childId != null) {
                result = gElement.setChildProperties(dataId, childId, map);
            } else if (colorMapName != null) {
            	result = gElement.setChildColorMapProperties(dataId, colorMapName, map);
            } else {
                result = gElement.setChildProperties(dataId, map);
            }
            if (result == null) {
                return STATUS_FAILED;
            }

            // after setting properties
            return this.afterSetProperties(map, result);
            
        } else if (this.isCommandBringToFront(command)) {
            Integer id = this.getId(argsList);
            if (id == null) {
                return STATUS_FAILED;
            }
            final String end = this.getCommandEndToMove(command, 
                    COM_HEADER_BRING_TO_FRONT);
            SGIFigureElement el = this.getFigureElementToMove(end, figure);
            if (el.moveChildToEnd(id.intValue(), true) == false) {
                return STATUS_FAILED;
            }
            return STATUS_SUCCEEDED;
        } else if (this.isCommandBringForward(command)) {
            Integer id = this.getId(argsList);
            if (id == null) {
                return STATUS_FAILED;
            }
            final String end = this.getCommandEndToMove(command, 
                    COM_HEADER_BRING_FORWARD);
            SGIFigureElement el = this.getFigureElementToMove(end, figure);
            if (el.moveChild(id.intValue(), true) == false) {
                return STATUS_FAILED;
            }
            return STATUS_SUCCEEDED;
        } else if (this.isCommandSendBackward(command)) {
            Integer id = this.getId(argsList);
            if (id == null) {
                return STATUS_FAILED;
            }
            final String end = this.getCommandEndToMove(command, 
                    COM_HEADER_SEND_BACKWARD);
            SGIFigureElement el = this.getFigureElementToMove(end, figure);
            if (el.moveChild(id.intValue(), false) == false) {
                return STATUS_FAILED;
            }
            return STATUS_SUCCEEDED;
        } else if (this.isCommandSendToBack(command)) {
            Integer id = this.getId(argsList);
            if (id == null) {
                return STATUS_FAILED;
            }
            final String end = this.getCommandEndToMove(command, 
                    COM_HEADER_SEND_TO_BACK);
            SGIFigureElement el = this.getFigureElementToMove(end, figure);
            if (el.moveChildToEnd(id.intValue(), false) == false) {
                return STATUS_FAILED;
            }
            return STATUS_SUCCEEDED;
        } else if (COM_MOVE_TO_TOP_DATA.equalsIgnoreCase(command)) {
            Integer id = this.getId(argsList);
            if (id == null) {
                return STATUS_FAILED;
            }
            SGIFigureElementLegend el = figure.getLegendElement();
            if (el.moveLegendToEnd(id.intValue(), true) == false) {
                return STATUS_FAILED;
            }
            return STATUS_SUCCEEDED;
        } else if (COM_MOVE_TO_UPPER_DATA.equalsIgnoreCase(command)) {
            Integer id = this.getId(argsList);
            if (id == null) {
                return STATUS_FAILED;
            }
            SGIFigureElementLegend el = figure.getLegendElement();
            if (el.moveLegend(id.intValue(), true) == false) {
                return STATUS_FAILED;                    
            }
            return STATUS_SUCCEEDED;
        } else if (COM_MOVE_TO_LOWER_DATA.equalsIgnoreCase(command)) {
            Integer id = this.getId(argsList);
            if (id == null) {
                return STATUS_FAILED;
            }
            SGIFigureElementLegend el = figure.getLegendElement();
            if (el.moveLegend(id.intValue(), false) == false) {
                return STATUS_FAILED;                    
            }
            return STATUS_SUCCEEDED;
        } else if (COM_MOVE_TO_BOTTOM_DATA.equalsIgnoreCase(command)) {
            Integer id = this.getId(argsList);
            if (id == null) {
                return STATUS_FAILED;
            }
            SGIFigureElementLegend el = figure.getLegendElement();
            if (el.moveLegendToEnd(id.intValue(), false) == false) {
                return STATUS_FAILED;                    
            }
            return STATUS_SUCCEEDED;
        } else if (COM_DELETE_DATA.equalsIgnoreCase(command)) {
            final int argn = argsList.size();
            int[] dataIdArray = new int[argn];
            List<Integer> dataIdList = new ArrayList<Integer>();
            for (int ii = 0; ii < dataIdArray.length; ii++) {
                String str = argsList.get(ii);
                Integer num = SGUtilityText.getInteger(str);
                if (null != num) {
                    dataIdList.add(num);
                }
            }
            if (argn!=dataIdList.size()) {
                return STATUS_FAILED;
            }
            for (int i = 0; i < argn; i++) {
                dataIdArray[i] = dataIdList.get(i).intValue();
            }

            // hide data
            if (figure.hideData(dataIdArray) == false) {
                return STATUS_FAILED;
            }
            
            if (dataIdArray.length!=0) {
//                SGDrawingWindow wnd = figure.getWindow();
                wnd.setChanged(true);
                wnd.notifyToRoot();
            }

            return STATUS_SUCCEEDED;
        } else if (COM_FIT_AXES.equalsIgnoreCase(command)) {
        	if (this.fitAxes(command, argsList, figure) == STATUS_SUCCEEDED) {
                return STATUS_SUCCEEDED;
        	} else {
        		return this.fitAxesOlder(command, argsList, figure);
        	}
        } else if (COM_ALIGN_BARS.equalsIgnoreCase(command)) {
//            final int argn = argsList.size();
//            if (argn != 0) {
//                return STATUS_FAILED;
//            }
            if (figure.alignVisibleBars() == false) {
                return STATUS_FAILED;
            }
            return STATUS_SUCCEEDED;
        } else if (COM_SPLIT.equalsIgnoreCase(command)) {
            final int argn = argsList.size();
            if (argn == 0) {
                return STATUS_FAILED;
            }
            int[] dataIdArray = new int[argn];
            for (int ii = 0; ii < dataIdArray.length; ii++) {
                String str = argsList.get(ii);
                Integer num = SGUtilityText.getInteger(str);
                if (num == null) {
                    return STATUS_FAILED;
                }
                dataIdArray[ii] = num.intValue();
            }

            // split
            List<SGData> dataList = SGMainFunctionsSplitMerge.splitData(figure, dataIdArray);
            if (dataList == null || dataList.size()==0) {
                return STATUS_FAILED;
            }

            return STATUS_SUCCEEDED;
        } else if (COM_MERGE.equalsIgnoreCase(command)) {
            final int argn = argsList.size();
            if (argn == 0) {
                return STATUS_FAILED;
            }
            int[] dataIdArray = new int[argn];
            for (int ii = 0; ii < dataIdArray.length; ii++) {
                String str = argsList.get(ii);
                Integer num = SGUtilityText.getInteger(str);
                if (num == null) {
                	return STATUS_FAILED;
                }
                dataIdArray[ii] = num.intValue();
            }

            // merge
            List<SGData> dataList = SGMainFunctionsSplitMerge.mergeData(figure, dataIdArray);
            if (dataList == null || dataList.size()==0) {
                return STATUS_FAILED;
            }

            return STATUS_SUCCEEDED;
        } else if (COM_INSERT_NETCDF_DATA_LABEL.equalsIgnoreCase(command)) {
            final int argn = argsList.size();
            if (argn == 0) {
                return STATUS_FAILED;
            }
            int[] dataIdArray = new int[argn];
            for (int ii = 0; ii < dataIdArray.length; ii++) {
                String str = argsList.get(ii);
                Integer num = SGUtilityText.getInteger(str);
                if (num == null) {
                	return STATUS_FAILED;
                }
                dataIdArray[ii] = num.intValue();
            }

            // insert labels
            if (figure.insertNetCDFLabels(dataIdArray)==false) {
                return STATUS_FAILED;
            }

            return STATUS_SUCCEEDED;
        } else if (SGIAxisConstants.COM_AXIS.equalsIgnoreCase(command)) {                
            if (argsList.size() < 1) {
                return STATUS_FAILED;
            }
            String args0 = argsList.get(0);
            final int location = SGUtility.getAxisLocation(args0);

            SGPropertyResults result = null;
            SGPropertyMap map = null;
            if (location != -1) {
                // for each axis
                List<String> paramsList = new ArrayList<String>(
                        argsList.subList(1, argsList.size()));
                map = this.getPropertiesMap(command, paramsList);
                if (map == null) {
                    return STATUS_FAILED;
                }
                
                // set properties for a axis
                result = figure.getAxisElement().setChildProperties(location, map);
                if (result == null) {
                    return STATUS_FAILED;
                }
                
            } else {
                // for all axes
                map = this.getPropertiesMap(command, argsList);
                if (map == null) {
                    return STATUS_FAILED;
                }
                
                // set properties of axes
                result = figure.getAxisElement().setProperties(map);
                if (result == null) {
                    return STATUS_FAILED;
                }
            }
            
            // after setting properties
            return this.afterSetProperties(map, result);
            
        } else if (SGIColorBarConstants.COM_COLOR_BAR.equalsIgnoreCase(command)) {
        	
        	String colorMapName = null;
        	if (argsList.size() > 1) {
        		String name = argsList.get(0);
        		if (SGColorBarColorMapManager.isValidColorMapName(name)) {
        			colorMapName = name;
        			argsList = new ArrayList<String>(argsList.subList(1, argsList.size()));
        		}
        	}
        	
            SGPropertyMap map = this.getPropertiesMap(command, argsList);
            if (map == null) {
                return STATUS_FAILED;
            }
            
            // set properties
            SGPropertyResults result = null;
            if (colorMapName != null) {
            	result = figure.getAxisElement().setChildColorMapProperties(
            			SGIFigureElementAxis.AXIS_NORMAL, colorMapName, map);
            } else {
                result = figure.getAxisElement().setChildProperties(
                		SGIFigureElementAxis.AXIS_NORMAL, map);
            }

            if (result == null) {
                return STATUS_FAILED;
            }
            
            // after setting properties
            return this.afterSetProperties(map, result);
            
        } else if (SGIScaleConstants.COM_SCALE.equalsIgnoreCase(command)) {

            SGPropertyMap map = this.getPropertiesMap(command, argsList);
            if (map == null) {
                return STATUS_FAILED;
            }
            
            // set properties
            SGPropertyResults result = figure.getAxisElement().setScaleProperties(map);
            if (result == null) {
                return STATUS_FAILED;
            }
            
            // after setting properties
            return this.afterSetProperties(map, result);

        } else if (SGILegendConstants.COM_LEGEND.equalsIgnoreCase(command)) {
            SGPropertyMap map = this.getPropertiesMap(command, argsList);
            if (map == null) {
                return STATUS_FAILED;
            }
            
            // set properties
            SGPropertyResults result = figure.getLegendElement().setProperties(map);
            if (result == null) {
                return STATUS_FAILED;
            }
            
            // after setting properties
            return this.afterSetProperties(map, result);

        } else if (SGIStringConstants.COM_LABEL.equalsIgnoreCase(command)
        		|| SGISignificantDifferenceConstants.COM_SIGNIFICANT_DIFFERENCE.equalsIgnoreCase(command)
        		|| SGIAxisBreakConstants.COM_AXIS_BREAK.equalsIgnoreCase(command)
        		|| SGITimingLineConstants.COM_TIMING_LINE.equalsIgnoreCase(command)
        		|| SGIShapeConstants.COM_SHAPE.equalsIgnoreCase(command)) {

            Class<? extends SGIFigureElement> cl = null;
            if (SGIStringConstants.COM_LABEL.equalsIgnoreCase(command)) {
                cl = SGIFigureElementString.class;
            } else if (SGISignificantDifferenceConstants.COM_SIGNIFICANT_DIFFERENCE.equalsIgnoreCase(command)) {
                cl = SGIFigureElementSignificantDifference.class;
            } else if (SGIAxisBreakConstants.COM_AXIS_BREAK.equalsIgnoreCase(command)) {
                cl = SGIFigureElementAxisBreak.class;
            } else if (SGITimingLineConstants.COM_TIMING_LINE.equalsIgnoreCase(command)) {
                cl = SGIFigureElementTimingLine.class;
            } else if (SGIShapeConstants.COM_SHAPE.equalsIgnoreCase(command)) {
                cl = SGIFigureElementShape.class;
            } else {
                return STATUS_FAILED;
            }
        	
            Integer id = this.getId(argsList);
            if (id == null) {
                return STATUS_FAILED;
            }
            final int argn = argsList.size();
            final List<String> paramsList = new ArrayList<String>(argsList.subList(1, argn));
            SGPropertyMap map = this.getPropertiesMap(command, paramsList);
            if (map == null) {
                return STATUS_FAILED;
            }
            
            SGIFigureElement el = (SGIFigureElement) figure.getIFigureElement(cl);
            if (el == null) {
                return STATUS_FAILED;
            }
            
            // set properties
            SGPropertyResults result = el.setChildProperties(id.intValue(), map);
            if (result == null) {
                return STATUS_FAILED;
            }
            
            // after setting properties
            return this.afterSetProperties(map, result);
        }
        
        return STATUS_NOT_FOUND;
    }
    
    private int fitAxes(String command, List<String> argsList, SGFigure figure) {
    	
        final List<String> paramsList = new ArrayList<String>(argsList);
        SGPropertyMap map = this.getPropertiesMap(command, paramsList);
        if (map == null) {
            return STATUS_FAILED;
        }

        // array of Data ID
        String dataIdString = map.getValue(COM_DATA_ID_LIST);
        int[] dataIdArray = null;
        if (dataIdString != null) {
        	dataIdArray = SGUtilityText.getIntegerArray(dataIdString);
        	if (dataIdArray == null) {
        		return STATUS_FAILED;
        	}
        	if (dataIdArray.length == 0) {
        		return STATUS_FAILED;
        	}
        }
    	
    	// axis direction
    	String axisDirectionString = map.getValue(COM_AXIS_DIRECTION);
    	String[] axisDirectionArray = null;
    	if (axisDirectionString != null) {
        	axisDirectionArray = SGUtilityText.getStringsInBracket(axisDirectionString);
        	if (axisDirectionArray == null) {
        		return STATUS_FAILED;
        	}
        	if (axisDirectionArray.length == 0) {
        		return STATUS_FAILED;
        	}
    	}
    	
    	// fit axes
        if (dataIdArray != null) {
            if (axisDirectionArray != null) {
        		for (int ii = 0; ii < axisDirectionArray.length; ii++) {
        			final int axisDirection = SGUtilityText.getAxisDirection(axisDirectionArray[ii]);
        			if (axisDirection == -1) {
                        return STATUS_FAILED;
        			}
                    if (figure.fitAxisRangeToData(dataIdArray, axisDirection, false, false) == false) {
                        return STATUS_FAILED;
                    }
        		}
        		if (figure.isChangedRoot()) {
        			figure.notifyToRoot();
        		}
            } else {
                if (figure.fitAxisRangeToData(dataIdArray, false) == false) {
                    return STATUS_FAILED;
                }
            }
        } else {
        	if (axisDirectionArray != null) {
        		for (int ii = 0; ii < axisDirectionArray.length; ii++) {
        			final int axisDirection = SGUtilityText.getAxisDirection(axisDirectionArray[ii]);
        			if (axisDirection == -1) {
                        return STATUS_FAILED;
        			}
                    if (figure.fitAxisRangeToVisibleData(axisDirection, false, false) == false) {
                        return STATUS_FAILED;
                    }
        		}
        		if (figure.isChangedRoot()) {
        			figure.notifyToRoot();
        		}
        	} else {
                if (figure.fitAxisRangeToVisibleData(false) == false) {
                    return STATUS_FAILED;
                }
        	}
        }

        return STATUS_SUCCEEDED;
    }
    
    private int fitAxesOlder(String command, List<String> argsList, SGFigure figure) {
    	// for backward compatibility
    	// version number <= 2.0.0
        final int argn = argsList.size();
        if (argn == 0) {
            return STATUS_FAILED;
        }
        int[] dataIdArray = new int[argn];
        List<Integer> dataIdList = new ArrayList<Integer>();
        for (int ii = 0; ii < dataIdArray.length; ii++) {
        	String str = argsList.get(ii);
        	Integer num = SGUtilityText.getInteger(str);
        	if (null != num) {
        	    dataIdList.add(num);
        	}
        }
        if (dataIdList.size()==0) {
            if (figure.fitAxisRangeToVisibleData(false) == false) {
                return STATUS_FAILED;
            }
            return STATUS_SUCCEEDED;
        }
        if (argn!=dataIdList.size()) {
            return STATUS_FAILED;
        }
        for (int i = 0; i < argn; i++) {
            dataIdArray[i] = dataIdList.get(i).intValue();
        }

        // fit axes
        if (figure.fitAxisRangeToData(dataIdArray, false) == false) {
            return STATUS_FAILED;
        }

        return STATUS_SUCCEEDED;
    }
    
    /**
     * Executes a command of global function.
     * 
     * @param command
     *           a command
     * @param argsList
     *           the list of arguments
     * @return the status
     */
    private int execGlobalCommand(final String command, 
            final List<String> argsList) {
    	
        if (COM_COMMAND_SLEEP.equalsIgnoreCase(command)) {
            final int argn = argsList.size();
            if (argn == 0) {
                return STATUS_FAILED;
            }
            int[] dataIdArray = new int[argn];
            List<Double> dataList = new ArrayList<Double>();
            for (int ii = 0; ii < dataIdArray.length; ii++) {
                String str = argsList.get(ii);
                Double num = SGUtilityText.getDouble(str);
                if (null != num) {
                    dataList.add(num);
                }
            }
            if (dataList.size()!=1) {
                return STATUS_FAILED;
            }
            if (argn!=dataList.size()) {
                return STATUS_FAILED;
            }
            try {   // sleep
                Thread.sleep(Math.round(Math.abs(dataList.get(0).doubleValue())*1000));
            } catch (InterruptedException e) {
                return STATUS_FAILED;
            }
            return STATUS_SUCCEEDED;
//        } else if (COM_SETUP_LINE_COLOR_MAP.equalsIgnoreCase(command)) {
//        	List<String> aList = new ArrayList<String>(argsList);
//        	String colorMapName = null;
//        	if (aList.size() > 1) {
//        		String name = aList.get(0);
//        		if (SGColorBarColorMapManager.isValidColorMapName(name)) {
//        			colorMapName = name;
//        			aList = new ArrayList<String>(aList.subList(1, aList.size()));
//        		}
//        	}
//        	SGColorMap colorMap = this.mMain.getColorMap(colorMapName);
//        	if (colorMap == null) {
//                return STATUS_FAILED;
//        	}
//            SGPropertyMap map = this.getPropertiesMap(command, aList);
//            if (map == null) {
//                return STATUS_FAILED;
//            }
//            
//            // set properties
//            SGPropertyResults result = colorMap.setProperties(map);
//            if (result == null) {
//                return STATUS_FAILED;
//            }
//            return STATUS_SUCCEEDED;
        } else if (COM_ASSIGN_LINE_COLOR.equalsIgnoreCase(command)) {
        	List<String> aList = new ArrayList<String>(argsList);
        	
        	// finds figure ID and data ID
        	List<Integer> figureIdList = new ArrayList<Integer>();
        	List<Integer> dataIdList = new ArrayList<Integer>();
        	for (int ii = 0; ii < aList.size(); ii++) {
        		String arg = aList.get(ii);
        		String[] tokens = arg.split(":");
        		if(tokens.length != 2) {
                    break;
        		}
        		Integer figureId = SGUtilityText.getInteger(tokens[0]);
        		if (figureId == null) {
                    break;
        		}
        		Integer dataId = SGUtilityText.getInteger(tokens[1]);
        		if (dataId == null) {
                    break;
        		}
        		figureIdList.add(figureId);
        		dataIdList.add(dataId);
        	}
            aList = new ArrayList<String>(aList.subList(figureIdList.size(), aList.size()));

    		String name = aList.get(0);
    		if (!SGColorBarColorMapManager.isValidColorMapName(name)) {
                return STATUS_FAILED;
    		}
        	String colorMapName = name;
			aList = new ArrayList<String>(aList.subList(1, aList.size()));
			final int dataNum = dataIdList.size();

            // parses input text string
            int[] figureIdArray = new int[dataNum];
            int[] dataIdArray = new int[dataNum];
        	for (int ii = 0; ii < dataNum; ii++) {
        		Integer figureId = figureIdList.get(ii);
        		if (figureId == null) {
        			return STATUS_FAILED;
        		}
        		Integer dataId = dataIdList.get(ii);
        		if (dataId == null) {
        			return STATUS_FAILED;
        		}
        		figureIdArray[ii] = figureId;
        		dataIdArray[ii] = dataId;
        	}

        	// gets data array
            SGDrawingWindow wnd = mWindowManager.getCurrentWindow();
            final int[] visibleFigureIdArray = wnd.getVisibleFigureIDArray();
            int totalChildNum = 0;
        	SGISXYTypeMultipleData[] sxyDataArray = new SGISXYTypeMultipleData[dataNum];
        	for (int ii = 0; ii < dataNum; ii++) {
        		final int figureId = figureIdArray[ii];
        		final int dataId = dataIdArray[ii];
        		if (!SGUtility.contains(visibleFigureIdArray, figureId)) {
        			return STATUS_FAILED;
        		}
        		SGFigure fig = wnd.getFigure(figureId);
        		if (!fig.isDataVisible(dataId)) {
        			return STATUS_FAILED;
        		}
        		SGData data = fig.getData(dataId);
        		if (!(data instanceof SGISXYTypeMultipleData)) {
        			return STATUS_FAILED;
        		}
        		SGISXYTypeMultipleData sxyData = (SGISXYTypeMultipleData) data;
        		totalChildNum += sxyData.getChildNumber();
    			// checks overlapping
        		if (SGUtility.contains(sxyDataArray, sxyData)) {
        			return STATUS_FAILED;
        		}
        		sxyDataArray[ii] = sxyData;
        	}
        	if (totalChildNum <= 1) {
    			return STATUS_FAILED;
        	}
        	
        	// sets color map properties
        	SGColorMap colorMap = this.mMain.getColorMap(colorMapName);
        	if (colorMap == null) {
                return STATUS_FAILED;
        	}
            SGPropertyMap map = this.getPropertiesMap(command, aList);
            if (map == null) {
                return STATUS_FAILED;
            }
            SGPropertyResults result = colorMap.setProperties(map);
            if (result == null) {
                return STATUS_FAILED;
            }

        	// assigns line color
            final List<SGLineStyle> lineStyleList = SGUtilityForFigureElement.createLineStyleList(
            		this.mMain.getColorMap(colorMapName), totalChildNum);
            int cnt = 0;
            for (int ii = 0; ii < dataNum; ii++) {
        		final int figureId = figureIdArray[ii];
        		SGFigure fig = wnd.getFigure(figureId);
        		SGIFigureElementGraph gElement = fig.getGraphElement();
        		
        		// prepares before setting line style
				SGIChildObject child = gElement.getChild((SGData) sxyDataArray[ii]);
				SGIElementGroupSetMultipleSXY gs = (SGIElementGroupSetMultipleSXY) child;
				gs.prepare();

				// sets the line style
        		List<SGStyle> styleList = new ArrayList<SGStyle>();
            	final int num = sxyDataArray[ii].getChildNumber();
            	for (int jj = 0; jj < num; jj++) {
            		SGLineStyle lineStyle = lineStyleList.get(cnt + jj);
            		styleList.add(lineStyle);
            	}
            	if (!gs.setStyle(styleList)) {
        			return STATUS_FAILED;
            	}
            	cnt += num;
            }
        	
            wnd.notifyToRoot();
            wnd.repaint();
            
            return STATUS_SUCCEEDED;
        }
        
        return STATUS_NOT_FOUND;
    }

    /**
     * Executes a command to export as an image.
     * 
     * @param command
     *           a command
     * @param argsList
     *           the list of arguments
     * @return the status
     */
    private int execImageCommand(final String command, 
            final List<String> argsList) {
        
        if (COM_EXPORT_IMAGE.equalsIgnoreCase(command)) {
            final SGDrawingWindow wnd = mWindowManager.getCurrentWindow();
            if (wnd == null) {
                return STATUS_FAILED;
            }
            if (wnd.getVisibleFigureList().size() == 0) {
                return STATUS_FAILED;
            }

            SGPropertyMap map = this.getPropertiesMap(command, argsList);
            if (map == null) {
                return STATUS_FAILED;
            }
            
            // create new data object
            String path = null;
            String imgType = null;
            Iterator<String> itr = map.getKeyIterator();
            while (itr.hasNext()) {
                String key = itr.next();
                if (COM_EXPORT_IMAGE_FILE_PATH.equalsIgnoreCase(key)) {
                    path = map.getValueString(key);
                } else if (COM_EXPORT_IMAGE_TYPE.equalsIgnoreCase(key)) {
                    imgType = map.getValueString(key);
                }
            }
            final String[] imageTypes = {
                    "EMF", "EPS", "PS", "PDF",
                    "SVG", "CGM", "SWF",
                    "JPG", "JPEG", "GIF", "PNG", "RAW", "PPM",
                    "BMP"
            };
            imgType = SGApplicationUtility.getExactTypeString(imageTypes, imgType);
            if (path == null || imgType == null) {
                return STATUS_FAILED;
            }
        	if (map.isDoubleQuoted(COM_EXPORT_IMAGE_FILE_PATH) == false) {
                return STATUS_FAILED;
        	}

        	// check the parent
        	File f = new File(path);
            File parent = new File(f.getParent());
            if (parent.exists() == false) {
            	return STATUS_FAILED;
            }
            
            // check the file name
            String name = f.getName();
            name = SGApplicationUtility.removeExtension(name);
            if (!SGApplicationUtility.checkOutputFileName(name)) {
            	return STATUS_FAILED;
            }

            // remove keys
            map.removeValue(COM_EXPORT_IMAGE_FILE_PATH);
            map.removeValue(COM_EXPORT_IMAGE_TYPE);

        	// export
            SGPropertyResults result = wnd.exportAsImage(map, imgType, 
            		path, true, true);
            if (result == null) {
                return STATUS_FAILED;
            }

            // after setting properties
            return this.afterSetProperties(map, result);
        }
        
        return STATUS_NOT_FOUND;
    }

    // Called after setting properties.
    private int afterSetProperties(SGPropertyMap map, SGPropertyResults result) {
    	
        // check "not found" keys
        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String name = map.getOriginalKey(key);
        	Integer res = result.getResult(key);
        	if (res == null) {
        		result.putResult(name, SGPropertyResults.NOT_FOUND);
        	}
        }

        // shows the results
        result.showResults();
        
        // checks setting error
        itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
        	Integer res = result.getResult(key);
        	if (res.intValue() == SGPropertyResults.INVALID_INPUT_VALUE
        			|| res.intValue() == SGPropertyResults.NOT_FOUND) {
        		return STATUS_PARTIALLY_FAILED;
        	}
        }
        return STATUS_SUCCEEDED;
    }
    
    // The map of alias objects. Keys of this map are given by the string representation of each alias.
    private Map<String, Alias> mAliasMap = new HashMap<String, Alias>();

    /**
     * Adds an alias.
     * 
     * @param command
     *           the command applying the new alias
     * @param key
     *           the property key applying the new alias
     * @param repr
     *           the string representation of the new alias
     * @param value
     *           the value for the new alias
     */
    public void addAlias(String command, String key, String repr, String value) {
    	Alias a = new Alias(command, key, repr, value);
    	this.mAliasMap.put(repr, a);
    }

    /**
     * Removes the alias.
     * 
     * @param repr
     *           the string representation of the alias to remove
     */
    public void removeAlias(String repr) {
    	this.mAliasMap.remove(repr);
    }
    
    /**
     * The alias class.
     */
    static class Alias {
    	
    	// The command applying this alias.
    	private String mCommand = null;
    	
    	// The property key applying this alias.
    	private String mKey = null;
    	
    	// The string representation of the alias.
    	private String mRepr = null;
    	
    	// The value of this alias.
    	private String mValue = null;
    	
    	/**
    	 * Builds an alias object.
    	 * The command and key can be set null value.
    	 * 
	     * @param command
	     *           the command applying the new alias
	     * @param key
	     *           the property key applying the new alias
	     * @param repr
	     *           the string representation of the new alias
	     * @param value
	     *           the value of the new alias
    	 */
    	public Alias(String command, String key, String repr, String value) {
    		if (repr == null || value == null) {
    			throw new IllegalArgumentException("repr == null || value == null");
    		}
    		this.mCommand = command;
    		this.mKey = key;
    		this.mRepr = repr;
    		this.mValue =  value;
    	}
    	
    	/**
    	 * Returns the command applying this alias.
    	 * 
    	 * @return the command applying this alias
    	 */
    	public String getCommand() {
    		return this.mCommand;
    	}
    	
    	/**
    	 * Returns the property key applying this alias.
    	 * 
    	 * @return the property key applying this alias
    	 */
    	public String getKey() {
    		return this.mKey;
    	}
    	
    	/**
    	 * Returns the string representation of this alias.
    	 * 
    	 * @return the string representation of this alias
    	 */
    	public String getRepr() {
    		return this.mRepr;
    	}
    	
    	/**
    	 * Returns the value of this alias.
    	 * 
    	 * @return the value of this alias
    	 */
    	public String getValue() {
    		return this.mValue;
    	}
	}
    
}

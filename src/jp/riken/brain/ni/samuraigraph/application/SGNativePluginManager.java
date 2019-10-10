package jp.riken.brain.ni.samuraigraph.application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.SwingUtilities;

import jp.riken.brain.ni.samuraigraph.application.SGIDataCalcLibrary.Reader;
import jp.riken.brain.ni.samuraigraph.application.SGIDataCalcLibrary.Writer;
import jp.riken.brain.ni.samuraigraph.application.SGPluginDataSelectionPanel.NamedDataBuffer;
import jp.riken.brain.ni.samuraigraph.application.SGPluginFile.Parameter;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIPlugin;
import jp.riken.brain.ni.samuraigraph.base.SGIPluginManager;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGDataViewerDialog;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIMDArrayConstants;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGSXYDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGSXYMultipleDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZGridDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGVXYDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGVXYGridDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGVirtualMDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGVirtualMDArrayVariable;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGDataPopupMenu;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class SGNativePluginManager implements SGIPluginManager, SGIApplicationConstants, 
		SGIApplicationTextConstants, SGIDataColumnTypeConstants, SGIDataPluginConstants, SGIConstants {
	
    SGMainFunctions mMain = null;

    /**
     * The list of native libraries to process data .
     */
    private List<SGPluginFile> mPluginList = new ArrayList<SGPluginFile>();

	public SGNativePluginManager(final SGMainFunctions main) {
		super();
		this.mMain = main;
	}
	
    boolean loadPlugins(final String pluginDirName) {
		final File pluginDir = new File(pluginDirName);
		List<SGIPlugin> pList = new ArrayList<SGIPlugin>();
		if (pluginDir.exists()) {
			List<SGPluginFile> pluginList = this.loadPluginsFiles(pluginDir);
    		this.mPluginList = pluginList;
    		for (SGPluginFile pf : pluginList) {
    			pList.add(pf);
    		}
		}
		SGDrawingWindow.setDataPlugins(pList);
		SGDrawingWindow.setDataPluginManager(this);
		SGDataPopupMenu.setDataPlugins(pList);
		SGDataPopupMenu.setDataPluginManager(this);
		SGDataViewerDialog.setDataPlugins(pList);
    	return true;
    }
    
    private List<SGPluginFile> loadPluginsFiles(File dir) {
    	List<SGPluginFile> libList = new ArrayList<SGPluginFile>();
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				libList.addAll(this.loadPluginsFiles(file));
			} else {
    			String path = file.getAbsolutePath();
    			if (!path.endsWith(".xml")) {
    				continue;
    			}
    			URL url = null;
				try {
					url = file.toURI().toURL();
				} catch (MalformedURLException e1) {
					continue;
				}
		        Document doc = SGUtilityText.getDocument(url);
				if (doc == null) {
					continue;
				}
	            Element root = doc.getDocumentElement();
	            if (root == null) {
	            	continue;
	            }
	            if (!TAG_NAME_PLUGIN.equals(root.getNodeName())) {
	            	continue;
	            }

    			// finds the plug-in for the current OS architecture
	            String osArch = System.getProperty("os.arch");
	            NodeList libNodeList = root.getElementsByTagName(TAG_NAME_LIB);
	            List<Element> validArchElementList = new ArrayList<Element>();
	            for (int ii = 0; ii < libNodeList.getLength(); ii++) {
	            	Node node = libNodeList.item(ii);
	            	if (node instanceof Element) {
	            		Element el = (Element) node;
	            		String osArchAttr = el.getAttribute(ATTR_NAME_LIB_ARCH);
	            		if (osArch.equalsIgnoreCase(osArchAttr)) {
	            			validArchElementList.add(el);
	            		}
	            	}
	            }
	            if (validArchElementList.size() == 0) {
	            	continue;
	            }
	            
    			// finds the plug-in for the current OS name
	            String osName = System.getProperty("os.name");
	            Element libElement = null;
	            for (Element el : validArchElementList) {
            		String osNameAttr = el.getAttribute(ATTR_NAME_LIB_OS);
            		if (osName.equalsIgnoreCase(osNameAttr)) {
            			libElement = el;
            			break;
            		}
	            }
	            if (libElement == null) {
            		// only for Windows
	                if (SGUtility.identifyOS(OS_NAME_WINDOWS)) {
	    	            for (Element el : validArchElementList) {
	                		String osNameAttr = el.getAttribute(ATTR_NAME_LIB_OS);
    	            		if (osNameAttr.toLowerCase().startsWith(OS_NAME_WINDOWS)) {
    	            			libElement = el;
    	            			break;
    	            		}
	    	            }
	                }
	            }
                if (libElement == null) {
	            	continue;
                }
	            String href = libElement.getAttribute(ATTR_NAME_LIB_HREF);
	            if (href.length() == 0) {
	            	continue;
	            }
	            File hrefFile = new File(href);
	            String libFilePath;
	            if (hrefFile.isAbsolute()) {
	            	libFilePath = hrefFile.getAbsolutePath();
	            } else {
		            File libFile = new File(file.getParent(), href);
	            	try {
						libFilePath = libFile.getCanonicalPath();
					} catch (IOException e) {
						continue;
					}
	            }
	            File libFile = new File(libFilePath);
	            if (!libFile.exists()) {
	            	continue;
	            }

    			// finds the plug-in file
    			SGIDataCalcLibrary lib = null;
    			try {
					lib = (SGIDataCalcLibrary) Native.loadLibrary(libFilePath,
							SGIDataCalcLibrary.class);
    			} catch (UnsatisfiedLinkError e) {
    				continue;
    			}
    			SGPluginFile pf = null;
				try {
					pf = new SGPluginFile(lib, libFilePath);
				} catch (IOException e) {
					continue;
				}

    			// gets the attributes and sets to the plug-in file
    			this.putAttribute(root, pf, TAG_NAME_NAME, KEY_PLUGIN_NAME);
    			this.putAttribute(root, pf, TAG_NAME_VERSION, KEY_PLUGIN_VERSION);
    			this.putAttribute(root, pf, TAG_NAME_DEVELOPER, KEY_PLUGIN_DEVELOPER);
    			this.putAttribute(root, pf, TAG_NAME_DESC, KEY_PLUGIN_DESC);
    			
    			// gets the parameters
    			Element paramElement = this.findElement(root, TAG_NAME_PARAMETERS);
    			NodeList paramNodeList = paramElement.getElementsByTagName(TAG_NAME_PARAMETER);
    			TreeMap<Integer, Element> paramElementMap = new TreeMap<Integer, Element>();
    			for (int ii = 0; ii < paramNodeList.getLength(); ii++) {
	            	Node node = paramNodeList.item(ii);
	            	if (node instanceof Element) {
	            		Element el = (Element) node;
	            		String str = el.getAttribute(ATTR_NAME_PARAMETER_INDEX);
	            		Integer index = SGUtilityText.getInteger(str);
	            		if (index == null) {
	            			continue;
	            		}
	            		paramElementMap.put(index, el);
	            	}
    			}
    			Iterator<Integer> indexItr = paramElementMap.keySet().iterator();
    			int cnt = 1;
    			while (indexItr.hasNext()) {
    				Integer index = indexItr.next();
    				if (index.intValue() != cnt) {
    					break;
    				}
    				cnt++;
    			}
    			List<Element> paramElementList = new ArrayList<Element>(paramElementMap.values());
    			for (int ii = 0; ii < paramElementList.size(); ii++) {
    				Element el = paramElementList.get(ii);
    				String name = el.getAttribute(ATTR_NAME_PARAMETER_NAME);
    				String def = el.getAttribute(ATTR_NAME_PARAMETER_DEFAULT);
    				Parameter param = new Parameter(name, def);
    				pf.addParameter(param);
    			}
    			
    			// adds to a list
				libList.add(pf);
			}
		}
    	return libList;
    }
    
    private void putAttribute(Element el, SGPluginFile pf, String tagName, String key) {
    	String value = this.getAttribute(el, tagName);
    	pf.setAttribute(key, value);
    }
    
    private Element findElement(Element parent, String tagName) {
        NodeList libNodeList = parent.getElementsByTagName(tagName);
        if (libNodeList.getLength() == 0) {
        	return null;
        }
        Node node = libNodeList.item(0);
        if (!(node instanceof Element)) {
        	return null;
        }
        Element el = (Element) node;
        return el;
    }
    
    private String getAttribute(Element parent, String tagName) {
    	Element el = this.findElement(parent, tagName);
    	if (el == null) {
    		return "";
    	}
        return el.getTextContent();
    }

	/**
	 * Executes the command for given window.
	 * 
	 * @param cmd
	 *           a text string of command
	 * @param wnd
	 *           a window
	 */
    @Override
	public void execCommand(final String cmd, final SGDrawingWindow wnd) {
    	final SGPluginFile pFile = this.findPlugin(cmd);
    	if (pFile == null) {
    		return;
    	}
		List<String> dataNameList = new ArrayList<String>();
		List<SGData> dataList = new ArrayList<SGData>();
		Map<Integer, List<SGData>> dataMap = wnd.getFocusedDataMap();
		Iterator<Entry<Integer, List<SGData>>> itr = dataMap.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<Integer, List<SGData>> entry = itr.next();
			Integer figureId = entry.getKey();
			SGFigure figure = wnd.getFigure(figureId);
			List<SGData> dList = entry.getValue();
			for (SGData data : dList) {
				String name = figure.getDataName(data);
				dataNameList.add(name);
				dataList.add(data);
			}
		}
		SGData[] dataArray = dataList.toArray(new SGData[dataList.size()]);
		String[] dataNameArray = dataNameList.toArray(new String[dataNameList.size()]);
		this.showInputDialog(pFile, cmd, wnd, dataArray, dataNameArray);
	}

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
    @Override
	public void execCommand(String cmd, SGDrawingWindow wnd, SGData[] dataArray,
			String[] dataNameArray) {
    	if (dataArray.length != dataNameArray.length) {
    		throw new IllegalArgumentException("dataArray.length != dataNameArray.length");
    	}
    	final SGPluginFile pFile = this.findPlugin(cmd);
    	if (pFile == null) {
    		return;
    	}
		this.showInputDialog(pFile, cmd, wnd, dataArray, dataNameArray);
	}

	private void showInputDialog(final SGPluginFile pFile,
			final String cmd, final SGDrawingWindow wnd,
			SGData[] dataArray, String[] dataNameArray) {

		// creates and set up the dialog
		final SGPluginInputWizardDialog inputDialog = new SGPluginInputWizardDialog(wnd, true);
		inputDialog.setPrevious(null);
		inputDialog.setNext(null);
		String pName = pFile.getName();
		StringBuffer sb = new StringBuffer();
		sb.append(SGPluginInputWizardDialog.TITLE);
		sb.append(" (");
		sb.append(pName);
		sb.append(')');
		inputDialog.setTitle(sb.toString());

		inputDialog.setDesc(pFile.getAttribute(TAG_NAME_DESC));
		for (int ii = 0; ii < pFile.getParameterNumber(); ii++) {
			Parameter p = pFile.getParameter(ii);
			String name = p.getName();
			String def = p.getDefaultValue();
			inputDialog.addParameter(name, def);
		}
		for (int ii = 0; ii < dataArray.length; ii++) {
			inputDialog.addData(dataNameArray[ii], dataArray[ii]);
		}
		inputDialog.syncDataTable();

		inputDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				if (SGWizardDialog.OK_BUTTON_TEXT.equals(command)) {
					String[] values = inputDialog.getParameterValues();
					NamedDataBuffer[] buffers = inputDialog.getSelectedDataBuffers();
					doInput(pFile, wnd, buffers, values);
				}
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						inputDialog.deleteTreeNode();
					}
				});
			}
		});
		
		// shows the dialog
		inputDialog.setCenter(wnd);
		inputDialog.setVisible(true);
	}

    SGPluginFile findPlugin(final String cmd) {
    	SGPluginFile ret = null;
		for (SGPluginFile pFile : mPluginList) {
			String id = pFile.getCommand();
			if (id.equals(cmd)) {
				ret = pFile;
				break;
			}
		}
    	return ret;
    }
	
	static final String ERRMSG_FUNCTION_NOT_FOUND = "Function is not found: ";
	
	static final String ERRMSG_FATAL_ERROR = "A fatal error occured.";

	static final String ERRMSG_CALCULATION_FAILED = "Calculation failed.";

	static class PluginResult {
		String mErrorMessage = null;
		String mWarningMessage = null;
		PluginResult() {
			super();
		}
		void showMessage(SGDrawingWindow wnd) {
			if (this.mErrorMessage != null) {
                SGUtility.showErrorMessageDialog(wnd, this.mErrorMessage, SGIConstants.TITLE_ERROR);
			} else if (this.mWarningMessage != null) {
                SGUtility.showWarningMessageDialog(wnd, this.mWarningMessage, SGIConstants.TITLE_WARNING);
			}
		}
	}
	
	private void doInput(final SGPluginFile pFile, final SGDrawingWindow wnd, 
			final NamedDataBuffer[] iBuffers, final String[] values) {
		final PluginResult result = new PluginResult();

		SGDataBuffer[] buffers = new SGDataBuffer[iBuffers.length];
		for (int ii = 0; ii < buffers.length; ii++) {
			buffers[ii] = iBuffers[ii].getDataBuffer();
		}
		
		SGDataPluginInput input = new SGDataPluginInput(buffers, values);
		Pointer inputPointer = Writer.toPointer(input);
		if (inputPointer == null) {
			result.mErrorMessage = ERRMSG_FATAL_ERROR;
			result.showMessage(wnd);
			return;
		}
		
		// call the method of plug-in
		final SGDataCalcLibrary lib = pFile.getLibrary();
		Pointer outputPointer = null;
		try {
			outputPointer = lib.calc(inputPointer);
		} catch (UnsatisfiedLinkError e) {
			result.mErrorMessage = ERRMSG_FUNCTION_NOT_FOUND + "calc()";
			result.showMessage(wnd);
			return;
		}
		if (outputPointer == null) {
			result.mErrorMessage = ERRMSG_CALCULATION_FAILED;
			result.showMessage(wnd);
			return;
		}
		
		// gets output data buffers
		SGDataPluginOutput output = Reader.readOutput(outputPointer);
		if (output == null) {
			result.mErrorMessage = ERRMSG_CALCULATION_FAILED;
			result.showMessage(wnd);
			return;
		}
		final SGDataBuffer[] oBuffers = output.getDataBufffers();
		if (oBuffers == null) {
			result.mErrorMessage = ERRMSG_CALCULATION_FAILED;
			result.showMessage(wnd);
			return;
		}
		String[] errorMessages = output.getErrorMessages();
		if (errorMessages != null && errorMessages.length != 0) {
			StringBuffer sb = new StringBuffer();
			for (int ii = 0; ii < errorMessages.length; ii++) {
				if (ii > 0) {
					sb.append('\n');
				}
				sb.append(errorMessages[ii]);
			}
			result.mErrorMessage = sb.toString();
			result.showMessage(wnd);
			return;
		}
		
		// shows a dialog to select output data
		final SGPluginOutputWizardDialog outputDialog = new SGPluginOutputWizardDialog(wnd, true);
		outputDialog.setPrevious(null);
		outputDialog.setNext(null);
		for (int ii = 0; ii < oBuffers.length; ii++) {
			StringBuffer sb = new StringBuffer();
			sb.append("Output");
			sb.append(ii + 1);
			outputDialog.addDataBuffer(sb.toString(), oBuffers[ii]);
		}
		outputDialog.syncDataTable();
		
		final Pointer outputPointerStore = outputPointer;
		outputDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				if (SGWizardDialog.OK_BUTTON_TEXT.equals(command)) {
					NamedDataBuffer[] bufferArray = outputDialog.getSelectedDataBuffers();
					doOutput(pFile, wnd, bufferArray);
				}
				
				// frees memory
				freeMemory(lib, outputPointerStore, wnd);
				
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						outputDialog.deleteTreeNode();
					}
				});
			}
		});
		
		// shows the dialog
		outputDialog.setCenter(wnd);
		outputDialog.setVisible(true);
	}
	
	private void freeMemory(SGDataCalcLibrary lib, Pointer outputPointer, SGDrawingWindow wnd) {
		final PluginResult result = new PluginResult();
		try {
			lib.freeData(outputPointer);
		} catch (UnsatisfiedLinkError e1) {
			StringBuffer sb = new StringBuffer();
			sb.append(ERRMSG_FUNCTION_NOT_FOUND);
			sb.append("freeData()");
			sb.append("\n");
			sb.append("This plugin file creates memory leak.");
			result.mWarningMessage = sb.toString();
			result.showMessage(wnd);
		}
	}
	
	private void doOutput(SGPluginFile pFile, SGDrawingWindow wnd, NamedDataBuffer[] oBuffers) {
		
		for (int ii = 0; ii < oBuffers.length; ii++) {
			SGDataBuffer buffer = oBuffers[ii].getDataBuffer();
			String dataName = oBuffers[ii].getName();
			String dataType = null;
			SGMDArrayDataColumnInfo[] cols = null;
			String[] columnTypes = null;
			Map<String, Object> infoMap = new HashMap<String, Object>();
			
			// creates variables
			final SGVirtualMDArrayVariable[] vars;
			if (buffer instanceof SGSXYDataBuffer) {
				SGSXYDataBuffer sxyBuffer = (SGSXYDataBuffer) buffer;
				SGVirtualMDArrayVariable xVar = new SGVirtualMDArrayVariable.D1(sxyBuffer.getXValues(), X_VALUE);
				SGVirtualMDArrayVariable yVar = new SGVirtualMDArrayVariable.D1(sxyBuffer.getYValues(), Y_VALUE);
				vars = new SGVirtualMDArrayVariable[] { xVar, yVar };
				dataType = SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA;
				columnTypes = new String[] { X_VALUE, Y_VALUE };
				cols = this.createColumns(vars);
				for (int jj = 0; jj < cols.length; jj++) {
					cols[jj].setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
				}
			} else if (buffer instanceof SGSXYMultipleDataBuffer) {
				SGSXYMultipleDataBuffer sxyBuffer = (SGSXYMultipleDataBuffer) buffer;
				final double[][] xValues = sxyBuffer.getXValues();
				final double[][] yValues = sxyBuffer.getYValues();
				final int multiplicity = sxyBuffer.getMultiplicity();
				dataType = SGDataTypeConstants.SXY_MULTIPLE_VIRTUAL_MDARRAY_DATA;
				if (multiplicity == 1) {
					SGVirtualMDArrayVariable xVar = new SGVirtualMDArrayVariable.D1(xValues[0], X_VALUE);
					SGVirtualMDArrayVariable yVar = new SGVirtualMDArrayVariable.D1(yValues[0], Y_VALUE);
					vars = new SGVirtualMDArrayVariable[] { xVar, yVar };
					columnTypes = new String[vars.length];
					columnTypes[0] = X_VALUE;
					columnTypes[1] = Y_VALUE;
					cols = this.createColumns(vars);
					for (int jj = 0; jj < cols.length; jj++) {
						cols[jj].setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
					}
				} else {
					SGVirtualMDArrayVariable xVar = new SGVirtualMDArrayVariable.D2(xValues, X_VALUE);
					SGVirtualMDArrayVariable yVar = new SGVirtualMDArrayVariable.D2(yValues, Y_VALUE);
					vars = new SGVirtualMDArrayVariable[] { xVar, yVar };
					columnTypes = new String[vars.length];
					columnTypes[0] = X_VALUE;
					columnTypes[1] = Y_VALUE;
					cols = this.createColumns(vars);
					for (int jj = 0; jj < cols.length; jj++) {
						cols[jj].setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 0);
						cols[jj].setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 1);
					}
					
					// sets information map
					SGIntegerSeriesSet pickUpIndices = SGIntegerSeriesSet.createInstance(sxyBuffer.getMultiplicity());
	            	infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, pickUpIndices);
	            	Map<String, Integer> dimensionIndexMap = new HashMap<String, Integer>();
	            	dimensionIndexMap.put(X_VALUE, 0);
	            	dimensionIndexMap.put(Y_VALUE, 0);
					infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP, dimensionIndexMap);
				}

			} else if (buffer instanceof SGSXYZDataBuffer) {
				SGSXYZDataBuffer sxyzBuffer = (SGSXYZDataBuffer) buffer;
				SGVirtualMDArrayVariable xVar = new SGVirtualMDArrayVariable.D1(sxyzBuffer.getXValues(), X_VALUE);
				SGVirtualMDArrayVariable yVar = new SGVirtualMDArrayVariable.D1(sxyzBuffer.getYValues(), Y_VALUE);
				SGVirtualMDArrayVariable zVar = new SGVirtualMDArrayVariable.D1(sxyzBuffer.getZValues(), Z_VALUE);
				vars = new SGVirtualMDArrayVariable[] { xVar, yVar, zVar };
				dataType = SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA;
				columnTypes = new String[] { X_VALUE, Y_VALUE, Z_VALUE };
				cols = this.createColumns(vars);
				for (int jj = 0; jj < cols.length; jj++) {
					cols[jj].setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
				}
			} else if (buffer instanceof SGVXYDataBuffer) {
				SGVXYDataBuffer vxyBuffer = (SGVXYDataBuffer) buffer;
				final String first = vxyBuffer.isPolar() ? MAGNITUDE : X_COMPONENT;
				final String second = vxyBuffer.isPolar() ? ANGLE : Y_COMPONENT;
				SGVirtualMDArrayVariable xVar = new SGVirtualMDArrayVariable.D1(vxyBuffer.getXValues(), X_COORDINATE);
				SGVirtualMDArrayVariable yVar = new SGVirtualMDArrayVariable.D1(vxyBuffer.getYValues(), Y_COORDINATE);
				SGVirtualMDArrayVariable fVar = new SGVirtualMDArrayVariable.D1(vxyBuffer.getFirstComponentValues(), first);
				SGVirtualMDArrayVariable sVar = new SGVirtualMDArrayVariable.D1(vxyBuffer.getSecondComponentValues(), second);
				vars = new SGVirtualMDArrayVariable[] { xVar, yVar, fVar, sVar };
				dataType = SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA;
				columnTypes = new String[] { X_COORDINATE, Y_COORDINATE, first, second };
				cols = this.createColumns(vars);
				for (int jj = 0; jj < cols.length; jj++) {
					cols[jj].setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
				}
			} else if (buffer instanceof SGSXYZGridDataBuffer) {
				SGSXYZGridDataBuffer sxyzBuffer = (SGSXYZGridDataBuffer) buffer;
				SGVirtualMDArrayVariable xVar = new SGVirtualMDArrayVariable.D1(sxyzBuffer.getXValues(), X_VALUE);
				SGVirtualMDArrayVariable yVar = new SGVirtualMDArrayVariable.D1(sxyzBuffer.getYValues(), Y_VALUE);
				SGVirtualMDArrayVariable zVar = new SGVirtualMDArrayVariable.D2(sxyzBuffer.getZValues(), Z_VALUE);
				vars = new SGVirtualMDArrayVariable[] { xVar, yVar, zVar };
				dataType = SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA;
				columnTypes = new String[] { X_VALUE, Y_VALUE, Z_VALUE };
				cols = this.createColumns(vars);
				cols[0].setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
				cols[1].setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
				cols[2].setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, 1);
				cols[2].setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, 0);
			} else if (buffer instanceof SGVXYGridDataBuffer) {
				SGVXYGridDataBuffer vxyBuffer = (SGVXYGridDataBuffer) buffer;
				final String first = vxyBuffer.isPolar() ? MAGNITUDE : X_COMPONENT;
				final String second = vxyBuffer.isPolar() ? ANGLE : Y_COMPONENT;
				SGVirtualMDArrayVariable xVar = new SGVirtualMDArrayVariable.D1(vxyBuffer.getXValues(), X_COORDINATE);
				SGVirtualMDArrayVariable yVar = new SGVirtualMDArrayVariable.D1(vxyBuffer.getYValues(), Y_COORDINATE);
				SGVirtualMDArrayVariable fVar = new SGVirtualMDArrayVariable.D2(vxyBuffer.getFirstComponentValues(), first);
				SGVirtualMDArrayVariable sVar = new SGVirtualMDArrayVariable.D2(vxyBuffer.getSecondComponentValues(), second);
				vars = new SGVirtualMDArrayVariable[] { xVar, yVar, fVar, sVar };
				dataType = SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA;
				columnTypes = new String[] { X_COORDINATE, Y_COORDINATE, first, second };
				cols = this.createColumns(vars);
				cols[0].setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
				cols[1].setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
				cols[2].setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, 1);
				cols[2].setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, 0);
				cols[3].setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, 1);
				cols[3].setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, 0);
			} else {
				continue;
			}
			
			for (int jj = 0; jj < cols.length; jj++) {
				cols[jj].setColumnType(columnTypes[jj]);
			}

			// setup virtual data file
			SGVirtualMDArrayFile file = new SGVirtualMDArrayFile(vars);
			SGMainFunctions.VirtualDataInfo vDataInfo = new SGMainFunctions.VirtualDataInfo();
			vDataInfo.file = file;
			vDataInfo.name = dataName;
			vDataInfo.dataType = dataType;
			vDataInfo.buffer = buffer;
			vDataInfo.colInfoSet = new SGDataColumnInfoSet(cols);
			vDataInfo.infoMap = infoMap;
			
			this.mMain.mVirtualMDArrayData = vDataInfo;
			
			// notifies to the main function
			this.mMain.startVirtualMDArrayDataAdditionWizard(wnd);
		}
	}
	
	private SGMDArrayDataColumnInfo[] createColumns(SGVirtualMDArrayVariable[] vars) {
		SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[vars.length];
		for (int ii = 0; ii < cols.length; ii++) {
			cols[ii] = new SGMDArrayDataColumnInfo(vars[ii], vars[ii].getName(), 
					SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
		}
		return cols;
	}

	/**
	 * Shows a dialog of the detail of plug-in files.
	 * 
	 * @param wnd
	 *           a window
	 */
    @Override
	public void showPluginInfoDialog(SGDrawingWindow wnd) {
		SGPluginInfoDialog dg = new SGPluginInfoDialog(wnd, false);
		dg.setPlugins(this.mPluginList);
		dg.setCenter(wnd);
		dg.setVisible(true);
	}

}

package jp.riken.brain.ni.samuraigraph.application;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow.BackgroundImage;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants.OPERATION;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementLegend;
import jp.riken.brain.ni.samuraigraph.base.SGIProgressControl;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyFileConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIStringModifier;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGINetCDFConstants;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYNetCDFData;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureTypeConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGXYFigure;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGIElementGroupSetForData;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGStringBraceModifier;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ucar.ma2.Array;
import ucar.ma2.ArrayByte;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Group;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;

class SGNetCDFDataSetManager implements SGIArchiveFileConstants, SGINetCDFConstants {
	
    /**
     * The main functions.
     */
    private final SGMainFunctions mMain;

    static final String ATTRIBUTE_KEY_IMAGE_FILE_EXTENSION = "extension";
    
    static final String DIMENSION_NAME_BACKGROUND_IMAGE_BYTE_INDEX = "byte_index";
    
    static final String VARIABLE_NAME_BACKGROUND_IMAGE = "background_image";

    SGNetCDFDataSetManager(SGMainFunctions main) {
        super();
        this.mMain = main;
    }

    private String createGroupName(final int figureIndex, final int dataIndex) {
        StringBuffer sb = new StringBuffer();
        sb.append("id");
        sb.append(figureIndex);
        sb.append('-');
        sb.append(dataIndex);
        return sb.toString();
    }

    private String getGroupVariableName(final String groupName, final String name) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(groupName);
    	sb.append('/');
    	sb.append(name);
    	return sb.toString();
    }

    private String getGroupNameFromDataFile(File file) {
        return file.getName().substring(0, file.getName().length() - 3);
    }

	private static class ByteData {
		Variable var = null;
		byte[] byteArray = null;
	}
	
	private ByteData createImageVariable(NetcdfFileWriteable ncfile,
			SGDrawingWindow wnd) throws IOException, InvalidRangeException {

		BackgroundImage bgImg = wnd.getBackgroundImage();
		if (bgImg == null) {
			return null;
		}
		byte[] imageByteArray = bgImg.getByteArray();
		if (imageByteArray == null) {
			return null;
		}
		
		// create dimension and variable
		Dimension dim = new Dimension(DIMENSION_NAME_BACKGROUND_IMAGE_BYTE_INDEX, imageByteArray.length);
		ncfile.addDimension(null, dim);
		Variable var = new Variable(ncfile, null, null, VARIABLE_NAME_BACKGROUND_IMAGE,
				DataType.BYTE, dim.getName());
		ncfile.addVariable(null, var);

		// add an attribute
		var.addAttribute(SGDataUtility
				.getValueTypeAttribute(SGIDataColumnTypeConstants.VALUE_TYPE_BYTE_DATA));
		var.addAttribute(new Attribute(ATTRIBUTE_KEY_IMAGE_FILE_EXTENSION, bgImg.getExtension()));

		// set to returned value
		ByteData ret = new ByteData();
		ret.var = var;
		ret.byteArray = imageByteArray;

		ncfile.create();

		return ret;
	}
	
	private void writeImageData(NetcdfFileWriteable ncfile, ByteData imageData)
			throws IOException, InvalidRangeException {
		Variable var = imageData.var;
		byte[] byteArray = imageData.byteArray;
		ArrayByte array = new ArrayByte(new int[] { byteArray.length });
		Index index = array.getIndex();
		for (int ii = 0; ii < byteArray.length; ii++) {
			array.setByte(index.set(ii), byteArray[ii]);
		}
		ncfile.write(var.getShortName(), array);
	}
    
    // save background image
    private File saveImageToFile(
            final SGDrawingWindow wnd, final File datasetTempDir)
    throws IOException, InvalidRangeException {
    	StringBuffer sb = new StringBuffer();
    	sb.append(datasetTempDir);
    	sb.append(SGIConstants.FILE_SEPARATOR);
    	sb.append(ARCHIVE_IMAGE_NAME);
    	sb.append('.');
    	sb.append(SGIApplicationConstants.NETCDF_FILE_EXTENSION);
        String fname = sb.toString();
        File file = new File(fname);
        NetcdfFileWriteable ncfile = null;
        try {
            ncfile = NetcdfFileWriteable.createNew(file.getAbsolutePath(), true);
            
            // create image variable
            ByteData imageData = this.createImageVariable(ncfile, wnd);

            // write image data
            if (imageData != null) {
            	this.writeImageData(ncfile, imageData);
            }

        } finally {
            if (ncfile != null) {
                ncfile.close();
            }
        }
    	
        return file;
    }

    private List<File> saveDataToFiles(
            final SGDrawingWindow wnd, final File datasetTempDir)
            throws IOException, InvalidRangeException {
        List<File> flist = new ArrayList<File>();
        ArrayList<SGFigure> figures = wnd.getVisibleFigureList();
        for (int ii = 0; ii < figures.size(); ii++) {
            SGFigure figure = (SGFigure) figures.get(ii);
            SGIFigureElementGraph gElement = figure.getGraphElement();
            List<SGData> dataList = figure.getVisibleDataList();
            for (int jj = 0; jj < dataList.size(); jj++) {
                SGData data = (SGData) dataList.get(jj);
                SGIElementGroupSetForData gs = (SGIElementGroupSetForData) gElement.getChild(data);
                String groupName = this.createGroupName(ii, jj);

                StringBuffer sb = new StringBuffer();
                sb.append(datasetTempDir.getPath());
                sb.append(SGIConstants.FILE_SEPARATOR);
                sb.append(groupName);
            	sb.append('.');
            	sb.append(SGIApplicationConstants.NETCDF_FILE_EXTENSION);
                String fname = sb.toString();
                File file = new File(fname);

                if (!gs.saveData(file, new SGExportParameter(OPERATION.SAVE_TO_DATA_SET_NETCDF), null)) {
                    file.delete();
                    file = null;
                }
                if (null != file) {
                    flist.add(file);
                }
            }
        }

        return flist;
    }

    private void copyNetcdfTempFileHeaderToOutFile(final File[] files, final NetcdfFileWriteable outNcfile)
    throws IOException {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String groupName = this.getGroupNameFromDataFile(file);

            NetcdfFile ncfile = null;
            Group group = new Group(outNcfile, null, groupName);
            try {
                ncfile = SGApplicationUtility.openNetCDF(file.getAbsolutePath());

                List<Attribute> gattrs = ncfile.getGlobalAttributes();
                List<Dimension> dims = ncfile.getDimensions();
                List<Variable> vars = ncfile.getVariables();

                for (Dimension dim : dims) {
                    String name = dim.getName();
                    dim.setName(this.getGroupVariableName(groupName, name));
                    outNcfile.addDimension(null, dim);
                }
                for (Attribute gattr : gattrs) {
                	// skips an attribute of the properties
                	if (ATTRIBUTE_PROPERTY.equals(gattr.getName())) {
                		continue;
                	}
                    group.addAttribute(gattr);
                }
                for (Variable v : vars) {
                    String varName = v.getShortName();
                    String[] groupNames = varName.split("/");
                    if (groupNames.length>=2) {
                        Group g = group;
                        for (int j = 0; j < groupNames.length-1; j++) {
                            Group g2 = g.findGroup(groupNames[j]);
                            if (g2==null) {
                                g2 = new Group(outNcfile, g, groupNames[j]);
                                g.addGroup(g2);
                            }
                            g = g2;
                        }
                        v.setName(groupNames[groupNames.length-1]);
                        g.addVariable(v);
                    } else {
                        group.addVariable(v);
                    }
                }

                outNcfile.addGroup(null, group);

            } finally {
                if (null!=ncfile) {
                    ncfile.close();
                }
            }
        }

    }

    private void copyNetcdfTempFileArrayToOutFile(final File[] files, final NetcdfFileWriteable outNcfile)
    throws IOException, InvalidRangeException {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String groupName = this.getGroupNameFromDataFile(file);

            NetcdfFile ncfile = null;
            try {
                ncfile = SGApplicationUtility.openNetCDF(file.getAbsolutePath());

                List<Variable> vars = ncfile.getVariables();
                for (Variable v : vars) {
                    Array array = v.read();
                    outNcfile.write(this.getGroupVariableName(groupName, v.getShortName()), array);
                }
            } finally {
                if (null!=ncfile) {
                    ncfile.close();
                }
            }
        }
    }

    private void copyNetcdfTempFileToOutFile(
            final String propertyString,
            final File datasetTempDir, final File outFile)
    throws IOException, InvalidRangeException {
        File[] files = datasetTempDir.listFiles();

        NetcdfFileWriteable outNcfile = null;
        try {
            outNcfile = NetcdfFileWriteable.createNew(outFile.getAbsolutePath());
            outNcfile.addGlobalAttribute(ATTRIBUTE_PROPERTY, propertyString);

            this.copyNetcdfTempFileHeaderToOutFile(files, outNcfile);

            outNcfile.create();

            this.copyNetcdfTempFileArrayToOutFile(files, outNcfile);

        } finally {
            if (null!=outNcfile) {
                outNcfile.close();
            }
        }

    }

    /**
     * Returns property string.
     * @param wnd
     * @param versionString
     * @return
     * @throws IOException
     */
    private String getPropertyString(final SGDrawingWindow wnd, final String versionString)
    		throws IOException {
    	return SGApplicationUtility.getPropertyString(wnd, 
    			new SGExportParameter(OPERATION.SAVE_TO_DATA_SET_NETCDF), versionString);
    }

    /**
     * Do save to netCDF dataset file.
     * @param wnd
     * @param versionString
     * @param datasetTempDir temporary directory that contains files which dataset file takes on.
     * @param outFile output file
     * @return SUCCESSFUL_COMPLETION if succeeds. other if failed.
     */
    public int save(
            final SGDrawingWindow wnd, final String versionString,
            final File datasetTempDir, final File outFile) {

        try {
        	
        	// save data
        	this.saveDataToFiles(wnd, datasetTempDir);
        	
        	// save background image
        	this.saveImageToFile(wnd, datasetTempDir);
        	
        	// get properties
            String propertyString = this.getPropertyString(wnd, versionString);
            
            // output to the file
            this.copyNetcdfTempFileToOutFile(propertyString, datasetTempDir, outFile);
            
        } catch (IOException e1) {
            return SGIConstants.FILE_SAVE_FAILURE;
        } catch (InvalidRangeException e1) {
            return SGIConstants.FILE_SAVE_FAILURE;
        } catch (Exception e1) {
            return SGIConstants.FILE_SAVE_FAILURE;
        }

        return SGIConstants.SUCCESSFUL_COMPLETION;

    }

    private void setupNetCDFGroup(final NetcdfFile ncfile) {
        List<Dimension> dims = ncfile.getDimensions();
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < dims.size(); i++) {
        	Dimension dim = dims.get(i);
        	String name = dim.getName();
        	String[] names = name.split("/");
        	String groupName = names[0].trim();
            if (!"".equals(groupName)) {
                set.add(groupName);
            }
        }
        for (String dimName : set) {
            Group g = new Group(ncfile, null, dimName);
            ncfile.addGroup(null, g);
        }
    }

    private int getNumberOfDataInNetcdfFile(final NetcdfFile ncfile, final int figureIndex) {
        Group root = ncfile.getRootGroup();
        List<Group> groups = root.getGroups();
        StringBuffer sb = new StringBuffer();
        sb.append("id");
        sb.append(figureIndex);
        sb.append('-');
        String dataNamePrefix = sb.toString();
        int count = 0;
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).getName().startsWith(dataNamePrefix)) {
                count++;
            }
        }
        return count;
    }


    private boolean setupFigureFromProperty(
            final Element elFigure, final SGDrawingWindow wnd, final SGFigure figure,
            final String versionNumber) {

        //
        // create a figure
        //

        final int figureID = wnd.assignFigureId();
        figure.setID(figureID);

        //
        // create SGIFigureElement objects
        //

        if (figure.readProperty(elFigure) == false) {
            return false;
        }

        if (this.mMain.mFigureCreator.createFigureElementFromPropertyFile(
                figure, elFigure, versionNumber) == SGIConstants.PROPERTY_FILE_INCORRECT) {
            return false;
        }

        return true;
    }

    private SGData getDataInstanceFromProperty(final String dataType, final Element elData) {
        Class<?> dataClass = null;
        if (SGDataUtility.isSDArrayData(dataType)) {
            // use netCDF data class for array data type.
            if (SGDataUtility.isSXYTypeData(dataType)) {
            	dataClass = SGSXYNetCDFMultipleData.class;
            } else if (SGDataTypeConstants.VXY_DATA.equals(dataType)) {
                dataClass = SGVXYNetCDFData.class;
            } else if (SGDataTypeConstants.SXYZ_DATA.equals(dataType)) {
                dataClass = SGSXYZNetCDFData.class;
            }
        } else if (SGDataUtility.isNetCDFData(dataType)) {
            if (SGDataUtility.isSXYTypeData(dataType)) {
            	dataClass = SGSXYNetCDFMultipleData.class;
            } else if (SGDataTypeConstants.VXY_NETCDF_DATA.equals(dataType)) {
                dataClass = SGVXYNetCDFData.class;
            } else if (SGDataTypeConstants.SXYZ_NETCDF_DATA.equals(dataType)) {
                dataClass = SGSXYZNetCDFData.class;
            }
        }
        if (dataClass == null) {
        	return null;
        }
        return SGApplicationUtility.createDataInstance(dataClass);
    }
    
    private int createDataObjectsFromPropertyFile(final Element elFigure,
            final SGFigure figure, final int figureIndex, final NetcdfFile ncfile,
            final SGIProgressControl progress, final boolean readDataProperty,
            final String versionNumber) {

        final int ic = SGIConstants.PROPERTY_FILE_INCORRECT;
        final int di = SGIConstants.DATA_FILE_INVALID;

        NodeList nList = elFigure.getElementsByTagName(SGIFigureElementGraph.TAG_NAME_DATA);
        final int len = nList.getLength();
        if (len != this.getNumberOfDataInNetcdfFile(ncfile, figureIndex)) {
            return ic;                
        }

        int[] indexArray = new int[len];
        SGData[] dataArray = new SGData[len];
        boolean indexValid = true;
        for (int ii = 0; ii < len; ii++) {
            String groupName = this.createGroupName(figureIndex, ii);

            Node node = nList.item(ii);
            if ((node instanceof Element) == false) {
                continue;
            }
            Element elData = (Element) node;

            // data type from the property file
            String originalDataType = elData.getAttribute(SGIFigureElement.KEY_DATA_TYPE);
            if (originalDataType == null) {
                return ic;
            }
            
            // data type as NetCDF data
            String ncDataType = originalDataType;
            if (SGDataUtility.isSDArrayData(ncDataType)) {
                // replaces the data type
                // because sampling SXY-data and date SXY-date is to be eliminated at some stage.
                ncDataType = SGApplicationUtility.getArrayDataType(ncDataType);
            }
            if (SGDataUtility.isSDArrayData(ncDataType)
            		|| SGDataUtility.isMDArrayData(ncDataType)) {
            	if (SGDataUtility.isSXYTypeData(ncDataType)) {
            		ncDataType = SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA;
            	} else if (SGDataUtility.isVXYTypeData(ncDataType)) {
            		ncDataType = SGDataTypeConstants.VXY_NETCDF_DATA;
            	} else if (SGDataTypeConstants.SXYZ_DATA.equals(ncDataType)) {
            		ncDataType = SGDataTypeConstants.SXYZ_NETCDF_DATA;
            	}
            }

            // set the class type for backward compatibility between 1.0.7
            if (SGIFigureTypeConstants.FIGURE_TYPE_XY.equals(figure.getClassType())) {
                String type = null;
                if (SGDataUtility.isVXYTypeData(ncDataType)) {
                    type = SGIFigureTypeConstants.FIGURE_TYPE_VXY;
                } else {
                    type = SGIFigureTypeConstants.FIGURE_TYPE_SXY;
                }
                figure.setClassType(type);
            }

            SGData data = this.getDataInstanceFromProperty(ncDataType, elData);
            if (data == null) {
                return ic;
            }

            // create information map
            Map<String, Object> infoMap = SGMainFunctions.createInfoMap(ncDataType, elData);
            infoMap.put(SGIFigureElementGraph.KEY_GROUP_NAME, groupName);

            if (this.getNumberOfDataInNetcdfFile(ncfile, figureIndex) == 0) {
                return SGIConstants.DATA_NUMBER_SHORTAGE;
            }

			// when given version number in a data set is smaller than or equal to 2.0.0, 
            // sets the modifier to data column
            SGIStringModifier mod = null;
            if (SGUtility.isVersionNumberEqualOrSmallerThanPermittingEmptyString(
					versionNumber, "2.0.0")) {
            	mod = new SGStringBraceModifier();
			}

            // get the data column information
            SGDataColumnInfoSet colInfoSet = null;
            SGNetCDFFile nc = null;
            try {
                NetcdfFile newNcfile = SGApplicationUtility.openNetCDF(ncfile.getLocation());
                nc = new SGNetCDFFile(newNcfile, mod);
            } catch (IOException e) {
                return di;
            }
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, nc);
            colInfoSet = this.mMain.getNetCDFDefaultDataColumnInfo(nc, ncDataType, infoMap);
            if (colInfoSet == null) {
                return di;
            }
            
            // create information map
            Map<String, Object> pfInfoMap = SGMainFunctions.createInfoMap(originalDataType, elData);

            // updates the information map
            final int ret = SGApplicationUtility.updateInformationMap(colInfoSet, infoMap, pfInfoMap);
            if (ret != SGIConstants.SUCCESSFUL_COMPLETION) {
            	return ret;
            }
            
            // create data object
            SGDataCreator.CreatedDataSet cdSet = null;

            cdSet = this.mMain.mDataCreator.createForNetCDFDataSet(nc, ncDataType, colInfoSet, infoMap, progress);
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

            // get the index in the legend
            String str = elData.getAttribute(SGIFigureElement.KEY_INDEX_IN_LEGEND);
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

    private int createSingleFigureFromPropertyFile(final Element elFigure,
            final SGDrawingWindow wnd, final int figureIndex, final NetcdfFile ncfile, 
            final boolean readDataProperty, final String versionNumber) {

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

        if (this.setupFigureFromProperty(elFigure, wnd, figure, versionNumber)==false) {
            return ic;
        }

        // create data objects
        final int ret = this.createDataObjectsFromPropertyFile(elFigure,
                figure, figureIndex, ncfile, wnd, readDataProperty, versionNumber);
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

    private int createFiguresFromPropertyFile(final Element elWnd,
            final SGDrawingWindow wnd, final NetcdfFile ncfile,
            final boolean readDataProperty, final String versionNumber) {

        NodeList nList = elWnd.getElementsByTagName(SGFigure.TAG_NAME_FIGURE);
        final int len = nList.getLength();

        for (int ii = 0; ii < len; ii++) {
            Node node = nList.item(ii);
            if (node instanceof Element) {
                Element el = (Element) node;
                int figureIndex = ii;
                final int ret = this.createSingleFigureFromPropertyFile(el,
                        wnd, figureIndex, ncfile, readDataProperty, versionNumber);
                if (ret != SGIConstants.SUCCESSFUL_COMPLETION) {
                    return ret;
                }
            }
        }

        return SGIConstants.SUCCESSFUL_COMPLETION;
    }

    /**
     * 
     * @param wnd
     * @param elWnd element of the window
     * @return
     */
	private boolean setWindowProperty(final SGDrawingWindow wnd,
			final Element elWnd, final String versionNumber,
			final NetcdfFile ncfile) {
        // start progress
        wnd.setProgressMessage("Read Property");
        wnd.startIndeterminateProgress();

        // set the property to window from property file
        final boolean result = wnd.readProperty(elWnd, 0.0f, 1.0f);

        // end progress
        wnd.endProgress();

        int errcode;
        if (!result) {
            errcode = SGIConstants.PROPERTY_FILE_INCORRECT;
        } else {
            // create figure objects in a window
            final boolean readDataProperty = true;
            errcode = this.createFiguresFromPropertyFile(
                    elWnd, wnd, ncfile, readDataProperty, versionNumber);

            if (errcode == SGIConstants.SUCCESSFUL_COMPLETION) {
                // add history
                wnd.initPropertiesHistory();

                // initialize the history of save
                wnd.initSavedHistory();

                // set the saved flag
                wnd.setSaved(true);
            }
        }

        // set the message
        String msg = null;
        switch (errcode) {
        case SGIConstants.SUCCESSFUL_COMPLETION:
            msg = SGIApplicationTextConstants.MSG_SUCCESSFUL_COMPLETION;
            break;
        case SGIConstants.DATA_NUMBER_SHORTAGE:
            msg = SGIApplicationTextConstants.MSG_DATA_NUMBER_SHORTAGE;
            break;
        case SGIConstants.DATA_NUMBER_EXCESS:
            msg = SGIApplicationTextConstants.MSG_DATA_NUMBER_EXCESS;
            break;
        case SGIConstants.FILE_OPEN_FAILURE:
            msg = SGIApplicationTextConstants.MSG_FILE_OPEN_FAILURE;
            break;
        case SGIConstants.PROPERTY_FILE_INCORRECT:
            msg = SGIApplicationTextConstants.MSG_PROPERTY_FILE_INVALID;
            break;
        case SGIConstants.DATA_FILE_INVALID:
            msg = SGIApplicationTextConstants.MSG_DATA_FILE_OPEN_FAILURE;
            break;
        default:
            msg = SGIApplicationTextConstants.MSG_UNKNOWN_ERROR_OCCURED;
        }

        // show the message dialog
        if (msg != SGIApplicationTextConstants.MSG_SUCCESSFUL_COMPLETION) {
            SGUtility.showMessageDialog(null, msg, "Property file",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean loadSub(final SGDrawingWindow wnd, final Document doc, final NetcdfFile ncfile) {
        // get root element
        Element root = doc.getDocumentElement();

        // get version number
        String versionNumber = root.getAttribute(SGIPropertyFileConstants.KEY_VERSION_NUMBER);

        // get window element
        Element elWnd = this.mMain.mPropertyFileManager.getWindowElement(doc);
        NodeList nListFigure = elWnd.getElementsByTagName(SGFigure.TAG_NAME_FIGURE);
        final int figureNum = nListFigure.getLength();
        int cnt = 0;
        final int[] dataNumArray = new int[figureNum];
        for (int ii = 0; ii < figureNum; ii++) {
            Node node = nListFigure.item(ii);
            if ((node instanceof Element) == false) {
                return false;
            }
            Element elFigure = (Element) node;
            NodeList nListData = elFigure.getElementsByTagName(SGIFigureElementGraph.TAG_NAME_DATA);
            dataNumArray[ii] = nListData.getLength();
            cnt += dataNumArray[ii];
        }

    	// finds the variable for the background image
    	Variable imgVar = null;
        for (Variable var : ncfile.getVariables()) {
        	if (DataType.BYTE.equals(var.getDataType())) {
            	if (var.getShortName().startsWith(ARCHIVE_IMAGE_NAME)) {
            		imgVar = var;
            		break;
            	}
        	}
        }

        boolean imgExists = true;
        byte[] imgBytes = null;
        String ext = null;
        if (imgVar != null) {
            // read byte array from the netCDF file
            ByteArrayOutputStream bos = null;
            try {
            	bos = new ByteArrayOutputStream();
    			Array array = imgVar.read();
    			if (!(array instanceof ArrayByte)) {
    				return false;
    			}
    			ArrayByte bArray = (ArrayByte) array;
    			Index index = bArray.getIndex();
    			for (int ii = 0; ii < bArray.getSize(); ii++) {
    				final byte b = bArray.get(index.set(ii));
    				bos.write(b);
    			}
    			imgBytes = bos.toByteArray();
    			
    		} catch (IOException e) {
    			return false;
    		} finally {
    			if (bos != null) {
    				try {
    					bos.close();
    				} catch (IOException e) {
    				}
    			}
    		}
    		
    		// get image type
    		Attribute attr = imgVar.findAttribute(ATTRIBUTE_KEY_IMAGE_FILE_EXTENSION);
    		if (attr == null) {
    			return false;
    		}
    		String value = attr.getStringValue();
    		if (value == null) {
    			return false;
    		}
    		ext = SGApplicationUtility.findExtension(value);
    		if (ext == null) {
    			return false;
    		}
    		
        } else {
        	imgExists = false;
        }

		// increment the counter
        if (imgExists) {
        	cnt++;
        }
        
        // setup netCDF group to the netCDF file
        this.setupNetCDFGroup(ncfile);

        Group rootGroup = ncfile.getRootGroup();
        List<Group> groups = rootGroup.getGroups();
        for (int ii = groups.size() - 1; ii >= 0; ii--) {
        	Group g = groups.get(ii);
        	String gName = g.getName().toLowerCase();
        	final String clenStr = "clen";
        	final int clenIndex = gName.lastIndexOf(clenStr);
        	if (clenIndex == -1) {
        		continue;
        	}
        	if (gName.endsWith(clenStr)) {
        		continue;
        	}
        	final String suffix = gName.substring(clenIndex + clenStr.length());
        	Integer num = SGUtilityText.getInteger(suffix);
        	if (num == null) {
        		continue;
        	}
    		groups.remove(ii);
        }
        if (cnt != groups.size()) {
            return false;
        }

        // clear old objects
        wnd.deleteImage();
        wnd.clearUndoBuffer();
        wnd.removeAllFigures();

        // set the background image
        if (imgExists) {
        	if (!wnd.setImage(imgBytes, ext, false)) {
        		return false;
        	}
        }

        // set properties
        if (this.setWindowProperty(wnd, elWnd, versionNumber, ncfile)==false) {
            return false;
        }

        // update the client rectangle
        wnd.updateClientRect();
        
        return true;
    }
    
    /**
     * Load netCDF dataset.
     * 
     * @param wnd
     * @param file filename of netCDF dataset.
     * @return 0 if succeeds. -1 if file is invalid.
     * @throws IOException 
     */
    public int load(final SGDrawingWindow wnd, final File file)
    throws IOException {
        NetcdfFile ncfile = null;
        try {
            ncfile = SGApplicationUtility.openNetCDF(file.getAbsolutePath());
            Attribute attr = ncfile.findGlobalAttribute(ATTRIBUTE_PROPERTY);
            if (null==attr) {
                return -1;
            }
            String propertyString = attr.getStringValue();
            if (null==propertyString || propertyString.length()==0) {
                return -1;
            }

            // create a Document object
            Document doc = SGUtilityText.getDocumentFromString(propertyString);
            if (doc == null) {
                return -1;
            }

            if (this.loadSub(wnd, doc, ncfile)==false) {
                return -1;
            }
        } finally {
            if (null!=ncfile) {
                ncfile.close();
            }
        }

        return SGIConstants.OK_OPTION;
    }
    
    /**
     * 
     * @param location
     * @return
     */
    public static boolean isNetCDFDatasetFile(final String location) {
        NetcdfFile ncfile = null;
        try {
            ncfile = SGApplicationUtility.openNetCDF(location);
            List<Attribute> attrList = ncfile.getGlobalAttributes();
            for (Attribute attr : attrList) {
                if (ATTRIBUTE_PROPERTY.equals(attr.getName()) &&
                        attr.isString() && attr.getStringValue().length()>0) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        } finally {
            if (ncfile!=null) {
                try {
                    ncfile.close();
                } catch (IOException e) {
                }
            }
        }
    }
}

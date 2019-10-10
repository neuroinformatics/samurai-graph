package jp.riken.brain.ni.samuraigraph.application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.riken.brain.ni.samuraigraph.base.SGBufferedFileReader;
import jp.riken.brain.ni.samuraigraph.base.SGCSVTokenizer.Token;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIProgressControl;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDateVariable;
import jp.riken.brain.ni.samuraigraph.data.SGHDF5File;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIMDArrayConstants;
import jp.riken.brain.ni.samuraigraph.data.SGINetCDFConstants;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.data.SGMATLABFile;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGSXYMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYMDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFDateData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYSDArrayData;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ch.systemsx.cisd.hdf5.IHDF5Reader;

import com.jmatio.io.MatFileReader;

/**
 * A class to create data objects.
 */
public class SGDataCreator implements SGIConstants, SGIDataColumnTypeConstants, SGINetCDFConstants,
		SGIMDArrayConstants, SGIApplicationConstants {

    /**
     * A data source observer.
     */
    private SGDataSourceObserver mDataSourceObserver = new SGDataSourceObserver();

    /**
     * The default constructor.
     *
     */
    public SGDataCreator() {
        super();
    }

    /**
     * Creates a data object.
     *
     * @param path
     *            the path name of the file
     * @param colInfoSet
     *            a set of column information
     * @param infoMap
     *            information of data
     * @param progress
     *            the progress monitor
     * @param versionNumber
     *            the versionNumber of the property file if a property file is used
     * @param mode
     *            the mode of loading properties
     * @return a set of created data. null if failed.
     */
    public CreatedDataSet create(final SGMainFunctions.DataSourceInfo dataSource, final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap, final SGIProgressControl progress,
            final String versionNumber, final int mode) throws FileNotFoundException {

    	CreatedDataSet cdSet = null;
        progress.startIndeterminateProgress();
        try {
			SGIDataSource src = null;
			if (dataSource.src != null) {
				src = dataSource.src;
			} else {
				src = SGApplicationUtility.createDataSource(dataSource.path, colInfoSet,
						infoMap, progress, versionNumber, mode, true);
			}
            cdSet = this.createData(src, colInfoSet, infoMap, progress);
        } finally {
            progress.endProgress();
        }

        return cdSet;
    }

    /**
     * A created data object.
     *
     * This has data and its title.
     */
    public static class CreatedData {
        private SGData mData = null;
        private String mTitle = null;

        /**
         * Creates a data object with given identifier.
         *
         * @param data
         *            the data
         * @param title
         *            the title
         */
        public CreatedData(final SGData data, final String title) {
            super();
            if (data == null) {
                throw new IllegalArgumentException("data == null");
            }
            this.mTitle = title;
            this.mData = data;
        }
        public String getTitle() {
            return this.mTitle;
        }
        public SGData getData() {
            return this.mData;
        }
    }

    public static class CreatedDataSet {
        private List<CreatedData> mDataList = new ArrayList<CreatedData>();
        private String mMiddleString = "\\_";

        public CreatedDataSet() {
            super();
        }
        public boolean addData(final SGData data, final String title) {
            this.mDataList.add(new CreatedData(data, title));
            return true;
        }
        public boolean addData(final SGData data) {
            return this.addData(data, null);
        }
        public int getDataLength() {
            return this.mDataList.size();
        }
        public CreatedData getData(final int n) {
            if (n < 0 || n >= this.mDataList.size()) {
                throw new IllegalArgumentException("Index out of bounds: " + n);
            }
            return this.mDataList.get(n);
        }
        public String getMiddleString() {
            return this.mMiddleString;
        }
        public void setMiddleString(final String middleString) {
            this.mMiddleString = middleString;
        }
    }


    /**
     * Creates an array of SXY Data objects.
     *
     * @return an array of created data objects
     */
    private CreatedDataSet createSingleSXYSDArrayData(final SGIDataSource dataSource,
    		final SGIProgressControl progress,
    		final SGDataColumnInfoSet colInfoSet, final Map<String, Object> infoMap) {

        SGSDArrayFile file = (SGSDArrayFile) dataSource;
        final SGDataColumnInfo[] colInfo = colInfoSet.getDataColumnInfoArray();

        // get column indices
        List<Integer> xIndexList = new ArrayList<Integer>();
        List<Integer> yIndexList = new ArrayList<Integer>();
        Map<Integer, Integer> lIndexMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> uIndexMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> tIndexMap = new HashMap<Integer, Integer>();
        if (SGDataUtility.getSXYColumnType(colInfo, xIndexList, yIndexList,
				lIndexMap, uIndexMap, tIndexMap) == false) {
        	return null;
		}

        if (xIndexList.size() != 1 && yIndexList.size() != 1) {
        	return null;
        }

        SGIntegerSeriesSet stride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
        SGIntegerSeriesSet tickLabelStride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
        final boolean strideAvailable = this.isStrideAvailable(infoMap);
        
        Double samplingRate = (Double) infoMap.get(SGIDataInformationKeyConstants.KEY_SAMPLING_RATE);

        // create data objects
        List<SGISXYTypeSingleData> dataList = new ArrayList<SGISXYTypeSingleData>();
        List<String> titleList = new ArrayList<String>();
        if (xIndexList.size() == 1 && yIndexList.size() == 1) {
        	final Integer xIndex = xIndexList.get(0);
        	final Integer yIndex = yIndexList.get(0);
        	Integer xlIndex = lIndexMap.get(xIndex);
        	Integer ylIndex = lIndexMap.get(yIndex);
        	Integer xuIndex = uIndexMap.get(xIndex);
        	Integer yuIndex = uIndexMap.get(yIndex);
        	Integer lIndex, uIndex, ehIndex;
        	if (xlIndex != null && xuIndex != null) {
        		lIndex = xlIndex;
        		uIndex = xuIndex;
        		ehIndex = xIndex;
        	} else 	if (ylIndex != null && yuIndex != null) {
        		lIndex = ylIndex;
        		uIndex = yuIndex;
        		ehIndex = yIndex;
        	} else {
        		lIndex = null;
        		uIndex = null;
        		ehIndex = null;
        	}
        	Integer xtIndex = tIndexMap.get(xIndex);
        	Integer ytIndex = tIndexMap.get(yIndex);
        	Integer tIndex, thIndex;
        	if (xtIndex != null) {
        		tIndex = xtIndex;
        		thIndex = xIndex;
        	} else if (ytIndex != null) {
        		tIndex = ytIndex;
        		thIndex = yIndex;
        	} else {
        		tIndex = null;
        		thIndex = null;
        	}
            SGISXYTypeSingleData data = new SGSXYSDArrayData(file, this.mDataSourceObserver,
            		xIndex, yIndex, lIndex, uIndex, ehIndex, tIndex, thIndex,
            		stride, tickLabelStride, strideAvailable, samplingRate);
            dataList.add(data);
            titleList.add(null);
        } else if (xIndexList.size() == 1) {
        	Integer xIndex = xIndexList.get(0);
            for (int ii = 0; ii < yIndexList.size(); ii++) {
            	final Integer yIndex = yIndexList.get(ii);
            	Integer lIndex = lIndexMap.get(yIndex);
            	Integer uIndex = uIndexMap.get(yIndex);
            	Integer tIndex = tIndexMap.get(yIndex);
                SGISXYTypeSingleData data = new SGSXYSDArrayData(file, this.mDataSourceObserver,
                		xIndex, yIndex, lIndex, uIndex, yIndex, tIndex, yIndex,
                		stride, tickLabelStride, strideAvailable, samplingRate);
                dataList.add(data);
                String title = colInfo[yIndex.intValue()].getTitle();
                if (title == null || "".equals(title)) {
                	title = Integer.valueOf(yIndex.intValue() + 1).toString();
                }
                titleList.add(title);
            }
        } else {
        	Integer yIndex = yIndexList.get(0);
            for (int ii = 0; ii < xIndexList.size(); ii++) {
            	final Integer xIndex = xIndexList.get(ii);
            	Integer lIndex = lIndexMap.get(xIndex);
            	Integer uIndex = uIndexMap.get(xIndex);
            	Integer tIndex = tIndexMap.get(xIndex);
                SGISXYTypeSingleData data = new SGSXYSDArrayData(file, this.mDataSourceObserver,
                		xIndex, yIndex, lIndex, uIndex, xIndex, tIndex, xIndex,
                		stride, tickLabelStride, strideAvailable, samplingRate);
                dataList.add(data);
                String title = colInfo[xIndex.intValue()].getTitle();
                if (title == null || "".equals(title)) {
                	title = Integer.valueOf(xIndex.intValue() + 1).toString();
                }
                titleList.add(title);
            }
        }

        // modifies the titles
        List<String> titleListNew = new ArrayList<String>();
        for (int ii = 0; ii < titleList.size(); ii++) {
        	String title = titleList.get(ii);
        	if (title == null) {
        		titleListNew.add(null);
        		continue;
        	}
        	title = SGUtility.addEscapeChar(title);
        	titleListNew.add(title);
        }
        titleList = titleListNew;

        SGData[] dataArray = new SGData[dataList.size()];
        for (int ii = 0; ii < dataArray.length; ii++) {
        	SGSXYSDArrayData data = (SGSXYSDArrayData) dataList.get(ii);
        	dataArray[ii] = (SGData) data.toMultiple();
        }
        String[] titleArray = titleList.toArray(new String[titleList.size()]);
        CreatedDataSet cdSet = new CreatedDataSet();
        for (int ii = 0; ii < titleArray.length; ii++) {
        	cdSet.addData(dataArray[ii], titleArray[ii]);
        }

        // disposes data objects
        for (int ii = 0; ii < dataArray.length; ii++) {
        	SGData data = (SGData) dataList.get(ii);
        	data.dispose();
        }

        return cdSet;
    }

    /**
     * Creates a SXY Multiple data.
     *
     * @return created data object
     */
    private CreatedDataSet createMultipleSXYSDArrayData(final SGIDataSource dataSource,
    		final SGIProgressControl progress,
    		final SGDataColumnInfoSet colInfoSet, final Map<String, Object> infoMap) {

        SGSDArrayFile file = (SGSDArrayFile) dataSource;
		final SGDataColumnInfo[] colInfo = colInfoSet.getDataColumnInfoArray();

        // get column indices
        List<Integer> xIndexList = new ArrayList<Integer>();
        List<Integer> yIndexList = new ArrayList<Integer>();
        Map<Integer, Integer> lIndexMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> uIndexMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> tIndexMap = new HashMap<Integer, Integer>();
        if (SGDataUtility.getSXYColumnType(colInfo, xIndexList, yIndexList,
				lIndexMap, uIndexMap, tIndexMap) == false) {
        	return null;
		}

        if (xIndexList.size() != 1 && yIndexList.size() != 1) {
        	return null;
        }

        SGIntegerSeriesSet stride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
        SGIntegerSeriesSet tickLabelStride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
        final boolean strideAvailable = this.isStrideAvailable(infoMap);

        // create a data object
        Integer[] xIndices = new Integer[xIndexList.size()];
        xIndices = xIndexList.toArray(xIndices);
        Integer[] yIndices = new Integer[yIndexList.size()];
        yIndices = yIndexList.toArray(yIndices);

        Integer[] lIndices = null;
        Integer[] uIndices = null;
        Integer[] ehIndices = null;
        Integer[] tIndices = null;
        Integer[] thIndices = null;
        if (xIndexList.size() == 1 && yIndexList.size() == 1) {
        	final Integer xIndex = xIndexList.get(0);
        	final Integer yIndex = yIndexList.get(0);
        	Integer xlIndex = lIndexMap.get(xIndex);
        	Integer ylIndex = lIndexMap.get(yIndex);
        	Integer xuIndex = uIndexMap.get(xIndex);
        	Integer yuIndex = uIndexMap.get(yIndex);
        	Integer lIndex = null;
        	Integer uIndex = null;
        	Integer ehIndex = null;
        	if (xlIndex != null && xuIndex != null) {
        		lIndex = xlIndex;
        		uIndex = xuIndex;
        		ehIndex = xIndex;
        	} else 	if (ylIndex != null && yuIndex != null) {
        		lIndex = ylIndex;
        		uIndex = yuIndex;
        		ehIndex = yIndex;
        	}
        	if (lIndex != null && uIndex != null && ehIndex != null) {
            	lIndices = new Integer[] { lIndex };
            	uIndices = new Integer[] { uIndex };
            	ehIndices = new Integer[] { ehIndex };
        	}
        	Integer xtIndex = tIndexMap.get(xIndex);
        	Integer ytIndex = tIndexMap.get(yIndex);
        	Integer tIndex = null;
        	Integer thIndex = null;
        	if (xtIndex != null) {
        		tIndex = xtIndex;
        		thIndex = xIndex;
        	} else if (ytIndex != null) {
        		tIndex = ytIndex;
        		thIndex = yIndex;
        	}
        	if (tIndex != null) {
        		tIndices = new Integer[] { tIndex };
        		thIndices = new Integer[] { thIndex };
        	}
        } else if (xIndexList.size() == 1) {
        	List<Integer> lIndexList = new ArrayList<Integer>();
        	List<Integer> uIndexList = new ArrayList<Integer>();
        	List<Integer> ehIndexList = new ArrayList<Integer>();
        	List<Integer> tIndexList = new ArrayList<Integer>();
        	List<Integer> thIndexList = new ArrayList<Integer>();
            for (int ii = 0; ii < yIndexList.size(); ii++) {
            	final Integer yIndex = yIndexList.get(ii);
            	Integer lIndex = lIndexMap.get(yIndex);
            	Integer uIndex = uIndexMap.get(yIndex);
            	if (lIndex != null && uIndex != null) {
            		lIndexList.add(lIndex);
            		uIndexList.add(uIndex);
                	ehIndexList.add(yIndex);
            	}
            	Integer tIndex = tIndexMap.get(yIndex);
            	if (tIndex != null) {
            		tIndexList.add(tIndex);
                	thIndexList.add(yIndex);
            	}
            }
            lIndices = lIndexList.toArray(new Integer[lIndexList.size()]);
            uIndices = uIndexList.toArray(new Integer[uIndexList.size()]);
            ehIndices = ehIndexList.toArray(new Integer[ehIndexList.size()]);
            tIndices = tIndexList.toArray(new Integer[tIndexList.size()]);
            thIndices = thIndexList.toArray(new Integer[thIndexList.size()]);
        } else {
        	List<Integer> lIndexList = new ArrayList<Integer>();
        	List<Integer> uIndexList = new ArrayList<Integer>();
        	List<Integer> ehIndexList = new ArrayList<Integer>();
        	List<Integer> tIndexList = new ArrayList<Integer>();
        	List<Integer> thIndexList = new ArrayList<Integer>();
            for (int ii = 0; ii < xIndexList.size(); ii++) {
            	final Integer xIndex = xIndexList.get(ii);
            	Integer lIndex = lIndexMap.get(xIndex);
            	Integer uIndex = uIndexMap.get(xIndex);
            	if (lIndex != null && uIndex != null) {
            		lIndexList.add(lIndex);
            		uIndexList.add(uIndex);
                	ehIndexList.add(xIndex);
            	}
            	Integer tIndex = tIndexMap.get(xIndex);
            	if (tIndex != null) {
            		tIndexList.add(tIndex);
            		thIndexList.add(xIndex);
            	}
            }
            lIndices = lIndexList.toArray(new Integer[lIndexList.size()]);
            uIndices = uIndexList.toArray(new Integer[uIndexList.size()]);
            ehIndices = ehIndexList.toArray(new Integer[ehIndexList.size()]);
            tIndices = tIndexList.toArray(new Integer[tIndexList.size()]);
            thIndices = thIndexList.toArray(new Integer[thIndexList.size()]);
        }
        
        Double samplingRate = (Double) infoMap.get(SGIDataInformationKeyConstants.KEY_SAMPLING_RATE);

		SGSXYSDArrayMultipleData data = new SGSXYSDArrayMultipleData(file,
				this.mDataSourceObserver, xIndices, yIndices, lIndices,
				uIndices, ehIndices, tIndices, thIndices, stride,
				tickLabelStride, strideAvailable, samplingRate);

        CreatedDataSet cdSet = new CreatedDataSet();
        cdSet.addData(data);

        return cdSet;
    }

    /**
     * Creates a VXY data.
     *
     * @return created data object
     */
    private CreatedDataSet createVXYSDArrayData(final SGIDataSource dataSource,
    		final SGIProgressControl progress,
    		final SGDataColumnInfoSet colInfoSet, final Map<String, Object> infoMap) {

    	SGSDArrayFile file = (SGSDArrayFile) dataSource;
		final SGDataColumnInfo[] colInfo = colInfoSet.getDataColumnInfoArray();

        // get polar flag
        if (infoMap.size() <= 1) {
            return null;
        }
        Object obj = infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED);
        if ((obj instanceof Boolean) == false) {
            return null;
        }
        Boolean b = (Boolean) obj;
        final boolean isPolar = b.booleanValue();

        // get column indices
        final String type1 = SGDataUtility.getVXYFirstComponentColumnType(infoMap);
        final String type2 = SGDataUtility.getVXYSecondComponentColumnType(infoMap);
        Integer xIndex = null;
        Integer yIndex = null;
        Integer c1Index = null;
        Integer c2Index = null;
        for (int ii = 0; ii < colInfo.length; ii++) {
            String cType = colInfo[ii].getColumnType();
            if (SGDataUtility.isEqualColumnType(X_COORDINATE, cType)) {
                xIndex = Integer.valueOf(ii);
            } else if (SGDataUtility.isEqualColumnType(Y_COORDINATE, cType)) {
                yIndex = Integer.valueOf(ii);
            } else if (SGDataUtility.isEqualColumnType(type1, cType)) {
                c1Index = Integer.valueOf(ii);
            } else if (SGDataUtility.isEqualColumnType(type2, cType)) {
                c2Index = Integer.valueOf(ii);
            } else if ("".equals(cType)) {
                continue;
            } else {
                throw new Error("Illegal Column Type: " + cType);
            }
        }

        // gets the stride
        SGIntegerSeriesSet stride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE);
        final boolean strideAvailable = this.isStrideAvailable(infoMap);

        // create a data object
        SGData data = new SGVXYSDArrayData(file,
        		this.mDataSourceObserver, xIndex, yIndex, c1Index, c2Index, isPolar,
        		stride, strideAvailable);

        CreatedDataSet cdSet = new CreatedDataSet();
        cdSet.addData(data);

        return cdSet;
    }

    /**
     * Creates a SXYZ data.
     *
     * @return created data object
     */
    private CreatedDataSet createSXYZSDArrayData(final SGIDataSource dataSource,
    		final SGIProgressControl progress,
    		final SGDataColumnInfoSet colInfoSet, final Map<String, Object> infoMap) {

    	SGSDArrayFile file = (SGSDArrayFile) dataSource;
		final SGDataColumnInfo[] colInfo = colInfoSet.getDataColumnInfoArray();

        // get column indices
        Integer xIndex = null;
        Integer yIndex = null;
        Integer zIndex = null;
        for (int ii = 0; ii < colInfo.length; ii++) {
            String cType = colInfo[ii].getColumnType();
            if (SGDataUtility.isEqualColumnType(X_VALUE, cType)) {
                xIndex = Integer.valueOf(ii);
            } else if (SGDataUtility.isEqualColumnType(Y_VALUE, cType)) {
                yIndex = Integer.valueOf(ii);
            } else if (SGDataUtility.isEqualColumnType(Z_VALUE, cType)) {
                zIndex = Integer.valueOf(ii);
            } else if ("".equals(cType)) {
                continue;
            } else {
                throw new Error("Illegal Column Type: " + cType);
            }
        }

        // gets the stride
        SGIntegerSeriesSet stride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE);
        final boolean strideAvailable = this.isStrideAvailable(infoMap);

		// create a data object
		SGData data = new SGSXYZSDArrayData(file, this.mDataSourceObserver,
				xIndex, yIndex, zIndex, stride, strideAvailable);

        CreatedDataSet cdSet = new CreatedDataSet();
        cdSet.addData(data);

        return cdSet;
    }

    /**
     * Determine the data-type from the first line.
     *
     * @param path
     *            the path of file
     * @param cList
     *            the list of candidates of data types
     * @return true if succeeded
     */
    public boolean getDataTypeCandidateList(final String path,
            final List<String> cList) throws FileNotFoundException {

    	if (this.getTextDataTypeCandidateList(path, cList) == false) {
    		return false;
    	}

    	/*
        try {
            if (NetcdfFile.canOpen(path)) {
            	if (this.getNetCDFDataTypeCandidateList(path, cList) == false) {
            		return false;
            	}

            } else {
            	if (this.getTextDataTypeCandidateList(path, cList) == false) {
            		return false;
            	}
            }

        } catch (IOException e) {
        	return false;
        }
        */

        return true;
    }

    /*
    private boolean getNetCDFDataTypeCandidateList(final String path,
            final List<String> cList) throws FileNotFoundException {

    	// open the netCDF file
        NetcdfFile ncFile = null;
    	try {
			ncFile = NetcdfFile.open(path);
		} catch (IOException e) {
			return false;
        } finally {
            if (ncFile != null) {
                try {
                    ncFile.close();
                } catch (IOException e) {
                }
            }
        }

    	SGNetCDFFile nc = new SGNetCDFFile(ncFile);

        // create column info map
        Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put(KEY_DATA_SOURCE, nc);
        infoMap.put(KEY_MULTIPLE, Boolean.FALSE);
        infoMap.put(KEY_MULTIPLE_VARIABLE, Boolean.TRUE);
        infoMap.put(KEY_POLAR_SELECTED, Boolean.TRUE);

        // create column list
        List<SGVariable> varList = nc.getVariables();
        final int size = varList.size();
        SGNetCDFDataColumnInfo[] cols = new SGNetCDFDataColumnInfo[size];
        int cnt = 0;
        for (SGVariable var : varList) {
            String name = var.getNameInPriorityOrder();
            cols[cnt] = new SGNetCDFDataColumnInfo(var, name, var.getValueType());
            cnt++;
        }
        List<SGDataColumnInfo> columnInfoList = new ArrayList<SGDataColumnInfo>(
        		Arrays.asList(cols));

        // get available data types
        String[] dataTypes = new String[] {
        		SGDataTypeConstants.SXY_NETCDF_DATA,
//        		SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA,
        		SGDataTypeConstants.VXY_NETCDF_DATA,
        		SGDataTypeConstants.SXYZ_NETCDF_DATA
        };
        for (int ii = 0; ii < dataTypes.length; ii++) {
            SGDataUtility.DefaultColumnTypeResult result = SGDataUtility
					.getDefaultColumnTypes(dataTypes[ii],
							columnInfoList, infoMap);
            if (result.isSucceeded()) {
            	cList.add(dataTypes[ii]);
            }
        }

        return true;
    }
    */

    private boolean getTextDataTypeCandidateList(final String path,
            final List<String> cList) throws FileNotFoundException {

    	SGBufferedFileReader reader = null;
        try {
        	reader = new SGBufferedFileReader(path);
            BufferedReader br = reader.getBufferedReader();

            // get a list of tokens from the first line
            List<Token> firstTokenList = SGUtilityText.getFirstTokenList(br);
            if (firstTokenList == null) {
                return false;
            }

            // get number convertible columns
            List<Integer> firstIndexList = SGDataUtility.getColumnIndexListOfNumber(
            		firstTokenList);

            // skip titles if they exist
            if (SGApplicationUtility.evaluteTitleList(firstIndexList)) {
                // this is title. read next line for indexList
                firstTokenList = SGUtilityText.getFirstTokenList(br);
                if (firstTokenList == null || firstTokenList.size() == 0) {
                    return false;
                }
                firstIndexList = SGDataUtility.getColumnIndexListOfNumber(firstTokenList);
            }

            // check all values
            List<String>[] listArray = SGApplicationUtility.createListArray(path);
            if (listArray == null) {
                return false;
            }
            List<Integer> indexList = new ArrayList<Integer>();
            for (int ii = 0; ii < listArray.length; ii++) {
                Integer firstIndex = (Integer) firstIndexList.get(ii);
                List<String> list = listArray[ii];
                if (firstIndex.intValue() != 0) {
                    // number
                    boolean err = false;
                    for (int jj = 0; jj < list.size(); jj++) {
                        String value = (String) list.get(jj);
                        // check the number
                        if (SGUtilityText.getDouble(value) == null) {
                            err = true;
                            break;
                        }
                    }
                    if (err) {
                        indexList.add(Integer.valueOf(0));
                    } else {
                        indexList.add(Integer.valueOf(1));
                    }
                } else {
                    indexList.add(Integer.valueOf(0));
                }
            }

            // get data type candidates
            if (SGDataUtility.getDataTypeCandidateList(
                    firstTokenList, indexList, cList) == false) {
                return false;
            }

        } catch (IOException ex) {
        	return false;
		} finally {
            if (reader != null) {
            	reader.close();
            }
        }

        return true;
    }

    /**
     * A class which has information of each column of a data file.
     */
    public static class FileColumn {
        // column index
        int index = -1;

        // title
        String title = null;

        // data type
        String valueType = null;

        /**
         * Returns a string representation.
         */
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append('[');
            sb.append("index=");
            sb.append(this.index);
            sb.append(", title=");
            sb.append(this.title);
            sb.append(", valueType=");
            sb.append(this.valueType);
            sb.append(']');
            return sb.toString();
        }
    }

    public static class SDArrayFileParseResult {
    	FileColumn[] fileColumns = null;
    	int length = -1;
    	SDArrayFileParseResult() {
    		super();
    	}
    }

    /**
     * Read a given file and parse the type of each column.
     *
     * @param path
     *            the file path
     * @param dataType
     *            the type of data
     * @param isPropertyFileData
     *            set true if data is for the property file
     * @param versionNumber
     *            the version number of the property file
     * @return result of parse
     * @throws FileNotFoundException
     */
    public SDArrayFileParseResult parseFileComlumnType(final String path, final String dataType,
            final boolean isPropertyFileData, final String versionNumber)
            throws FileNotFoundException {
        FileColumn[] fileColumns = null;
        int len = -1;
        SGBufferedFileReader reader = null;
        try {
        	reader = new SGBufferedFileReader(path);
            BufferedReader br = reader.getBufferedReader();

            List<Token> tokenList = SGUtilityText.getFirstTokenList(br);
            if (tokenList == null) {
                return null;
            }
            final int colNum = tokenList.size();
            fileColumns = new FileColumn[colNum];

            // get number convertible columns
            List<Integer> indexList = SGDataUtility.getColumnIndexListOfNumber(tokenList);

            // get titles
            String[] titles = new String[colNum];
            if (SGApplicationUtility.evaluteTitleList(indexList)) {
                // this line represents titles
                for (int ii = 0; ii < colNum; ii++) {
                    Token token = (Token) tokenList.get(ii);
                    titles[ii] = token.getString();
                }

                // read next line
                tokenList = SGUtilityText.getFirstTokenList(br);
                if (tokenList == null) {
                    return null;
                }
                if (tokenList.size() != colNum) {
                    return null;
                }
            } else {
                for (int ii = 0; ii < colNum; ii++) {
                    titles[ii] = null;
                }
            }

            for (int ii = 0; ii < colNum; ii++) {
                fileColumns[ii] = new FileColumn();
                fileColumns[ii].index = ii;
                fileColumns[ii].title = titles[ii];
                String valueType = null;
                Token token = (Token) tokenList.get(ii);
                if (token.isDoubleQuoted()) {
                	SGDate d = SGUtilityText.getDate(token.getString());
                    if (d != null) {
                        valueType = VALUE_TYPE_DATE;
                    } else {
                        valueType = VALUE_TYPE_TEXT;
                    }
                } else {
                    Number num = SGUtilityText.getDouble(token.getString());
                    if (num != null) {
                        valueType = VALUE_TYPE_NUMBER;
                    } else {
                    	SGDate d = SGUtilityText.getDate(token.getString());
                        if (d != null) {
                            valueType = VALUE_TYPE_DATE;
                        } else {
                            valueType = VALUE_TYPE_TEXT;
                        }
                    }
                }
                fileColumns[ii].valueType = valueType;
            }

            // check values in all number columns and date columns
            List<Integer> numColList = new ArrayList<Integer>();
            List<Integer> dateColList = new ArrayList<Integer>();
            for (int ii = 0; ii < colNum; ii++) {
                if (VALUE_TYPE_NUMBER.equals(fileColumns[ii].valueType)) {
                    numColList.add(Integer.valueOf(ii));
                } else if (VALUE_TYPE_DATE.equals(fileColumns[ii].valueType)) {
                    dateColList.add(Integer.valueOf(ii));
                }
            }

            if (numColList.size() != 0 || dateColList.size() != 0) {

                // get all values
            	List<String>[] listArray = SGApplicationUtility.createListArray(path);
                if (listArray == null) {
                    return null;
                }

                // get the length
                if (listArray.length > 0) {
                	List<String> list = listArray[0];
                	len = list.size();
                }

                // check number columns
                List<Integer> errNumColList = new ArrayList<Integer>();
                for (int ii = 0; ii < numColList.size(); ii++) {
                    Integer colIndex = (Integer) numColList.get(ii);
                    List<String> list = listArray[colIndex.intValue()];
                    boolean valid = true;
                    for (int jj = 0; jj < list.size(); jj++) {
                        String value = list.get(jj);
                        Double d = SGUtilityText.getDouble(value);
                        if (d == null) {
                            valid = false;
                        }
                    }
                    if (!valid) {
                        errNumColList.add(colIndex);
                    }
                }

                // check date columns
                List<Integer> errDateColList = new ArrayList<Integer>();
                for (int ii = 0; ii < dateColList.size(); ii++) {
                    Integer colIndex = (Integer) dateColList.get(ii);
                    List<String> list = listArray[colIndex.intValue()];
                    boolean valid = true;
                    for (int jj = 0; jj < list.size(); jj++) {
                        String value = list.get(jj);
                        SGDate d = SGUtilityText.getDate(value);
                        if (d == null) {
                            valid = false;
                        }
                    }
                    if (!valid) {
                            errDateColList.add(colIndex);
                    }
                }

                // replace the column type from number or date to text
                for (int ii = 0; ii < errNumColList.size(); ii++) {
                    Integer colIndex = (Integer) errNumColList.get(ii);
                    fileColumns[colIndex.intValue()].valueType = VALUE_TYPE_TEXT;
                }
                for (int ii = 0; ii < errDateColList.size(); ii++) {
                    Integer colIndex = (Integer) errDateColList.get(ii);
                    fileColumns[colIndex.intValue()].valueType = VALUE_TYPE_TEXT;
                }
            }

            // measures for version older than 2.0.0
            if (isPropertyFileData) {
                if (versionNumber != null) {
                    // if version of the property file is older than 2.0.0
                    if ("".equals(versionNumber)) {
                        // data with tick labels
                        if (SGDataUtility.hasTickLabels(dataType)) {
                            // the third and fifth columns are forced to be a text column
                            if (colNum == 3 || colNum == 5) {
                                fileColumns[colNum - 1].valueType = VALUE_TYPE_TEXT;
                            }
                        }
                    }
                }
            }

        } catch (IOException ex) {
            return null;
		} finally {
            if (reader != null) {
            	reader.close();
            }
        }

		SDArrayFileParseResult result = new SDArrayFileParseResult();
		result.fileColumns = fileColumns;
		result.length = len;

        return result;
    }


    /**
     * Creates an array of SXY NetCDF Data.
     *
     * @return created data
     */
    private CreatedDataSet createSingleVariableSXYNetCDFData(final SGIDataSource dataSource,
            final SGIProgressControl progress, final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap) {

        SGNetCDFFile file = (SGNetCDFFile) dataSource;
        final SGDataColumnInfo[] colInfo = colInfoSet.getDataColumnInfoArray();

        // get column names
        List<SGDataColumnInfo> xInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> yInfoList = new ArrayList<SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> leInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> ueInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> tlInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        List<SGDataColumnInfo> timeInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> indexInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> pickUpInfoList = new ArrayList<SGDataColumnInfo>();
        if (!SGDataUtility.getSXYColumnType(colInfo, xInfoList,
                yInfoList, leInfoMap, ueInfoMap, tlInfoMap, timeInfoList,
                indexInfoList, pickUpInfoList)) {
            return null;
        }
        if (xInfoList.size() != 1 && yInfoList.size() != 1) {
            return null;
        }
        if (pickUpInfoList.size() > 0) {
        	return null;
        }

        SGNetCDFDataColumnInfo timeInfo = null;
        if (timeInfoList.size() != 0) {
            timeInfo = (SGNetCDFDataColumnInfo) timeInfoList.get(0);
        }
        SGNetCDFDataColumnInfo indexInfo = null;
        if (indexInfoList.size() != 0) {
            indexInfo = (SGNetCDFDataColumnInfo) indexInfoList.get(0);
        }

        // get the stride
        SGIntegerSeriesSet stride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
        SGIntegerSeriesSet tickLabelStride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
        SGIntegerSeriesSet indexStride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
        final boolean strideAvailable = this.isStrideAvailable(infoMap);

        // create data objects
        List<SGISXYTypeSingleData> dataList = new ArrayList<SGISXYTypeSingleData>();
        List<String> titleList = new ArrayList<String>();
        if (xInfoList.size() == 1 && yInfoList.size() == 1) {
            final SGNetCDFDataColumnInfo xInfo = (SGNetCDFDataColumnInfo) xInfoList.get(0);
            final SGNetCDFDataColumnInfo yInfo = (SGNetCDFDataColumnInfo) yInfoList.get(0);
            SGNetCDFDataColumnInfo xlInfo = (SGNetCDFDataColumnInfo) leInfoMap.get(xInfo);
            SGNetCDFDataColumnInfo ylInfo = (SGNetCDFDataColumnInfo) leInfoMap.get(yInfo);
            SGNetCDFDataColumnInfo xuInfo = (SGNetCDFDataColumnInfo) ueInfoMap.get(xInfo);
            SGNetCDFDataColumnInfo yuInfo = (SGNetCDFDataColumnInfo) ueInfoMap.get(yInfo);
            SGNetCDFDataColumnInfo leInfo, ueInfo, ehInfo;
            if (xlInfo != null && xuInfo != null) {
                leInfo = xlInfo;
                ueInfo = xuInfo;
                ehInfo = xInfo;
            } else 	if (ylInfo != null && yuInfo != null) {
                leInfo = ylInfo;
                ueInfo = yuInfo;
                ehInfo = yInfo;
            } else {
                leInfo = null;
                ueInfo = null;
                ehInfo = null;
            }
            SGNetCDFDataColumnInfo xtInfo = (SGNetCDFDataColumnInfo) tlInfoMap.get(xInfo);
            SGNetCDFDataColumnInfo ytInfo = (SGNetCDFDataColumnInfo) tlInfoMap.get(yInfo);
            SGNetCDFDataColumnInfo tlInfo, thInfo;
            if (xtInfo != null) {
                tlInfo = xtInfo;
                thInfo = xInfo;
            } else if (ytInfo != null) {
                tlInfo = ytInfo;
                thInfo = yInfo;
            } else {
                tlInfo = null;
                thInfo = null;
            }

            SGSXYNetCDFData data;
            if (file.findVariable(xInfo.getName()) instanceof SGDateVariable
            		|| file.findVariable(yInfo.getName()) instanceof SGDateVariable) {
                data = new SGSXYNetCDFDateData(file,
                        this.mDataSourceObserver, xInfo, yInfo, leInfo, ueInfo, ehInfo,
                        tlInfo, thInfo, timeInfo, indexInfo, stride, indexStride,
                        tickLabelStride, strideAvailable);
            } else {
                data = new SGSXYNetCDFData(file,
                        this.mDataSourceObserver, xInfo, yInfo, leInfo, ueInfo, ehInfo,
                        tlInfo, thInfo, timeInfo, indexInfo, stride, indexStride,
                        tickLabelStride, strideAvailable);
            }
            dataList.add(data);

            // set the title
            titleList.add(null);

            // set origins
            this.setOrigin(colInfo, file, data);

        } else if (xInfoList.size() == 1) {
        	SGNetCDFDataColumnInfo xInfo = (SGNetCDFDataColumnInfo) xInfoList.get(0);
            for (int ii = 0; ii < yInfoList.size(); ii++) {
                final SGNetCDFDataColumnInfo yInfo = (SGNetCDFDataColumnInfo) yInfoList.get(ii);
                SGNetCDFDataColumnInfo leInfo = (SGNetCDFDataColumnInfo) leInfoMap.get(yInfo);
                SGNetCDFDataColumnInfo ueInfo = (SGNetCDFDataColumnInfo) ueInfoMap.get(yInfo);
                SGNetCDFDataColumnInfo tlInfo = (SGNetCDFDataColumnInfo) tlInfoMap.get(yInfo);
                SGSXYNetCDFData data = new SGSXYNetCDFData(file,
                        this.mDataSourceObserver, xInfo, yInfo, leInfo, ueInfo, yInfo,
                        tlInfo, yInfo, timeInfo, indexInfo, stride, indexStride,
                        tickLabelStride, strideAvailable);
                dataList.add(data);

                // set the title
                titleList.add(yInfo.getName());

                // set origins
                this.setOrigin(colInfo, file, data);
            }

        } else {
        	SGNetCDFDataColumnInfo yInfo = (SGNetCDFDataColumnInfo) yInfoList.get(0);
            for (int ii = 0; ii < xInfoList.size(); ii++) {
                final SGNetCDFDataColumnInfo xInfo = (SGNetCDFDataColumnInfo) xInfoList.get(ii);
                SGNetCDFDataColumnInfo leInfo = (SGNetCDFDataColumnInfo) leInfoMap.get(xInfo);
                SGNetCDFDataColumnInfo ueInfo = (SGNetCDFDataColumnInfo) ueInfoMap.get(xInfo);
                SGNetCDFDataColumnInfo tlInfo = (SGNetCDFDataColumnInfo) tlInfoMap.get(yInfo);
                SGSXYNetCDFData data = new SGSXYNetCDFData(file,
                        this.mDataSourceObserver, xInfo, yInfo, leInfo, ueInfo, xInfo,
                        tlInfo, xInfo, timeInfo, indexInfo, stride, indexStride,
                        tickLabelStride, strideAvailable);
                dataList.add(data);

                // set the title
                titleList.add(xInfo.getName());

                // set origins
                this.setOrigin(colInfo, file, data);
            }
        }

        // modifies the titles
        List<String> titleListNew = new ArrayList<String>();
        for (int ii = 0; ii < titleList.size(); ii++) {
        	String title = titleList.get(ii);
        	if (title == null) {
        		titleListNew.add(null);
        		continue;
        	}
        	title = SGUtility.addEscapeChar(title);
        	titleListNew.add(title);
        }
        titleList = titleListNew;

        // create an array of multiple data
        SGData[] dataArray = new SGData[dataList.size()];
        for (int ii = 0; ii < dataArray.length; ii++) {
            SGSXYNetCDFData data = (SGSXYNetCDFData) dataList.get(ii);
            dataArray[ii] = (SGData) data.toMultiple();
        }

        // creates a result
        String[] titleArray = titleList.toArray(new String[titleList.size()]);
        CreatedDataSet cdSet = new CreatedDataSet();
        for (int ii = 0; ii < titleArray.length; ii++) {
            cdSet.addData(dataArray[ii], titleArray[ii]);
        }

        // disposes data objects
        for (int ii = 0; ii < dataArray.length; ii++) {
            SGData data = (SGData) dataList.get(ii);
            data.dispose();
        }

        return cdSet;
    }

    /**
     * Creates SXY NetCDF Data with multiple variables.
     *
     * @return created data
     */
    private CreatedDataSet createMultipleVariableSXYNetCDFData(final SGIDataSource dataSource,
            final SGIProgressControl progress, final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap) {

        SGNetCDFFile file = (SGNetCDFFile) dataSource;
        final SGDataColumnInfo[] colInfo = colInfoSet.getDataColumnInfoArray();

        // get the stride
        SGIntegerSeriesSet sxyStride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
        SGIntegerSeriesSet indexStride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
        SGIntegerSeriesSet stride;
        if (indexStride != null) {
        	stride = indexStride;
        } else {
        	stride = sxyStride;
        }
        SGIntegerSeriesSet tickLabelStride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
        final boolean strideAvailable = this.isStrideAvailable(infoMap);

        SGSXYNetCDFMultipleData data = this.createVariableNetCDFData(file,
        		this.mDataSourceObserver, colInfo, stride, tickLabelStride, strideAvailable);

        // set origins
        this.setOrigin(colInfo, file, data);

        // creates a result
        CreatedDataSet cdSet = new CreatedDataSet();
        cdSet.addData(data);

        return cdSet;
    }
    
    private SGSXYNetCDFMultipleData createVariableNetCDFData(
            final SGNetCDFFile ncFile,
            final SGDataSourceObserver obs,
            final SGDataColumnInfo[] colInfo, SGIntegerSeriesSet stride,
            SGIntegerSeriesSet tickLabelStride, final boolean strideAvailable) {

        List<SGDataColumnInfo> xInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> yInfoList = new ArrayList<SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> leInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> ueInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> tlInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        List<SGDataColumnInfo> timeInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> indexInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> pickUpInfoList = new ArrayList<SGDataColumnInfo>();
        if (!SGDataUtility.getSXYColumnType(colInfo, xInfoList,
                yInfoList, leInfoMap, ueInfoMap, tlInfoMap, timeInfoList, indexInfoList, pickUpInfoList)) {
            return null;
        }
        if (leInfoMap.size() != ueInfoMap.size()) {
            return null;
        }
        SGNetCDFDataColumnInfo[] xNames = xInfoList.toArray(new SGNetCDFDataColumnInfo[xInfoList.size()]);
        SGNetCDFDataColumnInfo[] yNames = yInfoList.toArray(new SGNetCDFDataColumnInfo[yInfoList.size()]);

        Iterator<SGDataColumnInfo> eItr = leInfoMap.keySet().iterator();
        List<SGDataColumnInfo> lNameList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> uNameList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> ehNameList = new ArrayList<SGDataColumnInfo>();
        while (eItr.hasNext()) {
        	SGDataColumnInfo eh = eItr.next();
            ehNameList.add(eh);
            SGDataColumnInfo lName = leInfoMap.get(eh);
            if (lName != null) {
                lNameList.add(lName);
            }
            SGDataColumnInfo uName = ueInfoMap.get(eh);
            if (uName != null) {
                uNameList.add(uName);
            }
        }
        if (lNameList.size() != uNameList.size()) {
            return null;
        }
        if (lNameList.size() != ehNameList.size()) {
            return null;
        }
        SGNetCDFDataColumnInfo[] lNames = lNameList.toArray(new SGNetCDFDataColumnInfo[lNameList.size()]);
        SGNetCDFDataColumnInfo[] uNames = uNameList.toArray(new SGNetCDFDataColumnInfo[uNameList.size()]);
        SGNetCDFDataColumnInfo[] ehNames = ehNameList.toArray(new SGNetCDFDataColumnInfo[ehNameList.size()]);

        Iterator<SGDataColumnInfo> tItr = tlInfoMap.keySet().iterator();
        List<SGDataColumnInfo> tNameList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> thNameList = new ArrayList<SGDataColumnInfo>();
        while (tItr.hasNext()) {
        	SGDataColumnInfo th = tItr.next();
            thNameList.add(th);
            SGDataColumnInfo tName = tlInfoMap.get(th);
            if (tName != null) {
                tNameList.add(tName);
            }
        }
        if (tNameList.size() != thNameList.size()) {
            return null;
        }
        SGNetCDFDataColumnInfo[] tNames = tNameList.toArray(new SGNetCDFDataColumnInfo[tNameList.size()]);
        SGNetCDFDataColumnInfo[] thNames = thNameList.toArray(new SGNetCDFDataColumnInfo[thNameList.size()]);

        SGNetCDFDataColumnInfo timeName = null;
        if (timeInfoList.size() != 0) {
            timeName = (SGNetCDFDataColumnInfo) timeInfoList.get(0);
        }
        SGNetCDFDataColumnInfo indexName = null;
        if (indexInfoList.size() != 0) {
            indexName = (SGNetCDFDataColumnInfo) indexInfoList.get(0);
        }

        SGSXYNetCDFMultipleData data = new SGSXYNetCDFMultipleData(
                ncFile, obs, xNames, yNames, lNames, uNames, ehNames,
                tNames, thNames, timeName, indexName, stride, tickLabelStride, strideAvailable);

        return data;
    }

    private SGSXYMDArrayMultipleData createVariableMDData(
            final SGMDArrayFile file, final SGDataSourceObserver obs,
            final SGDataColumnInfo[] colInfo, Map<String, Object> infoMap,
            final boolean strideAvailable) {

        List<SGDataColumnInfo> xInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> yInfoList = new ArrayList<SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> leInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> ueInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> tlInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        List<SGDataColumnInfo> timeNameList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> indexInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> pickUpInfoList = new ArrayList<SGDataColumnInfo>();
        if (!SGDataUtility.getSXYColumnType(colInfo, xInfoList,
                yInfoList, leInfoMap, ueInfoMap, tlInfoMap, timeNameList, indexInfoList, pickUpInfoList)) {
            return null;
        }
        if (leInfoMap.size() != ueInfoMap.size()) {
            return null;
        }
        SGMDArrayDataColumnInfo[] xNames = xInfoList.toArray(new SGMDArrayDataColumnInfo[xInfoList.size()]);
        SGMDArrayDataColumnInfo[] yNames = yInfoList.toArray(new SGMDArrayDataColumnInfo[yInfoList.size()]);

        Iterator<SGDataColumnInfo> eItr = leInfoMap.keySet().iterator();
        List<SGDataColumnInfo> lNameList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> uNameList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> ehNameList = new ArrayList<SGDataColumnInfo>();
        while (eItr.hasNext()) {
        	SGDataColumnInfo eh = eItr.next();
            ehNameList.add(eh);
            SGDataColumnInfo lName = leInfoMap.get(eh);
            if (lName != null) {
                lNameList.add(lName);
            }
            SGDataColumnInfo uName = ueInfoMap.get(eh);
            if (uName != null) {
                uNameList.add(uName);
            }
        }
        if (lNameList.size() != uNameList.size()) {
            return null;
        }
        if (lNameList.size() != ehNameList.size()) {
            return null;
        }
        SGMDArrayDataColumnInfo[] lNames = lNameList.toArray(new SGMDArrayDataColumnInfo[lNameList.size()]);
        SGMDArrayDataColumnInfo[] uNames = uNameList.toArray(new SGMDArrayDataColumnInfo[uNameList.size()]);
        SGMDArrayDataColumnInfo[] ehNames = ehNameList.toArray(new SGMDArrayDataColumnInfo[ehNameList.size()]);

        Iterator<SGDataColumnInfo> tItr = tlInfoMap.keySet().iterator();
        List<SGDataColumnInfo> tNameList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> thNameList = new ArrayList<SGDataColumnInfo>();
        while (tItr.hasNext()) {
        	SGDataColumnInfo th = tItr.next();
            thNameList.add(th);
            SGDataColumnInfo tName = tlInfoMap.get(th);
            if (tName != null) {
                tNameList.add(tName);
            }
        }
        if (tNameList.size() != thNameList.size()) {
            return null;
        }
        SGMDArrayDataColumnInfo[] tNames = tNameList.toArray(new SGMDArrayDataColumnInfo[tNameList.size()]);
        SGMDArrayDataColumnInfo[] thNames = thNameList.toArray(new SGMDArrayDataColumnInfo[thNameList.size()]);

        SGIntegerSeriesSet stride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
        SGIntegerSeriesSet tickLabelStride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);

        SGSXYMDArrayMultipleData data = new SGSXYMDArrayMultipleData(
                file, obs,
                xNames, yNames, lNames, uNames, ehNames,
                tNames, thNames, stride, tickLabelStride, strideAvailable);

        return data;
    }

    private CreatedDataSet createDimensionSXYNetCDFData(final SGIDataSource dataSource,
            final SGIProgressControl progress, final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap, final boolean multiple) {

        SGNetCDFFile file = (SGNetCDFFile) dataSource;
        final SGDataColumnInfo[] colInfo = colInfoSet.getDataColumnInfoArray();

        // get column names
        List<SGDataColumnInfo> xInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> yInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> leInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> ueInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> tlInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> pickupInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> timeInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> indexInfoList = new ArrayList<SGDataColumnInfo>();
        if (SGDataUtility.getSXYDimensionDataColumnType(
                colInfo, xInfoList, yInfoList,
                leInfoList, ueInfoList, tlInfoList, pickupInfoList, timeInfoList,
                indexInfoList) == false) {
            return null;
        }
        if (xInfoList.size() != 1) {
            return null;
        }
        if (yInfoList.size() != 1) {
            return null;
        }
        if (pickupInfoList.size() != 1) {
            return null;
        }
        if (!(leInfoList.size() == 0 && ueInfoList.size() == 0)
                && !(leInfoList.size() == 1 && ueInfoList.size() == 1)) {
            return null;
        }
        SGNetCDFDataColumnInfo xInfo = (SGNetCDFDataColumnInfo) xInfoList.get(0);
        SGNetCDFDataColumnInfo yInfo = (SGNetCDFDataColumnInfo) yInfoList.get(0);
        SGNetCDFDataColumnInfo leInfo = null;
        if (leInfoList.size() > 0) {
            leInfo = (SGNetCDFDataColumnInfo) leInfoList.get(0);
        }
        SGNetCDFDataColumnInfo ueInfo = null;
        if (ueInfoList.size() > 0) {
            ueInfo = (SGNetCDFDataColumnInfo) ueInfoList.get(0);
        }
        SGNetCDFDataColumnInfo tlInfo = null;
        if (tlInfoList.size() > 0) {
            tlInfo = (SGNetCDFDataColumnInfo) tlInfoList.get(0);
        }
        SGNetCDFDataColumnInfo pickupInfo = (SGNetCDFDataColumnInfo) pickupInfoList.get(0);
        SGNetCDFDataColumnInfo timeInfo = null;
        if (timeInfoList.size() != 0) {
            timeInfo = (SGNetCDFDataColumnInfo) timeInfoList.get(0);
        }
        SGNetCDFDataColumnInfo indexInfo = null;
        if (indexInfoList.size() != 0) {
            indexInfo = (SGNetCDFDataColumnInfo) indexInfoList.get(0);
        }
        SGNetCDFDataColumnInfo ehInfo = null;
        if (leInfo != null) {
        	ehInfo = SGDataUtility.findHolderInfo(colInfo, leInfo);
        }
        SGNetCDFDataColumnInfo thInfo = null;
        if (tlInfo != null) {
        	thInfo = SGDataUtility.findHolderInfo(colInfo, tlInfo);
        }

        // create an array of dimension indices
        Dimension dim = file.findDimension(pickupInfo.getName());
        if (dim == null) {
            return null;
        }
        final String dimName = dim.getName();
        SGIntegerSeriesSet indices = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
        if (indices == null) {
        	Integer start = (Integer) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_START);
        	if (start == null) {
        		return null;
        	}
        	Integer end = (Integer) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_END);
        	if (end == null) {
        		return null;
        	}
        	Integer step = (Integer) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_STEP);
        	if (step == null) {
        		return null;
        	}
        	indices = new SGIntegerSeriesSet(start, end, step);
        }

        // get the stride
        SGIntegerSeriesSet sxyStride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
        SGIntegerSeriesSet indexStride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
        SGIntegerSeriesSet stride;
        if (indexStride != null) {
        	stride = indexStride;
        } else {
        	stride = sxyStride;
        }
        SGIntegerSeriesSet tickLabelStride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
        final boolean strideAvailable = this.isStrideAvailable(infoMap);

        CreatedDataSet cdSet = new CreatedDataSet();
        if (multiple) {
            // create data object
            SGSXYNetCDFMultipleData data = new SGSXYNetCDFMultipleData(
                    file, this.mDataSourceObserver, xInfo, yInfo, leInfo, ueInfo, ehInfo, tlInfo, thInfo,
                    pickupInfo.getName(), indices, timeInfo, indexInfo, stride, tickLabelStride,
                    strideAvailable);

            // set origins
            this.setOrigin(colInfo, file, data);

            // set the stride
            if (stride != null) {
                data.setStride(stride);
            }

            cdSet.addData(data);

        } else {

            int[] dimIndices = indices.getNumbers();
            for (int ii = 0; ii < dimIndices.length; ii++) {
            	SGIntegerSeriesSet index = new SGIntegerSeriesSet();
            	index.add(dimIndices[ii]);

                // create data object
            	SGSXYNetCDFMultipleData data =
                    new SGSXYNetCDFMultipleData(
                            file, this.mDataSourceObserver, xInfo, yInfo, leInfo, ueInfo, ehInfo,
                            tlInfo, thInfo, dimName, index, timeInfo, indexInfo, stride, tickLabelStride,
                            strideAvailable);

                // set origins
                this.setOrigin(colInfo, file, data);

                // set the stride
                if (stride != null) {
                    data.setStride(stride);
                }

                // the suffix for the data name
                String suffix = this.getDimensionDataName(data, ii, dimName, dimIndices[ii]);

                cdSet.addData(data, "("+suffix+")");
            }
            cdSet.setMiddleString(" ");
        }

        return cdSet;
    }

    /**
     * Return the suffix for the data name.
     * <p>
     * If dimIndex is null or data has no value for dimIndex, return index.
     * Otherwise, return "dimName=value".
     * if same string exists in cdSet.
     * @param data
     * @param index
     * @param dimName
     * @param dimIndex
     * @return the suffix for the data name
     */
    private String getDimensionDataName(
            final SGSXYNetCDFMultipleData data, final int index,
            final String dimName, final Integer dimIndex) {
        if (null==dimIndex) {
            return Integer.toString(index);
        }
        Double dvalue = data.getDimensionValue(dimIndex.intValue());
        if (null==dvalue) {
            return Integer.toString(index);
        } else {
            double val = dvalue.doubleValue();
            String name = dimName+"="+SGUtility.getNumberName(val);
            return name;
        }
    }

    /**
     * Creates VXY NetCDF Data.
     *
     * @return a data object
     */
    private CreatedDataSet createVXYNetCDFData(final SGIDataSource dataSource,
            final SGIProgressControl progress, final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap) {

    	SGNetCDFFile file = (SGNetCDFFile) dataSource;
		final SGDataColumnInfo[] colInfo = colInfoSet.getDataColumnInfoArray();

        // get polar flag
        if (infoMap.size() <= 1) {
            return null;
        }
        Object obj = infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED);
        if ((obj instanceof Boolean) == false) {
            return null;
        }
        Boolean b = (Boolean) obj;
        final boolean isPolar = b.booleanValue();

        // allocate memory
        progress.setProgressMessage(PROGRESS_MESSAGE_CREATEDATA);

        // get column indices
        final String type1 = SGDataUtility.getVXYFirstComponentColumnType(infoMap);
        final String type2 = SGDataUtility.getVXYSecondComponentColumnType(infoMap);
        SGNetCDFDataColumnInfo xInfo = null;
        SGNetCDFDataColumnInfo yInfo = null;
        SGNetCDFDataColumnInfo firstInfo = null;
        SGNetCDFDataColumnInfo secondInfo = null;
        SGNetCDFDataColumnInfo timeInfo = null;
        SGNetCDFDataColumnInfo indexInfo = null;
        SGNetCDFDataColumnInfo xIndexInfo = null;
        SGNetCDFDataColumnInfo yIndexInfo = null;
        for (int ii = 0; ii < colInfo.length; ii++) {
        	SGNetCDFDataColumnInfo col = (SGNetCDFDataColumnInfo) colInfo[ii];
            String cType = col.getColumnType();
            if (SGDataUtility.isEqualColumnType(X_COORDINATE, cType)) {
                xInfo = col;
            } else if (SGDataUtility.isEqualColumnType(Y_COORDINATE, cType)) {
                yInfo = col;
            } else if (SGDataUtility.isEqualColumnType(type1, cType)) {
                firstInfo = col;
            } else if (SGDataUtility.isEqualColumnType(type2, cType)) {
                secondInfo = col;
            } else if (SGDataUtility.isEqualColumnType(ANIMATION_FRAME, cType)
            		|| SGDataUtility.isEqualColumnType(TIME, cType)) {
                timeInfo = col;
            } else if (SGDataUtility.isEqualColumnType(INDEX, cType)) {
                indexInfo = col;
            } else if (SGDataUtility.isEqualColumnType(X_INDEX, cType)) {
                xIndexInfo = col;
            } else if (SGDataUtility.isEqualColumnType(Y_INDEX, cType)) {
                yIndexInfo = col;
            } else if ("".equals(cType)) {
                continue;
            } else {
                throw new Error("Illegal Column Type: " + cType);
            }
        }

        // get stride
        SGIntegerSeriesSet xStride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X);
        SGIntegerSeriesSet yStride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y);
        SGIntegerSeriesSet indexStride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE);
        final boolean strideAvailable = this.isStrideAvailable(infoMap);

        // create data object
        SGVXYNetCDFData data = new SGVXYNetCDFData(file,
        		this.mDataSourceObserver, xInfo, yInfo, firstInfo, secondInfo, isPolar, timeInfo,
        		indexInfo, xIndexInfo, yIndexInfo, indexStride, xStride, yStride, strideAvailable);

        // set origins
        this.setOrigin(colInfo, file, data);

        CreatedDataSet cdSet = new CreatedDataSet();
        cdSet.addData(data);
        return cdSet;
    }

    /**
     * Creates SXYZ NetCDF Data.
     *
     * @return a data object
     */
    private CreatedDataSet createSXYZNetCDFData(final SGIDataSource dataSource,
            final SGIProgressControl progress, final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap) {

    	SGNetCDFFile file = (SGNetCDFFile) dataSource;
		final SGDataColumnInfo[] colInfo = colInfoSet.getDataColumnInfoArray();

        // get column indices
        SGNetCDFDataColumnInfo xInfo = null;
        SGNetCDFDataColumnInfo yInfo = null;
        SGNetCDFDataColumnInfo zInfo = null;
        SGNetCDFDataColumnInfo timeInfo = null;
        SGNetCDFDataColumnInfo indexInfo = null;
        SGNetCDFDataColumnInfo xIndexInfo = null;
        SGNetCDFDataColumnInfo yIndexInfo = null;
        for (int ii = 0; ii < colInfo.length; ii++) {
        	SGNetCDFDataColumnInfo col = (SGNetCDFDataColumnInfo) colInfo[ii];
            String cType = col.getColumnType();
            if (SGDataUtility.isEqualColumnType(X_VALUE, cType)) {
                xInfo = col;
            } else if (SGDataUtility.isEqualColumnType(Y_VALUE, cType)) {
                yInfo = col;
            } else if (SGDataUtility.isEqualColumnType(Z_VALUE, cType)) {
                zInfo = col;
            } else if (SGDataUtility.isEqualColumnType(ANIMATION_FRAME, cType)
            		|| SGDataUtility.isEqualColumnType(TIME, cType)) {
                timeInfo = col;
            } else if (SGDataUtility.isEqualColumnType(INDEX, cType)) {
                indexInfo = col;
            } else if (SGDataUtility.isEqualColumnType(X_INDEX, cType)) {
                xIndexInfo = col;
            } else if (SGDataUtility.isEqualColumnType(Y_INDEX, cType)) {
                yIndexInfo = col;
            } else if ("".equals(cType)) {
                continue;
            } else {
                throw new Error("Illegal Column Type: " + cType);
            }
        }

        // get stride
        SGIntegerSeriesSet xStride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X);
        SGIntegerSeriesSet yStride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y);
        SGIntegerSeriesSet indexStride = (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE);
        final boolean strideAvailable = this.isStrideAvailable(infoMap);

        // create data object
        SGSXYZNetCDFData data = new SGSXYZNetCDFData(file,
        		this.mDataSourceObserver, xInfo, yInfo, zInfo, timeInfo, indexInfo, xIndexInfo, yIndexInfo,
        		indexStride, xStride, yStride, strideAvailable);

        // set origins
        this.setOrigin(colInfo, file, data);

        CreatedDataSet cdSet = new CreatedDataSet();
        cdSet.addData(data);
        return cdSet;
    }

    // Sets the origin to data.
    private void setOrigin(SGDataColumnInfo[] colInfo, SGNetCDFFile ncFile,
            SGNetCDFData ncData) {
        for (int ii = 0; ii < colInfo.length; ii++) {
            SGNetCDFDataColumnInfo col = (SGNetCDFDataColumnInfo) colInfo[ii];
            final String name = col.getName();
            final int origin = col.getOrigin();
            if (ncFile.findDimension(name) != null) {
                ncData.setOrigin(name, origin);
            }
        }
    }

    // Sets the origin to data.
    private void setMDInfo(SGDataColumnInfo[] colInfo, SGMDArrayFile file,
            SGMDArrayData mdData) {
        for (int ii = 0; ii < colInfo.length; ii++) {
            SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) colInfo[ii];
            final String name = col.getName();
        	mdData.setOrigin(name, col.getOrigins());
        	mdData.setDimensionIndex(name, col.getGenericDimensionIndex());
        }
    }

    /**
     * Creates a data object.
     *
     * @param dataSource
     *           the data source
     * @param colInfoSet
     *           a set of column information
     * @param infoMap
     *            information of data
     * @param progress
     *            the progress monitor
     * @return a set of created data
     */
    public CreatedDataSet create(SGIDataSource dataSource,
            final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap, final SGIProgressControl progress) {
//        progress.setIndeterminateProgress(true);
        return this.createData(dataSource, colInfoSet, infoMap, progress);
    }

    /**
     * Creates a data object.
     */
    private CreatedDataSet createData(SGIDataSource dataSource,
            final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap, final SGIProgressControl progress) {

        progress.setProgressMessage(PROGRESS_MESSAGE_CREATEDATA);
        progress.startIndeterminateProgress();

    	CreatedDataSet cdSet = null;
        try {
            if (dataSource == null || colInfoSet == null || infoMap == null) {
            	return null;
            }

            // when information is empty, returns null
            if (infoMap.size() == 0) {
                return null;
            }

            final String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);

            if (SGDataUtility.isSDArrayData(dataType)) {
                SGSDArrayFile file = (SGSDArrayFile) dataSource;
                if (SGDataUtility.isSXYTypeData(dataType)) {
                    Boolean multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
                    if (multiple == null) {
                        return null;
                    }
                    if (multiple) {
                        cdSet = this.createMultipleSXYSDArrayData(
                                file, progress, colInfoSet, infoMap);
                    } else {
                        cdSet = this.createSingleSXYSDArrayData(
                                file, progress, colInfoSet, infoMap);
                    }
				} else if (SGDataUtility.isVXYTypeData(dataType)) {
                    cdSet = this.createVXYSDArrayData(file, progress, colInfoSet, infoMap);
				} else if (SGDataUtility.isSXYZTypeData(dataType)) {
                    cdSet = this.createSXYZSDArrayData(file, progress, colInfoSet, infoMap);
                }

            } else if (SGDataUtility.isNetCDFData(dataType)) {
                SGNetCDFFile file = (SGNetCDFFile) dataSource;
                if (SGDataUtility.isSXYTypeData(dataType)) {
                    Boolean multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
                    if (multiple == null) {
                        return null;
                    }
                    Boolean multipleVariable = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE);
                    if (multipleVariable == null) {
                        return null;
                    }
                    if (multipleVariable.booleanValue()) {
                        if (multiple.booleanValue()) {
                            cdSet = this.createMultipleVariableSXYNetCDFData(
                                    file, progress, colInfoSet, infoMap);
                        } else {
                            cdSet = this.createSingleVariableSXYNetCDFData(
                                    file, progress, colInfoSet, infoMap);
                        }
                    } else {
                        cdSet = this.createDimensionSXYNetCDFData(
                                file, progress, colInfoSet, infoMap, multiple.booleanValue());
                    }
				} else if (SGDataUtility.isVXYTypeData(dataType)) {
                    cdSet = this.createVXYNetCDFData(file, progress, colInfoSet, infoMap);
				} else if (SGDataUtility.isSXYZTypeData(dataType)) {
                    cdSet = this.createSXYZNetCDFData(file, progress, colInfoSet, infoMap);
                }
            } else if (SGDataUtility.isMDArrayData(dataType)) {
            	SGMDArrayFile file = (SGMDArrayFile) dataSource;
				if (SGDataUtility.isSXYTypeData(dataType)) {
                    Boolean multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
                    if (multiple == null) {
                        return null;
                    }
                    Set<String> keySet = infoMap.keySet();
                    if (keySet.contains(SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP)
                    		&& keySet.contains(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES)) {
                        cdSet = this.createDimensionSXYMDArrayData(
                                file, progress, colInfoSet, infoMap, multiple.booleanValue());
                    } else {
    					if (multiple.booleanValue()) {
    						cdSet = this.createMultipleVariableSXYMDArrayData(file,
    								progress, colInfoSet, infoMap);
    					} else {
    						cdSet = this.createSingleVariableSXYMDArrayData(file,
    								progress, colInfoSet, infoMap);
    					}
                    }
				} else if (SGDataUtility.isVXYTypeData(dataType)) {
                    cdSet = this.createVXYMDArrayData(file, progress, colInfoSet, infoMap);
				} else if (SGDataUtility.isSXYZTypeData(dataType)) {
                    cdSet = this.createSXYZMDArrayData(file, progress, colInfoSet, infoMap);
				}
            } else {
                return null;
            }

        } finally {
            progress.endProgress();
        }

        return cdSet;
    }

    private boolean isStrideAvailable(Map<String, Object> infoMap) {
		Boolean strideAvailable = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE);
		final boolean b;
		if (strideAvailable != null) {
			b = strideAvailable.booleanValue();
		} else {
			b = false;
		}
		return b;
    }

    private boolean setTimeIndexMap(SGMDArrayData mdData, Map<String, Object> infoMap) {
        @SuppressWarnings("unchecked")
		Map<String, Integer> timeIndexMap = (Map<String, Integer>) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_TIME_DIMENSION_INDEX_MAP);
        if (timeIndexMap != null) {
            Iterator<Entry<String, Integer>> itr = timeIndexMap.entrySet().iterator();
            while (itr.hasNext()) {
            	Entry<String, Integer> entry = itr.next();
            	String name = entry.getKey();
            	Integer dim = entry.getValue();
            	if (!mdData.setTimeDimensionIndex(name, dim)) {
            		return false;
            	}
            }
        }
        return true;
    }

    /**
     * Creates a data object for netCDF dataset.
     * @param dataSource
     *           the data source.
     *           dataSource must be SGNetCDFFile because netCDF dataset.
     * @param dataType
     *           the data type of created data.
     * @param colInfoSet
     *           a set of column information
     * @param infoMap
     *            information of data
     * @param progress
     *            the progress monitor
     * @return a set of created data
     */
    CreatedDataSet createForNetCDFDataSet(
            final SGIDataSource dataSource,
            final String dataType,
            final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap, final SGIProgressControl progress) {
        
        progress.setProgressMessage(PROGRESS_MESSAGE_CREATEDATA);
        progress.startIndeterminateProgress();

        CreatedDataSet cdSet = null;
        try {
            if (dataSource == null || colInfoSet == null || infoMap == null) {
                return null;
            }

            // when information is empty, returns null
            if (infoMap.size() == 0) {
                return null;
            }

            if (SGDataUtility.isSDArrayData(dataType)) {
                Boolean multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
                if (multiple == null) {
                    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
                }
                Boolean multipleVariable = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE);
                if (multipleVariable == null) {
                    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
                }
            }

            SGNetCDFFile file = (SGNetCDFFile) dataSource;
            if (SGDataUtility.isSXYTypeData(dataType)) {
                Boolean multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
                if (multiple == null) {
                    return null;
                }
                Boolean multipleVariable = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE);
                if (multipleVariable == null) {
                    return null;
                }
                if (multipleVariable.booleanValue()) {
                    if (multiple.booleanValue()) {
                        cdSet = this.createMultipleVariableSXYNetCDFData(
                                file, progress, colInfoSet, infoMap);
                    } else {
                        cdSet = this.createSingleVariableSXYNetCDFData(
                                file, progress, colInfoSet, infoMap);
                    }
                } else {
                    cdSet = this.createDimensionSXYNetCDFData(
                            file, progress, colInfoSet, infoMap, multiple.booleanValue());
                }
            } else if (SGDataTypeConstants.VXY_NETCDF_DATA.equals(dataType) ||
                    SGDataTypeConstants.VXY_DATA.equals(dataType)) {
                cdSet = this.createVXYNetCDFData(file, progress, colInfoSet, infoMap);
            } else if (SGDataTypeConstants.SXYZ_NETCDF_DATA.equals(dataType) ||
                    SGDataTypeConstants.SXYZ_DATA.equals(dataType)) {
                cdSet = this.createSXYZNetCDFData(file, progress, colInfoSet, infoMap);
            }
        } finally {
            progress.endProgress();
        }

        return cdSet;
    }

    /**
     * Returns data source observer.
     * 
     * @return data source observer
     */
    public SGDataSourceObserver getDataSourceObserver() {
    	return this.mDataSourceObserver;
    }

    /**
     * Replaces the data source.
     *
     * @param data
     *           a data object
     * @param src
     *           a data source object
     */
    void replaceDataSource(final SGData data, final SGIDataSource src) {
    	
    	SGIDataSource srcOld = data.getDataSource();

    	// sets the data source to data
    	data.setDataSource(src);

    	// updates the data source observer
    	this.mDataSourceObserver.unregister(data, srcOld);
    	this.mDataSourceObserver.register(data, src);
    }

    private CreatedDataSet createSingleVariableSXYMDArrayData(final SGIDataSource dataSource,
            final SGIProgressControl progress, final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap) {

    	SGMDArrayFile file = (SGMDArrayFile) dataSource;
        SGDataColumnInfo[] colArray = colInfoSet.getDataColumnInfoArray();

        // get column names
        List<SGDataColumnInfo> xInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> yInfoList = new ArrayList<SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> leInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> ueInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> tlInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        List<SGDataColumnInfo> timeInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> indexInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> pickUpInfoList = new ArrayList<SGDataColumnInfo>();
        if (!SGDataUtility.getSXYColumnType(colArray, xInfoList,
                yInfoList, leInfoMap, ueInfoMap, tlInfoMap, timeInfoList,
                indexInfoList, pickUpInfoList)) {
            return null;
        }
        if (xInfoList.size() == 0 && yInfoList.size() == 0) {
        	return null;
        }

        SGIntegerSeriesSet stride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
        SGIntegerSeriesSet tickLabelStride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
        final boolean strideAvailable = this.isStrideAvailable(infoMap);

        // create data objects
        List<SGISXYTypeSingleData> dataList = new ArrayList<SGISXYTypeSingleData>();
        List<String> titleList = new ArrayList<String>();
        if (xInfoList.size() == 1 && yInfoList.size() == 1) {
            final SGMDArrayDataColumnInfo xInfo = (SGMDArrayDataColumnInfo) xInfoList.get(0);
            final SGMDArrayDataColumnInfo yInfo = (SGMDArrayDataColumnInfo) yInfoList.get(0);
            SGMDArrayDataColumnInfo xlInfo = (SGMDArrayDataColumnInfo) leInfoMap.get(xInfo);
            SGMDArrayDataColumnInfo ylInfo = (SGMDArrayDataColumnInfo) leInfoMap.get(yInfo);
            SGMDArrayDataColumnInfo xuInfo = (SGMDArrayDataColumnInfo) ueInfoMap.get(xInfo);
            SGMDArrayDataColumnInfo yuInfo = (SGMDArrayDataColumnInfo) ueInfoMap.get(yInfo);
            SGMDArrayDataColumnInfo leInfo, ueInfo, ehInfo;
            if (xlInfo != null && xuInfo != null) {
                leInfo = xlInfo;
                ueInfo = xuInfo;
                ehInfo = xInfo;
            } else 	if (ylInfo != null && yuInfo != null) {
                leInfo = ylInfo;
                ueInfo = yuInfo;
                ehInfo = yInfo;
            } else {
                leInfo = null;
                ueInfo = null;
                ehInfo = null;
            }
            SGMDArrayDataColumnInfo xtInfo = (SGMDArrayDataColumnInfo) tlInfoMap.get(xInfo);
            SGMDArrayDataColumnInfo ytInfo = (SGMDArrayDataColumnInfo) tlInfoMap.get(yInfo);
            SGMDArrayDataColumnInfo tlInfo, thInfo;
            if (xtInfo != null) {
                tlInfo = xtInfo;
                thInfo = xInfo;
            } else if (ytInfo != null) {
                tlInfo = ytInfo;
                thInfo = yInfo;
            } else {
                tlInfo = null;
                thInfo = null;
            }
			SGSXYMDArrayData data = new SGSXYMDArrayData(file,
					this.mDataSourceObserver, xInfo, yInfo, leInfo, ueInfo,
					ehInfo, tlInfo, thInfo, stride, tickLabelStride, strideAvailable);
            dataList.add(data);

            this.setTimeIndexMap(data, infoMap);

            // set the title
            titleList.add(null);

            // set the dimension index and origins
            this.setMDInfo(colArray, file, data);

        } else if ((xInfoList.size() == 1 || xInfoList.size() == 0) && yInfoList.size() >= 1) {
        	SGMDArrayDataColumnInfo xInfo;
        	if (xInfoList.size() == 1) {
            	xInfo = (SGMDArrayDataColumnInfo) xInfoList.get(0);
        	} else {
        		xInfo = null;
        	}
            for (int ii = 0; ii < yInfoList.size(); ii++) {
                final SGMDArrayDataColumnInfo yInfo = (SGMDArrayDataColumnInfo) yInfoList.get(ii);
                SGMDArrayDataColumnInfo leInfo = (SGMDArrayDataColumnInfo) leInfoMap.get(yInfo);
                SGMDArrayDataColumnInfo ueInfo = (SGMDArrayDataColumnInfo) ueInfoMap.get(yInfo);
                SGMDArrayDataColumnInfo tlInfo = (SGMDArrayDataColumnInfo) tlInfoMap.get(yInfo);
                SGSXYMDArrayData data = new SGSXYMDArrayData(file,
                        this.mDataSourceObserver, xInfo, yInfo, leInfo, ueInfo, yInfo,
                        tlInfo, yInfo, stride, tickLabelStride, strideAvailable);
                dataList.add(data);

                this.setTimeIndexMap(data, infoMap);

                // set the title
                if (yInfoList.size() > 1) {
                    titleList.add(yInfo.getName());
                } else {
                	titleList.add(null);
                }

                // set the dimension index and origins
                this.setMDInfo(colArray, file, data);
            }

        } else if ((yInfoList.size() == 1 || yInfoList.size() == 0) && xInfoList.size() >= 1) {
        	SGMDArrayDataColumnInfo yInfo;
        	if (yInfoList.size() == 1) {
            	yInfo = (SGMDArrayDataColumnInfo) yInfoList.get(0);
        	} else {
        		yInfo = null;
        	}
            for (int ii = 0; ii < xInfoList.size(); ii++) {
                final SGMDArrayDataColumnInfo xInfo = (SGMDArrayDataColumnInfo) xInfoList.get(ii);
                SGMDArrayDataColumnInfo leInfo = (SGMDArrayDataColumnInfo) leInfoMap.get(xInfo);
                SGMDArrayDataColumnInfo ueInfo = (SGMDArrayDataColumnInfo) ueInfoMap.get(xInfo);
                SGMDArrayDataColumnInfo tlInfo = (SGMDArrayDataColumnInfo) tlInfoMap.get(xInfo);
                SGSXYMDArrayData data = new SGSXYMDArrayData(file,
                        this.mDataSourceObserver, xInfo, yInfo, leInfo, ueInfo, xInfo,
                        tlInfo, xInfo, stride, tickLabelStride, strideAvailable);
                dataList.add(data);

                this.setTimeIndexMap(data, infoMap);

                // set the title
                if (xInfoList.size() > 1) {
                    titleList.add(xInfo.getName());
                } else {
                    titleList.add(null);
                }

                // set the dimension index and origins
                this.setMDInfo(colArray, file, data);
            }
        }

        // modifies the titles
        List<String> titleListNew = new ArrayList<String>();
        for (int ii = 0; ii < titleList.size(); ii++) {
        	String title = titleList.get(ii);
        	if (title == null) {
        		titleListNew.add(null);
        		continue;
        	}
        	title = SGUtility.addEscapeChar(title);
        	titleListNew.add(title);
        }
        titleList = titleListNew;

        SGData[] dataArray = new SGData[dataList.size()];
        for (int ii = 0; ii < dataArray.length; ii++) {
            SGSXYMDArrayData data = (SGSXYMDArrayData) dataList.get(ii);
            dataArray[ii] = (SGData) data.toMultiple();
            
            // sets the origins
            this.setOrigins(colArray, (SGMDArrayData) dataArray[ii]);
        }

        String[] titleArray = titleList.toArray(new String[titleList.size()]);
        CreatedDataSet cdSet = new CreatedDataSet();
        for (int ii = 0; ii < titleArray.length; ii++) {
            cdSet.addData(dataArray[ii], titleArray[ii]);
        }

        // disposes data objects
        for (int ii = 0; ii < dataArray.length; ii++) {
            SGData data = (SGData) dataList.get(ii);
            data.dispose();
        }

        return cdSet;
    }

    private CreatedDataSet createMultipleVariableSXYMDArrayData(final SGIDataSource dataSource,
            final SGIProgressControl progress, final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap) {

        SGMDArrayFile file = (SGMDArrayFile) dataSource;
        final SGDataColumnInfo[] colInfo = colInfoSet.getDataColumnInfoArray();
        final boolean strideAvailable = this.isStrideAvailable(infoMap);

        SGSXYMDArrayMultipleData data = this.createVariableMDData(file,
        		this.mDataSourceObserver, colInfo, infoMap, strideAvailable);
        this.setTimeIndexMap(data, infoMap);

        // set the dimension index and origins
        this.setMDInfo(colInfo, file, data);

        CreatedDataSet cdSet = new CreatedDataSet();
        cdSet.addData(data);
        return cdSet;
    }

    /**
     * Creates a SXYZ data.
     *
     * @return created data object
     */
    private CreatedDataSet createSXYZMDArrayData(final SGIDataSource dataSource,
    		final SGIProgressControl progress,
    		final SGDataColumnInfoSet colInfoSet, final Map<String, Object> infoMap) {

    	SGMDArrayFile file = (SGMDArrayFile) dataSource;
		final SGDataColumnInfo[] colInfo = colInfoSet.getDataColumnInfoArray();

        // get column indices
		SGMDArrayDataColumnInfo xInfo = null;
		SGMDArrayDataColumnInfo yInfo = null;
		SGMDArrayDataColumnInfo zInfo = null;
        for (int ii = 0; ii < colInfo.length; ii++) {
        	SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfo[ii];
            String cType = mdInfo.getColumnType();
            if (SGDataUtility.isEqualColumnType(X_VALUE, cType)) {
            	xInfo = mdInfo;
            } else if (SGDataUtility.isEqualColumnType(Y_VALUE, cType)) {
            	yInfo = mdInfo;
            } else if (SGDataUtility.isEqualColumnType(Z_VALUE, cType)) {
            	zInfo = mdInfo;
            } else if ("".equals(cType)) {
                continue;
            } else {
                throw new Error("Illegal Column Type: " + cType);
            }
        }

        // gets the stride
        SGIntegerSeriesSet xStride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X);
        SGIntegerSeriesSet yStride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y);
        SGIntegerSeriesSet indexStride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE);
        final boolean strideAvailable = this.isStrideAvailable(infoMap);

        // create a data object
        SGSXYZMDArrayData data = new SGSXYZMDArrayData(file,
        		this.mDataSourceObserver, xInfo, yInfo, zInfo, xStride, yStride, indexStride,
        		strideAvailable);
        
        this.setTimeIndexMap(data, infoMap);
        
        // sets the origins
        this.setOrigins(colInfo, data);

        CreatedDataSet cdSet = new CreatedDataSet();
        cdSet.addData(data);

        return cdSet;
    }

    private CreatedDataSet createDimensionSXYMDArrayData(final SGIDataSource dataSource,
            final SGIProgressControl progress, final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap, final boolean multiple) {

        SGMDArrayFile file = (SGMDArrayFile) dataSource;
        final SGDataColumnInfo[] cols = colInfoSet.getDataColumnInfoArray();

        List<SGDataColumnInfo> xInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> yInfoList = new ArrayList<SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> lInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> uInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        Map<SGDataColumnInfo, SGDataColumnInfo> tInfoMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
        List<SGDataColumnInfo> timeInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> indexInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> pickUpInfoList = new ArrayList<SGDataColumnInfo>();
        if (!SGDataUtility.getSXYColumnType(cols, xInfoList,
        		yInfoList, lInfoMap, uInfoMap, tInfoMap, timeInfoList, indexInfoList, pickUpInfoList)) {
            return null;
        }
        if (lInfoMap.size() != uInfoMap.size()) {
            return null;
        }
        if (xInfoList.size() != 1 && yInfoList.size() != 1) {
            return null;
        }

        SGMDArrayDataColumnInfo xInfo = null;
        if (xInfoList.size() > 0) {
            xInfo = (SGMDArrayDataColumnInfo) xInfoList.get(0);
        }
        SGMDArrayDataColumnInfo yInfo = null;
        if (yInfoList.size() > 0) {
            yInfo = (SGMDArrayDataColumnInfo) yInfoList.get(0);
        }
        SGMDArrayDataColumnInfo leInfo = null;
        if (lInfoMap.size() > 0) {
            leInfo = (SGMDArrayDataColumnInfo) (new ArrayList<SGDataColumnInfo>(lInfoMap.values())).get(0);
        }
        SGMDArrayDataColumnInfo ueInfo = null;
        if (uInfoMap.size() > 0) {
            ueInfo = (SGMDArrayDataColumnInfo) (new ArrayList<SGDataColumnInfo>(uInfoMap.values())).get(0);
        }
        SGMDArrayDataColumnInfo tlInfo = null;
        if (tInfoMap.size() > 0) {
            tlInfo = (SGMDArrayDataColumnInfo) (new ArrayList<SGDataColumnInfo>(tInfoMap.values())).get(0);
        }

        // create an array of dimension indices
        SGIntegerSeriesSet indices = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
        if (indices == null) {
        	return null;
        }
        
        SGIntegerSeriesSet stride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
        SGIntegerSeriesSet tickLabelStride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
        final boolean strideAvailable = this.isStrideAvailable(infoMap);

        CreatedDataSet cdSet = new CreatedDataSet();

        if (multiple) {
            // create data object
        	SGSXYMDArrayMultipleData data = new SGSXYMDArrayMultipleData(
                    file, this.mDataSourceObserver, xInfo, yInfo, leInfo, ueInfo,
                    tlInfo, indices, stride, tickLabelStride, strideAvailable);

            // set the dimension index and origins
            this.setMDInfo(cols, file, data);

            this.setTimeIndexMap(data, infoMap);

            cdSet.addData(data);

        } else {

            @SuppressWarnings("unchecked")
			Map<String, Integer> pickUpDimensionIndexMap = (Map<String, Integer>) infoMap.get(
            		SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP);

            int[] dimIndices = indices.getNumbers();
            for (int ii = 0; ii < dimIndices.length; ii++) {
            	final int pickUpIndex = dimIndices[ii];
            	SGIntegerSeriesSet index = new SGIntegerSeriesSet();
            	index.add(pickUpIndex);

                // create data object
            	SGSXYMDArrayMultipleData data =
                    new SGSXYMDArrayMultipleData(
                            file, this.mDataSourceObserver, xInfo, yInfo, leInfo, ueInfo,
                            tlInfo, index, stride, tickLabelStride, strideAvailable);

                // set the dimension index and origins
                this.setMDInfo(cols, file, data);

                this.setTimeIndexMap(data, infoMap);
                
                Map<String, int[]> originMap = data.getOriginMap();
                
                Iterator<Entry<String, Integer>> pickUpDimensionIndexItr = pickUpDimensionIndexMap.entrySet().iterator();
                while (pickUpDimensionIndexItr.hasNext()) {
                	Entry<String, Integer> entry = pickUpDimensionIndexItr.next();
                	String name = entry.getKey();
                	Integer pIndex = entry.getValue();
                	if (pIndex != -1) {
                		Integer pickUpDim = pickUpDimensionIndexMap.get(name);
                        int[] origins = originMap.get(name).clone();
                        origins[pickUpDim] = pickUpIndex;
                		data.setOrigin(name, origins);
                	}
                }

                // the suffix for the data name
                StringBuffer sb = new StringBuffer();
                if (dimIndices.length > 1) {
                    sb.append('(');
                    sb.append(dimIndices[ii]);
                    sb.append(')');
                }
                String suffix = sb.toString();

                cdSet.addData(data, suffix);
            }
            cdSet.setMiddleString(" ");
        }

        return cdSet;
    }

    private CreatedDataSet createVXYMDArrayData(final SGIDataSource dataSource,
            final SGIProgressControl progress, final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap) {

    	SGMDArrayFile file = (SGMDArrayFile) dataSource;
		final SGDataColumnInfo[] colInfo = colInfoSet.getDataColumnInfoArray();

        // get polar flag
        if (infoMap.size() <= 1) {
            return null;
        }
        Object obj = infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED);
        if ((obj instanceof Boolean) == false) {
            return null;
        }
        Boolean b = (Boolean) obj;
        final boolean isPolar = b.booleanValue();

        // allocate memory
        progress.setProgressMessage(PROGRESS_MESSAGE_CREATEDATA);

        // get column indices
        final String type1 = SGDataUtility.getVXYFirstComponentColumnType(infoMap);
        final String type2 = SGDataUtility.getVXYSecondComponentColumnType(infoMap);
        SGMDArrayDataColumnInfo xInfo = null;
        SGMDArrayDataColumnInfo yInfo = null;
        SGMDArrayDataColumnInfo firstInfo = null;
        SGMDArrayDataColumnInfo secondInfo = null;
        for (int ii = 0; ii < colInfo.length; ii++) {
        	SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) colInfo[ii];
            String cType = col.getColumnType();
            if (SGDataUtility.isEqualColumnType(X_COORDINATE, cType)) {
                xInfo = col;
            } else if (SGDataUtility.isEqualColumnType(Y_COORDINATE, cType)) {
                yInfo = col;
            } else if (SGDataUtility.isEqualColumnType(type1, cType)) {
                firstInfo = col;
            } else if (SGDataUtility.isEqualColumnType(type2, cType)) {
                secondInfo = col;
            } else if ("".equals(cType)) {
                continue;
            } else {
                throw new Error("Illegal Column Type: " + cType);
            }
        }

        // get the stride
        SGIntegerSeriesSet xStride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X);
        SGIntegerSeriesSet yStride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y);
        SGIntegerSeriesSet indexStride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE);
        final boolean strideAvailable = this.isStrideAvailable(infoMap);

        // create data object
        SGVXYMDArrayData data = new SGVXYMDArrayData(file,
        		this.mDataSourceObserver, xInfo, yInfo, firstInfo, secondInfo, isPolar,
        		xStride, yStride, indexStride, strideAvailable);
        this.setTimeIndexMap(data, infoMap);
        
        // sets the origins
        this.setOrigins(colInfo, data);

        CreatedDataSet cdSet = new CreatedDataSet();
        cdSet.addData(data);
        return cdSet;
    }

    // sets the origins
    private void setOrigins(SGDataColumnInfo[] colInfo, SGMDArrayData data) {
        for (int ii = 0; ii < colInfo.length; ii++) {
        	SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfo[ii];
        	String name = mdInfo.getName();
        	SGMDArrayVariable var = data.findVariable(name);
        	var.setOrigins(mdInfo.getOrigins());
        }
    }
    
    /**
     * Returns the list of data sources.
     * 
     * @return the list of data sources
     */
    public List<SGIDataSource> getDataSourceList() {
    	return this.mDataSourceObserver.getDataSourceList();
    }

    /**
     * Finds the data sources of given file path.
     * 
     * @param path
     *           the file path
     * 
     * @return data source if it is found
     */
    public SGIDataSource findDataSource(final String path) {
    	if (path == null) {
    		throw new IllegalArgumentException("path == null");
    	}
    	SGIDataSource ret = null;
    	List<SGIDataSource> dataSrcList = this.mDataSourceObserver.getDataSourceList();
    	for (SGIDataSource src : dataSrcList) {
    		final String srcPath = src.getPath();
    		if (path.equals(srcPath)) {
    			ret = src;
    			break;
    		}
    	}
    	return ret;
    }

    SGNetCDFFile getNetcdfFile(String path) {
    	SGNetCDFFile ret = null;
        try {
			NetcdfFile ncFile = SGApplicationUtility.openNetCDF(path);
			ret = new SGNetCDFFile(ncFile);
		} catch (IOException e) {
		}
    	return ret;
    }

    SGHDF5File getHDF5File(String path) {
    	SGHDF5File ret = null;
        try {
        	IHDF5Reader reader = SGApplicationUtility.openHDF5(path);
        	ret = new SGHDF5File(reader);
		} catch (HDF5Exception e) {
		}
    	return ret;
    }

    SGMATLABFile getMATLABFile(String path) {
    	SGMATLABFile ret = null;
        try {
        	MatFileReader reader = SGApplicationUtility.openMAT(path);
        	ret = new SGMATLABFile(path, reader);
		} catch (IOException e) {
		}
    	return ret;
    }

}

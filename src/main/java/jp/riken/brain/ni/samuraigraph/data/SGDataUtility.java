package jp.riken.brain.ni.samuraigraph.data;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import jp.riken.brain.ni.samuraigraph.base.SGCSVTokenizer.Token;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory.ISingleDimension;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants.OPERATION;
import jp.riken.brain.ni.samuraigraph.base.SGIData;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGInteger;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.SXYDoubleDataValue;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.SXYSingleDataValue;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.SXYZDataValue;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.VXYDataValue;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.Value;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData.DoubleValueSetResult;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;

import org.w3c.dom.NamedNodeMap;

import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ch.systemsx.cisd.hdf5.HDF5DataClass;
import ch.systemsx.cisd.hdf5.HDF5DataTypeInformation;
import ch.systemsx.cisd.hdf5.HDF5EnumerationValue;
import ch.systemsx.cisd.hdf5.HDF5FactoryProvider;
import ch.systemsx.cisd.hdf5.IHDF5Reader;
import ch.systemsx.cisd.hdf5.IHDF5Writer;

/**
 * An utility class for data.
 *
 */
public class SGDataUtility implements SGIDataColumnTypeConstants, SGIDataPropertyKeyConstants,
		SGINetCDFConstants, SGIMDArrayConstants {

	private static final String[] ARRAY_EMPTY = { "" };

	private static final String[] ARRAY_NUMBER_SDARRAY_SXYZ = { "", X_VALUE, Y_VALUE,
			Z_VALUE };

	private static final String[] ARRAY_NUMBER_NETCDF_SXYZ_ALL = { "", X_VALUE,
			Y_VALUE };

	private static final String[] ARRAY_NUMBER_MDARRAY_SXYZ = { "", X_VALUE, Y_VALUE,
			Z_VALUE };

	private static final String[] ARRAY_NUMBER_SDARRAY_VXY_POLAR = { "", X_COORDINATE,
			Y_COORDINATE, MAGNITUDE, ANGLE };

	private static final String[] ARRAY_NUMBER_SDARRAY_VXY_ORTHOGONAL = { "",
			X_COORDINATE, Y_COORDINATE, X_COMPONENT, Y_COMPONENT };

	private static final String[] ARRAY_NUMBER_NETCDF_VXY_POLAR_ALL = { "",
			X_COORDINATE, Y_COORDINATE };

	private static final String[] ARRAY_NUMBER_NETCDF_VXY_ORTHOGONAL_ALL = { "",
			X_COORDINATE, Y_COORDINATE };

	private static final String[] ARRAY_NUMBER_MDARRAY_VXY_POLAR = { "", X_COORDINATE,
			Y_COORDINATE, MAGNITUDE, ANGLE };

	private static final String[] ARRAY_NUMBER_MDARRAY_VXY_ORTHOGONAL = { "",
			X_COORDINATE, Y_COORDINATE, X_COMPONENT, Y_COMPONENT };
	
    static final String MID_COLUMN_NO = " for No.";

    static final String MID_COLUMN = " for ";

    /**
     * The default number of multiple dimension.
     */
    private static final int DEFAULT_MULTIPLE_DIMENSION_NUM = 4;

    /**
     * Appends the column number to a text string of the column type.
     *
     * @param colType
     *              the column type
     * @param index
     *              array index of the column
     */
    public static final String appendColumnNo(final String colType, final int index) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(colType);
    	sb.append(MID_COLUMN_NO);
    	sb.append(index + 1);
    	return sb.toString();
    }

    /**
     * Appends the variable name to a text string of the column type on the netCDF data.
     *
     * @param colType
     * @param variableName
     * @return
     */
    public static final String appendColumnTitle(final String colType, final String variableName) {
        StringBuffer sb = new StringBuffer();
        sb.append(colType);
        sb.append(MID_COLUMN);
        sb.append(variableName);
        return sb.toString();
    }

    public static final String appendColumnNoOrTitle(
            final String colType, final int index, final boolean isRepeated, final String title) {
        if (isRepeated) {
            return appendColumnNo(colType, index);
        } else {
            return appendColumnTitle(colType, title);
        }
    }
    
    public static String removeHeaderTitle(final String colType) {
    	return removeHeaderSub(colType, true);
    }

    public static String removeHeaderNo(final String colType) {
    	return removeHeaderSub(colType, false);
    }

    private static String removeHeaderSub(final String colType, final boolean isTitle) {
    	String mid;
    	if (isTitle) {
    		mid = MID_COLUMN;
    	} else {
    		mid = MID_COLUMN_NO;
    	}
    	final int index = colType.indexOf(mid);
    	if (index == -1) {
    		return null;
    	}
    	return colType.substring(index + mid.length());
    }

    /**
     * Appends the column title to a text string of the column type on the netCDF data.
     * <p>
     * When using the netCDF data, column title exists and is not doubled.
     * @param colType
     * @param index
     * @param colInfo
     * @return
     */
    public static final String appendColumnTitle(
            final String colType, final int index, final SGDataColumnInfo[] colInfo) {
        StringBuffer sb = new StringBuffer();
        sb.append(colType);
        sb.append(MID_COLUMN);
        sb.append(colInfo[index].getTitle());
        return sb.toString();
    }

    public static final String appendColumnType(
            final String colType, final int index, final boolean isNetCDFOrMDArrayData,
            final SGDataColumnInfo[] colInfo, final boolean isRepeated) {
        if (isNetCDFOrMDArrayData) {
            return appendColumnTitle(colType, index, colInfo);
        } else {
            return appendColumnNoOrTitle(colType, index, isRepeated, colInfo[index].getTitle());
        }
    }

    /**
     * Extracts the column number from a given text string.
     *
     * @param str
     *            a text string
     * @return the column number if it exists and otherwise null
     */
    public static final Integer getAppendedColumnIndex(final String str) {
        final String forSeparator = "for";
        final String noSeparator = "No";
        int idx = str.toUpperCase().indexOf(forSeparator.toUpperCase());
        if (idx == -1) {
            return null;
        }
        idx = idx + forSeparator.length();
        String substr = str.substring(idx).trim().toUpperCase();

        if (!substr.startsWith(noSeparator.toUpperCase())) {
            return null;
        }
        substr = SGUtilityText.getCharString(substr);
        final String numStr = substr.substring(noSeparator.length());
        Integer index = SGUtilityText.getInteger(numStr);
        if (index == null) {
            return null;
        }
        return Integer.valueOf(index.intValue() - 1);
    }

    public static final Integer getAppendedColumnIndex(
            final String str, final SGDataColumnInfo[] colInfo) {
        Integer value = getColumnIndexOfAppendedColumnTitle(str, colInfo);
        if (null==value) {
            value = getAppendedColumnIndex(str);
        }
        return value;
    }

    public static final Integer getAppendedColumnIndex(
            final String str, final String[] columnTitles) {
        int idx = str.toUpperCase().indexOf(MID_COLUMN.trim().toUpperCase());
        if (idx == -1) {
            return null;
        }
        idx = idx + MID_COLUMN.trim().length();
        final String titleStr = str.substring(idx).trim();
        for (int i = 0; i < columnTitles.length; i++) {
            if (columnTitles[i]!=null) {
                if (titleStr.equals(columnTitles[i].trim())) {
                    return Integer.valueOf(i);
                }
            }
        }
        return getAppendedColumnIndex(str);
    }

    /**
     * Extracts the column number from a given text string.
     *
     * @param str
     * @param colInfo
     * @return the column number if it exists and otherwise null
     */
    public static final Integer getColumnIndexOfAppendedColumnTitle(
            final String str, final SGDataColumnInfo[] colInfo) {
        int idx = str.toUpperCase().indexOf(MID_COLUMN.trim().toUpperCase());
        if (idx == -1) {
            return null;
        }
        idx = idx + MID_COLUMN.trim().length();
        final String titleStr = str.substring(idx).trim();
        for (int i = 0; i < colInfo.length; i++) {
            if (colInfo[i] instanceof SGNetCDFDataColumnInfo
            		|| colInfo[i] instanceof SGMDArrayDataColumnInfo) {
                if (titleStr.equals(colInfo[i].getName())) {
                    return Integer.valueOf(i);
                }
            } else {
                if (titleStr.equals(colInfo[i].getTitle())) {
                    return Integer.valueOf(i);
                }
            }
        }
        return null;
    }

    public static final Integer getColumnIndexOfAppendedColumnTitle(
            final String str, SGNetCDFData data) {
        final String titleStr = getTitleString(str);
        if (titleStr == null) {
        	return null;
        }
        SGNetCDFFile file = data.getNetcdfFile();
        int index = file.getVariableIndex(titleStr);
        if (index == -1) {
            return null;
        }
        return Integer.valueOf(index);
    }

    public static final Integer getColumnIndexOfAppendedColumnTitle(
            final String str, SGMDArrayData data) {
        final String titleStr = getTitleString(str);
        if (titleStr == null) {
        	return null;
        }
        int index = data.getVariableIndex(titleStr);
        if (index == -1) {
            return null;
        }
        return Integer.valueOf(index);
    }

    private static String getTitleString(final String str) {
        int idx = str.toUpperCase().indexOf(MID_COLUMN.toUpperCase().trim());
        if (idx == -1) {
            return null;
        }
        idx = idx + MID_COLUMN.trim().length();
        final String titleStr = str.substring(idx).trim();
        return titleStr;
    }

    public static final Integer getColumnIndexOfAppendedColumnType(
            final String str, final String dataType, final SGDataColumnInfo[] colInfo) {
        if (isNetCDFData(dataType)) {
            return getColumnIndexOfAppendedColumnTitle(str, colInfo);
        } else {
            return getAppendedColumnIndex(str, colInfo);
        }
    }

    /**
     * Create items for the combo box.
     * <p>
     * Return the enabled candidates for column type of Scalar-XY data type.
     *
     * @param dataType
     *            the type of data
     * @param infoMap
     *            the map of data information
     * @param valueType
     *            the type of value
     * @return an array of items
     */
    private static String[] getSXYColumnTypeCandidates(
            final String dataType,
            final Map<String, Object> infoMap, final String valueType) {

        final boolean isNetCDFData = isNetCDFData(dataType);
        final boolean isMDData = isMDArrayData(dataType);
        final boolean isNetCDFOrMDData = (isNetCDFData || isMDData);

        Boolean multiple = null;
        if (isNetCDFOrMDData) {
            multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
            if (multiple == null) {
                return null;
            }
        }

        List<String> itemList = new ArrayList<String>();
        itemList.add("");
        if (!VALUE_TYPE_TEXT.equals(valueType)) {
            // other than text column
            itemList.add(X_VALUE);
            itemList.add(Y_VALUE);
        }

        // current row index
        Integer rowIndex = (Integer) infoMap.get(SGIDataInformationKeyConstants.KEY_CURRENT_ROW_INDEX);

        // all columns
        SGDataColumnInfo[] colInfo = (SGDataColumnInfo[]) infoMap.get(SGIDataInformationKeyConstants.KEY_COLUMN_INFO);
        boolean[] isRepeatedTitle = isEmptyOrRepeatedColumnTitle(colInfo);

        // get column indices of x and y values
        List<Integer> xIndexList = new ArrayList<Integer>();
        List<Integer> yIndexList = new ArrayList<Integer>();
        for (int ii = 0; ii < colInfo.length; ii++) {
            String colType = colInfo[ii].getColumnType();
            final Integer index = Integer.valueOf(ii);
            if (X_VALUE.equals(colType)) {
                xIndexList.add(index);
            } else if (Y_VALUE.equals(colType)) {
                yIndexList.add(index);
            }
        }

        // counts the number of number type rows
        int numberRowNum = 0;
        for (int ii = 0; ii < colInfo.length; ii++) {
            String vType = colInfo[ii].getValueType();
            if (VALUE_TYPE_NUMBER.equals(vType)) {
                numberRowNum++;
            } else if (VALUE_TYPE_DATE.equals(vType)) {
                if (X_VALUE.equals(colInfo[ii].getColumnType()) || Y_VALUE.equals(colInfo[ii].getColumnType())) {
                    numberRowNum++;
                }
            }
        }

        // add time, pickup and serial number columns
//        if (isNetCDFData && VALUE_TYPE_NUMBER.equals(valueType)) {
//        	SGNetCDFDataColumnInfo ncInfo = (SGNetCDFDataColumnInfo) colInfo[rowIndex];
//        	if (ncInfo.isCoordinateVariable()) {
//                itemList.add(ANIMATION_FRAME);
//                itemList.add(PICKUP);
//                itemList.add(INDEX);
//        	}
//        }
		updateNetCDFItems(itemList, infoMap, dataType, valueType, 
				new String[] { ANIMATION_FRAME, PICKUP, INDEX }, new String[] {});

		// a flag whether to show options for error bars and tick labels
		boolean errorBarTickLabelFlag = true;
		if (isNetCDFData) {
			// skips NetCDF coordinate variables
			SGNetCDFDataColumnInfo ncInfo = (SGNetCDFDataColumnInfo) colInfo[rowIndex];
			if (ncInfo.isCoordinateVariable()) {
				errorBarTickLabelFlag = false;
			}
		}
		if (errorBarTickLabelFlag) {
	        // get the list of column index those are assignable
	        // for the error bar or tick labels
	        List<Integer> indexList = getOptionalColumnsAssignableIndexList(
	                dataType, colInfo, xIndexList, yIndexList);
	        if (indexList != null) {
	            // remove the current row if it exists
	            indexList.remove(rowIndex);

	            // remove picked up column for multidimensional data
	            if (SGDataUtility.isMDArrayData(dataType)) {
	            	List<Integer> pickUpIndexList = new ArrayList<Integer>();
	                for (int ii = 0; ii < colInfo.length; ii++) {
	                	SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfo[ii];
	                	Integer pickUpIndex = mdInfo.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
	                	if (pickUpIndex != null) {
	                		pickUpIndexList.add(ii);
	                	}
	                }
	                if (pickUpIndexList.size() > 0) {
	                    for (int ii = indexList.size() - 1; ii >= 0; ii--) {
	                    	Integer index = indexList.get(ii);
	                    	if (!pickUpIndexList.contains(index)) {
	                    		indexList.remove(index);
	                    	}
	                    }
	                }
	            }

	            if (VALUE_TYPE_NUMBER.equals(valueType)) {
	                // only for the number type column

	                // add items of error bars to the list
	                for (int ii = 0; ii < indexList.size(); ii++) {
	                    Integer index = indexList.get(ii);
	                    String vType = colInfo[index.intValue()].getValueType();
	                    if (VALUE_TYPE_SAMPLING_RATE.equals(vType)) {
	                        continue;
	                    }
	                    final int num = index.intValue();
	                    final int min1 = isMDData ? 3 : 4;
	                    final int min2 = min1 - 1;
	                    if (numberRowNum >= min1) {
	                        itemList.add(appendColumnType(LOWER_ERROR_VALUE, num, isNetCDFOrMDData, 
	                        		colInfo, isRepeatedTitle[num]));
	                        itemList.add(appendColumnType(UPPER_ERROR_VALUE, num, isNetCDFOrMDData, 
	                        		colInfo, isRepeatedTitle[num]));
	                        itemList.add(appendColumnType(LOWER_UPPER_ERROR_VALUE, num, isNetCDFOrMDData, 
	                        		colInfo, isRepeatedTitle[num]));
	                    } else if (numberRowNum >= min2) {
	                        itemList.add(appendColumnType(LOWER_UPPER_ERROR_VALUE, num, isNetCDFOrMDData, 
	                        		colInfo, isRepeatedTitle[num]));
	                    }
	                }
	            }

                // add items of tick labels to the list
	            if (!VALUE_TYPE_SAMPLING_RATE.equals(valueType)) {
	                // add items of tick labels to the list
	                for (int ii = 0; ii < indexList.size(); ii++) {
	                    Integer index = indexList.get(ii);
	                    final int num = index.intValue();
	                    itemList.add(appendColumnType(TICK_LABEL, num, isNetCDFOrMDData, 
	                    		colInfo, isRepeatedTitle[num]));
	                }
	            }
	        }
		}

        return itemList.toArray(new String[itemList.size()]);
    }

    /**
     * Create items for the combo box.
     * <p>
     * Return the enabled candidates for column type.
     *
     * @param dataType
     *            the type of data
     * @param infoMap
     *            the map of data information
     * @param valueType
     *            the type of value
     * @return an array of items
     */
    public static String[] getColumnTypeCandidates(final String dataType,
            final Map<String, Object> infoMap, final String valueType) {

        String[] items = null;
        if (isSXYTypeData(dataType)) {
            // SXY
        	if (VALUE_TYPE_BYTE_DATA.equals(valueType)) {
        		items = ARRAY_EMPTY;
        	} else {
                items = getSXYColumnTypeCandidates(dataType, infoMap, valueType);
        	}
        } else if (SGDataUtility.isSXYZTypeData(dataType)) {
            // SXYZ
            if (VALUE_TYPE_NUMBER.equals(valueType)) {
            	if (SGDataUtility.isSDArrayData(dataType)) {
                    items = ARRAY_NUMBER_SDARRAY_SXYZ;
            	} else if (SGDataUtility.isNetCDFData(dataType)) {
            		items = updateNetCDFItems(ARRAY_NUMBER_NETCDF_SXYZ_ALL, infoMap,
            				dataType, valueType, new String[] { ANIMATION_FRAME, INDEX },
            				new String[] { Z_VALUE });
            	} else if (SGDataUtility.isMDArrayData(dataType)) {
            		items = ARRAY_NUMBER_MDARRAY_SXYZ;
            	} else {
                    throw new IllegalArgumentException("Invalid data type: " + dataType);
            	}
            } else if (VALUE_TYPE_TEXT.equals(valueType)) {
                items = ARRAY_EMPTY;
            } else if (VALUE_TYPE_DATE.equals(valueType)) {
                items = ARRAY_EMPTY;
            } else if (VALUE_TYPE_BYTE_DATA.equals(valueType)) {
                items = ARRAY_EMPTY;
            } else {
                throw new IllegalArgumentException("Invalid value type: " + valueType);
            }
        } else if (SGDataUtility.isVXYTypeData(dataType)) {
            // VXY
            if (VALUE_TYPE_NUMBER.equals(valueType)) {
                if (isPolar(infoMap)) {
                	if (SGDataUtility.isSDArrayData(dataType)) {
                        items = ARRAY_NUMBER_SDARRAY_VXY_POLAR;
                	} else if (SGDataUtility.isNetCDFData(dataType)) {
                		items = updateNetCDFItems(ARRAY_NUMBER_NETCDF_VXY_POLAR_ALL, infoMap,
                				dataType, valueType, new String[] { ANIMATION_FRAME, INDEX },
                				new String[] { MAGNITUDE, ANGLE });
                	} else if (SGDataUtility.isMDArrayData(dataType)) {
                        items = ARRAY_NUMBER_MDARRAY_VXY_POLAR;
                	} else {
                        throw new IllegalArgumentException("Invalid data type: " + dataType);
                	}
                } else {
                	if (SGDataUtility.isSDArrayData(dataType)) {
                        items = ARRAY_NUMBER_SDARRAY_VXY_ORTHOGONAL;
                	} else if (SGDataUtility.isNetCDFData(dataType)) {
                		items = updateNetCDFItems(ARRAY_NUMBER_NETCDF_VXY_ORTHOGONAL_ALL, infoMap,
                				dataType, valueType, new String[] { ANIMATION_FRAME, INDEX },
                				new String[] { X_COMPONENT, Y_COMPONENT });
                	} else if (SGDataUtility.isMDArrayData(dataType)) {
                        items = ARRAY_NUMBER_MDARRAY_VXY_ORTHOGONAL;
                	} else {
                        throw new IllegalArgumentException("Invalid data type: " + dataType);
                	}
                }
            } else if (VALUE_TYPE_TEXT.equals(valueType)) {
                items = ARRAY_EMPTY;
            } else if (VALUE_TYPE_DATE.equals(valueType)) {
                items = ARRAY_EMPTY;
            } else if (VALUE_TYPE_BYTE_DATA.equals(valueType)) {
                items = ARRAY_EMPTY;
            } else {
                throw new IllegalArgumentException("Invalid value type: " + valueType);
            }
        } else {
            throw new IllegalArgumentException("Invalid data type: " + dataType);
        }

        return (String[]) items.clone();
    }

    /**
     * Checks whether given data columns are valid for given data type.
     * This method checks only the number of selected columns.
     *
     * @param dataType
     *            data type
     * @param columnInfoArray
     *            an array of columns information
     * @param infoMap
     *            information map for data
     */
    public static boolean checkDataColumns(final String dataType,
            final SGDataColumnInfo[] columnInfoArray,
            final Map<String, Object> infoMap) {

        final int len = columnInfoArray.length;
        String[] columnTypes = new String[len];
        for (int ii = 0; ii < len; ii++) {
        	columnTypes[ii] = columnInfoArray[ii].getColumnType();
        }

        // other than multiple and sampling data, check the duplication
        if (!isSXYTypeData(dataType)) {
            for (int ii = 0; ii < len - 1; ii++) {
                if (columnTypes[ii] == null || "".equals(columnTypes[ii])) {
                    continue;
                }
                for (int jj = ii + 1; jj < len; jj++) {
                    if (columnTypes[ii].equals(columnTypes[jj])) {
                        return false;
                    }
                }
            }
        }

        if (isSXYTypeData(dataType)) {

        	// one array for x values and multiple arrays for y values
        	// or one array for y values and multiple arrays for x values
        	// are permitted
            final int xCnt = count(columnTypes, X_VALUE);
            final int yCnt = count(columnTypes, Y_VALUE);
            if (xCnt == 0 && yCnt == 0) {
            	return false;
            }

			final boolean byMultiple;
			final boolean bxMultiple;
			final boolean bothSingle;
			if (isMDArrayData(dataType)) {
				byMultiple = ((xCnt == 0 || xCnt == 1) && yCnt > 1);
				bxMultiple = (xCnt > 1 && (yCnt == 0 || yCnt == 1));
				bothSingle = ((xCnt == 0 || xCnt == 1) && (yCnt == 0 || yCnt == 1));
			} else {
				byMultiple = (xCnt == 1 && yCnt > 1);
				bxMultiple = (xCnt > 1 && yCnt == 1);
				bothSingle = (xCnt == 1 && yCnt == 1);
				if (!byMultiple && !bxMultiple && !bothSingle) {
					return false;
				}
			}

			String multipleColumnType = null;
			String singleColumnType = null;
			if (byMultiple) {
				multipleColumnType = Y_VALUE;
				singleColumnType = X_VALUE;
			} else if (bxMultiple) {
				multipleColumnType = X_VALUE;
				singleColumnType = Y_VALUE;
			}

            if (!bothSingle) {
    			Set<String> multipleValueTypeSet = new HashSet<String>();
    			if (multipleColumnType != null) {
    				for (int ii = 0; ii < len; ii++) {
    					if (multipleColumnType.equals(columnTypes[ii])) {
    						multipleValueTypeSet.add(columnInfoArray[ii].getValueType());
    					}
    				}
    			}

                // checks whether sampling rate is used for multiple values
    			if (multipleValueTypeSet.contains(VALUE_TYPE_SAMPLING_RATE)) {
    				return false;
    			}

    			// checks whether the data column exists in multiple values
    			if (multipleValueTypeSet.contains(VALUE_TYPE_DATE)) {
    				return false;
    			}
            }

//            boolean checkAppendedIndex = false;
//            if (isArrayData(dataType)){
//            	checkAppendedIndex = true;
//            } else if (isNetCDFData(dataType) || isMDData(dataType)) {
//                Boolean variable = (Boolean) infoMap.get(KEY_MULTIPLE_VARIABLE);
//                if (variable == null) {
//                    return false;
//                }
//                checkAppendedIndex = variable.booleanValue();
//            } else {
//            	return false;
//            }

            boolean checkAppendedIndex = true;

            if (checkAppendedIndex) {
            	// checks column indices appended to the error bars and tick labels

                List<Integer> lList = new ArrayList<Integer>();
                List<Integer> uList = new ArrayList<Integer>();
                List<Integer> luList = new ArrayList<Integer>();
                for (int ii = 0; ii < columnTypes.length; ii++) {
                    String colType = columnTypes[ii];
                    if (colType.startsWith(LOWER_ERROR_VALUE)) {
                        Integer num = getColumnIndexOfAppendedColumnType(colType, dataType, columnInfoArray);
                        if (num == null) {
                            return false;
                        }
                        lList.add(num);
                    } else if (colType.startsWith(UPPER_ERROR_VALUE)) {
                        Integer num = getColumnIndexOfAppendedColumnType(colType, dataType, columnInfoArray);
                        if (num == null) {
                            return false;
                        }
                        uList.add(num);
                    } else if (colType.startsWith(LOWER_UPPER_ERROR_VALUE)) {
                        Integer num = getColumnIndexOfAppendedColumnType(colType, dataType, columnInfoArray);
                        if (num == null) {
                            return false;
                        }
                        luList.add(num);
                    }
                }

                List<Integer> tList = new ArrayList<Integer>();
                for (int ii = 0; ii < columnTypes.length; ii++) {
                    String colType = columnTypes[ii];
                    if (colType.startsWith(TICK_LABEL)) {
                        Integer num = getColumnIndexOfAppendedColumnType(colType, dataType, columnInfoArray);
                        if (num == null) {
                            return false;
                        }
                        tList.add(num);
                    }
                }

                if (!bothSingle) {
                    // checks whether error bars and tick labels are assigned to
                    // not multiple values
                    Integer singleIndex = null;
                    for (int ii = 0; ii < columnTypes.length; ii++) {
                        if (columnTypes[ii].equals(singleColumnType)) {
                            singleIndex = Integer.valueOf(ii);
                            break;
                    	}
                    }
                    if (!isMDArrayData(dataType)) {
                    	// check for array data and NetCDF data
                        if (singleIndex == null) {
                        	return false;
                        }
                    }
                    if (lList.contains(singleIndex)) {
                    	return false;
                    }
                    if (uList.contains(singleIndex)) {
                    	return false;
                    }
                    if (luList.contains(singleIndex)) {
                    	return false;
                    }
                    if (tList.contains(singleIndex)) {
                    	return false;
                    }
                }

                // checks whether error bars and tick labels for x values
    			// and those for y values exist at the same time
    			Set<Integer> allSet = new TreeSet<Integer>();
    			allSet.addAll(lList);
    			allSet.addAll(uList);
    			allSet.addAll(luList);
    			allSet.addAll(tList);
    			List<Integer> xList = new ArrayList<Integer>();
    			List<Integer> yList = new ArrayList<Integer>();
    			Iterator<Integer> itr = allSet.iterator();
    			while (itr.hasNext()) {
    				Integer index = itr.next();
    				String colType = columnTypes[index.intValue()];
            		if (X_VALUE.equals(colType)) {
            			xList.add(index);
            		} else if (Y_VALUE.equals(colType)) {
            			yList.add(index);
            		}
    			}
    			if (xList.size() != 0 && yList.size() != 0) {
    				return false;
    			}

                // check duplication
                if (isDuplicated(lList)) {
                	return false;
                }
                if (isDuplicated(uList)) {
                	return false;
                }
                if (isDuplicated(luList)) {
                	return false;
                }
                if (isDuplicated(tList)) {
                	return false;
                }

                // check consistency
                if (!lList.equals(uList)) {
                	return false;
                }
                for (int ii = 0; ii < luList.size(); ii++) {
                	Object obj = luList.get(ii);
                	if (lList.contains(obj)) {
                		return false;
                	}
                	if (uList.contains(obj)) {
                		return false;
                	}
                }
            }

        } else if (isSXYZTypeData(dataType)) {
            // duplication was already checked

            // only one x values must be selected
            final int xCnt = count(columnTypes, X_VALUE);
        	if (isMDArrayData(dataType)) {
                if (xCnt > 1) {
                    return false;
                }
        	} else {
                if (xCnt != 1) {
                    return false;
                }
        	}

            // only one y values must be selected
            final int yCnt = count(columnTypes, Y_VALUE);
        	if (isMDArrayData(dataType)) {
                if (yCnt > 1) {
                    return false;
                }
        	} else {
                if (yCnt != 1) {
                    return false;
                }
        	}

            // only one z values must be selected
            final int zCnt = count(columnTypes, Z_VALUE);
            if (zCnt != 1) {
                return false;
            }

            // x and y index
            if (isNetCDFData(dataType)) {
                final int indexCnt = count(columnTypes, INDEX);
                final int xIndexCnt = count(columnTypes, X_INDEX);
                final int yIndexCnt = count(columnTypes, Y_INDEX);
            	if (indexCnt == 1) {
            		if (xIndexCnt == 1 || yIndexCnt == 1) {
            			return false;
            		}
            	}
            }

        } else if (isVXYTypeData(dataType)) {
            // duplication was already checked

            final int xCnt = count(columnTypes, X_COORDINATE);
            final int yCnt = count(columnTypes, Y_COORDINATE);
            if (xCnt > 1) {
                return false;
            }
            if (yCnt > 1) {
                return false;
            }

            // both of magnitude and angle must be selected in polar mode,
            // and both of x component and y component must be selected in the
            // other mode
        	final String first = SGDataUtility.getVXYFirstComponentColumnType(infoMap);
        	final String second = SGDataUtility.getVXYSecondComponentColumnType(infoMap);
            final int bCnt1 = count(columnTypes, first);
            final int bCnt2 = count(columnTypes, second);
            if (bCnt1 != 1) {
                return false;
            }
            if (bCnt2 != 1) {
                return false;
            }
        }

        if (isNetCDFData(dataType)) {
            // animation
            final int tCnt = count(columnTypes, ANIMATION_FRAME);
            if (tCnt > 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Count the number of equal objects in a given array.
     *
     * @param items
     *            an array
     * @param value
     *            a value
     * @return the number of items in a given array
     */
    private static int count(Object[] items, Object value) {
    	if (value == null) {
    		throw new IllegalArgumentException("value == null");
    	}
        int cnt = 0;
        for (int ii = 0; ii < items.length; ii++) {
        	if (items[ii] == null) {
        		continue;
        	}
            if (SGDataUtility.isEqualColumnType(items[ii].toString(), value.toString())) {
                cnt++;
            }
        }
        return cnt;
    }

    private static boolean isDuplicated(List<?> items) {
    	for (int ii = 0; ii < items.size() - 1; ii++) {
    		for (int jj = ii + 1; jj < items.size(); jj++) {
    			if (SGUtility.equals(items.get(ii), items.get(jj))) {
    				return true;
    			}
    		}
    	}
    	return false;
    }

    /**
     * Returns whether the polar mode is selected.
     *
     * @param infoMap
     *            information map for data
     * @return true if polar mode is selected
     * @throws Error throw if infoMap not have KEY_POLAR_SELECTED key
     */
    public static boolean isPolar(final Map<String, Object> infoMap) {
        Object value = infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED);
        if (value == null) {
            throw new Error("Mode for vector data is not selected.");
        }
        Boolean b = (Boolean) value;
        return b.booleanValue();
    }

    static boolean[] isEmptyOrRepeatedColumnTitle(SGDataColumnInfo[] columnInfo) {
        int len = columnInfo.length;
        boolean[] result = new boolean[len];
        Arrays.fill(result, false);

        for (int i = 0; i < len; i++) {
            SGDataColumnInfo cInfo = columnInfo[i];
            String title = cInfo.getTitle();
            if (title==null || title.trim().equals("")) {
                result[i] = true;
            }
            if (result[i]==false) {
                for (int j = i+1; j < len; j++) {
                    if (title.equalsIgnoreCase(columnInfo[j].getTitle())) {
                        result[i] = true;
                        result[j] = true;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Determine the data-type from the first line.
     *
     * @param tokenList
     *            the list of tokens of the first line
     * @param indexList
     *            the list of indices whether each token is of the number type or the text type
     * @param cList
     *            the list of candidates of data types
     * @return true if succeeded
     */
    public static boolean getDataTypeCandidateList(final List<Token> tokenList,
            final List<Integer> indexList, final List<String> cList) {

    	// count the number of number type columns
        int cntNumber = 0;
        for (int ii = 0; ii < indexList.size(); ii++) {
            Integer obj = (Integer) indexList.get(ii);
            int num = obj.intValue();
            if (num == 1) {
                cntNumber++;
            }
        }

        String[] dataTypes = { SGDataTypeConstants.SXY_DATA,
				SGDataTypeConstants.SXY_MULTIPLE_DATA,
				SGDataTypeConstants.SXY_SAMPLING_DATA,
				SGDataTypeConstants.VXY_DATA, SGDataTypeConstants.SXYZ_DATA };
        for (int ii = 0; ii < dataTypes.length; ii++) {
        	final int num = getMinimumNumberColumns(dataTypes[ii]);
        	if (cntNumber >= num) {
        		cList.add(dataTypes[ii]);
        	}
        }
        if (cntNumber >= getMinimumNumberColumns(SGDataTypeConstants.SXY_DATE_DATA)) {
            for (int ii = 0; ii < tokenList.size(); ii++) {
                Token token = (Token) tokenList.get(ii);
                if (SGUtilityText.getDate(token.getString()) != null) {
                	cList.add(SGDataTypeConstants.SXY_DATE_DATA);
                    break;
                }
            }
        }

        return true;
    }

    /**
     * Returns the minimum number of number columns for given data type.
     *
     * @param dataType
     *           the data type
     * @return the minimum number of number type columns
     */
    public static int getMinimumNumberColumns(final String dataType) {
    	int num = -1;
    	if (SGDataTypeConstants.SXY_DATA.equals(dataType)) {
    		num = 1;
    	} else if (SGDataTypeConstants.SXY_MULTIPLE_DATA.equals(dataType)) {
    		num = 1;
    	} else if (SGDataTypeConstants.SXY_SAMPLING_DATA.equals(dataType)) {
    		num = 1;
    	} else if (SGDataTypeConstants.VXY_DATA.equals(dataType)) {
    		num = 4;
    	} else if (SGDataTypeConstants.SXYZ_DATA.equals(dataType)) {
    		num = 3;
    	}
    	return num;
    }

    // returns a list of number convertible column index
    public static List<Integer> getColumnIndexListOfNumber(final List<Token> tokenlist) {
        List<Integer> list = new ArrayList<Integer>();
        for (int ii = 0; ii < tokenlist.size(); ii++) {
            Token token = (Token) tokenlist.get(ii);
            final String str = token.getString();
            if (token.isDoubleQuoted()) {
                list.add(Integer.valueOf(0));
            } else {
                Double d = SGUtilityText.getDouble(str);
                if (d!= null) {
                    list.add(Integer.valueOf(1));
                } else {
                    list.add(Integer.valueOf(0));
                }
            }
        }
        return list;
    }

    private static List<String> getColumnNameList(final SGDataColumnInfo[] cols,
            final String colType) {
        List<String> list = new ArrayList<String>();
        for (int ii = 0; ii < cols.length; ii++) {
        	String type = cols[ii].getColumnType();
            if (type.equalsIgnoreCase(colType)) {
                list.add(cols[ii].getName());
            }
        }
        return list;
    }

    private static List<SGDataColumnInfo> getColumnList(final SGDataColumnInfo[] cols,
            final String colType) {
        List<SGDataColumnInfo> list = new ArrayList<SGDataColumnInfo>();
        for (int ii = 0; ii < cols.length; ii++) {
        	String type = cols[ii].getColumnType();
            if (type.equalsIgnoreCase(colType)) {
                list.add(cols[ii]);
            }
        }
        return list;
    }

    private static List<SGDataColumnInfo> getColumnListStartsWith(final SGDataColumnInfo[] cols,
            final String prefix) {
        List<SGDataColumnInfo> list = new ArrayList<SGDataColumnInfo>();
        for (int ii = 0; ii < cols.length; ii++) {
        	String type = cols[ii].getColumnType();
            if (type.toUpperCase().startsWith(prefix.toUpperCase())) {
                list.add(cols[ii]);
            }
        }
        return list;
    }

    private static boolean getColumnNameAndAppendedNumberList(
            final SGDataColumnInfo[] cols,
            final String colType, List<String> nameList, List<Integer> indexList) {
        for (int ii = 0; ii < cols.length; ii++) {
            String type = cols[ii].getColumnType();
            if (type.toUpperCase().startsWith(colType.toUpperCase())) {
            	String name = cols[ii].getName();
            	Integer index = getColumnIndexOfAppendedColumnTitle(type, cols);
            	if (index == null) {
            		return false;
            	}
            	nameList.add(name);
            	indexList.add(index);
            }
        }
        return true;
    }

    /**
     * Checks the data columns for netCDF data.
     *
     * @param cols
     *           the data columns
     * @param dataType
     *           the data type
     * @param ncfile
     *           the netCDF data
     * @param infoMap
     *           the information map
     * @return true if columns are valid
     */
    public static boolean checkNetCDFDataColumns(final SGDataColumnInfo[] cols,
            final String dataType, final SGNetCDFFile ncfile,
            final Map<String, Object> infoMap) {

        // animation
        final List<String> timeNameList = getColumnNameList(cols, ANIMATION_FRAME);
        if (timeNameList.size() > 1) {
            return false;
        }
        SGNetCDFVariable timeVar = null;
        Dimension timeDim = null;
        if (timeNameList.size() == 1) {
            String aName = timeNameList.get(0);
            timeVar = ncfile.findVariable(aName);
            if (timeVar == null) {
                return false;
            }
            if (!timeVar.isCoordinateVariable()) {
                return false;
            }
            timeDim = timeVar.getDimension(0);
        }

        // index
        final List<String> indexNameList = getColumnNameList(cols, INDEX);
        if (indexNameList.size() > 1) {
            return false;
        }
        SGNetCDFVariable indexVar = null;
        Dimension indexDim = null;
        if (indexNameList.size() == 1) {
            String sName = indexNameList.get(0);
            indexVar = ncfile.findVariable(sName);
            if (indexVar == null) {
                return false;
            }
            if (!indexVar.isCoordinateVariable()) {
                return false;
            }
            indexDim = indexVar.getDimension(0);
        }

        if (isSXYTypeData(dataType)) {
            Boolean multipleVariable = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE);
        	if (multipleVariable == null) {
        		return false;
        	}

            // x and y values
            final List<String> xNameList = getColumnNameList(cols, X_VALUE);
            final List<String> yNameList = getColumnNameList(cols, Y_VALUE);
            if (xNameList.size() == 0 || yNameList.size() == 0) {
                return false;
            }
            if (multipleVariable.booleanValue()) {
                if (xNameList.size() != 1 && yNameList.size() != 1) {
                    return false;
                }
            } else {
                if (xNameList.size() != 1 || yNameList.size() != 1) {
                    return false;
                }
            }
            final String xName0 = xNameList.get(0);
            final String yName0 = yNameList.get(0);
            final SGNetCDFVariable xVar0 = ncfile.findVariable(xName0);
            final SGNetCDFVariable yVar0 = ncfile.findVariable(yName0);
            final SGNetCDFVariable cVar;
            final List<String> oNameList;
        	if (indexVar != null) {
                cVar = indexVar;
                oNameList = new ArrayList<String>();
                oNameList.addAll(xNameList);
                oNameList.addAll(yNameList);
        	} else {
                if (xNameList.size() == 1 && yNameList.size() == 1) {
                    if (xVar0.isCoordinateVariable()) {
                    	cVar = xVar0;
                    	oNameList = yNameList;
                    } else if (yVar0.isCoordinateVariable()) {
                    	cVar = yVar0;
                    	oNameList = xNameList;
                    } else {
                    	return false;
                    }
                } else if (xNameList.size() == 1) {
                    if (!xVar0.isCoordinateVariable()) {
                    	return false;
                    }
                    cVar = xVar0;
                	oNameList = yNameList;
                } else {
                    if (!yVar0.isCoordinateVariable()) {
                    	return false;
                    }
                	cVar = yVar0;
                	oNameList = xNameList;
                }
        	}

            List<SGNetCDFVariable> oVarList = new ArrayList<SGNetCDFVariable>();
            for (int ii = 0; ii < oNameList.size(); ii++) {
            	String oName = oNameList.get(ii);
                SGNetCDFVariable oVar = ncfile.findVariable(oName);
                if (oVar == null) {
                    return false;
                }
                if (oVar.isCoordinateVariable()) {
                	return false;
                }
                oVarList.add(oVar);
            }

            // variable must have a dimension equal to the coordinate variable
            Dimension cDim = cVar.getDimension(0);
            for (int ii = 0; ii < oVarList.size(); ii++) {
            	SGNetCDFVariable oVar = oVarList.get(ii);
               	List<Dimension> oDims = oVar.getDimensions();
                if (!oDims.contains(cDim)) {
                    return false;
                }
            }

            // lower and upper error values
            if (!multipleVariable) {
            	// multiple dimension

                final List<String> lNameList = getColumnNameList(cols, LOWER_ERROR_VALUE);
                final List<String> uNameList = getColumnNameList(cols, UPPER_ERROR_VALUE);
                if (!(lNameList.size() == 0 && uNameList.size() == 0)
                        && !(lNameList.size() == 1 && uNameList.size() == 1)) {
                    return false;
                }

                // if lower and upper errors are selected, lower-upper must not
                // be selected
                final List<String> luNameList = getColumnNameList(cols, LOWER_UPPER_ERROR_VALUE);
                if (lNameList.size() == 1 && uNameList.size() == 1) {
                    if (luNameList.size() != 0) {
                        return false;
                    }
                }

                if (lNameList.size() != 0) {
                    String lName = lNameList.get(0);
                    String uName = uNameList.get(0);
                    SGNetCDFVariable lVar = ncfile.findVariable(lName);
                    if (lVar == null) {
                        return false;
                    }
                    SGNetCDFVariable uVar = ncfile.findVariable(uName);
                    if (uVar == null) {
                        return false;
                    }

                    // check validity of selected variables
                    final boolean bl = lVar.isCoordinateVariable();
                    if (bl) {
                        return false;
                    }
                    final boolean bu = uVar.isCoordinateVariable();
                    if (bu) {
                        return false;
                    }

                    // variables must have a dimension equal to the coordinate variable
                    List<Dimension> lDims = lVar.getDimensions();
                    if (!lDims.contains(cDim)) {
                        return false;
                    }
                    List<Dimension> uDims = uVar.getDimensions();
                    if (!uDims.contains(cDim)) {
                        return false;
                    }

                } else if (luNameList.size() != 0) {

                    String luName = luNameList.get(0);
                    SGNetCDFVariable luVar = ncfile.findVariable(luName);
                    if (luVar == null) {
                        return false;
                    }

                    // check validity of selected variables
                    final boolean blu = luVar.isCoordinateVariable();
                    if (blu) {
                        return false;
                    }

                    // variables must have a dimension equal to the coordinate variable
                    List<Dimension> luDims = luVar.getDimensions();
                    if (!luDims.contains(cDim)) {
                        return false;
                    }
                }

            } else {
            	// single variable

                final List<String> lNameList = new ArrayList<String>();
                final List<Integer> lIndexList = new ArrayList<Integer>();
                final List<String> uNameList = new ArrayList<String>();
                final List<Integer> uIndexList = new ArrayList<Integer>();
                if (getColumnNameAndAppendedNumberList(cols, LOWER_ERROR_VALUE, lNameList,
                		lIndexList) == false) {
                	return false;
                }
                if (getColumnNameAndAppendedNumberList(cols, UPPER_ERROR_VALUE, uNameList,
                		uIndexList) == false) {
                	return false;
                }
                if (lNameList.size() != uNameList.size()) {
                    return false;
                }
                for (int ii = 0; ii < lNameList.size(); ii++) {
                    String lName = lNameList.get(ii);
                    String uName = uNameList.get(ii);
                    SGNetCDFVariable lVar = ncfile.findVariable(lName);
                    if (lVar == null) {
                        return false;
                    }
                    SGNetCDFVariable uVar = ncfile.findVariable(uName);
                    if (uVar == null) {
                        return false;
                    }

                    // check validity of selected variables
                    final boolean bl = lVar.isCoordinateVariable();
                    if (bl) {
                        return false;
                    }
                    final boolean bu = uVar.isCoordinateVariable();
                    if (bu) {
                        return false;
                    }

                    // variables must have a dimension equal to the coordinate variable
                    List<Dimension> lDims = lVar.getDimensions();
                    if (!lDims.contains(cDim)) {
                        return false;
                    }
                    List<Dimension> uDims = uVar.getDimensions();
                    if (!uDims.contains(cDim)) {
                        return false;
                    }
                }

                final List<String> luNameList = new ArrayList<String>();
                final List<Integer> luIndexList = new ArrayList<Integer>();
                if (getColumnNameAndAppendedNumberList(cols, LOWER_UPPER_ERROR_VALUE,
                		luNameList, luIndexList) == false) {
                	return false;
                }
                for (int ii = 0; ii < luNameList.size(); ii++) {

                    String luName = luNameList.get(ii);
                    SGNetCDFVariable luVar = ncfile.findVariable(luName);
                    if (luVar == null) {
                        return false;
                    }

                    // check validity of selected variables
                    final boolean blu = luVar.isCoordinateVariable();
                    if (blu) {
                        return false;
                    }

                    // variables must have a dimension equal to the coordinate variable
                    List<Dimension> luDims = luVar.getDimensions();
                    if (!luDims.contains(cDim)) {
                        return false;
                    }

                	// checks whether lower/upper indices is contained
                	// in lower indices or upper indices
                	Integer luIndex = luIndexList.get(ii);
                	if (lIndexList.contains(luIndex)) {
                		return false;
                	}
                	if (uIndexList.contains(luIndex)) {
                		return false;
                	}
                }
            }

            // index
            if (indexVar != null) {
                for (int ii = 0; ii < oVarList.size(); ii++) {
                	SGNetCDFVariable oVar = oVarList.get(ii);
                   	List<Dimension> oDims = oVar.getDimensions();
                    if (!oDims.contains(indexDim)) {
                        return false;
                    }
                }
            }

            // multiple
            if (!multipleVariable.booleanValue()) {
                // multiple dimension

                final List<String> pickUpDimNameList = getColumnNameList(cols, PICKUP);
                if (pickUpDimNameList.size() != 1) {
                    return false;
                }
                String pickUpDimName = pickUpDimNameList.get(0);
                Dimension pickUpDim = ncfile.findDimension(pickUpDimName);
                if (pickUpDim == null) {
                    return false;
                }
                if (pickUpDim.equals(cDim) || pickUpDim.equals(timeDim)) {
                    return false;
                }
                boolean found = false;
                for (SGNetCDFVariable oVar : oVarList) {
                	List<Dimension> oDims = oVar.getDimensions();
                	if (oDims.contains(pickUpDim)) {
                		found = true;
                		break;
                	}
                }
                if (!found) {
                	return false;
                }
            }

        } else if (SGDataTypeConstants.SXYZ_NETCDF_DATA.equals(dataType)
        		|| SGDataTypeConstants.VXY_NETCDF_DATA.equals(dataType)) {

        	String xColumnType = SGDataTypeConstants.SXYZ_NETCDF_DATA.equals(dataType) ? X_VALUE : X_COORDINATE;
        	String yColumnType = SGDataTypeConstants.SXYZ_NETCDF_DATA.equals(dataType) ? Y_VALUE : Y_COORDINATE;

            final List<String> xNameList = getColumnNameList(cols, xColumnType);
            if (xNameList.size() != 1) {
                return false;
            }
            final List<String> yNameList = getColumnNameList(cols, yColumnType);
            if (yNameList.size() != 1) {
                return false;
            }
            String xName = xNameList.get(0);
            String yName = yNameList.get(0);
            SGNetCDFVariable xVar = ncfile.findVariable(xName);
            if (xVar == null) {
                return false;
            }
            SGNetCDFVariable yVar = ncfile.findVariable(yName);
            if (yVar == null) {
                return false;
            }

            final List<String> xIndexNameList = getColumnNameList(cols, X_INDEX);
            if (xIndexNameList.size() > 1) {
                return false;
            }
            SGNetCDFVariable xIndexVar = (xIndexNameList.size() == 1) ? ncfile.findVariable(xIndexNameList.get(0)) : null;

            final List<String> yIndexNameList = getColumnNameList(cols, Y_INDEX);
            if (yIndexNameList.size() > 1) {
                return false;
            }
            SGNetCDFVariable yIndexVar = (yIndexNameList.size() == 1) ? ncfile.findVariable(yIndexNameList.get(0)) : null;

            // x and y variables must be coordinate variables
            if (indexVar != null) {
                if (xVar.isCoordinateVariable()) {
                    return false;
                }
                if (yVar.isCoordinateVariable()) {
                    return false;
                }
            } else {
            	if (xIndexVar != null) {
            		if (!xIndexVar.isCoordinateVariable()) {
            			return false;
            		}
                    if (xVar.isCoordinateVariable()) {
                        return false;
                    }
            	} else {
                    if (!xVar.isCoordinateVariable()) {
                        return false;
                    }
            	}
            	if (yIndexVar != null) {
            		if (!yIndexVar.isCoordinateVariable()) {
            			return false;
            		}
                    if (yVar.isCoordinateVariable()) {
                        return false;
                    }
            	} else {
                    if (!yVar.isCoordinateVariable()) {
                        return false;
                    }
            	}
            }

            List<Dimension> xDims = xVar.getDimensions();
            List<Dimension> yDims = yVar.getDimensions();
        	Dimension xDim = null;
        	Dimension yDim = null;
        	if (indexVar == null) {
            	if (xIndexVar != null) {
                    xDim = xIndexVar.getDimension(0);
                    if (!xDims.contains(xDim)) {
                    	return false;
                    }
            	} else {
                    xDim = xDims.get(0);
            	}
            	if (yIndexVar != null) {
                    yDim = yIndexVar.getDimension(0);
                    if (!yDims.contains(yDim)) {
                    	return false;
                    }
            	} else {
                    yDim = yDims.get(0);
            	}
                if (xDim.equals(timeDim)) {
                	return false;
                }
                if (yDim.equals(timeDim)) {
                	return false;
                }
        	}

	        if (SGDataTypeConstants.SXYZ_NETCDF_DATA.equals(dataType)) {

	            final List<String> zNameList = getColumnNameList(cols, Z_VALUE);
	            if (zNameList.size() != 1) {
	                return false;
	            }
	            String zName = zNameList.get(0);
	            SGNetCDFVariable zVar = ncfile.findVariable(zName);
	            if (zVar == null) {
	                return false;
	            }

	            // z variable must not be a coordinate variable
	            if (zVar.isCoordinateVariable()) {
	                return false;
	            }

	            // variable must have a dimension equal to the coordinate variable
	            List<Dimension> zDims = zVar.getDimensions();
	            if (indexVar != null) {
	            	if (!xDims.contains(indexDim)) {
	            		return false;
	            	}
	            	if (!yDims.contains(indexDim)) {
	            		return false;
	            	}
	            	if (!zDims.contains(indexDim)) {
	            		return false;
	            	}
	            } else {
	                if (!zDims.contains(xDim)) {
	                    return false;
	                }
	                if (!zDims.contains(yDim)) {
	                    return false;
	                }
	            }

	        } else if (SGDataTypeConstants.VXY_NETCDF_DATA.equals(dataType)) {

	        	final String first = SGDataUtility.getVXYFirstComponentColumnType(infoMap);
	        	final String second = SGDataUtility.getVXYSecondComponentColumnType(infoMap);

	            final List<String> fNameList = getColumnNameList(cols, first);
	            if (fNameList.size() != 1) {
	                return false;
	            }
	            final List<String> sNameList = getColumnNameList(cols, second);
	            if (sNameList.size() != 1) {
	                return false;
	            }
	            String fName = fNameList.get(0);
	            String sName = sNameList.get(0);
	            SGNetCDFVariable fVar = ncfile.findVariable(fName);
	            if (fVar == null) {
	                return false;
	            }
	            SGNetCDFVariable sVar = ncfile.findVariable(sName);
	            if (sVar == null) {
	                return false;
	            }

	            // component variables must not be coordinate variable
	            if (fVar.isCoordinateVariable()) {
	                return false;
	            }
	            if (sVar.isCoordinateVariable()) {
	                return false;
	            }

	            List<Dimension> fDims = fVar.getDimensions();
	            List<Dimension> sDims = sVar.getDimensions();
	            if (indexVar != null) {
	                if (xDims.contains(indexDim) == false) {
	                    return false;
	                }
	                if (yDims.contains(indexDim) == false) {
	                    return false;
	                }
	                if (fDims.contains(indexDim) == false) {
	                    return false;
	                }
	                if (sDims.contains(indexDim) == false) {
	                    return false;
	                }
	            } else {
	                if (fDims.contains(xDim) == false) {
	                    return false;
	                }
	                if (fDims.contains(yDim) == false) {
	                    return false;
	                }
	                if (sDims.contains(xDim) == false) {
	                    return false;
	                }
	                if (sDims.contains(yDim) == false) {
	                    return false;
	                }
	            }
	        }

        } else {
            throw new Error("Invalid data type: " + dataType);
        }

        return true;
    }

    public static boolean checkMDArrayDataColumns(final SGDataColumnInfo[] cols,
            final String dataType, final SGMDArrayFile mdFile,
            final Map<String, Object> infoMap) {
    	return checkMDArrayDataColumns(cols, dataType, mdFile, infoMap, new StringBuffer());
    }

    /**
     * Checks the data columns for the multidimensional array data.
     *
     * @param cols
     *           the data columns
     * @param dataType
     *           the data type
     * @param mdFile
     *           the multidimensional data
     * @param infoMap
     *           the information map
     * @param errmsgBuffer
     *           buffer for the error message
     * @return true if columns are valid
     */
    public static boolean checkMDArrayDataColumns(final SGDataColumnInfo[] cols,
            final String dataType, final SGMDArrayFile mdFile,
            final Map<String, Object> infoMap, StringBuffer errmsgBuffer) {

        if (isSXYTypeData(dataType)) {
            // picked up dimension
            @SuppressWarnings("unchecked")
			Map<String, Integer> dimensionIndexMap = (Map<String, Integer>) infoMap.get(
            		SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP);
            SGMDArrayDataColumnInfo pickUpColumn = null;
            for (int ii = 0; ii < cols.length; ii++) {
            	SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
            	String columnType = mdCol.getColumnType();
            	if (X_VALUE.equals(columnType) || Y_VALUE.equals(columnType)) {
                	Integer dim = mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
                	if (SGDataUtility.isValidDimensionIndex(dim)) {
                		pickUpColumn = mdCol;
                		break;
                	}
            	}
            }
            boolean pickUpAssigned = (pickUpColumn != null);
            int pickUpDimLen = 0;
            if (pickUpAssigned) {
                if (pickUpColumn == null) {
                	throw new Error("pickUpColumn == null");
                }

                int[] dims = pickUpColumn.getDimensions();
                Integer dimIndex = dimensionIndexMap.get(pickUpColumn.getName());
        		if (!isValidPickUpValue(dimIndex)) {
        			throw new Error("Invalid dimension index: " + dimIndex);
            	}
            	if (dimIndex < 0 || dims.length < dimIndex) {
        			throw new Error("Invalid dimension index: " + dimIndex);
            	}
            	pickUpDimLen = dims[dimIndex];

                SGIntegerSeriesSet indices = (SGIntegerSeriesSet) infoMap.get(
                		SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
                if (indices == null) {
                	errmsgBuffer.append(MSG_PROPER_PICK_UP_INDICES);
                	return false;
                }
                int[] indexArray = indices.getNumbers();
                for (int ii = 0; ii < indexArray.length; ii++) {
                	if (indexArray[ii] < 0) {
                    	errmsgBuffer.append(MSG_PROPER_PICK_UP_INDICES);
                		return false;
                	}
                	if (pickUpDimLen <= indexArray[ii]) {
                    	errmsgBuffer.append(MSG_PROPER_PICK_UP_INDICES);
                		return false;
                	}
                }
            }

            // x and y values
            final List<SGDataColumnInfo> xColList = getColumnList(cols, X_VALUE);
            final List<SGDataColumnInfo> yColList = getColumnList(cols, Y_VALUE);
            if (xColList.size() == 0 && yColList.size() == 0) {
            	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
                return false;
            }
            if (pickUpAssigned) {
                if (xColList.size() > 1) {
                	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
                    return false;
                }
                if (yColList.size() > 1) {
                	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
                    return false;
                }
            } else {
                if (!(xColList.size() == 0 || xColList.size() == 1)
                		&& !(yColList.size() == 0 || yColList.size() == 1)) {
                	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
                    return false;
                }
            }

            // lower and upper error values
            final List<String> lNameList = new ArrayList<String>();
            final List<Integer> lIndexList = new ArrayList<Integer>();
            final List<String> uNameList = new ArrayList<String>();
            final List<Integer> uIndexList = new ArrayList<Integer>();
            if (getColumnNameAndAppendedNumberList(cols, LOWER_ERROR_VALUE, lNameList,
            		lIndexList) == false) {
            	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
            	return false;
            }
            if (getColumnNameAndAppendedNumberList(cols, UPPER_ERROR_VALUE, uNameList,
            		uIndexList) == false) {
            	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
            	return false;
            }
            if (lNameList.size() != uNameList.size()) {
            	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
                return false;
            }
            final List<String> luNameList = new ArrayList<String>();
            final List<Integer> luIndexList = new ArrayList<Integer>();
            if (getColumnNameAndAppendedNumberList(cols, LOWER_UPPER_ERROR_VALUE,
            		luNameList, luIndexList) == false) {
            	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
            	return false;
            }
            for (int ii = 0; ii < luNameList.size(); ii++) {
            	// checks whether lower/upper indices is contained
            	// in lower indices or upper indices
            	Integer luIndex = luIndexList.get(ii);
            	if (lIndexList.contains(luIndex)) {
                	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
            		return false;
            	}
            	if (uIndexList.contains(luIndex)) {
                	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
            		return false;
            	}
            }
            final List<SGDataColumnInfo> lColList = getColumnListStartsWith(cols, LOWER_ERROR_VALUE);
            final List<SGDataColumnInfo> uColList = getColumnListStartsWith(cols, UPPER_ERROR_VALUE);
            final List<SGDataColumnInfo> luColList = getColumnListStartsWith(cols, LOWER_UPPER_ERROR_VALUE);
            if (pickUpAssigned) {
            	if (lColList.size() > 1 || uColList.size() > 1 || luColList.size() > 1) {
                	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
            		return false;
            	}
            	if (lColList.size() != uColList.size()) {
                	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
            		return false;
            	}
            	if (lColList.size() + luColList.size() > 1) {
                	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
            		return false;
            	}
            	if (lColList.size() == 1 && uColList.size() == 1) {
            		SGMDArrayDataColumnInfo lCol = (SGMDArrayDataColumnInfo) lColList.get(0);
            		Integer lIndex = lCol.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
            		if (isValidPickUpValue(lIndex)) {
                		final int[] lDims = lCol.getDimensions();
                		if (lDims[lIndex] != pickUpDimLen) {
                        	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH_PICK_UP);
                			return false;
                		}
            		}
            		SGMDArrayDataColumnInfo uCol = (SGMDArrayDataColumnInfo) uColList.get(0);
            		Integer uIndex = uCol.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
            		if (isValidPickUpValue(uIndex)) {
                		final int[] uDims = uCol.getDimensions();
                		if (uDims[uIndex] != pickUpDimLen) {
                        	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH_PICK_UP);
                			return false;
                		}
            		}
            	} else if (luColList.size() == 1) {
            		SGMDArrayDataColumnInfo luCol = (SGMDArrayDataColumnInfo) luColList.get(0);
            		Integer lIndex = luCol.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
            		if (isValidPickUpValue(lIndex)) {
                		final int[] lDims = luCol.getDimensions();
                		if (lDims[lIndex] != pickUpDimLen) {
                        	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH_PICK_UP);
                			return false;
                		}
            		}
            	}
            }

            // tick labels
            final List<SGDataColumnInfo> tColList = getColumnListStartsWith(cols, TICK_LABEL);
            if (pickUpAssigned) {
            	if (tColList.size() > 1) {
                	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
            		return false;
            	}
            	if (tColList.size() == 1) {
            		SGMDArrayDataColumnInfo tCol = (SGMDArrayDataColumnInfo) tColList.get(0);
            		Integer index = tCol.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
            		if (isValidPickUpValue(index)) {
                		final int[] dims = tCol.getDimensions();
                		if (dims[index] != pickUpDimLen) {
                        	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH_PICK_UP);
                			return false;
                		}
            		}
            	}
            }

            // check length of all variables
            List<SGDataColumnInfo> allColList = new ArrayList<SGDataColumnInfo>();
            allColList.addAll(xColList);
            allColList.addAll(yColList);
            allColList.addAll(lColList);
            allColList.addAll(uColList);
            allColList.addAll(luColList);
            allColList.addAll(tColList);
            int len = 0;
            for (int ii = 0; ii < allColList.size(); ii++) {
            	SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) allColList.get(ii);
            	if (ii == 0) {
            		len = col.getGenericDimensionLength();
            		if (len == -1) {
            			return false;
            		}
            	} else {
            		final int gLen = col.getGenericDimensionLength();
            		if (gLen == -1) {
            			return false;
            		}
            		if (gLen != len) {
                    	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
            			return false;
            		}
            	}
            }

        } else if (isSXYZTypeData(dataType)) {

            final List<SGDataColumnInfo> zColList = getColumnList(cols, Z_VALUE);
            if (zColList.size() != 1) {
            	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
                return false;
            }
            SGMDArrayDataColumnInfo zCol = (SGMDArrayDataColumnInfo) zColList.get(0);
            final int[] zDims = zCol.getDimensions();

            final List<SGDataColumnInfo> xColList = getColumnList(cols, X_VALUE);
            if (xColList.size() > 1) {
            	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
                return false;
            }
            final List<SGDataColumnInfo> yColList = getColumnList(cols, Y_VALUE);
            if (yColList.size() > 1) {
            	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
                return false;
            }

            // checks whether given column types are for grid plot or scatter plot
            Integer zIndex = zCol.getGenericDimensionIndex();
            final boolean gridPlot = (zIndex.intValue() == -1);

            // checks length of dimensions
            if (gridPlot) {
            	// grid plot
                if(xColList.size() == 1) {
                    SGMDArrayDataColumnInfo xCol = (SGMDArrayDataColumnInfo) xColList.get(0);
                    final int len = xCol.getGenericDimensionLength();
                    if (len == -1) {
                    	return false;
                    }
                    final Integer zxIndex = zCol.getDimensionIndex(KEY_SXYZ_X_DIMENSION);
                    if (!SGDataUtility.isValidDimensionIndex(zxIndex)) {
                    	return false;
                    }
                    final int zxLen = zDims[zxIndex];
                    if (len != zxLen) {
                    	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
                    	return false;
                    }
                }
                if(yColList.size() == 1) {
                    SGMDArrayDataColumnInfo yCol = (SGMDArrayDataColumnInfo) yColList.get(0);
                    final int len = yCol.getGenericDimensionLength();
                    if (len == -1) {
                    	return false;
                    }
                    final Integer zyIndex = zCol.getDimensionIndex(KEY_SXYZ_Y_DIMENSION);
                    if (!SGDataUtility.isValidDimensionIndex(zyIndex)) {
                    	return false;
                    }
                    final int zyLen = zDims[zyIndex];
                    if (len != zyLen) {
                    	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
                    	return false;
                    }
                }
            } else {
            	// scatter plot
            	final int zLen = zCol.getGenericDimensionLength();
                if (zLen == -1) {
                	return false;
                }
                if(xColList.size() == 1) {
                    SGMDArrayDataColumnInfo xCol = (SGMDArrayDataColumnInfo) xColList.get(0);
                    final int len = xCol.getGenericDimensionLength();
                    if (len == -1) {
                    	return false;
                    }
                    if (len != zLen) {
                    	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
                    	return false;
                    }
                }
                if(yColList.size() == 1) {
                    SGMDArrayDataColumnInfo yCol = (SGMDArrayDataColumnInfo) yColList.get(0);
                    final int len = yCol.getGenericDimensionLength();
                    if (len == -1) {
                    	return false;
                    }
                    if (len != zLen) {
                    	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
                    	return false;
                    }
                }
            }
            
        } else if (isVXYTypeData(dataType)) {
        	
        	final String first = SGDataUtility.getVXYFirstComponentColumnType(infoMap);
        	final String second = SGDataUtility.getVXYSecondComponentColumnType(infoMap);
            final List<SGDataColumnInfo> fColList = getColumnList(cols, first);
            if (fColList.size() != 1) {
            	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
                return false;
            }
            final List<SGDataColumnInfo> sColList = getColumnList(cols, second);
            if (sColList.size() != 1) {
            	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
                return false;
            }

            SGMDArrayDataColumnInfo fCol = (SGMDArrayDataColumnInfo) fColList.get(0);
            final int[] fDims = fCol.getDimensions();
            SGMDArrayDataColumnInfo sCol = (SGMDArrayDataColumnInfo) sColList.get(0);
            final int[] sDims = sCol.getDimensions();
            final List<SGDataColumnInfo> xColList = getColumnList(cols, X_COORDINATE);
            if (xColList.size() > 1) {
            	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
                return false;
            }
            final List<SGDataColumnInfo> yColList = getColumnList(cols, Y_COORDINATE);
            if (yColList.size() > 1) {
            	errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
                return false;
            }

            // checks whether given column types are for grid plot or scatter plot
            Integer fIndex = fCol.getGenericDimensionIndex();
            final boolean gridPlot = (fIndex.intValue() == -1);

            // check length of dimensions
            if (gridPlot) {
            	// grid plot
                if(xColList.size() == 1) {
                    SGMDArrayDataColumnInfo xCol = (SGMDArrayDataColumnInfo) xColList.get(0);
                    final int len = xCol.getGenericDimensionLength();
                    if (len == -1) {
                    	return false;
                    }
                    final Integer fxIndex = fCol.getDimensionIndex(KEY_VXY_X_DIMENSION);
                    if (!SGDataUtility.isValidDimensionIndex(fxIndex)) {
                    	return false;
                    }
                    final int fxLen = fDims[fxIndex];
                    if (fxLen == -1) {
                    	return false;
                    }
                    if (len != fxLen) {
                    	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
                    	return false;
                    }
                    final Integer sxIndex = sCol.getDimensionIndex(KEY_VXY_X_DIMENSION);
                    if (!SGDataUtility.isValidDimensionIndex(sxIndex)) {
                    	return false;
                    }
                    final int sxLen = sDims[sxIndex];
                    if (sxLen == -1) {
                    	return false;
                    }
                    if (len != sxLen) {
                    	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
                    	return false;
                    }
                }
                if(yColList.size() == 1) {
                    SGMDArrayDataColumnInfo yCol = (SGMDArrayDataColumnInfo) yColList.get(0);
                    final int len = yCol.getGenericDimensionLength();
                    if (len == -1) {
                    	return false;
                    }
                    final Integer fyIndex = fCol.getDimensionIndex(KEY_VXY_Y_DIMENSION);
                    if (!SGDataUtility.isValidDimensionIndex(fyIndex)) {
                    	return false;
                    }
                    final int fyLen = fDims[fyIndex];
                    if (fyLen == -1) {
                    	return false;
                    }
                    if (len != fyLen) {
                    	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
                    	return false;
                    }
                    final Integer syIndex = sCol.getDimensionIndex(KEY_VXY_Y_DIMENSION);
                    if (!SGDataUtility.isValidDimensionIndex(syIndex)) {
                    	return false;
                    }
                    final int syLen = sDims[syIndex];
                    if (syLen == -1) {
                    	return false;
                    }
                    if (len != syLen) {
                    	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
                    	return false;
                    }
                }
            	
            } else {
            	// scatter plot
            	final int fLen = fCol.getGenericDimensionLength();
                if (fLen == -1) {
                	return false;
                }
            	final int sLen = sCol.getGenericDimensionLength();
                if (sLen == -1) {
                	return false;
                }
                if(xColList.size() == 1) {
                    SGMDArrayDataColumnInfo xCol = (SGMDArrayDataColumnInfo) xColList.get(0);
                    final int len = xCol.getGenericDimensionLength();
                    if (len == -1) {
                    	return false;
                    }
                    if (len != fLen) {
                    	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
                    	return false;
                    }
                    if (len != sLen) {
                    	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
                    	return false;
                    }
                }
                if(yColList.size() == 1) {
                    SGMDArrayDataColumnInfo yCol = (SGMDArrayDataColumnInfo) yColList.get(0);
                    final int len = yCol.getGenericDimensionLength();
                    if (len == -1) {
                    	return false;
                    }
                    if (len != fLen) {
                    	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
                    	return false;
                    }
                    if (len != sLen) {
                    	errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
                    	return false;
                    }
                }
            }
        }

    	return true;
    }

    /**
     * Returns the maximum values in a given array excepting NaN and infinity.
     *
     * @param array
     *           an array of values
     * @return
     *           the maximum value
     */
    public static double getMaxValue(double[] array) {
        boolean valid = false;
        double max = - Double.MAX_VALUE;
        for (int ii = 0; ii < array.length; ii++) {
            if (Double.isNaN(array[ii]) || Double.isInfinite(array[ii])) {
                continue;
            }
            if (array[ii] > max) {
                max = array[ii];
            }
            valid = true;
        }
        if (!valid) {
        	return Double.NaN;
        }
        return max;
    }

    /**
     * Returns the minimum values in a given array excepting NaN and infinity.
     *
     * @param array
     *           an array of values
     * @return
     *           the minimum value
     */
    public static double getMinValue(double[] array) {
        boolean valid = false;
        double min = Double.MAX_VALUE;
        for (int ii = 0; ii < array.length; ii++) {
            if (Double.isNaN(array[ii]) || Double.isInfinite(array[ii])) {
                continue;
            }
            if (array[ii] < min) {
                min = array[ii];
            }
            valid = true;
        }
        if (!valid) {
        	return Double.NaN;
        }
        return min;
    }

    /**
     * Returns the bounds from given array.
     *
     * @param array
     *          an array of values to search the mininum and maximum value
     * @return the bounds of given array
     */
    public static SGValueRange getBounds(final double[] array) {
        return new SGValueRange(getMinValue(array), getMaxValue(array));
    }

    /**
     * Returns the bounds of x-values of given data.
     *
     * @param data
     *           a data
     * @return the bounds of x-values
     */
    public static SGValueRange getBoundsX(SGISXYTypeSingleData data) {
        final double[] xValues = data.getXValueArray(false);
        if (xValues == null) {
            return null;
        }
        if (data.isErrorBarAvailable() && !data.isErrorBarVertical()) {
        	return getBounds(data, xValues);
        } else {
            return getBounds(xValues);
        }
    }

    /**
     * Returns the bounds of y-values of given data.
     *
     * @param data
     *           a data
     * @return the bounds of y-values
     */
    public static SGValueRange getBoundsY(SGISXYTypeSingleData data) {
        final double[] yValues = data.getYValueArray(false);
        if (yValues == null) {
            return null;
        }
        if (data.isErrorBarAvailable() && data.isErrorBarVertical()) {
        	return getBounds(data, yValues);
        } else {
            return getBounds(yValues);
        }
    }

    private static SGValueRange getBounds(SGISXYTypeSingleData data, double[] values) {
        final double[] lArray = data.getLowerErrorValueArray(false);
        final double[] uArray = data.getUpperErrorValueArray(false);
        final double[] lyValues = new double[values.length];
        final double[] uyValues = new double[values.length];
        for (int ii = 0; ii < values.length; ii++) {
            lyValues[ii] = values[ii] - Math.abs(lArray[ii]);
            uyValues[ii] = values[ii] + Math.abs(uArray[ii]);
        }
        SGValueRange vBounds = getBounds(values);
        SGValueRange lBounds = getBounds(lyValues);
        SGValueRange uBounds = getBounds(uyValues);
        List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
        rangeList.add(vBounds);
        rangeList.add(lBounds);
        rangeList.add(uBounds);
        Double min = getMinValue(rangeList);
        Double max = getMaxValue(rangeList);
        final double minValue = (min != null) ? min.doubleValue() : Double.NaN;
        final double maxValue = (max != null) ? max.doubleValue() : Double.NaN;
        SGValueRange bounds = new SGValueRange(minValue, maxValue);
        return bounds;
    }

    public static Double getMinValue(List<SGValueRange> rangeList) {
    	Double min = null;
    	for (int ii = 0; ii < rangeList.size(); ii++) {
    		SGValueRange range = rangeList.get(ii);
    		if (range.isMinValid()) {
    			final double value = range.getMinValue();
    			if (min == null) {
    				min = Double.valueOf(value);
    			} else {
    				if (value < min.doubleValue()) {
    					min = Double.valueOf(value);
    				}
    			}
    		}
    	}
    	return min;
    }

    public static Double getMaxValue(List<SGValueRange> rangeList) {
    	Double max = null;
    	for (int ii = 0; ii < rangeList.size(); ii++) {
    		SGValueRange range = rangeList.get(ii);
    		if (range.isMaxValid()) {
    			final double value = range.getMaxValue();
    			if (max == null) {
    				max = Double.valueOf(value);
    			} else {
    				if (value > max.doubleValue()) {
    					max = Double.valueOf(value);
    				}
    			}
    		}
    	}
    	return max;
    }

    /**
     * Returns the bounds of x-values of given data.
     *
     * @param data
     *           a data
     * @return the bounds of x-values
     */
    public static SGValueRange getBoundsX(SGISXYTypeMultipleData data) {
        SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
        List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
        for (int ii = 0; ii < sxyArray.length; ii++) {
        	SGValueRange range = getBoundsX(sxyArray[ii]);
        	rangeList.add(range);
        }
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);
    	return getBounds(rangeList);
    }

    /**
     * Returns the bounds of y-values of given data.
     *
     * @param data
     *           a data
     * @return the bounds of y-values
     */
    public static SGValueRange getBoundsY(SGISXYTypeMultipleData data) {
        SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
        List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
        for (int ii = 0; ii < sxyArray.length; ii++) {
        	SGValueRange range = getBoundsY(sxyArray[ii]);
        	rangeList.add(range);
        }
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);
    	return getBounds(rangeList);
    }

    private static SGValueRange getBounds(List<SGValueRange> rangeList) {
        Double min = getMinValue(rangeList);
        Double max = getMaxValue(rangeList);
    	final double minValue = (min != null) ? min.doubleValue() : Double.NaN;
    	final double maxValue = (max != null) ? max.doubleValue() : Double.NaN;
    	return new SGValueRange(minValue, maxValue);
    }

    /**
     * Returns the bounds of x-values of given data.
     *
     * @param data
     *           a data
     * @return the bounds of x-values
     */
    public static SGValueRange getBoundsX(SGIVXYTypeData data) {
        final double[] values = data.getXValueArray(false);
        if (values == null) {
            return null;
        }
        return getBounds(values);
//        final SGTuple2d[] coordinates = data.getXYValueArray();
//        if (coordinates == null) {
//            return null;
//        }
//        final int len = coordinates.length;
//        double[] values = new double[len];
//        for (int ii = 0; ii < len; ii++) {
//            values[ii] = coordinates[ii].x;
//        }
//        return getBounds(values);
    }

    /**
     * Returns the bounds of y-values of given data.
     *
     * @param data
     *           a data
     * @return the bounds of y-values
     */
    public static SGValueRange getBoundsY(SGIVXYTypeData data) {
//        final SGTuple2d[] coordinates = data.getXYValueArray();
//        if (coordinates == null) {
//            return null;
//        }
//        final int len = coordinates.length;
//        double[] values = new double[len];
//        for (int ii = 0; ii < len; ii++) {
//            values[ii] = coordinates[ii].y;
//        }
//        return getBounds(values);
        final double[] values = data.getYValueArray(false);
        if (values == null) {
            return null;
        }
        return getBounds(values);
    }

    /**
     * Returns whether the given type of data can have tick labels.
     *
     * @param dataType
     *           the data type
     * @return
     *           true if the given type of data can have tick labels
     */
    public static boolean hasTickLabels(final String dataType) {
        return (SGDataTypeConstants.SXY_DATA.equals(dataType)
                || SGDataTypeConstants.SXY_DATE_DATA.equals(dataType));
    }

    /**
     * Returns the bounds of x-values of given data.
     *
     * @param data
     *           a data
     * @return the bounds of x-values
     */
    public static SGValueRange getBoundsX(SGISXYZTypeData data) {
        final double[] values = data.getXValueArray(false);
        if (values == null) {
            return null;
        }
        return getBounds(values);
    }

    /**
     * Returns the bounds of y-values of given data.
     *
     * @param data
     *           a data
     * @return the bounds of y-values
     */
    public static SGValueRange getBoundsY(SGISXYZTypeData data) {
        final double[] values = data.getYValueArray(false);
        if (values == null) {
            return null;
        }
        return getBounds(values);
    }

    /**
     * Returns the bounds of z-values of given data.
     *
     * @param data
     *           a data
     * @return the bounds of z-values
     */
    public static SGValueRange getBoundsZ(SGISXYZTypeData data) {
        final double[] values = data.getZValueArray(false);
        if (values == null) {
            return null;
        }
        return getBounds(values);
    }

    /**
     * Returns whether the given data is of the single dimensional array type.
     *
     * @param data
     *           the data object
     * @return true if the given data is of the single dimensional array type
     */
    public static boolean isSDArrayData(final SGData data) {
        return isSDArrayData(data.getDataType());
    }

    /**
     * Returns whether the given data is of the netCDF type.
     *
     * @param data
     *           the data object
     * @return true if the given data is of the netCDF type
     */
    public static boolean isNetCDFData(final SGData data) {
        return isNetCDFData(data.getDataType());
    }

    /**
     * Returns whether given data is NetCDF-4 format.
     * 
     * @param data
     *           the data
     * @return true if given data is NetCDF-4 format
     */
    public static boolean isNetCDF4Data(final SGData data) {
    	if (!isNetCDFData(data.getDataType())) {
    		return false;
    	}
    	boolean hdf5 = true;
    	IHDF5Reader reader = null;
		try {
	    	reader = HDF5FactoryProvider.get().openForReading(new File(data.getPath()));
        } catch (HDF5Exception e) {
        	hdf5 = false;
        } finally {
        	if (reader != null) {
        		reader.close();
        	}
        }
        return hdf5;
    }

    /**
     * Returns whether the given data is of the HDF5 type.
     *
     * @param data
     *           the data object
     * @return true if the given data is of the HDF5 type
     */
    public static boolean isHDF5Data(final SGData data) {
        return isHDF5Data(data.getDataType());
    }

    /**
     * Returns whether the given data is of the MATLAB type.
     *
     * @param data
     *           the data object
     * @return true if the given data is of the MATLAB type
     */
    public static boolean isMATLABData(final SGData data) {
        return isMATLABData(data.getDataType());
    }

    /**
     * Returns whether the given data is of virtual multidimensional array type.
     *
     * @param data
     *           the data object
     * @return true if the given data is of virtual multidimensional array type
     */
    public static boolean isVirtualMDArrayData(final SGData data) {
        return isVirtualMDArrayData(data.getDataType());
    }

    /**
     * Returns whether the given data is of the multidimensional array type.
     *
     * @param data
     *           the data object
     * @return true if the given data is of the multidimensional array type
     */
    public static boolean isMDArrayData(final SGData data) {
        return isMDArrayData(data.getDataType());
    }

    /**
     * Returns whether the given type of data is of the single dimensional array type.
     *
     * @param dataType
     *           the data type
     * @return true if the given type of data is of the array type
     */
    public static boolean isSDArrayData(final String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Input data type is null.");
        }
        String[] types = { SGDataTypeConstants.SXY_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_DATA,
                SGDataTypeConstants.VXY_DATA,
                SGDataTypeConstants.SXYZ_DATA,

                // for backward compatibility
                SGDataTypeConstants.SXY_SAMPLING_DATA,
                SGDataTypeConstants.SXY_DATE_DATA
                };
        return SGUtility.contains(types, dataType);
    }

    /**
     * Returns whether the given type of data is of the netCDF type.
     *
     * @param dataType
     *           the data type
     * @return true if the given type of data is of the netCDF type
     */
    public static boolean isNetCDFData(final String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Input data type is null.");
        }
        String[] types = {
                SGDataTypeConstants.SXY_NETCDF_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA,
                SGDataTypeConstants.VXY_NETCDF_DATA,
                SGDataTypeConstants.SXYZ_NETCDF_DATA
        };
        return SGUtility.contains(types, dataType);
    }

    /**
     * Returns whether the given type of data is of the HDF5 type.
     *
     * @param dataType
     *           the data type
     * @return true if the given type of data is of the HDF5 type
     */
    public static boolean isHDF5Data(final String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Input data type is null.");
        }
        String[] types = {
                SGDataTypeConstants.SXY_HDF5_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA,
                SGDataTypeConstants.VXY_HDF5_DATA,
                SGDataTypeConstants.SXYZ_HDF5_DATA
        };
        return SGUtility.contains(types, dataType);
    }

    /**
     * Returns whether the given type of data is of the MATLAB type.
     *
     * @param dataType
     *           the data type
     * @return true if the given type of data is of the MATLAB type
     */
    public static boolean isMATLABData(final String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Input data type is null.");
        }
        String[] types = {
                SGDataTypeConstants.SXY_MATLAB_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA,
                SGDataTypeConstants.VXY_MATLAB_DATA,
                SGDataTypeConstants.SXYZ_MATLAB_DATA
        };
        return SGUtility.contains(types, dataType);
    }

    /**
     * Returns whether the given type of data is of virtual type.
     *
     * @param dataType
     *           the data type
     * @return true if the given type of data is of virtual type
     */
    public static boolean isVirtualMDArrayData(final String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Input data type is null.");
        }
        String[] types = {
                SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_VIRTUAL_MDARRAY_DATA,
                SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA,
                SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA
        };
        return SGUtility.contains(types, dataType);
    }

    /**
     * Returns whether the given type of data is of the multidimensional array type.
     *
     * @param dataType
     *           the data type
     * @return true if the given type of data is of the multidimensional array type
     */
    public static boolean isMDArrayData(final String dataType) {
    	return isHDF5Data(dataType) || isMATLABData(dataType) || isVirtualMDArrayData(dataType);
    }

    /**
     * Returns whether the given type of data is of HDF5 type or virtual multidimensional array type.
     *
     * @param dataType
     *           the data type
     * @return true if the given type of data is of HDF5 type or virtual multidimensional array type.
     */
    public static boolean isHDF5FileData(final String dataType) {
    	return isHDF5Data(dataType) || isVirtualMDArrayData(dataType);
    }

    /**
     * Returns whether the given type of data is of the scalar-XY type.
     * (Sampling XY data and date XY data are excepted.)
     *
     * @param dataType
     *           the data type
     * @return true if the given type of data is of the scalar-XY type
     */
    public static boolean isSXYTypeData(final String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Input data type is null.");
        }
        if (isSXYTypeSingleData(dataType)) {
        	return true;
        }
        if (isSXYTypeMultipleData(dataType)) {
        	return true;
        }
        
        // for backward compatibility
        String[] types = {
                SGDataTypeConstants.SXY_SAMPLING_DATA,
                SGDataTypeConstants.SXY_DATE_DATA
        };
        return SGUtility.contains(types, dataType);
    }

    public static boolean isSXYTypeSingleData(final String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Input data type is null.");
        }
        String[] types = {
                SGDataTypeConstants.SXY_DATA,
                SGDataTypeConstants.SXY_NETCDF_DATA,
                SGDataTypeConstants.SXY_HDF5_DATA,
                SGDataTypeConstants.SXY_MATLAB_DATA,
                SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA
        };
        return SGUtility.contains(types, dataType);
    }

    public static boolean isSXYTypeMultipleData(final String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Input data type is null.");
        }
        String[] types = {
                SGDataTypeConstants.SXY_MULTIPLE_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_VIRTUAL_MDARRAY_DATA
        };
        return SGUtility.contains(types, dataType);
    }

    /**
     * Returns whether the given type of data is of the vector-XY type.
     *
     * @param dataType
     *           the data type
     * @return true if the given type of data is of the vector-XY type
     */
    public static boolean isVXYTypeData(final String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Input data type is null.");
        }
        String[] types = { SGDataTypeConstants.VXY_DATA,
				SGDataTypeConstants.VXY_NETCDF_DATA,
				SGDataTypeConstants.VXY_HDF5_DATA,
				SGDataTypeConstants.VXY_MATLAB_DATA,
				SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA };
        return SGUtility.contains(types, dataType);
    }

    /**
     * Returns whether the given type of data is of the scalar-XYZ type.
     *
     * @param dataType
     *           the data type
     * @return true if the given type of data is of the scalar-XYZ type
     */
    public static boolean isSXYZTypeData(final String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Input data type is null.");
        }
        String[] types = { SGDataTypeConstants.SXYZ_DATA,
				SGDataTypeConstants.SXYZ_NETCDF_DATA,
				SGDataTypeConstants.SXYZ_HDF5_DATA,
				SGDataTypeConstants.SXYZ_MATLAB_DATA,
				SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA };
        return SGUtility.contains(types, dataType);
    }

    /**
     * Returns whether the given type of data is of the multiple type data.
     * This method is only for backward compatibility.
     *
     * @param dataType
     *           the data type
     * @return true if the given type of data is of the multiple type
     */
    public static boolean isMultipleData(final String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Input data type is null.");
        }
        String[] types = {
                SGDataTypeConstants.SXY_MULTIPLE_DATA,
                SGDataTypeConstants.SXY_SAMPLING_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA,
                SGDataTypeConstants.SXY_MULTIPLE_VIRTUAL_MDARRAY_DATA
        };
        return SGUtility.contains(types, dataType);
    }

    /**
     * Returns whether the given type of data is of the multiple type
     * netCDF dimension data.
     *
     * @param dataType
     * @return true if the given type of data is of the multiple type of netCDF dimension
     * @throws IllegalArgumentException if dataType is null
     */
    public static boolean isNetCDFDimensionData(final String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Input data type is null.");
        }
        String[] types = {
                SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA
        };
        return SGUtility.contains(types, dataType);
    }

    public static boolean isMDArrayDimensionData(final SGData data) {
        if (data == null) {
            throw new IllegalArgumentException("Input data.");
        }
        if (!(data instanceof SGSXYMDArrayMultipleData)) {
        	return false;
        }
        SGSXYMDArrayMultipleData sxyData = (SGSXYMDArrayMultipleData) data;
        return (sxyData.getPickUpDimensionInfo() != null);
    }

    /**
     * Returns whether the given data type is valid.
     *
     * @param dataType
     *           the data type
     * @return
     *           true if the given type of data is valid
     */
    public static boolean isValidData(final String dataType) {
        if (isSDArrayData(dataType)) {
            return true;
        }
        if (isNetCDFData(dataType)) {
            return true;
        }
        if (isMDArrayData(dataType)) {
            return true;
        }
        return false;
    }

    /**
     * Updates the information map with given data type and column information.
     *
     * @param dataType
     *           the data type
     * @param colInfo
     *           the column information
     * @param infoMap
     *           the information map
     * @return updated information map
     */
    public static Map<String, Object> updateInfoMap(String dataType,
    		SGDataColumnInfo[] colInfo, Map<String, Object> infoMap) {
    	Map<String, Object> infoMapUpd = new HashMap<String, Object>(infoMap);
//        if (isSXYTypeData(dataType)) {
//        	// column information
//        	infoMapUpd.put(SGIDataInformationKeyConstants.KEY_COLUMN_INFO, colInfo.clone());
//        }
    	// column information
    	infoMapUpd.put(SGIDataInformationKeyConstants.KEY_COLUMN_INFO, colInfo.clone());
    	return infoMapUpd;
    }

    /**
     * Updates the column types.
     *
     * @param dataType
     *            data type
     * @param colInfo
     *            column information
     * @param columnTypes
     *            an array of columns types
     * @return new column types
     */
    public static String[] updateDataColumns(final String dataType,
            final SGDataColumnInfo[] colInfo, final String[] columnTypes) {

    	String[] ret = (String[]) columnTypes.clone();
        if (isSXYTypeData(dataType)) {

            // find the column for X or Y
            List<Integer> xIndexList = new ArrayList<Integer>();
            List<Integer> yIndexList = new ArrayList<Integer>();
            for (int ii = 0; ii < columnTypes.length; ii++) {
                final Integer index = Integer.valueOf(ii);
                if (X_VALUE.equals(columnTypes[ii])) {
                    xIndexList.add(index);
                } else if (Y_VALUE.equals(columnTypes[ii])) {
                    yIndexList.add(index);
                }
            }

            // get the list of column index those are assignable for the error bar
            List<Integer> indexList = getOptionalColumnsAssignableIndexList(
                    dataType, colInfo, xIndexList, yIndexList);
            if (indexList == null) {
                return null;
            }

            // clear the column type for error bars for non-existing X or Y
            for (int ii = 0; ii < columnTypes.length; ii++) {
                String colType = columnTypes[ii];
                if (colType.startsWith(LOWER_ERROR_VALUE)
                        || colType.startsWith(UPPER_ERROR_VALUE)
                        || colType.startsWith(LOWER_UPPER_ERROR_VALUE)
                        || colType.startsWith(TICK_LABEL)) {
                    // get the column number
                    Integer num = getColumnIndexOfAppendedColumnType(colType, dataType, colInfo);
                    if (num == null) {
                        return null;
                    }
                    if (!indexList.contains(num)) {
                        // clear the column type
                        ret[ii] = "";
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Returns the list of column indices those are assignable optional columns
     * such as error bars tick labels.
     *
     */
    private static List<Integer> getOptionalColumnsAssignableIndexList(
    		String dataType, SGDataColumnInfo[] colInfo,
    		List<Integer> xIndexList, List<Integer> yIndexList) {
    	if (!isSXYTypeData(dataType)) {
    		return null;
    	}
    	Set<Integer> indexSet = new TreeSet<Integer>();
    	for (Integer index : xIndexList) {
    		String valueType = colInfo[index].getValueType();
    		if (VALUE_TYPE_NUMBER.equals(valueType)) {
    			indexSet.add(index);
    		}
    	}
    	for (Integer index : yIndexList) {
    		String valueType = colInfo[index].getValueType();
    		if (VALUE_TYPE_NUMBER.equals(valueType)) {
    			indexSet.add(index);
    		}
    	}
    	List<Integer> indexList = new ArrayList<Integer>();
    	if (isSDArrayData(dataType)) {
    		indexList.addAll(indexSet);
    	} else if (isNetCDFData(dataType)) {
    		for (int ii = 0; ii < colInfo.length; ii++) {
    			SGNetCDFDataColumnInfo ncInfo = (SGNetCDFDataColumnInfo) colInfo[ii];
    			Integer index = Integer.valueOf(ii);
    			if (indexSet.contains(index)) {
        			if (!ncInfo.isCoordinateVariable()) {
        				indexList.add(index);
        			}
    			}
    		}
    	} else if (isMDArrayData(dataType)) {
    		for (int ii = 0; ii < colInfo.length; ii++) {
    			Integer index = Integer.valueOf(ii);
    			if (indexSet.contains(index)) {
    				indexList.add(index);
    			}
    		}
    	} else {
    		return null;
    	}
    	return indexList;
    }

    /**
     * Creates a text string for the title with given sampling rate.
     *
     * @return the title for this sampling data
     */
    public static String createSamplingRateTitle(final double samplingRate) {
    	StringBuffer sb = new StringBuffer();
    	sb.append("Sampling Rate ");
    	sb.append(samplingRate);
    	sb.append(" Hz");
    	return sb.toString();
    }

    /**
     * Get index list of column type on text data.
     * <p>
     * If column type in <b>colInfo</b> matches "X", add the index of <b>colInfo</b>
     * into <b>xIndexList</b>. Same, check matching of "Y", "Lower Error", "Upper Error",
     * "Lower / Upper Error" and "Tick Label".
     *
     * @param colInfo
     * @param xIndexList
     * @param yIndexList
     * @param lIndexMap
     * @param uIndexMap
     * @param tIndexMap
     * @return false if column type name is a wrong name. true if succeeds.
     */
    public static boolean getSXYColumnType(SGDataColumnInfo[] colInfo,
    		List<Integer> xIndexList, List<Integer> yIndexList,
    		Map<Integer, Integer> lIndexMap, Map<Integer, Integer> uIndexMap,
    		Map<Integer, Integer> tIndexMap) {
        for (int ii = 0; ii < colInfo.length; ii++) {
            String cType = colInfo[ii].getColumnType();
            if (SGDataUtility.isEqualColumnType(X_VALUE, cType)) {
            	xIndexList.add(Integer.valueOf(ii));
            } else if (SGDataUtility.isEqualColumnType(Y_VALUE, cType)) {
            	yIndexList.add(Integer.valueOf(ii));
            } else if (SGDataUtility.columnTypeStartsWith(cType, LOWER_ERROR_VALUE)) {
            	Integer num = SGDataUtility.getAppendedColumnIndex(cType, colInfo);
            	lIndexMap.put(num, Integer.valueOf(ii));
            } else if (SGDataUtility.columnTypeStartsWith(cType, UPPER_ERROR_VALUE)) {
            	Integer num = SGDataUtility.getAppendedColumnIndex(cType, colInfo);
            	uIndexMap.put(num, Integer.valueOf(ii));
            } else if (SGDataUtility.columnTypeStartsWith(cType, LOWER_UPPER_ERROR_VALUE)) {
            	Integer num = SGDataUtility.getAppendedColumnIndex(cType, colInfo);
            	lIndexMap.put(num, Integer.valueOf(ii));
            	uIndexMap.put(num, Integer.valueOf(ii));
            } else if (SGDataUtility.columnTypeStartsWith(cType, TICK_LABEL)) {
            	Integer num = SGDataUtility.getAppendedColumnIndex(cType, colInfo);
            	tIndexMap.put(num, Integer.valueOf(ii));
            } else if ("".equals(cType)) {
                continue;
            } else {
            	return false;
            }
        }
        return true;
    }

    /**
     * Get name list of column type on netCDF data.
     * <p>
     * If column type in <b>colInfo</b> matches "X", add the variable name of <b>colInfo</b>
     * into <b>xNameList</b>. Same, check matching of "Y", "Lower Error", "Upper Error",
     * "Lower / Upper Error", "Tick Label", "Time" and "Serial Number".
     *
     * @param colInfo
     * @param xInfoList contains variable names which have "X" column type.
     * @param yInfoList
     * @param leInfoMap
     * @param ueInfoMap
     * @param tlInfoMap
     * @param timeInfoList
     * @param indexInfoList
     * @param pickUpInfoList
     * @return
     */
    public static boolean getSXYColumnType(SGDataColumnInfo[] colInfo,
            List<SGDataColumnInfo> xInfoList, List<SGDataColumnInfo> yInfoList,
            Map<SGDataColumnInfo, SGDataColumnInfo> leInfoMap, Map<SGDataColumnInfo, SGDataColumnInfo> ueInfoMap,
            Map<SGDataColumnInfo, SGDataColumnInfo> tlInfoMap, List<SGDataColumnInfo> timeInfoList,
            List<SGDataColumnInfo> indexInfoList, List<SGDataColumnInfo> pickUpInfoList) {
        for (int ii = 0; ii < colInfo.length; ii++) {
            SGDataColumnInfo col = colInfo[ii];
            String cType = col.getColumnType();
            if (SGDataUtility.isEqualColumnType(X_VALUE, cType)) {
                xInfoList.add(col);
            } else if (SGDataUtility.isEqualColumnType(Y_VALUE, cType)) {
                yInfoList.add(col);
            } else if (SGDataUtility.columnTypeStartsWith(cType, LOWER_ERROR_VALUE)) {
                Integer num = SGDataUtility.getColumnIndexOfAppendedColumnTitle(cType, colInfo);
                SGDataColumnInfo colAppend = colInfo[num.intValue()];
                leInfoMap.put(colAppend, col);
            } else if (SGDataUtility.columnTypeStartsWith(cType, UPPER_ERROR_VALUE)) {
                Integer num = SGDataUtility.getColumnIndexOfAppendedColumnTitle(cType, colInfo);
                SGDataColumnInfo colAppend = colInfo[num.intValue()];
                ueInfoMap.put(colAppend, col);
            } else if (SGDataUtility.columnTypeStartsWith(cType, LOWER_UPPER_ERROR_VALUE)) {
                Integer num = SGDataUtility.getColumnIndexOfAppendedColumnTitle(cType, colInfo);
                SGDataColumnInfo colAppend = colInfo[num.intValue()];
                leInfoMap.put(colAppend, col);
                ueInfoMap.put(colAppend, col);
            } else if (SGDataUtility.columnTypeStartsWith(cType, TICK_LABEL)) {
                Integer num = SGDataUtility.getColumnIndexOfAppendedColumnTitle(cType, colInfo);
                SGDataColumnInfo colAppend = colInfo[num.intValue()];
                tlInfoMap.put(colAppend, col);
            } else if (SGDataUtility.isEqualColumnType(ANIMATION_FRAME, cType)) {
                timeInfoList.add(col);
            } else if (SGDataUtility.isEqualColumnType(INDEX, cType)) {
                indexInfoList.add(col);
            } else if (SGDataUtility.isEqualColumnType(PICKUP, cType)) {
                pickUpInfoList.add(col);
            } else if ("".equals(cType)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Get name list of column type on netCDF data.
     * <p>
     * If column type in <b>colInfo</b> matches "X", add the variable name of <b>colInfo</b>
     * into <b>xNameList</b>. Same, check matching of "Y", "Lower Error", "Upper Error",
     * "Lower / Upper Error", "Tick Label", "Dimension", "Time" and "Index".
     *
     * @param colInfo
     * @param xInfoList contains variable names which have "X" column type.
     * @param yInfoList
     * @param leInfoList
     * @param ueInfoList
     * @param tlInfoList
     * @param pickupInfoList
     * @param timeInfoList
     * @param indexInfoList
     * @return
     */
	public static boolean getSXYDimensionDataColumnType(
			SGDataColumnInfo[] colInfo, List<SGDataColumnInfo> xInfoList,
			List<SGDataColumnInfo> yInfoList,
			List<SGDataColumnInfo> leInfoList,
			List<SGDataColumnInfo> ueInfoList,
			List<SGDataColumnInfo> tlInfoList,
			List<SGDataColumnInfo> pickupInfoList,
			List<SGDataColumnInfo> timeInfoList,
			List<SGDataColumnInfo> indexInfoList) {
        for (int ii = 0; ii < colInfo.length; ii++) {
        	SGDataColumnInfo col = colInfo[ii];
            String cType = col.getColumnType();
            if (SGDataUtility.isEqualColumnType(X_VALUE, cType)) {
                xInfoList.add(col);
            } else if (SGDataUtility.isEqualColumnType(Y_VALUE, cType)) {
                yInfoList.add(col);
            } else if (SGDataUtility.columnTypeStartsWith(cType, LOWER_ERROR_VALUE)) {
                leInfoList.add(col);
            } else if (SGDataUtility.columnTypeStartsWith(cType, UPPER_ERROR_VALUE)) {
                ueInfoList.add(col);
            } else if (SGDataUtility.columnTypeStartsWith(cType, LOWER_UPPER_ERROR_VALUE)) {
                leInfoList.add(col);
                ueInfoList.add(col);
            } else if (SGDataUtility.columnTypeStartsWith(cType, TICK_LABEL)) {
                tlInfoList.add(col);
            } else if (SGDataUtility.isEqualColumnType(PICKUP, cType)) {
                pickupInfoList.add(col);
            } else if (SGDataUtility.isEqualColumnType(ANIMATION_FRAME, cType)) {
                timeInfoList.add(col);
            } else if (SGDataUtility.isEqualColumnType(INDEX, cType)) {
                indexInfoList.add(col);
            } else if ("".equals(cType)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the complement button is to be visible.
     * <p>
     * If data type is Scalar-XY, and multiple graphs drawing and
     * multiple variables are selected, complement button is visible.
     *
     * @param infoMap
     *           the information map
     * @return true to set visible
     */
    public static boolean isComplementButtonVisible(final Map<String, Object> infoMap) {
        String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
        if (!isSXYTypeData(dataType)) {
            return false;
        }
        Boolean multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
        if (multiple == null) {
            return false;
        }
        final boolean compVisible;
        if (multiple.booleanValue()) {
            Boolean multipleVariable = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE);
            if (multipleVariable == null) {
                return false;
            }
            compVisible = multipleVariable.booleanValue();
        } else {
            compVisible = false;
        }
        return compVisible;
    }

    /**
     * Returns whether the complement button is to be enabled.
     * <p>
     * If data type is scalar-XY, and if number of "X" column type is 1 or
     * number of "Y" is 1, the complement button is enabled.
     *
     * @param infoMap the information map
     * @param curColType current column type
     * @param colInfoList list of column information
     * @return true to set visible
     */
    public static boolean isComplementedButtonEnabled(
            final Map<String, Object> infoMap,
            final String[] curColType, final List<SGDataColumnInfo> colInfoList) {
        String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
        if (isSXYTypeData(dataType)) {
            List<Integer> xIndexList = new ArrayList<Integer>();
            List<Integer> yIndexList = new ArrayList<Integer>();
            for (int ii = 0; ii < curColType.length; ii++) {
                if (X_VALUE.equals(curColType[ii])) {
                    xIndexList.add(Integer.valueOf(ii));
                } else if (Y_VALUE.equals(curColType[ii])) {
                    yIndexList.add(Integer.valueOf(ii));
                }
            }
            if (isSDArrayData(dataType)) {
                if (xIndexList.size() == 1 || yIndexList.size() == 1) {
                    return true;
                } else {
                    return false;
                }
            } else if (isNetCDFData(dataType)) {
                if (xIndexList.size() == 1) {
                    return isComplementedButtonEnabled(infoMap, curColType, colInfoList, xIndexList.get(0));
                } else if (yIndexList.size() == 1) {
                    return isComplementedButtonEnabled(infoMap, curColType, colInfoList, yIndexList.get(0));
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * If data is netCDF, single data selected must be a coordinate variable.
     */
    private static boolean isComplementedButtonEnabled(
            final Map<String, Object> infoMap, final String[] curColType,
            final List<SGDataColumnInfo> colInfoList, final Integer singleIndex) {
        SGNetCDFDataColumnInfo singleColInfo = (SGNetCDFDataColumnInfo) colInfoList.get(singleIndex);
        if (!singleColInfo.isCoordinateVariable()) {
            return false;
        }
        return true;
    }

    /**
     * Complements the column types.
     * <p>
     * If data type is not scalar-XY, return current column type.
     * If data type is scalar-XY, and if number of "X" column type is 1 and "Y" is 0,
     * set "Y" column type to columns which value types are Number (array data) or
     * which dimensions contain the dimension of "X" (netCDF data).
     * Same for number of "X" is 0 and "Y" is 1.
     * Otherwise return current column type.
     *
     * @param infoMap
     *            the information map
     * @param curColType
     *            the current column types
     * @param colInfoList
     *            the list of column information
     * @return complemented column types
     */
    public static String[] getComplementedColumnType(
            final Map<String, Object> infoMap,
            final String[] curColType, final List<SGDataColumnInfo> colInfoList) {
        if (curColType.length != colInfoList.size()) {
            return null;
        }
        String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
        String[] compColType = null;
        if (isSXYTypeData(dataType)) {
            List<Integer> xIndexList = new ArrayList<Integer>();
            List<Integer> yIndexList = new ArrayList<Integer>();
            for (int ii = 0; ii < curColType.length; ii++) {
                if (X_VALUE.equals(curColType[ii])) {
                    xIndexList.add(Integer.valueOf(ii));
                } else if (Y_VALUE.equals(curColType[ii])) {
                    yIndexList.add(Integer.valueOf(ii));
                }
            }
            if (xIndexList.size() == 1 && yIndexList.size() == 0) {
                compColType = getComplementedColumnType(infoMap, curColType,
                        colInfoList, xIndexList.get(0), X_VALUE, Y_VALUE);
            } else if (xIndexList.size() == 0 && yIndexList.size() == 1) {
                compColType = getComplementedColumnType(infoMap, curColType,
                        colInfoList, yIndexList.get(0), Y_VALUE, X_VALUE);
            } else {
                compColType = curColType.clone();
            }
        } else {
            compColType = curColType.clone();
        }
        return compColType;
    }

    private static String[] getComplementedColumnType(
            final Map<String, Object> infoMap,
            final String[] curColType, final List<SGDataColumnInfo> colInfoList,
            final Integer singleIndex, final String singleColumnType,
            final String multColumnType) {

        String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
        String[] compColType = new String[curColType.length];
        compColType[singleIndex.intValue()] = singleColumnType;
        if (isSDArrayData(dataType)) {
            for (int ii = 0; ii < colInfoList.size(); ii++) {
                if (ii == singleIndex.intValue()) {
                    continue;
                }
                SGDataColumnInfo colInfo = colInfoList.get(ii);
                String valueType = colInfo.getValueType();
                if (VALUE_TYPE_NUMBER.equals(valueType)) {
                    compColType[ii] = multColumnType;
                } else {
                    compColType[ii] = curColType[ii];
                }
            }
        } else if (isNetCDFData(dataType)) {
            SGNetCDFDataColumnInfo singleColInfo = (SGNetCDFDataColumnInfo) colInfoList.get(singleIndex);
            if (singleColInfo.isCoordinateVariable()) {
                SGDimensionInfo cDim = singleColInfo.getDimension(0);
                for (int ii = 0; ii < colInfoList.size(); ii++) {
                    if (ii == singleIndex.intValue()) {
                        continue;
                    }
                    SGNetCDFDataColumnInfo nColInfo = (SGNetCDFDataColumnInfo) colInfoList.get(ii);
                    if (nColInfo.isCoordinateVariable()) {
                        compColType[ii] = curColType[ii];
                    }
                    List<SGDimensionInfo> dimList = nColInfo.getDimensions();
                    if (dimList.contains(cDim)) {
                        compColType[ii] = multColumnType;
                    } else {
                        compColType[ii] = "";
                    }
                }
            } else {
                compColType = curColType.clone();
            }
        }

    	return compColType;
    }

    /**
     *
     * @param colInfo
     * @return true if colInfo has PICKUP column type.
     */
    public static boolean isPickupColumnContained(final SGDataColumnInfo[] colInfo) {
        for (int ii = 0; ii < colInfo.length; ii++) {
        	if (colInfo[ii] instanceof SGNetCDFDataColumnInfo) {
                if (PICKUP.equals(colInfo[ii].getColumnType())) {
                    return true;
                }
        	} else if (colInfo[ii] instanceof SGMDArrayDataColumnInfo) {
        		SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfo[ii];
        		Integer pickUpDimension = mdInfo.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
        		if (pickUpDimension != null && pickUpDimension != -1) {
        			return true;
        		}
        	}
        }
        return false;
    }

    public static SGNetCDFDataColumnInfo findHolderInfo(SGDataColumnInfo[] cols,
    		SGNetCDFDataColumnInfo info) {
		String holderName = getHolderName(info);
		return (SGNetCDFDataColumnInfo) SGDataUtility.findColumnWithName(cols, holderName);
    }

    public static String getHolderName(SGDataColumnInfo info) {
    	String columnType = info.getColumnType();
    	final int lastIndex = columnType.lastIndexOf(MID_COLUMN);
		String lastStr = columnType.substring(lastIndex + MID_COLUMN.length(), columnType.length());
		return lastStr;
    }

    /**
     *
     * @param nc
     * @param columns
     * @param xValueNames
     * @param yValueNames
     * @param leValueNames
     * @param ueValueNames
     * @param ehValueNames
     * @param tlValueNames
     * @param thValueNames
     */
    public static void updateColumnTypeOfSXYMultipleVariableNetCDFDataFromVariableNames(
            final SGNetCDFFile nc, final SGDataColumnInfo[] columns,
            final String[] xValueNames, final String[] yValueNames,
            final String[] leValueNames, final String[] ueValueNames, final String[] ehValueNames,
            final String[] tlValueNames, final String[] thValueNames) {
        for (int i = 0; i < xValueNames.length; i++) {
            int xVarIndex = nc.getVariableIndex(xValueNames[i]);
            columns[xVarIndex].setColumnType(SGIDataColumnTypeConstants.X_VALUE);
        }
        for (int i = 0; i < yValueNames.length; i++) {
            int yVarIndex = nc.getVariableIndex(yValueNames[i]);
            columns[yVarIndex].setColumnType(SGIDataColumnTypeConstants.Y_VALUE);
        }

        boolean errorbarFlag = false;
        boolean ticklabelFlag = false;
        if (leValueNames!=null && leValueNames.length>0 &&
                ueValueNames!=null && ueValueNames.length>0 &&
                ehValueNames!=null && ehValueNames.length>0 &&
                leValueNames.length==ueValueNames.length &&
                leValueNames.length==ehValueNames.length) {
            errorbarFlag = true;
        }
        if (tlValueNames!=null && tlValueNames.length>0 &&
                thValueNames!=null && thValueNames.length>0 &&
                tlValueNames.length==thValueNames.length) {
            ticklabelFlag = true;
        }
        if (errorbarFlag) {
            for (int i = 0; i < leValueNames.length; i++) {
                int leVarIndex = nc.getVariableIndex(leValueNames[i]);
                int ueVarIndex = nc.getVariableIndex(ueValueNames[i]);
                if (leVarIndex!=ueVarIndex) {
                    columns[leVarIndex].setColumnType(SGIDataColumnTypeConstants.LOWER_ERROR_VALUE+" for "+ehValueNames[i]);
                    columns[ueVarIndex].setColumnType(SGIDataColumnTypeConstants.UPPER_ERROR_VALUE+" for "+ehValueNames[i]);
                } else {
                    columns[leVarIndex].setColumnType(SGIDataColumnTypeConstants.LOWER_UPPER_ERROR_VALUE+" for "+ehValueNames[i]);
                }
            }
        }
        if (ticklabelFlag) {
            for (int i = 0; i < tlValueNames.length; i++) {
                int tlVarIndex = nc.getVariableIndex(tlValueNames[i]);
                columns[tlVarIndex].setColumnType(SGIDataColumnTypeConstants.TICK_LABEL+" for "+thValueNames[i]);
            }
        }
    }

    /**
     * Return true if name1 and name2 belongs to a same netCDF group.
     * @param name1
     * @param name2
     * @return
     */
    public static boolean isSameNetCDFGroup(final String name1, final String name2) {
        String[] str1 = name1.split("/");
        String[] str2 = name2.split("/");
        if (str1.length==str2.length) {
            for (int i = 0; i < str1.length-1; i++) {
                if (str1[i].equals(str2[i])==false) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static SGNetCDFDataColumnInfo getDimensionDataColumnInfo(final SGNetCDFDataColumnInfo[] nCols) {
    	SGNetCDFDataColumnInfo info = null;
        for (int i = 0; i < nCols.length; i++) {
            String colType = nCols[i].getColumnType();
            if (PICKUP.equalsIgnoreCase(colType)) {
                info = nCols[i];
                break;
            }
        }
        if (info != null && info.isCoordinateVariable()) {
            return info;
        } else {
            return null;
        }
    }

    /**
     * Updates pick up parameters for netCDF data.
     *
     * @param infoMap
     *           information map
     * @param nCols
     *           an array of columns
     * @return true if succeeded
     */
    public static boolean updatePickupParameters(
            final Map<String, Object> infoMap, SGNetCDFDataColumnInfo[] nCols) {
    	SGIntegerSeriesSet indices = new SGIntegerSeriesSet();
    	SGNetCDFDataColumnInfo col = getDimensionDataColumnInfo(nCols);
    	final SGIntegerSeries series;
    	if (col != null) {
    		SGDimensionInfo dim = col.getDimension(0);
            final int len = dim.getLength();
    		series = createDefaultStepSeries(len);
    	} else {
    		series = new SGIntegerSeries(0);
    	}
		indices.add(series);
		infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, indices);
        return true;
    }

	// Finds picked up column.
    public static SGMDArrayDataColumnInfo getMDArrayPickUpColumn(SGDataColumnInfo[] cols) {
		boolean valid = true;
		SGMDArrayDataColumnInfo pickUpCol = null;
		int cnt = 0;
		for (int ii = 0; ii < cols.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
			Integer dim = mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
			if (dim != null && dim != -1) {
				pickUpCol = mdCol;
				cnt++;
			}
		}
		if (cnt > 1) {
			valid = false;
		}
    	if (!valid) {
    		return null;
    	}
    	return pickUpCol;
    }

    public static boolean updatePickupParameters(
            final Map<String, Object> infoMap, SGMDArrayDataColumnInfo[] mdCols) {
    	SGIntegerSeriesSet indices = new SGIntegerSeriesSet();
    	SGMDArrayDataColumnInfo col = getMDArrayPickUpColumn(mdCols);
    	final SGIntegerSeries series;
    	if (col != null) {
    		int[] dims = col.getDimensions();
    		Integer pickUpDim = col.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
    		if (pickUpDim == null || pickUpDim == -1) {
    			return false;
    		}
            final int len = dims[pickUpDim];
    		series = createDefaultStepSeries(len);
    	} else {
    		series = new SGIntegerSeries(0);
    	}
		indices.add(series);
		infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, indices);
        return true;
    }

    public static SGIntegerSeries createDefaultStepSeries(final int len) {
		final int nEnd = len - 1;
		final int nStep;
    	if (len <= DEFAULT_MULTIPLE_DIMENSION_NUM) {
    		nStep = 1;
    	} else {
            nStep = len / DEFAULT_MULTIPLE_DIMENSION_NUM;
    	}
		SGIntegerSeries series = new SGIntegerSeries(0, nEnd, nStep);
		return series;
    }

    /**
     * Return whether given netCDF variable is SGDateVariable.
     * <p>
     * True if data type of the variable is CHAR and it has an attribute "value_type=Date".
     *
     * @param var
     * @return true if a given variable is SGDateVariable.
     */
    public static boolean isSGDateVariable(final Variable var) {
        if (DataType.CHAR.equals(var.getDataType())) {
            List<Attribute> attrList = var.getAttributes();
            for (Attribute attr : attrList) {
                if (attr.isString()) {
                    String name = attr.getName();
                    String value = attr.getStringValue();
                    if (ATTRIBUTE_VALUE_TYPE.equals(name.trim()) &&
                        SGIDataColumnTypeConstants.VALUE_TYPE_DATE.equals(value.trim())) {
                            return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Return whether given netCDF variable is SGTextVariable.
     * <p>
     * True if data type of the variable is CHAR and it has an attribute "value_type=Text".
     *
     * @param var
     * @return true if var is SGTextVariable.
     */
    public static boolean isSGTextVariable(final Variable var) {
        if (DataType.CHAR.equals(var.getDataType())) {
            List<Attribute> attrList = var.getAttributes();
            for (Attribute attr : attrList) {
                if (attr.isString()) {
                    String name = attr.getName();
                    String value = attr.getStringValue();
                    if (name!=null && value!=null &&
                            ATTRIBUTE_VALUE_TYPE.equals(name.trim()) &&
                            SGIDataColumnTypeConstants.VALUE_TYPE_TEXT.equals(value.trim())) {
                            return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Return netCDF attribute for given valueType.
     *
     * @param valueType
     * @return
     */
    public static Attribute getValueTypeAttribute(final String valueType) {
        Attribute attr = new Attribute(ATTRIBUTE_VALUE_TYPE, valueType);
        return attr;
    }

    /**
     * Return whether given netCDF data can connect with its file.
     *
     * @param data
     * @return
     */
    public static boolean canOpenNetCDF(SGNetCDFData data) {
        try {
            boolean isNetCDFFile = NetcdfFile.canOpen(data.getNetcdfFile().getNetcdfFile().getLocation());
            return isNetCDFFile;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Dump map for debug.
     *
     * @param infoMap
     */
    public static void dumpInfoMap(final Map<String, Object> infoMap) {
        for (String key : infoMap.keySet()) {
            if (null==infoMap.get(key)) {
                System.out.println("   key="+key+" : null");
            } else {
                if (key.equals(SGIDataInformationKeyConstants.KEY_DATA_SOURCE)) {
                    System.out.println("   key="+key+" : "+infoMap.get(key).getClass().getSimpleName());
                } else {
                    System.out.println("   key="+key+" : "+infoMap.get(key).toString());
                }
            }
        }
    }

    /**
     * Returns the value array for given date objects.
     *
     * @param dArray
     *            an array of date objects
     * @return the values for given date objects
     */
    public static double[] getDateValueArray(SGDate[] dArray) {
        final double[] values = new double[dArray.length];
        for (int ii = 0; ii < values.length; ii++) {
            values[ii] = dArray[ii].getDateValue();
        }
        return values;
    }

	public static SGNetCDFDataColumnInfo[] getNetCDFDataColumnInfo(
			List<SGNetCDFVariable> varList, Map<String, Object> infoMap) {

		// get the origin map
		NamedNodeMap nodeMap = (NamedNodeMap) infoMap
				.get(SGIFigureElementGraph.KEY_NODE_MAP);
		Map<String, Integer> originMap = null;
		if (nodeMap != null) {
			originMap = SGDefaultColumnTypeUtility.getNetCDFOriginMap(nodeMap);
			if (originMap == null) {
				return null;
			}
		}

		// get the group name
		String groupName = (String) infoMap.get(SGIFigureElementGraph.KEY_GROUP_NAME);
		if (groupName != null) {
			// replaces the origin map
			Map<String, Integer> tempMap = new HashMap<String, Integer>();
			Set<Entry<String, Integer>> entrySet = originMap.entrySet();
			for (Entry<String, Integer> e : entrySet) {
				String varName = e.getKey();
				Integer value = e.getValue();
				String key = SGDataUtility.appendGroupName(varName, groupName);
				tempMap.put(key, value);
			}
			originMap = tempMap;
		}

		// get column information: title and value type
		SGNetCDFDataColumnInfo[] colArray = new SGNetCDFDataColumnInfo[varList.size()];
		for (int ii = 0; ii < colArray.length; ii++) {
			SGNetCDFVariable var = varList.get(ii);

			// get the origin
			int origin = 0;
			if (originMap != null) {
				if (var.isCoordinateVariable()) {
					String varName = var.getName();
					Integer num = originMap.get(varName);
					if (num != null) {
						Dimension dim = var.getDimension(0);
						origin = num.intValue();
						if (origin < 0 || origin >= dim.getLength()) {
							origin = 0;
						}
					}
				}
			}

			colArray[ii] = new SGNetCDFDataColumnInfo(var,
					var.getName(), var.getValueType(), origin);
		}

		return colArray;
	}

	static String appendGroupName(final String name, final String groupName) {
		StringBuffer sb = new StringBuffer();
		sb.append(groupName);
		sb.append('/');
		sb.append(name);
		return sb.toString();
	}

	public static SGMDArrayDataColumnInfo[] getMDArrayDataColumnInfo(
			SGMDArrayFile mdFile, SGMDArrayVariable[] vars, Map<String, Object> infoMap) {

		// get the origin map
		NamedNodeMap nodeMap = (NamedNodeMap) infoMap
				.get(SGIFigureElementGraph.KEY_NODE_MAP);
		Map<String, int[]> originMap = null;
		if (nodeMap != null) {
			originMap = SGDefaultColumnTypeUtility.getMDArrayDataOriginMap(nodeMap);
			if (originMap == null) {
				return null;
			}
		}

		// get the group name
		String groupName = (String) infoMap.get(SGIFigureElementGraph.KEY_GROUP_NAME);
		if (groupName != null) {
			// replaces the origin map
			Map<String, int[]> tempMap = new HashMap<String, int[]>();
			Set<Entry<String, int[]>> entrySet = originMap.entrySet();
			for (Entry<String, int[]> e : entrySet) {
				String varName = e.getKey();
				int[] value = e.getValue();
				String key = SGDataUtility.appendGroupName(varName, groupName);
				tempMap.put(key, value);
			}
			originMap = tempMap;
		}

		// get column information: title and value type
		SGMDArrayDataColumnInfo[] colArray = new SGMDArrayDataColumnInfo[vars.length];
		for (int ii = 0; ii < colArray.length; ii++) {
			SGMDArrayVariable var = vars[ii];
			if (originMap != null) {
				int[] origins = originMap.get(var.getName());
				if (origins != null) {
					var.setOrigins(origins);
				}
			}
			colArray[ii] = new SGMDArrayDataColumnInfo(var, var.getName(), var.getValueType());
		}

		/*
		Object pickUpDimensionName = infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DATASET_NAME);
		if (pickUpDimensionName != null) {
			SGMDArrayDataColumnInfo pickUpCol = (SGMDArrayDataColumnInfo) SGDataUtility.findColumnWithName(
					colArray, pickUpDimensionName.toString());
			if (pickUpCol != null) {
				@SuppressWarnings("unchecked")
				Map<String, Integer> dimensionIndexMap = (Map<String, Integer>) infoMap.get(
						SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP);
				for (int ii = 0; ii < colArray.length; ii++) {
					SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) colArray[ii];
					Integer dimensionIndex = dimensionIndexMap.get(mdCol.getName());
					if (dimensionIndex != null) {
						final int[] dims = mdCol.getDimensions();
						final int generic = mdCol.getGenericDimensionIndex();
						if (0 < dimensionIndex && dimensionIndex < dims.length
								&& !dimensionIndex.equals(generic)) {
							mdCol.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION,
									dimensionIndex);
						}
					}
				}
			}
		}
		*/

		@SuppressWarnings("unchecked")
		Map<String, Integer> timeDimensionMap = (Map<String, Integer>) infoMap.get(
				SGIDataInformationKeyConstants.KEY_TIME_DIMENSION_INDEX_MAP);
		@SuppressWarnings("unchecked")
		Map<String, Integer> pickupDimensionMap = (Map<String, Integer>) infoMap.get(
				SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP);
		for (int ii = 0; ii < colArray.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) colArray[ii];
			final int generic = mdCol.getGenericDimensionIndex();
			final int[] dims = mdCol.getDimensions();
			boolean tValid = false;
			Integer timeDimension = null;
			if (timeDimensionMap != null) {
				timeDimension = timeDimensionMap.get(mdCol.getName());
				tValid = (timeDimension != null && timeDimension != -1);
				if (tValid) {
					if (timeDimension < 0 || timeDimension >= dims.length) {
						return null;
					}
					if (timeDimension.equals(generic)) {
						return null;
					}
					mdCol.setDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION,
							timeDimension);
				}
			}
			if (pickupDimensionMap != null) {
				Integer pickupDimension = pickupDimensionMap.get(mdCol.getName());
				final boolean pValid = (pickupDimension != null && pickupDimension != -1);
				if (pValid) {
					if (pickupDimension < 0 || pickupDimension >= dims.length) {
						return null;
					}
					if (pickupDimension.equals(generic)) {
						return null;
					}
					if (tValid) {
						if (pickupDimension.equals(timeDimension)) {
							return null;
						}
					}
					mdCol.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION,
							pickupDimension);
				}
			}
		}

		return colArray;
	}

    /**
     * Finds and returns the list of attributes.
     *
     * @param reader
     *           reader of HDF5
     * @param path
     *           the path
     * @param attrNameList
     *           the list of the name of attributes
     * @return the list of attributes
     */
	public static List<SGAttribute> findHDF5Attributes(final IHDF5Reader reader,
			final String path, final List<String> attrNameList) {
		List<SGAttribute> aList = new ArrayList<SGAttribute>();
		for (String name : attrNameList) {
			HDF5DataTypeInformation info = reader.getAttributeInformation(path,
					name);
			HDF5DataClass dClass = info.getDataClass();
			final boolean arrayType = (info.getNumberOfElements() > 1);
			Object obj = null;
			if (HDF5DataClass.STRING.equals(dClass)) {
				if (arrayType) {
					obj = reader.getStringArrayAttribute(path, name);
				} else {
					obj = reader.getStringAttribute(path, name);
				}
			} else if (HDF5DataClass.INTEGER.equals(dClass)) {
				if (arrayType) {
					obj = reader.getIntArrayAttribute(path, name);
				} else {
					obj = reader.getIntAttribute(path, name);
				}
			} else if (HDF5DataClass.FLOAT.equals(dClass)) {
				if (arrayType) {
					obj = reader.getFloatArrayAttribute(path, name);
				} else {
					obj = reader.getFloatAttribute(path, name);
				}
			} else if (HDF5DataClass.BOOLEAN.equals(dClass)) {
				obj = reader.getBooleanAttribute(path, name);
			} else if (HDF5DataClass.ENUM.equals(dClass)) {
				obj = reader.getEnumAttribute(path, name);
			}
			SGAttribute attr = new SGAttribute(name, obj);
			aList.add(attr);
		}
		return aList;
	}
	
	/**
	 * Writes attributes to the writer.
	 * 
	 * @param writer
	 *           the writer
	 * @param reader
	 *           the reader
	 * @param path
	 *           the path
	 * @param attrNameList
     *           the list of the name of attributes
	 * @return true if succeeded
	 */
	public static boolean writeHDF5Attribute(final IHDF5Writer writer, 
			final IHDF5Reader reader, final String path, final List<String> attrNameList) {
		for (String attrName : attrNameList) {
			HDF5DataTypeInformation attrInfo = reader.getAttributeInformation(path, attrName);
			final boolean arrayType = (attrInfo.getNumberOfElements() > 1);
			HDF5DataClass attrDataClass = attrInfo.getDataClass();
    		if (HDF5DataClass.STRING.equals(attrDataClass)) {
    			if (arrayType) {
        			final String[] value = reader.getStringArrayAttribute(path, attrName);
        			writer.setStringArrayAttribute(path, attrName, value);
    			} else {
        			final String value = reader.getStringAttribute(path, attrName);
        			writer.setStringAttribute(path, attrName, value);
    			}
    		} else if (HDF5DataClass.INTEGER.equals(attrDataClass)) {
    			if (arrayType) {
        			final int[] value = reader.getIntArrayAttribute(path, attrName);
        			writer.setIntArrayAttribute(path, attrName, value);
    			} else {
        			final int value = reader.getIntAttribute(path, attrName);
        			writer.setIntAttribute(path, attrName, value);
    			}
    		} else if (HDF5DataClass.FLOAT.equals(attrDataClass)) {
    			if (arrayType) {
    				final float[] value = reader.getFloatArrayAttribute(path, attrName);
    				writer.setFloatArrayAttribute(path, attrName, value);
    			} else {
        			final float value = reader.getFloatAttribute(path, attrName);
        			writer.setFloatAttribute(path, attrName, value);
    			}
			} else if (HDF5DataClass.BOOLEAN.equals(attrDataClass)) {
				final boolean value = reader.getBooleanAttribute(path, attrName);
				writer.setBooleanAttribute(path, attrName, value);
			} else if (HDF5DataClass.ENUM.equals(attrDataClass)) {
				HDF5EnumerationValue value = reader.getEnumAttribute(path, attrName);
				writer.setEnumAttribute(path, attrName, value);
    		}
		}
		return true;
	}

    /**
     * Calculates the series of dimension steps.
     *
     * @param dataList
     *           a list of data
     * @return the indices of dimension
     */
    public static SGIntegerSeriesSet getDimensionSeries(List<SGData> dataList, final int len) {

		Set<Integer> indexSet = new TreeSet<Integer>();
        for (int ii = 0; ii < dataList.size(); ii++) {
        	SGData data = dataList.get(ii);
        	if (!(data instanceof SGISXYMultipleDimensionData)) {
        		continue;
        	}
            SGISXYMultipleDimensionData dataMult = (SGISXYMultipleDimensionData) data;
            SGIntegerSeriesSet indices = dataMult.getIndices();
            int[] indexArray = indices.getNumbers();
            for (int jj = 0; jj < indexArray.length; jj++) {
            	indexSet.add(indexArray[jj]);
            }
        }

        SGIntegerSeriesSet ret = new SGIntegerSeriesSet();
        int[] indices = new int[indexSet.size()];
        Iterator<Integer> itr = indexSet.iterator();
        int cnt = 0;
        while (itr.hasNext()) {
        	Integer index = itr.next();
        	indices[cnt] = index;
        	cnt++;
        }
        List<SGIntegerSeries> seriesList = SGIntegerSeries.createList(indices);
        for (SGIntegerSeries series : seriesList) {
        	if (len > 0) {
        		SGInteger start = series.getStart();
        		SGInteger end = series.getEnd();
        		SGInteger step = series.getStep();
        		Integer nStart = start.getNumber();
        		Integer nEnd = end.getNumber();
        		Integer nStep = step.getNumber();
        		if (nStart != null && SGUtility.equals(nStart, len - 1)) {
        			start = new SGInteger(nStart, SGIntegerSeries.ARRAY_INDEX_END);
        		}
        		if (nEnd != null && SGUtility.equals(nEnd, len - 1)) {
        			end = new SGInteger(nEnd, SGIntegerSeries.ARRAY_INDEX_END);
        		}
        		if (nStep != null && SGUtility.equals(nStep, len - 1)) {
        			step = new SGInteger(nStep, SGIntegerSeries.ARRAY_INDEX_END);
        		}
        		series = new SGIntegerSeries(start, end, step);
        	}
        	ret.add(series);
        }

        return ret;
    }

    /**
     * Returns the column type for the first component.
     *
     * @param infoMap
     *           the information map
     * @return the column type for the first component
     */
    public static String getVXYFirstComponentColumnType(Map<String, Object> infoMap) {
    	Boolean polar = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED);
    	if (polar == null) {
    		return null;
    	}
    	return getVXYFirstComponentColumnType(polar.booleanValue());
    }

    /**
     * Returns the column type for the first component.
     *
     * @param polar
     *           true for the polar type data
     * @return the column type for the first component
     */
    public static String getVXYFirstComponentColumnType(final boolean polar) {
    	return polar ? MAGNITUDE : X_COMPONENT;
    }

    /**
     * Returns the column type for the second component.
     *
     * @param infoMap
     *           the information map
     * @return the column type for the second component
     */
    public static String getVXYSecondComponentColumnType(Map<String, Object> infoMap) {
    	Boolean polar = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED);
    	if (polar == null) {
    		return null;
    	}
    	return getVXYSecondComponentColumnType(polar.booleanValue());
    }


    /**
     * Returns the column type for the second component.
     *
     * @param polar
     *           true for the polar type data
     * @return the column type for the second component
     */
    public static String getVXYSecondComponentColumnType(final boolean polar) {
    	return polar ? ANGLE : Y_COMPONENT;
    }

    public static Map<String, SGIntegerSeriesSet> calcNetCDFDefaultStride(
    		SGDataColumnInfo[] colArray, Map<String, Object> infoMap) {
    	return calcNetCDFDefaultStride(colArray, infoMap, new HashMap<String, String>());
    }

    public static Map<String, SGIntegerSeriesSet> calcMDArrayDefaultStride(
    		SGDataColumnInfo[] colArray, Map<String, Object> infoMap) {
    	return calcMDArrayDefaultStride(colArray, infoMap, new HashMap<String, SGMDArrayDimensionInfo>());
    }

    /**
     * Calculates default stride of data arrays.
     *
     * @param colArray
     *           an array of data information
     * @param infoMap
     *           the information map
     * @param dimNameMap
     *           the map of dimension name (output)
     * @return map of the stride of data arrays
     */
    public static Map<String, SGIntegerSeriesSet> calcNetCDFDefaultStride(
    		SGDataColumnInfo[] colArray, Map<String, Object> infoMap,
    		Map<String, String> dimNameMap) {

    	SGTuple2f figureSize = (SGTuple2f) infoMap.get(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE);
    	String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
    	Map<String, SGIntegerSeriesSet> map = null;
    	final boolean hasIndex = hasIndexColumnType(colArray);
		final boolean bx = (figureSize.x > figureSize.y);
		List<String> cTypeList = new ArrayList<String>();

		if (SGDataUtility.isSXYTypeData(dataType)) {
			if (hasIndex) {
				cTypeList.add(INDEX);
				map = calcNetCDFStride(colArray, dataType, figureSize, cTypeList,
						SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE, bx, dimNameMap);
			} else {
				cTypeList.add(X_VALUE);
				Map<String, SGIntegerSeriesSet> xMap = calcNetCDFStride(colArray,
						dataType, figureSize, cTypeList, SGIDataInformationKeyConstants.KEY_SXY_STRIDE, true, dimNameMap);
				cTypeList.clear();
				cTypeList.add(Y_VALUE);
				Map<String, SGIntegerSeriesSet> yMap = calcNetCDFStride(colArray,
						dataType, figureSize, cTypeList, SGIDataInformationKeyConstants.KEY_SXY_STRIDE, false, dimNameMap);
				if ((xMap != null && yMap != null) || (xMap == null && yMap == null)) {
					return null;
				}
				if (xMap != null) {
					map = xMap;
				} else {
					map = yMap;
				}
//				SGIntegerSeriesSet tickLabelStride = map.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
//				map.put(SGIDataInformationKeyConstants.KEY_SXY_STRIDE, (SGIntegerSeriesSet) tickLabelStride.clone());
			}

		} else if (SGDataUtility.isSXYZTypeData(dataType)) {
			if (hasIndex) {
				cTypeList.add(INDEX);
				map = calcNetCDFStride(colArray, dataType, figureSize, cTypeList,
						SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE, bx, dimNameMap);
			} else {
				cTypeList.add(X_VALUE);
				cTypeList.add(X_INDEX);
				map = new HashMap<String, SGIntegerSeriesSet>();
				Map<String, SGIntegerSeriesSet> xMap = calcNetCDFStride(colArray,
						dataType, figureSize, cTypeList, SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X, true, dimNameMap);
				if (xMap != null) {
					map.putAll(xMap);
				}
				cTypeList.clear();
				cTypeList.add(Y_VALUE);
				cTypeList.add(Y_INDEX);
				Map<String, SGIntegerSeriesSet> yMap = calcNetCDFStride(colArray,
						dataType, figureSize, cTypeList, SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y, false, dimNameMap);
				if (yMap != null) {
					map.putAll(yMap);
				}
			}

		} else if (SGDataUtility.isVXYTypeData(dataType)) {
			if (hasIndex) {
				cTypeList.add(INDEX);
				map = calcNetCDFStride(colArray, dataType, figureSize, cTypeList,
						SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE, bx, dimNameMap);
			} else {
				List<String> cTypeXList = new ArrayList<String>();
				cTypeXList.add(X_COORDINATE);
				cTypeXList.add(X_INDEX);
				List<String> cTypeYList = new ArrayList<String>();
				cTypeYList.add(Y_COORDINATE);
				cTypeYList.add(Y_INDEX);
				map = calcVectorStride(colArray, infoMap, dimNameMap, dataType,
						figureSize, cTypeXList, SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X, cTypeYList,
						SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y);
			}
		}

		return map;
    }
    
    public static Map<String, SGIntegerSeriesSet> calcSDArrayDefaultStride(SGDataColumnInfo[] colArray, 
    		Map<String, Object> infoMap) {
    	SGTuple2f figureSize = (SGTuple2f) infoMap.get(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE);
		final int fSize = (int) (figureSize.x > figureSize.y ? figureSize.x : figureSize.y);
		SGSDArrayDataColumnInfo sdInfo = (SGSDArrayDataColumnInfo) colArray[0];
		final int len = sdInfo.getLength();
		SGIntegerSeriesSet stride = calcStride(len, fSize);
    	String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
    	Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
		if (SGDataUtility.isSXYTypeData(dataType)) {
			map.put(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE, stride);
		} else if (SGDataUtility.isSXYZTypeData(dataType)) {
			map.put(SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE, stride);
		} else if (SGDataUtility.isVXYTypeData(dataType)) {
			map.put(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE, stride);
		}
		return map;
    }

    private static Map<String, SGIntegerSeriesSet> calcNetCDFStride(
    		SGDataColumnInfo[] colArray, String dataType, SGTuple2f figureSize,
    		List<String> cTypeList, String key, final boolean bx, Map<String, String> dimNameMap) {
		final int fSize = (int) (bx ? figureSize.x : figureSize.y);
		return calcNetCDFStride(colArray, dataType, fSize, cTypeList, key, dimNameMap);
    }

    private static Map<String, SGIntegerSeriesSet> calcNetCDFStride(
    		SGDataColumnInfo[] colArray, String dataType, final int fSize,
    		List<String> cTypeList, String key, Map<String, String> dimNameMap) {

		int len = -1;
		int cnt = 0;
		String dimName = null;
		for (int ii = 0; ii < colArray.length; ii++) {
			SGNetCDFDataColumnInfo nCol = (SGNetCDFDataColumnInfo) colArray[ii];
			String colType = nCol.getColumnType();

	    	// Only for backward compatibility <= 2.0.0
	    	if (SERIAL_NUMBERS.equalsIgnoreCase(colType)) {
	    		colType = INDEX;
	    	}

			if (cTypeList.contains(colType)) {
				if (nCol.isCoordinateVariable()) {
					len = nCol.getDimension(0).getLength();
					dimName = nCol.getName();
					cnt++;
					if (cnt > 1) {
						break;
					}
				}
			}
		}
		if (cnt != 1) {
			return null;
		}

		// creates an instance of stride
		SGIntegerSeriesSet stride = calcStride(len, fSize);

		// creates returned value
    	Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
		map.put(key, stride);

		// set the output
		dimNameMap.put(key, dimName);

		return map;
    }

    private static SGIntegerSeriesSet calcStride(final int len, final int fSize) {
		final int end = len - 1;
    	int step = len / fSize;
		if (len % fSize != 0) {
			step += + 1;
		}
		return new SGIntegerSeriesSet(0, end, step);
    }

    private static Map<String, SGIntegerSeriesSet> calcVectorStride(
    		SGDataColumnInfo[] colArray, Map<String, Object> infoMap, Map<String, String> dimNameMap,
    		String dataType, SGTuple2f figureSize, List<String> cTypeXList, String xKey, List<String> cTypeYList, String yKey) {

    	SGNetCDFDataColumnInfo xCol = null;
    	SGNetCDFDataColumnInfo yCol = null;
		int xCnt = 0;
		int yCnt = 0;
		for (int ii = 0; ii < colArray.length; ii++) {
			SGNetCDFDataColumnInfo nCol = (SGNetCDFDataColumnInfo) colArray[ii];
			String colType = nCol.getColumnType();
			if (cTypeXList.contains(colType)) {
				if (nCol.isCoordinateVariable()) {
					xCol = nCol;
					xCnt++;
					if (xCnt > 1) {
						break;
					}
				}
			} else if (cTypeYList.contains(colType)) {
				if (nCol.isCoordinateVariable()) {
					yCol = nCol;
					yCnt++;
					if (yCnt > 1) {
						break;
					}
				}
			}
		}
		if (xCnt != 1 || yCnt != 1) {
			return null;
		}

		SGDimensionInfo xDim = xCol.getDimension(0);
		SGDimensionInfo yDim = yCol.getDimension(0);
		final int xLen = xDim.getLength();
		final int yLen = yDim.getLength();

		// calculates the step of stride
		final int xStep = calcVectorStep(figureSize, true, xLen);
		final int yStep = calcVectorStep(figureSize, false, yLen);

		// creates returned value
		final int xEnd = xLen - 1;
		final int yEnd = yLen - 1;
    	Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
		map.put(xKey, new SGIntegerSeriesSet(0, xEnd, xStep));
		map.put(yKey, new SGIntegerSeriesSet(0, yEnd, yStep));
    	
		// updates the output
		dimNameMap.put(xKey, xDim.getName());
		dimNameMap.put(yKey, yDim.getName());

		return map;
    }

    private static int calcVectorStep(SGTuple2f figureSize, final boolean bx, final int len) {
    	int step;
		if (figureSize != null) {
			final float fSizePt = bx ? figureSize.x : figureSize.y;
			final float fSizeCm = fSizePt * SGIConstants.CM_POINT_RATIO;
			final int fSize = (int) fSizeCm;
			step = len / fSize;
			if (len % fSize != 0) {
				step += + 1;
			}
		} else {
			step = 1;
		}
		return step;
    }

    private static final int MAGNITUDE_PER_CM_MINIMAL_ORDER = 3;

    private static float roundMagnitudePerCM(final float mag, final double max) {
        float magNew = (float) SGUtilityNumber.getNumberInNumberOrder(mag, max,
                MAGNITUDE_PER_CM_MINIMAL_ORDER, BigDecimal.ROUND_HALF_UP);
        return magNew;
    }

    /**
     * Rounds the input value of the magnitude per centimeter of vector type data
     * using the maximum length of vectors.
     *
     * @param magpercm
     *           the magnitude per centimeter of vector type data
     * @param data
     *           the vector type data
     * @return rounded value
     */
    public static float roundMagnitudePerCM(final float magpercm,
            final SGIVXYTypeData data) {
    	if (Float.isNaN(magpercm)) {
    		return Float.NaN;
    	}
        final double[] mArray = data.getMagnitudeArray(false);
        final double max = SGUtilityNumber.max(mArray);
        if (Double.isNaN(max) || max <= 0.0) {
        	return magpercm;
        }
        final float magpercmReduced = roundMagnitudePerCM(magpercm, max);
        return magpercmReduced;
    }

    /**
     * Calculates the initial magnitude of the vector per centimeter.
     *
     * @param data
     *           the vector data
     * @return the initial magnitude of the vector per centimeter
     */
    public static float getInitialMagnitudePerCM(SGIVXYTypeData data) {
        final double[] mArray = data.getMagnitudeArray(false);
        final double max = SGUtilityNumber.max(mArray);
        if (Double.isNaN(max) || max <= 0.0) {
            // Returns the default magnitude of a vector per centimeter.
        	return 1.0f;
        }
        final float magpercm = (float) max;
        final float magpercmReduced = roundMagnitudePerCM(magpercm, max);
        return magpercmReduced;
    }

    /**
     * Calculates default stride of data arrays.
     *
     * @param colArray
     *           an array of data information
     * @param infoMap
     *           the information map
     * @param dimNameMap
     *           the map of dimension name (output)
     * @return map of the stride of data arrays
     */
    public static Map<String, SGIntegerSeriesSet> calcMDArrayDefaultStride(
    		SGDataColumnInfo[] colArray, Map<String, Object> infoMap,
    		Map<String, SGMDArrayDimensionInfo> dimNameMap) {

    	SGTuple2f figureSize = (SGTuple2f) infoMap.get(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE);
    	String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
    	Map<String, SGIntegerSeriesSet> map = null;

		if (SGDataUtility.isSXYTypeData(dataType)) {
			Map<String, SGIntegerSeriesSet> xMap = calcMDArrayStride(colArray,
					dataType, figureSize, X_VALUE, SGIDataInformationKeyConstants.KEY_SXY_STRIDE, true, dimNameMap);
			Map<String, SGIntegerSeriesSet> yMap = calcMDArrayStride(colArray,
					dataType, figureSize, Y_VALUE, SGIDataInformationKeyConstants.KEY_SXY_STRIDE, false, dimNameMap);
			if (xMap == null && yMap == null) {
				return null;
			} else if (xMap != null && yMap != null) {
				map = xMap;
			} else {
				if (xMap != null) {
					map = xMap;
				} else {
					map = yMap;
				}
			}
//			SGIntegerSeriesSet tickLabelStride = map.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
//			map.put(SGIDataInformationKeyConstants.KEY_SXY_STRIDE, (SGIntegerSeriesSet) tickLabelStride.clone());

		} else if (SGDataUtility.isSXYZTypeData(dataType)) {
			map = new HashMap<String, SGIntegerSeriesSet>();
			Boolean grid = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG);
			if (grid) {
				Map<String, SGIntegerSeriesSet> xMap = calcMDArrayStride(colArray,
						dataType, figureSize, X_VALUE,
						SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X,
						true, dimNameMap);
				if (xMap != null) {
					map.putAll(xMap);
				} else {
					xMap = calcMDArrayStride(colArray, dataType, figureSize, Z_VALUE,
							SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION,
							SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X,
							true, dimNameMap);
					if (xMap != null) {
						map.putAll(xMap);
					}
				}
				Map<String, SGIntegerSeriesSet> yMap = calcMDArrayStride(colArray,
						dataType, figureSize, Y_VALUE,
						SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y,
						false, dimNameMap);
				if (yMap != null) {
					map.putAll(yMap);
				} else {
					yMap = calcMDArrayStride(colArray, dataType, figureSize, Z_VALUE,
							SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION,
							SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y,
							false, dimNameMap);
					if (yMap != null) {
						map.putAll(yMap);
					}
				}
				
			} else {
				String cType = null;
				List<SGDataColumnInfo> zColList = findColumnsWithColumnType(colArray, Z_VALUE);
				if (zColList.size() == 1) {
					cType = Z_VALUE;
				}
				if (cType == null) {
					List<SGDataColumnInfo> xColList = findColumnsWithColumnType(colArray, X_VALUE);
					if (xColList.size() == 1) {
						cType = X_VALUE;
					}
				}
				if (cType == null) {
					List<SGDataColumnInfo> yColList = findColumnsWithColumnType(colArray, Y_VALUE);
					if (yColList.size() == 1) {
						cType = Y_VALUE;
					}
				}
				if (cType == null) {
					return null;
				}
				final boolean bx = (figureSize.x > figureSize.y);
				map = calcMDArrayStride(colArray, dataType, figureSize, cType,
						SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE, bx, dimNameMap);
			}

		} else if (SGDataUtility.isVXYTypeData(dataType)) {
			final String fType, sType;
			if (SGDataUtility.isPolar(infoMap)) {
				fType = MAGNITUDE;
				sType = ANGLE;
			} else {
				fType = X_COMPONENT;
				sType = Y_COMPONENT;
			}
			map = new HashMap<String, SGIntegerSeriesSet>();
			Boolean grid = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG);
			if (grid) {
				map = calcMDArrayVectorStride(colArray, infoMap, dimNameMap, dataType,
						figureSize, X_COORDINATE, Y_COORDINATE, fType, sType,
						SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X,
						SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y);
			} else {
				String cType = null;
				List<SGDataColumnInfo> fColList = findColumnsWithColumnType(colArray, fType);
				if (fColList.size() == 1) {
					cType = fType;
				}
				if (cType == null) {
					List<SGDataColumnInfo> sColList = findColumnsWithColumnType(colArray, sType);
					if (sColList.size() == 1) {
						cType = sType;
					}
				}
				if (cType == null) {
					List<SGDataColumnInfo> xColList = findColumnsWithColumnType(colArray, X_COORDINATE);
					if (xColList.size() == 1) {
						cType = X_COORDINATE;
					}
				}
				if (cType == null) {
					List<SGDataColumnInfo> yColList = findColumnsWithColumnType(colArray, Y_COORDINATE);
					if (yColList.size() == 1) {
						cType = Y_COORDINATE;
					}
				}
				if (cType == null) {
					return null;
				}
				final boolean bx = (figureSize.x > figureSize.y);
				map = calcMDArrayStride(colArray, dataType, figureSize, cType,
						SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE, bx, dimNameMap);
			}
		}

		return map;
    }

    private static Map<String, SGIntegerSeriesSet> calcMDArrayStride(
    		SGDataColumnInfo[] colArray, String dataType, SGTuple2f figureSize,
    		String cType, String key, final boolean bx, Map<String, SGMDArrayDimensionInfo> dimNameMap) {
		final int fSize = (int) (bx ? figureSize.x : figureSize.y);
		return calcMDArrayStride(colArray, dataType, fSize, cType, key, dimNameMap);
    }

    private static Map<String, SGIntegerSeriesSet> calcMDArrayStride(
    		SGDataColumnInfo[] colArray, String dataType, SGTuple2f figureSize,
    		String cType, String dimName, String key, final boolean bx,
    		Map<String, SGMDArrayDimensionInfo> dimNameMap) {
		final int fSize = (int) (bx ? figureSize.x : figureSize.y);
		return calcMDArrayStride(colArray, dataType, fSize, cType, dimName, key, dimNameMap);
    }

    private static Map<String, SGIntegerSeriesSet> calcMDArrayStride(
    		SGDataColumnInfo[] colArray, String dataType, final int fSize,
    		String cType, String key, Map<String, SGMDArrayDimensionInfo> dimNameMap) {
		int dimLen = -1;
		String datasetName = null;
		int index = -1;
		for (int ii = 0; ii < colArray.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) colArray[ii];
			String colType = mdCol.getColumnType();
			if (cType.equals(colType)) {
				datasetName = mdCol.getName();
				index = mdCol.getGenericDimensionIndex();
				final int len = mdCol.getGenericDimensionLength();
	            if (len == -1) {
	            	return null;
	            }
				if (dimLen == -1) {
					dimLen = len;
				} else {
					if (len != dimLen) {
						return null;
					}
				}
			}
		}
		if (dimLen == -1) {
			return null;
		}

		// creates an instance of stride
		SGIntegerSeriesSet stride = calcStride(dimLen, fSize);

		// creates returned value
    	Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
		map.put(key, stride);

		// set the output
		SGMDArrayDimensionInfo info = new SGMDArrayDimensionInfo(datasetName, index);
		dimNameMap.put(key, info);

		return map;
    }

    private static Map<String, SGIntegerSeriesSet> calcMDArrayStride(
    		SGDataColumnInfo[] colArray, String dataType, final int fSize,
    		String cType, String dimName, String key, Map<String, SGMDArrayDimensionInfo> dimNameMap) {
		int len = -1;
		int cnt = 0;
		String datasetName = null;
		int index = -1;
		for (int ii = 0; ii < colArray.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) colArray[ii];
			String colType = mdCol.getColumnType();
			if (cType.equals(colType)) {
				datasetName = mdCol.getName();
				Integer dimIndex = mdCol.getDimensionIndex(dimName);
				if (dimIndex == null) {
					return null;
				}
				if (dimIndex == -1) {
					return null;
				}
				index = dimIndex.intValue();
				len = mdCol.getDimensions()[index];
				cnt++;
				if (cnt > 1) {
					break;
				}
			}
		}
		if (cnt != 1) {
			return null;
		}

		// creates an instance of stride
		SGIntegerSeriesSet stride = calcStride(len, fSize);

		// creates returned value
    	Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
		map.put(key, stride);

		// set the output
		SGMDArrayDimensionInfo info = new SGMDArrayDimensionInfo(datasetName, index);
		dimNameMap.put(key, info);

		return map;
    }

    private static Map<String, SGIntegerSeriesSet> calcMDArrayVectorStride(
    		SGDataColumnInfo[] colArray, Map<String, Object> infoMap, Map<String, SGMDArrayDimensionInfo> dimNameMap,
    		String dataType, SGTuple2f figureSize, String xCType, String yCType, String fCType, String sCType,
    		String xKey, String yKey) {

    	SGMDArrayDataColumnInfo xCol = null;
    	SGMDArrayDataColumnInfo yCol = null;
		int xCnt = 0;
		int yCnt = 0;
		for (int ii = 0; ii < colArray.length; ii++) {
			SGMDArrayDataColumnInfo nCol = (SGMDArrayDataColumnInfo) colArray[ii];
			String colType = nCol.getColumnType();
			if (xCType.equals(colType)) {
				xCol = nCol;
				xCnt++;
				if (xCnt > 1) {
					break;
				}
			} else if (yCType.equals(colType)) {
				yCol = nCol;
				yCnt++;
				if (yCnt > 1) {
					break;
				}
			}
		}
		if (xCnt > 1 || yCnt > 1) {
			return null;
		}

		String xName = null;
		String yName = null;
		int xIndex = -1;
		int yIndex = -1;
		int xLen = -1;
		int yLen = -1;
		if (xCol != null) {
			xName = xCol.getName();
			xIndex = xCol.getGenericDimensionIndex();
			xLen = xCol.getGenericDimensionLength();
			if (xLen == -1) {
				return null;
			}
		} else {
			for (int ii = 0; ii < colArray.length; ii++) {
				SGMDArrayDataColumnInfo nCol = (SGMDArrayDataColumnInfo) colArray[ii];
				String colType = nCol.getColumnType();
				if (fCType.equals(colType)) {
					Integer index = nCol.getDimensionIndex(KEY_VXY_X_DIMENSION);
					if (!SGDataUtility.isValidDimensionIndex(index)) {
						return null;
					}
					xName = nCol.getName();
					xIndex = index;
					xLen = nCol.getDimensions()[index];
					break;
				}
			}
		}
		if (yCol != null) {
			yName = yCol.getName();
			yIndex = yCol.getGenericDimensionIndex();
			yLen = yCol.getGenericDimensionLength();
			if (yLen == -1) {
				return null;
			}
		} else {
			for (int ii = 0; ii < colArray.length; ii++) {
				SGMDArrayDataColumnInfo nCol = (SGMDArrayDataColumnInfo) colArray[ii];
				String colType = nCol.getColumnType();
				if (fCType.equals(colType)) {
					Integer index = nCol.getDimensionIndex(KEY_VXY_Y_DIMENSION);
					if (!SGDataUtility.isValidDimensionIndex(index)) {
						return null;
					}
					yName = nCol.getName();
					yIndex = index;
					yLen = nCol.getDimensions()[index];
					break;
				}
			}
		}

		// calculates the step of stride
		final int xStep = calcVectorStep(figureSize, true, xLen);
		final int yStep = calcVectorStep(figureSize, false, yLen);

		// creates returned value
    	Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    	if (xLen > 0) {
    		final int xEnd = xLen - 1;
    		map.put(xKey, new SGIntegerSeriesSet(0, xEnd, xStep));
    	}
    	if (yLen > 0) {
    		final int yEnd = yLen - 1;
    		map.put(yKey, new SGIntegerSeriesSet(0, yEnd, yStep));
    	}

		// set the output
		SGMDArrayDimensionInfo xInfo = new SGMDArrayDimensionInfo(xName, xIndex);
		dimNameMap.put(xKey, xInfo);
		SGMDArrayDimensionInfo yInfo = new SGMDArrayDimensionInfo(yName, yIndex);
		dimNameMap.put(yKey, yInfo);

		return map;
    }

    public static boolean hasIndexColumnType(SGDataColumnInfo[] cols) {
		List<SGDataColumnInfo> indexColumnList = findColumnsWithColumnType(cols, INDEX);
		if (indexColumnList.size() == 1) {
			return true;
		} else {
			List<SGDataColumnInfo> serialNumberColumnList = findColumnsWithColumnType(cols, SERIAL_NUMBERS);
			return (serialNumberColumnList.size() == 1);
		}
    }

    public static List<SGDataColumnInfo> findColumnsWithColumnType(SGDataColumnInfo[] cols,
    		final String columnType) {
    	List<SGDataColumnInfo> colList = new ArrayList<SGDataColumnInfo>();
		for (int ii = 0; ii < cols.length; ii++) {
			SGDataColumnInfo ncInfo = (SGDataColumnInfo) cols[ii];
			if (ncInfo.getColumnType().equalsIgnoreCase(columnType)) {
				colList.add(ncInfo);
			}
		}
		return colList;
    }

    public static List<SGDataColumnInfo> findColumnsWithColumnTypeStartsWith(
    		SGDataColumnInfo[] cols, final String columnType) {
    	List<SGDataColumnInfo> colList = new ArrayList<SGDataColumnInfo>();
		for (int ii = 0; ii < cols.length; ii++) {
			SGDataColumnInfo ncInfo = (SGDataColumnInfo) cols[ii];
			if (ncInfo.getColumnType().toUpperCase().startsWith(columnType.toUpperCase())) {
				colList.add(ncInfo);
			}
		}
		return colList;
    }

    public static SGDataColumnInfo findColumnWithName(SGDataColumnInfo[] cols, final String name) {
		for (int ii = 0; ii < cols.length; ii++) {
			SGDataColumnInfo ncInfo = (SGDataColumnInfo) cols[ii];
			if (ncInfo.getName().equalsIgnoreCase(name)) {
				return ncInfo;
			}
		}
		return null;
    }

    public static List<SGDataColumnInfo> findColumnsWithValueType(
    		SGDataColumnInfo[] cols, final String valueType) {
    	List<SGDataColumnInfo> colList = new ArrayList<SGDataColumnInfo>();
		for (int ii = 0; ii < cols.length; ii++) {
			SGDataColumnInfo ncInfo = (SGDataColumnInfo) cols[ii];
			if (ncInfo.getValueType().equalsIgnoreCase(valueType)) {
				colList.add(ncInfo);
			}
		}
		return colList;
    }

    /**
     * Returns whether the given type of data is of the array type.
     *
     * @param dataType
     *           the data type
     * @return true if the given type of data is of the array type
     */
    public static boolean isArrayData(final String dataType) {
    	return isSDArrayData(dataType) || isNetCDFData(dataType) || isMDArrayData(dataType);
    }

    /**
     * Returns whether the given data is of the array type data.
     *
     * @param data
     *           the data
     * @return true if the given data is of the array type data
     */
    public static boolean isArrayData(final SGData data) {
    	return isArrayData(data.getDataType());
    }

	public static int getSXYNetCDFDataLength(SGDataColumnInfo[] cols) {
		final int len;
		List<SGDataColumnInfo> indexColList = findColumnsWithColumnType(cols, INDEX);
		if (indexColList.size() == 0) {
			List<SGDataColumnInfo> xColList = findColumnsWithColumnType(cols, X_VALUE);
			List<SGDataColumnInfo> yColList = findColumnsWithColumnType(cols, Y_VALUE);
			SGNetCDFDataColumnInfo xCol = (SGNetCDFDataColumnInfo) xColList.get(0);
			SGNetCDFDataColumnInfo yCol = (SGNetCDFDataColumnInfo) yColList.get(0);
			if (xCol.isCoordinateVariable()) {
				len = xCol.getDimension(0).getLength();
			} else {
				len = yCol.getDimension(0).getLength();
			}
		} else {
			SGNetCDFDataColumnInfo indexCol = (SGNetCDFDataColumnInfo) indexColList.get(0);
			len = indexCol.getDimension(0).getLength();
		}
		return len;
	}

	public static int getSXYMDArrayDataLength(SGDataColumnInfo[] cols) {
		List<SGDataColumnInfo> xColList = findColumnsWithColumnType(cols, X_VALUE);
		List<SGDataColumnInfo> yColList = findColumnsWithColumnType(cols, Y_VALUE);
		SGMDArrayDataColumnInfo col;
		if (xColList.size() > 0) {
			col = (SGMDArrayDataColumnInfo) xColList.get(0);
		} else if (yColList.size() > 0) {
			col = (SGMDArrayDataColumnInfo) yColList.get(0);
		} else {
			return -1;
		}
		return col.getGenericDimensionLength();
	}

	public static int getSXYZNetCDFDataXLength(SGDataColumnInfo[] cols) {
		return getTwoDimensionNetCDFDataLength(cols, X_VALUE);
	}

	public static int getSXYZNetCDFDataYLength(SGDataColumnInfo[] cols) {
		return getTwoDimensionNetCDFDataLength(cols, Y_VALUE);
	}

	public static int getSXYZMDArrayDataXLength(SGDataColumnInfo[] cols) {
		return getSXYZMDArrayDataXYLength(cols, X_VALUE, KEY_SXYZ_X_DIMENSION);
	}

	public static int getSXYZMDArrayDataYLength(SGDataColumnInfo[] cols) {
		return getSXYZMDArrayDataXYLength(cols, Y_VALUE, KEY_SXYZ_Y_DIMENSION);
	}

	public static int getSXYZMDArrayDataZGenericDimensionLength(SGDataColumnInfo[] cols) {
		List<SGDataColumnInfo> zColList = findColumnsWithColumnType(cols, Z_VALUE);
		if (zColList.size() != 1) {
			return -1;
		}
		SGMDArrayDataColumnInfo zCol = (SGMDArrayDataColumnInfo) zColList.get(0);
		return zCol.getGenericDimensionLength();
	}

	private static int getSXYZMDArrayDataXYLength(SGDataColumnInfo[] cols, String columnType, String key) {
		int len = -1;
		List<SGDataColumnInfo> colList = findColumnsWithColumnType(cols, columnType);
		if (colList.size() == 1) {
			SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) colList.get(0);
			len = col.getGenericDimensionLength();
		} else {
			List<SGDataColumnInfo> zColList = findColumnsWithColumnType(cols, Z_VALUE);
			SGMDArrayDataColumnInfo zCol = (SGMDArrayDataColumnInfo) zColList
					.get(0);
			int[] zDims = zCol.getDimensions();
			Integer dim = zCol.getDimensionIndex(key);
			if (dim != null) {
				len = zDims[dim];
			}
		}
		return len;
	}

	public static int getVXYNetCDFDataXLength(SGDataColumnInfo[] cols) {
		return getTwoDimensionNetCDFDataLength(cols, X_COORDINATE);
	}

	public static int getVXYNetCDFDataYLength(SGDataColumnInfo[] cols) {
		return getTwoDimensionNetCDFDataLength(cols, Y_COORDINATE);
	}

	public static int getVXYMDArrayDataXLength(SGDataColumnInfo[] cols, final boolean polar) {
		return getVXYMDArrayDataXYLength(cols, X_COORDINATE, KEY_VXY_X_DIMENSION, polar);
	}

	public static int getVXYMDArrayDataYLength(SGDataColumnInfo[] cols, final boolean polar) {
		return getVXYMDArrayDataXYLength(cols, Y_COORDINATE, KEY_VXY_Y_DIMENSION, polar);
	}

	public static int getVXYMDArrayDataComponentGenericDimensionLength(SGDataColumnInfo[] cols, final boolean polar) {
		String fName = polar ? MAGNITUDE : X_COMPONENT;
		List<SGDataColumnInfo> fColList = findColumnsWithColumnType(cols, fName);
		if (fColList.size() != 1) {
			return -1;
		}
		SGMDArrayDataColumnInfo fCol = (SGMDArrayDataColumnInfo) fColList.get(0);
		return fCol.getGenericDimensionLength();
	}

	private static int getVXYMDArrayDataXYLength(SGDataColumnInfo[] cols, String columnType, String key,
			final boolean polar) {
		int len = -1;
		List<SGDataColumnInfo> colList = findColumnsWithColumnType(cols, columnType);
		String fName = polar ? MAGNITUDE : X_COMPONENT;
		List<SGDataColumnInfo> fColList = findColumnsWithColumnType(cols, fName);
		if (colList.size() == 1) {
			SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) colList.get(0);
			len = col.getGenericDimensionLength();
		} else {
			SGMDArrayDataColumnInfo fCol = (SGMDArrayDataColumnInfo) fColList.get(0);
			int[] fDims = fCol.getDimensions();
			Integer dim = fCol.getDimensionIndex(key);
			if (dim != null) {
				len = fDims[dim];
			}
		}
		return len;
	}

	private static int getTwoDimensionNetCDFDataLength(SGDataColumnInfo[] cols, String key) {
		List<SGDataColumnInfo> colList = findColumnsWithColumnType(cols, key);
		SGNetCDFDataColumnInfo col = (SGNetCDFDataColumnInfo) colList.get(0);
		return col.getDimension(0).getLength();
	}

	public static int getNetCDFDataIndexLength(SGDataColumnInfo[] cols) {
		List<SGDataColumnInfo> colList = findColumnsWithColumnType(cols, INDEX);
		SGNetCDFDataColumnInfo col = (SGNetCDFDataColumnInfo) colList.get(0);
		final int len = col.getDimension(0).getLength();
		return len;
	}

	public static boolean hasEqualInput(SGDataColumnInfo[] a1, SGDataColumnInfo[] a2) {
		if (a1 == null || a2 == null) {
			throw new IllegalArgumentException("a1 == null || a2 == null");
		}
		if (a1.length != a2.length) {
			return false;
		}
		for (int ii = 0; ii < a1.length; ii++) {
			SGDataColumnInfo c1 = a1[ii];
			SGDataColumnInfo c2 = a2[ii];
			if (!c1.getClass().equals(c2.getClass())) {
				return false;
			}
			if (!SGUtility.equals(c1.getColumnType(), c2.getColumnType())) {
				return false;
			}
			if (c1 instanceof SGMDArrayDataColumnInfo) {
				SGMDArrayDataColumnInfo nc1 = (SGMDArrayDataColumnInfo) c1;
				SGMDArrayDataColumnInfo nc2 = (SGMDArrayDataColumnInfo) c2;
				if (!nc1.getDimensionIndices().equals(nc2.getDimensionIndices())) {
					return false;
				}
			}
		}
		return true;
	}

	static boolean isValidPickUpValue(Integer value) {
		return (value != null) && !Integer.valueOf(-1).equals(value);
	}

	static boolean isValidTimeValue(Integer value) {
		return (value != null) && !Integer.valueOf(-1).equals(value);
	}

	static List<SGMDArrayDataColumnInfo> findValidPickUpXYColumns(SGDataColumnInfo[] cols) {
		List<SGMDArrayDataColumnInfo> pickUpColList = new ArrayList<SGMDArrayDataColumnInfo>();
		List<SGDataColumnInfo> xColList = SGDataUtility.findColumnsWithColumnType(cols, X_VALUE);
		List<SGDataColumnInfo> yColList = SGDataUtility.findColumnsWithColumnType(cols, Y_VALUE);
		if (xColList.size() == 0 && yColList.size() == 0) {
			return null;
		} else if (xColList.size() > 1 || yColList.size() > 1) {
			return null;
		}
		SGMDArrayDataColumnInfo xCol = null;
		if (xColList.size() == 1) {
			xCol = (SGMDArrayDataColumnInfo) xColList.get(0);
		}
		SGMDArrayDataColumnInfo yCol = null;
		if (yColList.size() == 1) {
			yCol = (SGMDArrayDataColumnInfo) yColList.get(0);
		}
		if (xCol != null && yCol != null) {
			Integer xIndex = xCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
			Integer yIndex = yCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
			final boolean xValid = isValidPickUpValue(xIndex);
			final boolean yValid = isValidPickUpValue(yIndex);
			if (xValid && !yValid) {
				if (xIndex.equals(xCol.getGenericDimensionIndex())) {
					return null;
				}
				pickUpColList.add(xCol);
			} else if (!xValid && yValid) {
				if (yIndex.equals(yCol.getGenericDimensionIndex())) {
					return null;
				}
				pickUpColList.add(yCol);
			} else if (xValid && yValid) {
				if (xIndex.equals(xCol.getGenericDimensionIndex())) {
					return null;
				}
				pickUpColList.add(xCol);
				if (yIndex.equals(yCol.getGenericDimensionIndex())) {
					return null;
				}
				pickUpColList.add(yCol);
			}
		} else if (xCol != null) {
			Integer xIndex = xCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
			final boolean xValid = isValidPickUpValue(xIndex);
			if (xValid) {
				if (xIndex.equals(xCol.getGenericDimensionIndex())) {
					return null;
				}
				pickUpColList.add(xCol);
			}
		} else if (yCol != null) {
			Integer yIndex = yCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
			final boolean yValid = isValidPickUpValue(yIndex);
			if (yValid) {
				if (yIndex.equals(yCol.getGenericDimensionIndex())) {
					return null;
				}
				pickUpColList.add(yCol);
			}
		}
		return pickUpColList;
	}

    /**
     * Returns a text string that variable names are binded into curly bracket comma separately.
     *
     * @param vars
     *           the array of variables
     * @return created text string
     */
	static String bindVariableNamesInBracket(SGNetCDFVariable[] vars) {
		String[] names = new String[vars.length];
		for (int ii = 0; ii < vars.length; ii++) {
			names[ii] = vars[ii].getValidName();
		}
		return bindVariableNamesInBracket(names);
	}

	static String bindVariableNamesInBracket(SGMDArrayVariable[] vars) {
		String[] names = new String[vars.length];
		for (int ii = 0; ii < vars.length; ii++) {
			names[ii] = vars[ii].getName();
		}
		return bindVariableNamesInBracket(names);
	}

	static String bindVariableNamesInBracket(String[] names) {
		StringBuffer sb = new StringBuffer("{");
		for (int ii = 0; ii < names.length; ii++) {
			if (ii > 0) {
				sb.append(",");
			}
			sb.append(names[ii]);
		}
		sb.append("}");
		return sb.toString();
	}

	static String bindVariableNamesInBracket(List<String> nameList) {
		String[] names = new String[nameList.size()];
		nameList.toArray(names);
		return bindVariableNamesInBracket(names);
	}

	/**
	 * Finds and returns the most frequent origin of time dimension.
	 * If selected state of time dimension is invalid, returns -1.
	 *
	 * @param cols
	 *           an array of data columns
	 * @return the most frequent origin of time dimension
	 */
	public static int findFrequentTimeOrigin(SGDataColumnInfo[] cols) {
		
		// checks validity
		boolean selectionValid = true;
		int dimLen = -1;
		for (int ii = 0; ii < cols.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
			Integer index = mdCol.getTimeDimensionIndex();
			if (index != -1) {
				int[] dims = mdCol.getDimensions();
				if (dimLen == -1) {
					dimLen = dims[index];
				} else {
					if (dimLen != dims[index]) {
						selectionValid = false;
						break;
					}
				}
			}
		}
		if (dimLen == -1) {
			// time index is not selected at any column
			return -1;
		}
		if (!selectionValid) {
			return -1;
		}

		// checks whether all indices are equal
		boolean allEqual = true;
		int origin = -1;
		for (int ii = 0; ii < cols.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
			Integer index = mdCol.getTimeDimensionIndex();
			if(index != -1) {
				int[] origins = mdCol.getOrigins();
				if (origin != -1) {
					if (origin != origins[index]) {
						allEqual = false;
					}
				}
				origin = origins[index];
			}
		}
		if (allEqual) {
			// if all indices are equal, do nothing
			return -1;
		}
		
		// finds the most frequent origin
		List<Object> originList = new ArrayList<Object>();
		for (int ii = 0; ii < cols.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
			Integer index = mdCol.getTimeDimensionIndex();
			if(index != -1) {
				int[] origins = mdCol.getOrigins();
				origin = origins[index];
				if (origin != -1) {
					originList.add(origin);
				}
			}
		}
		List<Object> fIndexList = SGUtility.findMostFrequentObjects(originList);
		if (fIndexList.size() == 0) {
			return -1;
		}
		Object first = fIndexList.get(0);	// chooses the first element
		final int fIndex = (Integer) first;
		return fIndex;
	}

	/**
	 * Creates and returns a text string for the title of a dialog to setup the data.
	 *
	 * @param prefix
	 *           the prefix for the title
	 * @param dataType
	 *           the data type
	 * @return a text string for the title
	 */
    public static String createTitleString(String prefix, String dataType) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(prefix);
    	sb.append(" (");
    	String suffix = "";
    	if (SGDataUtility.isSXYTypeData(dataType)) {
    		suffix = "Scalar-XY Graph";
    	} else if (SGDataUtility.isSXYZTypeData(dataType)) {
    		suffix = "Pseudocolor Map";
    	} else if (SGDataUtility.isVXYTypeData(dataType)) {
    		suffix = "Vector-XY Graph";
    	}
    	sb.append(suffix);
    	sb.append(")");
    	return sb.toString();
    }

	// A message for the column type.
    public static final String MSG_PROPER_COLUMN_TYPE = "Select proper column types.";

	// Messages for the stride and indices.
    public static final String MSG_PROPER_STRIDE = "Input proper values for the stride.";

    public static final String MSG_PROPER_STRIDE_LINE_AND_BAR = "Input proper values for the stride of Line and Bar.";

    public static final String MSG_PROPER_STRIDE_TICK_LABEL = "Input proper values for the stride of Tick Label.";

    public static final String MSG_PROPER_STRIDE_INDEX = "Input proper values for the stride of Index.";

    public static final String MSG_PROPER_STRIDE_X = "Input proper values for the stride of X.";

    public static final String MSG_PROPER_STRIDE_Y = "Input proper values for the stride of Y.";

	public static final String MSG_PROPER_PICK_UP_INDICES = "Input proper values for Pick Up indices.";

	// Messages for the dimension overlapping.
	public static final String MSG_UNIQUE_DIMENSIONS = "Select unique dimensions.";

	// Messages for the dimension length.
	public static final String MSG_DIMENSIONS_SAME_LENGTH = "Select dimensions with the same length.";

	public static final String MSG_DIMENSIONS_SAME_LENGTH_ANIMATION_FRAME = "Select dimensions with the same length for Animation Frame.";

	public static final String MSG_DIMENSIONS_SAME_LENGTH_PICK_UP = "Select dimensions with the same length for Pick Up.";

	public static final String MSG_DIMENSION_AND_COLUMN_TYPE_PICK_UP = "Select proper dimension and column type for Pick Up.";

    static SGNetCDFDataColumnInfo createDataColumnInfo(SGNetCDFVariable var,
    		String columnType) {
    	if (var == null) {
    		return null;
    	}
    	SGNetCDFDataColumnInfo info = new SGNetCDFDataColumnInfo(var, null, var.getValueType());
    	info.setColumnType(columnType);
    	return info;
    }

    static SGNetCDFDataColumnInfo createDataColumnInfo(SGNetCDFVariable var,
    		String columnTypeHeader, String holderName) {
    	if (var == null) {
    		return null;
    	}
    	SGNetCDFDataColumnInfo info = new SGNetCDFDataColumnInfo(var, null, var.getValueType());
    	StringBuffer sb = new StringBuffer();
    	sb.append(columnTypeHeader);
    	sb.append(MID_COLUMN);
    	sb.append(holderName);
    	info.setColumnType(sb.toString());
    	return info;
    }

    static SGNetCDFDataColumnInfo[] createDataColumnInfoArray(SGNetCDFVariable var,
    		String columnType) {
    	SGNetCDFDataColumnInfo info = createDataColumnInfo(var, columnType);
    	return new SGNetCDFDataColumnInfo[] { info };
    }

    static SGNetCDFDataColumnInfo[] createDataColumnInfoArray(SGNetCDFVariable[] vars,
    		String columnType) {
    	if (vars == null) {
    		return null;
    	}
    	SGNetCDFDataColumnInfo[] infoArray = new SGNetCDFDataColumnInfo[vars.length];
    	for (int ii = 0; ii < infoArray.length; ii++) {
    		infoArray[ii] = createDataColumnInfo(vars[ii], columnType);
    	}
    	return infoArray;
    }

    static SGNetCDFDataColumnInfo[] createDataColumnInfoArray(SGNetCDFVariable[] vars,
    		String[] columnTypes) {
    	if (vars == null) {
    		return null;
    	}
    	SGNetCDFDataColumnInfo[] infoArray = new SGNetCDFDataColumnInfo[vars.length];
    	for (int ii = 0; ii < infoArray.length; ii++) {
    		infoArray[ii] = createDataColumnInfo(vars[ii], columnTypes[ii]);
    	}
    	return infoArray;
    }

    static SGNetCDFDataColumnInfo createErrorBarInfo(SGNetCDFVariable var, String nameStr,
    		SGNetCDFVariable leVar, SGNetCDFVariable ueVar, SGNetCDFVariable ehVar) {
		String name;
		if (leVar.equals(ueVar)) {
			name = LOWER_UPPER_ERROR_VALUE;
		} else {
			name = nameStr;
		}
		String ehName = ehVar.getName();
		return SGDataUtility.createDataColumnInfo(var, name, ehName);
    }

    static SGMDArrayDataColumnInfo createDataColumnInfo(SGMDArrayVariable var,
    		String columnType) {
    	if (var == null) {
    		return null;
    	}
    	SGMDArrayDataColumnInfo mdInfo = new SGMDArrayDataColumnInfo(var, null, var.getValueType());
    	mdInfo.setColumnType(columnType);
    	return mdInfo;
    }

    static SGMDArrayDataColumnInfo[] createDataColumnInfoArray(SGMDArrayVariable var,
    		String columnType) {
    	SGMDArrayDataColumnInfo info = createDataColumnInfo(var, columnType);
    	if (info == null) {
    		return new SGMDArrayDataColumnInfo[0];
    	}
    	return new SGMDArrayDataColumnInfo[] { info };
    }

    static SGMDArrayDataColumnInfo[] createDataColumnInfoArray(SGMDArrayVariable[] vars,
    		String columnType) {
    	if (vars == null) {
    		return null;
    	}
    	SGMDArrayDataColumnInfo[] infoArray = new SGMDArrayDataColumnInfo[vars.length];
    	for (int ii = 0; ii < infoArray.length; ii++) {
    		infoArray[ii] = createDataColumnInfo(vars[ii], columnType);
    	}
    	return infoArray;
    }

    static SGMDArrayDataColumnInfo[] createDataColumnInfoArray(SGMDArrayVariable[] vars,
    		String[] columnTypes) {
    	if (vars == null) {
    		return null;
    	}
    	SGMDArrayDataColumnInfo[] infoArray = new SGMDArrayDataColumnInfo[vars.length];
    	for (int ii = 0; ii < infoArray.length; ii++) {
    		infoArray[ii] = createDataColumnInfo(vars[ii], columnTypes[ii]);
    	}
    	return infoArray;
    }

    static SGMDArrayDataColumnInfo createErrorBarInfo(SGMDArrayVariable var, String nameStr,
    		SGMDArrayVariable leVar, SGMDArrayVariable ueVar, SGMDArrayVariable ehVar) {
		String name;
		if (leVar.equals(ueVar)) {
			name = LOWER_UPPER_ERROR_VALUE;
		} else {
			name = nameStr;
		}
		String ehName = ehVar.getName();
		return SGDataUtility.createDataColumnInfo(var, name, ehName);
    }

    static SGMDArrayDataColumnInfo createDataColumnInfo(SGMDArrayVariable var,
    		String columnTypeHeader, String holderName) {
    	if (var == null) {
    		return null;
    	}
    	SGMDArrayDataColumnInfo info = new SGMDArrayDataColumnInfo(var, null, var.getValueType());
    	StringBuffer sb = new StringBuffer();
    	sb.append(columnTypeHeader);
    	sb.append(MID_COLUMN);
    	sb.append(holderName);
    	info.setColumnType(sb.toString());
    	return info;
    }

    static String encodeString(String str) {
		byte[] bArray;
		try {
			bArray = str.getBytes(SGIConstants.CHAR_SET_NAME_UTF8);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		char[] cArray = new char[bArray.length];
		for (int ii = 0; ii < cArray.length; ii++) {
			cArray[ii] = (char) bArray[ii];
		}
		String strNew = new String(cArray);
		return strNew;
    }

    static String decodeString(String str) {
		char[] cArray = str.toCharArray();
		byte[] bArray = new byte[cArray.length];
		for (int jj = 0; jj < bArray.length; jj++) {
			bArray[jj] = (byte) cArray[jj];
		}
		String strNew;
		try {
			strNew = new String(bArray, SGIConstants.CHAR_SET_NAME_UTF8);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		return strNew;
    }
    
    /**
     * Returns whether each column type of given data column information arrays.
     * 
     * @param cols1
     *           the first column information array
     * @param cols2
     *           the second column information array
     * @return true if each column type of given data column information arrays are equal
     */
    public static boolean hasEqualColumnType(SGDataColumnInfo[] cols1, SGDataColumnInfo[] cols2) {
    	if (cols1 == null || cols2 == null) {
    		throw new IllegalArgumentException("cols1 == null || cols2 == null");
    	}
    	if (cols1.length != cols2.length) {
    		throw new IllegalArgumentException("cols1.length != cols2.length");
    	}
    	for (int ii = 0; ii < cols1.length; ii++) {
    		if (!SGUtility.equals(cols1[ii].getColumnType(), cols2[ii].getColumnType())) {
    			return false;
    		}
    	}
    	return true;
    }

    /**
     * Creates and returns a data buffer.
     * 
     * @param data
     *           a multiple scalar-XY data
     * @param policy
     *           parameters for data buffer
     * @return the data buffer
     */
    public static SGDataBuffer getDataBuffer(SGISXYTypeSingleData data, SGSXYDataBufferPolicy policy) {
    	if (data == null) {
    		throw new IllegalArgumentException("data == null");
    	}
    	SGSXYMultipleDataBuffer multipleBuffer = (SGSXYMultipleDataBuffer) getDataBuffer(data.toMultiple(), policy);
    	return multipleBuffer.getSXYDataBufferArray()[0];
	}

    /**
     * Creates and returns a data buffer.
     * 
     * @param data
     *           a multiple scalar-XY data
     * @param policy
     *           parameters for data buffer
     * @return the data buffer
     */
    public static SGDataBuffer getDataBuffer(SGISXYTypeMultipleData data, 
    		SGSXYDataBufferPolicy policy) {
    	if (data == null) {
    		throw new IllegalArgumentException("data == null");
    	}
    	if (policy == null) {
    		throw new IllegalArgumentException("param == null");
    	}
    	
    	final int childNum = data.getChildNumber();
    	int[] indices = new int[childNum];
    	for (int ii = 0; ii < childNum; ii++) {
    		indices[ii] = ii;
    	}
    	return getDataBuffer(data, policy, indices);
	}

    /**
     * Creates and returns a data buffer with given array of child indices.
     * 
     * @param data
     *           a multiple scalar-XY data
     * @param policy
     *           parameters for data buffer
     * @param indices
     *           array of child indices
     * @return the data buffer
     */
    public static SGDataBuffer getDataBuffer(SGISXYTypeMultipleData data, 
    		SGSXYDataBufferPolicy policy, int[] indices) {
    	if (data == null) {
    		throw new IllegalArgumentException("data == null");
    	}
    	if (policy == null) {
    		throw new IllegalArgumentException("policy == null");
    	}
    	if (indices == null) {
    		throw new IllegalArgumentException("indices == null");
    	}
		final int num = data.getChildNumber();
		for (int ii = 0; ii < indices.length; ii++) {
			if (indices[ii] < 0 || indices[ii] >= num) {
	    		throw new IllegalArgumentException("Index out of bounds: indices[" + ii
	    				+ "] == " + indices[ii]);
			}
		}
		if (!SGUtility.checkOverlapping(indices)) {
    		throw new IllegalArgumentException("Indices overlapping");
		}
		
    	SGSXYDataBufferPolicy sxyPolicy = (SGSXYDataBufferPolicy) policy;
		final boolean edit = sxyPolicy.isEditedValuesReflected();
		final boolean shift = sxyPolicy.isShiftValuesContained();
		SGTuple2d shiftValue = shift ? data.getShift() : new SGTuple2d();

		double[][] xArray = data.getXValueArray(sxyPolicy);
		double[][] yArray = data.getYValueArray(sxyPolicy);
		final Boolean yHolderFlag = data.isYValuesHolder();
    	final Boolean dateFlag = data.getDateFlag();
    	SGDate[] dateArray = data.getDateArray(sxyPolicy);
    	double[][] lowerErrorValues = data.getLowerErrorValueArray(sxyPolicy);
    	double[][] upperErrorValues = data.getUpperErrorValueArray(sxyPolicy);
    	String[][] tickLabels = data.getTickLabelArray(sxyPolicy);
    	Boolean[] sameErrorVariableFlags = data.hasSameErrorVariable();

		// set edited values
    	if (edit) {
    		final boolean all = policy.isAllValuesGotten();
    		SGIntegerSeriesSet stride = data.isIndexAvailable() ? data.getIndexStride() : data.getStride();
    		int[] indexArray = stride.getNumbers();
    		List<SGDataValueHistory> editedValueList = data.getEditedValueList();
    		for (int ii = 0; ii < editedValueList.size(); ii++) {
    			SGDataValueHistory dataValue = editedValueList.get(ii);
    			SGDataValueHistory.IMultiple multiple = (SGDataValueHistory.IMultiple) dataValue;
    			String columnType = dataValue.getColumnType();
    			final int childIndex = multiple.getChildIndex();
    			final int index = multiple.getIndex();
    			final int arrayIndex = all ? index : Arrays.binarySearch(indexArray, index);
    			final double value = dataValue.getValue();
    			if (X_VALUE.equals(columnType)) {
    				xArray[childIndex][arrayIndex] = value + shiftValue.x;
    			} else if (Y_VALUE.equals(columnType)) {
    				yArray[childIndex][arrayIndex] = value + shiftValue.y;
    			}
    		}
    	}

    	final SGDataBuffer buffer;
		if (data.hasOneSidedMultipleValues()) {
			double[] singleValues = null;
			final double[][] mValues;
			if (data.hasMultipleYValues()) {
				singleValues = xArray[0];
				mValues = yArray;
			} else {
				singleValues = yArray[0];
				mValues = xArray;
			}
			double[][] multipleValues = new double[indices.length][];
			for (int ii = 0; ii < indices.length; ii++) {
				multipleValues[ii] = mValues[indices[ii]];
			}
			buffer = new SGSXYMultipleDataBuffer(singleValues, multipleValues, data.hasMultipleYValues(),
					dateFlag, dateArray, lowerErrorValues, upperErrorValues, 
					sameErrorVariableFlags, tickLabels);
		} else {
			double[][] xValues = new double[indices.length][];
			double[][] yValues = new double[indices.length][];
			for (int ii = 0; ii < indices.length; ii++) {
				xValues[ii] = xArray[indices[ii]];
			}
			for (int ii = 0; ii < indices.length; ii++) {
				yValues[ii] = yArray[indices[ii]];
			}
			buffer = new SGSXYMultipleDataBuffer(xValues, yValues, 
					dateFlag, dateArray, yHolderFlag, lowerErrorValues, upperErrorValues, 
					sameErrorVariableFlags, tickLabels);
		}
		return buffer;
    }
    
    enum STATUS {
    	VALUE_EXIST, TICK_LABEL_EXIST, SKIPPED,
    };
    
    public static double[][] getXValues(final SGISXYTypeMultipleData data, 
    		final SGSXYDataBufferPolicy policy) {
		double[][] values = data.getUnshiftedXValueArray(policy);
		final double[][] ret;
		if (policy.isShiftValuesContained()) {
	    	SGTuple2d shift = data.getShift();
			ret = new double[values.length][];
	    	for (int ii = 0; ii < values.length; ii++) {
	    		ret[ii] = shiftValues(values[ii], shift.x);
	    	}
		} else {
			ret = values;
		}
    	return ret;
    }

    public static double[][] getUnshiftedXValues(final SGISXYTypeMultipleData data, 
    		final SGSXYDataBufferPolicy policy) {
    	final boolean remove = policy.isInvalidValuesRemoved() 
    			|| policy.isShiftValuesContained();	// set null if shifted
		double[][] allValues = data.getXValueArray(true, false, remove);
		if (policy.isAllValuesGotten()) {
			return allValues;
		}
		return getUnshiftedXYValues(data, policy, allValues, false);
    }

    public static double[][] getYValues(final SGISXYTypeMultipleData data, 
    		final SGSXYDataBufferPolicy policy) {
		double[][] values = data.getUnshiftedYValueArray(policy);
		final double[][] ret;
		if (policy.isShiftValuesContained()) {
	    	SGTuple2d shift = data.getShift();
			ret = new double[values.length][];
	    	for (int ii = 0; ii < values.length; ii++) {
	    		ret[ii] = shiftValues(values[ii], shift.y);
	    	}
		} else {
			ret = values;
		}
    	return ret;
    }

    public static double[][] getUnshiftedYValues(final SGISXYTypeMultipleData data, 
    		final SGSXYDataBufferPolicy policy) {
    	final boolean remove = policy.isInvalidValuesRemoved() 
    			|| policy.isShiftValuesContained();	// set null if shifted
		double[][] allValues = data.getYValueArray(true, false, remove);
		if (policy.isAllValuesGotten()) {
			return allValues;
		}
		return getUnshiftedXYValues(data, policy, allValues, true);
    }

    private static double[][] getUnshiftedXYValues(final SGISXYTypeMultipleData data, 
    		final SGSXYDataBufferPolicy policy, final double[][] allValues,
    		final boolean yFlag) {
		double[][] ret = null;
		if (policy.isTakingAllStride()) {
			if (data.isStrideAvailable()) {
				SGIntegerSeriesSet mainStride = data.getMainStride();
				SGIntegerSeriesSet tickLabelStride = data.getTickLabelStride();
				if (mainStride.isComplete() && tickLabelStride.isComplete()) {
					ret = allValues;
				} else {
					final boolean nanFlag;
					if (data.isTickLabelAvailable()) {
						final boolean horizontal = data.isTickLabelHorizontal();
						if (horizontal) {
							nanFlag = yFlag;
						} else {
							nanFlag = !yFlag;
						}
					} else {
						nanFlag = false;
					}
					int[] mainIndices = mainStride.getNumbers();
					int[] tickLabelIndices = tickLabelStride.getNumbers();
					final int allNum = data.getAllPointsNumber();
					STATUS[] statusArray = new STATUS[allNum];
					Arrays.fill(statusArray, STATUS.SKIPPED);
					for (int ii = 0; ii < tickLabelIndices.length; ii++) {
						statusArray[tickLabelIndices[ii]] = STATUS.TICK_LABEL_EXIST;	// status to set NaN
					}
					for (int ii = 0; ii < mainIndices.length; ii++) {
						statusArray[mainIndices[ii]] = STATUS.VALUE_EXIST;	// status to set given value
					}
					ret = new double[allValues.length][];
					for (int ii = 0; ii < allValues.length; ii++) {
						double[] allValueArray = allValues[ii];
						int cnt = 0;
						for (int jj = 0; jj < allNum; jj++) {
							final STATUS status = statusArray[jj];
							if (!STATUS.SKIPPED.equals(status)) {
								cnt++;
							}
						}
						double[] retArray = new double[cnt];
						cnt = 0;
						for (int jj = 0; jj < allNum; jj++) {
							final STATUS status = statusArray[jj];
							if (STATUS.VALUE_EXIST.equals(status)) {
								retArray[cnt] = allValueArray[jj];
								cnt++;
							} else if (STATUS.TICK_LABEL_EXIST.equals(status)) {
								retArray[cnt] = nanFlag ? Double.NaN : allValueArray[jj];
								cnt++;
							}
						}
						ret[ii] = retArray;
					}
				}
			} else {
				ret = allValues;
			}
		} else {
			ret = allValues;
		}
		return ret;
    }

    private static double[] shiftValues(final double[] values, final double shift) {
    	double[] ret = new double[values.length];
    	for (int ii = 0; ii < ret.length; ii++) {
    		ret[ii] = values[ii] + shift;
    	}
    	return ret;
    }

    public static double[][] getLowerErrorValueArray(final SGISXYTypeMultipleData data, 
    		final SGSXYDataBufferPolicy policy) {
		if (!data.isErrorBarAvailable()) {
			return null;
		}
    	final boolean all = policy.isAllValuesGotten();
    	final boolean remove = policy.isInvalidValuesRemoved();
		SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    	double[][] allValues = new double[sxyArray.length][];
		for (int ii = 0; ii < sxyArray.length; ii++) {
			if (sxyArray[ii].isErrorBarAvailable()) {
				allValues[ii] = sxyArray[ii].getLowerErrorValueArray(all, false, remove);
			}
		}
		
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);
		return getErrorValues(data, policy, allValues);
	}

    public static double[][] getUpperErrorValueArray(final SGISXYTypeMultipleData data, 
    		final SGSXYDataBufferPolicy policy) {
		if (!data.isErrorBarAvailable()) {
			return null;
		}
    	final boolean all = policy.isAllValuesGotten();
    	final boolean remove = policy.isInvalidValuesRemoved();
		SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
		double[][] allValues = new double[sxyArray.length][];
		for (int ii = 0; ii < sxyArray.length; ii++) {
			if (sxyArray[ii].isErrorBarAvailable()) {
				allValues[ii] = sxyArray[ii].getUpperErrorValueArray(all, false, remove);
			}
		}
		
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);
		return getErrorValues(data, policy, allValues);
    }

    private static double[][] getErrorValues(final SGISXYTypeMultipleData data, 
    		final SGSXYDataBufferPolicy policy, final double[][] allValues) {
		double[][] ret = null;
		if (policy.isTakingAllStride()) {
			if (data.isStrideAvailable()) {
				SGIntegerSeriesSet mainStride = data.getMainStride();
				SGIntegerSeriesSet tickLabelStride = data.getTickLabelStride();
				if (mainStride.isComplete() && tickLabelStride.isComplete()) {
					ret = allValues;
				} else {
					int[] mainIndices = mainStride.getNumbers();
					int[] tickLabelIndices = tickLabelStride.getNumbers();
					final int allNum = data.getAllPointsNumber();
					STATUS[] statusArray = new STATUS[allNum];
					Arrays.fill(statusArray, STATUS.SKIPPED);
					for (int ii = 0; ii < tickLabelIndices.length; ii++) {
						statusArray[tickLabelIndices[ii]] = STATUS.TICK_LABEL_EXIST;
					}
					for (int ii = 0; ii < mainIndices.length; ii++) {
						statusArray[mainIndices[ii]] = STATUS.VALUE_EXIST;
					}
					ret = new double[allValues.length][];
					for (int ii = 0; ii < allValues.length; ii++) {
						double[] allValueArray = allValues[ii];
						if (allValueArray == null) {
							continue;
						}
						int cnt = 0;
						for (int jj = 0; jj < allNum; jj++) {
							final STATUS status = statusArray[jj];
							if (!STATUS.SKIPPED.equals(status)) {
								cnt++;
							}
						}
						double[] retArray = new double[cnt];
						cnt = 0;
						for (int jj = 0; jj < allNum; jj++) {
							final STATUS status = statusArray[jj];
							if (!STATUS.SKIPPED.equals(status)) {
								retArray[cnt] = allValueArray[jj];
								cnt++;
							}
						}
						ret[ii] = retArray;
					}
				}
			} else {
				ret = allValues;
			}
		} else {
			ret = allValues;
		}
		return ret;
    }

    public static String[][] getTickLabelArray(final SGISXYTypeMultipleData data, 
    		final SGSXYDataBufferPolicy policy) {
		if (!data.isTickLabelAvailable()) {
			return null;
		}
		String[][] ret = null;
		SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
		String[][] allValues = new String[sxyArray.length][];
		for (int ii = 0; ii < sxyArray.length; ii++) {
			if (sxyArray[ii].isTickLabelAvailable()) {
				allValues[ii] = sxyArray[ii].getStringArray(true);
			}
		}
		if (policy.isTakingAllStride()) {
			if (data.isStrideAvailable()) {
				SGIntegerSeriesSet mainStride = data.getMainStride();
				SGIntegerSeriesSet tickLabelStride = data.getTickLabelStride();
				if (mainStride.isComplete() && tickLabelStride.isComplete()) {
					ret = allValues;
				} else {
					int[] mainIndices = mainStride.getNumbers();
					int[] tickLabelIndices = tickLabelStride.getNumbers();
					final int allNum = data.getAllPointsNumber();
					STATUS[] statusArray = new STATUS[allNum];
					Arrays.fill(statusArray, STATUS.SKIPPED);
					for (int ii = 0; ii < mainIndices.length; ii++) {
						statusArray[mainIndices[ii]] = STATUS.VALUE_EXIST;	// status to set an empty string
					}
					for (int ii = 0; ii < tickLabelIndices.length; ii++) {
						statusArray[tickLabelIndices[ii]] = STATUS.TICK_LABEL_EXIST;	// status to set given string
					}
					ret = new String[allValues.length][];
					for (int ii = 0; ii < allValues.length; ii++) {
						if (!sxyArray[ii].isTickLabelAvailable()) {
							continue;
						}
						String[] allValueArray = allValues[ii];
						int cnt = 0;
						for (int jj = 0; jj < allNum; jj++) {
							final STATUS status = statusArray[jj];
							if (!STATUS.SKIPPED.equals(status)) {
								cnt++;
							}
						}
						String[] retArray = new String[cnt];
						cnt = 0;
						for (int jj = 0; jj < allNum; jj++) {
							final STATUS status = statusArray[jj];
							if (STATUS.TICK_LABEL_EXIST.equals(status)) {
								retArray[cnt] = allValueArray[jj];
								cnt++;
							} else if (STATUS.VALUE_EXIST.equals(status)) {
								retArray[cnt] = "";
								cnt++;
							}
						}
						ret[ii] = retArray;
					}
				}
			} else {
				ret = allValues;
			}
		} else {
			ret = allValues;
		}
		
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);

		return ret;
    }

    public static SGDate[] getDateArray(final SGISXYTypeMultipleData data, 
    		final SGSXYDataBufferPolicy policy) {
		SGDate[] allValues = data.getDateArray(true);
		if (allValues == null) {
			return null;
		}
		SGDate[] ret = null;
		if (policy.isTakingAllStride()) {
			if (data.isStrideAvailable()) {
				SGIntegerSeriesSet mainStride = data.getMainStride();
				SGIntegerSeriesSet tickLabelStride = data.getTickLabelStride();
				if (mainStride.isComplete() && tickLabelStride.isComplete()) {
					ret = allValues;
				} else {
					int[] mainIndices = mainStride.getNumbers();
					int[] tickLabelIndices = tickLabelStride.getNumbers();
					final int allNum = data.getAllPointsNumber();
					STATUS[] statusArray = new STATUS[allNum];
					Arrays.fill(statusArray, STATUS.SKIPPED);
					for (int ii = 0; ii < mainIndices.length; ii++) {
						statusArray[mainIndices[ii]] = STATUS.VALUE_EXIST;	// status to set an empty string
					}
					for (int ii = 0; ii < tickLabelIndices.length; ii++) {
						statusArray[tickLabelIndices[ii]] = STATUS.TICK_LABEL_EXIST;	// status to set given string
					}
					int cnt = 0;
					for (int jj = 0; jj < allNum; jj++) {
						final STATUS status = statusArray[jj];
						if (!STATUS.SKIPPED.equals(status)) {
							cnt++;
						}
					}
					SGDate[] retArray = new SGDate[cnt];
					cnt = 0;
					for (int jj = 0; jj < allNum; jj++) {
						final STATUS status = statusArray[jj];
						if (!STATUS.SKIPPED.equals(status)) {
							retArray[cnt] = allValues[jj];
							cnt++;
						}
					}
					ret = retArray;
				}
			} else {
				ret = allValues;
			}
		} else {
			ret = allValues;
		}
		return ret;
    }

    /**
     * Returns the grid plot flag.
     * 
     * @param dataType
     *           the type of data
     * @param infoMap
     *           information map
     * @return the grid plot flag
     */
    public static Boolean isGridPlot(String dataType, Map<String, Object> infoMap) {
    	Boolean ret = null;
    	if (SGDataUtility.isSXYZTypeData(dataType)) {
    		Boolean gridPlot = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG);
    		final boolean b;
    		if (gridPlot != null) {
    			b = gridPlot.booleanValue();
    		} else {
    			SGIntegerSeriesSet indexStride = (SGIntegerSeriesSet) infoMap.get(
    					SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE);
    			b = (indexStride == null);
    		}
    		ret = b;
    	} else if (SGDataUtility.isVXYTypeData(dataType)) {
    		Boolean gridPlot = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG);
    		final boolean b;
    		if (gridPlot != null) {
    			b = gridPlot.booleanValue();
    		} else {
    			SGIntegerSeriesSet indexStride = (SGIntegerSeriesSet) infoMap.get(
    					SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE);
    			b = (indexStride == null);
    		}
    		ret = b;
    	}
    	return ret;
    }

    /**
     * Returns whether given dimension index is valid.
     * 
     * @param index
     *           dimension index
     * @return true if given dimension index is valid
     */
	public static boolean isValidDimensionIndex(final Integer index) {
		return (index != null) && (index != -1);
	}

	/**
	 * Adds the grid type of data to the information map.
	 * 
	 * @param colInfoArray
	 *            an array of data column information
	 * @param dataType
	 *            data type
	 * @param infoMap
	 *            the information map
	 * @return true if succeeded
	 */
	public static boolean addGridType(SGDataColumnInfo[] colInfoArray, String dataType,
			Map<String, Object> infoMap) {
		if (!SGDataUtility.isMDArrayData(dataType)) {
			return false;
		}
        if (SGDataUtility.isSXYZTypeData(dataType)) {
            List<SGDataColumnInfo> zList = SGDataUtility.findColumnsWithColumnType(
            		colInfoArray, SGIDataColumnTypeConstants.Z_VALUE);
            if (zList.size() != 1) {
                return false;
            }
        	SGMDArrayDataColumnInfo zInfo = (SGMDArrayDataColumnInfo) zList.get(0);
        	Integer xDim = zInfo.getDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION);
        	Integer yDim = zInfo.getDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION);
        	Boolean grid = SGDataUtility.isValidDimensionIndex(xDim) && SGDataUtility.isValidDimensionIndex(yDim);
        	infoMap.put(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, grid);
        } else if (SGDataUtility.isVXYTypeData(dataType)) {
        	Boolean polar = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED);
        	if (polar == null) {
                return false;
        	}
        	String colName = polar ? SGIDataColumnTypeConstants.MAGNITUDE : SGIDataColumnTypeConstants.X_COMPONENT;
            List<SGDataColumnInfo> fList = SGDataUtility.findColumnsWithColumnType(
            		colInfoArray, colName);
            if (fList.size() != 1) {
                return false;
            }
        	SGMDArrayDataColumnInfo fInfo = (SGMDArrayDataColumnInfo) fList.get(0);
        	Integer xDim = fInfo.getDimensionIndex(SGIMDArrayConstants.KEY_VXY_X_DIMENSION);
        	Integer yDim = fInfo.getDimensionIndex(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION);
        	Boolean grid = SGDataUtility.isValidDimensionIndex(xDim) && SGDataUtility.isValidDimensionIndex(yDim);
        	infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, grid);
        } else {
        	return false;
        }
        return true;
	}
	
	/**
	 * Creates and returns an array of text strings in canonical format for the column type
	 * from an array of input text strings.
	 * 
	 * @param columnTypes
	 *            an array of input text strings
	 * @return an array of text strings in canonical format for the column type
	 */
	public static String[] getCanonicalColumnTypes(String[] columnTypes) {
		String[] allColumnTypes = { X_VALUE, Y_VALUE, Z_VALUE,
				LOWER_ERROR_VALUE, UPPER_ERROR_VALUE, LOWER_UPPER_ERROR_VALUE,
				TICK_LABEL, X_COORDINATE, Y_COORDINATE, X_COMPONENT,
				Y_COMPONENT, MAGNITUDE, ANGLE, ANIMATION_FRAME, TIME, PICKUP,
				INDEX, SERIAL_NUMBERS, X_INDEX, Y_INDEX };
		String[] retColumnTypes = new String[columnTypes.length];
		for (int ii = 0; ii < retColumnTypes.length; ii++) {
			String retStr = columnTypes[ii];
			for (int jj = 0; jj < allColumnTypes.length; jj++) {
				String ref = allColumnTypes[jj];
				if (SGUtilityText.isEqualString(retStr, ref)) {
					retStr = ref;
					break;
				}
			}
			retColumnTypes[ii] = retStr;
		}
		return retColumnTypes;
	}
	
	public static final boolean isEqualColumnType(String str1, String str2) {
		return SGUtilityText.isEqualString(str1, str2);
//		return SGUtility.equals(str1, str2);
	}
	
	public static final boolean columnTypeStartsWith(String str, String prefix) {
		return SGUtilityText.startsWith(str, prefix);
//		return str.startsWith(prefix);
	}
	
	public static String getDimensionString(String[] dimNames) {
    	StringBuffer sb = new StringBuffer();
    	for (int ii = 0; ii < dimNames.length; ii++) {
    		if (ii > 0) {
    			sb.append(' ');
    		}
    		sb.append(dimNames[ii]);
    	}
    	return sb.toString();
	}
	
	public static String getDimensionString(Dimension[] dims) {
		String[] dimNames = new String[dims.length];
		for (int ii = 0; ii < dims.length; ii++) {
			dimNames[ii] = dims[ii].getName();
		}
		return getDimensionString(dimNames);
	}
	
	public static String getDimensionString(List<Dimension> dimList) {
		Dimension[] dims = dimList.toArray(new Dimension[dimList.size()]);
		return getDimensionString(dims);
	}
	
	public static String getTextValue(String str) {
		StringBuffer sb = new StringBuffer();
		sb.append('"');
		sb.append(str);
		sb.append('"');
		return sb.toString();
	}
	
    public static Object[][] getValueTable(SGISXYZTypeData data,
    		final SGExportParameter mode, SGDataBufferPolicy policy) {
    	SGDataBuffer buf = data.getDataBuffer(policy);
    	if (buf == null) {
    		return null;
    	}
    	if (buf instanceof SGSXYZDataBuffer) {
    		SGSXYZDataBuffer buffer = (SGSXYZDataBuffer) buf;
    		final int len = buffer.getLength();
    		double[] xValues = buffer.getXValues();
    		double[] yValues = buffer.getYValues();
    		double[] zValues = buffer.getZValues();
    		Object[][] array = new Object[len][3];
    		for (int ii = 0; ii < len; ii++) {
    			array[ii][0] = xValues[ii];
    			array[ii][1] = yValues[ii];
    			array[ii][2] = zValues[ii];
    		}
    		return array;
    	} else {
    		return null;
    	}
	}

    public static Object[][] getValueTable(SGIVXYTypeData data,
    		final SGExportParameter mode, SGDataBufferPolicy policy) {
    	SGDataBuffer buf = data.getDataBuffer(policy);
    	if (buf == null) {
    		return null;
    	}
    	if (buf instanceof SGVXYDataBuffer) {
    		SGVXYDataBuffer buffer = (SGVXYDataBuffer) buf;
    		final int len = buffer.getLength();
    		double[] xValues = buffer.getXValues();
    		double[] yValues = buffer.getYValues();
    		double[] fValues = buffer.getFirstComponentValues();
    		double[] sValues = buffer.getSecondComponentValues();
    		Object[][] array = new Object[len][4];
    		for (int ii = 0; ii < len; ii++) {
    			array[ii][0] = xValues[ii];
    			array[ii][1] = yValues[ii];
    			array[ii][2] = fValues[ii];
    			array[ii][3] = sValues[ii];
    		}
    		return array;
    	} else {
    		return null;
    	}
	}
    
	/**
	 * Returns true if all stride of given data are available and each string representation 
	 * is different from "0:end".
	 * 
	 * @param data
	 *           a data
	 * @return true if all stride are effective
	 */
    public static boolean hasEffectiveStride(SGISXYTypeData data) {
    	if (!data.isStrideAvailable()) {
    		return false;
    	}
    	SGIntegerSeriesSet stride = data.getStride();
    	SGIntegerSeriesSet tickLabelStride = data.getTickLabelStride();
    	SGIntegerSeriesSet indexStride = null;
    	if (data instanceof SGNetCDFData) {
    		SGNetCDFData ncData = (SGNetCDFData) data;
        	indexStride = ncData.getIndexStride();
    	}
    	final boolean tlFlag = !tickLabelStride.isComplete();
    	if (stride != null) {
        	return !stride.isComplete() || tlFlag;
    	} else if (indexStride != null) {
    		return !indexStride.isComplete() || tlFlag;
    	} else {
    		throw new Error("This shouldn't happen.");
    	}
    }

    /**
     * This method creates and returns valid name of dimensions, variables and attributes of NetCDF data
     * from given text string.
     * Note: NetCDF-type dataset file contains invalid name such as "id0-0/column0".
     *       This method is the major for such invalid files.
     * 
     * @param str
     *           a text string
     * @return valid text string
     */
    public static String getNetCDFValidName(String str) {
    	if (str == null) {
    		return null;
    	}
    	if (str.length() == 0) {
    		return null;
    	}
    	final char[] cArray = str.toCharArray();
    	final char c0 = cArray[0];
    	StringBuffer sb = new StringBuffer();
    	if (!SGUtility.isAlphabetic(c0)) {
    		sb.append("sg_");
    	}
    	for (int ii = 0; ii < cArray.length; ii++) {
    		final char c = cArray[ii];
    		final char cNew;
    		if (SGUtility.isAlphabetic(c) || SGUtility.isDigit(c) || (c == '_')) {
    			cNew = c;
    		} else {
    			cNew = '_';
    		}
    		sb.append(cNew);
    	}
    	return sb.toString();
    }
    
    public static boolean isArchiveDataSetOperation(OPERATION mode) {
    	return (OPERATION.SAVE_TO_ARCHIVE_DATA_SET.equals(mode) 
				|| OPERATION.SAVE_TO_ARCHIVE_DATA_SET_107.equals(mode));
    }

    public static void disposeSXYDataArray(SGISXYTypeSingleData[] sxyArray) {
    	for (int ii = 0; ii < sxyArray.length; ii++) {
    		sxyArray[ii].dispose();
    	}
    }

	public static boolean isAcceptableCharHDF5Wind(final char c) {
		return (c >= '\u0020' && c <= '\u007e');
	}

	public static boolean hasValidHDF5CharacterForWin(final String str) {
		char[] cArray = str.toCharArray();
		for (int ii = 0; ii < cArray.length; ii++) {
			final char c = cArray[ii];
			if (!isAcceptableCharHDF5Wind(c)) {
				return false;
			}
		}
		return true;
	}
	
	public static void getIndexList(List<SGXYSimpleDoubleValueIndexBlock> blocks,
			List<Integer> xIndexList, List<Integer> yIndexList,
			final int xAllLen, final int yAllLen) {
		Set<SGIntegerSeries> xSeriesSet = new HashSet<SGIntegerSeries>();
		Set<SGIntegerSeries> ySeriesSet = new HashSet<SGIntegerSeries>();
		for (SGXYSimpleDoubleValueIndexBlock block : blocks) {
        	SGIntegerSeries xSeries = block.getXSeries();
        	SGIntegerSeries ySeries = block.getYSeries();
        	xSeriesSet.add(xSeries);
        	ySeriesSet.add(ySeries);
		}
		List<SGIntegerSeries> xSeriesList = new ArrayList<SGIntegerSeries>(xSeriesSet);
		List<SGIntegerSeries> ySeriesList = new ArrayList<SGIntegerSeries>(ySeriesSet);
		
		Set<Integer> xIndexSet = new TreeSet<Integer>();
		for (SGIntegerSeries series : xSeriesList) {
			int[] numArray = series.getNumbers();
			for (int ii = 0; ii < numArray.length; ii++) {
				xIndexSet.add(numArray[ii]);
			}
		}
		Set<Integer> yIndexSet = new TreeSet<Integer>();
		for (SGIntegerSeries series : ySeriesList) {
			int[] numArray = series.getNumbers();
			for (int ii = 0; ii < numArray.length; ii++) {
				yIndexSet.add(numArray[ii]);
			}
		}

		Integer[] xIndexArray = xIndexSet.toArray(new Integer[xIndexSet.size()]);
		final int xIndexMin = xIndexArray[0];
		final int xIndexMax = xIndexArray[xIndexArray.length - 1];
		for (int ii = xIndexMin; ii <= xIndexMax; ii++) {
			boolean b = true;
			for (SGIntegerSeries series : xSeriesList) {
				if (series.isWithinRange(ii)) {
					b = false;
					break;
				}
			}
			if (b) {
				xIndexSet.add(ii);
			}
		}
		Integer[] yIndexArray = yIndexSet.toArray(new Integer[yIndexSet.size()]);
		final int yIndexMin = yIndexArray[0];
		final int yIndexMax = yIndexArray[yIndexArray.length - 1];
		for (int ii = yIndexMin; ii <= yIndexMax; ii++) {
			boolean b = true;
			for (SGIntegerSeries series : ySeriesList) {
				if (series.isWithinRange(ii)) {
					b = false;
					break;
				}
			}
			if (b) {
				yIndexSet.add(ii);
			}
		}
		
		xIndexList.addAll(xIndexSet);
		yIndexList.addAll(yIndexSet);
	}

	public static double[][] getTwoDimensionalValues(List<SGXYSimpleDoubleValueIndexBlock> blocks,
			List<Integer> xIndexList, List<Integer> yIndexList) {
		final int xLen = xIndexList.size();
		final int yLen = yIndexList.size();
		double[][] values = new double[yLen][xLen];
		for (int ii = 0; ii < values.length; ii++) {
			Arrays.fill(values[ii], Double.NaN);
		}
		for (SGXYSimpleDoubleValueIndexBlock block : blocks) {
			SGIntegerSeries xSeries = block.getXSeries();
			SGIntegerSeries ySeries = block.getYSeries();
			final int[] xIndices = xSeries.getNumbers();
			final int[] yIndices = ySeries.getNumbers();
			final double[] blockValueArray = block.getValues();
			for (int yy = 0; yy < yIndices.length; yy++) {
				final int yIndex = yIndices[yy];
				final int yIdx = yIndexList.indexOf(yIndex);
				for (int xx = 0; xx < xIndices.length; xx++) {
					final int index = xIndices.length * yy + xx;
					final double value = blockValueArray[index];
					final int xIndex = xIndices[xx];
					final int xIdx = xIndexList.indexOf(xIndex);
					if (xIdx == -1 || yIdx == -1) {
						return null;
					}
					values[yIdx][xIdx] = value;
				}
			}
		}
		return values;
	}
	
	public static String getDataColumnTypeCommand(List<String> varList,
			List<String> columnTypeList) {
		
		if (varList.size() != columnTypeList.size()) {
			throw new IllegalArgumentException("varList.size() != columnTypeList.size()");
		}
		StringBuffer sb = new StringBuffer();
		sb.append('(');
		for (int ii = 0; ii < varList.size(); ii++) {
			if (ii > 0) {
				sb.append(',');
			}
			String varName = varList.get(ii);
			String columnType = columnTypeList.get(ii);
			sb.append(varName);
			sb.append(':');
			sb.append(columnType);
		}
		sb.append(')');
		return sb.toString();
	}

	private static String[] updateNetCDFItems(String[] items, Map<String, Object> infoMap,
			String dataType, String valueType, String[] cOptions, String[] options) {
		List<String> itemList = new ArrayList<String>();
		for (String item : items) {
			itemList.add(item);
		}
		return updateNetCDFItems(itemList, infoMap, dataType, valueType, cOptions, options);
	}

	private static String[] updateNetCDFItems(List<String> itemList, Map<String, Object> infoMap,
			String dataType, String valueType, String[] cOptions, String[] options) {
		
        // current row index
        Integer rowIndex = (Integer) infoMap.get(SGIDataInformationKeyConstants.KEY_CURRENT_ROW_INDEX);

        // all columns
        SGDataColumnInfo[] colInfo = (SGDataColumnInfo[]) infoMap.get(SGIDataInformationKeyConstants.KEY_COLUMN_INFO);

        // add time, pickup and serial number columns
        final boolean isNetCDFData = isNetCDFData(dataType);
        if (isNetCDFData && VALUE_TYPE_NUMBER.equals(valueType)) {
        	SGNetCDFDataColumnInfo ncInfo = (SGNetCDFDataColumnInfo) colInfo[rowIndex];
        	if (ncInfo.isCoordinateVariable()) {
        		for (String op : cOptions) {
        			itemList.add(op);
        		}
        	} else {
        		for (String op : options) {
        			itemList.add(op);
        		}
        	}
        }

        String[] ret = itemList.toArray(new String[itemList.size()]);
        return ret;
	}
	
    public static SGValueRange getAllAnimationFrameBoundsX(SGISXYTypeSingleData data) {
    	if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
    		return getBoundsX(data);
    	}
    	SGIntegerSeriesSet arraySection = data.getTimeStride();
    	int[] indices = arraySection.getNumbers();
    	return getAllAnimationFrameBoundsXSub(data, indices);
    }
    
    static SGValueRange getAllAnimationFrameBoundsXSub(SGISXYTypeSingleData data,
    		final int[] indices) {
    	final int curIndex = data.getCurrentTimeValueIndex();
    	List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    	for (int ii = 0; ii < indices.length; ii++) {
    		data.setCurrentTimeValueIndex(indices[ii]);
    		SGValueRange range = getBoundsX(data);
    		rangeList.add(range);
    	}
    	data.setCurrentTimeValueIndex(curIndex);
    	return getBounds(rangeList);
    }
    
    public static SGValueRange getAllAnimationFrameBoundsY(SGISXYTypeSingleData data) {
    	if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
    		return getBoundsY(data);
    	}
    	SGIntegerSeriesSet arraySection = data.getTimeStride();
    	int[] indices = arraySection.getNumbers();
    	return getAllAnimationFrameBoundsYSub(data, indices);
    }

    static SGValueRange getAllAnimationFrameBoundsYSub(SGISXYTypeSingleData data,
    		final int[] indices) {
    	if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
    		return getBoundsY(data);
    	}
    	final int curIndex = data.getCurrentTimeValueIndex();
    	List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    	for (int ii = 0; ii < indices.length; ii++) {
    		data.setCurrentTimeValueIndex(indices[ii]);
    		SGValueRange range = getBoundsY(data);
    		rangeList.add(range);
    	}
    	data.setCurrentTimeValueIndex(curIndex);
    	return getBounds(rangeList);
    }
    
    public static SGValueRange getAllAnimationFrameBoundsX(SGISXYTypeMultipleData data) {
    	if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
    		return getBoundsX(data);
    	}
    	List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    	SGIntegerSeriesSet arraySection = data.getTimeStride();
    	int[] indices = arraySection.getNumbers();
    	SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    	for (SGISXYTypeSingleData sxy : sxyArray) {
    		SGValueRange range = getAllAnimationFrameBoundsXSub(sxy, indices);
    		rangeList.add(range);
    	}
    	return getBounds(rangeList);
    }
    
    public static SGValueRange getAllAnimationFrameBoundsY(SGISXYTypeMultipleData data) {
    	if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
    		return getBoundsY(data);
    	}
    	List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    	SGIntegerSeriesSet arraySection = data.getTimeStride();
    	int[] indices = arraySection.getNumbers();
    	SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    	for (SGISXYTypeSingleData sxy : sxyArray) {
    		SGValueRange range = getAllAnimationFrameBoundsYSub(sxy, indices);
    		rangeList.add(range);
    	}
    	return getBounds(rangeList);
    }

    public static SGValueRange getAllAnimationFrameBoundsX(SGISXYZTypeData data) {
    	if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
    		return getBoundsX(data);
    	}
    	SGIntegerSeriesSet arraySection = data.getTimeStride();
    	int[] indices = arraySection.getNumbers();
    	final int curIndex = data.getCurrentTimeValueIndex();
    	List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    	for (int ii = 0; ii < indices.length; ii++) {
    		data.setCurrentTimeValueIndex(indices[ii]);
    		SGValueRange range = getBoundsX(data);
    		rangeList.add(range);
    	}
    	data.setCurrentTimeValueIndex(curIndex);
    	return getBounds(rangeList);
    }
    
    public static SGValueRange getAllAnimationFrameBoundsY(SGISXYZTypeData data) {
    	if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
    		return getBoundsY(data);
    	}
    	SGIntegerSeriesSet arraySection = data.getTimeStride();
    	int[] indices = arraySection.getNumbers();
    	final int curIndex = data.getCurrentTimeValueIndex();
    	List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    	for (int ii = 0; ii < indices.length; ii++) {
    		data.setCurrentTimeValueIndex(indices[ii]);
    		SGValueRange range = getBoundsY(data);
    		rangeList.add(range);
    	}
    	data.setCurrentTimeValueIndex(curIndex);
    	return getBounds(rangeList);
    }

    public static SGValueRange getAllAnimationFrameBoundsZ(SGISXYZTypeData data) {
    	if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
    		return getBoundsZ(data);
    	}
    	SGIntegerSeriesSet arraySection = data.getTimeStride();
    	int[] indices = arraySection.getNumbers();
    	final int curIndex = data.getCurrentTimeValueIndex();
    	List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    	for (int ii = 0; ii < indices.length; ii++) {
    		data.setCurrentTimeValueIndex(indices[ii]);
    		SGValueRange range = getBoundsZ(data);
    		rangeList.add(range);
    	}
    	data.setCurrentTimeValueIndex(curIndex);
    	return getBounds(rangeList);
    }

    public static SGValueRange getAllAnimationFrameBoundsX(SGIVXYTypeData data) {
    	if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
    		return getBoundsX(data);
    	}
    	SGIntegerSeriesSet arraySection = data.getTimeStride();
    	int[] indices = arraySection.getNumbers();
    	final int curIndex = data.getCurrentTimeValueIndex();
    	List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    	for (int ii = 0; ii < indices.length; ii++) {
    		data.setCurrentTimeValueIndex(indices[ii]);
    		SGValueRange range = getBoundsX(data);
    		rangeList.add(range);
    	}
    	data.setCurrentTimeValueIndex(curIndex);
    	return getBounds(rangeList);
    }
    
    public static SGValueRange getAllAnimationFrameBoundsY(SGIVXYTypeData data) {
    	if (!data.isAnimationSupported() || !data.isAnimationAvailable()) {
    		return getBoundsY(data);
    	}
    	SGIntegerSeriesSet arraySection = data.getTimeStride();
    	int[] indices = arraySection.getNumbers();
    	final int curIndex = data.getCurrentTimeValueIndex();
    	List<SGValueRange> rangeList = new ArrayList<SGValueRange>();
    	for (int ii = 0; ii < indices.length; ii++) {
    		data.setCurrentTimeValueIndex(indices[ii]);
    		SGValueRange range = getBoundsY(data);
    		rangeList.add(range);
    	}
    	data.setCurrentTimeValueIndex(curIndex);
    	return getBounds(rangeList);
    }

    public static Double getDataViewerValue(
    		final SGISXYTypeMultipleData sxyData, final String columnType, final int row,
    		final int col) {
    	final int rIndex;
    	if (sxyData.isStrideAvailable()) {
        	SGIntegerSeriesSet indices = sxyData.getDataViewerRowStride(columnType);
        	rIndex = indices.search(row);
        	if (rIndex < 0) {
        		return null;
        	}
    	} else {
    		rIndex = row;
    	}
    	if (SGIDataColumnTypeConstants.X_VALUE.equals(columnType)) {
    		// add the shift value
    		return sxyData.getXValueAt(col, rIndex) + sxyData.getShift().x;
    	} else if (SGIDataColumnTypeConstants.Y_VALUE.equals(columnType)) {
    		// add the shift value
    		return sxyData.getYValueAt(col, rIndex) + sxyData.getShift().y;
    	} else {
    		return null;
    	}
    }

    public static Double getDataViewerValue(
    		final SGISXYZTypeData sxyzData, final String columnType, final int row,
    		final int col) {
    	final int rIndex, cIndex;
    	if (sxyzData.isStrideAvailable()) {
        	SGIntegerSeriesSet rowStride = sxyzData.getDataViewerRowStride(columnType);
        	if (rowStride != null) {
            	rIndex = rowStride.search(row);
            	if (rIndex < 0) {
            		return null;
            	}
        	} else {
        		rIndex = row;
        	}
        	SGIntegerSeriesSet colStride = sxyzData.getDataViewerColStride(columnType);
        	if (colStride != null) {
            	cIndex = colStride.search(col);
            	if (cIndex < 0) {
            		return null;
            	}
        	} else {
        		cIndex = col;
        	}
    	} else {
    		rIndex = row;
    		cIndex = col;
    	}
    	if (SGIDataColumnTypeConstants.X_VALUE.equals(columnType)) {
    		return sxyzData.getXValueAt(rIndex);
    	} else if (SGIDataColumnTypeConstants.Y_VALUE.equals(columnType)) {
    		return sxyzData.getYValueAt(rIndex);
    	} else if (SGIDataColumnTypeConstants.Z_VALUE.equals(columnType)) {
    		if (sxyzData.isIndexAvailable()) {
        		return sxyzData.getZValueAt(rIndex);
    		} else {
        		return sxyzData.getZValueAt(row, col);
    		}
    	} else {
    		return null;
    	}
    }

    public static String getPreferredDataViewColumnType(
    		final SGISXYTypeMultipleData sxyData) {
		String ret = null;
		if (sxyData.hasMultipleYValues()) {
			ret = SGIDataColumnTypeConstants.Y_VALUE;
		} else {
			ret = SGIDataColumnTypeConstants.X_VALUE;
		}
		return ret;
	}

    public static String getPreferredDataViewColumnType(
    		final SGISXYZTypeData sxyData) {
		return SGIDataColumnTypeConstants.Z_VALUE;
	}

    public static String getPreferredDataViewColumnType(final SGIVXYTypeData vxyData) {
		final boolean polar = vxyData.isPolar();
    	final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
		return first;
	}

    public static Double getDataViewerValue(
    		final SGIVXYTypeData vxyData, final String columnType, final int row,
    		final int col) {
    	final int rIndex, cIndex;
    	if (vxyData.isStrideAvailable()) {
        	SGIntegerSeriesSet rowStride = vxyData.getDataViewerRowStride(columnType);
        	if (rowStride != null) {
            	rIndex = rowStride.search(row);
            	if (rIndex < 0) {
            		return null;
            	}
        	} else {
        		rIndex = row;
        	}
        	SGIntegerSeriesSet colStride = vxyData.getDataViewerColStride(columnType);
        	if (colStride != null) {
            	cIndex = colStride.search(col);
            	if (cIndex < 0) {
            		return null;
            	}
        	} else {
        		cIndex = col;
        	}
    	} else {
    		rIndex = row;
    		cIndex = col;
    	}
    	if (SGIDataColumnTypeConstants.X_COORDINATE.equals(columnType)) {
    		return vxyData.getXValueAt(rIndex);
    	} else if (SGIDataColumnTypeConstants.Y_COORDINATE.equals(columnType)) {
    		return vxyData.getYValueAt(rIndex);
    	} else {
    		final boolean polar = vxyData.isPolar();
    		final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
    		final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
    		if (first.equals(columnType)) {
        		if (vxyData.isIndexAvailable()) {
            		return vxyData.getFirstComponentValueAt(rIndex);
        		} else {
        			return vxyData.getFirstComponentValueAt(row, col);
        		}
    		} else if (second.equals(columnType)) {
        		if (vxyData.isIndexAvailable()) {
            		return vxyData.getSecondComponentValueAt(rIndex);
        		} else {
        			return vxyData.getSecondComponentValueAt(row, col);
        		}
    		} else {
    			return null;
    		}
    	}
    }

	public static SGDataValueHistory setDataViewerValue(
			SGISXYTypeMultipleData data,
			String columnType, final int row, final int col, Object value,
			SGIntegerSeriesSet stride) {
		
		if (value == null) {
			return null;
		}
		String text = value.toString();
		Double d = SGUtilityText.getDouble(text);
		if (d == null) {
			return null;
		}
		SGArrayData aData = (SGArrayData) data;
		if (aData.getCache() == null) {
			aData.restoreCache();
		}
		
		// subtract the shift value
		if (X_VALUE.equals(columnType)) {
			d -= data.getShift().x;
		} else if (Y_VALUE.equals(columnType)) {
			d -= data.getShift().y;
		}
		
		final int index;
		if (data.isStrideAvailable()) {
			int[] indices = stride.getNumbers();
			index = Arrays.binarySearch(indices, row);
		} else {
			index = row;
		}

		SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
		SGISXYTypeSingleData child = sxyArray[col];
		DoubleValueSetResult setResult = child.setDataViewerDoubleValue(columnType, index, d);
		SGDataUtility.disposeSXYDataArray(sxyArray);
		
		SGDataValueHistory ret = null;
		if (setResult.status) {
			if (data instanceof SGSXYNetCDFMultipleData) {
				SGSXYNetCDFMultipleData ncData = (SGSXYNetCDFMultipleData) data;
				SGNetCDFVariable cVar;
				if (ncData.isIndexAvailable()) {
					cVar = ncData.getIndexVariable();
				} else {
					SGNetCDFVariable xVar = ncData.getXVariable();
					if (xVar.isCoordinateVariable()) {
						cVar = xVar;
					} else {
						SGNetCDFVariable yVar = ncData.getYVariable();
						cVar = yVar;
					}
				}
				String dimName = cVar.getName();

				final SGNetCDFVariable var;
				if (X_VALUE.equals(columnType)) {
					SGNetCDFVariable[] vars = ncData.getXVariables();
					if (ncData.hasMultipleYValues()) {
						var = vars[0];
					} else {
						if (ncData.isDimensionPicked()) {
							var = vars[0];
						} else {
							var = vars[col];
						}
					}
				} else if (Y_VALUE.equals(columnType)) {
					SGNetCDFVariable[] vars = ncData.getYVariables();
					if (ncData.hasMultipleYValues()) {
						if (ncData.isDimensionPicked()) {
							var = vars[0];
						} else {
							var = vars[col];
						}
					} else {
						var = vars[0];
					}
				} else {
					throw new Error("Invalid column type: " + columnType);
				}
				String varName = var.getName();

				SGDataValueHistory.NetCDF.MD1 cur = new SGDataValueHistory.NetCDF.MD1(
						d, columnType, col, row, varName, setResult.prev);
				cur.setDimInfo(dimName);
				SGNetCDFPickUpDimensionInfo pickUpInfo = (SGNetCDFPickUpDimensionInfo) ncData.getPickUpDimensionInfo();
				if (pickUpInfo != null) {
					String pickUpDimName = pickUpInfo.getDimensionName();
					final int[] pickUpIndices = pickUpInfo.getIndices().getNumbers();
					final int pickUpOrigin = pickUpIndices[col];
					cur.setPickUpInfo(pickUpDimName, pickUpOrigin);
				}
				SGNetCDFVariable timeVar = ncData.getTimeVariable();
				if (timeVar != null) {
					String timeDimName = timeVar.getName();
					final int timeOrigin = ncData.getOrigin(timeDimName);
					cur.setAnimationInfo(timeDimName, timeOrigin);
				}
				ret = cur;
				
			} else if (data instanceof SGSXYMDArrayMultipleData) {
				SGSXYMDArrayMultipleData mdData = (SGSXYMDArrayMultipleData) data;
				SGMDArrayVariable xVar = mdData.getXVariable();
				SGMDArrayVariable yVar = mdData.getYVariable();
				SGMDArrayVariable var = null;
				if (X_VALUE.equals(columnType)) {
					var = xVar;
				} else if (Y_VALUE.equals(columnType)) {
					var = yVar;
				}
				String varName = var.getName();
				final int dimension = var.getGenericDimensionIndex();

				SGDataValueHistory.MDArray.MD1 cur = new SGDataValueHistory.MDArray.MD1(
						d, columnType, col, row, varName, setResult.prev);
				cur.setDimension(dimension);
				Integer pickUpDimension = var.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
				if (pickUpDimension != null && pickUpDimension != -1) {
					SGMDArrayPickUpDimensionInfo pickUpInfo = (SGMDArrayPickUpDimensionInfo) mdData.getPickUpDimensionInfo();
					final int[] pickUpIndices = pickUpInfo.getIndices().getNumbers();
					final int pickUpOrigin = pickUpIndices[col];
					cur.setPickUpInfo(pickUpDimension, pickUpOrigin);
				}
				Integer timeDimension = var.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
				if (timeDimension != null && timeDimension != -1) {
					final int timeOrigin = var.getOrigin(timeDimension);
					cur.setAnimationInfo(timeDimension, timeOrigin);
				}
				ret = cur;

			} else {
				ret = new SGDataValueHistory.SDArray.MD1(d, columnType, col, row, setResult.prev);
			}
		}
		return ret;
	}
	
	private static SGDataValueHistory createTwoDimensionalDataValueHistory(
			SGTwoDimensionalNetCDFData ncData, Double d, String columnType,
			final int col, final int row, SGNetCDFVariable var, final double prev) {
		String varName = var.getName();
		SGDataValueHistory.NetCDF.D2 curValue = new SGDataValueHistory.NetCDF.D2(
				d, columnType, col, row, varName, prev);
		SGNetCDFVariable xVar = ncData.getXVariable();
		String xDimName = xVar.getName();
		SGNetCDFVariable yVar = ncData.getYVariable();
		String yDimName = yVar.getName();
		curValue.setXYDimName(xDimName, yDimName);
		SGNetCDFVariable timeVar = ncData.getTimeVariable();
		if (timeVar != null) {
			String timeDimName = timeVar.getName();
			final int timeOrigin = ncData.getOrigin(timeDimName);
			curValue.setAnimationInfo(timeDimName, timeOrigin);
		}
		return curValue;
	}
	
	private static SGDataValueHistory createTwoDimensionalDataValueHistory(
			SGMDArrayData mdData, Double d, String columnType,
			final int col, final int row, SGMDArrayVariable var, final double prev) {
		String varName = var.getName();
		SGDataValueHistory.MDArray.D2 curValue = new SGDataValueHistory.MDArray.D2(
				d, columnType, col, row, varName, prev);
		final int xDim = var
				.getDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION);
		final int yDim = var
				.getDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION);
		curValue.setXYDimension(xDim, yDim);
		return curValue;
	}
	
	private static SGDataValueHistory createOneDimensionalDataValueHistory(
			SGNetCDFData ncData, Double d, String columnType, final int col,
			final int row, SGNetCDFVariable var, final double prev) {
		String varName = var.getName();
		SGDataValueHistory.NetCDF.D1 curValue = new SGDataValueHistory.NetCDF.D1(
				d, columnType, row, varName, prev);
		return curValue;
	}

	private static SGDataValueHistory createIndexDataValueHistory(
			SGNetCDFData ncData, Double d, String columnType, final int col,
			final int row, SGNetCDFVariable var, final double prev) {
		String varName = var.getName();
		SGDataValueHistory.NetCDF.D1 curValue = new SGDataValueHistory.NetCDF.D1(
				d, columnType, row, varName, prev);
		curValue.setIndexDimName(varName);
		SGNetCDFVariable timeVar = ncData.getTimeVariable();
		if (timeVar != null) {
			String timeDimName = timeVar.getName();
			final int timeOrigin = ncData.getOrigin(timeDimName);
			curValue.setAnimationInfo(timeDimName, timeOrigin);
		}
		return curValue;
	}

	private static SGDataValueHistory createOneDimensionalDataValueHistory(
			SGMDArrayData mdData, Double d, String columnType, final int col,
			final int row, SGMDArrayVariable var, final double prev) {
		String varName = var.getName();
		SGDataValueHistory.MDArray.D1 curValue = new SGDataValueHistory.MDArray.D1(
				d, columnType, row, varName, prev);
		final int dimension = var.getGenericDimensionIndex();
		curValue.setDimension(dimension);
		return curValue;
	}

	public static SGDataValueHistory setDataViewerValue(SGISXYZTypeData data, String columnType, 
			final int row, final int col, Object value) {

		if (value == null) {
			return null;
		}
		String text = value.toString();
		Double d = SGUtilityText.getDouble(text);
		if (d == null) {
			return null;
		}
		SGArrayData aData = (SGArrayData) data;
		if (aData.getCache() == null) {
			aData.restoreCache();
		}
		SGSXYZDataCache cache = SGSXYZDataCache.getCache(aData);
		if (cache == null) {
			return null;
		}
		
		SGDataValueHistory ret = null;
		if (data.isIndexAvailable()) {
			final int index;
			if (data.isStrideAvailable()) {
				int[] indices = data.getIndexStride().getNumbers();
				index = Arrays.binarySearch(indices, row);
			} else {
				index = row;
			}
			boolean diff = false;
			double prev = 0.0;
			if (X_VALUE.equals(columnType)) {
				if (index < cache.mXValues.length) {
					if (cache.mXValues[index] != d) {
						prev = cache.mXValues[index];
						cache.mXValues[index] = d;
						diff = true;
					}
				}
			} else if (Y_VALUE.equals(columnType)) {
				if (index < cache.mYValues.length) {
					if (cache.mYValues[index] != d) {
						prev = cache.mYValues[index];
						cache.mYValues[index] = d;
						diff = true;
					}
				}
			} else if (Z_VALUE.equals(columnType)) {
				if (index < cache.mZValues.length) {
					if (cache.mZValues[index] != d) {
						prev = cache.mZValues[index];
						cache.mZValues[index] = d;
						diff = true;
					}
				}
			} else {
				return null;
			}
			if (diff) {
				if (data instanceof SGSXYZNetCDFData) {
					SGSXYZNetCDFData ncData = (SGSXYZNetCDFData) data;
					SGNetCDFVariable xVar = ncData.getXVariable();
					SGNetCDFVariable yVar = ncData.getYVariable();
					SGNetCDFVariable zVar = ncData.getZVariable();
					SGNetCDFVariable var = null;
					if (X_VALUE.equals(columnType)) {
						var = xVar;
					} else if (Y_VALUE.equals(columnType)) {
						var = yVar;
					} else if (Z_VALUE.equals(columnType)) {
						var = zVar;
					}
					ret = createIndexDataValueHistory(ncData, d, 
							columnType, col, row, var, prev);
				} else if (data instanceof SGSXYZMDArrayData) {
					SGSXYZMDArrayData mdData = (SGSXYZMDArrayData) data;
					SGMDArrayVariable xVar = mdData.getXVariable();
					SGMDArrayVariable yVar = mdData.getYVariable();
					SGMDArrayVariable zVar = mdData.getZVariable();
					SGMDArrayVariable var = null;
					if (X_VALUE.equals(columnType)) {
						var = xVar;
					} else if (Y_VALUE.equals(columnType)) {
						var = yVar;
					} else if (Z_VALUE.equals(columnType)) {
						var = zVar;
					}
					ret = createOneDimensionalDataValueHistory(mdData, d, 
							columnType, col, row, var, prev);
				} else {
					ret = new SGDataValueHistory.SDArray.D1(d, columnType, row, prev);
				}
			}
			
		} else {
			
			if (X_VALUE.equals(columnType)) {
				final int index;
				if (data.isStrideAvailable()) {
					int[] indices = data.getXStride().getNumbers();
					index = Arrays.binarySearch(indices, row);
				} else {
					index = row;
				}
				if (index < cache.mXValues.length) {
					if (cache.mXValues[index] != d) {
						final double prev = cache.mXValues[index];
						cache.mXValues[index] = d;
						if (data instanceof SGSXYZNetCDFData) {
							SGSXYZNetCDFData ncData = (SGSXYZNetCDFData) data;
							SGNetCDFVariable var = ncData.getXVariable();
							ret = createOneDimensionalDataValueHistory(ncData,
									d, columnType, col, row, var, prev);
						} else if (data instanceof SGSXYZMDArrayData) {
							SGSXYZMDArrayData mdData = (SGSXYZMDArrayData) data;
							SGMDArrayVariable var = mdData.getXVariable();
							ret = createOneDimensionalDataValueHistory(mdData,
									d, columnType, col, row, var, prev);
						}
					}
				}
				
			} else if (Y_VALUE.equals(columnType)) {
				final int index;
				if (data.isStrideAvailable()) {
					int[] indices = data.getYStride().getNumbers();
					index = Arrays.binarySearch(indices, row);
				} else {
					index = row;
				}
				if (index < cache.mYValues.length) {
					if (cache.mYValues[index] != d) {
						final double prev = cache.mYValues[index];
						cache.mYValues[index] = d;
						if (data instanceof SGSXYZNetCDFData) {
							SGSXYZNetCDFData ncData = (SGSXYZNetCDFData) data;
							SGNetCDFVariable var = ncData.getYVariable();
							ret = createOneDimensionalDataValueHistory(ncData,
									d, columnType, col, row, var, prev);
						} else if (data instanceof SGSXYZMDArrayData) {
							SGSXYZMDArrayData mdData = (SGSXYZMDArrayData) data;
							SGMDArrayVariable var = mdData.getYVariable();
							ret = createOneDimensionalDataValueHistory(mdData,
									d, columnType, col, row, var, prev);
						}
					}
				}
				
			} else if (Z_VALUE.equals(columnType)) {
				for (SGXYSimpleDoubleValueIndexBlock block : cache.mZValueBlockList) {
					final Double prev = block.getValue(col, row);
					if (block.setValue(d, col, row)) {
						if (data instanceof SGSXYZNetCDFData) {
							SGSXYZNetCDFData ncData = (SGSXYZNetCDFData) data;
							SGNetCDFVariable var = ncData.getZVariable();
							ret = createTwoDimensionalDataValueHistory(ncData, d, 
									columnType, col, row, var, prev);
						} else if (data instanceof SGSXYZMDArrayData) {
							SGSXYZMDArrayData mdData = (SGSXYZMDArrayData) data;
							SGMDArrayVariable var = mdData.getZVariable();
							ret = createTwoDimensionalDataValueHistory(mdData, d, 
									columnType, col, row, var, prev);
						}
						break;
					}
				}
			}
		}

		return ret;
	}
	
	public static SGDataValueHistory setDataViewerValue(SGIVXYTypeData data, String columnType, 
			final int row, final int col, Object value) {

		if (value == null) {
			return null;
		}
		String text = value.toString();
		Double d = SGUtilityText.getDouble(text);
		if (d == null) {
			return null;
		}
		SGArrayData aData = (SGArrayData) data;
		if (aData.getCache() == null) {
			aData.restoreCache();
		}
		SGVXYDataCache cache = SGVXYDataCache.getCache(aData);
		if (cache == null) {
			return null;
		}
		final boolean polar = data.isPolar();
		final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
		final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
		
		SGDataValueHistory ret = null;
		if (data.isIndexAvailable()) {
			final int index;
			if (data.isStrideAvailable()) {
				int[] indices = data.getIndexStride().getNumbers();
				index = Arrays.binarySearch(indices, row);
			} else {
				index = row;
			}
			boolean diff = false;
			double prev = 0.0;
			if (X_COORDINATE.equals(columnType)) {
				if (index < cache.mXValues.length) {
					if (cache.mXValues[index] != d) {
						prev = cache.mXValues[index];
						cache.mXValues[index] = d;
						diff = true;
					}
				}
			} else if (Y_COORDINATE.equals(columnType)) {
				if (index < cache.mYValues.length) {
					if (cache.mYValues[index] != d) {
						prev = cache.mYValues[index];
						cache.mYValues[index] = d;
						diff = true;
					}
				}
			} else if (first.equals(columnType)) {
				if (index < cache.mFirstComponentValues.length) {
					if (cache.mFirstComponentValues[index] != d) {
						prev = cache.mFirstComponentValues[index];
						cache.mFirstComponentValues[index] = d;
						diff = true;
					}
				}
			} else if (second.equals(columnType)) {
				if (index < cache.mSecondComponentValues.length) {
					if (cache.mSecondComponentValues[index] != d) {
						prev = cache.mSecondComponentValues[index];
						cache.mSecondComponentValues[index] = d;
						diff = true;
					}
				}
			} else {
				return null;
			}
			if (diff) {
				if (data instanceof SGVXYNetCDFData) {
					SGVXYNetCDFData ncData = (SGVXYNetCDFData) data;
					SGNetCDFVariable xVar = ncData.getXVariable();
					SGNetCDFVariable yVar = ncData.getYVariable();
					SGNetCDFVariable fVar = ncData.getFirstComponentVariable();
					SGNetCDFVariable sVar = ncData.getSecondComponentVariable();
					SGNetCDFVariable var = null;
					if (X_COORDINATE.equals(columnType)) {
						var = xVar;
					} else if (Y_COORDINATE.equals(columnType)) {
						var = yVar;
					} else if (first.equals(columnType)) {
						var = fVar;
					} else if (second.equals(columnType)) {
						var = sVar;
					}
					ret = createIndexDataValueHistory(ncData, d, 
							columnType, col, row, var, prev);
				} else if (data instanceof SGVXYMDArrayData) {
					SGVXYMDArrayData mdData = (SGVXYMDArrayData) data;
					SGMDArrayVariable xVar = mdData.getXVariable();
					SGMDArrayVariable yVar = mdData.getYVariable();
					SGMDArrayVariable fVar = mdData.getFirstComponentVariable();
					SGMDArrayVariable sVar = mdData.getSecondComponentVariable();
					SGMDArrayVariable var = null;
					if (X_COORDINATE.equals(columnType)) {
						var = xVar;
					} else if (Y_COORDINATE.equals(columnType)) {
						var = yVar;
					} else if (first.equals(columnType)) {
						var = fVar;
					} else if (second.equals(columnType)) {
						var = sVar;
					}
					ret = createOneDimensionalDataValueHistory(mdData, d, 
							columnType, col, row, var, prev);
				} else {
					ret = new SGDataValueHistory.SDArray.D1(d, columnType, row, prev);
				} 
			}
			
		} else {
			if (X_COORDINATE.equals(columnType)) {
				final int index;
				if (data.isStrideAvailable()) {
					int[] indices = data.getXStride().getNumbers();
					index = Arrays.binarySearch(indices, row);
				} else {
					index = row;
				}
				if (index < cache.mXValues.length) {
					if (cache.mXValues[index] != d) {
						final double prev = cache.mXValues[index];
						cache.mXValues[index] = d;
						if (data instanceof SGVXYNetCDFData) {
							SGVXYNetCDFData ncData = (SGVXYNetCDFData) data;
							SGNetCDFVariable var = ncData.getXVariable();
							ret = createOneDimensionalDataValueHistory(ncData,
									d, columnType, col, row, var, prev);
						} else if (data instanceof SGVXYMDArrayData) {
							SGVXYMDArrayData mdData = (SGVXYMDArrayData) data;
							SGMDArrayVariable var = mdData.getXVariable();
							ret = createOneDimensionalDataValueHistory(mdData,
									d, columnType, col, row, var, prev);
						}
					}
				}
				
			} else if (Y_COORDINATE.equals(columnType)) {
				final int index;
				if (data.isStrideAvailable()) {
					int[] indices = data.getYStride().getNumbers();
					index = Arrays.binarySearch(indices, row);
				} else {
					index = row;
				}
				if (index < cache.mYValues.length) {
					if (cache.mYValues[index] != d) {
						final double prev = cache.mYValues[index];
						cache.mYValues[index] = d;
						if (data instanceof SGVXYNetCDFData) {
							SGVXYNetCDFData ncData = (SGVXYNetCDFData) data;
							SGNetCDFVariable var = ncData.getYVariable();
							ret = createOneDimensionalDataValueHistory(ncData,
									d, columnType, col, row, var, prev);
						} else if (data instanceof SGVXYMDArrayData) {
							SGVXYMDArrayData mdData = (SGVXYMDArrayData) data;
							SGMDArrayVariable var = mdData.getYVariable();
							ret = createOneDimensionalDataValueHistory(mdData,
									d, columnType, col, row, var, prev);
						}
					}
				}
				
			} else if (first.equals(columnType)) {
				for (SGXYSimpleDoubleValueIndexBlock block : cache.mFirstComponentValueBlockList) {
					final Double prev = block.getValue(col, row);
					if (block.setValue(d, col, row)) {
						if (data instanceof SGVXYNetCDFData) {
							SGVXYNetCDFData ncData = (SGVXYNetCDFData) data;
							SGNetCDFVariable var = ncData.getFirstComponentVariable();
							ret = createTwoDimensionalDataValueHistory(ncData, d, 
									columnType, col, row, var, prev);
						} else if (data instanceof SGVXYMDArrayData) {
							SGVXYMDArrayData mdData = (SGVXYMDArrayData) data;
							SGMDArrayVariable var = mdData.getFirstComponentVariable();
							ret = createTwoDimensionalDataValueHistory(mdData, d, 
									columnType, col, row, var, prev);
						}
						break;
					}
				}
			} else if (second.equals(columnType)) {
				for (SGXYSimpleDoubleValueIndexBlock block : cache.mSecondComponentValueBlockList) {
					final Double prev = block.getValue(col, row);
					if (block.setValue(d, col, row)) {
						if (data instanceof SGVXYNetCDFData) {
							SGVXYNetCDFData ncData = (SGVXYNetCDFData) data;
							SGNetCDFVariable var = ncData.getSecondComponentVariable();
							ret = createTwoDimensionalDataValueHistory(ncData, d, 
									columnType, col, row, var, prev);
						} else if (data instanceof SGVXYMDArrayData) {
							SGVXYMDArrayData mdData = (SGVXYMDArrayData) data;
							SGMDArrayVariable var = mdData.getSecondComponentVariable();
							ret = createTwoDimensionalDataValueHistory(mdData, d, 
									columnType, col, row, var, prev);
						}
						break;
					}
				}
			}
		}
		
		return ret;
	}

	public static void updateCache(SGISXYTypeMultipleData multipleData,
			SGISXYTypeSingleData[] sxyDataArray) {
		final int cNum = multipleData.getChildNumber();
		if (sxyDataArray.length != cNum) {
			throw new IllegalArgumentException("sxyDataArray.length != cNum : " 
					+ sxyDataArray.length + ", " + cNum);
		}
		SGSXYMultipleDataCache cache = new SGSXYMultipleDataCache();
		cache.mCacheArray = new SGSXYDataCache[cNum];
		for (int ii = 0; ii < cNum; ii++) {
			SGDataCache c = ((SGArrayData) sxyDataArray[ii]).getCache();
			cache.mCacheArray[ii] = (SGSXYDataCache) c;
		}
		((SGArrayData) multipleData).setCache(cache);
	}

    public static double[] getXValueArray(SGISXYTypeSingleData data, final boolean all) {
    	double[] ret = null;
    	final boolean useCache = data.useValueCache(all);
    	if (useCache) {
    		ret = SGSXYDataCache.getXValues((SGArrayData) data);
    	}
    	if (ret == null) {
    		ret = data.getXValueArray(all, useCache);
    	}
    	return ret;
    }

    public static double[] getYValueArray(SGISXYTypeSingleData data, final boolean all) {
    	double[] ret = null;
    	final boolean useCache = data.useValueCache(all);
    	if (useCache) {
    		ret = SGSXYDataCache.getYValues((SGArrayData) data);
    	}
    	if (ret == null) {
    		ret = data.getYValueArray(all, useCache);
    	}
    	return ret;
    }

    public static double[] getLowerErrorValueArray(SGISXYTypeSingleData data, final boolean all) {
        if (!data.isErrorBarAvailable()) {
        	return null;
        }
    	double[] ret = null;
    	final boolean useCache = data.useValueCache(all);
    	if (useCache) {
    		ret = SGSXYDataCache.getLowerErrorValues((SGArrayData) data);
    	}
    	if (ret == null) {
    		ret = data.getLowerErrorValueArray(all, useCache);
    	}
        return ret;
    }
    
    public static double[] getUpperErrorValueArray(SGISXYTypeSingleData data, final boolean all) {
        if (!data.isErrorBarAvailable()) {
        	return null;
        }
    	double[] ret = null;
    	final boolean useCache = data.useValueCache(all);
    	if (useCache) {
    		ret = SGSXYDataCache.getUpperErrorValues((SGArrayData) data);
    	}
    	if (ret == null) {
    		ret = data.getUpperErrorValueArray(all, useCache);
    	}
        return ret;
    }

    public static String[] getStringArray(SGISXYTypeSingleData data, final boolean all) {
        if (!data.isTickLabelAvailable()) {
        	return null;
        }
        String[] ret = null;
    	final boolean useCache = data.useTickLabelCache(all);
        if (useCache) {
        	ret = SGSXYDataCache.getTickLabels((SGArrayData) data);
        }
        if (ret == null) {
        	ret = data.getStringArray(all, useCache);
        }
        if (ret == null) {
        	return null;
        }
//        if (data.isStrideAvailable() && !all) {
//            int[] indices = data.getTickLabelStride().getNumbers();
//            String[] strArray = new String[indices.length];
//            for (int ii = 0; ii < strArray.length; ii++) {
//            	strArray[ii] = ret[indices[ii]];
//            }
//            ret = strArray;
//        }
    	return ret;
    }

    public static double[] getXValueArray(SGISXYZTypeData data, final boolean all) {
		double[] ret = null;
    	final boolean useCache = data.useXValueCache(all);
    	if (useCache) {
    		ret = SGSXYZDataCache.getXValues((SGArrayData) data);
    	}
    	if (ret == null) {
    		ret = data.getXValueArray(all, useCache);
    	}
    	return ret;
    }
    
    public static double[] getYValueArray(SGISXYZTypeData data, final boolean all) {
		double[] ret = null;
    	final boolean useCache = data.useYValueCache(all);
    	if (useCache) {
    		ret = SGSXYZDataCache.getYValues((SGArrayData) data);
    	}
    	if (ret == null) {
    		ret = data.getYValueArray(all, useCache);
    	}
        return ret;
    }
    
    public static double[] getXValueArray(SGIVXYTypeData data, final boolean all) {
    	double[] ret = null;
    	final boolean useCache = data.useXValueCache(all);
    	if (useCache) {
    		ret = SGVXYDataCache.getXValues((SGArrayData) data);
    	}
    	if (ret == null) {
    		ret = data.getXValueArray(all, useCache);
    	}
    	return ret;
    }
    
    public static double[] getYValueArray(SGIVXYTypeData data, final boolean all) {
    	double[] ret = null;
    	final boolean useCache = data.useYValueCache(all);
    	if (useCache) {
    		ret = SGVXYDataCache.getYValues((SGArrayData) data);
    	}
    	if (ret == null) {
    		ret = data.getYValueArray(all, useCache);
    	}
    	return ret;
    }

	public static double[] updateXValueArray(
			SGITwoDimensionalData data, final boolean all,
			final double[] values) {
		double[] ret = SGUtility.copyDoubleArray(values);
    	if (all && data.isStrideAvailable() && !data.isIndexAvailable()) {
    		final SGIntegerSeriesSet stride = data.getXStride();
        	final boolean useCache = data.useXYCache(all, stride);
        	if (!useCache) {
                // update values with cache
        		if (!stride.isComplete()) {
                    final double[] cachedValues = data.getXValueArray(false);
            		int[] indices = stride.getNumbers();
            		for (int ii = 0; ii < indices.length; ii++) {
            			ret[indices[ii]] = cachedValues[ii];
            		}
        		}
        	}
    	}
        return ret;
    }

	public static double[] updateYValueArray(
			SGITwoDimensionalData data, final boolean all,
			final double[] values) {
		double[] ret = SGUtility.copyDoubleArray(values);
    	if (all && data.isStrideAvailable() && !data.isIndexAvailable()) {
    		final SGIntegerSeriesSet stride = data.getYStride();
        	final boolean useCache = data.useXYCache(all, stride);
        	if (!useCache) {
                // update values with cache
        		if (!stride.isComplete()) {
                    final double[] cachedValues = data.getYValueArray(false);
            		int[] indices = stride.getNumbers();
            		for (int ii = 0; ii < indices.length; ii++) {
            			ret[indices[ii]] = cachedValues[ii];
            		}
        		}
        	}
    	}
    	return ret;
	}
	
    public static double[] getZValueArray(SGISXYZTypeData data, final boolean all) {
    	double[] ret = null;
    	final boolean useCache = data.useZValueCache(all);
    	if (useCache) {
    		ret = SGSXYZDataCache.getZValues((SGArrayData) data);
    	}
    	if (ret == null) {
    		ret = data.getZValueArray(all, useCache);
    	}
        return ret;
    }

    public static List<SGXYSimpleDoubleValueIndexBlock> getZValueBlockList(
    		SGISXYZTypeData data, final boolean all, final boolean useCache, 
    		final boolean removeInvalidValues) {
        if (data.isIndexAvailable()) {
        	throw new Error("Not supported.");
        }
		List<SGXYSimpleDoubleValueIndexBlock> ret = null;
		if (useCache) {
			ret = SGSXYZDataCache.getZValueBlockList((SGArrayData) data);
		}
		if (ret == null) {
			ret = data.getZValueBlockListSub(all, useCache, removeInvalidValues);
		}
        return ret;
	}

    public static double[] getFirstComponentValueArray(SGIVXYTypeData data, final boolean all) {
    	double[] ret = null;
    	final boolean useCache = data.useFirstComponentValueCache(all);
    	if (useCache) {
    		ret = SGVXYDataCache.getFirstComponentValues((SGArrayData) data);
    	}
    	if (ret == null) {
    		ret = data.getFirstComponentValueArray(all, useCache);
    	}
        return ret;
    }

    public static double[] getSecondComponentValueArray(SGIVXYTypeData data, final boolean all) {
    	double[] ret = null;
    	final boolean useCache = data.useSecondComponentValueCache(all);
    	if (useCache) {
    		ret = SGVXYDataCache.getSecondComponentValues((SGArrayData) data);
    	}
    	if (ret == null) {
    		ret = data.getSecondComponentValueArray(all, useCache);
    	}
        return ret;
    }

    public static List<SGXYSimpleDoubleValueIndexBlock> getFirstComponentValueBlockList(
    		SGIVXYTypeData data, final boolean all, final boolean useCache, 
    		final boolean removeInvalidValues) {
        if (data.isIndexAvailable()) {
        	throw new Error("Not supported.");
        }
		List<SGXYSimpleDoubleValueIndexBlock> ret = null;
		if (useCache) {
			ret = SGVXYDataCache.getFirstComponentValueBlockList((SGArrayData) data);
		}
		if (ret == null) {
			ret = data.getFirstComponentValueBlockListSub(all, useCache, 
					removeInvalidValues);
		}
        return ret;
    }

    public static List<SGXYSimpleDoubleValueIndexBlock> getSecondComponentValueBlockList(
    		SGIVXYTypeData data, final boolean all, final boolean useCache, 
    		final boolean removeInvalidValues) {
        if (data.isIndexAvailable()) {
        	throw new Error("Not supported.");
        }
		List<SGXYSimpleDoubleValueIndexBlock> ret = null;
		if (useCache) {
			ret = SGVXYDataCache.getSecondComponentValueBlockList((SGArrayData) data);
		}
		if (ret == null) {
            ret = data.getSecondComponentValueBlockListSub(all, useCache, 
            		removeInvalidValues);
		}
        return ret;
    }

    public static boolean isValidTooltipTextString(final String str) {
    	return (str != null) && !"".equals(str);
    }
    
	public static SGDataValue getDataValue(SGISXYTypeSingleData data, 
			final int index) {
		SXYSingleDataValue ret = new SXYSingleDataValue();
    	final int arrayIndex;
    	if (data.isStrideAvailable()) {
        	SGIntegerSeriesSet stride = data.isIndexAvailable() ? data.getIndexStride() : data.getStride();
    		int[] indices = stride.getNumbers();
    		arrayIndex = Arrays.binarySearch(indices, index);
    	} else {
    		arrayIndex = index;
    	}
		ret.xValue = data.getXValueAt(arrayIndex);
		ret.yValue = data.getYValueAt(arrayIndex);
		return ret;
	}

	public static SGDataValue getDataValue(SGISXYTypeSingleData data, 
			final int index0, final int index1) {
		SXYDoubleDataValue ret = new SXYDoubleDataValue();
    	final int arrayIndex0, arrayIndex1;
    	if (data.isStrideAvailable()) {
        	SGIntegerSeriesSet stride = data.isIndexAvailable() ? data.getIndexStride() : data.getStride();
    		int[] indices = stride.getNumbers();
    		arrayIndex0 = Arrays.binarySearch(indices, index0);
    		arrayIndex1 = Arrays.binarySearch(indices, index1);
    	} else {
    		arrayIndex0 = index0;
    		arrayIndex1 = index1;
    	}
		ret.xValue0 = data.getXValueAt(arrayIndex0);
		ret.yValue0 = data.getYValueAt(arrayIndex0);
		ret.xValue1 = data.getXValueAt(arrayIndex1);
		ret.yValue1 = data.getYValueAt(arrayIndex1);
		return ret;
	}

	public static SGDataValue getDataValue(SGISXYZTypeData data, final int index) {
		if (!data.isIndexAvailable()) {
			return null;
		}
		final int arrayIndex;
    	if (data.isStrideAvailable()) {
    		SGIntegerSeriesSet stride = data.getIndexStride();
    		int[] indices = stride.getNumbers();
    		arrayIndex = Arrays.binarySearch(indices, index);
    	} else {
    		arrayIndex = index;
    	}
		SXYZDataValue ret = new SXYZDataValue();
		ret.xValue = data.getXValueAt(arrayIndex);
		ret.yValue = data.getYValueAt(arrayIndex);
		ret.zValue = new Value(data.getZValueAt(arrayIndex));
		return ret;
	}

	public static SGDataValue getDataValue(SGISXYZTypeData data, 
			final int xIndex, final int yIndex) {
		if (data.isIndexAvailable()) {
			return null;
		}
    	final int xArrayIndex, yArrayIndex;
    	if (data.isStrideAvailable()) {
    		SGIntegerSeriesSet xStride = data.getXStride();
    		int[] xIndices = xStride.getNumbers();
    		SGIntegerSeriesSet yStride = data.getYStride();
    		int[] yIndices = yStride.getNumbers();
    		xArrayIndex = Arrays.binarySearch(xIndices, xIndex);
    		yArrayIndex = Arrays.binarySearch(yIndices, yIndex);
    	} else {
    		xArrayIndex = xIndex;
    		yArrayIndex = yIndex;
    	}

		Double xValue = data.getXValueAt(xArrayIndex);
		Double yValue = data.getYValueAt(yArrayIndex);
		Value zValue = new Value(data.getZValueAt(yIndex, xIndex));

		SXYZDataValue ret = new SXYZDataValue();
		ret.xValue = xValue;
		ret.yValue = yValue;
		ret.zValue = zValue;
		return ret;
	}

	public static SGDataValue getDataValue(SGIVXYTypeData data, final int index) {
		if (!data.isIndexAvailable()) {
			return null;
		}
		VXYDataValue ret = new VXYDataValue();
		final int arrayIndex;
    	if (data.isStrideAvailable()) {
    		SGIntegerSeriesSet stride = data.getIndexStride();
    		int[] indices = stride.getNumbers();
    		arrayIndex = Arrays.binarySearch(indices, index);
    	} else {
    		arrayIndex = index;
    	}
		ret.xValue = data.getXValueAt(arrayIndex);
		ret.yValue = data.getYValueAt(arrayIndex);
		ret.fValue = new Value(data.getFirstComponentValueAt(arrayIndex));
		ret.sValue = new Value(data.getSecondComponentValueAt(arrayIndex));
		return ret;
	}

	public static SGDataValue getDataValue(SGIVXYTypeData data, 
			final int xIndex, final int yIndex) {
		if (data.isIndexAvailable()) {
			return null;
		}
    	final int xArrayIndex, yArrayIndex;
    	if (data.isStrideAvailable()) {
    		SGIntegerSeriesSet xStride = data.getXStride();
    		int[] xIndices = xStride.getNumbers();
    		SGIntegerSeriesSet yStride = data.getYStride();
    		int[] yIndices = yStride.getNumbers();
    		xArrayIndex = Arrays.binarySearch(xIndices, xIndex);
    		yArrayIndex = Arrays.binarySearch(yIndices, yIndex);
    	} else {
    		xArrayIndex = xIndex;
    		yArrayIndex = yIndex;
    	}
		VXYDataValue ret = new VXYDataValue();
		ret.xValue = data.getXValueAt(xArrayIndex);
		ret.yValue = data.getYValueAt(yArrayIndex);
		ret.fValue = new Value(data.getFirstComponentValueAt(yIndex, xIndex));
		ret.sValue = new Value(data.getSecondComponentValueAt(yIndex, xIndex));
		return ret;
	}

	public static boolean matches(final int row, String columnType,
			SGDataValueHistory value, final double d) {
		if (value.getRowIndex() != row) {
			return false;
		}
		if (!value.getColumnType().equals(columnType)) {
			return false;
		}
		return true;
	}

	public static boolean matches(final int row, final int col, String columnType,
			SGDataValueHistory value, final double d) {
		if (value.getRowIndex() != row) {
			return false;
		}
		if (value.getColumnIndex() != col) {
			return false;
		}
		if (!value.getColumnType().equals(columnType)) {
			return false;
		}
		return true;
	}

	public static void syncDataValueHistory(List<SGDataValueHistory> historyList,
			SGISXYTypeSingleData[] dataArray) {
        for (int ii = 0; ii < historyList.size(); ii++) {
        	SGDataValueHistory dataValue = historyList.get(ii);
        	SGDataValueHistory.IMultiple multipleDataValue = (SGDataValueHistory.IMultiple) historyList.get(ii);
        	final int childIndex = multipleDataValue.getChildIndex();
        	dataArray[childIndex].addMultipleDimensionEditedDataValue(dataValue);
        }
	}

	public static double[][] getXValueArray(SGISXYTypeMultipleData data,
			boolean all) {
		
    	SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    	double[][] ret = new double[sxyArray.length][];
    	for (int ii = 0; ii < ret.length; ii++) {
    		ret[ii] = sxyArray[ii].getXValueArray(all);
    	}
    	
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);

		return ret;
    }

	public static double[][] getXValueArray(SGISXYTypeMultipleData data,
			boolean all, boolean useCache, boolean removeInvalidValues) {
		
    	SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    	double[][] ret = new double[sxyArray.length][];
    	for (int ii = 0; ii < ret.length; ii++) {
    		ret[ii] = sxyArray[ii].getXValueArray(all, useCache, removeInvalidValues);
    	}
    	
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);

		return ret;
	}

	public static double[][] getXValueArray(SGISXYTypeMultipleData data,
			boolean all, boolean useCache) {
		
    	SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    	double[][] ret = new double[sxyArray.length][];
    	for (int ii = 0; ii < ret.length; ii++) {
    		ret[ii] = sxyArray[ii].getXValueArray(all, useCache);
    	}
    	
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);

		return ret;
	}

	public static double[][] getYValueArray(SGISXYTypeMultipleData data,
			boolean all) {
		
    	SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    	double[][] ret = new double[sxyArray.length][];
    	for (int ii = 0; ii < ret.length; ii++) {
    		ret[ii] = sxyArray[ii].getYValueArray(all);
    	}
    	
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);

		return ret;
    }

	public static double[][] getYValueArray(SGISXYTypeMultipleData data,
			boolean all, boolean useCache, boolean removeInvalidValues) {
		
    	SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    	double[][] ret = new double[sxyArray.length][];
    	for (int ii = 0; ii < ret.length; ii++) {
    		ret[ii] = sxyArray[ii].getYValueArray(all, useCache, removeInvalidValues);
    	}
    	
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);

		return ret;
	}

	public static double[][] getYValueArray(SGISXYTypeMultipleData data,
			boolean all, boolean useCache) {
		
    	SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    	double[][] ret = new double[sxyArray.length][];
    	for (int ii = 0; ii < ret.length; ii++) {
    		ret[ii] = sxyArray[ii].getYValueArray(all, useCache);
    	}
    	
    	// disposes of data objects
        SGDataUtility.disposeSXYDataArray(sxyArray);

		return ret;
	}

	public static int getDataViewerColumnNumber(
			final SGISXYTypeMultipleData data, final String columnType) {
		final int num;
		if (Y_VALUE.equals(columnType)) {
			if (data.hasMultipleYValues()) {
				num = data.getChildNumber();
			} else {
				num = 1;
			}
		} else {
			if (!data.hasMultipleYValues()) {
				num = data.getChildNumber();
			} else {
				num = 1;
			}
		}
		return num;
	}

	public static List<SGDataValueHistory> getEditedDataValueList(
			SGISXYTypeMultipleData data, String columnType, final int row,
			final int col, Object value, SGIntegerSeriesSet stride) {

		List<SGDataValueHistory> list = new ArrayList<SGDataValueHistory>();
		final int colNum = data.getDataViewerColumnNumber(columnType, true);	// true is meaningless
		if (colNum == 1) {
			for (int ii = 0; ii < data.getChildNumber(); ii++) {
				SGDataValueHistory editedValue = SGDataUtility
						.setDataViewerValue(data, columnType, row, ii, value,
								stride);
				if (editedValue != null) {
					list.add(editedValue);
				}
			}
		} else {
			SGDataValueHistory editedValue = SGDataUtility.setDataViewerValue(
					data, columnType, row, col, value, stride);
			if (editedValue != null) {
				list.add(editedValue);
			}
		}
		return list;
	}

	public static double getCoordinateVariableValue(final double[] array, 
			final int index) {
        final double value = array[index];
        return SGUtilityNumber.getNumberInRangeOrder(value, array,
        		SGINetCDFConstants.DIMENSION_EFFECTIVE_DIGIT);
	}

	public static SGDataBufferPolicy getArchiveDataSetBufferPolicy(SGData data) {
		SGDataBufferPolicy policy;
		if (data instanceof SGISXYTypeMultipleData) {
			policy = new SGSXYDataBufferPolicy(true, false, true, false, false);
		} else {
			policy = new SGDataBufferPolicy(true, false, true);
		}
		return policy;
	}
	
	private static int getArrayIndex(SGIData data, final boolean all,
			SGIntegerSeriesSet stride, final int index) {
		final int arrayIndex;
		if (!all && data.isStrideAvailable()) {
			int[] indices = stride.getNumbers();
			arrayIndex = Arrays.binarySearch(indices, index);
		} else {
			arrayIndex = index;
		}
		return arrayIndex;
	}
	
	public static void setEditedValue(SGISXYZTypeData data, double[] xValues,
			double[] yValues, double[] zValues, final boolean all,
			SGDataValueHistory dataValue) {
		final double value = dataValue.getValue();
		String columnType = dataValue.getColumnType();
		ISingleDimension dataValueD1 = (ISingleDimension) dataValue;
		final int index = dataValueD1.getIndex();
		double[] values;
		if (X_VALUE.equals(columnType)) {
			values = xValues;
		} else if (Y_VALUE.equals(columnType)) {
			values = yValues;
		} else if (Z_VALUE.equals(columnType)) {
			values = zValues;
		} else {
			throw new Error("Invalid column type: " + columnType);
		}
		final int arrayIndex = getArrayIndex(data, all, data.getIndexStride(), index);
		values[arrayIndex] = value;
	}

	public static void setEditedValue(SGISXYZTypeData data, double[] xValues,
			double[] yValues, double[][] zGridValues, final boolean all,
			SGDataValueHistory dataValue) {
		final double value = dataValue.getValue();
		String columnType = dataValue.getColumnType();
		if (X_VALUE.equals(columnType)) {
			ISingleDimension dataValueD1 = (ISingleDimension) dataValue;
			final int index = dataValueD1.getIndex();
			final int arrayIndex = getArrayIndex(data, all, data.getXStride(), index);
			xValues[arrayIndex] = value;
		} else if (Y_VALUE.equals(columnType)) {
			ISingleDimension dataValueD1 = (ISingleDimension) dataValue;
			final int index = dataValueD1.getIndex();
			final int arrayIndex = getArrayIndex(data, all, data.getYStride(), index);
			yValues[arrayIndex] = value;
		} else if (Z_VALUE.equals(columnType)) {
			final int col = dataValue.getColumnIndex();
			final int row = dataValue.getRowIndex();
			final int colIndex = getArrayIndex(data, all, data.getXStride(), col);
			final int rowIndex = getArrayIndex(data, all, data.getYStride(), row);
			zGridValues[rowIndex][colIndex] = value;
		}
	}

	public static void setEditedValue(SGIVXYTypeData data, double[] xValues,
			double[] yValues, double[] fValues, double[] sValues, final boolean all,
			SGDataValueHistory dataValue) {
		final double value = dataValue.getValue();
		String columnType = dataValue.getColumnType();
		ISingleDimension dataValueD1 = (ISingleDimension) dataValue;
		final int index = dataValueD1.getIndex();
		final boolean polar = data.isPolar();
		final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
		final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
		double[] values;
		if (X_COORDINATE.equals(columnType)) {
			values = xValues;
		} else if (Y_COORDINATE.equals(columnType)) {
			values = yValues;
		} else if (first.equals(columnType)) {
			values = fValues;
		} else if (second.equals(columnType)) {
			values = sValues;
		} else {
			throw new Error("Invalid column type: " + columnType);
		}
		final int arrayIndex = getArrayIndex(data, all, data.getIndexStride(), index);
		values[arrayIndex] = value;
	}

	public static void setEditedValue(SGIVXYTypeData data, double[] xValues,
			double[] yValues, double[][] fGridValues, double[][] sGridValues,
			final boolean all, SGDataValueHistory dataValue) {
		final double value = dataValue.getValue();
		String columnType = dataValue.getColumnType();
		final boolean polar = data.isPolar();
		final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
		final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
		if (X_COORDINATE.equals(columnType)) {
			ISingleDimension dataValueD1 = (ISingleDimension) dataValue;
			final int index = dataValueD1.getIndex();
			final int arrayIndex = getArrayIndex(data, all, data.getXStride(), index);
			xValues[arrayIndex] = value;
		} else if (Y_COORDINATE.equals(columnType)) {
			ISingleDimension dataValueD1 = (ISingleDimension) dataValue;
			final int index = dataValueD1.getIndex();
			final int arrayIndex = getArrayIndex(data, all, data.getYStride(), index);
			yValues[arrayIndex] = value;
		} else if (first.equals(columnType)) {
			final int col = dataValue.getColumnIndex();
			final int row = dataValue.getRowIndex();
			final int colIndex = getArrayIndex(data, all, data.getXStride(), col);
			final int rowIndex = getArrayIndex(data, all, data.getYStride(), row);
			fGridValues[rowIndex][colIndex] = value;
		} else if (second.equals(columnType)) {
			final int col = dataValue.getColumnIndex();
			final int row = dataValue.getRowIndex();
			final int colIndex = getArrayIndex(data, all, data.getXStride(), col);
			final int rowIndex = getArrayIndex(data, all, data.getYStride(), row);
			sGridValues[rowIndex][colIndex] = value;
		}
	}

}

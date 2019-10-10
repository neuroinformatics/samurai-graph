package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import ucar.nc2.Dimension;

public class SGDefaultColumnTypeUtility implements SGIDataColumnTypeConstants,
		SGIDataPropertyKeyConstants {

    /**
	 * A class for the result of default column types.
	 *
	 */
	public static class DefaultColumnTypeResult {
	    protected boolean mSucceeded = false;
	    protected SGDataColumnInfo[] mColumns = null;
	    public DefaultColumnTypeResult(final SGDataColumnInfo[] columns, final boolean succeeded) {
	        super();
	        if (columns == null) {
	            throw new IllegalArgumentException("columns == null");
	        }
	        this.mColumns = columns;
	        this.mSucceeded = succeeded;
	    }

	    public String[] getDefaultColumnTypes() {
	    	String[] columnTypes = new String[this.mColumns.length];
	    	for (int ii = 0; ii < columnTypes.length; ii++) {
	    		columnTypes[ii] = this.mColumns[ii].getColumnType();
	    	}
	    	return columnTypes;
	    }
	    public boolean isSucceeded() {
	        return this.mSucceeded;
	    }
	}

	public static class DefaultMDColumnTypeResult extends DefaultColumnTypeResult {
	    public DefaultMDColumnTypeResult(final SGDataColumnInfo[] columns, final boolean succeeded) {
	        super(columns, succeeded);
	    }
		public int[] getIndices() {
			int[] indices = new int[this.mColumns.length];
			for (int ii = 0; ii < indices.length; ii++) {
				SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) this.mColumns[ii];
				indices[ii] = col.getGenericDimensionIndex();
			}
			return indices;
		}
		public List<Map<String, Integer>> getAllIndices() {
			List<Map<String, Integer>> indices = new ArrayList<Map<String, Integer>>();
			for (int ii = 0; ii < this.mColumns.length; ii++) {
				SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) this.mColumns[ii];
				Map<String, Integer> map = col.getDimensionIndices();
				indices.add(map);
			}
			return indices;
		}
	}

	/**
     * Returns default data column type.
     *
     * @param dataType
     *            data type
     * @param columnInfoList
     *            a list of column information
     * @param infoMap
     *            information map for data
     * @return default data column types
     */
    public static DefaultColumnTypeResult getDefaultColumnTypes(final String dataType,
            final List<SGDataColumnInfo> columnInfoList, final Map<String, Object> infoMap) {

        if (dataType == null || columnInfoList == null || infoMap == null) {
            throw new IllegalArgumentException("Null input value.");
        }

        SGDataColumnInfo[] columns = new SGDataColumnInfo[columnInfoList.size()];
        for (int ii = 0; ii < columns.length; ii++) {
        	columns[ii] = (SGDataColumnInfo) columnInfoList.get(ii).clone();
        }

        // the node name map
		final boolean succeeded = SGDefaultColumnTypeUtility
				.getDefaultColumnTypesSub(dataType, columnInfoList, infoMap, columns);

        // returned value
		DefaultColumnTypeResult result;
		if (SGDataUtility.isMDArrayData(dataType)) {
			result = new DefaultMDColumnTypeResult(
					columns, succeeded);
		} else {
			result = new DefaultColumnTypeResult(
					columns, succeeded);
		}

        return result;
    }

	private static boolean getDefaultColumnTypesSub(final String dataType,
			final List<SGDataColumnInfo> columnInfoList,
			final Map<String, Object> infoMap, SGDataColumnInfo[] columns) {

		boolean succeeded = true;
		final int len = columnInfoList.size();
        final NamedNodeMap nodeMap = (NamedNodeMap) infoMap.get(SGIFigureElementGraph.KEY_NODE_MAP);
        final String groupName = (String) infoMap.get(SGIFigureElementGraph.KEY_GROUP_NAME);

		if (SGDataUtility.isSDArrayData(dataType)) {
			// single dimensional array data
			List<Integer> numberIndexList = new ArrayList<Integer>();
			List<Integer> textIndexList = new ArrayList<Integer>();
			List<Integer> dateIndexList = new ArrayList<Integer>();
			List<Integer> samplingIndexList = new ArrayList<Integer>();
			for (int ii = 0; ii < len; ii++) {
				SGDataColumnInfo cInfo = columnInfoList.get(ii);
				String valueType = cInfo.getValueType();
				if (VALUE_TYPE_NUMBER.equals(valueType)) {
					numberIndexList.add(Integer.valueOf(ii));
				} else if (VALUE_TYPE_TEXT.equals(valueType)) {
					textIndexList.add(Integer.valueOf(ii));
				} else if (VALUE_TYPE_DATE.equals(valueType)) {
					dateIndexList.add(Integer.valueOf(ii));
				} else if (VALUE_TYPE_SAMPLING_RATE.equals(valueType)) {
					samplingIndexList.add(Integer.valueOf(ii));
				}
			}
			if (SGDataUtility.isSXYTypeData(dataType)
					|| SGDataTypeConstants.SXY_SAMPLING_DATA.equals(dataType)) {
				if (nodeMap != null) {
					if (!getForSXYSDArrayData(
							columnInfoList, infoMap, nodeMap, groupName, columns)) {
						succeeded = false;
					}
					if (succeeded) {
						if (!SGDataUtility.checkDataColumns(dataType, columns, infoMap)) {
							succeeded = false;
						}
					}
				}
				if (nodeMap == null || !succeeded){
					if (!getForSXYSDArrayData(
							columnInfoList, len, infoMap, numberIndexList,
							textIndexList, dateIndexList, samplingIndexList,
							columns)) {
						return false;
					}
				}
			} else if (SGDataUtility.isSXYZTypeData(dataType)) {
				if (nodeMap != null) {
					if (!getForSXYZSDArrayData(
							columnInfoList, infoMap, nodeMap, groupName, columns)) {
						succeeded = false;
					}
					if (succeeded) {
						if (!SGDataUtility.checkDataColumns(dataType, columns, infoMap)) {
							succeeded = false;
						}
					}
				}
				if (nodeMap == null || !succeeded){
					if (!getForSXYZSDArrayData(
							columnInfoList, len, infoMap, numberIndexList,
							textIndexList, dateIndexList, samplingIndexList,
							columns)) {
						return false;
					}
				}
			} else if (SGDataUtility.isVXYTypeData(dataType)) {
				if (nodeMap != null) {
					if (!getForVXYSDArrayData(
							columnInfoList, infoMap, nodeMap, groupName, columns)) {
						succeeded = false;
					}
					if (succeeded) {
						if (!SGDataUtility.checkDataColumns(dataType, columns, infoMap)) {
							succeeded = false;
						}
					}
				}
				if (nodeMap == null || !succeeded){
					if (!getForVXYSDArrayData(
							columnInfoList, len, infoMap, numberIndexList,
							textIndexList, dateIndexList, samplingIndexList,
							columns)) {
						return false;
					}
				}
			} else {
				throw new Error("Invalid data type: " + dataType);
			}

		} else if (SGDataUtility.isNetCDFData(dataType)) {
			// NetCDF data
			SGNetCDFFile ncFile = (SGNetCDFFile) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_SOURCE);
			List<SGNetCDFVariable> varList = ncFile.getVariables();
			final int size = varList.size();
			if (SGDataUtility.isSXYTypeData(dataType)) {
				if (nodeMap != null) {
					if (!getForSXYNetCDFData(
							columnInfoList, infoMap, nodeMap, groupName, columns)) {
						succeeded = false;
					}
					if (succeeded) {
						if (!SGDataUtility.checkDataColumns(dataType, columns, infoMap)) {
							succeeded = false;
						}
					}
				}
				if (nodeMap == null || !succeeded){
					if (!getForSXYNetCDFData(infoMap,
							varList, size, columns)) {
						return false;
					}
				}
			} else if (SGDataUtility.isVXYTypeData(dataType)) {
				if (nodeMap != null) {
					if (!getForVXYNetCDFData(
							columnInfoList, infoMap, nodeMap, groupName, columns)) {
						succeeded = false;
					}
					if (succeeded) {
						if (!SGDataUtility.checkDataColumns(dataType, columns, infoMap)) {
							succeeded = false;
						}
					}
				}
				if (nodeMap == null || !succeeded){
					if (!getForVXYNetCDFData(infoMap,
							varList, size, columns)) {
						return false;
					}
				}
			} else if (SGDataUtility.isSXYZTypeData(dataType)) {
				if (nodeMap != null) {
					if (!getForSXYZNetCDFData(
							columnInfoList, infoMap, nodeMap, groupName, columns)) {
						succeeded = false;
					}
					if (succeeded) {
						if (!SGDataUtility.checkDataColumns(dataType, columns, infoMap)) {
							succeeded = false;
						}
					}
				}
				if (nodeMap == null || !succeeded){
					if (!getForSXYZNetCDFData(infoMap,
							varList, size, columns)) {
						return false;
					}
				}
			} else {
				throw new Error("Invalid data type: " + dataType);
			}
		} else if (SGDataUtility.isMDArrayData(dataType)) {
			// multidimensional array data
			SGMDArrayFile mdFile = (SGMDArrayFile) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_SOURCE);
			SGMDArrayVariable[] vars = mdFile.getVariables();
			final int size = vars.length;
			if (SGDataUtility.isSXYTypeData(dataType)) {
				if (nodeMap != null) {
					if (!getForSXYMDArrayData(
							columnInfoList, infoMap, nodeMap, groupName, columns)) {
						succeeded = false;
					}
					if (succeeded) {
						if (!SGDataUtility.checkDataColumns(dataType, columns, infoMap)) {
							succeeded = false;
						}
					}
				}
				if (nodeMap == null || !succeeded){
					if (!getForSXYMDArrayData(infoMap,
							vars, size, columns)) {
						return false;
					}
				}
			} else if (SGDataUtility.isVXYTypeData(dataType)) {
				if (nodeMap != null) {
					if (!getForVXYMDArrayData(
							columnInfoList, infoMap, nodeMap, groupName, columns)) {
						succeeded = false;
					}
					if (succeeded) {
						if (!SGDataUtility.checkDataColumns(dataType, columns, infoMap)) {
							succeeded = false;
						}
					}
				}
				if (nodeMap == null || !succeeded){
					if (!getForVXYMDArrayData(infoMap,
							vars, size, columns)) {
						return false;
					}
				}

			} else if (SGDataUtility.isSXYZTypeData(dataType)) {
				if (nodeMap != null) {
					if (!getForSXYZMDArrayData(
							columnInfoList, infoMap, nodeMap, groupName, columns)) {
						succeeded = false;
					}
					if (succeeded) {
						if (!SGDataUtility.checkDataColumns(dataType, columns, infoMap)) {
							succeeded = false;
						}
					}
				}
				if (nodeMap == null || !succeeded){
					if (!getForSXYZMDArrayData(infoMap,
							vars, size, columns)) {
						return false;
					}
				}
			} else {
				throw new Error("Invalid data type: " + dataType);
			}
		} else {
			throw new Error("Unsupported data type: " + dataType);
		}
		return true;
	}

	private static boolean getForSXYSDArrayData(
			final List<SGDataColumnInfo> columnInfoList, final int len,
			final Map<String, Object> infoMap, List<Integer> numberIndexList,
			List<Integer> textIndexList, List<Integer> dateIndexList,
			List<Integer> samplingIndexList, SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		if (samplingIndexList.size() > 1) {
			return false;
		}

		Double samplingRate = (Double) infoMap.get(SGIDataInformationKeyConstants.KEY_SAMPLING_RATE);
		Boolean multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
		if (multiple == null) {
			return false;
		}

		final boolean dateColumnUsed = (dateIndexList.size() > 0 
				&& dateIndexList.contains(Integer.valueOf(0)));
//		boolean dateColumnFound = false;
//		// when a date column is found first,
//		// the date column is used for x values
//		for (int ii = 0; ii < len; ii++) {
//			SGDataColumnInfo cInfo = (SGDataColumnInfo) columnInfoList.get(ii);
//			String valueType = cInfo.getValueType();
//			if (VALUE_TYPE_DATE.equals(valueType)) {
//				dateColumnFound = true;
//				break;
//			}
//		}

		if (multiple.booleanValue()) {
			if (samplingRate != null) {
				if (numberIndexList.size() >= 1) {
					for (int ii = 0; ii < numberIndexList.size(); ii++) {
						Integer num = (Integer) numberIndexList.get(ii);
						columnTypes[num.intValue()] = Y_VALUE;
					}
				} else {
					return false;
				}
				if (samplingIndexList.size() == 1) {
					Integer num = samplingIndexList.get(0);
					columnTypes[num.intValue()] = X_VALUE;
				}
			} else if (dateColumnUsed) {
				if (numberIndexList.size() >= 1) {
					for (int ii = 0; ii < numberIndexList.size(); ii++) {
						Integer num = (Integer) numberIndexList.get(ii);
						columnTypes[num.intValue()] = Y_VALUE;
					}
					Integer date0 = dateIndexList.get(0);
					columnTypes[date0.intValue()] = X_VALUE;
				} else {
					return false;
				}
			} else {
				if (numberIndexList.size() >= 2) {
					Integer num1 = (Integer) numberIndexList.get(0);
					columnTypes[num1.intValue()] = X_VALUE;
					for (int ii = 1; ii < numberIndexList.size(); ii++) {
						Integer num = (Integer) numberIndexList.get(ii);
						columnTypes[num.intValue()] = Y_VALUE;
					}
				} else {
					return false;
				}
			}

		} else {

			final int minNumberSize;
			if (samplingRate != null) {
				minNumberSize = 1;
			} else {
				if (dateColumnUsed) {
					minNumberSize = 1;
				} else {
					minNumberSize = 2;
				}
			}
			final int yNumberArrayIndex = minNumberSize - 1;

			// get the column index of y index
			int yNumberColumnIndex = -1;
			int cnt = 0;
			for (int ii = 0; ii < len; ii++) {
				SGDataColumnInfo cInfo = (SGDataColumnInfo) columnInfoList
						.get(ii);
				String valueType = cInfo.getValueType();
				if (VALUE_TYPE_NUMBER.equals(valueType)) {
					if (cnt == yNumberArrayIndex) {
						yNumberColumnIndex = ii;
						break;
					}
					cnt++;
				}
			}
			if (yNumberColumnIndex == -1) {
				return false;
			}

			if (numberIndexList.size() >= minNumberSize) {
				boolean dateAssigned = false;
				if (samplingRate != null) {
					if (samplingIndexList.size() == 1) {
						Integer num = samplingIndexList.get(0);
						columnTypes[num.intValue()] = X_VALUE;
					}
				} else {
					// x values
					if (dateIndexList.size() != 0 && dateColumnUsed) {
						Integer num1 = (Integer) dateIndexList.get(0);
						columnTypes[num1.intValue()] = X_VALUE;
						dateAssigned = true;
					} else {
						Integer num1 = (Integer) numberIndexList.get(0);
						columnTypes[num1.intValue()] = X_VALUE;
					}
				}

				// y values
				Integer num2 = (Integer) numberIndexList.get(yNumberArrayIndex);
				columnTypes[num2.intValue()] = Y_VALUE;

				boolean[] isRepeatedTitle = SGDataUtility
						.isEmptyOrRepeatedColumnTitle(columnInfoList
								.toArray(new SGDataColumnInfo[len]));

				// error bars
				if (numberIndexList.size() >= minNumberSize + 2) {
					Integer num3 = (Integer) numberIndexList
							.get(yNumberArrayIndex + 1);
					Integer num4 = (Integer) numberIndexList
							.get(yNumberArrayIndex + 2);
					columnTypes[num3.intValue()] = SGDataUtility
							.appendColumnNoOrTitle(LOWER_ERROR_VALUE,
									yNumberColumnIndex,
									isRepeatedTitle[yNumberColumnIndex],
									columnInfoList.get(yNumberColumnIndex)
											.getTitle());
					columnTypes[num4.intValue()] = SGDataUtility
							.appendColumnNoOrTitle(UPPER_ERROR_VALUE,
									yNumberColumnIndex,
									isRepeatedTitle[yNumberColumnIndex],
									columnInfoList.get(yNumberColumnIndex)
											.getTitle());
				}

				// tick labels
				if (textIndexList.size() > 0) {
					Integer tick = (Integer) textIndexList.get(0);
					columnTypes[tick.intValue()] = SGDataUtility
							.appendColumnNoOrTitle(TICK_LABEL,
									yNumberColumnIndex,
									isRepeatedTitle[yNumberColumnIndex],
									columnInfoList.get(yNumberColumnIndex)
											.getTitle());
				} else if (dateIndexList.size() > 0 && !dateAssigned) {
					Integer date = (Integer) dateIndexList.get(0);
					columnTypes[date.intValue()] = SGDataUtility
							.appendColumnNoOrTitle(TICK_LABEL,
									yNumberColumnIndex,
									isRepeatedTitle[yNumberColumnIndex],
									columnInfoList.get(yNumberColumnIndex)
											.getTitle());
				}

			} else {
				return false;
			}
		}

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}
	
	private static boolean isSXYSDArrayNumberValueType(String valueType) {
		return (VALUE_TYPE_NUMBER.equals(valueType) || VALUE_TYPE_DATE.equals(valueType)
				|| VALUE_TYPE_SAMPLING_RATE.equals(valueType));
	}

	private static boolean getForSXYSDArrayData(
			final List<SGDataColumnInfo> columnInfoList,
			final Map<String, Object> infoMap, final NamedNodeMap nodeMap,
			final String groupName, final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		// x and y values
		Integer[] xIndices = getIndices(nodeMap, KEY_X_VALUE_COLUMN_INDICES);
		if (xIndices == null) {
			return false;
		}
		if (xIndices.length == 0) {
			return false;
		}
		if (!checkIndices(xIndices, columnTypes)) {
			return false;
		}
		Integer[] yIndices = getIndices(nodeMap, KEY_Y_VALUE_COLUMN_INDICES);
		if (yIndices == null) {
			return false;
		}
		if (yIndices.length == 0) {
			return false;
		}
		if (!checkIndices(yIndices, columnTypes)) {
			return false;
		}
		if (xIndices.length > 1 && yIndices.length > 1) {
			return false;
		}
		int xCnt = 0;
		for (int ii = 0; ii < xIndices.length; ii++) {
			final int index = xIndices[ii].intValue();
			final String valueType = columns[index].getValueType();
			if (isSXYSDArrayNumberValueType(valueType)) {
				columnTypes[index] = X_VALUE;
				xCnt++;
			}
		}
		if (xCnt == 0) {
			return false;
		}
		int yCnt = 0;
		for (int ii = 0; ii < yIndices.length; ii++) {
			final int index = yIndices[ii].intValue();
			final String valueType = columns[index].getValueType();
			if (isSXYSDArrayNumberValueType(valueType)) {
				columnTypes[index] = Y_VALUE;
				yCnt++;
			}
		}
		if (yCnt == 0) {
			return false;
		}
		if (xCnt > 1 && yCnt > 1) {
			return false;
		}

		// error values
		getForSXYSDArrayDataErrorBar(columnInfoList,
				infoMap, nodeMap, groupName, columnTypes, columns);

		// tick labels
		getForSXYSDArrayDataTickLabel(columnInfoList,
				infoMap, nodeMap, groupName, columnTypes, columns);

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}

	private static boolean getForSXYSDArrayDataErrorBar(
			final List<SGDataColumnInfo> columnInfoList,
			final Map<String, Object> infoMap, final NamedNodeMap nodeMap,
			final String groupName, final String[] columnTypes,
			final SGDataColumnInfo[] columns) {

		Integer[] leIndices = getIndices(nodeMap,
				KEY_LOWER_ERROR_BAR_COLUMN_INDICES);
		Integer[] ueIndices = getIndices(nodeMap,
				KEY_UPPER_ERROR_BAR_COLUMN_INDICES);
		Integer[] ehIndices = getIndices(nodeMap,
				KEY_ERROR_BAR_HOLDER_COLUMN_INDICES);
		if (leIndices != null && ueIndices != null && ehIndices != null) {
			if (!checkIndices(leIndices, columnTypes)) {
				return false;
			}
			if (!checkIndices(ueIndices, columnTypes)) {
				return false;
			}
			if (!checkIndices(ehIndices, columnTypes)) {
				return false;
			}
			if (ehIndices.length != leIndices.length
					|| ehIndices.length != ueIndices.length) {
				return false;
			}
			for (int ii = 0; ii < ehIndices.length; ii++) {
				final int ehIndex = ehIndices[ii].intValue();
				SGDataColumnInfo ehColInfo = columnInfoList.get(ehIndex);
				final boolean b = isEmptyTitle(columnInfoList, ehIndex)
						|| isRepeatedTitle(columnInfoList, ehIndex);
				final String title = ehColInfo.getTitle();
				final int leIndex = leIndices[ii].intValue();
				final int ueIndex = ueIndices[ii].intValue();
				if (!VALUE_TYPE_NUMBER.equals(columns[leIndex].getValueType())) {
					continue;
				}
				if (!VALUE_TYPE_NUMBER.equals(columns[ueIndex].getValueType())) {
					continue;
				}
				if (leIndex == ueIndex) {
					columnTypes[leIndex] = SGDataUtility
							.appendColumnNoOrTitle(LOWER_UPPER_ERROR_VALUE,
									ehIndex, b, title);
				} else {
					columnTypes[leIndex] = SGDataUtility
							.appendColumnNoOrTitle(LOWER_ERROR_VALUE, ehIndex,
									b, title);
					columnTypes[ueIndex] = SGDataUtility
							.appendColumnNoOrTitle(UPPER_ERROR_VALUE, ehIndex,
									b, title);
				}
			}
		}
		return true;
	}

	private static boolean getForSXYSDArrayDataTickLabel(
			final List<SGDataColumnInfo> columnInfoList,
			final Map<String, Object> infoMap, final NamedNodeMap nodeMap,
			final String groupName, final String[] columnTypes,
			final SGDataColumnInfo[] columns) {
		Integer[] tlIndices = getIndices(nodeMap, KEY_TICK_LABEL_COLUMN_INDICES);
		Integer[] thIndices = getIndices(nodeMap,
				KEY_TICK_LABEL_HOLDER_COLUMN_INDICES);
		if (tlIndices != null && thIndices != null) {
			if (!checkIndices(tlIndices, columnTypes)) {
				return false;
			}
			if (!checkIndices(thIndices, columnTypes)) {
				return false;
			}
			if (tlIndices.length != thIndices.length) {
				return false;
			}
			for (int ii = 0; ii < thIndices.length; ii++) {
				final int thIndex = thIndices[ii].intValue();
				SGDataColumnInfo thColInfo = columnInfoList.get(thIndex);
				final boolean b = isEmptyTitle(columnInfoList, thIndex)
						|| isRepeatedTitle(columnInfoList, thIndex);
				final String title = thColInfo.getTitle();
				columnTypes[tlIndices[ii]] = SGDataUtility
						.appendColumnNoOrTitle(TICK_LABEL, thIndex, b, title);
			}
		}
		return true;
	}

	private static boolean getForVXYSDArrayData(
			final List<SGDataColumnInfo> columnInfoList, final int len,
			final Map<String, Object> infoMap, List<Integer> numberIndexList,
			List<Integer> textIndexList, List<Integer> dateIndexList,
			List<Integer> samplingIndexList,
			final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		if (numberIndexList.size() >= 4) {
			Integer num1 = (Integer) numberIndexList.get(0);
			Integer num2 = (Integer) numberIndexList.get(1);
			Integer num3 = (Integer) numberIndexList.get(2);
			Integer num4 = (Integer) numberIndexList.get(3);
			columnTypes[num1.intValue()] = X_COORDINATE;
			columnTypes[num2.intValue()] = Y_COORDINATE;
			columnTypes[num3.intValue()] = SGDataUtility.getVXYFirstComponentColumnType(infoMap);
			columnTypes[num4.intValue()] = SGDataUtility.getVXYSecondComponentColumnType(infoMap);
		} else {
			return false;
		}

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}

	private static boolean getForVXYSDArrayData(
			final List<SGDataColumnInfo> columnInfoList,
			final Map<String, Object> infoMap, final NamedNodeMap nodeMap,
			final String groupName, final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		Integer[] xIndices = getIndices(nodeMap, KEY_X_COORDINATE_COLUMN_INDEX);
		if (xIndices == null) {
			return false;
		}
		if (xIndices.length != 1) {
			return false;
		}
		if (!checkIndices(xIndices, columnTypes)) {
			return false;
		}
		Integer[] yIndices = getIndices(nodeMap, KEY_Y_COORDINATE_COLUMN_INDEX);
		if (yIndices == null) {
			return false;
		}
		if (yIndices.length != 1) {
			return false;
		}
		if (!checkIndices(yIndices, columnTypes)) {
			return false;
		}
		Integer[] fIndices = getIndices(nodeMap, KEY_FIRST_COMPONENT_COLUMN_INDEX);
		if (fIndices == null) {
			return false;
		}
		if (fIndices.length != 1) {
			return false;
		}
		if (!checkIndices(fIndices, columnTypes)) {
			return false;
		}
		Integer[] sIndices = getIndices(nodeMap, KEY_SECOND_COMPONENT_COLUMN_INDEX);
		if (sIndices == null) {
			return false;
		}
		if (sIndices.length != 1) {
			return false;
		}
		if (!checkIndices(sIndices, columnTypes)) {
			return false;
		}

		final int xIndex = xIndices[0].intValue();
		if (!VALUE_TYPE_NUMBER.equals(columns[xIndex].getValueType())) {
			return false;
		}
		columnTypes[xIndex] = X_COORDINATE;

		final int yIndex = yIndices[0].intValue();
		if (!VALUE_TYPE_NUMBER.equals(columns[yIndex].getValueType())) {
			return false;
		}
		columnTypes[yIndex] = Y_COORDINATE;

    	final int fIndex = fIndices[0].intValue();
		if (!VALUE_TYPE_NUMBER.equals(columns[fIndex].getValueType())) {
			return false;
		}
		columnTypes[fIndex] = SGDataUtility.getVXYFirstComponentColumnType(infoMap);

    	final int sIndex = sIndices[0].intValue();
		if (!VALUE_TYPE_NUMBER.equals(columns[sIndex].getValueType())) {
			return false;
		}
		columnTypes[sIndex] = SGDataUtility.getVXYSecondComponentColumnType(infoMap);

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}

	private static boolean getForSXYZSDArrayData(
			final List<SGDataColumnInfo> columnInfoList, final int len,
			final Map<String, Object> infoMap, List<Integer> numberIndexList,
			List<Integer> textIndexList, List<Integer> dateIndexList,
			List<Integer> samplingIndexList,
			final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		if (numberIndexList.size() >= 3) {
			// x, y and z values
			Integer num1 = (Integer) numberIndexList.get(0);
			Integer num2 = (Integer) numberIndexList.get(1);
			Integer num3 = (Integer) numberIndexList.get(2);
			columnTypes[num1.intValue()] = X_VALUE;
			columnTypes[num2.intValue()] = Y_VALUE;
			columnTypes[num3.intValue()] = Z_VALUE;
		} else {
			return false;
		}

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}

	private static boolean getForSXYZSDArrayData(
			final List<SGDataColumnInfo> columnInfoList,
			final Map<String, Object> infoMap, final NamedNodeMap nodeMap,
			final String groupName, final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		// x, y and z values
		Integer[] xIndices = getIndices(nodeMap, KEY_X_VALUE_COLUMN_INDEX);
		if (xIndices == null) {
			return false;
		}
		if (xIndices.length != 1) {
			return false;
		}
		if (!checkIndices(xIndices, columnTypes)) {
			return false;
		}
		Integer[] yIndices = getIndices(nodeMap, KEY_Y_VALUE_COLUMN_INDEX);
		if (yIndices == null) {
			return false;
		}
		if (yIndices.length != 1) {
			return false;
		}
		if (!checkIndices(yIndices, columnTypes)) {
			return false;
		}
		Integer[] zIndices = getIndices(nodeMap, KEY_Z_VALUE_COLUMN_INDEX);
		if (zIndices == null) {
			return false;
		}
		if (zIndices.length != 1) {
			return false;
		}
		if (!checkIndices(zIndices, columnTypes)) {
			return false;
		}

		final int xIndex = xIndices[0].intValue();
		if (!VALUE_TYPE_NUMBER.equals(columns[xIndex].getValueType())) {
			return false;
		}
		columnTypes[xIndex] = X_VALUE;

		final int yIndex = yIndices[0].intValue();
		if (!VALUE_TYPE_NUMBER.equals(columns[yIndex].getValueType())) {
			return false;
		}
		columnTypes[yIndex] = Y_VALUE;

		final int zIndex = zIndices[0].intValue();
		if (!VALUE_TYPE_NUMBER.equals(columns[zIndex].getValueType())) {
			return false;
		}
		columnTypes[zIndex] = Z_VALUE;

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}

	private static boolean isEmptyTitle(
			final List<SGDataColumnInfo> columnInfoList, final int colIndex) {
		SGDataColumnInfo colInfo = columnInfoList.get(colIndex);
		String title = colInfo.getTitle();
		if (title == null || "".equals(title)) {
			return true;
		}
		return false;
	}

	private static boolean isRepeatedTitle(
			final List<SGDataColumnInfo> columnInfoList, final int colIndex) {
		String[] titles = new String[columnInfoList.size()];
		for (int ii = 0; ii < titles.length; ii++) {
			SGDataColumnInfo colInfo = columnInfoList.get(ii);
			titles[ii] = colInfo.getTitle();
		}
		if (null == titles[colIndex]) {
			return true;
		}
		for (int i = 0; i < titles.length; i++) {
			if (colIndex != i && titles[colIndex].equals(titles[i])) {
				return true;
			}
		}
		return false;
	}

	private static Integer[] getIndices(final NamedNodeMap nodeMap,
			final String key) {
		Node nodeIndices = nodeMap.getNamedItem(key);
		if (nodeIndices == null) {
			return null;
		}
		String strIndices = nodeIndices.getNodeValue();
		if (strIndices == null) {
			return null;
		}
		Integer[] indices = SGUtilityText.parseIndices(strIndices);
		return indices;
	}

	private static boolean checkIndices(final Integer[] indices, final String[] columnTypes) {
		for (int ii = 0; ii < indices.length; ii++) {
			final int index = indices[ii].intValue();
			if (index < 0 || index >= columnTypes.length) {
				return false;
			}
		}
		return true;
	}

	private static boolean getForSXYNetCDFData(
			final Map<String, Object> infoMap, final List<SGNetCDFVariable> varList,
			final int size,
			final SGDataColumnInfo[] columns) {
		List<Integer> indices = getIndexVariableColumnIndices(columns);
		if (indices.size() > 0) {
			if (getForSXYNetCDFDataIndex(infoMap, varList, size, columns, indices)) {
				return true;
			}
			if (getForSXYNetCDFDataNormal(infoMap, varList, size, columns)) {
				return true;
			}
		} else {
			if (getForSXYNetCDFDataNormal(infoMap, varList, size, columns)) {
				return true;
			}
			if (getForSXYNetCDFDataIndex(infoMap, varList, size, columns, indices)) {
				return true;
			}
		}
		return false;
	}

	private static boolean getForSXYNetCDFDataNormal(
			final Map<String, Object> infoMap, final List<SGNetCDFVariable> varList,
			final int size,
			final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		// a coordinate variable found first is assigned to x variable
		int xIndex = -1;
		SGNetCDFVariable xVar = null;
		List<Integer> tempVarIndexList = new ArrayList<Integer>();

		// get unlimited variable
		for (int ii = 0; ii < size; ii++) {
			SGNetCDFVariable var = varList.get(ii);
			if (var.isUnlimited() && var.isCoordinateVariable()) {
				Dimension dim = var.getDimension(0);
				final int len = dim.getLength();
				if (len <= 1) {
					tempVarIndexList.add(ii);
					continue;
				}
				xIndex = ii;
				xVar = var;
				break;
			}
		}

		// if unlimited variable is not found, search the coordinate variable
		if (xVar == null) {
			for (int ii = 0; ii < size; ii++) {
				SGNetCDFVariable var = varList.get(ii);
				if (var.isCoordinateVariable()) {
					Dimension dim = var.getDimension(0);
					final int len = dim.getLength();
					if (len <= 1) {
						tempVarIndexList.add(ii);
						continue;
					}
					xIndex = ii;
					xVar = var;
					break;
				}
			}
		}
		
		// searches from temporary list
		if (xIndex == -1) {
			if (tempVarIndexList.size() > 0) {
				xIndex = tempVarIndexList.get(0);
				xVar = varList.get(xIndex);
			}
		}
		
		if (xIndex == -1) {
			return false;
		}

		Boolean multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
		if (multiple == null) {
			return false;
		}

		Boolean variable = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE);
		if (variable == null) {
			return false;
		}

		// y variable must not be a coordinate variable and it has a dimension
		// that is the same as that of the x variable
		Integer[] yIndices = null;
		Dimension xDim = xVar.getDimension(0);
		List<Integer> yList = new ArrayList<Integer>();
		for (int ii = 0; ii < size; ii++) {
			SGNetCDFVariable var = varList.get(ii);
			if (!var.isCoordinateVariable() && VALUE_TYPE_NUMBER.equals(var.getValueType())) {
				List<Dimension> dimList = var.getDimensions();
				if (dimList.contains(xDim)) {
					yList.add(ii);
				}
			}
		}
		if (yList.size() == 0) {
			return false;
		} else {
			yIndices = new Integer[yList.size()];
			for (int ii = 0; ii < yList.size(); ii++) {
				yIndices[ii] = yList.get(ii);
			}
		}

		// for multiple variables
		if (variable.booleanValue()) {
			columnTypes[xIndex] = X_VALUE;

			if (multiple.booleanValue()) {
				// multiple y-indices
				for (int yIndex : yIndices) {
					columnTypes[yIndex] = Y_VALUE;
				}
			} else {
				// finds the y-index
				for (int ii = 0; ii < yIndices.length; ii++) {
					final int yIndex = yIndices[ii];
					SGNetCDFVariable var = varList.get(yIndex);
					if (VALUE_TYPE_NUMBER.equals(var.getValueType())) {
						columnTypes[yIndex] = Y_VALUE;
						break;
					}
				}
			}
		} else {
			// for multiple dimension indices
			final int yIndex = yIndices[0];
			columnTypes[xIndex] = X_VALUE;
			columnTypes[yIndex] = Y_VALUE;

			int dIndex = -1;
			final SGNetCDFVariable yVar = varList.get(yIndex);
			List<Dimension> yDimList = yVar.getDimensions();
			for (int ii = 0; ii < varList.size(); ii++) {
				SGNetCDFVariable var = varList.get(ii);
				if (var.isCoordinateVariable()) {
					Dimension dim = var.getDimension(0);
					if (!xDim.equals(dim) && yDimList.contains(dim)) {
						dIndex = ii;
						break;
					}
				}
			}
			if (dIndex != -1) {
				columnTypes[dIndex] = PICKUP;
			} else {
				return false;
			}
		}

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}

	private static List<SGNetCDFVariable> sortVariableList(final List<SGNetCDFVariable> varList,
			final List<Integer> serialNumberIndices) {
		List<SGNetCDFVariable> varListSorted = new ArrayList<SGNetCDFVariable>();
		for (int ii = 0; ii < serialNumberIndices.size(); ii++) {
			Integer index = serialNumberIndices.get(ii);
			varListSorted.add(varList.get(index));
		}
		for (int ii = 0; ii < varList.size(); ii++) {
			if (!serialNumberIndices.contains(ii)) {
				varListSorted.add(varList.get(ii));
			}
		}
		return varListSorted;
	}

	// for serial number case
	private static boolean getForSXYNetCDFDataIndex(
			final Map<String, Object> infoMap, final List<SGNetCDFVariable> varList,
			final int size, final SGDataColumnInfo[] columns,
			final List<Integer> serialNumberIndices) {

		List<SGNetCDFVariable> varListSorted = sortVariableList(varList, serialNumberIndices);

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		int iIndex = -1;
		int xIndex = -1;
		int yIndex = -1;
		for (int ii = 0; ii < varListSorted.size(); ii++) {
			SGNetCDFVariable var = varListSorted.get(ii);
			if (var.isCoordinateVariable()) {
				Dimension cDim = var.getDimension(0);
				for (int jj = 0; jj < varListSorted.size(); jj++) {
					if (jj == ii) {
						continue;
					}
					SGNetCDFVariable v = varListSorted.get(jj);
					if (!VALUE_TYPE_NUMBER.equals(v.getValueType())) {
						continue;
					}
					List<Dimension> dimList = v.getDimensions();
					if (!dimList.contains(cDim)) {
						continue;
					}
					if (xIndex == -1) {
						xIndex = jj;
					} else if (yIndex == -1) {
						yIndex = jj;
					}
				}
				if (xIndex != -1 && yIndex != -1) {
					if (xIndex != yIndex) {
						iIndex = ii;
						break;
					}
				}

				// clears indices
				xIndex = -1;
				yIndex = -1;
			}
		}
		if (iIndex == -1 || xIndex == -1 || yIndex == -1) {
			return false;
		}

		columnTypes[iIndex] = INDEX;
		columnTypes[xIndex] = X_VALUE;
		columnTypes[yIndex] = Y_VALUE;

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}

	private static class MDArrayDimension implements Comparable<Object> {
		int variableIndex = 0;
		int dimensionIndex = 0;
		MDArrayDimension(final int variableIndex, final int dimensionIndex) {
			super();
			this.variableIndex = variableIndex;
			this.dimensionIndex = dimensionIndex;
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(this.variableIndex);
			sb.append('-');
			sb.append(this.dimensionIndex);
			return sb.toString();
		}
		
		@Override
		public int compareTo(Object o) {
			if (!(o instanceof MDArrayDimension)) {
				throw new IllegalArgumentException("Invalid input: " + o);
			}
			MDArrayDimension oDim = (MDArrayDimension) o;
			if (this.variableIndex < oDim.variableIndex) {
				return -1;
			} else if (this.variableIndex == oDim.variableIndex) {
				if (this.dimensionIndex < oDim.dimensionIndex) {
					return -1;
				} else if (this.dimensionIndex == oDim.dimensionIndex) {
					return 0;
				} else {
					return 1;
				}
			} else {
				return 1;
			}
		}
	}

	private static boolean getForSXYMDArrayData(
			final Map<String, Object> infoMap, final SGMDArrayVariable[] vars,
			final int size,
			final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		// dimension indices
		int[] indices = new int[columns.length];
		for (int ii = 0; ii < indices.length; ii++) {
			indices[ii] = 0;
		}

		Boolean multiple = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE);
		if (multiple == null) {
			return false;
		}

		// create a map of dimensions
		TreeMap<Integer, List<MDArrayDimension>> dimListMap = createDimensionListMap(columns);

		// assign the columns for y-values
		List<MDArrayDimension> yDimList = extractDimensions(dimListMap, 1);
		final int maxSize = 5;
		if (yDimList.size() > maxSize) {
			yDimList = new ArrayList<MDArrayDimension>(yDimList.subList(0, maxSize));
		}

		// for multiple variables
		if (multiple.booleanValue()) {
			// multiple y-indices
			for (MDArrayDimension yDim : yDimList) {
				columnTypes[yDim.variableIndex] = Y_VALUE;
				indices[yDim.variableIndex] = yDim.dimensionIndex;
			}
		} else {
			MDArrayDimension yDim = yDimList.get(0);
			columnTypes[yDim.variableIndex] = Y_VALUE;
			indices[yDim.variableIndex] = yDim.dimensionIndex;
		}

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
			SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
			mdInfo.setGenericDimensionIndex(indices[ii]);
		}

		return true;
	}

	private static boolean getForSXYMDArrayData(
			final List<SGDataColumnInfo> columnInfoList,
			final Map<String, Object> infoMap, final NamedNodeMap nodeMap,
			final String groupName, final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		int[] dimensionIndex = new int[columns.length];
		for (int ii = 0; ii < dimensionIndex.length; ii++) {
			dimensionIndex[ii] = 0;
		}

		// pick up
		@SuppressWarnings("unchecked")
		Map<String, Integer> dimensionIndexMap = (Map<String, Integer>) infoMap.get(
				SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP);
		SGMDArrayPickUpDimensionInfo pickUpInfo = null;
		if (dimensionIndexMap != null) {
			SGIntegerSeriesSet pickUpDimensionIndices = (SGIntegerSeriesSet) infoMap.get(
					SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
			pickUpInfo = new SGMDArrayPickUpDimensionInfo(dimensionIndexMap, pickUpDimensionIndices);
		}

		// x and y values
		String[] xNames = getNames(nodeMap, new String[] { KEY_X_VALUE_NAMES,
				KEY_X_VALUE_COLUMN_INDICES }, groupName);
		if (xNames == null) {
			xNames = getNames(nodeMap, new String[] { KEY_X_VALUE_NAME,
					KEY_X_VALUE_COLUMN_INDEX }, groupName);
		}
		if (xNames != null) {
			if (xNames.length == 0) {
				return false;
			}
		}
		String[] yNames = getNames(nodeMap, new String[] { KEY_Y_VALUE_NAMES,
				KEY_Y_VALUE_COLUMN_INDICES }, groupName);
		if (yNames == null) {
			yNames = getNames(nodeMap, new String[] { KEY_Y_VALUE_NAME,
					KEY_Y_VALUE_COLUMN_INDEX }, groupName);
		}
		if (yNames != null) {
			if (yNames.length == 0) {
				return false;
			}
		}
		if (xNames == null && yNames == null) {
			return false;
		}
		if (xNames != null && yNames != null) {
			if (xNames.length > 1 && yNames.length > 1) {
				return false;
			}
		}
		List<Integer> xIndices = new ArrayList<Integer>();
		if (xNames != null && !"".equals(xNames[0])) {
			for (int ii = 0; ii < xNames.length; ii++) {
				final int index = setupMDArrayColumn(columnInfoList, columnTypes,
						dimensionIndex, xNames[ii], X_VALUE);
				if (index == -1) {
					return false;
				}
				if (!isNumberColumn(columnInfoList, index)) {
					return false;
				}
				xIndices.add(index);
			}
		}
		List<Integer> yIndices = new ArrayList<Integer>();
		if (yNames != null && !"".equals(yNames[0])) {
			for (int ii = 0; ii < yNames.length; ii++) {
				final int index = setupMDArrayColumn(columnInfoList, columnTypes,
						dimensionIndex, yNames[ii], Y_VALUE);
				if (index == -1) {
					return false;
				}
				if (!isNumberColumn(columnInfoList, index)) {
					return false;
				}
				yIndices.add(index);
			}
		}

		// error values
		if (pickUpInfo != null) {
			String[] leNames = getNames(nodeMap, new String[] { KEY_LOWER_ERROR_VALUE_NAME,
					KEY_LOWER_ERROR_BAR_COLUMN_INDICES }, groupName);
			String[] ueNames = getNames(nodeMap, new String[] { KEY_UPPER_ERROR_VALUE_NAME,
					KEY_UPPER_ERROR_BAR_COLUMN_INDICES }, groupName);
			String[] ehNames = getNames(nodeMap, new String[] {
					KEY_ERROR_BAR_HOLDER_NAME,
					KEY_ERROR_BAR_HOLDER_COLUMN_INDICES }, groupName);
			if (leNames != null && ueNames != null && ehNames != null) {
				if (leNames.length != 1 || ueNames.length != 1 || ehNames.length != 1) {
					return false;
				}
				String leName = leNames[0];
				String ueName = ueNames[0];
				String ehName = ehNames[0];
				final int pickUpHolderIndex = getIndex(ehName, columnInfoList);
				if (pickUpHolderIndex == -1) {
					return false;
				}
				SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columnInfoList.get(
						pickUpHolderIndex);
				if (setMDArrayErrorBar(columnInfoList, columnTypes, dimensionIndex, leName,
						ueName, mdInfo.getName()) == false) {
					return false;
				}
			}

		} else {
			String[] leNames = getNames(nodeMap,
					new String[] { KEY_LOWER_ERROR_VALUE_NAMES,
							KEY_LOWER_ERROR_BAR_COLUMN_INDICES }, groupName);
			String[] ueNames = getNames(nodeMap,
					new String[] { KEY_UPPER_ERROR_VALUE_NAMES,
							KEY_UPPER_ERROR_BAR_COLUMN_INDICES }, groupName);
			String[] ehNames = getNames(nodeMap,
					new String[] { KEY_ERROR_BAR_HOLDER_NAMES,
							KEY_ERROR_BAR_HOLDER_COLUMN_INDICES }, groupName);
			if (leNames != null && ueNames != null && ehNames != null) {
				if (ehNames.length != leNames.length
						|| ehNames.length != ueNames.length) {
					return false;
				}
				for (int ii = 0; ii < ehNames.length; ii++) {
					final int index = findMDArrayColumnInfo(columnInfoList, ehNames[ii]);
					if (index == -1) {
						return false;
					}
					if (!isNumberColumn(columnInfoList, index)) {
						return false;
					}
					if (!xIndices.contains(index) && !yIndices.contains(index)) {
						return false;
					}
				}
				for (int ii = 0; ii < ehNames.length; ii++) {
					if (setMDArrayErrorBar(columnInfoList, columnTypes, dimensionIndex, leNames[ii],
							ueNames[ii], ehNames[ii]) == false) {
						return false;
					}
				}
			}
		}

		// tick labels
		if (pickUpInfo != null) {
			String[] tlNames = getNames(nodeMap, new String[] {
					KEY_TICK_LABEL_NAME, KEY_TICK_LABEL_COLUMN_INDICES },
					groupName);
			String[] thNames = getNames(nodeMap, new String[] {
					KEY_TICK_LABEL_HOLDER_NAME, KEY_TICK_LABEL_HOLDER_COLUMN_INDICES }, 
					groupName);
			if (tlNames != null) {
				if (tlNames.length != 1 || thNames.length != 1) {
					return false;
				}
				String tlName = tlNames[0];
				String thName = thNames[0];
				final int pickUpHolderIndex = getIndex(thName, columnInfoList);
				if (pickUpHolderIndex == -1) {
					return false;
				}
				SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columnInfoList.get(
						pickUpHolderIndex);
				if (setMDArrayTickLabel(columnInfoList, columnTypes, dimensionIndex, tlName,
						mdInfo.getName()) == false) {
					return false;
				}
			}

		} else {
			String[] tlNames = getNames(nodeMap, new String[] {
					KEY_TICK_LABEL_NAMES, KEY_TICK_LABEL_COLUMN_INDICES },
					groupName);
			String[] thNames = getNames(nodeMap, new String[] {
					KEY_TICK_LABEL_HOLDER_NAMES,
					KEY_TICK_LABEL_HOLDER_COLUMN_INDICES }, groupName);
			if (tlNames != null && thNames != null) {
				if (tlNames.length != thNames.length) {
					return false;
				}
				for (int ii = 0; ii < thNames.length; ii++) {
					final int index = findMDArrayColumnInfo(columnInfoList, thNames[ii]);
					if (index == -1) {
						return false;
					}
					if (!xIndices.contains(index) && !yIndices.contains(index)) {
						return false;
					}
				}
				for (int ii = 0; ii < thNames.length; ii++) {
					if (setMDArrayTickLabel(columnInfoList, columnTypes, dimensionIndex, tlNames[ii],
							thNames[ii]) == false) {
						return false;
					}
				}
			}
		}

		for (int ii = 0; ii < columns.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) columns[ii];
			mdCol.setColumnType(columnTypes[ii]);
			mdCol.setGenericDimensionIndex(dimensionIndex[ii]);
		}

		return true;
	}
	
	private static int getIndex(String name, List<SGDataColumnInfo> columnInfoList) {
		for (int ii = 0; ii < columnInfoList.size(); ii++) {
			SGDataColumnInfo info = columnInfoList.get(ii);
			if (name.equals(info.getName())) {
				return ii;
			}
		}
		return -1;
	}

	private static boolean setMDArrayTickLabel(final List<SGDataColumnInfo> columnInfoList,
			String[] columnTypes, int[] dimensionIndex, String tlName, String thName) {
		final int index = setMDArrayColumnType(columnInfoList, columnTypes, dimensionIndex,
				tlName, thName, TICK_LABEL);
		if (index == -1) {
			return false;
		}
		return true;
	}

	private static boolean setMDArrayErrorBar(final List<SGDataColumnInfo> columnInfoList,
			String[] columnTypes, int[] dimensionIndex, String leName, String ueName, String ehName) {
		if (leName.equals(ueName)) {
			final int index = setMDArrayColumnType(columnInfoList, columnTypes, dimensionIndex,
					leName, ehName, LOWER_UPPER_ERROR_VALUE);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index)) {
				return false;
			}
		} else {
			int index;
			index = setMDArrayColumnType(columnInfoList, columnTypes, dimensionIndex,
					leName, ehName, LOWER_ERROR_VALUE);
			if (index == -1) {
				return false;
			}
			index = setMDArrayColumnType(columnInfoList, columnTypes, dimensionIndex,
					ueName, ehName, UPPER_ERROR_VALUE);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index)) {
				return false;
			}
		}
		return true;
	}

	private static boolean getForSXYNetCDFData(
			final List<SGDataColumnInfo> columnInfoList,
			final Map<String, Object> infoMap, final NamedNodeMap nodeMap,
			final String groupName, final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		// serial numbers
		String[] indexNames = getNames(nodeMap,
				KEY_INDEX_VARIABLE_NAME, groupName);
		if (indexNames == null) {
			indexNames = getNames(nodeMap,
					KEY_SERIAL_NUMBER_VARIABLE_NAME, groupName);
		}
		int idxIndex = -1;
		if (indexNames != null) {
			if (indexNames.length != 1) {
				return false;
			}
			if ((idxIndex = setNetCDFColumnType(columnInfoList, columnTypes,
					indexNames[0], INDEX)) == -1) {
				return false;
			}
			if (!isCoordinateVariableColumn(columnInfoList, idxIndex)) {
				return false;
			}
		}

		// x and y values
		String[] xNames = getNames(nodeMap, new String[] { KEY_X_VALUE_NAME,
				KEY_X_VALUE_COLUMN_INDEX }, groupName);
		if (xNames == null) {
			xNames = getNames(nodeMap, new String[] { KEY_X_VALUE_NAMES,
					KEY_X_VALUE_COLUMN_INDICES }, groupName);
			if (xNames == null) {
				return false;
			}
		}
		if (xNames.length == 0) {
			return false;
		}
		String[] yNames = getNames(nodeMap, new String[] { KEY_Y_VALUE_NAME,
				KEY_Y_VALUE_COLUMN_INDEX }, groupName);
		if (yNames == null) {
			yNames = getNames(nodeMap, new String[] { KEY_Y_VALUE_NAMES,
					KEY_Y_VALUE_COLUMN_INDICES }, groupName);
			if (yNames == null) {
				return false;
			}
		}
		if (yNames.length == 0) {
			return false;
		}
		if (xNames.length > 1 && yNames.length > 1) {
			return false;
		}
		List<Integer> xIndices = new ArrayList<Integer>();
		for (int ii = 0; ii < xNames.length; ii++) {
			final int index = setNetCDFColumnType(columnInfoList, columnTypes,
					xNames[ii], X_VALUE);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index) && !isDateColumn(columnInfoList, index)) {
				return false;
			}
			xIndices.add(index);
		}
		List<Integer> yIndices = new ArrayList<Integer>();
		for (int ii = 0; ii < yNames.length; ii++) {
			final int index = setNetCDFColumnType(columnInfoList, columnTypes,
					yNames[ii], Y_VALUE);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index) && !isDateColumn(columnInfoList, index)) {
				return false;
			}
			yIndices.add(index);
		}

		if (idxIndex != -1) {
			for (Integer xIndex : xIndices) {
				if (isCoordinateVariableColumn(columnInfoList, xIndex)) {
					return false;
				}
			}
			for (Integer yIndex : yIndices) {
				if (isCoordinateVariableColumn(columnInfoList, yIndex)) {
					return false;
				}
			}
		} else {
			Boolean xCoordinateVariable = null;
			for (Integer xIndex : xIndices) {
				final boolean b = isCoordinateVariableColumn(columnInfoList, xIndex);
				if (xCoordinateVariable == null) {
					xCoordinateVariable = b;
				} else {
					if (!xCoordinateVariable.equals(b)) {
						return false;
					}
				}
			}
			for (Integer yIndex : yIndices) {
				final boolean b = isCoordinateVariableColumn(columnInfoList, yIndex);
				if (xCoordinateVariable.equals(b)) {
					return false;
				}
			}
		}

		// error values
		String[] leNames = getNames(nodeMap,
				new String[] { KEY_LOWER_ERROR_VALUE_NAME, KEY_LOWER_ERROR_VALUE_NAMES,
						KEY_LOWER_ERROR_BAR_COLUMN_INDICES }, groupName);
		String[] ueNames = getNames(nodeMap,
				new String[] { KEY_UPPER_ERROR_VALUE_NAME, KEY_UPPER_ERROR_VALUE_NAMES,
						KEY_UPPER_ERROR_BAR_COLUMN_INDICES }, groupName);
		String[] ehNames = getNames(nodeMap,
				new String[] { KEY_ERROR_BAR_HOLDER_NAME, KEY_ERROR_BAR_HOLDER_NAMES,
						KEY_ERROR_BAR_HOLDER_COLUMN_INDICES }, groupName);
		if (leNames != null && ueNames != null && ehNames != null) {
			if (ehNames.length != leNames.length
					|| ehNames.length != ueNames.length) {
				return false;
			}
			for (int ii = 0; ii < ehNames.length; ii++) {
				final int index = findNetCDFColumnInfo(columnInfoList, ehNames[ii]);
				if (index == -1) {
					return false;
				}
				if (!isNumberColumn(columnInfoList, index)) {
					return false;
				}
				if (isCoordinateVariableColumn(columnInfoList, index)) {
					return false;
				}
				if (!xIndices.contains(index) && !yIndices.contains(index)) {
					return false;
				}
			}
			for (int ii = 0; ii < ehNames.length; ii++) {
				if (leNames[ii].equals(ueNames[ii])) {
					final int index = setupNetCDFColumn(columnInfoList, columnTypes,
							leNames[ii], ehNames[ii], LOWER_UPPER_ERROR_VALUE);
					if (index == -1) {
						return false;
					}
					if (!isNumberColumn(columnInfoList, index)) {
						return false;
					}
					if (isCoordinateVariableColumn(columnInfoList, index)) {
						return false;
					}
				} else {
					int index;
					index = setupNetCDFColumn(columnInfoList, columnTypes,
							leNames[ii], ehNames[ii], LOWER_ERROR_VALUE);
					if (index == -1) {
						return false;
					}
					index = setupNetCDFColumn(columnInfoList, columnTypes,
							ueNames[ii], ehNames[ii], UPPER_ERROR_VALUE);
					if (index == -1) {
						return false;
					}
					if (!isNumberColumn(columnInfoList, index)) {
						return false;
					}
					if (isCoordinateVariableColumn(columnInfoList, index)) {
						return false;
					}
				}
			}
		}

		// tick labels
		String[] tlNames = getNames(nodeMap, new String[] {
				KEY_TICK_LABEL_NAME, KEY_TICK_LABEL_NAMES,
				KEY_TICK_LABEL_COLUMN_INDICES },
				groupName);
		String[] thNames = getNames(nodeMap, new String[] {
				KEY_TICK_LABEL_HOLDER_NAME, KEY_TICK_LABEL_HOLDER_NAMES,
				KEY_TICK_LABEL_HOLDER_COLUMN_INDICES }, groupName);
		if (tlNames != null && thNames != null) {
			if (tlNames.length != thNames.length) {
				return false;
			}
			for (int ii = 0; ii < thNames.length; ii++) {
				final int index = findNetCDFColumnInfo(columnInfoList, thNames[ii]);
				if (index == -1) {
					return false;
				}
				if (isCoordinateVariableColumn(columnInfoList, index)) {
					return false;
				}
				if (!xIndices.contains(index) && !yIndices.contains(index)) {
					return false;
				}
			}
			for (int ii = 0; ii < thNames.length; ii++) {
				final int index = setupNetCDFColumn(columnInfoList, columnTypes,
						tlNames[ii], thNames[ii], TICK_LABEL);
				if (index == -1) {
					return false;
				}
				if (isCoordinateVariableColumn(columnInfoList, index)) {
					return false;
				}
			}
		}

		// time
		String[] timeNames = getNames(nodeMap, KEY_TIME_VARIABLE_NAME,
				groupName);
		if (timeNames != null) {
			if (timeNames.length != 1) {
				return false;
			}
			final int index = setNetCDFColumnType(columnInfoList, columnTypes,
					timeNames[0], ANIMATION_FRAME);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index)) {
				return false;
			}
			if (!isCoordinateVariableColumn(columnInfoList, index)) {
				return false;
			}
		}

		Boolean variable = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE);
		if (variable != null) {
			if (!variable.booleanValue()) {
				// pick up
				String[] pickupNames = getNames(nodeMap,
						KEY_PICKUP_DIMENSION_NAME, groupName);
				if (pickupNames != null) {
					if (pickupNames.length != 1) {
						return false;
					}
					final int index = setNetCDFColumnType(columnInfoList, columnTypes,
							pickupNames[0], PICKUP);
					if (index == -1) {
						return false;
					}
					if (!isNumberColumn(columnInfoList, index)) {
						return false;
					}
					if (!isCoordinateVariableColumn(columnInfoList, index)) {
						return false;
					}
				}
			}
		}

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}

	static class IndexPair {
		int index1;
		int index2;
		
		public boolean partiallyEquals(IndexPair pair) {
			return (this.index1 == pair.index1) || (this.index2 == pair.index2);
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append('(');
			sb.append(index1);
			sb.append(',');
			sb.append(index2);
			sb.append(')');
			return sb.toString();
		}
	}

	private static boolean getForVXYMDArrayData(
			final Map<String, Object> infoMap, final SGMDArrayVariable[] vars,
			final int size,
			final SGDataColumnInfo[] columns) {
		if (getForVXYMDArrayDataNormal(infoMap, vars, size, columns)) {
			infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, true);
			return true;
		}
		if (getForVXYMDArrayDataIndex(infoMap, vars, size, columns)) {
			infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG, false);
			return true;
		}
		return false;
	}
	
	private static boolean getForVXYMDArrayDataNormal(
			final Map<String, Object> infoMap, final SGMDArrayVariable[] vars,
			final int size,
			final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		List<Integer> candidates = new ArrayList<Integer>();
		for (int ii = 0; ii < columns.length; ii++) {
			SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
			int[] dims = mdInfo.getDimensions();
			if (dims.length >= 2) {
				candidates.add(ii);
			}
		}
		if (candidates.size() < 2) {
			return false;
		}

		int fVarIndex = -1;
		int fXDim = -1;
		int fYDim = -1;
		int sVarIndex = -1;
		int sXDim = -1;
		int sYDim = -1;
		for (int ii = 0; ii < candidates.size() - 1; ii++) {
			Integer index1 = candidates.get(ii);
			SGMDArrayDataColumnInfo c1 = (SGMDArrayDataColumnInfo) columns[index1];
			final int[] dims1 = c1.getDimensions();
			for (int jj = ii + 1; jj < candidates.size(); jj++) {
				Integer index2 = candidates.get(jj);
				SGMDArrayDataColumnInfo c2 = (SGMDArrayDataColumnInfo) columns[index2];
				final int[] dims2 = c2.getDimensions();
				List<IndexPair> pairs = new ArrayList<IndexPair>();
				for (int kk = 0; kk < dims1.length; kk++) {
					for (int ll = 0; ll < dims2.length; ll++) {
						if (dims1[kk] == dims2[ll]) {
							IndexPair pair = new IndexPair();
							pair.index1 = kk;
							pair.index2 = ll;
							pairs.add(pair);
						}
					}
				}
				if (pairs.size() > 1) {
					// finds two pairs without overlapping
					IndexPair pairX = null;
					IndexPair pairY = null;
					for (int kk = 0; kk < pairs.size() - 1; kk++) {
						IndexPair p1 = pairs.get(kk);
						for (int ll = kk + 1; ll < pairs.size(); ll++) {
							IndexPair p2 = pairs.get(ll);
							if (!p1.partiallyEquals(p2)) {
								pairX = p1;
								pairY = p2;
								break;
							}
						}
						if (pairX != null && pairY != null) {
							break;
						}
					}
					if (pairX == null || pairY == null) {
						continue;
					}
					fVarIndex = index1;
					sVarIndex = index2;
					fXDim = pairX.index1;
					sXDim = pairX.index2;
					fYDim = pairY.index1;
					sYDim = pairY.index2;
					break;
				}
			}
			if (fVarIndex != -1) {
				break;
			}
		}

		if (fVarIndex == -1 || fXDim == -1 || fYDim == -1) {
			return false;
		}
		if (sVarIndex == -1 || sXDim == -1 || sYDim == -1) {
			return false;
		}

		final String first = SGDataUtility.getVXYFirstComponentColumnType(infoMap);
		final String second = SGDataUtility.getVXYSecondComponentColumnType(infoMap);
		columnTypes[fVarIndex] = first;
		columnTypes[sVarIndex] = second;

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
			SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
			mdInfo.setGenericDimensionIndex(0);
		}

		// sets xDim and yDim
		SGMDArrayDataColumnInfo fVarInfo = (SGMDArrayDataColumnInfo) columns[fVarIndex];
		fVarInfo.setDimensionIndex(SGIMDArrayConstants.KEY_VXY_X_DIMENSION, fXDim);
		fVarInfo.setDimensionIndex(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION, fYDim);
		SGMDArrayDataColumnInfo sVarInfo = (SGMDArrayDataColumnInfo) columns[sVarIndex];
		sVarInfo.setDimensionIndex(SGIMDArrayConstants.KEY_VXY_X_DIMENSION, sXDim);
		sVarInfo.setDimensionIndex(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION, sYDim);

		return true;
	}

	private static boolean getForVXYMDArrayDataIndex(
			final Map<String, Object> infoMap, final SGMDArrayVariable[] vars,
			final int size,
			final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		TreeMap<Integer, List<MDArrayDimension>> varDimListMap = createDimensionListMap(columns);
		List<MDArrayDimension> dimList = extractDimensions(varDimListMap, 4);
		if (dimList == null) {
			return false;
		}
		MDArrayDimension xDim = dimList.get(0);
		MDArrayDimension yDim = dimList.get(1);
		MDArrayDimension fDim = dimList.get(2);
		MDArrayDimension sDim = dimList.get(3);
		final String first = SGDataUtility.getVXYFirstComponentColumnType(infoMap);
		final String second = SGDataUtility.getVXYSecondComponentColumnType(infoMap);
		columnTypes[xDim.variableIndex] = X_COORDINATE;
		columnTypes[yDim.variableIndex] = Y_COORDINATE;
		columnTypes[fDim.variableIndex] = first;
		columnTypes[sDim.variableIndex] = second;

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
			SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
			mdInfo.setGenericDimensionIndex(0);
		}

		// sets dimensions
		setDimension(columns, xDim);
		setDimension(columns, yDim);
		setDimension(columns, fDim);
		setDimension(columns, sDim);

		return true;
	}
	
	private static void setDimension(SGDataColumnInfo[] columns, MDArrayDimension dim) {
		SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[dim.variableIndex];
		mdInfo.setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, dim.dimensionIndex);
	}

	private static boolean getForVXYMDArrayData(
			final List<SGDataColumnInfo> columnInfoList,
			final Map<String, Object> infoMap, final NamedNodeMap nodeMap,
			final String groupName, final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		int[] dimensionIndex = new int[columns.length];
		for (int ii = 0; ii < dimensionIndex.length; ii++) {
			dimensionIndex[ii] = 0;
		}

		int index;

		// x-coordinate
		SGMDArrayDataColumnInfo xCol = null;
		int xIndex = -1;
		String[] xNames = getNames(nodeMap, new String[] { KEY_X_COORDINATE_VARIABLE_NAME,
				KEY_X_COORDINATE_COLUMN_INDEX }, groupName);
		if (xNames != null) {
			if (xNames.length != 1) {
				return false;
			}
			index = setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, xNames[0],
					X_COORDINATE);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index)) {
				return false;
			}
			xIndex = dimensionIndex[index];
			xCol = (SGMDArrayDataColumnInfo) columns[index];
		}

		// y-coordinate
		SGMDArrayDataColumnInfo yCol = null;
		int yIndex = -1;
		String[] yNames = getNames(nodeMap, new String[] { KEY_Y_COORDINATE_VARIABLE_NAME,
				KEY_Y_COORDINATE_COLUMN_INDEX }, groupName);
		if (yNames != null) {
			if (yNames.length != 1) {
				return false;
			}
			index = setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, yNames[0],
					Y_COORDINATE);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index)) {
				return false;
			}
			yIndex = dimensionIndex[index];
			yCol = (SGMDArrayDataColumnInfo) columns[index];
		}

		String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
		if (dataType == null) {
			return false;
		}
    	Boolean gridPlot = SGDataUtility.isGridPlot(dataType, infoMap);
    	if (gridPlot == null) {
    		return false;
    	}

		// the first and the second component
    	final String first = SGDataUtility.getVXYFirstComponentColumnType(infoMap);
    	final String second = SGDataUtility.getVXYSecondComponentColumnType(infoMap);
		Map<String, Integer> dimensionMap = null;

		String[] fNames = getNames(nodeMap, new String[] { KEY_FIRST_COMPONENT_VARIABLE_NAME,
				KEY_FIRST_COMPONENT_COLUMN_INDEX }, groupName);
		if (fNames == null) {
			return false;
		}
		if (fNames.length != 1) {
			return false;
		}
		if (gridPlot) {
			dimensionMap = new HashMap<String, Integer>();
			index = setupMDArrayColumn2Dim(columnInfoList, columnTypes,
					dimensionMap, SGIMDArrayConstants.KEY_VXY_X_DIMENSION,
					SGIMDArrayConstants.KEY_VXY_Y_DIMENSION, fNames[0], first);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index)) {
				return false;
			}
			SGMDArrayDataColumnInfo fCol = (SGMDArrayDataColumnInfo) columns[index];
			int[] fDim = fCol.getDimensions();
			if (xCol != null) {
				int[] xDim = xCol.getDimensions();
				Integer fxIndex = dimensionMap.get(SGIMDArrayConstants.KEY_VXY_X_DIMENSION);
				if (fxIndex != null) {
					if (fDim[fxIndex] != xDim[xIndex]) {
						return false;
					}
				}
			}
			if (yCol != null) {
				int[] yDim = yCol.getDimensions();
				Integer fyIndex = dimensionMap.get(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION);
				if (fyIndex != null) {
					if (fDim[fyIndex] != yDim[yIndex]) {
						return false;
					}
				}
			}
			fCol.putAllDimensionIndices(dimensionMap);
		} else {
			index = setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, fNames[0],
					first);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index)) {
				return false;
			}
		}

		String[] sNames = getNames(nodeMap, new String[] { KEY_SECOND_COMPONENT_VARIABLE_NAME,
				KEY_SECOND_COMPONENT_COLUMN_INDEX }, groupName);
		if (sNames == null) {
			return false;
		}
		if (sNames.length != 1) {
			return false;
		}
		if (gridPlot) {
			dimensionMap = new HashMap<String, Integer>();
			index = setupMDArrayColumn2Dim(columnInfoList, columnTypes,
					dimensionMap, SGIMDArrayConstants.KEY_VXY_X_DIMENSION,
					SGIMDArrayConstants.KEY_VXY_Y_DIMENSION, sNames[0], second);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index)) {
				return false;
			}
			SGMDArrayDataColumnInfo sCol = (SGMDArrayDataColumnInfo) columns[index];
			int[] sDim = sCol.getDimensions();
			if (xCol != null) {
				int[] xDim = xCol.getDimensions();
				Integer sxIndex = dimensionMap.get(SGIMDArrayConstants.KEY_VXY_X_DIMENSION);
				if (sxIndex != null) {
					if (sDim[sxIndex] != xDim[xIndex]) {
						return false;
					}
				}
			}
			if (yCol != null) {
				int[] yDim = yCol.getDimensions();
				Integer syIndex = dimensionMap.get(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION);
				if (syIndex != null) {
					if (sDim[syIndex] != yDim[yIndex]) {
						return false;
					}
				}
			}
			sCol.putAllDimensionIndices(dimensionMap);
		} else {
			index = setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, sNames[0],
					second);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index)) {
				return false;
			}
		}

		for (int ii = 0; ii < columns.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) columns[ii];
			mdCol.setColumnType(columnTypes[ii]);
			mdCol.setGenericDimensionIndex(dimensionIndex[ii]);
		}

		return true;
	}

	private static boolean getForVXYNetCDFData(
			final Map<String, Object> infoMap, final List<SGNetCDFVariable> varList,
			final int size,
			final SGDataColumnInfo[] columns) {
		List<Integer> indices = getIndexVariableColumnIndices(columns);
		if (indices.size() > 0) {
			if (getForVXYNetCDFDataIndex(infoMap, varList, size, columns, indices)) {
				return true;
			}
			if (getForVXYNetCDFDataNormal(infoMap, varList, size, columns)) {
				return true;
			}
		} else {
			if (getForVXYNetCDFDataNormal(infoMap, varList, size, columns)) {
				return true;
			}
			if (getForVXYNetCDFDataIndex(infoMap, varList, size, columns, indices)) {
				return true;
			}
		}
		return false;
	}

	private static boolean getForVXYNetCDFDataNormal(
			final Map<String, Object> infoMap, final List<SGNetCDFVariable> varList,
			final int size, final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		int xIndex = -1;
		int yIndex = -1;
		int tIndex = -1;

		// a coordinate variable found first is assigned to the x variable
		// and found next is assigned to the y variable
		SGNetCDFVariable xVar = null;
		SGNetCDFVariable yVar = null;

		int startIndex = 0;
		int comIndex1 = -1;
		int comIndex2 = -1;
		List<Dimension> dimListCom1 = null;
		List<Dimension> dimListCom2 = null;

		for (int ii = 0; ii < varList.size() - 3; ii++) {
			int[] xyIndex = getNetCDFCoordinateVariableXYIndex(varList,
					startIndex);
			if (xyIndex == null) {
				startIndex += 1;
				continue;
			} else {
				xIndex = xyIndex[0];
				yIndex = xyIndex[1];
				xVar = varList.get(xIndex);
				yVar = varList.get(yIndex);
			}

			Dimension xDim = xVar.getDimension(0);
			Dimension yDim = yVar.getDimension(0);

			// variables for two components must have dimensions
			// those of x and y variables
			for (int jj = 0; jj < size; jj++) {
				SGNetCDFVariable var = varList.get(jj);
				if (!var.isCoordinateVariable() && VALUE_TYPE_NUMBER.equals(var.getValueType())) {
					List<Dimension> dimList = var.getDimensions();
					if (dimList.contains(xDim) && dimList.contains(yDim)) {
						comIndex1 = jj;
						dimListCom1 = dimList;
						break;
					}
				}
			}
			if (comIndex1 == -1 || comIndex1 == size - 1) {
				startIndex += 1;
				continue;
			}

			for (int jj = comIndex1 + 1; jj < size; jj++) {
				SGNetCDFVariable var = varList.get(jj);
				if (!var.isCoordinateVariable() && VALUE_TYPE_NUMBER.equals(var.getValueType())) {
					List<Dimension> dimList = var.getDimensions();
					if (dimList.contains(xDim) && dimList.contains(yDim)) {
						comIndex2 = jj;
						dimListCom2 = dimList;
						break;
					}
				}
			}
			if (comIndex2 != -1) {
				break;
			} else {
				startIndex += 1;
			}
		}

		if (comIndex1 == -1 || comIndex2 == -1) {
			return false;
		} else {
			dimListCom1 = varList.get(comIndex1).getDimensions();
			dimListCom2 = varList.get(comIndex2).getDimensions();
		}

		// unlimited coordinate variable is assigned to time variable
		// if it exists
		for (int ii = 0; ii < size; ii++) {
			if (ii == xIndex || ii == yIndex || ii == comIndex1
					|| ii == comIndex2) {
				continue;
			}
			SGNetCDFVariable var = varList.get(ii);
			if (var.isUnlimited() && var.isCoordinateVariable()) {
				Dimension dim = var.getDimension(0);
				if (dimListCom1.contains(dim) && dimListCom2.contains(dim)) {
					tIndex = ii;
				}
				break;
			}
		}

		columnTypes[xIndex] = X_COORDINATE;
		columnTypes[yIndex] = Y_COORDINATE;
		columnTypes[comIndex1] = SGDataUtility.getVXYFirstComponentColumnType(infoMap);
		columnTypes[comIndex2] = SGDataUtility.getVXYSecondComponentColumnType(infoMap);
		if (tIndex != -1) {
			columnTypes[tIndex] = ANIMATION_FRAME;
		}

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}

	// for serial number case
	private static boolean getForVXYNetCDFDataIndex(
			final Map<String, Object> infoMap, final List<SGNetCDFVariable> varList,
			final int size, final SGDataColumnInfo[] columns, List<Integer> serialNumberIndices) {

		List<SGNetCDFVariable> varListSorted = sortVariableList(varList, serialNumberIndices);

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		int iIndex = -1;
		int xIndex = -1;
		int yIndex = -1;
		int fIndex = -1;
		int sIndex = -1;
		for (int ii = 0; ii < varListSorted.size(); ii++) {
			SGNetCDFVariable var = varListSorted.get(ii);
			if (var.isCoordinateVariable()) {
				Dimension cDim = var.getDimension(0);
				for (int jj = 0; jj < varListSorted.size(); jj++) {
					if (jj == ii) {
						continue;
					}
					SGNetCDFVariable v = varListSorted.get(jj);
					if (!VALUE_TYPE_NUMBER.equals(v.getValueType())) {
						continue;
					}
					List<Dimension> dimList = v.getDimensions();
					if (!dimList.contains(cDim)) {
						continue;
					}
					if (xIndex == -1) {
						xIndex = jj;
					} else if (yIndex == -1) {
						yIndex = jj;
					} else if (fIndex == -1) {
						fIndex = jj;
					} else if (sIndex == -1) {
						sIndex = jj;
					}
				}
				if (xIndex != -1 && yIndex != -1 && fIndex != -1 && sIndex != -1) {
					// checks overlapping
					Set<Integer> indexSet = new HashSet<Integer>();
					indexSet.add(xIndex);
					indexSet.add(yIndex);
					indexSet.add(fIndex);
					indexSet.add(sIndex);
					if (indexSet.size() == 4) {
						iIndex = ii;
						break;
					}
				}

				// clears indices
				xIndex = -1;
				yIndex = -1;
				fIndex = -1;
				sIndex = -1;
			}
		}
		if (iIndex == -1 || xIndex == -1 || yIndex == -1 || fIndex == -1 || sIndex == -1) {
			return false;
		}

		columnTypes[iIndex] = INDEX;
		columnTypes[xIndex] = X_COORDINATE;
		columnTypes[yIndex] = Y_COORDINATE;
		columnTypes[fIndex] = SGDataUtility.getVXYFirstComponentColumnType(infoMap);;
		columnTypes[sIndex] = SGDataUtility.getVXYSecondComponentColumnType(infoMap);;

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}

	private static boolean getForVXYNetCDFData(
			final List<SGDataColumnInfo> columnInfoList,
			final Map<String, Object> infoMap, final NamedNodeMap nodeMap,
			final String groupName, final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		int idx;

		// serial numbers
		String[] indexNames = getNames(nodeMap,
				KEY_INDEX_VARIABLE_NAME, groupName);
		if (indexNames == null) {
			indexNames = getNames(nodeMap,
					KEY_SERIAL_NUMBER_VARIABLE_NAME, groupName);
		}
		int indexIdx = -1;
		if (indexNames != null) {
			if (indexNames.length != 1) {
				return false;
			}
			if ((indexIdx = setNetCDFColumnType(columnInfoList, columnTypes,
					indexNames[0], INDEX)) == -1) {
				return false;
			}
			if (!isCoordinateVariableColumn(columnInfoList, indexIdx)) {
				return false;
			}
		}

		// x-coordinate
		String[] xNames = getNames(nodeMap, new String[] { KEY_X_COORDINATE_VARIABLE_NAME,
				KEY_X_COORDINATE_COLUMN_INDEX }, groupName);
		if (xNames == null) {
			return false;
		}
		if (xNames.length != 1) {
			return false;
		}
		idx = setNetCDFColumnType(columnInfoList, columnTypes, xNames[0],
				X_COORDINATE);
		if (idx == -1) {
			return false;
		}
		if (!isNumberColumn(columnInfoList, idx)) {
			return false;
		}
		if (indexIdx != -1) {
			if (isCoordinateVariableColumn(columnInfoList, idx)) {
				return false;
			}
		} else {
			String[] xIndexNames = getNames(nodeMap, new String[] { KEY_X_INDEX_VARIABLE_NAME }, groupName);
			if (xIndexNames != null) {
				if (xIndexNames.length != 1) {
					return false;
				}
				idx = setNetCDFColumnType(columnInfoList, columnTypes, xIndexNames[0],
						X_INDEX);
				if (idx == -1) {
					return false;
				}
				if (!isNumberColumn(columnInfoList, idx)) {
					return false;
				}
				if (!isCoordinateVariableColumn(columnInfoList, idx)) {
					return false;
				}
			} else {
				if (!isCoordinateVariableColumn(columnInfoList, idx)) {
					return false;
				}
			}
		}

		// y-coordinate
		String[] yNames = getNames(nodeMap, new String[] { KEY_Y_COORDINATE_VARIABLE_NAME,
				KEY_Y_COORDINATE_COLUMN_INDEX }, groupName);
		if (yNames == null) {
			return false;
		}
		if (yNames.length != 1) {
			return false;
		}
		idx = setNetCDFColumnType(columnInfoList, columnTypes, yNames[0],
				Y_COORDINATE);
		if (idx == -1) {
			return false;
		}
		if (!isNumberColumn(columnInfoList, idx)) {
			return false;
		}
		if (indexIdx != -1) {
			if (isCoordinateVariableColumn(columnInfoList, idx)) {
				return false;
			}
		} else {
			String[] yIndexNames = getNames(nodeMap, new String[] { KEY_Y_INDEX_VARIABLE_NAME }, groupName);
			if (yIndexNames != null) {
				if (yIndexNames.length != 1) {
					return false;
				}
				idx = setNetCDFColumnType(columnInfoList, columnTypes, yIndexNames[0],
						Y_INDEX);
				if (idx == -1) {
					return false;
				}
				if (!isNumberColumn(columnInfoList, idx)) {
					return false;
				}
				if (!isCoordinateVariableColumn(columnInfoList, idx)) {
					return false;
				}
			} else {
				if (!isCoordinateVariableColumn(columnInfoList, idx)) {
					return false;
				}
			}
		}

		// the first and the second component
    	final String first = SGDataUtility.getVXYFirstComponentColumnType(infoMap);
    	final String second = SGDataUtility.getVXYSecondComponentColumnType(infoMap);

		String[] fNames = getNames(nodeMap, new String[] { KEY_FIRST_COMPONENT_VARIABLE_NAME,
				KEY_FIRST_COMPONENT_COLUMN_INDEX }, groupName);
		if (fNames == null) {
			return false;
		}
		if (fNames.length != 1) {
			return false;
		}
		idx = setNetCDFColumnType(columnInfoList, columnTypes, fNames[0], first);
		if (idx == -1) {
			return false;
		}
		if (!isNumberColumn(columnInfoList, idx)) {
			return false;
		}
		if (isCoordinateVariableColumn(columnInfoList, idx)) {
			return false;
		}

		String[] sNames = getNames(nodeMap, new String[] { KEY_SECOND_COMPONENT_VARIABLE_NAME,
				KEY_SECOND_COMPONENT_COLUMN_INDEX }, groupName);
		if (sNames == null) {
			return false;
		}
		if (sNames.length != 1) {
			return false;
		}
		idx = setNetCDFColumnType(columnInfoList, columnTypes, sNames[0], second);
		if (idx == -1) {
			return false;
		}
		if (!isNumberColumn(columnInfoList, idx)) {
			return false;
		}
		if (isCoordinateVariableColumn(columnInfoList, idx)) {
			return false;
		}

		// time
		String[] timeNames = getNames(nodeMap, KEY_TIME_VARIABLE_NAME, groupName);
		if (timeNames != null) {
			if (timeNames.length != 1) {
				return false;
			}
			idx = setNetCDFColumnType(columnInfoList, columnTypes,
					timeNames[0], ANIMATION_FRAME);
			if (idx == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, idx)) {
				return false;
			}
			if (!isCoordinateVariableColumn(columnInfoList, idx)) {
				return false;
			}
		}

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}

	private static boolean getForSXYZMDArrayData(
			final Map<String, Object> infoMap, final SGMDArrayVariable[] vars,
			final int size,
			final SGDataColumnInfo[] columns) {
		if (getForSXYZMDArrayDataNormal(infoMap, vars, size, columns)) {
			infoMap.put(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, true);
			return true;
		}
		if (getForSXYZMDArrayDataIndex(infoMap, vars, size, columns)) {
			infoMap.put(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG, false);
			return true;
		}
		return false;
	}
	
	private static boolean getForSXYZMDArrayDataNormal(
			final Map<String, Object> infoMap, final SGMDArrayVariable[] vars,
			final int size,
			final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		int zVarIndex = -1;
		int xDim = -1;
		int yDim = -1;
		for (int ii = 0; ii < columns.length; ii++) {
			SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
			int[] dims = mdInfo.getDimensions();
			if (dims.length >= 2) {
				zVarIndex = ii;
				xDim = 0;
				yDim = 1;
				break;
			}
		}
		if (zVarIndex == -1 || xDim == -1 || yDim == -1) {
			return false;
		}
		columnTypes[zVarIndex] = Z_VALUE;

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
			SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
			mdInfo.setGenericDimensionIndex(0);
		}

		// sets xDim and yDim
		SGMDArrayDataColumnInfo varInfo = (SGMDArrayDataColumnInfo) columns[zVarIndex];
		varInfo.setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, xDim);
		varInfo.setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, yDim);

		return true;
	}
	
	private static boolean getForSXYZMDArrayDataIndex(
			final Map<String, Object> infoMap, final SGMDArrayVariable[] vars,
			final int size,
			final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		TreeMap<Integer, List<MDArrayDimension>> varDimListMap = createDimensionListMap(columns);
		List<MDArrayDimension> dimList = extractDimensions(varDimListMap, 3);
		if (dimList == null) {
			return false;
		}
		MDArrayDimension xDim = dimList.get(0);
		MDArrayDimension yDim = dimList.get(1);
		MDArrayDimension zDim = dimList.get(2);
		columnTypes[xDim.variableIndex] = X_VALUE;
		columnTypes[yDim.variableIndex] = Y_VALUE;
		columnTypes[zDim.variableIndex] = Z_VALUE;

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
			SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
			mdInfo.setGenericDimensionIndex(0);
		}

		// sets dimensions
		setDimension(columns, xDim);
		setDimension(columns, yDim);
		setDimension(columns, zDim);

		return true;
	}
	
	private static boolean getForSXYZNetCDFData(
			final Map<String, Object> infoMap, final List<SGNetCDFVariable> varList,
			final int size,
			final SGDataColumnInfo[] columns) {
		List<Integer> indices = getIndexVariableColumnIndices(columns);
		if (indices.size() > 0) {
			if (getForSXYZNetCDFDataIndex(infoMap, varList, size, columns, indices)) {
				return true;
			}
			if (getForSXYZNetCDFDataNormal(infoMap, varList, size, columns)) {
				return true;
			}
		} else {
			if (getForSXYZNetCDFDataNormal(infoMap, varList, size, columns)) {
				return true;
			}
			if (getForSXYZNetCDFDataIndex(infoMap, varList, size, columns, indices)) {
				return true;
			}
		}
		return false;
	}

	private static boolean getForSXYZNetCDFDataNormal(
			final Map<String, Object> infoMap, final List<SGNetCDFVariable> varList,
			final int size,
			final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		int xIndex = -1;
		int yIndex = -1;
		int tIndex = -1;

		// a coordinate variable found first is assigned to the x variable
		// and found next is assigned to the y variable
		SGNetCDFVariable xVar = null;
		SGNetCDFVariable yVar = null;
		int startIndex = 0;
		int zIndex = -1;
		List<Dimension> dimListZ = null;
		for (int ii = 0; ii < varList.size() - 2; ii++) {
			int[] xyIndex = getNetCDFCoordinateVariableXYIndex(varList,
					startIndex);
			if (xyIndex == null) {
				startIndex += 1;
				continue;
			} else {
				xIndex = xyIndex[0];
				yIndex = xyIndex[1];
				xVar = varList.get(xIndex);
				yVar = varList.get(yIndex);
			}

			Dimension xDim = xVar.getDimension(0);
			Dimension yDim = yVar.getDimension(0);
			for (int jj = 0; jj < size; jj++) {
				SGNetCDFVariable var = varList.get(jj);
				if (!var.isCoordinateVariable() && VALUE_TYPE_NUMBER.equals(var.getValueType())) {
					List<Dimension> dimList = var.getDimensions();
					if (dimList.contains(xDim) && dimList.contains(yDim)) {
						zIndex = jj;
						break;
					}
				}
			}
			if (zIndex != -1) {
				break;
			} else {
				startIndex += 1;
			}
		}
		if (zIndex == -1) {
			return false;
		} else {
			dimListZ = varList.get(zIndex).getDimensions();
		}

		// unlimited coordinate variable is assigned to time variable
		// if it exists
		for (int ii = 0; ii < size; ii++) {
			if (ii == xIndex || ii == yIndex || ii == zIndex) {
				continue;
			}
			SGNetCDFVariable var = varList.get(ii);
			if (var.isUnlimited() && var.isCoordinateVariable()) {
				Dimension dim = var.getDimension(0);
				if (dimListZ.contains(dim)) {
					tIndex = ii;
				}
				break;
			}
		}

		columnTypes[xIndex] = X_VALUE;
		columnTypes[yIndex] = Y_VALUE;
		columnTypes[zIndex] = Z_VALUE;
		if (tIndex != -1) {
			columnTypes[tIndex] = ANIMATION_FRAME;
		}

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}

	// for serial number case
	private static boolean getForSXYZNetCDFDataIndex(
			final Map<String, Object> infoMap, final List<SGNetCDFVariable> varList,
			final int size, final SGDataColumnInfo[] columns, List<Integer> serialNumberIndices) {

		List<SGNetCDFVariable> varListSorted = sortVariableList(varList, serialNumberIndices);

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		int iIndex = -1;
		int xIndex = -1;
		int yIndex = -1;
		int zIndex = -1;
		for (int ii = 0; ii < varListSorted.size(); ii++) {
			SGNetCDFVariable var = varListSorted.get(ii);
			if (var.isCoordinateVariable()) {
				Dimension cDim = var.getDimension(0);
				for (int jj = 0; jj < varListSorted.size(); jj++) {
					if (jj == ii) {
						continue;
					}
					SGNetCDFVariable v = varListSorted.get(jj);
					if (!VALUE_TYPE_NUMBER.equals(v.getValueType())) {
						continue;
					}
					List<Dimension> dimList = v.getDimensions();
					if (!dimList.contains(cDim)) {
						continue;
					}
					if (xIndex == -1) {
						xIndex = jj;
					} else if (yIndex == -1) {
						yIndex = jj;
					} else if (zIndex == -1) {
						zIndex = jj;
					}
				}
				if (xIndex != -1 && yIndex != -1 && zIndex != -1) {
					// checks overlapping
					Set<Integer> indexSet = new HashSet<Integer>();
					indexSet.add(xIndex);
					indexSet.add(yIndex);
					indexSet.add(zIndex);
					if (indexSet.size() == 3) {
						iIndex = ii;
						break;
					}
				}

				// clears indices
				xIndex = -1;
				yIndex = -1;
				zIndex = -1;
			}
		}
		if (iIndex == -1 || xIndex == -1 || yIndex == -1 || zIndex == -1) {
			return false;
		}

		columnTypes[iIndex] = INDEX;
		columnTypes[xIndex] = X_VALUE;
		columnTypes[yIndex] = Y_VALUE;
		columnTypes[zIndex] = Z_VALUE;

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}

	private static boolean getForSXYZNetCDFData(
			final List<SGDataColumnInfo> columnInfoList,
			final Map<String, Object> infoMap, final NamedNodeMap nodeMap,
			final String groupName, final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		int idx;

		// index
		String[] indexNames = getNames(nodeMap,
				KEY_INDEX_VARIABLE_NAME, groupName);
		if (indexNames == null) {
			indexNames = getNames(nodeMap,
					KEY_SERIAL_NUMBER_VARIABLE_NAME, groupName);
		}
		int indexIdx = -1;
		if (indexNames != null) {
			if (indexNames.length != 1) {
				return false;
			}
			if ((indexIdx = setNetCDFColumnType(columnInfoList, columnTypes,
					indexNames[0], INDEX)) == -1) {
				return false;
			}
			if (!isCoordinateVariableColumn(columnInfoList, indexIdx)) {
				return false;
			}
		}

		// x, y and z values
		String[] xNames = getNames(nodeMap, new String[] { KEY_X_VALUE_NAME,
				KEY_X_VALUE_COLUMN_INDEX }, groupName);
		if (xNames == null) {
			return false;
		}
		if (xNames.length != 1) {
			return false;
		}
		idx = setNetCDFColumnType(columnInfoList, columnTypes, xNames[0],
				X_VALUE);
		if (idx == -1) {
			return false;
		}
		if (!isNumberColumn(columnInfoList, idx)) {
			return false;
		}
		if (indexIdx != -1) {
			if (isCoordinateVariableColumn(columnInfoList, idx)) {
				return false;
			}
		} else {
			String[] xIndexNames = getNames(nodeMap, new String[] { KEY_X_INDEX_VARIABLE_NAME }, groupName);
			if (xIndexNames != null) {
				if (xIndexNames.length != 1) {
					return false;
				}
				idx = setNetCDFColumnType(columnInfoList, columnTypes, xIndexNames[0],
						X_INDEX);
				if (idx == -1) {
					return false;
				}
				if (!isNumberColumn(columnInfoList, idx)) {
					return false;
				}
				if (!isCoordinateVariableColumn(columnInfoList, idx)) {
					return false;
				}
			} else {
				if (!isCoordinateVariableColumn(columnInfoList, idx)) {
					return false;
				}
			}
		}

		String[] yNames = getNames(nodeMap, new String[] { KEY_Y_VALUE_NAME,
				KEY_Y_VALUE_COLUMN_INDEX }, groupName);
		if (yNames == null) {
			return false;
		}
		if (yNames.length != 1) {
			return false;
		}
		idx = setNetCDFColumnType(columnInfoList, columnTypes, yNames[0],
				Y_VALUE);
		if (idx == -1) {
			return false;
		}
		if (!isNumberColumn(columnInfoList, idx)) {
			return false;
		}
		if (indexIdx != -1) {
			if (isCoordinateVariableColumn(columnInfoList, idx)) {
				return false;
			}
		} else {
			String[] yIndexNames = getNames(nodeMap, new String[] { KEY_Y_INDEX_VARIABLE_NAME }, groupName);
			if (yIndexNames != null) {
				if (yIndexNames.length != 1) {
					return false;
				}
				idx = setNetCDFColumnType(columnInfoList, columnTypes, yIndexNames[0],
						Y_INDEX);
				if (idx == -1) {
					return false;
				}
				if (!isNumberColumn(columnInfoList, idx)) {
					return false;
				}
				if (!isCoordinateVariableColumn(columnInfoList, idx)) {
					return false;
				}
			} else {
				if (!isCoordinateVariableColumn(columnInfoList, idx)) {
					return false;
				}
			}
		}

		String[] zNames = getNames(nodeMap, new String[] { KEY_Z_VALUE_NAME,
				KEY_Z_VALUE_COLUMN_INDEX }, groupName);
		if (zNames == null) {
			return false;
		}
		if (zNames.length != 1) {
			return false;
		}
		idx = setNetCDFColumnType(columnInfoList, columnTypes, zNames[0],
				Z_VALUE);
		if (idx == -1) {
			return false;
		}
		if (!isNumberColumn(columnInfoList, idx)) {
			return false;
		}
		if (isCoordinateVariableColumn(columnInfoList, idx)) {
			return false;
		}

		// time
		String[] timeNames = getNames(nodeMap, KEY_TIME_VARIABLE_NAME, groupName);
		if (timeNames != null) {
			if (timeNames.length != 1) {
				return false;
			}
			idx = setNetCDFColumnType(columnInfoList, columnTypes,
					timeNames[0], ANIMATION_FRAME);
			if (idx == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, idx)) {
				return false;
			}
			if (!isCoordinateVariableColumn(columnInfoList, idx)) {
				return false;
			}
		}

		for (int ii = 0; ii < columns.length; ii++) {
			columns[ii].setColumnType(columnTypes[ii]);
		}

		return true;
	}

	private static boolean getForSXYZMDArrayData(
			final List<SGDataColumnInfo> columnInfoList,
			final Map<String, Object> infoMap, final NamedNodeMap nodeMap,
			final String groupName, final SGDataColumnInfo[] columns) {

		String[] columnTypes = new String[columns.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = "";
		}

		int[] dimensionIndex = new int[columns.length];
		for (int ii = 0; ii < dimensionIndex.length; ii++) {
			dimensionIndex[ii] = 0;
		}

		int index;

		// x, y and z values
		SGMDArrayDataColumnInfo xCol = null;
		int xIndex = -1;
		String[] xNames = getNames(nodeMap, new String[] { KEY_X_VALUE_NAME,
				KEY_X_VALUE_COLUMN_INDEX }, groupName);
		if (xNames != null) {
			if (xNames.length != 1) {
				return false;
			}
			index = setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, xNames[0],
					X_VALUE);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index)) {
				return false;
			}
			xIndex = dimensionIndex[index];
			xCol = (SGMDArrayDataColumnInfo) columns[index];
		}

		SGMDArrayDataColumnInfo yCol = null;
		int yIndex = -1;
		String[] yNames = getNames(nodeMap, new String[] { KEY_Y_VALUE_NAME,
				KEY_Y_VALUE_COLUMN_INDEX }, groupName);
		if (yNames != null) {
			if (yNames.length != 1) {
				return false;
			}
			index = setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, yNames[0],
					Y_VALUE);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index)) {
				return false;
			}
			yIndex = dimensionIndex[index];
			yCol = (SGMDArrayDataColumnInfo) columns[index];
		}

		String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
		if (dataType == null) {
			return false;
		}
    	Boolean gridPlot = SGDataUtility.isGridPlot(dataType, infoMap);
    	if (gridPlot == null) {
    		return false;
    	}

		String[] zNames = getNames(nodeMap, new String[] { KEY_Z_VALUE_NAME,
				KEY_Z_VALUE_COLUMN_INDEX }, groupName);
		if (zNames == null) {
			return false;
		}
		if (zNames.length != 1) {
			return false;
		}
		Map<String, Integer> dimensionMap = new HashMap<String, Integer>();
		if (gridPlot) {
			index = setupMDArrayColumn2Dim(columnInfoList, columnTypes,
					dimensionMap, SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION,
					SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, zNames[0], Z_VALUE);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index)) {
				return false;
			}
			SGMDArrayDataColumnInfo zCol = (SGMDArrayDataColumnInfo) columns[index];
			int[] zDim = zCol.getDimensions();
			if (xCol != null) {
				int[] xDim = xCol.getDimensions();
				Integer zxIndex = dimensionMap.get(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION);
				if (zxIndex != null) {
					if (zDim[zxIndex] != xDim[xIndex]) {
						return false;
					}
				}
			}
			if (yCol != null) {
				int[] yDim = yCol.getDimensions();
				Integer zyIndex = dimensionMap.get(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION);
				if (zyIndex != null) {
					if (zDim[zyIndex] != yDim[yIndex]) {
						return false;
					}
				}
			}
			zCol.putAllDimensionIndices(dimensionMap);
		} else {
			index = setupMDArrayColumn(columnInfoList, columnTypes, dimensionIndex, zNames[0],
					Z_VALUE);
			if (index == -1) {
				return false;
			}
			if (!isNumberColumn(columnInfoList, index)) {
				return false;
			}
		}

		for (int ii = 0; ii < columns.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) columns[ii];
			mdCol.setColumnType(columnTypes[ii]);
			mdCol.setGenericDimensionIndex(dimensionIndex[ii]);
		}

		return true;
	}

	private static int[] getNetCDFCoordinateVariableXYIndex(
			final List<SGNetCDFVariable> varList, final int startIndex) {
		final int size = varList.size();
		int xIndex = -1;
		int yIndex = -1;

		// a coordinate variable found first is assigned to the x variable
		// and found next is assigned to the y variable
		xIndex = getNetCDFCoordinateVariableIndexWithoutUnlimited(varList,
				startIndex);
		if (xIndex == -1 || xIndex == size - 1) {
			xIndex = getNetCDFCoordinateVariableIndex(varList, startIndex);
		}
		if (xIndex == -1 || xIndex == size - 1) {
			return null;
		}

		yIndex = getNetCDFCoordinateVariableIndexWithoutUnlimited(varList,
				xIndex + 1);
		if (yIndex == -1) {
			yIndex = getNetCDFCoordinateVariableIndex(varList, xIndex + 1);
		}
		if (yIndex == -1) {
			return null;
		}

		return new int[] { xIndex, yIndex };
	}

	private static int getNetCDFCoordinateVariableIndex(
			final List<SGNetCDFVariable> varList, final int startIndex) {
		final int size = varList.size();
		int index = -1;

		for (int ii = startIndex; ii < size; ii++) {
			SGNetCDFVariable var = varList.get(ii);
			if (var.isCoordinateVariable()) {
				index = ii;
				break;
			}
		}

		return index;
	}

	private static int setNetCDFColumnType(
			final List<SGDataColumnInfo> columnInfoList,
			final String[] columnTypes, final String varName, final String value) {
		int index = findNetCDFColumnInfo(columnInfoList, varName);
		if (index != -1) {
			if (index < 0 || index >= columnTypes.length) {
				return -1;
			}
			columnTypes[index] = value;
		}
		return index;
	}

	private static int setupMDArrayColumn(
			final List<SGDataColumnInfo> columnInfoList,
			final String[] columnTypes, final int[] dimensionIndex,
			final String varName, final String value) {
		final int index = findMDArrayColumnInfo(columnInfoList, varName);
		if (index != -1) {
			if (index < 0 || index >= columnTypes.length) {
				return -1;
			}
			final int cIndex = varName.lastIndexOf(':');
			if (cIndex == -1 || cIndex == varName.length() - 1) {
				return -1;
			}
			final String dimString = varName.substring(cIndex + 1);
			Integer dim = SGUtilityText.getInteger(dimString);
			if (dim == null) {
				return -1;
			}
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) columnInfoList.get(index);
			int[] dims = mdCol.getDimensions();
			if (dim < 0 || dim >= dims.length) {
				return -1;
			}
			columnTypes[index] = value;
			dimensionIndex[index] = dim;
		}
		return index;
	}

	private static int setupMDArrayColumn2Dim(
			final List<SGDataColumnInfo> columnInfoList,
			final String[] columnTypes, final Map<String, Integer> dimensionMap,
			final String firstDimKey, final String secondDimKey,
			final String varName, final String value) {
		final int index = findMDArrayColumnInfo(columnInfoList, varName);
		if (index != -1) {
			if (index < 0 || index >= columnTypes.length) {
				return -1;
			}

			// the second dimension
			final int sIndex = varName.lastIndexOf(':');
			if (sIndex == -1 || sIndex == varName.length() - 1) {
				return -1;
			}
			final String sDimString = varName.substring(sIndex + 1);
			Integer sDim = SGUtilityText.getInteger(sDimString);
			if (sDim == null) {
				return -1;
			}

			// the first dimension
			final int fIndex = varName.lastIndexOf(':', sIndex - 1);
			if (fIndex == -1 || fIndex == varName.length() - 1) {
				return -1;
			}
			final String fDimString = varName.substring(fIndex + 1, sIndex);
			Integer fDim = SGUtilityText.getInteger(fDimString);
			if (fDim == null) {
				return -1;
			}

			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) columnInfoList.get(index);
			int[] dims = mdCol.getDimensions();
			if (sDim < 0 || sDim >= dims.length) {
				return -1;
			}
			if (fDim < 0 || fDim >= dims.length) {
				return -1;
			}
			columnTypes[index] = value;
			dimensionMap.put(firstDimKey, fDim);
			dimensionMap.put(secondDimKey, sDim);
		}
		return index;
	}

	private static int setupNetCDFColumn(
			final List<SGDataColumnInfo> columnInfoList,
			final String[] columnTypes, final String varName,
			final String holderVarName, final String value) {
		final int index = findNetCDFColumnInfo(columnInfoList, varName);
		if (index != -1) {
			if (index < 0 || index >= columnTypes.length) {
				return -1;
			}
			columnTypes[index] = SGDataUtility.appendColumnTitle(value,
					holderVarName);
		}
		return index;
	}

	private static int setMDArrayColumnType(
			final List<SGDataColumnInfo> columnInfoList,
			final String[] columnTypes, final int [] dimensionIndex,
			final String varName, final String holderVarName, final String value) {
		final int index = findMDArrayColumnInfo(columnInfoList, varName);
		if (index != -1) {
			if (index < 0 || index >= columnTypes.length) {
				return -1;
			}
			final int cIndex = varName.lastIndexOf(':');
			if (cIndex == -1 || cIndex == varName.length() - 1) {
				return -1;
			}
			String sub = varName.substring(cIndex + 1);
			Integer num = SGUtilityText.getInteger(sub);
			if (num == null) {
				return -1;
			}
			if (num < 0 || num >= columnTypes.length) {
				return -1;
			}
			columnTypes[index] = SGDataUtility.appendColumnTitle(value,
					holderVarName);
			dimensionIndex[index] = num;
		}
		return index;
	}

	private static int findNetCDFColumnInfo(
			final List<SGDataColumnInfo> columnInfoList, final String varName) {
		for (int ii = 0; ii < columnInfoList.size(); ii++) {
			SGNetCDFDataColumnInfo ncInfo = (SGNetCDFDataColumnInfo) columnInfoList
					.get(ii);
//			if (ncInfo.getName().equals(varName)) {
//				return ii;
//			}
			if (isEqualNetCDFName(ncInfo.getName(), varName)) {
				return ii;
			}
		}
		return -1;
	}
	
	// Compares two text strings for the name of NetCDF variables.
	// Slashes are replaces with underscores in the comparison.
	// This method is only for NetCDF data set file.
	private static boolean isEqualNetCDFName(String name1, String name2) {
		char[] cArray1 = name1.toCharArray();
		char[] cArray2 = name2.toCharArray();
		List<Character> cList1 = new ArrayList<Character>();
		List<Character> cList2 = new ArrayList<Character>();
		for (char c : cArray1) {
			if (c == '/') {
				c = '_';
			}
			cList1.add(c);
		}
		for (char c : cArray2) {
			if (c == '/') {
				c = '_';
			}
			cList2.add(c);
		}
		return cList1.equals(cList2);
	}

	// The argument "varName" is a text string that starts with the name of a data set
	// and ends with the dimension index.
	private static int findMDArrayColumnInfo(
			final List<SGDataColumnInfo> columnInfoList, final String varName) {
		for (int ii = 0; ii < columnInfoList.size(); ii++) {
			SGMDArrayDataColumnInfo ncInfo = (SGMDArrayDataColumnInfo) columnInfoList
					.get(ii);
			String name = ncInfo.getName();
			if (varName.toUpperCase().startsWith(name.toUpperCase())) {
				return ii;
			}
		}
		return -1;
	}

	private static int getNetCDFCoordinateVariableIndexWithoutUnlimited(
			final List<SGNetCDFVariable> varList, final int startIndex) {
		final int size = varList.size();
		int index = -1;
		for (int ii = startIndex; ii < size; ii++) {
			SGNetCDFVariable var = varList.get(ii);
			if (var.isCoordinateVariable() && !var.isUnlimited()) {
				// excepting unlimited variables first
				index = ii;
				break;
			}
		}
		return index;
	}

	private static String[] getNames(final NamedNodeMap nodeMap,
			final String[] keys, final String groupName) {
		for (int ii = 0; ii < keys.length; ii++) {
			String[] names = getNames(nodeMap, keys[ii], groupName);
			if (names != null) {
				return names;
			}
		}
		return null;
	}

	private static String[] getNames(final NamedNodeMap nodeMap,
			final String key, final String groupName) {
		String strNames = getString(nodeMap, key);
		if (strNames == null) {
			return null;
		}
		String[] names = SGUtilityText.parseStrings(strNames);
		if (groupName != null) {
			for (int ii = 0; ii < names.length; ii++) {
				names[ii] = SGDataUtility.appendGroupName(names[ii], groupName);
			}
		}
		return names;
	}

	private static String getString(final NamedNodeMap nodeMap, final String key) {
		Node nodeNames = nodeMap.getNamedItem(key);
		if (nodeNames == null) {
			return null;
		}
		String strNames = nodeNames.getNodeValue();
		if (strNames == null) {
			return null;
		}
		return strNames;
	}

	/**
	 * Finds and returns the origin map for netCDF data.
	 * If the origin map is not found, returns an empty map.
	 *
	 * @param nodeMap
	 *           a node map
	 * @return the origin map
	 */
	public static Map<String, Integer> getNetCDFOriginMap(NamedNodeMap nodeMap) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		if (nodeMap != null) {
			String originMapStr = getString(nodeMap, SGIDataPropertyKeyConstants.KEY_ORIGIN_MAP);
			if (originMapStr != null) {
				String[][] originMapArray = SGUtilityText.readStringMaps(originMapStr);
				for (int ii = 0; ii < originMapArray.length; ii++) {
					String key = originMapArray[ii][0];
					String value = originMapArray[ii][1];
					Integer num = SGUtilityText.getInteger(value);
					if (num == null) {
						return new HashMap<String, Integer>();
					}
					map.put(key, num);
				}
			}
		}
		return map;
	}

	/**
	 * Finds and returns the origin map for multidimensional data.
	 * If the origin map is not found, returns an empty map.
	 *
	 * @param nodeMap
	 *           a node map
	 * @return the origin map
	 */
	public static Map<String, int[]> getMDArrayDataOriginMap(NamedNodeMap nodeMap) {
		Map<String, int[]> map = new HashMap<String, int[]>();
		if (nodeMap != null) {
			String originMapStr = getString(nodeMap, SGIDataPropertyKeyConstants.KEY_ORIGIN_MAP);
			if (originMapStr != null) {
				char[] cArray = originMapStr.toCharArray();
				boolean skip = false;
				List<Integer> commaIndexList = new ArrayList<Integer>();
				for (int ii = 0; ii < cArray.length; ii++) {
					final char c = cArray[ii];
					if (c == '(') {
						skip = true;
					} else if (c == ')') {
						skip = false;
					} else if (c == ',') {
						if (!skip) {
							commaIndexList.add(ii);
						}
					}
				}
				List<String> originStrList = new ArrayList<String>();
				int beginIndex = 0;
				for (int ii = 0; ii <= commaIndexList.size(); ii++) {
					final int endIndex;
					if (ii < commaIndexList.size()) {
						endIndex = commaIndexList.get(ii);
					} else {
						endIndex = originMapStr.length();
					}
					String str = originMapStr.substring(beginIndex, endIndex);
					originStrList.add(str);
					beginIndex = endIndex + 1;
					if (beginIndex >= originMapStr.length()) {
						break;
					}
				}
				String[][] originStrArray = new String[originStrList.size()][];
				for (int ii = 0; ii < originStrArray.length; ii++) {
					String originStr = originStrList.get(ii);
					String[] keyValueStrArray = originStr.split("=");
					if (keyValueStrArray == null || keyValueStrArray.length != 2) {
						return null;
					}
					String key = keyValueStrArray[0];
					String value = keyValueStrArray[1];
					int[] numArray = SGUtilityText.getIntegerArray(value);
					if (numArray == null) {
						return null;
					}
					map.put(key, numArray);
				}
			}
		}
		return map;
	}

	private static List<Integer> getIndexVariableColumnIndices(SGDataColumnInfo[] columns) {
		final String idx = INDEX.toUpperCase();
		final String sn = SERIAL_NUMBERS.toUpperCase();
		List<Integer> indices = new ArrayList<Integer>();
		for (int ii = 0; ii < columns.length; ii++) {
			String name = columns[ii].getName();
			String nUpper = name.toUpperCase();
			if (nUpper.endsWith(idx) || nUpper.endsWith(sn)) {
				indices.add(ii);
			}
		}
		return indices;
	}

	private static boolean isNumberColumn(final List<SGDataColumnInfo> columnInfoList,
			final int index) {
		SGDataColumnInfo col = columnInfoList.get(index);
		return VALUE_TYPE_NUMBER.equals(col.getValueType());
	}

	private static boolean isDateColumn(final List<SGDataColumnInfo> columnInfoList,
			final int index) {
		SGDataColumnInfo col = columnInfoList.get(index);
		return VALUE_TYPE_DATE.equals(col.getValueType());
	}

	private static boolean isCoordinateVariableColumn(final List<SGDataColumnInfo> columnInfoList,
			final int index) {
		SGNetCDFDataColumnInfo col = (SGNetCDFDataColumnInfo) columnInfoList.get(index);
		return col.isCoordinateVariable();
	}
	
	// Creates a map of dimensions.
	private static TreeMap<Integer, List<MDArrayDimension>> createDimensionListMap(SGDataColumnInfo[] columns) {
		TreeMap<Integer, List<MDArrayDimension>> varDimListMap = new TreeMap<Integer, List<MDArrayDimension>>();
		for (int ii = 0; ii < columns.length; ii++) {
			SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
			String valueType = mdInfo.getValueType();
			if (!SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER.equals(valueType)) {
				continue;
			}
			int[] dims = mdInfo.getDimensions();
			for (int jj = 0; jj < dims.length; jj++) {
				final int len = dims[jj];
				MDArrayDimension varDim = new MDArrayDimension(ii, jj);
				List<MDArrayDimension> varDimList = varDimListMap.get(len);
				if (varDimList == null) {
					varDimList = new ArrayList<MDArrayDimension>();
					varDimListMap.put(len, varDimList);
				}
				varDimList.add(varDim);
			}
		}
		return varDimListMap;
	}

	// Extracts dimensions.
	private static List<MDArrayDimension> extractDimensions(
			TreeMap<Integer, List<MDArrayDimension>> varDimListMap, final int num) {
		List<MDArrayDimension> dimList = null;
		Iterator<Entry<Integer, List<MDArrayDimension>>> itr = varDimListMap.descendingMap().entrySet().iterator();
		while (itr.hasNext()) {
			Entry<Integer, List<MDArrayDimension>> entry = itr.next();
			List<MDArrayDimension> varDimList = entry.getValue();
			if (varDimList.size() >= num) {
				Map<Integer, MDArrayDimension> dimMap = new TreeMap<Integer, MDArrayDimension>();
				for (MDArrayDimension dim : varDimList) {
					MDArrayDimension cur = dimMap.get(dim.variableIndex);
					if (cur == null) {
						dimMap.put(dim.variableIndex, dim);
					}
				}
				if (dimMap.size() >= num) {
					dimList = new ArrayList<MDArrayDimension>(dimMap.values());
					break;
				}
			}
		}
		return dimList;
	}

}

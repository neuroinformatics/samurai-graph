package jp.riken.brain.ni.samuraigraph.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementString;
import jp.riken.brain.ni.samuraigraph.base.SGIProgressControl;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayPickUpDimensionInfo;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFVariable;
import jp.riken.brain.ni.samuraigraph.data.SGSXYMDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyle;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupSetInGraphSXYMultiple.MultipleSXYElementGroupSetPropertiesInFigureElement;

class SGMainFunctionsSplitMerge implements SGIApplicationTextConstants {

    static boolean splitData(SGDrawingWindow wnd) {
        List<SGFigure> fList = wnd.getVisibleFigureList();

        SGIProgressControl progress = (SGIProgressControl) wnd;
        progress.setProgressMessage("Split Data");
        progress.startProgress();

        Map<SGFigure, List<SGData>> dataMap = new HashMap<SGFigure, List<SGData>>();
        List<SGData> dataListAddedAll = new ArrayList<SGData>();
        try {
            for (SGFigure f : fList) {
                // get focused data objects
                SGIFigureElementGraph gElement = f.getGraphElement();
                List<SGData> dataList = gElement.getFocusedDataList();
                List<SGData> dataListNew = new ArrayList<SGData>();
                for (SGData data : dataList) {
                	if (isSplitEnabled(data)) {
                    	dataListNew.add(data);
                	} else {
                		gElement.setDataFocused(data, false);
                	}
                }

                List<SGData> dataListAdded = splitDataSub(f, dataListNew, progress);
                if (dataListAdded == null) {
                	return false;
                }
                dataListAddedAll.addAll(dataListAdded);
                dataMap.put(f, dataListAdded);
            }

        } finally {
            // end the progress bar
            progress.endProgress();
        }

        if (dataListAddedAll.size() == 0) {
        	return true;
        }

        // hide the original data
        if (wnd.hideSelectedObjects() == false) {
            return false;
        }

        // make the new data focused
        Iterator<SGFigure> itr = dataMap.keySet().iterator();
        while (itr.hasNext()) {
            SGFigure f = itr.next();
            List<SGData> dList = dataMap.get(f);
            SGIFigureElementGraph gElement = f.getGraphElement();
            for (int ii = 0; ii < dList.size(); ii++) {
                SGData d = dList.get(ii);
                gElement.setDataFocused(d, true);
            }
        }

        wnd.notifyToRoot();

        return true;
    }

    /**
     * Split given data in a given figure.
     *
     * @param figure
     * @param dataList
     * @param progress
     * @return list of data which is added by splitting multiple data.
     * or null if failed to split.
     */
    private static List<SGData> splitDataSub(
            final SGFigure figure, final List<SGData> dataList, final SGIProgressControl progress) {
        SGIFigureElement[] elArray = figure.getIFigureElementArray();
        SGIFigureElementGraph gElement = figure.getGraphElement();
        SGIFigureElementString fes = (SGIFigureElementString) figure.getIFigureElement(SGIFigureElementString.class);

        List<SGData> dList = new ArrayList<SGData>();
        for (SGData data : dataList) {
            if (data instanceof SGISXYTypeMultipleData) {
            	final String dataType = data.getDataType();
                if (!SGDataUtility.isSDArrayData(dataType) && !SGDataUtility.isNetCDFData(dataType)
                		&& !SGDataUtility.isMDArrayData(dataType)) {
                    throw new Error("Unsupported data type: " + data.getDataType());
                }
                SGISXYTypeMultipleData dataMult = (SGISXYTypeMultipleData) data;
                String dataNameMult = gElement.getDataName(data);

                SGISXYTypeMultipleData[] subArray = dataMult.getSXYTypeMultipleDataArray();
                if (subArray.length == 1) {
                	subArray[0].dispose();
                    continue;
                }
                SGData[] dataArray = new SGData[subArray.length];
                for (int ii = 0; ii < dataArray.length; ii++) {
                    dataArray[ii] = (SGData) subArray[ii];
                }

                String[] nameArray = getNameOfSplittedData(dataMult, dataArray, dataNameMult, dataType);
                SGProperties pMult = gElement.getDataProperties((SGData) dataMult);

                final float diff = 1.0f / (dataArray.length + 1);
                for (int ii = 0; ii < dataArray.length; ii++) {
                    if (null!=progress) {
                        progress.setProgressValue((ii + 1) * diff);
                    }

                    // get data properties
                    SGProperties dp = dataArray[ii].getProperties();

                    Map<Class<? extends SGIFigureElement>, SGProperties> propertiesMap =
                        new HashMap<Class<? extends SGIFigureElement>, SGProperties>();
                    for (int jj = 0; jj < elArray.length; jj++) {

                        // synchronize the properties for duplicated data object
                        SGProperties sp = elArray[jj].synchronizeDataProperties(pMult, dp);
                        if (sp == null) {
                            continue;
                        }

                        if (sp instanceof MultipleSXYElementGroupSetPropertiesInFigureElement) {
                        	MultipleSXYElementGroupSetPropertiesInFigureElement mgp = (MultipleSXYElementGroupSetPropertiesInFigureElement) sp;
                        	List<SGLineStyle> lineStyleList = mgp.getLineStyleList();
                        	SGLineStyle lineStyle = lineStyleList.get(ii);
                        	List<SGLineStyle> lineStyleListNew = new ArrayList<SGLineStyle>();
                        	lineStyleListNew.add(lineStyle);
                        	mgp.setLineStyleList(lineStyleListNew);
                        }

                        // put into the map
                        propertiesMap.put(elArray[jj].getClass(), sp);
                    }
                    if (figure.addData(dataArray[ii], nameArray[ii], propertiesMap) == false) {
                        return null;
                    }
                }

                for (int ii = 0; ii < dataArray.length; ii++) {
                    dList.add(dataArray[ii]);
                }

                gElement.hideNetCDFLabels(fes, (SGData) dataMult);
            }
        }

        return dList;
    }

    static List<SGData> splitData(final SGFigure figure, final int[] dataIdArray) {
    	SGIFigureElementGraph gElement = figure.getGraphElement();
        List<SGData> dataList = new ArrayList<SGData>();
        List<Integer> hiddenDataIdList = new ArrayList<Integer>();
        for (int ii = 0; ii < dataIdArray.length; ii++) {
            SGData data = figure.getData(dataIdArray[ii]);
            if (data == null) {
                return null;
            }
            if (!gElement.isDataVisible(data)) {
            	return null;
            }
        	if (isSplitEnabled(data)) {
            	dataList.add(data);
            	hiddenDataIdList.add(dataIdArray[ii]);
        	}
        }
        if (dataList.size() == 0) {
        	return dataList;
        }

        List<SGData> dataListAdded = splitDataSub(figure, dataList, null);
        if (dataListAdded == null) {
        	return null;
        }
        if(dataListAdded.size() == 0) {
        	return dataListAdded;
        }
        
        int[] hiddenDataIdArray = new int[hiddenDataIdList.size()];
        for (int ii = 0; ii < hiddenDataIdArray.length; ii++) {
        	hiddenDataIdArray[ii] = hiddenDataIdList.get(ii);
        }
        figure.hideData(hiddenDataIdArray);
        if (figure.isChangedRoot()) {
            figure.notifyToRoot();
        }
        figure.repaint();

        return dataListAdded;
    }
    
    private static boolean isSplitEnabled(SGData data) {
        if (data instanceof SGISXYTypeMultipleData) {
        	SGISXYTypeMultipleData sxyData = (SGISXYTypeMultipleData) data;
        	final int childNum = sxyData.getChildNumber();
        	return (childNum > 1);
        } else {
        	return false;
        }
    }

    /**
     * Returns the names of split data.
     *
     * @param dataMult
     * @param dataArray
     * @param dataNameMult
     * @param dataType
     * @return the names of split data
     */
    static String[] getNameOfSplittedData(
            final SGISXYTypeMultipleData dataMult,
            final SGData[] dataArray,
            final String dataNameMult, final String dataType) {

        String[] nameArray = new String[dataArray.length];

        for (int ii = 0; ii < dataArray.length; ii++) {
            StringBuffer sb = new StringBuffer();
            sb.append(dataNameMult);
            String suffix = "";
            if (SGDataUtility.isSDArrayData(dataType)) {
                SGSXYSDArrayMultipleData aData = (SGSXYSDArrayMultipleData) dataMult;
                SGDataColumn[] cols = aData.getMultipleColumns();
                String title = cols[ii].getTitle();
                if (title == null || "".equals(title)) {
                    suffix = Integer.toString(ii + 1);
                } else {
                    suffix = SGUtility.addEscapeChar(title);
                }

                sb.append("\\_");
                sb.append(suffix);
            } else if (SGDataUtility.isNetCDFData(dataType)) {
                if (dataMult instanceof SGSXYNetCDFMultipleData) {
                	SGSXYNetCDFMultipleData nData = (SGSXYNetCDFMultipleData) dataMult;
                	if (nData.isDimensionPicked()) {
                        SGIntegerSeriesSet indices = nData.getPickUpDimensionInfo().getIndices();
                        final int[] array = indices.getNumbers();
                        final int index = array[ii];

                        Double dvalue = nData.getDimensionValue(index);
                        if (null==dvalue) {
                            suffix = Integer.toString(index);

                            sb.append("\\_");
                            sb.append(suffix);
                        } else {
                            double val = dvalue.doubleValue();
                            sb.append(" (");
                            sb.append(SGUtility.addEscapeChar(nData.getDimensionName()));
                            sb.append('=');
                            sb.append(SGUtility.getNumberName(val));
                            sb.append(")");
                        }
                	} else {
                        SGNetCDFVariable[] vars = nData.getMultipleVariables();
                        suffix = SGUtility.addEscapeChar(vars[ii].getName());
                        sb.append("\\_");
                        sb.append(suffix);
                	}
                }
            } else {
            	if (dataMult instanceof SGSXYMDArrayMultipleData) {
            		SGSXYMDArrayMultipleData mdData = (SGSXYMDArrayMultipleData) dataMult;
            		if (mdData.isDimensionPicked()) {
                    	SGMDArrayPickUpDimensionInfo mdInfo = (SGMDArrayPickUpDimensionInfo) mdData.getPickUpDimensionInfo();
                    	final int[] array = mdInfo.getIndices().getNumbers();
                    	final int index = array[ii];
                        sb.append(" (");
                        sb.append(index);
                        sb.append(")");
            		} else {
                        SGMDArrayVariable[] vars = mdData.getMultipleVariables();
                        suffix = SGUtility.addEscapeChar(vars[ii].getSimpleName());
                        sb.append("\\_");
                        sb.append(suffix);
            		}
            	}
            	/*
                if (dataMult instanceof SGSXYMultipleVariableMDData) {
                	SGSXYMultipleVariableMDData mdData = (SGSXYMultipleVariableMDData) dataMult;
                    SGMDVariable[] vars = mdData.getMultipleVariables();
                    suffix = SGUtility.addEscapeChar(vars[ii].getSimpleName());
                    sb.append("\\_");
                    sb.append(suffix);
                } else if (dataMult instanceof SGSXYMultipleDimensionMDData) {
                	SGSXYMultipleDimensionMDData mdData = (SGSXYMultipleDimensionMDData) dataMult;
                	SGMDPickUpDimensionInfo mdInfo = (SGMDPickUpDimensionInfo) mdData.getPickUpDimensionInfo();
//                    final int start = mdInfo.getStart();
//                    final int end = mdInfo.getEnd();
//                    final int step = Math.abs(mdInfo.getStep());
//                    int index = start;
//                    if (start <= end) {
//                        index = start + step * ii;
//                        if (index > end) {
//                            index = end;
//                        }
//                    } else {
//                        index = start - step * ii;
//                        if (index < end) {
//                            index = end;
//                        }
//                    }
                	final int[] array = mdInfo.getIndices().getNumbers();
                	final int index = array[ii];
                    sb.append(" (");
                    sb.append(index);
                    sb.append(")");
                }
                */
            }
            nameArray[ii] = sb.toString();
        }

        return nameArray;
    }

    static boolean mergeData(SGDrawingWindow wnd) {
        List<SGFigure> fList = wnd.getVisibleFigureList();

        SGIProgressControl progress = (SGIProgressControl) wnd;
        progress.setProgressMessage("Merge Data");
        progress.startProgress();

        Map<SGFigure, List<SGData>> dataMap = new HashMap<SGFigure, List<SGData>>();
        List<SGData> dataListAddedAll = new ArrayList<SGData>();
        List<SGData> hiddenDataList = new ArrayList<SGData>();
        try {
            for (SGFigure f : fList) {
                // get focused data objects
                SGIFigureElementGraph gElement = f.getGraphElement();
                List<SGData> dataList = gElement.getFocusedDataList();

                List<SGData> dataListAdded = mergeDataSub(f, dataList, hiddenDataList, progress);
                if (dataListAdded == null) {
                	SGUtility.showErrorMessageDialog(wnd, "Failed to merge the data.", SGIConstants.TITLE_ERROR);
                	return false;
                }
                dataListAddedAll.addAll(dataListAdded);
                dataMap.put(f, dataListAdded);
            }
        } finally {
            // end the progress bar
            progress.endProgress();
        }

        if (dataListAddedAll.size() == 0) {
            return true;
        }

        // hide the original data
        if (wnd.hideSelectedObjects() == false) {
            return false;
        }

        // make the new data focused
        Iterator<SGFigure> itr = dataMap.keySet().iterator();
        while (itr.hasNext()) {
            SGFigure f = itr.next();
            List<SGData> dList = dataMap.get(f);
            SGIFigureElementGraph gElement = f.getGraphElement();
            for (int ii = 0; ii < dList.size(); ii++) {
                SGData d = dList.get(ii);
                gElement.setDataFocused(d, true);
            }
        }

        wnd.notifyToRoot();

        return true;
    }

    private static List<SGData> mergeDataSub(
            final SGFigure figure, final List<SGData> dataList, final List<SGData> mergedDataList, 
            final SGIProgressControl progress) {

        // get figure elements
        SGIFigureElement[] elArray = figure.getIFigureElementArray();
        SGIFigureElementGraph gElement = figure.getGraphElement();
        Map<SGIDataSource, List<SGData>> dataSrcMap = new HashMap<SGIDataSource, List<SGData>>();
        for (SGData data : dataList) {
            if (data instanceof SGISXYTypeMultipleData) {
                SGIDataSource dataSource = data.getDataSource();
                List<SGData> store = dataSrcMap.get(dataSource);
                if (null == store) {
                    store = new ArrayList<SGData>();
                    dataSrcMap.put(dataSource, store);
                }
                store.add(data);
            }
        }
        
        // remove single data
        Map<SGIDataSource, List<SGData>> dataSrcMapTmp = new HashMap<SGIDataSource, List<SGData>>(dataSrcMap);
        Iterator<Entry<SGIDataSource, List<SGData>>> itrTmp = dataSrcMapTmp.entrySet().iterator();
        while (itrTmp.hasNext()) {
        	Entry<SGIDataSource, List<SGData>> entry = itrTmp.next();
        	SGIDataSource key = entry.getKey();
            List<SGData> store = entry.getValue();
            if (store.size() <= 1) {
            	SGData data = store.get(0);
            	gElement.setDataFocused(data, false);
            	dataSrcMap.remove(key);
            }
        }
        
        List<SGData> dList = new ArrayList<SGData>();
        if (dataSrcMap.size() == 0) {
        	return dList;
        }

        int count = 0;
        final float diff = 1.0f / (dataSrcMap.size() + 1);
        Iterator<Entry<SGIDataSource, List<SGData>>> itr = dataSrcMap.entrySet().iterator();
        while (itr.hasNext()) {
            if (null != progress) {
                progress.setProgressValue((count + 1) * diff);
            }
            
        	Entry<SGIDataSource, List<SGData>> entry = itr.next();
            List<SGData> store = entry.getValue();
            mergedDataList.addAll(store);
            
            SGISXYTypeMultipleData dataMult = null;
            SGData dataLast = store.get(store.size() - 1);
            SGProperties pLast = gElement.getDataProperties(dataLast);
            Map<Class<? extends SGIFigureElement>, SGProperties> propertiesMap =
                new HashMap<Class<? extends SGIFigureElement>, SGProperties>();

            if (SGDataUtility.isSDArrayData(dataLast)) {
                dataMult = SGSXYSDArrayMultipleData.merge(store);
            } else if (SGDataUtility.isNetCDFData(dataLast)) {
                if (dataLast instanceof SGSXYNetCDFMultipleData) {
                    dataMult = SGSXYNetCDFMultipleData.merge(store);
                }
            } else if (SGDataUtility.isMDArrayData(dataLast)) {
            	dataMult = SGSXYMDArrayMultipleData.merge(store);
            } else {
            	return null;
            }
            if (dataMult == null) {
            	return null;
            }
            String name = getNameOfMergedData((SGData) dataMult);

            if (null != dataMult) {
                // get data properties
                SGProperties pMult = ((SGData) dataMult).getProperties();

                for (int jj = 0; jj < elArray.length; jj++) {
                    // synchronize the properties for duplicated data object
                    SGProperties sp = elArray[jj].synchronizeDataProperties(pLast, pMult);
                    if (sp == null) {
                        continue;
                    }

                    if (sp instanceof MultipleSXYElementGroupSetPropertiesInFigureElement) {
                    	MultipleSXYElementGroupSetPropertiesInFigureElement mp = (MultipleSXYElementGroupSetPropertiesInFigureElement) sp;
                    	List<SGLineStyle> lineStyleList = new ArrayList<SGLineStyle>();
                    	for (int ii = 0; ii < store.size(); ii++) {
                    		SGData data = store.get(ii);
                    		MultipleSXYElementGroupSetPropertiesInFigureElement p = (MultipleSXYElementGroupSetPropertiesInFigureElement) gElement.getDataProperties(data);
                    		List<SGLineStyle> sList = p.getLineStyleList();
                    		lineStyleList.add(sList.get(0));
                    	}
                    	mp.setLineStyleList(lineStyleList);
                    }

                    propertiesMap.put(elArray[jj].getClass(), sp);
                }
                if (figure.addData((SGData) dataMult, name, propertiesMap) == false) {
                    return null;
                }

                dList.add((SGData)dataMult);
            }

            count++;
        }

        return dList;
    }

    static List<SGData> mergeData(final SGFigure figure, final int[] dataIdArray) {
    	SGIFigureElementGraph gElement = figure.getGraphElement();
        List<SGData> dataList = new ArrayList<SGData>();
        for (int ii = 0; ii < dataIdArray.length; ii++) {
            SGData data = figure.getData(dataIdArray[ii]);
            if (data == null) {
                return null;
            }
            if (!gElement.isDataVisible(data)) {
            	return null;
            }
            dataList.add(data);
        }

        // set focus to data
        gElement.clearFocusedObjects();
        for (int ii = 0; ii < dataList.size(); ii++) {
            gElement.setDataFocused(dataList.get(ii), true);
        }

        List<SGData> hiddenDataList = new ArrayList<SGData>();
        List<SGData> dataListAdded = mergeDataSub(figure, dataList, hiddenDataList, null);
        if (dataListAdded == null) {
        	return null;
        }
        if (dataListAdded.size() == 0) {
        	return dataListAdded;
        }

        figure.hideData(dataIdArray);
        if (figure.isChangedRoot()) {
            figure.notifyToRoot();
        }
        figure.repaint();

        return dataListAdded;
    }

    /**
     * Returns the name of the merged data.
     *
     * @param data merged data
     * @return the name of the merged data.
     */
    static String getNameOfMergedData(final SGData data) {
        String path = null;
        if (data instanceof SGSXYSDArrayMultipleData) {
            path = ((SGSXYSDArrayMultipleData) data).getDataFile().getPath();
        } else if (data instanceof SGSXYNetCDFMultipleData) {
            path = ((SGSXYNetCDFMultipleData) data).getNetcdfFile().getNetcdfFile().getLocation();
        } else if (data instanceof SGSXYMDArrayMultipleData) {
        	path = ((SGSXYMDArrayMultipleData) data).getMDArrayFile().getPath();
        }
        if (null!=path) {
            return SGUtility.createDataNameBase(path);
        } else {
            return "MergedData";
        }
    }

}

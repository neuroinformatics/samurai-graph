package jp.riken.brain.ni.samuraigraph.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import jp.riken.brain.ni.samuraigraph.application.SGMainFunctions.FigureData;
import jp.riken.brain.ni.samuraigraph.application.SGMainFunctions.WrappedData;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGICopiable;
import jp.riken.brain.ni.samuraigraph.base.SGIDisposable;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyFileConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIRootObjectConstants;
import jp.riken.brain.ni.samuraigraph.base.SGISelectable;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * SGMainFunctions :: ClipBoardManager class
 * 
 * @author okumura
 */
class SGClipBoardManager implements SGIDisposable, SGIConstants, 
		SGIApplicationTextConstants, SGIPropertyFileConstants {
	
    /**
     * The list of copied objects.
     */
    private List<SGICopiable> mCopiedObjectsBuffer = new ArrayList<SGICopiable>();

    /**
     * The list of copied data objects.
     */
    private List<SGData> mCopiedDataObjectBuffer = new ArrayList<SGData>();

    /**
     * The list of copied data names.
     */
    private List<String> mCopiedDataNameBuffer = new ArrayList<String>();

    /**
     * The list of copied data properties.
     */
    private List<Map<Class, SGProperties>> mCopiedDataPropertiesBuffer = new ArrayList<Map<Class, SGProperties>>();

    /**
     * Information of the focused figures.
     */
    private WindowInfo mWindowInfo = null;
    
    /**
     * The main function.
     */
    private SGMainFunctions mMain = null;

    SGClipBoardManager(final SGMainFunctions main) {
    	super();
    	this.mMain = main;
    }

    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;
    	this.clearAllLists();
    }
    
    // The flag whether this object is already disposed of.
    private boolean mDisposed = false;

    /**
     * Returns whether this object is already disposed of.
     * 
     * @return true if this object is already disposed of
     */
    public boolean isDisposed() {
    	return this.mDisposed;
    }
    
    // Clears all lists.
    private void clearAllLists() {
        for (int ii = 0; ii < this.mCopiedDataObjectBuffer.size(); ii++) {
            SGData data = (SGData) this.mCopiedDataObjectBuffer.get(ii);
            data.dispose();
        }
        this.mCopiedObjectsBuffer.clear();
        this.mCopiedDataNameBuffer.clear();
        this.mCopiedDataPropertiesBuffer.clear();
        this.mCopiedDataObjectBuffer.clear();
        if (this.mWindowInfo != null) {
            for (Entry<Integer, List<SGData>> e : this.mWindowInfo.mDataListMap.entrySet()) {
            	List<SGData> dList = e.getValue();
            	for (SGData d : dList) {
            		d.dispose();
            	}
            	dList.clear();
            }
        	this.mWindowInfo.mDataListMap.clear();
        	this.mWindowInfo.mDocument = null;
        	this.mWindowInfo = null;
        }
    }

    /**
     * Cut or copy the objects.
     * 
     * @param wnd
     * @param isCopy
     *            a flag to set cut/copy
     * @return true:succeeded, false:failed
     */
    boolean cutAndCopy(final SGDrawingWindow wnd,
            final boolean isCopy) {
        // clear all lists
    	this.clearAllLists();

        // get copied objects list and add them to the buffer of the
        // attribute
        List<SGICopiable> cList = wnd.getCopiedObjectsList();
        this.mCopiedObjectsBuffer.addAll(cList);

        List<SGData> dList = wnd.getCopiedObjectsDataList();
        this.mCopiedDataObjectBuffer.addAll(dList);

        List<String> nList = wnd.getCopiedDataNameList();
        this.mCopiedDataNameBuffer.addAll(nList);

        List<Map<Class, SGProperties>> pList = wnd.getCopiedDataPropertiesMapList();
        this.mCopiedDataPropertiesBuffer.addAll(pList);

        WindowInfo info = new WindowInfo();

        if (isCopy) {
            if (this.copyFigures(wnd, info, new SGExportParameter(OPERATION.COPY_OBJECT)) == false) {
                return false;
            }
        } else {
            if (this.cutFigures(wnd, info) == false) {
                return false;
            }
        }

        // set to the attribute
        this.mWindowInfo = info;

        // set the paste menu enabled
        this.mMain.mWindowManager.setPasteMenuEnabled();

        // clear copied objects
        wnd.clearCopiedObjectsList();
        
        return true;
    }

    // Cuts the figure.
    private boolean cutFigures(SGDrawingWindow wnd, WindowInfo info) {
        if (this.copyFigures(wnd, info, new SGExportParameter(OPERATION.CUT_OBJECT)) == false) {
            return false;
        }

        if (info.mDataListMap.size() != 0) {
            wnd.hideSelectedObjects();
        }

        return true;
    }

    // Copies the figure.
    private boolean copyFigures(SGDrawingWindow wnd, WindowInfo info, final SGExportParameter operation) {
        DOMImplementation domImpl = SGApplicationUtility
                .getDOMImplementation();
        if (domImpl == null) {
            return false;
        }

        // create a Document object
        Document document = domImpl.createDocument("",
                TAG_NAME_FOCUSED_FIGURES, null);

        // create a DOM tree
        if (wnd.createDOMTree(document,
                SGIRootObjectConstants.FOCUSED_FIGURES_FOR_DUPLICATION,
                operation) == false) {
            return false;
        }

        // create a map of figure ID and data list
        Map<Integer, List<SGData>> dListMap = new TreeMap<Integer, List<SGData>>();
        List<SGFigure> fList = wnd.getCopiedFiguresList();
        for (int ii = 0; ii < fList.size(); ii++) {
            SGFigure figure = (SGFigure) fList.get(ii);
            List<SGData> dList = new ArrayList<SGData>(figure.getVisibleDataList());
            List<SGData> dListCopy = new ArrayList<SGData>();
            for (int jj = 0; jj < dList.size(); jj++) {
                SGData data = dList.get(jj);
                dListCopy.add((SGData) data.copy());
            }
            Integer key = Integer.valueOf(figure.getID());
            dListMap.put(key, dListCopy);
        }

        // set to the attribute
        info.mDocument = document;
        info.mDataListMap = dListMap;

        return true;
    }

    /**
     * Paste objects to the window.
     * 
     * @param wnd
     *            the target object
     * @return true if succeeded
     */
    boolean pasteToWindow(SGDrawingWindow wnd) {

    	List<SGISelectable> fList = wnd.getFocusedObjectsList();
        if (fList.size() != 0) {

            // paste the copied objects to figures
            wnd.pasteToFigures(this.mCopiedObjectsBuffer,
                    this.mCopiedDataObjectBuffer,
                    this.mCopiedDataNameBuffer,
                    this.mCopiedDataPropertiesBuffer);
        } else {

            // paste the copied figure objects to the window
            WindowInfo info = this.mWindowInfo;
            if (info == null) {
                return false;
            }
            Map<Integer, List<SGData>> dListMap = info.mDataListMap;
            
            List<WrappedData> wdList = new ArrayList<WrappedData>();
            Iterator<Integer> itr = dListMap.keySet().iterator();
            int cnt = 0;
            while (itr.hasNext()) {
                Integer figureId = itr.next();
                List<SGData> dList = dListMap.get(figureId);
                for (int jj = 0; jj < dList.size(); jj++) {
                    SGData data = dList.get(jj);
                    FigureData fd = new FigureData(data, cnt);
                    WrappedData wd = new WrappedData(fd);
                    wdList.add(wd);
                }
                cnt++;
            }
            WrappedData[] wdArray = new WrappedData[wdList.size()];
            wdList.toArray(wdArray);

            // get version number
            String versionNumber = this.mMain.getVersionString();

            // get root element
            Element root = info.mDocument.getDocumentElement();
            root.setAttribute(KEY_VERSION_NUMBER, versionNumber);

            // get the node of window
            NodeList wList = root
                    .getElementsByTagName(SGIRootObjectConstants.TAG_NAME_WINDOW);
            if (wList.getLength() == 0) {
                return false;
            }
            Element elWnd = (Element) wList.item(0);

            final int before = wnd.getFigureList().size();

            // create figures from a DOM tree
            final int ret = this.mMain.createFiguresFromPropertyFile(elWnd, wnd,
                    wdArray, true, versionNumber, LOAD_PROPERTIES_IN_PASTING);
            if (ret != SGIConstants.SUCCESSFUL_COMPLETION) {
                return false;
            }

            final int after = wnd.getFigureList().size();

            wnd.setChanged(before != after);
        }

        return true;
    }

    boolean getPasteMenuStatus() {
        final boolean b1 = (this.mCopiedObjectsBuffer.size() != 0);
        final boolean b2 = (this.mCopiedDataObjectBuffer.size() != 0);

        boolean b3 = false;
        if (this.mWindowInfo != null) {
            Document doc = this.mWindowInfo.mDocument;
            NodeList nodeList = doc
                    .getElementsByTagName(SGFigure.TAG_NAME_FIGURE);
            final int len = nodeList.getLength();
            b3 = (len != 0);
        }

        final boolean b = (b1 || b2 || b3);

        return b;
    }

    /**
     * A private class to store information of a figure.
     */
    private class WindowInfo {
        /**
         * A DOM tree object of figures.
         */
        private Document mDocument;

        /**
         * The map of the list of data objects.
         */
        private Map<Integer, List<SGData>> mDataListMap = new TreeMap<Integer, List<SGData>>();
    }

}

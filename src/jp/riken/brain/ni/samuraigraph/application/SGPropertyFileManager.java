package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import jp.riken.brain.ni.samuraigraph.application.SGMainFunctions.WrappedData;
import jp.riken.brain.ni.samuraigraph.base.SGDialog;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGExtensionFileFilter;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyFileConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIRootObjectConstants;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import foxtrot.Task;
import foxtrot.Worker;

/**
 * @author okumura
 */
class SGPropertyFileManager implements ActionListener, SGIConstants, SGIApplicationConstants,
		SGIApplicationTextConstants, SGIPropertyFileConstants {
	
    /**
     * 
     */
    private Document mPropertyFileDocument = null;

    /**
     * Property file creator.
     */
    private SGPropertyFileCreator mPropertyFileCreator = null;

    /**
     * Property file chooser
     */
    private SGPropertyFileChooserWizardDialog mPropertyFileChooserWizardDilaog = null;

    /**
     * Multiple data file chooser
     */
    private SGPropertyDataFileChooserWizardDialog mMultiDataFileChooserWizardDialog = null;

    /**
     * Multiple data file chooser for drag and drop
     */
    private SGPropertyDataFileChooserWizardDialog mMultiDataFileChooserWizardDialogDD = null;

    /**
     * The main functions.
     */
    private SGMainFunctions mMain = null;
    
    SGPropertyFileManager(SGMainFunctions main) {
    	super();
    	this.mMain = main;
        this.mPropertyFileCreator = new SGPropertyFileCreator();
        this.mPropertyFileChooserWizardDilaog = null;
        this.mMultiDataFileChooserWizardDialog = null;
    }

    /**
     * save properties
     * 
     * @param wnd -
     *            target window
     * @param path -
     *            save file path
     * @return status
     */
    public int saveProperties(final SGDrawingWindow wnd, final String path, 
    		final SGExportParameter params) {
        
        // get version string
        final String versionString = this.mMain.mAppProp.getVersionString();
        
        // create a property file
        return this.mPropertyFileCreator.create(wnd, new File(path), params, versionString);
    }

    /**
     * Shows a dialog to save properties.
     * 
     * @param wnd
     *            a window
     * @return status
     */
    public int savePropertiesByDialog(final SGDrawingWindow wnd) {
    	String fileName = SGApplicationUtility.getOutputFileName(wnd);
    	if (fileName == null) {
    		fileName = DEFAULT_PROPERTY_FILE_NAME;
    	}
    	String name = SGApplicationUtility.appendExtension(
                fileName, PROPERTY_FILE_EXTENSION);
    	String dir = this.mMain.getCurrentFileDirectory();
    	
        // set the selected file name
        this.mPropertyFileCreator.setCurrentFile(dir, name);

        // get version string
        final String versionString = this.mMain.mAppProp.getVersionString();
        
        // create a property file
        int ret;
		try {
			ret = this.mPropertyFileCreator.create(wnd, 
			        new SGExportParameter(OPERATION.SAVE_TO_PROPERTY_FILE), versionString);
		} catch (IOException e) {
            SGUtility.showErrorMessageDialog(wnd, e.getMessage(), TITLE_ERROR);
            ret = ERROR_OPTION;
		}
        if (ret != OK_OPTION) {
            return ret;
        }

        File f = this.mPropertyFileCreator.getCurrentFile();
        if (f != null) {
        	this.mMain.updateCurrentFile(f, FILE_TYPE.PROPERTY);
        }

        return OK_OPTION;
    }

    boolean loadPropertyFromDialog(final SGDrawingWindow wnd) {

        // create wizard dialogs
        this.createAllWizardDialogsToLoadPropertyFromToolBar(wnd);

        // sets the current file path
    	this.mPropertyFileChooserWizardDilaog.setCurrentFile(this.mMain.getCurrentFileDirectory(), null);

        // set location
        this.mPropertyFileChooserWizardDilaog.setCenter(wnd);

        // show the first wizard dialog
        this.mPropertyFileChooserWizardDilaog.setVisible(true);

        /*
        // update the selected file name
        File pf = this.mPropertyFileChooserWizardDilaog.getSelectedFile();
        String dfpath = this.mMultiDataFileChooserWizardDialog.getSelectedFilepath();
        final long pUsed = this.mPropertyFileChooserWizardDilaog.lastUsed();
        final long dUsed = this.mMultiDataFileChooserWizardDialog.lastUsed();
        if (pf != null && dfpath != null && !"".equals(dfpath.trim())) {
            if (pUsed < dUsed) {
                File df = new File(dfpath);
            	this.mMain.updateCurrentFile(df, FILE_TYPE.TXT_DATA);
            } else {
            	this.mMain.updateCurrentFile(pf, FILE_TYPE.PROPERTY);
            }
        } else if (pf != null) {
        	this.mMain.updateCurrentFile(pf, FILE_TYPE.PROPERTY);
        } else if (dfpath != null && !"".equals(dfpath.trim())) {
            File df = new File(dfpath);
        	this.mMain.updateCurrentFile(df, FILE_TYPE.TXT_DATA);
        }
        */

        return true;
    }

    /**
     * Version number of the current property file.
     */
    private String mVersionNumber = null;
    
    /**
     * 
     * @param e
     * @return
     */
    private boolean loadProperty(final ActionEvent e) {
        Object source = e.getSource();
        String command = e.getActionCommand();
        SGWizardDialog dg = (SGWizardDialog) source;

        if (source instanceof SGPropertyFileChooserWizardDialog) {
            if (this.fromPropertyFileChooserDialog(e, dg) == false) {
                return false;
            }
        } else if (source instanceof SGPropertyDataFileChooserWizardDialog) {
            if (this.fromMultiDataFileChooser(e) == false) {
                return false;
            }
        }

        // cancel or previous
        if (command.equals(SGDialog.CANCEL_BUTTON_TEXT)) {
            dg.setVisible(false);
            this.mVersionNumber = null;
        } else if (command.equals(SGDialog.PREVIOUS_BUTTON_TEXT)) {
            dg.showPrevious();
        }

        return true;
    }

    /**
     * 
     * @return
     */
    boolean setPropertyFile(final SGDrawingWindow wnd,
            final Document doc, final WrappedData[] wDataArray, 
            final boolean readDataProperty, final String versionNumber, final int mode) {
        
        // Element of the window
        final Element elWnd = getWindowElement(doc);

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
            errcode = this.mMain.createFiguresFromPropertyFile(elWnd, wnd, wDataArray, 
                    readDataProperty, versionNumber, mode);

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
            msg = MSG_SUCCESSFUL_COMPLETION;
            break;
        case SGIConstants.DATA_NUMBER_SHORTAGE:
            msg = MSG_DATA_NUMBER_SHORTAGE;
            break;
        case SGIConstants.DATA_NUMBER_EXCESS:
            msg = MSG_DATA_NUMBER_EXCESS;
            break;
        case SGIConstants.FILE_OPEN_FAILURE:
            msg = MSG_FILE_OPEN_FAILURE;
            break;
        case SGIConstants.PROPERTY_FILE_INCORRECT:
            msg = MSG_PROPERTY_FILE_INVALID;
            break;
        case SGIConstants.DATA_FILE_INVALID:
            msg = MSG_DATA_FILE_OPEN_FAILURE;
            break;
        default:
            msg = MSG_UNKNOWN_ERROR_OCCURED;
        }

        // show the message dialog
        if (msg != MSG_SUCCESSFUL_COMPLETION) {
            SGUtility.showMessageDialog(null, msg, "Property file",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * 
     * @return
     */
    private boolean setPropertyFileFromEventDispatchThread(
            final SGDrawingWindow wnd, final Document doc, final WrappedData[] wDataArray) {

        // get root element
        Element root = doc.getDocumentElement();

        // get version number
        final String versionNumber = root.getAttribute(KEY_VERSION_NUMBER);
        
        // disable window
        wnd.setWaitCursor(true);

        Boolean result = Boolean.FALSE;

        if (!SGMainFunctions.USE_FOXTROT) {
            if (setPropertyFile(wnd, doc, wDataArray, false, versionNumber, 
            		LOAD_PROPERTIES_FROM_PROPERTY_FILE)) {
                result = Boolean.TRUE;
            }
        } else {
            try {
                result = (Boolean) Worker.post(new Task() {
                    public Object run() throws Exception {
                        if (!setPropertyFile(wnd, doc, wDataArray, false, versionNumber, 
                        		LOAD_PROPERTIES_FROM_PROPERTY_FILE)) {
                            return Boolean.FALSE;
                        }
                        return Boolean.TRUE;
                    }
                });

            } catch (Exception ex) {
                result = Boolean.FALSE;
                ex.printStackTrace();
            }
        }

        // enable window again
        wnd.setWaitCursor(false);

        return result.booleanValue();
    }

    //
    Element getWindowElement(Document doc) {
        Element root = doc.getDocumentElement();

        // get the node of window
        NodeList wList = root
                .getElementsByTagName(SGIRootObjectConstants.TAG_NAME_WINDOW);
        if (wList.getLength() == 0) {
            return null;
        }
        Element elWnd = (Element) wList.item(0);

        return elWnd;
    }

    boolean getInfoFromPropertyFile(final File file,
            final Component parent, final List<SGPropertyFileData> pDataList, 
            final boolean silent) {
        // get file path
        String path = SGUtility.getCanonicalPath(file.getPath());
        if (path == null) {
        	if (!silent) {
                SGUtility.showMessageDialog(parent, MSG_FILE_OPEN_FAILURE,
                        TITLE_FILE_OPEN_FAILURE, JOptionPane.ERROR_MESSAGE);
        	}
            return false;
        }

        // check validity of the file
        URL url = null;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException ex) {
            return false;
        }

        // create a Document object
        Document doc = SGUtilityText.getDocument(url);
        if (doc == null) {
        	if (!silent) {
                SGUtility.showMessageDialog(parent, MSG_PROPERTY_FILE_INVALID,
                        TITLE_FILE_OPEN_FAILURE, JOptionPane.ERROR_MESSAGE);
        	}
            return false;
        }
        
        return this.getInfoFromPropertyFile(doc, parent, pDataList, silent);
    }
    
    boolean getInfoFromPropertyFile(final Document doc,
            final Component parent, final List<SGPropertyFileData> pDataList, 
            final boolean silent) {

        this.mPropertyFileDocument = doc;

        // get root element - property
        Element root = doc.getDocumentElement();
        
        // get version number
        String versionNumber = root.getAttribute(KEY_VERSION_NUMBER);
        this.mVersionNumber = versionNumber;

        // get the node of window
        NodeList wList = root
                .getElementsByTagName(SGIRootObjectConstants.TAG_NAME_WINDOW);
        if (wList.getLength() == 0) {
            return false;
        }

        // figure ID
        NodeList figureNodeList = doc
                .getElementsByTagName(SGFigure.TAG_NAME_FIGURE);
        if (figureNodeList.getLength() == 0) {
            // if figure does not exist, return false
        	if (!silent) {
                SGUtility.showMessageDialog(parent, MSG_PROPERTY_FILE_INVALID,
                        TITLE_FILE_OPEN_FAILURE, JOptionPane.ERROR_MESSAGE);
        	}
            return false;
        }
        final int fnLength = figureNodeList.getLength();
        for (int ii = 0; ii < fnLength; ii++) {

            Node node = figureNodeList.item(ii);
            if ((node instanceof Element) == false) {
                continue;
            }
            Element figure = (Element) node;

            // get figure size
            SGTuple2f figureSize = this.getFigureSize(figure);
            if (figureSize == null) {
            	return false;
            }

            NodeList dataList = figure
                    .getElementsByTagName(SGIFigureElementGraph.TAG_NAME_DATA);
            final int id = ii + 1;
            final int dataNum = dataList.getLength();
            if (dataNum == 0) {
                // if the figure has no data

                // create a data object and set values
                SGPropertyFileData pData = new SGPropertyFileData(id, null,
                        null, null);
                pData.setFigureSize(figureSize);

                // add to a given list
                pDataList.add(pData);

            } else {
                for (int jj = 0; jj < dataNum; jj++) {
                    Node n = dataList.item(jj);
                    if (n instanceof Element) {
                        Element el = (Element) n;
                        String name = el
                                .getAttribute(SGIFigureElement.KEY_DATA_NAME);
                        String type = el
                                .getAttribute(SGIFigureElement.KEY_DATA_TYPE);

                        // create information map
                        Map<String, Object> infoMap = SGMainFunctions.createInfoMap(type, el);
                        
                        // create a data object and set values
                        SGPropertyFileData pData = new SGPropertyFileData(
                                id, type, name, infoMap);
                        pData.setFigureSize(figureSize);

                        // add to a given list
                        pDataList.add(pData);
                    }
                }
            }
        }

        return true;
    }
    
    SGTuple2f getFigureSize(Element el) {
        // get the size of figure
        String strFigureWidth = el.getAttribute(SGIFigureConstants.KEY_FIGURE_WIDTH);
        if (strFigureWidth == null || strFigureWidth.length() == 0) {
        	return null;
        }
        StringBuffer sbFigureWidth = new StringBuffer();
        Number figureWidth = SGUtilityText.getNumber(strFigureWidth, sbFigureWidth);
        if (figureWidth == null) {
        	return null;
        }
        final Float wPt = SGFigure.calcFigureScale(figureWidth.floatValue(), sbFigureWidth.toString(), 
        		SGIFigureConstants.FIGURE_SIZE_UNIT, 
                SGIFigureConstants.FIGURE_WIDTH_MIN, SGIFigureConstants.FIGURE_WIDTH_MAX);
        if (wPt == null) {
            return null;
        }
        String strFigureHeight = el.getAttribute(SGIFigureConstants.KEY_FIGURE_HEIGHT);
        if (strFigureHeight == null || strFigureHeight.length() == 0) {
        	return null;
        }
        StringBuffer sbFigureHeight = new StringBuffer();
        Number figureHeight = SGUtilityText.getNumber(strFigureHeight, sbFigureHeight);
        if (figureHeight == null) {
        	return null;
        }
        final Float hPt = SGFigure.calcFigureScale(figureHeight.floatValue(), sbFigureHeight.toString(), 
        		SGIFigureConstants.FIGURE_SIZE_UNIT, 
                SGIFigureConstants.FIGURE_HEIGHT_MIN, SGIFigureConstants.FIGURE_HEIGHT_MAX);
        if (hPt == null) {
            return null;
        }
        SGTuple2f figureSize = new SGTuple2f(wPt, hPt);
        return figureSize;
    }

    /**
     * 
     * @param e
     * @param dg
     * @return
     */
    private boolean fromPropertyFileChooserDialog(ActionEvent e,
            SGWizardDialog dg) {
        String command = e.getActionCommand();
        // Window owner = dg.getOwner();
        SGWizardDialog next = dg.getNext();
        if ((dg instanceof SGPropertyFileChooserWizardDialog) == false) {
            return false;
        }
        if ((next instanceof SGPropertyDataFileChooserWizardDialog) == false) {
            return false;
        }
        SGPropertyFileChooserWizardDialog pdg = (SGPropertyFileChooserWizardDialog) dg;
        SGPropertyDataFileChooserWizardDialog mdg = (SGPropertyDataFileChooserWizardDialog) next;
        if (command.equals(SGDialog.NEXT_BUTTON_TEXT)) {
            if (this.fromPropertyFileChooserDialogNext(pdg, mdg) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * 
     * @param e
     * @return
     */
    private boolean fromMultiDataFileChooser(final ActionEvent e) {
        Object source = e.getSource();
        SGWizardDialog dg = (SGWizardDialog) source;
        String command = e.getActionCommand();
        SGPropertyDataFileChooserWizardDialog mdg = (SGPropertyDataFileChooserWizardDialog) dg;
        SGDrawingWindow wnd = dg.getOwnerWindow();

        //
        if (command.equals(SGDialog.OK_BUTTON_TEXT)) {

            // clear old objects
            wnd.deleteImage();
            wnd.clearUndoBuffer();
            wnd.removeAllFigures();

            // get data object from the dialog
            SGPropertyFileData[] pfData = mdg.getPropertyFileDataArray();
            WrappedData[] wDataArray = new WrappedData[pfData.length];
            for (int ii = 0; ii < wDataArray.length; ii++) {
                wDataArray[ii] = new WrappedData(pfData[ii]);
            }

            // set properties
            if (!this.setPropertyFileFromEventDispatchThread(wnd,
                    this.mPropertyFileDocument, wDataArray)) {
                return false;
            }

            // update the client rectangle
            wnd.updateClientRect();

            // hide the wizard dialog
            dg.setVisible(false);

        } else if (command.equals(SGDialog.CANCEL_BUTTON_TEXT)) {
            dg.setVisible(false);
        }

        return true;
    }

    /**
     * 
     * @param pdg
     * @param mdg
     * @return
     */
    private boolean fromPropertyFileChooserDialogNext(
            final SGPropertyFileChooserWizardDialog pdg,
            final SGPropertyDataFileChooserWizardDialog mdg) {

        // get file type from the selected file
        File file = new File(pdg.getFileName());
        if (file.exists() == false) {
            SGUtility.showFileNotFoundMessageDialog(pdg);
            return false;
        }

        // set the selected file to the file chooser
        pdg.setSelectedFile(file);

        // get information from the property file
        List dataList = new ArrayList();
        if (this.getInfoFromPropertyFile(file, pdg, dataList, false) == false) {
            return false;
        }

        // set data to the dialog
        mdg.setData(dataList, this.mVersionNumber);

        // show the multi data chooser dialog
        pdg.showNext();

        return true;
    }

    /**
     * 
     */
    private void createAllWizardDialogsToLoadPropertyFromToolBar(
            final SGDrawingWindow owner) {

        if (this.mPropertyFileChooserWizardDilaog != null) {
            SGDrawingWindow curOwner = this.mPropertyFileChooserWizardDilaog
                .getOwnerWindow();
            if (curOwner.equals(owner)) {
                return;
            }
        }
        
        //
        // create dialog objects
        //

        // dialog to load property file
        this.mPropertyFileChooserWizardDilaog = new SGPropertyFileChooserWizardDialog(
                owner, true);
        this.mPropertyFileChooserWizardDilaog.getPreviousButton()
                .setVisible(false);
        this.mPropertyFileChooserWizardDilaog.getOKButton().setVisible(
                false);
        this.mPropertyFileChooserWizardDilaog.pack();

        // dialog to select data files
        this.mMultiDataFileChooserWizardDialog = new SGPropertyDataFileChooserWizardDialog(
                owner, true);
        this.mMultiDataFileChooserWizardDialog.getNextButton().setVisible(
                false);
        this.mMultiDataFileChooserWizardDialog.pack();

        // create a file filter object
        SGExtensionFileFilter ff = new SGExtensionFileFilter();
        ff.setExplanation(PROPERTY_FILE_DESCRIPTION);
        ff.addExtension(PROPERTY_FILE_EXTENSION);
        this.mPropertyFileChooserWizardDilaog.setFileFilter(ff);

        // get current directory
        String dir = this.mMain.getCurrentFileDirectory();

        // set the selected file name
        this.mPropertyFileChooserWizardDilaog.setCurrentFile(dir, 
        		this.mMain.getCurrentFileName(FILE_TYPE.PROPERTY));

        // set the selected file name
        this.mMultiDataFileChooserWizardDialog.setSelectedFile(dir,
        		this.mMain.getCurrentFileName(FILE_TYPE.TXT_DATA));

        //
        // next and previous dialog
        //

        this.mPropertyFileChooserWizardDilaog
                .setNext(this.mMultiDataFileChooserWizardDialog);
        this.mMultiDataFileChooserWizardDialog
                .setPrevious(this.mPropertyFileChooserWizardDilaog);

        //
        // add action listener
        //

        this.mPropertyFileChooserWizardDilaog.addActionListener(this);
        this.mMultiDataFileChooserWizardDialog.addActionListener(this);

    }

    /**
     * Shows a wizard dialog to select multiple data files.
     * 
     * @param propertyFile
     *            the property file
     * @param owner
     *            the owner of dialogs
     * @return true if succeeded
     */
    boolean showMultiDataFileChooserDialog(final File propertyFile,
            final Frame owner) {

        if (propertyFile.exists() == false) {
            SGUtility.showErrorMessageDialog(owner, MSG_FILE_OPEN_FAILURE,
                    TITLE_FILE_OPEN_FAILURE);
            return false;
        }

        boolean createFlag = false;
        if (this.mMultiDataFileChooserWizardDialogDD == null) {
            createFlag = true;
        } else {
            if (!this.mMultiDataFileChooserWizardDialogDD.getOwner().equals(owner)) {
                createFlag = true;
            }
        }
        
        if (createFlag) {
            // create data chooser dialog
            this.mMultiDataFileChooserWizardDialogDD = new SGPropertyDataFileChooserWizardDialog(
                    owner, true);

            SGPropertyDataFileChooserWizardDialog dg = this.mMultiDataFileChooserWizardDialogDD;
            dg.getPreviousButton().setVisible(false);
            dg.getNextButton().setVisible(false);
            dg.pack();
            dg.addActionListener(this);

            // set directory
            this.mMultiDataFileChooserWizardDialogDD.setSelectedFile(
            		this.mMain.getCurrentFileDirectory(),
            		this.mMain.getCurrentFileName(FILE_TYPE.TXT_DATA));
        }

        // get information from the property file
        List<SGPropertyFileData> dataList = new ArrayList<SGPropertyFileData>();
        if (this.getInfoFromPropertyFile(propertyFile, owner, dataList, false) == false) {
            return false;
        }

        // set data to the dialog
        this.mMultiDataFileChooserWizardDialogDD.setData(dataList, this.mVersionNumber);

        // show dialog
        if (owner != null) {
            this.mMultiDataFileChooserWizardDialogDD.setCenter(owner);
        } else {
            this.mMultiDataFileChooserWizardDialogDD.setLocationRelativeTo(null);
        }
        this.mMultiDataFileChooserWizardDialogDD.setVisible(true);

        return true;
    }

    /**
     * 
     */
    public void actionPerformed(final ActionEvent e) {

        Object source = e.getSource();
        // String command = e.getActionCommand();

        // Load Property
        if (source.equals(this.mPropertyFileChooserWizardDilaog)
                || source.equals(this.mMultiDataFileChooserWizardDialog)
                || source.equals(this.mMultiDataFileChooserWizardDialogDD)) {
            this.loadProperty(e);
        }

    }

}

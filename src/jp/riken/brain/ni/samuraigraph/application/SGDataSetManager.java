package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import jp.riken.brain.ni.samuraigraph.application.SGArchiveFileCreator.ArchiveFile;
import jp.riken.brain.ni.samuraigraph.application.SGMainFunctions.WrappedData;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyFileConstants;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGIDataFileConstants;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFData;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGIElementGroupSetForData;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import foxtrot.Task;
import foxtrot.Worker;

/**
 * SGMainFunctions :: DataSetManager class
 * 
 */
class SGDataSetManager implements SGIConstants, SGIApplicationConstants, 
		SGIPropertyFileConstants, SGIApplicationTextConstants, SGIArchiveFileConstants {
	
    private static final String DATASET_TEMPDIR_NAME = "SamuraiGraphArcvhive";

    private static final String DATASET_PROPERTY_FILENAME = SGPropertyFileCreator.DEFAULT_PROPERTY_FILE_NAME_WITH_EXTENSION;

    private static final String MSG_SAVE_DATASET_FAILED = "Failed save dataset";

    private static final String MSG_LOAD_DATASET_FAILED = "Failed load dataset";

    public static final String ERRMSG_FAILED_TO_CREATE_DATASET = "Cannot create a data set with data in use.";

    /**
     * Data set file extractor.
     */
    private SGArchiveFileExtractor mArchiveFileExtractor = null;

    /**
     * Data set file extractor.
     */
    private SGArchiveFileCreator mArchiveFileCreator = null;

    /**
     * The main functions.
     */
    private SGMainFunctions mMain = null;
    
    /**
     * The list of folders for data set files.
     */
    private List<File> mDataSetFolderList = new ArrayList<File>();
    
    /**
     * The list of files for data set files.
     */
    private List<File> mDataSetFileList = new ArrayList<File>();
    
    SGDataSetManager(SGMainFunctions main, final SGSplashWindow sw, 
    		final float minprog, final float maxprog) {
    	
    	super();
    	this.mMain = main;
        final float step = (maxprog - minprog) / 3;
        sw.setProgressValue(minprog + step);
        this.mArchiveFileExtractor = new SGArchiveFileExtractor();
        sw.setProgressValue(minprog + step * 2.0f);
        this.mArchiveFileCreator = new SGArchiveFileCreator();
    }

    public boolean loadDataSetFromDialog(final SGDrawingWindow wnd) {
        return this.loadDataSetFromEventDispatchThread(wnd, null);
    }
    
    private boolean loadArchiveDataSet(final SGDrawingWindow wnd, final File file) {
        // create temporary directory
        final File datasetTempDir = this._createTemporaryDirectory(wnd);
        if (datasetTempDir == null) {
            return false;
        }
        
        int errcode = -1;
        errcode = this.mArchiveFileExtractor.extract(wnd, datasetTempDir.getPath(), file);
        
        if (errcode == -1 || errcode == -2) {
            this.mArchiveFileExtractor.deleteExtractedFiles();
            datasetTempDir.delete();
            return false;
        } else if (errcode == CANCEL_OPTION) {
            this.mArchiveFileExtractor.deleteExtractedFiles();
            datasetTempDir.delete();
            return true;
        }
        final ArrayList<File> flist = this.mArchiveFileExtractor.getExtractedFileList();

        // get property file name
        final String pfname = SGApplicationUtility.getPathName(
                datasetTempDir.getAbsolutePath(),
                DATASET_PROPERTY_FILENAME);
        if (!flist.contains(new File(pfname))) {
            this.mArchiveFileExtractor.deleteExtractedFiles();
            datasetTempDir.delete();
            return false;
        }
        
        // the number of data files
        int dataFileNum = 0;
        for (int ii = 0; ii < flist.size(); ii++) {
        	File f = flist.get(ii);
        	String path = f.getPath();
        	if (this.isDataFile(path)) {
        		dataFileNum++;
        	}
        }

        // get data file name list
        final ArrayList<String> dfnameList = new ArrayList<String>();
        this._sortDataList(datasetTempDir, flist, dataFileNum, dfnameList);
        
        // get the image file
        File imageFile = null;
        for (int ii = 0; ii < flist.size(); ii++) {
        	File f = flist.get(ii);
        	String path = f.getPath();
//            String[] imgArray = SGIImageConstants.DRAWABLE_IMAGE_EXTENSIONS;
//            for (int jj = 0; jj < imgArray.length; jj++) {
//            	String suffix = "." + imgArray[jj];
//            	if (path.toLowerCase().endsWith(suffix)) {
//            		imageFile = f;
//            		break;
//            	}
//            }
//            if (imageFile != null) {
//            	break;
//            }
        	if (this.hasExtension(path, SGIImageConstants.DRAWABLE_IMAGE_EXTENSIONS)) {
        		imageFile = f;
        		break;
        	}
        }

        // load
        boolean result = _loadDataSet(wnd, pfname, dfnameList, imageFile);
//        this.mArchiveFileExtractor.deleteExtractedFiles();
//        datasetTempDir.delete();
        this.mDataSetFolderList.add(datasetTempDir);
        this.mDataSetFileList.addAll(this.mArchiveFileExtractor.getExtractedFileList());

        File f = this.mArchiveFileExtractor.getCurrentFile();
        if (f != null) {
            this.mMain.updateCurrentFile(f, FILE_TYPE.DATASET);
        }

        return result;
    }
    
    private boolean isDataFile(String path) {
    	String[] extensions = { SGIDataFileConstants.CSV_FILE_EXTENSION, 
    			SGIDataFileConstants.NETCDF_FILE_EXTENSION,
    			SGIDataFileConstants.HDF5_FILE_EXTENSION,
    			SGIDataFileConstants.MATLAB_FILE_EXTENSION };
    	return this.hasExtension(path, extensions);
    }
    
    private boolean hasExtension(String path, String[] extensions) {
    	for (String ext : extensions) {
    		String suffix = "." + ext;
    		if (path.toLowerCase().endsWith(suffix)) {
    			return true;
    		}
    	}
    	return false;
    }

    boolean loadDataSet(final SGDrawingWindow wnd, final File archiveFile) {
        
        int errcode = -1;
        // extract
        File file = archiveFile;
        if (file == null) {
            // get current directory
            String dir = this.mMain.getCurrentFileDirectory();
            String fileName = (archiveFile != null) ? archiveFile.getName() : null;
            this.mArchiveFileExtractor.setCurrentFile(dir, fileName);
            
            // show dialog
            file = this.mArchiveFileExtractor.getArchiveFileFromFileChooser(wnd);
        }
        if (file == null) {
            errcode = CANCEL_OPTION;
            return true;
        } else {
        	try {
            	wnd.setWaitCursor(true);
            	wnd.startIndeterminateProgress();
            	
                if (file.getName().endsWith(ARCHIVE_FILE_EXTENSION)) {
                    return this.loadArchiveDataSet(wnd, file);
                } else {
                    FILE_TYPE type = SGApplicationUtility.identifyDataFileType(file.getPath());
                    if (FILE_TYPE.NETCDF_DATA.equals(type)) {
                        SGNetCDFDataSetManager netcdfDatasetManager = new SGNetCDFDataSetManager(this.mMain);
                        try {
                            errcode = netcdfDatasetManager.load(wnd, file);
                        } catch (IOException e) {
                            return false;
                        }
                        if (errcode == OK_OPTION) {
                            File f = this.mArchiveFileExtractor.getCurrentFile();
                            if (f != null) {
                                this.mMain.updateCurrentFile(f, FILE_TYPE.DATASET);
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        	} finally {
        		wnd.endProgress();
                wnd.setWaitCursor(false);
        	}
        }
                
        return false;
    }

    /**
     * 
     * @return
     */
    boolean loadDataSetFromEventDispatchThread(
            final SGDrawingWindow wnd, final File archiveFile) {

        Boolean result = Boolean.FALSE;
        if (!SGMainFunctions.USE_FOXTROT) {
            if (loadDataSet(wnd, archiveFile)) {
                result = Boolean.TRUE;
            }
        } else {
            try {
                result = (Boolean) Worker.post(new Task() {
                    public Object run() throws Exception {
                        if (!loadDataSet(wnd, archiveFile)) {
                            return Boolean.FALSE;
                        }
                        return Boolean.TRUE;
                    }
                });

            } catch (Exception ex) {
                result = Boolean.FALSE;
            }
        }
        return result.booleanValue();
    }
    
    /**
     * Returns a temporary directory for data set file.
     * @return
     *        a temporary directory for data set file
     */
    private File getDataSetTempDir() {
        String filename = SGApplicationUtility.getPathName(TMP_DIR, DATASET_TEMPDIR_NAME);
        StringBuffer sb = new StringBuffer();
        sb.append(filename);
        sb.append(System.currentTimeMillis());
        File datasetTempDir = new File(sb.toString());
        return datasetTempDir;
    }

    // create the temporary directory to extract an archive file
    private File _createTemporaryDirectory(Component owner) {
        String failed = MSG_LOAD_DATASET_FAILED;

        // create temporary directory
        File datasetTempDir = this.getDataSetTempDir();
        if (datasetTempDir.mkdir() == false) {
            JOptionPane.showMessageDialog(owner, failed);
            return null;
        }

        return datasetTempDir;
    }

    // sort the list of data files
    private void _sortDataList(File datasetTempDir, ArrayList<File> flist,
            final int dataFileNum, ArrayList<String> dfnameList) {

        StringBuffer sb = new StringBuffer();
        sb.append(datasetTempDir.getAbsolutePath());
        sb.append(FILE_SEPARATOR);
        sb.append("id");
        String header = sb.toString();
        
        int fileCnt = 0;
        for (int ii = 0; ii < dataFileNum; ii++) {
            for (int jj = 0; jj < dataFileNum; jj++) {
            	StringBuffer sb2 = new StringBuffer();
                sb2.append(header);
                sb2.append(ii);
                sb2.append('-');
                sb2.append(jj);
//                sb2.append(".csv");
                String fnameHeader = sb2.toString();
//                if (!flist.contains(new File(fname))) {
//                    break;
//                }
                String fname = null;
                for (File f : flist) {
                	String path = f.getAbsolutePath();
                	if (path.startsWith(fnameHeader)) {
                		fname = path;
                		break;
                	}
                }
                if (fname == null) {
                	break;
                }
                dfnameList.add(fname);
                fileCnt++;
            }
            if (fileCnt >= dataFileNum) {
                break;
            }
        }
    }

    private boolean _loadDataSet(final SGDrawingWindow wnd,
            final String pfname, final ArrayList<String> dfnameList, final File imageFile) {
    	
        final File pfile = new File(pfname);
        if (pfile.exists() == false) {
            return false;
        }
        final String path = SGUtility.getCanonicalPath(pfile.getPath());
        if (path == null) {
            return false;
        }

        // check validity of the file
        URL url = null;
        try {
            url = pfile.toURI().toURL();
        } catch (MalformedURLException ex) {
            return false;
        }

        // create a Document object
        Document doc = SGUtilityText.getDocument(url);
        if (doc == null) {
            return false;
        }

        // get root element
        Element root = doc.getDocumentElement();

        // get version number
        String versionNumber = root.getAttribute(KEY_VERSION_NUMBER);

        // get window element
        Element elWnd = this.mMain.mPropertyFileManager
                .getWindowElement(doc);
        NodeList nListFigure = elWnd
                .getElementsByTagName(SGFigure.TAG_NAME_FIGURE);
        final int figureNum = nListFigure.getLength();
        int cnt = 0;
        final int[] dataNumArray = new int[figureNum];
        for (int ii = 0; ii < figureNum; ii++) {
            Node node = nListFigure.item(ii);
            if ((node instanceof Element) == false) {
                return false;
            }
            Element elFigure = (Element) node;
            NodeList nListData = elFigure
                    .getElementsByTagName(SGIFigureElementGraph.TAG_NAME_DATA);
            dataNumArray[ii] = nListData.getLength();
            cnt += dataNumArray[ii];
        }
        if (cnt != dfnameList.size()) {
            return false;
        }

        // create wrapped data
        WrappedData[] wDataArray = new WrappedData[dfnameList.size()];
        cnt = 0;
        for (int ii = 0; ii < figureNum; ii++) {
            final int dataNum = dataNumArray[ii];
            for (int jj = 0; jj < dataNum; jj++) {
                String fileName = (String) dfnameList.get(cnt);
                final int figureID = ii + 1;
                SGPropertyFileData pfData = new SGPropertyFileData(
                        figureID, null, null, null);
                pfData.setFileName(fileName);
                wDataArray[cnt] = new WrappedData(pfData);;
                cnt++;
            }
        }

        // clear old objects
        wnd.deleteImage();
        wnd.clearUndoBuffer();
        wnd.removeAllFigures();

        // add background image
        if (imageFile != null) {
        	byte[] imageByteArray = SGApplicationUtility.toByteArray(imageFile);
        	String ext = SGApplicationUtility.getImageExtension(imageFile);
        	if (ext == null) {
        		return false;
        	}
            if (!wnd.setImage(imageByteArray, ext, false)) {
            	return false;
            }
            wnd.setImageFilePath(imageFile.getAbsolutePath());
        }
        
        // set properties
        if (this.mMain.mPropertyFileManager.setPropertyFile(wnd,
                doc, wDataArray, true, versionNumber, LOAD_PROPERTIES_FROM_DATA_SET) == false) {
            return false;
        }
        
        // update the client rectangle
        wnd.updateClientRect();

        return true;
    }

    // Checks whether netCDF data objects exist in a given window.
    private boolean checkNetCDFDataExists(final SGDrawingWindow wnd) {
        return this.checkDataClass(wnd, SGNetCDFData.class);
    }

    // Checks whether multidimensional array data objects exist in a given window.
    private boolean checkMDArrayDataExists(final SGDrawingWindow wnd) {
        return this.checkDataClass(wnd, SGMDArrayData.class);
    }
    
    private boolean checkDataClass(final SGDrawingWindow wnd, Class<?> cls) {
        List<SGFigure> fList = wnd.getVisibleFigureList();
        for (SGFigure f : fList) {
            List<SGData> dList = f.getVisibleDataList();
            for (SGData d : dList) {
                if (cls.isAssignableFrom(d.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Save all objects in a given window to a data set file.
     * 
     * @param wnd
     *           a window
     * @return the status
     */
    public int saveDataSet(final SGDrawingWindow wnd) {
        
        final String failed = MSG_SAVE_DATASET_FAILED;
        final String dir = this.mMain.getCurrentFileDirectory();
        String fileName = SGApplicationUtility.getOutputFileName(wnd);
        if (fileName == null) {
        	fileName = DEFAULT_ARCHIVE_FILE_NAME;
        }
        String name = SGApplicationUtility.appendExtension(fileName, ARCHIVE_FILE_TYPE_SGA);

        ArrayList<File> flist;

        // create temporary directory
        File datasetTempDir = this.getDataSetTempDir();
        if (datasetTempDir.mkdir() == false) {
            JOptionPane.showMessageDialog(wnd, failed);
			return ERROR_OPTION;
        }

        // set the selected file name
        this.mArchiveFileCreator.setCurrentFile(dir, name);

        // show a file chooser and get selected file
        ArchiveFile zfile = null;
		try {
			zfile = this.mArchiveFileCreator.getArchiveFileFromFileChooser(wnd);
		} catch (IOException e) {
            SGUtility.showErrorMessageDialog(wnd, e.getMessage(), TITLE_ERROR);
			return ERROR_OPTION;
		}
        if (zfile == null) {
            return CANCEL_OPTION;
        }
        final String fileDesc = zfile.desc;
        
        // create an archive file
        if (fileDesc != null) {
            if (fileDesc.startsWith(ARCHIVE_FILE_DESCRIPTION_SGA107) ||
                    fileDesc.startsWith(ARCHIVE_FILE_DESCRIPTION)) {

            	OPERATION mode;
                if (fileDesc.startsWith(ARCHIVE_FILE_DESCRIPTION_SGA107)) {
                    mode = OPERATION.SAVE_TO_ARCHIVE_DATA_SET_107;
                } else {
                    mode = OPERATION.SAVE_TO_ARCHIVE_DATA_SET;
                }

            	// checks the data type
                if (OPERATION.SAVE_TO_ARCHIVE_DATA_SET_107.equals(mode)) {
                	if (this.checkNetCDFDataExists(wnd)
                			|| this.checkMDArrayDataExists(wnd)) {
                        SGUtility.showErrorMessageDialog(wnd, ERRMSG_FAILED_TO_CREATE_DATASET, 
                        		SGIConstants.TITLE_ERROR);
            			return ERROR_OPTION;
                	}
                }
            	
                flist = _dumpDataSet(wnd, datasetTempDir, new SGExportParameter(mode));
            	
                if (flist == null) {
                    JOptionPane.showMessageDialog(wnd, failed);
                    datasetTempDir.delete();
        			return ERROR_OPTION;
                }
                int ret = this.mArchiveFileCreator.create(wnd, datasetTempDir.getPath(), zfile.file);
                
                // delete temporary files
                for (int kk = 0; kk < flist.size(); kk++) {
                    File f = (File) flist.get(kk);
                    f.delete();
                }
                datasetTempDir.delete();

                if (ret != OK_OPTION) {
                    return ret;
                }

                File f = this.mArchiveFileCreator.getCurrentFile();
                if (f != null) {
                    this.mMain.updateCurrentFile(f, FILE_TYPE.DATASET);
                }

                return OK_OPTION;
//            } else if (fileDesc.startsWith(ARCHIVE_FILE_DESCRIPTION_NETCDF)) {
//                final String versionString = this.mMain.mAppProp.getVersionString();
//                SGNetCDFDataSetManager netcdfDatasetManager = new SGNetCDFDataSetManager(this.mMain);
//                int result = netcdfDatasetManager.save(wnd, versionString, datasetTempDir, zfile.file);
//                
//                // delete temporary files
//                File[] tempFiles = datasetTempDir.listFiles();
//                for (File f : tempFiles) {
//                    f.delete();
//                }
//                datasetTempDir.delete();
//                if (result != SUCCESSFUL_COMPLETION) {
//                    JOptionPane.showMessageDialog(wnd, failed);
//                    return result;
//                }
//                
//                File f = this.mArchiveFileCreator.getSelectedFile();
//                if (f != null) {
//                    this.mMain.updateCurrentFile(f, FILE_TYPE.DATASET);
//                }
//
//                return OK_OPTION;
            } else {
    			return ERROR_OPTION;
            }
        } else {
			return ERROR_OPTION;
        }
    }

    /**
     * 
     * @param wnd
     * @param filePath
     * @param archiveType
     * @return
     */
    public int saveDataSet(final SGDrawingWindow wnd, final String filePath, final String archiveType) {
        
        ArrayList<File> flist = null;

        // create temporary directory
        File datasetTempDir = this.getDataSetTempDir();
        if (datasetTempDir.mkdir() == false) {
			return ERROR_OPTION;
        }
        
        File outFile = new File(filePath);
        
        if (ARCHIVE_FILE_TYPE_SGA.equalsIgnoreCase(archiveType) ||
                ARCHIVE_FILE_TYPE_SGA107.equalsIgnoreCase(archiveType)) {

        	OPERATION mode;
            if (ARCHIVE_FILE_TYPE_SGA.equalsIgnoreCase(archiveType)) {
                mode = OPERATION.SAVE_TO_ARCHIVE_DATA_SET;
            } else {
                mode = OPERATION.SAVE_TO_ARCHIVE_DATA_SET_107;
            }

        	// checks the data type
            if (OPERATION.SAVE_TO_ARCHIVE_DATA_SET_107.equals(mode)) {
            	if (!this.checkNetCDFDataExists(wnd)
            			|| !this.checkMDArrayDataExists(wnd)) {
        			return ERROR_OPTION;
            	}
            }

            flist = _dumpDataSet(wnd, datasetTempDir, new SGExportParameter(mode));
            if (flist == null) {
                datasetTempDir.delete();
    			return ERROR_OPTION;
            }
            
            int ret = this.mArchiveFileCreator.create(wnd, datasetTempDir.getPath(), outFile);
            
            // delete temporary files
            for (int kk = 0; kk < flist.size(); kk++) {
                File f = (File) flist.get(kk);
                f.delete();
            }
            datasetTempDir.delete();
            if (ret != OK_OPTION) {
                return ret;
            }
            
            this.mMain.updateCurrentFile(outFile, FILE_TYPE.DATASET);
            return OK_OPTION;
            
        } else if (ARCHIVE_FILETYPE_NETCDF.equalsIgnoreCase(archiveType)) {
            final String versionString = this.mMain.mAppProp.getVersionString();
            SGNetCDFDataSetManager netcdfDatasetManager = new SGNetCDFDataSetManager(this.mMain);
            int result = netcdfDatasetManager.save(wnd, versionString, datasetTempDir, outFile);
            
            // delete temporary files
            File[] tempFiles = datasetTempDir.listFiles();
            for (File f : tempFiles) {
                f.delete();
            }
            datasetTempDir.delete();
            if (result != SUCCESSFUL_COMPLETION) {
                return result;
            }
            
            this.mMain.updateCurrentFile(outFile, FILE_TYPE.DATASET);
            return OK_OPTION;
        }
        
		return ERROR_OPTION;
    }

    /**
     * 
     * @param wnd window
     * @param datasetDir temporary directory that is used to archive dataset files.
     * @param mode SAVE_PROPERTIES_TO_DATA_SET_OLDER(version 1.0.7) or SAVE_PROPERTIES_TO_DATA_SET.
     * @return list of data file that are included in archive file.
     */
    private ArrayList<File> _dumpDataSet(final SGDrawingWindow wnd,
            final File datasetDir, final SGExportParameter mode) {
        ArrayList<File> flist = new ArrayList<File>();

        // output property file
        final String pfname = SGApplicationUtility.getPathName(
                datasetDir.getPath(), DATASET_PROPERTY_FILENAME);
        int ret = this.mMain.mPropertyFileManager.saveProperties(
                wnd, pfname, mode);
        if (ret != OK_OPTION) {
            return null;
        }
        flist.add(new File(pfname));

        // output data files
        ArrayList<SGFigure> figures = wnd.getVisibleFigureList();
        for (int ii = 0; ii < figures.size(); ii++) {
            SGFigure figure = (SGFigure) figures.get(ii);
            SGIFigureElementGraph gElement = figure.getGraphElement();
            List<SGData> dataList;
            dataList = figure.getVisibleDataList();
            for (int jj = 0; jj < dataList.size(); jj++) {
                SGData data = (SGData) dataList.get(jj);
                String ext = data.getDataSetFileExtension();
                StringBuffer sb = new StringBuffer();
                sb.append(datasetDir.getPath());
                sb.append(FILE_SEPARATOR);
                sb.append("id");
                sb.append(ii);
                sb.append('-');
                sb.append(jj);
                sb.append('.');
                sb.append(ext);
                String fname = sb.toString();
                SGIElementGroupSetForData gs = (SGIElementGroupSetForData) gElement.getChild(data);
                File file = new File(fname);
                if (!gs.saveData(file, mode, null)) {
                    for (int kk = 0; kk < flist.size(); kk++) {
                        File f = (File) flist.get(kk);
                        f.delete();
                    }
                    return null;
                }
                flist.add(file);
            }
        }
        return flist;
    }
    
    void clearDataSetFiles() {
    	for (File f : this.mDataSetFileList) {
    		if (f.exists()) {
    			f.delete();
    		}
    	}
    	for (File f : this.mDataSetFolderList) {
    		if (f.exists()) {
    			f.delete();
    		}
    	}
    }

}

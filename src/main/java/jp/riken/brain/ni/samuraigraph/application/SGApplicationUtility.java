package jp.riken.brain.ni.samuraigraph.application;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jp.riken.brain.ni.samuraigraph.application.SGDataCreator.FileColumn;
import jp.riken.brain.ni.samuraigraph.base.SGBufferedFileReader;
import jp.riken.brain.ni.samuraigraph.base.SGCSVTokenizer.Token;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGExtensionFileFilter;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGFileChooser;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementLegend;
import jp.riken.brain.ni.samuraigraph.base.SGIProgressControl;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGDataTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDateDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGHDF5File;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIMDArrayConstants;
import jp.riken.brain.ni.samuraigraph.data.SGMATLABFile;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import jp.riken.brain.ni.samuraigraph.data.SGNumberDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGSamplingDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGTextDataColumn;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGStringBraceModifier;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;

import org.w3c.dom.DOMImplementation;

import ucar.nc2.NetcdfFile;
import ch.systemsx.cisd.hdf5.HDF5FactoryProvider;
import ch.systemsx.cisd.hdf5.IHDF5Reader;

import com.jmatio.io.MatFileReader;

/**
 * 
 * This class provides static methods for the application.
 */
public class SGApplicationUtility implements SGIApplicationConstants, SGIApplicationTextConstants, 
		SGIDataColumnTypeConstants, SGIImageConstants, SGIConstants {

    /**
     * Delete all files recursively.
     * 
     * @param f
     *            The abstract pathname to be deleted.
     */
    public static void deleteRecursively(final File f) {
        if (f.isDirectory()) {
            String fs = SGIConstants.FILE_SEPARATOR;
            String[] fList = f.list();
            StringBuffer sb = new StringBuffer();
            sb.append(f.toString());
            sb.append(fs);
            final int len = sb.length();
            for (int ii = 0; ii < fList.length; ii++) {
                sb.append(fList[ii].toString());
                File f_ = new File(sb.toString());
                deleteRecursively(f_);
                sb.setLength(len);
            }
        }
        f.delete();
    }

    /**
     * Delete all files recursively when the virtual machine terminates.
     * 
     * @param f
     *            The abstract pathname to be deleted.
     */
    public static void deleteOnExitRecursively(final File f) {
        if (f.isDirectory()) {
            String fs = SGIConstants.FILE_SEPARATOR;
            String[] fList = f.list();
            StringBuffer sb = new StringBuffer();
            sb.append(f.toString());
            sb.append(fs);
            final int len = sb.length();
            for (int ii = 0; ii < fList.length; ii++) {
                sb.append(fList[ii].toString());
                File f_ = new File(sb.toString());
                deleteOnExitRecursively(f_);
                sb.setLength(len);
            }
        }
        f.deleteOnExit();
    }

    /**
     * Copies a file.
     * 
     * @param src
     *           the source file
     * @param dest
     *           the destination file
     * @return true if succeeded
     */
    public static boolean copyBinaryFile(final File src, final File dest)
            throws IOException {
        if (src == null || dest == null) {
            return false;
        }

        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);
        } catch (FileNotFoundException ex) {
            if (in != null) {
                in.close();
            }
            return false;
        }

        BufferedInputStream bis = new BufferedInputStream(in);
        BufferedOutputStream bos = new BufferedOutputStream(out);

        final int bufferLength = 1024;
        byte[] buffer = new byte[bufferLength];
        int len;
        while ((len = bis.read(buffer, 0, bufferLength)) != -1) {
            bos.write(buffer, 0, len);
        }

        // close I/O streams
        bis.close();
        bos.close();

        return true;
    }

    /**
     * Copy file from f1 to f2.
     * 
     * @param f1
     * @param f2
     * @return true:success, false:failure
     */
    public static boolean copyBinaryFile(final String f1, final String f2)
            throws IOException {
        if (f1 == null || f2 == null) {
            return false;
        }
        return copyBinaryFile(new File(f1), new File(f2));
    }

    /**
     * Returns a DOMImplementation instance.
     * 
     * @return
     */
    public static DOMImplementation getDOMImplementation() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            return null;
        }
        DOMImplementation domImpl = builder.getDOMImplementation();

        return domImpl;
    }

    /**
     * Find files with the given name.
     * 
     * @param baseDir
     *            base directory to start to search
     * @param fileName
     *            the file name to search
     * @param pathList
     *            a list to add the found files
     */
    public static void findFiles(final File baseDir, final String fileName,
            final List<String> pathList) {
        if (baseDir.isDirectory() == false) {
            return;
        }
        File[] fArray = baseDir.listFiles();
        for (int ii = 0; ii < fArray.length; ii++) {
            if (fArray[ii].isDirectory()) {
                findFiles(fArray[ii], fileName, pathList);
            } else {
                String name = fArray[ii].getName();
                if (name.equals(fileName)) {
                    pathList.add(fArray[ii].getAbsolutePath());
                }
            }
        }
    }

    /**
     * Finds and returns files.
     * 
     * @param directory
     *           the directory to search
     * @return the list of files
     */
    public static List<File> findFiles(String directory) {
    	List<File> fileList = new ArrayList<File>();
        File dir = null;
        URL url = ClassLoader.getSystemClassLoader().getResource(directory);
        if (url == null) {
        	return null;
        }
        dir = new File(url.getFile());
        if (!dir.exists()) {
            return null;
        }
        File[] files = dir.listFiles();
        for (int ii = 0; ii < files.length; ii++) {
            fileList.add(files[ii]);
        }
        return fileList;
    }

    public static final void showDataFileInvalidMessageDialog(final Window owner) {
        SGUtility.showErrorMessageDialog(owner, MSG_DATA_FILE_OPEN_FAILURE,
                SGIConstants.TITLE_ERROR);
    }

    public static final void showDataTypeInvalidMessageDialog(final Window owner) {
        SGUtility.showErrorMessageDialog(owner, MSG_INVALID_DATA_TYPE,
                SGIConstants.TITLE_ERROR);
    }

    /**
     * Create a file path name.
     * @param parent
     *              The parent path name string
     * @param child
     *              The child path name string
     * @return
     *              The full path name string
     */
    public static String getPathName(final String parent, final String child) {
        StringBuffer sb = new StringBuffer();
        sb.append(parent);
        sb.append(SGIConstants.FILE_SEPARATOR);
        sb.append(child);
        String path = sb.toString();
        return path;
    }

    /**
     * Append the extension to the file name.
     * 
     * @param filename
     *           file name
     * @param extension
     *           the extension
     * @return file name with the extension
     */
    public static String appendExtension(final String filename, final String extension) {
        StringBuffer sb = new StringBuffer();
        sb.append(filename);
        sb.append('.');
        sb.append(extension);
        String path = sb.toString();
        return path;
    }
    
    /**
     * Removes the file extension if it exists from given file name.
     * 
     * @param fileName
     *           the file name
     * @return file name without file extension
     */
    public static String removeExtension(final String fileName) {
    	final int index = fileName.lastIndexOf('.');
    	if (index == -1) {
    		return fileName;
    	} else {
        	return fileName.substring(0, index);
    	}
    }

    /**
     * Returns whether a given file name has the given extension.
     * 
     * @param fileName
     *           the file name
     * @param extension
     *           the extension
     * @return true if a given file name has the given extension
     */
    public static boolean hasExtension(final String fileName, final String extension) {
    	return fileName.toLowerCase().endsWith("." + extension.toLowerCase());
    }

    /**
     * Returns whether a given file name has one of the given extensions.
     * 
     * @param fileName
     *           the file name
     * @param extensions
     *           array of extensions
     * @return true if a given file name has the given extension
     */
    public static boolean hasExtension(final String fileName, final String[] extensions) {
    	for (String ext : extensions) {
    		if (hasExtension(fileName, ext)) {
    			return true;
    		}
    	}
    	return false;
    }

    static SGDataColumnInfoSet getSDArrayDataColInfoSet(FileColumn[] fileCols, final int length,
			String dataType, Map<String, Object> infoMap) {

		final int colNum = fileCols.length;
		SGDataColumnInfo[] colInfoArray = new SGDataColumnInfo[colNum];
		for (int ii = 0; ii < colNum; ii++) {
			colInfoArray[ii] = new SGSDArrayDataColumnInfo(fileCols[ii].title,
					fileCols[ii].valueType, length);
		}
		SGDataColumnInfo[] aColInfoArray = getAdditionalInfoArray(dataType,
				infoMap, colInfoArray);

		SGDataColumnInfo[] allInfoArray = new SGDataColumnInfo[colInfoArray.length + aColInfoArray.length];
		for (int ii = 0; ii < colInfoArray.length; ii++) {
			allInfoArray[ii] = colInfoArray[ii];
		}
		for (int ii = 0; ii < aColInfoArray.length; ii++) {
			allInfoArray[ii + colInfoArray.length] = aColInfoArray[ii];
		}
		
		SGDataColumnInfoSet colInfoSet = new SGDataColumnInfoSet(allInfoArray);
		return colInfoSet;
	}

    static SGDataColumnInfo[] getAdditionalInfoArray(final String dataType, 
    		final Map<String, Object> infoMap, final SGDataColumnInfo[] colInfoArray) {

    	List<SGDataColumnInfo> aColInfoList = new ArrayList<SGDataColumnInfo>();
    	
		// create a column for the sampling rate
		Double samplingRate = (Double) infoMap.get(SGIDataInformationKeyConstants.KEY_SAMPLING_RATE);
		if (samplingRate != null) {
			// create column info for sampling
			SGDataColumnInfo samplingRateColumnInfo = new SGSDArrayDataColumnInfo(
					SGDataUtility.createSamplingRateTitle(samplingRate
							.doubleValue()),
					SGIDataColumnTypeConstants.VALUE_TYPE_SAMPLING_RATE);

			// set X_VALUE to the column type
			samplingRateColumnInfo.setColumnType(X_VALUE);

			// add to the list
			aColInfoList.add(samplingRateColumnInfo);
		}
		
//		// create a column for the tick label of the date
//		List<Integer> dateIndexList = new ArrayList<Integer>();
//		for (int ii = 0; ii < colInfoArray.length; ii++) {
//			final String valueType = colInfoArray[ii].getValueType();
//			if (VALUE_TYPE_DATE.equals(valueType)) {
//				dateIndexList.add(Integer.valueOf(ii));
//			}
//		}
//		for (int ii = 0; ii < dateIndexList.size(); ii++) {
//			Integer index = dateIndexList.get(ii);
//			String title = "Labels for date column of No." + (index + 1);
//			SGDataColumnInfo dateColumnInfo = new SGDataColumnInfo(title, 
//					SGIDataColumnTypeConstants.VALUE_TYPE_TEXT);
//			aColInfoList.add(dateColumnInfo);
//		}

		// create an array of columns
		SGDataColumnInfo[] addInfoArray = new SGDataColumnInfo[aColInfoList.size()];
		addInfoArray = aColInfoList.toArray(addInfoArray);

		return addInfoArray;
	}

    /**
     * Return the replaced data type of text data.
     * <p>
     * Replace the data type SXY_SAMPLING to SXY_MULTIPLE and SXY_DATE to SXY.
     * 
     * @param dataType
     * @return replaced data type
     */
    static String getArrayDataType(final String dataType) {
        String type = dataType;
        // replace the data type
        if (SGDataTypeConstants.SXY_SAMPLING_DATA.equals(dataType)) {
            type = SGDataTypeConstants.SXY_MULTIPLE_DATA;
        } else if (SGDataTypeConstants.SXY_DATE_DATA.equals(dataType)) {
            type = SGDataTypeConstants.SXY_DATA;
        }
        return type;

    }
    
    /**
     * Return exact string that is one of the specified types.
     * 
     * @param types type strings
     * @param str string which contains not letter or digit characters.
     * @return exact string that is one of the specified types. or null.
     */
    static String getExactTypeString(final String[] types, final String str) {
        if (null==str) {
            return null;
        }
        if (types.length==0) {
            return null;
        }
        for (int i = 0; i < types.length; i++) {
            if (SGUtilityText.isEqualString(types[i], str)) {
                return types[i];
            }
        }
        return null;
    }
    
    /**
     * Creates a byte array from given file object.
     * 
     * @param f
     *          the file
     * @return a byte array
     */
    public static byte[] toByteArray(final File f) {
		byte[] byteArray = null;
		ByteArrayOutputStream bos = null;
		BufferedInputStream bis = null;
		FileInputStream fis = null;
		try {
			// read image byte data
			try {
				fis = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				return null;
			} finally {
			}
			bis = new BufferedInputStream(fis);
			bos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			while (bis.read(buf) != -1) {
				bos.write(buf);
			}

			// get byte data
			byteArray = bos.toByteArray();

		} catch (IOException e) {
			return null;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
				}
			}
		}
		return byteArray;
    }

    /**
     * Returns the extension of an image file.
     * 
     * @param f
     *          image file
     * @return the extension of an image file
     */
    public static String getExtension(final File f) {
    	final String path = f.getPath();
    	final String ext = SGUtilityText.lastSubstring(path, '.');
    	return ext;
    }

    /**
     * Returns the extension of an image file.
     * 
     * @param f
     *          image file
     * @return the extension of an image file
     */
    public static String getImageExtension(final File f) {
    	final String ext = SGApplicationUtility.getExtension(f);
    	if (ext == null) {
    		return null;
    	}
    	return findExtension(ext);
    }

    /**
     * Finds and returns the extension of an image file.
     * 
     * @param str
     *            a text string to compare
     * @return a text string of the extension
     */
    public static String findExtension(final String str) {
    	for (int ii = 0; ii < DRAWABLE_IMAGE_EXTENSIONS.length; ii++) {
    		if (DRAWABLE_IMAGE_EXTENSIONS[ii].equalsIgnoreCase(str)) {
    			return DRAWABLE_IMAGE_EXTENSIONS[ii];
    		}
    	}
    	return null;
    }

    static SGIDataSource createDataSource(final String path, final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap) throws FileNotFoundException {
    	return createDataSource(path, colInfoSet, infoMap, null, null, -1, false);
    }
    
    // Creates the data source.
    static SGIDataSource createDataSource(final String path, final SGDataColumnInfoSet colInfoSet,
            final Map<String, Object> infoMap, final SGIProgressControl progress,
            final String versionNumber, final int mode, boolean showProgress) throws FileNotFoundException {

        if (path == null || infoMap == null) {
        	return null;
        }

        // when information is empty, returns null
        if (infoMap.size() == 0) {
            return null;
        }
        
        final boolean bp = showProgress && (progress != null);

        SGIDataSource dataSource = null;
        try {
            final String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
            if (SGDataUtility.isSDArrayData(dataType)) {
				
            	if (bp) {
                    progress.setProgressMessage(PROGRESS_MESSAGE_READFILE);
            	}

                // read text data from file
                List<String>[] listArray = createListArray(path);
                if (listArray == null) {
                    return null;
                }

            	if (bp) {
                    // allocate memory
                    progress.setProgressMessage(PROGRESS_MESSAGE_CREATEDATA);
            	}

                // create columns
                final SGDataColumnInfo[] colInfo = colInfoSet.getDataColumnInfoArray();
				SGDataColumn[] columns = createColumnArray(colInfo,
						listArray, infoMap, mode, versionNumber);
                if (columns == null) {
                    return null;
                }
		        
                if (versionNumber != null && mode == LOAD_PROPERTIES_FROM_DATA_SET) {
                    // if version of the property file is older than 2.0.0
                    if ("".equals(versionNumber)) {
                    	// if columns contains that of error bars, swap them
                    	// because old versions of Samurai Graph exports data columns 
                    	// into a data set file in reverse order
                    	int lowerIndex = -1;
                    	int upperIndex = -1;
                    	for (int ii = 0; ii < colInfo.length; ii++) {
                    		String columnType = colInfo[ii].getColumnType();
                    		if (columnType.startsWith(LOWER_ERROR_VALUE)) {
                    			lowerIndex = ii;
                    		}
                    		if (columnType.startsWith(UPPER_ERROR_VALUE)) {
                    			upperIndex = ii;
                    		}
                    	}
                    	if (lowerIndex != -1 && upperIndex != -1) {
                    		SGDataColumn tmp = columns[lowerIndex];
                    		columns[lowerIndex] = columns[upperIndex];
                    		columns[upperIndex] = tmp;
                    	}
                    }
                }

                // create data source
                dataSource = new SGSDArrayFile(path, columns);

            } else if (SGDataUtility.isNetCDFData(dataType)) {

                NetcdfFile ncFile = null;
                try {
                    ncFile = SGApplicationUtility.openNetCDF(path);
                } catch (IOException e) {
                    return null;
                }

                // create data source
                dataSource = new SGNetCDFFile(ncFile);
                
            } else if (SGDataUtility.isMDArrayData(dataType)) {
            	dataSource = SGApplicationUtility.openMDArrayFile(dataType, path);
            	
            } else {
                return null;
            }
			
        } finally {
        	if (bp) {
                progress.endProgress();
        	}
        }
        
        return dataSource;
    }

    public static NetcdfFile openNetCDF(final String path) throws IOException {
        NetcdfFile ncFile = NetcdfFile.open(path);
        return ncFile;
    }
    
    public static IHDF5Reader openHDF5(final String path) throws HDF5Exception {
		IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(new File(path));
		return reader;
    }
    
    public static MatFileReader openMAT(final String path) throws IOException {
		MatFileReader reader = new MatFileReader();
		File file = new File(path);
		final long len = file.length();
		if (len == 0L) {
			// throws IOException for an empty file because NullPointerException
			// is invoked for an empty file
			throw new IOException("An empty file is given.");
		}
		reader.read(file);
		return reader;
    }
    
    public static SGMDArrayFile openMDArrayFile(final String dataType, final String path) {
    	SGMDArrayFile mdFile = null;
	    if (SGDataUtility.isHDF5FileData(dataType)) {
			IHDF5Reader reader = null;
			try {
				reader = SGApplicationUtility.openHDF5(path);
			} catch (HDF5Exception e) {
				return null;
			}
			mdFile = new SGHDF5File(reader);
	    } else if (SGDataUtility.isMATLABData(dataType)) {
			MatFileReader reader = null;
			try {
				reader = SGApplicationUtility.openMAT(path);
			} catch (IOException e) {
				return null;
			}
			mdFile = new SGMATLABFile(path, reader);
	    }
	    return mdFile;
    }
    
    static final char[] WIN_FORBIDDEN_CHARS = { '/', ':', '*', '?', '"', '<', '>', '|' };
    
    static final String WIN_FORBIDDEN_PATTERN = "[" + new String(WIN_FORBIDDEN_CHARS) + "]";

    /**
     * Returns whether given name of output file is valid.
     * 
     * @param name
     *           the name of output file
     * @return true if given file name is valid
     */
    public static boolean checkOutputFileName(String name) {
        if (SGUtility.identifyOS(OS_NAME_WINDOWS)) {
        	// checks forbidden characters
        	char[] cArray = name.toCharArray();
        	if (SGUtility.contains(cArray, WIN_FORBIDDEN_CHARS)) {
        		return false;
        	}
    		
    		// checks reserved words
    		if (!checkReservedWordsForWin(name)) {
    			return false;
    		}
    		
        } else if (SGUtility.identifyOS(OS_NAME_MACOSX)) {
        	// checks a period at the beginning of the string
        	if (name.startsWith(".")) {
        		return false;
        	}
        	
        } else {
        	// checks forbidden characters
        	if (name.contains("/")) {
        		return false;
        	}
    		
        	// checks a period or a slash at the beginning of the string
        	if (name.startsWith(".") || name.startsWith("/")) {
        		return false;
        	}
        }

        return true;
    }

    /**
     * Returns a text string for the name of output file with given original file name.
     * 
     * @param name
     *           the original file name
     * @return a text string for the name of output file
     */
    public static String getOutputFileName(final String name) {
    	String ret = name;
        if (SGUtility.identifyOS(OS_NAME_WINDOWS)) {
        	// removes forbidden characters
    		ret = ret.replaceAll(WIN_FORBIDDEN_PATTERN, "");
    		
    		// checks reserved words
    		if (!checkReservedWordsForWin(ret)) {
    			return null;
    		}
    		
        } else if (SGUtility.identifyOS(OS_NAME_MACOSX)) {
        	// removes forbidden characters
    		ret = ret.replaceAll(":", "");
    		
        	// removes periods at the beginning of the string
        	char[] cArray = ret.toCharArray();
        	int start = -1;
        	for (int ii = 0; ii < cArray.length; ii++) {
        		final char c = cArray[ii];
        		if (c != '.') {
        			start = ii;
        			break;
        		}
        	}
        	if (start == -1) {
    			return null;
        	}
        	ret = ret.substring(start);
        } else {
        	// removes forbidden characters
    		ret = ret.replaceAll("/", "");
    		
        	// removes periods or slashes at the beginning of the string
        	char[] cArray = ret.toCharArray();
        	int start = -1;
        	for (int ii = 0; ii < cArray.length; ii++) {
        		final char c = cArray[ii];
        		if (c != '.' && c != '/') {
        			start = ii;
        			break;
        		}
        	}
        	if (start == -1) {
    			return null;
        	}
        	ret = ret.substring(start);
        }
        ret = ret.trim();
        return ret;
    }
    
    /**
     * Returns a text string for the name of output file.
     * 
     * @param wnd
     *           a window
     * @return a text string for the name of output file
     */
    public static String getOutputFileName(SGDrawingWindow wnd) {
    	ArrayList<SGFigure> figureList = wnd.getVisibleFigureList();
    	if (figureList.size() == 0) {
    		return null;
    	}
    	String ret = null;
    	for (SGFigure figure : figureList) {
        	List<SGData> dataList = figure.getVisibleDataList();
        	if (dataList.size() == 0) {
        		continue;
        	}
        	SGIFigureElementLegend legend = figure.getLegendElement();
        	if (legend.isVisible()) {
            	List<SGData> vDataList = legend.getViewableDataList();
            	if (vDataList.size() != 0) {
                	SGData headData = vDataList.get(0);
                	String name = figure.getDataName(headData);
                	name = getOutputFileName(name);
                	if (name == null) {
                		continue;
                	}
                    if (!SGUtilityText.isValidString(name)) {
                    	continue;
                    }
                    
                	// remove escape characters
                	return SGUtility.removeEscapeChar(name);
            	} else {
            		ret = getDataFileName(dataList);
            	}
        	} else {
        		ret = getDataFileName(dataList);
        	}
        	if (ret != null) {
        		break;
        	}
    	}
    	return ret;
    }
    
    private static boolean checkReservedWordsForWin(String name) {
    	String uName = name.toUpperCase();
		String[] reserved = { "CON", "PRN", "AUX", "NUL" };
		for (String res : reserved) {
			if (res.equals(uName)) {
				return false;
			}
		}
		final String comString = "COM";
		final String lptString = "LPT";
		String headString = null;
		if (uName.startsWith(comString)) {
			headString = comString;
		} else if (uName.startsWith(lptString)) {
			headString = lptString;
		}
		if (headString != null) {
			if (name.length() > headString.length()) {
				String sub = name.substring(headString.length(), name.length());
				Integer num = SGUtilityText.getInteger(sub);
				if (num != null) {
					if (num >= 0 && num <= 9) {
						return false;
					}
				}
			}
		}
		return true;
    }
    
    private static String getDataFileName(List<SGData> dataList) {
    	String path = null;
    	for (SGData data : dataList) {
    		path = data.getDataSource().getPath();
    		if (path != null) {
    			break;
    		}
    	}
    	if (path == null) {
    		return null;
    	}
		return SGUtility.getSimpleFileName(path);
    }

	/**
	 * Returns the bounds of virtual devices.
	 * 
	 * @return the bounds of virtual devices
	 */
	public static Rectangle[] getVirtualBoundsArray() {
		List<Rectangle> boundsList = new ArrayList<Rectangle>();
		GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getScreenDevices();
		for (int ii = 0; ii < gs.length; ii++) {
			GraphicsDevice gd = gs[ii];
			GraphicsConfiguration[] gc = gd.getConfigurations();
			for (int jj = 0; jj < gc.length; jj++) {
				boundsList.add(gc[jj].getBounds());
			}
		}
		Rectangle[] boundsArray = new Rectangle[boundsList.size()];
		return boundsList.toArray(boundsArray);
	}

	/**
	 * Returns the bounds of virtual devices.
	 * 
	 * @return the bounds of virtual devices
	 */
	public static Rectangle getVirtualBounds(Rectangle[] rectArray) {
		Rectangle virtualBounds = new Rectangle();
		for (int ii = 0; ii < rectArray.length; ii++) {
			virtualBounds = virtualBounds.union(rectArray[ii]);
		}
		return virtualBounds;
	}

	/**
	 * Creates an instance of data object from given class object.
	 * 
	 * @param cl
	 *           the class object
	 * @return created data object
	 */
    public static SGData createDataInstance(Class<?> cl) {
        SGData data = null;
        try {
            Object obj = cl.newInstance();
            if (obj instanceof SGData) {
                data = (SGData)obj;
            }
        } catch (InstantiationException e) {
        	return null;
		} catch (IllegalAccessException e) {
			return null;
        }
        return data;
    }

    // adjusts the stride
    static void adjustSXYDataStride(Map<String, Object> infoMap, String strideKey, final int length) {
        SGIntegerSeriesSet stride = (SGIntegerSeriesSet) infoMap.get(strideKey);
        SGIntegerSeriesSet tickLabelStride = (SGIntegerSeriesSet) infoMap.get(
        		SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
        if (stride != null) {
        	stride = SGUtility.createIndicesWithinRange(stride, length);
        	infoMap.put(strideKey, stride);
        }
        if (tickLabelStride != null) {
        	tickLabelStride = SGUtility.createIndicesWithinRange(tickLabelStride, length);
        	infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE, tickLabelStride);
        }
    }
    
    static int updateInformationMap(SGDataColumnInfoSet colInfoSet, Map<String, Object> infoMap,
    		Map<String, Object> pfInfoMap) {
    	
        final int di = SGIConstants.DATA_FILE_INVALID;

    	String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);

        // multiple variable or not
        if (SGDataUtility.isSXYTypeData(dataType)) {
        	SGDataColumnInfo[] columns = colInfoSet.getDataColumnInfoArray();
            Boolean multipleVariable = (Boolean) pfInfoMap.get(
            		SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE);
            if (multipleVariable == null) {
            	return di;
            }
            infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, multipleVariable);

            int arrayLength = -1;
            int indexLength = -1;

            if (SGDataUtility.isSDArrayData(dataType)) {
            	if (columns.length == 0) {
            		return di;
            	}
            	SGSDArrayDataColumnInfo sdCol = (SGSDArrayDataColumnInfo) columns[0]; 
            	indexLength = sdCol.getLength();
            	if (indexLength == -1) {
            		return di;
            	}
            } else if (SGDataUtility.isNetCDFData(dataType)) {
        		SGNetCDFDataColumnInfo pickedUpCol = null;
            	for (int jj = 0; jj < columns.length; jj++) {
            		SGNetCDFDataColumnInfo ncInfo = (SGNetCDFDataColumnInfo) columns[jj];
            		String columnType = ncInfo.getColumnType();
            		if (X_VALUE.equalsIgnoreCase(columnType) || Y_VALUE.equalsIgnoreCase(columnType)) {
            			if (ncInfo.isCoordinateVariable()) {
            				arrayLength = ncInfo.getDimension(0).getLength();
            			}
            		} else if (INDEX.equalsIgnoreCase(columnType)) {
            			if (ncInfo.isCoordinateVariable()) {
            				indexLength = ncInfo.getDimension(0).getLength();
            			}
            		} else if (PICKUP.equalsIgnoreCase(columnType)) {
            			if (ncInfo.isCoordinateVariable()) {
            				pickedUpCol = ncInfo;
            			}
            		}
            	}
                if (pickedUpCol != null) {
                    // get information of picked up dimension
                    Object indicesObj = pfInfoMap.get(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
                    if (indicesObj != null) {
                    	String str = indicesObj.toString();
                    	final int num = pickedUpCol.getDimension(0).getLength();
                    	SGIntegerSeriesSet indices = SGIntegerSeriesSet.parse(str, num);
                    	if (indices != null) {
                        	infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, indices);
                    	}
                    } else {
                        Object startObj = pfInfoMap.get(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_START);
                        Object endObj = pfInfoMap.get(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_END);
                        Object stepObj = pfInfoMap.get(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_STEP);
                        if (startObj == null || endObj == null || stepObj == null) {
                            SGNetCDFDataColumnInfo[] nCols = new SGNetCDFDataColumnInfo[columns.length];
                            for (int i = 0; i < nCols.length; i++) {
                                nCols[i] = (SGNetCDFDataColumnInfo) columns[i];
                            }
                            SGDataUtility.updatePickupParameters(infoMap, nCols);
                        }
                        if (startObj != null) {
                            infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_START, startObj);
                        }
                        if (endObj != null) {
                            infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_END, endObj);
                        }
                        if (stepObj != null) {
                            infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_STEP, stepObj);
                        }
                    }
                }
            } else if (SGDataUtility.isMDArrayData(dataType)) {
				Map<String, Integer> pickUpDimMap = new HashMap<String, Integer>();
            	for (int ii = 0; ii < columns.length; ii++) {
            		SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
            		String columnType = mdInfo.getColumnType();
            		if (X_VALUE.equals(columnType) || Y_VALUE.equals(columnType)) {
                		String name = mdInfo.getName();
                		Integer dimensionIndex = mdInfo.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
                		if (dimensionIndex != null && dimensionIndex != -1) {
                			pickUpDimMap.put(name, dimensionIndex);
                		}
            		}
            	}

            	// updates indices
                if (pickUpDimMap.size() > 0) {
                	Entry<String, Integer> firstEntry = pickUpDimMap.entrySet().iterator().next();
                	String pickedUpName = firstEntry.getKey();
                	Integer pickedUpDimension = firstEntry.getValue();
                	
                    Object pickedUpIndices = pfInfoMap.get(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
                	if (pickedUpDimension == null || pickedUpIndices == null) {
                		return di;
                	}
            		Integer nDimension = SGUtilityText.getInteger(pickedUpDimension.toString());
            		if (nDimension == null) {
            			return di;
            		}
            		SGMDArrayDataColumnInfo pickedUpCol = (SGMDArrayDataColumnInfo) SGDataUtility.findColumnWithName(
            				columns, pickedUpName);
            		final int[] dims = pickedUpCol.getDimensions();
            		if (nDimension < 0 || dims.length <= nDimension) {
            			return di;
            		}
            		final int num = dims[nDimension];
                	String str = pickedUpIndices.toString();
                	SGIntegerSeriesSet indices = SGIntegerSeriesSet.parse(str, num);
                	if (indices != null) {
                    	infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, indices);
                	}
                }
                
                String mapKey = SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP;
                infoMap.put(mapKey, pfInfoMap.get(mapKey));

            	for (int jj = 0; jj < columns.length; jj++) {
            		SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[jj];
            		String columnType = mdInfo.getColumnType();
            		if (X_VALUE.equalsIgnoreCase(columnType) || Y_VALUE.equalsIgnoreCase(columnType)) {
        				arrayLength = mdInfo.getGenericDimensionLength();
        				break;
            		}
            	}
            }

            // adjusts the stride
            if (arrayLength != -1) {
            	adjustSXYDataStride(infoMap, SGIDataInformationKeyConstants.KEY_SXY_STRIDE, 
            			arrayLength);
            }
            if (indexLength != -1) {
            	adjustSXYDataStride(infoMap, SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE, 
            			indexLength);
            }

        } else if (SGDataUtility.isSXYZTypeData(dataType)) {
        	SGDataColumnInfo[] columns = colInfoSet.getDataColumnInfoArray();
        	int xLength = -1;
        	int yLength = -1;
        	int indexLength = -1;
            if (SGDataUtility.isSDArrayData(dataType)) {
            	if (columns.length == 0) {
            		return di;
            	}
            	SGSDArrayDataColumnInfo sdCol = (SGSDArrayDataColumnInfo) columns[0]; 
            	indexLength = sdCol.getLength();
            	if (indexLength == -1) {
            		return di;
            	}
            } else if (SGDataUtility.isNetCDFData(dataType)) {
            	for (int jj = 0; jj < columns.length; jj++) {
            		SGNetCDFDataColumnInfo ncInfo = (SGNetCDFDataColumnInfo) columns[jj];
            		String columnType = ncInfo.getColumnType();
            		if (X_VALUE.equalsIgnoreCase(columnType)
            				|| X_INDEX.equalsIgnoreCase(columnType)) {
            			if (ncInfo.isCoordinateVariable()) {
            				xLength = ncInfo.getDimension(0).getLength();
            			}
            		} else if (Y_VALUE.equalsIgnoreCase(columnType)
            				|| Y_INDEX.equalsIgnoreCase(columnType)) {
            			if (ncInfo.isCoordinateVariable()) {
            				yLength = ncInfo.getDimension(0).getLength();
            			}
            		} else if (INDEX.equalsIgnoreCase(columnType)) {
            			if (ncInfo.isCoordinateVariable()) {
            				indexLength = ncInfo.getDimension(0).getLength();
            			}
            		}
            	}
            } else if (SGDataUtility.isMDArrayData(dataType)) {
        		SGMDArrayDataColumnInfo zInfo = null;
            	for (int jj = 0; jj < columns.length; jj++) {
            		SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[jj];
            		String columnType = mdInfo.getColumnType();
            		if (Z_VALUE.equalsIgnoreCase(columnType)) {
            			zInfo = mdInfo;
            			break;
            		}
            	}
            	if (zInfo == null) {
            		return di;
            	}
            	int[] dims = zInfo.getDimensions();
            	if (xLength == -1) {
            		Integer xIndex = zInfo.getDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION);
            		if (xIndex != null && xIndex != -1) {
                		xLength = dims[xIndex];
            		}
            	}
            	if (yLength == -1) {
            		Integer yIndex = zInfo.getDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION);
            		if (yIndex != null && yIndex != -1) {
                		yLength = dims[yIndex];
            		}
            	}
            	if (indexLength == -1) {
            		Integer zIndex = zInfo.getGenericDimensionIndex();
            		if (zIndex != null && zIndex != -1) {
                		indexLength = dims[zIndex];
            		}
            	}
            }

            // adjusts the stride
            if (xLength != -1) {
                SGIntegerSeriesSet xStride = (SGIntegerSeriesSet) infoMap.get(
                		SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X);
                if (xStride != null) {
                	xStride = SGUtility.createIndicesWithinRange(xStride, xLength);
                	infoMap.put(SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X, xStride);
                }
            }
            if (yLength != -1) {
                SGIntegerSeriesSet yStride = (SGIntegerSeriesSet) infoMap.get(
                		SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y);
                if (yStride != null) {
                	yStride = SGUtility.createIndicesWithinRange(yStride, yLength);
                	infoMap.put(SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y, yStride);
                }
            }
            if (indexLength != -1) {
                SGIntegerSeriesSet indexStride = (SGIntegerSeriesSet) infoMap.get(
                		SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE);
                if (indexStride != null) {
                	indexStride = SGUtility.createIndicesWithinRange(indexStride, indexLength);
                	infoMap.put(SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE, indexStride);
                }
            }
        	
        } else if (SGDataUtility.isVXYTypeData(dataType)) {
        	
        	Boolean polar = SGDataUtility.isPolar(infoMap);
        	if (polar == null) {
        		return di;
        	}
        	String fColType = SGDataUtility.getVXYFirstComponentColumnType(polar);
        	String sColType = SGDataUtility.getVXYSecondComponentColumnType(polar);
        	
        	SGDataColumnInfo[] columns = colInfoSet.getDataColumnInfoArray();
        	int xLength = -1;
        	int yLength = -1;
        	int indexLength = -1;
            if (SGDataUtility.isSDArrayData(dataType)) {
            	if (columns.length == 0) {
            		return di;
            	}
            	SGSDArrayDataColumnInfo sdCol = (SGSDArrayDataColumnInfo) columns[0]; 
            	indexLength = sdCol.getLength();
            	if (indexLength == -1) {
            		return di;
            	}
            } else if (SGDataUtility.isNetCDFData(dataType)) {
            	for (int jj = 0; jj < columns.length; jj++) {
            		SGNetCDFDataColumnInfo ncInfo = (SGNetCDFDataColumnInfo) columns[jj];
            		String columnType = ncInfo.getColumnType();
            		if (X_COORDINATE.equalsIgnoreCase(columnType)
            				|| X_INDEX.equalsIgnoreCase(columnType)) {
            			if (ncInfo.isCoordinateVariable()) {
            				xLength = ncInfo.getDimension(0).getLength();
            			}
            		} else if (Y_COORDINATE.equalsIgnoreCase(columnType)
            				|| Y_INDEX.equalsIgnoreCase(columnType)) {
            			if (ncInfo.isCoordinateVariable()) {
            				yLength = ncInfo.getDimension(0).getLength();
            			}
            		} else if (INDEX.equalsIgnoreCase(columnType)) {
            			if (ncInfo.isCoordinateVariable()) {
            				indexLength = ncInfo.getDimension(0).getLength();
            			}
            		}
            	}
            } else if (SGDataUtility.isMDArrayData(dataType)) {
        		SGMDArrayDataColumnInfo cInfo = null;
            	for (int jj = 0; jj < columns.length; jj++) {
            		SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[jj];
            		String columnType = mdInfo.getColumnType();
            		if (fColType.equalsIgnoreCase(columnType)) {
            			cInfo = mdInfo;
            			break;
            		} else if (sColType.equalsIgnoreCase(columnType)) {
            			cInfo = mdInfo;
            			break;
            		}
            	}
            	if (cInfo == null) {
            		return di;
            	}
            	int[] dims = cInfo.getDimensions();
            	if (xLength == -1) {
            		Integer xIndex = cInfo.getDimensionIndex(SGIMDArrayConstants.KEY_VXY_X_DIMENSION);
            		if (xIndex != null && xIndex != -1) {
                		xLength = dims[xIndex];
            		}
            	}
            	if (yLength == -1) {
            		Integer yIndex = cInfo.getDimensionIndex(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION);
            		if (yIndex != null && yIndex != -1) {
                		yLength = dims[yIndex];
            		}
            	}
            	if (indexLength == -1) {
            		Integer cIndex = cInfo.getGenericDimensionIndex();
            		if (cIndex != null && cIndex != -1) {
                		indexLength = dims[cIndex];
            		}
            	}
            }

            // adjusts the stride
            if (xLength != -1) {
                SGIntegerSeriesSet xStride = (SGIntegerSeriesSet) infoMap.get(
                		SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X);
                if (xStride != null) {
                	xStride = SGUtility.createIndicesWithinRange(xStride, xLength);
                	infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X, xStride);
                }
            }
            if (yLength != -1) {
                SGIntegerSeriesSet yStride = (SGIntegerSeriesSet) infoMap.get(
                		SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y);
                if (yStride != null) {
                	yStride = SGUtility.createIndicesWithinRange(yStride, yLength);
                	infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y, yStride);
                }
            }
            if (indexLength != -1) {
                SGIntegerSeriesSet indexStride = (SGIntegerSeriesSet) infoMap.get(
                		SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE);
                if (indexStride != null) {
                	indexStride = SGUtility.createIndicesWithinRange(indexStride, indexLength);
                	infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE, indexStride);
                }
            }
        }
        
        // get stride information
        infoMap.put(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE, 
        		pfInfoMap.get(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE));
        @SuppressWarnings("unchecked")
		Map<String, SGIntegerSeriesSet> strideMap = (Map<String, SGIntegerSeriesSet>) pfInfoMap.get(
        		SGIDataInformationKeyConstants.KEY_ALL_STRIDE);
        if (strideMap != null) {
            infoMap.putAll(strideMap);
        }
        
        // updates time dimension map
		@SuppressWarnings("unchecked")
		Map<String, Integer> timeDimensionMap = (Map<String, Integer>) pfInfoMap.get(
        		SGIDataInformationKeyConstants.KEY_TIME_DIMENSION_INDEX_MAP);
		if (timeDimensionMap != null) {
			infoMap.put(SGIDataInformationKeyConstants.KEY_TIME_DIMENSION_INDEX_MAP, timeDimensionMap);
		}

        return SGIConstants.SUCCESSFUL_COMPLETION;
    }

    /**
     * Creates an array of the list.
     * <p>
     * Array size is number of rows in file.
     * @param path file path
     * @return
     * @throws FileNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static List<String>[] createListArray(final String path)
    throws FileNotFoundException {

        List<String>[] listArray = null;
    	int colNum = -1;

        // read the file
    	SGBufferedFileReader reader = null;
        try {
        	reader = new SGBufferedFileReader(path);
        	BufferedReader br = reader.getBufferedReader();
            boolean isFirstLine = true;

            while (true) {
                // read a line
                final String line = SGUtilityText.readLine(br);
                if (line == null) {
                    break;
                }

                // break the string into tokens
                final List<Token> tokenList = new ArrayList<Token>();
                // line is read from data file
                if (SGUtilityText.tokenize(line, tokenList, true) == false) {
                    return null;
                }
                
                // in case of an empty line or a comment line
                if (tokenList.size() == 0) {
                	continue;
                }

                if (colNum == -1) {
                	colNum = tokenList.size();
                	
                	// create list array
                	listArray = new List[colNum];
                    for (int ii = 0; ii < listArray.length; ii++) {
                        listArray[ii] = new ArrayList<String>();
                    }
                    
                } else {
                    // check the tokens
                    if (tokenList.size() != colNum) {
                        if (tokenList.size() != 0) {
                            // format error
                            return null;
                        }
                        // only contains spaces and tabs
                        continue;
                    }
                }

                // Checks whether all tokens consist of spaces.
                boolean spacesOnly = true;
                for (int ii = 0; ii < colNum; ii++) {
                    Token token = (Token) tokenList.get(ii);
                	String str = token.getString();
                	char[] cArray = str.toCharArray();
                	for (int jj = 0; jj < cArray.length; jj++) {
                		if (!Character.isSpaceChar(cArray[jj])) {
                			spacesOnly = false;
                			break;
                		}
                	}
                	if (!spacesOnly) {
                		break;
                	}
                }
                if (spacesOnly) {
                	// Skips a line if all tokens consists of spaces.
                	continue;
                }

                // format check
                List<Integer> indexList = SGDataUtility.getColumnIndexListOfNumber(tokenList);

                if (isFirstLine) {
                    isFirstLine = false;
                    if (evaluteTitleList(indexList)) {
                        continue;
                    }
                }

                // array of the tokens
                for (int ii = 0; ii < colNum; ii++) {
                    Token token = (Token) tokenList.get(ii);
                    listArray[ii].add(token.getString());
                }
            }
        } catch (FileNotFoundException e) {
        	throw e;
        } catch (IOException ex) {
            return null;
		} finally {
            if (reader != null) {
            	reader.close();
            }
        }

		// check arrays
		if (listArray == null || listArray.length == 0) {
			return null;
		}
		for (int ii = 0; ii < listArray.length; ii++) {
			if (listArray[ii].size() == 0) {
				return null;
			}
		}

        // check array index
        final int dataLength = listArray[0].size();
        for (int ii = 1; ii < listArray.length; ii++) {
            if (listArray[ii].size() != dataLength) {
                return null;
            }
        }

        return listArray;
    }

    // check title list
    static boolean evaluteTitleList(final List<Integer> indexList) {
        boolean ret = true;
        if (indexList.size() == 0) {
            return false;
        }
        for (int ii = 0; ii < indexList.size(); ii++) {
            Integer val = (Integer) indexList.get(ii);
            if (val.intValue() != 0) {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Create an array of data column objects.
     */
    static SGDataColumn[] createColumnArray(SGDataColumnInfo[] colInfo,
    		List<String>[] listArray, Map<String, Object> infoMap, final int mode,
    		final String versionNumber) {
    	
    	// get data type
        final String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
        
        // check data length
        final int len = listArray[0].size();
        for (int ii = 1; ii < listArray.length; ii++) {
            if (listArray[ii].size() != len) {
            	return null;
            }
        }
        
        SGDataColumn[] columns = new SGDataColumn[colInfo.length];
        for (int ii = 0; ii < columns.length; ii++) {
            String title = colInfo[ii].getTitle();
            String valueType = colInfo[ii].getValueType();
            if (VALUE_TYPE_NUMBER.equals(valueType)) {
            	if (ii >= listArray.length) {
            		return null;
            	}
                List<String> valueList = listArray[ii];
                if (SGDataUtility.hasTickLabels(dataType)) {
                    for (int jj = 0; jj < len; jj++) {
                        String value = valueList.get(jj);
                        Double d = SGUtilityText.getDouble(value);
                        if (d == null) {
                            return null;
                        }
                    }
                    String[] sArray = new String[len];
                    for (int jj = 0; jj < len; jj++) {
                        sArray[jj] = valueList.get(jj);
                    }
                    columns[ii] = new SGNumberDataColumn(title, sArray);
                } else {
                    double[] dArray = new double[len];
                    for (int jj = 0; jj < len; jj++) {
                        String value = valueList.get(jj);
                        Double d = SGUtilityText.getDouble(value);
                        if (d == null) {
                            return null;
                        }
                        dArray[jj] = d.doubleValue();
                    }
                    columns[ii] = new SGNumberDataColumn(title, dArray);
                }
            } else if (VALUE_TYPE_TEXT.equals(valueType)) {
            	if (ii >= listArray.length) {
            		return null;
            	}
                List<String> valueList = listArray[ii];
                String[] sArray = new String[len];
                for (int jj = 0; jj < len; jj++) {
                    sArray[jj] = valueList.get(jj);
                }
    			// when given version number in a data set is smaller than or equal to 2.0.0, 
                // sets the modifier to data column
				if (mode == LOAD_PROPERTIES_FROM_DATA_SET
						&& SGUtility.isVersionNumberEqualOrSmallerThanPermittingEmptyString(
										versionNumber, "2.0.0")) {
					columns[ii] = new SGTextDataColumn(title, sArray, 
							new SGStringBraceModifier());
				} else {
					columns[ii] = new SGTextDataColumn(title, sArray);
				}
            } else if (VALUE_TYPE_DATE.equals(valueType)) {
            	if (ii >= listArray.length) {
            		return null;
            	}
                List<String> valueList = listArray[ii];
                SGDate[] dArray = new SGDate[len];
                for (int jj = 0; jj < len; jj++) {
                    String value = valueList.get(jj);
                    try {
                        dArray[jj] = new SGDate(value);
                    } catch (ParseException e) {
                        return null;
                    }
                }
                columns[ii] = new SGDateDataColumn(title, dArray);
            } else if (VALUE_TYPE_SAMPLING_RATE.equals(valueType)) {
                Number num = getSamplingRate(infoMap);
                if (num == null) {
                	return null;
                }
                final double samplingRate = num.doubleValue();
                columns[ii] = new SGSamplingDataColumn(samplingRate, len);
            } else {
                throw new Error("Illegal value type: " + valueType);
            }
        }
        return columns;
    }

    // Returns the sampling rate.
    static Number getSamplingRate(Map<String, Object> infoMap) {
        Object obj = infoMap.get(SGIDataInformationKeyConstants.KEY_SAMPLING_RATE);
        if (obj == null) {
        	return null;
        }
        if (!(obj instanceof Number)) {
            return null;
        }
        return (Number) obj;
    }
    
    /**
     * Identifies the data type of given file.
     * 
     * @param path
     *           the file path
     * @return identified file type
     */
    public static FILE_TYPE identifyDataFileType(final String path) {

    	// MATLAB
    	boolean matlab = true;
		try {
			SGApplicationUtility.openMAT(path);
		} catch (Exception e) {
			matlab = false;
		}
		if (matlab) {
			return FILE_TYPE.MATLAB_DATA;
		}
		
		// HDF5
    	boolean hdf5 = true;
		IHDF5Reader reader = null;
		try {
			reader = SGApplicationUtility.openHDF5(path);
        } catch (HDF5Exception e) {
        	hdf5 = false;
        } finally {
        	if (reader != null) {
        		reader.close();
        	}
        }
	    if (hdf5) {
			return FILE_TYPE.HDF5_DATA;
	    }
	    if (!hdf5) {
    		// only for Windows
	        if (SGUtility.identifyOS(OS_NAME_WINDOWS)) {
	        	if (!SGDataUtility.hasValidHDF5CharacterForWin(path)) {
			    	if (hasExtension(path, HDF5_FILE_EXTENSION_ARRAY)
			    			|| hasExtension(path, MATLAB_FILE_EXTENSION)) {
			    		return FILE_TYPE.POSSIBLY_HDF5_DATA;
			    	}
	        	}
	        }
	    }
    	
		// NetCDF
    	boolean netcdf = true;
        try {
        	if (!NetcdfFile.canOpen(path)) {
        		netcdf = false;
        	}
        } catch (IOException e) {
        	netcdf = false;
        }
        if (netcdf) {
        	// tries to open the file as a NetCDF file
        	NetcdfFile ncFile = null;
        	try {
				ncFile = SGApplicationUtility.openNetCDF(path);
			} catch (Exception e) {
	        	netcdf = false;
			} finally {
				try {
					if (ncFile != null) {
						ncFile.close();
					}
				} catch (IOException e) {
				}
			}
        }
        if (netcdf) {
        	return FILE_TYPE.NETCDF_DATA;
        }

	    // returns TXT for other type files
    	return FILE_TYPE.TXT_DATA;
    }
    
    static void setDataName(JTextField dataNameTextField, JPanel dataNamePanel, String name) {
    	if (name == null) {
    		dataNamePanel.setVisible(false);
    		dataNameTextField.setToolTipText(null);
    	} else {
    		dataNamePanel.setVisible(true);
    		dataNameTextField.setText(name);
        	dataNameTextField.setToolTipText(name);
    	}
    }

    private static void showHDF5ReadErrorMessageDialog(Window wnd, String msg, String path) {
    	SGHDF5ErrorMessagePanel p = new SGHDF5ErrorMessagePanel(msg, path);
    	SGUtility.showErrorMessageDialog(wnd, p, SGIConstants.TITLE_ERROR);
    }
    
	public static void showHDF5ReadErrorMessageDialog(Window wnd, String path) {
    	String msg = "Given file seems to be HDF5 format, but failed to open it due to unacceptable characters in the file path.";
		showHDF5ReadErrorMessageDialog(wnd, msg, path);
	}
	
	public static void showHDF5WriteErrorMessageDialog(Window wnd, String path) {
    	String msg = "Failed to export data to specified file due to unacceptable characters in the file path.";
		showHDF5ReadErrorMessageDialog(wnd, msg, path);
	}

	static char[] getUnacceptableCharacterHDF5Win(final String str) {
		List<Character> cList = new ArrayList<Character>();
		char[] cArray = str.toCharArray();
		for (int ii = 0; ii < cArray.length; ii++) {
			final char c = cArray[ii];
			if (!SGDataUtility.isAcceptableCharHDF5Wind(c)) {
				cList.add(c);
			}
		}
		char[] ret = new char[cList.size()];
		for (int ii = 0; ii < cList.size(); ii++) {
			ret[ii] = cList.get(ii);
		}
		return ret;
	}

    public static JFileChooser createFileChooser(
    		final String currentDirectory, final String currentFileName, final String defaultFileName) {
    	return createFileChooser(currentDirectory, currentFileName, (String) null,
    			(String) null, defaultFileName);
    }

    public static JFileChooser createFileChooser(
    		final String currentDirectory, final String currentFileName, 
    		final String ext, final String desc, final String defaultFileName) {
    	return createFileChooser(currentDirectory, currentFileName, new String[] { ext },
    			desc, defaultFileName);
    }
    
    public static JFileChooser createFileChooser(
    		final String currentDirectory, final String currentFileName, 
    		final String[] extArray, final String exp, final String defaultFileName) {
    	
        SGExtensionFileFilter ff = new SGExtensionFileFilter();
        SGExtensionFileFilter[] ffArray = null;
        if (extArray != null && exp != null) {
            for (String ext : extArray) {
                ff.addExtension(ext);
            }
            ff.setExplanation(exp);
            ffArray = new SGExtensionFileFilter[] { ff };
        }
        return createFileChooser(currentDirectory, currentFileName, 
        		ffArray, defaultFileName);
    }

    public static JFileChooser createFileChooser(
    		final String currentDirectory, final String currentFileName, 
    		final SGExtensionFileFilter[] ffArray, final String defaultFileName) {
    	
        final JFileChooser chooser = new SGFileChooser();
        if (ffArray != null) {
            for (SGExtensionFileFilter ff : ffArray) {
                chooser.setFileFilter(ff);
            }
        }

        // sets directory and file
        chooser.setCurrentDirectory(new File(currentDirectory));
        String fileName = null;
        if (currentFileName != null) {
            fileName = SGApplicationUtility.getPathName(currentDirectory, currentFileName);
        } else {
            fileName = defaultFileName;
        }
        chooser.setSelectedFile(new File(fileName));
        
        return chooser;
    }

    public static String getPropertyString(SGDrawingWindow wnd, 
    		SGExportParameter params, String versionString) throws IOException {
        SGPropertyFileCreator propFileCreator = new SGPropertyFileCreator();
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            propFileCreator.create(wnd, baos, params, versionString);
            return baos.toString(SGIConstants.CHAR_SET_NAME_UTF8);
        } catch (Exception e) {
            return null;
        } finally {
            if (null!=baos) {
                baos.close();
            }
        }
    }

	/**
	 * Returns a list of dropped files.
	 * @param dtde
	 *          a drop event
	 * @return  A file list. If some error occurs, returns null.
	 */
	@SuppressWarnings("unchecked")
	public static List<File> getDroppedFileList(final DropTargetDropEvent dtde) {
	    List<File> fileList = new ArrayList<File>();
	    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
	    try {
	        if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
	            Transferable trans = dtde.getTransferable();
	            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
	                List<File> list = (List<File>) trans.getTransferData(DataFlavor.javaFileListFlavor);
	                fileList.addAll(list);
	            } else if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
	                String str;
	                String ss = (String) trans
	                        .getTransferData(DataFlavor.stringFlavor);
	                StringTokenizer st = new StringTokenizer(ss, "\n");
	                while (st.hasMoreTokens()) {
	                    str = st.nextToken();
	                    File file = new File(new URI(str.trim()));
	                    fileList.add(file);
	                }
	            }
	        }
	    } catch (Exception ex) {
	        // notify drop failed
	        dtde.dropComplete(false);
	        return null;
	    }
	
	    // notify drop succeeded
	    dtde.dropComplete(true);
	
	    return fileList;
	}
}

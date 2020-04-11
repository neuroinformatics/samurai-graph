package jp.riken.brain.ni.samuraigraph.application;

import java.io.File;
import java.io.IOException;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGSXYDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGIElementGroupSetForData;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGIElementGroupSetMultipleSXY;

/**
 * A class to export a data to a file.
 *
 */
public class SGDataFileExporter extends SGFileHandler implements SGIApplicationConstants {

	public static final String DEFAULT_EXPORT_DATA_FILE_NAME = "data";
	
    /**
     * Type of the current file.
     */
    private FILE_TYPE mCurrentFileType = null;

    /**
     * Builds this object.
     */
	public SGDataFileExporter() {
		super();
		this.initFilePath();
	}
	
	/**
	 * Exports a data to a file.
	 * 
	 * @param wnd
	 *           a window
	 * @param gs
	 *           the group set
	 * @param dataName
	 *           the name of data
	 * @return the status
	 */
	public int export(final SGDrawingWindow wnd, final SGIElementGroupSetForData gs, 
			final String dataName, final String command) {
		
		// initialize the file name
		final String extension;
		final OPERATION mode;
		if (MENUCMD_EXPORT_TO_FILE.equals(command)) {
			final SGData data = gs.getData();
			final String dataType = data.getDataType();
			if (SGDataUtility.isSDArrayData(dataType)) {
				extension = CSV_FILE_EXTENSION;
			} else if (SGDataUtility.isNetCDFData(dataType)) {
				extension = NETCDF_FILE_EXTENSION;
			} else if (SGDataUtility.isHDF5FileData(dataType)) {
				extension = HDF5_FILE_EXTENSION;
			} else if (SGDataUtility.isMATLABData(dataType)) {
				extension = MATLAB_FILE_EXTENSION;
			} else {
				throw new Error("Unsupported data type: " + dataType);
			}
			mode = OPERATION.EXPORT_TO_FILE_AS_SAME_FORMAT;
		} else if (MENUCMD_EXPORT_TO_TEXT_FILE.equals(command)) {
			extension = CSV_FILE_EXTENSION;
			mode = OPERATION.EXPORT_TO_TEXT;
		} else if (MENUCMD_EXPORT_TO_NETCDF_FILE.equals(command)) {
			extension = NETCDF_FILE_EXTENSION;
			mode = OPERATION.EXPORT_TO_NETCDF;
		} else if (MENUCMD_EXPORT_TO_HDF5_FILE.equals(command)) {
			extension = HDF5_FILE_EXTENSION;
			mode = OPERATION.EXPORT_TO_HDF5;
		} else if (MENUCMD_EXPORT_TO_MATLAB_FILE.equals(command)) {
			extension = MATLAB_FILE_EXTENSION;
			mode = OPERATION.EXPORT_TO_MATLAB;
		} else {
			throw new Error("Unsupported command: " + command);
		}

		final String desc;
		final FILE_TYPE fileType;
		if (CSV_FILE_EXTENSION.equals(extension)) {
			desc = CSV_FILE_DESCRIPTION;
			fileType = FILE_TYPE.TXT_DATA;
		} else if (NETCDF_FILE_EXTENSION.equals(extension)) {
			desc = NETCDF_FILE_DESCRIPTION;
			fileType = FILE_TYPE.NETCDF_DATA;
		} else if (HDF5_FILE_EXTENSION.equals(extension)) {
			desc = HDF5_FILE_DESCRIPTION;
			fileType = FILE_TYPE.HDF5_DATA;
		} else if (MATLAB_FILE_EXTENSION.equals(extension)) {
			desc = MATLAB_FILE_DESCRIPTION;
			fileType = FILE_TYPE.MATLAB_DATA;
		} else {
			throw new Error("Unsupported File Type: " + extension);
		}
		
		SGDataExportWizardDialog dg = null;
		final boolean strideEffective = gs.hasEffectiveStride();
		final boolean editedDataValueEffective = gs.hasEditedDataValues();
		final boolean shiftVisible;
		final boolean shiftEnabled;
		if (gs instanceof SGIElementGroupSetMultipleSXY) {
			shiftVisible = true;
			shiftEnabled = ((SGIElementGroupSetMultipleSXY) gs).isDataShifted();
		} else {
			shiftVisible = false;
			shiftEnabled = false;
		}
		if (strideEffective || editedDataValueEffective || shiftEnabled) {
			// shows an option dialog
			dg = new SGDataExportWizardDialog(wnd, true);
			
			// array section
			dg.setStrideSelected(strideEffective);
			dg.setStrideComponentsEnabled(strideEffective);
			
			// edited data values
			dg.setEditedDataValueSelected(editedDataValueEffective);
			dg.setEditedDataValueComponentsEnabled(editedDataValueEffective);
			
			// shift
			dg.setShiftSelected(shiftEnabled);
			dg.setShiftComponentsVisible(shiftVisible);
			dg.setShiftComponentsEnabled(shiftEnabled);
			
			dg.pack();
			dg.setCenter(wnd);
			dg.setVisible(true);
			final int closeOption = dg.getCloseOption();
			if (closeOption == SGWizardDialog.CANCEL_OPTION) {
				return CANCEL_OPTION;
			}
		}

        // get the file name to save
		String name = SGApplicationUtility.getOutputFileName(dataName);
		if (name == null) {
			name = DEFAULT_EXPORT_DATA_FILE_NAME;
		}
        name = SGApplicationUtility.appendExtension(name, extension);
        name = SGUtility.removeEscapeChar(name);
        this.mCurrentFileName = name;

		this.mCurrentFileType = fileType;

        // select the file
        File file = null;
		try {
			file = this.selectOutputFile(wnd, extension, desc, name);
		} catch (IOException e) {
            SGUtility.showErrorMessageDialog(wnd, e.getMessage(), TITLE_ERROR);
            return ERROR_OPTION;
		}
        if (file == null) {
            return CANCEL_OPTION;
        }

        // checks the file path
        if (SGUtility.identifyOS(OS_NAME_WINDOWS)) {
    		// only for Windows
            if (HDF5_FILE_EXTENSION.equals(extension)) {
            	String path = file.getAbsolutePath();
        		if (!SGDataUtility.hasValidHDF5CharacterForWin(path)) {
        			SGApplicationUtility.showHDF5WriteErrorMessageDialog(wnd, path);
        			return ERROR_OPTION;
        		}
            }
        }
        
        SGDataBufferPolicy policy = null;
        final boolean removeInvalidValues = false;
        final boolean allDefault = true;
        final boolean editDefault = true;
        final boolean shiftDefault = false;
        final boolean all;
        final boolean edit;
        final boolean shift;
        if (dg != null) {
        	if (strideEffective) {
                all = !dg.isStrideSelected();
        	} else {
        		all = allDefault;
        	}
        	if (editedDataValueEffective) {
        		edit = dg.isEditedDataValueSelected();
        	} else {
        		edit = editDefault;
        	}
        	if (shiftEnabled) {
                shift = dg.isShiftSelected();
        	} else {
        		shift = shiftDefault;
        	}
        } else {
        	all = allDefault;
        	edit = editDefault;
        	shift = shiftDefault;
        }
        if (gs instanceof SGIElementGroupSetMultipleSXY) {
        	policy = new SGSXYDataBufferPolicy(all, removeInvalidValues, edit, shift, true);
        } else {
        	policy = new SGDataBufferPolicy(all, removeInvalidValues, edit);
        }
        
        // export to the file
		if(!gs.saveData(file, new SGExportParameter(mode), policy)) {
        	SGUtility.showErrorMessageDialog(wnd, "Failed to save the data.",
        			SGIConstants.TITLE_ERROR);
			return ERROR_OPTION;
		}
        
		return OK_OPTION;
	}
	
    public FILE_TYPE getCurrentFileType() {
    	return this.mCurrentFileType;
    }
}

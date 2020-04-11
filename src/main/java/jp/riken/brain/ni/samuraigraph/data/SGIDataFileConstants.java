package jp.riken.brain.ni.samuraigraph.data;

public interface SGIDataFileConstants {

    /**
     * Typical extension for the text file.
     */
    public static final String TEXT_FILE_EXTENSION = "txt";

    /**
     * Typical extension for the csv file.
     */
    public static final String CSV_FILE_EXTENSION = "csv";

    /**
     * Typical extension for the NetCDF file.
     */
    public static final String NETCDF_FILE_EXTENSION = "nc";

    /**
     * Array of extension for the HDF5 file.
     */
    public static String[] HDF5_FILE_EXTENSION_ARRAY = {
    	"h5", "he5", "hdf5", "hdf"
    };

    /**
     * Typical extension for the HDF5 file.
     */
    public static final String HDF5_FILE_EXTENSION = HDF5_FILE_EXTENSION_ARRAY[0];

    /**
     * Typical extension for the MATLAB file.
     */
    public static final String MATLAB_FILE_EXTENSION = "mat";

    /**
     * Description for the text file.
     */
    public static final String TEXT_FILE_DESCRIPTION = "Text File";

    /**
     * Description for the csv file.
     */
    public static final String CSV_FILE_DESCRIPTION = "Comma Separated Values File";

    /**
     * Description for the NetCDF file.
     */
    public static final String NETCDF_FILE_DESCRIPTION = "NetCDF-3 Format File";

    /**
     * Description for the HDF5 file.
     */
    public static final String HDF5_FILE_DESCRIPTION = "HDF5 Format File";

    /**
     * Description for the MATLAB file.
     */
    public static final String MATLAB_FILE_DESCRIPTION = "MATLAB Format File";

}

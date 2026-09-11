package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.data.SGDataBufferUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnInfoUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTitleUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataMiscUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataRangeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataStrideUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataTextUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataViewerUtility.*;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;
import java.io.File;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

/** Static helpers that classify data types in the application. */
public final class SGDataDataTypeUtility
    implements SGIDataColumnTypeConstants,
        SGIDataPropertyKeyConstants,
        SGINetCDFConstants,
        SGIMDArrayConstants {

  private SGDataDataTypeUtility() {}

  /**
   * Returns whether the given type of data can have tick labels.
   *
   * @param dataType the data type
   * @return true if the given type of data can have tick labels
   */
  public static boolean hasTickLabels(final String dataType) {
    return (SGDataTypeConstants.SXY_DATA.equals(dataType)
        || SGDataTypeConstants.SXY_DATE_DATA.equals(dataType));
  }

  /**
   * Returns whether the given data is of the single dimensional array type.
   *
   * @param data the data object
   * @return true if the given data is of the single dimensional array type
   */
  public static boolean isSDArrayData(final SGData data) {
    return isSDArrayData(data.getDataType());
  }

  /**
   * Returns whether the given data is of the netCDF type.
   *
   * @param data the data object
   * @return true if the given data is of the netCDF type
   */
  public static boolean isNetCDFData(final SGData data) {
    return isNetCDFData(data.getDataType());
  }

  /**
   * Returns whether given data is NetCDF-4 format.
   *
   * @param data the data
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
    } catch (Exception e) {
      if (e.getClass().getName().contains("HDF5Exception")) {
        hdf5 = false;
      } else {
        if (e instanceof RuntimeException) {
          throw (RuntimeException) e;
        }
      }
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
   * @param data the data object
   * @return true if the given data is of the HDF5 type
   */
  public static boolean isHDF5Data(final SGData data) {
    return isHDF5Data(data.getDataType());
  }

  /**
   * Returns whether the given data is of the MATLAB type.
   *
   * @param data the data object
   * @return true if the given data is of the MATLAB type
   */
  public static boolean isMATLABData(final SGData data) {
    return isMATLABData(data.getDataType());
  }

  /**
   * Returns whether the given data is of virtual multidimensional array type.
   *
   * @param data the data object
   * @return true if the given data is of virtual multidimensional array type
   */
  public static boolean isVirtualMDArrayData(final SGData data) {
    return isVirtualMDArrayData(data.getDataType());
  }

  /**
   * Returns whether the given data is of the multidimensional array type.
   *
   * @param data the data object
   * @return true if the given data is of the multidimensional array type
   */
  public static boolean isMDArrayData(final SGData data) {
    return isMDArrayData(data.getDataType());
  }

  /**
   * Returns whether the given type of data is of the single dimensional array type.
   *
   * @param dataType the data type
   * @return true if the given type of data is of the array type
   */
  public static boolean isSDArrayData(final String dataType) {
    if (dataType == null) {
      throw new IllegalArgumentException("Input data type is null.");
    }
    String[] types = {
      SGDataTypeConstants.SXY_DATA,
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
   * @param dataType the data type
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
   * @param dataType the data type
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
   * @param dataType the data type
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
   * @param dataType the data type
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
   * @param dataType the data type
   * @return true if the given type of data is of the multidimensional array type
   */
  public static boolean isMDArrayData(final String dataType) {
    return isHDF5Data(dataType) || isMATLABData(dataType) || isVirtualMDArrayData(dataType);
  }

  /**
   * Returns whether the given type of data is of HDF5 type or virtual multidimensional array type.
   *
   * @param dataType the data type
   * @return true if the given type of data is of HDF5 type or virtual multidimensional array type.
   */
  public static boolean isHDF5FileData(final String dataType) {
    return isHDF5Data(dataType) || isVirtualMDArrayData(dataType);
  }

  /**
   * Returns whether the given type of data is of the scalar-XY type. (Sampling XY data and date XY
   * data are excepted.)
   *
   * @param dataType the data type
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
    String[] types = {SGDataTypeConstants.SXY_SAMPLING_DATA, SGDataTypeConstants.SXY_DATE_DATA};
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
   * @param dataType the data type
   * @return true if the given type of data is of the vector-XY type
   */
  public static boolean isVXYTypeData(final String dataType) {
    if (dataType == null) {
      throw new IllegalArgumentException("Input data type is null.");
    }
    String[] types = {
      SGDataTypeConstants.VXY_DATA,
      SGDataTypeConstants.VXY_NETCDF_DATA,
      SGDataTypeConstants.VXY_HDF5_DATA,
      SGDataTypeConstants.VXY_MATLAB_DATA,
      SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA
    };
    return SGUtility.contains(types, dataType);
  }

  /**
   * Returns whether the given type of data is of the scalar-XYZ type.
   *
   * @param dataType the data type
   * @return true if the given type of data is of the scalar-XYZ type
   */
  public static boolean isSXYZTypeData(final String dataType) {
    if (dataType == null) {
      throw new IllegalArgumentException("Input data type is null.");
    }
    String[] types = {
      SGDataTypeConstants.SXYZ_DATA,
      SGDataTypeConstants.SXYZ_NETCDF_DATA,
      SGDataTypeConstants.SXYZ_HDF5_DATA,
      SGDataTypeConstants.SXYZ_MATLAB_DATA,
      SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA
    };
    return SGUtility.contains(types, dataType);
  }

  /**
   * Returns whether the given type of data is of the multiple type data. This method is only for
   * backward compatibility.
   *
   * @param dataType the data type
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
   * Returns whether the given type of data is of the multiple type netCDF dimension data.
   *
   * @param dataType
   * @return true if the given type of data is of the multiple type of netCDF dimension
   * @throws IllegalArgumentException if dataType is null
   */
  public static boolean isNetCDFDimensionData(final String dataType) {
    if (dataType == null) {
      throw new IllegalArgumentException("Input data type is null.");
    }
    String[] types = {SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA};
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
   * @param dataType the data type
   * @return true if the given type of data is valid
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
   * Returns whether the given type of data is of the array type.
   *
   * @param dataType the data type
   * @return true if the given type of data is of the array type
   */
  public static boolean isArrayData(final String dataType) {
    return isSDArrayData(dataType) || isNetCDFData(dataType) || isMDArrayData(dataType);
  }

  /**
   * Returns whether the given data is of the array type data.
   *
   * @param data the data
   * @return true if the given data is of the array type data
   */
  public static boolean isArrayData(final SGData data) {
    return isArrayData(data.getDataType());
  }

  /**
   * Returns whether given dimension index is valid.
   *
   * @param index dimension index
   * @return true if given dimension index is valid
   */
  public static boolean isValidDimensionIndex(final Integer index) {
    return (index != null) && (index != -1);
  }

  public static final boolean isEqualColumnType(String str1, String str2) {
    return SGUtilityText.isEqualString(str1, str2);
    // return SGUtility.equals(str1, str2);
  }

  public static final boolean columnTypeStartsWith(String str, String prefix) {
    return SGUtilityText.startsWith(str, prefix);
    // return str.startsWith(prefix);
  }

  /**
   * Returns the minimum number of number columns for given data type.
   *
   * @param dataType the data type
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
}

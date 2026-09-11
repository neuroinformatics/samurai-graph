package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGDataDataTypeUtility}. */
class SGDataDataTypeUtilityTest {

  @Test
  void hasTickLabelsRecognizesTickLabelTypes() {
    assertTrue(SGDataDataTypeUtility.hasTickLabels(SGDataTypeConstants.SXY_DATA));
    assertTrue(SGDataDataTypeUtility.hasTickLabels(SGDataTypeConstants.SXY_DATE_DATA));
    assertFalse(SGDataDataTypeUtility.hasTickLabels(SGDataTypeConstants.SXY_MULTIPLE_DATA));
  }

  @Test
  void isNetCDFDataRecognizesNetCDFTypes() {
    assertTrue(SGDataDataTypeUtility.isNetCDFData(SGDataTypeConstants.SXY_NETCDF_DATA));
    assertTrue(
        SGDataDataTypeUtility.isNetCDFData(SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA));
    assertFalse(SGDataDataTypeUtility.isNetCDFData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isHDF5DataRecognizesHDF5Types() {
    assertTrue(SGDataDataTypeUtility.isHDF5Data(SGDataTypeConstants.SXY_HDF5_DATA));
    assertFalse(SGDataDataTypeUtility.isHDF5Data(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isMATLABDataRecognizesMatlabTypes() {
    assertTrue(SGDataDataTypeUtility.isMATLABData(SGDataTypeConstants.SXY_MATLAB_DATA));
    assertFalse(SGDataDataTypeUtility.isMATLABData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isVirtualMDArrayDataRecognizesVirtualTypes() {
    assertTrue(
        SGDataDataTypeUtility.isVirtualMDArrayData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA));
    assertFalse(SGDataDataTypeUtility.isVirtualMDArrayData(SGDataTypeConstants.SXY_HDF5_DATA));
  }

  @Test
  void isMDArrayDataRecognizesHDF5MatlabAndVirtualTypes() {
    assertTrue(SGDataDataTypeUtility.isMDArrayData(SGDataTypeConstants.SXY_HDF5_DATA));
    assertTrue(SGDataDataTypeUtility.isMDArrayData(SGDataTypeConstants.SXY_MATLAB_DATA));
    assertTrue(SGDataDataTypeUtility.isMDArrayData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA));
    assertFalse(SGDataDataTypeUtility.isMDArrayData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isSDArrayDataRecognizesSingleDimensionalTypes() {
    assertTrue(SGDataDataTypeUtility.isSDArrayData(SGDataTypeConstants.SXY_DATA));
    assertFalse(SGDataDataTypeUtility.isSDArrayData(SGDataTypeConstants.SXY_NETCDF_DATA));
  }

  @Test
  void isHDF5FileDataRecognizesHDF5AndVirtualTypes() {
    assertTrue(SGDataDataTypeUtility.isHDF5FileData(SGDataTypeConstants.SXY_HDF5_DATA));
    assertTrue(SGDataDataTypeUtility.isHDF5FileData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA));
    assertFalse(SGDataDataTypeUtility.isHDF5FileData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isSXYTypeDataRecognizesSingleAndMultipleTypes() {
    assertTrue(SGDataDataTypeUtility.isSXYTypeData(SGDataTypeConstants.SXY_DATA));
    assertTrue(SGDataDataTypeUtility.isSXYTypeData(SGDataTypeConstants.SXY_MULTIPLE_DATA));
    assertTrue(SGDataDataTypeUtility.isSXYTypeData(SGDataTypeConstants.SXY_SAMPLING_DATA));
    assertFalse(SGDataDataTypeUtility.isSXYTypeData(SGDataTypeConstants.VXY_DATA));
  }

  @Test
  void isSXYTypeSingleDataRecognizesSingleTypes() {
    assertTrue(SGDataDataTypeUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_DATA));
    assertTrue(SGDataDataTypeUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_NETCDF_DATA));
    assertFalse(SGDataDataTypeUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_MULTIPLE_DATA));
  }

  @Test
  void isSXYTypeMultipleDataRecognizesMultipleTypes() {
    assertTrue(SGDataDataTypeUtility.isSXYTypeMultipleData(SGDataTypeConstants.SXY_MULTIPLE_DATA));
    assertFalse(SGDataDataTypeUtility.isSXYTypeMultipleData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isVXYTypeDataRecognizesVectorTypes() {
    assertTrue(SGDataDataTypeUtility.isVXYTypeData(SGDataTypeConstants.VXY_DATA));
    assertFalse(SGDataDataTypeUtility.isVXYTypeData(SGDataTypeConstants.SXYZ_DATA));
  }

  @Test
  void isSXYZTypeDataRecognizesScalarTypes() {
    assertTrue(SGDataDataTypeUtility.isSXYZTypeData(SGDataTypeConstants.SXYZ_DATA));
    assertFalse(SGDataDataTypeUtility.isSXYZTypeData(SGDataTypeConstants.VXY_DATA));
  }

  @Test
  void isMultipleDataRecognizesMultipleTypes() {
    assertTrue(SGDataDataTypeUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_DATA));
    assertFalse(SGDataDataTypeUtility.isMultipleData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isNetCDFDimensionDataRecognizesDimensionTypes() {
    assertTrue(
        SGDataDataTypeUtility.isNetCDFDimensionData(
            SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA));
    assertFalse(SGDataDataTypeUtility.isNetCDFDimensionData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isValidDataAcceptsKnownDataTypes() {
    assertTrue(SGDataDataTypeUtility.isValidData(SGDataTypeConstants.SXY_DATA));
    assertTrue(SGDataDataTypeUtility.isValidData(SGDataTypeConstants.SXY_NETCDF_DATA));
    assertFalse(SGDataDataTypeUtility.isValidData("UNKNOWN"));
  }

  @Test
  void isArrayDataRecognizesArrayTypes() {
    assertTrue(SGDataDataTypeUtility.isArrayData(SGDataTypeConstants.SXY_DATA));
    assertTrue(SGDataDataTypeUtility.isArrayData(SGDataTypeConstants.SXY_NETCDF_DATA));
    assertFalse(SGDataDataTypeUtility.isArrayData("UNKNOWN"));
  }

  @Test
  void isValidDimensionIndexRejectsNullAndMinusOne() {
    assertFalse(SGDataDataTypeUtility.isValidDimensionIndex(null));
    assertFalse(SGDataDataTypeUtility.isValidDimensionIndex(-1));
    assertTrue(SGDataDataTypeUtility.isValidDimensionIndex(0));
  }

  @Test
  void isEqualColumnTypeIsCaseInsensitiveAndIgnoresSeparators() {
    assertTrue(SGDataDataTypeUtility.isEqualColumnType("x_value", "X VALUE"));
    assertFalse(SGDataDataTypeUtility.isEqualColumnType("x_value", "y_value"));
  }

  @Test
  void columnTypeStartsWithMatchesPrefix() {
    assertTrue(SGDataDataTypeUtility.columnTypeStartsWith("LOWER_ERROR_VALUE", "lower"));
    assertFalse(SGDataDataTypeUtility.columnTypeStartsWith("Y_VALUE", "lower"));
  }

  @Test
  void predicatesRejectNullInput() {
    assertThrows(
        IllegalArgumentException.class, () -> SGDataDataTypeUtility.isNetCDFData((String) null));
    assertThrows(
        IllegalArgumentException.class, () -> SGDataDataTypeUtility.isHDF5Data((String) null));
    assertThrows(
        IllegalArgumentException.class, () -> SGDataDataTypeUtility.isSXYTypeData((String) null));
  }
}

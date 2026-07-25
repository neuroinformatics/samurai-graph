package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class SGDataTypeConstantsTest {

  @ParameterizedTest
  @CsvSource({
    "SXY, Scalar-XY",
    "SXY_DATE, Date-XY",
    "SXY_MULTIPLE, Scalar-XY",
    "SXY_SAMPLING, Sampling-XY",
    "VXY, Vector-XY",
    "SXYZ, Scalar-XYZ",
    "SXY_NETCDF, Scalar-XY",
    "SXY_MULTIPLE_NETCDF, Scalar-XY",
    "SXY_MULTIPLE_NETCDF_PICKUP, Scalar-XY",
    "VXY_NETCDF, Vector-XY",
    "SXYZ_NETCDF, Scalar-XYZ",
    "SXY_HDF5, Scalar-XY",
    "SXY_MULTIPLE_HDF5, Scalar-XY",
    "VXY_HDF5, Vector-XY",
    "SXYZ_HDF5, Scalar-XYZ",
    "SXY_MATLAB, Scalar-XY",
    "SXY_MULTIPLE_MATLAB, Scalar-XY",
    "VXY_MATLAB, Vector-XY",
    "SXYZ_MATLAB, Scalar-XYZ",
    "SXY_VIRTUAL_MDARRAY, Scalar-XY",
    "SXY_MULTIPLE_VIRTUAL_MDARRAY, Scalar-XY",
    "VXY_VIRTUAL_MDARRAY, Vector-XY",
    "SXYZ_VIRTUAL_MDARRAY, Scalar-XYZ",
  })
  void getLongDataTypeConstantReturnsProperMapping(String shortForm, String longForm) {
    assertEquals(longForm, SGDataTypeConstants.getLongDataTypeConstant(shortForm));
  }

  @ParameterizedTest
  @ValueSource(strings = {"UNKNOWN", "SXY_FAKE"})
  void getLongDataTypeConstantReturnsNullForUnknown(String dataType) {
    assertNull(SGDataTypeConstants.getLongDataTypeConstant(dataType));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void getLongDataTypeConstantReturnsNullForNullOrEmpty(String dataType) {
    assertNull(SGDataTypeConstants.getLongDataTypeConstant(dataType));
  }
}

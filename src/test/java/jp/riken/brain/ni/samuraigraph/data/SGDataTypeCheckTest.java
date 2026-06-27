package jp.riken.brain.ni.samuraigraph.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class SGDataTypeCheckTest {

  // -- isSDArrayData(String) --

  @Test
  void isSDArrayData_returnsTrueForSXYData() {
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXY_DATA)).isTrue();
  }

  @Test
  void isSDArrayData_returnsTrueForSXYMultipleData() {
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXY_MULTIPLE_DATA)).isTrue();
  }

  @Test
  void isSDArrayData_returnsTrueForVXYData() {
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.VXY_DATA)).isTrue();
  }

  @Test
  void isSDArrayData_returnsTrueForSXYZData() {
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXYZ_DATA)).isTrue();
  }

  @Test
  void isSDArrayData_returnsTrueForBackwardCompatibilityTypes() {
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXY_SAMPLING_DATA)).isTrue();
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXY_DATE_DATA)).isTrue();
  }

  @Test
  void isSDArrayData_returnsFalseForNonSDArrayTypes() {
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXY_NETCDF_DATA)).isFalse();
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXY_HDF5_DATA)).isFalse();
  }

  @Test
  void isSDArrayData_throwsExceptionForNull() {
    assertThatThrownBy(() -> SGDataUtility.isSDArrayData((String) null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  // -- isNetCDFData(String) --

  @Test
  void isNetCDFData_returnsTrueForNetCDFTypes() {
    assertThat(SGDataUtility.isNetCDFData(SGDataTypeConstants.SXY_NETCDF_DATA)).isTrue();
    assertThat(SGDataUtility.isNetCDFData(SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA)).isTrue();
    assertThat(SGDataUtility.isNetCDFData(SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA))
        .isTrue();
    assertThat(SGDataUtility.isNetCDFData(SGDataTypeConstants.VXY_NETCDF_DATA)).isTrue();
    assertThat(SGDataUtility.isNetCDFData(SGDataTypeConstants.SXYZ_NETCDF_DATA)).isTrue();
  }

  @Test
  void isNetCDFData_returnsFalseForNonNetCDFTypes() {
    assertThat(SGDataUtility.isNetCDFData(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isNetCDFData(SGDataTypeConstants.SXY_HDF5_DATA)).isFalse();
  }

  @Test
  void isNetCDFData_throwsExceptionForNull() {
    assertThatThrownBy(() -> SGDataUtility.isNetCDFData((String) null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  // -- isHDF5Data(String) --

  @Test
  void isHDF5Data_returnsTrueForHDF5Types() {
    assertThat(SGDataUtility.isHDF5Data(SGDataTypeConstants.SXY_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isHDF5Data(SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isHDF5Data(SGDataTypeConstants.VXY_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isHDF5Data(SGDataTypeConstants.SXYZ_HDF5_DATA)).isTrue();
  }

  @Test
  void isHDF5Data_returnsFalseForNonHDF5Types() {
    assertThat(SGDataUtility.isHDF5Data(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isHDF5Data(SGDataTypeConstants.SXY_NETCDF_DATA)).isFalse();
  }

  @Test
  void isHDF5Data_throwsExceptionForNull() {
    assertThatThrownBy(() -> SGDataUtility.isHDF5Data((String) null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  // -- isMATLABData(String) --

  @Test
  void isMATLABData_returnsTrueForMATLABTypes() {
    assertThat(SGDataUtility.isMATLABData(SGDataTypeConstants.SXY_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isMATLABData(SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isMATLABData(SGDataTypeConstants.VXY_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isMATLABData(SGDataTypeConstants.SXYZ_MATLAB_DATA)).isTrue();
  }

  @Test
  void isMATLABData_returnsFalseForNonMATLABTypes() {
    assertThat(SGDataUtility.isMATLABData(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isMATLABData(SGDataTypeConstants.SXY_HDF5_DATA)).isFalse();
  }

  @Test
  void isMATLABData_throwsExceptionForNull() {
    assertThatThrownBy(() -> SGDataUtility.isMATLABData((String) null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  // -- isVirtualMDArrayData(String) --

  @Test
  void isVirtualMDArrayData_returnsTrueForVirtualMDArrayTypes() {
    assertThat(SGDataUtility.isVirtualMDArrayData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA))
        .isTrue();
    assertThat(
            SGDataUtility.isVirtualMDArrayData(
                SGDataTypeConstants.SXY_MULTIPLE_VIRTUAL_MDARRAY_DATA))
        .isTrue();
    assertThat(SGDataUtility.isVirtualMDArrayData(SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA))
        .isTrue();
    assertThat(SGDataUtility.isVirtualMDArrayData(SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA))
        .isTrue();
  }

  @Test
  void isVirtualMDArrayData_returnsFalseForNonVirtualMDArrayTypes() {
    assertThat(SGDataUtility.isVirtualMDArrayData(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isVirtualMDArrayData(SGDataTypeConstants.SXY_HDF5_DATA)).isFalse();
  }

  @Test
  void isVirtualMDArrayData_throwsExceptionForNull() {
    assertThatThrownBy(() -> SGDataUtility.isVirtualMDArrayData((String) null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  // -- isMDArrayData(String) --

  @Test
  void isMDArrayData_returnsTrueForMDArrayTypes() {
    assertThat(SGDataUtility.isMDArrayData(SGDataTypeConstants.SXY_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isMDArrayData(SGDataTypeConstants.SXY_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isMDArrayData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA)).isTrue();
  }

  @Test
  void isMDArrayData_returnsFalseForNonMDArrayTypes() {
    assertThat(SGDataUtility.isMDArrayData(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isMDArrayData(SGDataTypeConstants.SXY_NETCDF_DATA)).isFalse();
  }

  // -- isHDF5FileData(String) --

  @Test
  void isHDF5FileData_returnsTrueForHDF5AndVirtualMDArrayTypes() {
    assertThat(SGDataUtility.isHDF5FileData(SGDataTypeConstants.SXY_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isHDF5FileData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA)).isTrue();
  }

  @Test
  void isHDF5FileData_returnsFalseForNonHDF5FileTypes() {
    assertThat(SGDataUtility.isHDF5FileData(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isHDF5FileData(SGDataTypeConstants.SXY_MATLAB_DATA)).isFalse();
  }

  // -- isSXYTypeData(String) --

  @Test
  void isSXYTypeData_returnsTrueForSXYTypes() {
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_MULTIPLE_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_NETCDF_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_SAMPLING_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_DATE_DATA)).isTrue();
  }

  @Test
  void isSXYTypeData_returnsFalseForNonSXYTypes() {
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.VXY_DATA)).isFalse();
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXYZ_DATA)).isFalse();
  }

  @Test
  void isSXYTypeData_throwsExceptionForNull() {
    assertThatThrownBy(() -> SGDataUtility.isSXYTypeData(null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  // -- isSXYTypeSingleData(String) --

  @Test
  void isSXYTypeSingleData_returnsTrueForSingleSXYTypes() {
    assertThat(SGDataUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_NETCDF_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA))
        .isTrue();
  }

  @Test
  void isSXYTypeSingleData_returnsFalseForMultipleSXYTypes() {
    assertThat(SGDataUtility.isSXYTypeSingleData(SGDataTypeConstants.SXY_MULTIPLE_DATA)).isFalse();
    assertThat(SGDataUtility.isSXYTypeSingleData(SGDataTypeConstants.VXY_DATA)).isFalse();
  }

  // -- isSXYTypeMultipleData(String) --

  @Test
  void isSXYTypeMultipleData_returnsTrueForMultipleSXYTypes() {
    assertThat(SGDataUtility.isSXYTypeMultipleData(SGDataTypeConstants.SXY_MULTIPLE_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeMultipleData(SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA))
        .isTrue();
    assertThat(SGDataUtility.isSXYTypeMultipleData(SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA))
        .isTrue();
    assertThat(SGDataUtility.isSXYTypeMultipleData(SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA))
        .isTrue();
    assertThat(
            SGDataUtility.isSXYTypeMultipleData(
                SGDataTypeConstants.SXY_MULTIPLE_VIRTUAL_MDARRAY_DATA))
        .isTrue();
    assertThat(
            SGDataUtility.isSXYTypeMultipleData(
                SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA))
        .isTrue();
  }

  @Test
  void isSXYTypeMultipleData_returnsFalseForSingleSXYTypes() {
    assertThat(SGDataUtility.isSXYTypeMultipleData(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isSXYTypeMultipleData(SGDataTypeConstants.VXY_DATA)).isFalse();
  }

  // -- isVXYTypeData(String) --

  @Test
  void isVXYTypeData_returnsTrueForVXYTypes() {
    assertThat(SGDataUtility.isVXYTypeData(SGDataTypeConstants.VXY_DATA)).isTrue();
    assertThat(SGDataUtility.isVXYTypeData(SGDataTypeConstants.VXY_NETCDF_DATA)).isTrue();
    assertThat(SGDataUtility.isVXYTypeData(SGDataTypeConstants.VXY_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isVXYTypeData(SGDataTypeConstants.VXY_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isVXYTypeData(SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA)).isTrue();
  }

  @Test
  void isVXYTypeData_returnsFalseForNonVXYTypes() {
    assertThat(SGDataUtility.isVXYTypeData(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isVXYTypeData(SGDataTypeConstants.SXYZ_DATA)).isFalse();
  }

  // -- isSXYZTypeData(String) --

  @Test
  void isSXYZTypeData_returnsTrueForSXYZTypes() {
    assertThat(SGDataUtility.isSXYZTypeData(SGDataTypeConstants.SXYZ_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYZTypeData(SGDataTypeConstants.SXYZ_NETCDF_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYZTypeData(SGDataTypeConstants.SXYZ_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYZTypeData(SGDataTypeConstants.SXYZ_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYZTypeData(SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA))
        .isTrue();
  }

  @Test
  void isSXYZTypeData_returnsFalseForNonSXYZTypes() {
    assertThat(SGDataUtility.isSXYZTypeData(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isSXYZTypeData(SGDataTypeConstants.VXY_DATA)).isFalse();
  }

  // -- isMultipleData(String) --

  @Test
  void isMultipleData_returnsTrueForMultipleTypes() {
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_DATA)).isTrue();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA)).isTrue();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_VIRTUAL_MDARRAY_DATA))
        .isTrue();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA))
        .isTrue();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_SAMPLING_DATA)).isTrue();
  }

  @Test
  void isMultipleData_returnsFalseForSingleTypes() {
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.VXY_DATA)).isFalse();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXYZ_DATA)).isFalse();
  }

  // -- isNetCDFDimensionData(String) --

  @Test
  void isNetCDFDimensionData_returnsTrueForDimensionType() {
    assertThat(
            SGDataUtility.isNetCDFDimensionData(
                SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA))
        .isTrue();
  }

  @Test
  void isNetCDFDimensionData_returnsFalseForNonDimensionTypes() {
    assertThat(SGDataUtility.isNetCDFDimensionData(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isNetCDFDimensionData(SGDataTypeConstants.SXY_NETCDF_DATA)).isFalse();
  }

  // -- isValidData(String) --

  @Test
  void isValidData_returnsTrueForValidTypes() {
    assertThat(SGDataUtility.isValidData(SGDataTypeConstants.SXY_DATA)).isTrue();
    assertThat(SGDataUtility.isValidData(SGDataTypeConstants.VXY_DATA)).isTrue();
    assertThat(SGDataUtility.isValidData(SGDataTypeConstants.SXYZ_DATA)).isTrue();
  }

  @Test
  void isValidData_returnsFalseForInvalidTypes() {
    assertThat(SGDataUtility.isValidData("unknown_type")).isFalse();
  }

  // -- isArrayData(String) --

  @Test
  void isArrayData_returnsTrueForArrayTypes() {
    assertThat(SGDataUtility.isArrayData(SGDataTypeConstants.SXY_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isArrayData(SGDataTypeConstants.SXY_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isArrayData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA)).isTrue();
  }

  @Test
  void isArrayData_returnsFalseForNonArrayTypes() {
    assertThat(SGDataUtility.isArrayData("unknown_type")).isFalse();
  }
}

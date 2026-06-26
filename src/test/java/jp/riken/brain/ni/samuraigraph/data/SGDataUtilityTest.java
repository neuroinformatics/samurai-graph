package jp.riken.brain.ni.samuraigraph.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SGDataUtilityTest {

  // -- getMaxValue tests --

  @Test
  void getMaxValue_returnsMaxForNormalArray() {
    double[] arr = {1.0, 3.0, 2.0, 5.0, 4.0};
    assertThat(SGDataUtility.getMaxValue(arr)).isEqualTo(5.0);
  }

  @Test
  void getMaxValue_returnsMaxForSingleElement() {
    double[] arr = {42.0};
    assertThat(SGDataUtility.getMaxValue(arr)).isEqualTo(42.0);
  }

  @Test
  void getMaxValue_skipsNaNAndInfinity() {
    double[] arr = {1.0, Double.NaN, Double.POSITIVE_INFINITY, 3.0};
    assertThat(SGDataUtility.getMaxValue(arr)).isEqualTo(3.0);
  }

  @Test
  void getMaxValue_returnsNaNForAllInvalid() {
    double[] arr = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    assertTrue(Double.isNaN(SGDataUtility.getMaxValue(arr)));
  }

  @Test
  void getMaxValue_returnsNaNForEmpty() {
    double[] arr = {};
    assertTrue(Double.isNaN(SGDataUtility.getMaxValue(arr)));
  }

  @Test
  void getMaxValue_handlesNegativeValues() {
    double[] arr = {-5.0, -3.0, -1.0, -10.0};
    assertThat(SGDataUtility.getMaxValue(arr)).isEqualTo(-1.0);
  }

  // -- getMinValue tests --

  @Test
  void getMinValue_returnsMinForNormalArray() {
    double[] arr = {1.0, 3.0, 2.0, 5.0, 4.0};
    assertThat(SGDataUtility.getMinValue(arr)).isEqualTo(1.0);
  }

  @Test
  void getMinValue_returnsMinForSingleElement() {
    double[] arr = {42.0};
    assertThat(SGDataUtility.getMinValue(arr)).isEqualTo(42.0);
  }

  @Test
  void getMinValue_skipsNaNAndInfinity() {
    double[] arr = {1.0, Double.NaN, Double.NEGATIVE_INFINITY, 3.0};
    assertThat(SGDataUtility.getMinValue(arr)).isEqualTo(1.0);
  }

  @Test
  void getMinValue_returnsNaNForAllInvalid() {
    double[] arr = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    assertTrue(Double.isNaN(SGDataUtility.getMinValue(arr)));
  }

  @Test
  void getMinValue_returnsNaNForEmpty() {
    double[] arr = {};
    assertTrue(Double.isNaN(SGDataUtility.getMinValue(arr)));
  }

  @Test
  void getMinValue_handlesNegativeValues() {
    double[] arr = {-5.0, -3.0, -1.0, -10.0};
    assertThat(SGDataUtility.getMinValue(arr)).isEqualTo(-10.0);
  }

  // -- getBounds tests --

  @Test
  void getBounds_returnsCorrectRange() {
    double[] arr = {1.0, 3.0, 2.0, 5.0, 4.0};
    var bounds = SGDataUtility.getBounds(arr);
    assertThat(bounds.getMinValue()).isEqualTo(1.0);
    assertThat(bounds.getMaxValue()).isEqualTo(5.0);
  }

  @Test
  void getBounds_handlesAllInvalid() {
    double[] arr = {Double.NaN, Double.POSITIVE_INFINITY};
    var bounds = SGDataUtility.getBounds(arr);
    assertTrue(Double.isNaN(bounds.getMinValue()));
    assertTrue(Double.isNaN(bounds.getMaxValue()));
  }

  // -- Data type classification tests --

  @Test
  void isSDArrayData_returnsTrueForSXY() {
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXY_DATA)).isTrue();
  }

  @Test
  void isSDArrayData_returnsTrueForSXYMultiple() {
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXY_MULTIPLE_DATA)).isTrue();
  }

  @Test
  void isSDArrayData_returnsTrueForVXY() {
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.VXY_DATA)).isTrue();
  }

  @Test
  void isSDArrayData_returnsTrueForSXYZ() {
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXYZ_DATA)).isTrue();
  }

  @Test
  void isSDArrayData_returnsTrueForBackwardCompatTypes() {
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXY_SAMPLING_DATA)).isTrue();
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXY_DATE_DATA)).isTrue();
  }

  @Test
  void isSDArrayData_returnsFalseForNetCDF() {
    assertThat(SGDataUtility.isSDArrayData(SGDataTypeConstants.SXY_NETCDF_DATA)).isFalse();
  }

  @Test
  void isSDArrayData_throwsForNull() {
    assertThatThrownBy(() -> SGDataUtility.isSDArrayData((String) null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  // -- isNetCDFData tests --

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
  void isNetCDFData_returnsFalseForNonNetCDF() {
    assertThat(SGDataUtility.isNetCDFData(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isNetCDFData(SGDataTypeConstants.SXY_HDF5_DATA)).isFalse();
  }

  @Test
  void isNetCDFData_throwsForNull() {
    assertThatThrownBy(() -> SGDataUtility.isNetCDFData((String) null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  // -- isHDF5Data tests --

  @Test
  void isHDF5Data_returnsTrueForHDF5Types() {
    assertThat(SGDataUtility.isHDF5Data(SGDataTypeConstants.SXY_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isHDF5Data(SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isHDF5Data(SGDataTypeConstants.VXY_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isHDF5Data(SGDataTypeConstants.SXYZ_HDF5_DATA)).isTrue();
  }

  @Test
  void isHDF5Data_returnsFalseForNonHDF5() {
    assertThat(SGDataUtility.isHDF5Data(SGDataTypeConstants.SXY_DATA)).isFalse();
  }

  // -- isMATLABData tests --

  @Test
  void isMATLABData_returnsTrueForMATLABTypes() {
    assertThat(SGDataUtility.isMATLABData(SGDataTypeConstants.SXY_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isMATLABData(SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isMATLABData(SGDataTypeConstants.VXY_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isMATLABData(SGDataTypeConstants.SXYZ_MATLAB_DATA)).isTrue();
  }

  @Test
  void isMATLABData_returnsFalseForNonMATLAB() {
    assertThat(SGDataUtility.isMATLABData(SGDataTypeConstants.SXY_DATA)).isFalse();
  }

  // -- isMDArrayData tests --

  @Test
  void isMDArrayData_returnsTrueForHDF5() {
    assertThat(SGDataUtility.isMDArrayData(SGDataTypeConstants.SXY_HDF5_DATA)).isTrue();
  }

  @Test
  void isMDArrayData_returnsTrueForMATLAB() {
    assertThat(SGDataUtility.isMDArrayData(SGDataTypeConstants.SXY_MATLAB_DATA)).isTrue();
  }

  @Test
  void isMDArrayData_returnsTrueForVirtualMDArray() {
    assertThat(SGDataUtility.isMDArrayData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA)).isTrue();
  }

  @Test
  void isMDArrayData_returnsFalseForSDArray() {
    assertThat(SGDataUtility.isMDArrayData(SGDataTypeConstants.SXY_DATA)).isFalse();
  }

  // -- isVirtualMDArrayData tests --

  @Test
  void isVirtualMDArrayData_returnsTrueForVirtualTypes() {
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
  void isVirtualMDArrayData_returnsFalseForNonVirtual() {
    assertThat(SGDataUtility.isVirtualMDArrayData(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isVirtualMDArrayData(SGDataTypeConstants.SXY_HDF5_DATA)).isFalse();
  }

  // -- isSXYTypeData tests --

  @Test
  void isSXYTypeData_returnsTrueForSXYTypes() {
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_MULTIPLE_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_NETCDF_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_SAMPLING_DATA)).isTrue();
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_DATE_DATA)).isTrue();
  }

  @Test
  void isSXYTypeData_returnsFalseForNonSXY() {
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.VXY_DATA)).isFalse();
    assertThat(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXYZ_DATA)).isFalse();
  }

  // -- isVXYTypeData tests --

  @Test
  void isVXYTypeData_returnsTrueForVXYTypes() {
    assertThat(SGDataUtility.isVXYTypeData(SGDataTypeConstants.VXY_DATA)).isTrue();
    assertThat(SGDataUtility.isVXYTypeData(SGDataTypeConstants.VXY_NETCDF_DATA)).isTrue();
    assertThat(SGDataUtility.isVXYTypeData(SGDataTypeConstants.VXY_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isVXYTypeData(SGDataTypeConstants.VXY_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isVXYTypeData(SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA)).isTrue();
  }

  @Test
  void isVXYTypeData_returnsFalseForNonVXY() {
    assertThat(SGDataUtility.isVXYTypeData(SGDataTypeConstants.SXY_DATA)).isFalse();
  }

  // -- isSXYZTypeData tests --

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
  void isSXYZTypeData_returnsFalseForNonSXYZ() {
    assertThat(SGDataUtility.isSXYZTypeData(SGDataTypeConstants.SXY_DATA)).isFalse();
  }

  // -- isMultipleData tests --

  @Test
  void isMultipleData_returnsTrueForMultipleTypes() {
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_DATA)).isTrue();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_SAMPLING_DATA)).isTrue();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA)).isTrue();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA))
        .isTrue();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA)).isTrue();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA)).isTrue();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_VIRTUAL_MDARRAY_DATA))
        .isTrue();
  }

  @Test
  void isMultipleData_returnsFalseForSingleTypes() {
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isMultipleData(SGDataTypeConstants.VXY_DATA)).isFalse();
  }

  // -- isNetCDFDimensionData tests --

  @Test
  void isNetCDFDimensionData_returnsTrueForDimensionData() {
    assertThat(
            SGDataUtility.isNetCDFDimensionData(
                SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA))
        .isTrue();
  }

  @Test
  void isNetCDFDimensionData_returnsFalseForOthers() {
    assertThat(SGDataUtility.isNetCDFDimensionData(SGDataTypeConstants.SXY_NETCDF_DATA)).isFalse();
    assertThat(SGDataUtility.isNetCDFDimensionData(SGDataTypeConstants.SXY_DATA)).isFalse();
  }

  // -- hasTickLabels tests --

  @Test
  void hasTickLabels_returnsTrueForSXY() {
    assertThat(SGDataUtility.hasTickLabels(SGDataTypeConstants.SXY_DATA)).isTrue();
  }

  @Test
  void hasTickLabels_returnsTrueForSXYDate() {
    assertThat(SGDataUtility.hasTickLabels(SGDataTypeConstants.SXY_DATE_DATA)).isTrue();
  }

  @Test
  void hasTickLabels_returnsFalseForOthers() {
    assertThat(SGDataUtility.hasTickLabels(SGDataTypeConstants.VXY_DATA)).isFalse();
    assertThat(SGDataUtility.hasTickLabels(SGDataTypeConstants.SXYZ_DATA)).isFalse();
  }

  // -- isHDF5FileData tests --

  @Test
  void isHDF5FileData_returnsTrueForHDF5() {
    assertThat(SGDataUtility.isHDF5FileData(SGDataTypeConstants.SXY_HDF5_DATA)).isTrue();
  }

  @Test
  void isHDF5FileData_returnsTrueForVirtualMDArray() {
    assertThat(SGDataUtility.isHDF5FileData(SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA)).isTrue();
  }

  @Test
  void isHDF5FileData_returnsFalseForOthers() {
    assertThat(SGDataUtility.isHDF5FileData(SGDataTypeConstants.SXY_DATA)).isFalse();
    assertThat(SGDataUtility.isHDF5FileData(SGDataTypeConstants.SXY_MATLAB_DATA)).isFalse();
  }

  // -- appendColumnNo tests --

  @Test
  void appendColumnNo_appendsCorrectIndex() {
    String result = SGDataUtility.appendColumnNo("X", 0);
    assertThat(result).isEqualTo("X for No.1");
  }

  @Test
  void appendColumnNo_handlesMultipleDigits() {
    String result = SGDataUtility.appendColumnNo("Y", 10);
    assertThat(result).isEqualTo("Y for No.11");
  }

  // -- getAppendedColumnIndex tests --

  @Test
  void getAppendedColumnIndex_extractsIndex() {
    Integer result = SGDataUtility.getAppendedColumnIndex("X for No.3");
    assertThat(result).isEqualTo(2);
  }

  @Test
  void getAppendedColumnIndex_returnsNullForMissingPattern() {
    Integer result = SGDataUtility.getAppendedColumnIndex("X value");
    assertThat(result).isNull();
  }

  @Test
  void getAppendedColumnIndex_returnsNullForInvalidNumber() {
    Integer result = SGDataUtility.getAppendedColumnIndex("X for No.abc");
    assertThat(result).isNull();
  }

  // -- removeHeaderTitle / removeHeaderNo tests --

  @Test
  void removeHeaderNo_extractsNumber() {
    String result = SGDataUtility.removeHeaderNo("X for No.5");
    assertThat(result).isEqualTo("5");
  }

  @Test
  void removeHeaderNo_returnsNullWhenNoPattern() {
    String result = SGDataUtility.removeHeaderNo("X value");
    assertThat(result).isNull();
  }

  @Test
  void removeHeaderTitle_extractsTitle() {
    String result = SGDataUtility.removeHeaderTitle("X for myVariable");
    assertThat(result).isEqualTo("myVariable");
  }

  @Test
  void removeHeaderTitle_returnsRemainingText() {
    String result = SGDataUtility.removeHeaderTitle("X for No.1");
    assertThat(result).isEqualTo("No.1");
  }
}

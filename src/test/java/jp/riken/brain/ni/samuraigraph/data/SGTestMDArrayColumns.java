package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;

/** Factory for {@link SGMDArrayDataColumnInfo} objects used in unit tests. */
final class SGTestMDArrayColumns {

  private SGTestMDArrayColumns() {}

  static SGMDArrayVariable variable(final String name, final int[] dims, final int[] origins) {
    SGMDArrayVariable var =
        new SGMDArrayVariable() {
          @Override
          public int[] getDimensions() {
            return dims.clone();
          }

          @Override
          public List<SGAttribute> getAttributes() {
            return new ArrayList<SGAttribute>();
          }

          @Override
          public String getValueType() {
            return SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER;
          }

          @Override
          public boolean isNumberVariable() {
            return true;
          }

          @Override
          public double getDoubleValue(
              final int dimensionIndex, final int arrayIndex, final int[] origins) {
            return 0.0;
          }

          @Override
          public double getDoubleValue(final int index) {
            return 0.0;
          }

          @Override
          public double[] getDoubleArray(
              final int dimensionIndex, final SGIntegerSeriesSet stride, final int[] origins) {
            return new double[0];
          }

          @Override
          public double[] getDoubleArray(final int xIndex, final int yIndex, final int[] origins) {
            return new double[0];
          }

          @Override
          public double[] getDoubleArray(
              final int xIndex,
              final int yIndex,
              final SGIntegerSeriesSet xStride,
              final SGIntegerSeriesSet yStride,
              final int[] origins) {
            return new double[0];
          }

          @Override
          public double[] getDoubleArray(
              final int xIndex,
              final int yIndex,
              final SGIntegerSeries xSeries,
              final SGIntegerSeries ySeries,
              final int[] origins) {
            return new double[0];
          }

          @Override
          public String[] getStringArray(final int dimensionIndex) {
            return new String[0];
          }

          @Override
          public String[] getStringArray(
              final int dimensionIndex, final SGIntegerSeriesSet stride) {
            return new String[0];
          }

          @Override
          public String[] getStringArray(
              final int dimensionIndex, final SGIntegerSeriesSet stride, final int[] origins) {
            return new String[0];
          }

          @Override
          public String getString(
              final int dimensionIndex, final int arrayIndex, final int[] origins) {
            return null;
          }

          @Override
          public String getString(final int[] origins) {
            return null;
          }

          @Override
          public String getString(final int dimensionIndex, final int arrayIndex) {
            return null;
          }

          @Override
          protected MDArrayDataType getDataType() {
            return new MDArrayDataType(VALUE_TYPE.FLOAT);
          }

          @Override
          protected MDArrayDataType getExportFloatingNumberDataType() {
            return new MDArrayDataType(VALUE_TYPE.FLOAT);
          }
        };
    var.mName = name;
    var.mOrigins = origins;
    return var;
  }

  static SGMDArrayDataColumnInfo column(
      final String name, final int[] dims, final String columnType) {
    SGMDArrayDataColumnInfo info =
        new SGMDArrayDataColumnInfo(
            variable(name, dims, new int[dims.length]),
            name,
            SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    info.setColumnType(columnType);
    return info;
  }
}

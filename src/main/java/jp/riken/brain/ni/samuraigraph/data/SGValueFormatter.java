package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;

/** Utility class for value formatting operations extracted from SGDataUtility. */
public final class SGValueFormatter {

  private SGValueFormatter() {
    // prevents instantiation
  }

  /** Wraps a string value with double quotes. */
  public static String getTextValue(String str) {
    StringBuffer sb = new StringBuffer();
    sb.append('"');
    sb.append(str);
    sb.append('"');
    return sb.toString();
  }

  /**
   * Extracts value table from SXYZ data.
   *
   * @param data the SXYZ data
   * @param mode the export mode
   * @param policy the buffer policy
   * @return 2D object array of [x, y, z] values
   */
  public static Object[][] getValueTable(
      SGISXYZTypeData data, final SGExportParameter mode, SGDataBufferPolicy policy) {
    SGDataBuffer buf = data.getDataBuffer(policy);
    if (buf == null) {
      return null;
    }
    if (buf instanceof SGSXYZDataBuffer) {
      SGSXYZDataBuffer buffer = (SGSXYZDataBuffer) buf;
      final int len = buffer.getLength();
      double[] xValues = buffer.getXValues();
      double[] yValues = buffer.getYValues();
      double[] zValues = buffer.getZValues();
      Object[][] array = new Object[len][3];
      for (int ii = 0; ii < len; ii++) {
        array[ii][0] = xValues[ii];
        array[ii][1] = yValues[ii];
        array[ii][2] = zValues[ii];
      }
      return array;
    } else {
      return null;
    }
  }

  /**
   * Extracts value table from VXY data.
   *
   * @param data the VXY data
   * @param mode the export mode
   * @param policy the buffer policy
   * @return 2D object array of [x, y, firstComponent, secondComponent] values
   */
  public static Object[][] getValueTable(
      SGIVXYTypeData data, final SGExportParameter mode, SGDataBufferPolicy policy) {
    SGDataBuffer buf = data.getDataBuffer(policy);
    if (buf == null) {
      return null;
    }
    if (buf instanceof SGVXYDataBuffer) {
      SGVXYDataBuffer buffer = (SGVXYDataBuffer) buf;
      final int len = buffer.getLength();
      double[] xValues = buffer.getXValues();
      double[] yValues = buffer.getYValues();
      double[] fValues = buffer.getFirstComponentValues();
      double[] sValues = buffer.getSecondComponentValues();
      Object[][] array = new Object[len][4];
      for (int ii = 0; ii < len; ii++) {
        array[ii][0] = xValues[ii];
        array[ii][1] = yValues[ii];
        array[ii][2] = fValues[ii];
        array[ii][3] = sValues[ii];
      }
      return array;
    } else {
      return null;
    }
  }
}

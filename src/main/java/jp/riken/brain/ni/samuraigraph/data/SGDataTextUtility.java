package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.data.SGDataBufferUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnInfoUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTitleUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataMiscUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataRangeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataStrideUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataViewerUtility.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import ucar.nc2.Dimension;

/** Static helpers for text and name manipulation. */
public final class SGDataTextUtility
    implements SGIDataColumnTypeConstants,
        SGIDataPropertyKeyConstants,
        SGINetCDFConstants,
        SGIMDArrayConstants {

  private SGDataTextUtility() {}

  /**
   * Creates and returns a text string for the title of a dialog to setup the data.
   *
   * @param prefix the prefix for the title
   * @param dataType the data type
   * @return a text string for the title
   */
  public static String createTitleString(String prefix, String dataType) {
    StringBuilder sb = new StringBuilder();
    sb.append(prefix);
    sb.append(" (");
    String suffix = "";
    if (SGDataDataTypeUtility.isSXYTypeData(dataType)) {
      suffix = "Scalar-XY Graph";
    } else if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
      suffix = "Pseudocolor Map";
    } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
      suffix = "Vector-XY Graph";
    }
    sb.append(suffix);
    sb.append(")");
    return sb.toString();
  }

  public static String getTextValue(String str) {
    StringBuilder sb = new StringBuilder();
    sb.append('"');
    sb.append(str);
    sb.append('"');
    return sb.toString();
  }

  public static String getDimensionString(String[] dimNames) {
    StringBuilder sb = new StringBuilder();
    for (int ii = 0; ii < dimNames.length; ii++) {
      if (ii > 0) {
        sb.append(' ');
      }
      sb.append(dimNames[ii]);
    }
    return sb.toString();
  }

  public static String getDimensionString(Dimension[] dims) {
    String[] dimNames = new String[dims.length];
    for (int ii = 0; ii < dims.length; ii++) {
      dimNames[ii] = dims[ii].getShortName();
    }
    return getDimensionString(dimNames);
  }

  public static String getDimensionString(List<Dimension> dimList) {
    Dimension[] dims = dimList.toArray(new Dimension[dimList.size()]);
    return getDimensionString(dims);
  }

  /**
   * This method creates and returns valid name of dimensions, variables and attributes of NetCDF
   * data from given text string. Note: NetCDF-type dataset file contains invalid name such as
   * "id0-0/column0". This method is the major for such invalid files.
   *
   * @param str a text string
   * @return valid text string
   */
  public static String getNetCDFValidName(String str) {
    if (str == null) {
      return null;
    }
    if (str.length() == 0) {
      return null;
    }
    final char[] cArray = str.toCharArray();
    final char c0 = cArray[0];
    StringBuilder sb = new StringBuilder();
    if (!SGUtility.isAlphabetic(c0)) {
      sb.append("sg_");
    }
    for (int ii = 0; ii < cArray.length; ii++) {
      final char c = cArray[ii];
      final char cNew;
      if (SGUtility.isAlphabetic(c) || SGUtility.isDigit(c) || (c == '_')) {
        cNew = c;
      } else {
        cNew = '_';
      }
      sb.append(cNew);
    }
    return sb.toString();
  }

  static String bindVariableNamesInBracket(String[] names) {
    StringBuilder sb = new StringBuilder("{");
    for (int ii = 0; ii < names.length; ii++) {
      if (ii > 0) {
        sb.append(",");
      }
      sb.append(names[ii]);
    }
    sb.append("}");
    return sb.toString();
  }

  static String bindVariableNamesInBracket(List<String> nameList) {
    String[] names = new String[nameList.size()];
    nameList.toArray(names);
    return bindVariableNamesInBracket(names);
  }

  static String encodeString(String str) {
    byte[] bArray;
    try {
      bArray = str.getBytes(SGIConstants.CHAR_SET_NAME_UTF8);
    } catch (UnsupportedEncodingException e) {
      return null;
    }
    char[] cArray = new char[bArray.length];
    for (int ii = 0; ii < cArray.length; ii++) {
      cArray[ii] = (char) bArray[ii];
    }
    String strNew = new String(cArray);
    return strNew;
  }

  static String decodeString(String str) {
    char[] cArray = str.toCharArray();
    byte[] bArray = new byte[cArray.length];
    for (int jj = 0; jj < bArray.length; jj++) {
      bArray[jj] = (byte) cArray[jj];
    }
    String strNew;
    try {
      strNew = new String(bArray, SGIConstants.CHAR_SET_NAME_UTF8);
    } catch (UnsupportedEncodingException e) {
      return null;
    }
    return strNew;
  }

  static String appendGroupName(final String name, final String groupName) {
    StringBuilder sb = new StringBuilder();
    sb.append(groupName);
    sb.append('/');
    sb.append(name);
    return sb.toString();
  }

  static String bindVariableNamesInBracket(SGNetCDFVariable[] vars) {
    String[] names = new String[vars.length];
    for (int ii = 0; ii < vars.length; ii++) {
      names[ii] = vars[ii].getValidName();
    }
    return bindVariableNamesInBracket(names);
  }

  static String bindVariableNamesInBracket(SGMDArrayVariable[] vars) {
    String[] names = new String[vars.length];
    for (int ii = 0; ii < vars.length; ii++) {
      names[ii] = vars[ii].getName();
    }
    return bindVariableNamesInBracket(names);
  }

  /**
   * Return true if name1 and name2 belongs to a same netCDF group.
   *
   * @param name1
   * @param name2
   * @return
   */
  public static boolean isSameNetCDFGroup(final String name1, final String name2) {
    String[] str1 = name1.split("/");
    String[] str2 = name2.split("/");
    if (str1.length == str2.length) {
      for (int i = 0; i < str1.length - 1; i++) {
        if (str1[i].equals(str2[i]) == false) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}

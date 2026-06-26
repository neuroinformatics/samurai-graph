package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGCSVTokenizer.Token;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

/** Utility class for CSV parsing operations extracted from SGDataUtility. */
public final class SGCSVParser {

  private SGCSVParser() {
    // prevents instantiation
  }

  /**
   * Determine the data-type from the first line.
   *
   * @param tokenList the list of tokens of the first line
   * @param indexList the list of indices whether each token is of the number type or the text type
   * @param cList the list of candidates of data types
   * @return true if succeeded
   */
  public static boolean getDataTypeCandidateList(
      final List<Token> tokenList, final List<Integer> indexList, final List<String> cList) {

    // count the number of number type columns
    int cntNumber = 0;
    for (int ii = 0; ii < indexList.size(); ii++) {
      Integer obj = (Integer) indexList.get(ii);
      int num = obj.intValue();
      if (num == 1) {
        cntNumber++;
      }
    }

    String[] dataTypes = {
      SGDataTypeConstants.SXY_DATA,
      SGDataTypeConstants.SXY_MULTIPLE_DATA,
      SGDataTypeConstants.SXY_SAMPLING_DATA,
      SGDataTypeConstants.VXY_DATA,
      SGDataTypeConstants.SXYZ_DATA
    };
    for (int ii = 0; ii < dataTypes.length; ii++) {
      final int num = getMinimumNumberColumns(dataTypes[ii]);
      if (cntNumber >= num) {
        cList.add(dataTypes[ii]);
      }
    }
    if (cntNumber >= getMinimumNumberColumns(SGDataTypeConstants.SXY_DATE_DATA)) {
      for (int ii = 0; ii < tokenList.size(); ii++) {
        Token token = (Token) tokenList.get(ii);
        if (SGUtilityText.getDate(token.getString()) != null) {
          cList.add(SGDataTypeConstants.SXY_DATE_DATA);
          break;
        }
      }
    }

    return true;
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

  /** Returns a list of number convertible column index. */
  public static List<Integer> getColumnIndexListOfNumber(final List<Token> tokenList) {
    List<Integer> list = new ArrayList<Integer>();
    for (int ii = 0; ii < tokenList.size(); ii++) {
      Token token = (Token) tokenList.get(ii);
      final String str = token.getString();
      if (token.isDoubleQuoted()) {
        list.add(Integer.valueOf(0));
      } else {
        Double d = SGUtilityText.getDouble(str);
        if (d != null) {
          list.add(Integer.valueOf(1));
        } else {
          list.add(Integer.valueOf(0));
        }
      }
    }
    return list;
  }
}

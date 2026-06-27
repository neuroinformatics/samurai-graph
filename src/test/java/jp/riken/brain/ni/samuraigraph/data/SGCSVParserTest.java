package jp.riken.brain.ni.samuraigraph.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGCSVTokenizer.Token;
import org.junit.jupiter.api.Test;

class SGCSVParserTest {

  // -- getMinimumNumberColumns --

  @Test
  void getMinimumNumberColumns_sxy() {
    assertThat(SGCSVParser.getMinimumNumberColumns(SGDataTypeConstants.SXY_DATA)).isEqualTo(1);
  }

  @Test
  void getMinimumNumberColumns_sxyMultiple() {
    assertThat(SGCSVParser.getMinimumNumberColumns(SGDataTypeConstants.SXY_MULTIPLE_DATA))
        .isEqualTo(1);
  }

  @Test
  void getMinimumNumberColumns_sxySampling() {
    assertThat(SGCSVParser.getMinimumNumberColumns(SGDataTypeConstants.SXY_SAMPLING_DATA))
        .isEqualTo(1);
  }

  @Test
  void getMinimumNumberColumns_vxy() {
    assertThat(SGCSVParser.getMinimumNumberColumns(SGDataTypeConstants.VXY_DATA)).isEqualTo(4);
  }

  @Test
  void getMinimumNumberColumns_sxyz() {
    assertThat(SGCSVParser.getMinimumNumberColumns(SGDataTypeConstants.SXYZ_DATA)).isEqualTo(3);
  }

  @Test
  void getMinimumNumberColumns_unknown() {
    assertThat(SGCSVParser.getMinimumNumberColumns("UNKNOWN")).isEqualTo(-1);
  }

  @Test
  void getMinimumNumberColumns_empty() {
    assertThat(SGCSVParser.getMinimumNumberColumns("")).isEqualTo(-1);
  }

  // -- getColumnIndexListOfNumber --

  @Test
  void getColumnIndexListOfNumber_allNumbers() {
    List<Token> tokens =
        Arrays.asList(new Token("1.0", false), new Token("2.5", false), new Token("3", false));
    List<Integer> result = SGCSVParser.getColumnIndexListOfNumber(tokens);
    assertThat(result).containsExactly(1, 1, 1);
  }

  @Test
  void getColumnIndexListOfNumber_mixed() {
    List<Token> tokens =
        Arrays.asList(new Token("1.0", false), new Token("text", false), new Token("3", false));
    List<Integer> result = SGCSVParser.getColumnIndexListOfNumber(tokens);
    assertThat(result).containsExactly(1, 0, 1);
  }

  @Test
  void getColumnIndexListOfNumber_allText() {
    List<Token> tokens = Arrays.asList(new Token("abc", false), new Token("def", false));
    List<Integer> result = SGCSVParser.getColumnIndexListOfNumber(tokens);
    assertThat(result).containsExactly(0, 0);
  }

  @Test
  void getColumnIndexListOfNumber_doubleQuoted() {
    List<Token> tokens = Arrays.asList(new Token("1.0", true), new Token("2.5", false));
    List<Integer> result = SGCSVParser.getColumnIndexListOfNumber(tokens);
    assertThat(result).containsExactly(0, 1);
  }

  @Test
  void getColumnIndexListOfNumber_emptyList() {
    List<Token> tokens = new ArrayList<>();
    List<Integer> result = SGCSVParser.getColumnIndexListOfNumber(tokens);
    assertThat(result).isEmpty();
  }

  @Test
  void getColumnIndexListOfNumber_scientificNotation() {
    List<Token> tokens = Arrays.asList(new Token("1.5e10", false), new Token("2.5E-3", false));
    List<Integer> result = SGCSVParser.getColumnIndexListOfNumber(tokens);
    assertThat(result).containsExactly(1, 1);
  }

  @Test
  void getColumnIndexListOfNumber_negativeNumbers() {
    List<Token> tokens = Arrays.asList(new Token("-1.5", false), new Token("-2", false));
    List<Integer> result = SGCSVParser.getColumnIndexListOfNumber(tokens);
    assertThat(result).containsExactly(1, 1);
  }

  // -- getDataTypeCandidateList --

  @Test
  void getDataTypeCandidateList_minimumSXY() {
    List<Token> tokens = Arrays.asList(new Token("1.0", false), new Token("text", false));
    List<Integer> indexList = Arrays.asList(1, 0);
    List<String> candidates = new ArrayList<>();

    boolean result = SGCSVParser.getDataTypeCandidateList(tokens, indexList, candidates);

    assertThat(result).isTrue();
    assertThat(candidates).contains(SGDataTypeConstants.SXY_DATA);
    assertThat(candidates).contains(SGDataTypeConstants.SXY_MULTIPLE_DATA);
    assertThat(candidates).contains(SGDataTypeConstants.SXY_SAMPLING_DATA);
  }

  @Test
  void getDataTypeCandidateList_minimumSXYZ() {
    List<Token> tokens =
        Arrays.asList(new Token("1.0", false), new Token("2.0", false), new Token("3.0", false));
    List<Integer> indexList = Arrays.asList(1, 1, 1);
    List<String> candidates = new ArrayList<>();

    boolean result = SGCSVParser.getDataTypeCandidateList(tokens, indexList, candidates);

    assertThat(result).isTrue();
    assertThat(candidates).contains(SGDataTypeConstants.SXYZ_DATA);
  }

  @Test
  void getDataTypeCandidateList_minimumVXY() {
    List<Token> tokens =
        Arrays.asList(
            new Token("1.0", false),
            new Token("2.0", false),
            new Token("3.0", false),
            new Token("4.0", false));
    List<Integer> indexList = Arrays.asList(1, 1, 1, 1);
    List<String> candidates = new ArrayList<>();

    boolean result = SGCSVParser.getDataTypeCandidateList(tokens, indexList, candidates);

    assertThat(result).isTrue();
    assertThat(candidates).contains(SGDataTypeConstants.VXY_DATA);
  }

  @Test
  void getDataTypeCandidateList_noCandidates() {
    List<Token> tokens = Arrays.asList(new Token("text", false));
    List<Integer> indexList = Arrays.asList(0);
    List<String> candidates = new ArrayList<>();

    boolean result = SGCSVParser.getDataTypeCandidateList(tokens, indexList, candidates);

    assertThat(result).isTrue();
    assertThat(candidates).isEmpty();
  }

  @Test
  void getDataTypeCandidateList_empty() {
    List<Token> tokens = new ArrayList<>();
    List<Integer> indexList = new ArrayList<>();
    List<String> candidates = new ArrayList<>();

    boolean result = SGCSVParser.getDataTypeCandidateList(tokens, indexList, candidates);

    assertThat(result).isTrue();
    assertThat(candidates).isEmpty();
  }
}

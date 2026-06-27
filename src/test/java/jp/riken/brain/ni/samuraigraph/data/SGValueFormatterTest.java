package jp.riken.brain.ni.samuraigraph.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SGValueFormatterTest {

  // -- getTextValue --

  @Test
  void getTextValue_normal() {
    assertThat(SGValueFormatter.getTextValue("hello")).isEqualTo("\"hello\"");
  }

  @Test
  void getTextValue_empty() {
    assertThat(SGValueFormatter.getTextValue("")).isEqualTo("\"\"");
  }

  @Test
  void getTextValue_withSpaces() {
    assertThat(SGValueFormatter.getTextValue("hello world")).isEqualTo("\"hello world\"");
  }

  @Test
  void getTextValue_withQuotes() {
    assertThat(SGValueFormatter.getTextValue("say \"hi\"")).isEqualTo("\"say \"hi\"\"");
  }

  @Test
  void getTextValue_withNumbers() {
    assertThat(SGValueFormatter.getTextValue("123")).isEqualTo("\"123\"");
  }

  @Test
  void getTextValue_specialChars() {
    assertThat(SGValueFormatter.getTextValue("a,b,c")).isEqualTo("\"a,b,c\"");
  }
}

package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import jp.riken.brain.ni.samuraigraph.base.SGIStringModifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFiles;

/** Unit tests of the text variable. */
class SGTextVariableTest {

  private static class UpperCaseModifier implements SGIStringModifier {

    @Override
    public String modify(String str) {
      return str.toUpperCase();
    }
  }

  private SGNetCDFFile file;

  private ucar.nc2.Variable var;

  @BeforeEach
  void openExampleFile() throws IOException {
    this.file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    this.var = this.file.findVariable("x").getVariable();
  }

  @Test
  void constructorRejectsNullStringArray() {
    assertThrows(
        IllegalArgumentException.class, () -> new SGTextVariable(this.var, this.file, null));
  }

  @Test
  void withoutModifierNullEntriesBecomeEmptyStrings() {
    final SGTextVariable variable =
        new SGTextVariable(this.var, this.file, new String[] {"a", null});
    assertArrayEquals(new String[] {"a", ""}, variable.getStringArray());
    assertNull(variable.getModifier());
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_TEXT, variable.getValueType());
  }

  @Test
  void theModifierIsAppliedToTheEntries() {
    final UpperCaseModifier modifier = new UpperCaseModifier();
    final SGTextVariable variable =
        new SGTextVariable(this.var, this.file, new String[] {"a", null}, modifier);
    assertArrayEquals(new String[] {"A", ""}, variable.getStringArray());
    assertSame(modifier, variable.getModifier());
  }

  @Test
  void getStringArrayReturnsACopy() {
    final SGTextVariable variable = new SGTextVariable(this.var, this.file, new String[] {"a"});
    final String[] first = variable.getStringArray();
    final String[] second = variable.getStringArray();
    assertNotSame(first, second);
    assertArrayEquals(first, second);
  }
}

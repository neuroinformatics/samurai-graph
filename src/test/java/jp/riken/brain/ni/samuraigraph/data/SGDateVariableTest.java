package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFiles;

/** Unit tests of the date variable. */
class SGDateVariableTest {

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
        IllegalArgumentException.class, () -> new SGDateVariable(this.var, this.file, null));
  }

  @Test
  void validDatesProduceTheDateArrays() {
    final SGDateVariable variable =
        new SGDateVariable(this.var, this.file, new String[] {"2024-07-20", " 2024.07.21"});
    assertEquals(2, variable.getDate().length);
    assertNotNull(variable.getStringArray());
    assertEquals(2, variable.getStringArray().length);
    assertNotNull(variable.getNumberArray());
    assertEquals(2, variable.getNumberArray().length);
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_DATE, variable.getValueType());
    assertNull(variable.getModifier());
  }

  @Test
  void unparseableDatesYieldNullArrays() {
    final SGDateVariable variable =
        new SGDateVariable(this.var, this.file, new String[] {"not-a-date"});
    assertNull(variable.getDate());
    assertNull(variable.getStringArray());
    assertNull(variable.getNumberArray());
  }

  @Test
  void getDateReturnsAClone() {
    final SGDateVariable variable =
        new SGDateVariable(this.var, this.file, new String[] {"2024-07-20"});
    final SGDate[] first = variable.getDate();
    final SGDate[] second = variable.getDate();
    assertNotSame(first, second);
    assertArrayEquals(first, second);
  }
}

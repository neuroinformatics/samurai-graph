package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGIStringModifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFiles;

/** Unit tests of the netCDF text variable. */
class SGNetCDFTextVariableTest {

  private SGNetCDFFile file;

  @BeforeEach
  void openExampleFile() throws IOException {
    this.file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
  }

  @Test
  void theConstructorsStoreTheModifier() {
    final SGIStringModifier modifier = str -> str;
    final ucar.nc2.Variable var = this.file.findVariable("x").getVariable();
    final SGNetCDFTextVariable without = new SGNetCDFTextVariable(var, this.file);
    assertNull(without.getModifier());
    final SGNetCDFTextVariable with = new SGNetCDFTextVariable(var, this.file, modifier);
    assertSame(modifier, with.getModifier());
  }

  @Test
  void getValueTypeReturnsText() {
    final SGNetCDFTextVariable variable =
        new SGNetCDFTextVariable(this.file.findVariable("x").getVariable(), this.file);
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_TEXT, variable.getValueType());
  }

  @Test
  void getDimensionReturnsOnlyTheFirstDimension() {
    final SGNetCDFTextVariable variable =
        new SGNetCDFTextVariable(this.file.findVariable("x").getVariable(), this.file);
    final ucar.nc2.Variable var = this.file.findVariable("x").getVariable();
    assertSame(var.getDimensions().get(0), variable.getDimension(0));
    assertNull(variable.getDimension(1));
  }

  @Test
  void getLengthDimensionLooksUpTheStrlenDimension() {
    final Dimension strlenDim = mock(Dimension.class);
    final SGNetCDFFile mockFile = mock(SGNetCDFFile.class);
    when(mockFile.findDimension("labels_strlen")).thenReturn(strlenDim);
    final SGNetCDFTextVariable variable =
        new SGNetCDFTextVariable(mockVariable("labels"), mockFile);
    assertSame(strlenDim, variable.getLengthDimension());
  }

  @Test
  void getDimensionsRemovesTheLengthDimension() {
    final Dimension firstDim = mock(Dimension.class);
    final Dimension strlenDim = mock(Dimension.class);
    final SGNetCDFFile mockFile = mock(SGNetCDFFile.class);
    when(mockFile.findDimension("labels_strlen")).thenReturn(strlenDim);
    final SGNetCDFTextVariable variable =
        new SGNetCDFTextVariable(mockVariable("labels", firstDim, strlenDim), mockFile);
    assertEquals(List.of(firstDim), variable.getDimensions());
  }

  @Test
  void getDimensionsKeepsAllDimensionsWithoutALengthDimension() {
    final Dimension firstDim = mock(Dimension.class);
    final SGNetCDFFile mockFile = mock(SGNetCDFFile.class);
    when(mockFile.findDimension("labels_strlen")).thenReturn(null);
    final SGNetCDFTextVariable variable =
        new SGNetCDFTextVariable(mockVariable("labels", firstDim), mockFile);
    assertEquals(List.of(firstDim), variable.getDimensions());
  }

  @Test
  void theConstructorRequiresAVariableAndAFile() {
    final ucar.nc2.Variable var = this.file.findVariable("x").getVariable();
    assertThrows(IllegalArgumentException.class, () -> new SGNetCDFTextVariable(null, this.file));
    assertThrows(IllegalArgumentException.class, () -> new SGNetCDFTextVariable(var, null));
  }

  private static ucar.nc2.Variable mockVariable(String name, Dimension... dims) {
    final ucar.nc2.Variable var = mock(ucar.nc2.Variable.class);
    when(var.getShortName()).thenReturn(name);
    when(var.getDimensions()).thenReturn(com.google.common.collect.ImmutableList.copyOf(dims));
    return var;
  }
}

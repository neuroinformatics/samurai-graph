package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer;
import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFiles;

/** Unit tests for {@link SGDataFileUtility}. */
class SGDataFileUtilityTest {

  private SGNetCDFFile openExample16() throws IOException {
    return new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
  }

  private File createTempHdf5File() throws IOException {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    return path.toFile();
  }

  @Test
  void getValueTypeAttributeCarriesValueType() {
    ucar.nc2.Attribute attr =
        SGDataFileUtility.getValueTypeAttribute(SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    assertEquals(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, attr.getShortName());
    assertEquals(SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, attr.getStringValue());
  }

  @Test
  void textAndDateVariableChecksRejectNumericVariables() throws IOException {
    SGNetCDFFile file = openExample16();
    assertFalse(SGDataFileUtility.isSGTextVariable(file.findVariable("x").getVariable()));
    assertFalse(SGDataFileUtility.isSGDateVariable(file.findVariable("x").getVariable()));
  }

  @Test
  void getNetCDFDataColumnInfoBuildsColumnsForAllVariables() throws IOException {
    SGNetCDFFile file = openExample16();
    List<SGNetCDFVariable> vars = file.getVariables();
    SGNetCDFDataColumnInfo[] columns =
        SGDataFileUtility.getNetCDFDataColumnInfo(vars, new HashMap<String, Object>());
    assertNotNull(columns);
    assertEquals(vars.size(), columns.length);
    assertEquals("x", columns[0].getName());
    assertEquals(SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, columns[0].getValueType());
  }

  @Test
  void getMDArrayDataColumnInfoBuildsColumnsForAllVariables() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a, b));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayDataColumnInfo[] columns =
        SGDataFileUtility.getMDArrayDataColumnInfo(
            matFile, matFile.getVariables(), new HashMap<String, Object>());
    assertNotNull(columns);
    assertEquals(2, columns.length);
    assertEquals("a", columns[0].getName());
  }

  @Test
  void findHDF5AttributesReadsAttribute() throws Exception {
    File file = createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(file)) {
      writer.writeDoubleArray("values", new double[] {1.5, 2.5});
      writer.string().setAttr("/", "value_type", "Text");
    }
    try (IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(file)) {
      List<SGAttribute> attrs =
          SGDataFileUtility.findHDF5Attributes(reader, "/", Arrays.asList("value_type"));
      assertEquals(1, attrs.size());
      assertEquals("value_type", attrs.get(0).getName());
      assertEquals("Text", attrs.get(0).getValue(0));
    }
  }

  @Test
  void writeHDF5AttributeCopiesAttributeToAnotherFile() throws Exception {
    File src = createTempHdf5File();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(src)) {
      writer.writeDoubleArray("values", new double[] {1.5, 2.5});
      writer.string().setAttr("/", "value_type", "Text");
    }
    File dst = createTempHdf5File();
    try (IHDF5Writer dstWriter = HDF5FactoryProvider.get().open(dst)) {
      dstWriter.writeDoubleArray("values", new double[] {3.0, 4.0});
      try (IHDF5Reader srcReader = HDF5FactoryProvider.get().openForReading(src)) {
        assertTrue(
            SGDataFileUtility.writeHDF5Attribute(
                dstWriter, srcReader, "/", Arrays.asList("value_type")));
      }
    }
    try (IHDF5Reader dstReader = HDF5FactoryProvider.get().openForReading(dst)) {
      List<SGAttribute> attrs =
          SGDataFileUtility.findHDF5Attributes(dstReader, "/", Arrays.asList("value_type"));
      assertEquals(1, attrs.size());
      assertEquals("Text", attrs.get(0).getValue(0));
    }
  }

  @Test
  void canOpenNetCDFReturnsTrueForLoadedData() throws IOException {
    SGNetCDFFile file = openExample16();
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE);
    SGSXYNetCDFData data =
        new SGSXYNetCDFData(
            file,
            new SGDataSourceObserver(),
            xInfo,
            yInfo,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            true);
    assertTrue(SGDataFileUtility.canOpenNetCDF(data));
  }
}

package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFiles;

/** Unit tests for {@link SGDataMergeUtility}. */
class SGDataMergeUtilityTest {

  private static final String EXAMPLE_16 = "examples/data/Example16.nc";

  private SGNetCDFFile ncfile;

  @BeforeEach
  void openFile() throws IOException {
    this.ncfile = new SGNetCDFFile(NetcdfFiles.open(EXAMPLE_16));
  }

  private SGNetCDFDataColumnInfo xInfo(String name) {
    return SGDataFileUtility.createDataColumnInfo(
        this.ncfile.findVariable(name), SGIDataColumnTypeConstants.X_VALUE);
  }

  private SGNetCDFDataColumnInfo yInfo(String name) {
    return SGDataFileUtility.createDataColumnInfo(
        this.ncfile.findVariable(name), SGIDataColumnTypeConstants.Y_VALUE);
  }

  private SGSXYNetCDFMultipleData createBasic() {
    return new SGSXYNetCDFMultipleData(
        this.ncfile,
        new SGDataSourceObserver(),
        new SGNetCDFDataColumnInfo[] {this.xInfo("x")},
        new SGNetCDFDataColumnInfo[] {this.yInfo("height")},
        null,
        null,
        null,
        null,
        true);
  }

  @Test
  void mergeReturnsNullForInvalidInputs() {
    assertNull(SGDataMergeUtility.merge(new ArrayList<SGData>()));
    assertNull(SGDataMergeUtility.merge(Arrays.asList(mock(SGData.class))));
    assertNull(
        SGDataMergeUtility.merge(
            Arrays.asList(
                mock(SGData.class, withSettings().extraInterfaces(SGISXYTypeMultipleData.class)),
                mock(SGData.class))));
  }

  @Test
  void mergeReturnsSingleMultipleDataAsIs() {
    SGSXYNetCDFMultipleData data = this.createBasic();
    assertEquals(data, SGDataMergeUtility.merge(Arrays.asList((SGData) data)));
  }

  @Test
  void mergeCombinesMultipleDataFromSameFile() {
    SGSXYNetCDFMultipleData data1 = this.createBasic();
    SGSXYNetCDFMultipleData data2 = this.createBasic();
    SGISXYTypeMultipleData merged =
        SGDataMergeUtility.merge(Arrays.asList((SGData) data1, (SGData) data2));
    assertNotNull(merged);
    assertTrue(merged instanceof SGSXYNetCDFMultipleData);
    assertEquals(1, merged.getChildNumber());
  }

  @Test
  void mergeReturnsNullForDifferentSources() throws IOException {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    Files.delete(path);
    path.toFile().deleteOnExit();
    Files.copy(Path.of(EXAMPLE_16), path);
    SGNetCDFFile otherFile = new SGNetCDFFile(NetcdfFiles.open(path.toString()));
    SGSXYNetCDFMultipleData other =
        new SGSXYNetCDFMultipleData(
            otherFile,
            new SGDataSourceObserver(),
            new SGNetCDFDataColumnInfo[] {
              SGDataFileUtility.createDataColumnInfo(
                  otherFile.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE)
            },
            new SGNetCDFDataColumnInfo[] {
              SGDataFileUtility.createDataColumnInfo(
                  otherFile.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE)
            },
            null,
            null,
            null,
            null,
            true);
    assertNull(
        SGDataMergeUtility.merge(Arrays.asList((SGData) this.createBasic(), (SGData) other)));
  }

  private static Path createTempHdf5File() throws IOException {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    return path;
  }

  private static SGHDF5File createHDF5File(Path path) throws IOException {
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(path.toFile())) {
      writer.writeDoubleArray("x", new double[] {1.0, 2.0, 3.0});
      writer.writeDoubleArray("y1", new double[] {10.0, 20.0, 30.0});
      writer.writeDoubleArray("y2", new double[] {40.0, 50.0, 60.0});
      writer.writeDoubleArray("le", new double[] {1.0, 1.0, 1.0});
      writer.writeDoubleArray("ue", new double[] {2.0, 2.0, 2.0});
    }
    IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(path.toFile());
    return new SGHDF5File(reader);
  }

  private static SGMDArrayDataColumnInfo[] infos(SGHDF5File file, String... names) {
    SGMDArrayDataColumnInfo[] infos = new SGMDArrayDataColumnInfo[names.length];
    for (int ii = 0; ii < names.length; ii++) {
      SGMDArrayDataColumnInfo col =
          (SGMDArrayDataColumnInfo)
              SGDataFileUtility.createDataColumnInfo(file.findVariable(names[ii]), "");
      col.setGenericDimensionIndex(0);
      infos[ii] = col;
    }
    return infos;
  }

  private static SGSXYMDArrayMultipleData createMDData(SGHDF5File file) {
    return new SGSXYMDArrayMultipleData(
        file,
        new SGDataSourceObserver(),
        infos(file, "x"),
        infos(file, "y1", "y2"),
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        true);
  }

  @Test
  void mergeMDArrayReturnsNullForInvalidInputs() {
    assertNull(SGDataMergeUtility.mergeMDArray(new ArrayList<SGData>()));
    assertNull(SGDataMergeUtility.mergeMDArray(Arrays.asList(mock(SGData.class))));
    SGSXYMDArrayMultipleData md1 = mock(SGSXYMDArrayMultipleData.class);
    when(md1.isDimensionPicked()).thenReturn(true);
    SGSXYMDArrayMultipleData md2 = mock(SGSXYMDArrayMultipleData.class);
    when(md2.isDimensionPicked()).thenReturn(false);
    assertNull(SGDataMergeUtility.mergeMDArray(Arrays.asList((SGData) md1, (SGData) md2)));
  }

  @Test
  void mergeMDArrayCombinesDataFromSameFile() throws IOException {
    SGHDF5File file = createHDF5File(createTempHdf5File());
    List<SGData> dataList = new ArrayList<SGData>();
    dataList.add(this.createMDData(file));
    dataList.add(this.createMDData(file));
    SGISXYTypeMultipleData merged = SGDataMergeUtility.mergeMDArray(dataList);
    assertNotNull(merged);
    assertTrue(merged instanceof SGSXYMDArrayMultipleData);
    assertFalse(((SGSXYMDArrayMultipleData) merged).isDimensionPicked());
  }

  @Test
  void mergeMDArrayCombinesErrorBarData() throws IOException {
    SGHDF5File file = createHDF5File(createTempHdf5File());
    SGSXYMDArrayMultipleData md =
        new SGSXYMDArrayMultipleData(
            file,
            new SGDataSourceObserver(),
            infos(file, "x"),
            infos(file, "y1", "y2"),
            infos(file, "le"),
            infos(file, "ue"),
            infos(file, "y1"),
            null,
            null,
            null,
            null,
            true);
    assertTrue(md.isErrorBarAvailable());
    List<SGData> dataList = new ArrayList<SGData>();
    dataList.add(md);
    dataList.add(md);
    SGISXYTypeMultipleData merged = SGDataMergeUtility.mergeMDArray(dataList);
    assertNotNull(merged);
    assertTrue(merged instanceof SGSXYMDArrayMultipleData);
  }

  @Test
  void mergeCombinesPickedUpMultipleData() throws IOException {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    Files.delete(path);
    path.toFile().deleteOnExit();
    ucar.nc2.write.NetcdfFormatWriter.Builder writer =
        ucar.nc2.write.NetcdfFormatWriter.createNewNetcdf3(path.toString());
    ucar.nc2.Dimension xDim = writer.addDimension("x", 8);
    ucar.nc2.Dimension yDim = writer.addDimension("y", 2);
    writer.addVariable("x", ucar.ma2.DataType.FLOAT, Arrays.asList(xDim));
    writer.addVariable("v", ucar.ma2.DataType.FLOAT, Arrays.asList(yDim, xDim));
    try (ucar.nc2.write.NetcdfFormatWriter ignored = writer.build()) {}
    SGNetCDFFile file = new SGNetCDFFile(ucar.nc2.NetcdfFiles.open(path.toString()));
    SGSXYNetCDFMultipleData picked =
        new SGSXYNetCDFMultipleData(
            file,
            new SGDataSourceObserver(),
            SGDataFileUtility.createDataColumnInfo(
                file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE),
            SGDataFileUtility.createDataColumnInfo(
                file.findVariable("v"), SGIDataColumnTypeConstants.Y_VALUE),
            null,
            null,
            null,
            null,
            null,
            "y",
            new jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet(0, 1, 1),
            null,
            null,
            null,
            null,
            false);
    assertTrue(picked.isDimensionPicked());
    SGISXYTypeMultipleData merged =
        SGDataMergeUtility.merge(Arrays.asList((SGData) picked, (SGData) picked));
    assertNotNull(merged);
    assertTrue(merged instanceof SGSXYNetCDFMultipleData);
  }
}

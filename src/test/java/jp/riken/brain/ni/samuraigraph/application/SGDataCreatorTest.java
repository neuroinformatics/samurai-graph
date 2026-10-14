package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIProgressControl;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGDataTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGHDF5File;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFVariable;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGSXYMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYMDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYSDArrayData;
import org.junit.jupiter.api.Test;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.write.NetcdfFormatWriter;

/**
 * Characterization tests for {@link SGDataCreator}: the SDArray / NetCDF / MDArray factory paths,
 * file parsing helpers, data source bookkeeping and the public entry points.
 */
class SGDataCreatorTest {

  private static final String EXAMPLE_DIR = "examples/data/";

  // -----------------------------------------------------------------
  // No-op progress control
  // -----------------------------------------------------------------

  /** No-op progress control for headless data creation. */
  private static class NoopProgressControl implements SGIProgressControl {

    @Override
    public void startProgress() {}

    @Override
    public void endProgress() {}

    @Override
    public void startIndeterminateProgress() {}

    @Override
    public void setProgressValue(float ratio) {}

    @Override
    public void setProgressMessage(String msg) {}
  }

  // -----------------------------------------------------------------
  // CreatedDataSet / CreatedData
  // -----------------------------------------------------------------

  @Test
  void createdDataSetAddsAndRetrievesData() {
    SGDataCreator.CreatedDataSet set = new SGDataCreator.CreatedDataSet();
    assertEquals(0, set.getDataLength());
    assertEquals("\\_", set.getMiddleString());
    assertTrue(set.addData(new SGSXYSDArrayData()));
    assertEquals(1, set.getDataLength());
    assertNull(set.getData(0).getTitle());
    assertTrue(set.addData(new SGSXYSDArrayData(), "title"));
    assertEquals(2, set.getDataLength());
    assertEquals("title", set.getData(1).getTitle());
    assertNotNull(set.getData(0).getData());
    set.setMiddleString(" ");
    assertEquals(" ", set.getMiddleString());
  }

  @Test
  void createdDataSetGetDataRejectsOutOfRangeIndex() {
    SGDataCreator.CreatedDataSet set = new SGDataCreator.CreatedDataSet();
    assertThrows(IllegalArgumentException.class, () -> set.getData(0));
    set.addData(new SGSXYSDArrayData());
    assertThrows(IllegalArgumentException.class, () -> set.getData(-1));
    assertThrows(IllegalArgumentException.class, () -> set.getData(1));
  }

  @Test
  void createdDataRejectsNullData() {
    assertThrows(IllegalArgumentException.class, () -> new SGDataCreator.CreatedData(null, "t"));
  }

  // -----------------------------------------------------------------
  // Public create() guards
  // -----------------------------------------------------------------

  @Test
  void createReturnsNullWhenInfoMapKeysMissingForSDArray() throws Exception {
    SGDataCreator creator = new SGDataCreator();
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_DATA);
    SGDataColumnInfoSet cols =
        sset(
            scol("x", SGDataColumnTypeConstants.X_VALUE),
            scol("y", SGDataColumnTypeConstants.Y_VALUE));
    SGSDArrayFile src = openSDArray("Example1.txt", cols, infoMap);
    assertNull(creator.create(src, cols, infoMap, new NoopProgressControl()));
  }

  @Test
  void createReturnsNullForNullArgs() throws Exception {
    SGDataCreator creator = new SGDataCreator();
    SGDataColumnInfoSet cols = new SGDataColumnInfoSet(new SGDataColumnInfo[0]);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_DATA);
    assertNull(creator.create((SGIDataSource) null, cols, infoMap, new NoopProgressControl()));
    assertNull(creator.create((SGIDataSource) null, null, infoMap, new NoopProgressControl()));
    assertNull(
        creator.create(
            (SGIDataSource) null, cols, new HashMap<String, Object>(), new NoopProgressControl()));
  }

  @Test
  void createReturnsNullForUnrecognizedDataType() throws Exception {
    SGDataCreator creator = new SGDataCreator();
    SGDataColumnInfoSet cols = new SGDataColumnInfoSet(new SGDataColumnInfo[0]);
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, "UNKNOWN_TYPE");
    assertNull(creator.create((SGIDataSource) null, cols, infoMap, new NoopProgressControl()));
  }

  @Test
  void createReturnsNullForEmptyInfoMap() throws Exception {
    SGDataCreator creator = new SGDataCreator();
    SGNetCDFFile src = openNc("Example16.nc");
    SGDataColumnInfoSet cols = new SGDataColumnInfoSet(new SGDataColumnInfo[0]);
    assertNull(creator.create(src, cols, new HashMap<String, Object>(), new NoopProgressControl()));
  }

  // -----------------------------------------------------------------
  // SDArray paths
  // -----------------------------------------------------------------

  @Test
  void createsSXYDataFromTextFile() throws Exception {
    SGDataCreator creator = new SGDataCreator();
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    SGDataColumnInfoSet cols =
        sset(
            scol("x", SGDataColumnTypeConstants.X_VALUE),
            scol("y", SGDataColumnTypeConstants.Y_VALUE));
    SGSDArrayFile src = openSDArray("Example1.txt", cols, infoMap);
    SGDataCreator.CreatedDataSet set =
        creator.create(src, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    assertEquals(1, set.getDataLength());
    SGSXYSDArrayMultipleData data = (SGSXYSDArrayMultipleData) set.getData(0).getData();
    assertEquals(1, data.getChildNumber());
    SGSXYSDArrayData child = (SGSXYSDArrayData) data.getSXYDataArray()[0];
    assertEquals(101, child.getXValueArray(false).length);
    assertEquals(0.0, child.getXValueArray(false)[0], 0.0);
    assertEquals(0.0628, child.getYValueArray(false)[1], 0.0);
    assertEquals(1, creator.getDataSourceList().size());
    SGIDataSource found = creator.findDataSource(src.getPath());
    assertNotNull(found);
    assertEquals(src.getPath(), found.getPath());
  }

  @Test
  void createsSXYMultipleWithErrorBarsFromTextFile() throws Exception {
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXY_MULTIPLE_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.TRUE);
    SGDataColumnInfoSet cols =
        sset(
            scol("x", SGDataColumnTypeConstants.X_VALUE),
            scol("y", SGDataColumnTypeConstants.Y_VALUE),
            scol("le", SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for No.2"),
            scol("ue", SGDataColumnTypeConstants.UPPER_ERROR_VALUE + " for No.2"));
    SGSDArrayFile src = openSDArray("Example2.txt", cols, infoMap);
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(src, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    assertEquals(1, set.getDataLength());
    SGSXYSDArrayMultipleData data = (SGSXYSDArrayMultipleData) set.getData(0).getData();
    assertEquals(1, data.getChildNumber());
    SGSXYSDArrayData child = (SGSXYSDArrayData) data.getSXYDataArray()[0];
    assertTrue(child.isErrorBarAvailable());
    assertEquals(26, child.getXValueArray(false).length);
    assertEquals(1.0, child.getYValueArray(false)[0], 0.0);
    assertEquals(-0.1789, child.getLowerErrorValueArray(false)[0], 0.0);
    assertEquals(0.2545, child.getUpperErrorValueArray(false)[0], 0.0);
  }

  @Test
  void createsSingleXMultipleYDataFromTextFile() throws Exception {
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    SGDataColumnInfoSet cols =
        sset(
            scol("x", SGDataColumnTypeConstants.X_VALUE),
            scol("y1", SGDataColumnTypeConstants.Y_VALUE),
            scol("y2", SGDataColumnTypeConstants.Y_VALUE),
            scol("y3", SGDataColumnTypeConstants.Y_VALUE));
    SGSDArrayFile src = openSDArray("Example6.txt", cols, infoMap);
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(src, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    // single-X multiple-Y: one data per Y column, titled by the column title
    assertEquals(3, set.getDataLength());
    assertEquals("y1", set.getData(0).getTitle());
    assertEquals("y2", set.getData(1).getTitle());
    assertEquals("y3", set.getData(2).getTitle());
    SGSXYSDArrayData child =
        (SGSXYSDArrayData)
            ((SGSXYSDArrayMultipleData) set.getData(0).getData()).getSXYDataArray()[0];
    assertEquals(0.1492, child.getYValueArray(false)[1], 0.0);
  }

  @Test
  void createsSingleYMultipleXDataFromTextFile() throws Exception {
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    SGDataColumnInfoSet cols =
        sset(
            scol("x1", SGDataColumnTypeConstants.X_VALUE),
            scol("x2", SGDataColumnTypeConstants.X_VALUE),
            scol("y", SGDataColumnTypeConstants.Y_VALUE));
    SGSDArrayFile src = openSDArray("Example7.txt", cols, infoMap);
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(src, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    assertEquals(2, set.getDataLength());
    assertEquals("x1", set.getData(0).getTitle());
    assertEquals("x2", set.getData(1).getTitle());
  }

  @Test
  void createsSamplingDataWithSamplingRate() throws Exception {
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXY_SAMPLING_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    infoMap.put(SGDataInformationKeyConstants.KEY_SAMPLING_RATE, Double.valueOf(1.0e6));
    SGDataColumnInfoSet cols =
        sset(
            scol("x", SGDataColumnTypeConstants.X_VALUE),
            scol("y", SGDataColumnTypeConstants.Y_VALUE),
            scol("sr", SGDataColumnTypeConstants.VALUE_TYPE_SAMPLING_RATE, ""));
    SGSDArrayFile src = openSDArray("Example7.txt", cols, infoMap);
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(src, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    assertEquals(1, set.getDataLength());
    SGSXYSDArrayData child =
        (SGSXYSDArrayData)
            ((SGSXYSDArrayMultipleData) set.getData(0).getData()).getSXYDataArray()[0];
    assertEquals(1.0e6, child.getSamplingRate().doubleValue(), 0.0);
  }

  @Test
  void createsTickLabelDataFromTextFile() throws Exception {
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    SGDataColumnInfoSet cols =
        sset(
            scol("x", SGDataColumnTypeConstants.X_VALUE),
            scol("y", SGDataColumnTypeConstants.Y_VALUE),
            scol(
                "tl",
                SGDataColumnTypeConstants.VALUE_TYPE_TEXT,
                SGDataColumnTypeConstants.TICK_LABEL + " for No.1"));
    SGSDArrayFile src = openSDArray("Example3.txt", cols, infoMap);
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(src, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    SGSXYSDArrayData child =
        (SGSXYSDArrayData)
            ((SGSXYSDArrayMultipleData) set.getData(0).getData()).getSXYDataArray()[0];
    assertTrue(child.isTickLabelAvailable());
    assertEquals("A^0_i", child.getStringArray(false)[0]);
  }

  @Test
  void createsDateSXYDataFromTextFile() throws Exception {
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXY_DATE_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    SGDataColumnInfoSet cols =
        sset(
            scol("x", SGDataColumnTypeConstants.VALUE_TYPE_DATE, SGDataColumnTypeConstants.X_VALUE),
            scol("y", SGDataColumnTypeConstants.Y_VALUE));
    SGSDArrayFile src = openSDArray("Example8.txt", cols, infoMap);
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(src, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    SGSXYSDArrayData child =
        (SGSXYSDArrayData)
            ((SGSXYSDArrayMultipleData) set.getData(0).getData()).getSXYDataArray()[0];
    assertEquals(12, child.getXValueArray(false).length);
    assertTrue(child.getDateFlag());
  }

  @Test
  void createsVXYDataFromTextFile() throws Exception {
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.VXY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.FALSE);
    SGDataColumnInfoSet cols =
        sset(
            scol("x", SGDataColumnTypeConstants.X_COORDINATE),
            scol("y", SGDataColumnTypeConstants.Y_COORDINATE),
            scol("c1", SGDataColumnTypeConstants.X_COMPONENT),
            scol("c2", SGDataColumnTypeConstants.Y_COMPONENT));
    SGSDArrayFile src = openSDArray("Example12.txt", cols, infoMap);
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(src, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    SGVXYSDArrayData data = (SGVXYSDArrayData) set.getData(0).getData();
    assertFalse(data.isPolar());
    assertEquals(81, data.getFirstComponentValueArray(false).length);
    assertEquals(-70.71067811865474, data.getFirstComponentValueArray(false)[0], 0.0);
    assertEquals(70.71067811865476, data.getSecondComponentValueArray(false)[0], 0.0);
  }

  @Test
  void createsPolarVXYDataFromTextFile() throws Exception {
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.VXY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.TRUE);
    SGDataColumnInfoSet cols =
        sset(
            scol("x", SGDataColumnTypeConstants.X_COORDINATE),
            scol("y", SGDataColumnTypeConstants.Y_COORDINATE),
            scol("m", SGDataColumnTypeConstants.MAGNITUDE),
            scol("a", SGDataColumnTypeConstants.ANGLE));
    SGSDArrayFile src = openSDArray("Example13.txt", cols, infoMap);
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(src, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    SGVXYSDArrayData data = (SGVXYSDArrayData) set.getData(0).getData();
    assertTrue(data.isPolar());
    assertEquals(49, data.getFirstComponentValueArray(false).length);
    assertEquals(1000.0, data.getFirstComponentValueArray(false)[0], 0.0);
    assertEquals(0.7853981633974483, data.getSecondComponentValueArray(false)[0], 0.0);
  }

  @Test
  void createsSXYZDataFromTextFile() throws Exception {
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXYZ_DATA);
    SGDataColumnInfoSet cols =
        sset(
            scol("x", SGDataColumnTypeConstants.X_VALUE),
            scol("y", SGDataColumnTypeConstants.Y_VALUE),
            scol("z", SGDataColumnTypeConstants.Z_VALUE));
    SGSDArrayFile src = openSDArray("Example14.txt", cols, infoMap);
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(src, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    SGSXYZSDArrayData data = (SGSXYZSDArrayData) set.getData(0).getData();
    assertEquals(20, data.getZValueArray(false).length);
    assertEquals(3.0, data.getZValueArray(false)[2], 0.0);
  }

  @Test
  void vxySdArrayRejectsMissingPolarFlag() throws Exception {
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.VXY_DATA);
    SGDataColumnInfoSet cols =
        sset(
            scol("x", SGDataColumnTypeConstants.X_COORDINATE),
            scol("y", SGDataColumnTypeConstants.Y_COORDINATE),
            scol("c1", SGDataColumnTypeConstants.X_COMPONENT),
            scol("c2", SGDataColumnTypeConstants.Y_COMPONENT));
    SGSDArrayFile src = openSDArray("Example12.txt", cols, infoMap);
    assertNull(new SGDataCreator().create(src, cols, infoMap, new NoopProgressControl()));
  }

  // -----------------------------------------------------------------
  // Public create(DataSourceInfo, ...) path
  // -----------------------------------------------------------------

  @Test
  void createThroughDataSourceInfoPath() throws Exception {
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    String path = EXAMPLE_DIR + "Example1.txt";
    SGDataColumnInfoSet cols =
        sset(
            scol("x", SGDataColumnTypeConstants.X_VALUE),
            scol("y", SGDataColumnTypeConstants.Y_VALUE));
    SGDataCreator creator = new SGDataCreator();
    SGDataCreator.CreatedDataSet set =
        creator.create(
            new SGMainFunctions.DataSourceInfo(path),
            cols,
            infoMap,
            new NoopProgressControl(),
            null,
            -1);
    assertNotNull(set);
    assertEquals(1, set.getDataLength());
  }

  @Test
  void createThroughDataSourceInfoUsesExistingSource() throws Exception {
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXY_DATA);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    SGDataColumnInfoSet cols =
        sset(
            scol("x", SGDataColumnTypeConstants.X_VALUE),
            scol("y", SGDataColumnTypeConstants.Y_VALUE));
    SGSDArrayFile src = openSDArray("Example1.txt", cols, infoMap);
    SGDataCreator creator = new SGDataCreator();
    SGDataCreator.CreatedDataSet set =
        creator.create(
            new SGMainFunctions.DataSourceInfo(src),
            cols,
            infoMap,
            new NoopProgressControl(),
            null,
            -1);
    assertNotNull(set);
    assertEquals(1, set.getDataLength());
  }

  // -----------------------------------------------------------------
  // NetCDF paths
  // -----------------------------------------------------------------

  @Test
  void createsSXYNetCDFDataSingleVariable() throws Exception {
    SGNetCDFFile file = openNc("Example16.nc");
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.SXY_NETCDF_DATA,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE,
            Boolean.FALSE,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE,
            Boolean.TRUE);
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_VALUE),
            nci(file, "height", SGDataColumnTypeConstants.Y_VALUE));
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    assertEquals(1, set.getDataLength());
    SGSXYNetCDFMultipleData data = (SGSXYNetCDFMultipleData) set.getData(0).getData();
    assertEquals(1, data.getChildNumber());
    SGSXYNetCDFData child = (SGSXYNetCDFData) data.getSXYDataArray()[0];
    assertEquals(8, child.getXValueArray(false).length);
    assertEquals(1.0, child.getXValueArray(false)[0], 0.0);
    assertEquals(1.5, child.getYValueArray(false)[0], 0.0);
    assertEquals(7.5, child.getYValueArray(false)[3], 0.0);
  }

  @Test
  void createsSXYNetCDFDataWithErrorBars() throws Exception {
    SGNetCDFFile file = openNc("Example16.nc");
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.SXY_NETCDF_DATA,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE,
            Boolean.FALSE,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE,
            Boolean.TRUE);
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_VALUE),
            nci(file, "height", SGDataColumnTypeConstants.Y_VALUE),
            nci(file, "le", SGDataColumnTypeConstants.LOWER_ERROR_VALUE + " for height"),
            nci(file, "ue", SGDataColumnTypeConstants.UPPER_ERROR_VALUE + " for height"));
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    SGSXYNetCDFData child =
        (SGSXYNetCDFData) ((SGSXYNetCDFMultipleData) set.getData(0).getData()).getSXYDataArray()[0];
    assertTrue(child.isErrorBarAvailable());
    assertEquals(1.0, child.getLowerErrorValueArray(false)[0], 0.0);
    assertEquals(2.0, child.getUpperErrorValueArray(false)[0], 0.0);
  }

  @Test
  void createsSingleXMultipleYNetCDFData() throws Exception {
    SGNetCDFFile file = openNc("Example17.nc");
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.SXY_NETCDF_DATA,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE,
            Boolean.FALSE,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE,
            Boolean.TRUE);
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_VALUE),
            nci(file, "height", SGDataColumnTypeConstants.Y_VALUE),
            nci(file, "le", SGDataColumnTypeConstants.Y_VALUE));
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    assertEquals(2, set.getDataLength());
    assertEquals("height", set.getData(0).getTitle());
    assertEquals("le", set.getData(1).getTitle());
    SGSXYNetCDFData child =
        (SGSXYNetCDFData) ((SGSXYNetCDFMultipleData) set.getData(0).getData()).getSXYDataArray()[0];
    assertEquals(0.0, child.getYValueArray(false)[0], 0.0);
  }

  @Test
  void createsSXYNetCDFDataWithTimeAndIndex() throws Exception {
    SGNetCDFFile file = openNc("Example15.nc");
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.SXY_NETCDF_DATA,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE,
            Boolean.FALSE,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE,
            Boolean.TRUE);
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_VALUE),
            nci(file, "height", SGDataColumnTypeConstants.Y_VALUE),
            nci(file, "time", SGDataColumnTypeConstants.ANIMATION_FRAME));
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    SGSXYNetCDFData child =
        (SGSXYNetCDFData) ((SGSXYNetCDFMultipleData) set.getData(0).getData()).getSXYDataArray()[0];
    assertEquals(4, child.getAnimationLength());
    assertEquals(8, child.getXValueArray(false).length);
    assertEquals(1.5, child.getYValueArray(false)[0], 0.0);
  }

  @Test
  void createsMultipleVariableNetCDFData() throws Exception {
    SGNetCDFFile file = openNc("Example16.nc");
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE,
            Boolean.TRUE,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE,
            Boolean.TRUE);
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_VALUE),
            nci(file, "height", SGDataColumnTypeConstants.Y_VALUE),
            nci(file, "le", SGDataColumnTypeConstants.Y_VALUE));
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    assertEquals(1, set.getDataLength());
    SGSXYNetCDFMultipleData data = (SGSXYNetCDFMultipleData) set.getData(0).getData();
    assertEquals(2, data.getChildNumber());
  }

  @Test
  void createsDimensionNetCDFDataMultiple() throws Exception {
    SGNetCDFFile file = openNc("Example16.nc");
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE,
            Boolean.TRUE,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE,
            Boolean.FALSE);
    infoMap.put(
        SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, new SGIntegerSeriesSet(0, 2, 1));
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_VALUE),
            nci(file, "y", SGDataColumnTypeConstants.PICKUP),
            nci(file, "height", SGDataColumnTypeConstants.Y_VALUE));
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    assertEquals(1, set.getDataLength());
    SGSXYNetCDFMultipleData data = (SGSXYNetCDFMultipleData) set.getData(0).getData();
    assertTrue(data.isDimensionPicked());
    assertEquals("y", data.getDimensionName());
    assertEquals(3, data.getChildNumber());
    assertEquals(1.5, data.getDimensionValue(1).doubleValue(), 0.0);
  }

  @Test
  void createsDimensionNetCDFDataSinglePerIndex() throws Exception {
    SGNetCDFFile file = openNc("Example16.nc");
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE,
            Boolean.FALSE,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE,
            Boolean.FALSE);
    infoMap.put(
        SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, new SGIntegerSeriesSet(0, 2, 1));
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_VALUE),
            nci(file, "y", SGDataColumnTypeConstants.PICKUP),
            nci(file, "height", SGDataColumnTypeConstants.Y_VALUE));
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    assertEquals(3, set.getDataLength());
    assertEquals("(y=0.5)", set.getData(0).getTitle());
    assertEquals("(y=1.5)", set.getData(1).getTitle());
    assertEquals("(y=2.5)", set.getData(2).getTitle());
    assertEquals(" ", set.getMiddleString());
  }

  @Test
  void createsDimensionNetCDFDataWithStartEndStep() throws Exception {
    SGNetCDFFile file = openNc("Example16.nc");
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE,
            Boolean.TRUE,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE,
            Boolean.FALSE);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_START, Integer.valueOf(0));
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_END, Integer.valueOf(4));
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_STEP, Integer.valueOf(2));
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_VALUE),
            nci(file, "y", SGDataColumnTypeConstants.PICKUP),
            nci(file, "height", SGDataColumnTypeConstants.Y_VALUE));
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    SGSXYNetCDFMultipleData data = (SGSXYNetCDFMultipleData) set.getData(0).getData();
    assertEquals(3, data.getChildNumber());
  }

  @Test
  void dimensionNetCDFDataRequiresPickupColumn() throws Exception {
    SGNetCDFFile file = openNc("Example16.nc");
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE,
            Boolean.TRUE,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE,
            Boolean.FALSE);
    infoMap.put(
        SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, new SGIntegerSeriesSet(0, 2, 1));
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_VALUE),
            nci(file, "height", SGDataColumnTypeConstants.Y_VALUE));
    // le / ue are not balanced, so the data must be rejected.
    assertNull(new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl()));
  }

  @Test
  void netCDFSXYRejectsMissingMultipleFlags() throws Exception {
    SGNetCDFFile file = openNc("Example16.nc");
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXY_NETCDF_DATA);
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_VALUE),
            nci(file, "height", SGDataColumnTypeConstants.Y_VALUE));
    SGDataCreator creator = new SGDataCreator();
    assertNull(creator.create(file, cols, infoMap, new NoopProgressControl()));
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);
    assertNull(creator.create(file, cols, infoMap, new NoopProgressControl()));
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
    assertNotNull(creator.create(file, cols, infoMap, new NoopProgressControl()));
  }

  @Test
  void createsVXYNetCDFData() throws Exception {
    SGNetCDFFile file = writeVXYNetCDF();
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.VXY_NETCDF_DATA,
            SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED,
            Boolean.FALSE);
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_COORDINATE),
            nci(file, "y", SGDataColumnTypeConstants.Y_COORDINATE),
            nci(file, "v1", SGDataColumnTypeConstants.X_COMPONENT),
            nci(file, "v2", SGDataColumnTypeConstants.Y_COMPONENT));
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    SGVXYNetCDFData data = (SGVXYNetCDFData) set.getData(0).getData();
    assertFalse(data.isPolar());
    assertEquals("v1", data.getFirstComponentVariable().getName());
    assertEquals("v2", data.getSecondComponentVariable().getName());
    assertEquals(6, data.getFirstComponentValueArray(false).length);
    assertEquals(0.0, data.getFirstComponentValueArray(false)[0], 0.0);
    assertEquals(3.0, data.getFirstComponentValueArray(false)[3], 0.0);
  }

  @Test
  void createsSXYZNetCDFData() throws Exception {
    SGNetCDFFile file = writeSXYZNetCDF();
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXYZ_NETCDF_DATA);
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_VALUE),
            nci(file, "y", SGDataColumnTypeConstants.Y_VALUE),
            nci(file, "v", SGDataColumnTypeConstants.Z_VALUE));
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    SGSXYZNetCDFData data = (SGSXYZNetCDFData) set.getData(0).getData();
    assertEquals("v", data.getZVariable().getName());
    assertEquals(6, data.getZValueArray(false).length);
    assertEquals(0.0, data.getZValueArray(false)[0], 0.0);
  }

  @Test
  void vxyNetCDFDataRejectsMissingPolarFlag() throws Exception {
    SGNetCDFFile file = writeVXYNetCDF();
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.VXY_NETCDF_DATA);
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_COORDINATE),
            nci(file, "y", SGDataColumnTypeConstants.Y_COORDINATE),
            nci(file, "v1", SGDataColumnTypeConstants.X_COMPONENT),
            nci(file, "v2", SGDataColumnTypeConstants.Y_COMPONENT));
    assertNull(new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl()));
  }

  @Test
  void createForNetCDFDataSetInjectsDefaults() throws Exception {
    SGNetCDFFile file = openNc("Example16.nc");
    // an SDArray data type is given but the source is NetCDF; the defaults inject
    // the multiple flags so that the single-variable path is taken.
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXY_DATA);
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_VALUE),
            nci(file, "height", SGDataColumnTypeConstants.Y_VALUE));
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator()
            .createForNetCDFDataSet(
                file, SGDataTypeConstants.SXY_DATA, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    assertEquals(1, set.getDataLength());
    assertEquals(Boolean.FALSE, infoMap.get(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE));
    assertEquals(
        Boolean.TRUE, infoMap.get(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE));
  }

  @Test
  void createForNetCDFDataSetReturnsNullForInvalidArgs() throws Exception {
    SGNetCDFFile file = openNc("Example16.nc");
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXY_DATA);
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_VALUE),
            nci(file, "height", SGDataColumnTypeConstants.Y_VALUE));
    SGDataCreator creator = new SGDataCreator();
    assertNull(
        creator.createForNetCDFDataSet(
            null, SGDataTypeConstants.SXY_DATA, cols, infoMap, new NoopProgressControl()));
    assertNull(
        creator.createForNetCDFDataSet(
            file, SGDataTypeConstants.SXY_DATA, null, infoMap, new NoopProgressControl()));
    assertNull(
        creator.createForNetCDFDataSet(
            file,
            SGDataTypeConstants.SXY_DATA,
            cols,
            new HashMap<String, Object>(),
            new NoopProgressControl()));
  }

  // -----------------------------------------------------------------
  // MDArray (HDF5) paths
  // -----------------------------------------------------------------

  @Test
  void createsSingleVariableMDArrayData() throws Exception {
    SGHDF5File file = createHDF5("x,y1,y2", null);
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.SXY_HDF5_DATA,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE,
            Boolean.FALSE);
    infoMap.put(
        SGDataInformationKeyConstants.KEY_TIME_DIMENSION_INDEX_MAP,
        Map.of("x", Integer.valueOf(0)));
    SGDataColumnInfoSet cols =
        mdset(
            mdi(file, "x", SGDataColumnTypeConstants.X_VALUE),
            mdi(file, "y1", SGDataColumnTypeConstants.Y_VALUE),
            mdi(file, "y2", SGDataColumnTypeConstants.Y_VALUE));
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    assertEquals(2, set.getDataLength());
    assertEquals("y1", set.getData(0).getTitle());
    assertEquals("y2", set.getData(1).getTitle());
    SGSXYMDArrayData child =
        (SGSXYMDArrayData)
            ((SGSXYMDArrayMultipleData) set.getData(0).getData()).getSXYDataArray()[0];
    assertEquals(2.0, child.getYValueArray(false)[0], 0.0);
    assertEquals(3, child.getAnimationLength());
  }

  @Test
  void createsSingleVariableMDArrayDataWithPickupDimension() throws Exception {
    SGHDF5File file = createHDF5("xm,ym", new int[] {2, 3});
    SGMDArrayDataColumnInfo xInfo = mdi(file, "xm", SGDataColumnTypeConstants.X_VALUE);
    xInfo.setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 1);
    SGMDArrayDataColumnInfo yInfo = mdi(file, "ym", SGDataColumnTypeConstants.Y_VALUE);
    yInfo.setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 1);
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.SXY_HDF5_DATA,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE,
            Boolean.FALSE);
    infoMap.put(
        SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, new SGIntegerSeriesSet(0, 2, 2));
    infoMap.put(
        SGDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP,
        Map.of("xm", Integer.valueOf(1), "ym", Integer.valueOf(1)));
    SGDataColumnInfoSet cols = mdset(xInfo, yInfo);
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    assertEquals(2, set.getDataLength());
    assertEquals("(0)", set.getData(0).getTitle());
    assertEquals("(2)", set.getData(1).getTitle());
    assertEquals(" ", set.getMiddleString());
  }

  @Test
  void createsDimensionMDArrayDataMultiple() throws Exception {
    SGHDF5File file = createHDF5("xm,ym", new int[] {2, 3});
    SGMDArrayDataColumnInfo xInfo = mdi(file, "xm", SGDataColumnTypeConstants.X_VALUE);
    xInfo.setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 1);
    SGMDArrayDataColumnInfo yInfo = mdi(file, "ym", SGDataColumnTypeConstants.Y_VALUE);
    yInfo.setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 1);
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.SXY_HDF5_DATA,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE,
            Boolean.TRUE);
    infoMap.put(
        SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, new SGIntegerSeriesSet(0, 2, 2));
    infoMap.put(
        SGDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP,
        Map.of("xm", Integer.valueOf(1), "ym", Integer.valueOf(1)));
    SGDataColumnInfoSet cols = mdset(xInfo, yInfo);
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    assertEquals(1, set.getDataLength());
    SGSXYMDArrayMultipleData data = (SGSXYMDArrayMultipleData) set.getData(0).getData();
    assertEquals(2, data.getChildNumber());
  }

  @Test
  void createsMultipleVariableMDArrayData() throws Exception {
    SGHDF5File file = createHDF5("x,y1,y2", null);
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE,
            Boolean.TRUE);
    SGDataColumnInfoSet cols =
        mdset(
            mdi(file, "x", SGDataColumnTypeConstants.X_VALUE),
            mdi(file, "y1", SGDataColumnTypeConstants.Y_VALUE),
            mdi(file, "y2", SGDataColumnTypeConstants.Y_VALUE));
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    assertEquals(1, set.getDataLength());
    SGSXYMDArrayMultipleData data = (SGSXYMDArrayMultipleData) set.getData(0).getData();
    assertEquals(2, data.getChildNumber());
  }

  @Test
  void createsVXYMDArrayData() throws Exception {
    SGHDF5File file = createHDF5("xm,xc,yc", new int[] {2, 3});
    SGMDArrayDataColumnInfo xInfo = mdi(file, "xm", SGDataColumnTypeConstants.X_COORDINATE);
    SGMDArrayDataColumnInfo firstInfo = mdi(file, "xc", SGDataColumnTypeConstants.X_COMPONENT);
    firstInfo.setDimensionIndex(SGMDArrayConstants.KEY_VXY_X_DIMENSION, 0);
    firstInfo.setDimensionIndex(SGMDArrayConstants.KEY_VXY_Y_DIMENSION, 1);
    SGMDArrayDataColumnInfo secondInfo = mdi(file, "yc", SGDataColumnTypeConstants.Y_COMPONENT);
    secondInfo.setDimensionIndex(SGMDArrayConstants.KEY_VXY_X_DIMENSION, 0);
    secondInfo.setDimensionIndex(SGMDArrayConstants.KEY_VXY_Y_DIMENSION, 1);
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.VXY_HDF5_DATA,
            SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED,
            Boolean.FALSE);
    SGDataColumnInfoSet cols = mdset(xInfo, firstInfo, secondInfo);
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    SGVXYMDArrayData data = (SGVXYMDArrayData) set.getData(0).getData();
    assertEquals(6, data.getFirstComponentValueArray(false).length);
    assertEquals(0.0, data.getFirstComponentValueArray(false)[0], 0.0);
  }

  @Test
  void createsSXYZMDArrayData() throws Exception {
    SGHDF5File file = createHDF5("xm,ym,zm", new int[] {2, 3});
    SGMDArrayDataColumnInfo xInfo = mdi(file, "xm", SGDataColumnTypeConstants.X_VALUE);
    SGMDArrayDataColumnInfo yInfo = mdi(file, "ym", SGDataColumnTypeConstants.Y_VALUE);
    yInfo.setGenericDimensionIndex(1);
    SGMDArrayDataColumnInfo zInfo = mdi(file, "zm", SGDataColumnTypeConstants.Z_VALUE);
    zInfo.setDimensionIndex(SGMDArrayConstants.KEY_SXYZ_X_DIMENSION, 0);
    zInfo.setDimensionIndex(SGMDArrayConstants.KEY_SXYZ_Y_DIMENSION, 1);
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXYZ_HDF5_DATA);
    SGDataColumnInfoSet cols = mdset(xInfo, yInfo, zInfo);
    SGDataCreator.CreatedDataSet set =
        new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl());
    assertNotNull(set);
    SGSXYZMDArrayData data = (SGSXYZMDArrayData) set.getData(0).getData();
    assertEquals(6, data.getZValueArray(false).length);
    assertEquals(0.0, data.getZValueArray(false)[0], 0.0);
  }

  @Test
  void mdArraySXYRejectsMissingMultipleFlag() throws Exception {
    SGHDF5File file = createHDF5("x,y1", null);
    Map<String, Object> infoMap = baseInfoMap(SGDataTypeConstants.SXY_HDF5_DATA);
    SGDataColumnInfoSet cols =
        mdset(
            mdi(file, "x", SGDataColumnTypeConstants.X_VALUE),
            mdi(file, "y1", SGDataColumnTypeConstants.Y_VALUE));
    assertNull(new SGDataCreator().create(file, cols, infoMap, new NoopProgressControl()));
  }

  // -----------------------------------------------------------------
  // File parsing helpers
  // -----------------------------------------------------------------

  @Test
  void getDataTypeCandidateListFindsCandidates() throws Exception {
    List<String> cList = new java.util.ArrayList<String>();
    boolean ok = new SGDataCreator().getDataTypeCandidateList(EXAMPLE_DIR + "Example1.txt", cList);
    assertTrue(ok);
    assertEquals(
        Arrays.asList(
            SGDataTypeConstants.SXY_DATA,
            SGDataTypeConstants.SXY_MULTIPLE_DATA,
            SGDataTypeConstants.SXY_SAMPLING_DATA),
        cList);
  }

  @Test
  void getDataTypeCandidateListFindsSXYZ() throws Exception {
    List<String> cList = new java.util.ArrayList<String>();
    new SGDataCreator().getDataTypeCandidateList(EXAMPLE_DIR + "Example14.txt", cList);
    assertTrue(cList.contains(SGDataTypeConstants.SXYZ_DATA));
    assertFalse(cList.contains(SGDataTypeConstants.VXY_DATA));
  }

  @Test
  void getDataTypeCandidateListFindsVXY() throws Exception {
    List<String> cList = new java.util.ArrayList<String>();
    new SGDataCreator().getDataTypeCandidateList(EXAMPLE_DIR + "Example12.txt", cList);
    assertTrue(cList.contains(SGDataTypeConstants.VXY_DATA));
  }

  @Test
  void getDataTypeCandidateListAddsDateCandidate() throws Exception {
    List<String> cList = new java.util.ArrayList<String>();
    new SGDataCreator().getDataTypeCandidateList(EXAMPLE_DIR + "Example8.txt", cList);
    assertTrue(cList.contains(SGDataTypeConstants.SXY_DATE_DATA));
  }

  @Test
  void getDataTypeCandidateListThrowsForNonexistentFile() {
    List<String> cList = new java.util.ArrayList<String>();
    // "file not found" is a distinct condition: it must be reported by the
    // declared exception rather than swallowed as a "no candidates" result.
    assertThrows(
        FileNotFoundException.class,
        () -> new SGDataCreator().getDataTypeCandidateList("no/such/file.txt", cList));
  }

  @Test
  void parseFileColumnTypeParsesNumberDateAndTextColumns() throws Exception {
    Path path = writeTemp(".csv", "1.0\t2.0\t\"2005.1.1\"\tabc", "3.0\t4.0\t\"2005.2.1\"\tdef");
    SGDataCreator.SDArrayFileParseResult result =
        new SGDataCreator()
            .parseFileColumnType(path.toString(), SGDataTypeConstants.SXY_DATA, false, null);
    assertNotNull(result);
    assertEquals(2, result.length);
    SGDataCreator.FileColumn[] cols = result.fileColumns;
    assertEquals(4, cols.length);
    assertEquals(0, cols[0].index);
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, cols[0].valueType);
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, cols[1].valueType);
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_DATE, cols[2].valueType);
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_TEXT, cols[3].valueType);
  }

  @Test
  void parseFileColumnTypeDemotesBrokenNumberColumn() throws Exception {
    Path path = writeTemp(".csv", "1.0\t2.0", "3.0\tbad");
    SGDataCreator.SDArrayFileParseResult result =
        new SGDataCreator()
            .parseFileColumnType(path.toString(), SGDataTypeConstants.SXY_DATA, false, null);
    assertNotNull(result);
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, result.fileColumns[0].valueType);
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_TEXT, result.fileColumns[1].valueType);
  }

  @Test
  void parseFileColumnTypeHandlesTitles() throws Exception {
    Path path = writeTemp(".csv", "x\ty", "1.0\t2.0", "3.0\t4.0");
    SGDataCreator.SDArrayFileParseResult result =
        new SGDataCreator()
            .parseFileColumnType(path.toString(), SGDataTypeConstants.SXY_DATA, false, null);
    assertNotNull(result);
    assertEquals("x", result.fileColumns[0].title);
    assertEquals("y", result.fileColumns[1].title);
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, result.fileColumns[0].valueType);
    assertEquals(2, result.length);
  }

  @Test
  void parseFileColumnTypeForcesTextColumnForOldDataWithTickLabels() throws Exception {
    Path path = writeTemp(".csv", "1.0\t2.0\t3.0", "4.0\t5.0\t6.0");
    SGDataCreator.SDArrayFileParseResult result =
        new SGDataCreator()
            .parseFileColumnType(path.toString(), SGDataTypeConstants.SXY_DATA, true, "");
    assertNotNull(result);
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, result.fileColumns[0].valueType);
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, result.fileColumns[1].valueType);
    assertEquals(SGDataColumnTypeConstants.VALUE_TYPE_TEXT, result.fileColumns[2].valueType);
  }

  @Test
  void parseFileColumnTypeThrowsForNonexistentFile() {
    // "file not found" must be reported by the declared exception so that the
    // callers can distinguish it from an empty or invalid file.
    assertThrows(
        FileNotFoundException.class,
        () ->
            new SGDataCreator()
                .parseFileColumnType(
                    "no/such/file.txt", SGDataTypeConstants.SXY_DATA, false, null));
  }

  // -----------------------------------------------------------------
  // Data source bookkeeping
  // -----------------------------------------------------------------

  @Test
  void findDataSourceRejectsNullPath() {
    assertThrows(IllegalArgumentException.class, () -> new SGDataCreator().findDataSource(null));
  }

  @Test
  void getNetcdfFileReturnsNullForBadPath() {
    assertNull(new SGDataCreator().getNetcdfFile("no/such/file.nc"));
  }

  @Test
  void getHDF5FileReturnsNullForBadPath() {
    assertNull(new SGDataCreator().getHDF5File("no/such/file.h5"));
  }

  @Test
  void getMATLABFileReturnsNullForBadPath() {
    assertNull(new SGDataCreator().getMATLABFile("no/such/file.mat"));
  }

  @Test
  void replaceDataSourceSwapsTheRegisteredSource() throws Exception {
    SGNetCDFFile file = openNc("Example16.nc");
    Map<String, Object> infoMap =
        baseInfoMap(
            SGDataTypeConstants.SXY_NETCDF_DATA,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE,
            Boolean.FALSE,
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE,
            Boolean.TRUE);
    SGDataColumnInfoSet cols =
        nset(
            nci(file, "x", SGDataColumnTypeConstants.X_VALUE),
            nci(file, "height", SGDataColumnTypeConstants.Y_VALUE));
    SGDataCreator creator = new SGDataCreator();
    SGDataCreator.CreatedDataSet set =
        creator.create(file, cols, infoMap, new NoopProgressControl());
    SGData data = set.getData(0).getData();
    assertEquals(file.getPath(), data.getDataSource().getPath());
    SGNetCDFFile file2 = openNc("Example17.nc");
    creator.replaceDataSource(data, file2);
    assertEquals(file2.getPath(), data.getDataSource().getPath());
    assertEquals(file2, creator.findDataSource(file2.getPath()));
  }

  // -----------------------------------------------------------------
  // Helpers
  // -----------------------------------------------------------------

  private static Map<String, Object> baseInfoMap(String dataType, Object... keyValues) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, dataType);
    for (int ii = 0; ii + 1 < keyValues.length; ii += 2) {
      map.put((String) keyValues[ii], keyValues[ii + 1]);
    }
    return map;
  }

  private static SGDataColumnInfoSet sset(SGDataColumnInfo... cols) {
    return new SGDataColumnInfoSet(cols);
  }

  private static SGDataColumnInfoSet nset(SGDataColumnInfo... cols) {
    return new SGDataColumnInfoSet(cols);
  }

  private static SGDataColumnInfoSet mdset(SGDataColumnInfo... cols) {
    return new SGDataColumnInfoSet(cols);
  }

  private static SGSDArrayDataColumnInfo scol(String title, String columnType) {
    return scol(title, SGDataColumnTypeConstants.VALUE_TYPE_NUMBER, columnType);
  }

  private static SGSDArrayDataColumnInfo scol(String title, String valueType, String columnType) {
    SGSDArrayDataColumnInfo col = new SGSDArrayDataColumnInfo(title, valueType);
    col.setColumnType(columnType);
    return col;
  }

  private static SGNetCDFDataColumnInfo nci(SGNetCDFFile file, String var, String columnType) {
    SGNetCDFVariable v = file.findVariable(var);
    SGNetCDFDataColumnInfo col =
        new SGNetCDFDataColumnInfo(
            v,
            null,
            jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    col.setColumnType(columnType);
    return col;
  }

  private static SGMDArrayDataColumnInfo mdi(SGMDArrayFile file, String var, String columnType) {
    SGMDArrayVariable v = file.findVariable(var);
    SGMDArrayDataColumnInfo col =
        new SGMDArrayDataColumnInfo(
            v,
            null,
            jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    col.setGenericDimensionIndex(0);
    col.setColumnType(columnType);
    return col;
  }

  private static SGSDArrayFile openSDArray(
      String fileName, SGDataColumnInfoSet cols, Map<String, Object> infoMap) throws Exception {
    String path = EXAMPLE_DIR + fileName;
    // Use the production parser to build the data source from the file.
    return (SGSDArrayFile) SGApplicationUtility.createDataSource(path, cols, infoMap);
  }

  private static SGNetCDFFile openNc(String fileName) throws IOException {
    return new SGNetCDFFile(NetcdfFiles.open(EXAMPLE_DIR + fileName));
  }

  private static Path writeTemp(String suffix, String... lines) throws IOException {
    Path path = Files.createTempFile("samurai-graph-test", suffix);
    path.toFile().deleteOnExit();
    Files.write(path, Arrays.asList(lines));
    return path;
  }

  // Writes a NetCDF file with dims x(3), y(2) and variables x, y, v1(y,x), v2(y,x).
  private static SGNetCDFFile writeVXYNetCDF() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    Files.delete(path);
    path.toFile().deleteOnExit();
    NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3(path.toString());
    Dimension xDim = builder.addDimension("x", 3);
    Dimension yDim = builder.addDimension("y", 2);
    builder.addVariable("x", DataType.DOUBLE, Arrays.asList(xDim));
    builder.addVariable("y", DataType.DOUBLE, Arrays.asList(yDim));
    builder.addVariable("v1", DataType.DOUBLE, Arrays.asList(yDim, xDim));
    builder.addVariable("v2", DataType.DOUBLE, Arrays.asList(yDim, xDim));
    try (NetcdfFormatWriter writer = builder.build()) {
      writer.write("x", makeDoubleArray(new double[] {10.0, 20.0, 30.0}));
      writer.write("y", makeDoubleArray(new double[] {1.5, 2.5}));
      writer.write("v1", makeDoubleArray(2, 3, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0));
      writer.write("v2", makeDoubleArray(2, 3, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0));
    }
    return new SGNetCDFFile(NetcdfFiles.open(path.toString()));
  }

  // Writes a NetCDF file with dims x(3), y(2), z(4) and variables x, y, z, v(z,y,x).
  private static SGNetCDFFile writeSXYZNetCDF() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    Files.delete(path);
    path.toFile().deleteOnExit();
    NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3(path.toString());
    Dimension xDim = builder.addDimension("x", 3);
    Dimension yDim = builder.addDimension("y", 2);
    Dimension zDim = builder.addDimension("z", 4);
    builder.addVariable("x", DataType.DOUBLE, Arrays.asList(xDim));
    builder.addVariable("y", DataType.DOUBLE, Arrays.asList(yDim));
    builder.addVariable("z", DataType.DOUBLE, Arrays.asList(zDim));
    builder.addVariable("v", DataType.DOUBLE, Arrays.asList(zDim, yDim, xDim));
    try (NetcdfFormatWriter writer = builder.build()) {
      writer.write("x", makeDoubleArray(new double[] {1.0, 2.0, 3.0}));
      writer.write("y", makeDoubleArray(new double[] {10.0, 20.0}));
      writer.write("z", makeDoubleArray(new double[] {100.0, 200.0, 300.0, 400.0}));
      double[] v = new double[24];
      for (int ii = 0; ii < v.length; ii++) {
        v[ii] = ii;
      }
      writer.write("v", makeDoubleArray(new int[] {4, 2, 3}, v));
    }
    return new SGNetCDFFile(NetcdfFiles.open(path.toString()));
  }

  private static Array makeDoubleArray(double[] values) {
    ArrayDouble.D1 array = new ArrayDouble.D1(values.length);
    for (int ii = 0; ii < values.length; ii++) {
      array.setDouble(ii, values[ii]);
    }
    return array;
  }

  private static Array makeDoubleArray(int rows, int cols, double... values) {
    return makeDoubleArray(new int[] {rows, cols}, values);
  }

  private static Array makeDoubleArray(int[] shape, double[] values) {
    if (shape.length == 3) {
      ArrayDouble.D3 array = new ArrayDouble.D3(shape[0], shape[1], shape[2]);
      for (int ii = 0; ii < values.length; ii++) {
        array.setDouble(ii, values[ii]);
      }
      return array;
    }
    if (shape.length == 2) {
      ArrayDouble.D2 array = new ArrayDouble.D2(shape[0], shape[1]);
      for (int ii = 0; ii < values.length; ii++) {
        array.setDouble(ii, values[ii]);
      }
      return array;
    }
    ArrayDouble.D1 array = new ArrayDouble.D1(shape[0]);
    for (int ii = 0; ii < values.length; ii++) {
      array.setDouble(ii, values[ii]);
    }
    return array;
  }

  // Creates an HDF5 file with 1-D datasets (or 2-D ones when dims is non-null).
  private static SGHDF5File createHDF5(String names, int[] dims) throws IOException {
    String[] nameArray = names.split(",");
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(path.toFile())) {
      if (dims == null) {
        for (int ii = 0; ii < nameArray.length; ii++) {
          double[] values = new double[3];
          for (int jj = 0; jj < 3; jj++) {
            values[jj] = (ii + 1) + jj * 10.0;
          }
          writer.writeDoubleArray(nameArray[ii], values);
        }
      } else {
        double[][] matrix = new double[dims[0]][dims[1]];
        for (int ii = 0; ii < dims[0]; ii++) {
          for (int jj = 0; jj < dims[1]; jj++) {
            matrix[ii][jj] = ii * 100 + jj;
          }
        }
        for (int ii = 0; ii < nameArray.length; ii++) {
          writer.writeDoubleMatrix(nameArray[ii], matrix);
        }
      }
    }
    IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(path.toFile());
    return new SGHDF5File(reader);
  }
}

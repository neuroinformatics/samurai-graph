package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.RELOAD_DATA_STATUS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5Exception;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.data.SGHDF5File;
import jp.riken.brain.ni.samuraigraph.data.SGMATLABFile;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests of the data source reload pipeline with fake operations. */
class SGDataReloaderTest {

  private static final class FakeActions implements SGDataReloadActions {
    boolean existsResult = true;
    final Set<String> netCDFFailed = new HashSet<String>();
    final Set<String> hdf5Failed = new HashSet<String>();
    final Set<String> matFailed = new HashSet<String>();
    SGIDataSource reopened = mock(SGIDataSource.class);
    final Set<SGData> sdArrayDataSet = new HashSet<SGData>();
    SGIDataSource sdCreated = mock(SGIDataSource.class);
    FileNotFoundException createError = null;
    boolean replaceResult = true;
    final List<String> reopenCalls = new ArrayList<String>();
    final List<String> createCalls = new ArrayList<String>();
    final List<String> replaceCalls = new ArrayList<String>();

    @Override
    public boolean isSDArrayData(final SGData data) {
      return this.sdArrayDataSet.contains(data);
    }

    @Override
    public boolean exists(final String path, final SGIDataSource current) {
      return this.existsResult;
    }

    @Override
    public SGIDataSource openNetCDF(final String path) throws IOException {
      this.reopenCalls.add(path);
      if (this.netCDFFailed.contains(path)) {
        throw new IOException("failed to open NetCDF");
      }
      return this.reopened;
    }

    @Override
    public SGIDataSource openHDF5(final String path) throws HDF5Exception {
      this.reopenCalls.add(path);
      if (this.hdf5Failed.contains(path)) {
        throw new HDF5Exception("failed to open HDF5");
      }
      return this.reopened;
    }

    @Override
    public SGIDataSource openMAT(final String path) throws IOException {
      this.reopenCalls.add(path);
      if (this.matFailed.contains(path)) {
        throw new IOException("failed to open MATLAB");
      }
      return this.reopened;
    }

    @Override
    public SGDataColumnInfoSet getDataColumnInfoSet(final SGData data, final SGFigure figure) {
      return new SGDataColumnInfoSet(new jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo[0]);
    }

    @Override
    public SGIDataSource createDataSource(
        final String path, final SGDataColumnInfoSet colInfoSet, final Map<String, Object> infoMap)
        throws FileNotFoundException {
      this.createCalls.add(path);
      if (this.createError != null) {
        throw this.createError;
      }
      return this.sdCreated;
    }

    @Override
    public SGDataSourceObserver getDataSourceObserver() {
      return mock(SGDataSourceObserver.class);
    }

    @Override
    public boolean replaceAndUpdateData(
        final SGData data,
        final SGFigure figure,
        final SGIDataSource srcOld,
        final SGIDataSource srcNew) {
      this.replaceCalls.add(data.getPath());
      return this.replaceResult;
    }
  }

  private SGDataReloader reloader;

  private FakeActions actions;

  private final Map<SGData, SGFigure> figureMap = new HashMap<SGData, SGFigure>();

  @BeforeEach
  void setUp() {
    this.reloader = new SGDataReloader();
    this.actions = new FakeActions();
  }

  private SGData netCDFData(final String path) {
    final SGData data = mock(SGData.class);
    when(data.getPath()).thenReturn(path);
    when(data.getDataSource()).thenReturn(mock(SGNetCDFFile.class));
    return data;
  }

  private SGData hdf5Data(final String path) {
    final SGData data = mock(SGData.class);
    when(data.getPath()).thenReturn(path);
    when(data.getDataSource()).thenReturn(mock(SGHDF5File.class));
    return data;
  }

  private SGData matlabData(final String path) {
    final SGData data = mock(SGData.class);
    when(data.getPath()).thenReturn(path);
    when(data.getDataSource()).thenReturn(mock(SGMATLABFile.class));
    return data;
  }

  private SGData plainData(final String path) {
    final SGData data = mock(SGData.class);
    when(data.getPath()).thenReturn(path);
    when(data.getDataSource()).thenReturn(mock(SGIDataSource.class));
    return data;
  }

  private Map<String, RELOAD_DATA_STATUS> reload(final List<SGData> dataList) {
    this.figureMap.clear();
    for (SGData data : dataList) {
      this.figureMap.put(data, mock(SGFigure.class));
    }
    return this.reloader.reload(dataList, this.figureMap, this.actions);
  }

  @Test
  void recordsSucceededForReopenedData() {
    final SGData data = netCDFData("a.nc");
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(data));
    assertEquals(RELOAD_DATA_STATUS.SUCCEEDED, status.get("a.nc"));
    assertEquals(Arrays.asList("a.nc"), this.actions.reopenCalls);
    assertEquals(Arrays.asList("a.nc"), this.actions.replaceCalls);
  }

  @Test
  void deduplicatesSamePath() {
    final SGData first = netCDFData("a.nc");
    final SGData second = netCDFData("a.nc");
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(first, second));
    assertEquals(RELOAD_DATA_STATUS.SUCCEEDED, status.get("a.nc"));
    assertEquals(Arrays.asList("a.nc"), this.actions.reopenCalls);
    assertEquals(2, this.actions.replaceCalls.size());
  }

  @Test
  void marksMissingFileAsLost() {
    this.actions.existsResult = false;
    final SGData data = netCDFData("a.nc");
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(data));
    assertEquals(RELOAD_DATA_STATUS.LOST, status.get("a.nc"));
    assertTrue(this.actions.reopenCalls.isEmpty());
    assertTrue(this.actions.replaceCalls.isEmpty());
  }

  @Test
  void marksNetCDFOpenFailureAsInvalid() {
    this.actions.netCDFFailed.add("bad.nc");
    final SGData data = netCDFData("bad.nc");
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(data));
    assertEquals(RELOAD_DATA_STATUS.INVALID_DATA, status.get("bad.nc"));
    assertTrue(this.actions.replaceCalls.isEmpty());
  }

  @Test
  void marksHDF5OpenFailureAsInvalid() {
    this.actions.hdf5Failed.add("bad.h5");
    final SGData data = hdf5Data("bad.h5");
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(data));
    assertEquals(RELOAD_DATA_STATUS.INVALID_DATA, status.get("bad.h5"));
    assertTrue(this.actions.replaceCalls.isEmpty());
  }

  @Test
  void marksMATLABOpenFailureAsInvalid() {
    this.actions.matFailed.add("bad.mat");
    final SGData data = matlabData("bad.mat");
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(data));
    assertEquals(RELOAD_DATA_STATUS.INVALID_DATA, status.get("bad.mat"));
    assertTrue(this.actions.replaceCalls.isEmpty());
  }

  @Test
  void skipsDataWithoutPath() {
    final SGData data = mock(SGData.class);
    when(data.getPath()).thenReturn(null);
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(data));
    assertTrue(status.isEmpty());
    assertTrue(this.actions.reopenCalls.isEmpty());
    assertTrue(this.actions.replaceCalls.isEmpty());
  }

  @Test
  void keepsPlainSourceSucceededWithNullReplacement() {
    final SGData data = plainData("plain");
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(data));
    assertEquals(RELOAD_DATA_STATUS.SUCCEEDED, status.get("plain"));
    assertEquals(Arrays.asList("plain"), this.actions.replaceCalls);
  }

  @Test
  void openFailureDoesNotBreakOtherPaths() {
    this.actions.netCDFFailed.add("bad.nc");
    final SGData bad = netCDFData("bad.nc");
    final SGData good = netCDFData("good.nc");
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(bad, good));
    assertEquals(RELOAD_DATA_STATUS.INVALID_DATA, status.get("bad.nc"));
    assertEquals(RELOAD_DATA_STATUS.SUCCEEDED, status.get("good.nc"));
    assertTrue(this.actions.reopenCalls.containsAll(Arrays.asList("bad.nc", "good.nc")));
    assertEquals(Arrays.asList("good.nc"), this.actions.replaceCalls);
  }

  @Test
  void marksReplaceFailureAsInvalid() {
    this.actions.replaceResult = false;
    final SGData data = netCDFData("a.nc");
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(data));
    assertEquals(RELOAD_DATA_STATUS.INVALID_DATA, status.get("a.nc"));
  }

  @Test
  void recreatesSDArraySource() {
    final SGData data = netCDFData("a.nc");
    this.actions.sdArrayDataSet.add(data);
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(data));
    assertEquals(RELOAD_DATA_STATUS.SUCCEEDED, status.get("a.nc"));
    assertEquals(Arrays.asList("a.nc"), this.actions.createCalls);
    assertEquals(Arrays.asList("a.nc"), this.actions.replaceCalls);
  }

  @Test
  void marksSDArrayCreateMissingAsLost() throws Exception {
    final SGData data = netCDFData("a.nc");
    this.actions.sdArrayDataSet.add(data);
    this.actions.createError = new FileNotFoundException("not found");
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(data));
    assertEquals(RELOAD_DATA_STATUS.LOST, status.get("a.nc"));
    assertEquals(Arrays.asList("a.nc"), this.actions.createCalls);
    assertTrue(this.actions.replaceCalls.isEmpty());
  }

  @Test
  void marksSDArrayCreateNullAsInvalid() {
    final SGData data = netCDFData("a.nc");
    this.actions.sdArrayDataSet.add(data);
    this.actions.sdCreated = null;
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(data));
    assertEquals(RELOAD_DATA_STATUS.INVALID_DATA, status.get("a.nc"));
    assertTrue(this.actions.replaceCalls.isEmpty());
  }

  @Test
  void appliesTheLastStatusInMixedScenarios() {
    final SGData missing = netCDFData("missing.nc");
    final SGData sdArray = netCDFData("sd.nc");
    this.actions.sdArrayDataSet.add(sdArray);
    this.actions.existsResult = false;
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(sdArray, missing));
    // the SDArray recreate overrides the LOST status of the reopen step
    assertEquals(RELOAD_DATA_STATUS.SUCCEEDED, status.get("sd.nc"));
    // the missing non-SDArray path stays LOST
    assertEquals(RELOAD_DATA_STATUS.LOST, status.get("missing.nc"));
  }

  @Test
  void sDArraySuccessOverridesTheReopenStatus() {
    final SGData data = netCDFData("sd.nc");
    this.actions.sdArrayDataSet.add(data);
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(data));
    assertEquals(RELOAD_DATA_STATUS.SUCCEEDED, status.get("sd.nc"));
    assertEquals(Arrays.asList("sd.nc"), this.actions.createCalls);
    assertTrue(this.actions.replaceCalls.contains("sd.nc"));
  }

  @Test
  void unknownStatusIsNotTreatedAsError() {
    final SGData data = netCDFData("a.nc");
    final Map<String, RELOAD_DATA_STATUS> status = reload(Arrays.asList(data));
    assertFalse(RELOAD_DATA_STATUS.LOST.equals(status.get("a.nc")));
    assertFalse(RELOAD_DATA_STATUS.INVALID_DATA.equals(status.get("a.nc")));
    assertNull(status.get("absent"));
  }
}

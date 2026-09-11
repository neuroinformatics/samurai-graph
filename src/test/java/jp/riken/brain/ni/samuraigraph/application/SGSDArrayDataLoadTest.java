package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGIProgressControl;
import jp.riken.brain.ni.samuraigraph.data.SGDataTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayMultipleData;
import org.junit.jupiter.api.Test;

/** Loads a generated text data file through the public data creation path. */
class SGSDArrayDataLoadTest {

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

  @Test
  void createsSXYDataFromTextFile() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".csv");
    path.toFile().deleteOnExit();
    Files.write(path, Arrays.asList("1 10", "2 20", "3 30"));

    SGSDArrayDataColumnInfo xInfo =
        new SGSDArrayDataColumnInfo("x", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    xInfo.setColumnType(SGIDataColumnTypeConstants.X_VALUE);
    SGSDArrayDataColumnInfo yInfo =
        new SGSDArrayDataColumnInfo("y", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    yInfo.setColumnType(SGIDataColumnTypeConstants.Y_VALUE);
    SGDataColumnInfoSet colInfoSet = new SGDataColumnInfoSet(new SGDataColumnInfo[] {xInfo, yInfo});

    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_DATA);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);

    SGDataCreator creator = new SGDataCreator();
    SGDataCreator.CreatedDataSet set =
        creator.create(
            SGApplicationUtility.createDataSource(path.toString(), colInfoSet, infoMap),
            colInfoSet,
            infoMap,
            new NoopProgressControl());

    assertNotNull(set);
    assertEquals(1, set.getDataLength());
    SGSXYSDArrayMultipleData data = (SGSXYSDArrayMultipleData) set.getData(0).getData();
    assertEquals(1, data.getChildNumber());
    SGISXYTypeSingleData child = data.getSXYDataArray()[0];
    double[] xValues = child.getXValueArray(false);
    double[] yValues = child.getYValueArray(false);
    assertEquals(3, xValues.length);
    assertEquals(1.0, xValues[0], 0.0);
    assertEquals(3.0, xValues[2], 0.0);
    assertEquals(10.0, yValues[0], 0.0);
    assertEquals(30.0, yValues[2], 0.0);
  }
}

package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.RELOAD_DATA_STATUS;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5Exception;
import java.io.FileNotFoundException;
import java.io.IOException;
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

/**
 * Reloads the data sources of the given data.
 *
 * <p>Collects the data paths, reopens the data sources, recreates the scalar XY array sources and
 * replaces the data sources in the graph elements. The file I/O and the graph operations are
 * delegated to {@link SGDataReloadActions} so that the pipeline is testable headlessly.
 */
final class SGDataReloader {

  /**
   * Reloads the data sources.
   *
   * @param dataList the list of data
   * @param dataFigureMap the figure map of each data
   * @param actions the file I/O and graph operations
   * @return the reload status of each data path
   */
  Map<String, RELOAD_DATA_STATUS> reload(
      final List<SGData> dataList,
      final Map<SGData, SGFigure> dataFigureMap,
      final SGDataReloadActions actions) {

    // collects the data paths to prevent duplication
    final Set<String> dataPathSet = new HashSet<String>();
    final Map<String, SGIDataSource> srcMap = new HashMap<String, SGIDataSource>();
    for (SGData data : dataList) {
      final String path = data.getPath();
      if (path == null) {
        continue;
      }
      dataPathSet.add(path);
      srcMap.put(path, data.getDataSource());
    }

    // reopens the data sources
    final Map<String, SGIDataSource> srcMapNew = new HashMap<String, SGIDataSource>();
    final Map<String, RELOAD_DATA_STATUS> resultMap = new HashMap<String, RELOAD_DATA_STATUS>();
    for (String path : dataPathSet) {
      final SGIDataSource srcCur = srcMap.get(path);
      if (!actions.exists(path, srcCur)) {
        resultMap.put(path, RELOAD_DATA_STATUS.LOST);
        continue;
      }
      SGIDataSource srcNew = null;
      if (srcCur instanceof SGNetCDFFile) {
        try {
          srcNew = actions.openNetCDF(path);
        } catch (IOException e) {
          resultMap.put(path, RELOAD_DATA_STATUS.INVALID_DATA);
          continue;
        }
      } else if (srcCur instanceof SGHDF5File) {
        try {
          srcNew = actions.openHDF5(path);
        } catch (HDF5Exception e) {
          resultMap.put(path, RELOAD_DATA_STATUS.INVALID_DATA);
          continue;
        }
      } else if (srcCur instanceof SGMATLABFile) {
        try {
          srcNew = actions.openMAT(path);
        } catch (IOException e) {
          resultMap.put(path, RELOAD_DATA_STATUS.INVALID_DATA);
          continue;
        }
      }
      srcMapNew.put(path, srcNew);
      resultMap.put(path, RELOAD_DATA_STATUS.SUCCEEDED);
    }

    // recreates the scalar XY array data sources
    for (SGData data : dataList) {
      if (!actions.isSDArrayData(data)) {
        continue;
      }
      final SGFigure figure = dataFigureMap.get(data);
      final String path = data.getPath();
      final SGDataColumnInfoSet colInfoSet = actions.getDataColumnInfoSet(data, figure);
      final Map<String, Object> infoMap = data.getInfoMap();
      SGIDataSource srcNew = null;
      try {
        srcNew = actions.createDataSource(path, colInfoSet, infoMap);
      } catch (FileNotFoundException e) {
        resultMap.put(path, RELOAD_DATA_STATUS.LOST);
        continue;
      }
      if (srcNew == null) {
        resultMap.put(path, RELOAD_DATA_STATUS.INVALID_DATA);
        continue;
      }
      srcMapNew.put(path, srcNew);
      resultMap.put(path, RELOAD_DATA_STATUS.SUCCEEDED);
    }

    // replaces the data sources in the graph elements
    final SGDataSourceObserver obs = actions.getDataSourceObserver();
    for (SGData data : dataList) {
      final String path = data.getPath();
      if (path == null) {
        continue;
      }
      final RELOAD_DATA_STATUS result = resultMap.get(path);
      if (RELOAD_DATA_STATUS.LOST.equals(result)
          || RELOAD_DATA_STATUS.INVALID_DATA.equals(result)) {
        continue;
      }
      final SGIDataSource srcNew = srcMapNew.get(path);
      final SGFigure figure = dataFigureMap.get(data);
      final SGIDataSource srcOld = data.getDataSource();
      if (!actions.replaceAndUpdateData(data, figure, srcOld, srcNew)) {
        resultMap.put(path, RELOAD_DATA_STATUS.INVALID_DATA);
      }
    }

    return resultMap;
  }
}

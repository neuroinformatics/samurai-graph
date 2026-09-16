package jp.riken.brain.ni.samuraigraph.application;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5Exception;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;

/**
 * The operations used to reload the data sources.
 *
 * <p>The data reloader only performs the refresh pipeline and delegates the file I/O and the graph
 * operations to this interface so that the pipeline can be tested headlessly with a fake
 * implementation.
 */
interface SGDataReloadActions {

  /** Returns whether the data is a scalar XY array data. */
  boolean isSDArrayData(SGData data);

  /**
   * Returns whether the file for the given path still exists. A remote NetCDF file is always
   * regarded as existing.
   */
  boolean exists(String path, SGIDataSource current);

  /** Opens a NetCDF data source. */
  SGIDataSource openNetCDF(String path) throws IOException;

  /** Opens an HDF5 data source. */
  SGIDataSource openHDF5(String path) throws HDF5Exception;

  /** Opens a MATLAB data source. */
  SGIDataSource openMAT(String path) throws IOException;

  /** Returns the column info set of the data in the figure. */
  SGDataColumnInfoSet getDataColumnInfoSet(SGData data, SGFigure figure);

  /** Recreates the data source of a scalar XY array data. */
  SGIDataSource createDataSource(
      String path, SGDataColumnInfoSet colInfoSet, Map<String, Object> infoMap)
      throws FileNotFoundException;

  /** Returns the observer of the data sources. */
  SGDataSourceObserver getDataSourceObserver();

  /**
   * Replaces the data source of the data in the figure and updates the drawing elements.
   *
   * @return false if the drawing elements cannot be updated
   */
  boolean replaceAndUpdateData(
      SGData data, SGFigure figure, SGIDataSource srcOld, SGIDataSource srcNew);
}

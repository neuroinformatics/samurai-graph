package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;

public interface SGISXYMultipleDimensionData {

  public SGPickUpDimensionInfo getPickUpDimensionInfo();

  public boolean setPickUpDimensionInfo(SGPickUpDimensionInfo info);

  public SGIntegerSeriesSet getIndices();

  /**
   * Sets the column types for the state with the picked up dimension.
   *
   * @param columns an array of column types
   * @return the result of setting the column types for the picked state
   */
  public boolean setColumnTypeDimensionPicked(String[] columns);

  /**
   * Sets the column types for the state without the picked up dimension.
   *
   * @param columns an array of column types
   * @return the result of setting the column types for the not picked state
   */
  public boolean setColumnTypeDimensionNotPicked(String[] columns);

  /**
   * Sets the column types with a given picked up dimension information.
   *
   * <p>The shared chain applies the picked up dimension information and its column types when
   * picked, and otherwise applies the not picked column types. When the picked path fails, the
   * applied picked up dimension information is cleared.
   *
   * @param columns an array of column types
   * @param info the picked up dimension information or null
   * @return the result of setting the column types
   */
  default boolean setColumnTypeWithPickUp(String[] columns, SGPickUpDimensionInfo info) {
    this.setPickUpDimensionInfo(info);
    if (info != null) {
      if (!this.setColumnTypeDimensionPicked(columns)) {
        // clears pick up information
        this.setPickUpDimensionInfo(null);
        return false;
      }
      return true;
    } else {
      return this.setColumnTypeDimensionNotPicked(columns);
    }
  }
}

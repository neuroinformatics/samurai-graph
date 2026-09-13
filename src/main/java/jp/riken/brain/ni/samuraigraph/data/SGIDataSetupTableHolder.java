package jp.riken.brain.ni.samuraigraph.data;

import javax.swing.JTable;
import jp.riken.brain.ni.samuraigraph.base.SGButton;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;

/** An interface for an object that has a table to setup data. */
public interface SGIDataSetupTableHolder {

  /** Returns the table. */
  public JTable getTable();

  /** Returns the button to restore all data column types. */
  public SGButton getRestoreButton();

  /** Returns the button to clear all data column types. */
  public SGButton getClearButton();

  /** Returns the button to complement the column types. */
  public SGButton getComplementButton();

  /** Checks selected items. */
  public boolean checkSelectedItems();

  /** Returns selected column types. */
  public SGDataColumnInfoSet getDataColumnInfoSet();

  /** Clear all data. */
  public void clear();

  /** Update the table. */
  public void updateTable();

  /**
   * Sets the data type.
   *
   * @param dataType the data type to be set
   */
  public void setDataType(final String dataType);
}

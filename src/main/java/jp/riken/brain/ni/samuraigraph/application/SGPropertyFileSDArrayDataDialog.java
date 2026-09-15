/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SGPropertyFileArrayDataDialog.java
 *
 * Created on 2009/08/28, 10:07:25
 */

package jp.riken.brain.ni.samuraigraph.application;

import java.util.Map;
import javax.swing.JButton;
import javax.swing.text.JTextComponent;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.data.SGDataSetupPanel;
import jp.riken.brain.ni.samuraigraph.data.SGIDataSetupTableHolder;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;

/** */
public class SGPropertyFileSDArrayDataDialog extends SGPropertyFileDataDialog {

  /** Serial Version UID */
  private static final long serialVersionUID = 3525676685533088167L;

  /** Creates new form SGPropertyFileArrayDataDialog */
  public SGPropertyFileSDArrayDataDialog(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
    this.initProperty();
  }

  /** Creates new form SGPropertyFileArrayDataDialog */
  public SGPropertyFileSDArrayDataDialog(java.awt.Dialog parent, boolean modal) {
    super(parent, modal);
    initComponents();
    this.initProperty();
  }

  private void initComponents() {
    this.mPanel = new javax.swing.JPanel();
    this.mDataSetupPanel = new jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataSetupPanel();

    this.getContentPane().setLayout(new java.awt.GridBagLayout());

    this.mPanel.setLayout(new java.awt.GridBagLayout());
    this.mPanel.add(this.mDataSetupPanel, new java.awt.GridBagConstraints());

    this.getContentPane().add(this.mPanel, new java.awt.GridBagConstraints());

    this.initializeButtonPanel(1);

    this.pack();
  }

  private jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataSetupPanel mDataSetupPanel;
  private javax.swing.JPanel mPanel;

  private void initProperty() {}

  protected JButton getFileChooserButton() {
    return null;
  }

  protected JTextComponent getFileNameTextComponent() {
    return null;
  }

  protected JButton getCancelButton() {
    return this.mCancelButton;
  }

  protected JButton getOKButton() {
    return this.mOKButton;
  }

  protected SGIDataSetupTableHolder getDataSetupTableHolder() {
    return this.mDataSetupPanel;
  }

  /**
   * Set information of data columns.
   *
   * @param fileName the name of file
   * @param sdFile single dimensional array file
   * @param dataType type of data
   * @param colInfoSet data columns
   * @param infoMap a map of information
   * @param showDefault a flag whether to show default column type
   * @param versionNumber the version number of property file
   * @return true if succeeded
   */
  public boolean setData(
      String fileName,
      SGSDArrayFile sdFile,
      String dataType,
      SGDataColumnInfoSet colInfoSet,
      Map<String, Object> infoMap,
      final boolean showDefault,
      final String versionNumber) {

    // set to attribute
    this.mVersionNumber = versionNumber;

    // set the data type and information map
    this.setDataType(dataType, infoMap);

    // set the file name
    this.setFileName(fileName);

    // set data
    if (this.mDataSetupPanel.setData(sdFile, dataType, colInfoSet, infoMap, showDefault) == false) {
      return false;
    }

    return true;
  }

  @Override
  protected boolean isAcceptable(String fileName) {
    return true;
  }

  @Override
  protected SGDataSetupPanel getDataSetupPanel() {
    return this.mDataSetupPanel;
  }
}

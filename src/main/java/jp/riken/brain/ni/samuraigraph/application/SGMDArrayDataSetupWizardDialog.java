/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SGHDF5DataSetupWizardDialog.java
 *
 * Created on 2010/12/28, 10:50:10
 */

package jp.riken.brain.ni.samuraigraph.application;

import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGTextField;
import jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataSetupPanel;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataSetupPanel;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayFile;

/** A wizard dialog to setup HDF5 data when it is added. */
public class SGMDArrayDataSetupWizardDialog extends SGDataSetupWizardDialog {

  private static final long serialVersionUID = -7084184295273722806L;

  /** Creates new form SGHDF5DataSetupWizardDialog */
  public SGMDArrayDataSetupWizardDialog(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
  }

  private void initComponents() {
    this.mMDArrayDataSetupPanel = new jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataSetupPanel();

    this.getContentPane().setLayout(new java.awt.GridBagLayout());

    this.initializeDataNamePanel();

    java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    this.getContentPane().add(this.mMDArrayDataSetupPanel, gridBagConstraints);

    this.initializeButtonPanel();

    this.pack();
  }

  @Override
  protected JButton getPreviousButton() {
    return this.mPreviousButton;
  }

  @Override
  protected JButton getNextButton() {
    return this.mNextButton;
  }

  @Override
  protected JButton getOKButton() {
    return this.mOKButton;
  }

  @Override
  protected JButton getCancelButton() {
    return this.mCancelButton;
  }

  @Override
  protected SGDataSetupPanel getDataSetupPanel() {
    return this.mMDArrayDataSetupPanel;
  }

  @Override
  protected SGTextField getDataNameTextField() {
    return this.mDataNameTextField;
  }

  private jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataSetupPanel mMDArrayDataSetupPanel;

  /**
   * Sets the multidimensional array data.
   *
   * @param mdFile multidimensional array file
   * @param dataType type of data
   * @param colInfoSet data columns
   * @param infoMap a map of information
   * @param showDefault a flag whether to show default column type
   * @return true if succeeded
   */
  public boolean setData(
      SGMDArrayFile mdFile,
      String dataType,
      SGDataColumnInfoSet colInfoSet,
      Map<String, Object> infoMap,
      final boolean showDefault) {

    // set the title
    this.setupTitle(dataType);

    if (this.mMDArrayDataSetupPanel.setData(mdFile, dataType, colInfoSet, infoMap, showDefault)
        == false) {
      return false;
    }

    // set data name
    String name = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_NAME);
    this.setDataName(name);

    // set visible of next button
    if (SGDataDataTypeUtility.isSXYTypeData(dataType)) {
      this.getNextButton().setVisible(true);
    } else {
      this.getNextButton().setVisible(false);
    }

    // enable the OK button
    this.getOKButton().setEnabled(true);
    this.getNextButton().setEnabled(true);

    // packs this dialog
    SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            pack();
          }
        });

    return true;
  }

  public List<String> getPickUpDatasetName() {
    return ((SGMDArrayDataSetupPanel) this.getDataSetupPanel()).getSXYDataPickUpDatasetName();
  }

  public Map<String, Integer> getPickupDimensionIndexMap() {
    return ((SGMDArrayDataSetupPanel) this.getDataSetupPanel()).getSXYDataPickUpDimensionIndexMap();
  }

  /**
   * Returns whether stride of data arrays is available.
   *
   * @return true if stride of data arrays is available
   */
  public boolean isStrideAvailable() {
    return this.mMDArrayDataSetupPanel.isStrideAvailable();
  }
}

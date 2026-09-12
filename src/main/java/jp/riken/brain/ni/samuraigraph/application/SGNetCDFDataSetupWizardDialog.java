/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SGNetCDFDataSetupWizardDialog.java
 *
 * Created on 2009/08/12, 14:12:54
 */

package jp.riken.brain.ni.samuraigraph.application;

import java.util.Map;
import javax.swing.JButton;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGTextField;
import jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataSetupPanel;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;

/** A wizard dialog to setup netCDF data when it is added. */
public class SGNetCDFDataSetupWizardDialog extends SGDataSetupWizardDialog {

  /** Serial Version UID */
  private static final long serialVersionUID = 9030133791210821567L;

  /** Creates new form SGNetCDFDataSetupWizardDialog */
  public SGNetCDFDataSetupWizardDialog(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
  }

  private void initComponents() {
    this.mNetCDFDataSetupPanel = new jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataSetupPanel();

    this.getContentPane().setLayout(new java.awt.GridBagLayout());

    this.initializeDataNamePanel();

    java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    this.getContentPane().add(this.mNetCDFDataSetupPanel, gridBagConstraints);

    this.initializeButtonPanel();

    this.pack();
  }

  private jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataSetupPanel mNetCDFDataSetupPanel;

  @Override
  protected JButton getCancelButton() {
    return this.mCancelButton;
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
  protected JButton getPreviousButton() {
    return this.mPreviousButton;
  }

  @Override
  protected SGDataSetupPanel getDataSetupPanel() {
    return this.mNetCDFDataSetupPanel;
  }

  @Override
  protected SGTextField getDataNameTextField() {
    return this.mDataNameTextField;
  }

  /**
   * Sets the netCDF data.
   *
   * @param ncFile the netCDF data
   * @param dataType type of data
   * @param colInfoSet data columns
   * @param infoMap a map of information
   * @param showDefault a flag whether to show default column type
   * @return true if succeeded
   */
  public boolean setData(
      SGNetCDFFile ncFile,
      String dataType,
      SGDataColumnInfoSet colInfoSet,
      Map<String, Object> infoMap,
      final boolean showDefault) {

    // set the title
    this.setupTitle(dataType);

    if (this.mNetCDFDataSetupPanel.setData(ncFile, dataType, colInfoSet, infoMap, showDefault)
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
    this.pack();

    return true;
  }

  /**
   * Returns whether stride of data arrays is available.
   *
   * @return true if stride of data arrays is available
   */
  public boolean isStrideAvailable() {
    return this.mNetCDFDataSetupPanel.isStrideAvailable();
  }
}

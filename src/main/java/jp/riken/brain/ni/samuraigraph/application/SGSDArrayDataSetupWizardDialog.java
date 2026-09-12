/*
 * SGSDArrayDataSetupWizardDialog.java
 *
 * Created on 2009/07/01, 15:50:03
 */

package jp.riken.brain.ni.samuraigraph.application;

import java.util.Map;
import javax.swing.JButton;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGTextField;
import jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataSetupPanel;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;

/** A class of wizard dialog to select the type of each column. */
public class SGSDArrayDataSetupWizardDialog extends SGDataSetupWizardDialog {

  /** Serial Version UID. */
  private static final long serialVersionUID = -3351323186366134225L;

  /** Creates new form SGSDArrayDataSetupWizardDialog */
  public SGSDArrayDataSetupWizardDialog(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
  }

  private void initComponents() {
    this.mDataSetupPanel = new jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataSetupPanel();

    this.getContentPane().setLayout(new java.awt.GridBagLayout());

    this.initializeButtonPanel(new java.awt.Insets(5, 0, 0, 0));

    this.initializeDataNamePanel();

    java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    this.getContentPane().add(this.mDataSetupPanel, gridBagConstraints);

    this.pack();
  }

  protected JButton getPreviousButton() {
    return this.mPreviousButton;
  }

  protected JButton getNextButton() {
    return this.mNextButton;
  }

  protected JButton getOKButton() {
    return this.mOKButton;
  }

  protected JButton getCancelButton() {
    return this.mCancelButton;
  }

  private jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataSetupPanel mDataSetupPanel;

  /**
   * Set information of data columns.
   *
   * @param sdFile single dimensional array file
   * @param dataType type of data
   * @param colInfoSet data columns
   * @param infoMap a map of information
   * @param showDefault a flag whether to show default column type
   * @return true if succeeded
   */
  public boolean setData(
      SGSDArrayFile sdFile,
      String dataType,
      SGDataColumnInfoSet colInfoSet,
      Map<String, Object> infoMap,
      final boolean showDefault) {

    // set the title
    this.setupTitle(dataType);

    // set data
    if (this.mDataSetupPanel.setData(sdFile, dataType, colInfoSet, infoMap, showDefault) == false) {
      return false;
    }

    // set data name
    final String dataName = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_NAME);
    this.setDataName(dataName);

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

  @Override
  protected SGDataSetupPanel getDataSetupPanel() {
    return this.mDataSetupPanel;
  }

  @Override
  protected SGTextField getDataNameTextField() {
    return this.mDataNameTextField;
  }

  public boolean isStrideAvailable() {
    return this.mDataSetupPanel.isStrideAvailable();
  }
}

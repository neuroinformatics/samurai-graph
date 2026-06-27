package jp.riken.brain.ni.samuraigraph.application;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;

/** Manages wizard dialogs for data addition. Extracted from SGMainFunctions. */
public class SGWizardManager {

  private SGFigureIDSelectionWizardDialog mFigureIDSelectionWizardDialog = null;
  private SGSingleDataFileChooserWizardDialog mSingleDataFileChooserWizardDialog = null;
  private SGFileTypeSelectionWizardDialog mFileTypeSelectionWizardDialog = null;
  private SGDataTypeWizardDialog mDataTypeWizardDialog = null;
  private SGSDArrayDataSetupWizardDialog mSDArrayDataSetupWizardDialog = null;
  private SGNetCDFDataSetupWizardDialog mNetCDFDataSetupWizardDialog = null;
  private SGMDArrayDataSetupWizardDialog mMDArrayDataSetupWizardDialog = null;
  private SGPlotTypeSelectionWizardDialog mPlotTypeSelectionWizardDialog = null;

  public SGFigureIDSelectionWizardDialog getFigureIDSelectionWizardDialog() {
    return mFigureIDSelectionWizardDialog;
  }

  public SGSingleDataFileChooserWizardDialog getSingleDataFileChooserWizardDialog() {
    return mSingleDataFileChooserWizardDialog;
  }

  public SGFileTypeSelectionWizardDialog getFileTypeSelectionWizardDialog() {
    return mFileTypeSelectionWizardDialog;
  }

  public SGDataTypeWizardDialog getDataTypeWizardDialog() {
    return mDataTypeWizardDialog;
  }

  public SGSDArrayDataSetupWizardDialog getSDArrayDataSetupWizardDialog() {
    return mSDArrayDataSetupWizardDialog;
  }

  public SGNetCDFDataSetupWizardDialog getNetCDFDataSetupWizardDialog() {
    return mNetCDFDataSetupWizardDialog;
  }

  public SGMDArrayDataSetupWizardDialog getMDArrayDataSetupWizardDialog() {
    return mMDArrayDataSetupWizardDialog;
  }

  public SGPlotTypeSelectionWizardDialog getPlotTypeSelectionWizardDialog() {
    return mPlotTypeSelectionWizardDialog;
  }

  /**
   * Creates the wizard dialogs for data addition.
   *
   * @param owner the owner window
   * @param currentFileDirectory the current file directory
   * @param listener the action/window listener
   */
  public void createDataAdditionWizardDialogs(
      final SGDrawingWindow owner,
      final String currentFileDirectory,
      final java.util.EventListener listener) {

    if (this.mFigureIDSelectionWizardDialog != null) {
      SGDrawingWindow curOwner = this.mFigureIDSelectionWizardDialog.getOwnerWindow();
      if (curOwner.equals(owner)) {
        return;
      }
    }

    this.mFigureIDSelectionWizardDialog = new SGFigureIDSelectionWizardDialog(owner, true);
    this.mSingleDataFileChooserWizardDialog = new SGSingleDataFileChooserWizardDialog(owner, true);
    this.mDataTypeWizardDialog = new SGDataTypeWizardDialog(owner, true);
    this.mPlotTypeSelectionWizardDialog = new SGPlotTypeSelectionWizardDialog(owner, true);

    this.mSDArrayDataSetupWizardDialog = new SGSDArrayDataSetupWizardDialog(owner, true);
    this.mSDArrayDataSetupWizardDialog.setPrevious(this.mDataTypeWizardDialog);

    this.mNetCDFDataSetupWizardDialog = new SGNetCDFDataSetupWizardDialog(owner, true);
    this.mNetCDFDataSetupWizardDialog.setPrevious(this.mDataTypeWizardDialog);

    this.mMDArrayDataSetupWizardDialog = new SGMDArrayDataSetupWizardDialog(owner, true);
    this.mMDArrayDataSetupWizardDialog.setPrevious(this.mDataTypeWizardDialog);

    this.mFileTypeSelectionWizardDialog = new SGFileTypeSelectionWizardDialog(owner, true);

    this.mFigureIDSelectionWizardDialog.setPrevious(null);

    String path = currentFileDirectory;
    this.mSingleDataFileChooserWizardDialog.setCurrentFile(path, null);

    this.mFigureIDSelectionWizardDialog.addActionListener((java.awt.event.ActionListener) listener);
    this.mSingleDataFileChooserWizardDialog.addActionListener(
        (java.awt.event.ActionListener) listener);
    this.mFileTypeSelectionWizardDialog.addActionListener((java.awt.event.ActionListener) listener);
    this.mDataTypeWizardDialog.addActionListener((java.awt.event.ActionListener) listener);
    this.mSDArrayDataSetupWizardDialog.addActionListener((java.awt.event.ActionListener) listener);
    this.mNetCDFDataSetupWizardDialog.addActionListener((java.awt.event.ActionListener) listener);
    this.mMDArrayDataSetupWizardDialog.addActionListener((java.awt.event.ActionListener) listener);
    this.mPlotTypeSelectionWizardDialog.addActionListener((java.awt.event.ActionListener) listener);

    this.mFigureIDSelectionWizardDialog.addWindowListener((java.awt.event.WindowListener) listener);
    this.mSingleDataFileChooserWizardDialog.addWindowListener(
        (java.awt.event.WindowListener) listener);
    this.mFileTypeSelectionWizardDialog.addWindowListener((java.awt.event.WindowListener) listener);
    this.mDataTypeWizardDialog.addWindowListener((java.awt.event.WindowListener) listener);
    this.mSDArrayDataSetupWizardDialog.addWindowListener((java.awt.event.WindowListener) listener);
    this.mNetCDFDataSetupWizardDialog.addWindowListener((java.awt.event.WindowListener) listener);
    this.mMDArrayDataSetupWizardDialog.addWindowListener((java.awt.event.WindowListener) listener);
    this.mPlotTypeSelectionWizardDialog.addWindowListener((java.awt.event.WindowListener) listener);

    this.mFigureIDSelectionWizardDialog.pack();
    this.mSingleDataFileChooserWizardDialog.pack();
    this.mFileTypeSelectionWizardDialog.pack();
  }

  /** Checks if the given object is one of the wizard dialogs. */
  public boolean isWizardDialog(Object obj) {
    return obj.equals(this.mFigureIDSelectionWizardDialog)
        || obj.equals(this.mSingleDataFileChooserWizardDialog)
        || obj.equals(this.mFileTypeSelectionWizardDialog)
        || obj.equals(this.mDataTypeWizardDialog)
        || obj.equals(this.mSDArrayDataSetupWizardDialog)
        || obj.equals(this.mNetCDFDataSetupWizardDialog)
        || obj.equals(this.mMDArrayDataSetupWizardDialog)
        || obj.equals(this.mPlotTypeSelectionWizardDialog);
  }

  /** Checks if any wizard dialog is currently showing. */
  public boolean isAnyWizardShowing() {
    return (mFigureIDSelectionWizardDialog != null && mFigureIDSelectionWizardDialog.isVisible())
        || (mSingleDataFileChooserWizardDialog != null
            && mSingleDataFileChooserWizardDialog.isVisible())
        || (mFileTypeSelectionWizardDialog != null && mFileTypeSelectionWizardDialog.isVisible())
        || (mDataTypeWizardDialog != null && mDataTypeWizardDialog.isVisible())
        || (mSDArrayDataSetupWizardDialog != null && mSDArrayDataSetupWizardDialog.isVisible())
        || (mNetCDFDataSetupWizardDialog != null && mNetCDFDataSetupWizardDialog.isVisible())
        || (mMDArrayDataSetupWizardDialog != null && mMDArrayDataSetupWizardDialog.isVisible())
        || (mPlotTypeSelectionWizardDialog != null && mPlotTypeSelectionWizardDialog.isVisible());
  }
}

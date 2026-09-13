package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationTextConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGAnimationConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDateConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGPaintConstant.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataInformationKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataPropertyKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGNetCDFConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGArrowConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGFigureDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSXYDataConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGShapeConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGStringConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSymbolConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGTimingLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGVXYDataConstants.*;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;

/** The base class of the dialog to set up the data. */
public abstract class SGDataSetupDialog extends SGDataDialog {

  /** Serial Version UID */
  private static final long serialVersionUID = 3424613979566257636L;

  /**
   * Builds this dialog.
   *
   * @param owner the owner of this dialog
   * @param modal true for modal dialog
   */
  public SGDataSetupDialog(Dialog owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Builds this dialog.
   *
   * @param owner the owner of this dialog
   * @param modal true for modal dialog
   */
  public SGDataSetupDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /** An array of selected data column information. */
  protected SGDataColumnInfo[] mDataColumnInfo = null;

  /** Returns an array of data column information. */
  public SGDataColumnInfo[] getDataColumnInfo() {
    if (this.mDataColumnInfo == null) {
      return null;
    } else {
      return (SGDataColumnInfo[]) mDataColumnInfo.clone();
    }
  }

  /** Called when the escape key is typed. */
  protected void onEscKeyTyped() {
    this.setVisible(false);
  }

  public SGDataColumnInfoSet getDataColumnInfoSet() {
    return this.getTableHolder().getDataColumnInfoSet();
  }

  /**
   * Called when the component is resized.
   *
   * @param e a component event
   */
  public void componentResized(ComponentEvent e) {
    super.componentResized(e);
    this.updateTable();
  }

  /**
   * Called when the component is shown.
   *
   * @param e a component event
   */
  public void componentShown(ComponentEvent e) {
    super.componentShown(e);
    this.updateTable();
  }

  /** Update the table. */
  private void updateTable() {
    this.getTableHolder().updateTable();
  }

  /** Called when an action event is generated. */
  @Override
  public void actionPerformed(ActionEvent e) {
    super.actionPerformed(e);
    Object source = e.getSource();
    String command = e.getActionCommand();
    if (OK_BUTTON_TEXT.equals(command)) {

      // get selected items and set them to the attribute
      this.mDataColumnInfo = this.getTableHolder().getDataColumnInfoSet().getDataColumnInfoArray();

      // notify to the listener
      this.notifyToListener(OK_BUTTON_TEXT);

      // hide this dialog
      this.setVisible(false);

    } else if (CANCEL_BUTTON_TEXT.equals(command)) {
      this.setVisible(false);
    } else if (source.equals(this.getTableHolder().getClearButton())) {
      this.getOKButton().setEnabled(false);
    } else if (source.equals(this.getTableHolder().getRestoreButton())) {
      this.getOKButton().setEnabled(true);
    } else if (source.equals(this.getTableHolder().getComplementButton())) {
      this.getOKButton().setEnabled(true);
    }
  }

  protected void setupTitle(String dataType) {
    String title = SGDataTextUtility.createTitleString("Set up the Data ", dataType);
    this.setTitle(title);
  }
}

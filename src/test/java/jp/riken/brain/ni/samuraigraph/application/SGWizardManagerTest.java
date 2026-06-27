package jp.riken.brain.ni.samuraigraph.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class SGWizardManagerTest {

  // -- Constructor and initial state --

  @Test
  void constructor_initialState_allNull() {
    SGWizardManager manager = new SGWizardManager();

    assertThat(manager.getFigureIDSelectionWizardDialog()).isNull();
    assertThat(manager.getSingleDataFileChooserWizardDialog()).isNull();
    assertThat(manager.getFileTypeSelectionWizardDialog()).isNull();
    assertThat(manager.getDataTypeWizardDialog()).isNull();
    assertThat(manager.getSDArrayDataSetupWizardDialog()).isNull();
    assertThat(manager.getNetCDFDataSetupWizardDialog()).isNull();
    assertThat(manager.getMDArrayDataSetupWizardDialog()).isNull();
    assertThat(manager.getPlotTypeSelectionWizardDialog()).isNull();
  }

  @Test
  void constructor_initialState_isAnyWizardShowingFalse() {
    SGWizardManager manager = new SGWizardManager();

    assertThat(manager.isAnyWizardShowing()).isFalse();
  }

  // -- isWizardDialog --

  @Test
  void isWizardDialog_nullThrowsNPE() {
    SGWizardManager manager = new SGWizardManager();

    assertThatThrownBy(() -> manager.isWizardDialog(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void isWizardDialog_stringReturnsFalse() {
    SGWizardManager manager = new SGWizardManager();

    assertThat(manager.isWizardDialog("not a wizard")).isFalse();
  }

  @Test
  void isWizardDialog_integerReturnsFalse() {
    SGWizardManager manager = new SGWizardManager();

    assertThat(manager.isWizardDialog(42)).isFalse();
  }

  @Test
  void isWizardDialog_newObjectReturnsFalse() {
    SGWizardManager manager = new SGWizardManager();
    Object obj = new Object();

    assertThat(manager.isWizardDialog(obj)).isFalse();
  }

  // -- isAnyWizardShowing --

  @Test
  void isAnyWizardShowing_noDialogsReturnsFalse() {
    SGWizardManager manager = new SGWizardManager();

    assertThat(manager.isAnyWizardShowing()).isFalse();
  }

  @Test
  void isAnyWizardShowing_allNullReturnsFalse() {
    SGWizardManager manager = new SGWizardManager();

    // All fields are null by default, explicitly verify
    assertThat(manager.isAnyWizardShowing()).isFalse();
  }
}

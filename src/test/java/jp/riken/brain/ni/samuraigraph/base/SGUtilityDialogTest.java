package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.border.MatteBorder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * Tests for the modal display helpers of {@link SGUtility}. The dialogs themselves are stubbed with
 * a static mock of {@link JOptionPane} (or a mock dialog for the color selection dialog), so the
 * tests never open a real window and are safe in a headless environment.
 */
class SGUtilityDialogTest {

  private static final int ARBITRARY_CHOICE = 42;

  private static final String[] YES_NO = {"Yes", "No"};

  @AfterEach
  void resetMessageDialogFlag() {
    SGUtility.clearMessageDialogVisible();
  }

  // -- message dialogs -----------------------------------------------------------

  @Test
  void showMessageDialogShowsOkDialogAndSetsVisibilityFlag() {
    try (MockedStatic<JOptionPane> options = mockStatic(JOptionPane.class)) {
      SGUtility.showMessageDialog(null, "boom", "Title", JOptionPane.ERROR_MESSAGE);
      options.verify(
          () ->
              JOptionPane.showOptionDialog(
                  isNull(),
                  eq("boom"),
                  eq("Title"),
                  eq(JOptionPane.OK_OPTION),
                  eq(JOptionPane.ERROR_MESSAGE),
                  any(),
                  any(),
                  any()));
      assertTrue(SGUtility.wasMessageDialogVisible());
    }
  }

  @Test
  void showErrorMessageDialogDelegatesWithErrorType() {
    SGUtility.clearMessageDialogVisible();
    try (MockedStatic<JOptionPane> options = mockStatic(JOptionPane.class)) {
      SGUtility.showErrorMessageDialog(null, "boom", "Title");
      options.verify(
          () ->
              JOptionPane.showOptionDialog(
                  isNull(),
                  eq("boom"),
                  eq("Title"),
                  eq(JOptionPane.OK_OPTION),
                  eq(JOptionPane.ERROR_MESSAGE),
                  any(),
                  any(),
                  any()));
      assertTrue(SGUtility.wasMessageDialogVisible());
    }
  }

  @Test
  void showWarningMessageDialogDelegatesWithWarningType() {
    try (MockedStatic<JOptionPane> options = mockStatic(JOptionPane.class)) {
      SGUtility.showWarningMessageDialog(null, "watch out", "Title");
      options.verify(
          () ->
              JOptionPane.showOptionDialog(
                  isNull(),
                  eq("watch out"),
                  eq("Title"),
                  eq(JOptionPane.OK_OPTION),
                  eq(JOptionPane.WARNING_MESSAGE),
                  any(),
                  any(),
                  any()));
      assertTrue(SGUtility.wasMessageDialogVisible());
    }
  }

  @Test
  void showIllegalInputErrorMessageDialogUsesDefaultAndCustomMessage() {
    try (MockedStatic<JOptionPane> options = mockStatic(JOptionPane.class)) {
      SGUtility.showIllegalInputErrorMessageDialog(null);
      options.verify(
          () ->
              JOptionPane.showOptionDialog(
                  isNull(),
                  eq("The input value is illegal."),
                  eq("Illegal input"),
                  eq(JOptionPane.OK_OPTION),
                  eq(JOptionPane.ERROR_MESSAGE),
                  any(),
                  any(),
                  any()));
      SGUtility.showIllegalInputErrorMessageDialog(null, "custom text");
      options.verify(
          () ->
              JOptionPane.showOptionDialog(
                  isNull(),
                  eq("custom text"),
                  eq("Illegal input"),
                  eq(JOptionPane.OK_OPTION),
                  eq(JOptionPane.ERROR_MESSAGE),
                  any(),
                  any(),
                  any()));
    }
  }

  @Test
  void showFileNotFoundMessageDialogUsesFixedMessageAndTitle() {
    try (MockedStatic<JOptionPane> options = mockStatic(JOptionPane.class)) {
      SGUtility.showFileNotFoundMessageDialog(null);
      options.verify(
          () ->
              JOptionPane.showOptionDialog(
                  isNull(),
                  eq("Failed to open the file denoted by a specified pathname."),
                  eq("File not found"),
                  eq(JOptionPane.OK_OPTION),
                  eq(JOptionPane.ERROR_MESSAGE),
                  any(),
                  any(),
                  any()));
    }
  }

  // -- confirmation dialogs -------------------------------------------------------

  @Test
  void showConfirmationDialogReturnsTheChosenOption() {
    try (MockedStatic<JOptionPane> options = mockStatic(JOptionPane.class)) {
      Object[] opts = {"Alpha", "Beta"};
      options
          .when(
              () ->
                  JOptionPane.showOptionDialog(
                      null,
                      "confirm?",
                      SGConstants.TITLE_CONFIRMATION,
                      JOptionPane.YES_NO_OPTION,
                      JOptionPane.QUESTION_MESSAGE,
                      null,
                      opts,
                      JOptionPane.YES_OPTION))
          .thenReturn(ARBITRARY_CHOICE);
      assertEquals(ARBITRARY_CHOICE, SGUtility.showConfirmationDialog(null, "confirm?", opts));
    }
  }

  @Test
  void showYesNoConfirmationDialogUsesYesNoOptions() {
    try (MockedStatic<JOptionPane> options = mockStatic(JOptionPane.class)) {
      options
          .when(
              () ->
                  JOptionPane.showOptionDialog(
                      isNull(),
                      eq("proceed?"),
                      eq(SGConstants.TITLE_CONFIRMATION),
                      eq(JOptionPane.YES_NO_OPTION),
                      eq(JOptionPane.QUESTION_MESSAGE),
                      isNull(),
                      any(),
                      eq(JOptionPane.YES_OPTION)))
          .thenReturn(JOptionPane.NO_OPTION);
      assertEquals(JOptionPane.NO_OPTION, SGUtility.showYesNoConfirmationDialog(null, "proceed?"));
      options.verify(
          () ->
              JOptionPane.showOptionDialog(
                  isNull(),
                  eq("proceed?"),
                  eq(SGConstants.TITLE_CONFIRMATION),
                  eq(JOptionPane.YES_NO_OPTION),
                  eq(JOptionPane.QUESTION_MESSAGE),
                  isNull(),
                  argThat(optionsArray -> Arrays.deepEquals(optionsArray, YES_NO)),
                  eq(JOptionPane.YES_OPTION)));
    }
  }

  // -- color selection dialog ------------------------------------------------------

  @Test
  void showColorSelectionDialogPositionsAndShowsHiddenDialog() {
    SGColorDialog dialog = mock(SGColorDialog.class);
    SGColorSelectionButton button = new SGColorSelectionButton();
    SGUtility.showColorSelectionDialog(dialog, button, Color.RED, 12, 34);
    verify(dialog).setSelectedColor(Color.RED);
    verify(dialog).setLocation(12, 34);
    verify(dialog).setVisible(true);
    assertInstanceOf(MatteBorder.class, button.getBorder());
  }

  @Test
  void showColorSelectionDialogKeepsVisibleDialogUntouched() {
    SGColorDialog dialog = mock(SGColorDialog.class);
    when(dialog.isVisible()).thenReturn(true);
    SGUtility.showColorSelectionDialog(dialog, null, Color.BLUE, 0, 0);
    verify(dialog).setSelectedColor(Color.BLUE);
    verify(dialog, never()).setLocation(anyInt(), anyInt());
    verify(dialog, never()).setVisible(anyBoolean());
  }

  // -- window positioning ----------------------------------------------------------

  @Test
  void setCenterPlacesWindowAtCenterOfOwner() {
    Window owner = mock(Window.class);
    Window window = mock(Window.class);
    when(owner.getSize()).thenReturn(new Dimension(100, 100));
    when(owner.getX()).thenReturn(0);
    when(owner.getY()).thenReturn(0);
    when(window.getWidth()).thenReturn(20);
    when(window.getHeight()).thenReturn(30);
    SGUtility.setCenter(window, owner);
    verify(window).setLocation(40, 35);
  }

  @Test
  void setCenterOffsetsByOwnerPosition() {
    Window owner = mock(Window.class);
    Window window = mock(Window.class);
    when(owner.getSize()).thenReturn(new Dimension(100, 100));
    when(owner.getX()).thenReturn(10);
    when(owner.getY()).thenReturn(20);
    when(window.getWidth()).thenReturn(40);
    when(window.getHeight()).thenReturn(50);
    SGUtility.setCenter(window, owner);
    verify(window).setLocation(40, 45);
  }
}

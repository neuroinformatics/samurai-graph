package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.event.MouseEvent;
import javax.swing.JFormattedTextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests of the caret position adjuster of the spinner. */
class SGSpinnerCaretPositionAdjusterTest {

  private SGSpinner spinner;

  @BeforeEach
  void createSpinner() {
    this.spinner = new SGSpinner();
  }

  @Test
  void runReturnsOnEmptyText() {
    this.spinner.getFormattedTextField().setText("");
    this.spinner.new CaretPositionAdjuster().run();
    assertEquals(0, this.spinner.getFormattedTextField().getCaretPosition());
  }

  @Test
  void runMovesTheCaretToTheEndWithoutASuffix() {
    this.spinner.getFormattedTextField().setText("12345");
    this.spinner.new CaretPositionAdjuster().run();
    assertEquals(5, this.spinner.getFormattedTextField().getCaretPosition());
  }

  @Test
  void runMovesTheCaretBeforeTheUnitSuffix() {
    this.spinner.getFormattedTextField().setText("12345 cm");
    this.spinner.new CaretPositionAdjuster().run();
    assertEquals(5, this.spinner.getFormattedTextField().getCaretPosition());
  }

  @Test
  void runUsesThePressedPointAndClearsIt() {
    this.spinner.getFormattedTextField().setText("123");
    // the far right point hits the end of the text
    this.spinner.mousePressed(
        new MouseEvent(this.spinner, MouseEvent.MOUSE_PRESSED, 0L, 0, 1000, 10, 1, false));
    this.spinner.new CaretPositionAdjuster().run();
    assertEquals(3, this.spinner.getFormattedTextField().getCaretPosition());
    // the pressed point is consumed, so a second run uses the suffix path
    this.spinner.new CaretPositionAdjuster().run();
    assertEquals(3, this.spinner.getFormattedTextField().getCaretPosition());
  }

  @Test
  void mousePressedRecordsThePoint() {
    this.spinner.getFormattedTextField().setText("123");
    final JFormattedTextField ftf = this.spinner.getFormattedTextField();
    this.spinner.mousePressed(
        new MouseEvent(this.spinner, MouseEvent.MOUSE_PRESSED, 0L, 0, 1, 10, 1, false));
    this.spinner.new CaretPositionAdjuster().run();
    // the left point hits the beginning of the text
    assertEquals(0, ftf.getCaretPosition());
  }
}

package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.awt.event.MouseEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Mouse interaction tests for the figure axis handler. */
class SGAxisMouseHandlerTest {

  private SGFigureAxis axisElement;

  private SGAxisMouseHandler handler;

  @BeforeEach
  void setUp() {
    this.axisElement = new SGFigureAxis(new SGFigureElementAxis());
    this.handler = new SGAxisMouseHandler(this.axisElement);
  }

  @AfterEach
  void tearDown() {
    if (this.axisElement != null) {
      this.axisElement.dispose();
      this.axisElement = null;
    }
  }

  private MouseEvent createMouseClickedEvent(int x, int y) {
    return new MouseEvent(
        new javax.swing.JPanel(),
        MouseEvent.MOUSE_CLICKED,
        System.currentTimeMillis(),
        0,
        x,
        y,
        1,
        false);
  }

  @Test
  void clickOutsideTheAxisIsNotHandled() {
    MouseEvent event = this.createMouseClickedEvent(10_000, 10_000);
    assertFalse(this.handler.onMouseClicked(event));
  }
}

package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.geom.Rectangle2D;
import org.junit.jupiter.api.Test;

/** Regression tests for element bounds updates in {@link SGDrawingElementString}. */
class SGDrawingElementStringTest {

  @Test
  void boundsAreUpdatedWhenLocationIsSet() {
    SGDrawingElementString el = new SGDrawingElementString("Hello");
    Rectangle2D before = el.getElementBounds();
    assertTrue(before.getWidth() > 0.0, "initial width");
    assertTrue(el.setLocation(100.0f, 50.0f));
    Rectangle2D after = el.getElementBounds();
    assertTrue(after.getX() >= 99.0f, "bounds x after setLocation: " + after.getX());
    assertTrue(after.getY() >= 49.0f, "bounds y after setLocation: " + after.getY());
  }

  @Test
  void boundsAreUpdatedWhenLocationTupleIsSet() {
    SGDrawingElementString el = new SGDrawingElementString("Hello");
    assertTrue(el.setLocation(80.0f, 40.0f));
    Rectangle2D rect = el.getElementBounds();
    assertTrue(rect.getX() >= 79.0f, "bounds x: " + rect.getX());
    assertTrue(rect.getY() >= 39.0f, "bounds y: " + rect.getY());
  }

  @Test
  void boundsAreUpdatedWhenStringIsSet() {
    SGDrawingElementString el = new SGDrawingElementString("a");
    double w0 = el.getElementBounds().getWidth();
    assertTrue(el.setString("abcdefghijklmnopqrstuvwxyz"));
    double w1 = el.getElementBounds().getWidth();
    assertTrue(w1 > w0, "wider bounds after longer string: " + w0 + " -> " + w1);
  }

  @Test
  void boundsAreUpdatedWhenFontIsSet() {
    SGDrawingElementString el = new SGDrawingElementString("Hello");
    double h0 = el.getElementBounds().getHeight();
    assertTrue(el.setFont("Dialog", java.awt.Font.PLAIN, 30.0f));
    double h1 = el.getElementBounds().getHeight();
    assertTrue(h1 > h0, "taller bounds after larger font: " + h0 + " -> " + h1);
  }

  @Test
  void boundsAreUpdatedWhenMagnificationIsSet() {
    SGDrawingElementString el = new SGDrawingElementString("Hello");
    double h0 = el.getElementBounds().getHeight();
    assertTrue(el.setMagnification(2.0f));
    double h1 = el.getElementBounds().getHeight();
    assertTrue(h1 > h0, "taller bounds after magnification: " + h0 + " -> " + h1);
  }
}

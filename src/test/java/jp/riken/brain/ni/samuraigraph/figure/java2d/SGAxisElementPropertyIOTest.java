package jp.riken.brain.ni.samuraigraph.figure.java2d;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Font;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleValue;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the property round trip of {@link SGAxisElement}. */
class SGAxisElementPropertyIOTest {

  private SGFigureElementAxis axisElement;

  private SGFigureAxis createAxis() {
    this.axisElement = new SGFigureElementAxis();
    SGFigureAxis axis = new SGFigureAxis(this.axisElement);
    axis.mAxis = new SGAxis(new SGTuple2d(0.0, 10.0));
    return axis;
  }

  @AfterEach
  void disposeAxisElement() {
    if (this.axisElement != null) {
      this.axisElement.dispose();
    }
  }

  @Test
  void axisElementCanBeCreatedAndDisposed() {
    SGFigureElementAxis element = new SGFigureElementAxis();
    assertNotNull(element);
    element.dispose();
  }

  @Test
  void axisPropertiesRoundTripPreservesValues() {
    SGFigureAxis axis = this.createAxis();

    SGAxisElement.AxisProperties ap = new FigureAxisProperties();
    ap.axisVisible = true;
    ap.axisLineVisible = true;
    ap.axisLineWidth = 2.0f;
    ap.axisLineColor = Color.BLUE;
    ap.spaceLineAndNumbers = 5.0f;
    ap.titleVisible = true;
    ap.titleText = "X Axis";
    ap.spaceTitleAndNumbers = 3.0f;
    ap.titleShiftFromCenter = 1.5f;
    ap.titleFontName = "Serif";
    ap.titleFontSize = 12.0f;
    ap.titleFontStyle = Font.BOLD;
    ap.titleFontColor = Color.RED;
    ap.numberVisible = true;
    ap.numberAngle = 45.0f;
    ap.numberFontName = "SansSerif";
    ap.numberFontSize = 10.0f;
    ap.numberFontStyle = Font.ITALIC;
    ap.numberFontColor = Color.GREEN;
    ap.tickMarkVisible = true;
    ap.tickMarkWidth = 1.0f;
    ap.tickMarkBothsides = false;
    ap.tickMarkLength = 4.0f;
    ap.minorTickMarkNumber = 1;
    ap.minorTickMarkLength = 2.0f;
    ap.tickMarkColor = Color.BLACK;
    ap.invertedCoordinates = false;
    ap.minValue = new SGAxisDoubleValue(0.0);
    ap.maxValue = new SGAxisDoubleValue(10.0);
    ap.scaleType = 0;
    ap.stepValue = new SGAxisDoubleStepValue(1.0);
    ap.baselineValue = new SGAxisDoubleValue(0.0);

    assertTrue(axis.setProperties(ap));

    SGAxisElement.AxisProperties restored = new FigureAxisProperties();
    assertTrue(axis.getProperties(restored));
    assertTrue(restored.axisVisible);
    assertTrue(restored.axisLineVisible);
    assertEquals(2.0f, restored.axisLineWidth, 0.001f);
    assertEquals(Color.BLUE, restored.axisLineColor);
    assertEquals("X Axis", restored.titleText);
    assertEquals("Serif", restored.titleFontName);
    assertEquals(12.0f, restored.titleFontSize, 0.001f);
    assertEquals(Font.BOLD, restored.titleFontStyle);
    assertEquals(Color.RED, restored.titleFontColor);
    assertTrue(restored.numberVisible);
    assertEquals("SansSerif", restored.numberFontName);
    assertEquals(10.0f, restored.numberFontSize, 0.001f);
    assertEquals(Color.GREEN, restored.numberFontColor);
    assertTrue(restored.tickMarkVisible);
    assertEquals(1.0f, restored.tickMarkWidth, 0.001f);
    assertFalse(restored.tickMarkBothsides);
    assertEquals(4.0f, restored.tickMarkLength, 0.001f);
    assertEquals(1, restored.minorTickMarkNumber);
    assertEquals(2.0f, restored.minorTickMarkLength, 0.001f);
    assertEquals(Color.BLACK, restored.tickMarkColor);
    assertFalse(restored.invertedCoordinates);
  }

  @Test
  void getPropertiesRejectsNonAxisProperties() {
    SGFigureAxis axis = this.createAxis();
    assertFalse(axis.getProperties(new SGProperties() {}));
  }
}

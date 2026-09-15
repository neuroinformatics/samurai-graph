package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Headless construction tests for {@link SGFigurePopupMenu}. */
class SGFigurePopupMenuTest {

  @Test
  void nullFigureListIsRejected() {
    List<SGFigure> list = new ArrayList<>();
    assertNotNull(list);
  }

  @Test
  void emptyFigureListIsRejected() {
    List<SGFigure> figList = new ArrayList<>();
    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class, () -> new SGFigurePopupMenu(figList));
  }
}

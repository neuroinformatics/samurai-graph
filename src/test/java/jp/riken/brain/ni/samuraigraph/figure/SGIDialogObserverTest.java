package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGIDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGIArrowDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGIArrowPanelObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGIAxisBreakDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGIAxisDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGIAxisPanelObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGIAxisScaleDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGIColorBarDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGIDataPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGILabelDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGILegendDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGILineStyleDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGILineStylePropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGIRectangularShapeDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGISXYDataDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGISXYZDataDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGISignificantDifferenceDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGIStrokeEditDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGITimingLineDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGIVXYDataDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGIXYFigureDialogObserver;
import org.junit.jupiter.api.Test;

/** The per-dialog observer contracts share the common dialog observer type. */
class SGIDialogObserverTest {

  @Test
  void allDialogObserversExtendTheCommonType() {
    final Class<?>[] observers = {
      SGIArrowDialogObserver.class,
      SGIArrowPanelObserver.class,
      SGIAxisBreakDialogObserver.class,
      SGIAxisDialogObserver.class,
      SGIAxisPanelObserver.class,
      SGIAxisScaleDialogObserver.class,
      SGIColorBarDialogObserver.class,
      SGIDataPropertyDialogObserver.class,
      SGILabelDialogObserver.class,
      SGILegendDialogObserver.class,
      SGILineStyleDialogObserver.class,
      SGILineStylePropertyDialogObserver.class,
      SGIRectangularShapeDialogObserver.class,
      SGISignificantDifferenceDialogObserver.class,
      SGIStrokeEditDialogObserver.class,
      SGISXYDataDialogObserver.class,
      SGISXYZDataDialogObserver.class,
      SGITimingLineDialogObserver.class,
      SGIVXYDataDialogObserver.class,
      SGIXYFigureDialogObserver.class,
    };
    for (Class<?> observer : observers) {
      assertTrue(
          SGIDialogObserver.class.isAssignableFrom(observer),
          observer.getName() + " is not a " + SGIDialogObserver.class.getSimpleName());
    }
  }
}

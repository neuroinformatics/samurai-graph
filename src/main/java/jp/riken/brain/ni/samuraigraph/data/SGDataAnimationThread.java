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

import jp.riken.brain.ni.samuraigraph.base.SGAnimationThread;
import jp.riken.brain.ni.samuraigraph.base.SGIAnimation;

/** A class of the animation. */
public class SGDataAnimationThread extends SGAnimationThread {

  /**
   * Builds an animation thread.
   *
   * @param animations an array of animation data source
   */
  public SGDataAnimationThread(SGIAnimation[] animations) {
    super(animations);
  }

  /**
   * Builds an animation thread with the same as given animation thread.
   *
   * @param th an animation thread to copy
   */
  public SGDataAnimationThread(SGAnimationThread th) {
    super(th);
  }

  /** Saves all changes of this data source. */
  public void saveChanges() {
    for (SGIAnimation animation : this.mAnimations) {
      SGIDataAnimation dAnim = (SGIDataAnimation) animation;
      dAnim.saveChanges();
    }
  }

  /** Cancels all changes of this data source. */
  public void cancelChanges() {
    for (SGIAnimation animation : this.mAnimations) {
      SGIDataAnimation dAnim = (SGIDataAnimation) animation;
      dAnim.cancelChanges();
    }
  }
}

package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGAnimationThread;
import jp.riken.brain.ni.samuraigraph.base.SGIAnimation;

/**
 * A class of the animation.
 * 
 */
public class SGDataAnimationThread extends SGAnimationThread {

    /**
     * Builds an animation thread.
     * 
     * @param animations
     *           an array of animation data source
     */
    public SGDataAnimationThread(SGIAnimation[] animations) {
        super(animations);
    }

    /**
     * Builds an animation thread with the same as given animation thread.
     * 
     * @param th
     *          an animation thread to copy
     */
    public SGDataAnimationThread(SGAnimationThread th) {
        super(th);
    }

    /**
     * Saves all changes of this data source.
     *
     */
    public void saveChanges() {
    	for (SGIAnimation animation : this.mAnimations) {
            SGIDataAnimation dAnim = (SGIDataAnimation) animation;
            dAnim.saveChanges();
    	}
    }

    /**
     * Cancels all changes of this data source.
     *
     */
    public void cancelChanges() {
    	for (SGIAnimation animation : this.mAnimations) {
            SGIDataAnimation dAnim = (SGIDataAnimation) animation;
            dAnim.cancelChanges();
    	}
    }

}

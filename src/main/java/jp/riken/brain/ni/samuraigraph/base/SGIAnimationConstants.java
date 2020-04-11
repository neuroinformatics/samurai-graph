package jp.riken.brain.ni.samuraigraph.base;

/**
 * An interface for the animation.
 *
 */
public interface SGIAnimationConstants {

    public static final double FRAME_RATE_INIT = 1.0;

    public static final double FRAME_RATE_MIN = 0.0;
    
    public static final double FRAME_RATE_MAX = 100.0;
    
    public static final double FRAME_RATE_STEP = 1.0;
    
    public static final int FRAME_RATE_FRAC_DIFIT_MIN = 1;
    
    public static final int FRAME_RATE_FRAC_DIFIT_MAX = 2;
    
    public static final int FRAME_RATE_MINIMAL_ORDER = - FRAME_RATE_FRAC_DIFIT_MAX;

}

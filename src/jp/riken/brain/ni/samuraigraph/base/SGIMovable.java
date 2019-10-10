package jp.riken.brain.ni.samuraigraph.base;

/**
 * An interface for selectable and movable objects.
 * 
 */
public interface SGIMovable extends SGISelectable {
	
    /**
     * Translates this object.
     * 
     * @param dx
     *           the x-axis displacement
     * @param dy
     *           the y-axis displacement
     */
    public void translate(float dx, float dy);

}

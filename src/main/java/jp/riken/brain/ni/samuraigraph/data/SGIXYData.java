package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGIData;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;

/**
 * An interface for the data with X and Y axes.
 *
 */
public interface SGIXYData extends SGIData {

    /**
     * Returns the bounds of x-values.
     * 
     * @return the bounds of x-values
     */
    public SGValueRange getBoundsX();

    /**
     * Returns the bounds of y-values.
     * 
     * @return the bounds of y-values
     */
    public SGValueRange getBoundsY();

    /**
     * Returns the bounds of x-values for all animation frames.
     * 
     * @return the bounds of x-values
     */
    public SGValueRange getAllAnimationFrameBoundsX();

    /**
     * Returns the bounds of y-values for all animation frames.
     * 
     * @return the bounds of y-values
     */
    public SGValueRange getAllAnimationFrameBoundsY();

    /**
     * Returns the title of X values.
     * 
     * @return the title of X values
     */
    public String getTitleX();
    
    /**
     * Returns the title of Y values.
     * 
     * @return the title of Y values
     */
    public String getTitleY();
    
    /**
     * Returns a text string of the unit for X values.
     * 
     * @return a text string of the unit for X values
     */
    public String getUnitsStringX();

    /**
     * Returns a text string of the unit for Y values.
     * 
     * @return a text string of the unit for Y values
     */
    public String getUnitsStringY();

}

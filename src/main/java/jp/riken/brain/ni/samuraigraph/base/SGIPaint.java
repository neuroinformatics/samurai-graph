package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

/**
 * An interface of inner painting.
 * 
 * @author minemoto
 *
 */
public interface SGIPaint extends Cloneable, SGIPaintConstant {
    
    public float getAlpha();
    
    /**
     * Set the transparency.
     * 
     * @param alpha
     * @return true if it succeeds.
     */
    public boolean setAlpha(final float alpha);
    
    /**
     * Set the magnification.
     * @param mag the magnification.
     * @return true if it succeeds.
     */
    public boolean setMagnification(float mag);
    
    public Paint getPaint(final Rectangle2D rect);
    
    public Object clone() throws CloneNotSupportedException;
    
    public SGPropertyMap getPropertyFileMap();
    
    public boolean writeProperty(final Element el);

}

package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

/**
 * Paint filled with Color.WHITE.
 * 
 * @author minemoto
 *
 */
public class SGNullPaint extends SGTransparentPaint {
    
    private final Color mColor;
    
    public SGNullPaint() {
        this(Color.WHITE);
    }
    
    public SGNullPaint(Color defaultColor) {
        this.mColor = defaultColor;
    }

    @Override
    public Paint getPaint(Rectangle2D rect) {
        return this.mColor;
    }
}

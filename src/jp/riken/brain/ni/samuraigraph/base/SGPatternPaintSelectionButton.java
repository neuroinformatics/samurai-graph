package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A button used for the selection of the color pattern.
 */
public class SGPatternPaintSelectionButton extends SGButton
implements ChangeListener {
    
    private static final long serialVersionUID = -4406790190370909484L;
    
    private SGPatternPaint mPatternPaint;
    
    /**
     * Creates a button.
     */
    public SGPatternPaintSelectionButton() {
        super();
        this.setBorder(new LineBorder(Color.BLACK, 1));
        this.init();
    }
    
    private void init() {
        this.mPatternPaint = new SGPatternPaint();
    }
    
    /**
     * Returns the pattern if the pattern is "set" to this button.
     * 
     * @return Paint object.
     */
    public SGPatternPaint getPatternPaint() {
        try {
            return (SGPatternPaint)this.mPatternPaint.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
    }
    
    /**
     * If the gradation is set with this method, the gradation is "set" to this button.
     * 
     * @param gradation
     *           gradation to be set to this button
     */
    public void setPatternPaint(SGPatternPaint pattern) {
        if (null!=pattern && !this.mPatternPaint.equals(pattern)) {
            try {
                this.mPatternPaint = (SGPatternPaint)pattern.clone();
            } catch (CloneNotSupportedException e) {
                throw new InternalError(e.getMessage());
            }
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (g != null && getModel().isEnabled()) {
            Graphics2D g2 = (Graphics2D)g;
            Rectangle2D rect2d = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
            g2.setPaint(this.mPatternPaint.getOpaquePaint(rect2d, 1.0f));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (source instanceof SGPatternPaintDialog) {
            SGPatternPaint ppaint = ((SGPatternPaintDialog)source).getPatternPaint();
            if (! this.mPatternPaint.equals(ppaint)) {
                this.mPatternPaint = ppaint;
                repaint();
            }
        }
    }

}

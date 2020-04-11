package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A button used for the selection of the color gradation.
 */
public class SGGradationPaintSelectionButton extends SGButton
implements ChangeListener {
    
    private static final long serialVersionUID = -3123341147693786400L;
    
    private SGGradationPaint mGradationPaint;
    
    private Icon mColorSelectionButtonEmphasisIcon = null;

    /**
     * Creates a button.
     */
    public SGGradationPaintSelectionButton() {
        super();
        this.setBorder(new LineBorder(Color.BLACK, 1));
        this.init();
    }
    
    private void init() {
        this.mGradationPaint = new SGGradationPaint();
        
        // create background icon for the button
        this.mColorSelectionButtonEmphasisIcon = SGUtility.createIcon(this, "Lines.gif");
    }
    
    /**
     * Returns the gradation if the gradation is "set" to this button.
     * 
     * @return Paint object.
     */
    public SGGradationPaint getGradationPaint() {
        try {
            return (SGGradationPaint)this.mGradationPaint.clone();
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
    public void setGradationPaint(SGGradationPaint gradation) {
        if (null!=gradation && !this.mGradationPaint.equals(gradation)) {
            try {
                this.mGradationPaint = (SGGradationPaint)gradation.clone();
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
            g2.setPaint(this.mGradationPaint.getOpaquePaint(rect2d));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (source instanceof SGGradationPaintDialog) {
            SGGradationPaint gpaint = ((SGGradationPaintDialog)source).getGradationPaint();
            if (! this.mGradationPaint.equals(gpaint)) {
                this.mGradationPaint = gpaint;
                repaint();
            }
        }
    }

    /**
     * Returns the border for a focused color selection button.
     *
     * @return the border for a focused color selection button
     */
    private Border getFocusedBorder() {
        return new MatteBorder(mColorSelectionButtonEmphasisIcon);
    }
    
    public void setFocused(final boolean focused) {
    	Border border = focused ? this.getFocusedBorder() : this.getUnfocusedBorder();
    	this.setBorder(border);
    }

    /**
     * Returns the border for an unfocused color selection button.
     *
     * @return the border for an unfocused color selection button
     */
    private Border getUnfocusedBorder() {
        return new LineBorder(Color.BLACK, 1);
    }

}

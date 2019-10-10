package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JTextField;

import jp.riken.brain.ni.samuraigraph.figure.SGFigureElement;

/**
 * The basis class of figure element in Java2D package.
 *
 */
public abstract class SGFigureElement2D extends SGFigureElement {

    private static final int TEXT_FIELD_MIN_SIZE = 120;

    /**
     * The default constructor.
     *
     */
    public SGFigureElement2D() {
        super();
    }
    
    /**
     * Shows the text field to edit a string element.
     * 
     * @param tf
     *          the text field
     * @param el
     *          a drawing element of text to edit
     * @param tx
     *          x-coordinate of the clicked point of the text field
     * @param ty
     *          y-coordinate of the clicked point of the text field
     */
    protected void showEditField(final JTextField tf, SGDrawingElementString2D el, 
            final int tx, final int ty) {
        
        // get a text string and font
        final String str = el.getString();
        Font font = el.getFont();
        final float fontSize = font.getSize();

        // get the bounds
        final Rectangle2D rect = el.getElementBounds();
        final Rectangle2D sRect = el.getStringRect();
        
        final boolean rotated = (el.getAngle() != 0.0f);
//        boolean hasChildren = false;
//        if (el instanceof SGDrawingElementString2DExtended) {
//            SGDrawingElementString2DExtended ex = (SGDrawingElementString2DExtended) el;
//            if (ex.hasSubscript() || ex.hasSuperscript()) {
//                hasChildren = true;
//            }
//        }

        // set the location
        int x;
        int y;
        if (rotated) {
            // inclined
            x = this.mPressedPoint.x - tf.getInsets().left;
            y = this.mPressedPoint.y - (int) (fontSize / 2.0f);
        } else {
            x = (int) (rect.getX() - tf.getInsets().left);
            y = (int) (rect.getY() - fontSize / 2.0f);
        }
        Rectangle bounds = this.getComponent().getBounds();
        final int right = bounds.x + bounds.width;
        if (Math.abs(x - right) < TEXT_FIELD_MIN_SIZE) {
        	x = right - TEXT_FIELD_MIN_SIZE;
        }
        tf.setLocation(x, y);
        
        // set the size
        final int w = (int) (sRect.getWidth() + fontSize);
        final int h = (int) (sRect.getHeight() + fontSize);
        tf.setSize(w, h);
        
        // set other properties
        tf.setFont(new Font(font.getName(), font.getStyle(), (int) fontSize));
        tf.setForeground(el.getColor());
        tf.setText(str);
        this.updateTextField(tf, font);

        // set caret position
        if (!rotated) {
            FontRenderContext frc = new FontRenderContext(null, false, false);
            TextLayout tl = new TextLayout(str, tf.getFont(), frc);
            TextHitInfo info = tl.hitTestChar(tx, ty);
            final int pos = info.getInsertionIndex();
            tf.setCaretPosition(pos);
        } else {
            tf.setCaretPosition(0);
        }
        
        // show the text field
        tf.setVisible(true);
        tf.requestFocus();
    }
    
    /**
     * Update the text field as the text string is modified.
     * 
     * @param tf
     *          the text field
     * @param font
     *          font in the text field
     */
    protected void updateTextField(final JTextField tf, final Font font) {

        // get the bounds of current text string
        final String str = tf.getText();
        Rectangle2D stringRect = font.getStringBounds(str,
                new FontRenderContext(null, false, false));
        
        // width of the string with offset length
        final double offset = font.getSize();
        final double strWidth = stringRect.getWidth() + offset;

        // if the width of string exceeds the width of the text field
        if (strWidth > tf.getWidth()) {
            
            // get the view boundary
            final Rectangle2D bounds = this.getViewBounds();
            final int space = 20;
            final double boundsRight = bounds.getX() + bounds.getWidth() - space;
            
            // calculate the difference between the clicked point and the right edge
            // of the graph rectangle
            final double diff = boundsRight - tf.getX();

            // if the end of the editing text string exceeds the right edge of the graph
            // rectangle, set the width of the text field to the difference
            final double length;
            if (strWidth < diff) {
                length = strWidth;
            } else {
                length = diff;
            }
            
            // set the size of text field
            tf.setSize((int) length,  tf.getHeight());
        }
        
//        // repaint
//        this.repaint();
    }
    
}

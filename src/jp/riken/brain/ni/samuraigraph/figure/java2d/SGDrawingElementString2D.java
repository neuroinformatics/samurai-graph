package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementString;

/**
 * A class of drawing elements with a text. Java2D API is used.
 */
public class SGDrawingElementString2D extends SGDrawingElementString implements
        SGIDrawingElementJava2D {

    /**
     * Visual bounding box of this string element on zero angle and given
     * magnification and font properties.
     */
    protected Rectangle2D mStringRect = null;

    /**
     * Element bounds
     */
    protected Rectangle2D mElementBounds = null;

    /**
     * test metrics
     */
    protected float mAscent = 0.0f;

    protected float mDescent = 0.0f;

    protected float mLeading = 0.0f;

    protected float mStrikethroughOffset = 0.0f;

    protected float mAdvance = 0.0f;

    /**
     * Default constructor.
     */
    public SGDrawingElementString2D() {
        super();
        this.updateMetrics();
    }

    /**
     * Construct a string element with given text.
     */
    public SGDrawingElementString2D(final String str) {
        super(str);
        this.updateMetrics();
        this.updateElementBounds();
    }

    /**
     * Construct a string element with given string element.
     */
    public SGDrawingElementString2D(final SGDrawingElementString element) {
        super(element);
        this.updateMetrics();
        this.updateElementBounds();
    }

    /**
     * Construct a string element with given text and font information.
     */
    public SGDrawingElementString2D(final String str, final String fontName,
            final int fontStyle, final float fontSize, final Color color,
            final float mag, final float angle) {
        super(str, fontName, fontStyle, fontSize, color, mag, angle);
        this.updateMetrics();
        this.updateElementBounds();
    }

    /**
     * Dispose this object.
     */
    public void dispose() {
        super.dispose();
        this.mStringRect = null;
        this.mElementBounds = null;
    }

    /**
     * Sets the text.
     * 
     * @param str
     *           a text to set
     * @return true if succeeded
     */
    public boolean setString(final String str) {
        if (super.setString(str) == false) {
            return false;
        }
        this.updateMetrics();
        this.updateElementBounds();
        return true;
    }

    /**
     * Set the magnification.
     * 
     * @param mag
     *            the magnification to set
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        if (super.setMagnification(mag) == false) {
            return false;
        }
        this.updateMetrics();
        this.updateElementBounds();
        return true;
    }

    /**
     * 
     */
    public boolean setFont(final String name, final int style, final float size) {
        super.setFont(name, style, size);
        this.updateMetrics();
        this.updateElementBounds();
        return true;
    }

    /**
     * Sets the location of this symbol.
     * 
     * @param pos
     *           the location to set
     * @return true if succeeded
     */
    public boolean setLocation(final SGTuple2f pos) {
        super.setLocation(pos);
        this.updateElementBounds();
        return true;
    }

    /**
     * Sets the location of this symbol.
     * 
     * @param x
     *          the x coordinate to set
     * @param y
     *          the y coordinate to set
     * @return true if succeeded
     */
    public boolean setLocation(final float x, final float y) {
        super.setLocation(x, y);
        this.updateElementBounds();
        return true;
    }

    /**
     * Sets the angle of this string.
     * 
     * @param angle
     *            the angle to be set in units of degree
     * @return true if succeeded
     */
    public boolean setAngle(final float angle) {
        if (super.setAngle(angle) == false) {
        	return false;
        }
        this.updateElementBounds();
        return true;
    }

    /**
     * 
     */
    public final boolean contains(final int x, final int y) {
        return this.mElementBounds.contains(x, y);
    }

    /**
     * Update the attributes of bounding box.
     */
    private void updateMetrics() {
        final Font font = this.getFont();
        final String str = this.getString();

        if (str.length() == 0) {
            this.mStringRect = new Rectangle2D.Float(); // (0,0), (0,0)
            this.mAscent = 0.0f;
            this.mDescent = 0.0f;
            this.mLeading = 0.0f;
            this.mStrikethroughOffset = 0.0f;
            this.mAdvance = 0.0f;
            return;
        }

        // create font render context
        final FontRenderContext frc = new FontRenderContext(null, false, false);

        // create text layout
        final TextLayout layout = new TextLayout(str, font, frc);

        // get a visual bounds rectangle from Font object
//        this.mStringRect = layout.getBounds();
        this.mStringRect = layout.getOutline(new AffineTransform()).getBounds2D();
        if (this.mStringRect.isEmpty()) {
            this.mStringRect.setRect(0, 0, 0, 0);
        }

        // get a line metrics from the Font object
        final LineMetrics metrics = font.getLineMetrics(str, frc);

        // this.mAscent = metrics.getAscent();
        // this.mDescent = metrics.getDescent();
        this.mAscent = (float) (-this.mStringRect.getY());
        this.mDescent = (float) (this.mStringRect.getHeight() + this.mStringRect.getY());
        this.mLeading = metrics.getLeading();
        this.mStrikethroughOffset = metrics.getStrikethroughOffset();
        this.mAdvance = layout.getAdvance();
    }

    /**
     * Returns the bounding box with the given magnification, location, font and
     * angle properties.
     */
    private void updateElementBounds() {
        final String str = this.getString();
        if (str.length() == 0) {
            this.mElementBounds = new Rectangle2D.Float(); // (0,0), (0,0)
            return;
        }

        // shift position
        final float angle = this.getAngle() * SGIConstants.RADIAN_DEGREE_RATIO;
        final float cv = (float) Math.cos(angle);
        final float sv = (float) Math.sin(angle);
        final float dy = (float) (this.mStringRect.getHeight() + this.mStringRect.getY());
        final float dx = 0.0f;
        final float pos_x = super.getX() + dx * cv + dy * sv;
        final float pos_y = super.getY() - dx * sv + dy * cv;

        // create affine trans form
        final AffineTransform af = new AffineTransform();
        af.translate(pos_x, pos_y);
        
        // rotate position
        af.rotate(-angle);

        // get element bounds
        final Rectangle2D sRect = (Rectangle2D) this.mStringRect.clone();
        sRect.setRect(0, 0, this.mAdvance, this.mStringRect.getHeight());
        final Shape sh = af.createTransformedShape(sRect);
        this.mElementBounds = sh.getBounds2D();
    }

    /**
     * Returns the bounding box with the given magnification, location, font and
     * angle properties.
     */
    public Rectangle2D getElementBounds() {
        return (Rectangle2D) this.mElementBounds.clone();
    }

    /**
     * Returns the bounding box with the given magnification and font
     * properties. The angle does not affect the returned value. The x- and
     * y-coordinate of this rectangle always equals to zero.
     */
    public Rectangle2D getStringRect() {
        return (Rectangle2D) this.mStringRect.clone();
    }

    /**
     * get ascent of text line
     */
    protected float getAscent() {
        return this.mAscent;
    }

    /**
     * get descent of text line
     */
    protected float getDescent() {
        return this.mDescent;
    }

    /**
     * get leading of text line
     * 
     */
    protected float getLeading() {
        return this.mLeading;
    }

    /**
     * get strike through offset of text line
     * 
     * @return
     */
    protected float getStrikethroughOffset() {
        return this.mStrikethroughOffset;
    }

    /**
     * get advance of text line
     */
    protected float getAdvance() {
        return this.mAdvance;
    }

    /**
     * Paint this object.
     * 
     * @param g2d
     *            graphics context
     */
    public void paint(final Graphics2D g2d) {
        if (g2d == null) {
            return;
        }
        if (this.isVisible() == false) {
            return;
        }

        final String str = this.getString();

        g2d.setPaint(this.getColor());

        // set the font
        g2d.setFont(this.getFont());

        // create an affine transformation matrix
        final AffineTransform af = new AffineTransform();
        // shift position
        af.translate(super.getX(), super.getY());
        // rotate position
        af.rotate(-this.getAngle() * SGIConstants.RADIAN_DEGREE_RATIO);

        // transform
        final AffineTransform saveAT = g2d.getTransform();
        g2d.transform(af);

        final boolean useAntiAliasing = true;

        if (useAntiAliasing) {
            // enable anti aliasing for text
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        // draw string
        g2d.drawString(str, 0, (float) this.mStringRect.getHeight());

        if (useAntiAliasing) {
            // disable anti aliasing for text
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }

        g2d.setTransform(saveAT);
    }

    /**
     * Paint this object with given clipping rectangle.
     * 
     * @param g2d
     *            graphics context
     * @param clipRect
     *            clipping rectangle
     */
    public void paint(final Graphics2D g2d, final Rectangle2D clipRect) {
        this.paint(g2d);
    }

}

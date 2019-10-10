package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Adjustable;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.MediaSize;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

/**
 * A panel put in the window.
 */
public class SGClientPanel extends JLayeredPane implements
        SGIRootObjectConstants, SGIDrawingElementConstants, SGIClientPanel,
        MouseWheelListener, KeyListener {

    private static final long serialVersionUID = 4663255737594310680L;

    /**
     * constants for layers
     */
    public final static int LAYER_GRID_PANEL = 10;

    public final static int LAYER_IMAGE_PANEL = 20;

    public final static int LAYER_FIGURE = 30;

    public final static int LAYER_ANCHOR_PANEL = 40;

    public final static int LAYER_RULER_PANEL = 50;

    public final static int LAYER_SCROLLBARS_PANEL = 60;

    /**
     * Flag of ruler visible.
     */
    protected boolean mRulerVisibleFlag = true;

    /**
     * Paper Size
     */
    private final SGTuple2f mPaperSize = new SGTuple2f();

    /**
     * A panel to draw grid lines.<BR>
     */
    private PaperGridPanel mBackgroundPanel;

    /**
     * Panel for figures.<BR>
     */
    private FigurePanel mFigurePanel;

    /**
     * A panel to draw anchors.<BR>
     */
    private ForegroundPanel mForegroundPanel;

    /**
     * A panel to draw an image.<BR>
     */
    private ImagePanel mImagePanel;

    /**
     * A panel to draw scroll bars.<BR>
     */
    private ScrollBarsPanel mScrollBarsPanel;

    /**
     * A panel to draw rulers.<BR>
     */
    private RulerPanel mRulerPanel;

    // /**
    // * Bounds of the client area.
    // */
    // private Rectangle2D mClientRect = null;

    /**
     * Build the client panel.
     * 
     * @param wnd
     *            the parent window
     */
    public SGClientPanel(final SGDrawingWindow wnd) {

        super();

        // create a figure panel
        this.createFigurePanel(wnd);

        // create a grid panel
        this.createBackgroundPanel(wnd);

        // create an anchor panel
        this.createForegroundPanel(wnd);

        // create a background image panel
        this.createImagePanel(wnd);

        // create a scroll-bar panel
        this.createScrollBarsPanel(wnd);

        // create ruler panel
        this.createRulerPanel(wnd);

        // add event listeners
        this.addMouseWheelListener(this);
        this.addMouseListener(this.mRulerPanel);
        this.addMouseMotionListener(this.mRulerPanel);

    }

    /**
     * Set the magnification.
     * 
     * @param mag
     *            a value to set to the magnification
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        this.mRulerPanel.setMagnification(mag);
        this.mBackgroundPanel.setMagnification(mag);
        this.mImagePanel.setMagnification(mag);
        this.mFigurePanel.setMagnification(mag);
        this.mForegroundPanel.setMagnification(mag);
        this.mScrollBarsPanel.setMagnification(mag);
        return true;
    }

    /**
     * Resize this component.
     * 
     * @param width
     *            a value for width
     * @param height
     *            a value for height
     */
    public void setSize(final int width, final int height) {
        super.setSize(width, height);
        final int rw = this.getRulerWidth();
        final int w = width - rw;
        final int h = height - rw;

        this.mRulerPanel.setBounds(0, 0, width, height);
        this.mForegroundPanel.setBounds(rw, rw, w, h);
        this.mForegroundPanel.setPreferredSize(new Dimension(w, h));
        this.mForegroundPanel.validate();
        this.mScrollBarsPanel.setBounds(rw, rw, w, h);
        this.mBackgroundPanel.setBounds(rw, rw, w, h);
        this.mImagePanel.setBounds(rw, rw, w, h);
        this.mFigurePanel.setBounds(rw, rw, w, h);
    }

    /**
     * Enable a pop-up menu.
     * 
     * @param menucmd
     *            a string of menu command
     * @param b
     *            true to enable
     */
    public void setPopupMenuEnabled(final String menucmd, final boolean b) {
        this.mFigurePanel.setPopupMenuEnabled(menucmd, b);
    }

    // Create a figure panel and set to this client panel.
    private boolean createFigurePanel(final SGDrawingWindow wnd) {
        FigurePanel p = new FigurePanel(wnd);
        this.add(p);
        this.setLayer(p, LAYER_FIGURE);

        // assign mouse wheel event listener
        p.addMouseWheelListener(this);

        // assign key event listener
        p.addKeyListener(this);

        p.setLayout(null);
        this.mFigurePanel = p;

        return true;
    }

    /**
     * Returns the figure panel.
     * 
     * @return the figure panel
     */
    public JComponent getFigurePanel() {
        return this.mFigurePanel;
    }

    // Create a foreground panel and set to this client panel.
    private boolean createForegroundPanel(final SGDrawingWindow wnd) {
        ForegroundPanel p = new ForegroundPanel(wnd);
        this.add(p);
        this.setLayer(p, LAYER_ANCHOR_PANEL);
        this.mForegroundPanel = p;
        return true;
    }

    // Returns the size of an anchor.
    static float getAnchorSize() {
        return ForegroundPanel.ANCHOR_SIZE;
    }

    // Create an image panel and set to this client panel.
    private boolean createImagePanel(final SGDrawingWindow wnd) {
        ImagePanel p = new ImagePanel(wnd);
        this.add(p);
        this.setLayer(p, LAYER_IMAGE_PANEL);
        this.mImagePanel = p;
        return true;
    }

    /**
     * Set an image.
     * 
     * @param img
     *            an image to set
     * @return true if succeeded
     */
    public boolean setImage(final Image img) {
        return this.mImagePanel.setImage(img);
    }
    
    /**
     * Sets the file path of image.
     * 
     * @param path
     *           the file path of image
     */
    public void setImageFilePath(final String path) {
    	this.mImagePanel.setImageFilePath(path);
    }

    /**
     * Set the location of the image.
     * 
     * @param x
     *            x coordinate
     * @param y
     *            y coordinate
     * @return true if succeeded
     */
    public boolean setImageLocation(final int x, final int y) {
        return this.mImagePanel.setImageLocation(x, y);
    }

    /**
     * Set the size of the image.
     * 
     * @param w
     *            width
     * @param h
     *            height
     * @return true if succeeded
     */
    public boolean setImageSize(final int w, final int h) {
        return this.mImagePanel.setImageSize(w, h);
    }

    /**
     * Returns the image.
     * 
     * @return the image
     */
    public Image getImage() {
        return this.mImagePanel.getImage();
    }

    /**
     * Returns the location of the image.
     * 
     * @return the location of the image
     */
    public SGTuple2f getImageLocation() {
        return this.mImagePanel.getImageLocation();
    }

    /**
     * Returns the size of the image.
     * 
     * @return the size of the image
     */
    public SGTuple2f getImageSize() {
        return this.mImagePanel.getImageSize();
    }

    // Create a background panel and set to this client panel.
    private boolean createBackgroundPanel(final SGDrawingWindow wnd) {
        PaperGridPanel p = new PaperGridPanel(wnd);
        this.add(p);
        this.setLayer(p, LAYER_GRID_PANEL);
        this.mBackgroundPanel = p;
        return true;
    }

    /**
     * Returns the width of the paper.
     * 
     * @return the width of the paper
     */
    public float getPaperWidth() {
        return this.mPaperSize.x;
    }

    /**
     * Returns the height of the paper.
     * 
     * @return the height of the paper
     */
    public float getPaperHeight() {
        return this.mPaperSize.y;
    }

    /**
     * Returns the width of the paper in a given unit.
     * 
     * @param unit
     *            unit of length
     * @return the width of the paper in a given unit of length
     */
    public float getPaperWidth(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getPaperWidth(),
                unit);
    }

    /**
     * Returns the height of the paper in a given unit.
     * 
     * @param unit
     *            unit of length
     * @return the height of the paper in a given unit of length
     */
    public float getPaperHeight(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getPaperHeight(),
                unit);
    }

    /**
     * Set the width of the paper.
     * 
     * @param w
     *            a value for the width of the paper
     * @return true if succeeded
     */
    public boolean setPaperWidth(final float w) {
        return this.setPaperSize(w, this.getPaperHeight());
    }

    /**
     * Set the height of the paper in a given unit.
     * 
     * @param h
     *            a value for the height of the paper
     * @param unit
     *            unit of length
     * @return true if succeeded
     */
    public boolean setPaperWidth(final float w, final String unit) {
        final Float wNew = SGUtility.calcPropertyValue(w, unit, PAPER_SIZE_UNIT, 
                PAPER_WIDTH_MIN_VALUE, PAPER_WIDTH_MAX_VALUE, LENGTH_MINIMAL_ORDER);
        if (wNew == null) {
            return false;
        }
        return this.setPaperSize(wNew, this.getPaperHeight());
    }

    /**
     * Set the height of the paper.
     * 
     * @param gh
     *            a value for the height of the paper
     * @return true if succeeded
     */
    public boolean setPaperHeight(final float h) {
        return this.setPaperSize(this.getPaperWidth(), h);
    }

    /**
     * Set the height of the paper in a given unit.
     * 
     * @param h
     *            a value for the height of the paper
     * @param unit
     *            unit of length
     * @return true if succeeded
     */
    public boolean setPaperHeight(final float h, final String unit) {
        final Float hNew = SGUtility.calcPropertyValue(h, unit, PAPER_SIZE_UNIT, 
                PAPER_HEIGHT_MIN_VALUE, PAPER_HEIGHT_MAX_VALUE, LENGTH_MINIMAL_ORDER);
        if (hNew == null) {
            return false;
        }
        return this.setPaperSize(this.getPaperWidth(), hNew);
    }

    /**
     * Set the size of the paper.
     * 
     * @param w
     *            a value for the width of the paper
     * @param h
     *            a value for the height of the paper
     * @return true if succeeded
     */
    public boolean setPaperSize(final float w, final float h) {
        return this.setPaperSizeRoundingOff(w, h);
    }

    /**
     * Set the size of the paper to a given media size.
     * 
     * @param size
     *            a media size
     * @param isPortrait
     *            true for portrait and false for landscape
     * @return true if succeeded
     */
    public boolean setPaperSize(MediaSize size, boolean isPortrait) {
        if (size == null) {
            return false;
        }

        // length in units of CM
        BigDecimal bdWidthCM = new BigDecimal(size.getX(Size2DSyntax.MM));
        BigDecimal bdHeightCM = new BigDecimal(size.getY(Size2DSyntax.MM));
        bdWidthCM = bdWidthCM.movePointLeft(1);
        bdHeightCM = bdHeightCM.movePointLeft(1);
        final float widthCM = bdWidthCM.floatValue();
        final float heightCM = bdHeightCM.floatValue();

        // set size
        if (isPortrait) {
            this.setPaperSizeRoundingOffInCMUnit(widthCM, heightCM);
        } else {
            this.setPaperSizeRoundingOffInCMUnit(heightCM, widthCM);
        }

        return true;
    }

    private boolean setPaperSizeRoundingOffInCMUnit(final float widthCM,
            final float heightCM) {
        // round out the size
        float dWidthCM = (float) SGUtilityNumber.roundOffNumber(widthCM,
                LENGTH_MINIMAL_ORDER - 1);
        float dHeightCM = (float) SGUtilityNumber.roundOffNumber(heightCM,
                LENGTH_MINIMAL_ORDER - 1);

        if (dWidthCM < PAPER_WIDTH_MIN_VALUE)
            return false;
        if (dWidthCM > PAPER_WIDTH_MAX_VALUE)
            return false;
        if (dHeightCM < PAPER_HEIGHT_MIN_VALUE)
            return false;
        if (dHeightCM > PAPER_HEIGHT_MAX_VALUE)
            return false;

        // length in units of pixel
        final float ratio = SGIConstants.CM_POINT_RATIO;
        final float width = dWidthCM / ratio;
        final float height = dHeightCM / ratio;

        // set the bounds of paper
        this.mPaperSize.setValues(width, height);

        return true;
    }

    private boolean setPaperSizeRoundingOutInCMUnit(final float widthCM,
            final float heightCM) {
        // round out the size
        final float dWidthCM = (float) SGUtilityNumber.roundOutNumber(widthCM,
                LENGTH_MINIMAL_ORDER - 1);
        final float dHeightCM = (float) SGUtilityNumber.roundOutNumber(
                heightCM, LENGTH_MINIMAL_ORDER - 1);

        if (dWidthCM < PAPER_WIDTH_MIN_VALUE)
            return false;
        if (dWidthCM > PAPER_WIDTH_MAX_VALUE)
            return false;
        if (dHeightCM < PAPER_HEIGHT_MIN_VALUE)
            return false;
        if (dHeightCM > PAPER_HEIGHT_MAX_VALUE)
            return false;

        // length in units of pixel
        final float ratio = SGIConstants.CM_POINT_RATIO;
        final float width = dWidthCM / ratio;
        final float height = dHeightCM / ratio;

        // set the bounds of paper
        this.mPaperSize.setValues(width, height);

        return true;
    }

    /**
     * Set the size of the paper in units of point by rounding off.
     * 
     * @param widthPt
     *            a value for the paper width in units of point
     * @param heightPt
     *            a value for the paper height in units of point
     * @return true if succeeded
     */
    public boolean setPaperSizeRoundingOff(final float widthPt,
            final float heightPt) {
        final float ratio = SGIConstants.CM_POINT_RATIO;
        final float widthCM = widthPt * ratio;
        final float heightCM = heightPt * ratio;
        if (this.setPaperSizeRoundingOffInCMUnit(widthCM, heightCM) == false) {
            return false;
        }
        return true;
    }

    /**
     * Set the size of the paper in units of point by rounding out.
     * 
     * @param widthPt
     *            a value for the paper width in units of point
     * @param heightPt
     *            a value for the paper height in units of point
     * @return true if succeeded
     */
    public boolean setPaperSizeRoundingOut(final float widthPt,
            final float heightPt) {
        final float ratio = SGIConstants.CM_POINT_RATIO;
        final float widthCM = widthPt * ratio;
        final float heightCM = heightPt * ratio;
        if (this.setPaperSizeRoundingOutInCMUnit(widthCM, heightCM) == false) {
            return false;
        }
        return true;
    }

    /**
     * Returns whether the grid lines are visible.
     * 
     * @return true if visible
     */
    public boolean isGridLineVisible() {
        return this.mBackgroundPanel.isGridVisible();
    }

    /**
     * Returns the width of grid lines.
     * 
     * @return width of grid lines
     */
    public float getGridLineWidth() {
        return this.mBackgroundPanel.getGridLineWidth();
    }

    /**
     * Returns the width of grid lines in a given unit.
     * 
     * @param unit
     *            unit of length
     * @return width of grid lines in a given unit
     */
    public float getGridLineWidth(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getGridLineWidth(),
                unit);
    }

    /**
     * Returns the interval between grid lines.
     * 
     * @return the interval between grid lines
     */
    public float getGridLineInterval() {
        return this.mBackgroundPanel.getGridInterval();
    }

    /**
     * Returns the interval between grid lines in a given unit.
     * 
     * @param unit
     *            unit of length
     * @return the interval between grid lines in a given unit
     */
    public float getGridLineInterval(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this
                .getGridLineInterval(), unit);
    }

    /**
     * Returns the color of grid lines.
     * 
     * @return the color of grid lines
     */
    public Color getGridLineColor() {
        return this.mBackgroundPanel.getGridLineColor();
    }

    public float getImageLocationX() {
        return this.mImagePanel.getImageLocation().x;
    }

    public float getImageLocationX(String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getImageLocationX(),
                unit);
    }

    public float getImageLocationY() {
        return this.mImagePanel.getImageLocation().y;
    }

    public float getImageLocationY(String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getImageLocationY(),
                unit);
    }

    public float getImageWidth() {
        return this.mImagePanel.getImageSize().x;
    }

    public float getImageWidth(String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getImageWidth(),
                unit);
    }

    public float getImageHeight() {
        return this.mImagePanel.getImageSize().y;
    }

    public float getImageHeight(String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getImageHeight(),
                unit);
    }
    
    public String getImageFilePath() {
    	return this.mImagePanel.getImageFilePath();
    }

    public boolean setGridLineWidth(final float width) {
        this.mBackgroundPanel.setGridLineWidth(width);
        return true;
    }

    public boolean setGridLineWidth(final float lw, final String unit) {
        final Float lwNew = SGUtility.getLineWidth(lw, unit);
        if (lwNew == null) {
            return false;
        }
        if (this.setGridLineWidth(lwNew) == false) {
            return false;
        }
        return true;
    }

    public boolean setGridLineColor(final Color color) {
        this.mBackgroundPanel.setGridLineColor(color);
        return true;
    }

    public boolean setPaperColor(final Color color) {
        this.mBackgroundPanel.setPaperColor(color);
        return true;
    }

    /**
     * Set the visibility of grid lines.
     * 
     * @param b
     *            true to set visible
     * @return true if succeeded
     */
    public boolean setGridLineVisible(final boolean b) {
        this.mBackgroundPanel.setGridVisible(b);
        return true;
    }

    public boolean setGridLineInterval(final float interval) {
        this.mBackgroundPanel.setGridInterval(interval);
        return true;
    }

    public boolean setGridLineInterval(final float interval, final String unit) {
        final Float iNew = SGUtility.calcPropertyValue(interval, unit, GRID_INTERVAL_UNIT, 
                GRID_INTERVAL_MIN_VALUE, GRID_INTERVAL_MAX_VALUE, LENGTH_MINIMAL_ORDER);
        if (iNew == null) {
            return false;
        }
        return this.setGridLineInterval(iNew);
    }

    public boolean setImageLocationX(final float value) {
        return this.mImagePanel.setImageLocation(value, this
                .getImageLocationY());
    }

    public boolean setImageLocationX(final float x, final String unit) {
        final Float xNew = SGUtility.calcPropertyValue(x, unit, IMAGE_LOCATION_UNIT, 
                IMAGE_LOCATION_X_MIN_VALUE, IMAGE_LOCATION_X_MAX_VALUE, LENGTH_MINIMAL_ORDER);
        if (xNew == null) {
            return false;
        }
        return this.setImageLocationX(xNew);
    }

    public boolean setImageLocationY(final float value) {
        return this.mImagePanel.setImageLocation(this.getImageLocationX(),
                value);
    }

    public boolean setImageLocationY(final float y, final String unit) {
        final Float yNew = SGUtility.calcPropertyValue(y, unit, IMAGE_LOCATION_UNIT, 
                IMAGE_LOCATION_Y_MIN_VALUE, IMAGE_LOCATION_Y_MAX_VALUE, LENGTH_MINIMAL_ORDER);
        if (yNew == null) {
            return false;
        }
        return this.setImageLocationY(yNew);
    }

    public float getImageScalingFactor() {
        return this.mImagePanel.getScalingFactor();
    }

    public boolean setImageScalingFactor(final float f) {
        final Float fNew = SGUtility.calcPropertyValue(f, null, null, 
                IMAGE_SCALE_MIN_VALUE, IMAGE_SCALE_MAX_VALUE, IMAGE_SCALING_ORDER);
        if (fNew == null) {
            return false;
        }
        return this.mImagePanel.setScalingFactor(fNew);
    }

    public Color getPaperColor() {
        return this.mBackgroundPanel.getPaperColor();
    }

    //
    // Scroll Bar
    //

    private boolean createScrollBarsPanel(final SGDrawingWindow wnd) {
        ScrollBarsPanel p = new ScrollBarsPanel(wnd);

        this.add(p);
        this.setLayer(p, LAYER_SCROLLBARS_PANEL);

        this.mScrollBarsPanel = p;

        return true;
    }

    public boolean setScrollRatio(final float vRatio, final float hRatio) {
        return this.mScrollBarsPanel.setScrollRatio(vRatio, hRatio);
    }

    public SGTuple2f getScrollRatio() {
        return this.mScrollBarsPanel.getScrollRatio();
    }

    public boolean setScrollBarValue(final Rectangle2D cRect,
            final Rectangle2D vpRect) {
        return this.mScrollBarsPanel.setScrollBarValue(cRect, vpRect);
    }

    public boolean setEnableScrollBars(final Rectangle2D vpRect,
            final Rectangle2D bbRect) {
        return this.mScrollBarsPanel.setEnableScrollBars(vpRect, bbRect);
    }

    //
    // Ruler
    //

    private boolean createRulerPanel(final SGDrawingWindow wnd) {
        RulerPanel p = new RulerPanel(wnd);
        this.add(p);
        this.setLayer(p, LAYER_RULER_PANEL);

        this.mRulerPanel = p;

        return true;
    }

    /*
     * Get Ruler width
     */
    public int getRulerWidth() {
        int width = 0;
        if (this.mRulerVisibleFlag) {
            width = RulerPanel.RULER_WIDTH;
        }
        return width;
    }

    //
    // event
    //
    /**
     * The listener interface for receiving keyboard events (keystrokes).
     * 
     * @see KeyAdapter
     * @see KeyEvent
     * @see <a
     *      href="http://java.sun.com/docs/books/tutorial/post1.0/ui/keylistener.html">Tutorial:
     *      Writing a Key Listener</a>
     * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference:
     *      The Java Class Libraries (update file)</a>
     * 
     */

    /**
     * Invoked when a key has been typed. See the class description for
     * {@link KeyEvent} for a definition of a key typed event.
     */
    public void keyTyped(final KeyEvent e) {
    }

    /**
     * Invoked when a key has been pressed. See the class description for
     * {@link KeyEvent} for a definition of a key pressed event.
     */
    public void keyPressed(final KeyEvent e) {
        if (this.mFigurePanel.onKeyPressed(e)) {
            return;
        }
        this.onKeyPressed(e);
    }

    /**
     * Invoked when a key has been released. See the class description for
     * {@link KeyEvent} for a definition of a key released event.
     */
    public void keyReleased(final KeyEvent e) {
    }

    // the body part of key event hander for figure panel
    private boolean onKeyPressed(final KeyEvent e) {
        final int keycode = e.getKeyCode();
        boolean effective = false;
        switch (keycode) {
        case KeyEvent.VK_UP:
            this.mScrollBarsPanel.onScrollBy(-1, true);
            effective = true;
            break;
        case KeyEvent.VK_DOWN:
            this.mScrollBarsPanel.onScrollBy(1, true);
            effective = true;
            break;
        case KeyEvent.VK_LEFT:
            this.mScrollBarsPanel.onScrollBy(-1, false);
            effective = true;
            break;
        case KeyEvent.VK_RIGHT:
            this.mScrollBarsPanel.onScrollBy(1, false);
            effective = true;
            break;
        }
        return effective;
    }

    // mouse wheel event
    public void mouseWheelMoved(MouseWheelEvent e) {

        final int amount = e.getScrollAmount() * e.getWheelRotation();
        final int mod = e.getModifiers();
        final boolean ctrl = ((mod & InputEvent.CTRL_MASK) != 0);
        if (ctrl) {
            // zoom in / out
            
            SGDrawingWindow wnd = this.mFigurePanel.mWnd;
            
            // current magnification
            final float mag = wnd.getMagnification() * 100.0f;

            // difference
            final int order = (int) Math.rint(Math.log10(mag));
            final float factor = 0.50f;
            float magDiff = (- amount) * order * factor;
            if (Math.abs(magDiff) < 1.0f) {
                magDiff = (magDiff > 0.0f ? 1.0f : -1.0f);
            }
            
            // new magnifiacation
            float magNew = mag + magDiff;
            if (magNew < MIN_MAGNIFICATION_VALUE) {
                magNew = MIN_MAGNIFICATION_VALUE;
            } else if (magNew > MAX_MAGNIFICATION_VALUE) {
                magNew = MAX_MAGNIFICATION_VALUE;
            }
            final int magInt = (int) magNew;
            
            // set to the window
            wnd.setZoomValue(Integer.valueOf(magInt));
            
        } else {
            // scroll the vertical scroll bar
            this.mScrollBarsPanel.onScrollBy(amount, true);
        }
        
    }

    /**
     * Base class of panel class in the window.
     */
    private static abstract class InnerPanel extends JPanel {

        private static final long serialVersionUID = -7645179645536848816L;

        /**
         * 
         */
        protected float mMagnification = 1.0f;

        /**
         * 
         */
        protected SGDrawingWindow mWnd = null;

        /**
         * 
         * @param wnd
         */
        protected InnerPanel(SGDrawingWindow wnd) {
            super();
            this.mWnd = wnd;
        }

        /**
         * @param f
         */
        public void setMagnification(final float f) {
            this.mMagnification = f;
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.mWnd.isDisposed()) {
            	return;
            }
        }
    }

    /**
     * Panel to draw anchors and rubber band.
     */
    private static class ForegroundPanel extends InnerPanel {
        /**
         * 
         */
        private static final long serialVersionUID = 5018390332255803060L;

        /**
         * 
         */
        public static final float ANCHOR_SIZE = 6.0f;

        /**
         * 
         */
        protected ForegroundPanel(SGDrawingWindow wnd) {
            super(wnd);
            this.setVisible(true);
            this.setOpaque(false);
        }

        /**
         * 
         */
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (this.mWnd == null) {
            	return;
            }

            final Graphics2D g2d = (Graphics2D) g.create();
            
            List<SGFigure> list = this.mWnd.getFocusedFigureList();
            for (int ii = 0; ii < list.size(); ii++) {

                final SGFigure figure = list.get(ii);
                if (figure.isSelectionSymbolsVisible() == false) {
                    continue;
                }

                Rectangle2D dRect = figure.getRubberBandRect();
                if (SGFigure.mRubberBandFlag && SGFigure.mRubberBandVisibleFlag) {
                    this.drawRubberBand(g2d, dRect.getBounds());
                }

                final Rectangle2D gRect = figure.getGraphRect();
                final int x = (int) gRect.getX();
                final int y = (int) gRect.getY();
                final int w = (int) gRect.getWidth();
                final int h = (int) gRect.getHeight();
                final int wHalf = w / 2;
                final int hHalf = h / 2;
                drawAnchor(g2d, x, y);
                drawAnchor(g2d, x + w, y);
                drawAnchor(g2d, x, y + h);
                drawAnchor(g2d, x + w, y + h);
                drawAnchor(g2d, x + wHalf, y);
                drawAnchor(g2d, x, y + hHalf);
                drawAnchor(g2d, x + wHalf, y + h);
                drawAnchor(g2d, x + w, y + hHalf);
            }

        }

        /**
         * 
         */
        private void drawAnchor(final Graphics2D g2d, final int x, final int y) {
            final float size = ANCHOR_SIZE;
            final float sizeHalf = size / 2;
            final Shape anchor = new Ellipse2D.Float(x - sizeHalf, y - sizeHalf,
                    size, size);

            g2d.setPaint(Color.BLACK);
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(anchor);

            g2d.setPaint(Color.WHITE);
            g2d.fill(anchor);
        }

        /**
         * 
         */
        private void drawRubberBand(final Graphics2D g2d, final Rectangle rect) {

            g2d.setPaint(Color.BLACK);

            final float width = 2.0f;
            final float dash[] = { 2.0f * width, width };
            g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));

            g2d.draw(rect);

        }

    }

    /**
     * Panel to draw scroll bars.
     */
    private static class ScrollBarsPanel extends InnerPanel implements
            AdjustmentListener {
        /**
         * 
         */
        private static final long serialVersionUID = 8449254425744244035L;

        /**
         * Vertical and Horizontal Scrollbar
         */
        private JScrollBar mVScrollBar = null;

        private JScrollBar mHScrollBar = null;

        protected ScrollBarsPanel(SGDrawingWindow wnd) {
            super(wnd);
            this.setVisible(true);
            this.setOpaque(false);
            this.createScrollBars();
        }

        /**
         * 
         */
        public void onScrollBy(final int amount, final boolean isVertical) {
            if (isVertical) {
                // scroll the vertical scroll bar
                final int value = this.mVScrollBar.getValue() + amount;
                this.mVScrollBar.setValue(value);
            } else {
                // scroll the horizontal scroll bar
                final int value = this.mHScrollBar.getValue() + amount;
                this.mHScrollBar.setValue(value);
            }
        }

        /**
         * 
         * @return
         */
        private boolean createScrollBars() {
            this.mVScrollBar = new JScrollBar(Adjustable.VERTICAL, 0, 100, 0,
                    100);
            this.mHScrollBar = new JScrollBar(Adjustable.HORIZONTAL, 0, 100, 0,
                    100);
            this.mVScrollBar.setVisible(false);
            this.mHScrollBar.setVisible(false);

            this.setLayout(new BorderLayout());
            this.add(this.mVScrollBar, BorderLayout.EAST);
            this.add(this.mHScrollBar, BorderLayout.SOUTH);

            {
                final int min = this.mHScrollBar.getMinimum();
                final int max = this.mHScrollBar.getMaximum();
                final int extent = this.mHScrollBar.getVisibleAmount();
                this.mHScrollBar.setValue((max - extent - min) / 2);
            }
            {
                final int min = this.mVScrollBar.getMinimum();
                final int max = this.mVScrollBar.getMaximum();
                final int extent = this.mVScrollBar.getVisibleAmount();
                this.mVScrollBar.setValue((max - extent - min) / 2);
            }

            this.mHScrollBar.addAdjustmentListener(this);
            this.mVScrollBar.addAdjustmentListener(this);

            return true;
        }

        /**
         * Set value of scrollbars
         */
        public boolean setScrollRatio(final double vRatio, final double hRatio) {

            // set value to vertical scroll bar
            final int v_min = this.mVScrollBar.getMinimum();
            final int v_max = this.mVScrollBar.getMaximum();
            final int v_extent = this.mVScrollBar.getVisibleAmount();

            final int v_num = v_max - v_min - v_extent;
            final double v_value = vRatio * v_num;

            this.mVScrollBar.setValue((int) v_value);

            // set value to horizontal scroll bar
            final int h_min = this.mHScrollBar.getMinimum();
            final int h_max = this.mHScrollBar.getMaximum();
            final int h_extent = this.mHScrollBar.getVisibleAmount();

            final int h_num = h_max - h_min - h_extent;
            final double h_value = hRatio * h_num;

            this.mHScrollBar.setValue((int) h_value);
            return true;
        }

        /**
         * switch visible of scroll bars
         * 
         * @return
         */
        protected boolean setEnableScrollBars(Rectangle2D vpRect,
                Rectangle2D bbRect) {
            final Rectangle vpRect_ = vpRect.getBounds();
            final Rectangle bbRect_ = bbRect.getBounds();
            final boolean hFlag = SGUtilityNumber.contains(vpRect_.x, vpRect_.x
                    + vpRect_.width, bbRect_.x, bbRect_.x + bbRect_.width);

            final boolean vFlag = SGUtilityNumber.contains(vpRect_.y, vpRect_.y
                    + vpRect_.height, bbRect_.y, bbRect_.y + bbRect_.height);

            this.mHScrollBar.setVisible(!hFlag);
            this.mVScrollBar.setVisible(!vFlag);

            return true;
        }

        /**
         * 
         * @return
         */
        private boolean setScrollBarValue(final Rectangle2D cRect,
                final Rectangle2D vpRect) {
            // System.out.println("<< setScrollBarValue >>");

            if (this.mHScrollBar.isVisible()) {
                this.setScrollBarValue(true, cRect, vpRect);
            }

            if (this.mVScrollBar.isVisible()) {
                this.setScrollBarValue(false, cRect, vpRect);
            }

            return true;
        }

        private boolean setScrollBarValue(final boolean flag,
                final Rectangle2D cRect, final Rectangle2D vpRect) {
            JScrollBar bar = null;
            float cLength = 0.0f;
            float cStart = 0.0f;
            float vpLength = 0.0f;

            if (flag) {
                bar = this.mHScrollBar;
                cLength = (float) cRect.getWidth();
                cStart = (float) cRect.getX();
                vpLength = (float) vpRect.getWidth();
            } else {
                bar = this.mVScrollBar;
                cLength = (float) cRect.getHeight();
                cStart = (float) cRect.getY();
                vpLength = (float) vpRect.getHeight();
            }

            final int min = bar.getMinimum();
            final int max = bar.getMaximum();
            final int extent = (int) ((vpLength / cLength) * (max - min));
            bar.setVisibleAmount(extent);

            final float ratio = -cStart / (cLength - vpLength);
            final int value = min + (int) (ratio * (max - extent - min));
            bar.setValue(value);

            // set the block increment to be equal to the extent
            bar.setBlockIncrement(extent);

            return true;
        }

        public SGTuple2f getScrollRatio() {

            float hValue = 0.0f;
            float vValue = 0.0f;
            {
                final int h_min = this.mHScrollBar.getMinimum();
                final int h_max = this.mHScrollBar.getMaximum();
                final int h_extent = this.mHScrollBar.getVisibleAmount();

                if (h_max - h_min - h_extent != 0) {
                    hValue = (float) this.mHScrollBar.getValue()
                            / (float) (h_max - h_min - h_extent);
                }
            }
            {
                final int v_min = this.mVScrollBar.getMinimum();
                final int v_max = this.mVScrollBar.getMaximum();
                final int v_extent = this.mVScrollBar.getVisibleAmount();

                if (v_max - v_min - v_extent != 0) {
                    vValue = (float) this.mVScrollBar.getValue()
                            / (float) (v_max - v_min - v_extent);
                }
            }

            SGTuple2f ratio = new SGTuple2f(hValue, vValue);
            return ratio;
        }

        /**
         * 
         * @param flag
         * @return
         */
        private boolean setClientRectByValueOfScrollBar(final boolean flag) {
            float value = 0.0f;

            final SGTuple2f ratio = this.getScrollRatio();

            if (flag) {
                value = ratio.x;
            } else {
                value = ratio.y;
            }

            Rectangle2D cRect = this.mWnd.getClientRect();
            Rectangle2D vpRect = this.mWnd.getViewportBounds();

            final float cx = (float) cRect.getX();
            final float cy = (float) cRect.getY();
            final float cWidth = (float) cRect.getWidth();
            final float cHeight = (float) cRect.getHeight();
            final float vpWidth = (float) vpRect.getWidth();
            final float vpHeight = (float) vpRect.getHeight();

            if (flag) {
                final float diff = cWidth - vpWidth;
                final float x = -value * diff;
                this.mWnd.setClientRect(x, cy, cWidth, cHeight);
            } else {
                final float diff = cHeight - vpHeight;
                final float y = -value * diff;
                this.mWnd.setClientRect(cx, y, cWidth, cHeight);
            }

            return true;
        }

        public void adjustmentValueChanged(final AdjustmentEvent e) {

            Object source = e.getSource();

            if (source instanceof JScrollBar) {

                JScrollBar bar = (JScrollBar) source;

                if (this.mHScrollBar.equals(bar)) {
                    this.setClientRectByValueOfScrollBar(true);
                } else if (this.mVScrollBar.equals(bar)) {
                    this.setClientRectByValueOfScrollBar(false);
                }

                //
                this.mWnd.updateGraphRectOfAllFigures();

                this.mWnd.repaintContentPane();
            }

        }
    }

    /**
     * Panel to draw figures.
     */
    private static class FigurePanel extends InnerPanel implements
            MouseInputListener, ActionListener {

        /**
         * 
         */
        private static final long serialVersionUID = -2389599796823507239L;

        private static final String DIALOG_TITLE_ADDITION_ERROR = "Failed to add a symbol.";

        private static final String ERRMSG_CLICK_GRAPH_RECTANGLE = "Click within the recatngle of figure.";

        /**
         * Popup menu.
         */
        private JPopupMenu mPopupMenu = null;

        /**
         * 
         */
        protected FigurePanel(SGDrawingWindow wnd) {
            super(wnd);
            this.setVisible(true);
            this.setOpaque(false);

            // assign mouse event listener
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            SGFigure[] array = this.mWnd.getFigureArray();
            for (int ii = 0; ii < array.length; ii++) {
                if (array[ii].isVisible()) {
                    array[ii].paint(g, true);
                }
            }

        }

        /**
         * Returns a pop-up menu.
         * @return
         *         a pop-up menu
         */
        public JPopupMenu getPopupMenu() {
            JPopupMenu p = null;
            if (this.mPopupMenu != null) {
                p = this.mPopupMenu;
            } else {
                p = this.createPopupMenu();
                this.mPopupMenu = p;
            }
            return p;
        }

        /**
         * Create a pop-up menu.
         * @return
         *         a pop-up menu
         */
        private JPopupMenu createPopupMenu() {
            JMenuItem item;
            
            JPopupMenu p = new JPopupMenu();
            p.setBounds(0, 0, 100, 100);
            
            StringBuffer sb = new StringBuffer();
            sb.append("  -- Window: ");
            sb.append(this.mWnd.getID());
            sb.append(" --");
            
            p.add(new JLabel(sb.toString()));
            p.addSeparator();

            item = SGUtility.addItem(p, this, MENUCMD_PASTE);
            item.setEnabled(false);

            p.addSeparator();

            SGUtility.addItem(p, this, MENUCMD_PROPERTY);
            
            Component[] array = p.getComponents();
            for (int ii = 0; ii < array.length; ii++) {
                if (array[ii] instanceof JMenuItem) {
                    item = (JMenuItem) array[ii];
                    String command = item.getActionCommand();
                    Boolean b = (Boolean) this.mPopupMenuEnabledMap.get(command);
                    if (b != null) {
                        item.setEnabled(b.booleanValue());
                    }
                }
            }

            return p;
        }

        protected void setPopupMenuEnabled(final String menucmd, final boolean b) {
            
            this.mPopupMenuEnabledMap.put(menucmd, Boolean.valueOf(b));
            
//            Component[] array = this.mPopupMenu.getComponents();
//            for (int ii = 0; ii < array.length; ii++) {
//                if (array[ii] instanceof JMenuItem) {
//                    JMenuItem item = (JMenuItem) array[ii];
//                    if (item.getActionCommand().equals(menucmd)) {
//                        item.setEnabled(b);
//                    }
//                }
//            }
        }
        
        private Map mPopupMenuEnabledMap = new HashMap();

        //
        // action event
        //
        /**
         * The listener interface for receiving action events.
         */
        public void actionPerformed(final ActionEvent e) {
            final String command = e.getActionCommand();
            // final Object source = e.getSource();

            if (command.equals(MENUCMD_PASTE)) {
                this.mWnd.doPaste();
            } else if (command.equals(MENUCMD_PROPERTY)) {
                this.mWnd.showPropertyDialog();
            }
        }

        
        /**
         * Invoked when the mouse button has been clicked (pressed and released)
         * on a component.
         */
        public void mouseClicked(final MouseEvent e) {
            this.grabFocus();
            final boolean b = this.clickFigures(e);
            if (!b) {
                // clear all focused figures and focused objects in all figures
                this.mWnd.clearAllFocusedObjectsInFigures();

                // set the default cursor
                this.mWnd.setCursor(null);

                // show the property dialog
                if ((SwingUtilities.isLeftMouseButton(e))
                        && (e.getClickCount() == 2)) {
                    this.mWnd.showPropertyDialog();
                }

                // show the pop-up menu
                if ((SwingUtilities.isRightMouseButton(e))
                        && (e.getClickCount() == 1)) {
                    this.getPopupMenu().show(this, e.getX(), e.getY());
                }
            }
            this.mWnd.updateDataItem();
            this.mWnd.updateFocusedObjectItem();
        }

        /**
         * Invoked when a mouse button has been pressed on a component.
         */
        public void mousePressed(final MouseEvent e) {
            // record the pressed point
            this.mWnd.mMousePressLocation = e.getPoint();
            this.mWnd.mTempMouseLocation.setLocation(e.getPoint());
            
            if (this.pressFigures(e)) {
                this.mWnd.repaintContentPane();
                return;
            }
            this.mWnd.clearAllFocusedObjectsInFigures();
        }

        /**
         * Invoked when a mouse button has been released on a component.
         */
        public void mouseReleased(final MouseEvent e) {
        	
            Point pressedPos = this.mWnd.mMousePressLocation;
        	this.mWnd.mMousePressLocation = null;

            // if visible figure do not exist, return
            ArrayList<SGFigure> figureList = this.mWnd.getVisibleFigureList();
            if (figureList.size() == 0) {
                return;
            }

            // whether the insert is selected
            final boolean b = this.mWnd.isInsertFlagSelected();
            if (b) {

                for (int ii = figureList.size() - 1; ii >= 0; ii--) {
                    SGFigure figure = (SGFigure) figureList.get(ii);
                    figure.onMouseReleased(e);
                    
                    // coordinate of pressed point
                    final int px = this.mWnd.mTempMouseLocation.x;
                    final int py = this.mWnd.mTempMouseLocation.y;

                    // add symbols to the figure by the toggle buttons
                    if (this.mWnd.insertSymbol(figure, px, py)) {
                	
                        // set the default cursor
                        this.mWnd.setCursor(null);

                        // set insert items unselected
                        this.mWnd.setInsertToggleItemsUnselected();

                        // repaint
                        this.mWnd.repaintContentPane();
                        
                        return;
                    }
                }

                // show an error message dialog
                SGUtility.showErrorMessageDialog(this,
                        ERRMSG_CLICK_GRAPH_RECTANGLE,
                        DIALOG_TITLE_ADDITION_ERROR);

            } else {
            	
                for (SGFigure figure : figureList) {
                    figure.onMouseReleased(e);
                }

                Point pos = e.getPoint();
            	if (!pos.equals(pressedPos)) {
                    // updates changed flag
                    for (SGFigure figure : figureList) {
                        SGIFigureElement[] array = figure.getIFigureElementArray();
                        for (int jj = array.length - 1; jj >= 0; jj--) {
                        	array[jj].updateChangedFlag();
                        }
                    }
                    
                    // checks changed flag
                    boolean isChanged = false;
                    List<SGFigure> fList = this.mWnd.getVisibleFigureList();
                    for (SGFigure figure : fList) {
                        if (figure.isChanged()) {
                            isChanged = true;
                            break;
                        }
                    }
                    if (!isChanged) {
                        for (SGFigure figure : fList) {
                            SGIFigureElement[] array = figure.getIFigureElementArray();
                            for (int jj = array.length - 1; jj >= 0; jj--) {
                                if (array[jj].isFocusedObjectsChanged()) {
                                    isChanged = true;
                                    break;
                                }
                            }
                        }
                    }
                    
                    if (isChanged) {
                        // update the client rectangle
                        this.mWnd.updateClientRect();
                        // this.setScrollBarValue();

                        // notify change of the location to the root
                        this.mWnd.notifyToRoot();
                    }
            	}

            	/*
                // notify to the figures that the mouse is released
                for (int ii = 0; ii < figureList.size(); ii++) {
                    SGFigure figure = (SGFigure) figureList.get(ii);
                    figure.onMouseReleased(e);
                }
                
                boolean isChanged = false;

                // save the history
                List<SGFigure> fList = this.mWnd.getFocusedFigureList();
                for (int ii = 0; ii < fList.size(); ii++) {
                    SGFigure figure = (SGFigure) fList.get(ii);
                    if (figure.isFigureMoved()) {
                        figure.setChanged(true);
                        isChanged = true;
                    }
                }

                fList = this.mWnd.getVisibleFigureList();
                for (int ii = 0; ii < fList.size(); ii++) {
                    SGFigure figure = (SGFigure) fList.get(ii);
                    SGIFigureElement[] array = figure.getIFigureElementArray();
                    for (int jj = array.length - 1; jj >= 0; jj--) {
                        array[jj].setChangedFocusedObjects();
                        if (array[jj].isFocusedObjectsChanged()) {
                            isChanged = true;
                        }
                    }
                }
                
                if (isChanged) {
                    // update the client rectangle
                    this.mWnd.updateClientRect();
                    // this.setScrollBarValue();

                    // notify change of the location to the root
                    this.mWnd.notifyToRoot();
                }
                */
                
                // repaint
                this.mWnd.repaintContentPane();
            }
        }

        /**
         * Invoked when the mouse enters a component.
         */
        public void mouseEntered(final MouseEvent e) {
        }

        /**
         * Invoked when the mouse exits a component.
         */
        public void mouseExited(final MouseEvent e) {

            // set the default cursor
            this.mWnd.setCursor(Cursor.getDefaultCursor());
            
            // set extra region flag to false
            List fList = mWnd.getVisibleFigureList();
            for (int ii = 0; ii < fList.size(); ii++) {
                SGFigure f = (SGFigure) fList.get(ii);
                f.setExtraRegionFlag(false);
            }
            repaint();
        }

        /**
         * Invoked when a mouse button is pressed on a component and then
         * dragged.
         */
        public void mouseDragged(final MouseEvent e) {
            
            // set to the position label
            this.mWnd.setPositionLabel(e.getX(), e.getY());

            // notify to figures
            // if insertion toggle button is selected
            if (this.mWnd.isInsertFlagSelected()) {
                return;
            }

            // if the right button is pressed, there is nothing to do
            // for the mouse drag event
            if (SwingUtilities.isRightMouseButton(e)) {
                return;
            }

            ArrayList<SGFigure> list = this.mWnd.getVisibleFigureList();
            for (int ii = list.size() - 1; ii >= 0; ii--) {
                SGFigure figure = (SGFigure) list.get(ii);
                if (figure.onMouseDragged(e) == true) {
                    this.mWnd.repaintContentPane();
                    return;
                }
            }
            this.mWnd.mTempMouseLocation.setLocation(e.getPoint());
            this.mWnd.repaintContentPane();
        }

        /**
         * Invoked when the mouse cursor has been moved onto a component but no
         * buttons have been pushed.
         */
        public void mouseMoved(final MouseEvent e) {
            
            final int x = e.getX();
            final int y = e.getY();
            
            // set to the position label
            this.mWnd.setPositionLabel(x, y);
            
            // notify to the figures
            ArrayList<SGFigure> list = this.mWnd.getVisibleFigureList();
            boolean inExtraRegion = false;
            for (int ii = list.size() - 1; ii >= 0; ii--) {
                SGFigure figure = (SGFigure) list.get(ii);
                if (inExtraRegion) {
                    figure.setExtraRegionFlag(false);
                } else {
                    final boolean b = figure.checkExtraRegion(x, y);
                    if (b) {
                        inExtraRegion = true;
                    }
                }
            }

            for (int ii = list.size() - 1; ii >= 0; ii--) {
                SGFigure figure = (SGFigure) list.get(ii);
                if (figure.onMouseMoved(e)) {
                    break;
                }
            }
            
            String axisValueString = "";
            for (int ii = list.size() - 1; ii >= 0; ii--) {
                SGFigure figure = (SGFigure) list.get(ii);
                Rectangle2D rect = figure.getGraphRect();
                if (rect.contains(x, y)) {
                	axisValueString = figure.getAxisValueString(x, y);
                	break;
                }
            }
        	this.mWnd.setAxisValueLabel(axisValueString);
            
            this.repaint();
        }

        /**
         * 
         */
        private boolean clickFigures(final MouseEvent e) {
            ArrayList list = this.mWnd.getVisibleFigureList();
            for (int ii = list.size() - 1; ii >= 0; ii--) {
                SGFigure figure = (SGFigure) list.get(ii);
                if (figure.onMouseClicked(e)) {
                    return true;
                }

            }
            return false;
        }

        /**
         * 
         */
        private boolean pressFigures(final MouseEvent e) {
            ArrayList list = this.mWnd.getVisibleFigureList();
            for (int ii = list.size() - 1; ii >= 0; ii--) {
                SGFigure figure = (SGFigure) list.get(ii);

                if (figure.onMousePressed(e)) {
                    return true;
                }
            }

            return false;
        }

        /**
         * Key Event
         */
        private boolean onKeyPressed(final KeyEvent e) {
            boolean effective = false;
            ArrayList<SGFigure> list = this.mWnd.getVisibleFigureList();
            for (int ii = list.size() - 1; ii >= 0; ii--) {
                SGFigure figure = (SGFigure) list.get(ii);
                if (figure.onKeyPressed(e)) {
                    effective = true;
                }
            }
            if (effective) {
                this.mWnd.notifyToRoot();
            }
            return effective;
        }
    }

    /**
     * A panel to display background image.
     *
     */
    private static class ImagePanel extends InnerPanel {

        private static final long serialVersionUID = 3909225890671157942L;

        private SGImage mImage = null;
        
        private String mImageFilePath = null;

        private float mImageLocationX;

        private float mImageLocationY;

        protected ImagePanel(SGDrawingWindow wnd) {
            super(wnd);
            this.setVisible(true);
            this.setOpaque(false);
            this.mImage = new SGImage(this);
        }

        public boolean setImage(Image img) {
            if (this.mImage.setImage(img) == false) {
                return false;
            }
            this.mImage.setDafaultImageSize();
            return true;
        }

        public boolean setImageLocation(final float x, final float y) {
            this.mImageLocationX = x;
            this.mImageLocationY = y;
            return true;
        }

        public boolean setImageSize(final float w, final float h) {
            return this.mImage.setImageSize(w, h);
        }
        
        public void setImageFilePath(final String path) {
        	this.mImageFilePath = path;
        }

        public Image getImage() {
            return this.mImage.getImage();
        }

        public SGTuple2f getImageLocation() {
            return new SGTuple2f(this.mImageLocationX, this.mImageLocationY);
        }

        public SGTuple2f getImageSize() {
            return this.mImage.getImageSize();
        }
        
        public String getImageFilePath() {
        	return this.mImageFilePath;
        }

        public void setMagnification(final float mag) {
            super.setMagnification(mag);
            this.mImage.setMagnification(mag);
        }

        public float getScalingFactor() {
            return this.mImage.getScalingFactor();
        }

        public boolean setScalingFactor(final float f) {
            return this.mImage.setScalingFactor(f);
        }

        /**
         * 
         */
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);

            if (this.mImage == null) {
                return;
            }
            // get the rectangle of paper
            Rectangle2D pRect = this.mWnd.getPaperRect();
            final float pw = (float) pRect.getWidth();
            final float ph = (float) pRect.getHeight();

            // set the location of image
            Rectangle2D cRect = this.mWnd.getClientRect();
            final float mag = this.mMagnification;
            final float x = (float) cRect.getX() + mag * this.mImageLocationX;
            final float y = (float) cRect.getY() + mag * this.mImageLocationY;
            this.mImage.setImageLocation(x, y);

            // draw image
            g.setClip((int) x, (int) y, (int) pw, (int) ph);
            this.mImage.drawImage(g);
        }

    }

    /**
     * Panel to draw grid lines and paper.
     */
    private static class PaperGridPanel extends InnerPanel {
        /**
         * 
         */
        private static final long serialVersionUID = 3222608753764251169L;

        /**
         * 
         */
        private boolean mGridVisibleFlag = SGIRootObjectConstants.DEFAULT_GRID_VISIBLE;

        /**
         * 
         */
        private float mGridInterval = SGIRootObjectConstants.DEFAULT_GRID_INTERVAL;

        /**
         * 
         */
        private float mGridLineWidth = SGIRootObjectConstants.DEFAULT_GRID_LINE_WIDTH;

        /**
         * 
         */
        private Color mGridLineColor = SGIRootObjectConstants.DEFAULT_GRID_LINE_COLOR;

        /**
         * 
         */
        private Color mPaperColor = SGIRootObjectConstants.DEFAULT_PAPER_COLOR;

        /**
         * 
         */
        protected PaperGridPanel(SGDrawingWindow wnd) {
            super(wnd);
            this.setVisible(true);
            this.setOpaque(true);
        }

        /**
         * 
         */
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);
            
            if (this.mWnd == null) {
            	return;
            }

            final Graphics2D g2d = (Graphics2D) g;

            // get the rectangle of paper
            Rectangle2D pRect = this.mWnd.getPaperRect();
            final float px = (float) pRect.getX();
            final float py = (float) pRect.getY();
            final float pw = (float) pRect.getWidth();
            final float ph = (float) pRect.getHeight();

            // fill the paper rectangle
            g2d.setPaint(this.getPaperColor());
            g2d.fill(pRect);

            final Line2D line = new Line2D.Float();

            // draw the grid lines
            if (this.isGridVisible()) {

                // set the property of grid lines
                g2d.setStroke(new BasicStroke(this.getGridLineWidth()));
                g2d.setPaint(this.getGridLineColor());

                // vertical lines
                final float[] xArray = this.getVerticalGridLocation();
                for (int ii = 0; ii < xArray.length; ii++) {
                    final float x = xArray[ii];
                    line.setLine(x, py, x, py + ph);
                    g2d.draw(line);
                }

                // horizontal lines
                final float[] yArray = this.getHorizontalGridLocation();
                for (int ii = 0; ii < yArray.length; ii++) {
                    final float y = yArray[ii];
                    line.setLine(px, y, px + pw, y);
                    g2d.draw(line);
                }

            }

            // draw the edge of paper
            g2d.setPaint(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            g2d.draw(pRect);

        }

        /**
         * 
         * @return
         */
        public float[] getVerticalGridLocation() {
            final float space = this.mMagnification * this.getGridInterval();

            // get the rectangle of paper
            Rectangle2D pRect = this.mWnd.getPaperRect();
            final float px = (float) pRect.getX();
            final float pw = (float) pRect.getWidth();

            Rectangle2D vpRect = this.mWnd.getViewportBounds();
            final float vx = (float) vpRect.getX();
            final float vw = (float) vpRect.getWidth();

            ArrayList list = new ArrayList();
            int cnt = (int) ((vx - px) / space) + 1;
            while (true) {
                final float x = cnt * space + px;

                if (x > vx + vw) {
                    break;
                }

                if (x > px + pw) {
                    break;
                }

                list.add(Float.valueOf(x));
                cnt++;
            }

            final float[] array = new float[list.size()];
            for (int ii = 0; ii < array.length; ii++) {
                array[ii] = ((Float) list.get(ii)).floatValue();
            }

            return array;
        }

        /**
         * 
         * @return
         */
        public float[] getHorizontalGridLocation() {
            final float space = this.mMagnification * this.getGridInterval();

            // get the rectangle of paper
            Rectangle2D pRect = this.mWnd.getPaperRect();
            final float py = (float) pRect.getY();
            final float ph = (float) pRect.getHeight();

            Rectangle2D vpRect = this.mWnd.getViewportBounds();
            final float vy = (float) vpRect.getY();
            final float vh = (float) vpRect.getHeight();

            ArrayList list = new ArrayList();
            int cnt = (int) ((vy - py) / space) + 1;
            while (true) {
                final float y = cnt * space + py;

                if (y > vy + vh) {
                    break;
                }

                if (y > py + ph) {
                    break;
                }

                list.add(Float.valueOf(y));
                cnt++;
            }

            final float[] array = new float[list.size()];
            for (int ii = 0; ii < array.length; ii++) {
                array[ii] = ((Float) list.get(ii)).floatValue();
            }

            return array;
        }

        /**
         * @return
         */
        public float getGridInterval() {
            return this.mGridInterval;
        }

        /**
         * @return
         */
        public Color getGridLineColor() {
            return this.mGridLineColor;
        }

        /**
         * @return
         */
        public float getGridLineWidth() {
            return this.mGridLineWidth;
        }

        /**
         * @return
         */
        public boolean isGridVisible() {
            return this.mGridVisibleFlag;
        }

        /**
         * @param f
         */
        public void setGridInterval(final float f) {
            this.mGridInterval = f;
        }

        /**
         * @param color
         */
        public void setGridLineColor(final Color color) {
            this.mGridLineColor = color;
        }

        /**
         * @param f
         */
        public void setGridLineWidth(final float f) {
            this.mGridLineWidth = f;
        }

        /**
         * @param b
         */
        public void setGridVisible(final boolean b) {
            this.mGridVisibleFlag = b;
        }

        /**
         * @return
         */
        public Color getPaperColor() {
            return this.mPaperColor;
        }

        /**
         * @param color
         */
        public void setPaperColor(Color color) {
            this.mPaperColor = color;
        }

    }

    /**
     * Panel to draw rulers.
     */
    private static class RulerPanel extends InnerPanel implements
            MouseInputListener {
        /**
         * 
         */
        private static final long serialVersionUID = -2106060300405970401L;

        // font of ruler
        private static final float RULER_FONT_SIZE = 11.0f;

        private static final String RULER_FONT_NAME = "Serif";

        private static final int RULER_FONT_STYLE = Font.PLAIN;

        /**
         * 
         */
        private static final int RULER_WIDTH = 20;

        // private static final int ANCHOR_SIZE = 12;
        private static final Color INNER_COLOR_1 = new Color(234, 238, 232);

        private static final Color INNER_COLOR_2 = new Color(124, 155, 64);

        // private static final Color ANCHOR_INNER_COLOR = new
        // Color(222,222,222);
        // private static final float ANCHOR_EDGE_LINE_WIDTH = 4.0f;
        // private static final Color ANCHOR_EDGE_LINE_COLOR = Color.WHITE;
        private static final Color LINE_COLOR = Color.BLACK;

        private static final float LINE_WIDTH = 2.0f;

        private static final BasicStroke LINE_STROKE = new BasicStroke(
                LINE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f,
                new float[] { 5 * LINE_WIDTH, LINE_WIDTH, LINE_WIDTH,
                        2 * LINE_WIDTH }, 0.0f);

        /**
         * 
         */
        private Font mRulerFont = null;

        private Point2D mHorizontalLocation = null;

        private Point2D mVerticalLocation = null;

        private boolean mDrawHorizontalLineFlag = false;

        private boolean mDrawVerticalLineFlag = false;

        /**
         * 
         */
        protected RulerPanel(SGDrawingWindow wnd) {
            super(wnd);

            this.setVisible(true);
            this.setOpaque(false);

            this.mRulerFont = new Font(RULER_FONT_NAME, RULER_FONT_STYLE,
                    (int) RULER_FONT_SIZE);

        }

        /**
         * 
         */
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (this.mWnd == null) {
            	return;
            }

            final Graphics2D g2d = (Graphics2D) g;

            // if( mRulerVisibleFlag )
            {
                this.drawRuler(g2d);
            }

            // lines
            if (this.mDrawHorizontalLineFlag) {
                this.drawHorizontalLine(g2d);
            }

            if (this.mDrawVerticalLineFlag) {
                this.drawVerticalLine(g2d);
            }

            //
            // anchor
            //

            // horizontal
            {
                Point2D pos = null;
                if (this.mDrawHorizontalLineFlag) {
                    pos = this.mHorizontalLocation;
                } else {
                    pos = this.getHorizontalAnchorLocationFromPaper();
                }
                this.drawHorizontalAnchor((int) pos.getX(), (Graphics2D) g2d
                        .create());
            }

            // vertical
            {
                Point2D pos = null;
                if (this.mDrawVerticalLineFlag) {
                    pos = this.mVerticalLocation;
                } else {
                    pos = this.getVerticalAnchorLocationFromPaper();
                }
                this.drawVerticalAnchor((int) pos.getY(), (Graphics2D) g2d
                        .create());
            }

        }

        private void drawHorizontalAnchor(final int x, final Graphics2D g2d) {

            final int nPoints = 7;
            final int[] xPos = new int[nPoints];
            final int[] yPos = new int[nPoints];

            final int y = (int) this.getHorizontalAnchorLocationFromPaper()
                    .getY() - 1;

            xPos[0] = x + 3;
            yPos[0] = y - 10;

            xPos[1] = xPos[0];
            yPos[1] = y - 6;

            xPos[2] = x + 6;
            yPos[2] = yPos[1];

            xPos[3] = x;
            yPos[3] = y;

            xPos[4] = x - 6;
            yPos[4] = yPos[2];

            xPos[5] = x - 3;
            yPos[5] = yPos[4];

            xPos[6] = xPos[5];
            yPos[6] = yPos[0];

            this.drawAnchor(xPos, yPos, nPoints, g2d);
        }

        private void drawVerticalAnchor(final int y, final Graphics2D g2d) {
            // final int size = ANCHOR_SIZE;
            final int nPoints = 7;
            final int[] xPos = new int[nPoints];
            final int[] yPos = new int[nPoints];

            final int x = (int) this.getVerticalAnchorLocationFromPaper()
                    .getX() - 1;

            xPos[0] = x - 10;
            yPos[0] = y + 3;

            xPos[1] = x - 6;
            yPos[1] = yPos[0];

            xPos[2] = xPos[1];
            yPos[2] = y + 6;

            xPos[3] = x;
            yPos[3] = y;

            xPos[4] = xPos[2];
            yPos[4] = y - 6;

            xPos[5] = xPos[4];
            yPos[5] = y - 3;

            xPos[6] = xPos[0];
            yPos[6] = yPos[5];

            this.drawAnchor(xPos, yPos, nPoints, g2d);
        }

        private void drawAnchor(final int[] xPos, final int[] yPos,
                final int nPoints, final Graphics2D g2d) {
            final Shape sh = new Polygon(xPos, yPos, nPoints);

            final int x1 = xPos[6];
            final int y1 = yPos[6];
            final int x2 = (xPos[2] + xPos[3]) / 2;
            final int y2 = (yPos[2] + yPos[3]) / 2;

            GradientPaint gPaint = new GradientPaint(x1, y1, INNER_COLOR_1, x2,
                    y2, INNER_COLOR_2);
            g2d.setPaint(gPaint);
            g2d.fill(sh);

            g2d.setStroke(new BasicStroke(1));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setPaint(Color.BLACK);
            g2d.draw(sh);

        }

        private void drawHorizontalLine(final Graphics2D g2d) {
            if (this.mHorizontalLocation == null) {
                return;
            }
            Point2D pos = this.mHorizontalLocation;
            // final float x = (float)pos.getX();
            Line2D line = new Line2D.Float();
            line.setLine(pos.getX(), this
                    .getHorizontalAnchorLocationFromPaper().getY(), pos.getX(),
                    this.getHeight());
            g2d.setPaint(LINE_COLOR);
            g2d.setStroke(LINE_STROKE);
            g2d.draw(line);
        }

        private void drawVerticalLine(final Graphics2D g2d) {
            if (this.mVerticalLocation == null) {
                return;
            }
            Point2D pos = this.mVerticalLocation;
            Line2D line = new Line2D.Float();
            line.setLine(this.getVerticalAnchorLocationFromPaper().getX(), pos
                    .getY(), this.getWidth(), pos.getY());
            g2d.setPaint(LINE_COLOR);
            g2d.setStroke(LINE_STROKE);
            g2d.draw(line);
        }

        /**
         * 
         * @return
         */
        private boolean isPressed() {
            return this.mDrawHorizontalLineFlag || this.mDrawVerticalLineFlag;
        }

        private Point2D getHorizontalAnchorLocationFromPaper() {
            Rectangle2D rect = this.mWnd.getPaperRect();
            final float x = (float) rect.getX();
            // final float y = (float)rect.getY();
            final float w = (float) rect.getWidth();
            final int rw = RulerPanel.RULER_WIDTH;
            Point2D pos = new Point2D.Float(x + rw + w, rw);
            return pos;
        }

        private Point2D getVerticalAnchorLocationFromPaper() {
            Rectangle2D rect = this.mWnd.getPaperRect();
            // final float x = (float)rect.getX();
            final float y = (float) rect.getY();
            final float h = (float) rect.getHeight();
            final int rw = RulerPanel.RULER_WIDTH;
            Point2D pos = new Point2D.Float(rw, y + rw + h);
            return pos;
        }

        private float getPaperStartX() {
            Rectangle2D rect = this.mWnd.getPaperRect();
            final float x = (float) rect.getX();
            final int rw = RulerPanel.RULER_WIDTH;
            return x + rw;
        }

        private float getPaperStartY() {
            Rectangle2D rect = this.mWnd.getPaperRect();
            final float y = (float) rect.getY();
            final int rw = RulerPanel.RULER_WIDTH;
            return y + rw;
        }

        // private float getPaperEndX()
        // {
        // Rectangle2D rect = this.mWnd.getPaperRect();
        // final float x = (float)rect.getX();
        // final float w = (float)rect.getWidth();
        // final int rw = RulerPanel.RULER_WIDTH;
        // return x + rw + w;
        // }

        // private float getPaperEndY()
        // {
        // Rectangle2D rect = this.mWnd.getPaperRect();
        // final float y = (float)rect.getY();
        // final float h = (float)rect.getHeight();
        // final int rw = RulerPanel.RULER_WIDTH;
        // return y + rw + h;
        // }

        /**
         * 
         */
        private boolean drawRuler(final Graphics2D g2d) {

            //
            final int rw = RULER_WIDTH;
            final int width = this.getWidth();
            final int height = this.getHeight();

            final int nPoints = 6;
            final int[] xPoints = new int[nPoints];
            final int[] yPoints = new int[nPoints];

            xPoints[0] = 0;
            yPoints[0] = 0;

            xPoints[1] = width;
            yPoints[1] = 0;

            xPoints[2] = width;
            yPoints[2] = rw;

            xPoints[3] = rw;
            yPoints[3] = rw;

            xPoints[4] = rw;
            yPoints[4] = height;

            xPoints[5] = 0;
            yPoints[5] = height;

            final Polygon polygon = new Polygon(xPoints, yPoints, nPoints);

            //
            // paint
            //

            Color bgColor = this.getBackground();
            Color lineColor = Color.BLACK;

            g2d.setPaint(bgColor);
            g2d.fill(polygon);
            g2d.setPaint(lineColor);
            g2d.setStroke(new BasicStroke(1));
            g2d.draw(polygon);

            // draw the lines of rulers
            this.drawNumbersAndLines(g2d);

            // corner
            final Rectangle2D rectCorner = new Rectangle2D.Float(0.0f, 0.0f,
                    rw, rw);
            g2d.setPaint(bgColor);
            g2d.fill(rectCorner);
            g2d.setPaint(lineColor);
            g2d.draw(rectCorner);

            // g2d.setStroke( new BasicStroke(2) );
            //
            // Line2D left = new Line2D.Float( 0.0f, 0.0f, 0.0f, rw );
            // Line2D top = new Line2D.Float( 0.0f, 0.0f, rw, 0.0f );
            // g2d.setPaint( Color.WHITE );
            // g2d.draw( left );
            // g2d.draw( top );
            //
            // Line2D right = new Line2D.Float( rw, 0.0f, rw, rw );
            // Line2D bottom = new Line2D.Float( 0.0f, rw, rw, rw );
            // g2d.setPaint( Color.BLACK );
            // g2d.draw( right );
            // g2d.draw( bottom );

            return true;

        }

        /**
         * 
         */
        private boolean drawNumbersAndLines(final Graphics2D g2d) {

            final int rw = RULER_WIDTH;
            final float mag = this.mMagnification;
            final int width = this.getWidth();
            final int height = this.getHeight();
            final Rectangle vpRect = new Rectangle(0, 0, width - rw, height
                    - rw);

            final Rectangle2D pRect = this.mWnd.getPaperRect();
            // final Rectangle2D cRect = this.mWnd.getClientRect();

            // final float hStart = rw + (float)cRect.getX();
            // final float vStart = rw + (float)cRect.getY();

            final Line2D line = new Line2D.Float();

            g2d.setPaint(Color.BLACK);

            //
            // axis lines
            //

            g2d.setStroke(new BasicStroke(2));

            // horizontal
            {
                final float x1 = rw;
                final float x2 = width;
                final float y = rw;
                line.setLine(x1, y, x2, y);
                g2d.draw(line);
            }

            // vertical
            {
                final float x = rw;
                final float y1 = rw;
                final float y2 = height;
                line.setLine(x, y1, x, y2);
                g2d.draw(line);
            }

            //
            // scale lines and numbers
            //

            Font font = this.mRulerFont;
            g2d.setStroke(new BasicStroke(1));
            g2d.setFont(font);
            final float fSize = font.getSize();

            int offset;
            int subNum;
            if (mag < 0.50f) {
                offset = 4;
                subNum = 8;
            } else if (mag < 1.0f) {
                offset = 2;
                subNum = 4;
            } else {
                offset = 1;
                subNum = 4;
            }

            final float factor = mag / CM_POINT_RATIO;

            // horizontal
            {
                final ArrayList numberList = new ArrayList();
                final float px = (float) pRect.getX();
                final float pOffset = -px;
                final float pOffsetInRulerUnit = pOffset / factor;
                final float endLocation = (float) vpRect.getWidth() + pOffset;
                int diff = (int) pOffsetInRulerUnit;
                if (diff % offset != 0) {
                    diff = (diff / offset) * offset;
                }

                int cnt = diff;
                while (true) {
                    // System.out.print(cnt+" ");
                    final float location = factor * cnt;
                    if (location > endLocation) {
                        break;
                    }

                    numberList.add(Integer.valueOf(cnt));
                    cnt += offset;
                }
                // System.out.println();

                // draw
                for (int ii = 0; ii < numberList.size(); ii++) {
                    // main
                    final int num = ((Integer) numberList.get(ii)).intValue();
                    final float location = num * factor;
                    final float pos = px + rw + location;
                    line.setLine(pos, 0.20f * rw, pos, rw);
                    g2d.draw(line);

                    // number
                    final int x = (int) (pos + 0.3f * fSize);
                    final int y = (int) fSize + 1;
                    g2d.drawString(Integer.toString(num), x, y);

                    // sub
                    for (int jj = 0; jj < subNum; jj++) {
                        final float pos_ = pos + 0.2f * (jj + 1) * factor
                                * offset;

                        line.setLine(pos_, 0.75f * rw, pos_, rw);
                        g2d.draw(line);
                    }

                }

            }

            // vertical
            {
                final ArrayList numberList = new ArrayList();
                final float py = (float) pRect.getY();
                final float pOffset = -py;
                final float pOffsetInRulerUnit = pOffset / factor;
                final float endLocation = (float) vpRect.getHeight() + pOffset;
                int diff = (int) pOffsetInRulerUnit;
                if (diff % offset != 0) {
                    diff = (diff / offset) * offset;
                }

                int cnt = diff;
                while (true) {
                    // System.out.print(cnt+" ");
                    final float location = factor * cnt;
                    if (location > endLocation) {
                        break;
                    }

                    numberList.add(Integer.valueOf(cnt));
                    cnt += offset;
                }
                // System.out.println();

                // draw
                for (int ii = 0; ii < numberList.size(); ii++) {

                    // main
                    final int num = ((Integer) numberList.get(ii)).intValue();
                    final float location = num * factor;
                    final float pos = py + rw + location;
                    line.setLine(0.20f * rw, pos, rw, pos);
                    g2d.draw(line);

                    // number
                    final int x = (int) (0.20f * rw);
                    int y = (int) (pos + fSize + 1);
                    char[] array = Integer.toString(num).toCharArray();
                    for (int jj = 0; jj < array.length; jj++) {
                        Character c = Character.valueOf(array[jj]);
                        g2d.drawString(c.toString(), x, y);
                        y += (int) fSize;
                    }

                    // sub
                    for (int jj = 0; jj < subNum; jj++) {
                        final float pos_ = pos + 0.2f * (jj + 1) * factor
                                * offset;
                        line.setLine(0.750f * rw, pos_, rw, pos_);
                        g2d.draw(line);
                    }
                }

            }

            return true;
        }

        //
        // mouse event
        //

        /**
         * Invoked when the mouse button has been clicked (pressed and released)
         * on a component.
         */
        public void mouseClicked(final MouseEvent e) {
        }

        /**
         * Invoked when a mouse button has been pressed on a component.
         */
        public void mousePressed(final MouseEvent e) {
            final int size = 10;

            {
                Point2D pos = this.getHorizontalAnchorLocationFromPaper();
                final int posX = (int) pos.getX();
                final int posY = (int) pos.getY();
                Rectangle rect = new Rectangle(posX - size / 2, posY - size,
                        size, size);

                if (rect.contains(e.getPoint())) {
                    this.mDrawHorizontalLineFlag = true;
                    this.mHorizontalLocation = pos;
                    this.repaint();
                    return;
                }
            }

            {
                Point2D pos = this.getVerticalAnchorLocationFromPaper();
                final int posX = (int) pos.getX();
                final int posY = (int) pos.getY();
                Rectangle rect = new Rectangle(posX - size, posY - size / 2,
                        size, size);

                if (rect.contains(e.getPoint())) {
                    this.mDrawVerticalLineFlag = true;
                    this.mVerticalLocation = pos;
                    this.repaint();
                    return;
                }
            }

            this.mDrawHorizontalLineFlag = false;
            this.mDrawVerticalLineFlag = false;
            this.mHorizontalLocation = null;
            this.mVerticalLocation = null;
            this.repaint();
        }

        /**
         * Invoked when a mouse button has been released on a component.
         */
        public void mouseReleased(final MouseEvent e) {
            if (this.isPressed()) {
                final int x = e.getX();
                final int y = e.getY();

                // Rectangle2D paper = this.mWnd.getPaperRect();
                final SGTuple2f paperSize = this.mWnd.getPaperSize();
                final float startX = this.getPaperStartX();
                final float startY = this.getPaperStartY();

                // final float wOld = (float)paper.getWidth();
                // final float hOld = (float)paper.getHeight();

                final float ratio = SGIConstants.CM_POINT_RATIO;
                final float minWidth = (float) SGIRootObjectConstants.PAPER_WIDTH_MIN_VALUE
                        / ratio;
                final float minHeight = (float) SGIRootObjectConstants.PAPER_HEIGHT_MIN_VALUE
                        / ratio;
                final float maxWidth = (float) SGIRootObjectConstants.PAPER_WIDTH_MAX_VALUE
                        / ratio;
                final float maxHeight = (float) SGIRootObjectConstants.PAPER_HEIGHT_MAX_VALUE
                        / ratio;

                if (this.mDrawHorizontalLineFlag) {
                    float w = (x - startX) / this.mMagnification;
                    if (w < minWidth) {
                        w = minWidth;
                    }
                    if (w > maxWidth) {
                        w = maxWidth;
                    }

                    this.mWnd.setPaperSize(w, paperSize.y);
                    this.mWnd.setChanged(true);
                }

                if (this.mDrawVerticalLineFlag) {
                    float h = (y - startY) / this.mMagnification;
                    if (h < minHeight) {
                        h = minHeight;
                    }
                    if (h > maxHeight) {
                        h = maxHeight;
                    }

                    this.mWnd.setPaperSize(paperSize.x, h);
                    this.mWnd.setChanged(true);
                }

                if (this.mDrawHorizontalLineFlag || this.mDrawVerticalLineFlag) {
                    this.mWnd.updateClientRect();
                    this.mWnd.notifyToRoot();
                    this.repaint();
                }

                this.mDrawHorizontalLineFlag = false;
                this.mDrawVerticalLineFlag = false;
                this.mHorizontalLocation = null;
                this.mVerticalLocation = null;
                this.mWnd.repaintContentPane();
            }
        }

        /**
         * Invoked when the mouse enters a component.
         */
        public void mouseEntered(final MouseEvent e) {
        }

        /**
         * Invoked when the mouse exits a component.
         */
        public void mouseExited(final MouseEvent e) {
        }

        /**
         * Invoked when a mouse button is pressed on a component and then
         * dragged.
         */
        public void mouseDragged(final MouseEvent e) {
            if (this.isPressed()) {
                this.mHorizontalLocation = e.getPoint();
                this.mVerticalLocation = e.getPoint();
                this.mWnd.repaintContentPane();
            }
        }

        /**
         * Invoked when the mouse cursor has been moved onto a component but no
         * buttons have been pushed.
         */
        public void mouseMoved(final MouseEvent e) {
        }

    }

}

package jp.riken.brain.ni.samuraigraph.base;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A figure in which data, axes and other various objects are drawn.
 */
public abstract class SGFigure implements ActionListener, SGIConstants,
        SGIUndoable, SGIMovable, SGIPaintable,
        SGIFigureDialogObserver, SGINode, SGIRootObjectConstants, SGIVisible,
        SGIDisposable, SGIFigureConstants {
	
    // ID-number of this figure.
    private int mID;

    /**
     * A window which this figure belongs to.
     */
    protected SGDrawingWindow mWnd = null;

    /**
     * List of data objects.
     */
    protected List<SGData> mDataList = new ArrayList<SGData>();

    /**
     * X-coordinate of the graph rectangle in the client area.
     */
    protected float mGraphRectX = 0.0f;

    /**
     * Y-coordinate of the graph rectangle in the client area.
     */
    protected float mGraphRectY = 0.0f;

    /**
     * Width of the graph rectangle.
     */
    protected float mGraphRectWidth = 0.0f;

    /**
     * Height of the graph rectangle.
     */
    protected float mGraphRectHeight = 0.0f;

    /**
     * The magnification.
     */
    protected float mMagnification = 1.0f;

    /**
     * The property dialog of this figure.
     */
    protected SGPropertyDialog mPropertyDialog = null;

    /**
     *
     */
    private SGIFigureElement mPressedElement = null;

    /**
     *
     */
    private Point mPressedPoint = new Point();

    /**
     *
     */
    private final Rectangle2D mTempFigureRect = new Rectangle2D.Float();

    /**
     *
     */
    private Color mBackgroundColor;

    /**
     * A rectangle for mouse drag.
     */
    private final Rectangle2D mDraggingRect = new Rectangle2D.Float();

    /**
     * Whether to snap to grid.
     */
    private static boolean mSnapToGridFlag = DEFAULT_FIGURE_SNAP_TO_GRID;

    /**
     * A rectangle for the rubber band.
     */
    private final Rectangle2D mRubberBandRect = new Rectangle2D.Float();

    /**
     * Whether to use rubber band on dragging the figures.
     */
    static boolean mRubberBandFlag = DEFAULT_FIGURE_RUBBER_BANDING_ENABLED;

    /**
     * Whether to show rubber band on dragging the figures.
     */
    static boolean mRubberBandVisibleFlag = false;

    /**
     * location of mouse pointer
     */
    private int mMouseLocation = 0;

    /**
     *
     */
    protected static final float MIN_WIDTH = 50.0f;

    /**
     *
     */
    protected static final float MAX_WIDTH = 50.0f;

    /**
     *
     */
    static boolean mBoundingBoxVisibleFlag = DEFAULT_FIGURE_BOUNDING_BOX_VISIBLE;

    /**
     *
     */
    protected SGProperties mTemporaryProperties = null;

    /**
     *
     */
    private boolean mTransparentFlag = false;

    public static final String MENUCMD_SHOW_BOUNDING_BOX = "Show Bounding Box";

    public static final String MENUCMD_LEGEND_VISIBLE = "Legend On/Off";

    public static final String MENUCMD_COLOR_BAR_VISIBLE = "Color Bar On/Off";

    public static final String MENUCMD_GRID_VISIBLE = "Grid On/Off";

    public static final String MENUCMD_SCALE_VISIBLE = "Scale On/Off";

    public static final String MENUCMD_DATA_ANCHORED = "Data Anchor On/Off";
    
    /**
     * A flag whether data objects in this figure are anchored.
     */
    protected boolean mDataAnchorFlag = true;

    /**
     * Create a figure which belongs to the given window.
     *
     * @param wnd
     *            ths window this figure belongs
     */
    public SGFigure(final SGDrawingWindow wnd) {
        super();
        // this.setWindow(wnd);
        this.mWnd = wnd;
        this.mComponent = wnd.getFigurePanel();
        this.create();
    }

    /**
     *
     * @return
     */
    public String toString() {
        return "SGFigure:" + this.getID();
    }

    /**
     *
     * @return
     */
    private boolean create() {
//        this.createDialog();
//        this.createPopupMenu();

        // initialize properties
        this.setBackgroundColor(DEFAULT_FIGURE_BACKGROUND_COLOR);
        this.setVisible(true);
        // this.setOpaque(false);

        return true;
    }

    private boolean mVisibleFlag = true;

    public boolean isVisible() {
        return this.mVisibleFlag;
    }

    public void setVisible(final boolean b) {
        this.mVisibleFlag = b;
        if (!b) {
        	// hide the tooltip text
        	this.getComponent().setToolTipText(null);
        }
    }
    
    private JComponent mComponent = null;

    public JComponent getComponent() {
        return this.mComponent;
    }

    public void repaint() {
        this.mComponent.repaint();
    }

    public int getWidth() {
        return this.mComponent.getWidth();
    }

    public int getHeight() {
        return this.mComponent.getHeight();
    }

    //
    // setters and getters
    //

    /**
     *
     */
    public int getID() {
        return this.mID;
    }

    /**
     *
     */
    public boolean setID(final int id) {
        this.mID = id;
        return true;
    }

    /**
     *
     */
    public boolean setBackgroundColor(final Color color) {
        this.mBackgroundColor = color;
        return true;
    }

    /**
     *
     */
    public boolean setBackgroundColor(final String value) {
        final Color cl = SGUtilityText.getColor(value);
        if (cl == null) {
            return false;
        }
        return this.setBackgroundColor(cl);
    }

    /**
     *
     * @param r
     * @param g
     * @param b
     * @return
     */
    public boolean setBackgroundColor(final String r, final String g,
            final String b) {
        final Color cl = SGUtilityText.getColor(r, g, b);
        if (cl == null) {
            return false;
        }
        return this.setBackgroundColor(cl);
    }

    /**
     *
     */
    public Color getBackgroundColor() {
        return this.mBackgroundColor;
    }

    /**
     *
     */
    public boolean isTransparent() {
        return this.mTransparentFlag;
    }

    /**
     *
     */
    public SGDrawingWindow getWindow() {
        return this.mWnd;
    }

//    /**
//     *
//     * @return
//     */
//    public float getSpaceAxisLineAndNumber() {
//        return this.getAxisElement().getSpaceAxisLineAndNumber();
//    }
//
//    /**
//     *
//     */
//    public float getSpaceAxisLineAndNumber(final String unit) {
//        final float space = this.getSpaceAxisLineAndNumber();
//        return (float) SGUtilityText.convertFromPoint(space, unit);
//    }
//
//    /**
//     *
//     * @return
//     */
//    public float getSpaceNumberAndTitle() {
//        return this.getAxisElement().getSpaceNumberAndTitle();
//    }
//
//    /**
//     *
//     */
//    public float getSpaceNumberAndTitle(final String unit) {
//        final float space = this.getSpaceNumberAndTitle();
//        return (float) SGUtilityText.convertFromPoint(space, unit);
//    }
//
//    /**
//     *
//     * @return
//     */
//    public boolean setSpaceAxisLineAndNumber(final float space) {
//        final SGIFigureElementAxis aElement = this.getAxisElement();
//        final float s = aElement.getSpaceAxisLineAndNumber();
//        if (space != s) {
//            aElement.setSpaceAxisLineAndNumber(space);
//            aElement.setChanged(true);
//            this.updateGraphRect();
//        }
//        return true;
//    }
//
//    /**
//     * Sets the space between the axis lne and the axis numbers.
//     *
//     * @param value
//     *           a value to set to the space
//     * @param unit
//     *           an unit of a given value
//     * @return true if succeeded
//     */
//    public boolean setSpaceAxisLineAndNumber(final float value,
//            final String unit) {
//        final Float sPt = this.calcFigureScale(value, unit, FIGURE_SPACE_UNIT,
//                FIGURE_SPACE_TO_SCALE_MIN, FIGURE_SPACE_TO_SCALE_MAX);
//        if (sPt == null) {
//            return false;
//        }
//        if (this.equalLength(this.getSpaceAxisLineAndNumber(), sPt)) {
//            return true;
//        }
//        return this.setSpaceAxisLineAndNumber(sPt);
//    }
//
//    /**
//     *
//     * @return
//     */
//    public boolean setSpaceNumberAndTitle(final float space) {
//        final SGIFigureElementAxis aElement = this.getAxisElement();
//        final float s = aElement.getSpaceNumberAndTitle();
//        if (space != s) {
//            aElement.setSpaceNumberAndTitle(space);
//            aElement.setChanged(true);
//            this.updateGraphRect();
//        }
//        return true;
//    }
//
//    /**
//     * Sets the space between the axis number and the axis title.
//     *
//     * @param value
//     *           a value to set to the space
//     * @param unit
//     *           an unit of a given value
//     * @return true if succeeded
//     */
//    public boolean setSpaceNumberAndTitle(final float value, final String unit) {
//        final Float sPt = this.calcFigureScale(value, unit, FIGURE_SPACE_UNIT,
//                FIGURE_SPACE_TO_TITLE_MIN, FIGURE_SPACE_TO_TITLE_MAX);
//        if (sPt == null) {
//            return false;
//        }
//        if (this.equalLength(this.getSpaceNumberAndTitle(), sPt)) {
//            return true;
//        }
//        return this.setSpaceNumberAndTitle(sPt);
//    }

    /**
     *
     */
    public boolean setLegendVisible(boolean flag) {
        final SGIFigureElementLegend lElement = this.getLegendElement();
        final boolean v = lElement.isVisible();
        if (v != flag) {
            lElement.setVisible(flag);
            lElement.setChanged(true);
        }
        return true;
    }

    /**
     *
     */
    public boolean setTransparent(boolean flag) {
        this.mTransparentFlag = flag;
        return true;
    }

    // Map of figure elements.
    private final Map<Integer, SGIFigureElement> mFigureElementMap = new TreeMap<Integer, SGIFigureElement>();

    /**
     * Get the array of SGIFigureElement objects.
     *
     * @return the array of SGIFigureElement objects
     */
    public SGIFigureElement[] getIFigureElementArray() {
        ArrayList<SGIFigureElement> list = new ArrayList<SGIFigureElement>(this.mFigureElementMap.values());
        SGIFigureElement[] array = (SGIFigureElement[]) list
                .toArray(new SGIFigureElement[] {});
        return array;
    }

    /**
     * Returns a list of child nodes.
     *
     * @return a list of child nodes
     */
    public ArrayList<SGINode> getChildNodes() {
        ArrayList<SGINode> list = new ArrayList<SGINode>();
//        list.addAll(this.mFigureElementMap.values());
//        if (this.getAxisElement().isColorBarAvailable()) {
//            list.add(this.getAxisElement().getColorBar());
//        }
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (SGIFigureElement el : array) {
        	list.addAll(el.getNodes());
        }
        return list;
    }

    /**
     *
     * @return
     */
    public String getClassDescription() {
        return "Figure: " + this.getID();
    }

    /**
     *
     * @return
     */
    public String getInstanceDescription() {
        final float ratio = SGIConstants.CM_POINT_RATIO;
        final int order = SGIRootObjectConstants.LENGTH_MINIMAL_ORDER;
        final float x = (float) SGUtilityNumber.roundOffNumber(this.mGraphRectX
                * ratio, order - 1);
        final float y = (float) SGUtilityNumber.roundOffNumber(this.mGraphRectY
                * ratio, order - 1);
        String str = this.getClassDescription();
        StringBuffer sb = new StringBuffer();
        sb.append(str);
        sb.append(" ( X=");
        sb.append(x);
        sb.append("cm, Y=");
        sb.append(y);
        sb.append("cm )");
//        str += " ( X=" + x + "cm, Y=" + y + "cm )";
//        return str;
        return sb.toString();
    }

    /**
     * Returns the figure element of the given class.
     *
     * @param cl
     *           a class object
     * @return an figure element
     */
    public SGIFigureElement getIFigureElement(final Class cl) {
        if (cl == null) {
            throw new IllegalArgumentException("");
        }

        final SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (cl.isAssignableFrom(array[ii].getClass())) {
                return array[ii];
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public SGIFigureElementAxis getAxisElement() {
        return (SGIFigureElementAxis) this
                .getIFigureElement(SGIFigureElementAxis.class);
    }

    /**
     *
     * @return
     */
    public SGIFigureElementGraph getGraphElement() {
        return (SGIFigureElementGraph) this
                .getIFigureElement(SGIFigureElementGraph.class);
    }

    /**
     *
     * @return
     */
    public SGIFigureElementLegend getLegendElement() {
        return (SGIFigureElementLegend) this
                .getIFigureElement(SGIFigureElementLegend.class);
    }

    /**
     *
     * @param f
     */
    public void setIFigureElement(final int layer, final SGIFigureElement f) {
        if (f == null) {
            throw new IllegalArgumentException("");
        }

        final Class cl = f.getClass();
        Iterator<SGIFigureElement> itr = this.mFigureElementMap.values().iterator();
        while (itr.hasNext()) {
            final SGIFigureElement ff = itr.next();
            if (ff.getClass().equals(cl)) {
                throw new IllegalArgumentException("");
            }
        }

        this.mFigureElementMap.put(Integer.valueOf(layer), f);
    }

    /**
     * Clear focused objects.
     * @param ori
     *            an origin of this clearance
     */
    void clearFocusedObjects(SGIFigureElement ori) {
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].clearFocusedObjects(ori);
        }

        // deseclet this figure
        this.setSelected(false);
    }

//    /**
//     *
//     */
//    public boolean isGraphRectContains(Point2D point) {
//        final Rectangle2D rect = this.getGraphRect();
//        return rect.contains(point);
//    }
//
//    /**
//     *
//     */
//    public boolean isGraphRectContains(int x, int y) {
//        final Rectangle2D rect = this.getGraphRect();
//        return rect.contains(x, y);
//    }

    /**
     *
     */
    public Rectangle2D getGraphRect() {
        final Rectangle2D rect = new Rectangle2D.Float(this.getGraphRectX(),
                this.getGraphRectY(), this.getGraphRectWidth(), this
                        .getGraphRectHeight());
        return rect;
    }

    public Rectangle2D getGraphRectInClientRect() {
        final Rectangle2D rect = new Rectangle2D.Float(this.mGraphRectX,
                this.mGraphRectY, this.mGraphRectWidth, this.mGraphRectHeight);
        return rect;
    }

    /**
     *
     * @return
     */
    public float getGraphRectX() {
        final Rectangle2D cRect = this.mWnd.getPaperRect();
        return (float) cRect.getX() + this.mMagnification * this.mGraphRectX;
    }

    /**
     *
     * @return
     */
    public float getGraphRectY() {
        final Rectangle2D cRect = this.mWnd.getPaperRect();
        return (float) cRect.getY() + this.mMagnification * this.mGraphRectY;
    }

    /**
     *
     * @return
     */
    public float getGraphRectWidth() {
        return this.mMagnification * this.mGraphRectWidth;
    }

    /**
     *
     * @return
     */
    public float getGraphRectHeight() {
        return this.mMagnification * this.mGraphRectHeight;
    }

    /**
     *
     */
    public float getFigureX() {
        return this.mGraphRectX;
    }

    /**
     *
     */
    public float getFigureX(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getFigureX(), unit);
    }

    /**
     *
     */
    public float getFigureY() {
        return this.mGraphRectY;
    }

    /**
     *
     */
    public float getFigureY(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getFigureY(), unit);
    }

    /**
     *
     */
    public float getFigureWidth() {
        return this.mGraphRectWidth;
    }

    /**
     *
     */
    public float getFigureWidth(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getFigureWidth(),
                unit);
    }

    /**
     *
     */
    public float getFigureHeight() {
        return this.mGraphRectHeight;
    }

    /**
     *
     */
    public float getFigureHeight(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getFigureHeight(),
                unit);
    }


    /**
     * Set the x coordinate of the figure in a given unit.
     * @param value
     *       x coordinate in a given unit in the client area
     * @param unit
     *       the unit of length
     */
    public boolean setFigureX(final float value, final String unit) {

        final Float xPt = calcFigureScale(value, unit, FIGURE_LOCATION_UNIT,
                FIGURE_X_MIN, FIGURE_X_MAX);
        if (xPt == null) {
            return false;
        }
        if (this.equalLength(this.mGraphRectX, xPt)) {
            return true;
        }
        this.mGraphRectX = xPt;

        // update the graph rectangle
        this.updateGraphRect();

        return true;
    }

    /**
     * Set the y coordinate of the figure in a given unit.
     * @param value
     *       y coordinate in a given unit in the client area
     * @param unit
     *       the unit of length
     */
    public boolean setFigureY(final float value, final String unit) {

        final Float yPt = calcFigureScale(value, unit, FIGURE_LOCATION_UNIT,
                FIGURE_Y_MIN, FIGURE_Y_MAX);
        if (yPt == null) {
            return false;
        }
        if (this.equalLength(this.mGraphRectY, yPt)) {
            return true;
        }
        this.mGraphRectY = yPt;

        // update the graph rectangle
        this.updateGraphRect();

        return true;
    }

    /**
     * Set width of the figure in a given unit.
     * @param value
     *       width in a given unit
     * @param unit
     *       the unit of length
     */
    public boolean setFigureWidth(final float value, final String unit) {

        final Float wPt = calcFigureScale(value, unit, FIGURE_SIZE_UNIT,
                FIGURE_WIDTH_MIN, FIGURE_WIDTH_MAX);
        if (wPt == null) {
            return false;
        }
        if (this.equalLength(this.mGraphRectWidth, wPt)) {
            return true;
        }
        this.mGraphRectWidth = wPt;

        // update the graph rectangle
        this.updateGraphRect();

        return true;
    }

    /**
     * Set height of the figure in a given unit.
     * @param value
     *       height in a given unit
     * @param unit
     *       the unit of length
     */
    public boolean setFigureHeight(final float value, final String unit) {

        final Float hPt = calcFigureScale(value, unit, FIGURE_SIZE_UNIT,
                FIGURE_HEIGHT_MIN, FIGURE_HEIGHT_MAX);
        if (hPt == null) {
            return false;
        }
        if (this.equalLength(this.mGraphRectHeight, hPt)) {
            return true;
        }
        this.mGraphRectHeight = hPt;

        // update the graph rectangle
        this.updateGraphRect();

        return true;
    }
    
    public boolean setFigureSize(final float width, final float height, final String unit) {
        final Float wPt = calcFigureScale(width, unit, FIGURE_SIZE_UNIT,
                FIGURE_WIDTH_MIN, FIGURE_WIDTH_MAX);
        if (wPt == null) {
            return false;
        }
        final Float hPt = calcFigureScale(height, unit, FIGURE_SIZE_UNIT,
                FIGURE_HEIGHT_MIN, FIGURE_HEIGHT_MAX);
        if (hPt == null) {
            return false;
        }
        
        if (this.equalLength(this.mGraphRectWidth, wPt) 
        		&& this.equalLength(this.mGraphRectHeight, hPt)) {
            return true;
        }
        
        this.mGraphRectWidth = wPt;
        this.mGraphRectHeight = hPt;

        // update the graph rectangle
        this.updateGraphRect();

    	return true;
    }

    // Calculates the location or length of a figure.
    public static Float calcFigureScale(final float value, final String unit, final String convUnit,
            final double min, final double max) {
        return SGUtility.calcPropertyValue(value, unit, convUnit, min, max, LENGTH_MINIMAL_ORDER);
    }

    // check whether two values in units of pt are equal in FIGURE_SIZE_UNIT
    private boolean equalLength(final float v1, final float v2) {
        final float diff = Math.abs(v1 - v2);
        final double conv = SGUtilityText.convert(diff, SGIConstants.pt, FIGURE_SIZE_UNIT);
        final double ten = SGUtilityNumber.getPowersOfTen(
        	SGIRootObjectConstants.LENGTH_MINIMAL_ORDER - 1);
        return (conv < ten);
    }

    /**
     * Set the location of figure.
     * @param x
     *     x coordinate
     * @param y
     *     y coordinate
     */
    public boolean setGraphRectLocation(final float x, final float y) {
        this.setGraphRectLocationAttributes(x, y);
        this.updateGraphRect();
        return true;
    }

    // Set the location of graph rectangle.
    // Arguments x and y are given in units of pt.
    private boolean setGraphRectLocationAttributes(final float x, final float y) {
	final Rectangle2D cRect = this.mWnd.getPaperRect();
	this.mGraphRectX = (x - (float) cRect.getX()) / this.mMagnification;
	this.mGraphRectY = (y - (float) cRect.getY()) / this.mMagnification;
        return true;
    }

    /**
     *
     * @param widthPt
     * @param heightPt
     * @return
     */
    protected boolean setGraphRectLocationRoundingOut(final float xPt,
            final float yPt) {
        final Rectangle2D cRect = this.mWnd.getPaperRect();

        final float xCM = (xPt - (float) cRect.getX())
                * SGIConstants.CM_POINT_RATIO / this.mMagnification;
        final float yCM = (yPt - (float) cRect.getY())
                * SGIConstants.CM_POINT_RATIO / this.mMagnification;

        // round out the size
        final float dXCM = (float) SGUtilityNumber.roundOutNumber(xCM, -2);
        final float dYCM = (float) SGUtilityNumber.roundOutNumber(yCM, -2);

        // length in units of pixel
        final float x = dXCM / SGIConstants.CM_POINT_RATIO;
        final float y = dYCM / SGIConstants.CM_POINT_RATIO;

        //
        this.mGraphRectX = x;
        this.mGraphRectY = y;

        this.updateGraphRect();

        return true;
    }

    /**
     *
     */
    public boolean setGraphRectSize(final float w, final float h) {
        this.setGraphRectSizeAttributes(w, h);
        this.updateGraphRect();
        return true;
    }

    /**
     *
     */
    private boolean setGraphRectSizeAttributes(final float w, final float h) {
        this.mGraphRectWidth = w / this.mMagnification;
        this.mGraphRectHeight = h / this.mMagnification;
        return true;
    }

    /**
     *
     * @param widthPt
     * @param heightPt
     * @return
     */
    protected boolean setGraphRectSizeRoundingOut(final float widthPt,
            final float heightPt) {

        final float widthCM = widthPt * SGIConstants.CM_POINT_RATIO
                / this.mMagnification;
        final float heightCM = heightPt * SGIConstants.CM_POINT_RATIO
                / this.mMagnification;

        // round out the size
        final float dWidthCM = (float) SGUtilityNumber.roundOutNumber(widthCM,
                -2);
        final float dHeightCM = (float) SGUtilityNumber.roundOutNumber(
                heightCM, -2);

        // length in units of pixel
        final float width = dWidthCM / SGIConstants.CM_POINT_RATIO;
        final float height = dHeightCM / SGIConstants.CM_POINT_RATIO;

        //
        this.mGraphRectWidth = width;
        this.mGraphRectHeight = height;

        return true;
    }

    /**
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    public boolean setGraphRect(final float x, final float y, final float w,
            final float h) {
        this.setGraphRectLocationAttributes(x, y);
        this.setGraphRectSizeAttributes(w, h);
        this.updateGraphRect();
        return true;
    }

    /**
     *
     */
    public boolean isLegendVisible() {
        return this.getLegendElement().isVisible();
    }

    /**
     * Returns whether the color bar is visible.
     *
     * @return true if visible
     */
    public boolean isColorBarVisible() {
        return this.getAxisElement().isColorBarVisible();
    }

    /**
     * Sets whether the color bar is visible.
     *
     * @param true to set visible
     *
     * @return true if succeeded
     */
    public boolean setColorBarVisible(final boolean b) {
        final SGIFigureElementAxis aElement = this.getAxisElement();
        aElement.setColorBarVisible(b);
        return true;
    }

    public boolean setAxisScaleVisible(final boolean b) {
        final SGIFigureElementAxis aElement = this.getAxisElement();
        aElement.setAxisScaleVisible(b);
        return true;
    }

    /**
     * Add a new data with given ID.
     *
     * @param dataArray
     *            the array of new data objects added
     * @param idArray
     *            the array of data ID
     * @param dataNameArray
     *            the array of data name
     * @param progress
     *            controller of the progress
     * @param min
     *            minimum progress value
     * @param max
     *            maximum progress value
     * @param infoMap
     *            information map of data
     * @return
     *            true if succeeded
     */
    public boolean addData(final SGData[] dataArray, final Integer[] idArray,
            final String[] nameArray, final SGIProgressControl progress,
            final float min, final float max, final Map<String, Object> infoMap) {

        // change the order
        SGIFigureElement[] array = this.getIFigureElementArray();
        List<SGIFigureElement> list = new ArrayList<SGIFigureElement>(Arrays.asList(array));
        SGIFigureElementAxis aElement = this.getAxisElement();
        list.remove(aElement);
        list.add(0, aElement);

        // add a data to the list
        for (int ii = 0; ii < dataArray.length; ii++) {
            this.mDataList.add(dataArray[ii]);
        }

        // creates an array of data name
        String dataNameArrayNew[] = new String[dataArray.length];
        for (int ii = 0; ii < nameArray.length; ii++) {
            // get a new name to avoid duplication of the name
            dataNameArrayNew[ii] = this.getNewDataName(nameArray[ii]);
        }

        int[] ids = null;
        if (idArray != null) {
            ids = new int[idArray.length];
            for (int ii = 0; ii < ids.length; ii++) {
                ids[ii] = idArray[ii].intValue();
            }
        }

        // add to the figure elements
        SGIFigureElement[] newArray = (SGIFigureElement[]) list.toArray(new SGIFigureElement[] {});
        final float ratio = (max - min) / newArray.length;
        for (int ii = 0; ii < newArray.length; ii++) {
            progress.setProgressValue(ratio * ii + min);
            if (idArray != null) {
                if (newArray[ii].addData(dataArray, dataNameArrayNew, ids, infoMap) == false) {
                    return false;
                }
            } else {
                if (newArray[ii].addData(dataArray, dataNameArrayNew, infoMap) == false) {
                    return false;
                }
            }
        }
        
        // sets anchored flag
        this.setDataAnchored(this.isDataAnchored());

        return true;
    }

    /**
     * Add a new data.
     *
     * @param data
     *             the new data added
     * @param name
     *             the name of data
     * @param map
     *             a map of properties
     * @return
     *             true if succeeded
     */
    public boolean addData(
            final SGData data, final String name,
            final Map<Class<? extends SGIFigureElement>, SGProperties> map) {

    	// add to the data list
        this.mDataList.add(data);

        // get a new name to avoid duplication of the name
        String nameNew = this.getNewDataName(name);

        // add a data object to figure elements
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            Object value = map.get(array[ii].getClass());
            SGProperties p = (SGProperties) value;
            if (array[ii].addData(data, nameNew, p) == false) {
                return false;
            }
        }

        // sets anchored flag
        this.setDataAnchored(this.isDataAnchored());

        return true;
    }

    /**
     * Returns a text string for the name of new data.
     * @param name
     *              the name
     * @return
     *              the name for new data
     */
    public String getNewDataName(final String name) {

        // get a list of names of data objects which already exist in this figure
        List<SGData> list = this.getVisibleDataList();
        List<String> dNameList = new ArrayList<String>();
        for (int ii = 0; ii < list.size(); ii++) {
            SGData data = (SGData) list.get(ii);
            String dName = this.getDataName(data);
            dNameList.add(dName);
        }

        // get the serialized name
        String nameNew = SGUtilityText.getSerialName(dNameList, name);

        return nameNew;
    }

    /**
     * Set the bounds of viewport.
     */
    protected boolean setViewBounds() {
        final Rectangle2D rectView = this.mWnd.getViewportBounds();
        this.setViewBounds(rectView);
        return true;
    }

    /**
     * Set the bounds of viewport.
     */
    protected boolean setViewBounds(final Rectangle2D rectView) {
        // // set to the attribute
        // this.mViewBounds = rectView;

        // set to SGIFigureElement objects
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].setViewBounds(rectView);
        }

        return true;
    }

    /**
     * Resize this figure.
     * @param ratioX
     *            ratio of the scale for x-direction
     * @param ratioY
     *            ratio of the scale for y-direction
     */
    public boolean resize(final float ratioX, final float ratioY) {

        this.mGraphRectX *= ratioX;
        this.mGraphRectY *= ratioY;
        this.mGraphRectWidth *= ratioX;
        this.mGraphRectHeight *= ratioY;

        this.updateGraphRect();

        this.setViewBounds();

        return true;
    }

    /**
     *
     * @return
     */
    public float getMagnification() {
        return this.mMagnification;
    }

    /**
     * Zoom in/out this component.
     *
     * @return true:secceeded, false:failed
     */
    public boolean setMagnification(final float mag) {

        this.mMagnification = mag;

        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].setMagnification(mag) == false) {
                return false;
            }
        }
        this.updateGraphRect();

        // if the dragging rectanlgle is visible,
        // set it to be equal to the graph area rectangle
        // if( SGFigure.mRubberBandFlag )
        // {
        // this.setRubberBandRect();
        // }

        return true;
    }

    /**
     * The flag whether to show anchors around focused figure.
     */
    private boolean mSelectionSymbolsVisibleFlag = true;

    /**
     * Sets the visibility of symbols around selected objects.
     *
     * @param b
     *          true to be visible
     */
    protected void setSelectionSymbolsVisible(final boolean b) {
        this.mSelectionSymbolsVisibleFlag = b;
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].setSymbolsVisibleAroundFocusedObjects(b);
        }
    }

    /**
     * Returns whether to show anchors around focused figure.
     *
     */
    protected boolean isSelectionSymbolsVisible() {
        return this.mSelectionSymbolsVisibleFlag;
    }

    /**
     *
     * @param e
     * @return
     */
    protected boolean onMouseMoved(final MouseEvent e) {
        final int x = e.getX();
        final int y = e.getY();

        if (this.mWnd.isInsertFlagSelected() == false) {
            // when the toggle button is not selected
        	
            SGIFigureElement[] array = this.getIFigureElementArray();
            for (int ii = array.length - 1; ii >= 0; ii--) {
            	array[ii].onMouseMoved(e);
            }
            
            // change the mouse cursor
            Cursor cur = this.setMouseCursor(x, y);

            if (Cursor.getDefaultCursor().equals(cur) == false) {
                return true;
            }

        } else if (this.mWnd.getTimingLineInsertionFlag()) {
            // timing line is to be inserted
            SGIFigureElementTimingLine el = (SGIFigureElementTimingLine) this
                    .getIFigureElement(SGIFigureElementTimingLine.class);
            if (el != null) {
                el.guideToAdd(x, y);
            }
        }

        if (this.mMouseInExtraRegionFlag) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks whether the given point is in the extra region.
     *
     * @param x
     *           the x coordinate
     * @param y
     *           the y coordinate
     * @return true if a given point is in the extra region
     */
    boolean checkExtraRegion(final int x, final int y) {
        Rectangle2D eBounds = this.getExtraRegionBounds();
        this.mMouseInExtraRegionFlag = eBounds.contains(x, y);
        return this.mMouseInExtraRegionFlag;
    }

    /**
     * Sets the flag that determines whether to display the extra region.
     *
     * @param b
     *          true to display the extra region
     */
    void setExtraRegionFlag(final boolean b) {
        this.mMouseInExtraRegionFlag = b;
    }

    /**
     * Inserts a label at a given point with default axes.
     *
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addString(final int x, final int y) {
        SGIFigureElementString el = (SGIFigureElementString) this
                .getIFigureElement(SGIFigureElementString.class);
        if (el != null) {
            return el.addString(x, y);
        }
        return false;
    }

    /**
     * Insert a label at a given point with given axis values with default axes.
     *
     * @param id
     *          the ID to set
     * @param str
     *          a text string to insert
     * @param x
     *          axis value for default x-axis
     * @param y
     *          axis value for default y-axis
     * @return true if succeeded
     */
    public boolean addString(final int id, final String str, final double x,
            final double y) {
        SGIFigureElementString el = (SGIFigureElementString) this
                .getIFigureElement(SGIFigureElementString.class);
        if (el != null) {
            return el.addString(id, str, x, y);
        }
        return false;
    }

    /**
     * Insert a label with given axis values with given axes.
     *
     * @param id
     *          the ID to set
     * @param str
     *          a text string to insert
     * @param x
     *          axis value for given x-axis
     * @param y
     *          axis value for given y-axis
     * @param xAxisLocation
     *          location of the x-axis
     * @param yAxisLocation
     *          location of the y-axis
     * @return true if succeeded
     */
    public boolean addString(final int id, final String str, final double x,
            final double y, final String xAxisLocation, final String yAxisLocation) {
        SGIFigureElementString el = (SGIFigureElementString) this
                .getIFigureElement(SGIFigureElementString.class);
        if (el != null) {
            return el.addString(id, str, x, y, xAxisLocation, yAxisLocation);
        }
        return false;
    }

    /**
     * Inserts a timing line at a given point with default axis.
     *
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addTimingLine(final int x, final int y) {
        SGIFigureElementTimingLine el = (SGIFigureElementTimingLine) this
                .getIFigureElement(SGIFigureElementTimingLine.class);
        if (el != null) {
            return el.addTimingLine(x, y);
        }
        return false;
    }

//    /**
//     * Inserts a timing line with given axis value to given axis.
//     *
//     * @param id
//     *          the ID to set
//     * @param value
//     *           the axis value for given axis
//     * @param axisLocation
//     *          location of the axis
//     * @return true if succeeded
//     */
//    public boolean addTimingLine(final int id, final double value,
//            final String axisLocation) {
//        SGIFigureElementTimingLine el = (SGIFigureElementTimingLine) this
//                .getIFigureElement(SGIFigureElementTimingLine.class);
//        if (el != null) {
//            return el.addTimingLine(id, value, axisLocation);
//        }
//        return false;
//    }

    /**
     * Inserts an axis break symbol at a given point with default axes.
     *
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addAxisBreakSymbol(final int x, final int y) {
        SGIFigureElementAxisBreak el = (SGIFigureElementAxisBreak) this
                .getIFigureElement(SGIFigureElementAxisBreak.class);
        if (el != null) {
            return el.addAxisBreakSymbol(x, y);
        }
        return false;
    }

    /**
     * Inserts a axis break symbol with given axis value for default axes.
     *
     * @param id
     *          the ID to set
     * @param x
     *          axis value for default x-axis
     * @param y
     *          axis value for default y-axis
     * @return true if succeeded
     */
    public boolean addAxisBreakSymbol(final int id, final double x, final double y) {
        SGIFigureElementAxisBreak el = (SGIFigureElementAxisBreak) this
                .getIFigureElement(SGIFigureElementAxisBreak.class);
        if (el != null) {
            return el.addAxisBreakSymbol(id, x, y);
        }
        return false;
    }

    /**
     * Inserts a axis break symbol with given axis value for given axes.
     *
     * @param id
     *          the ID to set
     * @param x
     *          axis value for given x-axis
     * @param y
     *          axis value for given y-axis
     * @param xAxisLocation
     *          location of the x-axis
     * @param yAxisLocation
     *          location of the y-axis
     * @return true if succeeded
     */
    public boolean addAxisBreakSymbol(final int id, final double x,
            final double y, final String xAxisLocation, final String yAxisLocation) {
        SGIFigureElementAxisBreak el = (SGIFigureElementAxisBreak) this
                .getIFigureElement(SGIFigureElementAxisBreak.class);
        if (el != null) {
            return el.addAxisBreakSymbol(id, x, y, xAxisLocation, yAxisLocation);
        }
        return false;
    }

    /**
     * Insert a significant difference symbol at a given point with default axes.
     *
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addSignificantDifferenceSymbol(final int x, final int y) {
        SGIFigureElementSignificantDifference el = (SGIFigureElementSignificantDifference) this
                .getIFigureElement(SGIFigureElementSignificantDifference.class);
        if (el != null) {
            return el.addSignificantDifferenceSymbol(x, y);
        }
        return false;
    }

    /**
     * Inserts a significant difference symbol with given axis values for default axes.
     *
     * @param id
     *          the ID to set
     * @param x
     *          axis value for default x-axis
     * @param y
     *          axis value for default y-axis
     * @return true if succeeded
     */
    public boolean addSignificantDifferenceSymbol(final int id, final double x,
            final double y) {
        SGIFigureElementSignificantDifference el = (SGIFigureElementSignificantDifference) this
                .getIFigureElement(SGIFigureElementSignificantDifference.class);
        if (el != null) {
            return el.addSignificantDifferenceSymbol(id, x, y);
        }
        return false;
    }

    /**
     * Inserts a significant difference symbol with given axis values for given axes.
     *
     * @param id
     *          the ID to set
     * @param x
     *          axis value for given x-axis
     * @param y
     *          axis value for given y-axis
     * @param xAxisLocation
     *          location of the x-axis
     * @param yAxisLocation
     *          location of the y-axis
     * @return true if succeeded
     */
    public boolean addSignificantDifferenceSymbol(final int id, final double x,
            final double y, final String xAxisLocation, final String yAxisLocation) {
        SGIFigureElementSignificantDifference el = (SGIFigureElementSignificantDifference) this
                .getIFigureElement(SGIFigureElementSignificantDifference.class);
        if (el != null) {
            return el.addSignificantDifferenceSymbol(id, x, y, xAxisLocation,
                    yAxisLocation);
        }
        return false;
    }

    /**
     * Inserts a shape object at a given point with default axes.
     *
     * @param type
     *          the type of shape
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addShape(final int type, final int x, final int y) {
        SGIFigureElementShape el = (SGIFigureElementShape) this
                .getIFigureElement(SGIFigureElementShape.class);
        if (el != null) {
            return el.addShape(type, x, y);
        }
        return false;
    }

    /**
     * Inserts a shape object at a given point with default axes.
     *
     * @param id
     *          the ID to set
     * @param type
     *          the type of shape
     * @param x
     *          axis value for default x-axis
     * @param y
     *          axis value for default y-axis
     * @return true if succeeded
     */
    public boolean addShape(final int id, final int type, final double x,
            final double y) {
        SGIFigureElementShape el = (SGIFigureElementShape) this
                .getIFigureElement(SGIFigureElementShape.class);
        if (el != null) {
            return el.addShape(id, type, x, y);
        }
        return false;
    }

    /**
     * Inserts a shape object with given axis values for given axes.
     *
     * @param id
     *          the ID to set
     * @param type
     *          the type of shape
     * @param x
     *          axis value for given x-axis
     * @param y
     *          axis value for given y-axis
     * @param xAxisLocation
     *          location of the x-axis
     * @param yAxisLocation
     *          location of the y-axis
     * @return true if succeeded
     */
    public boolean addShape(final int id, final int type, final double x,
            final double y, final String xAxisLocation, final String yAxisLocation) {
        SGIFigureElementShape el = (SGIFigureElementShape) this
                .getIFigureElement(SGIFigureElementShape.class);
        if (el != null) {
            return el.addShape(id, type, x, y, xAxisLocation, yAxisLocation);
        }
        return false;
    }

    protected boolean onFigureElementsKeyPressed(final KeyEvent e) {
        boolean effective = false;
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = array.length - 1; ii >= 0; ii--) {
            if (array[ii].onKeyPressed(e)) {
                effective = true;
            }
        }
        this.repaint();
        return effective;
    }

    /**
     * Called when the key pressed.
     *
     * @param e
     *            key event
     * @return whether an effective when key pressed
     */
    protected boolean onKeyPressed(final KeyEvent e) {
        boolean effective = false;

        effective = this.onFigureElementsKeyPressed(e);
        if (!this.isSelected()) {
            return effective;
        }

        final int keycode = e.getKeyCode();
        int dx = 0;
        int dy = 0;
        switch (keycode) {
        case KeyEvent.VK_UP:
            dy = -1;
            break;
        case KeyEvent.VK_DOWN:
            dy = 1;
            break;
        case KeyEvent.VK_LEFT:
            dx = -1;
            break;
        case KeyEvent.VK_RIGHT:
            dx = 1;
            break;
        }
        if (dx != 0 || dy != 0) {
            Rectangle2D rect = this.getGraphRect();
            float interval = this.mMagnification;
            if (SGFigure.isSnappingToGrid())
                interval *= this.mWnd.getGridLineInterval();
            else
                interval *= (float) (SGIRootObjectConstants.GRID_INTERVAL_STEP_SIZE / SGIConstants.CM_POINT_RATIO);
            // horizontal
            if (dx != 0)
                rect.setRect(rect.getX() + interval * dx, rect.getY(), rect
                        .getWidth(), rect.getHeight());
            // vertical
            if (dy != 0)
                rect.setRect(rect.getX(), rect.getY() + interval * dy, rect
                        .getWidth(), rect.getHeight());
            
            // moves figure
            this.setDraggingRect(rect);
            this.snapToLines(OTHER);
            this.setGraphRectOnDragging();
            if (this.isFigureMoved()) {
            	this.setChanged(true);
            }

            effective = true;
        }

        return effective;
    }

    /**
     * Called when the mouse is clicked.
     *
     * @param e
     *            mouse event
     * @return whether an effective point is clicked
     */
    protected boolean onMouseClicked(MouseEvent e) {

        // count
        final int count = e.getClickCount();

        // ask to SGIFigureElement objects
        SGIFigureElement el = this.onFigureElementClicked(e);
        if (el != null) {
            //
            this.mWnd.setFocusedFigure(this, false);

            // after treatment
            this.afterClicked(e);

            return true;
        }

        if (this.getExtraRegionBounds().contains(e.getPoint())) {
            // when a point in the extra region of the figure is clicked

            // update the list of focused figures
            this.updateFocusedFigureList(e);

            // after treatment
            this.afterClicked(e);

            // show the pop-up menu
            if (SwingUtilities.isRightMouseButton(e) && count == 1) {
            	this.mWnd.showPopupMenuForSelectedFigures(this.getComponent(), e.getX(), e.getY());
            }

            // show property dialog
            if (SwingUtilities.isLeftMouseButton(e) && count == 2) {
                this.mWnd.showPropertyDialogForSelectedFigures();
            }

            return true;

        }

        return false;

    }

    /**
     * Flag whether this object is focused.
     */
    private boolean mSelectedFlag = false;

    /**
     * Get the flag as a focused object.
     *
     * @return whether this object is focused.
     */
    public boolean isSelected() {
        return this.mSelectedFlag;
    }

    /**
     * Sets selected or deselected the object.
     * 
     * @param b
     *            true selects and false deselects the object
     */
    @Override
    public void setSelected(final boolean b) {
        this.mSelectedFlag = b;
    	this.mTemporaryProperties = b ? this.getProperties() : null;
    }

    /**
     *
     *
     */
    public boolean hideSelectedObjects() {
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].hideSelectedObjects() == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Hide data of given ids.
     *
     * @param dataIdArray
     * @return
     */
    public boolean hideData(final int[] dataIdArray) {
        boolean flagHide = false;

        this.getGraphElement().clearFocusedObjects();
        for (int ii = 0; ii < dataIdArray.length; ii++) {
            SGData data = this.getData(dataIdArray[ii]);
            if (data == null) {
                continue;
            }
            this.getGraphElement().setDataFocused(data, true);
        }
        SGIFigureElementString fes = (SGIFigureElementString) this.getIFigureElement(SGIFigureElementString.class);
        this.getGraphElement().hideNetCDFLabelsOfFocusedObjects(fes);

        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].hideData(dataIdArray)) {
                flagHide = true;
            }
        }
        return flagHide;
    }

    /**
     * Sets the visibility of symbols around all objects.
     *
     * @param b
     *          true to be visible
     */
    protected void setSymbolsAroundAllObjectsVisible(boolean b) {
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].setSymbolsAroundAllObjectsVisible(b);
        }
    }

//    /**
//     *
//     * @return
//     */
//    protected boolean updatePopupMenu(JPopupMenu p) {
//        Component[] array = p.getComponents();
//        for (int ii = 0; ii < array.length; ii++) {
//            if (array[ii] instanceof JCheckBoxMenuItem) {
//                JCheckBoxMenuItem item = (JCheckBoxMenuItem) array[ii];
//                String com = item.getActionCommand();
//                if (com.equals(MENUCMD_RUBBER_BANDING)) {
//                    item.setSelected(SGFigure.mRubberBandFlag);
//                } else if (com.equals(MENUCMD_SHOW_BOUNDING_BOX)) {
//                    item.setSelected(SGFigure.mBoundingBoxVisibleFlag);
//                } else if (com.equals(MENUBARCMD_SNAP_TO_GRID)) {
//                    item.setSelected(SGFigure.isSnappingToGrid());
//                }
//            }
//        }
//
//        return true;
//    }

    /**
     *
     */
    private boolean afterClicked(MouseEvent e) {
        this.repaint();
        return true;
    }

    /**
     *
     */
    private SGIFigureElement onFigureElementClicked(MouseEvent e) {
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = array.length - 1; ii >= 0; ii--) {
            if (array[ii].onMouseClicked(e)) {
                return array[ii];
            }
        }

        return null;
    }

    /**
     * Called when the mouse is pressed.
     *
     * @param e
     *            mouse event
     * @return whether an effective point is pressed
     */
    protected boolean onMousePressed(final MouseEvent e) {

        // if focused
        if (this.isSelected()) {
            // for anchors
            if (this.pressWithMouseLocation(e)) {
                return true;
            }
        }

        // press figure element
        if (this.pressFigureElement(e)) {
            return true;
        }

        // check whether the button is pressed inside the extra region
        if (this.getExtraRegionBounds().contains(e.getPoint())) {
            return this.pressed(e);
        }

        return false;
    }

    // ask to SGIFigureElement objects
    private boolean pressFigureElement(MouseEvent e) {
        SGIFigureElement el = this.onFigureElementPressed(e);
        if (el != null) {
            // aftertreatment
            this.afterPressed(e);
            return true;
        }

        return false;
    }

    // check whether the button is pressed on an effective point
    private boolean pressWithMouseLocation(MouseEvent e) {
        if (this.mMouseLocation != OTHER) {
            return this.pressed(e);
        }
        return false;
    }

    //
    private boolean pressed(MouseEvent e) {
        // clear all selected objects in this figure
        this.clearFocusedObjects();

        // aftertreatment
        this.afterPressed(e);

        return true;
    }

    /**
     *
     */
    private boolean afterPressed(MouseEvent e) {

        // set visible the rubber band
        if (SwingUtilities.isLeftMouseButton(e)) {
            SGFigure.mRubberBandVisibleFlag = true;

            // Change the cursor.
            if (this.mPressedElement == null) {
                if (this.mWnd.getCursor().equals(Cursor.getDefaultCursor())) {
                    this.changeCursor();
                    if (this.mMouseLocation == OTHER) {
                        this.mWnd.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                    }
                }
            }
        }

        // set the location of pressed point
        this.mPressedPoint.setLocation(e.getPoint());

        // record the rectangle of pressed figure when the mouse is pressed
        this.recordFigureRect();

        return true;
    }

    /**
     * Update the list of focused figures when the mouse is pressed.
     *
     * @return
     */
    protected boolean updateFocusedFigureList(final MouseEvent e) {
        final SGDrawingWindow wnd = this.getWindow();
        final List<SGFigure> fList = wnd.getFocusedFigureList();

        // Neither CTRL key nor SHIFT key is pressed.
        final int mod = e.getModifiers();
        if (((mod & InputEvent.CTRL_MASK) == 0)
                && ((mod & InputEvent.SHIFT_MASK) == 0)) {
            // If the list already contains this object.
            if (fList.contains(this)) {
                // There is nothing to do.
            } else {
                wnd.clearAllFocusedObjectsInFigures();
                wnd.setFocusedFigure(this, true);
            }

        }
        // otherwise
        else {
            // If the list already contains this object.
            if (fList.contains(this)) {
                final int loc = this.mMouseLocation;

                // except the four corners
                if (loc != NORTH_WEST && loc != NORTH_EAST && loc != SOUTH_EAST
                        && loc != SOUTH_WEST) {
                    // if no figure element is pressed, remove this figure
                    // from the list of the selected figure
                    if (this.mPressedElement == null) {
                        wnd.setFocusedFigure(this, !this.isSelected());
                    }
                }
            } else {
                wnd.setFocusedFigure(this, !this.isSelected());
            }
        }

        return true;
    }

    /**
     * Record the rectangle of this figure.
     * @return
     *       true if succeeded
     */
    boolean recordFigureRect() {
        this.mTempFigureRect.setRect(this.getGraphRectInClientRect());
        return true;
    }

    /**
     * Called when the figure element is pressed.
     */
    private SGIFigureElement onFigureElementPressed(MouseEvent e) {
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = array.length - 1; ii >= 0; ii--) {
//            // set temporary properties
//            if (array[ii].setTemporaryPropertiesOfFocusedObjects() == false) {
//                return null;
//            }
            if (array[ii].onMousePressed(e)) {
                this.mPressedElement = array[ii];
                return array[ii];
            }
        }

        return null;
    }

    /**
     * Called when the mouse is dragged.
     * @param e
     *       the mouse event
     */
    protected boolean onMouseDragged(final MouseEvent e) {

        // notify to the pressed SGIFigureElement object
        if (this.mPressedElement != null) {
        	List<SGISelectable> list = this.mPressedElement.getFocusedObjectsList();

            if (this.mPressedElement.onMouseDragged(e) == false) {
                return false;
            }

            if (list.size() != 0) {
                this.mWnd.moveFocusedObjects(e);
            } else {
            	this.mWnd.clearAllFocusedObjectsInFigures();
            }

            this.setCursorToWindow(this.mPressedElement);
            return true;
        }

        // if this figure is not selected, return false
        if (!this.isSelected()) {
            return false;
        }

        // mouse location
        final int ml = this.mMouseLocation;

        // other points
        if (ml == OTHER) {
    		Rectangle2D rect = this.getExtraRegionBounds();
    		if (this.mWnd.mMousePressLocation != null
    				&& !rect.contains(this.mWnd.mMousePressLocation)) {
    			return false;
    		}

            // parallel displacement
            this.mWnd.moveFocusedObjects(e);
            return true;
        }
        // record the temporary bounds
        this.recordFigureRect();

        // create a temporary object
        Point posNew = new Point(this.mPressedPoint);
        Rectangle2D rectNew = this.getDraggingRect();

        // update the rectangle
        SGUtility.resizeRectangle(rectNew, posNew, e, ml);

        // when the size of rectangle becomes too small, return true
        if (rectNew.getWidth() < MIN_WIDTH || rectNew.getHeight() < MAX_WIDTH) {
            return true;
        }

        // set to an attribute
        this.mPressedPoint.setLocation(posNew);

        // update the graph rectangle
        this.setDraggingRect(rectNew);
        this.snapToLines(ml);

        // if we do not draw the rubber band, change the rectangle of figure now
        if (SGFigure.mRubberBandFlag == false) {
            this.setGraphRectOnDragging();
        }

        return false;
    }

    /**
     *
     * @param dx
     * @param dy
     * @return
     */
    public void translate(final float dx, final float dy) {
        Rectangle2D dRect = this.getDraggingRect();
        this.setDraggingRect((float) dRect.getX() + dx, (float) dRect.getY()
                + dy, (float) dRect.getWidth(), (float) dRect.getHeight());

        this.snapToLines(OTHER);

        // if the rectangle for dragging is invisible,
        // set the graph area rectangle immediately
        if (SGFigure.mRubberBandFlag == false) {
            this.mPressedPoint.setLocation(this.mPressedPoint.x + dx,
                    this.mPressedPoint.y + dy);
            this.setGraphRectOnDragging();
        }

    }

    /**
     *
     * @param dx
     * @param dy
     */
    public void translateSelectedObjects(final int dx, final int dy) {
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].translateFocusedObjects(dx, dy);
        }
    }

    //
    Rectangle2D getRubberBandRect() {
        Rectangle2D rect = new Rectangle2D.Float();
        rect.setRect(this.mRubberBandRect);
        return rect;
    }

    //
    Rectangle2D getDraggingRect() {
        Rectangle2D rect = new Rectangle2D.Float();
        rect.setRect(this.mDraggingRect);
        return rect;
    }

    // set the rubber band
    void setRubberBandRect(final float x, final float y, final float w,
            final float h) {
        this.mRubberBandRect.setRect(x, y, w, h);
    }

    // set the dragging rectangle
    void setDraggingRect(final float x, final float y, final float w,
            final float h) {
        this.mDraggingRect.setRect(x, y, w, h);
    }

    // set the dragging rectangle
    void setDraggingRect(final Rectangle2D rect) {
        this.mDraggingRect.setRect(rect);
    }

    /**
     * Set the dragged rectangle to the graph rectangle.
     */
    protected boolean setGraphRectOnDragging() {
        Rectangle2D rbRect = this.getRubberBandRect();

        final float x = (float) rbRect.getX();
        final float y = (float) rbRect.getY();
        final float w = (float) rbRect.getWidth();
        final float h = (float) rbRect.getHeight();

        // returns if not changed
        Rectangle rectOld = this.getGraphRect().getBounds();
        if (rectOld.equals(rbRect.getBounds())) {
            return true;
        }

        // set the rectangle
        this.setGraphRectLocationAttributes(x, y);
        this.setGraphRectSizeAttributes(w, h);
        this.updateGraphRect();

        return true;
    }

    // // round off the number
    // private float roundOffInFigureLengthOrder( final float value )
    // {
    // final int order = SGIRootObjectConstants.MINIMAL_LENGTH_ORDER-1;
    // final float ratio = SGIConstants.CM_POINT_RATIO;
    // final float vCM = value*ratio;
    // final float r = (float)SGUtilityNumber.roundOffNumber( vCM, order );
    // final float v = r/ratio;
    // return v;
    // }

    // set the rubber band rectangle snapped to the lines
    void snapToLines(final int mouseLocation) {
        // System.out.println("snapToLines");
        if (isSnappingToGrid()) {
            this.snapToGrid(mouseLocation);
        } else {
            this.snapToUnitCell(mouseLocation);
        }
    }

    // set the rubber band rectangle snapped to the grid lines
    // with minimal width
    private void snapToUnitCell(final int mouseLocation) {
        final float eps = (float) SGUtilityNumber
                .getPowersOfTen(LENGTH_MINIMAL_ORDER);
        final float interval = this.mMagnification * eps / CM_POINT_RATIO;
        this.snap(interval, mouseLocation);
    }

    // set the rubber band rectangle snapped to the grid lines on the paper
    private void snapToGrid(final int mouseLocation) {
        final float interval = this.mMagnification
                * this.mWnd.getGridLineInterval();
        this.snap(interval, mouseLocation);
    }

    // set the rubber band rectangle snapped to the grid lines
    // with given interval
    private void snap(final float interval, final int mouseLocation) {
        final float px = this.mWnd.getPaperX();
        final float py = this.mWnd.getPaperY();

        Rectangle2D dRect = this.getDraggingRect();
        final float minX = (float) dRect.getMinX();
        final float maxX = (float) dRect.getMaxX();
        final float minY = (float) dRect.getMinY();
        final float maxY = (float) dRect.getMaxY();

        final float ox = minX;
        final float oy = maxY;

        final float ox2 = ox - px;
        final float oy2 = oy - py;

        final int nx = (int) (ox2 / interval);
        final int ny = (int) (oy2 / interval);

        // System.out.println(nx+" "+ny);

        final float rx = interval * nx;
        final float ry = interval * ny;

        int nxNew = nx;
        int nyNew = ny;
        if (ox2 - rx > interval / 2.0f) {
            nxNew++;
        }
        if (oy2 - ry > interval / 2.0f) {
            nyNew++;
        }

        // System.out.println(nxNew+" "+nyNew);

        // new origin
        final float oxNew = px + nxNew * interval;
        final float oyNew = py + nyNew * interval;

        // new bounds
        float xNew;
        float yNew;
        float wNew;
        float hNew;

        // x
        if (mouseLocation == WEST || mouseLocation == SOUTH_WEST
                || mouseLocation == NORTH_WEST) {
            xNew = oxNew;
            wNew = maxX - xNew;
        } else if (mouseLocation == EAST || mouseLocation == SOUTH_EAST
                || mouseLocation == NORTH_EAST) {
            xNew = minX;

            final int nMax = (int) ((maxX - px) / interval);
            final float rMax = interval * nMax;

            int nNew = nMax;
            if ((maxX - px) - rMax > interval / 2.0f) {
                nNew++;
            }

            final float maxNew = px + nNew * interval;
            wNew = maxNew - minX;
        } else if (mouseLocation == OTHER) {
            xNew = oxNew;
            wNew = (float) dRect.getWidth();
        } else {
            xNew = minX;
            wNew = (float) dRect.getWidth();
        }

        // y
        if (mouseLocation == SOUTH || mouseLocation == SOUTH_WEST
                || mouseLocation == SOUTH_EAST) {
            yNew = minY;
            hNew = oyNew - minY;
        } else if (mouseLocation == NORTH || mouseLocation == NORTH_EAST
                || mouseLocation == NORTH_WEST) {
            final int nMin = (int) ((minY - py) / interval);
            final float rMin = interval * nMin;

            int nNew = nMin;
            if ((minY - py) - rMin > interval / 2.0f) {
                nNew++;
            }

            final float minNew = py + nNew * interval;
            hNew = maxY - minNew;

            yNew = minNew;
        } else if (mouseLocation == OTHER) {
            hNew = (float) dRect.getHeight();
            yNew = oyNew - hNew;
        } else {
            yNew = minY;
            hNew = (float) dRect.getHeight();
        }

        // set new values to the rubber band rectangle
        this.setRubberBandRect(xNew, yNew, wNew, hNew);

        // System.out.println("new:"+this.getRubberBandRect());
        // System.out.println();

    }

    /**
     * Called when the mouse is released.
     * @param e
     *       the mouse event
     * @return
     *       true if succeeded
     */
    protected boolean onMouseReleased(final MouseEvent e) {
        final int x = e.getX();
        final int y = e.getY();
        
        // set the mouse cursor
        if (this.mWnd.isInsertFlagSelected() == false) {
            this.setMouseCursor(x, y);
        }

        // set the rectangle of the graph area
        if (SGFigure.mRubberBandFlag && this.mPressedElement == null) {
            this.setGraphRectOnDragging();
        }

        if (this.isFigureMoved()) {
        	this.setChanged(true);
        }

        // set invisible the rubber band
        if (SwingUtilities.isLeftMouseButton(e)) {
            SGFigure.mRubberBandVisibleFlag = false;
        }

        // call the "released" method of pressed SGIFigureElement object
        if (this.mPressedElement != null) {
            this.mPressedElement.onMouseReleased(e);
        }
        this.mPressedElement = null;
        this.mWnd.mDraggedDirection = null;

        return true;
    }

    /**
     *
     */
    protected boolean isFigureMoved() {
        Rectangle temp = this.mTempFigureRect.getBounds();
        Rectangle present = this.getGraphRectInClientRect().getBounds();
        final boolean flag = !(temp.equals(present));
        return flag;
    }

    /**
     *
     */
    public static final int DRAW_BACK_MARGIN = 2;

    /**
     *
     * @return
     */
    public boolean drawbackFigure() {
        final Rectangle2D cRect = this.mWnd.getClientRect();
        final Rectangle2D bbRect = this.getBoundingBox();
        final Rectangle2D pRect = this.mWnd.getPaperRect();

        final Rectangle2D rect = new Rectangle2D.Float();
        rect.setRect(bbRect);

        final int margin = DRAW_BACK_MARGIN;

        if (bbRect.getX() < cRect.getX()) {
            rect.setRect(margin, rect.getY() + margin, rect.getWidth(), rect
                    .getHeight());
        }

        if (bbRect.getY() < cRect.getY()) {
            rect.setRect(rect.getX() + margin, margin, rect.getWidth(), rect
                    .getHeight());
        }

        if (bbRect.getX() + bbRect.getWidth() > pRect.getX() + pRect.getWidth()) {
            rect.setRect(pRect.getX() + pRect.getWidth() - bbRect.getWidth()
                    - margin, rect.getY() + margin, rect.getWidth(), rect
                    .getHeight());
        }

        if (bbRect.getY() + bbRect.getHeight() > pRect.getY()
                + pRect.getHeight()) {
            rect.setRect(rect.getX() + margin, pRect.getY() + pRect.getHeight()
                    - bbRect.getHeight() - margin, rect.getWidth(), rect
                    .getHeight());
        }

        if (this.setBoundingBox(rect) == false) {
            return false;
        }

        // snap to the lines
        this.snapToLines(OTHER);
        this.setGraphRectOnDragging();

        return true;
    }

    /**
     * Clear all focused objects in SGIFigureElement objects.
     *
     * @return
     */
    public boolean clearFocusedObjects() {
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].clearFocusedObjects() == false) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return
     */
    public boolean setMementoBackward() {
        if (this.mUndoManager.setMementoBackward() == false) {
            return false;
        }

        this.updateGraphRect();

        return true;
    }

    /**
     *
     * @return
     */
    public boolean setMementoForward() {
        if (this.mUndoManager.setMementoForward() == false) {
            return false;
        }

        this.updateGraphRect();

        return true;
    }

    /**
     * Update the graph rect.
     *
     */
    void updateGraphRect() {
        final float x = this.getGraphRectX();
        final float y = this.getGraphRectY();
        final float w = this.getGraphRectWidth();
        final float h = this.getGraphRectHeight();

        // set to the rubber band
        this.setRubberBandRect(x, y, w, h);
        this.setDraggingRect(x, y, w, h);

        // set to SGIFigureElement objects
        SGIFigureElement[] array = this.getIFigureElementArray();
        if (array != null) {
            for (int ii = 0; ii < array.length; ii++) {
                array[ii].setGraphRect(x, y, w, h);
            }
        }

        this.repaint();

    }

    /**
     *
     *
     */
    public void notifyToRoot() {
        this.mWnd.notifyToRoot();
    }

    /**
     *
     * @return
     */
    public boolean isChanged() {
        return this.mUndoManager.isChanged();
    }

    /**
     *
     * @return
     */
    public boolean isChangedRoot() {
        if (this.isChanged()) {
            return true;
        }

        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].isChangedRoot()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    public void setChanged(final boolean b) {
        this.mUndoManager.setChanged(b);
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    private Cursor setMouseCursor(final int x, final int y) {
        this.setMouseLocation(x, y);
        if (this.mMouseLocation == OTHER) {
            if (this.setMouseCursorSub(x, y) == false) {
                return this.setMouseCursor();
            }
        } else {
            return this.setMouseCursor();
        }

        return null;
    }

    private Cursor setMouseCursor() {
        if (this.isSelected() == false) {
            Cursor cur = Cursor.getDefaultCursor();
            this.setMouseCursor(cur);
            return cur;
        }
        return this.changeCursor();
    }

    private void setMouseLocation(final int x, final int y) {
        final float radius = 1.50f * SGClientPanel.getAnchorSize();
        // final Rectangle2D rect = this.getGraphRect();
        final Rectangle2D rect = this.getRubberBandRect();
        this.mMouseLocation = SGUtility.getMouseLocation(rect, x, y, radius);
    }

    /**
     *
     */
    private Cursor changeCursor() {

        Cursor cur = null;
        switch (this.mMouseLocation) {
        case WEST: {
            cur = new Cursor(Cursor.W_RESIZE_CURSOR);
            break;
        }
        case EAST: {
            cur = new Cursor(Cursor.E_RESIZE_CURSOR);
            break;
        }
        case NORTH: {
            cur = new Cursor(Cursor.N_RESIZE_CURSOR);
            break;
        }
        case SOUTH: {
            cur = new Cursor(Cursor.S_RESIZE_CURSOR);
            break;
        }
        case NORTH_WEST: {
            cur = new Cursor(Cursor.NW_RESIZE_CURSOR);
            break;
        }
        case SOUTH_EAST: {
            cur = new Cursor(Cursor.SE_RESIZE_CURSOR);
            break;
        }
        case NORTH_EAST: {
            cur = new Cursor(Cursor.NE_RESIZE_CURSOR);
            break;
        }
        case SOUTH_WEST: {
            cur = new Cursor(Cursor.SW_RESIZE_CURSOR);
            break;
        }
        default: {
            cur = Cursor.getDefaultCursor();
        }
        }

        // set the cursor to the window
        // if set to the figure, the cursor does not change
        this.setMouseCursor(cur);

        return cur;
    }

    private void setMouseCursor(Cursor cur) {
        this.mWnd.setCursor(cur);
    }

    /**
     *
     */
    private boolean setMouseCursorSub(final int x, final int y) {
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = array.length - 1; ii >= 0; ii--) {
            if (array[ii].setMouseCursor(x, y)) {
                this.setCursorToWindow(array[ii]);
                return true;
            }
        }

        this.setMouseCursor(Cursor.getDefaultCursor());

        return false;
    }

    /**
     *
     * @return
     */
    protected boolean setCursorToWindow(final SGIFigureElement el) {
        Cursor cur = el.getFigureElementCursor();
        if (cur != null) {
            this.mWnd.setCursor(cur);
        }
        return true;
    }

    /**
     *
     */
    public static final String MENUCMD_RUBBER_BANDING = "Rubber Banding";

    /**
     *
     */
    public static final String MENUCMD_SAVE_PROPERTY = "Save Property";

    /**
     *
     */
    public static final String MENUCMD_PROPERTY = "Property";

    /**
     *
     */
    public boolean prepare() {
        this.mTemporaryProperties = this.getProperties();
        return true;
    }

    /**
     *
     */
    public SGProperties getProperties() {
        SGFigure.FigureProperties p = new SGFigure.FigureProperties();
        this.getProperties(p);
        return p;
    }

    /**
     *
     * @param p
     * @return
     */
    public boolean getProperties(SGProperties p) {
        if ((p instanceof FigureProperties) == false) {
            return false;
        }
        FigureProperties fp = (FigureProperties) p;
        fp.mX = this.mGraphRectX;
        fp.mY = this.mGraphRectY;
        fp.mWidth = this.mGraphRectWidth;
        fp.mHeight = this.mGraphRectHeight;
        fp.mBackgroundColor = this.mBackgroundColor;
        fp.mTransparentFlag = this.mTransparentFlag;
        fp.mLegendVisibleFlag = this.getLegendElement().isVisible();
        fp.mColorBarVisibleFlag = this.getAxisElement().isColorBarVisible();
        fp.mAxisScaleVisibleFlag = this.getAxisElement().isAxisScaleVisible();
        fp.mDataAnchorFlag = this.mDataAnchorFlag;
        return true;
    }

    /**
     * Returns a property dialog.
     *
     * @return property dialog
     */
    public abstract SGPropertyDialog getPropertyDialog();

    /**
     * Called when an action is performed.
     *
     * @param e
     *            an action event
     */
    public void actionPerformed(final ActionEvent e) {
        final String command = e.getActionCommand();
        final Object source = e.getSource();

        // an event from SGIFIgureElement
        if (source instanceof SGIFigureElement) {
            // get the source SGIFigureElement object
            final SGIFigureElement element = (SGIFigureElement) e.getSource();

            // notify the change of SGIFigureElement object
            if (command.equals(SGIFigureElement.SHOW_PROPERTY_DIALOG_FOR_SELECTED_OBJECTS)) {
                // set properties to all selected objects
                this.mWnd.showPropertyDialogForSelectedObjects(this, element);
            } else if (command.equals(SGIFigureElement.SHOW_PROPERTY_DIALOG_FOR_VISIBLE_OBJECTS)) {
                this.mWnd.showPropertyDialogForAllVisibleObjects(this, element);
            } else if (command.equals(SGIFigureElement.SHOW_PROPERTY_DIALOG_FOR_ALL_OBJECTS)) {
                this.mWnd.showPropertyDialogForAllObjects(this, element);
            } else if (command.equals(SGIFigureElement.CLEAR_FOCUSED_OBJECTS)) {
                // clear focused objects
                this.mWnd.clearAllFocusedObjectsInFigures();
            } else if (command.equals(SGIFigureElement.NOTIFY_CHANGE_TO_ROOT)) {
                // notify to root to update the history
                this.notifyToRoot();
            } else if (command.equals(SGIFigureElement.NOTIFY_CHANGE_CURSOR)) {
                // notify the change of mouse cursor
                this.setCursorToWindow(element);
            } else if (command.equals(SGIFigureElement.NOTIFY_DATA_WILL_BE_HIDDEN)) {
            	this.mWnd.notifyToListener(command);
            } else if (command.equals(SGIFigureElement.NOTIFY_DATA_CLICKED)) {
            	this.mWnd.notifyToListener(command);
            } else {
            	
            	if (SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_COMMIT.equals(command)
            			|| SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_PREVIEW.equals(command)
            			|| SGIFigureElement.NOTIFY_DATA_PROPERTIES_CHANGE_ON_COMMIT.equals(command)
            			|| SGIFigureElement.NOTIFY_DATA_PROPERTIES_CHANGE_ON_PREVIEW.equals(command)) {
                	this.mWnd.notifyToListener(command);
            	}
            	
                // broadcast the command
                this.broadcast(element, command);
                this.updateNetCDFLabel(element);
            }
            
        } else {
        	// from the item of pop-up menu of child objects
        	
            if (command.equals(MENUCMD_BRING_TO_FRONT)) {	 
                this.mWnd.bringFocusedObjectsToFront();	 
            } else if (command.equals(MENUCMD_BRING_FORWARD)) {	 
                this.mWnd.bringFocusedObjectsForward();	 
            } else if (command.equals(MENUCMD_SEND_BACKWARD)) {	 
                this.mWnd.sendFocusedObjectsBackward();	 
            } else if (command.equals(MENUCMD_SEND_TO_BACK)) {	 
                this.mWnd.sendFocusedObjectsToBack();	 
            } else if (command.equals(MENUCMD_CUT)) {	 
                this.mWnd.doCut();	 
            } else if (command.equals(MENUCMD_COPY)) {	 
                this.mWnd.doCopy();	 
            } else if (command.equals(MENUCMD_DUPLICATE)) {	 
                this.mWnd.doDuplicate();	 
            } else if (command.equals(MENUCMD_DELETE)) {	 
                this.mWnd.doDelete();	 
            } else if (command.equals(MENUCMD_INSERT_NETCDF_LABEL)) {	 
                this.mWnd.doInserNetCDFLabel();	 
                this.notifyToRoot();
            } else if (command.equals(MENUCMD_ANIMATION)) {
                this.mMouseInExtraRegionFlag = false;
//                this.clearFocusedObjects();
                this.mWnd.doAnimation();
    		} else if (MENUCMD_FIT_ALL_AXES_TO_DATA.equals(command)
    				|| MENUCMD_FIT_HORIZONTAL_AXIS_TO_DATA.equals(command)
    				|| MENUCMD_FIT_VERTICAL_AXIS_TO_DATA.equals(command)
    				|| MENUCMD_FIT_COLOR_BAR_TO_DATA.equals(command)
    				|| MENUCMD_FIT_ALL_AXES_TO_DATA_FOR_ALL_ANIMATION_FRAMES.equals(command)) {
    			this.mWnd.doFitAxisRangeToFocusedData(command);
            } else if (command.equals(MENUCMD_TRANSFORM_DATA)) {	 
                this.mWnd.doTransformData();	 
            } else if (command.equals(MENUCMD_SPLIT_DATA)) {	 
                this.mWnd.doSplitMultipleData();	 
            } else if (command.equals(MENUCMD_MERGE_DATA)) {
                this.mWnd.doMergeMultipleData();
            } else if (command.equals(MENUCMD_ASSIGN_LINE_COLORS)) {
            	this.mWnd.doAssignLineColors();
            } else if (command.equals(MENUCMD_EXPORT_TO_FILE)
            		|| command.equals(MENUCMD_EXPORT_TO_TEXT_FILE)
            		|| command.equals(MENUCMD_EXPORT_TO_NETCDF_FILE)
            		|| command.equals(MENUCMD_EXPORT_TO_HDF5_FILE)
            		|| command.equals(MENUCMD_EXPORT_TO_MATLAB_FILE)) {
                this.mWnd.doOutputDataToFile(command);	 
            } else if (command.equals(MENUCMD_ANCHORED)) {
                final boolean b = ((JCheckBoxMenuItem) e.getSource()).isSelected();
                this.mWnd.setAnchor(b);
            } else if (command.equals(SGIFigureElement.NOTIFY_UNKNOWN_DATA_ERROR)) {	 
                this.hideData(new int[] { ((Integer)source).intValue() } );	 
                this.getWindow().clearUndoBuffer();	 
                return;
            } else if (command.equals(SGIFigureElementAxisConstants.MENUCMD_HIDE_AXES)) {
            	this.mWnd.doHideSelectedAxes();
            } else {
            	this.mWnd.notifyToListener(command);
            }
        }
    }

    void setAxisVisible(final int location, final boolean b) {
    	this.getAxisElement().setAxisVisible(location, b);
    }
    
    boolean isAxisVisible(final int location) {
    	return this.getAxisElement().isAxisVisible(location);
    }

    // an undo manager
    private SGUndoManager mUndoManager = new SGUndoManager(this);

    /**
     * Initialize the history of properties.
     *
     * @return true if succeeded
     */
    public boolean initPropertiesHistory() {
        return this.mUndoManager.initPropertiesHistory();
    }

    /**
     * Undo the operation.
     *
     * @return true if succeeded
     */
    public boolean undo() {
        return this.mUndoManager.undo();
    }

    /**
     * Redo the operation.
     *
     * @return true if succeeded
     */
    public boolean redo() {
        return this.mUndoManager.redo();
    }

    /**
     * Commit the change.
     *
     * @return true if succeeded
     */
    public boolean commit() {
        SGProperties pTemp = this.mTemporaryProperties;
        SGProperties pPresent = this.getProperties();
        if (pTemp.equals(pPresent) == false) {
            this.mUndoManager.setChanged(true);
        }

        this.mTemporaryProperties = null;

        this.updateGraphRect();

        return true;
    }

    /**
     *
     */
    public boolean preview() {
        this.updateGraphRect();

        return true;
    }

    /**
     *
     */
    public boolean cancel() {
        this.setProperties(this.mTemporaryProperties);

        this.updateGraphRect();

        this.mTemporaryProperties = null;

        return true;
    }

    /**
     *
     * @return
     */
    public SGProperties getMemento() {
        return this.getProperties();
    }

    /**
     *
     * @param p
     * @return
     */
    public boolean setMemento(SGProperties p) {
        return this.setProperties(p);
    }

    /**
     *
     */
    public boolean updateHistory() {

        ArrayList list = new ArrayList();
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            SGIFigureElement element = array[ii];
            list.add(element);

//            // particular case
//            if (element.equals(this.getLegendElement())) {
//                // element.setChanged(true);
//            }
        }

        if (this.mUndoManager.updateHistory(list) == false) {
            return false;
        }

        return true;
    }

    /**
     * Initialize the undo buffer.
     */
    public void initUndoBuffer() {

        // figure elements
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].initUndoBuffer();
        }

        // this object
        this.mUndoManager.initUndoBuffer();

        // delete useless data objects
        if (this.deleteUselessData() == false) {
            throw new Error("Failed to initialize undo buffer.");
        }
    }

    /**
     *
     * @return
     */
    public boolean isUndoable() {
        return this.mUndoManager.isUndoable();
    }

    /**
     *
     * @return
     */
    public boolean isRedoable() {
        return this.mUndoManager.isRedoable();
    }

    /**
     * Clear changed flag of this undoable object and all child objects.
     *
     */
    public void clearChanged() {
        this.setChanged(false);
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].clearChanged();
        }
    }

    /**
     * Delete all forward histories.
     *
     * @return
     *         true if succeeded
     */
    public boolean deleteForwardHistory() {

        // delete forward history of figure elements
	SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].deleteForwardHistory() == false) {
        	return false;
            }
        }

        // delete forward history of this figure
        if (this.mUndoManager.deleteForwardHistory() == false) {
            return false;
        }

        // delete useless data objects
        if (this.deleteUselessData() == false) {
            return false;
        }

        return true;
    }

    /**
     * Delete useless data objects.
     * @return
     *         true if succeeded
     */
    protected boolean deleteUselessData() {

		SGIFigureElement[] array = this.getIFigureElementArray();

		// get useless data set
		Set<SGData> dataSet = new HashSet<SGData>();
	        for (int ii = 0; ii < array.length; ii++) {
	            List<SGData> dataList = array[ii].getUselessDataList();
	            dataSet.addAll(dataList);
	        }

	        // delete data objects
	        Iterator<SGData> itr = dataSet.iterator();
	        while (itr.hasNext()) {
	            SGData data = itr.next();
	            this.deleteData(data);
	        }

		return true;
    }

    // delete a data object from this figure and dispose
    private void deleteData(SGData data) {

    	SGIFigureElement[] array = this.getIFigureElementArray();

        // remove from the figure and figure elements
        this.mDataList.remove(data);
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].removeData(data);
        }

        // dispose data
        data.dispose();
    }

    /**
     *
     * @return
     */
    public boolean initGraphAreaLocation() {

    	// calculate the margin around the graph rectangle
        SGTuple2f topAndBottom = new SGTuple2f();
        SGTuple2f leftAndRight = new SGTuple2f();
        if (this.calcMargin(topAndBottom, leftAndRight) == false) {
            return false;
        }

        float topMax = topAndBottom.x;
        float leftMax = leftAndRight.x;

        // set to attributes
        this.mGraphRectX = leftMax;
        this.mGraphRectY = topMax;

        // set to figure elements
        final SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].setGraphRectLocation(this.mGraphRectX, this.mGraphRectY);
            array[ii].setGraphRectSize(this.mGraphRectWidth,
                    this.mGraphRectHeight);
        }

        return true;
    }

    /**
     *
     * @param topAndBottom
     * @param leftAndRight
     * @return
     */
    public boolean calcMargin(final SGTuple2f topAndBottom,
            final SGTuple2f leftAndRight) {

        final SGIFigureElement[] array = this.getIFigureElementArray();

        final SGTuple2f[] tbArray = new SGTuple2f[array.length];
        final SGTuple2f[] lrArray = new SGTuple2f[array.length];

        for (int ii = 0; ii < array.length; ii++) {
            tbArray[ii] = new SGTuple2f();
            lrArray[ii] = new SGTuple2f();
            final boolean flag = array[ii].getMarginAroundGraphRect(
                    tbArray[ii], lrArray[ii]);
            if (!flag) {
                return false;
            }
            // System.out.println(ii+" "+tbArray[ii]+" "+lrArray[ii]);
        }
        // System.out.println();

        float topMax = 0.0f;
        float bottomMax = 0.0f;
        float leftMax = 0.0f;
        float rightMax = 0.0f;
        for (int ii = 0; ii < array.length; ii++) {
            // System.out.println("ii="+ii);

            final float top = tbArray[ii].x;
            final float bottom = tbArray[ii].y;
            final float left = lrArray[ii].x;
            final float right = lrArray[ii].y;

            // System.out.println(top+" "+bottom+" "+left+" "+right);

            if (top > topMax) {
                topMax = top;
            }
            if (bottom > bottomMax) {
                bottomMax = bottom;
            }
            if (left > leftMax) {
                leftMax = left;
            }
            if (right > rightMax) {
                rightMax = right;
            }
        }
        // System.out.println();

        final float mag = this.mMagnification;
        topAndBottom.x = topMax + mag * MARGIN_TOP;
        topAndBottom.y = bottomMax + mag * MARGIN_BOTTOM;
        leftAndRight.x = leftMax + mag * MARGIN_LEFT;
        leftAndRight.y = rightMax + mag * MARGIN_RIGHT;

        return true;
    }

    /**
     *
     * @return
     */
    public Rectangle2D getBoundingBox() {
        final Rectangle2D graphAreaRect = this.getGraphRect();

        SGTuple2f tb = new SGTuple2f();
        SGTuple2f lr = new SGTuple2f();
        if (this.calcMargin(tb, lr) == false) {
            return null;
        }

        Rectangle2D rect = new Rectangle2D.Float((float) graphAreaRect.getX()
                - lr.x, (float) graphAreaRect.getY() - tb.x,
                (float) graphAreaRect.getWidth() + lr.x + lr.y,
                (float) graphAreaRect.getHeight() + tb.x + tb.y);

        return rect;
    }

    public static final int MARGIN_TOP = 5;

    public static final int MARGIN_BOTTOM = 5;

    public static final int MARGIN_LEFT = 5;

    public static final int MARGIN_RIGHT = 5;

    /**
     *
     * @return
     */
    public boolean setBoundingBox(final Rectangle2D bbRect) {
        if (bbRect == null) {
            return false;
        }

        SGTuple2f tb = new SGTuple2f();
        SGTuple2f lr = new SGTuple2f();
        if (this.calcMargin(tb, lr) == false) {
            return false;
        }

        final float x = (float) bbRect.getX() + lr.x;
        final float y = (float) bbRect.getY() + tb.x;
        final float w = (float) bbRect.getWidth() - (lr.x + lr.y);
        final float h = (float) bbRect.getHeight() - (tb.x + tb.y);

        this.setGraphRect(x, y, w, h);

        return true;
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public boolean setCenter(final float cx, final float cy) {
        Rectangle2D bbRect = this.getBoundingBox();
        final float w = (float) bbRect.getWidth();
        final float h = (float) bbRect.getHeight();

        final float x = cx - w / 2.0f;
        final float y = cy - h / 2.0f;

        Rectangle2D rect = new Rectangle2D.Float(x, y, w, h);
        if (this.setBoundingBox(rect) == false) {
            return false;
        }

        return true;
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public boolean setGraphRectLocationByLeftBottom(final float x, final float y) {
        Rectangle2D gRect = this.getGraphRect();
        this.setGraphRectLocation(x, y - (float) gRect.getHeight());
        return true;
    }

    /**
     *
     */
    private boolean broadcast(final SGIFigureElement element, String msg) {
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].synchronize(element, msg);
        }
        return true;
    }

    /**
     * Returns the list of data.
     *
     * @return a list of data
     */
    public List<SGData> getDataList() {
        return new ArrayList<SGData>(this.mDataList);
    }

    /**
     * Returns the list of visible data.
     *
     * @return a list of visible data
     */
    public List<SGData> getVisibleDataList() {
        return this.getGraphElement().getVisibleDataList();
    }

    /**
     *
     */
    public void paint(final Graphics g, final boolean clip) {
    	if (this.isDisposed()) {
    		return;
    	}
        this.paintGraphics((Graphics2D) g, clip);
    }

    /**
     *
     * @param g
     * @param clip
     */
    private void paintGraphics(Graphics2D g2d, boolean clip) {

        // if the mouse pointer is inside the extra region
        if (this.mMouseInExtraRegionFlag) {
            Rectangle2D eBounds = this.getExtraRegionBounds();
            g2d.setPaint(EXTRA_REGION_COLOR);
            g2d.fill(eBounds);
        }

        // fill the background
        if (this.mTransparentFlag == false) {
            final Rectangle2D graphAreaRect = this.getGraphRect();
            g2d.setPaint(this.mBackgroundColor);
            g2d.fill(graphAreaRect);
        }

        // draw the bounding box
        if (SGFigure.mBoundingBoxVisibleFlag
                && this.mSelectionSymbolsVisibleFlag) {
            Rectangle2D rect = this.getBoundingBox();
            if (rect != null) {
                g2d.setPaint(Color.BLUE);
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(rect);
            }
        }

        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].paint(g2d, clip);
        }

    }

    public Element createElement(final Document document, final SGExportParameter params) {
        Element el = document.createElement(SGFigure.TAG_NAME_FIGURE);

        // property of figure
        if (this.writeProperty(el, params) == false) {
            return null;
        }

        if (this.createElementLower(document, el, params) == false) {
            return null;
        }

        return el;
    }

    public Element createElementForFocusedInBoundingBox(final Document document,
    		final SGExportParameter params) {
        Element el = document.createElement(SGFigure.TAG_NAME_FIGURE);

        // property of figure
        if (this.writePropertyOnFocusedInBoundingBox(el, params) == false) {
            return null;
        }

        if (this.createElementLower(document, el, params) == false) {
            return null;
        }

        return el;
    }

    public Element createElementForFocusedForDuplication(final Document document,
    		final SGExportParameter params) {
        Element el = document.createElement(SGFigure.TAG_NAME_FIGURE);

        // property of figure
        if (this.writePropertyForDuplication(el, params) == false) {
            return null;
        }

        if (this.createElementLower(document, el, params) == false) {
            return null;
        }

        return el;
    }

    private boolean createElementLower(final Document document,
            final Element parent, final SGExportParameter params) {

        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii] instanceof SGIFigureElementGraph) {
                continue;
            }
            Element[] elements = array[ii].createElement(document, params);
            if (elements == null) {
                return false;
            }
            for (int jj = 0; jj < elements.length; jj++) {
                parent.appendChild(elements[jj]);
            }
        }

        // create Element objects for data
        List<Element> elList = new ArrayList<Element>();
        List<SGData> dataList = new ArrayList<SGData>();
        if (this.getGraphElement().createElementOfData(document, elList, dataList, params) == false) {
            return false;
        }

        // add an attribute for the index in legend
        SGIFigureElementLegend lElement = this.getLegendElement();
        for (int ii = 0; ii < elList.size(); ii++) {
            Element el = (Element) elList.get(ii);
            SGData data = (SGData) dataList.get(ii);
            final int index = lElement.getIndex(data);
            if (index < 0) {
        	return false;
            }
            el.setAttribute(SGIFigureElement.KEY_INDEX_IN_LEGEND, Integer.toString(index));
        }

        // append new Element objects to the parent
        for (int ii = 0; ii < elList.size(); ii++) {
            Element el = (Element) elList.get(ii);
            parent.appendChild(el);
        }

        return true;
    }

    public boolean writeProperty(final Element el, SGExportParameter params) {
    	SGPropertyMap map = this.getPropertyFileMap(params);
    	map.setToElement(el);
    	return true;
    }

    public boolean writePropertyOnFocusedInBoundingBox(final Element el, SGExportParameter params) {
    	SGPropertyMap map = this.getPropertyFileMap(params);
    	
    	// replaces the values
        final SGDrawingWindow wnd = this.getWindow();
        final Rectangle2D bb = wnd.getBoundingBoxOfFigures(wnd
                .getFocusedFigureList());
        final Rectangle2D paper = wnd.getPaperRect();
        final float x = this.mGraphRectX - (float) (bb.getX() - paper.getX())
                / this.mMagnification;
        final float y = this.mGraphRectY - (float) (bb.getY() - paper.getY())
                / this.mMagnification;
    	SGPropertyUtility.addProperty(map, KEY_FIGURE_X_IN_CLIENT, 
    			this.getExportLengthValue(x), FIGURE_LOCATION_UNIT);
    	SGPropertyUtility.addProperty(map, KEY_FIGURE_Y_IN_CLIENT, 
    			this.getExportLengthValue(y), FIGURE_LOCATION_UNIT);
    	map.setToElement(el);
    	
    	return true;
    }

    public boolean writePropertyForDuplication(final Element el, SGExportParameter params) {
    	SGPropertyMap map = this.getPropertyFileMap(params);
    	
    	// replaces the values
        final float offset = DUPLICATION_OFFSET * this.mMagnification;
        final float x = this.mGraphRectX + offset;
        final float y = this.mGraphRectY + offset;
    	SGPropertyUtility.addProperty(map, KEY_FIGURE_X_IN_CLIENT, 
    			this.getExportLengthValue(x), FIGURE_LOCATION_UNIT);
    	SGPropertyUtility.addProperty(map, KEY_FIGURE_Y_IN_CLIENT, 
    			this.getExportLengthValue(y), FIGURE_LOCATION_UNIT);
    	map.setToElement(el);
    	
    	return true;
    }

    private static final float DUPLICATION_OFFSET = 20.0f;

    /**
     * Returns a text string denoting the class type.
     *
     * @return a text string denoting the class type
     */
    public String getClassType() {
        return this.mClassType;
    }

    /**
     * A text string denoting the class type.
     */
    private String mClassType = null;

    /**
     * Sets the text string denoting the class type.
     *
     * @param type
     *           a text string denoting the class type
     */
    public void setClassType(String type) {
        this.mClassType = type;
    }
    
    /**
     *
     * @param el
     * @return
     */
    public boolean readProperty(final Element el) {
        String str = null;
        Number num = null;
        Boolean b = null;
        Color cl = null;

        // x
        str = el.getAttribute(SGFigure.KEY_FIGURE_X_IN_CLIENT);
        if (str.length() != 0) {
            StringBuffer ux = new StringBuffer();
            num = SGUtilityText.getNumber(str, ux);
            if (num == null) {
                return false;
            }
            final float x = num.floatValue();
            if (this.setFigureX(x, ux.toString()) == false) {
                return false;
            }
        }

        // y
        str = el.getAttribute(SGFigure.KEY_FIGURE_Y_IN_CLIENT);
        if (str.length() != 0) {
            StringBuffer uy = new StringBuffer();
            num = SGUtilityText.getNumber(str, uy);
            if (num == null) {
                return false;
            }
            final float y = num.floatValue();
            if (this.setFigureY(y, uy.toString()) == false) {
                return false;
            }
        }

        // width
        str = el.getAttribute(SGFigure.KEY_FIGURE_WIDTH);
        if (str.length() != 0) {
            StringBuffer uWidth = new StringBuffer();
            num = SGUtilityText.getNumber(str, uWidth);
            if (num == null) {
                return false;
            }
            final float width = num.floatValue();
            if (this.setFigureWidth(width, uWidth.toString()) == false) {
                return false;
            }
        }

        // height
        str = el.getAttribute(SGFigure.KEY_FIGURE_HEIGHT);
        if (str.length() != 0) {
            StringBuffer uHeight = new StringBuffer();
            num = SGUtilityText.getNumber(str, uHeight);
            if (num == null) {
                return false;
            }
            final float height = num.floatValue();
            if (this.setFigureHeight(height, uHeight.toString()) == false) {
                return false;
            }
        }

        // background color
        str = el.getAttribute(SGFigure.KEY_FIGURE_BACKGROUND_COLOR);
        if (str.length() != 0) {
            cl = SGUtilityText.parseColor(str);
            if (cl == null) {
                return false;
            }
            if (this.setBackgroundColor(cl) == false) {
                return false;
            }
        }

        // transparent
        str = el.getAttribute(SGFigure.KEY_FIGURE_BACKGROUND_TRANSPARENT);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return false;
            }
            final boolean transparent = b.booleanValue();
            if (this.setTransparent(transparent) == false) {
                return false;
            }
        }
        
        // data anchor
        str = el.getAttribute(SGFigure.KEY_FIGURE_DATA_ANCHOR);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return false;
            }
            final boolean anchor = b.booleanValue();
            this.setDataAnchored(anchor);
        }

        return true;
    }

    /**
     *
     */
    public boolean createDataObjectFromPropertyFile(final Element elData,
            final SGData data, final boolean readDataProperty) {
        this.mDataList.add(data);

        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].createDataObject(elData, data, readDataProperty) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     */
    public boolean setProperties(SGProperties p) {
        if ((p instanceof FigureProperties) == false) {
            return false;
        }
        FigureProperties fp = (FigureProperties) p;

        this.mGraphRectX = fp.mX;
        this.mGraphRectY = fp.mY;
        this.mGraphRectWidth = fp.mWidth;
        this.mGraphRectHeight = fp.mHeight;

        this.setBackgroundColor(fp.mBackgroundColor);
        this.setTransparent(fp.mTransparentFlag);

        // set to the legend
        this.setLegendVisible(fp.mLegendVisibleFlag);
        
        // set to the color bar
        this.setColorBarVisible(fp.mColorBarVisibleFlag);
        
        // set to the axis scale
        this.setAxisScaleVisible(fp.mAxisScaleVisibleFlag);

        // set to data objects
        this.setDataAnchored(fp.mDataAnchorFlag);

        return true;
    }

    /**
     * Duplicate the focused objects.
     *
     * @return true if succeeded
     */
    protected boolean duplicateFocusedObjects() {
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].duplicateFocusedObjects() == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Create a list of copied objects from focused objects.
     *
     * @return list of copies of focused objects
     */
    protected List<SGICopiable> createCopiedObjects() {
    	List<SGICopiable> list = new ArrayList<SGICopiable>();
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            list.addAll(array[ii].getCopiedObjectsList());
        }
        return list;
    }

    /**
     * Create a list of cut objects from focused objects.
     *
     * @return list of cut objects
     */
    protected List<SGICopiable> cutFocusedObjects() {
    	List<SGICopiable> list = new ArrayList<SGICopiable>();
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            list.addAll(array[ii].cutFocusedObjects());
        }
        return list;
    }

    /**
     * Create copies of focused data objects.
     *
     * @param dataList
     *            list of focused data objects
     * @param dataNameList
     *            list of data names
     * @param propertiesMapList
     *            the map of properties
     * @return true if succeeded
     */
    boolean createCopiedDataObjects(List<SGData> dataList, List<String> dataNameList,
            List<Map<Class, SGProperties>> propertiesMapList) {
    	List<SGData> gList = this.getGraphElement().getFocusedDataList();
        List<SGData> lList = this.getLegendElement().getFocusedDataList();
        return this.cutOrCopyFocusedDataObjects(
        	true, gList, lList, dataList, dataNameList, propertiesMapList);
    }

    /**
     * Create a list of cut data objects from focused objects.
     *
     * @param dataList
     *            list of focused data objects
     * @param dataNameList
     *            list of data names
     * @param propertiesMapList
     *            the map of properties
     * @return true if succeeded
     */
    boolean cutFocusedDataObjects(List<SGData> dataList, List<String> dataNameList,
            List<Map<Class, SGProperties>> propertiesMapList) {
        List<SGData> gList = this.getGraphElement().cutFocusedData();
        List<SGData> lList = this.getLegendElement().cutFocusedData();
        return this.cutOrCopyFocusedDataObjects(
        	true, gList, lList, dataList, dataNameList, propertiesMapList);
    }

    private boolean cutOrCopyFocusedDataObjects(
	    boolean isCopy, List<SGData> gList, List<SGData> lList,
	    List<SGData> dataList, List<String> dataNameList,
            List<Map<Class, SGProperties>> propertiesMapList) {

    	List<SGData> fList = new ArrayList<SGData>();
		fList.addAll(gList);
		for (int ii = 0; ii < lList.size(); ii++) {
			SGData data = lList.get(ii);
		    if (!gList.contains(data)) {
		    	fList.add(data);
		    }
		}

        for (int ii = 0; ii < fList.size(); ii++) {
            SGData data = fList.get(ii);
            dataList.add((SGData) data.copy());

            String name = this.getDataName(data);
            dataNameList.add(name);

            Map<Class, SGProperties> map = this.getDataPropertiesMap(data);
            propertiesMapList.add(map);
        }

        return true;
    }

    /**
     * Returns the data of a given ID.
     *
     * @param id
     *           the ID of data
     * @return the data object if it exists
     */
    public SGData getData(final int id) {
        return this.getGraphElement().getData(id);
    }

    /**
     * Returns whether the data is visible.
     *
     * @param id
     *          the ID of data
     * @return true if the data exists and is visible
     */
    public boolean isDataVisible(final int id) {
        SGData data = this.getData(id);
        if (data == null) {
            return false;
        }
        return this.getGraphElement().isDataVisible(data);
    }

    /**
     * Returns the name of data.
     *
     * @param data
     *            data object
     * @return the name of data
     */
    public String getDataName(SGData data) {
        return this.getGraphElement().getDataName(data);
    }

    /**
     * Sets the name of data.
     *
     * @param name
     *           the name to set to data
     * @param data
     *           the data object
     * @return true if succeeded
     */
    public boolean setDataName(String name, SGData data) {
        return this.getGraphElement().setDataName(name, data);
    }

    /**
     *
     * @param data
     * @return
     */
    public Map<Class, SGProperties> getDataPropertiesMap(SGData data) {
        Map<Class, SGProperties> map = new HashMap<Class, SGProperties>();

        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            SGProperties p = array[ii].getDataProperties(data);
            map.put(array[ii].getClass(), p);
        }

        return map;
    }

    /**
     * Paste the objects.
     *
     * @param list
     *            of the objects to be pasted
     * @return true:succeeded, false:failed
     */
    public boolean paste(List<SGICopiable> list) {
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].paste(list) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * Move the focused objects to the front or back.
     *
     * @param toFront
     *           true to move to the front and false to move to the back
     * @return true if succeeded
     */
    public boolean moveFocusedObjects(final boolean toFront) {
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].moveFocusedObjects(toFront) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Move the focused objects to the front or back.
     *
     * @param num
     *           the number of levels to move the objects
     * @return true if succeeded
     */
    public boolean moveFocusedObjects(final int num) {
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].moveFocusedObjects(num) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Dispose this figure object.
     * Do not call any method of this figure after this method is called.
     */
    public void dispose() {
    	this.mDisposed = true;

        // dispose the property dialog and the pop-up menu
        if (this.mPropertyDialog != null) {
            this.mPropertyDialog.dispose();
            this.mPropertyDialog = null;
        }

        // delete data objects
		SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < this.mDataList.size(); ii++) {
            SGData data = (SGData) this.mDataList.get(ii);
		    for (int jj = 0; jj < array.length; jj++) {
		        array[jj].removeData(data);
		    }
		    data.dispose();
		}
        this.mDataList.clear();

        // delete figure elements
        Iterator<SGIFigureElement> itr = this.mFigureElementMap.values().iterator();
        while (itr.hasNext()) {
            SGIFigureElement el = itr.next();
            el.dispose();
        }
        this.mFigureElementMap.clear();

        this.mComponent = null;
        this.mWnd = null;
    }

    private int mMode = MODE_DISPLAY;

    public void setMode(final int mode) {
        this.mMode = mode;

        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].setMode(mode);
        }
    }

    public int getMode() {
        return this.mMode;
    }

    /**
     *
     */
    public static class FigureProperties extends SGProperties {
        private float mX;

        private float mY;

        private float mWidth;

        private float mHeight;

        private float mSpaceLineAndNumber;

        private float mSpaceNumberAndTitle;

        private Color mBackgroundColor;

        private boolean mTransparentFlag;

        private boolean mLegendVisibleFlag;

        private boolean mColorBarVisibleFlag;

        private boolean mAxisScaleVisibleFlag;

        private boolean mDataAnchorFlag;

        public FigureProperties() {
            super();
        }

        public void dispose() {
        	super.dispose();
            this.mBackgroundColor = null;
        }

        public boolean equals(final Object obj) {
            if ((obj instanceof FigureProperties) == false) {
                return false;
            }
            FigureProperties p = (FigureProperties) obj;
            if (p.mX != this.mX) {
                return false;
            }
            if (p.mY != this.mY) {
                return false;
            }
            if (p.mWidth != this.mWidth) {
                return false;
            }
            if (p.mHeight != this.mHeight) {
                return false;
            }
            if (p.mSpaceLineAndNumber != this.mSpaceLineAndNumber) {
                return false;
            }
            if (p.mSpaceNumberAndTitle != this.mSpaceNumberAndTitle) {
                return false;
            }
            if (p.mBackgroundColor.equals(this.mBackgroundColor) == false) {
                return false;
            }
            if (p.mTransparentFlag != this.mTransparentFlag) {
                return false;
            }
            if (p.mLegendVisibleFlag != this.mLegendVisibleFlag) {
                return false;
            }
            if (p.mColorBarVisibleFlag != this.mColorBarVisibleFlag) {
                return false;
            }
            if (p.mAxisScaleVisibleFlag != this.mAxisScaleVisibleFlag) {
                return false;
            }
            if (p.mDataAnchorFlag != this.mDataAnchorFlag) {
            	return false;
            }
            return true;
        }
    }

    /**
     * @return
     */
    public static boolean isSnappingToGrid() {
        return mSnapToGridFlag;
    }

    /**
     * @param b
     */
    public static void setSnappingToGrid(final boolean b) {
        mSnapToGridFlag = b;
    }

    /**
     * Insert a label for netCDF data.
     *
     * @return true if succeeds.
     */
    boolean insertNetCDFLabel() {
        SGIFigureElementString fes = (SGIFigureElementString) this.getIFigureElement(SGIFigureElementString.class);
        SGIFigureElementGraph feg = (SGIFigureElementGraph) this.getIFigureElement(SGIFigureElementGraph.class);
        if (null==feg.showNetCDFLabels(fes)) {
            return true;
        }
        return false;
    }

    /**
     * Insert labels for netCDF data that have given IDs.
     *
     * @param dataIdArray
     * @return true if succeeds.
     */
    public boolean insertNetCDFLabels(final int[] dataIdArray) {
        List<SGData> dataList = new ArrayList<SGData>();
        for (int ii = 0; ii < dataIdArray.length; ii++) {
            SGData data = this.getData(dataIdArray[ii]);
            if (data == null) {
                return false;
            }
            if (!this.isDataVisible(dataIdArray[ii])) {
            	return false;
            }
            dataList.add(data);
        }

        SGIFigureElementString fes = (SGIFigureElementString) this.getIFigureElement(SGIFigureElementString.class);
        SGIFigureElementGraph feg = (SGIFigureElementGraph) this.getIFigureElement(SGIFigureElementGraph.class);
        if (null==feg.showNetCDFLabels(fes, dataList)) {
            return true;
        }
        return false;
    }

    /**
     * Update a label for netCDF data.
     * @param element
     */
    void updateNetCDFLabel(final SGIFigureElement element) {
        SGIFigureElementString fes = (SGIFigureElementString) this.getIFigureElement(SGIFigureElementString.class);
        SGIFigureElementGraph feg = (SGIFigureElementGraph) this.getIFigureElement(SGIFigureElementGraph.class);
        feg.updateNetCDFLabels(fes);
    }

    /**
     * Called when menu items in the menu bar is selected.
     *
     */
    void onMenuSelected() {
        SGIFigureElement[] array = this.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].onMenuSelected();
        }
    }

    /**
     * Width of the extra region around a figure.
     */
    public static final float EXTRA_REGION_WIDTH_IN_CM_UNIT = 1.0f;

    /**
     * Returns the bounds of the "extra" region.
     *
     * @return the bounds of the "extra" region
     */
    Rectangle2D getExtraRegionBounds() {
        Rectangle2D bounds = this.getGraphRect();
        final float margin = this.mMagnification * EXTRA_REGION_WIDTH_IN_CM_UNIT / CM_POINT_RATIO;
        final double x = bounds.getX() - margin;
        final double y = bounds.getY() - margin;
        final double w = bounds.getWidth() + 2.0 * margin;
        final double h = bounds.getHeight() + 2.0 * margin;
        bounds.setRect(x, y, w, h);
        return bounds;
    }

    /**
     * A flag whether the mouse is inside the extra region.
     */
    boolean mMouseInExtraRegionFlag = false;

    /**
     * Color for extra region.
     */
    private static final Color EXTRA_REGION_COLOR = new Color(127, 127, 127, 95);

    /**
     * Preprocessing for image export.
     *
     */
    void beforeExport() {
        this.mMouseInExtraRegionFlag = false;

        // hide anchors temporarily
        this.setSelectionSymbolsVisible(false);
        this.setSymbolsAroundAllObjectsVisible(false);
    }

    /**
     * Postprocessing for image export.
     *
     */
    void afterExport() {
        // set visible
        this.setVisible(true);

        // show anchors
        this.setSelectionSymbolsVisible(true);
        if (this.isSelected()) {
            this.setSymbolsAroundAllObjectsVisible(true);
        }
        this.setMode(MODE_DISPLAY);
    }

    /**
     * Returns whether the legend is enabled.
     * If there are any visible data objects, this method returns true.
     *
     * @return true if the legend is enabled
     */
    public boolean isLegendAvailable() {
        return (this.getGraphElement().getVisibleDataList().size() != 0);
    }

    /**
     * Returns whether the color bar is available.
     * If there are any visible data objects with the color bar, this method returns true.
     *
     * @return true if the color bar is available
     */
    public boolean isColorBarAvailable() {
        SGIFigureElementAxis aElement = this.getAxisElement();
        return aElement.isColorBarAvailable();
    }

    /**
     * Fits axis range to the focused data.
     *
     * @return true if succeeded
     */
    public boolean fitAxisRangeToFocusedData(final boolean forAnimationFrames) {
    	if (!this.fitAxisRangeToFocusedData(SGIFigureElementAxis.AXIS_DIRECTION_HORIZONTAL,
    			forAnimationFrames)) {
    		return false;
    	}
    	if (!this.fitAxisRangeToFocusedData(SGIFigureElementAxis.AXIS_DIRECTION_VERTICAL,
    			forAnimationFrames)) {
    		return false;
    	}
    	if (!this.fitAxisRangeToFocusedData(SGIFigureElementAxis.AXIS_DIRECTION_NORMAL,
    			forAnimationFrames)) {
    		return false;
    	}
    	return true;
    }

    public boolean fitAxisRangeToFocusedData(final int axisDirection,
    		final boolean forAnimationFrames) {
        SGIFigureElementGraph gElement = this.getGraphElement();
        SGIFigureElementAxis aElement = this.getAxisElement();
        if (aElement.fitAxisRangeToFocusedData(gElement, axisDirection,
        		forAnimationFrames) == false) {
            return false;
        }
        return true;
    }

    /**
     * Fits axis range to given data.
     *
     * @param dataIdArray
     *           an array of data ID
     * @return true if succeeded
     */
    public boolean fitAxisRangeToData(final int[] dataIdArray,
    		final boolean forAnimationFrames) {
    	if (!this.fitAxisRangeToDataSub(dataIdArray, SGIFigureElementAxis.AXIS_DIRECTION_HORIZONTAL,
    			forAnimationFrames)) {
    		return false;
    	}
    	if (!this.fitAxisRangeToDataSub(dataIdArray, SGIFigureElementAxis.AXIS_DIRECTION_VERTICAL,
    			forAnimationFrames)) {
    		return false;
    	}
    	if (!this.fitAxisRangeToDataSub(dataIdArray, SGIFigureElementAxis.AXIS_DIRECTION_NORMAL,
    			forAnimationFrames)) {
    		return false;
    	}
        if (this.isChangedRoot()) {
            this.notifyToRoot();
        }
        this.repaint();
    	return true;
    }

    public boolean fitAxisRangeToData(final int[] dataIdArray, final int axisDirection,
    		final boolean forAnimationFrames, final boolean bNotifyToRoot) {
        List<SGData> dataList = new ArrayList<SGData>();
        for (int ii = 0; ii < dataIdArray.length; ii++) {
            if (!this.isDataVisible(dataIdArray[ii])) {
            	return false;
            }
            SGData data = this.getData(dataIdArray[ii]);
            if (data == null) {
                return false;
            }
            dataList.add(data);
        }
        SGIFigureElementGraph gElement = this.getGraphElement();
        SGIFigureElementAxis aElement = this.getAxisElement();
        if (aElement.fitAxisRangeToData(gElement, dataList, axisDirection,
        		forAnimationFrames) == false) {
            return false;
        }
        if (bNotifyToRoot) {
            if (this.isChangedRoot()) {
                this.notifyToRoot();
            }
        }
        this.repaint();
        return true;
    }

    private boolean fitAxisRangeToDataSub(final int[] dataIdArray, final int axisDirection,
    		final boolean forAnimationFrames) {
        List<SGData> dataList = new ArrayList<SGData>();
        for (int ii = 0; ii < dataIdArray.length; ii++) {
            if (!this.isDataVisible(dataIdArray[ii])) {
            	return false;
            }
            SGData data = this.getData(dataIdArray[ii]);
            if (data == null) {
                return false;
            }
            dataList.add(data);
        }
        SGIFigureElementGraph gElement = this.getGraphElement();
        SGIFigureElementAxis aElement = this.getAxisElement();
        if (aElement.fitAxisRangeToData(gElement, dataList, axisDirection,
        		forAnimationFrames) == false) {
            return false;
        }
        return true;
    }

    /**
     * Fits axis range to the visible all data.
     *
     * @return true if succeeded
     */
    public boolean fitAxisRangeToVisibleData(final boolean forAnimationFrames) {
    	if (!this.fitAxisRangeToVisibleData(SGIFigureElementAxis.AXIS_DIRECTION_HORIZONTAL, 
    			forAnimationFrames, false)) {
    		return false;
    	}
    	if (!this.fitAxisRangeToVisibleData(SGIFigureElementAxis.AXIS_DIRECTION_VERTICAL, 
    			forAnimationFrames, false)) {
    		return false;
    	}
    	if (!this.fitAxisRangeToVisibleData(SGIFigureElementAxis.AXIS_DIRECTION_NORMAL, 
    			forAnimationFrames, false)) {
    		return false;
    	}
        if (this.isChangedRoot()) {
            this.notifyToRoot();
        }
    	return true;
    }

    public boolean fitAxisRangeToVisibleData(final int axisDirection, 
    		final boolean forAnimationFrames) {
    	return this.fitAxisRangeToVisibleData(axisDirection, forAnimationFrames, 
    			false);
    }

    public boolean fitAxisRangeToVisibleData(final int axisDirection, 
    		final boolean forAnimationFrames, final boolean bNotifyToRoot) {
        SGIFigureElementGraph gElement = this.getGraphElement();
        SGIFigureElementAxis aElement = this.getAxisElement();
        List<SGData> dataList = gElement.getVisibleDataList();
        if (dataList.size() == 0) {
        	return false;
        }
        if (aElement.fitAxisRangeToData(gElement, dataList, axisDirection,
        		forAnimationFrames) == false) {
            return false;
        }
        if (bNotifyToRoot) {
            if (this.isChangedRoot()) {
                this.notifyToRoot();
            }
        }
        this.repaint();
        return true;
    }

    /**
     *
     * @return
     */
    public boolean isAlignmentBarsAvailable() {
        return this.getGraphElement().isBarVisible();
    }

    /**
     * Align visible bars in this figure.
     * @return
     */
    public boolean alignVisibleBars() {
        SGIFigureElementGraph gElement = this.getGraphElement();
        if (gElement.alignVisibleBars()==false) {
            return false;
        }
        if (this.isChangedRoot()) {
            this.notifyToRoot();
        }
        this.repaint();
        return true;
    }

    /**
     * If focused objects are enabled to be anchored, set isAnchored flag.
     *
     * @param isAnchored true or false
     */
    void setAnchoredToFocusedObjects(final boolean isAnchored) {
        SGIFigureElement[] figureElement = this.getIFigureElementArray();
        for (int i = 0; i < figureElement.length; i++) {
            List<SGISelectable> alist = figureElement[i].getFocusedObjectsList();
            for (int j = 0; j < alist.size(); j++) {
                Object obj = alist.get(j);
                if (obj instanceof SGIAnchored) {
                    SGIAnchored el = (SGIAnchored)obj;
                    if (el.isAnchored()!=isAnchored) {
                        el.setAnchored(isAnchored);
                        if (el instanceof SGIUndoable) {
                            ((SGIUndoable)el).setChanged(true);
                        }
                    }
                }
            }
        }

        this.repaint();
        this.notifyToRoot();
    }

    /**
     * Sets the properties.
     *
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    public SGPropertyResults setProperties(SGPropertyMap map) {

        SGPropertyResults result = new SGPropertyResults();

        // prepare
        if (this.prepare() == false) {
            return null;
        }

        result = this.setProperties(map, result);
        if (result == null) {
            return null;
        }

        // commit the changes
        if (this.commit() == false) {
            return null;
        }
        this.notifyToRoot();
        this.repaint();

        return result;
    }

    protected SGPropertyResults setProperties(SGPropertyMap map,
            SGPropertyResults iResult) {

        SGPropertyResults result = (SGPropertyResults) iResult.clone();
        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);

            if (COM_FIGURE_LOCATION_X.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_FIGURE_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setFigureX(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_FIGURE_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_LOCATION_X, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_LOCATION_Y.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_FIGURE_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setFigureY(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_FIGURE_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_LOCATION_Y, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_WIDTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_FIGURE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setFigureWidth(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_FIGURE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_WIDTH, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_HEIGHT.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_FIGURE_HEIGHT, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setFigureHeight(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_FIGURE_HEIGHT, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_HEIGHT, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_FRAME_VISIBLE.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_FIGURE_FRAME_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setFrameVisible(b.booleanValue()) == false) {
                    result.putResult(COM_FIGURE_FRAME_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_FRAME_VISIBLE, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_FRAME_LINE_WIDTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_FIGURE_FRAME_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setFrameLineWidth(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_FIGURE_FRAME_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_FRAME_LINE_WIDTH,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_FRAME_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setFrameLineColor(cl) == false) {
                        result.putResult(COM_FIGURE_FRAME_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
                	if (cl == null) {
                        result.putResult(COM_FIGURE_FRAME_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.setFrameLineColor(cl) == false) {
                        result.putResult(COM_FIGURE_FRAME_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                }
                result.putResult(COM_FIGURE_FRAME_COLOR, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_BACKGROUND_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setBackgroundColor(cl) == false) {
                        result.putResult(COM_FIGURE_BACKGROUND_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
                	if (cl == null) {
                        result.putResult(COM_FIGURE_BACKGROUND_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.setBackgroundColor(cl) == false) {
                        result.putResult(COM_FIGURE_BACKGROUND_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                }
                result.putResult(COM_FIGURE_BACKGROUND_COLOR, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_TRANSPARENT.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_FIGURE_TRANSPARENT, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setTransparent(b.booleanValue()) == false) {
                    result.putResult(COM_FIGURE_TRANSPARENT, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_TRANSPARENT, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_DATA_ANCHOR.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_FIGURE_DATA_ANCHOR, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setDataAnchored(b.booleanValue()) == false) {
                    result.putResult(COM_FIGURE_DATA_ANCHOR, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_DATA_ANCHOR, SGPropertyResults.SUCCEEDED);
            }
        }

        return result;
    }

    /**
     * Closes the text field.
     *
     * @return true if succeeded
     */
    public boolean closeTextField() {
    	SGIFigureElement[] array = this.getIFigureElementArray();
    	for (int ii = 0; ii < array.length; ii++) {
    		if (array[ii].closeTextField() == false) {
    			return false;
    		}
    	}
    	return true;
    }

    // The flag whether this object is already disposed of.
    private boolean mDisposed = false;

    /**
     * Returns whether this object is already disposed of.
     *
     * @return true if this object is already disposed of
     */
    public boolean isDisposed() {
    	return this.mDisposed;
    }

    /**
     * Returns whether data objects in this figure are anchored.
     * 
     * @return true if data objects in this figure are anchored
     */
    public boolean isDataAnchored() {
    	return this.mDataAnchorFlag;
    }

    /**
     * Sets the flag whether data objects in this figure are anchored
     * 
     * @param b
     *           true to set data objects in this figure anchored
     * @return true if succeeded
     */
    public boolean setDataAnchored(final boolean b) {
    	this.mDataAnchorFlag = b;
    	SGIFigureElement[] array = this.getIFigureElementArray();
    	for (int ii = 0; ii < array.length; ii++) {
    		if (!array[ii].setDataAnchored(b)) {
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * Returns the list of focused data.
     * 
     * @return the list of focused data
     */
    public List<SGData> getFocusedDataList() {
        SGIFigureElementGraph gElement = this.getGraphElement();
        return gElement.getFocusedDataList();
    }
    
    /**
     * Returns the list of focused data in the order of legend.
     * 
     * @return the list of focused data in the order of legend
     */
    public List<SGData> getFocusedDataListInLegendOrder() {
        SGIFigureElementLegend legend = this.getLegendElement();
        return legend.getFocusedDataListInSortedOrder();
    }

    public void setPropertyDialogObserverClass(Class<?> cl) {
    	SGIFigureElement[] elArray = this.getIFigureElementArray();
    	for (SGIFigureElement el : elArray) {
    		el.setPropertyDialogObserverClass(cl);
    	}
    }

    /**
     * Creates and returns the map of properties for the property file.
     * 
     * @return the map of properties
     */
    public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	SGPropertyUtility.addProperty(map, KEY_FIGURE_TYPE, this.getClassType());
    	this.addProperties(map, KEY_FIGURE_X_IN_CLIENT, KEY_FIGURE_Y_IN_CLIENT, 
    			KEY_FIGURE_WIDTH, KEY_FIGURE_HEIGHT, KEY_FIGURE_BACKGROUND_COLOR, 
    			KEY_FIGURE_BACKGROUND_TRANSPARENT, KEY_FIGURE_DATA_ANCHOR);
    	return map;
    }
    
    /**
     * Creates and returns the map of properties.
     * 
     * @return the map of properties
     */
    public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	this.addProperties(map, COM_FIGURE_LOCATION_X, COM_FIGURE_LOCATION_Y, 
    			COM_FIGURE_WIDTH, COM_FIGURE_HEIGHT, COM_FIGURE_BACKGROUND_COLOR, 
    			COM_FIGURE_TRANSPARENT, COM_FIGURE_DATA_ANCHOR);
    	return map;
    }
    
    private void addProperties(SGPropertyMap map, String xKey, String yKey,
    		String widthKey, String heightKey, String bgColorKey, String transparentKey,
    		String dataAnchorKey) {
    	// location and size
    	SGPropertyUtility.addProperty(map, xKey, 
    			this.getExportLengthValue(this.mGraphRectX), FIGURE_LOCATION_UNIT);
    	SGPropertyUtility.addProperty(map, yKey, 
    			this.getExportLengthValue(this.mGraphRectY), FIGURE_LOCATION_UNIT);
    	SGPropertyUtility.addProperty(map, widthKey, 
    			this.getExportLengthValue(this.mGraphRectWidth), FIGURE_SIZE_UNIT);
    	SGPropertyUtility.addProperty(map, heightKey, 
    			this.getExportLengthValue(this.mGraphRectHeight), FIGURE_SIZE_UNIT);
    	
    	// background
    	SGPropertyUtility.addProperty(map, bgColorKey, this.getBackgroundColor());
    	SGPropertyUtility.addProperty(map, transparentKey, this.isTransparent());
    	
    	// anchor
    	SGPropertyUtility.addProperty(map, dataAnchorKey, this.isDataAnchored());
    }

    private float getExportLengthValue(final float ptLen) {
    	return SGUtility.getExportValue(ptLen * SGIConstants.CM_POINT_RATIO, 
    			SGIRootObjectConstants.LENGTH_MINIMAL_ORDER);
    }

    /**
     * Returns a text string of the commands.
     * 
     * @return a text string of the commands
     */
	public String getCommandString(SGExportParameter params) {
		StringBuffer sb = new StringBuffer();
		
		// creates the command for this figure
		String figureCommands = SGCommandUtility.createCommandString(COM_FIGURE, 
				Integer.toString(this.mID), this.getCommandPropertyMap(params));
		sb.append(figureCommands);
		
		SGIFigureElement[] elArray = this.getIFigureElementArray();
		for (SGIFigureElement el : elArray) {
			String elCommand = el.getCommandString(params);
			sb.append(elCommand);
		}
		
		return sb.toString();
	}

	boolean hasAnimationAvailableData() {
		boolean available = false;
		List<SGData> dataList = this.getVisibleDataList();
		for (SGData data : dataList) {
			if (!data.isAnimationSupported()) {
				continue;
			}
			if (data.isAnimationAvailable()) {
				available = true;
				break;
			}
		}
		return available;
	}
	
	String getAxisValueString(final int x, final int y) {
		SGIFigureElementGraph gElement = this.getGraphElement();
		return gElement.getAxisValueString(x, y);
	}

    public SGIFigureElementGrid getGridElement() {
        return (SGIFigureElementGrid) this
                .getIFigureElement(SGIFigureElementGrid.class);
    }

    public boolean isAxisScaleVisible() {
        return this.getAxisElement().isAxisScaleVisible();
    }

	public boolean setAxisScaleVisibleWithCommit(final boolean flag) {
		SGIFigureElementAxis aElement = this.getAxisElement();
        aElement.setAxisScaleVisibleForCommit(flag);
        return true;
	}

    public boolean isGridVisible() {
        return this.getGridElement().isGridVisible();
    }

	public boolean setGridVisibleWithCommit(final boolean flag) {
		SGIFigureElementGrid grid = this.getGridElement();
        final boolean v = grid.isGridVisible();
        if (v != flag) {
            grid.setGridVisible(flag);
            this.setChanged(true);
        }
        return true;
	}

    public boolean setColorBarVisibleWithCommit(final boolean b) {
        final SGIFigureElementAxis aElement = this.getAxisElement();
        aElement.setColorBarVisibleForCommit(b);
        return true;
    }


}

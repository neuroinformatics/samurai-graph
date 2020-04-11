package jp.riken.brain.ni.samuraigraph.figure.java2d;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementArrow;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementSymbol;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGIArrowConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;

import org.w3c.dom.Element;

/**
 * 
 */
public abstract class SGElementGroupArrow extends SGElementGroup implements
	SGIElementGroupGridSXY, SGIArrowConstants, SGIElementGroupConstants {

    /**
     * The line stroke.
     */
    protected SGStroke mStroke = new SGStroke();

    /**
     * The line type.
     */
    protected int mLineType;

    /**
     * Head size.
     */
    protected float mHeadSize;

    /**
     * Open angle.
     */
    protected float mHeadOpenAngle;

    /**
     * Close angle.
     */
    protected float mHeadCloseAngle;

    /**
     * The start head type.
     */
    protected int mStartHeadType;

    /**
     * The end head type.
     */
    protected int mEndHeadType;

    /**
     * The color.
     */
    protected Color mColor;

    /**
     * The shape of the start head.
     */
    protected Shape mStartHeadShape = null;
    
    /**
     * The shape of the end head.
     */
    protected Shape mEndHeadShape = null;
    
    /**
     * A flag whether the color map is under the grid mode.
     */
    protected boolean mGridMode = false;

    /**
     * The bounds of this arrows.
     */
    protected Rectangle2D mBounds = new Rectangle2D.Float();

    // the margin for single data value
    protected static final float SINGLE_VALUE_MARGIN = 10.0f;

    /**
     * The array of x-coordinates.
     */
    protected float[] mXCoordinateArray = null;
    
    /**
     * The array of y-coordinates.
     */
    protected float[] mYCoordinateArray = null;

    /**
     * The default constructor.
     */
    public SGElementGroupArrow() {
        super();
    }

    /**
     * Disposes of this object.
     */
    public void dispose() {
        super.dispose();
        this.mBounds = null;
        this.mXCoordinateArray = null;
        this.mYCoordinateArray = null;
    }

    /**
     * Returns a stroke.
     * 
     * @return a stroke
     */
    protected SGStroke getStroke() {
        return this.mStroke;
    }

    /**
     * Sets the magnification.
     * 
     * @param ratio
     *           the magnification
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        if (super.setMagnification(mag) == false) {
            return false;
        }
        this.mStroke.setMagnification(mag);
        this.updateHeadShape();
        return true;
    }

    /**
     * Returns the line type.
     * 
     * @return the line type
     */
    public int getLineType() {
        return this.mLineType;
    }

    /**
     * Returns the line width.
     * 
     * @return the line width.
     */
    public float getLineWidth() {
        return this.mStroke.getLineWidth();
    }

    /**
     * 
     * @param unit
     * @return
     */
    public float getLineWidth(final String unit) {
        return (float) SGUtilityText
                .convertFromPoint(this.getLineWidth(), unit);
    }

    /**
     * 
     */
    public float getHeadSize() {
        return this.mHeadSize;
    }

    /**
     * 
     * @param unit
     * @return
     */
    public float getHeadSize(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getHeadSize(), unit);
    }

    /**
     * 
     */
    public float getHeadOpenAngle() {
        return this.mHeadOpenAngle;
    }

    /**
     * 
     */
    public float getHeadCloseAngle() {
        return this.mHeadCloseAngle;
    }

    /**
     * 
     */
    public int getStartHeadType() {
        return this.mStartHeadType;
    }

    /**
     * 
     */
    public int getEndHeadType() {
        return this.mEndHeadType;
    }

    /**
     * Returns the color.
     * 
     * @return the color
     */
    public Color getColor() {
        return this.mColor;
    }
    
    /**
     * Returns the shape of the start head.
     * 
     * @return a shape object
     */
    public Shape getStartHeadShape() {
    	return this.mStartHeadShape;
    }
    
    /**
     * Returns the shape of the end head.
     * 
     * @return a shape object
     */
    public Shape getEndHeadShape() {
    	return this.mEndHeadShape;
    }
    
    /**
     * Updates the head shape.
     *
     */
    protected void updateHeadShape() {
    	final float size = this.getMagnification() * this.getHeadSize();
    	final float open = this.getHeadOpenAngle();
    	final float close = this.getHeadCloseAngle();
    	this.mStartHeadShape = SGDrawingElementArrow2D.createHeadShape(this.getStartHeadType(),
    			size, open, close);
    	this.mEndHeadShape = SGDrawingElementArrow2D.createHeadShape(this.getEndHeadType(),
    			size, open, close);
    }

    /**
     * Sets the line type.
     * 
     * @param type
     *           the line type to set
     * @return true if succeeded
     */
    public boolean setLineType(final int type) {
        if (SGDrawingElementLine.isValidLineType(type) == false) {
            return false;
        }
        this.mLineType = type;
        this.mStroke.setLineType(type);
        return true;
    }

    /**
     * Sets the line width.
     * 
     * @param lw
     *           line width to set
     * @return true if succeeded
     */
    public boolean setLineWidth(final float lw) {
        if (lw < 0.0f) {
            throw new IllegalArgumentException("lw < 0.0f");
        }
        this.mStroke.setLineWidth(lw);
        return true;
    }

    /**
     * Sets the line width with a given unit.
     * 
     * @param lw
     *           the line width to set
     * @param unit
     *           the unit for line width
     * @return true if succeeded
     */
    public abstract boolean setLineWidth(final float lw, final String unit);

    /**
     * Sets the head size.
     * 
     * @param size
     *           the head size to set
     * @return true if succeeded
     */
    public boolean setHeadSize(final float size) {
        if (size < 0.0f) {
            throw new IllegalArgumentException("size < 0.0f");
        }
        this.mHeadSize = size;
        this.updateHeadShape();
        return true;
    }

    /**
     * Sets the head size with a given unit.
     * @param size
     *           the head size to set
     * @param unit
     *           the unit for a given head size
     * @return true if succeeded
     */
    public abstract boolean setHeadSize(final float size, final String unit);

    /**
     * Sets the head open and close angle.
     * 
     * @param openAngle
     *           the head open angle to set in units of degree
     * @param closeAngle
     *           the head close angle to set in units of degree
     * @return true if succeeded
     */
    public abstract boolean setHeadAngle(final float openAngle, final float closeAngle);

    /**
     * Sets the start head type.
     * 
     * @param type
     *           the start head type to set
     * @return true if succeeded
     */
    public boolean setStartHeadType(final int type) {
        if (SGDrawingElementArrow.isValidArrowHeadType(type) == false) {
            return false;
        }
        this.mStartHeadType = type;
        this.updateHeadShape();
        return true;
    }

    /**
     * Sets the end head type.
     * 
     * @param type
     *           the end head type to set
     * @return true if succeeded
     */
    public boolean setEndHeadType(final int type) {
        if (SGDrawingElementArrow.isValidArrowHeadType(type) == false) {
            return false;
        }
        this.mEndHeadType = type;
        this.updateHeadShape();
        return true;
    }

    /**
     * Sets the color.
     * 
     * @param color
     *           the color to set
     * @return true if succeeded
     */
    public boolean setColor(final Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }
        this.mColor = color;
        return true;
    }

    /**
     * Returns the location of the start point at a given index.
     * 
     * @param index
     *           the index
     * @return the location of the start point at a given index
     */
    public abstract SGTuple2f getStartLocation(final int index);

    /**
     * Returns the location of the end point at a given index.
     * 
     * @param index
     *           the index
     * @return the location of the end point at a given index
     */
    public abstract SGTuple2f getEndLocation(final int index);

    /**
     * 
     */
    public boolean paintElement(final Graphics2D g2d, final Rectangle2D clipRect) {
        SGDrawingElement[] array = this.mDrawingElementArray;
        if (array == null) {
            return true;
        }
        for (int ii = 0; ii < array.length; ii++) {
            SGDrawingElementArrow2D el = (SGDrawingElementArrow2D) array[ii];
            if (el.isVisible() == false) {
                continue;
            }
            el.paint(g2d, clipRect);
        }
        return true;
    }

    /**
     * 
     */
    public static String getSymbolTypeName(final int type) {
        // System.out.println("type="+type);

        String typeName = null;
        switch (type) {
        case SGIArrowConstants.SYMBOL_TYPE_CIRCLE:
            typeName = SYMBOL_NAME_CIRCLE;
            break;
        case SGIArrowConstants.SYMBOL_TYPE_SQUARE:
            typeName = SYMBOL_NAME_SQUARE;
            break;
        case SGIArrowConstants.SYMBOL_TYPE_TRIANGLE:
            typeName = SYMBOL_NAME_TRIANGLE;
            break;
        case SGIArrowConstants.SYMBOL_TYPE_CROSS:
            typeName = SYMBOL_NAME_CROSS;
            break;
        case SGIArrowConstants.SYMBOL_TYPE_TRANSVERSELINE:
            typeName = SYMBOL_NAME_TRANSVERSE_LINE;
            break;
        case SGIArrowConstants.SYMBOL_TYPE_ARROW_HEAD:
            typeName = SYMBOL_NAME_ARROW_HEAD;
            break;
        case SGIArrowConstants.SYMBOL_TYPE_ARROW:
            typeName = SYMBOL_NAME_ARROW;
            break;
        case SGIArrowConstants.SYMBOL_TYPE_VOID:
            typeName = SYMBOL_NAME_VOID;
            break;
        default: {
        }
        }
        return typeName;
    }

    /**
     * 
     * @return
     */
    public String getTagName() {
        return TAG_NAME_ARROW;
    }

    /**
     * 
     */
    public boolean writeProperty(final Element el) {
        final float cp = SGIConstants.CM_POINT_RATIO;

        final int digitLineWidth = SGIConstants.LINE_WIDTH_MINIMAL_ORDER - 1;
        final int digitHeadSize = ARROW_HEAD_SIZE_MINIMAL_ORDER - 1;
        final int digitAngle = ARROW_HEAD_ANGLE_MINIMAL_ORDER - 1;

        final float lineWidth = (float) SGUtilityNumber.roundOffNumber(
                this.getLineWidth(), digitLineWidth);
        final float headSize = (float) SGUtilityNumber.roundOffNumber(
                this.getHeadSize() * cp, digitHeadSize);
        final float openAngle = (float) SGUtilityNumber.roundOffNumber(
        	this.getHeadOpenAngle(), digitAngle);
        final float closeAngle = (float) SGUtilityNumber.roundOffNumber(
        	this.getHeadCloseAngle(), digitAngle);
        List<Color> cList = new ArrayList<Color>();
        cList.add(this.getColor());
	
        el.setAttribute(KEY_LINE_WIDTH, Float.toString(lineWidth)
                + SGIConstants.pt);
        el.setAttribute(KEY_LINE_TYPE, SGDrawingElementLine
                .getLineTypeName(this.getLineType()));
        el.setAttribute(KEY_HEAD_SIZE, Float.toString(headSize)
                + SGIConstants.cm);
        el.setAttribute(KEY_START_HEAD_TYPE, SGDrawingElementArrow
                .getArrowHeadTypeName(this.getStartHeadType()));
        el.setAttribute(KEY_END_HEAD_TYPE, SGDrawingElementArrow
                .getArrowHeadTypeName(this.getEndHeadType()));
        el.setAttribute(KEY_HEAD_OPEN_ANGLE, Float.toString(openAngle));
        el.setAttribute(KEY_HEAD_CLOSE_ANGLE, Float.toString(closeAngle));
        el.setAttribute(KEY_COLOR, SGUtilityText.getColorListString(cList));
        
        return true;
    }

    /**
     * 
     * @param el
     * @return
     */
    public boolean readProperty(final Element el) {
        String str = null;
        Number num = null;
        Color cl = null;
        // Boolean b = null;
        List list = null;

        // line width
        str = el.getAttribute(KEY_LINE_WIDTH);
        if (str.length() != 0) {
            StringBuffer uLineWidth = new StringBuffer();
            num = SGUtilityText.getNumber(str, uLineWidth);
            if (num == null) {
                return false;
            }
            final float lineWidth = num.floatValue();
            if (this.setLineWidth(lineWidth, uLineWidth.toString()) == false) {
                return false;
            }
        }

        // line type
        str = el.getAttribute(KEY_LINE_TYPE);
        if (str.length() != 0) {
            num = SGDrawingElementLine.getLineTypeFromName(str);
            if (num == null) {
                return false;
            }
            final int lineType = num.intValue();
            if (this.setLineType(lineType) == false) {
                return false;
            }
        }

        // head size
        str = el.getAttribute(KEY_HEAD_SIZE);
        if (str.length() != 0) {
            StringBuffer uHeadSize = new StringBuffer();
            num = SGUtilityText.getNumber(str, uHeadSize);
            if (num == null) {
                return false;
            }
            final float headSize = num.floatValue();
            if (this.setHeadSize(headSize, uHeadSize.toString()) == false) {
                return false;
            }
        }

        // start head type
        str = el.getAttribute(KEY_START_HEAD_TYPE);
        if (str.length() != 0) {
            num = SGDrawingElementArrow.getArrowHeadTypeFromName(str);
            if (num == null) {
                return false;
            }
            final int startHeadType = num.intValue();
            if (this.setStartHeadType(startHeadType) == false) {
                return false;
            }
        }

        // end head type
        str = el.getAttribute(KEY_END_HEAD_TYPE);
        if (str.length() != 0) {
            num = SGDrawingElementArrow.getArrowHeadTypeFromName(str);
            if (num == null) {
                return false;
            }
            final int endHeadType = num.intValue();
            if (this.setEndHeadType(endHeadType) == false) {
                return false;
            }
        }

        // open angle
        str = el.getAttribute(KEY_HEAD_OPEN_ANGLE);
        if (str.length() != 0) {
            num = SGUtilityText.getFloat(str, SGIConstants.degree);
            if (num == null) {
                return false;
            }
            final float openAngle = num.floatValue();
            
            // close angle
            str = el.getAttribute(KEY_HEAD_CLOSE_ANGLE);
            if (str.length() != 0) {
                num = SGUtilityText.getFloat(str, SGIConstants.degree);
                if (num == null) {
                    return false;
                }
                final float closeAngle = num.floatValue();
                if (this.setHeadAngle(openAngle, closeAngle) == false) {
                    return false;
                }
            }
        }

        // color
        str = el.getAttribute(KEY_COLOR);
        if (str.length() != 0) {
            list = SGUtilityText.getColorList(str);
            if (list == null) {
                return false;
            }
            if (list.size() < 1) {
                return false;
            }
            cl = (Color) list.get(0);
            if (this.setColor(cl) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * 
     * @return
     */
    protected boolean initDrawingElement(final SGTuple2f[] startArray,
            final SGTuple2f[] endArray) {
        final int num = startArray.length;
        if (this.initDrawingElement(num) == false) {
            return false;
        }

        SGDrawingElement[] bArray = this.mDrawingElementArray;
        for (int ii = 0; ii < bArray.length; ii++) {
            ((SGDrawingElementArrow) bArray[ii]).setLocation(startArray[ii],
                    endArray[ii]);
        }

        return true;
    }

    /**
     * 
     */
    public boolean setLocation(final SGTuple2f[] startArray,
            final SGTuple2f[] endArray) {
        if (startArray == null || endArray == null) {
            throw new IllegalArgumentException(
                    "startArray==null || endArray==null");
        }
        if (startArray.length != endArray.length) {
            throw new IllegalArgumentException(
                    "startArray.length != endArray.length");
        }
        if (startArray.length != this.mDrawingElementArray.length) {
        	this.initDrawingElement(startArray.length);
        }
        SGDrawingElement[] array = this.mDrawingElementArray;
        for (int ii = 0; ii < array.length; ii++) {
            final boolean eff = !(startArray[ii].isInfinite()
                    || startArray[ii].isNaN() || endArray[ii].isInfinite() || endArray[ii]
                    .isNaN());
            final SGDrawingElementArrow arrow = (SGDrawingElementArrow) array[ii];
            arrow.setVisible(eff);
            if (eff) {
                arrow.setLocation(startArray[ii], endArray[ii]);
            }
        }
        return true;
    }

    /**
     * 
     */
    public SGProperties getProperties() {
        ArrowProperties p = new ArrowProperties();
        if (this.getProperties(p) == false)
            return null;

        return p;
    }

    /**
     * 
     */
    public boolean getProperties(SGProperties p) {
        if (p == null) {
            return false;
        }
        if ((p instanceof ArrowProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        ArrowProperties ap = (ArrowProperties) p;
        ap.setLineWidth(this.getLineWidth());
        ap.setLineType(this.getLineType());
        ap.setHeadSize(this.getHeadSize());
        ap.setStartHeadType(this.getStartHeadType());
        ap.setEndHeadType(this.getEndHeadType());
        ap.setHeadOpenAngle(this.getHeadOpenAngle());
        ap.setHeadCloseAngle(this.getHeadCloseAngle());
        ap.setColor(this.getColor());
        return true;
    }

    /**
     * 
     */
    public boolean setProperties(SGProperties p) {
        if ((p instanceof ArrowProperties) == false)
            return false;

        if (super.setProperties(p) == false)
            return false;

        ArrowProperties ap = (ArrowProperties) p;

        final Float lineWidth = ap.getLineWidth();
        if (lineWidth == null) {
            return false;
        }
        this.setLineWidth(lineWidth.floatValue());

        final Integer lineType = ap.getLineType();
        if (lineType == null) {
            return false;
        }
        this.setLineType(lineType.intValue());

        final Float headSize = ap.getHeadSize();
        if (headSize == null) {
            return false;
        }
        this.setHeadSize(headSize.floatValue());

        final Integer startHeadType = ap.getStartHeadType();
        if (startHeadType == null) {
            return false;
        }
        this.setStartHeadType(startHeadType.intValue());

        final Integer endHeadType = ap.getEndHeadType();
        if (endHeadType == null) {
            return false;
        }
        this.setEndHeadType(endHeadType.intValue());

        final Float headOpenAngle = ap.getHeadOpenAngle();
        if (headOpenAngle == null) {
            return false;
        }
        final Float headCloseAngle = ap.getHeadCloseAngle();
        if (headCloseAngle == null) {
            return false;
        }
        this.setHeadAngle(headOpenAngle.floatValue(), headCloseAngle.floatValue());
        
        final Color cl = ap.getColor();
        if (cl == null) {
            return false;
        }
        this.setColor(cl);

        return true;
    }

    /**
     * @author okumura
     */
    public static class ArrowProperties extends ElementGroupProperties {

        private SGDrawingElementArrow.ArrowProperties mArrowProperties = new SGDrawingElementArrow.ArrowProperties();

        /**
         * 
         * 
         */
        public ArrowProperties() {
            super();
        }

        /**
         * Copy this object.
         * @return a copied object
         */
        public Object copy() {
            Object obj = super.copy();
            ArrowProperties p = (ArrowProperties) obj;
            p.mArrowProperties = (SGDrawingElementArrow.ArrowProperties) this.mArrowProperties.copy();
            return p;
        }
        
        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.mArrowProperties.dispose();
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof ArrowProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            ArrowProperties p = (ArrowProperties) obj;
            if (this.mArrowProperties.equals(p.mArrowProperties) == false) {
                return false;
            }
            return true;
        }

        public Float getLineWidth() {
            return this.mArrowProperties.getLineWidth();
        }

        public Integer getLineType() {
            return this.mArrowProperties.getLineType();
        }

        public Float getHeadLineWidth() {
            return this.mArrowProperties.getLineWidth();
        }

        public Float getHeadSize() {
            return this.mArrowProperties.getHeadSize();
        }

        public Integer getStartHeadType() {
            return this.mArrowProperties.getStartHeadType();
        }

        public Integer getEndHeadType() {
            return this.mArrowProperties.getEndHeadType();
        }

        public Float getHeadOpenAngle() {
            return this.mArrowProperties.getHeadOpenAngle();
        }

        public Float getHeadCloseAngle() {
            return this.mArrowProperties.getHeadCloseAngle();
        }
        
        public Color getColor() {
            return this.mArrowProperties.getColor();
        }

        public boolean setLineWidth(final float width) {
            return this.mArrowProperties.setLineWidth(width);
        }

        public boolean setLineType(final int num) {
            return this.mArrowProperties.setLineType(num);
        }

        public boolean setStartHeadType(final int num) {
            return this.mArrowProperties.setStartHeadType(num);
        }

        public boolean setEndHeadType(final int num) {
            return this.mArrowProperties.setEndHeadType(num);
        }

        public boolean setHeadSize(final float size) {
            return this.mArrowProperties.setHeadSize(size);
        }

        public boolean setHeadOpenAngle(final float value) {
            return this.mArrowProperties.setHeadOpenAngle(value);
        }

        public boolean setHeadCloseAngle(final float value) {
            return this.mArrowProperties.setHeadCloseAngle(value);
        }

        public boolean setColor(final Color cl) {
            return this.mArrowProperties.setColor(cl);
        }

    }

    /**
     * Arrow in a group of arrows.
     *
     */
    protected static class ArrowInGroup extends SGDrawingElementArrow2D {
        
        /**
         * The group of arrows.
         */
        protected SGElementGroup mGroup = null;
        
        /**
         * The array index.
         */
        protected int mIndex = -1;

        // returns a group of arrows
        private SGElementGroupArrow getArrowGroup() {
            return (SGElementGroupArrow) this.mGroup;
        }

        /**
         * Builds this object in a given group.
         * 
         * @param group
         *           a group of arrows
         * @param index
         *           the array index
         */
        public ArrowInGroup(final SGElementGroup group, final int index) {
            super();
            this.mGroup = group;
            this.mIndex = index;
            this.mLine = this.createBodyInstance();
            this.mStartHead = this.createHeadInstance(this, true);
            this.mEndHead = this.createHeadInstance(this, false);
        }

        /**
         * Returns the line width.
         * 
         * @return the line width
         */
        public float getLineWidth() {
            return this.getArrowGroup().getLineWidth();
        }

        /**
         * Returns the line type.
         * 
         * @return the line type
         */
        public int getLineType() {
            return this.getArrowGroup().getLineType();
        }

        /**
         * Returns the head size.
         * 
         * @return the head size
         */
        public float getHeadSize() {
            return this.getArrowGroup().getHeadSize();
        }
        
        /**
         * Returns the start head type.
         * 
         * @return the start head type
         */
        public int getStartHeadType() {
            return this.getArrowGroup().getStartHeadType();
        }

        /**
         * Returns the end head type.
         * 
         * @return the end head type
         */
        public int getEndHeadType() {
            return this.getArrowGroup().getEndHeadType();
        }

        /**
         * Returns the close angle of the arrow head.
         * 
         * @return the close angle of the arrow head
         */
        public float getHeadCloseAngle() {
            return this.getArrowGroup().getHeadCloseAngle();
        }

        /**
         * Returns the open angle of the arrow head.
         * 
         * @return the open angle of the arrow head
         */
        public float getHeadOpenAngle() {
            return this.getArrowGroup().getHeadOpenAngle();
        }

        /**
         * Sets the open and close angle of the arrow head.
         * 
         * @param openAngle
         *           a value to set to the open angle
         * @param closeAngle
         *           a value to set to the close angle
         * @return true if succeeded
         */
        public boolean setHeadAngle(final Float openAngle, final Float closeAngle) {
            // do nothig
            return true;
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.mGroup = null;
        }

        @Override
        protected SGDrawingElementLine createBodyInstance() {
            return new ArrowBody(this);
        }

        /**
         * Creates and returns an instance of the head.
         * 
         * @param arrow
         *            an arrow that this head belongs to
         * @param start
         *            true for the arrow head at start
         * @return an instance of the head
         */
        protected SGDrawingElementSymbol createHeadInstance(
                SGDrawingElementArrow arrow, final boolean start) {
            return new ArrowHead(arrow, start);
        }

        @Override
        public SGStroke getStroke() {
            return this.getArrowGroup().getStroke();
        }

        @Override
        public boolean setLineType(int type) {
            // do nothing
            return true;
        }

        @Override
        public boolean setLineWidth(float width) {
            // do nothing
            return true;
        }
        
        /**
         * Sets the start head type.
         * 
         * @param type
         *           the start head type
         * @return true if succeeded
         */
        public boolean setStartHeadType(final int type) {
            // do nothing
            return true;
        }
        
        /**
         * Sets the end head type.
         * 
         * @param type
         *           the end head type
         * @return true if succeeded
         */
        public boolean setEndHeadType(final int type) {
            // do nothing
            return true;
        }
        
        /**
         * Sets the head size.
         * 
         * @param size
         *          the head size to set
         * @return true if succeeded
         */
        public boolean setHeadSize(final float size) {
            // do nothing
            return true;
        }

        @Override
        public float getMagnification() {
            return this.mGroup.getMagnification();
        }

        @Override
        public boolean setMagnification(float mag) {
            // do nothing
            return true;
        }

        @Override
        public Color getColor() {
            return this.getArrowGroup().getColor();
        }

        @Override
        public boolean setColor(Color cl) {
            // do nothing
            return true;
        }

        @Override
        public SGTuple2f getEnd() {
            return this.getArrowGroup().getEndLocation(this.mIndex);
        }

        @Override
        public SGTuple2f getStart() {
            return this.getArrowGroup().getStartLocation(this.mIndex);
        }

        @Override
        public boolean setLocation(SGTuple2f start, SGTuple2f end) {
            // do nothing
            return true;
        }

        @Override
        public boolean setEndX(float x) {
            // do nothing
            return true;
        }

        @Override
        public boolean setEndY(float y) {
            // do nothing
            return true;
        }

        @Override
        public boolean setStartX(float x) {
            // do nothing
            return true;
        }

        @Override
        public boolean setStartY(float y) {
            // do nothing
            return true;
        }

		@Override
		protected Shape getStartHeadShape() {
			return this.getArrowGroup().getStartHeadShape();
		}

		@Override
		protected Shape getEndHeadShape() {
			return this.getArrowGroup().getEndHeadShape();
		}
		
	    /**
	     * Updates the head shape.
	     *
	     */
	    protected void updateHeadShape() {
	    	// do nothing
	    }
	    
	    public int getIndex() {
	    	return this.mIndex;
	    }
    }

    /**
     * Returns whether this color map is under the grid mode.
     * 
     * @return true if this color map is under the grid mode
     */
    public boolean isGridMode() {
    	return this.mGridMode;
    }

    /**
     * Sets the grid mode.
     * 
     * @param b
     *          true to set the grid mode
     * @return true if succeeded
     */
    public boolean setGridMode(final boolean b) {
    	this.mGridMode = b;
    	return true;
    }
}

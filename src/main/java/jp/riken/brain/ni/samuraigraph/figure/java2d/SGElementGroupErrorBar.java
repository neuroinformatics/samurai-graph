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
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementErrorBar;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementSymbol;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGIArrowConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIErrorBarConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGILineConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISymbolConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGDrawingElementArrow2D.ArrowHead;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupArrow.ArrowInGroup;

import org.w3c.dom.Element;

/**
 * A group of error bars.
 * 
 */
public abstract class SGElementGroupErrorBar extends SGElementGroup
        implements SGILineConstants, SGISymbolConstants, SGIErrorBarConstants,
        SGIArrowConstants {

    /**
     * The line stroke.
     */
    protected SGStroke mStroke = new SGStroke();

    /**
     * The head size.
     */
    protected float mHeadSize;

    /**
     * The head type.
     */
    protected int mHeadType;

    /**
     * The error bar style.
     */
    protected int mErrorBarStyle;

    /**
     * The color.
     */
    protected Color mColor;

    /**
     * The shape of the head.
     */
    protected Shape mHeadShape;

	/**
	 * A flag whether error bars are vertical.
	 */
	protected boolean mVerticalFlag;
	
	/**
	 * A flag whether error bars positioned at line positions.
	 * If this flag is false, this error bars positioned at bar positions.
	 */
	protected boolean mPositionOnLine;

    /**
     * The default constructor.
     */
    public SGElementGroupErrorBar() {
        super();
    }

    /**
     * Creates and returns an instance of the error bar.
     * 
     * @return an instance of the error bar
     */
    protected SGDrawingElement createDrawingElementInstance(final int index) {
        return new ErrorBarInGroup(this, index);
    }

//    /**
//     * 
//     * @return
//     */
//    protected boolean initDrawingElement(
//            final SGTuple2f[] centerArray, 
//            final SGTuple2f[] lowerArray,
//            final SGTuple2f[] upperArray) {
//        final int num = centerArray.length;
//        if (this.initDrawingElement(num) == false) {
//            return false;
//        }
//
//        SGDrawingElement[] sArray = this.mDrawingElementArray;
//        for (int ii = 0; ii < num; ii++) {
//            ((SGDrawingElementErrorBar) sArray[ii]).setLocation(
//                    centerArray[ii], lowerArray[ii], upperArray[ii]);
//        }
//        return true;
//    }

    /**
     * Set the location of points.
     * 
     */
    public boolean setLocation(
            SGTuple2f[] centerArray, SGTuple2f[] lowerArray, SGTuple2f[] upperArray) {
        if (centerArray == null || lowerArray == null || upperArray == null) {
            throw new IllegalArgumentException(
                    "centerArray == null || lowerArray==null || upperArray==null");
        }
        if (centerArray.length != lowerArray.length) {
            throw new IllegalArgumentException(
                    "centerArray.length != lowerArray.length");
        }
        if (centerArray.length != upperArray.length) {
            throw new IllegalArgumentException(
                    "centerArray.length != upperArray.length");
        }
        if (centerArray.length != this.mDrawingElementArray.length) {
//            throw new IllegalArgumentException(
//                    "centerArray.length != this.mDrawingElementArray.length");
        	this.initDrawingElement(centerArray.length);
        }

        SGDrawingElement[] array = this.mDrawingElementArray;
        if (array == null) {
            return false;
        }

        for (int ii = 0; ii < array.length; ii++) {
//            final boolean eff = !(startArray[ii].isInfinite()
//                    || startArray[ii].isNaN() || endArray[ii].isInfinite() || endArray[ii]
//                    .isNaN());
//            array[ii].setVisible(eff);
//            if (eff) {
//                ((SGDrawingElementErrorBar) array[ii]).setTermPoints(
//                        startArray[ii], endArray[ii]);
//            }
            ((SGDrawingElementErrorBar) array[ii]).setLocation(
                    centerArray[ii], lowerArray[ii], upperArray[ii]);
        }

        return true;

    }
    
    public boolean setMagnification(final float ratio) {
        if (super.setMagnification(ratio) == false) {
            return false;
        }
        this.mStroke.setMagnification(ratio);
        this.updateHeadShape();
        return true;
    }
    
    protected SGStroke getStroke() {
        return this.mStroke;
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
     * Sets the line width.
     * 
     * @param lw
     *            line width to set
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
     * Sets the line width in a given unit.
     * 
     * @param lw
     *           the line width to set
     * @param unit
     *           the unit for given line width
     * @return true if succeeded
     */
    public abstract boolean setLineWidth(final float lw, final String unit);

    /**
     * 
     * @return
     */
    public float getHeadSize() {
        return this.mHeadSize;
    }

    /**
     * 
     * @return
     */
    public float getHeadSize(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getHeadSize(), unit);
    }

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
     * Sets the head size in a given unit.
     * 
     * @param size
     *          the head size to set
     * @param unit
     *          the unit for given head size
     * @return true if succeeded
     */
    public abstract boolean setHeadSize(final float size, final String unit);

    /**
     * Returns the error bar style.
     * 
     * @return the error bar style
     */
    public int getErrorBarStyle() {
        return this.mErrorBarStyle;
    }

    /**
     * Sets the error bar style
     * 
     * @param style
     *           an error bar style to set
     * @return true if succeeded
     */
    public boolean setErrorBarStyle(final int style) {
        if (SGDrawingElementErrorBar.isValidErrorBarStyle(style) == false) {
            return false;
        }
        this.mErrorBarStyle = style;
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
    
    public boolean isPositionOnLine() {
        return this.mPositionOnLine;
    }
    
    public boolean setPositionOnLine(final boolean b) {
        this.mPositionOnLine = b;
        return true;
    }

    /**
     * Retruns the location of the start point at a given index.
     * 
     * @param index
     *           the index
     * @return the location of the start point at a given index
     */
    public abstract SGTuple2f getStartLocation(final int index);

    /**
     * Retruns the location of the upper end point at a given index.
     * 
     * @param index
     *           the index
     * @return the location of the upper end point at a given index
     */
    public abstract SGTuple2f getUpperEndLocation(final int index);

    /**
     * Retruns the location of the lower end point at a given index.
     * 
     * @param index
     *           the index
     * @return the location of the lower end point at a given index
     */
    public abstract SGTuple2f getLowerEndLocation(final int index);

    /**
     * 
     * @return
     */
    public String getTagName() {
        return TAG_NAME_ERROR_BAR;
    }

    /**
     * 
     */
    public boolean writeProperty(final Element el) {
	
        final int digitSymbolSize = ERROR_BAR_HEAD_SIZE_MINIMAL_ORDER - 1;
        final int digitLineWidth = SGIConstants.LINE_WIDTH_MINIMAL_ORDER - 1;
	
        final float headSize = (float) SGUtilityNumber.roundOffNumber(
        	this.mHeadSize * SGIConstants.CM_POINT_RATIO, digitSymbolSize);
        final float lineWidth = (float) SGUtilityNumber.roundOffNumber(
        	this.getLineWidth(), digitLineWidth);
        List<Color> cList = new ArrayList<Color>();
        cList.add(this.getColor());

        el.setAttribute(KEY_ERROR_BAR_STYLE,
        	SGDrawingElementErrorBar.getErrorBarStyleName(this.mErrorBarStyle));
        el.setAttribute(KEY_HEAD_SIZE,
        	Float.toString(headSize) + SGIConstants.cm);
        el.setAttribute(KEY_LINE_WIDTH,
        	Float.toString(lineWidth) + SGIConstants.pt);
        el.setAttribute(KEY_ERROR_BAR_HEAD_TYPE,
        	SGDrawingElementErrorBar.getHeadTypeName(this.getHeadType()));
        el.setAttribute(KEY_COLOR, SGUtilityText.getColorListString(cList));
        el.setAttribute(KEY_ERROR_BAR_VERTICAL, Boolean
				.toString(this.mVerticalFlag));
        el.setAttribute(KEY_ERROR_BAR_POSITION,
				getErrorBarPositionName(this.mPositionOnLine ? ERROR_BAR_ON_LINE
						: ERROR_BAR_ON_BAR));
        
        return true;
    }

    /**
     * Returns the name of a given error bar position.
     * 
     * @param position
     *          the error bar position
     * @return the name of a given error bar position
     */
    public static String getErrorBarPositionName(final int position) {
        String name = null;
        switch (position) {
        case ERROR_BAR_ON_LINE:
            name = ERROR_BAR_POSITION_NAME_ON_LINE;
            break;
        case ERROR_BAR_ON_BAR:
            name = ERROR_BAR_POSITION_NAME_ON_BAR;
            break;
        default:
        }
        return name;
    }

    /**
     * 
     * @param el
     * @return
     */
    public boolean readProperty(final Element el) {
        String str = null;
        Number num = null;
        // Color cl = null;
        Boolean b = null;
        List<?> list = null;

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
            final Color color = (Color) list.get(0);
            if (this.setColor(color) == false) {
                return false;
            }
        }

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

        } else {
            // to maintain downward compatibility with old versions before 0.6.1
            str = el.getAttribute("LineWidth");
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

        // style of error bars
        str = el.getAttribute(KEY_ERROR_BAR_STYLE);
        if (str.length() != 0) {
            num = SGDrawingElementErrorBar.getErrorBarStyleFromName(str);
            if (num == null) {
                return false;
            }
            final int style = num.intValue();
            if (this.setErrorBarStyle(style) == false) {
                return false;
            }
        }

        // head type
        str = el.getAttribute(KEY_ERROR_BAR_HEAD_TYPE);
        if (str.length() != 0) {
            final Integer type = SGDrawingElementErrorBar.getHeadTypeFromName(str);
            if (type == null) {
                return false;
            }
            final int headType = type.intValue();
            if (this.setHeadType(headType) == false) {
                return false;
            }
        }

        // vertical
        str = el.getAttribute(KEY_ERROR_BAR_VERTICAL);
        if (str.length() != 0) {
        	b = SGUtilityText.getBoolean(str);
        	if (b == null) {
        		return false;
        	}
        	if (this.setVertical(b.booleanValue()) == false) {
        		return false;
        	}
        }
        
        // position
        str = el.getAttribute(KEY_ERROR_BAR_POSITION);
        if (str.length() != 0) {
            num = getErrorBarPositionFromName(str);
            if (num == null) {
                return false;
            }
            final int position = num.intValue();
            if (isValidErrorBarPosition(position)==false) {
                return false;
            }
            if (this.setPositionOnLine(position==ERROR_BAR_ON_LINE)==false) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether a given error bar position is valid.
     * 
     * @param position
     *            an error bar position
     * @return true if the given error bar position is valid
     */
    public static boolean isValidErrorBarPosition(final int position) {
        final int[] array = {
                ERROR_BAR_ON_LINE,
                ERROR_BAR_ON_BAR
        };
        for (int ii = 0; ii < array.length; ii++) {
            if (position == array[ii]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the error bar position constant from a given name.
     * 
     * @param name
     *            the name of error bar position
     * @return the error bar position constant if it exists or null otherwise
     */
    public static Integer getErrorBarPositionFromName(final String name) {
        if (name == null) {
            return null;
        }
        int style;
        if (SGUtilityText.isEqualString(ERROR_BAR_POSITION_NAME_ON_LINE, name)) {
            style = ERROR_BAR_ON_LINE;
        } else if (SGUtilityText.isEqualString(ERROR_BAR_POSITION_NAME_ON_BAR, name)) {
            style = ERROR_BAR_ON_BAR;
        } else {
            return null;
        }
        return Integer.valueOf(style);
    }

    /**
     * Returns the head type.
     * 
     * @return the head type
     */
    public int getHeadType() {
        return this.mHeadType;
    }

    /**
     * Sets the head type.
     * 
     * @param type
     *           the head type to set
     * @return true if succeeded
     */
    public boolean setHeadType(final int type) {
        if (SGDrawingElementErrorBar.isValidHeadType(type) == false) {
            return false;
        }
        this.mHeadType = type;
        this.updateHeadShape();
        return true;
    }

    /**
     * 
     */
    public boolean paintElement(final Graphics2D g2d, final Rectangle2D clipRect) {
        SGDrawingElement[] array = this.mDrawingElementArray;
        if (array != null) {
            for (int ii = 0; ii < array.length; ii++) {
                SGDrawingElementErrorBar2D el = (SGDrawingElementErrorBar2D) array[ii];
                if (el.isVisible() == false) {
                    continue;
                }
                el.paint(g2d, clipRect);
            }
        }
        return true;
    }

    /**
     * 
     */
    public SGProperties getProperties() {
        ErrorBarProperties p = new ErrorBarProperties();
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
        if ((p instanceof ErrorBarProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        ErrorBarProperties ep = (ErrorBarProperties) p;

        ep.setLineWidth(this.getLineWidth());
        ep.setHeadSize(this.getHeadSize());
        ep.setHeadType(this.getHeadType());
        ep.setErrorBarStyle(this.getErrorBarStyle());
        ep.setColor(this.getColor());
        ep.setVertical(this.isVertical());
        ep.mPositionOnLine = this.isPositionOnLine();

        return true;
    }

    /**
     * 
     */
    public boolean setProperties(SGProperties p) {
        if ((p instanceof ErrorBarProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }

        ErrorBarProperties ep = (ErrorBarProperties) p;

        this.setLineWidth(ep.getLineWidth().floatValue());
        this.setHeadSize(ep.getHeadSize().floatValue());
        this.setHeadType(ep.getHeadType().intValue());
        this.setColor(ep.getColor());
        this.setVertical(ep.isVertical());

        Integer style = ep.getErrorBarStyle();
        if (style == null) {
            return false;
        }
        this.setErrorBarStyle(style.intValue());
        this.setPositionOnLine(ep.mPositionOnLine);

        return true;
    }

//    /**
//     * 
//     * @return
//     */
//    public boolean setPropertiesOfDrawingElements() {
////        SGDrawingElement[] array = this.mDrawingElementArray;
////        final float mag = this.getMagnification();
////        final ArrayList cList = this.getColorList();
////        final float lineWidth = this.getLineWidth();
////        final float headSize = this.getHeadSize();
////        final int headType = this.getHeadType();
////        final int style = this.getErrorBarStyle();
//
////        for (int ii = 0; ii < array.length; ii++) {
////            SGDrawingElementErrorBar bar = (SGDrawingElementErrorBar) array[ii];
//////            bar.setErrorBarStyle(style);
////            bar.setMagnification(mag);
//////            bar.setColorList(cList);
//////            bar.setLineWidth(lineWidth);
//////            bar.setHeadSize(headSize);
//////            bar.setHeadType(headType);
////        }
//        return true;
//    }

//    /**
//     * 
//     */
//    public boolean setProperty(final SGDrawingElement element) {
//        if (!(element instanceof SGDrawingElementErrorBar)) {
//            return false;
//        }
//
////        if (super.setProperty(element) == false) {
////            return false;
////        }
//
//        SGDrawingElementErrorBar bar = (SGDrawingElementErrorBar) element;
//        this.setLineWidth(bar.getLineWidth());
//        this.setHeadSize(bar.getHeadSize());
//        this.setLineWidth(bar.getLineWidth());
//        this.setHeadType(bar.getHeadType());
//        this.setErrorBarStyle(bar.getErrorBarStyle());
//
//        return true;
//    }

    /**
     * @author okumura
     */
    public static class ErrorBarProperties extends ElementGroupProperties {

        private SGDrawingElementErrorBar.ErrorBarProperties mErrorBarProperties = new SGDrawingElementErrorBar.ErrorBarProperties();

        private boolean mPositionOnLine = true;
        
        /**
         * 
         * 
         */
        public ErrorBarProperties() {
            super();
        }

        /**
         * Copy this object.
         * @return a copied object
         */
        public Object copy() {
            Object obj = super.copy();
            ErrorBarProperties p = (ErrorBarProperties) obj;
            p.mErrorBarProperties = (SGDrawingElementErrorBar.ErrorBarProperties) this.mErrorBarProperties.copy();
            p.mPositionOnLine = this.mPositionOnLine;
            return p;
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.mErrorBarProperties.dispose();
        }

        /*
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof ErrorBarProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            ErrorBarProperties p = (ErrorBarProperties) obj;
            if (this.mErrorBarProperties.equals(p.mErrorBarProperties) == false) {
                return false;
            }
            
            if (this.mPositionOnLine!=p.mPositionOnLine) {
                return false;
            }
            return true;
        }

        public Float getLineWidth() {
            return this.mErrorBarProperties.getLineWidth();
        }

        public Float getHeadSize() {
            return this.mErrorBarProperties.getHeadSize();
        }

        public Integer getHeadType() {
            return this.mErrorBarProperties.getHeadType();
        }
        
        public Color getColor() {
            return this.mErrorBarProperties.getColor();
        }
        
        public Boolean isVertical() {
        	return this.mErrorBarProperties.isVertical();
        }

        public boolean setColor(final Color cl) {
            return this.mErrorBarProperties.setColor(cl);
        }

        public void setLineWidth(final float width) {
            this.mErrorBarProperties.setLineWidth(width);
        }

        public void setHeadSize(final float size) {
            this.mErrorBarProperties.setHeadSize(size);
        }

        public void setHeadType(final int type) {
            this.mErrorBarProperties.setHeadType(type);
        }

        public Integer getErrorBarStyle() {
            return this.mErrorBarProperties.getErrorBarStyle();
        }

        public void setErrorBarStyle(final int style) {
            this.mErrorBarProperties.setErrorBarStyle(style);
        }

        public void setVertical(final boolean b) {
        	this.mErrorBarProperties.setVertical(b);
        }
    }

    /**
     * Update the location of error bars.
     * @return true if succeeded
     */
    public abstract boolean updateLocation();

    
    /**
     * Error bar in a group of error bars.
     *
     */
    protected static class ErrorBarInGroup extends SGDrawingElementErrorBar2D {

        // a group of error bars
        protected SGElementGroupErrorBar mGroup = null;
        
        /**
         * The array index.
         */
        protected int mIndex = -1;

        /**
         * Builds this object in a given group.
         * 
         * @param group
         *           a group of arrows
         * @param index
         *           the array index
         */
        public ErrorBarInGroup(final SGElementGroupErrorBar group, final int index) {
            super();
            this.mGroup = group;
            
            // create instances for both sides
            this.mLowerArrowElement = this.createArrowInstance(index);
            this.mUpperArrowElement = this.createArrowInstance(index);
            
            // set the start head type for both arrows
            this.mLowerArrowElement.setStartHeadType(SGIErrorBarConstants.SYMBOL_TYPE_VOID);
            this.mUpperArrowElement.setStartHeadType(SGIErrorBarConstants.SYMBOL_TYPE_VOID);
            
            this.mIndex = index;
        }
        
        public int getIndex() {
        	return this.mIndex;
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.mGroup = null;
        }

        /**
         * Returns the line width.
         * 
         * @return the line width
         */
        public float getLineWidth() {
            return this.mGroup.getLineWidth();
        }

        /**
         * Returns the head size.
         * 
         * @return the head size
         */
        public float getHeadSize() {
            return this.mGroup.getHeadSize();
        }
        
        /**
         * Returns the head type.
         * 
         * @return the start head type
         */
        public int getHeadType() {
            return this.mGroup.getHeadType();
        }

        /**
         * Returns the error bar style.
         * 
         * @return the error bar style
         */
        public int getErrorBarStyle() {
            return this.mGroup.getErrorBarStyle();
        }

        @Override
        public boolean setLineWidth(float width) {
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

        /**
         * Sets the head type.
         * 
         * @param type
         *           the head type to set
         * @return true if succeeded
         */
        public boolean setHeadType(int type) {
            // do nothing
            return true;
        }
        
        /**
         * Sets the style of the error bar.
         * 
         * @param style
         *           the style
         * @return true if succeeded
         */
        public boolean setErrorBarStyle(final int style) {
            // do nothing
            return true;
        }

        /**
         * Create an instance of an arrow.
         * 
         * @param index
         *           the array index
         */
        protected SGDrawingElementArrow createArrowInstance(final int index) {
            SGDrawingElementArrow arrow = new ErrorBarArrowInGroup(this.mGroup, index);
            arrow.setLineType(SGILineConstants.LINE_TYPE_SOLID);
            return arrow;
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
            return this.mGroup.getColor();
        }

        @Override
        public boolean setColor(Color cl) {
            // do nothing
            return true;
        }
    }

    /**
     * Arrow for the body of an error bar.
     *
     */
    protected static class ErrorBarArrowInGroup extends ArrowInGroup {
        
        // returns a group of error bars
        private SGElementGroupErrorBar getErrorBarGroup() {
            return (SGElementGroupErrorBar) this.mGroup;
        }

        private SGTuple2f mEndPoint = new SGTuple2f();
        
        /**
         * 
         */
        public boolean setLocation(final SGTuple2f start, final SGTuple2f end) {
            if (super.setLocation(start, end) == false) {
                return false;
            }
            this.mEndPoint.setValues(end);
            return true;
        }
        
        /**
         * Builds this object in a given group.
         * 
         * @param group
         *           a group of error bars
         * @param index
         *           the array index
         */
        public ErrorBarArrowInGroup(final SGElementGroupErrorBar group, final int index) {
            super(group, index);
            this.mGroup = group;
            this.mIndex = index;
            this.mLine = this.createBodyInstance();
            this.mStartHead = this.createHeadInstance(this, true);
            this.mEndHead = this.createHeadInstance(this, false);
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
            return new ErrorBarHeadInGroup(this, start);
        }

        /**
         * Sets the head type.
         * 
         * @param type
         *           the head type to set
         * @return true if succeeded
         */
        public boolean setHeadType(int type) {
            this.setEndHeadType(type);
            return true;
        }

        /**
         * Returns the line width.
         * 
         * @return the line width
         */
        public float getLineWidth() {
            return this.getErrorBarGroup().getLineWidth();
        }

        /**
         * Returns the line type.
         * 
         * @return the line type
         */
        public int getLineType() {
            // returns a constant
            return SGDrawingElementLine.LINE_TYPE_SOLID;
        }

        /**
         * Returns the head size.
         * 
         * @return the head size
         */
        public float getHeadSize() {
            return this.getErrorBarGroup().getHeadSize();
        }
        
        /**
         * Returns the start head type.
         * 
         * @return the start head type
         */
        public int getStartHeadType() {
            return SGDrawingElementErrorBar.SYMBOL_TYPE_VOID;
        }

        /**
         * Returns the end head type.
         * 
         * @return the end head type
         */
        public int getEndHeadType() {
            return this.getErrorBarGroup().getHeadType();
        }

        /**
         * Returns the close angle of the arrow head.
         * 
         * @return the close angle of the arrow head
         */
        public float getHeadCloseAngle() {
            // returns zero
            return 0.0f;
        }

        /**
         * Returns the open angle of the arrow head.
         * 
         * @return the open angle of the arrow head
         */
        public float getHeadOpenAngle() {
            // returns zero
            return 0.0f;
        }

        @Override
        public SGStroke getStroke() {
            return this.getErrorBarGroup().getStroke();
        }

        @Override
        public Color getColor() {
            return this.getErrorBarGroup().getColor();
        }

        @Override
        public SGTuple2f getEnd() {
            return this.mEndPoint;
        }

        @Override
        public SGTuple2f getStart() {
            return this.getErrorBarGroup().getStartLocation(this.mIndex);
        }

		@Override
		protected Shape getStartHeadShape() {
			return this.getErrorBarGroup().getStartHeadShape();
		}

		@Override
		protected Shape getEndHeadShape() {
			return this.getErrorBarGroup().getEndHeadShape();
		}
    }

    protected static class ErrorBarHeadInGroup extends ArrowHead {

        /**
         * Builds a object in a given group of arrows.
         * 
         * @param arrow
         *           an arrow that this arrow head belongs to
         * @param start
         *           true for the start head
         * @param group
         *           a group of error bars
         */
        public ErrorBarHeadInGroup(final SGDrawingElementArrow arrow, 
                final boolean start) {
            super(arrow, start);
        }

        @Override
        public int getType() {
            if (this.mStartFlag) {
                return SGDrawingElementErrorBar.SYMBOL_TYPE_VOID;
            } else {
                return this.mArrow.getEndHeadType();
            }
        }

    }
    
    /**
     * Returns the shape of the start head.
     * 
     * @return a shape object
     */
    public Shape getStartHeadShape() {
    	// always returns null
    	return null;
    }
    
    /**
     * Returns the shape of the end head.
     * 
     * @return a shape object
     */
    public Shape getEndHeadShape() {
    	return this.mHeadShape;
    }

    /**
     * Updates the head shape.
     *
     */
    protected void updateHeadShape() {
    	final int type = this.getHeadType();
		final float size = this.getMagnification() * this.getHeadSize();
		this.mHeadShape = SGDrawingElementArrow2D.createHeadShape(type,
				size, 0.0f, 0.0f);
    }

    /**
	 * Returns whether the error bars are vertical.
	 * 
	 * @return true if error bars are vertical, false if they are horizontal
	 */
    public boolean isVertical() {
		return this.mVerticalFlag;
    }

    /**
     * Sets the direction of error bars.
     * 
     * @param b
     *          true for vertical direction
     * @return true if succeeded
     */
    public boolean setVertical(final boolean b) {
    	this.mVerticalFlag = b;
    	return true;
    }
}

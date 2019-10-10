package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

import org.w3c.dom.Element;

/**
 * Drawing element of arrow.
 */
public abstract class SGDrawingElementArrow extends SGDrawingElement implements
        SGIArrowConstants, SGIDrawingElementConstants {

    /**
     * A line object.
     */
    protected SGDrawingElementLine mLine;

    /**
     * A symbol object for the start head.
     */
    protected SGDrawingElementSymbol mStartHead;

    /**
     * A symbol object for the end head.
     */
    protected SGDrawingElementSymbol mEndHead;

    /**
     * Default constructor.
     */
    public SGDrawingElementArrow() {
        super();
    }

    /**
     * Creates and returns an instance of the body.
     * 
     * @return an instance of the body
     */
    protected abstract SGDrawingElementLine createBodyInstance();

    /**
     * Creates and returns an instance of the head.
     * 
     * @param arrow
     *            an arrow that this head belongs to
     * @param start
     *            true for the arrow head at start
     * @return an instance of the head
     */
    protected abstract SGDrawingElementSymbol createHeadInstance(
            SGDrawingElementArrow arrow, final boolean start);

    /**
     * Returns the line of this arrow.
     * 
     * @return the line of this arrow 
     */
    protected SGDrawingElementLine getLine() {
        return this.mLine;
    }

    /**
     * Returns the symbol of the start.
     * 
     * @return the symbol of the start
     */
    protected SGDrawingElementSymbol getStartHead() {
        return this.mStartHead;
    }

    /**
     * Returns the symbol of the end.
     * 
     * @return the symbol of the end
     */
    protected SGDrawingElementSymbol getEndHead() {
        return this.mEndHead;
    }

    /**
     * Dispose this object.
     */
    public void dispose() {
        super.dispose();
        this.mLine.dispose();
        this.mStartHead.dispose();
        this.mEndHead.dispose();
        this.mLine = null;
        this.mStartHead = null;
        this.mEndHead = null;
    }

    @Override
    public boolean contains(final int x, final int y) {
        final boolean lineFlag = this.getLine().contains(x, y);
        if (lineFlag) {
            return true;
        }
        SGDrawingElementSymbol startHead = this.getStartHead();
        if (this.isVisibleHead(startHead)) {
            if (startHead.contains(x, y)) {
                return true;
            }
        }
        SGDrawingElementSymbol endHead = this.getEndHead();
        if (this.isVisibleHead(endHead)) {
            if (endHead.contains(x, y)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isVisibleHead(SGDrawingElementSymbol head) {
    	if (!head.isVisible()) {
    		return false;
    	}
    	final int type = head.getType();
    	if (type == -1) {
    		return false;
    	}
    	return true;
    }

    /**
     * Returns the color.
     * 
     * @return the color
     */
    public abstract Color getColor();

    /**
     * Returns the line stroke.
     * 
     * @return the line stroke
     */
    public abstract SGStroke getStroke();

    /**
     * Returns the angle of this arrow.
     * 
     * @return the angle
     */
    public abstract float getGradient();
    
    /**
     * Sets the color.
     * 
     * @param cl
     *          the color to set
     * @return true if succeeded
     */
    public abstract boolean setColor(final Color cl);

    /**
     * Sets the coordinate of the start point.
     * 
     * @param start
     *           the coordinate set to the start point
     * @return true if succeeded
     */
    public boolean setStart(SGTuple2f start) {
        return this.setLocation(start, this.getEnd());
    }

    /**
     * Sets the x-coordinate of the start point.
     * 
     * @param x
     *           the x-coordinate set to the start point
     * @return true if succeeded
     */
    public abstract boolean setStartX(final float x);

    /**
     * Sets the y-coordinate of the start point.
     * 
     * @param y
     *           the y-coordinate set to the start point
     * @return true if succeeded
     */
    public abstract boolean setStartY(final float y);

    /**
     * Sets the coordinate of the end point.
     * 
     * @param end
     *           the coordinate set to the end point
     * @return true if succeeded
     */
    public boolean setEnd(SGTuple2f end) {
        return this.setLocation(this.getStart(), end);
    }

    /**
     * Sets the x-coordinate of the end point.
     * 
     * @param x
     *           the x-coordinate set to the end point
     * @return true if succeeded
     */
    public abstract boolean setEndX(final float x);
    
    /**
     * Sets the y-coordinate of the end point.
     * 
     * @param y
     *           the y-coordinate set to the end point
     * @return true if succeeded
     */
    public abstract boolean setEndY(final float y);
    
    /**
     * Sets the coordinates of the end points.
     * 
     * @param start
     *           the coordinate set to the start point
     * @param end
     *           the coordinate set to the end point
     * @return true if succeeded
     */
    public abstract boolean setLocation(final SGTuple2f start, final SGTuple2f end);
    
    /**
     * Sets the coordinates of the end points.
     * 
     * @param x1
     *           the x-coordinate set to the start point
     * @param y1
     *           the y-coordinate set to the start point
     * @param x2
     *           the x-coordinate set to the end point
     * @param y2
     *           the y-coordinate set to the end point
     * @return true if succeeded
     */
    public boolean setTermPoints(final float x1, final float y1,
            final float x2, final float y2) {
        return this.setLocation(new SGTuple2f(x1, y1), new SGTuple2f(x2, y2));
    }

    /**
     * Returns the x-coordinate of the start point.
     * 
     * @return the x-coordinate of the start point
     */
    public float getStartX() {
        return this.getStart().x;
    }

    /**
     * Returns the y-coordinate of the start point.
     * 
     * @return the y-coordinate of the start point
     */
    public float getStartY() {
        return this.getStart().y;
    }

    /**
     * Returns the x-coordinate of the end point.
     * 
     * @return the x-coordinate of the end point
     */
    public float getEndX() {
        return this.getEnd().x;
    }

    /**
     * Returns the y-coordinate of the end point.
     * 
     * @return the y-coordinate of the end point
     */
    public float getEndY() {
        return this.getEnd().y;
    }

    /**
     * Returns the start point.
     * 
     * @return the start point
     */
    public abstract SGTuple2f getStart();

    /**
     * Returns the end point.
     * 
     * @return the end point
     */
    public abstract SGTuple2f getEnd();

    /**
     * Sets the line width
     * 
     * @param width
     *           line width to set
     * @return true if succeeded
     */
    public abstract boolean setLineWidth(final float width);
    
    /**
     * Set the line width in a given unit.
     * 
     * @param lw
     *            the line width to set
     * @param unit
     *            the unit for the given line width
     * @return true if succeeded
     */
    public boolean setLineWidth(final float lw, final String unit) {
        final Float lwNew = SGUtility.getLineWidth(lw, unit);
        if (lwNew == null) {
            return false;
        }
        if (this.setLineWidth(lwNew) == false) {
            return false;
        }
        return true;
    }

    /**
     * Sets the line type.
     * 
     * @param type
     *           the line type to set
     * @return true if succeeded
     */
    public abstract boolean setLineType(final int type);
    
    /**
     * Sets the head size.
     * 
     * @param size
     *          the head size to set
     * @return true if succeeded
     */
    public abstract boolean setHeadSize(final float size);

    /**
     * Sets the size in a given unit.
     * 
     * @param size
     *           the symbol size to set
     * @param unit
     *           the unit for given symbol size
     * @return true if succeeded
     */
    public boolean setHeadSize(final float size, final String unit) {
        final Float sNew = SGUtility.calcPropertyValue(size, unit, 
        		ARROW_HEAD_SIZE_UNIT, ARROW_HEAD_SIZE_MIN, 
        		ARROW_HEAD_SIZE_MAX, ARROW_HEAD_SIZE_MINIMAL_ORDER);
        if (sNew == null) {
            return false;
        }
        if (this.setHeadSize(sNew) == false) {
            return false;
        }
        return true;
    }
    
    /**
     * Sets the start head type.
     * 
     * @param type
     *           the start head type
     * @return true if succeeded
     */
    public abstract boolean setStartHeadType(final int type);
    
    /**
     * Sets the end head type.
     * 
     * @param type
     *           the end head type
     * @return true if succeeded
     */
    public abstract boolean setEndHeadType(final int type);
    
    /**
     * Returns the line width.
     * 
     * @return the line width
     */
    public abstract float getLineWidth();
    
    /**
     * Returns the line type.
     * 
     * @return the line type
     */
    public abstract int getLineType();

    /**
     * Returns the head size.
     * 
     * @return the head size
     */
    public abstract float getHeadSize();
    
    /**
     * Sets the open and close angle of the arrow head.
     * 
     * @param openAngle
     *           a value to set to the open angle
     * @param closeAngle
     *           a value to set to the close angle
     * @return true if succeeded
     */
    public abstract boolean setHeadAngle(final Float openAngle, final Float closeAngle);

    /**
     * Returns the open angle of the arrow head.
     * 
     * @return the open angle of the arrow head
     */
    public abstract float getHeadOpenAngle();

    /**
     * Returns the close angle of the arrow head.
     * 
     * @return the close angle of the arrow head
     */
    public abstract float getHeadCloseAngle();

    /**
     * Returns the start head type.
     * 
     * @return the start head type
     */
    public abstract int getStartHeadType();

    /**
     * Returns the end head type.
     * 
     * @return the end head type
     */
    public abstract int getEndHeadType();

    public float getLineWidth(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getLineWidth(),
                unit);
    }

    public float getHeadSize(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getHeadSize(),
                unit);
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
        ap.setHeadOpenAngle(this.getHeadOpenAngle());
        ap.setHeadCloseAngle(this.getHeadCloseAngle());
        ap.setStartHeadType(this.getStartHeadType());
        ap.setEndHeadType(this.getEndHeadType());
        ap.setColor(this.getColor());
        return true;
    }

    /**
     * Sets the properties of arrow object.
     * 
     * @param p
     *          properties of an arrow
     * @return true if succeeded
     */
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof ArrowProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
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

        final Float openAngle = ap.getHeadOpenAngle();
        if (openAngle == null) {
            return false;
        }
        final Float closeAngle = ap.getHeadCloseAngle();
        if (closeAngle == null) {
            return false;
        }
        this.setHeadAngle(openAngle.floatValue(), closeAngle.floatValue());

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
        
        final Color cl = ap.getColor();
        if (cl == null) {
            return false;
        }
        this.setColor(cl);

        return true;
    }

    /**
     * Returns whether a given head type is valid.
     * 
     * @param type
     *           a head type
     * @return true if the given head type is valid
     */
    public static boolean isValidArrowHeadType(final int type) {
        if (SGDrawingElementSymbol.isValidSymbolType(type)) {
            return true;
        }
        final int[] array = { SYMBOL_TYPE_VOID, SYMBOL_TYPE_TRANSVERSELINE,
                SYMBOL_TYPE_ARROW_HEAD, SYMBOL_TYPE_ARROW };
        for (int ii = 0; ii < array.length; ii++) {
            if (type == array[ii]) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns whether a given symbol type is of the line type.
     * 
     * @param type
     *            a symbol type
     * @return true if the given symbol type is of the line type
     */
    public static boolean isLineTypeSymbol(final int type) {
    	if (SGDrawingElementSymbol.isLineTypeSymbol(type)) {
    		return true;
    	}
        return (type == SYMBOL_TYPE_TRANSVERSELINE || type == SYMBOL_TYPE_ARROW);
    }


    /**
     * Returns the arrow head type constant from a given name.
     * 
     * @param name
     *           the name of an arrow head type
     * @return the arrow head type constant if it exists or null otherwise
     */
    public static Integer getArrowHeadTypeFromName(final String name) {
        Integer type = SGDrawingElementSymbol.getSymbolTypeFromName(name);
        if (type != null) {
            return type;
        } else {
            if (SGUtilityText.isEqualString(SYMBOL_NAME_VOID, name)) {
                type = SYMBOL_TYPE_VOID;
            } else if (SGUtilityText.isEqualString(SYMBOL_NAME_TRANSVERSE_LINE, name)) {
                type = SYMBOL_TYPE_TRANSVERSELINE;
            } else if (SGUtilityText.isEqualString(SYMBOL_NAME_ARROW_HEAD, name)) {
                type = SYMBOL_TYPE_ARROW_HEAD;
            } else if (SGUtilityText.isEqualString(SYMBOL_NAME_ARROW, name)) {
                type = SYMBOL_TYPE_ARROW;
            }
        }
        return type;
    }

    /**
     * Returns the name of a given arrow head type.
     * 
     * @param type
     *          the arrow head type
     * @return the name of a given arrow head type
     */
    public static String getArrowHeadTypeName(final int type) {
        String name = SGDrawingElementSymbol.getSymbolTypeName(type);
        if (name == null) {
            switch (type) {
            case SYMBOL_TYPE_VOID:
                name = SYMBOL_NAME_VOID;
                break;
            case SYMBOL_TYPE_TRANSVERSELINE:
                name = SYMBOL_NAME_TRANSVERSE_LINE;
                break;
            case SYMBOL_TYPE_ARROW_HEAD:
                name = SYMBOL_NAME_ARROW_HEAD;
                break;
            case SYMBOL_TYPE_ARROW:
                name = SYMBOL_NAME_ARROW;
                break;
            default:
            }
        }
        return name;
    }

    public boolean writeProperty(final Element el, SGExportParameter params) {
    	SGPropertyMap map = this.getPropertyFileMap(params);
    	map.setToElement(el);
        return true;
    }
    
	public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	this.addProperties(map, KEY_LINE_WIDTH, KEY_LINE_TYPE, KEY_HEAD_SIZE, KEY_COLOR, 
    			KEY_START_HEAD_TYPE, KEY_END_HEAD_TYPE);
		return map;
	}

	protected void addProperties(SGPropertyMap map, String lineWidthKey, String lineTypeKey,
			String headSizeKey, String colorKey, String startTypeKey, String endTypeKey) {
        SGPropertyUtility.addProperty(map, lineWidthKey, 
        		SGUtility.getExportLineWidth(this.getLineWidth(LINE_WIDTH_UNIT)), 
        		LINE_WIDTH_UNIT);
        SGPropertyUtility.addProperty(map, lineTypeKey,
        		SGDrawingElementLine.getLineTypeName(this.getLineType()));
        SGPropertyUtility.addProperty(map, headSizeKey, 
        		SGUtility.getExportValue(this.getHeadSize(ARROW_HEAD_SIZE_UNIT), 
        				ARROW_HEAD_SIZE_MINIMAL_ORDER), ARROW_HEAD_SIZE_UNIT);
        SGPropertyUtility.addProperty(map, colorKey, this.getColor());
        SGPropertyUtility.addProperty(map, startTypeKey, 
        		SGDrawingElementArrow.getArrowHeadTypeName(this.getStartHeadType()));
        SGPropertyUtility.addProperty(map, endTypeKey, 
        		SGDrawingElementArrow.getArrowHeadTypeName(this.getEndHeadType()));
	}

    /**
     * Reads the properties from an element.
     * 
     * @param el
     *          an element
     * @return true if succeeded
     */
    public boolean readProperty(final Element el) {

        String str = null;
        Number num = null;

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

        // color
        str = el.getAttribute(KEY_COLOR);
        if (str.length() != 0) {
            Color cl = SGUtilityText.parseColorIncludingList(str);
            if (cl == null) {
            	return false;
            }
            if (this.setColor(cl) == false) {
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

        return true;
    }

    /**
     * @author okumura
     */
    public static class ArrowProperties extends DrawingElementProperties {

        private SGDrawingElementLine.LineProperties mLineProperties = new SGDrawingElementLine.LineProperties();

        private SGDrawingElementSymbol.SymbolProperties mSymbolProperties = new SGDrawingElementSymbol.SymbolProperties();

        private int mEndHeadType;

        private double mHeadOpenAngle;

        private double mHeadCloseAngle;
        
        private Color mColor = null;

        /**
         * 
         */
        public boolean equals(Object obj) {
            if ((obj instanceof ArrowProperties) == false)
                return false;
            if (super.equals(obj) == false)
                return false;

            ArrowProperties p = (ArrowProperties) obj;

            if (this.mLineProperties.equals(p.mLineProperties) == false) {
                return false;
            }
            if (this.mSymbolProperties.equals(p.mSymbolProperties) == false) {
                return false;
            }
            if (this.mEndHeadType != p.mEndHeadType) {
                return false;
            }
            if (this.mHeadOpenAngle != p.mHeadOpenAngle) {
                return false;
            }
            if (this.mHeadCloseAngle != p.mHeadCloseAngle) {
                return false;
            }
            if (SGUtility.equals(this.mColor, p.mColor) == false) {
                return false;
            }

            return true;
        }

        public Float getLineWidth() {
            return this.mLineProperties.getLineWidth();
        }

        public Integer getLineType() {
            return this.mLineProperties.getLineType();
        }

        public Float getHeadSize() {
            return this.mSymbolProperties.getSize();
        }

        public Integer getStartHeadType() {
            return this.mSymbolProperties.getSymbolType();
        }

        public Integer getEndHeadType() {
            return Integer.valueOf(this.mEndHeadType);
        }

        public Float getHeadOpenAngle() {
            return Float.valueOf((float) this.mHeadOpenAngle);
        }

        public Float getHeadCloseAngle() {
            return Float.valueOf((float) this.mHeadCloseAngle);
        }
        
        public Color getColor() {
            return this.mColor;
        }

        public boolean setLineWidth(final float width) {
            if (this.mLineProperties.setLineWidth(width) == false) {
                return false;
            }
            if (this.mSymbolProperties.setLineWidth(width) == false) {
                return false;
            }
            return true;
        }

        public boolean setLineType(final int type) {
            return this.mLineProperties.setLineType(type);
        }

        public boolean setStartHeadType(final int num) {
            return this.mSymbolProperties.setSymbolType(num);
        }

        public boolean setEndHeadType(final int num) {
            this.mEndHeadType = num;
            return true;
        }

        public boolean setHeadSize(final float size) {
            return this.mSymbolProperties.setSize(size);
        }

        public boolean setColor(final Color cl) {
            this.mColor = cl;
            return true;
        }

        public boolean setHeadOpenAngle(final float value) {
            this.mHeadOpenAngle = value;
            return true;
        }

        public boolean setHeadCloseAngle(final float value) {
            this.mHeadCloseAngle = value;
            return true;
        }

    }

}

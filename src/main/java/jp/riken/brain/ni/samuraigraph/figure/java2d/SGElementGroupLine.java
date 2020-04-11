package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGILineConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyle;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 */
public abstract class SGElementGroupLine extends SGElementGroup implements
        SGILineConstants, SGIElementGroupConstants {

    public static final int MODE_ALL = 0;

    public static final int MODE_OMIT = 1;

    protected int mMode = MODE_OMIT;

    /**
     * The line stroke.
     */
    protected SGStroke mStroke = new SGStroke();

    /**
     * The list of connected paths.
     */
    protected List<GeneralPath> mConnectedPathList = new ArrayList<GeneralPath>();

    /**
     * A flag whether to connect all effective points.
     */
    protected boolean mConnectingAllFlag = true;

    /**
     * The line style.
     */
    protected SGLineStyle mStyle = null;

    /**
     * Create a group of lines.
     */
    public SGElementGroupLine(final int lineType, final Color lineColor, final float lineWidth) {
        super();
        this.mStyle = new SGLineStyle(lineType, lineColor, lineWidth);
    }

    /**
     * Dispose this object.
     */
    public void dispose() {
        super.dispose();
        this.mConnectedPathList.clear();
        this.mConnectedPathList = null;
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
     * @param mag
     *           the magnification to set
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        if (super.setMagnification(mag) == false) {
            return false;
        }
        this.mStroke.setMagnification(mag);
        return true;
    }

    /**
     * Set the line width of this line group.
     *
     * @param width
     *            the line width
     * @return true if succeeded
     */
    public boolean setLineWidth(final float width) {
        if (width < 0.0f) {
            throw new IllegalArgumentException("width < 0.0f");
        }
        this.mStyle.setLineWidth(width);
        this.mStroke.setLineWidth(width);
        return true;
    }

    /**
     * Set the line width in a given unit.
     *
     * @param lw
     *            the line width to set
     * @param unit
     *            the unit for the given line width
     * @return true if succeeded
     */
    public abstract boolean setLineWidth(final float lw, final String unit);

    /**
     * Sets the line type.
     *
     * @param type
     *          a value to set to the line type
     * @return true if succeeded
     */
    public boolean setLineType(final int type) {
        if (SGDrawingElementLine.isValidLineType(type) == false) {
            return false;
        }
        this.mStyle.setLineType(type);
        this.mStroke.setLineType(type);
        return true;
    }

    /**
     * Sets the line color.
     * @param cl
     *           a color to set to the line color
     * @return true if succeeded
     */
    public boolean setColor(Color cl) {
        if (cl == null) {
            throw new IllegalArgumentException("cl == null");
        }
        this.mStyle.setColor(cl);
        return true;
    }

    /**
     * Sets whether the lines connect all effective points.
     *
     * @return true if succeeded
     */
    public boolean setLineConnectingAll(final boolean b) {
        this.mConnectingAllFlag = b;
        return true;
    }

    /**
     * Returns the line width.
     *
     * @return the line width.
     */
    public float getLineWidth() {
        return this.mStyle.getLineWidth();
    }

    /**
     * Returns the line width with given length unit.
     *
     * @param unit
     *            the length unit.
     * @return the line width in given length unit.
     */
    public float getLineWidth(final String unit) {
        return (float) SGUtilityText
                .convertFromPoint(this.getLineWidth(), unit);
    }

    /**
     * Returns the line type.
     *
     * @return the line type.
     */
    public int getLineType() {
        return this.mStyle.getLineType();
    }

    public Color getColor() {
        return this.mStyle.getColor();
    }

    /**
     * Returns whether the lines connect all effective points.
     *
     * @return true if connecting all effective points
     */
    public boolean isLineConnectingAll() {
        return this.mConnectingAllFlag;
    }

    /**
     * Returns the start point of a line at given array index.
     *
     * @param index
     *           the array index
     * @return the start point of a line
     */
    public abstract SGTuple2f getStart(final int index);

    /**
     * Returns the start point of a line at given array index.
     *
     * @param index
     *           the array index
     * @return the start point of a line
     */
    public abstract SGTuple2f getEnd(final int index);

    /**
     * Paint line objects.
     */
    public boolean paintElement(final Graphics2D g2d, final Rectangle2D clipRect) {
        if (g2d == null) {
            return false;
        }

        // setup the color
        Color cl;
        if (this.mStyle != null) {
        	cl = this.mStyle.getColor();
        } else {
        	cl = this.getColor();
        }
        g2d.setPaint(cl);

        // setup the stroke
        Stroke stroke;
        if (this.mStyle != null) {
        	SGStroke tempStroke = (SGStroke) this.mStroke.clone();
        	tempStroke.setLineType(this.mStyle.getLineType());
        	tempStroke.setLineWidth(this.mStyle.getLineWidth());
        	stroke = tempStroke.getBasicStroke();
        } else {
            stroke = this.mStroke.getBasicStroke();
        }
        g2d.setStroke(stroke);

        // paint
        List<GeneralPath> gpList = new ArrayList<GeneralPath>(this.mConnectedPathList);
        if (clipRect == null) {
            for (int ii = 0; ii < gpList.size(); ii++) {
                GeneralPath gp = gpList.get(ii);
                g2d.draw(gp);
            }
        } else {
            Area rectArea = new Area(clipRect);
            for (int ii = 0; ii < gpList.size(); ii++) {
                GeneralPath gp = gpList.get(ii);
                Shape sh = stroke.createStrokedShape(gp);
                Area shArea = new Area(sh);
                shArea.intersect(rectArea);
                g2d.fill(shArea);
            }
        }

        return true;
    }
    
    /**
     *
     * @return
     */
    public String getTagName() {
        return TAG_NAME_LINE;
    }

    @Override
    public boolean writeProperty(final Element el) {
    	// do nothing
    	return true;
    }

    public static boolean writeProperty(final Element el, final int lineType, final Color lineColor,
    		final float lineWidth) {
    	writeProperty(el, lineType);
    	writeProperty(el, lineColor);
    	writeProperty(el, lineWidth);
        return true;
    }

    private static void writeProperty(final Element el, final int lineType) {
        el.setAttribute(KEY_LINE_TYPE, SGDrawingElementLine.getLineTypeName(lineType));
    }

    private static void writeProperty(final Element el, final Color lineColor) {
        List<Color> cList = new ArrayList<Color>();
        cList.add(lineColor);
        el.setAttribute(KEY_COLOR, SGUtilityText.getColorListString(cList));
    }

    private static String getLineWidthString(final float lineWidth) {
        final int digitLineWidth = SGIConstants.LINE_WIDTH_MINIMAL_ORDER - 1;
        final float lw = (float) SGUtilityNumber.roundOffNumber(
        	lineWidth, digitLineWidth);
        String str = Float.toString(lw) + SGIConstants.pt;
        return str;
    }

    private static void writeProperty(final Element el, final float lineWidth) {
        el.setAttribute(KEY_LINE_WIDTH, getLineWidthString(lineWidth));
    }

    /**
     *
     * @param el
     * @return
     */
    public boolean readProperty(final Element el) {
        String str = null;
        Boolean b = null;
        
        // style properties
        NodeList lineStylesNodeList = el.getElementsByTagName(SGILineConstants.TAG_NAME_STYLES);
        if (lineStylesNodeList.getLength() > 0) {
        	Element lineStylesElement = (Element) lineStylesNodeList.item(0);
        	NodeList lineStyleNodeList = lineStylesElement.getChildNodes();
            int num = 0;
            for (int ii = 0; ii < lineStyleNodeList.getLength(); ii++) {
            	Node node = lineStyleNodeList.item(ii);
            	if (node instanceof Element) {
            		num++;
            	}
            }
            // uses the last index
            if (readStyleProperty(lineStylesElement, this, num - 1) == false) {
            	return false;
            }
        }

        // connecting all effective points
        str = el.getAttribute(KEY_LINE_CONNECT_ALL_EFFECTIVE_POINTS);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b == null) {
            	return false;
            }
            if (this.setLineConnectingAll(b.booleanValue()) == false) {
                return false;
            }
        }

        return true;
    }

    static boolean readStyleProperty(final Element el, final SGElementGroupLine group, 
    		final int index) {
        String str = null;
        Number num = null;
        Color color = null;
        
        // finds the style element of given index
        int cnt = 0;
        Element elStyle = null;
        NodeList nList = el.getElementsByTagName(SGILineConstants.TAG_NAME_STYLE);
        for (int ii = 0; ii < nList.getLength(); ii++) {
            Node node = nList.item(ii);
            if (node instanceof Element) {
            	if (cnt == index) {
                    elStyle = (Element) node;
            		break;
            	}
            	cnt++;
            }        	
        }

        // for backward compatibility <= 2.0.0
        Element elStyleHolder = (elStyle != null) ? elStyle : el;
        
        // line width
        str = elStyleHolder.getAttribute(KEY_LINE_WIDTH);
        if (str.length() != 0) {
            StringBuffer uLineWidth = new StringBuffer();
            num = SGUtilityText.getNumber(str, uLineWidth);
            if (num == null) {
            	return false;
            }
            final float lineWidth = num.floatValue();
            if (group.setLineWidth(lineWidth, uLineWidth.toString()) == false) {
                return false;
            }
        }

        // line type
        str = elStyleHolder.getAttribute(KEY_LINE_TYPE);
        if (str.length() != 0) {
            num = SGDrawingElementLine.getLineTypeFromName(str);
            if (num == null) {
            	return false;
            }
            final int lineType = num.intValue();
            if (group.setLineType(lineType) == false) {
                return false;
            }
        }

        // color
        str = elStyleHolder.getAttribute(KEY_COLOR);
        if (str.length() != 0) {
        	color = SGUtilityText.parseColor(str);
        	if (color == null) {
        		return false;
        	}
            if (group.setColor(color) == false) {
                return false;
            }
        }
        
        SGLineStyle lineStyle = new SGLineStyle(group.getLineType(), group.getColor(), group.getLineWidth());
        group.setStyle(lineStyle);

        return true;
    }

    /**
     *
     * @return
     */
    protected SGDrawingElement createDrawingElementInstance(final int index) {
        return new LineInGroup(this, index);
    }

    /**
     * A line in a group of lines.
     *
     */
    protected static class LineInGroup extends SGDrawingElementLine2D {

        // a group of lines
        protected SGElementGroupLine mGroup = null;

        /**
         * The array index.
         */
        protected int mIndex = -1;

        /**
         * Builds this object in a given group.
         *
         * @param group
         *           a group of lines
         */
        public LineInGroup(SGElementGroupLine group, final int index) {
            super();
            this.mGroup = group;
            this.mIndex = index;
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.mGroup = null;
        }

        /**
         * Returns a stroke.
         *
         * @return a stroke
         */
        protected SGStroke getStroke() {
            return this.mGroup.getStroke();
        }

        /**
         * Sets the line width.
         *
         * @param width
         *            line width to set
         * @return true if succeeded
         */
        public boolean setLineWidth(float width) {
            // do nothing
            return true;
        }

        /**
         * Sets the line type.
         *
         * @param type
         *           line type
         * @return true if succeeded
         */
        public boolean setLineType(int type) {
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
            return this.mGroup.getColor();
        }

        @Override
        public boolean setColor(Color cl) {
            // do nothing
            return true;
        }

        @Override
        public SGTuple2f getEnd() {
            return this.mGroup.getEnd(this.mIndex);
        }

        @Override
        public SGTuple2f getStart() {
            return this.mGroup.getStart(this.mIndex);
        }

        @Override
        public boolean setTermPoints(SGTuple2f start, SGTuple2f end) {
            // do nothing
            return true;
        }
        
        public int getIndex() {
        	return this.mIndex;
        }
    }

    /**
     *
     * @return
     */
    public boolean initDrawingElement(final int num) {
    	final int elNum;
    	if (num == 0) {
    		elNum = 0;
    	} else {
    		elNum = num - 1;
    	}
        return super.initDrawingElement(elNum);
    }

    /**
     *
     * @return
     */
    protected boolean initDrawingElement(final SGTuple2f[] array) {
        final int num = array.length;
        if (this.initDrawingElement(num) == false) {
            return false;
        }

        SGDrawingElement[] lArray = this.mDrawingElementArray;
        this.mEndPointsIndexArray = new int[lArray.length][2];
        for (int ii = 0; ii < lArray.length; ii++) {
            ((SGDrawingElementLine) lArray[ii]).setTermPoints(array[ii],
                    array[ii + 1]);
            this.mEndPointsIndexArray[ii][0] = ii;
            this.mEndPointsIndexArray[ii][1] = ii + 1;
        }
        return true;
    }

    /**
     * An array of array indices of the end  points of lines.
     */
    protected int[][] mEndPointsIndexArray = null;

    protected void setEndPointIndices(final int lineIndex, final int startIndex, final int endIndex) {
        this.mEndPointsIndexArray[lineIndex][0] = startIndex;
        this.mEndPointsIndexArray[lineIndex][1] = endIndex;
    }

    protected int getStartIndex(final int lineIndex) {
        return this.mEndPointsIndexArray[lineIndex][0];
    }

    protected int getEndIndex(final int lineIndex) {
        return this.mEndPointsIndexArray[lineIndex][1];
    }

    /**
     * Set the term points of each line element.
     *
     * @param pointArray
     *            location of term points
     */
    public boolean setLocation(final SGTuple2f[] pointArray) {
        return this.setLocationSub(pointArray);
    }

    /**
     * Set the term points of each line element.
     *
     * @param pointArray
     *            original location of term points
     */
    protected boolean setLocationSub(final SGTuple2f[] pointArray) {

        if (this.mDrawingElementArray == null) {
            return true;
        }

        if (pointArray.length - 1 != this.mDrawingElementArray.length) {
            this.initDrawingElement(pointArray);
        }

        final SGDrawingElement[] array = this.mDrawingElementArray;

        // clear the list
        final List<GeneralPath> pathList = this.mConnectedPathList;
        pathList.clear();

        // check the effectiveness of points
        boolean allEffectiveFlag = true;
        final boolean[] effArray = new boolean[pointArray.length];
        for (int ii = 0; ii < effArray.length; ii++) {
            effArray[ii] = !(pointArray[ii].isInfinite() || pointArray[ii]
                    .isNaN());
            if (effArray[ii] == false) {
                allEffectiveFlag = false;
            }
        }

        // if all points are effective
        if (allEffectiveFlag) {
            // set location to the drawing elements
            for (int ii = 0; ii < pointArray.length - 1; ii++) {
                this.setEndPointIndices(ii, ii, ii + 1);
            }

            // set all lines visible
            for (int ii = 0; ii < array.length; ii++) {
                final SGDrawingElementLine line = (SGDrawingElementLine) array[ii];
                line.setVisible(true);
            }

            // reduce the points
            SGTuple2f[] points = pointArray;

            // System.out.println(this.mMode);
            if (this.mMode == MODE_OMIT) {
                // points = this.reducePointsInNoise( points );
                points = this.reduceClosePoints(points);
            }

            // create general paths
            final int len = points.length;
            if (len > 1) {
                final GeneralPath gp = new GeneralPath();
                final Line2D line = new Line2D.Float(points[0].x, points[0].y,
                        points[1].x, points[1].y);
                gp.append(line, true);
                if (len > 2) {
                    for (int ii = 2; ii < len; ii++) {
                        gp.lineTo(points[ii].x, points[ii].y);
                    }
                }
                pathList.add(gp);
            }

        } else {        // allEffectiveFlag

            // a list of line lists
            List<List<SGDrawingElementLine>> lineListList = new ArrayList<List<SGDrawingElementLine>>();

            if (this.mConnectingAllFlag) {
                // connect all effective points

                ArrayList<SGDrawingElementLine> list = new ArrayList<SGDrawingElementLine>();
                int effLineCnt = 0;
                for (int ii = 0; ii < pointArray.length - 1; ii++) {
                    int next = -1;
                    if (!effArray[ii]) {
                        continue;
                    } else {
                        // find next effective point
                        for (int jj = ii + 1; jj < pointArray.length; jj++) {
                            if (effArray[jj]) {
                                next = jj;
                                break;
                            }
                        }
                    }
                    if (next == -1) {
                        // current point is the last effective point
                        break;
                    }

                    final SGDrawingElementLine line = (SGDrawingElementLine) array[effLineCnt];
                    this.setEndPointIndices(effLineCnt, ii, next);

                    line.setVisible(true);
                    list.add(line);
                    effLineCnt++;
                }
                if (list.size() != 0) {
                    lineListList.add(list);
                }

                // hide remaining lines
                for (int ii = effLineCnt; ii < array.length; ii++) {
                    final SGDrawingElementLine line = (SGDrawingElementLine) array[ii];
                    line.setVisible(false);
                }

            } else {    // this.mConnectingAllFlag == false

                ArrayList<SGDrawingElementLine> list = new ArrayList<SGDrawingElementLine>();
                for (int ii = 0; ii < pointArray.length - 1; ii++) {
                    final SGDrawingElementLine line = (SGDrawingElementLine) array[ii];
                    final boolean eff = effArray[ii] && effArray[ii + 1];
                    line.setVisible(eff);
                    if (eff) {
                        this.setEndPointIndices(ii, ii, ii + 1);
                        list.add(line);
                    } else {
                        if (list.size() != 0) {
                            lineListList.add(list);
                            list = new ArrayList<SGDrawingElementLine>();
                        }
                    }
                }
                if (list.size() != 0) {
                    lineListList.add(list);
                }
            }

            // create general paths
            for (int ii = 0; ii < lineListList.size(); ii++) {
                final List<SGDrawingElementLine> lineList = lineListList.get(ii);
                final GeneralPath gp = new GeneralPath();
                final SGDrawingElementLine el = lineList.get(0);
                final Line2D line = SGDrawingElementLine2D.getLine(el);
                gp.append(line, true);
                if (lineList.size() >= 2) {
                    Point2D pos = line.getP2();
                    int tmpX = (int) pos.getX();
                    int tmpY = (int) pos.getY();
                    for (int jj = 1; jj < lineList.size(); jj++) {
                        SGDrawingElementLine el2 = lineList.get(jj);
                        final SGTuple2f next = el2.getEnd();

                        // current location
                        final float x = next.x;
                        final float y = next.y;
                        if (((int) x == tmpX) && ((int) y == tmpY)) {
                            continue;
                        }
                        gp.lineTo(x, y);

                        // update temporary values
                        tmpX = (int) x;
                        tmpY = (int) y;
                    }
                }

                pathList.add(gp);
            }
        }

        return true;
    }

    private SGTuple2f[] reduceClosePoints(SGTuple2f[] array) {
        final int len = array.length;
        if (len < 2) {
            return array;
        }

        List<SGTuple2f> list = new ArrayList<SGTuple2f>();
        list.add(array[0]);

        // temporary variables
        int tmpX = (int) array[0].x;
        int tmpY = (int) array[0].y;

        for (int ii = 1; ii < len; ii++) {
            // current location
            final float x = array[ii].x;
            final float y = array[ii].y;

            if (((int) x == tmpX) && ((int) y == tmpY)) {
                continue;
            }

            list.add(array[ii]);

            // update temporary values
            tmpX = (int) x;
            tmpY = (int) y;
        }

        SGTuple2f[] arrayNew = (SGTuple2f[]) list.toArray(new SGTuple2f[] { });

        return arrayNew;
    }

    /**
     *
     */
    public SGProperties getProperties() {
        LineProperties p = new LineProperties();
        this.getProperties(p);
        return p;
    }

    /**
     *
     */
    public boolean getProperties(SGProperties p) {
        if (p == null) {
            return false;
        }
        if ((p instanceof LineProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        LineProperties lp = (LineProperties) p;
        lp.setLineWidth(this.getLineWidth());
        lp.setLineType(this.getLineType());
        lp.setColor(this.getColor());
        lp.mConnectAllFlag = this.mConnectingAllFlag;
        lp.mLineStyle = this.mStyle;
        return true;
    }

    /**
     *
     */
    public boolean setProperties(SGProperties p) {
        if ((p instanceof LineProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        LineProperties lp = (LineProperties) p;
        Float width = lp.getLineWidth();
        if (width == null) {
            return false;
        }
        Integer type = lp.getLineType();
        if (type == null) {
            return false;
        }
        Color cl = lp.getColor();
        if (cl == null) {
            return false;
        }
        this.setLineWidth(width.floatValue());
        this.setLineType(type.intValue());
        this.setLineConnectingAll(lp.mConnectAllFlag);
        this.setColor(cl);
        this.setStyle(lp.mLineStyle);
        return true;
    }

    /**
     * Set the number of the first line.
     *
     * @param num
     *            the number of the first line.
     */
    public void setLineNumber1(int num) {
        this.mStroke.setLineNumber1(this.getLineType(), num);
    }

    /**
     * Set the number of the second line.
     *
     * @param num
     *            the number of the second line.
     */
    public void setLineNumber2(int num) {
        this.mStroke.setLineNumber2(this.getLineType(), num);
    }

    /**
     * Set the length of the first line.
     *
     * @param len
     *            the length of the first line.
     */
    public void setLineLength1(float len) {
        this.mStroke.setLineLength1(this.getLineType(), len);
    }

    /**
     * Set the length of the second line.
     *
     * @param len
     *            the length of the second line.
     */
    public void setLineLength2(float len) {
        this.mStroke.setLineLength2(this.getLineType(), len);
    }

    /**
     * Set the space between lines.
     *
     * @param space
     *            the space between lines.
     */
    public void setSpace(float space) {
        this.mStroke.setSpace(this.getLineType(), space);
    }

    /**
     * Returns the number of the first line segments.
     *
     * @return the number of the first line segments.
     */
    public int getLineNum1() {
        return this.mStroke.getLineNum1(this.getLineType());
    }

    /**
     * Returns the number of the second line segments.
     *
     * @return the number of the second line segments.
     */
    public int getLineNum2() {
        return this.mStroke.getLineNum2(this.getLineType());
    }

    /**
     * Returns the length of the first line segments.
     *
     * @return the length of the first line segments.
     */
    public float getLineLength1() {
        return this.mStroke.getLineLength1(this.getLineType());
    }

    /**
     * Returns the length of the second line segments.
     *
     * @return the length of the second line segments.
     */
    public float getLineLength2() {
        return this.mStroke.getLineLength2(this.getLineType());
    }

    /**
     * Returns the space between line segments.
     *
     * @return the space between line segments.
     */
    public float getSpace() {
        return this.mStroke.getSpace(this.getLineType());
    }

    /**
     * Properties of lines.
     *
     */
    public static class LineProperties extends ElementGroupProperties {

        private SGDrawingElementLine.LineProperties mLineProperties = new SGDrawingElementLine.LineProperties();

        private boolean mConnectAllFlag = true;

        private double mShiftX;

        private double mShiftY;

        private SGLineStyle mLineStyle = null;

        /**
         *
         */
        public LineProperties() {
            super();
        }

        /**
         * Copy this object.
         * @return a copied object
         */
        public Object copy() {
            Object obj = super.copy();
            LineProperties p = (LineProperties) obj;
            p.mLineProperties = (SGDrawingElementLine.LineProperties) this.mLineProperties.copy();
            p.mConnectAllFlag = this.mConnectAllFlag;
            p.mShiftX = this.mShiftX;
            p.mShiftY = this.mShiftY;
            p.mLineStyle = this.mLineStyle;
            return p;
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.mLineProperties.dispose();
            this.mLineStyle = null;
        }

        /**
         *
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof LineProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            LineProperties p = (LineProperties) obj;
            if (this.mLineProperties.equals(p.mLineProperties) == false) {
                return false;
            }
            if (this.mConnectAllFlag != p.mConnectAllFlag) {
                return false;
            }
            if (this.mShiftX != p.mShiftX) {
                return false;
            }
            if (this.mShiftY != p.mShiftY) {
                return false;
            }
            if (!SGUtility.equals(this.mLineStyle, p.mLineStyle)) {
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

        public Color getColor() {
            return this.mLineProperties.getColor();
        }

        public boolean setLineWidth(final float w) {
            return this.mLineProperties.setLineWidth(w);
        }

        public boolean setLineType(final int num) {
            return this.mLineProperties.setLineType(num);
        }

        public boolean setColor(final Color cl) {
            return this.mLineProperties.setColor(cl);
        }
    }

    public void setStyle(SGLineStyle style) {
    	if (style == null) {
    		throw new IllegalArgumentException("style == null");
    	}
    	this.mStyle = (style != null) ? (SGLineStyle) style.clone() : null;
    }
    
    public SGLineStyle getLineStyle() {
    	return (SGLineStyle) this.mStyle.clone();
    }
}

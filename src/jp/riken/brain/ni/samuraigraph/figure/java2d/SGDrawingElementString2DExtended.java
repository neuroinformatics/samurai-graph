package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementString;

/**
 * An extended class of string element. This drawing element represent a string
 * with superscript and subscript.
 */
public class SGDrawingElementString2DExtended extends SGDrawingElementString2D {
    
    /**
     * The super/subscript font size scaling factor
     */
    private static final float SCRIPT_FONTFACTOR = 1.4f;

    /**
     * The list of string elements of base characters.
     */
    protected List<SGDrawingElementString2D> mBaseElementList = new ArrayList<SGDrawingElementString2D>();

    /**
     * The list of string elements of subscript.
     */
    protected List<SGDrawingElementString2D> mSubscriptElementList = new ArrayList<SGDrawingElementString2D>();

    /**
     * The list of string elements of superscript.
     */
    protected List<SGDrawingElementString2D> mSuperscriptElementList = new ArrayList<SGDrawingElementString2D>();

    /**
     * Check flag for update location requirements
     */
    private boolean mUpdateLocationRequired = true;
    
    /**
     * Default constructor.
     */
    public SGDrawingElementString2DExtended() {
        super();
        this.createStringElements();
    }

    /**
     * Construct a string element with given text.
     */
    public SGDrawingElementString2DExtended(final String str) {
        super(str);
        this.createStringElements();
    }

    /**
     * Construct a string element with given string element.
     */
    public SGDrawingElementString2DExtended(final SGDrawingElementString element) {
        super(element);
        this.createStringElements();
    }

    /**
     * Construct a string element with given text and font information.
     */
    public SGDrawingElementString2DExtended(final String str,
            final String fontName, final int fontStyle, final float fontSize,
            final Color color, final float mag, final float angle) {
        super(str, fontName, fontStyle, fontSize, color, mag, angle);
        this.createStringElements();
    }

    /**
     * 
     */
    public void dispose() {
        super.dispose();
        ArrayList<SGDrawingElementString2D> list = this.getAllStringElement();
        for (int ii = 0; ii < list.size(); ii++) {
            SGDrawingElementString2D el = (SGDrawingElementString2D) list.get(ii);
            el.dispose();
        }
        this.mBaseElementList.clear();
        this.mSuperscriptElementList.clear();
        this.mSubscriptElementList.clear();
        this.mBaseElementList = null;
        this.mSuperscriptElementList = null;
        this.mSubscriptElementList = null;
        this.mStringRect = null;
        this.mElementBounds = null;
    }

    /**
     * Returns whether this string element has superscripts or subscripts.
     * 
     * @return
     *       true if this string element has superscripts or subscripts
     */
    private boolean containsSubscriptsOrSuperscripts() {
        final String str = this.getString();
        boolean bEscape = false;
        for (int ii = 0; ii < str.length(); ii++) {
            char c = str.charAt(ii);
            if (bEscape) {
                bEscape = false;
            } else {
                if (c == '\\') {
                    bEscape = true;
                } else if (c == '_' || c == '^') {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a list of all string elements without distinction of base
     * character, subscript or superscript.
     * 
     * @return
     *       a list of all string elements
     */
    protected final ArrayList<SGDrawingElementString2D> getAllStringElement() {
        ArrayList<SGDrawingElementString2D> list = new ArrayList<SGDrawingElementString2D>();
        list.addAll(this.mBaseElementList);
        list.addAll(this.mSubscriptElementList);
        list.addAll(this.mSuperscriptElementList);
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            Object obj = list.get(ii);
            if (obj == null) {
                list.remove(ii);
            }
        }
        return list;
    }

    /**
     * Set the text.
     */
    public boolean setString(final String str) {
        boolean ret = super.setString(str);
        this.createStringElements();
        this.mUpdateLocationRequired = true;
        return ret;
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
        List<SGDrawingElementString2D> list = this.getAllStringElement();
        for (int ii = 0; ii < list.size(); ii++) {
            SGDrawingElementString2D el = list.get(ii);
            if (el.setMagnification(mag) == false) {
                return false;
            }
        }
        this.mUpdateLocationRequired = true;
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
        if (super.setColor(color) == false) {
            return false;
        }
        ArrayList<SGDrawingElementString2D> list = this.getAllStringElement();
        for (int ii = 0; ii < list.size(); ii++) {
            SGDrawingElementString el = (SGDrawingElementString) list.get(ii);
            el.setColor(color);
        }
        return true;
    }

    /**
     * 
     */
    public boolean setFont(final String name, final int style, final float size) {
        super.setFont(name, style, size);
        if (this.mBaseElementList == null) {
            return true;
        }
        Font f = new Font(name, style, (int) size);
        Font f2 = new Font(name, style, (int) (size / SCRIPT_FONTFACTOR));
        for (int ii = 0; ii < this.mBaseElementList.size(); ii++) {
            SGDrawingElementString2D el = this.mBaseElementList.get(ii);
            el.setFont(f.getFamily(), f.getStyle(), f.getSize());
        }
        for (int ii = 0; ii < this.mSubscriptElementList.size(); ii++) {
        	SGDrawingElementString2D el = this.mSubscriptElementList.get(ii);
            if (el != null) {
                el.setFont(f2.getFamily(), f2.getStyle(), f2.getSize());
            }
        }
        for (int ii = 0; ii < this.mSuperscriptElementList.size(); ii++) {
        	SGDrawingElementString2D el = this.mSuperscriptElementList.get(ii);
            if (el != null) {
                el.setFont(f2.getFamily(), f2.getStyle(), f2.getSize());
            }
        }
        this.mUpdateLocationRequired = true;
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
        this.mUpdateLocationRequired = true;
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
        this.mUpdateLocationRequired = true;
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
        ArrayList<SGDrawingElementString2D> list = this.getAllStringElement();
        for (int ii = 0; ii < list.size(); ii++) {
            SGDrawingElementString2D el = list.get(ii);
            el.setAngle(angle);
        }
        this.mUpdateLocationRequired = true;
        return true;
    }

    /**
     * Creates a string element without any modification of the input text string.
     * 
     * @param str
     *           a text string
     * @return true if succeeded
     */
    protected boolean createStringElementsDirectly(final String str) {
		SGDrawingElementString2D el = new SGDrawingElementString2D(str,
				this.getFontName(), this.getFontStyle(), this.getFontSize(),
				this.getColor(), this.getMagnification(), this.getAngle());
        this.mBaseElementList.add(el);
        this.mSubscriptElementList.add(null);
        this.mSuperscriptElementList.add(null);
        return true;
    }
    
	/**
	 * Creates a string element from an input string without subscript nor superscript.
	 * 
	 * @return true if succeeded
	 */
    protected boolean createStringElementWithoutIndex() {
		return this.createStringElementsDirectly(
				SGUtilityText.compile(this.getString()));
    }

    /**
     * Create all string elements. This method is called when the text is set or
     * changed.
     * 
     * @return true if succeeded
     */
    private boolean createStringElements() {
        final String name = this.getFontName();
        final int style = this.getFontStyle();
        final float size = this.getFontSize();
        final Color cl = this.getColor();
        final float mag = this.getMagnification();
        final float angle = this.getAngle();
        final float subsize = size / SCRIPT_FONTFACTOR;

        // clear all lists
        this.mBaseElementList.clear();
        this.mSubscriptElementList.clear();
        this.mSuperscriptElementList.clear();

		// if the string does not contain subscripts nor superscripts,
		// set the string to the base element
		if (this.containsSubscriptsOrSuperscripts() == false) {
			return this.createStringElementWithoutIndex();
		}

		// get information of the input line
		String line = this.getString();
		List<String> baseList = new ArrayList<String>();
		List<String> superList = new ArrayList<String>();
		List<String> subList = new ArrayList<String>();
		if (!this.getSubscriptAndSuperscriptInfo(line, baseList,
				superList, subList)) {
			return this.createStringElementsDirectly(this.getString());
		}

		for (int ii = 0; ii < baseList.size(); ii++) {
			String str = null;
			SGDrawingElementString2D el = null;

			// base characters
			str = (String) baseList.get(ii);
			el = new SGDrawingElementString2D(str, name, style, size, cl, mag,
					angle);
			this.mBaseElementList.add(el);

			// subscript characters
			Object sb = subList.get(ii);
			if (sb != null) {
				str = (String) sb;
				el = this.createIndexInstance(str, name, style, subsize, cl,
						mag, angle);
                this.mSubscriptElementList.add(el);
			} else {
				this.mSubscriptElementList.add(null);
			}

			// superscript characters
			Object sp = superList.get(ii);
			if (sp != null) {
				str = (String) sp;
				el = this.createIndexInstance(str, name, style, subsize, cl,
						mag, angle);
                this.mSuperscriptElementList.add(el);
			} else {
				this.mSuperscriptElementList.add(null);
			}
		}

        return true;
    }
    
	protected SGDrawingElementString2D createIndexInstance(final String str,
			final String name, final int style, final float subsize,
			final Color cl, final float mag, final float angle) {
		return new SGDrawingElementString2DExtended(str, name, style, subsize,
				cl, mag, angle);
	}
    
    /**
     * Separates a given text string into the base strings, subscript strings and 
     * superscript strings.
     * 
     * @param line
     *            A line.
     * @param bseList
     *            List of base strings.
     * @param superList
     *            List of superscript strings.
     * @param subList
     *            List of subscript strings.
     * @return true if succeeded
     */
    protected boolean getSubscriptAndSuperscriptInfo(final String line,
            final List<String> baseList, final List<String> superList,
            final List<String> subList) {
    	return SGUtilityText.getSubscriptAndSuperscriptInfo(line, baseList,
				superList, subList);
    }

    /**
     * get ascent
     */
    protected float getAscent() {
        if (this.mUpdateLocationRequired) {
            this.updateLocation();
        }
        return this.mAscent;
    }

    /**
     * get descent
     */
    protected float getDescent() {
        if (this.mUpdateLocationRequired) {
            this.updateLocation();
        }
        return this.mDescent;
    }

    /**
     * get leading
     */
    protected float getLeading() {
        if (this.mUpdateLocationRequired) {
            this.updateLocation();
        }
        return this.mLeading;
    }

    /**
     * get advance
     */
    protected float getAdvance() {
        if (this.mUpdateLocationRequired) {
            this.updateLocation();
        }
        return this.mAdvance;
    }

    /**
     * get strike trough offset
     */
    protected float getStrikethroughOffset() {
        if (this.mUpdateLocationRequired) {
            this.updateLocation();
        }
        return this.mStrikethroughOffset;
    }

    /**
     * 
     */
    public Rectangle2D getStringRect() {
        if (this.mUpdateLocationRequired) {
            this.updateLocation();
        }
        return (Rectangle2D) this.mStringRect.clone();
    }

    /**
     * 
     */
    public Rectangle2D getElementBounds() {
        if (this.mUpdateLocationRequired) {
            this.updateLocation();
        }
        return (Rectangle2D) this.mElementBounds.clone();
    }

    /**
     * Request update location
     */
    public void requestUpdateLocation() {
        this.mUpdateLocationRequired = true;
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

        if (this.mUpdateLocationRequired) {
            this.updateLocation();
        }

        // draw strings
        ArrayList<SGDrawingElementString2D> list = this.getAllStringElement();
        for (int ii = 0; ii < list.size(); ii++) {
            SGDrawingElementString2D el = list.get(ii);
            el.paint(g2d);
        }
    }

    /**
     * Update the location of the base, superscript and subscript elements.
     * 
     * @return status
     */
    private boolean updateLocation() {
        // calculate metrics
        this.calcMetrics();

        // initialize
        // angle
        final float angle = this.getAngle() * SGIConstants.RADIAN_DEGREE_RATIO;
        final float cv = (float) Math.cos(angle);
        final float sv = (float) Math.sin(angle);
        // location
        final float x = this.getX();
        final float y = this.getY();
        final float visual_ascent = (float) (-this.mStringRect.getY());
        final float strike_through = visual_ascent + this.mStrikethroughOffset;
        final float margin = this.mLeading / 2.0f;
        final float super_bottom = strike_through - margin;
        final float sub_top = strike_through + margin;

        float advance = 0.0f;
        for (int ii = 0; ii < this.mBaseElementList.size(); ii++) {
            // base list
            float base_advance = 0.0f;
            {
                final SGDrawingElementString2D base_el = this.mBaseElementList.get(ii);
                // get visual height of base list
                final float base_visual_height = (float) base_el
                        .getStringRect().getHeight();
                // calculate location
                final float base_dx = advance;
                final float base_dy = visual_ascent - base_visual_height;
                final float base_nx = x + base_dx * cv + base_dy * sv;
                final float base_ny = y - base_dx * sv + base_dy * cv;
                // set location
                base_el.setLocation(base_nx, base_ny);
                // get advance of baselist
                base_advance = base_el.getAdvance();
            }

            // superscript list
            float super_advance = 0.0f;
            {
                final SGDrawingElementString2DExtended super_el
                	= (SGDrawingElementString2DExtended) this.mSuperscriptElementList.get(ii);
                if (super_el != null) {
                    // get visual height of superscript list
                    final float super_visual_height = (float) super_el
                            .getStringRect().getHeight();
                    // calculate location
                    final float super_dx = advance + base_advance;
                    final float super_dy = super_bottom - super_visual_height;
                    final float super_nx = x + super_dx * cv + super_dy * sv;
                    final float super_ny = y - super_dx * sv + super_dy * cv;
                    // set location
                    super_el.setLocation(super_nx, super_ny);
                    // get advance of superscript list
                    super_advance = super_el.getAdvance();
                }
            }

            // subscript list
            float sub_advance = 0.0f;
            {
				final SGDrawingElementString2DExtended sub_el
					= (SGDrawingElementString2DExtended) this.mSubscriptElementList.get(ii);
                if (sub_el != null) {
                    // calculate location
                    final float sub_dx = advance + base_advance;
                    final float sub_dy = sub_top;
                    final float sub_nx = x + sub_dx * cv + sub_dy * sv;
                    final float sub_ny = y - sub_dx * sv + sub_dy * cv;
                    // set location
                    sub_el.setLocation(sub_nx, sub_ny);
                    // get advance of subscript list
                    sub_advance = sub_el.getAdvance();
                }
            }

            // increase advance
            advance += base_advance
                    + ((super_advance > sub_advance) ? super_advance
                            : sub_advance);
        }

        // calculate element bounds
        calcElementBounds();

        this.mUpdateLocationRequired = false;

        return true;
    }

    /**
     * calculate string metrics
     */
    private void calcMetrics() {
        float base_visual_x = 0.0f;
        float base_strike_through_offset = 0.0f;
        float base_leading = 0.0f;
        float base_visual_ascent = 0.0f;
        float base_visual_descent = 0.0f;
        float super_height = 0.0f;
        float sub_height = 0.0f;
        float visual_x = 0.0f;
        float visual_y = 0.0f;
        float visual_ascent = 0.0f;
        float visual_descent = 0.0f;
        float visual_height = 0.0f;
        float visual_advance = 0.0f;

        float strike_through_offset = 0.0f;
        // float leading = 0.0f;
        float advance = 0.0f;

        // calculate font specific values
        if (this.mBaseElementList.size() != 0) {
            final SGDrawingElementString2D el = this.mBaseElementList.get(0);
            // get center line height ( baseline to center line : negative value
            // )
            base_strike_through_offset = el.getStrikethroughOffset();
            // get leading ( baseline to center line )
            base_leading = el.getLeading();
            // get x offset
            base_visual_x = (float) el.getStringRect().getX();
        }
        // leading = base_leading;
        strike_through_offset = base_strike_through_offset;

        // calculate string specific value
        for (int ii = 0; ii < this.mBaseElementList.size(); ii++) {
            // base list
            float base_advance = 0.0f;
            {
                final SGDrawingElementString2D base_el = this.mBaseElementList.get(ii);
                // get visual ascent
                final float tmp_bva = -(float) base_el.getStringRect().getY();
                if (tmp_bva > base_visual_ascent)
                    base_visual_ascent = tmp_bva;
                // get visual descent
                final float tmp_bvd = (float) base_el.getStringRect()
                        .getHeight()
                        - tmp_bva;
                if (tmp_bvd > base_visual_descent)
                    base_visual_descent = tmp_bvd;
                // get advance of baselist
                base_advance = base_el.getAdvance();
            }

            // superscript list
            float super_advance = 0.0f;
            {
                final SGDrawingElementString2DExtended super_el
                	= (SGDrawingElementString2DExtended) this.mSuperscriptElementList.get(ii);
                if (super_el != null) {
                    // get height of superscript
                    final float tmp_sp_height = (float) super_el
                            .getStringRect().getHeight();
                    if (tmp_sp_height > super_height)
                        super_height = tmp_sp_height;
                    // get advance of superlist
                    super_advance = super_el.getAdvance();
                }
            }

            // subscript list
            float sub_advance = 0.0f;
            {
                final SGDrawingElementString2DExtended sub_el
                	= (SGDrawingElementString2DExtended) this.mSubscriptElementList.get(ii);
                if (sub_el != null) {
                    // get height of subscript
                    final float tmp_sb_height = (float) sub_el.getStringRect()
                            .getHeight();
                    if (tmp_sb_height > sub_height)
                        sub_height = tmp_sb_height;
                    // get advance of sublist
                    sub_advance = sub_el.getAdvance();
                }
            }

            // increase advance
            advance += base_advance
                    + ((super_advance > sub_advance) ? super_advance
                            : sub_advance);
        }

        final float margin = base_leading / 2.0f; // margin size between
                                                    // superscript and baseline

        // calculate ascent
        {
            final float center_top = base_visual_ascent
                    + base_strike_through_offset;
            final float tmp1 = super_height + margin;
            if (tmp1 > center_top)
                visual_ascent = tmp1 - base_strike_through_offset;
            else
                visual_ascent = center_top - base_strike_through_offset;
        }

        // calculate descent
        {
            final float center_bottom = base_visual_descent
                    - base_strike_through_offset;
            final float tmp2 = sub_height + margin;
            if (tmp2 > center_bottom)
                visual_descent = tmp2 + base_strike_through_offset;
            else
                visual_descent = center_bottom + base_strike_through_offset;
        }

        // calculate height
        visual_height = visual_ascent + visual_descent;

        // calculate string bounds
        visual_x = base_visual_x;
        visual_y = -visual_ascent;
        visual_advance = advance - visual_x; // FIXME: calculate right side
                                                // margin
        Rectangle2D rect = new Rectangle2D.Float(visual_x, visual_y,
                visual_advance, visual_height);

        // set class member variables
        this.mStrikethroughOffset = strike_through_offset;
        this.mLeading = base_leading;
        this.mAscent = visual_ascent;
        this.mDescent = visual_descent;
        this.mAdvance = advance;
        this.mStringRect = rect;
    }

    /**
     * Calculate the bounding box of this string element.
     */
    private void calcElementBounds() {
        final ArrayList<Rectangle2D> rectList = new ArrayList<Rectangle2D>();
        final ArrayList<SGDrawingElementString2D> list = this.getAllStringElement();
        for (int ii = 0; ii < list.size(); ii++) {
            SGDrawingElementString2D el = list.get(ii);
            rectList.add(el.getElementBounds());
        }

        // create a rectangle
        this.mElementBounds = SGUtility.createUnion(rectList);
    }

    /**
     * Returns whether this string element has subscripts.
     * 
     * @return true if this string element has subscripts
     */
    public boolean hasSubscript() {
        return this.hasElement(this.mSubscriptElementList);
    }
    
    /**
     * Returns whether this string element has superscripts.
     * 
     * @return true if this string element has superscripts
     */
    public boolean hasSuperscript() {
        return this.hasElement(this.mSuperscriptElementList);
    }
    
    private boolean hasElement(List<SGDrawingElementString2D> list) {
        for (int ii = 0; ii < list.size(); ii++) {
            if (list.get(ii) != null) {
                return true;
            }
        }
        return false;
    }
}

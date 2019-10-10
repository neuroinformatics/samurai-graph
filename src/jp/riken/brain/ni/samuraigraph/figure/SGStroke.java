package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.BasicStroke;
import java.util.HashMap;

/**
 * The class of the line stroke.
 *
 */
public class SGStroke implements Cloneable, SGILineConstants {

	// magnification
	private float mMagnification = 1.0f;
	
	// line width
	private float mLineWidth = 1.0f;
	
	// line type
	private int mLineType = SGILineConstants.LINE_TYPE_SOLID;

	// cap style
	private int mCap = BasicStroke.CAP_BUTT;

	// join style
	private int mJoin = BasicStroke.JOIN_BEVEL;

	// miter limit
	private float mMiterLimit = 1.0f;

	// dash phase
	private float mDashPhase = 0.0f;

	// map of stroke dash
	private HashMap<Integer, SGStrokeDash> mStrokeDashMap = new HashMap<Integer, SGStrokeDash>();

	// basic stroke object
	private BasicStroke mBasicStroke = null;
	
	/**
	 * Create a new Stroke object.
	 */
	public SGStroke() {
		super();
		this.init();
	}
	
	// set default dash patterns
	private void init() {
		
		// broken line
		SGStrokeDash broken = new SGStrokeDash(1, 0, 2.0f, 0.0f, 1.0f);
		
		// dotted line
		SGStrokeDash dot = new SGStrokeDash(1, 0, 1.0f, 0.0f, 1.0f);
		
		// dashed line
		SGStrokeDash dashed = new SGStrokeDash(1, 1, 4.0f, 1.0f, 1.0f);
		
		// double dashed line
		SGStrokeDash doubleDashed = new SGStrokeDash(1, 2, 4.0f, 1.0f, 1.0f);
		
		// put into the map
		this.mStrokeDashMap.put(Integer.valueOf(SGILineConstants.LINE_TYPE_BROKEN), broken);
		this.mStrokeDashMap.put(Integer.valueOf(SGILineConstants.LINE_TYPE_DOTTED), dot);
		this.mStrokeDashMap.put(Integer.valueOf(SGILineConstants.LINE_TYPE_DASHED), dashed);
		this.mStrokeDashMap.put(Integer.valueOf(SGILineConstants.LINE_TYPE_DOUBLE_DASHED), doubleDashed);
	}

	// Check whether the input line type is valid.
	private boolean isValidLineType(final int lineType) {
		if (lineType == SGILineConstants.LINE_TYPE_SOLID) {
			return true;
		} else {
			SGStrokeDash sd = (SGStrokeDash)this.mStrokeDashMap.get(Integer.valueOf(lineType));
			return (sd != null);
		}
	}
	
	/**
	 * Returns the magnification.
	 * @return  the magnification.
	 */
	public float getMagnification() {
		return this.mMagnification;
	}
	
	/**
	 * Returns the line width.
	 * @return  the line width.
	 */
	public float getLineWidth() {
		return mLineWidth;
	}

	/**
	 * Returns the line type;
	 * @return  the line type.
	 */
	public int getLineType() {
		return this.mLineType;
	}
	
	/**
	 * Returns the current dash phase.
	 * @return  the dash phase.
	 */
	public float getDashPhase() {
		return mDashPhase;
	}

	/**
	 * Returns the end cap style.
	 * @return  the end cap style.
	 */
	public int getEndCap() {
		return mCap;
	}

	/**
	 * Returns the line join style.
	 * @return  the line join style.
	 */
	public int getLineJoin() {
		return mJoin;
	}

	/**
	 * Returns the limit of miter joins.
	 * @return  the limit of miter joins.
	 */
	public float getMiterLimit() {
		return mMiterLimit;
	}

	// Returns the stroke dash object for the current line type.
	private SGStrokeDash getCurrentStrokeDash() {
		return this.getStrokeDash(this.mLineType);
	}

	/**
	 * Returns the stroke dash object.
	 * @param lineType  the type of line.
	 * @return  the stroke dash object.
	 */
	public SGStrokeDash getStrokeDash(final int lineType) {
		SGStrokeDash sd = (SGStrokeDash)this.mStrokeDashMap.get(Integer.valueOf(lineType));
		if (sd == null) {
			return null;
		}
		return (SGStrokeDash) sd.clone();
	}
	
	/**
	 * Set the magnification.
	 * @param mag  the magnification.
	 */
	public void setMagnification(final float mag) {
		if (mag <= 0.0f) {
			throw new IllegalArgumentException("Magnification is not positive: " + mag);
		}
		this.mMagnification = mag;
		this.updateBasicStroke();
	}

	/**
	 * Set the line type.
	 * @param lineType  the line type.
	 */
	public void setLineType(final int lineType) {
		if (!this.isValidLineType(lineType)) {
			throw new IllegalArgumentException("Illegal line type: " + lineType);
		}
		this.mLineType = lineType;
		this.updateBasicStroke();
	}
	
	/**
	 * Set the line width.
	 * @param lineWidth  the line width
	 */
	public void setLineWidth(final float lineWidth) {
		if (lineWidth < 0.0f) {
			throw new IllegalArgumentException("lineWidth < 0.0f :" + lineWidth);
		}
		mLineWidth = lineWidth;
		this.updateBasicStroke();
	}

	/**
	 * Set the dash phase.
	 * @param dashPhase  the dash phase.
	 */
	public void setDashPhase(final float dashPhase) {
		mDashPhase = dashPhase;
	}

	/**
	 * Set the end cap style.
	 * @param cap  the end cap style.
	 */
	public void setEndCap(final int cap) {
		if (cap != BasicStroke.CAP_BUTT && cap != BasicStroke.CAP_ROUND && cap != BasicStroke.CAP_SQUARE) {
			throw new IllegalArgumentException("The end cap style is illegal:" + cap);
		}
		mCap = cap;
	}

	/**
	 * Set the line join style.
	 * @param join  the line join style.
	 */
	public void setJoin(final int join) {
		if (join != BasicStroke.JOIN_BEVEL && join != BasicStroke.JOIN_MITER && join != BasicStroke.JOIN_ROUND) {
			throw new IllegalArgumentException("The line join style is illegal:" + join);
		}
		mJoin = join;
	}

	/**
	 * Set the limit of miter joins.
	 * @param miterLimit  the limit of miter joins.
	 */
	public void setMiterLimit(final float miterLimit) {
		mMiterLimit = miterLimit;
	}
	
	/**
	 * Returns the BasicStroke object.
	 * @return  the BasicStroke object.
	 */
	public BasicStroke getBasicStroke() {
		return this.mBasicStroke;
	}

	// Updates the array representing the lengths of the dash segments.
	private void updateBasicStroke() {
		
		// line width
		final float lw = this.mLineWidth * this.mMagnification;
		
		// for the solid line
		if (this.mLineType == SGILineConstants.LINE_TYPE_SOLID) {
			this.mBasicStroke = new BasicStroke(lw, this.mCap, this.mJoin);
			return;
		}
		
		// get parameters from stroke dash object
		SGStrokeDash sd = this.getCurrentStrokeDash();
		final int lineNum1 = sd.getLineNum1();
		final int lineNum2 = sd.getLineNum2();
		final float lineLen1 = sd.getLineLen1();
		final float lineLen2 = sd.getLineLen2();
		final float space = sd.getSpace();
		final int total = lineNum1 + lineNum2;
		
		// create basic stroke object
		float[] dash = new float[2 * total];
		for (int ii = 0; ii < lineNum1; ii++) {
			dash[2 * ii] = lineLen1 * lw;
			dash[2 * ii + 1] = space * lw;
		}
		for (int ii = lineNum1; ii < total; ii++) {
			dash[2 * ii] = lineLen2 * lw;
			dash[2 * ii + 1] = space * lw;
		}
		this.mBasicStroke = new BasicStroke(
				lw, this.mCap, this.mJoin, this.mMiterLimit, dash, this.mDashPhase);
	}
	
	/**
	 * Set the number of the first line.
	 * @param lineType  the type of line.
	 * @param num  the number of the first line.
	 */
	public void setLineNumber1(final int lineType, final int num) {
		SGStrokeDash sd = this.getStrokeDash(lineType);
		if (sd == null) {
			throw new IllegalArgumentException("Illegal line type: " + lineType);
		}
		sd.setLineNum1(num);
		this.updateBasicStroke();
	}
	
	/**
	 * Set the number of the second line.
	 * @param lineType  the type of line.
	 * @param num  the number of the second line.
	 */
	public void setLineNumber2(final int lineType, final int num) {
		SGStrokeDash sd = this.getStrokeDash(lineType);
		if (sd == null) {
			throw new IllegalArgumentException("Illegal line type: " + lineType);
		}
		sd.setLineNum2(num);
		this.updateBasicStroke();
	}
	
	/**
	 * Set the length of the first line.
	 * @param lineType  the type of line.
	 * @param len  the length of the first line.
	 */
	public void setLineLength1(final int lineType, final float len) {
		SGStrokeDash sd = this.getStrokeDash(lineType);
		if (sd == null) {
			throw new IllegalArgumentException("Illegal line type: " + lineType);
		}
		sd.setLineLen1(len);
		this.updateBasicStroke();
	}
	
	/**
	 * Set the length of the second line.
	 * @param lineType  the type of line.
	 * @param len  the length of the second line.
	 */
	public void setLineLength2(final int lineType, final float len) {
		SGStrokeDash sd = this.getStrokeDash(lineType);
		if (sd == null) {
			throw new IllegalArgumentException("Illegal line type: " + lineType);
		}
		sd.setLineLen2(len);
		this.updateBasicStroke();
	}
	
	/**
	 * Set the space between lines.
	 * @param lineType  the type of line.
	 * @param space  the space between lines.
	 */
	public void setSpace(final int lineType, final float space) {
		SGStrokeDash sd = this.getStrokeDash(lineType);
		if (sd == null) {
			throw new IllegalArgumentException("Illegal line type: " + lineType);
		}
		sd.setSpace(space);
		this.updateBasicStroke();
	}
	
	/**
	 * Returns the number of the first line segments.
	 * @param lineType  the type of line.
	 * @return  the number of the first line segments.
	 */
	public int getLineNum1(final int lineType) {
		SGStrokeDash sd = this.getStrokeDash(lineType);
		if (sd == null) {
			throw new IllegalArgumentException("Illegal line type: " + lineType);
		}
		return sd.getLineNum1();
	}

	/**
	 * Returns the number of the second line segments.
	 * @param lineType  the type of line.
	 * @return  the number of the second line segments.
	 */
	public int getLineNum2(final int lineType) {
		SGStrokeDash sd = this.getStrokeDash(lineType);
		if (sd == null) {
			throw new IllegalArgumentException("Illegal line type: " + lineType);
		}
		return sd.getLineNum2();
	}

	/**
	 * Returns the length of the first line segments.
	 * @param lineType  the type of line.
	 * @return  the length of the first line segments.
	 */
	public float getLineLength1(final int lineType) {
		SGStrokeDash sd = this.getStrokeDash(lineType);
		if (sd == null) {
			throw new IllegalArgumentException("Illegal line type: " + lineType);
		}
		return sd.getLineLen1();
	}

	/**
	 * Returns the length of the second line segments.
	 * @param lineType  the type of line.
	 * @return  the length of the second line segments.
	 */
	public float getLineLength2(final int lineType) {
		SGStrokeDash sd = this.getStrokeDash(lineType);
		if (sd == null) {
			throw new IllegalArgumentException("Illegal line type: " + lineType);
		}
		return sd.getLineLen2();
	}

	/**
	 * Returns the space between line segments.
	 * @param lineType  the type of line.
	 * @return  the space between line segments.
	 */
	public float getSpace(final int lineType) {
		SGStrokeDash sd = this.getStrokeDash(lineType);
		if (sd == null) {
			throw new IllegalArgumentException("Illegal line type: " + lineType);
		}
		return sd.getSpace();
	}

    /**
     * Clones this object.
     * 
     * @return the copy of this object
     */
    public Object clone() {
    	SGStroke stroke = null;
        try {
            stroke = (SGStroke) super.clone();
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
        return stroke;
    }

}

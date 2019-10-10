package jp.riken.brain.ni.samuraigraph.figure;

/**
 * A class of the stroke dash of a line.
 * @author kuromaru
 *
 */
public class SGStrokeDash implements Cloneable {

	// the number of the first line segments
	private int mLineNum1;
	
	// the number of the second line segments
	private int mLineNum2;
	
	// the length of the first line segments
	private float mLineLen1;
	
	// the length of the second line segments
	private float mLineLen2;
	
	// the space between line segments
	private float mSpace;

	/**
	 * Construct a stroke dash object.
	 * @param lineNum1
	 *          The number of the first line segments.
	 * @param lineNum2
	 *          The number of the second line segments.
	 * @param lineLen1
	 *          The length of the first line segments.
	 * @param lineLen2
	 *          The length of the second line segments.
	 * @param space
	 *          The space between line segments.
	 */
	public SGStrokeDash(int lineNum1, int lineNum2, float lineLen1, float lineLen2, float space) {
		super();
		this.setLineNum1(lineNum1);
		this.setLineNum2(lineNum2);
		this.setLineLen1(lineLen1);
		this.setLineLen2(lineLen2);
		this.setSpace(space);
	}

	/**
	 * Returns the length of the first line segments.
	 * @return  the length of the first line segments.
	 */
	public float getLineLen1() {
		return mLineLen1;
	}

	/**
	 * Returns the length of the second line segments.
	 * @return  the length of the second line segments.
	 */
	public float getLineLen2() {
		return mLineLen2;
	}

	/**
	 * Returns the number of the first line segments.
	 * @return  the number of the first line segments.
	 */
	public int getLineNum1() {
		return mLineNum1;
	}

	/**
	 * Returns the number of the second line segments.
	 * @return  the number of the second line segments.
	 */
	public int getLineNum2() {
		return mLineNum2;
	}

	/**
	 * Returns the space between lines.
	 * @return  the space between lines.
	 */
	public float getSpace() {
		return mSpace;
	}

	/**
	 * Set the number of the first line.
	 * @param num  the number of the first line.
	 */
	public void setLineNum1(int num) {
		if (num < 0) {
			throw new IllegalArgumentException("num < 0 :" + num);
		}
		mLineNum1 = num;
	}

	/**
	 * Set the number of the second line.
	 * @param num  the number of the second line.
	 */
	public void setLineNum2(int num) {
		if (num < 0) {
			throw new IllegalArgumentException("num < 0 :" + num);
		}
		mLineNum2 = num;
	}
	
	/**
	 * Set the length of the first line.
	 * @param len  the length of the first line.
	 */
	public void setLineLen1(float len) {
		if (len < 0.0f) {
			throw new IllegalArgumentException("len < 0.0f :" + len);
		}
		mLineLen1 = len;
	}

	/**
	 * Set the length of the second line.
	 * @param len  the length of the second line.
	 */
	public void setLineLen2(float len) {
		if (len < 0.0f) {
			throw new IllegalArgumentException("len < 0.0f :" + len);
		}
		mLineLen2 = len;
	}

	/**
	 * Set the space between lines.
	 * @param space  the space between lines.
	 */
	public void setSpace(float space) {
		if (space < 0.0f) {
			throw new IllegalArgumentException("space < 0.0f :" + space);
		}
		mSpace = space;
	}
	
	/**
	 * Clone this stroke dash.
	 * @return  copied object.
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}
}

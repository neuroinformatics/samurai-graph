package jp.riken.brain.ni.samuraigraph.figure.java2d;


/**
 * An observer of the property dialog for line stroke.
 */
public interface SGIStrokeEditDialogObserver {

	/**
	 * Set the number of the first line.
	 * @param num  the number of the first line.
	 */
	public void setLineNumber1(int num);
	
	/**
	 * Set the number of the second line.
	 * @param num  the number of the second line.
	 */
	public void setLineNumber2(int num);
	
	/**
	 * Set the length of the first line.
	 * @param len  the length of the first line.
	 */
	public void setLineLength1(float len);
	
	/**
	 * Set the length of the second line.
	 * @param len  the length of the second line.
	 */
	public void setLineLength2(float len);
	
	/**
	 * Set the space between lines.
	 * @param space  the space between lines.
	 */
	public void setSpace(float space);

	/**
	 * Returns the number of the first line segments.
	 * @return  the number of the first line segments.
	 */
	public int getLineNum1();

	/**
	 * Returns the number of the second line segments.
	 * @return  the number of the second line segments.
	 */
	public int getLineNum2();

	/**
	 * Returns the length of the first line segments.
	 * @return  the length of the first line segments.
	 */
	public float getLineLength1();

	/**
	 * Returns the length of the second line segments.
	 * @return  the length of the second line segments.
	 */
	public float getLineLength2();

	/**
	 * Returns the space between line segments.
	 * @return  the space between line segments.
	 */
	public float getSpace();

}

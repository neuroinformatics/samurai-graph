package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGCommandUtility;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxisConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIMovable;
import jp.riken.brain.ni.samuraigraph.base.SGINode;
import jp.riken.brain.ni.samuraigraph.base.SGIUndoable;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUndoManager;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility.MouseDragResult;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementScale.ScaleProperties;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementString;
import jp.riken.brain.ni.samuraigraph.figure.SGIScaleConstants;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGAxisElement.AxisScaleChangeListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

class SGAxisScale extends SGDrawingElementScale2D implements SGIMovable,
		SGIScaleConstants, SGIFigureElementConstants, SGIUndoable, AxisScaleChangeListener,
		SGIAxisScaleDialogObserver, ActionListener, SGIFigureElementAxisConstants,
		SGINode {

	private SGAxis mXAxis = null;

	private SGAxis mYAxis = null;
	
	private double mXValue = 0.0;
	
	private double mYValue = 0.0;
	
	private double mXLengthValue = 0.0;

	private double mYLengthValue = 0.0;
	
	private boolean mXInvertedFlag = false;

	private boolean mYInvertedFlag = false;
	
	private AXIS_LENGTH_MODE mAxisLengthMode = AXIS_LENGTH_MODE.LENGTH_FIXED;

	/**
	 * Flag whether this object is focused.
	 */
	private boolean mSelectedFlag = false;

	private SGFigureElementAxis mAxisElement = null;

	private SGProperties mTemporaryProperties = null;
	
	private SGAxisScale mTempSymbol = null;

    /**
     * The pop-up menu.
     */
    private JPopupMenu mPopupMenu = null;

	SGAxisScale(SGFigureElementAxis aElement) {
		super();
		
		this.mAxisElement = aElement;
		
        SGAxis xAxis = this.mAxisElement.getAxis(DEFAULT_SCALE_HORIZONTAL_AXIS);
        SGAxis yAxis = this.mAxisElement.getAxis(DEFAULT_SCALE_VERTICAL_AXIS);
        this.mXAxis = xAxis;
        this.mYAxis = yAxis;

		this.setXAxisTitleDownside(true);
		this.setYAxisTitleLeftside(true);

		this.setLineWidth(DEFAULT_SCALE_SYMBOL_LINE_WIDTH, LINE_WIDTH_UNIT);
		this.setLineColor(DEFAULT_SCALE_SYMBOL_COLOR);
		this.setSpace(DEFAULT_SCALE_SYMBOL_SPACE, SCALE_SPACE_UNIT);

		this.setFontSize(DEFAULT_SCALE_SYMBOL_FONT_SIZE, FONT_SIZE_UNIT);
		this.setFontName(DEFAULT_SCALE_SYMBOL_FONT_NAME);
		this.setFontStyle(DEFAULT_SCALE_SYMBOL_FONT_STYLE);
		this.setFontColor(DEFAULT_SCALE_SYMBOL_COLOR);
		this.setTextAngle(0.0f);
		
		this.mAxisLengthMode = DEFAULT_AXIS_CHANGE_MODE;
	}
	
	public SGAxis getXAxis() {
		return this.mXAxis;
	}
	
	public SGAxis getYAxis() {
		return this.mYAxis;
	}

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

	public void dispose() {
		super.dispose();

		if (this.mTemporaryProperties != null) {
			this.mTemporaryProperties.dispose();
			this.mTemporaryProperties = null;
		}

		this.mTempSymbol = null;

		this.mXAxis = null;
		this.mYAxis = null;
	}

	@Override
	public int getXAxisLocation() {
		return mAxisElement.getLocationInPlane(this.mXAxis);
	}

	@Override
	public int getYAxisLocation() {
		return mAxisElement.getLocationInPlane(this.mYAxis);
	}

	/**
	 * Sets the x-axis.
	 * 
	 * @param location
	 *            the axis location
	 * @return true if succeeded
	 */
	@Override
	public boolean setXAxisLocation(final int location) {
		if (location != SGIFigureElementAxis.AXIS_HORIZONTAL_1
				&& location != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
			return false;
		}
		SGAxis axis = mAxisElement.getAxisInPlane(location);
		if (axis == null) {
			return false;
		}
		this.mXAxis = axis;
		return true;
	}

	/**
	 * Sets the y-axis.
	 * 
	 * @param location
	 *            the axis location
	 * @return true if succeeded
	 */
	@Override
	public boolean setYAxisLocation(final int location) {
		if (location != SGIFigureElementAxis.AXIS_VERTICAL_1
				&& location != SGIFigureElementAxis.AXIS_VERTICAL_2) {
			return false;
		}
		SGAxis axis = mAxisElement.getAxisInPlane(location);
		if (axis == null) {
			return false;
		}
		this.mYAxis = axis;
		return true;
	}

	@Override
	public boolean setHorizontalText(final String text) {
		if (this.isValidText(text)) {
			this.mHorizontalText = text;
		} else {
			this.mHorizontalText = null;
			this.updateNumberLabelWithLocation(true);
		}
		this.setToStringElement(this.mHorizontalStringElement, text);
		return true;
	}

	@Override
	public boolean setVerticalText(final String text) {
		if (this.isValidText(text)) {
			this.mVerticalText = text;
		} else {
			this.mVerticalText = null;
			this.updateNumberLabelWithLocation(false);
		}
		this.setToStringElement(this.mVerticalStringElement, text);
		return true;
	}

	private void setToStringElement(SGDrawingElementString el, String text) {
    	String str = (text == null) ? "" : text;
        el.setString(str);
	}

	@Override
	public boolean readProperty(final Element el) {
		if (super.readProperty(el) == false) {
			return false;
		}

		String str = null;
		Number num = null;

		// x axis
		str = el.getAttribute(KEY_X_AXIS_POSITION);
		if (str.length() != 0) {
			SGAxis xAxis = mAxisElement.getAxis(str);
			if (xAxis == null) {
				return false;
			}
			this.mXAxis = xAxis;
		}

		// y axis
		str = el.getAttribute(KEY_Y_AXIS_POSITION);
		if (str.length() != 0) {
			SGAxis yAxis = mAxisElement.getAxis(str);
			if (yAxis == null) {
				return false;
			}
			this.mYAxis = yAxis;
		}

		// x value
		str = el.getAttribute(KEY_SCALE_X_VALUE);
		if (str.length() != 0) {
			num = SGUtilityText.getDouble(str);
			if (num == null) {
				return false;
			}
			final double value = num.doubleValue();
			this.setXValue(value);
		}

		// y value
		str = el.getAttribute(KEY_SCALE_Y_VALUE);
		if (str.length() != 0) {
			num = SGUtilityText.getDouble(str);
			if (num == null) {
				return false;
			}
			final double value = num.doubleValue();
			this.setYValue(value);
		}
		
		// x length value
		str = el.getAttribute(KEY_SCALE_X_AXIS_LENGTH_VALUE);
		if (str.length() != 0) {
			num = SGUtilityText.getDouble(str);
			if (num == null) {
				return false;
			}
			final double value = num.doubleValue();
			this.setXLengthValue(value);
		}

		// y length value
		str = el.getAttribute(KEY_SCALE_Y_AXIS_LENGTH_VALUE);
		if (str.length() != 0) {
			num = SGUtilityText.getDouble(str);
			if (num == null) {
				return false;
			}
			final double value = num.doubleValue();
			this.setYLengthValue(value);
		}
		
		// axis length mode
		str = el.getAttribute(KEY_SCALE_AXIS_LENGTH_MODE);
		if (str.length() != 0) {
			AXIS_LENGTH_MODE mode = this.toMode(str);
			if (mode == null) {
				return false;
			}
			this.setAxisLengthMode(mode);
		}

		if (this.updateSizeWithAxisValues() == false) {
			return false;
		}
		
		return true;
	}

	@Override
	public SGProperties getProperties() {
		ScaleProperties p = new AxisScaleProperties();
		if (this.getProperties(p) == false) {
			return null;
		}
		return p;
	}

	@Override
	public boolean getProperties(SGProperties p) {
		if ((p instanceof AxisScaleProperties) == false) {
			return false;
		}
		if (super.getProperties(p) == false) {
			return false;
		}
		AxisScaleProperties sp = (AxisScaleProperties) p;
		sp.mXAxis = this.mXAxis;
		sp.mYAxis = this.mYAxis;
		sp.mXValue = this.mXValue;
		sp.mYValue = this.mYValue;
		sp.mXLengthValue = this.mXLengthValue;
		sp.mYLengthValue = this.mYLengthValue;
		sp.mAxisLengthMode = this.mAxisLengthMode;
		return true;
	}

	@Override
	public boolean setProperties(final SGProperties p) {
		if ((p instanceof AxisScaleProperties) == false) {
			return false;
		}
		if (super.setProperties(p) == false) {
			return false;
		}
		AxisScaleProperties sp = (AxisScaleProperties) p;
		this.mXAxis = sp.mXAxis;
		this.mYAxis = sp.mYAxis;
		this.mXValue = sp.mXValue;
		this.mYValue = sp.mYValue;
		this.mXLengthValue = sp.mXLengthValue;
		this.mYLengthValue = sp.mYLengthValue;
		this.mAxisLengthMode = sp.mAxisLengthMode;
		return true;
	}
	
	/**
	 * Sets the location of this symbol.
	 * 
	 * @param x
	 *            the x coordinate to set
	 * @param y
	 *            the y coordinate to set
	 * @return true if succeeded
	 */
	@Override
	public boolean setLocation(final float x, final float y) {
		final float mag = this.getMagnification();
		final float nx = (x - this.mAxisElement.getGraphRectX()) / mag;
		final float ny = (y - this.mAxisElement.getGraphRectY()) / mag;
		super.setLocation(nx, ny);
		return true;
	}
	
	/**
	 * Translates this object.
	 * 
	 * @param dx
	 *            the x-axis displacement
	 * @param dy
	 *            the y-axis displacement
	 */
	@Override
	public void translate(final float dx, final float dy) {
		if (this.mMouseLocation == BODY
				|| this.mMouseLocation == ON_HORIZONTAL_STRING
				|| this.mMouseLocation == ON_VERTICAL_STRING
				|| this.mMouseLocation == -1) {
			this.setLocation(this.getX() + dx, this.getY() + dy);
			this.updateAxisValuesWithShape();
			this.updateSizeWithAxisValues();
		}
	}

	private boolean pressDrawingElements(final MouseEvent e) {
		final int x = e.getX();
		final int y = e.getY();

		if (this.contains(x, y)) {
			final float mag = this.getMagnification();

			// set the temporary object
			this.mTempSymbol = new SGAxisScale(this.mAxisElement);
			this.mTempSymbol.mAxisLengthMode = this.mAxisLengthMode;
			this.mTempSymbol.setMagnification(mag);
			this.mTempSymbol.setLocation(this.getX(), this.getY());
			this.mTempSymbol.setSize(this.getWidth(), this.getHeight());

			this.mMouseLocation = this.getMouseLocation(x, y);
			Cursor cur = null;
			if (this.mMouseLocation == BODY
					|| this.mMouseLocation == ON_HORIZONTAL_STRING
					|| this.mMouseLocation == ON_VERTICAL_STRING) {
				cur = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
			} else {
				this.getCursor(this.mMouseLocation);
			}
			mAxisElement.setMouseCursor(cur);

			return true;
		}

		return false;
	}

	ArrayList<Point2D> getAnchorPointList() {
		final boolean hVisible = this.isHorizontalVisible();
		final boolean vVisible = this.isVerticalVisible();
		ArrayList<Point2D> list = new ArrayList<Point2D>();
		if (hVisible) {
			list.add(this.getHorizontalMiddle());
			list.add(this.getHorizontalEnd());
		}
		if (vVisible) {
			list.add(this.getVerticalMiddle());
			list.add(this.getVerticalEnd());
		}
		if (hVisible || vVisible) {
			list.add(this.getJoint());
		}
		return list;
	}

	/**
	 * Location of mouse pointer.
	 */
	private int mMouseLocation;

	private boolean isInside(final Point2D pos, final int radius, final int x,
			final int y) {
		return ((Math.abs(pos.getX() - x) < radius) && (Math
				.abs(pos.getY() - y) < radius));
	}

	private int getMouseLocation(final int x, final int y) {
		final int radius = (int) (1.25f * ANCHOR_SIZE_FOR_FOCUSED_OBJECTS);

		final Point2D posHorizontalMiddle = this.getHorizontalMiddle();
		final Point2D posVerticalMiddle = this.getVerticalMiddle();
		final Point2D posJoint = this.getJoint();
		final Point2D posHorizontalEnd = this.getHorizontalEnd();
		final Point2D posVerticalEnd = this.getVerticalEnd();

		int location = -1;
		if (this.isInside(posHorizontalMiddle, radius, x, y)) {
			location = HORIZONTAL_MIDDLE;
		} else if (this.isInside(posVerticalMiddle, radius, x, y)) {
			location = VERTICAL_MIDDLE;
		} else if (this.isInside(posJoint, radius, x, y)) {
			location = JOINT;
		} else if (this.isInside(posHorizontalEnd, radius, x, y)) {
			location = HORIZONTAL_END;
		} else if (this.isInside(posVerticalEnd, radius, x, y)) {
			location = VERTICAL_END;
		} else {
			location = BODY;
		}
		if (this.getHorizontalStringElement().contains(x, y)) {
			location = ON_HORIZONTAL_STRING;
		}
		if (this.getVerticalStringElement().contains(x, y)) {
			location = ON_VERTICAL_STRING;
		}

		return location;
	}

	private Cursor getCursor(final int location) {
		Cursor cur = null;
		switch (location) {
		case HORIZONTAL_MIDDLE:
			if (!this.isFlippingVertical()) {
				cur = new Cursor(Cursor.N_RESIZE_CURSOR);
			} else {
				cur = new Cursor(Cursor.S_RESIZE_CURSOR);
			}
			break;
		case VERTICAL_MIDDLE:
			if (!this.isFlippingHorizontal()) {
				cur = new Cursor(Cursor.W_RESIZE_CURSOR);
			} else {
				cur = new Cursor(Cursor.E_RESIZE_CURSOR);
			}
			break;
		case JOINT:
			if (this.isFlippingVertical() == this.isFlippingHorizontal()) {
				cur = new Cursor(Cursor.SW_RESIZE_CURSOR);
			} else {
				cur = new Cursor(Cursor.NW_RESIZE_CURSOR);
			}
			break;
		case HORIZONTAL_END:
			if (this.isFlippingVertical() == this.isFlippingHorizontal()) {
				cur = new Cursor(Cursor.SE_RESIZE_CURSOR);
			} else {
				cur = new Cursor(Cursor.NE_RESIZE_CURSOR);
			}
			break;
		case VERTICAL_END: {
			if (this.isFlippingVertical() == this.isFlippingHorizontal()) {
				cur = new Cursor(Cursor.NW_RESIZE_CURSOR);
			} else {
				cur = new Cursor(Cursor.SW_RESIZE_CURSOR);
			}
			break;
		}
		case ON_HORIZONTAL_STRING:
		case ON_VERTICAL_STRING:
		default:
			cur = new Cursor(Cursor.HAND_CURSOR);
		}
		return cur;
	}

	// parallel displacement
	private boolean dragOtherPoint(final MouseEvent e) {
		// set the location to the symbol
		MouseDragResult result = mAxisElement.getMouseDragResult(e);
		final int dx = result.dx;
		final int dy = result.dy;
		this.translate(dx, dy);
		mAxisElement.setPressedPoint(e.getPoint());
		return true;
	}

	private boolean drag(final MouseEvent e) {

		if (mAxisElement.getPressedPoint() == null) {
			return false;
		}

		if (this.mMouseLocation == BODY
				|| this.mMouseLocation == ON_HORIZONTAL_STRING
				|| this.mMouseLocation == ON_VERTICAL_STRING) {
			if (this.isSelected()) {
				// do nothing
				return true;
			} else {
				return this.dragOtherPoint(e);
			}
		}

		final float mag = this.getMagnification();

		SGAxisScale temp = this.mTempSymbol;
		final float xOld = temp.getX();
		final float yOld = temp.getY();
		final float wOld = mag * temp.getWidth();
		final float hOld = mag * temp.getHeight();

		MouseDragResult result = mAxisElement.getMouseDragResult(e);
		final int diffX = result.dx;
		final int diffY = result.dy;

		final float sizeOldX = wOld;
		final float sizeOldY = hOld;

		float sizeNewX = 0.0f;
		float sizeNewY = 0.0f;

		// new values
		float x = xOld;
		float y = yOld;
		float w = wOld;
		float h = hOld;

		// switch operation by different dragging point

		final int loc = this.mMouseLocation;
		if (loc == HORIZONTAL_MIDDLE) {
			sizeNewY = sizeOldY - diffY;

			y = yOld + sizeOldY - sizeNewY;
			h = sizeNewY;
		} else if (loc == VERTICAL_MIDDLE) {
			sizeNewX = sizeOldX - diffX;
			
			x = xOld + sizeOldX - sizeNewX;
			w = sizeNewX;
		} else if (loc == JOINT) {
			sizeNewX = sizeOldX - diffX;
			sizeNewY = sizeOldY - diffY;

			x = xOld + sizeOldX - sizeNewX;
			y = yOld + sizeOldY - sizeNewY;
			w = sizeNewX;
			h = sizeNewY;
		} else if (loc == HORIZONTAL_END) {
			sizeNewX = sizeOldX + diffX;
			sizeNewY = sizeOldY - diffY;
			
			y = yOld + sizeOldY - sizeNewY;
			w = sizeNewX;
			h = sizeNewY;
		} else if (loc == VERTICAL_END) {
			sizeNewX = sizeOldX - diffX;
			sizeNewY = sizeOldY + diffY;

			x = xOld + sizeOldX - sizeNewX;
			w = sizeNewX;
			h = sizeNewY;
		}

		// update the pressed point
		Point pos = mAxisElement.getPressedPoint();
		mAxisElement.setPressedPointLocation(pos.getX() + diffX, pos.getY()
				+ diffY);

		// set properties
		this.setLocation(x, y);
		this.setSize(w / mag, h / mag);
		this.updateAxisValuesWithShape();
		this.updateSizeWithAxisValues();

		temp.setLocation(x, y);
		temp.setSize(w / mag, h / mag);
		temp.updateAxisValuesWithShape();
		temp.updateSizeWithAxisValues();

		if (loc == JOINT || loc == HORIZONTAL_END || loc == VERTICAL_END) {
			final int location = this.getMouseLocation(e.getX(), e.getY());
			mAxisElement.setMouseCursor(this.getCursor(location));
		}

		return true;
	}

	private boolean mValidFlag = true;

	public boolean isValid() {
		return this.mValidFlag;
	}

	public void setValid(final boolean b) {
		this.mValidFlag = b;
	}

	@Override
	public float getMagnification() {
		return mAxisElement.getMagnification();
	}

	public String getCommandString() {
		StringBuffer sb = new StringBuffer();
		String scaleCommands = SGCommandUtility.createCommandString(COM_SCALE, 
				null, this.getCommandPropertyMap(null));
		sb.append(scaleCommands);
		return sb.toString();
	}
	
	/**
	 * Sets the properties.
	 * 
	 * @param map
	 *            a map of properties
	 * @return the result of setting properties
	 */
	public SGPropertyResults setProperties(SGPropertyMap map) {
		SGPropertyResults result = new SGPropertyResults();

		 // prepare
		 if (this.prepare() == false) {
			 return null;
		 }

		Iterator<String> itr = map.getKeyIterator();
		while (itr.hasNext()) {
			String key = itr.next();
			String value = map.getValueString(key);

			if (COM_SCALE_VISIBLE.equalsIgnoreCase(key)) {
				final Boolean b = SGUtilityText.getBoolean(value);
				if (b == null) {
					result.putResult(COM_SCALE_VISIBLE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				this.setVisible(b);
				result.putResult(COM_SCALE_VISIBLE, SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_X_AXIS.equalsIgnoreCase(key)) {
				final int loc = SGUtility.getAxisLocation(value);
				if (loc == -1) {
					result.putResult(COM_SCALE_X_AXIS,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setXAxisLocation(loc) == false) {
					result.putResult(COM_SCALE_X_AXIS,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_SCALE_X_AXIS, SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_Y_AXIS.equalsIgnoreCase(key)) {
				final int loc = SGUtility.getAxisLocation(value);
				if (loc == -1) {
					result.putResult(COM_SCALE_Y_AXIS,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setYAxisLocation(loc) == false) {
					result.putResult(COM_SCALE_Y_AXIS,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_SCALE_Y_AXIS, SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_LOCATION_X.equalsIgnoreCase(key)) {
				Double num = SGUtilityText.getDouble(value);
				if (num == null) {
					result.putResult(COM_SCALE_LOCATION_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
					result.putResult(COM_SCALE_LOCATION_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setXValue(num.doubleValue()) == false) {
					result.putResult(COM_SCALE_LOCATION_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_SCALE_LOCATION_X,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_LOCATION_Y.equalsIgnoreCase(key)) {
				Double num = SGUtilityText.getDouble(value);
				if (num == null) {
					result.putResult(COM_SCALE_LOCATION_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
					result.putResult(COM_SCALE_LOCATION_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setYValue(num.doubleValue()) == false) {
					result.putResult(COM_SCALE_LOCATION_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_SCALE_LOCATION_Y,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_X_AXIS_VISIBLE.equalsIgnoreCase(key)) {
				final Boolean b = SGUtilityText.getBoolean(value);
				if (b == null) {
					result.putResult(COM_SCALE_X_AXIS_VISIBLE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				this.setHorizontalVisible(b);
				result.putResult(COM_SCALE_X_AXIS_VISIBLE, SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_X_AXIS_LENGTH.equalsIgnoreCase(key)) {
				Double num = SGUtilityText.getDouble(value);
				if (num == null) {
					result.putResult(COM_SCALE_X_AXIS_LENGTH,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setXLengthValue(num.doubleValue()) == false) {
					result.putResult(COM_SCALE_X_AXIS_LENGTH,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_SCALE_X_AXIS_LENGTH, SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_X_AXIS_TITLE_DOWNSIDE.equalsIgnoreCase(key)) {
				final Boolean b = SGUtilityText.getBoolean(value);
				if (b == null) {
					result.putResult(COM_SCALE_X_AXIS_TITLE_DOWNSIDE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				this.setHorizontalTextDownside(b);
				result.putResult(COM_SCALE_X_AXIS_TITLE_DOWNSIDE, SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_X_AXIS_TITLE_TEXT.equalsIgnoreCase(key)) {
				if (map.isDoubleQuoted(key) == false) {
					result.putResult(COM_SCALE_X_AXIS_TITLE_TEXT,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setHorizontalText(value) == false) {
					result.putResult(COM_SCALE_X_AXIS_TITLE_TEXT,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_SCALE_X_AXIS_TITLE_TEXT,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_X_AXIS_TITLE_UNIT.equalsIgnoreCase(key)) {
				if (map.isDoubleQuoted(key) == false) {
					result.putResult(COM_SCALE_X_AXIS_TITLE_UNIT,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setHorizontalUnit(value) == false) {
					result.putResult(COM_SCALE_X_AXIS_TITLE_UNIT,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_SCALE_X_AXIS_TITLE_UNIT,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_Y_AXIS_VISIBLE.equalsIgnoreCase(key)) {
				final Boolean b = SGUtilityText.getBoolean(value);
				if (b == null) {
					result.putResult(COM_SCALE_Y_AXIS_VISIBLE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				this.setVerticalVisible(b);
				result.putResult(COM_SCALE_Y_AXIS_VISIBLE, SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_Y_AXIS_LENGTH.equalsIgnoreCase(key)) {
				Double num = SGUtilityText.getDouble(value);
				if (num == null) {
					result.putResult(COM_SCALE_Y_AXIS_LENGTH,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setYLengthValue(num.doubleValue()) == false) {
					result.putResult(COM_SCALE_Y_AXIS_LENGTH,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_SCALE_Y_AXIS_LENGTH, SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_Y_AXIS_TITLE_LEFTSIDE.equalsIgnoreCase(key)) {
				final Boolean b = SGUtilityText.getBoolean(value);
				if (b == null) {
					result.putResult(COM_SCALE_Y_AXIS_TITLE_LEFTSIDE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				this.setVerticalTextLeftside(b);
				result.putResult(COM_SCALE_Y_AXIS_TITLE_LEFTSIDE, SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_Y_AXIS_TITLE_TEXT.equalsIgnoreCase(key)) {
				if (map.isDoubleQuoted(key) == false) {
					result.putResult(COM_SCALE_Y_AXIS_TITLE_TEXT,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setVerticalText(value) == false) {
					result.putResult(COM_SCALE_Y_AXIS_TITLE_TEXT,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_SCALE_Y_AXIS_TITLE_TEXT,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_Y_AXIS_TITLE_UNIT.equalsIgnoreCase(key)) {
				if (map.isDoubleQuoted(key) == false) {
					result.putResult(COM_SCALE_Y_AXIS_TITLE_UNIT,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setVerticalUnit(value) == false) {
					result.putResult(COM_SCALE_Y_AXIS_TITLE_UNIT,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_SCALE_Y_AXIS_TITLE_UNIT,
						SGPropertyResults.SUCCEEDED);
//			} else if (COM_SCALE_AXIS_LENGTH_MODE.equalsIgnoreCase(key)) {
//				AXIS_LENGTH_MODE mode = this.toMode(value);
//				if (mode == null) {
//					result.putResult(COM_SCALE_AXIS_LENGTH_MODE,
//							SGPropertyResults.INVALID_INPUT_VALUE);
//					continue;
//				}
//				if (this.setAxisLengthMode(mode) == false) {
//					result.putResult(COM_SCALE_AXIS_LENGTH_MODE,
//							SGPropertyResults.INVALID_INPUT_VALUE);
//					continue;
//				}
//				result.putResult(COM_SCALE_AXIS_LENGTH_MODE,
//						SGPropertyResults.SUCCEEDED);
			} else if (COM_SPACE.equalsIgnoreCase(key)) {
				StringBuffer unit = new StringBuffer();
				Number num = SGUtilityText.getNumber(value, unit);
				if (num == null) {
					result.putResult(COM_SPACE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setSpace(num.floatValue(), unit.toString()) == false) {
					result.putResult(COM_SPACE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_SPACE, SGPropertyResults.SUCCEEDED);
			} else if (COM_LINE_WIDTH.equalsIgnoreCase(key)) {
				StringBuffer unit = new StringBuffer();
				Number num = SGUtilityText.getNumber(value, unit);
				if (num == null) {
					result.putResult(COM_LINE_WIDTH,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setLineWidth(num.floatValue(), unit.toString()) == false) {
					result.putResult(COM_LINE_WIDTH,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_LINE_COLOR.equalsIgnoreCase(key)) {
				Color cl = SGUtilityText.getColor(value);
				if (cl != null) {
					if (this.setLineColor(cl) == false) {
						result.putResult(COM_SCALE_LINE_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				} else {
					cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(COM_SCALE_LINE_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setLineColor(cl) == false) {
						result.putResult(COM_SCALE_LINE_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				}
				result.putResult(COM_SCALE_LINE_COLOR, SGPropertyResults.SUCCEEDED);
			} else if (COM_FONT_NAME.equalsIgnoreCase(key)) {
				final String name = SGUtility.findFontFamilyName(value);
				if (name == null) {
					result.putResult(COM_FONT_NAME,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setFontName(name) == false) {
					result.putResult(COM_FONT_NAME,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_FONT_NAME, SGPropertyResults.SUCCEEDED);
			} else if (COM_FONT_STYLE.equalsIgnoreCase(key)) {
				Integer style = SGUtilityText.getFontStyle(value);
				if (style == null) {
					result.putResult(COM_FONT_STYLE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setFontStyle(style.intValue()) == false) {
					result.putResult(COM_FONT_STYLE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_FONT_STYLE, SGPropertyResults.SUCCEEDED);
			} else if (COM_FONT_SIZE.equalsIgnoreCase(key)) {
				StringBuffer unit = new StringBuffer();
				Number num = SGUtilityText.getNumber(value, unit);
				if (num == null) {
					result.putResult(COM_FONT_SIZE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setFontSize(num.floatValue(), unit.toString()) == false) {
					result.putResult(COM_FONT_SIZE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_FONT_SIZE, SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_FONT_COLOR.equalsIgnoreCase(key)) {
				Color cl = SGUtilityText.getColor(value);
				if (cl != null) {
					if (this.setStringColor(cl) == false) {
						result.putResult(COM_SCALE_FONT_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				} else {
					cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(COM_SCALE_FONT_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setStringColor(cl) == false) {
						result.putResult(COM_SCALE_FONT_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				}
				result.putResult(COM_SCALE_FONT_COLOR, SGPropertyResults.SUCCEEDED);
			} else if (COM_SCALE_FONT_ANGLE.equalsIgnoreCase(key)) {
				Float num = SGUtilityText.getFloat(value);
				if (num == null) {
					result.putResult(COM_SCALE_FONT_ANGLE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setTextAngle(num.floatValue()) == false) {
					result.putResult(COM_SCALE_FONT_ANGLE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_SCALE_FONT_ANGLE, SGPropertyResults.SUCCEEDED);
			}
		}

		// commit the changes
		if (this.commit() == false) {
			return null;
		}
		notifyToRoot();
		this.mAxisElement.notifyChange();
		this.mAxisElement.repaint();

		return result;
	}

	@Override
	public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
		SGPropertyMap map = super.getPropertyFileMap(params);
		this.addProperties(map, KEY_SCALE_X_VALUE, KEY_SCALE_Y_VALUE,
				KEY_X_AXIS_POSITION, KEY_Y_AXIS_POSITION, KEY_SCALE_X_AXIS_LENGTH_VALUE,
				KEY_SCALE_Y_AXIS_LENGTH_VALUE, KEY_SCALE_AXIS_LENGTH_MODE);
		return map;
	}

	@Override
	public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
		SGPropertyMap map = super.getCommandPropertyMap(params);
		this.addProperties(map, COM_SCALE_LOCATION_X, COM_SCALE_LOCATION_Y,
				COM_SCALE_X_AXIS, COM_SCALE_Y_AXIS, COM_SCALE_X_AXIS_LENGTH,
				COM_SCALE_Y_AXIS_LENGTH, COM_SCALE_AXIS_LENGTH_MODE);
		return map;
	}

	private void addProperties(SGPropertyMap map, String xValueKey,
			String yValueKey, String xAxisKey, String yAxisKey,
			String xLengthKey, String yLengthKey, String lengthModeKey) {
		SGPropertyUtility.addProperty(map, xValueKey, this.getXValue());
		SGPropertyUtility.addProperty(map, yValueKey, this.getYValue());
		SGPropertyUtility.addProperty(map, xAxisKey,
				mAxisElement.getLocationName(this.mXAxis));
		SGPropertyUtility.addProperty(map, yAxisKey,
				mAxisElement.getLocationName(this.mYAxis));
		SGPropertyUtility.addProperty(map, xLengthKey, this.getXLengthValue());
		SGPropertyUtility.addProperty(map, yLengthKey, this.getYLengthValue());
//		SGPropertyUtility.addProperty(map, lengthModeKey, 
//				this.toString(this.getAxisLengthMode()));
	}
	
	public static final String MODE_TEXT_ADAPTIVE = "Adaptive";

	public static final String MODE_TEXT_LENGTH_FIXED = "LengthFixed";

	public static final String MODE_TEXT_VALUE_FIXED = "ValueFixed";

	protected String toString(final AXIS_LENGTH_MODE mode) {
		String ret = null;
		if (AXIS_LENGTH_MODE.ADAPTIVE.equals(mode)) {
			ret = MODE_TEXT_ADAPTIVE;
		} else if (AXIS_LENGTH_MODE.LENGTH_FIXED.equals(mode)) {
			ret = MODE_TEXT_LENGTH_FIXED;
		} else if (AXIS_LENGTH_MODE.VALUE_FIXED.equals(mode)) {
			ret = MODE_TEXT_VALUE_FIXED;
		}
		return ret;
	}
	
	protected AXIS_LENGTH_MODE toMode(final String str) {
		AXIS_LENGTH_MODE ret = null;
		if (MODE_TEXT_ADAPTIVE.equals(str)) {
			ret = AXIS_LENGTH_MODE.ADAPTIVE;
		} else if (MODE_TEXT_LENGTH_FIXED.equals(str)) {
			ret = AXIS_LENGTH_MODE.LENGTH_FIXED;
		} else if (MODE_TEXT_VALUE_FIXED.equals(str)) {
			ret = AXIS_LENGTH_MODE.VALUE_FIXED;
		}
		return ret;
	}
	
    public boolean prepare() {
        this.mTemporaryProperties = this.getProperties();
        return true;
    }

    public boolean onMousePressed(final MouseEvent e) {
    	if (!this.isValid()) {
    		return false;
    	}
        if (this.pressDrawingElements(e)) {
            this.prepare();
            return true;
        }
        return false;
    }

    public boolean onMouseClicked(final MouseEvent e) {
        final int x = e.getX();
        final int y = e.getY();
        final int cnt = e.getClickCount();
        
        if (!this.contains(x, y)) {
        	return false;
        }
        
		if (this.isHorizontalVisible()) {
			ScaleString el = (ScaleString) this.getHorizontalStringElement();
	        if (el.unitContains(x, y)) {
	        	this.onLabelClicked(e, el);
	        }
		}
		if (this.isVerticalVisible()) {
			ScaleString el = (ScaleString) this.getVerticalStringElement();
	        if (el.unitContains(x, y)) {
	        	this.onLabelClicked(e, el);
	        }
		}

        // updates selected states
        this.mAxisElement.updateFocusedObjectsList(this, e);
        
        if ((SwingUtilities.isRightMouseButton(e)) && (cnt == 1)) {
        	this.mAxisElement.showPopupMenu(this.getPopupMenu(), x, y);
        } else if ((SwingUtilities.isLeftMouseButton(e)) && (cnt == 2)) {
            // show the dialog
            this.mAxisElement.setPropertiesOfSelectedObjects(this);
        }
        
        return true;
    }
    
    private void onLabelClicked(MouseEvent e, ScaleString el) {
        final int x = e.getX();
        final int y = e.getY();
        final int mod = e.getModifiers();
        final boolean ctrl = (mod & InputEvent.CTRL_MASK) != 0;
        final boolean shift = (mod & InputEvent.SHIFT_MASK) != 0;
        final int cnt = e.getClickCount();
        if (SwingUtilities.isLeftMouseButton(e) && cnt == 1) {
            if (this.isSelected() && !ctrl && !shift) {
				String unitStr = this.getHorizontalStringElement().equals(el) ? this.mHorizontalUnit
						: this.mVerticalUnit;
				if (this.isValidText(unitStr)) {
	            	SGDrawingElementString2D elUnit = new SGDrawingElementString2DExtended(unitStr);
	            	elUnit.setFont(el.getFontName(), el.getFontStyle(), el.getFontSize());
	            	elUnit.setMagnification(el.getMagnification());
	            	elUnit.setAngle(el.getAngle());
	            	Rectangle2D unitRect = elUnit.getStringRect();
	            	
	                this.mAxisElement.mEditingStringElement = el;
	                Rectangle2D sRect = el.getElementBounds();
	                Point pos = this.mAxisElement.getPressedPoint();
	                final int tx = pos.x - (int) sRect.getX();
	                final int ty = pos.y - (int) sRect.getY();
	                elUnit.setLocation((float) (sRect.getX() + sRect.getWidth() - unitRect.getWidth()), 
	                		(float) sRect.getY());
	                this.mAxisElement.showEditField(this.mAxisElement.mTextField, elUnit, tx, ty);
				}
            } else {
                this.mAxisElement.updateFocusedObjectsList(this, e);
            }
        } else if (SwingUtilities.isRightMouseButton(e) && cnt == 1) {
            this.mAxisElement.updateFocusedObjectsList(this, e);
        	this.mAxisElement.showPopupMenu(this.getPopupMenu(), x, y);
        }
    }

    public boolean onMouseReleased(final MouseEvent e) {
    	this.mMouseLocation = -1;
    	this.mTempSymbol = null;
        return false;
    }

    public boolean onMouseDragged(final MouseEvent e) {
        if (!this.isSelected()) {
        	this.mMouseLocation = BODY;
        	return false;
        }
        Point point = this.mAxisElement.getPressedPoint();
        if (point == null) {
            return false;
        }
        if (this.mAxisElement.hasDraggableAxisScaleElement()) {
        	return false;
        }
        if (this.drag(e) == false) {
            return false;
        }
        this.mAxisElement.setPressedPoint(e.getPoint());
        return true;
    }

    private SGUndoManager mUndoManager = new SGUndoManager(this);

	@Override
	public boolean setMementoBackward() {
        if (this.mUndoManager.setMementoBackward() == false) {
            return false;
        }
        if (this.updateLocationWithAxisValues() == false) {
        	return false;
        }
        if (this.updateSizeWithAxisValues() == false) {
            return false;
        }
        this.updateNumberLabelsWithLocation();
        return true;
	}

	@Override
	public boolean setMementoForward() {
        if (this.mUndoManager.setMementoForward() == false) {
            return false;
        }
        if (this.updateLocationWithAxisValues() == false) {
        	return false;
        }
        if (this.updateSizeWithAxisValues() == false) {
            return false;
        }
        this.updateNumberLabelsWithLocation();
        return true;
	}

	@Override
	public boolean undo() {
        return this.setMementoBackward();
	}

	@Override
	public boolean redo() {
        return this.setMementoForward();
	}

	@Override
	public boolean initPropertiesHistory() {
		return this.mUndoManager.initPropertiesHistory();
	}

	@Override
	public boolean updateHistory() {
        return this.mUndoManager.updateHistory();
	}

	@Override
	public void notifyToRoot() {
		this.mAxisElement.notifyToRoot();
	}

	@Override
	public boolean isChanged() {
        return this.mUndoManager.isChanged();
	}

	@Override
	public boolean isChangedRoot() {
        return this.isChanged();
	}

	@Override
	public void setChanged(boolean b) {
        this.mUndoManager.setChanged(b);
	}

	@Override
	public SGProperties getMemento() {
        return this.getProperties();
	}

	@Override
	public boolean setMemento(SGProperties p) {
        return this.setProperties(p);
	}

	@Override
	public boolean isUndoable() {
		return this.mUndoManager.isUndoable();
	}

	@Override
	public boolean isRedoable() {
		return this.mUndoManager.isRedoable();
	}

	@Override
	public void initUndoBuffer() {
        this.mUndoManager.initUndoBuffer();
	}

	@Override
	public boolean deleteForwardHistory() {
        return this.mUndoManager.deleteForwardHistory();
	}

	@Override
	public void clearChanged() {
        this.setChanged(false);
	}

	public boolean setMouseCursor(final int x, final int y) {
	    if (this.contains(x, y)) {
	        if (this.isSelected()) {
	            this.mMouseLocation = this.getMouseLocation(x, y);
	            this.mAxisElement.setMouseCursor(this.getCursor(this.mMouseLocation));
	        } else {
		        this.mAxisElement.setMouseCursor(Cursor.HAND_CURSOR);
	        }
	        return true;
	    }
	    return false;
	}

    protected boolean setChangedFocusedObjects() {
		SGProperties temp = this.mTemporaryProperties;
		if (temp != null) {
			SGProperties p = this.getProperties();
			if (p.equals(temp) == false) {
				this.setChanged(true);
			}
		}
		return true;
    }
    
//    @Override
//    protected boolean updateDrawingElements() {
//    	final float x = this.mLocation.x;
//    	final float y = this.mLocation.y;
//    	final double xValue = this.mAxisElement.calcValue(x, this.mXAxis, true);
//    	final double yValue = this.mAxisElement.calcValue(y, this.mYAxis, false);
//		final float endX = this.mAxisElement.calcLocation(
//				xValue + this.mXLengthValue, this.mXAxis, true);
//		final float endY = this.mAxisElement.calcLocation(
//				yValue + this.mYLengthValue, this.mYAxis, false);
//    	final float width = endX - x;
//    	final float height = endY - y;
//    	
//    	final float mag = this.getMagnification();
//    	this.mWidth = width / mag;
//    	this.mHeight = height / mag;
//    	
//    	this.updateNumberLabel(true);
//    	this.updateNumberLabel(false);
//    	return super.updateDrawingElements();
//    }

    private void updateNumberLabelWithLocation(final boolean horizontal) {
    	if (horizontal) {
        	if (this.mHorizontalText == null) {
        		final double lenValue = this.getDiffValue(horizontal);
            	this.setNumberLabel(lenValue, true);
        	}
    	} else {
        	if (this.mVerticalText == null) {
        		final double lenValue = this.getDiffValue(horizontal);
            	this.setNumberLabel(lenValue, false);
        	}
    	}
    }
    
    private Double getDiffValue(final boolean horizontal) {
		SGAxis axis = horizontal ? this.mXAxis : this.mYAxis;
		final float location = horizontal ? this.getX() : this.getY();
		final float endPointLocation = horizontal ? this.getEndPointX() : this.getEndPointY();
    	final double value = this.mAxisElement.calcValue(location, axis, horizontal);
    	final double endValue = this.mAxisElement.calcValue(endPointLocation, axis, horizontal);
    	final double diffValue = endValue - value;
    	return diffValue;
    }

	@Override
	public void axisScaleChanged(SGAxis axis) {
		SGProperties prev = this.getProperties();
		
		if (AXIS_LENGTH_MODE.VALUE_FIXED.equals(this.mAxisLengthMode)) {
			this.updateSizeWithAxisValues();
			
		} else if (AXIS_LENGTH_MODE.LENGTH_FIXED.equals(this.mAxisLengthMode)) {
			if (this.mXAxis.equals(axis)) {
				if (this.mHorizontalText == null) {
					final double diffValue = this.getDiffValue(true);
			    	this.setNumberLabel(diffValue, true);
					this.mXLengthValue = SGUtilityNumber.getNumberInRangeOrder(
							diffValue, this.mXAxis);
				}
			} else if (this.mYAxis.equals(axis)) {
				if (this.mVerticalText == null) {
					final double diffValue = this.getDiffValue(false);
			    	this.setNumberLabel(diffValue, false);
					this.mYLengthValue = SGUtilityNumber.getNumberInRangeOrder(
							diffValue, this.mYAxis);
				}
			}
			this.updateNumberLabelsWithLocation();

		} else if (AXIS_LENGTH_MODE.ADAPTIVE.equals(this.mAxisLengthMode)) {
			SGAxisElement el = this.mAxisElement.getAxisGroup(axis);
			final double stepValue = el.getStepValue();
			if (this.mXAxis.equals(axis)) {
				final int sign = this.mXLengthValue >= 0.0 ? 1 : -1;
				this.mXLengthValue = SGUtilityNumber.getNumberInRangeOrder(
						sign * stepValue, this.mXAxis);
			} else if (this.mYAxis.equals(axis)) {
				final int sign = this.mYLengthValue >= 0.0 ? 1 : -1;
				this.mYLengthValue = SGUtilityNumber.getNumberInRangeOrder(
						sign * stepValue, this.mYAxis);
			}
			this.updateSizeWithAxisValues();
			this.updateNumberLabelsWithLocation();
		}

		// update axis values
		this.updateAxisValuesWithShape();

		if (this.isVisible() && this.isValid()) {
			SGProperties cur = this.getProperties();
			final boolean changed = !prev.equals(cur);
			this.setChanged(changed);
		}
	}
	
	private void setNumberLabel(final double value, final boolean horizontal) {
		SGAxis axis = horizontal ? this.mXAxis : this.mYAxis;
    	final double valueNew = (value == 0.0 || Double.isNaN(value)) ? value : SGUtilityNumber.getNumberInRangeOrder(value, axis);
    	SGDrawingElementString el = horizontal ? this.getHorizontalStringElement() : this.getVerticalStringElement();
    	String unit = horizontal ? this.mHorizontalUnit : this.mVerticalUnit;
    	StringBuffer sb = new StringBuffer();
    	sb.append(Math.abs(valueNew));
    	if (this.isValidText(unit)) {
        	sb.append(" [");
        	sb.append(unit);
        	sb.append("]");
    	}
    	el.setString(sb.toString());
	}

	@Override
	public boolean commit() {
        // compare two properties
        SGProperties pTemp = this.mTemporaryProperties;
        SGProperties pPresent = this.getProperties();
        if (pTemp.equals(pPresent) == false) {
            this.setChanged(true);
        }
        
        // clear temporary properties
        this.mTemporaryProperties = null;

        if (this.updateLocationWithAxisValues() == false) {
        	return false;
        }
        if (this.updateSizeWithAxisValues() == false) {
            return false;
        }
        this.updateNumberLabelsWithLocation();
        this.mAxisElement.repaint();
        this.mAxisElement.notifyChangeOnCommit();

        return true;
	}

	@Override
	public boolean cancel() {
        // set temporary properties to drawing elements to cancel the change
        if (this.setProperties(this.mTemporaryProperties) == false) {
            return false;
        }

        // clear temporary properties
        this.mTemporaryProperties = null;
        
        if (this.updateLocationWithAxisValues() == false) {
        	return false;
        }
        if (this.updateSizeWithAxisValues() == false) {
            return false;
        }
        this.updateNumberLabelsWithLocation();
        this.mAxisElement.notifyChangeOnCancel();
        this.mAxisElement.repaint();

        return true;
	}

	@Override
	public boolean preview() {
        if (this.updateLocationWithAxisValues() == false) {
        	return false;
        }
        if (this.updateSizeWithAxisValues() == false) {
            return false;
        }
        this.updateNumberLabelsWithLocation();
        this.mAxisElement.repaint();
        this.mAxisElement.notifyChange();
        return true;
	}

	@Override
	public SGPropertyDialog getPropertyDialog() {
        return this.mAxisElement.mAxisScalePropertyDialog;
	}

	@Override
	public boolean getAxisDateMode(int location) {
		return this.mAxisElement.getAxisDateMode(location);
	}
	
    /**
     * Returns the location of this symbol.
     * 
     * @return the location of this symbol
     */
	@Override
    public SGTuple2f getLocation() {
        final float basex = super.getX();
        final float basey = super.getY();
        final float mag = this.getMagnification();
        final float x = this.mAxisElement.getGraphRectX() + mag * basex;
        final float y = this.mAxisElement.getGraphRectY() + mag * basey;
        return new SGTuple2f(x, y);
    }

	@Override
	public float getX() {
        return this.getLocation().x;
	}

	@Override
	public float getY() {
        return this.getLocation().y;
	}

	private double getValue(final float location, final boolean horizontal) {
		SGAxis axis = horizontal ? this.mXAxis : this.mYAxis;
		final double value = this.mAxisElement.calcValue(location, axis, horizontal);
		return value;
	}

	@Override
	public double getXValue() {
		return this.mXValue;
	}

	public double getEndXValue() {
		final float location = this.getX() + this.getMagnification() * this.mWidth;
		return this.getValue(location, true);
	}
	
	@Override
	public double getYValue() {
		return this.mYValue;
	}

	public double getEndYValue() {
		final float location = this.getY() + this.getMagnification() * this.mHeight;
		return this.getValue(location, false);
	}
	
	@Override
	public double getXLengthValue() {
		return this.mXLengthValue;
	}

	@Override
	public double getYLengthValue() {
		return this.mYLengthValue;
	}
	
	@Override
	public boolean isXAxisInverted() {
		return this.mXInvertedFlag;
	}

	@Override
	public boolean isYAxisInverted() {
		return this.mYInvertedFlag;
	}

	@Override
	public String getXAxisText() {
		return (this.mHorizontalText == null) ? "" : this.mHorizontalText;
	}

	@Override
	public String getXAxisUnit() {
		return this.mHorizontalUnit;
	}

	@Override
	public String getYAxisText() {
		return (this.mVerticalText == null) ? "" : this.mVerticalText;
	}

	@Override
	public String getYAxisUnit() {
		return this.mVerticalUnit;
	}

	@Override
	public boolean isXAxisVisible() {
		return this.isHorizontalVisible();
	}

	@Override
	public boolean isYAxisVisible() {
		return this.isVerticalVisible();
	}

	@Override
	public boolean setXValue(final double value) {
		this.mXValue = value;
		return true;
	}

	@Override
	public boolean setYValue(final double value) {
		this.mYValue = value;
		return true;
	}

	@Override
	public boolean setXLengthValue(final double value) {
		this.mXLengthValue = value;
		return true;
	}

	@Override
	public boolean setYLengthValue(final double value) {
		this.mYLengthValue = value;
		return true;
	}

	@Override
	public boolean setXAxisText(final String text) {
		return this.setHorizontalText(text);
	}

	@Override
	public boolean setXAxisUnit(final String unit) {
		this.mHorizontalUnit = unit;
		this.updateNumberLabelWithLocation(true);
		return true;
	}

	@Override
	public boolean setYAxisText(final String text) {
		return this.setVerticalText(text);
	}

	@Override
	public boolean setYAxisUnit(final String unit) {
		this.mVerticalUnit = unit;
		this.updateNumberLabelWithLocation(false);
		return true;
	}

	@Override
	public boolean setXAxisVisible(final boolean visible) {
		this.mHorizontalVisible = visible;
		return true;
	}

	@Override
	public boolean setYAxisVisible(final boolean visible) {
		this.mVerticalVisible = visible;
		return true;
	}

	@Override
	public boolean isAxisScaleVisible() {
		return this.isVisible();
	}

	@Override
	public boolean setAxisScaleVisible(final boolean b) {
		this.setVisible(b);
		return false;
	}

    /**
     * Returns whether the x value is valid.
     * 
     * @param location
     *           the axis location
     * @param value
     *           the x value
     * @return true if valid
     */
    public boolean hasValidXValue(final int location, final Number value) {
        final SGAxis axis = (location == -1) ? this.mXAxis
                : this.mAxisElement.getAxisInPlane(location);
        final double v = (value != null) ? value.doubleValue() : this.getXValue();
        return axis.isValidValue(v);
    }

    /**
     * Returns whether the y value is valid.
     * 
     * @param location
     *           the axis location
     * @param value
     *           the y value
     * @return true if valid
     */
    public boolean hasValidYValue(final int location, final Number value) {
        final SGAxis axis = (location == -1) ? this.mYAxis
                : this.mAxisElement.getAxisInPlane(location);
        final double v = (value != null) ? value.doubleValue() : this.getYValue();
        return axis.isValidValue(v);
    }

    public Element createElement(final Document document, SGExportParameter params) {
        Element element = document.createElement(TAG_NAME_SCALE);
        if (this.writeProperty(element, params) == false) {
            return null;
        }
        return element;
    }

    @Override
    public Color getLineColor() {
    	return this.getColor();
    }
    
    @Override
    public boolean setLineColor(final Color cl) {
    	return this.setColor(cl);
    }

    @Override
    public Color getFontColor() {
    	return this.getStringColor();
    }

    @Override
    public boolean setFontColor(final Color cl) {
    	return this.setStringColor(cl);
    }

	@Override
	public boolean isXAxisTitleDownside() {
		return this.mHorizontalTextDownside;
	}

	@Override
	public boolean isYAxisTitleLeftside() {
		return this.mVerticalTextLeftside;
	}

	@Override
	public boolean setXAxisTitleDownside(boolean b) {
		this.mHorizontalTextDownside = b;
		return true;
	}

	@Override
	public boolean setYAxisTitleLeftside(boolean b) {
		this.mVerticalTextLeftside = b;
		return true;
	}
	
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

    protected JPopupMenu createPopupMenu() {
        JPopupMenu p = new JPopupMenu();
        p.setBounds(0, 0, 100, 100);
        p.add(new JLabel("  -- Scale --"));
        p.addSeparator();
        SGUtility.addItem(p, this, MENUCMD_HIDE_SCALE);
        p.addSeparator();
        SGUtility.addItem(p, this, MENUCMD_PROPERTY);
        return p;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (MENUCMD_HIDE_SCALE.equals(command)) {
			this.setAxisScaleVisible(false);
            this.mAxisElement.repaint();
            this.setChanged(true);
            this.notifyToRoot();
		} else if (MENUCMD_PROPERTY.equals(command)) {
            // show the property dialog
            this.mAxisElement.setPropertiesOfSelectedObjects(this);
		}
	}

    @Override
    public void paint(final Graphics2D g2d) {
    	super.paint(g2d);
    	
        // draw focused anchors
        if (this.mAxisElement.isSymbolsVisibleAroundFocusedObjects()
                && this.isSelected()) {
            ArrayList<Point2D> pList = this.getAnchorPointList();
            SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(pList, g2d);
    	}

        // draw child anchors
        if (this.mAxisElement.isSymbolsVisibleAroundAllObjects()) {
            ArrayList<Point2D> pList = this.getAnchorPointList();
            SGUtilityForFigureElementJava2D.drawAnchorAsChildObject(pList, g2d);
        }
    }

	@Override
	public AXIS_LENGTH_MODE getAxisLengthMode() {
		return this.mAxisLengthMode;
	}

	@Override
	public boolean setAxisLengthMode(AXIS_LENGTH_MODE mode) {
		this.mAxisLengthMode = mode;
		return true;
	}

	@Override
	protected SGDrawingElementString createString(final boolean horizontal) {
		return new ScaleString("0.0", horizontal);
	}

	@Override
	protected SGDrawingElementLine createLine(final boolean horizontal) {
		return new ScaleLine();
	}

	class ScaleLine extends SGSimpleLine2D {
	    public ScaleLine() {
	        super();
	    }
	}

	class ScaleString extends SGDrawingElementString2DExtended {
		
		private boolean mHorizontal;
		
		public ScaleString(String str, final boolean horizontal) {
			super(str);
			this.mHorizontal = horizontal;
		}
		
		protected boolean unitContains(final int x, final int y) {
			if (this.mHorizontal) {
				if (mHorizontalText == null && mHorizontalUnit != null) {
					return this.unitContainsSub(x, y);
				}
			} else {
				if (mVerticalText == null && mVerticalUnit != null) {
					return this.unitContainsSub(x, y);
				}
			}
			return false;
		}
		
		private boolean unitContainsSub(final int x, final int y) {
			return this.contains(x, y);
		}
	}

	private boolean isValidText(final String str) {
		return (str != null) && !"".equals(str);
	}
	
    void updateAxisValuesWithShape() {
        final double xValue = this.mAxisElement.calcValue(this.getX(), this.mXAxis, true);
        final double yValue = this.mAxisElement.calcValue(this.getY(), this.mYAxis, false);
        final double xEndValue = this.mAxisElement.calcValue(this.getEndPointX(), this.mXAxis, true);
        final double yEndValue = this.mAxisElement.calcValue(this.getEndPointY(), this.mYAxis, false);
        this.mXValue = SGUtilityNumber.getNumberInRangeOrder(xValue, this.mXAxis);
        this.mYValue = SGUtilityNumber.getNumberInRangeOrder(yValue, this.mYAxis);
        this.mXLengthValue = SGUtilityNumber.getNumberInRangeOrder(xEndValue - xValue, this.mXAxis);
        this.mYLengthValue = SGUtilityNumber.getNumberInRangeOrder(yEndValue - yValue, this.mYAxis);
        
        if (this.mHorizontalText == null) {
            this.setNumberLabel(this.mXLengthValue, true);
        }
        if (this.mVerticalText == null) {
            this.setNumberLabel(this.mYLengthValue, false);
        }
        
        this.setValid(true);
    }
    
    boolean updateLocationWithAxisValues() {
        final float x = this.mAxisElement.calcLocation(this.mXValue, this.mXAxis, true);
        final float y = this.mAxisElement.calcLocation(this.mYValue, this.mYAxis, false);
		if (Float.isNaN(x) || Float.isNaN(y)) {
            this.setValid(false);
            return false;
        }
        this.setValid(true);
        
        // the location
        this.setLocation(x, y);
        
        if (this.updateDrawingElementsLocation() == false) {
        	return false;
        }
        return true;
    }

    boolean updateSizeWithAxisValues() {
        final float x = this.mAxisElement.calcLocation(this.mXValue, this.mXAxis, true);
        final float y = this.mAxisElement.calcLocation(this.mYValue, this.mYAxis, false);
        final float endX = this.mAxisElement.calcLocation(this.mXValue + this.mXLengthValue, 
        		this.mXAxis, true);
        final float endY = this.mAxisElement.calcLocation(this.mYValue + this.mYLengthValue, 
        		this.mYAxis, false);
		if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(endX) || Float.isNaN(endY)) {
            this.setValid(false);
            return false;
        }
        this.setValid(true);
        
        final float mag = this.getMagnification();

        // width
        final float w = (endX - x) / mag;
        this.setWidth(w);
        
        // height
        final float h = (endY - y) / mag;
        this.setHeight(h);
        
        if (this.updateDrawingElementsLocation() == false) {
        	return false;
        }
        return true;
    }
    
    void updateNumberLabelsWithLocation() {
        this.updateNumberLabelWithLocation(true);
        this.updateNumberLabelWithLocation(false);
        this.updateLabelLocation();
    }

	@Override
	public ArrayList getChildNodes() {
		// always returns an empty list
        return new ArrayList();
	}

	@Override
	public String getClassDescription() {
        return "Scale";
	}

	@Override
	public String getInstanceDescription() {
		return this.getClassDescription();
	}
}

class AxisScaleProperties extends ScaleProperties implements SGIScaleConstants {
	
	double mXValue;
	
	double mYValue;
	
	SGAxis mXAxis;

	SGAxis mYAxis;
	
	double mXLengthValue;
	
	double mYLengthValue;
	
	AXIS_LENGTH_MODE mAxisLengthMode;
	
	public AxisScaleProperties() {
		super();
	}

	@Override
	public boolean equals(final Object obj) {
		if ((obj instanceof AxisScaleProperties) == false) {
			return false;
		}
		if (super.equals(obj) == false) {
			return false;
		}

		AxisScaleProperties p = (AxisScaleProperties) obj;
		if (p.mXValue != this.mXValue) {
			return false;
		}
		if (p.mYValue != this.mYValue) {
			return false;
		}
		if (SGUtility.equals(p.mXAxis, this.mXAxis) == false) {
			return false;
		}
		if (SGUtility.equals(p.mYAxis, this.mYAxis) == false) {
			return false;
		}
		if (p.mXLengthValue != this.mXLengthValue) {
			return false;
		}
		if (p.mYLengthValue != this.mYLengthValue) {
			return false;
		}
		if (!SGUtility.equals(p.mAxisLengthMode, this.mAxisLengthMode)) {
			return false;
		}
		return true;
	}
}

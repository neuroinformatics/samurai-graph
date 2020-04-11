package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGDateUtility;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxisConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIUndoable;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGIAxisConstants;

import org.w3c.dom.Element;

/**
 * An axis in the figure.
 * 
 */
class SGFigureAxis extends SGAxisElement implements SGIUndoable {

	/**
	 * Shift of the location in units of AXIS_SHIFT_UNIT.
	 */
	private float mShift = 0.0f;

    /**
     * The pop-up menu.
     */
    private JPopupMenu mPopupMenu = null;

    /**
     * Builds an axis.
     */
    protected SGFigureAxis(SGFigureElementAxis axisElement) {
        super(axisElement);
        this.setShift(DEFAULT_AXIS_SHIFT, AXIS_SHIFT_UNIT);
    }
    
    public void init() {
    	this.initLocationOfExponentDrawingElement();
    }

    /**
     * Creates axis lines.
     * 
     * @return axis lines
     */
    protected ElementLineAxis[] createAxisLines() {
        ElementLineAxis[] array = new ElementLineAxis[1];
        array[0] = new ElementLineAxis();
        return array;
    }
    
    private ElementLineAxis getLine() {
    	return this.mAxisLines[0];
    }
    
    private float getMagnifiedShift() {
    	return this.mAxisElement.getMagnification() * this.getShift() / SGIConstants.CM_POINT_RATIO;
    }

    /**
     * Disposes of this object.
     */
    public void dispose() {
    	super.dispose();
        this.mPopupMenu = null;
    }

    /**
     * Returns a pop-up menu.
     * 
     * @return a pop-up menu
     */
    public JPopupMenu getPopupMenu() {
        JPopupMenu p = null;
        if (this.mPopupMenu != null) {
            p = this.mPopupMenu;
        } else {
            p = this.createPopupMenu();
            this.mPopupMenu = p;
        }
        
        JCheckBoxMenuItem item;
        Component[] array = p.getComponents();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii] instanceof JCheckBoxMenuItem) {
                item = (JCheckBoxMenuItem) array[ii];
                String com = item.getActionCommand();
                if (MENUCMD_DRAW_LATER.equals(com)) {
                    item.setSelected(!SGFigureElementAxis.mNotifyChangeOnDraggingFlag);
                } else if (MENUCMD_SHOW_AXIS.equals(com)) {
                    item.setSelected(this.isVisible());
                }
            }
        }
        
        return p;
    }
    
    /**
     * Create a pop-up menu.
     * @return
     *         a pop-up menu
     */
    protected JPopupMenu createPopupMenu() {
        JPopupMenu p = new JPopupMenu();
        
        p.setBounds(0, 0, 100, 100);

        StringBuffer sb = new StringBuffer();
        sb.append("  -- Axis: ");
        sb.append(this.mAxisElement.getLocationName(this.mAxis));
        sb.append(" --");
        
        p.add(new JLabel(sb.toString()));
        p.addSeparator();

        SGUtility.addCheckBoxItem(p, this, MENUCMD_SHOW_AXIS);

        p.addSeparator();

        SGUtility.addCheckBoxItem(p, this, MENUCMD_DRAW_LATER);

        p.addSeparator();

//        SGUtility.addItem(p, this, MENUCMD_PROPERTY);
        JMenu menu = SGUtility.addMenu(p, this, MENUCMD_PROPERTY, true);
    	SGUtility.addItem(menu, this, MENUCMD_SHOW_PROPERTIES_SELECTED_AXES);
    	SGUtility.addItem(menu, this, MENUCMD_SHOW_PROPERTIES_ALL_VISIBLE_AXES);
    	SGUtility.addItem(menu, this, MENUCMD_SHOW_PROPERTIES_ALL_AXES);

        return p;
    }

    /**
     * Called when the axis is clicked.
     * 
     * @param e
     *          the mouse event
     * @return true if a drawing element is clicked, and false otherwise
     */
    protected boolean clicked(final MouseEvent e) {
        final int x = e.getX();
        final int y = e.getY();
        final int cnt = e.getClickCount();
        
        // updates selected states
        this.mAxisElement.updateFocusedObjectsList(this, e);
        
        // avoids simultaneous selection
		if (this.isSelected()) {
			this.mTitle.setSelectedFlag(false);
			this.mExponentSymbol.setSelectedFlag(false);
		}

        if ((SwingUtilities.isRightMouseButton(e)) && (cnt == 1)) {
        	this.mAxisElement.showPopupMenu(this.getPopupMenu(), x, y);
        } else if ((SwingUtilities.isLeftMouseButton(e)) && (cnt == 2)) {
        	this.mAxisElement.showPropertyDialog(this);
        }
        
        return true;
    }

    /**
     * 
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(MENUCMD_SHOW_AXIS)) {
        	if (this.isVisible()) {
            	this.mAxisElement.notifyToListener(MENUCMD_HIDE_AXES, e.getSource());
        	} else {
        		this.exchangeVisibility();
        		this.mAxisElement.notifyToRoot();
        		this.mAxisElement.repaint();
        	}
        } else if (MENUCMD_SHOW_PROPERTIES_SELECTED_AXES.equals(command)) {
        	this.mAxisElement.showPropertyDialog(this);
        } else if (MENUCMD_SHOW_PROPERTIES_ALL_VISIBLE_AXES.equals(command)) {
        	this.mAxisElement.showPropertyDialog(this, true);
        } else if (MENUCMD_SHOW_PROPERTIES_ALL_AXES.equals(command)) {
        	this.mAxisElement.showPropertyDialog(this, false);
        } else if (command.equals(MENUCMD_DRAW_LATER)) {
            SGFigureElementAxis.mNotifyChangeOnDraggingFlag = !SGFigureElementAxis.mNotifyChangeOnDraggingFlag;
        }
    }
    
    void exchangeVisibility() {
        this.setVisible(!this.isVisible());
        this.setChanged(true);
    }

    /**
     * Sets the location of the axis line.
     * 
     * @return true if succeeded
     */
    protected boolean setLocationOfAxisLines() {
        float x1;
        float y1;
        float x2;
        float y2;
        final float gx = this.mAxisElement.getGraphRectX();
        final float gy = this.mAxisElement.getGraphRectY();
        final float gw = this.mAxisElement.getGraphRectWidth();
        final float gh = this.mAxisElement.getGraphRectHeight();
        final int loc = this.getLocationInPlane();
        switch (loc) {
        case SGIFigureElementAxis.AXIS_HORIZONTAL_1:
            x1 = gx;
            y1 = gy + gh - this.getMagnifiedShift();
            x2 = gx + gw;
            y2 = y1;
            break;
        case SGIFigureElementAxis.AXIS_HORIZONTAL_2:
            x1 = gx;
            y1 = gy + this.getMagnifiedShift();
            x2 = gx + gw;
            y2 = y1;
            break;
        case SGIFigureElementAxis.AXIS_VERTICAL_1:
            x1 = gx + this.getMagnifiedShift();
            y1 = gy + gh;
            x2 = x1;
            y2 = gy;
            break;
        case SGIFigureElementAxis.AXIS_VERTICAL_2:
            x1 = gx + gw - this.getMagnifiedShift();
            y1 = gy + gh;
            x2 = x1;
            y2 = gy;
            break;
        default:
            throw new Error("Invalid location: " + loc);
        }
        
		// set the location to the drawing element
        this.getLine().setTermPoints(x1, y1, x2, y2);
        
        return true;
    }

    /**
     * Sets the location of scale numbers.
     * 
     * @return true if succeeded
     */
    protected boolean setLocationOfScaleNumbers() {
        final int loc = this.getLocationInPlane();
        final boolean invcoord = this.isInvertCoordinates();
        final float gx = this.mAxisElement.getGraphRectX();
        final float gy = this.mAxisElement.getGraphRectY();
        final float gw = this.mAxisElement.getGraphRectWidth();
        final float gh = this.mAxisElement.getGraphRectHeight();
        final int scaleType = this.mAxis.getScaleType();
        double[] valueArrayInScale;
        double axisMinInScale;
        double axisMaxInScale;
        if (scaleType == SGAxis.LINEAR_SCALE) {
            valueArrayInScale = this.mAxisValueArray;
            axisMinInScale = this.mAxis.getMinDoubleValue();
            axisMaxInScale = this.mAxis.getMaxDoubleValue();
        } else if (scaleType == SGAxis.LOG_SCALE) {
            valueArrayInScale = new double[this.mAxisValueArray.length];
            for (int ii = 0; ii < valueArrayInScale.length; ii++) {
                valueArrayInScale[ii] = Math.log(this.mAxisValueArray[ii]);
            }
            axisMinInScale = Math.log(this.mAxis.getMinDoubleValue());
            axisMaxInScale = Math.log(this.mAxis.getMaxDoubleValue());
        } else {
        	throw new Error("Invalid scale type: " + scaleType);
        }

        final float mag = this.mAxisElement.getMagnification();
        final float factor = mag / SGIConstants.CM_POINT_RATIO;
        final float spaceLN = this.getSpaceAxisLineAndNumbers() * factor;
        for (int ii = 0; ii < this.mNumberList.size(); ii++) {
            ElementStringNumber el = this.mNumberList.get(ii);
            Rectangle2D rect = el.getElementBounds();
            final float width = (float) rect.getWidth();
            final float height = (float) rect.getHeight();
            float x;
            float y;
            switch (loc) {
            case SGIFigureElementAxis.AXIS_HORIZONTAL_1: // bottom
                if (invcoord) {
                    x = (float) (gx + gw
                            * (axisMaxInScale - valueArrayInScale[ii])
                            / (axisMaxInScale - axisMinInScale));
                } else {
                    x = (float) (gx + gw
                            * (valueArrayInScale[ii] - axisMinInScale)
                            / (axisMaxInScale - axisMinInScale));
                }
                x -= width / 2;
                y = gy + gh + spaceLN - this.getMagnifiedShift();
                break;
            case SGIFigureElementAxis.AXIS_HORIZONTAL_2: // top
                if (invcoord) {
                    x = (float) (gx + gw
                            * (axisMaxInScale - valueArrayInScale[ii])
                            / (axisMaxInScale - axisMinInScale));
                } else {
                    x = (float) (gx + gw
                            * (valueArrayInScale[ii] - axisMinInScale)
                            / (axisMaxInScale - axisMinInScale));
                }
                x -= width / 2;
                y = gy - spaceLN - (float) rect.getHeight() + this.getMagnifiedShift();
                break;
            case SGIFigureElementAxis.AXIS_VERTICAL_1: // left
                if (invcoord) {
                    y = (float) (gy + gh
                            * (1.0 - (axisMaxInScale - valueArrayInScale[ii])
                                    / (axisMaxInScale - axisMinInScale)));
                } else {
                    y = (float) (gy + gh
                            * (1.0 - (valueArrayInScale[ii] - axisMinInScale)
                                    / (axisMaxInScale - axisMinInScale)));
                }
                y -= height / 2;
                x = gx - spaceLN - (float) rect.getWidth() + this.getMagnifiedShift();
                break;
            case SGIFigureElementAxis.AXIS_VERTICAL_2: // right
                if (invcoord) {
                    y = (float) (gy + gh
                            * (1.0 - (axisMaxInScale - valueArrayInScale[ii])
                                    / (axisMaxInScale - axisMinInScale)));
                } else {
                    y = (float) (gy + gh
                            * (1.0 - (valueArrayInScale[ii] - axisMinInScale)
                                    / (axisMaxInScale - axisMinInScale)));
                }
                y -= height / 2;
                x = gx + gw + spaceLN - this.getMagnifiedShift();
                break;
            default: {
                throw new Error("Invalid location: " + loc);
            }
            }
            
    		// set the location to the drawing element
            el.setLocation(x, y);
        }

        // the exponent drawing element
        if (this.isExponentVisible()) {
            this.setLocationOfExponentDrawingElement();
        }

        return true;
    }

    /**
     * Create the scale lines.
     * 
     * @return true if succeeded
     */
    protected boolean createTickMarks() {
        // clear the list
        this.mTickMarksList.clear();

        boolean ret = true;
        
        // creates tick marks
        final int scaleType = this.mAxis.getScaleType();
        switch (scaleType) {
		case SGAxis.LINEAR_SCALE:
			ret = this.createTickMarksInLinearScale(
					this.getTickMarkWidth(),
					this.mMajorTickMarkLength,
					this.mTickMarkColor);
			break;
		case SGAxis.LOG_SCALE:
			ret = this.createTickMarksInLogScale(
					this.getTickMarkWidth(),
					this.mMajorTickMarkLength,
					this.mTickMarkColor);
			break;
		default:
			throw new Error("Invalid scale type: " + scaleType);
		}

        return ret;
    }

    // Creates tick marks for a given axis value for given direction.
    protected void createTicksSub(final double value, 
            final float width, final float len, final Color cl, final boolean inside) {
        ElementLineTickMark el;
        el = this.createSingleTickMark(value, width, len, inside);
        if (el != null) {
            this.setupCreatedScaleLineProperties(el, value, cl);
        }
    }
	
    /**
     * Sets the location of the title.
     * 
     * @return true if succeeded
     */
    protected boolean setLocationOfTitle() {
        final float gx = this.mAxisElement.getGraphRectX();
        final float gy = this.mAxisElement.getGraphRectY();
        final float gw = this.mAxisElement.getGraphRectWidth();
        final float gh = this.mAxisElement.getGraphRectHeight();
        final float mag = this.mAxisElement.getMagnification();
        final float factor = mag / SGIConstants.CM_POINT_RATIO;
        final float spaceLN = this.getSpaceAxisLineAndNumbers() * factor;
        final float spaceNT = this.getSpaceTitleAndNumbers() * factor;
        final float scaleNumberHeight = (float) getScaleHeight();
        final float maxWidth = this.mAxisElement.getMaxLengthOfScaleNumbers(this);
        final float shiftFromCenter = this.getTitleShiftFromCenter() * factor;
        final int location = this.getLocationInPlane();
        Rectangle2D rectTitle;
		float x;
		float y;
        switch (location) {
		case SGIFigureElementAxis.AXIS_HORIZONTAL_1:
			rectTitle = this.mTitle.getElementBounds();
			x = gx + (gw - (float) rectTitle.getWidth()) / 2.0f + shiftFromCenter;
			y = gy + gh + scaleNumberHeight + (spaceLN + spaceNT) - this.getMagnifiedShift();
			break;
		case SGIFigureElementAxis.AXIS_HORIZONTAL_2:
			rectTitle = this.mTitle.getElementBounds();
			x = gx + (gw - (float) rectTitle.getWidth()) / 2.0f + shiftFromCenter;
			y = gy - scaleNumberHeight - (float) rectTitle.getHeight()
					- (spaceLN + spaceNT) + this.getMagnifiedShift();
			break;
		case SGIFigureElementAxis.AXIS_VERTICAL_1:
			this.mTitle.setAngle(90.0f); // rotation of 90 degree
			rectTitle = this.mTitle.getElementBounds();
			x = gx - maxWidth - (float) rectTitle.getWidth()
					- (spaceLN + spaceNT) + this.getMagnifiedShift();
			y = gy + (gh + (float) rectTitle.getHeight()) / 2.0f + shiftFromCenter;
			break;
		case SGIFigureElementAxis.AXIS_VERTICAL_2:
			this.mTitle.setAngle(90.0f); // rotation of 90 degree
			rectTitle = this.mTitle.getElementBounds();
			x = gx + gw + maxWidth + (spaceLN + spaceNT) - this.getMagnifiedShift();
			y = gy + (gh + (float) rectTitle.getHeight()) / 2.0f + shiftFromCenter;
			break;
        default:
			throw new Error("Invalid location: " + location);
        }
        
		// set the location to the drawing element
        this.mTitle.setLocation(x, y);

        return true;
    }

    /**
     * Sets the location of exponent object.
     * 
     * @return true if succeeded
     */
    protected boolean setLocationOfExponentDrawingElement() {
    	SGIFigureElementAxis aElement = this.mAxisElement;
        final float gx = aElement.getGraphRectX();
        final float gy = aElement.getGraphRectY();
        final float gw = aElement.getGraphRectWidth();
        final float gh = aElement.getGraphRectHeight();
		final int loc = this.getLocationInPlane();
		final float baseX, baseY;
		switch (loc) {
		case SGIFigureElementAxis.AXIS_HORIZONTAL_1:
			baseX = gx + gw;
			baseY = gy + gh - this.getMagnifiedShift();
			break;
		case SGIFigureElementAxis.AXIS_HORIZONTAL_2:
			baseX = gx + gw;
			baseY = gy + this.getMagnifiedShift();
			break;
		case SGIFigureElementAxis.AXIS_VERTICAL_1:
			baseX = gx + this.getMagnifiedShift();
			baseY = gy;
			break;
		case SGIFigureElementAxis.AXIS_VERTICAL_2:
			baseX = gx + gw - this.getMagnifiedShift();
			baseY = gy;
			break;
		default:
			throw new Error("Invalid location: " + loc);
		}
		
        final float factor = aElement.getMagnification() / SGIConstants.CM_POINT_RATIO;
		final float x = baseX + this.mExponentLocationX * factor;
		final float y = baseY + this.mExponentLocationY * factor;

		// set the location to the drawing element
		this.mExponentSymbol.setLocation(x, y);

		return true;
    }

    private boolean initLocationOfExponentDrawingElement() {
        SGDrawingElementString2D el = this.mExponentSymbol;
        final Rectangle2D rect = el.getElementBounds();
        final float mag = this.mAxisElement.getMagnification();
        final float spaceLN = (float) SGUtilityText.convert(this.getSpaceAxisLineAndNumbers(), 
        		SGIAxisConstants.SPACE_UNIT, SGIConstants.pt) * mag;
        final float spaceNT = (float) SGUtilityText.convert(this.getSpaceTitleAndNumbers(), 
        		SGIAxisConstants.SPACE_UNIT, SGIConstants.pt) * mag;
		float x = 0.0f;
		float y = 0.0f;
		final int loc = this.getLocationInPlane();
		switch (loc) {
		case SGIFigureElementAxis.AXIS_HORIZONTAL_1:
			x = 0.0f;
			y = (float) (spaceLN + getScaleHeight() + spaceNT) - this.getMagnifiedShift();
			break;
		case SGIFigureElementAxis.AXIS_HORIZONTAL_2:
			Rectangle2D titleRect = this.mTitle.getStringRect();
			x = 0.0f;
			y = - (float) (spaceLN + getScaleHeight() + spaceNT + titleRect.getHeight()) 
					+ this.getMagnifiedShift();
			break;
		case SGIFigureElementAxis.AXIS_VERTICAL_1:
			x = - (float) (spaceLN + rect.getWidth() + mag * this.getNumberFontSize()) 
					+ this.getMagnifiedShift();
			y = - (float) rect.getHeight();
			break;
		case SGIFigureElementAxis.AXIS_VERTICAL_2:
			x = spaceLN + mag * this.getNumberFontSize() - this.getMagnifiedShift();
			y = - (float) rect.getHeight();
			break;
		default:
			throw new Error("Invalid location: " + loc);
		}
		x /= mag;
		y /= mag;
		x = (float) SGUtilityText.convert(x, SGIConstants.pt, SGIAxisConstants.SPACE_UNIT);
		y = (float) SGUtilityText.convert(y, SGIConstants.pt, SGIAxisConstants.SPACE_UNIT);
		x = this.roundOffExponentShift(x);
		y = this.roundOffExponentShift(y);

		// set to the attributes
		this.mExponentLocationX = x;
		this.mExponentLocationY = y;

		return true;
    }

    /**
     * Creates a scale line.
     * 
     * @param value
     *            axis value of the scale line
     * @param length
     *            the length of the scale line
     * @return an instance of the scale line
     */
    protected ElementLineTickMark createSingleTickMark(final double value,
            final float width, final float length, final boolean inside) {
        if (this.mAxis.insideRange(value) == false) {
            return null;
        }
        final float gx = this.mAxisElement.getGraphRectX();
        final float gy = this.mAxisElement.getGraphRectY();
        final float gw = this.mAxisElement.getGraphRectWidth();
        final float gh = this.mAxisElement.getGraphRectHeight();
        final float mag = this.mAxisElement.getMagnification();
        final boolean invcoord = this.isInvertCoordinates();
        double min;
        double max;
        double valueInScale;
        final int scaleType = this.mAxis.getScaleType();
        switch (scaleType) {
        case SGAxis.LINEAR_SCALE:
            min = this.mAxis.getMinDoubleValue();
            max = this.mAxis.getMaxDoubleValue();
            valueInScale = value;
            break;
        case SGAxis.LOG_SCALE:
            min = Math.log(this.mAxis.getMinDoubleValue());
            max = Math.log(this.mAxis.getMaxDoubleValue());
            valueInScale = Math.log(value);
            break;
        default:
			throw new Error("Invalid scale type: " + scaleType);
        }

        final SGTuple2f start;
        final SGTuple2f end;
        final int loc = this.getLocationInPlane();
        final double x, y;
        final double xStart, xEnd, yStart, yEnd;
        final double range = max - min;
        switch (loc) {
        case SGIFigureElementAxis.AXIS_HORIZONTAL_1: // bottom
            if (invcoord) {
                x = gx + gw * (max - valueInScale) / range;
            } else {
                x = gx + gw * (valueInScale - min) / range;
            }
            yStart = gy + gh - this.getMagnifiedShift();
            if (inside) {
                yEnd = yStart - length;
            } else {
                yEnd = yStart + length;
            }
            start = new SGTuple2f((float) x, (float) yStart);
            end = new SGTuple2f((float) x, (float) yEnd);
            break;
        case SGIFigureElementAxis.AXIS_HORIZONTAL_2: // top
            if (invcoord) {
                x = gx + gw * (max - valueInScale) / range;
            } else {
                x = gx + gw * (valueInScale - min) / range;
            }
            yStart = gy + this.getMagnifiedShift();
            if (inside) {
                yEnd = yStart + length;
            } else {
                yEnd = yStart - length;
            }
            start = new SGTuple2f((float) x, (float) yStart);
            end = new SGTuple2f((float) x, (float) yEnd);
            break;
        case SGIFigureElementAxis.AXIS_VERTICAL_1: // left
            if (invcoord) {
                y = gy + gh * (1.0 - (max - valueInScale) / range);
            } else {
                y = gy + gh * (1.0 - (valueInScale - min) / range);
            }
            xStart = gx + this.getMagnifiedShift();
            if (inside) {
                xEnd = xStart + length;
            } else {
                xEnd = xStart - length;
            }
            start = new SGTuple2f((float) xStart, (float) y);
            end = new SGTuple2f((float) xEnd, (float) y);
            break;
        case SGIFigureElementAxis.AXIS_VERTICAL_2: // right
            if (invcoord) {
                y = gy + gh * (1.0 - (max - valueInScale) / range);
            } else {
                y = gy + gh * (1.0 - (valueInScale - min) / range);
            }
            xStart = gx + gw - this.getMagnifiedShift();
            if (inside) {
                xEnd = xStart - length;
            } else {
                xEnd = xStart + length;
            }
            start = new SGTuple2f((float) xStart, (float) y);
            end = new SGTuple2f((float) xEnd, (float) y);
            break;
        default:
			throw new Error("Invalid location: " + loc);
        }

        // creates an instance
        ElementLineTickMark el = new ElementLineTickMark(start, end, this);
        el.setLineWidth(width);
        el.setMagnification(mag);
        
        return el;
    }

    /**
     * Called when a string of the scale number is dragged.
     */
    protected AxisValueRange dragScaleNumber(MouseEvent e) {
        final int loc = this.getLocationInPlane();
        ElementLineAxis line = this.getLine();
        AxisValueRange range = null;
        switch(loc) {
        case AXIS_HORIZONTAL_1:
        case AXIS_HORIZONTAL_2:
            // horizontal axes
        	range = this.dragScaleNumberHorizontal(e, line);
        	break;
        case AXIS_VERTICAL_1:
        case AXIS_VERTICAL_2:
            // vertical axes
        	range = this.dragScaleNumberPerpendiculer(e, line);
        	break;
       	default:
            throw new Error("Invalid location: " + loc);
        }
        return range;
    }

    /**
     * Called when a string of the scale number is dragged.
     */
    protected AxisValueRange dragElementLineOfScale(MouseEvent e) {
        final int loc = this.getLocationInPlane();
        ElementLineAxis line = this.getLine();
        AxisValueRange range = null;
        switch(loc) {
        case AXIS_HORIZONTAL_1:
        case AXIS_HORIZONTAL_2:
            // horizontal axes
        	range = this.dragScaleLineHorizontal(e, line);
        	break;
        case AXIS_VERTICAL_1:
        case AXIS_VERTICAL_2:
            // vertical axes
        	range = this.dragScaleLinePerpendiculer(e, line);
        	break;
       	default:
            throw new Error("Invalid location: " + loc);
        }
        return range;
    }

    /**
     * Returns the shift of axis line.
     * 
     * @return the shift of axis line
     */
    public float getShift() {
    	return this.mShift;
    }

    /**
     * Sets the shift of axis line.
     * 
     * @param shift
     *            the shift of axis line to set
     * @return true if succeeded
     */
    public void setShift(final float shift) {
		this.mShift = shift;
    }
    
    /**
     * Returns the shift of axis line in a given unit.
     * 
     * @param unit
     *           the unit of length
     * @return the shift of axis line
     */
    public float getShift(String unit) {
    	return (float) SGUtilityText.convert(this.getShift(), AXIS_SHIFT_UNIT, unit);
    }
    
    /**
     * Sets the shift of axis line.
     * 
     * @param shift
     *            the shift of axis line to set
     * @param unit
     *            the unit of length
     * @return true if succeeded
     */
    public boolean setShift(final float shift, final String unit) {
		final Float sNew = SGUtility.calcPropertyValueInUnit(shift, unit,
				AXIS_SHIFT_UNIT, AXIS_SHIFT_MIN, AXIS_SHIFT_MAX,
				AXIS_SHIFT_MINIMAL_ORDER);
		if (sNew == null) {
			return false;
		}
		this.setShift(sNew.floatValue());
		return true;
    }

    public SGProperties getProperties() {
    	FigureAxisProperties p = new FigureAxisProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }
    
    public boolean getProperties(SGProperties p) {
    	if (!(p instanceof FigureAxisProperties)) {
    		return false;
    	}
    	if (super.getProperties(p) == false) {
    		return false;
    	}
    	FigureAxisProperties ap = (FigureAxisProperties) p;
    	ap.shift = this.getShift();
        return true;
    }

    public boolean setProperties(SGProperties p) {
    	if (!(p instanceof FigureAxisProperties)) {
    		return false;
    	}
    	if (super.setProperties(p) == false) {
    		return false;
    	}
    	FigureAxisProperties ap = (FigureAxisProperties) p;
    	this.setShift(ap.shift);
        return true;
    }
    
    /**
	 * Sets the properties of this axis.
	 * 
	 * @param map
	 *            a map of properties
	 * @param iResult
	 *            the input result
	 * @return the updated result of setting properties
	 */
    protected SGPropertyResults setProperties(SGPropertyMap map,
    		SGPropertyResults iResult) {
        SGPropertyResults result = (SGPropertyResults) iResult.clone();
        
        // merges the results
        SGPropertyResults supResult = super.setProperties(map, iResult);
        Iterator<String> supItr = supResult.getKeyIterator();
        while (supItr.hasNext()) {
            String key = supItr.next();
            Integer status = supResult.getResult(key);
            String oriKey = supResult.getOriginalKey(key);
            result.putResult(oriKey, status);
        }        

        Iterator<String> itr = map.getKeyIterator();

        // find date mode flag
        Boolean dateModeFlag = null;
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);
            if (COM_AXIS_DATE_MODE.equalsIgnoreCase(key)) {
            	Boolean b = SGUtilityText.getBoolean(value);
            	dateModeFlag = b;
            }
        }
        final boolean dateMode = (dateModeFlag != null) ? dateModeFlag : this.getDateMode();
        
        itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);
            if (COM_AXIS_SHIFT.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_AXIS_SHIFT,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setShift(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_AXIS_SHIFT,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_SHIFT,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_DATE_MODE.equalsIgnoreCase(key)) {
            	Boolean b = SGUtilityText.getBoolean(value);
            	if (b == null) {
                    result.putResult(COM_AXIS_DATE_MODE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
            	if (this.setDateMode(b) == false) {
                    result.putResult(COM_AXIS_DATE_MODE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                result.putResult(COM_AXIS_DATE_MODE,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_DATE_FORMAT.equalsIgnoreCase(key)) {
            	if (map.isDoubleQuoted(key) == false) {
                    result.putResult(COM_AXIS_DATE_FORMAT, 
                    		SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
            	if (!"".equals(value)) {
    				if (SGDateUtility.isValidDateFormat(value) == false) {
                        result.putResult(COM_AXIS_DATE_FORMAT,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
            	}
            	if (this.setNumberDateFormat(value) == false) {
                    result.putResult(COM_AXIS_DATE_FORMAT,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                result.putResult(COM_AXIS_DATE_FORMAT,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_SCALE_MIN_VALUE.equalsIgnoreCase(key)) {
            	this.setScaleMinValue(map, key, value, dateMode, result);
            } else if (COM_AXIS_SCALE_MAX_VALUE.equalsIgnoreCase(key)) {
            	this.setScaleMaxValue(map, key, value, dateMode, result);
            } else if (COM_AXIS_SCALE_RANGE.equalsIgnoreCase(key)) {
            	this.setScaleRange(map, key, value, dateMode, result);
            } else if (COM_AXIS_SCALE_BASE.equalsIgnoreCase(key)
            		|| COM_AXIS_TICK_MARK_BASE.equalsIgnoreCase(key)) {
            	this.setScaleBaselineValue(map, key, value, dateMode, result);
            } else if (COM_AXIS_SCALE_STEP.equalsIgnoreCase(key)
            		|| COM_AXIS_TICK_MARK_STEP.equalsIgnoreCase(key)) {
            	this.setScaleStepValue(map, key, value, dateMode, result);
            } else if (COM_AXIS_LINE_WIDTH.equalsIgnoreCase(key)) {
            	// for backward compatibility
            	String k = map.getOriginalKey(key);
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setAxisLineWidth(num.floatValue(), unit.toString()) == false) {
                    result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(k, SGPropertyResults.SUCCEEDED);
			} else if (COM_AXIS_LINE_COLOR.equalsIgnoreCase(key)) {
            	// for backward compatibility
            	String k = map.getOriginalKey(key);
				Color cl = SGUtilityText.getColor(value);
				if (cl != null) {
					if (this.setAxisLineColor(cl) == false) {
						result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				} else {
					cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setAxisLineColor(cl) == false) {
						result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				}
				result.putResult(k, SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_SPACE_TO_SCALE.equalsIgnoreCase(key)) {
            	// for backward compatibility
            	String k = map.getOriginalKey(key);
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setSpaceAxisLineAndNumbers(num.floatValue(), unit.toString()) == false) {
                    result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(k, SGPropertyResults.SUCCEEDED);
            }
        }
        return result;
    }
    
//    /**
//     * Write the properties to an given Element object.
//     * 
//     * @param el
//     *          an Element object
//     * @return true if succeeded
//     */
//    public boolean writeProperty(final Element el, SGExportParameter params) {
//        if (super.writeProperty(el, params) == false) {
//            return false;
//        }
//        
//        // shift
//		final float shift = this.roundOffAxisShift(this.getShift());
//		el.setAttribute(KEY_SHIFT, Float.toString(shift) + AXIS_SHIFT_UNIT);
//		
//        return true;
//    }

    /**
     * Read properties from a given Element object.
     * 
     * @param element
     *           an Element object
     * @param versionNumber
     *           version number
     * @return true if succeeded
     */
    protected boolean readProperties(final Element element, final String versionNumber) {
        if (super.readProperties(element, versionNumber) == false) {
            return false;
        }
        String str;
        
        str = element.getAttribute(KEY_SHIFT);
        if (str.length() != 0) {
            StringBuffer uShift = new StringBuffer();
            Number num = SGUtilityText.getNumber(str, uShift);
        	if (num != null) {
                if (this.setShift(num.floatValue(), uShift.toString()) == false) {
                    return false;
                }
        	}
        }

        boolean dateMode = false;
        str = element.getAttribute(KEY_DATE_MODE);
        if (str.length() != 0) {
        	Boolean b = SGUtilityText.getBoolean(str);
        	if (b != null) {
        		if (!this.setDateMode(b)) {
        			return false;
        		}
        		dateMode = b;
        	}
        }
        
		if (!this.readScaleProperties(element, versionNumber, dateMode)) {
			return false;
		}

        return true;
    }

	/**
	 * Returns the list of anchor points.
	 * 
	 * @return the list of anchor points
	 */
	List<Point2D> getAnchorPoints() {
		List<Point2D> pList = new ArrayList<Point2D>();
    	if (this.isVisible()) {
        	if (this.isSelected()) {
            	// draw anchors
            	ElementLineAxis line = this.getLine();
            	SGTuple2f start = line.getStart();
            	SGTuple2f end = line.getEnd();
                float alpha, beta;
                pList.add(new Point2D.Float(start.x, start.y));
                alpha = 0.25f;
                beta = 0.75f;
                pList.add(new Point2D.Float(alpha * start.x + beta * end.x, alpha * start.y + beta * end.y));
                alpha = 0.75f;
                beta = 0.25f;
                pList.add(new Point2D.Float(alpha * start.x + beta * end.x, alpha * start.y + beta * end.y));
                pList.add(new Point2D.Float(end.x, end.y));
        	}
    	}
		return pList;
	}

    /**
     * Called when the mouse is dragged.
     * 
     * @param e
     *          the mouse event
     * @return true if succeeded
     */
	@Override
    public boolean onMouseDragged(final MouseEvent e) {
    	if (super.onMouseDragged(e)) {
    		// if mouse drag is succeeded for scale numbers or lines,
    		// returns true
    		return true;
    	}
    	
    	Point pos = this.mAxisElement.getPressedPoint();
    	if (this.mDraggingElement instanceof ElementLineAxis) {
    		// an axis line is dragged
    		if (pos != null) {
    			return true;
    		}
    	} else if (this.mTitle.equals(this.mDraggingElement)) {
			return true;
    	} else if (this.mExponentSymbol.equals(this.mDraggingElement)) {
			return true;
    	}
    	
		return false;
    }
	
    /**
     * Translates this object.
     * 
     * @param dx
     *           the x-axis displacement
     * @param dy
     *           the y-axis displacement
     */
	@Override
	public void translate(float dx, float dy) {
		final float mag = this.mAxisElement.getMagnification();
		final SGAxis axis = this.getAxis();
		final int location = this.mAxisElement.getLocationInPlane(axis);
		
		// difference in units of pt
		float diff = 0.0f;
		switch (location) {
		case SGIFigureElementAxisConstants.AXIS_HORIZONTAL_1:
			diff = - dy;
			break;
		case SGIFigureElementAxisConstants.AXIS_HORIZONTAL_2:
			diff = dy;
			break;
		case SGIFigureElementAxisConstants.AXIS_VERTICAL_1:
			diff = dx;
			break;
		case SGIFigureElementAxisConstants.AXIS_VERTICAL_2:
			diff = - dx;
			break;
		default:
			throw new Error("Unsupported axis location: " + location);
		}
		diff /= mag;
		
		// transform into a value in units of cm
		diff *= SGIConstants.CM_POINT_RATIO;
		float shift = this.getShift() + diff;
		this.setShift(shift);
		this.updateLocationOfDrawingElements();
	}

    /**
     * Called when the mouse is released.
     * 
     * @param e
     *          the mouse event
     * @return true if succeeded
     */
	@Override
    public boolean onMouseReleased(final MouseEvent e) {
        if (!this.mVisible) {
            return false;
        }
        try {
            if (this.mDraggingElement instanceof ElementLineAxis) {
            	// if axis line is dragged
        		float shift = this.getShift();
        		if (shift < AXIS_SHIFT_MIN) {
        			shift = (float) AXIS_SHIFT_MIN;
        		} else if (shift > AXIS_SHIFT_MAX) {
        			shift = (float) AXIS_SHIFT_MAX;
        		}
        		shift = this.roundOffAxisShift(shift);
        		this.setShift(shift);
        		this.updateLocationOfDrawingElements();
        		return true;
            } else if (this.mTitle.equals(this.mDraggingElement)) {
            	// if the title is dragged
            	this.onTitleReleased();
            	return true;
            } else if (this.mExponentSymbol.equals(this.mDraggingElement)) {
            	// if the exponent symbol is dragged
            	this.onExponentReleased();
            	return true;
            } else if (this.mDraggingElement instanceof ElementStringNumber
            		|| this.mDraggingElement instanceof ElementLineTickMark) {
                // if a number or a tick mark is dragged
            	this.setChangedFocusedObjects();
            	this.onMouseReleasedNumberOrTickMark();
                return true;
            }
        } finally {
        	this.onMouseReleasedFinally();
        }
    	return false;
    }

	private float roundOffAxisShift(final float value) {
		final int digitShift = AXIS_SHIFT_MINIMAL_ORDER - 1;
		final float shift = (float) SGUtilityNumber.roundOffNumber(
				value, digitShift);
		return shift;
	}

	@Override
	public SGPropertyDialog getPropertyDialog() {
        return this.mAxisElement.mPropertyDialog;
	}

	@Override
	protected String getLineWidthPropertyFileKey() {
		return KEY_AXIS_LINE_WIDTH;
	}

	@Override
	protected String getLineColorPropertyFileKey() {
		return KEY_AXIS_LINE_COLOR;
	}

	@Override
	protected String getSpaceLineAndNumberPropertyFileKey() {
		return KEY_SPACE_AXIS_LINE_AND_NUMBERS;
	}

	@Override
	protected String getLineWidthCommandKey() {
		return COM_AXIS_AXIS_LINE_WIDTH;
	}

	@Override
	protected String getLineColorCommandKey() {
		return COM_AXIS_AXIS_LINE_COLOR;
	}

	@Override
	protected String getSpaceLineAndNumberCommandKey() {
		return COM_AXIS_SPACE_AXIS_LINE_AND_NUMBER;
	}

    /**
     * Creates and returns the map of properties.
     * 
     * @return the map of properties
     */
	@Override
    public SGPropertyMap getPropertyMap() {
    	SGPropertyMap map = super.getPropertyMap();
    	
    	// shift
    	SGPropertyUtility.addProperty(map, COM_AXIS_SHIFT, 
    			this.getShift(AXIS_SHIFT_UNIT), AXIS_SHIFT_UNIT);
    	
    	// date mode
    	SGPropertyUtility.addProperty(map, COM_AXIS_DATE_MODE, 
    			this.getDateMode());

    	// date format
    	SGPropertyUtility.addQuotedStringProperty(map, 
    			COM_AXIS_DATE_FORMAT, this.getNumberDateFormat());

    	return map;
    }

    /**
     * Creates and returns the map of properties for the property file.
     * 
     * @return the map of properties
     */
	@Override
    public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    	SGPropertyMap map = super.getPropertyFileMap(params);
    	
    	// position
        final int loc = this.getLocationInPlane();
        final String locStr = this.mAxisElement.getLocationName(loc);
    	SGPropertyUtility.addProperty(map, KEY_POSITION, locStr);

    	// date mode
    	SGPropertyUtility.addProperty(map, KEY_DATE_MODE, this.getDateMode());

        // shift
		final float shift = this.roundOffAxisShift(this.getShift());
    	SGPropertyUtility.addProperty(map, KEY_SHIFT, 
    			Float.toString(shift) + AXIS_SHIFT_UNIT);

    	// date format
    	SGPropertyUtility.addProperty(map, KEY_NUMBER_DATE_FORMAT, 
    			this.getNumberDateFormat());

    	return map;
    }
	
	@Override
    protected void drawAxisLine(final Graphics2D g2d) {
    	if (this.isAxisLineVisible()) {
    		super.drawAxisLine(g2d);
    	}
    }

	@Override
	protected String getLineVisiblePropertyFileKey() {
		return KEY_AXIS_LINE_VISIBLE;
	}

	@Override
	protected String getLineVisibleCommandKey() {
		return COM_AXIS_LINE_VISIBLE;
	}
}


/**
 * Properties for a single axis.
 * 
 */
class FigureAxisProperties extends AxisProperties {
	
	float shift;
	
    /**
     * Returns whether a given object is equal to this.
     * 
     * @param obj
     *           an object
     * @return true if a given object is equals to this
     */
    public boolean equals(final Object obj) {
        if ((obj instanceof FigureAxisProperties) == false) {
            return false;
        }
        if (super.equals(obj) == false) {
        	return false;
        }
        FigureAxisProperties p = (FigureAxisProperties) obj;
        if (p.shift != this.shift) {
            return false;
        }
        return true;
    }
}

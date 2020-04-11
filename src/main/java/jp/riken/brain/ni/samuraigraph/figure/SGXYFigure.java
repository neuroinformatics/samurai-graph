package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import jp.riken.brain.ni.samuraigraph.base.SGAxisDateStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDateValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisValue;
import jp.riken.brain.ni.samuraigraph.base.SGCommandUtility;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxisBreak;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGrid;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementShape;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementSignificantDifference;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementString;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementTimingLine;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGFigureElementGrid.GridProperties;

import org.joda.time.Period;

/**
 * An XY-type figure.
 * 
 */
public class SGXYFigure extends SGFigure implements SGIXYFigureDialogObserver,
        SGIFigureTypeConstants {

    /**
     * Builds a figure.
     * 
     * @param wnd
     *           the parent window
     */
    public SGXYFigure(SGDrawingWindow wnd) {
        super(wnd);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mPropertyDialog = new SGXYFigureDialog(mWnd, true);
            }
        });
    }

    /**
     * Returns the type of this class.
     * 
     * @return The type of class.
     */
    public String getType() {
        return FIGURE_TYPE_XY;
    }

    /**
     * Returns a property dialog.
     * @return
     *        a property dialog
     */
    public SGPropertyDialog getPropertyDialog() {
        SGPropertyDialog dg = null;
        if (this.mPropertyDialog != null) {
            dg = this.mPropertyDialog;
        } else {
            dg = new SGXYFigureDialog(this.mWnd, true);
            this.mPropertyDialog = dg;
        }
        return dg;
    }

    public SGIFigureElement getSymbolElement(final int symbolElementType) {
        Class cl = null;
        switch (symbolElementType) {
        case SGIDrawingElementConstants.SYMBOL_ELEMENT_TYPE_STRING:
            cl = SGIFigureElementString.class;
            break;
        case SGIDrawingElementConstants.SYMBOL_ELEMENT_TYPE_AXISBREAK:
            cl = SGIFigureElementAxisBreak.class;
            break;
        case SGIDrawingElementConstants.SYMBOL_ELEMENT_TYPE_SIGDIFF:
            cl = SGIFigureElementSignificantDifference.class;
            break;
        case SGIDrawingElementConstants.SYMBOL_ELEMENT_TYPE_TIMINGLINE:
            cl = SGIFigureElementTimingLine.class;
            break;
        case SGIDrawingElementConstants.SYMBOL_ELEMENT_TYPE_SHAPE:
            cl = SGIFigureElementShape.class;
            break;
        }
        if (cl == null)
            return null;
        return this.getIFigureElement(cl);
    }

    /**
     * 
     */
    public int getXAxisLocation() {
        return this.getGridElement().getXAxisLocation();
    }

    /**
     * 
     */
    public int getYAxisLocation() {
        return this.getGridElement().getYAxisLocation();
    }

    public boolean setXAxisLocation(final int location) {
        return this.getGridElement().setXAxisLocation(location);
    }

    public boolean setYAxisLocation(final int location) {
        return this.getGridElement().setYAxisLocation(location);
    }

    public boolean setGridVisible(final boolean b) {
        return this.getGridElement().setGridVisible(b);
    }

    public boolean setAutoCalculateRange(final boolean b) {
        return this.getGridElement().setAutoRangeFlag(b);
    }

    public boolean setGridStepValueX(final SGAxisStepValue value) {
        return this.getGridElement().setStepValueX(value);
    }

    public boolean setGridStepValueY(final SGAxisStepValue value) {
        return this.getGridElement().setStepValueY(value);
    }

    public boolean setGridBaselineValueX(final SGAxisValue value) {
        return this.getGridElement().setBaselineValueX(value);
    }

    public boolean setGridBaselineValueY(final SGAxisValue value) {
        return this.getGridElement().setBaselineValueY(value);
    }

    public boolean setGridLineWidth(final float width, final String unit) {
        return this.getGridElement().setLineWidth(width, unit);
    }

    public boolean setGridLineType(final int type) {
        return this.getGridElement().setLineType(type);
    }

    public boolean setGridLineColor(final Color cl) {
        return this.getGridElement().setColor(cl);
    }

    public boolean isAutoCalculateRange() {
        return this.getGridElement().isAutoRange();
    }

    public SGAxisStepValue getGridStepValueX() {
        return this.getGridElement().getStepValueX();
    }

    public SGAxisStepValue getGridStepValueY() {
        return this.getGridElement().getStepValueY();
    }

    public SGAxisValue getGridBaselineValueX() {
        return this.getGridElement().getBaselineValueX();
    }

    public SGAxisValue getGridBaselineValueY() {
        return this.getGridElement().getBaselineValueY();
    }

    public float getGridLineWidth(final String unit) {
        return this.getGridElement().getLineWidth(unit);
    }

    public int getGridLineType() {
        return this.getGridElement().getLineType();
    }

    public Color getGridLineColor() {
        return this.getGridElement().getColor();
    }

    public boolean hasValidStepXValue(final SGAxisStepValue step) {
        return this.getGridElement().hasValidStepXValue(step);
    }

    public boolean hasValidStepYValue(final SGAxisStepValue step) {
        return this.getGridElement().hasValidStepYValue(step);
    }

    @Override
    public SGProperties getProperties() {
        XYFigureProperties p = new XYFigureProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    @Override
    public boolean getProperties(SGProperties p) {
        if ((p instanceof XYFigureProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        XYFigureProperties fp = (XYFigureProperties) p;
        fp.frameVisible = this.isFrameLineVisible();
        fp.frameLineWidth = this.getFrameLineWidth();
        fp.frameLineColor = this.getFrameLineColor();
        SGIFigureElementGrid el = this.getGridElement();
        fp.gridProperties = (GridProperties) el.getProperties();
        return true;
    }

    @Override
    public boolean setProperties(SGProperties p) {
        if ((p instanceof XYFigureProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        XYFigureProperties fp = (XYFigureProperties) p;
        this.setFrameVisible(fp.frameVisible);
        this.setFrameLineWidth(fp.frameLineWidth);
        this.setFrameLineColor(fp.frameLineColor);
        SGIFigureElementGrid el = this.getGridElement();
        if (!el.setProperties(fp.gridProperties)) {
        	return false;
        }
        return true;
    }

    /**
     * Properties of an XY-type figure.
     * 
     */
    public static class XYFigureProperties extends FigureProperties {
    	
        boolean frameVisible;
        
        float frameLineWidth;
        
        Color frameLineColor;

    	GridProperties gridProperties;

        public XYFigureProperties() {
            super();
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj instanceof XYFigureProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            XYFigureProperties p = (XYFigureProperties) obj;
            if (this.frameVisible != p.frameVisible) {
            	return false;
            }
            if (this.frameLineWidth != p.frameLineWidth) {
            	return false;
            }
            if (!SGUtility.equals(this.frameLineColor, p.frameLineColor)) {
            	return false;
            }
            if (!SGUtility.equals(this.gridProperties, p.gridProperties)) {
            	return false;
            }
            return true;
        }
    }
    
    protected SGPropertyResults setProperties(SGPropertyMap map,
            SGPropertyResults iResult) {
        
        SGPropertyResults result = (SGPropertyResults) iResult.clone();
        result = super.setProperties(map, result);
        if (result == null) {
            return null;
        }

        final boolean dateModeX = this.getAxisDateMode(this.getXAxisLocation());
        final boolean dateModeY = this.getAxisDateMode(this.getYAxisLocation());
        
        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);
            if (COM_FIGURE_GRID_VISIBLE.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_FIGURE_GRID_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setGridVisible(b.booleanValue()) == false) {
                    result.putResult(COM_FIGURE_GRID_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_GRID_VISIBLE, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_GRID_AXIS_X.equalsIgnoreCase(key)) {
                final int loc = SGUtility.getAxisLocation(value);
                if (loc == -1) {
                    result.putResult(COM_FIGURE_GRID_AXIS_X, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setXAxisLocation(loc) == false) {
                    result.putResult(COM_FIGURE_GRID_AXIS_X, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_GRID_AXIS_X, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_GRID_AXIS_Y.equalsIgnoreCase(key)) {
                final int loc = SGUtility.getAxisLocation(value);
                if (loc == -1) {
                    result.putResult(COM_FIGURE_GRID_AXIS_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setYAxisLocation(loc) == false) {
                    result.putResult(COM_FIGURE_GRID_AXIS_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_GRID_AXIS_Y, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_GRID_AUTO.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_FIGURE_GRID_AUTO, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setAutoCalculateRange(b.booleanValue()) == false) {
                    result.putResult(COM_FIGURE_GRID_AUTO, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_GRID_AUTO, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_GRID_BASE_X.equalsIgnoreCase(key)) {
            	final SGAxisValue axisValue;
            	if (dateModeX) {
            		SGDate date = SGUtilityText.getDate(value);
            		if (date == null) {
                        result.putResult(COM_FIGURE_GRID_BASE_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
            		}
            		axisValue = new SGAxisDateValue(date);
            	} else {
                    Number num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_FIGURE_GRID_BASE_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                	if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_FIGURE_GRID_BASE_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                	axisValue = new SGAxisDoubleValue(num.doubleValue());
            	}
                if (this.setGridBaselineValueX(axisValue) == false) {
                    result.putResult(COM_FIGURE_GRID_BASE_X, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_GRID_BASE_X, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_GRID_STEP_X.equalsIgnoreCase(key)) {
            	final SGAxisStepValue step;
            	if (dateModeX) {
            		Period p = SGUtilityText.getPeriod(value);
                    if (p == null) {
                        result.putResult(COM_FIGURE_GRID_STEP_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
            		step = new SGAxisDateStepValue(p);
            	} else {
                    Number num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_FIGURE_GRID_STEP_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                	if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_FIGURE_GRID_STEP_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                	step = new SGAxisDoubleStepValue(num.doubleValue());
            	}
                if (this.setGridStepValueX(step) == false) {
                    result.putResult(COM_FIGURE_GRID_STEP_X, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_GRID_STEP_X, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_GRID_BASE_Y.equalsIgnoreCase(key)) {
            	final SGAxisValue axisValue;
            	if (dateModeY) {
            		SGDate date = SGUtilityText.getDate(value);
            		if (date == null) {
                        result.putResult(COM_FIGURE_GRID_BASE_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
            		}
            		axisValue = new SGAxisDateValue(date);
            	} else {
                    Number num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_FIGURE_GRID_BASE_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                	if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_FIGURE_GRID_BASE_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                	axisValue = new SGAxisDoubleValue(num.doubleValue());
            	}
                if (this.setGridBaselineValueY(axisValue) == false) {
                    result.putResult(COM_FIGURE_GRID_BASE_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_GRID_BASE_Y, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_GRID_STEP_Y.equalsIgnoreCase(key)) {
            	final SGAxisStepValue step;
            	if (dateModeY) {
            		Period p = SGUtilityText.getPeriod(value);
                    if (p == null) {
                        result.putResult(COM_FIGURE_GRID_STEP_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
            		step = new SGAxisDateStepValue(p);
            	} else {
                    Number num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_FIGURE_GRID_STEP_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                	if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_FIGURE_GRID_STEP_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                	step = new SGAxisDoubleStepValue(num.doubleValue());
            	}
                if (this.setGridStepValueY(step) == false) {
                    result.putResult(COM_FIGURE_GRID_STEP_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_GRID_STEP_Y, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_GRID_LINE_WIDTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_FIGURE_GRID_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setGridLineWidth(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_FIGURE_GRID_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_GRID_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_GRID_LINE_TYPE.equalsIgnoreCase(key)) {
                final Integer type = SGDrawingElementLine.getLineTypeFromName(value);
                if (type == null) {
                    result.putResult(COM_FIGURE_GRID_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setGridLineType(type) == false) {
                    result.putResult(COM_FIGURE_GRID_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_FIGURE_GRID_LINE_TYPE, SGPropertyResults.SUCCEEDED);
            } else if (COM_FIGURE_GRID_LINE_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setGridLineColor(cl) == false) {
                        result.putResult(COM_FIGURE_GRID_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(COM_FIGURE_GRID_LINE_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setGridLineColor(cl) == false) {
						result.putResult(COM_FIGURE_GRID_LINE_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
                }
                result.putResult(COM_FIGURE_GRID_LINE_COLOR, SGPropertyResults.SUCCEEDED);
            }
        }
            
        return result;
    }

	@Override
	public boolean isFrameLineVisible() {
		SGIFigureElementAxis aElement = this.getAxisElement();
		return aElement.isFrameLineVisible();
	}

	public float getFrameLineWidth() {
		SGIFigureElementAxis aElement = this.getAxisElement();
		return aElement.getFrameLineWidth();
	}

	@Override
	public float getFrameLineWidth(String unit) {
		SGIFigureElementAxis aElement = this.getAxisElement();
		return aElement.getFrameLineWidth(unit);
	}

	@Override
	public Color getFrameLineColor() {
		SGIFigureElementAxis aElement = this.getAxisElement();
		return aElement.getFrameLineColor();
	}

	@Override
	public boolean setFrameVisible(boolean b) {
		SGIFigureElementAxis aElement = this.getAxisElement();
		return aElement.setFrameVisible(b);
	}

	public boolean setFrameLineWidth(float lw) {
		SGIFigureElementAxis aElement = this.getAxisElement();
		return aElement.setFrameLineWidth(lw);
	}

	@Override
	public boolean setFrameLineWidth(float lw, String unit) {
		SGIFigureElementAxis aElement = this.getAxisElement();
		return aElement.setFrameLineWidth(lw, unit);
	}

	@Override
	public boolean setFrameLineColor(Color cl) {
		SGIFigureElementAxis aElement = this.getAxisElement();
		return aElement.setFrameLineColor(cl);
	}

    /**
     * Creates and returns the map of properties for the property file.
     * 
     * @return the map of properties
     */
	@Override
    public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    	SGPropertyMap map = super.getPropertyFileMap(params);
    	
    	// frame
    	this.addFrameProperties(map, COM_FIGURE_FRAME_VISIBLE, COM_FIGURE_FRAME_LINE_WIDTH, 
    			COM_FIGURE_FRAME_COLOR);
    	
    	// NOTE: Grid properties are not added to this map.
    	return map;
    }

    /**
     * Creates and returns the map of properties.
     * 
     * @return the map of properties
     */
	@Override
    public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
    	SGPropertyMap map = super.getCommandPropertyMap(params);
    	
    	// frame
    	this.addFrameProperties(map, COM_FIGURE_FRAME_VISIBLE, COM_FIGURE_FRAME_LINE_WIDTH, 
    			COM_FIGURE_FRAME_COLOR);
    	
    	return map;
    }
	
	// Override to export command string for grid after that of figure element 
	// is exported.
	@Override
	public String getCommandString(SGExportParameter params) {
		String figureCommandString = super.getCommandString(params);
		StringBuffer sb = new StringBuffer();
		sb.append(figureCommandString);
		String gridCommands = SGCommandUtility.createCommandString(COM_FIGURE, 
				Integer.toString(this.getID()), 
				this.getGridCommandPropertyMap(params));
		sb.append(gridCommands);
		return sb.toString();
	}
	
	private SGPropertyMap getGridCommandPropertyMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	SGIFigureElementGrid gridElement = this.getGridElement();
    	map.putAll(gridElement.getCommandPropertyMap(params));
    	return map;
	}
	
    private void addFrameProperties(SGPropertyMap map, String frameVisibleKey, 
    		String frameLineWidthKey, String frameColorKey) {
    	SGPropertyUtility.addProperty(map, frameVisibleKey, 
    			this.isFrameLineVisible());
    	SGPropertyUtility.addProperty(map, frameLineWidthKey, 
    			SGUtility.getExportLineWidth(this.getFrameLineWidth(LINE_WIDTH_UNIT)), 
    			LINE_WIDTH_UNIT);
    	SGPropertyUtility.addProperty(map, frameColorKey, 
    			this.getFrameLineColor());
    }

    /*
	@Override
	public boolean getGridDateMode(boolean horizontal) {
		SGIFigureElementGrid gElement = this.getGridElement();
		SGIFigureElementAxis aElement = this.getAxisElement();
		final int location = horizontal ? gElement.getXAxisLocation() 
				: gElement.getYAxisLocation();
		SGAxis axis = aElement.getAxisInPlane(location);
		return axis.getDateMode();
	}
	*/

	@Override
	public boolean getAxisDateMode(final int location) {
		return this.getAxisElement().getAxisDateMode(location);
	}

    public void actionPerformed(final ActionEvent e) {
    	super.actionPerformed(e);
        final String command = e.getActionCommand();
        final Object source = e.getSource();
        if (source instanceof SGIFigureElementGrid) {
        	// When grid properties are changed, sets the changed flag
        	// to this figure.
            if (command.equals(SGIFigureElement.NOTIFY_CHANGE)) {
            	this.setChanged(true);
            }
        }
    }
}

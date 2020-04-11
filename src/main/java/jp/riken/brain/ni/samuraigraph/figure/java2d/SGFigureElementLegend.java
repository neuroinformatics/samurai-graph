package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap.ColorMapProperties;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGICopiable;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxisBreak;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGrid;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementLegend;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementShape;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementSignificantDifference;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementString;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementTimingLine;
import jp.riken.brain.ni.samuraigraph.base.SGIMovable;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGISelectable;
import jp.riken.brain.ni.samuraigraph.base.SGIUndoable;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGStyle;
import jp.riken.brain.ni.samuraigraph.base.SGTransparentPaint;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGISXYMultipleDimensionData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYZTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGIVXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGPickUpDimensionInfo;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFMultipleData;
import jp.riken.brain.ni.samuraigraph.figure.SGColorMapManager;
import jp.riken.brain.ni.samuraigraph.figure.SGColorMapManager.HueColorMap;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementArrow;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroupSet;
import jp.riken.brain.ni.samuraigraph.figure.SGIArrowConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGILegendConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYZDataConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIStringConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIVXYDataConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyle;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyleColorMapManager;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;
import jp.riken.brain.ni.samuraigraph.figure.SGUtilityForFigureElement;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupBar.BarInGroup;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupPseudocolorMap.PseudocolorMapRectangle;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupSetInGraphSXY.SXYElementGroupSetPropertiesInFigureElement;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupSetInGraphSXYMultiple.MultipleSXYElementGroupSetPropertiesInFigureElement;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupSetInGraphVXY.ElementGroupSetInVXYGraphProperties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The legend which displays symbols of data objects.
 */
public class SGFigureElementLegend extends SGFigureElementForData implements
		SGIFigureElementLegend, SGIStringConstants, CaretListener,
		DocumentListener, KeyListener, ActionListener, SGILegendConstants,
		SGIFigureDrawingElementConstants, SGIMovable, SGILegendDialogObserver {

	/**
	 * The graph figure element.
	 */
	private SGIFigureElementGraph mGraphElement = null;

	/**
	 * The x-axis.
	 */
	private SGAxis mXAxis = null;

	/**
	 * The y-axis.
	 */
	private SGAxis mYAxis = null;

	/**
	 * Relative x-coordinate from the origin of the graph rectangle
	 * at the default magnification.
	 */
	private float mLegendX = 0.0f;

	/**
	 * Relative y-coordinate from the origin of the graph rectangle
	 * at the default magnification.
	 */
	private float mLegendY = 0.0f;

	/**
	 * The width at the default magnification.
	 */
	private float mLegendWidth = 0.0f;

	/**
	 * The height at the default magnification.
	 */
	private float mLegendHeight = 0.0f;

	/**
	 * Font size for data names.
	 */
	private float mFontSize;

	/**
	 * Font style for data names.
	 */
	private int mFontStyle;

	/**
	 * Font name for data names.
	 */
	private String mFontName;

	/**
	 * Font color for data names.
	 */
	private Color mFontColor;

	/**
	 * Span width of symbols.
	 */
	private float mSymbolSpan;

	/**
	 * Line width of frame lines.
	 */
	private float mFrameLineWidth;

	/**
	 * Color of frame lines.
	 */
	private Color mFrameLineColor;

	/**
	 * Background paint.
	 */
	private SGFillPaint mBackgroundPaint = new SGFillPaint();

	/**
	 * The flag for visibility of legend.
	 */
	private boolean mLegendVisibleFlag = true;

	/**
	 * The flag for visibility of frame lines.
	 */
	private boolean mFrameVisibleFlag = true;

	/**
	 * Temporary properties.
	 */
	private SGProperties mTemporaryProperties = null;

	/**
	 * The pop-up menu.
	 */
	private JPopupMenu mPopupMenu = null;

	/**
	 * A property dialog for legend.
	 */
	private SGPropertyDialog mPropertyDialog = null;

	/**
	 * A text field to edit data names.
	 */
	private JTextField mTextField = new JTextField();

	/**
	 * A menu command string to hide the legend.
	 */
	public static final String MENUCMD_HIDE = "Hide";

	/**
	 * Default constructor.
	 */
	public SGFigureElementLegend() {
		super();
		this.initEditField();
		this.init();
	}

	/**
	 * Initialization
	 */
	private void init() {

		// set default values
		this.setVisible(DEFAULT_LEGEND_VISIBLE);
		this.setFontName(DEFAULT_LEGEND_FONT_NAME);
		this.setFontSize(DEFAULT_LEGEND_FONT_SIZE, FONT_SIZE_UNIT);
		this.setFontStyle(DEFAULT_LEGEND_FONT_STYLE);
		this.setFontColor(DEFAULT_LEGEND_FONT_COLOR);
		this.setFrameVisible(DEFAULT_LEGEND_FRAME_VISIBLE);
		this.setFrameLineWidth(DEFAULT_LEGEND_FRAME_WIDTH, LINE_WIDTH_UNIT);
		this.setFrameLineColor(DEFAULT_LEGEND_FRAME_COLOR);
                this.setBackgroundColor(DEFAULT_LEGEND_BACKGROUND_COLOR);
                this.setBackgroundTransparent(DEFAULT_LEGEND_BACKGROUND_TRANSPARENCY);
		this.setSymbolSpan(DEFAULT_LEGEND_SYMBOL_SPAN, SYMBOL_SPAN_UNIT);
	}

	/**
	 * Dispose this object.
	 */
	public void dispose() {
		super.dispose();

		// dispose the property dialog
		if (this.mPropertyDialog != null) {
			this.mPropertyDialog.dispose();
			this.mPropertyDialog = null;
		}

		this.mAxisElement = null;
		this.mFocusedGroup = null;
		this.mGraphElement = null;

		this.mTextField = null;
		this.mPopupMenu = null;
		this.mBackgroundPaint = null;
		this.mFontName = null;
		this.mFrameLineColor = null;
	}

	/**
	 * Returns a pop-up menu.
	 * @return
	 *         a pop-up menu
	 */
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

	/**
	 * Create a pop-up menu.
	 * @return
	 *         a pop-up menu
	 */
	private JPopupMenu createPopupMenu() {
		JPopupMenu p = new JPopupMenu();
		p.setBounds(0, 0, 100, 100);

		p.add(new JLabel("  -- Legend --"));
		p.addSeparator();

		SGUtility.addItem(p, this, MENUCMD_HIDE);

		p.addSeparator();

		SGUtility.addItem(p, this, MENUCMD_PROPERTY);

		return p;
	}

	/**
	 *
	 * @return
	 */
	public String getClassDescription() {
		return "Legend";
	}

	/**
	 * Add a data object.
	 * @param data
	 *          a data object
	 * @param name
	 *          the name of the data object
	 * @return
	 *          true if succeeded
	 */
	@Override
	public boolean addData(final SGData data, final String name) {
		return this.addData(data, name, 0, false, null);
	}

	@Override
    public boolean addData(final SGData data, final String name, final Map<String, Object> infoMap) {
        return this.addData(data, name, 0, false, infoMap);
    }

	/**
	 * Add a data object.
	 * @param data
	 *          a data object
	 * @param name
	 *          the name of the data object
     * @param id
     *          the ID to set
     * @param infoMap
     *          the information map of data object
	 * @return
	 *          true if succeeded
	 */
	@Override
    public boolean addData(final SGData data, final String name, final int id, final Map<String, Object> infoMap) {
        return this.addData(data, name, id, true, infoMap);
    }

    private boolean addData(final SGData data, final String name, final int id,
            final boolean withID, final Map<String, Object> infoMap) {

        if (super.addData(data, name) == false) {
            return false;
        }

        if (this.mGraphElement == null) {
            throw new Error("mGraphElement==null");
        }

        // create legend
        ElementGroupSetInLegend legend = (ElementGroupSetInLegend) this.createGroupSet(data, name, infoMap);
        if (legend == null) {
            return false;
        }

        // add to list
        if (withID) {
            if (this.addToList(id, legend) == false) {
                return false;
            }
        } else {
            if (this.addToList(legend) == false) {
                return false;
            }
        }

        this.updateAllDrawingElements();

        // initialize the history
        legend.initPropertiesHistory();

        // call one more time
        this.updateAllDrawingElements();

        // set properties on start-up
        if (this.mStartFlag) {
            this.onStartup(data);
        }

        // set the change flag
        this.setChanged(true);

        return true;
    }

	/**
	 * Add a data object with a set of properties.
	 *
	 * @param data
	 *             added data.
	 * @param name
	 *             the name set to the data
	 * @param p
	 *             properties set to be data.
	 * @return
	 *             true if succeeded
	 */
	public boolean addData(final SGData data, final String name,
			final SGProperties p) {

		if (this.mGraphElement == null || data == null) {
			throw new Error("mGraphElement==null || data==null");
		}

		if (super.addData(data, name, p) == false) {
			return false;
		}

		// create legend
		ElementGroupSetInLegend legend = (ElementGroupSetInLegend) this.createGroupSet(data, name, null);
		if (legend == null) {
			return false;
		}

		// add to list
		this.addToList(legend);

		// set properties
		if (legend.setProperties(p) == false) {
			return false;
		}

        if (SGDataUtility.isSXYTypeData(data.getDataType()) && SGDataUtility.isNetCDFData(data)) {
            this.updateBarVerticalOfNetCDFData(legend, data);
        }

		// set the name
		legend.setName(name);

		// set visible because cut data objects are set invisible
		legend.setVisible(true);

		// update drawing elements
		this.updateAllDrawingElements();

		// initialize the history
		legend.initPropertiesHistory();

		// call one more time
		this.updateAllDrawingElements();

		// set properties on start-up
		if (this.mStartFlag) {
			this.onStartup(data);
		}

		// set the change flag
		this.setChanged(true);

		return true;
	}

    private void updateBarVerticalOfNetCDFData(ElementGroupSetInLegend legend, SGData data) {
        if (data instanceof SGSXYNetCDFData) {
            SGSXYNetCDFData ndata = (SGSXYNetCDFData)data;
            boolean vertical = ndata.isXVariableCoordinate();
            if (legend instanceof ElementGroupSetInLegendMultipleSXY) {
                ElementGroupSetInLegendMultipleSXY gs = (ElementGroupSetInLegendMultipleSXY)legend;
                if (gs.isBarVertical()!=vertical) {
                    gs.setBarVertical(vertical);
                }
            } else if (legend instanceof ElementGroupSetInLegendSXY) {
                ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY)legend;
                if (gs.isBarVertical()!=vertical) {
                    gs.setBarVertical(vertical);
                }
            }
        } else if (data instanceof SGSXYNetCDFMultipleData) {
            SGSXYNetCDFMultipleData ndata = (SGSXYNetCDFMultipleData)data;
            boolean vertical = ndata.isXVariableCoordinate();
            if (legend instanceof ElementGroupSetInLegendMultipleSXY) {
                ElementGroupSetInLegendMultipleSXY gs = (ElementGroupSetInLegendMultipleSXY)legend;
                if (gs.isBarVertical()!=vertical) {
                    gs.setBarVertical(vertical);
                }
            } else if (legend instanceof ElementGroupSetInLegendSXY) {
                ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY)legend;
                if (gs.isBarVertical()!=vertical) {
                    gs.setBarVertical(vertical);
                }
            }
        }
    }

	/**
	 * called on the start-up
	 *
	 * @param data
	 */
	private void onStartup(SGData data) {
		// set the location of the legend
		// to the north-east point of the graph area
		final float x = this.getGraphRectX() + this.getGraphRectWidth()
				- this.mMagnification * this.getLegendWidth();
		final float y = this.getGraphRectY();
		this.setLegendLocation(x, y);

		// refresh the legend location with given axes
		double currentValueX = calcValue(this.getLegendX(), this.mXAxis, true);
		currentValueX = SGUtilityNumber.getNumberInRangeOrder(currentValueX, this.mXAxis);
		final float xNew = calcLocation(currentValueX, this.mXAxis, true);
		this.mLegendX = (xNew - this.mGraphRectX) / this.mMagnification;
		double currentValueY = calcValue(this.getLegendY(), this.mYAxis, false);
		currentValueY = SGUtilityNumber.getNumberInRangeOrder(currentValueY, this.mYAxis);
		final float yNew = calcLocation(currentValueY, this.mYAxis, false);
		this.mLegendY = (yNew - this.mGraphRectY) / this.mMagnification;

		// update drawing elements
		this.updateAllDrawingElements();

		this.mStartFlag = false;
	}

	// used only on the start-up
	private boolean mStartFlag = true;

	/**
	 *
	 */
	public boolean getFocusedObjectsList(List<SGISelectable> list) {
		if (this.isSelected()) {
			list.add(this);
		}
		ArrayList lList = this.getVisibleLegendList();
		for (int ii = 0; ii < lList.size(); ii++) {
			ElementGroupSetInLegend gs = (ElementGroupSetInLegend) lList
					.get(ii);
			if (gs.isSelected()) {
				list.add(gs);
			}
		}
		return true;
	}

	/**
	 * Hide the selected objects.
	 * @return
	 *         true if selected
	 */
	public boolean hideSelectedObjects() {
		if (super.hideSelectedObjects() == false) {
			return false;
		}

		this.updateAllDrawingElements();
		this.repaint();

		return true;
	}

	/**
	 *
	 *
	 */
	public boolean hideSelectedObject(SGISelectable s) {
		if (s.equals(this)) {
			// if s is equal to the legend element itself
			return true;
		} else {
			return super.hideSelectedObject(s);
		}
	}

	@Override
	public boolean hideData(final int[] dataIdArray) {
	    if (super.hideData(dataIdArray) == false) {
	        return false;
	    }
	    this.updateAllDrawingElements();
        this.repaint();

        return true;
	}

	/**
	 *
	 */
	private boolean mSelectedFlag = false;

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

	/**
	 *
	 */
	public boolean isSelected() {
		return this.mSelectedFlag;
	}

	/**
	 * Returns the x-coordinate of the legend in the fiugre.
	 * @return
	 *         the x-coordinate of the legend in the fiugre
	 */
	public float getLegendX() {
		return this.mGraphRectX + this.mLegendX * this.mMagnification;
	}

	/**
	 * Returns the y-coordinate of the legend in the fiugre.
	 * @return
	 *         the y-coordinate of the legend in the fiugre
	 */
	public float getLegendY() {
		return this.mGraphRectY + this.mLegendY * this.mMagnification;
	}

	/**
	 * Returns the width of the legend.
	 * @return
	 *         width of the legend
	 */
	public float getLegendWidth() {
		return this.mLegendWidth;
	}

	/**
	 * Returns the height of the legend.
	 * @return
	 *         height of the legend
	 */
	public float getLegendHeight() {
		return this.mLegendHeight;
	}

	/**
	 * Returns the location of the legend in the figure.
	 * @return
	 *         the location of the legend in the figure
	 */
	public Point2D getLegendLocation() {
		Point2D pos = new Point2D.Float(this.getLegendX(), this.getLegendY());
		return pos;
	}

	/**
	 * Sets the location of the legend in the figure.
	 * @param x
	 *          the x-coordinate
	 * @param y
	 *          the y-coordinate
	 * @return
	 *          true if succeeded
	 */
	public boolean setLegendLocation(float x, float y) {
		this.mLegendX = (x - this.mGraphRectX) / this.mMagnification;
		this.mLegendY = (y - this.mGraphRectY) / this.mMagnification;
		return true;
	}

	/**
	 * Sets the x-value for the location.
	 *
	 * @param value
	 *           the x-value to set
	 * @return true if succeeded
	 */
	public boolean setXValue(final double value) {
		SGAxis axis = this.mXAxis;
		if (axis.isValidValue(value) == false) {
			return false;
		}

		// current values gotten from the position of legend
		double currentValue = calcValue(this.getLegendX(), axis, true);
		currentValue = SGUtilityNumber.getNumberInRangeOrder(currentValue, axis);

		// the next value to set
		final double nextValue = SGUtilityNumber.getNumberInRangeOrder(value, axis);

		// if values from the dialog is diffrent from the current values,
		// set the values from the dialog
		float x;
		if (nextValue == currentValue) {
			x = this.mLegendX;
		} else {
			final float pos = calcLocation(value, axis, true);
			x = (pos - this.mGraphRectX) / this.mMagnification;
		}

		boolean changed = false;
		if (x != this.mLegendX) {
			changed = true;
		}
		this.mLegendX = x;

		if (changed) {
			this.updateAllDrawingElements();
		}

		return true;
	}

	/**
	 * Sets the y-value for the location.
	 *
	 * @param value
	 *           the y-value to set
	 * @return true if succeeded
	 */
	public boolean setYValue(final double value) {
		SGAxis axis = this.mYAxis;
		if (axis.isValidValue(value) == false) {
			return false;
		}

		// current values gotten from the position of legend
		double currentValue = calcValue(this.getLegendY(), axis, false);
		currentValue = SGUtilityNumber.getNumberInRangeOrder(currentValue, axis);

		// the next value to set
		final double nextValue = SGUtilityNumber.getNumberInRangeOrder(value, axis);

		// if values from the dialog is diffrent from the current values,
		// set the values from the dialog
		float y;
		if (nextValue == currentValue) {
			y = this.mLegendY;
		} else {
			final float pos = calcLocation(value, axis, false);
			y = (pos - this.mGraphRectY) / this.mMagnification;
		}

		boolean changed = false;
		if (y != this.mLegendY) {
			changed = true;
		}
		this.mLegendY = y;

		if (changed) {
			this.updateAllDrawingElements();
		}

		return true;
	}

	/**
	 *
	 * @param config
	 * @param value
	 * @return
	 */
	public boolean hasValidXAxisValue(final int config, final Number value) {
		final SGAxis axis = (config == -1) ? this.mXAxis : this.mAxisElement
				.getAxisInPlane(config);
		final double v = (value != null) ? value.doubleValue() : this
				.getXValue();
		return axis.isValidValue(v);
	}

	/**
	 *
	 * @param config
	 * @param value
	 * @return
	 */
	public boolean hasValidYAxisValue(final int config, final Number value) {
		final SGAxis axis = (config == -1) ? this.mYAxis : this.mAxisElement
				.getAxisInPlane(config);
		final double v = (value != null) ? value.doubleValue() : this
				.getYValue();
		return axis.isValidValue(v);
	}

	/**
	 *
	 */
	public void translate(final float dx, final float dy) {
		this.setLegendLocation(this.getLegendX() + dx, this.getLegendY() + dy);
		this.updateAllDrawingElements();
	}

	/**
	 *
	 * @return
	 */
	public double getXValue() {
		SGAxis axis = this.mXAxis;
		double value = calcValue(this.getLegendX(), axis, true);
		if (Double.isNaN(value)) {
			return value;
		}
		value = SGUtilityNumber.getNumberInRangeOrder(value, axis);
		return value;
	}

	/**
	 *
	 * @return
	 */
	public double getYValue() {
		SGAxis axis = this.mYAxis;
		double value = calcValue(this.getLegendY(), axis, false);
		if (Double.isNaN(value)) {
			return value;
		}
		value = SGUtilityNumber.getNumberInRangeOrder(value, axis);
		return value;
	}

	/**
	 * Initialize the text field.
	 */
	private void initEditField() {
		this.mTextField.setVisible(false);
		this.mTextField.addActionListener(this);
		this.mTextField.addCaretListener(this);
		this.mTextField.getDocument().addDocumentListener(this);
		this.mTextField.addKeyListener(this);
	}

	public void setComponent(JComponent com) {
		super.setComponent(com);
		com.add(this.mTextField);
	}

	/**
	 * Clear all focused objects.
	 * @return
	 *         true if succeeded
	 */
	public boolean clearFocusedObjects() {
		if (super.clearFocusedObjects() == false) {
			return false;
		}
		this.clearFocusedObjectsSub();
		return true;
	}

	/**
	 * Clear focused objects other than a given figure element.
	 * @param ori
	 *            an origin of this clearance
	 * @return
	 *           true if succeeded
	 */
	public boolean clearFocusedObjects(SGIFigureElement ori) {
		if (ori instanceof SGIFigureElementGraph) {
			// clear the selection of the legend figure element
			this.setSelected(false);
		} else if (ori instanceof SGIFigureElementLegend) {

			SGIFigureElementLegend l = (SGIFigureElementLegend) ori;
			if (!l.equals(this)) {
				if (l.isSelected()) {
					// clear data selection of l
					this.clearAllFocusedData();
				} else {
					// clear legend selection of l
					this.setSelected(false);
				}
			}

		} else {
			if (this.clearFocusedObjects() == false) {
				return false;
			}
		}
		this.clearFocusedObjectsSub();
		return true;
	}

	// sub method to clear focused objects
	private void clearFocusedObjectsSub() {
		if (this.mTextField.isVisible()) {
			this.closeTextField();
		}
		this.clearFocusedGroup();
	}

	/**
	 *
	 */
	public boolean prepare() {
		this.mTemporaryProperties = this.getProperties();
		return true;
	}

	/**
	 *
	 */
	public SGProperties getProperties() {
		SGProperties p = new LegendProperties();
		if (this.getProperties(p) == false) {
			return null;
		}
		return p;
	}

	/**
	 *
	 */
	public boolean getProperties(final SGProperties p) {
		if ((p instanceof LegendProperties) == false) {
			return false;
		}

		LegendProperties lp = (LegendProperties) p;

		lp.x = this.mLegendX;
		lp.y = this.mLegendY;

		lp.visible = this.isVisible();
		lp.frameLineVisible = this.isFrameVisible();
		lp.frameLineWidth = this.getFrameLineWidth();
		lp.frameLineColor = this.getFrameColor();
		lp.backgroundPaint.setColor(this.getBackgroundColor());
		lp.backgroundPaint.setTransparency(this.getBackgroundTransparency());
		lp.fontName = this.getFontName();
		lp.fontSize = this.getFontSize();
		lp.fontStyle = this.getFontStyle();
		lp.stringColor = this.getFontColor();
		lp.symbolSpan = this.getSymbolSpan();

		lp.xAxis = this.mXAxis;
		lp.yAxis = this.mYAxis;

		lp.visibleElementGroupList = new ArrayList(this.getVisibleChildList());

		return true;
	}

	/**
	 *
	 */
	public boolean setProperties(final SGProperties p) {

		if ((p instanceof LegendProperties) == false)
			return false;

		LegendProperties wp = (LegendProperties) p;

		if (this.setCommonProperties(wp) == false) {
			return false;
		}

		return true;
	}

	/**
	 *
	 */
	private boolean setCommonProperties(final LegendProperties p) {

		this.mLegendX = p.x;
		this.mLegendY = p.y;

		this.setVisible(p.visible);
		this.setFrameVisible(p.frameLineVisible);
		this.setFrameLineWidth(p.frameLineWidth);
		this.setFrameLineColor(p.frameLineColor);
                this.setBackgroundColor(p.backgroundPaint.getColor());
                this.setBackgroundTransparent(p.backgroundPaint.getTransparencyPercent());
		this.setFontName(p.fontName);
		this.setFontSize(p.fontSize);
		this.setFontStyle(p.fontStyle);
		this.setFontColor(p.stringColor);
		this.setSymbolSpan(p.symbolSpan);

		this.mXAxis = p.xAxis;
		this.mYAxis = p.yAxis;

		boolean flag;
		flag = this.setVisibleChildList(p.visibleElementGroupList);
		if (!flag) {
			return false;
		}

		return true;
	}

	private static final float MARGIN_HORIZONTAL = 6.0f;

	private static final float MARGIN_VERTICAL = 6.0f;

	private final static float marginTop = MARGIN_VERTICAL;

	private final static float marginBottom = MARGIN_VERTICAL;

	private final static float marginLeft = MARGIN_HORIZONTAL;

	private final static float marginRight = MARGIN_HORIZONTAL;

	private final static float spaceDataAndString = MARGIN_HORIZONTAL;

	private final static float spaceLegend = MARGIN_VERTICAL;

	/**
	 * Update all drawing elements.
	 * @return
	 *         true if succeeded
	 */
	private boolean updateAllDrawingElements() {

		// the magnification
		final float mag = this.mMagnification;

		// get an array of visible legend
		final List<ElementGroupSetInLegend> visibleGroupSetList = this
				.getViewableLegendList();
		final int num = visibleGroupSetList.size();
		final ElementGroupSetInLegend[] legendArray = new ElementGroupSetInLegend[num];
		for (int ii = 0; ii < num; ii++) {
			legendArray[ii] = (ElementGroupSetInLegend) visibleGroupSetList
					.get(ii);
		}
		//System.out.println(visibleGroupSetList);

		// create string elements
		for (int ii = 0; ii < num; ii++) {
			legendArray[ii].createStringElement();
		}

		// get the bounding box of string elements
		final Rectangle2D[] stringBoundsArray = new Rectangle2D[num];
		for (int ii = 0; ii < num; ii++) {
			stringBoundsArray[ii] = legendArray[ii].getStringBounds();
		}

		// get height of string elements
		final float[] stringHeightArray = new float[num];
		for (int ii = 0; ii < num; ii++) {
			stringHeightArray[ii] = (float) stringBoundsArray[ii].getHeight();
		}

		// max width of string elements
		float stringWidthMax = 0.0f;
		for (int ii = 0; ii < num; ii++) {
			float width = (float) stringBoundsArray[ii].getWidth();
			if (width > stringWidthMax) {
				stringWidthMax = width;
			}
		}

		// max width of drawing elements
		float dataWidthMax = this.getSymbolSpan();
		for (int ii = 0; ii < num; ii++) {
			final float width = legendArray[ii].getMaxDataElementWidth() / mag;
			if (width > dataWidthMax) {
				dataWidthMax = width;
			}
		}

		// legend width in the default zoom
		final float legendWidth = dataWidthMax + stringWidthMax / mag
				+ spaceDataAndString;

		// height array of drawing elements
		final double[] dataHeightArray = new double[num];
		for (int ii = 0; ii < num; ii++) {
			dataHeightArray[ii] = legendArray[ii].getMaxDataElementHeight();
		}

		// total height array
		float[] legendHeightArray = new float[num];
		for (int ii = 0; ii < num; ii++) {
			legendHeightArray[ii] = (float) Math.max(dataHeightArray[ii],
					stringHeightArray[ii]);
		}

		// rectangle array of legend
		Rectangle2D[] legendRectArray = new Rectangle2D[num];
		float rectY = this.getLegendY() + mag * marginTop;
		final float lx = this.getLegendX() + mag * marginLeft;
		final float lw = mag * legendWidth;
		for (int ii = 0; ii < num; ii++) {
			final float ly = rectY;
			final float lh = legendHeightArray[ii];
			legendRectArray[ii] = new Rectangle2D.Float(lx, ly, lw, lh);
			rectY += lh + mag * spaceLegend;
		}

		// set bounds
		for (int ii = 0; ii < num; ii++) {
			legendArray[ii].setRect(legendRectArray[ii]);
		}

		final float w = mag * dataWidthMax;
		for (int ii = 0; ii < num; ii++) {
			final float x = (float) legendRectArray[ii].getX();
			final float y = (float) legendRectArray[ii].getY();
			final float h = (float) legendArray[ii].getMaxDataElementHeight();
			final Rectangle2D dRect = new Rectangle2D.Float(x, y, w, h);
			legendArray[ii].setDrawingElementBounds(dRect);
		}

		// legend height in the default zoom
		float legendHeight = 0.0f;
		for (int ii = 0; ii < num; ii++) {
			legendHeight += legendHeightArray[ii] / mag;
		}
		legendHeight += (num - 1) * spaceLegend;

		// create drawing elements of each object
		for (int ii = 0; ii < num; ii++) {
			legendArray[ii].createDrawingElement();
		}

		// set location of string elements
		final float sx = this.getLegendX() + mag
				* (marginLeft + dataWidthMax + spaceDataAndString);
		for (int ii = 0; ii < num; ii++) {
			final float sy = (float) legendRectArray[ii].getY() - 0.50f
					* stringHeightArray[ii] + 0.50f * legendHeightArray[ii]
					+ (float) stringBoundsArray[ii].getY();
			legendArray[ii].getStringElement().setLocation(sx, sy);
		}

		// set to attributes
		this.mLegendWidth = marginLeft + legendWidth + marginRight;
		this.mLegendHeight = marginTop + legendHeight + marginBottom;

		return true;
	}

	/**
	 *
	 */
	public boolean setGraphRect(final float x, final float y,
			final float width, final float height) {
		super.setGraphRect(x, y, width, height);

		if (this.closeTextField() == false) {
			return false;
		}

		if (this.updateAllDrawingElements() == false) {
			return false;
		}

		return true;
	}

	/**
	 *
	 */
	public void paintGraphics(Graphics g, boolean clip) {
		final Graphics2D g2d = (Graphics2D) g;

		if (this.isVisible()) {

			List<ElementGroupSetInLegend> list = this.getViewableLegendList();
			if (list.size() != 0) {

				// paint background
			    if (this.getBackgroundTransparency()!=SGTransparentPaint.ALL_TRANSPARENT_VALUE) {
			        Rectangle rect = this.getLegendRect().getBounds();
			        g2d.setPaint(this.mBackgroundPaint.getPaint(rect));
			        g2d.fill(rect);
			    }

				// draw each legend
				for (int ii = 0; ii < list.size(); ii++) {
					ElementGroupSetInLegend groupSet = (ElementGroupSetInLegend) list
							.get(ii);
					groupSet.paintGraphics2D(g2d);
				}

				// draw frame
				if (this.mFrameVisibleFlag) {
					this.drawLegendFrameLines(g2d);
				}

				// draw anchors around all objects
				if (this.mSymbolsVisibleFlagAroundAllObjects) {
					ArrayList pList = this.getAnchorPointList();
					SGUtilityForFigureElementJava2D.drawAnchorAsChildObject(
							pList, g2d);
				}

				// draw anchors around focused objects
				if (this.mSymbolsVisibleFlagAroundFocusedObjects
						&& this.isSelected()) {
					ArrayList pList = this.getAnchorPointList();
					SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(
							pList, g2d);
				}
			}
		}

	}

	/**
	 * Returns a list of points to draw anchors.
	 * @return
	 *         a list of points to draw anchors
	 */
	private ArrayList getAnchorPointList() {
		ArrayList list = new ArrayList();

		Rectangle2D rect = this.getLegendRect();
		final float x = (float) rect.getX();
		final float y = (float) rect.getY();
		final float w = (float) rect.getWidth();
		final float h = (float) rect.getHeight();

		Point2D nw = new Point2D.Float(x, y);
		Point2D sw = new Point2D.Float(x, y + h);
		Point2D ne = new Point2D.Float(x + w, y);
		Point2D se = new Point2D.Float(x + w, y + h);

		list.add(nw);
		list.add(sw);
		list.add(ne);
		list.add(se);

		return list;
	}

	/**
	 *
	 * @param g2d
	 */
	private void drawLegendFrameLines(final Graphics2D g2d) {
		if (g2d == null) {
			return;
		}

		List<ElementGroupSetInLegend> list = this.getViewableLegendList();
		if (list.size() == 0) {
			return;
		}

		g2d.setPaint(this.mFrameLineColor);

		g2d.setStroke(new BasicStroke(this.mMagnification
				* this.mFrameLineWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER));

		g2d.draw(this.getLegendRect().getBounds());

	}

	/**
	 *
	 */
	public boolean contains(final int x, final int y) {
		Rectangle2D rect = new Rectangle2D.Float(this.getLegendX(), this
				.getLegendY(), this.mLegendWidth, this.mLegendHeight);
		return rect.contains(x, y);
	}

	/**
	 * Returns a set of available child objects in the histories.
	 * @return
	 *         a set of available child objects in the histories
	 */
	protected Set getAvailableChildSet() {
		Set set = new HashSet();
		List mList = this.getMementoList();
		for (int ii = 0; ii < mList.size(); ii++) {
			LegendProperties p = (LegendProperties) mList.get(ii);
			set.addAll(p.visibleElementGroupList);
		}
		return set;
	}

	/**
	 * Synchronize to the other figure element.
	 * @param element
	 *                a figure element
	 * @param msg
	 *                a message
	 * @return
	 *                true if succeeded
	 */
	public boolean synchronize(SGIFigureElement element, String msg) {

		boolean flag = true;
		if (element instanceof SGIFigureElementAxis) {
			SGIFigureElementAxis aElement = (SGIFigureElementAxis) element;
			flag = this.synchronizeToAxisElement(aElement, msg);
		} else if (element instanceof SGIFigureElementGraph) {
			SGIFigureElementGraph gElement = (SGIFigureElementGraph) element;
			flag = this.synchronizeToGraphElement(gElement, msg);
		} else if (element instanceof SGIFigureElementString) {

		} else if (element instanceof SGIFigureElementLegend) {

		} else if (element instanceof SGIFigureElementAxisBreak) {

		} else if (element instanceof SGIFigureElementSignificantDifference) {

		} else if (element instanceof SGIFigureElementTimingLine) {

		} else if (element instanceof SGIFigureElementGrid) {

		} else if (element instanceof SGIFigureElementShape) {

		} else {
			flag = element.synchronizeArgument(this, msg);
		}

		return flag;
	}

	/**
	 * Synchronize to the graph element.
	 * @param gElement
	 *                the graph element
	 * @param msg
	 *                a message
	 * @return
	 *                true if succeeded
	 */
	private boolean synchronizeToGraphElement(
			final SGIFigureElementGraph gElement, final String msg) {
    	return this.synchronizeToDataElement(gElement, msg);
	}

	/**
	 * Synchronize to the axis element.
	 *
	 * @param aElement
	 *           the axis element
	 * @param msg
	 *           the message
	 * @return true if succeeded
	 */
	private boolean synchronizeToAxisElement(
			final SGIFigureElementAxis aElement, final String msg) {
        if (this.isNotificationMessage(msg)) {
			// synchronize the color bar
			final SGColorMap model = this.getColorBarModel();
			for (int ii = 0; ii < this.mChildList.size(); ii++) {
				SGIChildObject groupSet = (SGIChildObject) this.mChildList
						.get(ii);
				if (groupSet instanceof ElementGroupSetInLegendSXYZ) {
					ElementGroupSetInLegendSXYZ legend = (ElementGroupSetInLegendSXYZ) groupSet;
					ElementGroupPseudocolorMap colorMap = (ElementGroupPseudocolorMap) legend
							.getColorMapGroup();
					colorMap.setColorBarModel(model);
				}
			}
		}
		return true;
	}

	/**
	 * Synchronize the element given by the argument.
	 *
	 * @param element
	 *            An object to be synchronized.
	 */
	public boolean synchronizeArgument(final SGIFigureElement element,
			final String msg) {
		// this shouldn't happen
		throw new Error();
	}

	/**
	 * Returns whether the data object is selected.
	 * @param data
	 *             a data object
	 * @return
	 *             true if selected
	 */
	public boolean isDataSelected(final SGData data) {
		if (data == null) {
			throw new IllegalArgumentException("data == null");
		}
		ElementGroupSetInLegend gs = (ElementGroupSetInLegend) this
				.getElementGroupSet(data);
		if (gs == null) {
			throw new Error("Data is not found.");
		}
		return gs.isSelected();
	}

	/**
	 * Sets the magnification.
	 *
	 * @param mag
	 *           the magnification to set
	 * @return true if succeeded
	 */
	public boolean setMagnification(final float mag) {
		if (this.closeTextField() == false) {
			return false;
		}
		if (super.setMagnification(mag) == false) {
			return false;
		}
		if (this.updateAllDrawingElements() == false) {
			return false;
		}
		return true;
	}

	/**
	 *
	 */
	public boolean getMarginAroundGraphRect(SGTuple2f topAndBottom,
			SGTuple2f leftAndRight) {

		if (super.getMarginAroundGraphRect(topAndBottom, leftAndRight) == false) {
			return false;
		}

		Rectangle2D graphRect = this.getGraphRect();
		Rectangle2D lRect = this.getLegendRect();
		if (lRect.getWidth() < Double.MIN_VALUE
				|| lRect.getHeight() < Double.MIN_VALUE) {
			return true;
		}

		ArrayList list = new ArrayList();
		list.add(graphRect);
		if (this.isVisible()) {
			list.add(lRect);
		}

		Rectangle2D uniRect = SGUtility.createUnion(list);

		// System.out.println(graphRect);
		// System.out.println(lRect);
		// System.out.println(uniRect);

		final float top = (float) (graphRect.getY() - uniRect.getY());
		final float bottom = (float) ((uniRect.getY() + uniRect.getHeight()) - (graphRect
				.getY() + graphRect.getHeight()));
		final float left = (float) (graphRect.getX() - uniRect.getX());
		final float right = (float) ((uniRect.getX() + uniRect.getWidth()) - (graphRect
				.getX() + graphRect.getWidth()));

		topAndBottom.x += top;
		topAndBottom.y += bottom;
		leftAndRight.x += left;
		leftAndRight.y += right;

		// System.out.println(topAndBottom+" "+leftAndRight);

		// System.out.println();

		return true;
	}

	@Override
    public boolean onKeyPressed(final KeyEvent e) {
		if (!super.onKeyPressed(e)) {
			return false;
		}
		this.updateAllDrawingElements();
		return true;
    }
    
	@Override
    protected List<MovableInfo> getMovableObjectList() {
    	List<MovableInfo> objList = new ArrayList<MovableInfo>();
    	if (this.isSelected()) {
        	objList.add(new MovableInfo(this, this));
    	}
    	return objList;
    }

	/**
	 *
	 */
	public boolean onMouseClicked(final MouseEvent e) {

		// if the legend is invisible, return false
		if (!isVisible()) {
			return false;
		}

		// get visible group sets
		List<ElementGroupSetInLegend> gList = this.getViewableLegendList();
		if (gList.size() == 0) {
			return false;
		}

		final int x = e.getX();
		final int y = e.getY();
		final int cnt = e.getClickCount();

		Rectangle2D rect = this.getLegendRect();
		if (rect.contains(x, y)) {
			if (cnt == 1) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					for (int ii = 0; ii < gList.size(); ii++) {
						ElementGroupSetInLegend legend = (ElementGroupSetInLegend) gList
								.get(ii);
						Rectangle2D sRect = legend.getStringElement()
								.getElementBounds();

						// show the text field to edit the name
						if (sRect.contains(x, y)) {
							if (mPressedPoint == null) {
								return false;
							}
							this.mFocusedGroup = legend;
							final int tx = mPressedPoint.x - (int) sRect.getX();
							final int ty = mPressedPoint.y - (int) sRect.getY();
							this.showEditField(this.mTextField,
									legend.mDrawingString, tx, ty);
							return true;
						}

						// select the legend
						if (legend.contains(x, y)) {
							onDataClicked(legend, e);
							return true;
						}
					}

					// update focused objects
					this.updateFocusedObjectsList(this, e);

					return true;

				} else if (SwingUtilities.isRightMouseButton(e)) {
					for (int ii = 0; ii < gList.size(); ii++) {
						ElementGroupSetInLegend legend = (ElementGroupSetInLegend) gList
								.get(ii);
						if (legend.contains(x, y)) {
							// select the legend
							this.onDataClicked(legend, e);

							// shows the pop-up menu for the group set
							this.showDataPopupMenu(legend, x, y, false, getWindow());
							return true;
						}
					}

					// update focused objects
					this.updateFocusedObjectsList(this, e);

					// shows the pop-up menu for this figure element
					this.getPopupMenu().show(this.getComponent(), x, y);

					return true;
				}

			} else if (cnt == 2 && SwingUtilities.isLeftMouseButton(e)) {

				for (int ii = 0; ii < gList.size(); ii++) {
					ElementGroupSetInLegend legend = (ElementGroupSetInLegend) gList
							.get(ii);

					ArrayList<SGElementGroup> list = legend.getElementGroupList();
					for (int jj = list.size() - 1; jj >= 0; jj--) {
						SGElementGroup group = list.get(jj);
						if (group.isVisible() == false) {
							continue;
						}
						if (group.contains(x, y)) {
							legend.onMouseClicked(e);

							// update focused objects
							this.onDataClicked(legend, e);

							// clear the selection of legend figure element
							this.setSelected(false);

							// shows the property dialog for the group set
							this.setPropertiesOfSelectedObjects(legend);

							return true;
						}
					}
				}

				if (SwingUtilities.isLeftMouseButton(e)) {
					// clear all focused legend
					this.clearAllFocusedData();

					// shows the property dialog for the figure element
					this.setPropertiesOfSelectedObjects(this);
				}
			}

			return true;
		}

		return false;
	}

	// Called when the data object is clicked.
	private void onDataClicked(ElementGroupSetInLegend legend, MouseEvent e) {
		this.updateFocusedObjectsList(legend, e);
		this.notifyDataSelection();
	}

	/**
	 * Clear all focused data.
	 */
	public void clearAllFocusedData() {
		List lList = this.getVisibleChildList();
		for (int ii = 0; ii < lList.size(); ii++) {
			ElementGroupSetInLegend gs = (ElementGroupSetInLegend) lList
					.get(ii);
			gs.setSelected(false);
		}
		this.clearFocusedObjectsSub();
	}

	/**
	 *
	 */
	private ElementGroupSetInLegend mFocusedGroup = null;

	/**
	 * Returns a list of child nodes.
	 *
	 * @return a list of chid nodes
	 */
	public ArrayList getChildNodes() {
		return new ArrayList();
	}

	/**
	 * Returns the property dialog.
	 *
	 * @return
	 *        a property dialog
	 */
	public SGPropertyDialog getPropertyDialog() {
		SGPropertyDialog dg = null;
		if (mPropertyDialog != null) {
			dg = mPropertyDialog;
		} else {
			dg = new SGLegendDialog(mDialogOwner, true);
			mPropertyDialog = dg;
		}
		return dg;
	}

	/**
	 *
	 */
	public String getFontName() {
		return this.mFontName;
	}

	/**
	 *
	 */
	public int getFontStyle() {
		return this.mFontStyle;
	}

	/**
	 *
	 */
	public float getFontSize() {
		return this.mFontSize;
	}

	/**
	 *
	 */
	public float getFontSize(final String unit) {
		return (float) SGUtilityText.convertFromPoint(this.getFontSize(), unit);
	}

	/**
	 *
	 */
	public float getFrameLineWidth() {
		return this.mFrameLineWidth;
	}

	/**
	 *
	 */
	public float getFrameLineWidth(final String unit) {
		return (float) SGUtilityText.convertFromPoint(this.getFrameLineWidth(),
				unit);
	}

	/**
	 *
	 */
	public Color getFrameColor() {
		return this.mFrameLineColor;
	}

	/**
	 *
	 */
	@Override
	public Color getBackgroundColor() {
	    return this.mBackgroundPaint.getColor();
	}

	@Override
	public int getBackgroundTransparency() {
	    return this.mBackgroundPaint.getTransparencyPercent();
	}

	/**
	 *
	 */
	public Color getFontColor() {
		return this.mFontColor;
	}

	/**
	 * Returns the symbol span.
	 *
	 * @return the symbol span
	 */
	public float getSymbolSpan() {
		return this.mSymbolSpan;
	}

	/**
	 * @return
	 */
	public float getSymbolSpan(final String unit) {
		return (float) SGUtilityText.convertFromPoint(this.getSymbolSpan(),
				unit);
	}

	/**
	 * Sets the symbol span.
	 *
	 * @param span
	 *           the symbol span to set
	 * @return true if succeeded
	 */
	public boolean setSymbolSpan(final float span) {
		if (span < 0.0f) {
			throw new IllegalArgumentException("span < 0.0f");
		}
		this.mSymbolSpan = span;
		return true;
	}

	/**
	 * Sets the symbol span in a given unit.
	 *
	 * @param span
	 *           the symbol span to set
	 * @param unit
	 *           the unit for the symbol span
	 * @return true if succeeded
	 */
	public boolean setSymbolSpan(final float span, final String unit) {
		final Float sNew = SGUtility.calcPropertyValue(span, unit,
				SYMBOL_SPAN_UNIT, SYMBOL_SPAN_MIN, SYMBOL_SPAN_MAX,
				SYMBOL_SPAN_MINIMAL_ORDER);
		if (sNew == null) {
			return false;
		}
		return this.setSymbolSpan(sNew);
	}

	/**
	 *
	 */
	private Point mLegendLocation = null;

	/**
	 *
	 */
	private boolean isMoved() {
		final boolean bx = ((int) this.mLegendX == (int) this.mLegendLocation
				.getX());
		final boolean by = ((int) this.mLegendY == (int) this.mLegendLocation
				.getY());
		final boolean b = !(bx && by);
		return b;
	}


    /**
     * Overrode for the text field for data name.
     *
     * @return true if a text field is shown
     */
    public boolean closeTextField() {
		this.commitEdit();
		this.hideEditField();
		this.clearFocusedGroup();
		this.repaint();
		return true;
	}

	/**
	 *
	 * @return
	 */
	private boolean commitEdit() {
		String str = this.mTextField.getText();

		if (!SGUtilityText.isValidString(str)) {
			return false;
		}

		String before = this.mFocusedGroup.getName();
		String after = str;
		this.mFocusedGroup.setName(after);

		//
		this.updateAllDrawingElements();

		// update the history
		if (before.equals(after) == false) {
			this.mFocusedGroup.setChanged(true);
		}

		this.notifyChangeOnCommit();
		this.notifyToRoot();

		return true;
	}

	/**
	 *
	 */
	private boolean hideEditField() {
		this.mTextField.setText("");
		this.mTextField.setVisible(false);
		return true;
	}

	/**
	 *
	 * @return
	 */
	private boolean clearFocusedGroup() {
		this.mFocusedGroup = null;
		return true;
	}

	/**
	 *
	 * @param e
	 */
	public boolean onMousePressed(final MouseEvent e) {
		if (!isVisible()) {
			return false;
		}

		if (this.mTextField.isVisible()) {
			this.closeTextField();
		}

		Rectangle2D lRect = this.getLegendRect();
		if (lRect.contains(e.getX(), e.getY())) {
			this.mLegendLocation = new Point((int) this.mLegendX,
					(int) this.mLegendY);
			this.mPressedPoint = e.getPoint();
			setMouseCursor(Cursor.MOVE_CURSOR);
			return true;
		}

		this.clearFocusedGroup();

		return false;
	}

	/**
	 *
	 * @param e
	 */
	public boolean onMouseDragged(final MouseEvent e) {
		if (this.mPressedPoint == null) {
			return false;
		}
		return true;
	}

	/**
	 *
	 */
	public boolean onMouseReleased(final MouseEvent e) {

		if (this.mLegendLocation != null) {
			if (this.isMoved()) {
				this.setChanged(true);
			}
		}

		Rectangle2D rect = this.getLegendRect();
		if (rect.contains(e.getPoint())) {
			setMouseCursor(Cursor.HAND_CURSOR);
		} else {
			setMouseCursor(Cursor.DEFAULT_CURSOR);
		}

        this.mDraggedDirection = null;

		return true;
	}

//	/**
//	 *
//	 * @return
//	 */
//	public boolean setTemporaryPropertiesOfFocusedObjects() {
//		this.mTemporaryProperties = this.getProperties();
//		return true;
//	}

    /**
     * Updates changed flag of focused objects.
     * 
     * @return true if succeeded
     */
    @Override
	public boolean updateChangedFlag() {
		if (this.isSelected()) {
			SGProperties temp = this.mTemporaryProperties;
			if (temp != null) {
				SGProperties p = this.getProperties();
				if (p.equals(temp) == false) {
					this.setChanged(true);
				}
			}
		}
		return true;
	}

	/**
	 *
	 */
	public Rectangle2D getLegendRect() {
		final float mag = this.mMagnification;
		Rectangle2D rect = new Rectangle2D.Float(this.getLegendX(), this
				.getLegendY(), mag * this.mLegendWidth, mag
				* this.mLegendHeight);

		return rect;
	}

	/**
	 *
	 */
	public boolean isResizable(final double w, final double h) {

		Rectangle2D rect = this.getLegendRect();

		// System.out.println(" "+w+" "+rect.getWidth());

		if (w < rect.getWidth() || h < rect.getHeight()) {
			return false;
		}
		return true;

	}

	/**
	 *
	 * @param e
	 */
	public boolean setMouseCursor(final int x, final int y) {
		if (this.isVisible()) {
			if (this.getLegendRect().contains(x, y)) {
				this.setMouseCursor(Cursor.HAND_CURSOR);
				return true;
			}
		}

		return false;
	}

	/**
	 * Sets the axis element.
	 *
	 * @param element
	 *           the axis element
	 * @return true if succeeded
	 */
	public boolean setAxisElement(final SGIFigureElementAxis element) {
		this.mAxisElement = element;

		// set axes
		SGAxis xAxis = this.mAxisElement
				.getAxis(DEFAULT_LEGEND_HORIZONTAL_AXIS);
		SGAxis yAxis = this.mAxisElement
				.getAxis(DEFAULT_LEGEND_VERTICAL_AXIS);
		this.mXAxis = xAxis;
		this.mYAxis = yAxis;

		return true;
	}

	/**
	 *
	 */
	public boolean setGraphElement(final SGIFigureElementGraph element) {
		this.mGraphElement = element;
		return true;
	}

	/**
	 * Returns the location of the x-axis.
	 *
	 * @return the location of the x-axis
	 */
	public int getXAxisLocation() {
		return this.mAxisElement.getLocationInPlane(this.mXAxis);
	}

	/**
	 * Returns the location of the y-axis.
	 *
	 * @return the location of the y-axis
	 */
	public int getYAxisLocation() {
		return this.mAxisElement.getLocationInPlane(this.mYAxis);
	}

	/**
	 * Sets the location of the x-axis.
	 *
	 * @param location
	 *           the location of the x-axis to set
	 * @return true if succeeded
	 */
	public boolean setXAxisLocation(final int location) {
		if (location != SGIFigureElementAxis.AXIS_HORIZONTAL_1
				&& location != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
			return false;
		}
		SGAxis axis = this.mAxisElement.getAxisInPlane(location);
		if (axis == null) {
			return false;
		}
		this.mXAxis = axis;
		return true;
	}

	/**
	 * Sets the location of the y-axis.
	 *
	 * @param location
	 *           the location of the y-axis to set
	 * @return true if succeeded
	 */
	public boolean setYAxisLocation(final int location) {
		if (location != SGIFigureElementAxis.AXIS_VERTICAL_1
				&& location != SGIFigureElementAxis.AXIS_VERTICAL_2) {
			return false;
		}
		SGAxis axis = this.mAxisElement.getAxisInPlane(location);
		if (axis == null) {
			return false;
		}
		this.mYAxis = axis;
		return true;
	}

	/**
	 *
	 */
	public boolean setVisible(final boolean b) {
		this.mLegendVisibleFlag = b;
		return true;
	}

	/**
	 * Sets the transparency of the background.
	 *
	 * @param percentAlpha
	 *          the alpha value of transparency to set
	 * @return true if succeeded
	 */
	@Override
	public boolean setBackgroundTransparent(final int percentAlpha) {
	    return this.mBackgroundPaint.setTransparency(percentAlpha);
	}

	/**
	 * Sets the visibility of the frame lines.
	 *
	 * @param b
	 *          a visibility flag to set
	 * @return true if succeeded
	 */
	public boolean setFrameVisible(final boolean b) {
		this.mFrameVisibleFlag = b;
		return true;
	}

	/**
	 * Sets the frame line width.
	 *
	 * @param lw
	 *           the frame line width to set
	 * @return true if succeeded
	 */
	public boolean setFrameLineWidth(final float lw) {
		if (lw < 0.0f) {
			throw new IllegalArgumentException("lw < 0.0f");
		}
		this.mFrameLineWidth = lw;
		return true;
	}

	/**
	 * Sets the frame line width in given unit.
	 *
	 * @param lw
	 *          the frame line width to set
	 * @param unit
	 *          the unit for the given frame line width
	 * @return true if succeeded
	 */
	public boolean setFrameLineWidth(final float lw, final String unit) {
		final Float lwNew = SGUtility.getLineWidth(lw, unit);
		if (lwNew == null) {
			return false;
		}
		return this.setFrameLineWidth(lwNew);
	}

	/**
	 * Sets the frame line color.
	 *
	 * @param cl
	 *          the frame line color to set
	 * @return true if succeeded
	 */
	public boolean setFrameLineColor(final Color cl) {
		if (cl == null) {
			throw new IllegalArgumentException("cl == null");
		}
		this.mFrameLineColor = cl;
		return true;
	}

	/**
	 * Sets the background color.
	 *
	 * @param cl
	 *          the background color to set
	 * @return true if succeeded
	 */
	@Override
	public boolean setBackgroundColor(final Color cl) {
	    if (cl == null) {
	        throw new IllegalArgumentException("cl == null");
	    }
	    return this.mBackgroundPaint.setColor(cl);
	}

	/**
	 * Sets the font size.
	 *
	 * @param size
	 *           the font size in units of pt
	 * @return true if succeeded
	 */
	public boolean setFontSize(final float size) {
		return this.setFont(this.getFontName(), this.getFontStyle(), size);
	}

	/**
	 * Sets the font size in a given unit.
	 *
	 * @param size
	 *           the font size
	 * @param unit
	 *           the unit of font size
	 * @return true if succeeded
	 */
	public boolean setFontSize(final float size, final String unit) {
		final Float sNew = SGUtility.getFontSize(size, unit);
		if (sNew == null) {
			return false;
		}
		if (this.setFontSize(sNew) == false) {
			return false;
		}
		return true;
	}

	/**
	 * Sets the font style.
	 *
	 * @param style
	 *           the font style
	 * @return true if succeeded
	 */
	public boolean setFontStyle(final int style) {
		if (SGUtilityText.isValidFontStyle(style) == false) {
			return false;
		}
		return this.setFont(this.getFontName(), style, this.getFontSize());
	}

	/**
	 * Sets the font color.
	 *
	 * @param color
	 *           the font color
	 * @return true if succeeded
	 */
	public boolean setFontColor(final Color color) {
		if (color == null) {
			throw new IllegalArgumentException("color == null");
		}
		this.mFontColor = color;
		return true;
	}

	/**
	 * Sets the font name.
	 *
	 * @param name
	 *          the font name
	 * @return true if succeeded
	 */
	public boolean setFontName(final String name) {
		return this.setFont(name, this.getFontStyle(), this.getFontSize());
	}

	/**
	 * Sets the font parameters.
	 *
	 * @param name
	 *          the font name
	 * @param style
	 *          the font style
	 * @param size
	 *          the font size in units of pt
	 * @return true if succeeded
	 */
	private boolean setFont(final String name, final int style, final float size) {
		boolean changed = false;
		if (name != null) {
			if (!name.equals(this.mFontName)) {
				changed = true;
			}
		} else {
			if (this.mFontName != null) {
				changed = true;
			}
		}
		if (!changed) {
			if (style != this.mFontStyle) {
				changed = true;
			}
		}
		if (!changed) {
			if (size != this.mFontSize) {
				changed = true;
			}
		}
		this.mFontName = name;
		this.mFontStyle = style;
		this.mFontSize = size;
		if (changed) {
			this.updateAllDrawingElements();
		}
		return true;
	}

	/**
	 *
	 */
	private SGData getData(final ElementGroupSetInLegend groupSet) {
		for (int ii = 0; ii < this.mChildList.size(); ii++) {
			ElementGroupSetInLegend groupSet_ = (ElementGroupSetInLegend) this.mChildList
					.get(ii);
			if (groupSet_.equals(groupSet)) {
				SGData data = groupSet.getData();
				return data;
			}
		}

		return null;
	}

	/**
	 * Returns whether the legend is visible.
	 *
	 * @return true if the legend is visible
	 */
	public boolean isVisible() {
		return this.mLegendVisibleFlag;
	}

	/**
	 *
	 */
	public boolean isFrameVisible() {
		return this.mFrameVisibleFlag;
	}

	/**
	 *
	 */
	public Rectangle2D getRectOfGroup(final SGElementGroup group) {
		for (int ii = 0; ii < this.mChildList.size(); ii++) {
			ElementGroupSetInLegend leg = (ElementGroupSetInLegend) this.mChildList
					.get(ii);
			ArrayList<SGElementGroup> groupList = leg.getElementGroupList();
			for (int jj = 0; jj < groupList.size(); jj++) {
				SGElementGroup group_ = (SGElementGroup) groupList.get(jj);
				if (group_.equals(group)) {
					return leg.mDataRect;
				}
			}

		}

		return null;
	}

	/**
	 *
	 */
	public Rectangle2D getRectOfGroupSet(final SGElementGroupSet groupSet) {
		for (int ii = 0; ii < this.mChildList.size(); ii++) {
			ElementGroupSetInLegend leg = (ElementGroupSetInLegend) this.mChildList
					.get(ii);
			if (groupSet.equals(leg)) {
				return leg.mDataRect;
			}
		}

		return null;
	}

	/**
	 *
	 */
	private ElementGroupLine getGroupLine(final ElementGroupSetInLegend groupSet) {
		ArrayList<SGElementGroup> groupList = groupSet.getElementGroupList();
		for (int ii = 0; ii < groupList.size(); ii++) {
			SGElementGroup group = (SGElementGroup) groupList.get(ii);
			if (group instanceof ElementGroupLine) {
				return (ElementGroupLine) group;
			}
		}

		return null;
	}

	/**
	 *
	 */
	private ElementGroupBar getGroupBar(final ElementGroupSetInLegend groupSet) {
		ArrayList<SGElementGroup> groupList = groupSet.getElementGroupList();
		for (int ii = 0; ii < groupList.size(); ii++) {
			SGElementGroup group = (SGElementGroup) groupList.get(ii);
			if (group instanceof ElementGroupBar) {
				return (ElementGroupBar) group;
			}
		}

		return null;
	}

	/**
	 *
	 */
	private ElementGroupSymbol getGroupSymbol(
			final ElementGroupSetInLegend groupSet) {
		ArrayList<SGElementGroup> groupList = groupSet.getElementGroupList();
		for (int ii = 0; ii < groupList.size(); ii++) {
			SGElementGroup group = (SGElementGroup) groupList.get(ii);
			if (group instanceof ElementGroupSymbol) {
				return (ElementGroupSymbol) group;
			}
		}

		return null;
	}

	/**
	 *
	 * @param groupSet
	 * @return
	 */
	private ElementGroupErrorBar getGroupErrorBar(
			final ElementGroupSetInLegend groupSet) {
		ArrayList<SGElementGroup> groupList = groupSet.getElementGroupList();
		for (int ii = 0; ii < groupList.size(); ii++) {
			SGElementGroup group = (SGElementGroup) groupList.get(ii);
			if (group instanceof ElementGroupErrorBar) {
				return (ElementGroupErrorBar) group;
			}
		}

		return null;
	}

	/**
	 *
	 */
	private ElementGroupArrow getGroupArrow(
			final ElementGroupSetInLegend groupSet) {
		ArrayList<SGElementGroup> groupList = groupSet.getElementGroupList();
		for (int ii = 0; ii < groupList.size(); ii++) {
			SGElementGroup group = (SGElementGroup) groupList.get(ii);
			if (group instanceof ElementGroupArrow) {
				return (ElementGroupArrow) group;
			}
		}

		return null;
	}

	private ElementGroupPseudocolorMap getColorMap(
			final ElementGroupSetInLegend groupSet) {
		ArrayList<SGElementGroup> groupList = groupSet.getElementGroupList();
		for (int ii = 0; ii < groupList.size(); ii++) {
			SGElementGroup group = (SGElementGroup) groupList.get(ii);
			if (group instanceof ElementGroupPseudocolorMap) {
				return (ElementGroupPseudocolorMap) group;
			}
		}
		return null;
	}

	/**
	 * Called when an action event is generated.
	 * @param e
	 *          an action event
	 */
	public void actionPerformed(final ActionEvent e) {

		String command = e.getActionCommand();
		Object source = e.getSource();

		// from the text field
		if (source.equals(this.mTextField)) {
			this.closeTextField();
			return;
		}

		// from the pop-up menu
		if (command.equals(MENUCMD_HIDE)) {
			this.setVisible(false);
			repaint();
			this.setChanged(true);
			this.notifyToRoot();
			return;
		} else if (command.equals(MENUCMD_PROPERTY)) {
			// clear all focused legend
			this.clearAllFocusedData();

			// show the property dialog of legend figure element
			this.setPropertiesOfSelectedObjects(this);
		}
	}

	/**
	 * Commit the change of the properties.
	 * @return
	 *         true if succeeded
	 */
	public boolean commit() {

		// compare two properties
		SGProperties pTemp = this.mTemporaryProperties;
		SGProperties pPresent = this.getProperties();
		if (pTemp.equals(pPresent) == false) {
			this.setChanged(true);
		}

		// clear temporary properties
		this.mTemporaryProperties = null;

		// update drawing elements
		if (this.updateAllDrawingElements() == false) {
			return false;
		}
		notifyChangeOnCommit();
		this.repaint();

		return true;
	}

	/**
	 * Cancel the setting of properties.
	 * @return
	 *         true if succeeded
	 */
	public boolean cancel() {

		// set temporay properties to drawing elements to cancel the change
		if (this.setProperties(this.mTemporaryProperties) == false) {
			return false;
		}

		// clear temporay properties
		this.mTemporaryProperties = null;

		// update drawing elements
		if (this.updateAllDrawingElements() == false) {
			return false;
		}
		notifyChangeOnCancel();
		this.repaint();
		return true;
	}

	/**
	 * Set properties from the property dialog.
	 * @return
	 *         true if succeeded
	 */
	public boolean preview() {

		// update drawing elements
		if (this.updateAllDrawingElements() == false) {
			return false;
		}
		notifyChange();
		this.repaint();
		return true;
	}

	/**
	 *
	 */
	public boolean setMementoBackward() {
		boolean flag = super.setMementoBackward();
		if (!flag) {
			return false;
		}

		this.updateAllDrawingElements();
		this.notifyChangeOnUndo();

		return true;
	}

	/**
	 *
	 */
	public boolean setMementoForward() {
		boolean flag = super.setMementoForward();
		if (!flag) {
			return false;
		}

		this.updateAllDrawingElements();
		this.notifyChangeOnUndo();

		return true;
	}

	/**
	 * Returns a list of visible group sets.
	 *
	 * @return a list of group sets
	 */
	protected ArrayList getVisibleLegendList() {
		ArrayList list = new ArrayList();
		for (int ii = 0; ii < this.mChildList.size(); ii++) {
			ElementGroupSetInLegend groupSet = (ElementGroupSetInLegend) this.mChildList
					.get(ii);
			if (groupSet.isVisible()) {
				list.add(groupSet);
			}
		}

		return list;
	}

	/**
	 * Returns a list of viewable group sets.
	 *
	 * @return a list of group sets
	 */
	private List<ElementGroupSetInLegend> getViewableLegendList() {
		List<ElementGroupSetInLegend> list = new ArrayList<ElementGroupSetInLegend>();
		for (int ii = 0; ii < this.mChildList.size(); ii++) {
			ElementGroupSetInLegend groupSet = (ElementGroupSetInLegend) this.mChildList
					.get(ii);
			if (groupSet.isViewable()) {
				list.add(groupSet);
			}
		}
		return list;
	}

	/**
	 * Returns a list of viewable data.
	 *
	 * @return a list of viewable data
	 */
	@Override
	public List<SGData> getViewableDataList() {
		List<SGData> list = new ArrayList<SGData>();
		for (int ii = 0; ii < this.mChildList.size(); ii++) {
			ElementGroupSetInLegend groupSet = (ElementGroupSetInLegend) this.mChildList
					.get(ii);
			if (groupSet.isViewable()) {
				list.add(groupSet.getData());
			}
		}
		return list;
	}

	/**
	 *
	 */
	public boolean createDataObject(final Element el, final SGData data,
			final boolean readDataProperty) {

		if (super.createDataObject(el, data, readDataProperty) == false) {
			return false;
		}

		// create drawing elements
		this.updateAllDrawingElements();

		// set false the flag
		this.mStartFlag = false;

		return true;
	}

	/**
	 * Cut focused copiable objects.
	 *
	 * @return a list of cut objects
	 */
	public List<SGICopiable> cutFocusedObjects() {
		// returns an empty list because no objects exist that can be cut
		// other than data
		return new ArrayList<SGICopiable>();
	}

	/**
	 * Returns a list of cut data objects.
	 *
	 * @return
	 *         a list of cut data objects
	 */
	public List<SGData> cutFocusedData() {
		List<SGData> list = this.getFocusedDataList();
		this.hideSelectedData();
		return list;
	}

	/**
	 *
	 */
	protected boolean hideSelectedData() {
		if (super.hideSelectedData() == false) {
			return false;
		}
		this.updateAllDrawingElements();
		this.repaint();
		return true;
	}

	/**
	 * Move the focused objects to front or back.
	 *
	 * @param toFront
	 *                flag whether to move to front or back
	 * @return
	 *                true if succeeded
	 */
	public boolean moveFocusedObjects(final boolean toFront) {
		// do nothing
		return true;
	}

	/**
	 * Move the focused objects to forward or backward for given steps.
	 *
	 * @param num
	 *           the number of levels to move
	 * @return true if succeeded
	 */
	public boolean moveFocusedObjects(int num) {
		// do nothing
		return true;
	}

	/**
	 * Returns the index of data object in legend.
	 * @param data
	 *             a data object
	 * @return
	 *             the index of data object in legend or -1 if not found
	 */
	public int getIndex(final SGData data) {
		List<SGIChildObject> cList = this.getVisibleChildList();
		for (int ii = 0; ii < cList.size(); ii++) {
			ElementGroupSetInLegend gs = (ElementGroupSetInLegend) cList
					.get(ii);
			SGData d = gs.getData();
			if (d.equals(data)) {
				return ii;
			}
		}
		return -1;
	}

	/**
	 * Sort the order of legend objects.
	 * @param dataArray
	 *                   an array of data
	 * @param indexArray
	 *                   an array of index
	 * @return
	 *                   true if succeeded
	 */
	public boolean sortLegend(SGData[] dataArray, int[] indexArray) {
		if (dataArray == null || indexArray == null) {
			throw new IllegalArgumentException(
					"dataArray == null || indexArray == null");
		}
		if (dataArray.length != indexArray.length) {
			throw new IllegalArgumentException(
					"dataArray.length != indexArray.length");
		}
		final int len = dataArray.length;

		// create the data list for given order
		int indexMin = Integer.MAX_VALUE;
		int indexMax = Integer.MIN_VALUE;
		for (int ii = 0; ii < len; ii++) {
			if (indexArray[ii] < indexMin) {
				indexMin = indexArray[ii];
			}
			if (indexArray[ii] > indexMax) {
				indexMax = indexArray[ii];
			}
		}
		int[] indexArrayNew = new int[len];
		for (int ii = 0; ii < len; ii++) {
			indexArrayNew[ii] = indexArray[ii] - indexMin;
		}
		int lenNew = indexMax - indexMin + 1;
		List[] dListArray = new ArrayList[lenNew];
		for (int ii = 0; ii < lenNew; ii++) {
			dListArray[ii] = new ArrayList();
		}
		for (int ii = 0; ii < len; ii++) {
			dListArray[indexArrayNew[ii]].add(dataArray[ii]);
		}
		List oDataList = new ArrayList();
		for (int ii = 0; ii < lenNew; ii++) {
			oDataList.addAll(dListArray[ii]);
		}

		// sort the legend objects
		List cList = new ArrayList();
		for (int ii = 0; ii < oDataList.size(); ii++) {
			SGData data = (SGData) oDataList.get(ii);
			SGElementGroupSet gs = this.getElementGroupSet(data);
			cList.add(gs);
		}

		// update the child list
		this.mChildList.clear();
		this.mChildList.addAll(cList);

		return true;
	}

	/**
	 *
	 * @return
	 */
	public String getTagName() {
		return TAG_NAME_LEGEND;
	}

	/**
	 *
	 */
	public boolean writeProperty(final Element el, SGExportParameter params) {
		SGPropertyMap map = this.getPropertyFileMap(params);
		map.setToElement(el);
		return true;
	}
	

	/**
	 *
	 */
	public Element[] createElement(final Document document, SGExportParameter params) {
		Element el = this.createThisElement(document, params);
		return new Element[] { el };
	}

    /**
     * Read properties from the Element object.
     *
     * @param element
     *            an Element object which has properties
     * @param versionNumber
     *            the version number of property file
     * @return true if succeeded
     */
    public boolean readProperty(final Element element, final String versionNumber) {
		String str = null;
		Number num = null;
		Color cl = null;
		Boolean b = null;

		// set legend visible
		str = element.getAttribute(SGIFigureElementLegend.KEY_LEGEND_VISIBLE);
		if (str.length() != 0) {
			b = SGUtilityText.getBoolean(str);
			if (b == null) {
				return false;
			}
			if (this.setVisible(b.booleanValue()) == false) {
				return false;
			}
		}

		// set axes
		str = element.getAttribute(KEY_X_AXIS_POSITION);
		if (str.length() != 0) {
			SGAxis xAxis = this.mAxisElement.getAxis(str);
			this.mXAxis = xAxis;
		}

		str = element.getAttribute(KEY_Y_AXIS_POSITION);
		if (str.length() != 0) {
			SGAxis yAxis = this.mAxisElement.getAxis(str);
			this.mYAxis = yAxis;
		}

		str = element.getAttribute(KEY_X_VALUE);
		if (str.length() != 0) {
			num = SGUtilityText.getDouble(str);
			if (num == null) {
				return false;
			}
			final double xValue = num.doubleValue();
			if (this.mXAxis.isValidValue(xValue) == false) {
				return false;
			}
			if (this.setXValue(xValue) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_Y_VALUE);
		if (str.length() != 0) {
			num = SGUtilityText.getDouble(str);
			if (num == null) {
				return false;
			}
			final double yValue = num.doubleValue();
			if (this.mYAxis.isValidValue(yValue) == false) {
				return false;
			}
			if (this.setYValue(yValue) == false) {
				return false;
			}
		}

		// set frame visible
		str = element.getAttribute(SGIFigureElementLegend.KEY_FRAME_VISIBLE);
		if (str.length() != 0) {
			b = SGUtilityText.getBoolean(str);
			if (b == null) {
				return false;
			}
			if (this.setFrameVisible(b.booleanValue()) == false) {
				return false;
			}
		}

		// set frame line width
		str = element.getAttribute(SGIFigureElementLegend.KEY_FRAME_LINE_WIDTH);
		if (str.length() != 0) {
			StringBuffer uFrameLineWidth = new StringBuffer();
			num = SGUtilityText.getNumber(str, uFrameLineWidth);
			if (num == null) {
				return false;
			}
			if (this.setFrameLineWidth(num.floatValue(), uFrameLineWidth
					.toString()) == false) {
				return false;
			}
		}

		// set frame line color
		str = element.getAttribute(SGIFigureElementLegend.KEY_FRAME_LINE_COLOR);
		if (str.length() != 0) {
			cl = SGUtilityText.parseColor(str);
			if (cl == null) {
				return false;
			}
			if (this.setFrameLineColor(cl) == false) {
				return false;
			}
		}

		// background color
		str = element.getAttribute(SGIFigureElementLegend.KEY_BACKGROUND_COLOR);
		if (str.length() != 0) {
			cl = SGUtilityText.parseColor(str);
			if (cl == null) {
				return false;
			}
			if (this.setBackgroundColor(cl) == false) {
				return false;
			}
		}

		// transparent
		str = element.getAttribute(SGIFigureElementLegend.KEY_BACKGROUND_TRANSPARENT);
                if (str.length() != 0) {
                    b = SGUtilityText.getBoolean(str);
                    if (b != null) {
                        if (b.booleanValue() == false) {
                            if (this.setBackgroundTransparent(SGTransparentPaint.ALL_OPAQUE_VALUE) == false) {
                                return false;
                            }
                        } else {
                            if (this.setBackgroundTransparent(SGTransparentPaint.ALL_TRANSPARENT_VALUE) == false) {
                                return false;
                            }
                        }
                    } else {
                        num = SGUtilityText.getInteger(str, SGIConstants.percent);
                        if (num == null) {
                            return false;
                        }
                        if (this.setBackgroundTransparent(num.intValue()) == false) {
                            return false;
                        }
                    }
                }

		// set font name
		str = element.getAttribute(KEY_FONT_NAME);
		if (str.length() != 0) {
			final String fontName = str;
			if (this.setFontName(fontName) == false) {
				return false;
			}
		}

		// set font size
		str = element.getAttribute(KEY_FONT_SIZE);
		if (str.length() != 0) {
			StringBuffer uFontSize = new StringBuffer();
			num = SGUtilityText.getNumber(str, uFontSize);
			if (num == null) {
				return false;
			}
			if (this.setFontSize(num.floatValue(), uFontSize.toString()) == false) {
				return false;
			}
		}

		// set font style
		str = element.getAttribute(KEY_FONT_STYLE);
		if (str.length() != 0) {
			final Integer fontStyle = SGUtilityText.getFontStyle(str);
			if (fontStyle == null) {
				return false;
			}
			if (this.setFontStyle(fontStyle.intValue()) == false) {
				return false;
			}
		}

		// set the font color
		str = element.getAttribute(KEY_STRING_COLORS);
		if (str.length() != 0) {
			cl = SGUtilityText.parseColor(str);
			if (cl == null) {
				return false;
			}
			if (this.setFontColor(cl) == false) {
				return false;
			}
		}

		// set symbol span
		// SymbolSpan is appeared since ver. 0.9.1
		str = element.getAttribute(KEY_SYMBOL_SPAN);
		float symbolSpan;
		if (str.length() != 0) {
			num = SGUtilityText.getLengthInPoint(str);
			if (num == null) {
				return false;
			}
			symbolSpan = num.floatValue();
		} else {
			// for previous version before 0.9.1
			num = Float.valueOf((float) SGUtilityText.convertToPoint(
					DEFAULT_LEGEND_SYMBOL_SPAN, SYMBOL_SPAN_UNIT));
			symbolSpan = num.floatValue();
		}
		if (this.setSymbolSpan(symbolSpan) == false) {
			return false;
		}

		return true;
	}

	/**
	 * An interface that element groups in the legend must implement.
	 */
	private interface ILegendElement {

		/**
		 * Returns the preferred width.
		 * @return
		 *         the preferred width
		 */
		public float getPreferredWidth();

		/**
		 * Returns the preferred height.
		 * @return
		 *         the preferred height
		 */
		public float getPreferredHeight();

		/**
		 *
		 * @param rect
		 */
		public void setDataElementBounds(final Rectangle2D rect);

		/**
		 *
		 * @return
		 */
		public boolean createDrawingElementInLegend();

		/**
		 * Returns the number of points of this element group.
		 * @return
		 *         the number of points
		 */
		public int getNumberOfPoints();

		/**
		 * Sets the element group set.
		 * @param gs
		 *           the element group set.
		 * @return
		 *           true if succeeded
		 */
		public boolean setElementGroupSet(ElementGroupSetInLegend gs);
	}

	/**
	 * A legend for single data object.
	 */
	private abstract class ElementGroupSetInLegend extends
			SGElementGroupSetForData implements ActionListener,
			SGIChildObject, SGISelectable, SGIUndoable,
			SGIDataPropertyDialogObserver {

		/**
		 * A rectangle for this legend.
		 */
		protected Rectangle2D mDataRect = null;

		/**
		 * String object which has a text actually displayed on the screen.
		 */
		protected SGDrawingElementString2DExtended mDrawingString = null;

		/**
		 * Temporary properties for this data.
		 */
		protected SGProperties mTemporaryPropertiesInner = null;

		/**
		 * The default constructor.
		 */
		protected ElementGroupSetInLegend(SGData data) {
			super(data);
		}

	    /**
	     * Returns the commands to move data objects.
	     *
	     * @return the commands
	     */
	    protected List<String> getMoveCommandList() {
	    	List<String> list = new ArrayList<String>();
	    	list.add(MENUCMD_MOVE_TO_TOP);
	    	list.add(MENUCMD_MOVE_TO_UPPER);
	    	list.add(MENUCMD_MOVE_TO_LOWER);
	    	list.add(MENUCMD_MOVE_TO_BOTTOM);
	    	return list;
	    }

		/**
		 * Returns whether this group set is visible in two means:
		 * the data is not deleted and visibility flag is set to true.
		 *
		 * @return true if this legend is not deleted and is visible
		 */
		private boolean isViewable() {
			return (this.isVisible() && this.isVisibleInLegend());
		}

		/**
		 * Returns whether this group set is visible.
		 * @return
		 *         true if this legend is visible
		 */
		public boolean getLegendVisibleFlag() {
			return this.isVisibleInLegend();
		}

		/**
		 * Sets the location of x-axis.
		 *
		 * @param location
		 *           the location of the x-axis
		 */
		public boolean setXAxisLocation(final int location) {
			if (location != SGIFigureElementAxis.AXIS_HORIZONTAL_1
					&& location != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
				return false;
			}
			this.mXAxis = mAxisElement.getAxisInPlane(location);
			return true;
		}

		/**
		 * Sets the location of y-axis.
		 *
		 * @param location
		 *           the location of the y-axis
		 */
		public boolean setYAxisLocation(final int location) {
			if (location != SGIFigureElementAxis.AXIS_VERTICAL_1
					&& location != SGIFigureElementAxis.AXIS_VERTICAL_2) {
				return false;
			}
			this.mYAxis = mAxisElement.getAxisInPlane(location);
			return true;
		}

		/**
		 * Create a string object to display the data name.
		 * @return
		 *         true if succeeded
		 */
		private boolean createStringElement() {
			SGDrawingElementString2DExtended el = new SGDrawingElementString2DExtended(
					this.mName, mFontName, mFontStyle, mFontSize, mFontColor,
					this.getMagnification(), 0.0f);
			this.mDrawingString = el;
			return true;
		}

		/**
		 *
		 * @return
		 */
		private boolean paintString(final Graphics2D g2d) {
			if (this.mDrawingString != null) {
				this.mDrawingString.paint(g2d);
			}
			return true;
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
			if (this.mDrawingString != null) {
				if (this.mDrawingString.setMagnification(mag) == false) {
					return false;
				}
			}
			return true;
		}

		/**
		 *
		 * @return
		 */
		public SGDrawingElementString2DExtended getStringElement() {
			return this.mDrawingString;
		}

		/**
		 *
		 * @return
		 */
		public Rectangle2D getStringBounds() {
			if (this.mDrawingString == null) {
				return null;
			}
			Rectangle2D rect = this.mDrawingString.getStringRect();
			final float visual_height = (float) rect.getHeight();
			final float visual_y = (float) rect.getY();
			final float strike_through_offset = this.mDrawingString
					.getStrikethroughOffset();
			final float advance = this.mDrawingString.getAdvance();
			final float top = -visual_y + strike_through_offset;
			final float bottom = visual_height - top;
			float glowh = 0.0f;
			float glowy = 0.0f;
			if (bottom > top) {
				glowh = bottom - top;
				glowy = glowh;
			} else {
				glowh = top - bottom;
			}
			rect.setRect(0, glowy, advance, visual_height + glowh);
			return rect;
		}

		/**
		 *
		 * @param rect
		 */
		private void setRect(Rectangle2D rect) {
			this.mDataRect = rect;
		}

		/**
		 *
		 */
		public float getMaxDataElementWidth() {
			float max = 0.0f;
			final List<SGElementGroup> list = this.mDrawingElementGroupList;
			for (int ii = 0; ii < list.size(); ii++) {
				ILegendElement el = (ILegendElement) list.get(ii);
				final float width = el.getPreferredWidth();
				if (width > max) {
					max = width;
				}
			}

			return max;
		}

		/**
		 *
		 */
		public double getMaxDataElementHeight() {
			double max = 0.0;
			List<SGElementGroup> list = this.mDrawingElementGroupList;
			for (int ii = 0; ii < list.size(); ii++) {
				SGElementGroup group = (SGElementGroup) list.get(ii);
				if (!group.isVisible()) {
					continue;
				}
				ILegendElement el = (ILegendElement) list.get(ii);
				double height = el.getPreferredHeight();
				if (height > max) {
					max = height;
				}
			}

			return max;
		}

		/**
		 *
		 * @param rect
		 */
		void setDrawingElementBounds(final Rectangle2D rect) {
			List<SGElementGroup> list = this.mDrawingElementGroupList;
			for (int ii = 0; ii < list.size(); ii++) {
				SGElementGroup group = (SGElementGroup) list.get(ii);
				if (!group.isVisible()) {
					continue;
				}
				ILegendElement el = (ILegendElement) list.get(ii);
				el.setDataElementBounds(rect);
			}
		}

		/**
		 *
		 * @return
		 */
		protected boolean createDrawingElement() {
			final List<SGElementGroup> list = this.mDrawingElementGroupList;
			for (int ii = 0; ii < list.size(); ii++) {
				SGElementGroup group = (SGElementGroup) list.get(ii);
				if (group.isVisible()) {
					ILegendElement el = (ILegendElement) group;
					el.createDrawingElementInLegend();
				}
			}

			return true;
		}

		/**
		 *
		 */
		public void paintGraphics2D(final Graphics2D g2d) {
			// draw the name of data
			g2d.setPaint(SGFigureElementLegend.this.mFontColor);
			this.paintString(g2d);

			// draw the symbols
			this.paintSymbol(g2d);
		}

		abstract void paintSymbol(Graphics2D g2d);

		abstract boolean onMouseClicked(final MouseEvent e);

		/**
		 *
		 */
		boolean addDrawingElementGroup(final SGElementGroup group) {

			ILegendElement lElement = (ILegendElement) group;
			lElement.setElementGroupSet(this);

			// create drawing elements
			group.initDrawingElement(lElement.getNumberOfPoints());

//			// set the properties to drawing elements
//			if (group.setPropertiesOfDrawingElements() == false) {
//				return false;
//			}

			// set magnification
			group.setMagnification(this.getMagnification());

			// add to the list
			this.mDrawingElementGroupList.add(group);

			return true;
		}

		protected SGAxis getAxis(final int location) {
			return SGFigureElementLegend.this.mAxisElement
					.getAxisInPlane(location);
		}

		/**
		 * Returns a property dialog.
		 *
		 * @return property dialog
		 */
		public SGPropertyDialog getPropertyDialog() {
			SGData data = this.getData();
			//            SGPropertyDialog dg = SGFigureElementLegend.this.getDataDialog(data.getDataType());
			SGPropertyDialog dg = SGFigureElementLegend.this
					.getDataDialog(data);
			return dg;
		}

		/**
		 * Prepare before the property setting starts.
		 */
		public boolean prepare() {
			// create temporay properties
			this.mTemporaryPropertiesInner = this.getProperties();
			return true;
		}

		/**
		 * Update drawing elements with related data object.
		 * @return
		 *         true if succeeded
		 */
		public boolean updateWithData() {
			// update drawing elements
			if (updateAllDrawingElements() == false) {
				return false;
			}
			return true;
		}

		/**
		 * Commit the change of the properties.
		 * @return
		 *         true if succeeded
		 */
		public boolean commit() {

	        // compare current data properties and temporary data properties and notify the change
	    	this.notifyDataProperties(this.mTemporaryPropertiesInner, 
	    			SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_COMMIT, 
	    			SGIFigureElement.NOTIFY_DATA_PROPERTIES_CHANGE_ON_COMMIT);

			// compare two properties
			SGProperties pTemp = this.mTemporaryPropertiesInner;
			SGProperties pPresent = this.getProperties();
			if (pTemp.equals(pPresent) == false) {
				this.setChanged(true);
			}

			// clear temporary properties
			this.mTemporaryPropertiesInner = null;

			// update drawing elements
			if (this.updateWithData() == false) {
				return false;
			}
			notifyChangeOnCommit();
			repaint();

			return true;
		}

		/**
		 * Cancel the setting of properties.
		 * @return
		 *         true if succeeded
		 */
		public boolean cancel() {

	        // compare current data properties and temporary data properties and notify the change
	    	this.notifyDataProperties(this.mTemporaryPropertiesInner, 
	    			SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_CANCEL, 
	    			SGIFigureElement.NOTIFY_DATA_PROPERTIES_CHANGE_ON_CANCEL);

			// set temporary properties to drawing elements to cancel the change
			if (this.setProperties(this.mTemporaryPropertiesInner) == false) {
				return false;
			}

			// clear temporary properties
			this.mTemporaryPropertiesInner = null;

			// update drawing elements
			if (this.updateWithData() == false) {
				return false;
			}
			notifyChangeOnCancel();
			repaint();

			return true;
		}

		/**
		 * Set properties from the property dialog.
		 * @return
		 *         true if succeeded
		 */
		public boolean preview() {

	        // compare current data properties and temporary data properties and notify the change
	    	this.notifyDataProperties(this.mTemporaryPropertiesInner, 
	    			SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_PREVIEW, 
	    			SGIFigureElement.NOTIFY_DATA_PROPERTIES_CHANGE_ON_PREVIEW);

			// update drawing elements
			if (this.updateWithData() == false) {
				return false;
			}
			notifyChange();
			repaint();

			return true;
		}

        @Override
	    protected void notifyToListener(final String msg) {
	    	SGFigureElementLegend.this.notifyToListener(msg);
	    }

	    @Override
	    protected void notifyToListener(final String msg, final Object source) {
	        SGFigureElementLegend.this.notifyToListener(msg, source);
	    }

		/**
		 *
		 * @return
		 */
		public String getTagName() {
			return "";
		}

		/**
		 *
		 */
		public boolean writeProperty(final Element el) {
			return true;
		}

		/**
		 *
		 */
		public void actionPerformed(final ActionEvent e) {
			String command = e.getActionCommand();
			Object source = e.getSource();

			if (command.equals(SGIConstants.MENUCMD_PROPERTY)) {
				// clear the selection of figure element
				SGFigureElementLegend.this.setSelected(false);

				// show the property dialog of a legend
				SGFigureElementLegend.this.setPropertiesOfSelectedObjects(this);
			} else if (command.equals(MENUCMD_MOVE_TO_TOP)) {
				moveChildToEnd(this.getID(), false);
			} else if (command.equals(MENUCMD_MOVE_TO_UPPER)) {
				moveChild(this.getID(), false);
			} else if (command.equals(MENUCMD_MOVE_TO_LOWER)) {
				moveChild(this.getID(), true);
			} else if (command.equals(MENUCMD_MOVE_TO_BOTTOM)) {
				moveChildToEnd(this.getID(), true);
//			} else if (command.equals(SGIConstants.MENUCMD_ANIMATION)) {
//				this.mTempDataProperties = this.mData.getProperties();
//				notifyToListener(command);
//				doAnimation(this);
			} else {
				notifyToListener(command, source);
			}

			// update drawing elements
			updateAllDrawingElements();
			repaint();
		}

		/**
		 *
		 */
		public boolean getProperties(final SGProperties p) {
			if ((p instanceof ElementGroupSetPropertiesInFigureElement) == false) {
				return false;
			}
			if (super.getProperties(p) == false) {
				return false;
			}
			ElementGroupSetPropertiesInFigureElement ep = (ElementGroupSetPropertiesInFigureElement) p;
			SGIFigureElementAxis aElement = SGFigureElementLegend.this.mAxisElement;
			ep.xAxis = aElement.getLocationInPlane(this.getXAxis());
			ep.yAxis = aElement.getLocationInPlane(this.getYAxis());
			return true;
		}

		/**
		 *
		 */
		public boolean setProperties(final SGProperties p) {
			if ((p instanceof ElementGroupSetPropertiesInFigureElement) == false) {
				return false;
			}
			if (super.setProperties(p) == false) {
				return false;
			}
			ElementGroupSetPropertiesInFigureElement ep = (ElementGroupSetPropertiesInFigureElement) p;
			SGIFigureElementAxis aElement = SGFigureElementLegend.this.mAxisElement;
			this.setXAxis(aElement.getAxisInPlane(ep.xAxis));
			this.setYAxis(aElement.getAxisInPlane(ep.yAxis));
			return true;
		}

		/**
		 * undo
		 */
		public boolean setMementoBackward() {
			if (super.setMementoBackward() == false)
				return false;

			updateAllDrawingElements();
			notifyChangeOnUndo();

			return true;
		}

		/**
		 * redo
		 */
		public boolean setMementoForward() {
			if (super.setMementoForward() == false)
				return false;

			updateAllDrawingElements();
			notifyChangeOnUndo();

			return true;
		}

		/**
		 *
		 *
		 */
		public void notifyToRoot() {
			SGFigureElementLegend.this.notifyToRoot();
		}

		/**
		 * Sets the current frame index.
		 *
		 * @param index the frame index to be set
		 */
		public void setCurrentFrameIndex(int index) {
			super.setCurrentFrameIndex(index);
			// notify the change
			notifyChange();
		}

		protected void onSaveChanges() {
			// notify the change
			this.setChanged(true);
			notifyChangeOnCommit();
			this.notifyToRoot();
		}

		/**
		 * Cancels all changes of this data source.
		 *
		 */
		public void cancelChanges() {
			super.cancelChanges();
			// notify the change
			notifyChange();
		}

		/**
		 * Sets the properties.
		 *
		 * @param map
		 *           a map of properties
		 * @return the result of setting properties
		 */
		public SGPropertyResults setProperties(SGPropertyMap map) {
			// do nothing
			return null;
		}

		@Override
	    protected boolean checkChanged() {
	    	if (this.mTemporaryPropertiesInner == null) {
	    		return false;
	    	}
	    	return !this.mTemporaryPropertiesInner.equals(this.getProperties());
	    }
	}

	/**
	 *
	 */
	private class ElementGroupSetInLegendSXY extends ElementGroupSetInLegend
			implements SGIElementGroupSetSXY, 	SGISXYDataConstants {

	    /**
	     * Shift to the x direction.
	     */
	    protected double mShiftX;

	    /**
	     * Shift to the y direction.
	     */
	    protected double mShiftY;

		/**
		 *
		 */
		protected ElementGroupSetInLegendSXY(SGData data) {
			super(data);
		}

	    /**
	     * Returns the figure element.
	     * 
	     * @return the figure element
	     */
	    public SGFigureElementForData getFigureElement() {
	    	return SGFigureElementLegend.this;
	    }

		public ELEMENT_TYPE getSelectedGroupType() {
			return this.mSelectedGroupType;
		}

		private ELEMENT_TYPE mSelectedGroupType = ELEMENT_TYPE.Void;

	    /**
	     * The series index in multiple graph
	     * when this group set is created from the group set of multiple graph.
	     */
	    private int mSeriesIndex = -1;

	    /**
	     * The total number of series in multiple graph
	     * which creates this group set.
	     */
	    private int mNumberOfSeries = -1;

	    public int getSeriesIndex() {
	        return this.mSeriesIndex;
	    }

	    public int getNumberOfSeries() {
	        return this.mNumberOfSeries;
	    }

	    public void setSeriesIndexAndNumber(final int index, final int number) {
	        this.mSeriesIndex = index;
	        this.mNumberOfSeries = number;
	    }

	    private SGLineStyle mLineStyle = null;

		public SGLineStyle getLineStyle() {
			return (this.mLineStyle != null) ? (SGLineStyle) this.mLineStyle.clone() : null;
		}

		public void setLineStyle(SGLineStyle style) {
			SGLineStyle lineStyle = (SGLineStyle) style.clone();
			this.mLineStyle = lineStyle;
			SGElementGroupLine lineGroup = this.getLineGroup();
			lineGroup.setStyle(lineStyle);
		}

	    private String mLineColorMapName = null;

	    /**
	     * Returns the name of the color map for lines.
	     * 
	     * @return the name of the color map for lines
	     */
		@Override
	    public String getLineColorMapName() {
	    	return this.mLineColorMapName;
	    }

		/**
		 * Sets the color map for lines.
		 * 
		 * @param name
		 *           name of the map to set
		 * @return true if succeeded
		 */
	    public boolean setLineColorMapName(String name) {
	    	this.mLineColorMapName = name;
	    	return true;
	    }

		/**
		 *
		 */
		void paintSymbol(final Graphics2D g2d) {

			// bar
			ElementGroupBar groupBar = getGroupBar(this);
			if (groupBar != null) {
				if (groupBar.isVisible()) {
					groupBar.paintElement(g2d);
				}
			}

			// error bar
			SGISXYTypeData dataSXY = (SGISXYTypeData) this.getData();
			if (dataSXY.isErrorBarAvailable()) {
				ElementGroupErrorBar groupErrorBar = getGroupErrorBar(this);
				if (groupErrorBar != null) {
					if (groupErrorBar.isVisible()) {
						groupErrorBar.paintElement(g2d);
					}
				}
			}

			// line
			ElementGroupLine groupLine = getGroupLine(this);
			if (groupLine != null) {
				if (groupLine.isVisible()) {
					groupLine.paintElement(g2d);
				}
			}

			// symbol
			ElementGroupSymbol groupSymbol = getGroupSymbol(this);
			if (groupSymbol != null) {
				if (groupSymbol.isVisible()) {
					groupSymbol.paintElement(g2d);
				}
			}

		}

	    /**
		 * Returns a list of line groups.
		 *
		 * @return a list of line groups
		 */
		public List<SGElementGroupLine> getLineGroups() {
			List<SGElementGroupLine> retList = new ArrayList<SGElementGroupLine>();
			List<SGElementGroup> list = SGUtilityForFigureElement.getGroups(
					SGElementGroupLine.class, this.mDrawingElementGroupList);
			for (int ii = 0; ii < list.size(); ii++) {
				retList.add((SGElementGroupLine) list.get(ii));
			}
			return retList;
		}

		/**
		 * Returns a list of symbol groups.
		 *
		 * @return a list of symbol groups
		 */
		public List<SGElementGroupSymbol> getSymbolGroups() {
			List<SGElementGroupSymbol> retList = new ArrayList<SGElementGroupSymbol>();
			List<SGElementGroup> list = SGUtilityForFigureElement.getGroups(
					SGElementGroupSymbol.class, this.mDrawingElementGroupList);
			for (int ii = 0; ii < list.size(); ii++) {
				retList.add((SGElementGroupSymbol) list.get(ii));
			}
			return retList;
		}

		/**
		 * Returns a list of bar groups.
		 *
		 * @return a list of bar groups
		 */
		public List<SGElementGroupBar> getBarGroups() {
			List<SGElementGroupBar> retList = new ArrayList<SGElementGroupBar>();
			List<SGElementGroup> list = SGUtilityForFigureElement.getGroups(
					SGElementGroupBar.class, this.mDrawingElementGroupList);
			for (int ii = 0; ii < list.size(); ii++) {
				retList.add((SGElementGroupBar) list.get(ii));
			}
			return retList;
		}

		/**
		 * Returns a list of error bar groups.
		 *
		 * @return a list of error bar groups
		 */
		public List<SGElementGroupErrorBar> getErrorBarGroups() {
			List<SGElementGroupErrorBar> retList = new ArrayList<SGElementGroupErrorBar>();
			List<SGElementGroup> list = SGUtilityForFigureElement.getGroups(
					SGElementGroupErrorBar.class, this.mDrawingElementGroupList);
			for (int ii = 0; ii < list.size(); ii++) {
				retList.add((SGElementGroupErrorBar) list.get(ii));
			}
			return retList;
		}

		/**
		 * Returns a list of tick label groups.
		 *
		 * @return a list of tick label groups
		 */
		public List<SGElementGroupTickLabel> getTickLabelGroups() {
			List<SGElementGroupTickLabel> retList = new ArrayList<SGElementGroupTickLabel>();
			List<SGElementGroup> list = SGUtilityForFigureElement.getGroups(
					SGElementGroupTickLabel.class, this.mDrawingElementGroupList);
			for (int ii = 0; ii < list.size(); ii++) {
				retList.add((SGElementGroupTickLabel) list.get(ii));
			}
			return retList;
		}

		/**
		 * Returns a line group which is the first element of an array.
		 *
		 * @return the first element of an array of line groups, or null when this
		 *         group set does not have any line groups
		 */
		public SGElementGroupLine getLineGroup() {
			return (SGElementGroupLine) SGUtilityForFigureElement.getGroup(
					SGElementGroupLine.class, this.mDrawingElementGroupList);
		}

		/**
		 * Returns a symbol group which is the first element of an array.
		 *
		 * @return the first element of an array of symbol groups, or null when this
		 *         group set does not have any symbol groups
		 */
		public SGElementGroupSymbol getSymbolGroup() {
			return (SGElementGroupSymbol) SGUtilityForFigureElement.getGroup(
					SGElementGroupSymbol.class, this.mDrawingElementGroupList);
		}

		/**
		 * Returns a bar group which is the first element of an array.
		 *
		 * @return the first element of an array of bar groups, or null when this
		 *         group set does not have any bar groups
		 */
		public SGElementGroupBar getBarGroup() {
			return (SGElementGroupBar) SGUtilityForFigureElement.getGroup(
					SGElementGroupBar.class, this.mDrawingElementGroupList);
		}

		/**
		 * Returns an error bar group which is the first element of an array.
		 *
		 * @return the first element of an array of error bar groups, or null when
		 *         this group set does not have any error bar groups
		 */
		public SGElementGroupErrorBar getErrorBarGroup() {
			return (SGElementGroupErrorBar) SGUtilityForFigureElement
					.getGroup(SGElementGroupErrorBar.class,
							this.mDrawingElementGroupList);
		}

		/**
		 * Returns a tick label group which is the first element of an array.
		 *
		 * @return the first element of an array of tick label groups, or null when
		 *         this group set does not have any tick label groups
		 */
		public SGElementGroupTickLabel getTickLabelGroup() {
			return (SGElementGroupTickLabel) SGUtilityForFigureElement
					.getGroup(SGElementGroupTickLabel.class,
							this.mDrawingElementGroupList);
		}

		// Line

		public boolean isLineVisible() {
			return this.getLineGroup().isVisible();
		}

		public float getLineWidth() {
			return this.getLineGroup().getLineWidth();
		}

		public float getLineWidth(final String unit) {
			return (float) SGUtilityText.convertFromPoint(this.getLineWidth(),
					unit);
		}

		public int getLineType() {
			return this.getLineGroup().getLineType();
		}

		public Color getLineColor() {
			return this.getLineGroup().getColor();
		}

		/**
		 * Returns whether the lines connect all effective points.
		 *
		 * @return true if connecting all effective points
		 */
		public boolean isLineConnectingAll() {
			return this.getLineGroup().isLineConnectingAll();
		}

        public double getShiftX() {
            return this.mShiftX;
        }

        public double getShiftY() {
        	return this.mShiftY;
        }

        public boolean setShiftX(final double shift) {
        	this.mShiftX = shift;
        	return true;
        }

        public boolean setShiftY(final double shift) {
        	this.mShiftY = shift;
        	return true;
        }

		public boolean setLineVisible(final boolean b) {
			this.getLineGroup().setVisible(b);
			return true;
		}

		public boolean setLineWidth(final float width) {
			return this.getLineGroup().setLineWidth(width);
		}

		public boolean setLineWidth(final float width, final String unit) {
			return this.setLineWidth((float) SGUtilityText.convertToPoint(
					width, unit));
		}

		public boolean setLineType(final int type) {
			return this.getLineGroup().setLineType(type);
		}

		public boolean setLineColor(final Color cl) {
			return this.getLineGroup().setColor(cl);
		}

		/**
		 * Sets whether the lines connect all effective points.
		 *
		 * @return true if succeeded
		 */
		public boolean setLineConnectingAll(final boolean b) {
			return this.getLineGroup().setLineConnectingAll(b);
		}

		// Symbol

		public boolean isSymbolVisible() {
			return this.getSymbolGroup().isVisible();
		}

		public int getSymbolType() {
			return this.getSymbolGroup().getType();
		}

		public float getSymbolSize() {
			return this.getSymbolGroup().getSize();
		}

		public float getSymbolSize(final String unit) {
			return (float) SGUtilityText.convertFromPoint(this.getSymbolSize(),
					unit);
		}

		public float getSymbolLineWidth() {
			return this.getSymbolGroup().getLineWidth();
		}

		public float getSymbolLineWidth(final String unit) {
			return (float) SGUtilityText.convertFromPoint(this
					.getSymbolLineWidth(), unit);
		}

		public SGIPaint getSymbolInnerPaint() {
			return this.getSymbolGroup().getInnerPaint();
		}

		public Color getSymbolLineColor() {
			return this.getSymbolGroup().getLineColor();
		}

        public boolean isSymbolLineVisible() {
            return this.getSymbolGroup().isLineVisible();
        }

		public boolean setSymbolVisible(final boolean b) {
			this.getSymbolGroup().setVisible(b);
			return true;
		}

		public boolean setSymbolType(final int type) {
			return this.getSymbolGroup().setType(type);
		}

		public boolean setSymbolSize(final float size) {
			return this.getSymbolGroup().setSize(size);
		}

		public boolean setSymbolSize(final float size, final String unit) {
			return this.setSymbolSize((float) SGUtilityText.convertToPoint(
					size, unit));
		}

		public boolean setSymbolLineWidth(final float width) {
			return this.getSymbolGroup().setLineWidth(width);
		}

		public boolean setSymbolLineWidth(final float width, final String unit) {
			return this.setSymbolLineWidth((float) SGUtilityText
					.convertToPoint(width, unit));
		}

		public boolean setSymbolInnerPaint(final SGIPaint paint) {
			return this.getSymbolGroup().setInnerPaint(paint);
		}

		public boolean setSymbolLineColor(final Color cl) {
			return this.getSymbolGroup().setLineColor(cl);
		}

        public boolean setSymbolLineVisible(boolean b) {
            return this.getSymbolGroup().setLineVisible(b);
        }

		// Bar

		public boolean isBarVisible() {
			return this.getBarGroup().isVisible();
		}

		public double getBarBaselineValue() {
			return this.getBarGroup().getBaselineValue();
		}

		public float getBarWidth() {
			return this.getBarGroup().getRectangleWidth();
		}

		public double getBarWidthValue() {
			return this.getBarGroup().getWidthValue();
		}

		public float getBarEdgeLineWidth() {
			return this.getBarGroup().getEdgeLineWidth();
		}

		public float getBarEdgeLineWidth(final String unit) {
			return (float) SGUtilityText.convertFromPoint(this
					.getBarEdgeLineWidth(), unit);
		}

		public SGIPaint getBarInnerPaint() {
		    return this.getBarGroup().getInnerPaint();
		}

		public Color getBarEdgeLineColor() {
			return this.getBarGroup().getEdgeLineColor();
		}

        public boolean isBarEdgeLineVisible() {
            return this.getBarGroup().isEdgeLineVisible();
        }

		public boolean isBarVertical() {
			return this.getBarGroup().isVertical();
		}

        public double getBarOffsetX() {
            return this.getBarGroup().getOffsetX();
        }

        public double getBarOffsetY() {
                return this.getBarGroup().getOffsetY();
        }

        public double getBarInterval() {
            return this.getBarGroup().getInterval();
        }

		public boolean setBarVisible(final boolean b) {
			this.getBarGroup().setVisible(b);
			return true;
		}

		public boolean setBarBaselineValue(final double value) {
			return this.getBarGroup().setBaselineValue(value);
		}

		public boolean setBarWidth(final float width) {
			return this.getBarGroup().setRectangleWidth(width);
		}

		public boolean setBarWidthValue(final double value) {
			return this.getBarGroup().setWidthValue(value);
		}

		public boolean setBarEdgeLineWidth(final float width) {
			return this.getBarGroup().setEdgeLineWidth(width);
		}

		public boolean setBarEdgeLineWidth(final float width, final String unit) {
			return this.setBarEdgeLineWidth((float) SGUtilityText
					.convertToPoint(width, unit));
		}

		public boolean setBarInnerPaint(final SGIPaint paint) {
		    return this.getBarGroup().setInnerPaint(paint);
		}

		public boolean setBarEdgeLineColor(final Color cl) {
			return this.getBarGroup().setEdgeLineColor(cl);
		}

        public boolean setBarEdgeLineVisible(boolean b) {
            return this.getBarGroup().setEdgeLineVisible(b);
        }

		public boolean setBarVertical(boolean b) {
			return this.getBarGroup().setVertical(b);
		}

		public boolean hasValidBaselineValue(final int config,
				final Number value) {
			final SGAxis axis = (config == -1) ? this.mYAxis
					: SGFigureElementLegend.this.mAxisElement
							.getAxisInPlane(config);
			final double v = (value != null) ? value.doubleValue() : this
					.getBarBaselineValue();
			return axis.isValidValue(v);
		}

        public boolean setBarOffsetX(final double shift) {
            return this.getBarGroup().setOffsetX(shift);
        }

        public boolean setBarOffsetY(final double shift) {
            return this.getBarGroup().setOffsetY(shift);
        }

        public boolean setBarInterval(final double interval) {
            return this.getBarGroup().setInterval(interval);
        }

		// Error Bar
		public boolean isErrorBarAvailable() {
			if (this.mData instanceof SGISXYTypeData) {
				SGISXYTypeData data = (SGISXYTypeData) this.mData;
				return data.isErrorBarAvailable();
			} else {
				return false;
			}
		}

		public boolean isErrorBarVisible() {
			return this.getErrorBarGroup().isVisible();
		}

		public boolean isErrorBarVertical() {
			return this.getErrorBarGroup().isVertical();
		}

		public int getErrorBarHeadType() {
			return this.getErrorBarGroup().getHeadType();
		}

		public float getErrorBarHeadSize() {
			return this.getErrorBarGroup().getHeadSize();
		}

		public float getErrorBarHeadSize(final String unit) {
			return (float) SGUtilityText.convertFromPoint(this
					.getErrorBarHeadSize(), unit);
		}

		public Color getErrorBarColor() {
			return this.getErrorBarGroup().getColor();
		}

		public float getErrorBarLineWidth() {
			return this.getErrorBarGroup().getLineWidth();
		}

		public float getErrorBarLineWidth(final String unit) {
			return (float) SGUtilityText.convertFromPoint(this
					.getErrorBarLineWidth(), unit);
		}

		public int getErrorBarStyle() {
			return this.getErrorBarGroup().getErrorBarStyle();
		}

        public boolean isErrorBarOnLinePosition() {
            return this.getErrorBarGroup().isPositionOnLine();
        }

		public boolean setErrorBarVisible(final boolean b) {
			this.getErrorBarGroup().setVisible(b);
			return true;
		}

		public boolean setErrorBarHeadType(final int type) {
			this.getErrorBarGroup().setHeadType(type);
			return true;
		}

		public boolean setErrorBarHeadSize(final float size) {
			this.getErrorBarGroup().setHeadSize(size);
			return true;
		}

		public boolean setErrorBarHeadSize(final float size, final String unit) {
			return this.setErrorBarHeadSize((float) SGUtilityText
					.convertToPoint(size, unit));
		}

		public boolean setErrorBarColor(final Color cl) {
			return this.getErrorBarGroup().setColor(cl);
		}

		public boolean setErrorBarLineWidth(final float width) {
			this.getErrorBarGroup().setLineWidth(width);
			return true;
		}

		public boolean setErrorBarLineWidth(final float width, final String unit) {
			return this.setErrorBarLineWidth((float) SGUtilityText
					.convertToPoint(width, unit));
		}

		public boolean setErrorBarStyle(final int style) {
			this.getErrorBarGroup().setErrorBarStyle(style);
			return true;
		}

		public boolean setErrorBarVertical(final boolean b) {
			return this.getErrorBarGroup().setVertical(b);
		}

        @Override
        public boolean setErrorBarOnLinePosition(boolean b) {
            return this.getErrorBarGroup().setPositionOnLine(b);
        }

		// Tick Label
		public boolean isTickLabelAvailable() {
			if (this.mData instanceof SGISXYTypeData) {
				SGISXYTypeData data = (SGISXYTypeData) this.mData;
				return data.isTickLabelAvailable();
			} else {
				return false;
			}
		}

		public boolean isTickLabelVisible() {
			return this.getTickLabelGroup().isVisible();
		}

		public String getTickLabelFontName() {
			return this.getTickLabelGroup().getFontName();
		}

		public int getTickLabelFontStyle() {
			return this.getTickLabelGroup().getFontStyle();
		}

		public float getTickLabelFontSize() {
			return this.getTickLabelGroup().getFontSize();
		}

		public float getTickLabelFontSize(final String unit) {
			return (float) SGUtilityText.convertFromPoint(this
					.getTickLabelFontSize(), unit);
		}

		public Color getTickLabelColor() {
			return this.getTickLabelGroup().getColor();
		}

		public float getTickLabelAngle() {
		    return this.getTickLabelGroup().getAngle();
		}

		public boolean hasTickLabelHorizontalAlignment() {
			return this.getTickLabelGroup().hasHorizontalAlignment();
		}

		public int getTickLabelDecimalPlaces() {
			return this.getTickLabelGroup().getDecimalPlaces();
		}

		public int getTickLabelExponent() {
			return this.getTickLabelGroup().getExponent();
		}

		public String getTickLabelDateFormat() {
			return this.getTickLabelGroup().getDateFormat();
		}

		public boolean setTickLabelVisible(final boolean b) {
			this.getTickLabelGroup().setVisible(b);
			return true;
		}

		public boolean setTickLabelFontName(final String name) {
			return this.getTickLabelGroup().setFontName(name);
		}

		public boolean setTickLabelFontStyle(final int style) {
			return this.getTickLabelGroup().setFontStyle(style);
		}

		public boolean setTickLabelFontSize(final float size) {
			return this.getTickLabelGroup().setFontSize(size);
		}

		public boolean setTickLabelFontSize(final float size, final String unit) {
			return this.setTickLabelFontSize((float) SGUtilityText
					.convertToPoint(size, unit));
		}

		public boolean setTickLabelColor(final Color cl) {
			return this.getTickLabelGroup().setColor(cl);
		}

		public boolean setTickLabelAngle(final float angle) {
		    return this.getTickLabelGroup().setAngle(angle);
		}

		public boolean setTickLabelHorizontalAlignment(final boolean b) {
			return this.getTickLabelGroup().setHorizontalAlignment(b);
		}

		public boolean setTickLabelDecimalPlaces(final int dp) {
			this.getTickLabelGroup().setDecimalPlaces(dp);
			return true;
		}

		public boolean setTickLabelExponent(final int exp) {
			this.getTickLabelGroup().setExponent(exp);
			return true;
		}

		/**
		 * Update the text strings of tick labels.
		 */
		public void updateTickLabelStrings() {
			// do nothing
		}

		//
		boolean onMouseClicked(final MouseEvent e) {
			SGElementGroup group = this.getElementGroupAt(e.getX(), e.getY());

			ELEMENT_TYPE type = ELEMENT_TYPE.Void;
			if (group instanceof ElementGroupLine) {
				type = ELEMENT_TYPE.Line;
			} else if (group instanceof ElementGroupSymbol) {
				type = ELEMENT_TYPE.Symbol;
			} else if (group instanceof ElementGroupBar) {
				type = ELEMENT_TYPE.Bar;
			} else if (group instanceof ElementGroupErrorBar) {
				type = ELEMENT_TYPE.ErrorBar;
			} else if (group instanceof ElementGroupTickLabels) {
				type = ELEMENT_TYPE.TickLabel;
			}
			this.mSelectedGroupType = type;

			return true;
		}

		/**
		 *
		 */
		public boolean addDrawingElementGroup(final int type) {

			SGISXYTypeData data = (SGISXYTypeData) this.mData;
			SGElementGroup group = null;
			if (type == SGIElementGroupConstants.POLYLINE_GROUP) {
				group = new ElementGroupLine(data);
			} else if (type == SGIElementGroupConstants.RECTANGLE_GROUP) {
				group = new ElementGroupBar(data);
			} else if (type == SGIElementGroupConstants.SYMBOL_GROUP) {
				group = new ElementGroupSymbol(data);
			} else if (type == SGIElementGroupConstants.ERROR_BAR_GROUP) {
				group = new ElementGroupErrorBar(data);
			} else if (type == SGIElementGroupConstants.TICK_LABEL_GROUP) {
				group = new ElementGroupTickLabels(data);
			} else {
				throw new Error();
			}

			this.addDrawingElementGroup(group);

			return true;
		}

		/**
		 *
		 */
		public int getXAxisLocation() {
			return SGFigureElementLegend.this.mAxisElement
					.getLocationInPlane(this.getXAxis());
		}

		/**
		 *
		 */
		public int getYAxisLocation() {
			return SGFigureElementLegend.this.mAxisElement
					.getLocationInPlane(this.getYAxis());
		}

		/**
		 *
		 */
		public boolean setXAxisLocation(final int location) {
	        if (location != SGIFigureElementAxis.AXIS_HORIZONTAL_1
	                && location != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
	            return false;
	        }
			this.mXAxis = this.getAxis(location);
			return true;
		}

		/**
		 *
		 */
		public boolean setYAxisLocation(final int location) {
	        if (location != SGIFigureElementAxis.AXIS_VERTICAL_1
	                && location != SGIFigureElementAxis.AXIS_VERTICAL_2) {
	            return false;
	        }
			this.mYAxis = this.getAxis(location);
			return true;
		}

		public SGElementGroupErrorBar createErrorBars(SGISXYTypeSingleData dataXY) {
			if (this
					.addDrawingElementGroup(SGIElementGroupConstants.ERROR_BAR_GROUP) == false) {
				return null;
			}
			return this.getErrorBarGroup();
		}

		public SGElementGroupTickLabel createTickLabels(SGISXYTypeSingleData dataXY) {
			if (this
					.addDrawingElementGroup(SGIElementGroupConstants.TICK_LABEL_GROUP) == false) {
				return null;
			}
			return this.getTickLabelGroup();
		}

	    /**
	     * Sets the properties of element groups.
	     *
	     * @param elementGroupPropertiesList
	     * @return true if succeeded
	     *
	     */
	    protected boolean setElementGroupProperties(List elementGroupPropertiesList) {
	        for (int ii = 0; ii < elementGroupPropertiesList.size(); ii++) {
	            SGProperties gp = (SGProperties) elementGroupPropertiesList.get(ii);
	            SGElementGroup group = null;
	            if (gp instanceof SGElementGroupLine.LineProperties) {
	                group = this.getLineGroup();
	            } else if (gp instanceof SGElementGroupSymbol.SymbolProperties) {
	                group = this.getSymbolGroup();
	            } else if (gp instanceof SGElementGroupBar.BarProperties) {
	                group = this.getBarGroup();
	            } else if (gp instanceof SGElementGroupErrorBar.ErrorBarProperties) {
	                group = this.getErrorBarGroup();
	            } else if (gp instanceof SGElementGroupString.StringProperties) {
	                group = this.getTickLabelGroup();
	            } else {
	                throw new Error("Illegal group property: " + gp);
	            }
	            if (group == null) {
	                continue;
	            }
	            if (group.setProperties(gp) == false) {
	                return false;
	            }
	        }
	        return true;
	    }

		/**
		 * Sets the direction of error bars.
		 *
		 * @param vertical
		 *           true to set vertical
		 * @return true if succeeded
		 */
		public boolean setErrorBarDirection(final boolean vertical) {
			return this.setErrorBarVertical(vertical);
		}

		/**
		 * Sets the alignment of tick label.
		 *
		 * @param horizontal
		 *           true to align horizontally
		 * @return true if succeeded
		 */
		public boolean setTickLabelAlignment(final boolean horizontal) {
			return this.setTickLabelHorizontalAlignment(horizontal);
		}

	    /**
	     * Sets the information of picked up dimension.
	     *
	     * @param info
	     *           the information of picked up dimension
	     * @return true if succeeded
	     */
		public boolean setPickUpDimensionInfo(SGPickUpDimensionInfo info) {
	        SGData data = this.getData();
	        if (data instanceof SGISXYMultipleDimensionData) {
	        	SGISXYMultipleDimensionData nData = (SGISXYMultipleDimensionData) data;
	            return nData.setPickUpDimensionInfo(info);
	        } else {
	            return false;
	        }
		}

	    /**
	     * Sets the stride.
	     *
	     * @param stride
	     *           stride of arrays
	     * @return true if succeeded
	     */
	    public boolean setStride(SGIntegerSeriesSet stride) {
			return this.setSXYStrideToData(stride);
	    }

		@Override
		public boolean setIndexStride(SGIntegerSeriesSet stride) {
			return this.setIndexStrideToData(stride);
		}

	    /**
	     * Sets the stride for single dimensional data.
	     *
	     * @param stride
	     *           stride of arrays
	     * @return true if succeeded
	     */
		@Override
	    public boolean setSDArrayStride(SGIntegerSeriesSet stride) {
			return this.setSDArrayStrideToData(stride);
		}

		public boolean setTickLabelStride(SGIntegerSeriesSet stride) {
			return this.setTickLabelStrideToData(stride);
		}

	    /**
	     * Sets the column types and related parameters.
	     *
	     * @param map
	     *           property map
	     * @param result
	     *           results of setting properties
	     * @param cols
	     *           an array of data columns
	     * @return true if succeeded
	     */
		@Override
	    protected boolean setProperties(SGPropertyMap map, SGPropertyResults result,
	    		SGDataColumnInfo[] cols) {
			// do nothing
			return true;
		}

		private boolean mLineColorAutoAssigned = false;

	    /**
	     * Returns whether line color is automatically assigned.
	     * 
	     * @return true if line color is automatically assigned
	     */
		@Override
	    public boolean isLineColorAutoAssigned() {
			return this.mLineColorAutoAssigned;
		}

	    /**
	     * Sets whether line color is automatically assigned
	     * 
	     * @param b
	     *           a flag whether line color is automatically assigned
	     */
		@Override
	    public void setLineColorAutoAssigned(final boolean b) {
			this.mLineColorAutoAssigned = b;
		}
		
	    @Override
	    public SGProperties getProperties() {
	    	SXYElementGroupSetPropertiesInFigureElement p = new SXYElementGroupSetPropertiesInFigureElement();
	        if (this.getProperties(p) == false) {
	            return null;
	        }
	        return p;
	    }

	    @Override
	    public boolean getProperties(SGProperties p) {
	        if ((p instanceof SXYElementGroupSetPropertiesInFigureElement) == false) {
	            return false;
	        }
	        if (super.getProperties(p) == false) {
	            return false;
	        }
	        SXYElementGroupSetPropertiesInFigureElement mp = (SXYElementGroupSetPropertiesInFigureElement) p;
	        mp.shiftX = this.getShiftX();
	        mp.shiftY = this.getShiftY();
	        mp.lineColorAutoAssigned = this.isLineColorAutoAssigned();
	        mp.lineColorMapName = this.getLineColorMapName();
	        return true;
	    }

	    @Override
	    public boolean setProperties(SGProperties p) {
	        if ((p instanceof SXYElementGroupSetPropertiesInFigureElement) == false) {
	            return false;
	        }
	        if (super.setProperties(p) == false) {
	            return false;
	        }

	        SXYElementGroupSetPropertiesInFigureElement mp = (SXYElementGroupSetPropertiesInFigureElement) p;
	        this.setShiftX(mp.shiftX);
	        this.setShiftY(mp.shiftY);
	        this.setLineColorAutoAssigned(mp.lineColorAutoAssigned);
	        this.setLineColorMapName(mp.lineColorMapName);
	        return true;
	    }

		@Override
		public Map<String, SGProperties> getElementGroupPropertiesMap() {
			return SGUtilityForFigureElementJava2D.getElementGroupPropertiesMap(this);
		}

		@Override
		public boolean setElementGroupPropertiesMap(Map<String, SGProperties> pMap) {
			return SGUtilityForFigureElementJava2D.setElementGroupPropertiesMap(this, pMap);
		}
	}

	/**
	 * A class of multiple element group set in legend.
	 *
	 */
	private class ElementGroupSetInLegendMultipleSXY extends
			ElementGroupSetInLegendSXY implements
			SGIElementGroupSetMultipleSXY, SGISXYDataDialogObserver,
			SGISXYDataConstants {

        @Override
        public boolean setShiftX(final double shift) {
        	if (super.setShiftX(shift) == false) {
        		return false;
        	}
        	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
        		ElementGroupSetInLegendSXY legend = (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
        		if (legend.setShiftX(shift) == false) {
        			return false;
        		}
        	}
        	return true;
        }

        @Override
        public boolean setShiftY(final double shift) {
        	if (super.setShiftY(shift) == false) {
        		return false;
        	}
        	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
        		ElementGroupSetInLegendSXY legend = (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
        		if (legend.setShiftY(shift) == false) {
        			return false;
        		}
        	}
        	return true;
        }

	    /**
	     * Color map manager for lines.
	     */
	    private SGColorMapManager mLineColorMapManager = new SGLineStyleColorMapManager();

	    /**
	     * The color map for lines.
	     */
	    private String mLineColorMapName = SGLineStyleColorMapManager.COLOR_MAP_NAME_HUE_GRADATION;

        /**
         * Returns the map of line style.
         *
         * @return the map of line style
         */
        @Override
        public Map<Integer, SGLineStyle> getLineStyleMap() {
        	Map<Integer, SGLineStyle> lineStyleMap = new TreeMap<Integer, SGLineStyle>();
        	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
        		ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
        		lineStyleMap.put(gs.getSeriesIndex(), gs.getLineStyle());
        	}
        	return lineStyleMap;
        }

        /**
         * Sets the line style to the child data object.
         *
         * @param style
         *          the line style to set
         * @param index
         *          array index of child data object
         * @return true if succeeded
         */
    	@Override
        public boolean setLineStyle(final SGLineStyle style, final int index) {
    		for (SGIElementGroupSetForData gs: this.mElementGroupSetList) {
    			ElementGroupSetInLegendSXY gsxy = (ElementGroupSetInLegendSXY) gs;
    			if (gsxy.getSeriesIndex() == index) {
    	    		gsxy.setLineStyle(style);
    	    		break;
    			}
    		}

            // set to line group of this group set
    		ElementGroupLine lineGroup = (ElementGroupLine) this.getLineGroup();
    		lineGroup.setLineStyle(style, index);
    		
    		return true;
    	}

        /**
         * Sets the line styles to the child data object.
         * 
         * @param styleList
         *           list of line styles
         * @return true if succeeded
         */
    	@Override
        public boolean setLineStyle(final List<SGLineStyle> styleList) {
    		if (styleList.size() != this.getChildNumber()) {
    			return false;
    		}
    		for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
    			ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
    			SGLineStyle style = styleList.get(ii);
    			gs.setLineStyle(style);
    		}
    		ElementGroupLine groupLine = (ElementGroupLine) this.getLineGroup();
    		groupLine.setLineStyleList(styleList);
    		
        	return true;
        }

		/**
		 * The list of element group set.
		 */
		protected ArrayList<SGIElementGroupSetForData> mElementGroupSetList = new ArrayList<SGIElementGroupSetForData>();

	    /**
	     * Adds a group set.
	     *
	     * @param gs
	     *           the group set to be add
	     */
		public void addChildGroupSet(SGIElementGroupSetForData gs) {
			this.mElementGroupSetList.add(gs);
		}

	    /**
	     * Removes a group set.
	     *
	     * @param gs
	     *           the group set to be removed
	     */
	    public void removeChildGroupSet(SGIElementGroupSetForData gs) {
	        this.mElementGroupSetList.remove(gs);
	    }

	    @Override
		public SGIElementGroupSetForData[] getChildGroupSetArray() {
			SGIElementGroupSetForData[] array = new SGIElementGroupSetForData[this.mElementGroupSetList
					.size()];
			this.mElementGroupSetList.toArray(array);
			return array;
		}

	    /**
	     * Returns the number of child data objects.
	     * 
	     * @return the number of child data objects
	     */
	    @Override
	    public int getChildNumber() {
	    	return this.mElementGroupSetList.size();
	    }

	    /**
	     * Returns the list of child objects.
	     * 
	     * @return the list of child objects
	     */
		@Override
	    public List<String> getChildNameList() {
			SGISXYTypeMultipleData data = (SGISXYTypeMultipleData) this.mData;
			return data.getChildNameList();
		}

		/**
		 * Builds the legend object with a given data object.
		 * @param data
		 *            a data object
		 */
		protected ElementGroupSetInLegendMultipleSXY(SGData data) {
			super(data);
		}

	    /**
	     * Disposes this object.
	     *
	     */
	    public void dispose() {
	        super.dispose();
	        for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
	        	ElementGroupSetInLegend gs = (ElementGroupSetInLegend) this.mElementGroupSetList.get(ii);
	        	SGData data = gs.mData;
	        	if (data != null) {
	        		data.dispose();
	        	}
	        }
	    }

		/**
		 * Returns the first element group set.
		 * @return
		 *         the first element group set
		 */
		public SGIElementGroupSetForData getFirst() {
			if (this.mElementGroupSetList.size() == 0) {
				return null;
			} else {
				return (SGElementGroupSetForData) this.mElementGroupSetList
						.get(0);
			}
		}

		public SGProperties getProperties() {
			MultipleSXYElementGroupSetPropertiesInFigureElement p = new MultipleSXYElementGroupSetPropertiesInFigureElement();
			if (this.getProperties(p) == false) {
				return null;
			}
			return p;
		}

		public boolean getProperties(SGProperties p) {
			if ((p instanceof MultipleSXYElementGroupSetPropertiesInFigureElement) == false) {
				return false;
			}
			if (super.getProperties(p) == false) {
				return false;
			}
			MultipleSXYElementGroupSetPropertiesInFigureElement mp = (MultipleSXYElementGroupSetPropertiesInFigureElement) p;
	        mp.shiftX = this.mShiftX;
	        mp.shiftY = this.mShiftY;
	        mp.lineColorAutoAssigned = this.isLineColorAutoAssigned();
	        mp.lineColorMapName = this.getLineColorMapName();
	        for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
	        	ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
	        	mp.childPropertyList.add(gs.getProperties());
	        	mp.childLineStyleList.add(gs.getLineStyle());
	        }
	        Map<String, SGColorMap> colorMaps = this.mLineColorMapManager.getColorMaps();
	        Iterator<Entry<String, SGColorMap>> itr = colorMaps.entrySet().iterator();
	        while (itr.hasNext()) {
	        	Entry<String, SGColorMap> entry = itr.next();
	        	String name = entry.getKey();
	            SGColorMap colorMap = entry.getValue();
	            mp.colorMapPropertiesMap.put(name, (ColorMapProperties) colorMap.getProperties());
	        }
			return true;
		}

		public boolean setProperties(SGProperties p) {
			if ((p instanceof MultipleSXYElementGroupSetPropertiesInFigureElement) == false) {
				return false;
			}
			if (super.setProperties(p) == false) {
				return false;
			}

			// disposes of old data
			if (SGMultipleSXYUtility.removeAllChildData(this) == false) {
			    return false;
			}

			// synchronize the child group set
			if (SGFigureElementLegend.this.updateChildGroupSet(this) == false) {
				return false;
			}

			MultipleSXYElementGroupSetPropertiesInFigureElement mp = (MultipleSXYElementGroupSetPropertiesInFigureElement) p;
			this.mShiftX = mp.shiftX;
			this.mShiftY = mp.shiftY;
	        this.setLineColorAutoAssigned(mp.lineColorAutoAssigned);
	        this.setLineColorMapName(mp.lineColorMapName);

	        Iterator<Entry<String, ColorMapProperties>> itr = mp.colorMapPropertiesMap.entrySet().iterator();
	        while (itr.hasNext()) {
	        	Entry<String, ColorMapProperties> entry = itr.next();
	        	String name = entry.getKey();
	        	ColorMapProperties colorMapProperties = entry.getValue();
	        	SGColorMap colorMap = this.mLineColorMapManager.getColorMap(name);
	        	if (!colorMap.setProperties(colorMapProperties)) {
	        		return false;
	        	}
	        }

			/* When merging data, mChildPropertyLit of MultipleGroupSet property has
			 * one child only (which property is the last merging split data).
			 */
			int count = 0;
            for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
                SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
                gs.setMagnification(this.mMagnification);
                if (gs.setProperties(mp.childPropertyList.get(count))==false) {
                    return false;
                }
                count++;
                if (count >= mp.childPropertyList.size()) {
                    count = mp.childPropertyList.size() - 1;
                }
            }
            ElementGroupLine lineGroup = (ElementGroupLine) this.getLineGroup();
            lineGroup.clearLineStyleList();
            count = 0;
            for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
            	ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
            	SGLineStyle lineStyle = mp.childLineStyleList.get(count);
                gs.setLineStyle(lineStyle);
                count++;
                if (count >= mp.childLineStyleList.size()) {
                    count = mp.childLineStyleList.size() - 1;
                }
                if (count < mp.childLineStyleList.size()) {
                    lineGroup.addLineStyle(lineStyle);
                }
            }
			return true;
		}

		/**
		 * Sets the x-axis.
		 *
		 * @param axis
		 *           the x-axis
		 * @return true if succeeded
		 */
		public boolean setXAxis(final SGAxis axis) {
			if (super.setXAxis(axis) == false) {
				return false;
			}
			for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
				SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
				if (gs.setXAxis(axis) == false) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Sets the y-axis.
		 *
		 * @param axis
		 *           the y-axis
		 * @return true if succeeded
		 */
		public boolean setYAxis(final SGAxis axis) {
			if (super.setYAxis(axis) == false) {
				return false;
			}
			for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
				SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
				if (gs.setYAxis(axis) == false) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Sets the x-axis location.
		 *
		 * @param location
		 *           the location of the x-axis
		 * @return true if succeeded
		 */
		public boolean setXAxisLocation(final int location) {
			if (super.setXAxisLocation(location) == false) {
				return false;
			}
			for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
				SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
				if (gs.setXAxisLocation(location) == false) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Sets the y-axis location.
		 *
		 * @param location
		 *           the location of the y-axis
		 * @return true if succeeded
		 */
		public boolean setYAxisLocation(final int location) {
			if (super.setYAxisLocation(location) == false) {
				return false;
			}
			for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
				SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
				if (gs.setYAxisLocation(location) == false) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Sets the name to this group set.
		 *
		 * @param name
		 *            the name to set to this group set
		 * @return true if succeeded
		 */
		public boolean setName(final String name) {
			if (super.setName(name) == false) {
				return false;
			}
			for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
				SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
				if (gs.setName(name) == false) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Sets the flag whether this data is visible in the legend
		 *
		 * @param b
		 *            true to set visible
		 * @return true if succeeded
		 */
		public boolean setVisibleInLegend(final boolean b) {
			if (super.setVisibleInLegend(b) == false) {
				return false;
			}
			for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
				SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
				if (gs.setVisibleInLegend(b) == false) {
					return false;
				}
			}
			return true;
		}

		public SGElementGroupErrorBar createErrorBars(SGISXYTypeMultipleData dataXY) {
			if (this
					.addDrawingElementGroup(SGIElementGroupConstants.ERROR_BAR_GROUP) == false) {
				return null;
			}
			return this.getErrorBarGroup();
		}

		public SGElementGroupTickLabel createTickLabels(SGISXYTypeMultipleData dataXY) {
			if (this
					.addDrawingElementGroup(SGIElementGroupConstants.TICK_LABEL_GROUP) == false) {
				return null;
			}
			return this.getTickLabelGroup();
		}

	    public List<SGElementGroupLine> getLineGroups() {
	    	return SGMultipleSXYUtility.getLineGroups(this);
	    }

	    public List<SGElementGroupLine> getLineGroupsIgnoreNull() {
	    	return SGMultipleSXYUtility.getLineGroupsIgnoreNull(this);
	    }

	    @Override
        public boolean setLineVisible(final boolean b) {
            return SGMultipleSXYUtility.setLineVisible(this.getLineGroupsIgnoreNull(), b);
        }

	    @Override
	    public boolean setLineWidth(final float width) {
	    	return SGMultipleSXYUtility.setLineWidth(this.getLineGroupsIgnoreNull(), width);
	    }

	    @Override
	    public boolean setLineWidth(final float width, final String unit) {
	    	return SGMultipleSXYUtility.setLineWidth(this, width, unit);
	    }

	    @Override
	    public boolean setLineType(final int type) {
	    	return SGMultipleSXYUtility.setLineType(this, type);
	    }

	    @Override
	    public boolean setLineColor(final Color cl) {
	    	return SGMultipleSXYUtility.setLineColor(this, cl);
	    }

	    /**
	     * Sets whether the lines connect all effective points.
	     *
	     * @return true if succeeded
	     */
	    @Override
	    public boolean setLineConnectingAll(final boolean b) {
	    	return SGMultipleSXYUtility.setLineConnectingAll(this.getLineGroupsIgnoreNull(), b);
	    }

	    public List<SGElementGroupSymbol> getSymbolGroups() {
	    	return SGMultipleSXYUtility.getSymbolGroups(this);
	    }

	    public List<SGElementGroupSymbol> getSymbolGroupsIgnoreNull() {
	    	return SGMultipleSXYUtility.getSymbolGroupsIgnoreNull(this);
	    }

	    public boolean setSymbolVisible(final boolean b) {
	    	return SGMultipleSXYUtility.setSymbolVisible(this.getSymbolGroupsIgnoreNull(), b);
	    }

	    public boolean setSymbolType(final int type) {
	    	return SGMultipleSXYUtility.setSymbolType(this.getSymbolGroupsIgnoreNull(), type);
	    }

	    public boolean setSymbolSize(final float size) {
	    	return SGMultipleSXYUtility.setSymbolSize(this.getSymbolGroupsIgnoreNull(), size);
	    }

	    public boolean setSymbolSize(final float size, final String unit) {
	    	return SGMultipleSXYUtility.setSymbolSize(this.getSymbolGroupsIgnoreNull(), size, unit);
	    }

	    public boolean setSymbolLineWidth(final float width) {
	    	return SGMultipleSXYUtility.setSymbolLineWidth(this.getSymbolGroupsIgnoreNull(), width);
	    }

	    public boolean setSymbolLineWidth(final float width, final String unit) {
	    	return SGMultipleSXYUtility.setSymbolLineWidth(this.getSymbolGroupsIgnoreNull(), width, unit);
	    }

	    @Override
	    public boolean setSymbolInnerPaint(final SGIPaint paint) {
	    	return SGMultipleSXYUtility.setSymbolInnerPaint(this.getSymbolGroupsIgnoreNull(), paint);
	    }

	    public boolean setSymbolLineColor(final Color cl) {
	    	return SGMultipleSXYUtility.setSymbolLineColor(this.getSymbolGroupsIgnoreNull(), cl);
	    }

        @Override
        public boolean setSymbolLineVisible(boolean b) {
            return SGMultipleSXYUtility.setSymbolLineVisible(this.getSymbolGroupsIgnoreNull(), b);
        }

	    public List<SGElementGroupBar> getBarGroups() {
	    	return SGMultipleSXYUtility.getBarGroups(this);
	    }

	    public List<SGElementGroupBar> getBarGroupsIgnoreNull() {
	    	return SGMultipleSXYUtility.getBarGroupsIgnoreNull(this);
	    }

	    public boolean setBarVisible(final boolean b) {
	    	return SGMultipleSXYUtility.setBarVisible(this.getBarGroupsIgnoreNull(), b);
	    }

	    public boolean setBarVertical(final boolean b) {
	    	return SGMultipleSXYUtility.setBarVertical(this.getBarGroupsIgnoreNull(), b);
	    }

	    public boolean setBarBaselineValue(final double value) {
	    	return SGMultipleSXYUtility.setBarBaselineValue(this.getBarGroupsIgnoreNull(), value);
	    }

	    public boolean setBarWidth(final float width) {
	    	return SGMultipleSXYUtility.setBarWidth(this.getBarGroupsIgnoreNull(), width);
	    }

	    public boolean setBarWidthValue(final double value) {
	    	return SGMultipleSXYUtility.setBarWidthValue(this.getBarGroupsIgnoreNull(), value);
	    }

	    public boolean setBarEdgeLineWidth(final float width) {
	    	return SGMultipleSXYUtility.setBarEdgeLineWidth(this.getBarGroupsIgnoreNull(), width);
	    }

	    public boolean setBarEdgeLineWidth(final float width, final String unit) {
	    	return SGMultipleSXYUtility.setBarEdgeLineWidth(this.getBarGroupsIgnoreNull(), width, unit);
	    }

	    @Override
	    public boolean setBarInnerPaint(final SGIPaint paint) {
	        return SGMultipleSXYUtility.setBarInnerPaint(this.getBarGroupsIgnoreNull(), paint);
	    }

	    @Override
	    public boolean setBarEdgeLineColor(final Color cl) {
	    	return SGMultipleSXYUtility.setBarEdgeLineColor(this.getBarGroupsIgnoreNull(), cl);
	    }

	    @Override
	    public boolean setBarEdgeLineVisible(final boolean visible) {
	        return SGMultipleSXYUtility.setBarEdgeLineVisible(this.getBarGroupsIgnoreNull(), visible);
	    }

            @Override
            public boolean setBarOffsetX(final double shift) {
                return SGMultipleSXYUtility.setBarOffsetX(this.getBarGroupsIgnoreNull(), shift);
            }

            @Override
            public boolean setBarOffsetY(final double shift) {
                return SGMultipleSXYUtility.setBarOffsetY(this.getBarGroupsIgnoreNull(), shift);
            }

            @Override
            public boolean setBarInterval(final double interval) {
                return SGMultipleSXYUtility.setBarInterval(this.getBarGroupsIgnoreNull(), interval);
            }

	    public List<SGElementGroupErrorBar> getErrorBarGroups() {
	    	return SGMultipleSXYUtility.getErrorBarGroups(this);
	    }

	    public List<SGElementGroupErrorBar> getErrorBarGroupsIgnoreNull() {
	    	return SGMultipleSXYUtility.getErrorBarGroupsIgnoreNull(this);
	    }

	    public boolean setErrorBarVisible(final boolean b) {
	    	return SGMultipleSXYUtility.setErrorBarVisible(this.getErrorBarGroupsIgnoreNull(), b);
	    }

	    public boolean setErrorBarVertical(final boolean b) {
	    	return SGMultipleSXYUtility.setErrorBarVertical(this.getErrorBarGroupsIgnoreNull(), b);
	    }

	    public boolean setErrorBarHeadType(final int type) {
	    	return SGMultipleSXYUtility.setErrorBarHeadType(this.getErrorBarGroupsIgnoreNull(), type);
	    }

	    public boolean setErrorBarHeadSize(final float size) {
	    	return SGMultipleSXYUtility.setErrorBarHeadSize(this.getErrorBarGroupsIgnoreNull(), size);
	    }

	    public boolean setErrorBarHeadSize(final float size, final String unit) {
	    	return SGMultipleSXYUtility.setErrorBarHeadSize(this.getErrorBarGroupsIgnoreNull(), size, unit);
	    }

	    public boolean setErrorBarColor(final Color cl) {
	    	return SGMultipleSXYUtility.setErrorBarColor(this.getErrorBarGroupsIgnoreNull(), cl);
	    }

	    public boolean setErrorBarLineWidth(final float width) {
	    	return SGMultipleSXYUtility.setErrorBarLineWidth(this.getErrorBarGroupsIgnoreNull(), width);
	    }

	    public boolean setErrorBarLineWidth(final float width, final String unit) {
	    	return SGMultipleSXYUtility.setErrorBarLineWidth(this.getErrorBarGroupsIgnoreNull(), width, unit);
	    }

	    public boolean setErrorBarStyle(final int style) {
	    	return SGMultipleSXYUtility.setErrorBarStyle(this.getErrorBarGroupsIgnoreNull(), style);
	    }

        @Override
        public boolean setErrorBarOnLinePosition(boolean b) {
            return SGMultipleSXYUtility.setErrorBarOnLinePosition(this.getErrorBarGroupsIgnoreNull(), b);
        }

	    public List<SGElementGroupTickLabel> getTickLabelGroups() {
	    	return SGMultipleSXYUtility.getTickLabelGroups(this);
	    }

	    public List<SGElementGroupTickLabel> getTickLabelGroupsIgnoreNull() {
	    	return SGMultipleSXYUtility.getTickLabelGroupsIgnoreNull(this);
	    }

	    public boolean setTickLabelVisible(final boolean b) {
	        return SGMultipleSXYUtility.setTickLabelVisible(this.getTickLabelGroupsIgnoreNull(), b);
	    }

	    public boolean setTickLabelHorizontalAlignment(final boolean b) {
	        return SGMultipleSXYUtility.setTickLabelHorizontalAlignment(this.getTickLabelGroupsIgnoreNull(), b);
	    }

	    public boolean setTickLabelFontName(final String name) {
	        return SGMultipleSXYUtility.setTickLabelFontName(this.getTickLabelGroupsIgnoreNull(), name);
	    }

	    public boolean setTickLabelFontStyle(final int style) {
	        return SGMultipleSXYUtility.setTickLabelFontStyle(this.getTickLabelGroupsIgnoreNull(), style);
	    }

	    public boolean setTickLabelFontSize(final float size) {
	        return SGMultipleSXYUtility.setTickLabelFontSize(this.getTickLabelGroupsIgnoreNull(), size);
	    }

	    public boolean setTickLabelFontSize(final float size, final String unit) {
	        return SGMultipleSXYUtility.setTickLabelFontSize(this.getTickLabelGroupsIgnoreNull(), size, unit);
	    }

	    public boolean setTickLabelColor(final Color cl) {
	        return SGMultipleSXYUtility.setTickLabelColor(this.getTickLabelGroupsIgnoreNull(), cl);
	    }

	    public boolean setTickLabelAngle(final float angle) {
	        return SGMultipleSXYUtility.setTickLabelAngle(this.getTickLabelGroupsIgnoreNull(), angle);
	    }

	    public boolean setTickLabelDecimalPlaces(final int dp) {
	        return SGMultipleSXYUtility.setTickLabelDecimalPlaces(this.getTickLabelGroupsIgnoreNull(), dp);
	    }

	    public boolean setTickLabelExponent(final int exp) {
	        return SGMultipleSXYUtility.setTickLabelExponent(this.getTickLabelGroupsIgnoreNull(), exp);
	    }

	    /**
	     * Returns whether this group set "contains" the given group set.
	     *
	     * @param gs
	     *           the group set
	     * @return true if this group set "contains" the given group set
	     */
	    public boolean contains(SGElementGroupSetForData gs) {
	    	if (super.contains(gs)) {
	    		return true;
	    	}
	    	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
	    		SGIElementGroupSetForData groupSet = this.mElementGroupSetList.get(ii);
	    		if (groupSet.equals(gs)) {
	    			return true;
	    		}
	    	}
	    	return false;
	    }

	    /**
	     * Sets the information of data columns.
	     * This method is overridden to update the child data objects.
	     *
	     * @param columns
	     *                information of data columns
	     * @return true if succeeded
	     */
	    public boolean setColumnInfo(SGDataColumnInfo[] columns, String message) {
	    	if (!super.setColumnInfo(columns, message)) {
	    		return false;
	    	}
	    	if (SGMultipleSXYUtility.setColumnInfo(this, SGFigureElementLegend.this) == false) {
	    		return false;
	    	}
	    	return true;
	    }

	    /**
	     * Sets the data.
	     *
	     * @param data
	     *            a data object
	     * @return true if succeeded
	     */
	    public boolean setData(SGData data) {
	    	if (super.setData(data) == false) {
	    		return false;
	    	}
	    	if (data == null) {
	        	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
	        		SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);

	        		// disposes of the data of child group set
	        		SGData d = gs.getData();
	        		if (d != null) {
	            		d.dispose();
	        		}

	        		// set null
	        		gs.setData(null);
	        	}
	    	}
	    	return true;
	    }

        /**
         * Updates the line style of child objects.
         *
         * @return true if succeeded
         */
        @Override
        public boolean initChildLineStyle() {
            final int dataNum = this.mElementGroupSetList.size();
            if (dataNum > 1 && this.mLineColorAutoAssigned) {
            	List<SGLineStyle> lineStyleList = new ArrayList<SGLineStyle>();
                HueColorMap model = new HueColorMap();
                for (int ii = 0; ii < dataNum; ii++) {
                	ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
                    Color cl = model.eval((double) ii / (dataNum - 1));
                    SGLineStyle s = new SGLineStyle(DEFAULT_LINE_TYPE, cl, DEFAULT_LINE_WIDTH);
                    gs.setLineStyle(s);
                    lineStyleList.add(s);
                }
                // set to line group of this group set
                ElementGroupLine lineGroup = (ElementGroupLine) this.getLineGroup();
                lineGroup.setLineStyleList(lineStyleList);
            } else {
                for (int ii = 0; ii < dataNum; ii++) {
                	ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
                    SGLineStyle s = new SGLineStyle(DEFAULT_LINE_TYPE, DEFAULT_LINE_COLOR, DEFAULT_LINE_WIDTH);
                    gs.setLineStyle(s);

                    // set to line group of this group set
                    ElementGroupLine lineGroup = (ElementGroupLine) this.getLineGroup();
                    lineGroup.addLineStyle(s);
                }
            }
            return true;
        }
        
    	/**
    	 * Sets the style of drawing elements.
    	 *
    	 * @param styleList
    	 *          the list of style
    	 * @return true if succeeded
    	 */
    	@Override
        public boolean setStyle(List<SGStyle> styleList) {
    		if (styleList.size() != this.mElementGroupSetList.size()) {
    			return false;
    		}
    		Map<Integer, SGLineStyle> prevMap = this.getLineStyleMap();
    		for (int ii = 0; ii < styleList.size(); ii++) {
    			SGStyle s = styleList.get(ii);
    			if (!(s instanceof SGLineStyle)) {
    				return false;
    			}
    			SGLineStyle lineStyle = (SGLineStyle) s;
    			if (!this.setLineStyle(lineStyle, ii)) {
    				return false;
    			}
    			SGLineStyle prev = prevMap.get(ii);
    			if (!lineStyle.equals(prev)) {
    				this.setChanged(true);
    			}
    		}
    		SGFigureElementLegend.this.notifyChangeOnCommit();
        	return true;
        }

    	/**
    	 * Returns the style of drawing elements.
    	 *
    	 * @return the list of style
    	 */
    	@Override
        public List<SGStyle> getStyle() {
    		Map<Integer, SGLineStyle> styleMap = this.getLineStyleMap();
    		return new ArrayList<SGStyle>(styleMap.values());
        }

    	private boolean mLineColorAutoAssigned = false;

        /**
         * Returns whether line color is automatically assigned.
         * 
         * @return true if line color is automatically assigned
         */
    	@Override
        public boolean isLineColorAutoAssigned() {
    		return this.mLineColorAutoAssigned;
    	}

        /**
         * Sets whether line color is automatically assigned
         * 
         * @param b
         *           a flag whether line color is automatically assigned
         */
    	@Override
        public void setLineColorAutoAssigned(final boolean b) {
    		this.mLineColorAutoAssigned = b;
    	}
    	
        /**
         * Returns the color map manager for lines.
         * 
         * @return the color map manager for lines
         */
    	@Override
        public SGColorMapManager getLineColorMapManager() {
        	return this.mLineColorMapManager;
        }
    	
        /**
         * Returns the name of the color map for lines.
         * 
         * @return the name of the color map for lines
         */
    	@Override
        public String getLineColorMapName() {
        	return this.mLineColorMapName;
        }
    	
    	/**
    	 * Sets the color map for lines.
    	 * 
    	 * @param name
    	 *           name of the map to set
    	 * @return true if succeeded
    	 */
    	@Override
        public boolean setLineColorMapName(String name) {
    		super.setLineColorMapName(name);
    		if (this.mLineColorMapManager.getColorMap(name) == null) {
    			return false;
    		}
        	return true;
        }
    	
        /**
         * Returns the color map for lines.
         * 
         * @return the color map for lines
         */
    	@Override
        public SGColorMap getLineColorMap() {
    		return this.mLineColorMapManager.getColorMap(this.mLineColorMapName);
    	}
    	
        /**
         * Returns a map of the properties of line color maps.
         * 
         * @return a map of the properties of line color maps
         */
    	@Override
        public Map<String, SGProperties> getLineColorMapProperties() {
    		return this.mLineColorMapManager.getColorMapProperties();
        }

	    /**
	     * Sets the properties of line color map.
	     * 
	     * @param colorMapProperties
	     *           the map of properties of color maps
	     * @return true if succeeded
	     */
	    @Override
	    public boolean setLineColorMapProperties(Map<String, SGProperties> colorMapProperties) {
	    	return this.mLineColorMapManager.setColorMapProperties(colorMapProperties);
	    }

		@Override
		public boolean initLineStyle(Element el) {
			List<SGLineStyle> lineStyleList = new ArrayList<SGLineStyle>();
	        for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
	            ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
	            SGElementGroupLine lineGroup = gs.getLineGroup();
	            if (el != null) {
    	            if (SGElementGroupLine.readStyleProperty(el, lineGroup, ii) == false) {
    	            	return false;
    	            }
	            }
	            SGLineStyle lineStyle = lineGroup.getLineStyle();
	            gs.setLineStyle(lineStyle);
	            lineStyleList.add(lineStyle);
	        }
	        ElementGroupLine lineGroup = (ElementGroupLine) this.getLineGroup();
	        lineGroup.setLineStyleList(lineStyleList);
	        return true;
		}

		@Override
	    public boolean readColorMap(final Element el) {
			return this.mLineColorMapManager.readProperty(el);
	    }

	    /**
	     * Returns the line style at given index.
	     * 
	     * @param index
	     *           index of child object
	     * @return true if succeeded
	     */
		@Override
	    public SGLineStyle getLineStyle(final int index) {
	    	if (index < 0 || index >= this.getChildNumber()) {
	    		throw new IllegalArgumentException("Index out of bounds: " + index);
	    	}
	        ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(index);
	    	return (SGLineStyle) gs.getLineStyle().clone();
	    }
		
	    /**
	     * Sets the properties of a child data object.
	     * 
	     * @param map
	     *           a map of properties
	     * @param childId
	     *           ID of a child data object
	     * @return the result of setting properties
	     */
		@Override
	    public SGPropertyResults setProperties(SGPropertyMap map, final int childId) {
	    	// do nothing
	    	return null;
	    }
		
	    /**
	     * Sets the properties of color map.
	     * 
	     * @param colorMapName
	     *           the name of color map
	     * @param map
	     *           a map of properties
	     * @return the result of setting properties
	     */
		@Override
	    public SGPropertyResults setColorMapProperties(final String colorMapName, SGPropertyMap map) {
	    	// do nothing
	    	return null;
	    }

	    /**
	     * Updates the child objects.
	     * 
	     * @return true if succeeded
	     */
	    @Override
	    public boolean updateChild() {
	    	if (!updateChildGroupSet(this)) {
	    		return false;
	    	}
	    	if (!this.initChildLineStyle()) {
	    		return false;
	    	}
	    	return true;
	    }

		@Override
		public boolean isDataShifted() {
	    	SGISXYTypeMultipleData sxyData = (SGISXYTypeMultipleData) this.mData;
	    	SGTuple2d shift = sxyData.getShift();
	    	return !shift.isZero();
		}
		
	    /**
	     * Replaces the data source.
	     * 
	     * @param srcOld
	     *           old data source
	     * @param srcNew
	     *           new data source
	     * @param obs
	     *           data source observer
	     */
		@Override
	    public void replaceDataSource(final SGIDataSource srcOld, final SGIDataSource srcNew,
	    		final SGDataSourceObserver obs) {
			super.replaceDataSource(srcOld, srcNew, obs);
			for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
				ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
				gs.replaceDataSource(srcOld, srcNew, obs);
			}
	    }

		@Override
		public boolean getAxisDateMode(final int location) {
			return mAxisElement.getAxisDateMode(location);
		}

		@Override
		public boolean setTickLabelDateFormat(String format) {
			return SGMultipleSXYUtility.setTickLabelDateFormat(
					this.getTickLabelGroupsIgnoreNull(), format);
		}

		@Override
		public boolean hasDateTickLabels() {
			SGISXYTypeMultipleData sxyDate = (SGISXYTypeMultipleData) this.mData; 
			return !sxyDate.hasGenericTickLabels();
		}
    }

	/**
	 *
	 */
	private class ElementGroupSetInLegendVXY extends ElementGroupSetInLegend
			implements SGIElementGroupSetVXY, SGIVXYDataDialogObserver,
			SGIVXYDataConstants {

		/**
		 *
		 */
		protected ElementGroupSetInLegendVXY(SGData data) {
			super(data);
		}

	    /**
	     * Returns the figure element.
	     * 
	     * @return the figure element
	     */
	    public SGFigureElementForData getFigureElement() {
	    	return SGFigureElementLegend.this;
	    }

		// scaling factor for the magnitude of vectors
		private float mMagnitudePerCM = 1.0f;

		public float getMagnitudePerCM() {
			return this.mMagnitudePerCM;
		}

		public boolean setMagnitudePerCM(final float mag) {
			if (mag <= 0.0) {
				throw new IllegalArgumentException("mag <= 0.0");
			}
			SGIVXYTypeData vData = (SGIVXYTypeData) this.mData;
			if (Float.isNaN(mag)) {
				this.mMagnitudePerCM = mag;
			} else {
				this.mMagnitudePerCM = SGDataUtility.roundMagnitudePerCM(mag, vData);
			}
			return true;
		}

		// a flag whether to fix the direction of each vector
		private boolean mDirectionFixedFlag;

		public boolean isDirectionInvariant() {
			return this.mDirectionFixedFlag;
		}

		public boolean setDirectionInvariant(final boolean b) {
			this.mDirectionFixedFlag = b;
			return true;
		}

		/**
		 *
		 */
		public boolean addDrawingElementGroup(final int type) {
			SGElementGroup group = null;
			if (type == SGIElementGroupConstants.ARROW_GROUP) {
				group = new ElementGroupArrow();
			} else {
				throw new Error();
			}

			this.addDrawingElementGroup(group);

			return true;
		}

		/**
		 *
		 */
		void paintSymbol(final Graphics2D g2d) {
			// arrow
			ElementGroupArrow groupArrow = getGroupArrow(this);
			if (groupArrow != null) {
				if (groupArrow.isVisible()) {
					groupArrow.paintElement(g2d);
				}
			}

		}

		boolean onMouseClicked(final MouseEvent e) {
			// do nothing
			return true;
		}

		/**
		 *
		 */
		public int getXAxisLocation() {
			return SGFigureElementLegend.this.mAxisElement
					.getLocationInPlane(this.getXAxis());
		}

		/**
		 *
		 */
		public int getYAxisLocation() {
			return SGFigureElementLegend.this.mAxisElement
					.getLocationInPlane(this.getYAxis());
		}

		public float getLineWidth(final String unit) {
			return this.getArrowGroup().getLineWidth(unit);
		}

		public int getLineType() {
			return this.getArrowGroup().getLineType();
		}

		public Color getColor() {
			return this.getArrowGroup().getColor();
		}

		public float getHeadSize(final String unit) {
			return this.getArrowGroup().getHeadSize(unit);
		}

		public float getHeadOpenAngle() {
			return this.getArrowGroup().getHeadOpenAngle();
		}

		public float getHeadCloseAngle() {
			return this.getArrowGroup().getHeadCloseAngle();
		}

		public int getStartHeadType() {
			return this.getArrowGroup().getStartHeadType();
		}

		public int getEndHeadType() {
			return this.getArrowGroup().getEndHeadType();
		}

		/**
		 *
		 */
		public boolean setXAxisLocation(final int config) {
			this.mXAxis = this.getAxis(config);
			return true;
		}

		/**
		 *
		 */
		public boolean setYAxisLocation(final int config) {
			this.mYAxis = this.getAxis(config);
			return true;
		}

		public boolean setLineWidth(final float width, final String unit) {
			return this.getArrowGroup().setLineWidth(width, unit);
		}

		public boolean setLineType(final int type) {
			return this.getArrowGroup().setLineType(type);
		}

		public boolean setColor(final Color cl) {
			return this.getArrowGroup().setColor(cl);
		}

		public boolean setHeadSize(final float size, final String unit) {
			return this.getArrowGroup().setHeadSize(size, unit);
		}

		public boolean setHeadAngle(final Float openAngle,
				final Float closeAngle) {
			return this.getArrowGroup().setHeadAngle(openAngle, closeAngle);
		}

		public boolean setStartHeadType(final int type) {
			return this.getArrowGroup().setStartHeadType(type);
		}

		public boolean setEndHeadType(final int type) {
			return this.getArrowGroup().setEndHeadType(type);
		}

		public boolean hasValidAngle(final Number open, final Number close) {
			final float openAngle = (open != null) ? open.floatValue() : this
					.getHeadOpenAngle();
			final float closeAngle = (close != null) ? close.floatValue()
					: this.getHeadCloseAngle();
			return (openAngle < closeAngle);
		}

		/**
		 *
		 */
		public SGProperties getProperties() {
			ElementGroupSetInVXYGraphProperties ep = new ElementGroupSetInVXYGraphProperties();
			if (this.getProperties(ep) == false) {
				return null;
			}

			return ep;
		}

		/**
		 *
		 */
		public boolean getProperties(final SGProperties p) {
			if ((p instanceof ElementGroupSetInVXYGraphProperties) == false) {
				return false;
			}
			if (super.getProperties(p) == false) {
				return false;
			}

			ElementGroupSetInVXYGraphProperties ep = (ElementGroupSetInVXYGraphProperties) p;
			ep.mMagnitudeScalingFactor = this.getMagnitudePerCM();
			ep.mDirectionInvariant = this.isDirectionInvariant();

			return true;
		}

		/**
		 *
		 */
		public boolean setProperties(final SGProperties p) {
			if ((p instanceof ElementGroupSetInVXYGraphProperties) == false) {
				return false;
			}
			if (super.setProperties(p) == false) {
				return false;
			}

			ElementGroupSetInVXYGraphProperties ep = (ElementGroupSetInVXYGraphProperties) p;
			this.setMagnitudePerCM(ep.mMagnitudeScalingFactor);
			this.setDirectionInvariant(ep.mDirectionInvariant);

			return true;
		}

		/**
		 * Returns a list of arrow groups.
		 *
		 * @return a list of arrow groups
		 */
		public List<SGElementGroupArrow> getArrowGroups() {
			List<SGElementGroupArrow> retList = new ArrayList<SGElementGroupArrow>();
			List<SGElementGroup> list = SGUtilityForFigureElement.getGroups(
					SGElementGroupArrow.class, this.mDrawingElementGroupList);
			for (int ii = 0; ii < list.size(); ii++) {
				retList.add((SGElementGroupArrow) list.get(ii));
			}
			return retList;
		}

		/**
		 * Returns an arrow group which is the first element of an array.
		 *
		 * @return the first element of an array of arrow groups, or null when this
		 *         group set does not have any arrow groups
		 */
		public SGElementGroupArrow getArrowGroup() {
			return (SGElementGroupArrow) SGUtilityForFigureElement.getGroup(
					SGElementGroupArrow.class, this.mDrawingElementGroupList);
		}

	    /**
	     * Sets the properties of element groups.
	     *
	     * @param elementGroupPropertiesList
	     * @return true if succeeded
	     *
	     */
	    protected boolean setElementGroupProperties(List elementGroupPropertiesList) {
	        for (int ii = 0; ii < elementGroupPropertiesList.size(); ii++) {
	            SGProperties gp = (SGProperties) elementGroupPropertiesList.get(ii);
	            SGElementGroup group = null;
	            if (gp instanceof SGElementGroupArrow.ArrowProperties) {
	                group = this.getArrowGroup();
	            } else {
	                throw new Error("Illegal group property: " + gp);
	            }
	            if (group == null) {
	                continue;
	            }
	            if (group.setProperties(gp) == false) {
	                return false;
	            }
	        }
	        return true;
	    }

		@Override
		public boolean setStrideX(SGIntegerSeriesSet stride) {
			return this.setStrideXToData(stride);
		}

		@Override
		public boolean setStrideY(SGIntegerSeriesSet stride) {
			return this.setStrideYToData(stride);
		}

		@Override
		public boolean setIndexStride(SGIntegerSeriesSet stride) {
			return this.setIndexStrideToData(stride);
		}

	    /**
	     * Sets the stride for single dimensional data.
	     *
	     * @param stride
	     *           stride of arrays
	     * @return true if succeeded
	     */
		@Override
	    public boolean setSDArrayStride(SGIntegerSeriesSet stride) {
			return this.setIndexStride(stride);
	    }

	    /**
	     * Sets the column types and related parameters.
	     *
	     * @param map
	     *           property map
	     * @param result
	     *           results of setting properties
	     * @param cols
	     *           an array of data columns
	     * @return true if succeeded
	     */
		@Override
	    protected boolean setProperties(SGPropertyMap map, SGPropertyResults result,
	    		SGDataColumnInfo[] cols) {
			// do nothing
			return true;
		}

		@Override
		public Map<String, SGProperties> getElementGroupPropertiesMap() {
			return SGUtilityForFigureElementJava2D.getElementGroupPropertiesMap(this);
		}

		@Override
		public boolean setElementGroupPropertiesMap(Map<String, SGProperties> pMap) {
			return SGUtilityForFigureElementJava2D.setElementGroupPropertiesMap(this, pMap);
		}

		@Override
		public boolean getAxisDateMode(final int location) {
			return mAxisElement.getAxisDateMode(location);
		}
	}

	private class ElementGroupLine extends SGElementGroupLineForData implements
			ILegendElement, SGISXYDataConstants {

		private SGTuple2f mStart = new SGTuple2f();

		private SGTuple2f mEnd = new SGTuple2f();

		/**
		 * A group set that this element group belongs.
		 */
		protected ElementGroupSetInLegend mGroupSet = null;

		/**
		 * The default constructor.
		 */
		protected ElementGroupLine(SGISXYTypeData data) {
			super(data);

			// initialize the index array
			this.mEndPointsIndexArray = new int[1][2];
		}

		/**
		 * Sets the element group set.
		 * @param gs
		 *           the element group set
		 */
		public boolean setElementGroupSet(ElementGroupSetInLegend gs) {
			this.mGroupSet = gs;
			return true;
		}

		/**
		 *
		 */
		public float getPreferredWidth() {
			return this.getMagnification() * getSymbolSpan();
		}

		/**
		 * Returns the preferred height.
		 * @return
		 *         the preferred height
		 */
		public float getPreferredHeight() {
			return this.getMagnification() * this.getLineWidth();
		}

		private Rectangle2D mBoundsRect = new Rectangle2D.Float();

		/**
		 *
		 * @param rect
		 */
		public void setDataElementBounds(final Rectangle2D rect) {
			this.mBoundsRect = rect;
		}

		/**
		 * Returns the number of points in this element group.
		 * @return
		 *         the number of points
		 */
		public int getNumberOfPoints() {
			return 2;
		}

		/**
		 * Create drawing elements.
		 */
		public boolean createDrawingElementInLegend() {
			// final float width = this.getDataElementWidth();

			Rectangle2D lRect = getRectOfGroupSet(this.mGroupSet);
			Rectangle2D dRect = this.mBoundsRect;

			SGTuple2f start = new SGTuple2f();
			start.x = (float) lRect.getX();
			start.y = (float) lRect.getY() + 0.50f * (float) lRect.getHeight();
			SGTuple2f end = new SGTuple2f();
			end.x = start.x + (float) dRect.getWidth();
			end.y = start.y;

			if (this.setLocation(new SGTuple2f[] { start, end }) == false) {
				return false;
			}
			this.mStart = start;
			this.mEnd = end;

			return true;
		}

		/**
		 * Overrode for gradient paint and anchors.
		 * 
		 */
		@Override
		public boolean paintElement(final Graphics2D g2d, final Rectangle2D clipRect) {
	        if (g2d == null) {
	            return false;
	        }
	        
   	        // setup the stroke
        	List<Object> lineTypeList = new ArrayList<Object>();
	        List<Object> lineWidthList = new ArrayList<Object>();
        	for (SGLineStyle style : this.mLineStyleList) {
        		lineTypeList.add(style.getLineType());
        		lineWidthList.add(style.getLineWidth());
        	}
        	
        	List<Object> mfLineTypeList = SGUtility.findMostFrequentObjects(lineTypeList);
        	if (mfLineTypeList.size() == 0) {
        		return false;
        	}
        	int minLineType = Integer.MAX_VALUE;
        	for (Object obj : mfLineTypeList) {
        		Integer lt = (Integer) obj;
        		if (lt < minLineType) {
        			minLineType = lt;
        		}
        	}
        	Integer lineType = minLineType;
        	
        	List<Object> mfLineWidthList = SGUtility.findMostFrequentObjects(lineWidthList);
        	if (mfLineWidthList.size() == 0) {
        		return false;
        	}
        	float maxLineWidth = 0.0f;
        	for (Object obj : mfLineWidthList) {
        		Float lw = (Float) obj;
        		if (lw > maxLineWidth) {
        			maxLineWidth = lw;
        		}
        	}
        	Float lineWidth = maxLineWidth;
        	
        	SGStroke tempStroke = (SGStroke) this.mStroke.clone();
        	tempStroke.setLineType(lineType);
        	tempStroke.setLineWidth(lineWidth);
	        Stroke stroke = tempStroke.getBasicStroke();
	        g2d.setStroke(stroke);

	        // draw lines
			final float y = this.mStart.y;
			if (this.mLineStyleList.size() == 1) {
        		SGLineStyle style = this.mLineStyleList.get(0);
        		Color cl = style.getColor();
        		g2d.setPaint(cl);
        		Line2D line = new Line2D.Float(this.mStart.x, y, this.mEnd.x, y);
        		g2d.draw(line);
			} else if (this.mLineStyleList.size() > 1) {
		        // setup the location of lines
				final float xDiff = (this.mEnd.x - this.mStart.x) / (this.mLineStyleList.size() - 1);
				final float[] xArray = new float[this.mLineStyleList.size()];
				for (int ii = 0; ii < this.mLineStyleList.size(); ii++) {
					xArray[ii] = this.mStart.x + ii * xDiff;
				}
				
				final int lineNum = this.mLineStyleList.size() - 1;
				
				// creates a single GeneralPath object
				GeneralPath gPath = new GeneralPath();
				for (int ii = 0; ii < lineNum; ii++) {
	        		Line2D line = new Line2D.Float(xArray[ii], y, xArray[ii + 1], y);
	        		gPath.append(line, true);
				}
				
				// creates GradientPaint objects
				Paint[] paintArray = new Paint[lineNum];
	        	for (int ii = 0; ii < lineNum; ii++) {
	        		SGLineStyle style1 = this.mLineStyleList.get(ii);
	        		SGLineStyle style2 = this.mLineStyleList.get(ii + 1);
	        		final float start = xArray[ii];
	        		final float end = xArray[ii + 1];
	        		Color cl1 = style1.getColor();
	        		Color cl2 = style2.getColor();
	        		GradientPaint gp = new GradientPaint(start, y, cl1, end, y, cl2);
	        		paintArray[ii] = gp;
	        	}

	        	// draw shapes
	        	Rectangle2D rect = getRectOfGroupSet(this.mGroupSet);
	        	if (rect != null) {
		        	final float rectY = (float) rect.getY();
		        	final float rectH = (float) rect.getHeight();
		        	Shape clip = g2d.getClip();
		        	for (int ii = 0; ii < lineNum; ii++) {
						// clips the path
		        		final float rectW = xArray[ii + 1] - xArray[ii];
		        		Rectangle2D cRect = new Rectangle2D.Float(xArray[ii], rectY, rectW, rectH);
		        		g2d.setClip(cRect);
		        		g2d.setPaint(paintArray[ii]);
		        		g2d.draw(gPath);
		        	}
	        		g2d.setClip(clip);
	        	}
			}
        	
			// draw anchors if the group set is selected
			if (this.mGroupSet.isViewable()
					&& this.mGroupSet.isSelected()
					&& isSymbolsVisibleAroundFocusedObjects()) {
				if (this.mGroupSet instanceof SGIElementGroupSetXY) {
					SGIElementGroupSetXY gsSXY = (SGIElementGroupSetXY) this.mGroupSet;
					if (!gsSXY.getSymbolGroup().isVisible()
							&& !gsSXY.getBarGroup().isVisible()) {
						SGDrawingElementLine2D line = (SGDrawingElementLine2D) this.mDrawingElementArray[0];
						SGTuple2f start = line.getStart();
						SGTuple2f end = line.getEnd();
						SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(
								new Point2D.Float(start.x, start.y), g2d);
						SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(
								new Point2D.Float(end.x, end.y), g2d);
					}
				}
			}
			return true;
		}

		/**
		 *
		 * @param el
		 * @return
		 */
		public boolean readProperty(final Element el) {
			if (super.readProperty(el) == false) {
				return false;
			}

			return SGFigureElementLegend.this.readProperty(this, el);
		}

		@Override
		public SGTuple2f getEnd(int index) {
			return this.mEnd;
		}

		@Override
		public SGTuple2f getStart(int index) {
			return this.mStart;
		}

		@Override
	    public void setStyle(SGLineStyle s) {
	    	super.setStyle(s);
	    	SGLineStyle style = (s != null) ? (SGLineStyle) s.clone() : null;
	    	this.mLineStyleList.clear();
	    	this.mLineStyleList.add(style);
	    }
		
		List<SGLineStyle> mLineStyleList = new ArrayList<SGLineStyle>();
		
		void clearLineStyleList() {
			this.mLineStyleList.clear();
		}
		
		void setLineStyleList(List<SGLineStyle> lineStyleList) {
			this.mLineStyleList = new ArrayList<SGLineStyle>(lineStyleList);
		}
		
		void addLineStyle(SGLineStyle lineStyle) {
			this.mLineStyleList.add(lineStyle);
		}
		
		void setLineStyle(SGLineStyle lineStyle, final int index) {
			this.mLineStyleList.set(index, lineStyle);
		}
	}

	protected static class BarInLegend extends BarInGroup {
		/**
		 * Builds a rectangle in a group of rectangles.
		 *
		 * @param group
		 *           a group of rectangles
		 */
		public BarInLegend(ElementGroupBar group, final int index) {
			super(group, index);
		}

		public float getWidth() {
			return this.mGroup.getRectangleWidth();
		}

		public float getHeight() {
			return this.mGroup.getRectangleHeight();
		}

		public boolean setWidth(final float w) {
			return this.mGroup.setRectangleWidth(w);
		}

		public boolean setHeight(final float h) {
			return this.mGroup.setRectangleHeight(h);
		}
	}

	/**
	 * @author okumura
	 */
	private class ElementGroupBar extends SGElementGroupBarForData implements
			ILegendElement, SGISXYDataConstants {

		/**
		 * A group set that this element group belongs.
		 */
		protected ElementGroupSetInLegend mGroupSet = null;

		/**
		 * The default constructor.
		 */
		protected ElementGroupBar(SGISXYTypeData data) {
			super(data);
		}

		/**
		 * Creates and returns an instance of drawing element.
		 *
		 * @return an instance of drawing element
		 */
		protected SGDrawingElement createDrawingElementInstance(final int index) {
			return new BarInLegend(this, index);
		}

		/**
		 * Sets the element group set.
		 * @param gs
		 *           the element group set
		 */
		public boolean setElementGroupSet(ElementGroupSetInLegend gs) {
			this.mGroupSet = gs;
			return true;
		}

		/**
		 * Returns the preferred width.
		 * @return
		 *         the preferred width
		 */
		public float getPreferredWidth() {
			final ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY) this.mGroupSet;
			Rectangle2D strRect = gs.mDrawingString.getElementBounds();
			return (float) strRect.getHeight();
		}

		/**
		 * Returns the preferred height.
		 * @return
		 *         the preferred height
		 */
		public float getPreferredHeight() {
			return this.getPreferredWidth();
		}

		private Rectangle2D mBoundsRect = new Rectangle2D.Float();

		/**
		 *
		 * @param rect
		 */
		public void setDataElementBounds(final Rectangle2D rect) {
			this.mBoundsRect = rect;
		}

		/**
		 * Returns the number of points in this element group.
		 * @return
		 *         the number of points
		 */
		public int getNumberOfPoints() {
			return 1;
		}

		/**
		 *
		 */
		public boolean createDrawingElementInLegend() {
			Rectangle2D lRect = getRectOfGroupSet(this.mGroupSet);
			Rectangle2D dRect = this.mBoundsRect;

			// set size
			final float h = this.getPreferredWidth();
			this.setRectangleHeight(h);
			final float w = (float) dRect.getWidth();
			this.setRectangleWidth(w);

			// set location
			final float x = (float) lRect.getX();
			final float y = (float) (lRect.getY() + lRect.getHeight() / 2
					- this.getPreferredHeight() / 2);
			SGTuple2f start = new SGTuple2f(x, y);
			this.setLocation(new SGTuple2f[] { start });

			return true;
		}

		/**
		 *
		 * @return
		 */
		public boolean initDrawingElement(final int num) {
			super.initDrawingElement(num);
			return true;
		}

		/**
		 * The location.
		 */
		private SGTuple2f mLocation = new SGTuple2f();

		/**
		 *
		 */
		public boolean setLocation(final SGTuple2f[] pointArray) {

			if (this.mDrawingElementArray == null) {
				return true;
			}

			if (pointArray.length != this.mDrawingElementArray.length) {
//				throw new IllegalArgumentException();
				this.initDrawingElement(pointArray);
			}

			this.mLocation = pointArray[0];

			return true;
		}

		/**
		 * Paint line objects.
		 */
		public boolean paintElement(final Graphics2D g2d,
				final Rectangle2D clipRect) {
			if (super.paintElement(g2d, clipRect) == false) {
				return false;
			}

			// draw anchors if the group set is selected
			if (this.mGroupSet.isViewable()
					&& this.mGroupSet.isSelected()
					&& isSymbolsVisibleAroundFocusedObjects()) {
				SGDrawingElementBar2D bar = (SGDrawingElementBar2D) this.mDrawingElementArray[0];
				Rectangle2D rect = bar.getElementBounds();
				SGUtilityForFigureElementJava2D.drawAnchorsOnRectangle(rect,
						g2d);
			}
			return true;
		}

		/**
		 *
		 * @param el
		 * @return
		 */
		public boolean readProperty(final Element el) {
			if (super.readProperty(el) == false) {
				return false;
			}
			return SGFigureElementLegend.this.readProperty(this, el);
		}

		@Override
		public float getX(int index) {
			return this.mLocation.x;
		}

		@Override
		public float getY(int index) {
			return this.mLocation.y;
		}

//		@Override
//		protected float getShiftXInGraph() {
//		    return 0;
//		}
//
//		@Override
//		protected float getShiftYInGraph() {
//		    return 0;
//		}

	}

	/**
	 * @author okumura
	 */
	private class ElementGroupSymbol extends SGElementGroupSymbolForData
			implements ILegendElement, SGISXYDataConstants {

		/**
		 * A group set that this element group belongs.
		 */
		protected ElementGroupSetInLegend mGroupSet = null;

		/**
		 * The default constructor.
		 */
		protected ElementGroupSymbol(SGISXYTypeData data) {
			super(data);
		}

		/**
		 * Sets the element group set.
		 * @param gs
		 *           the element group set
		 */
		public boolean setElementGroupSet(ElementGroupSetInLegend gs) {
			this.mGroupSet = gs;
			return true;
		}

		/**
		 * Returns the preferred width.
		 * @return
		 *         the preferred width
		 */
		public float getPreferredWidth() {
			return this.getDataElementSize();
		}

		/**
		 * Returns the preferred height.
		 * @return
		 *         the preferred height
		 */
		public float getPreferredHeight() {
			return 1.20f * this.getDataElementSize();
		}

		/**
		 *
		 */
		private float getDataElementSize() {
			SGDrawingElement[] array = this.mDrawingElementArray;
			if (array != null) {
				if (array.length == 0) {
					return 0.0f;
				}

				SGDrawingElementSymbol2D symbol = (SGDrawingElementSymbol2D) array[0];
				Rectangle2D rect = symbol.getElementBounds().getBounds2D();
				return (float) rect.getHeight();
			}
			return 0.0f;
		}

		private Rectangle2D mBoundsRect = new Rectangle2D.Float();

		/**
		 *
		 * @param rect
		 */
		public void setDataElementBounds(final Rectangle2D rect) {
			this.mBoundsRect = rect;
		}

		/**
		 * Returns the number of points of this element group.
		 * @return
		 *         the number of points
		 */
		public int getNumberOfPoints() {
			return 1;
		}

		private SGTuple2f mLocation = new SGTuple2f();

		/**
		 *
		 */
		public boolean createDrawingElementInLegend() {
			Rectangle2D lRect = getRectOfGroupSet(this.mGroupSet);
			Rectangle2D dRect = this.mBoundsRect;
			// final float x = (float)lRect.getX() +
			// 0.50f*this.getDataElementWidth();
			final float x = (float) lRect.getX() + 0.50f
					* (float) dRect.getWidth();
			final float y = (float) lRect.getY() + 0.50f
					* (float) lRect.getHeight();
			SGTuple2f position = new SGTuple2f(x, y);

			this.mLocation = position;
			if (this.setLocation(new SGTuple2f[] { position }) == false) {
				return false;
			}

			return true;

		}

		/**
		 * Paint line objects.
		 */
		public boolean paintElement(final Graphics2D g2d,
				final Rectangle2D clipRect) {
			if (super.paintElement(g2d, clipRect) == false) {
				return false;
			}

			// draw anchors if the group set is selected
			if (this.mGroupSet.isViewable()
					&& this.mGroupSet.isSelected()
					&& isSymbolsVisibleAroundFocusedObjects()) {
				if (this.mGroupSet instanceof SGIElementGroupSetXY) {
					SGIElementGroupSetXY gsSXY = (SGIElementGroupSetXY) this.mGroupSet;
					if (!gsSXY.getBarGroup().isVisible()) {
						SGDrawingElementSymbol2D symbol = (SGDrawingElementSymbol2D) this.mDrawingElementArray[0];
						Rectangle2D rect = symbol.getElementBounds();
						SGUtilityForFigureElementJava2D.drawAnchorsOnRectangle(
								rect, g2d);
					}
				}
			}
			return true;
		}

		/**
		 *
		 * @param el
		 * @return
		 */
		public boolean readProperty(final Element el) {
			if (super.readProperty(el) == false) {
				return false;
			}

			return SGFigureElementLegend.this.readProperty(this, el);
		}

		/**
		 * Retruns the location at a given index.
		 *
		 * @param index
		 *           the index
		 * @return the location
		 */
		public SGTuple2f getLocation(final int index) {
			return this.mLocation;
		}

	}

	/**
	 * @author okumura
	 */
	private class ElementGroupArrow extends SGElementGroupArrowForData
			implements ILegendElement, SGIVXYDataConstants {

		/**
		 * A group set that this element group belongs.
		 */
		protected ElementGroupSetInLegend mGroupSet = null;

		/**
		 * The default constructor.
		 */
		protected ElementGroupArrow() {
			super();

			// set properties to the magnitude label
			this.mMagnitudeString.setFontName(DEFAULT_LEGEND_FONT_NAME);
			this.mMagnitudeString.setFontStyle(DEFAULT_LEGEND_FONT_STYLE);
			this.mMagnitudeString.setFontSize(DEFAULT_LEGEND_FONT_SIZE,
					FONT_SIZE_UNIT);
			this.mMagnitudeString.setColor(DEFAULT_LEGEND_FONT_COLOR);
		}

		/**
		 *
		 * @return
		 */
		protected SGDrawingElement createDrawingElementInstance(final int index) {
			return new ArrowInGroup(this, index);
		}

		/**
		 * Sets the element group set.
		 * @param gs
		 *           the element group set
		 */
		public boolean setElementGroupSet(ElementGroupSetInLegend gs) {
			this.mGroupSet = gs;
			return true;
		}

		/**
		 * Returns the preferred width.
		 * @return
		 *         the preferred width
		 */
		public float getPreferredWidth() {
			return this.getMagnification() * getSymbolSpan();
		}

		/**
		 *
		 */
		private Rectangle2D getDataElementBounds() {
			SGDrawingElement[] array = this.mDrawingElementArray;
			if (array != null) {
				if (array.length == 0) {
					return new Rectangle2D.Float();
				}

				SGDrawingElementArrow2D el = (SGDrawingElementArrow2D) array[0];
				Rectangle2D rect = el.getElementBounds();
				return rect;
			}
			return new Rectangle2D.Float();
		}

		/**
		 * Returns the preferred height.
		 * @return
		 *         the preferred height
		 */
		public float getPreferredHeight() {
			final float elementSize = 1.20f * (float) this
					.getDataElementBounds().getHeight();
			final float strHeight = (float) this.mMagnitudeString
					.getElementBounds().getHeight();
			final float size = elementSize + 2.0f * strHeight;
			return size;
		}

		private Rectangle2D mBoundsRect = new Rectangle2D.Float();

		/**
		 *
		 * @param rect
		 */
		public void setDataElementBounds(final Rectangle2D rect) {
			this.mBoundsRect = rect;
		}

		/**
		 * Returns the number of points in this element group.
		 * @return
		 *         the number of points
		 */
		public int getNumberOfPoints() {
			return 1;
		}

		private SGTuple2f mStartPoint = new SGTuple2f();

		private SGTuple2f mEndPoint = new SGTuple2f();

		/**
		 *
		 */
		public boolean createDrawingElementInLegend() {
			Rectangle2D lRect = getRectOfGroupSet(this.mGroupSet);
			Rectangle2D dRect = this.mBoundsRect;
			final float headSize = this.getMagnification() * this.getHeadSize();
			final int startHeadType = this.getStartHeadType();
			final int endHeadType = this.getEndHeadType();

			final float x = (float) lRect.getX();
			final float y = (float) lRect.getY();
			final float w = (float) dRect.getWidth();
			final float h = (float) lRect.getHeight();

			SGTuple2f start = new SGTuple2f();
			start.x = x;
			if (this.doShiftX(startHeadType)) {
				start.x += headSize;
			}
			start.y = y + 0.50f * h;

			SGTuple2f end = new SGTuple2f();
			end.x = x + w;
			if (this.doShiftX(endHeadType)) {
				end.x -= headSize;
			}
			end.y = start.y;

			this.mStartPoint = start;
			this.mEndPoint = end;
			if (this.setLocation(new SGTuple2f[] { start },
					new SGTuple2f[] { end }) == false) {
				return false;
			}

			//
			this.updateMagnitudeString();

			return true;
		}

		private boolean doShiftX(final int type) {
			final boolean b = (type != SGIArrowConstants.SYMBOL_TYPE_ARROW_HEAD)
					&& (type != SGIArrowConstants.SYMBOL_TYPE_ARROW)
					&& (type != SGIArrowConstants.SYMBOL_TYPE_TRANSVERSELINE)
					&& (type != SGIArrowConstants.SYMBOL_TYPE_VOID);
			return b;
		}

		private boolean updateMagnitudeString() {
			ElementGroupSetInLegendVXY groupSet = (ElementGroupSetInLegendVXY) this.mGroupSet;
			final float percm = groupSet.getMagnitudePerCM();
			if (Float.isNaN(percm)) {
				this.mMagnitudeString.setString("NaN");
			} else {
				final float span = SGFigureElementLegend.this.getSymbolSpan(cm);
				final float value = percm * span;
				final float valueReduced = (float) SGUtilityNumber
						.getNumberInNumberOrder(value, value, 3,
								BigDecimal.ROUND_HALF_UP);
				this.mMagnitudeString.setString(Float.toString(valueReduced));
			}

			Rectangle2D lRect = getRectOfGroupSet(this.mGroupSet);
			Rectangle2D dRect = this.mBoundsRect;
			Rectangle2D sRect = this.mMagnitudeString.getElementBounds();
			Rectangle2D elRect = this.getDataElementBounds();

			SGTuple2f location = new SGTuple2f();
			location.x = (float) (lRect.getX() + 0.50f * (dRect.getWidth() - sRect
					.getWidth()));
			location.y = (float) (elRect.getY() + elRect.getHeight()) + 2.0f;

			this.mMagnitudeString.setLocation(location);

			return true;
		}

		// string to display the magnitude of an arrow
		private SGDrawingElementString2DExtended mMagnitudeString = new SGDrawingElementString2DExtended();

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
			if (this.mMagnitudeString.setMagnification(mag) == false) {
				return false;
			}
			return true;
		}

		/**
		 *
		 */
		public boolean paintElement(final Graphics2D g2d,
				final Rectangle2D clipRect) {
			super.paintElement(g2d, clipRect);

			// paint the string to display the magnitude
			this.mMagnitudeString.paint(g2d, clipRect);

			// draw anchors if the group set is selected
			if (this.mGroupSet.isViewable()
					&& this.mGroupSet.isSelected()
					&& isSymbolsVisibleAroundFocusedObjects()) {
				SGDrawingElementArrow arrow = (SGDrawingElementArrow) this.mDrawingElementArray[0];
				SGTuple2f start = arrow.getStart();
				SGTuple2f end = arrow.getEnd();
				SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(
						new Point2D.Float(start.x, start.y), g2d);
				SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(
						new Point2D.Float(end.x, end.y), g2d);
			}

			return true;
		}

		/**
		 *
		 * @param el
		 * @return
		 */
		public boolean readProperty(final Element el) {
			if (super.readProperty(el) == false) {
				return false;
			}

			return SGFigureElementLegend.this.readProperty(this, el);
		}

		@Override
		public SGTuple2f getEndLocation(int index) {
			return this.mEndPoint;
		}

		@Override
		public SGTuple2f getStartLocation(int index) {
			return this.mStartPoint;
		}

		public boolean initDrawingElement(float[] xArray, float[] yArray) {
			return false;
		}

		public boolean setLocation(float[] xCoordinateArray, float[] yCoordinateArray) {
			return false;
		}

	}

	/**
	 * Error bars.
	 */
	private class ElementGroupErrorBar extends SGElementGroupErrorBarForData
			implements ILegendElement, SGISXYDataConstants {

		/**
		 * A group set that this element group belongs.
		 */
		protected ElementGroupSetInLegend mGroupSet = null;

		/**
		 * The constructor.
		 */
		protected ElementGroupErrorBar(SGISXYTypeData data) {
			super(data);
		}

		/**
		 * Sets the element group set.
		 * @param gs
		 *           the element group set
		 */
		public boolean setElementGroupSet(ElementGroupSetInLegend gs) {
			this.mGroupSet = gs;
			return true;
		}

		/**
		 * Returns the preferred width.
		 * @return
		 *         the preferred width
		 */
		public float getPreferredWidth() {
			return this.getMagnification() * this.getHeadSize();
		}

		private static final float DEFAULT_ERROR_BAR_HEIGHT = 10.0f;

		/**
		 * Returns the preferred height.
		 * @return
		 *         the preferred height
		 */
		public float getPreferredHeight() {

			ElementGroupSetInLegendSXY legend = (ElementGroupSetInLegendSXY) this.mGroupSet;

			SGData data = legend.getData();
			if (data instanceof SGISXYTypeData) {
				SGISXYTypeData dataSXY = (SGISXYTypeData) data;
				if (dataSXY.isErrorBarAvailable() == false) {
					return 0.0f;
				}
			}

			SGElementGroupSymbol sg = legend.getSymbolGroup();
			SGElementGroupBar bg = legend.getBarGroup();

			float size = 0.0f;
			if (sg.isVisible() || bg.isVisible()) {
				final float symbolSize = sg.isVisible() ? ((ILegendElement) sg)
						.getPreferredHeight() : 0.0f;
				final float barWidth = bg.isVisible() ? ((ILegendElement) bg)
						.getPreferredHeight() : 0.0f;
				size = (symbolSize > barWidth) ? symbolSize : barWidth;
			} else {
				size = DEFAULT_ERROR_BAR_HEIGHT;
			}

			final float mag = this.getMagnification();
			final float barHeight = 1.20f * mag * size;
			final float headSize = mag * this.getHeadSize();
			return barHeight + 2.0f * headSize;
		}

		private Rectangle2D mBoundsRect = new Rectangle2D.Float();

		/**
		 *
		 * @param rect
		 */
		public void setDataElementBounds(final Rectangle2D rect) {
			this.mBoundsRect = rect;
		}

		/**
		 * Returns the number of points of this element group.
		 * @return
		 *         the number of points
		 */
		public int getNumberOfPoints() {
			return 1;
		}

		private SGTuple2f mCenter = new SGTuple2f();

		private SGTuple2f mLower = new SGTuple2f();

		private SGTuple2f mUpper = new SGTuple2f();

		/**
		 * Create drawing elements.
		 */
		public boolean createDrawingElementInLegend() {
			SGTuple2f center = new SGTuple2f();
			SGTuple2f lower = new SGTuple2f();
			SGTuple2f upper = new SGTuple2f();

			Rectangle2D lRect = getRectOfGroupSet(this.mGroupSet);
			Rectangle2D dRect = this.mBoundsRect;

			final float mag = this.getMagnification();

			final float x = (float) lRect.getX() + 0.50f
					* (float) dRect.getWidth();
			center.x = x;
			lower.x = x;
			upper.x = x;

			final float headSize = mag * this.getHeadSize();
			final float y = (float) lRect.getY() + (float) lRect.getHeight()
					/ 2;
			final float d = ((float) dRect.getHeight() - headSize) / 2;

			center.y = y;
			lower.y = y + d;
			upper.y = y - d;

			this.mCenter = center;
			this.mLower = lower;
			this.mUpper = upper;
			if (this.setLocation(new SGTuple2f[] { center },
					new SGTuple2f[] { lower }, new SGTuple2f[] { upper }) == false) {
				return false;
			}

			return true;
		}

		/**
		 *
		 * @param el
		 * @return
		 */
		public boolean readProperty(final Element el) {
			if (super.readProperty(el) == false) {
				return false;
			}

			return SGFigureElementLegend.this.readProperty(this, el);
		}

		/**
		 * Returns whether this group set contains the given point.
		 *
		 * @param x
		 *          the x coordinate
		 * @param y
		 *          the y coordinate
		 * @return
		 *          true if this element group contains the given point
		 */
		public boolean contains(final int x, final int y) {
			// if the data object do not have error bars, return false
			SGISXYTypeData dataSXY = (SGISXYTypeData) getData(this.mGroupSet);
			if (dataSXY.isErrorBarAvailable() == false) {
				return false;
			}
			return super.contains(x, y);
		}

		/**
		 * Update the location of error bars.
		 * @return true if succeeded
		 */
		public boolean updateLocation() {
			// do nothing
			return true;
		}

		@Override
		public SGTuple2f getLowerEndLocation(int index) {
			return this.mLower;
		}

		@Override
		public SGTuple2f getStartLocation(int index) {
			return this.mCenter;
		}

		@Override
		public SGTuple2f getUpperEndLocation(int index) {
			return this.mUpper;
		}

	}

	/**
	 * @author okumura
	 */
	private class ElementGroupTickLabels extends SGElementGroupTickLabelForData
			implements ILegendElement, SGISXYDataConstants {

		/**
		 * A group set that this element group belongs.
		 */
		protected ElementGroupSetInLegend mGroupSet = null;

		/**
		 * Sets the element group set.
		 * @param gs
		 *           the element group set
		 */
		public boolean setElementGroupSet(ElementGroupSetInLegend gs) {
			this.mGroupSet = gs;
			return true;
		}

		/**
		 * The default constructor.
		 *
		 */
		ElementGroupTickLabels(SGISXYTypeData data) {
			super(data);
		}

		/**
		 *
		 */
		protected SGDrawingElement createDrawingElementInstance(final int index) {
			return new SGDrawingElementString2DExtended();
		}

		/**
		 * Create drawing elements.
		 */
		public boolean createDrawingElementInLegend() {
			// Do nothing
			return true;
		}

		/**
		 *
		 */
		public float getPreferredWidth() {
			return 0.0f;
		}

		/**
		 *
		 */
		public float getPreferredHeight() {
			return 0.0f;
		}

		// private Rectangle2D mBoundsRect = new Rectangle2D.Float();

		/**
		 *
		 * @param rect
		 */
		public void setDataElementBounds(final Rectangle2D rect) {
			// this.mBoundsRect = rect;
		}

		/**
		 * Returns the number of points of this element group.
		 * @return
		 *         the number of points
		 */
		public int getNumberOfPoints() {
			return 0;
		}

		/**
		 *
		 * @param el
		 * @return
		 */
		public boolean readProperty(final Element el) {
			if (super.readProperty(el) == false) {
				return false;
			}
			return SGFigureElementLegend.this.readProperty(this, el);
		}

		/**
		 * Returns whether this group set contains the given point.
		 *
		 * @param x
		 *          the x coordinate
		 * @param y
		 *          the y coordinate
		 * @return
		 *          true if this element group contains the given point
		 */
		public boolean contains(final int x, final int y) {
			// always returns false
			return false;
		}

		/**
		 * Update the location of tick labels.
		 * @return true if succeeded
		 */
		public boolean updateLocation() {
			return true;
		}
	}

	//
	private boolean readProperty(final SGElementGroup group, final Element el) {
		String str;
		Boolean b;

		// visible
		str = el.getAttribute(KEY_VISIBLE);
		if (str.length() != 0) {
			b = SGUtilityText.getBoolean(str);
			if (b == null) {
				return false;
			}
			group.setVisible(b.booleanValue());
		}

		return true;
	}

	/**
	 * @author okumura
	 */
	public static class LegendProperties extends SGProperties {

		float x;

		float y;

		boolean visible;

		boolean frameLineVisible;

		float frameLineWidth;

		Color frameLineColor;

		String fontName;

		float fontSize;

		int fontStyle;

		Color stringColor;

		SGFillPaint backgroundPaint = new SGFillPaint();

		float symbolSpan;

		SGAxis xAxis;

		SGAxis yAxis;

		ArrayList visibleElementGroupList = new ArrayList();

		public void dispose() {
        	super.dispose();
			this.frameLineColor = null;
			this.fontName = null;
			this.stringColor = null;
			this.stringColor = null;
            this.backgroundPaint = null;
			this.xAxis = null;
			this.yAxis = null;
			this.visibleElementGroupList.clear();
			this.visibleElementGroupList = null;
		}

		/**
		 *
		 */
		public boolean equals(final Object obj) {

			if ((obj instanceof LegendProperties) == false)
				return false;

			LegendProperties p = (LegendProperties) obj;

			if (p.x != this.x)
				return false;
			if (p.y != this.y)
				return false;
			if (p.visible != this.visible)
				return false;
			if (p.frameLineVisible != this.frameLineVisible)
				return false;
			if (p.frameLineWidth != this.frameLineWidth)
				return false;
			if (p.frameLineColor.equals(this.frameLineColor) == false)
				return false;
			if (p.fontName.equals(this.fontName) == false)
				return false;
			if (p.fontSize != this.fontSize)
				return false;
			if (p.fontStyle != this.fontStyle)
				return false;
			if (p.stringColor.equals(this.stringColor) == false)
				return false;
			if (p.backgroundPaint.equals(this.backgroundPaint) == false)
			    return false;
			if (p.symbolSpan != this.symbolSpan)
				return false;
			if (p.xAxis.equals(this.xAxis) == false)
				return false;
			if (p.yAxis.equals(this.yAxis) == false)
				return false;
			if (p.visibleElementGroupList.equals(this.visibleElementGroupList) == false) {
				return false;
			}

			return true;
		}

		/**
		 * Returns a string representation.
		 * @return
		 *         a string representation
		 */
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("[x=");
			sb.append(this.x);
			sb.append(", y=");
			sb.append(this.y);
			sb.append(", visible=");
			sb.append(this.visible);
			sb.append(", frameLineVisible=");
			sb.append(this.frameLineVisible);
			sb.append(", frameLineWidth=");
			sb.append(this.frameLineWidth);
			sb.append(", frameLineColor=");
			sb.append(this.frameLineColor);
			sb.append(", fontName=");
			sb.append(this.fontName);
			sb.append(", fontSize=");
			sb.append(this.fontSize);
			sb.append(", fontStyle=");
			sb.append(this.fontStyle);
			sb.append(", stringColor=");
			sb.append(this.stringColor);
                        sb.append(", innerColor=");
                        sb.append(this.backgroundPaint.getColor());
                        sb.append(", transparent=");
                        sb.append(this.backgroundPaint.getTransparencyPercent());
			sb.append(", grouopSetList=");
			sb.append(this.visibleElementGroupList.toString());
			sb.append("]");
			return sb.toString();
		}

	}

	/**
	 * Called when the key is pressed.
	 */
	public void keyPressed(KeyEvent e) {
	}

	/**
	 * Called when the key is released.
	 */
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * Called when the key is typed.
	 */
	public void keyTyped(KeyEvent e) {
		Object source = e.getSource();
		char c = e.getKeyChar();

		// if the text field is visible
		if (source.equals(this.mTextField)) {

			// hide the text field
			if (c == KeyEvent.VK_ESCAPE) {
				this.hideEditField();
				this.clearFocusedGroup();
			}
		}
	}

	protected SGIElementGroupSetSXY createSXYGroupSetInstance(
			SGISXYTypeSingleData dataSXY) {
		return new ElementGroupSetInLegendSXY((SGData) dataSXY);
	}

	protected SGIElementGroupSetVXY createVXYGroupSetInstance(
			SGIVXYTypeData dataVXY) {
		return new ElementGroupSetInLegendVXY((SGData) dataVXY);
	}

	protected SGIElementGroupSetVXY createGridVXYGroupSetInstance(
			SGIVXYTypeData dataVXY) {
		return new ElementGroupSetInLegendVXY((SGData) dataVXY);
	}

	protected SGIElementGroupSetMultipleSXY createMultipleSXYGroupSetInstance(
			SGISXYTypeMultipleData dataMultSXY) {
		return new ElementGroupSetInLegendMultipleSXY((SGData) dataMultSXY);
	}

	protected SGIElementGroupSetSXYZ createSXYZGroupSetInstance(
			SGISXYZTypeData dataSXYZ) {
		return new ElementGroupSetInLegendSXYZ((SGData) dataSXYZ);
	}

	/**
	 * Sets the dialog owner this figure element.
	 *
	 * @param frame
	 *            the dialog owner
	 * @return true if succeeded
	 */
	public boolean setDialogOwner(final Frame frame) {
		if (super.setDialogOwner(frame) == false) {
			return false;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (mPropertyDialog == null) {
					mPropertyDialog = new SGLegendDialog(mDialogOwner, true);
				}
			}
		});
		return true;
	}

	/**
	 * Called when the text string in the text field is updated.
	 *
	 * @param e
	 *          a document event
	 */
	public void changedUpdate(DocumentEvent e) {
		this.updateLabelTextField();
	}

	/**
	 * Called when the text string in the text field is updated.
	 *
	 * @param e
	 *          a document event
	 */
	public void insertUpdate(DocumentEvent e) {
		this.updateLabelTextField();
	}

	/**
	 * Called when the text string in the text field is updated.
	 *
	 * @param e
	 *          a document event
	 */
	public void removeUpdate(DocumentEvent e) {
		this.updateLabelTextField();
	}

	/**
	 * Update the text field.
	 *
	 */
	private void updateLabelTextField() {

		// create the font
		final Font font = new Font(this.getFontName(), this.getFontStyle(),
				(int) (this.getFontSize() * this.getMagnification()));

		// update the text field
		this.updateTextField(this.mTextField, font);
	}

	/**
	 * Called when the caret in the text field is update.
	 */
	public void caretUpdate(final CaretEvent e) {
	}

	/**
	 * Called when menu items in the menu bar is selected.
	 *
	 */
	public void onMenuSelected() {
		if (this.mTextField.isVisible()) {
			this.closeTextField();
		}
	}

	private class ElementGroupSetInLegendSXYZ extends ElementGroupSetInLegend
			implements SGIElementGroupSetSXYZ,
			SGISXYZDataConstants, SGISXYZDataDialogObserver {

		protected ElementGroupSetInLegendSXYZ(SGData data) {
			super(data);
		}

	    /**
	     * Returns the figure element.
	     * 
	     * @return the figure element
	     */
	    public SGFigureElementForData getFigureElement() {
	    	return SGFigureElementLegend.this;
	    }

		boolean onMouseClicked(MouseEvent e) {
			return false;
		}

		/**
		 * Paint the symbol in legend.
		 */
		void paintSymbol(Graphics2D g2d) {
			// Color Map
			ElementGroupPseudocolorMap groupBar = SGFigureElementLegend.this
					.getColorMap(this);
			if (groupBar != null) {
				if (groupBar.isVisible()) {
					groupBar.paintElement(g2d);
				}
			}
		}

		public boolean addDrawingElementGroup(int type) {
			SGElementGroup group = null;
			if (type == SGIElementGroupConstants.RECTANGLE_GROUP) {
				SGElementGroupPseudocolorMap colorMap = new ElementGroupPseudocolorMap();
				colorMap.setColorBarModel(SGFigureElementLegend.this
						.getColorBarModel());
				group = colorMap;
			} else {
				throw new Error();
			}
			this.addDrawingElementGroup(group);
			return true;
		}

		public SGElementGroupPseudocolorMap getColorMap() {
			return SGFigureElementLegend.this.getColorMap(this);
		}

		public boolean setZAxis(SGAxis axis) {
			this.mZAxis = axis;
			return true;
		}

		public double getRectangleWidthValue() {
			return this.getColorMap().getWidthValue();
		}

		public double getRectangleHeightValue() {
			return this.getColorMap().getHeightValue();
		}

		public boolean setRectangleWidthValue(double value) {
			this.getColorMap().setWidthValue(value);
			return true;
		}

		public boolean setRectangleHeightValue(double value) {
			this.getColorMap().setHeightValue(value);
			return true;
		}

		public boolean hasValidRectangleWidthValue(int config, Number value) {
			final SGAxis axis = (config == -1) ? this.mXAxis : mAxisElement
					.getAxisInPlane(config);
			final double v = (value != null) ? value.doubleValue() : this
					.getRectangleWidthValue();
			return axis.isValidValue(v);
		}

		public boolean hasValidRectangleHeightValue(int config, Number value) {
			final SGAxis axis = (config == -1) ? this.mYAxis : mAxisElement
					.getAxisInPlane(config);
			final double v = (value != null) ? value.doubleValue() : this
					.getRectangleHeightValue();
			return axis.isValidValue(v);
		}

		public int getXAxisLocation() {
			return mAxisElement.getLocationInPlane(this.getXAxis());
		}

		public int getYAxisLocation() {
			return mAxisElement.getLocationInPlane(this.getYAxis());
		}

		public boolean setXAxisLocation(int location) {
			this.mXAxis = this.getAxis(location);
			return true;
		}

		public boolean setYAxisLocation(int location) {
			this.mYAxis = this.getAxis(location);
			return true;
		}

		/**
		 * Returns a list of color map groups.
		 *
		 * @return a list of color map groups
		 */
		public List<SGElementGroupPseudocolorMap> getColorMapGroups() {
			List<SGElementGroupPseudocolorMap> retList = new ArrayList<SGElementGroupPseudocolorMap>();
			List<SGElementGroup> list = SGUtilityForFigureElement.getGroups(
					SGElementGroupPseudocolorMap.class, this.mDrawingElementGroupList);
			for (int ii = 0; ii < list.size(); ii++) {
				retList.add((SGElementGroupPseudocolorMap) list.get(ii));
			}
			return retList;
		}

		/**
		 * Returns a color map group which is the first element of an array.
		 *
		 * @return the first element of an array of arrow groups, or null when this
		 *         group set does not have any arrow groups
		 */
		public SGElementGroupPseudocolorMap getColorMapGroup() {
			return (SGElementGroupPseudocolorMap) SGUtilityForFigureElement
					.getGroup(SGElementGroupPseudocolorMap.class,
							this.mDrawingElementGroupList);
		}

	    /**
	     * Sets the properties of element groups.
	     *
	     * @param elementGroupPropertiesList
	     * @return true if succeeded
	     *
	     */
	    protected boolean setElementGroupProperties(List elementGroupPropertiesList) {
	        for (int ii = 0; ii < elementGroupPropertiesList.size(); ii++) {
	            SGProperties gp = (SGProperties) elementGroupPropertiesList.get(ii);
	            SGElementGroup group = null;
	            if (gp instanceof SGElementGroupPseudocolorMap.PseudocolorMapProperties) {
	                group = this.getColorMapGroup();
	            } else {
	                throw new Error("Illegal group property: " + gp);
	            }
	            if (group == null) {
	                continue;
	            }
	            if (group.setProperties(gp) == false) {
	                return false;
	            }
	        }
	        return true;
	    }

	    /**
	     * Updates the size of color map.
	     *
	     * @return true if succeeded
	     */
		public boolean updateColorMapSize() {

	    	SGISXYZTypeData data = (SGISXYZTypeData) this.getData();

	    	SGAxis axisX = this.getXAxis();
	    	final double[] xArray = data.getXValueArray(false);
	    	final double widthValue = SGUtilityForFigureElementJava2D.calcSizeValue(
	    			xArray, axisX, true);

	    	SGAxis axisY = this.getYAxis();
	    	final double[] yArray = data.getYValueArray(false);
	    	final double heightValue = SGUtilityForFigureElementJava2D.calcSizeValue(
	    			yArray, axisY, false);

	        SGElementGroupPseudocolorMap colorMap = (SGElementGroupPseudocolorMap) this.getColorMap();
	        colorMap.setWidthValue(widthValue);
	        colorMap.setHeightValue(heightValue);

			return true;
		}

	    /**
	     * Sets the stride for the x-direction.
	     *
	     * @param stride
	     *           stride of arrays
	     * @return true if succeeded
	     */
		@Override
		public boolean setStrideX(SGIntegerSeriesSet stride) {
			return this.setStrideXToData(stride);
		}

	    /**
	     * Sets the stride for the y-direction.
	     *
	     * @param stride
	     *           stride of arrays
	     * @return true if succeeded
	     */
		@Override
		public boolean setStrideY(SGIntegerSeriesSet stride) {
			return this.setStrideYToData(stride);
		}

		@Override
		public boolean setIndexStride(SGIntegerSeriesSet stride) {
			return this.setIndexStrideToData(stride);
		}

	    /**
	     * Sets the stride for single dimensional data.
	     *
	     * @param stride
	     *           stride of arrays
	     * @return true if succeeded
	     */
		@Override
	    public boolean setSDArrayStride(SGIntegerSeriesSet stride) {
			return this.setSDArrayStrideToData(stride);
	    }

	    /**
	     * Sets the column types and related parameters.
	     *
	     * @param map
	     *           property map
	     * @param result
	     *           results of setting properties
	     * @param cols
	     *           an array of data columns
	     * @return true if succeeded
	     */
		@Override
	    protected boolean setProperties(SGPropertyMap map, SGPropertyResults result,
	    		SGDataColumnInfo[] cols) {
			// do nothing
			return true;
		}

		@Override
		public Map<String, SGProperties> getElementGroupPropertiesMap() {
			return SGUtilityForFigureElementJava2D.getElementGroupPropertiesMap(this);
		}

		@Override
		public boolean setElementGroupPropertiesMap(Map<String, SGProperties> pMap) {
			return SGUtilityForFigureElementJava2D.setElementGroupPropertiesMap(this, pMap);
		}

		@Override
		public boolean getAxisDateMode(final int location) {
			return mAxisElement.getAxisDateMode(location);
		}
	}

	/**
	 * A rectangle in a color map in legend.
	 *
	 */
	protected static class PseudocolorMapRectangleInLegend extends PseudocolorMapRectangle {
		/**
		 * Builds a rectangle in a color map.
		 *
		 * @param group
		 *           a color map
		 */
		public PseudocolorMapRectangleInLegend(SGElementGroupPseudocolorMap group,
				final int index) {
			super(group, index);
		}

		public float getWidth() {
			return this.mGroup.getRectangleWidth();
		}

		public float getHeight() {
			return this.mGroup.getRectangleHeight();
		}

		public boolean setWidth(final float w) {
			return this.mGroup.setRectangleWidth(w);
		}

		public boolean setHeight(final float h) {
			return this.mGroup.setRectangleHeight(h);
		}
	}

	private class ElementGroupPseudocolorMap extends SGElementGroupPseudocolorMapForData implements
			ILegendElement, SGISXYZDataConstants {

		/**
		 * Creates and returns an instance of drawing element.
		 *
		 * @return an instance of drawing element
		 */
		protected SGDrawingElement createDrawingElementInstance(final int index) {
			return new PseudocolorMapRectangleInLegend(this, index);
		}

		public boolean createDrawingElementInLegend() {
			// set size
			final float w = this.getPreferredWidth();
			final float h = this.getPreferredHeight();
			PseudocolorMapRectangle rect = (PseudocolorMapRectangle) this.mDrawingElementArray[0];
			rect.setWidth(w);
			rect.setHeight(h);

			// set the location
			Rectangle2D lRect = getRectOfGroupSet(this.mGroupSet);
			Rectangle2D dRect = this.mBoundsRect;
			final float x = (float) lRect.getX() + 0.50f
					* (float) dRect.getWidth();
			final float y = (float) lRect.getY() + 0.50f
					* (float) lRect.getHeight();
			SGTuple2f position = new SGTuple2f(x, y);
			if (this.setLocation(new SGTuple2f[] { position }) == false) {
				return false;
			}

			return true;
		}

		public int getNumberOfPoints() {
			return 1;
		}

		public float getPreferredHeight() {
			final ElementGroupSetInLegendSXYZ gs = (ElementGroupSetInLegendSXYZ) this.mGroupSet;
			Rectangle2D strRect = gs.mDrawingString.getElementBounds();
			return (float) strRect.getHeight();
		}

		public float getPreferredWidth() {
			return this.getMagnification() * getSymbolSpan();
		}

		private Rectangle2D mBoundsRect = new Rectangle2D.Float();

		public void setDataElementBounds(Rectangle2D rect) {
			this.mBoundsRect = rect;
		}

		/**
		 * A group set that this element group belongs.
		 */
		protected ElementGroupSetInLegend mGroupSet = null;

		public boolean setElementGroupSet(ElementGroupSetInLegend gs) {
			this.mGroupSet = gs;
			return true;
		}

		/**
		 * Paint a color bar.
		 */
		public boolean paintElement(final Graphics2D g2d,
				final Rectangle2D clipRect) {
			//            if (super.paintElement(g2d, clipRect) == false) {
			//                return false;
			//            }
			PseudocolorMapRectangle rect = (PseudocolorMapRectangle) this.mDrawingElementArray[0];
			Rectangle bounds = rect.getElementBounds().getBounds();

			final double x = bounds.getX();
			final double y = bounds.getY();
			final double w = 1.0;
			final double h = bounds.getHeight();
			final int nStart = (int) bounds.getMinX();
			final int nEnd = (int) bounds.getMaxX();

			// get the value range of the color bar
			final double zMin = this.mColorBarModel.getMinValue();
			final double zMax = this.mColorBarModel.getMaxValue();
			final double zRange = zMax - zMin;

			// fill the rectangle
			final int nDiff = nEnd - nStart;
			if (nDiff != 0) {
				for (int ii = 0; ii <= nDiff; ii++) {
					final Rectangle2D thinRect = new Rectangle2D.Double();
					thinRect.setRect(x + ii, y, w, h);
					final double ratio = (double) ii / nDiff;
					final double zValue = zMin + ratio * zRange;
					final Color cl = this.mColorBarModel.evaluate(zValue,
							SGAxis.LINEAR_SCALE);
					g2d.setColor(cl);
					g2d.fill(thinRect);
				}
			}

			// draw bounds
			g2d.setStroke(new BasicStroke(1.0f));
			g2d.setColor(Color.BLACK);
			g2d.draw(bounds);

			// draw anchors if the group set is selected
			if (this.mGroupSet.isViewable()
					&& this.mGroupSet.isSelected()
					&& isSymbolsVisibleAroundFocusedObjects()) {
				SGUtilityForFigureElementJava2D.drawAnchorsOnRectangle(bounds,
						g2d);
			}
			return true;
		}

		/**
		 * Sets the location of each rectangle.
		 *
		 * @param pointArray
		 *          an array of location
		 */
		public boolean setLocation(SGTuple2f[] pointArray) {
			if (this.mDrawingElementArray == null) {
				return true;
			}

			if (pointArray.length != this.mDrawingElementArray.length) {
//				throw new IllegalArgumentException();
				this.initDrawingElement(pointArray);
			}

			this.mLocation.setValues(pointArray[0]);

			return true;
		}

		private SGTuple2f mLocation = new SGTuple2f();

		@Override
		public float getX(int index) {
			return this.mLocation.x;
		}

		@Override
		public float getY(int index) {
			return this.mLocation.y;
		}

	}

	/**
	 * Returns the color bar model.
	 *
	 * @return the color bar model
	 */
	public SGColorMap getColorBarModel() {
		return this.mAxisElement.getColorMap();
	}

//	@Override
//	protected SGIElementGroupSetGridSXYZ createGridSXYZGroupSetInstance(
//			SGISXYZTypeData dataSXYZ) {
//		return new ElementGroupSetInLegendSXYZ((SGData) dataSXYZ);
//	}

	/**
	 * Moves the data of given ID to the top or the bottom in legend.
	 *
	 * @param id
	 *           the ID of an object
	 * @param toTop
	 *           true to move to the top
	 * @return true if succeeded
	 */
	public boolean moveLegendToEnd(final int id, final boolean toTop) {
		ElementGroupSetInLegend child = (ElementGroupSetInLegend) this
				.getVisibleChild(id);
		if (child == null) {
			return false;
		}
		this.moveChildToEnd(id, !toTop);
		updateAllDrawingElements();
		repaint();
		return true;
	}

	/**
	 * Moves the data of given ID to upper or lower in legend.
	 *
	 * @param id
	 *           the ID of an object
	 * @param toUpper
	 *           true to move to upper
	 * @return true if succeeded
	 */
	public boolean moveLegend(final int id, final boolean toUpper) {
		ElementGroupSetInLegend child = (ElementGroupSetInLegend) this
				.getVisibleChild(id);
		if (child == null) {
			return false;
		}
		this.moveChild(id, !toUpper);
		updateAllDrawingElements();
		repaint();
		return true;
	}

	/**
	 * Sets the common properties.
	 *
	 * @param kvList
	 *           a list of key and values of properties
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

			if (COM_LEGEND_AXIS_X.equalsIgnoreCase(key)) {
				final int loc = SGUtility.getAxisLocation(value);
				if (loc == -1) {
					result.putResult(COM_LEGEND_AXIS_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setXAxisLocation(loc) == false) {
					result.putResult(COM_LEGEND_AXIS_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result
						.putResult(COM_LEGEND_AXIS_X,
								SGPropertyResults.SUCCEEDED);
			} else if (COM_LEGEND_AXIS_Y.equalsIgnoreCase(key)) {
				final int loc = SGUtility.getAxisLocation(value);
				if (loc == -1) {
					result.putResult(COM_LEGEND_AXIS_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setYAxisLocation(loc) == false) {
					result.putResult(COM_LEGEND_AXIS_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result
						.putResult(COM_LEGEND_AXIS_Y,
								SGPropertyResults.SUCCEEDED);
			} else if (COM_LEGEND_LOCATION_X.equalsIgnoreCase(key)) {
				Double num = SGUtilityText.getDouble(value);
				if (num == null) {
					result.putResult(COM_LEGEND_LOCATION_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
					result.putResult(COM_LEGEND_LOCATION_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setXValue(num.doubleValue()) == false) {
					result.putResult(COM_LEGEND_LOCATION_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_LEGEND_LOCATION_X,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_LEGEND_LOCATION_Y.equalsIgnoreCase(key)) {
				Double num = SGUtilityText.getDouble(value);
				if (num == null) {
					result.putResult(COM_LEGEND_LOCATION_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
					result.putResult(COM_LEGEND_LOCATION_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setYValue(num.doubleValue()) == false) {
					result.putResult(COM_LEGEND_LOCATION_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_LEGEND_LOCATION_Y,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_LEGEND_VISIBLE.equalsIgnoreCase(key)) {
				Boolean b = SGUtilityText.getBoolean(value);
				if (b == null) {
					result.putResult(COM_LEGEND_VISIBLE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				this.setVisible(b.booleanValue());
				result.putResult(COM_LEGEND_VISIBLE,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_LEGEND_FONT_NAME.equalsIgnoreCase(key)) {
            	final String name = SGUtility.findFontFamilyName(value);
            	if (name == null) {
                    result.putResult(COM_LEGEND_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
				if (this.setFontName(name) == false) {
					result.putResult(COM_LEGEND_FONT_NAME,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_LEGEND_FONT_NAME,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_LEGEND_FONT_STYLE.equalsIgnoreCase(key)) {
				Integer style = SGUtilityText.getFontStyle(value);
				if (style == null) {
					result.putResult(COM_LEGEND_FONT_STYLE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setFontStyle(style.intValue()) == false) {
					result.putResult(COM_LEGEND_FONT_STYLE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_LEGEND_FONT_STYLE,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_LEGEND_FONT_SIZE.equalsIgnoreCase(key)) {
				StringBuffer unit = new StringBuffer();
				Number num = SGUtilityText.getNumber(value, unit);
				if (num == null) {
					result.putResult(COM_LEGEND_FONT_SIZE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setFontSize(num.floatValue(), unit.toString()) == false) {
					result.putResult(COM_LEGEND_FONT_SIZE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_LEGEND_FONT_SIZE,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_LEGEND_FONT_COLOR.equalsIgnoreCase(key)) {
				Color cl = SGUtilityText.getColor(value);
				if (cl != null) {
					if (this.setFontColor(cl) == false) {
						result.putResult(COM_LEGEND_FONT_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				} else {
                	cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(COM_LEGEND_FONT_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setFontColor(cl) == false) {
						result.putResult(COM_LEGEND_FONT_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				}
				result.putResult(COM_LEGEND_FONT_COLOR,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_LEGEND_FRAME_VISIBLE.equalsIgnoreCase(key)) {
				Boolean b = SGUtilityText.getBoolean(value);
				if (b == null) {
					result.putResult(COM_LEGEND_FRAME_VISIBLE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setFrameVisible(b.booleanValue()) == false) {
					result.putResult(COM_LEGEND_FRAME_VISIBLE,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_LEGEND_FRAME_VISIBLE,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_LEGEND_FRAME_LINE_WIDTH.equalsIgnoreCase(key)) {
				StringBuffer unit = new StringBuffer();
				Number num = SGUtilityText.getNumber(value, unit);
				if (num == null) {
					result.putResult(COM_LEGEND_FRAME_LINE_WIDTH,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setFrameLineWidth(num.floatValue(), unit.toString()) == false) {
					result.putResult(COM_LEGEND_FRAME_LINE_WIDTH,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_LEGEND_FRAME_LINE_WIDTH,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_LEGEND_FRAME_COLOR.equalsIgnoreCase(key)) {
				Color cl = SGUtilityText.getColor(value);
				if (cl != null) {
					if (this.setFrameLineColor(cl) == false) {
						result.putResult(COM_LEGEND_FRAME_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				} else {
                	cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(COM_LEGEND_FRAME_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setFrameLineColor(cl) == false) {
						result.putResult(COM_LEGEND_FRAME_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				}
				result.putResult(COM_LEGEND_FRAME_COLOR,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_LEGEND_BACKGROUND_TRANSPARENCY.equalsIgnoreCase(key)) {
			    final Integer num = SGUtilityText.getInteger(value, SGIConstants.percent);
			    if (num == null) {
			        result.putResult(COM_LEGEND_BACKGROUND_TRANSPARENCY, SGPropertyResults.INVALID_INPUT_VALUE);
			        continue;
			    }
			    if (this.setBackgroundTransparent(num.intValue()) == false) {
			        result.putResult(COM_LEGEND_BACKGROUND_TRANSPARENCY, SGPropertyResults.INVALID_INPUT_VALUE);
			        continue;
			    }
			    result.putResult(COM_LEGEND_BACKGROUND_TRANSPARENCY, SGPropertyResults.SUCCEEDED);
			} else if (COM_LEGEND_BACKGROUND_COLOR.equalsIgnoreCase(key)) {
				Color cl = SGUtilityText.getColor(value);
				if (cl != null) {
					if (this.setBackgroundColor(cl) == false) {
						result.putResult(COM_LEGEND_BACKGROUND_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				} else {
                	cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(COM_LEGEND_BACKGROUND_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setBackgroundColor(cl) == false) {
						result.putResult(COM_LEGEND_BACKGROUND_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				}
				result.putResult(COM_LEGEND_BACKGROUND_COLOR,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_LEGEND_SYMBOL_SPAN.equalsIgnoreCase(key)) {
				StringBuffer unit = new StringBuffer();
				Number num = SGUtilityText.getNumber(value, unit);
				if (num == null) {
					result.putResult(COM_LEGEND_SYMBOL_SPAN,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setSymbolSpan(num.floatValue(), unit.toString()) == false) {
					result.putResult(COM_LEGEND_SYMBOL_SPAN,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_LEGEND_SYMBOL_SPAN,
						SGPropertyResults.SUCCEEDED);
			}
		}

		if (this.updateAllDrawingElements() == false) {
			return null;
		}

		// commit the changes
		if (this.commit() == false) {
			return null;
		}
		this.notifyChange();
		this.notifyToRoot();
		this.repaint();

		return result;
	}

    /**
     * Returns a list of focused data objects in sorted order.
     * 
     * @return a list of focused data objects
     */
	@Override
    public List<SGData> getFocusedDataListInSortedOrder() {
    	List<SGData> dataList = new ArrayList<SGData>();
    	for (int ii = 0; ii < this.mChildList.size(); ii++) {
    		ElementGroupSetInLegend gs = (ElementGroupSetInLegend) this.mChildList.get(ii);
    		if (!gs.isVisible()) {
    			continue;
    		}
    		if (gs.isSelected()) {
    			SGData data = this.getData(gs);
    			dataList.add(data);
    		}
    	}
    	return dataList;
    }

    /**
     * Returns a text string of the commands.
     * Overrode to get legend command.
     * 
     * @return a text string of the commands
     */
	@Override
	public String getCommandString(SGExportParameter params) {
		StringBuffer sb = new StringBuffer();
		SGPropertyMap map = this.getPropertyMap();
		sb.append(COM_LEGEND);
		sb.append('(');
		sb.append(map.toString());
		sb.append(")\n");
		return sb.toString();
	}
	
    /**
     * Creates and returns the map of properties.
     * 
     * @return the map of properties
     */
	public SGPropertyMap getPropertyMap() {
		SGPropertyMap map = new SGPropertyMap();

		// visible
		SGPropertyUtility.addProperty(map, COM_LEGEND_VISIBLE, this.isVisible());

		// axis
		SGPropertyUtility.addProperty(map, COM_LEGEND_AXIS_X, 
				this.mAxisElement.getLocationName(this.mXAxis));
		SGPropertyUtility.addProperty(map, COM_LEGEND_AXIS_Y, 
				this.mAxisElement.getLocationName(this.mYAxis));
		
		// location
		SGPropertyUtility.addProperty(map, COM_LEGEND_LOCATION_X, this.getXValue());
		SGPropertyUtility.addProperty(map, COM_LEGEND_LOCATION_Y, this.getYValue());

		// font
		SGPropertyUtility.addProperty(map, COM_LEGEND_FONT_NAME, this.getFontName());
		SGPropertyUtility.addProperty(map, COM_LEGEND_FONT_STYLE, 
				SGUtilityText.getFontStyleName(this.getFontStyle()));
		SGPropertyUtility.addProperty(map, COM_LEGEND_FONT_SIZE, 
				SGUtility.getExportFontSize(this.getFontSize()), FONT_SIZE_UNIT);
		SGPropertyUtility.addProperty(map, COM_LEGEND_FONT_COLOR, this.getFontColor());

		// frame
		SGPropertyUtility.addProperty(map, COM_LEGEND_FRAME_VISIBLE, this.isFrameVisible());
		SGPropertyUtility.addProperty(map, COM_LEGEND_FRAME_LINE_WIDTH, 
				SGUtility.getExportLineWidth(
						this.getFrameLineWidth(LINE_WIDTH_UNIT)), LINE_WIDTH_UNIT);
		SGPropertyUtility.addProperty(map, COM_LEGEND_FRAME_COLOR, this.getFrameColor());
		
		// background
		SGPropertyUtility.addProperty(map, COM_LEGEND_BACKGROUND_TRANSPARENCY, 
				this.getBackgroundTransparency(), percent);
		SGPropertyUtility.addProperty(map, COM_LEGEND_BACKGROUND_COLOR, 
				this.getBackgroundColor());

		// span
		SGPropertyUtility.addProperty(map, COM_LEGEND_SYMBOL_SPAN, 
				SGUtility.getExportValue(this.getSymbolSpan(SYMBOL_SPAN_UNIT), 
						SYMBOL_SPAN_MINIMAL_ORDER), SYMBOL_SPAN_UNIT);

		return map;
	}

    /**
     * Creates and returns the map of properties for the property file.
     * 
     * @return the map of properties
     */
    public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	
		// visible
		SGPropertyUtility.addProperty(map, KEY_LEGEND_VISIBLE, this.isVisible());

		// axis
		SGPropertyUtility.addProperty(map, KEY_X_AXIS_POSITION, 
				this.mAxisElement.getLocationName(this.mXAxis));
		SGPropertyUtility.addProperty(map, KEY_Y_AXIS_POSITION, 
				this.mAxisElement.getLocationName(this.mYAxis));

		// location
		SGPropertyUtility.addProperty(map, KEY_X_VALUE, this.getXValue());
		SGPropertyUtility.addProperty(map, KEY_Y_VALUE, this.getYValue());

		// font
		SGPropertyUtility.addProperty(map, KEY_FONT_NAME, this.getFontName());
		SGPropertyUtility.addProperty(map, KEY_FONT_STYLE, 
				SGUtilityText.getFontStyleName(this.getFontStyle()));
		SGPropertyUtility.addProperty(map, KEY_FONT_SIZE, 
				SGUtility.getExportFontSize(this.getFontSize()), FONT_SIZE_UNIT);
		SGPropertyUtility.addProperty(map, KEY_STRING_COLORS, this.getFontColor());

		// frame
		SGPropertyUtility.addProperty(map, KEY_FRAME_VISIBLE, this.isFrameVisible());
		SGPropertyUtility.addProperty(map, KEY_FRAME_LINE_WIDTH, 
				SGUtility.getExportLineWidth(
						this.getFrameLineWidth(LINE_WIDTH_UNIT)), LINE_WIDTH_UNIT);
		SGPropertyUtility.addProperty(map, KEY_FRAME_LINE_COLOR, this.getFrameColor());

		// background
		SGPropertyUtility.addProperty(map, KEY_BACKGROUND_TRANSPARENT, 
				this.getBackgroundTransparency(), percent);
		SGPropertyUtility.addProperty(map, KEY_BACKGROUND_COLOR, 
				this.getBackgroundColor());

		// span
		SGPropertyUtility.addProperty(map, KEY_SYMBOL_SPAN, 
				SGUtility.getExportValue(this.getSymbolSpan(SYMBOL_SPAN_UNIT), 
						SYMBOL_SPAN_MINIMAL_ORDER), SYMBOL_SPAN_UNIT);

    	return map;
    }

	@Override
	public boolean getAxisDateMode(final int location) {
		return this.mAxisElement.getAxisDateMode(location);
	}
}

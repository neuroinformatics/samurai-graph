package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGICopyable;
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
import jp.riken.brain.ni.samuraigraph.base.SGINode;
import jp.riken.brain.ni.samuraigraph.base.SGISelectable;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTransparentPaint;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYZTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGIVXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFMultipleData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** The legend which displays symbols of data objects. */
public class SGFigureElementLegend extends SGFigureElementForData
    implements SGIFigureElementLegend,
        SGIStringConstants,
        CaretListener,
        DocumentListener,
        KeyListener,
        ActionListener,
        SGILegendConstants,
        SGIMovable,
        SGILegendDialogObserver {

  /** The graph figure element. */
  private SGIFigureElementGraph mGraphElement = null;

  /** The x-axis. */
  SGAxis mXAxis = null;

  /** The y-axis. */
  SGAxis mYAxis = null;

  /** Relative x-coordinate from the origin of the graph rectangle at the default magnification. */
  float mLegendX = 0.0f;

  /** Relative y-coordinate from the origin of the graph rectangle at the default magnification. */
  float mLegendY = 0.0f;

  /** The width at the default magnification. */
  private float mLegendWidth = 0.0f;

  /** The height at the default magnification. */
  private float mLegendHeight = 0.0f;

  /** Font size for data names. */

  /** Font style for data names. */

  /** Font name for data names. */

  /** Font color for data names. */

  /** Span width of symbols. */

  /** Line width of frame lines. */

  /** Color of frame lines. */

  /** Background paint. */

  /** The flag for visibility of legend. */

  /** The flag for visibility of frame lines. */

  /** Temporary properties. */
  final SGFigureElementLegendStyle mStyleHolder = new SGFigureElementLegendStyle();

  private final SGFigureElementLegendPropertyIO mPropertyIO =
      new SGFigureElementLegendPropertyIO(this);

  private SGProperties mTemporaryProperties = null;

  /** The pop-up menu. */
  private JPopupMenu mPopupMenu = null;

  /** A property dialog for legend. */
  private SGPropertyDialog mPropertyDialog = null;

  /** A text field to edit data names. */
  private JTextField mTextField = new JTextField();

  /** A menu command string to hide the legend. */
  public static final String MENUCMD_HIDE = "Hide";

  /** Default constructor. */
  public SGFigureElementLegend() {
    super();
    this.initEditField();
    this.init();
  }

  /** Initialization */
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

  /** Dispose this object. */
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
    this.mStyleHolder.clear();
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
    return p;
  }

  /**
   * Create a pop-up menu.
   *
   * @return a pop-up menu
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
   * @return
   */
  public String getClassDescription() {
    return "Legend";
  }

  /**
   * Add a data object.
   *
   * @param data a data object
   * @param name the name of the data object
   * @return true if succeeded
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
   *
   * @param data a data object
   * @param name the name of the data object
   * @param id the ID to set
   * @param infoMap the information map of data object
   * @return true if succeeded
   */
  @Override
  public boolean addData(
      final SGData data, final String name, final int id, final Map<String, Object> infoMap) {
    return this.addData(data, name, id, true, infoMap);
  }

  private boolean addData(
      final SGData data,
      final String name,
      final int id,
      final boolean withID,
      final Map<String, Object> infoMap) {

    if (super.addData(data, name) == false) {
      return false;
    }

    if (this.mGraphElement == null) {
      throw new Error("mGraphElement==null");
    }

    // create legend
    ElementGroupSetInLegend legend =
        (ElementGroupSetInLegend) this.createGroupSet(data, name, infoMap);
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
   * @param data added data.
   * @param name the name set to the data
   * @param p properties set to be data.
   * @return true if succeeded
   */
  public boolean addData(final SGData data, final String name, final SGProperties p) {

    if (this.mGraphElement == null || data == null) {
      throw new Error("mGraphElement==null || data==null");
    }

    if (super.addData(data, name, p) == false) {
      return false;
    }

    // create legend
    ElementGroupSetInLegend legend =
        (ElementGroupSetInLegend) this.createGroupSet(data, name, null);
    if (legend == null) {
      return false;
    }

    // add to list
    this.addToList(legend);

    // set properties
    if (legend.setProperties(p) == false) {
      return false;
    }

    if (SGDataDataTypeUtility.isSXYTypeData(data.getDataType())
        && SGDataDataTypeUtility.isNetCDFData(data)) {
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
      SGSXYNetCDFData ncData = (SGSXYNetCDFData) data;
      boolean vertical = ncData.isXVariableCoordinate();
      if (legend instanceof ElementGroupSetInLegendMultipleSXY) {
        ElementGroupSetInLegendMultipleSXY gs = (ElementGroupSetInLegendMultipleSXY) legend;
        if (gs.isBarVertical() != vertical) {
          gs.setBarVertical(vertical);
        }
      } else if (legend instanceof ElementGroupSetInLegendSXY) {
        ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY) legend;
        if (gs.isBarVertical() != vertical) {
          gs.setBarVertical(vertical);
        }
      }
    } else if (data instanceof SGSXYNetCDFMultipleData) {
      SGSXYNetCDFMultipleData ncData = (SGSXYNetCDFMultipleData) data;
      boolean vertical = ncData.isXVariableCoordinate();
      if (legend instanceof ElementGroupSetInLegendMultipleSXY) {
        ElementGroupSetInLegendMultipleSXY gs = (ElementGroupSetInLegendMultipleSXY) legend;
        if (gs.isBarVertical() != vertical) {
          gs.setBarVertical(vertical);
        }
      } else if (legend instanceof ElementGroupSetInLegendSXY) {
        ElementGroupSetInLegendSXY gs = (ElementGroupSetInLegendSXY) legend;
        if (gs.isBarVertical() != vertical) {
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
    final float x =
        this.getGraphRectX()
            + this.getGraphRectWidth()
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

  /** */
  public boolean getFocusedObjectsList(List<SGISelectable> list) {
    if (this.isSelected()) {
      list.add(this);
    }
    ArrayList<ElementGroupSetInLegend> lList = this.getVisibleLegendList();
    for (int ii = 0; ii < lList.size(); ii++) {
      ElementGroupSetInLegend gs = lList.get(ii);
      if (gs.isSelected()) {
        list.add(gs);
      }
    }
    return true;
  }

  /**
   * Hide the selected objects.
   *
   * @return true if selected
   */
  public boolean hideSelectedObjects() {
    if (super.hideSelectedObjects() == false) {
      return false;
    }

    this.updateAllDrawingElements();
    this.repaint();

    return true;
  }

  /** */
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

  /** */
  private boolean mSelectedFlag = false;

  /**
   * Sets selected or deselected the object.
   *
   * @param b true selects and false deselects the object
   */
  @Override
  public void setSelected(final boolean b) {
    this.mSelectedFlag = b;
    this.mTemporaryProperties = b ? this.getProperties() : null;
  }

  /** */
  public boolean isSelected() {
    return this.mSelectedFlag;
  }

  /**
   * Returns the x-coordinate of the legend in the figure.
   *
   * @return the x-coordinate of the legend in the figure
   */
  public float getLegendX() {
    return this.mGraphRectX + this.mLegendX * this.mMagnification;
  }

  /**
   * Returns the y-coordinate of the legend in the figure.
   *
   * @return the y-coordinate of the legend in the figure
   */
  public float getLegendY() {
    return this.mGraphRectY + this.mLegendY * this.mMagnification;
  }

  /**
   * Returns the width of the legend.
   *
   * @return width of the legend
   */
  public float getLegendWidth() {
    return this.mLegendWidth;
  }

  /**
   * Returns the height of the legend.
   *
   * @return height of the legend
   */
  public float getLegendHeight() {
    return this.mLegendHeight;
  }

  /**
   * Returns the location of the legend in the figure.
   *
   * @return the location of the legend in the figure
   */
  public Point2D getLegendLocation() {
    Point2D pos = new Point2D.Float(this.getLegendX(), this.getLegendY());
    return pos;
  }

  /**
   * Sets the location of the legend in the figure.
   *
   * @param x the x-coordinate
   * @param y the y-coordinate
   * @return true if succeeded
   */
  public boolean setLegendLocation(float x, float y) {
    this.mLegendX = (x - this.mGraphRectX) / this.mMagnification;
    this.mLegendY = (y - this.mGraphRectY) / this.mMagnification;
    return true;
  }

  /**
   * Sets the x-value for the location.
   *
   * @param value the x-value to set
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

    // if values from the dialog is different from the current values,
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
   * @param value the y-value to set
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

    // if values from the dialog is different from the current values,
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
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidXAxisValue(final int config, final Number value) {
    final SGAxis axis = (config == -1) ? this.mXAxis : this.mAxisElement.getAxisInPlane(config);
    final double v = (value != null) ? value.doubleValue() : this.getXValue();
    return axis.isValidValue(v);
  }

  /**
   * @param config
   * @param value
   * @return
   */
  public boolean hasValidYAxisValue(final int config, final Number value) {
    final SGAxis axis = (config == -1) ? this.mYAxis : this.mAxisElement.getAxisInPlane(config);
    final double v = (value != null) ? value.doubleValue() : this.getYValue();
    return axis.isValidValue(v);
  }

  /** */
  public void translate(final float dx, final float dy) {
    this.setLegendLocation(this.getLegendX() + dx, this.getLegendY() + dy);
    this.updateAllDrawingElements();
  }

  /**
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

  /** Initialize the text field. */
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
   *
   * @return true if succeeded
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
   *
   * @param ori an origin of this clearance
   * @return true if succeeded
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

  /** */
  public boolean prepare() {
    this.mTemporaryProperties = this.getProperties();
    return true;
  }

  /** */
  public SGProperties getProperties() {
    return this.mPropertyIO.getProperties();
  }

  /** */
  public boolean getProperties(final SGProperties p) {
    return this.mPropertyIO.getProperties(p);
  }

  /** */
  public boolean setProperties(final SGProperties p) {
    return this.mPropertyIO.setProperties(p);
  }

  /** */
  private boolean setCommonProperties(final LegendProperties p) {
    return this.mPropertyIO.setCommonProperties(p);
  }

  private static final float MARGIN_HORIZONTAL = 6.0f;

  private static final float MARGIN_VERTICAL = 6.0f;

  private static final float marginTop = MARGIN_VERTICAL;

  private static final float marginBottom = MARGIN_VERTICAL;

  private static final float marginLeft = MARGIN_HORIZONTAL;

  private static final float marginRight = MARGIN_HORIZONTAL;

  private static final float spaceDataAndString = MARGIN_HORIZONTAL;

  private static final float spaceLegend = MARGIN_VERTICAL;

  /**
   * Update all drawing elements.
   *
   * @return true if succeeded
   */
  boolean updateAllDrawingElements() {

    // the magnification
    final float mag = this.mMagnification;

    // get an array of visible legend
    final List<ElementGroupSetInLegend> visibleGroupSetList = this.getViewableLegendList();
    final int num = visibleGroupSetList.size();
    final ElementGroupSetInLegend[] legendArray = new ElementGroupSetInLegend[num];
    for (int ii = 0; ii < num; ii++) {
      legendArray[ii] = (ElementGroupSetInLegend) visibleGroupSetList.get(ii);
    }

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
    final float legendWidth = dataWidthMax + stringWidthMax / mag + spaceDataAndString;

    // height array of drawing elements
    final double[] dataHeightArray = new double[num];
    for (int ii = 0; ii < num; ii++) {
      dataHeightArray[ii] = legendArray[ii].getMaxDataElementHeight();
    }

    // total height array
    float[] legendHeightArray = new float[num];
    for (int ii = 0; ii < num; ii++) {
      legendHeightArray[ii] = (float) Math.max(dataHeightArray[ii], stringHeightArray[ii]);
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
    final float sx = this.getLegendX() + mag * (marginLeft + dataWidthMax + spaceDataAndString);
    for (int ii = 0; ii < num; ii++) {
      final float sy =
          (float) legendRectArray[ii].getY()
              - 0.50f * stringHeightArray[ii]
              + 0.50f * legendHeightArray[ii]
              + (float) stringBoundsArray[ii].getY();
      legendArray[ii].getStringElement().setLocation(sx, sy);
    }

    // set to attributes
    this.mLegendWidth = marginLeft + legendWidth + marginRight;
    this.mLegendHeight = marginTop + legendHeight + marginBottom;

    return true;
  }

  /** */
  public boolean setGraphRect(final float x, final float y, final float width, final float height) {
    super.setGraphRect(x, y, width, height);

    if (this.closeTextField() == false) {
      return false;
    }

    if (this.updateAllDrawingElements() == false) {
      return false;
    }

    return true;
  }

  /** */
  public void paintGraphics(Graphics g, boolean clip) {
    final Graphics2D g2d = (Graphics2D) g;

    if (this.isVisible()) {

      List<ElementGroupSetInLegend> list = this.getViewableLegendList();
      if (list.size() != 0) {

        // paint background
        if (this.getBackgroundTransparency() != SGTransparentPaint.ALL_TRANSPARENT_VALUE) {
          Rectangle rect = this.getLegendRect().getBounds();
          g2d.setPaint(this.mStyleHolder.getBackgroundPaint().getPaint(rect));
          g2d.fill(rect);
        }

        // draw each legend
        for (int ii = 0; ii < list.size(); ii++) {
          ElementGroupSetInLegend groupSet = (ElementGroupSetInLegend) list.get(ii);
          groupSet.paintGraphics2D(g2d);
        }

        // draw frame
        if (this.mStyleHolder.isFrameVisible()) {
          this.drawLegendFrameLines(g2d);
        }

        // draw anchors around all objects
        if (this.mSymbolsVisibleFlagAroundAllObjects) {
          ArrayList<Point2D> pList = this.getAnchorPointList();
          SGUtilityForFigureElementJava2D.drawAnchorAsChildObject(pList, g2d);
        }

        // draw anchors around focused objects
        if (this.mSymbolsVisibleFlagAroundFocusedObjects && this.isSelected()) {
          ArrayList<Point2D> pList = this.getAnchorPointList();
          SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(pList, g2d);
        }
      }
    }
  }

  /**
   * Returns a list of points to draw anchors.
   *
   * @return a list of points to draw anchors
   */
  private ArrayList<Point2D> getAnchorPointList() {
    ArrayList<Point2D> list = new ArrayList<Point2D>();

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

    g2d.setPaint(this.mStyleHolder.getFrameColor());

    g2d.setStroke(
        new BasicStroke(
            this.mMagnification * this.mStyleHolder.getFrameLineWidth(),
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER));

    g2d.draw(this.getLegendRect().getBounds());
  }

  /** */
  public boolean contains(final int x, final int y) {
    Rectangle2D rect =
        new Rectangle2D.Float(
            this.getLegendX(), this.getLegendY(), this.mLegendWidth, this.mLegendHeight);
    return rect.contains(x, y);
  }

  /**
   * Returns a set of available child objects in the histories.
   *
   * @return a set of available child objects in the histories
   */
  protected Set<SGIChildObject> getAvailableChildSet() {
    Set<SGIChildObject> set = new HashSet<SGIChildObject>();
    List<SGProperties> mList = this.getMementoList();
    for (int ii = 0; ii < mList.size(); ii++) {
      LegendProperties p = (LegendProperties) mList.get(ii);
      set.addAll(p.visibleElementGroupList);
    }
    return set;
  }

  /**
   * Synchronize to the other figure element.
   *
   * @param element a figure element
   * @param msg a message
   * @return true if succeeded
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
   *
   * @param gElement the graph element
   * @param msg a message
   * @return true if succeeded
   */
  private boolean synchronizeToGraphElement(
      final SGIFigureElementGraph gElement, final String msg) {
    return this.synchronizeToDataElement(gElement, msg);
  }

  /**
   * Synchronize to the axis element.
   *
   * @param aElement the axis element
   * @param msg the message
   * @return true if succeeded
   */
  private boolean synchronizeToAxisElement(final SGIFigureElementAxis aElement, final String msg) {
    if (this.isNotificationMessage(msg)) {
      // synchronize the color bar
      final SGColorMap model = this.getColorBarModel();
      for (int ii = 0; ii < this.mChildList.size(); ii++) {
        SGIChildObject groupSet = (SGIChildObject) this.mChildList.get(ii);
        if (groupSet instanceof ElementGroupSetInLegendSXYZ) {
          ElementGroupSetInLegendSXYZ legend = (ElementGroupSetInLegendSXYZ) groupSet;
          ElementGroupPseudocolorMap colorMap =
              (ElementGroupPseudocolorMap) legend.getColorMapGroup();
          colorMap.setColorBarModel(model);
        }
      }
    }
    return true;
  }

  /**
   * Synchronize the element given by the argument.
   *
   * @param element An object to be synchronized.
   */
  public boolean synchronizeArgument(final SGIFigureElement element, final String msg) {
    // this shouldn't happen
    throw new Error();
  }

  /**
   * Returns whether the data object is selected.
   *
   * @param data a data object
   * @return true if selected
   */
  public boolean isDataSelected(final SGData data) {
    if (data == null) {
      throw new IllegalArgumentException("data == null");
    }
    ElementGroupSetInLegend gs = (ElementGroupSetInLegend) this.getElementGroupSet(data);
    if (gs == null) {
      throw new Error("Data is not found.");
    }
    return gs.isSelected();
  }

  /**
   * Sets the magnification.
   *
   * @param mag the magnification to set
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

  /** */
  public boolean getMarginAroundGraphRect(SGTuple2f topAndBottom, SGTuple2f leftAndRight) {

    if (super.getMarginAroundGraphRect(topAndBottom, leftAndRight) == false) {
      return false;
    }

    Rectangle2D graphRect = this.getGraphRect();
    Rectangle2D lRect = this.getLegendRect();
    if (lRect.getWidth() < Double.MIN_VALUE || lRect.getHeight() < Double.MIN_VALUE) {
      return true;
    }

    ArrayList<Rectangle2D> list = new ArrayList<Rectangle2D>();
    list.add(graphRect);
    if (this.isVisible()) {
      list.add(lRect);
    }

    Rectangle2D uniRect = SGUtility.createUnion(list);

    final float top = (float) (graphRect.getY() - uniRect.getY());
    final float bottom =
        (float)
            ((uniRect.getY() + uniRect.getHeight()) - (graphRect.getY() + graphRect.getHeight()));
    final float left = (float) (graphRect.getX() - uniRect.getX());
    final float right =
        (float) ((uniRect.getX() + uniRect.getWidth()) - (graphRect.getX() + graphRect.getWidth()));

    topAndBottom.x += top;
    topAndBottom.y += bottom;
    leftAndRight.x += left;
    leftAndRight.y += right;

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

  /** */
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
            ElementGroupSetInLegend legend = (ElementGroupSetInLegend) gList.get(ii);
            Rectangle2D sRect = legend.getStringElement().getElementBounds();

            // show the text field to edit the name
            if (sRect.contains(x, y)) {
              if (mPressedPoint == null) {
                return false;
              }
              this.mFocusedGroup = legend;
              final int tx = mPressedPoint.x - (int) sRect.getX();
              final int ty = mPressedPoint.y - (int) sRect.getY();
              this.showEditField(this.mTextField, legend.mDrawingString, tx, ty);
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
            ElementGroupSetInLegend legend = (ElementGroupSetInLegend) gList.get(ii);
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
          ElementGroupSetInLegend legend = (ElementGroupSetInLegend) gList.get(ii);

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

  /** Clear all focused data. */
  public void clearAllFocusedData() {
    List<SGIChildObject> lList = this.getVisibleChildList();
    for (int ii = 0; ii < lList.size(); ii++) {
      ElementGroupSetInLegend gs = (ElementGroupSetInLegend) lList.get(ii);
      gs.setSelected(false);
    }
    this.clearFocusedObjectsSub();
  }

  /** */
  private ElementGroupSetInLegend mFocusedGroup = null;

  /**
   * Returns a list of child nodes.
   *
   * @return a list of chid nodes
   */
  public ArrayList<SGINode> getChildNodes() {
    return new ArrayList<SGINode>();
  }

  /**
   * Returns the property dialog.
   *
   * @return a property dialog
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

  /** */
  public String getFontName() {
    return this.mStyleHolder.getFontName();
  }

  /** */
  public int getFontStyle() {
    return this.mStyleHolder.getFontStyle();
  }

  /** */
  public float getFontSize() {
    return this.mStyleHolder.getFontSize();
  }

  /** */
  public float getFontSize(final String unit) {
    return this.mStyleHolder.getFontSize(unit);
  }

  /** */
  public float getFrameLineWidth() {
    return this.mStyleHolder.getFrameLineWidth();
  }

  /** */
  public float getFrameLineWidth(final String unit) {
    return this.mStyleHolder.getFrameLineWidth(unit);
  }

  /** */
  public Color getFrameColor() {
    return this.mStyleHolder.getFrameColor();
  }

  /** */
  @Override
  public Color getBackgroundColor() {
    return this.mStyleHolder.getBackgroundColor();
  }

  @Override
  public int getBackgroundTransparency() {
    return this.mStyleHolder.getBackgroundTransparency();
  }

  /** */
  public Color getFontColor() {
    return this.mStyleHolder.getFontColor();
  }

  /**
   * Returns the symbol span.
   *
   * @return the symbol span
   */
  public float getSymbolSpan() {
    return this.mStyleHolder.getSymbolSpan();
  }

  /**
   * @return
   */
  public float getSymbolSpan(final String unit) {
    return this.mStyleHolder.getSymbolSpan(unit);
  }

  /**
   * Sets the symbol span.
   *
   * @param span the symbol span to set
   * @return true if succeeded
   */
  public boolean setSymbolSpan(final float span) {
    return this.mStyleHolder.setSymbolSpan(span);
  }

  /**
   * Sets the symbol span in a given unit.
   *
   * @param span the symbol span to set
   * @param unit the unit for the symbol span
   * @return true if succeeded
   */
  public boolean setSymbolSpan(final float span, final String unit) {
    return this.mStyleHolder.setSymbolSpan(span, unit);
  }

  /** */
  private Point mLegendLocation = null;

  /** */
  private boolean isMoved() {
    final boolean bx = ((int) this.mLegendX == (int) this.mLegendLocation.getX());
    final boolean by = ((int) this.mLegendY == (int) this.mLegendLocation.getY());
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

  /** */
  private boolean hideEditField() {
    this.mTextField.setText("");
    this.mTextField.setVisible(false);
    return true;
  }

  /**
   * @return
   */
  private boolean clearFocusedGroup() {
    this.mFocusedGroup = null;
    return true;
  }

  /**
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
      this.mLegendLocation = new Point((int) this.mLegendX, (int) this.mLegendY);
      this.mPressedPoint = e.getPoint();
      setMouseCursor(Cursor.MOVE_CURSOR);
      return true;
    }

    this.clearFocusedGroup();

    return false;
  }

  /**
   * @param e
   */
  public boolean onMouseDragged(final MouseEvent e) {
    if (this.mPressedPoint == null) {
      return false;
    }
    return true;
  }

  /** */
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

  // /**
  // *
  // * @return
  // */
  // public boolean setTemporaryPropertiesOfFocusedObjects() {
  // this.mTemporaryProperties = this.getProperties();
  // return true;
  // }

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

  /** */
  public Rectangle2D getLegendRect() {
    final float mag = this.mMagnification;
    Rectangle2D rect =
        new Rectangle2D.Float(
            this.getLegendX(),
            this.getLegendY(),
            mag * this.mLegendWidth,
            mag * this.mLegendHeight);

    return rect;
  }

  /** */
  public boolean isResizable(final double w, final double h) {

    Rectangle2D rect = this.getLegendRect();

    if (w < rect.getWidth() || h < rect.getHeight()) {
      return false;
    }
    return true;
  }

  /**
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
   * @param element the axis element
   * @return true if succeeded
   */
  public boolean setAxisElement(final SGIFigureElementAxis element) {
    this.mAxisElement = element;

    // set axes
    SGAxis xAxis = this.mAxisElement.getAxis(DEFAULT_LEGEND_HORIZONTAL_AXIS);
    SGAxis yAxis = this.mAxisElement.getAxis(DEFAULT_LEGEND_VERTICAL_AXIS);
    this.mXAxis = xAxis;
    this.mYAxis = yAxis;

    return true;
  }

  /** */
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
   * @param location the location of the x-axis to set
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
   * @param location the location of the y-axis to set
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

  /** */
  public boolean setVisible(final boolean b) {
    this.mStyleHolder.setVisible(b);
    return true;
  }

  /**
   * Sets the transparency of the background.
   *
   * @param percentAlpha the alpha value of transparency to set
   * @return true if succeeded
   */
  @Override
  public boolean setBackgroundTransparent(final int percentAlpha) {
    return this.mStyleHolder.setBackgroundTransparent(percentAlpha);
  }

  /**
   * Sets the visibility of the frame lines.
   *
   * @param b a visibility flag to set
   * @return true if succeeded
   */
  public boolean setFrameVisible(final boolean b) {
    this.mStyleHolder.setFrameVisible(b);
    return true;
  }

  /**
   * Sets the frame line width.
   *
   * @param lw the frame line width to set
   * @return true if succeeded
   */
  public boolean setFrameLineWidth(final float lw) {
    return this.mStyleHolder.setFrameLineWidth(lw);
  }

  /**
   * Sets the frame line width in given unit.
   *
   * @param lw the frame line width to set
   * @param unit the unit for the given frame line width
   * @return true if succeeded
   */
  public boolean setFrameLineWidth(final float lw, final String unit) {
    return this.mStyleHolder.setFrameLineWidth(lw, unit);
  }

  /**
   * Sets the frame line color.
   *
   * @param cl the frame line color to set
   * @return true if succeeded
   */
  public boolean setFrameLineColor(final Color cl) {
    return this.mStyleHolder.setFrameLineColor(cl);
  }

  /**
   * Sets the background color.
   *
   * @param cl the background color to set
   * @return true if succeeded
   */
  @Override
  public boolean setBackgroundColor(final Color cl) {
    return this.mStyleHolder.setBackgroundColor(cl);
  }

  /**
   * Sets the font size.
   *
   * @param size the font size in units of pt
   * @return true if succeeded
   */
  public boolean setFontSize(final float size) {
    this.mStyleHolder.setFontSize(size);
    return true;
  }

  /**
   * Sets the font size in a given unit.
   *
   * @param size the font size
   * @param unit the unit of font size
   * @return true if succeeded
   */
  public boolean setFontSize(final float size, final String unit) {
    this.mStyleHolder.setFontSize(size, unit);
    return true;
  }

  /**
   * Sets the font style.
   *
   * @param style the font style
   * @return true if succeeded
   */
  public boolean setFontStyle(final int style) {
    this.mStyleHolder.setFontStyle(style);
    return true;
  }

  /**
   * Sets the font color.
   *
   * @param color the font color
   * @return true if succeeded
   */
  public boolean setFontColor(final Color color) {
    return this.mStyleHolder.setFontColor(color);
  }

  /**
   * Sets the font name.
   *
   * @param name the font name
   * @return true if succeeded
   */
  public boolean setFontName(final String name) {
    this.mStyleHolder.setFontName(name);
    return true;
  }

  /**
   * Sets the font parameters.
   *
   * @param name the font name
   * @param style the font style
   * @param size the font size in units of pt
   * @return true if succeeded
   */
  private boolean setFont(final String name, final int style, final float size) {
    final boolean changed = this.mStyleHolder.setFont(name, style, size);
    if (changed) {
      this.updateAllDrawingElements();
    }
    return true;
  }

  /** */
  SGData getData(final ElementGroupSetInLegend groupSet) {
    for (int ii = 0; ii < this.mChildList.size(); ii++) {
      ElementGroupSetInLegend groupSet_ = (ElementGroupSetInLegend) this.mChildList.get(ii);
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
    return this.mStyleHolder.isVisible();
  }

  /** */
  public boolean isFrameVisible() {
    return this.mStyleHolder.isFrameVisible();
  }

  /** */
  public Rectangle2D getRectOfGroup(final SGElementGroup group) {
    for (int ii = 0; ii < this.mChildList.size(); ii++) {
      ElementGroupSetInLegend leg = (ElementGroupSetInLegend) this.mChildList.get(ii);
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

  /** */
  public Rectangle2D getRectOfGroupSet(final SGElementGroupSet groupSet) {
    for (int ii = 0; ii < this.mChildList.size(); ii++) {
      ElementGroupSetInLegend leg = (ElementGroupSetInLegend) this.mChildList.get(ii);
      if (groupSet.equals(leg)) {
        return leg.mDataRect;
      }
    }

    return null;
  }

  /** */
  ElementGroupLine getGroupLine(final ElementGroupSetInLegend groupSet) {
    ArrayList<SGElementGroup> groupList = groupSet.getElementGroupList();
    for (int ii = 0; ii < groupList.size(); ii++) {
      SGElementGroup group = (SGElementGroup) groupList.get(ii);
      if (group instanceof ElementGroupLine) {
        return (ElementGroupLine) group;
      }
    }

    return null;
  }

  /** */
  ElementGroupBar getGroupBar(final ElementGroupSetInLegend groupSet) {
    ArrayList<SGElementGroup> groupList = groupSet.getElementGroupList();
    for (int ii = 0; ii < groupList.size(); ii++) {
      SGElementGroup group = (SGElementGroup) groupList.get(ii);
      if (group instanceof ElementGroupBar) {
        return (ElementGroupBar) group;
      }
    }

    return null;
  }

  /** */
  ElementGroupSymbol getGroupSymbol(final ElementGroupSetInLegend groupSet) {
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
   * @param groupSet
   * @return
   */
  ElementGroupErrorBar getGroupErrorBar(final ElementGroupSetInLegend groupSet) {
    ArrayList<SGElementGroup> groupList = groupSet.getElementGroupList();
    for (int ii = 0; ii < groupList.size(); ii++) {
      SGElementGroup group = (SGElementGroup) groupList.get(ii);
      if (group instanceof ElementGroupErrorBar) {
        return (ElementGroupErrorBar) group;
      }
    }

    return null;
  }

  /** */
  ElementGroupArrow getGroupArrow(final ElementGroupSetInLegend groupSet) {
    ArrayList<SGElementGroup> groupList = groupSet.getElementGroupList();
    for (int ii = 0; ii < groupList.size(); ii++) {
      SGElementGroup group = (SGElementGroup) groupList.get(ii);
      if (group instanceof ElementGroupArrow) {
        return (ElementGroupArrow) group;
      }
    }

    return null;
  }

  ElementGroupPseudocolorMap getColorMap(final ElementGroupSetInLegend groupSet) {
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
   *
   * @param e an action event
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
   *
   * @return true if succeeded
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
   *
   * @return true if succeeded
   */
  public boolean cancel() {

    // set temporary properties to drawing elements to cancel the change
    if (this.setProperties(this.mTemporaryProperties) == false) {
      return false;
    }

    // clear temporary properties
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
   *
   * @return true if succeeded
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

  /** */
  public boolean setMementoBackward() {
    boolean flag = super.setMementoBackward();
    if (!flag) {
      return false;
    }

    this.updateAllDrawingElements();
    this.notifyChangeOnUndo();

    return true;
  }

  /** */
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
  protected ArrayList<ElementGroupSetInLegend> getVisibleLegendList() {
    ArrayList<ElementGroupSetInLegend> list = new ArrayList<ElementGroupSetInLegend>();
    for (int ii = 0; ii < this.mChildList.size(); ii++) {
      ElementGroupSetInLegend groupSet = (ElementGroupSetInLegend) this.mChildList.get(ii);
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
      ElementGroupSetInLegend groupSet = (ElementGroupSetInLegend) this.mChildList.get(ii);
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
      ElementGroupSetInLegend groupSet = (ElementGroupSetInLegend) this.mChildList.get(ii);
      if (groupSet.isViewable()) {
        list.add(groupSet.getData());
      }
    }
    return list;
  }

  /** */
  public boolean createDataObject(
      final Element el, final SGData data, final boolean readDataProperty) {

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
   * Cut focused copyable objects.
   *
   * @return a list of cut objects
   */
  public List<SGICopyable> cutFocusedObjects() {
    // returns an empty list because no objects exist that can be cut
    // other than data
    return new ArrayList<SGICopyable>();
  }

  /**
   * Returns a list of cut data objects.
   *
   * @return a list of cut data objects
   */
  public List<SGData> cutFocusedData() {
    List<SGData> list = this.getFocusedDataList();
    this.hideSelectedData();
    return list;
  }

  /** */
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
   * @param toFront flag whether to move to front or back
   * @return true if succeeded
   */
  public boolean moveFocusedObjects(final boolean toFront) {
    // do nothing
    return true;
  }

  /**
   * Move the focused objects to forward or backward for given steps.
   *
   * @param num the number of levels to move
   * @return true if succeeded
   */
  public boolean moveFocusedObjects(int num) {
    // do nothing
    return true;
  }

  /**
   * Returns the index of data object in legend.
   *
   * @param data a data object
   * @return the index of data object in legend or -1 if not found
   */
  public int getIndex(final SGData data) {
    List<SGIChildObject> cList = this.getVisibleChildList();
    for (int ii = 0; ii < cList.size(); ii++) {
      ElementGroupSetInLegend gs = (ElementGroupSetInLegend) cList.get(ii);
      SGData d = gs.getData();
      if (d.equals(data)) {
        return ii;
      }
    }
    return -1;
  }

  /**
   * Sort the order of legend objects.
   *
   * @param dataArray an array of data
   * @param indexArray an array of index
   * @return true if succeeded
   */
  public boolean sortLegend(SGData[] dataArray, int[] indexArray) {
    if (dataArray == null || indexArray == null) {
      throw new IllegalArgumentException("dataArray == null || indexArray == null");
    }
    if (dataArray.length != indexArray.length) {
      throw new IllegalArgumentException("dataArray.length != indexArray.length");
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
    ArrayList<List<SGData>> dListArray = new ArrayList<List<SGData>>(lenNew);
    for (int ii = 0; ii < lenNew; ii++) {
      dListArray.add(new ArrayList<SGData>());
    }
    for (int ii = 0; ii < len; ii++) {
      dListArray.get(indexArrayNew[ii]).add(dataArray[ii]);
    }
    List<SGData> oDataList = new ArrayList<SGData>();
    for (int ii = 0; ii < lenNew; ii++) {
      oDataList.addAll(dListArray.get(ii));
    }

    // sort the legend objects
    List<SGIChildObject> cList = new ArrayList<SGIChildObject>();
    for (int ii = 0; ii < oDataList.size(); ii++) {
      SGData data = oDataList.get(ii);
      SGIChildObject gs = (SGIChildObject) this.getElementGroupSet(data);
      cList.add(gs);
    }

    // update the child list
    this.mChildList.clear();
    this.mChildList.addAll(cList);

    return true;
  }

  /**
   * @return
   */
  /** */
  boolean setVisibleChildListForPropertyIO(final List<SGIChildObject> list) {
    return this.setVisibleChildList(list);
  }

  /** */
  Element createThisElementForPropertyIO(final Document document, final SGExportParameter params) {
    return this.createThisElement(document, params);
  }

  public String getTagName() {
    return TAG_NAME_LEGEND;
  }

  /** */
  public boolean writeProperty(final Element el, final SGExportParameter params) {
    return this.mPropertyIO.writeProperty(el, params);
  }

  /** */
  public Element[] createElement(final Document document, final SGExportParameter params) {
    return this.mPropertyIO.createElement(document, params);
  }

  /**
   * Read properties from the Element object.
   *
   * @param element an Element object which has properties
   * @param versionNumber the version number of property file
   * @return true if succeeded
   */
  public boolean readProperty(final Element element, final String versionNumber) {
    return this.mPropertyIO.readProperty(element, versionNumber);
  }

  /** An interface that element groups in the legend must implement. */

  /** A legend for single data object. */

  /** */

  /** A class of multiple element group set in legend. */

  /** */

  /** Error bars. */

  //
  boolean readProperty(final SGElementGroup group, final Element el) {
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

  /** Called when the key is pressed. */
  public void keyPressed(KeyEvent e) {}

  /** Called when the key is released. */
  public void keyReleased(KeyEvent e) {}

  /** Called when the key is typed. */
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

  protected SGIElementGroupSetSXY createSXYGroupSetInstance(SGISXYTypeSingleData dataSXY) {
    return new ElementGroupSetInLegendSXY(SGFigureElementLegend.this, (SGData) dataSXY);
  }

  protected SGIElementGroupSetVXY createVXYGroupSetInstance(SGIVXYTypeData dataVXY) {
    return new ElementGroupSetInLegendVXY(SGFigureElementLegend.this, (SGData) dataVXY);
  }

  protected SGIElementGroupSetVXY createGridVXYGroupSetInstance(SGIVXYTypeData dataVXY) {
    return new ElementGroupSetInLegendVXY(SGFigureElementLegend.this, (SGData) dataVXY);
  }

  protected SGIElementGroupSetMultipleSXY createMultipleSXYGroupSetInstance(
      SGISXYTypeMultipleData dataMultiSXY) {
    return new ElementGroupSetInLegendMultipleSXY(
        SGFigureElementLegend.this, (SGData) dataMultiSXY);
  }

  protected SGIElementGroupSetSXYZ createSXYZGroupSetInstance(SGISXYZTypeData dataSXYZ) {
    return new ElementGroupSetInLegendSXYZ(SGFigureElementLegend.this, (SGData) dataSXYZ);
  }

  /**
   * Sets the dialog owner this figure element.
   *
   * @param frame the dialog owner
   * @return true if succeeded
   */
  public boolean setDialogOwner(final Frame frame) {
    if (super.setDialogOwner(frame) == false) {
      return false;
    }
    SwingUtilities.invokeLater(
        new Runnable() {
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
   * @param e a document event
   */
  public void changedUpdate(DocumentEvent e) {
    this.updateLabelTextField();
  }

  /**
   * Called when the text string in the text field is updated.
   *
   * @param e a document event
   */
  public void insertUpdate(DocumentEvent e) {
    this.updateLabelTextField();
  }

  /**
   * Called when the text string in the text field is updated.
   *
   * @param e a document event
   */
  public void removeUpdate(DocumentEvent e) {
    this.updateLabelTextField();
  }

  /** Update the text field. */
  private void updateLabelTextField() {

    // create the font
    final Font font =
        new Font(
            this.getFontName(),
            this.getFontStyle(),
            (int) (this.getFontSize() * this.getMagnification()));

    // update the text field
    this.updateTextField(this.mTextField, font);
  }

  /** Called when the caret in the text field is update. */
  public void caretUpdate(final CaretEvent e) {}

  /** Called when menu items in the menu bar is selected. */
  public void onMenuSelected() {
    if (this.mTextField.isVisible()) {
      this.closeTextField();
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

  // @Override
  // protected SGIElementGroupSetGridSXYZ createGridSXYZGroupSetInstance(
  // SGISXYZTypeData dataSXYZ) {
  // return new ElementGroupSetInLegendSXYZ(SGFigureElementLegend.this, (SGData) dataSXYZ);
  // }

  /**
   * Moves the data of given ID to the top or the bottom in legend.
   *
   * @param id the ID of an object
   * @param toTop true to move to the top
   * @return true if succeeded
   */
  public boolean moveLegendToEnd(final int id, final boolean toTop) {
    ElementGroupSetInLegend child = (ElementGroupSetInLegend) this.getVisibleChild(id);
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
   * @param id the ID of an object
   * @param toUpper true to move to upper
   * @return true if succeeded
   */
  public boolean moveLegend(final int id, final boolean toUpper) {
    ElementGroupSetInLegend child = (ElementGroupSetInLegend) this.getVisibleChild(id);
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
   * @param kvList a list of key and values of properties
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
          result.putResult(COM_LEGEND_AXIS_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setXAxisLocation(loc) == false) {
          result.putResult(COM_LEGEND_AXIS_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_LEGEND_AXIS_X, SGPropertyResults.SUCCEEDED);
      } else if (COM_LEGEND_AXIS_Y.equalsIgnoreCase(key)) {
        final int loc = SGUtility.getAxisLocation(value);
        if (loc == -1) {
          result.putResult(COM_LEGEND_AXIS_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setYAxisLocation(loc) == false) {
          result.putResult(COM_LEGEND_AXIS_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_LEGEND_AXIS_Y, SGPropertyResults.SUCCEEDED);
      } else if (COM_LEGEND_LOCATION_X.equalsIgnoreCase(key)) {
        Double num = SGUtilityText.getDouble(value);
        if (num == null) {
          result.putResult(COM_LEGEND_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
          result.putResult(COM_LEGEND_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setXValue(num.doubleValue()) == false) {
          result.putResult(COM_LEGEND_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_LEGEND_LOCATION_X, SGPropertyResults.SUCCEEDED);
      } else if (COM_LEGEND_LOCATION_Y.equalsIgnoreCase(key)) {
        Double num = SGUtilityText.getDouble(value);
        if (num == null) {
          result.putResult(COM_LEGEND_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
          result.putResult(COM_LEGEND_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setYValue(num.doubleValue()) == false) {
          result.putResult(COM_LEGEND_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_LEGEND_LOCATION_Y, SGPropertyResults.SUCCEEDED);
      } else if (COM_LEGEND_VISIBLE.equalsIgnoreCase(key)) {
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(COM_LEGEND_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        this.setVisible(b.booleanValue());
        result.putResult(COM_LEGEND_VISIBLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_LEGEND_FONT_NAME.equalsIgnoreCase(key)) {
        final String name = SGUtility.findFontFamilyName(value);
        if (name == null) {
          result.putResult(COM_LEGEND_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setFontName(name) == false) {
          result.putResult(COM_LEGEND_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_LEGEND_FONT_NAME, SGPropertyResults.SUCCEEDED);
      } else if (COM_LEGEND_FONT_STYLE.equalsIgnoreCase(key)) {
        Integer style = SGUtilityText.getFontStyle(value);
        if (style == null) {
          result.putResult(COM_LEGEND_FONT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setFontStyle(style.intValue()) == false) {
          result.putResult(COM_LEGEND_FONT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_LEGEND_FONT_STYLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_LEGEND_FONT_SIZE.equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_LEGEND_FONT_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setFontSize(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_LEGEND_FONT_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_LEGEND_FONT_SIZE, SGPropertyResults.SUCCEEDED);
      } else if (COM_LEGEND_FONT_COLOR.equalsIgnoreCase(key)) {
        Color cl = SGUtilityText.getColor(value);
        if (cl != null) {
          if (this.setFontColor(cl) == false) {
            result.putResult(COM_LEGEND_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        } else {
          cl = SGUtilityText.parseColor(value);
          if (cl == null) {
            result.putResult(COM_LEGEND_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (this.setFontColor(cl) == false) {
            result.putResult(COM_LEGEND_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        }
        result.putResult(COM_LEGEND_FONT_COLOR, SGPropertyResults.SUCCEEDED);
      } else if (COM_LEGEND_FRAME_VISIBLE.equalsIgnoreCase(key)) {
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(COM_LEGEND_FRAME_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setFrameVisible(b.booleanValue()) == false) {
          result.putResult(COM_LEGEND_FRAME_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_LEGEND_FRAME_VISIBLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_LEGEND_FRAME_LINE_WIDTH.equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_LEGEND_FRAME_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setFrameLineWidth(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_LEGEND_FRAME_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_LEGEND_FRAME_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
      } else if (COM_LEGEND_FRAME_COLOR.equalsIgnoreCase(key)) {
        Color cl = SGUtilityText.getColor(value);
        if (cl != null) {
          if (this.setFrameLineColor(cl) == false) {
            result.putResult(COM_LEGEND_FRAME_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        } else {
          cl = SGUtilityText.parseColor(value);
          if (cl == null) {
            result.putResult(COM_LEGEND_FRAME_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (this.setFrameLineColor(cl) == false) {
            result.putResult(COM_LEGEND_FRAME_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        }
        result.putResult(COM_LEGEND_FRAME_COLOR, SGPropertyResults.SUCCEEDED);
      } else if (COM_LEGEND_BACKGROUND_TRANSPARENCY.equalsIgnoreCase(key)) {
        final Integer num = SGUtilityText.getInteger(value, SGIConstants.percent);
        if (num == null) {
          result.putResult(
              COM_LEGEND_BACKGROUND_TRANSPARENCY, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setBackgroundTransparent(num.intValue()) == false) {
          result.putResult(
              COM_LEGEND_BACKGROUND_TRANSPARENCY, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_LEGEND_BACKGROUND_TRANSPARENCY, SGPropertyResults.SUCCEEDED);
      } else if (COM_LEGEND_BACKGROUND_COLOR.equalsIgnoreCase(key)) {
        Color cl = SGUtilityText.getColor(value);
        if (cl != null) {
          if (this.setBackgroundColor(cl) == false) {
            result.putResult(COM_LEGEND_BACKGROUND_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        } else {
          cl = SGUtilityText.parseColor(value);
          if (cl == null) {
            result.putResult(COM_LEGEND_BACKGROUND_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (this.setBackgroundColor(cl) == false) {
            result.putResult(COM_LEGEND_BACKGROUND_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        }
        result.putResult(COM_LEGEND_BACKGROUND_COLOR, SGPropertyResults.SUCCEEDED);
      } else if (COM_LEGEND_SYMBOL_SPAN.equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_LEGEND_SYMBOL_SPAN, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (this.setSymbolSpan(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_LEGEND_SYMBOL_SPAN, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_LEGEND_SYMBOL_SPAN, SGPropertyResults.SUCCEEDED);
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
   * Returns a text string of the commands. Overrode to get legend command.
   *
   * @return a text string of the commands
   */
  @Override
  public String getCommandString(SGExportParameter params) {
    StringBuilder sb = new StringBuilder();
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
    SGPropertyUtility.addProperty(
        map, COM_LEGEND_AXIS_X, this.mAxisElement.getLocationName(this.mXAxis));
    SGPropertyUtility.addProperty(
        map, COM_LEGEND_AXIS_Y, this.mAxisElement.getLocationName(this.mYAxis));

    // location
    SGPropertyUtility.addProperty(map, COM_LEGEND_LOCATION_X, this.getXValue());
    SGPropertyUtility.addProperty(map, COM_LEGEND_LOCATION_Y, this.getYValue());

    // font
    SGPropertyUtility.addProperty(map, COM_LEGEND_FONT_NAME, this.getFontName());
    SGPropertyUtility.addProperty(
        map, COM_LEGEND_FONT_STYLE, SGUtilityText.getFontStyleName(this.getFontStyle()));
    SGPropertyUtility.addProperty(
        map, COM_LEGEND_FONT_SIZE, SGUtility.getExportFontSize(this.getFontSize()), FONT_SIZE_UNIT);
    SGPropertyUtility.addProperty(map, COM_LEGEND_FONT_COLOR, this.getFontColor());

    // frame
    SGPropertyUtility.addProperty(map, COM_LEGEND_FRAME_VISIBLE, this.isFrameVisible());
    SGPropertyUtility.addProperty(
        map,
        COM_LEGEND_FRAME_LINE_WIDTH,
        SGUtility.getExportLineWidth(this.getFrameLineWidth(LINE_WIDTH_UNIT)),
        LINE_WIDTH_UNIT);
    SGPropertyUtility.addProperty(map, COM_LEGEND_FRAME_COLOR, this.getFrameColor());

    // background
    SGPropertyUtility.addProperty(
        map, COM_LEGEND_BACKGROUND_TRANSPARENCY, this.getBackgroundTransparency(), percent);
    SGPropertyUtility.addProperty(map, COM_LEGEND_BACKGROUND_COLOR, this.getBackgroundColor());

    // span
    SGPropertyUtility.addProperty(
        map,
        COM_LEGEND_SYMBOL_SPAN,
        SGUtility.getExportValue(this.getSymbolSpan(SYMBOL_SPAN_UNIT), SYMBOL_SPAN_MINIMAL_ORDER),
        SYMBOL_SPAN_UNIT);

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
    SGPropertyUtility.addProperty(
        map, KEY_X_AXIS_POSITION, this.mAxisElement.getLocationName(this.mXAxis));
    SGPropertyUtility.addProperty(
        map, KEY_Y_AXIS_POSITION, this.mAxisElement.getLocationName(this.mYAxis));

    // location
    SGPropertyUtility.addProperty(map, KEY_X_VALUE, this.getXValue());
    SGPropertyUtility.addProperty(map, KEY_Y_VALUE, this.getYValue());

    // font
    SGPropertyUtility.addProperty(map, KEY_FONT_NAME, this.getFontName());
    SGPropertyUtility.addProperty(
        map, KEY_FONT_STYLE, SGUtilityText.getFontStyleName(this.getFontStyle()));
    SGPropertyUtility.addProperty(
        map, KEY_FONT_SIZE, SGUtility.getExportFontSize(this.getFontSize()), FONT_SIZE_UNIT);
    SGPropertyUtility.addProperty(map, KEY_STRING_COLORS, this.getFontColor());

    // frame
    SGPropertyUtility.addProperty(map, KEY_FRAME_VISIBLE, this.isFrameVisible());
    SGPropertyUtility.addProperty(
        map,
        KEY_FRAME_LINE_WIDTH,
        SGUtility.getExportLineWidth(this.getFrameLineWidth(LINE_WIDTH_UNIT)),
        LINE_WIDTH_UNIT);
    SGPropertyUtility.addProperty(map, KEY_FRAME_LINE_COLOR, this.getFrameColor());

    // background
    SGPropertyUtility.addProperty(
        map, KEY_BACKGROUND_TRANSPARENT, this.getBackgroundTransparency(), percent);
    SGPropertyUtility.addProperty(map, KEY_BACKGROUND_COLOR, this.getBackgroundColor());

    // span
    SGPropertyUtility.addProperty(
        map,
        KEY_SYMBOL_SPAN,
        SGUtility.getExportValue(this.getSymbolSpan(SYMBOL_SPAN_UNIT), SYMBOL_SPAN_MINIMAL_ORDER),
        SYMBOL_SPAN_UNIT);

    return map;
  }

  @Override
  public boolean getAxisDateMode(final int location) {
    return this.mAxisElement.getAxisDateMode(location);
  }
}

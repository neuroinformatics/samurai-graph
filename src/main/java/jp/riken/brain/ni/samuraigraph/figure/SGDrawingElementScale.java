package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGScaleConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGStringConstants.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import org.w3c.dom.Element;

public abstract class SGDrawingElementScale extends SGDrawingElementLineAndStringSymbol {

  protected float mWidth;

  protected float mHeight;

  protected SGDrawingElementString mHorizontalStringElement;

  protected SGDrawingElementString mVerticalStringElement;

  protected SGDrawingElementLine mHorizontalLine;

  protected SGDrawingElementLine mVerticalLine;

  protected boolean mHorizontalVisible = true;

  protected boolean mVerticalVisible = true;

  protected String mHorizontalText = null;

  protected String mVerticalText = null;

  protected String mHorizontalUnit = null;

  protected String mVerticalUnit = null;

  protected boolean mHorizontalTextDownSide;

  protected boolean mVerticalTextLeftSide;

  protected float mTextAngle;

  protected Color mStringColor = null;

  /** Build a symbol. */
  public SGDrawingElementScale() {
    super();
    this.mHorizontalLine = this.createLine(true);
    this.mHorizontalStringElement = this.createString(true);
    this.mVerticalLine = this.createLine(false);
    this.mVerticalStringElement = this.createString(false);
  }

  public SGDrawingElementScale(final float x, final float y, final float w, final float h) {
    super();
    this.setLocation(x, y);
    this.setSize(w, h);
  }

  @Override
  public void dispose() {
    super.dispose();
    this.mHorizontalLine.dispose();
    this.mVerticalLine.dispose();
    this.mHorizontalStringElement.dispose();
    this.mVerticalStringElement.dispose();
    this.mHorizontalLine = null;
    this.mVerticalLine = null;
    this.mHorizontalStringElement = null;
    this.mVerticalStringElement = null;
  }

  @Override
  public boolean contains(int x, int y) {
    if (this.isHorizontalVisible()) {
      if (this.mHorizontalLine.contains(x, y)) {
        return true;
      }
      if (this.mHorizontalStringElement.contains(x, y)) {
        return true;
      }
    }
    if (this.isVerticalVisible()) {
      if (this.mVerticalLine.contains(x, y)) {
        return true;
      }
      if (this.mVerticalStringElement.contains(x, y)) {
        return true;
      }
    }
    return false;
  }

  public boolean isHorizontalVisible() {
    return this.mHorizontalVisible;
  }

  public boolean isVerticalVisible() {
    return this.mVerticalVisible;
  }

  public void setHorizontalVisible(final boolean b) {
    this.mHorizontalVisible = b;
  }

  public void setVerticalVisible(final boolean b) {
    this.mVerticalVisible = b;
  }

  protected abstract SGDrawingElementString createString(final boolean horizontal);

  protected abstract SGDrawingElementLine createLine(final boolean horizontal);

  public boolean setSize(final float w, final float h) {
    this.setWidth(w);
    this.setHeight(h);
    return true;
  }

  public boolean setWidth(final float w) {
    this.mWidth = w;
    return true;
  }

  public boolean setHeight(final float h) {
    this.mHeight = h;
    return true;
  }

  public float getWidth() {
    return this.mWidth;
  }

  public float getWidth(final String unit) {
    return (float) SGUtilityText.convertFromPoint(this.getWidth(), unit);
  }

  public float getHeight() {
    return this.mHeight;
  }

  public float getHeight(final String unit) {
    return (float) SGUtilityText.convertFromPoint(this.getHeight(), unit);
  }

  protected final SGDrawingElementLine getHorizontalLine() {
    return this.mHorizontalLine;
  }

  protected final SGDrawingElementLine getVerticalLine() {
    return this.mVerticalLine;
  }

  public final SGDrawingElementString getHorizontalStringElement() {
    return this.mHorizontalStringElement;
  }

  public final SGDrawingElementString getVerticalStringElement() {
    return this.mVerticalStringElement;
  }

  public String getHorizontalText() {
    return this.mHorizontalText;
  }

  public String getVerticalText() {
    return this.mVerticalText;
  }

  public String getHorizontalUnit() {
    return this.mHorizontalUnit;
  }

  public String getVerticalUnit() {
    return this.mVerticalUnit;
  }

  public Color getLineColor() {
    return this.getColor();
  }

  public boolean setLineColor(final Color cl) {
    return this.setColor(cl);
  }

  public Color getStringColor() {
    return this.mStringColor;
  }

  public boolean setStringColor(final Color color) {
    this.mStringColor = color;
    this.mHorizontalStringElement.setColor(color);
    this.mVerticalStringElement.setColor(color);
    return true;
  }

  public boolean setHorizontalText(final String text) {
    this.mHorizontalText = text;
    String str = (text == null) ? "" : text;
    this.mHorizontalStringElement.setString(str);
    return true;
  }

  public boolean setVerticalText(final String text) {
    this.mVerticalText = text;
    String str = (text == null) ? "" : text;
    this.mVerticalStringElement.setString(str);
    return true;
  }

  public boolean setHorizontalUnit(final String unit) {
    this.mHorizontalUnit = unit;
    return true;
  }

  public boolean setVerticalUnit(final String unit) {
    this.mVerticalUnit = unit;
    return true;
  }

  @Override
  public boolean setLineWidth(final float lw) {
    if (super.setLineWidth(lw) == false) {
      return false;
    }
    this.mHorizontalLine.setLineWidth(lw);
    this.mVerticalLine.setLineWidth(lw);
    return true;
  }

  @Override
  public boolean setFont(final String name, final int style, final float size) {
    if (super.setFont(name, style, size) == false) {
      return false;
    }
    this.mHorizontalStringElement.setFont(name, style, size);
    this.mVerticalStringElement.setFont(name, style, size);
    return true;
  }

  /**
   * Sets the font name.
   *
   * @param name the font name
   */
  @Override
  public boolean setFontName(final String name) {
    if (super.setFontName(name) == false) {
      return false;
    }
    return this.setFont(name, this.mFontStyle, this.mFontSize);
  }

  /**
   * Sets the font size.
   *
   * @param size the font size
   */
  @Override
  public boolean setFontSize(final float size) {
    if (super.setFontSize(size) == false) {
      return false;
    }
    return this.setFont(this.mFontName, this.mFontStyle, size);
  }

  /**
   * Sets the font style.
   *
   * @param style the font style
   */
  @Override
  public boolean setFontStyle(final int style) {
    if (super.setFontStyle(style) == false) {
      return false;
    }
    return this.setFont(this.mFontName, style, this.mFontSize);
  }

  public boolean isFlippingHorizontal() {
    return (this.mWidth < 0.0f);
  }

  public boolean isFlippingVertical() {
    return (this.mHeight > 0.0f);
  }

  public boolean isHorizontalTextDownside() {
    return this.mHorizontalTextDownSide;
  }

  public void setHorizontalTextDownside(final boolean b) {
    this.mHorizontalTextDownSide = b;
  }

  public boolean isVerticalTextLeftside() {
    return this.mVerticalTextLeftSide;
  }

  public void setVerticalTextLeftside(final boolean b) {
    this.mVerticalTextLeftSide = b;
  }

  public float getTextAngle() {
    return this.mTextAngle;
  }

  public boolean setTextAngle(float angle) {
    final Float aNew =
        SGUtility.calcPropertyValue(
            angle, null, null, STRING_ANGLE_MIN, STRING_ANGLE_MAX, STRING_ANGLE_MINIMAL_ORDER);
    if (aNew == null) {
      return false;
    }
    this.mTextAngle = aNew.floatValue();
    this.getHorizontalStringElement().setAngle(aNew);
    this.getVerticalStringElement().setAngle(aNew);
    return true;
  }

  @Override
  public SGProperties getProperties() {
    ScaleProperties p = new ScaleProperties();
    if (this.getProperties(p) == false) {
      return null;
    }
    return p;
  }

  @Override
  public boolean getProperties(SGProperties p) {
    if ((p instanceof ScaleProperties) == false) {
      return false;
    }
    if (super.getProperties(p) == false) {
      return false;
    }
    ScaleProperties sp = (ScaleProperties) p;
    sp.mWidth = this.getWidth();
    sp.mHeight = this.getHeight();
    sp.mTextAngle = this.mTextAngle;
    sp.mHorizontalVisible = this.mHorizontalVisible;
    sp.mVerticalVisible = this.mVerticalVisible;
    sp.mHorizontalText = this.mHorizontalText;
    sp.mVerticalText = this.mVerticalText;
    sp.mHorizontalUnit = this.mHorizontalUnit;
    sp.mVerticalUnit = this.mVerticalUnit;
    sp.mHorizontalTextDownside = this.mHorizontalTextDownSide;
    sp.mVerticalTextLeftside = this.mVerticalTextLeftSide;
    sp.mStringColor = this.mStringColor;
    return true;
  }

  @Override
  public boolean setProperties(final SGProperties p) {
    if ((p instanceof ScaleProperties) == false) {
      return false;
    }
    if (super.setProperties(p) == false) {
      return false;
    }
    ScaleProperties sp = (ScaleProperties) p;
    this.setWidth(sp.mWidth);
    this.setHeight(sp.mHeight);
    this.setTextAngle(sp.mTextAngle);
    this.setHorizontalVisible(sp.mHorizontalVisible);
    this.setVerticalVisible(sp.mVerticalVisible);
    this.setHorizontalText(sp.mHorizontalText);
    this.setVerticalText(sp.mVerticalText);
    this.mHorizontalUnit = sp.mHorizontalUnit;
    this.mVerticalUnit = sp.mVerticalUnit;
    this.mHorizontalTextDownSide = sp.mHorizontalTextDownside;
    this.mVerticalTextLeftSide = sp.mVerticalTextLeftside;
    this.setStringColor(sp.mStringColor);
    return true;
  }

  @Override
  public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    SGPropertyMap map = super.getPropertyFileMap(params);
    this.addProperties(
        map,
        KEY_VISIBLE,
        KEY_SCALE_X_AXIS_VISIBLE,
        KEY_SCALE_X_AXIS_TITLE_DOWNSIDE,
        KEY_SCALE_Y_AXIS_VISIBLE,
        KEY_SCALE_Y_AXIS_TITLE_LEFTSIDE,
        KEY_SCALE_LINE_COLOR,
        KEY_SCALE_FONT_COLOR,
        KEY_SCALE_TEXT_ANGLE);
    SGPropertyUtility.addProperty(map, KEY_SCALE_X_AXIS_TEXT, this.getHorizontalText());
    SGPropertyUtility.addProperty(map, KEY_SCALE_Y_AXIS_TEXT, this.getVerticalText());
    SGPropertyUtility.addProperty(map, KEY_SCALE_X_AXIS_UNIT, this.getHorizontalUnit());
    SGPropertyUtility.addProperty(map, KEY_SCALE_Y_AXIS_UNIT, this.getVerticalUnit());
    return map;
  }

  @Override
  public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
    SGPropertyMap map = super.getCommandPropertyMap(params);
    this.addProperties(
        map,
        COM_SCALE_VISIBLE,
        COM_SCALE_X_AXIS_VISIBLE,
        COM_SCALE_X_AXIS_TITLE_DOWNSIDE,
        COM_SCALE_Y_AXIS_VISIBLE,
        COM_SCALE_Y_AXIS_TITLE_LEFT_SIDE,
        COM_SCALE_LINE_COLOR,
        COM_SCALE_FONT_COLOR,
        COM_SCALE_FONT_ANGLE);
    SGPropertyUtility.addQuotedStringProperty(
        map, COM_SCALE_X_AXIS_TITLE_TEXT, this.getHorizontalText());
    SGPropertyUtility.addQuotedStringProperty(
        map, COM_SCALE_Y_AXIS_TITLE_TEXT, this.getVerticalText());
    SGPropertyUtility.addQuotedStringProperty(
        map, COM_SCALE_X_AXIS_TITLE_UNIT, this.getHorizontalUnit());
    SGPropertyUtility.addQuotedStringProperty(
        map, COM_SCALE_Y_AXIS_TITLE_UNIT, this.getVerticalUnit());
    return map;
  }

  private void addProperties(
      SGPropertyMap map,
      String visibleKey,
      String xAxisVisibleKey,
      String xAxisTitleDownsideKey,
      String yAxisVisibleKey,
      String yAxisTitleLeftsideKey,
      String lineColorKey,
      String fontColorKey,
      String angleKey) {
    SGPropertyUtility.addProperty(map, visibleKey, this.isVisible());
    SGPropertyUtility.addProperty(map, xAxisVisibleKey, this.isHorizontalVisible());
    SGPropertyUtility.addProperty(map, xAxisTitleDownsideKey, this.isHorizontalTextDownside());
    SGPropertyUtility.addProperty(map, yAxisVisibleKey, this.isVerticalVisible());
    SGPropertyUtility.addProperty(map, yAxisTitleLeftsideKey, this.isVerticalTextLeftside());
    SGPropertyUtility.addProperty(map, lineColorKey, this.getLineColor());
    SGPropertyUtility.addProperty(map, fontColorKey, this.getStringColor());
    SGPropertyUtility.addProperty(
        map, angleKey, SGUtility.getExportValue(this.getTextAngle(), STRING_ANGLE_MINIMAL_ORDER));
  }

  public static class ScaleProperties extends LineAndStringProperties {

    float mWidth;

    float mHeight;

    float mTextAngle;

    boolean mHorizontalVisible;

    boolean mVerticalVisible;

    String mHorizontalText;

    String mVerticalText;

    String mHorizontalUnit;

    String mVerticalUnit;

    boolean mHorizontalTextDownside;

    boolean mVerticalTextLeftside;

    Color mStringColor;

    public ScaleProperties() {
      super();
    }

    @Override
    public boolean equals(final Object obj) {
      if (!(obj instanceof ScaleProperties)) {
        return false;
      }
      if (super.equals(obj) == false) {
        return false;
      }

      ScaleProperties p = (ScaleProperties) obj;
      if (p.mWidth != this.mWidth) {
        return false;
      }
      if (p.mHeight != this.mHeight) {
        return false;
      }
      if (p.mTextAngle != this.mTextAngle) {
        return false;
      }
      if (p.mHorizontalVisible != this.mHorizontalVisible) {
        return false;
      }
      if (p.mVerticalVisible != this.mVerticalVisible) {
        return false;
      }
      if (SGUtility.equals(p.mHorizontalText, this.mHorizontalText) == false) {
        return false;
      }
      if (SGUtility.equals(p.mHorizontalUnit, this.mHorizontalUnit) == false) {
        return false;
      }
      if (SGUtility.equals(p.mVerticalText, this.mVerticalText) == false) {
        return false;
      }
      if (SGUtility.equals(p.mVerticalUnit, this.mVerticalUnit) == false) {
        return false;
      }
      if (p.mHorizontalTextDownside != this.mHorizontalTextDownside) {
        return false;
      }
      if (p.mVerticalTextLeftside != this.mVerticalTextLeftside) {
        return false;
      }
      if (SGUtility.equals(p.mStringColor, this.mStringColor) == false) {
        return false;
      }
      return true;
    }
  }

  public boolean setNodePointLocation(
      final float x, final float y, final float endX, final float endY) {
    final float mag = this.getMagnification();

    // the location
    this.setLocation(x, y);

    // width
    final float w = (endX - x) / mag;
    this.setWidth(w);

    // height
    final float h = (endY - y) / mag;
    this.setHeight(h);

    return true;
  }

  @Override
  public boolean readProperty(final Element el) {
    if (super.readProperty(el) == false) {
      return false;
    }

    String str = null;
    Number num = null;
    Boolean b = null;
    Color cl = null;

    // visible
    str = el.getAttribute(KEY_VISIBLE);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      this.setVisible(b);
    }

    // x axis visible
    str = el.getAttribute(KEY_SCALE_X_AXIS_VISIBLE);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      this.setHorizontalVisible(b);
    }

    // x axis title downside
    str = el.getAttribute(KEY_SCALE_X_AXIS_TITLE_DOWNSIDE);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      this.setHorizontalTextDownside(b);
    }

    // x axis text
    str = el.getAttribute(KEY_SCALE_X_AXIS_TEXT);
    if (str.length() != 0) {
      this.setHorizontalText(str);
    }

    // x axis unit
    str = el.getAttribute(KEY_SCALE_X_AXIS_UNIT);
    if (str.length() != 0) {
      this.setHorizontalUnit(str);
    }

    // y axis visible
    str = el.getAttribute(KEY_SCALE_Y_AXIS_VISIBLE);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      this.setVerticalVisible(b);
    }

    // y axis title leftside
    str = el.getAttribute(KEY_SCALE_Y_AXIS_TITLE_LEFTSIDE);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      this.setVerticalTextLeftside(b);
    }

    // y axis text
    str = el.getAttribute(KEY_SCALE_Y_AXIS_TEXT);
    if (str.length() != 0) {
      this.setVerticalText(str);
    }

    // y axis unit
    str = el.getAttribute(KEY_SCALE_Y_AXIS_UNIT);
    if (str.length() != 0) {
      this.setVerticalUnit(str);
    }

    // line color
    str = el.getAttribute(KEY_SCALE_LINE_COLOR);
    if (str.length() != 0) {
      cl = SGUtilityText.parseColorIncludingList(str);
      if (cl == null) {
        return false;
      }
      if (this.setLineColor(cl) == false) {
        return false;
      }
    }

    // font color
    str = el.getAttribute(KEY_SCALE_FONT_COLOR);
    if (str.length() != 0) {
      cl = SGUtilityText.parseColorIncludingList(str);
      if (cl == null) {
        return false;
      }
      if (this.setStringColor(cl) == false) {
        return false;
      }
    }

    // angle
    str = el.getAttribute(KEY_SCALE_TEXT_ANGLE);
    if (str.length() != 0) {
      num = SGUtilityText.getFloat(str);
      if (num == null) {
        return false;
      }
      final float value = num.floatValue();
      this.setTextAngle(value);
    }

    return true;
  }

  @Override
  public boolean setMagnification(float mag) {
    this.getHorizontalStringElement().setMagnification(mag);
    this.getVerticalStringElement().setMagnification(mag);
    return this.updateDrawingElementsLocation();
  }

  protected boolean updateDrawingElementsLocation() {
    final SGDrawingElementLine hLine = (SGDrawingElementLine) this.getHorizontalLine();
    final SGDrawingElementLine vLine = (SGDrawingElementLine) this.getVerticalLine();

    Point2D.Float jointPos = (Point2D.Float) this.getJoint();
    Point2D.Float hPos = (Point2D.Float) this.getHorizontalEnd();
    Point2D.Float vPos = (Point2D.Float) this.getVerticalEnd();
    final SGTuple2f joint = new SGTuple2f(jointPos.x, jointPos.y);
    final SGTuple2f hEnd = new SGTuple2f(hPos.x, hPos.y);
    final SGTuple2f vEnd = new SGTuple2f(vPos.x, vPos.y);

    hLine.setTermPoints(joint, hEnd);
    vLine.setTermPoints(joint, vEnd);

    this.updateLabelLocation();

    return true;
  }

  protected void updateLabelLocation() {
    final float mag = this.getMagnification();

    final SGDrawingElementString2DExtended hStr =
        (SGDrawingElementString2DExtended) this.getHorizontalStringElement();
    final SGDrawingElementString2DExtended vStr =
        (SGDrawingElementString2DExtended) this.getVerticalStringElement();

    final float width = mag * this.getWidth();
    final float height = mag * this.getHeight();
    final float space = mag * this.getSpace();

    // horizontal string
    final Rectangle2D hRect = hStr.getElementBounds();
    final float hRectWidth = (float) hRect.getWidth();
    final float hRectHeight = (float) hRect.getHeight();
    final float hDefaultX = this.getX() + width / 2.0f - hRectWidth / 2.0f;
    final float hDefaultY;
    if (this.isHorizontalTextDownside()) {
      hDefaultY = this.getY() + space;
    } else {
      hDefaultY = this.getY() - space - hRectHeight;
    }
    hStr.setLocation(hDefaultX, hDefaultY);
    final float hStrX = hDefaultX + (hDefaultX - (float) hStr.getElementBounds().getX());
    final float hStrY = hDefaultY + (hDefaultY - (float) hStr.getElementBounds().getY());
    hStr.setLocation(hStrX, hStrY);

    // vertical string
    final Rectangle2D vRect = vStr.getElementBounds();
    final float vRectWidth = (float) vRect.getWidth();
    final float vRectHeight = (float) vRect.getHeight();
    final float vDefaultY = this.getY() + height / 2.0f - (vRectHeight / 2.0f);
    final float vDefaultX;
    if (this.isVerticalTextLeftside()) {
      vDefaultX = this.getX() - space - vRectWidth;
    } else {
      vDefaultX = this.getX() + space;
    }
    vStr.setLocation(vDefaultX, vDefaultY);
    final float vStrX = vDefaultX + (vDefaultX - (float) vStr.getElementBounds().getX());
    final float vStrY = vDefaultY + (vDefaultY - (float) vStr.getElementBounds().getY());
    vStr.setLocation(vStrX, vStrY);
  }

  public Point2D getJoint() {
    Point2D pos = new Point2D.Float(this.getX(), this.getY());
    return pos;
  }

  public float getEndPointX() {
    return this.getX() + this.getMagnification() * this.getWidth();
  }

  public Point2D getHorizontalEnd() {
    final Point2D pos = new Point2D.Float(this.getEndPointX(), this.getY());
    return pos;
  }

  public float getEndPointY() {
    return this.getY() + this.getMagnification() * this.getHeight();
  }

  public Point2D getVerticalEnd() {
    final Point2D pos = new Point2D.Float(this.getX(), this.getEndPointY());
    return pos;
  }

  public Point2D getHorizontalMiddle() {
    final Point2D pos =
        new Point2D.Float(
            this.getX() + this.getMagnification() * this.getWidth() / 2.0f, this.getY());
    return pos;
  }

  public Point2D getVerticalMiddle() {
    final Point2D pos =
        new Point2D.Float(
            this.getX(), this.getY() + this.getMagnification() * this.getHeight() / 2.0f);
    return pos;
  }

  protected Rectangle2D getLineBounds() {
    final SGDrawingElementLine hLine = (SGDrawingElementLine) this.getHorizontalLine();
    final SGDrawingElementLine vLine = (SGDrawingElementLine) this.getVerticalLine();
    ArrayList<Rectangle2D> rectList = new ArrayList<Rectangle2D>();
    rectList.add(hLine.getLineShape().getBounds2D());
    rectList.add(vLine.getLineShape().getBounds2D());
    Rectangle2D rect = SGUtility.createUnion(rectList);
    return rect;
  }

  public Rectangle2D getHorizontalStringBounds() {
    return ((SGDrawingElementString2DExtended) this.getHorizontalStringElement())
        .getElementBounds();
  }

  public Rectangle2D getVerticalStringBounds() {
    return ((SGDrawingElementString2DExtended) this.getVerticalStringElement()).getElementBounds();
  }

  public Rectangle2D getElementBounds() {
    ArrayList<Rectangle2D> rectList = new ArrayList<Rectangle2D>();
    rectList.add(this.getLineBounds());
    rectList.add(this.getHorizontalStringBounds());
    rectList.add(this.getVerticalStringBounds());
    Rectangle2D rect = SGUtility.createUnion(rectList);
    return rect;
  }

  /**
   * Paint this object.
   *
   * @param g2d graphics context
   */
  public void paint(final Graphics2D g2d) {
    g2d.setPaint(this.getColor());

    // lines
    final float lineWidth = this.getMagnification() * this.getLineWidth();
    g2d.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
    final Shape hLineShape = ((SGDrawingElementLine) this.getHorizontalLine()).getLineShape();
    final Shape vLineShape = ((SGDrawingElementLine) this.getVerticalLine()).getLineShape();

    // string
    SGDrawingElementString2DExtended hStr =
        (SGDrawingElementString2DExtended) this.getHorizontalStringElement();
    SGDrawingElementString2DExtended vStr =
        (SGDrawingElementString2DExtended) this.getVerticalStringElement();

    if (this.isHorizontalVisible() && this.isVerticalVisible()) {
      GeneralPath gp = new GeneralPath();
      gp.append(hLineShape, true);
      gp.append(vLineShape, true);
      g2d.draw(gp);
      hStr.paint(g2d);
      vStr.paint(g2d);
    } else {
      if (this.isHorizontalVisible()) {
        g2d.draw(hLineShape);
        hStr.paint(g2d);
      }
      if (this.isVerticalVisible()) {
        g2d.draw(vLineShape);
        vStr.paint(g2d);
      }
    }
  }

  /**
   * Paint this object with given clipping rectangle.
   *
   * @param g2d graphics context
   * @param clipRect clipping rectangle
   */
  public void paint(final Graphics2D g2d, final Rectangle2D clipRect) {
    this.paint(g2d);
  }
}

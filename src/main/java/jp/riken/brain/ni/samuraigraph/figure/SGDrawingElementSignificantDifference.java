package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSignificantDifferenceConstants.*;

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

/** Symbol of the significant difference. */
public abstract class SGDrawingElementSignificantDifference
    extends SGDrawingElementLineAndStringSymbol {

  private float mWidth;

  private float mVerticalHeight1;

  private float mVerticalHeight2;

  private SGDrawingElementString mStringElement;

  private SGDrawingElementLine mHorizontalLine;

  private SGDrawingElementLine mVerticalLine1;

  private SGDrawingElementLine mVerticalLine2;

  /** The flag of visibility of lines. */
  protected boolean mLineVisibleFlag = true;

  /** The default constructor. */
  public SGDrawingElementSignificantDifference() {
    super();
    this.mStringElement = this.createString();
    this.mHorizontalLine = this.createLine();
    this.mVerticalLine1 = this.createLine();
    this.mVerticalLine2 = this.createLine();
  }

  public SGDrawingElementSignificantDifference(
      final float x, final float y, final float w, final float hl, final float hr) {
    super();
    this.setLocation(x, y);
    this.setSize(w, hl, hr);
  }

  @Override
  public void dispose() {
    super.dispose();
    this.mHorizontalLine.dispose();
    this.mVerticalLine1.dispose();
    this.mVerticalLine2.dispose();
    this.mStringElement.dispose();
    this.mHorizontalLine = null;
    this.mVerticalLine1 = null;
    this.mVerticalLine2 = null;
    this.mStringElement = null;
  }

  @Override
  public boolean contains(final int x, final int y) {
    if (this.isLineVisible()) {
      if (this.mHorizontalLine.contains(x, y)) {
        return true;
      }

      if (this.mVerticalLine1.contains(x, y)) {
        return true;
      }

      if (this.mVerticalLine2.contains(x, y)) {
        return true;
      }
    }

    if (this.mStringElement.contains(x, y)) {
      return true;
    }

    return false;
  }

  public float getX1() {
    return this.getX();
  }

  public float getX2() {
    return this.getX1() + this.getMagnification() * this.getWidth();
  }

  public float getY1() {
    return this.getY() + this.getMagnification() * this.getVerticalHeight1();
  }

  public float getY2() {
    return this.getY() + this.getMagnification() * this.getVerticalHeight2();
  }

  public float getWidth() {
    return this.mWidth;
  }

  public float getVerticalHeight1() {
    return this.mVerticalHeight1;
  }

  public float getVerticalHeight2() {
    return this.mVerticalHeight2;
  }

  public final SGDrawingElementString getStringElement() {
    return this.mStringElement;
  }

  protected final SGDrawingElementLine getHorizontalLine() {
    return this.mHorizontalLine;
  }

  protected final SGDrawingElementLine getVerticalLine1() {
    return this.mVerticalLine1;
  }

  protected final SGDrawingElementLine getVerticalLine2() {
    return this.mVerticalLine2;
  }

  public String getText() {
    return this.mStringElement.getString();
  }

  /**
   * Sets the color.
   *
   * @param color the color to set
   */
  @Override
  public boolean setColor(final Color color) {
    if (super.setColor(color) == false) {
      return false;
    }
    this.mStringElement.setColor(color);
    return true;
  }

  public boolean setSize(final float w, final float h1, final float h2) {
    this.setWidth(w);
    this.setVerticalHeight1(h1);
    this.setVerticalHeight2(h2);
    return true;
  }

  public boolean setWidth(final float w) {
    this.mWidth = w;
    return true;
  }

  public boolean setWidth(final float w, final String unit) {
    return this.setWidth((float) SGUtilityText.convertToPoint(w, unit));
  }

  public boolean setVerticalHeight1(final float h) {
    this.mVerticalHeight1 = h;
    return true;
  }

  public boolean setVerticalHeight1(final float h, final String unit) {
    return this.setVerticalHeight1((float) SGUtilityText.convertToPoint(h, unit));
  }

  public boolean setVerticalHeight2(final float h) {
    this.mVerticalHeight2 = h;
    return true;
  }

  public boolean setVerticalHeight2(final float h, final String unit) {
    return this.setVerticalHeight2((float) SGUtilityText.convertToPoint(h, unit));
  }

  public boolean setText(final String text) {
    if (text == null) {
      throw new IllegalArgumentException("text==null");
    }
    this.mStringElement.setString(text);
    return true;
  }

  /**
   * Sets the line width.
   *
   * @param lw the line width to set
   */
  @Override
  public boolean setLineWidth(final float lw) {
    if (super.setLineWidth(lw) == false) {
      return false;
    }
    this.mHorizontalLine.setLineWidth(lw);
    this.mVerticalLine1.setLineWidth(lw);
    this.mVerticalLine2.setLineWidth(lw);
    return true;
  }

  @Override
  public boolean setFont(final String name, final int style, final float size) {
    if (super.setFont(name, style, size) == false) {
      return false;
    }
    this.mStringElement.setFont(name, style, size);
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

  public boolean isLineVisible() {
    return this.mLineVisibleFlag;
  }

  public boolean setLineVisible(final boolean b) {
    this.mLineVisibleFlag = b;
    return true;
  }

  public boolean isFlippingHorizontal() {
    return (this.mWidth < 0.0f);
  }

  public boolean isFlippingVertical() {
    return (this.isFlippingVertical1() && this.isFlippingVertical2());
  }

  public boolean isFlippingVertical1() {
    return (this.mVerticalHeight1 < 0.0f);
  }

  public boolean isFlippingVertical2() {
    return (this.mVerticalHeight2 < 0.0f);
  }

  public boolean isFlippingVerticalLeft() {
    return (!this.isFlippingHorizontal() ? this.isFlippingVertical1() : this.isFlippingVertical2());
  }

  public boolean isFlippingVerticalRight() {
    return (!this.isFlippingHorizontal() ? this.isFlippingVertical2() : this.isFlippingVertical1());
  }

  public float getLeftHeight() {
    if (!this.isFlippingHorizontal()) {
      return this.getVerticalHeight1();
    }
    return this.getVerticalHeight2();
  }

  public float getRightHeight() {
    if (!this.isFlippingHorizontal()) {
      return this.getVerticalHeight2();
    }
    return this.getVerticalHeight1();
  }

  public boolean setLeftHeight(final float h) {
    if (!this.isFlippingHorizontal()) {
      this.setVerticalHeight1(h);
    } else {
      this.setVerticalHeight2(h);
    }
    return true;
  }

  public boolean setRightHeight(final float h) {
    if (!this.isFlippingHorizontal()) {
      this.setVerticalHeight2(h);
    } else {
      this.setVerticalHeight1(h);
    }
    return true;
  }

  public boolean setNodePointLocation(
      final float x1, final float y1, final float x2, final float y2, final float y) {
    final float mag = this.getMagnification();

    // the location
    this.setLocation(x1, y);

    // width
    final float w = (x2 - x1) / mag;
    this.setWidth(w);

    // height
    final float h1 = (y1 - y) / mag;
    final float h2 = (y2 - y) / mag;
    this.setVerticalHeight1(h1);
    this.setVerticalHeight2(h2);

    return true;
  }

  @Override
  public SGProperties getProperties() {
    SigDiffProperties p = new SigDiffProperties();
    if (this.getProperties(p) == false) {
      return null;
    }
    return p;
  }

  @Override
  public boolean getProperties(SGProperties p) {
    if ((p instanceof SigDiffProperties) == false) {
      return false;
    }
    if (super.getProperties(p) == false) {
      return false;
    }
    SigDiffProperties sp = (SigDiffProperties) p;
    sp.mText = this.getText();
    sp.mLineVisible = this.mLineVisibleFlag;
    return true;
  }

  @Override
  public boolean setProperties(final SGProperties p) {
    if ((p instanceof SigDiffProperties) == false) {
      return false;
    }
    if (super.setProperties(p) == false) {
      return false;
    }
    SigDiffProperties sp = (SigDiffProperties) p;
    this.setText(sp.mText);
    this.setLineVisible(sp.mLineVisible);
    return true;
  }

  @Override
  public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    SGPropertyMap map = super.getPropertyFileMap(params);
    SGPropertyUtility.addProperty(map, KEY_SIGDIFF_TEXT, this.getText());
    SGPropertyUtility.addProperty(map, KEY_SIGDIFF_LINE_VISIBLE, this.isLineVisible());
    SGPropertyUtility.addProperty(map, KEY_COLOR, this.getColor());
    return map;
  }

  @Override
  public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
    SGPropertyMap map = super.getCommandPropertyMap(params);
    SGPropertyUtility.addQuotedStringProperty(map, COM_SIGDIFF_TEXT, this.getText());
    SGPropertyUtility.addProperty(map, COM_SIGDIFF_LINE_VISIBLE, this.isLineVisible());
    SGPropertyUtility.addProperty(map, COM_SIGDIFF_COLOR, this.getColor());
    return map;
  }

  @Override
  public boolean readProperty(final Element el) {

    String str = null;
    Boolean b = null;
    Color cl = null;

    if (super.readProperty(el) == false) {
      return false;
    }

    // text
    str = el.getAttribute(KEY_SIGDIFF_TEXT);
    if (str.length() != 0) {
      final String text = str;
      if (this.setText(text) == false) {
        return false;
      }
    }

    // line visible
    str = el.getAttribute(KEY_SIGDIFF_LINE_VISIBLE);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      final boolean lineVisible = b.booleanValue();
      if (this.setLineVisible(lineVisible) == false) {
        return false;
      }
    }

    // color
    str = el.getAttribute(KEY_DRAWING_ELEMENT_COLORS);
    if (str.length() != 0) {
      cl = SGUtilityText.parseColorIncludingList(str);
      if (cl == null) {
        return false;
      }
      if (this.setColor(cl) == false) {
        return false;
      }
    }

    return true;
  }

  public static class SigDiffProperties extends LineAndStringProperties {

    String mText;

    boolean mLineVisible;

    public SigDiffProperties() {
      super();
    }

    @Override
    public boolean equals(final Object obj) {
      if (!(obj instanceof SigDiffProperties)) {
        return false;
      }
      if (super.equals(obj) == false) {
        return false;
      }

      SigDiffProperties p = (SigDiffProperties) obj;
      if (SGUtility.equals(p.mText, this.mText) == false) {
        return false;
      }
      if (p.mLineVisible != this.mLineVisible) {
        return false;
      }
      return true;
    }
  }

  /** */

  /** */

  /** */
  protected SGDrawingElementString createString() {
    return new SGDrawingElementString2DExtended("*");
  }

  protected SGDrawingElementLine createLine() {
    return new SigDiffLine();
  }

  /** A line in a significant different symbol. */
  private static class SigDiffLine extends SGSimpleLine2D {
    public SigDiffLine() {
      super();
    }
  }

  /** */
  protected boolean createDrawingElement() {
    final float mag = this.getMagnification();
    final float width = mag * this.getWidth();
    final float h1 = mag * this.getVerticalHeight1();
    final float h2 = mag * this.getVerticalHeight2();
    final float space = mag * this.getSpace();

    final SGDrawingElementString2DExtended str =
        (SGDrawingElementString2DExtended) this.getStringElement();
    final SGDrawingElementLine hLine = (SGDrawingElementLine) this.getHorizontalLine();
    final SGDrawingElementLine pLine1 = (SGDrawingElementLine) this.getVerticalLine1();
    final SGDrawingElementLine pLine2 = (SGDrawingElementLine) this.getVerticalLine2();

    final SGTuple2f leftTop = new SGTuple2f(this.getX(), this.getY());

    final SGTuple2f rightTop = new SGTuple2f(this.getX() + width, this.getY());

    final SGTuple2f leftBottom = new SGTuple2f(this.getX(), this.getY() + h1);

    final SGTuple2f rightBottom = new SGTuple2f(this.getX() + width, this.getY() + h2);

    hLine.setTermPoints(leftTop, rightTop);
    pLine1.setTermPoints(leftTop, leftBottom);
    pLine2.setTermPoints(rightTop, rightBottom);

    final Rectangle2D rect = str.getElementBounds();
    final float strLeading = str.getLeading() * 2.0f;
    final float rectWidth = (float) rect.getWidth();
    final float rectHeight = (float) rect.getHeight();

    final float strX = this.getX() + width / 2.0f - (rectWidth / 2.0f);
    float strY;
    if (!this.isFlippingVertical()) {
      strY = this.getY() - space - rectHeight - strLeading;
    } else {
      strY = this.getY() + space + strLeading;
    }

    str.setLocation(strX, strY);

    return true;
  }

  /** */
  public Point2D getJoint1() {
    Point2D pos = new Point2D.Float(this.getX1(), this.getY());
    return pos;
  }

  /** */
  public Point2D getJoint2() {
    Point2D pos = new Point2D.Float(this.getX2(), this.getY());
    return pos;
  }

  /** */
  public Point2D getLeftJoint() {
    final Point2D pos;
    if (!this.isFlippingHorizontal()) {
      pos = this.getJoint1();
    } else {
      pos = this.getJoint2();
    }
    return pos;
  }

  /** */
  public Point2D getLeftTerm() {
    final Point2D pos = this.getLeftJoint();
    pos.setLocation(pos.getX(), pos.getY() + this.getMagnification() * this.getLeftHeight());
    return pos;
  }

  /** */
  public Point2D getRightJoint() {
    final Point2D pos;
    if (!this.isFlippingHorizontal()) {
      pos = this.getJoint2();
    } else {
      pos = this.getJoint1();
    }
    return pos;
  }

  /** */
  public Point2D getRightTerm() {
    final Point2D pos = this.getRightJoint();
    pos.setLocation(pos.getX(), pos.getY() + this.getMagnification() * this.getRightHeight());
    return pos;
  }

  /** */
  public Point2D getHorizontalMiddle() {
    final Point2D pos =
        new Point2D.Float(
            this.getX1() + this.getMagnification() * this.getWidth() / 2.0f, this.getY());
    return pos;
  }

  /** */
  public Point2D getLeftMiddle() {
    final Point2D pos = new Point2D.Float();
    final Point2D joint = this.getLeftJoint();
    final Point2D term = this.getLeftTerm();
    pos.setLocation((joint.getX() + term.getX()) / 2.0, (joint.getY() + term.getY()) / 2.0);
    return pos;
  }

  /** */
  public Point2D getRightMiddle() {
    final Point2D pos = new Point2D.Float();
    final Point2D joint = this.getRightJoint();
    final Point2D term = this.getRightTerm();
    pos.setLocation((joint.getX() + term.getX()) / 2.0, (joint.getY() + term.getY()) / 2.0);
    return pos;
  }

  /** */
  public Shape getConnectedShape() {
    final SGDrawingElementLine hLine = (SGDrawingElementLine) this.getHorizontalLine();
    final SGDrawingElementLine pLine1 = (SGDrawingElementLine) this.getVerticalLine1();
    final SGDrawingElementLine pLine2 = (SGDrawingElementLine) this.getVerticalLine2();
    GeneralPath gp = new GeneralPath();
    gp.append(pLine1.getLineShape(), true);
    gp.append(hLine.getLineShape(), true);
    gp.append(pLine2.getLineShape(), true);
    return gp;
  }

  /**
   * Sets the magnification.
   *
   * @param mag the magnification to set
   */
  public boolean setMagnification(final float mag) {
    this.getStringElement().setMagnification(mag);
    return this.createDrawingElement();
  }

  /** */
  public Rectangle2D getElementBounds() {
    ArrayList<Rectangle2D> rectList = new ArrayList<Rectangle2D>();
    rectList.add(this.getLineBounds());
    rectList.add(this.getStringBounds());
    Rectangle2D rect = SGUtility.createUnion(rectList);

    return rect;
  }

  /** */
  public Rectangle2D getLineBounds() {
    final SGDrawingElementLine hLine = (SGDrawingElementLine) this.getHorizontalLine();
    final SGDrawingElementLine pLine1 = (SGDrawingElementLine) this.getVerticalLine1();
    final SGDrawingElementLine pLine2 = (SGDrawingElementLine) this.getVerticalLine2();
    ArrayList<Rectangle2D> rectList = new ArrayList<Rectangle2D>();
    rectList.add(hLine.getLineShape().getBounds2D());
    rectList.add(pLine1.getLineShape().getBounds2D());
    rectList.add(pLine2.getLineShape().getBounds2D());
    Rectangle2D rect = SGUtility.createUnion(rectList);

    return rect;
  }

  /** */
  public Rectangle2D getStringBounds() {
    return ((SGDrawingElementString2DExtended) this.getStringElement()).getElementBounds();
  }

  /**
   * Paint this object.
   *
   * @param g2d graphics context
   */
  public void paint(final Graphics2D g2d) {
    g2d.setPaint(this.getColor());

    if (this.isLineVisible()) {
      final float lineWidth = this.getMagnification() * this.getLineWidth();
      g2d.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

      Shape sh = this.getConnectedShape();
      g2d.draw(sh);
    }

    // string
    SGDrawingElementString2DExtended sElement =
        (SGDrawingElementString2DExtended) this.getStringElement();
    sElement.paint(g2d);
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

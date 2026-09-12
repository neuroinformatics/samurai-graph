package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGIConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGILegendConstants.*;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

/** Appearance settings of a legend element. */
final class SGFigureElementLegendStyle {

  private float mFontSize;

  private int mFontStyle;

  private String mFontName;

  private Color mFontColor;

  private float mSymbolSpan;

  private float mFrameLineWidth;

  private Color mFrameLineColor;

  private final SGFillPaint mBackgroundPaint = new SGFillPaint();

  private boolean mLegendVisibleFlag = true;

  private boolean mFrameVisibleFlag = true;

  String getFontName() {
    return this.mFontName;
  }

  int getFontStyle() {
    return this.mFontStyle;
  }

  float getFontSize() {
    return this.mFontSize;
  }

  float getFontSize(final String unit) {
    return (float) SGUtilityText.convertFromPoint(this.getFontSize(), unit);
  }

  float getFrameLineWidth() {
    return this.mFrameLineWidth;
  }

  float getFrameLineWidth(final String unit) {
    return (float) SGUtilityText.convertFromPoint(this.getFrameLineWidth(), unit);
  }

  Color getFrameColor() {
    return this.mFrameLineColor;
  }

  Color getBackgroundColor() {
    return this.mBackgroundPaint.getColor();
  }

  int getBackgroundTransparency() {
    return this.mBackgroundPaint.getTransparencyPercent();
  }

  Color getFontColor() {
    return this.mFontColor;
  }

  float getSymbolSpan() {
    return this.mSymbolSpan;
  }

  float getSymbolSpan(final String unit) {
    return (float) SGUtilityText.convertFromPoint(this.getSymbolSpan(), unit);
  }

  boolean setSymbolSpan(final float span) {
    if (span < 0.0f) {
      throw new IllegalArgumentException("span < 0.0f");
    }
    this.mSymbolSpan = span;
    return true;
  }

  boolean setSymbolSpan(final float span, final String unit) {
    final Float sNew =
        SGUtility.calcPropertyValue(
            span,
            unit,
            SYMBOL_SPAN_UNIT,
            SYMBOL_SPAN_MIN,
            SYMBOL_SPAN_MAX,
            SYMBOL_SPAN_MINIMAL_ORDER);
    if (sNew == null) {
      return false;
    }
    return this.setSymbolSpan(sNew);
  }

  void setVisible(final boolean b) {
    this.mLegendVisibleFlag = b;
  }

  boolean isVisible() {
    return this.mLegendVisibleFlag;
  }

  void setFrameVisible(final boolean b) {
    this.mFrameVisibleFlag = b;
  }

  boolean isFrameVisible() {
    return this.mFrameVisibleFlag;
  }

  boolean setBackgroundTransparent(final int percentAlpha) {
    return this.mBackgroundPaint.setTransparency(percentAlpha);
  }

  boolean setFrameLineWidth(final float lw) {
    if (lw < 0.0f) {
      throw new IllegalArgumentException("lw < 0.0f");
    }
    this.mFrameLineWidth = lw;
    return true;
  }

  boolean setFrameLineWidth(final float lw, final String unit) {
    final Float lwNew = SGUtility.getLineWidth(lw, unit);
    if (lwNew == null) {
      return false;
    }
    return this.setFrameLineWidth(lwNew);
  }

  boolean setFrameLineColor(final Color cl) {
    if (cl == null) {
      throw new IllegalArgumentException("cl == null");
    }
    this.mFrameLineColor = cl;
    return true;
  }

  boolean setBackgroundColor(final Color cl) {
    if (cl == null) {
      throw new IllegalArgumentException("cl == null");
    }
    return this.mBackgroundPaint.setColor(cl);
  }

  boolean setFontSize(final float size) {
    return this.setFont(this.getFontName(), this.getFontStyle(), size);
  }

  boolean setFontSize(final float size, final String unit) {
    final Float sNew = SGUtility.getFontSize(size, unit);
    if (sNew == null) {
      return false;
    }
    return this.setFontSize(sNew);
  }

  boolean setFontStyle(final int style) {
    if (SGUtilityText.isValidFontStyle(style) == false) {
      return false;
    }
    return this.setFont(this.getFontName(), style, this.getFontSize());
  }

  boolean setFontColor(final Color color) {
    if (color == null) {
      throw new IllegalArgumentException("color == null");
    }
    this.mFontColor = color;
    return true;
  }

  boolean setFontName(final String name) {
    return this.setFont(name, this.getFontStyle(), this.getFontSize());
  }

  /**
   * Sets the font parameters.
   *
   * @param name the font name
   * @param style the font style
   * @param size the font size in units of pt
   * @return true if the values are changed
   */
  boolean setFont(final String name, final int style, final float size) {
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
    return changed;
  }

  /** Releases the reference to the fill paint. */
  void clear() {
    this.mFontName = null;
    this.mFrameLineColor = null;
  }

  SGFillPaint getBackgroundPaint() {
    return this.mBackgroundPaint;
  }

  void setFontFields(final String name, final int style, final float size, final Color color) {
    this.mFontName = name;
    this.mFontStyle = style;
    this.mFontSize = size;
    this.mFontColor = color;
  }
}

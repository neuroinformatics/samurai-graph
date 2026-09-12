package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.*;
import java.awt.Color;
import java.awt.geom.*;
import java.util.*;
import java.util.ArrayList;
import jp.riken.brain.ni.samuraigraph.base.*;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.data.*;
import jp.riken.brain.ni.samuraigraph.figure.*;

class LegendProperties extends SGProperties {

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

  ArrayList<SGIChildObject> visibleElementGroupList = new ArrayList<SGIChildObject>();

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

  /** */
  public boolean equals(final Object obj) {

    if ((obj instanceof LegendProperties) == false) return false;

    LegendProperties p = (LegendProperties) obj;

    if (p.x != this.x) return false;
    if (p.y != this.y) return false;
    if (p.visible != this.visible) return false;
    if (p.frameLineVisible != this.frameLineVisible) return false;
    if (p.frameLineWidth != this.frameLineWidth) return false;
    if (p.frameLineColor.equals(this.frameLineColor) == false) return false;
    if (p.fontName.equals(this.fontName) == false) return false;
    if (p.fontSize != this.fontSize) return false;
    if (p.fontStyle != this.fontStyle) return false;
    if (p.stringColor.equals(this.stringColor) == false) return false;
    if (p.backgroundPaint.equals(this.backgroundPaint) == false) return false;
    if (p.symbolSpan != this.symbolSpan) return false;
    if (p.xAxis.equals(this.xAxis) == false) return false;
    if (p.yAxis.equals(this.yAxis) == false) return false;
    if (p.visibleElementGroupList.equals(this.visibleElementGroupList) == false) {
      return false;
    }

    return true;
  }

  /**
   * Returns a string representation.
   *
   * @return a string representation
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
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
    sb.append(", groupSetList=");
    sb.append(this.visibleElementGroupList.toString());
    sb.append("]");
    return sb.toString();
  }
}

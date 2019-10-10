package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementErrorBar;

/**
 * 
 * 
 */
public abstract class SGDrawingElementErrorBar2D extends SGDrawingElementErrorBar
        implements SGIDrawingElementJava2D {

    /**
     * 
     */
    public SGDrawingElementErrorBar2D() {
        super();
    }

    /**
     * 
     */
    public boolean contains(int x, int y) {
        final int style = this.getErrorBarStyle();
        final SGDrawingElementArrow2D lArrow = (SGDrawingElementArrow2D) this.getLowerArrow();
        final SGDrawingElementArrow2D uArrow = (SGDrawingElementArrow2D) this.getUpperArrow();
        switch (style) {
        case ERROR_BAR_BOTHSIDES:
            if (lArrow.contains(x, y)) {
                return true;
            }
            if (uArrow.contains(x, y)) {
                return true;
            }
            break;
        case ERROR_BAR_DOWNSIDE:
            if (lArrow.contains(x, y)) {
                return true;
            }
            break;
        case ERROR_BAR_UPSIDE:
            if (uArrow.contains(x, y)) {
                return true;
            }
            break;
        default:
        }

//        if (this.isLowerVisible()) {
//            if (this.getLowerArrow().contains(x, y)) {
//                return true;
//            }
//        }
//        if (this.isUpperVisible()) {
//            if (this.getUpperArrow().contains(x, y)) {
//                return true;
//            }
//        }
        return false;
    }

    /**
     * Paint this object.
     * 
     * @param g2d
     *            graphics context
     */
    public void paint(Graphics2D g2d) {
        final int style = this.getErrorBarStyle();
        final SGDrawingElementArrow2D lArrow = (SGDrawingElementArrow2D) this.getLowerArrow();
        final SGDrawingElementArrow2D uArrow = (SGDrawingElementArrow2D) this.getUpperArrow();
        switch (style) {
        case ERROR_BAR_BOTHSIDES:
            lArrow.paint(g2d);
            uArrow.paint(g2d);
            break;
        case ERROR_BAR_DOWNSIDE:
            lArrow.paint(g2d);
            break;
        case ERROR_BAR_UPSIDE:
            uArrow.paint(g2d);
            break;
        default:
        }
        
//        if (this.isLowerVisible()) {
//            ((SGDrawingElementArrow2D) this.getLowerArrow()).paint(g2d);
//        }
//        if (this.isUpperVisible()) {
//            ((SGDrawingElementArrow2D) this.getUpperArrow()).paint(g2d);
//        }
    }

    /**
     * Paint this object with given clipping rectangle.
     * 
     * @param g2d
     *            graphics context
     * @param clipRect
     *            clipping rectangle
     */
    public void paint(Graphics2D g2d, Rectangle2D rect) {
        
        final int style = this.getErrorBarStyle();
        final SGDrawingElementArrow2D lArrow = (SGDrawingElementArrow2D) this.getLowerArrow();
        final SGDrawingElementArrow2D uArrow = (SGDrawingElementArrow2D) this.getUpperArrow();
        switch (style) {
        case ERROR_BAR_BOTHSIDES:
            lArrow.paint(g2d, rect);
            uArrow.paint(g2d, rect);
            break;
        case ERROR_BAR_DOWNSIDE:
            lArrow.paint(g2d, rect);
            break;
        case ERROR_BAR_UPSIDE:
            uArrow.paint(g2d, rect);
            break;
        default:
        }
        
//        if (this.isLowerVisible()) {
//            ((SGDrawingElementArrow2D) this.getLowerArrow()).paint(g2d, rect);
//        }
//        if (this.isUpperVisible()) {
//            ((SGDrawingElementArrow2D) this.getUpperArrow()).paint(g2d, rect);
//        }
    }

    /**
     * 
     */
    public Rectangle2D getElementBounds() {
        final int style = this.getErrorBarStyle();
        final SGDrawingElementArrow2D lArrow = (SGDrawingElementArrow2D) this.getLowerArrow();
        final SGDrawingElementArrow2D uArrow = (SGDrawingElementArrow2D) this.getUpperArrow();
        Rectangle2D rectRet = null;
        switch (style) {
        case ERROR_BAR_BOTHSIDES:
            Rectangle2D rectLower = ((SGDrawingElementArrow2D) this.getLowerArrow()).getElementBounds();
            Rectangle2D rectUpper = ((SGDrawingElementArrow2D) this.getUpperArrow()).getElementBounds();
            ArrayList<Rectangle2D> rectList = new ArrayList<Rectangle2D>();
            rectList.add(rectLower);
            rectList.add(rectUpper);
            Rectangle2D rectAll = SGUtility.createUnion(rectList);
            rectRet = rectAll;
            break;
        case ERROR_BAR_DOWNSIDE:
            rectRet = lArrow.getElementBounds();
            break;
        case ERROR_BAR_UPSIDE:
            rectRet = uArrow.getElementBounds();
            break;
        default:
        }
        
        return rectRet;

//        if (this.isLowerVisible() && this.isUpperVisible()) {
//            Rectangle2D rectLower = ((SGDrawingElementArrow2D) this.getLowerArrow()).getElementBounds();
//            Rectangle2D rectUpper = ((SGDrawingElementArrow2D) this.getUpperArrow()).getElementBounds();
//            ArrayList rectList = new ArrayList();
//            rectList.add(rectLower);
//            rectList.add(rectUpper);
//            Rectangle2D rectAll = SGUtility.createUnion(rectList);
//            return rectAll;
//        } else if (this.isLowerVisible() && !this.isUpperVisible()) {
//            return ((SGDrawingElementArrow2D) this.getLowerArrow()).getElementBounds();
//        } else if (!this.isLowerVisible() && this.isUpperVisible()) {
//            return ((SGDrawingElementArrow2D) this.getUpperArrow()).getElementBounds();
//        } else {
//            return new Rectangle2D.Double();
//        }
    }

}

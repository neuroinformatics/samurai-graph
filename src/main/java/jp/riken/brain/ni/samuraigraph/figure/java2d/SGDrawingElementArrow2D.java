package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementArrow;
import jp.riken.brain.ni.samuraigraph.figure.SGIArrowConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;

/**
 * An arrow class using Java2D.
 * 
 */
public abstract class SGDrawingElementArrow2D extends SGDrawingElementArrow implements
        SGIDrawingElementJava2D {

    /**
     * The default constructor.
     */
    public SGDrawingElementArrow2D() {
        super();
    }

//    /**
//     * Returns a stroke.
//     * 
//     * @return a stroke
//     */
//    protected abstract SGStroke getStroke();

    /**
     * 
     */
    public Rectangle2D getElementBounds() {
        SGDrawingElementLine2D line = (SGDrawingElementLine2D) this.getLine();
        SGDrawingElementSymbol2D start = (SGDrawingElementSymbol2D) this
                .getStartHead();
        SGDrawingElementSymbol2D end = (SGDrawingElementSymbol2D) this
                .getEndHead();
        ArrayList<Rectangle2D> list = new ArrayList<Rectangle2D>();
        list.add(line.getElementBounds());
        list.add(start.getElementBounds());
        list.add(end.getElementBounds());
        Rectangle2D rect = SGUtility.createUnion(list);
        return rect;
    }

    /**
     * Paint this object.
     * 
     * @param g2d
     *            graphics context
     */
    public void paint(Graphics2D g2d) {
        if (this.isVisible() == false) {
            return;
        }
        if (this.getMagnitude() == 0.0f) {
        	this.paintZeroLengthArrow(g2d, this.getStart());
        	return;
        }
        final SGDrawingElementLine2D line = (SGDrawingElementLine2D) this
                .getLine();
        final SGDrawingElementSymbol2D start = (SGDrawingElementSymbol2D) this
                .getStartHead();
        final SGDrawingElementSymbol2D end = (SGDrawingElementSymbol2D) this
                .getEndHead();
        line.paint(g2d);
        start.paint(g2d);
        end.paint(g2d);
    }

    /**
     * Paint this object with given clipping rectangle.
     * 
     * @param g2d
     *            graphics context
     * @param clipRect
     *            clipping rectangle
     */
    public void paint(Graphics2D g2d, Rectangle2D clipRect) {
        if (this.isVisible() == false) {
            return;
        }
        if (this.getMagnitude() == 0.0f) {
        	this.paintZeroLengthArrow(g2d, this.getStart());
        	return;
        }
        final SGDrawingElementLine2D line = (SGDrawingElementLine2D) this
                .getLine();
        final SGDrawingElementSymbol2D start = (SGDrawingElementSymbol2D) this
                .getStartHead();
        final SGDrawingElementSymbol2D end = (SGDrawingElementSymbol2D) this
                .getEndHead();
        line.paint(g2d, clipRect);
        start.paint(g2d, clipRect);
        end.paint(g2d, clipRect);
    }
    
    private void paintZeroLengthArrow(Graphics2D g2d, SGTuple2f point) {
    	final float lw = this.getMagnification() * this.getLineWidth();
    	final float lwHalf = lw / 2;
    	final float x = point.x - lwHalf;
    	final float y = point.y - lwHalf;
    	Shape circle = new Ellipse2D.Float(x, y, lw, lw);
    	g2d.setColor(this.getColor());
    	g2d.draw(circle);
    }

    /**
     * Returns the magnitude of this arrow.
     * 
     * @return the magnitude of this arrow
     */
    public float getMagnitude() {
        final SGTuple2f start = this.getStart();
        final SGTuple2f end = this.getEnd();
        final float x = start.x - end.x;
        final float y = start.y - end.y;
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Returns the gradient of this arrow.
     * 
     * @return the gradient of this arrow in units of radian
     */
    public float getGradient() {
        final SGTuple2f start = this.getStart();
        final SGTuple2f end = this.getEnd();
        return SGUtilityForFigureElementJava2D.getGradient(start.x, start.y, end.x, end.y);
    }

    protected void updateHeadAngle() {
        final SGDrawingElementLine2D line = (SGDrawingElementLine2D) this
                .getLine();
        final SGDrawingElementSymbol2D start = (SGDrawingElementSymbol2D) this
                .getStartHead();
        final SGDrawingElementSymbol2D end = (SGDrawingElementSymbol2D) this
                .getEndHead();
        final float pi = (float) Math.PI;
        final float angle = line.getGradient();
        start.setAngle(angle - 0.50f * pi);
        end.setAngle(angle + 0.50f * pi);
    }

    /**
     * Returns the shape of the start head.
     * 
     * @return a shape object
     */
    protected abstract Shape getStartHeadShape();

    /**
     * Returns the shape of the start head.
     * 
     * @return a shape object
     */
    protected abstract Shape getEndHeadShape();
    
    /**
     * Updates the head shape.
     *
     */
    protected abstract void updateHeadShape();

    /**
     * An inner class for arrow body.
     */
    protected static class ArrowBody extends SGDrawingElementLine2D {

        /**
         * The arrow.
         */
        protected SGDrawingElementArrow mArrow = null;
        
        /**
         * The default constructor.
         *
         */
        public ArrowBody(final SGDrawingElementArrow arrow) {
            super();
            this.mArrow = arrow;
        }
        
        /**
         * Returns a line object.
         * 
         * @return a line object
         */
        public Shape getLineShape() {
        	final float yStart, yEnd;
            final int aType = SGIArrowConstants.SYMBOL_TYPE_ARROW_HEAD;
        	final boolean startArrow = (this.mArrow.getStartHeadType() == aType);
        	final boolean endArrow = (this.mArrow.getEndHeadType() == aType);
            final float len = this.getMagnitude();
        	if (startArrow || endArrow) {
                final float open = this.mArrow.getHeadOpenAngle() * SGIConstants.RADIAN_DEGREE_RATIO;
                final float close = this.mArrow.getHeadCloseAngle() * SGIConstants.RADIAN_DEGREE_RATIO;
                final float tanOpen = (float) Math.tan(open);
                final float tanClose = (float) Math.tan(close);
                final float diff = (open <= close) ? this.getMagnification()
                        * this.mArrow.getHeadSize() * (1.0f - tanOpen / tanClose) : 0.0f;
                yStart = startArrow ? 0.50f * diff : 0.0f;
                yEnd = endArrow ? (len - 0.50f * diff) : len;
        	} else {
        		yStart = 0.0f;
        		yEnd = len;
        	}
            Line2D line = new Line2D.Float(0.0f, yStart, 0.0f, yEnd);
            Shape shape = this.getAffineTransform()
                    .createTransformedShape(line);
            return shape;
        }

        /**
         * Returns an affine transform.
         * 
         * @return an affine transform
         */
        private AffineTransform getAffineTransform() {
            AffineTransform af = new AffineTransform();

            // translate
            SGTuple2f start = this.getStart();
            af.translate(start.x, start.y);

            // rotate
            final double angle = this.getGradient() - 0.50 * Math.PI;
            af.rotate(angle);

            return af;
        }

        @Override
        public Color getColor() {
            return this.mArrow.getColor();
        }

        @Override
        protected SGStroke getStroke() {
            return this.mArrow.getStroke();
        }

        @Override
        public boolean setColor(Color cl) {
            // do nothing
            return true;
        }

        @Override
        public boolean setLineType(int type) {
            // do nothing
            return true;
        }

        @Override
        public boolean setLineWidth(float width) {
            // do nothing
            return true;
        }

        @Override
        public float getMagnification() {
            return this.mArrow.getMagnification();
        }

        @Override
        public boolean setMagnification(float mag) {
            // do nothing
            return true;
        }

        @Override
        public SGTuple2f getEnd() {
            return this.mArrow.getEnd();
        }

        @Override
        public SGTuple2f getStart() {
            return this.mArrow.getStart();
        }

        @Override
        public boolean setTermPoints(SGTuple2f start, SGTuple2f end) {
            // do nothing
            return true;
        }

    }

    /**
     * An inner class for arrow head.
     */
    protected static class ArrowHead extends SGDrawingElementSymbol2D {

        /**
         * The arrow.
         */
        protected SGDrawingElementArrow mArrow = null;
        
        /**
         * A flag whether this arrow head is on the head of the arrow.
         */
        protected boolean mStartFlag = true;
        
        /**
         * Builds this object.
         * 
         * @param arrow
         *           an arrow that this arrow head belongs to
         * @param start
         *           true for the head of the arrow
         */
        public ArrowHead(final SGDrawingElementArrow arrow, final boolean start) {
            super();
            this.mArrow = arrow;
            this.mStartFlag = start;
        }

        /**
         * Disposes this object.
         */
        public void dispose() {
            super.dispose();
            this.mArrow = null;
        }
        
        /**
         * Overrode not to draw line in the symbol with finite area.
         */
        protected void paintLine(Graphics2D g2d, Shape sh) {
            if (SGDrawingElementArrow.isLineTypeSymbol(this.getType())) {
                super.paintLine(g2d, sh);
            }
        }

        /**
         * Overrode not to draw line in the symbol with finite area.
         */
        protected void paintLine(Graphics2D g2d, Area clipArea, Shape sh) {
            if (SGDrawingElementArrow.isLineTypeSymbol(this.getType())) {
                super.paintLine(g2d, clipArea, sh);
            }
        }

        protected float getHeadOpenAngle() {
            return this.mArrow.getHeadOpenAngle();
        }
        
        protected float getHeadCloseAngle() {
            return this.mArrow.getHeadCloseAngle();
        }

        /**
         * Creates a shape.
         * 
         * @return a shape
         */
        protected Shape createShape() {
			final int type = this.getType();
			final float headSize = this.getMagnification() * this.getSize();
			final float open = this.getHeadOpenAngle();
			final float close = this.getHeadCloseAngle();
			return SGDrawingElementArrow2D.createHeadShape(type, headSize, open, close);
        }

        /**
		 * Paint inside the arrow head.
		 * 
		 * @param g2d
		 *            The graphics context.
		 * @param shape
		 *            Shape object of this arrow head.
		 */
        protected void paintInner(Graphics2D g2d, Shape shape) {
            if (!SGDrawingElementArrow.isLineTypeSymbol(this.getType())) {
                g2d.setPaint(this.getInnerColor());
                g2d.fill(shape);
            }
        }

        @Override
        public float getAngle() {
            final float angle = this.mArrow.getGradient();
            final float shift = (float) Math.PI / 2;
            if (this.mStartFlag) {
                return angle - shift;
            } else {
                return angle + shift;
            }
        }

        public Color getInnerColor() {
            return this.mArrow.getColor();
        }

        /**
         * @return null
         */
        @Override
        public SGIPaint getInnerPaint() {
            return null;
        }

        @Override
        public Color getLineColor() {
            return this.mArrow.getColor();
        }

        @Override
        public float getLineWidth() {
            return this.mArrow.getLineWidth();
        }

        @Override
        public boolean isLineVisible() {
            // always true
            return true;
        }

        @Override
        public float getSize() {
            return this.mArrow.getHeadSize();
        }

        @Override
        public int getType() {
            if (this.mStartFlag) {
                return this.mArrow.getStartHeadType();
            } else {
                return this.mArrow.getEndHeadType();
            }
        }

        @Override
        public boolean setAngle(float angle) {
            // do nothing
            return true;
        }

        @Override
        public boolean setInnerColor(Color color) {
            // do nothing
            return true;
        }

        @Override
        public boolean setLineColor(Color color) {
            // do nothing
            return true;
        }

        @Override
        public boolean setLineWidth(float lineWidth) {
            // do nothing
            return true;
        }

        @Override
        public boolean setLineVisible(boolean visible) {
            // do nothing
            return true;
        }

        @Override
        public boolean setSize(float size) {
            // do nothing
            return true;
        }

        @Override
        public boolean setType(int type) {
            // do nothing
            return true;
        }

        @Override
        public float getMagnification() {
            return this.mArrow.getMagnification();
        }

        @Override
        public boolean setMagnification(float mag) {
            // do nothing
            return true;
        }

        @Override
        public SGTuple2f getLocation() {
            if (this.mStartFlag) {
                return this.mArrow.getStart();
            } else {
                return this.mArrow.getEnd();
            }
        }
        
        public float getX() {
            return this.getLocation().x;
        }

        public float getY() {
            return this.getLocation().y;
        }

        @Override
        public boolean setLocation(float x, float y) {
            // do nothing
            return true;
        }

        @Override
        public boolean setLocation(SGTuple2f point) {
            // do nothing
            return true;
        }

        @Override
        public boolean setX(float x) {
            // do nothing
            return true;
        }

        @Override
        public boolean setY(float y) {
            // do nothing
            return true;
        }

		@Override
		protected Shape getShape() {
			if (this.mStartFlag) {
				return ((SGDrawingElementArrow2D) this.mArrow).getStartHeadShape();
			} else {
				return ((SGDrawingElementArrow2D) this.mArrow).getEndHeadShape();
			}
		}

		@Override
		protected void updateShape() {
            // do nothing
		}
    }

    /**
     * Creates a shape.
     * 
     * @param type
     *           the symbol type
     * @param headSize
     *           the head size
     * @param open
     *           the open angle in units of degree
     * @param close
     *           the close angle in units of degree
     * @return a shape object
     */
    protected static Shape createHeadShape(final int type, final float headSize, 
    		final float open, final float close) {
        Shape sh = null;

		// arrow head
		if (type == SYMBOL_TYPE_ARROW_HEAD) {
			if (close <= open) {
				return null;
			}
			final float tanOpen = (float) Math.tan(open * SGIConstants.RADIAN_DEGREE_RATIO);
			final float tanClose = (float) Math.tan(close * SGIConstants.RADIAN_DEGREE_RATIO);
			final float openSize = headSize * tanOpen;
			Point2D[] pointArray = new Point2D[4];
			pointArray[0] = new Point2D.Float(0, 0);
			pointArray[1] = new Point2D.Float(openSize, headSize);
			pointArray[2] = new Point2D.Float(0, headSize - openSize
					/ tanClose);
			pointArray[3] = new Point2D.Float(- openSize, headSize);
			Shape[] pathArray = new Line2D[pointArray.length];
			for (int ii = 0; ii < pathArray.length; ii++) {
				pathArray[ii] = new Line2D.Float(pointArray[ii],
						pointArray[(ii + 1) % pointArray.length]);
			}
			GeneralPath gp = new GeneralPath();
			for (int ii = 0; ii < pathArray.length; ii++) {
				gp.append(pathArray[ii], true);
			}
			sh = gp;
		} else if (type == SYMBOL_TYPE_ARROW) {
			Path2D gp = new Path2D.Float();
			final float tanOpen = (float) Math.tan(open * SGIConstants.RADIAN_DEGREE_RATIO);
			final float openSize = headSize * tanOpen;
			gp.moveTo(openSize, headSize);
			gp.lineTo(0, 0);
			gp.lineTo(- openSize, headSize);
			sh = gp;
		} else if (type == SYMBOL_TYPE_TRANSVERSELINE) {
			final float half = 0.50f * headSize;
            GeneralPath gp = new GeneralPath();
            gp.moveTo(- half, 0);
            gp.lineTo(0, 0);
            gp.lineTo(half, 0);
            sh = gp;
		} else if (type == SYMBOL_TYPE_VOID ) {
			// do nothing
		} else {
			sh = SGDrawingElementSymbol2D.createShape(type, headSize);
		}
		return sh;
    }
    
    protected boolean contains(final int x, final int y, SGTuple2f startLocation,
    		SGTuple2f endLocation) {
    	SGDrawingElementLine2D line = (SGDrawingElementLine2D) this.getLine();
        if (line.contains(x, y, startLocation, endLocation)) {
            return true;
        }
        SGDrawingElementSymbol2D startHead = (SGDrawingElementSymbol2D) this.getStartHead();
        if (this.isVisibleHead(startHead)) {
            if (startHead.contains(x, y, startLocation)) {
                return true;
            }
        }
        SGDrawingElementSymbol2D endHead = (SGDrawingElementSymbol2D) this.getEndHead();
        if (this.isVisibleHead(endHead)) {
            if (endHead.contains(x, y, endLocation)) {
                return true;
            }
        }
        return false;
    }

}

package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Paint using gradation.
 * 
 * @author minemoto
 *
 */
public class SGGradationPaint extends SGTransparentPaint
implements SGIDisposable {
    
    public static final String NAME_UNKNOWN = "UNKNOWN";
    
    public static final int INDEX_DIRECTION_HORIZONTAL = 0;
    
    public static final int INDEX_DIRECTION_VERTICAL = 1;
    
    public static final int INDEX_DIRECTION_DIAGONAL_UP_RIGHT = 2;
    
    public static final int INDEX_DIRECTION_DIAGONAL_LOW_RIGHT = 3;
    
    public static final String NAME_DIRECTION_HORIZONTAL = "Horizontal";
    
    public static final String NAME_DIRECTION_VERTICAL = "Vertical";
    
    public static final String NAME_DIRECTION_DIAGONAL_UP_RIGHT = "Diagonal upper right";
    
    public static final String NAME_DIRECTION_DIAGONAL_LOW_RIGHT = "Diagonal lower right";
    
    public static final String[] DIRECTION_NAMES = {
        NAME_DIRECTION_HORIZONTAL,
        NAME_DIRECTION_VERTICAL,
        NAME_DIRECTION_DIAGONAL_UP_RIGHT,
        NAME_DIRECTION_DIAGONAL_LOW_RIGHT
    };
    
    public static final int[] DIRECTION_INDEX = {
        INDEX_DIRECTION_HORIZONTAL,
        INDEX_DIRECTION_VERTICAL,
        INDEX_DIRECTION_DIAGONAL_UP_RIGHT,
        INDEX_DIRECTION_DIAGONAL_LOW_RIGHT
    };
    
    public static final int INDEX_ORDER_COLOR_1_2 = 0;
    
    public static final int INDEX_ORDER_COLOR_2_1 = 1;
    
    public static final int INDEX_ORDER_COLOR_1_2_1 = 2;
    
    public static final int INDEX_ORDER_COLOR_2_1_2 = 3;
    
    public static final String NAME_ORDER_COLOR_1_2 = "Color 1_2";
    
    public static final String NAME_ORDER_COLOR_2_1 = "Color 2_1";
    
    public static final String NAME_ORDER_COLOR_1_2_1 = "Color 1_2_1";
    
    public static final String NAME_ORDER_COLOR_2_1_2 = "Color 2_1_2";
    
    public static final String[] ORDER_NAMES = {
        NAME_ORDER_COLOR_1_2,
        NAME_ORDER_COLOR_2_1,
        NAME_ORDER_COLOR_1_2_1,
        NAME_ORDER_COLOR_2_1_2
    };
    
    public static final int[] ORDER_INDEX = {
        INDEX_ORDER_COLOR_1_2,
        INDEX_ORDER_COLOR_2_1,
        INDEX_ORDER_COLOR_1_2_1,
        INDEX_ORDER_COLOR_2_1_2
    };
    
    protected int mDirectionIndex;
    
    protected int mOrderIndex;
    
    protected Color[] mColors = null;
    
    public SGGradationPaint() {
        super();
        this.init();
    }
    
    private void init() {
        this.mDirectionIndex = 0;
        this.mOrderIndex = 0;
        this.mColors = new Color[] { Color.BLACK, Color.WHITE };
    }

    @Override
    public Paint getPaint(final Rectangle2D rect) {
        return getLinearGradientPaint(rect,
                getTransparentedColor(this.mColors[0]),
                getTransparentedColor(this.mColors[1]),
                this.mDirectionIndex,
                this.mOrderIndex);
    }
    
    public Paint getOpaquePaint(final Rectangle2D rect) {
        return getLinearGradientPaint(rect,
                getOpaqueColor(this.mColors[0]),
                getOpaqueColor(this.mColors[1]),
                this.mDirectionIndex,
                this.mOrderIndex);
    }

    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;
        this.mColors = null;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        SGGradationPaint model = (SGGradationPaint) super.clone();
        model.mDirectionIndex = this.mDirectionIndex;
        model.mOrderIndex = this.mOrderIndex;
        model.mColors = this.mColors.clone();
        return model;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SGGradationPaint)) return false;
        SGGradationPaint o = (SGGradationPaint)obj;
        if (! super.equals(o)) return false;
        if (o.mDirectionIndex != this.mDirectionIndex) return false;
        if (o.mOrderIndex != this.mOrderIndex) return false;
        if (o.mColors.length != this.mColors.length) return false;
        for (int i = 0; i < this.mColors.length; i++) {
            if (! o.mColors[i].equals(this.mColors[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 37*result+this.mDirectionIndex;
        result = 37*result+this.mOrderIndex;
        for (int i = 0; i < this.mColors.length; i++) {
            result = 37*result+this.mColors[i].hashCode();
        }
        return result;
    }
    
    public int getDirectionIndex() {
        return this.mDirectionIndex;
    }
    
    public int getOrderIndex() {
        return this.mOrderIndex;
    }
    
    public Color[] getColors() {
        return (Color[]) this.mColors.clone();
    }
    
    public boolean setDirection(final int direction) {
        if (! isValidDirection(direction)) {
            return false;
        }
        this.mDirectionIndex = direction;
        return true;
    }
    
    public boolean setOrder(final int order) {
        if (! isValidOrder(order)) {
            return false;
        }
        this.mOrderIndex = order;
        return true;
    }
    
    public boolean setColors(final Color[] colors) {
        if (null==colors) {
            return false;
        }
        this.mColors = colors.clone();
        return true;
    }
    
    public boolean setColor1(final Color color) {
        if (null==color) {
            return false;
        }
        if (null==this.mColors || this.mColors.length!=2) {
            return false;
        }
        this.mColors[0] = color;
        return true;
    }
    
    public boolean setColor2(final Color color) {
        if (null==color) {
            return false;
        }
        if (null==this.mColors || this.mColors.length!=2) {
            return false;
        }
        this.mColors[1] = color;
        return true;
    }
    
    public static boolean isValidDirection(final int direction) {
        for (int i = 0; i < DIRECTION_INDEX.length; i++) {
            if (direction==DIRECTION_INDEX[i]) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isValidOrder(final int order) {
        for (int i = 0; i < ORDER_INDEX.length; i++) {
            if (order==ORDER_INDEX[i]) {
                return true;
            }
        }
        return false;
    }
    
    public static String getDirectionName(final int direction) {
        if (! isValidDirection(direction)) {
            return NAME_UNKNOWN;
        }
        return DIRECTION_NAMES[direction];
    }
    
    public static String getOrderName(final int order) {
        if (! isValidOrder(order)) {
            return NAME_UNKNOWN;
        }
        return ORDER_NAMES[order];
    }
    
    public static Integer getDirectionIndex(final String name) {
        for (int i = 0; i < DIRECTION_NAMES.length; i++) {
            if (SGUtilityText.isEqualString(name, DIRECTION_NAMES[i])) {
                return Integer.valueOf(DIRECTION_INDEX[i]);
                
            }
        }
        return null;
    }
    
    public static Integer getOrderIndex(final String name) {
        for (int i = 0; i < ORDER_NAMES.length; i++) {
            if (SGUtilityText.isEqualString(name, ORDER_NAMES[i])) {
                return Integer.valueOf(ORDER_INDEX[i]);
            }
        }
        return null;
    }
    
    /**
     * Returns a linear gradient paint.
     * 
     * @param rect
     * @param color1
     * @param color2
     * @param directionIndex
     * @param orderIndex
     * @return
     */
    private static Paint getLinearGradientPaint(final Rectangle2D rect,
            final Color color1, final Color color2, final int directionIndex, final int orderIndex) {
        
        float[] dist;
        Color[] colors;
        switch (orderIndex) {
        case INDEX_ORDER_COLOR_1_2 :
            dist = new float[] { 0.0f, 1.0f };
            colors = new Color[] { color1, color2 };
            break;
        case INDEX_ORDER_COLOR_2_1 :
            dist = new float[] { 0.0f, 1.0f };
            colors = new Color[] { color2, color1 };
            break;
        case INDEX_ORDER_COLOR_1_2_1 :
            dist = new float[] { 0.0f, 0.5f, 1.0f };
            colors = new Color[] { color1, color2, color1 };
            break;
        case INDEX_ORDER_COLOR_2_1_2 :
            dist = new float[] { 0.0f, 0.5f, 1.0f };
            colors = new Color[] { color2, color1, color2 };
            break;
        default :
            throw new IllegalArgumentException("Illegal orderIndex = "+orderIndex);
        }
        
        Point2D start, end;
        switch (directionIndex) {
        case INDEX_DIRECTION_HORIZONTAL :
            start = new Point2D.Double(rect.getX(), rect.getY());
            end = new Point2D.Double(rect.getMaxX(), rect.getY());
            break;
        case INDEX_DIRECTION_VERTICAL :
            start = new Point2D.Double(rect.getX(), rect.getY());
            end = new Point2D.Double(rect.getX(), rect.getMaxY());
            break;
        case INDEX_DIRECTION_DIAGONAL_UP_RIGHT :
            start = new Point2D.Double(rect.getX(), rect.getMaxY());
            end = new Point2D.Double(rect.getMaxX(), rect.getY());
            break;
        case INDEX_DIRECTION_DIAGONAL_LOW_RIGHT :
            start = new Point2D.Double(rect.getX(), rect.getY());
            end = new Point2D.Double(rect.getMaxX(), rect.getMaxY());
            break;
        default :
            throw new IllegalArgumentException("Illegal directionIndex = "+directionIndex);
        }
        
        if (! start.equals(end)) {
            Paint paint = new LinearGradientPaint(start, end, dist, colors);
            return paint;
        } else {
            return null;
        }
    }
    
    // The flag whether this object is already disposed of.
    private boolean mDisposed = false;

    /**
     * Returns whether this object is already disposed of.
     * 
     * @return true if this object is already disposed of
     */
    public boolean isDisposed() {
    	return this.mDisposed;
    }

    @Override
	public SGPropertyMap getPropertyFileMap() {
    	SGPropertyMap map = super.getPropertyFileMap();
        Color[] colors = this.getColors();
    	SGPropertyUtility.addProperty(map, KEY_GRADATION_COLOR1, 
    			SGUtilityText.getColorString(colors[0]));
    	SGPropertyUtility.addProperty(map, KEY_GRADATION_COLOR2, 
    			SGUtilityText.getColorString(colors[1]));
    	SGPropertyUtility.addProperty(map, KEY_GRADATION_DIRECTION, 
    			SGGradationPaint.getDirectionName(this.getDirectionIndex()));
    	SGPropertyUtility.addProperty(map, KEY_GRADATION_ORDER, 
    			SGGradationPaint.getOrderName(this.getOrderIndex()));
    	return map;
	}

}

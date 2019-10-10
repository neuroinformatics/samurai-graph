package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Paint using pattern texture.
 * 
 * @author minemoto
 *
 */
public class SGPatternPaint extends SGTransparentPaint {
    
    public static final int INDEX_0_DEGREE = 0;

    public static final int INDEX_45_DEGREE = 1;

    public static final int INDEX_90_DEGREE = 2;

    public static final int INDEX_135_DEGREE = 3;
    
    public static final int INDEX_STRIPES_0_DEGREE = 4;
    
    public static final int INDEX_STRIPES_45_DEGREE = 5;
    
    public static final int INDEX_3_STRIPES_90_DEGREE = 6;
    
    public static final int INDEX_45_DEGREE_WIDE = 7;
    
    public static final String NAME_0_DEGREE = "0 degree";

    public static final String NAME_45_DEGREE = "45 degree";

    public static final String NAME_90_DEGREE = "90 degree";

    public static final String NAME_135_DEGREE = "135 degree";

    public static final String NAME_STRIPES_0_DEGREE = "Stripes 0 degree";

    public static final String NAME_STRIPES_45_DEGREE = "Stripes 45 degree";
    
    public static final String NAME_3_STRIPES_90_DEGREE = "3 stripes 90 degree";
    
    public static final String NAME_45_DEGREE_WIDE = "45 degree wide";
    
    public static final String[] TYPE_PATTERN_NAMES = {
        NAME_0_DEGREE,
        NAME_45_DEGREE,
        NAME_90_DEGREE,
        NAME_135_DEGREE,
        NAME_STRIPES_0_DEGREE,
        NAME_STRIPES_45_DEGREE,
        NAME_3_STRIPES_90_DEGREE,
        NAME_45_DEGREE_WIDE
    };
    public static final int[] INDEX_PATTERN_TYPES = {
        INDEX_0_DEGREE,
        INDEX_45_DEGREE,
        INDEX_90_DEGREE,
        INDEX_135_DEGREE,
        INDEX_STRIPES_0_DEGREE,
        INDEX_STRIPES_45_DEGREE,
        INDEX_3_STRIPES_90_DEGREE,
        INDEX_45_DEGREE_WIDE
    };
    
    public static final int INDEX_UNKNOWN = -1;
    
    public static final String NAME_UNKNOWN = "UNKNOWN";
    
    protected Color mColor;
    
    protected int mTypeIndex;
    
    public SGPatternPaint() {
        this.mColor = Color.BLACK;
        this.mTypeIndex = INDEX_0_DEGREE;
    }
    
    public Color getColor() {
        return this.mColor;
    }
    
    public int getTypeIndex() {
        return this.mTypeIndex;
    }
    
    public boolean setColor(final Color color) {
        if (null==color) {
            return false;
        }
        this.mColor = color;
        return true;
    }

    public boolean setTypeIndex(final int typeIndex) {
        if (!isValidType(typeIndex)) {
            return false;
        }
        this.mTypeIndex = typeIndex;
        return true;
    }
    
    @Override
    public Paint getPaint(final Rectangle2D rect) {
        switch (this.mTypeIndex) {
        case INDEX_0_DEGREE :
            return getPatternPaint_Size3x3(
                    getTransparentedColor(this.mColor),
                    getTransparentedColor(Color.WHITE),
                    0,
                    this.mMagnification);
        case INDEX_45_DEGREE :
            return getPatternPaint_Size4x4(
                    getTransparentedColor(this.mColor),
                    getTransparentedColor(Color.WHITE),
                    1,
                    this.mMagnification);
        case INDEX_90_DEGREE :
            return getPatternPaint_Size3x3(
                    getTransparentedColor(this.mColor),
                    getTransparentedColor(Color.WHITE),
                    3,
                    this.mMagnification);
        case INDEX_135_DEGREE :
            return getPatternPaint_Size4x4(
                    getTransparentedColor(this.mColor),
                    getTransparentedColor(Color.WHITE),
                    2,
                    this.mMagnification);
        case INDEX_STRIPES_0_DEGREE :
            return getPatternPaint_Stripe_Size4x4(
                    getTransparentedColor(this.mColor),
                    getTransparentedColor(Color.WHITE),
                    0,
                    this.mMagnification);
        case INDEX_STRIPES_45_DEGREE :
            return getPatternPaint_Stripe_Size4x4(
                    getTransparentedColor(this.mColor),
                    getTransparentedColor(Color.WHITE),
                    2,
                    this.mMagnification);
        case INDEX_3_STRIPES_90_DEGREE :
            return getPatternPaint_3_stripes_Size5x5(
                    getTransparentedColor(this.mColor),
                    getTransparentedColor(Color.WHITE),
                    this.mMagnification);
        case INDEX_45_DEGREE_WIDE :
            return getPatternPaint_Size10x10(
                    getTransparentedColor(this.mColor),
                    getTransparentedColor(Color.WHITE),
                    1,
                    this.mMagnification);
        default :
            return null;
        }
    }
    
    public Paint getOpaquePaint(final Rectangle2D rect) {
        return getOpaquePaint(rect, this.mMagnification);
    }
    
    public Paint getOpaquePaint(final Rectangle2D rect, final float magnification) {
        switch (this.mTypeIndex) {
        case INDEX_0_DEGREE :
            return getPatternPaint_Size3x3(
                    this.mColor,
                    Color.WHITE,
                    0,
                    magnification);
        case INDEX_45_DEGREE :
            return getPatternPaint_Size4x4(
                    this.mColor,
                    Color.WHITE,
                    1,
                    magnification);
        case INDEX_90_DEGREE :
            return getPatternPaint_Size3x3(
                    this.mColor,
                    Color.WHITE,
                    3,
                    magnification);
        case INDEX_135_DEGREE :
            return getPatternPaint_Size4x4(
                    this.mColor,
                    Color.WHITE,
                    2,
                    magnification);
        case INDEX_STRIPES_0_DEGREE :
            return getPatternPaint_Stripe_Size4x4(
                    this.mColor,
                    Color.WHITE,
                    0,
                    this.mMagnification);
        case INDEX_STRIPES_45_DEGREE :
            return getPatternPaint_Stripe_Size4x4(
                    this.mColor,
                    Color.WHITE,
                    2,
                    this.mMagnification);
        case INDEX_3_STRIPES_90_DEGREE :
            return getPatternPaint_3_stripes_Size5x5(
                    this.mColor,
                    Color.WHITE,
                    this.mMagnification);
        case INDEX_45_DEGREE_WIDE :
            return getPatternPaint_Size10x10(
                    this.mColor,
                    Color.WHITE,
                    1,
                    this.mMagnification);
        default :
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SGPatternPaint)) return false;
        SGPatternPaint o = (SGPatternPaint)obj;
        if (! super.equals(o)) return false;
        if (! o.mColor.equals(this.mColor)) return false;
        if (o.mTypeIndex!=this.mTypeIndex) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 37*result+this.mColor.hashCode();
        result = 37*result+this.mTypeIndex;
        return result;
    }
    
    private static Paint getPatternPaint_Size3x3(
            Color foregroundColor, Color backgroundColor, int type, float magnification) {
        BufferedImage bi = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, 3, 3);
        g.setColor(foregroundColor);
        switch (type) {
        case 0 :    // horizontal
            g.fillRect(0, 1, 3, 1);
            break;
        case 1 :    // diagonal(upper-right)
            g.fillRect(2, 0, 1, 1);
            g.fillRect(1, 1, 1, 1);
            g.fillRect(0, 2, 1, 1);
            break;
        case 2 :    // diagonal(lower-right)
            g.fillRect(0, 0, 1, 1);
            g.fillRect(1, 1, 1, 1);
            g.fillRect(2, 2, 1, 1);
            break;
        case 3 :    // vertical
            g.fillRect(1, 0, 1, 3);
            break;
        default :
            break;
        }
        if (magnification == 1.0f) {
            Rectangle r = new Rectangle(0,0,3,3);
            return new TexturePaint(bi, r);
        } else {
            int size = (int)Math.ceil(3.0*magnification);
            BufferedImage bi2 = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bi2.createGraphics();
            g2.drawImage(bi.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, null);
            Rectangle r = new Rectangle(0,0,size,size);
            return new TexturePaint(bi2, r);
        }
    }
    
    
    private static Paint getPatternPaint_Size4x4(
            Color foregroundColor, Color backgroundColor, int type, float magnification) {
        BufferedImage bi = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, 4, 4);
        g.setColor(foregroundColor);
        switch (type) {
        case 0 :    // horizontal
            g.fillRect(0, 1, 4, 1);
            break;
        case 1 :    // diagonal(upper-right)
            g.fillRect(3, 0, 1, 1);
            g.fillRect(2, 1, 1, 1);
            g.fillRect(1, 2, 1, 1);
            g.fillRect(0, 3, 1, 1);
            break;
        case 2 :    // diagonal(lower-right)
            g.fillRect(0, 0, 1, 1);
            g.fillRect(1, 1, 1, 1);
            g.fillRect(2, 2, 1, 1);
            g.fillRect(3, 3, 1, 1);
            break;
        case 3 :    // vertical
            g.fillRect(1, 0, 1, 4);
            break;
        default :
            break;
        }
        if (magnification == 1.0f) {
            Rectangle r = new Rectangle(0,0,4,4);
            return new TexturePaint(bi, r);
        } else {
            int size = (int)Math.ceil(4.0*magnification);
            BufferedImage bi2 = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bi2.createGraphics();
            g2.drawImage(bi.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, null);
            Rectangle r = new Rectangle(0,0,size,size);
            return new TexturePaint(bi2, r);
        }
    }
    
    private static Paint getPatternPaint_Stripe_Size4x4(
            Color foregroundColor, Color backgroundColor, int type, float magnification) {
        BufferedImage bi = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, 4, 4);
        g.setColor(foregroundColor);
        switch (type) {
        case 0 :    // little
            g.fillRect(0, 0, 4, 1);
            g.fillRect(0, 1, 1, 3);
            g.fillRect(0, 2, 4, 1);
            g.fillRect(2, 1, 1, 3);
            break;
        case 1 :
            g.fillRect(0, 0, 4, 1);
            g.fillRect(0, 1, 1, 3);
            break;
        case 2 :
            g.fillRect(0, 0, 1, 1);
            g.fillRect(1, 1, 1, 1);
            g.fillRect(2, 2, 1, 1);
            g.fillRect(3, 3, 1, 1);
            g.fillRect(1, 3, 1, 1);
            g.fillRect(3, 1, 1, 1);
            break;
        case 3 :
            g.drawLine(0, 0, 3, 0);
            g.drawLine(0, 0, 0, 3);
            g.drawLine(1, 3, 3, 1);
            break;
        default :
            break;
        }
        if (magnification == 1.0f) {
            Rectangle r = new Rectangle(0,0,4,4);
            return new TexturePaint(bi, r);
        } else {
            int size = (int)Math.ceil(4.0*magnification);
            BufferedImage bi2 = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bi2.createGraphics();
            g2.drawImage(bi.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, null);
            Rectangle r = new Rectangle(0,0,size,size);
            return new TexturePaint(bi2, r);
        }
    }
    
    private static Paint getPatternPaint_3_stripes_Size5x5(
            Color foregroundColor, Color backgroundColor, float magnification) {
        BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, 5, 5);
        g.setColor(foregroundColor);
        g.drawLine(0, 0, 4, 0);
        g.drawLine(0, 1, 0, 4);
        g.drawLine(2, 4, 4, 2);
        
        if (magnification == 1.0f) {
            return new TexturePaint(bi, new Rectangle(0,0,5,5));
        } else {
            int size = (int)Math.ceil(5.0*magnification);
            BufferedImage bi2 = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bi2.createGraphics();
            g2.drawImage(bi.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, null);
            Rectangle r = new Rectangle(0,0,size,size);
            return new TexturePaint(bi2, r);
        }
    }
    
    private static Paint getPatternPaint_Size10x10(
            Color foregroundColor, Color backgroundColor, int type, float magnification) {
        BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, 10, 10);
        g.setColor(foregroundColor);
        switch (type) {
        case 0 :    // horizontal
            g.fillRect(0, 0, 10, 1);
            break;
        case 1 :    // diagonal(upper-right)
            g.fillRect(9, 0, 1, 1);
            g.fillRect(8, 1, 1, 1);
            g.fillRect(7, 2, 1, 1);
            g.fillRect(6, 3, 1, 1);
            g.fillRect(5, 4, 1, 1);
            g.fillRect(4, 5, 1, 1);
            g.fillRect(3, 6, 1, 1);
            g.fillRect(2, 7, 1, 1);
            g.fillRect(1, 8, 1, 1);
            g.fillRect(0, 9, 1, 1);
            break;
        case 2 :    // diagonal(lower-right)
            g.fillRect(0, 0, 1, 1);
            g.fillRect(1, 1, 1, 1);
            g.fillRect(2, 2, 1, 1);
            g.fillRect(3, 3, 1, 1);
            g.fillRect(4, 4, 1, 1);
            g.fillRect(5, 5, 1, 1);
            g.fillRect(6, 6, 1, 1);
            g.fillRect(7, 7, 1, 1);
            g.fillRect(8, 8, 1, 1);
            g.fillRect(9, 9, 1, 1);
            break;
        case 3 :    // vertical
            g.fillRect(0, 0, 1, 10);
            break;
        }
        
        if (magnification == 1.0f) {
            return new TexturePaint(bi, new Rectangle(0,0,10,10));
        } else {
            int size = (int)Math.ceil(10.0*magnification);
            BufferedImage bi2 = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bi2.createGraphics();
            g2.drawImage(bi.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, null);
            Rectangle r = new Rectangle(0,0,size,size);
            return new TexturePaint(bi2, r);
        }
    }
    
    /**
     * Returns whether a given pattern type is valid.
     * 
     * @param type
     *           a pattern type
     * @return true if the given pattern type is valid
     */
    public static boolean isValidType(final int type) {
        final int[] array = INDEX_PATTERN_TYPES;
        for (int ii = 0; ii < array.length; ii++) {
            if (type == array[ii]) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the pattern type constant from a given name.
     * 
     * @param name
     *           the name of pattern type
     * @return the pattern type constant if it exists or null otherwise
     */
    public static Integer getTypeFromName(final String name) {
        if (name == null) {
            return null;
        }
        int type = INDEX_UNKNOWN;
        for (int i = 0; i < TYPE_PATTERN_NAMES.length; i++) {
            if (SGUtilityText.isEqualString(TYPE_PATTERN_NAMES[i], name)) {
                type = INDEX_PATTERN_TYPES[i];
                break;
            }
        }
        if (type!=INDEX_UNKNOWN) {
            return Integer.valueOf(type);
        } else {
            return null;
        }
    }
    
    /**
     * Returns the name of a given pattern type.
     * 
     * @param type
     *          the pattern type
     * @return the name of a given pattern type
     */
    public static String getTypeName(final int type) {
        if (isValidType(type)) {
            return TYPE_PATTERN_NAMES[type];
        }
        return NAME_UNKNOWN;
    }
    
    @Override
	public SGPropertyMap getPropertyFileMap() {
    	SGPropertyMap map = super.getPropertyFileMap();
    	SGPropertyUtility.addProperty(map, KEY_PATTERN_COLOR, this.getColor());
    	SGPropertyUtility.addProperty(map, KEY_PATTERN_TYPE, SGPatternPaint.getTypeName(this.getTypeIndex()));
    	return map;
	}

}

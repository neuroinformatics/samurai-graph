package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Color;
import java.awt.Font;

import jp.riken.brain.ni.samuraigraph.base.SGIDateConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;

/**
 * Constants for string objects.
 */
public interface SGIStringConstants extends SGIDrawingElementConstants, SGIDateConstants {

    //
    // Angle
    //
    
    public static final double STRING_ANGLE_MIN = -180.0;

    public static final double STRING_ANGLE_MAX = 180.0;

    public static final double STRING_ANGLE_STEP = 1.0;

    public static final int STRING_ANGLE_FRAC_DIFIT_MIN = 1;
    
    public static final int STRING_ANGLE_FRAC_DIFIT_MAX = 2;
    
    public static final int STRING_ANGLE_MINIMAL_ORDER = - STRING_ANGLE_FRAC_DIFIT_MAX;
    
    
    //
    // Keys
    //
    
    public static final String TAG_NAME_LABEL = "Label";
   
    // location
    public static final String KEY_X = "X";

    public static final String KEY_Y = "Y";

    public static final String KEY_ANGLE = "Angle";

    // font
    public static final String KEY_TEXT = "Text";

    public static final String KEY_FONT_SIZE = "FontSize";

    public static final String KEY_FONT_NAME = "FontName";

    public static final String KEY_FONT_STYLE = "FontStyle";

    public static final String KEY_STRING_COLORS = "StringColor";

    public static final String KEY_X_VALUE = "XValue";

    public static final String KEY_Y_VALUE = "YValue";
    
    // format
    public static final String KEY_DECIMAL_PLACES = "DecimalPlaces";
    
    public static final String KEY_EXPONENT = "Exponent";
    
    public static final String KEY_DATE_FORMAT = "DateFormat";
    
    //
    // Default values for SGDrawingElementString
    //
    
    public static final String DEFAULT_STRING_FONT_NAME = "Serif";

    public static final int DEFAULT_STRING_FONT_STYLE = Font.PLAIN;

    public static final float DEFAULT_STRING_FONT_SIZE = 16.0f;

    public static final Color DEFAULT_STRING_FONT_COLOR = Color.BLACK;

    public static final float DEFAULT_STRING_ANGLE = 0.0f;

    
    //
    // Default values for labels used in SGIFigureElementString
    //
    
    public static final String DEFAULT_LABEL_FONT_NAME = DEFAULT_STRING_FONT_NAME;

    public static final int DEFAULT_LABEL_FONT_STYLE = DEFAULT_STRING_FONT_STYLE;

    public static final float DEFAULT_LABEL_FONT_SIZE = DEFAULT_STRING_FONT_SIZE;

    public static final Color DEFAULT_LABEL_FONT_COLOR = DEFAULT_STRING_FONT_COLOR;

    public static final float DEFAULT_LABEL_ANGLE = DEFAULT_STRING_ANGLE;

    // Horizontal Axis
    public static final String DEFAULT_LABEL_HORIZONTAL_AXIS = SGIFigureElementAxis.AXIS_BOTTOM;

    // Vertical Axis
    public static final String DEFAULT_LABEL_VERTICAL_AXIS = SGIFigureElementAxis.AXIS_LEFT;

	public static final String COM_LABEL = "Label";
    
    public static final String COM_LABEL_AXIS_X = "AxisX";

    public static final String COM_LABEL_AXIS_Y = "AxisY";

    public static final String COM_LABEL_TEXT = "Text";

    public static final String COM_LABEL_LOCATION_X = "X";

    public static final String COM_LABEL_LOCATION_Y = "Y";

    public static final String COM_LABEL_FONT_NAME = "FontName";

    public static final String COM_LABEL_FONT_STYLE = "FontStyle";

    public static final String COM_LABEL_FONT_SIZE = "FontSize";

    public static final String COM_LABEL_FONT_COLOR = "Color";

    public static final String COM_LABEL_ANGLE = "Angle";

}

package jp.riken.brain.ni.samuraigraph.data;

public interface SGIDataCommandConstants {

	/**
	 * The symbol for the file path in command script in a global attribute of a NetCDF file.
	 * When the file path is given by this symbol, the NetCDF file is used as the data file.
	 */
	public static final String FILE_PATH_NETCDF_ITSELF = ".";

	public static final String COM_DATA = "Data";

    public static final String COM_DATA_AXIS_X = "AxisX";

    public static final String COM_DATA_AXIS_Y = "AxisY";

    public static final String COM_DATA_VISIBLE_IN_LEGEND = "VisibleInLegend";

    public static final String COM_DATA_ANIMATION_ARRAY_SECTION = "AnimationArraySection";

    public static final String COM_DATA_ANIMATION_FRAME_RATE = "AnimationFrameRate";
    
    public static final String COM_DATA_ANIMATION_LOOP_PLAYBACK = "AnimationLoopPlayback";
    
    public static final String COM_DATA_NAME = "Name";

    public static final String COM_DATA_COLUMN_TYPE = "ColumnType";

	// only for backward compatibility
    public static final String COM_DATA_COORDINATE_VARIABLES_INDEX = "CoordinateVariablesIndex";

    public static final String COM_DATA_ORIGIN = "Origin";

    public static final String COM_DATA_SHIFT_X = "ShiftX";

    public static final String COM_DATA_SHIFT_Y = "ShiftY";

    public static final String COM_DATA_ANIMATION_FRAME_DIMENSION = "AnimationFrameDimension";

    public static final String COM_DATA_PICKUP_DIMENSION = "PickUpDimension";

	// only for backward compatibility
    public static final String COM_DATA_PICKUP_START = "PickupStart";

	// only for backward compatibility
    public static final String COM_DATA_PICKUP_END = "PickupEnd";

	// only for backward compatibility
    public static final String COM_DATA_PICKUP_STEP = "PickupStep";

    public static final String COM_DATA_PICKUP_INDICES = "PickUpIndices";

    // stride
    public static final String COM_DATA_ARRAY_SECTION_AVAILABLE = "ArraySectionAvailable";

    public static final String COM_DATA_ARRAY_SECTION = "ArraySection";

    public static final String COM_DATA_X_ARRAY_SECTION = "XArraySection";

    public static final String COM_DATA_Y_ARRAY_SECTION = "YArraySection";

    public static final String COM_DATA_TICK_LABEL_ARRAY_SECTION = "TickLabelArraySection";

    public static final String COM_DATA_INDEX_ARRAY_SECTION = "IndexArraySection";

    // line
    public static final String COM_DATA_LINE_VISIBLE = "LineVisible";

    public static final String COM_DATA_LINE_WIDTH = "LineWidth";

    public static final String COM_DATA_LINE_TYPE = "LineType";

    public static final String COM_DATA_LINE_COLOR = "LineColor";
    
    public static final String COM_DATA_COLOR_STYLE_REVERSED = "LineColorStyleReversed";
    
    public static final String COM_DATA_LINE_CONNECT_ALL = "LineConnectAll";

	// only for backward compatibility
    public static final String COM_DATA_LINE_SHIFT_X = "LineShiftX";

	// only for backward compatibility
    public static final String COM_DATA_LINE_SHIFT_Y = "LineShiftY";

    // symbol
    public static final String COM_DATA_SYMBOL_VISIBLE = "SymbolVisible";

    public static final String COM_DATA_SYMBOL_TYPE = "SymbolType";

    public static final String COM_DATA_SYMBOL_SIZE = "SymbolSize";

    public static final String COM_DATA_SYMBOL_BODY_COLOR = "SymbolBodyColor";

    public static final String COM_DATA_SYMBOL_BODY_TRANSPARENCY = "SymbolBodyTransparency";

    public static final String COM_DATA_SYMBOL_LINE_WIDTH = "SymbolLineWidth";

    public static final String COM_DATA_SYMBOL_LINE_COLOR = "SymbolLineColor";

    public static final String COM_DATA_SYMBOL_LINE_VISIBLE = "SymbolLineVisible";

    // bar
    public static final String COM_DATA_BAR_VISIBLE = "BarVisible";

    public static final String COM_DATA_BAR_VERTICAL = "BarVertical";

    public static final String COM_DATA_BAR_BASELINE_VALUE = "BarBaseline";

    public static final String COM_DATA_BAR_BODY_WIDTH_VALUE = "BarBodyWidth";

    public static final String COM_DATA_BAR_BODY_PAINT_STYLE = "BarBodyPaintStyle";

    public static final String COM_DATA_BAR_BODY_FILL_COLOR = "BarBodyFillColor";

    public static final String COM_DATA_BAR_BODY_PATTERN_COLOR = "BarBodyPatternColor";

    public static final String COM_DATA_BAR_BODY_PATTERN_TYPE = "BarBodyPatternType";

    public static final String COM_DATA_BAR_BODY_GRADATION_COLOR1 = "BarBodyGradationColor1";

    public static final String COM_DATA_BAR_BODY_GRADATION_COLOR2 = "BarBodyGradationColor2";

    public static final String COM_DATA_BAR_BODY_GRADATION_DIRECTION = "BarBodyGradationDirection";

    public static final String COM_DATA_BAR_BODY_GRADATION_ORDER = "BarBodyGradationOrder";

    public static final String COM_DATA_BAR_BODY_TRANSPARENCY = "BarBodyTransparency";

    public static final String COM_DATA_BAR_LINE_WIDTH = "BarLineWidth";

    public static final String COM_DATA_BAR_LINE_COLOR = "BarLineColor";

    public static final String COM_DATA_BAR_LINE_VISIBLE = "BarLineVisible";

    public static final String COM_DATA_BAR_INTERVAL = "BarInterval";

    public static final String COM_DATA_BAR_OFFSET_X = "BarOffsetX";

    public static final String COM_DATA_BAR_OFFSET_Y = "BarOffsetY";

	// only for backward compatibility
    public static final String COM_DATA_BAR_SHIFT_X = "BarShiftX";

	// only for backward compatibility
    public static final String COM_DATA_BAR_SHIFT_Y = "BarShiftY";

    // error bar
    public static final String COM_DATA_ERROR_BAR_VISIBLE = "ErrorBarVisible";

    public static final String COM_DATA_ERROR_BAR_SYMBOL_TYPE = "ErrorBarSymbolType";

    public static final String COM_DATA_ERROR_BAR_SYMBOL_SIZE = "ErrorBarSymbolSize";

    public static final String COM_DATA_ERROR_BAR_COLOR = "ErrorBarColor";

    public static final String COM_DATA_ERROR_BAR_LINE_WIDTH = "ErrorBarLineWidth";

    public static final String COM_DATA_ERROR_BAR_STYLE = "ErrorBarStyle";

    public static final String COM_DATA_ERROR_BAR_POSITION = "ErrorBarPosition";

    // tick label
    public static final String COM_DATA_TICK_LABEL_VISIBLE = "TickLabelVisible";

    public static final String COM_DATA_TICK_LABEL_FONT_NAME = "TickLabelFontName";

    public static final String COM_DATA_TICK_LABEL_FONT_STYLE = "TickLabelFontStyle";

    public static final String COM_DATA_TICK_LABEL_FONT_SIZE = "TickLabelFontSize";

    public static final String COM_DATA_TICK_LABEL_FONT_COLOR = "TickLabelFontColor";

    public static final String COM_DATA_TICK_LABEL_ANGLE = "TickLabelAngle";

    public static final String COM_DATA_TICK_LABEL_DECIMAL_PLACES = "TickLabelDecimalPlaces";

    public static final String COM_DATA_TICK_LABEL_EXPONENT = "TickLabelExponent";

    public static final String COM_DATA_TICK_LABEL_DATE_FORMAT = "TickLabelDateFormat";

    // vector
    public static final String COM_DATA_ARROW_MAGNITUDE_PER_CM = "ArrowMagnitudePerCM";

    public static final String COM_DATA_ARROW_DIRECTION_INVARIANT = "ArrowDirectionInvariant";

    public static final String COM_DATA_ARROW_LINE_WIDTH = "ArrowLineWidth";

    public static final String COM_DATA_ARROW_LINE_TYPE = "ArrowLineType";

    public static final String COM_DATA_ARROW_HEAD_SIZE = "ArrowHeadSize";

    public static final String COM_DATA_ARROW_COLOR = "ArrowColor";

    public static final String COM_DATA_ARROW_START_TYPE = "ArrowStartType";

    public static final String COM_DATA_ARROW_END_TYPE = "ArrowEndType";

    public static final String COM_DATA_ARROW_HEAD_OPEN_ANGLE = "ArrowHeadOpenAngle";

    public static final String COM_DATA_ARROW_HEAD_CLOSE_ANGLE = "ArrowHeadCloseAngle";

    public static final String COM_DATA_ARROW_HEAD_ANGLE = "ArrowHeadAngle";

    // 2D map
    public static final String COM_DATA_RECTANGLE_WIDTH = "RectangleWidth";

    public static final String COM_DATA_RECTANGLE_HEIGHT = "RectangleHeight";

	public static final String COM_DATA_FILE_PATH = "FilePath";

	public static final String COM_DATA_TYPE = "DataType";

	public static final String COM_DATA_SAMPLING_RATE = "SamplingRate";

	public static final String COM_DATA_POLAR = "Polar";

}

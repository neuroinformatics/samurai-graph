package jp.riken.brain.ni.samuraigraph.base;

/**
 * An interface of the grid element.
 */
public interface SGIFigureElementGrid extends SGIFigureElement,
        SGIGridDialogObserver {

    /**
     * Set the SGIAxisElement object.
     * 
     * @param el -
     *            an SGIAxisElement object
     */
    public void setAxisElement(SGIFigureElementAxis el);

    /**
     * Returns whether the grid is visible.
     * 
     * @return visible flag
     * @uml.property name="gridVisible"
     */
    public boolean isGridVisible();

    /**
     * Set visible the grid.
     * 
     * @param b
     *            visible flag
     * @uml.property name="gridVisible"
     */
    public boolean setGridVisible(final boolean b);

    /**
     * @uml.property name="lineWidth"
     */
    public float getLineWidth();

    /**
     * @param width
     * @return
     * @uml.property name="lineWidth"
     */
    public boolean setLineWidth(final float width);

    /**
     * 
     */
    public static final String TAG_NAME_GRID_ELEMENT = "Grid";

    public static final String KEY_GRID_VISIBLE = "GridVisible";

    public static final String KEY_BASELINE_VALUE_X = "BaselineValueX";

    public static final String KEY_BASELINE_VALUE_Y = "BaselineValueY";

    public static final String KEY_STEP_VALUE_X = "StepValueX";

    public static final String KEY_STEP_VALUE_Y = "StepValueY";

    public static final String KEY_AUTO_CALC = "AutomaticCalculationOfTick";

    public static final String KEY_GRID_COLOR = "Color";

    /**
     * Creates and returns the map of properties for the property file.
     * 
     * @return the map of properties
     */
    public SGPropertyMap getPropertyFileMap(SGExportParameter params);

    /**
     * Creates and returns the map of properties.
     * 
     * @return the map of properties
     */
    public SGPropertyMap getCommandPropertyMap(SGExportParameter params);

    public SGProperties getProperties();
    
    public boolean setProperties(SGProperties p);
}

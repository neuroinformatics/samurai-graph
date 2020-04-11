package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;

/**
 * An observer of the property panel for arrows.
 */
public interface SGIArrowPanelObserver {

    public float getLineWidth(final String unit);

    /**
     * @return
     * @uml.property name="lineType"
     */
    public int getLineType();

    /**
     * @return
     * @uml.property name="color"
     */
    public Color getColor();

    public float getHeadSize(final String unit);

    /**
     * @return
     * @uml.property name="headOpenAngle"
     */
    public float getHeadOpenAngle();

    /**
     * @return
     * @uml.property name="headCloseAngle"
     */
    public float getHeadCloseAngle();

    /**
     * @return
     * @uml.property name="startHeadType"
     */
    public int getStartHeadType();

    /**
     * @return
     * @uml.property name="endHeadType"
     */
    public int getEndHeadType();

    public boolean setLineWidth(final float width, final String unit);

    /**
     * @param type
     * @return
     * @uml.property name="lineType"
     */
    public boolean setLineType(final int type);

    /**
     * @param cl
     * @return
     * @uml.property name="color"
     */
    public boolean setColor(final Color cl);

    public boolean setHeadSize(final float size, final String unit);

    /**
     * @param angle
     * @return
     * @uml.property name="headOpenAngle"
     */
    public boolean setHeadAngle(final Float openAngle, final Float closeAngle);

    /**
     * @param type
     * @return
     * @uml.property name="startHeadType"
     */
    public boolean setStartHeadType(final int type);

    /**
     * @param type
     * @return
     * @uml.property name="endHeadType"
     */
    public boolean setEndHeadType(final int type);

    /**
     * 
     * @param open
     * @param close
     * @return
     */
    public boolean hasValidAngle(final Number open, final Number close);

}

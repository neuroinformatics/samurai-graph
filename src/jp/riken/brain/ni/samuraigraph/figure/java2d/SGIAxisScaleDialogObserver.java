package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;

import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesHolder;
import jp.riken.brain.ni.samuraigraph.figure.SGIScaleConstants;

public interface SGIAxisScaleDialogObserver extends SGIPropertyDialogObserver,
		SGITwoAxesHolder, SGIScaleConstants {

    public boolean hasValidXValue(final int location, final Number value);

    public boolean hasValidYValue(final int location, final Number value);

	public boolean isAxisScaleVisible();
	
	public AXIS_LENGTH_MODE getAxisLengthMode();
	
	public double getXValue();
	
	public double getYValue();
	
	public boolean isXAxisVisible();
	
	public double getXLengthValue();
	
	public boolean isXAxisInverted();
	
	public boolean isXAxisTitleDownside();
	
	public String getXAxisText();
	
	public String getXAxisUnit();

	public boolean isYAxisVisible();
	
	public double getYLengthValue();
	
	public boolean isYAxisInverted();

	public boolean isYAxisTitleLeftside();

	public String getYAxisText();
	
	public String getYAxisUnit();
	
    public float getLineWidth(final String unit);

    public Color getLineColor();
    
    public float getSpace(final String unit);
    
    public String getFontName();
    
    public int getFontStyle();

    public float getFontSize(final String unit);
    
    public float getTextAngle();
    
    public Color getFontColor();

    
    public boolean setAxisScaleVisible(final boolean b);
    
    public boolean setAxisLengthMode(final AXIS_LENGTH_MODE mode);

    public boolean setXValue(final double value);
    
    public boolean setYValue(final double value);

    public boolean setXAxisVisible(final boolean visible);
    
    public boolean setXLengthValue(final double value);
    
	public boolean setXAxisTitleDownside(final boolean b);

    public boolean setXAxisText(final String text);
    
    public boolean setXAxisUnit(final String unit);
    
    public boolean setYAxisVisible(final boolean visible);
    
    public boolean setYLengthValue(final double value);

	public boolean setYAxisTitleLeftside(final boolean b);

    public boolean setYAxisText(final String text);
    
    public boolean setYAxisUnit(final String unit);
    
    public boolean setLineWidth(final float lw, final String unit);
    
    public boolean setLineColor(final Color cl);
    
    public boolean setSpace(final float space, final String unit);
    
    public boolean setFontName(final String name);
    
    public boolean setFontStyle(final int style);

    public boolean setFontSize(final float size, final String unit);

    public boolean setTextAngle(final float angle);
    
    public boolean setFontColor(final Color cl);

}

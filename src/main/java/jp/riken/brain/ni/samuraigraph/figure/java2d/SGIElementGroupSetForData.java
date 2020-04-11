package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.io.File;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDataObject;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementForData.DataLabel;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;

public interface SGIElementGroupSetForData extends SGIConstants, SGIChildObject,
        SGIDataCommandConstants, SGIDataObject {
    
    /**
     * Sets the name.
     * 
     * @param name
     *            a text string to set to the name
     * @return true if succeeded
     */
    public boolean setName(final String name);
    
    /**
     * Sets the X-axis.
     * 
     * @param axis
     *            an axis to set to the X-axis
     * @return true if succeeded
     */
    public boolean setXAxis(final SGAxis axis);
    
    /**
     * Sets the Y-axis.
     * 
     * @param axis
     *            an axis to set to the Y-axis
     * @return true if succeeded
     */
    public boolean setYAxis(final SGAxis axis);

    /**
     * Sets the X-axis.
     * 
     * @param location
     *            the location of the X-axis
     * @return true if succeeded
     */
    public boolean setXAxisLocation(final int location);

    /**
     * Sets the Y-axis.
     * 
     * @param location
     *            the location of the Y-axis
     * @return true if succeeded
     */
    public boolean setYAxisLocation(final int location);

    /**
     * Sets the magnification.
     * @param mag
     *           the magnification
     * @return true if succeeded
     */
    public boolean setMagnification(float mag);

    /**
     * Add an element group of a given type.
     * @param type
     *            the type of element group to add
     * @return true if succeeded
     */
    public boolean addDrawingElementGroup(final int type);

    /**
     * Initialize the properties history.
     * @return true if succeeded
     */
    public boolean initPropertiesHistory();
    
    public boolean setVisibleInLegend(boolean b);
    
    public void setFrameRate(final double rate);
    
    public void setLoopPlaybackAvailable(final boolean b);
    
    public SGProperties getProperties();
    
    public boolean setProperties(SGProperties p);
    
    public SGAxis getXAxis();
    
    public SGAxis getYAxis();
    
    public String getName();
    
    public Map<String, SGProperties> getElementGroupPropertiesMap();

    public boolean setElementGroupPropertiesMap(Map<String, SGProperties> pMap);

    /**
     * Returns a label for a data object.
     * 
     * @return a label for a data object 
     */
    public DataLabel getDataLabel();
    
    /**
     * Sets the data.
     * 
     * @param data
     *            a data object
     * @return true if succeeded
     */
    public boolean setData(SGData data);
    
    /**
     * Returns whether this group set "contains" the given group set.
     * 
     * @param gs
     *           the group set
     * @return true if this group set "contains" the given group set
     */
    public boolean contains(SGElementGroupSetForData gs);

    /**
     * Returns the data.
     * 
     * @return the data
     */
    public SGData getData();
    
	/**
	 * Save values to given file.
	 * 
	 * @param data
	 *            a data
	 * @param mode
	 *            the mode of saving data
	 * @return true if succeeded
	 */
	public boolean saveData(final File file, final SGExportParameter mode,
			SGDataBufferPolicy policy);

	/**
	 * Returns true if all stride of this data are available and each string representation 
	 * is different from "0:end".
	 * 
	 * @return true if all stride are effective
	 */
	public boolean hasEffectiveStride();
	
	/**
	 * Returns true if the data has edited values.
	 * 
	 * @return true if the data has edited values
	 */
	public boolean hasEditedDataValues();
	
}

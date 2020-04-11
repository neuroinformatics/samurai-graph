package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;

/**
 * A class of data column for netCDF data.
 *
 */
public class SGNetCDFDataColumnInfo extends SGDataColumnInfo {

	/**
	 * The name of variable.
	 */
    private String mName = null;

    /**
     * The origin of data column.
     */
    private int mOrigin = 0;

    /**
     * The flag whether the netCDF variable is of coordinate variable.
     */
    private boolean mCoorinateVariableFlag = false;
    
    /**
     * The list of information of dimensions.
     */
    private List<SGDimensionInfo> mDimensionList = new ArrayList<SGDimensionInfo>();

    /**
     * Builds a data column object with a given title and the value type.
     * 
     * @param var
     *           the netCDF variable
     * @param title
     *           the title
     * @param valueType
     *           the value type
     */
    public SGNetCDFDataColumnInfo(final SGNetCDFVariable var, final String title, final String valueType) {
        super(title, valueType);
        this.mName = var.getName();
        this.mCoorinateVariableFlag = var.isCoordinateVariable();
        this.mUnitsString = var.getUnitsString();

        List<Dimension> dimList = var.getDimensions();
        for (Dimension dim : dimList) {
        	SGDimensionInfo info = new SGDimensionInfo(dim);
        	this.mDimensionList.add(info);
        }
        
        List<Attribute> attrList = var.getAttributes();
        for (Attribute attr : attrList) {
        	final int len = attr.getLength();
        	Object[] values = new Object[len];
        	for (int ii = 0; ii < len; ii++) {
        		values[ii] = attr.getValue(ii);
        	}
        	SGAttribute a = new SGAttribute(attr.getName(), values);
        	this.mAttributeList.add(a);
        }
    }

    /**
     * Builds a data column object with a given title, value type and the origin.
     * 
     * @param var
     *            a variable
     * @param title
     *            the title
     * @param valueType
     *            the value type
     * @param origin
     *            the origin
     */
    public SGNetCDFDataColumnInfo(final SGNetCDFVariable var, final String title, final String valueType, 
    		final int origin) {
        this(var, title, valueType);
        this.mOrigin = origin;
    }
    
    /**
     * Builds a data column object with given information, title and value type.
     * 
     * @param info
     *           information for a netCDF variable
     * @param title
     *           the title
     * @param valueType
     *           the value type
     */
    public SGNetCDFDataColumnInfo(final SGNetCDFDataColumnInfo info, final String title, final String valueType) {
    	super(title, valueType);
    	this.mName = info.getName();
    	this.mCoorinateVariableFlag = info.mCoorinateVariableFlag;
    	this.mOrigin = info.mOrigin;
    	this.mDimensionList.addAll(info.mDimensionList);
    }
    
    /**
     * Returns true if the variable is of a coordinate variable.
     * 
     * @return true if the variable is of a coordinate variable
     */
    public boolean isCoordinateVariable() {
    	return this.mCoorinateVariableFlag;
    }
    
    /**
     * Sets the origin.
     * 
     * @param origin
     *           the value to set to the origin
     */
    public void setOrigin(final int origin) {
        this.mOrigin = origin;
    }
    
    /**
     * Returns the origin.
     * 
     * @return the origin
     */
    public int getOrigin() {
        return this.mOrigin;
    }

    /**
     * Returns the name of variable.
     * 
     * @return the name variable
     */
    public String getName() {
    	return this.mName;
    }

    /**
     * Returns a text string for this object.
     * 
     * @return a text string for this object
     */
    protected String paramString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.paramString());
        sb.append(", origin=");
        sb.append(this.getOrigin());
        return sb.toString();
    }
    
    /**
     * Returns the list of information of dimensions.
     * 
     * @return the list of information of dimensions
     */
    public List<SGDimensionInfo> getDimensions() {
    	return new ArrayList<SGDimensionInfo>(this.mDimensionList);
    }

    /**
     * Returns the number of dimensions.
     * 
     * @return the number of dimensions
     */
    public int getSize() {
    	return this.mDimensionList.size();
    }

    /**
     * Returns the dimension at given array index.
     * 
     * @param index
     *            the array index
     * @return the dimension at given array index
     */
    public SGDimensionInfo getDimension(final int index) {
    	return this.mDimensionList.get(index);
    }
    
    /**
     * Overrode to copies attributes.
     * 
     * @return a copied object
     */
    @Override
    public Object clone() {
    	SGNetCDFDataColumnInfo info = (SGNetCDFDataColumnInfo) super.clone();
    	info.mDimensionList = new ArrayList<SGDimensionInfo>(this.mDimensionList);
    	return info;
    }

    @Override
    public boolean equals(Object obj) {
    	if (!(obj instanceof SGNetCDFDataColumnInfo)) {
    		return false;
    	}
    	if (!super.equals(obj)) {
    		return false;
    	}
    	SGNetCDFDataColumnInfo col = (SGNetCDFDataColumnInfo) obj;
    	if (!SGUtility.equals(this.mName, col.mName)) {
    		return false;
    	}
    	if (this.mCoorinateVariableFlag != col.mCoorinateVariableFlag) {
    		return false;
    	}
    	if (!SGUtility.equals(this.mDimensionList, col.mDimensionList)) {
    		return false;
    	}
    	if (this.mOrigin != col.mOrigin) {
    		return false;
    	}
    	return true;
    }

}

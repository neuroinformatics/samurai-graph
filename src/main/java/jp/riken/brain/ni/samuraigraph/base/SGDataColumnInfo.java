package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.List;


/**
 * A class of data column.
 */
public abstract class SGDataColumnInfo implements Cloneable {

    /**
     * The title of this column.
     */
    private String title = null;

    /**
     * The type of value such as number, text and so on.
     */
    private String valueType = null;

    /**
     * The type of column such as x-value, tick label and so on.
     */
    private String columnType = "";

    /**
     * List of the attributes.
     */
    protected List<SGAttribute> mAttributeList = new ArrayList<SGAttribute>();
    
    /**
     * A text string for the unit.
     */
    protected String mUnitsString = null;
    
    /**
     * Builds a data column object with a given title and the value type.
     * 
     * @param title
     *            a string to set to the title of this column
     * @param valueType
     *            a value to set to the type of value of this column
     */
    public SGDataColumnInfo(final String title, final String valueType) {
        super();
        this.title = title;
        this.valueType = valueType;
    }

    /**
     * Returns the type of this column.
     * 
     * @return the type of this column
     */
    public String getColumnType() {
        return columnType;
    }

    /**
     * Sets the type of this column.
     * 
     * @param columnType
     *            a value to set to the type of this column
     */
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    /**
     * Returns the title of this column.
     * 
     * @return the title of this column
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this column
     * 
     * @param title
     *            a string to set to the title of this column
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the type of value of this column.
     * 
     * @return the type of value of this column
     */
    public String getValueType() {
        return valueType;
    }

    /**
     * Sets the type of value of this column.
     * 
     * @param columnType
     *            a value to set to the type of value of this column
     */
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    /**
     * Clones this data column.
     * 
     * @return shallow copy of this data column
     */
    public Object clone() {
    	SGDataColumnInfo info = null;
        try {
            info = (SGDataColumnInfo) super.clone();
            info.mAttributeList = new ArrayList<SGAttribute>(this.mAttributeList);
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
        return info;
    }

    /**
     * Returns a text string for this object.
     * 
     * @return a text string for this object
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append(this.paramString());
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Returns a text string for this object.
     * 
     * @return a text string for this object
     */
    protected String paramString() {
        StringBuffer sb = new StringBuffer();
        sb.append("title=");
        sb.append(this.getTitle());
        sb.append(", valueType=");
        sb.append(this.getValueType());
        sb.append(", columnType=");
        sb.append(this.getColumnType());
        return sb.toString();
    }

    public String getName() {
    	return this.getTitle();
    }
    
    public List<SGAttribute> getAttributes() {
    	return new ArrayList<SGAttribute>(this.mAttributeList);
    }
    
    /**
     * Returns the text string for the unit.
     * 
     * @return the text string for the unit
     */
    public String getUnitsString() {
    	return this.mUnitsString;
    }

    @Override
    public boolean equals(Object obj) {
    	if (!(obj instanceof SGDataColumnInfo)) {
    		return false;
    	}
    	SGDataColumnInfo col = (SGDataColumnInfo) obj;
    	if (!SGUtility.equals(this.columnType, col.columnType)) {
    		return false;
    	}
    	if (!SGUtility.equals(this.valueType, col.valueType)) {
    		return false;
    	}
    	if (!SGUtility.equals(this.title, col.title)) {
    		return false;
    	}
    	if (!SGUtility.equals(this.mUnitsString, col.mUnitsString)) {
    		return false;
    	}
    	if (!SGUtility.equals(this.mAttributeList, col.mAttributeList)) {
    		return false;
    	}
    	return true;
    }

}

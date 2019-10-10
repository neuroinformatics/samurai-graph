package jp.riken.brain.ni.samuraigraph.data;

import java.io.IOException;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;

/**
 * The wrapper class for the netCDF variable.
 *
 */
public class SGNetCDFVariable extends SGVariable implements SGINetCDFConstants {

	/**
	 * The variable of netCDF.
	 * 
	 */
	protected Variable mVariable = null;

	/**
	 * The netCDF file.
	 */
	protected SGNetCDFFile mNetCDFFile = null;

	/**
	 * Builds an variable.
	 *
	 * @param ncfile
	 *           the netCDF file
	 */
	protected SGNetCDFVariable(final SGNetCDFFile ncfile) {
		super();
		if (ncfile == null) {
			throw new IllegalArgumentException("ncfile == null");
		}
		this.mNetCDFFile = ncfile;
	}
	
	/**
	 * Builds an object with a given variable.
	 *
	 * @param var
	 *           the variable
	 * @param ncfile
	 *           the netCDF file
	 */
	public SGNetCDFVariable(final Variable var, final SGNetCDFFile ncfile) {
		this(ncfile);
		if (var == null) {
			throw new IllegalArgumentException("var == null");
		}
		this.mVariable = var;
	}

	/**
	 * Disposes of this object.
	 * 
	 */
	@Override
	public void dispose() {
		super.dispose();
		this.mNetCDFFile = null;
		this.mVariable = null;
	}	

	/**
	 * Returns the variable.
	 * 
	 * @return the variable
	 */
	public Variable getVariable() {
		return this.mVariable;
	}

	/**
	 * Returns the netCDF file.
	 * 
	 * @return the netCDF file
	 */
	public SGNetCDFFile getNetCDFFile() {
		return this.mNetCDFFile;
	}

	/**
	 * Returns the name of this variable.
	 * 
	 * @return the name of this variable
	 */
	public String getName() {
		return this.mVariable.getShortName();
	}
	
	/**
	 * Returns the long name if it exists.
	 * 
	 * @return the long name
	 */
	public String getLongName() {
		return this.getAttrStringValue(ATTR_LONG_NAME);
	}

	/**
	 * Returns the standard name if it exists.
	 * 
	 * @return the standard name
	 */
	public String getStandardName() {
		return this.getAttrStringValue(ATTR_STANDARD_NAME);
	}

    private String getAttrStringValue(String attr) {
    	Variable var = this.getVariable();
    	if (var == null) {
    		return null;
    	}
        Attribute attrValue = var.findAttribute(attr);
        if (attrValue != null) {
            return attrValue.getStringValue();
        } else {
            return null;
        }
    }

	/**
	 * Returns the fill value if it exists.
	 * 
	 * @return the fill value
	 */
    public Number getFillValue() {
    	return this.getAttrNumericValue(ATTR_FILL_VALUE);
    }

	/**
	 * Returns the missing value if it exists.
	 * 
	 * @return the missing value
	 */
    public Number getMissingValue() {
    	return this.getAttrNumericValue(ATTR_MISSING_VALUE);
    }

    private Number getAttrNumericValue(String attr) {
    	Variable var = this.getVariable();
    	if (var == null) {
    		return null;
    	}
        Attribute attrValue = var.findAttribute(attr);
        if (attrValue != null) {
            return attrValue.getNumericValue();
        } else {
            return null;
        }
    }
    
    /**
     * Returns the valid range if it exists.
     *
     * @return the valid range
     */
    public double[] getValidRange() {
        Attribute attrValidRange = this.findAttribute(ATTR_VALID_RANGE);
        if (attrValidRange == null) {
            return null;
        }
        final int len = attrValidRange.getLength();
        if (len != 2) {
        	return null;
        }
        Number num0 = attrValidRange.getNumericValue(0);
        if (num0 == null) {
        	return null;
        }
        Number num1 = attrValidRange.getNumericValue(1);
        if (num1 == null) {
        	return null;
        }
    	double[] ret = { num0.doubleValue(), num1.doubleValue() };
        return ret;
    }

    /**
     * Returns the minimum valid value if it exists.
     *
     * @return the minimum valid value
     */
    public Number getValidMin() {
    	return this.getAttrNumericValue(ATTR_VALID_MIN);
    }
    
    /**
     * Returns the maximum valid value if it exists.
     *
     * @return the maximum valid value
     */
    public Number getValidMax() {
    	return this.getAttrNumericValue(ATTR_VALID_MAX);
    }
    
	/**
	 * Returns the name of this variable in order of priority:
	 * long_name, standard_name and variable name.
	 * 
	 * @return the name of this variable
	 */
	public String getNameInPriorityOrder() {
		String name = null;
		if ((name = this.getLongName()) != null) {
			return name;
		} else if ((name = this.getStandardName()) != null) {
			return name;
		} else {
			return this.getName();
		}
	}
	
	public int findDimensionIndex(final String name) {
		return this.mVariable.findDimensionIndex(name);
	}
	
	public List<Dimension> getDimensions() {
		return this.mVariable.getDimensions();
	}
	
	public Dimension getDimension(final int index) {
		return this.mVariable.getDimension(index);
	}
	
	public boolean isCoordinateVariable() {
		return this.mVariable.isCoordinateVariable();
	}
	
	public Attribute findAttribute(final String name) {
		return this.mVariable.findAttribute(name);
	}

	public String getUnitsString() {
		return this.mVariable.getUnitsString();
	}
	
	public boolean isUnlimited() {
		return this.mVariable.isUnlimited();
	}
	
	public Array read(int[] origin, int[] shape) throws InvalidRangeException, IOException {
		return this.mVariable.read(origin, shape);
	}
	
	public Array read(String sectionSpec) throws InvalidRangeException, IOException {
		return this.mVariable.read(sectionSpec);
	}
	
	public Array read() throws IOException {
		return this.mVariable.read();
	}

	public List<Attribute> getAttributes() {
		return this.mVariable.getAttributes();
	}
	
	public String toString() {
		if (this.mVariable == null) {
			return "null";
		} else {
			return this.mVariable.toString();
		}
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof SGNetCDFVariable)) {
			return false;
		}
		SGNetCDFVariable var = (SGNetCDFVariable) obj;
		return SGUtility.equals(var.mVariable, this.mVariable);
	}

	/**
	 * Returns the value type.
	 * 
	 * @return the value type
	 */
	public String getValueType() {
		return SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER;
	}

	/**
	 * Returns the array of numbers only when this variable is for a coordinate variable.
	 * 
	 * @return the array of numbers
	 */
	public double[] getNumberArray() {
		if (!this.isCoordinateVariable()) {
			return null;
		}
        Array array = null;
        try {
            array = this.mVariable.read();
        } catch (IOException e) {
            return null;
        }
        final int len = (int) array.getSize();
    	if (len == 0) {
    		// if the length of a coordinate variable is zero, returns null
    		return null;
    	}
        double[] values = new double[len];
        for (int ii = 0; ii < len; ii++) {
            values[ii] = array.getDouble(ii);
        }
        return values;
	}

	/**
	 * Returns the data type.
	 * 
	 * @return the data type
	 */
	public DataType getDataType() {
		return this.mVariable.getDataType();
	}
	
	protected String getValidName() {
		return SGDataUtility.getNetCDFValidName(this.getName());
	}
}

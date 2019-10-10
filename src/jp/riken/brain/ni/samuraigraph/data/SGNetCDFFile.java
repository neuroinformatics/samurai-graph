package jp.riken.brain.ni.samuraigraph.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import jp.riken.brain.ni.samuraigraph.base.SGIStringModifier;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import ucar.ma2.Array;
import ucar.ma2.ArrayChar;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 * The wrapper class for netCDF file.
 *
 */
public class SGNetCDFFile extends SGDataSource implements SGINetCDFConstants {

	/**
	 * The netCDF file.
	 * 
	 */
	private NetcdfFile mNetcdfFile = null;
	
	/**
	 * The list of variables.
	 * 
	 */
	private List<SGNetCDFVariable> mVariableList = new ArrayList<SGNetCDFVariable>();

	/**
	 * A flag whether the NetCDF file is in remote location.
	 */
	private boolean mRemoteFileFlag = true;

	/**
	 * Builds this object with given netCDF file.
	 * 
	 * @param ncfile
	 *           a netCDF file
	 */
	public SGNetCDFFile(NetcdfFile ncfile) {
		this(ncfile, null);
	}
	
	/**
	 * Builds this object with given netCDF file and a string modifier.
	 * 
	 * @param ncfile
	 *           a netCDF file
	 * @param mod
	 *           a string modifier
	 */
	public SGNetCDFFile(NetcdfFile ncfile, SGIStringModifier mod) {
		super();

		if (ncfile == null) {
			throw new IllegalArgumentException("ncfile == null");
		}
		
        List<Variable> varList = new ArrayList<Variable>(ncfile.getVariables());
        
		// get dimension which is used as max length of string.
		List<Dimension> textLengthDimension = new ArrayList<Dimension>();
		for (Variable var : varList) {
		    if (SGDataUtility.isSGTextVariable(var) || SGDataUtility.isSGDateVariable(var)) {
		    	List<Dimension> dims = var.getDimensions();
		        textLengthDimension.add(var.getDimension(dims.size() - 1));
		    }
		}
		
		// get dimension which is used as the length of byte data
		List<Dimension> byteLengthDimension = new ArrayList<Dimension>();
		for (Variable var : varList) {
		    if (DataType.BYTE.equals(var.getDataType())) {
		    	byteLengthDimension.add(var.getDimension(0));
		    }
		}

		// add coordinate variables
		for (Variable var : varList) {
			if (var.isCoordinateVariable()) {
                this.mVariableList.add(new SGNetCDFVariable(var, this));
			}
		}
		
		// add coordinate variables that do not exist in the file
		List<Dimension> dimList = ncfile.getDimensions();
		for (Dimension dim : dimList) {
			boolean exists = false;
			for (Variable var : varList) {
				if (var.isCoordinateVariable()) {
					Dimension varDim = var.getDimension(0);
					if (varDim.equals(dim)) {
						// the coordinate variable exists
						exists = true;
						break;
					}
				}
			}
			if (!exists) {
			    if (textLengthDimension.contains(dim)) {
			        exists = true;
			    }
			}
			if (!exists) {
			    if (byteLengthDimension.contains(dim)) {
			        exists = true;
			    }
			}
			if (!exists) {
				this.mVariableList.add(new SGCoordinateVariable(dim, this));
			}
		}
		
		// add remaining variables
		for (Variable var : varList) {
			if (!var.isCoordinateVariable()) {
				DataType dataType = var.getDataType();
                SGNetCDFVariable ncVar = null;
			    if (DataType.CHAR.equals(dataType)) {
			    	// text or date
			        List<Dimension> dimensionList = var.getDimensions();
			        if (dimensionList.size() == 2) {
			        	// use old classes for backward compatibility
	                    int[] shape = var.getShape();
	                    String[] str = new String[shape[0]];
	                    try {
	                        Array array = var.read();
	                        if (array instanceof ArrayChar) {
	                        	ArrayChar cArray = (ArrayChar)array;
	                        	Index index = cArray.getIndex();
	                            for (int ii = 0; ii < shape[0]; ii++) {
	                            	byte[] byteArray = new byte[shape[1]];
	                            	for (int jj = 0; jj < shape[1]; jj++) {
	                            		byteArray[jj] = cArray.getByte(index.set(ii, jj));
	                            	}
	                            	str[ii] = SGUtility.createString(byteArray);
	                            }
	                        }
	                    } catch (IOException e) {
	                        continue;
	                    }
	                    if (SGDataUtility.isSGDateVariable(var)) {
		                    ncVar = new SGDateVariable(var, this, str);
	                    } else {
		                    ncVar = new SGTextVariable(var, this, str, mod);
	                    }
			        } else if (dimensionList.size() > 2) {
			        	// use new class for multidimensional text array
	                    ncVar = new SGNetCDFTextVariable(var, this, mod);
			        }
			    } else if (DataType.BYTE.equals(dataType)) {
			    	// byte data
                    ncVar = new SGByteDataVariable(var, this);
				} else if (DataType.DOUBLE.equals(dataType)
						|| DataType.FLOAT.equals(dataType)
						|| DataType.LONG.equals(dataType)
						|| DataType.INT.equals(dataType)
						|| DataType.SHORT.equals(dataType)) {
			    	// number
			        ncVar = new SGNetCDFVariable(var, this);
			    } else {
			    	continue;
			    }
			    
			    if (ncVar == null) {
			    	continue;
			    }
			    
			    // add to the list
	        	this.mVariableList.add(ncVar);
			}
		}
		
        // set to attributes
		this.mNetcdfFile = ncfile;

		// set up the remote flag
		boolean remote = true;
		try {
			new URL(ncfile.getLocation());
		} catch (MalformedURLException e) {
			remote = false;
		}
		this.mRemoteFileFlag = remote;
	}
	
	/**
	 * Returns the netCDF file.
	 * 
	 * @return the netCDF file
	 */
	public NetcdfFile getNetcdfFile() {
		return this.mNetcdfFile;
	}

	/**
	 * Returns a list of variables.
	 * 
	 * @return a list of variables
	 */
	public List<SGNetCDFVariable> getVariables() {
		return new ArrayList<SGNetCDFVariable>(this.mVariableList);
	}
	
	/**
	 * Finds and returns a variable of a given name if it exists.
	 * 
	 * @param name
	 *           the name of variable
	 * @return found variable
	 */
	public SGNetCDFVariable findVariable(String name) {
		for (SGNetCDFVariable var : this.mVariableList) {
			if (var.getName().equals(name)) {
				return var;
			}
		}
		return null;
	}

	/**
	 * Finds and returns a variable of a given name if it exists.
	 * 
	 * @param name
	 *           the name of variable
	 * @return found variable
	 */
	public SGNetCDFVariable findVariableEx(final String name) {
		for (int ii = 0; ii < this.mVariableList.size(); ii++) {
			SGNetCDFVariable var = this.mVariableList.get(ii);
			String vName;
			if ((vName = var.getLongName()) != null) {
				if (name.equals(vName)) {
					return var;
				}
			}
			if ((vName = var.getStandardName()) != null) {
				if (name.equals(vName)) {
					return var;
				}
			}
			if (name.equals(var.getName())) {
				return var;
			}
		}
		return null;
	}
	
	/**
	 * Finds a variable of a given name if it exists and returns its array index.
	 * 
	 * @param name
	 *           the name of variable
	 * @return the array index of found variable or -1 if it is not found
	 */
	public int getVariableIndex(final String name) {
		for (int ii = 0; ii < this.mVariableList.size(); ii++) {
			SGNetCDFVariable var = this.mVariableList.get(ii);
			if (name.equals(var.getName())) {
				return ii;
			}
//			if (var instanceof SGTextVariable) {
//			    if (name.equals(var.getName())) {
//			        return ii;
//			    }
//			}
		}
		return -1;
	}
	
	public List<Dimension> getDimensions() {
		return this.mNetcdfFile.getDimensions();
	}
	
	public Dimension findDimension(String name) {
		return this.mNetcdfFile.findDimension(name);
	}

	public String toString() {
		if (this.mNetcdfFile == null) {
			return "null";
		} else {
			return this.mNetcdfFile.toString();
		}
	}

	/**
	 * Finds and returns a text variable.
	 * 
	 * @param dimName
	 *            the name of dimension
	 * @return a text variable for a given dimension
	 */
	public SGTextVariable findTextVariable(String dimName) {
		SGTextVariable ret = null;
		for (int ii = 0; ii < this.mVariableList.size(); ii++) {
			SGNetCDFVariable var = this.mVariableList.get(ii);
			if (var instanceof SGTextVariable) {
				SGTextVariable tVar = (SGTextVariable) var;
				if (tVar.getName().endsWith(dimName)) {
					ret = tVar;
					break;
				}
			}
		}
		return ret;
	}

    /**
     * Clones this data file.
     * 
     * @return copy of this data file
     */
    public Object clone() {
    	SGNetCDFFile dataFile = null;
        try {
            dataFile = (SGNetCDFFile) super.clone();
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
        dataFile.mVariableList = new ArrayList<SGNetCDFVariable>();
        dataFile.mVariableList.addAll(this.mVariableList);
        return dataFile;
    }

	/**
	 * Disposes of this object.
	 */
	public void dispose() {
    	super.dispose();
    	if (this.mVariableList != null) {
    		this.mVariableList.clear();
            this.mVariableList = null;
    	}
    	if (this.mNetcdfFile != null) {
            try {
                this.mNetcdfFile.close();
            } catch (IOException ioe) {
            }
            this.mNetcdfFile = null;
    	}
	}

    /**
     * Returns the file path.
     * 
     * @return the file path
     */
    public String getPath() {
    	return this.mNetcdfFile.getLocation();
    }

    /**
     * Returns the list of attributes.
     * 
     * @return the list of attributes
     */
	public List<SGAttribute> getAttributes() {
        List<Attribute> attrList = this.getNetcdfFile().getGlobalAttributes();
        List<SGAttribute> aList = new ArrayList<SGAttribute>();
        for (Attribute attr : attrList) {
        	final int len = attr.getLength();
        	Object[] values = new Object[len];
        	for (int ii = 0; ii < len; ii++) {
        		values[ii] = attr.getValue(ii);
        	}
        	SGAttribute a = new SGAttribute(attr.getName(), values);
        	aList.add(a);
        }
        return aList;
	}

	/**
	 * Returns whether the NetCDF file is in remote locations.
	 * @return
	 */
	public boolean isRemoteFile() {
		return this.mRemoteFileFlag;
	}
	
}

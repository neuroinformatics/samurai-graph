/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SGNetCDFDataColumnSelectionPanel.java
 *
 * Created on 2009/08/20, 14:59:51
 */

package jp.riken.brain.ni.samuraigraph.data;

import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGTable;


/**
 * A panel to select the data columns for netCDF type data.
 *
 */
public class SGNetCDFDataColumnSelectionPanel extends SGDataColumnSelectionPanel {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -8916914942493060714L;
    
    /** Creates new form SGNetCDFDataColumnSelectionPanel */
    public SGNetCDFDataColumnSelectionPanel() {
        super();
    }

    /**
     * Name of the column for the name of each data column.
     */
    public static final String COLUMN_NAME_NAME = "Name";

    /**
     * Name of the column for the dimension of each data column.
     */
    public static final String COLUMN_NAME_DIMENSION = "Dimension";

    /**
     * The array of column name.
     */
    public static final String[] COLUMN_NAME_ARRAY = { COLUMN_NAME_NUMBER,
        COLUMN_NAME_NAME, COLUMN_NAME_VALUE_TYPE, COLUMN_NAME_DIMENSION, COLUMN_NAME_COLUMN_TYPE };

    /**
     * A constant array of column width.
     */
    private static final int[] PREFERRED_COLUMN_WIDTH_ARRAY = { 10, 80, 60, 80, 
        120 };

    /**
     * An array of acceptable data types.
     */
    private static final String[] DATA_TYPE_ARRAY = {
            SGDataTypeConstants.SXY_NETCDF_DATA,
            SGDataTypeConstants.SXYZ_NETCDF_DATA,
            SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA,
            SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA,
            SGDataTypeConstants.VXY_NETCDF_DATA };

    protected String[] getColumnNameArray() {
        return COLUMN_NAME_ARRAY;
    }
    
    protected int[] getPreferredColumnWidthArray() {
        return PREFERRED_COLUMN_WIDTH_ARRAY;
    }

    protected String[] getDataTypeArray() {
        return DATA_TYPE_ARRAY;
    }

    protected DataColumnTableModel createTableModel() {
        return new NetCDFDataColumnTableModel();
    }

    /**
     * A class of table model.
     */
    protected class NetCDFDataColumnTableModel extends DataColumnTableModel {
        
        /**
         * 
         */
        private static final long serialVersionUID = 5724156778412127794L;

        public NetCDFDataColumnTableModel() {
            super();
        }

        protected void addData(int index, SGDataColumnInfo info) {
            
            SGNetCDFDataColumnInfo ncInfo = (SGNetCDFDataColumnInfo) info;
                
            String cType = ncInfo.getColumnType();
            if (cType == null) {
                cType = "";
            }

            List<SGDimensionInfo> dimList = ncInfo.getDimensions();
            StringBuffer sb = new StringBuffer();
            final int len = dimList.size();
            for (int ii = 0; ii < len; ii++) {
                SGDimensionInfo dim = dimList.get(ii);
                sb.append(dim.getName());
                if (ii != len - 1) {
                    sb.append(", ");
                }
            }

            // set the variable to the cell of a button
            Object[] array = { Integer.toString(index + 1), info.getTitle(),
                    info.getValueType(), sb.toString(), cType };
            
            super.addRow(array);
        }
    }
    
    /**
     * Checks selected items.
     * 
     * @return true if selected items are valid for the data type
     */
    public boolean checkSelectedItems() {

        List<SGDataColumnInfo> columnInfo = this.getColumnInfoList();
        final int len = this.getColumnInfoList().size();
        
        final int colIndexName = this.getColumnIndex(COLUMN_NAME_NAME);
        final int colIndexColumnType = this.getColumnIndex(COLUMN_NAME_COLUMN_TYPE);
        
        SGNetCDFDataColumnInfo[] items = new SGNetCDFDataColumnInfo[len];
        for (int ii = 0; ii < len; ii++) {
            String name = (String) this.getValueAt(ii, colIndexName);
            if (name == null) {
                name = "";
            }
            SGNetCDFDataColumnInfo info = (SGNetCDFDataColumnInfo) columnInfo.get(ii);
            items[ii] = new SGNetCDFDataColumnInfo(info, name, info.getValueType());
            String columnType = (String) this.getValueAt(ii, colIndexColumnType);
            if (columnType == null) {
            	columnType = "";
            }
            items[ii].setColumnType(columnType);
        }
        
        // call a static utility method
        if (SGDataUtility.checkDataColumns(this.mDataType, items,
                this.mDataInfoMap) == false) {
        	this.setMessage(SGDataUtility.MSG_PROPER_COLUMN_TYPE);
            return false;
        }

        // clears the message
        this.clearMessage();

        return true;
    }
    
    /**
     * Creates and returns a table object.
     * 
     * @return a table object
     */
    protected SGTable createTableInstance() {
    	return new SGTable();
    }
    
	@Override
	protected void initTableModel(String dataType) {
		this.initTableModel(new NetCDFDataColumnTableModel());
	}

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SGTextDataColumnSelectionPanel.java
 *
 * Created on 2009/08/20, 15:09:35
 */

package jp.riken.brain.ni.samuraigraph.data;

import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGTable;


/**
 * A panel to select columns from an array data.
 *
 */
public class SGSDArrayDataColumnSelectionPanel extends SGDataColumnSelectionPanel {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 3814702849177447212L;

    /** Creates new form SGTextDataColumnSelectionPanel */
    public SGSDArrayDataColumnSelectionPanel() {
        super();
    }

    /**
     * Name of the column for the title of each data column.
     */
    public static final String COLUMN_NAME_TITLE = "Title";

    /**
     * Name of the column for the value type of each data column such as
     * number or text.
     */
    public static final String COLUMN_NAME_VALUE_TYPE = "Value Type";

    public static final String[] COLUMN_NAME_ARRAY = { COLUMN_NAME_NUMBER,
        COLUMN_NAME_TITLE, COLUMN_NAME_VALUE_TYPE, COLUMN_NAME_COLUMN_TYPE };

    /**
     * A constant array of column width.
     */
    private static final int[] PREFERRED_COLUMN_WIDTH_ARRAY = { 10, 80, 80, 110 };
    
    /**
     * An array of acceptable data types.
     */
    private static final String[] DATA_TYPE_ARRAY = {
            SGDataTypeConstants.SXY_DATA,
            SGDataTypeConstants.VXY_DATA,
            SGDataTypeConstants.SXYZ_DATA,
            SGDataTypeConstants.SXY_MULTIPLE_DATA,
            SGDataTypeConstants.SXY_SAMPLING_DATA,
            SGDataTypeConstants.SXY_DATE_DATA };

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
        return new TextDataColumnTableModel();
    }

    /**
     * A class of table model.
     */
    protected class TextDataColumnTableModel extends DataColumnTableModel {
        
        /**
         * 
         */
        private static final long serialVersionUID = -6117760685886862082L;

        public TextDataColumnTableModel() {
            super();
        }

        protected void addData(int index, SGDataColumnInfo data) {
            String cType = data.getColumnType();
            if (cType == null) {
                cType = "";
            }
            Object[] array = { Integer.toString(index + 1), data.getTitle(),
                    data.getValueType(), cType };
            super.addRow(array);
        }
    }

    /**
     * Checks selected items.
     * 
     * @return true if selected items are valid for the data type
     */
    public boolean checkSelectedItems() {
    	
        final int colNum = this.getColumnInfoList().size();
        final int colIndexTitle = this.getColumnIndex(COLUMN_NAME_TITLE);
        final int colIndexValueType = this.getColumnIndex(COLUMN_NAME_VALUE_TYPE);
        final int colIndexColumnType = this.getColumnIndex(COLUMN_NAME_COLUMN_TYPE);
        SGDataColumnInfo[] items = new SGDataColumnInfo[colNum];
        for (int ii = 0; ii < colNum; ii++) {
            String title = (String) this.getValueAt(ii, colIndexTitle);
            if (title == null) {
                title = "";
            }
            String valueType = (String) this.getValueAt(ii, colIndexValueType);
            if (valueType == null) {
            	valueType = "";
            }
            items[ii] = new SGSDArrayDataColumnInfo(title, valueType);
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
		this.initTableModel(new TextDataColumnTableModel());
	}
}

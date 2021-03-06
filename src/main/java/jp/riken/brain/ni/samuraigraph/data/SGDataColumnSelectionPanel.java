/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJPanel.java
 *
 * Created on 2009/07/07, 17:24:33
 */

package jp.riken.brain.ni.samuraigraph.data;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import jp.riken.brain.ni.samuraigraph.base.SGButton;
import jp.riken.brain.ni.samuraigraph.base.SGComboBox;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGTable;


/**
 * A panel to select the data columns.
 * 
 */
public abstract class SGDataColumnSelectionPanel extends javax.swing.JPanel
        implements SGIDataColumnTypeConstants, MouseWheelListener,  
        ActionListener, PropertyChangeListener, SGIDataSetupTableHolder {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 4393429488857680488L;
    
    /** Creates new form NewJPanel */
    public SGDataColumnSelectionPanel() {
        super();
        initComponents();
        this.initProperty();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mComplementButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mTableScrollPane = new javax.swing.JScrollPane();
        mTable = this.createTableInstance();
        mButtonPanel = new javax.swing.JPanel();
        mRestoreButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mClearButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mMessagePanel = new javax.swing.JPanel();
        mMessageLabel = new javax.swing.JLabel();

        mComplementButton.setText("Complement");
        mComplementButton.setFont(new java.awt.Font("Dialog", 1, 12));

        setLayout(new java.awt.GridBagLayout());

        mTableScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mTableScrollPane.setFont(new java.awt.Font("Dialog", 0, 11));
        mTableScrollPane.setPreferredSize(new java.awt.Dimension(550, 150));

        mTable.setFont(new java.awt.Font("Dialog", 0, 11));
        mTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        mTableScrollPane.setViewportView(mTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        add(mTableScrollPane, gridBagConstraints);

        mRestoreButton.setText("Restore");
        mRestoreButton.setFont(new java.awt.Font("Dialog", 1, 12));
        mButtonPanel.add(mRestoreButton);

        mClearButton.setText("Clear");
        mClearButton.setFont(new java.awt.Font("Dialog", 1, 12));
        mButtonPanel.add(mClearButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(mButtonPanel, gridBagConstraints);

        mMessagePanel.setLayout(new java.awt.GridBagLayout());

        mMessageLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        mMessageLabel.setForeground(new java.awt.Color(255, 0, 0));
        mMessageLabel.setText("message");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        mMessagePanel.add(mMessageLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        add(mMessagePanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mButtonPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mClearButton;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mComplementButton;
    private javax.swing.JLabel mMessageLabel;
    private javax.swing.JPanel mMessagePanel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mRestoreButton;
    protected jp.riken.brain.ni.samuraigraph.base.SGTable mTable;
    private javax.swing.JScrollPane mTableScrollPane;
    // End of variables declaration//GEN-END:variables

    /**
     * The table model.
     */
    protected DataColumnTableModel mTableModel = null;
    
    protected abstract String[] getColumnNameArray();
    
    protected abstract int[] getPreferredColumnWidthArray();
    
    protected abstract DataColumnTableModel createTableModel();
    
    /**
     * Initializes the table model.
     */
    protected abstract void initTableModel(String dataType);

    protected void initTableModel(DataColumnTableModel model) {
        this.mTableModel = model;
        this.mTableModel.setColumnIdentifiers(this.getColumnNameArray());
        this.mTable.setModel(this.mTableModel);
    }

    /**
     * A class of table model.
     */
    protected abstract class DataColumnTableModel extends DefaultTableModel {
        
        /**
         * Serial Version UID
         */
        private static final long serialVersionUID = 2213043296565029421L;

        public DataColumnTableModel() {
            super();
        }

        /**
         * Returns false for the column for column type.
         */
        public boolean isCellEditable(int row, int col) {
            if (getEditableColumnNameList().contains(getColumnName(col))) {
                return true;
            } else {
                return false;
            }
        }

        protected abstract void addData(int index, SGDataColumnInfo data);
        
        public int getColumnCount() {
            return getColumnNameArray().length;
        }

        public String getColumnName(int index) {
            return getColumnNameArray()[index];
        }
    }
    
    protected List<String> getEditableColumnNameList() {
    	List<String> list = new ArrayList<String>();
    	list.add(COLUMN_NAME_COLUMN_TYPE);
    	return list;
    }
    
    /**
     * Returns the table.
     * 
     * @return a table object
     */
    public JTable getTable() {
        return this.mTable;
    }

    /**
     * Creates and returns an instance of a combo box.
     * 
     * @return a new instance of the combo box
     */
    protected SGComboBox createComboBoxInstance() {
    	SGComboBox cb = new SGComboBox();
        cb.setBorder(BorderFactory.createEmptyBorder());
        return cb;
    }
    
    /**
     * Initialize this panel.
     */
    private void initProperty() {

        this.mRestoreButton.setEnabled(false);
        this.mClearButton.setEnabled(false);
        this.mComplementButton.setEnabled(false);

        // setup the combo box
        this.mColumnTypeEditorComboBox = this.createComboBoxInstance();
        this.mColumnTypeRendererComboBox = this.createComboBoxInstance();
        this.mColumnTypeEditorComboBox.setMaximumRowCount(12);
        this.mColumnTypeRendererComboBox.setMaximumRowCount(12);
        
        // add the table as a mouse wheel listener
        this.mTable.addMouseWheelListener(this);
        
        // add an action event listener
        this.mClearButton.addActionListener(this);
        this.mRestoreButton.addActionListener(this);
        this.mComplementButton.addActionListener(this);

        // add a property change event listener
        this.mTable.addPropertyChangeListener(this);

        // setup the scroll bar properties
        JScrollBar vertBar = this.mTableScrollPane.getVerticalScrollBar();
        vertBar.setUnitIncrement(SGIConstants.SCROLL_BAR_UNIT_INCREMENT);
        vertBar.setBlockIncrement(SGIConstants.SCROLL_BAR_BLOCK_INCREMENT);
        
        // setup the message label
        this.clearMessage();
    }

    /**
     * Clear all data.
     */
    public void clear() {
        
        // clear attributes
    	this.clearAttributes();
        
        // update the table
        this.updateTable();
    }
    
    private void clearAttributes() {
        this.mDataType = null;
        this.mColumnInfoList.clear();
        this.mDataInfoMap = null;
        this.mInitColumnInfoArray = null;
    }

    /**
     * Name of the column for the sequential number.
     */
    public static final String COLUMN_NAME_NUMBER = "No.";

    /**
     * Name of the column for the title.
     */
    public static final String COLUMN_NAME_TITLE = "Title";

    /**
     * Name of the column for the type of the value of each data column.
     */
    public static final String COLUMN_NAME_VALUE_TYPE = "Value Type";

    /**
     * Name of the column for the type of each data column.
     */
    public static final String COLUMN_NAME_COLUMN_TYPE = "Column Type";


    /**
     * The type of data.
     */
    protected String mDataType = null;

    /**
     * A list of column information.
     */
    protected List<SGDataColumnInfo> mColumnInfoList = new ArrayList<SGDataColumnInfo>();

    /**
     * A map of data information.
     */
    protected Map<String, Object> mDataInfoMap = null;
    
    /**
     * A flag whether to set default data column type.
     */
    protected boolean mShowDefaultFlag = false;

    /**
     * A combo box for table cell renderer for the column type.
     */
    SGComboBox mColumnTypeRendererComboBox = null;

    /**
     * A combo box for table cell editor for the column type.
     */
    SGComboBox mColumnTypeEditorComboBox = null;

    /**
     * A list of column information.
     */
    protected SGDataColumnInfo[] mInitColumnInfoArray = null;

    /**
     * Update the table.
     */
    public void updateTable() {
    	if (this.mTableModel != null) {
            this.setPreferredColumnWidth();
    	}
    }

    /**
     * Set preferred size of columns.
     */
    private void setPreferredColumnWidth() {
    	if (this.mDataType == null) {
    		// do nothing
    		return;
    	}
        String[] cArray = getColumnNameArray();
        int[] pArray = getPreferredColumnWidthArray();
        for (int ii = 0; ii < cArray.length; ii++) {
            TableColumn cl = this.mTable.getColumn(cArray[ii]);
            cl.setPreferredWidth(pArray[ii]);
        }
    }
    
    /**
     * Set the data type.
     * @param dataType
     *                the data type to be set
     */
    public void setDataType(final String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("dataType == null");
        }
        this.mDataType = dataType;
    }
    
    /**
     * Returns the data type.
     * 
     * @return the data type
     */
    public String getDataType() {
    	return this.mDataType;
    }
    
    protected abstract String[] getDataTypeArray();

    /**
     * Set information of data columns.
     *
     * @param dataType
     *            type of data
     * @param colInfoSet
     *            data columns
     * @param infoMap
     *            a map of information
     * @param showDefault
     *            a flag whether to show default column type
     * @return true if succeeded
     */
    public boolean setData(String dataType, SGDataColumnInfoSet colInfoSet, 
    		Map<String, Object> infoMap, final boolean showDefault) {
        
        if (dataType == null || colInfoSet == null) {
            throw new IllegalArgumentException(
                    "dataType == null || colInfoSet == null");
        }

        // check data type
        String[] dataTypeArray = this.getDataTypeArray();
        boolean bType = false;
        for (int ii = 0; ii < dataTypeArray.length; ii++) {
            if (dataTypeArray[ii].equals(dataType)) {
                bType = true;
                break;
            }
        }
        if (!bType) {
            throw new IllegalArgumentException("Data type is not supported: "
                    + dataType);
        }
        
        // clear old data
        this.clearAttributes();

        // set the data type
        this.setDataType(dataType);
        
        // set new table model
        this.initTableModel(dataType);
        this.updateTable();

        // get data columns
        SGDataColumnInfo[] cols = colInfoSet.getDataColumnInfoArray();

        // set to attributes
        for (int ii = 0; ii < cols.length; ii++) {
            SGDataColumnInfo info = (SGDataColumnInfo) cols[ii].clone();
            this.mColumnInfoList.add(info);
        }
        this.mDataInfoMap = infoMap;
        this.mShowDefaultFlag = showDefault;

        // set up the combo_box
        final int colIndexColumnType = this.getColumnIndex(COLUMN_NAME_COLUMN_TYPE);
        TableColumn col = this.mTable.getColumnModel().getColumn(
                colIndexColumnType);
        col.setCellEditor(new ColumnTypeCellEditor(this.mColumnTypeEditorComboBox));
        col.setCellRenderer(new DataColumnCellRenderer(this.mColumnTypeRendererComboBox));

        // set up the table
        for (int ii = 0; ii < this.mColumnInfoList.size(); ii++) {
            SGDataColumnInfo cInfo = (SGDataColumnInfo) this.mColumnInfoList.get(ii);
            this.mTableModel.addData(ii, cInfo);
        }

        // initialize the column for data column type
        if (showDefault) {
            if (this.setDefaultColumnType() == false) {
                return false;
            }
        }
        
        this.mRestoreButton.setEnabled(true);
        this.mClearButton.setEnabled(true);
        this.mComplementButton.setEnabled(true);
        
        // show or hide the complement button
        if (infoMap.containsKey(SGIDataInformationKeyConstants.KEY_DATA_TYPE) == false) {
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, dataType);
        }
//        final boolean compVisible = SGDataUtility.isComplementButtonVisible(infoMap);
//        this.mComplementButton.setVisible(compVisible);
        // not use complement button.
        this.mComplementButton.setVisible(false);

        // set the value of vertical scroll bar to zero
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                mTableScrollPane.getVerticalScrollBar().setValue(0);
            }
        });

        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                // updates the column information with given data
                syncAllDataColumnInfoWithTable();
                
                // saves initial column types
                mInitColumnInfoArray = getDataColumnInfoArray();
            }
        });

    	// selects a row
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    	        int firstRow = -1;
    	        final int rowCnt = getRowCount();
    	        final int columnIndexColumnType = getColumnIndex(COLUMN_NAME_COLUMN_TYPE);
    	        for (int ii = 0; ii < rowCnt; ii++) {
    	        	Object columnType = mTable.getValueAt(ii, columnIndexColumnType);
    	        	if (!"".equals(columnType)) {
    	        		firstRow = ii;
    	        		break;
    	        	}
    	        }
    	        if (firstRow != -1) {
    	        	mTable.setRowSelectionInterval(firstRow, firstRow);
    	        	final int value = firstRow * SGTable.ROW_HEIGHT;
    	        	mTableScrollPane.getVerticalScrollBar().setValue(value);
    	        }
    		}
    	});

    	// clears the message
    	this.clearMessage();    	
    	
        return true;
    }
    
    protected List<SGDataColumnInfo> getColumnInfoList() {
    	List<SGDataColumnInfo> list = new ArrayList<SGDataColumnInfo>(this.mColumnInfoList);
    	return list;
    }
    
    protected boolean setDefaultColumnType() {
        // If default column types are to be shown, get them.
        // If null value is returned, return false.
        SGDefaultColumnTypeUtility.DefaultColumnTypeResult result = SGDefaultColumnTypeUtility
                .getDefaultColumnTypes(this.mDataType, this.getColumnInfoList(),
                        this.mDataInfoMap);
        String[] columnTypes = result.getDefaultColumnTypes();
        for (int ii = 0; ii < columnTypes.length; ii++) {
        	this.setColumnType(ii, columnTypes[ii]);
        }
        return result.isSucceeded();
    }

    /**
     * Checks selected items.
     * 
     * @return true if selected items are valid for the data type
     */
    public abstract boolean checkSelectedItems();
    
    /**
     * Updates selected items.
     * 
     * @return true if succeeded
     */
    public boolean updateSelectedItems() {
    	
        final int rowNum = this.getRowCount();
        String[] items = this.getColumnTypes();
        
        // call a static utility method
        String[] columnTypes = SGDataUtility.updateDataColumns(this.mDataType,
                this.mColumnInfoList.toArray(new SGDataColumnInfo[this.mColumnInfoList.size()]),
                items);
        if (columnTypes == null) {
        	return false;
        }
        
        // set to the table
        for (int ii = 0; ii < rowNum; ii++) {
            this.setColumnType(ii, columnTypes[ii]);
        }
        
    	return true;
    }
    
    /**
     * Returns the array of titles from the table.
     * 
     * @return the array of titles from the table
     */
    public String[] getTitles() {
    	return this.getValues(COLUMN_NAME_TITLE);
    }

    /**
     * Returns the array of value types from the table.
     * 
     * @return the array of value types from the table
     */
    public String[] getValueTypes() {
    	return this.getValues(COLUMN_NAME_VALUE_TYPE);
    }

    /**
     * Returns the array of column types from the table.
     * 
     * @return the array of column types from the table
     */
    public String[] getColumnTypes() {
    	return this.getValues(COLUMN_NAME_COLUMN_TYPE);
    }
    
    protected String[] getValues(final String colName) {
        final int colIndex = this.getColumnIndex(colName);
        if (colIndex == -1) {
        	return null;
        }
        final int num = this.mColumnInfoList.size();
        String[] items = new String[num];
        for (int ii = 0; ii < num; ii++) {
            items[ii] = (String) this.getValueAt(ii, colIndex);
        }
    	return items;
    }

    /**
     * Returns the column index of table.
     *
     * @param identifier
     * @return If the column with given identifier exists, returns the index.
     */
    public int getColumnIndex(final String identifier) {
        return this.mTable.getColumnModel().getColumnIndex(identifier);
    }

    /**
     * A cell editor class to display items in a combo box for various data
     * type.
     */
    protected class ColumnTypeCellEditor extends DefaultCellEditor {

        /**
         * SerialVersionUID
         */
        private static final long serialVersionUID = 1310814500603128330L;

        /**
         * Builds the cell editor for a combo box.
         *
         * @param comboBox
         */
        public ColumnTypeCellEditor(JComboBox comboBox) {
            super(comboBox);
        }

        /**
         * Returns the combo box component.
         */
        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {

        	String[] items = getColumnTypeEditorComboBoxItems(row);

            // set up new combo box items
            JComboBox cb = (JComboBox) super.getTableCellEditorComponent(table,
                    value, isSelected, row, column);
            
            setColumnTypeEditorComboBoxItems(cb, items, value);
            
            return cb;
        }
    }

    /**
     * A cell renderer class to display items in a combo box for various data
     * type.
     */
    protected class DataColumnCellRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 4333550774377195466L;
        
        private SGComboBox mComboBox = null;
        
        /**
         * Builds the cell editor for a combo box.
         *
         * @param comboBox
         */
        public DataColumnCellRenderer(JComboBox cb) {
            super();
            this.mComboBox = (SGComboBox) cb;
        }

        /**
         * Returns the combo box component.
         */
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            
            // set value
            this.mComboBox.removeAllItems();
            this.mComboBox.addItem(value);
            
            return this.mComboBox;
        }
    }

    
    /**
     * Set visible or invisible this dialog.
     * @param b
     *         true to set visible
     */
    public void setVisible(boolean b) {
        if (!b) {
            // when this dialog is hidden, clean up the combo box
            if (this.mColumnTypeEditorComboBox != null) {
                this.mColumnTypeEditorComboBox.removeAllItems();
            }
            if (this.mColumnTypeRendererComboBox != null) {
                this.mColumnTypeRendererComboBox.removeAllItems();
            }
        }
        super.setVisible(b);
    }

    public SGDataColumnInfoSet getDataColumnInfoSet() {
        return new SGDataColumnInfoSet(this.getDataColumnInfoArray());
    }
    
    /**
     * Returns the number of data column information.
     * 
     * @return the number of data column information
     */
    public int getDataColumnNum() {
    	return this.mColumnInfoList.size();
    }

    /**
     * Returns an array of data column information.
     * 
     * @return an array of data column information
     */
    public SGDataColumnInfo[] getDataColumnInfoArray() {
        SGDataColumnInfo[] cols = new SGDataColumnInfo[this.mColumnInfoList.size()];
        for (int ii = 0; ii < this.mColumnInfoList.size(); ii++) {
        	cols[ii] = (SGDataColumnInfo) this.mColumnInfoList.get(ii).clone();
        }
        return cols;
    }
    
    /**
     * Searches and returns the data column information of given index.
     * 
     * @param index
     *           the index
     * @return the data column information
     */
    protected SGDataColumnInfo findDataColumnInfo(final int index) {
    	if (index < 0 || index >= this.mColumnInfoList.size()) {
    		throw new IllegalArgumentException("Index out of bounds: " + index);
    	}
        SGDataColumnInfo info = (SGDataColumnInfo) this.mColumnInfoList.get(index);
		return info;
    }
    
	/**
	 * Searches and returns the data column information of given name.
	 * 
	 * @param name
	 *           the name of data column information
	 * @return the data column information if it is found
	 */
	protected SGDataColumnInfo findDataColumnInfo(final String name) {
		SGDataColumnInfo info = null;
		for (int ii = 0; ii < this.mColumnInfoList.size(); ii++) {
			SGDataColumnInfo col = (SGDataColumnInfo) this.mColumnInfoList.get(ii);
			if (col.getName().equals(name)) {
				info = col;
				break;
			}
		}
		return info;
	}

	protected int findDataColumnInfoIndex(final String name) {
		int index = - 1;
		for (int ii = 0; ii < this.mColumnInfoList.size(); ii++) {
			SGDataColumnInfo col = (SGDataColumnInfo) this.mColumnInfoList.get(ii);
			if (col.getName().equals(name)) {
				index = ii;
				break;
			}
		}
		return index;
	}

	public void setColumnType(final int rowIndex, final String columnType) {
		final int colIndex = this.getColumnIndex(COLUMN_NAME_COLUMN_TYPE);
		
		// set to table
		this.setValueAt(columnType, rowIndex, colIndex);

		// set to the data column information
		SGDataColumnInfo info = this.findDataColumnInfo(rowIndex);
		info.setColumnType(columnType);
	}

    /**
     * Scroll the vertical scroll bar.
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        final int amount = e.getScrollAmount() * e.getWheelRotation();
        JScrollBar vertBar = this.mTableScrollPane.getVerticalScrollBar();
        final int value = vertBar.getValue() + amount * 6;
        vertBar.setValue(value);
    }
    
    protected void clearSelectedColumns() {
        // clear selected data column type
        final int rowNum = this.getRowCount();
        for (int ii = 0; ii < rowNum; ii++) {
        	this.setColumnType(ii, "");
        }
        
        // clear combo boxes
        this.mColumnTypeEditorComboBox.setSelectedItem("");
        this.mColumnTypeRendererComboBox.setSelectedItem("");
        
        this.updateComponentsEnabled();
    }
    
	protected void restoreSelectedColumns() {
		for (int ii = 0; ii < this.mInitColumnInfoArray.length; ii++) {
			this.setColumnType(ii, this.mInitColumnInfoArray[ii].getColumnType());
		}

		// set the value to combo_boxes
		final int editRow = this.mTable.getEditingRow();
		if (editRow != -1) {
			final int colIndexColumnType = this
					.getColumnIndex(COLUMN_NAME_COLUMN_TYPE);
			Object editingItem = this.getValueAt(editRow, colIndexColumnType);
			this.mColumnTypeEditorComboBox.setSelectedItem(editingItem);
			this.mColumnTypeRendererComboBox.setSelectedItem(editingItem);
		}

		this.updateComponentsEnabled();
		this.clearMessage();
	}

    /**
     * Called when an action event is generated.
     * 
     * @param e
     *            an action event
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(this.mClearButton)) {
        	this.clearSelectedColumns();
        } else if (source.equals(this.mRestoreButton)) {
        	this.restoreSelectedColumns();
        } else if (source.equals(this.mComplementButton)) {
            final int colIndexColumnType = this
					.getColumnIndex(COLUMN_NAME_COLUMN_TYPE);
            final int num = this.mColumnInfoList.size();
			String[] colType = new String[num];
			for (int ii = 0; ii < num; ii++) {
				Object obj = this.getValueAt(ii, colIndexColumnType);
				if (obj == null) {
					colType[ii] = "";
				} else {
					colType[ii] = obj.toString();
				}
			}
			String[] colTypeComp = SGDataUtility.getComplementedColumnType(
					this.mDataInfoMap, colType, this.getColumnInfoList());
			for (int ii = 0; ii < num; ii++) {
				this.setColumnType(ii, colTypeComp[ii]);
			}
			
            this.updateComponentsEnabled();
        }
    }

    /**
     * Returns the button to restore all data column types.
     * 
     * @return the button to restore all data column types
     */
    public SGButton getRestoreButton() {
        return mRestoreButton;
    }

    /**
     * Returns the button to clear all data column types.
     * 
     * @return the button to clear all data column types
     */
    public SGButton getClearButton() {
        return mClearButton;
    }
    
    /**
     * Returns the button to complement the column types.
     * 
     * @return the button to complement the column types
     */
    public SGButton getComplementButton() {
    	return this.mComplementButton;
    }
    
    /**
     * Called when the table is changed.
     * 
     * @param evt
     *            an event that the property changes
     */
    public void propertyChange(PropertyChangeEvent evt) {
        Object source = evt.getSource();
        if (source.equals(this.getTable())) {
            String name = evt.getPropertyName();
            if ("tableCellEditor".equals(name)) {
                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {
                    	updateDataColumnInfo();
                    }
                });
            }
        }
    }
    
    /**
     * Updates the column information.
     * 
     */
    protected void updateDataColumnInfo() {
    	final int rowIndex = this.mTable.getSelectedRow();
    	if (rowIndex != -1) {
    		this.syncDataColumnInfoWithTable(rowIndex);
    	}
    }

    /**
     * Synchronizes the column information of given index with values in the table.
     * 
     * @param rowIndex
     *           the row index
     */
    protected void syncDataColumnInfoWithTable(final int rowIndex) {
    	final int colIndex = this.getColumnIndex(COLUMN_NAME_COLUMN_TYPE);
		SGDataColumnInfo info = this.mColumnInfoList.get(rowIndex);
		String columnType = (String) this.getValueAt(rowIndex, colIndex);
		info.setColumnType(columnType);
    }

    /**
     * Synchronizes all column information with values in the table.
     * 
     */
    protected void syncAllDataColumnInfoWithTable() {
    	for (int ii = 0; ii < this.mColumnInfoList.size(); ii++) {
    		this.syncDataColumnInfoWithTable(ii);
    	}
    }

    /**
     * Sets enabled or disabled the components with selected variables.
     *
     */
    private void updateComponentsEnabled() {
        // set enabled or disabled the complement button
    	SGDataColumnInfo[] colInfoArray = this.getDataColumnInfoArray();
    	String[] curColType = new String[colInfoArray.length];
    	for (int ii = 0; ii < curColType.length; ii++) {
    		curColType[ii] = colInfoArray[ii].getColumnType();
    	}
        final boolean b = SGDataUtility.isComplementedButtonEnabled(
        		this.mDataInfoMap, curColType, this.mColumnInfoList);
        this.getComplementButton().setEnabled(b);
    }

    /**
     * Creates and returns a table object.
     * 
     * @return a table object
     */
    protected abstract SGTable createTableInstance();
    
    /**
     * The owner of popup dialogs.
     */
    protected Window mDialogOwner = null;
    
    /**
     * Sets the owner of popup dialogs.
     * 
     * @param owner
     *           the owner of popup dialogs
     */
    public void setPopupDialogOwner(Window owner) {
        this.mDialogOwner = owner;
    }

    /**
     * Cell editor class for a column with a button.
     *
     */
    protected static class ButtonColumn extends AbstractCellEditor implements
            TableCellRenderer, TableCellEditor {

        private static final long serialVersionUID = 1089174278086303154L;
        private static final String BUTTON_TEXT = " ";
        private final JButton renderButton;
        private final JButton editorButton;

        public ButtonColumn() {
            super();
            renderButton = new SGButton(BUTTON_TEXT);
            editorButton = new SGButton(BUTTON_TEXT);
            editorButton.setFocusPainted(false);
            editorButton.setRolloverEnabled(false);
        }

        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            return renderButton;
        }

        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {
            return editorButton;
        }

        public Object getCellEditorValue() {
            return BUTTON_TEXT;
        }
    }

    /**
     * Returns the number of the rows of the table.
     * 
     * @return the number of the rows of the table
     */
    public int getRowCount() {
    	return this.mTable.getRowCount();
    }
    
    protected void setValueAt(final Object value, final int dataColumnIndex, final int tableColumnIndex) {
    	this.mTable.setValueAt(value, dataColumnIndex, tableColumnIndex);
    }
    
    protected Object getValueAt(final int dataColumnIndex, final int tableColumnIndex) {
    	return this.mTable.getValueAt(dataColumnIndex, tableColumnIndex);
    }

	void setColumnTypeEditorComboBoxItems(JComboBox cb, String[] items, Object selectedItem) {
		cb.removeAllItems();
		for (int ii = 0; ii < items.length; ii++) {
			this.mColumnTypeEditorComboBox.addItem(items[ii]);
		}
		this.mColumnTypeEditorComboBox.setSelectedItem(selectedItem);
	}
	
	String[] getColumnTypeEditorComboBoxItems(final int row) {
        // get the column info for a given row index
        SGDataColumnInfo cInfo = (SGDataColumnInfo) getColumnInfoList().get(row);

        Map<String, Object> infoMapUpd = new HashMap<String, Object>(mDataInfoMap);

        // the current row index
        infoMapUpd.put(SGIDataInformationKeyConstants.KEY_CURRENT_ROW_INDEX, Integer.valueOf(row));

        // get the combo box items
        String valueType = cInfo.getValueType();
        Map<String, Object> infoMap = SGDataUtility.updateInfoMap(mDataType, 
        		getDataColumnInfoArray(), infoMapUpd);
        
        // get combo box items
        String[] items = SGDataUtility.getColumnTypeCandidates(mDataType, 
                infoMap, valueType);
        return items;
	}

	protected void setMessage(String text) {
		this.mMessageLabel.setText(text);
	}
	
	protected void clearMessage() {
		this.mMessageLabel.setText("");
	}
}

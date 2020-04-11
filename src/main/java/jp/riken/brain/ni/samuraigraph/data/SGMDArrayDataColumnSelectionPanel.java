package jp.riken.brain.ni.samuraigraph.data;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import jp.riken.brain.ni.samuraigraph.base.SGComboBox;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGTable;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDefaultColumnTypeUtility.DefaultMDColumnTypeResult;


/**
 * A panel to select the data columns for multidimensional data.
 *
 */
public class SGMDArrayDataColumnSelectionPanel extends SGDataColumnSelectionPanel {

	private static final long serialVersionUID = -227336333681156520L;

    /**
     * Name of the column for the name of each data column.
     */
    public static final String COLUMN_NAME_NAME = "Name";

    /**
     * Name of the column for the generic dimension of each data column.
     */
    public static final String COLUMN_NAME_GENERIC_DIMENSION = "Dimension";

    /**
     * Name of the column for the time dimension of each data column.
     */
    public static final String COLUMN_NAME_TIME_DIMENSION = "Animation Frame";

    /**
     * Name of the column for picked up dimension of each data column.
     */
    public static final String COLUMN_NAME_PICKED_UP_DIMENSION = "PickUp";

	/**
	 * The array of column name.
	 */
	public static final String[] COLUMN_NAME_ARRAY_SXY = { COLUMN_NAME_NUMBER,
			COLUMN_NAME_NAME, COLUMN_NAME_VALUE_TYPE, COLUMN_NAME_GENERIC_DIMENSION,
			COLUMN_NAME_TIME_DIMENSION, COLUMN_NAME_PICKED_UP_DIMENSION, COLUMN_NAME_COLUMN_TYPE };

	private static final int[] PREFERRED_COLUMN_WIDTH_ARRAY_SXY = { 10, 80, 60, 50, 50, 50,
			120 };

	public static final String[] COLUMN_NAME_ARRAY_MULTI_DIMENSION = {
			COLUMN_NAME_NUMBER, COLUMN_NAME_NAME, COLUMN_NAME_VALUE_TYPE, COLUMN_NAME_TIME_DIMENSION,
			COLUMN_NAME_COLUMN_TYPE };

	private static final int[] PREFERRED_COLUMN_WIDTH_ARRAY_MULTI_DIMENSION = {
			10, 80, 60, 40, 140 };

	private static final String UNSELECTED_STRING = " ";

    /**
     * An array of acceptable data types.
     */
	private static final String[] DATA_TYPE_ARRAY = {
			SGDataTypeConstants.SXY_HDF5_DATA,
			SGDataTypeConstants.SXYZ_HDF5_DATA,
			SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA,
			SGDataTypeConstants.VXY_HDF5_DATA,
			SGDataTypeConstants.SXY_MATLAB_DATA,
			SGDataTypeConstants.SXYZ_MATLAB_DATA,
			SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA,
			SGDataTypeConstants.VXY_MATLAB_DATA,
			SGDataTypeConstants.SXY_VIRTUAL_MDARRAY_DATA,
			SGDataTypeConstants.SXYZ_VIRTUAL_MDARRAY_DATA,
			SGDataTypeConstants.SXY_MULTIPLE_VIRTUAL_MDARRAY_DATA,
			SGDataTypeConstants.VXY_VIRTUAL_MDARRAY_DATA };

    protected SGComboBox mGenericDimensionRendererComboBox = null;

    protected DimensionEditorComboBox mGenericDimensionEditorComboBox = null;

    protected SGComboBox mTimeDimensionRendererComboBox = null;

    protected DimensionEditorComboBox mTimeDimensionEditorComboBox = null;

    protected SGComboBox mPickUpDimensionRendererComboBox = null;

    protected DimensionEditorComboBox mPickUpDimensionEditorComboBox = null;

	/**
	 * The default constructor.
	 *
	 */
	public SGMDArrayDataColumnSelectionPanel() {
		super();
		this.initProperty();
	}

    protected DimensionEditorComboBox createDimensionEditorComboBoxInstance() {
    	DimensionEditorComboBox cb = new DimensionEditorComboBox();
        cb.setBorder(BorderFactory.createEmptyBorder());
        return cb;
    }

    private static final int MAXIMUM_ROW_CNT = 12;

	private void initProperty() {
        this.mGenericDimensionEditorComboBox = this.createDimensionEditorComboBoxInstance();
        this.mGenericDimensionEditorComboBox.setMaximumRowCount(MAXIMUM_ROW_CNT);

        this.mGenericDimensionRendererComboBox = this.createComboBoxInstance();
        this.mGenericDimensionRendererComboBox.setMaximumRowCount(MAXIMUM_ROW_CNT);

        this.mTimeDimensionEditorComboBox = this.createDimensionEditorComboBoxInstance();
        this.mTimeDimensionEditorComboBox.setMaximumRowCount(MAXIMUM_ROW_CNT);

        this.mTimeDimensionRendererComboBox = this.createComboBoxInstance();
        this.mTimeDimensionRendererComboBox.setMaximumRowCount(MAXIMUM_ROW_CNT);

        this.mPickUpDimensionEditorComboBox = this.createDimensionEditorComboBoxInstance();
        this.mPickUpDimensionEditorComboBox.setMaximumRowCount(MAXIMUM_ROW_CNT);

        this.mPickUpDimensionRendererComboBox = this.createComboBoxInstance();
        this.mPickUpDimensionRendererComboBox.setMaximumRowCount(MAXIMUM_ROW_CNT);

        this.mTimeDimensionEditorComboBox.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		/*
		Object source = e.getSource();
		if (this.mTimeDimensionEditorComboBox.equals(source)) {
			// resets the origin of time dimension
			Object item = this.mTimeDimensionEditorComboBox.getSelectedItem();
			String str = item.toString();
			Integer timeDimension = SGUtilityText.getInteger(str);
			if (timeDimension != null) {
				final int rowIndex = this.mTable.getSelectedRow();
				this.setOrigin(rowIndex, timeDimension, 0);
			}
		}
		*/
	}

    protected String[] getColumnNameArray() {
    	if (SGDataUtility.isSXYTypeData(this.mDataType)) {
            return COLUMN_NAME_ARRAY_SXY;
    	} else {
            return COLUMN_NAME_ARRAY_MULTI_DIMENSION;
    	}
    }

    protected int[] getPreferredColumnWidthArray() {
    	if (SGDataUtility.isSXYTypeData(this.mDataType)) {
            return PREFERRED_COLUMN_WIDTH_ARRAY_SXY;
    	} else {
            return PREFERRED_COLUMN_WIDTH_ARRAY_MULTI_DIMENSION;
    	}
    }

    protected String[] getDataTypeArray() {
        return DATA_TYPE_ARRAY;
    }

    protected DataColumnTableModel createTableModel() {
		return new MDArrayDataColumnTableModel();
    }

    /**
     * Creates and returns a table object.
     *
     * @return a table object
     */
    protected SGTable createTableInstance() {
    	return new SGTable(){
			private static final long serialVersionUID = -2048668978677071802L;
			
			@Override
			public String getToolTipText(MouseEvent e){
				String str = super.getToolTipText(e);
				if (str == null) {
					return null;
				}
                if (UNSELECTED_STRING.equals(str)) {
					return null;
				}
                return str;
            }
        };
    }

    protected class MDArrayDataColumnTableModel extends DataColumnTableModel {

		private static final long serialVersionUID = -1797402127155111111L;

		public MDArrayDataColumnTableModel() {
            super();
        }

        protected void addData(int index, SGDataColumnInfo data) {
            SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) data;
            String cType = col.getColumnType();
            if (cType == null) {
                cType = "";
            }
            final String timeIndexStr = getTimeIndexString(col);

            // set the variable to the cell of a button
            Object[] array = { Integer.toString(index + 1), data.getTitle(),
                    col.getValueType(), timeIndexStr, cType };

            super.addRow(array);
        }
    }

    protected class SXYMDArrayDataColumnTableModel extends DataColumnTableModel {

		private static final long serialVersionUID = 2023283487123490354L;

		public SXYMDArrayDataColumnTableModel() {
            super();
        }

        protected void addData(int index, SGDataColumnInfo data) {
            SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) data;
            String cType = col.getColumnType();
            if (cType == null) {
                cType = "";
            }
            final Integer dimIndex = col.getGenericDimensionIndex();
            final String timeIndexStr = getTimeIndexString(col);
            Integer pickUpIndex = col.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
            String pickUpIndexStr = null;
            if (pickUpIndex == null) {
            	pickUpIndexStr = UNSELECTED_STRING;
            } else {
            	if (SGDataUtility.isValidPickUpValue(pickUpIndex)) {
                	pickUpIndexStr = pickUpIndex.toString();
            	} else {
            		pickUpIndexStr = UNSELECTED_STRING;
            	}
            }

            // set the variable to the cell of a button
            Object[] array = { Integer.toString(index + 1), data.getTitle(),
                    col.getValueType(), dimIndex, timeIndexStr, pickUpIndexStr, cType };

            super.addRow(array);
        }
    }

    static String getTimeIndexString(SGMDArrayDataColumnInfo col) {
        Integer timeIndex = col.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
        String timeIndexStr = null;
        if (timeIndex == null) {
        	timeIndexStr = UNSELECTED_STRING;
        } else {
        	if (SGDataUtility.isValidTimeValue(timeIndex)) {
            	timeIndexStr = timeIndex.toString();
        	} else {
        		timeIndexStr = UNSELECTED_STRING;
        	}
        }
        return timeIndexStr;
    }

    /**
     * Checks selected items.
     *
     * @return true if selected items are valid for the data type
     */
	@Override
	public boolean checkSelectedItems() {
        List<SGDataColumnInfo> colInfoList = this.getColumnInfoList();

        final int colIndexName = this.getColumnIndex(COLUMN_NAME_NAME);
        final int colIndexColumnType = this.getColumnIndex(COLUMN_NAME_COLUMN_TYPE);

        SGMDArrayDataColumnInfo[] items = new SGMDArrayDataColumnInfo[colInfoList.size()];
        for (int ii = 0; ii < items.length; ii++) {
            String name = (String) this.getValueAt(ii, colIndexName);
            if (name == null) {
                name = "";
            }
            SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) colInfoList.get(ii);
            items[ii] = new SGMDArrayDataColumnInfo(col, name, col.getValueType());
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

        // checks overlapping of indices
        for (int ii = 0; ii < colInfoList.size(); ii++) {
        	SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfoList.get(ii);
        	String columnType = mdInfo.getColumnType();
            List<String> keyList = new ArrayList<String>();
        	keyList.add(SGIMDArrayConstants.KEY_TIME_DIMENSION);
            if (SGDataUtility.isSXYTypeData(this.mDataType)) {
            	keyList.add(SGIMDArrayConstants.KEY_GENERIC_DIMENSION);
            	keyList.add(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
            } else if (SGDataUtility.isSXYZTypeData(this.mDataType)) {
            	if (Z_VALUE.equals(columnType)) {
                	keyList.add(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION);
                	keyList.add(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION);
            	} else {
                	keyList.add(SGIMDArrayConstants.KEY_GENERIC_DIMENSION);
            	}
            } else if (SGDataUtility.isVXYTypeData(this.mDataType)) {
            	if (X_COMPONENT.equals(columnType) || Y_COMPONENT.equals(columnType)
            			|| MAGNITUDE.equals(columnType) || ANGLE.equals(columnType)) {
                	keyList.add(SGIMDArrayConstants.KEY_VXY_X_DIMENSION);
                	keyList.add(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION);
            	} else {
                	keyList.add(SGIMDArrayConstants.KEY_GENERIC_DIMENSION);
            	}
            }
        	List<Integer> indexList = new ArrayList<Integer>();
        	for (String key : keyList) {
        		Integer index = mdInfo.getDimensionIndex(key);
        		if (index != null && index != -1) {
        			indexList.add(index);
        		}
        	}
        	Set<Integer> indexSet = new HashSet<Integer>();
        	indexSet.addAll(indexList);
        	if (indexSet.size() != indexList.size()) {
            	this.setMessage(SGDataUtility.MSG_UNIQUE_DIMENSIONS);
        		return false;
        	}
        }

        // checks the length of time dimension
        int timeLen = -1;
        for (int ii = 0; ii < colInfoList.size(); ii++) {
        	SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfoList.get(ii);
        	String columnType = mdInfo.getColumnType();
        	if ("".equals(columnType)) {
        		continue;
        	}
        	Integer index = mdInfo.getTimeDimensionIndex();
        	if (index == null || index == -1) {
        		continue;
        	}
        	int[] dims = mdInfo.getDimensions();
        	final int len = dims[index];
        	if (timeLen == -1) {
        		timeLen = len;
        	} else {
        		if (timeLen != len) {
                	this.setMessage(SGDataUtility.MSG_DIMENSIONS_SAME_LENGTH_ANIMATION_FRAME);
        			return false;
        		}
        	}
        }

        // checks for pick up
        if (SGDataUtility.isSXYTypeData(this.mDataType)) {
        	List<SGMDArrayDataColumnInfo> xList = new ArrayList<SGMDArrayDataColumnInfo>();;
        	List<SGMDArrayDataColumnInfo> yList = new ArrayList<SGMDArrayDataColumnInfo>();;
            for (int ii = 0; ii < colInfoList.size(); ii++) {
            	SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfoList.get(ii);
            	String columnType = mdInfo.getColumnType();
            	if (X_VALUE.equals(columnType)) {
            		xList.add(mdInfo);
            	} else if (Y_VALUE.equals(columnType)) {
            		yList.add(mdInfo);
            	}
            }
        	List<SGMDArrayDataColumnInfo> pickUpList = new ArrayList<SGMDArrayDataColumnInfo>();
            for (int ii = 0; ii < colInfoList.size(); ii++) {
            	SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfoList.get(ii);
            	Integer pickUpDim = mdInfo.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
            	if (pickUpDim != null && pickUpDim != -1) {
            		pickUpList.add(mdInfo);
            	}
            }
            if (pickUpList.size() > 0) {
            	if (xList.size() > 1 || yList.size() > 1) {
                	this.setMessage(SGDataUtility.MSG_DIMENSION_AND_COLUMN_TYPE_PICK_UP);
        			return false;
            	}
            }
        }

        // clears the message
        this.clearMessage();

        return true;
	}

    @Override
    protected boolean setDefaultColumnType() {
        // If default column types are to be shown, get them.
        // If null value is returned, return false.
		SGDefaultColumnTypeUtility.DefaultMDColumnTypeResult result = (DefaultMDColumnTypeResult) SGDefaultColumnTypeUtility
				.getDefaultColumnTypes(this.mDataType,
						this.getColumnInfoList(), this.mDataInfoMap);
        String[] columnTypes = result.getDefaultColumnTypes();
        int[] indices = result.getIndices();
        List<Map<String, Integer>> allIndices = result.getAllIndices();
        for (int ii = 0; ii < columnTypes.length; ii++) {
        	this.setColumnType(ii, columnTypes[ii]);
        }
    	if (SGDataUtility.isSXYTypeData(this.mDataType)) {
            for (int ii = 0; ii < columnTypes.length; ii++) {
            	this.setGenericDimensionIndex(ii, indices[ii]);
            }
    	}
        for (int ii = 0; ii < this.mColumnInfoList.size(); ii++) {
        	SGMDArrayDataColumnInfo colInfo = (SGMDArrayDataColumnInfo) this.mColumnInfoList.get(ii);
        	colInfo.putAllDimensionIndices(allIndices.get(ii));
        }
        return result.isSucceeded();
    }

    @Override
    public boolean setData(String dataType, SGDataColumnInfoSet colInfoSet,
    		Map<String, Object> infoMap, final boolean showDefault) {
    	if (super.setData(dataType, colInfoSet, infoMap, showDefault) == false) {
    		return false;
    	}

        // set up the combo_box
        final int timeColIndex = this.getColumnIndex(COLUMN_NAME_TIME_DIMENSION);
        TableColumn timeCol = this.mTable.getColumnModel().getColumn(
                timeColIndex);
        timeCol.setCellEditor(new TimeCellEditor(this.mTimeDimensionEditorComboBox));
        timeCol.setCellRenderer(new DataColumnCellRenderer(this.mTimeDimensionRendererComboBox));

    	if (SGDataUtility.isSXYTypeData(this.mDataType)) {
            // set up the combo_box
            final int dimensionColIndex = this.getColumnIndex(COLUMN_NAME_GENERIC_DIMENSION);
            TableColumn dimensionCol = this.mTable.getColumnModel().getColumn(
                    dimensionColIndex);
            dimensionCol.setCellEditor(new GenericDimensionCellEditor(this.mGenericDimensionEditorComboBox));
            dimensionCol.setCellRenderer(new DataColumnCellRenderer(this.mGenericDimensionRendererComboBox));

            final int pickUpColIndex = this.getColumnIndex(COLUMN_NAME_PICKED_UP_DIMENSION);
            TableColumn pickUpCol = this.mTable.getColumnModel().getColumn(
            		pickUpColIndex);
            pickUpCol.setCellEditor(new PickUpDimensionCellEditor(this.mPickUpDimensionEditorComboBox));
            pickUpCol.setCellRenderer(new DataColumnCellRenderer(this.mPickUpDimensionRendererComboBox));
    	}

    	// saves the initial state
        this.mInitColumnInfoArray = colInfoSet.getDataColumnInfoArray();

    	return true;
    }

    /**
     * Set visible or invisible this dialog.
     *
     * @param b
     *         true to set visible
     */
    @Override
    public void setVisible(boolean b) {
        if (!b) {
            // when this dialog is hidden, clean up the combo box
            if (this.mGenericDimensionEditorComboBox != null) {
                this.mGenericDimensionEditorComboBox.removeAllItems();
            }
            if (this.mGenericDimensionRendererComboBox != null) {
                this.mGenericDimensionRendererComboBox.removeAllItems();
            }
            if (this.mTimeDimensionEditorComboBox != null) {
                this.mTimeDimensionEditorComboBox.removeAllItems();
            }
            if (this.mTimeDimensionRendererComboBox != null) {
                this.mTimeDimensionRendererComboBox.removeAllItems();
            }
            if (this.mPickUpDimensionEditorComboBox != null) {
            	this.mPickUpDimensionEditorComboBox.removeAllItems();
            }
            if (this.mPickUpDimensionRendererComboBox != null) {
            	this.mPickUpDimensionRendererComboBox.removeAllItems();
            }
        }
        super.setVisible(b);
    }

    @Override
    protected List<String> getEditableColumnNameList() {
    	List<String> list = super.getEditableColumnNameList();
    	list.add(COLUMN_NAME_TIME_DIMENSION);
    	if (SGDataUtility.isSXYTypeData(this.mDataType)) {
        	list.add(COLUMN_NAME_GENERIC_DIMENSION);
        	list.add(COLUMN_NAME_PICKED_UP_DIMENSION);
    	}
    	return list;
    }

    protected class GenericDimensionCellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = -7057361850365123546L;

        public GenericDimensionCellEditor(JComboBox comboBox) {
            super(comboBox);
        }

        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {

            // get the column info for a given row index
            SGMDArrayDataColumnInfo cInfo = (SGMDArrayDataColumnInfo) getColumnInfoList().get(row);
            int[] dims = cInfo.getDimensions();

            // set up new combo box items
            DimensionEditorComboBox cb = (DimensionEditorComboBox) super.getTableCellEditorComponent(
            		table, value, isSelected, row, column);
            cb.removeAllItems();
            for (int ii = 0; ii < dims.length; ii++) {
                cb.addItem(Integer.toString(ii));
            }

            // set value
            cb.setSelectedItem(value);

            // updates the combo box
            updateGenericDimensionComboBox(cInfo);

            return cb;
        }
    }

    private void updateGenericDimensionComboBox(SGMDArrayDataColumnInfo cInfo) {
        // sets deprecated indices
        List<String> nameList = new ArrayList<String>();
        nameList.add(SGIMDArrayConstants.KEY_TIME_DIMENSION);
        nameList.add(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
        List<String> deprecatedList = getDeprecatedList(cInfo, nameList);
        this.mGenericDimensionEditorComboBox.setDeprecatedList(deprecatedList);
    }

    protected class TimeCellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = -8323763303354706069L;

		public TimeCellEditor(JComboBox comboBox) {
            super(comboBox);
        }

        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {

            // get the column info for a given row index
            SGMDArrayDataColumnInfo cInfo = (SGMDArrayDataColumnInfo) getColumnInfoList().get(row);
            int[] dims = cInfo.getDimensions();

            // set up new combo box items
            DimensionEditorComboBox cb = (DimensionEditorComboBox) super.getTableCellEditorComponent(
            		table, value, isSelected, row, column);
            cb.removeAllItems();
            cb.addItem(UNSELECTED_STRING);
            for (int ii = 0; ii < dims.length; ii++) {
                cb.addItem(Integer.toString(ii));
            }

            // set value
            cb.setSelectedItem(value);

            // updates the combo box
            updateTimeDimensionComboBox(cInfo);

            return cb;
        }
    }

    private void updateTimeDimensionComboBox(SGMDArrayDataColumnInfo cInfo) {
        // sets deprecated indices
        List<String> nameList = new ArrayList<String>();
        nameList.add(SGIMDArrayConstants.KEY_GENERIC_DIMENSION);
        String dataType = getDataType();
        String columnType = cInfo.getColumnType();
        if (SGDataUtility.isSXYTypeData(dataType)) {
            nameList.add(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
        } else if (SGDataUtility.isSXYZTypeData(dataType)) {
        	if (Z_VALUE.equals(columnType)) {
                nameList.add(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION);
                nameList.add(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION);
        	}
        } else if (SGDataUtility.isVXYTypeData(dataType)) {
        	if (X_COMPONENT.equals(columnType) || Y_COMPONENT.equals(columnType)
        			|| MAGNITUDE.equals(columnType) || ANGLE.equals(columnType)) {
                nameList.add(SGIMDArrayConstants.KEY_VXY_X_DIMENSION);
                nameList.add(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION);
        	}
        }
        List<String> deprecatedList = getDeprecatedList(cInfo, nameList);
        this.mTimeDimensionEditorComboBox.setDeprecatedList(deprecatedList);
    }

    protected class PickUpDimensionCellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = -8136894336623617659L;

		public PickUpDimensionCellEditor(JComboBox comboBox) {
            super(comboBox);
        }

        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {

            // get the column info for a given row index
            SGMDArrayDataColumnInfo cInfo = (SGMDArrayDataColumnInfo) getColumnInfoList().get(row);
            int[] dims = cInfo.getDimensions();

            // set up new combo box items
            DimensionEditorComboBox cb = (DimensionEditorComboBox) super.getTableCellEditorComponent(
            		table, value, isSelected, row, column);
            cb.removeAllItems();
            cb.addItem(UNSELECTED_STRING);
            for (int ii = 0; ii < dims.length; ii++) {
                cb.addItem(Integer.toString(ii));
            }

            // set value
            cb.setSelectedItem(value);

            // updates the combo box
            updatePickUpDimensionComboBox(cInfo);

            return cb;
        }
    }

    private void updatePickUpDimensionComboBox(SGMDArrayDataColumnInfo cInfo) {
        // sets deprecated indices
        List<String> nameList = new ArrayList<String>();
        nameList.add(SGIMDArrayConstants.KEY_TIME_DIMENSION);
        nameList.add(SGIMDArrayConstants.KEY_GENERIC_DIMENSION);
        List<String> deprecatedList = getDeprecatedList(cInfo, nameList);
        this.mPickUpDimensionEditorComboBox.setDeprecatedList(deprecatedList);
    }

    @Override
    protected void restoreSelectedColumns() {
    	super.restoreSelectedColumns();
        for (int ii = 0; ii < this.mInitColumnInfoArray.length; ii++) {
        	SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) this.mInitColumnInfoArray[ii];
            this.setTimeDimensionIndex(ii, mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION));
            if (SGDataUtility.isSXYTypeData(this.mDataType)) {
                this.setGenericDimensionIndex(ii, mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION));
                this.setPickUpDimensionIndex(ii, mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION));
            } else if (SGDataUtility.isSXYZTypeData(this.mDataType)) {
        		this.setDimensionIndex(ii, SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, 
        				mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION));
        		this.setDimensionIndex(ii, SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, 
        				mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION));
            } else if (SGDataUtility.isVXYTypeData(this.mDataType)) {
        		this.setDimensionIndex(ii, SGIMDArrayConstants.KEY_VXY_X_DIMENSION, 
        				mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_VXY_X_DIMENSION));
        		this.setDimensionIndex(ii, SGIMDArrayConstants.KEY_VXY_Y_DIMENSION, 
        				mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION));
            }
        }
    }

	@Override
	protected void initTableModel(String dataType) {
		DataColumnTableModel model = null;
		if (SGDataUtility.isSXYTypeData(dataType)) {
			model = new SXYMDArrayDataColumnTableModel();
		} else {
			model = new MDArrayDataColumnTableModel();
		}
		this.initTableModel(model);
	}

    protected void clearSelectedColumns() {
        final int rowNum = this.getRowCount();
        for (int ii = 0; ii < rowNum; ii++) {
            this.setTimeDimensionIndex(ii, -1);
        }
    	if (SGDataUtility.isSXYTypeData(this.mDataType)) {
            // clear selected data column type
            for (int ii = 0; ii < rowNum; ii++) {
                this.setGenericDimensionIndex(ii, 0);
                this.setPickUpDimensionIndex(ii, -1);
            }
    	}

        this.mGenericDimensionEditorComboBox.setSelectedItem(Integer.valueOf(0));
        this.mGenericDimensionRendererComboBox.setSelectedItem(Integer.valueOf(0));

        this.mTimeDimensionEditorComboBox.setSelectedItem(UNSELECTED_STRING);
        this.mTimeDimensionRendererComboBox.setSelectedItem(UNSELECTED_STRING);

        this.mPickUpDimensionEditorComboBox.setSelectedItem(UNSELECTED_STRING);
        this.mPickUpDimensionRendererComboBox.setSelectedItem(UNSELECTED_STRING);

    	super.clearSelectedColumns();
    }

    /**
     * Synchronizes the column information of given index with values in the table.
     * 
     * @param rowIndex
     *           the row index
     */
    @Override
    protected void syncDataColumnInfoWithTable(final int rowIndex) {
    	super.syncDataColumnInfoWithTable(rowIndex);
		SGMDArrayDataColumnInfo info = (SGMDArrayDataColumnInfo) this.mColumnInfoList.get(rowIndex);

		// time
		Object objTime = this.getValueAt(rowIndex, this.getColumnIndex(COLUMN_NAME_TIME_DIMENSION));
		Integer time = SGUtilityText.getInteger(objTime.toString());
		if (time == null) {
			time = Integer.valueOf(-1);
		}
		info.setDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION, time);

    	if (SGDataUtility.isSXYTypeData(this.mDataType)) {
    		// generic
    		Object objDimension = this.getValueAt(rowIndex, this.getColumnIndex(COLUMN_NAME_GENERIC_DIMENSION));
    		Integer dimension = SGUtilityText.getInteger(objDimension.toString());
    		info.setGenericDimensionIndex(dimension);

    		// pick up
    		Object objPickUpDimension = this.getValueAt(rowIndex, this.getColumnIndex(COLUMN_NAME_PICKED_UP_DIMENSION));
    		Integer pickUpDimension = SGUtilityText.getInteger(objPickUpDimension.toString());
    		if (pickUpDimension == null) {
    			pickUpDimension = Integer.valueOf(-1);
    		}
    		info.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, pickUpDimension);
    	}
    }

	public void setGenericDimensionIndex(final int rowIndex, final Integer dimensionIndex) {
		if (dimensionIndex == null) {
			return;
		}
		
		// set to the data column information
		this.setDimensionIndex(rowIndex, SGIMDArrayConstants.KEY_GENERIC_DIMENSION, dimensionIndex);

		// set to table
		final int colIndex = this.getColumnIndex(COLUMN_NAME_GENERIC_DIMENSION);
		this.setValueAt(dimensionIndex, rowIndex, colIndex);
	}

	public void setPickUpDimensionIndex(final int rowIndex, final Integer dimensionIndex) {
		if (dimensionIndex == null) {
			return;
		}
		
		// set to the data column information
		this.setDimensionIndex(rowIndex, SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, dimensionIndex);

		// set to table
		final int colIndex = this.getColumnIndex(COLUMN_NAME_PICKED_UP_DIMENSION);
		String str;
		if (dimensionIndex == -1) {
			str = UNSELECTED_STRING;
		} else {
			str = Integer.toString(dimensionIndex);
		}
		this.setValueAt(str, rowIndex, colIndex);
	}

	public void setTimeDimensionIndex(final int rowIndex, final Integer dimensionIndex) {
		if (dimensionIndex == null) {
			return;
		}
		
		// set to the data column information
		this.setDimensionIndex(rowIndex, SGIMDArrayConstants.KEY_TIME_DIMENSION, dimensionIndex);

		// set to table
		final int colIndex = this.getColumnIndex(COLUMN_NAME_TIME_DIMENSION);
		String str;
		if (dimensionIndex == -1) {
			str = UNSELECTED_STRING;
		} else {
			str = Integer.toString(dimensionIndex);
		}
		this.setValueAt(str, rowIndex, colIndex);
	}

	public void setDimensionIndex(final int rowIndex, final String key, final Integer dimensionIndex) {
		if (dimensionIndex == null) {
			return;
		}
		SGMDArrayDataColumnInfo cInfo = (SGMDArrayDataColumnInfo) this.findDataColumnInfo(rowIndex);
		cInfo.setDimensionIndex(key, dimensionIndex);
	}

	// updates the items in combo boxes
	public void updateDimensionComboBox(final int rowIndex) {
		SGMDArrayDataColumnInfo cInfo = (SGMDArrayDataColumnInfo) this.findDataColumnInfo(rowIndex);
		this.updateTimeDimensionComboBox(cInfo);
		if (SGDataUtility.isSXYTypeData(this.mDataType)) {
			this.updateGenericDimensionComboBox(cInfo);
			this.updatePickUpDimensionComboBox(cInfo);
		}
	}

	public void setDimensionIndex(final String name, final String key, final int dimensionIndex) {
		final int rowIndex = this.findDataColumnInfoIndex(name);
		if (rowIndex == -1) {
			throw new IllegalArgumentException("Data column is not found: " + name);
		}
		this.setDimensionIndex(rowIndex, key, dimensionIndex);
	}

	public void setOrigin(final int rowIndex, final int dimensionIndex, final int origin) {
		SGMDArrayDataColumnInfo cInfo = (SGMDArrayDataColumnInfo) this.findDataColumnInfo(rowIndex);
		cInfo.setOrigin(dimensionIndex, origin);
	}

	public void setOrigin(final String name, final int dimensionIndex, final int origin) {
		final int rowIndex = this.findDataColumnInfoIndex(name);
		if (rowIndex == -1) {
			throw new IllegalArgumentException("Data column is not found: " + name);
		}
		this.setOrigin(rowIndex, dimensionIndex, origin);
	}

	public void clearDimensionIndex(final int rowIndex, final String key) {
		SGMDArrayDataColumnInfo info = (SGMDArrayDataColumnInfo) this.findDataColumnInfo(rowIndex);
		info.clearDimensionIndex(key);
	}

	protected class DimensionEditorComboBox extends SGComboBox {
		private static final long serialVersionUID = -1258556477585773902L;
		public DimensionEditorComboBox() {
			super();
			this.setRenderer(new DimensionListCellRenderer());
		}
		public void setDeprecatedList(List<String> list) {
			DimensionListCellRenderer renderer = (DimensionListCellRenderer) this.getRenderer();
			renderer.setDeprecatedList(list);
		}
	}

	protected class DimensionListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = -6316688554096959721L;
		private List<String> mDeprecatedList = new ArrayList<String>();
		public DimensionListCellRenderer() {
			super();
		}
		public void setDeprecatedList(List<String> list) {
			this.mDeprecatedList = new ArrayList<String>(list);
		}
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			DefaultListCellRenderer renderer = (DefaultListCellRenderer) super.getListCellRendererComponent(
					list, value, index, isSelected, cellHasFocus);
	        Color background;
	        Color foreground;
        	if (isSelected) {
	        	background = list.getSelectionBackground();
	        	foreground = list.getSelectionForeground();
        	} else {
	        	background = list.getBackground();
	        	foreground = list.getForeground();
        	}
	        if (this.mDeprecatedList.contains(value)) {
	        	foreground = Color.LIGHT_GRAY;
	        }
	        renderer.setBackground(background);
	        renderer.setForeground(foreground);
	        return renderer;
		}
	}

	private List<String> getDeprecatedList(SGMDArrayDataColumnInfo cInfo, List<String> nameList) {
        List<String> deprecatedList = new ArrayList<String>();
        for (String name : nameList) {
            Integer dim = cInfo.getDimensionIndex(name);
            if (SGDataUtility.isValidDimensionIndex(dim)) {
                deprecatedList.add(dim.toString());
            }
        }
        return deprecatedList;
	}

}

package jp.riken.brain.ni.samuraigraph.data;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;

import jp.riken.brain.ni.samuraigraph.base.SGButton;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

public abstract class SGDataSetupPanel extends JPanel implements
		SGIDataColumnTypeConstants, PropertyChangeListener, ActionListener,
		ListSelectionListener, SGIDataSetupTableHolder {

	private static final long serialVersionUID = -7171083044587328400L;

	/**
     * The information map.
     */
    protected Map<String, Object> mInfoMap = null;

    /**
     * The data type.
     */
    protected String mDataType = null;

	/**
	 * The default constructor.
	 */
    public SGDataSetupPanel() {
    	super();
    	this.initProperty();
    }

    /**
     * Initializes the properties.
     */
    private void initProperty() {
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    	    	getTable().getSelectionModel().addListSelectionListener(SGDataSetupPanel.this);

    	        // set up the scroll bar properties
    			JScrollPane scrollPane = getOriginScrollPane();
    			if (scrollPane != null) {
        	        JScrollBar vertBar = scrollPane.getVerticalScrollBar();
        	        vertBar.setUnitIncrement(SGIConstants.SCROLL_BAR_UNIT_INCREMENT);
        	        vertBar.setBlockIncrement(SGIConstants.SCROLL_BAR_BLOCK_INCREMENT);
    			}

    	        // add an action event listener
    	        getClearButton().addActionListener(SGDataSetupPanel.this);
    	        getRestoreButton().addActionListener(SGDataSetupPanel.this);
    	        getComplementButton().addActionListener(SGDataSetupPanel.this);

    	        // add a property change event listener
    	        getTable().addPropertyChangeListener(SGDataSetupPanel.this);
    		}
    	});
    }

    protected abstract JScrollPane getOriginScrollPane();

    protected abstract SGDataColumnSelectionPanel getDataColumnSelectionPanel();

    /**
     * Returns the button to restore all data column types.
     *
     * @return the button to restore all data column types
     */
    public SGButton getRestoreButton() {
        return this.getDataColumnSelectionPanel().getRestoreButton();
    }

    /**
     * Returns the button to clear all data column types.
     *
     * @return the button to clear all data column types
     */
    public SGButton getClearButton() {
        return this.getDataColumnSelectionPanel().getClearButton();
    }

    /**
     * Returns the button to complement the column types.
     *
     * @return the button to complement the column types
     */
    public SGButton getComplementButton() {
        return this.getDataColumnSelectionPanel().getComplementButton();
    }

    /**
     * Returns the table.
     * @return
     *         a table object
     */
    public JTable getTable() {
        return this.getDataColumnSelectionPanel().getTable();
    }

    /**
     * Returns selected column types.
     *
     * @return
     *        selected column types
     */
    public abstract SGDataColumnInfo[] getDataColumnTypes();

	@Override
	public SGDataColumnInfoSet getDataColumnInfoSet() {
    	SGDataColumnInfoSet colInfoSet = new SGDataColumnInfoSet(this.getDataColumnTypes());
    	return colInfoSet;
	}

	@Override
	public void clear() {
        this.getDataColumnSelectionPanel().clear();
	}

    /**
     * Update the table.
     */
    public void updateTable() {
        this.getDataColumnSelectionPanel().updateTable();
    }

    /**
     * Sets the data type.
     * @param dataType
     *                the data type to be set
     */
    public void setDataType(String dataType) {
        this.mDataType = dataType;
        this.getDataColumnSelectionPanel().setDataType(dataType);
    }

    /**
     * Called when an action event is generated.
     * @param e
     *         an action event
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(this.getClearButton()) || source.equals(this.getRestoreButton())
        		|| source.equals(this.getComplementButton())) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    updateComponentsWithTable();
                }
            });
        }
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
                        updateComponentsWithTable();
                    }
                });
            }
        }
    }

    /**
     * Updates the components with variables in the table.
     *
     */
    protected abstract void updateComponentsWithTable();

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
        this.getDataColumnSelectionPanel().setPopupDialogOwner(owner);
    }

    public abstract Boolean isVariableDataType();

    protected static class TemporaryColumnInfo {
        boolean multipleVariable;
        SGDataColumnInfo[] colInfoVariable;
        SGDataColumnInfo[] colInfoPickup;
        void setColumnInfo(final boolean multipleVariable, final SGDataColumnInfo[] cols) {
            this.multipleVariable = multipleVariable;
            if (multipleVariable) {
                this.colInfoVariable = new SGDataColumnInfo[cols.length];
                for (int i = 0; i < cols.length; i++) {
                    this.colInfoVariable[i] = (SGDataColumnInfo)cols[i].clone();
                }
            } else {
                this.colInfoPickup = new SGDataColumnInfo[cols.length];
                for (int i = 0; i < cols.length; i++) {
                    this.colInfoPickup[i] = (SGDataColumnInfo)cols[i].clone();
                }
            }
        }
        void clearColumnInfo() {
            this.colInfoVariable = new SGDataColumnInfo[0];
            this.colInfoPickup = new SGDataColumnInfo[0];
        }
    }

    protected final TemporaryColumnInfo mTemporaryColumnInfo = new TemporaryColumnInfo();

    protected SGDimensionPanel[] getDimensionPanels(JPanel p) {
    	Component[] coms = p.getComponents();
    	List<SGDimensionPanel> dpList = new ArrayList<SGDimensionPanel>();
    	for (int ii = 0; ii < coms.length; ii++) {
    		if (coms[ii] instanceof SGDimensionPanel) {
    			dpList.add((SGDimensionPanel) coms[ii]);
    		}
    	}
    	SGDimensionPanel[] dpArray = new SGDimensionPanel[dpList.size()];
    	return dpList.toArray(dpArray);
    }

    /**
     * Returns the indices for picked up dimension of scalar XY data.
     *
     * @return the indices for picked up dimension of scalar XY data
     */
    public abstract SGIntegerSeriesSet getSXYPickUpIndices();

    /**
     * Adds an item listener.
     *
     * @param l
     *          an item listener
     */
    public abstract void addItemListener(ItemListener l);

	/**
	 * Adds a document listener to the components.
	 *
	 * @param l
	 *          a document listener
	 */
    public abstract void addDocumentListener(DocumentListener l);

    /**
     * Returns an array of data column information.
     *
     * @return an array of data column information
     */
    public SGDataColumnInfo[] getDataColumnInfoArray() {
    	return this.getDataColumnSelectionPanel().getDataColumnInfoArray();
    }

    // refresh the columns that error bars and tick labels are appended
    protected void clearUselessColumnType(SGDataColumnInfo[] cols) {
        for (int ii = 0; ii < cols.length; ii++) {
        	String columnType = cols[ii].getColumnType();
        	String header = null;
        	if (columnType.startsWith(LOWER_ERROR_VALUE)) {
        		header = LOWER_ERROR_VALUE;
        	} else if (columnType.startsWith(UPPER_ERROR_VALUE)) {
        		header = UPPER_ERROR_VALUE;
        	} else if (columnType.startsWith(LOWER_UPPER_ERROR_VALUE)) {
        		header = LOWER_UPPER_ERROR_VALUE;
        	} else if (columnType.startsWith(TICK_LABEL)) {
        		header = TICK_LABEL;
        	}
        	if (header != null) {
        		boolean bClear = true;
        		final String nameStr = SGDataUtility.removeHeaderTitle(columnType);
    			for (int jj = 0; jj < cols.length; jj++) {
    				boolean check = false;
    				if (nameStr != null && nameStr.equals(cols[jj].getName())) {
    					// NetCDF or MDArray data
    					check = true;
    				} else {
    	        		final String noStr = SGDataUtility.removeHeaderNo(columnType);
    					Integer num = SGUtilityText.getInteger(noStr);
    					if (num != null) {
    						if (num.intValue() == jj + 1) {
    							// SDArray
    							check = true;
    						}
    					}
    				}
    				if (check) {
    					String colType = cols[jj].getColumnType();
    					if (X_VALUE.equals(colType) || Y_VALUE.equals(colType)) {
        					bClear = false;
        					break;
    					}
    				}
    			}
        		if (bClear) {
        			// clear column type
        			this.getDataColumnSelectionPanel().setColumnType(ii, "");
        			cols[ii].setColumnType("");
        		}
        	}
        }
    }

    /**
     * Returns the stride for an array of given name.
     *
     * @param name
     *           the name of an array
     * @return the stride
     */
    public SGIntegerSeriesSet getStride(final String name) {
    	return this.getStrideMap().get(name);
    }

    /**
     * Returns the map of the stride for arrays.
     *
     * @return the map of the stride for arrays
     */
    public abstract Map<String, SGIntegerSeriesSet> getStrideMap();

    protected List<SGDataColumnInfo> findColumnsWithColumnType(SGDataColumnInfo[] cols,
    		final String columnType) {
    	return SGDataUtility.findColumnsWithColumnType(cols, columnType);
    }

    protected List<SGDataColumnInfo> findColumnsWithColumnTypeStartsWith(
    		SGDataColumnInfo[] cols, final String columnType) {
    	return SGDataUtility.findColumnsWithColumnTypeStartsWith(cols, columnType);
    }

    protected SGDataColumnInfo findColumnWithName(SGDataColumnInfo[] cols, final String name) {
    	return SGDataUtility.findColumnWithName(cols, name);
    }

    protected List<SGDataColumnInfo> findColumnsWithValueType(
    		SGDataColumnInfo[] cols, final String columnType) {
    	return SGDataUtility.findColumnsWithValueType(cols, columnType);
    }

    /**
     * Returns whether stride of data arrays is available.
     *
     * @return true if stride of data arrays is available
     */
    public abstract boolean isStrideAvailable();

	protected void updateStrideMap(SGIndexPanel panel, String key, Map<String, SGIntegerSeriesSet> map) {
		SGIntegerSeriesSet indices = panel.getIndices();
		map.put(key, indices);
	}

	protected void updateLengthMap(SGIndexPanel p, String key, Map<String, Integer> lengthMap) {
		lengthMap.put(key, p.getLength());
	}

	protected void setStride(SGIndexPanel p, String key, Map<String, SGIntegerSeriesSet> map) {
		SGIntegerSeriesSet stride = map.get(key);
		p.setIndices(stride);
	}

	protected abstract Map<String, Integer> getFullLengthMap();
	
	protected abstract void setStrideMap(Map<String, SGIntegerSeriesSet> strideMap);
}

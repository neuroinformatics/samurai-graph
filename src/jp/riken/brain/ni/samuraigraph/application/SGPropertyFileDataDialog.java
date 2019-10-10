/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SGPropertyFileDataDialog.java
 *
 * Created on 2009/07/28, 15:15:38
 */

package jp.riken.brain.ni.samuraigraph.application;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;

import jp.riken.brain.ni.samuraigraph.base.SGButton;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTextField;
import jp.riken.brain.ni.samuraigraph.data.SGDataSetupPanel;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataSetupTableHolder;

/**
 *
 */
public abstract class SGPropertyFileDataDialog extends SGSingleFileChooserWizardDialog
        implements PropertyChangeListener, ItemListener, SGIDataColumnTypeConstants {

	private static final long serialVersionUID = 4613530368941416965L;

	/**
     * The title of this dialog.
     */
    public static final String TITLE = "Setup the Data";

    /** Creates new form SGPropertyFileDataDialog */
    public SGPropertyFileDataDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.initProperty();
    }

    /** Creates new form SGPropertyFileDataDialog */
    public SGPropertyFileDataDialog(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        this.initProperty();
    }

    protected abstract SGIDataSetupTableHolder getDataSetupTableHolder();
        
    // dummy Previous button
    private SGButton mPreviousButton;
    
    // dummy Next button
    private SGButton mNextButton;

    // Invisible text field.
    protected SGTextField mInvisibleTextField;

    /**
     * The data type.
     */
    protected String mDataType = null;
    
    /**
     * An information map.
     */
    protected Map<String, Object> mInfoMap = null;

    protected SGDataColumnInfoSet mDataColumnInfoSet = null;
    
    /**
     * Version number of the property file.
     */
    protected String mVersionNumber = null;
    
    /**
     * Initialize this dialog.
     */
    private void initProperty() {
        
        // set the title
        this.setTitle(TITLE);

        // create dummy buttons
        this.mPreviousButton = new SGButton(PREVIOUS_BUTTON_TEXT);
        this.mNextButton = new SGButton(NEXT_BUTTON_TEXT);
        this.mInvisibleTextField = new SGTextField();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	SGIDataSetupTableHolder holder = getDataSetupTableHolder();
            	SGDataSetupPanel ds = getDataSetupPanel();
            	
                // add this dialog as an action event listener of the buttons
                holder.getRestoreButton().addActionListener(SGPropertyFileDataDialog.this);
                holder.getClearButton().addActionListener(SGPropertyFileDataDialog.this);
                holder.getComplementButton().addActionListener(SGPropertyFileDataDialog.this);

                // add this dialog as a property change event listener of the table
                holder.getTable().addPropertyChangeListener(SGPropertyFileDataDialog.this);
                
    	        ds.addItemListener(SGPropertyFileDataDialog.this);

    	        // add this dialog as a property change event listener
    	        ds.setPopupDialogOwner(SGPropertyFileDataDialog.this);
    	        
    	        // add this dialog as a dialog change event listener
    	        ds.addDocumentListener(SGPropertyFileDataDialog.this);
            }
        });
    }

    /**
     * Called when an action event is thrown.
     * @param e
     *          an action event
     */
    public void actionPerformed(final ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(this.getFileChooserButton())) {
            String fileName = this.getFileName();
            if (this.isAcceptable(fileName)) {
                if (this.getDataSetupTableHolder().checkSelectedItems()) {
                    // enable the OK button
                    acceptFile(true);
                }
            }
        } else if (source.equals(this.getOKButton())) {
            // get selected items and set them to the attribute
            this.mDataColumnInfoSet = this.getDataSetupTableHolder().getDataColumnInfoSet();
        } else if (source.equals(this.getDataSetupTableHolder().getClearButton())) {
            this.getOKButton().setEnabled(false);
        } else if (source.equals(this.getDataSetupTableHolder().getRestoreButton())) {
            this.getOKButton().setEnabled(true);
        } else if (source.equals(this.getDataSetupTableHolder().getComplementButton())) {
            this.getOKButton().setEnabled(true);
        }
        
        // calls the method of the super class
        super.actionPerformed(e);
    }

    protected JButton getPreviousButton() {
        return this.mPreviousButton;
    }

    protected JButton getNextButton() {
        return this.mNextButton;
    }

    /**
     * Clear all data.
     */
    public void clear() {
        
        // clear the table
        this.getDataSetupTableHolder().clear();
        
        // clear the file name
        this.setFileName("");
    }    
    
    /**
     * Sets data.
     * 
     * @param dataType
     *            data type
     * @param infoMap
     *            a map of information
     */
    public void setDataType(final String dataType, final Map<String, Object> infoMap) {
        if (dataType == null || infoMap == null) {
            throw new IllegalArgumentException("dataType == null || infoMap == null");
        }
        this.mDataType = dataType;
        this.mInfoMap = new HashMap<String, Object>(infoMap);
        this.getDataSetupTableHolder().setDataType(dataType);
    }
    
//    /**
//     * Returns selected column types.
//     * 
//     * @return selected column types
//     */
//    public SGDataColumnInfo[] getDataColumnTypes() {
//        return this.getDataSetupTableHolder().getDataColumnTypes();
//    }
    
    /**
     * Overrode to enable or disable the OK button and the table instead of the Next button.
     */
    protected void acceptFile(final boolean b) {
        
        // set enable or disable the OK button
        this.getOKButton().setEnabled(b);
    }

//    /**
//     * Returns an array of data column information.
//     * @return
//     *         an array of data column information
//     */
//    public SGDataColumnInfo[] getDataColumnInfo() {
//        if (this.mDataColumnInfo == null) {
//            return null;
//        } else {
//            return (SGDataColumnInfo[]) mDataColumnInfo.clone();
//        }
//    }

    /**
     * Returns an array of data column information.
     * @return
     *         an array of data column information
     */
    public SGDataColumnInfoSet getDataColumnInfoSet() {
        if (this.mDataColumnInfoSet == null) {
            return null;
        } else {
            return (SGDataColumnInfoSet) this.mDataColumnInfoSet.clone();
        }
    }

    /**
     * Called when the component is resized.
     *
     * @param e
     *            a component event
     */
    public void componentResized(ComponentEvent e) {
        super.componentResized(e);
        this.updateTable();
    }

    /**
     * Called when the component is shown.
     *
     * @param e
     *            a component event
     */
    public void componentShown(ComponentEvent e) {
        super.componentShown(e);
        this.updateTable();
    }
    
    /**
     * Update the table.
     */
    private void updateTable() {
        this.getDataSetupTableHolder().updateTable();
    }

    /**
     * Called when the table is changed.
     * 
     * @param evt
     *            an event that the property changes
     */
    public void propertyChange(PropertyChangeEvent evt) {
        Object source = evt.getSource();
        if (source.equals(this.getDataSetupTableHolder().getTable())) {
            String name = evt.getPropertyName();
            if ("tableCellEditor".equals(name)) {
                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {
                        final boolean b = getDataSetupTableHolder().checkSelectedItems();
                        getOKButton().setEnabled(b);
                    }
                });
            }
        }
    }

    /**
     * Returns the panel to set up the data.
     * 
     * @return the panel to set up the data
     */
    protected abstract SGDataSetupPanel getDataSetupPanel();

    /**
     * Called when a text string is inserted.
     */
    public void insertUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(new InputObserver());
    }

    /**
     * Called when a text string is removed.
     */
    public void removeUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(new InputObserver());
    }

    /**
     * Called when a text string is changed.
     */
    public void changedUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(new InputObserver());
    }

	@Override
	public void itemStateChanged(ItemEvent e) {
        SwingUtilities.invokeLater(new InputObserver());
	}

    /**
     * An observer to set enabled / disabled the OK button with input values.
     *
     */
    protected class InputObserver extends Thread {
    	InputObserver() {
    		super();
    	}
    	public void run() {
            final boolean b = getDataSetupTableHolder().checkSelectedItems();
            getOKButton().setEnabled(b);
        }
    }

    /**
     * Returns a text string for the data type.
     * 
     * @return a text string for the data type
     */
    public String getDataType() {
    	return this.mDataType;
    }
    
    @Override
    protected String getFileName() {
        return this.mInvisibleTextField.getText();
    }
    
    @Override
    protected void setFileName(final String fileName) {
        this.mInvisibleTextField.setText(fileName);
    }

	public boolean isStrideAvailable() {
		return this.getDataSetupPanel().isStrideAvailable();
	}

	public Map<String,SGIntegerSeriesSet> getStrideMap() {
		return this.getDataSetupPanel().getStrideMap();
	}

}

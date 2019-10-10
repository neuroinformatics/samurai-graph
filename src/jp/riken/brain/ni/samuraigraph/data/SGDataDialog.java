package jp.riken.brain.ni.samuraigraph.data;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jp.riken.brain.ni.samuraigraph.base.SGDialog;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;

public abstract class SGDataDialog extends SGDialog implements PropertyChangeListener, 
		DocumentListener, ItemListener {

	private static final long serialVersionUID = 2973844074792392106L;

	/**
     * Builds a dialog for given frame.
     * 
     * @param parent
     *           the owner of this dialog
     * @param modal
     *           true for the modal dialog
     */
    public SGDataDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.initProperty();
    }
    
    /**
     * Builds a dialog for given dialog.
     * 
     * @param parent
     *           the owner of this dialog
     * @param modal
     *           true for the modal dialog
     */
    public SGDataDialog(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        this.initProperty();
    }
    
    private void initProperty() {
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			SGDataSetupPanel ds = getDataSetupPanel();
    			
    	        // add this dialog as an action event listener
    	        getOKButton().addActionListener(SGDataDialog.this);
    	        getCancelButton().addActionListener(SGDataDialog.this);
    	        ds.getRestoreButton().addActionListener(SGDataDialog.this);
    	        ds.getClearButton().addActionListener(SGDataDialog.this);
    	        ds.getComplementButton().addActionListener(SGDataDialog.this);
    	        
    	        // add this dialog as an item event listener
    	        ds.addItemListener(SGDataDialog.this);

    	        // add this dialog as a property change event listener
    	        ds.getTable().addPropertyChangeListener(SGDataDialog.this);

    	        // add this dialog as a dialog change event listener
    	        ds.addDocumentListener(SGDataDialog.this);
    	        
    	        // packs this dialog
    	        pack();
    		}
    	});
    }
    
    /**
     * Returns the OK button of this dialog.
     * 
     * @return the OK button
     */
    protected abstract JButton getOKButton();

    /**
     * Returns the Cancel button of this dialog.
     * 
     * @return the Cancel button
     */
    protected abstract JButton getCancelButton();
    
    /**
     * Returns an object that has a table to setup data.
     * 
     * @return an object that has a table to setup data
     */
    protected abstract SGIDataSetupTableHolder getTableHolder();

    /**
     * Returns the panel to set up the data.
     * 
     * @return the panel to set up the data
     */
    protected abstract SGDataSetupPanel getDataSetupPanel();

    /**
     * An observer to set enabled / disabled the OK button with input values.
     *
     */
    protected class InputObserver extends Thread {
    	InputObserver() {
    		super();
    	}
    	public void run() {
            final boolean b = getTableHolder().checkSelectedItems();
            getOKButton().setEnabled(b);
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
        if (source.equals(this.getTableHolder().getTable())) {
            String name = evt.getPropertyName();
            if ("tableCellEditor".equals(name)) {
                SwingUtilities.invokeLater(new InputObserver());
            }
        }
    }

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
     * Clear all data.
     */
    public void clear() {
        this.getTableHolder().clear();
    }

    /**
     * Returns the map of the stride of data arrays.
     * 
     * @return the map of the stride of data arrays
     */
    public Map<String, SGIntegerSeriesSet> getStrideMap() {
    	return this.getDataSetupPanel().getStrideMap();
    }
    
    /**
     * Returns whether stride of data arrays is available.
     * 
     * @return true if stride of data arrays is available
     */
    public boolean isStrideAvailable() {
    	return this.getDataSetupPanel().isStrideAvailable();
    }
}

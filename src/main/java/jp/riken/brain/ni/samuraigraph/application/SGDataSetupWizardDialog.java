package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTextField;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataSetupPanel;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;

/**
 * The base class of the wizard dialog to set up the data.
 *
 */
public abstract class SGDataSetupWizardDialog extends SGWizardDialog implements
		PropertyChangeListener, DocumentListener, ItemListener, SGIDataColumnTypeConstants,
		SGIApplicationTextConstants {

	private static final long serialVersionUID = -225586291971946590L;

	public SGDataSetupWizardDialog(Frame owner, boolean modal) {
		super(owner, modal);
		this.initProperty();
	}
	
	private void initProperty() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SGDataSetupPanel ds = getDataSetupPanel();
		        
		        // add this dialog as an action event listener
		        ds.getClearButton().addActionListener(SGDataSetupWizardDialog.this);
		        ds.getRestoreButton().addActionListener(SGDataSetupWizardDialog.this);
		        ds.getComplementButton().addActionListener(SGDataSetupWizardDialog.this);
		        ds.getTable().addPropertyChangeListener(SGDataSetupWizardDialog.this);
		        getDataNameTextField().addActionListener(SGDataSetupWizardDialog.this);
		        ds.addDocumentListener(SGDataSetupWizardDialog.this);
		        ds.addItemListener(SGDataSetupWizardDialog.this);
		        
		        // set dialog owner
		        ds.setPopupDialogOwner(SGDataSetupWizardDialog.this);
		        
    	        // packs this dialog
    	        pack();
			}
		});
	}

	protected abstract SGDataSetupPanel getDataSetupPanel();

    /**
     * Clear all data.
     */
    public void clear() {
        this.getDataSetupPanel().clear();
    }

    /**
     * An observer to set enabled / disabled the OK button with input values.
     *
     */
    class InputObserver extends Thread {
    	InputObserver() {
    		super();
    	}
    	
    	public void run() {
            final boolean b = getDataSetupPanel().checkSelectedItems();
            getOKButton().setEnabled(b);
            getNextButton().setEnabled(b);
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

    /**
     * Called when the table is changed.
     * 
     * @param evt
     *            an event that the property changes
     */
    public void propertyChange(PropertyChangeEvent evt) {
        Object source = evt.getSource();
        if (source.equals(this.getDataSetupPanel().getTable())) {
            String name = evt.getPropertyName();
            if ("tableCellEditor".equals(name)) {
                SwingUtilities.invokeLater(new InputObserver());
            }
        }
    }

    /**
     * Returns data column information
     * 
     * @return data column information
     */
    public SGDataColumnInfoSet getDataColumnInfoSet() {
        return this.getDataSetupPanel().getDataColumnInfoSet();
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
        this.getDataSetupPanel().updateTable();
    }

    protected abstract SGTextField getDataNameTextField();

    /**
     * Called when an action event is generated.
     * @param e
     *         an action event
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Object source = e.getSource();
        if (source.equals(this.getDataSetupPanel().getClearButton())) {
            this.getOKButton().setEnabled(false);
            this.getNextButton().setEnabled(false);
        } else if (source.equals(this.getDataSetupPanel().getRestoreButton())) {
            this.getOKButton().setEnabled(true);
            this.getNextButton().setEnabled(true);
        } else if (source.equals(this.getDataSetupPanel().getComplementButton())) {
            this.getOKButton().setEnabled(true);
            this.getNextButton().setEnabled(true);
        } else if (source.equals(this.getDataNameTextField())) {
            this.onOK();
        }
    }
    
    /**
     * Returns a text string input to the text field for the data name.
     * 
     * @return a text string input to the text field for the data name
     */
    public String getDataName() {
        return this.getDataNameTextField().getText();
    }
    
    /**
     * Sets a text string to the text field for the data name.
     * 
     * @param name
     *           a text string set to the text field for the data name
     */
    public void setDataName(final String name) {
        this.getDataNameTextField().setText(name);
    }
    
    /**
     * Overrode to check validity of the input value.
     */
    protected boolean onOK() {
        // check the data name
        boolean ok = true;
        String name = this.getDataName();
        if (!SGUtilityText.isValidString(name)) {
            ok = false;
        }
        if (!ok) {
            SGUtility.showMessageDialog(this, MSG_INVALID_INPUT_VALUE,
                    SGIConstants.TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return super.onOK();
    }
    
    public Boolean isVariableDataType() {
        return this.getDataSetupPanel().isVariableDataType();
    }
    
    /**
     * Returns the indices for picked up dimension of scalar XY data.
     * 
     * @return the indices for picked up dimension of scalar XY data
     */
    public SGIntegerSeriesSet getSXYPickUpIndices() {
    	return this.getDataSetupPanel().getSXYPickUpIndices();
    }

    /**
     * Called when the state of a combo-box is changed.
     */
	@Override
	public void itemStateChanged(ItemEvent e) {
		SwingUtilities.invokeLater(new InputObserver());
	}

    /**
     * Returns the stride for an array of given name.
     * 
     * @param name
     *           the name of an array
     * @return the stride
     */
    public SGIntegerSeriesSet getStride(final String name) {
    	return this.getDataSetupPanel().getStride(name);
    }

    /**
     * Returns the map of the stride for arrays.
     * 
     * @return the map of the stride for arrays
     */
    public Map<String, SGIntegerSeriesSet> getStrideMap() {
    	return this.getDataSetupPanel().getStrideMap();
    }

    protected void setupTitle(String dataType) {
    	String title = SGDataUtility.createTitleString("Set up the Data ", dataType);
    	this.setTitle(title);
    }
}

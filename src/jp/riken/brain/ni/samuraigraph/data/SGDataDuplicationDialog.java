package jp.riken.brain.ni.samuraigraph.data;

import java.awt.event.ActionEvent;

import javax.swing.JTextField;

import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;

/**
 * The base class of the dialog to duplicate a data.
 *
 */
public abstract class SGDataDuplicationDialog extends SGDataDialog {

	private static final long serialVersionUID = -4950349260939908575L;

	/**
     * Builds a dialog for given frame.
     * 
     * @param parent
     *           the owner of this dialog
     * @param modal
     *           true for the modal dialog
     */
    public SGDataDuplicationDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }
    
    /**
     * Builds a dialog for given dialog.
     * 
     * @param parent
     *           the owner of this dialog
     * @param modal
     *           true for the modal dialog
     */
    public SGDataDuplicationDialog(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }

    /**
     * Returns the name of the data.
     * 
     * @return the name of the data
     */
    public String getDataName() {
        return this.getDataNameTextField().getText();
    }

    /**
     * Returns the text field to set data name.
     * 
     * @return a text field to set the data name
     */
    protected abstract JTextField getDataNameTextField();

    /**
     * Close this dialog.
     */
    protected void onEscKeyTyped() {
        this.onCanceled();
    }

    /**
     * Called when the OK button is pressed.
     *
     * @return true if succeeded
     */
    protected boolean onOK() {
        this.setCloseOption(OK_OPTION);
        this.setVisible(false);
        return true;
    }

    /**
     * Called when the cancel button is pressed.
     *
     * @return true if succeeded
     */
    protected boolean onCanceled() {
        this.setCloseOption(CANCEL_OPTION);
        this.setVisible(false);
        return true;
    }

    /**
     * Called when an action event is invoked.
     *
     * @param e
     *          an action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
    	super.actionPerformed(e);
        Object source = e.getSource();
        if (source.equals(this.getOKButton())) {
            this.onOK();
        } else if (source.equals(this.getCancelButton())) {
            this.onCanceled();
        } else if (source.equals(this.getTableHolder().getClearButton())) {
            this.getOKButton().setEnabled(false);
        } else if (source.equals(this.getTableHolder().getRestoreButton())) {
            this.getOKButton().setEnabled(true);
        } else if (source.equals(this.getTableHolder().getComplementButton())) {
            this.getOKButton().setEnabled(true);
        }
    }

    /**
     * Returns selected column types.
     * 
     * @return
     *        selected column types
     */
    public SGDataColumnInfo[] getDataColumnTypes() {
        return this.getDataSetupPanel().getDataColumnTypes();
    }
    
    protected void setupTitle(String dataType) {
    	String title = SGDataUtility.createTitleString("Duplicate ", dataType);
    	this.setTitle(title);
    }
}

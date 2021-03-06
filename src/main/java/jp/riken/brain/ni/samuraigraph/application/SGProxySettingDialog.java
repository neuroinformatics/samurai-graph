package jp.riken.brain.ni.samuraigraph.application;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;

import jp.riken.brain.ni.samuraigraph.base.SGDialog;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

/**
 * A dialog to set the proxy connection.
 * 
 */
public class SGProxySettingDialog extends SGDialog {

    private static final long serialVersionUID = -8514470147936484758L;

    public static final String TITLE = "Connection Settings";

    /** Creates new form JDialog */
    public SGProxySettingDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
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

        mButtonPanel = new javax.swing.JPanel();
        mOKButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mCancelButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mPanel = new javax.swing.JPanel();
        mDirectAccessRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
        mManualRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
        mHostNameLabel = new javax.swing.JLabel();
        mHostNameTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mPortNumberLabel = new javax.swing.JLabel();
        mPortNumberTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mSpaceLabel = new javax.swing.JLabel();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        mOKButton.setText("OK");
        mOKButton.setFont(new java.awt.Font("Dialog", 1, 12));
        mButtonPanel.add(mOKButton);

        mCancelButton.setText("Cancel");
        mCancelButton.setFont(new java.awt.Font("Dialog", 1, 12));
        mButtonPanel.add(mCancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(mButtonPanel, gridBagConstraints);

        mPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Configure Proxy to Access the Internet", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        mPanel.setLayout(new java.awt.GridBagLayout());

        mDirectAccessRadioButton.setText("Direct connection to the Internet");
        mDirectAccessRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        mPanel.add(mDirectAccessRadioButton, gridBagConstraints);

        mManualRadioButton.setText("Manual proxy configuration");
        mManualRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 0, 0);
        mPanel.add(mManualRadioButton, gridBagConstraints);

        mHostNameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        mHostNameLabel.setText("HTTP Proxy:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 10, 5);
        mPanel.add(mHostNameLabel, gridBagConstraints);

        mHostNameTextField.setColumns(20);
        mHostNameTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 10, 0);
        mPanel.add(mHostNameTextField, gridBagConstraints);

        mPortNumberLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        mPortNumberLabel.setText("Port:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 10, 5);
        mPanel.add(mPortNumberLabel, gridBagConstraints);

        mPortNumberTextField.setColumns(3);
        mPortNumberTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 10, 10);
        mPanel.add(mPortNumberTextField, gridBagConstraints);

        mSpaceLabel.setText("        ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 10, 0);
        mPanel.add(mSpaceLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(mPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mButtonPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mCancelButton;
    private jp.riken.brain.ni.samuraigraph.base.SGRadioButton mDirectAccessRadioButton;
    private javax.swing.JLabel mHostNameLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mHostNameTextField;
    private jp.riken.brain.ni.samuraigraph.base.SGRadioButton mManualRadioButton;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mOKButton;
    private javax.swing.JPanel mPanel;
    private javax.swing.JLabel mPortNumberLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mPortNumberTextField;
    private javax.swing.JLabel mSpaceLabel;
    // End of variables declaration//GEN-END:variables

    /**
     * The direct access flag.
     */
    private boolean mDirectAccessFlag = false;

    /**
     * The host name.
     */
    private String mHostName = "";

    /**
     * The port number.
     */
    private int mPortNumber = -1;

    /**
     * Initializes this dialog.
     * 
     * @return true if succeeded
     */
    private boolean initProperty() {
        // set the title
        this.setTitle(TITLE);

        // create a button group
        ButtonGroup bg = new ButtonGroup();
        bg.add(this.mDirectAccessRadioButton);
        bg.add(this.mManualRadioButton);

        // add this object as an action listener
        this.mOKButton.addActionListener(this);
        this.mCancelButton.addActionListener(this);
        this.mDirectAccessRadioButton.addActionListener(this);
        this.mManualRadioButton.addActionListener(this);
        this.mHostNameTextField.addActionListener(this);
        this.mPortNumberTextField.addActionListener(this);

        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	super.actionPerformed(e);
        Object source = e.getSource();
        String command = e.getActionCommand();
        if (command.equals(SGDialog.OK_BUTTON_TEXT)
                || source.equals(this.mHostNameTextField)
                || source.equals(this.mPortNumberTextField)) {
            this.onOK();
        } else if (command.equals(SGDialog.CANCEL_BUTTON_TEXT)) {
            this.onCanceled();
        } else if (source.equals(this.mDirectAccessRadioButton)
                || source.equals(this.mManualRadioButton)) {
            this.setDirectAccess(this.isDirectAccessFromRadioButton());
        }
    }

    /**
     * 
     */
    protected boolean onOK() {
        final String hostName = this.getHostFromTextField();
        final int num = this.getPortNumberFromTextField();
        final boolean direct = this.isDirectAccessFromRadioButton();
        if (direct == false) {
            final int len = hostName.length();
            if (len == 0 || (len != 0 && num == -1)) {
                SGUtility.showIllegalInputErrorMessageDialog(this);
                return false;
            }
        }

        // set attributes
        this.mHostName = hostName;
        this.mPortNumber = num;
        this.mDirectAccessFlag = direct;

        //
        this.setVisible(false);
        this.notifyToListener(OK_BUTTON_TEXT);
        return true;
    }

    /**
     * 
     */
    protected boolean onCanceled() {
        this.setVisible(false);
        this.notifyToListener(CANCEL_BUTTON_TEXT);
        return true;
    }

    private boolean isDirectAccessFromRadioButton() {
        return this.mDirectAccessRadioButton.isSelected();
    }

    private String getHostFromTextField() {
        return this.mHostNameTextField.getText();
    }

    private int getPortNumberFromTextField() {
        String text = this.mPortNumberTextField.getText();
        Integer num = SGUtilityText.getInteger(text);
        if (num == null) {
        	return -1;
        } else {
        	return num.intValue();
        }
    }

    /**
     * Returns the direct access flag.
     * 
     * @return the direct access flag
     */
    public boolean isDirectAccess() {
        return this.mDirectAccessFlag;
    }

    /**
     * Returns the host name.
     * 
     * @return the host name
     */
    public String getHostName() {
        return this.mHostName;
    }

    /**
     * Returns the port number.
     * 
     * @return the port number
     */
    public int getPortNumber() {
        return this.mPortNumber;
    }

    /**
     * Sets the direct access flag.
     * 
     * @param b
     *          the direct access flag to set
     */
    public void setDirectAccess(final boolean b) {
    	
    	// set to the attribute
        this.mDirectAccessFlag = b;
        
        // set enabled / disabled the components
        final boolean man = !b;
        this.mManualRadioButton.setSelected(man);
        this.mHostNameLabel.setEnabled(man);
        this.mHostNameTextField.setEnabled(man);
        this.mPortNumberLabel.setEnabled(man);
        this.mPortNumberTextField.setEnabled(man);
    }

    /**
     * Sets the host name.
     * 
     * @param name
     *           the host name to set
     */
    public void setHostName(final String name) {
    	// set to the attribute
        this.mHostName = name;
    }

    /**
     * Sets the port number.
     * 
     * @param num
     *           the port number to set
     */
    public void setPortNumber(final int num) {
    	// set to the attribute
        this.mPortNumber = num;
    }

    /**
     * Sets the visibility of this dialog.
     * 
     * @param b
     *          true to set visible
     */
    public void setVisible(final boolean b) {
    	
    	if (b) {
        	// set the components
            this.mDirectAccessRadioButton.setSelected(this.mDirectAccessFlag);
            this.mHostNameTextField.setText(this.mHostName);
            String str = null;
            if (this.mPortNumber > 0) {
                str = Integer.toString(this.mPortNumber);
            } else {
                str = "";
            }
            this.mPortNumberTextField.setText(str);
    	}
        
        // set visible
        super.setVisible(b);
    }

    /**
     * Called when this dialog is closing.
     * 
     * @param e
     *          the window event
     */
    public void windowClosing(final WindowEvent e) {
        super.windowClosing(e);
        this.onCanceled();
    }

    /**
     * Called when the escape key is typed.
     *
     */
    protected void onEscKeyTyped() {
        this.onCanceled();
    }

}

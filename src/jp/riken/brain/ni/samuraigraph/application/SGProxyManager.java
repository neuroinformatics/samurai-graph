package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import jp.riken.brain.ni.samuraigraph.base.SGDialog;

/**
 * A class to handle proxy connection.
 * 
 */
class SGProxyManager implements ActionListener, SGIPreferencesConstants {

    /**
     * A flag whether access to the internet is direct.
     */
    private boolean mDirectAccessFlag = true;

    /**
     * The name of proxy server.
     */
    private String mProxyHostName = "";

    /**
     * The port number of proxy.
     */
    private int mProxyPortNumber = -1;

    /**
     * Builds the proxy manager.
     * 
     */
    public SGProxyManager() {
    	super();
    	
        Preferences pref = Preferences.userNodeForPackage(this.getClass());
        final boolean direct = pref.getBoolean(PREF_KEY_DIRECT_ACCESS, true);
        final String hostName = pref.get(PREF_KEY_PROXY_HOST_NAME, "");
        final int portNumber = pref.getInt(PREF_KEY_PROXY_PORT_NUMBER, -1);

        this.mDirectAccessFlag = direct;
        if ("".equals(hostName) == false) {
            this.mProxyHostName = hostName;
        }
        if (portNumber != -1) {
            this.mProxyPortNumber = portNumber;
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
     * Sets the direct access flag.
     * 
     * @param b
     *          the direct access flag to set
     */
    public void setDirectAccess(final boolean b) {
        this.mDirectAccessFlag = b;
    }

    /**
     * Returns the host name.
     * 
     * @return the host name
     */
    public String getProxyHostName() {
        return this.mProxyHostName;
    }

    /**
     * Returns the port number.
     * 
     * @return the port number
     */
    public int getProxyPortNumber() {
        return this.mProxyPortNumber;
    }

    /**
     * Sets the host name.
     * 
     * @param name
     *           the host name to set
     */
    public void setProxyHostName(final String name) {
        this.mProxyHostName = name;
    }

    /**
     * Sets the port number.
     * 
     * @param num
     *           the port number to set
     */
    public void setProxyPortNumber(final int num) {
        this.mProxyPortNumber = num;
    }

    /**
     * Shows the dialog to set proxy dialog.
     * 
     * @param owner
     *           the owner of the dialog
     * @return true if succeeded
     */
    public boolean showProxySettingDialog(final Frame owner) {
    	
    	// creates an instance
        SGProxySettingDialog dg = new SGProxySettingDialog(owner, true);
        dg.addActionListener(this);
        dg.setCenter(owner);

        // set properties
        dg.setDirectAccess(this.mDirectAccessFlag);
        dg.setHostName(this.mProxyHostName);
        dg.setPortNumber(this.mProxyPortNumber);

        // show dialog
        dg.setVisible(true);

        // dispose
        dg.dispose();

        return true;
    }

    /**
     * Called when an action event is invoked.
     * 
     * @param e
     *           the action event
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        SGProxySettingDialog dg = (SGProxySettingDialog) source;
        String command = e.getActionCommand();
        if (command.equals(SGDialog.OK_BUTTON_TEXT)) {
        	// get parameters from the dialog and set to the attributes
            this.mDirectAccessFlag = dg.isDirectAccess();
            this.mProxyHostName = dg.getHostName();
            this.mProxyPortNumber = dg.getPortNumber();

            // set to the preferences
            Preferences pref = Preferences.userNodeForPackage(this.getClass());
            pref.putBoolean(PREF_KEY_DIRECT_ACCESS, this.mDirectAccessFlag);
            pref.put(PREF_KEY_PROXY_HOST_NAME, this.mProxyHostName);
            pref.putInt(PREF_KEY_PROXY_PORT_NUMBER, this.mProxyPortNumber);
        }
    }

}
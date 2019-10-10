package jp.riken.brain.ni.samuraigraph.application;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import jp.riken.brain.ni.samuraigraph.base.SGBufferedFileWriter;
import jp.riken.brain.ni.samuraigraph.base.SGDialog;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;

/**
 * A dialog to confirm upgrading of the application.
 * 
 */
public class SGUpgradeConfirmDialog extends SGScrollPaneDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 5617296361195236539L;

    public static final String TITLE = "Samurai Graph Auto Upgrade";

    /** Creates new form SGUpgradeConfirmDialog */
    public SGUpgradeConfirmDialog() {
        super();
        this.initProperty();
    }

    /** Creates new form SGUpgradeConfirmDialog */
    public SGUpgradeConfirmDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.initProperty();
    }

    /** Creates new form SGUpgradeConfirmDialog */
    public SGUpgradeConfirmDialog(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        this.initProperty();
    }

    public static final String YES = SGDialog.YES_BUTTON_TEXT;

    public static final String NO = SGDialog.NO_BUTTON_TEXT;

    private boolean initProperty() {
        // set the title
        this.setTitle(TITLE);

        this.mOKButton.setText(YES);
        this.mCancelButton.setText(NO);

        this.pack();

        return true;
    }

    /**
     * 
     */
    public void actionPerformed(final ActionEvent e) {
        String command = e.getActionCommand();

//        // if escape key is pressed, cancel all changes
//        if (ESCAPE_KEY_TYPED.equals(command)) {
//            this.onCanceled();
//            return;
//        }
        
        if (command.equals(YES)) {
            this.onOK();
        } else if (command.equals(NO)) {
            this.onCanceled();
        }
    }

    /**
     * 
     * @param t
     * @return
     */
    public boolean setPage(String t) {
        String filename = SGApplicationUtility.getPathName(TMP_DIR, "upgrade.html");
        File f = new File(filename);
        f.deleteOnExit();

        SGBufferedFileWriter writer = null;
        try {
        	writer = new SGBufferedFileWriter(f.getPath(), SGIConstants.CHAR_SET_NAME_UTF8);
            BufferedWriter bw = writer.getBufferedWriter();
            bw.write(t);
            bw.flush();
            this.setPage(f.toURI().toURL());
        } catch (IOException ex) {
            return false;
        } finally {
        	if (writer != null) {
            	writer.close();
        	}
        }

        return true;
    }

}

package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import jp.riken.brain.ni.samuraigraph.base.SGDialog;

/**
 * The base class of wizard dialogs.
 */
public abstract class SGWizardDialog extends SGDialog {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -3377714626195028234L;

    /**
     * The previous wizard dialog.
     */
    private SGWizardDialog mPreviousWizardDialog = null;

    /**
     * The next wizard dialog.
     */
    private SGWizardDialog mNextWizardDialog = null;

    /**
     * 
     */
    public SGWizardDialog() {
        super();
        this.init();
    }

    /**
     * 
     * @param title
     */
    public SGWizardDialog(final Frame owner) {
        super(owner);
        this.init();
    }

    /**
     * 
     * @param title
     */
    public SGWizardDialog(final Frame owner, final String title) {
        super(owner, title);
        this.init();
    }

    /**
     * 
     * @param title
     */
    public SGWizardDialog(final Frame owner, final boolean modal) {
        super(owner, modal);
        this.init();
    }

    /**
     * 
     * @param title
     */
    public SGWizardDialog(final Frame owner, final String title,
            final boolean modal) {
        super(owner, title, modal);
        this.init();
    }

    /**
     * 
     * @param title
     */
    public SGWizardDialog(final Dialog owner) {
        super(owner);
        this.init();
    }

    /**
     * 
     * @param title
     */
    public SGWizardDialog(final Dialog owner, final String title) {
        super(owner, title);
        this.init();
    }

    /**
     * 
     * @param title
     */
    public SGWizardDialog(final Dialog owner, final boolean modal) {
        super(owner, modal);
        this.init();
    }

    /**
     * 
     * @param title
     */
    public SGWizardDialog(final Dialog owner, final String title,
            final boolean modal) {
        super(owner, title, modal);
        this.init();
    }

    /**
     * Initialize this dialog.
     */
    private void init() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // add this dialog as an action event listener
                getPreviousButton().addActionListener(SGWizardDialog.this);
                getNextButton().addActionListener(SGWizardDialog.this);
                getOKButton().addActionListener(SGWizardDialog.this);
                getCancelButton().addActionListener(SGWizardDialog.this);
            }
        });
    }

    /**
     * Called when the escape key is typed.
     *
     */
    protected void onEscKeyTyped() {
        this.onCanceled();
    }

    /**
     * Returns the previous wizard dialog.
     * 
     * @return the previous wizard dialog
     */
    public SGWizardDialog getPrevious() {
        return this.mPreviousWizardDialog;
    }

    /**
     * Returns the next wizard dialog.
     * 
     * @return the next wizard dialog
     */
    public SGWizardDialog getNext() {
        return this.mNextWizardDialog;
    }

    /**
     * Sets the previous dialog.
     * 
     * @param dg
     *           the previous wizard dialog
     * @return true if succeeded
     */
    public boolean setPrevious(final SGWizardDialog dg) {
        this.mPreviousWizardDialog = dg;
        final boolean prevVisible = (dg != null);
        this.getPreviousButton().setVisible(prevVisible);
        return true;
    }

    /**
     * Sets the next dialog.
     * 
     * @param dg
     *           the next wizard dialog
     * @return true if succeeded
     */
    public boolean setNext(final SGWizardDialog dg) {
        this.mNextWizardDialog = dg;
        final boolean nextVisible = (dg != null);
        this.getNextButton().setVisible(nextVisible);
        return true;
    }

    /**
     * 
     */
    public void windowClosing(final WindowEvent e) {
        super.windowClosing(e);
        this.close(e);
    }

    /**
     * Overrode this method.
     * 
     * @param e
     */
    protected void close(final WindowEvent e) {
        Object obj = e.getSource();
        if (obj.equals(this)) {
            this.onCanceled();
        }
    }

    /**
     * Called when an action event is generated.
     * @param e
     *         an action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
    	super.actionPerformed(e);
        Object source = e.getSource();
        if (source.equals(this.getOKButton())) {
            this.onOK();
        } else if (source.equals(this.getCancelButton())) {
            this.onCanceled();
        } else if (source.equals(this.getPreviousButton())) {
            this.onPrevious();
        } else if (source.equals(this.getNextButton())) {
            this.onNext();
        }
    }

    /**
     * Called when the OK button is pressed. Closes this dialog and notify this
     * action to the action listeners of this dialog.
     * 
     * @return true if succeeded
     */
    protected boolean onOK() {
        this.setCloseOption(OK_OPTION);
        this.setVisible(false);
        
        // notify to the action listeners of this dialog
        this.notifyToListener(OK_BUTTON_TEXT);
        return true;
    }

    /**
     * Called when the Cancel button is pressed. Closes this dialog and notify
     * this action to the action listeners of this dialog.
     * 
     * @return true if succeeded
     */
    protected boolean onCanceled() {
        this.setCloseOption(CANCEL_OPTION);
        this.setVisible(false);
        
        // notify to the action listeners of this dialog
        this.notifyToListener(CANCEL_BUTTON_TEXT);
        return true;
    }

    /**
     * Called when the Previous button is pressed. Closes this dialog, shows the
     * previous wizard dialog and notify this action to the action listeners
     * this dialog.
     * 
     * @return true if succeeded
     */
    protected boolean onPrevious() {
        // notify to the action listeners of this dialog
        this.notifyToListener(PREVIOUS_BUTTON_TEXT);
        return true;
    }

    /**
     * Called when the Next button is pressed. Closes this dialog, shows the
     * next wizard dialog and notify this action to the action listeners this
     * dialog.
     * 
     * @return true if succeeded
     */
    protected boolean onNext() {
        // notify to the action listeners of this dialog
        this.notifyToListener(NEXT_BUTTON_TEXT);
        return true;
    }

    /**
     * Shows the next wizard dialog if it exists.
     */
    public void showNext() {
        SGWizardDialog next = this.getNext();
        if (next != null) {
            this.setVisible(false);
            next.setCenter(this);
            next.setVisible(true);
        }
    }

    /**
     * Shows the previous wizard dialog if it exists.
     */
    public void showPrevious() {
        SGWizardDialog prev = this.getPrevious();
        if (prev != null) {
            this.setVisible(false);
            prev.setCenter(this);
            prev.setVisible(true);
        }
    }

    /**
     * Shows or hides this wizard dialog.
     * The location is set to the center of the screen.
     * @param b
     *          true if set to visible
     */
    public void setVisible(final boolean b) {

        if (b) {
            Window wnd = this.getOwner();
            if (wnd == null) {
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                final int width = this.getWidth();
                final int height = this.getHeight();
                final int x = (dim.width - width) / 2;
                final int y = (dim.height - height) / 2;
                this.setLocation(x, y);
            }
        }

        super.setVisible(b);
    }

    protected abstract JButton getPreviousButton();

    protected abstract JButton getNextButton();

    protected abstract JButton getOKButton();

    protected abstract JButton getCancelButton();

}

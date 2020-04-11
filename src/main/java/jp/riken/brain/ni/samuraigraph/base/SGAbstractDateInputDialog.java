package jp.riken.brain.ni.samuraigraph.base;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public abstract class SGAbstractDateInputDialog extends SGDialog
		implements ItemListener, ChangeListener, DocumentListener, FocusListener {

	private static final long serialVersionUID = -8811503975748065777L;

	public SGAbstractDateInputDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

    public SGAbstractDateInputDialog(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }

    /**
     * The flag whether the text field is focused.
     */
    protected boolean mTextFieldFocused;
    
	protected abstract SGTextField getTextField();
	
	protected abstract SGButton getOKButton();

	protected abstract SGButton getCancelButton();

	protected abstract void onTextUpdated(DocumentEvent e);

	@Override
	protected void onEscKeyTyped() {
		this.onCanceled();
	}
	
	private void onCanceled() {
		this.setCloseOption(CANCEL_OPTION);
		this.setVisible(false);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		this.updateText();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.updateText();
	}

	private void updateText() {
		if (this.mTextFieldFocused) {
			return;
		}
		String str = this.createString();
		this.updateTextSub(str);
		this.getTextField().setText(str);
	}
	
	protected abstract void updateTextSub(final String str);

	protected abstract String createString();

	@Override
	public void insertUpdate(DocumentEvent e) {
		this.onDocumentUpdated(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		this.onDocumentUpdated(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		this.onDocumentUpdated(e);
	}
	
	private void onDocumentUpdated(DocumentEvent e) {
		Document doc = e.getDocument();
		if (doc.equals(this.getTextField().getDocument())) {
			if (this.mTextFieldFocused) {
				this.onTextUpdated(e);
			}
		} else {
			if (!this.mTextFieldFocused) {
				this.updateText();
			}
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
		this.mTextFieldFocused = source.equals(this.getTextField());
	}

	@Override
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
		this.mTextFieldFocused = !source.equals(this.getTextField());
	}

    @Override
    public void actionPerformed(ActionEvent e) {
    	super.actionPerformed(e);
        Object source = e.getSource();
        if (source.equals(this.getOKButton())) {
        	this.setCloseOption(OK_OPTION);
        	this.setVisible(false);
        } else if (source.equals(this.getCancelButton())) {
        	this.onCanceled();
        }
    }

}

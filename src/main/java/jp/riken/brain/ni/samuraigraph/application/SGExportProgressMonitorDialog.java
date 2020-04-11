package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Dialog;
import java.awt.Frame;

public class SGExportProgressMonitorDialog extends SGProgressMonitorDialog {

	private static final long serialVersionUID = -334002620408039254L;
	
	public static final String TITLE = "Exporting Images";

	public SGExportProgressMonitorDialog() {
		super();
		this.initProperty();
	}

	public SGExportProgressMonitorDialog(Frame parent, boolean modal) {
		super(parent, modal);
		this.initProperty();
	}

	public SGExportProgressMonitorDialog(Dialog parent, boolean modal) {
		super(parent, modal);
		this.initProperty();
	}

	private void initProperty() {
        this.setTitle(TITLE);
        this.setMessage("Exporting Images Now...");
        this.mProgressBar.setStringPainted(true);
	}
}

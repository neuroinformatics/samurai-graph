package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Dialog;
import java.awt.Frame;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SGDownloadMonitorDialog extends SGProgressMonitorDialog
		implements Runnable {

	private static final long serialVersionUID = -7273909006330864014L;

    public static final String TITLE = "Downloading Files";

	private InputStream mInputStream = null;

    private OutputStream mOutputStream = null;

    public void setInputStream(InputStream in) {
        this.mInputStream = in;
    }
    
    public void setOutputStream(OutputStream out) {
        this.mOutputStream = out;
    }

	public SGDownloadMonitorDialog() {
		super();
		this.initProperty();
	}

	public SGDownloadMonitorDialog(Frame parent, boolean modal) {
		super(parent, modal);
		this.initProperty();
	}

	public SGDownloadMonitorDialog(Dialog parent, boolean modal) {
		super(parent, modal);
		this.initProperty();
	}
	
	private void initProperty() {
		this.setTitle(TITLE);
        this.setMessage("Downloading Files Now...");
        this.mProgressBar.setStringPainted(true);
	}

	@Override
    public void run() {
        final int bufferLength = 1024;
        byte[] buffer = new byte[bufferLength];
        int len = 0;
        int dl = 0;
        InputStream in = this.mInputStream;
        OutputStream out = this.mOutputStream;
        try {
            // read from input stream
            while ((len = in.read(buffer, 0, bufferLength)) != -1) {
                out.write(buffer, 0, len);
                dl += len;

                // sets the progress
                this.setProgress(dl);

                // if download is canceled, break the loop
                if (this.isCanceled()) {
                    break;
                }
            }
        } catch (IOException ex) {
            return;
        } finally {
            // set invisible this dialog
            this.setVisible(false);
        }
    }

}

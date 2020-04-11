package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Component;
import java.awt.Toolkit;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * The class of the file chooser.
 */
public class SGFileChooser extends JFileChooser {

    /**
     * 
     */
    private static final long serialVersionUID = -7900924452421491559L;

    // Title of the confirmation dialog.
    private static final String TITLE_CONFIRM_OVERWRITE = "Overwrite Confirmation";

    // Message in the confirmation dialog.
    private static final String MSG_CONFIRM_OVERWRITE = "The file you have selected already exists.\n"
            + "Overwrite it?";

    /**
     * 
     */
    public SGFileChooser() {
        super();
    }

    /**
     * Overridden to avoid slow instantiation in XP if directory contains large
     * zip files.
     */
    /*
     * // Removed this overridden function because the file chooser doesn't work
     * normally. // We cannot move to other directories. public void updateUI() {
     * putClientProperty("FileChooser.useShellFolder", Boolean.FALSE);
     * super.updateUI(); }
     */

    /**
     * 
     * @param currentDirectory
     */
    public SGFileChooser(File currentDirectory) {
        super(currentDirectory);
    }

    /**
     * 
     * @param currentDirectoryPath
     */
    public SGFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
    }

    /**
     * Overridden to show confirmation dialog when the file already exists.
     */
    public void approveSelection() {
        // get the selected file
        File file = this.getSelectedFile();

        if (file != null) {
            //
            // check the file extension
            //

            FileFilter ff = this.getFileFilter();
            if (ff instanceof SGExtensionFileFilter) {
                SGExtensionFileFilter eff = (SGExtensionFileFilter) ff;
                List<String> eList = eff.getExtensionList(); // list of extensions

                String path = file.getAbsolutePath(); // path set to the file
                // chooser
                // if file does not have extension in lists
                // then append selected extension
                final String path_l = path.toLowerCase();
                boolean ext_found = false;
                for (String e : eList) {
                    if (path_l.endsWith(e.toLowerCase())) {
                        path = path.substring(0, path.length() - e.length())
                                + e;
                        ext_found = true;
                        break;
                    }
                }
                if (!ext_found) {
                    path += (String) eList.get(0);
                }
                file = new File(path);
                this.setSelectedFile(file);
            }

            // if selected file already exists
            if (this.getSelectedFile().exists()
                    && this.getDialogType() == JFileChooser.SAVE_DIALOG) {
                // beep
                Toolkit.getDefaultToolkit().beep();

                // show confirmation dialog
                int ret = JOptionPane.showConfirmDialog(this,
                        MSG_CONFIRM_OVERWRITE, TITLE_CONFIRM_OVERWRITE,
                        JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.YES_OPTION) {
                    super.approveSelection();
                }
            } else {
                super.approveSelection();
            }
        }
    }

    /**
     * Overridden not to select directories.
     * 
     * @param f
     *          file to be set
     */
    public void setSelectedFile(File f) {
        if (f != null && f.isDirectory()) {
            return;
        }
        super.setSelectedFile(f);
    }

    /**
     * Shows the file chooser dialog.
     * 
     * @param parent
     *           the parent of this file chooser
     * @param approveButtonText
     *           the text of the ApproveButton
     * @return the status
     */
    public int showDialog(Component parent, String approveButtonText) {
    	SGDrawingWindow wnd = null;
    	if (parent instanceof SGDrawingWindow) {
    		wnd = (SGDrawingWindow) parent;
    	}
        File old = this.getSelectedFile();
        if (wnd != null) {
        	wnd.setModalDialogShown(true);
        }
        final int ret = super.showDialog(parent, approveButtonText);
        switch (ret) {
        case JFileChooser.APPROVE_OPTION:
            // selected
            break;
        case JFileChooser.CANCEL_OPTION:
            // canceled
            this.setSelectedFile(old);
            break;
        case JFileChooser.ERROR_OPTION:
            // error
            this.setSelectedFile(old);
            break;
        }

        if (wnd != null) {
        	wnd.setModalDialogShown(false);
        }
        return ret;
    }

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        // protected to public
        super.firePropertyChange(propertyName, oldValue, newValue);
    }
}

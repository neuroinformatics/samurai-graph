package jp.riken.brain.ni.samuraigraph.application;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;


/**
 * A wizard dialog to choose a file.
 * This dialog has a button to show a file chooser and a text field to display the file name.
 */
public abstract class SGSingleFileChooserWizardDialog extends SGFileChooserWizardDialog 
    implements DropTargetListener, DocumentListener {

	private static final long serialVersionUID = -1648548028433673895L;

    /** Creates new form SGPropertyFileChooserWizardDialog */
    public SGSingleFileChooserWizardDialog(java.awt.Frame parent,
            boolean modal) {
        super(parent, modal);
        this.initProperty();
    }

    /** Creates new form SGPropertyFileChooserWizardDialog */
    public SGSingleFileChooserWizardDialog(java.awt.Dialog parent,
            boolean modal) {
        super(parent, modal);
        this.initProperty();
    }

    /**
     * Initialize this dialog.
     */
    private void initProperty() {

        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                // set drag and drop target
                DropTarget target = new DropTarget(getFileNameTextComponent(),
                        DnDConstants.ACTION_COPY_OR_MOVE, 
                        SGSingleFileChooserWizardDialog.this, true);
                setDropTarget(target);
                
                // disable the next button by default
                acceptFile(false);

                // add this dialog as an event listener
                if (null!=getFileNameTextComponent()) {
                    getFileNameTextComponent().getDocument().addDocumentListener(
                            SGSingleFileChooserWizardDialog.this);
                }
                if (null!=getFileChooserButton()) {
                    getFileChooserButton().addActionListener(
                            SGSingleFileChooserWizardDialog.this);
                }

                String fileName = getFileName();
                if (isAcceptable(fileName)) {
                    // enable the next button
                    acceptFile(true);
                }
            }
        });
        
    }
    
    /**
     * Returns a text component to input file name.
     * @return
     *         a text component to input file name
     */
    protected abstract JTextComponent getFileNameTextComponent();
    
    /**
     * Returns a button to show the file chooser.
     * @return
     *         a button to show the file chooser
     */
    protected abstract JButton getFileChooserButton();

    /**
     * 
     */
    public void dragEnter(final DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    /**
     * 
     */
    public void dragExit(final DropTargetEvent dte) {
    }

    /**
     * 
     */
    public void dragOver(final DropTargetDragEvent dtde) {
    }

    /**
     * Called when files are dropped onto this dialog. This method only sets the
     * file path to the text field.
     * 
     * @param dtde
     *            drop event
     */
    public void drop(final DropTargetDropEvent dtde) {
        
        // get dropped file list
        List<File> fileList = SGApplicationUtility.getDroppedFileList(dtde);
        if (fileList == null || fileList.size() == 0) {
            return;
        }
        
        // get only the first file
        File file = (File) fileList.get(0);

        // set to the text field
        this.setFileName(file);
    }
    
    /**
     * 
     */
    public void dropActionChanged(final DropTargetDragEvent dtde) {
    }
    
    
//    /**
//     * Called when the text field is edited.
//     * @param e
//     *          a caret event
//     */
//    public void caretUpdate(CaretEvent e) {
//        Object source = e.getSource();
//        if (source.equals(this.getFileNameTextComponent())) {
//            JTextComponent tc = (JTextComponent) source;
//            String str = tc.getText();
//            File f = new File(str);
//            
//            // enable or disable the next button
//            final boolean enable = this.isAcceptable(f);
//            this.acceptFile(enable);
//        }
//    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(this.getFileChooserButton())) {
        	String inputFileName = this.getFileName();
        	File inputFile = new File(inputFileName);
        	if (inputFile.exists()) {
            	String parent = inputFile.getParent();
            	if (parent != null) {
            		this.mCurrentDirectory = parent;
            	}
            	String name = inputFile.getName();
            	this.mCurrentFileName = name;
        	} else {
        		this.mCurrentFileName = "";
        	}
        	
            // show the file chooser and get files
            File[] files = this.openFileChooser();
            if (files == null) {
                return;
            }
            if (files.length != 1) {
                return;
            }

            // get a file from the file chooser
            File f = files[0];
            this.setFileName(f);

            if (this.isAcceptable(f)) {
                // enable the next button
                this.acceptFile(true);
            }
        }
        
        // calls the method of the super class
        super.actionPerformed(e);
    }

    /**
     * Sets a text string for file path to the text field.
     * 
     * @param f
     *            a file object whose path is to be displayed in the text field
     */
    protected void setFileName(final File f) {
        this.setFileName(f.getPath());
    }

    /**
     * Sets a text string for file path to the text field.
     * 
     * @param path
     *            a text string to be displayed in the text field
     */
    protected void setFileName(final String path) {
        this.getFileNameTextComponent().setText(path);
    }

    /**
     * Returns a text string for file path displayed in the text field.
     * 
     * @return a text string for file path displayed in the text field
     */
    protected String getFileName() {
        return this.getFileNameTextComponent().getText();
    }

    /**
     * Sets the file name and returns a file.
     * 
     * @param path
     *             file path
     * @return
     *             a file object
     */
    public File setSelectedFile(String path) {
        
        File f = super.setSelectedFile(path);

        // set to the text field
        if (f.isFile()) {
            this.setFileName(f);
        }
        
        return f;
    }
    
    @Override
    public void setCurrentFile(String dir, String name) {
    	super.setCurrentFile(dir, name);
    	if (dir != null && name != null) {
        	File f = new File(dir, name);
        	this.setFileName(f);
    	} else {
    		this.setFileName("");
    	}
    }
    
    /**
     * Accept or reject the given file.
     * @param b
     *        true if the file is accepted
     */
    protected void acceptFile(final boolean b) {
        // enable the next button
        this.getNextButton().setEnabled(b);
    }

    public void changedUpdate(DocumentEvent e) {
    }

    public void insertUpdate(DocumentEvent e) {
        this.onEdited(e);
    }

    public void removeUpdate(DocumentEvent e) {
        this.onEdited(e);
    }
    
    /**
     * Called when the text string in the text field is edited.
     * @param e
     *          a document event
     */
    protected void onEdited(DocumentEvent e) {
        
        // get a text string from a text field
        String text = this.getFileName();
        
        // check whether an input text string is acceptable
        final boolean enable = this.isAcceptable(text);
        
        // accept or reject
        this.acceptFile(enable);
    }

}

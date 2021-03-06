package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.SwingUtilities;

import jp.riken.brain.ni.samuraigraph.base.SGDialog;

/**
 * The dialog to display the attributes of a plug-in file.
 * 
 */
public class SGPluginInfoDialog extends SGDialog {

	private static final long serialVersionUID = -5571412361892370240L;
	
	public static final String TITLE = "Details of Plug-in Files";
	
	/** Creates new form SGPluginInfoDialog */
    public SGPluginInfoDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.initProperty();
    }

	/** Creates new form SGPluginInfoDialog */
    public SGPluginInfoDialog(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.initProperty();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mPluginInfoPanel = new jp.riken.brain.ni.samuraigraph.application.SGPluginInfoPanel();
        mButtonPanel = new javax.swing.JPanel();
        mOKButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();

        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));
        getContentPane().add(mPluginInfoPanel);

        mButtonPanel.setLayout(new javax.swing.BoxLayout(mButtonPanel, javax.swing.BoxLayout.LINE_AXIS));

        mOKButton.setText("OK");
        mOKButton.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        mButtonPanel.add(mOKButton);

        getContentPane().add(mButtonPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mButtonPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mOKButton;
    private jp.riken.brain.ni.samuraigraph.application.SGPluginInfoPanel mPluginInfoPanel;
    // End of variables declaration//GEN-END:variables

    private void initProperty() {
    	// sets the title
    	this.setTitle(TITLE);

    	this.setResizable(true);
    	
    	// sets the event listener
    	this.mOKButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			setVisible(false);
    		}
    	});
    	
        SwingUtilities.invokeLater(new Runnable() {
        	public void run() {
            	setPreferredSize(new Dimension(600, 400));
                pack();
                mPluginInfoPanel.setDividerLocation(0.5);
        	}
        });
	}
    
	@Override
	protected void onEscKeyTyped() {
        this.setVisible(false);
        this.setCloseOption(CANCEL_OPTION);
	}
	
	public void setPlugins(List<SGPluginFile> pluginList) {
		this.mPluginInfoPanel.setPlugins(pluginList);
	}
}

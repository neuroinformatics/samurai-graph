package jp.riken.brain.ni.samuraigraph.base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

/**
 * A dialog with a color chooser.
 * 
 */
public class SGColorDialog extends JDialog {

    private static final long serialVersionUID = 1972378173252289790L;
    
    public static final String TITLE = "Color Dialog";

    // a panel to set color chooser
    private JPanel mColorChooserPanel = new JPanel();

    // a color chooser object
    private JColorChooser mColorChooser = new JColorChooser();

    /**
     * Constructs a SGColorDialog.
     */
    public SGColorDialog(final Dialog parent) {
        super(parent, false);

        // initialize
        this.init();
    }

    private void init() {
    	this.setTitle(TITLE);
    	
        // hides the preview panel
        this.mColorChooser.setPreviewPanel(new JPanel());

        // adds the color chooser to a color chooser panel
        this.mColorChooserPanel.setLayout(new BorderLayout());
        this.mColorChooserPanel.add(this.mColorChooser, BorderLayout.CENTER);

        // sets color chooser panel to this dialog
        this.setContentPane(this.mColorChooserPanel);

        // disables resize
        this.setResizable(false);

        // enables to close this dialog when the Esc key is pressed
        AbstractAction act = new AbstractAction("") {
            private static final long serialVersionUID = 4743921657783211757L;
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };
        InputMap imap = this.getRootPane().getInputMap(
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close-it");
        this.getRootPane().getActionMap().put("close-it", act);
        
        // packs
        this.pack();
    }

    /**
     * Returns the color chooser.
     * 
     * @return JColorChooser object
     */
    public JColorChooser getColorChooser() {
        return this.mColorChooser;
    }

    /**
     * Returns the selected color.
     * 
     * @return the selected color
     */
    public Color getSelectedColor() {
    	return this.mColorChooser.getColor();
    }

    /**
     * Sets the selected color.
     * 
     * @param color
     *           the color to set
     */
    public void setSelectedColor(final Color color) {
    	this.mColorChooser.setColor(color);
    }

}

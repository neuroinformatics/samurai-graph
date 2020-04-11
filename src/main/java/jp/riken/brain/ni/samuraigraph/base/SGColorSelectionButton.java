package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

/**
 * A button used for the selection of the color.
 */
public class SGColorSelectionButton extends SGButton {

    private static final long serialVersionUID = -875675138016036356L;

    // a flag whether the color is set to this button
    private boolean mColorSetFlag = true;

    private Icon mColorSelectionButtonEmphasisIcon = null;

    static {
    	
    }
    /**
     * Creates a button.
     */
    public SGColorSelectionButton() {
        super();
        this.setBorder(new LineBorder(Color.BLACK, 1));
        
        // create background icon for the button
        this.mColorSelectionButtonEmphasisIcon = SGUtility.createIcon(this, "Lines.gif");
    }

    /**
     * Returns the background color if the color is "set" to this button.
     * 
     * @return Color object is the color is "set", otherwise null.
     */
    public Color getColor() {
        return this.mColorSetFlag ? this.getBackground() : null;
    }

    /**
     * If the color is set with this method, the color is "set" to this button.
     * 
     * @param cl
     *           color to be set to this button
     */
    public void setColor(final Color cl) {
        // if given object is not null, the color is "set"
        this.mColorSetFlag = (cl != null);
        
        // set as the background color
        setBackground(cl);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // set the tooltip text
            	String str = SGUtilityText.getSimpleColorString(cl);
                setToolTipText(str);
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (g != null && getModel().isEnabled()) {
            Insets insets = this.getInsets();
            Dimension dimComponent = this.getSize();
            int width = dimComponent.width - (insets.left + insets.right);
            int height = dimComponent.height - (insets.bottom + insets.top);
            g.setColor(getBackground());
            g.fillRect(insets.left, insets.top, width, height);
        }
    }
    
    
    /**
     * Returns the border for a focused color selection button.
     *
     * @return the border for a focused color selection button
     */
    private Border getFocusedBorder() {
        return new MatteBorder(mColorSelectionButtonEmphasisIcon);
    }
    
    public void setFocused(final boolean focused) {
    	Border border = focused ? this.getFocusedBorder() : this.getUnfocusedBorder();
    	this.setBorder(border);
    }

    /**
     * Returns the border for an unfocused color selection button.
     *
     * @return the border for an unfocused color selection button
     */
    private Border getUnfocusedBorder() {
        return new LineBorder(Color.BLACK, 1);
    }

}

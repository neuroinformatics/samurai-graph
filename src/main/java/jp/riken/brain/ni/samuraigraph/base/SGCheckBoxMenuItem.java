package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;

/**
 * The class of a menu item with check box which has the "indeterminate" property. 
 * If this object is in the "indeterminate" state, it is neither selected nor unselected. 
 * Note that the method isSelected, which is inherited from the super class JCheckBoxMenuItem,
 * returns false in the indeterminate state.
 * 
 */
public class SGCheckBoxMenuItem extends JCheckBoxMenuItem implements ActionListener {

	private static final long serialVersionUID = -7916666404708578271L;

    // A flag for the indeterminate state.
    private boolean mIndeterminateFlag = false;

    /**
     * Builds an object with given text string.
     * 
     * @param text
     *           a text string
     */
	public SGCheckBoxMenuItem(String text) {
		super(text);
		
        // add an event listener
        this.addActionListener(this);
	}

    /**
     * Sets the selected state.
     * 
     * @param b
     *            if b is equal to Boolean.TRUE, this check box is set to be
     *            selected. and if b equals to Boolean.FALSE, to be not
     *            selected. If b is equal to null, this check box is set to be in
     *            indeterminate state.
     */
    public void setSelected(final Boolean b) {
        this.setIndeterminate(b == null);
        if (b != null) {
            this.setSelected(b.booleanValue());
        } else {
            this.setSelected(false);
        }
    }

    /**
     * Returns the selected state.
     * 
     * @return if this check box is not in the indeterminate state, it equals to
     *          Boolean.TRUE or Boolean.FALSE. Otherwise, it equals to null.
     */
    public Boolean getSelected() {
        if (!this.mIndeterminateFlag) {
            return Boolean.valueOf(this.isSelected());
        }
        return null;
    }

    // Sets the indeterminate flag.
    private void setIndeterminate(final boolean b) {
        this.mIndeterminateFlag = b;
        Color cl = b ? Color.DARK_GRAY : (this.isEnabled() ? Color.BLACK : Color.GRAY);
        this.setForeground(cl);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(this)) {
            // if the box is checked, set the value is determined
            this.setIndeterminate(false);
        }
    }
}

package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

/**
 * The class of CheckBox  which has the "indeterminate" property. If this object 
 * is in the "indeterminate" state, it is neither selected nor unselected. 
 * Note that the method isSelected, which is inherited from the super class JCheckBox,
 * returns false in the indeterminate state.
 * 
 */
public class SGCheckBox extends JCheckBox implements ActionListener {

    private static final long serialVersionUID = -769949727938978175L;

    // A flag for the indeterminate state.
    private boolean mIndeterminateFlag = false;

    /**
     * The default constructor.
     */
    public SGCheckBox() {
        super();
        
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
     *         Boolean.TRUE or Boolean.FALSE. Otherwise, it equals to null.
     */
    public Boolean getSelected() {
        if (!this.mIndeterminateFlag) {
            return Boolean.valueOf(this.isSelected());
        }
        return null;
    }
    
    static Color INTERMEDIATE_COLOR = new Color(96, 96, 96);

    // Sets the indeterminate flag.
    private void setIndeterminate(final boolean b) {
        this.mIndeterminateFlag = b;
    	this.updateForegroundColor(this.isEnabled(), b);
    }
    
    // Overrode to update foreground color.
    @Override
    public void setEnabled(final boolean b) {
    	super.setEnabled(b);
    	this.updateForegroundColor(b, this.mIndeterminateFlag);
    }
    
    private void updateForegroundColor(final boolean enabled, final boolean indeterminate) {
        Color cl = indeterminate ? INTERMEDIATE_COLOR : (enabled ? Color.BLACK : Color.GRAY);
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

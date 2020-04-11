package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

/**
 * The menu item which displays selected or unselected state by its background
 * color.
 * 
 */
public class SGToggleMenuItem extends JMenuItem {

    /**
     * 
     */
    private static final long serialVersionUID = 6848991692223617976L;

    /**
     * 
     */
    public SGToggleMenuItem() {
        super();
        this.init();
    }

    /**
     * @param text
     */
    public SGToggleMenuItem(String text) {
        super(text);
        this.init();
    }

    /**
     * @param text
     * @param mnemonic
     */
    public SGToggleMenuItem(String text, int mnemonic) {
        super(text, mnemonic);
        this.init();
    }

    /**
     * @param a
     */
    public SGToggleMenuItem(Action a) {
        super(a);
        this.init();
    }

    /**
     * @param icon
     */
    public SGToggleMenuItem(Icon icon) {
        super(icon);
        this.init();
    }

    /**
     * @param text
     * @param icon
     */
    public SGToggleMenuItem(String text, Icon icon) {
        super(text, icon);
        this.init();
    }

    private Color mDefaultBackgroundColor = null;

    private void init() {
        this.mDefaultBackgroundColor = this.getBackground();

    }

    /**
     * 
     */
    public void setSelected(boolean b) {
        super.setSelected(b);

        // set background color
        if (b) {
            this.setBackground(Color.GRAY);
        } else {
            this.setBackground(this.mDefaultBackgroundColor);
        }
    }

}

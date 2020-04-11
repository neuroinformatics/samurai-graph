package jp.riken.brain.ni.samuraigraph.base;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * The original button class.
 * 
 * @author kuromaru
 * 
 */
public class SGButton extends JButton {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -8663053110453432386L;

    public SGButton() {
        super();
        this.init();
    }

    public SGButton(Icon icon) {
        super(icon);
        this.init();
    }

    public SGButton(String text) {
        super(text);
        this.init();
    }

    public SGButton(Action a) {
        super(a);
        this.init();
    }

    public SGButton(String text, Icon icon) {
        super(text, icon);
        this.init();
    }

    /**
     * Initialize this button.
     * 
     */
    private void init() {
    }

}

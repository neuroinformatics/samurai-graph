package jp.riken.brain.ni.samuraigraph.base;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

/**
 * The original radio button class.
 * 
 * @author kuromaru
 * 
 */
public class SGRadioButton extends JRadioButton 
//implements KeyListener, SGIConstants 
{

    /**
     * 
     */
    private static final long serialVersionUID = -5664751933445633492L;

    public SGRadioButton() {
        super();
        this.init();
    }

    public SGRadioButton(Icon icon) {
        super(icon);
        this.init();
    }

    public SGRadioButton(Action a) {
        super(a);
        this.init();
    }

    public SGRadioButton(String text) {
        super(text);
        this.init();
    }

    public SGRadioButton(Icon icon, boolean selected) {
        super(icon, selected);
        this.init();
    }

    public SGRadioButton(String text, boolean selected) {
        super(text, selected);
        this.init();
    }

    public SGRadioButton(String text, Icon icon) {
        super(text, icon);
        this.init();
    }

    public SGRadioButton(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
        this.init();
    }

    /**
     * Initialize this text field.
     * 
     */
    private void init() {
//        this.addKeyListener(this);
    }

//    public void keyPressed(KeyEvent e) {
//    }
//
//    public void keyReleased(KeyEvent e) {
//    }
//
//    /**
//     * Called when the key is typed.
//     * 
//     * @param e
//     *            the key event
//     */
//    public void keyTyped(KeyEvent e) {
//        final char c = e.getKeyChar();
//        if (c == KeyEvent.VK_ESCAPE) {
//            // when the escape key is typed, notify to the listeners
//            ActionEvent ae = new ActionEvent(this, 0, ESCAPE_KEY_TYPED);
//            ActionListener[] al = this.getActionListeners();
//            for (int ii = 0; ii < al.length; ii++) {
//                al[ii].actionPerformed(ae);
//            }
//        }
//    }

}

package jp.riken.brain.ni.samuraigraph.base;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractButton;

/**
 * A group of abstract buttons.
 */
public class SGButtonGroup implements ActionListener {

    private ArrayList mButtonList = new ArrayList();

    /**
     * The default constructor.
     */
    public SGButtonGroup() {
        super();
    }

    /**
     * Add to this button list.
     * 
     * @param btn
     *            a button to be added
     */
    public void add(AbstractButton btn) {
        this.mButtonList.add(btn);
        btn.addActionListener(this);
    }

    /**
     * Remove from this button list.
     * 
     * @param btn
     *            a button to be removed
     */
    public void remove(AbstractButton btn) {
        this.mButtonList.remove(btn);
        btn.removeActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        AbstractButton btn = (AbstractButton) e.getSource();
        ArrayList list = this.mButtonList;
        for (int ii = 0; ii < list.size(); ii++) {
            AbstractButton b = (AbstractButton) list.get(ii);
            if (btn.equals(b) == false) {
                b.setSelected(false);
            }
        }
    }

}

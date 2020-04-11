package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Dimension;
import java.awt.event.AdjustmentListener;

/**
 * A dialog to display the change log.
 * 
 */
public class SGChangeLogDialog extends SGScrollPaneDialog implements
        AdjustmentListener {

    /**
     * 
     */
    private static final long serialVersionUID = -6251793568672517832L;

    public static final String TITLE = "Change Log";

    /** Creates new form SGChangeLogDialog */
    public SGChangeLogDialog() {
        super();
        this.initProperty();
    }

    /** Creates new form SGChangeLogDialog */
    public SGChangeLogDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.initProperty();
    }

    /** Creates new form SGChangeLogDialog */
    public SGChangeLogDialog(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        this.initProperty();
    }

    private boolean initProperty() {
        this.setTitle(TITLE);

        // hide the cancel button
        this.mCancelButton.setVisible(false);

        // set the size of scroll pane
        this.mDetailScrollPane.setPreferredSize(new Dimension(304, 200));

        this.pack();

        return true;
    }

}

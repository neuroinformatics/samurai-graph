package jp.riken.brain.ni.samuraigraph.base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SGPatternPaintDialog extends JDialog
implements ActionListener, FocusListener, ChangeListener {

    private static final long serialVersionUID = 1735316951627888668L;

    protected SGPatternPaint mPatternPaint = null;

    private JPanel mPatternSelectionPanel = null;
    private JLabel mColorSelectionLabel = null;
    private SGColorSelectionButton mColorSelectionButton = null;

    private JLabel mPatternSelectionLabel = null;
    private SGComboBox mPatternSelectionComboBox = null;

    /**
     * The color selection dialog.
     *
     */
    protected SGColorDialog mColorDialog = null;

    /**
     * A color selection button currently used.
     *
     */
    protected SGColorSelectionButton mCurrentColorSetButton = null;

    public SGPatternPaintDialog(final JDialog parent, final String title,
            final boolean modal) {
        super(parent, title, modal);

        // initialize
        this.mPatternPaint = new SGPatternPaint();

        this.initComponents();
        this.initProperty();
        this.initialize();
    }

    private void initComponents() {
        getContentPane().setLayout(new BorderLayout());
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();

        this.mPatternSelectionPanel = new JPanel();
        this.mColorSelectionLabel = new JLabel();
        this.mColorSelectionButton = new SGColorSelectionButton();
        this.mPatternSelectionLabel = new JLabel();
        this.mPatternSelectionComboBox = new SGComboBox();

        this.mPatternSelectionPanel.setLayout(new GridBagLayout());

        mColorSelectionLabel.setText("Color");
        mColorSelectionLabel.setFont(new java.awt.Font("Dialog", 0, 11));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        this.mPatternSelectionPanel.add(mColorSelectionLabel, gridBagConstraints);

        mColorSelectionButton.setFont(new java.awt.Font("Dialog", 0, 11));
        mColorSelectionButton.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        this.mPatternSelectionPanel.add(mColorSelectionButton, gridBagConstraints);

        mPatternSelectionLabel.setText("Pattern");
        mPatternSelectionLabel.setFont(new java.awt.Font("Dialog", 0, 11));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        this.mPatternSelectionPanel.add(mPatternSelectionLabel, gridBagConstraints);

        mPatternSelectionComboBox.setPreferredSize(new java.awt.Dimension(140, 20));
        mPatternSelectionComboBox.setFont(new java.awt.Font("Dialog", 0, 11));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        this.mPatternSelectionPanel.add(mPatternSelectionComboBox, gridBagConstraints);

        add(this.mPatternSelectionPanel, BorderLayout.CENTER);
    }

    private boolean initProperty() {
        // add items to the inner pattern type combo box
        final String typeArray[] = SGPatternPaint.TYPE_PATTERN_NAMES;
        for (int ii = 0; ii < typeArray.length; ii++) {
            this.mPatternSelectionComboBox.addItem(typeArray[ii]);
        }

        this.pack();

        return true;
    }

    protected boolean initialize() {
        this.mColorSelectionButton.addActionListener(this);
        this.mColorSelectionButton.addFocusListener(this);
        this.mPatternSelectionComboBox.addActionListener(this);

        // create a color dialog
        this.mColorDialog = new SGColorDialog(this);

        // set this as a ChangeListener of the color selection model
        JColorChooser cc = this.mColorDialog.getColorChooser();
        ColorSelectionModel cModel = cc.getSelectionModel();
        cModel.addChangeListener(this);

        this.mColorSelectionButton.setColor(Color.BLACK);

        return true;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Invoked when the state is changed.
     *
     * @param e
     *          the changed event
     */
    @Override
    public void stateChanged(final ChangeEvent e) {
        Object source = e.getSource();
        if (this.mCurrentColorSetButton != null
                && (source instanceof ColorSelectionModel)) {
                // set the selected color to the color selection button
            Color cl = this.mColorDialog.getSelectedColor();
            this.mCurrentColorSetButton.setColor(cl);
            firePatternModelChanged();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(this.mColorSelectionButton)) {
            SGColorSelectionButton btn = (SGColorSelectionButton) source;
            
            // clears old color button
            if (this.mCurrentColorSetButton != null) {
                this.mCurrentColorSetButton.setFocused(false);
            }

            // sets to the attribute
            this.mCurrentColorSetButton = btn;
            
            // shows the color dialog
            final int x = this.getX() + 20;
            final int y = this.getY() + 20;
            SGUtility.showColorSelectionDialog(this.mColorDialog, btn, 
            		btn.getColor(), x, y);
        } else if (source.equals(this.mPatternSelectionComboBox)) {
            firePatternModelChanged();
        }
    }

    /**
     * Called when a component gained focus.
     * @param e
     *          focus event
     */
    @Override
    public void focusGained(FocusEvent e) {
        Object source = e.getSource();
        if (source instanceof SGColorSelectionButton) {
            SGColorSelectionButton btn = (SGColorSelectionButton) source;

            // set the border
            btn.setFocused(true);

            // set the current color button
            this.mCurrentColorSetButton = btn;
        }
    }

    /**
     * Called when a component lost focus.
     * @param e
     *          focus event
     */
    @Override
    public void focusLost(FocusEvent e) {
        Object source = e.getSource();
        if (source instanceof SGColorSelectionButton) {
            SGColorSelectionButton btn = (SGColorSelectionButton) source;
            Component opposite = e.getOppositeComponent();
            if (opposite == null) {
                return;
            }
            final boolean bcc = this.isColorChooserComponent(opposite);

            // if the opposite component is not the part of the color chooser dialog,
            // clear the focused button
            if (!bcc) {
                // set the border
                btn.setFocused(false);

                // clear the current color button
                this.mCurrentColorSetButton = null;
            }
        }
    }

    /**
     * Checks whether the given component is a part of the color chooser dialog.
     * @param com
     *             a component
     * @return
     *             true if the component is a part of the color chooser dialog
     */
    private boolean isColorChooserComponent(Component com) {
        Container parent = com.getParent();
        if (parent == null) {
            return false;
        }
        if (this.mColorDialog.equals(parent)) {
            return true;
        } else {
            return this.isColorChooserComponent(parent);
        }
    }

    public void setSelectedPatternPaint(final SGPatternPaint patternPaint) {
        if (!this.mPatternPaint.equals(patternPaint)) {
            try {
                this.mPatternPaint = (SGPatternPaint)patternPaint.clone();
            } catch (CloneNotSupportedException e) {
                throw new InternalError();
            }
        }
        Color color = patternPaint.getColor();
        int typeIndex = patternPaint.getTypeIndex();

        this.mColorSelectionButton.setColor(color);
        this.mPatternSelectionComboBox.setSelectedItem(SGPatternPaint.getTypeName(typeIndex));
    }

    public SGPatternPaint getPatternPaint() {
        Color color = this.mColorSelectionButton.getColor();
        int typeIndex = SGPatternPaint.getTypeFromName((String)this.mPatternSelectionComboBox.getSelectedItem());

        SGPatternPaint ppaint = new SGPatternPaint();
        ppaint.setColor(color);
        ppaint.setTypeIndex(typeIndex);

        return ppaint;
    }

    private List<ChangeListener> _listener = new ArrayList<ChangeListener>();

    public void addModelChangeListener(ChangeListener l) {
        if (!_listener.contains(l)) {
            _listener.add(l);
        }
    }

    private ChangeEvent _changeEvent = null;

    protected void firePatternModelChanged() {
        if (null==_changeEvent) {
            _changeEvent = new ChangeEvent(this);
        }
        for (ChangeListener l : _listener) {
            l.stateChanged(_changeEvent);
        }
    }
}

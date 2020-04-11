package jp.riken.brain.ni.samuraigraph.base;

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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Property dialog for gradation painting.
 * 
 * @author minemoto
 *
 */
public class SGGradationPaintDialog extends JDialog
implements ActionListener, FocusListener, ChangeListener {
    
    private static final long serialVersionUID = 3040335379897961866L;

    protected SGGradationPaint mGradationPaint = null;
    
    private JPanel mColorSelectionPanel = null;
    private JLabel mColorSelectionLabel1 = null;
    private JLabel mColorSelectionLabel2 = null;
    private SGColorSelectionButton mColorSelectionButton1 = null;
    private SGColorSelectionButton mColorSelectionButton2 = null;
    
    private JPanel mDirectionPanel = null;
    private SGRadioButton mDirectionVerticalRadioButton = null;
    private SGRadioButton mDirectionHorizontalRadioButton = null;
    private SGRadioButton mDirectionUpperRightRadioButton = null;
    private SGRadioButton mDirectionLowerRigthRadioButton = null;
    
    private JPanel mOrderPanel = null;
    private SGGradationPaintSelectionButton mOrderButtonPanel1 = null;
    private SGGradationPaintSelectionButton mOrderButtonPanel2 = null;
    private SGGradationPaintSelectionButton mOrderButtonPanel3 = null;
    private SGGradationPaintSelectionButton mOrderButtonPanel4 = null;
    
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
    
    /**
     * A gradient order selection button currently used.
     */
    protected SGGradationPaintSelectionButton mCurrentOrderSetButton = null;
    
    public SGGradationPaintDialog(final JDialog parent, final String title,
            final boolean modal) {
        super(parent, title, modal);

        // initialize
        this.mGradationPaint = new SGGradationPaint();
        
        this.initComponents();
        this.initProperty();
        this.initialize();
    }
    
    private void initComponents() {
        getContentPane().setLayout(new GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        
        this.mColorSelectionPanel = new JPanel();
        this.mColorSelectionLabel1 = new JLabel();
        this.mColorSelectionLabel2 = new JLabel();
        this.mColorSelectionButton1 = new SGColorSelectionButton();
        this.mColorSelectionButton2 = new SGColorSelectionButton();
        this.mDirectionPanel = new JPanel();
        this.mDirectionVerticalRadioButton = new SGRadioButton();
        this.mDirectionHorizontalRadioButton = new SGRadioButton();
        this.mDirectionUpperRightRadioButton = new SGRadioButton();
        this.mDirectionLowerRigthRadioButton = new SGRadioButton();
        this.mOrderPanel = new JPanel();
        this.mOrderButtonPanel1 = new SGGradationPaintSelectionButton();
        this.mOrderButtonPanel2 = new SGGradationPaintSelectionButton();
        this.mOrderButtonPanel3 = new SGGradationPaintSelectionButton();
        this.mOrderButtonPanel4 = new SGGradationPaintSelectionButton();
        
        mColorSelectionPanel.setLayout(new GridBagLayout());
        mColorSelectionPanel.setBorder(BorderFactory.createTitledBorder("Color"));
        
        mColorSelectionLabel1.setText("Color1");
        mColorSelectionLabel1.setFont(new java.awt.Font("Dialog", 0, 11));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        mColorSelectionPanel.add(mColorSelectionLabel1, gridBagConstraints);
        
        mColorSelectionLabel2.setText("Color2");
        mColorSelectionLabel2.setFont(new java.awt.Font("Dialog", 0, 11));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        mColorSelectionPanel.add(mColorSelectionLabel2, gridBagConstraints);
        
        mColorSelectionButton1.setFont(new java.awt.Font("Dialog", 0, 11));
        mColorSelectionButton1.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        mColorSelectionPanel.add(mColorSelectionButton1, gridBagConstraints);
        
        mColorSelectionButton2.setFont(new java.awt.Font("Dialog", 0, 11));
        this.mColorSelectionButton2.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        mColorSelectionPanel.add(mColorSelectionButton2, gridBagConstraints);
        
        mDirectionPanel.setLayout(new GridBagLayout());
        mDirectionPanel.setBorder(BorderFactory.createTitledBorder("Direction"));
        
        mDirectionVerticalRadioButton.setText("Vertical");
        mDirectionVerticalRadioButton.setFont(new java.awt.Font("Dialog", 0, 11));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        mDirectionPanel.add(mDirectionVerticalRadioButton, gridBagConstraints);
        
        mDirectionHorizontalRadioButton.setText("Horizontal");
        mDirectionHorizontalRadioButton.setFont(new java.awt.Font("Dialog", 0, 11));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        mDirectionPanel.add(mDirectionHorizontalRadioButton, gridBagConstraints);
        
        mDirectionUpperRightRadioButton.setText("Diagonal (upper right)");
        mDirectionUpperRightRadioButton.setFont(new java.awt.Font("Dialog", 0, 11));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        mDirectionPanel.add(mDirectionUpperRightRadioButton, gridBagConstraints);
        
        mDirectionLowerRigthRadioButton.setText("Diagonal (lower right)");
        mDirectionLowerRigthRadioButton.setFont(new java.awt.Font("Dialog", 0, 11));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        mDirectionPanel.add(mDirectionLowerRigthRadioButton, gridBagConstraints);
        
        mOrderPanel.setLayout(new GridBagLayout());
        mOrderPanel.setBorder(BorderFactory.createTitledBorder("Order"));
        
        mOrderButtonPanel1.setFont(new java.awt.Font("Dialog", 0, 11));
        mOrderButtonPanel1.setPreferredSize(new java.awt.Dimension(50, 50));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        mOrderPanel.add(mOrderButtonPanel1, gridBagConstraints);
        
        mOrderButtonPanel2.setFont(new java.awt.Font("Dialog", 0, 11));
        mOrderButtonPanel2.setPreferredSize(new java.awt.Dimension(50, 50));
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        mOrderPanel.add(mOrderButtonPanel2, gridBagConstraints);
        
        mOrderButtonPanel3.setFont(new java.awt.Font("Dialog", 0, 11));
        mOrderButtonPanel3.setPreferredSize(new java.awt.Dimension(50, 50));
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        mOrderPanel.add(mOrderButtonPanel3, gridBagConstraints);
        
        mOrderButtonPanel4.setFont(new java.awt.Font("Dialog", 0, 11));
        mOrderButtonPanel4.setPreferredSize(new java.awt.Dimension(50, 50));
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        mOrderPanel.add(mOrderButtonPanel4, gridBagConstraints);
        
        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(mColorSelectionPanel, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(mDirectionPanel, gridBagConstraints);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        add(mOrderPanel, gridBagConstraints);
        
    }
    
    private boolean initProperty() {
        final ButtonGroup group = new ButtonGroup();
        group.add(this.mDirectionVerticalRadioButton);
        group.add(this.mDirectionHorizontalRadioButton);
        group.add(this.mDirectionUpperRightRadioButton);
        group.add(this.mDirectionLowerRigthRadioButton);
        
        return true;
    }
    
    protected boolean initialize() {
        this.mColorSelectionButton1.addActionListener(this);
        this.mColorSelectionButton2.addActionListener(this);
        this.mColorSelectionButton1.addFocusListener(this);
        this.mColorSelectionButton2.addFocusListener(this);
        
        // create a color dialog
        this.mColorDialog = new SGColorDialog(this);

        // set this as a ChangeListener of the color selection model
        JColorChooser cc = this.mColorDialog.getColorChooser();
        ColorSelectionModel cModel = cc.getSelectionModel();
        cModel.addChangeListener(this);
        
        this.mDirectionHorizontalRadioButton.addActionListener(this);
        this.mDirectionVerticalRadioButton.addActionListener(this);
        this.mDirectionUpperRightRadioButton.addActionListener(this);
        this.mDirectionLowerRigthRadioButton.addActionListener(this);
        
        this.mOrderButtonPanel1.addActionListener(this);
        this.mOrderButtonPanel2.addActionListener(this);
        this.mOrderButtonPanel3.addActionListener(this);
        this.mOrderButtonPanel4.addActionListener(this);
        this.mOrderButtonPanel1.addFocusListener(this);
        this.mOrderButtonPanel2.addFocusListener(this);
        this.mOrderButtonPanel3.addFocusListener(this);
        this.mOrderButtonPanel4.addFocusListener(this);
        
        this.mColorSelectionButton1.setColor(Color.BLACK);
        this.mColorSelectionButton2.setColor(Color.WHITE);
        this.mDirectionVerticalRadioButton.setSelected(true);
        this.mCurrentOrderSetButton = this.mOrderButtonPanel1;
        this.mCurrentOrderSetButton.setFocused(true);
        
        return true;
    }
    
    @Override
    public void dispose() {
        super.dispose();
        
        this.mGradationPaint = null;
        
        this.mColorSelectionButton1 = null;
        this.mColorSelectionButton2 = null;
        this.mDirectionVerticalRadioButton = null;
        this.mDirectionHorizontalRadioButton = null;
        this.mDirectionUpperRightRadioButton = null;
        this.mDirectionLowerRigthRadioButton = null;
        
        this.mOrderButtonPanel1 = null;
        this.mOrderButtonPanel2 = null;
        this.mOrderButtonPanel3 = null;
        this.mOrderButtonPanel4 = null;
        
        this.mColorDialog.dispose();
        
        this.mCurrentColorSetButton = null;
        this.mCurrentOrderSetButton = null;
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
            updateOrderButtonPanel();
            fireGradationModelChanged();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source==this.mColorSelectionButton1 ||
                source==this.mColorSelectionButton2
                ) {
            if (this.mCurrentColorSetButton != null) {
                this.mCurrentColorSetButton.setFocused(false);
            }
            this.mCurrentColorSetButton = (SGColorSelectionButton)source;
            this.mCurrentColorSetButton.setFocused(true);
            if (!this.mColorDialog.isVisible()) {
                this.mColorDialog.setSelectedColor(this.mCurrentColorSetButton.getColor());
                this.mColorDialog.setLocation(this.getX() + 20, this.getY() + 20);
                this.mColorDialog.setVisible(true);
            }
        } else if (source==this.mOrderButtonPanel1 ||
                source==this.mOrderButtonPanel2 ||
                source==this.mOrderButtonPanel3 ||
                source==this.mOrderButtonPanel4
                ) {
            setCurrentOrderSetButton((SGGradationPaintSelectionButton)source);
            fireGradationModelChanged();
        } else if (source==this.mDirectionHorizontalRadioButton ||
                source==this.mDirectionVerticalRadioButton ||
                source==this.mDirectionUpperRightRadioButton ||
                source==this.mDirectionLowerRigthRadioButton
                ) {
            updateOrderButtonPanel();
            fireGradationModelChanged();
        }
    }
    
    private boolean updateOrderButtonPanel() {
        SGGradationPaint gpaint = new SGGradationPaint();
        Color color1 = this.mColorSelectionButton1.getColor();
        Color color2 = this.mColorSelectionButton2.getColor();
        int directionIndex = -1;
        if (this.mDirectionHorizontalRadioButton.isSelected()) {
            directionIndex = SGGradationPaint.INDEX_DIRECTION_HORIZONTAL;
        } else if (this.mDirectionVerticalRadioButton.isSelected()) {
            directionIndex = SGGradationPaint.INDEX_DIRECTION_VERTICAL;
        } else if (this.mDirectionUpperRightRadioButton.isSelected()) {
            directionIndex = SGGradationPaint.INDEX_DIRECTION_DIAGONAL_UP_RIGHT;
        } else if (this.mDirectionLowerRigthRadioButton.isSelected()) {
            directionIndex = SGGradationPaint.INDEX_DIRECTION_DIAGONAL_LOW_RIGHT;
        } else {
            return false;
        }
        gpaint.setColors(new Color[] { color1, color2 });
        gpaint.setDirection(directionIndex);
        gpaint.setOrder(SGGradationPaint.INDEX_ORDER_COLOR_1_2);
        this.mOrderButtonPanel1.setGradationPaint(gpaint);
        gpaint.setOrder(SGGradationPaint.INDEX_ORDER_COLOR_2_1);
        this.mOrderButtonPanel2.setGradationPaint(gpaint);
        gpaint.setOrder(SGGradationPaint.INDEX_ORDER_COLOR_1_2_1);
        this.mOrderButtonPanel3.setGradationPaint(gpaint);
        gpaint.setOrder(SGGradationPaint.INDEX_ORDER_COLOR_2_1_2);
        this.mOrderButtonPanel4.setGradationPaint(gpaint);
        return true;
    }
    
    private void setCurrentOrderSetButton(final SGGradationPaintSelectionButton button) {
        if (null!=this.mCurrentOrderSetButton) {
            this.mCurrentOrderSetButton.setFocused(false);
        }
        this.mCurrentOrderSetButton = button;
        this.mCurrentOrderSetButton.setFocused(true);
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

    public void setSelectedGradationPaint(final SGGradationPaint gradation) {
        if (gradation == null) {
        	return;
        }
        if (!this.mGradationPaint.equals(gradation)) {
            try {
                this.mGradationPaint = (SGGradationPaint)gradation.clone();
            } catch (CloneNotSupportedException e) {
                throw new InternalError();
            }
        }
        Color[] colors = gradation.getColors();
        int directionIndex = gradation.getDirectionIndex();
        int orderIndex = gradation.getOrderIndex();
        
        this.mColorSelectionButton1.setColor(colors[0]);
        this.mColorSelectionButton2.setColor(colors[1]);
        switch (directionIndex) {
        case SGGradationPaint.INDEX_DIRECTION_HORIZONTAL :
            this.mDirectionHorizontalRadioButton.setSelected(true);
            break;
        case SGGradationPaint.INDEX_DIRECTION_VERTICAL :
            this.mDirectionVerticalRadioButton.setSelected(true);
            break;
        case SGGradationPaint.INDEX_DIRECTION_DIAGONAL_UP_RIGHT :
            this.mDirectionUpperRightRadioButton.setSelected(true);
            break;
        case SGGradationPaint.INDEX_DIRECTION_DIAGONAL_LOW_RIGHT :
            this.mDirectionLowerRigthRadioButton.setSelected(true);
            break;
        default :
            break;
        }
        switch (orderIndex) {
        case SGGradationPaint.INDEX_ORDER_COLOR_1_2 :
            setCurrentOrderSetButton(this.mOrderButtonPanel1);
            break;
        case SGGradationPaint.INDEX_ORDER_COLOR_2_1 :
            setCurrentOrderSetButton(this.mOrderButtonPanel2);
            break;
        case SGGradationPaint.INDEX_ORDER_COLOR_1_2_1 :
            setCurrentOrderSetButton(this.mOrderButtonPanel3);
            break;
        case SGGradationPaint.INDEX_ORDER_COLOR_2_1_2 :
            setCurrentOrderSetButton(this.mOrderButtonPanel4);
            break;
        default :
            break;
        }
        updateOrderButtonPanel();
    }
    
    public SGGradationPaint getGradationPaint() {
        SGGradationPaint gpaint = new SGGradationPaint();
        Color color1 = this.mColorSelectionButton1.getColor();
        Color color2 = this.mColorSelectionButton2.getColor();
        int directionIndex = -1;
        if (this.mDirectionHorizontalRadioButton.isSelected()) {
            directionIndex = SGGradationPaint.INDEX_DIRECTION_HORIZONTAL;
        } else if (this.mDirectionVerticalRadioButton.isSelected()) {
            directionIndex = SGGradationPaint.INDEX_DIRECTION_VERTICAL;
        } else if (this.mDirectionUpperRightRadioButton.isSelected()) {
            directionIndex = SGGradationPaint.INDEX_DIRECTION_DIAGONAL_UP_RIGHT;
        } else if (this.mDirectionLowerRigthRadioButton.isSelected()) {
            directionIndex = SGGradationPaint.INDEX_DIRECTION_DIAGONAL_LOW_RIGHT;
        } else {
            return null;
        }
        
        int orderIndex = -1;
        if (this.mCurrentOrderSetButton==this.mOrderButtonPanel1) {
            orderIndex = SGGradationPaint.INDEX_ORDER_COLOR_1_2;
        } else if (this.mCurrentOrderSetButton==this.mOrderButtonPanel2) {
            orderIndex = SGGradationPaint.INDEX_ORDER_COLOR_2_1;
        } else if (this.mCurrentOrderSetButton==this.mOrderButtonPanel3) {
            orderIndex = SGGradationPaint.INDEX_ORDER_COLOR_1_2_1;
        } else if (this.mCurrentOrderSetButton==this.mOrderButtonPanel4) {
            orderIndex = SGGradationPaint.INDEX_ORDER_COLOR_2_1_2;
        } else {
            return null;
        }
        
        gpaint.setColors(new Color[] { color1, color2 });
        gpaint.setDirection(directionIndex);
        gpaint.setOrder(orderIndex);
        return gpaint;
    }
    
    private List<ChangeListener> _listener = new ArrayList<ChangeListener>();
    
    public void addModelChangeListener(ChangeListener l) {
        if (!_listener.contains(l)) {
            _listener.add(l);
        }
    }
    
    private ChangeEvent _changeEvent = null;
    
    protected void fireGradationModelChanged() {
        if (null==_changeEvent) {
            _changeEvent = new ChangeEvent(this);
        }
        for (ChangeListener l : _listener) {
            l.stateChanged(_changeEvent);
        }
    }

}

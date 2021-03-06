package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import jp.riken.brain.ni.samuraigraph.base.SGAxisSelectionPanel;
import jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton;
import jp.riken.brain.ni.samuraigraph.base.SGComponentGroup;
import jp.riken.brain.ni.samuraigraph.base.SGComponentGroupElement;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesDialog;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGTextField;
import jp.riken.brain.ni.samuraigraph.base.SGTwoAxesSelectionPanel;

/**
 * A dialog to set the properties of arrows.
 */
public class SGArrowDialog extends SGPropertyDialog
		implements SGITwoAxesDialog {

    private static final long serialVersionUID = 369310544895101136L;

    public static final String ARROW_TITLE = "Arrow Properties";
    
    /** Creates new form SGArrowDialog */
    public SGArrowDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.initProperty();
        this.initialize();
        this.pack();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mButtonPanel = new javax.swing.JPanel();
        mOKButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mCancelButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mPreviewButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mPanel = new javax.swing.JPanel();
        mFormLabel = new javax.swing.JLabel();
        mFormSeparator = new javax.swing.JSeparator();
        mLeftSpaceLabel = new javax.swing.JLabel();
        mLocationLabel = new javax.swing.JLabel();
        mLocationSeparator = new javax.swing.JSeparator();
        mArrowPanelContainer = new javax.swing.JPanel();
        mLocationPanel = new javax.swing.JPanel();
        mLeftXValueLabel = new javax.swing.JLabel();
        mRightXValueLabel = new javax.swing.JLabel();
        mLeftYValueLabel = new javax.swing.JLabel();
        mRightYValueLabel = new javax.swing.JLabel();
        mStartYPanel = new javax.swing.JPanel();
        mStartYValueTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mStartYDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mStartXPanel = new javax.swing.JPanel();
        mStartXValueTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mStartXDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mEndXPanel = new javax.swing.JPanel();
        mEndXValueTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mEndXDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mEndYPanel = new javax.swing.JPanel();
        mEndYValueTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mEndYDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mRightSpaceLabel = new javax.swing.JLabel();
        mLocationAnchoredCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
        mHeadPanel = new javax.swing.JPanel();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        mOKButton.setText("OK");
        mOKButton.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        mButtonPanel.add(mOKButton);

        mCancelButton.setText("Cancel");
        mCancelButton.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        mButtonPanel.add(mCancelButton);

        mPreviewButton.setText("Preview");
        mPreviewButton.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        mButtonPanel.add(mPreviewButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(mButtonPanel, gridBagConstraints);

        mPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        mPanel.setLayout(new java.awt.GridBagLayout());

        mFormLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mFormLabel.setText("Form");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mPanel.add(mFormLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        mPanel.add(mFormSeparator, gridBagConstraints);

        mLeftSpaceLabel.setText("    ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        mPanel.add(mLeftSpaceLabel, gridBagConstraints);

        mLocationLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mLocationLabel.setText("Location");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        mPanel.add(mLocationLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 5, 5);
        mPanel.add(mLocationSeparator, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        mPanel.add(mArrowPanelContainer, gridBagConstraints);

        mLocationPanel.setLayout(new java.awt.GridBagLayout());

        mLeftXValueLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mLeftXValueLabel.setText("Start X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 5);
        mLocationPanel.add(mLeftXValueLabel, gridBagConstraints);

        mRightXValueLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mRightXValueLabel.setText("End X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 5, 5);
        mLocationPanel.add(mRightXValueLabel, gridBagConstraints);

        mLeftYValueLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mLeftYValueLabel.setText("Start Y");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 5);
        mLocationPanel.add(mLeftYValueLabel, gridBagConstraints);

        mRightYValueLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mRightYValueLabel.setText("End Y");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 5, 5);
        mLocationPanel.add(mRightYValueLabel, gridBagConstraints);

        mStartYPanel.setLayout(new java.awt.GridBagLayout());

        mStartYValueTextField.setColumns(6);
        mStartYValueTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mStartYPanel.add(mStartYValueTextField, gridBagConstraints);

        mStartYDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mStartYPanel.add(mStartYDateButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mLocationPanel.add(mStartYPanel, gridBagConstraints);

        mStartXPanel.setLayout(new java.awt.GridBagLayout());

        mStartXValueTextField.setColumns(6);
        mStartXValueTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mStartXPanel.add(mStartXValueTextField, gridBagConstraints);

        mStartXDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mStartXPanel.add(mStartXDateButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mLocationPanel.add(mStartXPanel, gridBagConstraints);

        mEndXPanel.setLayout(new java.awt.GridBagLayout());

        mEndXValueTextField.setColumns(6);
        mEndXValueTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mEndXPanel.add(mEndXValueTextField, gridBagConstraints);

        mEndXDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mEndXPanel.add(mEndXDateButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mLocationPanel.add(mEndXPanel, gridBagConstraints);

        mEndYPanel.setLayout(new java.awt.GridBagLayout());

        mEndYValueTextField.setColumns(6);
        mEndYValueTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mEndYPanel.add(mEndYValueTextField, gridBagConstraints);

        mEndYDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mEndYPanel.add(mEndYDateButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mLocationPanel.add(mEndYPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        mPanel.add(mLocationPanel, gridBagConstraints);

        mRightSpaceLabel.setText("    ");
        mPanel.add(mRightSpaceLabel, new java.awt.GridBagConstraints());

        mLocationAnchoredCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mLocationAnchoredCheckBox.setLabel("Anchored");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        mPanel.add(mLocationAnchoredCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        getContentPane().add(mPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(mHeadPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mArrowPanelContainer;
    private javax.swing.JPanel mButtonPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mCancelButton;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mEndXDateButton;
    private javax.swing.JPanel mEndXPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mEndXValueTextField;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mEndYDateButton;
    private javax.swing.JPanel mEndYPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mEndYValueTextField;
    private javax.swing.JLabel mFormLabel;
    private javax.swing.JSeparator mFormSeparator;
    private javax.swing.JPanel mHeadPanel;
    private javax.swing.JLabel mLeftSpaceLabel;
    private javax.swing.JLabel mLeftXValueLabel;
    private javax.swing.JLabel mLeftYValueLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGCheckBox mLocationAnchoredCheckBox;
    private javax.swing.JLabel mLocationLabel;
    private javax.swing.JPanel mLocationPanel;
    private javax.swing.JSeparator mLocationSeparator;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mOKButton;
    private javax.swing.JPanel mPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mPreviewButton;
    private javax.swing.JLabel mRightSpaceLabel;
    private javax.swing.JLabel mRightXValueLabel;
    private javax.swing.JLabel mRightYValueLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mStartXDateButton;
    private javax.swing.JPanel mStartXPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mStartXValueTextField;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mStartYDateButton;
    private javax.swing.JPanel mStartYPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mStartYValueTextField;
    // End of variables declaration//GEN-END:variables

    private SGTwoAxesSelectionPanel mAxisPanel = new SGTwoAxesSelectionPanel();

    // a panel to set arrow properties
    private SGArrowPanel mArrowPanel = new SGArrowPanel();

    protected SGComponentGroup mDateXComponentGroup = new SGComponentGroup();

    protected SGComponentGroup mDateYComponentGroup = new SGComponentGroup();

    // the initialization method
    private void initProperty() {
    	
    	this.mHeadPanel.add(this.mAxisPanel);
    	this.mAxisPanel.addAxisSelectionListener(this);
        this.mArrowPanelContainer.add(this.mArrowPanel);

        // set the title
        this.setTitle(ARROW_TITLE);

        // set the name
        this.mStartXValueTextField.setDescription("Location-> StartX");
        this.mStartYValueTextField.setDescription("Location-> StartY");
        this.mEndXValueTextField.setDescription("Location-> EndX");
        this.mEndYValueTextField.setDescription("Location-> EndY");

        // add an action event listener
        this.mAxisPanel.addActionListener(this);
        this.mArrowPanel.addActionListener(this);
        this.mStartXDateButton.addActionListener(this);
        this.mStartYDateButton.addActionListener(this);
        this.mEndXDateButton.addActionListener(this);
        this.mEndYDateButton.addActionListener(this);
        
        SGComponentGroupElement[] dateXComponents = {
                new SGComponentGroupElement(this.mStartXDateButton),
                new SGComponentGroupElement(this.mEndXDateButton) };
        SGComponentGroupElement[] dateYComponents = {
                new SGComponentGroupElement(this.mStartYDateButton),
                new SGComponentGroupElement(this.mEndYDateButton) };
        this.mDateXComponentGroup.addElement(dateXComponents);
        this.mDateYComponentGroup.addElement(dateYComponents);
        
        // sets the calendar icon
        this.setCalendarIcon(this.mStartXDateButton);
        this.setCalendarIcon(this.mStartYDateButton);
        this.setCalendarIcon(this.mEndXDateButton);
        this.setCalendarIcon(this.mEndYDateButton);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	super.actionPerformed(e);
    	Object source = e.getSource();
    	if (source.equals(this.mStartXDateButton)) {
    		this.onDateButtonPressed(this.mStartXValueTextField);
    	} else if (source.equals(this.mStartYDateButton)) {
    		this.onDateButtonPressed(this.mStartYValueTextField);
    	} else if (source.equals(this.mEndXDateButton)) {
    		this.onDateButtonPressed(this.mEndXValueTextField);
    	} else if (source.equals(this.mEndYDateButton)) {
    		this.onDateButtonPressed(this.mEndYValueTextField);
    	}
    }

    /**
     * 
     */
    public List<SGColorSelectionButton> getColorSelectionButtonsList() {
        final List<SGColorSelectionButton> list = new ArrayList<SGColorSelectionButton>();
        list.addAll(this.mArrowPanel.getColorSelectionButtonsList());
        return list;
    }

    /**
     * 
     */
    public List getTextFieldComponentsList() {
        final List list = this.getFormattedTextFieldsListFromSpinners();
        list.addAll(this.getAxisNumberTextFieldList());
        return list;
    }

    /**
     * Returns a list of text fields to set number.
     * 
     * @return
     */
    public List<SGTextField> getAxisNumberTextFieldList() {
        final List<SGTextField> list = new ArrayList<SGTextField>();
        list.add(this.mStartXValueTextField);
        list.add(this.mEndXValueTextField);
        list.add(this.mStartYValueTextField);
        list.add(this.mEndYValueTextField);
        return list;
    }

    /**
     * 
     */
    public List getSpinnerList() {
        ArrayList list = new ArrayList();
        list.addAll(this.mArrowPanel.getSpinnerList());
        return list;
    }

    /**
     * 
     * @param l
     * @return
     */
    public boolean addPropertyDialogObserver(final SGIPropertyDialogObserver l) {
        super.addPropertyDialogObserver(l);
        if (l instanceof SGIArrowPanelObserver) {
            this.mArrowPanel.addObserver((SGIArrowPanelObserver) l);
        }
        return true;
    }

    /**
     * 
     * @param l
     * @return
     */
    public boolean removePropertyDialogObserver(
            final SGIPropertyDialogObserver l) {
        super.removePropertyDialogObserver(l);
        if (l instanceof SGIArrowPanelObserver) {
            this.mArrowPanel.removeObserver((SGIArrowPanelObserver) l);
        }
        return true;
    }

    /**
     * 
     * @return
     */
    public boolean removeAllPropertyDialogObserver() {
        super.removeAllPropertyDialogObserver();
        this.mArrowPanel.removeAllObserver();
        return true;
    }

    /**
     * 
     * @param value
     */
    public boolean setStartXValue(final Object obj) {
        return this.setValue(this.mStartXValueTextField, obj);
    }

    /**
     * 
     * @param value
     */
    public boolean setStartYValue(final Object obj) {
        return this.setValue(this.mStartYValueTextField, obj);
    }

    /**
     * 
     * @param value
     */
    public boolean setEndXValue(final Object obj) {
        return this.setValue(this.mEndXValueTextField, obj);
    }

    /**
     * 
     * @param value
     */
    public boolean setEndYValue(final Object obj) {
        return this.setValue(this.mEndYValueTextField, obj);
    }
    
    public boolean setAnchored(final Boolean b) {
        this.mLocationAnchoredCheckBox.setSelected(b);
        return true;
    }

    /**
     * 
     * @return
     */
    public Number getStartXValue() {
        return this.getNumber(this.mStartXValueTextField);
    }

    /**
     * 
     * @return
     */
    public Number getStartYValue() {
        return this.getNumber(this.mStartYValueTextField);
    }

    /**
     * 
     * @return
     */
    public Number getEndXValue() {
        return this.getNumber(this.mEndXValueTextField);
    }

    /**
     * 
     * @return
     */
    public Number getEndYValue() {
        return this.getNumber(this.mEndYValueTextField);
    }
    
    public Boolean getAnchored() {
        return this.mLocationAnchoredCheckBox.getSelected();
    }

    /**
     * 
     * @return
     */
    public boolean setDialogProperty() {
        List<SGIPropertyDialogObserver> list = this.mPropertyDialogObserverList;
        
        // select axis panel
        selectAxisPanel(this, this.mAxisPanel, list);

        // set properties to the date components
        setDateComponentProperties(this, this.mAxisPanel, list);

        // set to the arrow panel
        if (this.mArrowPanel.setComponentsProperty() == false) {
            return false;
        }

        SGIArrowDialogObserver arrow0 = (SGIArrowDialogObserver) list.get(0);

        final double startXValue0 = arrow0.getStartXValue();
        final double startYValue0 = arrow0.getStartYValue();
        final double endXValue0 = arrow0.getEndXValue();
        final double endYValue0 = arrow0.getEndYValue();
        final boolean anchored0 = arrow0.isAnchored();

        Double startXValue = Double.valueOf(startXValue0);
        Double startYValue = Double.valueOf(startYValue0);
        Double endXValue = Double.valueOf(endXValue0);
        Double endYValue = Double.valueOf(endYValue0);
        Boolean anchored = Boolean.valueOf(anchored0);

        if (list.size() > 1) {
            for (int ii = 1; ii < list.size(); ii++) {
                SGIArrowDialogObserver arrow1 = (SGIArrowDialogObserver) list
                        .get(ii);
                final double xValue1 = arrow1.getStartXValue();
                if (startXValue0 != xValue1) {
                    startXValue = null;
                    break;
                }
            }

            for (int ii = 1; ii < list.size(); ii++) {
                SGIArrowDialogObserver arrow1 = (SGIArrowDialogObserver) list
                        .get(ii);
                final double yValue1 = arrow1.getStartYValue();
                if (startYValue0 != yValue1) {
                    startYValue = null;
                    break;
                }
            }

            for (int ii = 1; ii < list.size(); ii++) {
                SGIArrowDialogObserver arrow1 = (SGIArrowDialogObserver) list
                        .get(ii);
                final double xValue1 = arrow1.getEndXValue();
                if (endXValue0 != xValue1) {
                    endXValue = null;
                    break;
                }
            }

            for (int ii = 1; ii < list.size(); ii++) {
                SGIArrowDialogObserver arrow1 = (SGIArrowDialogObserver) list
                        .get(ii);
                final double yValue1 = arrow1.getEndYValue();
                if (endYValue0 != yValue1) {
                    endYValue = null;
                    break;
                }
            }

            for (int ii = 1; ii < list.size(); ii++) {
                SGIArrowDialogObserver arrow1 = (SGIArrowDialogObserver) list
                        .get(ii);
                final boolean anchored1 = arrow1.isAnchored();
                if (anchored0 != anchored1) {
                    anchored = null;
                    break;
                }
            }
        }

        //
        this.setStartXValue(startXValue);
        this.setStartYValue(startYValue);
        this.setEndXValue(endXValue);
        this.setEndYValue(endYValue);
        this.setAnchored(anchored);

        return true;
    }
    
    /**
     * 
     */
    public boolean setPropertiesToObserver(SGIPropertyDialogObserver l) {
        SGIArrowDialogObserver arrow = (SGIArrowDialogObserver) l;

        // get values
        final int xLocation = this.mAxisPanel.getXAxisLocation();
        final int yLocation = this.mAxisPanel.getYAxisLocation();
        final Number startX = this.getStartXValue();
        final Number startY = this.getStartYValue();
        final Number endX = this.getEndXValue();
        final Number endY = this.getEndYValue();
        final Boolean anchored = this.getAnchored();

        final ArrayList<String> list = new ArrayList<String>();
        if (arrow.hasValidStartXValue(xLocation, startX) == false) {
            list.add("StartX");
        }
        if (arrow.hasValidStartYValue(yLocation, startY) == false) {
            list.add("StartY");
        }
        if (arrow.hasValidEndXValue(xLocation, endX) == false) {
            list.add("EndX");
        }
        if (arrow.hasValidEndYValue(yLocation, endY) == false) {
            list.add("EndY");
        }
        if (list.size() != 0) {
//            String msg = ERRMSG_AXIS_VALUE_INVALID + ":\n";
//            for (int ii = 0; ii < list.size(); ii++) {
//                String str = (String) list.get(ii);
//                msg += "- " + str + "\n";
//            }
        	StringBuffer sb = new StringBuffer();
        	sb.append(ERRMSG_AXIS_VALUE_INVALID);
        	sb.append(":\n");
            for (int ii = 0; ii < list.size(); ii++) {
                String str = (String) list.get(ii);
                sb.append("- ");
                sb.append(str);
                sb.append('\n');
            }
            this.setInputErrorMessage(sb.toString());
            return false;
        }

        // set the related axes
        if (xLocation != -1) {
            arrow.setXAxisLocation(xLocation);
        }

        if (yLocation != -1) {
            arrow.setYAxisLocation(yLocation);
        }

        if (startX != null) {
            arrow.setStartXValue(startX.doubleValue());
        }

        if (startY != null) {
            arrow.setStartYValue(startY.doubleValue());
        }

        if (endX != null) {
            arrow.setEndXValue(endX.doubleValue());
        }

        if (endY != null) {
            arrow.setEndYValue(endY.doubleValue());
        }
        
        if (anchored != null) {
            arrow.setAnchored(anchored.booleanValue());
        }

        // set with the arrow panel
        if (this.mArrowPanel
                .setPropertiesToListeners((SGIArrowPanelObserver) arrow) == false) {
            final String msg = this.mArrowPanel.getInputErrorMessage();
            if (msg != null) {
                this.setInputErrorMessage(msg);
            }
            return false;
        }

        return true;
    }

    /**
     * Returns the OK button.
     * @return
     *        the OK button
     */
    protected JButton getOKButton() {
        return this.mOKButton;
    }
    
    /**
     * Returns the cancel button.
     * @return
     *        the cancel button
     */
    protected JButton getCancelButton() {
        return this.mCancelButton;
    }
    
    /**
     * Returns the preview button.
     * @return
     *        the preview button
     */
    protected JButton getPreviewButton() {
        return this.mPreviewButton;
    }

    /**
     * Overrode this method to check the input values.
     * 
     * @return true if all input values are valid
     */
    protected boolean hasValidInputValues() {
        boolean valid = true;
        if (super.hasValidInputValues() == false) {
            valid = false;
        }
        if (this.hasValidNumber(this.mStartXValueTextField) == false) {
        	valid = false;
        }
        if (this.hasValidNumber(this.mStartYValueTextField) == false) {
        	valid = false;
        }
        if (this.hasValidNumber(this.mEndXValueTextField) == false) {
        	valid = false;
        }
        if (this.hasValidNumber(this.mEndYValueTextField) == false) {
        	valid = false;
        }
    	return valid;
    }

	@Override
	public void onXAxisDateSelected(boolean selected) {
		this.mDateXComponentGroup.setEnabled(selected);
	}

	@Override
	public void onYAxisDateSelected(boolean selected) {
		this.mDateYComponentGroup.setEnabled(selected);
	}
	
	@Override
	public void onAxisSelectionStateChanged(SGAxisSelectionPanel axisPanel) {
        // set properties to the date components
        setDateComponentProperties(this, this.mAxisPanel, 
        		this.mPropertyDialogObserverList);
	}

}

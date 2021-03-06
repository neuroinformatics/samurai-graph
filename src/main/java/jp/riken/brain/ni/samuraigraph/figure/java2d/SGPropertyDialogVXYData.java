package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;

import jp.riken.brain.ni.samuraigraph.base.SGAxisSelectionPanel;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesDialog;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTwoAxesSelectionPanel;
import jp.riken.brain.ni.samuraigraph.data.SGDataSetupDialog;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIIndexData;
import jp.riken.brain.ni.samuraigraph.data.SGINetCDFConstants;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataSetupDialog;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataSetupDialog;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataSetupDialog;

/**
 * A dialog to set the properties of two-dimensional vector type data.
 */
public class SGPropertyDialogVXYData extends SGDataDialog
		implements SGINetCDFConstants, SGITwoAxesDialog {

    // serialVersionUID
    private static final long serialVersionUID = -8568651377496230416L;

    /**
     * The title of this dialog.
     */
    public static final String TITLE = "Data Properties";

    /** Creates new form SGPropertyDialogVXYData */
    public SGPropertyDialogVXYData(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.initComponents();
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

        mCommonPanel = new javax.swing.JPanel();
        mLegendVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
        mNameLabel = new javax.swing.JLabel();
        mNameField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mMagnitudePerCMLabel = new javax.swing.JLabel();
        mMagnitudePerCMTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mDirectionInvariantRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
        mAmplitudeInvariantRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
        mInvariantModeLabel = new javax.swing.JLabel();
        mDataColumnSelectionButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mButtonPanel = new javax.swing.JPanel();
        mOKButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mCancelButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mPreviewButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mArrowPanel = new jp.riken.brain.ni.samuraigraph.figure.java2d.SGArrowPanel();
        mHeadPanel = new javax.swing.JPanel();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        mCommonPanel.setLayout(new java.awt.GridBagLayout());

        mLegendVisibleCheckBox.setText("Legend Visible");
        mLegendVisibleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        mCommonPanel.add(mLegendVisibleCheckBox, gridBagConstraints);

        mNameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        mNameLabel.setText("Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mCommonPanel.add(mNameLabel, gridBagConstraints);

        mNameField.setColumns(16);
        mNameField.setFont(new java.awt.Font("Dialog", 0, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        mCommonPanel.add(mNameField, gridBagConstraints);

        mMagnitudePerCMLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        mMagnitudePerCMLabel.setText("Magnitude per cm");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mCommonPanel.add(mMagnitudePerCMLabel, gridBagConstraints);

        mMagnitudePerCMTextField.setColumns(6);
        mMagnitudePerCMTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        mCommonPanel.add(mMagnitudePerCMTextField, gridBagConstraints);

        mDirectionInvariantRadioButton.setText("Direction");
        mDirectionInvariantRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mCommonPanel.add(mDirectionInvariantRadioButton, gridBagConstraints);

        mAmplitudeInvariantRadioButton.setText("Amplitude");
        mAmplitudeInvariantRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mCommonPanel.add(mAmplitudeInvariantRadioButton, gridBagConstraints);

        mInvariantModeLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        mInvariantModeLabel.setText("Invariant Mode");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mCommonPanel.add(mInvariantModeLabel, gridBagConstraints);

        mDataColumnSelectionButton.setText("Data Column");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        mCommonPanel.add(mDataColumnSelectionButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(mCommonPanel, gridBagConstraints);

        mOKButton.setText("OK");
        mOKButton.setFont(new java.awt.Font("Dialog", 1, 12));
        mButtonPanel.add(mOKButton);

        mCancelButton.setText("Cancel");
        mCancelButton.setFont(new java.awt.Font("Dialog", 1, 12));
        mButtonPanel.add(mCancelButton);

        mPreviewButton.setText("Preview");
        mPreviewButton.setFont(new java.awt.Font("Dialog", 1, 12));
        mButtonPanel.add(mPreviewButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(mButtonPanel, gridBagConstraints);

        mArrowPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        getContentPane().add(mArrowPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(mHeadPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private jp.riken.brain.ni.samuraigraph.base.SGRadioButton mAmplitudeInvariantRadioButton;
    private jp.riken.brain.ni.samuraigraph.figure.java2d.SGArrowPanel mArrowPanel;
    private javax.swing.JPanel mButtonPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mCancelButton;
    private javax.swing.JPanel mCommonPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mDataColumnSelectionButton;
    private jp.riken.brain.ni.samuraigraph.base.SGRadioButton mDirectionInvariantRadioButton;
    private javax.swing.JPanel mHeadPanel;
    private javax.swing.JLabel mInvariantModeLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGCheckBox mLegendVisibleCheckBox;
    private javax.swing.JLabel mMagnitudePerCMLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mMagnitudePerCMTextField;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mNameField;
    private javax.swing.JLabel mNameLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mOKButton;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mPreviewButton;
    // End of variables declaration//GEN-END:variables
    
    private SGTwoAxesSelectionPanel mAxisPanel = new SGTwoAxesSelectionPanel();

    private boolean initProperty() {

    	this.mHeadPanel.add(this.mAxisPanel);
    	this.mAxisPanel.addAxisSelectionListener(this);

        // set the title
        this.setTitle(SGPropertyDialogVXYData.TITLE);

        // create a button group
        ButtonGroup bg = new ButtonGroup();
        bg.add(this.mDirectionInvariantRadioButton);
        bg.add(this.mAmplitudeInvariantRadioButton);
        bg.add(this.mNoInvariantModeSelectionRadioButton);
        
        // add an action event listener
        this.mDataColumnSelectionButton.addActionListener(this);
        this.mArrowPanel.addActionListener(this);
        this.mAxisPanel.addActionListener(this);
        this.mAmplitudeInvariantRadioButton.addActionListener(this);
        this.mDirectionInvariantRadioButton.addActionListener(this);
        this.mLegendVisibleCheckBox.addActionListener(this);

        // set the name
        this.mNameField.setDescription("Name");
        this.mMagnitudePerCMTextField.setDescription("Magnitude per cm");

        return true;
    }

    /**
     * 
     */
    public java.util.List getColorSelectionButtonsList() {
        ArrayList list = new ArrayList();
        list.addAll(this.mArrowPanel.getColorSelectionButtonsList());
        return list;
    }

    /**
     * 
     */
    public java.util.List getAxisNumberTextFieldList() {
        ArrayList list = new ArrayList();
        return list;
    }

    /**
     * 
     */
    public java.util.List getSpinnerList() {
        ArrayList list = new ArrayList();
        list.addAll(this.mArrowPanel.getSpinnerList());
        return list;
    }

    /**
     * 
     */
    public java.util.List getTextFieldComponentsList() {
        final List list = this.getFormattedTextFieldsListFromSpinners();
        list.addAll(this.getAxisNumberTextFieldList());
        list.add(this.mNameField);
        list.add(this.mMagnitudePerCMTextField);
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
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Object source = e.getSource();
        String command = e.getActionCommand();
        if (source.equals(this.mDataColumnSelectionButton)) {
            this.showDataColumnSelectionDialog();
        } else if (source.equals(this.mDataColumnSelectionDialog)) {
            SGDataSetupDialog dg = (SGDataSetupDialog) source;
            if (OK_BUTTON_TEXT.equals(command)) {
            	SGDataColumnInfo[] colInfo = dg.getDataColumnInfoSet().getDataColumnInfoArray();
                this.mDataInfoArray = colInfo;
                
                // get stride
                SGIVXYDataDialogObserver obs = (SGIVXYDataDialogObserver) this.mPropertyDialogObserverList.get(0);
                SGData data = obs.getData();
                if (SGDataUtility.isSDArrayData(data)) {
                    SGSDArrayDataSetupDialog sdg = (SGSDArrayDataSetupDialog) dg;
                    SGIntegerSeriesSet stride = sdg.getVXYStride();
                    this.mStrideMap.put(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE, stride);
                } else if (SGDataUtility.isNetCDFData(data)) {
                    SGNetCDFDataSetupDialog ndg = (SGNetCDFDataSetupDialog) dg;
                    if (ndg.isIndexAvailable()) {
                        SGIntegerSeriesSet stride = ndg.getVXYIndexStride();
                        this.mStrideMap.put(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE, stride);
                    } else {
                        SGIntegerSeriesSet xStride = ndg.getVXYStrideX();
                        SGIntegerSeriesSet yStride = ndg.getVXYStrideY();
                        this.mStrideMap.put(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X, xStride);
                        this.mStrideMap.put(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y, yStride);
                    }
                } else if (SGDataUtility.isMDArrayData(data)) {
                	SGMDArrayDataSetupDialog mdg = (SGMDArrayDataSetupDialog) dg;
                    if (mdg.isVXYIndexAvailable()) {
                    	SGIntegerSeriesSet stride = mdg.getVXYIndexStride();
                        this.mStrideMap.put(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE, stride);
                    } else {
                        SGIntegerSeriesSet xStride = mdg.getVXYStrideX();
                        SGIntegerSeriesSet yStride = mdg.getVXYStrideY();
                        this.mStrideMap.put(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X, xStride);
                        this.mStrideMap.put(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y, yStride);
                    }
                }
            }
        }
    }
    
    /**
     * 
     * @return
     */
    public String getDataName() {
        return this.mNameField.getText();
    }

    /**
     * 
     * @param str
     * @return
     */
    public boolean setDataName(final String str) {
        this.mNameField.setText(str);
        return true;
    }

    /**
     * 
     * @return
     */
    public Boolean getLegendVisible() {
        return this.mLegendVisibleCheckBox.getSelected();
    }

    /**
     * 
     * @param b
     */
    public void setLegendVisible(final Boolean b) {
        this.mLegendVisibleCheckBox.setSelected(b);
    }

    /**
     * 
     */
    public boolean setDialogProperty() {
        List<SGIPropertyDialogObserver>  list = this.mPropertyDialogObserverList;
        final int len = list.size();

        // column selection button
        final boolean single = (len == 1);
        this.mDataColumnSelectionButton.setEnabled(single);

        // select axis panel
        selectAxisPanel(this, this.mAxisPanel, list);

        // set properties to the date components
        setDateComponentProperties(this, this.mAxisPanel, list);

        // data name
        String name = this.getDataNameFromObservers();
        this.setDataName(name);

        // visible in legend
        Boolean legendVisible = this.getLegendVisibleFromObservers();
        this.setLegendVisible(legendVisible);

        // scaling factor
        Float f = this.getScalingFactorFromObservers();
        this.setMagnitudePerCM(f);

        // invariance
        Boolean invariant = this.getInvarianceFromObservers();
        this.setDirectionInvariant(invariant);

        // arrow properties
        if (this.mArrowPanel.setComponentsProperty() == false) {
            return false;
        }

        return true;
    }

    /**
     * 
     * @param value
     */
    public boolean setMagnitudePerCM(final Object obj) {
        return this.setValue(this.mMagnitudePerCMTextField, obj);
    }

    /**
     * 
     * @return
     */
    public Number getMagnitudePerCM() {
        return this.getNumber(this.mMagnitudePerCMTextField);
    }

    //
    private Float getScalingFactorFromObservers() {
        List oList = this.mPropertyDialogObserverList;
        final int len = oList.size();
        if (len == 0) {
            return null;
        }

        ArrayList lList = new ArrayList(len);
        for (int ii = 0; ii < len; ii++) {
            SGIVXYDataDialogObserver l = (SGIVXYDataDialogObserver) oList
                    .get(ii);
            lList.add(Float.valueOf(l.getMagnitudePerCM()));
        }

        Float m0 = (Float) lList.get(0);
        Float m = m0;
        if (len > 1) {
            for (int ii = 1; ii < len; ii++) {
                Float m1 = (Float) lList.get(ii);
                if (m0.equals(m1) == false) {
                    m = null;
                    break;
                }
            }
        }

        return m;
    }

    private JRadioButton mNoInvariantModeSelectionRadioButton = new JRadioButton();

    /**
     * 
     */
    public Boolean isDirectionInvariant() {
        if (this.mDirectionInvariantRadioButton.isSelected()) {
            return Boolean.TRUE;
        } else if (this.mAmplitudeInvariantRadioButton.isSelected()) {
            return Boolean.FALSE;
        } else {
            return null;
        }
    }

    /**
     * 
     */
    public boolean setDirectionInvariant(final Object obj) {
        if (obj == null) {
            this.mNoInvariantModeSelectionRadioButton.setSelected(true);
            return false;
        }

        Boolean b = (Boolean) obj;
        if (b.booleanValue()) {
            this.mDirectionInvariantRadioButton.setSelected(true);
        } else {
            this.mAmplitudeInvariantRadioButton.setSelected(true);
        }

        return true;
    }

    //
    private Boolean getInvarianceFromObservers() {
        List oList = this.mPropertyDialogObserverList;
        final int len = oList.size();
        if (len == 0) {
            return null;
        }

        ArrayList lList = new ArrayList(len);
        for (int ii = 0; ii < len; ii++) {
            SGIVXYDataDialogObserver l = (SGIVXYDataDialogObserver) oList
                    .get(ii);
            lList.add(Boolean.valueOf(l.isDirectionInvariant()));
        }

        Boolean m0 = (Boolean) lList.get(0);
        Boolean m = m0;
        if (len > 1) {
            for (int ii = 1; ii < len; ii++) {
                Boolean m1 = (Boolean) lList.get(ii);
                if (m0.equals(m1) == false) {
                    m = null;
                    break;
                }
            }
        }

        return m;
    }

    protected boolean commit() {
    	if (!this.setDataColumnInfo()) {
    		return false;
    	}
    	return super.commit();
    }
    
    protected boolean preview() {
    	if (!this.setDataColumnInfo()) {
    		return false;
    	}
    	return super.preview();
    }
    
    private boolean setDataColumnInfo() {
        if (this.mDataInfoArray != null) {
        	SGIVXYDataDialogObserver l = (SGIVXYDataDialogObserver) this.mPropertyDialogObserverList.get(0);
        	SGData data = l.getData();

        	// sets the stride
            if (SGDataUtility.isSDArrayData(data)) {
            	SGIntegerSeriesSet stride = this.mStrideMap.get(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE);
            	if (!l.setSDArrayStride(stride)) {
            		return false;
            	}
            }
            if (data instanceof SGIIndexData) {
            	final boolean indexAvailable;
            	if (SGDataUtility.isNetCDFData(data)) {
                	SGNetCDFDataSetupDialog dg = (SGNetCDFDataSetupDialog) this.mDataColumnSelectionDialog;
            		indexAvailable = dg.isIndexAvailable();
            	} else if (SGDataUtility.isMDArrayData(data)) {
                	SGMDArrayDataSetupDialog dg = (SGMDArrayDataSetupDialog) this.mDataColumnSelectionDialog;
            		indexAvailable = dg.isVXYIndexAvailable();
            	} else {
            		return false;
            	}
            	if (indexAvailable) {
                	SGIntegerSeriesSet indexStride = this.mStrideMap.get(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE);
                	if (!l.setIndexStride(indexStride)) {
                		return false;
                	}
                	if (!l.setStrideX(null)) {
                		return false;
                	}
                	if (!l.setStrideY(null)) {
                		return false;
                	}
            	} else {
                	SGIntegerSeriesSet xStride = this.mStrideMap.get(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X);
                	if (!l.setStrideX(xStride)) {
                		return false;
                	}
                	SGIntegerSeriesSet yStride = this.mStrideMap.get(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y);
                	if (!l.setStrideY(yStride)) {
                		return false;
                	}
                	if (!l.setIndexStride(null)) {
                		return false;
                	}
            	}
            }
            l.setStrideAvailable(this.mDataColumnSelectionDialog.isStrideAvailable());

            // set data columns
            if (l.setColumnInfo(this.mDataInfoArray, this.mCommitActionStateMessage) == false) {
            	return false;
            }
        }
        return true;
    }
    
    public boolean setPropertiesToObserver(SGIPropertyDialogObserver l) {
        SGIVXYDataDialogObserver vxy = (SGIVXYDataDialogObserver) l;

        // set the related axes
        final int xLocation = this.mAxisPanel.getXAxisLocation();
        final int yLocation = this.mAxisPanel.getYAxisLocation();
        if (xLocation != -1) {
            vxy.setXAxisLocation(xLocation);
        }
        if (yLocation != -1) {
            vxy.setYAxisLocation(yLocation);
        }

        // data name
        String name = this.getDataName();
        if (name != null) {
            vxy.setName(name);
        }

        // visibility in legend
        Boolean legendVisible = this.getLegendVisible();
        if (legendVisible != null) {
            vxy.setVisibleInLegend(legendVisible.booleanValue());
        }

        // scaling factor
        Number factor = this.getMagnitudePerCM();
        if (factor != null) {
            vxy.setMagnitudePerCM(factor.floatValue());
        }

        // invariant mode
        Boolean invariant = this.isDirectionInvariant();
        if (invariant != null) {
            vxy.setDirectionInvariant(invariant.booleanValue());
        }

        // set with the arrow panel
        if (this.mArrowPanel.setPropertiesToListeners(vxy) == false) {
        	String msg = this.mArrowPanel.getInputErrorMessage();
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
     * Returns whether this dialog has valid input values.
     * 
     * @return true if all input values are valid
     */
    protected boolean hasValidInputValues() {
    	
        boolean valid = true;
        if (super.hasValidInputValues() == false) {
            valid = false;
        }
        
        // check the data name
        if (this.mNameField.hasValidText() == false) {
            this.addInputErrorDescription(this.mNameField.getDescription());
            valid = false;
        }
        
        // check the magnitude per cm
        if (this.hasValidNumber(this.mMagnitudePerCMTextField, Float.MIN_VALUE, null) == false) {
        	valid = false;
        }

        return valid;
    }

	@Override
	public void onXAxisDateSelected(boolean selected) {
		// do nothing
	}

	@Override
	public void onYAxisDateSelected(boolean selected) {
		// do nothing
	}

	@Override
	public void onAxisSelectionStateChanged(SGAxisSelectionPanel axisPanel) {
        // set properties to the date components
        setDateComponentProperties(this, this.mAxisPanel, 
        		this.mPropertyDialogObserverList);
	}

}

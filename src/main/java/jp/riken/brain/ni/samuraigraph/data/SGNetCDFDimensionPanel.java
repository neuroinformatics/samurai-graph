package jp.riken.brain.ni.samuraigraph.data;

import javax.swing.SwingUtilities;

import jp.riken.brain.ni.samuraigraph.data.SGSliderPanel.ISliderChangeListener;


/**
 * A panel for a dimension.
 *
 */
public class SGNetCDFDimensionPanel extends SGDimensionPanel
    implements ISliderChangeListener {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 5417060635224002801L;

    /**
     * The name of this dimension.
     */
    private String mName = null;

    /**
     * The range of values.
     */
    private double[] mValues = null;
    
    /**
     * The default constructor.
     *
     */
    public SGNetCDFDimensionPanel() {
        super();
        initComponents();
        this.initProperty();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mPanel = new javax.swing.JPanel();
        mWestPanel = new javax.swing.JPanel();
        mNameLabel = new javax.swing.JLabel();
        mWestPanelSouthSpaceLabel = new javax.swing.JLabel();
        mWestPanelWestSpaceLabel = new javax.swing.JLabel();
        mEastPanel = new javax.swing.JPanel();
        mValueTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mUnitLabel = new javax.swing.JLabel();
        mEastPanelSouthSpaceLabel = new javax.swing.JLabel();
        mEastPanelEastSpaceLabel = new javax.swing.JLabel();
        mCenterPanel = new javax.swing.JPanel();

        mPanel.setPreferredSize(new java.awt.Dimension(400, 36));
        mPanel.setLayout(new java.awt.GridBagLayout());

        setLayout(new java.awt.BorderLayout());

        mWestPanel.setLayout(new java.awt.GridBagLayout());

        mNameLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        mNameLabel.setText("Name");
        mNameLabel.setPreferredSize(new java.awt.Dimension(60, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        mWestPanel.add(mNameLabel, gridBagConstraints);

        mWestPanelSouthSpaceLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        mWestPanelSouthSpaceLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mWestPanelSouthSpaceLabel.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        mWestPanel.add(mWestPanelSouthSpaceLabel, gridBagConstraints);

        mWestPanelWestSpaceLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        mWestPanelWestSpaceLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mWestPanelWestSpaceLabel.setText("    ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        mWestPanel.add(mWestPanelWestSpaceLabel, gridBagConstraints);

        add(mWestPanel, java.awt.BorderLayout.WEST);

        mEastPanel.setLayout(new java.awt.GridBagLayout());

        mValueTextField.setColumns(6);
        mValueTextField.setEditable(false);
        mValueTextField.setText("Value");
        mValueTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        mEastPanel.add(mValueTextField, gridBagConstraints);

        mUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mUnitLabel.setText("Unit");
        mUnitLabel.setPreferredSize(new java.awt.Dimension(100, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mEastPanel.add(mUnitLabel, gridBagConstraints);

        mEastPanelSouthSpaceLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        mEastPanelSouthSpaceLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mEastPanelSouthSpaceLabel.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        mEastPanel.add(mEastPanelSouthSpaceLabel, gridBagConstraints);

        mEastPanelEastSpaceLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        mEastPanelEastSpaceLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mEastPanelEastSpaceLabel.setText("    ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        mEastPanel.add(mEastPanelEastSpaceLabel, gridBagConstraints);

        add(mEastPanel, java.awt.BorderLayout.EAST);

        mCenterPanel.setLayout(new java.awt.GridBagLayout());
        add(mCenterPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mCenterPanel;
    private javax.swing.JPanel mEastPanel;
    private javax.swing.JLabel mEastPanelEastSpaceLabel;
    private javax.swing.JLabel mEastPanelSouthSpaceLabel;
    private javax.swing.JLabel mNameLabel;
    private javax.swing.JPanel mPanel;
    private javax.swing.JLabel mUnitLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mValueTextField;
    private javax.swing.JPanel mWestPanel;
    private javax.swing.JLabel mWestPanelSouthSpaceLabel;
    private javax.swing.JLabel mWestPanelWestSpaceLabel;
    // End of variables declaration//GEN-END:variables
    
    private SGSliderPanel mSliderPanel;
    
    private void initProperty() {
    	// setup the slider panel
        mSliderPanel = new jp.riken.brain.ni.samuraigraph.data.SGSliderPanel();
        mCenterPanel.add(mSliderPanel, new java.awt.GridBagConstraints());
    	this.mSliderPanel.setSliderPreferredWidth(250);
    	this.addSliderChangeListener(this);
    }

    /**
     * Sets the name of dimension.
     * 
     * @param name
     *           the name to set
     */
    public void setDimensionName(final String name) {
        this.mName = name;
        this.mNameLabel.setText(name);
        this.mNameLabel.setToolTipText(name);
    }
    
    /**
     * Sets the values for the slider.
     * 
     * @param values
     *           the values
     * @param initIndex
     *           initial index of values
     */
    public void setValues(final double[] values, final int initIndex) {
    	super.setValues(values, initIndex);
        
        // sets values to an attribute
        this.mValues = values.clone();

        // sets the text
        this.setValueText(initIndex);
    }
    
    /**
     * Sets the unit text string.
     * 
     * @param unit
     *           a text string to set
     */
    public void setUnit(final String unit) {
        String unitString = null;
        if (unit == null) {
            unitString = "";
        } else {
            unitString = unit;
        }
        this.mUnitLabel.setText(unitString);
        this.mUnitLabel.setToolTipText(unitString);
    }

    /**
     * Sets the text of value.
     * 
     * @param index
     *           index of values
     */
    private void setValueText(final int index) {
    	if (this.mValues.length == 0 && index == 0) {
    		this.mValueTextField.setText("");
    		this.mValueTextField.setToolTipText(null);
    	} else {
//            final double value = this.mValues[index];
            final double value = SGDataUtility.getCoordinateVariableValue(
            		this.mValues, index);
            final String text = Double.toString(value);
            this.mValueTextField.setText(text);
            this.mValueTextField.setToolTipText(text);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    mValueTextField.setCaretPosition(0);
                }
            });
    	}
    }
    
    /**
     * Returns the current index.
     * 
     * @return the current index
     */
    public int getCurrentIndex() {
        return this.mSliderPanel.getCurrentIndex();
    }
    
    /**
     * Returns the current value.
     * 
     * @return the current value
     */
    public double getCurrentValue() {
        return this.mValues[this.getCurrentIndex()];
    }

    /**
     * Sets the components enabled.
     * 
     * @param enabled
     *           true to enable
     */
    public void setComponentsEnabled(final boolean enabled) {
        this.mNameLabel.setEnabled(enabled);
        this.mValueTextField.setEnabled(enabled);
        this.mSliderPanel.setComponentsEnabled(enabled);
    }

    /**
     * Returns the name of the dimension.
     * 
     * @return the name of the dimension
     */
    public String getDimensionName() {
    	if (this.mName != null) {
        	return this.mName;
    	} else {
    		return "";
    	}
    }
    
    /**
     * Sets the visibility of the components for the value.
     * 
     * @param visible
     *           true to set visible
     */
    public void setValueVisible(final boolean visible) {
    	this.mValueTextField.setVisible(visible);
    	this.mUnitLabel.setVisible(visible);
    }

    /**
     * Adds a listener for the change of slider.
     * 
     * @param l
     *          a listener
     */
    protected void addSliderChangeListener(SGSliderPanel.ISliderChangeListener l) {
    	this.mSliderPanel.addSliderChangeListener(l);
    }

	@Override
	public void changed(SGSliderPanel p) {
    	final int index = p.getCurrentIndex();
    	this.setValueText(index);
	}

    protected SGSliderPanel getSliderPanel() {
    	return this.mSliderPanel;
    }

}

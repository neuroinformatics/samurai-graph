package jp.riken.brain.ni.samuraigraph.data;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import jp.riken.brain.ni.samuraigraph.base.SGComponentGroup;
import jp.riken.brain.ni.samuraigraph.base.SGComponentGroupElement;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGInteger;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;

/**
 * The panel to set array indices.
 *
 */
public class SGIndexPanel extends javax.swing.JPanel implements DocumentListener,
		FocusListener, SGIConstants {

    private static final long serialVersionUID = -2603135691486810038L;

	/** Creates new form SGIndexPanel */
    public SGIndexPanel() {
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

        mStartLabel = new javax.swing.JLabel();
        mStartTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mEndLabel = new javax.swing.JLabel();
        mEndTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mStepLabel = new javax.swing.JLabel();
        mStepTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mIndexTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mLabelPanel = new javax.swing.JPanel();
        mTotalNumberLabel = new javax.swing.JLabel();
        mTotalNumberTitleLabel = new javax.swing.JLabel();
        mEndValueTitleLabel = new javax.swing.JLabel();
        mEndValueLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        mStartLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        mStartLabel.setText("Start");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(mStartLabel, gridBagConstraints);

        mStartTextField.setColumns(3);
        mStartTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        mStartTextField.setMinimumSize(new java.awt.Dimension(39, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        add(mStartTextField, gridBagConstraints);

        mEndLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        mEndLabel.setText("End");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        add(mEndLabel, gridBagConstraints);

        mEndTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        mEndTextField.setMinimumSize(new java.awt.Dimension(39, 22));
        mEndTextField.setPreferredSize(new java.awt.Dimension(39, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(mEndTextField, gridBagConstraints);

        mStepLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        mStepLabel.setText("Step");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        add(mStepLabel, gridBagConstraints);

        mStepTextField.setColumns(3);
        mStepTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        mStepTextField.setMinimumSize(new java.awt.Dimension(39, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        add(mStepTextField, gridBagConstraints);

        mIndexTextField.setColumns(3);
        mIndexTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        mIndexTextField.setMinimumSize(new java.awt.Dimension(39, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        add(mIndexTextField, gridBagConstraints);

        mLabelPanel.setLayout(new java.awt.GridBagLayout());

        mTotalNumberLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTotalNumberLabel.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mLabelPanel.add(mTotalNumberLabel, gridBagConstraints);

        mTotalNumberTitleLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTotalNumberTitleLabel.setText("total number = ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        mLabelPanel.add(mTotalNumberTitleLabel, gridBagConstraints);

        mEndValueTitleLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mEndValueTitleLabel.setText("end = ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        mLabelPanel.add(mEndValueTitleLabel, gridBagConstraints);

        mEndValueLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mEndValueLabel.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mLabelPanel.add(mEndValueLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(mLabelPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel mEndLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mEndTextField;
    private javax.swing.JLabel mEndValueLabel;
    private javax.swing.JLabel mEndValueTitleLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mIndexTextField;
    private javax.swing.JPanel mLabelPanel;
    private javax.swing.JLabel mStartLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mStartTextField;
    private javax.swing.JLabel mStepLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mStepTextField;
    private javax.swing.JLabel mTotalNumberLabel;
    private javax.swing.JLabel mTotalNumberTitleLabel;
    // End of variables declaration//GEN-END:variables

    /**
     * The flag whether the index text field is selected.
     */
    private boolean mIndexSelected;

    private SGComponentGroup mAllGroup = new SGComponentGroup();

    /**
     * The length of index array.
     */
    private int mLength = 0;

    /**
     * The alias map.
     */
    private Map<String, Integer> mAliasMap = new HashMap<String, Integer>();

    private void initProperty() {
    	this.addDocumentListener(this);
        this.mIndexTextField.addFocusListener(this);
        this.mStartTextField.addFocusListener(this);
        this.mEndTextField.addFocusListener(this);
        this.mStepTextField.addFocusListener(this);

        // creates a map of component groups
        Map<Component, SGComponentGroupElement> compMap = new HashMap<Component, SGComponentGroupElement>();
        this.addComponentGroupMap(this.mStartLabel, compMap);
        this.addComponentGroupMap(this.mStartTextField, compMap);
        this.addComponentGroupMap(this.mEndLabel, compMap);
        this.addComponentGroupMap(this.mEndTextField, compMap);
        this.addComponentGroupMap(this.mStepLabel, compMap);
        this.addComponentGroupMap(this.mStepTextField, compMap);
        this.addComponentGroupMap(this.mIndexTextField, compMap);
        this.addComponentGroupMap(this.mEndValueTitleLabel, compMap);
        this.addComponentGroupMap(this.mEndValueLabel, compMap);
        this.addComponentGroupMap(this.mTotalNumberTitleLabel, compMap);
        this.addComponentGroupMap(this.mTotalNumberLabel, compMap);

		List<SGComponentGroupElement> indexComponents = new ArrayList<SGComponentGroupElement>();
		indexComponents.add(compMap.get(this.mIndexTextField));

		List<SGComponentGroupElement> allComponents = new ArrayList<SGComponentGroupElement>();
		allComponents.add(compMap.get(this.mStartLabel));
		allComponents.add(compMap.get(this.mStartTextField));
		allComponents.add(compMap.get(this.mEndLabel));
		allComponents.add(compMap.get(this.mEndTextField));
		allComponents.add(compMap.get(this.mStepLabel));
		allComponents.add(compMap.get(this.mStepTextField));
		allComponents.add(compMap.get(this.mEndValueTitleLabel));
		allComponents.add(compMap.get(this.mEndValueLabel));
		allComponents.add(compMap.get(this.mTotalNumberTitleLabel));
		allComponents.add(compMap.get(this.mTotalNumberLabel));
		allComponents.addAll(indexComponents);

		// add elements to component groups
        this.mAllGroup.addElement(allComponents);

        // initializes the number label
    	this.setTotalNumberLabel(-1);
    	this.setLength(-1);

        this.mIndexSelected = true;
    }

    private void addComponentGroupMap(Component comp, Map<Component, SGComponentGroupElement> map) {
    	map.put(comp, new SGComponentGroupElement(comp));
    }

    /**
     * Overrode to set enabled inner components.
     *
     * @param enabled
     *           true to set enabled
     */
    @Override
    public void setEnabled(final boolean enabled) {
    	super.setEnabled(enabled);
    	this.setInnerComponentsEnabled(enabled);
    }

    /**
     * Sets all inner components enabled.
     *
     * @param enabled
     *           true to set enabled
     */
    public void setInnerComponentsEnabled(final boolean enabled) {
    	this.mAllGroup.setEnabled(enabled);
    }

    /**
     * Sets the indices.
     *
     * @param indices
     *           the indices
     */
    public void setIndices(SGIntegerSeriesSet indices) {
    	final boolean indicesValid = (indices != null);
    	if (indicesValid) {
    		SGIntegerSeries series = indices.testReduce();
    		final boolean seriesValid = (series != null);
    		if (seriesValid) {
    	    	this.setNumber(series);
    		} else {
    	    	this.clearIndexFields();
    		}
    		this.mIndexTextField.setText(indices.toString());
    		this.setTotalNumberLabel(indices.getLength());
    	} else {
    		this.clearAll();
    	}
    }

    /**
     * Sets the indices.
     *
     * @param series
     *           the indices
     */
    public void setIndices(SGIntegerSeries series) {
    	this.setIndices(new SGIntegerSeriesSet(series));
    }

    private void setNumber(SGIntegerSeries series) {
		final SGInteger start = series.getStart();
		final SGInteger end = series.getEnd();
		final SGInteger step = series.getStep();
    	this.setNumber(start, end, step);
    }

    private void setNumber(final SGInteger start, final SGInteger end, final SGInteger step) {
    	this.setNumber(this.mStartTextField, start);
    	this.setNumber(this.mEndTextField, end);
    	this.setNumber(this.mStepTextField, step);
    }

    private void setNumber(JTextField tf, final SGInteger num) {
    	tf.setText(num.toString());
    }

    private SGInteger getInteger(JTextField tf) {
        String str = tf.getText();
        if (str == null) {
            return null;
        }
        return SGInteger.parse(str, this.mAliasMap);
    }

    /**
     * Returns the start index.
     *
     * @return the start index
     */
    public SGInteger getStart() {
    	return this.getInteger(this.mStartTextField);
    }

    /**
     * Returns the end index.
     *
     * @return the end index
     */
    public SGInteger getEnd() {
    	return this.getInteger(this.mEndTextField);
    }

    /**
     * Returns the step value.
     *
     * @return the step value
     */
    public SGInteger getStep() {
    	return this.getInteger(this.mStepTextField);
    }

    /**
     * Returns the indices.
     *
     * @return the indices
     */
    public SGIntegerSeriesSet getIndices() {
    	SGIntegerSeriesSet indices = null;
    	if (this.isIndexSelected()) {
        	indices = SGIntegerSeriesSet.parse(this.mIndexTextField.getText(), this.mAliasMap);
    	} else {
            SGInteger start = this.getStart();
            SGInteger end = this.getEnd();
            SGInteger step = this.getStep();
            if (start != null && end != null && step != null) {
            	if (SGIntegerSeriesSet.isValidInput(start, end, step, this.mAliasMap)) {
                	indices = new SGIntegerSeriesSet(start, end, step);
            	}
            }
    	}
    	return indices;
    }

    /**
     * Returns whether input values are valid.
     *
     * @return true if input values are valid
     */
    public boolean hasValidInput() {
    	String text = this.mIndexTextField.getText();
    	SGIntegerSeriesSet stride = SGIntegerSeriesSet.parse(text, this.mAliasMap);
    	if (stride == null) {
    		return false;
    	}
    	final int[] indices = stride.getNumbers();
    	for (int ii = 0; ii < indices.length; ii++) {
    		if (indices[ii] < 0) {
    			return false;
    		}
    	}
    	if (this.mLength > 0) {
	    	for (int ii = 0; ii < indices.length; ii++) {
	    		if (this.mLength <= indices[ii]) {
	    			return false;
	    		}
	    	}
    	}
		return true;
    }

    private void addDocumentListener(DocumentListener l, JTextField tf) {
    	tf.getDocument().addDocumentListener(l);
    }

    /**
     * Adds a document listener.
     *
     * @param l
     *          a document listener
     */
    public void addDocumentListener(DocumentListener l) {
    	this.addDocumentListener(l, this.mStartTextField);
    	this.addDocumentListener(l, this.mEndTextField);
    	this.addDocumentListener(l, this.mStepTextField);
    	this.addDocumentListener(l, this.mIndexTextField);
    }

	@Override
	public void insertUpdate(DocumentEvent e) {
		this.onDocumentUpdated(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		this.onDocumentUpdated(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		this.onDocumentUpdated(e);
	}

	private void onDocumentUpdated(DocumentEvent e) {
		Document doc = e.getDocument();
		if (doc.equals(this.mIndexTextField.getDocument())) {
			if (this.isIndexSelected()) {
				this.onIndexSeriesUpdated(e);
			}
		} else if (doc.equals(this.mStartTextField.getDocument())
				|| doc.equals(this.mEndTextField.getDocument())
				|| doc.equals(this.mStepTextField.getDocument())) {
			if (!this.isIndexSelected()) {
				this.onIndexUpdated(e);
			}
		}
	}

	private void onIndexSeriesUpdated(DocumentEvent e) {
		Document doc = e.getDocument();
		String text = null;
		try {
			text = doc.getText(0, doc.getLength());
		} catch (BadLocationException e1) {
			return;
		}
		int num = -1;
		SGIntegerSeriesSet indices = SGIntegerSeriesSet.parse(text, this.mAliasMap);
		if (indices != null) {
			SGIntegerSeries series = indices.testReduce();
			if (series != null) {
            	this.setNumber(series);
			} else {
				this.clearIndexFields();
			}
			num = indices.getLength();
		} else {
			this.clearIndexFields();
		}
		this.setTotalNumberLabel(num);
	}

	private void onIndexUpdated(DocumentEvent e) {
		SGInteger start = this.getStart();
		SGInteger end = this.getEnd();
		SGInteger step = this.getStep();
		final String str;
		int num = -1;
		if (start != null && end != null && step != null) {
			if (SGIntegerSeriesSet.isValidInput(start, end, step, this.mAliasMap)) {
				SGIntegerSeriesSet indices = new SGIntegerSeriesSet(start, end, step);
				str = indices.toString();
				num = indices.getLength();
			} else {
				str = "";
			}
		} else {
			str = "";
		}
		this.mIndexTextField.setText(str);
		this.setTotalNumberLabel(num);
	}

	private void setTotalNumberLabel(final int num) {
		if (num < 0) {
			this.mTotalNumberLabel.setVisible(false);
		} else {
			this.mTotalNumberLabel.setVisible(true);
			this.mTotalNumberLabel.setText(Integer.toString(num));
		}
	}

	private boolean isIndexSelected() {
		return this.mIndexSelected;
	}

    private void clearIndexFields() {
    	this.mStartTextField.setText("");
    	this.mEndTextField.setText("");
    	this.mStepTextField.setText("");
    }

    /**
     * Clears all input values.
     *
     */
    public void clearAll() {
    	this.mIndexTextField.setText("");
    	this.clearIndexFields();
    	this.setLength(-1);
    	this.setTotalNumberLabel(-1);
    }

    /**
     * Overrode to set the flag whether the index text field is selected.
     *
     * @param e
     *          the focus event
     */
	@Override
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
		if (source.equals(this.mStartTextField)
				|| source.equals(this.mEndTextField)
				|| source.equals(this.mStepTextField)) {
			this.mIndexSelected = false;
		}
	}

    /**
     * Overrode to set the flag whether the index text field is selected.
     *
     * @param e
     *          the focus event
     */
	@Override
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
		if (source.equals(this.mStartTextField)
				|| source.equals(this.mEndTextField)
				|| source.equals(this.mStepTextField)) {
			this.mIndexSelected = true;
		}
	}

	/**
	 * Returns the length of index array.
	 *
	 * @return the length of index array
	 */
	public int getLength() {
		return this.mLength;
	}

	/**
	 * Sets the length of index array.
	 *
	 * @param len
	 *          the length to set
	 */
	public void setLength(final int len) {
		this.mLength = len;
		if (len > 0) {
			final int end = len - 1;
			this.mAliasMap.put(SGIntegerSeries.ARRAY_INDEX_END, end);
			this.mEndValueLabel.setText(Integer.toString(end));
			this.mEndValueLabel.setVisible(true);
		} else {
			this.mEndValueLabel.setVisible(false);
		}
	}

	/**
	 * Returns whether a given document is the same instance with that of a text field in this panel.
	 *
	 * @param doc
	 *           a document
	 * @return true if a given document is the same instance with that of a text field in this panel
	 */
	public boolean hasDocument(Document doc) {
		if (this.mStartTextField.getDocument().equals(doc)) {
			return true;
		}
		if (this.mEndTextField.getDocument().equals(doc)) {
			return true;
		}
		if (this.mStepTextField.getDocument().equals(doc)) {
			return true;
		}
		if (this.mIndexTextField.getDocument().equals(doc)) {
			return true;
		}
		return false;
	}

	public void setTextFieldForeground(Color cl) {
		this.mStartTextField.setForeground(cl);
		this.mEndTextField.setForeground(cl);
		this.mStepTextField.setForeground(cl);
		this.mIndexTextField.setForeground(cl);
	}
}

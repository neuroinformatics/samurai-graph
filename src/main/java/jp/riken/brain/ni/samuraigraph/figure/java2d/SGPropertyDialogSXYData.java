package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

import jp.riken.brain.ni.samuraigraph.base.SGAxisSelectionPanel;
import jp.riken.brain.ni.samuraigraph.base.SGCheckBox;
import jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton;
import jp.riken.brain.ni.samuraigraph.base.SGComponentGroup;
import jp.riken.brain.ni.samuraigraph.base.SGComponentGroupElement;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDateUtility;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGGradationPaint;
import jp.riken.brain.ni.samuraigraph.base.SGGradationPaintDialog;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGITwoAxesDialog;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGPatternPaint;
import jp.riken.brain.ni.samuraigraph.base.SGPatternPaintDialog;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGSelectablePaint;
import jp.riken.brain.ni.samuraigraph.base.SGSpinner;
import jp.riken.brain.ni.samuraigraph.base.SGTextField;
import jp.riken.brain.ni.samuraigraph.base.SGTransparentPaint;
import jp.riken.brain.ni.samuraigraph.base.SGTwoAxesSelectionPanel;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataSetupDialog;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIIndexData;
import jp.riken.brain.ni.samuraigraph.data.SGIMDArrayConstants;
import jp.riken.brain.ni.samuraigraph.data.SGINetCDFConstants;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataSetupDialog;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayPickUpDimensionInfo;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataSetupDialog;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFPickUpDimensionInfo;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFVariable;
import jp.riken.brain.ni.samuraigraph.data.SGPickUpDimensionInfo;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataSetupDialog;
import jp.riken.brain.ni.samuraigraph.data.SGSXYMDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementErrorBar;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementSymbol;
import jp.riken.brain.ni.samuraigraph.figure.SGIErrorBarConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGILineConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISymbolConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyle;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyleDialog;
import jp.riken.brain.ni.samuraigraph.figure.SGPaintConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGUtilityForFigureElement;

/**
 * A dialog to set the properties of two-dimensional scalar type data.
 */
public class SGPropertyDialogSXYData extends SGDataDialog implements
        SGIFigureDrawingElementConstants, SGIDataColumnTypeConstants, 
        SGISXYDataConstants, SGINetCDFConstants, SGILineConstants,
        SGISymbolConstants, SGITwoAxesDialog {

    // serialVersionID
    private static final long serialVersionUID = -8504726423602738949L;

    /**
     * The title of this dialog.
     */
    public static final String TITLE = "Data Properties";

    /**
     * Creates new form SGPropertyDialogSXYData
     */
    public SGPropertyDialogSXYData(java.awt.Frame parent, boolean modal) {
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

        mLineEditButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mCommonPanel = new javax.swing.JPanel();
        mLegendVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
        mNameLabel = new javax.swing.JLabel();
        mNameField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mDataColumnSelectionButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mTabbedPane = new javax.swing.JTabbedPane();
        mLinePanel = new javax.swing.JPanel();
        mLineSubPanel = new javax.swing.JPanel();
        mLineVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
        mLineWidthLabel = new javax.swing.JLabel();
        mLineWidthSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
        mLineColorLabel = new javax.swing.JLabel();
        mLineColorButton = new jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton();
        mLineTypeLabel = new javax.swing.JLabel();
        mLineTypeComboBox = new jp.riken.brain.ni.samuraigraph.base.SGComboBox();
        mLineConnectCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
        mLineSymbolLabel = new javax.swing.JLabel();
        mLineSymbolSeparator = new javax.swing.JSeparator();
        mLineSymbolPanel = new javax.swing.JPanel();
        mSymbolVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
        mSymbolTypePanel = new javax.swing.JPanel();
        mSymbolTypeLabel = new javax.swing.JLabel();
        mSymbolTypeComboBox = new jp.riken.brain.ni.samuraigraph.base.SGComboBox();
        mSymbolBodyLabel = new javax.swing.JLabel();
        mSymbolBodySeparator = new javax.swing.JSeparator();
        mSymbolSizeLabel = new javax.swing.JLabel();
        mSymbolSizeSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
        mSymbolColorLabel = new javax.swing.JLabel();
        mSymbolColorButton = new jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton();
        mSymbolBodyTransparencyPanel = new javax.swing.JPanel();
        mSymbolBodyTransparencyLabel = new javax.swing.JLabel();
        mSymbolBodyTransparencySpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
        mSymbolLineWidthSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
        mSymbolLineWidthLabel = new javax.swing.JLabel();
        mSymbolLineColorLabel = new javax.swing.JLabel();
        mSymbolLineColorButton = new jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton();
        mSymbolLineLabel = new javax.swing.JLabel();
        mSymbolLineVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
        mSymbolLineSeparator = new javax.swing.JSeparator();
        mLineStyleCustomizeButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mBarPanel = new javax.swing.JPanel();
        mBarSubPanel = new javax.swing.JPanel();
        mBodyPanel = new javax.swing.JPanel();
        mBarBodyTransparencyPanel = new javax.swing.JPanel();
        mBarBodyTransparencyLabel = new javax.swing.JLabel();
        mBarBodyTransparencySpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
        mBarColorPanel = new javax.swing.JPanel();
        mBarColorLabel = new javax.swing.JLabel();
        mBarBodyColorFillRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
        mBarBodyColorPatternRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
        mBarBodyColorGradationRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
        mBarBodyColorGradationColorButton = new jp.riken.brain.ni.samuraigraph.base.SGGradationPaintSelectionButton();
        mBarBodyColorPatternPaintButton = new jp.riken.brain.ni.samuraigraph.base.SGPatternPaintSelectionButton();
        mBarInnerColorButton = new jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton();
        mBarBodyWidthIntervalPanel = new javax.swing.JPanel();
        mBarIntervalPanel = new javax.swing.JPanel();
        mBarIntervalTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mBarIntervalDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mBarIntervalLabel = new javax.swing.JLabel();
        mBarWidthPanel = new javax.swing.JPanel();
        mBarWidthTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mBarWidthDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mBarWidthLabel = new javax.swing.JLabel();
        mBarLinePanel = new javax.swing.JPanel();
        mBarLineWidthLabel = new javax.swing.JLabel();
        mBarLineWidthSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
        mBarLineColorLabel = new javax.swing.JLabel();
        mBarLineColorButton = new jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton();
        mBarOffsetPanel = new javax.swing.JPanel();
        mBarOffsetXPanel = new javax.swing.JPanel();
        mBarOffsetXTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mBarOffsetXDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mBarOffsetXLabel = new javax.swing.JLabel();
        mBarOffsetYPanel = new javax.swing.JPanel();
        mBarOffsetYTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mBarOffsetYDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mBarOffsetYLabel = new javax.swing.JLabel();
        mBarBodyLabel = new javax.swing.JLabel();
        mBarBodySeparator = new javax.swing.JSeparator();
        mBarTopPanel = new javax.swing.JPanel();
        mBarBaseLineValueLabel = new javax.swing.JLabel();
        mBarVerticalCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
        mBarVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
        mBarBaselinePanel = new javax.swing.JPanel();
        mBarBaselineTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mBarBaselineDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mBarLineLabel = new javax.swing.JLabel();
        mBarLineVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
        mBarLineSeparator = new javax.swing.JSeparator();
        mBarOffsetLabel = new javax.swing.JLabel();
        mBarOffsetSeparator = new javax.swing.JSeparator();
        mErrorBarPanel = new javax.swing.JPanel();
        mErrorBarSubPanel = new javax.swing.JPanel();
        mErrorBarVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
        mErrorBarSymbolSizeLabel = new javax.swing.JLabel();
        mErrorBarSymbolSizeSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
        mErrorBarColorLabel = new javax.swing.JLabel();
        mErrorBarTypeLabel = new javax.swing.JLabel();
        mErrorBarTypeComboBox = new jp.riken.brain.ni.samuraigraph.base.SGComboBox();
        mErrorBarColorButton = new jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton();
        mErrorBarStyleLabel = new javax.swing.JLabel();
        mErrorBarSymbolLabel = new javax.swing.JLabel();
        mErrorBarStyleSeparator = new javax.swing.JSeparator();
        mErrorBarSymbolSeparator = new javax.swing.JSeparator();
        mErrorBarSylePanel = new javax.swing.JPanel();
        mErrorBarBothsidesRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
        mErrorBarUpsideRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
        mErrorBarDownsideRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
        mErrorBarLineWidthPanel = new javax.swing.JPanel();
        mErrorBarLineWidthLabel1 = new javax.swing.JLabel();
        mErrorBarLineWidthSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
        mErrorBarLineWidthLabel2 = new javax.swing.JLabel();
        mErrorBarPositionLabel = new javax.swing.JLabel();
        mErrorBarPositionSeparator = new javax.swing.JSeparator();
        mErrorBarPositionPanel = new javax.swing.JPanel();
        mErrorBarPositionLineRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
        mErrorBarPositionBarRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
        mTickLabelPanel = new javax.swing.JPanel();
        mTickLabelSubPanel = new javax.swing.JPanel();
        mTickLabelFontSizeLabel = new javax.swing.JLabel();
        mTickLabelVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
        mTickLabelFontSizeSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
        mTickLabelColorButton = new jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton();
        mTickLabelColorLabel = new javax.swing.JLabel();
        mTickLabelAnglePanel = new javax.swing.JPanel();
        mTickLabelAngleLabel = new javax.swing.JLabel();
        mTickLabelAngleSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
        mTickLabelFontLabel = new javax.swing.JLabel();
        mTickLabelFontSeparator = new javax.swing.JSeparator();
        mTickLabelTextLabel = new javax.swing.JLabel();
        mTickLanelAngleSeparator = new javax.swing.JSeparator();
        mTickLabelFontStyleComboBox = new jp.riken.brain.ni.samuraigraph.base.SGComboBox();
        mTickLabelFontNameComboBox = new jp.riken.brain.ni.samuraigraph.base.SGComboBox();
        mTickLabelFontNameLabel = new javax.swing.JLabel();
        mTickLabelFontStyleLabel = new javax.swing.JLabel();
        mTickLabelFormatLabel = new javax.swing.JLabel();
        mTickLabelFormatSeparator = new javax.swing.JSeparator();
        mTickLabelFormatPanel = new javax.swing.JPanel();
        mTickLabelDecimalPlacesSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
        mTickLabelDecimalPlacesLabel = new javax.swing.JLabel();
        mTickLabelExponentLabel = new javax.swing.JLabel();
        mTickLabelDateFormatComboBox = new jp.riken.brain.ni.samuraigraph.base.SGComboBox();
        mExponentPanel = new javax.swing.JPanel();
        mTickLabelExponentSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
        mTickLabelExponentBaseLabel = new javax.swing.JLabel();
        mTickLabelDateFormatLabel = new javax.swing.JLabel();
        mButtonPanel = new javax.swing.JPanel();
        mOKButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mCancelButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mPreviewButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mBottomCommonPanel = new javax.swing.JPanel();
        mShiftXLabel = new javax.swing.JLabel();
        mShiftLabel = new javax.swing.JLabel();
        mShiftYLabel = new javax.swing.JLabel();
        mShiftXPanel = new javax.swing.JPanel();
        mShiftXTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mShiftXDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mShiftYPanel = new javax.swing.JPanel();
        mShiftYTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
        mShiftYDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
        mHeadPanel = new javax.swing.JPanel();

        mLineEditButton.setText("Edit");

        getContentPane().setLayout(new java.awt.GridBagLayout());

        mCommonPanel.setLayout(new java.awt.GridBagLayout());

        mLegendVisibleCheckBox.setText("Legend Visible");
        mLegendVisibleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        mCommonPanel.add(mLegendVisibleCheckBox, gridBagConstraints);

        mNameLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mNameLabel.setText("Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        mCommonPanel.add(mNameLabel, gridBagConstraints);

        mNameField.setColumns(16);
        mNameField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        mCommonPanel.add(mNameField, gridBagConstraints);

        mDataColumnSelectionButton.setText("Data Column");
        mDataColumnSelectionButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        mCommonPanel.add(mDataColumnSelectionButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(mCommonPanel, gridBagConstraints);

        mTabbedPane.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        mTabbedPane.setPreferredSize(new java.awt.Dimension(360, 360));

        mLinePanel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mLinePanel.setLayout(new java.awt.GridBagLayout());

        mLineSubPanel.setLayout(new java.awt.GridBagLayout());

        mLineVisibleCheckBox.setText("Visible");
        mLineVisibleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 10);
        mLineSubPanel.add(mLineVisibleCheckBox, gridBagConstraints);

        mLineWidthLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mLineWidthLabel.setText("Width");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 2, 5);
        mLineSubPanel.add(mLineWidthLabel, gridBagConstraints);

        mLineWidthSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mLineWidthSpinner.setMinimumSize(new java.awt.Dimension(75, 22));
        mLineWidthSpinner.setPreferredSize(new java.awt.Dimension(75, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 5);
        mLineSubPanel.add(mLineWidthSpinner, gridBagConstraints);

        mLineColorLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mLineColorLabel.setText("Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 2, 5);
        mLineSubPanel.add(mLineColorLabel, gridBagConstraints);

        mLineColorButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        mLineColorButton.setMinimumSize(new java.awt.Dimension(65, 20));
        mLineColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        mLineSubPanel.add(mLineColorButton, gridBagConstraints);

        mLineTypeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mLineTypeLabel.setText("Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 2, 5);
        mLineSubPanel.add(mLineTypeLabel, gridBagConstraints);

        mLineTypeComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mLineTypeComboBox.setMinimumSize(new java.awt.Dimension(140, 22));
        mLineTypeComboBox.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 5);
        mLineSubPanel.add(mLineTypeComboBox, gridBagConstraints);

        mLineConnectCheckBox.setText("Ignore Missing Values");
        mLineConnectCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 2, 10);
        mLineSubPanel.add(mLineConnectCheckBox, gridBagConstraints);

        mLineSymbolLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mLineSymbolLabel.setText("Symbol");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        mLineSubPanel.add(mLineSymbolLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 50, 2, 10);
        mLineSubPanel.add(mLineSymbolSeparator, gridBagConstraints);

        mLineSymbolPanel.setLayout(new java.awt.GridBagLayout());

        mSymbolVisibleCheckBox.setText("Visible");
        mSymbolVisibleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 10);
        mLineSymbolPanel.add(mSymbolVisibleCheckBox, gridBagConstraints);

        mSymbolTypePanel.setLayout(new java.awt.GridBagLayout());

        mSymbolTypeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mSymbolTypeLabel.setText("Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mSymbolTypePanel.add(mSymbolTypeLabel, gridBagConstraints);

        mSymbolTypeComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mSymbolTypeComboBox.setMinimumSize(new java.awt.Dimension(150, 22));
        mSymbolTypeComboBox.setPreferredSize(new java.awt.Dimension(150, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        mSymbolTypePanel.add(mSymbolTypeComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 2, 0);
        mLineSymbolPanel.add(mSymbolTypePanel, gridBagConstraints);

        mSymbolBodyLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mSymbolBodyLabel.setText("Body");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 5);
        mLineSymbolPanel.add(mSymbolBodyLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 50, 0, 10);
        mLineSymbolPanel.add(mSymbolBodySeparator, gridBagConstraints);

        mSymbolSizeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mSymbolSizeLabel.setText("Size");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 5);
        mLineSymbolPanel.add(mSymbolSizeLabel, gridBagConstraints);

        mSymbolSizeSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mSymbolSizeSpinner.setMinimumSize(new java.awt.Dimension(75, 22));
        mSymbolSizeSpinner.setPreferredSize(new java.awt.Dimension(75, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 5);
        mLineSymbolPanel.add(mSymbolSizeSpinner, gridBagConstraints);

        mSymbolColorLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mSymbolColorLabel.setText("Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 2, 5);
        mLineSymbolPanel.add(mSymbolColorLabel, gridBagConstraints);

        mSymbolColorButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        mSymbolColorButton.setMinimumSize(new java.awt.Dimension(65, 20));
        mSymbolColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        mLineSymbolPanel.add(mSymbolColorButton, gridBagConstraints);

        mSymbolBodyTransparencyPanel.setLayout(new java.awt.GridBagLayout());

        mSymbolBodyTransparencyLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mSymbolBodyTransparencyLabel.setText("Transparency");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mSymbolBodyTransparencyPanel.add(mSymbolBodyTransparencyLabel, gridBagConstraints);

        mSymbolBodyTransparencySpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mSymbolBodyTransparencySpinner.setMinimumSize(new java.awt.Dimension(75, 22));
        mSymbolBodyTransparencySpinner.setPreferredSize(new java.awt.Dimension(75, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        mSymbolBodyTransparencyPanel.add(mSymbolBodyTransparencySpinner, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 0);
        mLineSymbolPanel.add(mSymbolBodyTransparencyPanel, gridBagConstraints);

        mSymbolLineWidthSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mSymbolLineWidthSpinner.setMinimumSize(new java.awt.Dimension(75, 22));
        mSymbolLineWidthSpinner.setPreferredSize(new java.awt.Dimension(75, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        mLineSymbolPanel.add(mSymbolLineWidthSpinner, gridBagConstraints);

        mSymbolLineWidthLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mSymbolLineWidthLabel.setText("Width");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 5);
        mLineSymbolPanel.add(mSymbolLineWidthLabel, gridBagConstraints);

        mSymbolLineColorLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mSymbolLineColorLabel.setText("Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 5);
        mLineSymbolPanel.add(mSymbolLineColorLabel, gridBagConstraints);

        mSymbolLineColorButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        mSymbolLineColorButton.setMinimumSize(new java.awt.Dimension(65, 20));
        mSymbolLineColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 10);
        mLineSymbolPanel.add(mSymbolLineColorButton, gridBagConstraints);

        mSymbolLineLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mSymbolLineLabel.setText("Line");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 2, 5);
        mLineSymbolPanel.add(mSymbolLineLabel, gridBagConstraints);

        mSymbolLineVisibleCheckBox.setText("Visible");
        mSymbolLineVisibleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 0);
        mLineSymbolPanel.add(mSymbolLineVisibleCheckBox, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 45, 2, 10);
        mLineSymbolPanel.add(mSymbolLineSeparator, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mLineSubPanel.add(mLineSymbolPanel, gridBagConstraints);

        mLineStyleCustomizeButton.setText("Customize");
        mLineStyleCustomizeButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        mLineSubPanel.add(mLineStyleCustomizeButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 30, 0);
        mLinePanel.add(mLineSubPanel, gridBagConstraints);

        mTabbedPane.addTab("Line", mLinePanel);

        mBarPanel.setLayout(new java.awt.GridBagLayout());

        mBarSubPanel.setLayout(new java.awt.GridBagLayout());

        mBodyPanel.setLayout(new java.awt.GridBagLayout());

        mBarBodyTransparencyPanel.setLayout(new java.awt.GridBagLayout());

        mBarBodyTransparencyLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mBarBodyTransparencyLabel.setText("Transparency");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        mBarBodyTransparencyPanel.add(mBarBodyTransparencyLabel, gridBagConstraints);

        mBarBodyTransparencySpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mBarBodyTransparencySpinner.setMinimumSize(new java.awt.Dimension(75, 22));
        mBarBodyTransparencySpinner.setPreferredSize(new java.awt.Dimension(75, 22));
        mBarBodyTransparencySpinner.setText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarBodyTransparencyPanel.add(mBarBodyTransparencySpinner, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 10);
        mBodyPanel.add(mBarBodyTransparencyPanel, gridBagConstraints);

        mBarColorPanel.setLayout(new java.awt.GridBagLayout());

        mBarColorLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mBarColorLabel.setText("Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mBarColorPanel.add(mBarColorLabel, gridBagConstraints);

        mBarBodyColorFillRadioButton.setText("Fill");
        mBarBodyColorFillRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarColorPanel.add(mBarBodyColorFillRadioButton, gridBagConstraints);

        mBarBodyColorPatternRadioButton.setText("Pattern");
        mBarBodyColorPatternRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarColorPanel.add(mBarBodyColorPatternRadioButton, gridBagConstraints);

        mBarBodyColorGradationRadioButton.setText("Gradation");
        mBarBodyColorGradationRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarColorPanel.add(mBarBodyColorGradationRadioButton, gridBagConstraints);

        mBarBodyColorGradationColorButton.setMinimumSize(new java.awt.Dimension(65, 20));
        mBarBodyColorGradationColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarColorPanel.add(mBarBodyColorGradationColorButton, gridBagConstraints);

        mBarBodyColorPatternPaintButton.setMinimumSize(new java.awt.Dimension(65, 20));
        mBarBodyColorPatternPaintButton.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarColorPanel.add(mBarBodyColorPatternPaintButton, gridBagConstraints);

        mBarInnerColorButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        mBarInnerColorButton.setMinimumSize(new java.awt.Dimension(65, 20));
        mBarInnerColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarColorPanel.add(mBarInnerColorButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBodyPanel.add(mBarColorPanel, gridBagConstraints);

        mBarBodyWidthIntervalPanel.setLayout(new java.awt.GridBagLayout());

        mBarIntervalPanel.setLayout(new java.awt.GridBagLayout());

        mBarIntervalTextField.setColumns(6);
        mBarIntervalTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarIntervalPanel.add(mBarIntervalTextField, gridBagConstraints);

        mBarIntervalDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarIntervalPanel.add(mBarIntervalDateButton, gridBagConstraints);

        mBarIntervalLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mBarIntervalLabel.setText("Interval");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mBarIntervalPanel.add(mBarIntervalLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        mBarBodyWidthIntervalPanel.add(mBarIntervalPanel, gridBagConstraints);

        mBarWidthPanel.setLayout(new java.awt.GridBagLayout());

        mBarWidthTextField.setColumns(6);
        mBarWidthTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarWidthPanel.add(mBarWidthTextField, gridBagConstraints);

        mBarWidthDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarWidthPanel.add(mBarWidthDateButton, gridBagConstraints);

        mBarWidthLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mBarWidthLabel.setText("Width");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mBarWidthPanel.add(mBarWidthLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        mBarBodyWidthIntervalPanel.add(mBarWidthPanel, gridBagConstraints);

        mBodyPanel.add(mBarBodyWidthIntervalPanel, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        mBarSubPanel.add(mBodyPanel, gridBagConstraints);

        mBarLinePanel.setLayout(new java.awt.GridBagLayout());

        mBarLineWidthLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mBarLineWidthLabel.setText("Width");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mBarLinePanel.add(mBarLineWidthLabel, gridBagConstraints);

        mBarLineWidthSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mBarLineWidthSpinner.setMinimumSize(new java.awt.Dimension(75, 22));
        mBarLineWidthSpinner.setPreferredSize(new java.awt.Dimension(75, 22));
        mBarLineWidthSpinner.setText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mBarLinePanel.add(mBarLineWidthSpinner, gridBagConstraints);

        mBarLineColorLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mBarLineColorLabel.setText("Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        mBarLinePanel.add(mBarLineColorLabel, gridBagConstraints);

        mBarLineColorButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        mBarLineColorButton.setMinimumSize(new java.awt.Dimension(65, 20));
        mBarLineColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarLinePanel.add(mBarLineColorButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        mBarSubPanel.add(mBarLinePanel, gridBagConstraints);

        mBarOffsetPanel.setLayout(new java.awt.GridBagLayout());

        mBarOffsetXPanel.setLayout(new java.awt.GridBagLayout());

        mBarOffsetXTextField.setColumns(6);
        mBarOffsetXTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarOffsetXPanel.add(mBarOffsetXTextField, gridBagConstraints);

        mBarOffsetXDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarOffsetXPanel.add(mBarOffsetXDateButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        mBarOffsetPanel.add(mBarOffsetXPanel, gridBagConstraints);

        mBarOffsetXLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mBarOffsetXLabel.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mBarOffsetPanel.add(mBarOffsetXLabel, gridBagConstraints);

        mBarOffsetYPanel.setLayout(new java.awt.GridBagLayout());

        mBarOffsetYTextField.setColumns(6);
        mBarOffsetYTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarOffsetYPanel.add(mBarOffsetYTextField, gridBagConstraints);

        mBarOffsetYDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarOffsetYPanel.add(mBarOffsetYDateButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        mBarOffsetPanel.add(mBarOffsetYPanel, gridBagConstraints);

        mBarOffsetYLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mBarOffsetYLabel.setText("Y");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mBarOffsetPanel.add(mBarOffsetYLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        mBarSubPanel.add(mBarOffsetPanel, gridBagConstraints);

        mBarBodyLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mBarBodyLabel.setText("Body");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        mBarSubPanel.add(mBarBodyLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 50, 2, 0);
        mBarSubPanel.add(mBarBodySeparator, gridBagConstraints);

        mBarTopPanel.setLayout(new java.awt.GridBagLayout());

        mBarBaseLineValueLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mBarBaseLineValueLabel.setText("Baseline");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mBarTopPanel.add(mBarBaseLineValueLabel, gridBagConstraints);

        mBarVerticalCheckBox.setText("Vertical");
        mBarVerticalCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        mBarTopPanel.add(mBarVerticalCheckBox, gridBagConstraints);

        mBarVisibleCheckBox.setText("Visible");
        mBarVisibleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mBarTopPanel.add(mBarVisibleCheckBox, gridBagConstraints);

        mBarBaselinePanel.setLayout(new java.awt.GridBagLayout());

        mBarBaselineTextField.setColumns(6);
        mBarBaselineTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarBaselinePanel.add(mBarBaselineTextField, gridBagConstraints);

        mBarBaselineDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarBaselinePanel.add(mBarBaselineDateButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        mBarTopPanel.add(mBarBaselinePanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mBarSubPanel.add(mBarTopPanel, gridBagConstraints);

        mBarLineLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mBarLineLabel.setText("Line");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        mBarSubPanel.add(mBarLineLabel, gridBagConstraints);

        mBarLineVisibleCheckBox.setText("Visible");
        mBarLineVisibleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 50, 0, 0);
        mBarSubPanel.add(mBarLineVisibleCheckBox, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 125, 0, 0);
        mBarSubPanel.add(mBarLineSeparator, gridBagConstraints);

        mBarOffsetLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mBarOffsetLabel.setText("Offset ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        mBarSubPanel.add(mBarOffsetLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 80, 0, 0);
        mBarSubPanel.add(mBarOffsetSeparator, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        mBarPanel.add(mBarSubPanel, gridBagConstraints);

        mTabbedPane.addTab("Bar", mBarPanel);

        mErrorBarPanel.setLayout(new java.awt.GridBagLayout());

        mErrorBarSubPanel.setLayout(new java.awt.GridBagLayout());

        mErrorBarVisibleCheckBox.setText("Visible");
        mErrorBarVisibleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 5);
        mErrorBarSubPanel.add(mErrorBarVisibleCheckBox, gridBagConstraints);

        mErrorBarSymbolSizeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mErrorBarSymbolSizeLabel.setText("Size");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 5);
        mErrorBarSubPanel.add(mErrorBarSymbolSizeLabel, gridBagConstraints);

        mErrorBarSymbolSizeSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mErrorBarSymbolSizeSpinner.setPreferredSize(new java.awt.Dimension(75, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 5);
        mErrorBarSubPanel.add(mErrorBarSymbolSizeSpinner, gridBagConstraints);

        mErrorBarColorLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mErrorBarColorLabel.setText("Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 2, 5);
        mErrorBarSubPanel.add(mErrorBarColorLabel, gridBagConstraints);

        mErrorBarTypeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mErrorBarTypeLabel.setText("Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 5);
        mErrorBarSubPanel.add(mErrorBarTypeLabel, gridBagConstraints);

        mErrorBarTypeComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mErrorBarTypeComboBox.setPreferredSize(new java.awt.Dimension(90, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        mErrorBarSubPanel.add(mErrorBarTypeComboBox, gridBagConstraints);

        mErrorBarColorButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        mErrorBarColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        mErrorBarSubPanel.add(mErrorBarColorButton, gridBagConstraints);

        mErrorBarStyleLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mErrorBarStyleLabel.setText("Style");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 10);
        mErrorBarSubPanel.add(mErrorBarStyleLabel, gridBagConstraints);

        mErrorBarSymbolLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mErrorBarSymbolLabel.setText("Symbol ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 10);
        mErrorBarSubPanel.add(mErrorBarSymbolLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 10);
        mErrorBarSubPanel.add(mErrorBarStyleSeparator, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 65, 0, 10);
        mErrorBarSubPanel.add(mErrorBarSymbolSeparator, gridBagConstraints);

        mErrorBarSylePanel.setLayout(new java.awt.GridBagLayout());

        mErrorBarBothsidesRadioButton.setText("Bothsides");
        mErrorBarBothsidesRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mErrorBarSylePanel.add(mErrorBarBothsidesRadioButton, gridBagConstraints);

        mErrorBarUpsideRadioButton.setText("Upside");
        mErrorBarUpsideRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mErrorBarSylePanel.add(mErrorBarUpsideRadioButton, gridBagConstraints);

        mErrorBarDownsideRadioButton.setText("Downside");
        mErrorBarDownsideRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mErrorBarSylePanel.add(mErrorBarDownsideRadioButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 10);
        mErrorBarSubPanel.add(mErrorBarSylePanel, gridBagConstraints);

        mErrorBarLineWidthPanel.setLayout(new java.awt.GridBagLayout());

        mErrorBarLineWidthLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mErrorBarLineWidthLabel1.setText("Line");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mErrorBarLineWidthPanel.add(mErrorBarLineWidthLabel1, gridBagConstraints);

        mErrorBarLineWidthSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mErrorBarLineWidthSpinner.setPreferredSize(new java.awt.Dimension(75, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        mErrorBarLineWidthPanel.add(mErrorBarLineWidthSpinner, gridBagConstraints);

        mErrorBarLineWidthLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mErrorBarLineWidthLabel2.setText("Width");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mErrorBarLineWidthPanel.add(mErrorBarLineWidthLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 10);
        mErrorBarSubPanel.add(mErrorBarLineWidthPanel, gridBagConstraints);

        mErrorBarPositionLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mErrorBarPositionLabel.setText("Position");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 10);
        mErrorBarSubPanel.add(mErrorBarPositionLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 70, 0, 10);
        mErrorBarSubPanel.add(mErrorBarPositionSeparator, gridBagConstraints);

        mErrorBarPositionPanel.setLayout(new java.awt.GridBagLayout());

        mErrorBarPositionLineRadioButton.setText("Line");
        mErrorBarPositionLineRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mErrorBarPositionPanel.add(mErrorBarPositionLineRadioButton, gridBagConstraints);

        mErrorBarPositionBarRadioButton.setText("Bar");
        mErrorBarPositionBarRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        mErrorBarPositionPanel.add(mErrorBarPositionBarRadioButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 10);
        mErrorBarSubPanel.add(mErrorBarPositionPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 80, 0);
        mErrorBarPanel.add(mErrorBarSubPanel, gridBagConstraints);

        mTabbedPane.addTab("Error Bar", mErrorBarPanel);

        mTickLabelPanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        mTickLabelPanel.setMinimumSize(new java.awt.Dimension(297, 229));
        mTickLabelPanel.setLayout(new java.awt.GridBagLayout());

        mTickLabelSubPanel.setLayout(new java.awt.GridBagLayout());

        mTickLabelFontSizeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTickLabelFontSizeLabel.setText("Size");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 5);
        mTickLabelSubPanel.add(mTickLabelFontSizeLabel, gridBagConstraints);

        mTickLabelVisibleCheckBox.setText("Visible");
        mTickLabelVisibleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 10);
        mTickLabelSubPanel.add(mTickLabelVisibleCheckBox, gridBagConstraints);

        mTickLabelFontSizeSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTickLabelFontSizeSpinner.setPreferredSize(new java.awt.Dimension(75, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 5);
        mTickLabelSubPanel.add(mTickLabelFontSizeSpinner, gridBagConstraints);

        mTickLabelColorButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        mTickLabelColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        mTickLabelSubPanel.add(mTickLabelColorButton, gridBagConstraints);

        mTickLabelColorLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTickLabelColorLabel.setText("Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 2, 5);
        mTickLabelSubPanel.add(mTickLabelColorLabel, gridBagConstraints);

        mTickLabelAnglePanel.setMinimumSize(new java.awt.Dimension(150, 22));
        mTickLabelAnglePanel.setLayout(new java.awt.GridBagLayout());

        mTickLabelAngleLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTickLabelAngleLabel.setText("Angle");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mTickLabelAnglePanel.add(mTickLabelAngleLabel, gridBagConstraints);

        mTickLabelAngleSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTickLabelAngleSpinner.setPreferredSize(new java.awt.Dimension(75, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 27, 0, 0);
        mTickLabelAnglePanel.add(mTickLabelAngleSpinner, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 10);
        mTickLabelSubPanel.add(mTickLabelAnglePanel, gridBagConstraints);

        mTickLabelFontLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mTickLabelFontLabel.setText("Font");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 2, 5);
        mTickLabelSubPanel.add(mTickLabelFontLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 45, 2, 10);
        mTickLabelSubPanel.add(mTickLabelFontSeparator, gridBagConstraints);

        mTickLabelTextLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mTickLabelTextLabel.setText("Text");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 10);
        mTickLabelSubPanel.add(mTickLabelTextLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 55, 0, 10);
        mTickLabelSubPanel.add(mTickLanelAngleSeparator, gridBagConstraints);

        mTickLabelFontStyleComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTickLabelFontStyleComboBox.setPreferredSize(new java.awt.Dimension(100, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        mTickLabelSubPanel.add(mTickLabelFontStyleComboBox, gridBagConstraints);

        mTickLabelFontNameComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTickLabelFontNameComboBox.setPreferredSize(new java.awt.Dimension(170, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        mTickLabelSubPanel.add(mTickLabelFontNameComboBox, gridBagConstraints);

        mTickLabelFontNameLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTickLabelFontNameLabel.setText("Family");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 5);
        mTickLabelSubPanel.add(mTickLabelFontNameLabel, gridBagConstraints);

        mTickLabelFontStyleLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTickLabelFontStyleLabel.setText("Style");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 5);
        mTickLabelSubPanel.add(mTickLabelFontStyleLabel, gridBagConstraints);

        mTickLabelFormatLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mTickLabelFormatLabel.setText("Format");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 10);
        mTickLabelSubPanel.add(mTickLabelFormatLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        mTickLabelSubPanel.add(mTickLabelFormatSeparator, gridBagConstraints);

        mTickLabelFormatPanel.setLayout(new java.awt.GridBagLayout());

        mTickLabelDecimalPlacesSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTickLabelDecimalPlacesSpinner.setPreferredSize(new java.awt.Dimension(75, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 0);
        mTickLabelFormatPanel.add(mTickLabelDecimalPlacesSpinner, gridBagConstraints);

        mTickLabelDecimalPlacesLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTickLabelDecimalPlacesLabel.setText("Decimal Places");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 5);
        mTickLabelFormatPanel.add(mTickLabelDecimalPlacesLabel, gridBagConstraints);

        mTickLabelExponentLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTickLabelExponentLabel.setText("Exponent");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 5);
        mTickLabelFormatPanel.add(mTickLabelExponentLabel, gridBagConstraints);

        mTickLabelDateFormatComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTickLabelDateFormatComboBox.setPreferredSize(new java.awt.Dimension(170, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        mTickLabelFormatPanel.add(mTickLabelDateFormatComboBox, gridBagConstraints);

        mExponentPanel.setLayout(new java.awt.GridBagLayout());

        mTickLabelExponentSpinner.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        mTickLabelExponentSpinner.setPreferredSize(new java.awt.Dimension(40, 20));
        mTickLabelExponentSpinner.setValue(new Integer(0));
        mTickLabelExponentSpinner.setVerifyInputWhenFocusTarget(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        mExponentPanel.add(mTickLabelExponentSpinner, gridBagConstraints);

        mTickLabelExponentBaseLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        mTickLabelExponentBaseLabel.setText("10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mExponentPanel.add(mTickLabelExponentBaseLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
        mTickLabelFormatPanel.add(mExponentPanel, gridBagConstraints);

        mTickLabelDateFormatLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mTickLabelDateFormatLabel.setText("Date");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 5);
        mTickLabelFormatPanel.add(mTickLabelDateFormatLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 10);
        mTickLabelSubPanel.add(mTickLabelFormatPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 40, 0);
        mTickLabelPanel.add(mTickLabelSubPanel, gridBagConstraints);

        mTabbedPane.addTab("Tick Label", mTickLabelPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(mTabbedPane, gridBagConstraints);

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
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(mButtonPanel, gridBagConstraints);

        mBottomCommonPanel.setLayout(new java.awt.GridBagLayout());

        mShiftXLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mShiftXLabel.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 5);
        mBottomCommonPanel.add(mShiftXLabel, gridBagConstraints);

        mShiftLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        mShiftLabel.setText("Shift");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        mBottomCommonPanel.add(mShiftLabel, gridBagConstraints);

        mShiftYLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mShiftYLabel.setText("Y");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mBottomCommonPanel.add(mShiftYLabel, gridBagConstraints);

        mShiftXPanel.setLayout(new java.awt.GridBagLayout());

        mShiftXTextField.setColumns(6);
        mShiftXTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mShiftXPanel.add(mShiftXTextField, gridBagConstraints);

        mShiftXDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mShiftXPanel.add(mShiftXDateButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mBottomCommonPanel.add(mShiftXPanel, gridBagConstraints);

        mShiftYPanel.setLayout(new java.awt.GridBagLayout());

        mShiftYTextField.setColumns(6);
        mShiftYTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mShiftYPanel.add(mShiftYTextField, gridBagConstraints);

        mShiftYDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mShiftYPanel.add(mShiftYDateButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mBottomCommonPanel.add(mShiftYPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(mBottomCommonPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(mHeadPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel mBarBaseLineValueLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mBarBaselineDateButton;
    private javax.swing.JPanel mBarBaselinePanel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mBarBaselineTextField;
    private jp.riken.brain.ni.samuraigraph.base.SGRadioButton mBarBodyColorFillRadioButton;
    private jp.riken.brain.ni.samuraigraph.base.SGGradationPaintSelectionButton mBarBodyColorGradationColorButton;
    private jp.riken.brain.ni.samuraigraph.base.SGRadioButton mBarBodyColorGradationRadioButton;
    private jp.riken.brain.ni.samuraigraph.base.SGPatternPaintSelectionButton mBarBodyColorPatternPaintButton;
    private jp.riken.brain.ni.samuraigraph.base.SGRadioButton mBarBodyColorPatternRadioButton;
    private javax.swing.JLabel mBarBodyLabel;
    private javax.swing.JSeparator mBarBodySeparator;
    private javax.swing.JLabel mBarBodyTransparencyLabel;
    private javax.swing.JPanel mBarBodyTransparencyPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGSpinner mBarBodyTransparencySpinner;
    private javax.swing.JPanel mBarBodyWidthIntervalPanel;
    private javax.swing.JLabel mBarColorLabel;
    private javax.swing.JPanel mBarColorPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton mBarInnerColorButton;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mBarIntervalDateButton;
    private javax.swing.JLabel mBarIntervalLabel;
    private javax.swing.JPanel mBarIntervalPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mBarIntervalTextField;
    private jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton mBarLineColorButton;
    private javax.swing.JLabel mBarLineColorLabel;
    private javax.swing.JLabel mBarLineLabel;
    private javax.swing.JPanel mBarLinePanel;
    private javax.swing.JSeparator mBarLineSeparator;
    private jp.riken.brain.ni.samuraigraph.base.SGCheckBox mBarLineVisibleCheckBox;
    private javax.swing.JLabel mBarLineWidthLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGSpinner mBarLineWidthSpinner;
    private javax.swing.JLabel mBarOffsetLabel;
    private javax.swing.JPanel mBarOffsetPanel;
    private javax.swing.JSeparator mBarOffsetSeparator;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mBarOffsetXDateButton;
    private javax.swing.JLabel mBarOffsetXLabel;
    private javax.swing.JPanel mBarOffsetXPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mBarOffsetXTextField;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mBarOffsetYDateButton;
    private javax.swing.JLabel mBarOffsetYLabel;
    private javax.swing.JPanel mBarOffsetYPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mBarOffsetYTextField;
    private javax.swing.JPanel mBarPanel;
    private javax.swing.JPanel mBarSubPanel;
    private javax.swing.JPanel mBarTopPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGCheckBox mBarVerticalCheckBox;
    private jp.riken.brain.ni.samuraigraph.base.SGCheckBox mBarVisibleCheckBox;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mBarWidthDateButton;
    private javax.swing.JLabel mBarWidthLabel;
    private javax.swing.JPanel mBarWidthPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mBarWidthTextField;
    private javax.swing.JPanel mBodyPanel;
    private javax.swing.JPanel mBottomCommonPanel;
    private javax.swing.JPanel mButtonPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mCancelButton;
    private javax.swing.JPanel mCommonPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mDataColumnSelectionButton;
    private jp.riken.brain.ni.samuraigraph.base.SGRadioButton mErrorBarBothsidesRadioButton;
    private jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton mErrorBarColorButton;
    private javax.swing.JLabel mErrorBarColorLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGRadioButton mErrorBarDownsideRadioButton;
    private javax.swing.JLabel mErrorBarLineWidthLabel1;
    private javax.swing.JLabel mErrorBarLineWidthLabel2;
    private javax.swing.JPanel mErrorBarLineWidthPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGSpinner mErrorBarLineWidthSpinner;
    private javax.swing.JPanel mErrorBarPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGRadioButton mErrorBarPositionBarRadioButton;
    private javax.swing.JLabel mErrorBarPositionLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGRadioButton mErrorBarPositionLineRadioButton;
    private javax.swing.JPanel mErrorBarPositionPanel;
    private javax.swing.JSeparator mErrorBarPositionSeparator;
    private javax.swing.JLabel mErrorBarStyleLabel;
    private javax.swing.JSeparator mErrorBarStyleSeparator;
    private javax.swing.JPanel mErrorBarSubPanel;
    private javax.swing.JPanel mErrorBarSylePanel;
    private javax.swing.JLabel mErrorBarSymbolLabel;
    private javax.swing.JSeparator mErrorBarSymbolSeparator;
    private javax.swing.JLabel mErrorBarSymbolSizeLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGSpinner mErrorBarSymbolSizeSpinner;
    private jp.riken.brain.ni.samuraigraph.base.SGComboBox mErrorBarTypeComboBox;
    private javax.swing.JLabel mErrorBarTypeLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGRadioButton mErrorBarUpsideRadioButton;
    private jp.riken.brain.ni.samuraigraph.base.SGCheckBox mErrorBarVisibleCheckBox;
    private javax.swing.JPanel mExponentPanel;
    private javax.swing.JPanel mHeadPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGCheckBox mLegendVisibleCheckBox;
    private jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton mLineColorButton;
    private javax.swing.JLabel mLineColorLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGCheckBox mLineConnectCheckBox;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mLineEditButton;
    private javax.swing.JPanel mLinePanel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mLineStyleCustomizeButton;
    private javax.swing.JPanel mLineSubPanel;
    private javax.swing.JLabel mLineSymbolLabel;
    private javax.swing.JPanel mLineSymbolPanel;
    private javax.swing.JSeparator mLineSymbolSeparator;
    private jp.riken.brain.ni.samuraigraph.base.SGComboBox mLineTypeComboBox;
    private javax.swing.JLabel mLineTypeLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGCheckBox mLineVisibleCheckBox;
    private javax.swing.JLabel mLineWidthLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGSpinner mLineWidthSpinner;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mNameField;
    private javax.swing.JLabel mNameLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mOKButton;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mPreviewButton;
    private javax.swing.JLabel mShiftLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mShiftXDateButton;
    private javax.swing.JLabel mShiftXLabel;
    private javax.swing.JPanel mShiftXPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mShiftXTextField;
    private jp.riken.brain.ni.samuraigraph.base.SGButton mShiftYDateButton;
    private javax.swing.JLabel mShiftYLabel;
    private javax.swing.JPanel mShiftYPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGTextField mShiftYTextField;
    private javax.swing.JLabel mSymbolBodyLabel;
    private javax.swing.JSeparator mSymbolBodySeparator;
    private javax.swing.JLabel mSymbolBodyTransparencyLabel;
    private javax.swing.JPanel mSymbolBodyTransparencyPanel;
    private jp.riken.brain.ni.samuraigraph.base.SGSpinner mSymbolBodyTransparencySpinner;
    private jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton mSymbolColorButton;
    private javax.swing.JLabel mSymbolColorLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton mSymbolLineColorButton;
    private javax.swing.JLabel mSymbolLineColorLabel;
    private javax.swing.JLabel mSymbolLineLabel;
    private javax.swing.JSeparator mSymbolLineSeparator;
    private jp.riken.brain.ni.samuraigraph.base.SGCheckBox mSymbolLineVisibleCheckBox;
    private javax.swing.JLabel mSymbolLineWidthLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGSpinner mSymbolLineWidthSpinner;
    private javax.swing.JLabel mSymbolSizeLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGSpinner mSymbolSizeSpinner;
    private jp.riken.brain.ni.samuraigraph.base.SGComboBox mSymbolTypeComboBox;
    private javax.swing.JLabel mSymbolTypeLabel;
    private javax.swing.JPanel mSymbolTypePanel;
    private jp.riken.brain.ni.samuraigraph.base.SGCheckBox mSymbolVisibleCheckBox;
    private javax.swing.JTabbedPane mTabbedPane;
    private javax.swing.JLabel mTickLabelAngleLabel;
    private javax.swing.JPanel mTickLabelAnglePanel;
    private jp.riken.brain.ni.samuraigraph.base.SGSpinner mTickLabelAngleSpinner;
    private jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton mTickLabelColorButton;
    private javax.swing.JLabel mTickLabelColorLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGComboBox mTickLabelDateFormatComboBox;
    private javax.swing.JLabel mTickLabelDateFormatLabel;
    private javax.swing.JLabel mTickLabelDecimalPlacesLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGSpinner mTickLabelDecimalPlacesSpinner;
    private javax.swing.JLabel mTickLabelExponentBaseLabel;
    private javax.swing.JLabel mTickLabelExponentLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGSpinner mTickLabelExponentSpinner;
    private javax.swing.JLabel mTickLabelFontLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGComboBox mTickLabelFontNameComboBox;
    private javax.swing.JLabel mTickLabelFontNameLabel;
    private javax.swing.JSeparator mTickLabelFontSeparator;
    private javax.swing.JLabel mTickLabelFontSizeLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGSpinner mTickLabelFontSizeSpinner;
    private jp.riken.brain.ni.samuraigraph.base.SGComboBox mTickLabelFontStyleComboBox;
    private javax.swing.JLabel mTickLabelFontStyleLabel;
    private javax.swing.JLabel mTickLabelFormatLabel;
    private javax.swing.JPanel mTickLabelFormatPanel;
    private javax.swing.JSeparator mTickLabelFormatSeparator;
    private javax.swing.JPanel mTickLabelPanel;
    private javax.swing.JPanel mTickLabelSubPanel;
    private javax.swing.JLabel mTickLabelTextLabel;
    private jp.riken.brain.ni.samuraigraph.base.SGCheckBox mTickLabelVisibleCheckBox;
    private javax.swing.JSeparator mTickLanelAngleSeparator;
    // End of variables declaration//GEN-END:variables

    private SGTwoAxesSelectionPanel mAxisPanel = new SGTwoAxesSelectionPanel();

    private JRadioButton mNoErrorBarStyleSelectionRadioButton = new JRadioButton();

    private JRadioButton mNoBarBodyColorStyleSelectionRadioButton = new JRadioButton();

    private JRadioButton mNoErrorBarPositionRadioButton = new JRadioButton();

    private SGPickUpDimensionInfo mPickUpDimensionInfo = null;

    private SGPatternPaintDialog mPatternPaintDialog;

    private SGGradationPaintDialog mGradationPaintDialog;

    private SGLineStyleDialog mLineStyleDialog = null;
    
    private List<SGLineStyle> mLineStyleList = null;
    
    private List<String> mChildNameList = null;
    
    private String mLineColorMapName = null;
    
    private Map<String, SGProperties> mLineColorMapProperties = null;

    private boolean mLineColorAutoAssigned = false;

    protected SGComponentGroup mBarComponentGroup = new SGComponentGroup();

    /** members of this group disabled if transparency is 1.0 */
    private SGComponentGroup mBarBodyTransparentComponentGroup = new SGComponentGroup();
    
    private SGComponentGroup mBarIntervalComponentGroup = new SGComponentGroup();

    protected SGComponentGroup mDateWidthComponentGroup = new SGComponentGroup();

    protected SGComponentGroup mDateHeightComponentGroup = new SGComponentGroup();

    protected SGComponentGroup mDateXComponentGroup = new SGComponentGroup();

    protected SGComponentGroup mDateYComponentGroup = new SGComponentGroup();

    protected SGComponentGroup mTickLabelComponentGroup = new SGComponentGroup();

    protected SGComponentGroup mDateTickLabelComponentGroup = new SGComponentGroup();

    private boolean initProperty() {
    	this.mHeadPanel.add(this.mAxisPanel);
    	this.mAxisPanel.addAxisSelectionListener(this);

        // set the title
        this.setTitle(SGPropertyDialogSXYData.TITLE);

        // add items to the line type combo box
        for (int ii = 0; ii < LINE_NAME_ARRAY.length; ii++) {
            this.mLineTypeComboBox.addItem(LINE_NAME_ARRAY[ii]);
        }

        // add items to the symbol type combo box
        for (int ii = 0; ii < SYMBOL_NAME_ARRAY.length; ii++) {
            this.mSymbolTypeComboBox.addItem(SYMBOL_NAME_ARRAY[ii]);
        }

        // add items to the error bar type combo box
        final String typeArray[] = { SGIErrorBarConstants.SYMBOL_NAME_CIRCLE,
        		SGIErrorBarConstants.SYMBOL_NAME_TRANSVERSE_LINE,
        		SGIErrorBarConstants.SYMBOL_NAME_VOID };
        for (int ii = 0; ii < typeArray.length; ii++) {
            this.mErrorBarTypeComboBox.addItem(typeArray[ii]);
        }

        // create a button group for the radio buttons of error bar style
        this.mNoErrorBarStyleSelectionRadioButton.setVisible(false);
        final ButtonGroup errorBarStyleButtonGroup = new ButtonGroup();
        errorBarStyleButtonGroup.add(this.mErrorBarBothsidesRadioButton);
        errorBarStyleButtonGroup.add(this.mErrorBarUpsideRadioButton);
        errorBarStyleButtonGroup.add(this.mErrorBarDownsideRadioButton);
        errorBarStyleButtonGroup.add(this.mNoErrorBarStyleSelectionRadioButton);

        // create a button group for the radio buttons of bar body paint types
        this.mNoBarBodyColorStyleSelectionRadioButton.setVisible(false);
        final ButtonGroup barBodyColorStyleButtonGrouop  = new ButtonGroup();
        barBodyColorStyleButtonGrouop.add(this.mBarBodyColorFillRadioButton);
        barBodyColorStyleButtonGrouop.add(this.mBarBodyColorPatternRadioButton);
        barBodyColorStyleButtonGrouop.add(this.mBarBodyColorGradationRadioButton);
        barBodyColorStyleButtonGrouop.add(this.mNoBarBodyColorStyleSelectionRadioButton);

        // create a button group for the radio buttons of error bar position
        this.mNoErrorBarPositionRadioButton.setVisible(false);
        final ButtonGroup errorBarPositionButtonGroup  = new ButtonGroup();
        errorBarPositionButtonGroup.add(this.mErrorBarPositionLineRadioButton);
        errorBarPositionButtonGroup.add(this.mErrorBarPositionBarRadioButton);
        errorBarPositionButtonGroup.add(this.mNoErrorBarPositionRadioButton);

        // set up the combo boxes
        this.initFontFamilyNameComboBox(this.mTickLabelFontNameComboBox);
        this.initFontStyleComboBox(this.mTickLabelFontStyleComboBox);
        this.initDateFormatComboBox(this.mTickLabelDateFormatComboBox);

        //
        // Sets up spinner models
        //

        // line
        this.mLineWidthSpinner.initProperties(
                getLineWidthSpinnerNumberModel(), LINE_WIDTH_UNIT,
                LINE_WIDTH_FRAC_DIGIT_MIN, LINE_WIDTH_FRAC_DIGIT_MAX);

        // symbol
        this.mSymbolSizeSpinner.initProperties(new SpinnerNumberModel(0.1,
                SYMBOL_SIZE_MIN, SYMBOL_SIZE_MAX, SYMBOL_SIZE_STEP),
                SYMBOL_SIZE_UNIT, SYMBOL_SIZE_FRAC_DIFIT_MIN, SYMBOL_SIZE_FRAC_DIFIT_MAX);

        this.mSymbolLineWidthSpinner.initProperties(
                getLineWidthSpinnerNumberModel(), LINE_WIDTH_UNIT,
                LINE_WIDTH_FRAC_DIGIT_MIN, LINE_WIDTH_FRAC_DIGIT_MAX);

        this.mSymbolBodyTransparencySpinner.initProperties(
                new SpinnerNumberModel(100.0,
                        (float)SGPaintConstants.TRANSPARENCY_MIN,
                        (float)SGPaintConstants.TRANSPARENCY_MAX,
                        (float)SGPaintConstants.TRANSPARENCY_STEP),
                SGIConstants.percent,
                SGPaintConstants.TRANSPARENCY_FRAC_DIGIT_MIN,
                SGPaintConstants.TRANSPARENCY_FRAC_DIGIT_MAX);

        // bar
        this.mBarLineWidthSpinner.initProperties(
                getLineWidthSpinnerNumberModel(), LINE_WIDTH_UNIT,
                LINE_WIDTH_FRAC_DIGIT_MIN, LINE_WIDTH_FRAC_DIGIT_MAX);

        // error bar
        this.mErrorBarLineWidthSpinner.initProperties(
                getLineWidthSpinnerNumberModel(), LINE_WIDTH_UNIT,
                LINE_WIDTH_FRAC_DIGIT_MIN, LINE_WIDTH_FRAC_DIGIT_MAX);

        // bar inner paint transparency
        this.mBarBodyTransparencySpinner.initProperties(
                new SpinnerNumberModel(100.0,
                        (float)SGPaintConstants.TRANSPARENCY_MIN,
                        (float)SGPaintConstants.TRANSPARENCY_MAX,
                        (float)SGPaintConstants.TRANSPARENCY_STEP),
                SGIConstants.percent,
                SGPaintConstants.TRANSPARENCY_FRAC_DIGIT_MIN,
                SGPaintConstants.TRANSPARENCY_FRAC_DIGIT_MAX);

        this.mErrorBarSymbolSizeSpinner.initProperties(new SpinnerNumberModel(
                0.1, ERROR_BAR_HEAD_SIZE_MIN, ERROR_BAR_HEAD_SIZE_MAX,
                ERROR_BAR_HEAD_SIZE_STEP), ERROR_BAR_HEAD_SIZE_UNIT,
                ERROR_BAR_HEAD_SIZE_FRAC_DIFIT_MIN, ERROR_BAR_HEAD_SIZE_FRAC_DIFIT_MAX);

        // tick label
        this.mTickLabelFontSizeSpinner.initProperties(
                getFontSizeSpinnerNumberModel(), FONT_SIZE_UNIT,
                FONT_SIZE_FRAC_DIGIT_MIN, FONT_SIZE_FRAC_DIGIT_MAX);
        this.mTickLabelDecimalPlacesSpinner.initProperties(new SpinnerNumberModel(0.0,
                (float) TICK_LABEL_DECIMAL_PLACES_MIN, (float) TICK_LABEL_DECIMAL_PLACES_MAX,
                (float) TICK_LABEL_DECIMAL_PLACES_STEP), null, 0, 0);
        this.mTickLabelExponentSpinner.initProperties(new SpinnerNumberModel(0.0,
                (float) TICK_LABEL_EXPONENT_MIN, (float) TICK_LABEL_EXPONENT_MAX,
                (float) TICK_LABEL_EXPONENT_STEP), null, 0, 0);
        this.mTickLabelAngleSpinner.initProperties(new SpinnerNumberModel(0.0,
                (float) TICK_LABEL_TEXT_ANGLE_MIN, (float) TICK_LABEL_TEXT_ANGLE_MAX,
                (float) TICK_LABEL_TEXT_ANGLE_STEP), degree,
                TICK_LABEL_TEXT_ANGLE_FRAC_DIFIT_MIN,
                TICK_LABEL_TEXT_ANGLE_FRAC_DIFIT_MAX);

        // add an action listener to each items
        this.mLegendVisibleCheckBox.addActionListener(this);
        this.mLineEditButton.addActionListener(this);
        this.mDataColumnSelectionButton.addActionListener(this);
        this.mAxisPanel.addActionListener(this);
        this.mLineVisibleCheckBox.addActionListener(this);
        this.mLineStyleCustomizeButton.addActionListener(this);
        this.mLineTypeComboBox.addActionListener(this);
        this.mSymbolVisibleCheckBox.addActionListener(this);
        this.mSymbolLineVisibleCheckBox.addActionListener(this);
        this.mSymbolTypeComboBox.addActionListener(this);
        this.mBarVisibleCheckBox.addActionListener(this);
        this.mBarLineVisibleCheckBox.addActionListener(this);
        this.mBarVerticalCheckBox.addActionListener(this);
        this.mBarBodyColorPatternPaintButton.addActionListener(this);
        this.mBarBodyColorGradationColorButton.addActionListener(this);
        this.mErrorBarVisibleCheckBox.addActionListener(this);
        this.mErrorBarTypeComboBox.addActionListener(this);
        this.mTickLabelVisibleCheckBox.addActionListener(this);
        this.mTickLabelFontNameComboBox.addActionListener(this);
        this.mTickLabelFontStyleComboBox.addActionListener(this);
        this.mErrorBarBothsidesRadioButton.addActionListener(this);
        this.mErrorBarUpsideRadioButton.addActionListener(this);
        this.mErrorBarDownsideRadioButton.addActionListener(this);
        this.mBarBaselineDateButton.addActionListener(this);
        this.mBarWidthDateButton.addActionListener(this);
        this.mBarIntervalDateButton.addActionListener(this);
        this.mBarOffsetXDateButton.addActionListener(this);
        this.mBarOffsetYDateButton.addActionListener(this);
        this.mShiftXDateButton.addActionListener(this);
        this.mShiftYDateButton.addActionListener(this);

        // set the name
        this.mNameField.setDescription("Name");
        this.mShiftXTextField.setDescription("Shift X");
        this.mShiftYTextField.setDescription("Shift Y");
        this.mLineWidthSpinner.setDescription("Line-> Width");
        this.mSymbolSizeSpinner.setDescription("Line-> Symbol-> Body-> Size");
        this.mSymbolBodyTransparencySpinner.setDescription("Line-> Symbol-> Body-> Transparency");
        this.mSymbolLineWidthSpinner.setDescription("Line-> Symbol-> Line-> Width");
        this.mBarBaselineTextField.setDescription("Bar-> Baseline");
        this.mBarWidthTextField.setDescription("Bar-> Body-> Width");
        this.mBarIntervalTextField.setDescription("Bar-> Placement-> Interval");
        this.mBarBodyTransparencySpinner.setDescription("Bar-> Body-> Transparency");
        this.mBarLineWidthSpinner.setDescription("Bar-> Line-> Width");
        this.mBarOffsetXTextField.setDescription("Bar-> Offset-> X");
        this.mBarOffsetYTextField.setDescription("Bar-> Offset-> Y");
        this.mErrorBarSymbolSizeSpinner
                .setDescription("Error Bar-> Symbol-> Size");
        this.mErrorBarLineWidthSpinner
                .setDescription("Error Bar-> Symbol-> Line Width");
        this.mTickLabelFontSizeSpinner
                .setDescription("Tick Label-> Font-> Size");
        this.mTickLabelAngleSpinner
                .setDescription("Tick Label-> Text-> Angle");
        this.mTickLabelDecimalPlacesSpinner
                .setDescription("Tick Label-> Format-> Decimal Places");
        this.mTickLabelExponentSpinner
                .setDescription("Tick Label-> Format-> Exponent");
        this.mTickLabelDateFormatComboBox.setDescription("Tick Label-> Format-> Date");

        // add as a change listener
        this.mTabbedPane.addChangeListener(this);
        this.mBarBodyTransparencySpinner.addChangeListener(this);
        this.mSymbolBodyTransparencySpinner.addChangeListener(this);

        // create a dialog to edit line stroke
        // this.mStrokeDialog = new SGStrokeDialog(this, true);

        // setup gradation paint dialog.
        this.mGradationPaintDialog = new SGGradationPaintDialog(this, "Gradation Paint Dialog", false);
        this.mGradationPaintDialog.pack();
        this.mGradationPaintDialog.setResizable(false);
        this.mGradationPaintDialog.addModelChangeListener(this);

        // setup pattern paint dialog.
        this.mPatternPaintDialog = new SGPatternPaintDialog(this, "Pattern Paint Dialog", false);
        this.mPatternPaintDialog.pack();
        this.mPatternPaintDialog.setResizable(false);
        this.mPatternPaintDialog.addModelChangeListener(this);

        SGComponentGroupElement[] dateTickLabelComponents = {
                new SGComponentGroupElement(this.mTickLabelDateFormatLabel),
                new SGComponentGroupElement(this.mTickLabelDateFormatComboBox)
        };
        this.mDateTickLabelComponentGroup.addElement(dateTickLabelComponents);
        
        SGComponentGroupElement[] tickLabelComponents = {
                new SGComponentGroupElement(this.mTickLabelFontLabel),
                new SGComponentGroupElement(this.mTickLabelFontNameLabel),
                new SGComponentGroupElement(this.mTickLabelFontNameComboBox),
                new SGComponentGroupElement(this.mTickLabelFontStyleLabel),
                new SGComponentGroupElement(this.mTickLabelFontStyleComboBox),
                new SGComponentGroupElement(this.mTickLabelFontSizeLabel),
                new SGComponentGroupElement(this.mTickLabelFontSizeSpinner),
                new SGComponentGroupElement(this.mTickLabelColorLabel),
                new SGComponentGroupElement(this.mTickLabelColorButton),
                new SGComponentGroupElement(this.mTickLabelTextLabel),
                new SGComponentGroupElement(this.mTickLabelAngleLabel),
                new SGComponentGroupElement(this.mTickLabelAngleSpinner),
                new SGComponentGroupElement(this.mTickLabelFormatLabel),
                new SGComponentGroupElement(this.mTickLabelDecimalPlacesLabel),
                new SGComponentGroupElement(this.mTickLabelDecimalPlacesSpinner),
                new SGComponentGroupElement(this.mTickLabelExponentLabel),
                new SGComponentGroupElement(this.mTickLabelExponentBaseLabel),
                new SGComponentGroupElement(this.mTickLabelExponentSpinner),
        		dateTickLabelComponents[0],
        		dateTickLabelComponents[1]
        };
        this.mTickLabelComponentGroup.addElement(tickLabelComponents);

        // sets the calendar icon
        this.setCalendarIcon(this.mShiftXDateButton);
        this.setCalendarIcon(this.mShiftYDateButton);
        this.setCalendarIcon(this.mBarBaselineDateButton);
        this.setCalendarIcon(this.mBarWidthDateButton);
        this.setCalendarIcon(this.mBarIntervalDateButton);
        this.setCalendarIcon(this.mBarOffsetXDateButton);
        this.setCalendarIcon(this.mBarOffsetYDateButton);

        return true;
    }
    
    private void initBarVerticalCheckBox() {
    	Boolean verticalEnabled = null;
    	for (SGIPropertyDialogObserver g : this.mPropertyDialogObserverList) {
    		SGISXYDataDialogObserver gxy = (SGISXYDataDialogObserver) g;
            SGData data = (SGData) gxy.getData();
            final boolean b = !SGDataUtility.isNetCDFData(data);
            if (verticalEnabled == null) {
            	verticalEnabled = Boolean.valueOf(b);
            } else {
                if (b != verticalEnabled.booleanValue()) {
                	verticalEnabled = Boolean.valueOf(false);
                	break;
                }
            }
    	}
    	if (verticalEnabled == null) {
    		verticalEnabled = false;
    	}
		this.mBarVerticalCheckBox.setVisible(verticalEnabled.booleanValue());
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        Object source = e.getSource();
        if (source==this.mGradationPaintDialog) {
            SGGradationPaint gradation = this.mGradationPaintDialog.getGradationPaint();
            this.mBarBodyColorGradationColorButton.setGradationPaint(gradation);
            return;
        } else if (source==this.mPatternPaintDialog) {
            SGPatternPaint patternPaint = this.mPatternPaintDialog.getPatternPaint();
            this.mBarBodyColorPatternPaintButton.setPatternPaint(patternPaint);
            return;
        } else if (source==this.mBarBodyTransparencySpinner) {
            if (null!=this.getTransparency()) {
                this.setBarBodyTransparentComponentEnabled(Integer.valueOf(this.getTransparency().intValue()));
            }
        } else if (source==this.mSymbolBodyTransparencySpinner) {
            if (null!=this.getSymbolInnerTransparency()) {
                this.setSymbolBodyTransparentComponentEnabled(Integer.valueOf(this.getSymbolInnerTransparency().intValue()));
            }
        }
        super.stateChanged(e);
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

    //
    // Line
    //

    /**
     *
     */
    public Boolean getLineVisible() {
        return this.mLineVisibleCheckBox.getSelected();
    }

    /**
     *
     */
    public Number getLineWidth() {
        return this.mLineWidthSpinner.getNumber();
    }

    /**
     *
     */
    public Integer getLineType() {
        final String typeName = (String) this.mLineTypeComboBox
                .getSelectedItem();
        final Integer num = SGDrawingElementLine.getLineTypeFromName(typeName);
        return num;
    }

    /**
     *
     */
    public Color getLineColor() {
        return this.mLineColorButton.getColor();
    }

    /**
     *
     */
    public boolean setLineVisible(final Boolean flag) {
        this.mLineVisibleCheckBox.setSelected(flag);
        this.setLineComponentsEnabled(flag);
        return true;
    }

    /**
     *
     */
    public boolean setLineWidth(final Object obj) {
        return this.setValue(this.mLineWidthSpinner, obj);
    }

    /**
     *
     */
    public boolean setLineType(final Integer type) {
        if (type != null) {
            final String typeName = SGDrawingElementLine.getLineTypeName(type
                    .intValue());
            this.mLineTypeComboBox.setSelectedItem(typeName);
        } else {
            this.mLineTypeComboBox.setSelectedItem(null);
        }

        return true;
    }

    /**
     *
     */
    public boolean setLineColor(final Color cl) {
        this.mLineColorButton.setColor(cl);
        return true;
    }

    /**
     * Returns whether to connect all effective points.
     *
     * @return true if connecting all effective points
     */
    public Boolean getLineConnection() {
        return this.mLineConnectCheckBox.getSelected();
    }

    /**
     * Sets whether to connect all effective points.
     *
     * @param flag
     *           true to connect all effective points
     * @return true if succeeded
     */
    public boolean setLineConnection(final Boolean flag) {
        this.mLineConnectCheckBox.setSelected(flag);
        return true;
    }

    public Number getShiftX() {
        return this.getNumber(this.mShiftXTextField);
    }

    public Number getShiftY() {
        return this.getNumber(this.mShiftYTextField);
    }

    public boolean setShiftX(final Object obj) {
        return this.setValue(this.mShiftXTextField, obj);
    }

    public boolean setShiftY(final Object obj) {
        return this.setValue(this.mShiftYTextField, obj);
    }

    //
    // Symbol
    //

    /**
     *
     */
    public Boolean getSymbolVisible() {
        return this.mSymbolVisibleCheckBox.getSelected();
    }

    /**
     *
     */
    public Number getSymbolSize() {
        return this.mSymbolSizeSpinner.getNumber();
    }

    /**
     *
     */
    public Integer getSymbolType() {
        final String typeName = (String) this.mSymbolTypeComboBox
                .getSelectedItem();
        final Integer num = SGDrawingElementSymbol
                .getSymbolTypeFromName(typeName);
        return num;
    }

    /**
     *
     */
    public Number getSymbolLineWidth() {
        return this.mSymbolLineWidthSpinner.getNumber();
    }

    /**
     *
     */
    public Color getSymbolInnerColor() {
        return this.mSymbolColorButton.getColor();
    }

    public Number getSymbolInnerTransparency() {
        return this.mSymbolBodyTransparencySpinner.getNumber();
    }

    public SGIPaint getSymbolInnerPaint() {
        SGFillPaint innerPaint = new SGFillPaint();
        innerPaint.setColor(this.getSymbolInnerColor());
        innerPaint.setTransparency(this.getSymbolInnerTransparency().intValue());
        return innerPaint;
    }

    /**
     *
     */
    public Color getSymbolLineColor() {
        return this.mSymbolLineColorButton.getColor();
    }

    public Boolean getSymbolLineVisible() {
        return this.mSymbolLineVisibleCheckBox.getSelected();
    }

    /**
     *
     */
    public boolean setSymbolVisible(final Boolean flag) {
        this.mSymbolVisibleCheckBox.setSelected(flag);
        this.setSymbolComponentsEnabled(flag);
        return true;
    }

    /**
     *
     */
    public boolean setSymbolType(final Integer type) {
        if (type != null) {
            final String typeName = SGDrawingElementSymbol
                    .getSymbolTypeName(type.intValue());
            this.mSymbolTypeComboBox.setSelectedItem(typeName);
        } else {
            this.mSymbolTypeComboBox.setSelectedItem(null);
        }
        return true;
    }

    /**
     *
     */
    public boolean setSymbolSize(final Object obj) {
        return this.setValue(this.mSymbolSizeSpinner, obj);
    }

    /**
     *
     */
    public boolean setSymbolLineWidth(final Object obj) {
        return this.setValue(this.mSymbolLineWidthSpinner, obj);
    }

    /**
     *
     */
    public boolean setSymbolInnerColor(final Color cl) {
        this.mSymbolColorButton.setColor(cl);
        return true;
    }

    public boolean setSymbolInnerTransparent(final Integer alpha) {
        setSymbolBodyTransparentComponentEnabled(alpha);
        return this.setValue(this.mSymbolBodyTransparencySpinner, Float.valueOf(alpha.floatValue()));
    }

    public boolean setSymbolInnerPaint(final SGIPaint paint) {
        if (paint instanceof SGFillPaint) {
            SGFillPaint fpaint = (SGFillPaint)paint;
            this.setSymbolInnerColor(SGFillPaint.getOpaqueColor((Color)fpaint.getPaint(null)));
            this.setSymbolInnerTransparent(Integer.valueOf(fpaint.getTransparencyPercent()));
            return true;
        } else {
            return false;
        }
    }

    private boolean setSymbolBodyTransparentComponentEnabled(final Integer alpha) {
        boolean b = true;
        Boolean visible = this.getSymbolVisible();
        if (visible != null) {
            if (visible.booleanValue() == false) {
                b = false;
            }
            if (Integer.valueOf(SGTransparentPaint.ALL_TRANSPARENT_VALUE).equals(alpha)) {
                b = false;
            }
        }
        this.mSymbolColorButton.setEnabled(b);
        this.mSymbolColorLabel.setEnabled(b);
        return true;
    }

    /**
     *
     */
    public boolean setSymbolLineColor(final Color cl) {
        this.mSymbolLineColorButton.setColor(cl);
        return true;
    }

    public boolean setSymbolLineVisible(final Boolean flag) {
        this.mSymbolLineVisibleCheckBox.setSelected(flag);
        this.setSymbolLineComponentsEnabled(flag);
        return true;
    }

    //
    // Bar
    //

    /**
     *
     */
    public Boolean getBarVisible() {
        return this.mBarVisibleCheckBox.getSelected();
    }

    /**
     *
     */
    public Number getBarWidth() {
        return this.getNumber(this.mBarWidthTextField);
    }

    /**
     *
     */
    public Number getBarEdgeLineWidth() {
        return this.mBarLineWidthSpinner.getNumber();
    }

    /**
     *
     */
    public Number getBarBaselineValue() {
        return this.getNumber(this.mBarBaselineTextField);
    }

    /**
     *
     */
    public Color getBarEdgeLineColor() {
        return this.mBarLineColorButton.getColor();
    }

    public Boolean getBarEdgeLineVisible() {
        return this.mBarLineVisibleCheckBox.getSelected();
    }

    /**
     *
     */
    public Color getBarBodyFillPaintColor() {
        return this.mBarInnerColorButton.getColor();
    }

    public SGPatternPaint getBarBodyPatternPaint() {
        return this.mBarBodyColorPatternPaintButton.getPatternPaint();
    }

    public SGGradationPaint getBarBodyGradationPaint() {
        return this.mBarBodyColorGradationColorButton.getGradationPaint();
    }

    public Integer getBarBodyColorStyle() {
        int style;
        if (this.mBarBodyColorFillRadioButton.isSelected()) {
            style = SGSelectablePaint.STYLE_INDEX_FILL;
        } else if (this.mBarBodyColorPatternRadioButton.isSelected()) {
            style = SGSelectablePaint.STYLE_INDEX_PATTERN;
        } else if (this.mBarBodyColorGradationRadioButton.isSelected()) {
            style = SGSelectablePaint.STYLE_INDEX_GRADATION;
        } else {
            style = SGSelectablePaint.STYLE_INDEX_FILL;
        }
        return Integer.valueOf(style);
    }

    public Number getTransparency() {
        return this.mBarBodyTransparencySpinner.getNumber();
    }

    public SGIPaint getBarBodyPaint() {
        SGSelectablePaint selectablePaint = new SGSelectablePaint();
        selectablePaint.setFillColor(this.getBarBodyFillPaintColor());
        selectablePaint.setPatternPaint(this.getBarBodyPatternPaint());
        selectablePaint.setGradationPaint(this.getBarBodyGradationPaint());
        selectablePaint.setSelectedPaintStyle(this.getBarBodyColorStyle().intValue());
        selectablePaint.setTransparency(this.getTransparency().intValue());
        return selectablePaint;
    }

    public Boolean isBarVertical() {
        return this.mBarVerticalCheckBox.getSelected();
    }

    public Number getBarInterval() {
    	/*
        if (this.mBarIntervalTextField.isEnabled()) {
            return this.getNumber(this.mBarIntervalTextField);
        } else {
            return SGUtilityText.getDouble(this.mTemporaryBarIntervalText);
        }
        */
        return this.getNumber(this.mBarIntervalTextField);
    }

    public boolean setBarBodyFillPaintColor(final Color color) {
        this.mBarInnerColorButton.setColor(color);
        return true;
    }

    public boolean setBarBodyPatternPaint(final SGPatternPaint paint) {
        if (paint != null) {
            this.mBarBodyColorPatternPaintButton.setPatternPaint(paint);
            return true;
        } else {
            return false;
        }
    }

    public boolean setBarBodyGradationPaint(final SGGradationPaint paint) {
        if (paint != null) {
            this.mBarBodyColorGradationColorButton.setGradationPaint(paint);
            return true;
        } else {
            return false;
        }
    }

    public boolean setBarBodyColorStyle(final Object obj) {
        if (obj == null) {
            this.mNoBarBodyColorStyleSelectionRadioButton.setSelected(true);
            return false;
        }
        Integer style = null;
        try {
            style = Integer.valueOf(obj.toString());
        } catch (NumberFormatException ex) {
            return false;
        }

        switch (style.intValue()) {
        case SGSelectablePaint.STYLE_INDEX_FILL :
            this.mBarBodyColorFillRadioButton.setSelected(true);
            break;
        case SGSelectablePaint.STYLE_INDEX_PATTERN :
            this.mBarBodyColorPatternRadioButton.setSelected(true);
            break;
        case SGSelectablePaint.STYLE_INDEX_GRADATION :
            this.mBarBodyColorGradationRadioButton.setSelected(true);
            break;
        default:
            throw new Error();
        }
        return true;
    }

    /**
     *
     */
    public boolean setBarVisible(final Boolean flag) {
        this.mBarVisibleCheckBox.setSelected(flag);
        this.setBarComponentsEnabled(flag);
        return true;
    }

    /**
     *
     */
    public boolean setBarWidth(final Object obj) {
        // return this.setValue( this.mBarWidthSpinner, obj );
        return this.setValue(this.mBarWidthTextField, obj);
    }

    /**
     *
     */
    public boolean setBarEdgeLineWidth(final Object obj) {
        return this.setValue(this.mBarLineWidthSpinner, obj);
    }

    /**
     *
     */
    public boolean setBarBaselineValue(final Object obj) {
        return this.setValue(this.mBarBaselineTextField, obj);
    }

    public boolean setBarInnerPaint(final SGIPaint paint) {
        if (paint instanceof SGSelectablePaint) {
            SGSelectablePaint selectablePaint = (SGSelectablePaint)paint;
            this.setBarBodyColorStyle(selectablePaint.getSelectedStyle());
            this.setBarBodyFillPaintColor(SGFillPaint.getOpaqueColor(selectablePaint.getFillColor()));
            this.setBarBodyPatternPaint(selectablePaint.getPatternPaint());
            this.setBarBodyGradationPaint(selectablePaint.getGradationPaint());
            this.setBarBodyTransparent(Integer.valueOf(selectablePaint.getTransparencyPercent()));
            return true;
        } else if (paint instanceof SGFillPaint) {
            SGFillPaint fpaint = (SGFillPaint)paint;
            this.setBarBodyColorStyle(SGSelectablePaint.STYLE_INDEX_FILL);
            this.setBarBodyFillPaintColor(SGFillPaint.getOpaqueColor((Color)fpaint.getPaint(null)));
            this.setBarBodyTransparent(Integer.valueOf(fpaint.getTransparencyPercent()));
            return true;
        } else if (paint instanceof SGPatternPaint) {
            SGPatternPaint ppaint = (SGPatternPaint)paint;
            this.setBarBodyColorStyle(SGSelectablePaint.STYLE_INDEX_PATTERN);
            this.setBarBodyPatternPaint(ppaint);
            this.setBarBodyTransparent(Integer.valueOf(ppaint.getTransparencyPercent()));
            return true;
        } else if (paint instanceof SGGradationPaint) {
            SGGradationPaint gpaint = (SGGradationPaint)paint;
            this.setBarBodyColorStyle(SGSelectablePaint.STYLE_INDEX_GRADATION);
            this.setBarBodyGradationPaint(gpaint);
            this.setBarBodyTransparent(Integer.valueOf(gpaint.getTransparencyPercent()));
            return true;
        } else {
            return false;
        }
    }

    public boolean setBarBodyTransparent(final Integer alpha) {
        setBarBodyTransparentComponentEnabled(alpha);
        return this.setValue(this.mBarBodyTransparencySpinner, Float.valueOf(alpha.floatValue()));
    }

    private boolean setBarBodyTransparentComponentEnabled(final Integer alpha) {
        if (alpha.equals(Integer.valueOf(SGTransparentPaint.ALL_TRANSPARENT_VALUE))) {
            return setBarBodyTransparentComponentEnabled(Boolean.FALSE);
        } else {
            return setBarBodyTransparentComponentEnabled(Boolean.TRUE);
        }
    }

    private boolean setBarBodyTransparentComponentEnabled(final Boolean flag) {
        if (this.mBarBodyTransparentComponentGroup.isEnabled()!=flag.booleanValue()) {
            this.mBarBodyTransparentComponentGroup.setEnabled(flag.booleanValue());
        }
        return true;
    }

    /**
     *
     */
    public boolean setBarEdgeLineColor(final Color cl) {
        this.mBarLineColorButton.setColor(cl);
        return true;
    }

    public boolean setBarEdgeLineVisible(final Boolean visible) {
        this.mBarLineVisibleCheckBox.setSelected(visible);
        this.setBarEdgeLineComponentsEnabled(visible);
        return true;
    }

    public boolean setBarVertical(final Boolean flag) {
        this.mBarVerticalCheckBox.setSelected(flag);
        return true;
    }

    public boolean setBarInterval(final Object obj) {
        return this.setValue(this.mBarIntervalTextField, obj);
    }

    public Number getBarOffsetX() {
        return this.getNumber(this.mBarOffsetXTextField);
    }

    public Number getBarOffsetY() {
        return this.getNumber(this.mBarOffsetYTextField);
    }

    public boolean setBarOffsetX(final Object obj) {
        return this.setValue(this.mBarOffsetXTextField, obj);
    }

    public boolean setBarOffsetY(final Object obj) {
        return this.setValue(this.mBarOffsetYTextField, obj);
    }

    //
    // Error Bar
    //

    /**
     *
     */
    public Boolean getErrorBarVisible() {
        return this.mErrorBarVisibleCheckBox.getSelected();
    }

    /**
     *
     */
    public Number getErrorBarSymbolSize() {
        return this.mErrorBarSymbolSizeSpinner.getNumber();
    }

    /**
     *
     */
    public Number getErrorBarLineWidth() {
        return this.mErrorBarLineWidthSpinner.getNumber();
    }

    /**
     *
     */
    public Color getErrorBarColor() {
        return this.mErrorBarColorButton.getColor();
    }

    /**
     *
     */
    public String getErrorBarTypeName() {
        return (String) this.mErrorBarTypeComboBox.getSelectedItem();
    }

    /**
     *
     */
    public Integer getErrorBarStyle() {
        int style = -1;
        if (this.mErrorBarBothsidesRadioButton.isSelected()) {
            style = SGIErrorBarConstants.ERROR_BAR_BOTHSIDES;
        } else if (this.mErrorBarUpsideRadioButton.isSelected()) {
            style = SGIErrorBarConstants.ERROR_BAR_UPSIDE;
        } else if (this.mErrorBarDownsideRadioButton.isSelected()) {
            style = SGIErrorBarConstants.ERROR_BAR_DOWNSIDE;
        } else {
            return null;
        }

        return Integer.valueOf(style);
    }

    public Boolean getErrorBarOnLinePosition() {
    	Boolean ret = null;
    	if (this.mNoErrorBarPositionRadioButton.isSelected()) {
    		ret = null;
    	} else if (this.mErrorBarPositionLineRadioButton.isSelected()) {
    		ret = Boolean.valueOf(true);
    	} else if (this.mErrorBarPositionBarRadioButton.isSelected()) {
    		ret = Boolean.valueOf(false);
    	}
    	return ret;
    }

    /**
     *
     */
    public boolean setErrorBarVisible(final Boolean flag) {
        this.mErrorBarVisibleCheckBox.setSelected(flag);
        this.setErrorBarComponentsEnabled(flag);
        return true;
    }

    /**
     *
     */
    public boolean setErrorBarSymbolSize(final Object obj) {
        return this.setValue(this.mErrorBarSymbolSizeSpinner, obj);
    }

    /**
     *
     */
    public boolean setErrorBarLineWidth(final Object obj) {
        return this.setValue(this.mErrorBarLineWidthSpinner, obj);
    }

    /**
     *
     */
    public boolean setErrorBarColor(final Color cl) {
        this.mErrorBarColorButton.setColor(cl);
        return true;
    }

    /**
     *
     */
    public boolean setErrorBarType(final String typeName) {
        this.mErrorBarTypeComboBox.setSelectedItem(typeName);
        return true;
    }

    /**
     *
     */
    public boolean setErrorBarStyle(final Object obj) {
        if (obj == null) {
            this.mNoErrorBarStyleSelectionRadioButton.setSelected(true);
            return false;
        }
        Integer style = null;
        try {
            style = Integer.valueOf(obj.toString());
        } catch (NumberFormatException ex) {
            return false;
        }

        switch (style.intValue()) {
        case SGIErrorBarConstants.ERROR_BAR_BOTHSIDES: {
            this.mErrorBarBothsidesRadioButton.setSelected(true);
            break;
        }

        case SGIErrorBarConstants.ERROR_BAR_UPSIDE: {
            this.mErrorBarUpsideRadioButton.setSelected(true);
            break;
        }

        case SGIErrorBarConstants.ERROR_BAR_DOWNSIDE: {
            this.mErrorBarDownsideRadioButton.setSelected(true);
            break;
        }

        default: {
            throw new Error();
        }
        }

        return true;
    }

    public boolean setErrorBarOnLinePosition(final Boolean b) {
    	if (b == null) {
    		this.mNoErrorBarPositionRadioButton.setSelected(true);
    	} else {
    		if (b.booleanValue()) {
    			this.mErrorBarPositionLineRadioButton.setSelected(true);
    		} else {
    			this.mErrorBarPositionBarRadioButton.setSelected(true);
    		}
    	}
        return true;
    }

    //
    // Tick Label
    //

    /**
     *
     */
    public Boolean getTickLabelVisible() {
        return this.mTickLabelVisibleCheckBox.getSelected();
    }

    /**
     *
     */
    public String getTickLabelFontName() {
        return (String) this.mTickLabelFontNameComboBox.getSelectedItem();
    }

    /**
     *
     */
    public Number getTickLabelFontSize() {
        return this.mTickLabelFontSizeSpinner.getNumber();
    }

    /**
     *
     */
    public Integer getTickLabelFontStyle() {
        final String name = (String) this.mTickLabelFontStyleComboBox
                .getSelectedItem();
        return SGUtilityText.getFontStyle(name);
    }

    public Number getTickLabelAngle() {
        return this.mTickLabelAngleSpinner.getNumber();
    }

    /**
     *
     */
    public Color getTickLabelColor() {
        return this.mTickLabelColorButton.getColor();
    }

    public Number getTickLabelDecimalPlaces() {
        return this.mTickLabelDecimalPlacesSpinner.getNumber();
    }

    public Number getTickLabelExponent() {
        return this.mTickLabelExponentSpinner.getNumber();
    }

    public String getTickLabelDateFormat() {
        return (String) this.mTickLabelDateFormatComboBox.getSelectedItem();
    }

    /**
     *
     */
    public boolean setTickLabelVisible(final Boolean flag) {
        this.mTickLabelVisibleCheckBox.setSelected(flag);
        this.setTickLabelComponentsEnabled(flag);
        return true;
    }

    /**
     * Sets the tick label font name.
     *
     * @param name
     *           the font name to set
     * @return true if succeeded
     */
    public boolean setTickLabelFontName(final String name) {
    	final String fName = SGUtility.findFontFamilyName(name);
        this.mTickLabelFontNameComboBox.setSelectedItem(fName);
        return true;
    }

    /**
     *
     */
    public boolean setTickLabelFontSize(final Object obj) {
        return this.setValue(this.mTickLabelFontSizeSpinner, obj);
    }

    /**
     *
     */
    public boolean setTickLabelFontStyle(final Object obj) {
        if (obj == null) {
            this.mTickLabelFontStyleComboBox.setSelectedItem(null);
            return false;
        }
        Integer style = null;
        try {
            style = Integer.valueOf(obj.toString());
        } catch (NumberFormatException ex) {
            return false;
        }
        this.mTickLabelFontStyleComboBox.setSelectedItem(SGUtilityText
                .getFontStyleName(style.intValue()));
        return true;
    }

    public boolean setTickLabelAngle(final Object obj) {
        return this.setValue(this.mTickLabelAngleSpinner, obj);
    }

    /**
     *
     */
    public boolean setTickLabelColor(final Color cl) {
        this.mTickLabelColorButton.setColor(cl);
        return true;
    }

    public boolean setTickLabelDecimalPlaces(final Object obj) {
        return this.setValue(this.mTickLabelDecimalPlacesSpinner, obj);
    }

    public boolean setTickLabelExponent(final Object obj) {
        return this.setValue(this.mTickLabelExponentSpinner, obj);
    }

    public boolean setTickLabelDateFormat(final String format) {
        this.mTickLabelDateFormatComboBox.setSelectedItem(format);
        return true;
    }

    /**
     *
     */
    public List<SGColorSelectionButton> getColorSelectionButtonsList() {
        List<SGColorSelectionButton> list = new ArrayList<SGColorSelectionButton>();
        list.add(this.mLineColorButton);
        list.add(this.mSymbolColorButton);
        list.add(this.mSymbolLineColorButton);
        list.add(this.mBarInnerColorButton);
        list.add(this.mBarLineColorButton);
        list.add(this.mErrorBarColorButton);
        list.add(this.mTickLabelColorButton);
        return list;
    }

    /**
     *
     */
    public List getTextFieldComponentsList() {
        final List list = this.getFormattedTextFieldsListFromSpinners();
        list.addAll(this.getAxisNumberTextFieldList());
        list.add(this.mNameField);
        return list;
    }

    /**
     * Returns a list of text fields to set number.
     *
     * @return
     */
    public List<SGTextField> getAxisNumberTextFieldList() {
        final List<SGTextField> list = new ArrayList<SGTextField>();
        list.add(this.mShiftXTextField);
        list.add(this.mShiftYTextField);
        list.add(this.mBarBaselineTextField);
        list.add(this.mBarWidthTextField);
        list.add(this.mBarOffsetXTextField);
        list.add(this.mBarOffsetYTextField);
        list.add(this.mBarIntervalTextField);
        return list;
    }

    /**
     *
     */
    public List<SGSpinner> getSpinnerList() {
        List<SGSpinner> list = new ArrayList<SGSpinner>();
        list.add(this.mLineWidthSpinner);
        list.add(this.mSymbolSizeSpinner);
        list.add(this.mSymbolLineWidthSpinner);
        list.add(this.mSymbolBodyTransparencySpinner);
        list.add(this.mBarLineWidthSpinner);
        list.add(this.mBarBodyTransparencySpinner);
        list.add(this.mErrorBarLineWidthSpinner);
        list.add(this.mErrorBarSymbolSizeSpinner);
        list.add(this.mTickLabelFontSizeSpinner);
        list.add(this.mTickLabelAngleSpinner);
        list.add(this.mTickLabelDecimalPlacesSpinner);
        list.add(this.mTickLabelExponentSpinner);
        return list;
    }

    /**
     *
     */
    public boolean setTabEnabled(final Component com, final boolean flag) {
        if (com == null) {
            return false;
        }
        final int index = this.mTabbedPane.indexOfComponent(com);
        if (index == -1) {
            return false;
        }

        this.mTabbedPane.setEnabledAt(index, flag);

        // // set enabled all components on the panel
        // this.setComponentsOnPanelEnabled(com,flag);

        return true;
    }

    /**
     *
     * @param com
     * @return
     */
    public boolean isTabEnabled(final Component com) {
        if (com == null) {
            return false;
        }
        final int index = this.mTabbedPane.indexOfComponent(com);
        if (index == -1) {
            return false;
        }
        return this.mTabbedPane.isEnabledAt(index);
    }

    // /**
    // *
    // * @param p
    // * @return
    // */
    // protected boolean setComponentsOnPanelEnabled( final Component com, final
    // boolean flag )
    // {
    // if( com==null )
    // {
    // return false;
    // }
    //
    // if( com.equals( this.mLinePanel ) )
    // {
    // this.mLineVisibleCheckBox.setEnabled(flag);
    // this.setLineComponentsEnabled(flag&&this.mLineVisibleCheckBox.isSelected());
    // }
    // else if( com.equals( this.mSymbolPanel ) )
    // {
    // this.mSymbolVisibleCheckBox.setEnabled(flag);
    // this.setSymbolComponentsEnabled(flag&&this.mSymbolVisibleCheckBox.isSelected());
    // }
    // else if( com.equals( this.mBarPanel ) )
    // {
    // this.mBarVisibleCheckBox.setEnabled(flag);
    // this.setBarComponentsEnabled(flag&&this.mBarVisibleCheckBox.isSelected());
    // }
    // else if( com.equals( this.mErrorBarPanel ) )
    // {
    // this.mErrorBarVisibleCheckBox.setEnabled(flag);
    // this.setErrorBarComponentsEnabled(flag&&this.mErrorBarVisibleCheckBox.isSelected());
    // }
    // else if( com.equals( this.mTickLabelPanel ) )
    // {
    // this.mTickLabelVisibleCheckBox.setEnabled(flag);
    // this.setTickLabelComponentsEnabled(flag&&this.mTickLabelVisibleCheckBox.isSelected());
    // }
    //
    // return true;
    // }

    /**
     *
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Object source = e.getSource();
        String command = e.getActionCommand();
        if (this.isVisible()) {
            if (source.equals(this.mBarInnerColorButton)) {
                this.mBarBodyColorFillRadioButton.setSelected(true);
            } else if (source.equals(this.mBarBodyColorPatternPaintButton)) {
                this.mBarBodyColorPatternRadioButton.setSelected(true);
            } else if (source.equals(this.mBarBodyColorGradationColorButton)) {
                this.mBarBodyColorGradationRadioButton.setSelected(true);
            }
        }
        if (source instanceof SGCheckBox) {
            if (source.equals(this.mLineVisibleCheckBox)) {
                this.setLineVisible(this.getLineVisible());
            } else if (source.equals(this.mSymbolVisibleCheckBox)) {
                this.setSymbolVisible(this.getSymbolVisible());
            } else if (source.equals(this.mSymbolLineVisibleCheckBox)) {
                this.setSymbolLineVisible(this.getSymbolLineVisible());
            } else if (source.equals(this.mBarVisibleCheckBox)) {
                this.setBarVisible(this.getBarVisible());
            } else if (source.equals(this.mBarLineVisibleCheckBox)) {
                this.setBarEdgeLineVisible(this.getBarEdgeLineVisible());
            } else if (source.equals(this.mErrorBarVisibleCheckBox)) {
                this.setErrorBarVisible(this.getErrorBarVisible());
            } else if (source.equals(this.mTickLabelVisibleCheckBox)) {
                this.setTickLabelVisible(this.getTickLabelVisible());
            } else if (source.equals(this.mLegendVisibleCheckBox)) {
                this.setLegendVisible(this.getLegendVisible());
            } else if (source.equals(this.mBarVerticalCheckBox)) {
            	this.setUpBarComponentGroup();
            	this.setBarDateComponentsEnabled(
            			this.mDateXComponentGroup.isEnabled(),
            			this.mDateYComponentGroup.isEnabled());
            }
        } else if (source instanceof JButton) {
        	if (source.equals(this.mLineEditButton)) {
                // this.showLineEditDialog();
            } else if (source.equals(this.mDataColumnSelectionButton)) {
                this.showDataColumnSelectionDialog();
            } else if (source.equals(this.mLineStyleCustomizeButton)) {
            	this.showLineStyleDialog();
            } else if (source.equals(this.mBarBodyColorPatternPaintButton)) {
                if (! this.mPatternPaintDialog.isVisible()) {
                    this.mPatternPaintDialog.setSelectedPatternPaint(this.mBarBodyColorPatternPaintButton.getPatternPaint());
                    this.mPatternPaintDialog.setLocation(
                            this.mBarBodyColorPatternPaintButton.getX() + 20,
                            this.mBarBodyColorPatternPaintButton.getY() + 20);
                    this.mPatternPaintDialog.setVisible(true);
                }
            } else if (source.equals(this.mBarBodyColorGradationColorButton)) {
                if (! this.mGradationPaintDialog.isVisible()) {
                    this.mGradationPaintDialog.setSelectedGradationPaint(this.mBarBodyColorGradationColorButton.getGradationPaint());
                    this.mGradationPaintDialog.setLocation(
                            this.mBarBodyColorGradationColorButton.getX() + 20,
                            this.mBarBodyColorGradationColorButton.getY() + 20);
                    this.mGradationPaintDialog.setVisible(true);
                }
            } else if (source.equals(this.mShiftXDateButton)) {
            	this.onPeriodButtonPressedForDoubleValue(this.mShiftXTextField);
            } else if (source.equals(this.mShiftYDateButton)) {
            	this.onPeriodButtonPressedForDoubleValue(this.mShiftYTextField);
            } else if (source.equals(this.mBarOffsetXDateButton)) {
            	this.onPeriodButtonPressedForDoubleValue(this.mBarOffsetXTextField);
            } else if (source.equals(this.mBarOffsetYDateButton)) {
            	this.onPeriodButtonPressedForDoubleValue(this.mBarOffsetYTextField);
            } else if (source.equals(this.mBarBaselineDateButton)) {
            	this.onDateButtonPressed(this.mBarBaselineTextField);
            } else if (source.equals(this.mBarWidthDateButton)) {
            	this.onPeriodButtonPressedForDoubleValue(this.mBarWidthTextField);
            } else if (source.equals(this.mBarIntervalDateButton)) {
            	this.onPeriodButtonPressedForDoubleValue(this.mBarIntervalTextField);
            }
        } else if (source.equals(this.mDataColumnSelectionDialog)) {
            SGDataSetupDialog dg = (SGDataSetupDialog) source;
            if (OK_BUTTON_TEXT.equals(command)) {
                // get column types
            	SGDataColumnInfo[] colInfo = dg.getDataColumnInfoSet().getDataColumnInfoArray();

                // check selected tab
                boolean ebFlag = false;
                boolean tlFlag = false;
                for (int ii = 0; ii < colInfo.length; ii++) {
                    final String colType = colInfo[ii].getColumnType();
                    final String valueType = colInfo[ii].getValueType();
                    if (colType == null) {
                    	continue;
                    }
                    if (colType.startsWith(LOWER_ERROR_VALUE)
                    		|| colType.startsWith(UPPER_ERROR_VALUE)
                    		|| colType.startsWith(LOWER_UPPER_ERROR_VALUE)) {
                        ebFlag = true;
                    } else if (colType.startsWith(TICK_LABEL)) {
                        tlFlag = true;
                    } else if (VALUE_TYPE_DATE.equals(valueType)) {
                    	if (X_VALUE.equals(colType) || Y_VALUE.equals(colType)) {
                    		tlFlag = true;
                    	}
                    }
                }
                this.setTabEnabled(this.mErrorBarPanel, ebFlag);
                this.setTabEnabled(this.mTickLabelPanel, tlFlag);
                Component cur = this.mTabbedPane.getSelectedComponent();
                if (!ebFlag) {
                    if (this.mErrorBarPanel.equals(cur)) {
                        this.mTabbedPane.setSelectedComponent(this.mLinePanel);
                    }
                }
                if (!tlFlag) {
                    if (this.mTickLabelPanel.equals(cur)) {
                        this.mTabbedPane.setSelectedComponent(this.mLinePanel);
                    }
                }

                SGISXYDataDialogObserver obs = (SGISXYDataDialogObserver) this.mPropertyDialogObserverList.get(0);
                SGData data = obs.getData();
                SGIntegerSeriesSet pickUpIndices = null;
                SGIntegerSeriesSet pickUpIndicesOld = null;
                if (SGDataUtility.isSDArrayData(data)) {
                	// text data
                    this.setupBarVerticalByChangingTextDataColumn(colInfo, data);

                    // get stride
                    SGSDArrayDataSetupDialog sdg = (SGSDArrayDataSetupDialog) dg;
                    SGIntegerSeriesSet stride = sdg.getSXYStride();
                    this.mStrideMap.put(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE, stride);
                    SGIntegerSeriesSet tickLabelStride = sdg.getSXYTickLabelStride();
                    this.mStrideMap.put(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE, tickLabelStride);

                } else if (SGDataUtility.isNetCDFData(data)) {
                    // for netCDF data, changes the direction of error bars and tick labels
                    // automatically.
                    // but if change whether Pickup column type exists, data and data type are
                    // converted and then must not be change bar vertical.
                    if (obs instanceof SGElementGroupSetForData) {
                        this.setupBarVerticalByChangingNetCDFDataColumn(colInfo, data);
                    }

                    // get multiple origin and step
                    SGNetCDFDataSetupDialog ndg = (SGNetCDFDataSetupDialog) dg;
                    String dimName = ndg.getPickUpDimensionName();
                    pickUpIndices = ndg.getSXYPickUpIndices();

                    if (this.mPickUpDimensionInfo != null) {
                        pickUpIndicesOld = this.mPickUpDimensionInfo.getIndices();
                    }
                    
                    // set to the attribute
                    if (pickUpIndices != null) {
                        this.mPickUpDimensionInfo = new SGNetCDFPickUpDimensionInfo(dimName, pickUpIndices);
                    } else {
                    	this.mPickUpDimensionInfo = null;
                    }

                    // get stride
                    SGNetCDFData nData = (SGNetCDFData) data;
                    if (nData.isIndexAvailable()) {
                        SGIntegerSeriesSet indexStride = ndg.getSXYIndexStride();
                        this.mStrideMap.put(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE, indexStride);
                    } else {
                        SGIntegerSeriesSet stride = ndg.getSXYStride();
                        this.mStrideMap.put(SGIDataInformationKeyConstants.KEY_SXY_STRIDE, stride);
                    }
                    SGIntegerSeriesSet tickLabelStride = ndg.getSXYTickLabelStride();
                    this.mStrideMap.put(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE, tickLabelStride);

                } else if (SGDataUtility.isMDArrayData(data)) {
                	SGMDArrayDataSetupDialog ndg = (SGMDArrayDataSetupDialog) dg;

                    // get multiple origin and step
                    List<String> pickUpVarNameList = ndg.getPickUpDatasetName();
                    Map<String, Integer> dimensionIndexMap = ndg.getPickUpDimensionIndexMap();
                    pickUpIndices = ndg.getIndices();

                    if (this.mPickUpDimensionInfo != null) {
                        pickUpIndicesOld = this.mPickUpDimensionInfo.getIndices();
                    }

                    // set to the attribute
                    if (pickUpVarNameList != null && pickUpIndices != null) {
                        this.mPickUpDimensionInfo = new SGMDArrayPickUpDimensionInfo(
                        		dimensionIndexMap, pickUpIndices);
                        for (int ii = 0; ii < colInfo.length; ii++) {
                        	SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfo[ii];
                        	String name = mdInfo.getName();
                        	if (pickUpVarNameList.contains(name)) {
                                Integer dimensionIndex = dimensionIndexMap.get(name);
                        		mdInfo.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, dimensionIndex);
                        		break;
                        	}
                        }
                    } else {
                    	this.mPickUpDimensionInfo = null;
                        for (int ii = 0; ii < colInfo.length; ii++) {
                        	SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfo[ii];
                    		mdInfo.clearDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
                        }
                    }

                    // get stride
                    SGIntegerSeriesSet stride = ndg.getSXYStride();
                    this.mStrideMap.put(SGIDataInformationKeyConstants.KEY_SXY_STRIDE, stride);
                    SGIntegerSeriesSet tickLabelStride = ndg.getSXYTickLabelStride();
                    this.mStrideMap.put(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE, tickLabelStride);
                }
                
                // updates the line style
                if (this.mDataInfoArray != null) {
                	if (!SGDataUtility.hasEqualColumnType(this.mDataInfoArray, colInfo)) {
                        this.updateLineStyle(obs, colInfo);
                	} else if (this.mPickUpDimensionInfo != null) {
                		if (!SGUtility.equals(pickUpIndicesOld, pickUpIndices)) {
                            this.updateLineStyle(obs, colInfo);
                		}
                	}
                } else {
                    this.updateLineStyle(obs, colInfo);
                }
                
                // set to the attribute
                this.mDataInfoArray = colInfo;
            }
            
        } else if (source.equals(this.mLineStyleDialog)) {
            SGLineStyleDialog dg = (SGLineStyleDialog) source;
            if (OK_BUTTON_TEXT.equals(command)) {
            	// sets to the attribute
            	this.mLineStyleList = dg.getLineStyleList();
            	this.mLineColorMapName = dg.getSelectedColorMapName();
            	this.mLineColorAutoAssigned = dg.isLineColorAutoAssigned();
            	
            	// updates to the components for the line style
            	this.updateLineStyleComponents(this.mLineStyleList);
            }
        }
    }
    
    // updates the line style
    private void updateLineStyle(SGISXYDataDialogObserver obs, SGDataColumnInfo[] cols) {
    	
    	SGData data = obs.getData();
    	final boolean pickedUp = SGDataUtility.isPickupColumnContained(cols);
        int xNum = 0;
        int yNum = 0;
        final int childNum;
        if(pickedUp) {
        	SGIntegerSeriesSet indices = this.mPickUpDimensionInfo.getIndices();
        	childNum = indices.getLength();
        } else {
            for (int ii = 0; ii < cols.length; ii++) {
            	String columnType = cols[ii].getColumnType();
            	if (X_VALUE.equals(columnType)) {
            		xNum++;
            	} else if (Y_VALUE.equals(columnType)) {
            		yNum++;
            	}
            }
            if (xNum > 1) {
            	childNum = xNum;
            } else {
            	childNum = yNum;
            }
        }
        
        final List<SGLineStyle> lineStyleList;
        if (this.mLineColorAutoAssigned) {
            lineStyleList = SGUtilityForFigureElement.createLineStyleList(obs.getLineColorMap(), childNum);
        } else {
        	lineStyleList = new ArrayList<SGLineStyle>();
        	for (int ii = 0; ii < childNum; ii++) {
        		SGLineStyle style = new SGLineStyle(DEFAULT_LINE_TYPE, DEFAULT_LINE_COLOR, DEFAULT_LINE_WIDTH);
        		lineStyleList.add(style);
        	}
        }
        
        // sets common values
        Number lineWidth = this.mLineWidthSpinner.getNumber();
        Object lineTypeItem = this.mLineTypeComboBox.getSelectedItem();
        Color lineColor = this.mLineColorButton.getColor();
        for (int ii = 0; ii < lineStyleList.size(); ii++) {
        	SGLineStyle lineStyle = lineStyleList.get(ii);
        	if (lineWidth != null) {
        		lineStyle.setLineWidth(lineWidth.floatValue());
        	}
        	if (lineTypeItem != null) {
        		final int lineType = SGDrawingElementLine.getLineTypeFromName(
        				lineTypeItem.toString());
        		lineStyle.setLineType(lineType);
        	}
        	if (lineColor != null) {
        		lineStyle.setColor(lineColor);
        	}
        }
        
        // updates the list of line style
        this.mLineStyleList = new ArrayList<SGLineStyle>();
        for (SGLineStyle style : lineStyleList) {
        	this.mLineStyleList.add((SGLineStyle) style.clone());
        }
        
        // updates the list of child name
        List<String> nameList = new ArrayList<String>();
        if (pickedUp) {
            if (SGDataUtility.isNetCDFData(data)) {
            	SGNetCDFPickUpDimensionInfo pickUpInfo = (SGNetCDFPickUpDimensionInfo) this.mPickUpDimensionInfo;
            	SGSXYNetCDFMultipleData sxyData = (SGSXYNetCDFMultipleData) data;
            	nameList.addAll(SGSXYNetCDFMultipleData.getChildNameList(
            			(SGSXYNetCDFMultipleData) sxyData, pickUpInfo));
            } else if (SGDataUtility.isMDArrayData(data)) {
            	SGMDArrayPickUpDimensionInfo pickUpInfo = (SGMDArrayPickUpDimensionInfo) this.mPickUpDimensionInfo;
            	nameList.addAll(SGSXYMDArrayMultipleData.getChildNameList(pickUpInfo));
            }
        } else {
        	if (SGDataUtility.isSDArrayData(data)) {
        		nameList.addAll(SGSXYSDArrayMultipleData.getChildNameList(cols));
        	} else if (SGDataUtility.isNetCDFData(data)) {
            	nameList.addAll(SGSXYNetCDFMultipleData.getChildNameList(cols));
            } else if (SGDataUtility.isMDArrayData(data)) {
            	nameList.addAll(SGSXYMDArrayMultipleData.getChildNameList(cols));
            }
        }
        this.mChildNameList = nameList;
        
    	// updates to the components for the line style
        this.updateLineStyleComponents(this.mLineStyleList);
    }
    
	// updates to the components for the line style
    private void updateLineStyleComponents(List<SGLineStyle> lineStyleList) {
    	final int num = lineStyleList.size();
        final Color[] lineColorArray = new Color[num];
        final int[] lineTypeArray = new int[num];
        final float[] lineWidthArray = new float[num];
    	for (int ii = 0; ii < num; ii++) {
    		SGLineStyle style = lineStyleList.get(ii);
    		lineColorArray[ii] = style.getColor();
    		lineTypeArray[ii] = style.getLineType();
    		lineWidthArray[ii] = style.getLineWidth();
    	}
    	final Color color = SGUtility.checkEquality(lineColorArray);
    	final Integer type = SGUtility.checkEquality(lineTypeArray);
    	final Float lineWidth = SGUtility.checkEquality(lineWidthArray);
    	
    	// sets to the components
        this.setLineWidth(lineWidth);
        this.setLineType(type);
        this.setLineColor(color);
        
        // line style dialog
        final boolean available = (this.mPropertyDialogObserverList.size() == 1) && (lineStyleList.size() > 1);
        this.mLineStyleCustomizeButton.setEnabled(available);
    }
    
    private void setupBarVerticalByChangingNetCDFDataColumn(
            SGDataColumnInfo[] colInfo, SGData data) {

        SGNetCDFData ncData = (SGNetCDFData) data;
        SGNetCDFFile ncFile = ncData.getNetcdfFile();

        List<SGDataColumnInfo> xInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> yInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> leInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> ueInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> tlInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> timeInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> indexInfoList = new ArrayList<SGDataColumnInfo>();
        List<SGDataColumnInfo> pickUpInfoList = new ArrayList<SGDataColumnInfo>();
        if (SGDataUtility.isPickupColumnContained(colInfo)) {
            if (!SGDataUtility.getSXYDimensionDataColumnType(
                    colInfo, xInfoList, yInfoList,
                    leInfoList, ueInfoList, tlInfoList, pickUpInfoList,
                    timeInfoList, indexInfoList)) {
                return;
            }
        } else {
            Map<SGDataColumnInfo, SGDataColumnInfo> lNameMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
            Map<SGDataColumnInfo, SGDataColumnInfo> uNameMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
            Map<SGDataColumnInfo, SGDataColumnInfo> tNameMap = new HashMap<SGDataColumnInfo, SGDataColumnInfo>();
            if (!SGDataUtility.getSXYColumnType(
            		colInfo, xInfoList, yInfoList,
                    lNameMap, uNameMap, tNameMap,
                    timeInfoList, indexInfoList, pickUpInfoList)) {
                return;
            }
            leInfoList = new ArrayList<SGDataColumnInfo>(lNameMap.keySet());
            tlInfoList = new ArrayList<SGDataColumnInfo>(tNameMap.keySet());
        }

        // checks whether bars must be vertical
        final SGNetCDFDataColumnInfo xInfo = (SGNetCDFDataColumnInfo) xInfoList.get(0);
        final SGNetCDFDataColumnInfo yInfo = (SGNetCDFDataColumnInfo) yInfoList.get(0);
        SGNetCDFVariable xVar = ncFile.findVariable(xInfo.getName());
        SGNetCDFVariable yVar = ncFile.findVariable(yInfo.getName());
        if (xVar.isCoordinateVariable() || yVar.isCoordinateVariable()) {
            final boolean barVertical = xVar.isCoordinateVariable();
            this.setBarVertical(barVertical);
        }
    }

    private void setupBarVerticalByChangingTextDataColumn(
            SGDataColumnInfo[] colInfo, SGData data) {

        List<Integer> xIndexList = new ArrayList<Integer>();
        List<Integer> yIndexList = new ArrayList<Integer>();
        Map<Integer, Integer> lIndexMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> uIndexMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> tIndexMap = new HashMap<Integer, Integer>();
        if (SGDataUtility.getSXYColumnType(
                colInfo, xIndexList, yIndexList,
                lIndexMap, uIndexMap, tIndexMap) == false) {
            return;
        }

        SGISXYTypeData dataSXY = (SGISXYTypeData) data;
        boolean directionChanged = false;

        // checks whether error bars must be vertical
        List<Integer> lIndexList = new ArrayList<Integer>(lIndexMap.keySet());
        if (lIndexList.size() != 0) {
            final boolean errorBarVertical = SGUtility.containsAll(yIndexList, lIndexList);
            if (!Boolean.valueOf(errorBarVertical).equals(dataSXY.isErrorBarVertical())) {
                directionChanged = true;
            }
        }

        // checks whether tick labels must be aligned horizontally
        if (null != dataSXY.isTickLabelHorizontal()) {
            List<Integer> tickIndexList = new ArrayList<Integer>(tIndexMap.keySet());
            final boolean tickLabelAlignedHorizontally;
            if (tickIndexList.size() != 0) {
                tickLabelAlignedHorizontally = SGUtility.containsAll(yIndexList, tickIndexList);
            } else {
                boolean b = false;
                for (int ii = 0; ii < colInfo.length; ii++) {
                    if (VALUE_TYPE_DATE.equals(colInfo[ii].getValueType())) {
                        b = xIndexList.contains(Integer.valueOf(ii));
                        break;
                    }
                }
                tickLabelAlignedHorizontally = b;
            }
            if (!directionChanged) {
                if (!Boolean.valueOf(tickLabelAlignedHorizontally).equals(dataSXY.isTickLabelHorizontal())) {
                    directionChanged = true;
                }
            }
        }

        // sets bar direction according to error bars and tick labels
        if (directionChanged) {
            Boolean barVertical = this.isBarVertical();
            if (barVertical != null) {
                this.setBarVertical(!barVertical.booleanValue());
            }
        }
    }

    private boolean isOneXYColumn(SGIDataPropertyDialogObserver obs) {
        SGDataColumnInfo[] cols = obs.getDataColumnInfoArray();
        if (this.mDataInfoArray != null) {
            for (int ii = 0; ii < cols.length; ii++) {
                cols[ii] = this.mDataInfoArray[ii];
            }
        }

        int xcount = 0;
        int ycount = 0;
        for (int i = 0; i <  cols.length; i++) {
            if (SGIDataColumnTypeConstants.X_VALUE.equals(cols[i].getColumnType())) {
                xcount++;
            } else if (SGIDataColumnTypeConstants.Y_VALUE.equals(cols[i].getColumnType())) {
                ycount++;
            }
        }
        if (xcount>1 || ycount>1) {
            return false;
        } else {
            return true;
        }
    }

    private boolean getBarIntervalEnabledInMultipleSeriesBars() {
        SGIDataPropertyDialogObserver obs = (SGIDataPropertyDialogObserver) this.mPropertyDialogObserverList.get(0);
        SGData data = obs.getData();
        String dataType = obs.getDataType();

        if (SGDataUtility.isSXYTypeData(dataType)==false) {
            return false;
        }

        if (SGDataUtility.isMultipleData(dataType)) {
            if (SGDataUtility.isNetCDFDimensionData(dataType)) {
                if (obs.getData() instanceof SGSXYNetCDFMultipleData) {
                	SGSXYNetCDFMultipleData ndata = (SGSXYNetCDFMultipleData) obs.getData();
                    if (ndata.getDimensionIndices().length > 1) {
                        return true;
                    }
                }
            } else if (SGDataUtility.isMDArrayDimensionData(data)) {
            	SGSXYMDArrayMultipleData ndata = (SGSXYMDArrayMultipleData) obs.getData();
                if (ndata.getDimensionIndices().length > 1) {
                    return true;
                }
            } else {
                if (this.isOneXYColumn(obs)==false) {
                    return true;
                }
            }
        }
        return false;
    }

//    protected void setDataToDialog(SGIDataPropertyDialogObserver obs) {
//    	super.setDataToDialog(obs);
//
//        SGData data = obs.getData();
//        if (SGDataUtility.isNetCDFData(data)) {
//            SGNetCDFDataSetupDialog dg = (SGNetCDFDataSetupDialog) this.mDataColumnSelectionDialog;
//            SGNetCDFData nData = (SGNetCDFData) data;
//
//            // show or hide the panel for multiple dimension
//            if (nData instanceof SGSXYMultipleDimensionNetCDFData) {
//            	SGISXYDataDialogObserver oxy = (SGISXYDataDialogObserver) obs;
//            	int origin;
//            	if (this.mMultipleDimensionOrigin != null) {
//            		origin = this.mMultipleDimensionOrigin.intValue();
//            	} else {
//            		origin = oxy.getMultipleDimensionOrigin();
//            	}
//            	int step;
//            	if (this.mMultipleDimensionStep != null) {
//            		step = this.mMultipleDimensionStep.intValue();
//            	} else {
//            		step = oxy.getMultipleDimensionStep();
//            	}
//
//            	// set to the dialog
//            	dg.setMultipleDimensionOrigin(origin);
//            	dg.setMultipleDimensionStep(step);
//                dg.setMultipleDimensionPanelVisible(true);
//
//            } else {
//                dg.setMultipleDimensionPanelVisible(false);
//            }
//        }
//    }

    /*
     * // a dialog to edit line stroke private SGStrokeDialog mStrokeDialog =
     * null;
     *  // shows a dialog to edit line stroke private void showLineEditDialog() {
     *  // set the location this.mStrokeDialog.setLocation(this.getLocation());
     *  // set visible this.mStrokeDialog.setVisible(true); }
     */
    /**
     * An error message which is shown when all elements are set to be
     * invisible.
     */
    private static final String ERRMSG_NOT_HIDE_ALL_ELEMENTS = "Lines, symbols and bars cannot be hidden at the same time.";

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

        // Enabled to hide all types of drawing elements at the same time.
        /*
        // if all drawing elements for any one data are hidden,
        // show an error message dialog
        if (this.isAnyElementsShown() == false) {
            this.setInputErrorMessage(ERRMSG_NOT_HIDE_ALL_ELEMENTS);
            valid = false;
        }
        */

        // check the data name
        if (this.mNameField.hasValidText() == false) {
            this.addInputErrorDescription(this.mNameField.getDescription());
            valid = false;
        }

        if (this.hasValidNumber(this.mBarWidthTextField, 0.0, null) == false) {
        	valid = false;
        }
        if (this.hasValidNumber(this.mBarBaselineTextField) == false) {
        	valid = false;
        }
        if (this.mBarIntervalTextField.isEnabled() &&
                this.hasValidNumber(this.mBarIntervalTextField) == false) {
            valid = false;
        }
        if (this.hasValidNumber(this.mShiftXTextField) == false) {
            valid = false;
        }
        if (this.hasValidNumber(this.mShiftYTextField) == false) {
            valid = false;
        }
        if (this.hasValidNumber(this.mBarOffsetXTextField) == false) {
            valid = false;
        }
        if (this.hasValidNumber(this.mBarOffsetYTextField) == false) {
            valid = false;
        }

        if (this.isTabEnabled(this.mTickLabelPanel)) {
        	if (!SGDateUtility.checkDateComboBoxInputValidatity(
        			this.mTickLabelDateFormatComboBox)) {
            	String desc = this.mTickLabelDateFormatComboBox.getDescription();
            	this.addInputErrorDescription(desc);
            	valid = false;
        	}
        }

        return valid;
    }

    private boolean isAnyElementsShown() {
        final Boolean line = this.getLineVisible();
        final Boolean symbol = this.getSymbolVisible();
        final Boolean bar = this.getBarVisible();

        List list = this.mPropertyDialogObserverList;
        for (int ii = 0; ii < list.size(); ii++) {
            SGISXYDataDialogObserver obs = (SGISXYDataDialogObserver) list
                    .get(ii);
            boolean lineVisible;
            boolean symbolVisible;
            boolean barVisible;

            if (line != null) {
                lineVisible = line.booleanValue();
            } else {
                lineVisible = obs.isLineVisible();
            }

            if (symbol != null) {
                symbolVisible = symbol.booleanValue();
            } else {
                symbolVisible = obs.isSymbolVisible();
            }

            if (bar != null) {
                barVisible = bar.booleanValue();
            } else {
                barVisible = obs.isBarVisible();
            }

            if (!lineVisible && !symbolVisible && !barVisible) {
                return false;
            }
        }

        return true;
    }

    public static final String ERRMSG_BASELINE_VALUE_INVALID = "Baseline value is invalid.";

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
            SGISXYDataDialogObserver l = (SGISXYDataDialogObserver) this.mPropertyDialogObserverList.get(0);
            SGData data = l.getData();

        	// sets the stride
            if (SGDataUtility.isSDArrayData(data)) {
            	SGIntegerSeriesSet stride = this.mStrideMap.get(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
            	if (!l.setSDArrayStride(stride)) {
            		return false;
            	}
                SGIntegerSeriesSet tickLabelStride = this.mStrideMap.get(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
                if (!l.setTickLabelStride(tickLabelStride)) {
                	return false;
                }
            }
            if (data instanceof SGIIndexData) {
            	SGIIndexData indexData = (SGIIndexData) data;
            	if (indexData.isIndexAvailable()) {
                	SGIntegerSeriesSet indexStride = this.mStrideMap.get(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
                	if (!l.setIndexStride(indexStride)) {
                		return false;
                	}
            	}
            }
            l.setStrideAvailable(this.mDataColumnSelectionDialog.isStrideAvailable());

            // set pick up info
            if (SGDataUtility.isNetCDFData(data) || SGDataUtility.isMDArrayData(data)) {
            	if (!l.setPickUpDimensionInfo(this.mPickUpDimensionInfo)) {
            		return false;
            	}
            	SGIntegerSeriesSet stride = this.mStrideMap.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
            	if (!l.setStride(stride)) {
            		return false;
            	}
                SGIntegerSeriesSet tickLabelStride = this.mStrideMap.get(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
                if (!l.setTickLabelStride(tickLabelStride)) {
                	return false;
                }
            }

            // set data columns
		    if (l.setColumnInfo(this.mDataInfoArray, this.mCommitActionStateMessage) == false) {
		        return false;
		    }
        }
        return true;
    }

    /**
     *
     */
    public boolean setPropertiesToObserver(SGIPropertyDialogObserver l) {
        SGISXYDataDialogObserver lxy = (SGISXYDataDialogObserver) l;

        // set the related axes
        final int xLocation = this.mAxisPanel.getXAxisLocation();
        final int yLocation = this.mAxisPanel.getYAxisLocation();
        if (xLocation != -1) {
            lxy.setXAxisLocation(xLocation);
        }
        if (yLocation != -1) {
            lxy.setYAxisLocation(yLocation);
        }

        // data name
        String name = this.getDataName();
        if (name != null) {
            lxy.setName(name);
        }

        // visibility in legend
        Boolean legendVisible = this.getLegendVisible();
        if (legendVisible != null) {
            lxy.setVisibleInLegend(legendVisible.booleanValue());
        }

        // data shift
        final Number shiftX = this.getShiftX();
        if (shiftX != null) {
            lxy.setShiftX(shiftX.doubleValue());
        }
        final Number shiftY = this.getShiftY();
        if (shiftY != null) {
            lxy.setShiftY(shiftY.doubleValue());
        }

        // line and symbols
        if (this.isTabEnabled(this.mLinePanel)) {
            if (this.setLineProperties(lxy) == false) {
                return false;
            }
            if (this.setSymbolProperties(lxy) == false) {
                return false;
            }
        }

        // bars
        if (this.isTabEnabled(this.mBarPanel)) {
            if (this.setBarProperties(lxy) == false) {
                return false;
            }
        }

        // error bars
        if (this.isTabEnabled(this.mErrorBarPanel)) {
            if (this.setErrorBarProperties(lxy) == false) {
                return false;
            }
        }

        // tick labels
        if (this.isTabEnabled(this.mTickLabelPanel)) {
            if (this.setTickLabelProperties(lxy) == false) {
                return false;
            }
        }

        return true;
    }


    private void setLineComponentsEnabled(Boolean flag) {
        boolean b = true;
        if (flag != null) {
            b = flag.booleanValue();
        }
        this.mLineColorButton.setEnabled(b);
        this.mLineColorLabel.setEnabled(b);
        this.mLineTypeLabel.setEnabled(b);
        this.mLineTypeComboBox.setEnabled(b);
        this.mLineWidthLabel.setEnabled(b);
        this.mLineWidthSpinner.setEnabled(b);
        this.mLineConnectCheckBox.setEnabled(b);

//        this.setLinePlacementComponentsEnabled(b);
    }

//    private void setLinePlacementComponentsEnabled(Boolean flag) {
//        boolean b = true;
//        Boolean lineVisible = this.getLineVisible();
//        Boolean symbolVisible = this.getSymbolVisible();
//        if (lineVisible != null && symbolVisible != null) {
//            if (flag != null) {
//                if (lineVisible.booleanValue() || symbolVisible.booleanValue()) {
//                    b = true;
//                } else {
//                    b = false;
//                }
//            }
//        }
//        this.mLinePlacementLabel.setEnabled(b);
//        this.mLinePlacementSeparator.setEnabled(b);
//        this.mLinePlacementShiftXLabel.setEnabled(b);
//        this.mLinePlacementShiftXTextField.setEnabled(b);
//        this.mLinePlacementShiftYLabel.setEnabled(b);
//        this.mLinePlacementShiftYTextField.setEnabled(b);
//    }

    private void setSymbolComponentsEnabled(Boolean flag) {
        boolean b = true;
        if (flag != null) {
            b = flag.booleanValue();
        }
        this.mSymbolColorButton.setEnabled(b);
        this.mSymbolColorLabel.setEnabled(b);
        this.mSymbolLineColorButton.setEnabled(b);
        this.mSymbolLineColorLabel.setEnabled(b);
        this.mSymbolLineWidthLabel.setEnabled(b);
        this.mSymbolLineWidthSpinner.setEnabled(b);
        this.mSymbolLineVisibleCheckBox.setEnabled(b);
        this.mSymbolSizeLabel.setEnabled(b);
        this.mSymbolSizeSpinner.setEnabled(b);
        this.mSymbolTypeComboBox.setEnabled(b);
        this.mSymbolTypeLabel.setEnabled(b);
        this.mSymbolBodyLabel.setEnabled(b);
        this.mSymbolBodySeparator.setEnabled(b);
        this.mSymbolBodyTransparencyLabel.setEnabled(b);
        this.mSymbolBodyTransparencySpinner.setEnabled(b);
        this.mSymbolLineLabel.setEnabled(b);
        this.mSymbolLineSeparator.setEnabled(b);

//        this.setLinePlacementComponentsEnabled(b);
    }

    private void setSymbolLineComponentsEnabled(Boolean flag) {
        boolean b = true;
        Boolean visible = this.getSymbolVisible();
        if (visible != null) {
            if (flag != null) {
                if (visible.booleanValue()) {
                    b = flag.booleanValue();
                } else {
                    b = false;
                }
            }
        }
        this.mSymbolLineColorButton.setEnabled(b);
        this.mSymbolLineColorLabel.setEnabled(b);
        this.mSymbolLineWidthLabel.setEnabled(b);
        this.mSymbolLineWidthSpinner.setEnabled(b);
    }

    private void setBarComponentsEnabled(Boolean flag) {
        boolean b = true;
        if (flag != null) {
            b = flag.booleanValue();
        }
        this.mBarComponentGroup.setEnabled(b);
    }

    private void setBarEdgeLineComponentsEnabled(Boolean flag) {
        boolean b = true;
        if (flag != null) {
        	Boolean visible = this.getBarVisible();
        	if (visible != null) {
                if (visible.booleanValue()) {
                    b = flag.booleanValue();
                } else {
                    b = false;
                }
        	}
        }
        this.mBarLineColorButton.setEnabled(b);
        this.mBarLineColorLabel.setEnabled(b);
        this.mBarLineWidthLabel.setEnabled(b);
        this.mBarLineWidthSpinner.setEnabled(b);
    }

    /*
    private String mTemporaryBarIntervalText = "";
    private void setBarIntervalEnabled(final boolean enabled) {
        if (enabled==false) {
            this.mTemporaryBarIntervalText = this.mBarIntervalTextField.getText();
            this.mBarIntervalLabel.setEnabled(enabled);
            this.mBarIntervalTextField.setEnabled(enabled);
            this.mBarIntervalTextField.setText("");
        }
    }
    */

    private void setErrorBarComponentsEnabled(Boolean flag) {
        boolean b = true;
        if (flag != null) {
            b = flag.booleanValue();
        }
        this.mErrorBarBothsidesRadioButton.setEnabled(b);
        this.mErrorBarColorButton.setEnabled(b);
        this.mErrorBarColorLabel.setEnabled(b);
        this.mErrorBarDownsideRadioButton.setEnabled(b);
        this.mErrorBarLineWidthLabel1.setEnabled(b);
        this.mErrorBarLineWidthLabel2.setEnabled(b);
        this.mErrorBarLineWidthSpinner.setEnabled(b);
        this.mErrorBarSymbolSizeLabel.setEnabled(b);
        this.mErrorBarSymbolSizeSpinner.setEnabled(b);
        this.mErrorBarTypeComboBox.setEnabled(b);
        this.mErrorBarTypeLabel.setEnabled(b);
        this.mErrorBarUpsideRadioButton.setEnabled(b);
        this.mErrorBarSymbolLabel.setEnabled(b);
        this.mErrorBarSymbolSeparator.setEnabled(b);
        this.mErrorBarStyleLabel.setEnabled(b);
        this.mErrorBarStyleSeparator.setEnabled(b);
        this.mErrorBarPositionLabel.setEnabled(b);
        this.mErrorBarPositionSeparator.setEnabled(b);
        this.mErrorBarPositionLineRadioButton.setEnabled(b);
        this.mErrorBarPositionBarRadioButton.setEnabled(b);
    }

    private void setTickLabelComponentsEnabled(Boolean flag) {
        boolean b = true;
        if (flag != null) {
            b = flag.booleanValue();
        }
        /*
        this.mTickLabelTextLabel.setEnabled(b);
        this.mTickLabelAngleLabel.setEnabled(b);
        this.mTickLabelAngleSpinner.setEnabled(b);
        this.mTickLabelFontNameLabel.setEnabled(b);
        this.mTickLabelFontNameComboBox.setEnabled(b);
        this.mTickLabelFontLabel.setEnabled(b);
        this.mTickLabelFontSizeLabel.setEnabled(b);
        this.mTickLabelFontSizeSpinner.setEnabled(b);
        this.mTickLabelFontStyleLabel.setEnabled(b);
        this.mTickLabelFontStyleComboBox.setEnabled(b);
        this.mTickLabelColorButton.setEnabled(b);
        this.mTickLabelColorLabel.setEnabled(b);
        this.mTickLabelFormatLabel.setEnabled(b);
        this.mTickLabelDecimalPlacesLabel.setEnabled(b);
        this.mTickLabelDecimalPlacesSpinner.setEnabled(b);
        this.mTickLabelExponentLabel.setEnabled(b);
        this.mTickLabelExponentBaseLabel.setEnabled(b);
        this.mTickLabelExponentSpinner.setEnabled(b);
        this.mTickLabelDateFormatLabel.setEnabled(b);
        this.mTickLabelDateFormatComboBox.setEnabled(b);
        */
        this.mTickLabelComponentGroup.setEnabled(b);
    }

    /**
     *
     * @return
     */
    public boolean setDialogProperty() {
        List<SGIPropertyDialogObserver>  list = this.mPropertyDialogObserverList;
        final int len = list.size();

        // column selection button
        final boolean single = (len == 1);
        this.mDataColumnSelectionButton.setEnabled(single);

        // select axis panel
        selectAxisPanel(this, this.mAxisPanel, list);

//        // set properties to the date components
//        setDateComponentProperties(this, this.mAxisPanel, list);

        // data name
        String name = this.getDataNameFromObservers();
        this.setDataName(name);

        // visible in legend
        Boolean legendVisible = this.getLegendVisibleFromObservers();
        this.setLegendVisible(legendVisible);

        // set properties of lines, symbols, bars, error-bars and tick-labels
        if (this.setLineTabProperties(list) == false) {
            return false;
        }
        if (this.setSymbolTabProperties(list) == false) {
            return false;
        }
        if (this.setBarTabProperties(list) == false) {
            return false;
        }
        if (this.setErrorBarTabProperties(list) == false) {
            return false;
        }
        if (this.setTickLabelTabProperties(list) == false) {
            return false;
        }

        ELEMENT_TYPE type = ELEMENT_TYPE.Void;
        for (int ii = 0; ii < len; ii++) {
            SGISXYDataDialogObserver l = (SGISXYDataDialogObserver) list
                    .get(ii);
            type = l.getSelectedGroupType();
            if (type != ELEMENT_TYPE.Void) {
                break;
            }
        }
        Component c = null;
        if (type == ELEMENT_TYPE.Line
        		|| type == ELEMENT_TYPE.Symbol) {
        	c = this.mLinePanel;
        } else if (type == ELEMENT_TYPE.Bar) {
        	c = this.mBarPanel;
        } else if (type == ELEMENT_TYPE.ErrorBar) {
        	c = this.mErrorBarPanel;
        } else if (type == ELEMENT_TYPE.TickLabel) {
        	c = this.mTickLabelPanel;
        } else {
        	return false;
        }
    	this.mTabbedPane.setSelectedComponent(c);

    	this.initBarVerticalCheckBox();

        return true;
    }

    // sets the properties of line tab
    private boolean setLineTabProperties(final List<SGIPropertyDialogObserver> list) {
        final int num = list.size();
        
        final boolean[] visibleArray = new boolean[num];
        final boolean[] connectArray = new boolean[num];
        final double[] shiftXArray = new double[num];
        final double[] shiftYArray = new double[num];

        List<SGLineStyle> lineStyleList = new ArrayList<SGLineStyle>();
        for (int ii = 0; ii < num; ii++) {
        	SGISXYDataDialogObserver l = (SGISXYDataDialogObserver) list.get(ii);
        	visibleArray[ii] = l.isLineVisible();
        	connectArray[ii] = l.isLineConnectingAll();
        	shiftXArray[ii] = l.getShiftX();
        	shiftYArray[ii] = l.getShiftY();
        	Map<Integer, SGLineStyle> lineStyleMap = l.getLineStyleMap();
        	lineStyleList.addAll(lineStyleMap.values());
        }
        
        //
        // Checks the equality.
        //
        
        Boolean visible = SGUtility.checkEquality(visibleArray);
        Boolean connect = SGUtility.checkEquality(connectArray);
        Double shiftX = SGUtility.checkEquality(shiftXArray);
        Double shiftY = SGUtility.checkEquality(shiftYArray);

        //
        // Sets parameters to the components.
        //
        
        this.setLineVisible(visible);
        this.setLineConnection(connect);

        this.setShiftX(shiftX);
        this.setShiftY(shiftY);
        
        this.updateLineStyleComponents(lineStyleList);

        final boolean bSingleData = (num == 1);
        if (bSingleData) {
        	this.mLineStyleList = new ArrayList<SGLineStyle>(lineStyleList);
        	SGISXYDataDialogObserver l = (SGISXYDataDialogObserver) list.get(0);
        	this.mLineColorMapName = l.getLineColorMapName();
        	this.mLineColorMapProperties = l.getLineColorMapProperties();
        	this.mChildNameList = l.getChildNameList();
            this.mLineColorAutoAssigned = l.isLineColorAutoAssigned();
        }
        
        /*
         * // enable or disable the stroke dialog button // When solid lines are
         * selected, the button is set disabled.
         * this.mLineEditButton.setEnabled(!solidLineFlag); if (!solidLineFlag) { //
         * set properties of the stroke dialog if
         * (this.mStrokeDialog.setDialogProperty() == false) { return false; } }
         */

        return true;
    }

    private boolean setSymbolTabProperties(final List<SGIPropertyDialogObserver> list) {
        SGISXYDataDialogObserver g0 = (SGISXYDataDialogObserver) list.get(0);
        final boolean visible0 = g0.isSymbolVisible();
        final float size0 = g0.getSymbolSize(cm);
        final float lineWidth0 = g0.getSymbolLineWidth(pt);
        final int type0 = g0.getSymbolType();
        final SGIPaint innerPaint0 = g0.getSymbolInnerPaint();
        final Color lineColor0 = g0.getSymbolLineColor();
        final boolean lineVisible0 = g0.isSymbolLineVisible();

        Boolean visible = Boolean.valueOf(visible0);
        Float size = Float.valueOf(size0);
        Float lineWidth = Float.valueOf(lineWidth0);
        Integer type = Integer.valueOf(type0);
        SGIPaint innerPaint = innerPaint0;
        Color lineColor = lineColor0;
        Boolean lineVisible = Boolean.valueOf(lineVisible0);

        if (list.size() > 1) {
            for (int ii = 1; ii < list.size(); ii++) {
                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
                        .get(ii);
                final boolean visible1 = g1.isSymbolVisible();
                if (visible0 != visible1) {
                    visible = null;
                    break;
                }
            }
            for (int ii = 1; ii < list.size(); ii++) {
                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
                        .get(ii);
                final float size1 = g1.getSymbolSize(cm);
                if (size0 != size1) {
                    size = null;
                    break;
                }
            }
            for (int ii = 1; ii < list.size(); ii++) {
                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
                        .get(ii);
                final float lineWidth1 = g1.getSymbolLineWidth(pt);
                if (lineWidth0 != lineWidth1) {
                    lineWidth = null;
                    break;
                }
            }
            for (int ii = 1; ii < list.size(); ii++) {
                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
                        .get(ii);
                final int type1 = g1.getSymbolType();
                if (type0 != type1) {
                    type = null;
                    break;
                }
            }
//            for (int ii = 1; ii < list.size(); ii++) {
//                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
//                        .get(ii);
//                final Color innerColor1 = g1.getSymbolInnerColor();
//                if (innerColor0.equals(innerColor1) == false) {
//                    innerColor = null;
//                    break;
//                }
//            }
            for (int ii = 1; ii < list.size(); ii++) {
                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
                        .get(ii);
                final SGIPaint innerPaint1 = g1.getSymbolInnerPaint();
                if (innerPaint0.equals(innerPaint1) == false) {
                    innerPaint = null;
                    break;
                }
            }
            for (int ii = 1; ii < list.size(); ii++) {
                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
                        .get(ii);
                final Color lineColor1 = g1.getSymbolLineColor();
                if (lineColor0.equals(lineColor1) == false) {
                    lineColor = null;
                    break;
                }
            }
            for (int ii = 1; ii < list.size(); ii++) {
                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
                        .get(ii);
                final boolean lineVisible1 = g1.isSymbolLineVisible();
                if (lineVisible0 != lineVisible1) {
                    lineVisible = null;
                    break;
                }
            }

        }

        this.setSymbolVisible(visible);
        this.setSymbolSize(size);
        this.setSymbolLineWidth(lineWidth);
        this.setSymbolType(type);
        this.setSymbolInnerPaint(innerPaint);
        this.setSymbolLineColor(lineColor);
        this.setSymbolLineVisible(lineVisible);

        return true;
    }

    private boolean setBarTabProperties(final List<SGIPropertyDialogObserver> list) {
    	final int num = list.size();

        final boolean[] visibleArray = new boolean[num];
        final boolean[] verticalArray = new boolean[num];
        final double[] baselineArray = new double[num];
        final double[] widthArray = new double[num];
        final boolean[] edgeLineVisibleArray = new boolean[num];
        final float[] edgeLineWidthArray = new float[num];
        final Color[] edgeLineColorArray = new Color[num];
        final SGIPaint[] innerPaintArray = new SGIPaint[num];
        final double[] offsetXArray = new double[num];
        final double[] offsetYArray = new double[num];
        final double[] intervalArray = new double[num];
        
        for (int ii = 0; ii < num; ii++) {
        	SGISXYDataDialogObserver l = (SGISXYDataDialogObserver) list.get(ii);
        	visibleArray[ii] = l.isBarVisible();
        	verticalArray[ii] = l.isBarVertical();
        	baselineArray[ii] = l.getBarBaselineValue();
        	widthArray[ii] = l.getBarWidthValue();
        	edgeLineVisibleArray[ii] = l.isBarEdgeLineVisible();
        	edgeLineWidthArray[ii] = l.getBarEdgeLineWidth(LINE_WIDTH_UNIT);
        	edgeLineColorArray[ii] = l.getBarEdgeLineColor();
        	innerPaintArray[ii] = l.getBarInnerPaint();
        	offsetXArray[ii] = l.getBarOffsetX();
        	offsetYArray[ii] = l.getBarOffsetY();
        	intervalArray[ii] = l.getBarInterval();
        }

        Boolean visible = SGUtility.checkEquality(visibleArray);
        Boolean vertical = SGUtility.checkEquality(verticalArray);
        Double baseline = SGUtility.checkEquality(baselineArray);
        Double width = SGUtility.checkEquality(widthArray);
        Boolean edgeLineVisible = SGUtility.checkEquality(edgeLineVisibleArray);
        Float edgeLineWidth = SGUtility.checkEquality(edgeLineWidthArray);
        Color edgeLineColor = SGUtility.checkEquality(edgeLineColorArray);
        SGIPaint innerPaint = (SGIPaint) SGUtility.checkEquality(innerPaintArray);
        Double offsetX = SGUtility.checkEquality(offsetXArray);
        Double offsetY = SGUtility.checkEquality(offsetYArray);
        Double interval = SGUtility.checkEquality(intervalArray);

        this.setBarVisible(visible);
        this.setBarVertical(vertical);
        this.setBarBaselineValue(baseline);
        this.setBarWidth(width);
        this.setBarEdgeLineVisible(edgeLineVisible);
        this.setBarEdgeLineWidth(edgeLineWidth);
        this.setBarEdgeLineColor(edgeLineColor);
        this.setBarInnerPaint(innerPaint);
        this.setBarOffsetX(offsetX);
        this.setBarOffsetY(offsetY);
        this.setBarInterval(interval);

        // sets up bar component groups
        this.setUpBarComponentGroup();
        
        this.mBarIntervalComponentGroup.setEnabled(
        		this.getBarIntervalEnabledInMultipleSeriesBars());
        
        // set properties to the date components
        setDateComponentProperties(this, this.mAxisPanel, 
        		this.mPropertyDialogObserverList);

        return true;
    }
    
    private void setUpBarComponentGroup() {
    	this.mDateWidthComponentGroup.clear();
    	this.mDateHeightComponentGroup.clear();
    	this.mDateXComponentGroup.clear();
    	this.mDateYComponentGroup.clear();
    	this.mBarBodyTransparentComponentGroup.clear();
    	this.mBarComponentGroup.clear();
    	this.mBarIntervalComponentGroup.clear();
    	
        SGComponentGroupElement[] dateWidthComponents = {
        		new SGComponentGroupElement(this.mBarWidthDateButton),
                new SGComponentGroupElement(this.mBarIntervalDateButton)
        };
        this.mDateWidthComponentGroup.addElement(dateWidthComponents);
        
        SGComponentGroupElement[] dateHeightComponents = {
        		new SGComponentGroupElement(this.mBarBaselineDateButton)
        };
        this.mDateHeightComponentGroup.addElement(dateHeightComponents);
        
        List<SGComponentGroupElement> dateXComponentList = new ArrayList<SGComponentGroupElement>();
        SGComponentGroupElement[] dateXComponents = {
                new SGComponentGroupElement(this.mShiftXDateButton),
                new SGComponentGroupElement(this.mBarOffsetXDateButton) };
        for (SGComponentGroupElement comp : dateXComponents) {
        	dateXComponentList.add(comp);
        }
        this.mDateXComponentGroup.addElement(dateXComponentList);
        
        List<SGComponentGroupElement> dateYComponentList = new ArrayList<SGComponentGroupElement>();
        SGComponentGroupElement[] dateYComponents = {
                new SGComponentGroupElement(this.mShiftYDateButton),
                new SGComponentGroupElement(this.mBarOffsetYDateButton) };
        for (SGComponentGroupElement comp : dateYComponents) {
        	dateYComponentList.add(comp);
        }
        this.mDateYComponentGroup.addElement(dateYComponentList);

        SGComponentGroupElement[] barBodyTransparentComponents = {
                new SGComponentGroupElement(this.mBarColorLabel),
                new SGComponentGroupElement(this.mBarInnerColorButton),
                new SGComponentGroupElement(this.mBarBodyColorFillRadioButton),
                new SGComponentGroupElement(this.mBarBodyColorPatternPaintButton),
                new SGComponentGroupElement(this.mBarBodyColorPatternRadioButton),
                new SGComponentGroupElement(this.mBarBodyColorGradationColorButton),
                new SGComponentGroupElement(this.mBarBodyColorGradationRadioButton)
        };
        this.mBarBodyTransparentComponentGroup.addElement(barBodyTransparentComponents);

        SGComponentGroupElement[] barIntervalComponents = {
        		new SGComponentGroupElement(this.mBarIntervalLabel),
        		new SGComponentGroupElement(this.mBarIntervalTextField),
        		dateWidthComponents[1]
        };
        this.mBarIntervalComponentGroup.addElement(barIntervalComponents);
        
        SGComponentGroupElement[] barComponents = {
        		dateWidthComponents[0],
        		dateWidthComponents[1],
        		dateHeightComponents[0],
        		dateXComponents[1],
        		dateYComponents[1],
        		barBodyTransparentComponents[0],
        		barBodyTransparentComponents[1],
        		barBodyTransparentComponents[2],
        		barBodyTransparentComponents[3],
        		barBodyTransparentComponents[4],
        		barBodyTransparentComponents[5],
        		barBodyTransparentComponents[6],
                barIntervalComponents[0],
                barIntervalComponents[1],
                new SGComponentGroupElement(this.mBarBaseLineValueLabel),
                new SGComponentGroupElement(this.mBarBaselineTextField),
                new SGComponentGroupElement(this.mBarBodyTransparencyLabel),
                new SGComponentGroupElement(this.mBarBodyTransparencySpinner),
                new SGComponentGroupElement(this.mBarLineColorButton),
                new SGComponentGroupElement(this.mBarLineColorLabel),
                new SGComponentGroupElement(this.mBarLineWidthLabel),
                new SGComponentGroupElement(this.mBarLineWidthSpinner),
                new SGComponentGroupElement(this.mBarLineVisibleCheckBox),
                new SGComponentGroupElement(this.mBarLineWidthSpinner),
                new SGComponentGroupElement(this.mBarWidthLabel),
                new SGComponentGroupElement(this.mBarWidthTextField),
                new SGComponentGroupElement(this.mBarVerticalCheckBox),
                new SGComponentGroupElement(this.mBarBodyLabel),
                new SGComponentGroupElement(this.mBarLineLabel),
                new SGComponentGroupElement(this.mBarOffsetLabel),
                new SGComponentGroupElement(this.mBarOffsetXLabel),
                new SGComponentGroupElement(this.mBarOffsetXTextField),
                new SGComponentGroupElement(this.mBarOffsetYLabel),
                new SGComponentGroupElement(this.mBarOffsetYTextField)
        };
        this.mBarComponentGroup.addElement(barComponents);
    }

    private boolean setErrorBarTabProperties(final List<SGIPropertyDialogObserver> list) {
        boolean available = true;
        for (int ii = 0; ii < list.size(); ii++) {
            SGISXYDataDialogObserver g = (SGISXYDataDialogObserver) list
                    .get(ii);
            if (g.isErrorBarAvailable() == false) {
                available = false;
                break;
            }
        }
        this.setTabEnabled(this.mErrorBarPanel, available);

        SGISXYDataDialogObserver g0 = (SGISXYDataDialogObserver) list.get(0);
        final boolean visible0 = g0.isErrorBarVisible();
        final float lineWidth0 = g0.getErrorBarLineWidth(pt);
        final float headSize0 = g0.getErrorBarHeadSize(cm);
        final int style0 = g0.getErrorBarStyle();
        final int type0 = g0.getErrorBarHeadType();
        final Color color0 = g0.getErrorBarColor();
        final boolean isLinePosition0 = g0.isErrorBarOnLinePosition();

        Boolean visible = Boolean.valueOf(visible0);
        Float lineWidth = Float.valueOf(lineWidth0);
        Float headSize = Float.valueOf(headSize0);
        Integer style = Integer.valueOf(style0);
        Integer type = Integer.valueOf(type0);
        Color color = color0;
        Boolean isLinePosition = Boolean.valueOf(isLinePosition0);

        if (list.size() > 1) {
            for (int ii = 1; ii < list.size(); ii++) {
                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
                        .get(ii);
                final boolean visible1 = g1.isErrorBarVisible();
                if (visible0 != visible1) {
                    visible = null;
                    break;
                }
            }
            for (int ii = 1; ii < list.size(); ii++) {
                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
                        .get(ii);
                final float lineWidth1 = g1.getErrorBarLineWidth(pt);
                if (lineWidth0 != lineWidth1) {
                    lineWidth = null;
                    break;
                }
            }
            for (int ii = 1; ii < list.size(); ii++) {
                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
                        .get(ii);
                final float headSize1 = g1.getErrorBarHeadSize(cm);
                if (headSize0 != headSize1) {
                    headSize = null;
                    break;
                }
            }
            for (int ii = 1; ii < list.size(); ii++) {
                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
                        .get(ii);
                final int style1 = g1.getErrorBarStyle();
                if (style0 != style1) {
                    style = null;
                    break;
                }
            }
            for (int ii = 1; ii < list.size(); ii++) {
                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
                        .get(ii);
                final int type1 = g1.getErrorBarHeadType();
                if (type0 != type1) {
                    type = null;
                    break;
                }
            }
            for (int ii = 1; ii < list.size(); ii++) {
                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
                        .get(ii);
                final Color color1 = g1.getErrorBarColor();
                if (color0.equals(color1) == false) {
                    color = null;
                    break;
                }
            }
            for (int ii = 1; ii < list.size(); ii++) {
                SGISXYDataDialogObserver g1 = (SGISXYDataDialogObserver) list
                        .get(ii);
                final boolean isLinePosition1 = g1.isErrorBarOnLinePosition();
                if (isLinePosition0 != isLinePosition1) {
                	isLinePosition = null;
                    break;
                }
            }
        }

        this.setErrorBarVisible(visible);
        if (headSize != null) {
            headSize = Float.valueOf(headSize.floatValue());
        }
        String typeName = null;
        if (type != null) {
            typeName = SGDrawingElementErrorBar.getHeadTypeName(type.intValue());
        }
        this.setErrorBarLineWidth(lineWidth);
        this.setErrorBarSymbolSize(headSize);
        this.setErrorBarStyle(style);
        this.setErrorBarType(typeName);
        this.setErrorBarColor(color);
        this.setErrorBarOnLinePosition(isLinePosition);

        return true;
    }

    private boolean setTickLabelTabProperties(final List<SGIPropertyDialogObserver> list) {
    	final int num = list.size();
    	
        boolean available = true;
        for (int ii = 0; ii < num; ii++) {
            SGISXYDataDialogObserver g = (SGISXYDataDialogObserver) list
                    .get(ii);
            if (g.isTickLabelAvailable() == false) {
                available = false;
                break;
            }
        }
        this.setTabEnabled(this.mTickLabelPanel, available);

        final boolean[] visibleArray = new boolean[num];
        final float[] fontSizeArray = new float[num];
        final int[] fontStyleArray = new int[num];
        final String[] fontNameArray = new String[num];
        final float[] angleArray = new float[num];
        final Color[] colorArray = new Color[num];
        final int[] decimalPlacesArray = new int[num];
        final int[] exponentArray = new int[num];
        final String[] dateFormatArray = new String[num];
        final boolean[] dateTickLabelArray = new boolean[num];
        for (int ii = 0; ii < num; ii++) {
        	SGISXYDataDialogObserver l = (SGISXYDataDialogObserver) list.get(ii);
        	visibleArray[ii] = l.isTickLabelVisible();
        	fontNameArray[ii] = l.getTickLabelFontName();
        	fontStyleArray[ii] = l.getTickLabelFontStyle();
        	fontSizeArray[ii] = l.getTickLabelFontSize(FONT_SIZE_UNIT);
        	angleArray[ii] = l.getTickLabelAngle();
        	colorArray[ii] = l.getTickLabelColor();
        	decimalPlacesArray[ii] = l.getTickLabelDecimalPlaces();
        	exponentArray[ii] = l.getTickLabelExponent();
        	dateFormatArray[ii] = l.getTickLabelDateFormat();
        	dateTickLabelArray[ii] = l.hasDateTickLabels();
        }

        Boolean visible = SGUtility.checkEquality(visibleArray);
        String fontName = SGUtility.checkEquality(fontNameArray);
        Integer fontStyle = SGUtility.checkEquality(fontStyleArray);
        Float fontSize = SGUtility.checkEquality(fontSizeArray);
        Float angle = SGUtility.checkEquality(angleArray);
        Color color = SGUtility.checkEquality(colorArray);
        Integer decimalPlaces = SGUtility.checkEquality(decimalPlacesArray);
        Integer exponent = SGUtility.checkEquality(exponentArray);
        String dateFormat = SGUtility.checkEquality(dateFormatArray);
        Boolean dateTickLabel = SGUtility.checkEquality(dateTickLabelArray);

        this.setTickLabelVisible(visible);
        this.setTickLabelFontName(fontName);
        this.setTickLabelFontStyle(fontStyle);
        this.setTickLabelFontSize(fontSize);
        this.setTickLabelAngle(angle);
        this.setTickLabelColor(color);
        this.setTickLabelDecimalPlaces(decimalPlaces);
        this.setTickLabelExponent(exponent);
    	this.setTickLabelDateFormat(dateFormat);
        
        final boolean dateTickLabelEnabled = (dateTickLabel != null) 
        		? dateTickLabel.booleanValue() : false;
        this.mDateTickLabelComponentGroup.setEnabled(dateTickLabelEnabled);

        return true;
    }

    private boolean setLineProperties(SGISXYDataDialogObserver l) {
        final Boolean visible = this.getLineVisible();
        if (visible != null) {
            l.setLineVisible(visible.booleanValue());
        }
        final Boolean connect = this.getLineConnection();
        if (connect != null) {
            l.setLineConnectingAll(connect.booleanValue());
        }
        
        List<SGLineStyle> lineStyleList = new ArrayList<SGLineStyle>();
        if (this.mLineStyleList != null) {
        	lineStyleList.addAll(mLineStyleList);
        } else {
        	lineStyleList.addAll(l.getLineStyleMap().values());
        }

        final Number lineWidth = this.getLineWidth();
        if (lineWidth != null) {
            for (SGLineStyle style : lineStyleList) {
            	style.setLineWidth(lineWidth.floatValue());
            }
        }
        final Integer type = this.getLineType();
        if (type != null) {
            for (SGLineStyle style : lineStyleList) {
            	style.setLineType(type.intValue());
            }
        }
        final Color color = this.getLineColor();
        if (color != null) {
            for (SGLineStyle style : lineStyleList) {
            	style.setColor(color);
            }
        }

        l.setLineStyle(lineStyleList);
        l.setLineColorMapName(this.mLineColorMapName);
        if (this.mLineColorMapProperties != null) {
            l.setLineColorMapProperties(this.mLineColorMapProperties);
        }

        l.setLineColorAutoAssigned(this.mLineColorAutoAssigned);
        
        return true;
    }
    
    private boolean setSymbolProperties(SGISXYDataDialogObserver l) {
        final Boolean visible = this.getSymbolVisible();
        if (visible != null) {
            l.setSymbolVisible(visible.booleanValue());
        }
        final Number size = this.getSymbolSize();
        if (size != null) {
            l.setSymbolSize(size.floatValue(), cm);
        }
        final Integer type = this.getSymbolType();
        if (type != null) {
            l.setSymbolType(type.intValue());
        }
        final SGIPaint innerPaint = this.getSymbolInnerPaint();
        if (innerPaint != null) {
            l.setSymbolInnerPaint(innerPaint);
        }
        final Number lineWidth = this.getSymbolLineWidth();
        if (lineWidth != null) {
            l.setSymbolLineWidth(lineWidth.floatValue(), pt);
        }
        final Color lineColor = this.getSymbolLineColor();
        if (lineColor != null) {
            l.setSymbolLineColor(lineColor);
        }
        final Boolean lineVisible = this.getSymbolLineVisible();
        if (lineVisible != null) {
            l.setSymbolLineVisible(lineVisible.booleanValue());
        }
        return true;
    }

    private boolean setBarProperties(SGISXYDataDialogObserver l) {
        final Boolean visible = this.getBarVisible();
        if (visible != null) {
            l.setBarVisible(visible.booleanValue());
        }
        final SGIPaint innerPaint = this.getBarBodyPaint();
        if (innerPaint != null) {
            l.setBarInnerPaint(innerPaint);
        }
        Number barWidth = this.getBarWidth();
        if (barWidth != null) {
            l.setBarWidthValue(barWidth.doubleValue());
        }
        Number edgeLineWidth = this.getBarEdgeLineWidth();
        if (edgeLineWidth != null) {
            l.setBarEdgeLineWidth(edgeLineWidth.floatValue(), pt);
        }
        Color lineColor = this.getBarEdgeLineColor();
        if (lineColor != null) {
            l.setBarEdgeLineColor(lineColor);
        }
        Boolean lineVisible = this.getBarEdgeLineVisible();
        if (lineVisible != null) {
            l.setBarEdgeLineVisible(lineVisible.booleanValue());
        }
        Number value = this.getBarBaselineValue();
        if (value != null) {
            l.setBarBaselineValue(value.doubleValue());
        }
        Boolean vertical = this.isBarVertical();
        if (vertical != null) {
        	l.setBarVertical(vertical.booleanValue());
        }
        Number offsetX = this.getBarOffsetX();
        if (offsetX != null) {
            l.setBarOffsetX(offsetX.doubleValue());
        }
        Number offsetY = this.getBarOffsetY();
        if (offsetY != null) {
            l.setBarOffsetY(offsetY.doubleValue());
        }
        Number interval = this.getBarInterval();
        if (interval != null) {
            l.setBarInterval(interval.doubleValue());
        }
        return true;
    }

    private boolean setErrorBarProperties(SGISXYDataDialogObserver l) {
        final Boolean visible = this.getErrorBarVisible();
        if (visible != null) {
            l.setErrorBarVisible(visible.booleanValue());
        }
        final Color cl = this.getErrorBarColor();
        if (cl != null) {
            l.setErrorBarColor(cl);
        }
        final Number lineWidth = this.getErrorBarLineWidth();
        if (lineWidth != null) {
            l.setErrorBarLineWidth(lineWidth.floatValue(), LINE_WIDTH_UNIT);
        }
        final Number headSize = this.getErrorBarSymbolSize();
        if (headSize != null) {
            l.setErrorBarHeadSize(headSize.floatValue(), cm);
        }
        final Integer style = this.getErrorBarStyle();
        if (style != null) {
            l.setErrorBarStyle(style.intValue());
        }
        final String headSymbolTypeName = this.getErrorBarTypeName();
        final Integer type = SGDrawingElementErrorBar
                .getHeadTypeFromName(headSymbolTypeName);
        if (type != null) {
            l.setErrorBarHeadType(type.intValue());
        }
        final Boolean isLinePositionUsed = this.getErrorBarOnLinePosition();
        if (isLinePositionUsed != null) {
            l.setErrorBarOnLinePosition(isLinePositionUsed.booleanValue());
        }
        return true;
    }

    private boolean setTickLabelProperties(SGISXYDataDialogObserver l) {
        Boolean visible = this.getTickLabelVisible();
        if (visible != null) {
            l.setTickLabelVisible(visible.booleanValue());
        }
        final Color cl = this.getTickLabelColor();
        if (cl != null) {
            l.setTickLabelColor(cl);
        }
        final String fontName = this.getTickLabelFontName();
        if (fontName != null) {
            l.setTickLabelFontName(fontName);
        }
        final Number fontSize = this.getTickLabelFontSize();
        if (fontSize != null) {
            l.setTickLabelFontSize(fontSize.floatValue(), pt);
        }
        final Integer fontStyle = this.getTickLabelFontStyle();
        if (fontStyle != null) {
            l.setTickLabelFontStyle(fontStyle.intValue());
        }
        final Number angle = this.getTickLabelAngle();
        if (angle != null) {
            l.setTickLabelAngle(angle.floatValue());
        }
        final Number decimalPlaces = this.getTickLabelDecimalPlaces();
        if (decimalPlaces != null) {
            l.setTickLabelDecimalPlaces(decimalPlaces.intValue());
        }
        final Number exponent = this.getTickLabelExponent();
        if (exponent != null) {
            l.setTickLabelExponent(exponent.intValue());
        }
        final String dateFormat = this.getTickLabelDateFormat();
        if (dateFormat != null) {
        	l.setTickLabelDateFormat(dateFormat);
        }
        return true;
    }

    /**
     * Add a property dialog observer.
     * @param l  property dialog observer.
     * @return  true:succeeded, false:failed
     */
    /*
     public boolean addPropertyDialogObserver(final SGIPropertyDialogObserver l) {
     if (super.addPropertyDialogObserver(l) == false) {
     return false;
     }
     return this.mStrokeDialog.addPropertyDialogObserver(l);
     }
     */
    /**
     * Remove a property dialog observer.
     * @param l  property dialog observer.
     * @return  true:succeeded, false:failed
     */
    /*
     public boolean removePropertyDialogObserver(final SGIPropertyDialogObserver l) {
     if (super.removePropertyDialogObserver(l) == false) {
     return false;
     }
     return this.mStrokeDialog.removePropertyDialogObserver(l);
     }
     */
    /**
     * Remove all property dialog observers.
     * @return  true:succeeded, false:failed
     */
    /*
     public boolean removeAllPropertyDialogObserver() {
     if (super.removeAllPropertyDialogObserver() == false) {
     return false;
     }
     return this.mStrokeDialog.removeAllPropertyDialogObserver();
     }
     */

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
     * Overrode to clear an attribute.
     *
     * @return true if succeeded
     */
    protected boolean onOK() {
        if (super.onOK() == false) {
            return false;
        }
        this.mPickUpDimensionInfo = null;
        this.mLineStyleList = null;
        this.mLineColorMapName = null;
        this.mChildNameList = null;
        return true;
    }

    /**
     * Overrode to clear an attribute.
     *
     * @return true if succeeded
     */
    protected boolean onCanceled() {
        if (super.onCanceled() == false) {
            return false;
        }
        this.mPickUpDimensionInfo = null;
        this.mLineStyleList = null;
        this.mLineColorMapName = null;
        this.mChildNameList = null;
        return true;
    }

    /**
     * Shows a dialog to set line style.
     * 
     */
    protected void showLineStyleDialog() {
		this.mLineStyleDialog = new SGLineStyleDialog(this, true);
		this.mLineStyleDialog.addActionListener(this);

        SGISXYDataDialogObserver obs = (SGISXYDataDialogObserver) this.mPropertyDialogObserverList.get(0);
        this.mLineStyleDialog.setSelectedColorMapName(this.mLineColorMapName);
        this.mLineStyleDialog.setColorMapProperties(this.mLineColorMapProperties);

		List<SGLineStyle> lineStyleList = null;
		if (this.mLineStyleList != null) {
			lineStyleList = new ArrayList<SGLineStyle>(this.mLineStyleList);
		} else {
	        Map<Integer, SGLineStyle> lineStyleMap = obs.getLineStyleMap();
	        lineStyleList = new ArrayList<SGLineStyle>(lineStyleMap.values());
		}
		
		List<String> nameList = null;
		if (this.mChildNameList != null) {
			nameList = new ArrayList<String>(this.mChildNameList);
		} else {
			nameList = obs.getChildNameList();
		}
		
		// adds line styles
		for (int ii = 0; ii < lineStyleList.size(); ii++) {
			String name = nameList.get(ii);
			SGLineStyle lineStyle = lineStyleList.get(ii);
			this.mLineStyleDialog.addLineStyle(name, lineStyle);
		}
		
		// sets auto-assigned flag
		this.mLineStyleDialog.setLineColorAutoAssigned(this.mLineColorAutoAssigned);

        // shows the dialog
		this.mLineStyleDialog.pack();
        this.mLineStyleDialog.setLocation(
                this.getX() + 20, this.getY() + 20);
        this.mLineStyleDialog.setVisible(true);
        this.mLineStyleDialog.removeActionListener(this);
        
        // updates the attributes
        this.mLineColorMapName = this.mLineStyleDialog.getSelectedColorMapName();
        this.mLineColorMapProperties = this.mLineStyleDialog.getColorMapProperties();
    }
    
    @Override
    protected void buttonColorAssigned(SGColorSelectionButton b) {
    	if (this.mLineColorButton.equals(b)) {
    		if (this.mPropertyDialogObserverList.size() == 1) {
        		Set<Color> colorSet = new HashSet<Color>();
        		for (SGLineStyle lineStyle : this.mLineStyleList) {
        			colorSet.add(lineStyle.getColor());
        		}
        		if (colorSet.size() > 1) {
        			this.mLineColorAutoAssigned = false;
        		} else if (colorSet.size() == 1) {
        			List<Color> colorList = new ArrayList<Color>(colorSet);
        			Color cl = colorList.get(0);
        			if (!SGUtility.equals(cl, b.getColor())) {
        				this.mLineColorAutoAssigned = false;
        			}
        		}
    		}
    	}
    }
    
    @Override
    protected Map<String, Object> createInfoMap(SGIDataPropertyDialogObserver obs) {
    	Map<String, Object> infoMap = super.createInfoMap(obs);
    	if (this.mPickUpDimensionInfo != null) {
    		infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, this.mPickUpDimensionInfo.getIndices());
    	}
    	return infoMap;
    }

	@Override
	public void onXAxisDateSelected(boolean selected) {
		this.mDateXComponentGroup.setEnabled(selected);
		this.setBarDateComponentsEnabled(selected, 
				this.mDateYComponentGroup.isEnabled());
	}
	
	@Override
	public void onYAxisDateSelected(boolean selected) {
		this.mDateYComponentGroup.setEnabled(selected);
		this.setBarDateComponentsEnabled(
				this.mDateXComponentGroup.isEnabled(), selected);
	}

	private void setBarDateComponentsEnabled(final boolean xAxisDate,
			final boolean yAxisDate) {
		Boolean barVertical = this.isBarVertical();
		final boolean widthEnabled;
		final boolean heightEnabled;
		if (barVertical != null) {
			widthEnabled = (xAxisDate && barVertical) || (yAxisDate && !barVertical);
			heightEnabled = (yAxisDate && barVertical) || (xAxisDate && !barVertical);
		} else {
			widthEnabled = false;
			heightEnabled = false;
		}
		this.mDateWidthComponentGroup.setEnabled(widthEnabled);
		this.mDateHeightComponentGroup.setEnabled(heightEnabled);
	}

	@Override
	public void onAxisSelectionStateChanged(SGAxisSelectionPanel axisPanel) {
		this.setUpBarComponentGroup();
		
        // set properties to the date components
        setDateComponentProperties(this, this.mAxisPanel, 
        		this.mPropertyDialogObserverList);
	}
}

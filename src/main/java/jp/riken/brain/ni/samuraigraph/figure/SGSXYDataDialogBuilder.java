package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import jp.riken.brain.ni.samuraigraph.base.*;
import jp.riken.brain.ni.samuraigraph.base.SGConstants;
import jp.riken.brain.ni.samuraigraph.data.*;
import jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants;
import jp.riken.brain.ni.samuraigraph.figure.dialog.SGISXYDataDialogObserver;

class SGSXYDataDialogBuilder {

  private final SGPropertyDialogSXYData owner;

  public SGSXYDataDialogBuilder(final SGPropertyDialogSXYData owner) {
    this.owner = owner;
  }

  void initializeLineSymbolPanel() {
    java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    owner.mLineSymbolPanel.setLayout(new java.awt.GridBagLayout());

    owner.mSymbolVisibleCheckBox.setText("Visible");
    owner.mSymbolVisibleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 10);
    owner.mLineSymbolPanel.add(owner.mSymbolVisibleCheckBox, gridBagConstraints);

    owner.mSymbolTypePanel.setLayout(new java.awt.GridBagLayout());

    owner.mSymbolTypeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mSymbolTypeLabel.setText("Type");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
    owner.mSymbolTypePanel.add(owner.mSymbolTypeLabel, gridBagConstraints);

    owner.mSymbolTypeComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mSymbolTypeComboBox.setMinimumSize(new java.awt.Dimension(150, 22));
    owner.mSymbolTypeComboBox.setPreferredSize(new java.awt.Dimension(150, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
    owner.mSymbolTypePanel.add(owner.mSymbolTypeComboBox, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 30, 2, 0);
    owner.mLineSymbolPanel.add(owner.mSymbolTypePanel, gridBagConstraints);

    owner.mSymbolBodyLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
    owner.mSymbolBodyLabel.setText("Body");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 5);
    owner.mLineSymbolPanel.add(owner.mSymbolBodyLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 50, 0, 10);
    owner.mLineSymbolPanel.add(owner.mSymbolBodySeparator, gridBagConstraints);

    owner.mSymbolSizeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mSymbolSizeLabel.setText("Size");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 5);
    owner.mLineSymbolPanel.add(owner.mSymbolSizeLabel, gridBagConstraints);

    owner.mSymbolSizeSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mSymbolSizeSpinner.setMinimumSize(new java.awt.Dimension(75, 22));
    owner.mSymbolSizeSpinner.setPreferredSize(new java.awt.Dimension(75, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 5);
    owner.mLineSymbolPanel.add(owner.mSymbolSizeSpinner, gridBagConstraints);

    owner.mSymbolColorLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mSymbolColorLabel.setText("Color");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 20, 2, 5);
    owner.mLineSymbolPanel.add(owner.mSymbolColorLabel, gridBagConstraints);

    owner.mSymbolColorButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
    owner.mSymbolColorButton.setMinimumSize(new java.awt.Dimension(65, 20));
    owner.mSymbolColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
    owner.mLineSymbolPanel.add(owner.mSymbolColorButton, gridBagConstraints);

    owner.mSymbolBodyTransparencyPanel.setLayout(new java.awt.GridBagLayout());

    owner.mSymbolBodyTransparencyLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mSymbolBodyTransparencyLabel.setText("Transparency");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mSymbolBodyTransparencyPanel.add(owner.mSymbolBodyTransparencyLabel, gridBagConstraints);

    owner.mSymbolBodyTransparencySpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mSymbolBodyTransparencySpinner.setMinimumSize(new java.awt.Dimension(75, 22));
    owner.mSymbolBodyTransparencySpinner.setPreferredSize(new java.awt.Dimension(75, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
    owner.mSymbolBodyTransparencyPanel.add(
        owner.mSymbolBodyTransparencySpinner, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 0);
    owner.mLineSymbolPanel.add(owner.mSymbolBodyTransparencyPanel, gridBagConstraints);

    owner.mSymbolLineWidthSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mSymbolLineWidthSpinner.setMinimumSize(new java.awt.Dimension(75, 22));
    owner.mSymbolLineWidthSpinner.setPreferredSize(new java.awt.Dimension(75, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    owner.mLineSymbolPanel.add(owner.mSymbolLineWidthSpinner, gridBagConstraints);

    owner.mSymbolLineWidthLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mSymbolLineWidthLabel.setText("Width");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 5);
    owner.mLineSymbolPanel.add(owner.mSymbolLineWidthLabel, gridBagConstraints);

    owner.mSymbolLineColorLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mSymbolLineColorLabel.setText("Color");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 5);
    owner.mLineSymbolPanel.add(owner.mSymbolLineColorLabel, gridBagConstraints);

    owner.mSymbolLineColorButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
    owner.mSymbolLineColorButton.setMinimumSize(new java.awt.Dimension(65, 20));
    owner.mSymbolLineColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 10);
    owner.mLineSymbolPanel.add(owner.mSymbolLineColorButton, gridBagConstraints);

    owner.mSymbolLineLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
    owner.mSymbolLineLabel.setText("Line");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 15, 2, 5);
    owner.mLineSymbolPanel.add(owner.mSymbolLineLabel, gridBagConstraints);

    owner.mSymbolLineVisibleCheckBox.setText("Visible");
    owner.mSymbolLineVisibleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 0);
    owner.mLineSymbolPanel.add(owner.mSymbolLineVisibleCheckBox, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 45, 2, 10);
    owner.mLineSymbolPanel.add(owner.mSymbolLineSeparator, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mLineSubPanel.add(owner.mLineSymbolPanel, gridBagConstraints);

    owner.mLineStyleCustomizeButton.setText("Customize");
    owner.mLineStyleCustomizeButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
  }

  void initializeBarBodyPanel() {
    java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    owner.mBodyPanel.setLayout(new java.awt.GridBagLayout());

    owner.mBarBodyTransparencyPanel.setLayout(new java.awt.GridBagLayout());

    owner.mBarBodyTransparencyLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mBarBodyTransparencyLabel.setText("Transparency");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
    owner.mBarBodyTransparencyPanel.add(owner.mBarBodyTransparencyLabel, gridBagConstraints);

    owner.mBarBodyTransparencySpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mBarBodyTransparencySpinner.setMinimumSize(new java.awt.Dimension(75, 22));
    owner.mBarBodyTransparencySpinner.setPreferredSize(new java.awt.Dimension(75, 22));
    owner.mBarBodyTransparencySpinner.setText("");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mBarBodyTransparencyPanel.add(owner.mBarBodyTransparencySpinner, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 10);
    owner.mBodyPanel.add(owner.mBarBodyTransparencyPanel, gridBagConstraints);

    owner.mBarColorPanel.setLayout(new java.awt.GridBagLayout());

    owner.mBarColorLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mBarColorLabel.setText("Color");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
    owner.mBarColorPanel.add(owner.mBarColorLabel, gridBagConstraints);

    owner.mBarBodyColorFillRadioButton.setText("Fill");
    owner.mBarBodyColorFillRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mBarColorPanel.add(owner.mBarBodyColorFillRadioButton, gridBagConstraints);

    owner.mBarBodyColorPatternRadioButton.setText("Pattern");
    owner.mBarBodyColorPatternRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mBarColorPanel.add(owner.mBarBodyColorPatternRadioButton, gridBagConstraints);

    owner.mBarBodyColorGradationRadioButton.setText("Gradation");
    owner.mBarBodyColorGradationRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mBarColorPanel.add(owner.mBarBodyColorGradationRadioButton, gridBagConstraints);

    owner.mBarBodyColorGradationColorButton.setMinimumSize(new java.awt.Dimension(65, 20));
    owner.mBarBodyColorGradationColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mBarColorPanel.add(owner.mBarBodyColorGradationColorButton, gridBagConstraints);

    owner.mBarBodyColorPatternPaintButton.setMinimumSize(new java.awt.Dimension(65, 20));
    owner.mBarBodyColorPatternPaintButton.setPreferredSize(new java.awt.Dimension(65, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mBarColorPanel.add(owner.mBarBodyColorPatternPaintButton, gridBagConstraints);

    owner.mBarInnerColorButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
    owner.mBarInnerColorButton.setMinimumSize(new java.awt.Dimension(65, 20));
    owner.mBarInnerColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mBarColorPanel.add(owner.mBarInnerColorButton, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mBodyPanel.add(owner.mBarColorPanel, gridBagConstraints);

    owner.mBarBodyWidthIntervalPanel.setLayout(new java.awt.GridBagLayout());

    owner.mBarIntervalPanel.setLayout(new java.awt.GridBagLayout());

    owner.mBarIntervalTextField.setColumns(6);
    owner.mBarIntervalTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mBarIntervalPanel.add(owner.mBarIntervalTextField, gridBagConstraints);

    owner.mBarIntervalDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mBarIntervalPanel.add(owner.mBarIntervalDateButton, gridBagConstraints);

    owner.mBarIntervalLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mBarIntervalLabel.setText("Interval");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
    owner.mBarIntervalPanel.add(owner.mBarIntervalLabel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    owner.mBarBodyWidthIntervalPanel.add(owner.mBarIntervalPanel, gridBagConstraints);

    owner.mBarWidthPanel.setLayout(new java.awt.GridBagLayout());

    owner.mBarWidthTextField.setColumns(6);
    owner.mBarWidthTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mBarWidthPanel.add(owner.mBarWidthTextField, gridBagConstraints);

    owner.mBarWidthDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mBarWidthPanel.add(owner.mBarWidthDateButton, gridBagConstraints);

    owner.mBarWidthLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mBarWidthLabel.setText("Width");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
    owner.mBarWidthPanel.add(owner.mBarWidthLabel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
    owner.mBarBodyWidthIntervalPanel.add(owner.mBarWidthPanel, gridBagConstraints);

    owner.mBodyPanel.add(owner.mBarBodyWidthIntervalPanel, new java.awt.GridBagConstraints());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
    owner.mBarSubPanel.add(owner.mBodyPanel, gridBagConstraints);
  }

  void createComponents() {
    java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();

    owner.mLineEditButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    owner.mCommonPanel = new javax.swing.JPanel();
    owner.mLegendVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
    owner.mNameLabel = new javax.swing.JLabel();
    owner.mNameField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
    owner.mDataColumnSelectionButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    owner.mTabbedPane = new javax.swing.JTabbedPane();
    owner.mLinePanel = new javax.swing.JPanel();
    owner.mLineSubPanel = new javax.swing.JPanel();
    owner.mLineVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
    owner.mLineWidthLabel = new javax.swing.JLabel();
    owner.mLineWidthSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
    owner.mLineColorLabel = new javax.swing.JLabel();
    owner.mLineColorButton = new jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton();
    owner.mLineTypeLabel = new javax.swing.JLabel();
    owner.mLineTypeComboBox = new jp.riken.brain.ni.samuraigraph.base.SGComboBox<>();
    owner.mLineConnectCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
    owner.mLineSymbolLabel = new javax.swing.JLabel();
    owner.mLineSymbolSeparator = new javax.swing.JSeparator();
    owner.mLineSymbolPanel = new javax.swing.JPanel();
    owner.mSymbolVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
    owner.mSymbolTypePanel = new javax.swing.JPanel();
    owner.mSymbolTypeLabel = new javax.swing.JLabel();
    owner.mSymbolTypeComboBox = new jp.riken.brain.ni.samuraigraph.base.SGComboBox<>();
    owner.mSymbolBodyLabel = new javax.swing.JLabel();
    owner.mSymbolBodySeparator = new javax.swing.JSeparator();
    owner.mSymbolSizeLabel = new javax.swing.JLabel();
    owner.mSymbolSizeSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
    owner.mSymbolColorLabel = new javax.swing.JLabel();
    owner.mSymbolColorButton = new jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton();
    owner.mSymbolBodyTransparencyPanel = new javax.swing.JPanel();
    owner.mSymbolBodyTransparencyLabel = new javax.swing.JLabel();
    owner.mSymbolBodyTransparencySpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
    owner.mSymbolLineWidthSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
    owner.mSymbolLineWidthLabel = new javax.swing.JLabel();
    owner.mSymbolLineColorLabel = new javax.swing.JLabel();
    owner.mSymbolLineColorButton = new jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton();
    owner.mSymbolLineLabel = new javax.swing.JLabel();
    owner.mSymbolLineVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
    owner.mSymbolLineSeparator = new javax.swing.JSeparator();
    owner.mLineStyleCustomizeButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    owner.mBarPanel = new javax.swing.JPanel();
    owner.mBarSubPanel = new javax.swing.JPanel();
    owner.mBodyPanel = new javax.swing.JPanel();
    owner.mBarBodyTransparencyPanel = new javax.swing.JPanel();
    owner.mBarBodyTransparencyLabel = new javax.swing.JLabel();
    owner.mBarBodyTransparencySpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
    owner.mBarColorPanel = new javax.swing.JPanel();
    owner.mBarColorLabel = new javax.swing.JLabel();
    owner.mBarBodyColorFillRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
    owner.mBarBodyColorPatternRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
    owner.mBarBodyColorGradationRadioButton =
        new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
    owner.mBarBodyColorGradationColorButton =
        new jp.riken.brain.ni.samuraigraph.base.SGGradationPaintSelectionButton();
    owner.mBarBodyColorPatternPaintButton =
        new jp.riken.brain.ni.samuraigraph.base.SGPatternPaintSelectionButton();
    owner.mBarInnerColorButton = new jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton();
    owner.mBarBodyWidthIntervalPanel = new javax.swing.JPanel();
    owner.mBarIntervalPanel = new javax.swing.JPanel();
    owner.mBarIntervalTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
    owner.mBarIntervalDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    owner.mBarIntervalLabel = new javax.swing.JLabel();
    owner.mBarWidthPanel = new javax.swing.JPanel();
    owner.mBarWidthTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
    owner.mBarWidthDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    owner.mBarWidthLabel = new javax.swing.JLabel();
    owner.mBarLinePanel = new javax.swing.JPanel();
    owner.mBarLineWidthLabel = new javax.swing.JLabel();
    owner.mBarLineWidthSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
    owner.mBarLineColorLabel = new javax.swing.JLabel();
    owner.mBarLineColorButton = new jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton();
    owner.mBarOffsetPanel = new javax.swing.JPanel();
    owner.mBarOffsetXPanel = new javax.swing.JPanel();
    owner.mBarOffsetXTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
    owner.mBarOffsetXDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    owner.mBarOffsetXLabel = new javax.swing.JLabel();
    owner.mBarOffsetYPanel = new javax.swing.JPanel();
    owner.mBarOffsetYTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
    owner.mBarOffsetYDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    owner.mBarOffsetYLabel = new javax.swing.JLabel();
    owner.mBarBodyLabel = new javax.swing.JLabel();
    owner.mBarBodySeparator = new javax.swing.JSeparator();
    owner.mBarTopPanel = new javax.swing.JPanel();
    owner.mBarBaseLineValueLabel = new javax.swing.JLabel();
    owner.mBarVerticalCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
    owner.mBarVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
    owner.mBarBaselinePanel = new javax.swing.JPanel();
    owner.mBarBaselineTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
    owner.mBarBaselineDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    owner.mBarLineLabel = new javax.swing.JLabel();
    owner.mBarLineVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
    owner.mBarLineSeparator = new javax.swing.JSeparator();
    owner.mBarOffsetLabel = new javax.swing.JLabel();
    owner.mBarOffsetSeparator = new javax.swing.JSeparator();
    owner.mErrorBarPanel = new javax.swing.JPanel();
    owner.mErrorBarSubPanel = new javax.swing.JPanel();
    owner.mErrorBarVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
    owner.mErrorBarSymbolSizeLabel = new javax.swing.JLabel();
    owner.mErrorBarSymbolSizeSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
    owner.mErrorBarColorLabel = new javax.swing.JLabel();
    owner.mErrorBarTypeLabel = new javax.swing.JLabel();
    owner.mErrorBarTypeComboBox = new jp.riken.brain.ni.samuraigraph.base.SGComboBox<>();
    owner.mErrorBarColorButton = new jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton();
    owner.mErrorBarStyleLabel = new javax.swing.JLabel();
    owner.mErrorBarSymbolLabel = new javax.swing.JLabel();
    owner.mErrorBarStyleSeparator = new javax.swing.JSeparator();
    owner.mErrorBarSymbolSeparator = new javax.swing.JSeparator();
    owner.mErrorBarStylePanel = new javax.swing.JPanel();
    owner.mErrorBarBothsidesRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
    owner.mErrorBarUpsideRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
    owner.mErrorBarDownsideRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
    owner.mErrorBarLineWidthPanel = new javax.swing.JPanel();
    owner.mErrorBarLineWidthLabel1 = new javax.swing.JLabel();
    owner.mErrorBarLineWidthSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
    owner.mErrorBarLineWidthLabel2 = new javax.swing.JLabel();
    owner.mErrorBarPositionLabel = new javax.swing.JLabel();
    owner.mErrorBarPositionSeparator = new javax.swing.JSeparator();
    owner.mErrorBarPositionPanel = new javax.swing.JPanel();
    owner.mErrorBarPositionLineRadioButton =
        new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
    owner.mErrorBarPositionBarRadioButton = new jp.riken.brain.ni.samuraigraph.base.SGRadioButton();
    owner.mTickLabelPanel = new javax.swing.JPanel();
    owner.mTickLabelSubPanel = new javax.swing.JPanel();
    owner.mTickLabelFontSizeLabel = new javax.swing.JLabel();
    owner.mTickLabelVisibleCheckBox = new jp.riken.brain.ni.samuraigraph.base.SGCheckBox();
    owner.mTickLabelFontSizeSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
    owner.mTickLabelColorButton = new jp.riken.brain.ni.samuraigraph.base.SGColorSelectionButton();
    owner.mTickLabelColorLabel = new javax.swing.JLabel();
    owner.mTickLabelAnglePanel = new javax.swing.JPanel();
    owner.mTickLabelAngleLabel = new javax.swing.JLabel();
    owner.mTickLabelAngleSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
    owner.mTickLabelFontLabel = new javax.swing.JLabel();
    owner.mTickLabelFontSeparator = new javax.swing.JSeparator();
    owner.mTickLabelTextLabel = new javax.swing.JLabel();
    owner.mTickLabelAngleSeparator = new javax.swing.JSeparator();
    owner.mTickLabelFontStyleComboBox = new jp.riken.brain.ni.samuraigraph.base.SGComboBox<>();
    owner.mTickLabelFontNameComboBox = new jp.riken.brain.ni.samuraigraph.base.SGComboBox<>();
    owner.mTickLabelFontNameLabel = new javax.swing.JLabel();
    owner.mTickLabelFontStyleLabel = new javax.swing.JLabel();
    owner.mTickLabelFormatLabel = new javax.swing.JLabel();
    owner.mTickLabelFormatSeparator = new javax.swing.JSeparator();
    owner.mTickLabelFormatPanel = new javax.swing.JPanel();
    owner.mTickLabelDecimalPlacesSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
    owner.mTickLabelDecimalPlacesLabel = new javax.swing.JLabel();
    owner.mTickLabelExponentLabel = new javax.swing.JLabel();
    owner.mTickLabelDateFormatComboBox = new jp.riken.brain.ni.samuraigraph.base.SGComboBox<>();
    owner.mExponentPanel = new javax.swing.JPanel();
    owner.mTickLabelExponentSpinner = new jp.riken.brain.ni.samuraigraph.base.SGSpinner();
    owner.mTickLabelExponentBaseLabel = new javax.swing.JLabel();
    owner.mTickLabelDateFormatLabel = new javax.swing.JLabel();
    owner.mButtonPanel = new javax.swing.JPanel();
    owner.mOKButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    owner.mCancelButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    owner.mPreviewButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    owner.mBottomCommonPanel = new javax.swing.JPanel();
    owner.mShiftXLabel = new javax.swing.JLabel();
    owner.mShiftLabel = new javax.swing.JLabel();
    owner.mShiftYLabel = new javax.swing.JLabel();
    owner.mShiftXPanel = new javax.swing.JPanel();
    owner.mShiftXTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
    owner.mShiftXDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    owner.mShiftYPanel = new javax.swing.JPanel();
    owner.mShiftYTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
    owner.mShiftYDateButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    owner.mHeadPanel = new javax.swing.JPanel();

    owner.mLineEditButton.setText("Edit");
  }

  void setupTickLabelTab() {
    java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    owner.mTickLabelPanel.setLayout(new java.awt.GridBagLayout());

    owner.mTickLabelSubPanel.setLayout(new java.awt.GridBagLayout());

    owner.mTickLabelFontSizeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mTickLabelFontSizeLabel.setText("Size");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 5);
    owner.mTickLabelSubPanel.add(owner.mTickLabelFontSizeLabel, gridBagConstraints);

    owner.mTickLabelVisibleCheckBox.setText("Visible");
    owner.mTickLabelVisibleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 10);
    owner.mTickLabelSubPanel.add(owner.mTickLabelVisibleCheckBox, gridBagConstraints);

    owner.mTickLabelFontSizeSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mTickLabelFontSizeSpinner.setPreferredSize(new java.awt.Dimension(75, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 5);
    owner.mTickLabelSubPanel.add(owner.mTickLabelFontSizeSpinner, gridBagConstraints);

    owner.mTickLabelColorButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
    owner.mTickLabelColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
    owner.mTickLabelSubPanel.add(owner.mTickLabelColorButton, gridBagConstraints);

    owner.mTickLabelColorLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mTickLabelColorLabel.setText("Color");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 20, 2, 5);
    owner.mTickLabelSubPanel.add(owner.mTickLabelColorLabel, gridBagConstraints);

    owner.mTickLabelAnglePanel.setMinimumSize(new java.awt.Dimension(150, 22));

    owner.initializeTickLabelAnglePanel();

    owner.mTickLabelFontLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
    owner.mTickLabelFontLabel.setText("Font");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 15, 2, 5);
    owner.mTickLabelSubPanel.add(owner.mTickLabelFontLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 45, 2, 10);
    owner.mTickLabelSubPanel.add(owner.mTickLabelFontSeparator, gridBagConstraints);

    owner.mTickLabelTextLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
    owner.mTickLabelTextLabel.setText("Text");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 10);
    owner.mTickLabelSubPanel.add(owner.mTickLabelTextLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 55, 0, 10);
    owner.mTickLabelSubPanel.add(owner.mTickLabelAngleSeparator, gridBagConstraints);

    owner.mTickLabelFontStyleComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mTickLabelFontStyleComboBox.setPreferredSize(new java.awt.Dimension(100, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
    owner.mTickLabelSubPanel.add(owner.mTickLabelFontStyleComboBox, gridBagConstraints);

    owner.mTickLabelFontNameComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mTickLabelFontNameComboBox.setPreferredSize(new java.awt.Dimension(170, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
    owner.mTickLabelSubPanel.add(owner.mTickLabelFontNameComboBox, gridBagConstraints);

    owner.mTickLabelFontNameLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mTickLabelFontNameLabel.setText("Family");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 5);
    owner.mTickLabelSubPanel.add(owner.mTickLabelFontNameLabel, gridBagConstraints);

    owner.mTickLabelFontStyleLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mTickLabelFontStyleLabel.setText("Style");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 5);
    owner.mTickLabelSubPanel.add(owner.mTickLabelFontStyleLabel, gridBagConstraints);

    owner.mTickLabelFormatLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
    owner.mTickLabelFormatLabel.setText("Format");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 10);
    owner.mTickLabelSubPanel.add(owner.mTickLabelFormatLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
    owner.mTickLabelSubPanel.add(owner.mTickLabelFormatSeparator, gridBagConstraints);

    owner.initializeTickLabelFormatPanel();

    owner.initializeTickLabelExponentPanel();

    owner.mTickLabelSubPanel.add(owner.mTickLabelFormatPanel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 40, 0);
    owner.mTickLabelPanel.add(owner.mTickLabelSubPanel, gridBagConstraints);

    owner.mTabbedPane.addTab("Tick Label", owner.mTickLabelPanel);
  }

  void setupErrorBarTab() {
    java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    owner.mErrorBarPanel.setLayout(new java.awt.GridBagLayout());

    owner.mErrorBarSubPanel.setLayout(new java.awt.GridBagLayout());

    owner.mErrorBarVisibleCheckBox.setText("Visible");
    owner.mErrorBarVisibleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 5);
    owner.mErrorBarSubPanel.add(owner.mErrorBarVisibleCheckBox, gridBagConstraints);

    owner.mErrorBarSymbolSizeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mErrorBarSymbolSizeLabel.setText("Size");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 5);
    owner.mErrorBarSubPanel.add(owner.mErrorBarSymbolSizeLabel, gridBagConstraints);

    owner.mErrorBarSymbolSizeSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mErrorBarSymbolSizeSpinner.setPreferredSize(new java.awt.Dimension(75, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 5);
    owner.mErrorBarSubPanel.add(owner.mErrorBarSymbolSizeSpinner, gridBagConstraints);

    owner.mErrorBarColorLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mErrorBarColorLabel.setText("Color");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 20, 2, 5);
    owner.mErrorBarSubPanel.add(owner.mErrorBarColorLabel, gridBagConstraints);

    owner.mErrorBarTypeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mErrorBarTypeLabel.setText("Type");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 5);
    owner.mErrorBarSubPanel.add(owner.mErrorBarTypeLabel, gridBagConstraints);

    owner.mErrorBarTypeComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mErrorBarTypeComboBox.setPreferredSize(new java.awt.Dimension(90, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
    owner.mErrorBarSubPanel.add(owner.mErrorBarTypeComboBox, gridBagConstraints);

    owner.mErrorBarColorButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
    owner.mErrorBarColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
    owner.mErrorBarSubPanel.add(owner.mErrorBarColorButton, gridBagConstraints);

    owner.mErrorBarStyleLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
    owner.mErrorBarStyleLabel.setText("Style");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 10);
    owner.mErrorBarSubPanel.add(owner.mErrorBarStyleLabel, gridBagConstraints);

    owner.mErrorBarSymbolLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
    owner.mErrorBarSymbolLabel.setText("Symbol ");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 10);
    owner.mErrorBarSubPanel.add(owner.mErrorBarSymbolLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 10);
    owner.mErrorBarSubPanel.add(owner.mErrorBarStyleSeparator, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 65, 0, 10);
    owner.mErrorBarSubPanel.add(owner.mErrorBarSymbolSeparator, gridBagConstraints);

    owner.initializeErrorBarStylePanel();

    owner.initializeErrorBarLineWidthPanel();

    owner.mErrorBarPositionLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
    owner.mErrorBarPositionLabel.setText("Position");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 10);
    owner.mErrorBarSubPanel.add(owner.mErrorBarPositionLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 70, 0, 10);
    owner.mErrorBarSubPanel.add(owner.mErrorBarPositionSeparator, gridBagConstraints);

    owner.initializeErrorBarPositionPanel();

    owner.mErrorBarPanel.add(owner.mErrorBarSubPanel, gridBagConstraints);

    owner.mTabbedPane.addTab("Error Bar", owner.mErrorBarPanel);
  }

  void setupLineTab() {
    java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    owner.mLinePanel.setLayout(new java.awt.GridBagLayout());

    owner.mLineSubPanel.setLayout(new java.awt.GridBagLayout());

    owner.mLineVisibleCheckBox.setText("Visible");
    owner.mLineVisibleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 10);
    owner.mLineSubPanel.add(owner.mLineVisibleCheckBox, gridBagConstraints);

    owner.mLineWidthLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mLineWidthLabel.setText("Width");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 15, 2, 5);
    owner.mLineSubPanel.add(owner.mLineWidthLabel, gridBagConstraints);

    owner.mLineWidthSpinner.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mLineWidthSpinner.setMinimumSize(new java.awt.Dimension(75, 22));
    owner.mLineWidthSpinner.setPreferredSize(new java.awt.Dimension(75, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 5);
    owner.mLineSubPanel.add(owner.mLineWidthSpinner, gridBagConstraints);

    owner.mLineColorLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mLineColorLabel.setText("Color");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 20, 2, 5);
    owner.mLineSubPanel.add(owner.mLineColorLabel, gridBagConstraints);

    owner.mLineColorButton.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
    owner.mLineColorButton.setMinimumSize(new java.awt.Dimension(65, 20));
    owner.mLineColorButton.setPreferredSize(new java.awt.Dimension(65, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
    owner.mLineSubPanel.add(owner.mLineColorButton, gridBagConstraints);

    owner.mLineTypeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mLineTypeLabel.setText("Type");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 15, 2, 5);
    owner.mLineSubPanel.add(owner.mLineTypeLabel, gridBagConstraints);

    owner.mLineTypeComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mLineTypeComboBox.setMinimumSize(new java.awt.Dimension(140, 22));
    owner.mLineTypeComboBox.setPreferredSize(new java.awt.Dimension(140, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 5);
    owner.mLineSubPanel.add(owner.mLineTypeComboBox, gridBagConstraints);

    owner.mLineConnectCheckBox.setText("Ignore Missing Values");
    owner.mLineConnectCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 40, 2, 10);
    owner.mLineSubPanel.add(owner.mLineConnectCheckBox, gridBagConstraints);

    owner.mLineSymbolLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
    owner.mLineSymbolLabel.setText("Symbol");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
    owner.mLineSubPanel.add(owner.mLineSymbolLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 50, 2, 10);
    owner.mLineSubPanel.add(owner.mLineSymbolSeparator, gridBagConstraints);

    owner.initializeLineSymbolPanel();

    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
    owner.mLineSubPanel.add(owner.mLineStyleCustomizeButton, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 30, 0);
    owner.mLinePanel.add(owner.mLineSubPanel, gridBagConstraints);

    owner.mTabbedPane.addTab("Line", owner.mLinePanel);
  }

  void finishComponentLayout() {
    java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.getContentPane().add(owner.mTabbedPane, gridBagConstraints);

    owner.mOKButton.setText("OK");
    owner.mOKButton.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
    owner.mButtonPanel.add(owner.mOKButton);

    owner.mCancelButton.setText("Cancel");
    owner.mCancelButton.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
    owner.mButtonPanel.add(owner.mCancelButton);

    owner.mPreviewButton.setText("Preview");
    owner.mPreviewButton.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
    owner.mButtonPanel.add(owner.mPreviewButton);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    owner.getContentPane().add(owner.mButtonPanel, gridBagConstraints);

    owner.mBottomCommonPanel.setLayout(new java.awt.GridBagLayout());

    owner.mShiftXLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mShiftXLabel.setText("X");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 5);
    owner.mBottomCommonPanel.add(owner.mShiftXLabel, gridBagConstraints);

    owner.mShiftLabel.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
    owner.mShiftLabel.setText("Shift");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
    owner.mBottomCommonPanel.add(owner.mShiftLabel, gridBagConstraints);

    owner.mShiftYLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    owner.mShiftYLabel.setText("Y");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    owner.mBottomCommonPanel.add(owner.mShiftYLabel, gridBagConstraints);

    owner.mShiftXPanel.setLayout(new java.awt.GridBagLayout());

    owner.mShiftXTextField.setColumns(6);
    owner.mShiftXTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mShiftXPanel.add(owner.mShiftXTextField, gridBagConstraints);

    owner.mShiftXDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mShiftXPanel.add(owner.mShiftXDateButton, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    owner.mBottomCommonPanel.add(owner.mShiftXPanel, gridBagConstraints);

    owner.mShiftYPanel.setLayout(new java.awt.GridBagLayout());

    owner.mShiftYTextField.setColumns(6);
    owner.mShiftYTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mShiftYPanel.add(owner.mShiftYTextField, gridBagConstraints);

    owner.mShiftYDateButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.mShiftYPanel.add(owner.mShiftYDateButton, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    owner.mBottomCommonPanel.add(owner.mShiftYPanel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    owner.getContentPane().add(owner.mBottomCommonPanel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    owner.getContentPane().add(owner.mHeadPanel, gridBagConstraints);

    owner.pack();
  }

  void initSpinnerModels() {
    // Sets up spinner models
    //

    // line
    owner.mLineWidthSpinner.initProperties(
        owner.getLineWidthSpinnerNumberModel(),
        SGConstants.LINE_WIDTH_UNIT,
        SGConstants.LINE_WIDTH_FRAC_DIGIT_MIN,
        SGConstants.LINE_WIDTH_FRAC_DIGIT_MAX);

    // symbol
    owner.mSymbolSizeSpinner.initProperties(
        new SpinnerNumberModel(
            0.1,
            SGFigureDrawingElementConstants.SYMBOL_SIZE_MIN,
            SGFigureDrawingElementConstants.SYMBOL_SIZE_MAX,
            SGFigureDrawingElementConstants.SYMBOL_SIZE_STEP),
        SGFigureDrawingElementConstants.SYMBOL_SIZE_UNIT,
        SGFigureDrawingElementConstants.SYMBOL_SIZE_FRAC_DIFIT_MIN,
        SGFigureDrawingElementConstants.SYMBOL_SIZE_FRAC_DIFIT_MAX);

    owner.mSymbolLineWidthSpinner.initProperties(
        owner.getLineWidthSpinnerNumberModel(),
        SGConstants.LINE_WIDTH_UNIT,
        SGConstants.LINE_WIDTH_FRAC_DIGIT_MIN,
        SGConstants.LINE_WIDTH_FRAC_DIGIT_MAX);

    owner.mSymbolBodyTransparencySpinner.initProperties(
        new SpinnerNumberModel(
            100.0,
            (float) SGPaintConstants.TRANSPARENCY_MIN,
            (float) SGPaintConstants.TRANSPARENCY_MAX,
            (float) SGPaintConstants.TRANSPARENCY_STEP),
        SGConstants.percent,
        SGPaintConstants.TRANSPARENCY_FRAC_DIGIT_MIN,
        SGPaintConstants.TRANSPARENCY_FRAC_DIGIT_MAX);

    // bar
    owner.mBarLineWidthSpinner.initProperties(
        owner.getLineWidthSpinnerNumberModel(),
        SGConstants.LINE_WIDTH_UNIT,
        SGConstants.LINE_WIDTH_FRAC_DIGIT_MIN,
        SGConstants.LINE_WIDTH_FRAC_DIGIT_MAX);

    // error bar
    owner.mErrorBarLineWidthSpinner.initProperties(
        owner.getLineWidthSpinnerNumberModel(),
        SGConstants.LINE_WIDTH_UNIT,
        SGConstants.LINE_WIDTH_FRAC_DIGIT_MIN,
        SGConstants.LINE_WIDTH_FRAC_DIGIT_MAX);

    // bar inner paint transparency
    owner.mBarBodyTransparencySpinner.initProperties(
        new SpinnerNumberModel(
            100.0,
            (float) SGPaintConstants.TRANSPARENCY_MIN,
            (float) SGPaintConstants.TRANSPARENCY_MAX,
            (float) SGPaintConstants.TRANSPARENCY_STEP),
        SGConstants.percent,
        SGPaintConstants.TRANSPARENCY_FRAC_DIGIT_MIN,
        SGPaintConstants.TRANSPARENCY_FRAC_DIGIT_MAX);

    owner.mErrorBarSymbolSizeSpinner.initProperties(
        new SpinnerNumberModel(
            0.1,
            SGFigureDrawingElementConstants.ERROR_BAR_HEAD_SIZE_MIN,
            SGFigureDrawingElementConstants.ERROR_BAR_HEAD_SIZE_MAX,
            SGFigureDrawingElementConstants.ERROR_BAR_HEAD_SIZE_STEP),
        SGFigureDrawingElementConstants.ERROR_BAR_HEAD_SIZE_UNIT,
        SGFigureDrawingElementConstants.ERROR_BAR_HEAD_SIZE_FRAC_DIFIT_MIN,
        SGFigureDrawingElementConstants.ERROR_BAR_HEAD_SIZE_FRAC_DIFIT_MAX);

    // tick label
    owner.mTickLabelFontSizeSpinner.initProperties(
        owner.getFontSizeSpinnerNumberModel(),
        SGConstants.FONT_SIZE_UNIT,
        SGConstants.FONT_SIZE_FRAC_DIGIT_MIN,
        SGConstants.FONT_SIZE_FRAC_DIGIT_MAX);
    owner.mTickLabelDecimalPlacesSpinner.initProperties(
        new SpinnerNumberModel(
            0.0,
            (float) SGFigureDrawingElementConstants.TICK_LABEL_DECIMAL_PLACES_MIN,
            (float) SGFigureDrawingElementConstants.TICK_LABEL_DECIMAL_PLACES_MAX,
            (float) SGFigureDrawingElementConstants.TICK_LABEL_DECIMAL_PLACES_STEP),
        null,
        0,
        0);
    owner.mTickLabelExponentSpinner.initProperties(
        new SpinnerNumberModel(
            0.0,
            (float) SGFigureDrawingElementConstants.TICK_LABEL_EXPONENT_MIN,
            (float) SGFigureDrawingElementConstants.TICK_LABEL_EXPONENT_MAX,
            (float) SGFigureDrawingElementConstants.TICK_LABEL_EXPONENT_STEP),
        null,
        0,
        0);
    owner.mTickLabelAngleSpinner.initProperties(
        new SpinnerNumberModel(
            0.0,
            (float) SGFigureDrawingElementConstants.TICK_LABEL_TEXT_ANGLE_MIN,
            (float) SGFigureDrawingElementConstants.TICK_LABEL_TEXT_ANGLE_MAX,
            (float) SGFigureDrawingElementConstants.TICK_LABEL_TEXT_ANGLE_STEP),
        SGConstants.degree,
        SGFigureDrawingElementConstants.TICK_LABEL_TEXT_ANGLE_FRAC_DIFIT_MIN,
        SGFigureDrawingElementConstants.TICK_LABEL_TEXT_ANGLE_FRAC_DIFIT_MAX);
  }

  void handleDataColumnSelectionDialogAction(final Object source, final String command) {
    if (source.equals(owner.mDataColumnSelectionDialog)) {
      SGDataSetupDialog dg = (SGDataSetupDialog) source;
      if (owner.OK_BUTTON_TEXT.equals(command)) {
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
          if (colType.startsWith(SGDataColumnTypeConstants.LOWER_ERROR_VALUE)
              || colType.startsWith(SGDataColumnTypeConstants.UPPER_ERROR_VALUE)
              || colType.startsWith(SGDataColumnTypeConstants.LOWER_UPPER_ERROR_VALUE)) {
            ebFlag = true;
          } else if (colType.startsWith(SGDataColumnTypeConstants.TICK_LABEL)) {
            tlFlag = true;
          } else if (SGDataColumnTypeConstants.VALUE_TYPE_DATE.equals(valueType)) {
            if (SGDataColumnTypeConstants.X_VALUE.equals(colType)
                || SGDataColumnTypeConstants.Y_VALUE.equals(colType)) {
              tlFlag = true;
            }
          }
        }
        owner.setTabEnabled(owner.mErrorBarPanel, ebFlag);
        owner.setTabEnabled(owner.mTickLabelPanel, tlFlag);
        Component cur = owner.mTabbedPane.getSelectedComponent();
        if (!ebFlag) {
          if (owner.mErrorBarPanel.equals(cur)) {
            owner.mTabbedPane.setSelectedComponent(owner.mLinePanel);
          }
        }
        if (!tlFlag) {
          if (owner.mTickLabelPanel.equals(cur)) {
            owner.mTabbedPane.setSelectedComponent(owner.mLinePanel);
          }
        }

        SGISXYDataDialogObserver obs =
            (SGISXYDataDialogObserver) owner.getDialogObserverList().get(0);
        SGData data = obs.getData();
        SGIntegerSeriesSet pickUpIndices = null;
        SGIntegerSeriesSet pickUpIndicesOld = null;
        if (SGDataDataTypeUtility.isSDArrayData(data)) {
          // text data
          owner.setupBarVerticalByChangingTextDataColumn(colInfo, data);

          // get stride
          SGSDArrayDataSetupDialog sdg = (SGSDArrayDataSetupDialog) dg;
          SGIntegerSeriesSet stride = sdg.getSXYStride();
          owner.mStrideMap.put(SGDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE, stride);
          SGIntegerSeriesSet tickLabelStride = sdg.getSXYTickLabelStride();
          owner.mStrideMap.put(
              SGDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE, tickLabelStride);

        } else if (SGDataDataTypeUtility.isNetCDFData(data)) {
          // automatically.
          // but if change whether Pickup column type exists, data and data type are
          // converted and then must not be change bar vertical.
          if (obs instanceof SGElementGroupSetForData) {
            owner.setupBarVerticalByChangingNetCDFDataColumn(colInfo, data);
          }

          // get multiple origin and step
          SGNetCDFDataSetupDialog ndg = (SGNetCDFDataSetupDialog) dg;
          String dimName = ndg.getPickUpDimensionName();
          pickUpIndices = ndg.getSXYPickUpIndices();

          if (owner.mPickUpDimensionInfo != null) {
            pickUpIndicesOld = owner.mPickUpDimensionInfo.getIndices();
          }

          // set to the attribute
          if (pickUpIndices != null) {
            owner.mPickUpDimensionInfo = new SGNetCDFPickUpDimensionInfo(dimName, pickUpIndices);
          } else {
            owner.mPickUpDimensionInfo = null;
          }

          // get stride
          SGNetCDFData nData = (SGNetCDFData) data;
          if (nData.isIndexAvailable()) {
            SGIntegerSeriesSet indexStride = ndg.getSXYIndexStride();
            owner.mStrideMap.put(SGDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE, indexStride);
          } else {
            SGIntegerSeriesSet stride = ndg.getSXYStride();
            owner.mStrideMap.put(SGDataInformationKeyConstants.KEY_SXY_STRIDE, stride);
          }
          SGIntegerSeriesSet tickLabelStride = ndg.getSXYTickLabelStride();
          owner.mStrideMap.put(
              SGDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE, tickLabelStride);

        } else if (SGDataDataTypeUtility.isMDArrayData(data)) {
          SGMDArrayDataSetupDialog ndg = (SGMDArrayDataSetupDialog) dg;

          // get multiple origin and step
          List<String> pickUpVarNameList = ndg.getPickUpDatasetName();
          Map<String, Integer> dimensionIndexMap = ndg.getPickUpDimensionIndexMap();
          pickUpIndices = ndg.getIndices();

          if (owner.mPickUpDimensionInfo != null) {
            pickUpIndicesOld = owner.mPickUpDimensionInfo.getIndices();
          }

          // set to the attribute
          if (pickUpVarNameList != null && pickUpIndices != null) {
            owner.mPickUpDimensionInfo =
                new SGMDArrayPickUpDimensionInfo(dimensionIndexMap, pickUpIndices);
            for (int ii = 0; ii < colInfo.length; ii++) {
              SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfo[ii];
              String name = mdInfo.getName();
              if (pickUpVarNameList.contains(name)) {
                Integer dimensionIndex = dimensionIndexMap.get(name);
                mdInfo.setDimensionIndex(
                    SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, dimensionIndex);
                break;
              }
            }
          } else {
            owner.mPickUpDimensionInfo = null;
            for (int ii = 0; ii < colInfo.length; ii++) {
              SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfo[ii];
              mdInfo.clearDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
            }
          }

          // get stride
          SGIntegerSeriesSet stride = ndg.getSXYStride();
          owner.mStrideMap.put(SGDataInformationKeyConstants.KEY_SXY_STRIDE, stride);
          SGIntegerSeriesSet tickLabelStride = ndg.getSXYTickLabelStride();
          owner.mStrideMap.put(
              SGDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE, tickLabelStride);
        }

        // updates the line style
        if (owner.mDataInfoArray != null) {
          if (!SGDataColumnInfoUtility.hasEqualColumnType(owner.mDataInfoArray, colInfo)) {
            owner.updateLineStyle(obs, colInfo);
          } else if (owner.mPickUpDimensionInfo != null) {
            if (!SGUtility.equals(pickUpIndicesOld, pickUpIndices)) {
              owner.updateLineStyle(obs, colInfo);
            }
          }
        } else {
          owner.updateLineStyle(obs, colInfo);
        }

        // set to the attribute
        owner.mDataInfoArray = colInfo;
      }
    }
  }
}

package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import ucar.nc2.NetcdfFiles;

public class SGNetCDFDataFileChooserPanel extends javax.swing.JPanel
    implements ActionListener, ChangeListener {

  private static final long serialVersionUID = 7028556477430131723L;

  /** Creates new form SGNetCDFDataFileChooserPanel */
  public SGNetCDFDataFileChooserPanel() {
    initComponents();
    this.initProperty();
  }

  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    mTabbedPane = new javax.swing.JTabbedPane();
    mLocalFilePanel = new javax.swing.JPanel();
    mDataFileNameTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
    mShowFileChooserButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    mNetCDFUrlPanel = new javax.swing.JPanel();
    mNetCDFUrlTextField = new jp.riken.brain.ni.samuraigraph.base.SGTextField();
    mNetCDFUrlCheckValidButton = new jp.riken.brain.ni.samuraigraph.base.SGButton();
    mNetCDFUrlSpacerLabel = new javax.swing.JLabel();

    setLayout(new java.awt.GridBagLayout());

    mTabbedPane.setFont(new java.awt.Font("Dialog", 1, 12));

    mLocalFilePanel.setLayout(new java.awt.GridBagLayout());

    mDataFileNameTextField.setFont(new java.awt.Font("Dialog", 0, 12));
    mDataFileNameTextField.setMinimumSize(new java.awt.Dimension(250, 22));
    mDataFileNameTextField.setPreferredSize(new java.awt.Dimension(250, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 5);
    mLocalFilePanel.add(mDataFileNameTextField, gridBagConstraints);

    mShowFileChooserButton.setText("Browse");
    mShowFileChooserButton.setFont(new java.awt.Font("Dialog", 1, 12));
    mShowFileChooserButton.setMaximumSize(new java.awt.Dimension(80, 25));
    mShowFileChooserButton.setMinimumSize(new java.awt.Dimension(80, 25));
    mShowFileChooserButton.setPreferredSize(new java.awt.Dimension(80, 25));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(20, 5, 20, 20);
    mLocalFilePanel.add(mShowFileChooserButton, gridBagConstraints);

    mTabbedPane.addTab("Local File", mLocalFilePanel);

    mNetCDFUrlPanel.setFont(new java.awt.Font("Dialog", 1, 12));
    mNetCDFUrlPanel.setLayout(new java.awt.GridBagLayout());

    mNetCDFUrlTextField.setFont(new java.awt.Font("Dialog", 0, 12));
    mNetCDFUrlTextField.setMinimumSize(new java.awt.Dimension(250, 22));
    mNetCDFUrlTextField.setPreferredSize(new java.awt.Dimension(250, 22));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 5);
    mNetCDFUrlPanel.add(mNetCDFUrlTextField, gridBagConstraints);

    mNetCDFUrlCheckValidButton.setText("Open");
    mNetCDFUrlCheckValidButton.setFont(new java.awt.Font("Dialog", 1, 12));
    mNetCDFUrlCheckValidButton.setMaximumSize(new java.awt.Dimension(80, 25));
    mNetCDFUrlCheckValidButton.setMinimumSize(new java.awt.Dimension(80, 25));
    mNetCDFUrlCheckValidButton.setPreferredSize(new java.awt.Dimension(80, 25));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(20, 5, 20, 20);
    mNetCDFUrlPanel.add(mNetCDFUrlCheckValidButton, gridBagConstraints);

    mNetCDFUrlSpacerLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 15);
    mNetCDFUrlPanel.add(mNetCDFUrlSpacerLabel, gridBagConstraints);

    mTabbedPane.addTab("URL of NetCDF", mNetCDFUrlPanel);

    add(mTabbedPane, new java.awt.GridBagConstraints());
  }

  private jp.riken.brain.ni.samuraigraph.base.SGTextField mDataFileNameTextField;
  private javax.swing.JPanel mLocalFilePanel;
  private jp.riken.brain.ni.samuraigraph.base.SGButton mNetCDFUrlCheckValidButton;
  private javax.swing.JPanel mNetCDFUrlPanel;
  private javax.swing.JLabel mNetCDFUrlSpacerLabel;
  private jp.riken.brain.ni.samuraigraph.base.SGTextField mNetCDFUrlTextField;
  private jp.riken.brain.ni.samuraigraph.base.SGButton mShowFileChooserButton;
  private javax.swing.JTabbedPane mTabbedPane;

  private void initProperty() {
    // add an action event listener
    this.mNetCDFUrlCheckValidButton.addActionListener(this);
    this.mNetCDFUrlTextField.addActionListener(this);

    // add change listener
    this.mTabbedPane.addChangeListener(this);

    this.mTabbedPane.setSelectedIndex(0);

    this.mNetCDFUrlCheckValidButton.setVisible(false);
    this.mNetCDFUrlTextField.setPreferredSize(new java.awt.Dimension(340, 22));
  }

  protected JTextComponent getLocalFileNameTextComponent() {
    return this.mDataFileNameTextField;
  }

  protected JTextComponent getNetCDFUrlTextComponent() {
    return this.mNetCDFUrlTextField;
  }

  protected JButton getLocalFileChooserButton() {
    return this.mShowFileChooserButton;
  }

  public void focusedToLocalFileTextField() {
    this.mDataFileNameTextField.requestFocusInWindow();
  }

  public void setVisibleOfNetCDFUrlCheckButton(final boolean visible) {
    this.mNetCDFUrlCheckValidButton.setVisible(visible);
    if (visible == true) {
      this.mNetCDFUrlTextField.setPreferredSize(new java.awt.Dimension(250, 22));
    } else {
      this.mNetCDFUrlTextField.setPreferredSize(new java.awt.Dimension(340, 22));
    }
  }

  /**
   * Return the selected filename.
   *
   * @return filename
   */
  protected String getFileName() {
    if (this.mTabbedPane.getSelectedComponent().equals(this.mLocalFilePanel)) {
      return this.mDataFileNameTextField.getText().trim();
    } else {
      if (this.mNetCDFUrlCheckValidButton.isVisible() == false) {
        return this.mNetCDFUrlTextField.getText().trim();
      } else {
        return this.validUrlLocationInputted;
      }
    }
  }

  protected void setFileName(final String fileName) {
    if (isNetcdfUrl(fileName)) {
      this.mNetCDFUrlTextField.setText(fileName);
      this.mTabbedPane.setSelectedComponent(this.mNetCDFUrlPanel);
      this.checkValidationOfNetCDFUrlAndFireResult(false);
    } else {
      this.mDataFileNameTextField.setText(fileName);
      this.mTabbedPane.setSelectedComponent(this.mLocalFilePanel);
    }
  }

  /**
   * Return whether data file is local or not.
   *
   * @return true if data file is local.
   */
  public boolean isLocalFileSelected() {
    return this.mTabbedPane.getSelectedComponent().equals(this.mLocalFilePanel);
  }

  public void setLocalFileSelected(final boolean flag) {
    if (flag) {
      this.mTabbedPane.setSelectedComponent(this.mLocalFilePanel);
    } else {
      this.mTabbedPane.setSelectedComponent(this.mNetCDFUrlPanel);
    }
  }

  /**
   * @return true if netCDF URL is selected and the url is set.
   */
  public boolean isNetCDFUrlAcceptable() {
    if (this.mTabbedPane.getSelectedComponent().equals(this.mNetCDFUrlPanel)) {
      if (this.mNetCDFUrlCheckValidButton.isVisible() == false) {
        return true;
      } else {
        if (isValidUrlLocationNotChanged()) {
          return true;
        } else {
          return false;
        }
      }
    } else {
      return false;
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    if (source == this.mNetCDFUrlCheckValidButton) {
      this.checkValidationOfNetCDFUrlAndFireResult(true);
    } else if (source == this.mNetCDFUrlTextField) {
      if (this.mNetCDFUrlCheckValidButton.isVisible()) {
        this.checkValidationOfNetCDFUrlAndFireResult(true);
      } else {
        this.fireCheckNetCDFUrlTextField();
      }
    }
  }

  protected void checkValidationOfNetCDFUrlAndFireResult(final boolean showDialog) {
    if (isValidUrlLocation(showDialog)) {
      this.fireAcceptNetCDFUrl(true);
    } else {
      this.fireAcceptNetCDFUrl(false);
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    Object source = e.getSource();
    if (source == this.mTabbedPane) {
      if (this.mTabbedPane.getSelectedComponent().equals(this.mLocalFilePanel)) {
        this.mDataFileNameTextField.requestFocusInWindow();
        this.fireCheckLocalFileTextField();
      } else {
        this.mNetCDFUrlTextField.requestFocusInWindow();
        if (this.mNetCDFUrlCheckValidButton.isVisible() == false) {
          this.fireAcceptNetCDFUrl(true);
        } else {
          if (isValidUrlLocationNotChanged()) {
            this.fireAcceptNetCDFUrl(true);
          } else {
            this.fireAcceptNetCDFUrl(false);
          }
        }
      }
    }
  }

  /** valid url location inputted */
  private String validUrlLocationInputted = "";

  /**
   * Return true if inputted url location is valid and not changed.
   *
   * @return true if inputted url location is valid and not changed
   */
  private boolean isValidUrlLocationNotChanged() {
    if (null == this.validUrlLocationInputted || this.validUrlLocationInputted.equals("")) {
      return false;
    }
    if (this.mNetCDFUrlTextField.getText().trim().equals(this.validUrlLocationInputted)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Validate the url of netCDF and return true if valid or false if not valid.
   *
   * @param showDialog
   * @return true if valid.
   */
  public boolean isValidUrlLocation(final boolean showDialog) {
    boolean valid =
        isValidUrlLocationOfNetCDF(
            this.mNetCDFUrlTextField.getText().trim(), this.mNetCDFUrlTextField, showDialog);

    if (valid == false) {
      this.mNetCDFUrlTextField.requestFocusInWindow();
      return false;
    } else {
      this.validUrlLocationInputted = this.mNetCDFUrlTextField.getText().trim();
      return true;
    }
  }

  /**
   * Return whether NetcdfFile can open the location.
   *
   * @param location
   * @param parentComponent
   * @param showDialog
   * @return true if NetcdfFile can open the location. if false, shows error dialog.
   */
  public static boolean isValidUrlLocationOfNetCDF(
      final String location, final Component parentComponent, final boolean showDialog) {
    try {
      if (NetcdfFiles.canOpen(location)) {
        return true;
      } else {
        if (showDialog) {
          SGUtility.showErrorMessageDialog(
              parentComponent, "Failed to open the NetCDF file.", "Failed to open NetCDF URL");
        }
        return false;
      }
    } catch (NoClassDefFoundError e) {
      if (showDialog) {
        SGUtility.showErrorMessageDialog(
            parentComponent,
            "java.lang.NoClassDefFoundError : " + e.getMessage(),
            "Java class not found");
      }
      return false;
    } catch (Exception e) {
      if (showDialog) {
        SGUtility.showErrorMessageDialog(
            parentComponent, "Failed to open the NetCDF file.", "Failed to open NetCDF URL");
      }
      return false;
    }
  }

  public interface AcceptListener {
    /**
     * Accept the url of netCDF.
     *
     * <p>This is called when netCDF URL location is set or when the netCDF URL radio button
     * actions.
     *
     * @param accept true if url of netCDF is accepted.
     */
    public void acceptNetCDFUrl(final boolean accept);

    /**
     * Check whether the url of netcdf is valid or not.
     *
     * <p>This is called when the netcdf url check valid button is not visible and the netcdf url
     * text field actions.
     */
    public void checkNetCDFUrlTextField();

    /**
     * Check the local file name is valid.
     *
     * <p>This is called when the local file text field gains focus or when the local file radio
     * button actions.
     */
    public void checkLocalFileTextField();
  }

  private List<AcceptListener> listeners = new ArrayList<AcceptListener>();

  public void addAcceptListener(AcceptListener listener) {
    this.listeners.add(listener);
  }

  protected void fireAcceptNetCDFUrl(final boolean accept) {
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).acceptNetCDFUrl(accept);
    }
  }

  protected void fireCheckNetCDFUrlTextField() {
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).checkNetCDFUrlTextField();
    }
  }

  protected void fireCheckLocalFileTextField() {
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).checkLocalFileTextField();
    }
  }

  public static final String PREFIX_HTTP_PROTOCOL = "http";

  public static final String PREFIX_FILE_PROTOCOL = "file:";

  public static final String PREFIX_NODODS_PROTOCOL = "nodods:";

  public static final String PREFIX_SLURP_PROTOCOL = "slurp:";

  /**
   * Return whether specified location is a http, file, nodods or slurp url of netCDF file or not.
   *
   * @param location
   * @return true if location is netcdf url.
   */
  public static boolean isNetcdfUrl(String location) {
    if (location.startsWith(PREFIX_HTTP_PROTOCOL)
        || location.startsWith(PREFIX_FILE_PROTOCOL)
        || location.startsWith(PREFIX_NODODS_PROTOCOL)
        || location.startsWith(PREFIX_SLURP_PROTOCOL)) {
      return true;
    }
    return false;
  }

  /**
   * Set enabled to Url of netCDF panel if text data file is used.
   *
   * @param enabled
   */
  public void setEnabledUrlOfNetCDFPanel(final boolean enabled) {
    this.mTabbedPane.setEnabledAt(this.mTabbedPane.indexOfComponent(this.mNetCDFUrlPanel), enabled);
    this.mTabbedPane.setSelectedComponent(this.mLocalFilePanel);
  }
}

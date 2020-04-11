package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.joda.time.Period;

/**
 * The base class of the property dialogs.
 */
public abstract class SGPropertyDialog extends SGDialog implements
        SGIDrawingElementConstants {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 214371996196142459L;

    /**
     * List of the property setting listeners.
     */
    protected List<SGIPropertyDialogObserver> mPropertyDialogObserverList = new ArrayList<SGIPropertyDialogObserver>();

    public SGPropertyDialog() {
        super();
    }

    public SGPropertyDialog(Dialog owner) {
        super(owner);
    }

    public SGPropertyDialog(Dialog owner, boolean modal) {
        super(owner, modal);
    }

    public SGPropertyDialog(Dialog owner, String title) {
        super(owner, title);
    }

    public SGPropertyDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    public SGPropertyDialog(Frame owner) {
        super(owner);
    }

    public SGPropertyDialog(Frame owner, boolean modal) {
        super(owner, modal);
    }

    public SGPropertyDialog(Frame owner, String title) {
        super(owner, title);
    }

    public SGPropertyDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    /**
     * Initialization method to be called in the constructor of sub classes.
     * This method must be called after the call of the constructor of the super
     * class.
     */
    protected boolean initialize() {

        // OK, Cancel and Preview buttons
        SwingUtilities.invokeLater(new Runnable() {
        	public void run() {
                //
                // add this dialog as an action event listener
                //

                // text field components
                List<JTextField> tList = getTextFieldComponentsList();
                for (int ii = 0; ii < tList.size(); ii++) {
                	JTextField com = tList.get(ii);
                    com.addActionListener(SGPropertyDialog.this);
                }

                getOKButton().addActionListener(SGPropertyDialog.this);
                getCancelButton().addActionListener(SGPropertyDialog.this);
                getPreviewButton().addActionListener(SGPropertyDialog.this);
        	}
        });

        return true;
    }

    /**
     * Called when the escape key is typed.
     *
     */
    protected void onEscKeyTyped() {
        this.onCanceled();
    }

    /**
     *
     */
    protected String getString(final String key) {
        ResourceBundle bundle = ResourceBundle.getBundle("res", Locale.JAPAN);
        // ResourceBundle bundle =
        // ResourceBundle.getBundle("res",Locale.ENGLISH);
        String val = bundle.getString(key);

        return val;
    }


    /**
     * Returns a list of text field components.
     *
     * @return a list of text field components
     */
    public abstract List<JTextField> getTextFieldComponentsList();

    /**
     * Returns a list of text field components to input axis number.
     *
     * @return a list of text field components to input axis number
     */
    public abstract List<SGTextField> getAxisNumberTextFieldList();

    /**
     * Returns a list of spinners.
     *
     * @return a list of spinners
     */
    public abstract List<SGSpinner> getSpinnerList();

    /**
     * Returns a list of JFormattedTextField from spinners.
     *
     * @return a list of JFormattedTextField from spinners
     */
    protected List<JFormattedTextField> getFormattedTextFieldsListFromSpinners() {
        final List<JFormattedTextField> list = new ArrayList<JFormattedTextField>();
        final List<SGSpinner> sList = this.getSpinnerList();
        for (int ii = 0; ii < sList.size(); ii++) {
            SGSpinner spinner = sList.get(ii);
            list.add(spinner.getFormattedTextField());
        }
        return list;
    }

    /**
     * Called when an action event is generated.
     *
     * @param e
     *          an action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
    	super.actionPerformed(e);
        Object source = e.getSource();
        try {
            List comList = this.getTextFieldComponentsList();
            if (source.equals(this.getOKButton()) || comList.contains(source)) {
                this.onOK();
            } else if (source.equals(this.getCancelButton())) {
                this.onCanceled();
            } else if (source.equals(this.getPreviewButton())) {
                this.onPreviewed();
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
            SGUtility.showErrorMessageDialog(this, "Unknown error occurred.\b This dialog will be closed.", "ERROR");
            this.setVisible(false);
            this.clearAllSpinners();
        }
    }

    /**
     * Returns the OK button.
     * @return
     *        the OK button
     */
    protected abstract JButton getOKButton();

    /**
     * Returns the cancel button.
     * @return
     *        the cancel button
     */
    protected abstract JButton getCancelButton();

    /**
     * Returns the preview button.
     * @return
     *        the preview button
     */
    protected abstract JButton getPreviewButton();

    /**
     * Returns the spinner model for line width.
     *
     * @return the spinner model for line width
     */
    public static SpinnerNumberModel getLineWidthSpinnerNumberModel() {
    	return SGUtility.getLineWidthSpinnerNumberModel();
    }

    /**
     * Returns the spinner model for font size.
     *
     * @return the spinner model for font size
     */
    public static SpinnerNumberModel getFontSizeSpinnerNumberModel() {
    	return SGUtility.getFontSizeSpinnerNumberModel();
    }

    /**
     *
     * @param l
     * @return
     */
    public boolean addPropertyDialogObserver(final SGIPropertyDialogObserver l) {
        this.mPropertyDialogObserverList.add(l);
        return true;
    }

    /**
     *
     * @param l
     * @return
     */
    public boolean removePropertyDialogObserver(
            final SGIPropertyDialogObserver l) {
        return this.mPropertyDialogObserverList.remove(l);
    }

    /**
     *
     * @param l
     * @return
     */
    public boolean removeAllPropertyDialogObserver() {
        this.mPropertyDialogObserverList.clear();
        return true;
    }

    /**
     * Called when all changed properties are committed.
     */
    protected boolean onOK() {
        if (this.commit() == false) {
            return false;
        }
        this.setCloseOption(OK_OPTION);
        this.setVisible(false);
        this.clearAllSpinners();

        return true;
    }

    /**
     * Called when all changed properties are canceled.
     */
    protected boolean onCanceled() {
        if (this.cancel() == false) {
            return false;
        }
        this.setCloseOption(CANCEL_OPTION);
        this.setVisible(false);
        this.clearAllSpinners();

        return true;
    }

    /**
     * Called when all changed properties are reflected temporarily to preview.
     */
    protected boolean onPreviewed() {
        return this.preview();
    }

    /**
     *
     *
     */
    protected void clearAllSpinners() {
        List list = this.getSpinnerList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGSpinner sp = (SGSpinner) list.get(ii);
            sp.clearTemporaryValues();
        }
    }

    public static final String NOTIFY_ACTION_COMMIT = "Notify action commit";

    public static final String NOTIFY_ACTION_PREVIEW = "Notify action preview";

    public static final String NOTIFY_ACTION_CANCEL = "Notify action cancel";

    protected String mCommitActionStateMessage = null;

    /**
     *
     * @return
     */
    protected boolean commit() {
        this.mCommitActionStateMessage = NOTIFY_ACTION_COMMIT;
        if (!this.setPropertiesToAllListeners()) {
        	return false;
        }
        for (int ii = 0; ii < this.mPropertyDialogObserverList.size(); ii++) {
            SGIPropertyDialogObserver l = this.mPropertyDialogObserverList.get(ii);
            if (l.commit() == false) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return
     */
    protected boolean cancel() {
        this.mCommitActionStateMessage = NOTIFY_ACTION_CANCEL;
        for (int ii = 0; ii < this.mPropertyDialogObserverList.size(); ii++) {
            SGIPropertyDialogObserver l = this.mPropertyDialogObserverList.get(ii);
            if (l.cancel() == false) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return
     */
    protected boolean preview() {
        this.mCommitActionStateMessage = NOTIFY_ACTION_PREVIEW;
        if (!this.setPropertiesToAllListeners()) {
        	return false;
        }
        for (int ii = 0; ii < this.mPropertyDialogObserverList.size(); ii++) {
            SGIPropertyDialogObserver l = this.mPropertyDialogObserverList.get(ii);
            if (l.preview() == false) {
                return false;
            }
        }
        return true;
    }

    protected boolean setPropertiesToAllListeners() {
        if (!this.checkInputValues()) {
            return false;
        }
        for (int ii = 0; ii < this.mPropertyDialogObserverList.size(); ii++) {
            SGIPropertyDialogObserver l = this.mPropertyDialogObserverList.get(ii);
            if (this.setPropertiesToObserver(l) == false) {
                String msg;
                String err = this.getInputErrorMessage();
                if (err != null) {
                    msg = err;
                } else {
                    msg = DEFAULT_INPUT_ERROR_MESSAGE;
                }
                SGUtility.showIllegalInputErrorMessageDialog(this, msg);
                return false;
            }
        }
    	return true;
    }

    /**
     * Checks the input values.
     *
     * @return true if input values are valid
     */
    protected boolean checkInputValues() {
        if (!this.hasValidInputValues()) {
            // shows a message dialog
            String msg = this.getInputErrorMessage();
            SGUtility.showIllegalInputErrorMessageDialog(this, msg);
            return false;
        }
        return true;
    }

    /**
     * Overrode this method to check the input values.
     *
     * @return true if all input values are valid
     */
    protected boolean hasValidInputValues() {
        boolean ret = true;

        this.mInputErrorComponentNameList.clear();

        // spinners
        List<SGSpinner> sList = this.getSpinnerList();
        for (int ii = 0; ii < sList.size(); ii++) {
            SGSpinner s = sList.get(ii);
            if (s.hasValidValue() == false) {
                this.addInputErrorDescription(s.getDescription());
                ret = false;
            }
        }

        return ret;
    }

    protected boolean hasValidNumber(final SGTextField tf,
    		final Number min, final Number max) {
    	return this.hasValidNumber(tf, true, min, max);
    }
    
    /**
     * Checks whether the text field has valid number type input value.
     *
     * @param tf
     *          the text field
     * @param min
     *          the minimum value for the input value if it exists
     * @param max
     *          the maximum value for the input value if it exists
     * @return true if the input value is valid
     */
    protected boolean hasValidNumber(final SGTextField tf,
    		final boolean addDesc,
    		final Number min, final Number max) {
        boolean valid = true;
        boolean check = false;
        if (tf.isIndeterminate()) {
        	if (tf.getText() != null) {
        		check = true;
        	}
        } else {
        	check = true;
        }
        if (check) {
            final String desc = tf.getDescription();
            Number value = this.getNumber(tf);
        	if (value != null) {
        		if (Double.isNaN(value.doubleValue())) {
        			if (addDesc) {
                    	this.addInputErrorDescription(desc);
        			}
        			valid = false;
        		} else {
            		if (min != null) {
                		if (value.doubleValue() < min.doubleValue()) {
                			if (addDesc) {
                            	this.addInputErrorDescription(desc);
                			}
                    		valid = false;
                		}
            		}
            		if (valid) {
                		if (max != null) {
                			if (value.doubleValue() > max.doubleValue()) {
                    			if (addDesc) {
                                	this.addInputErrorDescription(desc);
                    			}
                        		valid = false;
                			}
                		}
            		}
        		}
        	} else {
    			if (addDesc) {
                	this.addInputErrorDescription(desc);
    			}
        		valid = false;
        	}
        }
        return valid;
    }

    protected boolean hasValidNumber(final SGTextField tf) {
    	return this.hasValidNumber(tf, true);
    }
    
    protected boolean hasValidNumber(final SGTextField tf, final boolean addDesc) {
    	return this.hasValidNumber(tf, addDesc, null, null);
    }

    protected boolean hasValidDate(final SGTextField tf, 
    		final boolean addDesc) {
        boolean valid = true;
        boolean check = false;
        if (tf.isIndeterminate()) {
        	if (tf.getText() != null) {
        		check = true;
        	}
        } else {
        	check = true;
        }
        if (check) {
            SGDate date = this.getDate(tf);
            if (date == null) {
            	if (addDesc) {
                    final String desc = tf.getDescription();
                	this.addInputErrorDescription(desc);
            	}
        		valid = false;
            }
        }
        return valid;
	}

    protected boolean hasValidPeriod(final SGTextField tf,
    		final boolean addDesc) {
        boolean valid = true;
        boolean check = false;
        if (tf.isIndeterminate()) {
        	if (tf.getText() != null) {
        		check = true;
        	}
        } else {
        	check = true;
        }
        if (check) {
        	Period p = this.getPeriod(tf);
            if (p == null) {
            	if (addDesc) {
                    final String desc = tf.getDescription();
                	this.addInputErrorDescription(desc);
            	}
        		valid = false;
            }
        }
        return valid;
	}

    protected SGDate getDate(final JTextComponent com) {
        if (com == null) {
            return null;
        }
        String str = com.getText();
        SGDate date = null;
		try {
			date = new SGDate(str);
		} catch (ParseException e) {
		}
        return date;
    }

    protected Period getPeriod(final JTextComponent com) {
        if (com == null) {
            return null;
        }
        String str = com.getText();
		return SGUtilityText.getPeriod(str);
    }

    private String mInputErrorMessage = "";

    // The list of name of components set erroneous input value.
    private final List<String> mInputErrorComponentNameList = new ArrayList<String>();

    private static final String ERR_MSG_HEADER = "The input values are illegal in the following components:\n";

    /**
     * Returns a text string used as an error message for illegal input.
     *
     * @return
     *         a text string used as an error message for illegal input
     */
    protected String getInputErrorMessage() {
        if (this.mInputErrorComponentNameList.size() != 0) {
            List<String> list = this.mInputErrorComponentNameList;
            StringBuffer sb = new StringBuffer();
            sb.append(ERR_MSG_HEADER);
            for (int ii = 0; ii < list.size(); ii++) {
                String name = list.get(ii);
                sb.append(" - ");
                sb.append(name);
                sb.append('\n');
            }
            return sb.toString();
        }
        return this.mInputErrorMessage;
    }

    /**
     * Set the error message used as an error message on input.
     *
     * @param str
     * @return
     */
    protected boolean addInputErrorDescription(final String name) {
        this.mInputErrorComponentNameList.add(name);
        return true;
    }

    /**
     *
     * @param msg
     * @return
     */
    protected boolean setInputErrorMessage(final String msg) {
        this.mInputErrorMessage = msg;
        return true;
    }

    /**
     *
     */
    protected static final String DEFAULT_INPUT_ERROR_MESSAGE = "The input value is illegal.";

    public static final String ERRMSG_AXIS_VALUE_INVALID = "Axis value is invalid";

    /**
     * Set properties to all observers.
     *
     * @param obs
     * @return
     */
    public abstract boolean setPropertiesToObserver(
            SGIPropertyDialogObserver obs);

    /**
     * Set parameters to the components in the dialog.
     *
     * @return
     */
    public abstract boolean setDialogProperty();

    /**
     *
     */
    public void windowClosing(final WindowEvent e) {
        super.windowClosing(e);
        this.onCanceled();
    }

    /**
     *
     * @param obj
     * @return
     */
    protected boolean setValue(final SGTextField tf, final Object obj) {
        return SGUtility.setDoubleValue(tf, obj);
    }

    /**
     *
     * @param obj
     * @return
     */
    protected boolean setValue(final SGSpinner sp, final Object obj) {
        return SGUtility.setValue(sp, obj);
    }
    
    protected void onDateButtonPressed(SGTextField field) {
    	SGDateInputDialog dg = new SGDateInputDialog(this, true);
    	String str = field.getText();
    	Double d = SGUtilityText.getDouble(str);
    	if (d == null) {
    		return;
    	}
    	dg.setDate(d.doubleValue());
    	dg.setCenter(this);
    	dg.setVisible(true);
    	
    	final int closeOption = dg.getCloseOption();
    	if (closeOption == OK_OPTION) {
    		SGDate date = dg.getDate();
    		field.setText(Double.toString(date.getDateValue()));
    	}
    }

    protected void onPeriodButtonPressed(SGTextField field) {
    	SGPeriodInputDialog dg = new SGPeriodInputDialog(this, true);
    	this.onPeriodButtonPressedCommon(dg, field);
    	final int closeOption = dg.getCloseOption();
    	if (closeOption == OK_OPTION) {
    		Period p = dg.getPeriod();
    		field.setText(p.toString());
    	}
    }

    protected void onPeriodButtonPressedForDoubleValue(SGTextField field) {
    	SGPeriodInputDialog dg = new SGPeriodInputDialog(this, true);
    	this.onPeriodButtonPressedCommon(dg, field);
    	final int closeOption = dg.getCloseOption();
    	if (closeOption == OK_OPTION) {
    		Period p = dg.getPeriod();
    		final double dateValue = SGDateUtility.toApproximateDateValue(p);
    		field.setText(Double.toString(dateValue));
    	}
    }
    
    private void onPeriodButtonPressedCommon(
    		SGPeriodInputDialog dg, SGTextField field) {
    	String str = field.getText();
    	Double d = SGUtilityText.getDouble(str);
    	if (d == null) {
    		return;
    	}
    	dg.setPeriod(d.doubleValue());
    	dg.setCenter(this);
    	dg.setVisible(true);
    }

    protected static void selectAxisPanel(
    		SGITwoAxesDialog dg,
    		SGTwoAxesSelectionPanel axesPanel,
    		List<SGIPropertyDialogObserver> lList) {

    	List<SGITwoAxesHolder> holderList = new ArrayList<SGITwoAxesHolder>();
    	for (SGIPropertyDialogObserver l : lList) {
    		if (l instanceof SGITwoAxesHolder) {
    			holderList.add((SGITwoAxesHolder) l);
    		}
    	}
        final int num = holderList.size();
        Integer[] xAxes = new Integer[num];
        Integer[] yAxes = new Integer[num];
        for (int ii = 0; ii < num; ii++) {
            SGITwoAxesHolder l = holderList.get(ii);
            xAxes[ii] = l.getXAxisLocation();
            yAxes[ii] = l.getYAxisLocation();
        }
        Integer xAxis = (Integer) SGUtility.checkEquality(xAxes);
        Integer yAxis = (Integer) SGUtility.checkEquality(yAxes);
    	axesPanel.setSelectedAxis(xAxis, yAxis);
    }

    protected static void setDateComponentProperties(
    		SGITwoAxesDialog dg,
    		SGTwoAxesSelectionPanel axisPanel,
    		List<SGIPropertyDialogObserver> lList) {

    	List<SGITwoAxesHolder> holderList = new ArrayList<SGITwoAxesHolder>();
    	for (SGIPropertyDialogObserver l : lList) {
    		if (l instanceof SGITwoAxesHolder) {
    			holderList.add((SGITwoAxesHolder) l);
    		}
    	}
        final int num = holderList.size();

        final boolean bx;
    	final int xLocation = axisPanel.getXAxisLocation();
        if (xLocation == -1) {
        	bx = false;
        } else {
        	boolean[] dateXArray = new boolean[num];
        	for (int ii = 0; ii < num; ii++) {
                SGITwoAxesHolder l = holderList.get(ii);
        		dateXArray[ii] = l.getAxisDateMode(xLocation);
        	}
        	Boolean dateX = SGUtility.checkEquality(dateXArray);
        	bx = (dateX != null) ? dateX : false;
        }
		dg.onXAxisDateSelected(bx);
		
		final boolean by;
    	final int yLocation = axisPanel.getYAxisLocation();
		if (yLocation == -1) {
			by = false;
		} else {
	    	Boolean[] dateYArray = new Boolean[num];
	    	for (int ii = 0; ii < num; ii++) {
	            SGITwoAxesHolder l = holderList.get(ii);
	    		dateYArray[ii] = l.getAxisDateMode(yLocation);
	    	}
	    	Boolean dateY = (Boolean) SGUtility.checkEquality(dateYArray);
	    	by = (dateY != null) ? dateY : false;
		}
		dg.onYAxisDateSelected(by);
    }

    protected static void selectAxisPanel(
    		SGISingleAxisDialog dg,
    		SGSingleAxisSelectionPanel axisPanel,
    		List<SGIPropertyDialogObserver> lList) {
    	
    	List<SGISingleAxisHolder> holderList = new ArrayList<SGISingleAxisHolder>();
    	for (SGIPropertyDialogObserver l : lList) {
    		if (l instanceof SGISingleAxisHolder) {
    			holderList.add((SGISingleAxisHolder) l);
    		}
    	}
        final int num = holderList.size();
        Integer[] axes = new Integer[num];
        for (int ii = 0; ii < num; ii++) {
        	SGISingleAxisHolder l = holderList.get(ii);
            axes[ii] = l.getAxisLocation();
        }
        Integer axis = (Integer) SGUtility.checkEquality(axes);
    	axisPanel.setSelectedAxis(axis);
    }

    protected static void setDateComponentProperties(
    		SGISingleAxisDialog dg,
    		SGSingleAxisSelectionPanel axisPanel,
    		List<SGIPropertyDialogObserver> lList) {
    	
    	List<SGISingleAxisHolder> holderList = new ArrayList<SGISingleAxisHolder>();
    	for (SGIPropertyDialogObserver l : lList) {
    		if (l instanceof SGISingleAxisHolder) {
    			holderList.add((SGISingleAxisHolder) l);
    		}
    	}
        final int num = holderList.size();
        final boolean b;
    	final int location = axisPanel.getAxisLocation();
        if (location == -1) {
        	b = false;
        } else {
        	boolean[] dateArray = new boolean[num];
        	for (int ii = 0; ii < num; ii++) {
                SGISingleAxisHolder l = holderList.get(ii);
        		dateArray[ii] = l.getAxisDateMode(location);
        	}
        	Boolean date = SGUtility.checkEquality(dateArray);
        	b = (date != null) ? date : false;
        }
		dg.setAxisDateComponentsEnabled(b);
    }

    protected void addComponentGroupMap(Component comp, 
    		Map<Component, SGComponentGroupElement> map) {
    	map.put(comp, new SGComponentGroupElement(comp));
    }

    protected void initFontFamilyNameComboBox(final JComboBox cb) {
        final String fontNameArray[] = SGUtility.getAvailableFontFamilyNames();
        for (String fontName : fontNameArray) {
            cb.addItem(fontName);
        }
    }
    
    protected void initFontStyleComboBox(final JComboBox cb) {
        final String[] styleNameArray = { FONT_PLAIN, FONT_ITALIC,
                FONT_BOLD, FONT_BOLD_ITALIC };
        for (String fontStyleName : styleNameArray) {
            cb.addItem(fontStyleName);
        }
    }
    
    protected void initDateFormatComboBox(final JComboBox cb) {
        final String[] dateFormatArray = SGIDateConstants.DEFAULT_DATE_DISPLAY_FORMAT_ARRAY;
        for (String format : dateFormatArray) {
        	cb.addItem(format);
        }
        cb.setEditable(true);
    }
}

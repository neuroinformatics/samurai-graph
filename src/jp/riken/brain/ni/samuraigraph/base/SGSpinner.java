package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * An original spinner class.
 */
public class SGSpinner extends JSpinner
	implements SGIConstants, FocusListener, KeyListener, MouseListener {

    // serialVersionUID
    private static final long serialVersionUID = -107749809547053625L;

    // The suffix for cm.
    private static final String SUFFIX_CM = " " + cm;

    // The suffix for mm.
    private static final String SUFFIX_MM = " " + mm;

    // The suffix for pt.
    private static final String SUFFIX_PT = " " + pt;

    // The suffix for inch.
    private static final String SUFFIX_INCH = " " + inch;

    // The suffix for degree.
    private static final String SUFFIX_DEGREE = degree;
    
    // The suffix for percent.
    private static final String SUFFIX_PERCENT = " " + percent;

    // The mode of committing the value as it is.
    protected static final int MODE_DEFAULT = 0;

    // The mode of committing the value after shifted to the step value.
    protected static final int MODE_ON_STEP = 1;

    // The name of this spinner.
    private String mDescription = "";

    // The baseline value.
    private Number mBaselineValue = Double.valueOf(0.0);
    
    // The array of step values
    private double[] mStepArray = null;
    
    /**
     * The default constructor.
     */
    public SGSpinner() {
        super();
    }

    /**
     * The constructor with initialization.
     */
    public SGSpinner(final SpinnerNumberModel model, final String unit,
            final int min, final int max) {
        super();
        this.initProperties(model, unit, min, max);
    }

    /**
     * Initialize the spinner with fraction digits. This method must be called
     * immediately on the creation of this spinner instance.
     * 
     * @param model
     *            Number model of this spinner. Note that the range of this
     *            model is modified appropriately.
     * @param min
     *            minimum fraction digits
     * @param max
     *            maximum fraction digits
     * @param unit
     *            A string added to the number in the spinner.
     */
    public void initProperties(final SpinnerNumberModel model,
            final String unit, final int min, final int max) {

        // check input value
        if (model == null) {
            throw new IllegalArgumentException("model == null");
        }
        if (model.getStepSize().doubleValue() < Double.MIN_VALUE) {
            throw new IllegalArgumentException("too small step value");
        }

        // round off the range
        final int round = - (max + 1);
        final double minValue = SGUtilityNumber.roundOffNumber(((Number) model
                .getMinimum()).doubleValue(), round);
        final double maxValue = SGUtilityNumber.roundOffNumber(((Number) model
                .getMaximum()).doubleValue(), round);
        model.setMinimum(Double.valueOf(minValue));
        model.setMaximum(Double.valueOf(maxValue));

        // set the spinner model
        this.setModel(model);

        // set editor
        this.setEditor(new JSpinner.NumberEditor(this, "0"));

        // set the suffix of unit
        if (unit != null) {
            final String suffix = getSuffix(unit);
            final DecimalFormat df = this.getDecimalFormat();
            df.setPositiveSuffix(suffix);
            df.setNegativeSuffix(suffix);
        }

        // set the property of the formatted-text-field
        final JFormattedTextField ftf = this.getFormattedTextField();
        ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);
        ftf.setHorizontalAlignment(SwingConstants.LEFT);
        // ftf.addPropertyChangeListener(this);
        ftf.addFocusListener(this);
        ftf.addKeyListener(this);
        ftf.addMouseListener(this);

        // set the fraction digits
        this.setMinimumFractionDigits(min);
        this.setMaximumFractionDigits(max);

        // calculate the step values
        this.mStepArray = SGUtilityNumber.calcStepValueSorted(
        		this.getMinimumValue().doubleValue(), 
        		this.getMaximumValue().doubleValue(), 
        		this.mBaselineValue.doubleValue(), 
        		this.getStepValue().doubleValue(), 
        		max + 1);
    }

    /**
     * Sets the description of this spinner.
     * 
     * @param str
     *            the name to be set
     */
    public void setDescription(final String str) {
        if (str == null) {
            throw new IllegalArgumentException("str == null");
        }
        this.mDescription = str;
    }

    /**
     * Returns the description of this spinner.
     * 
     * @return the description of this spinner
     */
    public String getDescription() {
        return this.mDescription;
    }

    /**
     * 
     * @return
     */
    public boolean isEditable() {
        return this.getFormattedTextField().isEditable();
    }

    /**
     * 
     * @param b
     */
    public void setEditable(final boolean b) {
        this.getFormattedTextField().setEditable(b);
    }

    /**
     * 
     * @return
     */
    public JFormattedTextField getFormattedTextField() {
        final JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) this
                .getEditor();
        final JFormattedTextField ftf = editor.getTextField();
        return ftf;
    }

    /**
     * 
     * @return
     */
    public String getText() {
        return this.getFormattedTextField().getText();
    }

    /**
     * 
     * @param str
     */
    public void setText(final String str) {
        this.getFormattedTextField().setText(str);
    }

    //
    // Format
    //

    /**
     * 
     * @return
     */
    public DecimalFormat getDecimalFormat() {
        final JSpinner.NumberEditor editor = (JSpinner.NumberEditor) this
                .getEditor();
        return editor.getFormat();
    }

    // The argument "unit" is one of the constants defined in SGUtilityNumber.
    // Returned value is a string only used in the spinners.
    private static String getSuffix(final String unit) {
        String suffix = null;
        if (unit.equals(SGIConstants.cm)) {
            suffix = SUFFIX_CM;
        } else if (SGIConstants.mm.equals(unit)) {
            suffix = SUFFIX_MM;
        } else if (SGIConstants.pt.equals(unit)) {
            suffix = SUFFIX_PT;
        } else if (SGIConstants.inch.equals(unit)) {
            suffix = SUFFIX_INCH;
        } else if (SGIConstants.degree.equals(unit)) {
            suffix = SUFFIX_DEGREE;
        } else if (percent.equals(unit)) {
            suffix = SUFFIX_PERCENT;
        }
        return suffix;
    }

    /**
     * Returns a string of unit.
     * 
     * @return - the string of unit
     */
    public String getUnit() {
        String suffix = this.getSuffix();
        String unit = null;
        if (SUFFIX_CM.equals(suffix)) {
            unit = SGIConstants.cm;
        } else if (SUFFIX_MM.equals(suffix)) {
            unit = SGIConstants.mm;
        } else if (SUFFIX_INCH.equals(suffix)) {
            unit = SGIConstants.inch;
        } else if (SUFFIX_PT.equals(suffix)) {
            unit = SGIConstants.pt;
        } else if (SUFFIX_DEGREE.equals(suffix)) {
            unit = SGIConstants.degree;
        } else if (SUFFIX_PERCENT.equals(suffix)) {
            unit = percent;
        }
        return unit;
    }

    // Returns the suffix.
    protected String getSuffix() {
        return this.getDecimalFormat().getPositiveSuffix();
    }

    /**
     * 
     * @return
     */
    public int getMinimumFractionDigits() {
        return this.getDecimalFormat().getMinimumFractionDigits();
    }

    /**
     * 
     * @return
     */
    public int getMaximumFractionDigits() {
        return this.getDecimalFormat().getMaximumFractionDigits();
    }

    /**
     * 
     * @param newValue
     */
    private void setMinimumFractionDigits(final int newValue) {
        this.getDecimalFormat().setMinimumFractionDigits(newValue);
    }

    /**
     * 
     * @param newValue
     */
    private void setMaximumFractionDigits(final int newValue) {
        this.getDecimalFormat().setMaximumFractionDigits(newValue);
    }

    /**
     * 
     * @return
     */
    private int getDigitForRoundingOut() {
        return -this.getMaximumFractionDigits() - 1;
    }

    // Returns whether this spinner treat only integral numbers.
    private boolean isInteger() {
        final int min = this.getMinimumFractionDigits();
        final int max = this.getMaximumFractionDigits();
        return (min == 0 && max == 0);
    }

    //
    // Number model
    //

    // Only casts the SpinnerModel and returns.
    private SpinnerNumberModel getSpinnerNumberModel() {
        return (SpinnerNumberModel) this.getModel();
    }

    /**
     * Returns the minimum value.
     * 
     * @return the minimum value
     */
    public Number getMinimumValue() {
        return (Number) this.getSpinnerNumberModel().getMinimum();
    }

    /**
     * Returns the maximum value.
     * 
     * @return the maximum value
     */
    public Number getMaximumValue() {
        return (Number) this.getSpinnerNumberModel().getMaximum();
    }

    /**
     * Returns the step value.
     * 
     * @return the step value
     */
    public Number getStepValue() {
        return this.getSpinnerNumberModel().getStepSize();
    }
    
    /**
     * Returns the baseline value.
     * 
     * @return the baseline value
     */
    public Number getBaselineValue() {
    	return this.mBaselineValue;
    }

    private Number getValueOnStepInside(final double v1, final double v2) {
        double smaller;
        double larger;
        if (v1 < v2) {
            smaller = v1;
            larger = v2;
        } else if (v2 < v1) {
            smaller = v2;
            larger = v1;
        } else {
            return null;
        }
        
        for (int ii = 0; ii < this.mStepArray.length; ii++) {
        	final double value = this.mStepArray[ii];
        	if (smaller < value && value < larger) {
        		return Double.valueOf(value);
        	}
        }
        
        return null;
/*
        final double min = this.getMinimumValue().doubleValue();
        // final double max = this.getMaximumValue().doubleValue();
        final double step = this.getStepValue().doubleValue();

        Number num = null;
        int cnt = 0;
        final int digit = this.getDigitForRoundingOut();
        while (true) {
            final double valueOld = min + cnt * step;
            final double value = SGUtilityNumber
                    .roundOffNumber(valueOld, digit);
            if (smaller < value) {
                if (value < larger) {
                    num = Double.valueOf(value);
                    break;
                }
                break;
            }
            cnt++;
        }

        return num;
*/        
    }

    /**
     * An overridden method of JSpinner class. Commits the value to the
     * <code>SpinnerModel</code> after rounding up the value.
     */
    public void commitEdit() throws ParseException {
        this.commit(MODE_ON_STEP);

        super.commitEdit();

        // record the temporary string after committed
        this.mTempString = this.getText();
        // System.out.println("c:" + mTempString);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String str = getText();
                if (mTempString != null && str.equals(mTempString) == false) {
                    String unit = getUnit();
                    if (isInteger() == false) {
                        String strOld = SGUtilityText.removeSuffix(mTempString,
                                unit);
                        String strNew = SGUtilityText.removeSuffix(str, unit);
                        Double valueOld = Double.valueOf(strOld);
                        Double valueNew = Double.valueOf(strNew);
                        // System.out.println("d:" + strOld);
                        // System.out.println("e:" + strNew);
                        // get the step value between two values
                        Number num = getValueOnStepInside(valueOld
                                .doubleValue(), valueNew.doubleValue());
                        if (num == null) {
                            num = valueNew;
                        }
                        // System.out.println("f:" + num);
                        // System.out.println();
                        // set new value to the spinner
                        setValue(num);
                    }
                }

                // clear the temporary string
                mTempString = null;

                // adjust the caret position
                SwingUtilities.invokeLater(new CaretPositionAdjuster());
            }
        });
    }

    /**
     * Returns whether currently set value is valid. In this class, only a value
     * interpreted as a number is permitted.
     * 
     * @return true if the current value is valid
     */
    public boolean hasValidValue() {
        Number num = this.getNumber();
        if (num == null) {
            String str = this.getText();
            if ("".equals(str) == false) {
                return false;
            }
        }
        return true;
    }

    // a temporary string
    private String mTempString = null;

    /**
     * Clear the temporary String object.
     */
    void clearTemporaryValues() {
        this.mTempString = null;
    }

    /**
     * Returns a number if it is interpreted as a number.
     * 
     * @return a number if this spinner has a number object, and otherwise null
     */
    public Number getNumber() {
        try {
            this.commitEditByDefault();
        } catch (Exception ex) {
            return null;
        }
        Object obj = this.getValue();
        return (Number) obj;
    }

    /**
     * Commit the value as it is.
     */
    public void commitEditByDefault() throws ParseException {
        this.commit(MODE_DEFAULT);
        super.commitEdit();
    }

    /**
     * Commit the value in a given commitment mode.
     * 
     * @param mode
     *            the commitment mode
     */
    private void commit(final int mode) throws ParseException {
        final JFormattedTextField ftf = this.getFormattedTextField();
        // System.out.println("0:" + ftf.getText());

        // parse the input string
        String parsedStr = this.parseString(ftf.getText(), mode);
        // System.out.println("a:" + parsedStr);
        if (parsedStr != null) {

            // set the parsed string to the formatted text field
            ftf.setText(parsedStr);

            // commit the parsed text
            try {
                ftf.commitEdit();
                // System.out.println("b:" + ftf.getText());
            } catch (ParseException e) {
                // e.printStackTrace();
                throw e;
            }
        } else {
            // parse failed
            throw new ParseException("parse failed", 0);
        }
    }

    /**
     * Parse the string in a given mode.
     * 
     * @param input
     *            a string to be parsed
     * @param mode
     *            mode
     * @return parsed string when succeeded, otherwise null
     */
    protected String parseString(final String input, final int mode) {
    	Number num = this.parseStringToNumber(input, mode);
    	if (num == null) {
    		return null;
    	}
    	StringBuffer sb = new StringBuffer();
    	sb.append(num);
        final String unit = this.getUnit();
        if (unit != null) {
        	String suffix = getSuffix(unit);
        	sb.append(suffix);
        }
        return sb.toString();
    }

    protected Number parseStringToNumber(final String input, final int mode) {
        if (input == null) {
            return null;
        }
        if (input.length() == 0) {
            return null;
        }

        final String unit = this.getUnit();

        // with no unit
        if (unit == null) {
            // parse the input string directly
            Number num = SGUtilityText.getDouble(input);
            if (num == null) {
                return null;
            }

            final float fValue = num.floatValue();
            if (this.isInteger()) {
                int value = (int) Math.rint(fValue);
                final int min = this.getMinimumValue().intValue();
                final int max = this.getMaximumValue().intValue();
                if (value < min) {
                    value = min;
                }
                if (value > max) {
                    value = max;
                }
                return value;
            } else {
            	float value = fValue;
                final float min = this.getMinimumValue().floatValue();
                final float max = this.getMaximumValue().floatValue();
                if (value < min) {
                    value = min;
                }
                if (value > max) {
                    value = max;
                }
                return value;
            }
        }

        // parse the input string directly, because only the number can be input
        String str = null;
        {
            Number num = null;
            if (this.isInteger()) {
                num = SGUtilityText.getInteger(input);
            } else {
                num = SGUtilityText.getDouble(input);
            }

            // just a number is set
            if (num != null) {
                str = num.toString() + getSuffix(unit);
            } else {
                str = input;
            }
        }

        // in case of the unit of length
        String strValue = null;
        if (SGUtilityText.isLengthUnit(unit)) {
            // convert the string to the number in the default unit
            strValue = SGUtilityText.convertString(str, unit);
        } else {
            // remove the unit
            strValue = SGUtilityText.removeSuffix(str, unit);
        }
        if (strValue == null) {
            return null;
        }

        // check the range
        if (this.isInteger()) {
            int value = Integer.valueOf(strValue.trim()).intValue();
            final int min = this.getMinimumValue().intValue();
            final int max = this.getMaximumValue().intValue();
            if (value < min) {
                value = min;
            }
            if (value > max) {
                value = max;
            }
            return value;
        }

        // get the value
        double value = SGUtilityText.getDouble(strValue).doubleValue();
        final double min = this.getMinimumValue().doubleValue();
        final double max = this.getMaximumValue().doubleValue();
        if (value < min) {
            value = min;
        }
        if (value > max) {
            value = max;
        }

        // round up the number
        double valueNew = 0.0;
        switch (mode) {
        case MODE_DEFAULT:
            valueNew = SGUtilityNumber.roundOffNumber(value, this
                    .getDigitForRoundingOut());
            break;
        case MODE_ON_STEP:
            valueNew = value;
            break;
        default:
            throw new IllegalArgumentException();
        }

        return valueNew;
    }

    /**
     * Get suffix from the current txt. If spaces between the value and the
     * suffix, they are attached to the head of the returned value.
     */
    private String getTextSuffix() {
        String txt = this.getText();
        String suffix = null;
        String[] sArray = { SGIConstants.cm, SGIConstants.mm, SGIConstants.pt,
                SGIConstants.inch, SGIConstants.degree };
        for (int ii = 0; ii < sArray.length; ii++) {
            if (txt.endsWith(sArray[ii])) {
                suffix = sArray[ii];

                // count the number of spaces and attach them
                // to the suffix
                int suffixStart = txt.length() - suffix.length() - 1;
                if (suffixStart > 0) {
                    int spaceCnt = 0;
                    for (int jj = suffixStart; jj >= 0; jj--) {
                        char c = txt.charAt(jj);
                        if (c == ' ') {
                            spaceCnt++;
                        } else {
                            break;
                        }
                    }
                    StringBuffer sb = new StringBuffer();
                    for (int jj = 0; jj < spaceCnt; jj++) {
                        sb.append(' ');
                    }
                    sb.append(suffix);
                    suffix = sb.toString();
                }
                break;
            }
        }
        return suffix;
    }

    /**
     * Called when focus is gained by this spinner.
     */
    public void focusGained(FocusEvent e) {
        // adjust the caret position
        SwingUtilities.invokeLater(new CaretPositionAdjuster());
    }

    /**
     * Called when focus is lost from this spinner.
     */
    public void focusLost(FocusEvent e) {
    }

    /**
     * A thread class to adjust the position of caret.
     * 
     */
    class CaretPositionAdjuster implements Runnable {
        CaretPositionAdjuster() {
        }

        public void run() {
            JFormattedTextField ftf = getFormattedTextField();
            String str = ftf.getText();
            
            // returns when this spinner has an empty string
            if (str.length() == 0) {
                return;
            }
            
            int pos;
            if (mPressedPoint != null) {
                // when focus is gained by the mouse press, set the location
                // of the caret to the pressed point
                Font f = getFont();
                FontRenderContext frc = new FontRenderContext(null, false, false);
                TextLayout tl = new TextLayout(str, f, frc);
                TextHitInfo info = tl.hitTestChar(mPressedPoint.x, mPressedPoint.y);
                pos = info.getInsertionIndex();
                mPressedPoint = null;
            } else {
                // otherwise, set the location of the caret to the end of 
                // the text string of a number
                String suffix = getTextSuffix();
                int len = 0;
                if (suffix != null) {
                    len = suffix.length();
                }
                pos = str.length() - len;
            }
            ftf.setCaretPosition(pos);
        }
    }

    /**
     * Called when the key is pressed.
     * @param e
     *          key event
     */
    public void keyPressed(KeyEvent e) {
    }

    /**
     * Called when the key is released.
     * @param e
     *          key event
     */
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Called when the key is typed.
     * @param e
     *          key event
     */
    public void keyTyped(KeyEvent e) {
        final char c = e.getKeyChar();
        if (c == KeyEvent.VK_ENTER) {
            // when a key is typed, notify to the listeners
            JFormattedTextField ftf = this.getFormattedTextField();
            ActionEvent ae = new ActionEvent(ftf, 0, "Enter");
            ActionListener[] al = ftf.getActionListeners();
            for (int ii = 0; ii < al.length; ii++) {
                al[ii].actionPerformed(ae);
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    /**
     * The location at which mouse button is pressed.
     */
    private Point mPressedPoint = null;

    public void mousePressed(MouseEvent e) {
        this.mPressedPoint = e.getPoint();
    }

    public void mouseReleased(MouseEvent e) {
    }

}

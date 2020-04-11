package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;

import jp.riken.brain.ni.samuraigraph.base.SGCSVTokenizer.Token;

/**
 * A combo box to display the magnification.
 * 
 */
public class SGZoomComboBox extends JComboBox implements SGIRootObjectConstants {

    /**
     * 
     */
    private static final long serialVersionUID = 4844389430288943687L;

    private static final String PERCENT = "%";

    /**
     * The default constructor.
     */
    public SGZoomComboBox() {
        super();
        this.init();
    }

    // initialize the combo box
    private void init() {

        // add items
        String[] array = MAGNIFICATION_STRING_ARRAY;
        for (int ii = 0; ii < array.length; ii++) {
            this.addItem(array[ii] + PERCENT);
        }
        this.addItem(AUTO_ZOOM_IN_COMBO_BOX);

        this.setEditable(true);
        this.setToolTipText(TIP_ZOOM);
        this.setFont(new java.awt.Font("Dialog", 1, 12));

        Dimension dim;
        String laf = SGUtility.getLookAndFeelID();
        if (!LAF_AQUA.equals(laf) && !LAF_WINDOWS.equals(laf)) {
            dim = new Dimension(120, 25);
            this.setBackground(Color.WHITE);
            this.setBorder(BorderFactory.createLoweredBevelBorder());
        } else {
            dim = new Dimension(120, 22);
        }
        this.setPreferredSize(dim);
        this.setMaximumSize(this.getPreferredSize());
        this.setMinimumSize(this.getPreferredSize());

        this.addActionListener(this);

        this.setText(Integer.valueOf(DEFAULT_ZOOM));
    }

    /**
     * 
     * @param e -
     *            action event
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);

        Object source = e.getSource();

        if (source.equals(this)) {
            if (!this.mExternalFlag) {
                String str = (String) this.getSelectedItem();
                final boolean auto = (str.equals(AUTO_ZOOM_IN_COMBO_BOX));
                this.setAutoZoom(auto);
                if (!auto) {
                    this.onZoomed();
                }
            }
        }

    }

    /**
     * Current magnification value.
     */
    private float mMagnification = DEFAULT_ZOOM;

    // set auto zoom flag to all zoomable objects
    private void setAutoZoom(final boolean b) {
        ArrayList list = this.mZoomableList;
        for (int ii = 0; ii < list.size(); ii++) {
            SGIZoomable l = (SGIZoomable) list.get(ii);
            l.setAutoZoom(b);
        }
    }

    /**
     * Set the value to the combo box.
     * 
     * @param num -
     *            the magnification
     */
    public void setZoomValue(Number num) {
        if (num == null) {
            throw new IllegalArgumentException("num==null");
        }

        // set the flag true
        this.mExternalFlag = true;

        // set the text
        this.setText(num);

        // set the flag false
        this.mExternalFlag = false;
    }

    // a flag to controll the event handling
    private boolean mExternalFlag = false;

    /**
     * Called in action event handler method.
     */
    private void onZoomed() {

        // get the selected value
        String str = (String) this.getSelectedItem();

        // current magnification value
        Number present = Integer.valueOf((int) (this.mMagnification * 100.0f));

        // parse the string
        Number num = SGUtilityText.getFloat(str);
        if (num == null) {
            num = this.parse(str);

            // when failed to parse, set the current value
            if (num == null) {
                num = present;
            }
        }

        // check the range
        float cl = num.floatValue();
        Number numNew;
        if (cl < 0.0) {
            numNew = present;
        } else {
            if (cl < MIN_MAGNIFICATION_VALUE) {
                numNew = Integer.valueOf(MIN_MAGNIFICATION_VALUE);
            } else if (cl > MAX_MAGNIFICATION_VALUE) {
                numNew = Integer.valueOf(MAX_MAGNIFICATION_VALUE);
            } else {
                numNew = Integer.valueOf((int) cl);
            }
        }

        // set the text
        this.setText(numNew);

        // set current value
        this.mMagnification = numNew.floatValue() / 100.0f;

        // zoom all zoomable objects
        this.zoom(this.mMagnification);

    }

    // parse the text
    private Float parse(String text) {
        Float num = null;
        if (text.endsWith(PERCENT)) {
            String str = text.substring(0, text.length() - 1);
            ArrayList list = new ArrayList();
            // str is not read from data file
            if (SGUtilityText.tokenize(str, list, false) == false) {
                return null;
            }

            if (list.size() != 1) {
                return null;
            }

            Token token = (Token) list.get(0);
            num = SGUtilityText.getFloat(token.getString());
        }

        return num;
    }

    // set text to the combo box
    private void setText(final Number num) {
        String text = num.toString();
        text += PERCENT;
        this.setSelectedItem(text);
    }

    /**
     * The list of zoomable objects.
     */
    private final ArrayList mZoomableList = new ArrayList();

    /**
     * Add a zoomable object.
     * 
     * @param obj -
     *            a zoomable object
     */
    public void addZoomable(SGIZoomable obj) {
        this.mZoomableList.add(obj);
    }

    /**
     * Remove a zoomable object.
     * 
     * @param obj -
     *            a zoomable object
     */
    public void removeZoomable(SGIZoomable obj) {
        this.mZoomableList.remove(obj);
    }

    // zoom all zoomable objects.
    private void zoom(final float mag) {
        ArrayList list = this.mZoomableList;
        for (int ii = 0; ii < list.size(); ii++) {
            SGIZoomable l = (SGIZoomable) list.get(ii);
            l.zoom(mag);
        }
    }

}

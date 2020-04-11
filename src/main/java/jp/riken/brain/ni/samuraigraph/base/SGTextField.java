package jp.riken.brain.ni.samuraigraph.base;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * The text field which has the "indeterminate" property. If this object is in
 * the "indeterminate" state, it displays an empty string.
 * 
 */
public class SGTextField extends JTextField {
    /**
     * 
     */
    private static final long serialVersionUID = 5980559626327986902L;

    // The name of this text field.
    private String mName = "";

    /**
     * 
     */
    private boolean mIndeterminate = false;

    /**
     * 
     */
    public SGTextField() {
        super();
        this.init();
    }

    /**
     * @param columns
     */
    public SGTextField(int columns) {
        super(columns);
        this.init();
    }

    /**
     * @param text
     */
    public SGTextField(String text) {
        super(text);
        this.init();
    }

    /**
     * @param text
     * @param columns
     */
    public SGTextField(String text, int columns) {
        super(text, columns);
        this.init();
    }

    /**
     * @param doc
     * @param text
     * @param columns
     */
    public SGTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
        this.init();
    }

    /**
     * Initialize this text field.
     *
     */
    private void init() {
    }

    /**
     * 
     * @return
     */
    public boolean isIndeterminate() {
        return this.mIndeterminate;
    }

    /**
     * 
     * @param b
     * @return
     */
    private boolean setIndeterminate(final boolean b) {
        this.mIndeterminate = b;
        return true;
    }

    /**
     * 
     */
    public String getDescription() {
        return this.mName;
    }

    /**
     * 
     */
    public void setDescription(final String str) {
        if (str == null) {
            throw new IllegalArgumentException("str==null");
        }
        this.mName = str;
    }

    /**
     * Sets the text string.
     * 
     * @param str
     *           a text string to set
     */
    public void setText(final String str) {
        String str2;
        if (str == null) {
            this.setIndeterminate(true);
            str2 = "";
        } else {
            this.setIndeterminate(false);
            str2 = str;
        }
        super.setText(str2);
    }

    /**
     * Returns the input text string.
     * 
     * @return the input text string
     */
    public String getText() {
        String str = super.getText();
        if (this.isIndeterminate()) {
            if ("".equals(str)) {
                return null;
            }
        }
        return str;
    }

    /**
     * Returns whether this text field has valid text string.
     * 
     * @return true if this text field has valid text string
     */
    public boolean hasValidText() {
        String text = this.getText();
        if (text == null) {
            if (this.isIndeterminate() == false) {
                return false;
            }
        } else {
//            if (text.equals("")) {
//                return false;
//            }
        	if (SGUtilityText.isValidString(text) == false) {
        		return false;
        	}
        }

        return true;
    }

}

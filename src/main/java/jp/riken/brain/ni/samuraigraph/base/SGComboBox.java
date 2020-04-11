package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Dimension;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * The original combo box class.
 * 
 */
public class SGComboBox extends JComboBox {

    private static final long serialVersionUID = -1508926886575260391L;

    public SGComboBox() {
        super();
        this.init();
    }

    public SGComboBox(ComboBoxModel aModel) {
        super(aModel);
        this.init();
    }

    public SGComboBox(Object[] items) {
        super(items);
        this.init();
    }
    
    private void init() {
    }

	private boolean mLayingOut = false;

	@Override
	public void doLayout() {
		try {
			this.mLayingOut = true;
			super.doLayout();
		} finally {
			this.mLayingOut = false;
		}
	}

	@Override
	public Dimension getSize() {
		Dimension dim = super.getSize();
		if (!this.mLayingOut) {
			dim.width = Math.max(dim.width, this.getPreferredSize().width);
		}
		return dim;
	}
    
    @Override
    public void setSelectedItem(Object obj) {
    	super.setSelectedItem(obj);
    	this.updateToolTipText(obj);
    }
    
    @Override
    public void setSelectedIndex(final int anIndex) {
    	super.setSelectedIndex(anIndex);
    	Object obj = this.getItemAt(anIndex);
    	this.updateToolTipText(obj);
    }
    
    @Override
    public void removeAllItems() {
    	super.removeAllItems();
    	this.updateToolTipText(null);
    }
    
    private void updateToolTipText(Object obj) {
        // updates the tool tip text
    	String text = null;
    	if (obj != null) {
    		String str = obj.toString();
    		boolean bSpace = true;
    		char[] cArray = str.toCharArray();
    		for (int ii = 0; ii < cArray.length; ii++) {
    			if (!Character.isSpaceChar(cArray[ii])) {
    				bSpace = false;
    				break;
    			}
    		}
    		if (!bSpace) {
        		text = str;
    		}
    	}
    	this.setToolTipText(text);
    }
    
    private String mName = "";
    
    public String getDescription() {
        return this.mName;
    }

    public void setDescription(final String str) {
        if (str == null) {
            throw new IllegalArgumentException("str==null");
        }
        this.mName = str;
    }

}

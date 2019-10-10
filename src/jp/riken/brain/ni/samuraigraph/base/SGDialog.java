package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

/**
 * The base class of dialog.
 */
public abstract class SGDialog extends JDialog implements ActionListener,
        WindowListener, ComponentListener, FocusListener, ChangeListener, SGIConstants {
	
    // serialVersionUID
    private static final long serialVersionUID = 5190851306204747759L;

    /**
     * Virtual bounds.
     */
	private static Rectangle virtualBounds = null;

    /**
     * Text for the OK button.
     */
    public static final String OK_BUTTON_TEXT = "OK";

    /**
     * Text for the Cancel button.
     */
    public static final String CANCEL_BUTTON_TEXT = "Cancel";
    
    /**
     * Text for the Cancel button.
     */
    public static final String PREVIEW_BUTTON_TEXT = "Preview";

    /**
     * Text for the Yes button.
     */
    public static final String YES_BUTTON_TEXT = "Yes";

    /**
     * Text for the No button.
     */
    public static final String NO_BUTTON_TEXT = "No";

    /**
     * Text for the Previous button.
     */
    public static final String PREVIOUS_BUTTON_TEXT = "<Prev";

    /**
     * Text for the Next button.
     */
    public static final String NEXT_BUTTON_TEXT = "Next>";

    // A list of action listeners.
    private ArrayList mActionListenerList = new ArrayList();

    // Closed option of the dialog.
    private int mCloseOption = -1;

    /**
     * Creates a modeless dialog.
     */
    public SGDialog() {
        super();
        this.initialize();
    }

    /**
     * Creates a modal dialog with given owner.
     * 
     * @param owner -
     *            the owner of this dialog
     */
    public SGDialog(Dialog owner) {
        super(owner);
        this.initialize();
    }

    /**
     * Creates a modal or modeless dialog with given owner.
     * 
     * @param owner -
     *            the owner of this dialog
     * @param modal -
     *            true for a modal dialog
     */
    public SGDialog(Dialog owner, boolean modal) {
        super(owner, modal);
        this.initialize();
    }

    /**
     * Creates a modal dialog with given owner and the title.
     * 
     * @param owner -
     *            the owner of this dialog
     * @param title -
     *            the title of this dialog
     */
    public SGDialog(Dialog owner, String title) {
        super(owner, title);
        this.initialize();
    }

    /**
     * Creates a dialog with given owner and title.
     * 
     * @param owner -
     *            the owner of this dialog
     * @param title -
     *            the title of this dialog
     * @param modal -
     *            true for a modal dialog
     */
    public SGDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        this.initialize();
    }

    /**
     * Creates a dialog with given owner, title and GraphicsConfiguration.
     * 
     * @param owner -
     *            the owner of this dialog
     * @param modal -
     *            true for a modal dialog
     * @param title -
     *            the title of this dialog
     * @param gc -
     *            graphics configuration
     */
    public SGDialog(Dialog owner, String title, boolean modal,
            GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        this.initialize();
    }

    /**
     * Creates a modal dialog with given owner.
     * 
     * @param owner -
     *            the owner of this dialog
     */
    public SGDialog(Frame owner) {
        super(owner);
        this.initialize();
    }

    /**
     * Creates a modal or modeless dialog with given owner.
     * 
     * @param owner -
     *            the owner of this dialog
     * @param modal -
     *            true for a modal dialog
     */
    public SGDialog(Frame owner, boolean modal) {
        super(owner, modal);
        this.initialize();
    }

    /**
     * Creates a modal dialog with given owner and the title.
     * 
     * @param owner -
     *            the owner of this dialog
     * @param title -
     *            the title of this dialog
     */
    public SGDialog(Frame owner, String title) {
        super(owner, title);
        this.initialize();
    }

    /**
     * Creates a dialog with given owner and title.
     * 
     * @param owner -
     *            the owner of this dialog
     * @param title -
     *            the title of this dialog
     * @param modal -
     *            true for a modal dialog
     */
    public SGDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        this.initialize();
    }

    /**
     * Creates a dialog with given owner, title and GraphicsConfiguration.
     * 
     * @param owner -
     *            the owner of this dialog
     * @param modal -
     *            true for a modal dialog
     * @param title -
     *            the title of this dialog
     * @param gc -
     *            graphics configuration
     */
    public SGDialog(Frame owner, String title, boolean modal,
            GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        this.initialize();
    }

    // initialization method
    private void initialize() {
        // set unresizable
        this.setResizable(false);

        // add as a listener
        this.addComponentListener(this);
        this.addWindowListener(this);
        
        // cancel all changes and close this dialog when escape key is pressed
        AbstractAction act = new AbstractAction("") {
            private static final long serialVersionUID = -2720225923746562702L;
            public void actionPerformed(ActionEvent e) {
                onEscKeyTyped();
            }
        };
        InputMap imap = this.getRootPane().getInputMap(
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close-it");
        this.getRootPane().getActionMap().put("close-it", act);
        
        // creates a color dialog
        this.mColorDialog = new SGColorDialog(this);

        // set this as a ChangeListener of the color selection model
        JColorChooser cc = this.mColorDialog.getColorChooser();
        ColorSelectionModel cModel = cc.getSelectionModel();
        cModel.addChangeListener(this);

        // OK, Cancel and Preview buttons
        SwingUtilities.invokeLater(new Runnable() {
        	public void run() {
                List<SGColorSelectionButton> cList = getColorSelectionButtonsList();
                for (int ii = 0; ii < cList.size(); ii++) {
                    SGColorSelectionButton btn = cList.get(ii);
                    
                    // adds a focus event listener
                    btn.addFocusListener(SGDialog.this);
                    
                    // adds an action event listener
                    btn.addActionListener(SGDialog.this);
                }
        	}
        });

    }
    
    /**
     * Called when the escape key is typed.
     *
     */
    protected abstract void onEscKeyTyped();
    

    public void componentShown(final ComponentEvent e) {
    }

    public void componentHidden(final ComponentEvent e) {
    }

    public void componentMoved(final ComponentEvent e) {
        Window wnd = this.getOwner();
        if (wnd != null)
            wnd.repaint();
    }

    public void componentResized(final ComponentEvent e) {
    }

    public void windowActivated(final WindowEvent e) {
	repaint();
    }

    public void windowDeactivated(final WindowEvent e) {
	repaint();
    }

    public void windowIconified(final WindowEvent e) {
    }

    public void windowDeiconified(final WindowEvent e) {
    }

    public void windowOpened(final WindowEvent e) {
    }

    public void windowClosed(final WindowEvent e) {
    }

    /**
     * Overridden to set the canceled-option to the closed option.
     */
    public void windowClosing(final WindowEvent e) {
        this.mCloseOption = CANCEL_OPTION;
    }

    /**
     * Add an action listener.
     */
    public void addActionListener(final ActionListener l) {
        ArrayList list = this.mActionListenerList;
        for (int ii = 0; ii < list.size(); ii++) {
            final ActionListener el = (ActionListener) list.get(ii);
            if (el.equals(l)) {
                return;
            }
        }
        list.add(l);
    }

    /**
     * Remove an action listener.
     */
    public void removeActionListener(ActionListener l) {
        ArrayList list = this.mActionListenerList;
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            final ActionListener el = (ActionListener) list.get(ii);
            if (el.equals(l)) {
                list.remove(l);
            }
        }
    }

    /**
     * Notify the given command to the action listeners.
     * 
     * @param command -
     *            the command to notify to the listeners
     */
    protected void notifyToListener(final String command) {
        ArrayList list = this.mActionListenerList;
        for (int ii = 0; ii < list.size(); ii++) {
            final ActionListener el = (ActionListener) list.get(ii);
            el.actionPerformed(this.getActionEvent(command));
        }
    }

    // Creates an ActionEvent object with given command.
    private ActionEvent getActionEvent(final String command) {
        return new ActionEvent(this, 0, command);
    }

    /**
     * Set the location of this dialog as the center of this dialog is equal to
     * the center of the given window.
     * 
     * @param wnd -
     *            a window to which the location of the center of this dialog is
     *            set
     */
    public void setCenter(final Window wnd) {
        SGUtility.setCenter(this, wnd);
    }

    /**
     * Parse the text string in the given text component, and returns a Number
     * object if it is parsed as number.
     * 
     * @param com -
     *            text component
     * @return Number object if parsing succeeds, otherwise null
     */
    protected Number getNumber(final JTextComponent com) {
        if (com == null) {
            return null;
        }
        String str = com.getText();
        Number num = SGUtilityText.getDouble(str);
        return num;
    }

    /**
     * Returns the closed option.
     * 
     * @return the closed option
     */
    public int getCloseOption() {
        return this.mCloseOption;
    }

    /**
     * Set the closed option.
     * 
     * @param num
     *           the closed option to set
     */
    protected void setCloseOption(final int num) {
        this.mCloseOption = num;
    }
    
//    /**
//     * Sets the visibility of this dialog with the parent window.
//     * 
//     * @param b
//     *          true to set visible
//     * @param wnd
//     *           the parent window
//     */
//    public void setVisible(final boolean b, final SGDrawingWindow wnd) {
//    	if (b && this.isModal()) {
//    		wnd.setModalDialogShown(true);
//    	}
//        this.setVisible(b);
//    	if (b && this.isModal()) {
//    		wnd.setModalDialogShown(false);
//    	}
//    }

    /**
     * Sets the visibility of this dialog with the parent window.
     * 
     * @param b
     *          true to set visible
     * @param wnd
     *           the parent window
     */
    public void setVisible(final boolean b) {
    	if (b) {
            this.adjustLocation();
    	}
    	
        if (!b) {
            if (this.mColorDialog != null) {
                this.mColorDialog.setVisible(false);
            }
        }

    	Window owner = this.getOwner();
    	if (owner instanceof SGDrawingWindow) {
    		SGDrawingWindow wnd = (SGDrawingWindow) owner;
        	if (b && this.isModal()) {
        		wnd.setModalDialogShown(true);
        	}
            super.setVisible(b);
        	if (b && this.isModal()) {
        		wnd.setModalDialogShown(false);
        	}
    	} else {
    		super.setVisible(b);
    	}
    }

    private void adjustLocation() {
    	Rectangle bounds = getVirtualBounds();
        int x = this.getX();
        int y = this.getY();
        final int width = this.getWidth();
        final int height = this.getHeight();
        if (this.getX() < 0) {
        	x = 0;
        } else if (this.getX() + width > bounds.width) {
        	x = bounds.width - this.getWidth();
        }
        if (this.getY() < 0) {
        	y = 0;
        } else if (this.getY() + height > bounds.height) {
        	y = bounds.height - height;
        }
    	this.setLocation(x, y);
    }

    /**
     * Returns the owner window.
     * @return
     *         the owner window
     */
    public SGDrawingWindow getOwnerWindow() {
        SGDrawingWindow wnd = null;
        Window cur = this.getOwner();
        while (true) {
            if (cur == null) {
            	break;
            }
            if (cur instanceof SGDrawingWindow) {
                wnd = (SGDrawingWindow) cur;
                break;
            } else {
                cur = cur.getOwner();
            }
        }
        return wnd;
    }
    
    /**
     * Sets virtual bounds.
     * 
     * @param rect
     *           a rectangle to set
     */
    public static void setVirtualBounds(Rectangle rect) {
    	virtualBounds = rect;
    }

    /**
     * Returns the virtual bounds.
     * 
     * @return virtual bounds
     */
    public static Rectangle getVirtualBounds() {
    	return virtualBounds;
    }
    
    /**
     * Returns a list of color selection buttons.
     *
     * @return a list of color selection buttons
     */
    public List<SGColorSelectionButton> getColorSelectionButtonsList() {
    	// returns an empty list by default
    	return new ArrayList<SGColorSelectionButton>();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // if the event source is one of the color selection buttons
        if (this.getColorSelectionButtonsList().contains(source)) {
            SGColorSelectionButton btn = (SGColorSelectionButton) source;
            
            // clears old color button
            if (this.mCurrentColorSetButton != null) {
                this.mCurrentColorSetButton.setFocused(false);
            }

            // sets to the attribute
            this.mCurrentColorSetButton = btn;
            
            // shows the color dialog
            final int x = this.getX() + 20;
            final int y = this.getY() + 20;
            SGUtility.showColorSelectionDialog(this.mColorDialog, btn,
            		btn.getColor(), x, y);
        }
    }
    
    public void setCurrentColorButton(SGColorSelectionButton btn) {
    	this.mCurrentColorSetButton = btn;
    }
    
    /**
     * The color selection dialog.
     *
     */
    protected SGColorDialog mColorDialog = null;

    /**
     * A color selection button currently used.
     *
     */
    protected SGColorSelectionButton mCurrentColorSetButton = null;

    /**
     * Returns the color selection dialog.
     *
     * @return the color selection dialog
     */
    public SGColorDialog getColorDialog() {
        return this.mColorDialog;
    }

    /**
     * Invoked when the state is changed.
     *
     * @param e
     *          the changed event
     */
    public void stateChanged(final ChangeEvent e) {
        Object source = e.getSource();
        if (source instanceof ColorSelectionModel) {
        	this.colorSelectionModelSelected((ColorSelectionModel) source);
        }
    }
    
    protected void colorSelectionModelSelected(ColorSelectionModel model) {
        if (this.mCurrentColorSetButton != null) {
        	// set the selected color to the color selection button
            Color cl = this.mColorDialog.getSelectedColor();
            this.mCurrentColorSetButton.setColor(cl);
            this.buttonColorAssigned(this.mCurrentColorSetButton);
        }
    }

    /**
     * Invoked when the color of a color selection button is changed.
     * 
     * @param b
     *          a button
     */
    protected void buttonColorAssigned(SGColorSelectionButton b) {
    	// do nothing by default
    }

    /**
     * Called when a component gained focus.
     * @param e
     *          focus event
     */
    public void focusGained(FocusEvent e) {
        Object source = e.getSource();
        if (source instanceof SGColorSelectionButton) {
            SGColorSelectionButton btn = (SGColorSelectionButton) source;

            // set the border
            btn.setFocused(true);

            // set the current color button
            this.mCurrentColorSetButton = btn;
        }
    }

    /**
     * Called when a component lost focus.
     * @param e
     *          focus event
     */
    public void focusLost(FocusEvent e) {
        Object source = e.getSource();
        if (source instanceof SGColorSelectionButton) {
            SGColorSelectionButton btn = (SGColorSelectionButton) source;
            Component opposite = e.getOppositeComponent();
            if (opposite == null) {
                return;
            }
            final boolean bcc = this.isColorChooserComponent(opposite);

            // if the opposite component is not the part of the color chooser dialog,
            // clear the focused button
            if (!bcc) {
                // set the border
                btn.setFocused(false);

                // clear the current color button
                this.mCurrentColorSetButton = null;
            }
        }
    }

    /**
     * Checks whether the given component is a part of the color chooser dialog.
     * @param com
     *             a component
     * @return
     *             true if the component is a part of the color chooser dialog
     */
    private boolean isColorChooserComponent(Component com) {
        Container parent = com.getParent();
        if (parent == null) {
            return false;
        }
        if (this.mColorDialog.equals(parent)) {
            return true;
        } else {
            return this.isColorChooserComponent(parent);
        }
    }

    protected void setCalendarIcon(JButton btn) {
    	btn.setIcon(SGUtility.createIcon(this, "Calendar.png"));
    	btn.setPreferredSize(new Dimension(24, 21));
    }

}

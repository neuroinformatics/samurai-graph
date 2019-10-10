package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.LookAndFeel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 */
public class SGUtility implements SGIConstants, SGIDrawingElementConstants {

    /**
	 * Returns an array of available font family names.
	 *
	 * @return available font family names
	 */
    public static String[] getAvailableFontFamilyNames() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
    }

    /**
     * Finds a right font name from the system with a given font name.
     *
     * @param name
     *           a font name
     * @return found font family name
     */
    public static String findFontFamilyName(final String name) {
    	if (name == null) {
//    		throw new IllegalArgumentException("name == null");
    		return null;
    	}
    	String fName = null;
    	String[] names = getAvailableFontFamilyNames();
    	for (int ii = 0; ii < names.length; ii++) {
    		if (SGUtilityText.isEqualString(names[ii], name)) {
    			fName = names[ii];
    			break;
    		}
    	}
    	return fName;
    }

    /**
     * Returns the canonical path.
     *
     * @param path
     *            path name
     * @return the canonical path
     */
    public static String getCanonicalPath(final String path) {
        File file = new File(path);
        String cPath = null;
        try {
            cPath = file.getCanonicalPath();
        } catch (IOException ex) {
            return null;
        }

        return cPath;
    }

    /**
     *
     */
    public static Rectangle2D createUnion(final ArrayList rectList) {

        if (rectList == null) {
            throw new IllegalArgumentException("rectList==null");
        }

        Rectangle2D rectAll = null;
        for (int ii = 0; ii < rectList.size(); ii++) {
            Rectangle2D rect = (Rectangle2D) rectList.get(ii);
            if (rect == null) {
                throw new IllegalArgumentException("rect==null");
            }

            if (rectAll == null) {
                rectAll = rect;
                continue;
            }

            rectAll = rect.createUnion(rectAll);
        }

        return rectAll;
    }

    /**
     *
     * @param rect
     * @param direction
     * @return
     */
    public static double getRectStart(final Rectangle2D rect,
            final boolean direction) {
        if (direction) {
            return rect.getX();
        }
        return rect.getY();
    }

    /**
     *
     * @param rect
     * @param direction
     * @return
     */
    public static double getRectSize(final Rectangle2D rect,
            final boolean direction) {
        if (direction) {
            return rect.getWidth();
        }
        return rect.getHeight();
    }

    /**
     *
     * @param rect
     * @param direction
     * @return
     */
    public static double getRectEnd(final Rectangle2D rect,
            final boolean direction) {
        return getRectStart(rect, direction) + getRectSize(rect, direction);
    }

    /**
     *
     * @param rect
     * @param value
     * @param direction
     * @return
     */
    public static boolean setRectStart(final Rectangle2D rect,
            final double value, final boolean direction) {

        if (direction) {
            rect.setRect(value, rect.getY(), rect.getWidth(), rect.getHeight());
        } else {
            rect.setRect(rect.getX(), value, rect.getWidth(), rect.getHeight());
        }

        return true;
    }

    /**
     *
     * @param rect
     * @param value
     * @param direction
     * @return
     */
    public static boolean setRectEnd(final Rectangle2D rect,
            final double value, final boolean direction) {

        if (direction) {
            rect.setRect(value - rect.getWidth(), rect.getY(), rect.getWidth(),
                    rect.getHeight());
        } else {
            rect.setRect(rect.getX(), value - rect.getHeight(),
                    rect.getWidth(), rect.getHeight());
        }

        return true;
    }

    /**
     *
     * @param rect
     * @param value
     * @param direction
     * @return
     */
    public static boolean setRectSize(final Rectangle2D rect,
            final double value, final boolean direction) {

        if (direction) {
            rect.setRect(rect.getX(), rect.getY(), value, rect.getHeight());
        } else {
            rect.setRect(rect.getX(), rect.getY(), rect.getWidth(), value);
        }

        return true;
    }

    /**
     *
     * @param rect
     * @param value
     * @param direction
     * @return
     */
    public static boolean isRectContains(final Rectangle2D rect,
            final double value, final boolean direction) {
        return SGUtilityNumber.contains(getRectStart(rect, direction),
                getRectEnd(rect, direction), value);
    }

    /**
     *
     * @param rect1
     * @param rect2
     * @param direction
     * @return
     */
    public static boolean isRectContains(final Rectangle2D rect1,
            final Rectangle2D rect2, final boolean direction) {
        return SGUtilityNumber.contains(getRectStart(rect1, direction),
                getRectEnd(rect1, direction), getRectStart(rect2, direction),
                getRectEnd(rect2, direction));
    }

    /**
     *
     * @param rect1
     * @param rect2
     * @param direction
     * @return
     */
    public static double getOverlapping(final Rectangle2D rect1,
            final Rectangle2D rect2, final boolean direction) {
        return SGUtilityNumber.getOverlap(getRectStart(rect1, direction),
                getRectEnd(rect1, direction), getRectStart(rect2, direction),
                getRectEnd(rect2, direction));
    }

    private static boolean mMessageDialogWasVisible = false;

    /**
     * Set false to mMessageDialogWasVisible flag.
     */
    public static void clearMessageDialogVisible() {
        mMessageDialogWasVisible = false;
    }

    /**
     * Set true to mMessageDialogWasVisible flag.
     */
    public static void setMessageDialogWasVisible() {
        mMessageDialogWasVisible = true;
    }

    /**
     * Return whether message dialog was showed.
     * @return
     */
    public static boolean wasMessageDialogVisible() {
        return mMessageDialogWasVisible;
    }

    /**
     * Shows a message dialog.
     * 
     * @param parentComponent
     *           the parent component
     * @param msg
     *           message to show
     * @param title
     *           title for dialog
     * @param msgType
     *           message type
     */
    public static void showMessageDialog(Component parentComponent,
            Object msg, String title, final int msgType) {
//        JOptionPane.showMessageDialog(parentComponent, msg, title,
//                msgType);
		JOptionPane.showOptionDialog(parentComponent, msg, title,
				JOptionPane.OK_OPTION, msgType, null, new String[] { "OK" },
				JOptionPane.YES_OPTION);
        setMessageDialogWasVisible();
    }

    /**
     * Shows a dialog with error message.
     * 
     * @param parentComponent
     *           the parent component
     * @param msg
     *           message to show
     * @param title
     *           title for dialog
     */
    public static void showErrorMessageDialog(Component parentComponent,
            Object msg, String title) {
        Toolkit.getDefaultToolkit().beep();
        SGUtility.showMessageDialog(parentComponent, msg, title,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a dialog with warning message.
     * 
     * @param parentComponent
     *           the parent component
     * @param msg
     *           message to show
     * @param title
     *           title for dialog
     */
    public static void showWarningMessageDialog(Component parentComponent,
    		Object msg, String title) {
        Toolkit.getDefaultToolkit().beep();
        SGUtility.showMessageDialog(parentComponent, msg, title,
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Shows the confirmation dialog.
     * 
     * @param parentComponent
     *           the parent component
     * @param message
     *           the message
     * @param options
     *           the array of options
     */
    public static int showConfirmationDialog(Component parentComponent, Object message, 
    		Object[] options) {
        // beep
        Toolkit.getDefaultToolkit().beep();

        // show a dialog
		final int ret = JOptionPane.showOptionDialog(parentComponent,
				message, SGIConstants.TITLE_CONFIRMATION,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, JOptionPane.YES_OPTION);

        return ret;
    }

    /**
     * Shows the confirmation dialog with Yes/No options.
     * 
     * @param parentComponent
     *           the parent component
     * @param message
     *           the message
     */
    public static int showYesNoConfirmationDialog(Component parentComponent, Object message) {
    	Object[] options = new String[] { "Yes", "No" };
        return showConfirmationDialog(parentComponent, message, options);
    }
    
    public static void showIllegalInputErrorMessageDialog(
            Component parentComponent) {
        showIllegalInputErrorMessageDialog(parentComponent, "The input value is illegal.");
    }

    public static void showIllegalInputErrorMessageDialog(
            Component parentComponent, String msg) {
        showErrorMessageDialog(parentComponent, msg, "Illegal input");
    }

    public static void showFileNotFoundMessageDialog(
            Component parentComponent) {
        SGUtility.showMessageDialog(parentComponent,
                "Failed to open the file denoted by a specified pathname.",
                "File not found", JOptionPane.ERROR_MESSAGE);
    }

    /**
     *
     * @param wnd
     * @return
     */
    public static void setCenter(final Window wnd, final Window owner) {
        Dimension dim = owner.getSize();
        final int width = wnd.getWidth();
        final int height = wnd.getHeight();
        final int x = owner.getX() + (dim.width - width) / 2;
        final int y = owner.getY() + (dim.height - height) / 2;
        wnd.setLocation(x, y);
    }

    /**
     * Copy the list of copied objects.
     *
     * @param list1
     *            a list which contains copiable objects to be copied
     * @param list2
     *            a list to set the copied objects
     * @return true if succeeded
     */
    @SuppressWarnings("unchecked")
	public static boolean copyObjects(final List list1,
            final List list2) {
        if (list1 == null || list2 == null) {
            throw new IllegalArgumentException("list1==null || list2==null");
        }

        for (int ii = 0; ii < list1.size(); ii++) {
            Object obj = list1.get(ii);
            if ((obj instanceof SGICopiable) == false) {
                throw new IllegalArgumentException(
                        "Not copiable objects are included.");
            }
            SGICopiable cp = (SGICopiable) obj;
            list2.add((SGICopiable) cp.copy());
        }

        return true;
    }

	/**
	 * Adds a menu item to the pop-up menu.
	 * 
	 * @param p
	 *            a pop-up menu
	 * @param l
	 *            an action listener of the command
	 * @param text
	 *            a text string
	 * @param cmd
	 *            a command to be added
	 * @param enabled
	 *            a flag whether the command is enabled
	 * @return added menu item
	 */
    public static JMenuItem addItem(JPopupMenu p, ActionListener l, String text,
    		String cmd, final boolean enabled) {
        final JMenuItem item = new JMenuItem(text);
        item.setActionCommand(cmd);
        p.add(item);
        item.addActionListener(l);
        item.setEnabled(enabled);
        return item;
    }

    public static JMenuItem addItem(JPopupMenu p, ActionListener l, String cmd) {
    	return addItem(p, l, cmd, cmd, true);
    }

	/**
	 * Add a menu command with the check box to the pop-up menu.
	 * 
	 * @param menu
	 *            a pop-up menu
	 * @param l
	 *            an action listener of the command
	 * @param text
	 *            a text string
	 * @param cmd
	 *            a command to be added
	 * @param enabled
	 *            a flag whether the command is enabled
	 * @return added menu item
	 */
    public static JCheckBoxMenuItem addCheckBoxItem(JPopupMenu p, ActionListener l, String text,
    		String cmd, boolean enabled) {
        final SGCheckBoxMenuItem item = new SGCheckBoxMenuItem(text);
        item.setActionCommand(cmd);
        p.add(item);
        item.addActionListener(l);
        item.setEnabled(enabled);
        return item;
    }

    public static JCheckBoxMenuItem addCheckBoxItem(JPopupMenu menu, ActionListener l, String cmd) {
        return addCheckBoxItem(menu, l, cmd, cmd, true);
    }

    /**
     * Adds a menu to the pop-up menu.
     *
     * @param p
     *            a pop-up menu
     * @param l
     *            an action listener of the command
     * @param cmd
     *            a command to be added
     * @param enabled
     *            a flag whether the command is enabled
     * @return added menu
     */
    public static JMenu addMenu(JPopupMenu p, ActionListener l, String cmd, boolean enabled) {
        final JMenu menu = new JMenu(cmd);
        p.add(menu);
        menu.addActionListener(l);
        menu.setEnabled(enabled);
        return menu;
    }

    public static JMenu addMenu(JPopupMenu p, ActionListener l, String cmd) {
    	return addMenu(p, l, cmd, true);
    }

    /**
     * Add a menu command to the menu item.
     *
     * @param parent
     *            the parent menu item
     * @param l
     *            an action listener of the command
     * @param cmd
     *            a command to be added
     * @param enabled
     *            a flag whether the command is enabled
     * @return added menu item
     */
    public static JMenuItem addItem(JMenuItem parent, ActionListener l, String text, String cmd, 
    		final boolean enabled) {
        final JMenuItem item = new JMenuItem(text);
        parent.add(item);
        item.setActionCommand(cmd);
        item.addActionListener(l);
        item.setEnabled(enabled);
        return item;
    }

    public static JMenuItem addItem(JMenuItem parent, ActionListener l, String cmd) {
    	return addItem(parent, l, cmd, cmd, true);
    }
    
    /**
     * Add a menu command with the check box to the menu.
     *
     * @param parent
     *          the parent menu item
     * @param l
     *          an action listener of the command
     * @param cmd
     *          a command to be added
     * @param enabled
     *            a flag whether the command is enabled
     * @return added menu item
     */
    public static JCheckBoxMenuItem addCheckBoxItem(JMenuItem parent, ActionListener l, String text,
    		String cmd, boolean enabled) {
        final SGCheckBoxMenuItem item = new SGCheckBoxMenuItem(text);
        parent.add(item);
        item.setActionCommand(cmd);
        item.addActionListener(l);
        item.setEnabled(enabled);
        return item;
    }

    public static JCheckBoxMenuItem addCheckBoxItem(JMenuItem parent, ActionListener l, String cmd) {
    	return addCheckBoxItem(parent, l, cmd, cmd, true);
    }

    /**
     * Identify the OS name.
     *
     * @param prefix -
     *            the prefix of OS name
     * @return whether the OS name starts with the prefix
     */
    public static boolean identifyOS(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix==null");
        }

        return OS_NAME.toLowerCase().startsWith(prefix);
    }

    /**
     *
     * @return
     */
    public static String getLookAndFeelID() {
        LookAndFeel laf = UIManager.getLookAndFeel();
        if (laf != null) {
            return laf.getID();
        }
        return null;
    }

    /**
     * Copy an array of a String array.
     *
     * @param src
     *            an array to be copied
     * @return a copy of an array
     */
    public static String[] copyStringArray(String[] src) {
    	if (src == null) {
    		return null;
    	}
        final String[] dest = new String[src.length];
        System.arraycopy(src, 0, dest, 0, src.length);
        return dest;
    }

    /**
     * Copy an array of a String array.
     *
     * @param src
     *            an array to be copied
     * @return a copy of an array
     */
    public static String[][] copyStringArray(String[][] src) {
    	if (src == null) {
    		return null;
    	}
        final String[][] dest = new String[src.length][];
        for (int ii = 0; ii < src.length; ii++) {
        	if (src[ii] != null) {
                dest[ii] = copyStringArray(src[ii]);
        	}
        }
        return dest;
    }

    /**
     * Copy an array of a primitive integer array.
     *
     * @param src
     *            an array to be copied
     * @return a copy of an array
     */
    public static int[] copyIntegerArray(int[] src) {
    	if (src == null) {
    		return null;
    	}
        final int[] dest = new int[src.length];
        System.arraycopy(src, 0, dest, 0, src.length);
        return dest;
    }

    /**
     * Copy an array of a primitive integer array.
     *
     * @param src
     *            an array to be copied
     * @return a copy of an array
     */
    public static Integer[] copyIntegerArray(Integer[] src) {
    	if (src == null) {
    		return null;
    	}
        final Integer[] dest = new Integer[src.length];
        System.arraycopy(src, 0, dest, 0, src.length);
        return dest;
    }

    /**
     * Copy an array of a primitive double array.
     *
     * @param src
     *            an array to be copied
     * @return a copy of an array
     */
    public static double[] copyDoubleArray(double[] src) {
    	if (src == null) {
    		return null;
    	}
        final double[] dest = new double[src.length];
        System.arraycopy(src, 0, dest, 0, src.length);
        return dest;
    }

    /**
     * Copy an array of a primitive double array.
     *
     * @param src
     *            an array to be copied
     * @return a copy of an array
     */
    public static double[][] copyDoubleArray(double[][] src) {
    	if (src == null) {
    		return null;
    	}
        final double[][] dest = new double[src.length][];
        for (int ii = 0; ii < src.length; ii++) {
        	if (src[ii] != null) {
                dest[ii] = copyDoubleArray(src[ii]);
        	}
        }
        return dest;
    }

    /**
     * Sets a text string of a double number obtained from a given object 
     * to a text field.
     * 
     * @param tf
     *          a text field
     * @param obj
     *          an object to get a value
     * @return true if succeeded
     */
    public static boolean setDoubleValue(final SGTextField tf, final Object obj) {
        if (obj == null) {
            tf.setText(null);
        } else {
            String str = obj.toString().trim();
            Double d = SGUtilityText.getDouble(str);
            if (d == null) {
                return false;
            }
            tf.setText(str);
        }
        return true;
    }

    /**
     * Sets a text string of an integer number obtained from a given object 
     * to a text field.
     * 
     * @param tf
     *          a text field
     * @param obj
     *          an object to get a value
     * @return true if succeeded
     */
    public static boolean setIntValue(final SGTextField tf, final Object obj) {
        if (obj == null) {
            tf.setText(null);
        } else {
            String str = obj.toString().trim();
            Integer n = SGUtilityText.getInteger(str);
            if (n == null) {
                return false;
            }
            tf.setText(str);
        }
        return true;
    }

    /**
     *
     * @param obj
     * @return
     */
    public static boolean setValue(final SGSpinner sp, final Object obj) {
        if (obj == null) {
            sp.setText("");
            return false;
        }

        String str = obj.toString().trim();
        Double d = SGUtilityText.getDouble(str);
        if (d == null) {
            return false;
        }
        sp.setText(str);
        sp.setValue(d);

        try {
            sp.commitEditByDefault();
        } catch (ParseException e) {
        }

        return true;
    }

    /**
     *
     * @param obj
     * @return
     */
    public static boolean setIntValue(final SGSpinner sp, final Object obj) {
        if (obj == null) {
            sp.setText("");
            return false;
        }

        String str = obj.toString().trim();
        Integer n = SGUtilityText.getInteger(str);
        if (n == null) {
            return false;
        }
        sp.setText(str);
        sp.setValue(Double.valueOf(n.doubleValue()));

        try {
            sp.commitEditByDefault();
        } catch (ParseException e) {
        }

        return true;
    }

    /**
     * Returns a location on a rectangle.
     */
    public static int getMouseLocation(final Rectangle2D rect, final int x,
            final int y, final float radius) {
        final boolean bMinX = (Math.abs(x - rect.getMinX()) < radius);
        final boolean bMidX = (Math.abs(x - rect.getCenterX()) < radius);
        final boolean bMaxX = (Math.abs(x - rect.getMaxX()) < radius);
        final boolean bMinY = (Math.abs(y - rect.getMinY()) < radius);
        final boolean bMidY = (Math.abs(y - rect.getCenterY()) < radius);
        final boolean bMaxY = (Math.abs(y - rect.getMaxY()) < radius);

        int location = OTHER;

        if (bMinX) {
            if (bMinY) {
                // north west
                location = NORTH_WEST;
            } else if (bMidY) {
                // west
                location = WEST;
            } else if (bMaxY) {
                // south west
                location = SOUTH_WEST;
            }
        } else if (bMaxX) {
            if (bMinY) {
                // north east
                location = NORTH_EAST;
            } else if (bMidY) {
                // east
                location = EAST;
            } else if (bMaxY) {
                // south east
                location = SOUTH_EAST;
            }
        } else if (bMidX) {
            if (bMinY) {
                // north
                location = NORTH;
            } else if (bMaxY) {
                // south
                location = SOUTH;
            }
        }

        return location;
    }

    /**
     * Resize the rectangle by mouse drag.
     *
     * @param rect
     * @param pos
     * @param e
     * @param ml
     */
    public static void resizeRectangle(final Rectangle2D rect, // rectangle
            final Point pos, // mouse position
            final MouseEvent e, // mouse event on dragging
            final int ml // mouse location
    ) {

        // is Shift-key is pressed
        final boolean onShift = ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0);

        final int diffX = e.getX() - pos.x;
        final int diffY = e.getY() - pos.y;

        final float xOld = (float) rect.getX();
        final float yOld = (float) rect.getY();
        final float wOld = (float) rect.getWidth();
        final float hOld = (float) rect.getHeight();

        final float sizeOldX = wOld;
        final float sizeOldY = hOld;

        float sizeNewX = 0.0f;
        float sizeNewY = 0.0f;

        // variables to be updated
        float x = xOld;
        float y = yOld;
        float w = wOld;
        float h = hOld;

        if (ml == NORTH) {
            // System.out.println("NORTH");
            pos.setLocation(pos.getX(), pos.getY() + diffY);
            sizeNewY = sizeOldY - diffY;
            y = yOld + sizeOldY - sizeNewY;
            h = sizeNewY;
        } else if (ml == SOUTH) {
            // System.out.println("SOUTH");
            pos.setLocation(pos.getX(), pos.getY() + diffY);
            sizeNewY = sizeOldY + diffY;
            h = sizeNewY;
        } else if (ml == WEST) {
            // System.out.println("WEST");
            pos.setLocation(pos.getX() + diffX, pos.getY() + diffY);
            sizeNewX = sizeOldX - diffX;
            x = xOld + sizeOldX - sizeNewX;
            w = sizeNewX;
        } else if (ml == EAST) {
            // System.out.println("EAST");
            pos.setLocation(pos.getX() + diffX, pos.getY());
            sizeNewX = sizeOldX + diffX;
            w = sizeNewX;
        } else if (ml == NORTH_WEST) {
            pos.setLocation(pos.getX() + diffX, pos.getY() + diffY);
            sizeNewX = sizeOldX - diffX;

            if (onShift) {
                sizeNewY = sizeNewX * (sizeOldY / sizeOldX);
            } else {
                sizeNewY = sizeOldY - diffY;
            }
            x = xOld + sizeOldX - sizeNewX;
            y = yOld + sizeOldY - sizeNewY;
            w = sizeNewX;
            h = sizeNewY;
            // System.out.println(sizeOldY+" "+sizeNewY);
        } else if (ml == NORTH_EAST) {
            if (!onShift) {
                pos.setLocation(pos.getX() + diffX, pos.getY() + diffY);

                sizeNewX = sizeOldX + diffX;
                sizeNewY = sizeOldY - diffY;

                y = yOld + sizeOldY - sizeNewY;
                w = sizeNewX;
                h = sizeNewY;

            } else {
                pos.setLocation(pos.getX(), pos.getY() + diffY);

                sizeNewY = sizeOldY - diffY;
                sizeNewX = sizeNewY * (sizeOldX / sizeOldY);

                y = yOld + sizeOldY - sizeNewY;
                w = sizeNewX;
                h = sizeNewY;
            }
        } else if (ml == SOUTH_WEST) {
            if (!onShift) {
                pos.setLocation(pos.getX() + diffX, pos.getY() + diffY);

                sizeNewX = sizeOldX - diffX;
                sizeNewY = sizeOldY + diffY;

                x = xOld + sizeOldX - sizeNewX;
                w = sizeNewX;
                h = sizeNewY;
            } else {
                pos.setLocation(pos.getX() + diffX, pos.getY() + diffY);

                sizeNewX = sizeOldX - diffX;
                sizeNewY = sizeNewX * (sizeOldY / sizeOldX);

                x = xOld + sizeOldX - sizeNewX;
                w = sizeNewX;
                h = sizeNewY;
            }
        } else if (ml == SOUTH_EAST) {
            if (!onShift) {
                pos.setLocation(pos.getX() + diffX, pos.getY() + diffY);

                sizeNewX = sizeOldX + diffX;
                sizeNewY = sizeOldY + diffY;

                w = sizeNewX;
                h = sizeNewY;
            } else {
                pos.setLocation(pos.getX() + diffX, pos.getY());

                sizeNewX = sizeOldX + diffX;
                sizeNewY = sizeNewX * (sizeOldY / sizeOldX);

                w = sizeNewX;
                h = sizeNewY;
            }
        }

        // update the rectangle
        rect.setRect(x, y, w, h);

        // System.out.println(rect);
    }

    /**
     * Moves an object to the tail of the list.
     *
     * @param obj
     *            an objct to be moved
     * @param list
     *            the list which contains the object
     * @return true if succeeded
     */
    public static boolean moveObjectToTail(final Object obj,
            final List list) {
        return moveObjectTo(obj, list, list.size() - 1);
    }

    /**
     * Moves an object to next position of the list.
     *
     * @param obj
     *            an objct to be moved
     * @param list
     *            the list which contains the object
     * @return true if succeeded
     */
    public static boolean moveObjectToNext(final Object obj,
            final List list) {
        List<Object> objList = new ArrayList<Object>();
        objList.add(obj);
        return moveObject(objList, list, 1);
    }

    /**
     * Moves an object to previous position of the list.
     *
     * @param obj
     *            an objct to be moved
     * @param list
     *            the list which contains the object
     * @return true if succeeded
     */
    public static boolean moveObjectToPrevious(final Object obj,
            final List list) {
        List<Object> objList = new ArrayList<Object>();
        objList.add(obj);
        return moveObject(objList, list, -1);
    }

    /**
     * Moves an object to the head of the list.
     *
     * @param obj
     *            an objct to be moved
     * @param list
     *            the list which contains the object
     * @return true if succeeded
     */
    public static boolean moveObjectToHead(final Object obj,
            final List list) {
        return moveObjectTo(obj, list, 0);
    }

    /**
     * Moves an object to the location of given index.
     *
     * @param obj
     *            an object to be moved
     * @param list
     *            a list that contains the given object
     * @param index
     *            the array index
     * @return true if succeeded
     */
    public static boolean moveObjectTo(final Object obj, final List list,
            final int index) {
        if (list == null) {
            throw new IllegalArgumentException("list==null");
        }
        if (index < 0 || index >= list.size()) {
            throw new IllegalArgumentException(
                    "index < 0 || index >= list.size() : " + index);
        }
        if (list.remove(obj) == false) {
            return false;
        }
        list.add(index, obj);
        return true;
    }

    /**
     * Moves the objects in a list.
     *
     * @param movedList
     *           a list of objects to be moved
     * @param list
     *           a list of objects
     * @param num
     *           the level to move the objects
     * @return true if succeeded
     */
    public static boolean moveObject(final List movedList, final List list,
            final int num) {

        if (num > 0) {
            for (int ii = movedList.size() - 1; ii >= 0; ii--) {
                Object obj = movedList.get(ii);
                final int index = list.indexOf(obj);
                if (index == -1) {
                    return false;
                }
                final int indexNew = index + num;
                if (indexNew > list.size() - 1) {
                    continue;
                }
                if (moveObjectTo(obj, list, indexNew) == false) {
                    return false;
                }
            }
        } else {
            for (int ii = 0; ii < movedList.size(); ii++) {
                Object obj = movedList.get(ii);
                final int index = list.indexOf(obj);
                if (index == -1) {
                    return false;
                }
                final int indexNew = index + num;
                if (indexNew < 0) {
                    continue;
                }
                if (moveObjectTo(obj, list, indexNew) == false) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     *
     * @param list
     *            a list to be modified
     * @param listVisible
     *            a list of visible objects
     * @return
     */
    public static boolean setVisibleList(final List list, final List listVisible) {
        ArrayList listInvisible = new ArrayList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGIVisible el = (SGIVisible) list.get(ii);
            final boolean b = listVisible.contains(el);
            el.setVisible(b);
            if (!b) {
                listInvisible.add(el);
            }
        }

        list.clear();
        for (int ii = 0; ii < listVisible.size(); ii++) {
            list.add(listVisible.get(ii));
        }
        for (int ii = 0; ii < listInvisible.size(); ii++) {
            list.add(listInvisible.get(ii));
        }

        return true;
    }

	/**
	 * Returns the file name from given path name.
	 * 
	 * @param path
	 *            path name
	 * @return the file name
	 */
	public static String getFileName(final String path) {
		String name = null;
		final int separatorIndex = path.lastIndexOf(FILE_SEPARATOR);
		if (separatorIndex == -1 || separatorIndex == path.length() - 1) {
			final int slashIndex = path.lastIndexOf('/');
			if (slashIndex == -1 || slashIndex == path.length() - 1) {
				name = path;
			} else {
				name = path.substring(slashIndex + 1);
			}
		} else {
			name = path.substring(separatorIndex + 1);
		}
		return name;
	}

	/**
	 * Returns the simplified file name from given path name.
	 * 
	 * @param path
	 *            path name
	 * @return the simplified file name
	 */
	public static String getSimpleFileName(final String path) {
		String fileName = getFileName(path);
		
		// removes extension
		String name = null;
		final int cIndex = fileName.lastIndexOf('.');
		if (cIndex <= 0) {
			name = fileName;
		} else {
			name = fileName.substring(0, cIndex);
		}
		return name;
	}
	
    /**
     * Creates a text string for a base of the data names from the file path.
     *
     * @param filepath
     *             path of the data file
     * @return a text string for data name
     */
    public static String createDataNameBase(final String filepath) {

        // check the input value
        if (SGUtilityText.isValidString(filepath) == false) {
            return null;
        }

        // get the file name
        String name = getSimpleFileName(filepath);

        // add escape character
        String nameNew = addEscapeChar(name);

        return nameNew;
    }

    /**
     * Add escape characters before each characters '_' or '^';
     *
     * @param str
     *           an input text string
     * @return
     *           modified text string
     */
    public static String addEscapeChar(final String str) {
        // if the simplified file name contains underscores,
        // append a backslash before each of them
        char[] cArray = str.toCharArray();
        List<Character> cList = new ArrayList<Character>();
        for (int ii = 0; ii < cArray.length; ii++) {
            final char c = cArray[ii];
            if (c == '_' || c == '^') {
                cList.add('\\');
            }
            cList.add(c);
        }
        char[] cArrayNew = new char[cList.size()];
        for (int ii = 0; ii < cArrayNew.length; ii++) {
            cArrayNew[ii] = cList.get(ii);
        }
        String strNew = new String(cArrayNew);
        return strNew;
    }

    /**
     * Remove escape characters from a given string.
     *
     * @param str
     *           an input text string
     * @return
     *           modified text string
     */
    public static String removeEscapeChar(final String str) {
        char[] cArray = str.toCharArray();
        List<Character> cList = new ArrayList<Character>();
        for (int ii = 0; ii < cArray.length; ii++) {
            final char c = cArray[ii];
            if (c != '\\') {
                cList.add(c);
            }
        }
        char[] cArrayNew = new char[cList.size()];
        for (int ii = 0; ii < cArrayNew.length; ii++) {
            cArrayNew[ii] = cList.get(ii);
        }
        String strNew = new String(cArrayNew);
        return strNew;
    }

    /**
     * Check whether two object arrays are equal. Null values are permitted.
     *
     * @param a1
     *          the first array
     * @param a2
     *          the second array
     * @return true if two arrays are equal
     */
    public static boolean equals(Object[] a1, Object[] a2) {
        if (a1 != null) {
            if (a2 == null) {
                return false;
            }
            if (a1.length != a2.length) {
                return false;
            }
            for (int ii = 0; ii < a1.length; ii++) {
                if (a1[ii] != null) {
                    if (a1[ii].equals(a2[ii]) == false) {
                        return false;
                    }
                } else {
                    if (a2 != null) {
                        return false;
                    }
                }
            }
        } else {
            if (a2 != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether two objects are equal. Null values are permitted.
     *
     * @param obj1
     *           the first object
     * @param obj2
     *           the second object
     * @return true if two objects are equal
     */
    public static boolean equals(Object obj1, Object obj2) {
        if (obj1 != null) {
            return obj1.equals(obj2);
        } else {
            if (obj2 == null) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Check whether two integer arrays are equal. Null values are permitted.
     *
     * @param a1
     *           the first array
     * @param a2
     *           the second array
     * @return true if two arrays are equal
     */
    public static boolean equals(int[] a1, int[] a2) {
    	if (a1 == null && a2 == null) {
    		return true;
    	}
    	if (a1 == null) {
    		if (a2 != null) {
    			return false;
    		}
    	} else {
    		if (a2 == null) {
    			return false;
    		}
    	}
    	if (a1.length != a2.length) {
    		return false;
    	}
    	for (int ii = 0; ii < a1.length; ii++) {
    		if (a1[ii] != a2[ii]) {
    			return false;
    		}
    	}
    	return true;
    }

    public static boolean isValidPropertyValue(final float value) {
    	if (Float.isNaN(value) || Float.isInfinite(value)) {
    		return false;
    	}
    	return true;
    }

    public static boolean isValidPropertyValue(final double value) {
    	if (Double.isNaN(value) || Double.isInfinite(value)) {
    		return false;
    	}
    	return true;
    }

    /**
     * Calculates a value to set to a property of various objects.
     *
     * @param value
     *           an input value
     * @param unit
     *           the unit for the input value
     * @param convUnit
     *           the unit to convert
     * @param min
     *           the minimum value for converted result
     * @param max
     *           the minimum value for converted result
     * @param order
     *           the minimal order for converted result
     */
    public static Float calcPropertyValue(final float value, final String unit,
            final String convUnit, final double min, final double max, final int order) {

    	Float cValue = calcPropertyValueInUnit(value, unit, convUnit, min, max, order);
    	if (cValue == null) {
    		return null;
    	}

        // convert the value into a given unit
        final float vNew;
        if (convUnit == null) {
            vNew = cValue.floatValue();
        } else {
            vNew = (float) SGUtilityText.convertToPoint(cValue.floatValue(), convUnit);
        }
        return Float.valueOf(vNew);
    }

    /**
     * Calculates a value to set to a property of various objects.
     *
     * @param value
     *           an input value
     * @param unit
     *           the unit for the input value
     * @param convUnit
     *           the unit to convert
     * @param min
     *           the minimum value for converted result
     * @param max
     *           the minimum value for converted result
     * @param order
     *           the minimal order for converted result
     */
    public static Float calcPropertyValueInUnit(final float value, final String unit,
            final String convUnit, final double min, final double max, final int order) {

    	// check the input value
    	if (isValidPropertyValue(value) == false) {
    		return null;
    	}

        // convert a given value to a value in a given unit
        double vConv;
        if (unit == null || convUnit == null) {
        	if (value < min) {
                return null;
        	} else if (value > max) {
                return null;
        	} else {
                vConv = value;
        	}
        } else {
            final double conv = SGUtilityText.convert(value, unit, convUnit);
            if (conv < min) {
                return null;
            } else if (conv > max) {
                return null;
            } else {
                vConv = conv;
            }
        }

        // round off the value in a given unit
        final int digit = order - 1;
        final double convOff = SGUtilityNumber.roundOffNumber(vConv, digit);

        return Float.valueOf((float) convOff);
    }

    /**
     * Returns the line width from the input value within an allowed range.
     *
     * @param lw
     *          an input value
     * @param unit
     *          the unit for the input value
     * @return line width within an allowed range
     */
    public static final Float getLineWidth(final float lw, final String unit) {
        return calcPropertyValue(lw, unit, LINE_WIDTH_UNIT, LINE_WIDTH_MIN_VALUE,
                LINE_WIDTH_MAX_VALUE, LINE_WIDTH_MINIMAL_ORDER);
    }

    /**
     * Returns the font size from the input value within an allowed range.
     *
     * @param size
     *          an input value
     * @param unit
     *          the unit for the input value
     * @return font size within an allowed range
     */
    public static final Float getFontSize(final float size, final String unit) {
        return calcPropertyValue(size, unit, FONT_SIZE_UNIT, FONT_SIZE_MIN_VALUE,
                FONT_SIZE_MAX_VALUE, FONT_SIZE_MINIMAL_ORDER);
    }


    // returns an integer value for axis location from a string
    public static int getAxisLocation(final String location) {
        int loc = -1;
        if (SGUtilityText.isEqualString(SGIFigureElementAxis.AXIS_BOTTOM, location)) {
            loc = SGIFigureElementAxis.AXIS_HORIZONTAL_1;
        } else if (SGUtilityText.isEqualString(SGIFigureElementAxis.AXIS_TOP, location)) {
            loc = SGIFigureElementAxis.AXIS_HORIZONTAL_2;
        } else if (SGUtilityText.isEqualString(SGIFigureElementAxis.AXIS_LEFT, location)) {
            loc = SGIFigureElementAxis.AXIS_VERTICAL_1;
        } else if (SGUtilityText.isEqualString(SGIFigureElementAxis.AXIS_RIGHT, location)) {
            loc = SGIFigureElementAxis.AXIS_VERTICAL_2;
        }
        return loc;
    }

    /**
     * Returns the location name of a given location ID.
     *
     * @param id
     *           a location ID
     * @return the location name
     */
    public static String getLocationName(final int id) {
        String name = null;
        switch (id) {
        case SGIFigureElementAxis.AXIS_HORIZONTAL_1:
            name = SGIFigureElementAxis.AXIS_BOTTOM;
            break;
        case SGIFigureElementAxis.AXIS_HORIZONTAL_2:
            name = SGIFigureElementAxis.AXIS_TOP;
            break;
        case SGIFigureElementAxis.AXIS_VERTICAL_1:
            name = SGIFigureElementAxis.AXIS_LEFT;
            break;
        case SGIFigureElementAxis.AXIS_VERTICAL_2:
            name = SGIFigureElementAxis.AXIS_RIGHT;
            break;
        case SGIFigureElementAxis.AXIS_NORMAL:
            name = SGIFigureElementAxis.AXIS_COLOR_BAR;
            break;
        }
        return name;
    }

    /**
     * Returns the location ID of a given location name.
     *
     * @param str
     *           a location name
     * @return the location ID
     */
    public static int getLocationInPlane(final String str) {
        int loc = -1;
        if (SGUtilityText.isEqualString(SGIFigureElementAxis.AXIS_BOTTOM, str)) {
            loc = SGIFigureElementAxis.AXIS_HORIZONTAL_1;
        } else if (SGUtilityText.isEqualString(SGIFigureElementAxis.AXIS_TOP, str)) {
            loc = SGIFigureElementAxis.AXIS_HORIZONTAL_2;
        } else if (SGUtilityText.isEqualString(SGIFigureElementAxis.AXIS_LEFT, str)) {
            loc = SGIFigureElementAxis.AXIS_VERTICAL_1;
        } else if (SGUtilityText.isEqualString(SGIFigureElementAxis.AXIS_RIGHT, str)) {
            loc = SGIFigureElementAxis.AXIS_VERTICAL_2;
        } else if (SGUtilityText.isEqualString(SGIFigureElementAxis.AXIS_COLOR_BAR, str)) {
            loc = SGIFigureElementAxis.AXIS_NORMAL;
        }
        return loc;
    }

    /**
     * Assigns ID number.
     *
     * @param curIdList
     *           The list of current ID numbers. All elements must be positive.
     * @return an assigned ID number
     */
    public static int assignIdNumber(final List<Integer> curIdList) {

    	// returns 1 when the given ID list is empty
    	if (curIdList.size() == 0) {
    		return 1;
    	}

    	// sorts the current ID numbers
    	Integer[] array = new Integer[curIdList.size()];
    	array = curIdList.toArray(array);
    	Arrays.sort(array);

    	int id;
    	final int last = array[array.length - 1];
    	if (last == Integer.MAX_VALUE) {
    		if (array.length == Integer.MAX_VALUE) {
    			// empty numbers do not exist
    			// this shouldn't happen
    			throw new Error("Failed to assign new ID number.");
    		}

        	// search an empty number
    		id = 1;
    		for (int ii = 0; ii < array.length; ii++) {
    			final int num = ii + 1;
    			if (array[ii] != num) {
    				id = num;
    				break;
    			}
    		}
    	} else {
    		id = last + 1;
    	}

    	return id;

/*
    	// initialize the returned value
    	int id = array.length + 1;

    	// search an empty number
		for (int ii = 0; ii < array.length; ii++) {
			if (array[ii] <= 0) {
    			throw new IllegalArgumentException("Non-positive ID number exists: "
    					+ array[ii]);
			}
			final int num = ii + 1;
			if (array[ii] != num) {
				id = num;
				break;
			}
		}
		return id;
*/
    }

    /**
     * Checks whether a given array contains a given object.
     * Null objects are ignored.
     *
     * @param array
     *           an array
     * @param obj
     *           an object
     * @return true if a given array contains a given object
     */
    public static boolean containsNullIgnored(Object[] array, Object obj) {
    	if (array == null) {
    		throw new IllegalArgumentException("array == null");
    	}
    	if (obj == null) {
    		return false;
    	}
    	for (int ii = 0; ii < array.length; ii++) {
    		if (array[ii] == null) {
    			continue;
    		}
    		if (array[ii].equals(obj)) {
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * Checks whether a given array a1 contains at least one element of an array a2.
     * Null objects are ignored.
     *
     * @param a1
     *           the first array
     * @param a2
     *           the second array
     * @return true if a given array a1 contains at least one element of an array a2
     */
    public static boolean containsNullIgnored(Object[] a1, Object[] a2) {
    	if (a1 == null || a2 == null) {
    		throw new IllegalArgumentException("a1 == null || a2 == null");
    	}
    	for (int ii = 0; ii < a2.length; ii++) {
    		if (containsNullIgnored(a1, a2[ii])) {
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * Checks whether a given array contains a given object.
     *
     * @param array
     *           an array
     * @param obj
     *           an object
     * @return true if a given array contains a given object
     */
    public static boolean contains(Object[] array, Object obj) {
    	if (array == null) {
    	    throw new IllegalArgumentException("array == null");
    	}
    	for (int ii = 0; ii < array.length; ii++) {
    	    if (SGUtility.equals(array[ii], obj)) {
    	        return true;
    	    }
    	}
    	return false;
    }

    /**
     * Checks whether a given array contains a given number.
     *
     * @param array
     *           an array of numbers
     * @param num
     *           an number
     * @return true if a given array contains a given number
     */
    public static boolean contains(int[] array, int num) {
    	if (array == null) {
    	    throw new IllegalArgumentException("array == null");
    	}
    	for (int ii = 0; ii < array.length; ii++) {
    	    if (array[ii] == num) {
    	        return true;
    	    }
    	}
    	return false;
    }
    
    /**
     * Removes an element of given value from given array.
     * 
     * @param array
     *          an array
     * @param num
     *          an element to remove
     * @return modified array
     */
    public static int[] remove(int[] array, final int num) {
    	if (array == null) {
    	    throw new IllegalArgumentException("array == null");
    	}
    	List<Integer> list = new ArrayList<Integer>();
    	for (int ii = 0; ii < array.length; ii++) {
    		if (array[ii] != num) {
    			list.add(array[ii]);
    		}
    	}
    	int[] ret = new int[list.size()];
    	for (int ii = 0; ii < ret.length; ii++) {
    		ret[ii] = list.get(ii);
    	}
    	return ret;
    }

    /**
     * Checks whether a given array contains a given character.
     *
     * @param array
     *           an array of characters
     * @param c
     *           an character
     * @return true if a given array contains a given character
     */
    public static boolean contains(char[] array, final char c) {
    	if (array == null) {
    	    throw new IllegalArgumentException("array == null");
    	}
    	for (int ii = 0; ii < array.length; ii++) {
    	    if (array[ii] == c) {
    	        return true;
    	    }
    	}
    	return false;
    }

    /**
     * Checks whether a given array a1 contains at least one element of an array a2.
     *
     * @param a1
     *           the first array
     * @param a2
     *           the second array
     * @return true if a given array a1 contains at least one element of an array a2
     */
    public static boolean contains(Object[] a1, Object[] a2) {
    	if (a1 == null || a2 == null) {
    		throw new IllegalArgumentException("a1 == null || a2 == null");
    	}
    	for (int ii = 0; ii < a2.length; ii++) {
    		if (contains(a1, a2[ii])) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public static boolean contains(char[] a1, char[] a2) {
    	if (a1 == null || a2 == null) {
    		throw new IllegalArgumentException("a1 == null || a2 == null");
    	}
    	for (int ii = 0; ii < a2.length; ii++) {
    		if (contains(a1, a2[ii])) {
    			return true;
    		}
    	}
    	return false;
    }


    /**
     * Checks whether the first list contains at least one element in the second list.
     *
     * @param list1
     *           the first list
     * @param list2
     *           the second list
     * @return true if the first list contains at least one element in the second list
     */
    public static boolean contains(List list1, List list2) {
    	if (list1 == null || list2 == null) {
    		throw new IllegalArgumentException("a1 == null || a2 == null");
    	}
    	for (int ii = 0; ii < list2.size(); ii++) {
    		if (list1.contains(list2.get(ii))) {
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * Checks whether the first list contains all elements of the second list.
     *
     * @param list1
     *           the first list
     * @param list2
     *           the second list
     * @return true if the first list contains all elements of the second list
     */
    public static boolean containsAll(List<?> list1, List<?> list2) {
    	if (list1 == null || list2 == null) {
    		throw new IllegalArgumentException("list1 == null || list2 == null");
    	}
    	for (int ii = 0; ii < list2.size(); ii++) {
    		if (!list1.contains(list2.get(ii))) {
    			return false;
    		}
    	}
    	return true;
    }

    /**
     * Checks whether the first array contains all elements of the second array.
     *
     * @param a1
     *           the first array
     * @param a2
     *           the second array
     * @return true if the first array contains all elements of the second array
     */
    public static boolean containsAll(Object[] a1, Object[] a2) {
    	if (a1 == null || a2 == null) {
    		throw new IllegalArgumentException("a1 == null || a2 == null");
    	}
    	for (int ii = 0; ii < a2.length; ii++) {
    		if (!SGUtility.contains(a1, a2[ii])) {
    			return false;
    		}
    	}
    	return true;
    }

    /**
     * Read and returns the column indices.
     *
     * @param el
     *            an Element object
     * @param key
     *            the key of index property
     * @return the indices it exists
     */
    public static Integer[] readIndices(Element el, String key) {
//        List<Integer> numList = new ArrayList<Integer>();
        String str = el.getAttribute(key);
//        if (str.length() != 0) {
//            final int start = str.indexOf('{');
//            final int end = str.lastIndexOf('}');
//            if (start == -1 || end == -1) {
//            	Integer num = SGUtilityText.getInteger(str);
//            	if (num == null) {
//            		return null;
//            	}
//            	return new Integer[] { num };
//            }
//            if (start > end) {
//                return null;
//            }
//            String sub = str.substring(start + 1, end);
//            StringTokenizer st = new StringTokenizer(sub, ",");
//            while (st.hasMoreTokens()) {
//                String token = st.nextToken();
//                Integer num = SGUtilityText.getInteger(token);
//                if (num == null) {
//                    return null;
//                }
//                numList.add(num);
//            }
//        }
//        Integer[] array = new Integer[numList.size()];
//        for (int ii = 0; ii < array.length; ii++) {
//            array[ii] = (Integer) numList.get(ii);
//        }
//        return array;
        return SGUtilityText.parseIndices(str);
    }

    /**
     * Return the string of given value.
     * @param value
     * @return
     */
    public static String getNumberName(double value) {
        boolean minus = (value < 0);
        String sval;
        value = Math.abs(value);
        if (value==0.0) {
            return "0";
        } else if (value > 10000) {
            double val2 = value;
            int count = 0;
            while (val2>=1) {
                val2 = val2 / 10.0;
                count++;
            }
            val2 = value/Math.pow(10, count-1);
            sval = String.format("%.2f", val2);
            sval = SGUtility.removeLastZero(sval);
            sval = sval+String.format("e%d", count-1);
        } else if (value>100) {
            sval = String.format("%.0f", value);
        } else if (value>1) {
            sval = String.format("%.2f", value);
            sval = SGUtility.removeLastZero(sval);
        } else if (value>0.0001) {
            sval = String.format("%.4f", value);
            sval = SGUtility.removeLastZero(sval);
        } else {
            double val2 = value;
            int count = 0;
            while (val2<10) {
                val2 = val2 * 10.0;
                count++;
            }
            val2 = value * Math.pow(10, count-1);
            sval = String.format("%.2f", val2);
            sval = SGUtility.removeLastZero(sval);
            sval = sval+String.format("e-%d", count-1);
        }
        if (minus) {
            return "-"+sval;
        } else {
            return sval;
        }
    }

    /**
     * Remove last zero characters in string.
     * @param str
     * @return string which has no zero at last.
     */
    private static String removeLastZero(String str) {
        while (str.substring(str.length()-1).equals("0")) {
            str = str.substring(0, str.length()-1);
        }
        if (str.substring(str.length()-1).equals(".")) {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }

	/**
	 * Check whether one version is released later than the other version.
	 *
	 * @param m1
	 *           major version number of version m
	 * @param m2
	 *           minor version number of version m
	 * @param m3
	 *           micro version number of version m
	 * @param n1
	 *           major version number of version n
	 * @param n2
	 *           minor version number of version n
	 * @param n3
	 *           micro version number of version n
	 * @return true if version n is released later than version m
	 */
	public static boolean compareVersionNumber(final int m1, final int m2,
	        final int m3, final int n1, final int n2, final int n3) {
	    final boolean b;
	    if (m1 < n1) {
	        b = true;
	    } else if (m1 == n1) {
	        if (m2 < n2) {
	            b = true;
	        } else if (m2 == n2) {
	            b = (m3 < n3);
	        } else {
	            b = false;
	        }
	    } else {
	        b = false;
	    }
	    return b;
	}

	/**
	 * Checks whether one version m is smaller than or equal to the other version n.
	 *
	 * @param vm
	 *           a text string of the version number of version m
	 * @param vn
	 *           a text string of the version number of version n
	 * @return true if version n is released later than version m
	 */
	public static boolean isVersionNumberEqualOrSmallerThan(final String vm, final String vn) {
		if (vm == null || vn == null) {
			throw new IllegalArgumentException("vm == null || vn == null");
		}
		if (vm.equals(vn)) {
			return true;
		}
		int[] mNum = splitVersionNumber(vm);
		if (mNum == null) {
			throw new IllegalArgumentException("Invalid version number for version m: " + vm);
		}
		int[] nNum = splitVersionNumber(vn);
		if (nNum == null) {
			throw new IllegalArgumentException("Invalid version number for version n: " + vn);
		}
		return compareVersionNumber(mNum[0], mNum[1], mNum[2], nNum[0], nNum[1], nNum[2]);
	}

	/**
	 * Checks whether one version n is larger than the other version m.
	 * If two version numbers are equal, this method returns true.
	 * This method permits empty strings as input values.
	 *
	 * (1) If both version numbers are empty strings, returns false.
	 * (2) If one of the version numbers is an empty string and the other one is not,
	 *     empty string is always supposed to be a string "1.0.7".
	 * (3) If both version numbers are not empty strings, comparison of numbers is executed.
	 *
	 * @param vm
	 *           a text string of the version number of version m
	 * @param vn
	 *           a text string of the version number of version n
	 * @return true if version n is released later than version m
	 */
	public static boolean isVersionNumberEqualOrSmallerThanPermittingEmptyString(
			final String vm, final String vn) {
		if (vm == null || vn == null) {
			throw new IllegalArgumentException("vm == null || vn == null");
		}
		final boolean bm = "".equals(vm);
		final boolean bn = "".equals(vn);
		if (bm && bn) {
			return true;
		} else if (bm && !bn) {
			return isVersionNumberEqualOrSmallerThan("1.0.7", vn);
		} else if (!bm && bn) {
			return isVersionNumberEqualOrSmallerThan(vm, "1.0.7");
		} else {
			return isVersionNumberEqualOrSmallerThan(vm, vn);
		}
	}

	/**
	 * Splits a text string of version number into an integer array.
	 *
	 * @param str
	 *           a text string of version number
	 * @return an array of the major, minor and micro version number
	 */
	public static int[] splitVersionNumber(final String str) {
		String[] tokens = str.split("\\.");
		if (tokens.length < 3) {
			return null;
		}
		int[] numbers = new int[tokens.length];
		for (int ii = 0; ii < numbers.length; ii++) {
			Integer num = SGUtilityText.getInteger(tokens[ii]);
			if (num == null) {
				return null;
			}
			numbers[ii] = num.intValue();
		}
		return numbers;
	}

	/**
	 * Input parameters for mouse drag operation.
	 *
	 */
    public static class MouseDragInput {
    	private MouseEvent mouseEvent;
    	private Point pressedPoint;
    	private SGTuple2f draggedDirection;
    	private int fixedCoordinate;
		public MouseDragInput(final MouseEvent e, final Point pressedPoint,
				final SGTuple2f draggedDirection, final int fixedCoordinate) {
			super();
			this.mouseEvent = e;
			this.pressedPoint = pressedPoint;
			this.draggedDirection = draggedDirection;
			this.fixedCoordinate = fixedCoordinate;
		}
    }

	/**
	 * Output for mouse drag operation.
	 *
	 */
    public static class MouseDragResult {
    	public int dx, dy, ex, ey;
    	public SGTuple2f draggedDirection;
    	public int fixedCoordinate;
    }

    /**
     * Returns the results for mouse drag operation.
     *
     * @param input
     *           input parameters for mouse drag operation
     * @return output for mouse drag operation
     */
    public static MouseDragResult getMouseDragResult(final MouseDragInput input) {
    	MouseEvent e = input.mouseEvent;
        SGTuple2f draggedDirection = input.draggedDirection;
        int fixedCoordinate = input.fixedCoordinate;

    	MouseDragResult result = new MouseDragResult();
    	int ex = e.getX();
    	int ey = e.getY();
        final int mod = e.getModifiers();
        final boolean shift = (mod & InputEvent.SHIFT_MASK) != 0;
        int dx = ex - input.pressedPoint.x;
        int dy = ey - input.pressedPoint.y;

        if (shift) {
        	// When the shift key is pressed, fix one of the components
        	// on mouse drag
            if (draggedDirection == null) {
            	draggedDirection = new SGTuple2f(dx, dy);
            	final float ax = Math.abs(dx);
            	final float ay = Math.abs(dy);
            	if (ax > ay) {
            		fixedCoordinate = ey;
            	} else {
            		fixedCoordinate = ex;
            	}
            }
        	final float ax = Math.abs(draggedDirection.x);
        	final float ay = Math.abs(draggedDirection.y);
        	if (ax > ay) {
        		dy = 0;
        		ey = fixedCoordinate;
        	} else {
        		dx = 0;
        		ex = fixedCoordinate;
        	}
        }

        // set to the result
        result.dx = dx;
        result.dy = dy;
        result.ex = ex;
        result.ey = ey;
        result.draggedDirection = draggedDirection;
        result.fixedCoordinate = fixedCoordinate;

        return result;
    }

    /**
     * Returns the spinner model for line width.
     *
     * @return the spinner model for line width
     */
    public static SpinnerNumberModel getLineWidthSpinnerNumberModel() {
        return new SpinnerNumberModel(LINE_WIDTH_MIN_VALUE,
                LINE_WIDTH_MIN_VALUE, LINE_WIDTH_MAX_VALUE,
                LINE_WIDTH_STEP_SIZE);
    }

    /**
     * Returns the spinner model for font size.
     *
     * @return the spinner model for font size
     */
    public static SpinnerNumberModel getFontSizeSpinnerNumberModel() {
        return new SpinnerNumberModel(FONT_SIZE_MIN_VALUE, FONT_SIZE_MIN_VALUE,
                FONT_SIZE_MAX_VALUE, FONT_SIZE_STEP_VALUE);
    }

    public static Double checkEquality(final double[] values) {
    	if (values.length == 0) {
    		return null;
    	}
    	if (values.length == 1) {
    		return values[0];
    	}
    	Double value0 = values[0];
    	for (int ii = 1; ii < values.length; ii++) {
            final double value1 = values[ii];
            if (!SGUtility.equals(value0, value1)) {
            	return null;
            }
        }
    	return value0;
    }

    public static Float checkEquality(final float[] values) {
    	if (values.length == 0) {
    		return null;
    	}
    	if (values.length == 1) {
    		return values[0];
    	}
    	Float value0 = values[0];
    	for (int ii = 1; ii < values.length; ii++) {
            final float value1 = values[ii];
            if (!SGUtility.equals(value0, value1)) {
            	return null;
            }
        }
    	return value0;
    }

    public static Integer checkEquality(final int[] values) {
    	if (values.length == 0) {
    		return null;
    	}
    	if (values.length == 1) {
    		return values[0];
    	}
    	Integer value0 = values[0];
    	for (int ii = 1; ii < values.length; ii++) {
            final int value1 = values[ii];
            if (!SGUtility.equals(value0, value1)) {
            	return null;
            }
        }
    	return value0;
    }

    public static Boolean checkEquality(final boolean[] values) {
    	if (values.length == 0) {
    		return null;
    	}
    	if (values.length == 1) {
    		return values[0];
    	}
    	Boolean value0 = values[0];
    	for (int ii = 1; ii < values.length; ii++) {
            final boolean value1 = values[ii];
            if (!SGUtility.equals(value0, value1)) {
            	return null;
            }
        }
    	return value0;
    }

    public static Color checkEquality(final Color[] values) {
    	if (values.length == 0) {
    		return null;
    	}
    	if (values.length == 1) {
    		return values[0];
    	}
    	Color value0 = values[0];
    	for (int ii = 1; ii < values.length; ii++) {
            final Color value1 = values[ii];
            if (!SGUtility.equals(value0, value1)) {
            	return null;
            }
        }
    	return value0;
    }

    public static Color[] checkEquality(final Color[][] values) {
    	if (values.length == 0) {
    		return null;
    	}
    	if (values.length == 1) {
    		return values[0];
    	}
    	Color[] value0 = values[0];
    	for (int ii = 1; ii < values.length; ii++) {
            final Color[] value1 = values[ii];
            if (!SGUtility.equals(value0, value1)) {
            	return null;
            }
        }
    	return value0;
    }

    public static String checkEquality(final String[] values) {
    	if (values.length == 0) {
    		return null;
    	}
    	if (values.length == 1) {
    		return values[0];
    	}
    	String value0 = values[0];
    	for (int ii = 1; ii < values.length; ii++) {
            final String value1 = values[ii];
            if (!SGUtility.equals(value0, value1)) {
            	return null;
            }
        }
    	return value0;
    }

    public static Object checkEquality(final Object[] values) {
    	if (values.length == 0) {
    		return null;
    	}
    	if (values.length == 1) {
    		return values[0];
    	}
    	Object value0 = values[0];
    	for (int ii = 1; ii < values.length; ii++) {
            final Object value1 = values[ii];
            if (!SGUtility.equals(value0, value1)) {
            	return null;
            }
        }
    	return value0;
    }

    /**
     * Finds and returns the version number in a property file for given element.
     *
     * @param el
     *           an element
     * @return a text string of the version number or null if the version number does not exist
     */
    public static String getVersionNumber(Element el) {
    	Element property = null;
    	Element cur = el;
    	while (true) {
        	Node node = cur.getParentNode();
        	if (node == null) {
            	property = (Element) cur;
        		break;
        	}
        	if (!(node instanceof Element)) {
            	property = (Element) cur;
        		break;
        	}
        	cur = (Element) node;
    	}
    	if (property == null) {
    		return null;
    	}
    	String version = property.getAttribute(SGIPropertyFileConstants.KEY_VERSION_NUMBER);
    	return version;
    }

    /**
     * Finds and returns a sub menu of given name from a given menu.
     *
     * @param menu
     *           a menu
     * @param menuName
     *           the name of sub menu
     * @return a sub menu if it is found
     */
    public static JMenu findSubMenu(final JMenu menu, final String menuName) {
        for (int ii = 0; ii < menu.getItemCount(); ii++) {
            JMenuItem item = menu.getItem(ii);
            if (item == null) {
                continue;
            }
            if (item instanceof JMenu) {
                JMenu m = (JMenu) item;
                if (m.getText().equals(menuName)) {
                    return m;
                }
            }
        }
        return null;
    }

    /**
     * Finds and returns a menu item of given name from a given menu.
     *
     * @param menu
     *           a menu
     * @param itemName
     *           the name of menu item
     * @return a menu item if it is found
     */
    public static JMenuItem findMenuItem(final JMenu menu, final String itemName) {
        for (int ii = 0; ii < menu.getItemCount(); ii++) {
            JMenuItem item = menu.getItem(ii);
            if (item == null) {
                continue;
            }
            if (item instanceof JMenu) {
                JMenu subMenu = (JMenu) item;
                JMenuItem subMenuItem = findMenuItem(subMenu, itemName);
                if (subMenuItem != null) {
                    return subMenuItem;
                }
            } else {
                if (item.getText().equals(itemName)) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Finds and returns a menu of given name from given pop up menu.
     *
     * @param pMenu
     *           the pop up menu
     * @param menuName
     *           the name of menu
     * @return a menu if it is found
     */
    public static JMenu findMenu(JPopupMenu pMenu, final String menuName) {
    	Component[] components = pMenu.getComponents();
        for (int ii = 0; ii < components.length; ii++) {
        	if (components[ii] instanceof JMenu) {
                JMenu menu = (JMenu) components[ii];
                String text = menu.getText();
                if (text.equals(menuName)) {
                    return menu;
                }
        	}
        }
        return null;
    }

    /**
     * Finds and returns a menu item of given name from given pop up menu.
     * under the menu of given name.
     *
     * @param pMenu
     *           the pop up menu
     * @param menuName
     *           the name of menu
     * @param itemName
     *           the name of item
     * @return a menu if it is found
     */
    public static JMenuItem findMenuItem(JPopupMenu pMenu, final String menuName,
    		final String itemName) {
        JMenu menu = findMenu(pMenu, menuName);
        if (menu == null) {
            return null;
        }
        return SGUtility.findMenuItem(menu, itemName);
    }

    /**
     * Finds and returns a menu item of given name from given pop up menu.
     *
     * @param pMenu
     *           the pop up menu
     * @param itemName
     *           the name of item
     * @return a menu if it is found
     */
    public static JMenuItem findMenuItem(JPopupMenu pMenu, final String itemName) {
    	Component[] components = pMenu.getComponents();
        for (int ii = 0; ii < components.length; ii++) {
        	if (components[ii] instanceof JMenuItem) {
        		JMenuItem menu = (JMenuItem) components[ii];
                String text = menu.getText();
                if (text.equals(itemName)) {
                    return menu;
                }
        	}
        }
        return null;
    }

    /**
     * Adds menu items to arrange objects.
     *
     * @param p
     *          the pop up menu
     * @param l
     *          the action listener
     */
    public static void addArrangeItems(JPopupMenu p, ActionListener l) {
        JMenu arrangeMenu = SGUtility.addMenu(p, l, MENUCMD_ARRANGE);
        SGUtility.addItem(arrangeMenu, l, MENUCMD_BRING_TO_FRONT);
        SGUtility.addItem(arrangeMenu, l, MENUCMD_BRING_FORWARD);
        SGUtility.addItem(arrangeMenu, l, MENUCMD_SEND_BACKWARD);
        SGUtility.addItem(arrangeMenu, l, MENUCMD_SEND_TO_BACK);
    }

    /**
     * Creates and returns the array indices within given array length.
     *
     * @param stride
     *           the stride of array
     * @param len
     *           length of the array
     * @return created indices
     */
    public static SGIntegerSeriesSet createIndicesWithinRange(final SGIntegerSeriesSet stride,
    		final int len) {

    	SGIntegerSeriesSet strideNew = (SGIntegerSeriesSet) stride.clone();

    	// adds the alias
    	strideNew.addAlias(len - 1, SGIntegerSeries.ARRAY_INDEX_END);

		int[] numArray = strideNew.getNumbers();
		for (int ii = 0; ii < numArray.length; ii++) {
			if (numArray[ii] >= len) {
				return SGIntegerSeriesSet.createInstance(len);
			}
		}
		return strideNew;
    }

	/**
	 * Creates a text string from a given byte array.
	 *
	 * @param byteArray
	 *           a byte array
	 * @return a text string
	 * @throws UnsupportedEncodingException
	 */
	public static String createString(byte[] byteArray) throws UnsupportedEncodingException {
    	return new String(byteArray, CHAR_SET_NAME_UTF8).trim();
	}

	/**
	 * Returns the nearest index in given index array to given index.
	 *
	 * @param value
	 *           the index
	 * @param array
	 *           an array of integer values
	 * @return the nearest value
	 */
	public static int findNearestValue(final int value, final int[] array) {
		if (array.length == 0) {
			return -1;
		}
		if (array.length == 1) {
			return array[0];
		}
		int bIndex = Arrays.binarySearch(array, value);
		if (bIndex >= 0) {
			return value;
		} else {
			bIndex = - bIndex - 1;
			if (bIndex == 0 || bIndex == array.length - 1) {
				return array[bIndex];
			} else if (bIndex == array.length) {
				return array[array.length - 1];
			} else {
				final int valuePrev = array[bIndex - 1];
				final int diffPrev = Math.abs(valuePrev - value);
				final int valueNext = array[bIndex];
				final int diffNext = Math.abs(valueNext - value);
				return (diffNext <= diffPrev) ? valueNext : valuePrev;
			}
		}
	}

	/**
	 * Creates an image icon.
	 * 
	 * @param comp
	 *          a component
	 * @param name
	 *          the name of icon resource
	 * @return created image icon
	 */
	public static ImageIcon createIcon(Component comp, final String name) {
		return createIcon(comp.getClass(), comp, name);
	}

	/**
	 * Creates an image icon.
	 * 
	 * @param cl
	 *          the class object to load the resource
	 * @param comp
	 *          a component
	 * @param name
	 *          the name of icon resource
	 * @return created image icon
	 */
	public static ImageIcon createIcon(Class<?> cl, Component comp, final String name) {
		URL url = cl.getResource(
				SGIConstants.RESOURCES_DIRNAME + name);
		ImageIcon icon = null;
        if (url != null) {
            icon = new ImageIcon(url);
        } else {
            icon = new ImageIcon(name);
        }
        if (comp != null) {
            // setup the media tracker
        	setupMediaTracker(comp, icon.getImage());
        }
		return icon;
	}
	
	/**
	 * Creates an image.
	 * 
	 * @param cl
	 *          the class object to load the resource
	 * @param comp
	 *          a component
	 * @param name
	 *          the name of image resource
	 * @return created image
	 */
	public static Image createImage(Class<?> cl, Component comp, String name) {
        URL url = cl.getResource(
        		SGIConstants.RESOURCES_DIRNAME + name);
        Image image = Toolkit.getDefaultToolkit().getImage(url);
        if (comp != null) {
            // setup the media tracker
        	setupMediaTracker(comp, image);
        }
        return image;
	}

	/**
	 * Creates an image.
	 * 
	 * @param comp
	 *          a component
	 * @param name
	 *          the name of image resource
	 * @return created image
	 */
	public static Image createImage(Component comp, String name) {
		return createImage(comp.getClass(), comp, name);
	}
	
	private static void setupMediaTracker(Component comp, Image image) {
        // set the media tracker
        MediaTracker mt = new MediaTracker(comp);
        mt.addImage(image, 0);
        try {
            mt.waitForAll();
        } catch (InterruptedException ex) {
        }
	}
	
    /**
     * Returns the map that contains given objects as keys and the number of each values as values.
     * 
     * @param objArray
     *           the object array
     * @return created map
     */
    public static Map<Object, Integer> getCntMap(Object[] objArray) {
    	return getCntMap(Arrays.asList(objArray));
    }

    /**
     * Returns the map that contains given objects as keys and the number of each values as values.
     * 
     * @param objList
     *           the object list
     * @return created map
     */
    public static Map<Object, Integer> getCntMap(List<Object> objList) {
		Map<Object, Integer> cntMap = new HashMap<Object, Integer>();
		for (Object obj : objList) {
			Integer cnt = cntMap.get(obj);
			if (cnt == null) {
				cnt = new Integer(1);
			} else {
				cnt = new Integer(cnt + 1);
			}
			cntMap.put(obj, cnt);
		}
    	return cntMap;
    }

    /**
     * Finds the most frequent existing objects in given objects and returns the list of them.
     * 
     * @param objArray
     *           the object array
     * @return list of the most frequent objects
     */
    public static List<Object> findMostFrequentObjects(Object[] objArray) {
    	return findMostFrequentObjects(Arrays.asList(objArray));
    }

    /**
     * Finds the most frequent existing objects in given objects and returns the list of them.
     * 
     * @param objList
     *           the object list
     * @return list of the most frequent objects
     */
    public static List<Object> findMostFrequentObjects(List<Object> objList) {
		Map<Object, Integer> cntMap = getCntMap(objList);
		int maxCnt = -1;
		List<Object> maxObjList = new ArrayList<Object>();
		Iterator<Entry<Object, Integer>> itr = cntMap.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<Object, Integer> entry = itr.next();
			Object obj = entry.getKey();
			Integer cnt = entry.getValue();
			if (cnt > maxCnt) {
				maxCnt = cnt;
				maxObjList.clear();
				maxObjList.add(obj);
			} else if (cnt == maxCnt) {
				maxObjList.add(obj);
			}
		}
    	return maxObjList;
    }

    /**
     * Shows the color selection dialog for given color selection button.
     * 
     * @param colorDialog
     *           a color selection dialog
     * @param btn
     *           a color selection button
     * @param cl
     *           initial color
     * @param x
     *           x-coordinate of the dialog
     * @param y
     *           y-coordinate of the dialog
     */
    public static void showColorSelectionDialog(SGColorDialog colorDialog, 
    		SGColorSelectionButton btn, Color cl, final int x, final int y) {

        // set currently focused color button
    	if (btn != null) {
            btn.setFocused(true);
    	}

    	// set the current color to the dialog
    	colorDialog.setSelectedColor(cl);

        if (!colorDialog.isVisible()) {
            // set the location of color dialog
            colorDialog.setLocation(x, y);

            // show color dialog
            colorDialog.setVisible(true);
        }
    }

    /**
     * Creates a text string for a menu item for the plug-in.
     * 
     * @param lib
     *           the plug-in
     * @return a text string for a menu item for the plug-in
     */
    public static String createPluginItemString(SGIPlugin lib) {
    	return lib.getName();
    }
    
    /**
     * Checks whether given array has overlapping elements.
     * 
     * @param objects
     *            an array
     * @return true if there are no overlapping elements
     */
    public static boolean checkOverlapping(final Object[] objects) {
    	Set<Object> objSet = new HashSet<Object>();
    	for (int ii = 0; ii < objects.length; ii++) {
    		objSet.add(objects[ii]);
    	}
    	return (objSet.size() == objects.length);
    }
    
    /**
     * Checks whether given array has overlapping elements.
     * 
     * @param objects
     *            an array
     * @return true if there are no overlapping elements
     */
    public static boolean checkOverlapping(final int[] values) {
    	Integer[] objects = new Integer[values.length];
    	for (int ii = 0; ii < objects.length; ii++) {
    		objects[ii] = values[ii];
    	}
    	return checkOverlapping(objects);
    }

    public static boolean isAlphabetic(final char c) {
    	return (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z'));
    }

    public static boolean isDigit(final char c) {
    	return ('0' <= c && c <= '9');
    }

    public static double[][] transpose(double[][] array) {
    	if (array == null) {
    		throw new IllegalArgumentException("array == null");
    	}
    	if (array.length == 0) {
    		throw new IllegalArgumentException("array.length == 0");
    	}
    	int len = -1;
    	for (int ii = 0; ii < array.length; ii++) {
    		if (array[ii] == null) {
        		throw new IllegalArgumentException("array[" + ii + "] == null");
    		}
    		if (len == -1) {
    			len = array[ii].length;
    			if (len == 0) {
    	    		throw new IllegalArgumentException("array[" + ii + "].length == 0");
    			}
    		} else {
    			if (array[ii].length != len) {
    				throw new IllegalArgumentException("array[" + ii + "].length != len: " 
    						+ array[ii].length + ", " + len);
    			}
    		}
    	}
    	double[][] ret = new double[len][array.length];
    	for (int ii = 0; ii < len; ii++) {
    		for (int jj = 0; jj < array.length; jj++) {
    			ret[ii][jj] = array[jj][ii];
    		}
    	}
    	return ret;
    }

    /**
     * Calculates and returns line width to export from given line width.
     * 
     * @param lw
     *           line width in units of LINE_WIDTH_UNIT
     * @return line width to export
     */
    public static float getExportLineWidth(final float lw) {
        final int digit = SGIConstants.LINE_WIDTH_MINIMAL_ORDER - 1;
    	return (float) SGUtilityNumber.roundOffNumber(lw, digit);
    }
    
    /**
     * Calculates and returns font size to export from given font size.
     * 
     * @param size
     *           font size in units of FONT_SIZE_UNIT
     * @return font size to export
     */
    public static float getExportFontSize(final float size) {
        final int digit = SGIConstants.FONT_SIZE_MINIMAL_ORDER - 1;
    	return (float) SGUtilityNumber.roundOffNumber(size, digit);
    }

    /**
     * Calculates and returns value to export from given value and the order.
     * 
     * @param value
     *           a value in arbitrary unit
     * @param order
     *           the minimal order
     * @return value to export
     */
    public static float getExportValue(final float value, final int order) {
        final int digitSpace = order - 1;
        return (float) SGUtilityNumber.roundOffNumber(value, digitSpace);
    }
    
    public static Number getAxisNumber(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return null;
        }
        
        // parses as a double value
        Number ret = SGUtilityText.getDouble(str);
        if (ret != null) {
        	return ret;
        }
        
        // parses as a date value
        try {
			SGDate date = new SGDate(str);
			ret = date.getDateValue();
		} catch (ParseException e) {
		}
        return ret;
    }

    public static SGAxisValue getAxisValue(final String str, 
    		final boolean dateMode) throws ParseException {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return null;
        }
        
        if (dateMode) {
            // parses as a date value
    		SGAxisDateValue dateValue = null;
            try {
            	dateValue = new SGAxisDateValue(str);
    		} catch (ParseException e) {
    			throw e;
    		}
    		return dateValue;
        } else {
            // parses as a double value
            Double d = SGUtilityText.getDouble(str);
            if (d == null) {
            	throw new ParseException("Invalid input: " + d, 0);
            }
        	return new SGAxisDoubleValue(d);
        }
    }

    public static SGAxisStepValue getAxisStepValue(final String str,
    		final boolean dateMode) throws ParseException {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return null;
        }
        
        if (dateMode) {
        	// parses as a date value
    		SGAxisDateStepValue p = null;
    		try {
    			p = new SGAxisDateStepValue(str);
    		} catch (ParseException e) {
    			throw e;
    		}
    		return p;
        } else {
            // parses as a double value
            Double d = SGUtilityText.getDouble(str);
            if (d == null) {
            	throw new ParseException("Invalid input: " + d, 0);
            }
            return new SGAxisDoubleStepValue(d);
        }
    }
}

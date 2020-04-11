package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public abstract class SGPopupMenu extends JPopupMenu implements ActionListener, SGIConstants {

	private static final long serialVersionUID = 4221949302356042792L;

	public SGPopupMenu() {
		super();
	}

	public SGPopupMenu(String label) {
		super(label);
	}
	
    /**
     * Adds a menu item.
     *
     * @param cmd
     *            a command to be added
     * @return added menu item
     */
    public JMenuItem addItem(String cmd) {
        final JMenuItem item = new JMenuItem(cmd);
        this.add(item);
        return item;
    }

    /**
     * Add a menu command to the menu item.
     *
     * @param parent
     *            the parent menu item
     * @param cmd
     *            a command to be added
     * @return added menu item
     */
    public JMenuItem addItem(JMenuItem parent, String cmd) {
        final JMenuItem item = new JMenuItem(cmd);
        parent.add(item);
        return item;
    }


    /**
     * Add a menu command with the check box.
     *
     * @param cmd
     *          a command to be added
     * @param enabled
     *            a flag whether the command is enabled
     * @return added menu item
     */
    public JCheckBoxMenuItem addCheckBoxItem(String cmd) {
        final SGCheckBoxMenuItem item = new SGCheckBoxMenuItem(cmd);
        this.add(item);
        return item;
    }

    /**
     * Add a menu command with the check box to the menu.
     *
     * @param parent
     *          the parent menu item
     * @param cmd
     *          a command to be added
     * @return added menu item
     */
    public JCheckBoxMenuItem addCheckBoxItem(JMenuItem parent, String cmd) {
        final SGCheckBoxMenuItem item = new SGCheckBoxMenuItem(cmd);
        parent.add(item);
        return item;
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
    public JMenu addMenu(String cmd) {
        final JMenu menu = new JMenu(cmd);
        this.add(menu);
        return menu;
    }

    /**
     * Finds and returns a menu of given name.
     *
     * @param menuName
     *           the name of menu
     * @return a menu if it is found
     */
    public JMenu findMenu(final String menuName) {
    	Component[] components = this.getComponents();
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
     * Finds and returns a menu item of given name under the menu of given name.
     *
     * @param menuName
     *           the name of menu
     * @param itemName
     *           the name of item
     * @return a menu if it is found
     */
    public JMenuItem findMenuItem(final String menuName, final String itemName) {
        JMenu menu = SGUtility.findMenu(this, menuName);
        if (menu == null) {
            return null;
        }
        return SGUtility.findMenuItem(menu, itemName);
    }

    /**
     * Finds and returns a menu item of given name.
     *
     * @param itemName
     *           the name of item
     * @return a menu if it is found
     */
    public JMenuItem findMenuItem(final String itemName) {
    	Component[] components = this.getComponents();
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

    protected void addArrangeItems() {
        JMenu arrangeMenu = this.addMenu(MENUCMD_ARRANGE);
        this.addItem(arrangeMenu, MENUCMD_BRING_TO_FRONT);
        this.addItem(arrangeMenu, MENUCMD_BRING_FORWARD);
        this.addItem(arrangeMenu, MENUCMD_SEND_BACKWARD);
        this.addItem(arrangeMenu, MENUCMD_SEND_TO_BACK);
    }

    protected void addActionListener(ActionListener l) {
    	Component[] components = this.getComponents();
    	for (Component com : components) {
    		if (com instanceof JMenuItem) {
    			JMenuItem item = (JMenuItem) com;
    			this.addActionListener(item, l);
    		}
    	}
    }
    
    private void addActionListener(JMenuItem item, ActionListener l) {
		item.addActionListener(l);
		if (item instanceof JMenu) {
			JMenu menu = (JMenu) item;
	        for (int ii = 0; ii < menu.getItemCount(); ii++) {
	            JMenuItem subItem = menu.getItem(ii);
	            if (subItem == null) {
	            	continue;
	            }
	            this.addActionListener(subItem, l);
	        }
		}
    }
    
    protected void removeActionListener(ActionListener l) {
    	Component[] components = this.getComponents();
    	for (Component com : components) {
    		if (com instanceof JMenuItem) {
    			JMenuItem item = (JMenuItem) com;
    			this.removeActionListener(item, l);
    		}
    	}
    }
    
    private void removeActionListener(JMenuItem item, ActionListener l) {
		item.addActionListener(l);
		if (item instanceof JMenu) {
			JMenu menu = (JMenu) item;
	        for (int ii = 0; ii < menu.getItemCount(); ii++) {
	            JMenuItem subItem = menu.getItem(ii);
	            if (subItem == null) {
	            	continue;
	            }
	            this.removeActionListener(subItem, l);
	        }
		}
    }

}

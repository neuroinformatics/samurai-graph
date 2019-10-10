package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow.NodeMenuItem;

/**
 * A menu bar.
 * 
 */
public class SGMenuBar extends JMenuBar implements ActionListener,
        MenuListener, SGIRootObjectConstants {

    /**
     * 
     */
    private static final long serialVersionUID = 7504775458155768651L;

    /**
     * 
     */
    public SGMenuBar() {
        super();

        this.create();
    }

    /**
     * 
     */
    private boolean create() {
        JMenuBar menuBar = this;

        final int shortcutmask = Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask();
        // final int ctrl = ActionEvent.CTRL_MASK;
        final int shift = ActionEvent.SHIFT_MASK;
        final int alt = ActionEvent.ALT_MASK;

        // File
        {
            final JMenu menuFile = new JMenu(MENUBAR_FILE);
            menuFile.setMnemonic(KeyEvent.VK_F);
            menuBar.add(menuFile);
            
            menuFile.addMenuListener(this);

            // open window
            this.createMenuItem(menuFile, MENUBARCMD_CREATE_NEW_WINDOW, this,
                    KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_N,
                            shortcutmask), true);

            // close window
            this.createMenuItem(menuFile, MENUBARCMD_CLOSE_WINDOW, this,
                    KeyEvent.VK_W, KeyStroke.getKeyStroke(KeyEvent.VK_W,
                            shortcutmask), true);

            menuFile.addSeparator();

            // draw graph
            this.createMenuItem(menuFile, MENUBARCMD_DRAW_GRAPH, this,
                    KeyEvent.VK_D, KeyStroke.getKeyStroke(KeyEvent.VK_O,
                            shortcutmask), true);
            
            menuFile.addSeparator();

            // reload
            this.createMenuItem(menuFile, MENUBARCMD_RELOAD, this,
                    KeyEvent.VK_R, KeyStroke.getKeyStroke(KeyEvent.VK_R,
                            shortcutmask), true);

            menuFile.addSeparator();

            // load property
            this.createMenuItem(menuFile, MENUBARCMD_LOAD_PROPERTY, this,
                    KeyEvent.VK_L, null, true);

            // save property
            this.createMenuItem(menuFile, MENUBARCMD_SAVE_PROPERTY, this,
                    KeyEvent.VK_S, null, true);

            menuFile.addSeparator();

            // load dataset
            this.createMenuItem(menuFile, MENUBARCMD_LOAD_DATASET, this, -1,
                    null, true);

            // save dataset
            this.createMenuItem(menuFile, MENUBARCMD_SAVE_DATASET, this, -1,
                    null, true);

            menuFile.addSeparator();
            
            // load script
            this.createMenuItem(menuFile, MENUBARCMD_LOAD_SCRIPT, this, -1, 
            		null, true);
            
            // save as script
            this.createMenuItem(menuFile, MENUBARCMD_SAVE_AS_SCRIPT, this, -1, 
            		null, true);

            menuFile.addSeparator();

            // load background image
            this.createMenuItem(menuFile, MENUBARCMD_LOAD_BACKGROUND_IMAGE,
                    this, KeyEvent.VK_I, null, true);

            menuFile.addSeparator();

            // export as image
            this.createMenuItem(menuFile, MENUBARCMD_EXPORT_AS_IMAGE, this,
                    KeyEvent.VK_E, KeyStroke.getKeyStroke(KeyEvent.VK_E,
                            shortcutmask), false);

            // print
            this.createMenuItem(menuFile, MENUBARCMD_PRINT, this,
                    KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_P,
                            shortcutmask), false);

            menuFile.addSeparator();

            // exit
            this.createMenuItem(menuFile, MENUBARCMD_EXIT, this, KeyEvent.VK_X,
                    KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcutmask), true);

        }

        // Edit
        {
            final JMenu menuEdit = new JMenu(MENUBAR_EDIT);
            menuEdit.setMnemonic(KeyEvent.VK_E);
            menuBar.add(menuEdit);

            menuEdit.addMenuListener(this);
            
            // undo
            this.createMenuItem(menuEdit, MENUBARCMD_UNDO, this, KeyEvent.VK_U,
                    KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortcutmask), false);

            // redo
            this.createMenuItem(menuEdit, MENUBARCMD_REDO, this, KeyEvent.VK_R,
                    KeyStroke.getKeyStroke(KeyEvent.VK_Y, shortcutmask), false);

            menuEdit.addSeparator();

            // clear undo buffer
            this.createMenuItem(menuEdit, MENUBARCMD_CLEAR_UNDO_BUFFER, this,
                    KeyEvent.VK_B, null, true);

            menuEdit.addSeparator();

            // cut
            this.createMenuItem(menuEdit, MENUBARCMD_CUT, this, KeyEvent.VK_T,
                    KeyStroke.getKeyStroke(KeyEvent.VK_X, shortcutmask), false);

            // copy
            this.createMenuItem(menuEdit, MENUBARCMD_COPY, this, KeyEvent.VK_C,
                    KeyStroke.getKeyStroke(KeyEvent.VK_C, shortcutmask), false);

            // paste
            this.createMenuItem(menuEdit, MENUBARCMD_PASTE, this,
                    KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_V,
                            shortcutmask), false);

            menuEdit.addSeparator();

            // delete
            this.createMenuItem(menuEdit, MENUBARCMD_DELETE, this,
                    KeyEvent.VK_D, KeyStroke
                            .getKeyStroke(KeyEvent.VK_DELETE, 0), false);

            /*
             * final JMenuItem selectAll = new JMenuItem( MENUBARCMD_SELECT_ALL );
             * selectAll.setActionCommand(MENUBARCMD_SELECT_ALL);
             * selectAll.addActionListener(this); selectAll.setAccelerator(
             * KeyStroke.getKeyStroke(KeyEvent.VK_A, ctrl));
             * selectAll.setMnemonic( KeyEvent.VK_A ); menuEdit.add(selectAll);
             * selectAll.setEnabled(false);
             */

            // duplicate
            this.createMenuItem(menuEdit, MENUBARCMD_DUPLICATE, this,
                    KeyEvent.VK_I, KeyStroke.getKeyStroke(KeyEvent.VK_D,
                            shortcutmask), false);

            menuEdit.addSeparator();

            // delete background image
            this.createMenuItem(menuEdit, MENUBARCMD_DELETE_BACKGROUND_IMAGE,
                    this, KeyEvent.VK_M, null, false);

        }

        // Insert
        {
            final JMenu menuInsert = new JMenu(MENUBAR_INSERT);
            menuInsert.setMnemonic(KeyEvent.VK_I);
            menuBar.add(menuInsert);

            menuInsert.addMenuListener(this);
            
            // label
            JMenuItem label = this.createToggleMenuItem(menuInsert,
                    MENUBARCMD_INSERT_LABEL, this, KeyEvent.VK_L, null, false);

            // significant difference
            JMenuItem sigDiff = this.createToggleMenuItem(menuInsert,
                    MENUBARCMD_INSERT_SIG_DIFF_SYMBOL, this, KeyEvent.VK_D,
                    null, false);

            // axis break
            JMenuItem axisBreak = this.createToggleMenuItem(menuInsert,
                    MENUBARCMD_INSERT_AXIS_BREAK_SYMBOL, this, KeyEvent.VK_B,
                    null, false);

            // timing line
            JMenuItem timingLine = this.createToggleMenuItem(menuInsert,
                    MENUBARCMD_INSERT_TIMING_LINE, this, KeyEvent.VK_T, null,
                    false);

            // rectangle
            JMenuItem rectangle = this.createToggleMenuItem(menuInsert,
                    MENUBARCMD_INSERT_RECTANGLE, this, KeyEvent.VK_R, null,
                    false);

            // ellipse
            JMenuItem ellipse = this
                    .createToggleMenuItem(menuInsert,
                            MENUBARCMD_INSERT_ELLIPSE, this, KeyEvent.VK_E,
                            null, false);

            // arrow
            JMenuItem arrow = this.createToggleMenuItem(menuInsert,
                    MENUBARCMD_INSERT_ARROW, this, KeyEvent.VK_A, null, false);

            // line
            JMenuItem line = this.createToggleMenuItem(menuInsert,
                    MENUBARCMD_INSERT_LINE, this, KeyEvent.VK_I, null, false);

            //
            SGButtonGroup bg = new SGButtonGroup();
            bg.add(label);
            bg.add(sigDiff);
            bg.add(axisBreak);
            bg.add(timingLine);
            bg.add(rectangle);
            bg.add(ellipse);
            bg.add(arrow);
            bg.add(line);
        }

        // Layout
        {
            final JMenu menuLayout = new JMenu(MENUBAR_LAYOUT);
            menuLayout.setMnemonic(KeyEvent.VK_L);
            menuBar.add(menuLayout);

            menuLayout.addMenuListener(this);
            
            // Paper Size
            {
                final JMenu menuPaperSize = new JMenu(MENUBAR_PAPER_SIZE);
                menuPaperSize.setMnemonic(KeyEvent.VK_P);
                menuLayout.add(menuPaperSize);

//                // A4
//                this.createMenuItem(menuPaperSize, MENUBARCMD_PAPER_A4_SIZE,
//                        this, KeyEvent.VK_4, null, true);
//
//                // B5
//                this.createMenuItem(menuPaperSize, MENUBARCMD_PAPER_B5_SIZE,
//                        this, KeyEvent.VK_5, null, true);
//
//                // US Letter
//                this.createMenuItem(menuPaperSize,
//                        MENUBARCMD_PAPER_USLETTER_SIZE, this, KeyEvent.VK_U,
//                        null, true);
//
//                menuPaperSize.addSeparator();
//
//                // portrait
//                JRadioButtonMenuItem portrait = (JRadioButtonMenuItem) this
//                        .createRadioButtonMenuItem(menuPaperSize,
//                                MENUBARCMD_PAPER_PORTRAIT, this, KeyEvent.VK_P,
//                                null, true);
//
//                // landscape
//                JRadioButtonMenuItem landscape = (JRadioButtonMenuItem) this
//                        .createRadioButtonMenuItem(menuPaperSize,
//                                MENUBARCMD_PAPER_LANDSCAPE, this,
//                                KeyEvent.VK_L, null, true);

                // A4 Portrait
                this.createMenuItem(menuPaperSize, MENUBARCMD_PAPER_A4_PORTRAIT,
                        this, KeyEvent.VK_A, null, true);

                // B5 Portrait
                this.createMenuItem(menuPaperSize, MENUBARCMD_PAPER_B5_PORTRAIT,
                        this, KeyEvent.VK_B, null, true);

                // US Letter Portrait
                this.createMenuItem(menuPaperSize,
                        MENUBARCMD_PAPER_USLETTER_PORTRAIT, this, KeyEvent.VK_U,
                        null, true);

                menuPaperSize.addSeparator();
                
                // A4 Landscape
                this.createMenuItem(menuPaperSize, MENUBARCMD_PAPER_A4_LANDSCAPE,
                        this, KeyEvent.VK_4, null, true);

                // B5 Landscape
                this.createMenuItem(menuPaperSize, MENUBARCMD_PAPER_B5_LANDSCAPE,
                        this, KeyEvent.VK_5, null, true);

                // US Letter Landscape
                this.createMenuItem(menuPaperSize,
                        MENUBARCMD_PAPER_USLETTER_LANDSCAPE, this, KeyEvent.VK_S,
                        null, true);

//                // create a button-group
//                ButtonGroup bGroup = new ButtonGroup();
//                bGroup.add(portrait);
//                bGroup.add(landscape);

                menuPaperSize.addSeparator();

                // bounding box
                this.createMenuItem(menuPaperSize, MENUBARCMD_BOUNDING_BOX,
                        this, KeyEvent.VK_O, null, true);

                // user customize
                this.createMenuItem(menuPaperSize,
                        MENUBARCMD_PAPER_USER_CUSTOMIZE, this, KeyEvent.VK_C,
                        null, true);

            }

            menuLayout.addSeparator();

            // Tool Bar
            {
                final JMenu menuToolBar = new JMenu(MENUBAR_TOOL_BAR);
                menuToolBar.setMnemonic(KeyEvent.VK_T);
                menuLayout.add(menuToolBar);

                JCheckBoxMenuItem item;

                // File
                item = this.createCheckBoxMenuItem(menuToolBar,
                        MENUBARCMD_VISIBLE_FILE, this, -1, null, true);
                item.setSelected(true);

                // Edit
                item = this.createCheckBoxMenuItem(menuToolBar,
                        MENUBARCMD_VISIBLE_EDIT, this, -1, null, true);
                item.setSelected(true);

                // Insert
                item = this.createCheckBoxMenuItem(menuToolBar,
                        MENUBARCMD_VISIBLE_INSERT, this, -1, null, true);
                item.setSelected(true);

                // Layout
                item = this.createCheckBoxMenuItem(menuToolBar,
                        MENUBARCMD_VISIBLE_LAYOUT, this, -1, null, true);
                item.setSelected(true);

                // // Help
                // item = this.createCheckBoxMenuItem(
                // menuToolBar, MENUBARCMD_VISIBLE_HELP, this, -1, null, true );
                // item.setSelected(true);

                // Zoom
                item = this.createCheckBoxMenuItem(menuToolBar,
                        MENUBARCMD_VISIBLE_ZOOM, this, -1, null, true);
                item.setSelected(true);
            }

            menuLayout.addSeparator();

            // Grid
            {
                final JMenu menuGrid = new JMenu(MENUBAR_GRID);
                menuGrid.setMnemonic(KeyEvent.VK_G);
                menuLayout.add(menuGrid);

                // plus grid
                this.createMenuItem(menuGrid, MENUBARCMD_PLUS_GRID, this,
                        KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_I,
                                shift + alt), true);

                // minus grid
                this.createMenuItem(menuGrid, MENUBARCMD_MINUS_GRID, this,
                        KeyEvent.VK_M, KeyStroke.getKeyStroke(KeyEvent.VK_D,
                                shift + alt), true);

                // grid visible
                this.createCheckBoxMenuItem(menuGrid, MENUBARCMD_GRID_VISIBLE,
                        this, KeyEvent.VK_V, KeyStroke.getKeyStroke(
                                KeyEvent.VK_G, shift + alt), true);

                // snap to grid
                this.createCheckBoxMenuItem(menuGrid, MENUBARCMD_SNAP_TO_GRID,
                        this, KeyEvent.VK_S, KeyStroke.getKeyStroke(
                                KeyEvent.VK_S, shift + alt), true);

            }

            menuLayout.addSeparator();

            // Zoom
            {
                final JMenu menuZoom = new JMenu(MENUBAR_ZOOM);
                menuZoom.setMnemonic(KeyEvent.VK_Z);
                menuLayout.add(menuZoom);

                // zoom in
                this.createMenuItem(menuZoom, MENUBARCMD_ZOOM_IN, this,
                        KeyEvent.VK_I, KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                                shift + alt), true);

                // zoom out
                JMenuItem zoomOut = this
                        .createMenuItem(menuZoom, MENUBARCMD_ZOOM_OUT, this,
                                KeyEvent.VK_O, KeyStroke.getKeyStroke(
                                        KeyEvent.VK_O, shift + alt), true);
                zoomOut.setDisplayedMnemonicIndex(5);

                // default zoom
                this.createMenuItem(menuZoom, MENUBARCMD_DEFAULT_ZOOM, this,
                        KeyEvent.VK_D, null, true);

                // zoom way out
                this.createMenuItem(menuZoom, MENUBARCMD_ZOOM_WAY_OUT, this,
                        KeyEvent.VK_W, null, true);

                // auto zoom
                this.createCheckBoxMenuItem(menuZoom, MENUBARCMD_AUTO_ZOOM,
                        this, KeyEvent.VK_A, null, true);

            }

            menuLayout.addSeparator();

            // lock
            this.createCheckBoxMenuItem(menuLayout, MENUBARCMD_LOCK, this,
                    KeyEvent.VK_O, null, true);

        }

        // Arrange
        {
            final JMenu menuArrange = new JMenu(MENUBAR_ARRANGE);
            menuArrange.setMnemonic(KeyEvent.VK_A);
            menuBar.add(menuArrange);

            menuArrange.addMenuListener(this);
            
            // bring to front
            this.createMenuItem(menuArrange, MENUBARCMD_BRING_TO_FRONT, this,
                    KeyEvent.VK_F, KeyStroke.getKeyStroke(KeyEvent.VK_F,
                            shortcutmask), false);
            
            // bring forward
            this.createMenuItem(menuArrange, MENUBARCMD_BRING_FORWARD, this,
                    KeyEvent.VK_B, KeyStroke.getKeyStroke(KeyEvent.VK_B,
                            shortcutmask), false);

            // send backward
            this.createMenuItem(menuArrange, MENUBARCMD_SEND_BACKWARD, this,
                    KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S,
                            shortcutmask), false);
            
            // send to back
            this.createMenuItem(menuArrange, MENUBARCMD_SEND_TO_BACK, this,
                    KeyEvent.VK_K, KeyStroke.getKeyStroke(KeyEvent.VK_K,
                            shortcutmask), false);

            menuArrange.addSeparator();

            // // mode
            // this.createCheckBoxMenuItem(
            // menuArrange, MENUBARCMD_MODE, this, KeyEvent.VK_M,
            // null, true );
            //
            // menuArrange.addSeparator();

            // align figures
            this.createMenuItem(menuArrange, MENUBARCMD_AUTO_ARRANGEMENT, this,
                    KeyEvent.VK_R, null, true);

            /*
             * final JMenu menuAlignObjects = new JMenu(MENUBAR_ALIGN_OBJECTS);
             * menuAlignObjects.addMenuListener(this);
             * menuAlignObjects.setMnemonic( KeyEvent.VK_O ); menuArrange.add(
             * menuAlignObjects ); { final JMenuItem left = new
             * JMenuItem(MENUBARCMD_ALIGN_LEFT);
             * left.setActionCommand(MENUBARCMD_ALIGN_LEFT);
             * left.addActionListener(this); left.setMnemonic( KeyEvent.VK_L );
             * menuAlignObjects.add(left);
             * 
             * final JMenuItem center = new JMenuItem(MENUBARCMD_ALIGN_CENTER);
             * center.setActionCommand(MENUBARCMD_ALIGN_CENTER);
             * center.addActionListener(this); center.setMnemonic( KeyEvent.VK_C );
             * menuAlignObjects.add(center);
             * 
             * final JMenuItem right = new JMenuItem(MENUBARCMD_ALIGN_RIGHT);
             * right.setActionCommand(MENUBARCMD_ALIGN_RIGHT);
             * right.addActionListener(this); right.setMnemonic( KeyEvent.VK_R );
             * menuAlignObjects.add(right);
             * 
             * final JMenuItem top = new JMenuItem(MENUBARCMD_ALIGN_TOP);
             * top.setActionCommand(MENUBARCMD_ALIGN_TOP);
             * top.addActionListener(this); top.setMnemonic( KeyEvent.VK_T );
             * menuAlignObjects.add(top);
             * 
             * final JMenuItem middle = new JMenuItem(MENUBARCMD_ALIGN_MIDDLE);
             * middle.setActionCommand(MENUBARCMD_ALIGN_MIDDLE);
             * middle.addActionListener(this); middle.setMnemonic( KeyEvent.VK_M );
             * menuAlignObjects.add(middle);
             * 
             * final JMenuItem bottom = new JMenuItem(MENUBARCMD_ALIGN_BOTTOM);
             * bottom.setActionCommand(MENUBARCMD_ALIGN_BOTTOM);
             * bottom.addActionListener(this); bottom.setMnemonic( KeyEvent.VK_B );
             * menuAlignObjects.add(bottom);
             *  }
             */

        }

        // Properties
        {
            final JMenu menuProperties = new JMenu(MENUBAR_PROPERTIES);
            menuProperties.setMnemonic(KeyEvent.VK_P);
            menuBar.add(menuProperties);

            menuProperties.addMenuListener(this);
        }

        // Plug-in
        {
            final JMenu menuPlugin = new JMenu(MENUBAR_PLUGIN);
            menuPlugin.setMnemonic(KeyEvent.VK_L);
            menuBar.add(menuPlugin);

            menuPlugin.addMenuListener(this);
        }
        
        // Help
        {
            final JMenu menuHelp = new JMenu(MENUBAR_HELP);
            menuHelp.setMnemonic(KeyEvent.VK_H);
            menuBar.add(menuHelp);
            
            menuHelp.addMenuListener(this);

            /*
             * // Look and Feel { final JMenu menuLaf = new
             * JMenu(MENUBAR_LOOKANDFEEL); menuLaf.setMnemonic( KeyEvent.VK_L );
             * menuHelp.add(menuLaf);
             *  // metal this.createMenuItem( menuLaf, MENUBARCMD_LAF_METAL,
             * LAF_METAL, this, KeyEvent.VK_T, null, true );
             *  // motif this.createMenuItem( menuLaf, MENUBARCMD_LAF_MOTIF,
             * LAF_MOTIF, this, KeyEvent.VK_M, null, true );
             *  // windows this.createMenuItem( menuLaf, MENUBARCMD_LAF_WINDOWS,
             * LAF_WINDOWS, this, KeyEvent.VK_W, null, true );
             *  // windows classic this.createMenuItem( menuLaf,
             * MENUBARCMD_LAF_WINDOWSCLASSIC, LAF_WINDOWSCLASSIC, this,
             * KeyEvent.VK_C, null, true );
             *  // aqua this.createMenuItem( menuLaf, MENUBARCMD_LAF_AQUA,
             * LAF_AQUA, this, KeyEvent.VK_A, null, true ); }
             */

            // upgrade
            this.createMenuItem(menuHelp, MENUBARCMD_UPGRADE, this,
                    KeyEvent.VK_U, null, true);

            menuHelp.addSeparator();

            // change log
            this.createMenuItem(menuHelp, MENUBARCMD_CHANGE_LOG, this,
                    KeyEvent.VK_C, null, true);

            menuHelp.addSeparator();

            // proxy
            this.createMenuItem(menuHelp, MENUBARCMD_PROXY, this,
                    KeyEvent.VK_P, null, true);

            menuHelp.addSeparator();

            // memory
            String dev = SGUserProperties.getInstance().getProperty("dev");
            if (Boolean.valueOf(dev)) {
                this.createMenuItem(menuHelp, MENUBARCMD_MEMORY, this,
                        KeyEvent.VK_M, KeyStroke.getKeyStroke(
                                KeyEvent.VK_M, shift + alt), true);
                menuHelp.addSeparator();
            }

            // detail of plug-in
            this.createMenuItem(menuHelp, MENUBARCMD_PLUGIN_DETAIL, this,
                    KeyEvent.VK_D, null, true);
            
            menuHelp.addSeparator();

            // about
            this.createMenuItem(menuHelp, MENUBARCMD_ABOUT, this,
                    KeyEvent.VK_A, null, true);

        }

        return true;

    }

    private JMenuItem createMenuItem(JMenu menu, String command,
            ActionListener l, int mnemonic, KeyStroke keyStroke, boolean enabled) {
        return this.createMenuItem(menu, command, command, l, mnemonic,
                keyStroke, enabled);
    }

    private JMenuItem createMenuItem(JMenu menu, String text, String command,
            ActionListener l, int mnemonic, KeyStroke keyStroke, boolean enabled) {
        final JMenuItem item = new JMenuItem(text);
        return this.createMenuItemSub(menu, item, command, l, mnemonic,
                keyStroke, enabled);
    }

    private JCheckBoxMenuItem createCheckBoxMenuItem(JMenu menu,
            String command, ActionListener l, int mnemonic,
            KeyStroke keyStroke, boolean enabled) {
        final JMenuItem item = new JCheckBoxMenuItem(command);
        return (JCheckBoxMenuItem) this.createMenuItemSub(menu, item, command, l,
                mnemonic, keyStroke, enabled);
    }

    private JMenuItem createToggleMenuItem(JMenu menu, String command,
            ActionListener l, int mnemonic, KeyStroke keyStroke, boolean enabled) {
        final JMenuItem item = new SGToggleMenuItem(command);
        return this.createMenuItemSub(menu, item, command, l, mnemonic,
                keyStroke, enabled);
    }

    /**
     * 
     * @param node
     * @param menu
     * @param command
     * @param l
     * @return
     */
    private JMenuItem createNodeMenuItem(SGINode node, JMenu menu,
            String command, ActionListener l) {
        return this.createNodeMenuItem(node, menu, command, l, -1, null, true);
    }

    private JMenuItem createNodeMenuItem(SGINode node, JMenu menu,
            String command, ActionListener l, int mnemonic,
            KeyStroke keyStroke, boolean enabled) {
        final NodeMenuItem item = new NodeMenuItem(command);
        item.setNode(node);
        return this.createMenuItemSub(menu, item, command, l, mnemonic,
                keyStroke, enabled);
    }

    private JMenuItem createMenuItemSub(JMenu menu, JMenuItem item,
            String command, ActionListener l, int mnemonic,
            KeyStroke keyStroke, boolean enabled) {
        item.setActionCommand(command);
        item.addActionListener(l);
        if (mnemonic != -1) {
            item.setMnemonic(mnemonic);
        }
        if (keyStroke != null) {
            item.setAccelerator(keyStroke);
        }
        item.setEnabled(enabled);
        menu.add(item);
        return item;
    }
    
    private List<Component> getMenuItemList(final String menuName) {
        JMenu menu = findMenu(this, menuName);
        if (menu == null) {
            return null;
        }
        List<Component> list = new ArrayList<Component>();
        Component[] comArray = menu.getMenuComponents();
        for (int ii = 0; ii < comArray.length; ii++) {
            if ((comArray[ii] instanceof JMenuItem) == false) {
                continue;
            }
            list.add(comArray[ii]);
        }
        return list;
    }

    /**
     * The list of action event listeners.
     */
    private List<ActionListener> mActionListenerList = new ArrayList<ActionListener>();

    /**
     * Adds an action event listener.
     * 
     * @param listener
     *            an action event listener to add
     */
    public void addActionListener(final ActionListener listener) {
        this.mActionListenerList.add(listener);
    }

    /**
     * Removes an action event listener.
     * 
     * @param listener
     *            an action event listener to remove
     */
    public void removeActionListener(ActionListener listener) {
        this.mActionListenerList.remove(listener);
    }

    /**
     * Invoked when an action event is generated.
     * 
     * @param e
     *           an action event
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // set the toggle menu items
        if (source instanceof SGToggleMenuItem) {
            SGToggleMenuItem item = (SGToggleMenuItem) source;
            item.setSelected(!item.isSelected());
        }

        // throw an action event to listeners
		ActionEvent eNew = new ActionEvent(this, e.getID(), e
				.getActionCommand(), e.getModifiers());
		for (int ii = 0; ii < this.mActionListenerList.size(); ii++) {
			final ActionListener el = (ActionListener) this.mActionListenerList
					.get(ii);
			el.actionPerformed(eNew);
		}
    }

    /**
	 * 
	 * @param menuName
	 * @param itemName
	 * @return
	 */
    public boolean isMenuItemEnabled(final String menuName,
            final String itemName) {
        JMenuItem item = findMenuItem(this, menuName, itemName);
        if (item == null) {
            throw new IllegalArgumentException("item==null");
        }
        return item.isEnabled();
    }

    /**
     * 
     * @param menuName
     * @param itemName
     * @param b
     */
    public void setMenuItemEnabled(final String menuName,
            final String itemName, final boolean b) {
        JMenuItem item = findMenuItem(this, menuName, itemName);
        if (item == null) {
            throw new IllegalArgumentException("item==null");
        }
        item.setEnabled(b);
    }

    public void setMenuEnabled(final String menuName,
            final boolean b) {
    	JMenu menu = findMenu(this, menuName);
        menu.setEnabled(b);
    }

    /**
     * 
     * @param menuName
     * @param itemName
     * @return
     */
    public boolean isMenuItemSelected(final String menuName,
            final String itemName) {
        JMenuItem item = findMenuItem(this, menuName, itemName);
        if (item == null) {
            throw new IllegalArgumentException("item==null");
        }

        return item.isSelected();
    }

    /**
     * 
     * @param menuName
     * @param itemName
     * @param b
     */
    public void setMenuItemSelected(final String menuName,
            final String itemName, final boolean b) {
        JMenuItem item = findMenuItem(this, menuName, itemName);
        if (item == null) {
            throw new IllegalArgumentException("item==null");
        }

        item.setSelected(b);
    }

    /**
     * The list of menu listeners.
     */
    private List<MenuListener> mMenuListenerList = new ArrayList<MenuListener>();

    /**
     * Adds a menu event listener.
     * 
     * @param listener
     *           a menu event listener to add
     */
    public void addMenuListener(MenuListener listener) {
        for (int ii = 0; ii < this.mMenuListenerList.size(); ii++) {
            final MenuListener el = this.mMenuListenerList.get(ii);
            if (el.equals(listener)) {
                return;
            }
        }
        this.mMenuListenerList.add(listener);
    }

    /**
     * Removes a menu event listener.
     * 
     * @param listener
     *           a menu event listener to remove
     */
    public void removeActionListener(MenuListener listener) {
        for (int ii = this.mMenuListenerList.size() - 1; ii >= 0; ii--) {
            final MenuListener el = this.mMenuListenerList.get(ii);
            if (el.equals(listener)) {
                this.mMenuListenerList.remove(listener);
            }
        }
    }

    /**
     * Invoked when a menu is selected.
     * 
     * @param e
     *          a menu event
     */
    public void menuSelected(MenuEvent e) {
        for (int ii = 0; ii < this.mMenuListenerList.size(); ii++) {
            final MenuListener el = this.mMenuListenerList.get(ii);
            el.menuSelected(e);
        }
    }

    /**
     * Invoked when a menu is deselected.
     * 
     * @param e
     *          a menu event
     */
    public void menuDeselected(MenuEvent e) {
        for (int ii = 0; ii < this.mMenuListenerList.size(); ii++) {
            final MenuListener el = this.mMenuListenerList.get(ii);
            el.menuDeselected(e);
        }
    }

    /**
     * Invoked when a menu is canceled.
     * 
     * @param e
     *          a menu event
     */
    public void menuCanceled(MenuEvent e) {
        for (int ii = 0; ii < this.mMenuListenerList.size(); ii++) {
            final MenuListener el = this.mMenuListenerList.get(ii);
            el.menuCanceled(e);
        }
    }

    /**
     * 
     */
    public void setInsertToggleButtonsEnabled(final boolean flag) {
        List<Component> itemList = this.getMenuItemList(MENUBAR_INSERT);
        for (int ii = 0; ii < itemList.size(); ii++) {
            SGToggleMenuItem item = (SGToggleMenuItem) itemList.get(ii);
            item.setEnabled(flag);
        }
    }

    /**
     * 
     */
    public void setInsertToggleItemsUnSelected() {
        List<Component> itemList = this.getMenuItemList(MENUBAR_INSERT);
        for (int ii = 0; ii < itemList.size(); ii++) {
            SGToggleMenuItem item = (SGToggleMenuItem) itemList.get(ii);
            item.setSelected(false);
        }
    }

    /**
     * 
     * @param itemName
     * @return
     */
    public boolean hasMenuItem(final String itemName) {
        return (findMenuItem(this, MENUBAR_INSERT, itemName) != null);
    }

    /**
     * 
     * @param command
     */
    public void setInsertToggleItemSelected(final String itemName,
            final boolean b) {
        if (this.hasMenuItem(itemName) == false) {
            throw new IllegalArgumentException();
        }
        findMenuItem(this, MENUBAR_INSERT, itemName).setSelected(b);
    }

    /**
     * 
     * @param command
     */
    public boolean isInsertToggleItemSelected(final String itemName) {
        if (this.hasMenuItem(itemName) == false) {
            throw new IllegalArgumentException();
        }
        return findMenuItem(this, MENUBAR_INSERT, itemName).isSelected();
    }

    void createPropertyMenuBarItem(SGINode node, ActionListener l) {
        JMenu root = findMenu(this, MENUBAR_PROPERTIES);
        root.removeAll();
        this.createMenuItem(node, root, l);
    }

    private void createMenuItem(SGINode node, JMenu parent, ActionListener l) {
        final ArrayList childList = node.getChildNodes();
        final String cText = node.getClassDescription();
        final String iText = node.getInstanceDescription();
        // final String command = MENUBAR_PROPERTIES;
        final boolean pFlag = (node instanceof SGIPropertyDialogObserver);

        // has child objects
        if (childList.size() != 0) {
            if (cText != null && iText != null) {
                JMenu menu = new JMenu(cText);
                parent.add(menu);

                // property dialog observer
                if (pFlag) {
                    this.createNodeMenuItem(node, menu, iText, l);
                }

                // add child
                for (int ii = 0; ii < childList.size(); ii++) {
                    SGINode child = (SGINode) childList.get(ii);
                    this.createMenuItem(child, menu, l);
                }
            }
        } else {
            // property dialog observer
            if (pFlag) {
                if (iText != null) {
                    this.createNodeMenuItem(node, parent, iText, l);
                }
            }
        }
    }
    
    /**
     * Creates the menu items for data plug-in.
     * 
     * @param cmdList
     *           the list of command
     * @param l
     *           an action listener
     */
    public void createDataPluginMenuBarItem(List<SGIPlugin> libList, ActionListener l) {
        JMenu root = findMenu(this, MENUBAR_PLUGIN);
        root.removeAll();
        if (libList != null && libList.size() != 0) {
        	for (SGIPlugin lib : libList) {
        		String text = SGUtility.createPluginItemString(lib);
        		if (text == null) {
        			continue;
        		}
        		String cmd = lib.getCommand();
        		this.createMenuItem(root, text, cmd, l, -1, null, true);
        	}
        } else {
        	String cmd = SGIPluginManager.NO_PLUGIN;
    		this.createMenuItem(root, cmd, cmd, l, -1, null, false);
        }
    }

    /**
     * 
     * @param itemName
     * @return
     */
    public boolean isToolBarMenuSelected(final String itemName) {
        final JMenu layout = findMenu(this, MENUBAR_LAYOUT);
        final JMenu tb = SGUtility.findSubMenu(layout, MENUBAR_TOOL_BAR);
        final JMenuItem item = SGUtility.findMenuItem(tb, itemName);
        if (item != null) {
            return item.isSelected();
        }
        throw new IllegalArgumentException();
    }

    /**
     * 
     * @param itemName
     * @param b
     */
    public void setToolBarMenuItemSelected(final String itemName,
            final boolean b) {
        final JMenu layout = findMenu(this, MENUBAR_LAYOUT);
        final JMenu tb = SGUtility.findSubMenu(layout, MENUBAR_TOOL_BAR);
        final JMenuItem item = SGUtility.findMenuItem(tb, itemName);
        if (item != null) {
            item.setSelected(b);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Finds and returns a menu of given name from given menu bar.
     * 
     * @param menuBar
     *           the menu bar
     * @param menuName
     *           the name of menu
     * @return a menu if it is found
     */
    static JMenu findMenu(JMenuBar menuBar, final String menuName) {
        for (int ii = 0; ii < menuBar.getMenuCount(); ii++) {
            JMenu menu = menuBar.getMenu(ii);
            String text = menu.getText();
            if (text.equals(menuName)) {
                return menu;
            }
        }
        return null;
    }

    /**
     * Finds and returns a menu item of given name from given menu bar.
     * 
     * @param menuBar
     *           the menu bar
     * @param menuName
     *           the name of menu
     * @return a menu if it is found
     */
    static JMenuItem findMenuItem(JMenuBar menuBar, final String menuName, 
    		final String itemName) {
        JMenu menu = findMenu(menuBar, menuName);
        if (menu == null) {
            return null;
        }
        return SGUtility.findMenuItem(menu, itemName);
    }

    static List<JMenuItem> getAllMenuItems(JMenuBar menuBar, String menuName) {
    	List<JMenuItem> itemList = new ArrayList<JMenuItem>();
    	JMenu menu = findMenu(menuBar, menuName);
    	for (int ii = 0; ii < menu.getItemCount(); ii++) {
    		JMenuItem item = menu.getItem(ii);
    		itemList.add(item);
    	}
    	return itemList;
    }
}

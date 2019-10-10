package jp.riken.brain.ni.samuraigraph.base;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * The tool bar.
 */
public class SGToolBar extends JToolBar implements ActionListener,
        ComponentListener, MouseListener,
        // PropertyChangeListener,
        SGIRootObjectConstants {

    /**
     *
     */
    private static final long serialVersionUID = -8958357281404104383L;

    // array of filename of icons
    private static final String[] ICON_FILENAME_ARRAY = {
            NEW_WINDOW_ICON_FILENAME, DRAW_GRAPH_ICON_FILENAME,
            LOAD_PROPERTY_ICON_FILENAME, SAVE_PROPERTY_ICON_FILENAME,
            EXPORT_IMAGE_ICON_FILENAME, PRINT_ICON_FILENAME,
            UNDO_ICON_FILENAME, REDO_ICON_FILENAME, CUT_ICON_FILENAME,
            COPY_ICON_FILENAME, PASTE_ICON_FILENAME,
            INSERT_LABEL_ICON_FILENAME, INSERT_SIGDIFF_ICON_FILENAME,
            INSERT_BREAK_ICON_FILENAME, INSERT_TIMING_ICON_FILENAME,
            BOUNDING_BOX_ICON_FILENAME, LOCK_ICON_FILENAME,
            UNLOCK_ICON_FILENAME, HELP_ICON_FILENAME };

    // array of commands
    private static final String[] COMMANDS_ARRAY = {
            MENUBARCMD_CREATE_NEW_WINDOW, MENUBARCMD_DRAW_GRAPH,
            MENUBARCMD_LOAD_PROPERTY, MENUBARCMD_SAVE_PROPERTY,
            MENUBARCMD_EXPORT_AS_IMAGE, MENUBARCMD_PRINT, MENUBARCMD_UNDO,
            MENUBARCMD_REDO, MENUBARCMD_CUT, MENUBARCMD_COPY, MENUBARCMD_PASTE,
            MENUBARCMD_INSERT_LABEL, MENUBARCMD_INSERT_SIG_DIFF_SYMBOL,
            MENUBARCMD_INSERT_AXIS_BREAK_SYMBOL, MENUBARCMD_INSERT_TIMING_LINE,
            MENUBARCMD_BOUNDING_BOX, MENUBARCMD_LOCK, MENUBARCMD_LOCK,
            MENUBARCMD_HELP };

    /**
     * Buttons.
     */
    private ToolBarButton mCreateNewWindowButton;

    private ToolBarButton mDrawGraphButton;

    private ToolBarButton mLoadPropertyButton;

    private ToolBarButton mSavePropertyButton;

    private ToolBarButton mExportFormatButton;

    private ToolBarButton mPrintButton;

    private ToolBarButton mUndoButton;

    private ToolBarButton mRedoButton;

    private ToolBarButton mCutButton;

    private ToolBarButton mCopyButton;

    private ToolBarButton mPasteButton;

    private ToolBarToggleButton mInsertBreakButton;

    private ToolBarButton mBoundingBoxButton;

    private ToolBarToggleButton mInsertLabelButton;

    private ToolBarToggleButton mInsertTimingLineButton;

    private ToolBarToggleButton mInsertSignificantDifferenceSymbolButton;

    private ToolBarToggleButton mLockFigureButton;

    private ToolBarButton mHelpButton;

    /**
     * Tool Bars.
     */
    private JToolBar mFileToolBar;

    private JToolBar mEditToolBar;

    private JToolBar mInsertToolBar;

    private JToolBar mLayoutToolBar;

    // private JToolBar mHelpToolBar;
    private JToolBar mZoomToolBar;

    /**
     * Tool Bars Title.
     */
    private static final String FILE_TOOLBAR_TITLE = "File";

    private static final String EDIT_TOOLBAR_TITLE = "Edit";

    private static final String INSERT_TOOLBAR_TITLE = "Insert";

    private static final String LAYOUT_TOOLBAR_TITLE = "Layout";

    // private static final String HELP_TOOLBAR_TITLE = "Help";
    private static final String ZOOM_TOOLBAR_TITLE = "Zoom";

    /**
     * A combo box to set the magnification.
     */
    private SGZoomComboBox mZoomComboBox;

    /**
     * A popup menu.
     */
    private JPopupMenu mPopupMenu = null;

    /**
     * Default constructor.
     *
     */
    public SGToolBar() {
        super();

        this.create();
    }

    /**
     * A constructor setting the root object.
     *
     */
    public SGToolBar(SGIRootObject root) {
        super();

        this.setRoot(root);

        this.create();
    }

    // create all objects in the tool bar.
    private boolean create() {

        // array of filename
        final String[] filenameArray = ICON_FILENAME_ARRAY;
        final int num = filenameArray.length;

        //
        // create icon array
        //

        ImageIcon[] icons = new ImageIcon[num];
        for (int ii = 0; ii < num; ii++) {
            icons[ii] = SGUtility.createIcon(this, filenameArray[ii]);
        }

        // a map of file name and icon
        HashMap map = new HashMap();
        for (int ii = 0; ii < num; ii++) {
            map.put(filenameArray[ii], icons[ii]);
        }

        //
        // create buttons
        //

        this.mCreateNewWindowButton = this.createButton(map,
                NEW_WINDOW_ICON_FILENAME, TIP_CREATE_NEW_WINDOW);

        this.mDrawGraphButton = this.createButton(map,
                DRAW_GRAPH_ICON_FILENAME, TIP_DRAW_GRAPH);

        this.mLoadPropertyButton = this.createButton(map,
                LOAD_PROPERTY_ICON_FILENAME, TIP_LOAD_PROPERTY);

        this.mSavePropertyButton = this.createButton(map,
                SAVE_PROPERTY_ICON_FILENAME, TIP_SAVE_PROPERTY);

        this.mExportFormatButton = this.createButton(map,
                EXPORT_IMAGE_ICON_FILENAME, TIP_EXPORT_FORMAT);

        this.mPrintButton = this.createButton(map, PRINT_ICON_FILENAME,
                TIP_PRINT);

        this.mUndoButton = this.createButton(map, UNDO_ICON_FILENAME, TIP_UNDO);

        this.mRedoButton = this.createButton(map, REDO_ICON_FILENAME, TIP_REDO);

        this.mCutButton = this.createButton(map, CUT_ICON_FILENAME, TIP_CUT);

        this.mCopyButton = this.createButton(map, COPY_ICON_FILENAME, TIP_COPY);

        this.mPasteButton = this.createButton(map, PASTE_ICON_FILENAME,
                TIP_PASTE);

        this.mInsertLabelButton = this.createToggleButton(map,
                INSERT_LABEL_ICON_FILENAME, TIP_INSERT_LABEL);

        this.mInsertSignificantDifferenceSymbolButton = this
                .createToggleButton(map, INSERT_SIGDIFF_ICON_FILENAME,
                        TIP_INSERT_SIG_DIFF_SYMBOL);

        this.mInsertBreakButton = this.createToggleButton(map,
                INSERT_BREAK_ICON_FILENAME, TIP_INSERT_AXIS_BREAK_SYMBOL);

        this.mInsertTimingLineButton = this.createToggleButton(map,
                INSERT_TIMING_ICON_FILENAME, TIP_INSERT_TIMING_LINE);

        this.mBoundingBoxButton = this.createButton(map,
                BOUNDING_BOX_ICON_FILENAME, TIP_BOUNDING_BOX);

        this.mLockFigureButton = this.createToggleButton(map,
                UNLOCK_ICON_FILENAME, TIP_LOCK);

        this.mHelpButton = this.createButton(map, HELP_ICON_FILENAME, TIP_HELP);

        // add insert buttons to an original button group
        SGButtonGroup bg = new SGButtonGroup();
        bg.add(this.mInsertBreakButton);
        bg.add(this.mInsertLabelButton);
        bg.add(this.mInsertSignificantDifferenceSymbolButton);
        bg.add(this.mInsertTimingLineButton);
        bg.add(new JToggleButton());

        // create a combo box for zoom
        this.mZoomComboBox = new SGZoomComboBox();

        //
        String laf = SGUtility.getLookAndFeelID();
        if (LAF_WINDOWS.equals(laf)) {
            this.setRollover(true);
        }

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setOrientation(SwingConstants.HORIZONTAL);
        this.setFloatable(false);

        // add components
        JToolBar barFile = new JToolBar(FILE_TOOLBAR_TITLE);
        barFile.add(this.mCreateNewWindowButton);
        barFile.add(this.mDrawGraphButton);
        barFile.add(this.mLoadPropertyButton);
        barFile.add(this.mSavePropertyButton);
        barFile.add(this.mExportFormatButton);
        barFile.add(this.mPrintButton);
        barFile.setMaximumSize(barFile.getPreferredSize());
        barFile.setMinimumSize(barFile.getPreferredSize());
        this.mFileToolBar = barFile;

        JToolBar barEdit = new JToolBar(EDIT_TOOLBAR_TITLE);
        barEdit.add(this.mUndoButton);
        barEdit.add(this.mRedoButton);
        barEdit.add(this.mCutButton);
        barEdit.add(this.mCopyButton);
        barEdit.add(this.mPasteButton);
        barEdit.setMaximumSize(barEdit.getPreferredSize());
        barEdit.setMinimumSize(barEdit.getPreferredSize());
        this.mEditToolBar = barEdit;

        JToolBar barInsert = new JToolBar(INSERT_TOOLBAR_TITLE);
        barInsert.add(this.mInsertLabelButton);
        barInsert.add(this.mInsertSignificantDifferenceSymbolButton);
        barInsert.add(this.mInsertBreakButton);
        barInsert.add(this.mInsertTimingLineButton);
        barInsert.setMaximumSize(barInsert.getPreferredSize());
        barInsert.setMinimumSize(barInsert.getPreferredSize());
        this.mInsertToolBar = barInsert;

        JToolBar barLayout = new JToolBar(LAYOUT_TOOLBAR_TITLE);
        barLayout.add(this.mBoundingBoxButton);
        barLayout.add(this.mLockFigureButton);
        barLayout.setMaximumSize(barLayout.getPreferredSize());
        barLayout.setMinimumSize(barLayout.getPreferredSize());
        this.mLayoutToolBar = barLayout;

        // JToolBar barHelp = new JToolBar( HELP_TOOLBAR_TITLE );
        // barHelp.add(this.mHelpButton);
        // barHelp.setMaximumSize( barHelp.getPreferredSize() );
        // barHelp.setMinimumSize( barHelp.getPreferredSize() );
        // this.mHelpToolBar = barHelp;

        JToolBar barZoom = new JToolBar(ZOOM_TOOLBAR_TITLE);
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(132, this.mBoundingBoxButton
                .getPreferredSize().height));
        panel.setMaximumSize(panel.getPreferredSize());
        panel.setMinimumSize(panel.getPreferredSize());
        panel.add(this.mZoomComboBox, BorderLayout.CENTER);
        barZoom.add(panel);
        barZoom.setMaximumSize(barZoom.getPreferredSize());
        barZoom.setMinimumSize(barZoom.getPreferredSize());
        this.mZoomToolBar = barZoom;

        if (LAF_WINDOWS.equals(laf)) {
            barFile.setRollover(true);
            barEdit.setRollover(true);
            barInsert.setRollover(true);
            barLayout.setRollover(true);
            // barHelp.setRollover(true);
            barZoom.setRollover(true);
        }

        // AbstractButton btn = new JToggleButton("..");
        // btn.addActionListener( this );
        // this.mButton = btn;

        this.add(barFile);
        this.add(barEdit);
        this.add(barInsert);
        this.add(barLayout);
        // this.add( barHelp );
        this.add(barZoom);
        // this.add( btn );

        this.createButtonMap();
        this.createToolBarMap();

        // add itself as a mouse listener
        this.addMouseListener(this);

        return true;
    }


    /**
     * Returns a pop-up menu.
     * @return
     *         a pop-up menu
     */
    public JPopupMenu getPopupMenu() {
        JPopupMenu p = null;
        if (this.mPopupMenu != null) {
            p = this.mPopupMenu;
        } else {
            p = this.createPopupMenu();
            this.mPopupMenu = p;
        }

        Map map = this.mToolBarMap;
        Component[] array = p.getComponents();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii] instanceof JCheckBoxMenuItem) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) array[ii];
                String command = item.getActionCommand();
                JToolBar bar = (JToolBar) map.get(command);
                item.setSelected(bar.isVisible());
            }
        }

        return p;
    }

    /**
     * Create a pop-up menu.
     * @return
     *         a pop-up menu
     */
    private JPopupMenu createPopupMenu() {
        JPopupMenu p = new JPopupMenu();
        p.setBounds(0, 0, 100, 100);

        p.add(new JLabel("  -- Tool Bar --"));
        p.addSeparator();

        // commands
        String[] commands = TOOLBAR_MENUCMD_ARRAY;
        for (int ii = 0; ii < commands.length; ii++) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(commands[ii]);
            item.addActionListener(this);
            p.add(item);
        }

        return p;
    }

    private ToolBarButton createButton(Map map, String filename, String tip) {
        ImageIcon icon = (ImageIcon) map.get(filename);
        ToolBarButton b = new ToolBarButton(icon);
        b.setToolTipText(tip);
        b.addActionListener(this);
        return b;
    }

    private ToolBarToggleButton createToggleButton(Map map, String filename,
            String tip) {
        ImageIcon icon = (ImageIcon) map.get(filename);
        ToolBarToggleButton b = new ToolBarToggleButton(icon);
        b.setToolTipText(tip);
        b.addActionListener(this);
        return b;
    }

    /**
     *
     * @param obj
     */
    public void setRoot(SGIRootObject obj) {
        // this.mRoot = obj;
        this.mZoomComboBox.addZoomable(obj);
    }

    //
    // action event
    //

    /**
     *
     */
    private ArrayList mActionListenerList = new ArrayList();

    /**
     *
     */
    public void addActionListener(final ActionListener listener) {
        this.mActionListenerList.add(listener);
    }

    /**
     *
     */
    public void removeActionListener(ActionListener listener) {
        this.mActionListenerList.remove(listener);
    }

    /**
     *
     */
    public void notifyToListener(final String command) {
        ActionEvent e = new ActionEvent(this, 0, command);
        this.notifyToListener(e);
    }

    /**
     *
     */
    public void notifyToListener(final ActionEvent e) {
        ArrayList list = this.mActionListenerList;
        for (int ii = 0; ii < list.size(); ii++) {
            final ActionListener el = (ActionListener) list.get(ii);
            el.actionPerformed(e);
        }
    }

    /**
     * Returns an array of buttons.
     */
    private IToolBarButton[] getButtonArray() {
        IToolBarButton[] bArray = { this.mCreateNewWindowButton,
                this.mDrawGraphButton, this.mLoadPropertyButton,
                this.mSavePropertyButton, this.mExportFormatButton,
                this.mPrintButton, this.mUndoButton, this.mRedoButton,
                this.mCutButton, this.mCopyButton, this.mPasteButton,
                this.mInsertLabelButton,
                this.mInsertSignificantDifferenceSymbolButton,
                this.mInsertBreakButton, this.mInsertTimingLineButton,
                this.mBoundingBoxButton, this.mLockFigureButton,
                this.mLockFigureButton, this.mHelpButton };

        return bArray;
    }

    private IToolBarButton getButton(String com) {
        IToolBarButton[] bArray = this.getButtonArray();
        for (int ii = 0; ii < bArray.length; ii++) {
            String com_ = bArray[ii].getCommand();
            if (com_.equals(com)) {
                return bArray[ii];
            }
        }

        return null;
    }

    private void createButtonMap() {
        // commands
        String[] commands = COMMANDS_ARRAY;

        IToolBarButton[] bArray = this.getButtonArray();

        final int num = commands.length;
        for (int ii = 0; ii < num; ii++) {
            bArray[ii].setCommand(commands[ii]);
        }
    }

    private void createToolBarMap() {

        // commands
        String[] commands = TOOLBAR_MENUCMD_ARRAY;

        JToolBar[] tArray = { this.mFileToolBar, this.mEditToolBar,
                this.mInsertToolBar, this.mLayoutToolBar,
                // this.mHelpToolBar,
                this.mZoomToolBar };

        final int num = commands.length;

        HashMap map = new HashMap();
        for (int ii = 0; ii < num; ii++) {
            map.put(commands[ii], tArray[ii]);
        }

        this.mToolBarMap = map;
    }

    /**
     *
     */
    private Map mToolBarMap;

    /**
     *
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        String command = e.getActionCommand();

        // from the pop-up menu
        if (Arrays.asList(TOOLBAR_MENUCMD_ARRAY).contains(command)) {
            // set buttons visibility
            this.setToolBarVisible(command, !this.isToolBarVisible(command));
//            this.mButton.setSelected(false);

        }

        // from the lock button
        if (source.equals(this.mLockFigureButton)) {
            this.setButtonSelected(MENUBARCMD_LOCK, this.mLockFigureButton
                    .isSelected());
        }

        // notify the command to listeners
        if (source instanceof IToolBarButton) {
            IToolBarButton b = (IToolBarButton) source;
            String com = b.getCommand();

            IToolBarButton[] array = this.getButtonArray();
            for (int ii = 0; ii < array.length; ii++) {
                String com_ = array[ii].getCommand();
                if (com_.equals(com)) {
                    this.notifyToListener(com);
                    break;
                }
            }

        }

        // throw an action event
        ArrayList lList = this.mActionListenerList;
        for (int ii = 0; ii < lList.size(); ii++) {
            ActionListener l = (ActionListener) lList.get(ii);
            l.actionPerformed(new ActionEvent(this, e.getID(), command, e
                    .getModifiers()));
        }

    }

    /**
     *
     * @param command
     * @return
     */
    public boolean isButtonEnabled(final String command) {
        Object obj = this.getButton(command);
        if (obj == null) {
            throw new IllegalArgumentException("obj==null");
        }
        AbstractButton btn = (AbstractButton) obj;
        return btn.isEnabled();
    }

    /**
     *
     * @param command
     * @param b
     */
    public void setButtonEnabled(final String command, final boolean b) {
        Object obj = this.getButton(command);
        if (obj == null) {
            throw new IllegalArgumentException("obj==null");
        }
        AbstractButton btn = (AbstractButton) obj;
        btn.setEnabled(b);
    }

    /**
     *
     * @param command
     * @return
     */
    public boolean isButtonSelected(final String command) {
        Object obj = this.getButton(command);
        if (obj == null) {
            throw new IllegalArgumentException("obj==null");
        }
        AbstractButton btn = (AbstractButton) obj;
        return btn.isSelected();
    }

    /**
     *
     * @param command
     * @param b
     */
    public void setButtonSelected(final String command, final boolean b) {
        Object obj = this.getButton(command);
        if (obj == null) {
            throw new IllegalArgumentException("obj==null");
        }
        AbstractButton btn = (AbstractButton) obj;
        btn.setSelected(b);

        if (command.equals(MENUBARCMD_LOCK)) {
            this.setLockButtonIcon(b);
        }
    }

    /**
     *
     * @return
     */
    public boolean isInsertToggleButtonSelected() {
        final ArrayList list = this.getInsertToggleButtonList();
        for (int ii = 0; ii < list.size(); ii++) {
            final AbstractButton btn = (AbstractButton) list.get(ii);
            if (btn.isSelected()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    public void setInsertToggleButtonsEnabled(final boolean flag) {
        final ArrayList btnList = this.getInsertToggleButtonList();
        for (int ii = 0; ii < btnList.size(); ii++) {
            final AbstractButton btn = (AbstractButton) btnList.get(ii);
            btn.setEnabled(flag);
        }
    }

    /**
     *
     */
    public void setInsertToggleItemsUnSelected() {
        final ArrayList btnList = this.getInsertToggleButtonList();
        for (int ii = 0; ii < btnList.size(); ii++) {
            final AbstractButton btn = (AbstractButton) btnList.get(ii);
            btn.setSelected(false);
        }
    }

    /**
     *
     */
    private ArrayList getInsertToggleButtonList() {
        final ArrayList list = new ArrayList(Arrays.asList(this
                .getInsertToggleButtonArray()));
        return list;
    }

    /**
     *
     * @return
     */
    private JToggleButton[] getInsertToggleButtonArray() {
        JToggleButton[] array = { this.mInsertLabelButton,
                this.mInsertSignificantDifferenceSymbolButton,
                this.mInsertBreakButton, this.mInsertTimingLineButton };

        return array;
    }

    /**
     *
     * @param command
     * @return
     */
    public boolean hasButton(final String command) {
        Object obj = this.getButton(command);
        return (obj != null);
    }

    /**
     *
     * @param command
     * @param b
     */
    public void setInsertTogglebuttonSelected(final String command,
            final boolean b) {
        Object obj = this.getButton(command);
        if (obj != null) {
            AbstractButton btn = (AbstractButton) obj;
            btn.setSelected(b);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     * @param command
     */
    public boolean isInsertTogglebuttonSelected(final String command) {
        Object obj = this.getButton(command);
        if (obj != null) {
            AbstractButton btn = (AbstractButton) obj;
            return btn.isSelected();
        }
        throw new IllegalArgumentException();
    }

    /**
     *
     */
    private void setLockButtonIcon(final boolean flag) {

        ImageIcon iconLock;
        if (flag) {
            iconLock = SGUtility.createIcon(this,LOCK_ICON_FILENAME);
        } else {
            iconLock = SGUtility.createIcon(this, UNLOCK_ICON_FILENAME);
        }

        this.mLockFigureButton.setIcon(iconLock);
    }

    /**
     *
     * @param command
     * @param b
     */
    public void setToolBarVisible(final String command, final boolean b) {
        Object obj = this.mToolBarMap.get(command);
        if (obj == null) {
            throw new IllegalArgumentException("obj==null");
        }
        JToolBar tb = (JToolBar) obj;
        tb.setVisible(b);

        // update the bounds
        this.updateBounds();

//        this.updatePopupMenu();
    }

    /**
     *
     * @param command
     * @return
     */
    public boolean isToolBarVisible(final String command) {
        Object obj = this.mToolBarMap.get(command);
        if (obj == null) {
            throw new IllegalArgumentException("obj==null");
        }
        JToolBar tb = (JToolBar) obj;
        return tb.isVisible();
    }

    private JToolBar[] getToolBarArray() {
        Component[] com = this.getComponents();

        ArrayList list = new ArrayList();
        for (int ii = 0; ii < com.length; ii++) {
            if (com[ii] instanceof JToolBar) {
                list.add(com[ii]);
            }
        }

        JToolBar[] array = new JToolBar[list.size()];
        for (int ii = 0; ii < array.length; ii++) {
            array[ii] = (JToolBar) list.get(ii);
        }

        return array;
    }

    /**
     * Returns an array of keys of inner tool bars.
     *
     * @return an array of keys of inner tool bars.
     */
    public String[] getToolBarPattern() {
        Component[] com = this.getToolBarArray();
        String[] keys = TOOLBAR_MENUCMD_ARRAY;
        Map map = this.mToolBarMap;

        ArrayList keyList = new ArrayList();
        for (int ii = 0; ii < com.length; ii++) {
            if (!com[ii].isVisible()) {
                continue;
            }

            for (int jj = 0; ii < keys.length; jj++) {
                Object value = map.get(keys[jj]);
                if (com[ii].equals(value)) {
                    keyList.add(keys[jj]);
                    break;
                }
            }
        }

        String[] array = new String[keyList.size()];
        for (int ii = 0; ii < array.length; ii++) {
            array[ii] = (String) keyList.get(ii);
        }

        return array;
    }

    /**
     * Set visible inner tool bars.
     *
     * @param pattern -
     *            an array of keys of visible tool bars.
     */
    public void setToolBarPattern(final String[] pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern==null");
        }

        ArrayList visibleList = new ArrayList();
        for (int ii = 0; ii < pattern.length; ii++) {
            final String key = pattern[ii];
            visibleList.add(key);
        }

        Map map = this.mToolBarMap;
        String[] keys = TOOLBAR_MENUCMD_ARRAY;
        for (int ii = 0; ii < keys.length; ii++) {
            final boolean visible = (visibleList.contains(keys[ii]));
            JToolBar bar = (JToolBar) map.get(keys[ii]);
            bar.setVisible(visible);
        }

    }

    /**
     * Handles the componentShown event by invoking the componentShown methods
     * on listener-a and listener-b.
     *
     * @param e
     *            the component event
     */
    public void componentShown(final ComponentEvent e) {
    }

    /**
     * Handles the componentHidden event by invoking the componentHidden methods
     * on listener-a and listener-b.
     *
     * @param e
     *            the component event
     */
    public void componentHidden(final ComponentEvent e) {
    }

    /**
     * Handles the componentMoved event by invoking the componentMoved methods
     * on listener-a and listener-b.
     *
     * @param e
     *            the component event
     */
    public void componentMoved(final ComponentEvent e) {
    }

    /**
     * Handles the componentResized event by invoking the componentResized
     * methods on listener-a and listener-b.
     *
     * @param e
     *            the component event
     */
    public void componentResized(final ComponentEvent e) {
        this.updateBounds();
    }

    /**
     * Update the bounds with the parent.
     *
     */
    private void updateBounds() {

        JToolBar[] array = this.getToolBarArray();

        ArrayList tList = new ArrayList();
        ArrayList pList = new ArrayList();
        int num = array.length;
        int x = 0;
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].isVisible() == false) {
                continue;
            }

            final int width = array[ii].getPreferredSize().width;
            x += width;
            if (x > this.getWidth()) {
                num = ii;
                break;
            }
        }

        for (int ii = 0; ii < num; ii++) {
            if (array[ii].isVisible() == false) {
                continue;
            }
            tList.add(array[ii]);
        }
        for (int ii = num; ii < array.length; ii++) {
            if (array[ii].isVisible() == false) {
                continue;
            }
            pList.add(array[ii]);
        }
    }

    //
    // for zooming
    //

    /**
     *
     */
    public void setZoomValue(final Number mag) {
        if (mag == null) {
            return;
        }
        this.mZoomComboBox.setZoomValue(mag);
    }

    /**
     *
     */
    public void mouseClicked(final MouseEvent e) {

        final int x = e.getX();
        final int y = e.getY();
        if (e.getClickCount() == 1) {
            if (SwingUtilities.isRightMouseButton(e)) {
                this.getPopupMenu().show(this, x, y);
            }
        }

    }

    public void mouseEntered(final MouseEvent e) {
    }

    public void mouseExited(final MouseEvent e) {
    }

    public void mousePressed(final MouseEvent e) {
    }

    public void mouseReleased(final MouseEvent e) {
    }

    /**
     * Inner interface for buttons which holds command string.
     */
    private interface IToolBarButton {
        /**
         * @param com
         * @uml.property name="command"
         */
        public void setCommand(String com);

        /**
         * @return
         * @uml.property name="command"
         */
        public String getCommand();
    }

    /**
     * An inner button class with command string.
     */
    private static class ToolBarButton extends JButton implements IToolBarButton {
        /**
         *
         */
        private static final long serialVersionUID = 8505619799910066578L;

        private String mCommand;

        ToolBarButton(Icon icon) {
            super(icon);
        }

        public void setCommand(String com) {
            this.mCommand = com;
        }

        public String getCommand() {
            return this.mCommand;
        }
    }

    /**
     * An inner toggle button class with command string.
     */
    private static class ToolBarToggleButton extends JToggleButton implements
            IToolBarButton {
        /**
         *
         */
        private static final long serialVersionUID = -7127631923417672362L;

        private String mCommand;

        ToolBarToggleButton(Icon icon) {
            super(icon);
        }

        public void setCommand(String com) {
            this.mCommand = com;
        }

        public String getCommand() {
            return this.mCommand;
        }
    }

}

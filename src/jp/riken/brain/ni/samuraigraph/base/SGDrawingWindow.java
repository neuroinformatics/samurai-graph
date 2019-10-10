package jp.riken.brain.ni.samuraigraph.base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.print.attribute.standard.MediaSize;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import jp.riken.brain.ni.samuraigraph.base.SGUtility.MouseDragInput;
import jp.riken.brain.ni.samuraigraph.base.SGUtility.MouseDragResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The window which has figures in its pane.
 */
public class SGDrawingWindow extends JFrame implements ComponentListener,
        PropertyChangeListener, MenuListener, ActionListener, SGIDisposable, SGIUndoable,
        SGINode, SGIRootObject, SGIWindowDialogObserver, SGIProgressControl {

    // serialVersionUID
    private static final long serialVersionUID = -7587763518020468378L;

    // ID-number of this window
    private int mID;

    // magnification
    private float mMagnification = 1.0f;

    // flag of auto zooming
    private boolean mAutoZoomFlag = false;

    // A layered pane.
    private SGClientPanel mClientPanel;

    // Property dialog of this window object
    private SGWindowDialog mPropertyDialog = null;

    // image file export manager
    private SGIImageExportManager mImageExportManager;

    // Temporary size of the view port, which is used in the lock mode.
    private final SGTuple2f mTemporaryViewportSize = new SGTuple2f();

    // The tool bar
    private SGToolBar mToolBar;

    // The status bar.
    private SGStatusBar mStatusBar;

    /**
     *
     */
    private boolean mLockFigureFlag = false;

    /**
     *
     */
    private int mMode = MODE_DISPLAY;

    /**
     *
     */
    private SGProperties mTemporaryProperties = null;

    /**
     *
     */
    private final SGTuple2f mPaperOrigin = new SGTuple2f();

    /**
     * Bounds of the client area.
     */
    private Rectangle2D mClientRect = null;

    /**
     * The list of copied figures in this window.
     */
    private List<SGFigure> mCopiedFiguresList = new ArrayList<SGFigure>();

    /**
     * The list of copied objects in this window such as labels or symbols.
     */
    private List<SGICopiable> mCopiedObjectsList = new ArrayList<SGICopiable>();

    /**
     * The list of copied data objects in this window.
     */
    private List<SGData> mCopiedDataObjectsList = new ArrayList<SGData>();

    /**
     * The list of names of copied data objects.
     */
    private List<String> mCopiedDataNameList = new ArrayList<String>();

    /**
     * The list of properties of copied data objects.
     */
    private List<Map<Class, SGProperties>> mCopiedDataPropertiesMapList = new ArrayList<Map<Class, SGProperties>>();

    /**
     * The background image.
     */
    private BackgroundImage mBackgroundImage = null;

    /**
     * constructor
     */
    public SGDrawingWindow() {
        super();
        this.create();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mPropertyDialog = new SGWindowDialog(SGDrawingWindow.this, true);
            }
        });
    }


    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;

        // dispose the property dialog
        if (this.mPropertyDialog != null) {
            this.mPropertyDialog.dispose();
            this.mPropertyDialog = null;
        }

		// dispose figures
		this.removeAllFigures();

		// dispose copied objects
		for (int ii = 0; ii < this.mCopiedDataObjectsList.size(); ii++) {
		    SGData data = (SGData) this.mCopiedDataObjectsList.get(ii);
		    data.dispose();
		}
		for (int ii = 0; ii < this.mCopiedDataPropertiesMapList.size(); ii++) {
		    Map map = (Map) this.mCopiedDataPropertiesMapList.get(ii);
		    map.clear();
		}
		for (int ii = 0; ii < this.mCopiedObjectsList.size(); ii++) {
		    Object obj = this.mCopiedObjectsList.get(ii);
		    if (obj instanceof SGIDisposable) {
			SGIDisposable disp = (SGIDisposable) obj;
			disp.dispose();
		    }
		}

		// clear lists of copied objects
        this.clearCopiedObjectsList();

        // dispose temporary properties
		if (this.mTemporaryProperties != null) {
		    this.mTemporaryProperties.dispose();
		}
		this.mTemporaryProperties = null;

		this.mClientRect = null;

		this.mBackgroundImage = null;

		// dispose this window
		super.dispose();
    }

    // The flag whether this object is already disposed of.
    private boolean mDisposed = false;

    /**
     * Returns whether this object is already disposed of.
     *
     * @return true if this object is already disposed of
     */
    public boolean isDisposed() {
    	return this.mDisposed;
    }

    /**
     *
     * @return
     */
    public String toString() {
        return "SGDrawingWindow:" + this.getID();
    }

    private static final String[] IMAGE_FILENAMES_ARRAY = { SAMURAI_IMG_FILENAME };

    /**
     * Load image objects.
     *
     * @return a map object
     */
    private Map<String, ImageIcon> loadImages() {
        String[] keys = IMAGE_FILENAMES_ARRAY;
        final int num = keys.length;
        ImageIcon[] icons = new ImageIcon[num];
        for (int ii = 0; ii < num; ii++) {
            icons[ii] = SGUtility.createIcon(this, keys[ii]);
        }
        Map<String, ImageIcon> m = new HashMap<String, ImageIcon>();
        for (int ii = 0; ii < num; ii++) {
            m.put(keys[ii], icons[ii]);
        }
        return m;
    }

    /**
     * create window
     */
    private boolean create() {
        // load images
        Map map = this.loadImages();

        // update the UI
        SwingUtilities.updateComponentTreeUI(this);

        // icon image
        final ImageIcon icon = (ImageIcon) map.get(SAMURAI_IMG_FILENAME);
        this.setIconImage(icon.getImage());

        // create the menu bar
        this.createMenuBar();

        // create a tool bar
        this.createToolBar();

        // create a status bar
        this.createStatusBar();

        // create a client panel
        this.createClientPanel();

        // pack
        this.pack();

        // initializes flag map
        this.initInsertFlagMap();

        // update items
        this.updateItemsByFigureNumbers();
        this.updateGridItems();
        this.updateUndoItems();
        this.updateFocusedObjectItem();
        this.updateZoomItems();
        this.updateSnapToGridItems();
        this.updateModeMenuItems();

        // set saved flag
        this.setSaved(false);

        return true;
    }

    /**
     * Update items enabled for toolbar and menubar
     * by whether any figures is visible or not.
     */
    public void updateItemsByFigureNumbers() {
        final boolean b = (this.getVisibleFigureList().size() != 0);

        // tool bar
        SGToolBar tBar = this.mToolBar;
        tBar.setButtonEnabled(MENUBARCMD_EXPORT_AS_IMAGE, b);
        tBar.setButtonEnabled(MENUBARCMD_SAVE_PROPERTY, b);
        tBar.setButtonEnabled(MENUBARCMD_PRINT, b);
        tBar.setButtonEnabled(MENUBARCMD_BOUNDING_BOX, b);
        tBar.setInsertToggleButtonsEnabled(b);

        // menu bar
        SGMenuBar mBar = this.mMenuBar;
        mBar.setMenuItemEnabled(MENUBAR_FILE, MENUBARCMD_EXPORT_AS_IMAGE, b);
        mBar.setMenuItemEnabled(MENUBAR_FILE, MENUBARCMD_SAVE_PROPERTY, b);
        mBar.setMenuItemEnabled(MENUBAR_FILE, MENUBARCMD_SAVE_DATASET, b);
        mBar.setMenuItemEnabled(MENUBAR_FILE, MENUBARCMD_PRINT, b);
        mBar.setMenuItemEnabled(MENUBAR_LAYOUT, MENUBARCMD_BOUNDING_BOX, b);
        mBar.setMenuItemEnabled(MENUBAR_LAYOUT, MENUBARCMD_SNAP_TO_GRID, b);
        mBar.setMenuItemEnabled(MENUBAR_ARRANGE, MENUBARCMD_AUTO_ARRANGEMENT, b);
        mBar.setInsertToggleButtonsEnabled(b);
    }

    /**
     *
     *
     */
    private void updateInsertItems() {
        String[] array = INSERT_MENUBARCMD_ARRAY;
        for (int ii = 0; ii < array.length; ii++) {
            this.updateInsertItems(array[ii]);
        }
    }

    //
    private void updateInsertItems(final String command) {
        final boolean b = this.getInsertFlag(command);

        SGMenuBar mBar = this.mMenuBar;
        if (mBar.hasMenuItem(command)) {
            mBar.setInsertToggleItemSelected(command, b);
        }
        SGToolBar tBar = this.mToolBar;
        if (tBar.hasButton(command)) {
            tBar.setInsertTogglebuttonSelected(command, b);
        }
    }

    /**
     *
     * @return
     */
    public boolean init() {

        // set default size
    	SGTuple2f size = this.getViewportSize();
    	final float width = size.x;
    	final float height = size.y;
        this.mToolBar.setPreferredSize(new Dimension((int) width,
                this.mToolBar.getHeight()));
        this.mTemporaryViewportSize.setValues(width, height);

        // set the size of the components in window pane
        this.setComponentBounds();

        // set the client rectangle
        this.mClientRect = new Rectangle2D.Float();

        // set the paper rectangle
        final float initX = 0.0f / SGIConstants.CM_POINT_RATIO;
        final float initY = initX;
        this.setPaperOrigin(initX, initY);
        this.mClientPanel.setPaperSizeRoundingOff(
        		SGIRootObjectConstants.DEFAULT_PAPER_WIDTH,
        		SGIRootObjectConstants.DEFAULT_PAPER_HEIGHT);

        this.updateClientRect();

    	// creates the menu items for data plug-in
    	this.createDataPluginMenuBarItem();

        return true;
    }

    //
    public void repaintContentPane() {
        this.getContentPane().repaint();
    }

    // Create the client panel.
    private void createClientPanel() {
        SGClientPanel cPanel = new SGClientPanel(this);
        this.getContentPane().add(cPanel, BorderLayout.CENTER);
        this.mClientPanel = cPanel;
    }

    // Create the status bar.
    private void createStatusBar() {
        SGStatusBar sBar = new SGStatusBar(this);
        this.getContentPane().add(sBar, BorderLayout.SOUTH);
        this.mStatusBar = sBar;
    }

    /**
     *
     * @return
     */
    private boolean createToolBar() {
        // add components into tool bar
        SGToolBar bar = new SGToolBar();
        this.mToolBar = bar;
        bar.setRoot(this);

        bar.addActionListener(this);
        bar.addPropertyChangeListener(this);

        this.addComponentListener(bar);
        bar.addComponentListener(this);
        this.addComponentListener(this);

        this.setToolBar();

        return true;
    }

    /**
     *
     * @return
     */
    private boolean setToolBar() {

        this.getContentPane().remove(this.mToolBar);

        final int ori = this.mToolBar.getOrientation();
        if (ori == SwingConstants.HORIZONTAL) {
            this.getContentPane().add(this.mToolBar, BorderLayout.NORTH);
        } else if (ori == SwingConstants.VERTICAL) {
            this.getContentPane().add(this.mToolBar, BorderLayout.WEST);
        }

        this.validate();

        return true;
    }

    /**
     *
     */
    private int getToolBarHeight() {
        int height = 0;
        if (this.mToolBar.isVisible()) {
            height = this.mToolBar.getHeight();
        }
        return height;
    }

    /**
     *
     * @return
     */
    private int getToolBarWidth() {
        int width = 0;
        if (this.mToolBar.isVisible()) {
            width = this.mToolBar.getWidth();
        }
        return width;
    }

    /**
     * Returns an array of keys of inner tool bars.
     *
     * @return an array of keys of inner tool bars.
     */
    public String[] getToolBarPattern() {
        final String[] pattern = this.mToolBar.getToolBarPattern();
        return pattern;
    }

    /**
     * Set visible inner tool bars.
     *
     * @param pattern -
     *            an array of keys of visible tool bars.
     */
    public void setToolBarPattern(final String[] pattern) {
        this.mToolBar.setToolBarPattern(pattern);
        this.updateToolBarVisibleMenuItems();
        this.updateToolBarVisibleItems();
    }

    /**
     * Returns the window ID.
     *
     * @return the window ID
     */
    public int getID() {
        return this.mID;
    }

    /**
     * Sets the window ID.
     *
     * @param id
     *           the window ID to set
     * @return true if succeeded
     */
    public boolean setID(final int id) {
        this.mID = id;
        return true;
    }

    /**
     * Returns the ID number of a new figure.
     *
     * @return
     *        the ID number of a new figure
     */
    public int assignFigureId() {
        List<SGFigure> fList = this.getVisibleFigureList();
//        if (fList.size() == 0) {
//            return 1;
//        }
//        int maxId = 0;
//        for (int ii = 0; ii < fList.size(); ii++) {
//            SGFigure f = (SGFigure) fList.get(ii);
//            final int id = f.getID();
//            if (id > maxId) {
//                maxId = id;
//            }
//        }
//        return (maxId + 1);

    	List<Integer> idList = new ArrayList<Integer>();
    	for (int ii = 0; ii < fList.size(); ii++) {
    		SGFigure f = fList.get(ii);
    		idList.add(f.getID());
    	}
    	final int id = SGUtility.assignIdNumber(idList);
    	return id;
    }

    /**
     *
     */
    // private JToolBar mBottomToolBar = new JToolBar();

    /**
     *
     * @return
     */
    public int getTopWidth() {
        final Insets insets = this.getInsets();
        final int iTop = insets.top;

        // width of menu bar
        final JMenuBar menuBar = this.getJMenuBar();
        final int menuBarWidth = menuBar.getHeight();

        // width of tool bar
        int tHeight = 0;
        if (this.mToolBar.getOrientation() == SwingConstants.HORIZONTAL) {
            tHeight = this.getToolBarHeight();
        }

        final int width = iTop + menuBarWidth + tHeight;

        return width;
    }

    /**
     *
     * @return
     */
    public int getBottomWidth() {
        final Insets insets = this.getInsets();
        final int iBottom = insets.bottom;

        return iBottom;
    }

    /**
     *
     * @return
     */
    public int getLeftWidth() {
        final Insets insets = this.getInsets();
        final int iLeft = insets.left;

        int tWidth = 0;
        if (this.mToolBar.getOrientation() == SwingConstants.VERTICAL) {
            tWidth = this.getToolBarWidth();
        }

        return iLeft + tWidth;
    }

    /**
     *
     * @return
     */
    public int getRightWidth() {
        final Insets insets = this.getInsets();
        final int iRight = insets.right;
        return iRight;
    }

    /**
     *
     * @return
     */
    public float getMagnification() {
        return this.mMagnification;
    }

    /**
     *
     * @return
     */
    public float getMagnificationPercent() {
        return this.mMagnification * 100.0f;
    }

    /**
     *
     * @param creator
     */
    public void setImageFileCreator(SGIImageExportManager creator) {
        this.mImageExportManager = creator;
    }

    //
    // Figures
    //

    /**
     * The list of figures.
     */
    private final List<SGFigure> mFigureList = new ArrayList<SGFigure>();

    /**
     * Returns a figure with given ID.
     *
     * @param id -
     *            Figure ID
     * @return figure with given ID. null if not found.
     */
    public SGFigure getFigure(final int id) {
        List list = this.getVisibleFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure f = (SGFigure) list.get(ii);
            if (f.getID() == id) {
                return f;
            }
        }

        return null;
    }

    /**
     * Returns the list of figures.
     *
     * @return a list of figures
     */
    public ArrayList<SGFigure> getFigureList() {
        return new ArrayList<SGFigure>(this.mFigureList);
    }

    /**
     * Returns an array of figures.
     *
     * @return
     */
    public SGFigure[] getFigureArray() {
        return (SGFigure[]) this.getFigureList().toArray(new SGFigure[] {});
    }

    /**
     * Returns a list of visible figures.
     *
     * @return a list of visible figures
     */
    public ArrayList<SGFigure> getVisibleFigureList() {
        ArrayList<SGFigure> list = new ArrayList<SGFigure>();
        for (int ii = 0; ii < this.mFigureList.size(); ii++) {
            SGFigure f = this.mFigureList.get(ii);
            if (f.isVisible()) {
                list.add(f);
            }
        }
        return list;
    }

    /**
     * Returns an array of ID numbers of visible figures.
     *
     * @return an array of ID numbers of visible figures
     */
    public int[] getVisibleFigureIDArray() {
        List<SGFigure> fList = this.getVisibleFigureList();
        List<Integer> idList = new ArrayList<Integer>();
        for (int ii = 0; ii < fList.size(); ii++) {
            SGFigure fig = (SGFigure) fList.get(ii);
            idList.add(Integer.valueOf(fig.getID()));
        }
        int[] idArray = new int[idList.size()];
        for (int ii = 0; ii < idArray.length; ii++) {
            idArray[ii] = (idList.get(ii)).intValue();
        }
        Arrays.sort(idArray);
        return idArray;
    }

    /**
     * Hide the figure.
     *
     * @param figure
     * @return
     */
    private boolean hideFigure(SGFigure figure) {
    	this.notifyToListener(NOTIFY_FIGURE_WILL_BE_HIDDEN);
        figure.setVisible(false);
        figure.setChanged(true);
        return true;
    }

    public static final String NOTIFY_FIGURE_WILL_BE_HIDDEN = "Figure Will be hidden";

    /**
     * Remove and dispose all figures.
     *
     */
    public void removeAllFigures() {
    	for (int ii = 0; ii < this.mFigureList.size(); ii++) {
    	    SGFigure f = (SGFigure) this.mFigureList.get(ii);
    	    f.dispose();
    	}
        this.mFigureList.clear();
        this.updateClientRect();
    }

    /**
     * Add a figure to the window.
     *
     * @param figure
     * @param pos :
     *            mouse location of window
     * @return
     */
    public boolean addFigure(final SGFigure figure, final Point pos) {
        // add to the list
        if (this.addFigure(figure) == false) {
            return false;
        }

        if (pos != null) {
            // set location
            Point2D location = this.getLocationInPane(pos.x, pos.y);
            figure.setGraphRectLocation((float) location.getX(), (float) location
                    .getY());
        }

        // snap to the lines
        figure.snapToLines(SGIConstants.OTHER);
        figure.setGraphRectOnDragging();

        return true;
    }

    /**
     * Add a new figure.
     *
     * @param figure -
     *            figure to be added.
     * @return
     */
    public boolean addFigure(final SGFigure figure) {
        // add to the list
        this.mFigureList.add(figure);

        // set the view bounds
        figure.setViewBounds();

        // zoom the added figure
        figure.setMagnification(this.getMagnification());

        // update menu items
        this.updateItemsByFigureNumbers();

        return true;
    }

    //
    public boolean needsConfirmationBeforeDiscard() {
        return (this.getVisibleFigureList().size() != 0 && !this.isSaved());
    }

    /**
     * Sets a given image.
     *
     * @param img
     *           an image to set
     * @param updateHistory
     *           true to update the history
     * @return true if succeeded
     */
    private boolean setImage(final Image img, final boolean updateHistory) {
        boolean changed = false;
        if (updateHistory) {
            if (img != null) {
                if (img.equals(this.getImage()) == false) {
                    changed = true;
                }
            } else {
                if (this.getImage() != null) {
                	changed = true;
                }
            }
        }

        if (this.mClientPanel.setImage(img) == false) {
            return false;
        }

        if (updateHistory) {
            this.updateBackgroundImageItems();
            this.setChanged(changed);
            this.notifyToRoot();
        }

        this.repaintContentPane();

        return true;
    }

    /**
     * Creates an image object from a given byte array and sets to this window.
     *
     * @param ext
     *           the extension of image file
     * @param b
     *           the byte array of image
     * @param updateHistory
     *           true to update the history
     * @return true if succeeded
     */
    public boolean setImage(final byte[] b, final String ext,
    		final boolean updateHistory) {

    	if (b == null || ext == null) {
    		throw new IllegalArgumentException("b == null || ext == null");
    	}

        // create an image object
        Image img = Toolkit.getDefaultToolkit().createImage(b);
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(img, 0);
        try {
            mt.waitForAll();
        } catch (InterruptedException ex) {
        }
        if (img == null) {
            return false;
        }

        // add an image to the window
        if (this.setImage(img, updateHistory) == false) {
            return false;
        }

        // set to the attribute
    	this.mBackgroundImage = new BackgroundImage(ext, b);

        return true;
    }
    
    /**
     * Sets the file path of image.
     * 
     * @param path
     *           the file path of image
     */
    public void setImageFilePath(final String path) {
    	this.mClientPanel.setImageFilePath(path);
    }

    /**
     * Deletes the background image.
     *
     * @return true if succeeded
     */
    public boolean deleteImage() {
    	this.mBackgroundImage = null;
        return this.setImage((Image) null, true);
    }

    public boolean setImageLocation(final int x, final int y) {
        return this.mClientPanel.setImageLocation(x, y);
    }

    public boolean setImageSize(final int w, final int h) {
        return this.mClientPanel.setImageSize(w, h);
    }

    public Image getImage() {
        return this.mClientPanel.getImage();
    }

    /**
     *
     * @return
     */
    public boolean drawBackAllVisibleFigures() {
        ArrayList<SGFigure> list = this.getVisibleFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure fig = (SGFigure) list.get(ii);
            if (fig.drawbackFigure() == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a list of child nodes.
     *
     * @return a list of child nodes
     */
    public ArrayList getChildNodes() {
        return this.getVisibleFigureList();
    }

    /**
     * Deselect the focused objects.
     * @return true
     */
    public boolean clearFocusedObjects() {
    	List<SGISelectable> list = this.getFocusedObjectsList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGISelectable s = (SGISelectable) list.get(ii);
            s.setSelected(false);
        }
        return true;
    }

    /**
     *
     * @return
     */
    public List<SGISelectable> getFocusedObjectsList() {
        List<SGISelectable> list = new ArrayList<SGISelectable>();
        list.addAll(this.getFocusedFigureList());
        return list;
    }

    /**
     * Returns a list of focused figures.
     *
     * @return a list of focused figures
     */
    public List<SGFigure> getFocusedFigureList() {
        List<SGFigure> list = new ArrayList<SGFigure>();
        List<SGFigure> fList = this.getVisibleFigureList();
        for (int ii = 0; ii < fList.size(); ii++) {
        	SGFigure f = fList.get(ii);
            if (f.isSelected()) {
                list.add(f);
            }
        }
        return list;
    }

    /**
     * Returns the map of focused data list.
     * 
     * @return the map of focused data list
     */
    public Map<Integer, List<SGData>> getFocusedDataMap() {
    	Map<Integer, List<SGData>> dataMap = new HashMap<Integer, List<SGData>>();
    	List<SGFigure> figureList = this.getVisibleFigureList();
    	for (SGFigure figure : figureList) {
    		dataMap.put(figure.getID(), figure.getFocusedDataList());
    	}
    	return dataMap;
    }

    /**
     * Returns the map of focused data list in legend order.
     * 
     * @return the map of focused data in legend order
     */
    public Map<Integer, List<SGData>> getFocusedDataMapInLegendOrder() {
    	Map<Integer, List<SGData>> dataMap = new HashMap<Integer, List<SGData>>();
    	List<SGFigure> figureList = this.getVisibleFigureList();
    	for (SGFigure figure : figureList) {
    		dataMap.put(figure.getID(), figure.getFocusedDataListInLegendOrder());
    	}
    	return dataMap;
    }

    /**
     *
     * @param list
     * @return
     */
    public boolean getFocusedObjectsList(List<SGISelectable> list) {
    	list.addAll(this.getFocusedFigureList());
        return true;
    }

    /**
     * Returns a list of copied figures.
     *
     * @return a list of copied figures
     */
    public List<SGFigure> getCopiedFiguresList() {
        return new ArrayList<SGFigure>(this.mCopiedFiguresList);
    }

    /**
     *
     *
     */
    public boolean hideSelectedObjects() {
        ArrayList<SGFigure> list = this.getVisibleFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            if (figure.isSelected()) {
                this.hideFigure(figure);
            } else {
                if (figure.hideSelectedObjects() == false) {
                    return false;
                }
            }
        }

        if (list.size() != 0) {
            //
            this.setChanged(true);

            // clear the list
            this.clearFocusedObjects();

            //
            this.updateItemsByFigureNumbers();
        }

        return true;
    }

    /**
     * Sets the visibility of symbols around selected objects.
     *
     * @param b
     *          true to be visible
     */
    protected void setSelectionSymbolsVisible(final boolean b) {
        ArrayList<SGFigure> list = this.getVisibleFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            figure.setSelectionSymbolsVisible(b);
        }
    }

    /**
     * Sets the visibility of symbols around all objects.
     *
     * @param b
     *          true to be visible
     */
    protected void setSymbolsAroundAllObjectsVisible(final boolean b) {
        ArrayList<SGFigure> list = this.getVisibleFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            figure.setSymbolsAroundAllObjectsVisible(b);
        }
    }

    /**
     * Clear focused figures and focused objects in all figures.
     */
    public boolean clearAllFocusedObjectsInFigures() {
        ArrayList<SGFigure> list = this.getVisibleFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure fig = (SGFigure) list.get(ii);
            if (fig.clearFocusedObjects() == false) {
                return false;
            }
        }
        if (this.clearFocusedFigures() == false) {
            return false;
        }
        return true;
    }

    /**
     * Clear focused figures.
     *
     * @return true if succeeded
     */
    public boolean clearFocusedFigures() {
    	List<SGFigure> list = this.getFocusedFigureList();
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            SGFigure figure = list.get(ii);
            this.setFocusedFigure(figure, false);
        }
        return true;
    }

    /**
     * Set the focused figures.
     *
     * @param figure
     *            figure
     * @param focused
     *            flag to set
     * @return true:succeeded, false:failed
     */
    public boolean setFocusedFigure(final SGFigure figure, final boolean b) {
        figure.setSelected(b);
        figure.setSymbolsAroundAllObjectsVisible(b);
        return true;
    }

    //
    // components in the window
    //

    /**
     * get view port size
     */
    public SGTuple2f getViewportSize() {
        final SGTuple2f size = this.getPaneSize();
        final int rw = this.mClientPanel.getRulerWidth();
        size.x -= rw;
        size.y -= rw;
        return size;
    }

    /**
     *
     * @return
     */
    public SGTuple2f getPaneOrigin() {
        Rectangle2D rect = this.getPaneBounds();
        final SGTuple2f origin = new SGTuple2f((float) rect.getX(),
                (float) rect.getY());
        return origin;
    }

    /**
     * Returns the size of pane.
     */
    public SGTuple2f getPaneSize() {
        Rectangle2D rect = this.getPaneBounds();
        final SGTuple2f size = new SGTuple2f((float) rect.getWidth(),
                (float) rect.getHeight());
        return size;
    }

    public Rectangle2D getPaneBounds() {

        // get size of boarder area
        final Insets insets = this.getInsets();
        final int iTop = insets.top;
        final int iBottom = insets.bottom;
        final int iLeft = insets.left;
        final int iRight = insets.right;

        // width of menu bar
        final JMenuBar menuBar = this.getJMenuBar();
        final int menuBarWidth = menuBar.getHeight();

        // width of tool bar
        final int toolBarWidth = this.getToolBarHeight();

        // StatusBar
        final int sbH = this.mStatusBar.getHeight();

        // set size
        final float sizeX = this.getWidth() - (iLeft + iRight);
        final float sizeY = this.getHeight()
                - (iTop + iBottom + menuBarWidth + toolBarWidth + sbH);

        final float x = iLeft;
        final float y = iTop + menuBarWidth + toolBarWidth;

        Rectangle2D rect = new Rectangle2D.Float(x, y, sizeX, sizeY);

        return rect;
    }

    public boolean setPaperSize(final float width, final float height) {
        return this.mClientPanel.setPaperSize(width, height);
    }

    /**
     *
     * @return
     */
    public JComponent getFigurePanel() {
        return this.mClientPanel.getFigurePanel();
    }

    public float getGridLineInterval() {
        return this.mClientPanel.getGridLineInterval();
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public boolean setPaperOrigin(final float x, final float y) {
        final Rectangle2D cRect = this.getClientRect();
        final float mag = this.mMagnification;
        final float xx = (x - (float) cRect.getX()) / mag;
        final float yy = (y - (float) cRect.getY()) / mag;
        this.mPaperOrigin.setValues(xx, yy);
        return true;
    }

//    /**
//     *
//     */
//    private boolean mPaperPortraitFlag = true;
//
//    /**
//     *
//     * @param b
//     */
//    public void setPaperPortrait(final boolean b) {
//        this.mPaperPortraitFlag = b;
//        this.updatePaperItems();
//    }
//
//    /**
//     *
//     * @return
//     */
//    public boolean getPaperPortrait() {
//        return this.mPaperPortraitFlag;
//    }

    /**
     *
     * @return
     */
    public SGTuple2f getPaperSize() {
        return new SGTuple2f(this.mClientPanel.getPaperWidth(),
                this.mClientPanel.getPaperHeight());
    }

    /**
     *
     * @return
     */
    public float getPaperX() {
        final Rectangle2D cRect = this.getClientRect();
        return (float) cRect.getX() + this.mMagnification * this.mPaperOrigin.x;
    }

    /**
     *
     * @return
     */
    public float getPaperY() {
        final Rectangle2D cRect = this.getClientRect();
        return (float) cRect.getY() + this.mMagnification * this.mPaperOrigin.y;
    }

    /**
     *
     * @return
     */
    public Rectangle2D getPaperRect() {
        final float mag = this.getMagnification();
        Rectangle2D rect = new Rectangle2D.Float(this.getPaperX(), this
                .getPaperY(), mag * this.mClientPanel.getPaperWidth(), mag
                * this.mClientPanel.getPaperHeight());
        return rect;
    }

    public static final float PAPER_MARGIN = 2.0f / SGIConstants.CM_POINT_RATIO;

    /**
     *
     * @return
     */
    public Rectangle2D getBoundingBox() {
        Rectangle2D rect = new Rectangle2D.Float();

        final float margin = this.mMagnification * PAPER_MARGIN;
        Rectangle2D pRect = this.getPaperRect();
        rect.setRect(pRect.getX(), pRect.getY(), pRect.getWidth() + margin,
                pRect.getHeight() + margin);

        return rect;
    }

    /**
     * Set size of viewport in units of pixel.
     */
    public boolean setViewportSize(final float width, final float height) {

        this.mTemporaryViewportSize.setValues(width, height);

        // get size of boarder area
        final Insets insets = this.getInsets();
        final int iTop = insets.top;
        final int iBottom = insets.bottom;
        final int iLeft = insets.left;
        final int iRight = insets.right;

        // width of menu bar
        final JMenuBar menuBar = this.getJMenuBar();
        final int menuBarWidth = menuBar.getHeight();

        // width of tool bar
        final int toolBarWidth = this.getToolBarHeight();

        // set size
        final int rw = this.mClientPanel.getRulerWidth();
        final float sizeX = width + iLeft + iRight + rw;
        final float sizeY = height + iTop + iBottom + menuBarWidth
                + toolBarWidth + rw + this.mStatusBar.getHeight();
        this.setSize((int) sizeX, (int) sizeY);

        return true;
    }

    /**
     *
     * @return
     */
    protected boolean setComponentBounds() {
        final Rectangle2D rect = this.getPaneBounds();
        // final int x = (int)rect.getX();
        // final int y = (int)rect.getY();
        final int width = (int) rect.getWidth();
        final int height = (int) rect.getHeight();
        this.mClientPanel.setSize(width, height);

        this.validate();

        return true;
    }

    /**
     * Called when the window is resized.
     */
    private boolean onResized() {
        // System.out.println("onResized");
        if (this.getClientRect() == null) {
            return false;
        }

        // set the size of the components
        this.setComponentBounds();

        // get and record the size of viewport
        final SGTuple2f size = this.getViewportSize();

        // ratio of the viewport size
        final float ratioX = size.x / this.mTemporaryViewportSize.x;
        final float ratioY = size.y / this.mTemporaryViewportSize.y;

        //
        this.updateClientRect();

        // resize the figures
        ArrayList list = this.getVisibleFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            figure.recordFigureRect();
            figure.setViewBounds();
        }

        // when the figures are locked
        if (this.isLocked()) {
            // resize the paper
            Rectangle2D pRect = this.getPaperRect();
            final float pWidth = ratioX * (float) pRect.getWidth()
                    / this.mMagnification;
            final float pHeight = ratioY * (float) pRect.getHeight()
                    / this.mMagnification;
            this.mClientPanel.setPaperSizeRoundingOff(pWidth, pHeight);

            // resize the figures
            for (int ii = 0; ii < list.size(); ii++) {
                SGFigure figure = (SGFigure) list.get(ii);
                figure.recordFigureRect();
                figure.resize(ratioX, ratioY);
                figure.setChanged(true);
            }

            //
            this.updateClientRect();

            if (this.mTemporaryViewportSize.equals(size) == false) {
                this.setChanged(true);
                this.notifyToRoot();
            }

        }

        //
        this.mTemporaryViewportSize.setValues(size);

        //
        this.doAutoZoom();

        return true;
    }

    /**
     * Zoom this object.
     *
     * @param cl
     * @return
     */
    public boolean zoom(final float cl) {
        //
        this.mMagnification = cl;

        // zoom figures
        SGFigure[] array = this.getFigureArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].setMagnification(cl) == false) {
                throw new Error();
            }
        }

        // zoom panels
        this.mClientPanel.setMagnification(cl);

        //
        this.updateClientRect();

        //
        // this.setScrollBarValue();

        this.repaintContentPane();

        return true;
    }

    /**
     * Sets the auto zoom flag and do auto zoom.
     * @param b
     *          true if auto zoom is to be enabled
     */
    public void setAutoZoom(final boolean b) {

        // set to an attribute
        this.mAutoZoomFlag = b;

        // set to the menu item
        this.mMenuBar.setMenuItemSelected(MENUBAR_LAYOUT, MENUBARCMD_AUTO_ZOOM,
                b);

        // do auto zoom
        this.doAutoZoom();
    }

    /**
     * Returns whether auto zoom is enabled.
     * @return true if auto zoom is enabled
     */
    public boolean isAutoZoom() {
        return this.mAutoZoomFlag;
    }

    /**
     * Do automatic zoom.
     */
    private void doAutoZoom() {
        // auto zoom if flag is set
        if (this.isAutoZoom()) {
            this.zoomWayOut();
        }

        // update menu items
        this.updateZoomItems();
    }

    /**
     *
     * @return
     */
    private boolean zoomWayOut() {
        Rectangle2D bbRect = this.getBoundingBox();
        SGTuple2f vpSize = this.getViewportSize();
        final float ratioX = (float) (vpSize.x / (bbRect.getWidth() / this.mMagnification));
        final float ratioY = (float) (vpSize.y / (bbRect.getHeight() / this.mMagnification));
        final float smaller = (ratioX < ratioY ? ratioX : ratioY);
        final int mag = (int) Math.floor(smaller * 100.0f);
        this.setZoomValue(Integer.valueOf(mag));
        return true;
    }

    /**
     *
     * @param mag
     * @return
     */
    public boolean setZoomValue(final Number mag) {
        this.mToolBar.setZoomValue(mag);
        this.zoom(mag.floatValue() / 100.0f);
        this.updateZoomItems();
        return true;
    }

    /**
     *
     * @return
     */
    private boolean setDefaultZoom() {
        return this.setZoomValue(Integer.valueOf(DEFAULT_ZOOM));
    }

    /**
     *
     */
    public boolean setFigureBoundingBox(final int mode) {
        if (mode != 0 && mode != 1 && mode != 2) {
            return false;
        }

        ArrayList<Rectangle2D> rectList = new ArrayList<Rectangle2D>();
        ArrayList fList = this.getVisibleFigureList();
        for (int ii = 0; ii < fList.size(); ii++) {
            SGFigure figure = (SGFigure) fList.get(ii);
            rectList.add(figure.getBoundingBox());
        }
        Rectangle2D bbRect = SGUtility.createUnion(rectList);
        if (bbRect == null) {
            return false;
        }

        Rectangle2D cRect = this.getClientRect();
        Rectangle2D pRect = this.getPaperRect();
        float width = (float) pRect.getWidth();
        float height = (float) pRect.getHeight();
        final float mag = this.mMagnification;

        // width
        if (mode == 0 || mode == 1) {
            width = BOUNDING_BOX_MARGIN
                    + (float) (-cRect.getX() + bbRect.getX() + bbRect
                            .getWidth()) / mag;
        }

        // height
        if (mode == 0 || mode == 2) {
            height = BOUNDING_BOX_MARGIN
                    + (float) (-cRect.getY() + bbRect.getY() + bbRect
                            .getHeight()) / mag;
        }

        // set to the paper
        this.mClientPanel.setPaperSizeRoundingOut(width, height);

        this.updateClientRect();

        return true;
    }

    public Rectangle2D getBoundingBoxOfFigures(final List<SGFigure> figureList) {

        if (figureList == null) {
            return null;
        }

        if (figureList.size() == 0) {
            return new Rectangle2D.Float();
        }

        ArrayList<Rectangle2D> list = new ArrayList<Rectangle2D>();
        for (int ii = 0; ii < figureList.size(); ii++) {
            final SGFigure figure = figureList.get(ii);
            if (figure == null) {
                continue;
            }

            Rectangle2D rect = figure.getBoundingBox();
            if (rect == null) {
                return null;
            }
            list.add(rect);
        }

        Rectangle2D rectAll = SGUtility.createUnion(list);

        return rectAll;

    }

    /**
     *
     */
    public Object getComponent(final int x, final int y) {
        Object com = this;

        SGFigure[] array = this.getFigureArray();
        for (int ii = array.length - 1; ii >= 0; ii--) {
            if (array[ii].isVisible() == false) {
                continue;
            }

            Rectangle2D rect = array[ii].getGraphRect();
            Point2D p = this.getLocationInPane(x, y);
            if (rect.contains(p)) {
                com = array[ii];
                break;
            }
        }

        return com;
    }

    /**
     * Sets the properties.
     *
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    public SGPropertyResults setProperties(SGPropertyMap map) {

        SGPropertyResults result = new SGPropertyResults();

        // prepare
        if (this.prepare() == false) {
            return null;
        }

        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);

            if (COM_PAPER_WIDTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_PAPER_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.mClientPanel.setPaperWidth(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_PAPER_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_PAPER_WIDTH, SGPropertyResults.SUCCEEDED);
            } else if (COM_PAPER_HEIGHT.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_PAPER_HEIGHT, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.mClientPanel.setPaperHeight(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_PAPER_HEIGHT, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_PAPER_HEIGHT, SGPropertyResults.SUCCEEDED);
            } else if (COM_PAPER_SIZE.equalsIgnoreCase(key)) {
                String[] strArray = SGUtilityText.getStringsInBracket(value);
                if (strArray == null) {
                    result.putResult(COM_PAPER_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (strArray.length != 2) {
                    result.putResult(COM_PAPER_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                String str1 = strArray[0];
                String str2 = strArray[1];
                MediaSize size = SGUtilityText.getMediaSize(str1);
                if (size == null) {
                    result.putResult(COM_PAPER_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                Boolean portrait = SGUtilityText.isPortrait(str2);
                if (portrait == null) {
                    result.putResult(COM_PAPER_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.mClientPanel.setPaperSize(size, portrait) == false) {
                    result.putResult(COM_PAPER_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_PAPER_SIZE, SGPropertyResults.SUCCEEDED);
            } else if (COM_WINDOW_BACKGROUND_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.mClientPanel.setPaperColor(cl) == false) {
                        result.putResult(COM_WINDOW_BACKGROUND_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
                	if (cl == null) {
                        result.putResult(COM_WINDOW_BACKGROUND_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.mClientPanel.setPaperColor(cl) == false) {
                        result.putResult(COM_WINDOW_BACKGROUND_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                }
                result.putResult(COM_WINDOW_BACKGROUND_COLOR, SGPropertyResults.SUCCEEDED);
            } else if (COM_WINDOW_GRID_VISIBLE.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_WINDOW_GRID_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.mClientPanel.setGridLineVisible(b.booleanValue()) == false) {
                    result.putResult(COM_WINDOW_GRID_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_WINDOW_GRID_VISIBLE, SGPropertyResults.SUCCEEDED);
            } else if (COM_WINDOW_GRID_INTERVAL.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_WINDOW_GRID_INTERVAL, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.mClientPanel.setGridLineInterval(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_WINDOW_GRID_INTERVAL, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_WINDOW_GRID_INTERVAL, SGPropertyResults.SUCCEEDED);
            } else if (COM_WINDOW_GRID_LINE_WIDTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_WINDOW_GRID_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.mClientPanel.setGridLineWidth(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_WINDOW_GRID_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_WINDOW_GRID_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
            } else if (COM_WINDOW_GRID_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.mClientPanel.setGridLineColor(cl) == false) {
                        result.putResult(COM_WINDOW_GRID_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
                	if (cl == null) {
                        result.putResult(COM_WINDOW_GRID_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.mClientPanel.setGridLineColor(cl) == false) {
                        result.putResult(COM_WINDOW_GRID_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                }
                result.putResult(COM_WINDOW_GRID_COLOR, SGPropertyResults.SUCCEEDED);
            } else if (COM_IMAGE_LOCATION_X.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_IMAGE_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.mClientPanel.setImageLocationX(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_IMAGE_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_IMAGE_LOCATION_X, SGPropertyResults.SUCCEEDED);
            } else if (COM_IMAGE_LOCATION_Y.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_IMAGE_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.mClientPanel.setImageLocationY(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_IMAGE_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_IMAGE_LOCATION_Y, SGPropertyResults.SUCCEEDED);
            } else if (COM_IMAGE_SCALING_FACTOR.equalsIgnoreCase(key)) {
                Number num = SGUtilityText.getFloat(value);
                if (num == null) {
                    result.putResult(COM_IMAGE_SCALING_FACTOR, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
            	if (SGUtility.isValidPropertyValue(num.floatValue()) == false) {
                    result.putResult(COM_IMAGE_SCALING_FACTOR, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                if (this.mClientPanel.setImageScalingFactor(num.floatValue()) == false) {
                    result.putResult(COM_IMAGE_SCALING_FACTOR, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_IMAGE_SCALING_FACTOR, SGPropertyResults.SUCCEEDED);
            }
        }

        // commit the changes
        if (this.commit() == false) {
            return null;
        }
        this.notifyToRoot();
        this.repaintContentPane();

        return result;
    }

    public void propertyChange(PropertyChangeEvent e) {
        // System.out.println(e);

        Object source = e.getSource();
        // String pName = e.getPropertyName();
        // Object oldValue = e.getOldValue();
        // Object newValue = e.getNewValue();

        this.onResized();

        if (source.equals(this.mToolBar)) {
            this.updateToolBarVisibleMenuItems();

            this.firePropertyChange(PROPERTY_NAME_TOOL_BAR, null, null);
        }

    }

    /**
     * Called when menu item is selected.
     *
     * @param e the menu event
     */
    public void menuSelected(MenuEvent e) {
        Object source = e.getSource();

        // notify to figures
        List<SGFigure> fList = this.getVisibleFigureList();
        for (int ii = 0; ii < fList.size(); ii++) {
            SGFigure f = (SGFigure) fList.get(ii);
            f.onMenuSelected();
        }

        // create menu items for properties
        JMenu menu = (JMenu) source;
        String text = menu.getText();
        if (MENUBAR_PROPERTIES.equals(text)) {
            this.createPropertyMenuBarItem();
        } else if (MENUBAR_PLUGIN.equals(text)) {
        	this.updateDataPluginMenuBarItems();
        }
    }

    public void menuDeselected(MenuEvent e) {
    }

    public void menuCanceled(MenuEvent e) {
    }

    public void componentShown(final ComponentEvent e) {
    }

    public void componentHidden(final ComponentEvent e) {
    }

    public void componentMoved(final ComponentEvent e) {
    }

    public void componentResized(final ComponentEvent e) {
        this.onResized();
    }

    // private int getTopShift()
    // {
    // return this.getTopWidth() + this.mClientPanel.getRulerWidth();
    // }

    // private int getLeftShift()
    // {
    // return this.getLeftWidth() + this.mClientPanel.getRulerWidth();
    // }

    /**
     * Shows the property dialog of this window.
     *
     * @return true if succeeded
     */
    boolean showPropertyDialog() {

        SGWindowDialog dg = (SGWindowDialog) this.getPropertyDialog();

        // set title of dialog
        String title = SGWindowDialog.TITLE + " : " + this.getID();
        dg.setTitle(title);

        // create temporary objects
        this.prepare();

        // show the dialog
        List<SGIPropertyDialogObserver> obsList = new ArrayList<SGIPropertyDialogObserver>();
        obsList.add(this);
        this.showPropertyDialog(dg, obsList);

        return true;
    }

    // insert a symbol to figure
    protected boolean insertSymbol(final SGFigure figure, final int x,
            final int y) {

        boolean flag = false;

        // a label
        if (this.getLabelInsertionFlag()) {
            flag = figure.addString(x, y);
        }

        // a timing line
        if (this.getTimingLineInsertionFlag()) {
            flag = figure.addTimingLine(x, y);
        }

        // an axis break symbol
        if (this.getAxisBreakSymbolInsertionFlag()) {
            flag = figure.addAxisBreakSymbol(x, y);
        }

        // a symbol of significant difference
        if (this.getSignificantDifferenceSymbolInsertionFlag()) {
            flag = figure.addSignificantDifferenceSymbol(x, y);
        }

        // rectangle
        if (this.getRectangleInsertionFlag()) {
            flag = figure.addShape(SGIFigureElementShape.RECTANGLE, x, y);
        }

        // ellipse
        if (this.getEllipseInsertionFlag()) {
            flag = figure.addShape(SGIFigureElementShape.ELLIPSE, x, y);
        }

        // arrow
        if (this.getArrowInsertionFlag()) {
            flag = figure.addShape(SGIFigureElementShape.ARROW, x, y);
        }

        // line
        if (this.getLineInsertionFlag()) {
            flag = figure.addShape(SGIFigureElementShape.LINE, x, y);
        }

        return flag;
    }

    protected final Point mTempMouseLocation = new Point();

    // The point where mouse button is pressed.
    protected Point mMousePressLocation = null;

    /**
     *
     * @param f
     */
    void clearFocusedFiguresOtherThan(SGFigure f) {
        ArrayList list = this.getVisibleFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            final SGFigure figure = (SGFigure) list.get(ii);
            if (!figure.equals(f)) {
                figure.setSelected(false);
            }
        }
    }

    /**
     * Translate all selected objects.
     *
     * @param dx
     * @param dy
     * @return
     */
    public boolean translateFocusedObjects(final int dx, final int dy) {
        ArrayList<SGFigure> list = this.getVisibleFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            final SGFigure figure = (SGFigure) list.get(ii);
            if (figure.isSelected()) {
                figure.translate(dx, dy);
            } else {
                figure.translateSelectedObjects(dx, dy);
            }
        }

        return true;
    }

    protected SGTuple2f mDraggedDirection = null;

    protected int mFixedCoordinate = 0;

    protected MouseDragResult getMouseDragResult(MouseEvent e) {
    	MouseDragInput params = new MouseDragInput(e,
    			this.mTempMouseLocation, this.mDraggedDirection, this.mFixedCoordinate);
    	MouseDragResult result = SGUtility.getMouseDragResult(params);
    	this.mDraggedDirection = result.draggedDirection;
    	this.mFixedCoordinate = result.fixedCoordinate;
        return result;
    }

    protected boolean moveFocusedObjects(MouseEvent e) {
    	MouseDragResult result = this.getMouseDragResult(e);
    	final int dx = result.dx;
    	final int dy = result.dy;

        this.translateFocusedObjects(dx, dy);

        // update the pressed point
        this.mTempMouseLocation.setLocation(this.mTempMouseLocation.x + dx,
                this.mTempMouseLocation.y + dy);

        return true;
    }

    /**
     *
     */
    private Point2D getLocationInPane(final int x, final int y) {

        int xx = x;
        int yy = y;

        // get size of boarder area
        final Insets insets = this.getInsets();
        final int mTop = insets.top;
        // final int mBottom = insets.bottom;
        final int mLeft = insets.left;
        // final int mRight = insets.right;

        xx -= mLeft;
        yy -= mTop;

        // menu bar
        final JMenuBar menuBar = this.getJMenuBar();
        final double menuHeight = menuBar.getHeight();
        yy -= menuHeight;

        // tool bar
        yy -= this.getToolBarHeight();

        // ruler
        final double rulerWidth = this.mClientPanel.getRulerWidth();
        xx -= rulerWidth;
        yy -= rulerWidth;

        return new Point2D.Float(xx, yy);
    }

    public boolean setPositionLabel(final int x, final int y) {
        final Rectangle2D cRect = this.getClientRect();
        final float cx = (float) cRect.getX();
        final float cy = (float) cRect.getY();

        final float ratio = CM_POINT_RATIO / this.mMagnification;
        float xx = (-cx + x) * ratio;
        float yy = (-cy + y) * ratio;

        this.mStatusBar.drawPosition(xx, yy);

        return true;
    }
    
    public void setAxisValueLabel(String str) {
    	this.mStatusBar.drawAxisValueString(str);
    }

    /**
     *
     */
    private ArrayList mActionListenerList = new ArrayList();

    /**
     *
     */
    public void addActionListener(final ActionListener listener) {
        ArrayList list = this.mActionListenerList;
        for (int ii = 0; ii < list.size(); ii++) {
            final ActionListener el = (ActionListener) list.get(ii);
            if (el.equals(listener)) {
                return;
            }
        }
        list.add(listener);
    }

    /**
     *
     */
    public void removeActionListener(ActionListener listener) {
        ArrayList list = this.mActionListenerList;
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            final ActionListener el = (ActionListener) list.get(ii);
            if (el.equals(listener)) {
                this.mActionListenerList.remove(listener);
            }
        }
    }

    /**
     *
     */
    public void notifyToListener(final String command) {
        ArrayList list = this.mActionListenerList;
        for (int ii = 0; ii < list.size(); ii++) {
            final ActionListener el = (ActionListener) list.get(ii);
            el.actionPerformed(this.getActionEvent(command));
        }
    }

    /**
     *
     */
    private ActionEvent getActionEvent(final String command) {
        return new ActionEvent(this, 0, command);
    }

    /**
     *
     */
    public void notifyToListener(final String command, final Object source) {
        ArrayList list = this.mActionListenerList;
        for (int ii = 0; ii < list.size(); ii++) {
            final ActionListener el = (ActionListener) list.get(ii);
            el.actionPerformed(new ActionEvent(source, 0, command));
        }
    }

    /**
     *
     */
    public boolean prepare() {
        this.mTemporaryProperties = this.getProperties();
        return true;
    }

    /**
     *
     * @param flag
     * @return
     */
    protected void setInsertToggleItemsUnselected() {
        this.mMenuBar.setInsertToggleItemsUnSelected();
        this.mToolBar.setInsertToggleItemsUnSelected();

        Map map = this.mInsertFlagMap;
        Boolean b = Boolean.FALSE;

        String[] cmdArray = INSERT_MENUBARCMD_ARRAY;
        for (int ii = 0; ii < cmdArray.length; ii++) {
            map.put(cmdArray[ii], b);
        }

        this.updateInsertItems();
    }

    private void initInsertFlagMap() {
        Map map = new HashMap();
        Boolean b = Boolean.FALSE;

        String[] cmdArray = INSERT_MENUBARCMD_ARRAY;
        for (int ii = 0; ii < cmdArray.length; ii++) {
            map.put(cmdArray[ii], b);
        }

        this.mInsertFlagMap = map;
    }

    private Map mInsertFlagMap;

    /**
     *
     * @return
     */
    public boolean isInsertFlagSelected() {
        Map map = this.mInsertFlagMap;
        Iterator itr = map.values().iterator();
        while (itr.hasNext()) {
            Boolean b = (Boolean) itr.next();
            if (b.booleanValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param command
     * @return
     */
    public boolean getInsertFlag(final String command) {
        Object obj = this.mInsertFlagMap.get(command);
        if (obj == null) {
            throw new IllegalArgumentException();
        }
        Boolean b = (Boolean) obj;
        return b.booleanValue();
    }

    /**
     *
     * @param command
     * @param b
     */
    public void setInsertFlag(final String command, final boolean b) {
        this.mInsertFlagMap.put(command, Boolean.valueOf(b));
    }

    /**
     *
     * @return
     */
    public boolean getLabelInsertionFlag() {
        return this.getInsertFlag(MENUBARCMD_INSERT_LABEL);
    }

    /**
     *
     * @return
     */
    public boolean getTimingLineInsertionFlag() {
        return this.getInsertFlag(MENUBARCMD_INSERT_TIMING_LINE);
    }

    /**
     *
     * @return
     */
    public boolean getAxisBreakSymbolInsertionFlag() {
        return this.getInsertFlag(MENUBARCMD_INSERT_AXIS_BREAK_SYMBOL);
    }

    /**
     *
     * @return
     */
    public boolean getSignificantDifferenceSymbolInsertionFlag() {
        return this.getInsertFlag(MENUBARCMD_INSERT_SIG_DIFF_SYMBOL);
    }

    /**
     *
     * @return
     */
    public boolean getRectangleInsertionFlag() {
        return this.getInsertFlag(MENUBARCMD_INSERT_RECTANGLE);
    }

    /**
     *
     * @return
     */
    public boolean getEllipseInsertionFlag() {
        return this.getInsertFlag(MENUBARCMD_INSERT_ELLIPSE);
    }

    /**
     *
     * @return
     */
    public boolean getArrowInsertionFlag() {
        return this.getInsertFlag(MENUBARCMD_INSERT_ARROW);
    }

    /**
     *
     * @return
     */
    public boolean getLineInsertionFlag() {
        return this.getInsertFlag(MENUBARCMD_INSERT_LINE);
    }

    /**
     * Paste the objects to the target figures.
     *
     * @param list
     *            a list of the objects.
     * @param dataList
     *            a list of a data objects.
     * @param nameList
     *            a list of a data name.
     * @param propertiesMapList
     *            a list of property map
     */
    public void pasteToFigures(List<SGICopiable> list, List<SGData> dataList,
            List<String> nameList, List<Map<Class, SGProperties>> propertiesMapList) {

    	List<SGFigure> fList = this.getFocusedFigureList();
        if (fList.size() == 0) {
            return;
        }

        // paste to the target object
        for (int ii = 0; ii < fList.size(); ii++) {
            SGFigure figure = fList.get(ii);
            figure.paste(list);
            for (int jj = 0; jj < nameList.size(); jj++) {
                SGData data = (SGData) dataList.get(jj);
                String name = (String) nameList.get(jj);
                Map map = (Map) propertiesMapList.get(jj);
                SGData dataNew = (SGData) data.copy();
                if (figure.addData(dataNew, name, map) == false) {
                    throw new Error("Failed to add data.");
                }
            }
        }

        // repaint after pasted
        this.repaintContentPane();

        // notify the change to the root
        this.notifyToRoot();

    }

    /**
     *
     *
     */
    protected void updateDataItem() {
        this.updateFocusedObjectItem();
    }

    /**
     *
     * @return
     */
    protected void updateFocusedObjectItem() {
        boolean eff = false;
        ArrayList list = this.getVisibleFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            if (figure.isSelected()) {
                eff = true;
                break;
            }

            SGIFigureElement[] array = figure.getIFigureElementArray();
            for (int jj = 0; jj < array.length; jj++) {
                if (array[jj].getFocusedObjectsList().size() != 0) {
                    eff = true;
                    break;
                }
            }

            if (eff) {
                break;
            }
        }

        // set to the menu bar
        SGMenuBar mBar = this.mMenuBar;
        mBar.setMenuItemEnabled(MENUBAR_EDIT, MENUBARCMD_CUT, eff);
        mBar.setMenuItemEnabled(MENUBAR_EDIT, MENUBARCMD_COPY, eff);
        mBar.setMenuItemEnabled(MENUBAR_EDIT, MENUBARCMD_DELETE, eff);
        mBar.setMenuItemEnabled(MENUBAR_EDIT, MENUBARCMD_DUPLICATE, eff);
        mBar.setMenuItemEnabled(MENUBAR_ARRANGE, MENUBARCMD_BRING_TO_FRONT, eff);
        mBar.setMenuItemEnabled(MENUBAR_ARRANGE, MENUBARCMD_BRING_FORWARD, eff);
        mBar.setMenuItemEnabled(MENUBAR_ARRANGE, MENUBARCMD_SEND_BACKWARD, eff);
        mBar.setMenuItemEnabled(MENUBAR_ARRANGE, MENUBARCMD_SEND_TO_BACK, eff);

        // set to the tool bar
        SGToolBar tBar = this.mToolBar;
        tBar.setButtonEnabled(MENUBARCMD_CUT, eff);
        tBar.setButtonEnabled(MENUBARCMD_COPY, eff);

    }

    /**
     *
     * @param b
     */
    public void setPasteMenuEnabled(final boolean b) {
        this.mMenuBar.setMenuItemEnabled(MENUBAR_EDIT, MENUBARCMD_PASTE, b);

        this.mToolBar.setButtonEnabled(MENUBARCMD_PASTE, b);

        this.mClientPanel.setPopupMenuEnabled(MENUBARCMD_PASTE, b);
    }

    // update the menu items for grid lines on the window
    private void updateGridItems() {
        final boolean gridVisible = this.mClientPanel.isGridLineVisible();
        boolean plusFlag = false;
        boolean minusFlag = false;
        if (gridVisible) {
            final double interval = this.mClientPanel.getGridLineInterval()
                    * CM_POINT_RATIO;
            final double min = SGIRootObjectConstants.GRID_INTERVAL_MIN_VALUE;
            final double max = SGIRootObjectConstants.GRID_INTERVAL_MAX_VALUE;
            final double step = SGIRootObjectConstants.GRID_INTERVAL_STEP_SIZE;
            final int nCeilInterval = (int) Math.ceil(interval / step);
            final int nFloorInterval = (int) Math.floor(interval / step);
            final int nMin = (int) Math.rint(min / step);
            final int nMax = (int) Math.rint(max / step);
            if (nMin < nFloorInterval) {
                minusFlag = true;
            }
            if (nCeilInterval < nMax) {
                plusFlag = true;
            }
        }

        // set to the menu bar
        SGMenuBar bar = this.mMenuBar;
        bar.setMenuItemSelected(MENUBAR_LAYOUT, MENUBARCMD_GRID_VISIBLE,
                gridVisible);
        bar.setMenuItemEnabled(MENUBAR_LAYOUT, MENUBARCMD_PLUS_GRID, plusFlag);
        bar
                .setMenuItemEnabled(MENUBAR_LAYOUT, MENUBARCMD_MINUS_GRID,
                        minusFlag);
    }

    private void updateModeMenuItems() {
        // final boolean b = ( this.getMode() == MODE_NORMAL );
        // this.mMenuBar.setMenuItemSelected(
        // MENUBAR_ARRANGE, MENUBARCMD_MODE, b );
    }

    // update the menu item "Snap to Grid"
    void updateSnapToGridItems() {
        this.mMenuBar.setMenuItemSelected(MENUBAR_LAYOUT,
                MENUBARCMD_SNAP_TO_GRID, SGFigure.isSnappingToGrid());
    }

    // update the items for zooming
    private void updateZoomItems() {
        final int[] array = SGIRootObjectConstants.MAGNIFICATION_ARRAY;
        final int max = array[0];
        final int min = array[array.length - 1];
        final int mag = (int) (this.getMagnificationPercent());

        boolean zoomIn;
        boolean zoomOut;
        boolean def;
        boolean zoomWayOut;

        // set to the menu bar
        if (this.isAutoZoom()) {
            zoomIn = false;
            zoomOut = false;
            def = false;
            zoomWayOut = false;
        } else {
            zoomIn = (mag != max);
            zoomOut = (mag != min);
            def = (mag != DEFAULT_ZOOM);
            zoomWayOut = true;
        }

        SGMenuBar bar = this.mMenuBar;
        bar.setMenuItemEnabled(MENUBAR_LAYOUT, MENUBARCMD_ZOOM_IN, zoomIn);
        bar.setMenuItemEnabled(MENUBAR_LAYOUT, MENUBARCMD_ZOOM_OUT, zoomOut);
        bar.setMenuItemEnabled(MENUBAR_LAYOUT, MENUBARCMD_DEFAULT_ZOOM, def);
        bar.setMenuItemEnabled(MENUBAR_LAYOUT, MENUBARCMD_ZOOM_WAY_OUT,
                zoomWayOut);
    }

    //
    private void updateToolBarVisibleItems() {
        String[] keys = TOOLBAR_MENUCMD_ARRAY;
        SGToolBar tBar = this.mToolBar;
        SGMenuBar mBar = this.mMenuBar;
        for (int ii = 0; ii < keys.length; ii++) {
            tBar.setToolBarVisible(keys[ii], mBar
                    .isToolBarMenuSelected(keys[ii]));
        }

    }

    //
    private void updateToolBarVisibleMenuItems() {
        String[] keys = TOOLBAR_MENUCMD_ARRAY;
        SGToolBar tBar = this.mToolBar;
        SGMenuBar mBar = this.mMenuBar;
        for (int ii = 0; ii < keys.length; ii++) {
            mBar.setToolBarMenuItemSelected(keys[ii], tBar
                    .isToolBarVisible(keys[ii]));
        }
    }

    // update menu items for background image
    private void updateBackgroundImageItems() {
	boolean b = (this.getImage() != null);
	this.mMenuBar.setMenuItemEnabled(MENUBAR_EDIT, MENUBARCMD_DELETE_BACKGROUND_IMAGE, b);
    }

    /**
     * Called when an action is performed.
     */
    public void actionPerformed(final ActionEvent e) {
        final String command = e.getActionCommand();
        final Object source = e.getSource();

        if (command.equals(MENUBARCMD_SAVE_PROPERTY)) {
            this.mPropertyFileCreationModeOfFigures = ALL_FIGURES;
            this.notifyToListener(MENUBARCMD_SAVE_PROPERTY);
        } else if (command.equals(MENUBARCMD_SAVE_DATASET)) {
            this.mPropertyFileCreationModeOfFigures = ALL_FIGURES;
            this.notifyToListener(MENUBARCMD_SAVE_DATASET);
        } else if (command.equals(MENUBARCMD_DELETE)) {
            this.deleteFocusedObjects();
        } else if (command.equals(MENUBARCMD_CUT)) {
            this.cutFocusedObjects();
        } else if (command.equals(MENUBARCMD_COPY)) {
            this.copyFocusedObjects();
        }else if (command.equals(MENUBARCMD_PASTE)) {
            this.pasteCopiedObjects();
        } else if (command.equals(MENUBARCMD_DUPLICATE)) {
            this.duplicateFocusedObjects();
        } else if (command.equals(MENUBARCMD_DELETE_BACKGROUND_IMAGE)) {
            this.deleteImage();
        } else if (command.equals(MENUBARCMD_BRING_TO_FRONT)) {
            this.bringFocusedObjectsToFront();
        } else if (command.equals(MENUBARCMD_BRING_FORWARD)) {
            this.bringFocusedObjectsForward();
        } else if (command.equals(MENUBARCMD_SEND_BACKWARD)) {
            this.sendFocusedObjectsBackward();
        } else if (command.equals(MENUBARCMD_SEND_TO_BACK)) {
            this.sendFocusedObjectsToBack();
        } else if (command.equals(MENUBARCMD_CLEAR_UNDO_BUFFER)) {
            this.clearUndoBuffer();
        } else if (command.equals(MENUBARCMD_PAPER_A4_PORTRAIT)) {
            this.setPaperSizeDirectly(MediaSize.ISO.A4, true);
        } else if (command.equals(MENUBARCMD_PAPER_B5_PORTRAIT)) {
            this.setPaperSizeDirectly(MediaSize.ISO.B5, true);
        } else if (command.equals(MENUBARCMD_PAPER_USLETTER_PORTRAIT)) {
            this.setPaperSizeDirectly(MediaSize.NA.LETTER, true);
        } else if (command.equals(MENUBARCMD_PAPER_A4_LANDSCAPE)) {
            this.setPaperSizeDirectly(MediaSize.ISO.A4, false);
        } else if (command.equals(MENUBARCMD_PAPER_B5_LANDSCAPE)) {
            this.setPaperSizeDirectly(MediaSize.ISO.B5, false);
        } else if (command.equals(MENUBARCMD_PAPER_USLETTER_LANDSCAPE)) {
            this.setPaperSizeDirectly(MediaSize.NA.LETTER, false);
        } else if (command.equals(MENUBARCMD_BOUNDING_BOX)) {
            this.setBoundingBox();
        } else if (command.equals(MENUBARCMD_PAPER_USER_CUSTOMIZE)) {
            this.showPropertyDialog();
        } else if (command.equals(MENUBARCMD_MODE)) {
            final int mode = (this.getMode() == MODE_EXPORT_AS_IMAGE) ? MODE_DISPLAY
                    : MODE_EXPORT_AS_IMAGE;
            this.setMode(mode);
            this.updateModeMenuItems();
        } else if (command.equals(MENUBARCMD_AUTO_ARRANGEMENT)) {
            this.alignFigures();
        } else if (command.equals(MENUBARCMD_GRID_VISIBLE)) {
            this.mClientPanel.setGridLineVisible(!this.mClientPanel
                    .isGridLineVisible());
            this.updateGridItems();

            this.setChanged(true);
            this.notifyToRoot();
            this.repaintContentPane();
        } else if (command.equals(MENUBARCMD_PLUS_GRID)) {
            final double value = this.mClientPanel.getGridLineInterval()
                    * CM_POINT_RATIO;
            final double min = SGIRootObjectConstants.GRID_INTERVAL_MIN_VALUE;
            final double max = SGIRootObjectConstants.GRID_INTERVAL_MAX_VALUE;
            final double step = SGIRootObjectConstants.GRID_INTERVAL_STEP_SIZE;
            double valueNew = SGUtilityNumber.stepValue(true, value, min, max,
                    step, 0.001f);
            final int indexNew = (int) Math.rint(valueNew / step);
            final int indexMax = (int) Math.rint(max / step);
            if (indexNew != indexMax + 1) {
                if (valueNew > max) {
                    valueNew = max;
                }
                this.mClientPanel.setGridLineInterval((float) valueNew
                        / SGIConstants.CM_POINT_RATIO);
                this.updateGridItems();
                this.repaintContentPane();

                this.setChanged(true);
                this.notifyToRoot();
            }
        } else if (command.equals(MENUBARCMD_MINUS_GRID)) {
            final double value = this.mClientPanel.getGridLineInterval()
                    * CM_POINT_RATIO;
            final double min = SGIRootObjectConstants.GRID_INTERVAL_MIN_VALUE;
            final double max = SGIRootObjectConstants.GRID_INTERVAL_MAX_VALUE;
            final double step = SGIRootObjectConstants.GRID_INTERVAL_STEP_SIZE;
            double valueNew = SGUtilityNumber.stepValue(false, value, min, max,
                    step, 0.001f);
            final int indexNew = (int) Math.rint(valueNew / step);
            final int indexMin = (int) Math.rint(min / step);
            if (indexNew != indexMin - 1) {
                if (valueNew < min) {
                    valueNew = min;
                }
                this.mClientPanel.setGridLineInterval((float) valueNew
                        / SGIConstants.CM_POINT_RATIO);
                this.updateGridItems();
                this.repaintContentPane();

                this.setChanged(true);
                this.notifyToRoot();
            }
        } else if (command.equals(MENUBARCMD_SNAP_TO_GRID)) {
            SGFigure.setSnappingToGrid(!SGFigure.isSnappingToGrid());
            this.updateSnapToGridItems();
        } else if (command.equals(MENUBARCMD_ZOOM_IN)) {
            final int mag = (int) (this.getMagnificationPercent());
            final int[] array = SGIRootObjectConstants.MAGNIFICATION_ARRAY;
            for (int ii = array.length - 1; ii >= 0; ii--) {
                if (array[ii] > mag) {
                    this.setZoomValue(Integer.valueOf(array[ii]));
                    break;
                }
            }
        } else if (command.equals(MENUBARCMD_ZOOM_OUT)) {
            final int mag = (int) (this.getMagnificationPercent());
            final int[] array = SGIRootObjectConstants.MAGNIFICATION_ARRAY;
            for (int ii = 0; ii < array.length; ii++) {
                if (array[ii] < mag) {
                    this.setZoomValue(Integer.valueOf(array[ii]));
                    break;
                }
            }
        } else if (command.equals(MENUBARCMD_DEFAULT_ZOOM)) {
            this.setDefaultZoom();
        } else if (command.equals(MENUBARCMD_ZOOM_WAY_OUT)) {
            this.zoomWayOut();
        } else if (command.equals(MENUBARCMD_AUTO_ZOOM)) {
            this.setAutoZoom(!this.isAutoZoom());
        } else if (command.equals(MENUBARCMD_LOCK)) {
            this.setLocked(!this.isLocked());
        } else if (Arrays.asList(INSERT_MENUBARCMD_ARRAY).contains(command)) {
            // menu to insert a symbol

            boolean selected;

            // synchronize the tool bar and the menu bar
            if (source.equals(this.mMenuBar)) {
                selected = this.mMenuBar.isInsertToggleItemSelected(command);
            } else if (source.equals(this.mToolBar)) {
                selected = this.mToolBar.isInsertTogglebuttonSelected(command);
            } else {
                return;
            }

            this.setInsertToggleItemsUnselected();
            this.setInsertFlag(command, selected);
            this.updateInsertItems();

            // change the mouse cursor
            if (selected) {
                final Cursor cur = new Cursor(Cursor.CROSSHAIR_CURSOR);
                this.setCursor(cur);
            } else {
                this.setCursor(null);
            }
        } else if (Arrays.asList(TOOLBAR_MENUCMD_ARRAY).contains(command)) {
            // menu for the tool bar
            if (source.equals(this.mMenuBar)) {
                this.updateToolBarVisibleItems();
            } else if (source.equals(this.mToolBar)) {
                this.updateToolBarVisibleMenuItems();
            }
            this.firePropertyChange(PROPERTY_NAME_TOOL_BAR, null, null);
        } else {
            this.notifyToListener(command);
        }
    }

    public void setMode(final int mode) {
        this.mMode = mode;
        SGFigure[] array = this.getFigureArray();
        for (int ii = 0; ii < array.length; ii++) {
            array[ii].setMode(mode);
        }
    }

    public int getMode() {
        return this.mMode;
    }

    /**
     * Exports this window as an image file.
     *
     * @param map
     *           the map of image properties
     * @param type
     *           the type of image
     * @param path
     *           the file path to export
     * @param silent
     *           true for the silent mode
     * @return the result of setting properties
     */
    public SGPropertyResults exportAsImage(final SGPropertyMap map, 
    		final String type, final String path, final boolean silent,
    		final boolean bPrePostProcess) {

    	// preprocess
    	if (bPrePostProcess) {
            if (this.startExport(silent) == false) {
            	return null;
            }
    	}

        Component target = this.getExportTarget();
        SGPropertyResults result = this.mImageExportManager.export(
        		target, type, path, map);
        if (result == null) {
            this.endExport(silent);
            return null;
        }

        // post-process
        if (bPrePostProcess) {
            if (this.endExport(silent) == false) {
            	return null;
            }
        }

        return result;
    }

    /**
     * Bring the focused objects to the front.
     *
     */
    void bringFocusedObjectsToFront() {
        this.moveFocusedObjects(true);
    }

    /**
     * Send the focused objects to the back.
     *
     */
    void sendFocusedObjectsToBack() {
        this.moveFocusedObjects(false);
    }

    /**
     * Move the focused objects to the front or back.
     *
     * @param toFront
     *           true to move to the front and false to move to the back
     * @return true if succeeded
     */
    private boolean moveFocusedObjects(final boolean toFront) {

        ArrayList fList = this.getVisibleFigureList();
        boolean changed = false;
        for (int ii = 0; ii < fList.size(); ii++) {
            SGFigure figure = (SGFigure) fList.get(ii);
            if (figure.moveFocusedObjects(toFront) == false) {
                return false;
            }
            SGIFigureElement[] array = figure.getIFigureElementArray();
            for (int jj = 0; jj < array.length; jj++) {
        	if (array[jj].isChanged()) {
        	    changed = true;
        	}
            }
        }

        List<SGISelectable> list = this.getFocusedObjectsList();
        List<SGFigure> objList = this.mFigureList;
        List<SGFigure> objListOld = new ArrayList<SGFigure>(objList);

        // move focused objects
        if (toFront) {
            for (int ii = 0; ii < list.size(); ii++) {
                Object obj = list.get(ii);
                if (SGUtility.moveObjectTo(obj, objList, objList.size() - 1) == false) {
                    return false;
                }
            }
        } else {
            for (int ii = list.size() - 1; ii >= 0; ii--) {
                Object obj = list.get(ii);
                if (SGUtility.moveObjectTo(obj, objList, 0) == false) {
                    return false;
                }
            }
        }

        if (objList.equals(objListOld) == false) {
            this.setChanged(true);
            changed = true;
        }

        if (changed) {
            this.notifyToRoot();
            this.updateDataItem();
        }

        // repaint
        this.repaintContentPane();

        return true;
    }

    /**
     * Bring the focused objects to forward.
     *
     */
    void bringFocusedObjectsForward() {
        this.moveFocusedObjects(1);
    }

    /**
     * Send the focused objects to backward.
     *
     */
    void sendFocusedObjectsBackward() {
        this.moveFocusedObjects(-1);
    }

    /**
     * Move the focused objects to the front or back.
     *
     * @param num
     *           the number of levels to move the focused objects
     * @return true if succeeded
     */
    private boolean moveFocusedObjects(final int num) {

        ArrayList fList = this.getVisibleFigureList();
        boolean changed = false;
        for (int ii = 0; ii < fList.size(); ii++) {
            SGFigure figure = (SGFigure) fList.get(ii);
            if (figure.moveFocusedObjects(num) == false) {
                return false;
            }
            SGIFigureElement[] array = figure.getIFigureElementArray();
            for (int jj = 0; jj < array.length; jj++) {
                if (array[jj].isChanged()) {
                    changed = true;
                }
            }
        }

        List<SGISelectable> list = this.getFocusedObjectsList();
        List<SGFigure> objList = this.mFigureList;

        // record the list before edited
        List<SGFigure> objListOld = new ArrayList<SGFigure>(objList);
        if (SGUtility.moveObject(list, objList, num) == false) {
            return false;
        }

        if (objList.equals(objListOld) == false) {
            this.setChanged(true);
            changed = true;
        }

        if (changed) {
            this.notifyToRoot();
            this.updateDataItem();
        }

        // repaint
        this.repaintContentPane();

        return true;
    }

    /**
     * Brings to front or sends to back the figure.
     *
     * @param id
     *           the figure ID
     * @param toFront
     *           true to bring to front
     * @return true if succeeded
     */
    public boolean moveFigureToEnd(final int id, final boolean toFront) {
        SGFigure f = this.getFigure(id);
        if (f == null) {
            return false;
        }
        if (f.isVisible() == false) {
            return false;
        }

        List objList = this.mFigureList;
        List objListOld = new ArrayList<SGFigure>(objList);

        // move focused objects
        if (toFront) {
            if (SGUtility.moveObjectToTail(f, objList) == false) {
                return false;
            }
        } else {
            if (SGUtility.moveObjectToHead(f, objList) == false) {
                return false;
            }
        }

        final boolean ch = !this.mFigureList.equals(objListOld);
        if (ch) {
            this.setChanged(true);
            this.notifyToRoot();
            this.updateDataItem();
            this.repaintContentPane();
        }

        return true;
    }

    /**
     * Brings forward or sends backward the figure.
     *
     * @param id
     *           the figure ID
     * @param toFront
     *           true to bring forward
     * @return true if succeeded
     */
    public boolean moveFigure(final int id, final boolean toFront) {
        SGFigure f = this.getFigure(id);
        if (f == null) {
            return false;
        }
        if (f.isVisible() == false) {
            return false;
        }

        List<SGFigure> objList = this.mFigureList;
        List<SGFigure> objListOld = new ArrayList<SGFigure>(objList);

        // move focused objects
        if (toFront) {
            if (SGUtility.moveObjectToNext(f, objList) == false) {
                return false;
            }
        } else {
            if (SGUtility.moveObjectToPrevious(f, objList) == false) {
                return false;
            }
        }

        final boolean ch = !this.mFigureList.equals(objListOld);
        if (ch) {
            this.setChanged(true);
            this.notifyToRoot();
            this.updateDataItem();
            this.repaintContentPane();
        }

        return true;
    }

    /**
     * Copy the focused objects.
     */
    public void doCopy() {
        this.copyFocusedObjects();
    }

    // Copy the focused objects.
    void copyFocusedObjects() {
        // get copied objects from all figures
        this.copyAllObjectsInVisibleFigures();

        // notify the copy command
        this.notifyToListener(MENUBARCMD_COPY);

        // update the menu items
        this.updateFocusedObjectItem();
    }

    /**
     * Cut the focused objects.
     */
    public void doCut() {
        this.cutFocusedObjects();
    }

    // Cut focused objects.
    private void cutFocusedObjects() {
        // get copied objects from all figures
        this.cutAllObjectsInVisibleFigures();

        // notify the cut command
        this.notifyToListener(MENUBARCMD_CUT);

        // notify the change to the root
        this.notifyToRoot();

        // update the menu items
        this.updateFocusedObjectItem();

        // repaint
        this.repaintContentPane();
    }

    /**
     *
     * @param id
     * @param isCopy
     * @return
     */
    public boolean cutOrCopyFigure(final int id, final boolean isCopy) {
        // get the figure
        SGFigure f = this.getFigure(id);
        if (f == null) {
            return false;
        }
        if (f.isVisible() == false) {
            return false;
        }

        // add to the attribute
        this.mCopiedFiguresList.add(f);

        // hide when cut the figure
        if (!isCopy) {
            this.hideFigure(f);
        }

        // notify the command
        if (isCopy) {
            this.notifyToListener(MENUBARCMD_COPY);
        } else {
            this.notifyToListener(MENUBARCMD_CUT);
        }

        // notify the change to the root
        this.notifyToRoot();

        // update the menu items
        this.updateFocusedObjectItem();

        // repaint
        this.repaintContentPane();

        return true;
    }

    /**
     * Paste the copied objects.
     */
    public void doPaste() {
        this.pasteCopiedObjects();
    }

    // Paste the copied objects.
    private void pasteCopiedObjects() {
        this.notifyToListener(MENUBARCMD_PASTE);

        // notify the change to the root
        this.notifyToRoot();
    }

    /**
     * Duplicate the focused objects.
     */
    public void doDuplicate() {
        this.duplicateFocusedObjects();
    }

    // Duplicate the focused objects.
    void duplicateFocusedObjects() {
        ArrayList list = this.getVisibleFigureList();

        // duplicate child object of all figures
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            if (figure.duplicateFocusedObjects() == false) {
                return;
            }
        }

        // repaint after duplication
        this.repaintContentPane();

        // notify the duplication command
        this.notifyToListener(MENUBARCMD_DUPLICATE);

        // set unfocused the focused figures
        List<SGFigure> fList = this.getFocusedFigureList();
        for (int ii = 0; ii < fList.size(); ii++) {
            SGFigure figure = fList.get(ii);
            this.setFocusedFigure(figure, false);
        }

        // set focused the duplicated figures
        List<SGFigure> listNew = this.getVisibleFigureList();
        for (int ii = 0; ii < listNew.size(); ii++) {
            SGFigure figure = (SGFigure) listNew.get(ii);
            if (list.contains(figure) == false) {
                this.setFocusedFigure(figure, true);
            }
        }

        // notify the change to the root
        this.notifyToRoot();
    }

    /**
     * Delete the focused objects.
     */
    public void doDelete() {
        this.deleteFocusedObjects();
    }

    // Delete the focused objects.
    private void deleteFocusedObjects() {
        // hide all focused objects
        this.hideSelectedObjects();

        // notify the change to the root
        this.notifyToRoot();

        // update the menu items
        this.updateDataItem();

        // repaint
        this.repaintContentPane();
    }

    /**
     * Hide the figure with given ID.
     *
     * @param id -
     *            ID of figure to hide
     * @return true:succeeded, false:failed
     */
    public boolean hideFigure(final int id) {
        SGFigure f = this.getFigure(id);
        if (f == null) {
            return false;
        }

        // hide the figure
        if (this.hideFigure(f) == false) {
            return false;
        }

        // clear the list
        this.clearFocusedObjects();

        // set changed flag
        this.setChanged(true);

        // notify the change to the root
        this.notifyToRoot();

        // update the menu items
        this.updateItemsByFigureNumbers();

        // repaint
        this.repaintContentPane();

        return true;
    }

    /**
     * Cuts all objects in all visible figures.
     */
    private void cutAllObjectsInVisibleFigures() {
        this.cutOrCopyAllObjectsInVisibleFigures(false);
    }

    /**
     * Copies all objects in all visible figures.
     */
    private void copyAllObjectsInVisibleFigures() {
        this.cutOrCopyAllObjectsInVisibleFigures(true);
    }

    /**
     * Cuts or copies all objects in all visible figures.
     * @param isCopy
     *               true: copy, false: cut
     */
    private void cutOrCopyAllObjectsInVisibleFigures(final boolean isCopy) {

    	// get all visible figures
        List<SGFigure> fList = this.getVisibleFigureList();

        // get objects from all visible figures
        List<SGICopiable> copiedObjList = new ArrayList<SGICopiable>();
        List<SGData> dataList = new ArrayList<SGData>();
        List<String> dataNameList = new ArrayList<String>();
        List<Map<Class, SGProperties>> propertiesMapList = new ArrayList<Map<Class, SGProperties>>();
        if (isCopy) {
            for (int ii = 0; ii < fList.size(); ii++) {
                SGFigure figure = (SGFigure) fList.get(ii);

                // copied objects such as labels and symbols
                copiedObjList.addAll(figure.createCopiedObjects());

                // create copied data
                figure.createCopiedDataObjects(dataList, dataNameList,
                        propertiesMapList);
            }
        } else {
            for (int ii = 0; ii < fList.size(); ii++) {
                SGFigure figure = (SGFigure) fList.get(ii);

                // copied objects such as labels and symbols
                copiedObjList.addAll(figure.cutFocusedObjects());

                // create copied data
                figure.cutFocusedDataObjects(dataList, dataNameList,
                        propertiesMapList);
            }
        }

        // clear all lists below
        this.clearCopiedObjectsList();

        // set to the attribute
        this.mCopiedObjectsList.addAll(copiedObjList);
        this.mCopiedDataObjectsList.addAll(dataList);
        this.mCopiedDataNameList.addAll(dataNameList);
        this.mCopiedDataPropertiesMapList.addAll(propertiesMapList);

    	List<SGFigure> focusedFigureList = this.getFocusedFigureList();
        for (int ii = 0; ii < focusedFigureList.size(); ii++) {
        	this.mCopiedFiguresList.add(focusedFigureList.get(ii));
        }
    }

    /**
     * Returns the list of copied objects in this window.
     *
     * @return a list of copied objects
     */
    public List<SGICopiable> getCopiedObjectsList() {
    	List<SGICopiable> list = new ArrayList<SGICopiable>();
        SGUtility.copyObjects(this.mCopiedObjectsList, list);
        return list;
    }

    /**
     * Returns the list of copied data objects in this window.
     *
     * @return a list of copied data objects
     */
    public List<SGData> getCopiedObjectsDataList() {
    	List<SGData> list = new ArrayList<SGData>();
        SGUtility.copyObjects(this.mCopiedDataObjectsList, list);
        return list;
    }

    /**
     * Returns the list of names of copied data objects in this window.
     *
     * @return a list of names of copied data objects
     */
    public List<String> getCopiedDataNameList() {
    	List<String> list = new ArrayList<String>(this.mCopiedDataNameList);
        return list;
    }

    /**
     * Returns the list of properties of copied data objects in this window.
     *
     * @return
     *       a list of properties of copied data objects
     */
    public List<Map<Class, SGProperties>> getCopiedDataPropertiesMapList() {
        List<Map<Class, SGProperties>> list = new ArrayList<Map<Class, SGProperties>>();
        for (int ii = 0; ii < this.mCopiedDataPropertiesMapList.size(); ii++) {
        	Map<Class, SGProperties> map = this.mCopiedDataPropertiesMapList.get(ii);
            list.add(new HashMap<Class, SGProperties>(map));
        }
        return list;
    }

    /**
     * Clear the list of copied objects.
     *
     */
    public void clearCopiedObjectsList() {

    	// disposes all copied objects
        for (SGICopiable cp : this.mCopiedObjectsList) {
        	if (cp instanceof SGIDisposable) {
        		SGIDisposable d = (SGIDisposable) cp;
        		d.dispose();
        	}
        }
        this.mCopiedObjectsList.clear();

    	// disposes all copied data objects
        for (SGData d : this.mCopiedDataObjectsList) {
        	d.dispose();
        }
        this.mCopiedDataObjectsList.clear();

        // clear other lists
        this.mCopiedDataNameList.clear();
        this.mCopiedDataPropertiesMapList.clear();
        this.mCopiedFiguresList.clear();
    }

    /**
     *
     */
    void notifyPasteToFocusedFigures() {
        this.mPasteTargetList.clear();
        this.mPasteTargetList.addAll(this.getFocusedObjectsList());
        this.notifyToListener(MENUBARCMD_PASTE);
    }

    /**
     * The target object to paste the copied objects.
     */
    private ArrayList mPasteTargetList = new ArrayList();

    // /**
    // *
    // */
    // private void setLookAndFeel( String laf )
    // {
    // try
    // {
    // UIManager.setLookAndFeel(laf);
    // SwingUtilities.updateComponentTreeUI(this);
    // }
    // catch(Exception ex)
    // {
    // System.out.println("Error L&F Setting");
    // }
    // }

    /**
     * Insert a label for netCDF data.
     */
    void doInserNetCDFLabel() {
        List<SGFigure> fList = this.getVisibleFigureList();
        for (int ii = 0; ii < fList.size(); ii++) {
            SGFigure f = (SGFigure) fList.get(ii);
            f.insertNetCDFLabel();
        }
        this.repaintContentPane();
    }

    /**
     * Fits axis range to the visible data in the focused figure.
     */
    void doFitAxisRangeToVisibleData(final List<Integer> axisDirections,
    		final boolean forAnimationFrames) {
        boolean changed = false;
        List<SGFigure> fList = this.getFocusedFigureList();
        for (int dir : axisDirections) {
            for (int ii = 0; ii < fList.size(); ii++) {
                SGFigure f = (SGFigure) fList.get(ii);
                f.fitAxisRangeToVisibleData(dir, forAnimationFrames);
                if (f.isChangedRoot()) {
                    changed = true;
                }
            }
        }
        if (changed) {
            this.notifyToRoot();
        }
        this.repaintContentPane();
    }

    /**
     * Align all visible bars in the focused figure.
     */
    void doAlignBars() {
        boolean changed = false;
        List<SGFigure> fList = this.getFocusedFigureList();
        for (int ii = 0; ii < fList.size(); ii++) {
            SGFigure f = (SGFigure) fList.get(ii);
            f.alignVisibleBars();
            if (f.isChangedRoot()) {
                changed = true;
            }
        }
        if (changed) {
            this.notifyToRoot();
        }
        this.repaintContentPane();
    }

    /**
     *
     * @return
     */
    public boolean isLocked() {
        return this.mLockFigureFlag;
    }

    /**
     *
     * @param b
     * @return
     */
    public boolean setLocked(final boolean b) {
        this.mLockFigureFlag = b;
        this.updateLockItems();
        return true;
    }

    /**
     *
     * @return
     */
    private void updateLockItems() {
        final boolean flag = this.isLocked();

        // set the toggle button
        this.mToolBar.setButtonSelected(MENUBARCMD_LOCK, flag);

        this.mMenuBar
                .setMenuItemSelected(MENUBAR_LAYOUT, MENUBARCMD_LOCK, flag);
    }

    /**
     * Discard all objects in the undo buffer.
     */
    public void clearUndoBuffer() {
        final boolean saved = this.isSaved();

        // initialize the undo buffer
        this.initUndoBuffer();

        // set the saved index
        if (saved) {
            this.mSavedListIndex = 0;
            this.updateStatusBarSavedFlag();
        } else {
            this.initSavedHistory();
        }

        // update items in the menu bar
        this.updateUndoItems();

        // notify to the root
        this.notifyToRoot();
    }

    /**
     *
     * @param idArray
     * @return
     */
    public boolean setSelectedFigure(final int[] idArray) {
        for (int ii = 0; ii < idArray.length; ii++) {
            SGFigure f = this.getFigure(idArray[ii]);
            f.setSelected(true);
        }
        this.updateFocusedObjectItem();
        this.repaintContentPane();
        return true;
    }

    /**
     *
     * @return
     */
    public String getClassDescription() {
        return this.getInstanceDescription();
    }

    /**
     *
     * @return
     */
    public String getInstanceDescription() {
        return "Window: " + this.mID;
    }

    // Creates the menu items in the menu bar for properties.
    private void createPropertyMenuBarItem() {
        // create an action event listener instance
        ActionListener l = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                NodeMenuItem item = (NodeMenuItem) source;

                SGINode node = item.node;
                SGIPropertyDialogObserver obs = (SGIPropertyDialogObserver) node;
                SGPropertyDialog dg = obs.getPropertyDialog();

                SGDrawingWindow.this.showPropertyDialog(dg, obs);
            }
        };

        this.mMenuBar.createPropertyMenuBarItem(this, l);
    }
    
    // The list of data plug-in.
    private static List<SGIPlugin> mDataPluginList = new ArrayList<SGIPlugin>();
    
    private static SGIPluginManager mDataPluginManager = null;
    
    /**
     * Sets the list of data plug-in.
     * 
     * @param pluginList
     *           the list of data plug-in
     */
    public static void setDataPlugins(List<SGIPlugin> pluginList) {
    	mDataPluginList = new ArrayList<SGIPlugin>(pluginList);
    }
    
    /**
     * Sets the data plug-in manager.
     * 
     * @param l
     *           the data plug-in manager
     */
    public static void setDataPluginManager(SGIPluginManager m) {
    	mDataPluginManager = m;
    }
    
    // Creates the menu items in the menu bar for data plug-in.
    private void createDataPluginMenuBarItem() {
    	ActionListener l = new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			String command = e.getActionCommand();
    			mDataPluginManager.execCommand(command, SGDrawingWindow.this);
    		}
    	};
        this.mMenuBar.createDataPluginMenuBarItem(mDataPluginList, l);
        this.updateDataPluginMenuBarItems();
    }
    
    // Updates menu items for data plug-in in the menu bar.
    private void updateDataPluginMenuBarItems() {
    	/*
        boolean eff = false;
        ArrayList<SGFigure> list = this.getVisibleFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = list.get(ii);
            SGIFigureElementGraph gElement = figure.getGraphElement();
            List<SGData> dataList = gElement.getFocusedDataList();
            if (dataList.size() > 0) {
            	eff = true;
            	break;
            }
        }
        List<JMenuItem> itemList = SGMenuBar.getAllMenuItems(this.mMenuBar, MENUBAR_PLUGIN);
        for (JMenuItem item : itemList) {
        	item.setEnabled(eff);
        }
        */
    }

    /**
     *
     * @param sb
     * @return
     */
    public boolean createTree(StringBuffer sb) {
        this.createTree(this, sb, 0);
        return true;
    }

    private void createTree(final SGINode node, final StringBuffer sb,
            final int depth) {
        final ArrayList childList = node.getChildNodes();
        final String cText = node.getClassDescription();
        final String iText = node.getInstanceDescription();
        final boolean pFlag = (node instanceof SGIPropertyDialogObserver);

        // has child objects
        if (childList.size() != 0) {
            if (cText != null && iText != null) {
                this.append(sb, iText, depth);

                final int d = depth + 1;

                // add child
                for (int ii = 0; ii < childList.size(); ii++) {
                    SGINode child = (SGINode) childList.get(ii);
                    this.createTree(child, sb, d);
                }
            }
        } else {
            if (iText != null) {
                // property dialog observer
                if (pFlag) {
                    this.append(sb, iText, depth);
                }
            }
        }
    }

    private void append(final StringBuffer sb, final String str, final int depth) {
        final String s = "  ";
        for (int ii = 0; ii < depth; ii++) {
            sb.append(s);
        }
        sb.append(str);
        sb.append(SGIConstants.LINE_SEPARATOR);
    }

    //
    // implementation of the SGIProgressControl
    //

    /**
     * Starts the progress.
     *
     */
    @Override
    public void startProgress() {
        this.mStatusBar.startProgress();
    }

    /**
     * Stops the progress.
     * 
     */
    @Override
    public void endProgress() {
        this.mStatusBar.endProgress();
    }

    /**
     * Starts the progress in indeterminate mode.
     * 
     */
    @Override
    public void startIndeterminateProgress() {
    	this.mStatusBar.startIndeterminateProgress();
    }

    /**
     * Sets the progress value.
     * 
     */
    @Override
    public void setProgressValue(final float ratio) {
        this.mStatusBar.setProgressValue(ratio);
    }

    /**
     * Set the messsage of the Progress bar.
     *
     * @param msg
     *            progress messasge
     */
    @Override
    public void setProgressMessage(final String msg) {
        this.mStatusBar.setProgressMessage(msg);
    }

    //
    // About component bounds
    //

    /**
     *
     * @return
     */
    private Rectangle2D getPaperRectInClientRect() {
        Rectangle2D rect = new Rectangle2D.Float(this.mPaperOrigin.x,
                this.mPaperOrigin.y, this.mClientPanel.getPaperWidth(),
                this.mClientPanel.getPaperHeight());
        return rect;
    }

    private final Rectangle2D mTempPaperRect = new Rectangle2D.Float();

    /**
     *
     * @return
     */
    boolean recordPaperRect() {
        this.mTempPaperRect.setRect(this.getPaperRectInClientRect());
        return true;
    }

    /**
     *
     * @return
     */
    public boolean isPaperBoundsChanged() {
        Rectangle temp = this.mTempPaperRect.getBounds();
        Rectangle present = this.getPaperRectInClientRect().getBounds();
        return !temp.equals(present);
    }

    /**
     * Align all figures.
     *
     * @return
     */
    public boolean alignFigures() {
        // record the location
        ArrayList list = this.getVisibleFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            figure.recordFigureRect();
        }
        this.recordPaperRect();

        // aligns figures
        if (this.alignFiguresByGraphAreaNew() == false) {
            return false;
        }

        //
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            if (figure.isFigureMoved()) {
                figure.setChanged(true);
            }
        }

        if (this.isPaperBoundsChanged()) {
            this.setChanged(true);
        }

        // notify to the root
        this.notifyToRoot();

        return true;
    }

    /**
     *
     * @param size
     * @return
     */
    public boolean setPaperSizeDirectly(final MediaSize size, boolean isPortrait) {
        // record the previous size
        Rectangle pRect = this.getPaperRect().getBounds();

        // set the size of the paper
        this.mClientPanel.setPaperSize(size, isPortrait);

        // compare the size
        Rectangle rect = this.getPaperRect().getBounds();
        if (pRect.equals(rect)) {
            return true;
        }

        // update the client rectangle
        this.updateClientRect();

        // set changed flag
        this.setChanged(true);
        this.notifyToRoot();

        //
        this.doAutoZoom();

        // repaint
        this.repaintContentPane();

        return true;
    }

    private static final float BOUNDING_BOX_MARGIN;

    static {
        final float ratio = SGIConstants.CM_POINT_RATIO;
        final float ten = (float) SGUtilityNumber
                .getPowersOfTen(LENGTH_MINIMAL_ORDER);
        BOUNDING_BOX_MARGIN = ten / ratio;
    }

    /**
     *
     * @return
     */
    public boolean setBoundingBox() {
        // Rectangle pRect = this.getPaperRect().getBounds();
        Rectangle2D cRect = this.getClientRect();

        ArrayList list = this.getVisibleFigureList();
        if (list.size() != 0) {
            // update the temporary rectangles
            for (int ii = 0; ii < list.size(); ii++) {
                SGFigure figure = (SGFigure) list.get(ii);
                figure.recordFigureRect();
            }
            this.recordPaperRect();

            // align figures
            Rectangle2D bbRect = this.getBoundingBoxOfFigures(list);
            for (int ii = 0; ii < list.size(); ii++) {
                SGFigure figure = (SGFigure) list.get(ii);
                float x = BOUNDING_BOX_MARGIN
                        + (float) (cRect.getX() + figure.getGraphRectX() - bbRect
                                .getX());
                float y = BOUNDING_BOX_MARGIN
                        + (float) (cRect.getY() + figure.getGraphRectY() - bbRect
                                .getY());
                figure.setGraphRectLocationRoundingOut(x, y);
                if (figure.isFigureMoved()) {
                    figure.setChanged(true);
                }
            }

            //
            this.setFigureBoundingBox(0);

            //
            if (this.isPaperBoundsChanged()) {
                this.setChanged(true);
            }

            // notify to the root
            this.notifyToRoot();

        } else {
            SGUtility.showMessageDialog(this, "There is no figure.",
                    "Failed to get the Bounding box.",
                    JOptionPane.WARNING_MESSAGE);
        }

        return true;
    }

    /**
     *
     */
    public boolean initPropertiesHistory() {
        return this.mUndoManager.initPropertiesHistory();
    }

//    //
//    private void updatePaperItems() {
//        this.mMenuBar.setMenuItemSelected(MENUBAR_LAYOUT,
//                MENUBARCMD_PAPER_PORTRAIT, this.getPaperPortrait());
//    }

    /**
     *
     */
    public boolean commit() {

        this.updateClientRect();
        this.doAutoZoom();
        this.updateGridItems();

        // update the history only when properties are changed
        SGProperties pTemp = this.mTemporaryProperties;
        SGProperties pPresent = this.getProperties();
        if (pTemp.equals(pPresent) == false) {
            this.setChanged(true);
        }

        this.mTemporaryProperties = null;

        this.repaintContentPane();

        return true;
    }

    /**
     *
     */
    public boolean updateHistory() {
        // System.out.println(this.isChanged());

        // update the updated index
        // this method must be called before SGUndoManager::updateHistory is
        // called
        this.updateSavedListIndex();

        // update the history
        if (this.mUndoManager.updateHistory(this.getVisibleFigureList()) == false) {
            return false;
        }

        // update items
        this.updateUndoItems();

        // update the status bar
        this.updateStatusBarSavedFlag();

        return true;
    }

    // update the index the properties has changed
    private void updateSavedListIndex() {
        boolean changed = false;
        if (this.isChanged()) {
            changed = true;
        } else {
            ArrayList list = this.getVisibleFigureList();
            for (int ii = 0; ii < list.size(); ii++) {
                SGFigure f = (SGFigure) list.get(ii);
                if (f.isChanged()) {
                    changed = true;
                    break;
                }
            }
        }

        //
        if (changed) {
            final int index = this.mUndoManager.getChangedObjectListIndex();
            if (index < this.mSavedListIndex) {
                this.initSavedHistory();
            }
        }
    }

    //
    private void updateStatusBarSavedFlag() {
        boolean b = false;
        if (this.getVisibleFigureList().size() != 0) {
            final int index = this.mUndoManager.getChangedObjectListIndex();
            b = (index != this.mSavedListIndex);
        }
        this.mStatusBar.setSaved(b);
    }

    /**
     *
     *
     */
    public void initSavedHistory() {
        this.mSavedListIndex = -1;
        this.updateStatusBarSavedFlag();
    }

    /**
     *
     */
    protected Set getAvailableChildSet() {
        Set set = new HashSet();
        List mList = this.mUndoManager.getMementoList();
        for (int ii = 0; ii < mList.size(); ii++) {
            WindowProperties p = (WindowProperties) mList.get(ii);
            set.addAll(p.mVisibleFigureList);
        }

        return set;
    }

    /**
     * Initialize the undo buffer.
     * Useless figures are deleted here.
     */
    public void initUndoBuffer() {

        // figures
        for (int ii = 0; ii < this.mFigureList.size(); ii++) {
            SGFigure f = (SGFigure) this.mFigureList.get(ii);
            f.initUndoBuffer();
        }

//        // dispose invisible figures
//        for (int ii = this.mFigureList.size() - 1; ii >= 0; ii--) {
//            SGFigure f = (SGFigure) this.mFigureList.get(ii);
//            if (!f.isVisible()) {
//        	this.mFigureList.remove(f);
//        	f.dispose();
//            }
//        }

        // initialize undo buffer
        this.mUndoManager.initUndoBuffer();

        // delete useless figures
        if (this.deleteUselessFigures() == false) {
            throw new Error("Failed to initialize undo buffer.");
        }

    }

    /**
     *
     * @return
     */
    public boolean isUndoable() {
        return this.mUndoManager.isUndoable();
    }

    /**
     *
     * @return
     */
    public boolean isRedoable() {
        return this.mUndoManager.isRedoable();
    }

    /**
     * Clear changed flag of this undoable object and all child objects.
     *
     */
    public void clearChanged() {
        this.setChanged(false);
        List fList = this.getVisibleFigureList();
        for (int ii = 0; ii < fList.size(); ii++) {
            SGFigure f = (SGFigure) fList.get(ii);
            f.clearChanged();
        }
    }

    /**
     *
     */
    public boolean cancel() {
        //
        if (this.setProperties(this.mTemporaryProperties) == false) {
            return false;
        }

        this.mTemporaryProperties = null;

        //
        this.updateClientRect();

        this.repaintContentPane();

        return true;
    }

    /**
     *
     */
    public boolean preview() {
        this.updateClientRect();
        this.doAutoZoom();
        this.updateGridItems();
        this.repaintContentPane();

        return true;
    }

    /**
     *
     * @return
     */
    public void setEnabled(boolean b) {
        super.setEnabled(b);
    }

    /**
     *
     * @return
     */
    public void setWaitCursor(final boolean b) {
        final Cursor cur = (b) ? new Cursor(Cursor.WAIT_CURSOR) : new Cursor(
                Cursor.DEFAULT_CURSOR);
        RootPaneContainer root = (RootPaneContainer) this.getRootPane()
                .getTopLevelAncestor();
        root.getGlassPane().setCursor(cur);
        root.getGlassPane().setVisible(b);
        if (b)
            root.getGlassPane().addMouseListener(new MouseAdapter() {
            });
    }

    /**
     * Returns a property dialog for the window.
     *
     * @return
     *        a property dialog
     */
    public SGPropertyDialog getPropertyDialog() {
        if (this.mPropertyDialog == null) {
            this.mPropertyDialog = new SGWindowDialog(this, true);
        }
        return this.mPropertyDialog;
    }

    /**
     *
     * @return
     */
    public boolean updateClientRect() {
        this.updateClientRectOld();
        return true;
    }

    // private Rectangle2D mTempRect = new Rectangle2D.Double();

    /**
     *
     *
     * @return
     */
    private boolean updateClientRectOld() {

        //
        // if the client rectangle does not contain the bounding box,
        // fit the client rectangle to the bounding box.
        //
        // if the viewport rectangle contains the bounding box,
        // fit the the client rect to the viewport rectangle.
        //

        // horizontal

        this.fitRect(this.mClientRect, this.getBoundingBox(), true);

        if (SGUtility.isRectContains(this.getViewportBounds(), this
                .getBoundingBox(), true)) {
            this.fitRect(this.mClientRect, this.getViewportBounds(), true);
        }

        if (SGUtility.isRectContains(this.getClientRect(), this
                .getViewportBounds(), true) == false) {
            Rectangle2D cRect = this.getClientRect();
            Rectangle2D vpRect = this.getViewportBounds();

            final boolean b1 = SGUtility.isRectContains(cRect, vpRect.getX(),
                    true);
            final boolean b2 = SGUtility.isRectContains(cRect, vpRect.getX()
                    + vpRect.getWidth(), true);

            double diff = 0.0;
            if (!b1 && b2) {
                diff = vpRect.getX() - cRect.getX();
            } else if (b1 && !b2) {
                diff = (vpRect.getX() + vpRect.getWidth())
                        - (cRect.getX() + cRect.getWidth());
            } else if (!b1 && !b2) {
                if (cRect.getX() < vpRect.getX()) {
                    diff = (vpRect.getX() + vpRect.getWidth())
                            - (cRect.getX() + cRect.getWidth());
                } else {
                    diff = vpRect.getX() - cRect.getX();
                }
            }

            this.setClientRect((float) (cRect.getX() + diff), (float) cRect
                    .getY(), (float) cRect.getWidth(), (float) cRect
                    .getHeight());
        }

        // vertical

        this.fitRect(this.mClientRect, this.getBoundingBox(), false);

        if (SGUtility.isRectContains(this.getViewportBounds(), this
                .getBoundingBox(), false)) {
            this.fitRect(this.mClientRect, this.getViewportBounds(), false);
        }

        if (SGUtility.isRectContains(this.getClientRect(), this
                .getViewportBounds(), false) == false) {
            Rectangle2D cRect = this.getClientRect();
            Rectangle2D vpRect = this.getViewportBounds();

            final boolean b1 = SGUtility.isRectContains(cRect, vpRect.getY(),
                    false);
            final boolean b2 = SGUtility.isRectContains(cRect, vpRect.getY()
                    + vpRect.getHeight(), false);

            double diff = 0.0;
            if (!b1 && b2) {
                diff = vpRect.getY() - cRect.getY();
            } else if (b1 && !b2) {
                diff = (vpRect.getY() + vpRect.getHeight())
                        - (cRect.getY() + cRect.getHeight());
            } else if (!b1 && !b2) {
                if (cRect.getY() < vpRect.getY()) {
                    diff = (vpRect.getY() + vpRect.getHeight())
                            - (cRect.getY() + cRect.getHeight());
                } else {
                    diff = vpRect.getY() - cRect.getY();
                }
            }

            this.setClientRect((float) cRect.getX(),
                    (float) (cRect.getY() + diff), (float) cRect.getWidth(),
                    (float) cRect.getHeight());
        }

        final Rectangle2D bbRect = this.getBoundingBox();
        final Rectangle2D vpRect = this.getViewportBounds();
        final Rectangle2D cRect = this.getClientRect();

        this.mClientPanel.setScrollBarValue(cRect, vpRect);

        //
        this.mClientPanel.setEnableScrollBars(vpRect, bbRect);

        if (SGUtility.isRectContains(vpRect, bbRect, true)) {
            this.fitRect(this.mClientRect, vpRect, true);
        }
        if (SGUtility.isRectContains(vpRect, bbRect, false)) {
            this.fitRect(this.mClientRect, vpRect, false);
        }

        //
        this.mClientPanel.setScrollBarValue(cRect, vpRect);

        return true;
    }

    /**
     * Fit rect1 to rect2.
     *
     * @param rect1
     * @param rect2
     * @param flag -
     *            true: x-direction, false: y-direction
     */
    private void fitRect(Rectangle2D rect1, Rectangle2D rect2,
            final boolean flag) {
        if (flag) {
            rect1.setRect(rect2.getX(), rect1.getY(), rect2.getWidth(), rect1
                    .getHeight());
        } else {
            rect1.setRect(rect1.getX(), rect2.getY(), rect1.getWidth(), rect2
                    .getHeight());
        }
    }

    /**
     *
     */
    public boolean setProperties(SGProperties p) {
        // System.out.println("setProperties");

        if ((p instanceof WindowProperties) == false)
            return false;

        WindowProperties wp = (WindowProperties) p;

        final Float w = wp.getPaperWidth();
        final Float h = wp.getPaperHeight();
        if (w == null || h == null) {
            return false;
        }

        this.mClientPanel.setPaperSize(w.floatValue(), h.floatValue());

        final Color bgColor = wp.getBackgroundColor();
        if (bgColor == null) {
            return false;
        }
        this.mClientPanel.setPaperColor(bgColor);

        final Color gridColor = wp.getGridColor();
        if (gridColor == null) {
            return false;
        }
        this.mClientPanel.setGridLineColor(gridColor);

        final Boolean gridVisible = wp.getGridVisible();
        if (gridVisible == null) {
            return false;
        }
        this.mClientPanel.setGridLineVisible(gridVisible.booleanValue());

        final Float gridInterval = wp.getGridInterval();
        if (gridInterval == null) {
            return false;
        }
        this.mClientPanel.setGridLineInterval(gridInterval.floatValue());

        final Float gridLineWidth = wp.getGridLineWidth();
        if (gridLineWidth == null) {
            return false;
        }
        this.mClientPanel.setGridLineWidth(gridLineWidth.floatValue());

        final Float imageLocationX = wp.getImageLocationX();
        if (imageLocationX == null) {
            return false;
        }
        this.mClientPanel.setImageLocationX(imageLocationX.floatValue());

        final Float imageLocationY = wp.getImageLocationY();
        if (imageLocationY == null) {
            return false;
        }
        this.mClientPanel.setImageLocationY(imageLocationY.floatValue());

        final Float imageScalingFactor = wp.getImageScalingFactor();
        if (imageScalingFactor == null) {
            return false;
        }
        this.mClientPanel
                .setImageScalingFactor(imageScalingFactor.floatValue());

        final Image img = wp.getImage();
        this.mClientPanel.setImage(img);
        if (img == null) {
        	this.mBackgroundImage = null;
        }

        this.setVisibleFigure(wp.getVisibleFigureList());

        return true;
    }

    /**
     *
     * @return
     */
    public SGProperties getProperties() {

        final WindowProperties p = new WindowProperties();

        p.setPaperWidth(this.mClientPanel.getPaperWidth());
        p.setPaperHeight(this.mClientPanel.getPaperHeight());
        p.setBackGroundColor(this.mClientPanel.getPaperColor());
        p.setGridColor(this.mClientPanel.getGridLineColor());
        p.setGridVisible(this.mClientPanel.isGridLineVisible());
        p.setGridInterval(this.mClientPanel.getGridLineInterval());
        p.setGridLineWidth(this.mClientPanel.getGridLineWidth());
        p.setVisibleFigureList(this.getVisibleFigureList());
        p.setImageLocationX(this.getImageLocationX());
        p.setImageLocationY(this.getImageLocationY());
        p.setImageScalingFactor(this.getImageScalingFactor());
        p.setImage(this.getImage());

        return p;

    }

    /**
     *
     */
    protected boolean setVisibleFigure(final List list) {
        return SGUtility.setVisibleList(this.mFigureList, list);
    }

    /**
     *
     * @return
     */
    public Rectangle2D getClientRect() {
        if (this.mClientRect == null) {
            return null;
        }
        return (Rectangle2D) this.mClientRect.clone();
    }

    // public void dumpClientRect()
    // {
    // Rectangle2D rect = this.getClientRect();
    // final double x = rect.getX();
    // final double y = rect.getY();
    // final double w = rect.getWidth();
    // final double h = rect.getHeight();
    //
    // System.out.println("x="+x*SGIConstants.CM_POINT_RATIO+"cm,
    // y="+y*SGIConstants.CM_POINT_RATIO+"cm");
    // System.out.println("w="+w*SGIConstants.CM_POINT_RATIO+"cm,
    // h="+h*SGIConstants.CM_POINT_RATIO+"cm");
    // System.out.println();
    // }

    // public void dumpRect()
    // {
    // Rectangle2D cRect = this.getClientRect();
    // Rectangle2D vpRect = this.getViewportBounds();
    // Rectangle2D bbRect = this.getBoundingBox();
    //
    // Rectangle2D cRect_ = new Rectangle2D.Float();
    // Rectangle2D vpRect_ = new Rectangle2D.Float();
    // Rectangle2D bbRect_ = new Rectangle2D.Float();
    //
    // cRect_.setRect(
    // (float)cRect.getX()*SGIConstants.CM_POINT_RATIO,
    // (float)cRect.getY()*SGIConstants.CM_POINT_RATIO,
    // (float)cRect.getWidth()*SGIConstants.CM_POINT_RATIO,
    // (float)cRect.getHeight()*SGIConstants.CM_POINT_RATIO
    // );
    //
    // vpRect_.setRect(
    // (float)vpRect.getX()*SGIConstants.CM_POINT_RATIO,
    // (float)vpRect.getY()*SGIConstants.CM_POINT_RATIO,
    // (float)vpRect.getWidth()*SGIConstants.CM_POINT_RATIO,
    // (float)vpRect.getHeight()*SGIConstants.CM_POINT_RATIO
    // );
    //
    // bbRect_.setRect(
    // (float)bbRect.getX()*SGIConstants.CM_POINT_RATIO,
    // (float)bbRect.getY()*SGIConstants.CM_POINT_RATIO,
    // (float)bbRect.getWidth()*SGIConstants.CM_POINT_RATIO,
    // (float)bbRect.getHeight()*SGIConstants.CM_POINT_RATIO
    // );
    //
    // System.out.println("client:"+cRect_);
    // System.out.println("viewport:"+vpRect_);
    // System.out.println("bounding box:"+bbRect_);
    // }

    /**
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    public boolean setClientRect(final float x, final float y, final float w,
            final float h) {
        // this.dumpClientRect();
        this.mClientRect.setRect(x, y, w, h);
        // System.out.println(this.mClientRect);
        return true;
    }

    /**
     *
     * @param rect
     * @return
     */
    public boolean setClientRect(Rectangle2D rect) {
        this.mClientRect.setRect(rect);
        return true;
    }

    /**
     *
     * @return
     */
    public Rectangle2D getViewportBounds() {
        final SGTuple2f dim = this.getViewportSize();
        final float w = dim.x;
        final float h = dim.y;
        Rectangle2D rect = new Rectangle2D.Float(0.0f, 0.0f, w, h);
        return rect;
    }

    public Rectangle2D getViewportBoundsInLayeredPane() {
        final SGTuple2f dim = this.getViewportSize();
        final float w = dim.x;
        final float h = dim.y;
        final int rw = this.mClientPanel.getRulerWidth();
        Rectangle2D rect = new Rectangle2D.Float(rw, rw, w, h);
        return rect;
    }

    /**
     *
     * @return
     */
    public Rectangle2D getViewportBoundsInComponent() {
        final int top = this.getTopWidth();
        final int left = this.getLeftWidth();
        final int rw = this.mClientPanel.getRulerWidth();
        final SGTuple2f dim = this.getViewportSize();
        final float w = dim.x + rw;
        final float h = dim.y + rw;
        Rectangle2D rect = new Rectangle2D.Float(left, top, w, h);
        // System.out.println(rect);
        return rect;
    }

    /**
     *
     * @return
     */
    protected SGFigure[][] getOrderedFigureArray() {

        // get the visible figure list
        ArrayList<SGFigure> list = this.getVisibleFigureList();

        // get the size of array
        final int n = list.size();
        if (n == 0) {
            return new SGFigure[0][0];
        }
        int size = 0;
        for (int ii = 1; ii <= 16; ii++) {
            final int sqSmall = (ii - 1) * (ii - 1);
            final int sqLarge = ii * ii;
            if ((sqSmall < n) && (n <= sqLarge)) {
                size = ii;
                break;
            }
        }
        int sx = size;
        int div = n / sx;
        int sy = n % sx == 0 ? div : div + 1;

        // create a figure array
        final SGFigure[][] figureArray = new SGFigure[sy][sx];

        //
        // in the order of figure-ID
        //

        boolean flag = true;
        for (int ny = 0; ny < sy; ny++) {
            for (int nx = 0; nx < sx; nx++) {
                final int index = ny * sx + nx;
                if (index >= list.size()) {
                    flag = false;
                    break;
                }
                figureArray[ny][nx] = (SGFigure) list.get(index);
            }
            if (!flag) {
                break;
            }
        }

        return figureArray;
    }

    /**
     * Returns a two dimensional array of figure list.
     */
    private ArrayList[][] getFigureListArray() {
        // get the visible figure list
        ArrayList<SGFigure> figureList = this.getVisibleFigureList();
        if (figureList.size() == 0) {
            return null;
        }

        // width of division
        float minWidth = Float.MAX_VALUE;
        float minHeight = Float.MAX_VALUE;
        for (int ii = 0; ii < figureList.size(); ii++) {
            SGFigure figure = (SGFigure) figureList.get(ii);
            Rectangle2D rect = figure.getGraphRect();
            if (rect.getWidth() < minWidth) {
                minWidth = (float) rect.getWidth();
            }
            if (rect.getHeight() < minHeight) {
                minHeight = (float) rect.getHeight();
            }
        }
        final float dx = minWidth;
        final float dy = minHeight;

        Rectangle2D bbRect = this.getBoundingBoxOfFigures(figureList);

        final int numX = (int) ((float) bbRect.getWidth() / dx) + 1;
        final int numY = (int) ((float) bbRect.getHeight() / dy) + 1;

        // get a two-dimensional array of figures
        ArrayList[][] fListArray = new ArrayList[numX][numY];
        for (int ii = 0; ii < numX; ii++) {
            for (int jj = 0; jj < numY; jj++) {
                fListArray[ii][jj] = new ArrayList();
            }
        }
        for (int ii = 0; ii < figureList.size(); ii++) {
            SGFigure figure = (SGFigure) figureList.get(ii);
            Rectangle2D gRect = figure.getGraphRect();
            int nx = (int) ((gRect.getCenterX() - bbRect.getX()) / dx);
            int ny = (int) ((gRect.getCenterY() - bbRect.getY()) / dy);
            fListArray[nx][ny].add(figure);
        }

        ArrayList numListX = new ArrayList();
        for (int nx = 0; nx < numX; nx++) {
            boolean flag = false;
            for (int ny = 0; ny < numY; ny++) {
                if (fListArray[nx][ny].size() != 0) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                numListX.add(Integer.valueOf(nx));
            }
        }

        ArrayList numListY = new ArrayList();
        for (int ny = 0; ny < numY; ny++) {
            boolean flag = false;
            for (int nx = 0; nx < numX; nx++) {
                if (fListArray[nx][ny].size() != 0) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                numListY.add(Integer.valueOf(ny));
            }
        }

        final int sx = numListX.size();
        final int sy = numListY.size();

        ArrayList[][] figureListArray = new ArrayList[sx][sy];
        for (int ii = 0; ii < sx; ii++) {
            final int nx = ((Integer) numListX.get(ii)).intValue();
            for (int jj = 0; jj < sy; jj++) {
                final int ny = ((Integer) numListY.get(jj)).intValue();
                figureListArray[ii][jj] = fListArray[nx][ny];
            }
        }

        return figureListArray;
    }

    /**
     *
     * @return
     */
    private boolean alignFiguresLeftAndBottom(ArrayList[][] figureListArray) {
        final int sx = figureListArray.length;
        final int sy = figureListArray[0].length;

        //
        final float[][] topArray = new float[sx][sy];
        final float[][] bottomArray = new float[sx][sy];
        final float[][] leftArray = new float[sx][sy];
        final float[][] rightArray = new float[sx][sy];
        for (int ii = 0; ii < sx; ii++) {
            for (int jj = 0; jj < sy; jj++) {
                ArrayList list = figureListArray[ii][jj];
                float maxTop = 0.0f;
                float maxBottom = 0.0f;
                float maxLeft = 0.0f;
                float maxRight = 0.0f;
                for (int kk = 0; kk < list.size(); kk++) {
                    SGFigure figure = (SGFigure) list.get(kk);
                    Rectangle2D rect = figure.getGraphRect();
                    final float width = (float) rect.getWidth();
                    final float height = (float) rect.getHeight();
                    SGTuple2f tb = new SGTuple2f();
                    SGTuple2f lr = new SGTuple2f();
                    figure.calcMargin(tb, lr);
                    final float top = tb.x;
                    final float bottom = tb.y;
                    final float left = lr.x;
                    final float right = lr.y;
                    if (top + height > maxTop) {
                        maxTop = top + height;
                    }
                    if (bottom > maxBottom) {
                        maxBottom = bottom;
                    }
                    if (left > maxLeft) {
                        maxLeft = left;
                    }
                    if (right + width > maxRight) {
                        maxRight = right + width;
                    }
                }

                topArray[ii][jj] = maxTop;
                bottomArray[ii][jj] = maxBottom;
                leftArray[ii][jj] = maxLeft;
                rightArray[ii][jj] = maxRight;
            }
        }

        // get arrays of the width and the height
        final float[] widthArray = new float[sx];
        for (int nx = 0; nx < sx; nx++) {
            float wMax = 0.0f;
            for (int ny = 0; ny < sy; ny++) {
                float width = leftArray[nx][ny] + rightArray[nx][ny];
                if (width > wMax) {
                    wMax = width;
                }
            }
            widthArray[nx] = wMax;
        }

        final float[] heightArray = new float[sy];
        for (int ny = 0; ny < sy; ny++) {
            float hMax = 0.0f;
            for (int nx = 0; nx < sx; nx++) {
                float height = topArray[nx][ny] + bottomArray[nx][ny];
                if (height > hMax) {
                    hMax = height;
                }
            }
            heightArray[ny] = hMax;
        }

        // get arrays of the width and the height
        final float[] maxLeftArray = new float[sx];
        for (int nx = 0; nx < sx; nx++) {
            float wMax = 0.0f;
            for (int ny = 0; ny < sy; ny++) {
                float width = leftArray[nx][ny];
                if (width > wMax) {
                    wMax = width;
                }
            }
            maxLeftArray[nx] = wMax;
        }

        final float[] maxRightArray = new float[sx];
        for (int nx = 0; nx < sx; nx++) {
            float wMax = 0.0f;
            for (int ny = 0; ny < sy; ny++) {
                float width = rightArray[nx][ny];
                if (width > wMax) {
                    wMax = width;
                }
            }
            maxRightArray[nx] = wMax;
        }

        final float[] maxBottomArray = new float[sy];
        for (int ny = 0; ny < sy; ny++) {
            float hMax = 0.0f;
            for (int nx = 0; nx < sx; nx++) {
                float height = bottomArray[nx][ny];
                if (height > hMax) {
                    hMax = height;
                }
            }
            maxBottomArray[ny] = hMax;
        }

        // create arrays of the coordinate of the left-bottom corner
        final float diff = this.getMagnification()
                * this.mClientPanel.getGridLineInterval();
        Rectangle2D pRect = this.getPaperRect();
        final float px = (float) pRect.getX();
        final float py = (float) pRect.getY();
        float x = px;
        float y = py;
        final float[] originXArray = new float[sx];
        for (int nx = 0; nx < sx; nx++) {
            final float value = x + maxLeftArray[nx];
            final int index = (int) ((value - px) / diff) + 1;
            originXArray[nx] = px + index * diff;
            x = originXArray[nx] + maxRightArray[nx];
        }
        final float[] originYArray = new float[sy];
        for (int ny = 0; ny < sy; ny++) {
            final float value = y + heightArray[ny] - maxBottomArray[ny];
            final int index = (int) ((value - py) / diff) + 1;
            originYArray[ny] = py + index * diff;
            y = originYArray[ny] + maxBottomArray[ny];
        }

        // set the location of figures
        boolean flag = true;
        for (int ny = 0; ny < sy; ny++) {
            for (int nx = 0; nx < sx; nx++) {
                ArrayList list = figureListArray[nx][ny];
                for (int ii = 0; ii < list.size(); ii++) {
                    SGFigure figure = (SGFigure) list.get(ii);
                    if (figure == null) {
                        flag = false;
                        break;
                    }

                    if (figure.setGraphRectLocationByLeftBottom(
                            originXArray[nx], originYArray[ny]) == false) {
                        return false;
                    }
                }
                if (!flag) {
                    break;
                }
            }
            if (!flag) {
                break;
            }
        }

        return true;
    }

    // private Float findCeilingValue( final float[] array, final float value )
    // {
    // float[] copy = (float[])array.clone();
    // Arrays.sort(copy);
    //
    // for( int ii=0; ii<copy.length; ii++ )
    // {
    // if( value <= copy[ii] )
    // {
    // return Float.valueOf( copy[ii] );
    // }
    // }
    //
    // return null;
    // }

    /**
     *
     * @return
     */
    public boolean alignFiguresByGraphArea() {
        // get the visible figure list
        ArrayList figureList = this.getVisibleFigureList();
        if (figureList.size() == 0) {
            return true;
        }

        Rectangle2D cRect = this.mClientRect;

        // width of division
        float minWidth = Float.MAX_VALUE;
        float minHeight = Float.MAX_VALUE;
        for (int ii = 0; ii < figureList.size(); ii++) {
            SGFigure figure = (SGFigure) figureList.get(ii);
            Rectangle2D rect = figure.getGraphRect();
            if (rect.getWidth() < minWidth) {
                minWidth = (float) rect.getWidth();
            }
            if (rect.getHeight() < minHeight) {
                minHeight = (float) rect.getHeight();
            }
        }
        final float dx = minWidth;
        final float dy = minHeight;

        Rectangle2D bbRect = this.getBoundingBoxOfFigures(figureList);

        // final int numX = (int)( (float)cRect.getWidth()/dx ) + 1;
        // final int numY = (int)( (float)cRect.getHeight()/dy ) + 1;

        final int numX = (int) ((float) bbRect.getWidth() / dx) + 1;
        final int numY = (int) ((float) bbRect.getHeight() / dy) + 1;

        // get a two-dimensional array of figures
        ArrayList[][] fListArray = new ArrayList[numX][numY];
        for (int ii = 0; ii < numX; ii++) {
            for (int jj = 0; jj < numY; jj++) {
                fListArray[ii][jj] = new ArrayList();
            }
        }
        for (int ii = 0; ii < figureList.size(); ii++) {
            SGFigure figure = (SGFigure) figureList.get(ii);
            Rectangle2D gRect = figure.getGraphRect();

            // int nx = (int)( ( gRect.getCenterX() - cRect.getX() )/dx );
            // int ny = (int)( ( gRect.getCenterY() - cRect.getY() )/dy );

            int nx = (int) ((gRect.getCenterX() - bbRect.getX()) / dx);
            int ny = (int) ((gRect.getCenterY() - bbRect.getY()) / dy);

            fListArray[nx][ny].add(figure);
        }

        ArrayList numListX = new ArrayList();
        for (int nx = 0; nx < numX; nx++) {
            boolean flag = false;
            for (int ny = 0; ny < numY; ny++) {
                if (fListArray[nx][ny].size() != 0) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                numListX.add(Integer.valueOf(nx));
            }
        }

        ArrayList numListY = new ArrayList();
        for (int ny = 0; ny < numY; ny++) {
            boolean flag = false;
            for (int nx = 0; nx < numX; nx++) {
                if (fListArray[nx][ny].size() != 0) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                numListY.add(Integer.valueOf(ny));
            }
        }

        final int sx = numListX.size();
        final int sy = numListY.size();

        ArrayList[][] figureListArray = new ArrayList[sx][sy];
        for (int ii = 0; ii < sx; ii++) {
            final int nx = ((Integer) numListX.get(ii)).intValue();
            for (int jj = 0; jj < sy; jj++) {
                final int ny = ((Integer) numListY.get(jj)).intValue();
                figureListArray[ii][jj] = fListArray[nx][ny];
            }
        }

        //
        float[][] topArray = new float[sx][sy];
        float[][] bottomArray = new float[sx][sy];
        float[][] leftArray = new float[sx][sy];
        float[][] rightArray = new float[sx][sy];
        for (int ii = 0; ii < sx; ii++) {
            for (int jj = 0; jj < sy; jj++) {
                if (figureListArray[ii][jj] == null) {
                    continue;
                }

                ArrayList list = figureListArray[ii][jj];
                float maxTop = 0.0f;
                float maxBottom = 0.0f;
                float maxLeft = 0.0f;
                float maxRight = 0.0f;
                for (int kk = 0; kk < list.size(); kk++) {
                    SGFigure figure = (SGFigure) list.get(kk);
                    Rectangle2D rect = figure.getGraphRect();
                    SGTuple2f tb = new SGTuple2f();
                    SGTuple2f lr = new SGTuple2f();
                    figure.calcMargin(tb, lr);
                    if (tb.x + (float) rect.getHeight() > maxTop) {
                        maxTop = tb.x + (float) rect.getHeight();
                    }
                    if (tb.y > maxBottom) {
                        maxBottom = tb.y;
                    }
                    if (lr.x > maxLeft) {
                        maxLeft = lr.x;
                    }
                    if (lr.y + (float) rect.getWidth() > maxRight) {
                        maxRight = lr.y + (float) rect.getWidth();
                    }
                }

                topArray[ii][jj] = maxTop;
                bottomArray[ii][jj] = maxBottom;
                leftArray[ii][jj] = maxLeft;
                rightArray[ii][jj] = maxRight;

            }
        }

        // get arrays of the width and the height
        final float[] widthArray = new float[sx];
        for (int nx = 0; nx < sx; nx++) {
            float wMax = 0.0f;
            for (int ny = 0; ny < sy; ny++) {
                float width = leftArray[nx][ny] + rightArray[nx][ny];
                if (width > wMax) {
                    wMax = width;
                }
            }
            widthArray[nx] = wMax;
        }

        final float[] heightArray = new float[sy];
        for (int ny = 0; ny < sy; ny++) {
            float hMax = 0.0f;
            for (int nx = 0; nx < sx; nx++) {
                float height = topArray[nx][ny] + bottomArray[nx][ny];
                if (height > hMax) {
                    hMax = height;
                }
            }
            heightArray[ny] = hMax;
        }

        // get arrays of the width and the height
        final float[] maxLeftArray = new float[sx];
        for (int nx = 0; nx < sx; nx++) {
            float wMax = 0.0f;
            for (int ny = 0; ny < sy; ny++) {
                float width = leftArray[nx][ny];
                if (width > wMax) {
                    wMax = width;
                }
            }
            maxLeftArray[nx] = wMax;
        }

        final float[] maxBottomArray = new float[sy];
        for (int ny = 0; ny < sy; ny++) {
            float hMax = 0.0f;
            for (int nx = 0; nx < sx; nx++) {
                float height = bottomArray[nx][ny];
                if (height > hMax) {
                    hMax = height;
                }
            }
            maxBottomArray[ny] = hMax;
        }

        // create arrays of the coordinate of the centers
        final float[] originXArray = new float[sx];
        float cx = (float) cRect.getX();
        for (int nx = 0; nx < sx; nx++) {
            originXArray[nx] = cx + maxLeftArray[nx];
            cx += widthArray[nx];
        }

        final float[] originYArray = new float[sy];
        float cy = (float) cRect.getY();
        for (int ny = 0; ny < sy; ny++) {
            originYArray[ny] = cy + heightArray[ny] - maxBottomArray[ny];
            cy += heightArray[ny];
        }

        // set the location of figures
        boolean flag = true;
        for (int ny = 0; ny < sy; ny++) {
            for (int nx = 0; nx < sx; nx++) {
                ArrayList list = figureListArray[nx][ny];
                for (int ii = 0; ii < list.size(); ii++) {
                    SGFigure figure = (SGFigure) list.get(ii);
                    if (figure == null) {
                        flag = false;
                        break;
                    }

                    if (figure.setGraphRectLocationByLeftBottom(
                            originXArray[nx], originYArray[ny]) == false) {
                        return false;
                    }
                }
                if (!flag) {
                    break;
                }
            }
            if (!flag) {
                break;
            }
        }

        /*
         * // enlarge the size of paper int mode = -1; float wTotal = 0.0f;
         * float hTotal = 0.0f; for( int ii=0; ii<widthArray.length; ii++ ) {
         * wTotal += widthArray[ii]; } for( int ii=0; ii<heightArray.length;
         * ii++ ) { hTotal += heightArray[ii]; } Rectangle2D pRect =
         * this.getPaperRect(); final boolean bw = ( pRect.getWidth() < wTotal );
         * final boolean bh = ( pRect.getHeight() < hTotal );
         *
         *
         * if( bw && bh ) { mode = 0; } else if( bw ) { mode = 1; } else if( bh ) {
         * mode = 2; }
         */

        final int mode = 0;

        // if( mode!=-1 )
        {
            if (this.setFigureBoundingBox(mode) == false) {
                return false;
            }
        }

        return true;

    }

    /**
     *
     * @return
     */
    private boolean alignFiguresByGraphAreaNew() {

        // get the visible figure list
        ArrayList figureList = this.getVisibleFigureList();
        if (figureList.size() == 0) {
            return true;
        }

        // get a two-dimensional array of the list of figures
        ArrayList[][] figureListArray = this.getFigureListArray();
        if (figureListArray == null) {
            return false;
        }
        if (figureListArray.length == 0) {
            return false;
        }

        // align figures
        if (this.alignFiguresLeftAndBottom(figureListArray) == false) {
            return false;
        }

        // set bounding box
        if (this.setFigureBoundingBox(0) == false) {
            return false;
        }

        return true;
    }

    /*
     * public static final double OVERLAP_RATIO = 0.50;
     *
     *
     * private boolean isOverlapping( SGFigure figure1, SGFigure figure2, final
     * boolean flag ) {
     *
     * Rectangle2D rect1 = figure1.getGraphAreaRect(); Rectangle2D rect2 =
     * figure2.getGraphAreaRect();
     *
     * final double value = SGUtility.getOverlapping( rect1, rect2, flag );
     *
     * boolean ret = false; if( flag ) { final double ratio1 =
     * value/rect1.getWidth(); final double ratio2 = value/rect2.getWidth(); if(
     * ratio1>OVERLAP_RATIO || ratio2>OVERLAP_RATIO ) { ret = true; } } else {
     * final double ratio1 = value/rect1.getHeight(); final double ratio2 =
     * value/rect2.getHeight(); if( ratio1>OVERLAP_RATIO || ratio2>OVERLAP_RATIO ) {
     * ret = true; } }
     *
     * return ret; }
     *
     */

    /**
     * Returns the relative location of figure2 to figure1.
     *
     * @param figure1
     * @param figure2
     * @return 0:top 1:bottom 2:left 3:right
     */
    /*
     * private int getAlignment( SGFigure figure1, SGFigure figure2 ) {
     * Rectangle2D rect1 = figure1.getGraphAreaRect(); Rectangle2D rect2 =
     * figure2.getGraphAreaRect();
     *
     * final double vx = rect2.getCenterX() - rect1.getCenterX(); final double
     * vy = rect2.getCenterY() - rect1.getCenterY();
     *
     * final double angle = Math.atan2(vy,vx);
     *
     *
     * int num = -1; if( -0.75*Math.PI<=angle && angle<-0.25*Math.PI ) { num =
     * 0; } else if( 0.25*Math.PI<=angle && angle<0.75*Math.PI ) { num = 1; }
     * else if( ( -Math.PI<=angle && angle<-0.75*Math.PI ) || ( 0.75*Math.PI<=angle &&
     * angle<=Math.PI ) ) { num = 2; } else if( -0.25*Math.PI<=angle && angle<0.25*Math.PI ) {
     * num = 3; }
     *
     * return num; }
     */

    /*
     * class Figure { SGFigure fig; Figure top; Figure bottom; Figure left;
     * Figure right; Figure topLeft; Figure topRight; Figure bottomLeft; Figure
     * bottomRight; ArrayList topList = new ArrayList(); ArrayList bottomList =
     * new ArrayList(); ArrayList leftList = new ArrayList(); ArrayList
     * rightList = new ArrayList();
     *
     * public String toString() { if( fig==null ) { return "null"; } else {
     * return fig.toString(); } } }
     */

    /**
     * Order the figures.
     *
     * @return
     */
    public boolean alignFiguresByBoundingBox() {
        boolean flag;

        final SGFigure[][] figureArray = this.getOrderedFigureArray();
        if (figureArray == null) {
            return false;
        }
        if (figureArray.length == 0) {
            return true;
        }
        final int sy = figureArray.length;
        final int sx = figureArray[0].length;

        // create an array of the bounding box of the figures
        Rectangle2D[][] rectArray = new Rectangle2D[sy][sx];
        flag = true;
        for (int ny = 0; ny < sy; ny++) {
            for (int nx = 0; nx < sx; nx++) {
                if (figureArray[ny][nx] == null) {
                    flag = false;
                    break;
                }
                rectArray[ny][nx] = figureArray[ny][nx].getBoundingBox();
            }
            if (!flag) {
                break;
            }
        }

        /*
         * for( int ny=0; ny<sy; ny++ ) { for( int nx=0; nx<sx; nx++ ) {
         * System.out.println(ny+" "+nx+" "+array[ny][nx]); } }
         * System.out.println();
         */

        // get arrays of the width and the height
        final float[] widthArray = new float[sx];
        for (int nx = 0; nx < sx; nx++) {
            float wMax = 0.0f;
            for (int ny = 0; ny < sy; ny++) {
                Rectangle2D rect = rectArray[ny][nx];
                if (rect == null) {
                    break;
                }
                float width = (float) rectArray[ny][nx].getWidth();
                if (width > wMax) {
                    wMax = width;
                }
            }
            widthArray[nx] = wMax;
        }

        final float[] heightArray = new float[sy];
        for (int ny = 0; ny < sy; ny++) {
            float hMax = 0.0f;
            for (int nx = 0; nx < sx; nx++) {
                Rectangle2D rect = rectArray[ny][nx];
                if (rect == null) {
                    break;
                }
                float height = (float) rectArray[ny][nx].getHeight();
                if (height > hMax) {
                    hMax = height;
                }
            }
            heightArray[ny] = hMax;
        }

        /*
         * for( int ii=0; ii<sx; ii++ ) { System.out.println(ii+"
         * "+widthArray[ii]); } System.out.println();
         *
         * for( int ii=0; ii<sy; ii++ ) { System.out.println(ii+"
         * "+heightArray[ii]); } System.out.println();
         */

        // create arrays of the coordinate of the centers
        Rectangle2D cRect = this.getClientRect();

        final float[] centerXArray = new float[sx];
        float cx = (float) cRect.getX();
        for (int nx = 0; nx < sx; nx++) {
            centerXArray[nx] = cx + widthArray[nx] / 2.0f;
            cx += widthArray[nx];
        }

        final float[] centerYArray = new float[sy];
        float cy = (float) cRect.getY();
        for (int ny = 0; ny < sy; ny++) {
            centerYArray[ny] = cy + heightArray[ny] / 2.0f;
            cy += heightArray[ny];
        }

        /*
         * for( int ii=0; ii<sx; ii++ ) { System.out.println(ii+"
         * "+centerXArray[ii]); } System.out.println();
         *
         * for( int ii=0; ii<sy; ii++ ) { System.out.println(ii+"
         * "+centerYArray[ii]); } System.out.println();
         */

        // set the location of figures
        flag = true;
        for (int ny = 0; ny < sy; ny++) {
            for (int nx = 0; nx < sx; nx++) {
                // final int index = ny*sx + nx;
                SGFigure figure = figureArray[ny][nx];
                if (figure == null) {
                    flag = false;
                    break;
                }
                if (figure.setCenter(centerXArray[nx], centerYArray[ny]) == false) {
                    return false;
                }
            }
            if (!flag) {
                break;
            }
        }

        // enlarge the size of paper
        int mode = -1;
        float wTotal = 0.0f;
        float hTotal = 0.0f;
        for (int ii = 0; ii < widthArray.length; ii++) {
            wTotal += widthArray[ii];
        }
        for (int ii = 0; ii < heightArray.length; ii++) {
            hTotal += heightArray[ii];
        }
        Rectangle2D pRect = this.getPaperRect();
        final boolean bw = (pRect.getWidth() < wTotal);
        final boolean bh = (pRect.getHeight() < hTotal);

        // System.out.println("pRect:"+pRect);
        // System.out.println("wTotal="+wTotal);
        // System.out.println("hTotal="+hTotal);

        if (bw && bh) {
            mode = 0;
        } else if (bw) {
            mode = 1;
        } else if (bh) {
            mode = 2;
        }

        // System.out.println("mode="+mode);
        // System.out.println();

        if (mode != -1) {
            if (this.setFigureBoundingBox(mode) == false) {
                return false;
            }
        }

        return true;
    }


    private SGUndoManager mUndoManager = new SGUndoManager(this);

    public SGProperties getMemento() {
        return this.getProperties();
    }

    public boolean setMemento(SGProperties p) {
        return this.setProperties(p);
    }

    /**
     *
     * @return
     */
    public boolean setMementoBackward() {
        if (this.mUndoManager.setMementoBackward() == false) {
            return false;
        }

        // this.repaintAll();

        return true;
    }

    /**
     *
     * @return
     */
    public boolean setMementoForward() {
        if (this.mUndoManager.setMementoForward() == false) {
            return false;
        }

        // this.repaintAll();

        return true;
    }

    boolean updateGraphRectOfAllFigures() {
        ArrayList list = this.getVisibleFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            figure.updateGraphRect();
        }
        return true;
    }

    /**
     * Update method called on undo and redo.
     */
    private boolean updateOnUndo() {
        // clear all focused objects
        this.clearAllFocusedObjectsInFigures();

        // update the location and scroll values
        this.updateClientRect();
        // this.setScrollBarValue();

        // update the graph rectangle of figures
        this.updateGraphRectOfAllFigures();

        // update menu items
        this.updateUndoItems();
        this.updateItemsByFigureNumbers();
        this.updateDataItem();
        this.updateGridItems();
        this.updateBackgroundImageItems();

        return true;
    }

    /**
     * Undo the operations.
     */
    public boolean undo() {
        if (this.mUndoManager.undo() == false) {
            return false;
        }

        // update
        this.updateOnUndo();
        this.updateStatusBarSavedFlag();

        this.repaintContentPane();

        return true;
    }
    
    /**
     * Redo the operations.
     */
    public boolean redo() {
        if (this.mUndoManager.redo() == false) {
            return false;
        }

        // update
        this.updateOnUndo();
        this.updateStatusBarSavedFlag();

        this.repaintContentPane();
        
        return true;
    }
    
    /**
     * Returns true if both of given figure and data exists in the state
     * after undo/redo operation.
     * 
     * @param bUndo
     *          true to undo and false to redo
     * @param figure
     *          a figure to check
     * @param data
     *          a data to check
     * @return true if both of given figure and data exists
     */
    public boolean existsOnUndo(final boolean bUndo, final SGFigure figure,
    		final SGData data) {
    	
    	// try undo / redo
    	if (bUndo) {
            if (this.isUndoable() == false) {
                throw new Error("This must not happen.");
            }
    		this.mUndoManager.undo();
    	} else {
    		if (this.isRedoable() == false) {
                throw new Error("This must not happen.");
    		}
    		this.mUndoManager.redo();
    	}
    	
    	try {
        	List<SGFigure> figureList = this.getVisibleFigureList();
        	if (!figureList.contains(figure)) {
        		return false;
        	}
        	boolean dataFound = false;
        	for (SGFigure f : figureList) {
        		List<SGData> dataList = f.getVisibleDataList();
        		if (dataList.contains(data)) {
        			dataFound = true;
        			break;
        		}
        	}
        	return dataFound;
    		
    	} finally {
    		
        	// recover the state
        	if (bUndo) {
        		this.mUndoManager.redo();
        	} else {
        		this.mUndoManager.undo();
        	}
    	}
    }

    /**
     *
     * @return
     */
    public boolean isChanged() {
        return this.mUndoManager.isChanged();
    }

    /**
     *
     * @return
     */
    public boolean isChangedRoot() {
        if (this.isChanged()) {
            return true;
        }
        List fList = this.getVisibleFigureList();
        for (int ii = 0; ii < fList.size(); ii++) {
            SGIUndoable obj = (SGIUndoable) fList.get(ii);
            if (obj.isChangedRoot()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    public void setChanged(final boolean b) {
        this.mUndoManager.setChanged(b);
    }

    // update items in menu bar and tool bar related to undo / redo operations
    private void updateUndoItems() {
        final boolean undoEnable = this.mUndoManager.isUndoable();
        final boolean redoEnable = this.mUndoManager.isRedoable();

        // menu bar
        SGMenuBar mBar = this.mMenuBar;
        mBar.setMenuItemEnabled(MENUBAR_EDIT, MENUBARCMD_UNDO, undoEnable);
        mBar.setMenuItemEnabled(MENUBAR_EDIT, MENUBARCMD_REDO, redoEnable);
        mBar.setMenuItemEnabled(MENUBAR_EDIT, MENUBARCMD_CLEAR_UNDO_BUFFER, (undoEnable || redoEnable));

        // tool bar
        SGToolBar tBar = this.mToolBar;
        tBar.setButtonEnabled(MENUBARCMD_UNDO, undoEnable);
        tBar.setButtonEnabled(MENUBARCMD_REDO, redoEnable);
    }

    /**
     *
     *
     */
    public void notifyToRoot() {
        // System.out.println("notifyToRoot");
        if (this.updateHistoryTree() == false) {
            throw new Error("Failed to update the history.");
        }
    }

    private boolean updateHistoryTree() {
        // update the history
        if (this.updateHistory() == false ) {
            return false;
        }

        if (this.isChangedRoot()) {
            // delete the forward histories
            if (this.deleteForwardHistory() == false) {
                return false;
            }
        }

        // clear changed flag of this object and all child objects
        this.clearChanged();

        // update undo items
        this.updateUndoItems();

        return true;
    }

    /**
     * Delete all forward histories.
     *
     * @return
     *         true if succeeded
     */
    public boolean deleteForwardHistory() {
//        System.out.println("******");

        // delete forward history of figures
        for (int ii = 0; ii < this.mFigureList.size(); ii++) {
            SGFigure f = (SGFigure) this.mFigureList.get(ii);
            if (f.deleteForwardHistory() == false) {
        	return false;
            }
        }

        // delete forward history of this window
        if (this.mUndoManager.deleteForwardHistory() == false) {
            return false;
        }

        // delete useless child objects
        if (this.deleteUselessFigures() == false) {
            return false;
        }

//        System.out.println("******");
//        System.out.println();
        return true;
    }

    // delete useless figures in histories
    private boolean deleteUselessFigures() {
        Set set = this.getAvailableChildSet();
        boolean gc = false;
        List<SGFigure> cList = new ArrayList<SGFigure>(this.mFigureList);
        for (int ii = cList.size() - 1; ii >= 0; ii--) {
            Object obj = cList.get(ii);
            if (set.contains(obj) == false) {
                SGFigure f = (SGFigure) obj;
                this.deleteFigure(f);
                gc = true;
            }
            obj = null;
        }

        if (gc) {
            cList.clear();
            set.clear();
        }
        return true;
    }

    // delete figure from this window
    private void deleteFigure(SGFigure f) {
        this.mFigureList.remove(f);
        f.dispose();
    }


    // The menu bar.
    private SGMenuBar mMenuBar = null;

    /**
     * Create the menu bar.
     */
    private boolean createMenuBar() {
        this.mMenuBar = new SGMenuBar();
        this.setJMenuBar(this.mMenuBar);
        this.mMenuBar.addActionListener(this);
        this.mMenuBar.addMenuListener(this);
        return true;
    }

    /**
     * Used for the menu items with tree structure.
     */
    static class NodeMenuItem extends JMenuItem {
        /**
         *
         */
        private static final long serialVersionUID = 8728676638490853269L;

        SGINode node;

        NodeMenuItem(String text) {
            super(text);
        }

        SGINode getNode() {
            return this.node;
        }

        void setNode(SGINode _node) {
            this.node = _node;
        }
    }

    /**
     * Exports this window as an image.
     *
     * @param silent
     *           silent mode flag
     * @return true if succeeded
     */
    public boolean exportAsImage(final boolean silent) {
        return this.toImage(EXPORT, silent);
    }

    /**
     * Information for exporting an image.
     */
    private InfoForExport mExportInfo = null;

    /**
     * The target component.
     */
    private ExportPanel mExportTarget = null;

    /**
     * Preprocess for exporting this window as an image.
     *
     * @param silent
     *           true for the silent mode
     * @return true if succeeded
     */
    public boolean startExport(final boolean silent) {
        final int width = (int) this.mClientPanel.getPaperWidth();
        final int height = (int) this.mClientPanel.getPaperHeight();

        InfoForExport info = new InfoForExport();
        ExportPanel target = new ExportPanel();
        target.setOpaque(true);
        target.setBackground(this.mClientPanel.getPaperColor());
        target.setPreferredSize(new Dimension(width, height));

        this.beforeExport(target, info, silent);

        this.mExportTarget = target;
        this.mExportInfo = info;

        return true;
    }

    /**
     * Post-process for exporting this window as an image.
     *
     * @param silent
     *           true for the silent mode
     * @return true if succeeded
     */
    public boolean endExport(final boolean silent) {
        this.afterExport(this.mExportTarget, this.mExportInfo, silent);
        this.mExportTarget = null;
        this.mExportInfo = null;
        return true;
    }

    /**
     * Returns the target component for exporting an image.
     *
     * @return the target component
     */
    public Component getExportTarget() {
        return this.mExportTarget;
    }

    /**
     * Prints this window as an image.
     *
     * @param silent
     *           silent mode flag
     * @return true if succeeded
     */
    public boolean printImage(final boolean silent) {
        return this.toImage(PRINT, silent);
    }

    private boolean toImage(final int mode, final boolean silent) {
        final int width = (int) this.mClientPanel.getPaperWidth();
        final int height = (int) this.mClientPanel.getPaperHeight();

        InfoForExport info = new InfoForExport();
        ExportPanel target = new ExportPanel();
        target.setOpaque(true);
        target.setBackground(this.mClientPanel.getPaperColor());
        target.setPreferredSize(new Dimension(width, height));

        SGIImageExportManager man = this.mImageExportManager;
        man.preprocessExport(this);

        this.beforeExport(target, info, silent);

        boolean ret;
        switch (mode) {
        case EXPORT: {
            // export as image
            ret = man.export(target, this, width, height, silent);
            break;
        }

        case PRINT: {
            // print as image
            ret = man.print(target, this, width, height, silent);
            break;
        }

        default: {
            ret = false;
        }
        }

        this.afterExport(target, info, silent);

        return ret;
    }

    /**
     * A class used for the image export.
     */
    private static class InfoForExport {
        float mag;

        float hValue;

        float vValue;

        /**
         * @uml.property name="locationArray"
         * @uml.associationEnd multiplicity="(0 -1)"
         */
        SGTuple2f[] locationArray;

        List<SGFigure> visibleFigureList;
    }

    private boolean beforeExport(final ExportPanel ePanel,
            final InfoForExport info, final boolean silent) {
        float mag = this.mMagnification;
        SGTuple2f value = this.mClientPanel.getScrollRatio();
        float hValue = value.x;
        float vValue = value.y;
        List<SGFigure> list = this.getVisibleFigureList();

        if (!silent) {
            // check whether the figures run off the edge of the paper
            boolean isInside = true;
            Rectangle pRect = this.getPaperRect().getBounds();
            for (int ii = 0; ii < list.size(); ii++) {
                SGFigure figure = (SGFigure) list.get(ii);
                Rectangle rect = figure.getBoundingBox().getBounds();
                if (pRect.contains(rect) == false) {
                    isInside = false;
                    break;
                }
            }
            if (!isInside) {
                SGUtility.showMessageDialog(this,
                        "Some figures run off the edge of paper.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        // preprocessing for image export
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            figure.beforeExport();
        }

        // record the location of figures
        SGTuple2f[] locationArray = new SGTuple2f[list.size()];
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            locationArray[ii] = new SGTuple2f(figure.mGraphRectX,
                    figure.mGraphRectY);
        }

        // zoom
        this.zoom(1.0f);

        // set the location of figures
        Rectangle2D cRect = this.getClientRect();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            figure.setGraphRectLocation(figure.getGraphRectX()
                    - (float) cRect.getX(), figure.getGraphRectY()
                    - (float) cRect.getY());
        }

        //
        // set to the export panel
        //

        // set the location and the size of preview dialog
        final float width = this.mClientPanel.getPaperWidth();
        final float height = this.mClientPanel.getPaperHeight();

        // set the layered pane
        ePanel.setOpaque(false);
        ePanel.setLocation(0, 0);
        ePanel.setSize((int) width, (int) height);

        // add figures to the export panel
        Rectangle2D vBounds = new Rectangle2D.Float(0.0f, 0.0f, width, height);
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            ePanel.add(figure);
            // Rectangle bounds = new Rectangle(
            // 0, 0, figure.getWidth(), figure.getHeight() );
            figure.setViewBounds(vBounds);
        }

        // set an image
        Image image = this.mClientPanel.getImage();
        if (image != null) {
            SGTuple2f location = this.mClientPanel.getImageLocation();
            SGTuple2f size = this.mClientPanel.getImageSize();
            final float f = this.mClientPanel.getImageScalingFactor();
            ePanel.setImage(image, location.x, location.y, size.x, size.y, f);
        }

        // set invisible
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            figure.setVisible(false);
        }

        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            figure.setMode(MODE_EXPORT_AS_IMAGE);
        }

        // set information
        info.mag = mag;
        info.hValue = hValue;
        info.vValue = vValue;
        info.locationArray = locationArray;
        info.visibleFigureList = list;

        return true;
    }

    private boolean afterExport(final ExportPanel ePanel,
            final InfoForExport info, final boolean silent) {
        float mag = info.mag;
        float hValue = info.hValue;
        float vValue = info.vValue;
        SGTuple2f[] locationArray = info.locationArray;
        List<SGFigure> fList = info.visibleFigureList;

        // zoom
        this.zoom(mag);

        // set scroll value
        this.mClientPanel.setScrollRatio(vValue, hValue);

        // set the location
        // SGTuple2f vpSize = this.getViewportSize();
        for (int ii = 0; ii < fList.size(); ii++) {
            SGFigure figure = (SGFigure) fList.get(ii);
            figure.mGraphRectX = locationArray[ii].x;
            figure.mGraphRectY = locationArray[ii].y;
            figure.updateGraphRect();
            figure.setViewBounds();
        }

        // postprocessing for image export
        for (int ii = 0; ii < fList.size(); ii++) {
            SGFigure figure = (SGFigure) fList.get(ii);
            figure.afterExport();
        }

        this.repaintContentPane();

        return true;
    }

    /**
     * A panel class used to export images.
     * 
     */
    public static class ExportPanel extends JPanel {
    	
        private static final long serialVersionUID = 6760038313006131448L;

        // The list of printable objects.
        private List<SGFigure> mFigureList = new ArrayList<SGFigure>();

        // The clipping flag.
        private boolean mClipFlag = true;

        // The background image.
        private SGImage mImage = null;

        /**
         * Default constructor.
         */
        public ExportPanel() {
            super();
        }

        /**
         * Add a figure.
         *
         * @param f
         *          figure
         */
        public void add(SGFigure f) {
            this.mFigureList.add(f);
        }

        /**
         * Paint this object.
         *
         * @param g
         *          graphic context
         */
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.mImage != null) {
                this.mImage.drawImage(g);
            }
            for (SGFigure f : this.mFigureList) {
                f.paint(g, this.mClipFlag);
            }
        }

        /**
         * Returns the clipping flag.
         * 
         * @return the clipping flag
         */
        public boolean getClipFlag() {
            return this.mClipFlag;
        }

        /**
         * Sets the clipping flag.
         * 
         * @param b
         *          the flag to set
         */
        public void setClipFlag(boolean b) {
            this.mClipFlag = b;
        }

        public SGImage getImage() {
            return this.mImage;
        }

        public SGTuple2f getImageSize() {
            return this.mImage.getImageSize();
        }

        public SGTuple2f getImageLocation() {
            return this.mImage.getImageLocation();
        }

        public boolean setImage(final Image img, final float x, final float y,
                final float w, final float h, final float factor) {
            this.mImage = new SGImage(img, this, x, y, w, h);
            this.mImage.setScalingFactor(factor);
            return true;
        }
    }

	public Element createElement(final Document document, SGExportParameter params) {
		Element element = document
				.createElement(SGIRootObjectConstants.TAG_NAME_WINDOW);
		SGPropertyMap map = this.getPropertyFileMap(params);
    	map.setToElement(element);
		return element;
	}
	
    /**
     *
     * @param document
     * @return
     */
    public Element createElementForFocusedFiguresInBoundingBox(
            final Document document, SGExportParameter params) {
        Element element = document
                .createElement(SGIRootObjectConstants.TAG_NAME_WINDOW);
        if (this.createElementForFocusedFiguresInBoundingBox(element, params) == false) {
            return null;
        }
        return element;
    }

    /**
     * Create a DOM Tree.
     *
     * @param document
     * @return
     */
    public boolean createDOMTree(Document document, final SGExportParameter params) {
        boolean flag;
        switch (this.mPropertyFileCreationModeOfFigures) {
        case ALL_FIGURES: {
            flag = this.createDOMTreeForAllFigures(document, params);
            break;
        }

        case FOCUSED_FIGURES_FOR_COPY: {
            flag = this.createDOMTreeForFocusedFiguresForDuplication(document, params);
            break;
        }

        case FOCUSED_FIGURES_IN_BOUNDING_BOX: {
            flag = this.createDOMTreeForFocusedFiguresInBoundingBox(document, params);
            break;
        }

        case FOCUSED_FIGURES_FOR_DUPLICATION: {
            flag = this.createDOMTreeForFocusedFiguresForDuplication(document, params);
            break;
        }

        default: {
            throw new Error();
        }
        }

        return flag;
    }

    /**
     * Create a DOM Tree.
     *
     * @param document
     * @param focused
     * @return
     */
    public boolean createDOMTree(Document document, int mode, SGExportParameter params) {
        this.mPropertyFileCreationModeOfFigures = mode;
        return this.createDOMTree(document, params);
    }

    /**
     *
     */
    private boolean createDOMTreeForAllFigures(Document document, SGExportParameter params) {

        // get the root element
        Element property = document.getDocumentElement();

        // write properties of the window
        Element windowElement = this.createElement(document, params);
        if (windowElement == null) {
            return false;
        }
        property.appendChild(windowElement);

        // figures
        ArrayList list = this.getVisibleFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = (SGFigure) list.get(ii);
            Element el = figure.createElement(document, params);
            if (el == null) {
                return false;
            }
            windowElement.appendChild(el);
        }

        return true;
    }

    /**
     * Creation mode of the property file of focused figures.
     */
    private int mPropertyFileCreationModeOfFigures;

    /**
     *
     * @return
     */
    boolean createPropertyFileFromFocusedFigures() {
        this.mPropertyFileCreationModeOfFigures = FOCUSED_FIGURES_IN_BOUNDING_BOX;
        this.notifyToListener(MENUBARCMD_SAVE_PROPERTY);
        return true;
    }

    /**
     *
     */
    private boolean createDOMTreeForFocusedFiguresInBoundingBox(
            Document document, SGExportParameter params) {

        // get the root element
        Element property = document.getDocumentElement();

        // write properties of the window
        Element windowElement = this
                .createElementForFocusedFiguresInBoundingBox(document, params);
        if (windowElement == null) {
            return false;
        }
        property.appendChild(windowElement);

        // figures
    	List<SGFigure> list = this.getFocusedFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = list.get(ii);
            Element el = figure.createElementForFocusedInBoundingBox(document, params);
            if (el == null) {
                return false;
            }
            windowElement.appendChild(el);
        }

        return true;
    }

    /**
     *
     */
    private boolean createDOMTreeForFocusedFiguresForDuplication(
            Document document, SGExportParameter params) {

        // get the root element
        Element property = document.getDocumentElement();

        // write properties of the window
        Element windowElement = this.createElement(document, params);
        if (windowElement == null) {
            return false;
        }
        property.appendChild(windowElement);

        // figures
    	List<SGFigure> list = this.getFocusedFigureList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGFigure figure = list.get(ii);
            Element el = figure.createElementForFocusedForDuplication(document, params);
            if (el == null) {
                return false;
            }
            windowElement.appendChild(el);
        }

        return true;
    }

    /**
     *
     * @param element
     * @return
     */
    public boolean createElementForFocusedFiguresInBoundingBox(
            final Element element, SGExportParameter params) {

		SGPropertyMap map = this.getPropertyFileMap(params);

    	// replaces the attributes for the size
        Rectangle2D rect = this.getBoundingBoxOfFigures(this
                .getFocusedFigureList());
        final float width = (float) rect.getWidth()
                * SGIConstants.CM_POINT_RATIO / this.mMagnification;
        final float height = (float) rect.getHeight()
                * SGIConstants.CM_POINT_RATIO / this.mMagnification;
    	SGPropertyUtility.addProperty(map, KEY_PAPER_WIDTH, 
    			width, PAPER_SIZE_UNIT);
    	SGPropertyUtility.addProperty(map, KEY_PAPER_HEIGHT, 
    			height, PAPER_SIZE_UNIT);
    	
    	map.setToElement(element);

        return true;
    }

    /**
     *
     * @param el
     * @return
     */
    public boolean readProperty(final Element el, final float min,
            final float max) {
        String str = null;
        Number num = null;
        Boolean b = null;
        Color cl = null;

        final float ratio = (max - min) / 9;
        final SGIProgressControl progress = (SGIProgressControl) this;

        // width
        progress.setProgressValue(min);
        str = el.getAttribute(SGIRootObjectConstants.KEY_PAPER_WIDTH);
        if (str.length() != 0) {
            StringBuffer uWidth = new StringBuffer();
            num = SGUtilityText.getNumber(str, uWidth);
            if (num == null) {
                return false;
            }
            final float width = num.floatValue();
            if (this.mClientPanel.setPaperWidth(width, uWidth.toString()) == false) {
                return false;
            }
        }

        // height
        progress.setProgressValue(min + ratio * 1);
        str = el.getAttribute(SGIRootObjectConstants.KEY_PAPER_HEIGHT);
        if (str.length() != 0) {
            StringBuffer uHeight = new StringBuffer();
            num = SGUtilityText.getNumber(str, uHeight);
            if (num == null) {
                return false;
            }
            final float height = num.floatValue();
            if (this.mClientPanel.setPaperHeight(height, uHeight.toString()) == false) {
                return false;
            }
        }

        // grid visible
        progress.setProgressValue(min + ratio * 2);
        str = el.getAttribute(SGIRootObjectConstants.KEY_GRID_VISIBLE);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return false;
            }
            final boolean gridVisible = b.booleanValue();
            if (this.mClientPanel.setGridLineVisible(gridVisible) == false) {
                return false;
            }
        }

        // grid interval
        progress.setProgressValue(min + ratio * 3);
        str = el.getAttribute(SGIRootObjectConstants.KEY_GRID_INTERVAL);
        if (str.length() != 0) {
            StringBuffer uInterval = new StringBuffer();
            num = SGUtilityText.getNumber(str, uInterval);
            if (num == null) {
                return false;
            }
            final float interval = num.floatValue();
            if (this.mClientPanel.setGridLineInterval(interval, uInterval
                    .toString()) == false) {
                return false;
            }
        }

        // grid line width
        progress.setProgressValue(min + ratio * 4);
        str = el.getAttribute(SGIRootObjectConstants.KEY_GRID_LINE_WIDTH);
        if (str.length() != 0) {
            StringBuffer uGridLineWidth = new StringBuffer();
            num = SGUtilityText.getNumber(str, uGridLineWidth);
            if (num == null) {
                return false;
            }
            final float gridLineWidth = num.floatValue();
            if (this.mClientPanel.setGridLineWidth(gridLineWidth,
                    uGridLineWidth.toString()) == false) {
                return false;
            }
        }

        // background color
        progress.setProgressValue(min + ratio * 5);
        str = el.getAttribute(SGIRootObjectConstants.KEY_BACKGROUND_COLOR);
        if (str.length() != 0) {
            cl = SGUtilityText.parseColor(str);
            if (cl == null) {
                return false;
            }
            final Color bgColor = cl;
            if (this.mClientPanel.setPaperColor(bgColor) == false) {
                return false;
            }
        }

        // grid line color
        progress.setProgressValue(min + ratio * 6);
        str = el.getAttribute(SGIRootObjectConstants.KEY_GRID_COLOR);
        if (str.length() != 0) {
            cl = SGUtilityText.parseColor(str);
            if (cl == null) {
                return false;
            }
            final Color gridColor = cl;
            if (this.mClientPanel.setGridLineColor(gridColor) == false) {
                return false;
            }
        }

        // image location X
        progress.setProgressValue(min + ratio * 7);
        str = el.getAttribute(SGIRootObjectConstants.KEY_IMAGE_LOCATION_X);
        if (str.length() != 0) {
            StringBuffer uX = new StringBuffer();
            num = SGUtilityText.getNumber(str, uX);
            if (num == null) {
                return false;
            }
            final float x = num.floatValue();
            if (this.mClientPanel.setImageLocationX(x, uX.toString()) == false) {
                return false;
            }
        }

        // image location Y
        progress.setProgressValue(min + ratio * 8);
        str = el.getAttribute(SGIRootObjectConstants.KEY_IMAGE_LOCATION_Y);
        if (str.length() != 0) {
            StringBuffer uY = new StringBuffer();
            num = SGUtilityText.getNumber(str, uY);
            if (num == null) {
                return false;
            }
            final float y = num.floatValue();
            if (this.mClientPanel.setImageLocationY(y, uY.toString()) == false) {
                return false;
            }
        }

        // image scaling factor
        progress.setProgressValue(max);
        str = el.getAttribute(SGIRootObjectConstants.KEY_IMAGE_SCALE);
        if (str.length() != 0) {
            num = SGUtilityText.getDouble(str);
            if (num == null) {
                return false;
            }
            final float f = num.floatValue();
            if (this.mClientPanel.setImageScalingFactor(f) == false) {
                return false;
            }
        }

        return true;
    }

    boolean showPropertyDialogForSelectedFigures() {

    	List<SGFigure> figList = this.getFocusedFigureList();
        List<SGPropertyDialog> dList = new ArrayList<SGPropertyDialog>();
        for (int ii = 0; ii < figList.size(); ii++) {
            SGFigure fig = figList.get(ii);
            SGPropertyDialog dg = fig.getPropertyDialog();
            if (dg != null) {
                dList.add(dg);
            }
        }

        // clear focused objects in figures
        List listAll = this.mFigureList;
        for (int ii = 0; ii < listAll.size(); ii++) {
            SGFigure fig = (SGFigure) listAll.get(ii);
            fig.clearFocusedObjects();
        }

        // add listeners to the property dialog
        SGPropertyDialog dg = (SGPropertyDialog) dList.get(0);
        List<SGIPropertyDialogObserver> lList = new ArrayList<SGIPropertyDialogObserver>();
        lList.addAll(figList);
        this.showPropertyDialog(dg, lList);

        return true;
    }

    boolean showPopupMenuForSelectedFigures(JComponent component, final int x, final int y) {
    	List<SGFigure> figList = this.getFocusedFigureList();
    	
        // get properties fo figures
        final int num = figList.size();
        boolean[] legendVisibleArray = new boolean[num];
        boolean[] colorBarVisibleArray = new boolean[num];
        boolean[] axisScaleVisibleArray = new boolean[num];
        boolean[] gridVisibleArray = new boolean[num];
        boolean[] dataAnchoredArray = new boolean[num];
        boolean[] horizontalAxis1VisibleArray = new boolean[num];
        boolean[] horizontalAxis2VisibleArray = new boolean[num];
        boolean[] verticalAxis1VisibleArray = new boolean[num];
        boolean[] verticalAxis2VisibleArray = new boolean[num];
        boolean[] legendAvailableArray = new boolean[num];
        boolean[] colorBarAvailableArray = new boolean[num];
        boolean[] alignmentBarsAvailableArray = new boolean[num];
        boolean[] animationAvailableArray = new boolean[num];
        for (int ii = 0; ii < num; ii++) {
        	SGFigure fig = figList.get(ii);
			legendVisibleArray[ii] = fig.isLegendVisible();
			colorBarVisibleArray[ii] = fig.isColorBarVisible();
			axisScaleVisibleArray[ii] = fig.isAxisScaleVisible();
			gridVisibleArray[ii] = fig.isGridVisible();
			dataAnchoredArray[ii] = fig.isDataAnchored();
        	SGIFigureElementAxis aElement = fig.getAxisElement();
			horizontalAxis1VisibleArray[ii] = aElement.isAxisVisible(SGIFigureElementAxis.AXIS_HORIZONTAL_1);
			horizontalAxis2VisibleArray[ii] = aElement.isAxisVisible(SGIFigureElementAxis.AXIS_HORIZONTAL_2);
			verticalAxis1VisibleArray[ii] = aElement.isAxisVisible(SGIFigureElementAxis.AXIS_VERTICAL_1);
			verticalAxis2VisibleArray[ii] = aElement.isAxisVisible(SGIFigureElementAxis.AXIS_VERTICAL_2);
			legendAvailableArray[ii] = fig.isLegendAvailable();
			colorBarAvailableArray[ii] = fig.isColorBarAvailable();
			alignmentBarsAvailableArray[ii] = fig.isAlignmentBarsAvailable();
			animationAvailableArray[ii] = fig.hasAnimationAvailableData();
        }
        
        Boolean legendVisible = SGUtility.checkEquality(legendVisibleArray);
        Boolean colorBarVisible = SGUtility.checkEquality(colorBarVisibleArray);
        Boolean axisScaleVisible = SGUtility.checkEquality(axisScaleVisibleArray);
        Boolean gridVisible = SGUtility.checkEquality(gridVisibleArray);
        Boolean dataAnchored = SGUtility.checkEquality(dataAnchoredArray);
        Boolean horizontalAxis1Visible = SGUtility.checkEquality(horizontalAxis1VisibleArray);
        Boolean horizontalAxis2Visible = SGUtility.checkEquality(horizontalAxis2VisibleArray);
        Boolean verticalAxis1Visible = SGUtility.checkEquality(verticalAxis1VisibleArray);
        Boolean verticalAxis2Visible = SGUtility.checkEquality(verticalAxis2VisibleArray);
        Boolean legendAvailable = SGUtility.checkEquality(legendAvailableArray);
        Boolean colorBarAvailable = SGUtility.checkEquality(colorBarAvailableArray);
        Boolean alignmentBarsAvailable = SGUtility.checkEquality(alignmentBarsAvailableArray);
        Boolean animationAvailable = SGUtility.checkEquality(animationAvailableArray);

        SGFigurePopupMenu p = new SGFigurePopupMenu(figList);
        p.addPopupMenuListener(new PopupMenuListener() {
        	// Adds a PopupMenuListener to refresh the attributes when popup menu becomes invisible.
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				Point pos = getMousePosition();
				if (pos != null) {
		            mMousePressLocation = pos;
		            mTempMouseLocation.setLocation(pos);
				}
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
        });
        
        // set enabled or disabled the menu items
        Component[] components = p.getComponents();
        for (int ii = 0; ii < components.length; ii++) {
            if (components[ii] instanceof JMenuItem) {
                final JMenuItem item = (JMenuItem) components[ii];
                final String command = item.getActionCommand();
                if (SGFigure.MENUCMD_LEGEND_VISIBLE.equals(command)) {
                    final boolean b = (legendAvailable != null) ? legendAvailable.booleanValue() : false;
                    item.setEnabled(b);
                } else if (SGFigure.MENUCMD_COLOR_BAR_VISIBLE.equals(command)) {
                    final boolean b = (colorBarAvailable != null) ? colorBarAvailable.booleanValue() : false;
                    item.setEnabled(b);
                } else if (MENUCMD_ALIGN_BARS.equals(command)) {
                    final boolean b = (alignmentBarsAvailable != null) ? alignmentBarsAvailable.booleanValue() : false;
                    item.setEnabled(b);
                } else if (MENUCMD_FIT_AXES_TO_DATA.equals(command)) {
                	JMenu menu = (JMenu) item;
                	Component[] cArray = menu.getMenuComponents();
                	for (int jj = 0; jj < cArray.length; jj++) {
                		if (cArray[jj] instanceof JMenuItem) {
                			JMenuItem cItem = (JMenuItem) cArray[jj];
                			String text = cItem.getText();
                    		if (MENUCMD_FIT_COLOR_BAR_TO_DATA.equals(text)) {
                    			final boolean b = (colorBarAvailable != null) ? colorBarAvailable : false;
                    			cItem.setEnabled(b);
                    		} else if (MENUCMD_FIT_ALL_AXES_TO_DATA_FOR_ALL_ANIMATION_FRAMES.equals(text)) {
                    			final boolean b = (animationAvailable != null) ? animationAvailable : false;
                    			cItem.setEnabled(b);
                    		}
                		}
                	}
                }
            }
        }

        // set selected/deselected the menu items
        for (int ii = 0; ii < components.length; ii++) {
            if (components[ii] instanceof SGCheckBoxMenuItem) {
                SGCheckBoxMenuItem item = (SGCheckBoxMenuItem) components[ii];
                String com = item.getActionCommand();
                if (com.equals(SGFigure.MENUCMD_RUBBER_BANDING)) {
                    item.setSelected(SGFigure.mRubberBandFlag);
                } else if (com.equals(SGFigure.MENUCMD_SHOW_BOUNDING_BOX)) {
                    item.setSelected(SGFigure.mBoundingBoxVisibleFlag);
                } else if (com.equals(MENUBARCMD_SNAP_TO_GRID)) {
                    item.setSelected(SGFigure.isSnappingToGrid());
                } else if (com.equals(SGFigure.MENUCMD_LEGEND_VISIBLE)) {
                    item.setSelected(legendVisible);
                } else if (com.equals(SGFigure.MENUCMD_COLOR_BAR_VISIBLE)) {
                    item.setSelected(colorBarVisible);
                } else if (com.equals(SGFigure.MENUCMD_SCALE_VISIBLE)) {
                    item.setSelected(axisScaleVisible);
                } else if (com.equals(SGFigure.MENUCMD_GRID_VISIBLE)) {
                    item.setSelected(gridVisible);
                } else if (com.equals(SGFigure.MENUCMD_DATA_ANCHORED)) {
                	item.setSelected(dataAnchored);
                }
            } else if (components[ii] instanceof JMenu) {
            	JMenu menu = (JMenu) components[ii];
                if (MENUCMD_AXES_VISIBLE.equals(menu.getActionCommand())) {
                	SGCheckBoxMenuItem item;
                	item = (SGCheckBoxMenuItem) SGUtility.findMenuItem(menu, MENUCMD_VISIBLE_BOTTOM_AXIS);
                	item.setSelected(horizontalAxis1Visible);
                	item = (SGCheckBoxMenuItem) SGUtility.findMenuItem(menu, MENUCMD_VISIBLE_LEFT_AXIS);
                	item.setSelected(verticalAxis1Visible);
                	item = (SGCheckBoxMenuItem) SGUtility.findMenuItem(menu, MENUCMD_VISIBLE_TOP_AXIS);
                	item.setSelected(horizontalAxis2Visible);
                	item = (SGCheckBoxMenuItem) SGUtility.findMenuItem(menu, MENUCMD_VISIBLE_RIGHT_AXIS);
                	item.setSelected(verticalAxis2Visible);
                }
            }
        }

        // shows the menu
        p.show(component, x, y);

        return true;
    }
    
    /**
     * Shows the property dialog for selected objects.
     * 
     * @param figure
     *               a figure that an event generated
     * @param element
     *               an figure element that an event generated
     * @return
     *               true if succeeded
     */
    boolean showPropertyDialogForSelectedObjects(final SGFigure figure,
            final SGIFigureElement element) {

        ArrayList figList = this.getVisibleFigureList();
        
        // clears focused objects
        this.clearFocusedObjects(figure, element, figList);

        // gets observers
        List<SGIPropertyDialogObserver> obsList = this.getSelectedPropertyDialogObserverList(
        		element, figList);
        if (obsList.size() == 0) {
            return false;
        }
        
        // gets property dialogs
        List<SGPropertyDialog> dList = this.getPropertyDialogList(obsList);
        if (dList == null || dList.size() == 0) {
            return false;
        }

        // show the property dialog
        SGPropertyDialog dg = (SGPropertyDialog) dList.get(0);
        this.showPropertyDialog(dg, obsList);

        return true;
    }

    boolean showPropertyDialogForAllVisibleObjects(final SGFigure figure,
            final SGIFigureElement element) {

        ArrayList figList = this.getVisibleFigureList();
        
        // clears focused objects
        this.clearFocusedObjects(figure, element, figList);

        // gets observers
        List<SGIPropertyDialogObserver> obsList = this.getVisiblePropertyDialogObserverList(
        		element, figList);
        if (obsList.size() == 0) {
            return false;
        }
        
        // gets property dialogs
        List<SGPropertyDialog> dList = this.getPropertyDialogList(obsList);
        if (dList == null || dList.size() == 0) {
            return false;
        }

        // show the property dialog
        SGPropertyDialog dg = (SGPropertyDialog) dList.get(0);
        this.showPropertyDialog(dg, obsList);

        return true;
    }
    
    boolean showPropertyDialogForAllObjects(final SGFigure figure,
            final SGIFigureElement element) {

        ArrayList figList = this.getVisibleFigureList();
        
        // clears focused objects
        this.clearFocusedObjects(figure, element, figList);

        // gets observers
        List<SGIPropertyDialogObserver> obsList = this.getAllPropertyDialogObserverList(
        		element, figList);
        if (obsList.size() == 0) {
            return false;
        }
        
        // gets property dialogs
        List<SGPropertyDialog> dList = this.getPropertyDialogList(obsList);
        if (dList == null || dList.size() == 0) {
            return false;
        }

        // show the property dialog
        SGPropertyDialog dg = (SGPropertyDialog) dList.get(0);
        this.showPropertyDialog(dg, obsList);

        return true;
    }
    
    // Clears focused objects.
    private void clearFocusedObjects(final SGFigure figure,
            final SGIFigureElement element, ArrayList<SGFigure> figList) {
    	
        // sets the class object of property observer
        Class<?> cl = element.getPropertyDialogObserverClass();
        for (int ii = 0; ii < figList.size(); ii++) {
        	SGFigure fig = (SGFigure) figList.get(ii);
        	fig.setPropertyDialogObserverClass(cl);
        }

        // clears focused objects
        for (int ii = 0; ii < figList.size(); ii++) {
            SGFigure fig = (SGFigure) figList.get(ii);
            fig.clearFocusedObjects(element);
            if (fig.equals(figure) == false) {
                fig.setSelected(false);
            }
        }
		this.repaintContentPane();
    }
    
    enum PROPERTY_SETTING_CONDITION {
    	SELECTED, VISIBLE, ALL,
    };
    
    private List<SGIPropertyDialogObserver> getPropertyDialogObserverList(
    		SGIFigureElement element, ArrayList<SGFigure> figList, 
    		PROPERTY_SETTING_CONDITION condition) {
        List<SGIPropertyDialogObserver> obsList = new ArrayList();
        Class cl = element.getClass();
        Class<?> obsClass = element.getPropertyDialogObserverClass();
        for (int ii = 0; ii < figList.size(); ii++) {
            SGFigure fig = (SGFigure) figList.get(ii);
            SGIFigureElement el = fig.getIFigureElement(cl);
            if (el == null) {
                continue;
            }
            List<SGIPropertyDialogObserver> list = null;
            if (PROPERTY_SETTING_CONDITION.SELECTED.equals(condition)) {
                list = el.getSelectedPropertyDialogObserverList(obsClass);
            } else if (PROPERTY_SETTING_CONDITION.VISIBLE.equals(condition)) {
                list = el.getVisiblePropertyDialogObserverList(obsClass);
            } else if (PROPERTY_SETTING_CONDITION.ALL.equals(condition)) {
                list = el.getAllPropertyDialogObserverList(obsClass);
            }
            if (list == null || list.size() == 0) {
                continue;
            }
            obsList.addAll(list);
        }
        return obsList;
    }

    // Returns the list of selected property dialog observers.
    private List<SGIPropertyDialogObserver> getSelectedPropertyDialogObserverList(
    		SGIFigureElement element, ArrayList<SGFigure> figList) {
    	return this.getPropertyDialogObserverList(element, figList, PROPERTY_SETTING_CONDITION.SELECTED);
    }

    // Returns the list of visible property dialog observers.
    private List<SGIPropertyDialogObserver> getVisiblePropertyDialogObserverList(
    		SGIFigureElement element, ArrayList<SGFigure> figList) {
    	return this.getPropertyDialogObserverList(element, figList, PROPERTY_SETTING_CONDITION.VISIBLE);
    }

    // Returns the list of all property dialog observers.
    private List<SGIPropertyDialogObserver> getAllPropertyDialogObserverList(
    		SGIFigureElement element, ArrayList<SGFigure> figList) {
    	return this.getPropertyDialogObserverList(element, figList, PROPERTY_SETTING_CONDITION.ALL);
    }

    // Returns the list of property dialogs.
    private List<SGPropertyDialog> getPropertyDialogList(
    		List<SGIPropertyDialogObserver> obsList) {
        List<SGPropertyDialog> dList = new ArrayList<SGPropertyDialog>();
        for (int ii = 0; ii < obsList.size(); ii++) {
            SGIPropertyDialogObserver obs = obsList.get(ii);
            SGPropertyDialog dg = obs.getPropertyDialog();
            dList.add(dg);
        }
        for (int ii = 0; ii < dList.size() - 1; ii++) {
            Object obj1 = dList.get(ii);
            for (int jj = ii + 1; jj < dList.size(); jj++) {
                Object obj2 = dList.get(jj);
                if (obj1.getClass().equals(obj2.getClass()) == false) {
                    SGUtility.showMessageDialog(this, "Object type is different.",
                            "Failed to show the property dialog.",
                            JOptionPane.WARNING_MESSAGE);
                    return null;
                }
            }
        }
        return dList;
    }

    /**
     *
     * @param dg
     * @param l -
     *            a property dialog observer
     */
    private void showPropertyDialog(SGPropertyDialog dg,
            SGIPropertyDialogObserver l) {
        ArrayList list = new ArrayList();
        list.add(l);
        this.showPropertyDialog(dg, list);
    }

    // true if modal dialog is shown
    private boolean mModalDialogShownFlag = false;

    /**
     * Returns whether a modal dialog is shown.
     *
     * @return true if a modal dialog is shown
     */
    public boolean isModalDialogShown() {
    	return this.mModalDialogShownFlag;
    }

    /**
     * Sets whether a modal dialog is shown.
     *
     * @param b
     *          a flag to set
     */
    public void setModalDialogShown(final boolean b) {
    	this.mModalDialogShownFlag = b;
    }

    /**
     * Closes the text field.
     *
     * @return true if succeeded
     */
    public boolean closeTextField() {
    	List<SGFigure> list = this.getVisibleFigureList();
    	for (int ii = 0; ii < list.size(); ii++) {
    		SGFigure f = list.get(ii);
    		if (f.closeTextField() == false) {
    			return false;
    		}
    	}
    	return true;
    }

    /**
     * Shows a property dialog.
     *
     * @param dg
     *           the property dialog to be shown
     * @param lList
     *           a list of property dialog observer
     */
    private void showPropertyDialog(SGPropertyDialog dg,
    		List<SGIPropertyDialogObserver> lList) {

    	// add dialog observers
        for (int ii = 0; ii < lList.size(); ii++) {
            SGIPropertyDialogObserver l = lList.get(ii);
            dg.addPropertyDialogObserver(l);
            l.prepare();
        }

        // set properties to dialog
        dg.setDialogProperty();
        dg.setLocation(this.getLocation());

        // show property dialog
        dg.setVisible(true);

        // remove all dialog observers
        dg.removeAllPropertyDialogObserver();

        // when the OK button is pressed, update the history tree
        final int closeOption = dg.getCloseOption();
        if (closeOption == SGDialog.OK_OPTION) {
            this.notifyToRoot();
        }
    }

    private int mSavedListIndex = -1;

    /**
     * Set guarantee that no information is lost by discarding this window.
     *
     * @param b
     */
    public void setSaved(final boolean b) {
        if (b)
            this.mSavedListIndex = this.mUndoManager
                    .getChangedObjectListIndex();
        this.updateStatusBarSavedFlag();
    }

    /**
     * Whether it is guaranteed that no information is lost by discarding this
     * window.
     *
     * @return
     */
    public boolean isSaved() {
        return (this.mSavedListIndex == this.mUndoManager
                .getChangedObjectListIndex());
    }

    // interface implements of 'SGIWindowDialogObserver'
    public float getPaperWidth(final String unit) {
        return this.mClientPanel.getPaperWidth(unit);
    }

    public float getPaperHeight(final String unit) {
        return this.mClientPanel.getPaperHeight(unit);
    }

    public float getGridLineInterval(final String unit) {
        return this.mClientPanel.getGridLineInterval(unit);
    }

    public float getGridLineWidth(final String unit) {
        return this.mClientPanel.getGridLineWidth(unit);
    }

    public boolean isGridLineVisible() {
        return this.mClientPanel.isGridLineVisible();
    }

    public Color getPaperColor() {
        return this.mClientPanel.getPaperColor();
    }

    public Color getGridLineColor() {
        return this.mClientPanel.getGridLineColor();
    }

    public float getImageLocationX() {
        return this.mClientPanel.getImageLocationX();
    }

    public float getImageLocationX(String unit) {
        return this.mClientPanel.getImageLocationX(unit);
    }

    public float getImageLocationY() {
        return this.mClientPanel.getImageLocationY();
    }

    public float getImageLocationY(String unit) {
        return this.mClientPanel.getImageLocationY(unit);
    }

    public float getImageWidth() {
        return this.mClientPanel.getImageWidth();
    }

    public float getImageWidth(String unit) {
        return this.mClientPanel.getImageWidth(unit);
    }

    public float getImageHeight() {
        return this.mClientPanel.getImageHeight();
    }

    public float getImageHeight(String unit) {
        return this.mClientPanel.getImageHeight(unit);
    }
    
    public String getImageFilePath() {
    	return this.mClientPanel.getImageFilePath();
    }

    public float getImageScalingFactor() {
        return this.mClientPanel.getImageScalingFactor();
    }

    public boolean setPaperWidth(final float value, final String unit) {
        return this.mClientPanel.setPaperWidth(value, unit);
    }

    public boolean setPaperHeight(final float value, final String unit) {
        return this.mClientPanel.setPaperHeight(value, unit);
    }

    public boolean setGridLineInterval(final float value, final String unit) {
        return this.mClientPanel.setGridLineInterval(value, unit);
    }

    public boolean setGridLineWidth(final float value, final String unit) {
        return this.mClientPanel.setGridLineWidth(value, unit);
    }

    public boolean setGridLineVisible(final boolean b) {
        return this.mClientPanel.setGridLineVisible(b);
    }

    public boolean setPaperColor(final Color cl) {
        return this.mClientPanel.setPaperColor(cl);
    }

    public boolean setGridLineColor(final Color cl) {
        return this.mClientPanel.setGridLineColor(cl);
    }

    public boolean setImageLocationX(final float value, final String unit) {
        return this.mClientPanel.setImageLocationX(value, unit);
    }

    public boolean setImageLocationY(final float value, final String unit) {
        return this.mClientPanel.setImageLocationY(value, unit);
    }

//    public boolean setImageWidth(final float value, final String unit) {
//        return this.mClientPanel.setImageWidth(value, unit);
//    }
//
//    public boolean setImageHeight(final float value, final String unit) {
//        return this.mClientPanel.setImageHeight(value, unit);
//    }

    public boolean setImageScalingFactor(final float f) {
        return this.mClientPanel.setImageScalingFactor(f);
    }


    /**
     * Property of SGDrawingWindow.
     */
    public static class WindowProperties extends SGProperties {

        private ArrayList mVisibleFigureList = new ArrayList();

        private float mPaperWidth;

        private float mPaperHeight;

        private Color mBackgroundColor;

        private boolean mGridVisible;

        private Color mGridColor;

        private float mGridInverval;

        private float mGridLineWidth;

        private float mImageLocationX;

        private float mImageLocationY;

        private float mImageScalingFactor;

        private Image mImage;

        /**
         *
         */
        public WindowProperties() {
            super();
        }

        /**
         *
         */
        public void dispose() {
        	super.dispose();
            this.mVisibleFigureList.clear();
            this.mVisibleFigureList = null;

            this.mBackgroundColor = null;
            this.mGridColor = null;
            this.mImage = null;
        }

        /**
         *
         */
        public boolean equals(final Object obj) {

            if ((obj instanceof WindowProperties) == false) {
                return false;
            }

            WindowProperties p = (WindowProperties) obj;

            if (p.mVisibleFigureList.equals(this.mVisibleFigureList) == false)
                return false;
            if (p.mPaperWidth != this.mPaperWidth)
                return false;
            if (p.mPaperHeight != this.mPaperHeight)
                return false;
            if (p.mBackgroundColor.equals(this.mBackgroundColor) == false)
                return false;
            if (p.mGridVisible != this.mGridVisible)
                return false;
            if (p.mGridColor.equals(this.mGridColor) == false)
                return false;
            if (p.mGridInverval != this.mGridInverval)
                return false;
            if (p.mGridLineWidth != this.mGridLineWidth)
                return false;
            if (p.mImageLocationX != this.mImageLocationX)
                return false;
            if (p.mImageLocationY != this.mImageLocationY)
                return false;
            if (p.mImageScalingFactor != this.mImageScalingFactor)
                return false;

            if (p.mImage != null) {
                if (p.mImage.equals(this.mImage) == false)
                    return false;
            } else {
                if (this.mImage != null)
                    return false;
            }
            return true;
        }

        public List getVisibleFigureList() {
            List list = new ArrayList(this.mVisibleFigureList);
            return list;
        }

        public Float getPaperWidth() {
            return Float.valueOf(this.mPaperWidth);
        }

        public Float getPaperHeight() {
            return Float.valueOf(this.mPaperHeight);
        }

        public Color getBackgroundColor() {
            return this.mBackgroundColor;
        }

        public Color getGridColor() {
            return this.mGridColor;
        }

        public Boolean getGridVisible() {
            return Boolean.valueOf(this.mGridVisible);
        }

        public Float getGridInterval() {
            return Float.valueOf(this.mGridInverval);
        }

        public Float getGridLineWidth() {
            return Float.valueOf(this.mGridLineWidth);
        }

        public Float getImageLocationX() {
            return Float.valueOf(this.mImageLocationX);
        }

        public Float getImageLocationY() {
            return Float.valueOf(this.mImageLocationY);
        }

        public Float getImageScalingFactor() {
            return Float.valueOf(this.mImageScalingFactor);
        }

        public Image getImage() {
            return this.mImage;
        }

        public void setVisibleFigureList(final List list) {
            if (list == null) {
                throw new IllegalArgumentException("list==null");
            }
            this.mVisibleFigureList = new ArrayList(list);
        }

        public void setPaperWidth(final float w) {
            if (w < 0.0f) {
                throw new IllegalArgumentException("w<0.0f");
            }
            this.mPaperWidth = w;
        }

        public void setPaperHeight(final float h) {
            if (h < 0.0f) {
                throw new IllegalArgumentException("h<0.0f");
            }
            this.mPaperHeight = h;
        }

        public void setBackGroundColor(final Color cl) {
            if (cl == null) {
                throw new IllegalArgumentException("cl==null");
            }
            this.mBackgroundColor = cl;
        }

        public void setGridColor(final Color cl) {
            if (cl == null) {
                throw new IllegalArgumentException("cl==null");
            }
            this.mGridColor = cl;
        }

        public void setGridVisible(final boolean b) {
            this.mGridVisible = b;
        }

        public void setGridInterval(final float num) {
            if (num < 0.0f) {
                throw new IllegalArgumentException("num<0.0f");
            }
            this.mGridInverval = num;
        }

        public void setGridLineWidth(final float num) {
            if (num < 0.0f) {
                throw new IllegalArgumentException("num<0.0f");
            }
            this.mGridLineWidth = num;
        }

        public boolean setImageLocationX(final float num) {
            this.mImageLocationX = num;
            return true;
        }

        public boolean setImageLocationY(final float num) {
            this.mImageLocationY = num;
            return true;
        }

        public boolean setImageScalingFactor(final float num) {
            if (num < 0.0f) {
                throw new IllegalArgumentException("num<0.0f");
            }
            this.mImageScalingFactor = num;
            return true;
        }

        public boolean setImage(Image img) {
            this.mImage = img;
            return true;
        }
    }

    /**
     * Splits the focused data objects.
     */
    public void doSplitMultipleData() {

        // notify the command
        this.notifyToListener(MENUBARCMD_SPLIT);

        // notify the change to the root
        this.notifyToRoot();

        // update the menu items
        this.updateFocusedObjectItem();

        // repaint
        this.repaintContentPane();
    }

    /**
     * Transforms the focused data objects to other type data.
     */
    public void doTransformData() {

        // notify the command
        this.notifyToListener(MENUCMD_TRANSFORM_DATA);

        // notify the change to the root
        this.notifyToRoot();

        // update the menu items
        this.updateFocusedObjectItem();

        // repaint
        this.repaintContentPane();
    }

    /**
     * Merge the focused data objects to one object.
     */
    public void doMergeMultipleData() {

        // notify the command
        this.notifyToListener(MENUBARCMD_MERGE);

        // notify the change to the root
        this.notifyToRoot();

        // update the menu items
        this.updateFocusedObjectItem();

        // repaint
        this.repaintContentPane();
    }

    /**
     * Assignes line colors.
     *
     */
    public void doAssignLineColors() {
        // notify the command
        this.notifyToListener(MENUBARCMD_ASSIGN_LINE_COLORS);

        // notify the change to the root
        this.notifyToRoot();

        // update the menu items
        this.updateFocusedObjectItem();

        // repaint
        this.repaintContentPane();
    }
    
    public void doHideSelectedAxes() {
    	ArrayList<SGFigure> fList = this.getVisibleFigureList();
    	for (SGFigure f : fList) {
    		f.getAxisElement().hideSelectedAxes();
    	}
    	this.repaint();
    	this.notifyToRoot();
    }

    /**
     *
     * @param data
     */
    void doOutputDataToFile(final SGData data) {
        SGPluginsQueryMessage message = new SGPluginsQueryMessage(SGPluginsQueryMessage.MENUCMD_EXEC_OUTPUT_TO_FILE);
        message.set(data);

        this.notifyToListener(SGPluginsQueryMessage.MENUCMD_EXEC_OUTPUT_TO_FILE, message);
    }

    void doOutputDataToFile(String command) {
    	this.notifyToListener(command);
    }

    /**
     *
     */
    void doUndoAndDeleteForward() {
        this.undo();
        this.deleteForwardHistory();
        this.updateUndoItems();
        this.repaintContentPane();
    }

    /**
     * Returns the background image.
     *
     * @return the background image
     */
    public BackgroundImage getBackgroundImage() {
    	return this.mBackgroundImage;
    }

    /**
     * The background image.
     */
    public static class BackgroundImage {

    	// The extension of image file.
    	private String mExtension = null;

    	// The byte array of the image.
    	private byte[] mByteArray = null;

    	/**
    	 * Builds an object with given byte array.
    	 *
    	 * @param ext
    	 *          the extension of image file
    	 * @param b
    	 *          the byte array of the image
    	 */
    	public BackgroundImage(final String ext, final byte[] b) {
    		super();
    		if (ext == null || b == null) {
    			throw new IllegalArgumentException("ext == null || b == null");
    		}
    		this.mExtension = ext;
    		this.mByteArray = Arrays.copyOf(b, b.length);
    	}

    	/**
    	 * Returns the extension of image file.
    	 *
    	 * @return the extension of image file
    	 */
    	public String getExtension() {
    		return this.mExtension;
    	}

    	/**
    	 * Returns the byte array of the image.
    	 *
    	 * @return the byte array of the image
    	 */
    	public byte[] getByteArray() {
    		return this.mByteArray;
    	}
    }

    void setAnchor(final boolean b) {
    	for (SGFigure fig : this.mFigureList) {
            fig.setAnchoredToFocusedObjects(b);
    	}
    }
    
    /**
     * Creates and returns the map of properties for the property file.
     * 
     * @return the map of properties
     */
    public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	this.addProperties(map, KEY_PAPER_WIDTH, KEY_PAPER_HEIGHT, 
    			KEY_BACKGROUND_COLOR, KEY_GRID_VISIBLE, 
    			KEY_GRID_INTERVAL, KEY_GRID_LINE_WIDTH, 
    			KEY_GRID_COLOR, KEY_IMAGE_LOCATION_X, KEY_IMAGE_LOCATION_Y, 
    			KEY_IMAGE_SCALE);
    	return map;
    }

    /**
     * Creates and returns the map of properties for the commands.
     * 
     * @return the map of properties
     */
    public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	this.addProperties(map, COM_PAPER_WIDTH, COM_PAPER_HEIGHT, 
    			COM_WINDOW_BACKGROUND_COLOR, COM_WINDOW_GRID_VISIBLE, 
    			COM_WINDOW_GRID_INTERVAL, COM_WINDOW_GRID_LINE_WIDTH, 
    			COM_WINDOW_GRID_COLOR, COM_IMAGE_LOCATION_X, COM_IMAGE_LOCATION_Y, 
    			COM_IMAGE_SCALING_FACTOR);
    	String path = this.getImageFilePath();
    	if (path != null) {
        	SGPropertyUtility.addQuotedStringProperty(map, COM_IMAGE_FILE_PATH, path);
    	}
    	return map;
    }

	private void addProperties(SGPropertyMap map, String widthKey,
			String heightKey, String bgColorKey, String gridVisibleKey,
			String gridIntervalKey, String gridLineWidthKey,
			String gridColorKey, String imageXKey, String imageYKey,
			String imageScaleKey) {
    	
    	// paper
    	SGPropertyUtility.addProperty(map, widthKey, 
    			this.getExportLengthValue(this.getPaperWidth(PAPER_SIZE_UNIT)), 
    			PAPER_SIZE_UNIT);
    	SGPropertyUtility.addProperty(map, heightKey, 
    			this.getExportLengthValue(this.getPaperHeight(PAPER_SIZE_UNIT)), 
    			PAPER_SIZE_UNIT);
    	SGPropertyUtility.addProperty(map, bgColorKey, this.getPaperColor());
    	
    	// grid
    	SGPropertyUtility.addProperty(map, gridVisibleKey, 
    			this.isGridLineVisible());
    	SGPropertyUtility.addProperty(map, gridIntervalKey, 
    			this.getExportLengthValue(this.getGridLineInterval(GRID_INTERVAL_UNIT)), 
    			GRID_INTERVAL_UNIT);
    	SGPropertyUtility.addProperty(map, gridLineWidthKey, 
    			SGUtility.getExportLineWidth(this.getGridLineWidth(LINE_WIDTH_UNIT)), 
    			LINE_WIDTH_UNIT);
    	SGPropertyUtility.addProperty(map, gridColorKey, 
    			this.getGridLineColor());
    	
    	// image
    	SGPropertyUtility.addProperty(map, imageXKey, 
    			this.getExportLengthValue(this.getImageLocationX(IMAGE_LOCATION_UNIT)), 
    			IMAGE_LOCATION_UNIT);
    	SGPropertyUtility.addProperty(map, imageYKey, 
    			this.getExportLengthValue(this.getImageLocationY(IMAGE_LOCATION_UNIT)), 
    			IMAGE_LOCATION_UNIT);
    	SGPropertyUtility.addProperty(map, imageScaleKey, 
    			SGUtility.getExportValue(this.getImageScalingFactor(), 
    					SGIRootObjectConstants.IMAGE_SCALING_ORDER));
    }

    /**
     * Returns a text string of the commands.
     * 
     * @return a text string of the commands
     */
	public String getCommandString(SGExportParameter params) {
		OPERATION type = params.getType();
		
		StringBuffer sb = new StringBuffer();
		
		// creates the command for this window
		String wndCommands = SGCommandUtility.createCommandString(COM_WINDOW, 
				null, this.getCommandPropertyMap(params));
		sb.append(wndCommands);
		
		// creates the command of figures
		List<SGFigure> figureList = this.getVisibleFigureList();
		for (SGFigure f : figureList) {
			if (OPERATION.SAVE_INTO_FILE_ATTRIBUTE.equals(type)) {
				List<SGData> dataList = f.getVisibleDataList();
				SGDataExportParameter exportParams = (SGDataExportParameter) params;
				boolean found = false;
				for (SGData data : dataList) {
					if (exportParams.canExport(data)) {
						found = true;
						break;
					}
				}
				if (!found) {
					continue;
				}
			}
			String fCommands = f.getCommandString(params);
			sb.append(fCommands);
		}
		return sb.toString();
	}

	/**
	 * Sets enabled or disabled the menu items related to the command script.
	 * 
	 * @param commandEnabled
	 *           true to set enabled
	 */
    public void setCommandMenuEnabled(final boolean commandEnabled) {
        this.mMenuBar.setMenuItemEnabled(MENUBAR_FILE, MENUBARCMD_LOAD_SCRIPT, commandEnabled);
    }

    private float getExportLengthValue(final float len) {
    	return SGUtility.getExportValue(
    			len, SGIRootObjectConstants.LENGTH_MINIMAL_ORDER);
    }
    
    void doAnimation() {
    	this.notifyToListener(MENUCMD_ANIMATION);
    }

    void doFitAxisRangeToFocusedData(final String command) {
    	this.notifyToListener(command);
    }
}

package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationTextConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGImageConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGPreferencesConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGRootObjectConstants.*;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import jp.riken.brain.ni.samuraigraph.application.SGMainFunctions.SAVED_OBJECT_TYPE;
import jp.riken.brain.ni.samuraigraph.base.SGCSVTokenizer.Token;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

/** SGMainFunctions :: WindowManager class */
class SGWindowManager
    implements ActionListener,
        DropTargetListener,
        PropertyChangeListener,
        WindowListener,
        ComponentListener {

  /** Image file creator. */
  private SGImageExportManager mImageFileCreator = null;

  /** The list of windows. */
  private TreeMap<Integer, SGDrawingWindow> mWndMap = null;

  /** The current window. */
  private SGDrawingWindow mCurrentWindow = null;

  /** The main functions. */
  private SGMainFunctions mMain = null;

  SGWindowManager(
      final SGMainFunctions main,
      final SGSplashWindow sw,
      final float minProg,
      final float maxProg) {
    super();
    this.mMain = main;
    final float step = (maxProg - minProg) / 3;
    sw.setProgressValue(minProg + step);
    this.mWndMap = new TreeMap<Integer, SGDrawingWindow>();
    sw.setProgressValue(minProg + step * 2);
    this.mImageFileCreator = new SGImageExportManager();
  }

  /** */
  public static final String WINDOW_TITLE_PREFIX = "Samurai Graph - Window : ";

  /**
   * Create a window with given ID number.
   *
   * @param id ID number
   */
  private SGDrawingWindow createWindow(final int id) {

    // create an instance
    SGDrawingWindow wnd = new SGDrawingWindow();

    // set ID number
    wnd.setID(id);

    // put into the map
    this.mWndMap.put(Integer.valueOf(id), wnd);

    // set the close operation
    wnd.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    // set window title
    String title = WINDOW_TITLE_PREFIX + wnd.getID();
    wnd.setTitle(title);

    // add this object as an event listener of the window
    wnd.addActionListener(this);
    wnd.addWindowListener(this);
    wnd.addPropertyChangeListener(this);
    wnd.addComponentListener(this);

    // create and set the drop target
    DropTarget target = new DropTarget(wnd, DnDConstants.ACTION_COPY_OR_MOVE, this, true);
    wnd.setDropTarget(target);

    wnd.setImageFileCreator(this.mImageFileCreator);

    // set the tool bar pattern
    Preferences pref = Preferences.userNodeForPackage(this.getClass());
    final String pattern = pref.get(PREF_KEY_TOOL_BAR_PATTERN, null);
    String[] array;
    if (pattern != null) {
      // tokenize a string
      List<Token> tokenList = new ArrayList<>();
      // pattern is not read from data file.
      if (SGUtilityText.tokenize(pattern, tokenList, false)) {
        List<String> keyList = new ArrayList<>();
        for (int ii = 0; ii < tokenList.size(); ii++) {
          Token token = tokenList.get(ii);
          if (token != null) {
            keyList.add(token.getString());
          }
        }

        array = keyList.toArray(new String[0]);
      } else {
        array = TOOLBAR_MENUCMD_ARRAY;
      }
    } else {
      array =
          new String[] {
            MENUBARCMD_VISIBLE_FILE, MENUBARCMD_VISIBLE_INSERT, MENUBARCMD_VISIBLE_ZOOM
          };

      // set to preferences
      this.mMain.updateToolBarPatternInPreferences(array);
    }

    // set to the window
    wnd.setToolBarPattern(array);

    // initializes the size
    final float width;
    final String wStr = pref.get(PREF_KEY_VIEWPORT_WIDTH, null);
    if (wStr != null) {
      Float num = SGUtilityText.getFloat(wStr);
      if (num != null) {
        width = num.floatValue();
      } else {
        width = DEFAULT_VIEWPORT_WIDTH;
      }
    } else {
      width = DEFAULT_VIEWPORT_WIDTH;
    }
    final float height;
    final String hStr = pref.get(PREF_KEY_VIEWPORT_HEIGHT, null);
    if (hStr != null) {
      Float num = SGUtilityText.getFloat(hStr);
      if (num != null) {
        height = num.floatValue();
      } else {
        height = DEFAULT_VIEWPORT_HEIGHT;
      }
    } else {
      height = DEFAULT_VIEWPORT_HEIGHT;
    }
    wnd.setViewportSize(width, height);

    // initialize
    if (wnd.init() == false) {
      return null;
    }

    // initializes the location
    Rectangle virtualBounds = this.mMain.getVirtualBounds();
    final int x = virtualBounds.x + (virtualBounds.width - wnd.getWidth()) / 2;
    final int y = virtualBounds.y + (virtualBounds.height - wnd.getHeight()) / 2;
    wnd.setLocation(x, y);

    // set the menus enabled
    this.setPasteMenuEnabled(); // paste

    // set enabled / disabled the menu items
    wnd.setCommandMenuEnabled(this.mMain.isCommandMode());

    return wnd;
  }

  /**
   * Returns a window of given window ID.
   *
   * @param wndID the window ID
   */
  public SGDrawingWindow getWindow(final int wndID) {
    return this.mWndMap.get(Integer.valueOf(wndID));
  }

  /** Returns the current window. This method must be used in command handler. */
  public SGDrawingWindow getCurrentWindow() {
    return this.mCurrentWindow;
  }

  /** The map of the current figure. */
  private Map<Integer, SGFigure> mCurrentFigureMap = new HashMap<Integer, SGFigure>();

  /** Returns the current figure. */
  public SGFigure getCurrentFigure() {
    if (this.mCurrentWindow == null) {
      return null;
    }
    return this.mCurrentFigureMap.get(this.mCurrentWindow.getID());
  }

  /**
   * Sets the current figure for given window.
   *
   * @param wndId thw window ID
   * @param figure a figure
   */
  public boolean setCurrentFigure(final int wndId, final SGFigure figure) {
    this.mCurrentFigureMap.put(wndId, figure);
    return true;
  }

  /** Returns the ID number for a new window. */
  private int assignWindowID() {

    List<Integer> keyList = new ArrayList<Integer>(this.mWndMap.keySet());
    final int id = SGUtility.assignIdNumber(keyList);
    return id;
  }

  /** Returns the number of windows. */
  public int getWindowNumber() {
    return this.mWndMap.size();
  }

  /** Returns the list of ID of all windows. */
  public List<Integer> getWindowIdList() {
    return new ArrayList<Integer>(this.mWndMap.keySet());
  }

  /** Create a new window. */
  public SGDrawingWindow createNewWindow() {
    final int id = this.assignWindowID();
    SGDrawingWindow wnd = this.createWindow(id);
    if (wnd == null) {
      return null;
    }
    wnd.initPropertiesHistory();
    return wnd;
  }

  /**
   * Create a new window of given ID.
   *
   * @param id the window ID
   */
  public SGDrawingWindow createNewWindow(final int id) {
    if (this.getWindow(id) != null) {
      return null;
    }
    SGDrawingWindow wnd = this.createWindow(id);
    if (wnd == null) {
      return null;
    }
    wnd.initPropertiesHistory();
    return wnd;
  }

  /**
   * Closes the window without confirmation.
   *
   * @param id the window ID of a window to close
   */
  public boolean closeWindowWithoutConfirmation(final int id) {
    SGDrawingWindow wnd = this.getWindow(id);
    if (wnd == null) {
      return false;
    }
    return this.removeWindow(wnd);
  }

  /**
   * Closes the window with confirmation.
   *
   * @param id the window ID of a window to close
   */
  public boolean closeWindow(final int id) {
    SGDrawingWindow wnd = this.getWindow(id);
    if (wnd == null) {
      return false;
    }
    this.closeWindow(wnd);
    return true;
  }

  /**
   * Closes the window with confirmation.
   *
   * @param wnd a window to close
   */
  public int closeWindow(final SGDrawingWindow wnd) {
    if (wnd.needsConfirmationBeforeDiscard()) {
      final int ret = this.mMain.confirmBeforeClosing(wnd);
      if (ret == JOptionPane.YES_OPTION) {
        this.removeWindow(wnd);
      } else if (ret == JOptionPane.NO_OPTION || ret == JOptionPane.CLOSED_OPTION) {
        // canceled and there is nothing to do
        return CANCEL_OPTION;
      } else if (ret == JOptionPane.CANCEL_OPTION) {
        // save the properties
        final int retSave = this.mMain.mPropertyFileManager.savePropertiesByDialog(wnd);
        if (retSave == OK_OPTION) {
          this.removeWindow(wnd);
        }
        return retSave;
      }
    } else {
      this.removeWindow(wnd);
    }
    return OK_OPTION;
  }

  /** Closes all windows. */
  void closeAllWindow() {
    final List<SGDrawingWindow> wndList = new ArrayList<SGDrawingWindow>(this.mWndMap.values());
    for (int ii = wndList.size() - 1; ii >= 0; ii--) {
      SGDrawingWindow wnd = wndList.get(ii);
      final int ret = this.closeWindow(wnd);
      if (ret == CANCEL_OPTION) {
        break;
      }
    }
  }

  /** Closes all windows without confirmation. */
  void closeAllWindowWithoutConfirmation() {
    List<SGDrawingWindow> wndList = this.getWindowList();
    for (int ii = wndList.size() - 1; ii >= 0; ii--) {
      SGDrawingWindow wnd = wndList.get(ii);
      this.removeWindow(wnd);
    }
  }

  /**
   * Sets the current window with given window ID. This method must be used in command handler.
   *
   * @param wndId the ID of the window to set to the current window
   */
  public boolean setCurrentWindow(final int wndId) {
    SGDrawingWindow wnd = this.getWindow(wndId);
    if (wnd == null) {
      return false;
    }
    this.mCurrentWindow = wnd;
    return true;
  }

  /**
   * Sets the current window.
   *
   * @param wnd a window to set to the current window
   */
  public void setCurrentWindow(final SGDrawingWindow wnd) {
    this.mCurrentWindow = wnd;
  }

  /** */
  void setPasteMenuEnabled() {
    final boolean b = this.mMain.mClipBoardManager.getPasteMenuStatus();
    List<SGDrawingWindow> wList = new ArrayList<SGDrawingWindow>(this.mWndMap.values());
    for (int ii = 0; ii < wList.size(); ii++) {
      SGDrawingWindow wnd = wList.get(ii);
      wnd.setPasteMenuEnabled(b);
    }
  }

  // Remove the window from window map.
  private boolean removeWindow(final SGDrawingWindow wnd) {
    wnd.removeWindowListener(this);
    wnd.removeActionListener(this);
    EventQueue.invokeLater(
        new Runnable() {
          public void run() {
            final int id = wnd.getID();
            mWndMap.remove(Integer.valueOf(id));
            wnd.setVisible(false);
            wnd.dispose();

            if (wnd.equals(mCurrentWindow)) {
              mCurrentWindow = null;
            }
            mCurrentFigureMap.remove(id);

            if (mWndMap.size() == 0) {
              mMain.exitApplication(0);
            }
          }
        });
    return true;
  }

  /**
   * The listener interface for receiving action events.
   *
   * @param e the action event
   */
  public void actionPerformed(final ActionEvent e) {

    Object source = e.getSource();
    String command = e.getActionCommand();

    if (source instanceof SGDrawingWindow) {
      SGDrawingWindow wnd = (SGDrawingWindow) source;

      if (command.equals(MENUBARCMD_CREATE_NEW_WINDOW)) {
        SGDrawingWindow w = this.createNewWindow();
        if (w == null) {
          return;
        }
        w.setVisible(true);
      } else if (command.equals(MENUBARCMD_CLOSE_WINDOW)) {
        this.closeWindow(wnd);
      } else if (command.equals(MENUBARCMD_EXIT)) {
        this.closeAllWindow();
      } else if (command.equals(MENUBARCMD_DRAW_GRAPH)) {
        this.mMain.getDataAdditionHandler().onToolBarDataAdditionExecuted(wnd);
      } else if (command.equals(MENUBARCMD_RELOAD)) {
        this.mMain.reloadData(wnd, true);
      } else if (command.equals(MENUBARCMD_SAVE_PROPERTY)) {
        final int ret = this.mMain.mPropertyFileManager.savePropertiesByDialog(wnd);
        wnd.setSaved(ret == OK_OPTION);
      } else if (command.equals(MENUBARCMD_LOAD_PROPERTY)) {
        if (wnd.needsConfirmationBeforeDiscard()) {
          final int ret = this.mMain.beforeDiscard(wnd);
          if (ret == CANCEL_OPTION) {
            return;
          }
        }

        // close all data viewer and animation dialogs
        this.mMain.closeAllModelessDialogs(wnd);

        this.mMain.mPropertyFileManager.loadPropertyFromDialog(wnd);
      } else if (command.equals(MENUBARCMD_SAVE_DATASET)) {
        final int ret = this.mMain.mDataSetManager.saveDataSet(wnd);
        wnd.setSaved(ret == OK_OPTION);
      } else if (command.equals(MENUBARCMD_LOAD_DATASET)) {
        if (wnd.needsConfirmationBeforeDiscard()) {
          final int ret = this.mMain.beforeDiscard(wnd);
          if (ret == CANCEL_OPTION) {
            return;
          }
        }

        // close all data viewer and animation dialogs
        this.mMain.closeAllModelessDialogs(wnd);

        final boolean result = this.mMain.mDataSetManager.loadDataSetFromDialog(wnd);
        if (!result) {
          SGUtility.showErrorMessageDialog(wnd, MSG_DATA_SET_FILE_INVALID, TITLE_ERROR);
        }
        wnd.setSaved(result);
      } else if (command.equals(MENUBARCMD_LOAD_SCRIPT)) {
        JFileChooser chooser =
            this.createFileChooser(
                FILE_TYPE.SCRIPT, SCRIPT_FILE_EXTENSION, SCRIPT_FILE_DESCRIPTION, "");

        // shows open dialog
        final int ret = chooser.showOpenDialog(wnd);
        File file = null;
        if (ret == JFileChooser.APPROVE_OPTION) {
          file = chooser.getSelectedFile();
        }
        if (file != null && file.exists()) {
          // sets the current window
          this.setCurrentWindow(wnd);

          // loads the command script file
          this.mMain.getConsoleRunner().loadCommandScriptFile(file.getPath());

          // updates the current file
          this.mMain.updateCurrentFile(file, FILE_TYPE.SCRIPT);
        }
      } else if (command.equals(MENUBARCMD_SAVE_AS_SCRIPT)) {
        final int ret = this.mMain.mCommandScriptManager.create(wnd);
        wnd.setSaved(ret == OK_OPTION);

      } else if (command.equals(MENUBARCMD_LOAD_BACKGROUND_IMAGE)) {

        // set up the file chooser
        JFileChooser chooser =
            this.createFileChooser(
                FILE_TYPE.IMAGE, DRAWABLE_IMAGE_EXTENSIONS, IMAGE_FILE_DESCRIPTION, "");

        // show open dialog
        final int ret = chooser.showOpenDialog(wnd);

        File file = null;
        if (ret == JFileChooser.APPROVE_OPTION) {
          file = chooser.getSelectedFile();
        }

        // set the image to the window
        if (file != null) {
          byte[] imageByteArray = SGApplicationUtility.toByteArray(file);
          String ext = SGApplicationUtility.getImageExtension(file);
          if (ext == null) {
            return;
          }
          if (!wnd.setImage(imageByteArray, ext, true)) {
            SGUtility.showErrorMessageDialog(wnd, MSG_FILE_OPEN_FAILURE, TITLE_FILE_OPEN_FAILURE);
            return;
          }
          wnd.setImageFilePath(file.getAbsolutePath());
          this.mMain.updateCurrentFile(file, FILE_TYPE.IMAGE);
        }
      } else if (command.equals(MENUBARCMD_CUT)) {
        this.mMain.mClipBoardManager.cutAndCopy(wnd, false);
      } else if (command.equals(MENUBARCMD_COPY)) {
        this.mMain.mClipBoardManager.cutAndCopy(wnd, true);
      } else if (command.equals(MENUBARCMD_PASTE)) {
        this.mMain.mClipBoardManager.pasteToWindow(wnd);
      } else if (command.equals(MENUBARCMD_DUPLICATE)) {
        // duplicate the focused figures
        this.mMain.duplicateFocusedFigures(wnd);

        // duplicate the focused data
        this.mMain.duplicateFocusedData(wnd);
      } else if (command.equals(MENUBARCMD_EXPORT_AS_IMAGE)) {
        this.mImageFileCreator.setBaseDirectory(this.mMain.getCurrentFileDirectory());
        wnd.exportAsImage(false);
        this.mMain.setCurrentFileDirectory(this.mImageFileCreator.getBaseDirectory());
      } else if (command.equals(MENUBARCMD_PRINT)) {
        wnd.printImage(false);
      } else if (command.equals(MENUBARCMD_UPGRADE)) {
        this.mMain.mUpgradeManager.showUpgradeDialog(wnd);
      } else if (command.equals(MENUBARCMD_CHANGE_LOG)) {
        this.mMain.showChangeLogDialog(wnd);
      } else if (command.equals(MENUBARCMD_PROXY)) {
        this.mMain.mProxyManager.showProxySettingDialog(wnd);
      } else if (command.equals(MENUBARCMD_MEMORY)) {
        this.mMain.showMemoryInfo(wnd);
      } else if (command.equals(MENUBARCMD_PLUGIN_DETAIL)) {
        this.mMain.showPluginDetailDialog(wnd);
      } else if (command.equals(MENUBARCMD_ABOUT)) {
        this.mMain.showAboutDialog(wnd);
      } else if (command.equals(MENUBARCMD_SPLIT)) {
        this.mMain.splitSXYData(wnd);
      } else if (command.equals(MENUCMD_TRANSFORM_DATA)) {
        this.mMain.transformData(wnd);
      } else if (command.equals(MENUBARCMD_MERGE)) {
        this.mMain.mergeSXYData(wnd);
      } else if (command.equals(MENUBARCMD_ASSIGN_LINE_COLORS)) {
        if (!this.mMain.assignLineColors(wnd)) {
          SGUtility.showErrorMessageDialog(
              wnd, "Cannot open the dialog to setup line style.", TITLE_ERROR);
          return;
        }
      } else if (command.equals(MENUCMD_EXPORT_TO_FILE)
          || command.equals(MENUCMD_EXPORT_TO_TEXT_FILE)
          || command.equals(MENUCMD_EXPORT_TO_NETCDF_FILE)
          || command.equals(MENUCMD_EXPORT_TO_HDF5_FILE)
          || command.equals(MENUCMD_EXPORT_TO_MATLAB_FILE)) {
        this.mMain.outputDataToFile(wnd, command);
      } else if (command.equals(MENUCMD_ADD_PROPERTIES_TO_NETCDF)) {
        this.mMain.saveIntoGlobalAttributes(wnd, SAVED_OBJECT_TYPE.PROPERTIES);
      } else if (command.equals(MENUCMD_ADD_COMMANDS_TO_NETCDF)) {
        this.mMain.saveIntoGlobalAttributes(wnd, SAVED_OBJECT_TYPE.COMMAND_SCRIPT);
      } else if (command.equals(MENUCMD_ANIMATION)) {
        this.mMain.showAnimationDialog(wnd);
      } else if (command.equals(MENUCMD_SHOW_DATA_VIEWER)) {
        this.mMain.showDataViewerDialog(wnd);
      } else if (MENUCMD_FIT_ALL_AXES_TO_DATA.equals(command)
          || MENUCMD_FIT_HORIZONTAL_AXIS_TO_DATA.equals(command)
          || MENUCMD_FIT_VERTICAL_AXIS_TO_DATA.equals(command)
          || MENUCMD_FIT_COLOR_BAR_TO_DATA.equals(command)
          || MENUCMD_FIT_ALL_AXES_TO_DATA_FOR_ALL_ANIMATION_FRAMES.equals(command)) {
        this.mMain.fitAxisRangeToFocusedData(wnd, command);
      } else if (NOTIFY_DATA_WILL_BE_HIDDEN.equals(command)) {
        this.mMain.closeDataViewerDialogsOfFocusedData(wnd);
        this.mMain.closeDataAnimationDialogsOfFocusedData(wnd);
      } else if (NOTIFY_DATA_STRUCTURE_CHANGE_ON_COMMIT.equals(command)
          || NOTIFY_DATA_STRUCTURE_CHANGE_ON_PREVIEW.equals(command)) {
        // column types of data are changed
        this.mMain.refreshAllDataViewerDialogs();
        this.mMain.closeDataAnimationDialogsOfFocusedData(wnd);
      } else if (SGDrawingWindow.NOTIFY_FIGURE_WILL_BE_HIDDEN.equals(command)) {
        this.mMain.closeDataViewerDialogsInFocusedFigures(wnd);
        this.mMain.closeDataAnimationDialogsInFocusedFigures(wnd);
      } else if (command.equals(MENUBARCMD_UNDO)) {
        this.mMain.closeDataViewerDialogInAllFigures(wnd, true);
        this.mMain.closeDataAnimationDialogInAllFigures(wnd, true);
        wnd.undo();
        this.mMain.refreshAllDataViewerDialogs();
      } else if (command.equals(MENUBARCMD_REDO)) {
        this.mMain.closeDataViewerDialogInAllFigures(wnd, false);
        this.mMain.closeDataAnimationDialogInAllFigures(wnd, false);
        wnd.redo();
        this.mMain.refreshAllDataViewerDialogs();
      } else if (command.equals(NOTIFY_DATA_CLICKED)) {
        this.mMain.updateDataTableCellSelection(wnd);
      }
    }
  }

  private JFileChooser createFileChooser(
      final FILE_TYPE fType, final String ext, final String desc, final String defaultFileName) {
    return this.createFileChooser(fType, new String[] {ext}, desc, defaultFileName);
  }

  private JFileChooser createFileChooser(
      final FILE_TYPE fType,
      final String[] extArray,
      final String desc,
      final String defaultFileName) {
    String currentDirectory = this.mMain.getCurrentFileDirectory();
    String currentFileName = this.mMain.getCurrentFileName(fType);
    return SGApplicationUtility.createFileChooser(
        currentDirectory, currentFileName, extArray, desc, defaultFileName);
  }

  // DropTargetListener
  /**
   * Called while a drag operation is ongoing, when the mouse pointer enters the operable part of
   * the drop site for the <code>DropTarget</code> registered with this listener.
   *
   * @param event the <code>DropTargetDragEvent</code>
   */
  public void dragEnter(final DropTargetDragEvent event) {
    event.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
  }

  /**
   * Called when a drag operation is ongoing, while the mouse pointer is still over the operable
   * part of the drop site for the <code>DropTarget</code> registered with this listener.
   *
   * @param event the <code>DropTargetDragEvent</code>
   */
  public void dragOver(final DropTargetDragEvent event) {}

  /**
   * Called if the user has modified the current drop gesture.
   *
   * @param event the <code>DropTargetDragEvent</code>
   */
  public void dropActionChanged(final DropTargetDragEvent event) {}

  /**
   * Called while a drag operation is ongoing, when the mouse pointer has exited the operable part
   * of the drop site for the <code>DropTarget</code> registered with this listener.
   *
   * @param event the <code>DropTargetEvent</code>
   */
  public void dragExit(final DropTargetEvent event) {}

  /**
   * Called when the drag operation has terminated with a drop on the operable part of the drop site
   * for the <code>DropTarget</code> registered with this listener.
   *
   * @param event the <code>DropTargetDropEvent</code>
   */
  public void drop(final DropTargetDropEvent event) {
    // get dropped file list
    List<File> fileList = SGApplicationUtility.getDroppedFileList(event);
    if (fileList != null && fileList.size() != 0) {
      DropTarget tg = (DropTarget) event.getSource();
      Component com = tg.getComponent();
      final SGDrawingWindow wnd = (SGDrawingWindow) com;
      final Point point = event.getLocation();
      SwingUtilities.invokeLater(
          new Runnable() {
            public void run() {
              SGWindowManager.this.mMain.onFilesDropped(fileList, wnd, point);
              wnd.getContentPane().repaint();
            }
          });
    }
  }

  // PropertyChangeListener
  /**
   * This method gets called when a bound property is changed.
   *
   * @param e A PropertyChangeEvent object describing the event source and the property that has
   *     changed.
   */
  public void propertyChange(PropertyChangeEvent e) {
    Object source = e.getSource();
    String name = e.getPropertyName();

    if (source instanceof SGDrawingWindow) {
      if (PROPERTY_NAME_TOOL_BAR.equals(name)) {
        SGDrawingWindow wnd = (SGDrawingWindow) source;
        this.mMain.updateToolBarPatternInPreferences(wnd.getToolBarPattern());
      }
    }
  }

  /** Invoked the first time a window is made visible. */
  public void windowOpened(final WindowEvent e) {}

  /**
   * Invoked when the user attempts to close the window from the window's system menu. If the
   * program does not explicitly hide or dispose the window while processing this event, the window
   * close operation will be canceled.
   */
  public void windowClosing(final WindowEvent e) {
    final SGDrawingWindow wnd = (SGDrawingWindow) e.getSource();
    this.closeWindow(wnd);
  }

  /** Invoked when a window has been closed as the result of calling dispose on the window. */
  public void windowClosed(final WindowEvent e) {}

  /**
   * Invoked when a window is changed from a normal to a minimized state. For many platforms, a
   * minimized window is displayed as the icon specified in the window's iconImage property.
   *
   * @see java.awt.Frame#setIconImage
   */
  public void windowIconified(final WindowEvent e) {}

  /** Invoked when a window is changed from a minimized to a normal state. */
  public void windowDeiconified(final WindowEvent e) {}

  /**
   * Invoked when the Window is set to be the active Window. Only a Frame or a Dialog can be the
   * active Window. The native windowing system may denote the active Window or its children with
   * special decorations, such as a highlighted title bar. The active Window is always either the
   * focused Window, or the first Frame or Dialog that is an owner of the focused Window.
   */
  public void windowActivated(final WindowEvent e) {
    Object source = e.getSource();
    if (source instanceof SGDrawingWindow) {
      SGDrawingWindow wnd = (SGDrawingWindow) source;
      this.mActiveWindow = wnd;
    }
    e.getWindow().repaint();
  }

  // Currently active window.
  private SGDrawingWindow mActiveWindow = null;

  /** Returns the active window. */
  public SGDrawingWindow getActiveWindow() {
    return this.mActiveWindow;
  }

  /**
   * Invoked when a Window is no longer the active Window. Only a Frame or a Dialog can be the
   * active Window. The native windowing system may denote the active Window or its children with
   * special decorations, such as a highlighted title bar. The active Window is always either the
   * focused Window, or the first Frame or Dialog that is an owner of the focused Window.
   */
  public void windowDeactivated(final WindowEvent e) {
    e.getWindow().repaint();
  }

  /**
   * Returns a list of windows.
   *
   * @return a list of windows
   */
  public List<SGDrawingWindow> getWindowList() {
    List<SGDrawingWindow> wndList = new ArrayList<SGDrawingWindow>(this.mWndMap.values());
    return wndList;
  }

  @Override
  public void componentResized(ComponentEvent e) {
    Object source = e.getSource();
    if (source instanceof SGDrawingWindow) {
      SGDrawingWindow wnd = (SGDrawingWindow) source;
      this.mMain.updateWindowInfoInPreferences(wnd);
    }
  }

  @Override
  public void componentMoved(ComponentEvent e) {
    // do nothing
  }

  @Override
  public void componentShown(ComponentEvent e) {
    // do nothing
  }

  @Override
  public void componentHidden(ComponentEvent e) {
    // do nothing
  }
}

package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** An interface for objects on a figure. */
public interface SGIFigureElement
    extends SGIUndoable,
        SGIPaintable,
        SGINode,
        SGIDisposable,
        SGIDrawingElementConstants,
        SGIFigureElementConstants {

  /** */
  public JComponent getComponent();

  /**
   * @param com
   */
  public void setComponent(JComponent com);

  /**
   * Adds a data object with given name.
   *
   * @param data a data object
   * @param name name of the data object
   */
  public boolean addData(final SGData data, final String name);

  /**
   * Adds data objects with given name.
   *
   * @param data an array of data objects
   * @param name an array of names of data objects
   * @param infoMap information map of data objects
   */
  public boolean addData(
      final SGData[] data, final String[] name, final Map<String, Object> infoMap);

  /**
   * Adds a data object with given name and given ID.
   *
   * @param data a data object
   * @param name name of the data object
   * @param id the ID of the data object
   * @param infoMap information map of data object
   */
  public boolean addData(
      final SGData data, final String name, final int id, final Map<String, Object> infoMap);

  /**
   * Adds data objects with given name and given ID.
   *
   * @param data an array of data objects
   * @param name an array of names of data objects
   * @param id the ID of data objects
   * @param infoMap information map of data objects
   */
  public boolean addData(
      final SGData[] data, final String[] name, final int[] id, final Map<String, Object> infoMap);

  /**
   * Add a data object with given name and properties.
   *
   * @param data data to add
   * @param name name of data
   * @param p properties to be set the data
   */
  public boolean addData(final SGData data, final String name, final SGProperties p);

  /**
   * Remove a data object.
   *
   * @param data data to be removed
   */
  public boolean removeData(SGData data);

  /** Returns a list of useless data objects in this figure element. */
  public List<SGData> getUselessDataList();

  /** Returns a list of data. */
  public List<SGData> getDataList();

  /** Returns properties of the given data. */
  public SGProperties getDataProperties(SGData data);

  /**
   * Sets the magnification.
   *
   * @param mag the magnification to set
   */
  public boolean setMagnification(float mag);

  /** Returns the magnification. */
  public float getMagnification();

  /**
   * Synchronize this component to the other component. <br>
   *
   * @param element - the SGFigureElement object whose property has changed.
   * @param msg - message
   */
  public boolean synchronize(SGIFigureElement element, String msg);

  /** */
  public boolean synchronizeArgument(SGIFigureElement element, String msg);

  /** */
  public boolean setViewBounds(Rectangle2D rect);

  /**
   * @param topAndBottom
   * @param leftAndRight
   */
  public boolean getMarginAroundGraphRect(
      final SGTuple2f topAndBottom, final SGTuple2f leftAndRight);

  /** */
  public float getGraphRectX();

  /** */
  public float getGraphRectY();

  /** */
  public float getGraphRectWidth();

  /** */
  public float getGraphRectHeight();

  /** ] */
  public Rectangle2D getGraphRect();

  /** */
  public boolean setGraphRect(float x, float y, float width, float height);

  /**
   * @param rect
   */
  public boolean setGraphRect(Rectangle2D rect);

  /** */
  public boolean setGraphRectLocation(float x, float y);

  /** */
  public boolean setGraphRectSize(float width, float height);

  /** */
  public boolean setDialogOwner(Frame frame);

  /** */
  public Cursor getFigureElementCursor();

  /** key event */
  public boolean onKeyPressed(KeyEvent e);

  /**
   * @param e
   */
  public boolean onMouseClicked(MouseEvent e);

  /**
   * @param e
   */
  public boolean onMousePressed(MouseEvent e);

  /**
   * @param e
   */
  public boolean onMouseDragged(MouseEvent e);

  /**
   * @param e
   */
  public boolean onMouseReleased(MouseEvent e);

  /**
   * Called when the mouse pointer moves.
   *
   * @param e the mouse event
   */
  public boolean onMouseMoved(MouseEvent e);

  /**
   * @param x the x parameter
   * @param y the y parameter
   */
  public boolean setMouseCursor(int x, int y);

  /** */
  public void addActionListener(ActionListener listener);

  /** */
  public void removeActionListener(ActionListener listener);

  /**
   * Creates an array of Element objects.
   *
   * @param params the params parameter
   * @param document an Document objects to append elements
   */
  public Element[] createElement(final Document document, SGExportParameter params);

  /**
   * Read properties from the Element object.
   *
   * @param element an Element object which has properties
   * @param versionNumber the version number of property file
   */
  public boolean readProperty(final Element element, final String versionNumber);

  /** initialize compatible properties for previous property file. */
  public boolean initCompatibleProperty();

  /**
   * Create objects related to the data with information given by an Element object.
   *
   * @param readDataProperty the readDataProperty parameter
   * @param el an Element object
   * @param data a data object
   */
  public boolean createDataObject(Element el, SGData data, final boolean readDataProperty);

  /** Clear all focused objects. */
  public boolean clearFocusedObjects();

  /**
   * Clear focused objects.
   *
   * @param ori an origin of this clearance
   */
  public boolean clearFocusedObjects(SGIFigureElement ori);

  /** */
  public boolean hideSelectedObjects();

  /**
   * Set the visibility of symbols around the focused objects. This method just hide the symbol, and
   * does not clear the list of the focused objects.
   *
   * @param b visibility
   */
  public void setSymbolsVisibleAroundFocusedObjects(final boolean b);

  /**
   * Set the visibility of symbols around all objects.
   *
   * @param b visibility
   */
  public void setSymbolsAroundAllObjectsVisible(final boolean b);

  /**
   * @param dx
   * @param dy
   */
  public void translateFocusedObjects(final int dx, final int dy);

  /** Updates changed flag of focused objects. */
  public boolean updateChangedFlag();

  /** Returns whether any focused objects are changed. */
  public boolean isFocusedObjectsChanged();

  /** Returns a list of the focused objects. */
  public List<SGISelectable> getFocusedObjectsList();

  /** Duplicate the focused objects. */
  public boolean duplicateFocusedObjects();

  /** Returns the list of copied objects. */
  public List<SGICopyable> getCopiedObjectsList();

  /** Cut focused copyable objects. */
  public List<SGICopyable> cutFocusedObjects();

  /**
   * Paste the objects.
   *
   * @param list of the objects to be pasted
   */
  public boolean paste(List<SGICopyable> list);

  /**
   * Move the focused objects to the head or the tail of the list
   *
   * @param toTail flag whether to move focused objects to the tail of the list
   */
  public boolean moveFocusedObjects(final boolean toTail);

  /**
   * Move the focused objects to forward or backward for given steps.
   *
   * @param num the number of levels to move
   */
  public boolean moveFocusedObjects(int num);

  /** Return list of the visible child objects. */
  public List<SGIChildObject> getVisibleChildList();

  /**
   * Returns the list of selected property dialog observers of given class type.
   *
   * @param cl the class
   */
  public List<SGIPropertyDialogObserver> getSelectedPropertyDialogObserverList(Class<?> cl);

  /**
   * Returns the list of visible property dialog observers of given class type.
   *
   * @param cl the class
   */
  public List<SGIPropertyDialogObserver> getVisiblePropertyDialogObserverList(Class<?> cl);

  /**
   * Returns the list of all property dialog observers of given class type.
   *
   * @param cl the class
   */
  public List<SGIPropertyDialogObserver> getAllPropertyDialogObserverList(Class<?> cl);

  public void setMode(final int mode);

  /**
   * Returns the child object of a given ID.
   *
   * @param id the ID number of the child
   */
  public SGIChildObject getChild(final int id);

  /**
   * Hides the object of given ID.
   *
   * @param id the ID of an object
   */
  public boolean hideChildObject(final int id);

  /**
   * Brings to front or sends to back the object of given ID.
   *
   * @param id the ID of an object
   * @param toFront true to bring to front
   */
  public boolean moveChildToEnd(final int id, final boolean toFront);

  /**
   * Brings forward or sends backward the object of given ID.
   *
   * @param id the ID of an object
   * @param toFront true to bring forward
   */
  public boolean moveChild(final int id, final boolean toFront);

  /**
   * Synchronize the data properties with given data properties.
   *
   * @param p the properties
   * @param dp data properties
   */
  public SGProperties synchronizeDataProperties(SGProperties p, SGProperties dp);

  /** Called when menu items in the menu bar is selected. */
  public void onMenuSelected();

  /**
   * Sets the properties.
   *
   * @param map a map of properties
   */
  public SGPropertyResults setProperties(SGPropertyMap map);

  /**
   * Sets the properties of child object.
   *
   * @param id the ID of child object
   * @param map a map of properties
   */
  public SGPropertyResults setChildProperties(final int id, SGPropertyMap map);

  /**
   * Sets the properties of sub child object.
   *
   * @param id the ID of child object
   * @param subId the ID of sub child object
   * @param map a map of properties
   */
  public SGPropertyResults setChildProperties(final int id, final int subId, SGPropertyMap map);

  /**
   * Sets the properties of sub child object.
   *
   * @param id the ID of child object
   * @param colorMapName the name of color map
   * @param map a map of properties
   */
  public SGPropertyResults setChildColorMapProperties(
      final int id, final String colorMapName, SGPropertyMap map);

  /** Closes the text field. */
  public boolean closeTextField();

  /**
   * Hides the data objects of given IDs.
   *
   * @param dataIdArray
   */
  public boolean hideData(final int[] dataIdArray);

  /**
   * Sets the flag whether data objects in this figure are anchored
   *
   * @param b true to set data objects in this figure anchored
   */
  public boolean setDataAnchored(final boolean b);

  /** Returns the class object of property dialog observer. */
  public Class<?> getPropertyDialogObserverClass();

  /**
   * Sets the class object of property dialog observer.
   *
   * @param cl a class object to set
   */
  public void setPropertyDialogObserverClass(Class<?> cl);

  /**
   * Sets the window.
   *
   * @param wnd a window
   */
  public void setWindow(SGDrawingWindow wnd);

  /** Returns the window. */
  public SGDrawingWindow getWindow();

  /** Returns a text string of the commands. */
  public String getCommandString(SGExportParameter params);

  /**
   * Replaces the data source.
   *
   * @param srcOld old data source
   * @param srcNew new data source
   * @param obs data source observer
   */
  public void replaceDataSource(
      final SGIDataSource srcOld, final SGIDataSource srcNew, final SGDataSourceObserver obs);

  public List<SGINode> getNodes();
}

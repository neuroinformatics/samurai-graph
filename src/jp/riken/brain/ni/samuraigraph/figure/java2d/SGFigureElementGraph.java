package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGICopiable;
import jp.riken.brain.ni.samuraigraph.base.SGIData;
import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxisBreak;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGrid;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementLegend;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementShape;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementSignificantDifference;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementString;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementTimingLine;
import jp.riken.brain.ni.samuraigraph.base.SGISelectable;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility.MouseDragResult;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYZTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGIVXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFMultipleData;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroupSet;
import jp.riken.brain.ni.samuraigraph.figure.SGIBarConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIVXYDataConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * An object drawing drawing elements of data.
 */
public class SGFigureElementGraph extends SGFigureElementForData implements
        SGIFigureElementGraph, SGIFigureDrawingElementConstants,
        SGIDrawingElementConstants, SGISXYDataConstants, SGIVXYDataConstants {

    /**
     * Returns the axis figure element.
     *
     * @return the axis figure element
     */
    public SGIFigureElementAxis getAxisElement() {
        return this.mAxisElement;
    }

    /**
     * Default constructor.
     */
    public SGFigureElementGraph() {
        super();
    }

    public void dispose() {
        super.dispose();
    }

    /**
     *
     * @return
     */
    public String getInstanceDescription() {
        return "Graph";
    }

    /**
     * Update the location of all drawing elements with data.
     */
    protected boolean updateAllDrawingElementsLocation() {
    	if (!this.updateAllDrawingElementsLocationSub()) {
    		return false;
    	}
        this.repaint();
        return true;
    }

    private boolean updateAllDrawingElementsLocationSub() {
        for (int ii = 0; ii < this.mChildList.size(); ii++) {
            final SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) this.mChildList
                    .get(ii);
            if (groupSet.isVisible() == false) {
                continue;
            }
            final SGData data = groupSet.getData();
        	if (!this.updateDrawingElementsLocation(data, groupSet)) {
        		return false;
        	}
        }
        return true;
    }

    /**
     * Update the location of drawing elements of given data.
     *
     * @param data
     *           a data
     */
    public boolean updateDrawingElementsLocation(final SGData data) {
    	SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) this.getElementGroupSet(data);
    	if (groupSet == null) {
    		return false;
    	}
    	if (!this.updateDrawingElementsLocation(data, groupSet)) {
    		return false;
    	}
        this.repaint();
        return true;
    }

    private boolean updateDrawingElementsLocation(final SGData data, final SGElementGroupSetInGraph groupSet) {
        try {
            // calculate coordinates in graph
            if (groupSet.updateDrawingElementsLocation(data) == false) {
                return false;
            }
        } catch (Exception e) {
            if (SGDataUtility.isNetCDFData(data)) {
                if (SGDataUtility.canOpenNetCDF((SGNetCDFData)data)==false) {
                    SGUtility.showErrorMessageDialog(null, "NetCDF file connection refused. Delete this data.", "ERROR");
                    this.notifyToListener(NOTIFY_UNKNOWN_DATA_ERROR, Integer.valueOf(groupSet.getID()));
                }
            }
            return false;
        }
        return true;
    }

    /**
     *
     */
    public boolean setAxisElement(final SGIFigureElementAxis element) {
        this.mAxisElement = element;
        return true;
    }

    /**
     * Add a data object with given name.
     *
     * @param data
     *            data to add
     * @param name
     *            name of data
     * @return true if succeeded
     */
    @Override
    public boolean addData(final SGData data, final String name) {
        return this.addData(data, name, 0, false, null);
    }

    @Override
    public boolean addData(final SGData data, final String name, final Map<String, Object> infoMap) {
        return this.addData(data, name, 0, false, infoMap);
    }

    /**
     * Add a data object with given name and given ID.
     *
     * @param data
     *            data to add
     * @param name
     *            name of data
     * @param id
     *            the ID to set
     * @param infoMap
     *            the information map of data object
     * @return true if succeeded
     */
    @Override
    public boolean addData(final SGData data, final String name, final int id, final Map<String, Object> infoMap) {
        return this.addData(data, name, id, true, infoMap);
    }

    private boolean addData(final SGData data, final String name, final int id,
            final boolean withID, final Map<String, Object> infoMap) {
        if (super.addData(data, name) == false) {
            return false;
        }

        // create an ElementGroupSetInGraph object
        SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) this.createGroupSet(
        		data, name, infoMap);
        if (groupSet == null) {
            return false;
        }

        // add to list
        if (withID) {
            if (this.addToList(id, groupSet) == false) {
                return false;
            }
        } else {
            if (this.addToList(groupSet) == false) {
                return false;
            }
        }

        // initialize the properties
        groupSet.initPropertiesHistory();

        // set the change flag
        this.setChanged(true);

        // update the location
        if (this.updateAllDrawingElementsLocationSub() == false) {
            return false;
        }
        if (!groupSet.updateWithData()) {
        	return false;
        }
        this.repaint();
        
        return true;
    }

    /**
     * Add a data object with a set of properties.
     *
     * @param data
     *             added data.
     * @param name
     *             the name set to the data
     * @param p
     *             properties set to be data.
     * @return
     *             true if succeeded
     */
    public boolean addData(final SGData data, final String name,
            final SGProperties p) {

        if (super.addData(data, name, p) == false) {
            return false;
        }

        // create an ElementGroupSetInGraph object
        SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) this.createGroupSet(
        		data, name, null);
        if (groupSet == null) {
            return false;
        }

        // add to list
        this.addToList(groupSet);

        // set properties
        if (groupSet.setProperties(p) == false) {
            return false;
        }

        if (SGDataUtility.isSXYTypeData(data.getDataType()) && SGDataUtility.isNetCDFData(data)) {
            this.updateBarVerticalOfNetCDFData(groupSet, data);
        }

        // set visible because cut data objects are set invisible
        groupSet.setVisible(true);

        // Set the given name to data.
        // The name given in p would be redundant.
        groupSet.setName(name);

        // initialize the properties
        groupSet.initPropertiesHistory();

        // set the change flag
        this.setChanged(true);

        // update the location
        if (groupSet.updateWithData() == false) {
            return false;
        }

        return true;
    }

	private void updateBarVerticalOfNetCDFData(
			SGElementGroupSetInGraph groupSet, SGData data) {
		if (data instanceof SGSXYNetCDFData) {
			SGSXYNetCDFData ndata = (SGSXYNetCDFData) data;
			boolean vertical = ndata.isXVariableCoordinate();
			if (groupSet instanceof SGElementGroupSetInGraphSXYMultiple) {
				SGElementGroupSetInGraphSXYMultiple gs = (SGElementGroupSetInGraphSXYMultiple) groupSet;
				if (gs.isBarVertical() != vertical) {
					gs.setBarVertical(vertical);
				}
			} else if (groupSet instanceof SGElementGroupSetInGraphSXY) {
				SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) groupSet;
				if (gs.isBarVertical() != vertical) {
					gs.setBarVertical(vertical);
				}
			}
		} else if (data instanceof SGSXYNetCDFMultipleData) {
			SGSXYNetCDFMultipleData ndata = (SGSXYNetCDFMultipleData) data;
			boolean vertical = ndata.isXVariableCoordinate();
			if (groupSet instanceof SGElementGroupSetInGraphSXYMultiple) {
				SGElementGroupSetInGraphSXYMultiple gs = (SGElementGroupSetInGraphSXYMultiple) groupSet;
				if (gs.isBarVertical() != vertical) {
					gs.setBarVertical(vertical);
				}
			} else if (groupSet instanceof SGElementGroupSetInGraphSXY) {
				SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) groupSet;
				if (gs.isBarVertical() != vertical) {
					gs.setBarVertical(vertical);
				}
			}
		}
	}

    /**
     * Sets the magnification.
     *
     * @param mag
     *            the magnification to set
     * @return
     *            true if succeeded
     */
    public boolean setMagnification(final float mag) {
        if (super.setMagnification(mag) == false) {
            return false;
        }
        if (this.updateAllDrawingElementsLocation() == false) {
            return false;
        }
        return true;
    }


    /**
     *
     */
    public boolean setGraphRect(final float x, final float y,
            final float width, final float height) {
        super.setGraphRect(x, y, width, height);
        if (this.updateAllDrawingElementsLocation() == false) {
            return false;
        }
        return true;
    }

    /**
     * Synchronize to the other figure element.
     *
     * @param element
     *            a figure element
     * @param msg
     *            a message
     * @return true if succeeded
     */
    public boolean synchronize(final SGIFigureElement element, final String msg) {

        boolean flag = true;
        if (element instanceof SGIFigureElementLegend) {
            final SGIFigureElementLegend lElement = (SGIFigureElementLegend) element;
            flag = this.synchronizeToLegendElement(lElement, msg);
        } else if (element instanceof SGIFigureElementAxis) {
            // System.out.println("SGIAxisElement");

            final SGIFigureElementAxis aElement = (SGIFigureElementAxis) element;
            flag = this.synchronizeToAxisElement(aElement, msg);
        } else if (element instanceof SGIFigureElementString) {
            if (NOTIFY_NETCDF_DATALABEL_WILL_BE_HIDDEN.equals(msg)) {
                SGIFigureElementString sElement = (SGIFigureElementString)element;
                this.removeHiddenNetCDFLabels(sElement);
            }
        } else if (element instanceof SGIFigureElementGraph) {

        } else if (element instanceof SGIFigureElementAxisBreak) {

        } else if (element instanceof SGIFigureElementSignificantDifference) {

        } else if (element instanceof SGIFigureElementTimingLine) {

        } else if (element instanceof SGIFigureElementGrid) {

        } else if (element instanceof SGIFigureElementShape) {

        } else {
            flag = this.synchronizeArgument(element, msg);
        }

        return flag;
    }

    /**
     *
     */
    private boolean synchronizeToAxisElement(
            final SGIFigureElementAxis aElement, final String msg) {
        if (this.updateAllDrawingElementsLocation() == false) {
            return false;
        }
        if (this.isNotificationMessage(msg)) {
            final SGColorMap model = aElement.getColorMap();
            for (int ii = 0; ii < this.mChildList.size(); ii++) {
                SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) this.mChildList
                        .get(ii);
                if (groupSet instanceof SGElementGroupSetInGraphSXYZ) {
                    SGElementGroupSetInGraphSXYZ gs = (SGElementGroupSetInGraphSXYZ) groupSet;

                    // synchronize the color bar
                    if (gs.setColorBarModel(model) == false) {
                    	return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Synchronize to the legend element.
     * @param lElement
     *                the legend element
     * @param msg
     *                a message
     * @return
     *                true if succeeded
     */
    private boolean synchronizeToLegendElement(
            final SGIFigureElementLegend lElement, final String msg) {
    	return this.synchronizeToDataElement(lElement, msg);
    }

    /**
     * Synchronize the element given by the argument.
     *
     * @param element
     *            An object to be synchronized.
     */
    public boolean synchronizeArgument(final SGIFigureElement element,
            final String msg) {
        // this shouldn't happen
        throw new Error();
    }

    /**
     * Returns whether the data object is selected.
     * @param data
     *             a data object
     * @return
     *             true if selected
     */
    public boolean isDataSelected(final SGData data) {
	if (data == null) {
	    throw new IllegalArgumentException("data == null");
	}
	SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) this.getElementGroupSet(data);
	if (gs == null) {
	    throw new Error("Data is not found.");
	}
	return gs.isSelected();
    }

    /**
     *
     */
    protected boolean isInsideAxisRange(final SGTuple2d value,
            final SGAxis axisX, final SGAxis axisY) {
        return (axisX.insideRange(value.x) && axisY.insideRange(value.y));
    }

    /**
     * Calculate the location of points.
     *
     * @param valueArray
     *           the array of axis values
     * @param axis
     *           the axis for give values
     * @param isXValues
     *           a flag whether the given values are for the x-axis
     * @param pointArray
     *           the coordinates for given values
     * @return true if succeeded
     */
    protected boolean calcLocationOfPoints(final double[] valueArray,
            final SGAxis axis, final boolean isXValue, final float[] pointArray) {

        final int num = valueArray.length;

        final SGTuple2d range = axis.getRange();
        final double min = range.x;
        final double max = range.y;
        final int type = axis.getScaleType();
        final boolean invcoord = axis.isInvertCoordinates();

        // transform to the values in the graph
        double minInScale = 0.0;
        double maxInScale = 0.0;
        final double[] valueInScale = new double[num];
        if (type == SGAxis.LINEAR_SCALE) {
            minInScale = min;
            maxInScale = max;
        } else if (type == SGAxis.LOG_SCALE) {
            minInScale = Math.log(min);
            maxInScale = Math.log(max);
        }

        // calculate the ratio in the graph rectangle
        // and get the location
        final float start = isXValue ? this.mGraphRectX : this.mGraphRectY;
        final float size = isXValue ? this.mGraphRectWidth : this.mGraphRectHeight;
        for (int ii = 0; ii < num; ii++) {
            if (type == SGAxis.LINEAR_SCALE) {
                valueInScale[ii] = valueArray[ii];
            } else if (type == SGAxis.LOG_SCALE) {
                valueInScale[ii] = Math.log(valueArray[ii]);
            }
            // ratio in the graph area rectangle
            float ratio;
            if (invcoord) {
                ratio = (float) ((maxInScale - valueInScale[ii]) / (maxInScale - minInScale));
            } else {
                ratio = (float) ((valueInScale[ii] - minInScale) / (maxInScale - minInScale));
            }
            if (!isXValue) {
                ratio = 1.0f - ratio;
            }

            // set the location of points
            final float pos = start + ratio * size;
            pointArray[ii] = pos;
        }

        return true;
    }

    /**
     * Calculate the location of points.
     *
     * @param cArray
     *           the array of axis values
     * @param axisX
     *           the x-axis for give values
     * @param axisY
     *           the y-axis for give values
     * @param pointArray
     *           the coordinates for given values
     * @return true if succeeded
     */
    protected boolean calcLocationOfPoints(final SGTuple2d[] cArray,
            final SGAxis axisX, final SGAxis axisY, final SGTuple2f[] pointArray) {

        final int num = cArray.length;

        final SGTuple2d rangeX = axisX.getRange();
        final SGTuple2d rangeY = axisY.getRange();
        final double minX = rangeX.x;
        final double maxX = rangeX.y;
        final double minY = rangeY.x;
        final double maxY = rangeY.y;
        final int typeX = axisX.getScaleType();
        final int typeY = axisY.getScaleType();

        final boolean invcoordX = axisX.isInvertCoordinates();
        final boolean invcoordY = axisY.isInvertCoordinates();

        // transform to the values in the graph
        double minXInScale = 0.0;
        double maxXInScale = 0.0;
        double minYInScale = 0.0;
        double maxYInScale = 0.0;
        final double[] xInScale = new double[num];
        final double[] yInScale = new double[num];

        if (typeX == SGAxis.LINEAR_SCALE) {
            minXInScale = minX;
            maxXInScale = maxX;
        } else if (typeX == SGAxis.LOG_SCALE) {
            minXInScale = Math.log(minX);
            maxXInScale = Math.log(maxX);
        }

        if (typeY == SGAxis.LINEAR_SCALE) {
            minYInScale = minY;
            maxYInScale = maxY;
        } else if (typeY == SGAxis.LOG_SCALE) {
            minYInScale = Math.log(minY);
            maxYInScale = Math.log(maxY);
        }

        // calculate the ratio in the graph rectangle
        // and get the location
        final float gx = this.mGraphRectX;
        final float gy = this.mGraphRectY;
        final float gw = this.mGraphRectWidth;
        final float gh = this.mGraphRectHeight;
        for (int ii = 0; ii < num; ii++) {
            if (typeX == SGAxis.LINEAR_SCALE) {
                xInScale[ii] = cArray[ii].x;
            } else if (typeX == SGAxis.LOG_SCALE) {
                xInScale[ii] = Math.log(cArray[ii].x);
            }
            if (typeY == SGAxis.LINEAR_SCALE) {
                yInScale[ii] = cArray[ii].y;
            } else if (typeY == SGAxis.LOG_SCALE) {
                yInScale[ii] = Math.log(cArray[ii].y);
            }
            // ratio in the graph area rectangle
            float xRatio, yRatio;
            if (invcoordX) {
                xRatio = (float) ((maxXInScale - xInScale[ii]) / (maxXInScale - minXInScale));
            } else {
                xRatio = (float) ((xInScale[ii] - minXInScale) / (maxXInScale - minXInScale));
            }
            if (invcoordY) {
                yRatio = (float) (1.0 - (maxYInScale - yInScale[ii])
                        / (maxYInScale - minYInScale));
            } else {
                yRatio = (float) (1.0 - (yInScale[ii] - minYInScale)
                        / (maxYInScale - minYInScale));
            }

            // set the location of points
            final float posX = gx + xRatio * gw;
            final float posY = gy + yRatio * gh;
            pointArray[ii].setValues(posX, posY);
        }

        return true;
    }

//    /**
//     * Calculate the location of points.
//     *
//     * @param xValueArray
//     *           the array of x-values
//     * @param yValueArray
//     *           the array of y-values
//     * @param axisX
//     *           the x-axis for give values
//     * @param axisY
//     *           the y-axis for give values
//     * @param shiftX
//     *           shift value for x-values
//     * @param shiftY
//     *           shift value for y-values
//     * @param pointArray
//     *           the coordinates for given values
//     * @return true if succeeded
//     */
//	protected boolean calcLocationOfPoints(final double[] xValueArray,
//			final double[] yValueArray, final SGAxis axisX, final SGAxis axisY,
//			final double shiftX, final double shiftY,
//			final SGTuple2f[] pointArray) {
//
//		if (xValueArray == null || yValueArray == null) {
//			return false;
//		}
//
//		if (xValueArray.length != yValueArray.length) {
//			throw new IllegalArgumentException(
//					"xValueArray.length!=yValueArray.length : "
//							+ xValueArray.length + ", " + yValueArray.length);
//		}
//
//		final int num = xValueArray.length;
//		SGTuple2d[] cArray = new SGTuple2d[num];
//		for (int ii = 0; ii < cArray.length; ii++) {
//			cArray[ii] = new SGTuple2d(xValueArray[ii] + shiftX,
//					yValueArray[ii] + shiftY);
//		}
//
//		return this.calcLocationOfPoints(cArray, axisX, axisY, pointArray);
//	}

    /**
     *
     */
    public int getSelectedDataNumber() {
        return this.getFocusedObjectsList().size();
    }

    /**
     * Clear focused objects.
     * @param ori
     *            an origin of this clearance
     * @return
     *           true if succeeded
     */
    public boolean clearFocusedObjects(SGIFigureElement ori) {
	if (ori instanceof SGIFigureElementLegend) {
	    // if data object is not selected in the legend element,
	    // clear the focused objects in this graph
	    SGIFigureElementLegend lElement = (SGIFigureElementLegend) ori;
	    List dList = lElement.getFocusedDataList();
	    if (dList.size() == 0) {
		if (this.clearFocusedObjects() == false) {
		    return false;
		}
	    }
	} else if (ori instanceof SGIFigureElementGraph) {
	    // do nothing
	    return true;
	} else {
	    if (this.clearFocusedObjects() == false) {
		return false;
	    }
	}
        return true;
    }

    /**
     * Cut focused copiable objects.
     *
     * @return a list of cut objects
     */
    public List<SGICopiable> cutFocusedObjects() {
		// returns an empty list because no objects exist that can be cut
		// other than data
        return new ArrayList<SGICopiable>();
    }

    /**
     * Returns a list of the cut data objects.
     *
     * @return a list to which the cut data objects are added
     */
    public List cutFocusedData() {
        List<SGData> list = this.getFocusedDataList();
        this.hideSelectedData();
        return list;
    }

    /**
     *
     * @param groupSet
     * @return
     */
    protected boolean hideGroupSet(final SGElementGroupSet groupSet) {
        // set invisible
        groupSet.setVisible(false);

        return true;
    }

    /**
     *
     * @param g2d
     * @param symbol
     * @return
     */
    protected boolean drawAnchorsOnRectangle(final Rectangle2D rect,
            final Graphics2D g2d) {
        final float x = (float) rect.getX();
        final float y = (float) rect.getY();
        final float w = (float) rect.getWidth();
        final float h = (float) rect.getHeight();
        Point2D pos = new Point2D.Float();
        pos.setLocation(x, y);
        SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(pos, g2d);
        pos.setLocation(x + w, y);
        SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(pos, g2d);
        pos.setLocation(x, y + h);
        SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(pos, g2d);
        pos.setLocation(x + w, y + h);
        SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(pos, g2d);
        return true;
    }

    /**
     * Called when the mouse is clicked.
     *
     * @param e
     *            the mouse event
     * @return true when something is clicked in this object
     */
    public boolean onMouseClicked(final MouseEvent e) {

        // click the data
        List<SGIChildObject> list = this.mChildList;
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            final SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) list
                    .get(ii);
            if (groupSet.isVisible() == false) {
                continue;
            }
//            try {
//                if (groupSet.onMouseClicked(e)) {
//                    return true;
//                }
//            } catch (Exception ex) {
//                if (SGDataUtility.isNetCDFData(groupSet.getData())) {
//                    if (SGDataUtility.canOpenNetCDF((SGNetCDFData)groupSet.getData())==false) {
//                        SGUtility.showErrorMessageDialog(null, "NetCDF file connection refused. Delete this data.", "ERROR");
//                        this.notifyToListener(NOTIFY_UNKNOWN_DATA_ERROR, Integer.valueOf(groupSet.getID()));
//                    }
//                }
//                return false;
//            }
            if (groupSet.onMouseClicked(e)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Called when the mouse is pressed.
     *
     * @param e
     *            the mouse event
     * @return true when something is pressed in this object
     */
    public boolean onMousePressed(final MouseEvent e) {
		for (int ii = this.mChildList.size() - 1; ii >= 0; ii--) {
			final SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) this.mChildList
					.get(ii);
			if (!groupSet.isVisible()) {
				continue;
			}
			if (groupSet.onMousePressed(e)) {
				// records the present point of other group set objects
				for (int jj = 0; jj < this.mChildList.size(); jj++) {
					final SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) this.mChildList
							.get(jj);
					if (gs.equals(groupSet)) {
						continue;
					}
					gs.recordPresentPoint(e.getX(), e.getY());
				}

				// records the pressed point and the group set
				this.mPressedPoint = e.getPoint();
				this.mPressedGroupSet = groupSet;
                this.mDraggableFlag = true;
				return true;
			}
		}
		return false;
    }

    SGElementGroupSetInGraph mPressedGroupSet = null;
    
    /**
     * Called when the mouse is dragged.
     *
     * @param e
     *            the mouse event
     * @return true if succeeded
     */
    public boolean onMouseDragged(final MouseEvent e) {
    	if (this.mDraggableFlag == false) {
    		return false;
    	}
        if (this.mPressedPoint == null) {
            return false;
        }

        SGElementGroupSetInGraph gs = null;
        List<SGISelectable> list = this.getFocusedObjectsList();
        if (list.size() == 1) {
            gs = (SGElementGroupSetInGraph) list.get(0);
        } else if (list.size() > 1 && this.mPressedGroupSet != null) {
        	gs = this.mPressedGroupSet;
        }
        if (gs != null) {
        	MouseDragResult result = this.getMouseDragResult(e);
        	final int ex = result.ex;
        	final int ey = result.ey;
        	
            // translate the group set selected at the last
            this.mPressedGroupSet.translateTo(ex, ey);
            
            // updates the pressed point
            this.mPressedPoint = e.getPoint();
            
            // notifies the change
            this.notifyChange();
        }

        return true;
    }

    /**
     * Called when the mouse is released.
     *
     * @param e
     *            the mouse event
     * @return always returns true
     */
    public boolean onMouseReleased(final MouseEvent e) {
        List<SGISelectable> list = this.getFocusedObjectsList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) list.get(ii);
            gs.onMouseReleased(e);
        }
        this.mDraggableFlag = false;
        this.mDraggedDirection = null;
        this.mPressedGroupSet = null;

        return true;
    }

    /**
     *
     * @param e
     */
    public boolean setMouseCursor(final int x, final int y) {
        /*
         * for( int
         * ii=this.mGroupSetList.size()-1; ii>=0; ii-- ) { final
         * ElementGroupSetInGraph groupSet =
         * (ElementGroupSetInGraph)mGroupSetList.get(ii); if(
         * groupSet.onDrawingElement(x,y) ) { this.mCursor = new Cursor(
         * Cursor.HAND_CURSOR ); return true; } }
         */

        return false;
    }

//    /**
//     *
//     * @return
//     */
//    public boolean setTemporaryPropertiesOfFocusedObjects() {
//        return true;
//    }

    /**
     * Updates changed flag of focused objects.
     * 
     * @return true if succeeded
     */
    @Override
    public boolean updateChangedFlag() {
    	// do nothing
        return true;
    }

    protected boolean drawRectangle(final double xMin, final double xMax,
            final double yMin, double yMax, final Graphics2D g2d) {

        if (g2d == null) {
            return false;
        }

        final Line2D lineUpper = new Line2D.Double(xMin, yMin, xMax, yMin);
        final Line2D lineLower = new Line2D.Double(xMin, yMax, xMax, yMax);
        final Line2D lineLeft = new Line2D.Double(xMin, yMin, xMin, yMax);
        final Line2D lineRight = new Line2D.Double(xMax, yMin, xMax, yMax);

        g2d.draw(lineUpper);
        g2d.draw(lineLower);
        g2d.draw(lineLeft);
        g2d.draw(lineRight);

        return true;
    }

    /**
     *
     * @return
     */
    public String getTagName() {
        return TAG_NAME_GRAPH;
    }

    /**
     *
     */
    public Element[] createElement(final Document document, SGExportParameter params) {
        return null;
    }

    /**
     *
     */
    public boolean writeProperty(final Element el, SGExportParameter params) {
        return true;
    }

    /**
     * Read properties from the Element object.
     *
     * @param element
     *            an Element object which has properties
     * @param versionNumber
     *            the version number of property file
     * @return true if succeeded
     */
    public boolean readProperty(final Element element, final String versionNumber) {
        return true;
    }

    /**
     * Create and add Element to the Document object.
     * 
     * @param document
     *           the document
     * @param elList
     *           a list of Element objects
     * @param dataList
     *           a list of data
     * @param params
     *           the parameters
     * @return true if succeeded
     */
    @Override
    public boolean createElementOfData(Document document,
            List<Element> elList, List<SGData> dataList, SGExportParameter params) {
        List<SGIChildObject> groupSetList = this.getVisibleChildList();
        for (int ii = 0; ii < groupSetList.size(); ii++) {
            SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) groupSetList
                    .get(ii);
            SGData data = groupSet.getData();
        	if (params instanceof SGDataExportParameter) {
        		SGDataExportParameter dParams = (SGDataExportParameter) params;
        		if (!dParams.canExport(data)) {
        			continue;
        		}
        	}
            Element el = groupSet.createElement(document, params);
            if (el == null) {
                return false;
            }
            elList.add(el);
            dataList.add(data);
        }
        return true;
    }

    /**
     *
     */
    public SGProperties getProperties() {
        SGProperties p = new GraphProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    /**
     *
     */
    public boolean getProperties(final SGProperties p) {
        if ((p instanceof GraphProperties) == false) {
            return false;
        }

        GraphProperties gp = (GraphProperties) p;
        gp.visibleElementGroupList = new ArrayList(this.getVisibleChildList());

        return true;
    }

    /**
     *
     */
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof GraphProperties) == false) {
            return false;
        }

        GraphProperties gp = (GraphProperties) p;

        boolean flag;
        flag = this.setVisibleChildList(gp.visibleElementGroupList);
        if (!flag) {
            return false;
        }

        return true;

    }

    /**
     *
     */
    protected boolean setVisibleChildList(final List<SGIChildObject> list) {

        List<SGIChildObject> visibleList = new ArrayList<SGIChildObject>(list);
        List<SGIChildObject> invisibleList = new ArrayList<SGIChildObject>();

        List<SGData> visibleDataList = new ArrayList<SGData>();
        List<SGData> invisibleDataList = new ArrayList<SGData>();

        for (int ii = 0; ii < this.mChildList.size(); ii++) {
            SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) this.mChildList
                    .get(ii);
            final boolean b = list.contains(groupSet);
            groupSet.setVisible(b);
            if (!b) {
                invisibleList.add(groupSet);
                invisibleDataList.add(this.getData(groupSet));
            }
        }

        for (int ii = 0; ii < visibleList.size(); ii++) {
            SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) visibleList
                    .get(ii);
            SGData data = this.getData(groupSet);
            visibleDataList.add(data);
        }

        this.mChildList.clear();
//        this.mDataList.clear();
        for (int ii = 0; ii < visibleList.size(); ii++) {
            this.mChildList.add(visibleList.get(ii));
//            this.mDataList.add(visibleDataList.get(ii));
        }
        for (int ii = 0; ii < invisibleList.size(); ii++) {
            this.mChildList.add(invisibleList.get(ii));
//            this.mDataList.add(invisibleDataList.get(ii));
        }

        return true;
    }

    /**
     *
     */
    public boolean setMementoBackward() {
        boolean flag = super.setMementoBackward();
        if (!flag) {
            return false;
        }

        this.clearFocusedObjects();
        this.updateAllDrawingElementsLocation();
        // updateImage();
        this.notifyChangeOnUndo();

        return true;
    }

    /**
     *
     */
    public boolean setMementoForward() {
        boolean flag = super.setMementoForward();
        if (!flag) {
            return false;
        }

        this.clearFocusedObjects();
        this.updateAllDrawingElementsLocation();
        // updateImage();
        this.notifyChangeOnUndo();

        return true;
    }

    /**
     *
     */
    protected Set getAvailableChildSet() {
        Set set = new HashSet();
        List mList = this.getMementoList();
        for (int ii = 0; ii < mList.size(); ii++) {
            GraphProperties p = (GraphProperties) mList.get(ii);
            set.addAll(p.visibleElementGroupList);
        }

        return set;
    }

    //
    protected boolean readProperty(final SGElementGroup group, final Element el) {
        String str;
        Boolean b;

        // visible
        str = el.getAttribute(KEY_VISIBLE);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return false;
            }
            group.setVisible(b.booleanValue());
        }

        return true;
    }

    protected void paintGraphics(Graphics g, boolean clip) {
        Graphics2D g2d = (Graphics2D) g;

        List list = this.mChildList;
        if (list == null) {
        	return;
        }

        // draw strings in data for SXY data
        for (int ii = 0; ii < list.size(); ii++) {
            final SGElementGroupSet groupSet = (SGElementGroupSet) list.get(ii);

            if (groupSet.isVisible()) {
                if (groupSet instanceof SGIElementGroupSetInGraphSXY) {
                    SGIElementGroupSetInGraphSXY iGroupSet = (SGIElementGroupSetInGraphSXY) groupSet;
                    iGroupSet.paintDataString(g2d);
                }
            }
        }

        // clipping of graph rectangle
        if (clip) {
            SGUtilityForFigureElementJava2D.clipGraphRect(this, g2d);
        }

        // draw the graph
        for (int ii = 0; ii < list.size(); ii++) {
            final SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) list
                    .get(ii);
            if (groupSet.isVisible()) {
                groupSet.setClipFlag(clip);
                groupSet.paintGraphics2D(g2d);
            }
        }

        if (clip) {
            g2d.setClip(this.getViewBounds());
        }
    }

    public String getClassDescription() {
        return "Graph";
    }

    public boolean getMarginAroundGraphRect(SGTuple2f topAndBottom,
            SGTuple2f leftAndRight) {

        if (super.getMarginAroundGraphRect(topAndBottom, leftAndRight) == false) {
            return false;
        }

        final ArrayList rectList = new ArrayList();
        for (int ii = 0; ii < this.mChildList.size(); ii++) {
            final SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) this.mChildList
                    .get(ii);

            if (groupSet instanceof SGIElementGroupSetInGraphSXY) {
                // get a bounding box which includes all tick labels

                SGIElementGroupSetInGraphSXY gsSXY = (SGIElementGroupSetInGraphSXY) groupSet;
                SGData data = getData(groupSet);
                Rectangle2D rectAll = gsSXY.getTickLabelsBoundingBox(data);
                if (rectAll == null) {
                    continue;
                }

                if (rectAll.getWidth() < Double.MIN_VALUE
                        || rectAll.getHeight() < Double.MIN_VALUE) {
                    continue;
                }

                rectList.add(rectAll);
            }

        }

        if (rectList.size() == 0) {
            return true;
        }

        // join all rectangles
        Rectangle2D uniRect = SGUtility.createUnion(rectList);

        final float top = this.mGraphRectY - (float) uniRect.getY();
        final float bottom = -(this.mGraphRectY + this.mGraphRectHeight)
                + (float) (uniRect.getY() + uniRect.getHeight());
        final float left = this.mGraphRectX - (float) uniRect.getX();
        final float right = -(this.mGraphRectX + this.mGraphRectWidth)
                + (float) (uniRect.getX() + uniRect.getWidth());

        // set to attributes
        topAndBottom.x += top;
        topAndBottom.y += bottom;
        leftAndRight.x += left;
        leftAndRight.y += right;

        return true;

    }

    protected SGElementGroupLineInGraph getGroupLine(
            final SGElementGroupSetInGraph groupSet) {
        ArrayList groupList = groupSet.getElementGroupList();
        for (int ii = 0; ii < groupList.size(); ii++) {
            final SGElementGroup group = (SGElementGroup) groupList.get(ii);
            if (group instanceof SGElementGroupLineInGraph) {
                return (SGElementGroupLineInGraph) group;
            }
        }

        return null;
    }

    protected SGElementGroupBarInGraph getGroupBar(
            final SGElementGroupSetInGraph groupSet) {
        ArrayList groupList = groupSet.getElementGroupList();
        for (int ii = 0; ii < groupList.size(); ii++) {
            final SGElementGroup group = (SGElementGroup) groupList.get(ii);
            if (group instanceof SGElementGroupBarInGraph) {
                return (SGElementGroupBarInGraph) group;
            }
        }

        return null;
    }

    protected SGElementGroupSymbolInGraph getGroupSymbol(
            final SGElementGroupSetInGraph groupSet) {
        ArrayList groupList = groupSet.getElementGroupList();
        for (int ii = 0; ii < groupList.size(); ii++) {
            final SGElementGroup group = (SGElementGroup) groupList.get(ii);
            if (group instanceof SGElementGroupSymbolInGraph) {
                return (SGElementGroupSymbolInGraph) group;
            }
        }

        return null;
    }

    protected SGElementGroupPseudocolorMapInGraph getColorMap(
            final SGElementGroupSetInGraph groupSet) {
        ArrayList groupList = groupSet.getElementGroupList();
        for (int ii = 0; ii < groupList.size(); ii++) {
            final SGElementGroup group = (SGElementGroup) groupList.get(ii);
            if (group instanceof SGElementGroupPseudocolorMapInGraph) {
                return (SGElementGroupPseudocolorMapInGraph) group;
            }
        }
        return null;
    }

    /**
     *
     */
    public static class GraphProperties extends SGProperties {

        ArrayList visibleElementGroupList = new ArrayList();

        /**
         *
         *
         */
        public GraphProperties() {
            super();
        }

        public void dispose() {
        	super.dispose();
            this.visibleElementGroupList.clear();
            this.visibleElementGroupList = null;
        }

        /**
         *
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof GraphProperties) == false) {
                return false;
            }

            GraphProperties p = (GraphProperties) obj;

            if (p.visibleElementGroupList.equals(this.visibleElementGroupList) == false) {
                return false;
            }
            return true;
        }

    }

    protected SGIElementGroupSetSXY createSXYGroupSetInstance(SGISXYTypeSingleData dataSXY) {
        SGElementGroupSetInGraphSXY groupSet = new SGElementGroupSetInGraphSXY((SGData) dataSXY, this);
        final int num = (dataSXY != null) ? dataSXY.getPointsNumber() : 0;
        groupSet.initPointsArray(num);
        return groupSet;
    }
    protected SGIElementGroupSetVXY createVXYGroupSetInstance(SGIVXYTypeData dataVXY) {
        SGElementGroupSetInGraphVXY groupSet = new SGElementGroupSetInGraphVXY((SGData) dataVXY, this);
        final int num = dataVXY.getPointsNumber();
        groupSet.initPointsArray(num);
        return groupSet;
    }
    protected SGIElementGroupSetMultipleSXY createMultipleSXYGroupSetInstance(SGISXYTypeMultipleData dataMultSXY) {
    	SGElementGroupSetInGraphSXYMultiple groupSet = new SGElementGroupSetInGraphSXYMultiple((SGData) dataMultSXY, this);
        return groupSet;
    }
    protected SGIElementGroupSetSXYZ createSXYZGroupSetInstance(SGISXYZTypeData dataSXYZ) {
        SGElementGroupSetInGraphSXYZ groupSet = new SGElementGroupSetInGraphSXYZ(
                (SGData) dataSXYZ, this);
        final int num = dataSXYZ.getPointsNumber();
        groupSet.initPointsArray(num);
        return groupSet;
    }

    // create an instance of SXY groupSet
    protected SGIElementGroupSetSXY createSingleGroupSetSXY(
            final SGISXYTypeSingleData data, final SGAxis axisX, final SGAxis axisY,
            final boolean multiple) {

        SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) super
                .createSingleGroupSetSXY(data, axisX, axisY, multiple);
        if (groupSet == null) {
        	return null;
        }

        // set the location of the drawing elements of data
        if (data != null) {
            if (groupSet.updateDrawingElementsLocation((SGData) data) == false) {
                return null;
            }
        }

        // set magnification
        groupSet.setMagnification(this.getMagnification());

        return groupSet;
    }

    // create an instance of VXY groupSet
    protected SGIElementGroupSetVXY createSingleGroupSetVXY(
            final SGIVXYTypeData data, final SGAxis axisX, final SGAxis axisY) {

        SGElementGroupSetInGraphVXY groupSet = (SGElementGroupSetInGraphVXY) super
                .createSingleGroupSetVXY(data, axisX, axisY);
        if (groupSet == null) {
        	return null;
        }

        // set magnification
        groupSet.setMagnification(this.getMagnification());

        // set component values
        double[] fValues = data.getFirstComponentValueArray(false);
        double[] sValues = data.getSecondComponentValueArray(false);
        SGElementGroupArrowInGraph arrowGroup = (SGElementGroupArrowInGraph) groupSet.getArrowGroup();
        arrowGroup.setComponentValues(fValues, sValues);
        
        return groupSet;
    }

    // create an instance of SXYZ groupSet
    protected SGIElementGroupSetSXYZ createSingleGroupSetSXYZ(
            final SGISXYZTypeData data, final SGAxis axisX, final SGAxis axisY,
            final SGAxis axisZ) {

        SGElementGroupSetInGraphSXYZ groupSet = (SGElementGroupSetInGraphSXYZ) super
                .createSingleGroupSetSXYZ(data, axisX, axisY, axisZ);
        if (groupSet == null) {
        	return null;
        }

        // set the location of the drawing elements of data
        if (groupSet.updateDrawingElementsLocation((SGData) data) == false) {
            return null;
        }

        // set magnification
        groupSet.setMagnification(this.getMagnification());

        return groupSet;
    }

    /**
     * Returns an array of data columns of given data.
     *
     * @param data
     *            a data
     * @return
     *            an array of data columns
     */
    public SGDataColumnInfo[] getDataColumnInfoArray(SGData data) {
        SGElementGroupSetForData gs = this.getElementGroupSet(data);
        if (gs == null) {
            return null;
        }
        return gs.getDataColumnInfoArray();
    }

    /**
     * Returns information map of given data.
     *
     * @param data
     *            a data
     * @return
     *            information map
     */
    public Map<String, Object> getInfoMap(SGData data) {
        SGElementGroupSetForData gs = this.getElementGroupSet(data);
        if (gs == null) {
            return null;
        }
        return gs.getInfoMap();
    }

    /**
     * Returns the color bar model.
     *
     * @return the color bar model
     */
    public SGColorMap getColorBarModel() {
        return this.mAxisElement.getColorMap();
    }

    @Override
    public String showNetCDFLabels(SGIFigureElementString fes) {
        List<SGData> dList = this.getFocusedDataList();
        String errorName = this.showNetCDFLabels(fes, dList);
        if (null!=errorName) {
            SGUtility.showErrorMessageDialog(this.mDialogOwner,
                    errorName + ": " + "Coordinate variables with fixed values do not exist.",
                    SGIConstants.ERROR);

            return errorName;
        }
        return null;
    }

    @Override
    public String showNetCDFLabels(final SGIFigureElementString fes, final List<SGData> dataList) {
        final int step = (int) (10 * this.mMagnification);
        Rectangle2D rect = this.getGraphRect();
        final int x = (int) (rect.getX() + step);
        final int y = (int) (rect.getY() + rect.getHeight() / 2);

        String errorName = null;

        for (int ii = 0; ii< dataList.size(); ii++) {
            SGData data = dataList.get(ii);
            SGElementGroupSetForData gs = this.getElementGroupSet(data);
            if (gs.isVisible()==false) {
                continue;
            }
            DataLabel dl = gs.getDataLabel();
            if (dl != null) {
                final String text = dl.getText();
                final int offsetX = step * ii;
                final int offsetY = offsetX;
                int id = fes.addNewString(text, x + offsetX, y + offsetY);
                if (id > 0) {
                    if (gs.addLabelStringId(id, text)) {
                        gs.setChanged(true);
                    }
                }

            } else {
                errorName = gs.getName();
            }
        }
        this.repaint();

        return errorName;
    }

    /**
     * Returns changed "name=value" labels only.
     * @param oldString
     * @param newString
     * @return
     */
    private Map<String, String> compareNetCDFLabelText(final String oldString, final String newString) {
        Map<String, String> mapChanged = new HashMap<String, String>();
        Map<String, String> newMap = new HashMap<String, String>();

        String[] oldstr = oldString.split(", ");
        String[] newstr = newString.split(", ");
        for (int i = 0; i < newstr.length; i++) {
            String[] values = newstr[i].split("=");
            newMap.put(values[0], values[1]);
        }
        for (int i = 0; i < oldstr.length; i++) {
            String[] values = oldstr[i].split("=");
            String newValue = newMap.get(values[0]);
            if (null==newValue) {
                // delete
                mapChanged.put(oldstr[i], "");
            } else if (null!=newValue && newValue.equals(values[1])==false) {
                // change
                mapChanged.put(oldstr[i], values[0]+"="+newValue);
            }
        }
        StringBuffer sb = new StringBuffer();
//        String addValue = "";
        for (Entry<String, String> e : newMap.entrySet()) {
        	String key = e.getKey();
            boolean notfound = true;
            for (int i = 0; i < oldstr.length; i++) {
                String[] values = oldstr[i].split("=");
                if (key.equals(values[0])) {
                    notfound = false;
                    break;
                }
            }
            if (notfound) {
            	if (sb.length() == 0) {
            		sb.setLength(0);
            	} else {
            		sb.append(", ");
            	}
        		sb.append(key);
        		sb.append("=");
        		sb.append(newMap.get(key));
//                if (addValue.equals("")) {
//                    addValue = key+"="+newMap.get(key);
//                } else {
//                    addValue = addValue+", "+key+"="+newMap.get(key);
//                }
            }
        }
        if (sb.length() != 0) {
        	mapChanged.put("", sb.toString());
        }
//        if (addValue.equals("")==false) {
//            mapChanged.put("", addValue);
//        }

        return mapChanged;
    }

    @Override
    public void updateNetCDFLabels(final SGIFigureElementString fes) {
        List<SGIChildObject> elList = this.getVisibleChildList();
        for (int ii = 0; ii < elList.size(); ii++) {
            SGIChildObject el = elList.get(ii);
            if (el instanceof SGElementGroupSetForData) {
                SGElementGroupSetForData gs = (SGElementGroupSetForData)el;
                if (gs.mLabelStringList.size() == 0) {
                	continue;
                }
                DataLabel dl = gs.getDataLabel();
                if (null != dl) {
                    for (Entry<Integer, String> e : gs.mLabelStringList.entrySet()) {
                    	Integer i = e.getKey();
                        int id = i.intValue();
                        String oldDataLabel = gs.mLabelStringList.get(i);
                        String newDataLabel = dl.getText();
                        /*
                        Map<String, String> mapChanged = compareNetCDFLabelText(oldDataLabel, newDataLabel);
                        String current = fes.getString(id);
                        StringBuffer sb = new StringBuffer();
//                        String newStr = "";
                        for (Entry<String, String> mce : mapChanged.entrySet()) {
                        	String key = mce.getKey();
                            if (key.equals("")) {
                                continue;
                            }
                            int pos = current.indexOf(key);
                            if (pos>=0) {
                            	sb.setLength(0);
                            	sb.append(current.substring(0, pos));
                            	sb.append(mapChanged.get(key));
                            	sb.append(current.substring(pos + key.length()));
                            	current = sb.toString();
//                                newStr = current.substring(0, pos);
//                                newStr = newStr + mapChanged.get(key);
//                                newStr = newStr + current.substring(pos+key.length());
//                                current = newStr;
                            }
                        }
                        String addedValue = mapChanged.get("");
                        if (null!=addedValue && addedValue.equals("")==false) {
                        	if (sb.toString().trim().length() == 0) {
                        		sb.setLength(0);
                        	} else {
                        		sb.append(", ");
                        	}
                    		sb.append(addedValue);
//                            if (newStr.trim().equals("")) {
//                                newStr = addedValue;
//                            } else {
//                                newStr = newStr+", "+addedValue;
//                            }
                        }
                        if (fes.setString(id, sb.toString())) {
                            gs.mLabelStringList.put(i, newDataLabel);
                            fes.setChanged(true);
                        }
                        */
                        if (!oldDataLabel.equals(newDataLabel)) {
                            if (fes.setString(id, newDataLabel)) {
                                gs.mLabelStringList.put(i, newDataLabel);
                                fes.setChanged(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void hideNetCDFLabels(final SGIFigureElementString fes, final SGData data) {
        List<SGIChildObject> elList = this.getVisibleChildList();
        for (int ii = 0; ii < elList.size(); ii++) {
            SGIChildObject el = elList.get(ii);
            if (el instanceof SGElementGroupSetForData) {
                SGElementGroupSetForData gs = (SGElementGroupSetForData)el;
                if (gs.mData.equals(data)) {
                    DataLabel dl = gs.getDataLabel();
                    if (null!=dl) {
                        for (Integer i : gs.mLabelStringList.keySet()) {
                            int id = i.intValue();
                            fes.setVisible(id, false);
                        }
                        fes.setChanged(true);
                    }
                }
            }
        }
    }

    @Override
    public void hideNetCDFLabelsOfFocusedObjects(final SGIFigureElementString fes) {
    	List<SGISelectable> fList = this.getFocusedObjectsList();
        for (int ii = 0; ii < fList.size(); ii++) {
            Object obj = fList.get(ii);
            if (obj instanceof SGElementGroupSetForData) {
                SGElementGroupSetForData gs = (SGElementGroupSetForData)obj;
                if (gs.mLabelStringList.size()>0) {
                    for (Integer i : gs.mLabelStringList.keySet()) {
                        int id = i.intValue();
                        fes.setVisible(id, false);
                        fes.setChanged(true);
                    }
                }
            }
        }
    }

    private void removeHiddenNetCDFLabels(final SGIFigureElementString fes) {
        List<SGISelectable> sList = fes.getFocusedObjectsList();

        List<SGIChildObject> elList = this.getVisibleChildList();
        for (int ii = 0; ii < elList.size(); ii++) {
            SGIChildObject el = elList.get(ii);
            if (el instanceof SGElementGroupSetForData) {
                SGElementGroupSetForData gs = (SGElementGroupSetForData)el;
                Set<Integer> keys = new HashSet<Integer>(gs.mLabelStringList.keySet());
                for (Integer i : keys) {
                    for (int j = 0; j < sList.size(); j++) {
                        Object obj = sList.get(j);
                        if (obj instanceof SGIChildObject) {
                            if (((SGIChildObject)obj).getID()==i.intValue()) {
                                gs.mLabelStringList.remove(i);
                                gs.setChanged(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isBarVisible() {
        List<SGIChildObject> childList = this.getVisibleChildList();
        for (int i = 0; i < childList.size(); i++) {
            SGIChildObject child = childList.get(i);
            if (child instanceof SGIElementGroupSetXY) {
                SGElementGroupBar bar = ((SGIElementGroupSetXY)child).getBarGroup();
                if (null==bar) {
                    continue;
                }
                if (bar.isVisible()) {
                    return true;
                }
                List<SGElementGroupBar> bars = ((SGIElementGroupSetXY)child).getBarGroups();
                for (int j = 0; j < bars.size(); j++) {
                    if (bars.get(j).isVisible()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    double getMinimumBarWithInVisibleBars() {
        double minBarWidth = Double.MAX_VALUE;

        List<SGIChildObject> childList = this.getVisibleChildList();
        for (int i = 0; i < childList.size(); i++) {
            SGIChildObject child = childList.get(i);
            if (child instanceof SGIElementGroupSetXY) {
                double barWidth = getBarWidthOfBarSeries((SGIElementGroupSetXY)child);
                if (!Double.isNaN(barWidth) && minBarWidth > barWidth) {
                    minBarWidth = barWidth;
                }
            }
        }

        return minBarWidth;
    }

    @Override
    public boolean alignVisibleBars() {

        double minBarWidth = Double.MAX_VALUE;
        List<Integer> numberOfBarSeriesList = new ArrayList<Integer>();

        List<SGIChildObject> childList = this.getVisibleChildList();
        for (int i = 0; i < childList.size(); i++) {
            SGIChildObject child = childList.get(i);
            if (child instanceof SGIElementGroupSetXY) {
                int numberOfBarSeries = getNumberOfVisibleBarSeries((SGIElementGroupSetXY)child);
                numberOfBarSeriesList.add(Integer.valueOf(numberOfBarSeries));
                double barWidth = getBarWidthOfBarSeries((SGIElementGroupSetXY)child);
                if (!Double.isNaN(barWidth) && minBarWidth > barWidth) {
                    minBarWidth = barWidth;
                }
            } else {
                numberOfBarSeriesList.add(Integer.valueOf(0));
            }
        }

        int sumNumberOfBars = 0;
        for (int i = 0; i < numberOfBarSeriesList.size(); i++) {
            sumNumberOfBars += numberOfBarSeriesList.get(i).intValue();
        }
        if (sumNumberOfBars==0) {
            return false;
        }
        double barWidth = minBarWidth / (sumNumberOfBars+1);
        double barInterval = barWidth;

        boolean changed = false;

        for (int i = 0; i < childList.size(); i++) {
            SGIChildObject child = childList.get(i);
            boolean childChanged = false;
            if (child instanceof SGIElementGroupSetXY) {
                int numberOfBarsInChild = numberOfBarSeriesList.get(i).intValue();
                int numberOfPreviousBars = 0;
                for (int j = 0; j < i; j++) {
                    numberOfPreviousBars += numberOfBarSeriesList.get(j).intValue();
                }
                double shiftBar = -sumNumberOfBars*0.5+numberOfBarsInChild*0.5+numberOfPreviousBars;
                double shift = barWidth*shiftBar;

                SGISXYDataDialogObserver obs = null;
                boolean vertical = true;
                SGAxis axis = null;
                if (child instanceof SGElementGroupSetInGraphSXY) {
                    SGElementGroupSetInGraphSXY egs = (SGElementGroupSetInGraphSXY)child;
                    obs = (SGISXYDataDialogObserver)egs;
                    vertical = obs.isBarVertical();
                    if (vertical) {
                        axis = egs.getXAxis();
                    } else {
                        axis = egs.getYAxis();
                    }
                } else if (child instanceof SGElementGroupSetInGraphSXYMultiple) {
                	SGElementGroupSetInGraphSXYMultiple egs = (SGElementGroupSetInGraphSXYMultiple)child;
                    obs = (SGISXYDataDialogObserver)egs;
                    vertical = obs.isBarVertical();
                    if (vertical) {
                        axis = egs.getXAxis();
                    } else {
                        axis = egs.getYAxis();
                    }
                } else {
                    continue;
                }
                if (null!=axis) {
                    barWidth = SGUtilityNumber.getNumberInRangeOrder(barWidth, axis,
                            SGIBarConstants.BAR_EFFECTIVE_DIGIT, BigDecimal.ROUND_HALF_UP);
                    barInterval = SGUtilityNumber.getNumberInRangeOrder(barInterval, axis,
                            SGIBarConstants.BAR_EFFECTIVE_DIGIT, BigDecimal.ROUND_HALF_UP);
                    shift = SGUtilityNumber.getNumberInRangeOrder(shift, axis,
                            SGIBarConstants.BAR_EFFECTIVE_DIGIT, BigDecimal.ROUND_HALF_UP);
                } else {
                    continue;
                }
                if (null!=obs) {
                    double currentBarWidth = obs.getBarWidthValue();
                    double currentBarInterval = obs.getBarInterval();
                    double currentBarShiftX = obs.getBarOffsetX();
                    double currentBarShiftY = obs.getBarOffsetY();

                    if (currentBarWidth!=barWidth) {
                        obs.setBarWidthValue(barWidth);
                        childChanged = true;
                        changed = true;
                    }
                    if (currentBarInterval!=barInterval) {
                        obs.setBarInterval(barInterval);
                        childChanged = true;
                        changed = true;
                    }
                    if (vertical) {
                        if (currentBarShiftX!=shift) {
                            obs.setBarOffsetX(shift);
                            childChanged = true;
                            changed = true;
                        }
                    } else {
                        if (currentBarShiftY!=shift) {
                            obs.setBarOffsetY(shift);
                            childChanged = true;
                            changed = true;
                        }
                    }
                    if (childChanged) {
                        ((SGElementGroupSetInGraph)child).setChanged(true);
                    }
                }
            }
        }

        if (changed) {
            this.updateAllDrawingElementsLocation();
            this.notifyChange();
            this.setChanged(true);
        }

        return true;
    }

    /**
     * Get the number of bar series in a given groupSet.
     *
     * @param gs
     * @return
     */
    int getNumberOfVisibleBarSeries(SGIElementGroupSetXY gs) {
        if (gs instanceof SGElementGroupSetInGraphSXY) {
            SGElementGroupSetInGraphSXY egs = (SGElementGroupSetInGraphSXY)gs;
            if (egs.isBarVisible()) {
                return 1;
            }
        } else if (gs instanceof SGElementGroupSetInGraphSXYMultiple) {
        	SGElementGroupSetInGraphSXYMultiple egs = (SGElementGroupSetInGraphSXYMultiple) gs;
            if (egs.isBarVisible()) {
                List<SGElementGroupBar> barlist = egs.getBarGroupsIgnoreNull();
                return barlist.size()-1;
            }
        }
        return 0;
    }
    
    /**
     * Called when the mouse pointer moves.
     * 
     * @param e
     *          the mouse event
     * @return true if the mouse pointer passes over some objects
     */
    @Override
    public boolean onMouseMoved(MouseEvent e) {
    	for (int ii = this.mChildList.size() - 1; ii >= 0; ii--) {
            final SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) this.mChildList.get(ii);
            if (groupSet.isVisible() == false) {
                continue;
            }
            if (groupSet.onMouseMoved(e)) {
            	return true;
            }
    	}
    	return false;
    }

    @Override
    public String getAxisValueString(final int x, final int y) {
    	String ret = "";
    	for (int ii = this.mChildList.size() - 1; ii >= 0; ii--) {
            final SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) this.mChildList.get(ii);
            if (groupSet.isVisible() == false) {
                continue;
            }
            String str = groupSet.getAxisValueString(x, y);
            if (str != null) {
            	ret = str;
            	break;
            }
    	}
    	return ret;
    }
    
    void putDataTableCellList(SGIData data, List<SGTwoDimensionalArrayIndex> cellList) {
		SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) this.getElementGroupSet((SGData) data);
		if (gs != null) {
			gs.addDataTableCellList(cellList);
		}
    }
    
    Map<SGIData, List<SGTwoDimensionalArrayIndex>> getDataTableCellListMap() {
    	Map<SGIData, List<SGTwoDimensionalArrayIndex>> map = new HashMap<SGIData, List<SGTwoDimensionalArrayIndex>>();    	
    	for (int ii = 0; ii < this.mChildList.size(); ii++) {
    		SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) this.mChildList.get(ii);
    		map.put(gs.getData(), gs.getDataTableCellList());
    	}
    	return map;
    }
    
    void clearTableCellListMap() {
    	for (int ii = 0; ii < this.mChildList.size(); ii++) {
    		SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) this.mChildList.get(ii);
    		gs.clearDataTableCellList();
    	}
    }

    @Override
    public List<SGTwoDimensionalArrayIndex> getSelectedDataIndexList(SGIData data,
    		String columnType) {
    	SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph) this.getElementGroupSet((SGData) data);
    	if (gs == null) {
    		return new ArrayList<SGTwoDimensionalArrayIndex>();
    	}
    	return gs.getDataTableCellList();
    }

}

package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureConstants;
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
import jp.riken.brain.ni.samuraigraph.base.SGIProgressControl;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYZTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGIVXYTypeData;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureTypeConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGXYFigure;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 */
public class SGFigureCreator implements SGIFigureConstants {

    // 2D class names
    private static final String CLASS_NAME_OF_AXIS_ELEMENT_2D = "jp.riken.brain.ni.samuraigraph.figure.java2d.SGFigureElementAxis";

    // private static final String CLASS_NAME_OF_SXY_GRAPH_ELEMENT_2D
    // = "jp.riken.brain.ni.samuraigraph.figure.java2d.SGFigureElementGraphSXY";
    // private static final String CLASS_NAME_OF_VXY_GRAPH_ELEMENT_2D
    // = "jp.riken.brain.ni.samuraigraph.figure.java2d.SGFigureElementGraphVXY";
    private static final String CLASS_NAME_OF_GRAPH_ELEMENT_2D = "jp.riken.brain.ni.samuraigraph.figure.java2d.SGFigureElementGraph";

    private static final String CLASS_NAME_OF_GRID_ELEMENT_2D = "jp.riken.brain.ni.samuraigraph.figure.java2d.SGFigureElementGrid";

    private static final String CLASS_NAME_OF_STRING_ELEMENT_2D = "jp.riken.brain.ni.samuraigraph.figure.java2d.SGFigureElementString";

    private static final String CLASS_NAME_OF_LEGEND_ELEMENT_2D = "jp.riken.brain.ni.samuraigraph.figure.java2d.SGFigureElementLegend";

    private static final String CLASS_NAME_OF_AXIS_BREAK_ELEMENT_2D = "jp.riken.brain.ni.samuraigraph.figure.java2d.SGFigureElementAxisBreak";

    private static final String CLASS_NAME_OF_SIGNIFICANT_DIFFERENCE_ELEMENT_2D = "jp.riken.brain.ni.samuraigraph.figure.java2d.SGFigureElementSignificantDifference";

    private static final String CLASS_NAME_OF_TIMING_LINE_ELEMENT_2D = "jp.riken.brain.ni.samuraigraph.figure.java2d.SGFigureElementTimingLine";

    private static final String CLASS_NAME_OF_SHAPE_ELEMENT_2D = "jp.riken.brain.ni.samuraigraph.figure.java2d.SGFigureElementShape";

    // 3D class names - not implemented now
    private static final String CLASS_NAME_OF_AXIS_ELEMENT_3D = "jp.riken.brain.ni.samuraigraph.figure.java3d.SGFigureElementAxis";

    // private static final String CLASS_NAME_OF_SXY_GRAPH_ELEMENT_3D
    // = "jp.riken.brain.ni.samuraigraph.figure.java3d.SGFigureElementGraphSXY";
    // private static final String CLASS_NAME_OF_VXY_GRAPH_ELEMENT_3D
    // = "jp.riken.brain.ni.samuraigraph.figure.java3d.SGFigureElementGraphVXY";
    private static final String CLASS_NAME_OF_GRAPH_ELEMENT_3D = "jp.riken.brain.ni.samuraigraph.figure.java3d.SGFigureElementGraph";

    private static final String CLASS_NAME_OF_GRID_ELEMENT_3D = "jp.riken.brain.ni.samuraigraph.figure.java3d.SGFigureElementGrid";

    private static final String CLASS_NAME_OF_STRING_ELEMENT_3D = "jp.riken.brain.ni.samuraigraph.figure.java3d.SGFigureElementString";

    private static final String CLASS_NAME_OF_LEGEND_ELEMENT_3D = "jp.riken.brain.ni.samuraigraph.figure.java3d.SGFigureElementLegend";

    private static final String CLASS_NAME_OF_AXIS_BREAK_ELEMENT_3D = "jp.riken.brain.ni.samuraigraph.figure.java3d.SGFigureElementAxisBreak";

    private static final String CLASS_NAME_OF_SIGNIFICANT_DIFFERENCE_ELEMENT_3D = "jp.riken.brain.ni.samuraigraph.figure.java3d.SGFigureElementSignificantDifference";

    private static final String CLASS_NAME_OF_TIMING_LINE_ELEMENT_3D = "jp.riken.brain.ni.samuraigraph.figure.java3d.SGFigureElementTimingLinet";

    private static final String CLASS_NAME_OF_SHAPE_ELEMENT_3D = "jp.riken.brain.ni.samuraigraph.figure.java3d.SGFigureElementShape";

    // property names for loadClassesFromFile()
    private static final String PROPERTY_NAME_OF_AXIS_ELEMENT = "SGFigureElementAxis";

    // private static final String PROPERTY_NAME_OF_SXY_GRAPH_ELEMENT
    // = "SGFigureElementGraphSXY";
    // private static final String PROPERTY_NAME_OF_VXY_GRAPH_ELEMENT
    // = "SGFigureElementGraphVXY";
    private static final String PROPERTY_NAME_OF_GRAPH_ELEMENT = "SGFigureElementGraph";

    private static final String PROPERTY_NAME_OF_GRID_ELEMENT = "SGFigureElementGrid";

    private static final String PROPERTY_NAME_OF_STRING_ELEMENT = "SGFigureElementString";

    private static final String PROPERTY_NAME_OF_LEGEND_ELEMENT = "SGFigureElementLegend";

    private static final String PROPERTY_NAME_OF_AXIS_BREAK_ELEMENT = "SGFigureElementAxisBreak";

    private static final String PROPERTY_NAME_OF_SIGNIFICANT_DIFFERENCE_ELEMENT = "SGSignificantDifferenceElement";

    private static final String PROPERTY_NAME_OF_TIMING_LINE_ELEMENT = "SGFigureElementTimingLinet";

    private static final String PROPERTY_NAME_OF_SHAPE_ELEMENT = "SGFigureElementShape";

    // The health check flag for class dynamic loading.
    private boolean mSuccess = false;

    // The class object for SGIAxisElement.
    private Class mClassOfAxisElement = null;

    // // The class object for SGIGraphElement of the scalar XY-type.
    // private Class mClassOfSXYGraphElement = null;
    //
    // // The class object for SGIGraphElement of the vector XY-type.
    // private Class mClassOfVXYGraphElement = null;

    // The class object for SGIGraphElement.
    private Class mClassOfGraphElement = null;

    // The class object for SGIGridElement.
    private Class mClassOfGridElement = null;

    // The class object for SGIStringElement.
    private Class mClassOfStringElement = null;

    // The class object for SGILegendElement.
    private Class mClassOfLegendElement = null;

    // The class object for SGIAxisBreakElement.
    private Class mClassOfAxisBreakElement = null;

    // The class object for SGISignificantDifferenceElement.
    private Class mClassOfSignificantDifferenceElement = null;

    // The class object for SGITimingLineElement.
    private Class mClassOfTimingLineElement = null;

    // The class object for SGIShapeElement.
    private Class mClassOfShapeElement = null;

    /**
     * Constants of layer.
     */
    private static final int LAYER_GRID = 10;

    private static final int LAYER_GRAPH = 20;

    private static final int LAYER_AXIS = 30;

    private static final int LAYER_TIMING_LINE = 40;

    private static final int LAYER_SIGNIFICANT_DIFFERENCE = 50;

    private static final int LAYER_SHAPE = 60;

    private static final int LAYER_AXIS_BREAK = 70;

    private static final int LAYER_LEGEND = 80;

    private static final int LAYER_STRING = 90;

    //
    public SGFigureCreator(boolean is2d) {
        if (is2d)
            this.mSuccess = load2DClasses();
        else
            this.mSuccess = load3DClasses();
        if (this.mSuccess)
            this.mSuccess = healthCheck();
    }

    public SGFigureCreator(final String path) {
        this.mSuccess = loadClassesFromFile(path);
        if (this.mSuccess)
            this.mSuccess = healthCheck();
    }

    // load 2d classes
    private boolean load2DClasses() {
        try {
            this.mClassOfAxisElement = Class
                    .forName(CLASS_NAME_OF_AXIS_ELEMENT_2D);
            // this.mClassOfSXYGraphElement =
            // Class.forName(CLASS_NAME_OF_SXY_GRAPH_ELEMENT_2D);
            // this.mClassOfVXYGraphElement =
            // Class.forName(CLASS_NAME_OF_VXY_GRAPH_ELEMENT_2D);
            this.mClassOfGraphElement = Class
                    .forName(CLASS_NAME_OF_GRAPH_ELEMENT_2D);
            this.mClassOfGridElement = Class
                    .forName(CLASS_NAME_OF_GRID_ELEMENT_2D);
            this.mClassOfStringElement = Class
                    .forName(CLASS_NAME_OF_STRING_ELEMENT_2D);
            this.mClassOfLegendElement = Class
                    .forName(CLASS_NAME_OF_LEGEND_ELEMENT_2D);
            this.mClassOfAxisBreakElement = Class
                    .forName(CLASS_NAME_OF_AXIS_BREAK_ELEMENT_2D);
            this.mClassOfSignificantDifferenceElement = Class
                    .forName(CLASS_NAME_OF_SIGNIFICANT_DIFFERENCE_ELEMENT_2D);
            this.mClassOfTimingLineElement = Class
                    .forName(CLASS_NAME_OF_TIMING_LINE_ELEMENT_2D);
            this.mClassOfShapeElement = Class
                    .forName(CLASS_NAME_OF_SHAPE_ELEMENT_2D);
        } catch (ClassNotFoundException ex) {
            return false;
        }
        return true;
    }

    // load 3d classes
    private boolean load3DClasses() {
        try {
            this.mClassOfAxisElement = Class
                    .forName(CLASS_NAME_OF_AXIS_ELEMENT_3D);
            // this.mClassOfSXYGraphElement =
            // Class.forName(CLASS_NAME_OF_SXY_GRAPH_ELEMENT_3D);
            // this.mClassOfVXYGraphElement =
            // Class.forName(CLASS_NAME_OF_VXY_GRAPH_ELEMENT_3D);
            this.mClassOfGraphElement = Class
                    .forName(CLASS_NAME_OF_GRAPH_ELEMENT_3D);
            this.mClassOfGridElement = Class
                    .forName(CLASS_NAME_OF_GRID_ELEMENT_3D);
            this.mClassOfStringElement = Class
                    .forName(CLASS_NAME_OF_STRING_ELEMENT_3D);
            this.mClassOfLegendElement = Class
                    .forName(CLASS_NAME_OF_LEGEND_ELEMENT_3D);
            this.mClassOfAxisBreakElement = Class
                    .forName(CLASS_NAME_OF_AXIS_BREAK_ELEMENT_3D);
            this.mClassOfSignificantDifferenceElement = Class
                    .forName(CLASS_NAME_OF_SIGNIFICANT_DIFFERENCE_ELEMENT_3D);
            this.mClassOfTimingLineElement = Class
                    .forName(CLASS_NAME_OF_TIMING_LINE_ELEMENT_3D);
            this.mClassOfShapeElement = Class
                    .forName(CLASS_NAME_OF_SHAPE_ELEMENT_3D);
        } catch (ClassNotFoundException ex) {
            return false;
        }
        return true;
    }

    // load classes from property file
    private boolean loadClassesFromFile(final String path) {
        // open the file
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(path));
        } catch (FileNotFoundException ex) {
            return false;
        }

        try {
            // load properties
            Properties p = new Properties();
            p.load(fis);

            String name;
            name = p.getProperty(PROPERTY_NAME_OF_AXIS_ELEMENT);
            this.mClassOfAxisElement = (name != null && !name.equals("")) ? Class
                    .forName(name)
                    : null;
            // name = p.getProperty(PROPERTY_NAME_OF_SXY_GRAPH_ELEMENT);
            // this.mClassOfSXYGraphElement = (name != null && !name.equals(""))
            // ? Class.forName(name) : null;
            // name = p.getProperty(PROPERTY_NAME_OF_VXY_GRAPH_ELEMENT);
            // this.mClassOfVXYGraphElement = (name != null && !name.equals(""))
            // ? Class.forName(name) : null;
            name = p.getProperty(PROPERTY_NAME_OF_GRAPH_ELEMENT);
            this.mClassOfGraphElement = (name != null && !name.equals("")) ? Class
                    .forName(name)
                    : null;
            name = p.getProperty(PROPERTY_NAME_OF_GRID_ELEMENT);
            this.mClassOfGridElement = (name != null && !name.equals("")) ? Class
                    .forName(name)
                    : null;
            name = p.getProperty(PROPERTY_NAME_OF_STRING_ELEMENT);
            this.mClassOfStringElement = (name != null && !name.equals("")) ? Class
                    .forName(name)
                    : null;
            name = p.getProperty(PROPERTY_NAME_OF_LEGEND_ELEMENT);
            this.mClassOfLegendElement = (name != null && !name.equals("")) ? Class
                    .forName(name)
                    : null;
            name = p.getProperty(PROPERTY_NAME_OF_AXIS_BREAK_ELEMENT);
            this.mClassOfAxisBreakElement = (name != null && !name.equals("")) ? Class
                    .forName(name)
                    : null;
            name = p
                    .getProperty(PROPERTY_NAME_OF_SIGNIFICANT_DIFFERENCE_ELEMENT);
            this.mClassOfSignificantDifferenceElement = (name != null && !name
                    .equals("")) ? Class.forName(name) : null;
            name = p.getProperty(PROPERTY_NAME_OF_TIMING_LINE_ELEMENT);
            this.mClassOfTimingLineElement = (name != null && !name.equals("")) ? Class
                    .forName(name)
                    : null;
            name = p.getProperty(PROPERTY_NAME_OF_SHAPE_ELEMENT);
            this.mClassOfShapeElement = (name != null && !name.equals("")) ? Class
                    .forName(name)
                    : null;

        } catch (ClassNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                }
            }
        }

        return true;
    }

    // health check for loaded classes
    private boolean healthCheck() {
        if (!SGIFigureElementAxis.class
                .isAssignableFrom(this.mClassOfAxisElement))
            return false;
        // if( !SGIFigureElementGraph.class.isAssignableFrom(
        // this.mClassOfSXYGraphElement ) )
        // return false;
        // if( !SGIFigureElementGraph.class.isAssignableFrom(
        // this.mClassOfVXYGraphElement ) )
        // return false;
        if (!SGIFigureElementGraph.class
                .isAssignableFrom(this.mClassOfGraphElement))
            return false;
        if (!SGIFigureElementGrid.class
                .isAssignableFrom(this.mClassOfGridElement))
            return false;
        if (!SGIFigureElementString.class
                .isAssignableFrom(this.mClassOfStringElement))
            return false;
        if (!SGIFigureElementLegend.class
                .isAssignableFrom(this.mClassOfLegendElement))
            return false;
        if (!SGIFigureElementAxisBreak.class
                .isAssignableFrom(this.mClassOfAxisBreakElement))
            return false;
        if (!SGIFigureElementSignificantDifference.class
                .isAssignableFrom(this.mClassOfSignificantDifferenceElement))
            return false;
        if (!SGIFigureElementTimingLine.class
                .isAssignableFrom(this.mClassOfTimingLineElement))
            return false;
        if (!SGIFigureElementShape.class
                .isAssignableFrom(this.mClassOfShapeElement))
            return false;

        return true;
    }

    // validate class
    public boolean validateClasses() {
        return this.mSuccess;
    }

    private void setRelationOfFigureElements(final SGFigure figure) {
        // class SGFigure
        SGIFigureElementAxis axis = figure.getAxisElement();
        SGIFigureElementGraph graph = figure.getGraphElement();
        SGIFigureElementLegend legend = figure.getLegendElement();
        graph.setAxisElement(axis);
        legend.setAxisElement(axis);
        legend.setGraphElement(graph);

        if (figure instanceof SGXYFigure) {
            final SGXYFigure fig = (SGXYFigure) figure;
            // class SGXYFigure
            SGIFigureElementGrid grid = fig.getGridElement();
            // additional symbols
            SGIFigureElementString string = (SGIFigureElementString) fig
                    .getSymbolElement(SGIDrawingElementConstants.SYMBOL_ELEMENT_TYPE_STRING);
            SGIFigureElementAxisBreak axisBreak = (SGIFigureElementAxisBreak) fig
                    .getSymbolElement(SGIDrawingElementConstants.SYMBOL_ELEMENT_TYPE_AXISBREAK);
            SGIFigureElementSignificantDifference sigDiff = (SGIFigureElementSignificantDifference) fig
                    .getSymbolElement(SGIDrawingElementConstants.SYMBOL_ELEMENT_TYPE_SIGDIFF);
            SGIFigureElementTimingLine timingLine = (SGIFigureElementTimingLine) fig
                    .getSymbolElement(SGIDrawingElementConstants.SYMBOL_ELEMENT_TYPE_TIMINGLINE);
            SGIFigureElementShape shape = (SGIFigureElementShape) fig
                    .getSymbolElement(SGIDrawingElementConstants.SYMBOL_ELEMENT_TYPE_SHAPE);

            grid.setAxisElement(axis);

            axisBreak.setAxisElement(axis);
            sigDiff.setAxisElement(axis);
            timingLine.setAxisElement(axis);
            string.setAxisElement(axis);
            shape.setAxisElement(axis);
        }
    }

    /**
     *
     */
    private boolean createFigureElements(final SGFigure figure,
            final SGIProgressControl progress) {
        // dialog owner
        SGDrawingWindow wnd = figure.getWindow();

        // create a SGAxisElement
        SGIFigureElementAxis axisElement = (SGIFigureElementAxis) this
                .setIElement(this.mClassOfAxisElement, figure, wnd);

        progress.setProgressValue(0.05f);

        SGIFigureElementGraph graphElement = (SGIFigureElementGraph) this
                .setIElement(this.mClassOfGraphElement, figure, wnd);

        progress.setProgressValue(0.1f);

        // create a SGLegendElement
        SGIFigureElementLegend legendElement = (SGIFigureElementLegend) this
                .setIElement(this.mClassOfLegendElement, figure, wnd);

        progress.setProgressValue(0.15f);

        // create a SGStringElement
        SGIFigureElementString stringElement = (SGIFigureElementString) this
                .setIElement(this.mClassOfStringElement, figure, wnd);

        progress.setProgressValue(0.2f);

        // SGAxisBreakElement
        SGIFigureElementAxisBreak axisBreakElement = (SGIFigureElementAxisBreak) this
                .setIElement(this.mClassOfAxisBreakElement, figure, wnd);

        progress.setProgressValue(0.25f);

        // SGSignificantDifferenceElement
        SGIFigureElementSignificantDifference sigDiffElement = (SGIFigureElementSignificantDifference) this
                .setIElement(this.mClassOfSignificantDifferenceElement, figure,
                        wnd);

        progress.setProgressValue(0.3f);

        // SGTimingLineElement
        SGIFigureElementTimingLine timingLineElement = (SGIFigureElementTimingLine) this
                .setIElement(this.mClassOfTimingLineElement, figure, wnd);

        progress.setProgressValue(0.35f);

        // SGGridElement
        SGIFigureElementGrid gridElement = (SGIFigureElementGrid) this
                .setIElement(this.mClassOfGridElement, figure, wnd);

        progress.setProgressValue(0.4f);

        // SGShapeElement
        SGIFigureElementShape shapeElement = (SGIFigureElementShape) this
                .setIElement(this.mClassOfShapeElement, figure, wnd);

        progress.setProgressValue(0.45f);

        // set to figure
        figure.setIFigureElement(LAYER_GRID, gridElement);
        figure.setIFigureElement(LAYER_TIMING_LINE, timingLineElement);
        figure.setIFigureElement(LAYER_GRAPH, graphElement);
        figure.setIFigureElement(LAYER_AXIS, axisElement);
        figure.setIFigureElement(LAYER_SIGNIFICANT_DIFFERENCE, sigDiffElement);
        figure.setIFigureElement(LAYER_SHAPE, shapeElement);
        figure.setIFigureElement(LAYER_AXIS_BREAK, axisBreakElement);
        figure.setIFigureElement(LAYER_LEGEND, legendElement);
        figure.setIFigureElement(LAYER_STRING, stringElement);

        // set relation between SGIFigureElement objects
        this.setRelationOfFigureElements(figure);

        progress.setProgressValue(0.5f);

        return true;
    }

    // create SGIFigureElement object
    private SGIFigureElement setIElement(final Class<?> cl, final SGFigure figure,
            final SGDrawingWindow wnd) {
        Object obj = null;
        try {
            obj = cl.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        SGIFigureElement element = (SGIFigureElement) obj;
        element.setWindow(wnd);
        element.setComponent(figure.getComponent());
        element.addActionListener(figure);
        element.setDialogOwner(wnd);

        return element;
    }

    /**
     *
     * @param figure
     * @param element
     * @return
     */
    public int createFigureElementFromPropertyFile(final SGFigure figure,
            final Element fElement, final String versionNumber) {
        NodeList nList = null;
        Element element = null;
        SGIFigureElement el = null;

        SGDrawingWindow wnd = figure.getWindow();
        final int ic = SGIConstants.PROPERTY_FILE_INCORRECT;
        Rectangle2D gRect = figure.getGraphRect();

        // axis
        el = this.setIElement(this.mClassOfAxisElement, figure, wnd);
        if (el == null) {
            return ic;
        }
        el.setGraphRect(gRect);
        SGIFigureElementAxis axisElement = (SGIFigureElementAxis) el;
        figure.setIFigureElement(LAYER_AXIS, axisElement);
        nList = fElement
                .getElementsByTagName(SGIFigureElementAxis.TAG_NAME_AXES);
        element = (Element) nList.item(0);
        if (el.readProperty(element, versionNumber) == false) {
            return ic;
        }

        el = this.setIElement(this.mClassOfGraphElement, figure, wnd);
        if (el == null) {
            return ic;
        }
        el.setGraphRect(gRect);
        SGIFigureElementGraph graphElement = (SGIFigureElementGraph) el;
        figure.setIFigureElement(LAYER_GRAPH, graphElement);

        // legend
        el = this.setIElement(this.mClassOfLegendElement, figure, wnd);
        if (el == null) {
            return ic;
        }
        el.setGraphRect(gRect);
        SGIFigureElementLegend legendElement = (SGIFigureElementLegend) el;
        figure.setIFigureElement(LAYER_LEGEND, legendElement);
        legendElement.setAxisElement(axisElement); // set in advance
        nList = fElement
                .getElementsByTagName(SGIFigureElementLegend.TAG_NAME_LEGEND);
        if (nList.getLength() != 0) {
            element = (Element) nList.item(0);
            if (el.readProperty(element, versionNumber) == false) {
                return ic;
            }
        }

        // string
        el = this.setIElement(this.mClassOfStringElement, figure, wnd);
        if (el == null) {
            return ic;
        }
        el.setGraphRect(gRect);
        SGIFigureElementString stringElement = (SGIFigureElementString) el;
        figure.setIFigureElement(LAYER_STRING, stringElement);
        stringElement.setAxisElement(axisElement); // set in advance
        nList = fElement
                .getElementsByTagName(SGIFigureElementString.TAG_NAME_STRING_ELEMENT);
        if (nList.getLength() != 0) {
            element = (Element) nList.item(0);
            if (el.readProperty(element, versionNumber) == false) {
                return ic;
            }
        }

        // axis break
        el = this.setIElement(this.mClassOfAxisBreakElement, figure, wnd);
        if (el == null) {
            return ic;
        }
        el.setGraphRect(gRect);
        SGIFigureElementAxisBreak axisBreakElement = (SGIFigureElementAxisBreak) el;
        figure.setIFigureElement(LAYER_AXIS_BREAK, axisBreakElement);
        axisBreakElement.setAxisElement(axisElement); // set in advance
        nList = fElement
                .getElementsByTagName(SGIFigureElementAxisBreak.TAG_NAME_AXIS_BREAK);
        if (nList.getLength() != 0) {
            element = (Element) nList.item(0);
            if (el.readProperty(element, versionNumber) == false) {
                return ic;
            }
        }

        // significant difference
        el = this.setIElement(this.mClassOfSignificantDifferenceElement,
                figure, wnd);
        if (el == null) {
            return ic;
        }
        el.setGraphRect(gRect);
        SGIFigureElementSignificantDifference sigDiffElement = (SGIFigureElementSignificantDifference) el;
        figure.setIFigureElement(LAYER_SIGNIFICANT_DIFFERENCE, sigDiffElement);
        sigDiffElement.setAxisElement(axisElement); // set in advance
        nList = fElement
                .getElementsByTagName(SGIFigureElementSignificantDifference.TAG_NAME_SIGNIFICANT_DIFFERENCE);
        if (nList.getLength() != 0) {
            element = (Element) nList.item(0);
            if (el.readProperty(element, versionNumber) == false) {
                return ic;
            }
        }

        // timing line
        el = this.setIElement(this.mClassOfTimingLineElement, figure, wnd);
        if (el == null) {
            return ic;
        }
        el.setGraphRect(gRect);
        SGIFigureElementTimingLine timingLineElement = (SGIFigureElementTimingLine) el;
        figure.setIFigureElement(LAYER_TIMING_LINE, timingLineElement);
        timingLineElement.setAxisElement(axisElement); // set in advance
        nList = fElement
                .getElementsByTagName(SGIFigureElementTimingLine.TAG_NAME_TIMING_LINES);
        if (nList.getLength() != 0) {
            element = (Element) nList.item(0);
            if (el.readProperty(element, versionNumber) == false) {
                return ic;
            }
        }

        // grid - Added from version 0.5.1
        el = this.setIElement(this.mClassOfGridElement, figure, wnd);
        if (el == null) {
            return ic;
        }
        el.setGraphRect(gRect);
        SGIFigureElementGrid gridElement = (SGIFigureElementGrid) el;
        figure.setIFigureElement(LAYER_GRID, gridElement);
        gridElement.setAxisElement(axisElement); // set in advance
        nList = fElement
                .getElementsByTagName(SGIFigureElementGrid.TAG_NAME_GRID_ELEMENT);
        if (nList.getLength() != 0) {
            element = (Element) nList.item(0);
            if (el.readProperty(element, versionNumber) == false)
                return ic;
        } else {
            // initialize compatible properties
            if (el.initCompatibleProperty() == false)
                return ic;
        }

        // shape - Added from version 0.8.0
        el = this.setIElement(this.mClassOfShapeElement, figure, wnd);
        SGIFigureElementShape shapeElement = (SGIFigureElementShape) el;
        if (el == null) {
            return ic;
        }
        el.setGraphRect(gRect);
        figure.setIFigureElement(LAYER_SHAPE, shapeElement);
        shapeElement.setAxisElement(axisElement); // set in advance
        nList = fElement
                .getElementsByTagName(SGIFigureElementShape.TAG_NAME_SHAPE);
        if (nList.getLength() != 0) {
            element = (Element) nList.item(0);
            if (el.readProperty(element, versionNumber) == false) {
                return ic;
            }
        }

        // set relation between SGIFigureElement objects
        setRelationOfFigureElements(figure);

        return SGIConstants.SUCCESSFUL_COMPLETION;
    }

    /**
     * Default width of a figure.
     */
    public static final float FIGURE_DEFAULT_WIDTH = 10.0f;

    /**
     * Default height of a figure.
     */
    public static final float FIGURE_DEFAULT_HEIGHT = 10.0f;

    /**
     * Default width of a figure for scalar XY data.
     */
    public static final float FIGURE_SXY_DEFAULT_WIDTH = 15.0f;

    /**
     * Default height of a figure for scalar XY data.
     */
    public static final float FIGURE_SXY_DEFAULT_HEIGHT = 9.0f;

    /**
     * Default width of a figure for vector XY data.
     */
    public static final float FIGURE_VXY_DEFAULT_WIDTH = 9.0f;

    /**
     * Default height of a figure for vector XY data.
     */
    public static final float FIGURE_VXY_DEFAULT_HEIGHT = FIGURE_VXY_DEFAULT_WIDTH;

    /**
     * Default width of a figure for scalar XYZ data.
     */
    public static final float FIGURE_SXYZ_DEFAULT_WIDTH = 10.0f;

    /**
     * Default height of a figure for scalar XYZ data.
     */
    public static final float FIGURE_SXYZ_DEFAULT_HEIGHT = FIGURE_SXYZ_DEFAULT_WIDTH;

	/**
	 * Returns the default size of a new figure for given data type in units of
	 * default unit for figure size.
	 *
	 * @param dataType
	 *            the type of data
	 * @return the default size of a new figure
	 */
    static SGTuple2f getDefaultFigureSize(final String dataType) {
    	final float w, h;
    	if (SGDataUtility.isSXYTypeData(dataType)) {
            w = FIGURE_SXY_DEFAULT_WIDTH;
            h = FIGURE_SXY_DEFAULT_HEIGHT;
    	} else if (SGDataUtility.isVXYTypeData(dataType)) {
            w = FIGURE_VXY_DEFAULT_WIDTH;
            h = FIGURE_VXY_DEFAULT_HEIGHT;
    	} else if (SGDataUtility.isSXYZTypeData(dataType)) {
            w = FIGURE_SXYZ_DEFAULT_WIDTH;
            h = FIGURE_SXYZ_DEFAULT_HEIGHT;
    	} else {
    		throw new IllegalArgumentException("Invalid data type: " + dataType);
    	}
    	SGTuple2f size = new SGTuple2f(w, h);
    	return size;
    }

    private SGFigure createFigure(final int figureID,
            final SGDrawingWindow wnd, final SGData[] data) {
        final SGIProgressControl progress = (SGIProgressControl) wnd;

        SGFigure figure = new SGXYFigure(wnd);
        SGData dataFirst = data[0];
        String type;
        if (dataFirst instanceof SGISXYTypeSingleData
                || dataFirst instanceof SGISXYTypeMultipleData) {
            type = SGIFigureTypeConstants.FIGURE_TYPE_SXY;
        } else if (dataFirst instanceof SGIVXYTypeData) {
            type = SGIFigureTypeConstants.FIGURE_TYPE_VXY;
        } else if (dataFirst instanceof SGISXYZTypeData) {
            type = SGIFigureTypeConstants.FIGURE_TYPE_SXY;
        } else {
            return null;
        }

        figure.setVisible(false);
        figure.setID(figureID);
        figure.setClassType(type);

        if (this.createFigureElements(figure, progress) == false) {
            return null;
        }

        // set size after created a figure instance
        SGTuple2f size = getDefaultFigureSize(dataFirst.getDataType());
//        figure.setFigureWidth(size.x, SGIFigureConstants.FIGURE_SIZE_UNIT);
//        figure.setFigureHeight(size.y, SGIFigureConstants.FIGURE_SIZE_UNIT);
        figure.setFigureSize(size.x, size.y, SGIFigureConstants.FIGURE_SIZE_UNIT);

        return figure;
    }

    public SGFigure createEmptyFigure(final int figureID,
            final SGDrawingWindow wnd) {

        SGFigure figure = new SGXYFigure(wnd);
        figure.setVisible(false);
        figure.setID(figureID);
        if (this.createFigureElements(figure, wnd) == false) {
            return null;
        }

        // set size after created a figure instance
        figure.setFigureWidth(FIGURE_DEFAULT_WIDTH, SGIFigureConstants.FIGURE_SIZE_UNIT);
        figure.setFigureHeight(FIGURE_DEFAULT_HEIGHT, SGIFigureConstants.FIGURE_SIZE_UNIT);

        return figure;
    }

    /**
     * Creates a new figure.
     *
     * @param wnd
     *           the parent window
     * @param figureID
     *           the figure to create
     * @param pos
     *            mouse location of window
     * @param dataArray
     *           the array of data object added
     * @param dataId
     *           the data ID
     * @param nameArray
     *           the array of data name
     * @return true if succeeded
     */
    public boolean createNewFigure(final SGDrawingWindow wnd,
            final int figureID, final Point pos, final Integer[] dataIdArray,
            final SGData[] dataArray, final String[] nameArray,
            final Map<String, Object> infoMap) {

        SGIProgressControl progress = (SGIProgressControl) wnd;

        // create a figure instance
        progress.startProgress();
        progress.setProgressMessage("Create Figure");

        // progress 0.0~0.5
        SGFigure figure = this.createFigure(figureID, wnd, dataArray);
        if (figure == null) {
            progress.endProgress();
            return false;
        }

        progress.setProgressValue(0.55f);

        // add a new figure to the window
        if (wnd.addFigure(figure, pos) == false) {
            wnd.endProgress();
            return false;
        }

        // add data object to the figure
        if (figure.addData(dataArray, dataIdArray, nameArray, progress, 0.6f, 0.9f, infoMap)==false) {
            wnd.endProgress();
            return false;
        }

        progress.setProgressValue(1.0f);

        // initializes the history of the properties after the data is added
        SGIFigureElement[] array = figure.getIFigureElementArray();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii].initPropertiesHistory() == false) {
                wnd.endProgress();
                return false;
            }
        }

        if (pos != null) {
            // draw back the figure into the paper
            figure.drawbackFigure();
        } else {
            // set the default location
            figure.setFigureX(DEFAULT_FIGURE_X, FIGURE_LOCATION_UNIT);
            figure.setFigureY(DEFAULT_FIGURE_Y, FIGURE_LOCATION_UNIT);
        }

        // initialize the history of the properties
        // after drawn back
        figure.initPropertiesHistory();

        // set visible
        figure.setVisible(true);

        // show the window and figure
        wnd.setVisible(true);

        // set changed
        wnd.setChanged(true);

        // update the items after the figure is set visible
        wnd.updateItemsByFigureNumbers();

        // terminate progress timer
        progress.endProgress();

        return true;
    }

}

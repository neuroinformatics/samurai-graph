package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDisposable;
import jp.riken.brain.ni.samuraigraph.base.SGIVisible;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base class for the set of the groups of drawing elements. This object has an
 * array of drawing element groups.
 * 
 */
public abstract class SGElementGroupSet implements SGIConstants, SGIVisible, SGIDisposable {

    /**
     * The list of groups of drawing elements.
     */
    protected List<SGElementGroup> mDrawingElementGroupList = new ArrayList<SGElementGroup>();

    /**
     * The magnification.
     */
    protected float mMagnification = 1.0f;

    /**
     * The flag of visibility.
     */
    protected boolean mVisibleFlag = true;

    /**
     * The default constructor.
     */
    public SGElementGroupSet() {
        super();
    }

    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;
        for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
            SGElementGroup group = (SGElementGroup) this.mDrawingElementGroupList
                    .get(ii);
            group.dispose();
        }
        this.mDrawingElementGroupList.clear();
        this.mDrawingElementGroupList = null;
    }

    /**
     * Returns a new instance of a list of groups of drawing elements.
     * 
     * @return a list of groups of drawing elements
     */
    public ArrayList<SGElementGroup> getElementGroupList() {
        return new ArrayList<SGElementGroup>(this.mDrawingElementGroupList);
    }
    
//    public Map<String, SGProperties> getElementGroupPropertiesList() {
//        List<SGProperties> pList = new ArrayList<SGProperties>();
//        for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
//            SGElementGroup g = (SGElementGroup) this.mDrawingElementGroupList.get(ii);
//            pList.add(g.getProperties());
//        }
//        return pList;
//    }
//    
//    public boolean setElementGroupListProperties(Map<String, SGProperties> pMap) {
//        for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
//            SGElementGroup group = (SGElementGroup) this.mDrawingElementGroupList.get(ii);
//            SGProperties gp = pList.get(ii);
//            if (group.setProperties(gp) == false) {
//                return false;
//            }
//        }
//        return true;
//    }
    

//    /**
//     * Returns a list of drawing elements.
//     * 
//     * @return a list of drawing elements
//     */
//    public ArrayList getDrawingElementList() {
//        ArrayList list = new ArrayList();
//        for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
//            SGElementGroup group = (SGElementGroup) this.mDrawingElementGroupList
//                    .get(ii);
//            SGDrawingElement dElement = group.getDrawingElement();
//            list.add(dElement);
//        }
//        return list;
//    }

    /**
     * Move a group of drawing elements to front.
     * 
     * @param group
     *            a group to move
     * @return true if succeeded
     */
    public boolean moveElementsToFront(final SGElementGroup group) {
    	List<SGElementGroup> groupList = this.mDrawingElementGroupList;
        for (int ii = 0; ii < groupList.size(); ii++) {
            if (group.equals(groupList.get(ii))) {
                groupList.remove(ii);
                break;
            }
        }
        groupList.add(groupList.size(), group);
        return true;
    }

    /**
     * Move a group of drawing elements to back.
     * 
     * @param group
     *            a group to move
     * @return true if succeeded
     */
    public boolean moveElementsToBack(final SGElementGroup group) {
    	List<SGElementGroup> groupList = this.mDrawingElementGroupList;
        for (int ii = 0; ii < groupList.size(); ii++) {
            if (group.equals(groupList.get(ii))) {
                groupList.remove(ii);
                break;
            }
        }
        groupList.add(0, group);
        return true;
    }

    /**
     * Remove a group of drawing elements.
     * 
     * @param group
     *            a group to remove
     * @return true if succeeded
     */
    public boolean removeElements(final SGElementGroup group) {
    	List<SGElementGroup> groupList = this.mDrawingElementGroupList;
        for (int ii = 0; ii < groupList.size(); ii++) {
            if (group.equals(groupList.get(ii))) {
                groupList.remove(ii);
                return true;
            }
        }
        return false;
    }

    /**
     * Set the visibility of this group set.
     * 
     * @param b
     *            a value to set to the visibility
     */
    public void setVisible(final boolean b) {
        this.mVisibleFlag = b;
    }

    /**
     * Returns the visibility of this group set.
     * 
     * @return the visibility of this group set
     */
    public boolean isVisible() {
        return this.mVisibleFlag;
    }

    /**
     * Returns the magnification.
     * 
     * @return the magnification
     */
    public float getMagnification() {
        return this.mMagnification;
    }

    /**
     * Set the magnification.
     * 
     * @param mag
     *            a value to set to the magnification
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        this.mMagnification = mag;
        for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
            SGElementGroup group = (SGElementGroup) this.mDrawingElementGroupList.get(ii);
            if (group.setMagnification(mag) == false) {
                return false;
            }
        }
        return true;
    }

//    /**
//     * Zoom this object.
//     * 
//     * @param mag
//     *            a value to set to the magnification
//     * @return true if succeeded
//     */
//    public boolean zoom(final float mag) {
//        this.mMagnification = mag;
//        ArrayList list = this.mDrawingElementGroupList;
//        for (int ii = 0; ii < list.size(); ii++) {
//            SGElementGroup group = (SGElementGroup) list.get(ii);
//            group.zoom(mag);
//        }
//        return true;
//    }

    /**
     * Add a group of drawing elements to this group set.
     * 
     * @param type
     *            the type of group
     * @return true if succeeded
     */
    public abstract boolean addDrawingElementGroup(final int type);

    /**
     * Paint this group set.
     * 
     * @param g2d
     *            graphic context
     */
    public abstract void paintGraphics2D(final Graphics2D g2d);

    /**
     * Returns whether this group set contains the given point.
     * 
     * @param x
     *          x coordinate
     * @param y
     *          y coordinate
     * @return true if this group set contains the given point
     */
    public boolean contains(final int x, final int y) {
    	List<SGElementGroup> list = this.mDrawingElementGroupList;
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            SGElementGroup group = (SGElementGroup) list.get(ii);
            if (group.isVisible() == false) {
                continue;
            }
            if (group.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an element group at a given point.
     * 
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @return an element group if it contains the given point
     */
    public SGElementGroup getElementGroupAt(final int x, final int y) {
    	List<SGElementGroup> list = this.mDrawingElementGroupList;
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            SGElementGroup group = (SGElementGroup) list.get(ii);
            if (group.isVisible() == false) {
                continue;
            }
            if (group.contains(x, y)) {
                return group;
            }
        }
        return null;
    }

    // /**
    // *
    // */
    // public abstract boolean addDrawingElementGroup( final SGDrawingElement
    // element );

    /**
     * Returns the name of tag in property file.
     * 
     * @return the name of tag in property file
     */
    public abstract String getTagName();

    /**
     * Write properties of this object to the Element.
     * 
     * @param el
     *            the Element object
     * @param operation
     *            the operation
     * @return true if succeeded
     */
    public abstract boolean writeProperty(final Element el, final SGExportParameter params);

    /**
     * Create an Element object and write properties of this group set and
     * element groups.
     * 
     * @param document
     *            the Document object
     * @param operation
     *            the operation
     * @return an Element object created
     */
    public Element createElement(final Document document, final SGExportParameter params) {

        // create an Element object
        Element el = document.createElement(this.getTagName());

        // write properties of this group set
        if (this.writeProperty(el, params) == false) {
            return null;
        }

        // create Element objects and write properties for all element groups
        for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
            SGElementGroup group = (SGElementGroup) this.mDrawingElementGroupList
                    .get(ii);
            String tagName = group.getTagName();
            Element elGroup = document.createElement(tagName);
            if (group.writeProperty(elGroup) == false) {
                return null;
            }
            el.appendChild(elGroup);
        }

        return el;
    }

    /**
     * Check whether this group set contains a given point.
     * 
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @return true if this group set contains a given point
     */
    public abstract boolean onDrawingElement(final int x, final int y);

    /**
     * Set properties to this group set.
     * 
     * @param p
     *            properties to set to this group set
     * @return true if succeeded
     */
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof ElementGroupSetProperties) == false) {
            return false;
        }
        ElementGroupSetProperties ep = (ElementGroupSetProperties) p;
        this.setVisible(ep.visible);

        // set properties of element groups
        if (this.setElementGroupProperties(ep.mElementGroupPropertiesList) == false) {
            return false;
        }
        
//        if (this.mDrawingElementGroupList.size() != ep.mElementGroupPropertiesList.size()) {
//            return false;
//        }
//        for (int ii = 0; ii < ep.mElementGroupPropertiesList.size(); ii++) {
//            SGProperties gp = (SGProperties) ep.mElementGroupPropertiesList.get(ii);
//            SGElementGroup group = (SGElementGroup) this.mDrawingElementGroupList
//                    .get(ii);
//            if (group.setProperties(gp) == false) {
//                return false;
//            }
//        }
        return true;
    }

    /**
     * Sets the properties of element groups.
     * 
     * @param elementGroupPropertiesList
     * @return true if succeeded
     * 
     */
    protected abstract boolean setElementGroupProperties(
            List elementGroupPropertiesList);
    
    /**
     * Returns properties of this group set.
     * 
     * @return properties of this group set
     */
    public SGProperties getProperties() {
        ElementGroupSetProperties p = new ElementGroupSetProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    /**
     * Get properties of this group set.
     * 
     * @param p
     *            a property object to be set
     * @return true if succeeded
     */
    public boolean getProperties(final SGProperties p) {
        if ((p instanceof ElementGroupSetProperties) == false) {
            return false;
        }
        ElementGroupSetProperties gp = (ElementGroupSetProperties) p;
        gp.visible = this.isVisible();
        (gp.mElementGroupPropertiesList).clear();
        for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
            SGElementGroup group = (SGElementGroup) this.mDrawingElementGroupList
                    .get(ii);
            (gp.mElementGroupPropertiesList).add(group.getProperties());
        }
        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append("visible=");
        sb.append(this.mVisibleFlag);
        sb.append(", ");
        sb.append("]");
        return sb.toString();
    }


    /**
     * A class of properties of a group set.
     */
    public static class ElementGroupSetProperties extends SGProperties {

        /**
         * visibility
         */
        protected boolean visible;

        /**
         * a list of properties of element groups in the group set
         */
        protected List mElementGroupPropertiesList = new ArrayList();
        
        /**
         * The default constructor.
         */
        public ElementGroupSetProperties() {
            super();
        }
        
        /**
         * Copy this object.
         * @return a copied object
         */
        public Object copy() {
            Object obj = super.copy();
            ElementGroupSetProperties p = (ElementGroupSetProperties) obj;
            List list = new ArrayList();
            for (int ii = 0; ii < this.mElementGroupPropertiesList.size(); ii++) {
                SGProperties gp = (SGProperties) this.mElementGroupPropertiesList.get(ii);
                list.add(gp.copy());
            }
            p.mElementGroupPropertiesList = list;
            return p;
        }

        /**
         * Disposes of this object.
         */
        public void dispose() {
        	super.dispose();
            for (int ii = 0; ii < this.mElementGroupPropertiesList.size(); ii++) {
                SGProperties p = (SGProperties) this.mElementGroupPropertiesList.get(ii);
                p.dispose();
            }
            this.mElementGroupPropertiesList.clear();
            this.mElementGroupPropertiesList = null;
        }

        /**
         * Returns whether this object is equal to given object.
         * 
         * @param obj
         *            an object to be compared
         * @return true if two objects are equal
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof ElementGroupSetProperties) == false) {
                return false;
            }
            ElementGroupSetProperties p = (ElementGroupSetProperties) obj;
            if (p.visible != this.visible) {
                return false;
            }
            if (p.mElementGroupPropertiesList
                    .equals(this.mElementGroupPropertiesList) == false) {
                return false;
            }
            return true;
        }

        /**
         * Returns string representation of this object.
         * 
         * @return string representation of this object
         */
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("[");
            sb.append("visible=");
            sb.append(this.visible);
            sb.append(", ");
            sb.append(this.mElementGroupPropertiesList);
            sb.append("]");
            return sb.toString();
        }

        public List getElementGroupPropertiesList() {
            return new ArrayList(mElementGroupPropertiesList);
        }

        public void setElementGroupPropertiesList(List elementGroupPropertiesList) {
            mElementGroupPropertiesList = new ArrayList(elementGroupPropertiesList);
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

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

    public SGTuple2f getLocation(final int index, final SGTuple2f[] pointsArray) {
        if (pointsArray == null) {
            return null;
        }
        if (index < 0 || index >= pointsArray.length) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        return pointsArray[index];
    }


}

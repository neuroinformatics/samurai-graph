package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;

public class SGSXYZDataPopupMenu extends SGDataPopupMenu {

	private static final long serialVersionUID = 5484201211236316780L;

	public SGSXYZDataPopupMenu(final SGDrawingWindow wnd, final SGElementGroupSetForData gs, 
			final boolean inGraph) {
		super(wnd, gs, inGraph);
	}

	protected boolean checkDataType(final String dataType) {
		return SGDataUtility.isSXYZTypeData(dataType);
	}

    /**
     * Returns the list of child menu items for axis fitting.
     * 
     * @return the list of child menu items for axis fitting
     */
	@Override
    protected List<PopupMenuItem> getFitAxisChildList(ActionListener l) {
    	List<PopupMenuItem> list = new ArrayList<PopupMenuItem>();
        list.add(new PopupMenuItem(MENUCMD_FIT_ALL_AXES_TO_DATA, l));
        list.add(new PopupMenuItem(MENUCMD_FIT_HORIZONTAL_AXIS_TO_DATA, l));
        list.add(new PopupMenuItem(MENUCMD_FIT_VERTICAL_AXIS_TO_DATA, l));
        list.add(new PopupMenuItem(MENUCMD_FIT_COLOR_BAR_TO_DATA, l));
    	return list;
    }
}

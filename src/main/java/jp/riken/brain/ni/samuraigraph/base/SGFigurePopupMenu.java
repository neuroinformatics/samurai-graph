package jp.riken.brain.ni.samuraigraph.base;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;

public class SGFigurePopupMenu extends SGPopupMenu {

	private static final long serialVersionUID = 3579134284613026133L;
	
	private List<SGFigure> mFigureList = new ArrayList<SGFigure>();

	public SGFigurePopupMenu(List<SGFigure> figList) {
		super();
		
		if (figList.size() == 0) {
			throw new IllegalArgumentException("figList.size() == 0");
		}
		
		Set<SGDrawingWindow> wndSet = new HashSet<SGDrawingWindow>();
		for (SGFigure fig : figList) {
			wndSet.add(fig.getWindow());
		}
		if (wndSet.size() > 1) {
			throw new IllegalArgumentException("Not for multiple windows.");
		}
		
		this.mFigureList.addAll(figList);
		this.initProperty();
	}

	private void initProperty() {
        this.setBounds(0, 0, 100, 100);

        StringBuffer sb = new StringBuffer();
        sb.append("  -- Figure: ");
        for (int ii = 0; ii < this.mFigureList.size(); ii++) {
            if (ii > 0) {
            	sb.append(", ");
            }
            SGFigure fig = this.mFigureList.get(ii);
            final int id = fig.getID();
            sb.append(id);
        }
        sb.append(" --");

        this.add(new JLabel(sb.toString()));
        this.addSeparator();

        // draw rectangle
        this.addCheckBoxItem(SGFigure.MENUCMD_RUBBER_BANDING);

        // snap to grid
        this.addCheckBoxItem(SGFigure.MENUBARCMD_SNAP_TO_GRID);

        // show bounding box
        this.addCheckBoxItem(SGFigure.MENUCMD_SHOW_BOUNDING_BOX);

        this.addSeparator();

        // save property
        this.addItem(SGFigure.MENUCMD_SAVE_PROPERTY);

        this.addSeparator();

        // move to back or front
        this.addArrangeItems();
        
        this.addSeparator();

        // cut
        this.addItem(SGFigure.MENUCMD_CUT);

        // copy
        this.addItem(SGFigure.MENUCMD_COPY);

        // paste
        this.addItem(SGFigure.MENUCMD_PASTE);

        this.addSeparator();

        // delete
        this.addItem(SGFigure.MENUCMD_DELETE);

        // duplicate
        this.addItem(SGFigure.MENUCMD_DUPLICATE);

        this.addSeparator();

        // visibility of legend and color bar
        this.addCheckBoxItem(SGFigure.MENUCMD_LEGEND_VISIBLE);
        this.addCheckBoxItem(SGFigure.MENUCMD_COLOR_BAR_VISIBLE);
        this.addCheckBoxItem(SGFigure.MENUCMD_SCALE_VISIBLE);
        this.addCheckBoxItem(SGFigure.MENUCMD_GRID_VISIBLE);
        this.addCheckBoxItem(SGFigure.MENUCMD_DATA_ANCHORED);
        JMenu visibleAxesMenu = this.addMenu(SGFigure.MENUCMD_AXES_VISIBLE);
        this.addCheckBoxItem(visibleAxesMenu, SGFigure.MENUCMD_VISIBLE_BOTTOM_AXIS);
        this.addCheckBoxItem(visibleAxesMenu, SGFigure.MENUCMD_VISIBLE_LEFT_AXIS);
        this.addCheckBoxItem(visibleAxesMenu, SGFigure.MENUCMD_VISIBLE_TOP_AXIS);
        this.addCheckBoxItem(visibleAxesMenu, SGFigure.MENUCMD_VISIBLE_RIGHT_AXIS);

        this.addSeparator();

        // fit axes and align bars
        JMenu fitAxesMenu = this.addMenu(SGFigure.MENUCMD_FIT_AXES_TO_DATA);
        this.addItem(fitAxesMenu, SGFigure.MENUCMD_FIT_ALL_AXES_TO_DATA);
    	this.addItem(fitAxesMenu, SGFigure.MENUCMD_FIT_HORIZONTAL_AXIS_TO_DATA);
    	this.addItem(fitAxesMenu, SGFigure.MENUCMD_FIT_VERTICAL_AXIS_TO_DATA);
    	this.addItem(fitAxesMenu, SGFigure.MENUCMD_FIT_COLOR_BAR_TO_DATA);
    	this.addItem(fitAxesMenu, SGFigure.MENUCMD_FIT_ALL_AXES_TO_DATA_FOR_ALL_ANIMATION_FRAMES);
    	this.addItem(SGFigure.MENUCMD_ALIGN_BARS);

    	this.addSeparator();

        // property
        this.addItem(SGFigure.MENUCMD_PROPERTY);
        
        this.addActionListener(this);
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        final String command = e.getActionCommand();
        Object source = e.getSource();
        
        SGFigure figure = this.mFigureList.get(0);
        SGDrawingWindow wnd = figure.getWindow();

        // an event from the pop-up menu
        if (command.equals(SGFigure.MENUCMD_RUBBER_BANDING)) {
            SGFigure.mRubberBandFlag = !SGFigure.mRubberBandFlag;
            wnd.repaint();
        } else if (command.equals(SGFigure.MENUBARCMD_SNAP_TO_GRID)) {
            SGFigure.setSnappingToGrid(!SGFigure.isSnappingToGrid());
            wnd.updateSnapToGridItems();
        } else if (command.equals(SGFigure.MENUCMD_SHOW_BOUNDING_BOX)) {
            SGFigure.mBoundingBoxVisibleFlag = !SGFigure.mBoundingBoxVisibleFlag;
            wnd.repaint();
        } else if (command.equals(SGFigure.MENUCMD_SAVE_PROPERTY)) {
            wnd.createPropertyFileFromFocusedFigures();
        } else if (command.equals(MENUCMD_PROPERTY)) {
            wnd.showPropertyDialogForSelectedFigures();
        } else if (command.equals(MENUCMD_BRING_TO_FRONT)) {
            wnd.bringFocusedObjectsToFront();
        } else if (command.equals(MENUCMD_BRING_FORWARD)) {
            wnd.bringFocusedObjectsForward();
        } else if (command.equals(MENUCMD_SEND_BACKWARD)) {
            wnd.sendFocusedObjectsBackward();
        } else if (command.equals(MENUCMD_SEND_TO_BACK)) {
            wnd.sendFocusedObjectsToBack();
        } else if (command.equals(MENUCMD_CUT)) {
            wnd.doCut();
        } else if (command.equals(MENUCMD_COPY)) {
            wnd.doCopy();
        } else if (command.equals(MENUCMD_PASTE)) {
            wnd.doPaste();
        } else if (command.equals(MENUCMD_DELETE)) {
            wnd.doDelete();
        } else if (command.equals(MENUCMD_DUPLICATE)) {
            wnd.doDuplicate();
        } else if (command.equals(SGFigure.MENUCMD_LEGEND_VISIBLE)) {
        	SGCheckBoxMenuItem item = (SGCheckBoxMenuItem) this.findMenuItem(SGFigure.MENUCMD_LEGEND_VISIBLE);
        	final boolean b = item.isSelected();
        	for (SGFigure fig : this.mFigureList) {
                fig.setLegendVisible(b);
        	}
            wnd.notifyToRoot();
            wnd.repaint();
        } else if (command.equals(SGFigure.MENUCMD_COLOR_BAR_VISIBLE)) {
        	SGCheckBoxMenuItem item = (SGCheckBoxMenuItem) this.findMenuItem(SGFigure.MENUCMD_COLOR_BAR_VISIBLE);
        	final boolean b = item.isSelected();
        	for (SGFigure fig : this.mFigureList) {
                fig.setColorBarVisibleWithCommit(b);
        	}
            wnd.notifyToRoot();
            wnd.repaint();
        } else if (command.equals(SGFigure.MENUCMD_SCALE_VISIBLE)) {
        	SGCheckBoxMenuItem item = (SGCheckBoxMenuItem) this.findMenuItem(SGFigure.MENUCMD_SCALE_VISIBLE);
        	final boolean b = item.isSelected();
        	for (SGFigure fig : this.mFigureList) {
                fig.setAxisScaleVisibleWithCommit(b);
        	}
            wnd.notifyToRoot();
            wnd.repaint();
        } else if (command.equals(SGFigure.MENUCMD_GRID_VISIBLE)) {
        	SGCheckBoxMenuItem item = (SGCheckBoxMenuItem) this.findMenuItem(SGFigure.MENUCMD_GRID_VISIBLE);
        	final boolean b = item.isSelected();
        	for (SGFigure fig : this.mFigureList) {
                fig.setGridVisibleWithCommit(b);
        	}
            wnd.notifyToRoot();
            wnd.repaint();
        } else if (command.equals(SGFigure.MENUCMD_DATA_ANCHORED)) {
        	SGCheckBoxMenuItem item = (SGCheckBoxMenuItem) this.findMenuItem(SGFigure.MENUCMD_DATA_ANCHORED);
        	final boolean b = item.isSelected();
        	for (SGFigure fig : this.mFigureList) {
        		final boolean cur = fig.isDataAnchored();
	        	fig.setDataAnchored(b);
	        	if (b != cur) {
		        	fig.setChanged(true);
	        	}
        	}
        	wnd.notifyToRoot();
        } else if (MENUCMD_VISIBLE_BOTTOM_AXIS.equals(command)
        		|| MENUCMD_VISIBLE_LEFT_AXIS.equals(command)
        		|| MENUCMD_VISIBLE_TOP_AXIS.equals(command)
        		|| MENUCMD_VISIBLE_RIGHT_AXIS.equals(command)) {

        	int location = -1;
	        if (MENUCMD_VISIBLE_BOTTOM_AXIS.equals(command)) {
	        	location = SGIFigureElementAxis.AXIS_HORIZONTAL_1;
	        } else if (MENUCMD_VISIBLE_LEFT_AXIS.equals(command)) {
	        	location = SGIFigureElementAxis.AXIS_VERTICAL_1;
	        } else if (MENUCMD_VISIBLE_TOP_AXIS.equals(command)) {
	        	location = SGIFigureElementAxis.AXIS_HORIZONTAL_2;
	        } else if (MENUCMD_VISIBLE_RIGHT_AXIS.equals(command)) {
	        	location = SGIFigureElementAxis.AXIS_VERTICAL_2;
	        }
	        if (location == -1) {
	        	return;
	        }
            final boolean visible = ((JCheckBoxMenuItem)e.getSource()).isSelected();
        	for (SGFigure fig : this.mFigureList) {
        		final boolean cur = fig.isAxisVisible(location);
        		fig.setAxisVisible(location, visible);
        		if (cur != visible) {
        			fig.getAxisElement().setChanged(location, true);
        		}
        	}
        	wnd.notifyToRoot();
        	wnd.repaint();
        } else if (MENUCMD_FIT_ALL_AXES_TO_DATA.equals(command)
        		|| MENUCMD_FIT_HORIZONTAL_AXIS_TO_DATA.equals(command)
        		|| MENUCMD_FIT_VERTICAL_AXIS_TO_DATA.equals(command)
        		|| MENUCMD_FIT_COLOR_BAR_TO_DATA.equals(command)
        		|| MENUCMD_FIT_ALL_AXES_TO_DATA_FOR_ALL_ANIMATION_FRAMES.equals(command)) {
            List<Integer> axisDirList = new ArrayList<Integer>();
			boolean forAnimationFrames = false;
	        if (MENUCMD_FIT_ALL_AXES_TO_DATA.equals(command)
	        		|| MENUCMD_FIT_ALL_AXES_TO_DATA_FOR_ALL_ANIMATION_FRAMES.equals(command)) {
	        	axisDirList.add(SGIFigureElementAxis.AXIS_DIRECTION_HORIZONTAL);
	        	axisDirList.add(SGIFigureElementAxis.AXIS_DIRECTION_VERTICAL);
	        	axisDirList.add(SGIFigureElementAxis.AXIS_DIRECTION_NORMAL);
				if (MENUCMD_FIT_ALL_AXES_TO_DATA_FOR_ALL_ANIMATION_FRAMES.equals(command)) {
					forAnimationFrames = true;
				}
	        } else if (MENUCMD_FIT_HORIZONTAL_AXIS_TO_DATA.equals(command)) {
	        	axisDirList.add(SGIFigureElementAxis.AXIS_DIRECTION_HORIZONTAL);
	        } else if (MENUCMD_FIT_VERTICAL_AXIS_TO_DATA.equals(command)) {
	        	axisDirList.add(SGIFigureElementAxis.AXIS_DIRECTION_VERTICAL);
	        } else if (MENUCMD_FIT_COLOR_BAR_TO_DATA.equals(command)) {
	        	axisDirList.add(SGIFigureElementAxis.AXIS_DIRECTION_NORMAL);
	        }
            wnd.doFitAxisRangeToVisibleData(axisDirList, forAnimationFrames);
//        } else if (command.equals(MENUCMD_INSERT_NETCDF_LABEL)) {
//        	wnd.doInserNetCDFLabel();
//            wnd.notifyToRoot();
//        } else if (command.equals(MENUCMD_ANIMATION)) {
//        	for (SGFigure fig : this.mFigureList) {
//                fig.mMouseInExtraRegionFlag = false;
//                fig.clearFocusedObjects();
//        	}
        } else if (command.equals(MENUCMD_ALIGN_BARS)) {
            wnd.doAlignBars();
//        } else if (command.equals(MENUCMD_SPLIT_DATA)) {
//            wnd.doSplitMultipleData();
//        } else if (command.equals(MENUCMD_TRANSFORM_DATA)) {
//            wnd.doTransformData();
//        } else if (command.equals(MENUCMD_ANCHOR)) {
//            boolean isAnchored = ((JCheckBoxMenuItem)e.getSource()).isSelected();
//        	for (SGFigure fig : this.mFigureList) {
//                fig.setAnchoredToFocusedObjects(isAnchored);
//        	}
//        } else if (command.equals(SGPluginsQueryMessage.MENUCMD_OUTPUT_TO_FILE_IS_ENABLED)) {
//            if (source instanceof SGPluginsQueryMessage) {
//                List<SGData> data = this.getGraphElement().getFocusedDataList();
//                if (data.size()!=1) {
//                    ((SGPluginsQueryMessage)source).set(Boolean.FALSE);
//                } else {
//                    this.mWnd.notifyToListener(MENUCMD_OUTPUT_TO_FILE, source);
//                }
//                return;
//            }
//        } else if (command.equals(MENUCMD_OUTPUT_TO_FILE)) {
//        	wnd.doOutputDataToFile();
        } else if (command.equals(SGIFigureElement.NOTIFY_UNKNOWN_DATA_ERROR)) {
        	for (SGFigure fig : this.mFigureList) {
        		fig.hideData(new int[] { ((Integer) source).intValue() } );
        	}
            wnd.clearUndoBuffer();
        }
    }

}

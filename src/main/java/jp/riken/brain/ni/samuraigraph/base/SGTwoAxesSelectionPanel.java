package jp.riken.brain.ni.samuraigraph.base;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

/**
 * @author kuromaru
 */
public class SGTwoAxesSelectionPanel extends SGAxisSelectionPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -5861520065833367854L;

    // The radio button with no selection.
    private JToggleButton mHorizontalNoAxisSelectionButton = new JToggleButton();

    private JToggleButton mVerticalNoAxisSelectionButton = new JToggleButton();

    /**
     * The default constructor.
     */
    public SGTwoAxesSelectionPanel() {
        super();
        this.initProperty();
    }

    // initialization
    private void initProperty() {
        // add to ButtonGroups
        ButtonGroup hbg = new ButtonGroup();
        hbg.add(this.mTopButton);
        hbg.add(this.mBottomButton);
        hbg.add(this.mHorizontalNoAxisSelectionButton);

        ButtonGroup pbg = new ButtonGroup();
        pbg.add(this.mLeftButton);
        pbg.add(this.mRightButton);
        pbg.add(this.mVerticalNoAxisSelectionButton);

        // add to controllers
        this.mHorizontalController.add(this.mBottomLine);
        this.mHorizontalController.add(this.mTopLine);
        this.mVerticalController.add(this.mLeftLine);
        this.mVerticalController.add(this.mRightLine);
    }

    private SGExclusiveAccessController mHorizontalController = new SGExclusiveAccessController();

    private SGExclusiveAccessController mVerticalController = new SGExclusiveAccessController();

    protected void updateImage() {
        this.mAxisImagePanel.setTopSelected(this.isTopSelected());
        this.mAxisImagePanel.setBottomSelected(this.isBottomSelected());
        this.mAxisImagePanel.setLeftSelected(this.isLeftSelected());
        this.mAxisImagePanel.setRightSelected(this.isRightSelected());
        this.repaint();
    }

    protected void updateButtons() {
        final boolean top = this.isTopSelected();
        final boolean bottom = this.isBottomSelected();
        final boolean left = this.isLeftSelected();
        final boolean right = this.isRightSelected();

        if (top || bottom) {
            this.mTopButton.setSelected(top);
            this.mBottomButton.setSelected(bottom);
        } else {
            this.mHorizontalNoAxisSelectionButton.setSelected(true);
        }

        if (left || right) {
            this.mLeftButton.setSelected(left);
            this.mRightButton.setSelected(right);
        } else {
            this.mVerticalNoAxisSelectionButton.setSelected(true);
        }
    }

    /**
     * Returns the selected x-axis location.
     * 
     * @return the location of x-axis
     */
    public int getXAxisLocation() {
        int location = -1;

        // set the related axes
        if (this.mHorizontalNoAxisSelectionButton.isSelected() == false) {
            if (this.isBottomSelected()) {
                location = SGIFigureElementAxis.AXIS_HORIZONTAL_1;
            } else if (this.isTopSelected()) {
                location = SGIFigureElementAxis.AXIS_HORIZONTAL_2;
            }
        }

        return location;
    }

    /**
     * Returns the selected y-axis location.
     * 
     * @return the location of y-axis
     */
    public int getYAxisLocation() {
        int location = -1;

        // set the related axes
        if (this.mVerticalNoAxisSelectionButton.isSelected() == false) {
            if (this.isLeftSelected()) {
                location = SGIFigureElementAxis.AXIS_VERTICAL_1;
            } else if (this.isRightSelected()) {
                location = SGIFigureElementAxis.AXIS_VERTICAL_2;
            }
        }

        return location;
    }

    public void setSelectedAxis(Integer xAxisLocation, Integer yAxisLocation) {
        this.clearAll();
        if (xAxisLocation != null) {
            this.selectHorizontalAxis(xAxisLocation);
        }
        if (yAxisLocation != null) {
            this.selectVerticalAxis(yAxisLocation);
        }

        // update components and image
        this.updateButtons();
        this.updateImage();
    }

    // set selected buttons
    private void selectHorizontalAxis(final int location) {
        switch (location) {
        case SGIFigureElementAxis.AXIS_HORIZONTAL_1: {
            this.setBottomSelected(true);
            break;
        }
        case SGIFigureElementAxis.AXIS_HORIZONTAL_2: {
            this.setTopSelected(true);
            break;
        }
        default: {
            throw new IllegalArgumentException("");
        }
        }
    }

    // set selected buttons
    private void selectVerticalAxis(final int location) {
        switch (location) {
        case SGIFigureElementAxis.AXIS_VERTICAL_1: {
            this.setLeftSelected(true);
            break;
        }

        case SGIFigureElementAxis.AXIS_VERTICAL_2: {
            this.setRightSelected(true);
            break;
        }

        default: {
            throw new IllegalArgumentException("");
        }
        }
    }

    /**
     * Set the given line to be selected.
     */
    protected void selectLine(Line l) {
        if (this.mHorizontalController.contains(l)) {
            this.mHorizontalController.select(l);
        } else if (this.mVerticalController.contains(l)) {
            this.mVerticalController.select(l);
        }
    }

}

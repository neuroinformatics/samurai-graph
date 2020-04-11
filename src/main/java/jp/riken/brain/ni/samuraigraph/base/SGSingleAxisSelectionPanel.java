package jp.riken.brain.ni.samuraigraph.base;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

/**
 * @author kuromaru
 */
public class SGSingleAxisSelectionPanel extends SGAxisSelectionPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 4245679432245319436L;

    // The radio button with no selection.
    private JToggleButton mNoAxisSelectionButton = new JToggleButton();

    /**
     * The default constructor.
     */
    public SGSingleAxisSelectionPanel() {
        super();
        this.initProperty();
    }

    private void initProperty() {
        // add to a ButtonGroup
        ButtonGroup bg = new ButtonGroup();
        bg.add(this.mTopButton);
        bg.add(this.mBottomButton);
        bg.add(this.mLeftButton);
        bg.add(this.mRightButton);
        bg.add(this.mNoAxisSelectionButton);

        // add to a controller
        this.mController.add(this.mTopLine);
        this.mController.add(this.mBottomLine);
        this.mController.add(this.mLeftLine);
        this.mController.add(this.mRightLine);
    }

    private SGExclusiveAccessController mController = new SGExclusiveAccessController();

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
        if (top || bottom || left || right) {
            this.mTopButton.setSelected(top);
            this.mBottomButton.setSelected(bottom);
            this.mLeftButton.setSelected(left);
            this.mRightButton.setSelected(right);
        } else {
            this.mNoAxisSelectionButton.setSelected(true);
        }
    }

    /**
     * Returns selected axis location.
     * 
     * @return selected axis location
     */
    public int getAxisLocation() {
        int location = -1;
        if (this.mNoAxisSelectionButton.isSelected() == false) {
            if (this.isTopSelected()) {
                location = SGIFigureElementAxis.AXIS_HORIZONTAL_2;
            } else if (this.isBottomSelected()) {
                location = SGIFigureElementAxis.AXIS_HORIZONTAL_1;
            } else if (this.isLeftSelected()) {
                location = SGIFigureElementAxis.AXIS_VERTICAL_1;
            } else if (this.isRightSelected()) {
                location = SGIFigureElementAxis.AXIS_VERTICAL_2;
            }
        }
        return location;
    }

    public void setSelectedAxis(Integer axisLocation) {
        // set selected button
        this.clearAll();
        if (axisLocation != null) {
            this.selectAxis(axisLocation.intValue());
        }

        // update components and image
        this.updateButtons();
        this.updateImage();
    }

    // set selected buttons with given configuration
    private void selectAxis(final int location) {
        switch (location) {
        case SGIFigureElementAxis.AXIS_HORIZONTAL_1: {
            this.setBottomSelected(true);
            break;
        }
        case SGIFigureElementAxis.AXIS_HORIZONTAL_2: {
            this.setTopSelected(true);
            break;
        }
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
        this.mController.select(l);
    }

}

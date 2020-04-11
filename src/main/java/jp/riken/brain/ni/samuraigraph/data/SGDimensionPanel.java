package jp.riken.brain.ni.samuraigraph.data;

import javax.swing.JPanel;

/**
 * The base class of dimension panel.
 *
 */
public abstract class SGDimensionPanel extends JPanel {

	private static final long serialVersionUID = 2695872968969587858L;

	/**
	 * The default constructor.
	 */
	public SGDimensionPanel() {
		super();
	}

    /**
     * Sets the components enabled.
     * 
     * @param enabled
     *           true to enable
     */
    public abstract void setComponentsEnabled(final boolean enabled);

    /**
     * Returns the slider panel.
     * 
     * @return the slider panel
     */
    protected abstract SGSliderPanel getSliderPanel();

    /**
     * Sets the values for the slider.
     * 
     * @param values
     *           the values
     * @param initIndex
     *           initial index of values
     */
    public void setValues(final double[] values, final int initIndex) {
        final int min = 0;
        final int max = (values.length == 0) ? 0 : (values.length - 1);

        // set to the slider
        this.getSliderPanel().setRange(min, max, initIndex);
    }

}

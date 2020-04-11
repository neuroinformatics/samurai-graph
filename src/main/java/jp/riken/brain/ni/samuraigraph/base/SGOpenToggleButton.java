package jp.riken.brain.ni.samuraigraph.base;

import javax.swing.JToggleButton;

/**
 * A toggle button to open / close the components.
 *
 */
public class SGOpenToggleButton extends JToggleButton {

	private static final long serialVersionUID = -2794731261680113088L;

	public SGOpenToggleButton() {
		super();
		this.initProperty();
		this.setSelected(false);
	}

	public SGOpenToggleButton(final boolean selected) {
		super();
		this.initProperty();
		this.setSelected(selected);
	}

	private void initProperty() {
		this.setIcon(SGUtility.createIcon(this, "Plus.png"));
		this.setSelectedIcon(SGUtility.createIcon(this, "Minus.png"));
	}
}

package jp.riken.brain.ni.samuraigraph.base;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

/** The original radio button class. */
public class SGRadioButton extends JRadioButton {

  /** */
  private static final long serialVersionUID = -5664751933445633492L;

  public SGRadioButton() {
    super();
    this.init();
  }

  public SGRadioButton(Icon icon) {
    super(icon);
    this.init();
  }

  public SGRadioButton(Action a) {
    super(a);
    this.init();
  }

  public SGRadioButton(String text) {
    super(text);
    this.init();
  }

  public SGRadioButton(Icon icon, boolean selected) {
    super(icon, selected);
    this.init();
  }

  public SGRadioButton(String text, boolean selected) {
    super(text, selected);
    this.init();
  }

  public SGRadioButton(String text, Icon icon) {
    super(text, icon);
    this.init();
  }

  public SGRadioButton(String text, Icon icon, boolean selected) {
    super(text, icon, selected);
    this.init();
  }

  /** Initialize this text field. */
  private void init() {}
}

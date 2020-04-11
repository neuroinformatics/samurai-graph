package jp.riken.brain.ni.samuraigraph.base;

import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

/**
 * The progress bar.
 */
public class SGProgressBar extends JProgressBar {

    private static final long serialVersionUID = -2851876059437040068L;

    /**
     * Minimum value of the progress bar.
     */
    public static final int PROGRESS_VALUE_MIN = 0;

    /**
     * Maximum value of the progress bar.
     */
    public static final int PROGRESS_VALUE_MAX = 100;

    /**
     * The default constructor.
     * 
     */
    public SGProgressBar() {
        super();
        this.setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    /**
     * Sets progress value.
     * 
     * @param ratio
     *          a value between 0 and 1
     */
    public void setProgressValue(final float ratio) {
    	if (ratio < 0.0 || ratio > 1.0) {
    		throw new IllegalArgumentException("ratio < 0.0 || ratio > 1.0 : " + ratio);
    	}
        final int min = PROGRESS_VALUE_MIN;
        final int max = PROGRESS_VALUE_MAX;
        final int value = (int) (min + ratio * max);
        final int current = this.getValue();
        int cnt = current;
        if (current == value) {
            return;
        } else if (current < value) {
            while (true) {
                this.setValue(cnt);
                cnt++;
                if (cnt > value) {
                    break;
                }
            }
        } else {
            while (true) {
                this.setValue(cnt);
                cnt--;
                if (cnt < value) {
                    break;
                }
            }
        }
    }

    /**
     * Initializes the progress value.
     * 
     */
    public void initProgressValue() {
        this.setValue(PROGRESS_VALUE_MIN);
    }

}

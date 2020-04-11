package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JWindow;

import jp.riken.brain.ni.samuraigraph.base.SGImagePanel;
import jp.riken.brain.ni.samuraigraph.base.SGProgressBar;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;

/**
 * The splash window displayed on the start up.
 */
public class SGSplashWindow extends JWindow {

    private static final long serialVersionUID = 5874724519536469064L;

    /**
     * Builds an splash window.
     * 
     * @param imageName
     *           the name of image file
     * @param version
     *           the version number
     */
    public SGSplashWindow(String imageName, String version) {
        super();

        Image img = SGUtility.createImage(this, imageName);
        if (img == null) {
        	return;
        }

        // get size from the image
        final int width = img.getWidth(this);
        final int height = img.getHeight(this);

        // set the bounds of this window
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((screen.width - width) / 2,
                (screen.height - height) / 2, width, height);

        // create an SGImagePanel object
        SGImagePanel imgPanel = new SGImagePanel();
        imgPanel.setImage(img);
        imgPanel.setPreferredSize(new Dimension(width, height));
        imgPanel.setLayout(null);

        // set text and position of the version number label
        final String vertext = "Version " + version;
        JLabel vLabel = new JLabel(vertext);
        final Font font = new Font("Serif", 1, 14);
        vLabel.setFont(font);
        final Dimension dim = vLabel.getPreferredSize();
        final int vw = dim.width;
        final int vh = dim.height;
        final int vx = verpos_r - vw;
        final int vy = (verpos_b - verpos_t - vh) / 2 + verpos_t;
        vLabel.setBounds(vx, vy, vw, vh);

        // creates a progress bar
        SGProgressBar bar = new SGProgressBar();
        bar.setValue(0);
        final int barX = 25;
        final int barY = 258;
        final int barWidth = 350;
        final int barHeight = 12;
        bar.setSize(barWidth, barHeight);
        bar.setLocation(barX, barY);
        this.mProgressBar = bar;
        // bar.setIndeterminate(true);
        // bar.setStringPainted(true);

        // add to the image panel
        imgPanel.add(vLabel);
        imgPanel.add(bar);

        // add image panel to the window
        this.getContentPane().add(imgPanel);

    }

    private static final int verpos_r = 375; // right position of the version
                                                // label

    private static final int verpos_t = 125; // top position of the version
                                                // label

    private static final int verpos_b = 155; // bottom position of the
                                                // version label

    /**
     * A progress bar.
     */
    private SGProgressBar mProgressBar;

    /**
     * Set progress value.
     * 
     * @param ratio
     *           progress ratio
     */
    public void setProgressValue(final float ratio) {
        if (this.mProgressBar == null) {
            return;
        }
        this.mProgressBar.setProgressValue(ratio);
    }

}

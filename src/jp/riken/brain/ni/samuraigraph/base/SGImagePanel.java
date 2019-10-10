package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * A panel class holding an Image object.
 */
public class SGImagePanel extends JPanel implements SGIDisposable {

    /**
     * 
     */
    private static final long serialVersionUID = 5727883543630249011L;

    /**
     * An image object.
     */
    private SGImage mImage = new SGImage(this);

    /**
     * 
     */
    public SGImagePanel() {
        super();
    }

    /**
     * 
     * @param image
     */
    public SGImagePanel(Image image) {
        super();
        this.mImage.setImage(image);
    }

    /**
     * 
     * @param image
     */
    public SGImagePanel(Image image, final int x, final int y, final int w,
            final int h) {
        super();
        this.mImage.setImage(image);
        this.mImage.setImageBounds(x, y, w, h);
    }

    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;
        this.mImage = null;
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

    /**
     * 
     * @param x
     * @param y
     */
    public void setImageLocation(final int x, final int y) {
        this.mImage.setImageLocation(x, y);
    }

    /**
     * 
     * @param w
     * @param h
     */
    public void setImageSize(final int w, final int h) {
        this.mImage.setImageSize(w, h);
    }

    /**
     * 
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void setImageBounds(final int x, final int y, final int w,
            final int h) {
        this.mImage.setImageBounds(x, y, w, h);
    }

    /**
     * 
     * @return
     */
    public SGTuple2f getImageLocation() {
        return this.mImage.getImageLocation();
    }

    /**
     * 
     * @return
     */
    public SGTuple2f getImageSize() {
        return this.mImage.getImageSize();
    }

    /**
     * 
     * @param image
     */
    public void setImage(final Image image) {
        this.mImage = new SGImage(image, this);
    }

    /**
     * Returns the image.
     * 
     * @return
     */
    public Image getImage() {
        return this.mImage.getImage();
    }

    /**
     * 
     */
    public void paintComponent(Graphics g) {
        if (this.mImage == null) {
            return;
        }

        super.paintComponent(g);

        this.mImage.drawImage(g);

    }

}

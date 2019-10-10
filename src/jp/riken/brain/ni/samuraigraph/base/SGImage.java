package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

/**
 * A class which manage an image and its image observer.
 */
public class SGImage implements SGIDisposable {

    /**
     * An image object.
     */
    private Image mImage = null;

    /**
     * An image observer.
     */
    private ImageObserver mImageObserver = null;

    /**
     * Location of the image.
     */
    private float mX;

    private float mY;

    /**
     * Size of the image.
     */
    private float mWidth;

    private float mHeight;

    /**
     * The magnification.
     */
    private float mMagnification = 1.0f;

    /**
     * The scaling factor.
     */
    private float mScalingFactor = 1.0f;

    /**
     * 
     * @param image
     */
    public SGImage(final ImageObserver obs) {
        super();
        this.setImageObserver(obs);
        this.setDafaultImageSize();
    }

    /**
     * 
     * @param image
     */
    public SGImage(final Image image, final ImageObserver obs) {
        super();
        this.setImage(image);
        this.setImageObserver(obs);
        this.setDafaultImageSize();
    }

    /**
     * 
     * @param image
     */
    public SGImage(final Image image, final ImageObserver obs, final float x,
            final float y, final float w, final float h) {
        super();
        this.setImage(image);
        this.setImageObserver(obs);
        this.setImageBounds(x, y, w, h);
    }

    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed =  true;
        this.mImage = null;
        this.mImageObserver = null;
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

    public boolean setImageLocationX(final float x) {
        return this.setImageLocation(x, this.getImageLocation().y);
    }

    public boolean setImageLocationY(final float y) {
        return this.setImageLocation(this.getImageLocation().x, y);
    }

    /**
     * 
     * @param x
     * @param y
     */
    public boolean setImageLocation(final float x, final float y) {
        this.mX = x;
        this.mY = y;
        return true;
    }

    public boolean setImageWidth(final float w) {
        this.mWidth = w;
        return true;
    }

    public boolean setImageHeight(final float h) {
        this.mHeight = h;
        return true;
    }

    /**
     * 
     * @param w
     * @param h
     */
    public boolean setImageSize(final float w, final float h) {
        this.setImageWidth(w);
        this.setImageHeight(h);
        return true;
    }

    /**
     * 
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public boolean setImageBounds(final float x, final float y, final float w,
            final float h) {
        this.setImageLocation(x, y);
        this.setImageSize(w, h);
        return true;
    }

    /**
     * 
     * @return
     */
    public SGTuple2f getImageLocation() {
        return new SGTuple2f(this.mX, this.mY);
    }

    public float getImageLocationX() {
        return this.mX;
    }

    public float getImageLocationY() {
        return this.mY;
    }

    /**
     * 
     * @return
     */
    public SGTuple2f getImageSize() {
        return new SGTuple2f(this.mWidth, this.mHeight);
    }

    public float getImageWidth() {
        return this.mWidth;
    }

    public float getImageHeight() {
        return this.mHeight;
    }

    /**
     * 
     * @param image
     */
    public boolean setImage(final Image image) {
        this.mImage = image;
        return true;
    }

    /**
     * 
     * @return
     */
    public boolean setDafaultImageSize() {
        if (this.mImageObserver == null || this.mImage == null) {
            return false;
        }

        Image img = this.mImage;
        ImageObserver obs = this.mImageObserver;
        this.setImageSize(img.getWidth(obs), img.getHeight(obs));
        return true;
    }

    /**
     * Returns the image.
     * 
     * @return
     */
    public Image getImage() {
        return this.mImage;
    }

    /**
     * 
     * @param obs
     */
    public boolean setImageObserver(final ImageObserver obs) {
        this.mImageObserver = obs;
        return true;
    }

    /**
     * 
     * @return
     */
    public ImageObserver getImageObserver() {
        return this.mImageObserver;
    }

    /**
     * 
     * @param mag
     * @return
     */
    public boolean setMagnification(final float mag) {
        this.mMagnification = mag;
        return true;
    }

    public float getMagnification() {
        return this.mMagnification;
    }

    /**
     * 
     * @param f
     * @return
     */
    public boolean setScalingFactor(final float f) {
        if (f < 0.0f) {
            throw new IllegalArgumentException("f<0.0f");
        }
        this.mScalingFactor = f;
        return true;
    }

    public float getScalingFactor() {
        return this.mScalingFactor;
    }

    /**
     * 
     */
    public void drawImage(Graphics g) {
        if (this.mImage == null || this.mImageObserver == null) {
            return;
        }

        final float mag = this.getMagnification();
        final float f = this.getScalingFactor();
        final int x = (int) this.mX;
        final int y = (int) this.mY;
        final int w = (int) (mag * f * this.mWidth);
        final int h = (int) (mag * f * this.mHeight);

        g.drawImage(this.mImage, x, y, w, h, this.mImageObserver);
    }

}

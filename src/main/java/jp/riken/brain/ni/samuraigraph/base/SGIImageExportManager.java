package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Component;
import java.awt.Frame;

/**
 * An interface to export as an image.
 */
public interface SGIImageExportManager {
	
    /**
     * Exports the target object as an image file.
     * 
     * @param target
     *           target object to export
     * @param owner
     *           the owner of the preview dialog
     * @param w
     *           width of the image
     * @param h
     *           height of the image
     * @param silent
     *           the flag for the silent mode
     * @return true if succeeded
     */
    public boolean export(Component target, Frame owner, int w, int h,
            boolean silent);

    /**
     * Preprocesses for exporting an image.
     * 
     * @param wnd
     *           a window
     */
    public void preprocessExport(SGDrawingWindow wnd);

    /**
     * Exports the target object as an image file.
     * 
     * @param target
     *           the target object to export
     * @param type
     *           the image type
     * @param path
     *           the file path to export
     * @param prop
     *           properties of the image
     * @return the result of setting properties
     */
    public SGPropertyResults export(final Component target, final String type,
			final String path, final SGPropertyMap prop);

    /**
	 * Prints the image of the target object.
	 * 
	 * @param target
	 *            target object to export
	 * @param owner
	 *            the owner of the preview dialog
	 * @param w
	 *            width of the image
	 * @param h
	 *            height of the image
	 * @param silent
	 *            the flag for the silent mode
	 * @return true if succeeded
	 */
    public boolean print(Component target, Frame owner, int w, int h,
            boolean silent);

}

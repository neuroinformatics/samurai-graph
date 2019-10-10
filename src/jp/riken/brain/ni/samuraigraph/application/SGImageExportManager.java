package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jp.riken.brain.ni.samuraigraph.base.SGDialog;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIImageExportManager;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.FontConstants;
import org.freehep.graphicsio.ImageConstants;
import org.freehep.graphicsio.ImageGraphics2D;
import org.freehep.graphicsio.InfoConstants;
import org.freehep.graphicsio.PageConstants;
import org.freehep.graphicsio.cgm.CGMGraphics2D;
import org.freehep.graphicsio.emf.EMFGraphics2D;
import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.freehep.graphicsio.ps.PSGraphics2D;
import org.freehep.graphicsio.svg.SVGGraphics2D;
import org.freehep.graphicsio.swf.SWFGraphics2D;
import org.freehep.util.UserProperties;
import org.freehep.util.export.ExportDialog;

/**
 * A class used to preview, print and export an image.
 */
public class SGImageExportManager implements SGIImageExportManager,
        SGIConstants {

    /**
     * The default file name of an exported image.
     */
    private static final String DEFAULT_EXPORT_FILE_NAME = "export";

    /**
     * The title of the export dialog.
     */
    private static final String DEFAULT_TITLE_NAME = "Export view as ...";

    /**
     * The title of the preview dialog.
     */
    private static final String PREVIEW_DIALOG_TITLE = "Export as Image";

    /**
     * Static constants for VectorGraphics
     */
    private static final String PREFIX = ImageGraphics2D.rootKey + ".";

    private static final String BG_SUFFIX = "." + PageConstants.BACKGROUND;

    private static final String BG_COLOR_SUFFIX = "."
            + PageConstants.BACKGROUND_COLOR;

    private static final String[][] VECTOR_BG_KEY_ARRAY = {
            { CGMGraphics2D.BACKGROUND, CGMGraphics2D.BACKGROUND_COLOR },
            { PSGraphics2D.BACKGROUND, PSGraphics2D.BACKGROUND_COLOR },
            { SWFGraphics2D.BACKGROUND, SWFGraphics2D.BACKGROUND_COLOR },
            { PDFGraphics2D.BACKGROUND, PDFGraphics2D.BACKGROUND_COLOR },
            { SVGGraphics2D.BACKGROUND, SVGGraphics2D.BACKGROUND_COLOR },
            { EMFGraphics2D.BACKGROUND, EMFGraphics2D.BACKGROUND_COLOR } };

    // only used for dialog
    private static final String[][] RASTER_BG_KEY_ARRAY = {
        { PREFIX + ImageConstants.BMP.toLowerCase() + BG_SUFFIX, 
    		PREFIX + ImageConstants.BMP.toLowerCase() + BG_COLOR_SUFFIX },
        { PREFIX + ImageConstants.GIF.toLowerCase() + BG_SUFFIX, 
        		PREFIX + ImageConstants.GIF.toLowerCase() + BG_COLOR_SUFFIX },
        { PREFIX + ImageConstants.JPG.toLowerCase() + BG_SUFFIX, 
        		PREFIX + ImageConstants.JPG.toLowerCase() + BG_COLOR_SUFFIX },
        { PREFIX + ImageConstants.JPEG.toLowerCase() + BG_SUFFIX, 
    		PREFIX + ImageConstants.JPEG.toLowerCase() + BG_COLOR_SUFFIX },
        { PREFIX + ImageConstants.PNG.toLowerCase() + BG_SUFFIX, 
        		PREFIX + ImageConstants.PNG.toLowerCase() + BG_COLOR_SUFFIX },
        { PREFIX + ImageConstants.RAW.toLowerCase() + BG_SUFFIX,
        		PREFIX + ImageConstants.RAW.toLowerCase() + BG_COLOR_SUFFIX },
        { PREFIX + ImageConstants.PPM.toLowerCase() + BG_SUFFIX, 
        		PREFIX + ImageConstants.PPM.toLowerCase() + BG_COLOR_SUFFIX } };

    private static final Color DEFAULT_BACKGROUND_COLOR = Color.YELLOW;
    
    /**
     * The command key for Vector graphics.
     * FontConstants.TEXT_AS_SHAPES = "TEXT_AS_SHAPES" does not match other key name style.
     */
    private static final String TEXT_AS_SHAPES = "TextAsShapes";
    
    /**
     * Property key for the resolution.
     */
    public static final String KEY_RESOLUTION = "Resolution";

    /**
     * Property key for rounding image size to the multiple of 8.
     */
    public static final String KEY_ROUND_SIZE_8 = "RoundSize8";

    /**
     * Property key for rounding image size to the multiple of 16.
     */
    public static final String KEY_ROUND_SIZE_16 = "RoundSize16";

    /**
     * Property key for rounding image size to the multiple of 32.
     */
    public static final String KEY_ROUND_SIZE_32 = "RoundSize32";

    /**
     * An export dialog.
     */
    private final ExportDialog mExportDialog = new ExportDialog();

    /**
     *
     */
    private String mBaseDirectoryName = null;

    /**
     *
     */
    private String mExportFileName = null;

    /**
     * The target component.
     */
    private Component mTarget = null;

    /**
     * Default constructor.
     *
     */
    public SGImageExportManager() {
        ExportDialogActionListener bl = new ExportDialogActionListener();
        this.mExportDialog.addActionListener(bl);
    }

    /**
     * Preprocesses for exporting an image.
     *
     * @param wnd
     *           a window
     */
    public void preprocessExport(SGDrawingWindow wnd) {
        String fileName;
        if (wnd != null) {
        	fileName = SGApplicationUtility.getOutputFileName(wnd);
        	if (fileName == null) {
        		fileName = DEFAULT_EXPORT_FILE_NAME;
        	}
        } else {
        	fileName = DEFAULT_EXPORT_FILE_NAME;
        }
        this.mExportFileName = fileName;
    }

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
    public boolean export(Component target, Frame owner, final int w,
            final int h, final boolean silent) {

    	SGDrawingWindow wnd = null;
    	if (owner instanceof SGDrawingWindow) {
    		wnd = (SGDrawingWindow) owner;
    	}

        this.mTarget = target;

        // preview
        if (!silent) {
            if (this.preview(target, owner, w, h, 
                    PREVIEW_DIALOG_TITLE, SGDialog.NEXT_BUTTON_TEXT,
                    SGDialog.CANCEL_BUTTON_TEXT, silent) == CANCEL) {
                return true;
            }
        }

        if (this.mBaseDirectoryName == null) {
            this.mBaseDirectoryName = System.getProperty("user.dir");
        }

        ExportDialog ed = this.mExportDialog;
        Color bg = target.getBackground();
        Properties p = ed.getUserProperties();

        // directory
        String key = ExportDialog.SAVE_AS_FILE;
        String baseDir = this.mBaseDirectoryName;
        String path = ed.getUserProperties().getProperty(key);
        if (path != null) {
            String pathNew = SGApplicationUtility.getPathName(baseDir, this.mExportFileName);
            ed.setUserProperty(key, pathNew);
        }
        String defFile = SGApplicationUtility.getPathName(baseDir, this.mExportFileName);

        // background color
        for (int ii = 0; ii < VECTOR_BG_KEY_ARRAY.length; ii++) {
            String[] array = VECTOR_BG_KEY_ARRAY[ii];
            UserProperties.setProperty(p, array[0], true);
            UserProperties.setProperty(p, array[1], bg);
        }
        for (int ii = 0; ii < RASTER_BG_KEY_ARRAY.length; ii++) {
            String[] array = RASTER_BG_KEY_ARRAY[ii];
            UserProperties.setProperty(p, array[0], true);
            UserProperties.setProperty(p, array[1], bg);
        }

        // set default background color to target component
        target.setBackground(DEFAULT_BACKGROUND_COLOR);

        // show the modal export dialog
        if (wnd != null) {
        	wnd.setModalDialogShown(true);
        }
        ed.showExportDialog(owner, DEFAULT_TITLE_NAME, target, defFile);
        if (wnd != null) {
        	wnd.setModalDialogShown(false);
        }

        // get current directory
        String curPath = p.getProperty(key);
        if (curPath != null) {
            String parent = new File(curPath).getParent();
            String name = curPath.substring(parent.length() + 1);
            this.setBaseDirectory(parent);
            this.mExportFileName = name;
        }

        return true;
    }

    // "International", "A3", "A4", "A5", "A6",
    // "Letter", "Legal", "Executive" or "Ledger";
    private static String[] getSizeList() {
        return PageConstants.getSizeList().clone();
    }

    // "No Margin", "Small", "Medium" or "Large"
    private static String[] getMarginList() {
        return new String[] { PageConstants.NO_MARGIN, PageConstants.SMALL,
                PageConstants.MEDIUM, PageConstants.LARGE };
    }

    // "Portrait" or "Landscape"
    private static String[] getOrientationList() {
        return PageConstants.getOrientationList().clone();
    }

    // "Type1" or "Type3"
    private static String[] getEmbedFontsAsList() {
        return FontConstants.getEmbedFontsAsList().clone();
    }

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
    		final String path, final SGPropertyMap prop) {

    	if (target == null || type == null || path == null || prop == null) {
    		throw new IllegalArgumentException("Null input value.");
    	}

    	SGPropertyResults result = new SGPropertyResults();
        File f = new File(path);

        UserProperties p = new UserProperties();
        
        // set default background color (only for vector type)
        for (int ii = 0; ii < VECTOR_BG_KEY_ARRAY.length; ii++) {
            String[] array = VECTOR_BG_KEY_ARRAY[ii];
            p.setProperty(array[1], DEFAULT_BACKGROUND_COLOR);
        }
        
        VectorGraphics g = null;
        boolean clipFlag = true;
        try {
            if (ImageConstants.EMF.equalsIgnoreCase(type)) {
                g = new EMFGraphics2D(f, target);

                // "Transparent"
                this.setBoolean(prop, PageConstants.TRANSPARENT, p,
                        EMFGraphics2D.TRANSPARENT, result);

                // "Background"
                final boolean bgFlag = this.setBoolean(prop, PageConstants.BACKGROUND, p,
                        EMFGraphics2D.BACKGROUND, result);

                // "BackgroundColor"
                this.setBackgroundColor(prop, PageConstants.BACKGROUND_COLOR, p,
                        EMFGraphics2D.BACKGROUND_COLOR, target, bgFlag, result);

                // "TEXT_AS_SHAPES"
                this.setBoolean(prop, TEXT_AS_SHAPES, p,
                        EMFGraphics2D.TEXT_AS_SHAPES, result);

            } else if ("EPS".equalsIgnoreCase(type) || "PS".equalsIgnoreCase(type)) {
                g = new PSGraphics2D(f, target);
                clipFlag = false;

                // "Background"
                final boolean bgFlag = this.setBoolean(prop, PageConstants.BACKGROUND, p,
                        PSGraphics2D.BACKGROUND, result);

                // "BackgroundColor"
                this.setBackgroundColor(prop, PageConstants.BACKGROUND_COLOR, p,
                        PSGraphics2D.BACKGROUND_COLOR, target, bgFlag, result);

                // "PageSize"
                this.setString(prop, PageConstants.PAGE_SIZE, p,
                        PSGraphics2D.PAGE_SIZE, getSizeList(), result);

                // "PageMargins"
                this.setMargin(prop, PageConstants.PAGE_MARGINS, p,
                        PSGraphics2D.PAGE_MARGINS, result);

                // "Orientation"
                this.setString(prop, PageConstants.ORIENTATION, p,
                        PSGraphics2D.ORIENTATION, getOrientationList(), result);

                // "FitToPage"
                this.setBoolean(prop, PageConstants.FIT_TO_PAGE, p,
                        PSGraphics2D.FIT_TO_PAGE, result);

                // "EmbedFonts"
                this.setBoolean(prop, FontConstants.EMBED_FONTS, p,
                        PSGraphics2D.EMBED_FONTS, result);

                // "EmbedFontsAs"
                this.setString(prop, FontConstants.EMBED_FONTS_AS, p,
                        PSGraphics2D.EMBED_FONTS_AS, getEmbedFontsAsList(), result);

                // "TEXT_AS_SHAPES"
                this.setBoolean(prop, TEXT_AS_SHAPES, p,
                        PSGraphics2D.TEXT_AS_SHAPES, result);

                // "For"
				this.setText(prop, InfoConstants.FOR, p,
						PSGraphics2D.FOR, result);

				// "Title"
				this.setText(prop, InfoConstants.TITLE, p,
						PSGraphics2D.TITLE, result);

                // "WriteImagesAs"
                // "Smallest Size", "ZLIB" or "JPG"
                this.setString(prop, ImageConstants.WRITE_IMAGES_AS, p,
						PSGraphics2D.WRITE_IMAGES_AS, new String[] {
								ImageConstants.SMALLEST, ImageConstants.ZLIB,
								ImageConstants.JPG }, result);

                // only for EPS
                if ("EPS".equalsIgnoreCase(type)) {
                    // "Preview"
                    this.setBoolean(prop, "Preview", p, PSGraphics2D.PREVIEW, result);

                    // "PreviewBits"
                    this.setInteger(prop, "PreviewBits", p,
                            PSGraphics2D.PREVIEW_BITS, new int[] { 1, 2, 4, 8 },
                            result);
                }
                
            } else if (ImageConstants.PDF.equalsIgnoreCase(type)) {
                g = new PDFGraphics2D(f, target);
                clipFlag = false;

                // "Transparent"
                this.setBoolean(prop, PageConstants.TRANSPARENT, p,
                        PDFGraphics2D.TRANSPARENT, result);

                // "Background"
                final boolean bgFlag = this.setBoolean(prop, PageConstants.BACKGROUND, p,
                        PDFGraphics2D.BACKGROUND, result);

                // "BackgroundColor"
                this.setBackgroundColor(prop, PageConstants.BACKGROUND_COLOR, p,
                        PDFGraphics2D.BACKGROUND_COLOR, target, bgFlag, result);

                // "Version"
                this.setString(prop, "Version", p, PDFGraphics2D.VERSION,
                        new String[] { PDFGraphics2D.VERSION4,
                                PDFGraphics2D.VERSION5 }, result);

                // "Thumbnails"
                this.setBoolean(prop, "Thumbnails", p, PDFGraphics2D.THUMBNAILS,
                		result);
//                // set by PDF version
//                String version = p.getProperty(PDFGraphics2D.VERSION);
//                if (version != null) {
//                    p.setProperty(PDFGraphics2D.THUMBNAILS, version
//                            .equals(PDFGraphics2D.VERSION4));
//                }

                // "Compress"
                this.setBoolean(prop, "Compress", p, PDFGraphics2D.COMPRESS, result);

                // "PageSize"
                this.setString(prop, PageConstants.PAGE_SIZE, p,
                        PDFGraphics2D.PAGE_SIZE, getSizeList(), result);

                // "PageMargins"
                this.setMargin(prop, PageConstants.PAGE_MARGINS, p,
                        PDFGraphics2D.PAGE_MARGINS, result);

                // "Orientation"
                this.setString(prop, PageConstants.ORIENTATION, p,
                        PDFGraphics2D.ORIENTATION, getOrientationList(), result);

                // "FitToPage"
                this.setBoolean(prop, PageConstants.FIT_TO_PAGE, p,
                        PDFGraphics2D.FIT_TO_PAGE, result);

                // "EmbedFonts"
                this.setBoolean(prop, FontConstants.EMBED_FONTS, p,
                        PDFGraphics2D.EMBED_FONTS, result);

                // "EmbedFontsAs"
                this.setString(prop, FontConstants.EMBED_FONTS_AS, p,
                        PDFGraphics2D.EMBED_FONTS_AS, getEmbedFontsAsList(), result);

                // "TEXT_AS_SHAPES"
                this.setBoolean(prop, TEXT_AS_SHAPES, p,
                        PDFGraphics2D.TEXT_AS_SHAPES, result);

                // "WriteImagesAs"
                // "Smallest Size", "ZLIB" or "JPG"
                this.setString(prop, ImageConstants.WRITE_IMAGES_AS, p,
                        PDFGraphics2D.WRITE_IMAGES_AS, new String[] {
                                ImageConstants.SMALLEST, ImageConstants.ZLIB,
                                ImageConstants.JPG }, result);

                // "Author"
                this.setText(prop, InfoConstants.AUTHOR, p,
                        PDFGraphics2D.AUTHOR, result);

                // "Title"
                this.setText(prop, InfoConstants.TITLE, p,
                        PDFGraphics2D.TITLE, result);

                // "Subject"
                this.setText(prop, InfoConstants.SUBJECT, p,
                        PDFGraphics2D.SUBJECT, result);

                // "Keywords"
                this.setText(prop, InfoConstants.KEYWORDS, p,
                        PDFGraphics2D.KEYWORDS, result);

            } else if (ImageConstants.SVG.equalsIgnoreCase(type)) {
                g = new SVGGraphics2D(f, target);
                clipFlag = true;

                // "Transparent"
                this.setBoolean(prop, PageConstants.TRANSPARENT, p,
                        SVGGraphics2D.TRANSPARENT, result);

                // "Background"
                final boolean bgFlag = this.setBoolean(prop, PageConstants.BACKGROUND, p,
                        SVGGraphics2D.BACKGROUND, result);

                // "BackgroundColor"
                this.setBackgroundColor(prop, PageConstants.BACKGROUND_COLOR, p,
                        SVGGraphics2D.BACKGROUND_COLOR, target, bgFlag, result);

                // "Version"
                // disabled in freeHep
//                this.setString(prop, "Version", p, SVGGraphics2D.VERSION,
//                        new String[] { SVGGraphics2D.VERSION_1_0,
//                                SVGGraphics2D.VERSION_1_1 });

                // "Compress"
                this.setBoolean(prop, "Compress", p, SVGGraphics2D.COMPRESS, result);

                // "Stylable"
                this.setBoolean(prop, "Stylable", p, SVGGraphics2D.STYLABLE, result);

                // "ImageSize"
                this.setDimension(prop, ImageConstants.IMAGE_SIZE, p,
                        SVGGraphics2D.IMAGE_SIZE, result);

                // "ExportImages"
                this.setBoolean(prop, "ExportImages", p,
                        SVGGraphics2D.EXPORT_IMAGES, result);

                // "ExportSuffix"
                this.setText(prop, "ExportSuffix", p,
                        SVGGraphics2D.EXPORT_SUFFIX, result);

                // "WriteImagesAs"
                // "Smallest Size", "PNG" or "JPG"
                this.setString(prop, ImageConstants.WRITE_IMAGES_AS, p,
                        SVGGraphics2D.WRITE_IMAGES_AS, new String[] {
                                ImageConstants.SMALLEST, ImageConstants.PNG,
                                ImageConstants.JPG }, result);

                // "TEXT_AS_SHAPES"
                this.setBoolean(prop, TEXT_AS_SHAPES, p,
                        SVGGraphics2D.TEXT_AS_SHAPES, result);

                // "Creator"
                this.setText(prop, "Creator", p, SVGGraphics2D.FOR, result);

                // "Title"
                this.setText(prop, InfoConstants.TITLE, p,
                        SVGGraphics2D.TITLE, result);

            } else if ("CGM".equalsIgnoreCase(type)) {
                g = new CGMGraphics2D(f, target);
                clipFlag = true;

                // "Background"
                final boolean bgFlag = this.setBoolean(prop, PageConstants.BACKGROUND, p,
                        CGMGraphics2D.BACKGROUND, result);

                // "BackgroundColor"
                this.setBackgroundColor(prop, PageConstants.BACKGROUND_COLOR, p,
                        CGMGraphics2D.BACKGROUND_COLOR, target, bgFlag, result);

                // "Binary"
                this.setBoolean(prop, "Binary", p, CGMGraphics2D.BINARY, result);

                // "Author"
                this.setText(prop, InfoConstants.AUTHOR, p,
                        CGMGraphics2D.AUTHOR, result);

                // "Title"
                this.setText(prop, InfoConstants.TITLE, p,
                        CGMGraphics2D.TITLE, result);

                // "Subject"
                this.setText(prop, InfoConstants.SUBJECT, p,
                        CGMGraphics2D.SUBJECT, result);

                // "Keywords"
                this.setText(prop, InfoConstants.KEYWORDS, p,
                        CGMGraphics2D.KEYWORDS, result);

            } else if (ImageConstants.SWF.equalsIgnoreCase(type)) {
                g = new SWFGraphics2D(f, target);
                clipFlag = true;

                // "Transparent"
                this.setBoolean(prop, PageConstants.TRANSPARENT, p,
                        SWFGraphics2D.TRANSPARENT, result);

                // "Background"
                final boolean bgFlag = this.setBoolean(prop, PageConstants.BACKGROUND, p,
                        SWFGraphics2D.BACKGROUND, result);

                // "BackgroundColor"
                this.setBackgroundColor(prop, PageConstants.BACKGROUND_COLOR, p,
                        SWFGraphics2D.BACKGROUND_COLOR, target, bgFlag, result);

                // "WriteImagesAs"
                // "Smallest Size", "ZLIB" or "JPG"
                this.setString(prop, ImageConstants.WRITE_IMAGES_AS, p,
                        SWFGraphics2D.WRITE_IMAGES_AS, new String[] {
                                ImageConstants.SMALLEST, ImageConstants.ZLIB,
                                ImageConstants.JPG }, result);

            } else if (ImageConstants.JPG.equalsIgnoreCase(type) 
            		|| ImageConstants.JPEG.equalsIgnoreCase(type)
                    || ImageConstants.GIF.equalsIgnoreCase(type) 
                    || ImageConstants.PNG.equalsIgnoreCase(type)
                    || ImageConstants.RAW.equalsIgnoreCase(type) 
                    || ImageConstants.PPM.equalsIgnoreCase(type)
                    || ImageConstants.BMP.equalsIgnoreCase(type)) {

            	// checks whether the BMP format is supported
                if (ImageConstants.BMP.equalsIgnoreCase(type)) {
                	boolean found = false;
                	String[] writerFormatNames = ImageIO.getWriterFormatNames();
                	for (String name : writerFormatNames) {
                		if (ImageConstants.BMP.equalsIgnoreCase(name)) {
                			found = true;
                			break;
                		}
                	}
                	if (!found) {
                		return null;
                	}
                }
                
                clipFlag = true;
            	final String uType = type.toUpperCase();

            	// round
            	final int roundNum = this.getRoundNumber(prop);

				// "Resolution"
                String strResolution = prop.getValue(KEY_RESOLUTION);
                if (strResolution != null) {
                    boolean valid = true;
                    Double resolution = SGUtilityText.getDouble(strResolution);
    				if (resolution != null) {
    					if (resolution > 0.0) {
        	                g = new ImageGraphics2D(f, target, uType, resolution / 72.0, roundNum);
    					} else {
    						valid = false;
    					}
    				} else {
    					valid = false;
    				}
    				final int status = valid ? SGPropertyResults.SUCCEEDED : SGPropertyResults.INVALID_INPUT_VALUE;
    	            result.putResult(KEY_RESOLUTION, status);
                }
                if (g == null) {
	                g = new ImageGraphics2D(f, target, uType, 1.0, roundNum);
                }

                String formatKey = ImageGraphics2D.rootKey + "." + uType;

                // set default background color for raster type
                p.setProperty(formatKey + ImageGraphics2D.BACKGROUND_COLOR, Color.WHITE);

                // for the transparent format
                if (ImageGraphics2D.canWriteTransparent(uType)) {
                    // "Transparent"
                    this.setBoolean(prop, PageConstants.TRANSPARENT, p,
                            ImageGraphics2D.TRANSPARENT, result);
                }

                // "Background"
                final boolean bgFlag = this.setBoolean(prop, PageConstants.BACKGROUND, p, 
                		formatKey + ImageGraphics2D.BACKGROUND, result);

                // "BackgroundColor"
                this.setBackgroundColor(prop, PageConstants.BACKGROUND_COLOR, p,
                        formatKey + ImageGraphics2D.BACKGROUND_COLOR, target, bgFlag, result);

                // "Antialias"
                this.setBoolean(prop, "Antialias", p, formatKey
                        + ImageGraphics2D.ANTIALIAS, result);

                // "AntialiasText"
                this.setBoolean(prop, "AntialiasText", p, formatKey
                        + ImageGraphics2D.ANTIALIAS_TEXT, result);

                if (ImageConstants.JPG.equalsIgnoreCase(type) 
                		|| ImageConstants.JPEG.equalsIgnoreCase(type)
                        || ImageConstants.GIF.equalsIgnoreCase(type) 
                        || ImageConstants.PNG.equalsIgnoreCase(type)) {
                    // "Progressive"
    				this.setBoolean(prop, "Progressive", p, ImageGraphics2D.PROGRESSIVE,
    						result);
                }

				// "QualityValue"
				this.setFloat(prop, "QualityValue", p, ImageGraphics2D.COMPRESS_QUALITY,
						new SGTuple2f(0.0f, 1.0f), result);

            } else {
                return null;
            }

            g.setProperties(p);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
        	e.printStackTrace();
        	return null;
        }

        if (target instanceof SGDrawingWindow.ExportPanel) {
            SGDrawingWindow.ExportPanel exportPanel = (SGDrawingWindow.ExportPanel) target;
            exportPanel.setClipFlag(clipFlag);
        }

        g.startExport();
        target.print(g);
        g.endExport();

        return result;
    }
    
    private int getRoundNumber(SGPropertyMap prop) {
		String roundSize8Str = prop.getValue(KEY_ROUND_SIZE_8);
		Boolean roundSize8 = (roundSize8Str != null) 
				? SGUtilityText.getBoolean(roundSize8Str) : null;
		String roundSize16Str = prop.getValue(KEY_ROUND_SIZE_16);
		Boolean roundSize16 = (roundSize16Str != null) 
				? SGUtilityText.getBoolean(roundSize16Str) : null;
		String roundSize32Str = prop.getValue(KEY_ROUND_SIZE_32);
		Boolean roundSize32 = (roundSize32Str != null) 
				? SGUtilityText.getBoolean(roundSize32Str) : null;
		final int roundNum;
		if (roundSize32 != null && roundSize32) {
			roundNum = 32;
		} else if (roundSize16 != null && roundSize16) {
			roundNum = 16;
		} else if (roundSize8 != null && roundSize8) {
			roundNum = 8;
		} else {
			roundNum = 1;
		}
		return roundNum;
    }
    
    /**
     * Sets a text string to the image properties.
     *
     * @param prop
     *           the map input properties
     * @param propKey
     *           the key for the map of input properties
     * @param p
     *           the image properties
     * @param key
     *           the key for the image properties
     * @param result
     *           the results of setting properties
     * @return true if succeeded
     */
    private boolean setString(final SGPropertyMap prop, final String propKey,
            final UserProperties p, final String key,
            final SGPropertyResults result) {
    	String value = prop.getValue(propKey);
        if (value != null) {
        	String str = prop.getValueString(propKey);
            p.setProperty(key, str);
            result.putResult(propKey, SGPropertyResults.SUCCEEDED);
            return true;
        }
        return false;
    }

    /**
     * Sets a text string to the image properties from given candidates.
     *
     * @param prop
     *           the map input properties
     * @param propKey
     *           the key for the map of input properties
     * @param p
     *           the image properties
     * @param key
     *           the key for an image property
     * @param candidates
     *           the candidates of values for an image property
     * @param result
     *           the results of setting properties
     * @return true if succeeded
     */
    private boolean setString(final SGPropertyMap prop, final String propKey,
            final UserProperties p, final String key, final String[] candidates,
            final SGPropertyResults result) {
        String value = prop.getValue(propKey);
        if (value != null) {
        	String str = prop.getValueString(propKey);
            for (int ii = 0; ii < candidates.length; ii++) {
                if (SGUtilityText.isEqualString(candidates[ii], str)) {
                    p.setProperty(key, candidates[ii]);
                    result.putResult(propKey, SGPropertyResults.SUCCEEDED);
                    return true;
                }
            }
            result.putResult(propKey, SGPropertyResults.INVALID_INPUT_VALUE);
            return false;
        }
        return false;
    }

    /**
     * Sets a text string to the image properties. Given string must be double quoted.
     *
     * @param prop
     *           the map input properties
     * @param propKey
     *           the key for the map of input properties
     * @param p
     *           the image properties
     * @param key
     *           the key for the image properties
     * @param result
     *           the results of setting properties
     * @return true if succeeded
     */
    private boolean setText(final SGPropertyMap prop, final String propKey,
            final UserProperties p, final String key,
            final SGPropertyResults result) {
    	String value = prop.getValue(propKey);
        if (value != null) {
        	if (prop.isDoubleQuoted(propKey) == false) {
                result.putResult(propKey, SGPropertyResults.INVALID_INPUT_VALUE);
        		return false;
        	}
        	String str = prop.getValueString(propKey);
            p.setProperty(key, str);
            result.putResult(propKey, SGPropertyResults.SUCCEEDED);
            return true;
        }
        return false;
    }

    /**
     * Sets a boolean value to the image properties.
     *
     * @param prop
     *           the map input properties
     * @param propKey
     *           the key for the map of input properties
     * @param p
     *           the image properties
     * @param key
     *           the key for the image properties
     * @param result
     *           the results of setting properties
     * @return true if succeeded
     */
    private boolean setBoolean(final SGPropertyMap prop, final String propKey,
            final UserProperties p, final String key, final SGPropertyResults result) {
        String value = prop.getValue(propKey);
        if (value != null) {
            Boolean b = SGUtilityText.getBoolean(value);
            if (b == null) {
                result.putResult(propKey, SGPropertyResults.INVALID_INPUT_VALUE);
                return false;
            }
            p.setProperty(key, b.booleanValue());
            result.putResult(propKey, SGPropertyResults.SUCCEEDED);
            return true;
        }
        return false;
    }

    /**
     * Sets an integer value to the image properties.
     *
     * @param prop
     *           the map input properties
     * @param propKey
     *           the key for the map of input properties
     * @param p
     *           the image properties
     * @param key
     *           the key for the image properties
     * @param result
     *           the results of setting properties
     * @return true if succeeded
     */
    private boolean setInteger(final SGPropertyMap prop, final String propKey,
            final UserProperties p, final String key, final SGPropertyResults result) {
        String value = prop.getValue(propKey);
        if (value != null) {
            Integer n = SGUtilityText.getInteger(value);
            if (n == null) {
                result.putResult(propKey, SGPropertyResults.INVALID_INPUT_VALUE);
                return false;
            }
            p.setProperty(key, n.intValue());
            result.putResult(propKey, SGPropertyResults.SUCCEEDED);
            return true;
        }
        return false;
    }

    /**
     * Sets an integer value to the image properties with candidates.
     *
     * @param prop
     *           the map input properties
     * @param propKey
     *           the key for the map of input properties
     * @param p
     *           the image properties
     * @param key
     *           the key for the image properties
     * @param candidates
     *           the candidates for an image property
     * @param result
     *           the results of setting properties
     * @return true if succeeded
     */
    private boolean setInteger(final SGPropertyMap prop, final String propKey,
            final UserProperties p, final String key, final int[] candidates,
            final SGPropertyResults result) {
        String value = prop.getValue(propKey);
        if (value != null) {
            Integer n = SGUtilityText.getInteger(value);
            if (n == null) {
                result.putResult(propKey, SGPropertyResults.INVALID_INPUT_VALUE);
                return false;
            }
            for (int ii = 0; ii < candidates.length; ii++) {
            	if (candidates[ii] == n.intValue()) {
                    p.setProperty(key, n.intValue());
                    result.putResult(propKey, SGPropertyResults.SUCCEEDED);
                    return true;
            	}
            }
        }
        return false;
    }

    /**
     * Sets a float value to the image properties.
     *
     * @param prop
     *           the map input properties
     * @param propKey
     *           the key for the map of input properties
     * @param p
     *           the image properties
     * @param key
     *           the key for the image properties
     * @param result
     *           the results of setting properties
     * @return true if succeeded
     */
    private boolean setFloat(final SGPropertyMap prop, final String propKey,
            final UserProperties p, final String key, final SGPropertyResults result) {
        String value = prop.getValue(propKey);
        if (value != null) {
            Float f = SGUtilityText.getFloat(value);
            if (f == null) {
                result.putResult(propKey, SGPropertyResults.INVALID_INPUT_VALUE);
                return false;
            }
            p.setProperty(key, f.floatValue());
            result.putResult(propKey, SGPropertyResults.SUCCEEDED);
            return true;
        }
        return false;
    }

    /**
     * Sets a float value to the image properties within given value range.
     *
     * @param prop
     *           the map input properties
     * @param propKey
     *           the key for the map of input properties
     * @param p
     *           the image properties
     * @param key
     *           the key for the image properties
     * @param range
     *           value range
     * @param result
     *           the results of setting properties
     * @return true if succeeded
     */
    private boolean setFloat(final SGPropertyMap prop, final String propKey,
            final UserProperties p, final String key, final SGTuple2f range,
            final SGPropertyResults result) {
        String value = prop.getValue(propKey);
        if (value != null) {
            Float f = SGUtilityText.getFloat(value);
            if (f == null) {
                result.putResult(propKey, SGPropertyResults.INVALID_INPUT_VALUE);
                return false;
            }
            if (f < range.x || range.y < f) {
                result.putResult(propKey, SGPropertyResults.INVALID_INPUT_VALUE);
            	return false;
            }
            p.setProperty(key, f.floatValue());
            result.putResult(propKey, SGPropertyResults.SUCCEEDED);
            return true;
        }
        return false;
    }

    /**
     * Sets a color to the image properties.
     *
     * @param prop
     *           the map input properties
     * @param propKey
     *           the key for the map of input properties
     * @param p
     *           the image properties
     * @param key
     *           the key for the image properties
     * @param target
     *           target component
     * @param bgFlag
     *           true to set background color
     * @param result
     *           the results of setting properties
     * @return true if succeeded
     */
    private boolean setBackgroundColor(final SGPropertyMap prop, final String propKey,
            final UserProperties p, final String key, final Component target,
            final boolean bgFlag, final SGPropertyResults result) {
    	
        String value = prop.getValue(propKey);
        if (value != null) {
            Color cl = SGUtilityText.getColor(value);
            if (cl == null) {
                cl = SGUtilityText.parseColor(value);
                if (cl == null) {
                	// invalid input for color
                    result.putResult(propKey, SGPropertyResults.INVALID_INPUT_VALUE);
                    
                    if (bgFlag) {
                        p.setProperty(key, target.getBackground());
                    } else {
                        p.setProperty(key, DEFAULT_BACKGROUND_COLOR);
	                    target.setBackground(DEFAULT_BACKGROUND_COLOR);
                    }
                    
                	return false;
                }
            }
            
            if (bgFlag) {
                p.setProperty(key, cl);
                target.setBackground(cl);
            }

            result.putResult(propKey, SGPropertyResults.SUCCEEDED);

            return true;
        }

        if (bgFlag) {
            p.setProperty(key, target.getBackground());
        } else {
            p.setProperty(key, DEFAULT_BACKGROUND_COLOR);
            target.setBackground(DEFAULT_BACKGROUND_COLOR);
        }

        return false;
    }

    /**
     * Sets a dimension to the image properties.
     *
     * @param prop
     *           the map input properties
     * @param propKey
     *           the key for the map of input properties
     * @param p
     *           the image properties
     * @param key
     *           the key for the image properties
     * @param result
     *           the results of setting properties
     * @return true if succeeded
     */
    private boolean setDimension(final SGPropertyMap prop, final String propKey,
            final UserProperties p, final String key, final SGPropertyResults result) {
        String value = prop.getValue(propKey);
        if (value != null) {
            Dimension dim = SGUtilityText.getDimension(value);
            if (dim == null) {
                result.putResult(propKey, SGPropertyResults.INVALID_INPUT_VALUE);
                return false;
            }
            p.setProperty(key, dim);
            result.putResult(propKey, SGPropertyResults.SUCCEEDED);
            return true;
        }
        return false;
    }

    /**
     * Sets the margin to the image properties.
     *
     * @param prop
     *           the map input properties
     * @param propKey
     *           the key for the map of input properties
     * @param p
     *           the image properties
     * @param key
     *           the key for the image properties
     * @param result
     *           the results of setting properties
     * @return true if succeeded
     */
    private boolean setMargin(final SGPropertyMap prop, final String propKey,
            final UserProperties p, final String key, final SGPropertyResults result) {
        String value = prop.getValue(propKey);
        if (value != null) {
        	String[] marginTextArray = SGUtilityText.getStringsInBracket(value);
        	if (marginTextArray != null) {
        		// in the format as (top, bottom, left, right)
        		if (marginTextArray.length != 4) {
                    result.putResult(propKey, SGPropertyResults.INVALID_INPUT_VALUE);
                    return false;
        		}
        		int[] marginValues = new int[marginTextArray.length];
        		for (int ii = 0; ii < marginValues.length; ii++) {
        			Integer num = SGUtilityText.getInteger(marginTextArray[ii]);
        			if (num == null) {
                        result.putResult(propKey, SGPropertyResults.INVALID_INPUT_VALUE);
                        return false;
        			}
        			marginValues[ii] = num.intValue();
        		}
        		final int top = marginValues[0];
        		final int bottom = marginValues[1];
        		final int left = marginValues[2];
        		final int right = marginValues[3];
        		Insets in = new Insets(top, left, bottom, right);
                p.setProperty(key, in);
                result.putResult(propKey, SGPropertyResults.SUCCEEDED);
                return true;
        	} else {
        		// constants: No Margin, Small, Medium or Large
            	String[] margins = getMarginList();
            	for (int ii = 0; ii < margins.length; ii++) {
            		if (SGUtilityText.isEqualString(margins[ii], value)) {
                        Insets in = PageConstants.getMargins(margins[ii]);
                        p.setProperty(key, in);
                        result.putResult(propKey, SGPropertyResults.SUCCEEDED);
                        return true;
            		}
            	}
        	}
            result.putResult(propKey, SGPropertyResults.INVALID_INPUT_VALUE);
            return false;
        }
        return false;
    }

    // An action listener class for the export file dialog.
    private class ExportDialogActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source instanceof JComboBox) {
                JComboBox cb = (JComboBox) source;
                Object obj = cb.getSelectedItem();

                Component target = SGImageExportManager.this.mTarget;
                if (target instanceof SGDrawingWindow.ExportPanel) {
                    SGDrawingWindow.ExportPanel p = (SGDrawingWindow.ExportPanel) target;
                    final boolean b = (obj instanceof org.freehep.graphicsio.ps.EPSExportFileType)
                            || (obj instanceof org.freehep.graphicsio.ps.PSExportFileType)
                            || (obj instanceof org.freehep.graphicsio.pdf.PDFExportFileType);
                    p.setClipFlag(!b);
                }
            }
        }
    }

    /**
     *
     * @param dir
     */
    public void setBaseDirectory(String dir) {
        this.mBaseDirectoryName = dir;
    }

    /**
     *
     * @return
     */
    public String getBaseDirectory() {
        return this.mBaseDirectoryName;
    }

    /**
     * The OK button is pressed.
     */
    public static final int OK = 0;

    /**
     * The Cancel button is pressed or the window is closed.
     */
    public static final int CANCEL = 1;

    /**
     * Shows the preview image of the target object.
     *
     * @param target
     *          target object for export
     * @param owner
     *          owner object
     * @param w
     *          width of image
     * @param h
     *          height of image
     * @return status
     */
    private int preview(Component target, Frame owner, final int w, 
    		final int h, final String title, final String textOK, 
    		final String textCancel, final boolean silent) {

        // create and show the preview dialog
        SGPreviewDialog dg = new SGPreviewDialog(owner, title, !silent);
        dg.setTargetObject(target, w, h, target.getBackground());
        dg.setSize(owner.getSize());
        dg.setLocation(owner.getLocation());
        dg.setOKButtonText(textOK);
        dg.setCancelButtonText(textCancel);
        if (!silent) {
            dg.setVisible(true);
        }

        final int ret = dg.getCloseOption();

        // dispose preview window
        dg.dispose();

        if (ret == SGIConstants.CANCEL_OPTION) {
            return CANCEL;
        }

        return OK;
    }

    /**
     * Prints the image of the target object.
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
    public boolean print(final Component target, final Frame owner,
            final int w, final int h, final boolean silent) {

        // gets printer job
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        
        // finds printer services
        PrintService[] services = PrinterJob.lookupPrintServices();
        if (services.length <= 0) {
            if (!silent) {
                SGUtility.showMessageDialog(owner, "No printer is available.",
                        SGIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
        
        // shows the page dialog
        PageFormat defaultFormat = (PageFormat) printerJob.defaultPage().clone();
        Paper paper = defaultFormat.getPaper();
        paper.setImageableArea(0.0, 0.0, paper.getWidth(), paper.getHeight());
        defaultFormat.setPaper(paper);
        PageFormat pageFormat = printerJob.pageDialog(defaultFormat);
        if (defaultFormat.equals(pageFormat)) {
        	// canceled
        	return true;
        }
        final int nWidth = (int) pageFormat.getWidth();
        final int nHeight = (int) pageFormat.getHeight();

    	// creates a printable panel
        PrintablePanel panel = new PrintablePanel(target, nWidth, nHeight);
        
        // preview
		final int ret = this.preview(panel, owner, nWidth, nHeight, "Print as Image",
				SGDialog.NEXT_BUTTON_TEXT, SGDialog.CANCEL_BUTTON_TEXT, silent);
        if (ret == CANCEL) {
        	// canceled
            return true;
        }

        // sets printable object
        printerJob.setPrintable(panel, pageFormat);

        // starts printing
        if (printerJob.printDialog()) {
            try {
                printerJob.print();
            } catch (PrinterException ex) {
                SGUtility.showMessageDialog(owner, "Printing failed.",
                        SGIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
            	return false;
            }
        }

        return true;
    }

    private class PrintablePanel extends JPanel implements Printable {

        private static final long serialVersionUID = -5904811006944374494L;

        public PrintablePanel(Component target, int width, int height) {
            super();
            setLayout(null);
            setSize(width, height);
            add(target);
            setOpaque(true);
            setBackground(target.getBackground());
        }

        public int print(Graphics g, PageFormat pageFormat, int pageIndex)
                throws PrinterException {
            if (pageIndex >= 1) {
                return Printable.NO_SUCH_PAGE;
            }
            printAll(g);
            return Printable.PAGE_EXISTS;
        }
    }

}

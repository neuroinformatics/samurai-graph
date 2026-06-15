package jp.riken.brain.ni.samuraigraph.export;

import org.freehep.graphicsio.exportchooser.ImageExportFileType;

/** PNG export file type using javax.imageio.ImageIO. */
public class PNGExportFileType extends ImageExportFileType {

  /** Constructs a PNG export file type. */
  public PNGExportFileType() {
    super("png");
  }

  @Override
  public String getDescription() {
    return "PNG Image";
  }

  @Override
  public String[] getExtensions() {
    return new String[] {"png"};
  }

  @Override
  public String[] getMIMETypes() {
    return new String[] {"image/png"};
  }
}

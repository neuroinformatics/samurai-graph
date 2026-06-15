package jp.riken.brain.ni.samuraigraph.export;

import org.freehep.graphicsio.exportchooser.ImageExportFileType;

/** JPEG export file type using javax.imageio.ImageIO. */
public class JPEGExportFileType extends ImageExportFileType {

  /** Constructs a JPEG export file type. */
  public JPEGExportFileType() {
    super("jpeg");
  }

  @Override
  public String getDescription() {
    return "JPEG Image";
  }

  @Override
  public String[] getExtensions() {
    return new String[] {"jpg", "jpeg"};
  }

  @Override
  public String[] getMIMETypes() {
    return new String[] {"image/jpeg"};
  }
}

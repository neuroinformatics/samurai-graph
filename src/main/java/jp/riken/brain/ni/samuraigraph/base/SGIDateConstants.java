package jp.riken.brain.ni.samuraigraph.base;

public final class SGIDateConstants {

  private SGIDateConstants() {
    // uninstantiable utility class
  }

  /** Array of date format for displaying a date string. */
  public static final String[] DEFAULT_DATE_DISPLAY_FORMAT_ARRAY = {
    "",
    "yyyy-MM-dd",
    "yyyy/MM/dd",
    "yyyy.MM.dd",
    "yyyy-MM-dd'T'HH:SGIConstants.mm:ssZZ",
    "aK:SGIConstants.mm",
    "HH:SGIConstants.mm:ss"
  };
}

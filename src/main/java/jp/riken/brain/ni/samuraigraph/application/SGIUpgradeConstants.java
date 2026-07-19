package jp.riken.brain.ni.samuraigraph.application;

/** Constants for the upgrading of the application. */
public interface SGIUpgradeConstants {

  /** GitHub API URL for the latest release. */
  public static final String GITHUB_API_LATEST_URL =
      "https://api.github.com/repos/neuroinformatics/samurai-graph/releases/latest";

  /** Base URL for raw GitHub content. */
  public static final String GITHUB_RAW_BASE_URL =
      "https://raw.githubusercontent.com/neuroinformatics/samurai-graph";

  /** GitHub Releases page URL. */
  public static final String GITHUB_RELEASES_URL =
      "https://github.com/neuroinformatics/samurai-graph/releases";

  /** Not upgrading automatically. */
  public static final int NO_UPGRADE = 0;

  public static final int UPGRADE_EVERY_TIME = 1;

  public static final int UPGRADE_EVERY_DAY = 2;

  public static final int UPGRADE_EVERY_WEEK = 3;

  public static final int UPGRADE_EVERY_MONTH = 4;
}

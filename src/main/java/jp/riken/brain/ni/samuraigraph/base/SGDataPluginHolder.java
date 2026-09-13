package jp.riken.brain.ni.samuraigraph.base;

import java.util.ArrayList;
import java.util.List;

/**
 * A single holder of the data plug-in list and the data plug-in manager, shared application-wide.
 */
public final class SGDataPluginHolder {

  private SGDataPluginHolder() {}

  private static List<SGIPlugin> mDataPluginList = new ArrayList<SGIPlugin>();

  private static SGIPluginManager mDataPluginManager = null;

  /**
   * Sets the list of data plug-in.
   *
   * @param pluginList the list of data plug-in
   */
  public static void setDataPlugins(final List<SGIPlugin> pluginList) {
    mDataPluginList = new ArrayList<SGIPlugin>(pluginList);
  }

  /**
   * Returns the list of data plug-in.
   *
   * @return the list of data plug-in
   */
  public static List<SGIPlugin> getDataPlugins() {
    return mDataPluginList;
  }

  /**
   * Sets the data plug-in manager.
   *
   * @param m the data plug-in manager
   */
  public static void setDataPluginManager(final SGIPluginManager m) {
    mDataPluginManager = m;
  }

  /**
   * Returns the data plug-in manager.
   *
   * @return the data plug-in manager
   */
  public static SGIPluginManager getDataPluginManager() {
    return mDataPluginManager;
  }
}

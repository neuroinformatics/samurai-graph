package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import jp.riken.brain.ni.samuraigraph.base.SGDialog;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A class to manage upgrade of the application.
 *
 * <p>Queries the GitHub Releases API for the latest release. If a newer version is found, fetches
 * the changelog from the tagged source tree and opens a confirmation dialog. On confirmation, opens
 * the GitHub Releases page in the default browser.
 */
class SGUpgradeManager
    implements ActionListener, SGIConstants, SGIUpgradeConstants, SGIPreferencesConstants {

  private static final String MSG_LATEST_VERSION_INSTALLED =
      "The latest version is already installed.";

  private static final String MSG_NEW_VERSION_FOUND_BEFORE = "Samurai Graph Ver.";

  private static final String MSG_NEW_VERSION_FOUND_AFTER = " is found.\nDownload now?\n";

  private static final String MSG_CHECK_FAILED =
      "Failed to check for updates.\nPlease check your network connection.";

  private static final String MSG_BROWSER_FAILED =
      "Failed to open the browser.\nPlease visit the following URL manually:\n";

  private static final String MAJOR_VER = "majorver";

  private static final String MINOR_VER = "minorver";

  private static final String MICRO_VER = "microver";

  private static final String TAG_NAME_PATTERN = "v(\\d+)\\.(\\d+)\\.(\\d+)";

  /** Pattern to extract "tag_name" value from GitHub API JSON response. */
  private static final Pattern TAG_NAME_JSON_PATTERN =
      Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");

  /** */
  private SGUpgradeDialog mUpgradeDialog;

  /** */
  private SGProxyManager mProxyManager;

  /** application properties */
  private SGApplicationProperties mAppProp;

  /** */
  public SGUpgradeManager(final SGProxyManager p, final SGApplicationProperties ap) {
    this.mProxyManager = p;
    this.mAppProp = ap;
  }

  /** */
  public void actionPerformed(final ActionEvent e) {
    Object source = e.getSource();
    String command = e.getActionCommand();

    int type;
    if (command.equals(SGDialog.OK_BUTTON_TEXT)) {
      String cycle = this.mUpgradeDialog.getUpgradeCycle();

      if (cycle.equals(SGUpgradeDialog.NO_UPGRADE)) {
        type = NO_UPGRADE;
      } else if (cycle.equals(SGUpgradeDialog.EVERY_TIME)) {
        type = UPGRADE_EVERY_TIME;
      } else if (cycle.equals(SGUpgradeDialog.EVERY_DAY)) {
        type = UPGRADE_EVERY_DAY;
      } else if (cycle.equals(SGUpgradeDialog.EVERY_WEEK)) {
        type = UPGRADE_EVERY_WEEK;
      } else if (cycle.equals(SGUpgradeDialog.EVERY_MONTH)) {
        type = UPGRADE_EVERY_MONTH;
      } else {
        throw new Error();
      }

      // update the upgrade cycle
      Preferences pref = Preferences.userNodeForPackage(this.getClass());
      pref.putInt(PREF_KEY_UPGRADE_CYCLE, type);

    } else if (command.equals(SGUpgradeDialog.UPGRADE_NOW)) {
      SGUpgradeDialog dg = (SGUpgradeDialog) source;
      this.upgradeByCommand(dg);
    }
  }

  /**
   * @param owner
   * @return
   */
  public boolean showUpgradeDialog(Frame owner) {
    this.mUpgradeDialog = new SGUpgradeDialog(owner, true);
    this.mUpgradeDialog.addActionListener(this);

    this.mUpgradeDialog.setCenter(owner);

    Preferences pref = Preferences.userNodeForPackage(this.getClass());
    final int cycleType = pref.getInt(PREF_KEY_UPGRADE_CYCLE, NO_UPGRADE);

    String cycle = null;
    switch (cycleType) {
      case NO_UPGRADE:
        {
          cycle = SGUpgradeDialog.NO_UPGRADE;
          break;
        }

      case UPGRADE_EVERY_TIME:
        {
          cycle = SGUpgradeDialog.EVERY_TIME;
          break;
        }

      case UPGRADE_EVERY_DAY:
        {
          cycle = SGUpgradeDialog.EVERY_DAY;
          break;
        }

      case UPGRADE_EVERY_WEEK:
        {
          cycle = SGUpgradeDialog.EVERY_WEEK;
          break;
        }

      case UPGRADE_EVERY_MONTH:
        {
          cycle = SGUpgradeDialog.EVERY_MONTH;
          break;
        }

      default:
        {
          cycle = SGUpgradeDialog.NO_UPGRADE;
        }
    }
    this.mUpgradeDialog.setUpgradeCycle(cycle);

    // show
    this.mUpgradeDialog.setVisible(true);

    return true;
  }

  boolean upgradeWithCheckDate(Window owner) {
    if (this.checkDate() == false) {
      return false;
    }
    return this.upgrade(owner, true);
  }

  boolean upgradeByCommand(Window owner) {
    return this.upgrade(owner, false);
  }

  /**
   * @param owner
   * @param onStartUp
   */
  private boolean upgrade(final Window owner, final boolean onStartUp) {

    // fetch the latest release from GitHub API
    String tagName = fetchLatestReleaseTagName();
    if (tagName == null) {
      if (!onStartUp) {
        JOptionPane.showMessageDialog(owner, MSG_CHECK_FAILED);
      }
      return false;
    }

    // update the date
    Preferences pref = Preferences.userNodeForPackage(this.getClass());
    pref.putLong(PREF_KEY_DATE, System.currentTimeMillis());

    // parse tag name to extract version
    int[] remoteVersion = parseTagVersion(tagName);
    if (remoteVersion == null) {
      if (!onStartUp) {
        JOptionPane.showMessageDialog(owner, MSG_CHECK_FAILED);
      }
      return false;
    }

    final int nMajor = remoteVersion[0];
    final int nMinor = remoteVersion[1];
    final int nMicro = remoteVersion[2];

    // compare the version number
    final int major = this.mAppProp.getMajorVersion();
    final int minor = this.mAppProp.getMinorVersion();
    final int micro = this.mAppProp.getMicroVersion();

    boolean hasNewer = SGUtility.compareVersionNumber(major, minor, micro, nMajor, nMinor, nMicro);

    if (!hasNewer) {
      if (!onStartUp) {
        JOptionPane.showMessageDialog(owner, MSG_LATEST_VERSION_INSTALLED);
      }
      return true;
    }

    // get the changelog from product.xml in the tagged source
    String changelogHtml = fetchChangelog(tagName);

    // create version string
    final String majorStr = Integer.valueOf(nMajor).toString();
    final String minorStr = Integer.valueOf(nMinor).toString();
    final String microStr = Integer.valueOf(nMicro).toString();

    // create a message
    StringBuffer sb = new StringBuffer();
    sb.append(MSG_NEW_VERSION_FOUND_BEFORE);
    sb.append(majorStr);
    sb.append('.');
    sb.append(minorStr);
    sb.append('.');
    sb.append(microStr);
    sb.append(MSG_NEW_VERSION_FOUND_AFTER);
    String msg = sb.toString();

    // build changelog HTML
    sb.setLength(0);
    sb.append("<html><head></head><body>-- New Features --");
    sb.append("<font size=\"3\">");
    if (changelogHtml != null) {
      sb.append(changelogHtml);
    }
    sb.append("</font></body></html>");

    // create a message dialog
    SGUpgradeConfirmDialog cfDialog = null;
    if (owner instanceof Frame) {
      cfDialog = new SGUpgradeConfirmDialog((Frame) owner, true);
    } else if (owner instanceof Dialog) {
      cfDialog = new SGUpgradeConfirmDialog((Dialog) owner, true);
    }
    if (cfDialog == null) {
      return false;
    }
    cfDialog.setMessage(msg);
    cfDialog.setPage(sb.toString());
    cfDialog.pack();
    cfDialog.setCenter(owner);

    // show a message dialog
    cfDialog.setVisible(true);

    // if upgrade is canceled, return true
    if (cfDialog.isCanceled()) {
      return true;
    }

    // open GitHub Releases page in the default browser
    openReleasesPage(owner);

    return true;
  }

  /**
   * Fetch the tag_name from the latest GitHub release.
   *
   * @return tag name string (e.g. "v2.3.0"), or null on failure
   */
  private String fetchLatestReleaseTagName() {
    Proxy proxy = buildProxy();
    BufferedReader reader = null;
    try {
      URL url = URI.create(GITHUB_API_LATEST_URL).toURL();
      URLConnection connection = url.openConnection(proxy);
      connection.setRequestProperty("Accept", "application/vnd.github+json");
      connection.setConnectTimeout(10000);
      connection.setReadTimeout(15000);

      reader =
          new BufferedReader(
              new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line);
      }

      // extract tag_name from JSON response
      Matcher m = TAG_NAME_JSON_PATTERN.matcher(response.toString());
      if (m.find()) {
        return m.group(1);
      }
      return null;

    } catch (Exception ex) {
      return null;
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
        }
      }
    }
  }

  /**
   * Parse the version number from a tag name like "v2.3.0".
   *
   * @param tagName the tag name
   * @return version array [major, minor, micro], or null if parsing fails
   */
  private int[] parseTagVersion(String tagName) {
    Pattern p = Pattern.compile(TAG_NAME_PATTERN);
    Matcher m = p.matcher(tagName);
    if (!m.matches()) {
      return null;
    }
    try {
      int major = Integer.parseInt(m.group(1));
      int minor = Integer.parseInt(m.group(2));
      int micro = Integer.parseInt(m.group(3));
      return new int[] {major, minor, micro};
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  /**
   * Fetch the changelog from the product.xml file in the tagged source.
   *
   * @param tagName the Git tag name (e.g. "v2.3.0")
   * @return HTML string of the changelog, or null on failure
   */
  private String fetchChangelog(String tagName) {
    Proxy proxy = buildProxy();
    BufferedReader reader = null;
    try {
      String xmlUrlStr = GITHUB_RAW_BASE_URL + "/" + tagName + "/changelog/product.xml";
      URL url = URI.create(xmlUrlStr).toURL();
      URLConnection connection = url.openConnection(proxy);
      connection.setConnectTimeout(10000);
      connection.setReadTimeout(15000);

      reader =
          new BufferedReader(
              new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }

      // parse the XML
      Document doc = SGUtilityText.getDocumentFromString(sb.toString());
      if (doc == null) {
        return null;
      }

      // collect changelog entries from all release elements newer than current version
      final int cMajor = this.mAppProp.getMajorVersion();
      final int cMinor = this.mAppProp.getMinorVersion();
      final int cMicro = this.mAppProp.getMicroVersion();

      NodeList rList = doc.getElementsByTagName("release");
      List<String> logEntries = new ArrayList<String>();
      for (int ii = 0; ii < rList.getLength(); ii++) {
        Element el = (Element) rList.item(ii);
        final int rMajor = getVersion(el, MAJOR_VER);
        final int rMinor = getVersion(el, MINOR_VER);
        final int rMicro = getVersion(el, MICRO_VER);
        if (SGUtility.compareVersionNumber(cMajor, cMinor, cMicro, rMajor, rMinor, rMicro)) {
          // extract changelog content
          NodeList rInfoList = el.getElementsByTagName("releaseinfo");
          if (rInfoList.getLength() > 0) {
            Element rInfo = (Element) rInfoList.item(0);
            NodeList cLogList = rInfo.getElementsByTagName("changelog");
            if (cLogList.getLength() > 0) {
              Element cLog = (Element) cLogList.item(0);
              StringBuffer logSb = new StringBuffer();
              printNode(cLog, logSb);
              logEntries.add(logSb.toString());
            }
          }
        }
      }

      if (logEntries.isEmpty()) {
        return null;
      }

      StringBuilder result = new StringBuilder();
      for (String entry : logEntries) {
        result.append(entry);
      }
      return result.toString();

    } catch (Exception ex) {
      return null;
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
        }
      }
    }
  }

  /**
   * @param root
   * @param tagName
   * @return
   */
  private int getVersion(Element root, String tagName) {
    NodeList nodeList = root.getElementsByTagName(tagName);
    if (nodeList.getLength() == 0) {
      return 0;
    }
    Element ver = (Element) nodeList.item(0);
    if (ver.getFirstChild() == null) {
      return 0;
    }
    String verString = ver.getFirstChild().getNodeValue();
    try {
      return Integer.parseInt(verString);
    } catch (NumberFormatException ex) {
      return 0;
    }
  }

  /**
   * Open the GitHub Releases page in the default browser.
   *
   * @param owner the owner window
   */
  private void openReleasesPage(Window owner) {
    try {
      Desktop.getDesktop().browse(URI.create(GITHUB_RELEASES_URL));
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(owner, MSG_BROWSER_FAILED + GITHUB_RELEASES_URL);
    }
  }

  /**
   * Build a java.net.Proxy from the application's proxy settings.
   *
   * @return the proxy
   */
  private Proxy buildProxy() {
    if (this.mProxyManager.isDirectAccess()) {
      return Proxy.NO_PROXY;
    }
    String host = this.mProxyManager.getProxyHostName();
    int port = this.mProxyManager.getProxyPortNumber();
    if (host == null || host.isEmpty() || port <= 0) {
      return Proxy.NO_PROXY;
    }
    return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
  }

  /**
   * @param element
   * @param sb
   */
  private void printNode(Element element, StringBuffer sb) {
    printNodeRecursively(element, sb, -1);
  }

  /**
   * @param element
   * @param sb
   * @param depth
   */
  private void printNodeRecursively(Element element, StringBuffer sb, final int depth) {
    NodeList childList = element.getChildNodes();
    for (int ii = 0; ii < childList.getLength(); ii++) {
      org.w3c.dom.Node child = childList.item(ii);
      if (child instanceof Element) {
        Element el = (Element) child;
        String tagName = el.getTagName();
        if (tagName.equals("item")) {
          NodeList nList = el.getChildNodes();
          final int num = nList.getLength();
          if (num == 1) {
            sb.append("<li>");
            printFirstChild(el, sb, depth);
            sb.append("</li>");
          } else if (num > 1) {
            sb.append("<li>");
            printFirstChild(el, sb, depth);
            sb.append("<ul>");
            for (int jj = 0; jj < num; jj++) {
              org.w3c.dom.Node node = nList.item(jj);
              if (node instanceof Element) {
                Element el_ = (Element) node;
                printNodeRecursively(el_, sb, depth + 1);
              }
            }
            sb.append("</ul>");
            sb.append("</li>");
          }
        } else if (tagName.equals("itemize")) {
          sb.append("<ul>");
          printNodeRecursively(el, sb, depth + 1);
          sb.append("</ul>");
        }
      }
    }
  }

  /**
   * @param node
   * @param sb
   * @param depth
   */
  private void printFirstChild(org.w3c.dom.Node node, StringBuffer sb, final int depth) {
    org.w3c.dom.Node child = node.getFirstChild();
    printText(child, sb, depth);
  }

  /**
   * @param node
   * @param sb
   * @param depth
   */
  private void printText(org.w3c.dom.Node node, StringBuffer sb, final int depth) {
    String line = node.getNodeValue();
    String sub = tokenize(line);
    if (sub.length() != 0) {
      sb.append(sub);
    }
  }

  /**
   * @param line
   * @return
   */
  private String tokenize(String line) {
    StringTokenizer tkn = new StringTokenizer(line);
    List<String> tokenList = new ArrayList<String>();
    while (tkn.hasMoreTokens()) {
      tokenList.add(tkn.nextToken());
    }
    StringBuffer sb = new StringBuffer();
    for (int ii = 0; ii < tokenList.size(); ii++) {
      String str = tokenList.get(ii);
      sb.append(str);
      if (ii != tokenList.size() - 1) {
        sb.append(' ');
      }
    }
    return sb.toString();
  }

  /**
   * Check whether to do auto-upgrade.
   *
   * @return whether to do auto-upgrade
   */
  private boolean checkDate() {
    Preferences pref = Preferences.userNodeForPackage(this.getClass());
    final long time = pref.getLong(PREF_KEY_DATE, 0);
    final long current = System.currentTimeMillis();
    final long diff = current - time;

    final long day = 1000 * 3600 * 24;
    final long week = 7 * day;
    final long month = 30 * day;
    final int cycle = pref.getInt(PREF_KEY_UPGRADE_CYCLE, UPGRADE_EVERY_TIME);

    boolean b;
    switch (cycle) {
      case NO_UPGRADE:
        b = false;
        break;
      case UPGRADE_EVERY_TIME:
        b = true;
        break;
      case UPGRADE_EVERY_DAY:
        b = (diff > day);
        break;
      case UPGRADE_EVERY_WEEK:
        b = (diff > week);
        break;
      case UPGRADE_EVERY_MONTH:
        b = (diff > month);
        break;
      default:
        b = false;
    }

    return b;
  }
}

package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import jp.riken.brain.ni.samuraigraph.base.SGDialog;
import jp.riken.brain.ni.samuraigraph.base.SGExtensionFileFilter;
import jp.riken.brain.ni.samuraigraph.base.SGFileChooser;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGUserProperties;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * A class to manage upgrade of the application.
 */
class SGUpgradeManager implements ActionListener, SGIConstants,
        SGIUpgradeConstants, SGIPreferencesConstants {

    private static final String MSG_LATEST_VERSION_INSTALLED = "The latest version is already installed.";

    private static final String MSG_NEW_VERSION_FOUND_BEFORE = "Samurai Graph Ver.";

    private static final String MSG_NEW_VERSION_FOUND_AFTER = " is found.\nDownload now?\n";

    private static final String MSG_UPGRADE_FAILED = "Upgrade is failed for some reason.";

    private static final String MSG_UPGRADE_WARNING = "This application will be terminated.\nPresent work will be lost.";

    private static final String TITLE_WARNING = "Warning";

    private static final String MSG_LOCAL_FILE_NOT_FOUND = "Local file is not found.";

    private static final String MSG_CONNECTION_FAILED = "Connection Failed.";

    private static final String MAJOR_VER = "majorver";
    
    private static final String MINOR_VER = "minorver";
    
    private static final String MICRO_VER = "microver";

    /**
     * A file chooser to download the new version.
     */
    private JFileChooser mUpgradeFileChooser;

    /**
     * 
     */
    private SGUpgradeDialog mUpgradeDialog;

    /**
     * 
     */
    private SGProxyManager mProxyManager;

    /**
     * application properties
     */
    private SGApplicationProperties mAppProp;

    /**
     * 
     * 
     */
    public SGUpgradeManager(final SGProxyManager p,
            final SGApplicationProperties ap) {
        this.mProxyManager = p;
        this.mAppProp = ap;
        this.mUpgradeFileChooser = new SGFileChooser();
        this.mUpgradeFileChooser.setCurrentDirectory(new File(USER_HOME));
    }

    /**
     * 
     */
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
            if (this.upgradeByCommand(dg) == false) {
                return;
            }
        }

    }

    /**
     * 
     * @param wnd
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
        case NO_UPGRADE: {
            cycle = SGUpgradeDialog.NO_UPGRADE;
            break;
        }

        case UPGRADE_EVERY_TIME: {
            cycle = SGUpgradeDialog.EVERY_TIME;
            break;
        }

        case UPGRADE_EVERY_DAY: {
            cycle = SGUpgradeDialog.EVERY_DAY;
            break;
        }

        case UPGRADE_EVERY_WEEK: {
            cycle = SGUpgradeDialog.EVERY_WEEK;
            break;
        }

        case UPGRADE_EVERY_MONTH: {
            cycle = SGUpgradeDialog.EVERY_MONTH;
            break;
        }

        default: {
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
     * 
     * @param owner
     * @param onStartUp
     */
    private boolean upgrade(final Window owner, final boolean onStartUp) {

        final boolean direct = this.mProxyManager.isDirectAccess();
        String host = this.mProxyManager.getProxyHostName();
        int port = this.mProxyManager.getProxyPortNumber();

        // get file name
    	SGUserProperties prop = SGUserProperties.getInstance();
    	String devMode = prop.getProperty("dev");
    	Boolean bDevMode = Boolean.valueOf(devMode);
    	final String fileName;
    	if (bDevMode) {
        	fileName = DEV_PRODUCT_XML_FILE_NAME;
    	} else {
        	fileName = PRODUCT_XML_FILE_NAME;
    	}
    	
        // get URL
        URL url = null;
        try {
            if (direct) {
                url = new URL(fileName);
            } else {
                url = new URL("http", host, port, fileName);
            }
        } catch (MalformedURLException ex) {
            return false;
        }

        // create a Document object
        Document doc = SGUtilityText.getDocument(url);
        if (doc == null) {
            return false;
        }

        // update the date
        Preferences pref = Preferences.userNodeForPackage(this.getClass());
        pref.putLong(PREF_KEY_DATE, System.currentTimeMillis());

        // get root element - product
        Element root = doc.getDocumentElement();

        // compare the version number
        if (this.compareVersion(root)) {
            // start installation
            if (this.installLatestVersion(root, owner, onStartUp) == false) {
                return false;
            }
        } else {
            if (!onStartUp) {
                // show a message dialog
                JOptionPane.showMessageDialog(owner,
                        MSG_LATEST_VERSION_INSTALLED);
            }
        }

        return true;
    }

    /**
     * 
     * @return
     */
    private boolean compareVersion(Element root) {
        final int major = this.mAppProp.getMajorVersion();
        final int minor = this.mAppProp.getMinorVersion();
        final int micro = this.mAppProp.getMicroVersion();
        final int nMajor = this.getVersion(root, MAJOR_VER);
        final int nMinor = this.getVersion(root, MINOR_VER);
        final int nMicro = this.getVersion(root, MICRO_VER);
        final boolean b = SGUtility.compareVersionNumber(major,
                minor, micro, nMajor, nMinor, nMicro);
        return b;
    }

    /**
     * 
     * @param root
     * @param tagName
     * @return
     */
    private int getVersion(Element root, String tagName) {
        Element ver = (Element) root.getElementsByTagName(tagName).item(0);
        String verString = ver.getFirstChild().getNodeValue();
        final int verNumber = Integer.parseInt(verString);
        return verNumber;
    }

    /**
     * 
     * @param root
     * @return
     * @throws Exception
     */
    private boolean installLatestVersion(Element root, final Window owner,
            final boolean onStartUp) {
        // current version numbers
        final int mMajor = this.mAppProp.getMajorVersion();
        final int mMinor = this.mAppProp.getMinorVersion();
        final int mMicro = this.mAppProp.getMicroVersion();

        // get later releases and the latest release
        NodeList rList = root.getElementsByTagName("release");
        Element latestRelease = null;
        List<Element> laterReleaseList = new ArrayList<Element>();
        for (int ii = 0; ii < rList.getLength(); ii++) {
            Element el = (Element) rList.item(ii);
            String value = el.getAttribute("latest");
            if (Boolean.TRUE.toString().equals(value)) {
                latestRelease = el;
            }

            final int nMajor = this.getVersion(el, MAJOR_VER);
            final int nMinor = this.getVersion(el, MINOR_VER);
            final int nMicro = this.getVersion(el, MICRO_VER);
            if (SGUtility.compareVersionNumber(mMajor, mMinor,
                    mMicro, nMajor, nMinor, nMicro)) {
                laterReleaseList.add(el);
            }
        }

        if (latestRelease == null) {
            return false;
        }
        
        // get the latest version numbers
        final int nMajor = this.getVersion(latestRelease, MAJOR_VER);
        final int nMinor = this.getVersion(latestRelease, MINOR_VER);
        final int nMicro = this.getVersion(latestRelease, MICRO_VER);

        final String major = Integer.valueOf(nMajor).toString();
        final String minor = Integer.valueOf(nMinor).toString();
        final String micro = Integer.valueOf(nMicro).toString();

        // create a message
        StringBuffer sb = new StringBuffer();
        sb.append(MSG_NEW_VERSION_FOUND_BEFORE);
        sb.append(major);
        sb.append('.');
        sb.append(minor);
        sb.append('.');
        sb.append(micro);
        sb.append(MSG_NEW_VERSION_FOUND_AFTER);
        String msg = sb.toString();

        // get the change log
        sb.setLength(0);
        sb.append("<html><head></head><body>-- New Features --");
        String fSize = "<font size=\"3\">";
        sb.append(fSize);
        for (int ii = 0; ii < laterReleaseList.size(); ii++) {
            Element release = (Element) laterReleaseList.get(ii);
            StringBuffer sb_ = new StringBuffer();
            NodeList rInfoList = release.getElementsByTagName("releaseinfo");
            Element rInfo = (Element) rInfoList.item(0);
            NodeList cLogList = rInfo.getElementsByTagName("changelog");
            Element cLog = (Element) cLogList.item(0);
            this.printNode(cLog, sb_);
            sb.append(sb_);
        }

        // create a message dialog
        SGUpgradeConfirmDialog cfDialog = null;
        if (owner instanceof Frame) {
            cfDialog = new SGUpgradeConfirmDialog((Frame) owner, true);
        } else if (owner instanceof Dialog) {
            cfDialog = new SGUpgradeConfirmDialog((Dialog) owner, true);
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

        // get OS name
        String name = null;
        if (SGUtility.identifyOS(OS_NAME_WINDOWS)) {
            name = "win32";
        } else if (SGUtility.identifyOS(OS_NAME_MACOSX)) {
            name = "macosx";
        } else {
            name = "other";
        }

        // upgrade
        NodeList pList = latestRelease.getElementsByTagName("package");
        for (int ii = 0; ii < pList.getLength(); ii++) {
            Element el = (Element) pList.item(ii);
            String attr = el.getAttribute("category");

            if (attr.equals("win32")) {
                if (name.equals("win32")) {
                    if (this
                            .forWin32(el, major, minor, micro, owner, onStartUp)) {
                        break;
                    }
                    return false;
                }
            } else if (attr.equals("macosx")) {
                if (name.equals("macosx")) {
                    if (this.forMacOSX(el, major, minor, micro, owner,
                            onStartUp)) {
                        break;
                    }
                    return false;
                }
            } else if (attr.equals("bin")) {
                if (name.equals("other")) {
                    if (this.forOtherPlatform(el, major, minor, micro, owner,
                            onStartUp)) {
                        break;
                    }
                    return false;
                }
            } else if (attr.equals("src")) {

            }
        }

        return true;
    }

    /**
     * 
     * @param el
     * @param major
     *            Major version number of the new version
     * @param minor
     *            Minor version number of the new version
     * @param micro
     *            Micro version number of the new version
     * @param owner
     *            Dialog owner
     * @param onStartUp
     *            whether this upgrade is on the start-up
     * @return true:success, false:failure
     */
    private boolean forWin32(final Element el, final String major,
            final String minor, final String micro, final Window owner,
            final boolean onStartUp) {
        
        StringBuffer sb = new StringBuffer();

        final String failed = MSG_UPGRADE_FAILED;
        sb.setLength(0);
        sb.append("samurai-graph-win32-");
        sb.append(major);
        sb.append('.');
        sb.append(minor);
        sb.append('.');
        sb.append(micro);
        sb.append(".exe ");
        final String fnameInst = sb.toString();

        // show a message dialog
        if (!onStartUp) {
            Object[] options = { "OK", "Cancel" };
            final int ret = JOptionPane.showOptionDialog(owner,
                    MSG_UPGRADE_WARNING, TITLE_WARNING,
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
            if (ret == JOptionPane.NO_OPTION
                    || ret == JOptionPane.CLOSED_OPTION) {
                return true;
            }
        }

        // get the root directory
        String classpath = System.getProperty("java.class.path");
        StringTokenizer stk = new StringTokenizer(classpath, PATH_SEPARATOR);
        String root = null;
        while (stk.hasMoreTokens()) {
            String str = stk.nextToken();
            if (str.endsWith("samurai-graph.jar")) {
                root = new File(str).getParent();
                break;
            }
        }
        if (root == null) {
            JOptionPane.showMessageDialog(owner, failed);
            return false;
        }

        // copy the helper application from ./lib to the temporary directory
        sb.setLength(0);
        sb.append(root);
        sb.append(FILE_SEPARATOR);
        sb.append("lib");
        sb.append(FILE_SEPARATOR);
        sb.append(UPGRADE_HELPER_FILE_NAME);
        File helper = new File(sb.toString());
        if (helper.exists() == false) {
            // helper application is not found.
            JOptionPane.showMessageDialog(owner, failed);
            return false;
        }

        File helperTempDir = new File(
                SGApplicationUtility.getPathName(TMP_DIR, HELPER_TEMP_DIR_NAME));
        helperTempDir.deleteOnExit();
        if (helperTempDir.mkdir() == false) {
            // failed to create a temporary directory.
            JOptionPane.showMessageDialog(owner, failed);
            return false;
        }
        File helperTemp = new File(SGApplicationUtility.getPathName(
                helperTempDir.getAbsolutePath(), UPGRADE_HELPER_FILE_NAME));
        helperTemp.deleteOnExit();
        try {
            SGApplicationUtility.copyBinaryFile(helper, helperTemp);
        } catch (IOException ex) {
            // failed to copy the helper application.
            JOptionPane.showMessageDialog(owner, failed);
            return false;
        }

        //
        // download the installer
        //
        File installer = new File(SGApplicationUtility.getPathName(
                helperTempDir.getAbsolutePath(), fnameInst));
        if (this.download(el, owner, installer) == false) {
            // failed to download the latest version.
            JOptionPane.showMessageDialog(owner, failed);
            helperTemp.delete();
            helperTempDir.delete();
            return false;
        }

        //
        // start the helper application
        //

        String upperDir = new File(root).getParent();
        String instPath = installer.getAbsolutePath();
        String pathNew = root;
        String[] cmdArray = new String[6];
        sb.setLength(0);
        sb.append(System.getProperty("java.home"));
        sb.append(FILE_SEPARATOR);
        sb.append("bin");
        sb.append(FILE_SEPARATOR);
        sb.append("javaw.exe");
        cmdArray[0] = sb.toString();
        cmdArray[1] = "-jar";
        cmdArray[2] = helperTemp.getAbsolutePath();
        cmdArray[3] = root; // A pathname string of the old version
        cmdArray[4] = instPath; // A pathname string of the installer
        cmdArray[5] = pathNew; // A pathname string of the new version
        if (helperTemp.exists() == false) {
            // helper application not found.
            JOptionPane.showMessageDialog(owner, failed);
            return false;
        }
        try {
            Runtime.getRuntime().exec(cmdArray, null, new File(upperDir));
        } catch (IOException ex) {
            // failed to start the helper application.
            JOptionPane.showMessageDialog(owner, failed);
            return false;
        }

        // try to quite application
        if (SGDrawingServer.quitHandler() == false) {
            JOptionPane.showMessageDialog(owner,
                    "Failed to terminate Samurai Graph application.");
            return false;
        }

        return true;
    }

    /**
     * 
     * @param el
     * @return
     * @throws Exception
     */
    private boolean forOtherPlatform(final Element el, final String major,
            final String minor, final String micro, final Window owner,
            final boolean onStartUp)// throws Exception
    {
        return this.downloadWithFileChooser(el, major, minor, micro, owner,
                onStartUp, "samurai-graph-bin", "zip", "Zip Archive");
    }

    /**
     * 
     * @param el
     * @return
     * @throws Exception
     */
    private boolean forMacOSX(final Element el, final String major,
            final String minor, final String micro, final Window owner,
            final boolean onStartUp)// throws Exception
    {
        return this.downloadWithFileChooser(el, major, minor, micro, owner,
                onStartUp, "samurai-graph-mac", "dmg.gz",
                "Compressed Disk Image");
    }

    /**
     * 
     * @param el
     * @param major
     * @param minor
     * @param micro
     * @param owner
     * @param onStartUp
     * @param extension
     * @param description
     * @return
     * @throws Exception
     */
    private boolean downloadWithFileChooser(final Element el,
            final String major, final String minor, final String micro,
            final Window owner, final boolean onStartUp, final String name,
            final String extension, final String description) {
        JFileChooser chooser = this.mUpgradeFileChooser;

        // reset filters
        chooser.resetChoosableFileFilters();

        // create a file filter object
        SGExtensionFileFilter ff = new SGExtensionFileFilter();
        ff.setExplanation(description);
        ff.addExtension(extension);
        chooser.setFileFilter(ff);

        // set file name
        StringBuffer sb = new StringBuffer();
        sb.append(name);
        sb.append('-');
        sb.append(major);
        sb.append('.');
        sb.append(minor);
        sb.append('.');
        sb.append(micro);
        sb.append('.');
        sb.append(extension);
//        String fileName = name + "-" + major + "." + minor + "." + micro + "."
//                + extension;
        String fileName = sb.toString();
        chooser.setSelectedFile(new File(fileName));

        // show save dialog
        final int ret = chooser.showSaveDialog(owner);

        File fileSaved = null;
        switch (ret) {
        case JFileChooser.APPROVE_OPTION:
            // selected
            fileSaved = chooser.getSelectedFile();
            break;
        case JFileChooser.CANCEL_OPTION:
            // canceled
            return true;
        case JFileChooser.ERROR_OPTION:
            // error
            throw new Error();
        default:
        }

        String path = fileSaved.getAbsolutePath();
        File file = new File(path);

        // download
        if (this.download(el, owner, file) == false) {
            return false;
        }

        return true;
    }

    /**
     * 
     * @param el
     * @param owner
     * @param suffix
     * @return
     * @throws Exception
     */
    private boolean download(final Element el, final Window owner,
            final File file) {
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        SGDownloadMonitorDialog dg = null;

        boolean ret = false;

        try {
            // create output stream
            bos = new BufferedOutputStream(new FileOutputStream(file));

            NodeList urlList = el.getElementsByTagName("url");
            for (int ii = 0; ii < urlList.getLength(); ii++) {
                Element urlElement = (Element) urlList.item(ii);
                String urlStr = urlElement.getFirstChild().getNodeValue();

                // create an URL instance
                URL url = null;
                try {
                    url = new URL(urlStr);
                } catch (MalformedURLException ex) {
                    continue;
                }

                // get input stream
                URLConnection co = null;
                try {
                    co = url.openConnection();
                    bis = new BufferedInputStream(co.getInputStream());
                } catch (IOException ex) {
                    continue;
                }

                // get file name
                String path = file.getAbsolutePath();
                /*
                 * final int nameLen = 20; if( path.length() > nameLen ) { path =
                 * path.substring(0,nameLen); path += "..."; }
                 */

                // get file size
                final int fileSize = co.getContentLength();

                // create a progress monitor dialog
                boolean modal = true;
                if (owner instanceof Dialog) {
                    dg = new SGDownloadMonitorDialog((Dialog) owner, modal);
                } else if (owner instanceof Frame) {
                    dg = new SGDownloadMonitorDialog((Frame) owner, modal);
                } else {
                    throw new Error("The owner window is not Dialog nor Frame.");
                }

                // set properties of the dialog
                dg.setMaxValue(fileSize);
                dg.setInputStream(bis);
                dg.setOutputStream(bos);
                dg.setStatusText(path);
                dg.setCenter(owner);

                // set the mouse cursor
                owner.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                // start download
                Thread th = new Thread(dg);
                th.start();

                dg.setVisible(true);

                if (dg.isCanceled() == true) {
                    return false;
                }

                ret = true;
                break;
            }

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(owner, MSG_LOCAL_FILE_NOT_FOUND);
            return false;
        } finally {

            // set default cursor
            owner.setCursor(Cursor.getDefaultCursor());

            // clear attributes
            dg.setInputStream(null);
            dg.setOutputStream(null);

            // close streams
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ex) {
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException ex) {
                }
            }
        }

        // when connection failed, return false
        if (!ret) {
            JOptionPane.showMessageDialog(owner, MSG_CONNECTION_FAILED);
            return false;
        }

        return true;
    }

    /**
     * 
     * @param element
     * @param tagList
     * @param out
     */
    private void printNode(Element element, StringBuffer sb) {
        printNodeRecursively(element, sb, -1);
    }

    /**
     * 
     * @param node
     * @return
     */
    private void printNodeRecursively(Element element, StringBuffer sb,
            final int depth) {
        NodeList childList = element.getChildNodes();
        for (int ii = 0; ii < childList.getLength(); ii++) {
            Node child = childList.item(ii);
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
                            Node node = nList.item(jj);
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
            } else if (child instanceof Text) {

            }
        }

    }

    /**
     * 
     * @param node
     * @param out
     * @param depth
     */
    private void printFirstChild(Node node, StringBuffer sb, final int depth) {
        Node child = node.getFirstChild();
        printText(child, sb, depth);
    }

    /**
     * 
     * @param node
     * @param out
     * @param depth
     */
    private void printText(Node node, StringBuffer sb, final int depth) {
        String line = node.getNodeValue();
        String sub = tokenize(line);
        if (sub.length() != 0) {
            // System.out.println(sub);
            sb.append(sub);
        }
    }

    /**
     * 
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
            String str = (String) tokenList.get(ii);
            sb.append(str);
            if (ii != tokenList.size() - 1) {
                sb.append(' ');
            }
        }
        String subStr = sb.toString();
        return subStr;
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
        final int cycle = pref.getInt(PREF_KEY_UPGRADE_CYCLE,
                UPGRADE_EVERY_TIME);

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

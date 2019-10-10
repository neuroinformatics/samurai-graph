package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import jp.riken.brain.ni.samuraigraph.application.SGIApplicationConstants.FILE_TYPE;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGExtensionFileFilter;
import jp.riken.brain.ni.samuraigraph.base.SGFileChooser;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDisposable;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGIDataFileConstants;

public class SGPluginManager implements SGIDisposable {
    
    private SGMainFunctions mMain = null;
    
    protected final List<Class<?>> mClassList = new ArrayList<Class<?>>();
    
    protected final List<PluginException> mExceptionList = new ArrayList<PluginException>();
    
    SGPluginManager(final SGMainFunctions main) {
        super();
        this.mMain = main;
    }
    
    /**
     * Loads jar plugins in given directory.
     * 
     * @param directory this directory 
     * @return true if succeeded
     */
    boolean loadPlugins(final String directory) {
        this.mClassList.clear();
        this.mExceptionList.clear();
        List<File> fileList = SGApplicationUtility.findFiles(directory);
        if (fileList == null) {
        	return true;
        }
        List<File> jarFileList = new ArrayList<File>();
        for (File f : fileList) {
        	if (f.getName().endsWith(".jar")) {
        		jarFileList.add(f);
        	}
        }
        File[] jarFileArray = jarFileList.toArray(new File[jarFileList.size()]);
        if (this.loadJars(jarFileArray) == false) {
        	return false;
        }
        return true;
    }
    
    /**
     * Load jar files.
     * 
     * @param jarfiles
     * @return true if all jar files are loaded. false if some exception occurred.
     */
    private boolean loadJars(File[] jarfiles) {
        boolean result = true;
        for (int i = 0; i < jarfiles.length; i++) {
            try {
                this.loadJar(jarfiles[i], this.mClassList);
            } catch (Exception e) {
                this.mExceptionList.add(new PluginException(e, jarfiles[i].getName()));
                result = false;
            }
        }
        return result;
    }
    
    private void loadJar(final File jarfile, List<Class<?>> clslist)
    throws ClassNotFoundException, ZipException, IOException, InstantiationException, IllegalAccessException {
        ZipFile zip = new ZipFile(jarfile);
        SGJarClassLoader jcl = new SGJarClassLoader(zip);

        List<Class<?>> clsInJar = new ArrayList<Class<?>>();
        Enumeration<? extends ZipEntry> entry = zip.entries();
        while (entry.hasMoreElements()) {
            ZipEntry ze = entry.nextElement();
            if (!ze.isDirectory()) {
                String entryName = ze.getName();
                if (entryName.endsWith(".class")) {
                    jcl.setDelegateFirst(false);
                    String className = entryName.substring(0, entryName.length()-6).replace('/','.');
                    Class<?> classObj = jcl.loadClass(className, false);
                    clsInJar.add(classObj);
                    if (SGIPluginOutputDataToFile.class.isAssignableFrom(classObj)) {
                        classObj.newInstance();
                    }
                }
            }
        }
        clslist.addAll(clsInJar);
    }
    
    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;
        this.mClassList.clear();
        this.mExceptionList.clear();
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

    public boolean hasOutputPlugins() {
        for (int i = 0; i < this.mClassList.size(); i++) {
            if (SGIPluginOutputDataToFile.class.isAssignableFrom(this.mClassList.get(i))) {
                return true;
            }
        }
        return false;
    }
    
    public void doOutputToFile(final Component parent, final SGData data) throws IOException {
        
        Map<FileFilter, SGIPluginOutputDataToFile> pluginMap = this.getPluginFilterMap();
        JFileChooser chooser = this.getFileChooser(pluginMap.keySet());

        final int ret = chooser.showSaveDialog(parent);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            this.mCurrentFilePath = file.getParent();
            this.mCurrentFileName = file.getName();
            
            FileFilter fSelected = chooser.getFileFilter();
            for (FileFilter ff : pluginMap.keySet()) {
                if (ff.equals(fSelected)) {
                    pluginMap.get(ff).writeData(file, data);
                    
                    FILE_TYPE fileType = null;
                    if (SGDataUtility.isSDArrayData(data)) {
                    	fileType = FILE_TYPE.TXT_DATA;
                    } else if (SGDataUtility.isNetCDFData(data)) {
                    	fileType = FILE_TYPE.NETCDF_DATA;
                    } else if (SGDataUtility.isHDF5Data(data)) {
                    	fileType = FILE_TYPE.HDF5_DATA;
                    } else if (SGDataUtility.isMATLABData(data)) {
                    	fileType = FILE_TYPE.MATLAB_DATA;
                    }
                    if (fileType != null) {
                        this.mMain.updateCurrentFile(file, fileType);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Return map of file filter for chooser with its plugin class.
     * 
     * @return map of file filter with its class.
     */
    private Map<FileFilter, SGIPluginOutputDataToFile> getPluginFilterMap() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        for (int i = 0; i < this.mClassList.size(); i++) {
            if (SGIPluginOutputDataToFile.class.isAssignableFrom(this.mClassList.get(i))) {
                list.add(this.mClassList.get(i));
            }
        }
        
        Map<FileFilter, SGIPluginOutputDataToFile> pluginMap = new HashMap<FileFilter, SGIPluginOutputDataToFile>();
        for (int i = 0; i < list.size(); i++) {
            try {
                Object obj = list.get(i).newInstance();
                SGIPluginOutputDataToFile plugin = (SGIPluginOutputDataToFile)obj;
                
                SGExtensionFileFilter filter = new SGExtensionFileFilter();
                filter.setExplanation(plugin.getDescription());
                filter.addExtension(plugin.getExtension());
                
                pluginMap.put(filter, plugin);
            } catch (Exception e) {
            }
        }
        
        return pluginMap;
    }
    
    /**
     * 
     * @param filterSet
     * @return
     */
    private JFileChooser getFileChooser(final Set<FileFilter> filterSet) {
        JFileChooser chooser = new SGFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        
        for (FileFilter filter : filterSet) {
            chooser.setFileFilter(filter);
        }
        
        // set current directory
        chooser.setCurrentDirectory(new File(this.getCurrentFilePath()));

        // create the full path name
        String path = SGApplicationUtility.getPathName(this.getCurrentFilePath(), this.getCurrentFileName());
        File f = new File(path);
        chooser.setSelectedFile(f);

        return chooser;
    }
    
    /**
     * Return jar filename if some exceptions occurred and failed to load it.
     * @return "" if succeeds. Return jar filenaem if failed.
     */
    public String getFirstExceptionJarFilename() {
        if (this.mExceptionList.size()==0) {
            return "";
        }
        return this.mExceptionList.get(0).getFilename();
    }
    
    private String getCurrentFilePath() {
        if (this.mCurrentFilePath==null || this.mCurrentFilePath.equals("")) {
            this.mCurrentFilePath = SGIConstants.USER_HOME;
        }
        return this.mCurrentFilePath;
    }
    
    private String getCurrentFileName() {
        if (this.mCurrentFileName==null || this.mCurrentFileName.equals("")) {
            this.mCurrentFileName = SGApplicationUtility.appendExtension(DEFAULT_OUTPUT_FILE_NAME, DEFAULT_OUTPUT_FILE_EXT);
        }
        return this.mCurrentFileName;
    }

    /**
     * Current file path and name
     */
    private String mCurrentFilePath;

    private String mCurrentFileName;
    
    public static final String DEFAULT_OUTPUT_FILE_NAME = "output";
    
    public static final String DEFAULT_OUTPUT_FILE_EXT = SGIDataFileConstants.TEXT_FILE_EXTENSION;
    
    private static class PluginException extends Exception {
        private static final long serialVersionUID = -924108456633860433L;
        private final String mFilename;
        
        PluginException(Exception exception, String filename) {
            super(exception);
            this.mFilename = filename;
        }
        
        public String getFilename() {
            return this.mFilename;
        }
    }

}

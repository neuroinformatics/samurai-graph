package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JFileChooser;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;

/**
 * Extract an archive file.
 */
public class SGArchiveFileExtractor extends SGFileHandler implements SGIArchiveFileConstants {

    /**
     * Constant value of End of File
     */
    protected static final int EOF = -1;

    /**
     * Extracted File Lists
     */
    private ArrayList<File> mFileList = new ArrayList<File>();

    /**
     * Constructs an object.
     * 
     */
    public SGArchiveFileExtractor() {
    	super();
        this.initFilePath(DEFAULT_ARCHIVE_FILE_NAME, ARCHIVE_FILE_EXTENSION);
    }

    /**
     * extract an archive file.
     */
    public int extract(final SGDrawingWindow wnd, final String destDirName,
            final File file) {
        return this.extract(wnd, destDirName, file.getPath());
    }

    /*
     * Extract a zip archive file.
     * 
     */
    public int extract(final SGDrawingWindow wnd, final String destDirName,
            final String pathName) {
        // extract zip archive
        int res = -1;
        try {
            res = this.extract(destDirName, pathName);
        } catch (IOException ex) {
            return -2;
        }
        return res;
    }

    /**
     * 
     * @param destDirName
     * @param zfileName
     * @return always 0 if succeeds.
     * @throws IOException
     */
    private int extract(final String destDirName, final String zfileName)
            throws IOException {
        this.mFileList.clear();
        File destDir = new File(destDirName);
        if (!destDir.isDirectory()) {
            throw new IOException("Couldn't access dir " + destDir);
        }
        ZipFile zfile = new ZipFile(zfileName);
        Enumeration entries = zfile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry ze = (ZipEntry) entries.nextElement();
            String path = SGApplicationUtility.getPathName(
                    destDir.getAbsolutePath(), 
                    ze.getName());
            File f = new File(path);            
            if (ze.isDirectory()) {
                if (!f.exists() && !f.mkdirs())
                    throw new IOException("Couldn't create dir " + f);
                this.mFileList.add(f);
            } else {
                BufferedInputStream is = null;
                BufferedOutputStream os = null;
                try {
                    is = new BufferedInputStream(zfile.getInputStream(ze));
                    File dest = f.getParentFile();
                    if (!dest.exists() && !dest.mkdirs())
                        throw new IOException("Couldn't create dir " + dest);
                    os = new BufferedOutputStream(new FileOutputStream(f));
                    int b = EOF;
                    while ((b = is.read()) != EOF)
                        os.write(b);
                    is.close();
                    os.close();
                    this.mFileList.add(f);
                } finally {
                    if (is != null)
                        is.close();
                    if (os != null)
                        is.close();
                }
            }
        }
        return 0;
    }

    /**
     * 
     * @param wnd
     * @return
     */
    public File getArchiveFileFromFileChooser(final Component parent) {
    	JFileChooser chooser = SGApplicationUtility.createFileChooser(this.mCurrentDirectory, 
    			this.mCurrentFileName, ARCHIVE_FILE_EXTENSION, ARCHIVE_FILE_DESCRIPTION, "");

        // show save dialog
        final int ret = chooser.showOpenDialog(parent);

        File file = null;
        if (ret == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            this.mCurrentDirectory = file.getParent();
            this.mCurrentFileName = file.getName();
        }

        return file;
    }

    public ArrayList<File> getExtractedFileList() {
        return this.mFileList;
    }

    public boolean deleteExtractedFiles() {
        ArrayList<File> dirlist = new ArrayList<File>();
        for (int ii = 0; ii < this.mFileList.size(); ii++) {
            File f = (File) this.mFileList.get(ii);
            if (f.isDirectory())
                dirlist.add(f);
            f.delete();
        }
        for (int ii = dirlist.size() - 1; ii >= 0; ii--) {
            File f = (File) dirlist.get(ii);
            f.delete();
        }
        this.mFileList.clear();
        return true;
    }

}

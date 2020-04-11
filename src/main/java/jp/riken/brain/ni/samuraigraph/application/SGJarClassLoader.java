package jp.riken.brain.ni.samuraigraph.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SGJarClassLoader extends ClassLoader {
    
    private final ZipFile mZipFile;
    
    private static Hashtable<String, Object> mHash = new Hashtable<String, Object>();
    
    private boolean mDelegateFirst = false;
    
    private static final Object CLASS_OBJECT = new Object();
    
    public SGJarClassLoader(ZipFile zip) {
        this.mZipFile = zip;
    }
    
    public void setDelegateFirst(boolean delegate) {
        this.mDelegateFirst = delegate;
    }
    
    @Override
    public Class<?> loadClass(String name, boolean resolve)
    throws ClassNotFoundException {
        
        if (this.mDelegateFirst) {
            return this.loadFromParent(name);
        } else {
        }

        Object obj = mHash.get(name);
        if (obj instanceof SGJarClassLoader) {
            SGJarClassLoader classLoader = (SGJarClassLoader)obj;
            try {
                return classLoader._loadClass(mZipFile, name, resolve);
            } catch (ClassNotFoundException cnf2) {
                mHash.put(name, CLASS_OBJECT);
                throw cnf2;
            }
        }
        
        return this._loadClass(mZipFile, name, resolve);
    }
    
    private synchronized Class<?> _loadClass(ZipFile zipFile, String className, boolean resolveIt)
    throws ClassNotFoundException {
        mHash.put(className, this);
        this.mDelegateFirst = true;

        synchronized(this) {
            Class<?> cls = super.findLoadedClass(className);
            if(cls != null) {
                if(resolveIt) {
                    super.resolveClass(cls);
                }
                return cls;
            }

            try {
                this.definePackage(className);

                byte[] data = this.getClassBytesFromZipFile(zipFile, className);

                // convert bytes array to an instance of Class class.
                cls = super.defineClass(className, data, 0, data.length);

                if(resolveIt) {
                    super.resolveClass(cls);        // link given class
                }

                return cls;
            } catch(IOException io) {
                // failed to read jar file.
                throw new ClassNotFoundException(className);
            }
        }
    }
    
    /**
     * Get file data of given class from jar file.
     * @param zip
     * @param className
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private byte[] getClassBytesFromZipFile(ZipFile zip, String className)
    throws ClassNotFoundException, IOException {
        
        String name = SGJarClassLoader.classToFile(className);
        
        ZipEntry entry = zip.getEntry(name);      // get file entry from zip file.
        if (entry == null) {
            throw new ClassNotFoundException(className);
        }

        InputStream in = zip.getInputStream(entry);

        int len = (int)entry.getSize();
        byte[] data = new byte[len];
        int success = 0;
        int offset = 0;
        
        while(success < len) {
            len -= success;
            offset += success;
            success = in.read(data, offset, len);
            if(-1==success) {
                throw new ClassNotFoundException(className);
            }
        }
        
        return data;
    }
    
    /**
     * Define package of a given class.
     * @param className
     * @throws IOException
     */
    private void definePackage(String className) throws IOException {
        int idx = className.lastIndexOf('.');
        if (idx != -1) {
            String name = className.substring(0, idx);      // package name of given class
            if (getPackage(name) == null) {
                definePackage(name, null, null, null, null, null, null, null);   // define package by using its name.
                return;
            }
        }
    }
    
    /**
     * Classloader that loads this classloader loads given class.
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    private Class<?> loadFromParent(final String className)
    throws ClassNotFoundException {
        Class<?> cls;

        ClassLoader parentLoader = getClass().getClassLoader();
        if (parentLoader != null) {
            cls = parentLoader.loadClass(className);
        } else {
            cls = findSystemClass(className);
        }
        return cls;
    }

    /**
     * Convert class name to file name.
     * @param name
     * @return
     */
    public static String classToFile(final String name) {
        return name.replace('.','/').concat(".class");
    }
    
}

package org.freehep.graphicsbase.util.export;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class ExportFileTypeRegistry {
    private static ExportFileTypeRegistry registry;
    private static ClassLoader loader;
    private List<ExportFileType> extraTypes = new ArrayList<ExportFileType>();

    private ExportFileTypeRegistry() {
    }

    public static synchronized ExportFileTypeRegistry getDefaultInstance(ClassLoader loader) {
        if (loader != null && ExportFileTypeRegistry.loader != null && loader != ExportFileTypeRegistry.loader) {
            throw new RuntimeException("ExportFileTypeRegistry: Different classloader was already used in getDefaultInstance");
        }
        if (loader != null) {
            ExportFileTypeRegistry.loader = loader;
        }
        if (registry == null) {
            registry = new ExportFileTypeRegistry();
            addApplicationClasspathExportFileTypes(registry);
        }
        return registry;
    }

    public List<ExportFileType> get() {
        return get(null);
    }

    public List<ExportFileType> get(String extension) {
        List<ExportFileType> list = new ArrayList<ExportFileType>();
        
        ClassLoader classLoader = loader != null ? loader : Thread.currentThread().getContextClassLoader();
        ServiceLoader<ExportFileType> serviceLoader = ServiceLoader.load(ExportFileType.class, classLoader);
        addExportFileTypeToList(list, extension, serviceLoader.iterator());
        
        addExportFileTypeToList(list, extension, extraTypes.iterator());
        
        return list;
    }

    public void add(ExportFileType type) {
        extraTypes.add(type);
    }

    private void addExportFileTypeToList(List<ExportFileType> list, String extension, Iterator<ExportFileType> iterator) {
        while (iterator.hasNext()) {
            ExportFileType type = iterator.next();
            if (extension == null) {
                if (!list.contains(type)) {
                    list.add(type);
                }
            } else {
                String[] extensions = type.getExtensions();
                for (int i = 0; i < extensions.length; i++) {
                    if (extensions[i].equalsIgnoreCase(extension)) {
                        if (!list.contains(type)) {
                            list.add(type);
                        }
                        break;
                    }
                }
            }
        }
    }

    private static void addApplicationClasspathExportFileTypes(ExportFileTypeRegistry registry) {
        // No-op: ServiceLoader.load is called on-demand in the get method
    }
}

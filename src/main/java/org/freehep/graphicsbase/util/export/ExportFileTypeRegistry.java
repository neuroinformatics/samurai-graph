package org.freehep.graphicsbase.util.export;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Replacement for the library's ExportFileTypeRegistry which uses javax.imageio.spi.ServiceRegistry
 * — that class rejects ExportFileType because it is not an ImageIO SPI class. This implementation
 * manually reads META-INF/services files and skips providers that do not actually implement
 * ExportFileType (e.g. ImageIOExportFileType only implements RegisterableService).
 */
public class ExportFileTypeRegistry {
  private static final String SERVICE_FILE = "META-INF/services/" + ExportFileType.class.getName();

  private static ExportFileTypeRegistry registry;
  private static ClassLoader loader;
  private List<ExportFileType> discoveredTypes = new ArrayList<ExportFileType>();
  private List<ExportFileType> extraTypes = new ArrayList<ExportFileType>();

  private ExportFileTypeRegistry() {
    ClassLoader classLoader =
        loader != null ? loader : Thread.currentThread().getContextClassLoader();
    discoveredTypes = loadExportFileTypeProviders(classLoader);
  }

  public static synchronized ExportFileTypeRegistry getDefaultInstance(ClassLoader loader) {
    if (loader != null
        && ExportFileTypeRegistry.loader != null
        && loader != ExportFileTypeRegistry.loader) {
      throw new RuntimeException(
          "ExportFileTypeRegistry: Different classloader was already used in getDefaultInstance");
    }
    if (loader != null) {
      ExportFileTypeRegistry.loader = loader;
    }
    if (registry == null) {
      registry = new ExportFileTypeRegistry();
    }
    return registry;
  }

  public List<ExportFileType> get() {
    return get(null);
  }

  public List<ExportFileType> get(String extension) {
    List<ExportFileType> list = new ArrayList<ExportFileType>();
    addExportFileTypeToList(list, extension, discoveredTypes.iterator());
    addExportFileTypeToList(list, extension, extraTypes.iterator());
    return list;
  }

  public void add(ExportFileType type) {
    extraTypes.add(type);
  }

  private void addExportFileTypeToList(
      List<ExportFileType> list, String extension, Iterator<ExportFileType> iterator) {
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

  /** Reads META-INF/services files and instantiates valid ExportFileType providers. */
  private static List<ExportFileType> loadExportFileTypeProviders(ClassLoader classLoader) {
    List<ExportFileType> types = new ArrayList<ExportFileType>();
    try {
      Enumeration<URL> urls = classLoader.getResources(SERVICE_FILE);
      while (urls.hasMoreElements()) {
        URL url = urls.nextElement();
        try (InputStream is = url.openStream();
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
          String line;
          while ((line = reader.readLine()) != null) {
            int ci = line.indexOf('#');
            if (ci >= 0) line = line.substring(0, ci);
            line = line.trim();
            if (line.isEmpty()) continue;

            try {
              Class<?> clazz = Class.forName(line, true, classLoader);
              if (ExportFileType.class.isAssignableFrom(clazz)) {
                ExportFileType type = (ExportFileType) clazz.getDeclaredConstructor().newInstance();
                types.add(type);
              }
              // Silently skip classes that do not implement ExportFileType
            } catch (Exception e) {
              // Class not available or failed to instantiate, skip silently
            }
          }
        }
      }
    } catch (IOException e) {
      // No service files found, return empty list
    }
    return types;
  }
}

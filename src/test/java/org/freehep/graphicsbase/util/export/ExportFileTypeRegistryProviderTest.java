package org.freehep.graphicsbase.util.export;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ExportFileTypeRegistry} classloader-mismatch guard and the META-INF/services
 * provider-loading branches (comments, blank lines, non-assignable classes, and missing classes).
 *
 * <p>The provider branches are exercised by a test-only services file at {@code
 * src/test/resources/META-INF/services/}, which lists a valid provider, a class that does not
 * implement {@link ExportFileType}, and a class that does not exist.
 */
class ExportFileTypeRegistryProviderTest {

  /** A valid {@link ExportFileType} provider referenced by the test services file. */
  static class TestProvider extends ExportFileType {
    @Override
    public String getDescription() {
      return "Registry Provider Test Type";
    }

    @Override
    public String[] getExtensions() {
      return new String[] {"smt"};
    }

    @Override
    public String[] getMIMETypes() {
      return new String[] {"application/smt"};
    }

    @Override
    public void exportToFile(
        OutputStream out,
        Component[] components,
        Component master,
        Properties properties,
        String format)
        throws IOException {}

    @Override
    public void exportToFile(
        File file, Component[] components, Component master, Properties properties, String format)
        throws IOException {}
  }

  /** Reset static state before each test. */
  @BeforeEach
  void resetRegistry() throws Exception {
    Class<?> clazz = ExportFileTypeRegistry.class;
    Field registryField = clazz.getDeclaredField("registry");
    registryField.setAccessible(true);
    registryField.set(null, null);

    Field loaderField = clazz.getDeclaredField("loader");
    loaderField.setAccessible(true);
    loaderField.set(null, null);
  }

  @Test
  void getDefaultInstanceRejectsADifferentClassLoader() {
    // The first call binds the registry to the supplied loader.
    ExportFileTypeRegistry first =
        ExportFileTypeRegistry.getDefaultInstance(TestProvider.class.getClassLoader());
    assertEquals(first, ExportFileTypeRegistry.getDefaultInstance(null));
    // A second, distinct loader is rejected.
    ClassLoader other = new ClassLoader() {};
    assertThrows(RuntimeException.class, () -> ExportFileTypeRegistry.getDefaultInstance(other));
  }

  @Test
  void providerLoadingHandlesCommentsBlanksNonAssignableAndMissingClasses() {
    // The test services file is read alongside the main one. Its valid provider is discovered,
    // which proves the comment, blank, non-assignable and missing lines in the same file were
    // iterated and handled without an exception escaping.
    ExportFileTypeRegistry registry =
        ExportFileTypeRegistry.getDefaultInstance(TestProvider.class.getClassLoader());
    List<ExportFileType> all = registry.get();
    assertEquals(1, countByDescription(all, "Registry Provider Test Type"));
    // The valid provider resolves by its own extension.
    assertEquals(1, registry.get("smt").size());
    assertEquals("application/smt", registry.get("smt").get(0).getMIMETypes()[0]);
  }

  /** Helper: count types matching a description. */
  private int countByDescription(List<ExportFileType> types, String desc) {
    int count = 0;
    for (ExportFileType t : types) {
      if (t.getDescription().equals(desc)) count++;
    }
    return count;
  }
}

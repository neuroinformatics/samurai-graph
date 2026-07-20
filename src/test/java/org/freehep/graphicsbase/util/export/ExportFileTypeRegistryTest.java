package org.freehep.graphicsbase.util.export;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Basic smoke tests for {@link ExportFileTypeRegistry}. */
class ExportFileTypeRegistryTest {

  /** Simple test ExportFileType implementation. */
  static class TestExportFileType extends ExportFileType {
    @Override
    public String getDescription() {
      return "Test Type";
    }

    @Override
    public String[] getExtensions() {
      return new String[] {"test"};
    }

    @Override
    public String[] getMIMETypes() {
      return new String[] {"application/test"};
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
  void getDefaultInstanceReturnsNonNull() {
    ExportFileTypeRegistry registry = ExportFileTypeRegistry.getDefaultInstance(null);
    assertNotNull(registry);
  }

  @Test
  void addAndGetIncludesAddedType() throws Exception {
    ExportFileTypeRegistry registry = ExportFileTypeRegistry.getDefaultInstance(null);
    TestExportFileType testType = new TestExportFileType();
    registry.add(testType);

    List<ExportFileType> types = registry.get();
    // Classpath may contribute discovered types; verify our added type is present
    assertEquals(1, countTypes(types, "Test Type"));
  }

  /** Helper: count types matching a description. */
  private int countTypes(List<ExportFileType> types, String desc) {
    int count = 0;
    for (ExportFileType t : types) {
      if (t.getDescription().equals(desc)) count++;
    }
    return count;
  }

  @Test
  void getWithExtensionMatchesCorrectType() throws Exception {
    ExportFileTypeRegistry registry = ExportFileTypeRegistry.getDefaultInstance(null);
    TestExportFileType testType = new TestExportFileType();
    registry.add(testType);

    List<ExportFileType> types = registry.get("test");
    assertEquals(1, types.size());

    List<ExportFileType> noMatch = registry.get("xyz");
    assertTrue(noMatch.isEmpty());
  }
}

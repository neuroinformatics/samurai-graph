package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests of the embedded command loader with fake reader and actions. */
class SGEmbeddedCommandLoaderTest {

  private static final class FakeReader implements SGEmbeddedContentReader {
    String command = null;
    String properties = null;
    final String path = "/embedded-test/data.nc";

    @Override
    public String getCommand() {
      return this.command;
    }

    @Override
    public String getProperties() {
      return this.properties;
    }

    @Override
    public String getPath() {
      return this.path;
    }
  }

  private static final class FakeActions implements SGEmbeddedActions {
    boolean execResult = true;
    boolean applyResult = true;
    final List<String> execCalls = new ArrayList<String>();
    final List<String> applyCalls = new ArrayList<String>();

    @Override
    public boolean executeEmbeddedCommands(
        final String commands,
        final String path,
        final jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow wnd) {
      this.execCalls.add(path + "::" + commands);
      return this.execResult;
    }

    @Override
    public boolean applyEmbeddedProperties(
        final String commands,
        final String path,
        final jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow wnd) {
      this.applyCalls.add(path + "::" + commands);
      return this.applyResult;
    }
  }

  @Test
  void doesNothingWhenNoContents() {
    final FakeReader reader = new FakeReader();
    final FakeActions actions = new FakeActions();
    final SGEmbeddedCommandLoader loader = new SGEmbeddedCommandLoader();
    assertFalse(loader.executeEmbeddedCommands(reader, null, actions));
    assertFalse(loader.applyEmbeddedProperties(reader, null, actions));
    assertTrue(actions.execCalls.isEmpty());
    assertTrue(actions.applyCalls.isEmpty());
  }

  @Test
  void executesEmbeddedCommandsWithPath() {
    final FakeReader reader = new FakeReader();
    reader.command = "print x;";
    final FakeActions actions = new FakeActions();
    final SGEmbeddedCommandLoader loader = new SGEmbeddedCommandLoader();
    assertTrue(loader.executeEmbeddedCommands(reader, null, actions));
    assertEquals(
        java.util.Collections.singletonList(reader.path + "::print x;"), actions.execCalls);
    assertTrue(actions.applyCalls.isEmpty());
  }

  @Test
  void returnsExecutionResultOfCommands() {
    final FakeReader reader = new FakeReader();
    reader.command = "print x;";
    final FakeActions actions = new FakeActions();
    actions.execResult = false;
    final SGEmbeddedCommandLoader loader = new SGEmbeddedCommandLoader();
    assertFalse(loader.executeEmbeddedCommands(reader, null, actions));
    assertEquals(1, actions.execCalls.size());
  }

  @Test
  void appliesEmbeddedPropertiesWithPath() {
    final FakeReader reader = new FakeReader();
    reader.properties = "<properties/>";
    final FakeActions actions = new FakeActions();
    final SGEmbeddedCommandLoader loader = new SGEmbeddedCommandLoader();
    assertTrue(loader.applyEmbeddedProperties(reader, null, actions));
    assertEquals(
        java.util.Collections.singletonList(reader.path + "::<properties/>"), actions.applyCalls);
    assertTrue(actions.execCalls.isEmpty());
  }

  @Test
  void returnsApplicationResultOfProperties() {
    final FakeReader reader = new FakeReader();
    reader.properties = "<properties/>";
    final FakeActions actions = new FakeActions();
    actions.applyResult = false;
    final SGEmbeddedCommandLoader loader = new SGEmbeddedCommandLoader();
    assertFalse(loader.applyEmbeddedProperties(reader, null, actions));
    assertEquals(1, actions.applyCalls.size());
  }
}

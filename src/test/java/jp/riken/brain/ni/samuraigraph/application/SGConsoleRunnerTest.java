package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Unit tests for the console command reading loop of {@link SGConsoleRunner}. */
class SGConsoleRunnerTest {

  private static class RecordingExecutor implements SGConsoleCommandExecutor {

    final ArrayList<String> commands = new ArrayList<>();

    int nextStatus = SGApplicationCommandConstants.STATUS_SUCCEEDED;

    boolean dialogOpen = false;

    @Override
    public int exec(String line) {
      this.commands.add(line);
      return this.nextStatus;
    }

    @Override
    public boolean isDialogOpen() {
      return this.dialogOpen;
    }

    @Override
    public boolean closeTextField() {
      return true;
    }
  }

  @TempDir Path tempDir;

  private RecordingExecutor executor;

  @BeforeEach
  void setUp() {
    this.executor = new RecordingExecutor();
  }

  private String run(String input) throws Exception {
    SGConsoleRunner runner = new SGConsoleRunner(this.executor);
    runner.setInputStream(new BufferedReader(new StringReader(input)));
    StringWriter out = new StringWriter();
    runner.setOutputStream(new BufferedWriter(out));
    runner.startReadingInput();
    return out.toString();
  }

  private String runInFile(String scriptContent) throws Exception {
    Path script = this.tempDir.resolve("script.txt");
    Files.writeString(script, scriptContent);
    return this.run("<< " + script.toFile().getPath() + "\n");
  }

  @Test
  void successStatusIsEchoedWithPrompt() throws Exception {
    String out = this.run("figure new\n");
    assertTrue(out.startsWith("$ succeeded: figure new\n"));
    assertEquals(1, this.executor.commands.size());
    assertEquals("figure new", this.executor.commands.get(0));
  }

  @Test
  void statusPrefixesReflectTheCommandResult() throws Exception {
    Map<Integer, String> table = new java.util.LinkedHashMap<>();
    table.put(SGApplicationCommandConstants.STATUS_SUCCEEDED, "succeeded: ");
    table.put(SGApplicationCommandConstants.STATUS_FAILED, "failed: ");
    table.put(SGApplicationCommandConstants.STATUS_NOT_FOUND, "not found: ");
    table.put(SGApplicationCommandConstants.STATUS_PARTIALLY_FAILED, "partially failed: ");
    for (Map.Entry<Integer, String> entry : table.entrySet()) {
      this.executor.nextStatus = entry.getKey().intValue();
      String out = this.run("probe\n");
      assertTrue(out.startsWith("$ " + entry.getValue() + "probe\n"));
      this.executor = new RecordingExecutor();
      this.executor.nextStatus = 0;
    }
  }

  @Test
  void emptyAndCommentLinesDoNotExecuteCommands() throws Exception {
    String out = this.run("\n# comment\n// another\n   \nactual\n");
    assertEquals(1, this.executor.commands.size());
    assertEquals("actual", this.executor.commands.get(0));
    assertTrue(out.contains("$ succeeded: actual\n"));
  }

  @Test
  void openDialogBlocksExecution() throws Exception {
    this.executor.dialogOpen = true;
    String out = this.run("cmd\n");
    assertTrue(out.startsWith("$ Dialog is open.\n"));
    assertTrue(this.executor.commands.isEmpty());
  }

  @Test
  void missingFileInputReportsFileNotFound() throws Exception {
    String out = this.run("<< missing-script.txt\n");
    assertTrue(out.contains("file not found: missing-script.txt\n"));
    assertTrue(this.executor.commands.isEmpty());
  }

  @Test
  void quotedFileInputPathIsUnquotedBeforeOpen() throws Exception {
    Path script = this.tempDir.resolve("script");
    Files.writeString(script, "inner\n");
    String out = this.run("<< \"" + script.toFile().getPath() + "\"\n");
    assertEquals(1, this.executor.commands.size());
    assertEquals("inner", this.executor.commands.get(0));
  }

  @Test
  void blockCommentsInFileInputDoNotExecuteCommands() throws Exception {
    String out = this.runInFile("/* skipped\nstill commented\n*/\nactual\n");
    assertEquals(1, this.executor.commands.size());
    assertEquals("actual", this.executor.commands.get(0));
  }

  @Test
  void deepRecursionStopsSafely() throws Exception {
    final int depth = 14;
    for (int ii = 1; ii < depth; ii++) {
      Path nested = this.tempDir.resolve("s" + ii + ".txt");
      Path next = this.tempDir.resolve("s" + (ii + 1) + ".txt");
      Files.writeString(nested, "<< " + next.toFile().getPath() + "\ncmd" + ii + "\n");
    }
    Path first = this.tempDir.resolve("s0.txt");
    Path second = this.tempDir.resolve("s1.txt");
    Files.writeString(first, "<< " + second.toFile().getPath() + "\ncmd0\n");
    String out = this.run("<< " + first.toFile().getPath() + "\n");
    assertTrue(out.contains("Recursion is too deep.\n"), () -> "OUT=" + out);
    assertTrue(this.executor.commands.size() < depth);
  }
}

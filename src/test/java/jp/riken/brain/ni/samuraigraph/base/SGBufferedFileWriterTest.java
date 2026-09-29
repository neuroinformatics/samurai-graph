package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Unit tests of the buffered file writer. */
class SGBufferedFileWriterTest {

  @TempDir Path tempDir;

  private static String read(final Path file, final java.nio.charset.Charset charset)
      throws Exception {
    final StringBuilder sb = new StringBuilder();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile()), charset))) {
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
        sb.append('\n');
      }
    }
    return sb.toString();
  }

  @Test
  void writesTextToTheGivenPath() throws Exception {
    final Path file = this.tempDir.resolve("out.txt");
    final SGBufferedFileWriter writer = new SGBufferedFileWriter(file.toString());
    writer.getBufferedWriter().write("hello");
    writer.getBufferedWriter().newLine();
    writer.getBufferedWriter().write("world");
    writer.close();
    assertEquals("hello\nworld\n", read(file, StandardCharsets.UTF_8));
  }

  @Test
  void writesTextWithTheGivenCharacterSet() throws Exception {
    final Path file = this.tempDir.resolve("out-sjis.txt");
    final SGBufferedFileWriter writer = new SGBufferedFileWriter(file.toString(), "Shift_JIS");
    writer.getBufferedWriter().write("日本語");
    writer.close();
    assertEquals("日本語\n", read(file, java.nio.charset.Charset.forName("Shift_JIS")));
  }

  @Test
  void nullCharacterSetFallsBackToUtf8() throws Exception {
    final Path file = this.tempDir.resolve("out-null.txt");
    final SGBufferedFileWriter writer = new SGBufferedFileWriter(file.toString(), null);
    writer.getBufferedWriter().write("テキスト");
    writer.close();
    assertEquals("テキスト\n", read(file, StandardCharsets.UTF_8));
  }

  @Test
  void closeIsIdempotent() throws Exception {
    final Path file = this.tempDir.resolve("out-close.txt");
    final SGBufferedFileWriter writer = new SGBufferedFileWriter(file.toString());
    writer.getBufferedWriter().write("data");
    writer.close();
    writer.close();
    assertTrue(Files.exists(file));
  }
}

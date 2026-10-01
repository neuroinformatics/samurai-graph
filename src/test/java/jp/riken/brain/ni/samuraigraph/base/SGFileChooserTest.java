package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Container;
import java.awt.Window;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Characterization tests for the file chooser wrapper: the extension normalization and overwrite
 * guard of {@code approveSelection}, the directory-rejecting {@code setSelectedFile} and the public
 * {@code firePropertyChange}.
 *
 * <p>The overwrite-confirmation branch is driven headfully: the approval runs on the EDT while the
 * test thread watches for the modal confirmation dialog and dismisses it by clicking its first
 * button. The modal dialog runs a nested event pump, so the click is delivered while the EDT is
 * blocked inside {@code showConfirmDialog}.
 */
class SGFileChooserTest {

  @TempDir static Path dir = null;

  @Test
  void theConstructorTakesADirectoryAPathAndNothing() {
    final SGFileChooser fromDir = new SGFileChooser(dir.toFile());
    assertEquals(dir.toFile(), fromDir.getCurrentDirectory());
    final SGFileChooser fromPath = new SGFileChooser(dir.toString());
    assertEquals(dir.toFile(), fromPath.getCurrentDirectory());
    assertSame(JFileChooser.OPEN_DIALOG, new SGFileChooser().getDialogType());
  }

  @Test
  void theSetSelectedFileRejectsDirectoriesAndKeepsFiles() {
    final SGFileChooser chooser = new SGFileChooser(dir.toString());
    chooser.setSelectedFile(dir.toFile());
    assertNull(chooser.getSelectedFile());
    final File file = dir.resolve("data.csv").toFile();
    chooser.setSelectedFile(file);
    assertSame(file, chooser.getSelectedFile());
    chooser.setSelectedFile(null);
    assertNull(chooser.getSelectedFile());
  }

  @Test
  void theApproveSelectionNormalizesTheExtensionWithTheFilter() throws Exception {
    // a selected path without extension gets the first extension appended
    runApprove(
        dir.resolve("newplot").toFile(),
        "txt",
        false,
        (chooser, result) -> assertEquals(dir.resolve("newplot.txt").toFile(), result));
    // a selected path with another extension gets the filter extension appended
    runApprove(
        dir.resolve("other.dat").toFile(),
        "txt",
        false,
        (chooser, result) -> assertEquals(dir.resolve("other.dat.txt").toFile(), result));
    // a path that already matches keeps its extension as given
    runApprove(
        dir.resolve("existing.txt").toFile(),
        "txt",
        false,
        (chooser, result) -> assertEquals(dir.resolve("existing.txt").toFile(), result));
  }

  @Test
  void theApproveSelectionFallsBackToTheFirstExtension() throws Exception {
    // an extension matching no filter entry appends the first filter extension
    runApprove(
        dir.resolve("plot.xyz").toFile(),
        "mat",
        false,
        (chooser, result) -> assertEquals(dir.resolve("plot.xyz.mat").toFile(), result));
  }

  @Test
  void theApproveSelectionKeepsTheFileForOpenDialogsAndNonSGFilters() throws Exception {
    // an existing file in an open dialog is approved without confirmation;
    // the selected path is kept
    final File existing = dir.resolve("open.txt").toFile();
    existing.deleteOnExit();
    assertTrue(existing.createNewFile());
    runApprove(
        existing,
        "txt",
        true,
        (chooser, result) -> assertEquals(existing.getAbsolutePath(), result.getAbsolutePath()));
    // a plain (non SG) filter is left untouched by the normalization
    final File other = dir.resolve("plain").toFile();
    runApprove(
        other,
        null,
        true,
        (chooser, result) -> assertEquals(other.getAbsolutePath(), result.getAbsolutePath()));
  }

  @Test
  void theApproveSelectionSkipsEverythingWhenNoFileIsSelected() throws Exception {
    final SGFileChooser chooser = new SGFileChooser(dir.toString());
    chooser.setFileFilter(new SGExtensionFileFilter());
    final Object[] result = new Object[1];
    runOnEdt(chooser, () -> chooser.approveSelection(), result);
    assertNull(result[0]);
  }

  @Test
  void theApproveSelectionConfirmsBeforeOverwriting() throws Exception {
    final File existing = dir.resolve("overwrite.txt").toFile();
    assertTrue(existing.createNewFile());
    existing.deleteOnExit();
    // the save dialog on an existing file asks for confirmation; the
    // confirmation is dismissed, so the selection stays on the same path
    runApprove(
        existing,
        "txt",
        false,
        (chooser, result) -> assertEquals(existing.getAbsolutePath(), result.getAbsolutePath()));
  }

  @Test
  void theFirePropertyChangeIsPubliclyInvocable() {
    final SGFileChooser chooser = new SGFileChooser();
    final boolean[] fired = {false};
    chooser.addPropertyChangeListener(
        "selectedFile",
        e -> {
          fired[0] = true;
        });
    chooser.firePropertyChange("selectedFile", null, dir.toFile());
    assertTrue(fired[0]);
  }

  @Test
  void theFileFilterDescriptionListsTheExtensions() {
    final SGExtensionFileFilter filter = new SGExtensionFileFilter();
    filter.addExtension("txt");
    filter.addExtension(".csv");
    assertTrue(filter.getDescription().contains("*.txt"));
    assertTrue(filter.getDescription().contains("*.csv"));
  }

  // -- helpers -----------------------------------------------------------------

  private void runApprove(
      final File selected,
      final String extension,
      final boolean openDialog,
      final BiConsumer<SGFileChooser, File> assertAfter)
      throws Exception {
    final SGFileChooser chooser = new SGFileChooser(dir.toString());
    chooser.setDialogType(openDialog ? JFileChooser.OPEN_DIALOG : JFileChooser.SAVE_DIALOG);
    if (extension != null) {
      final SGExtensionFileFilter filter = new SGExtensionFileFilter();
      filter.addExtension(extension);
      chooser.setFileFilter(filter);
    }
    chooser.setSelectedFile(selected);
    final Object[] result = new Object[1];
    runOnEdt(chooser, () -> chooser.approveSelection(), result);
    assertAfter.accept(chooser, (File) result[0]);
  }

  private void runOnEdt(final SGFileChooser chooser, final Runnable task, final Object[] result)
      throws Exception {
    final Thread approvalThread =
        new Thread(
            () -> {
              try {
                task.run();
              } catch (final Exception ex) {
                // an unexpected failure must not leave the join waiting forever
              }
            },
            "approve-selection");
    SwingUtilities.invokeAndWait(approvalThread::start);
    // if a modal confirmation dialog appears, dismiss it from this thread;
    // the modal dialog's nested event pump delivers the click
    dismissConfirmationDialogs(approvalThread);
    approvalThread.join(20000);
    assertFalse(approvalThread.isAlive(), "approveSelection finished on the EDT");
    SwingUtilities.invokeAndWait(
        () -> {
          result[0] = chooser.getSelectedFile();
        });
  }

  private void dismissConfirmationDialogs(final Thread approvalThread) throws Exception {
    final long deadline = System.currentTimeMillis() + 20000;
    while (System.currentTimeMillis() < deadline) {
      final JDialog dialog = findConfirmationDialog();
      if (dialog != null) {
        final JButton first = firstButton(dialog);
        if (first != null) {
          SwingUtilities.invokeAndWait(first::doClick);
        }
        // the dialog may take a moment to disappear
        Thread.sleep(100);
        continue;
      }
      if (!approvalThread.isAlive()) {
        return;
      }
      Thread.sleep(100);
    }
  }

  private static JDialog findConfirmationDialog() {
    for (Window w : Window.getWindows()) {
      if (w instanceof JDialog d
          && "Overwrite Confirmation".equals(d.getTitle())
          && d.isShowing()) {
        return d;
      }
    }
    return null;
  }

  private static JButton firstButton(final Container root) {
    final List<JButton> buttons = new ArrayList<>();
    collectButtons(root, buttons);
    return buttons.isEmpty() ? null : buttons.get(0);
  }

  private static void collectButtons(final Container c, final List<JButton> out) {
    for (java.awt.Component child : c.getComponents()) {
      if (child instanceof JButton b) {
        out.add(b);
      } else if (child instanceof Container cc) {
        collectButtons(cc, out);
      }
    }
  }
}

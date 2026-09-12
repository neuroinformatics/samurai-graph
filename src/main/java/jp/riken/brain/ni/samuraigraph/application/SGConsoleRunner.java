package jp.riken.brain.ni.samuraigraph.application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import jp.riken.brain.ni.samuraigraph.base.SGBufferedFileReader;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Reads commands from input streams, executes them through the main class and writes the status to
 * the output stream.
 */
final class SGConsoleRunner {

  private static final Logger logger = LogManager.getLogger(SGConsoleRunner.class);

  // the prompt
  private static final String PROMPT = "$ ";

  // a symbol for the file input stream
  private static final String FILE_INPUT = "<<";

  // the maximum number for the recursion of file input
  private static final int FILE_RECURSION_DEPTH_MAX = 10;

  // the header for a comment line
  private static final String COMMENT_HEADER = "#";

  // the header for a comment line
  private static final String COMMENT_HEADER_2 = "//";

  // a text string that represents the start of a block comment
  private static final String BLOCK_COMMENT_START = "/*";

  // a text string that represents the end of a block comment
  private static final String BLOCK_COMMENT_END = "*/";

  // a flag whether the current position is in a block comment
  private boolean mBlockComment = false;

  private final SGMainFunctions mMain;

  private BufferedReader mStdinReader = null;

  private BufferedWriter mStdoutWriter = null;

  SGConsoleRunner(SGMainFunctions main) {
    this.mMain = main;
  }

  /**
   * Sets the input stream.
   *
   * @param reader the input stream
   */
  void setInputStream(BufferedReader reader) {
    this.mStdinReader = reader;
  }

  /**
   * Sets the output stream.
   *
   * @param writer the output stream
   */
  void setOutputStream(BufferedWriter writer) {
    this.mStdoutWriter = writer;
  }

  /**
   * Returns whether the input and output streams are ready.
   *
   * @return true if both streams are ready
   */
  boolean hasStreams() {
    return this.mStdinReader != null && this.mStdoutWriter != null;
  }

  /** Reads the input recursively. */
  void startReadingInput() throws IOException {
    this.readRecursively(this.mStdinReader, this.mStdoutWriter, 0, true, false);
  }

  /**
   * Replaces the input stream by the system standard input after closing it.
   *
   * @throws IOException if an I/O error occurs
   */
  void restoreSystemInput() throws IOException {
    this.mStdinReader.close();
    this.mStdinReader = new BufferedReader(new InputStreamReader(System.in));
  }

  /**
   * Loads the command script file.
   *
   * @param fileName file name
   */
  void loadCommandScriptFile(final String fileName) {
    // creates and starts a thread
    new CommandThread(fileName);
  }

  private void readRecursively(
      BufferedReader br,
      BufferedWriter bw,
      int depth,
      final boolean showPrompt,
      final boolean inFile)
      throws IOException {

    // check whether the recursion depth is within range
    depth++;
    if (depth > FILE_RECURSION_DEPTH_MAX) {
      bw.write("Recursion is too deep.\n");
      bw.flush();
      return;
    }

    // infinite loop as a server process
    while (true) {

      if (showPrompt) {
        // display the prompt
        bw.write(PROMPT);
        bw.flush();
      }

      // get a line
      String line = br.readLine();
      if (line == null) {
        break;
      }

      // closes the text field
      this.mMain.closeTextField();

      // checks whether a dialog is open
      if (this.mMain.isDialogOpen()) {
        bw.write("Dialog is open.\n");
        bw.flush();
        continue;
      }

      // trim the line
      String tLine = line.trim();

      // skip an empty line
      if (tLine.length() == 0) {
        continue;
      }

      // skip the comment line
      if (tLine.startsWith(COMMENT_HEADER) || tLine.startsWith(COMMENT_HEADER_2)) {
        continue;
      }

      // start or end a block comment only when the file input stream is opened
      if (inFile) {

        // remove block comment.
        int sPos = tLine.indexOf(BLOCK_COMMENT_START);
        int ePos = tLine.indexOf(BLOCK_COMMENT_END);
        if (this.mBlockComment && ((sPos < 0 && ePos >= 0) || (ePos >= 0 && sPos > ePos + 1))) {
          // "... */" in block comment
          tLine = tLine.substring(ePos + 2);
          this.mBlockComment = false;
          sPos = tLine.indexOf(BLOCK_COMMENT_START);
          ePos = tLine.indexOf(BLOCK_COMMENT_END);
        }

        if (this.mBlockComment) {
          continue;
        }

        while (sPos >= 0 && ePos >= 1 && sPos + 1 < ePos) {
          //   "/* ... */"
          tLine = tLine.substring(0, sPos) + tLine.substring(ePos + 2);
          sPos = tLine.indexOf(BLOCK_COMMENT_START);
          ePos = tLine.indexOf(BLOCK_COMMENT_END);
        }

        sPos = tLine.indexOf(BLOCK_COMMENT_START);
        if (sPos >= 0) {
          // "/* ..."
          this.mBlockComment = true;
          tLine = tLine.substring(0, sPos);
        }
        tLine = tLine.trim();
        if (tLine.length() == 0) {
          continue;
        }
      }

      // interpret as a file path
      if (tLine.startsWith(FILE_INPUT)) {
        String path = tLine.substring(FILE_INPUT.length());
        path = path.trim();
        if (SGUtilityText.isDoubleQuoted(path)) {
          path = path.substring(1, path.length() - 1);
        }
        File file = new File(path);
        if (!file.exists()) {
          String errmsg = this.getFileNotFoundString(file);
          bw.write(errmsg);
          bw.flush();
          continue;
        }
        SGBufferedFileReader reader = new SGBufferedFileReader(path);
        BufferedReader br2 = reader.getBufferedReader();
        this.readRecursively(br2, bw, depth, false, true);
        reader.close();
        continue;
      }

      // parse the line and execute the command
      final int ret = this.mMain.exec(tLine);

      // output the status
      StringBuilder status = new StringBuilder();
      if (ret == SGMainFunctions.STATUS_FAILED) {
        status.append("failed: ");
      } else if (ret == SGMainFunctions.STATUS_NOT_FOUND) {
        status.append("not found: ");
      } else if (ret == SGMainFunctions.STATUS_SUCCEEDED) {
        status.append("succeeded: ");
      } else if (ret == SGMainFunctions.STATUS_PARTIALLY_FAILED) {
        status.append("partially failed: ");
      }
      status.append(tLine);
      status.append('\n');
      bw.write(status.toString());
      bw.flush();
    }
  }

  private String getFileNotFoundString(File file) {
    StringBuilder sb = new StringBuilder();
    sb.append("file not found: ");
    sb.append(file.getPath());
    sb.append('\n');
    return sb.toString();
  }

  class CommandThread extends Thread {

    private String mScriptFileName = null;

    CommandThread(final String script) {
      super();
      this.mScriptFileName = script;
      this.start();
    }

    /** Runs the thread. */
    public void run() {

      File sf = new File(this.mScriptFileName);
      SGBufferedFileReader reader = null;
      try {
        reader = new SGBufferedFileReader(this.mScriptFileName);
        BufferedReader brs = reader.getBufferedReader();
        try {
          // read the input script file
          SGConsoleRunner.this.readRecursively(
              brs, SGConsoleRunner.this.mStdoutWriter, 0, false, true);

          // show a prompt
          SGConsoleRunner.this.mStdoutWriter.write(PROMPT);
          SGConsoleRunner.this.mStdoutWriter.flush();
        } catch (IOException ex) {
          logger.debug("Exception occurred", ex);
        }
      } catch (FileNotFoundException e) {
        String errmsg = SGConsoleRunner.this.getFileNotFoundString(sf);
        try {
          SGConsoleRunner.this.mStdoutWriter.write(errmsg);
          SGConsoleRunner.this.mStdoutWriter.flush();
        } catch (IOException e1) {
          logger.debug("Failed to write error message to stdout", e1);
        }
      } catch (IOException e) {
        logger.debug("Exception occurred", e);
      } finally {
        if (reader != null) {
          reader.close();
        }
      }
    }
  }
}

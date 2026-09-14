package jp.riken.brain.ni.samuraigraph.application;

/**
 * Executes console commands for {@link SGConsoleRunner}. The implementation returns one of the
 * status values of {@link SGApplicationCommandConstants}.
 */
interface SGConsoleCommandExecutor {

  /**
   * Executes a given line.
   *
   * @param line a line of command
   * @return the command status
   */
  public int exec(String line);

  /** Returns whether a dialog is open. */
  boolean isDialogOpen();

  /** Closes the text field. */
  boolean closeTextField();
}

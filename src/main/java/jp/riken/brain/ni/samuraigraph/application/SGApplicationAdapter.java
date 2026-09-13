package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Desktop;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenFilesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import java.awt.desktop.QuitStrategy;

public class SGApplicationAdapter implements AboutHandler, QuitHandler, OpenFilesHandler {

  private final SGMainFunctions mMain;

  private SGApplicationAdapter(final SGMainFunctions main) {
    this.mMain = main;
  }

  /** Register application menu handler hooks using java.awt.Desktop API. */
  public static void registerApplication(final SGMainFunctions main) {
    if (Desktop.isDesktopSupported()) {
      Desktop desktop = Desktop.getDesktop();
      if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
        SGApplicationAdapter adapter = new SGApplicationAdapter(main);
        desktop.setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
        desktop.setAboutHandler(adapter);
        desktop.setQuitHandler(adapter);
        desktop.setOpenFileHandler(adapter);
      }
    }
  }

  /** Handle application about event */
  @Override
  public void handleAbout(AboutEvent event) {
    jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow wnd = this.mMain.getActiveWindow();
    if (wnd != null) {
      this.mMain.showAboutDialog(wnd);
    }
  }

  /** Handle application quit event */
  @Override
  public void handleQuitRequestWith(QuitEvent event, QuitResponse response) {
    this.mMain.exit();
    response.performQuit();
  }

  /** Handle open file event */
  @Override
  public void openFiles(OpenFilesEvent event) {
    jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow wnd = this.mMain.getActiveWindow();
    if (wnd != null) {
      for (java.io.File file : event.getFiles()) {
        this.mMain.openFile(file.getAbsolutePath(), wnd);
      }
    }
  }
}

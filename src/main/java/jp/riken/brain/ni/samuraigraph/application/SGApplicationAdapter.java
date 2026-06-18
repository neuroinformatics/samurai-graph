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
  private static SGApplicationAdapter mAdapter;

  private SGApplicationAdapter() {}

  /** Register application menu handler hooks using java.awt.Desktop API. */
  public static void registerApplication() {
    if (mAdapter == null) {
      mAdapter = new SGApplicationAdapter();
    }
    if (Desktop.isDesktopSupported()) {
      Desktop desktop = Desktop.getDesktop();
      if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
        desktop.setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
        desktop.setAboutHandler(mAdapter);
        desktop.setQuitHandler(mAdapter);
        desktop.setOpenFileHandler(mAdapter);
      }
    }
  }

  /** Handle application about event */
  @Override
  public void handleAbout(AboutEvent event) {
    SGDrawingServer.aboutHandler();
  }

  /** Handle application quit event */
  @Override
  public void handleQuitRequestWith(QuitEvent event, QuitResponse response) {
    if (SGDrawingServer.quitHandler() == false) {
      response.cancelQuit();
    } else {
      response.performQuit();
    }
  }

  /** Handle open file event */
  @Override
  public void openFiles(OpenFilesEvent event) {
    for (java.io.File file : event.getFiles()) {
      SGDrawingServer.openFileHandler(file.getAbsolutePath());
    }
  }
}

package jp.riken.brain.ni.samuraigraph.base;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * BrowserLauncher is a class that provides one static method, openURL, which opens the default web
 * browser for the current user of the system to the given URL. It may support other protocols
 * depending on the system -- mailto, ftp, etc. -- but that has not been rigorously tested and is
 * not guaranteed to work.
 *
 * <p>This implementation uses {@link Desktop#browse(URI)} which is available since Java 6. On
 * platforms where the default browser cannot be launched (e.g., headless environments), this method
 * will throw an {@link IOException}.
 */
public class SGBrowserLauncher {

  private SGBrowserLauncher() {
    /* nothing */
  }

  /**
   * Attempts to open the default web browser to the given URL.
   *
   * @param url The URL to open
   * @throws IOException If the web browser could not be located or does not run
   */
  public static void openURL(String url) throws IOException {
    if (!Desktop.isDesktopSupported()) {
      throw new IOException("Desktop API is not supported on this platform");
    }
    Desktop desktop = Desktop.getDesktop();
    if (!desktop.isSupported(Desktop.Action.BROWSE)) {
      throw new IOException("BROWSE action is not supported on this platform");
    }
    try {
      // Validate that the URL is a well-formed URL
      final URI uri = URI.create(url);
      if (uri.toURL() != null) {
        desktop.browse(uri);
      }
    } catch (IllegalArgumentException | MalformedURLException e) {
      throw new IOException("Invalid URL: " + url, e);
    }
  }
}

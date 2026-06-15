package jp.riken.brain.ni.samuraigraph.base;

import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility for executing tasks on a background thread while keeping the EDT responsive. This
 * replaces the foxtrot library's Worker.post() functionality using only public Java APIs.
 *
 * <p>Usage: must be called from the EDT. The task runs on a background thread, while the EDT pumps
 * AWT events to stay responsive. When the task completes, the result is returned.
 */
public class SGAsyncWorker {

  private SGAsyncWorker() {}

  /**
   * Execute a task on a background thread, pumping AWT events on the EDT while waiting.
   *
   * @param <T> the result type
   * @param task the task to execute
   * @return the result of the task
   * @throws Exception if the task throws an exception
   */
  public static <T> T post(Callable<T> task) throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<T> resultRef = new AtomicReference<>();
    AtomicReference<Exception> errorRef = new AtomicReference<>();

    Thread worker =
        new Thread(
            () -> {
              try {
                resultRef.set(task.call());
              } catch (Exception e) {
                errorRef.set(e);
              } finally {
                latch.countDown();
              }
            },
            "SGAsyncWorker");
    worker.setDaemon(true);
    worker.start();

    pumpEventsWhileWaiting(latch);

    Exception error = errorRef.get();
    if (error != null) {
      throw error;
    }
    return resultRef.get();
  }

  /**
   * Pump AWT events on the EDT while the latch is not yet counted down.
   *
   * <p>Uses a SecondaryLoop with a periodic timer to continuously process events. This is the
   * public-API equivalent of foxtrot's reflection-based event pumping.
   */
  private static void pumpEventsWhileWaiting(CountDownLatch latch) {
    SecondaryLoop loop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
    ActionListener pumpTimer =
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (latch.getCount() == 0) {
              loop.exit();
            }
          }
        };

    javax.swing.Timer timer = new javax.swing.Timer(10, pumpTimer);
    timer.setRepeats(true);
    timer.start();

    loop.enter();
    timer.stop();
  }
}

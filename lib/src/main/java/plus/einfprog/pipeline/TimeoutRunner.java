package plus.einfprog.pipeline;

import java.util.concurrent.*;

/**
 * <p>Runs tasks on a separate thread and waits at most a given time for their result.
 * It is used to call student code (and its {@code toString} methods) so that endless loops
 * don't block the test run.</p>
 *
 * <p>Each runner owns a cached thread pool of daemon threads.
 * Runners are meant to be held in {@code static final} fields.</p>
 *
 * <p><strong>Limitation:</strong> on a timeout, the task is only <em>interrupted</em>
 * ({@link Future#cancel(boolean) cancel(true)}). Java cannot force a thread to stop, so code that never checks
 * the interrupt flag or blocks (e.g. {@code while (true) {}}) keeps running in the background until the JVM exits.</p>
 */
public class TimeoutRunner {

    private final ExecutorService executor;

    /**
     * Creates a runner with its own thread pool.
     *
     * @param threadName the name of the pool's threads, shown in thread dumps
     */
    public TimeoutRunner(String threadName) {
        executor = Executors.newCachedThreadPool((r) -> {
            Thread t = new Thread(r, threadName);
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Runs the task on a pool thread and blocks until it finishes or the timeout expires.
     *
     * <p>A {@link RuntimeException} thrown by the task is rethrown unchanged. Any other {@link Throwable}
     * (checked exceptions and {@link Error}s), as well as an interrupt of the calling thread,
     * is wrapped in a {@link RuntimeException}.</p>
     *
     * @param timeout the maximum time to wait
     * @param unit    the unit of {@code timeout}
     * @param task    the task to run
     * @param <T>     the result type of the task
     * @return the result of the task
     * @throws TimeoutException if the task didn't finish in time; it is then interrupted, but may keep running
     */
    public <T> T submit(long timeout, TimeUnit unit, Callable<T> task) throws TimeoutException {
        Future<T> future = executor.submit(task);
        try {
            return future.get(timeout, unit);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException r) {
                throw r;
            } else {
                throw new RuntimeException("an unexpected error occurred", e);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("an unexpected error occurred", e);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        }
    }

}

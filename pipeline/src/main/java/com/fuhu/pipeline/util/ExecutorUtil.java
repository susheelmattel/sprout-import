package com.fuhu.pipeline.util;

import com.fuhu.pipeline.internal.PipeLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorUtil {
    private static final String TAG = ExecutorUtil.class.getSimpleName();

    /**
     * Shutdown to reject incoming tasks, and then calling shutdownNow.
     */
    public static void shutdownAndAwaitTermination(final ExecutorService pool) {
        if (pool != null) {
            pool.shutdown(); // Disable new tasks from being submitted
            try {
                // Wait a while for existing tasks to terminate
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    pool.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                        PipeLog.e(TAG, "Pool did not terminate");
                }
            } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                pool.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }
        }
    }
}

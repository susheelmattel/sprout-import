package com.fuhu.pipeline.internal;

import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;

import java.util.concurrent.Callable;

public class DoPipeTaskThread implements Callable<Boolean> {
    private static final String TAG = DoPipeTaskThread.class.getSimpleName();
    private APipeTask pipeTask;
    private IPipeItem pipeItem;
    private long timeout;

    public DoPipeTaskThread(final APipeTask pipeTask, final IPipeItem pipeItem, final long timeout) {
        this.pipeTask = pipeTask;
        this.pipeItem = pipeItem;
        this.timeout = timeout;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    public Boolean call() throws Exception {
        long startTime = System.currentTimeMillis();
        if (pipeTask != null && pipeItem != null) {
            PipeLog.d(TAG, "task name: " + pipeTask.getClass().getSimpleName());

            // The pipeline task processing function.
            pipeTask.process(pipeItem);

            // Wait for pipe task to finish.
            while (!pipeTask.isDone()) {
                PipeLog.d(TAG, "wait time: " + (System.currentTimeMillis() - startTime));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }

                // Check if timeout expired.
                if ((System.currentTimeMillis() - startTime) > timeout) {
                    pipeItem.setPipeStatus(PipeStatus.PROCESS_TIMEOUT);
                    pipeItem.setErrorMessage("Process timeout expired");
                    return false;
                }
            }

            PipeLog.d(TAG, "process time: " + (System.currentTimeMillis() - startTime));
            return true;
        }
        return false;
    }
}

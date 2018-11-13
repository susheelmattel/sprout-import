package com.fuhu.pipeline.internal;


import android.os.Handler;

import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.contract.IPipeTaskList;
import com.fuhu.pipeline.contract.IPipeline;
import com.fuhu.pipeline.util.ExecutorUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DoPipelineThread implements Runnable {
    private static final String TAG = DoPipelineThread.class.getSimpleName();
    private IPipeline pipeline;
    private Handler mHandler;
    private ExecutorService executor;

    public DoPipelineThread(Handler mHandler, IPipeline pipeline) {
        this.mHandler = mHandler;
        this.pipeline = pipeline;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public DoPipelineThread(Handler mHandler, IPipeline pipeline, ExecutorService executor) {
        this.mHandler = mHandler;
        this.pipeline = pipeline;
        this.executor = executor;
    }

    @Override
    public void run() {
        if (pipeline != null && pipeline.getTaskList() != null && pipeline.getPipeItem() != null) {
            IPipeTaskList pipeTaskList = pipeline.getTaskList();
            IPipeItem pipeItem = pipeline.getPipeItem();
            IPipeCallback pipeCallback = pipeline.getCallback();
            long timeout = pipeItem.getTimeout();
            PipeLog.d(TAG, "process timeout: " + timeout);

            // Get the task chain from PipeTaskList.
            List<APipeTask> taskChain = getTaskChain(pipeTaskList, pipeItem);

            if (taskChain != null && taskChain.size() > 0) {
                // Get first task from task list.
                APipeTask pipeTask = taskChain.get(0);
                timeout = timeout < 1000L ? 1000L : timeout;

                // Return to callback if task error occurs.
                while (pipeTask != null && !PipeStatus.isError(pipeItem)) {
                    Future<Boolean> future = executor.submit(
                            new DoPipeTaskThread(pipeTask, pipeItem, timeout));
                    try {
                        future.get(timeout, TimeUnit.MILLISECONDS);
                    } catch (TimeoutException ex) {
                        ex.printStackTrace();
                        pipeItem.setPipeStatus(PipeStatus.PROCESS_TIMEOUT);
                        pipeItem.setErrorMessage(ex.getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        pipeItem.setPipeStatus(PipeStatus.INTERRUPTED_EXCEPTION);
                        pipeItem.setErrorMessage(e.getMessage());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        pipeItem.setPipeStatus(PipeStatus.EXECUTION_EXCEPTION);
                        pipeItem.setErrorMessage(e.getMessage());
                    } finally {
                        // Attempts to cancel execution of this task.
                        future.cancel(true);
                    }

                    // Get next task.
                    pipeTask = pipeTask.getNextTask();
                }
            }

            // Return to main thread.
            if (mHandler != null) {
                mHandler.post(new PipeCallbackThread(pipeItem, pipeCallback));
            } else {
                PipeLog.d(TAG, "No handler");
            }

            ExecutorUtil.shutdownAndAwaitTermination(executor);
        } else {
            PipeLog.d(TAG, "No PipeItem or task list.");
        }
    }

    /**
     * Get the task chain of pipeTaskList.
     * @param pipeTaskList PipeTaskList
     * @param pipeItem input data
     * @return task chain
     */
    private List<APipeTask> getTaskChain(final IPipeTaskList pipeTaskList, final IPipeItem pipeItem) {
        List<APipeTask> taskList = null;

        if (pipeTaskList != null) {
            taskList = pipeTaskList.getTaskList(pipeItem);

            // Set the next task of pipeTask.
            if (taskList != null) {
                for (int i = 0; i < taskList.size() - 1; i++) {
                    APipeTask pipeTask = taskList.get(i);
                    pipeTask.setNextTask(taskList.get(i + 1));
                }
            } else {
                PipeLog.d(TAG, "No task list");
            }
        }
        return taskList;
    }
}


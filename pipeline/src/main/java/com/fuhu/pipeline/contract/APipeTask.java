package com.fuhu.pipeline.contract;

public abstract class APipeTask {
    private static final String TAG = APipeTask.class.getSimpleName();
    private APipeTask nextTask;

    /**
     * Default constructor.
     */
    public APipeTask() {

    }

    /**
     * Constructor with next task.
     * @param nextTask APipeTask
     */
    public APipeTask(APipeTask nextTask) {
        this.nextTask = nextTask;
    }

    /**
     * Set the next task.
     * @param nextTask APipeTask
     */
    public void setNextTask(APipeTask nextTask) {
        this.nextTask = nextTask;
    }

    /**
     * Get the next task.
     * @return nextTask APipeTask
     */
    public APipeTask getNextTask() {
        return nextTask;
    }

    /**
     * The pipeline task processing function. Implement our processing here.
     * @param pipeItem PipeItem
     */
    public abstract void process(IPipeItem pipeItem);

    /**
     * Returns true if this task completed. Completion may be due to normal termination,
     * an exception -- in all of these cases, this method will return true.
     * @return true if this task completed
     */
    public abstract boolean isDone();
}
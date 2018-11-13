package com.fuhu.pipeline.contract;

public interface IPipeline {
    /**
     * Get the input data from this Pipeline.
     * @return
     */
    public IPipeItem getPipeItem();

    /**
     * Set the input data tothis pipeline.
     * @return
     */
    public void setPipeItem(final IPipeItem item);

    /**
     * Get the taskList from this pipeline.
     * @return
     */
    public IPipeTaskList getTaskList();

    /**
     * Set the taskList to this pipeline.
     * @return
     */
    public void setTaskList(final IPipeTaskList TaskList);

    /**
     * Get the callback from this pipeline.
     * @return
     */
    public IPipeCallback getCallback();

    /**
     * Set the callback to this pipeline.
     * @param back
     */
    public void setCallback(IPipeCallback back);
}

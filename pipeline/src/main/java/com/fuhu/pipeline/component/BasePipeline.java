package com.fuhu.pipeline.component;

import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.contract.IPipeTaskList;
import com.fuhu.pipeline.contract.IPipeline;

public class BasePipeline implements IPipeline {
    private IPipeItem mPipeItem;
    private IPipeTaskList mPipeTaskList;
    private IPipeCallback mPipeCallback;

    /**
     * Constructor with PipeItem, PipeTaskList and PipeCallback.
     */
    public BasePipeline(final IPipeTaskList taskList, final IPipeItem pipeItem, final IPipeCallback callback) {
        this.mPipeItem = pipeItem;
        this.mPipeTaskList = taskList;
        this.mPipeCallback= callback;
    }

    /**
     * Get the input data from this Pipeline.
     * @return IPipeItem
     */
    public IPipeItem getPipeItem() {
        return mPipeItem;
    }

    /**
     * Set the input data to this Pipeline.
     * @param item  IPipeItem
     */
    public void setPipeItem(final IPipeItem item) {
        this.mPipeItem = item;
    }

    /**
     * Get the configuration of this Pipeline.
     * @return IPipeTaskList
     */
    public IPipeTaskList getTaskList() {
        return mPipeTaskList;
    }

    /**
     * Set the configuration to this Pipeline.
     * @param taskList  IPipeTaskList
     */
    public void setTaskList(final IPipeTaskList taskList) {
        this.mPipeTaskList = taskList;
    }

    /**
     * Get the callback from this pipeline.
     * @return IPipeCallback
     */
    public IPipeCallback getCallback() {
        return mPipeCallback;
    }

    /**
     * Set the callback to this pipeline.
     * @param callback IPipeCallback
     */
    public void setCallback(final IPipeCallback callback) {
        this.mPipeCallback= callback;
    }
}

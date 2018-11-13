package com.fuhu.pipeline.contract;

import java.util.List;

public interface IPipeTaskList {
    /**
     * Get the APipeTask list of this PipeConfiguration.
     * @param pipeItem IPipeItem
     * @return task list
     */
    public List<APipeTask> getTaskList(final IPipeItem pipeItem);
}

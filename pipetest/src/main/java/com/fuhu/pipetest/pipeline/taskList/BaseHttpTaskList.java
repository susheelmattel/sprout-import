package com.fuhu.pipetest.pipeline.taskList;

import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.contract.IPipeTaskList;
import com.fuhu.pipeline.task.BuildOkHttpClientTask;
import com.fuhu.pipeline.task.BuildOkHttpRequestBodyTask;
import com.fuhu.pipeline.task.BuildOkHttpRequestTask;
import com.fuhu.pipeline.task.ConvertHttpResponseJsonTask;
import com.fuhu.pipeline.task.ConvertObjectToJsonTask;
import com.fuhu.pipeline.task.OkHttpResponseToJsonTask;
import com.fuhu.pipeline.task.SendOkHttpRequestTask;
import com.fuhu.pipetest.pipeline.task.CheckSSErrorTask;

import java.util.LinkedList;
import java.util.List;

public class BaseHttpTaskList implements IPipeTaskList {
    /**
     * Get the APipeTask list of this PipeConfiguration.
     * @param pipeItem IPipeItem
     * @return task list
     */
    @Override
    public List<APipeTask> getTaskList(final IPipeItem pipeItem) {
        List<APipeTask> taskList = new LinkedList<>();
        taskList.add(new BuildOkHttpClientTask());
        taskList.add(new ConvertObjectToJsonTask());
        taskList.add(new BuildOkHttpRequestBodyTask());
        taskList.add(new BuildOkHttpRequestTask());
        taskList.add(new SendOkHttpRequestTask());
        taskList.add(new OkHttpResponseToJsonTask());
        taskList.add(new CheckSSErrorTask());
        taskList.add(new ConvertHttpResponseJsonTask());
        return taskList;
    }
}
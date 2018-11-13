package com.fuhu.pipeline.task;


import android.content.Context;

import com.fuhu.pipeline.component.BaseHttpItem;
import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeStatus;
import com.fuhu.pipeline.manager.HttpClientManager;

import okhttp3.OkHttpClient;

public class BuildOkHttpClientTask extends APipeTask {
    private static final String TAG = BuildOkHttpClientTask.class.getSimpleName();

    /**
     * The pipeline task processing function. Implement our processing here.
     * @param pipeItem PipeItem
     */
    @Override
    public void process(IPipeItem pipeItem) {
        if (pipeItem != null && pipeItem instanceof BaseHttpItem) {
            HttpItem okHttpItem = (HttpItem) pipeItem;
            Context context = okHttpItem.getContext();

            if (context != null) {
                // Create a single OkHttpClient instance with a cache
                // and reuse it for all of your HTTP calls.
                OkHttpClient okHttpClient = HttpClientManager.getInstance()
                        .createOkHttpClient(context);
                okHttpItem.setOkHttpClient(okHttpClient);
            } else {
                okHttpItem.setPipeStatus(PipeStatus.CONTEXT_NULL);
                okHttpItem.setErrorMessage("Context is null.");
            }
        }
    }

    /**
     * Returns true if this task completed. Completion may be due to normal termination,
     * an exception -- in all of these cases, this method will return true.
     * @return true if this task completed
     */
    public boolean isDone() {
        return true;
    }
}

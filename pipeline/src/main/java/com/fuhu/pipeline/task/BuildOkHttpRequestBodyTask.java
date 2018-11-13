package com.fuhu.pipeline.task;

import com.fuhu.pipeline.PipelineConfig;
import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeLog;

import org.json.JSONObject;

import okhttp3.RequestBody;

public class BuildOkHttpRequestBodyTask extends APipeTask {
    private static final String TAG = BuildOkHttpRequestBodyTask.class.getSimpleName();

    /**
     * The pipeline task processing function. Implement our processing here.
     * @param pipeItem PipeItem
     */
    @Override
    public void process(IPipeItem pipeItem) {
        if (pipeItem != null && pipeItem instanceof HttpItem) {
            HttpItem okHttpItem = (HttpItem) pipeItem;
            JSONObject jsonObject = okHttpItem.getRequestJson();

            if (jsonObject != null ) {
                // Create a new request body.
                RequestBody requestBody = RequestBody.create(PipelineConfig.JSON_TYPE,
                        jsonObject.toString());

                PipeLog.d(TAG, "RequestBody: " + jsonObject.toString());
                okHttpItem.setOkHttpRequestBody(requestBody);
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

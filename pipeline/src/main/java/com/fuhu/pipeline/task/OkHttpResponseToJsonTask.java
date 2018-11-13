package com.fuhu.pipeline.task;


import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.internal.PipeStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class OkHttpResponseToJsonTask extends APipeTask {
    private static final String TAG = OkHttpResponseToJsonTask.class.getSimpleName();

    /**
     * The pipeline task processing function. Implement our processing here.
     * @param pipeItem PipeItem
     */
    @Override
    public void process(IPipeItem pipeItem) {
        if (pipeItem != null && pipeItem instanceof HttpItem) {
            HttpItem okHttpItem = (HttpItem) pipeItem;
            Response response = okHttpItem.getOkHttpResponse();

            // Get the response from the API server.
            if (response != null && response.body() != null) {
                try {
                    String responseString = response.body().string();
                    PipeLog.d(TAG, "response: " + responseString);

                    if (responseString != null && responseString.length() > 0) {
                        try {
                            // Try to convert response to JSONObject.
                            okHttpItem.setResponseJson(new JSONObject(responseString));
                            okHttpItem.setPipeStatus(PipeStatus.SUCCESS);
                        } catch (JSONException je) {
                            // Try to convert response to JSONArray.
                            try {
                                okHttpItem.setResponseJsonArray(new JSONArray(responseString));
                                okHttpItem.setPipeStatus(PipeStatus.SUCCESS);
                            } catch (JSONException jae) {
                                jae.printStackTrace();
                                okHttpItem.setPipeStatus(PipeStatus.JSON_PARSE_FAILED);
                                okHttpItem.setErrorMessage(je.getMessage());
                            }
                        }
                    }
                } catch (IOException ie) {
                    ie.printStackTrace();
                    okHttpItem.setPipeStatus(PipeStatus.UNKNOWN_ERROR);
                    okHttpItem.setErrorMessage(ie.getMessage());
                } finally {
                    response.close();
                }
            } else {
                okHttpItem.setPipeStatus(PipeStatus.HTTP_RESPONSE_NULL);
                okHttpItem.setErrorMessage("Http response is null.");
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
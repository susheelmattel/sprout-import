/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.pipeline.task;

import android.util.Log;

import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeStatus;
import com.fuhu.pipeline.parser.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sproutling.object.SSErrorItem;
import com.sproutling.object.SSResponseItem;

import org.json.JSONObject;

public class CheckSSErrorTask extends APipeTask {
    private static final String TAG = CheckSSErrorTask.class.getSimpleName();

    /**
     * The pipeline task processing function. Implement our processing here.
     *
     * @param pipeItem PipeItem
     */
    @Override
    public void process(IPipeItem pipeItem) {
        if (pipeItem != null && pipeItem instanceof HttpItem) {
            HttpItem okHttpItem = (HttpItem) pipeItem;
            JSONObject responseJson = okHttpItem.getResponseJson();

            // No error of json array.
            if (okHttpItem.getResponseJsonArray() != null) {
                return;
            }

            // Get the response from the API server.
            if (responseJson != null) {
                try {
                    if (responseJson.has("_embedded")) {
                        // Parse JSONObject using JsonParser.
                        SSResponseItem responseItem = JsonParser.fromJSON(responseJson, SSResponseItem.class);

                        // Checks if response has error.
                        if (responseItem.isError()) {
                            SSErrorItem errorItem = responseItem.getErrorItem();
                            Log.d(TAG, "ref: " + errorItem.getLogRef() + " message: " + errorItem.getMessage());

                            // Update status and set message to pipeItem.
                            okHttpItem.setPipeStatus(errorItem.getLogRef());
                            okHttpItem.setErrorMessage(errorItem.getMessage());
                        } else {
                            Log.d(TAG, "no error");
                        }
                    }
                } catch (JsonSyntaxException jse) {
                    jse.printStackTrace();
                    okHttpItem.setPipeStatus(PipeStatus.JSON_PARSE_FAILED);
                }
            } else {
                okHttpItem.setPipeStatus(PipeStatus.HTTP_RESPONSE_NULL);
            }
        }
    }

    /**
     * Returns true if this task completed. Completion may be due to normal termination,
     * an exception -- in all of these cases, this method will return true.
     *
     * @return true if this task completed
     */
    public boolean isDone() {
        return true;
    }
}
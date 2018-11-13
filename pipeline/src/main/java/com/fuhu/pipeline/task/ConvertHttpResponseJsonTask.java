package com.fuhu.pipeline.task;

import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.parser.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConvertHttpResponseJsonTask extends APipeTask {
    private static final String TAG = ConvertHttpResponseJsonTask.class.getSimpleName();

    /**
     * The pipeline task processing function. Implement our processing here.
     *
     * @param pipeItem PipeItem
     */
    @Override
    public void process(IPipeItem pipeItem) {
        if (pipeItem != null) {
            HttpItem okHttpItem = (HttpItem) pipeItem;
            JSONObject responseJson = okHttpItem.getResponseJson();
            JSONArray responseJsonArray = okHttpItem.getResponseJsonArray();
            Class dataModel = okHttpItem.getDataModel();

            if (okHttpItem.isSuccessful() && dataModel != null) {
                PipeLog.d(TAG, "Data model : " + dataModel.getSimpleName());
                try {
                    if (responseJson != null) {
                        // Parse json object using JsonParser.
                        Object responseItem = JsonParser.fromJSON(responseJson, dataModel);
                        okHttpItem.setResponseObject(responseItem);
                    } else if (responseJsonArray != null) {
                        // Parse json object using JsonParser.
                        Object [] responseItems = JsonParser.fromJSON(responseJsonArray, dataModel);
                        okHttpItem.setResponseArray(responseItems);
                    }
                } catch (JsonSyntaxException jse) {
                    jse.printStackTrace();
                    PipeLog.d(TAG, "Converting json to java object failed");
                }
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
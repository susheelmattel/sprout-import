package com.fuhu.pipeline.task;

import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.parser.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

public class ConvertObjectToJsonTask extends APipeTask {
    private static final String TAG = ConvertObjectToJsonTask.class.getSimpleName();

    /**
     * The pipeline task processing function. Implement our processing here.
     *
     * @param pipeItem PipeItem
     */
    @Override
    public void process(IPipeItem pipeItem) {
        if (pipeItem != null) {
            Object requestObject = pipeItem.getRequestObject();
            JSONObject requestJson = pipeItem.getRequestJson();

            // Checks if request json is null.
            if (requestJson == null && requestObject != null) {
                try {
                    // Convert java object to json object using JsonParser.
                    requestJson = JsonParser.toJSON(requestObject);

                    // Update pipeItem.
                    pipeItem.setRequestJson(requestJson);
                } catch (JSONException je) {
                    je.printStackTrace();
                    PipeLog.d(TAG, "Converting java json to object failed");
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
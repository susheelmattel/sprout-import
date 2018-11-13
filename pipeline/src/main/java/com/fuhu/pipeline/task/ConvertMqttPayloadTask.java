package com.fuhu.pipeline.task;

import com.fuhu.pipeline.PipelineConfig;
import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.internal.PipeStatus;
import com.fuhu.pipeline.mqtt.MqttItem;
import com.fuhu.pipeline.parser.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ConvertMqttPayloadTask extends APipeTask {
    private static final String TAG = ConvertMqttPayloadTask.class.getSimpleName();

    /**
     * The pipeline task processing function. Implement our processing here.
     *
     * @param pipeItem PipeItem
     */
    @Override
    public void process(IPipeItem pipeItem) {
        if (pipeItem != null && pipeItem instanceof MqttItem) {
            MqttItem mqttItem = (MqttItem) pipeItem;
            JSONObject payloadJson = mqttItem.getRequestJson();
            Object payloadObject = mqttItem.getRequestObject();
            String payloadString = mqttItem.getPayloadString();
            byte [] payload = mqttItem.getPayload();

            // Checks that the payload has been set.
            if (payload != null) {
                PipeLog.d(TAG, "The payload has been set.");
                return;
            }

            try {
                // Checks that the payload string has been set.
                if (payloadString != null) {
                    PipeLog.d(TAG, "payload string: " + payloadString);
                    mqttItem.setPayload(payloadString.getBytes(PipelineConfig.CHARSET_NAME));
                    return;
                }

                // Checks that the payload json has been set.
                if (payloadJson != null) {
                    PipeLog.d(TAG, "payload json: " + payloadJson.toString());
                    mqttItem.setPayload(payloadJson.toString().getBytes(PipelineConfig.CHARSET_NAME));
                    return;
                }

                // Checks that the payload object has been set.
                if (payloadObject != null) {
                    // Convert java object to json object.
                    payloadJson = JsonParser.toJSON(payloadObject);

                    if (payloadJson != null) {
                        PipeLog.d(TAG, "payload object: " + payloadJson.toString());
                        mqttItem.setPayload(payloadJson.toString().getBytes(PipelineConfig.CHARSET_NAME));
                    } else {
                        mqttItem.setPipeStatus(PipeStatus.CONVERT_JSON_FAILED);
                        mqttItem.setErrorMessage("Convert json failed.");
                    }
                }
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
                mqttItem.setPipeStatus(PipeStatus.UNSUPPORTED_UTF8);
                mqttItem.setErrorMessage(uee.getMessage());
            } catch (JSONException je) {
                je.printStackTrace();
                mqttItem.setPipeStatus(PipeStatus.CONVERT_JSON_FAILED);
                mqttItem.setErrorMessage(je.getMessage());
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
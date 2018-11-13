package com.fuhu.pipeline.contract;


import android.content.Context;

import com.fuhu.pipeline.PipelineConfig;
import com.fuhu.pipeline.internal.PipeStatus;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class APipeItem implements IPipeItem {
    // General
    private int pipeStatus;
    private Context context;
    private String errorMessage;
    private long timeout;
    private JSONObject requestJson;
    private Object requestObject;

    /**
     * Default Constructor
     */
    public APipeItem() {
        this.pipeStatus = PipeStatus.DEFAULT;
        this.timeout = PipelineConfig.TIMEOUT_PROCESS;
    }

    /**
     * Constructor with parameters.
     * @param context Context
     */
    public APipeItem(final Context context) {
        this.context = context;
        this.pipeStatus = PipeStatus.DEFAULT;
        this.timeout = PipelineConfig.TIMEOUT_PROCESS;
    }

    /**
     * Constructor with parameters.
     * @param context Context
     */
    public APipeItem(final Context context, final long timeout) {
        this.context = context;
        this.timeout = timeout;
        this.pipeStatus = PipeStatus.DEFAULT;
    }

    /**
     * Constructor with parameters.
     * @param context Context
     */
    public APipeItem(final Context context, final JSONObject requestJson, final Object requestObject, final long timeout) {
        this.context = context;
        this.timeout = timeout;
        this.requestJson = requestJson;
        this.requestObject = requestObject;
        this.pipeStatus = PipeStatus.DEFAULT;
    }

    /**
     * Get the status of this PipeItem.
     * @return pipeStatus
     */
    public int getPipeStatus() {
        return pipeStatus;
    }

    /**
     * Set the status of this PipeItem.
     * @param pipeStatus
     */
    public void setPipeStatus(final int pipeStatus) {
        this.pipeStatus = pipeStatus;
    }

    /**
     * Get the error message of this pipeItem.
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set the error message of this pipeItem.
     * @param errorMessage error message
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Get the context of the single, global Application object of the current process.
     */
    public Context getContext() {
        return context;
    }

    /**
     * Set the context of the current process.
     * @param context
     */
    public void setContext(final Context context) {
        this.context = context;
    }

    /**
     * Get the timeout of this pipeItem.
     * @return timeout
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Set the timeout of this pipeItem.
     * @param timeout process timeout
     */
    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }

    /**
     * Serializes the specified object into its equivalent representation as a tree of
     * {@link JsonElement}s.
     */
    public JsonElement toJsonTree(final Gson gson) {
        return gson.toJsonTree(this);
    }

    /**
     * Creates a new {@code JSONObject} with name/value mappings from the JSON
     * string.
     * @throws JSONException
     */
    public JSONObject toJSONObject(final Gson gson) throws JSONException {
        return new JSONObject(gson.toJson(this));
    }

    /**
     * Get the JSON object of this pipeItem.
     */
    public JSONObject getRequestJson() {
        return requestJson;
    }

    /**
     * Set the JSON object of this pipeItem.
     */
    public void setRequestJson(final JSONObject requestJson) {
        this.requestJson = requestJson;
    }

    /**
     * Get the java object of this pipeItem.
     */
    public Object getRequestObject() {
        return requestObject;
    }

    /**
     * Set the java object of this pipeItem.
     */
    public void setRequestObject(final Object requestObject) {
        this.requestObject = requestObject;
    }
}

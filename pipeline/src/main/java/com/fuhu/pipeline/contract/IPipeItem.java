package com.fuhu.pipeline.contract;

import org.json.JSONObject;

public interface IPipeItem {
    /**
     * Get the status of this PipeItem.
     * @return pipeStatus
     */
    public int getPipeStatus();

    /**
     * Set the status of this PipeItem.
     * @param pipeStatus
     */
    public void setPipeStatus(final int pipeStatus);

    /**
     * Get the error message of this pipeItem.
     * @return error message
     */
    public String getErrorMessage();

    /**
     * Set the error message of this pipeiTEM.
     * @param errorMessage error message
     */
    public void setErrorMessage(final String errorMessage);

    /**
     * Get the timeout of this pipeItem.
     * @return timeout
     */
    public long getTimeout();

    /**
     * Set the timeout of this pipeItem.
     * @param timeout process timeout
     */
    public void setTimeout(final long timeout);

    /**
     * Get the JSON object of this pipeItem.
     */
    public JSONObject getRequestJson();

    /**
     * Set the JSON object of this pipeItem.
     */
    public void setRequestJson(final JSONObject requestJson);

    /**
     * Get the java object of this pipeItem.
     */
    public Object getRequestObject();

    /**
     * Set the java object of this pipeItem.
     */
    public void setRequestObject(final Object requestObject);
}

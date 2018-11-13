package com.fuhu.pipeline.component;


import com.fuhu.pipeline.contract.APipeItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import javax.net.ssl.SSLContext;

public class BaseHttpItem extends APipeItem {
    // Http
    private String url;
    private Map<String, String> headers;
    private int method;
    private Class dataModel;

    // Https
    private SSLContext sslContext;

    // response
    private int responseCode;
    private JSONObject responseJson;
    private JSONArray responseJsonArray;
    private Object responseObject;
    private Object [] responseArray;

    /**
     * Default Constructor
     */
    public BaseHttpItem() {
        super();
    }

    /**
     * Constructor with builder.
     * @param builder
     */
    public BaseHttpItem(final HttpItem.Builder builder) {
        super(builder.context, builder.requestJson, builder.requestObject, builder.timeout);
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.dataModel = builder.dataModel;
        this.sslContext = builder.sslContext;
    }

    /**
     * Get the url of this PipeItem.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the url of this PipeItem.
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Get the header fields of a single HTTP message.
     */
    public Map<String, String> getHttpHeaders() {
        return headers;
    }

    /**
     * Set the header fields of a single HTTP message.
     */
    public void setHttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Get the HTTP method of a single HTTP message.
     */
    public int getMethod() {
        return method;
    }

    /**
     * Set the HTTP method of a single HTTP message.
     */
    public void setMethod(final int method) {
        this.method = method;
    }

    /**
     * Get the data model of this pipeItem.
     */
    public Class<? extends APipeItem> getDataModel() {
        return dataModel;
    }

    /**
     * Set the data model of this pipeItem.
     */
    public void setDataModel(final Class<? extends APipeItem> dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * Get the SSLContext of a single HTTPS message.
     */
    public SSLContext getSSLContext() {
        return sslContext;
    }

    /**
     * Set the SSLContext of a single HTTPS message.
     */
    public void setSSLContext(final SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    /**
     * Get the HTTP status code of a single HTTP message.
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Set the HTTP status code of a single HTTP message.
     */
    public void setResponseCode(final int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * Get the json object from response.
     */
    public JSONObject getResponseJson() {
        return responseJson;
    }

    /**
     * Set the json object to this pipeItem from response.
     */
    public void setResponseJson(final JSONObject responseJson) {
        this.responseJson = responseJson;
    }

    /**
     * Get the json array from response.
     */
    public JSONArray getResponseJsonArray() {
        return responseJsonArray;
    }

    /**
     * Set the json array to this pipeItem from response.
     */
    public void setResponseJsonArray(final JSONArray responseJsonArray) {
        this.responseJsonArray = responseJsonArray;
    }

    /**
     * Get the java object from response.
     */
    public Object getResponseObject() {
        return responseObject;
    }

    /**
     * Set the java object to this pipeItem from response.
     */
    public void setResponseObject(final Object responseObject) {
        this.responseObject = responseObject;
    }

    /**
     * Get the java Array from response.
     */
    public Object [] getResponseArray() {
        return responseArray;
    }

    /**
     * Set the java Array to this pipeItem from response.
     */
    public void setResponseArray(final Object [] responseArray) {
        this.responseArray = responseArray;
    }

    /**
     * Returns true if the code is in [200..300), which means the request was successfully received,
     * understood, and accepted.
     */
    public boolean isSuccessful() {
        return responseCode >= 200 && responseCode < 300;
    }
}

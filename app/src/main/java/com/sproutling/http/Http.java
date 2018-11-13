/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.http;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Http builder on top of HttpUrlConnection
 * <p/>
 * Created by bradylin on 11/18/16.
 */

public class Http {
    public static final String TAG = "Http";

    public static final String ACCEPT = "Accept";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String AUTHORIZATION = "Authorization";

    private static final int DEFAULT_TIMEOUT = 30000;

    public static boolean sLogRequest;
    public static boolean sLogResponse;

    private String mUrl;
    private HashMap<String, String> mHeaders;
    private HashMap<String, String> mParameters;
    private boolean mKeepParametersQuery;
    private int mTimeout = DEFAULT_TIMEOUT;

    private Http(String url) {
        mUrl = url;
        mHeaders = new HashMap<>();
        mParameters = new HashMap<>();
    }

    /**
     * Configure http module
     *
     * @param logRequest  Print details of requests
     * @param logResponse Print details of responses
     */
    public static void configure(boolean logRequest, boolean logResponse) {
        sLogRequest = logRequest;
        sLogResponse = logResponse;
    }

    /**
     * Create http based on url
     *
     * @param url Url
     * @return Http
     */
    public static Http url(String url) {
        return new Http(url);
    }

    /**
     * Create http based on url
     *
     * @param url Url
     * @return Http
     */
    public static Http url(URL url) {
        return new Http(url.toString());
    }

    /**
     * Add http header value
     *
     * @param name  Name
     * @param value Value
     * @return Http
     */
    public Http header(String name, String value) {
        mHeaders.put(name, value);
        return this;
    }

    /**
     * Add http header value
     *
     * @param name  Name
     * @param value Value
     * @return Http
     */
    public Http header(String name, double value) {
        return header(name, String.valueOf(value));
    }

    /**
     * Add http header value
     *
     * @param name  Name
     * @param value Value
     * @return Http
     */
    public Http header(String name, float value) {
        return header(name, String.valueOf(value));
    }

    /**
     * Add http header value
     *
     * @param name  Name
     * @param value Value
     * @return Http
     */
    public Http header(String name, int value) {
        return header(name, String.valueOf(value));
    }

    /**
     * Add http header value
     *
     * @param name  Name
     * @param value Value
     * @return Http
     */
    public Http header(String name, long value) {
        return header(name, String.valueOf(value));
    }

    /**
     * Add http header value
     *
     * @param name  Name
     * @param value Value
     * @return Http
     */
    public Http header(String name, boolean value) {
        return header(name, String.valueOf(value));
    }

    /**
     * Set Accept header value
     *
     * @param value Value
     * @return Http
     */
    public Http accept(String value) {
        return header(ACCEPT, value);
    }

    /**
     * Set Content-Type header value
     *
     * @param value Value
     * @return Http
     */
    public Http contentType(String value) {
        return header(CONTENT_TYPE, value);
    }

    /**
     * Set Authorization header value
     *
     * @param value Value
     * @return Http
     */
    public Http authorization(String value) {
        return header(AUTHORIZATION, value);
    }

    /**
     * Add parameter to url query (GET, DELETE...) or request body (POST, PUT...)
     *
     * @param name      Name
     * @param delimiter Delimiter
     * @param values    Values
     * @return Http
     */
    public Http parameter(String name, CharSequence delimiter, Object[] values) {
        return parameter(name, TextUtils.join(delimiter, values));
    }

    /**
     * Add parameter to url query (GET, DELETE...) or request body (POST, PUT...)
     *
     * @param name      Name
     * @param delimiter Delimiter
     * @param values    Values
     * @return Http
     */
    public Http parameter(String name, CharSequence delimiter, Iterable values) {
        return parameter(name, TextUtils.join(delimiter, values));
    }

    /**
     * Add parameter to url query (GET, DELETE...) or request body (POST, PUT...)
     *
     * @param name  Name
     * @param value Value
     * @return Http
     */
    public Http parameter(String name, String value) {
        mParameters.put(name, value);
        return this;
    }

    /**
     * Add parameter to url query (GET, DELETE...) or request body (POST, PUT...)
     *
     * @param name  Name
     * @param value Value
     * @return Http
     */
    public Http parameter(String name, double value) {
        return parameter(name, String.valueOf(value));
    }

    /**
     * Add parameter to url query (GET, DELETE...) or request body (POST, PUT...)
     *
     * @param name  Name
     * @param value Value
     * @return Http
     */
    public Http parameter(String name, float value) {
        return parameter(name, String.valueOf(value));
    }

    /**
     * Add parameter to url query (GET, DELETE...) or request body (POST, PUT...)
     *
     * @param name  Name
     * @param value Value
     * @return Http
     */
    public Http parameter(String name, int value) {
        return parameter(name, String.valueOf(value));
    }

    /**
     * Add parameter to url query (GET, DELETE...) or request body (POST, PUT...)
     *
     * @param name  Name
     * @param value Value
     * @return Http
     */
    public Http parameter(String name, long value) {
        return parameter(name, String.valueOf(value));
    }

    /**
     * Add parameter to url query (GET, DELETE...) or request body (POST, PUT...)
     *
     * @param name  Name
     * @param value Value
     * @return Http
     */
    public Http parameter(String name, boolean value) {
        return parameter(name, String.valueOf(value));
    }

    public Http keepParametersQuery() {
        mKeepParametersQuery = true;
        return this;
    }

    /**
     * Set a connection timeout to the request
     *
     * @param timeout  Timeout
     * @return Http
     */
    public Http timeout(int timeout) {

        return this;
    }

    /**
     * POST request
     *
     * @param body Request body
     * @return Http response
     * @throws IOException
     */
    public HttpContent post(JSONObject body) throws IOException {
        return post(body.toString());
    }

    /**
     * POST request. Body is url encoded parameters
     *
     * @return Http response
     * @throws IOException
     */
    public HttpContent post() throws IOException {
        if (mKeepParametersQuery) {
            return post(new byte[0]);
        }
        return post(createEncodedParameters(mParameters));
    }

    /**
     * POST request
     *
     * @param body Request body
     * @return Http response
     * @throws IOException
     */
    public HttpContent post(String body) throws IOException {
        try {
            return post(body.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Invalid environment!", e);
        }
    }

    /**
     * PUT request
     *
     * @param body Request body
     * @return Http response
     * @throws IOException
     */
    public HttpContent put(JSONObject body) throws IOException {
        return put(body.toString());
    }

    /**
     * PUT request. Body is url encoded parameters
     *
     * @return Http response
     * @throws IOException
     */
    public HttpContent put() throws IOException {
        return put(createEncodedParameters(mParameters));
    }

    /**
     * PUT request
     *
     * @param body Request body
     * @return Http response
     * @throws IOException
     */
    public HttpContent put(String body) throws IOException {
        try {
            return put(body.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Invalid environment!", e);
        }
    }

    /**
     * POST request
     *
     * @param body Request body
     * @return Http response
     * @throws IOException
     */
    public HttpContent post(byte[] body) throws IOException {
        String url = mUrl;
        if (mKeepParametersQuery) {
            url += "?" + createEncodedParameters(mParameters);
        }
        if (sLogRequest) {
            logRequest("POST", url, mHeaders, body);
        }
        HttpContent httpContent = new HttpContent(new URL(url), mHeaders);
        httpContent.setTimeout(mTimeout == DEFAULT_TIMEOUT ? DEFAULT_TIMEOUT : mTimeout);
        return httpContent.request("POST", body);
    }

    /**
     * PUT request
     *
     * @param body Request body
     * @return Http response
     * @throws IOException
     */
    public HttpContent put(byte[] body) throws IOException {
        String url = mUrl;
        if (mKeepParametersQuery) {
            url += "?" + createEncodedParameters(mParameters);
        }
        if (sLogRequest) {
            logRequest("PUT", url, mHeaders, body);
        }
        HttpContent httpContent = new HttpContent(new URL(url), mHeaders);
        httpContent.setTimeout(mTimeout == DEFAULT_TIMEOUT ? DEFAULT_TIMEOUT : mTimeout);
        return httpContent.request("PUT", body);
    }

    /**
     * GET request. Url is appended by url encoded parameters
     *
     * @return Http response
     * @throws IOException
     */
    public HttpContent get() throws IOException {
        String url = mUrl + "?" + createEncodedParameters(mParameters);
        if (sLogRequest) {
            logRequest("GET", url, mHeaders, null);
        }
        HttpContent httpContent = new HttpContent(new URL(url), mHeaders);
        httpContent.setTimeout(mTimeout == DEFAULT_TIMEOUT ? DEFAULT_TIMEOUT : mTimeout);
        return httpContent.request("GET");
    }

    /**
     * HEAD request. Url is appended by url encoded parameters
     *
     * @return Http response
     * @throws IOException
     */
    public HttpContent head() throws IOException {
        String url = mUrl + "?" + createEncodedParameters(mParameters);
        if (sLogRequest) {
            logRequest("HEAD", url, mHeaders, null);
        }
        HttpContent httpContent = new HttpContent(new URL(url), mHeaders);
        httpContent.setTimeout(mTimeout == DEFAULT_TIMEOUT ? DEFAULT_TIMEOUT : mTimeout);
        return httpContent.request("HEAD");
    }

    /**
     * DELETE request. Url is appended by url encoded parameters
     *
     * @return Http response
     * @throws IOException
     */
    public HttpContent delete() throws IOException {
        String url = mUrl + "?" + createEncodedParameters(mParameters);
        if (sLogRequest) {
            logRequest("DELETE", url, mHeaders, null);
        }
        HttpContent httpContent = new HttpContent(new URL(url), mHeaders);
        httpContent.setTimeout(mTimeout == DEFAULT_TIMEOUT ? DEFAULT_TIMEOUT : mTimeout);
        return httpContent.request("DELETE");
    }

    static void logRequest(String method, String url, HashMap<String, String> headers, byte[] body) {
        Log.v(TAG, debug("--> Request", method, url, headers, body));
    }

    static void logResponse(String method, String url, HashMap<String, String> headers, byte[] body) {
        Log.v(TAG, debug("<-- Response", method, url, headers, body));
    }

    static String debug(String request, String method, String url, HashMap<String, String> headers, byte[] body) {
        StringBuilder debug = new StringBuilder(request).append(": \n");
        debug.append(method).append(' ').append(url).append('\n');
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            debug.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }
        if (body != null && body.length > 0) {
            debug.append('\n').append(new String(body));
        }
        return debug.toString();
    }

    static String createEncodedParameters(HashMap<String, String> parameters) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null) {
                try {
                    builder.append(key)
                            .append('=')
                            .append(URLEncoder.encode(value, "UTF-8"))
                            .append('&');
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("Invalid environment!", e);
                }
            }
        }
        return builder.toString();
    }
}

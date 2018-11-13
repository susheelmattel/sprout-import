package com.sproutling.apitest.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Http content is a response from Http request
 * Created by bradylin on 11/18/16.
 */

public class HttpContent {

    int mResponseCode;
    byte[] mResponse;
    HashMap<String, String> mRequestHeaders;
    HashMap<String, String> mResponseHeaders;
    URL mUrl;
    int mTimeout;

    HttpContent(URL url, HashMap<String, String> headers) {
        mUrl = url;
        mRequestHeaders = headers;
        mResponseHeaders = new HashMap<>();
    }

    /**
     * Set connection timout
     *
     * @param timeout Timeout
     */
    public void setTimeout(int timeout) {
        mTimeout = timeout;
    }

    /**
     * Get http response header
     *
     * @param name Name
     * @return Header value
     */
    public String header(String name) {
        return mResponseHeaders.get(name);
    }

    /**
     * Get http response code
     *
     * @return Http response code
     */
    public int responseCode() {
        return mResponseCode;
    }

    /**
     * Get http response as byte array
     *
     * @return Response byte array
     */
    public byte[] bytes() {
        return mResponse;
    }

    /**
     * Get http response as string
     *
     * @return Response string
     */
    public String string() {
        return new String(bytes());
    }

    /**
     * Get http response as json object
     *
     * @return Response json object
     * @throws JSONException
     */
    public JSONObject json() throws JSONException {
        return new JSONObject(string());
    }

    /**
     * Get http response as json array
     *
     * @return Response json array
     * @throws JSONException
     */
    public JSONArray jsonArray() throws JSONException {
        return new JSONArray(string());
    }

    HttpContent request(String method) throws IOException {
        return request(method, null);
    }

    HttpContent request(String method, byte[] body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
        try {
            connection.setRequestMethod(method);
            setHeaderFields(connection, mRequestHeaders);
            if (mTimeout > 0) connection.setConnectTimeout(mTimeout);
            if (body != null && body.length > 0) write(connection, body);
            mResponseCode = connection.getResponseCode();
            mResponse = read(connection);
            mResponseHeaders = getHeaderFields(connection);
            if (Http.sLogResponse) {
                Http.logResponse(method, mUrl.toString(), mResponseHeaders, mResponse);
            }
            return this;
        } finally {
            connection.disconnect();
        }
    }

    static byte[] read(HttpURLConnection connection) throws IOException {
        return Streams.bytes(input(connection));
    }

    static InputStream input(HttpURLConnection connection) throws IOException {
        InputStream input = connection.getErrorStream();
        if (input != null) return input;
        return connection.getInputStream();
    }

    static void write(HttpURLConnection connection, byte[] body) throws IOException {
        OutputStream output = connection.getOutputStream();
        output.write(body);
        output.flush();
    }

    static HashMap<String, String> getHeaderFields(HttpURLConnection connection) {
        HashMap<String, String> result = new HashMap<>();
        Map<String, List<String>> headers = connection.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            List<String> value = entry.getValue();
            if (value != null && !value.isEmpty()) {
                result.put(entry.getKey(), value.get(0));
            }
        }
        return result;
    }

    static void setHeaderFields(HttpURLConnection connection, HashMap<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.addRequestProperty(entry.getKey(), entry.getValue());
        }
    }
}

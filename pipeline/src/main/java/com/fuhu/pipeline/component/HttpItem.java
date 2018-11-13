package com.fuhu.pipeline.component;

import android.content.Context;

import com.fuhu.pipeline.PipelineConfig;
import com.fuhu.pipeline.contract.HttpMethod;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpItem extends BaseHttpItem {
    private OkHttpClient okHttpClient;
    private Request.Builder okHttpRequestBuilder;
    private RequestBody okHttpRequestBody;
    private Request okHttpRequest;
    private Response okHttpResponse;

    public HttpItem() {
        super();
    }

    public HttpItem(Builder builder) {
        super(builder);
    }

    /**
     * Get an instance of OKHttpClient.
     */
    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    /**
     * Set an instance of OKHttpClient.
     */
    public void setOkHttpClient(final OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    /**
     * Get an Http request builder for OkHttp3.
     */
    public Request.Builder getOkHttpRequestBuilder() {
        return okHttpRequestBuilder;
    }

    /**
     * Set an Http request builder for OkHttp3.
     */
    public void setOkHttpRequestBuilder(final Request.Builder okHttpRequestBuilder) {
        this.okHttpRequestBuilder = okHttpRequestBuilder;
    }

    /**
     * Get a request body for OkHttp3.
     */
    public RequestBody getOkHttpRequestBody() {
        return okHttpRequestBody;
    }

    /**
     * Set a request body for OkHttp3.
     */
    public void setOkHttpRequestBody(final RequestBody requestBody) {
        this.okHttpRequestBody = requestBody;
    }

    /**
     * Get an Http request for OkHttp3.
     */
    public Request getOkHttpRequest() {
        return okHttpRequest;
    }

    /**
     * Set an Http request for OkHttp3.
     */
    public void setOkHttpRequest(final Request request) {
        this.okHttpRequest = request;
    }


    /**
     * Get an HTTP response for OkHttp3.
     */
    public Response getOkHttpResponse() {
        return okHttpResponse;
    }

    /**
     * Set an HTTP response for OkHttp3.
     */
    public void setOkHttpResponse(final Response response) {
        this.okHttpResponse = response;
    }

    /**
     * Build BasePipeItem object.
     */
    public static class Builder {
        Context context;
        String url;
        int method;
        Map<String, String> headers;
        JSONObject requestJson;
        Object requestObject;
        Class dataModel;
        SSLContext sslContext;
        long timeout;

        public Builder(final Context context) {
            if (context == null) throw new IllegalArgumentException("context == null");
            this.context = context;
            this.timeout = PipelineConfig.TIMEOUT_PROCESS;

            // Default headers
            Map<String, String> defaultHeaders = new HashMap<>();
            defaultHeaders.put("Content-Type", "application/json");
            defaultHeaders.put("Accept", "application/json");
            this.headers = defaultHeaders;
        }

        public Builder timeout(final long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder url(final String url) {
            if (url == null) throw new IllegalArgumentException("url == null");
            this.url = url;
            return this;
        }

        public Builder addHeader(final String key, final String value) {
            if (key == null) throw new IllegalArgumentException("key == null");
            if (value == null) throw new IllegalArgumentException("value == null");
            headers.put(key, value);
            return this;
        }

        public Builder headers(final Map<String, String> headers) {
            if (headers == null) throw new IllegalArgumentException("headers == null");
            this.headers = headers;
            return this;
        }

        public HttpItem get() {
            validate();
            this.method = HttpMethod.GET;
            return new HttpItem(this);
        }

        public HttpItem post(final JSONObject requestJson) {
            validate();
            if (requestJson == null) throw new IllegalArgumentException("requestJson == null");
            this.method = HttpMethod.POST;
            this.requestJson = requestJson;
            return new HttpItem(this);
        }

        public HttpItem post(final Object requestObject) {
            validate();
            if (requestObject == null) throw new IllegalArgumentException("requestObject == null");
            this.method = HttpMethod.POST;
            this.requestObject = requestObject;
            return new HttpItem(this);
        }

        public HttpItem put(final JSONObject requestJson) {
            validate();
            if (requestJson == null) throw new IllegalArgumentException("requestJson == null");
            this.method = HttpMethod.PUT;
            this.requestJson = requestJson;
            return new HttpItem(this);
        }

        public HttpItem put(final Object requestObject) {
            validate();
            if (requestObject == null) throw new IllegalArgumentException("requestObject == null");
            this.method = HttpMethod.PUT;
            this.requestObject = requestObject;
            return new HttpItem(this);
        }

        public HttpItem delete() {
            validate();
            this.method = HttpMethod.DELETE;
            return new HttpItem(this);
        }

        public Builder sslContext(final SSLContext sslContext) {
            if (sslContext == null) throw new IllegalArgumentException("sslContext == null");
            this.sslContext = sslContext;
            return this;
        }

        public Builder dataModel(final Class dataModel) {
            if (dataModel == null) throw new IllegalArgumentException("dataModel == null");
            this.dataModel = dataModel;
            return this;
        }

        private void validate() {
            if (context == null) throw new IllegalArgumentException("context == null");
            if (url == null) throw new IllegalArgumentException("url == null");
        }
    }
}

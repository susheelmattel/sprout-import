package com.fuhu.pipeline.task;

import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.HttpMethod;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.internal.PipeStatus;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.Util;

public class BuildOkHttpRequestTask extends APipeTask {
    private static final String TAG = BuildOkHttpRequestTask.class.getSimpleName();
    private IPipeItem inputItem;

    /**
     * The pipeline task processing function. Implement our processing here.
     * @param pipeItem PipeItem
     */
    @Override
    public void process(IPipeItem pipeItem) {
        if (pipeItem != null && pipeItem instanceof HttpItem) {
            HttpItem okHttpItem = (HttpItem) pipeItem;
            int method = okHttpItem.getMethod();
            Request.Builder builder = null;

            switch (method) {
                case HttpMethod.GET:
                    builder = get(okHttpItem);
                    break;

                case HttpMethod.POST:
                    builder = post(okHttpItem);
                    break;

                case HttpMethod.PUT:
                    builder = put(okHttpItem);
                    break;

                case HttpMethod.DELETE:
                    builder = delete(okHttpItem);
                    break;
            }

            if (builder != null) {
                // Set the request's headers.
                Map<String, String> headers = okHttpItem.getHttpHeaders();
                if (headers != null) {
                    for (String name: headers.keySet()) {
                        PipeLog.d(TAG, "Header: " + name + " value: " + headers.get(name));
                        builder.addHeader(name, headers.get(name));
                    }
                }

                // Build an HTTP request.
                Request request = builder.build();
                okHttpItem.setOkHttpRequest(request);
            } else {
                okHttpItem.setPipeStatus(PipeStatus.HTTP_REQUEST_BUILDER_NULL);
                okHttpItem.setErrorMessage("Request builder is null.");
            }
        }
    }

    /**
     * Create a Request.Builder for Http GET.
     * @param okHttpItem OkHttpItem
     * @return Request.Builder
     */
    private Request.Builder get(final HttpItem okHttpItem) {
        String url = okHttpItem.getUrl();

        if (url != null) {
            // Create an HTTP request for OkHttp.
            Request.Builder builder = new Request.Builder().url(url).get();
            return builder;
        }
        return null;
    }

    /**
     * Create a Request.Builder for Http POST.
     * @param okHttpItem OkHttpItem
     * @return Request.Builder
     */
    private Request.Builder post(final HttpItem okHttpItem) {
        String url = okHttpItem.getUrl();

        if (url != null) {
            // Create an HTTP request for OkHttp.
            Request.Builder builder = new Request.Builder().url(url);
            RequestBody requestBody = okHttpItem.getOkHttpRequestBody();

            // Sets the request's body.
            if (requestBody != null) {
                builder.post(requestBody);
            } else {
                builder.post(Util.EMPTY_REQUEST);
            }
            return builder;
        }
        return null;
    }

    /**
     * Create a Request.Builder for Http PUT.
     * @param okHttpItem OkHttpItem
     * @return Request.Builder
     */
    private Request.Builder put(final HttpItem okHttpItem) {
        String url = okHttpItem.getUrl();

        if (url != null) {
            // Create an HTTP request for OkHttp.
            Request.Builder builder = new Request.Builder().url(url);
            RequestBody requestBody = okHttpItem.getOkHttpRequestBody();

            // Sets the request's body.
            if (requestBody != null) {
                builder.put(requestBody);
            } else {
                builder.put(Util.EMPTY_REQUEST);
            }
            return builder;
        }
        return null;
    }

    /**
     * Create a Request.Builder for Http DELETE.
     * @param okHttpItem OkHttpItem
     * @return Request.Builder
     */
    private Request.Builder delete(final HttpItem okHttpItem) {
        String url = okHttpItem.getUrl();

        if (url != null) {
            // Create an HTTP request for OkHttp.
            Request.Builder builder = new Request.Builder().url(url).delete();
            return builder;
        }
        return null;
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

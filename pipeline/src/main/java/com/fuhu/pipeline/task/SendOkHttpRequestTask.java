package com.fuhu.pipeline.task;

import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.internal.PipeStatus;

import java.io.IOException;
import java.net.UnknownHostException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SendOkHttpRequestTask extends APipeTask {
    private static final String TAG = SendOkHttpRequestTask.class.getSimpleName();

    /**
     * The pipeline task processing function. Implement our processing here.
     * @param pipeItem PipeItem
     */
    @Override
    public void process(IPipeItem pipeItem) {
        if (pipeItem != null && pipeItem instanceof HttpItem) {
            HttpItem okHttpItem = (HttpItem) pipeItem;
            OkHttpClient okHttpClient = okHttpItem.getOkHttpClient();
            Request request = okHttpItem.getOkHttpRequest();
            Response response = null;

            if (okHttpClient != null && request != null) {
                PipeLog.d(TAG, "[" + request.method() + "] " + request.url().toString());

                try {
                    // Invokes the request immediately, and blocks until
                    // the response can be processed or is in error.
                    response = okHttpClient.newCall(request).execute();

                    if (response != null) {
                        int responseCode = response.code();
                        // Set the HTTP status code.
                        okHttpItem.setResponseCode(responseCode);
                    }
                } catch (UnknownHostException uhe) {
                    uhe.printStackTrace();
                    okHttpItem.setPipeStatus(PipeStatus.UNKNOWN_HOST);
                    okHttpItem.setErrorMessage(uhe.getMessage());
                } catch (IOException ie) {
                    ie.printStackTrace();
                    okHttpItem.setPipeStatus(PipeStatus.UNKNOWN_ERROR);
                    okHttpItem.setErrorMessage(ie.getMessage());
                }
                okHttpItem.setOkHttpResponse(response);

                // Set Error status.
            } else if (okHttpClient == null) {
                okHttpItem.setPipeStatus(PipeStatus.HTTP_CLIENT_NULL);
                okHttpItem.setErrorMessage("Http client is null.");
            } else if (request == null) {
                okHttpItem.setPipeStatus(PipeStatus.HTTP_REQUEST_NULL);
                okHttpItem.setErrorMessage("Http request is null.");
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
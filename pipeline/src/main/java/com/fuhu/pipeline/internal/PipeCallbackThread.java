package com.fuhu.pipeline.internal;

import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.contract.IPipeItem;

public class PipeCallbackThread implements Runnable {
    private static final String TAG = PipeCallbackThread.class.getSimpleName();
    private IPipeCallback callback;
    private IPipeItem pipeItem;

    public PipeCallbackThread(final IPipeItem pipeItem, final IPipeCallback callback) {
        this.callback = callback;
        this.pipeItem = pipeItem;
    }

    @Override
    public void run() {
        if (callback != null) {
            if (pipeItem != null) {
                int status = pipeItem.getPipeStatus();

                // debug
                PipeLog.d(TAG, "status: " + status);

                // Checks if the operation was successful.
                if (PipeStatus.isSuccess(status)) {
                    if (pipeItem instanceof HttpItem) {
                        callback.onResult(convertHttpResponse((HttpItem)pipeItem));
                    } else {
                        callback.onResult(pipeItem);
                    }
                } else {
                    callback.onError(status, pipeItem);
                }
            } else {
                callback.onError(PipeStatus.PIPEITEM_NULL, pipeItem);
            }
        } else {
            PipeLog.w(TAG, "no callback");
        }
    }

    /**
     * Convert HTTP response json object to java object.
     * @param httpItem HttpItem
     * @return response object
     */
    private Object convertHttpResponse(final HttpItem httpItem) {
        if (httpItem != null) {
            Object responseObject = httpItem.getResponseObject();
            Object responseArray = httpItem.getResponseArray();

            if (responseObject != null) {
                return responseObject;
            }

            if (responseArray != null) {
                return responseArray;
            }
        }
        return httpItem;
    }
}

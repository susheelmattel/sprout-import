package com.fuhu.pipeline.contract;

public interface IPipeCallback {
    /**
     * When pipeline is finished.
     * @param responseObject response object
     */
    public void onResult(final Object responseObject);

    /**
     * This callback is invoked when there is a pipeline execution error.
     * @param status error
     * @param pipeItem errorItem
     */
    public void onError(final int status, final IPipeItem pipeItem);
}

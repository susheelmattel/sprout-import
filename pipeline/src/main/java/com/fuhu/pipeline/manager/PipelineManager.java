package com.fuhu.pipeline.manager;


import android.os.Handler;
import android.os.Looper;

import com.fuhu.pipeline.component.BasePipeline;
import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.contract.IPipeTaskList;
import com.fuhu.pipeline.contract.IPipeline;
import com.fuhu.pipeline.internal.DoPipelineThread;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.parser.JsonObjWrapper;
import com.fuhu.pipeline.parser.JsonParser;
import com.fuhu.pipeline.util.ExecutorUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PipelineManager {
    private static final String TAG = PipelineManager.class.getSimpleName();
    private static PipelineManager INSTANCE;
    private Handler mHandler;
    private ExecutorService pipelineExecutor;

    /**
     * Default constructor.
     */
    public PipelineManager() {
        // Use the application's main looper, which lives in the main thread of the application.
        this.mHandler = new Handler(Looper.getMainLooper());
        this.pipelineExecutor = Executors.newFixedThreadPool(2);
    }

    /**
     * Get an instance of PipelineManager.
     * @return PipelineManager
     */
    public static PipelineManager getInstance() {
        if (INSTANCE == null){
            synchronized(PipelineManager.class) {
                if(INSTANCE == null) {
                    INSTANCE = new PipelineManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Process the pipeline tasks with TaskList, PipeItem and PipelineCallback.
     * @param taskList task list
     * @param pipeItem input data
     * @param callback callback
     */
    public void doPipeline(IPipeTaskList taskList, IPipeItem pipeItem, IPipeCallback callback) {
        if (taskList != null && pipeItem != null) {
            process(new BasePipeline(taskList, pipeItem, callback));
        } else {
            PipeLog.w(TAG, "tastList or pipeItem is null.");
        }
    }

    /**
     * Process the pipeline tasks with IPipeline.
     * @param pipeline IPipeline
     */
    public void doPipeline(IPipeline pipeline) {
        if (pipeline != null && pipeline.getTaskList() != null && pipeline.getPipeItem() != null) {
            process(pipeline);
        } else {
            PipeLog.w(TAG, "pipeline or tastList or pipeItem is null.");
        }
    }

    /**
     * Process the pipeline tasks.
     * @param pipeline IPipeline
     */
    private void process(IPipeline pipeline) {
        if (pipeline != null) {
            pipelineExecutor.submit(new DoPipelineThread(mHandler, pipeline));
        } else {
            PipeLog.w(TAG, "pipeline is null");
        }
    }

    /**
     * Initiates an orderly shutdown in which previously submitted tasks are executed.
     */
    public void shutdown() {
        if (pipelineExecutor != null) {
            ExecutorUtil.shutdownAndAwaitTermination(pipelineExecutor);
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * Destroy object.
     */
    public static void release() {
        if (INSTANCE != null) {
            INSTANCE.shutdown();
            INSTANCE = null;
        }
        HttpClientManager.release();
        MqttManager.release();
        JsonObjWrapper.release();
        JsonParser.release();
    }
}

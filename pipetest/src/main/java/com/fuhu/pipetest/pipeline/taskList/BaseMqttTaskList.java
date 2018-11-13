package com.fuhu.pipetest.pipeline.taskList;

import android.util.Log;

import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.contract.IPipeTaskList;
import com.fuhu.pipeline.mqtt.MqttAction;
import com.fuhu.pipeline.mqtt.MqttItem;
import com.fuhu.pipeline.task.BuildMqttClientTask;
import com.fuhu.pipeline.task.ConnectMqttTask;
import com.fuhu.pipeline.task.ConvertMqttPayloadTask;
import com.fuhu.pipeline.task.DisconnectMqttTask;
import com.fuhu.pipeline.task.PublishMqttMessageTask;
import com.fuhu.pipeline.task.SubscribeMqttTopicTask;
import com.fuhu.pipeline.task.UnsubscribeMqttTopicTask;
import com.fuhu.pipeline.task.WaitForMqttActionTask;
import com.fuhu.pipetest.pipeline.task.BuildMqttOptionsTask;

import java.util.LinkedList;
import java.util.List;

public class BaseMqttTaskList implements IPipeTaskList {
    private static final String TAG = BaseMqttTaskList.class.getSimpleName();

    /**
     * Get the APipeTask list of this PipeConfiguration.
     * @return task list
     */
    @Override
    public List<APipeTask> getTaskList(final IPipeItem pipeItem) {
        List<APipeTask> taskList = new LinkedList<>();

        if (pipeItem != null && pipeItem instanceof MqttItem) {
            int method = ((MqttItem) pipeItem).getActionType();
            taskList.add(new BuildMqttClientTask());
            taskList.add(new BuildMqttOptionsTask());

            // Checks that MQTT client is necessary to connect an MQTT broker.
            if (method != MqttAction.CONNECT && method != MqttAction.DISCONNECT) {
                taskList.add(new ConnectMqttTask());
                taskList.add(new WaitForMqttActionTask());
            }

            APipeTask actionTask = null;
            switch (method) {
                case MqttAction.CONNECT:
                    actionTask = new ConnectMqttTask();
                    break;

                case MqttAction.SUBSCRIBE:
                    actionTask = new SubscribeMqttTopicTask();
                    break;

                case MqttAction.PUBLISH_MESSAGE:
                    taskList.add(new ConvertMqttPayloadTask());
                    actionTask = new PublishMqttMessageTask();
                    break;

                case MqttAction.UNSUBSCRIBE:
                    actionTask = new UnsubscribeMqttTopicTask();
                    break;

                case MqttAction.DISCONNECT:
                    taskList.add(new UnsubscribeMqttTopicTask());
                    taskList.add(new WaitForMqttActionTask());
                    actionTask = new DisconnectMqttTask();
                    break;
            }

            if (actionTask != null) {
                taskList.add(actionTask);
                taskList.add(new WaitForMqttActionTask());
            }
        }
        Log.d(TAG, "Mqtt task list size: " + taskList.size());
        return taskList;
    }
}
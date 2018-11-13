package com.fuhu.pipeline.internal;

import com.fuhu.pipeline.contract.IPipeItem;

public class PipeStatus {
    private int code;
    private String content = null;

    public static final int DEFAULT = 0;
    public static final int SUCCESS = 1;

    // General error
    public static final int UNKNOWN_EXCEPTION = -2;
    public static final int UNKNOWN_ERROR = -3;
    public static final int UNKNOWN_HOST = -4;
    public static final int UNSUPPORTED_UTF8 = -5;
    public static final int JSON_PARSE_FAILED = -6;
    public static final int CONVERT_JSON_FAILED = -7;
    public static final int INVALID_PARAMETER = -8;
    public static final int CONTEXT_NULL = -9;
    public static final int USERNAME_OR_PASSWORD_NULL = -10;


    // Pipeline error
    public static final int PIPEITEM_NULL = -100;
    public static final int URL_NULL = -101;
    public static final int DATA_MODEL_NULL = -102;
    public static final int PROCESS_TIMEOUT = -103;
    public static final int INTERRUPTED_EXCEPTION = -104;
    public static final int EXECUTION_EXCEPTION = -105;

    // HTTP error
    public static final int REQUEST_DATA_NULL = -200;
    public static final int HTTP_CLIENT_NULL = -201;
    public static final int HTTP_REQUEST_NULL = -202;
    public static final int HTTP_REQUEST_BUILDER_NULL = -203;
    public static final int HTTP_REQUEST_BODY_NULL = -204;
    public static final int HTTP_RESPONSE_NULL = -205;

    // MQTT status
    public static final int MQTT_CONNECTED = -301;
    public static final int MQTTITEM_NULL = -302;
    public static final int MQTT_CLIENT_NULL = -303;
    public static final int MQTT_LISTENER_NULL = -304;
    public static final int MQTT_TOPIC_NULL = -305;
    public static final int MQTT_MESSAGE_NULL = -306;
    public static final int MQTT_TOKEN_NULL = -307;
    public static final int COULD_NOT_CONNECT_MQTT = -308;
    public static final int MQTT_EXCEPTION = -309;


    // HTTP status 1xx - 5xx
    // The request contained an error.
    public static final int BAD_REQUEST = 400;
    // Access was denied. You may have entered your credentials incorrectly, or you might not have access to the requested resource or operation.
    public static final int UNAUTHORIZED = 401;
    // The request is for something forbidden. Authorization will not help.
    public static final int FORBIDDEN = 403;
    // The requested resource was not found
    public static final int NOT_FOUND = 404;
    // The user has sent too many requests in a given amount of time. The account is being rate limited.
    public static final int Too_MANY_REQUESTS = 429;
    // "Your request could not be completed because there was a problem with the service."
    public static final int INTERNAL_SERVER_ERROR = 500;
    // "There's a problem with the service right now. Please try again later.
    public static final int SERVICE_UNAVAILABLE = 503;


    public PipeStatus(final int code){
        this.code = code;
    }

    /**
     * Get the code of this status.
     * @return code
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns true if the operation was successful.
     * @param status PipeStatus
     * @return true or false
     */
    public static boolean isSuccess(final int status) {
        if (status == SUCCESS) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the error occurred.
     * @param pipeItem pipe item
     * @return true or false
     */
    public static boolean isError(final IPipeItem pipeItem) {
        if (pipeItem != null) {
            int status = pipeItem.getPipeStatus();
            return isError(status);
        }
        return true;
    }

    /**
     * Returns true if the error occureed.
     * @param status status
     * @return true or false
     */
    public static boolean isError(final int status) {
        if (status == DEFAULT || status == SUCCESS) {
            return false;
        }
        return true;
    }
}

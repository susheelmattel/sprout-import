package com.fuhu.pipetest.pipeline.object;

public class SSErrorItem {
    private String message;
    private int logref;
    private String reason;
    private String path;

    public String getMessage () {
        return message;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public int getLogref () {
        return logref;
    }

    public void setLogref (int logref) {
        this.logref = logref;
    }

    public String getReason () {
        return reason;
    }

    public void setReason (String reason) {
        this.reason = reason;
    }

    public String getPath () {
        return path;
    }

    public void setPath (String path) {
        this.path = path;
    }
}
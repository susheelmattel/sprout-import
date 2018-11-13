package com.fuhu.pipetest.pipeline.object;

public class EmbeddedItem {
    private SSErrorItem[] errors;

    public SSErrorItem[] getSSErrorItem () {
        return errors;
    }

    public void setSSErrorItem (SSErrorItem[] errors) {
        this.errors = errors;
    }
}

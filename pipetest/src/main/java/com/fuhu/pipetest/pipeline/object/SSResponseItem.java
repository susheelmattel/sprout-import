package com.fuhu.pipetest.pipeline.object;

public class SSResponseItem {
    private EmbeddedItem _embedded;

    public EmbeddedItem getEmbeddedItem ()
    {
        return _embedded;
    }

    public void setEmbeddedItem (EmbeddedItem _embedded)
    {
        this._embedded = _embedded;
    }

    /**
     * Checks if have error item.
     * @return true or false.
     */
    public boolean isError() {
        return (_embedded != null
                && _embedded.getSSErrorItem() != null
                && _embedded.getSSErrorItem().length > 0);
    }

    /**
     * Get the first error item from EmbeddedItem.
     * @return SSErrorItem
     */
    public SSErrorItem getErrorItem() {
        if (isError()) {
            return _embedded.getSSErrorItem()[0];
        }
        return null;
    }
}

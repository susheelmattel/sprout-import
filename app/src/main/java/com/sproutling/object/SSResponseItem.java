/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

public class SSResponseItem {
    private EmbeddedItem mEmbedded;

    public EmbeddedItem getEmbeddedItem() {
        return mEmbedded;
    }

    public void setEmbeddedItem(EmbeddedItem embedded) {
        mEmbedded = embedded;
    }

    /**
     * Checks if have error item.
     *
     * @return true or false.
     */
    public boolean isError() {
        return (mEmbedded != null
                && mEmbedded.getSSErrorItem() != null
                && mEmbedded.getSSErrorItem().length > 0);
    }

    /**
     * Get the first error item from EmbeddedItem.
     *
     * @return SSErrorItem
     */
    public SSErrorItem getErrorItem() {
        if (isError()) {
            return mEmbedded.getSSErrorItem()[0];
        }
        return null;
    }
}

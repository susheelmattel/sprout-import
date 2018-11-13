package com.fuhu.pipetest.services;

import com.fuhu.pipetest.pipeline.object.SSChildItem;

import java.util.HashMap;
import java.util.Map;

public class ChildrenManagement {
    static ChildrenManagement sInstance;
    private Map<String, SSChildItem> mChildren;

    private ChildrenManagement() {
        mChildren = new HashMap<>();
    }

    public static ChildrenManagement getInstance() {
        if (sInstance == null) {
            synchronized (ChildrenManagement.class) {
                if (sInstance == null) {
                    sInstance = new ChildrenManagement();
                }
            }
        }
        return sInstance;
    }

    public Map<String, SSChildItem> getChildren() {
        return mChildren;
    }

    public synchronized void addChild(final Map<String, Object> userMap) {
        if (mChildren != null) {
            String childId = (String)userMap.get("id");

            if (!mChildren.containsKey(childId)) {
                SSChildItem child = new SSChildItem();
                child.setId(childId);
                mChildren.put(childId, child);
            }
        }
    }

    public synchronized void deleteChild(final String id) {
        if (mChildren != null) {
            mChildren.remove(id);
        }
    }

    public synchronized void updateChild(final SSChildItem newChild) {
        if (mChildren != null && newChild != null && newChild.getId() != null) {
            String childId = newChild.getId();
            if (mChildren.containsKey(childId)) {
                mChildren.remove(childId);
            }
            mChildren.put(childId, newChild);
        }
    }
}
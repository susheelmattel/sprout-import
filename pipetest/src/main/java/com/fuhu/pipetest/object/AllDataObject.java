package com.fuhu.pipetest.object;

import java.util.HashMap;

/**
 * Created by Xylon on 2016/12/29.
 */

public class AllDataObject {

    //The KEY to find the Data.
    public static final String STATUS_ACTIVITY_BG = "statusActivityBg";

    //The Data defined.
    private int statusActivityBgResource;

    public AllDataObject()
    {
        super();
    }

    public AllDataObject(HashMap<String, Object> DataSet)
    {
        if (DataSet.size() > 0)
        {
            statusActivityBgResource = (int)DataSet.get(STATUS_ACTIVITY_BG);
        }
    }

    public int getStatusActivityBgResource()
    {
        return statusActivityBgResource;
    }

    public void setStatusActivityBgResource(int resourceId) {
        this.statusActivityBgResource = resourceId;
    }

}

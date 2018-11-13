package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;

/**
 * Created by 322511 on 6/20/2018.
 */

public class UpdateDeviceNameRequestBody {

    @SerializedName("name")
    private String deviceName;

    @SerializedName("owner_id")
    private String ownerId;

    @SerializedName("owner_type")
    private String ownerType;


    public UpdateDeviceNameRequestBody(String deviceName, String ownerId, String ownerType) {
        this.deviceName = deviceName;
        this.ownerId = ownerId;
        this.ownerType = ownerType;
    }
}

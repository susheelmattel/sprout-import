package com.sproutling.apitest.services;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bradylin on 3/2/17.
 */

public class SSStatus {

    public static final String STATUS_OK = "OK";
    public static final String STATUS_NOT_FOUND = "NotFound";

    public String status;

    public SSStatus(JSONObject jsonObject) throws JSONException {
        status = jsonObject.getString("status");
    }

    public static boolean hasStatus(String content) {
        return content.contains("status");
    }

    public boolean isSuccess() {
        return STATUS_OK.equalsIgnoreCase(status);
    }

    public boolean isNotFound() {
        return STATUS_NOT_FOUND.equals(status);
    }

    @Override
    public String toString() {
        return "Status: " + status;
    }
}
package com.sproutling.pojos;

import java.util.Locale;

/**
 * Created by subram13 on 3/15/17.
 */

public class UpdateHandheldRequestBody extends CreateHandheldRequestBody {
    public UpdateHandheldRequestBody(String uuid, String token, Locale locale,String appID) {
        super(uuid,token,locale,appID);
    }
}

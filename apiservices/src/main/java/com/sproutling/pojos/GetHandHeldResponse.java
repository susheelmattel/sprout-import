package com.sproutling.pojos;

import java.util.Locale;

/**
 * Created by subram13 on 3/15/17.
 */

public class GetHandHeldResponse extends CreateHandheldResponse {
    public GetHandHeldResponse( String uuid,String token, String createdAt, String updatedAt, String id, String userId, Locale locale,String appID) {
        super(uuid,token, createdAt, updatedAt, id, userId,locale,appID);
    }
}

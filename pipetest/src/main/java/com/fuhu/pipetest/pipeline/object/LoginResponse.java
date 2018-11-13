package com.fuhu.pipetest.pipeline.object;

public class LoginResponse {
    private String scopes;
    private String resource_owner_id;
    private String created_at;
    private String token_type;
    private String expires_in;
    private String refresh_token;
    private String access_token;

    public String getScopes () {
        return scopes;
    }

    public void setScopes (String scopes) {
        this.scopes = scopes;
    }

    public String getResourceOwnerId () {
        return resource_owner_id;
    }

    public void setResourceOwnerId (String resource_owner_id) {
        this.resource_owner_id = resource_owner_id;
    }

    public String getCreatedAt () {
        return created_at;
    }

    public void setCreatedAt (String created_at) {
        this.created_at = created_at;
    }

    public String getTokenType () {
        return token_type;
    }

    public void setTokenType (String token_type) {
        this.token_type = token_type;
    }

    public String getExpiresIn () {
        return expires_in;
    }

    public void setExpiresIn (String expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefreshToken () {
        return refresh_token;
    }

    public void setRefreshToken (String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getAccessToken () {
        return access_token;
    }

    public void setAccessToken (String access_token) {
        this.access_token = access_token;
    }

    @Override
    public String toString()
    {
        return "[scopes = "+scopes+", resource_owner_id = "+resource_owner_id+", created_at = "+created_at+", token_type = "+token_type+", expires_in = "+expires_in+", refresh_token = "+refresh_token+", access_token = "+access_token+"]";
    }
}
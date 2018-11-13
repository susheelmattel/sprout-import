package com.fuhu.pipetest.pipeline.object;

public class LoginItem {
    private String username;
    private String grant_type;
    private String client_id;
    private String password;
    private String scope;

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getGrantType () {
        return grant_type;
    }

    public void setGrantType (String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClientId () {
        return client_id;
    }

    public void setClientId (String client_id) {
        this.client_id = client_id;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public String getScope () {
        return scope;
    }

    public void setScope (String scope) {
        this.scope = scope;
    }
}

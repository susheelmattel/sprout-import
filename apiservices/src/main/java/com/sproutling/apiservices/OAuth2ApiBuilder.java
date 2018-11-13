package com.sproutling.apiservices;

import com.sproutling.pojos.LoginRequestBody;
import com.sproutling.pojos.LoginResponse;
import com.sproutling.pojos.LogoutResponse;
import com.sproutling.pojos.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by subram13 on 3/6/17.
 */

public class OAuth2ApiBuilder extends ApiBuilder<OAuth2ApiBuilder.OAuth2Api> {
    public static final String LOGIN_URL = "identity/v1/oauth2/token";
    private static final String LOGOUT_URL = "identity/v1/oauth2/logout";
    private static final String TOKEN_INFO_URL = "identity/v1/oauth2/token/info";

    @Override
    protected Class<OAuth2Api> getService() {
        return OAuth2Api.class;
    }

    @Override
    protected OAuth2Api getMockInstance() {
        return null;
    }

    public interface OAuth2Api {
        @POST(LOGIN_URL)
        Call<LoginResponse> login(@Body LoginRequestBody loginRequestBody);

        @POST(LOGOUT_URL)
        Call<LogoutResponse> logout();

        @GET(TOKEN_INFO_URL)
        Call<User> getTokenInfo();

    }
}

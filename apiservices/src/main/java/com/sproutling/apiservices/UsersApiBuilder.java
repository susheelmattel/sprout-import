package com.sproutling.apiservices;

import com.sproutling.pojos.CreateUserRequestBody;
import com.sproutling.pojos.CreateUserResponse;
import com.sproutling.pojos.ResetPasswordRequestBody;
import com.sproutling.pojos.ResetPasswordResponse;
import com.sproutling.pojos.ResetPinRequestBody;
import com.sproutling.pojos.ResetPinResponse;
import com.sproutling.pojos.UpdateUserRequestBody;
import com.sproutling.pojos.ValidatePinRequestBody;
import com.sproutling.pojos.ValidatePinResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by subram13 on 3/6/17.
 */

public class UsersApiBuilder extends ApiBuilder<UsersApiBuilder.UsersApi> {
    private static final String CREATE_USER_URL = "identity/v1/users";
    private static final String GET_USER_URL = "identity/v1/users/{id}";
    private static final String REQ_RESET_PIN_URL = "identity/v1/passwords/reset";
    private static final String VALIDATE_RESET_PIN_URL = "/identity/v1/passwords/reset/validate_pin";
    private static final String RESET_PASSWORD_URL = "/identity/v1/passwords/reset/pin";

    @Override
    protected Class<UsersApi> getService() {
        return UsersApi.class;
    }

    @Override
    protected UsersApi getMockInstance() {
        return null;
    }

    public interface UsersApi {
        @POST(CREATE_USER_URL)
        Call<CreateUserResponse> createUser(@Body CreateUserRequestBody createUserRequestBody);

        @GET(GET_USER_URL)
        Call<CreateUserResponse> getUser(@Path("id") String userID);

        @PUT(GET_USER_URL)
        Call<CreateUserResponse> updateUser(@Path("id") String userID, @Body UpdateUserRequestBody updateUserRequestBody);

        @POST(REQ_RESET_PIN_URL)
        Call<ResetPinResponse> requestResetPin(@Body ResetPinRequestBody resetPinRequestBody);

        @POST(VALIDATE_RESET_PIN_URL)
        Call<ValidatePinResponse> validatePin(@Body ValidatePinRequestBody validatePinRequestBody);

        @PUT(RESET_PASSWORD_URL)
        Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequestBody resetPasswordRequestBody);

        @PUT(GET_USER_URL)
        Call<ResetPasswordResponse> resetPasswordByID(@Path("id") String userID, @Body ResetPasswordRequestBody resetPasswordRequestBody);

    }
}

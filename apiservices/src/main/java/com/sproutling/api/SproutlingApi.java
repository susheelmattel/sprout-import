package com.sproutling.api;

import android.content.Context;

import com.sproutling.apiservices.ApiBuilder;
import com.sproutling.apiservices.AppVersionApiBuilder;
import com.sproutling.apiservices.ChildrenApiBuilder;
import com.sproutling.apiservices.EventApiBuilder;
import com.sproutling.apiservices.HandheldApiBuilder;
import com.sproutling.apiservices.OAuth2ApiBuilder;
import com.sproutling.apiservices.PhotoApiBuilder;
import com.sproutling.apiservices.ProductsApiBuilder;
import com.sproutling.apiservices.UserFeedBackApiBuilder;
import com.sproutling.apiservices.UsersApiBuilder;
import com.sproutling.pojos.AppVersionResponseBody;
import com.sproutling.pojos.Child;
import com.sproutling.pojos.CreateChildRequestBody;
import com.sproutling.pojos.CreateChildResponse;
import com.sproutling.pojos.CreateEventRequestBody;
import com.sproutling.pojos.CreateHandheldRequestBody;
import com.sproutling.pojos.CreateHandheldResponse;
import com.sproutling.pojos.CreatePhotoResponse;
import com.sproutling.pojos.CreateUserRequestBody;
import com.sproutling.pojos.CreateUserResponse;
import com.sproutling.pojos.Device;
import com.sproutling.pojos.DevicePresetSettingsRequestBody;
import com.sproutling.pojos.DeviceRequestBody;
import com.sproutling.pojos.DeviceResponse;
import com.sproutling.pojos.DeviceType;
import com.sproutling.pojos.GetHandHeldResponse;
import com.sproutling.pojos.GetPhotoResponse;
import com.sproutling.pojos.LoginRequestBody;
import com.sproutling.pojos.LoginResponse;
import com.sproutling.pojos.ProductResponse;
import com.sproutling.pojos.ProductSettings;
import com.sproutling.pojos.ProductSettingsRequestBody;
import com.sproutling.pojos.ProductSettingsResponseBody;
import com.sproutling.pojos.ResetPasswordRequestBody;
import com.sproutling.pojos.ResetPasswordResponse;
import com.sproutling.pojos.ResetPinRequestBody;
import com.sproutling.pojos.ResetPinResponse;
import com.sproutling.pojos.UpdateChildRequestBody;
import com.sproutling.pojos.UpdateChildResponse;
import com.sproutling.pojos.UpdateDeviceNameRequestBody;
import com.sproutling.pojos.UpdateHandheldRequestBody;
import com.sproutling.pojos.UpdateHandheldResponse;
import com.sproutling.pojos.UpdateUserRequestBody;
import com.sproutling.pojos.User;
import com.sproutling.pojos.UserStatusFeedbackRequestBody;
import com.sproutling.pojos.UserStatusFeedbackResponseBody;
import com.sproutling.pojos.ValidatePinRequestBody;
import com.sproutling.pojos.ValidatePinResponse;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by subram13 on 3/6/17.
 */

public class SproutlingApi {
    private static String AUTH_TOKEN;
    private static String REFRESH_TOKEN;

    public static void initialize(Context ctx, String serverUrl) {
        ApiBuilder.SERVER_URL = serverUrl;
        ApiBuilder.apicontext=ctx;
    }

    public static void updateServerUrl(String serverUrl) {
        ApiBuilder.SERVER_URL = serverUrl;
    }

    public static void createPhoto(String childId, File file, String authToken, Callback<CreatePhotoResponse> callback) {
        AUTH_TOKEN = authToken;
        uploadPhoto(childId, file, callback);
    }

    public static void createPhoto(String childId, File file, Callback<CreatePhotoResponse> callback) {
        uploadPhoto(childId, file, callback);
    }

    private static void uploadPhoto(String childId, File file, Callback<CreatePhotoResponse> callback) {
        PhotoApiBuilder.PhotoApi photoApi = new PhotoApiBuilder().createApi();
        RequestBody photofilePart = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData("profile_img", file.getName(), photofilePart);
        Call<CreatePhotoResponse> createPhotoResponseCall = photoApi.createPhoto(childId, fileBody);
        createPhotoResponseCall.enqueue(callback);
    }

    public static void createUser(CreateUserRequestBody createUserRequestBody, Callback<CreateUserResponse> callback) {
        UsersApiBuilder.UsersApi usersApi = new UsersApiBuilder().createApi();
        Call<CreateUserResponse> createUserResponseCall = usersApi.createUser(createUserRequestBody);
        createUserResponseCall.enqueue(callback);
    }

    public static void updateUser(UpdateUserRequestBody updateUserRequestBody, Callback<CreateUserResponse> callback, String userID) {
        UsersApiBuilder.UsersApi usersApi = new UsersApiBuilder().createApi();
        Call<CreateUserResponse> createUserResponseCall = usersApi.updateUser(userID, updateUserRequestBody);
        createUserResponseCall.enqueue(callback);
    }

    public static void getUserInfo(Callback<CreateUserResponse> callback, String authToken, String userID) {
        AUTH_TOKEN = authToken;
        UsersApiBuilder.UsersApi usersApi = new UsersApiBuilder().createApi();
        Call<CreateUserResponse> getUserResponseCall = usersApi.getUser(userID);
        getUserResponseCall.enqueue(callback);
    }

    public static void requestResetPin(ResetPinRequestBody resetPinRequestBody, Callback<ResetPinResponse> callback) {
        UsersApiBuilder.UsersApi usersApi = new UsersApiBuilder().createApi();
        Call<ResetPinResponse> resetPinResponseCall = usersApi.requestResetPin(resetPinRequestBody);
        resetPinResponseCall.enqueue(callback);
    }

    public static void validatePin(ValidatePinRequestBody validatePinRequestBody, Callback<ValidatePinResponse> callback) {
        UsersApiBuilder.UsersApi usersApi = new UsersApiBuilder().createApi();
        Call<ValidatePinResponse> validatePinResponseCall = usersApi.validatePin(validatePinRequestBody);
        validatePinResponseCall.enqueue(callback);
    }

    public static void resetPassword(ResetPasswordRequestBody resetPasswordRequestBody, Callback<ResetPasswordResponse> callback) {
        UsersApiBuilder.UsersApi usersApi = new UsersApiBuilder().createApi();
        Call<ResetPasswordResponse> resetPasswordResponseCall = usersApi.resetPassword(resetPasswordRequestBody);
        resetPasswordResponseCall.enqueue(callback);
    }

    public static void resetPasswordByID(ResetPasswordRequestBody resetPasswordRequestBody, String userID,  Callback<ResetPasswordResponse> callback) {
        UsersApiBuilder.UsersApi usersApi = new UsersApiBuilder().createApi();
        Call<ResetPasswordResponse> resetPasswordResponseCall = usersApi.resetPasswordByID(userID, resetPasswordRequestBody);
        resetPasswordResponseCall.enqueue(callback);
    }

    public static void login(LoginRequestBody loginRequestBody, Callback<LoginResponse> callback) {
        OAuth2ApiBuilder.OAuth2Api oAuth2Api = new OAuth2ApiBuilder().createApi();
        Call<LoginResponse> loginResponseCall = oAuth2Api.login(loginRequestBody);
        loginResponseCall.enqueue(callback);
    }

    public static void getTokenInfo(Callback<User> callback, String authToken) {
        AUTH_TOKEN = authToken;
        OAuth2ApiBuilder.OAuth2Api oAuth2Api = new OAuth2ApiBuilder().createApi();
        Call<User> userCall = oAuth2Api.getTokenInfo();
        userCall.enqueue(callback);

    }

    public static void createChild(CreateChildRequestBody createChildRequestBody, Callback<CreateChildResponse> callback) {
        ChildrenApiBuilder.ChildrenApi childrenApi = new ChildrenApiBuilder().createApi();
        Call<CreateChildResponse> createChildResponseCall = childrenApi.createChild(createChildRequestBody);
        createChildResponseCall.enqueue(callback);
    }

    public static void updateChild(UpdateChildRequestBody updateChildRequestBody, Callback<UpdateChildResponse> callback, String childID, String authToken) {
        AUTH_TOKEN = authToken;
        ChildrenApiBuilder.ChildrenApi childrenApi = new ChildrenApiBuilder().createApi();
        Call<UpdateChildResponse> updateChildResponseCall = childrenApi.updateChild(childID, updateChildRequestBody);
        updateChildResponseCall.enqueue(callback);
    }

    public static void createChild(CreateChildRequestBody createChildRequestBody, Callback<CreateChildResponse> callback, String authToken) {
        AUTH_TOKEN = authToken;
        ChildrenApiBuilder.ChildrenApi childrenApi = new ChildrenApiBuilder().createApi();
        Call<CreateChildResponse> createChildResponseCall = childrenApi.createChild(createChildRequestBody);
        createChildResponseCall.enqueue(callback);
    }

    public static void getChildrenList(Callback<ArrayList<Child>> callback, String authToken) {
        AUTH_TOKEN = authToken;
        ChildrenApiBuilder.ChildrenApi childrenApi = new ChildrenApiBuilder().createApi();
        Call<ArrayList<Child>> childrenCall = childrenApi.getChildren();
        childrenCall.enqueue(callback);
    }

    public static void getChildPhoto(String photoId, Callback<GetPhotoResponse> callback, String authToken) {
        AUTH_TOKEN = authToken;
        PhotoApiBuilder.PhotoApi photoApi = new PhotoApiBuilder().createApi();
        Call<GetPhotoResponse> getPhotoResponseCall = photoApi.getPhoto(photoId);
        getPhotoResponseCall.enqueue(callback);

    }

    public static void downloadChildPhoto(String pathUrl, Callback<ResponseBody> callback, String authToken) {
        AUTH_TOKEN = authToken;
        PhotoApiBuilder.PhotoApi photoApi = new PhotoApiBuilder().createApi();
        Call<ResponseBody> call = photoApi.downloadPhoto(pathUrl);
        call.enqueue(callback);
    }

    public static void createHandheld(CreateHandheldRequestBody createHandheldRequestBody,
                                      Callback<CreateHandheldResponse> createHandheldResponseCallback,
                                      String authToken) {
        AUTH_TOKEN = authToken;
        HandheldApiBuilder.HandheldApi handheldApi = new HandheldApiBuilder().createApi();
        Call<CreateHandheldResponse> createHandheldResponseCall = handheldApi.createHandheld(createHandheldRequestBody);
        createHandheldResponseCall.enqueue(createHandheldResponseCallback);
    }

    public static void updateHandheld(String id, UpdateHandheldRequestBody updateHandheldRequestBody,
                                      Callback<UpdateHandheldResponse> updateHandheldResponseCallback,
                                      String authToken) {
        AUTH_TOKEN = authToken;
        HandheldApiBuilder.HandheldApi handheldApi = new HandheldApiBuilder().createApi();
        Call<UpdateHandheldResponse> updateHandheldResponseCall = handheldApi.updateHandheld(id, updateHandheldRequestBody);
        updateHandheldResponseCall.enqueue(updateHandheldResponseCallback);
    }

    public static void getHandheld(String id,
                                   Callback<GetHandHeldResponse> getHandHeldResponseCallback,
                                   String authToken) {
        AUTH_TOKEN = authToken;
        HandheldApiBuilder.HandheldApi handheldApi = new HandheldApiBuilder().createApi();
        Call<GetHandHeldResponse> getHandHeldResponseCall = handheldApi.getHandheld(id);
        getHandHeldResponseCall.enqueue(getHandHeldResponseCallback);
    }

    public static void deleteHandheld(String id,
                                      Callback<ResponseBody> responseBodyCallback,
                                      String authToken) {
        AUTH_TOKEN = authToken;
        HandheldApiBuilder.HandheldApi handheldApi = new HandheldApiBuilder().createApi();
        Call<ResponseBody> deleteHandheld = handheldApi.deleteHandheld(id);
        deleteHandheld.enqueue(responseBodyCallback);
    }

    public static void createEvent(CreateEventRequestBody createEventRequestBody,
                                   Callback<ResponseBody> responseBodyCallback, String authToken) {
        AUTH_TOKEN = authToken;
        EventApiBuilder.EventApi eventApi = new EventApiBuilder().createApi();
        Call<ResponseBody> createEvent = eventApi.createEvent(createEventRequestBody);
        createEvent.enqueue(responseBodyCallback);
    }

    public static void getAppVersionInfo(Callback<AppVersionResponseBody> appVersionResponseBodyCallback) {
        AppVersionApiBuilder.AppVersionApi appVersionApi = new AppVersionApiBuilder().createApi();
        Call<AppVersionResponseBody> getAppVersion = appVersionApi.getAppVersion();
        getAppVersion.enqueue(appVersionResponseBodyCallback);
    }

    public static void sendUserStatusFeedback(UserStatusFeedbackRequestBody userStatusFeedbackRequestBody,
                                              Callback<UserStatusFeedbackResponseBody> userStatusFeedbackResponseBodyCallback,
                                              String authToken) {
        AUTH_TOKEN = authToken;
        UserFeedBackApiBuilder.UserFeedBackApi userFeedBackApi = new UserFeedBackApiBuilder().createApi();
        Call<UserStatusFeedbackResponseBody> userStatusFeedbackResponseBodyCall = userFeedBackApi.sendUserStatusFeedback(userStatusFeedbackRequestBody);
        userStatusFeedbackResponseBodyCall.enqueue(userStatusFeedbackResponseBodyCallback);
    }

    public static void getDeviceTypes(Callback<ArrayList<DeviceType>> productResponseCallback, String authToken) {
        AUTH_TOKEN = authToken;
        ProductsApiBuilder.ProductsApi productsApi = new ProductsApiBuilder().createApi();
        Call<ArrayList<DeviceType>> productTypeResponseCall = productsApi.getDeviceTypes();
        productTypeResponseCall.enqueue(productResponseCallback);

    }

    public static void createDevice(String authToken, DeviceRequestBody deviceRequestBody, Callback<Device> deviceResponseCallback) {
        AUTH_TOKEN = authToken;
        ProductsApiBuilder.ProductsApi productsApi = new ProductsApiBuilder().createApi();
        Call<Device> deviceCreationResponseCall = productsApi.createDevice(deviceRequestBody);
        deviceCreationResponseCall.enqueue(deviceResponseCallback);

    }

    public static void createDeviceSettings(ProductSettings productSettingsRequestBody, Callback<ProductSettings> responseBodyCallback, String authToken) {
        AUTH_TOKEN = authToken;
        ProductsApiBuilder.ProductsApi productsApi = new ProductsApiBuilder().createApi();
        Call<ProductSettings> productResponseCall = productsApi.createProductSettings(productSettingsRequestBody);
        productResponseCall.enqueue(responseBodyCallback);
    }

    public static void deleteDevicebySerial(String authToken, String serial, Callback<ResponseBody> deviceResponseCallback) {
        AUTH_TOKEN = authToken;
        ProductsApiBuilder.ProductsApi productsApi = new ProductsApiBuilder().createApi();
        Call<ResponseBody> deviceCreationResponseCall = productsApi.deleteDevice(serial);
        deviceCreationResponseCall.enqueue(deviceResponseCallback);
    }

    public static void getProducts(Callback<ProductResponse> productResponseCallback, String authToken) {
        AUTH_TOKEN = authToken;
        ProductsApiBuilder.ProductsApi productsApi = new ProductsApiBuilder().createApi();
        Call<ProductResponse> productResponseCall = productsApi.getProducts();
        productResponseCall.enqueue(productResponseCallback);

    }

    public static void saveProductSettings(ProductSettingsRequestBody productSettingsRequestBody, Callback<ResponseBody> responseBodyCallback, String authToken) {
        AUTH_TOKEN = authToken;
        ProductsApiBuilder.ProductsApi productsApi = new ProductsApiBuilder().createApi();
        Call<ResponseBody> productResponseCall = productsApi.saveProductSettings(productSettingsRequestBody);
        productResponseCall.enqueue(responseBodyCallback);
    }

    public static void updateProductSettings(ProductSettingsRequestBody productSettingsRequestBody, Callback<ResponseBody> responseBodyCallback, String authToken) {
        AUTH_TOKEN = authToken;
        ProductsApiBuilder.ProductsApi productsApi = new ProductsApiBuilder().createApi();
        Call<ResponseBody> productResponseCall = productsApi.updateProductSettings(productSettingsRequestBody);
        productResponseCall.enqueue(responseBodyCallback);
    }

    public static void updateDevicePresetSettings(DevicePresetSettingsRequestBody devicePresetSettingsRequestBody, Callback<ResponseBody> responseBodyCallback, String authToken) {
        AUTH_TOKEN = authToken;
        ProductsApiBuilder.ProductsApi productsApi = new ProductsApiBuilder().createApi();
        Call<ResponseBody> productResponseCall = productsApi.updateDevicePresetSettings(devicePresetSettingsRequestBody);
        productResponseCall.enqueue(responseBodyCallback);
    }



    public static void getProductSettings(Callback<ProductSettingsResponseBody> productSettingsResponseBodyCallback, String deviceID, String authToken) {
        ProductsApiBuilder.ProductsApi productsApi = new ProductsApiBuilder().createApi();
        Call<ProductSettingsResponseBody> productSettingsResponseBodyCall = productsApi.getProductSettings(deviceID);
        productSettingsResponseBodyCall.enqueue(productSettingsResponseBodyCallback);
    }

    public static void getDeviceList(Callback<ArrayList<Device>> deviceListCallback, String authToken) {
        AUTH_TOKEN = authToken;
        ProductsApiBuilder.ProductsApi productsApi = new ProductsApiBuilder().createApi();
        Call<ArrayList<Device>> deviceListResponseBodyCall = productsApi.getDevicesList();
        deviceListResponseBodyCall.enqueue(deviceListCallback);
    }


    public static void updateProductName(UpdateDeviceNameRequestBody updateDeviceNameRequestBody, Callback<DeviceResponse> responseBodyCallback, String authToken) {
        AUTH_TOKEN = authToken;
        ProductsApiBuilder.ProductsApi productsApi = new ProductsApiBuilder().createApi();
        Call<DeviceResponse> productResponseCall = productsApi.updateProductName(updateDeviceNameRequestBody);
        productResponseCall.enqueue(responseBodyCallback);
    }

    /* Updates the name from the product setting screen. */
    public static void updateProductName(String serial, UpdateDeviceNameRequestBody updateDeviceNameRequestBody, Callback<DeviceResponse> responseBodyCallback, String authToken) {
        AUTH_TOKEN = authToken;
        ProductsApiBuilder.ProductsApi productsApi = new ProductsApiBuilder().createApi();
        Call<DeviceResponse> productResponseCall = productsApi.updateProductSetting(serial,updateDeviceNameRequestBody);
        productResponseCall.enqueue(responseBodyCallback);
    }

    //TODO:update this implementation once all the APIs are moved here
    public static String getAuthToken() {
        return AUTH_TOKEN;
    }

    public static void setAuthToken(String authToken) {
        AUTH_TOKEN = authToken;
    }

    public static String getRefreshToken() {
        return REFRESH_TOKEN;
    }

    public static void setRefreshToken(String refreshToken) {
        REFRESH_TOKEN = refreshToken;
    }
}

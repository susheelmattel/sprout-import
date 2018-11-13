package com.sproutling.apiservices;

import com.sproutling.pojos.Device;
import com.sproutling.pojos.DevicePresetResponseBody;
import com.sproutling.pojos.DevicePresetSettingsRequestBody;
import com.sproutling.pojos.DeviceRequestBody;
import com.sproutling.pojos.DeviceResponse;
import com.sproutling.pojos.DeviceType;
import com.sproutling.pojos.ProductResponse;
import com.sproutling.pojos.ProductSettings;
import com.sproutling.pojos.ProductSettingsRequestBody;
import com.sproutling.pojos.ProductSettingsResponseBody;
import com.sproutling.pojos.UpdateDeviceNameRequestBody;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by subram13 on 2/14/18.
 */

public class ProductsApiBuilder extends ApiBuilder<ProductsApiBuilder.ProductsApi> {
    private static final String PRODUCTS_URL = "identity/v1/products";
    private static final String PRODUCTS_TYPE_URL = "identity/v1/products";
    private static final String PRODUCTS_SETTINGS = "identity/v1/product_settings";
    private static final String DEVICE_LIST_URL = "device/v1/devices";
    private static final String UPDATE_PRODUCTNAME_URL = "/devices/{serial}";
    private static final String UPDATE_PRODUCT_SETTINGS = "device/v1/devices/{serial}";

    @Override
    protected Class<ProductsApi> getService() {
        return ProductsApi.class;
    }

    @Override
    protected ProductsApi getMockInstance() {
        return null;
    }

    public interface ProductsApi {
        @GET(PRODUCTS_URL)
        Call<ProductResponse> getProducts();

        @GET(PRODUCTS_URL)
        Call<ArrayList<DeviceType>> getDeviceTypes();

        @GET(DEVICE_LIST_URL)
        Call<ArrayList<Device>> getDevicesList();

        @POST(DEVICE_LIST_URL)
        Call<Device> createDevice(@Body DeviceRequestBody deviceRequestBody);

        @POST(PRODUCTS_SETTINGS)
        Call<ProductSettings> createProductSettings(@Body ProductSettings productSettings);

        @DELETE(DEVICE_LIST_URL+"/{serial}")
        Call<ResponseBody> deleteDevice(@Path("serial") String serial);

        @POST(PRODUCTS_SETTINGS)
        Call<ResponseBody> saveProductSettings(@Body ProductSettingsRequestBody productSettingsRequestBody);

        @PUT(PRODUCTS_SETTINGS)
        Call<ResponseBody> updateProductSettings(@Body ProductSettingsRequestBody productSettingsRequestBody);

        @GET(PRODUCTS_SETTINGS)
        Call<ProductSettingsResponseBody> getProductSettings(@Query("device_id") String deviceID);

        @POST(UPDATE_PRODUCTNAME_URL)
        Call<DeviceResponse> updateProductName(@Body UpdateDeviceNameRequestBody updateDeviceNameRequestBody);

        /*API for updating device settings*/
        @PUT(UPDATE_PRODUCT_SETTINGS)
        Call<DeviceResponse> updateProductSetting(@Path("serial") String serial, @Body UpdateDeviceNameRequestBody updateDeviceNameRequestBody);


        @PUT(PRODUCTS_SETTINGS)
        Call<ResponseBody> updateDevicePresetSettings(@Body DevicePresetSettingsRequestBody devicePresetSettingsRequestBody);

        @GET(PRODUCTS_SETTINGS)
        Call<DevicePresetResponseBody> getDevicePresetSettings(@Query("device_id") String deviceID);

    }
}

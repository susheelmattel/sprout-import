/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.common.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fisherprice.api.models.FPModel;
import com.fisherprice.api.utilities.FPUtilities;
import com.fisherprice.smartconnect.api.constants.FPBLEConstants;
import com.fisherprice.smartconnect.api.models.FPDeluxeSleeperModel;
import com.fisherprice.smartconnect.api.models.FPLampSootherModel;
import com.fisherprice.smartconnect.api.models.FPSeahorseModel;
import com.fisherprice.smartconnect.api.models.FPSleeperModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.sproutling.api.SproutlingApi;
import com.sproutling.common.R;
import com.sproutling.common.app.BaseApplication;
import com.sproutling.common.pojos.DeviceParent;
import com.sproutling.common.pojos.events.PushNotificationRegistrationEvent;
import com.sproutling.pojos.CreateHandheldRequestBody;
import com.sproutling.pojos.CreateHandheldResponse;
import com.sproutling.pojos.Device;
import com.sproutling.pojos.DeviceType;
import com.sproutling.pojos.ProductSettings;
import com.sproutling.pojos.UpdateHandheldRequestBody;
import com.sproutling.pojos.UpdateHandheldResponse;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fisherprice.smartconnect.api.constants.FPBLEConstants.CONNECT_PERIPHERAL_TYPE.DELUXE_SLEEPER;
import static com.fisherprice.smartconnect.api.constants.FPBLEConstants.CONNECT_PERIPHERAL_TYPE.LAMP_SOOTHER;
import static com.fisherprice.smartconnect.api.constants.FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SEAHORSE;
import static com.fisherprice.smartconnect.api.constants.FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SLEEPER;
import static java.lang.Math.cos;
import static java.lang.Math.sin;


public class Utils {

    public static final String TAG = Utils.class.getSimpleName();
    public static final String SHARED_PREFERENCES_FILE = "account";
    public static final String SHARED_PREFERENCES_ALERT = "alert";

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static final int ONE_SECOND_IN_MILLISECOND = 1000;
    private static int MAX_BATTERY_VOLTAGE = 4200; //4.2V
    private static int MIN_BATTERY_VOLTAGE = 3000; // 3V

    public static final int PERMISSION_REQUEST_CALL = 20;

    public final static int SOOTHER_MIN_VERSION = 10;
    public final static int SLEEPER_MIN_VERSION = 10;
    public final static int DELUXE_SLEEPER_MIN_VERSION = 5;
    public final static int SEAHORSE_MIN_VERSION = 13;

    public final static int PHONE_LENGTH_MIN_WO_CODE = 9;
    public final static int PHONE_LENGTH_MAX_WO_CODE = 11;
    public final static int PHONE_LENGTH_MIN = 12;
    public final static int PHONE_LENGTH_MAX = 14;

    public static String getChildPhotoFileName() {
        return "IMG_profile";
    }

    public static boolean isFilePath(String path) {
        return path.split(":")[0].contains("file");
    }

    public static String getPathForPreV19(Context context, Uri contentUri) {
        String res = null;

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();

        return res;
    }

    public static String getPathForV19AndUp(Context context, Uri contentUri) {
        String wholeID = DocumentsContract.getDocumentId(contentUri);

        String id = wholeID.split(":")[1];
        String[] column = {MediaStore.Images.Media.DATA};

        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        String filePath = "";
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    // To rotate Camera image
    public static Bitmap rotateImageIfRequired(Context context, Bitmap img, String uriPath, Uri imgUri) throws IOException {
        ExifInterface ei = new ExifInterface(uriPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            case ExifInterface.ORIENTATION_UNDEFINED:
                return rotateBitmap(context, imgUri, img);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    // To rotate Gallery image
    public static Bitmap rotateBitmap(Context context, Uri photoUri, Bitmap bitmap) {
        int orientation = getOrientation(context, photoUri);
        if (orientation <= 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return bitmap;
    }

    private static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        assert cursor != null;
        if (cursor.getCount() != 1) {
            cursor.close();
            return -1;
        }


        int orientation = -1;
        if(cursor.getColumnCount() > 0){
            cursor.moveToFirst();
            orientation = cursor.getInt(0);
        }
//        if(cursor.moveToFirst()){
//            orientation = cursor.getInt(0);
//        }
        cursor.close();
        cursor = null;
        return orientation;
    }


    private static void showAlertDialog(Activity context,
                                        String title, String msg, String positiveBtnStr, String negativeBtnStr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveBtnStr, null);
        if (!TextUtils.isEmpty(negativeBtnStr)) {
            builder.setNegativeButton(negativeBtnStr, null);
        }
        builder.show();
    }

    public static boolean isGPSEnabled(Context ctx) {
        final LocationManager manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        boolean retVal = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return retVal;
    }

    public static float[] getPointByAngle(int centerX, int centerY, float pointR, float startAngle, float progressAngle) {
        double progressPointRadians = Math.toRadians((-1 * startAngle) - progressAngle);
        return new float[]{
                centerX + pointR * (float) cos(progressPointRadians),
                centerY - pointR * (float) sin(progressPointRadians)
        };
    }

    public static boolean isLocationPermissionGranted(Context context) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * @param ctx Pass the activity context here
     * @return
     */
    public static boolean checkPlayServices(Activity ctx) {
        GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
        int resultCode = gApi.isGooglePlayServicesAvailable(ctx);
        return resultCode == ConnectionResult.SUCCESS;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.M) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static boolean isAlphaNumeric(String value) {
        Pattern p = Pattern.compile("[A-Za-z0-9]");
        return p.matcher(value).find();
    }

    public static boolean isValidSerialID(String value) {
        Pattern p = Pattern.compile("(\\d)(\\d{2})(\\w)(\\w{3})(\\w)(\\d{5})");
        return p.matcher(value).find();
    }

    public static boolean isValidWifiSSID(String value) {
        Pattern p = Pattern.compile("^[!#-&(-<>-\\[\\]-~]{1}[ !#-&(-<>-\\[\\]-~]*[!#-&(-<>-\\[\\]-~]{1}$");
        return p.matcher(value).find();
    }

    public static boolean isPhoneValid(String phone) {
        String formattedPhone = Utils.formatPhone(phone);
        if (phone.length()!= 0 && phone.charAt(0) == '+')
            return withCountryCode(formattedPhone);
        else
            return withoutCountryCode(formattedPhone);
    }

    private static boolean withoutCountryCode(String phone) {
        return !(phone.length() < PHONE_LENGTH_MIN_WO_CODE || phone.length() > PHONE_LENGTH_MAX_WO_CODE) && phone.matches("[0-9]+");
    }

    private static boolean withCountryCode(String phone) {
        return !(phone.length() < PHONE_LENGTH_MIN || phone.length() > PHONE_LENGTH_MAX) && phone.substring(1).matches("[0-9]([\\s -]*[0-9])+");
    }

    public static String formatPhone(String number) {
        return number.replaceAll("[()\\-\\s]", "");
    }

    public static String prefixCountryCode(String number) {
        String phone = formatPhone(number);
         if(number.charAt(0) == '+'){
          return  phone;
         } else {
             PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
             int countryCode = phoneUtil.getCountryCodeForRegion(Locale.getDefault().getCountry());

             return "+" + String.valueOf(countryCode) + phone;
         }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static int getBatteryValue(int batteryVoltage) {
        int batteryValue = 0;
        int bValue = batteryVoltage - MIN_BATTERY_VOLTAGE;
        if (bValue <= 0) {
            batteryValue = 0;
        } else {
            int range = MAX_BATTERY_VOLTAGE - MIN_BATTERY_VOLTAGE;
            batteryValue = (bValue * 100) / range;
            Log.d(TAG, "Battery Value: " + batteryValue);
        }
        if (batteryValue > 100) {
            batteryValue = 100;
        }
        if (batteryValue < 0) {
            batteryValue = 0;
        }
        return batteryValue;
    }


    public static String getTimeZoneInShort() {
        return TimeZone.getDefault().getDisplayName(TimeZone.getDefault().useDaylightTime(), TimeZone.SHORT);
    }

    public static String getLanguageWithCountry() {
        return Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry();
    }

    public static String toJsonString(Object object) {
        return new GsonBuilder().create().toJson(object);
    }

    public static <T> T toObjectFromJson(Class<T> className, String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, className);
    }

    public static <T> String toJsonFromList(ArrayList<T> jsonString) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<T>>() {
        }.getType();
        return gson.toJson(jsonString, listType);
    }

    public static ArrayList<Device> toDevicesListFromJson(String jsonString) {
        Gson gson = new Gson();
        Type token = new TypeToken<ArrayList<Device>>() {
        }.getType();
        return gson.fromJson(jsonString, token);
    }

    public static ArrayList<DeviceType> toDeviceTypesFromJson(String jsonString) {
        Gson gson = new Gson();
        Type token = new TypeToken<ArrayList<DeviceType>>() {
        }.getType();
        return gson.fromJson(jsonString, token);
    }

    public static ArrayList<ProductSettings> toDevicesSettingsListFromJson(String jsonString) {
        Gson gson = new Gson();
        Type token = new TypeToken<ArrayList<ProductSettings>>() {
        }.getType();
        return gson.fromJson(jsonString, token);
    }

    public static Intent getIntentToShowInstalledAppDetails(Context ctx) {
        String packageName = ctx.getApplicationContext().getPackageName();
        final int apiLevel = Build.VERSION.SDK_INT;
        Intent intent = new Intent();

        if (apiLevel >= 9) {
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
        } else {
            final String appPkgName = (apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");

            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra(appPkgName, packageName);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        // Start Activity
//        getActivity().startActivityForResult(intent, resultInt);
        return intent;
    }

    public static void savePhoto(final Bitmap bitmap, final String filename) {
        if (bitmap != null) {
            new AsyncTask<Void, Void, Void>() {
                FileOutputStream outputStream = null;

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        outputStream = new FileOutputStream(filename);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // bmp is your Bitmap sInstance
                        // PNG is a lossless format, the compression factor (100) is ignored
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (outputStream != null) {
                                outputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            }.execute();
        }
    }

    public static int getAppVersionCode(Context context) {
        int retVal = -1;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            retVal = pInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public static String getAppVersion(Context context) {
        String retVal = "1.0";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            retVal = pInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "error " + e.getMessage());
        }
        return apiKey;
    }


    public static int getMonthCountDifference(Calendar calendar) {
        int retVal = -1;
        if (Calendar.getInstance().getTimeInMillis() >= calendar.getTimeInMillis()) {
            long diff = Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis();
            long dayCount = TimeUnit.MILLISECONDS.toDays(diff);
            retVal = ((int) dayCount) / 30;
        }
        return retVal;
    }


    public static void sendNotification(Context context, String title, String message) {
//        int notifyID = 1;
//        int largeIconId;
//        switch (title) {
//            case NotificationTopic.ROLL_OVER:
//                largeIconId = R.drawable.ic_notification_rollover;
//                break;
//            case NotificationTopic.HEART_RATE_ALERT:
//                largeIconId = R.drawable.ic_notification_heart_rate;
//                break;
//            case NotificationTopic.SLEEP:
//                largeIconId = R.drawable.ic_notification_asleep;
//                break;
//            case NotificationTopic.WAKE:
//                largeIconId = R.drawable.ic_notification_awake;
//                break;
//            case NotificationTopic.STIR:
//                largeIconId = R.drawable.ic_notification_stirring;
//                break;
//            case NotificationTopic.WEARABLE_FELL_OFF:
//                largeIconId = R.drawable.ic_notification_wearable_fell_off;
//                break;
//            case NotificationTopic.OFFLINE:
//                largeIconId = R.drawable.ic_notification_hub_offline;
//                break;
//            case NotificationTopic.CANT_FIND_WEARABLE:
//                largeIconId = R.drawable.ic_notification_cant_find_wearable;
//                break;
//            case NotificationTopic.BLE_DISCONNECT:
//                largeIconId = R.drawable.ic_notification_ble_disconnect;
//                break;
//            case NotificationTopic.BATTERY_LOW:
//                largeIconId = R.drawable.ic_notification_wearable_low_battery;
//                break;
//            case NotificationTopic.OUT_OF_BATTERY:
//                largeIconId = R.drawable.ic_notification_wearable_out_of_battery;
//                break;
//            default:
//                largeIconId = R.mipmap.ic_launcher;
//                break;
//        }
//        Intent myIntent = new Intent(context, MainActivity.class);
//        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                context,
//                0,
//                myIntent,
//                PendingIntent.FLAG_ONE_SHOT);
////        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.rollover_dialog);
//        Bitmap bitmap = getBitmapFromVectorDrawable(context, largeIconId);
//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(context)
//                        .setSmallIcon(R.drawable.ic_push_notif_status_bar)
//                        .setColor(Utils.getColor(context, R.color.colorAccent))
//                        .setLargeIcon(bitmap)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setContentTitle(toCamelCase(title))
//                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
//                        .setContentText(message)
//                        .setAutoCancel(true)
//                        .setPriority(NotificationCompat.PRIORITY_MAX)
//                        .setContentIntent(pendingIntent);
//
//
//        NotificationManager mNotificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Notification notification = builder.build();
//        mNotificationManager.notify(notifyID, notification);
    }

    public static Bitmap getBitmapFromFile(File photoFile) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(BaseApplication.Companion.getSInstance().getContentResolver(),
                    Uri.fromFile(photoFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
//        if (bitmap != null) {
//            mAddChildFragmentView.setChildPhoto(bitmap)
//        }
    }

    public static String toCamelCase(String text) {
        String[] words = text.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) { // only upper-casing first letter
            if (i == 0) sb.append(Character.toUpperCase(words[i].charAt(0)))
                    .append(words[i].subSequence(1, words[i].length()));
            else sb.append(words[i]);
            if (i != words.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return BaseApplication.Companion.getSInstance().getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return BaseApplication.Companion.getSInstance().getResources().getConfiguration().locale;
        }
    }

    public static void sendRegistrationToServer(final Context context, String token) {
        String previousPushNotificationToken = SharedPrefManager.getPushNotificationTokenId(context);
        if (TextUtils.isEmpty(previousPushNotificationToken)) {
            Log.d(TAG, "Creating Handheld..");
            CreateHandheldRequestBody createHandheldRequestBody = new CreateHandheldRequestBody(SharedPrefManager.getUniqueIdentifier(context),
                    token, Utils.getCurrentLocale(), BaseApplication.Companion.getSInstance().getAppPackage());
            SproutlingApi.createHandheld(createHandheldRequestBody, new Callback<CreateHandheldResponse>() {
                @Override
                public void onResponse(Call<CreateHandheldResponse> call, Response<CreateHandheldResponse> response) {
                    if (response.isSuccessful()) {
                        saveHandheldResponse(context, response.body());
                        EventBus.getDefault().post(new PushNotificationRegistrationEvent(true, response.body(), null));
                    } else {
                        ResponseBody responseBody = response.errorBody();
                        EventBus.getDefault().post(new PushNotificationRegistrationEvent(false, null, null));
                    }
                }

                @Override
                public void onFailure(Call<CreateHandheldResponse> call, Throwable t) {
                    EventBus.getDefault().post(new PushNotificationRegistrationEvent(false, null, null));
                }
            }, AccountManagement.getInstance(context).getUserAccount().getAccessToken());
        } else if (!previousPushNotificationToken.equalsIgnoreCase(token)) {
            Log.d(TAG, "Updating Handheld..");
            UpdateHandheldRequestBody updateHandheldRequestBody = new UpdateHandheldRequestBody(SharedPrefManager.getUniqueIdentifier(context), token,
                    Utils.getCurrentLocale(), BaseApplication.Companion.getSInstance().getAppPackage());
            SproutlingApi.updateHandheld(SharedPrefManager.getHandHeldId(context), updateHandheldRequestBody, new Callback<UpdateHandheldResponse>() {
                @Override
                public void onResponse(Call<UpdateHandheldResponse> call, Response<UpdateHandheldResponse> response) {
                    if (response.isSuccessful()) {
                        saveHandheldResponse(context, response.body());
                        EventBus.getDefault().post(new PushNotificationRegistrationEvent(true, response.body(), null));
                    } else {
                        ResponseBody responseBody = response.errorBody();
                        EventBus.getDefault().post(new PushNotificationRegistrationEvent(false, response.body(), null));
                    }
                }

                @Override
                public void onFailure(Call<UpdateHandheldResponse> call, Throwable t) {
                    EventBus.getDefault().post(new PushNotificationRegistrationEvent(false, null, null));
                }
            }, AccountManagement.getInstance(context).getUserAccount().getAccessToken());
        } else {
            Log.d(TAG, "Same Push Notification Token. Ignoring the api call to create or update..");
            //Don't send the push notification token if it hasn't refreshed. Just send the event that push notification registration is success, so that the app will proceed.
            EventBus.getDefault().post(new PushNotificationRegistrationEvent(true, null, null));
        }
    }

    public static boolean isUserIdValid(String userId) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(userId).matches() || Utils.isPhoneValid(userId);
    }

    public static boolean isEmailIdValid(String userId){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(userId).matches();
    }

    public static String getAppName() {
        final PackageManager pm = BaseApplication.Companion.getSInstance().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(BaseApplication.Companion.getSInstance().getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        return applicationName;
    }


    public static Spanned getFormattedHtmlString(String stringRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(stringRes, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(stringRes);
        }
    }

    public static String convertByteStreamToString(InputStream inputStream) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void saveHandheldResponse(Context context, CreateHandheldResponse createHandheldResponse) {
//        if (createHandheldResponse != null) {
//            SharedPrefManager.saveHandHeldId(context, createHandheldResponse.getId());
//            SharedPrefManager.savePushNotificationTokenId(context, createHandheldResponse.getToken());
//        }
    }

    private interface NotificationTopic {
        String ROLL_OVER = "RollOver";
        String HEART_RATE_ALERT = "Heart rate alert";
        String SLEEP = "Sleep";
        String WAKE = "Wake";
        String STIR = "Stir";
        String WEARABLE_FELL_OFF = "Wearable fell off";
        String OFFLINE = "offline";
        String CANT_FIND_WEARABLE = "can't find wearable";
        String BLE_DISCONNECT = "BLE disconnect";
        String BATTERY_LOW = "Battery Low";
        String OUT_OF_BATTERY = "Out of battery";
    }

    /**
     * Generate a random hexadecimal ID
     */
    public static byte[] getNewDeviceSerialID() {
        UUID uuid = UUID.randomUUID();
        byte[] newUniqueIdentifierByteArray = uuid.toString().getBytes();
        newUniqueIdentifierByteArray = Arrays.copyOfRange(newUniqueIdentifierByteArray, 0, 16);
        Log.d(TAG, "getNewDeviceSerialID + new ByteArray : " + newUniqueIdentifierByteArray);
        return newUniqueIdentifierByteArray;
    }


    public static String byteArrayToHexWithNoSpaces(byte[] serialID) {
        return FPUtilities.byteArrayToHex(serialID).replaceAll("\\s+", "");
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public static ArrayList<DeviceParent> toDevicesParentListFromJson(String jsonString) {
        Gson gson = new Gson();
        Type token = new TypeToken<ArrayList<DeviceParent>>() {
        }.getType();
        return gson.fromJson(jsonString, token);
    }

    public static FPBLEConstants.CONNECT_PERIPHERAL_TYPE getPeripheralTypeById(String id, ArrayList<DeviceType> deviceTypes) {
        if (id != null) {
            for (DeviceType deviceType : deviceTypes) {
                if (deviceType.getId().equals(id)) {
                    return getPeripheralTypeFromname(deviceType.getName());
                }
            }
        }
        return null;
    }

    private static FPBLEConstants.CONNECT_PERIPHERAL_TYPE getPeripheralTypeFromname(String deviceName) {

        switch (deviceName) {
            case CommonConstant.SEA_HORSE:
                return SEAHORSE;
            case CommonConstant.DELUXE_SOOTHER:
                return LAMP_SOOTHER;
            case CommonConstant.ROCK_N_PLAY_SLEEPER:
                return SLEEPER;
            case CommonConstant.PREMIUM_ROCK_N_PLAY_SLEEPER:
                return DELUXE_SLEEPER;
        }

        return null;
    }

    public static int getImageByPeripheralType(FPBLEConstants.PERIPHERAL_TYPE peripheralType) {

        if (peripheralType != null) {
            FPBLEConstants.PERIPHERAL_TYPE type = peripheralType.getBasePeripheralType();
            if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.DELUXE_SLEEPER.equals(type)) {
                return R.drawable.ic_rnppremium_dashboard_icon;
            } else if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SLEEPER.equals(type)) {
                return R.drawable.ic_rnp_dashboard_icon;
            } else if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SEAHORSE.equals(type)) {
                return R.drawable.ic_seahorse_dashboard_icon;
            } else if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.LAMP_SOOTHER.equals(type)) {
                return R.drawable.ic_soother_dashboard_icon;
            }
        }

        // TODO replace with a default image
        return R.drawable.ic_soother_addproduct;
    }

    public static int getAddDeviceImageByPeripheralType(FPBLEConstants.PERIPHERAL_TYPE peripheralType) {

        if (peripheralType != null) {
            FPBLEConstants.PERIPHERAL_TYPE type = peripheralType.getBasePeripheralType();
            if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.DELUXE_SLEEPER.equals(type)) {
                return R.drawable.ic_rocknplay_addproduct;
            } else if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SLEEPER.equals(type)) {
                return R.drawable.ic_rocknplay_addproduct;
            } else if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SEAHORSE.equals(type)) {
                return R.drawable.ic_seahorse_addproduct;
            } else if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.LAMP_SOOTHER.equals(type)) {
                return R.drawable.ic_soother_addproduct;
            }
        }

        // TODO replace with a default image
        return R.drawable.ic_soother_addproduct;
    }

    public static String getIDFromPeripehralValue(int peripheralValue) {

        String peripheralName = getPeripheralName(peripheralValue);

        if (peripheralName == null) {
            return null;
        }
        ArrayList<DeviceType> deviceTypes = AccountData.INSTANCE.getDeviceTypes();

        for (DeviceType deviceType : deviceTypes) {
            if (deviceType.getName().equals(peripheralName)) {
                return deviceType.getId();
            }
        }
        return null;
    }

    private static String getPeripheralName(int peripheralValue) {

        if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.LAMP_SOOTHER.getValue() == peripheralValue) {
            return CommonConstant.DELUXE_SOOTHER;
        } else if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SEAHORSE.getValue() == peripheralValue) {
            return CommonConstant.SEA_HORSE;
        } else if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SLEEPER.getValue() == peripheralValue) {
            return CommonConstant.ROCK_N_PLAY_SLEEPER;
        } else if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.DELUXE_SLEEPER.getValue() == peripheralValue) {
            return CommonConstant.PREMIUM_ROCK_N_PLAY_SLEEPER;
        }

        return null;
    }

    public static String getFWVersion(FPModel fpModel) {

        int peripheralValue = fpModel.getPeripheralType().getValue();

        if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.LAMP_SOOTHER.getValue() == peripheralValue) {
            FPLampSootherModel fpLampSootherModel = (FPLampSootherModel) fpModel;
            return String.valueOf(fpLampSootherModel.getCurrentFirmwareVersion());
        } else if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SEAHORSE.getValue() == peripheralValue) {
            FPSeahorseModel fpSeahorseModel = (FPSeahorseModel) fpModel;
            return String.valueOf(fpSeahorseModel.getCurrentFirmwareVersion());
        } else if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SLEEPER.getValue() == peripheralValue) {
            FPSleeperModel fpSleeperModel = (FPSleeperModel) fpModel;
            return String.valueOf(fpSleeperModel.getCurrentFirmwareVersion());
        } else if (FPBLEConstants.CONNECT_PERIPHERAL_TYPE.DELUXE_SLEEPER.getValue() == peripheralValue) {
            FPDeluxeSleeperModel fpDeluxeSleeperModel = (FPDeluxeSleeperModel) fpModel;
            return String.valueOf(fpDeluxeSleeperModel.getCurrentFirmwareVersion());
        }

        return null;
    }

    public static String getDefaultDeviceName(FPBLEConstants.CONNECT_PERIPHERAL_TYPE peripheralType) {

        switch (peripheralType) {
            case SLEEPER:
                return getString(R.string.rock_n_play);
            case DELUXE_SLEEPER:
                return getString(R.string.premium_rock_n_play_sleeper);
            case SEAHORSE:
                return getString(R.string.seahorse);
            case LAMP_SOOTHER:
                return getString(R.string.deluxe_soother);

        }
        return null;
    }

    public static boolean isBluetoothEnabled() {
        if (BaseApplication.Companion.getSInstance() != null) {
            BluetoothManager bluetoothManager = (BluetoothManager) BaseApplication.Companion.getSInstance().getSystemService(Context.BLUETOOTH_SERVICE);
            assert bluetoothManager != null;
            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
            return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
        }
        return false;
    }

    public static boolean enableBluetooth() {
        if (BaseApplication.Companion.getSInstance() != null) {
            BluetoothManager bluetoothManager = (BluetoothManager) BaseApplication.Companion.getSInstance().getSystemService(Context.BLUETOOTH_SERVICE);
            assert bluetoothManager != null;
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
                return true;
            } else {
                Log.e(TAG, "Unable to enable bluetooth. Device might not have bluetooth support");
                return false;
            }
        }
        return false;
    }

    /** Used as a snackbar just to display text */
    public static void displayCustomToast(Activity activity, String toastTxt){
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout_root));
        TextView text = layout.findViewById(R.id.text);
        text.setText(toastTxt);
        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.BOTTOM| Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static String getUuidFromByteArray(byte[] bytes) {
        if (bytes != null) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            UUID uuid = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
            String retVal = uuid.toString();
            Log.d(TAG, "getUuidFromByteArray : serial ID " + retVal);
            return retVal;
        } else {
            Log.d(TAG, "getUuidFromByteArray : serial ID  is null");
            return null;
        }
    }

    public static boolean isAssociatedFpModel(FPModel fpModel, String serialID) {
        if (fpModel != null) {
            if (fpModel.getUniqueIdentifier() != null) {
                String fpModelSerialID = Utils.byteArrayToHexWithNoSpaces(fpModel.getUniqueIdentifier());
                Log.d(TAG, "isAssociatedFpModel: serialID of Current FP Model : " + fpModelSerialID);
                Log.d(TAG, "isAssociatedFpModel: serialID  : " + serialID);
                return fpModelSerialID.equalsIgnoreCase(serialID);
            } else {
                Log.d(TAG, "isAssociatedFpModel: fpModel.getUniqueIdentifier()  is null ");
                return false;
            }
        }
        return false;
    }

    public static Boolean isCallPermissionGranted(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static String getString(int resID) {
        return BaseApplication.instance().getResources().getString(resID);
    }

    public static String getCurrentLanguage() {
        return Locale.getDefault().getDisplayLanguage();
    }

    public static int getMinRequiredVersion(FPBLEConstants.CONNECT_PERIPHERAL_TYPE connectPeripheralType) {
        switch (connectPeripheralType) {
            case SLEEPER:
                return SLEEPER_MIN_VERSION;
            case DELUXE_SLEEPER:
                return DELUXE_SLEEPER_MIN_VERSION;
            case SEAHORSE:
                return SEAHORSE_MIN_VERSION;
            case LAMP_SOOTHER:
                return SOOTHER_MIN_VERSION;
        }
        return 0;
    }
}
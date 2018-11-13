/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatDrawableManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.protobuf.ByteString;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.sproutling.App;
import com.sproutling.BuildConfig;
import com.sproutling.R;
import com.sproutling.api.SproutlingApi;
import com.sproutling.object.MixpanelUserProfileData;
import com.sproutling.object.PushNotificationRegistrationEvent;
import com.sproutling.pojos.CreateHandheldRequestBody;
import com.sproutling.pojos.CreateHandheldResponse;
import com.sproutling.pojos.UpdateHandheldRequestBody;
import com.sproutling.pojos.UpdateHandheldResponse;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.LoggerService;
import com.sproutling.services.SSManagement;
import com.sproutling.states.States;
import com.sproutling.ui.activity.MainActivity;
import com.sproutling.ui.fragment.status.DrawerLayoutFragment;
import com.wx.wheelview.widget.WheelView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sproutling.object.MixpanelUserProfileData.BABY_AGE_IN_MONTHS;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by bradylin on 11/22/16.
 */

public class Utils {

    public static final String TAG = Utils.class.getSimpleName();
    public static final String SHARED_PREFERENCES_FILE = "account";
    public static final String SHARED_PREFERENCES_ALERT = "alert";
    public static final String SLEEPING = "sleeping";
    public static final String AWAKE = "awake";
    public static final String STIRRING = "stirring";
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public final static int ONE_MINUTE_MILLISECONDS = 60000;
    public static final int HALF_SECOND_MILLS = 500;
    public static final int ONE_MINUTE_SECONDS = 60;
    public static final long ONE_SECOND_MILLIS = 1000;
    private static int MAX_BATTERY_VOLTAGE = 4200; //4.2V
    private static int MIN_BATTERY_VOLTAGE = 3000; // 3V
    private static MixpanelAPI sMixpanel = MixpanelAPI.getInstance(App.getInstance(), BuildConfig.MIX_PANEL_API_KEY);

    public static boolean isActivityActive(Activity activity) {
        return !activity.isFinishing() && !activity.isDestroyed();
    }

    public static String getChildPhotoFileName() {
        return "IMG_profile" + ".jpg";
    }

    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
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

    public static Bitmap rotateImageIfRequired(Bitmap img, String uriPath) throws IOException {
        ExifInterface ei = new ExifInterface(uriPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
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

    /**
     * @param ctx Pass the activity context here
     * @return
     */
    public static boolean checkPlayServices(Activity ctx) {
        GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
        int resultCode = gApi.isGooglePlayServicesAvailable(ctx);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (gApi.isUserResolvableError(resultCode)) {
                gApi.getErrorDialog(ctx, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                showAlertDialog(ctx, "Error", " Please install Google Play Services and restart this app", ctx.getString(R.string.okay), null);
                Toast.makeText(ctx, "This device is not supported", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    public static void showAlertDialog(Activity context,
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

    public static boolean isLocationPermissionGranted(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }

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

    public static boolean isPhoneValid(String number) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            // TODO: fixed for US temporarily
            String country = Locale.getDefault().getCountry();
//            PhoneNumber phoneNumber = phoneUtil.parse(number, country);
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, "US");
            return phoneUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String formatPhone(String number, boolean isNationalFormat) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            // TODO: fixed for US temporarily
            String country = Locale.getDefault().getCountry();
//            PhoneNumber phoneNumber = phoneUtil.parse(number, country);
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, "US");
            if (isNationalFormat)
                return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            else
                return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return number;
    }

    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    public static String getVersion(ByteString bytes) {
        int length = bytes.size();
        StringBuilder version = new StringBuilder();
        for (int i = 0; i < length; i++) {
            Byte byteVal = bytes.byteAt(i);
            version.append(byteVal.intValue());
            if (i < length - 1) {
                version.append(".");
            }
        }
        return version.toString();
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

    public static String getBleStrength(int bleValue) {
        Context ctx = App.getAppContext();
        String retVal;
        //range is [0,-127] this value is divided into 3 segments to get the strength
        if (0 <= bleValue && bleValue <= -42) {
            retVal = ctx.getString(R.string.settings_device_settings_signal_level_strong);
        } else if ((-43 <= bleValue && bleValue <= -84)) {
            retVal = ctx.getString(R.string.settings_device_settings_signal_level_okay);
        } else if (-85 <= bleValue && bleValue <= -126) {
            retVal = ctx.getString(R.string.settings_device_settings_signal_level_weak);
        } else {
            retVal = ctx.getString(R.string.settings_device_settings_signal_level_strong);
        }
        return retVal;
    }

    public static String getWifiStrength(int wifiStrength) {
        Context ctx = App.getAppContext();
        String retVal;
        //range is [0,-120] this value is divided into 3 segments to get the strength
        if (wifiStrength <= 0 && -40 <= wifiStrength) {
            retVal = ctx.getString(R.string.settings_device_settings_signal_level_strong);
        } else if ((wifiStrength <= -41 && -80 <= wifiStrength)) {
            retVal = ctx.getString(R.string.settings_device_settings_signal_level_okay);
        } else if (wifiStrength <= -81 && -120 <= wifiStrength) {
            retVal = ctx.getString(R.string.settings_device_settings_signal_level_weak);
        } else {
            retVal = ctx.getString(R.string.settings_device_settings_signal_level_strong);
        }
        return retVal;
    }

    public static Drawable getWifiStrengthImgResource(int wifStrength) {
        Context ctx = App.getAppContext();
        String wifiStrength = getWifiStrength(wifStrength);
        int retVal;
        if (wifiStrength.equalsIgnoreCase(ctx.getString(R.string.settings_device_settings_signal_level_strong))) {
            retVal = R.drawable.ic_wifi_signal_strong;
        } else if (wifiStrength.equalsIgnoreCase(ctx.getString(R.string.settings_device_settings_signal_level_okay))) {
            retVal = R.drawable.ic_wifi_signal_okay;
        } else if (wifiStrength.equalsIgnoreCase(ctx.getString(R.string.settings_device_settings_signal_level_weak))) {
            retVal = R.drawable.ic_wifi_signal_weak;
        } else {
            retVal = R.drawable.ic_wifi_signal_strong;
        }
        return ContextCompat.getDrawable(ctx, retVal);
    }

    public static Drawable getBleStrengthImgResource(int wifStrength) {
        Context ctx = App.getAppContext();
        String wifiStrength = getBleStrength(wifStrength);
        int retVal;
        if (wifiStrength.equalsIgnoreCase(ctx.getString(R.string.settings_device_settings_signal_level_strong))) {
            retVal = R.drawable.ic_bluetooth_signal_strong;
        } else if (wifiStrength.equalsIgnoreCase(ctx.getString(R.string.settings_device_settings_signal_level_okay))) {
            retVal = R.drawable.ic_bluetooth_signal_okay;
        } else if (wifiStrength.equalsIgnoreCase(ctx.getString(R.string.settings_device_settings_signal_level_weak))) {
            retVal = R.drawable.ic_bluetooth_signal_weak;
        } else {
            retVal = R.drawable.ic_bluetooth_signal_strong;
        }
        return ContextCompat.getDrawable(ctx, retVal);
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

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return App.getInstance().getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return App.getInstance().getResources().getConfiguration().locale;
        }
    }

    public static void sendKibanaLogs(String log) {
        LoggerService.logMessage(App.getInstance(), "Android : " + log);
    }

    public static void logEvents(@LogEvents.Events String event, JSONObject jsonObject) {
        sMixpanel.track(event, jsonObject);
    }

    public static void setMixpanelUserProfile() {
        AccountManagement accountManagement = AccountManagement.getInstance(App.getInstance());
        SSManagement.UserAccountInfo userAccountInfo = accountManagement.getUserAccountInfo();
        if (userAccountInfo != null) {
            MixpanelUserProfileData mixpanelUserProfileData = new MixpanelUserProfileData();
            mixpanelUserProfileData.setEmail(userAccountInfo.email);
            mixpanelUserProfileData.setFirstName(userAccountInfo.firstName);
            mixpanelUserProfileData.setLastName(userAccountInfo.lastName);
            mixpanelUserProfileData.setJoinedDate(userAccountInfo.createdAt);
            sMixpanel.getPeople().identify(userAccountInfo.email);
            try {
                JSONObject data = new JSONObject(toJsonString(mixpanelUserProfileData));
                sMixpanel.getPeople().set(data);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
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

    public static void setBabyAgeInMonthsMixpanel(Integer babyAgeInMonths) {
        if (babyAgeInMonths != null) {
            AccountManagement accountManagement = AccountManagement.getInstance(App.getInstance());
            SSManagement.UserAccountInfo userAccountInfo = accountManagement.getUserAccountInfo();
            sMixpanel.getPeople().identify(userAccountInfo.email);
            sMixpanel.getPeople().set(BABY_AGE_IN_MONTHS, babyAgeInMonths);
        }
    }

    public static boolean isMixpanelUserProfileAvailable() {
        AccountManagement accountManagement = AccountManagement.getInstance(App.getInstance());
        SSManagement.UserAccountInfo userAccountInfo = accountManagement.getUserAccountInfo();
        if (userAccountInfo != null) {
            if (TextUtils.isEmpty(sMixpanel.getPeople().getDistinctId())) {
                return false;
            } else
                return true;
        }
        return false;
    }

    public static void logEvents(@LogEvents.Events String event) {
        logEvents(event, null);
    }

    public static void sendLogEvents() {
        sMixpanel.flush();
    }

    public static void sendNotification(Context context, String title, String message) {
        int notifyID = 1;
        int largeIconId;
        switch (title) {
            case NotificationTopic.ROLL_OVER:
                largeIconId = R.drawable.ic_notification_rollover;
                break;
            case NotificationTopic.HEART_RATE_ALERT:
                largeIconId = R.drawable.ic_notification_heart_rate;
                break;
            case NotificationTopic.SLEEP:
                largeIconId = R.drawable.ic_notification_asleep;
                break;
            case NotificationTopic.WAKE:
                largeIconId = R.drawable.ic_notification_awake;
                break;
            case NotificationTopic.STIR:
                largeIconId = R.drawable.ic_notification_stirring;
                break;
            case NotificationTopic.WEARABLE_FELL_OFF:
                largeIconId = R.drawable.ic_notification_wearable_fell_off;
                break;
            case NotificationTopic.OFFLINE:
                largeIconId = R.drawable.ic_notification_hub_offline;
                break;
            case NotificationTopic.CANT_FIND_WEARABLE:
                largeIconId = R.drawable.ic_notification_cant_find_wearable;
                break;
            case NotificationTopic.BLE_DISCONNECT:
                largeIconId = R.drawable.ic_notification_ble_disconnect;
                break;
            case NotificationTopic.BATTERY_LOW:
                largeIconId = R.drawable.ic_notification_wearable_low_battery;
                break;
            case NotificationTopic.OUT_OF_BATTERY:
                largeIconId = R.drawable.ic_notification_wearable_out_of_battery;
                break;
            default:
                largeIconId = R.mipmap.ic_launcher;
                break;
        }
        Intent myIntent = new Intent(context, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                myIntent,
                PendingIntent.FLAG_ONE_SHOT);
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.rollover_dialog);
        Bitmap bitmap = getBitmapFromVectorDrawable(context, largeIconId);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_push_notif_status_bar)
                        .setColor(Utils.getColor(context, R.color.colorAccent))
                        .setLargeIcon(bitmap)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentTitle(toCamelCase(title))
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(pendingIntent);


        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = builder.build();
        mNotificationManager.notify(notifyID, notification);
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

    private static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
//        Drawable drawable = VectorDrawableCompat.create(context.getResources(), drawableId, null);
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void sendRegistrationToServer(final Context context, String token) {
        String previousPushNotificationToken = SharedPrefManager.getPushNotificationTokenId(context);
        if (TextUtils.isEmpty(previousPushNotificationToken)) {
            Log.d(TAG, "Creating Handheld..");
            CreateHandheldRequestBody createHandheldRequestBody = new CreateHandheldRequestBody(SharedPrefManager.getUniqueIdentifier(context),
                    token, Utils.getCurrentLocale(), App.getAppPackage());
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
            }, AccountManagement.getInstance(context).getAccessToken());
        } else if (!previousPushNotificationToken.equalsIgnoreCase(token)) {
            Log.d(TAG, "Updating Handheld..");
            UpdateHandheldRequestBody updateHandheldRequestBody = new UpdateHandheldRequestBody(SharedPrefManager.getUniqueIdentifier(context), token,
                    Utils.getCurrentLocale(), App.getAppPackage());
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
            }, AccountManagement.getInstance(context).getAccessToken());
        } else {
            Log.d(TAG, "Same Push Notification Token. Ignoring the api call to create or update..");
            //Don't send the push notification token if it hasn't refreshed. Just send the event that push notification registration is success, so that the app will proceed.
            EventBus.getDefault().post(new PushNotificationRegistrationEvent(true, null, null));
        }
    }

    private static void saveHandheldResponse(Context context, CreateHandheldResponse createHandheldResponse) {
        if (createHandheldResponse != null) {
            SharedPrefManager.saveHandHeldId(context, createHandheldResponse.getId());
            SharedPrefManager.savePushNotificationTokenId(context, createHandheldResponse.getToken());
        }
    }

    public static int getAppVolumeFromHubVolume(int musicVolume) {
        return (int) Math.floor((musicVolume - DrawerLayoutFragment.MIN_VOLUME) / ((DrawerLayoutFragment.MAX_VOLUME - DrawerLayoutFragment.MIN_VOLUME) / 100f));
    }

    public static int getHubVolumeFromAppVolume(int musicVolume) {
        return (int) Math.ceil(musicVolume * (DrawerLayoutFragment.MAX_VOLUME - DrawerLayoutFragment.MIN_VOLUME) / 100 + 40); //Trans "0 ~ 100" to "40 ~ 75"
    }

    public static int getAppBrightnessFromHubBrightness(int ledBrightness) {
        return (int) Math.floor((ledBrightness - DrawerLayoutFragment.MIN_BRIGHTNESS) / ((DrawerLayoutFragment.MAX_BRIGHTNESS - DrawerLayoutFragment.MIN_BRIGHTNESS) / 100));
    }

    public static int getHubBrightnessFromAppBrightness(int brightness) {
        Log.d(TAG, "Input Brightness : " + brightness);
        float retVal = brightness * (DrawerLayoutFragment.MAX_BRIGHTNESS - DrawerLayoutFragment.MIN_BRIGHTNESS) / 100 + 1000; //Trans "0 ~ 100" to "1000 ~ 65535"
        Log.d(TAG, "Output converted Brightness : " + retVal);
        return (int) Math.ceil(retVal);
    }


    public static String getPlaylistName(ArrayList<String> songs) {
        String retVal = getString(R.string.custom_playlist);
        String[] classicalPlaylist = App.getInstance().getResources().getStringArray(R.array.classical_playlist_items);
        String[] lullabyPlaylist = App.getInstance().getResources().getStringArray(R.array.lullabies_playlist_items);
        String[] ambientPlaylist = App.getInstance().getResources().getStringArray(R.array.ambient_playlist_items);
        boolean isNotClassical = false;
        boolean isNotAmbient = false;
        for (String song : ambientPlaylist) {
            if (!songs.contains(Utils.getHubSongName(song))) {
                retVal = getString(R.string.custom_playlist);
                isNotAmbient = true;
                break;
            }
            retVal = getString(R.string.ambient_playlist);
        }
        if (isNotAmbient) {
            for (String song : classicalPlaylist) {
                if (!songs.contains(Utils.getHubSongName(song))) {
                    retVal = getString(R.string.custom_playlist);
                    isNotClassical = true;
                    break;
                }
                retVal = getString(R.string.classical_playlist);
            }
            if (isNotClassical) {
                for (String song : lullabyPlaylist) {
                    if (!songs.contains(Utils.getHubSongName(song))) {
                        retVal = getString(R.string.custom_playlist);
                        break;
                    }
                    retVal = getString(R.string.lullabies_playlist);
                }
            }
        }

        return retVal;
    }


    public static WheelView.WheelViewStyle getWheelViewStyle(Context context) {
        WheelView.WheelViewStyle wheelViewStyle = new WheelView.WheelViewStyle();
        wheelViewStyle.selectedTextZoom = 1.2f;
        wheelViewStyle.holoBorderColor = Utils.getColor(context, R.color.divider);
        return wheelViewStyle;
    }

    public static String getFormattedStatus(String status) {
        if (status.equalsIgnoreCase(getString(R.string.asleep))) {
            return SLEEPING;
        } else if (status.equalsIgnoreCase(getString(R.string.stirring))) {
            return STIRRING;
        } else if (status.equalsIgnoreCase(getString(R.string.awake))) {
            return AWAKE;
        }
        return null;
    }

    public static long getTimerValueInMillis(int minutes) {
        Calendar calendar = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.MILLISECOND, (minutes * ONE_MINUTE_MILLISECONDS));
        long timerValueinMillis = calendar.getTimeInMillis();
        return timerValueinMillis;
    }

    public static int getTimerValueInMinutes(long  timerMillisUTC) {
        Calendar calendar = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
        long currentMillisUTC = calendar.getTimeInMillis();

        if(timerMillisUTC>currentMillisUTC){
            long pendingTimerMillisValue = timerMillisUTC-currentMillisUTC; //convert pendingTimerMillisValue to Minutes
            int toMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(pendingTimerMillisValue);
            return toMinutes;
        }

        return -1;
    }

    public static int minuteToSeconds(int minutes) {
        return minutes * ONE_MINUTE_SECONDS;
    }

    public static String getTimerDisplayText(Context context, long millisUtc) {
        if (isTimerInFuture(millisUtc)) {
            Calendar calendar = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
            //adding 1 min to get the correct minutes values when it is set in UI
            long diffMillisUtc = (millisUtc + ONE_MINUTE_MILLISECONDS) - calendar.getTimeInMillis();
            long hours = diffMillisUtc / (60 * ONE_MINUTE_MILLISECONDS);
            long mins = (diffMillisUtc % (60 * ONE_MINUTE_MILLISECONDS)) / ONE_MINUTE_MILLISECONDS;
            if (hours > 0)
                return context.getString(R.string.timer_display_h_m_text, hours, mins);
            else
                return context.getString(R.string.timer_display_m_text, mins);
        } else {
            return context.getString(R.string.timer_off);
        }
    }

    public static boolean isTimerInFuture(long millisUtc) {
        Calendar calendar = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
        return millisUtc > calendar.getTimeInMillis();
    }


    public static String getStatus(String status) {
        if (status.equalsIgnoreCase(getString(R.string.asleep))) {
            return getString(R.string.asleep);
        } else if (status.equalsIgnoreCase(getString(R.string.stirring))) {
            return getString(R.string.stirring);
        } else if (status.equalsIgnoreCase(getString(R.string.awake))) {
            return getString(R.string.awake);
        }
        return null;
    }

    public static String getString(int resID) {
        return App.getInstance().getResources().getString(resID);
    }

    public static String getHubSongName(String displaySongName) {
        if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_air_on_g_string))) {
            return (States.SongValue.AIR_ON_G_STRING);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_ambient_a))) {
            return (States.SongValue.AMBIENT_A);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_ambient_b))) {
            return (States.SongValue.AMBIENT_B);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_ambient_c))) {
            return (States.SongValue.AMBIENT_C);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_ambient_d))) {
            return (States.SongValue.AMBIENT_D);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_ambient_e))) {
            return (States.SongValue.AMBIENT_E);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_are_you_sleeping))) {
            return (States.SongValue.ARE_YOU_SLEEPING);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_beautiful_dreamer))) {
            return (States.SongValue.BEAUTIFUL_DREAMER);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_brahms_lullaby))) {
            return (States.SongValue.BRAHMS_LULLABY);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_clair_de_lune))) {
            return (States.SongValue.CLAIR_DR_LUNE);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_dance_of_the_spirits))) {
            return (States.SongValue.DANCE_OF_THE_SPIRITS);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_hush_little_baby))) {
            return (States.SongValue.HUSH_LITTLE_BABY);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_liebestraum))) {
            return (States.SongValue.LIEBESTRAUM);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_schuberts_lullaby))) {
            return (States.SongValue.SCHUBERTS_LULLABY);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_twinkle_twinkle_little_star))) {
            return (States.SongValue.TWINKLE_TWINKLE_LITTLE_STAR);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_brown))) {
            return (States.SongValue.BROWN);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_ocean))) {
            return (States.SongValue.OCEAN);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_pink))) {
            return (States.SongValue.PINK);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_rain))) {
            return (States.SongValue.RAIN);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_thunder))) {
            return (States.SongValue.THUNDER);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_white))) {
            return (States.SongValue.WHITE);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_heart_slow))) {
            return (States.SongValue.HEART_SLOW);
        } else if (displaySongName.equalsIgnoreCase(getString(R.string.hsl_heart_fast))) {
            return (States.SongValue.HEART_FAST);
        } else {
            //default to first song
            return (States.SongValue.AIR_ON_G_STRING);
        }
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

    public static String getDisplayMetrics(Context context){

        String resolutionString;
        switch (context.getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                resolutionString = displayMetrics.LDPI;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                resolutionString = displayMetrics.MDPI;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                resolutionString = displayMetrics.HDPI;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                resolutionString = displayMetrics.XHDPI;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                resolutionString = displayMetrics.XXDPI;
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                resolutionString = displayMetrics.XXXHDPI;
                break;
            case DisplayMetrics.DENSITY_260:
                resolutionString = displayMetrics.SW260;
                break;
            case DisplayMetrics.DENSITY_280:
                resolutionString = displayMetrics.SW280;
                break;
            case DisplayMetrics.DENSITY_300:
                resolutionString = displayMetrics.SW300;
                break;
            case DisplayMetrics.DENSITY_340:
                resolutionString = displayMetrics.SW340;
                break;
            case DisplayMetrics.DENSITY_360:
                resolutionString = displayMetrics.SW360;
                break;
            case DisplayMetrics.DENSITY_400:
                resolutionString = displayMetrics.SW400;
                break;
            case DisplayMetrics.DENSITY_420:
                resolutionString = displayMetrics.SW420;
                break;
            case DisplayMetrics.DENSITY_560:
                resolutionString = displayMetrics.SW560;
                break;
            default:
                resolutionString = "Invalid Resolution";
        }
        Log.d(TAG, "Device Resolution: "+resolutionString);
        return resolutionString;
    }

    public interface displayMetrics {
        String LDPI = "LDPI";
        String MDPI = "MDPI";
        String HDPI = "HDPI";
        String XHDPI = "XHDPI";
        String XXDPI = "XXHDPI";
        String XXXHDPI = "XXXHPI";

        String SW260 = "SW260";
        String SW280 = "SW280";
        String SW300= "SW300";
        String SW340= "SW340";
        String SW360= "SW360";
        String SW400= "SW400";
        String SW420= "SW420";
        String SW560 = "SW560";
    }

}
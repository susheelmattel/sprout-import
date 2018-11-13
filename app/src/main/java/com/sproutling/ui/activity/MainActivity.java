/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.helpshift.Core;
import com.sproutling.App;
import com.sproutling.BuildConfig;
import com.sproutling.R;
import com.sproutling.api.SproutlingApi;
import com.sproutling.pojos.CreateHandheldResponse;
import com.sproutling.pojos.ProductSettingsResponseBody;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.services.SSServerException;
import com.sproutling.ui.dialogfragment.GenericErrorDialogFragment;
import com.sproutling.ui.dialogfragment.PostEOLDialogFragment;
import com.sproutling.ui.dialogfragment.SoftwareUpdateDialogFragment;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sproutling.App.FISHER_PRICE_CHINA_FLAVOR;

public class MainActivity extends BaseActivity implements SoftwareUpdateDialogFragment.OnSoftwareUpdateListener,
        PostEOLDialogFragment.OnLearnMoreListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_CODE_QR = 1;
    private static final int SPLASH_DURATION = 2000;
    private static final int MAX_ATTEMPT = 3;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };
    View.OnClickListener mOnLogInButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //we are unregistering here because, when we setup pushnotification from Loginscreen, we donot want this class to listen to it
            unRegisterEventBus();
            Utils.logEvents(LogEvents.SIGN_IN);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener mOnSignUpButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Utils.logEvents(LogEvents.SIGN_UP);
            showSignUpOptions();

//            test();
        }
    };
    private ImageView mSplashView, mImgBackground;
    private Handler mHandler;
    private long mStartTime;
    private int mAttempt = MAX_ATTEMPT;
    private ShTextView mLoginBtn;
    private Button mSignUpBtn;
    private boolean isVisible;
    private ArrayList<Integer> mAnimationImageList = new ArrayList<>();
    private int mImageCounter = 0;
    private AlphaAnimation mAlphaAnimation = new AlphaAnimation(1f, 0.7f);
    private Animation mZoomOutAnimation;
    Runnable mDismissRunnable = new Runnable() {
        @Override
        public void run() {
            mSplashView.setVisibility(View.INVISIBLE);
            enableButtons(true);
            startZoomOutAnimation();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mZoomOutAnimation = AnimationUtils.loadAnimation(this, R.anim.image_zoom_out);
        mHandler = new Handler();

        mSplashView = (ImageView) findViewById(R.id.splash);
        mImgBackground = (ImageView) findViewById(R.id.img_family);
        mLoginBtn = (ShTextView) findViewById(R.id.tv_sign_in);
        mLoginBtn.setOnClickListener(mOnLogInButtonClickListener);
        mSignUpBtn = (Button) findViewById(R.id.sign_up);
        mSignUpBtn.setOnClickListener(mOnSignUpButtonClickListener);
        enableButtons(false);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mAnimationImageList.add(R.drawable.img_welcome_bg_1);
        mAnimationImageList.add(R.drawable.img_welcome_bg_2);
        mAnimationImageList.add(R.drawable.img_welcome_bg_3);
        mAlphaAnimation.setDuration(1000);

        mImgBackground.setAnimation(mZoomOutAnimation);
        mImgBackground.setImageResource(getNextDrawable());

        mStartTime = System.currentTimeMillis();
        checkUpdate();
    }

    private void test() {
        SproutlingApi.getProductSettings(new Callback<ProductSettingsResponseBody>() {
            @Override
            public void onResponse(Call<ProductSettingsResponseBody> call, Response<ProductSettingsResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Received Products Settings");
                    ProductSettingsResponseBody productSettingsResponseBody = response.body();
                }
            }

            @Override
            public void onFailure(Call<ProductSettingsResponseBody> call, Throwable t) {
                Log.d(TAG, t.getLocalizedMessage());
            }
        }, "862f4a09-4cf1-4be2-96d8-e79fd3e4377f", "eyJhbGciOiJSUzI1NiIsImtpZCI6InYwLjAuMSIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiI5M2U2YTQ5MC02ZDVlLTQzMzEtODgxMS01MjQ3ZDBjY2MxNTUiLCJhaWQiOiI3MTljNjdjOS1mMTQwLTRlMjktYTNjYS1lYWYyY2MyM2ZhNzAiLCJlbWFpbCI6InBvaUBwb2kucG9pIiwiYXBwaWQiOiI2MmI5Y2Y2ODhmZjFhNTZhMzI4ZTdkNzk2ZmQzZGI0MzdiZTNhNWVjY2RhOWVhODNkMTc3NjA5Y2NmOTQ2NTg3IiwiZXhwIjoxNTIxNzYxOTU0LCJqdGkiOiIyYTY0MzE3NC02ZDljLTQyYmQtYWRiYi1lYWNhOWJjMDFkMmYiLCJpYXQiOjE1MTkxNjk5NTQsImlzcyI6IlNwcm91dGxpbmciLCJzdWIiOiJBY2Nlc3MgVG9rZW4ifQ.dN-swMGn5br5hmqmAPYoX3DlNuEGGAqWnw21LubEY_9SwLElMCbtl-FFrPzVHQX3BnsEsERUOIIaWcmd8YK3IDZ8I1UEm-o6tJ7dJIdmzevxyg4gdBNslLYUtYeHEp6kQfhmOyEV_RfoZELScDmwLOeZdMmPP2RqFSv8CAZjrN4");
    }

    private void enableButtons(boolean enable) {
        mSignUpBtn.setEnabled(enable);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_QR:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(this, SetupActivity.class);
                    intent.putExtras(data.getExtras());
                    startActivity(intent);
                }
                break;
            default:
        }
    }

    private void showSignUpOptions() {
        startActivity(new Intent(MainActivity.this, SproutlingVideoActivity.class));
    }

    private void startZoomOutAnimation() {
        mZoomOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG, "onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startAlphaAnimation();
                Log.d(TAG, "onAnimationEnd");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d(TAG, "onAnimationRepeat");
            }
        });

        mImgBackground.startAnimation(mZoomOutAnimation);
    }

    private void startAlphaAnimation() {
        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mImgBackground.setImageResource(getNextDrawable());
                mImgBackground.setAlpha(1f);
                startZoomOutAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mImgBackground.startAnimation(mAlphaAnimation);
    }


    private int getNextDrawable() {
        int size = mAnimationImageList.size();
        if (mImageCounter == Integer.MAX_VALUE) {
            mImageCounter = 0;
        }
        if (mImageCounter < size) {
            return mAnimationImageList.get(mImageCounter++);
        } else {
            Log.d(TAG, "Animation imageCounter : " + String.valueOf(mImageCounter));
            int index = mImageCounter++ % size;
            Log.d(TAG, "Animation image index : " + String.valueOf(index));
            return mAnimationImageList.get(index);
        }

    }

    private void transitionToActivity(final Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION - (System.currentTimeMillis() - mStartTime));
    }

    boolean retry(SSException exception) {
        if (exception.getResponseCode() == 401) {
            if (--mAttempt != 0) {
                return true;
            } else {
                mAttempt = MAX_ATTEMPT;
                return false;
            }
        }
        return false;
    }

    void checkUpdate() {
        new AsyncTask<Object, Object, SSManagement.UpdateInfo>() {
            private SSException mException;

            @Override
            protected SSManagement.UpdateInfo doInBackground(Object... params) {
                try {
                    return SKManagement.getUpdateInfo();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mException = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(final SSManagement.UpdateInfo updateInfo) {
                if (updateInfo != null) {
                    try {
                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        if (pInfo.versionCode < updateInfo.androidInfo.minRequiredVersion && !isFinishing() && isVisible) {
                            SoftwareUpdateDialogFragment.newInstance(updateInfo.androidInfo.updateUrl).show(getFragmentManager(), null);
                            // TODO: need to clear?
//                            AccountManagement.getInstance(MainActivity.this).clear();
                        } else {
                            getEOLData();
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (mException != null && (mException.getResponseCode() == 404 || mException.getResponseCode() == 400)) {
                    mHandler.postDelayed(mDismissRunnable, SPLASH_DURATION);
                    // TODO: do something else?
                } else {
                    mHandler.postDelayed(mDismissRunnable, SPLASH_DURATION);
                }
            }
        }.execute();
    }

    private void openUpdateUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
        finish();
    }

    private void initAccount() {
        SSManagement.EOLData eolData = AccountManagement.getInstance(MainActivity.this).readEOLData();

        if(eolData != null && !eolData.isEOL.isEmpty()){
            if(eolData.isEOL.equals("true")){
                PostEOLDialogFragment.newInstance(eolData.settingsPopUpSupportWebsite, getString(R.string.dialog_important_notice),
                        eolData.postEOLPopUpText,getString(R.string.dialog_eol_learn_more)).show(getFragmentManager(), null);
            } else{
                afterEOLcheck();
            }
        } else{
            afterEOLcheck();
        }
    }

    private void afterEOLcheck(){
        String accessToken = AccountManagement.getInstance(this).getAccessToken();
        if (accessToken != null) {
            checkToken(accessToken);
        } else {
            mHandler.postDelayed(mDismissRunnable, SPLASH_DURATION);
        }
    }

    void getEOLData() {
        new AsyncTask<Object, Object, SSManagement.EOLData>() {
            private SSException mException;

            @Override
            protected SSManagement.EOLData doInBackground(Object... params) {
                try {
                    return SKManagement.getEOLData();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mException = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(final SSManagement.EOLData eolData) {
                if (eolData != null) {
                    AccountManagement.getInstance(MainActivity.this).setEOLData(eolData);
                }
                initAccount();

            }
        }.execute();
    }

    private void setup() {
        if (!BuildConfig.FLAVOR_app.equals(FISHER_PRICE_CHINA_FLAVOR)) {
            SSManagement.UserAccountInfo userAccountInfo = AccountManagement.getInstance(this).getUserAccountInfo();
            Core.login(userAccountInfo.email, userAccountInfo.firstName, userAccountInfo.email);
        }
        setUpMixPanelUserProfile();
        setUpPushNotification();
    }

    private void setUpMixPanelUserProfile() {
        if (!Utils.isMixpanelUserProfileAvailable()) {
            Utils.setMixpanelUserProfile();
        }
    }

    private void checkToken(final String accessToken) {
        new AsyncTask<Void, Void, SSManagement.User>() {
            private SSException mException;

            @Override
            protected SSManagement.User doInBackground(Void... params) {
                try {
                    return SKManagement.getTokenInfo(accessToken);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mException = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(final SSManagement.User user) {
                if (user != null) {
                    AccountManagement.getInstance(MainActivity.this).writeToPreferences(user);
                    App.getInstance().initializeMqtt();
                    setup();
                } else if (mException != null && mException instanceof SSServerException) {
                    mHandler.postDelayed(mDismissRunnable, SPLASH_DURATION);
                    handleError(mException.getError());
                } else {
//                    AccountManagement.getInstance(MainActivity.this).clear();
//                    SharedPrefManager.clear(MainActivity.this);
                    mHandler.postDelayed(mDismissRunnable, SPLASH_DURATION);
                }
            }
        }.execute();
    }

    public void refreshToken(final String accessToken, final String refreshToken) {
        new AsyncTask<Void, Void, SSManagement.User>() {
            private SSException mException;

            @Override
            protected SSManagement.User doInBackground(Void... params) {
                try {
                    return SKManagement.refreshToken(accessToken, refreshToken);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mException = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(final SSManagement.User user) {
                if (user != null) {
                    AccountManagement.getInstance(MainActivity.this).writeToPreferences(user);
                }
            }
        }.execute();
    }

    void checkChildSetup() {
        final String accessToken = AccountManagement.getInstance(this).getAccessToken();
        new AsyncTask<Void, Void, SSManagement.Child>() {
            private SSException mException;

            private SSManagement.Child getChild() throws JSONException, SSException, IOException {
                List<SSManagement.Child> children = SKManagement.listChildren(accessToken);
                if (children.isEmpty()) return null;
                // TODO: only checking for 1 child for now
                return children.get(0);
            }

            @Override
            protected SSManagement.Child doInBackground(Void... voids) {
                try {
                    return getChild();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    mException = e;
                    if (e.getResponseCode() == 401) {
                        refreshToken(accessToken, AccountManagement.getInstance(MainActivity.this).getRefreshToken());
                        try {
                            return getChild();
                        } catch (JSONException | SSException | IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(SSManagement.Child child) {
                if (child != null) {
                    mAttempt = MAX_ATTEMPT;
                    AccountManagement.getInstance(MainActivity.this).writeChild(child);
                    //update child age in month
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        cal.setTime(sdf.parse(child.birthDate));
                        Utils.setBabyAgeInMonthsMixpanel(Utils.getMonthCountDifference(cal));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    checkDeviceSetup(accessToken);
                } else {
                    if (mException != null && retry(mException)) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                checkChildSetup();
                            }
                        }, 1000);
                    } else {
                        transitionToActivity(new Intent(MainActivity.this, SetupActivity.class)
                                .putExtra(SetupActivity.EXTRA_SETUP_CHILD, true));
                    }
                }
            }
        }.execute();
    }

    void checkDeviceSetup(final String accessToken) {
        new AsyncTask<Void, Void, List<SSManagement.DeviceResponse>>() {
            private SSException mException;

            @Override
            protected List<SSManagement.DeviceResponse> doInBackground(Void... params) {
                try {
                    return SKManagement.listDevices(accessToken);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mException = e;
                    if (e.getResponseCode() == 401) {
                        refreshToken(accessToken, AccountManagement.getInstance(MainActivity.this).getRefreshToken());
                        try {
                            return SKManagement.listDevices(accessToken);
                        } catch (JSONException | SSException | IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<SSManagement.DeviceResponse> devices) {
                if (devices != null && !devices.isEmpty()) {
                    SharedPrefManager.saveDevice(MainActivity.this, devices.get(0));
                    transitionToActivity(new Intent(getApplicationContext(), StatusActivity.class));
                } else if (devices != null && devices.isEmpty()) {
                    SharedPrefManager.clearDevice(MainActivity.this);
                    checkEvents(accessToken);
//                    transitionToActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                } else if (mException.getResponseCode() == 404) {
                    // TODO: how to handle this
                    transitionToActivity(new Intent(getApplicationContext(), StatusActivity.class));
                } else if (retry(mException)) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkDeviceSetup(accessToken);
                        }
                    }, 2000);
                } else if (SharedPrefManager.getDevice(MainActivity.this) != null) {
                    transitionToActivity(new Intent(getApplicationContext(), StatusActivity.class));
                } else {
                    transitionToActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                }
            }
        }.execute();
    }

    private void checkEvents(final String accessToken) {
        new AsyncTask<Void, Void, Boolean>() {
            SSError mError;

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    List<SSManagement.SSEvent> events = SKManagement.listEventsByChild(accessToken, AccountManagement.getInstance(MainActivity.this).getChild().id);
                    return events == null || events.isEmpty();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return Boolean.FALSE;
            }

            @Override
            protected void onPostExecute(Boolean empty) {
                if (empty) {
                    Utils.logEvents(LogEvents.TIMELINE_NO_DATA);
                    transitionToActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                } else {
                    transitionToActivity(new Intent(getApplicationContext(), StatusActivity.class));
                }
            }
        }.execute();
    }

    @Override
    protected void onPushNotificationRegistrationSuccess(CreateHandheldResponse createHandheldResponse) {
        super.onPushNotificationRegistrationSuccess(createHandheldResponse);
        if (AccountManagement.getInstance(this).getAccessToken() != null && !isFinishing() && isVisible) {
            checkChildSetup();
        }
    }

    @Override
    protected void onPushNotificationRegistrationFailure(Throwable t) {
        super.onPushNotificationRegistrationFailure(t);
        //TODO:remove this after testing
        if (AccountManagement.getInstance(this).getAccessToken() != null && !isFinishing() && isVisible) {
            checkChildSetup();
        }
    }

    @Override
    public void onUpdateClicked(String url) {
        openUpdateUrl(url);
    }

    void handleError(SSError error) {
        showProgressBar(false);
        GenericErrorDialogFragment.newInstance()
                .setTitle(R.string.server_error_message_title)
                .setMessage(R.string.server_error_message_body)
                .setButtonText(R.string.server_error_message_button)
                .show(getFragmentManager(), null);
    }
}

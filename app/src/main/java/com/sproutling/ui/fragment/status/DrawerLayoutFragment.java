/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.status;

import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.fuhu.states.app.AStateFragment;
import com.fuhu.states.interfaces.IStatePayload;
import com.fuhu.states.payloads.Payload;
import com.sproutling.R;
import com.sproutling.api.SproutlingApi;
import com.sproutling.databinding.FragmentDrawerlayoutBinding;
import com.sproutling.object.CustomPlaylist;
import com.sproutling.object.HubMusicTimerEvent;
import com.sproutling.object.HubNightLightTimerEvent;
import com.sproutling.object.MusicSelectedEvent;
import com.sproutling.object.NightLightColor;
import com.sproutling.pojos.HubSettings;
import com.sproutling.pojos.OtherSettings;
import com.sproutling.pojos.Product;
import com.sproutling.pojos.ProductResponse;
import com.sproutling.pojos.ProductSettings;
import com.sproutling.pojos.ProductSettingsRequestBody;
import com.sproutling.pojos.ProductSettingsResponseBody;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SSManagement;
import com.sproutling.states.Actions;
import com.sproutling.states.States;
import com.sproutling.ui.adapter.MusicSongPagerAdapter;
import com.sproutling.ui.adapter.NightLightColorAdapter;
import com.sproutling.ui.fragment.MusicSongFragmentView;
import com.sproutling.ui.fragment.TimerBottomSheetFragment;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.Utils;
import com.wx.wheelview.widget.WheelView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sproutling.Hub;

/**
 * Created by xylonchen on 2017/4/20.
 */

public class DrawerLayoutFragment extends AStateFragment {

    public static final String TAG = DrawerLayoutFragment.class.getSimpleName();
    public static final float MIN_BRIGHTNESS = 1000;
    public static final float MAX_BRIGHTNESS = 65535;
    public static final float MIN_VOLUME = 40;
    public static final float MAX_VOLUME = 75;
    public static final int FIVE_MINUTES = 5;
    //For recyclerView data...
    private static final String ITEM_TITLE = "Item title";
    private static final String ITEM_ICON = "Item icon";
    private static final String SPROUTLING_WEARABLE_BABY_MONITOR = "Sproutling Wearable Baby Monitor";
    Handler mHandler;
    private FragmentDrawerlayoutBinding mBinding;
    //To record the Choose Color and Music button status in drawerlayout.
    private boolean isChooseColorClicked = false;
    private boolean isChooseMusicClicked = false;
    private boolean isPlayingMusic = false;
    private int mNightLightBrightness;
    private int mMusicVolume;
    private int mColorSelectedNow;
    private ArrayList<String> mMusicSelectedNow;
    private Product mCurrentProduct = null;
    private ProductSettings mServerHubSettings = null;
    private ProductSettings mLocalHubSettings = null;
    private boolean mIsFirstTimeSaveSettings = false;
    private ShTextView mNightLightTimer;
    private RelativeLayout mNightLightLayout;
    private ShTextView mMusicTimer;
    private RelativeLayout mMusicLayout;

    private boolean mIsNightLightTimerOn = false;
    private boolean mIsMusicTimerOn = false;
    private CountDownTimer mNightLightCountDownTimer;
    private CountDownTimer mMusicCountDownTimer;
    private boolean mIgnoreStatusUpdateOnceForMusicTimer = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_drawerlayout, container, false);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler = new Handler();
        loadLocalHubSettings();
        setViewComponent();
        getHubControlSettings();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNightLightTimer = view.findViewById(R.id.nightLightTimerText);
        mMusicTimer = view.findViewById(R.id.musicTimerText);
        mNightLightLayout = view.findViewById(R.id.nightLightTimerLayout);
        mMusicLayout = view.findViewById(R.id.musicTimerLayout);
        mNightLightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNightLightTimer();
            }
        });

        mMusicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMusicTimer();
            }
        });
        mIsNightLightTimerOn = SharedPrefManager.getNightLightTimerValue(getActivity()) != -1 &&
                Utils.isTimerInFuture(SharedPrefManager.getNightLightTimerValue(getActivity()));
        mIsMusicTimerOn = SharedPrefManager.getMusicTimerValue(getActivity()) != -1 &&
                Utils.isTimerInFuture(SharedPrefManager.getMusicTimerValue(getActivity()));
        updateMusicTimerUI();
        updateNightLightTimerUI();
    }

    private void updateMusicTimerUI() {
        if (mIsMusicTimerOn && SharedPrefManager.getMusicTimerValue(getActivity()) != -1 &&
                Utils.isTimerInFuture(SharedPrefManager.getMusicTimerValue(getActivity()))) {
            mMusicLayout.setBackground(getResources().getDrawable(R.drawable.button_bg_rounded_edges_accent));
            mMusicTimer.setTextColor(Utils.getColor(getActivity(), R.color.white));
            mMusicTimer.setText(Utils.getTimerDisplayText(getActivity(), SharedPrefManager.getMusicTimerValue(getActivity())));
        } else {
            mMusicLayout.setBackground(getResources().getDrawable(R.drawable.button_bg_rounded_edges_white));
            mMusicTimer.setTextColor(Utils.getColor(getActivity(), R.color.dark_dolphin));
            mMusicTimer.setText(R.string.timer_off);
        }
    }

    private void updateNightLightTimerUI() {
        if (mIsNightLightTimerOn && SharedPrefManager.getNightLightTimerValue(getActivity()) != -1 &&
                Utils.isTimerInFuture(SharedPrefManager.getNightLightTimerValue(getActivity()))) {
            mNightLightLayout.setBackground(getResources().getDrawable(R.drawable.button_bg_rounded_edges_accent));
            mNightLightTimer.setTextColor(Utils.getColor(getActivity(), R.color.white));
            mNightLightTimer.setText(Utils.getTimerDisplayText(getActivity(), SharedPrefManager.getNightLightTimerValue(getActivity())));
        } else {
            mNightLightLayout.setBackground(getResources().getDrawable(R.drawable.button_bg_rounded_edges_white));
            mNightLightTimer.setTextColor(Utils.getColor(getActivity(), R.color.dark_dolphin));
            mNightLightTimer.setText(R.string.timer_off);
        }
    }

    @Override
    public void onStateChanged(IStatePayload iStatePayload) {
        // Get all data from payload...
        Payload payload = (Payload) iStatePayload;

        Hub.HubStatus hubStatus = (Hub.HubStatus) payload.get(States.Key.MQTT_HUB_STATUS);

        //To update UI by Hub Status (Hub will send status proactively).
        updateUiByHubStatus(hubStatus);

        //To update UI by Hub Control (After user do some action).
//        updateUiByHubControl(mHubControl);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void showNightLightTimer() {
        final TimerBottomSheetFragment timerBottomSheetFragment = TimerBottomSheetFragment.Companion.getInstance(getString(R.string.night_light_timer));
        timerBottomSheetFragment.setMTimerListener(new TimerBottomSheetFragment.TimerListener() {
            @Override
            public void onSelectedClickListener(int minutes) {
                Log.d(TAG, "Selected Night Light Mins : " + minutes);
                if (minutes >= FIVE_MINUTES) {
                    SharedPrefManager.saveNightLightTimerValue(getActivity(), Utils.getTimerValueInMillis(minutes));
                    turnLedOn(true);

                    EventBus.getDefault().post(new HubNightLightTimerEvent(Utils.minuteToSeconds(minutes)));
                    initializeNightLightCountDownTimer(Utils.getTimerValueInMillis(minutes));
                    mIsNightLightTimerOn = true;
                }

                updateNightLightTimerUI();
            }

            @Override
            public void onResetClickListener() {
                mIsNightLightTimerOn = false;
                SharedPrefManager.saveNightLightTimerValue(getActivity(), -1);
                turnLedOn(false);
                updateNightLightTimerUI();
            }
        });
        timerBottomSheetFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "TimerBottomSheetFragment");
    }

    private void turnLedOn(boolean turnOn) {
        int brightnessTrans = Utils.getHubBrightnessFromAppBrightness(mNightLightBrightness);
        Payload mPayload = new Payload();
        mPayload.put(Actions.Key.NIGHT_LIGHT_COLOR, mColorSelectedNow);
        mPayload.put(Actions.Key.NIGHT_LIGHT_LEVEL, brightnessTrans);
        mPayload.put(Actions.Key.IS_NIGHT_LIGHT_ON, turnOn);

        mBinding.progressColor.setVisibility(View.VISIBLE);
        disPatchAction(Actions.SWITCH_NIGHT_LIGHT, mPayload);
    }

    private void initializeNightLightCountDownTimer(long timerValueInMillis) {
        if (mNightLightCountDownTimer != null) {
            mNightLightCountDownTimer.cancel();
        }
        mNightLightCountDownTimer = new CountDownTimer(timerValueInMillis, Utils.ONE_MINUTE_MILLISECONDS) {

            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "mNightLightCountDownTimer :onTick");
                updateNightLightTimerUI();
            }

            public void onFinish() {
                mIsNightLightTimerOn = false;
                Log.d(TAG, "mNightLightCountDownTimer :onFinish");
            }
        }.start();

    }


    private void showMusicTimer() {
        final TimerBottomSheetFragment timerBottomSheetFragment = TimerBottomSheetFragment.Companion.getInstance(getString(R.string.music_timer));
        timerBottomSheetFragment.setMTimerListener(new TimerBottomSheetFragment.TimerListener() {
            @Override
            public void onSelectedClickListener(int minutes) {
                Log.d(TAG, "Selected Music Mins : " + minutes);
                if (minutes >= FIVE_MINUTES) {
                    SharedPrefManager.saveMusicTimerValue(getActivity(), Utils.getTimerValueInMillis(minutes));
                    mIsMusicTimerOn = true;
                    mIgnoreStatusUpdateOnceForMusicTimer = true;
                    playMusic(true);
                    waitAndSetMusicTimer(minutes);
                }
                updateMusicTimerUI();
            }

            @Override
            public void onResetClickListener() {
                mIsMusicTimerOn = false;
                SharedPrefManager.saveMusicTimerValue(getActivity(), -1);
                playMusic(false);
                updateMusicTimerUI();
            }
        });
        timerBottomSheetFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "TimerBottomSheetFragment");
    }

    private void waitAndSetMusicTimer(final int minutes) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SystemClock.sleep(Utils.ONE_SECOND_MILLIS);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                EventBus.getDefault().post(new HubMusicTimerEvent(Utils.minuteToSeconds(minutes)));
                initializeMusicCountDownTimer(Utils.getTimerValueInMillis(minutes));
                mIsMusicTimerOn = true;
                updateMusicTimerUI();
                mIgnoreStatusUpdateOnceForMusicTimer = true;
            }
        }.execute();
    }

    private void playMusic(boolean play) {
        Payload payload = new Payload();
        payload.put(Actions.Key.CONTEXT, getActivity());
        payload.put(Actions.Key.IS_MUSIC_PLAY, play);
        payload.put(Actions.Key.MUSIC_VOL, Utils.getHubVolumeFromAppVolume(mMusicVolume));
        payload.put(Actions.Key.MUSIC_SONG, mMusicSelectedNow);
        mBinding.progressMusic.setVisibility(View.VISIBLE);
        disPatchAction(Actions.SWITCH_MUSIC_PLAY, payload);
    }

    private void initializeMusicCountDownTimer(long timerValueInMillis) {
        if (mMusicCountDownTimer != null) {
            mMusicCountDownTimer.cancel();
        }
        mMusicCountDownTimer = new CountDownTimer(timerValueInMillis, Utils.ONE_MINUTE_MILLISECONDS) {

            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "mMusicCountDownTimer :onTick");
                updateMusicTimerUI();
            }

            public void onFinish() {
                mIsMusicTimerOn = false;
                Log.d(TAG, "mMusicCountDownTimer :onFinish");
            }

        }.start();

    }

    private void showMusicListBottomSheet(int selectedItem) {
        new BottomSheetMusicDialogFragment().show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "Dialog");
    }

    private void showColorListBottomSheet(int selectedItem) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.HubControlBottomDialog);
        final View view = getActivity().getLayoutInflater().inflate(R.layout.night_light_color_layout, null);

        TypedArray colorStringList = getResources().obtainTypedArray(R.array.color_string_list);
        final TypedArray colorIconList = getResources().obtainTypedArray(R.array.color_icon_list);
        ArrayList<NightLightColor> nightLightColors = new ArrayList<>();
        for (int i = 0; i < colorStringList.length(); i++) {
            final String colorTitle = colorStringList.getString(i);
            final int colorResId = colorIconList.getResourceId(i, 0);
            nightLightColors.add(new NightLightColor(colorTitle, colorResId));
        }
        final WheelView colorList = view.findViewById(R.id.colorList);
        colorList.setLoop(false);
        colorList.setSkin(WheelView.Skin.Holo);
        final NightLightColorAdapter nightLightColorAdapter = new NightLightColorAdapter(getActivity());
        colorList.setWheelAdapter(nightLightColorAdapter);
        colorList.setStyle(Utils.getWheelViewStyle(getActivity()));
        colorList.setSelection(selectedItem - 1);
        colorList.setWheelData(nightLightColors);

        Button selectBtn = view.findViewById(R.id.btnSelect);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColorItemClicked(((NightLightColor) colorList.getSelectionItem()).getColor());
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }

    public void updateUiByHubStatus(Hub.HubStatus hubStatus) {
        if (hubStatus != null) {
            //For Nightlight...
            int ledBrightness = hubStatus.getLedBrightness();
            int ledNightLightColor = hubStatus.getLedColorValue();
            int ledModeValue = hubStatus.getLedModeValue();
            int nightLightBrightnessf = Utils.getAppBrightnessFromHubBrightness(ledBrightness);


            if (ledModeValue == Hub.LEDMode.LEDModes.OFF_VALUE ||
                    ledModeValue == Hub.LEDMode.LEDModes.FADE_OFF_VALUE) {
                mBinding.switchNightlight.setChecked(false);
                mBinding.imgColorDot.setVisibility(View.GONE);
                mBinding.txtColorName.setText(R.string.hub_control_choose_a_color);
                mBinding.progressColor.setVisibility(View.INVISIBLE);
                mBinding.seekBarLightValue.setProgress(mNightLightBrightness);
                mIsNightLightTimerOn = false;
                if (mNightLightCountDownTimer != null) {
                    Log.d(TAG, "cancelling mNightLightCountDownTimer");
                    mNightLightCountDownTimer.cancel();
                }
            } else if (ledModeValue == Hub.LEDMode.LEDModes.SET_COLOR_VALUE) {
                mBinding.switchNightlight.setChecked(true);
                mBinding.progressColor.setVisibility(View.INVISIBLE);
                mBinding.imgColorDot.setVisibility(View.VISIBLE);
                setImgDotAndName(mColorSelectedNow = ledNightLightColor == States.NightLightValue.UNKNOW ? mColorSelectedNow = States.NightLightValue.WHITE : ledNightLightColor);
                mNightLightBrightness = nightLightBrightnessf;
                mBinding.seekBarLightValue.setProgress(mNightLightBrightness);
            }

            //For Music...
            int musicVol = hubStatus.getVolume();
            int musicStateValue = hubStatus.getMusicStateValue();
            ArrayList<String> musicSongs = new ArrayList<String>() {{
                add(States.SongValue.TWINKLE_TWINKLE_LITTLE_STAR);
            }}; //default
            if (hubStatus.getMusicListCount() > 0) {
                musicSongs.clear();
                for (String music : hubStatus.getMusicListList()) {
                    musicSongs.add(music);
                }
            }
            Log.d(TAG, "Music volume from HUB : " + musicVol);
            mMusicVolume = Utils.getAppVolumeFromHubVolume(musicVol);
            Log.d(TAG, "Music volume after conversion : " + mMusicVolume);
            if (musicStateValue == Hub.MusicState.MusicStates.STOP_VALUE) {
                mBinding.progressMusic.setVisibility(View.INVISIBLE);
                mBinding.switchMusicplay.setImageResource(R.drawable.ic_play_button);
                mBinding.imgMusicplaying.setVisibility(View.GONE);
                mBinding.txtMusicName.setText(R.string.hub_control_choose_a_song);
                if (!mIgnoreStatusUpdateOnceForMusicTimer) {
                    mIsMusicTimerOn = false;
                }
                mIgnoreStatusUpdateOnceForMusicTimer = false;

            } else if (musicStateValue == Hub.MusicState.MusicStates.PLAY_VALUE) {
                isPlayingMusic = true; //ensure if the play action is call by other phone.
                mBinding.progressMusic.setVisibility(View.INVISIBLE);
                mBinding.switchMusicplay.setImageResource(R.drawable.ic_pause_button);
                mBinding.imgMusicplaying.setVisibility(View.VISIBLE);
                setMusicSong(musicSongs);
                mBinding.seekBarVolumeValue.setProgress(mMusicVolume);
            } else if (musicStateValue == Hub.MusicState.MusicStates.UNKNOWN_VALUE && ledModeValue == Hub.LEDMode.LEDModes.UNKNOWN_VALUE) { //Set volume will case this state.
                mBinding.progressMusic.setVisibility(View.INVISIBLE);
                mBinding.switchMusicplay.setImageResource(R.drawable.ic_pause_button);
                mBinding.imgMusicplaying.setVisibility(View.VISIBLE);
            }
            updateMusicTimerUI();
            updateNightLightTimerUI();
        }
    }


    private void loadLocalHubSettings() {

        mLocalHubSettings = SharedPrefManager.getHubControlSettings(getActivity());
        if (mLocalHubSettings == null) {
            mColorSelectedNow = States.NightLightValue.WHITE; //Set default color by white
            mMusicSelectedNow = new ArrayList<String>() {{
                add(States.SongValue.TWINKLE_TWINKLE_LITTLE_STAR);
            }}; //Set default music by twinkle twinkle
            mNightLightBrightness = SharedPrefManager.getInt(getActivity(), SharedPrefManager.SPKey.INT_BRIGHTNESS, 50);
            mMusicVolume = SharedPrefManager.getInt(getActivity(), SharedPrefManager.SPKey.INT_VOLUME, 50);
        } else {
            //convert hub value to app control values
            mColorSelectedNow = mLocalHubSettings.getOtherSettings().getHubSettings().getNightLightColor();
            mNightLightBrightness = Utils.getAppBrightnessFromHubBrightness(mLocalHubSettings.getOtherSettings().getHubSettings().getNightLightBrightness());
            mMusicSelectedNow = mLocalHubSettings.getOtherSettings().getHubSettings().getMusicSongs();
            mMusicVolume = Utils.getAppVolumeFromHubVolume(mLocalHubSettings.getOtherSettings().getHubSettings().getMusicVolumeLevel());
        }

    }

    public void setViewComponent() {
        mBinding.setBtnHandler(new BtnHandler());
        mBinding.switchNightlight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mBinding.switchNightlight.isPressed()) {
                    turnLedOn(isChecked);
                }
            }
        });

        mBinding.seekBarLightValue.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow Drawer to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow Drawer to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle seekbar touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        mBinding.seekBarLightValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //To set the last time volume setting...
//                SharedPrefManager.put(getActivity(), SharedPrefManager.SPKey.INT_BRIGHTNESS, mNightLightBrightness);

                int brightnessTrans = Utils.getHubBrightnessFromAppBrightness(mNightLightBrightness);

                Payload payload = new Payload();
                payload.put(Actions.Key.NIGHT_LIGHT_COLOR, mColorSelectedNow);
                payload.put(Actions.Key.NIGHT_LIGHT_LEVEL, brightnessTrans);

                mBinding.progressColor.setVisibility(View.VISIBLE);
                disPatchAction(Actions.SET_NIGHT_LIGHT_LEVEL, payload);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mNightLightBrightness = progress;
            }
        });

        mBinding.seekBarVolumeValue.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });

        mBinding.seekBarVolumeValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int volumeTrans = Utils.getHubVolumeFromAppVolume(mMusicVolume);

                Payload mPayload = new Payload();
                mPayload.put(Actions.Key.MUSIC_VOL, volumeTrans);
                mPayload.put(Actions.Key.IS_MUSIC_PLAY, isPlayingMusic);
                mPayload.put(Actions.Key.MUSIC_SONG, mMusicSelectedNow);
                isPlayingMusic = true;

                mBinding.progressMusic.setVisibility(View.VISIBLE);
                disPatchAction(Actions.SET_MUSIC_VOL, mPayload);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mMusicVolume = progress;
            }
        });
        mBinding.seekBarVolumeValue.setProgress(mMusicVolume);
        mBinding.seekBarLightValue.setProgress(mNightLightBrightness);
    }

    private void getHubControlSettings() {
        final String accessToken = AccountManagement.getInstance(getActivity()).getAccessToken();
        final SSManagement.DeviceResponse deviceResponse = SharedPrefManager.getDevice(getActivity());
        if (deviceResponse != null) {

            SproutlingApi.getProducts(new Callback<ProductResponse>() {
                @Override
                public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Received Products list");
                        ProductResponse productResponse = response.body();

                        for (Product product : productResponse) {
                            if (product.getName().equalsIgnoreCase(SPROUTLING_WEARABLE_BABY_MONITOR)) {
                                mCurrentProduct = product;
                                break;
                            }
                        }
                        if (mCurrentProduct != null) {
                            SproutlingApi.getProductSettings(new Callback<ProductSettingsResponseBody>() {
                                @Override
                                public void onResponse(Call<ProductSettingsResponseBody> call, Response<ProductSettingsResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Log.d(TAG, "Received Products Settings");
                                        ProductSettingsResponseBody productSettingsResponseBody = response.body();
                                        String jsonStr = Utils.toJsonString(productSettingsResponseBody);
                                        Log.d(TAG, "Product Settings : " + jsonStr);
//                                        mIsFirstTimeSaveSettings = response.body().isEmpty();
                                        for (ProductSettings productSettings : response.body()) {
                                            if (productSettings.getDeviceID().equalsIgnoreCase(deviceResponse.getId())) {
                                                if (productSettings.getOtherSettings() != null) {
                                                    //convert hub value to app control values
                                                    HubSettings hubSettings = productSettings.getOtherSettings().getHubSettings();

                                                    if(hubSettings != null){
                                                        mColorSelectedNow = productSettings.getOtherSettings().getHubSettings().getNightLightColor();
                                                        mNightLightBrightness = Utils.getAppBrightnessFromHubBrightness(productSettings.getOtherSettings().getHubSettings().getNightLightBrightness());
                                                        mMusicSelectedNow = productSettings.getOtherSettings().getHubSettings().getMusicSongs();
                                                        mMusicVolume = Utils.getAppVolumeFromHubVolume(productSettings.getOtherSettings().getHubSettings().getMusicVolumeLevel());
                                                        mServerHubSettings = productSettings;
                                                        Log.d(TAG, "Received NightLightColor : " + String.valueOf(mServerHubSettings.getOtherSettings().getHubSettings().getNightLightColor()));
                                                        Log.d(TAG, "Received NightLightBrightness : " + String.valueOf(mServerHubSettings.getOtherSettings().getHubSettings().getNightLightBrightness()));
                                                        Log.d(TAG, "Received Song : " + String.valueOf(mServerHubSettings.getOtherSettings().getHubSettings().getMusicSongs().get(0)));
                                                        Log.d(TAG, "Received Volume Level : " + String.valueOf(mServerHubSettings.getOtherSettings().getHubSettings().getMusicVolumeLevel()));
                                                    }

                                                    setViewComponent();
                                                    break;
                                                }
                                            }

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ProductSettingsResponseBody> call, Throwable t) {
                                    Log.d(TAG, t.getLocalizedMessage());
                                }
                            }, deviceResponse.getId(), accessToken);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ProductResponse> call, Throwable t) {

                }
            }, accessToken);
        }
    }

    public void saveHubControlSettings() {
        //while saving convert brightness and volume to hub value
        HubSettings hubSettings = new HubSettings(Utils.getHubBrightnessFromAppBrightness(mNightLightBrightness), mColorSelectedNow, Utils.getHubVolumeFromAppVolume(mMusicVolume), mMusicSelectedNow);
        Log.d(TAG, "Current NightLightColor : " + String.valueOf(hubSettings.getNightLightColor()));
        Log.d(TAG, "Current NightLightBrightness : " + String.valueOf(hubSettings.getNightLightBrightness()));
        Log.d(TAG, "Current Song : " + String.valueOf(hubSettings.getMusicSongs().get(0)));
        Log.d(TAG, "Current Volume Level : " + String.valueOf(hubSettings.getMusicVolumeLevel()));
        //if there is no product settings in the server then save it in server
        if (mServerHubSettings == null) {
            mServerHubSettings = new ProductSettings("my_settings", AccountManagement.getInstance(getActivity()).getUser().resourceOwnerId,
                    SharedPrefManager.getDevice(getActivity()).getId(), mCurrentProduct != null ? mCurrentProduct.getId() : null, null, new OtherSettings(null,hubSettings), null);
            SharedPrefManager.saveHubControlSettings(getActivity(), mServerHubSettings);
            //Don't save the product settings without product id
            if (mCurrentProduct != null) {
                //Save to server
                saveHubControlSettingsToServer(mServerHubSettings);
            }

        } else if (isSettingsChanged()) {
            mServerHubSettings.setOtherSettings(new OtherSettings(null,hubSettings));
            SharedPrefManager.saveHubControlSettings(getActivity(), mServerHubSettings);
            //save to server
            updateHubControlSettingsToServer(mServerHubSettings);

        } else if (mLocalHubSettings == null || mLocalHubSettings.getOtherSettings() == null) {
            mServerHubSettings.setOtherSettings(new OtherSettings(null,hubSettings));
            SharedPrefManager.saveHubControlSettings(getActivity(), mServerHubSettings);
            //save to server
            updateHubControlSettingsToServer(mServerHubSettings);
        }
    }

    private void updateHubControlSettingsToServer(ProductSettings productSettings) {
        ProductSettingsRequestBody productSettingsRequestBody = new ProductSettingsRequestBody(productSettings.getName(), productSettings.getUserID(), productSettings.getDeviceID(), productSettings.getProductID(), productSettings.getPushNotificationSettings(), productSettings.getOtherSettings(), productSettings.getID());
        SproutlingApi.updateProductSettings(productSettingsRequestBody, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Hub control settings updated successfully");
                } else {
                    Log.d(TAG, "Hub control settings update failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        }, AccountManagement.getInstance(getActivity()).getAccessToken());
    }

    private void saveHubControlSettingsToServer(ProductSettings productSettings) {
        ProductSettingsRequestBody productSettingsRequestBody = new ProductSettingsRequestBody(productSettings.getName(), productSettings.getUserID(), productSettings.getDeviceID(), productSettings.getProductID(), productSettings.getPushNotificationSettings(), productSettings.getOtherSettings(), productSettings.getID());

        SproutlingApi.saveProductSettings(productSettingsRequestBody, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Hub control settings saved successfully");
                } else {
                    Log.d(TAG, "Hub control settings save failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        }, AccountManagement.getInstance(getActivity()).getAccessToken());
    }

    private boolean isSettingsChanged() {

        return (mLocalHubSettings != null && (mLocalHubSettings.getOtherSettings() != null) &&
                (mLocalHubSettings.getOtherSettings().getHubSettings().getNightLightBrightness() != mNightLightBrightness ||
                        mLocalHubSettings.getOtherSettings().getHubSettings().getNightLightColor() != mColorSelectedNow ||
                        mLocalHubSettings.getOtherSettings().getHubSettings().getMusicSongs() != mMusicSelectedNow ||
                        mLocalHubSettings.getOtherSettings().getHubSettings().getMusicVolumeLevel() != mMusicVolume));
    }

    public void setImgDotAndName(int colorValue) {
        switch (colorValue) {
            case States.NightLightValue.BLUE:
                mBinding.imgColorDot.setImageResource(R.drawable.shape_item_oval_blue);
                mBinding.txtColorName.setText(R.string.blue);
                break;
            case States.NightLightValue.GREEN:
                mBinding.imgColorDot.setImageResource(R.drawable.shape_item_oval_green);
                mBinding.txtColorName.setText(R.string.green);
                break;
            case States.NightLightValue.RED:
                mBinding.imgColorDot.setImageResource(R.drawable.shape_item_oval_red);
                mBinding.txtColorName.setText(R.string.red);
                break;
            case States.NightLightValue.WHITE:
                mBinding.imgColorDot.setImageResource(R.drawable.shape_item_oval_white);
                mBinding.txtColorName.setText(R.string.white);
                break;
            case States.NightLightValue.YELLOW:
                mBinding.imgColorDot.setImageResource(R.drawable.shape_item_oval_yellow);
                mBinding.txtColorName.setText(R.string.yellow);
                break;
            case States.NightLightValue.PINK:
                mBinding.imgColorDot.setImageResource(R.drawable.shape_item_oval_pink);
                mBinding.txtColorName.setText(R.string.pink);
                break;
            case States.NightLightValue.AMBER:
                mBinding.imgColorDot.setImageResource(R.drawable.shape_item_oval_amber);
                mBinding.txtColorName.setText(R.string.amber);
                break;
            case States.NightLightValue.UNKNOW:
            default:
                mBinding.imgColorDot.setVisibility(View.INVISIBLE);
                mBinding.txtColorName.setText(R.string.hub_control_choose_a_color);

        }
    }

    public void setColorItemClicked(String colorName) {
        int brightnessTrans = Utils.getHubBrightnessFromAppBrightness(mNightLightBrightness);

        Payload mPayload = new Payload();
        mPayload.put(Actions.Key.NIGHT_LIGHT_COLOR_NAME, colorName);
        mPayload.put(Actions.Key.NIGHT_LIGHT_LEVEL, brightnessTrans);

        mBinding.progressColor.setVisibility(View.VISIBLE);

        if (colorName.equalsIgnoreCase(getString(R.string.blue))) {
            mColorSelectedNow = States.NightLightValue.BLUE;
            mBinding.imgColorDot.setImageResource(R.drawable.shape_item_oval_blue);
            disPatchAction(Actions.SET_NIGHT_LIGHT_COLOR, mPayload.put(Actions.Key.NIGHT_LIGHT_COLOR, States.NightLightValue.BLUE));
        } else if (colorName.equalsIgnoreCase(getString(R.string.green))) {
            mColorSelectedNow = States.NightLightValue.GREEN;
            mBinding.imgColorDot.setImageResource(R.drawable.shape_item_oval_green);
            disPatchAction(Actions.SET_NIGHT_LIGHT_COLOR, mPayload.put(Actions.Key.NIGHT_LIGHT_COLOR, States.NightLightValue.GREEN));
        } else if (colorName.equalsIgnoreCase(getString(R.string.yellow))) {
            mColorSelectedNow = States.NightLightValue.YELLOW;
            mBinding.imgColorDot.setImageResource(R.drawable.shape_item_oval_yellow);
            disPatchAction(Actions.SET_NIGHT_LIGHT_COLOR, mPayload.put(Actions.Key.NIGHT_LIGHT_COLOR, States.NightLightValue.YELLOW));
        } else if (colorName.equalsIgnoreCase(getString(R.string.white))) {
            mColorSelectedNow = States.NightLightValue.WHITE;
            mBinding.imgColorDot.setImageResource(R.drawable.shape_item_oval_white);
            disPatchAction(Actions.SET_NIGHT_LIGHT_COLOR, mPayload.put(Actions.Key.NIGHT_LIGHT_COLOR, States.NightLightValue.WHITE));
        } else if (colorName.equalsIgnoreCase(getString(R.string.red))) {
            mColorSelectedNow = States.NightLightValue.RED;
            mBinding.imgColorDot.setImageResource(R.drawable.shape_item_oval_red);
            disPatchAction(Actions.SET_NIGHT_LIGHT_COLOR, mPayload.put(Actions.Key.NIGHT_LIGHT_COLOR, States.NightLightValue.RED));
        } else if (colorName.equalsIgnoreCase(getString(R.string.pink))) {
            mColorSelectedNow = States.NightLightValue.PINK;
            mBinding.imgColorDot.setImageResource(R.drawable.shape_item_oval_pink);
            disPatchAction(Actions.SET_NIGHT_LIGHT_COLOR, mPayload.put(Actions.Key.NIGHT_LIGHT_COLOR, States.NightLightValue.PINK));
        } else if (colorName.equalsIgnoreCase(getString(R.string.amber))) {
            mColorSelectedNow = States.NightLightValue.AMBER;
            mBinding.imgColorDot.setImageResource(R.drawable.shape_item_oval_amber);
            disPatchAction(Actions.SET_NIGHT_LIGHT_COLOR, mPayload.put(Actions.Key.NIGHT_LIGHT_COLOR, States.NightLightValue.AMBER));
        }
    }

    public void setMusicSong(ArrayList<String> musicNames) {
        if (musicNames != null) {
            if (musicNames.size() == 1) {
                String musicName = musicNames.get(0);
                if (musicName.equalsIgnoreCase(States.SongValue.AIR_ON_G_STRING)) {
                    mBinding.txtMusicName.setText(R.string.hsl_air_on_g_string);
                } else if (musicName.equalsIgnoreCase(States.SongValue.AMBIENT_A)) {
                    mBinding.txtMusicName.setText(R.string.hsl_ambient_a);
                } else if (musicName.equalsIgnoreCase(States.SongValue.AMBIENT_B)) {
                    mBinding.txtMusicName.setText(R.string.hsl_ambient_b);
                } else if (musicName.equalsIgnoreCase(States.SongValue.AMBIENT_C)) {
                    mBinding.txtMusicName.setText(R.string.hsl_ambient_c);
                } else if (musicName.equalsIgnoreCase(States.SongValue.AMBIENT_D)) {
                    mBinding.txtMusicName.setText(R.string.hsl_ambient_d);
                } else if (musicName.equalsIgnoreCase(States.SongValue.AMBIENT_E)) {
                    mBinding.txtMusicName.setText(R.string.hsl_ambient_e);
                } else if (musicName.equalsIgnoreCase(States.SongValue.ARE_YOU_SLEEPING)) {
                    mBinding.txtMusicName.setText(R.string.hsl_are_you_sleeping);
                } else if (musicName.equalsIgnoreCase(States.SongValue.BEAUTIFUL_DREAMER)) {
                    mBinding.txtMusicName.setText(R.string.hsl_beautiful_dreamer);
                } else if (musicName.equalsIgnoreCase(States.SongValue.BRAHMS_LULLABY)) {
                    mBinding.txtMusicName.setText(R.string.hsl_brahms_lullaby);
                } else if (musicName.equalsIgnoreCase(States.SongValue.CLAIR_DR_LUNE)) {
                    mBinding.txtMusicName.setText(R.string.hsl_clair_de_lune);
                } else if (musicName.equalsIgnoreCase(States.SongValue.DANCE_OF_THE_SPIRITS)) {
                    mBinding.txtMusicName.setText(R.string.hsl_dance_of_the_spirits);
                } else if (musicName.equalsIgnoreCase(States.SongValue.HUSH_LITTLE_BABY)) {
                    mBinding.txtMusicName.setText(R.string.hsl_hush_little_baby);
                } else if (musicName.equalsIgnoreCase(States.SongValue.LIEBESTRAUM)) {
                    mBinding.txtMusicName.setText(R.string.hsl_liebestraum);
                } else if (musicName.equalsIgnoreCase(States.SongValue.SCHUBERTS_LULLABY)) {
                    mBinding.txtMusicName.setText(R.string.hsl_schuberts_lullaby);
                } else if (musicName.equalsIgnoreCase(States.SongValue.TWINKLE_TWINKLE_LITTLE_STAR)) {
                    mBinding.txtMusicName.setText(R.string.hsl_twinkle_twinkle_little_star);
                } else if (musicName.equalsIgnoreCase(States.SongValue.BROWN)) {
                    mBinding.txtMusicName.setText(R.string.hsl_brown);
                } else if (musicName.equalsIgnoreCase(States.SongValue.OCEAN)) {
                    mBinding.txtMusicName.setText(R.string.hsl_ocean);
                } else if (musicName.equalsIgnoreCase(States.SongValue.PINK)) {
                    mBinding.txtMusicName.setText(R.string.hsl_pink);
                } else if (musicName.equalsIgnoreCase(States.SongValue.RAIN)) {
                    mBinding.txtMusicName.setText(R.string.hsl_rain);
                } else if (musicName.equalsIgnoreCase(States.SongValue.THUNDER)) {
                    mBinding.txtMusicName.setText(R.string.hsl_thunder);
                } else if (musicName.equalsIgnoreCase(States.SongValue.WHITE)) {
                    mBinding.txtMusicName.setText(R.string.hsl_white);
                } else if (musicName.equalsIgnoreCase(States.SongValue.HEART_SLOW)) {
                    mBinding.txtMusicName.setText(R.string.hsl_heart_slow);
                } else if (musicName.equalsIgnoreCase(States.SongValue.HEART_FAST)) {
                    mBinding.txtMusicName.setText(R.string.hsl_heart_fast);
                }
            } else {
                mBinding.txtMusicName.setText(Utils.getPlaylistName(musicNames));
            }
        }
    }

    public void setMusicItemClicked(MusicSelectedEvent musicSelectedEvent) {
        ArrayList<String> musicNames = musicSelectedEvent.getMusicSongs();
        int volumeTrans = Utils.getHubVolumeFromAppVolume(mMusicVolume);

        Payload mPayload = new Payload();
        mPayload.put(Actions.Key.CONTEXT, getActivity());
//        mPayload.put(Actions.Key.MUSIC_SONG_NAME, musicNames);
        mPayload.put(Actions.Key.MUSIC_VOL, volumeTrans);
        mMusicSelectedNow.clear();
        if (!musicSelectedEvent.isPlayList() && musicNames != null && musicNames.size() == 1) {
            String musicName = musicNames.get(0);
            mMusicSelectedNow.add(Utils.getHubSongName(musicName));
        } else {
            for (String musicName : musicNames) {
                mMusicSelectedNow.add(Utils.getHubSongName(musicName));
            }
        }
        mBinding.imgMusicplaying.setVisibility(View.VISIBLE);
        mBinding.switchMusicplay.setImageResource(R.drawable.ic_pause_button);

        isPlayingMusic = true;

        mBinding.progressMusic.setVisibility(View.VISIBLE);
        disPatchAction(Actions.SET_MUSIC_SONG, mPayload.put(Actions.Key.MUSIC_SONG, mMusicSelectedNow));

        setMusicWindowsView(true);

        //  bug resolve
        if(mIsMusicTimerOn &&
                Utils.isTimerInFuture(SharedPrefManager.getMusicTimerValue(getActivity()))){
            mIsMusicTimerOn = true;
            mIgnoreStatusUpdateOnceForMusicTimer = true;
            int minutes = Utils.getTimerValueInMinutes(SharedPrefManager.getMusicTimerValue(getActivity()));
            waitAndSetMusicTimer(minutes);
        }
    }

    public void setMusicWindowsView(boolean isAlreadyChooseMusic) {
        if (isAlreadyChooseMusic) {
            isChooseMusicClicked = false;
            mBinding.imgArrowMusic.setImageResource(R.drawable.ic_down_arrow);
            mBinding.linMusicplayControl.setVisibility(View.VISIBLE);
            mBinding.linNightlightControl.setVisibility(View.VISIBLE);
        } else {
            isChooseMusicClicked = true;
            mBinding.imgArrowMusic.setImageResource(R.drawable.ic_up_arrow);
            mBinding.linMusicplayControl.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unRegisterEventBus();

    }

    protected void unRegisterEventBus() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MusicSelectedEvent musicSelectedEvent) {
        setMusicItemClicked(musicSelectedEvent);
    }


    /*  Bottom Sheet for selecting music */
    public static class BottomSheetMusicDialogFragment extends BottomSheetDialogFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View contentView = inflater.inflate(R.layout.music_play_song_layout, null, false);
            return contentView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            ViewPager viewPager = view.findViewById(R.id.viewPagerMusic);
            final TabLayout tabLayout = view.findViewById(R.id.slidingTabLayout);
            tabLayout.setupWithViewPager(viewPager);
            final MusicSongPagerAdapter musicSongPagerAdapter = new MusicSongPagerAdapter(getChildFragmentManager());
            viewPager.setAdapter(musicSongPagerAdapter);
            Button playButton = view.findViewById(R.id.btnPlay);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectedTab = tabLayout.getSelectedTabPosition();
                    MusicSongFragmentView musicSongFragmentView = musicSongPagerAdapter.getMusicSongFragmentViews().get(selectedTab);
                    processMusicSelection(selectedTab, musicSongFragmentView);
                    dismiss();
                }
            });
        }

        private void processMusicSelection(int selectedTab, MusicSongFragmentView musicSongFragmentView) {
            final String selectedMusicItem = musicSongFragmentView.getSelectedMusic();
            ArrayList<String> selectedMusics = new ArrayList<>();
            boolean isPlaylist = (selectedTab == 0);
            if (isPlaylist) {
                if (selectedMusicItem.equalsIgnoreCase(getString(R.string.classical_playlist))) {
                    selectedMusics.addAll(Arrays.asList(getResources().getStringArray(R.array.classical_playlist_items)));
                } else if (selectedMusicItem.equalsIgnoreCase(getString(R.string.ambient_playlist))) {
                    selectedMusics.addAll(Arrays.asList(getResources().getStringArray(R.array.ambient_playlist_items)));
                } else if (selectedMusicItem.equalsIgnoreCase(getString(R.string.lullabies_playlist))) {
                    selectedMusics.addAll(Arrays.asList(getResources().getStringArray(R.array.lullabies_playlist_items)));
                } else if (selectedMusicItem.equalsIgnoreCase(getString(R.string.custom_playlist))) {
                    CustomPlaylist customPlaylist = SharedPrefManager.getCustomPlaylist(getContext());
                    selectedMusics = customPlaylist != null ? customPlaylist.getMusicSongs() : null;
                }
            } else {
                selectedMusics.add(selectedMusicItem);
            }
            EventBus.getDefault().post(new MusicSelectedEvent(selectedMusics, selectedMusicItem, isPlaylist));

        }
    }

    public class BtnHandler {

        public void onMusicPlayClicked() {
            Log.v(TAG, "onMusicPlayClicked.");
            isPlayingMusic = !isPlayingMusic;


            Payload payload = new Payload();
            payload.put(Actions.Key.CONTEXT, getActivity());
            payload.put(Actions.Key.IS_MUSIC_PLAY, isPlayingMusic);
//            payload.put(Actions.Key.MUSIC_SONG_NAME, );
            payload.put(Actions.Key.MUSIC_VOL, Utils.getHubVolumeFromAppVolume(mMusicVolume));
            payload.put(Actions.Key.MUSIC_SONG, mMusicSelectedNow);
            mBinding.progressMusic.setVisibility(View.VISIBLE);
            disPatchAction(Actions.SWITCH_MUSIC_PLAY, payload);
        }

        public void onHubChooseColorClicked() {
            Log.v(TAG, "onHubChooseColorClicked.");
            showColorListBottomSheet(mColorSelectedNow);
        }

        public void onHubChooseMusicClicked() {
            Log.v(TAG, "onHubChooseMusicClicked.");
            showMusicListBottomSheet(1);
        }
    }
}

package com.sproutling.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.MediaController;
import android.widget.VideoView;

import com.sproutling.R;
import com.sproutling.object.TimeSpent;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.sproutling.utils.LogEvents.PUTTING_ON_WEARABLE_VIDEO_CLOSE;
import static com.sproutling.utils.LogEvents.SPROUTLING_VIDEO_CLOSE;

/**
 * Created by subram13 on 8/15/17.
 */

public class VideoPlayerActivity extends BaseActivity {


    public static final String CHARGING_WEARABLE_VIDEO_URL = "https://s3-us-west-2.amazonaws.com/sproutling-marketing/SproutlingDemo_02_HowToCharge_RC2a_Email.mp4";
    //    public static final String WHAT_IS_SPROUTLING_VIDEO_URL = "https://www.dropbox.com/sh/vx6f8qo4hz4mm09/AACxrEBS21d_qFwYmcNjVhD4a?dl=0&preview=SproutlingReCreation_RC3a.mp4?dl=1";
    public static final String WHAT_IS_SPROUTLING_VIDEO_URL = "https://s3-us-west-2.amazonaws.com/sproutling-marketing/SproutlingReCreation_RC3a.mp4";
    public static final String PUTTING_ON_WEARABLE_VIDEO_URL = "https://s3-us-west-2.amazonaws.com/sproutling-marketing/SproutlingDemo_03_HowToPutOnBaby_RC2a_Email.mp4";
    private static final String VIDEO_URL = "VIDEO_URL";
    private String mVideoURL;
    private VideoView mVideoView;
    private MediaController mMediaController;

    public static void startVideoActivity(Context context, String videoURL) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(VIDEO_URL, videoURL);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mVideoURL = bundle.getString(VIDEO_URL);
        }

        mVideoView = (VideoView) findViewById(R.id.video_view);
        if (!TextUtils.isEmpty(mVideoURL)) {
            mVideoView.setVideoURI(Uri.parse(mVideoURL));
            mMediaController = new MediaController(this);
            mMediaController.setAnchorView(mVideoView);
            mVideoView.setMediaController(mMediaController);
//            mVideoView.setZOrderOnTop(true);
            mVideoView.start();
            showProgressBar(true);
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    showProgressBar(false);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpToolbar();
        initActionBar();
        setToolBarTitle("");
        setBackDrawable(R.drawable.ic_android_back_white);
    }

    @Override
    public void finish() {
        super.finish();

        String jsonString = Utils.toJsonString(new TimeSpent(mVideoView.getCurrentPosition()));
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            String logEvent = null;
            if (CHARGING_WEARABLE_VIDEO_URL.equalsIgnoreCase(VIDEO_URL)) {
                logEvent = LogEvents.HOW_TO_CHARGE_VIDEO_CLOSE;
            } else if (PUTTING_ON_WEARABLE_VIDEO_URL.equalsIgnoreCase(VIDEO_URL)) {
                logEvent = PUTTING_ON_WEARABLE_VIDEO_CLOSE;
            } else if (WHAT_IS_SPROUTLING_VIDEO_URL.equalsIgnoreCase(VIDEO_URL)) {
                logEvent = SPROUTLING_VIDEO_CLOSE;

            }

            Utils.logEvents(logEvent, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

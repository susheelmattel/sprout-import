package com.sproutling.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.sproutling.R;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

/**
 * Created by subram13 on 8/15/17.
 */

public class SproutlingVideoActivity extends BaseActivity {
    public static final int REQUEST_CODE_QR = 1;
    private static final String SPROUTLING_URL = "http://www.fisher-price.com/sproutling";
    private ShTextView mTvJoin;
    private ShTextView mTvSetUp;
    private ShTextView mTvDontHave;
    private RelativeLayout mVideoLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sproutling_video);
        mTvJoin = (ShTextView) findViewById(R.id.join);
        mTvJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logEvents(LogEvents.JOIN_EXISTING_FAMILY);
                startActivityForResult(new Intent(SproutlingVideoActivity.this, QRScannerActivity.class), REQUEST_CODE_QR);
            }
        });
        mTvSetUp = (ShTextView) findViewById(R.id.setup);
        mTvSetUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logEvents(LogEvents.SETUP_NEW_SPROUTLING);
                startActivity(new Intent(SproutlingVideoActivity.this, LegalActivity.class));
            }
        });
        mTvDontHave = (ShTextView) findViewById(R.id.tv_no_sproutling);
        mTvDontHave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logEvents(LogEvents.DONT_HAVE_SPROUTLING);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(SPROUTLING_URL));
                startActivity(intent);
            }
        });
        mVideoLayout = (RelativeLayout) findViewById(R.id.video_layout);
        mVideoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logEvents(LogEvents.SPROUTLING_VIDEO);
                playSproutlingVideo();
            }
        });
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

    private void playSproutlingVideo() {
        VideoPlayerActivity.startVideoActivity(this, VideoPlayerActivity.WHAT_IS_SPROUTLING_VIDEO_URL);
    }
}

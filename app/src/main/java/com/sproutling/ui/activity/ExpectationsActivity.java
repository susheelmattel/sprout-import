/*
 * Copyright (c) 2017 Fuhu, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.fuhu.states.interfaces.IStatePayload;
import com.sproutling.R;
import com.sproutling.object.TimeSpent;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bradylin on 6/22/17.
 */

public class ExpectationsActivity extends BaseMqttActivity {
    private long mStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expectations);

        ((ShTextView) findViewById(R.id.navigation_title)).setText(R.string.expectations_title);
        findViewById(R.id.navigation_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.navigation_action).setVisibility(View.GONE);

        ShTextView content3 = (ShTextView) findViewById(R.id.section_3_content);
        ShTextView content5 = (ShTextView) findViewById(R.id.section_5_content);
//        ShTextView letUsKnow = (ShTextView) findViewById(R.id.let_us_know);

        setSpannableText(content3, R.string.expectations_section_3_content, R.string.expectations_text_bold_not);
        setSpannableText(content5, R.string.expectations_section_5_content, R.string.expectations_text_bold_never);
//        setSpannableText(letUsKnow, R.string.expectations_let_us_know, R.string.expectations_text_tealish_drop);
        mStartTime = System.currentTimeMillis();
    }

    private void setSpannableText(TextView view, int textId, int spannableTextId) {
        String text = getString(textId);
        final String spannableText = getString(spannableTextId);

        SpannableString ss = new SpannableString(text);

        int index = text.indexOf(spannableText);
        ss.setSpan(getClickableSpan(view.getId()), index, index + spannableText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        view.setText(ss);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setHighlightColor(Color.TRANSPARENT);
    }

    private ClickableSpan getClickableSpan(int id) {
        switch (id) {
            case R.id.section_3_content:
                return new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setTypeface(Typeface.DEFAULT_BOLD);
                        ds.setColor(getResources().getColor(R.color.dolphin));
                    }
                };
            case R.id.section_5_content:
                return new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setTypeface(Typeface.DEFAULT_BOLD);
                        ds.setColor(getResources().getColor(R.color.dolphin));
                    }
                };
//            case R.id.let_us_know:
//                return new ClickableSpan() {
//                    @Override
//                    public void onClick(View textView) {
//                        Toast.makeText(ExpectationsActivity.this, "Drop us a line", Toast.LENGTH_SHORT).show();
////                        startActivity(new Intent(ExpectationsActivity.this, ExpectationsActivity.class));
//                    }
//
//                    @Override
//                    public void updateDrawState(TextPaint ds) {
//                        super.updateDrawState(ds);
//                        ds.setUnderlineText(false);
//                        ds.setColor(getResources().getColor(R.color.tealish));
//                    }
//                };
            default:
                return new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setTypeface(Typeface.DEFAULT_BOLD);
                        ds.setColor(getResources().getColor(R.color.tealish));
                    }
                };
        }
    }

    @Override
    public void onStateChanged(IStatePayload payload) {

    }

    @Override
    protected ArrayList<String> getTopicsToSubscribe() {
        return null;
    }

    @Override
    public void finish() {
        super.finish();
        long elapsedTimeMillis = System.currentTimeMillis() - mStartTime;
        String jsonString = Utils.toJsonString(new TimeSpent((elapsedTimeMillis / 1000F)));

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            Utils.logEvents(LogEvents.EXPECTATIONS_TIME_SPENT, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

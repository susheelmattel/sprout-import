/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.databinding;

import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sproutling.App;
import com.sproutling.R;
import com.sproutling.ui.widget.BatteryDisplay;
import com.sproutling.ui.widget.ChildNameButton;
import com.sproutling.utils.Utils;

/**
 * Created by Xylon on 2016/12/29.
 */

public class StatusActivityModel extends BaseObservable {

    private static final String TAG = "StatusActivityModel";

    // ImageButton in the bottom...
    @BindingAdapter(value = {"image_src"})
    public static void setImageResource(ImageView imageView, int resourceId) {
        imageView.setImageResource(resourceId);
    }

    // Control the battery value display in right top...
    @BindingAdapter(value = {"battery_display_value"})
    public static void setBatteryDisplayValue(BatteryDisplay view, int batteryValue) {
        view.setBatteryDisplay(batteryValue);
    }

    // Control the activity background shape and color...
    @BindingAdapter(value = {"background_shape", "background_start_color", "background_end_color"}, requireAll = true)
    public static void setBgResource(final ViewGroup view, int o_bgResourceId, int o_startColor, int o_endColor, int bgResourceId, final int startColor, final int endColor) {

        if (o_endColor != endColor) {
            final TransitionDrawable bgShape = (TransitionDrawable) view.getBackground();
            Log.d(TAG, "StartColor");
            GradientDrawable startGradient = bgShape == null ? getGradientDrawable(startColor) : (GradientDrawable) bgShape.getDrawable(1);
            Log.d(TAG, "endColor");
            GradientDrawable endGradient = getGradientDrawable(endColor);


            GradientDrawable[] gradientDrawables = new GradientDrawable[]{startGradient, endGradient};
            TransitionDrawable transitionDrawable = new TransitionDrawable(gradientDrawables);
            view.setBackground(transitionDrawable);
            transitionDrawable.startTransition(1000);

        }
    }

    private static GradientDrawable getGradientDrawable(int color) {
        GradientDrawable gradientDrawable;
        switch (color) {
            case R.color.statusgreen:
                gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{getCompColor(R.color.status_green_start), getCompColor(R.color.status_green_end)});
                Log.d(TAG, "green");
                break;
            case R.color.statusred:
                gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{getCompColor(R.color.status_red_start), getCompColor(R.color.status_red_end)});
                Log.d(TAG, "red");
                break;
            case R.color.statusyellow:
                gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{getCompColor(R.color.status_yellow_start), getCompColor(R.color.status_yellow_end)});
                Log.d(TAG, "yellow");
                break;
            default:
                gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{getCompColor(R.color.status_green_start), getCompColor(R.color.status_green_end)});
                Log.d(TAG, "no color: green");
                break;
        }
        int radius = App.getAppContext().getResources().getDimensionPixelSize(R.dimen.status_top_radius);
        gradientDrawable.setCornerRadii(new float[]{radius, radius, radius, radius, 0, 0, 0, 0});
        return gradientDrawable;
    }

    private static int getCompColor(int color) {
        return Utils.getColor(App.getInstance(), color);
    }

    //ChildNameButton View...
    @BindingAdapter(value = {"child_name"})
    public static void setChildName(ChildNameButton view, String babyName) {
        view.setBabyName(babyName);
    }

//    //ChildNameButton View...
//    @BindingAdapter(value = {"child_name"})
//    public static void setChildName(ShTextView view, String babyName)
//    {
//        String str = view.getText().toString();
//        str = String.format(str, babyName);
//        view.setText(str);
//    }

}

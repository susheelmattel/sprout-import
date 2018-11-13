package com.sproutling.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.fuhu.states.payloads.Payload;
import com.sproutling.App;
import com.sproutling.R;
import com.sproutling.states.Actions;
import com.sproutling.ui.activity.StatusActivity;
import com.sproutling.utils.Utils;

import static com.sproutling.utils.LogEvents.LEARNING_PERIOD_STARTED;

/**
 * Created by subram13 on 10/13/17.
 */

public class LearningPeriodStartDialog extends Dialog {
    public LearningPeriodStartDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
        setContentView(R.layout.dialog_learningperiod_start);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        findViewById(R.id.btnBegin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StatusActivity.sInstance != null) {
                    String childId = StatusActivity.sInstance.getCurrentChild().id;
                    App.getInstance().dispatchAction(Actions.START_LEARNING_PERIOD, new Payload().put(Actions.Key.CHILD_ID, childId), StatusActivity.sInstance);
//                        SharedPrefManager.put(StatusActivity.sInstance, SharedPrefManager.SPKey.BOOL_TOOLTIPS_FIRST_SHOWN, true);

                }
                Utils.logEvents(LEARNING_PERIOD_STARTED);
                cancel();
            }
        });
    }
}

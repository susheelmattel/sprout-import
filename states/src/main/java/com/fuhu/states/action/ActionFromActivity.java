package com.fuhu.states.action;

import android.app.Activity;

import com.fuhu.states.interfaces.IStatePayload;

/**
 * Created by moi0312 on 2017/1/13.
 */

public class ActionFromActivity extends Action {

	public ActionFromActivity(int type, Activity dispatcher, IStatePayload payload) {
		super(type, dispatcher, payload);
	}

	@Override
	public Activity getDispatcher() {
		return (Activity)dispatcher;
	}
}

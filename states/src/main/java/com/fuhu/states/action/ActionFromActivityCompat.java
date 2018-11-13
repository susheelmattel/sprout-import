package com.fuhu.states.action;

import android.support.v7.app.AppCompatActivity;

import com.fuhu.states.interfaces.IStatePayload;

/**
 * Created by moi0312 on 2017/1/13.
 */

public class ActionFromActivityCompat extends Action {

	public ActionFromActivityCompat(int type, AppCompatActivity dispatcher, IStatePayload payload) {
		super(type, dispatcher, payload);
	}

	@Override
	public AppCompatActivity getDispatcher() {
		return (AppCompatActivity)dispatcher;
	}
}

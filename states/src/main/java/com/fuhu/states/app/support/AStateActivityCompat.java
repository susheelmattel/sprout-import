package com.fuhu.states.app.support;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fuhu.states.Store;
import com.fuhu.states.action.ActionFromActivityCompat;
import com.fuhu.states.interfaces.IStateApplication;
import com.fuhu.states.interfaces.IStateListener;
import com.fuhu.states.interfaces.IStatePayload;

public abstract class AStateActivityCompat extends AppCompatActivity implements IStateListener {

	protected Store store;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		store = ((IStateApplication)getApplication()).getStore();
		store.subscribe(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		stateRegain();

	}

	@Override
	protected void onDestroy() {
		store.unSubscribe(this);
		super.onDestroy();
	}

	@Override
	public void disPatchAction(int type, IStatePayload payload){
		store.dispatch(new ActionFromActivityCompat(type, this, payload));
	}

	@Override
	public void stateRegain(){
		onStateChanged(store.getStateData());
	}

	@Override
	abstract public void onStateChanged(IStatePayload payload);




}

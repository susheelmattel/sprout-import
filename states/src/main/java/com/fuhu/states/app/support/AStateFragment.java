package com.fuhu.states.app.support;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.fuhu.states.Store;
import com.fuhu.states.action.ActionFromSupportFragment;
import com.fuhu.states.interfaces.IStateApplication;
import com.fuhu.states.interfaces.IStateListener;
import com.fuhu.states.interfaces.IStatePayload;

public abstract class AStateFragment extends Fragment implements IStateListener {

	protected Store store;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		store = ((IStateApplication)getActivity().getApplication()).getStore();
		store.subscribe(this);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		stateRegain();
	}

	@Override
	public void onDestroyView() {
		store.unSubscribe(this);
		super.onDestroyView();
	}

	@Override
	public void disPatchAction(int type, IStatePayload payload){
		store.dispatch(new ActionFromSupportFragment(type, this, payload));
	}

	@Override
	public void stateRegain(){
		onStateChanged(store.getStateData());
	}

	@Override
	abstract public void onStateChanged(IStatePayload payload);
}

package com.fuhu.states.interfaces;

public interface IStateListener {
	void onStateChanged(IStatePayload payload);
	void stateRegain();
	void disPatchAction(int type, IStatePayload payload);
}
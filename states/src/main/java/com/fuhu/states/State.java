package com.fuhu.states;

import com.fuhu.states.interfaces.IState;
import com.fuhu.states.interfaces.IStatePayload;
import com.fuhu.states.payloads.Payload;

/**
 * Created by moi0312 on 2016/11/30.
 */

public class State implements IState {

	protected Payload stateObj;

	public State() {
		this.stateObj = new Payload();
	}
	public State(IStatePayload stateObj) {
		this.stateObj = (Payload) stateObj;
	}

	@Override
	public Payload data() {
		return stateObj;
	}

	@Override
	public State duplicate() {
		return new State( this.stateObj.duplicate() );
	}

	@Override
	public State update(IStatePayload payload) {
		this.stateObj.update(payload);
		return this;
	}
}
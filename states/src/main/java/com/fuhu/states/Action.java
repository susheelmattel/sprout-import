package com.fuhu.states;

import com.fuhu.states.interfaces.IStatePayload;

/**
 * Created by moi0312 on 2016/12/1.
 */

public class Action {

	private int type;
	private Object dispatcher;
	private IStatePayload payload;

	public Action(int type, IStatePayload payload) {
		this.type = type;
		this.payload = payload;
	}

	public Action(int type, Object dispatcher, IStatePayload payload) {
		this(type, payload);
		this.dispatcher = dispatcher;
	}

	public int getType() {
		return type;
	}
	public IStatePayload payload() {
		return payload;
	}

	public void setDispatcher(Object dispatcher) {
		this.dispatcher = dispatcher;
	}
	public Object getDispatcher() { return dispatcher; }

}

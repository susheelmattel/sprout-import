package com.fuhu.states.interfaces;

/**
 * Created by moi0312 on 2016/12/6.
 */

public interface IStatePayload {
	IStatePayload duplicate();
	IStatePayload update(IStatePayload payload);
	boolean isEmpty();

}

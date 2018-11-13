package com.fuhu.states.interfaces;

/**
 * Created by moi0312 on 2016/11/30.
 */
public interface IState{
	IState duplicate();
	IState update(IStatePayload payload);
	IStatePayload data();
}

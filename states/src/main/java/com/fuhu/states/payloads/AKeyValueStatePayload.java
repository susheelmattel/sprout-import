package com.fuhu.states.payloads;

import com.fuhu.states.interfaces.IStatePayload;

/**
 * Created by moi0312 on 2017/1/11.
 */

public abstract class AKeyValueStatePayload implements IStatePayload {

	@Override
	abstract public IStatePayload duplicate();

	@Override
	abstract public IStatePayload update(IStatePayload payload);

	abstract public String[] getKeys();
	abstract public boolean isEmpty();
	abstract public void remove(String key);

	abstract public Object get(String k);
	abstract public String getString(String k);
	abstract public boolean getBoolean(String k, boolean defaultValue);
	abstract public int getInt(String k, int defaultValue);
	abstract public double getDouble(String k, double defaultValue);
	abstract public float getFloat(String k, float defaultValue);
	abstract public long getLong(String k, long defaultValue);
	abstract public AKeyValueStatePayload put(String k, Object v);
	abstract public AKeyValueStatePayload put(String k, String v);
	abstract public AKeyValueStatePayload put(String k, boolean v);
	abstract public AKeyValueStatePayload put(String k, int v);
	abstract public AKeyValueStatePayload put(String k, double v);
	abstract public AKeyValueStatePayload put(String k, float v);
	abstract public AKeyValueStatePayload put(String k, long v);

}

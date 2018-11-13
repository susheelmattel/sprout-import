package com.fuhu.states.payloads;

import com.fuhu.states.interfaces.IStatePayload;

import java.util.HashMap;
import java.util.Set;


public class Payload extends AKeyValueStatePayload {

	private HashMap<String, Object> data;

	public Payload() {
		this.data = new HashMap<>();
	}

	public Payload(HashMap<String, Object> data) {
		this.data = data;
	}

	public Payload(HashMap<String, Object> copyFrom, String[] keys) {
		this.data = new HashMap<>();
		for(String key : keys){
			this.data.put(key, copyFrom.get(key));
		}
	}

	public String[] getKeys() {
		Set<String> keySet = this.data.keySet();
		String[] keys = new String[keySet.size()];
		int index = 0;
		for(String key: keySet){
			keys[index] = key;
			index++;
		}
		return keys;
	}

	@Override
	public IStatePayload duplicate() {
		return new Payload((HashMap<String, Object>) this.data.clone());
	}

	@Override
	public IStatePayload update(IStatePayload payload) {
		if(payload != null && payload instanceof Payload){
			Payload pl = (Payload)payload;
			if(!pl.isEmpty()){
				String[] names = pl.getKeys();
				for(int i=0; i< names.length; i++){
					this.put(names[i], pl.get(names[i]));
				}
			}
		}
		return this;
	}
	@Override
	public boolean isEmpty() {
		return this.data.isEmpty();
	}
	@Override
	public void remove(String key){
		this.data.remove(key);
	}
	@Override
	public Object get(String k) {
		return data.get(k);
	}
	@Override
	public String getString(String k) {
		if(this.data.containsKey(k)) {
			return (String) this.data.get(k);
		}else{
			return null;
		}
	}
	@Override
	public boolean getBoolean(String k, boolean defaultValue) {
		if(this.data.containsKey(k)) {
			return (boolean) this.data.get(k);
		}else{
			return defaultValue;
		}
	}
	@Override
	public int getInt(String k, int defaultValue) {
		if(this.data.containsKey(k)) {
			return (int)this.data.get(k);
		}else{
			return defaultValue;
		}
	}
	@Override
	public double getDouble(String k, double defaultValue) {
		if(this.data.containsKey(k)) {
			return (double) this.data.get(k);
		}else{
			return defaultValue;
		}
	}
	@Override
	public float getFloat(String k, float defaultValue) {
		if(this.data.containsKey(k)) {
			return (float) this.data.get(k);
		}else{
			return defaultValue;
		}
	}
	@Override
	public long getLong(String k, long defaultValue) {
		if(this.data.containsKey(k)) {
			return (long) this.data.get(k);
		}else{
			return defaultValue;
		}
	}
	@Override
	public Payload put(String k, Object v) {
		this.data.put(k,v);
		return this;
	}
	@Override
	public Payload put(String k, String v) {
		this.data.put(k, v);
		return this;
	}
	@Override
	public Payload put(String k, boolean v) {
		this.data.put(k, v);
		return this;
	}
	@Override
	public Payload put(String k, int v) {
		this.data.put(k, v);
		return this;
	}
	@Override
	public Payload put(String k, double v) {
		this.data.put(k, v);
		return this;
	}
	@Override
	public Payload put(String k, float v) {
		this.data.put(k, v);
		return this;
	}
	@Override
	public Payload put(String k, long v) {
		this.data.put(k, v);
		return this;
	}
}

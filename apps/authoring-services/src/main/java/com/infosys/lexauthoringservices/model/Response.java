package com.infosys.lexauthoringservices.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Response implements Serializable, Cloneable {

	private static final long serialVersionUID = -3773253896160786443L;
	private String id;
	private String ver;
	private String ts;
	private Map<String, Object> result = new HashMap<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public Map<String, Object> getResult() {
		return result;
	}

	public Object get(String key) {
		return result.get(key);
	}

	public void put(String key, Object vo) {
		result.put(key, vo);
	}

	public void putAll(Map<String, Object> map) {
		result.putAll(map);
	}

	public boolean containsKey(String key) {
		return result.containsKey(key);
	}

	public Response clone(Response response) {
		try {
			return (Response) response.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}

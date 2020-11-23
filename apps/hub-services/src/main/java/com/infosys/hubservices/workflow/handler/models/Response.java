package com.infosys.hubservices.workflow.handler.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Response implements Serializable {

    private static final long serialVersionUID = -3773253896160786443L;

    private transient Map<String, Object> result = new HashMap<>();

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

}

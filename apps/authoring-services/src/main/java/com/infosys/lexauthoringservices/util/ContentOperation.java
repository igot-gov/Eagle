package com.infosys.lexauthoringservices.util;

public enum ContentOperation {

	UPDATE("update"), UPDATE_HIERARCHY("updateHierarchy"), FETCH("fetch"), FETCH_HIERARCHY("fetchHierarchy"),

	STATUS_CHANGE("statusChange");

	private String contentOperation;

	public String get() {
		return contentOperation;
	}

	private ContentOperation(String operation) {
		this.contentOperation = operation;
	}

}
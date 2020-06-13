package com.infosys.lexauthoringservices.validation;

public enum Operations {

	EQUALS("equals"), NOT_EQUALS("notequals"), GREATER_THAN("greaterthan"), LESS_THAN("lessthan"), NOT_NULL("notnull"),
	IN("in"), HIT_URL("hiturl"), CONTAINS("contains"), NOT_CONTAINS("notcontains"), EMPTY("empty");

	private String operation;

	public String get() {
		return operation;
	}

	private Operations(String operation) {
		this.operation = operation;
	}
}
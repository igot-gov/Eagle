package com.infosys.lexauthoringservices.validation;

public enum ValidationConstants {

	OPERATION("operation"), PROPERTY("property"), DATA_TYPE("dataType"), VALUE("value"),
	VALIDATE_BELOW("validateBelow"), VALIDATE_HERE("validateHere");

	private String validationConstant;

	public String get() {
		return validationConstant;
	}

	private ValidationConstants(String validationConstant) {
		this.validationConstant = validationConstant;
	}
}
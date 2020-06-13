package com.infosys.lexauthoringservices.validation;

public enum DataTypes {

	STRING("String"), LONG("Long"), BOOLEAN("Boolean"), LIST("List");

	private String dataType;

	public String get() {
		return dataType;
	}

	private DataTypes(String dataType) {
		this.dataType = dataType;
	}
}
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.exercise.bodhi.repo;

public class ClassErrorsSpecification {
	public String className = "";
	public String attributes = "";
	public String methods = "";
	public String accessSpecifier = "";
	public float errorMarks = 100.0f;
	public String errorType = "";

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public String getMethods() {
		return methods;
	}

	public void setMethods(String methods) {
		this.methods = methods;
	}

	public String getAccessSpecifier() {
		return accessSpecifier;
	}

	public void setAccessSpecifier(String accessSpecifier) {
		this.accessSpecifier = accessSpecifier;
	}

	public float getErrorMarks() {
		return errorMarks;
	}

	public void setErrorMarks(float errorMarks) {
		this.errorMarks = errorMarks;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public ClassErrorsSpecification() {
		className = "";
		attributes = "";
		methods = "";
		accessSpecifier = "";
		errorMarks = 0;
		errorType = "";
	}

	public ClassErrorsSpecification(String className, float errorMarks, String errorType) {
		this();
		this.className = className;
		this.errorMarks = errorMarks;
		this.errorType = errorType;
	}

	public ClassErrorsSpecification(String className, String attributes, float errorMarks, String errorType) {
		this();
		this.className = className;
		this.errorMarks = errorMarks;
		this.errorType = errorType;
		this.attributes = attributes;
	}
	
	public ClassErrorsSpecification(String className,  float errorMarks, String errorType,String methods) {
		this();
		this.className = className;
		this.errorMarks = errorMarks;
		this.errorType = errorType;
		this.methods = methods;
	}
	
	public ClassErrorsSpecification(String className, String accessSpecifier, float errorMarks, String errorType,String methods) {
		this();
		this.className = className;
		this.errorMarks = errorMarks;
		this.errorType = errorType;
		this.methods = methods;
		this.accessSpecifier = accessSpecifier;

	}
	
	public ClassErrorsSpecification(String className, String attributes,String accessSpecifier, float errorMarks, String errorType) {
		this();
		this.className = className;
		this.errorMarks = errorMarks;
		this.errorType = errorType;
		this.attributes = attributes;
		this.accessSpecifier = accessSpecifier;
	}
}

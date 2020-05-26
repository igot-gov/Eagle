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

import java.util.List;



public class Evaluatedmarks {
	private float marksPercent;
	private float marks;
	private List<ClassErrorsSpecification> classErrorsSpecification;
	private List<RelationErrorsSpecification> relationErrorsSpecification;
	private String message;

	public Evaluatedmarks() {
		message = "";
		marks = 0.0f;
	}

	public float getMarksPercent() {
		return marksPercent;
	}

	public void setMarksPercent(float marksPercent) {
		this.marksPercent = marksPercent;
	}

	public float getMarks() {
		return marks;
	}

	public void setMarks(float marks) {
		this.marks = marks;
	}

	public List<ClassErrorsSpecification> getClassErrorsSpecification() {
		return classErrorsSpecification;
	}

	public void setClassErrorsSpecification(List<ClassErrorsSpecification> classErrorsSpecification) {
		this.classErrorsSpecification = classErrorsSpecification;
	}

	public List<RelationErrorsSpecification> getRelationErrorsSpecification() {
		return relationErrorsSpecification;
	}

	public void setRelationErrorsSpecification(List<RelationErrorsSpecification> relationErrorsSpecification) {
		this.relationErrorsSpecification = relationErrorsSpecification;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		
		return String.valueOf(this.marks);
		
	}
}
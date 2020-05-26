/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.exercise.dto;

public class JavaVerificationAnalysis {

	private int passedProceduralActualTC;
	private int passedStructuralTC;
	private int failedProceduralActualTC;
	private int failedProceduralSampleTC;
	private int failedStructuralTC;
	private int passedProceduralSampleTC;
	private String responseCode;
	private String response;
	
	
	
	
	public int getPassedProceduralActualTC() {
		return passedProceduralActualTC;
	}




	public void setPassedProceduralActualTC(int passedProceduralActualTC) {
		this.passedProceduralActualTC = passedProceduralActualTC;
	}




	public int getPassedStructuralTC() {
		return passedStructuralTC;
	}




	public void setPassedStructuralTC(int passedStructuralTC) {
		this.passedStructuralTC = passedStructuralTC;
	}




	public int getFailedProceduralActualTC() {
		return failedProceduralActualTC;
	}




	public void setFailedProceduralActualTC(int failedProceduralActualTC) {
		this.failedProceduralActualTC = failedProceduralActualTC;
	}




	public int getFailedProceduralSampleTC() {
		return failedProceduralSampleTC;
	}




	public void setFailedProceduralSampleTC(int failedProceduralSampleTC) {
		this.failedProceduralSampleTC = failedProceduralSampleTC;
	}




	

	public int getPassedProceduralSampleTC() {
		return passedProceduralSampleTC;
	}




	public void setPassedProceduralSampleTC(int passedProceduralSampleTC) {
		this.passedProceduralSampleTC = passedProceduralSampleTC;
	}




	public String getResponseCode() {
		return responseCode;
	}




	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}




	public String getResponse() {
		return response;
	}




	public void setResponse(String response) {
		this.response = response;
	}




	public JavaVerificationAnalysis() {

		this.passedProceduralActualTC = 0;
		this.passedStructuralTC = 0;
		this.failedProceduralActualTC = 0;
		this.failedProceduralSampleTC = 0;
		this.failedStructuralTC = 0;
		this.passedProceduralSampleTC = 0;
	}




	public int getFailedStructuralTC() {
		return failedStructuralTC;
	}




	public void setFailedStructuralTC(int failedStructuralTC) {
		this.failedStructuralTC = failedStructuralTC;
	}
}

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
package com.infosys.lex.exercise.dto;

public class JsonNumericalVerificationData {

	
	private int TotalTestCases ;
    private int TotalLogicalTestCases ;
    private int TotalStructuralTestCases ;
    private int LogicalTestCasesPassed ;
    private int StructuralTestCasesPassed ;
    private int TotalCasesPassed ;
    private int ILPStatusCode ;

    private int StructuralDisplayCount ;
    private int SampleDisplayCount ;
    private int ActualDisplayCount ;
    private int TotalCasesNotExecuted ;
    private String StatusDescription ; 
    private float CovPercentile ;

    private int TotalStructuralTestcasesFailed ;
    private int TotalLogicalTestCasesFailed ;
    public int getTotalTestCases() {
		return TotalTestCases;
	}
	public void setTotalTestCases(int totalTestCases) {
		TotalTestCases = totalTestCases;
	}
	public int getTotalLogicalTestCases() {
		return TotalLogicalTestCases;
	}
	public void setTotalLogicalTestCases(int totalLogicalTestCases) {
		TotalLogicalTestCases = totalLogicalTestCases;
	}
	public int getTotalStructuralTestCases() {
		return TotalStructuralTestCases;
	}
	public void setTotalStructuralTestCases(int totalStructuralTestCases) {
		TotalStructuralTestCases = totalStructuralTestCases;
	}
	public int getLogicalTestCasesPassed() {
		return LogicalTestCasesPassed;
	}
	public void setLogicalTestCasesPassed(int logicalTestCasesPassed) {
		LogicalTestCasesPassed = logicalTestCasesPassed;
	}
	public int getStructuralTestCasesPassed() {
		return StructuralTestCasesPassed;
	}
	public void setStructuralTestCasesPassed(int structuralTestCasesPassed) {
		StructuralTestCasesPassed = structuralTestCasesPassed;
	}
	public int getTotalCasesPassed() {
		return TotalCasesPassed;
	}
	public void setTotalCasesPassed(int totalCasesPassed) {
		TotalCasesPassed = totalCasesPassed;
	}
	public int getILPStatusCode() {
		return ILPStatusCode;
	}
	public void setILPStatusCode(int iLPStatusCode) {
		ILPStatusCode = iLPStatusCode;
	}
	public int getStructuralDisplayCount() {
		return StructuralDisplayCount;
	}
	public void setStructuralDisplayCount(int structuralDisplayCount) {
		StructuralDisplayCount = structuralDisplayCount;
	}
	public int getSampleDisplayCount() {
		return SampleDisplayCount;
	}
	public void setSampleDisplayCount(int sampleDisplayCount) {
		SampleDisplayCount = sampleDisplayCount;
	}
	public int getActualDisplayCount() {
		return ActualDisplayCount;
	}
	public void setActualDisplayCount(int actualDisplayCount) {
		ActualDisplayCount = actualDisplayCount;
	}
	public int getTotalCasesNotExecuted() {
		return TotalCasesNotExecuted;
	}
	public void setTotalCasesNotExecuted(int totalCasesNotExecuted) {
		TotalCasesNotExecuted = totalCasesNotExecuted;
	}
	public float getCovPercentile() {
		return CovPercentile;
	}
	public void setCovPercentile(float covPercentile) {
		CovPercentile = covPercentile;
	}
	public int getTotalStructuralTestcasesFailed() {
		return TotalStructuralTestcasesFailed;
	}
	public void setTotalStructuralTestcasesFailed(int totalStructuralTestcasesFailed) {
		TotalStructuralTestcasesFailed = totalStructuralTestcasesFailed;
	}
	public int getTotalLogicalTestCasesFailed() {
		return TotalLogicalTestCasesFailed;
	}
	public void setTotalLogicalTestCasesFailed(int totalLogicalTestCasesFailed) {
		TotalLogicalTestCasesFailed = totalLogicalTestCasesFailed;
	}
	public int getTotalSampleTestcases() {
		return TotalSampleTestcases;
	}
	public void setTotalSampleTestcases(int totalSampleTestcases) {
		TotalSampleTestcases = totalSampleTestcases;
	}
	public String getStatusDescription() {
		return StatusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.StatusDescription = statusDescription;
	}
	private int TotalSampleTestcases ;
}

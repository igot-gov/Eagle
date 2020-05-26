/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.exercise.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.infosys.lex.exercise.dto.JsonNumericalVerificationData;

@Service
public class PythonVerificationUtilServiceImpl implements PythonVerificationUtilService {
	
	/*
	 * 
	 * PYTHON VERIFICATION PROCESSOR (START)
	 * --------------------------------------------------------------
	 * 
	 */

	@Override
	public String analysePythonVerificationResponse(JSONObject resp) throws JSONException {
		JSONObject responseData = new JSONObject();
		JsonNumericalVerificationData numData = new JsonNumericalVerificationData();
		try {

			JSONObject resultSumJson = new JSONObject(String.valueOf(resp.get("ResultSummary")));
			String statusCode = String.valueOf(resultSumJson.get("StatusCode"));
//				0	Success
//				-1	Technical Error in PyEval Tool
//				-2	Improper Trainee Code
//				-3	Improper JSON Keys
//				-4	Improper JSON Structure

			JSONArray testResults = new JSONArray();
			int failedCasesLogical = 0;
			int numberOfFailedTestCasesToShowInSample = 0;
			int logicalTestCaseCount = 0;
			int structuralTestCaseCount = 0;
			int logicalTestCasesPassed = 0;
			int logicalTestCasesFailed = 0;
			int structuralTestCasesPassed = 0;
			int structuralTestCasesFailed = 0;
			int actualLogicalTestNodeCount = 0;
			int actualStructuralTestNodeCount = 0;
			int totalTestCases = 0;
			int totalCasesPassed = 0;
			// int casesNotExecuted = 0;
			int serialNo = 1;
			int totalSampleTestcases = 0;
			List<Integer> failedTests = new ArrayList<Integer>();
			List<Integer> passedLogicalSampleTests = new ArrayList<Integer>();
			List<Integer> logicalFailedTests = new ArrayList<Integer>();
			JSONArray structuralTestDetails = new JSONArray();
			JSONArray samplelogicalTestDetails = new JSONArray();
			JSONArray actualLogicalTestDetails = new JSONArray();
			JSONArray tempLogicalDetails = resp.getJSONArray("LogicalDetails");
			JSONArray jsonTempData = new JSONArray();

			JSONObject codeAnalyse = new JSONObject(String.valueOf(resp.get("CodeAnalyzerDetails")));

			JSONArray codeAnalyzerDetails = this.formatCodeAnalyserDetailList(codeAnalyse);

			for (int i = 0; i < tempLogicalDetails.length(); i++) {
				JSONObject testcaseJson = tempLogicalDetails.getJSONObject(i);
				if (String.valueOf(testcaseJson.get("TCType")).equals("Actual")) {
					jsonTempData.put(testcaseJson);
				}

			}

			// final testcase count analysed (start)
			structuralTestCasesPassed = Integer.valueOf(String.valueOf(resultSumJson.get("NumberPassedStructuralTC")));
			structuralTestCasesFailed = Integer.valueOf(String.valueOf(resultSumJson.get("NumberFailedStructuralTC")));
			logicalTestCasesPassed = Integer.valueOf(String.valueOf(resultSumJson.get("NumberPassedActualTC")));
			logicalTestCasesFailed = Integer.valueOf(String.valueOf(resultSumJson.get("NumberFailedActualTC")));

			logicalTestCaseCount = logicalTestCasesPassed + logicalTestCasesFailed;
			structuralTestCaseCount = structuralTestCasesPassed + structuralTestCasesFailed;

			totalCasesPassed = logicalTestCasesPassed + structuralTestCasesPassed;
			totalTestCases = logicalTestCaseCount + structuralTestCaseCount;
			// testcase count analyse(end)

			/*
			 * Structural testcase parsing (start)
			 */
			// setting i to 0 again.
			JSONArray structuralTestCaseDetails = resp.getJSONArray("StructuralDetails");
			structuralTestCaseCount = structuralTestCaseDetails.length();
			actualStructuralTestNodeCount = structuralTestCaseDetails.length();

			if (structuralTestCaseCount > 0) {
				// failedTest = new int[sampleStructuralTestCount];
				for (int i = 0; i < actualStructuralTestNodeCount; i++) {
					JSONObject structuralTestcase = structuralTestCaseDetails.getJSONObject(i);
					if (!String.valueOf(structuralTestcase.get("TCStatus")).equals("Pass")) {

						structuralTestDetails.put(structuralTestCaseDetails.getJSONObject(i));
						failedTests.add(i);
					}
					if (failedTests.size() >= structuralTestCaseCount)
						break;
				}

				int i = 0;
				// In this loop all passed testcases are added to the end of structural testcase
				// array.
				while (structuralTestDetails.length() < structuralTestCaseCount) {

					if (!failedTests.contains(i)) {
						structuralTestDetails.put(structuralTestCaseDetails.getJSONObject(i));
					}
					i++;
				}

				// this loop formats the final list of structural testcases.
				for (i = 0; i < structuralTestDetails.length(); i++) {
					JSONObject result = new JSONObject();

					result.put("SAType", "N/A");
					result.put("Type", "Structural");
					result.put("SNo", serialNo++);
					JSONObject structuralTestcase = structuralTestDetails.getJSONObject(i);
					if (!String.valueOf(structuralTestcase.get("TCClass")).toLowerCase().equals("none")
							&& structuralTestcase.has("TCClass") && !(structuralTestcase.isNull("TCClass"))) {
						result.put("TestTarget", String.valueOf(structuralTestcase.get("TCClass")) + "\n"
								+ String.valueOf(structuralTestcase.get("TCTarget")));

					} else {
						result.put("TestTarget", String.valueOf(structuralTestcase.get("TCTarget")));
					}

					result.put("Input", "NA");
					result.put("Expected", "NA");
					result.put("Actual", "NA");

					// result.StatusCode = 1;
					result.put("Result", String.valueOf(structuralTestcase.get("TCStatus")));
					result.put("TCTips", String.valueOf(structuralTestcase.get("TCTips")));
					testResults.put(result);
				}
			}

			/*
			 * Structural testcases parsing (end)
			 */

			// 30% of total number of logical test cases to be displayed as sample
			if (jsonTempData.length() != 0) {
				if (logicalTestCaseCount > 0) {
					failedCasesLogical = logicalTestCaseCount - logicalTestCasesPassed;
					JSONArray logicalTestCaseDetails = jsonTempData;
					actualLogicalTestNodeCount = logicalTestCaseDetails.length();

					// 35% of total logical test cases to be shown as sample
					int maxToShow = (int) (Math.floor(actualLogicalTestNodeCount * 0.35));
					int sampleLogicalTestCount = maxToShow < 5 ? maxToShow : 5;

					// This is done when maxToShow/actualLogicalTestNodeCount turns out to be 0. We
					// do ceil instead of floor
					if (sampleLogicalTestCount == 0) {
						sampleLogicalTestCount = (int) (Math.ceil(actualLogicalTestNodeCount * 0.35));
					}

					// now for actual show minimum of sample logical test count and failed test
					// cases
					int actualLogicalTestCount = sampleLogicalTestCount < (logicalTestCaseCount
							- logicalTestCasesPassed) ? sampleLogicalTestCount
									: (logicalTestCaseCount - logicalTestCasesPassed);
					// actualLogicalTestCount = 2;

					// if all Logical test case pass
					if (logicalTestCasesPassed == logicalTestCaseCount) {
						if (logicalTestCaseCount <= 5) {
							sampleLogicalTestCount = 1;
							actualLogicalTestCount = 1;
						} else if (logicalTestCaseCount > 5 && logicalTestCaseCount <= 10) {
							sampleLogicalTestCount = 2;
							actualLogicalTestCount = 2;
						} else if (logicalTestCaseCount > 10) {
							sampleLogicalTestCount = 5;
							actualLogicalTestCount = 5;
						}
					}

					// if all test cases fail and number of logical test cases exceed 10 then
					// display only seven failed test cases.
					// Modified sample and actual count.
					// (Here actualLogicalTestCount and sampleLogicalTestcount is equated with 5
					// because their count will remain same and their sum should be equal t0 10
					if (logicalTestCasesPassed == 0 && actualLogicalTestCount == 5 && sampleLogicalTestCount == 5) {
						sampleLogicalTestCount = 2;
						actualLogicalTestCount = 5;
					}

					// if number of test cases exceeds sample test cases to be shown, then include
					// one failed test case in sample.
					if (sampleLogicalTestCount < failedCasesLogical && logicalTestCasesPassed > 0
							&& sampleLogicalTestCount > 1) {
						numberOfFailedTestCasesToShowInSample = 1;
					}

					failedTests.clear();
					for (int i = 0; i < actualLogicalTestNodeCount; i++) {

						JSONObject logicalTestcase = logicalTestCaseDetails.getJSONObject(i);
						if (String.valueOf(logicalTestcase.get("TCStatus")).equals("Pass")) {
							passedLogicalSampleTests.add(i);
							// add
						}

						if (passedLogicalSampleTests
								.size() == (sampleLogicalTestCount - numberOfFailedTestCasesToShowInSample))
							break;
					}
					if (passedLogicalSampleTests.size() > 0) {
						for (int pos : passedLogicalSampleTests) {
							samplelogicalTestDetails.put(logicalTestCaseDetails.getJSONObject(pos));
							// add
						}
					}
					int i = 0;
					while (samplelogicalTestDetails.length() < sampleLogicalTestCount) {

						if (!passedLogicalSampleTests.contains(i)) {
							// here we are storing the failed test cases that are going in as sample test
							// cases so that they don't get repeated when we display
							// failed test cases in actual.
							logicalFailedTests.add(i);
							samplelogicalTestDetails.put(logicalTestCaseDetails.getJSONObject(i));
						}
						i++;
					}
					serialNo = addTestCases(samplelogicalTestDetails, "Sample", serialNo, testResults);

					if (actualLogicalTestCount > 0) {
						for (i = 0; i < actualLogicalTestNodeCount; i++) {
							JSONObject logicalTestcase = logicalTestCaseDetails.getJSONObject(i);
							if (!(String.valueOf(logicalTestcase.get("TCStatus")).equals("Pass"))
									&& (!logicalFailedTests.contains(i))) {
								// we are storing the failed logical test cases that have not been displayed in
								// sample part
								failedTests.add(i);
							}
							if (failedTests.size() == actualLogicalTestCount)
								break;
						}
						if (failedTests.size() > 0) {
							for (int pos : failedTests) {
								actualLogicalTestDetails.put(logicalTestCaseDetails.getJSONObject(pos));
							}
						}
						i = 0;
						while (actualLogicalTestDetails.length() < actualLogicalTestCount) {

							if (!failedTests.contains(i) && !logicalFailedTests.contains(i)
									&& !passedLogicalSampleTests.contains(i)) {
								actualLogicalTestDetails.put(logicalTestCaseDetails.getJSONObject(i));

							}
							i++;
						}
						serialNo = addTestCases(actualLogicalTestDetails, "Actual", serialNo, testResults);
					}
				}
			}

			numData.setTotalLogicalTestCases(logicalTestCaseCount);
			numData.setTotalStructuralTestCases(structuralTestCaseCount);
			numData.setLogicalTestCasesPassed(logicalTestCasesPassed);
			numData.setStructuralTestCasesPassed(structuralTestCasesPassed);
			numData.setTotalTestCases(totalTestCases);
			numData.setTotalCasesPassed(totalCasesPassed);
			numData.setILPStatusCode(Integer.valueOf(statusCode));
			numData.setActualDisplayCount(actualLogicalTestDetails.length());
			numData.setStructuralDisplayCount(structuralTestDetails.length());
			numData.setSampleDisplayCount(samplelogicalTestDetails.length());
			// numData.TotalCasesNotExecuted = casesNotExecuted;
			numData.setTotalSampleTestcases(totalSampleTestcases);
			numData.setTotalStructuralTestcasesFailed(structuralTestCasesFailed);
			numData.setTotalLogicalTestCasesFailed(logicalTestCasesFailed);
			Gson gson = new Gson();
			responseData.put("NumericalStatistics", new JSONObject(gson.toJson(numData)));
			responseData.put("TestResultData", new JSONArray(testResults.toString()));
			responseData.put("CodeAnalyzerDetails", new JSONArray(codeAnalyzerDetails.toString()));

		} catch (Exception e) {
			numData.setILPStatusCode(-57);
			Gson gson = new Gson();
			responseData.put("NumericalStatistics", new JSONObject(gson.toJson(numData)));
			// responseData += JsonConvert.SerializeObject(e.StackTrace + ""+ e.Source);
			numData.setStatusDescription("Error occured while parsing the pyeval verification.");
			responseData.put("TestResultData", new JSONArray());
			return responseData.toString();
		}
		return responseData.toString();
	}

	private int addTestCases(JSONArray logicalTestDetails, String type, int serialNo, JSONArray resultData)
			throws JSONException {
		for (int i = 0; i < logicalTestDetails.length(); i++) {
			JSONObject result = new JSONObject();

			result.put("SAType", type);
			result.put("Type", "Logical");
			result.put("SNo", serialNo++);

			JSONObject actualLogicalTestcase = logicalTestDetails.getJSONObject(i);

			if (!(actualLogicalTestcase.isNull("TCClass")
					|| String.valueOf(actualLogicalTestcase.get("TCClass")).isEmpty())) {

				result.put("TestTarget", String.valueOf(actualLogicalTestcase.get("TCClass")) + "\n"
						+ String.valueOf(actualLogicalTestcase.get("TCMethod")));

			} else {

				result.put("TestTarget", String.valueOf(actualLogicalTestcase.get("TCMethod")));
			}

			result.put("Input", String.valueOf(actualLogicalTestcase.get("TCInput")));
			result.put("Expected", "N/A");
			if (type.toLowerCase().equals("sample"))
				result.put("Expected", String.valueOf(actualLogicalTestcase.get("TCExpected")));
			result.put("Actual", String.valueOf(actualLogicalTestcase.get("TCActual")));
			result.put("Result", String.valueOf(actualLogicalTestcase.get("TCStatus")));

			// result.Result = actualLogicalTestDetails[i]["TC"].ToString();
			// result.StatusCode = 1;
			resultData.put(result);

		}
		return serialNo;
	}

	/**
	 * In this method the pyeval tool response is parsed and all the testcases are
	 * formatted and returned.
	 * 
	 * @param resp
	 * @return
	 * @throws JSONException
	 */
	@Override
	public String verifySolutionToolAnalyzeForPreview(JSONObject resp) throws JSONException {
		JSONObject responseData = new JSONObject();
		JsonNumericalVerificationData numData = new JsonNumericalVerificationData();
		try {

			JSONObject resultSumJson = new JSONObject(String.valueOf(resp.get("ResultSummary")));
			String statusCode = String.valueOf(resultSumJson.get("StatusCode"));
			JSONArray testResults = new JSONArray();
			int logicalTestCaseCount = 0;
			int structuralTestCaseCount = 0;
			int logicalTestCasesPassed = 0;
			int logicalTestCasesFailed = 0;
			int structuralTestCasesPassed = 0;
			int structuralTestCasesFailed = 0;
			int totalTestCases = 0;
			int totalCasesPassed = 0;
			// int casesNotExecuted = 0;
			int serialNo = 1;
			int totalSampleTestcases = 0;
			JSONArray structuralTestDetails = new JSONArray();
			JSONArray samplelogicalTestDetails = new JSONArray();
			JSONArray actualLogicalTestDetails = new JSONArray();
			JSONArray tempLogicalDetails = resp.getJSONArray("LogicalDetails");

			JSONObject codeAnalyse = new JSONObject(String.valueOf(resp.get("CodeAnalyzerDetails")));

			JSONArray codeAnalyzerDetails = this.formatCodeAnalyserDetailList(codeAnalyse);

			for (int i = 0; i < tempLogicalDetails.length(); i++) {
				JSONObject testcaseJson = tempLogicalDetails.getJSONObject(i);
				if (String.valueOf(testcaseJson.get("TCType")).equals("Actual")) {
					actualLogicalTestDetails.put(testcaseJson);
				}

			}

			// final testcase conunt analysed (start)
			structuralTestCasesPassed = Integer.valueOf(String.valueOf(resultSumJson.get("NumberPassedStructuralTC")));
			structuralTestCasesFailed = Integer.valueOf(String.valueOf(resultSumJson.get("NumberFailedStructuralTC")));
			logicalTestCasesPassed = Integer.valueOf(String.valueOf(resultSumJson.get("NumberPassedActualTC")));
			logicalTestCasesFailed = Integer.valueOf(String.valueOf(resultSumJson.get("NumberFailedActualTC")));

			logicalTestCaseCount = logicalTestCasesPassed + logicalTestCasesFailed;
			structuralTestCaseCount = structuralTestCasesPassed + structuralTestCasesFailed;

			totalCasesPassed = logicalTestCasesPassed + structuralTestCasesPassed;
			totalTestCases = logicalTestCaseCount + structuralTestCaseCount;
			// testcase count analyse(end)

			/*
			 * Structural testcase parsing (start)
			 */
			// setting i to 0 again.
			JSONArray structuralTestCaseDetails = resp.getJSONArray("StructuralDetails");
			structuralTestCaseCount = structuralTestCaseDetails.length();

			if (structuralTestDetails.length() > 0 && structuralTestCaseCount > 0) {

				// this loop formats the final list of structural testcases.
				for (int i = 0; i < structuralTestCaseDetails.length(); i++) {
					JSONObject result = new JSONObject();

					result.put("SAType", "N/A");
					result.put("Type", "Structural");
					result.put("SNo", serialNo++);
					JSONObject structuralTestcase = structuralTestCaseDetails.getJSONObject(i);
					if (!String.valueOf(structuralTestcase.get("TCClass")).toLowerCase().equals("none")
							&& structuralTestcase.has("TCClass") && !(structuralTestcase.isNull("TCClass"))) {
						result.put("TestTarget", String.valueOf(structuralTestcase.get("TCClass")) + "\n"
								+ String.valueOf(structuralTestcase.get("TCTarget")));

					} else {
						result.put("TestTarget", String.valueOf(structuralTestcase.get("TCTarget")));
					}

					result.put("Input", "NA");
					result.put("Expected", "NA");
					result.put("Actual", "NA");

					// result.StatusCode = 1;
					result.put("Result", String.valueOf(structuralTestcase.get("TCStatus")));
					result.put("TCTips", String.valueOf(structuralTestcase.get("TCTips")));
					testResults.put(result);
				}
			}

			/*
			 * Structural testcases parsing (end)
			 */

			if (actualLogicalTestDetails.length() != 0 && logicalTestCaseCount > 0) {
				addTestCasesForPreview(actualLogicalTestDetails, serialNo, testResults);

			}

			numData.setTotalLogicalTestCases(logicalTestCaseCount);
			numData.setTotalStructuralTestCases(structuralTestCaseCount);
			numData.setLogicalTestCasesPassed(logicalTestCasesPassed);
			numData.setStructuralTestCasesPassed(structuralTestCasesPassed);
			numData.setTotalTestCases(totalTestCases);
			numData.setTotalCasesPassed(totalCasesPassed);
			numData.setILPStatusCode(Integer.valueOf(statusCode));
			numData.setActualDisplayCount(actualLogicalTestDetails.length());
			numData.setStructuralDisplayCount(structuralTestDetails.length());
			numData.setSampleDisplayCount(samplelogicalTestDetails.length());
			// numData.TotalCasesNotExecuted = casesNotExecuted;
			numData.setTotalSampleTestcases(totalSampleTestcases);
			numData.setTotalStructuralTestcasesFailed(structuralTestCasesFailed);
			numData.setTotalLogicalTestCasesFailed(logicalTestCasesFailed);
			Gson gson = new Gson();
			responseData.put("NumericalStatistics", new JSONObject(gson.toJson(numData)));
			responseData.put("TestResultData", new JSONArray(testResults.toString()));
			responseData.put("CodeAnalyzerDetails", new JSONArray(codeAnalyzerDetails.toString()));

		} catch (Exception e) {
			numData.setILPStatusCode(-57);
			Gson gson = new Gson();
			responseData.put("NumericalStatistics", new JSONObject(gson.toJson(numData)));
			// responseData += JsonConvert.SerializeObject(e.StackTrace + ""+ e.Source);
			numData.setStatusDescription("Error occured while parsing the pyeval verification.");
			responseData.put("TestResultData", new JSONArray());
			return responseData.toString();
		}
		return responseData.toString();
	}

	/**
	 * This method formats testcase for author preview in which case expected result
	 * is displayed
	 * 
	 * @param logicalTestDetails
	 * @param type
	 * @param serialNo
	 * @param resultData
	 * @return
	 * @throws JSONException
	 */
	private int addTestCasesForPreview(JSONArray logicalTestDetails, int serialNo, JSONArray resultData)
			throws JSONException {
		for (int i = 0; i < logicalTestDetails.length(); i++) {
			JSONObject result = new JSONObject();

			result.put("SAType", "Actual");
			result.put("Type", "Logical");
			result.put("SNo", serialNo++);

			JSONObject actualLogicalTestcase = logicalTestDetails.getJSONObject(i);

			if (!(actualLogicalTestcase.isNull("TCClass")
					|| String.valueOf(actualLogicalTestcase.get("TCClass")).isEmpty())) {

				result.put("TestTarget", String.valueOf(actualLogicalTestcase.get("TCClass")) + "\n"
						+ String.valueOf(actualLogicalTestcase.get("TCMethod")));

			} else {

				result.put("TestTarget", String.valueOf(actualLogicalTestcase.get("TCMethod")));
			}

			result.put("Input", String.valueOf(actualLogicalTestcase.get("TCInput")));
			result.put("Expected", "N/A");
//			if (type.toLowerCase().equals("sample"))//This check isn't done for preview as expected result is displayed
			result.put("Expected", String.valueOf(actualLogicalTestcase.get("TCExpected")));
			result.put("Actual", String.valueOf(actualLogicalTestcase.get("TCActual")));
			result.put("Result", String.valueOf(actualLogicalTestcase.get("TCStatus")));

			// result.Result = actualLogicalTestDetails[i]["TC"].ToString();
			// result.StatusCode = 1;
			resultData.put(result);

		}
		return serialNo;
	}

	@SuppressWarnings("unchecked")
	private JSONArray formatCodeAnalyserDetailList(JSONObject codeAnalyse) throws JSONException {
		Iterator<String> keys = codeAnalyse.keys();

		JSONArray codeAnalyzerDetails = new JSONArray();
		while (keys.hasNext()) {
			String key = keys.next();
			JSONObject anaResult = new JSONObject();

			anaResult.put("RID", key);
			JSONObject ruleJSON = new JSONObject(String.valueOf(codeAnalyse.get(key)));

			anaResult.put("marks",
					(ruleJSON.has("marks") && !ruleJSON.isNull("marks")) ? String.valueOf(ruleJSON.get("marks"))
							: null);

			anaResult.put("Status", (ruleJSON.has("Status") || ruleJSON.isNull("Status"))

					? String.valueOf(ruleJSON.get("Status"))
					: null);

			anaResult.put("Violation",
					(ruleJSON.has("Violation") || ruleJSON.isNull("Violation"))
							? String.valueOf(ruleJSON.get("Violation"))
							: null);

			anaResult.put("Name",
					(ruleJSON.has("Name") || ruleJSON.isNull("Name")) ? String.valueOf(ruleJSON.get("Name")) : null);

			anaResult.put("Description",
					(ruleJSON.has("Description") || ruleJSON.isNull("Description"))
							? String.valueOf(ruleJSON.get("Description"))
							: null);

			anaResult.put("Suggestion",
					(ruleJSON.has("Suggestion") || ruleJSON.isNull("Suggestion"))
							? String.valueOf(ruleJSON.get("Suggestion"))
							: null);

			codeAnalyzerDetails = codeAnalyzerDetails.put(anaResult);

		}

		return codeAnalyzerDetails;
	}

	/*
	 * 
	 * 
	 * PYTHON VERIFICATION PROCESSOR (END)
	 * -----------------------------------------------------------------------------
	 * 
	 */


}

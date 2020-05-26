/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.exercise.util;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class MultiLangVerificationUtilServiceImpl implements MultiLangVerificationUtilService {
	
	/*
	 * 
	 * MULTI LANGUAGE VERIFICATION PROCESSOR (START
	 * 
	 * 
	 */

	/**
	 * In this method ispreview is used to indicate that used for authoring tool
	 * preview in whiich case both all testcases and filtered testcases are passed.
	 * 
	 */
	@Override
	public JSONObject analyseVerifcationWithExpectedOutput(JSONObject testcaseArray, String verificationResponse,
			boolean isPreview) throws JSONException {
		JSONObject resp = new JSONObject(verificationResponse);

		resp = analyseAllLangVerification(resp);
		addExpectedOutputToTestcases(testcaseArray, resp, isPreview);
		if (!isPreview && resp.has("allTestCaseOutputs"))
			resp.remove("allTestCaseOutputs");
		return resp;
	}

	/*
	 * Adds expected output to final testcases and all testcase in case its for
	 * authoring tool preview
	 */
	private void addExpectedOutputToTestcases(JSONObject testcaseArray, JSONObject resp, boolean isPreview)
			throws JSONException {
		JSONArray verifiedTestcases = resp.getJSONArray("testCaseOutputs");
		JSONObject testcase = null;
		for (int i = 0; i < verifiedTestcases.length(); i++) {
			JSONObject verifiedTestcase = verifiedTestcases.getJSONObject(i);
			String type = String.valueOf(verifiedTestcase.get("type"));
			if (type.toLowerCase().equals("sample")) {
				JSONArray sampleArray = testcaseArray.getJSONArray("sample");
				for (int j = 0; j < sampleArray.length(); j++) {

					testcase = sampleArray.getJSONObject(j);
					if (String.valueOf(verifiedTestcase.get("id")).equals(String.valueOf(testcase.get("id")))) {
						verifiedTestcase.put("expectedOutput", testcase.get("output"));
						break;
					}
				}
			} else {
				JSONArray hiddenArray = testcaseArray.getJSONArray("hidden");
				for (int j = 0; j < hiddenArray.length(); j++) {

					testcase = hiddenArray.getJSONObject(j);
					if (String.valueOf(verifiedTestcase.get("id")).equals(String.valueOf(testcase.get("id")))) {
						verifiedTestcase.put("expectedOutput", testcase.get("output"));
					}
				}
			}
		}

		if (isPreview) {
			resp.put("allTestCaseOutputs", new JSONArray(verifiedTestcases.toString()));
		}
	}

	private JSONObject analyseAllLangVerification(JSONObject resp) throws JSONException {
		int samplePassed = 0;
		int sampleFailed = 0;
		int hiddenPassed = 0;
		int hiddenFailed = 0;
		JSONArray filteredTestcases = new JSONArray();
		JSONArray hiddenPassedTestcases = new JSONArray();
		JSONArray hiddenFailedTestcases = new JSONArray();
		JSONArray allTestcases = resp.getJSONArray("testCaseOutputs");
		for (int i = 0; i < allTestcases.length(); i++) {
			JSONObject testcase = allTestcases.getJSONObject(i);
			if (String.valueOf(testcase.get("type")).toLowerCase().equals("sample")) {
				filteredTestcases.put(testcase);
				if (String.valueOf(testcase.get("result")).toLowerCase().equals("passed"))
					samplePassed++;
				else
					sampleFailed++;
			} else {
				if (String.valueOf(testcase.get("result")).toLowerCase().equals("passed")) {
					hiddenPassedTestcases.put(testcase);
					hiddenPassed++;
				} else {
					hiddenFailedTestcases.put(testcase);
					hiddenFailed++;
				}
			}

		}

		int no_of_hidden_passed_to_show = (int) Math.ceil(hiddenPassedTestcases.length() / 2.0);
		int no_of_hidden_failed_to_show = (int) Math.ceil(hiddenFailedTestcases.length() / 2.0);

		for (int i = 0; i < no_of_hidden_passed_to_show; i++) {
			filteredTestcases.put(hiddenPassedTestcases.getJSONObject(i));
		}

		for (int i = 0; i < no_of_hidden_failed_to_show; i++) {
			filteredTestcases.put(hiddenFailedTestcases.getJSONObject(i));

		}
		JSONObject verifySummary = new JSONObject();
		verifySummary.put("SampleTCPassed", samplePassed);
		verifySummary.put("HiddenTCPassed", hiddenPassed);
		verifySummary.put("HiddenTCFailed", hiddenFailed);
		verifySummary.put("SampleTCFailed", sampleFailed);

		resp.put("verifySummary", verifySummary);

		resp.put("testCaseOutputs", filteredTestcases);
		resp.put("allTestCaseOutputs", allTestcases);

		return resp;
	}

	/*
	 * 
	 * MULTI LANGUAGE VERIFICATTION PROCESSOR (END)
	 * 
	 */

}

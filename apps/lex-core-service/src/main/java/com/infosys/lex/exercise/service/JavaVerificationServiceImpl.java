/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at http-urls://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.exercise.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.ExerciseCodeVerifcationException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.exercise.bodhi.repo.ExerciseRepository;
import com.infosys.lex.exercise.dto.AssignmentSubmissionDTO;
import com.infosys.lex.exercise.dto.SubmitDataDTO;
import com.infosys.lex.exercise.util.JavaVerificationUtilService;

@Service
@Qualifier("javaVerification")
public class JavaVerificationServiceImpl implements VerificationService {

	private final ExerciseRepository exerciseRepo;

	private final UserUtilityService userUtilService;

	private final LexServerProperties lexServerProperties;

	private final JavaVerificationUtilService utilService;

	private final ContentService contentServ;

	private final IAPVerificationService iapVerificationService;

	@Autowired
	public JavaVerificationServiceImpl(ExerciseRepository exerciseRepo, UserUtilityService userUtilService,
			LexServerProperties lexServerProperties, JavaVerificationUtilService exerciseUtilService,
			ContentService contentServ, IAPVerificationService iapVerificationService) {
		this.exerciseRepo = exerciseRepo;
		this.userUtilService = userUtilService;
		this.lexServerProperties = lexServerProperties;
		this.utilService = exerciseUtilService;
		this.contentServ = contentServ;
		this.iapVerificationService = iapVerificationService;
	}

	@Override
	public Map<String, Object> executeVerification(String rootOrg, AssignmentSubmissionDTO submittedData)
			throws Exception {

		if (!userUtilService.validateUser(rootOrg, submittedData.getUserId())) {
			throw new BadRequestException("Invalid User : " + submittedData.getUserId());
		}

		Map<String, Object> output = new HashMap<String, Object>();

		exerciseRepo.validateExerciseID(submittedData.getResourceId());

		output = this.verifyJavaAssignment(submittedData);
		return output;

	}

	/*
	 * This method creates the submitdata object containing brief summary of the
	 * verfiication result
	 */
	private SubmitDataDTO getVerifyStatus(JSONObject verifyResJson) throws Exception {
		SubmitDataDTO submitData = new SubmitDataDTO();

		JSONObject resultSummary = verifyResJson.getJSONObject("resultSummary");
		int totalTestcases = resultSummary.getInt("failedProceduralActualTC")
				+ resultSummary.getInt("passedStructuralTC") + resultSummary.getInt("passedProceduralSampleTC")
				+ resultSummary.getInt("passedProceduralActualTC") + resultSummary.getInt("failedStructuralTC")
				+ resultSummary.getInt("failedProceduralSampleTC");
		
		int totalTestcasesPassed = resultSummary.getInt("passedStructuralTC")
				+ resultSummary.getInt("passedProceduralSampleTC") + resultSummary.getInt("passedProceduralActualTC");
		
		float resultPercent = 0f;
		if (totalTestcases != 0)
			resultPercent = (float) (totalTestcasesPassed / totalTestcases * 100);

		submitData.setTotal_testcases(totalTestcases);
		submitData.setTestcases_passed(totalTestcasesPassed);
		submitData.setTestcases_failed(totalTestcases - totalTestcasesPassed);
		submitData.setResult_percent(resultPercent);

		return submitData;

	}

	/*
	 * In this method testcases with solution is sent to java evaluation tool and
	 * response is parsed returned.
	 */
	private Map<String, Object> verifyJavaAssignment(AssignmentSubmissionDTO data) throws Exception {

		String verifyJson;
//		verifyJson = "{\r\n"
//				+ "    \"traineeSolution\": \"package abstractex;\\r\\n\\r\\npublic class GraduateStudent extends Student {\\r\\n\\r\\n\\tpublic GraduateStudent(String name) {\\r\\n\\t\\tsuper(name);\\r\\n\\t}\\r\\n\\r\\n\\tpublic void generateResult() {\\r\\n\\t\\tint sum = 0;\\r\\n\\t\\tfor (int score : this.getTest()) {\\r\\n\\t\\t\\tsum += score;\\r\\n\\t\\t}\\r\\n\\t\\tfloat average = sum/this.getTest().length;\\r\\n\\t\\tif (average >= 70) {\\r\\n\\t\\t\\tthis.setResult(\\\"Pass\\\");\\r\\n\\t\\t}\\r\\n\\t\\telse {\\r\\n\\t\\t\\tthis.setResult(\\\"Fail\\\");\\r\\n\\t\\t}\\r\\n\\t}\\r\\n}\\r\\n\\n//package abstractex;\\r\\n\\r\\nabstract public class Student {\\r\\n\\r\\n\\tprivate String name;\\r\\n\\tprivate int[] test;\\r\\n\\tprivate String result;\\r\\n\\t\\r\\n\\tpublic Student(String name) {\\r\\n\\t\\tthis.name = name;\\r\\n\\t\\tthis.test = new int[4];\\r\\n\\t}\\r\\n\\t\\r\\n\\tpublic abstract void generateResult();\\r\\n\\t\\r\\n\\tpublic void setTestScore(int testNumber, int testScore) {\\r\\n\\t\\ttest[testNumber] = testScore;\\r\\n\\t}\\r\\n\\t\\r\\n\\tpublic String getName() {\\r\\n\\t\\treturn name;\\r\\n\\t}\\r\\n\\tpublic void setName(String name) {\\r\\n\\t\\tthis.name = name;\\r\\n\\t}\\r\\n\\tpublic int[] getTest() {\\r\\n\\t\\treturn test;\\r\\n\\t}\\r\\n\\tpublic String getResult() {\\r\\n\\t\\treturn result;\\r\\n\\t}\\r\\n\\tpublic void setResult(String result) {\\r\\n\\t\\tthis.result = result;\\r\\n\\t}\\r\\n\\t\\r\\n}\\n//package abstractex;\\r\\n\\r\\npublic class UndergraduateStudent extends Student {\\r\\n\\r\\n\\tpublic UndergraduateStudent(String name) {\\r\\n\\t\\tsuper(name);\\r\\n\\t}\\r\\n\\t\\r\\n\\tpublic void generateResult() {\\r\\n\\t\\tint sum = 0;\\r\\n\\t\\tfor (int score : this.getTest()) {\\r\\n\\t\\t\\tsum += score;\\r\\n\\t\\t}\\r\\n\\t\\tfloat average = sum/this.getTest().length;\\r\\n\\t\\tif (average >= 60) {\\r\\n\\t\\t\\tthis.setResult(\\\"Pass\\\");\\r\\n\\t\\t}\\r\\n\\t\\telse {\\r\\n\\t\\t\\tthis.setResult(\\\"Fail\\\");\\r\\n\\t\\t}\\r\\n\\t}\\r\\n}\\r\\n\\n\",\r\n"
//				+ "    \"type\": \"all\",\r\n" + "    \"artifacts\": [\r\n" + "        {\r\n"
//				+ "            \"solutionFileContent\": \"package abstractex;\\r\\n\\r\\npublic class GraduateStudent extends Student {\\r\\n\\r\\n\\tpublic GraduateStudent(String name) {\\r\\n\\t\\tsuper(name);\\r\\n\\t}\\r\\n\\r\\n\\tpublic void generateResult() {\\r\\n\\t\\tint sum = 0;\\r\\n\\t\\tfor (int score : this.getTest()) {\\r\\n\\t\\t\\tsum += score;\\r\\n\\t\\t}\\r\\n\\t\\tfloat average = sum/this.getTest().length;\\r\\n\\t\\tif (average >= 70) {\\r\\n\\t\\t\\tthis.setResult(\\\"Pass\\\");\\r\\n\\t\\t}\\r\\n\\t\\telse {\\r\\n\\t\\t\\tthis.setResult(\\\"Fail\\\");\\r\\n\\t\\t}\\r\\n\\t}\\r\\n}\\r\\n\",\r\n"
//				+ "            \"testCaseFileContent\": \"package abstractex;\\r\\n\\r\\nimport org.testng.annotations.DataProvider;\\r\\nimport org.testng.annotations.Test;\\r\\nimport org.unitils.reflectionassert.ReflectionAssert;\\r\\n\\r\\n@Test(groups = \\\"GraduateStudentTest\\\")\\r\\npublic class GraduateStudentTest {\\r\\n\\r\\n\\tprivate GraduateStudent graduateInstance;\\r\\n\\r\\n\\t@DataProvider(name=\\\"dataProvider\\\")\\r\\n\\tprivate Object[][] createInput(){\\r\\n\\t\\t\\r\\n\\t\\tGraduateStudent graduateStudent1 = new GraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\tgraduateStudent1.setTestScore(0, 70);\\r\\n\\t\\tgraduateStudent1.setTestScore(1, 69);\\r\\n\\t\\tgraduateStudent1.setTestScore(2, 71);\\r\\n\\t\\tgraduateStudent1.setTestScore(3, 75);\\r\\n\\t\\t\\r\\n\\t\\tGraduateStudent graduateStudent2 = new GraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\tgraduateStudent2.setTestScore(0, 80);\\r\\n\\t\\tgraduateStudent2.setTestScore(1, 72);\\r\\n\\t\\tgraduateStudent2.setTestScore(2, 68);\\r\\n\\t\\tgraduateStudent2.setTestScore(3, 60);\\r\\n\\t\\t\\r\\n\\t\\tGraduateStudent graduateStudent3 = new GraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\tgraduateStudent3.setTestScore(0, 75);\\r\\n\\t\\tgraduateStudent3.setTestScore(1, 80);\\r\\n\\t\\tgraduateStudent3.setTestScore(2, 75);\\r\\n\\t\\tgraduateStudent3.setTestScore(3, 53);\\r\\n\\t\\t\\r\\n\\t\\tGraduateStudent graduateStudent4 = new GraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\tgraduateStudent4.setTestScore(0, 68);\\r\\n\\t\\tgraduateStudent4.setTestScore(1, 72);\\r\\n\\t\\tgraduateStudent4.setTestScore(2, 68);\\r\\n\\t\\tgraduateStudent4.setTestScore(3, 67);\\r\\n\\t\\t\\r\\n\\t\\tGraduateStudent graduateStudent5 = new GraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\tgraduateStudent5.setTestScore(0, 65);\\r\\n\\t\\tgraduateStudent5.setTestScore(1, 64);\\r\\n\\t\\tgraduateStudent5.setTestScore(2, 62);\\r\\n\\t\\tgraduateStudent5.setTestScore(3, 88);\\r\\n\\t\\t\\r\\n\\t\\tGraduateStudent expected1 = new GraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\texpected1.setTestScore(0, 70);\\r\\n\\t\\texpected1.setTestScore(1, 69);\\r\\n\\t\\texpected1.setTestScore(2, 71);\\r\\n\\t\\texpected1.setTestScore(3, 75);\\r\\n\\t\\texpected1.setResult(\\\"Pass\\\");\\r\\n\\t\\t\\r\\n\\t\\tGraduateStudent expected2 = new GraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\texpected2.setTestScore(0, 80);\\r\\n\\t\\texpected2.setTestScore(1, 72);\\r\\n\\t\\texpected2.setTestScore(2, 68);\\r\\n\\t\\texpected2.setTestScore(3, 60);\\r\\n\\t\\texpected2.setResult(\\\"Pass\\\");\\r\\n\\t\\t\\r\\n\\t\\tGraduateStudent expected3 = new GraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\texpected3.setTestScore(0, 75);\\r\\n\\t\\texpected3.setTestScore(1, 80);\\r\\n\\t\\texpected3.setTestScore(2, 75);\\r\\n\\t\\texpected3.setTestScore(3, 53);\\r\\n\\t\\texpected3.setResult(\\\"Pass\\\");\\r\\n\\t\\t\\r\\n\\t\\tGraduateStudent expected4 = new GraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\texpected4.setTestScore(0, 68);\\r\\n\\t\\texpected4.setTestScore(1, 72);\\r\\n\\t\\texpected4.setTestScore(2, 68);\\r\\n\\t\\texpected4.setTestScore(3, 67);\\r\\n\\t\\texpected4.setResult(\\\"Fail\\\");\\r\\n\\t\\t\\r\\n\\t\\tGraduateStudent expected5 = new GraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\texpected5.setTestScore(0, 65);\\r\\n\\t\\texpected5.setTestScore(1, 64);\\r\\n\\t\\texpected5.setTestScore(2, 62);\\r\\n\\t\\texpected5.setTestScore(3, 88);\\r\\n\\t\\texpected5.setResult(\\\"Fail\\\");\\r\\n\\t\\t\\r\\n\\t\\tObject[][] inputs=new Object[][]{\\r\\n\\t\\t\\t\\t{ graduateStudent1, expected1 }, { graduateStudent2, expected2 }, { graduateStudent3, expected3 }, { graduateStudent4, expected4 },\\r\\n\\t\\t\\t\\t{ graduateStudent5, expected5 }\\r\\n\\t\\t};\\r\\n\\t\\treturn inputs;\\r\\n\\r\\n\\t}\\r\\n\\t\\r\\n\\t@Test(alwaysRun=true,dataProvider=\\\"dataProvider\\\",timeOut=5000)\\r\\n\\tpublic void generateResultTest(GraduateStudent input, GraduateStudent expected) throws Exception{\\r\\n\\t\\tgraduateInstance = input;\\r\\n\\t\\tgraduateInstance.generateResult();\\r\\n\\t\\t\\r\\n\\t\\tReflectionAssert.assertReflectionEquals(expected, graduateInstance);\\r\\n\\t}\\r\\n}\\r\\n\",\r\n"
//				+ "            \"testCaseFileName\": \"GraduateStudentTest.java\",\r\n"
//				+ "            \"solutionFileName\": \"GraduateStudent.java\"\r\n" + "        },\r\n" + "        {\r\n"
//				+ "            \"solutionFileContent\": \"package abstractex;\\r\\n\\r\\nabstract public class Student {\\r\\n\\r\\n\\tprivate String name;\\r\\n\\tprivate int[] test;\\r\\n\\tprivate String result;\\r\\n\\t\\r\\n\\tpublic Student(String name) {\\r\\n\\t\\tthis.name = name;\\r\\n\\t\\tthis.test = new int[4];\\r\\n\\t}\\r\\n\\t\\r\\n\\tpublic abstract void generateResult();\\r\\n\\t\\r\\n\\tpublic void setTestScore(int testNumber, int testScore) {\\r\\n\\t\\ttest[testNumber] = testScore;\\r\\n\\t}\\r\\n\\t\\r\\n\\tpublic String getName() {\\r\\n\\t\\treturn name;\\r\\n\\t}\\r\\n\\tpublic void setName(String name) {\\r\\n\\t\\tthis.name = name;\\r\\n\\t}\\r\\n\\tpublic int[] getTest() {\\r\\n\\t\\treturn test;\\r\\n\\t}\\r\\n\\tpublic String getResult() {\\r\\n\\t\\treturn result;\\r\\n\\t}\\r\\n\\tpublic void setResult(String result) {\\r\\n\\t\\tthis.result = result;\\r\\n\\t}\\r\\n\\t\\r\\n}\",\r\n"
//				+ "            \"testCaseFileContent\": \"package abstractex;\\r\\n\\r\\nimport org.testng.annotations.BeforeMethod;\\r\\nimport org.testng.annotations.DataProvider;\\r\\nimport org.testng.annotations.Test;\\r\\nimport org.unitils.reflectionassert.ReflectionAssert;\\r\\n\\r\\n\\r\\n@Test(groups = \\\"StudentTest\\\")\\r\\npublic class StudentTest {\\r\\n\\r\\n//\\tprivate Student studentReference;\\r\\n\\tprivate GraduateStudent graduateStudentInstance;\\r\\n\\r\\n\\t@DataProvider(name=\\\"dataProvider1\\\")\\r\\n\\tprivate Object[][] createInputsValid(){\\r\\n\\t\\t\\r\\n\\t\\tint[] input1 = {0,78};\\r\\n\\t\\tint expected1 = 78;\\r\\n\\t\\t\\r\\n\\t\\tint[] input2 = {1,68};\\r\\n\\t\\tint expected2 = 68;\\r\\n\\t\\t\\r\\n\\t\\tint[] input3 = {2,99};\\r\\n\\t\\tint expected3 = 99;\\r\\n\\t\\t\\r\\n\\t\\tint[] input4 = {3,54};\\r\\n\\t\\tint expected4 = 54;\\r\\n\\t\\t\\r\\n\\t\\tObject[][] inputs=new Object[][]{\\r\\n\\t\\t\\t\\t{ input1, expected1 }, { input2, expected2 }, { input3, expected3 }, { input4, expected4 }\\r\\n\\t\\t};\\r\\n\\t\\treturn inputs;\\r\\n\\t}\\r\\n\\t\\r\\n\\t@DataProvider(name=\\\"dataProvider2\\\")\\r\\n\\tprivate Object[][] createInputsInvalid(){\\t\\r\\n\\t\\t\\r\\n\\t\\tint[] input1 = {4,54};\\r\\n\\t\\tint[] input2 = {5,54};\\r\\n\\t\\tint[] input3 = {6,54};\\r\\n\\t\\tint[] input4 = {-1,54};\\r\\n\\t\\t\\t\\r\\n\\t\\tObject[][] inputs=new Object[][]{\\r\\n\\t\\t\\t\\t{ input1 }, { input2 }, { input3 }, { input4 }\\r\\n\\t\\t};\\r\\n\\t\\treturn inputs;\\r\\n\\t}\\r\\n\\t\\r\\n\\t@BeforeMethod\\r\\n\\tpublic void init(){\\r\\n//\\t\\tstudentReference = new GraduateStudent(\\\"Tom\\\");\\r\\n\\t\\tgraduateStudentInstance = new GraduateStudent(\\\"Tom\\\");\\r\\n\\t}\\r\\n\\t\\r\\n\\t@Test(alwaysRun=true,dataProvider=\\\"dataProvider1\\\",timeOut=5000)\\r\\n\\tpublic void setTestScoreTest1(int[] input, int expected) throws Exception {\\r\\n\\r\\n//\\t\\tstudentReference.setTestScore(input[0], input[1]);\\r\\n//\\t\\tReflectionAssert.assertReflectionEquals(expected, studentReference.getTest()[input[0]]);\\r\\n\\t\\t\\r\\n\\t\\tgraduateStudentInstance.setTestScore(input[0], input[1]);\\r\\n\\t\\tReflectionAssert.assertReflectionEquals(expected, graduateStudentInstance.getTest()[input[0]]);\\r\\n\\t}\\r\\n\\t\\r\\n\\t@Test(alwaysRun=true,dataProvider=\\\"dataProvider2\\\", expectedExceptions = ArrayIndexOutOfBoundsException.class, timeOut=5000)\\r\\n\\tpublic void setTestScoreTest2(int[] input) throws Exception {\\r\\n\\r\\n//\\t\\tstudentReference.setTestScore(input[0], input[1]);\\r\\n\\t\\t\\r\\n\\t\\tgraduateStudentInstance.setTestScore(input[0], input[1]);\\r\\n\\t}\\r\\n\\t\\r\\n}\\r\\n\",\r\n"
//				+ "            \"testCaseFileName\": \"StudentTest.java\",\r\n"
//				+ "            \"solutionFileName\": \"Student.java\"\r\n" + "        },\r\n" + "        {\r\n"
//				+ "            \"solutionFileContent\": \"package abstractex;\\r\\n\\r\\npublic class UndergraduateStudent extends Student {\\r\\n\\r\\n\\tpublic UndergraduateStudent(String name) {\\r\\n\\t\\tsuper(name);\\r\\n\\t}\\r\\n\\t\\r\\n\\tpublic void generateResult() {\\r\\n\\t\\tint sum = 0;\\r\\n\\t\\tfor (int score : this.getTest()) {\\r\\n\\t\\t\\tsum += score;\\r\\n\\t\\t}\\r\\n\\t\\tfloat average = sum/this.getTest().length;\\r\\n\\t\\tif (average >= 60) {\\r\\n\\t\\t\\tthis.setResult(\\\"Pass\\\");\\r\\n\\t\\t}\\r\\n\\t\\telse {\\r\\n\\t\\t\\tthis.setResult(\\\"Fail\\\");\\r\\n\\t\\t}\\r\\n\\t}\\r\\n}\\r\\n\",\r\n"
//				+ "            \"testCaseFileContent\": \"package abstractex;\\r\\n\\r\\nimport org.testng.annotations.DataProvider;\\r\\nimport org.testng.annotations.Test;\\r\\nimport org.unitils.reflectionassert.ReflectionAssert;\\r\\n\\r\\n\\r\\n\\r\\n@Test(groups = \\\"UndergraduateStudentTest\\\")\\r\\npublic class UndergraduateStudentTest {\\r\\n\\r\\n\\tprivate UndergraduateStudent ugInstance;\\r\\n\\r\\n\\t@DataProvider(name=\\\"dataProvider\\\")\\r\\n\\tprivate Object[][] createInput(){\\r\\n\\t\\t\\r\\n\\t\\tUndergraduateStudent ugStudent1 = new UndergraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\tugStudent1.setTestScore(0, 70);\\r\\n\\t\\tugStudent1.setTestScore(1, 69);\\r\\n\\t\\tugStudent1.setTestScore(2, 71);\\r\\n\\t\\tugStudent1.setTestScore(3, 55);\\r\\n\\t\\t\\r\\n\\t\\tUndergraduateStudent ugStudent2 = new UndergraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\tugStudent2.setTestScore(0, 60);\\r\\n\\t\\tugStudent2.setTestScore(1, 62);\\r\\n\\t\\tugStudent2.setTestScore(2, 58);\\r\\n\\t\\tugStudent2.setTestScore(3, 60);\\r\\n\\t\\t\\r\\n\\t\\tUndergraduateStudent ugStudent3 = new UndergraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\tugStudent3.setTestScore(0, 43);\\r\\n\\t\\tugStudent3.setTestScore(1, 80);\\r\\n\\t\\tugStudent3.setTestScore(2, 55);\\r\\n\\t\\tugStudent3.setTestScore(3, 66);\\r\\n\\t\\t\\r\\n\\t\\tUndergraduateStudent ugStudent4 = new UndergraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\tugStudent4.setTestScore(0, 58);\\r\\n\\t\\tugStudent4.setTestScore(1, 62);\\r\\n\\t\\tugStudent4.setTestScore(2, 58);\\r\\n\\t\\tugStudent4.setTestScore(3, 60);\\r\\n\\t\\t\\r\\n\\t\\tUndergraduateStudent ugStudent5 = new UndergraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\tugStudent5.setTestScore(0, 43);\\r\\n\\t\\tugStudent5.setTestScore(1, 74);\\r\\n\\t\\tugStudent5.setTestScore(2, 52);\\r\\n\\t\\tugStudent5.setTestScore(3, 66);\\r\\n\\t\\t\\r\\n\\t\\tUndergraduateStudent expected1 = new UndergraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\texpected1.setTestScore(0, 70);\\r\\n\\t\\texpected1.setTestScore(1, 69);\\r\\n\\t\\texpected1.setTestScore(2, 71);\\r\\n\\t\\texpected1.setTestScore(3, 55);\\r\\n\\t\\texpected1.setResult(\\\"Pass\\\");\\r\\n\\t\\t\\r\\n\\t\\tUndergraduateStudent expected2 = new UndergraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\texpected2.setTestScore(0, 60);\\r\\n\\t\\texpected2.setTestScore(1, 62);\\r\\n\\t\\texpected2.setTestScore(2, 58);\\r\\n\\t\\texpected2.setTestScore(3, 60);\\r\\n\\t\\texpected2.setResult(\\\"Pass\\\");\\r\\n\\t\\t\\r\\n\\t\\tUndergraduateStudent expected3 = new UndergraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\texpected3.setTestScore(0, 43);\\r\\n\\t\\texpected3.setTestScore(1, 80);\\r\\n\\t\\texpected3.setTestScore(2, 55);\\r\\n\\t\\texpected3.setTestScore(3, 66);\\r\\n\\t\\texpected3.setResult(\\\"Pass\\\");\\r\\n\\t\\t\\r\\n\\t\\tUndergraduateStudent expected4 = new UndergraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\texpected4.setTestScore(0, 58);\\r\\n\\t\\texpected4.setTestScore(1, 62);\\r\\n\\t\\texpected4.setTestScore(2, 58);\\r\\n\\t\\texpected4.setTestScore(3, 60);\\r\\n\\t\\texpected4.setResult(\\\"Fail\\\");\\r\\n\\t\\t\\r\\n\\t\\tUndergraduateStudent expected5 = new UndergraduateStudent(\\\"Caroline\\\");\\r\\n\\t\\texpected5.setTestScore(0, 43);\\r\\n\\t\\texpected5.setTestScore(1, 74);\\r\\n\\t\\texpected5.setTestScore(2, 52);\\r\\n\\t\\texpected5.setTestScore(3, 66);\\r\\n\\t\\texpected5.setResult(\\\"Fail\\\");\\r\\n\\t\\t\\r\\n\\t\\tObject[][] inputs=new Object[][]{\\r\\n\\t\\t\\t\\t{ ugStudent1, expected1 }, { ugStudent2, expected2 }, { ugStudent3, expected3 }, { ugStudent4, expected4 },\\r\\n\\t\\t\\t\\t{ ugStudent5, expected5 }\\r\\n\\t\\t};\\r\\n\\t\\treturn inputs;\\r\\n\\r\\n\\t}\\r\\n\\t\\r\\n\\t@Test(alwaysRun=true,dataProvider=\\\"dataProvider\\\",timeOut=5000)\\r\\n\\tpublic void generateResultTest(UndergraduateStudent input, UndergraduateStudent expected) throws Exception{\\r\\n\\t\\tugInstance = input;\\r\\n\\t\\tugInstance.generateResult();\\r\\n\\t\\t\\r\\n\\t\\tReflectionAssert.assertReflectionEquals(expected, ugInstance);\\r\\n\\t}\\r\\n\\t\\r\\n\\t\\r\\n\\r\\n}\\r\\n\",\r\n"
//				+ "            \"testCaseFileName\": \"UndergraduateStudentTest.java\",\r\n"
//				+ "            \"solutionFileName\": \"UndergraduateStudent.java\"\r\n" + "        }\r\n" + "    ],\r\n"
//				+ "    \"username\": \"\"\r\n" + "}";

		List<Map<String, Object>> sources = contentServ.getMetaByIDListandSource(
				Arrays.asList(new String[] { data.getResourceId() }), new String[] { "artifactUrl" }, null);
		if (sources.size() == 0) {
			throw new InvalidDataInputException("invalid.resource");
		}

		Map<String, Object> source = sources.get(0);

		String answerKeyUrl = String.valueOf(source.get("artifactUrl"));
		int index = answerKeyUrl.lastIndexOf("/");
		String completeFileName = answerKeyUrl.substring(index + 1);
		String fileName = completeFileName.split("\\.")[0];
		fileName = fileName + "-key.json";
		answerKeyUrl = answerKeyUrl.substring(0, index + 1) + fileName;
		// uncomment when running locally
//	 		answerKeyUrl = answerKeyUrl.replaceAll("wingspan-staging.infosysapps.com", "private-content-service");

		// This was done because answer key was present in two instances
		if (answerKeyUrl.contains("private-content-service") || answerKeyUrl.contains("private-static-host"))
			verifyJson = contentServ.getContentStoreData(answerKeyUrl);
		else {
			verifyJson = contentServ.getKeyFromContentStore(answerKeyUrl);
		}

		JSONObject keyObject = new JSONObject(verifyJson);
		verifyJson = keyObject.get("fpTestCase").toString();
		Map<String, Object> responseData = javaEval(verifyJson, data);
		return responseData;
	}

	private Map<String, Object> javaEval(String verifyJson, AssignmentSubmissionDTO data) throws Exception {
		String responseData;

		String serviceIp = lexServerProperties.getJavaEvalServerHost();
		String servicePort = lexServerProperties.getJavaEvalServerPort();
		String serviceName = lexServerProperties.getJavaEvalEndpoint();

		JSONObject testCaseObject = new JSONObject(verifyJson);
		testCaseObject = testCaseObject.put("username", data.getUserId());
		testCaseObject.put("traineeSolution", data.getUser_solution());
		testCaseObject.put("type", "sub");
		testCaseObject.put("userAgent", "Lex");

		verifyJson = testCaseObject.toString();
		System.out.println(verifyJson);

		String url = "http-url://" + serviceIp + ":" + servicePort + serviceName;
		try {
			responseData = iapVerificationService.postSolutionString(url, verifyJson);
		} catch (Exception ex) {

			throw new ApplicationLogicError("Error in Pyeval Verifying Engine", ex);
		}
//		responseData = "{\r\n" + "    \"responseCode\": \"200\",\r\n" + "    \"message\": \"Success\",\r\n"
//				+ "    \"data\": {\r\n" + "        \"structural\": [{\r\n"
//				+ "                \"tcid\": \"tc1572930424192_3418\",\r\n"
//				+ "                \"tctarget\": \"number of functions\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tccomponent\": \"methods\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tcid\": \"tc1572930424192_8027\",\r\n"
//				+ "                \"tctarget\": \"methods\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tccomponent\": \"method_signature\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tcid\": \"tc1572930424204_6183\",\r\n"
//				+ "                \"tctarget\": \"number of functions\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tccomponent\": \"methods\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tcid\": \"tc1572930424204_2840\",\r\n"
//				+ "                \"tctarget\": \"methods\",\r\n" + "                \"tcclass\": \"Student\",\r\n"
//				+ "                \"tccomponent\": \"method_signature\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"setName\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tcid\": \"tc1572930424204_8344\",\r\n"
//				+ "                \"tctarget\": \"methods\",\r\n" + "                \"tcclass\": \"Student\",\r\n"
//				+ "                \"tccomponent\": \"method_signature\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tcid\": \"tc1572930424204_1952\",\r\n"
//				+ "                \"tctarget\": \"methods\",\r\n" + "                \"tcclass\": \"Student\",\r\n"
//				+ "                \"tccomponent\": \"method_signature\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"getName\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tcid\": \"tc1572930424204_6819\",\r\n"
//				+ "                \"tctarget\": \"methods\",\r\n" + "                \"tcclass\": \"Student\",\r\n"
//				+ "                \"tccomponent\": \"method_signature\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"setResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tcid\": \"tc1572930424204_6964\",\r\n"
//				+ "                \"tctarget\": \"methods\",\r\n" + "                \"tcclass\": \"Student\",\r\n"
//				+ "                \"tccomponent\": \"method_signature\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"setTestScore\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tcid\": \"tc1572930424204_4924\",\r\n"
//				+ "                \"tctarget\": \"methods\",\r\n" + "                \"tcclass\": \"Student\",\r\n"
//				+ "                \"tccomponent\": \"method_signature\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"getResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tcid\": \"tc1572930424204_9768\",\r\n"
//				+ "                \"tctarget\": \"methods\",\r\n" + "                \"tcclass\": \"Student\",\r\n"
//				+ "                \"tccomponent\": \"method_signature\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"getTest\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tcid\": \"tc1572930424209_2972\",\r\n"
//				+ "                \"tctarget\": \"number of functions\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tccomponent\": \"methods\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tcid\": \"tc1572930424209_5986\",\r\n"
//				+ "                \"tctarget\": \"methods\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tccomponent\": \"method_signature\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            }\r\n" + "        ],\r\n" + "        \"codeQuality\": {\r\n"
//				+ "            \"ResponseCode\": 0,\r\n" + "            \"Message\": \"Success\",\r\n"
//				+ "            \"Data\": [{\r\n"
//				+ "                    \"Violation\": \"Parameter 'name' is not assigned and could be declared final\",\r\n"
//				+ "                    \"Filename\": \"GraduateStudent.java\",\r\n"
//				+ "                    \"Classname\": \"GraduateStudent\",\r\n"
//				+ "                    \"MethodName\": \"GraduateStudent\",\r\n"
//				+ "                    \"Rule\": \"\\nA method argument that is never re-assigned within the method can be declared final.\\n      \",\r\n"
//				+ "                    \"LineNo\": 5,\r\n" + "                    \"Tips\": \"\"\r\n"
//				+ "                },\r\n" + "                {\r\n"
//				+ "                    \"Violation\": \"Local variable 'score' could be declared final\",\r\n"
//				+ "                    \"Filename\": \"GraduateStudent.java\",\r\n"
//				+ "                    \"Classname\": \"GraduateStudent\",\r\n"
//				+ "                    \"MethodName\": \"generateResult\",\r\n"
//				+ "                    \"Rule\": \"\\nA local variable assigned only once can be declared final.\\n      \",\r\n"
//				+ "                    \"LineNo\": 11,\r\n" + "                    \"Tips\": \"\"\r\n"
//				+ "                },\r\n" + "                {\r\n"
//				+ "                    \"Violation\": \"Local variable 'average' could be declared final\",\r\n"
//				+ "                    \"Filename\": \"GraduateStudent.java\",\r\n"
//				+ "                    \"Classname\": \"GraduateStudent\",\r\n"
//				+ "                    \"MethodName\": \"generateResult\",\r\n"
//				+ "                    \"Rule\": \"\\nA local variable assigned only once can be declared final.\\n      \",\r\n"
//				+ "                    \"LineNo\": 14,\r\n" + "                    \"Tips\": \"\"\r\n"
//				+ "                },\r\n" + "                {\r\n"
//				+ "                    \"Violation\": \"Abstract classes should be named AbstractXXX\",\r\n"
//				+ "                    \"Filename\": \"GraduateStudent.java\",\r\n"
//				+ "                    \"Classname\": \"GraduateStudent\",\r\n"
//				+ "                    \"MethodName\": \"\",\r\n"
//				+ "                    \"Rule\": \"\\nAbstract classes should be named 'AbstractXXX'.\\n       \",\r\n"
//				+ "                    \"LineNo\": 26,\r\n" + "                    \"Tips\": \"\"\r\n"
//				+ "                },\r\n" + "                {\r\n"
//				+ "                    \"Violation\": \"Parameter 'name' is not assigned and could be declared final\",\r\n"
//				+ "                    \"Filename\": \"Student.java\",\r\n"
//				+ "                    \"Classname\": \"Student\",\r\n"
//				+ "                    \"MethodName\": \"Student\",\r\n"
//				+ "                    \"Rule\": \"\\nA method argument that is never re-assigned within the method can be declared final.\\n      \",\r\n"
//				+ "                    \"LineNo\": 32,\r\n" + "                    \"Tips\": \"\"\r\n"
//				+ "                },\r\n" + "                {\r\n"
//				+ "                    \"Violation\": \"Parameter 'testNumber' is not assigned and could be declared final\",\r\n"
//				+ "                    \"Filename\": \"Student.java\",\r\n"
//				+ "                    \"Classname\": \"Student\",\r\n"
//				+ "                    \"MethodName\": \"setTestScore\",\r\n"
//				+ "                    \"Rule\": \"\\nA method argument that is never re-assigned within the method can be declared final.\\n      \",\r\n"
//				+ "                    \"LineNo\": 39,\r\n" + "                    \"Tips\": \"\"\r\n"
//				+ "                },\r\n" + "                {\r\n"
//				+ "                    \"Violation\": \"Parameter 'testScore' is not assigned and could be declared final\",\r\n"
//				+ "                    \"Filename\": \"Student.java\",\r\n"
//				+ "                    \"Classname\": \"Student\",\r\n"
//				+ "                    \"MethodName\": \"setTestScore\",\r\n"
//				+ "                    \"Rule\": \"\\nA method argument that is never re-assigned within the method can be declared final.\\n      \",\r\n"
//				+ "                    \"LineNo\": 39,\r\n" + "                    \"Tips\": \"\"\r\n"
//				+ "                },\r\n" + "                {\r\n"
//				+ "                    \"Violation\": \"Parameter 'name' is not assigned and could be declared final\",\r\n"
//				+ "                    \"Filename\": \"Student.java\",\r\n"
//				+ "                    \"Classname\": \"Student\",\r\n"
//				+ "                    \"MethodName\": \"setName\",\r\n"
//				+ "                    \"Rule\": \"\\nA method argument that is never re-assigned within the method can be declared final.\\n      \",\r\n"
//				+ "                    \"LineNo\": 46,\r\n" + "                    \"Tips\": \"\"\r\n"
//				+ "                },\r\n" + "                {\r\n"
//				+ "                    \"Violation\": \"Parameter 'result' is not assigned and could be declared final\",\r\n"
//				+ "                    \"Filename\": \"Student.java\",\r\n"
//				+ "                    \"Classname\": \"Student\",\r\n"
//				+ "                    \"MethodName\": \"setResult\",\r\n"
//				+ "                    \"Rule\": \"\\nA method argument that is never re-assigned within the method can be declared final.\\n      \",\r\n"
//				+ "                    \"LineNo\": 55,\r\n" + "                    \"Tips\": \"\"\r\n"
//				+ "                },\r\n" + "                {\r\n"
//				+ "                    \"Violation\": \"Parameter 'name' is not assigned and could be declared final\",\r\n"
//				+ "                    \"Filename\": \"UndergraduateStudent.java\",\r\n"
//				+ "                    \"Classname\": \"UndergraduateStudent\",\r\n"
//				+ "                    \"MethodName\": \"UndergraduateStudent\",\r\n"
//				+ "                    \"Rule\": \"\\nA method argument that is never re-assigned within the method can be declared final.\\n      \",\r\n"
//				+ "                    \"LineNo\": 64,\r\n" + "                    \"Tips\": \"\"\r\n"
//				+ "                },\r\n" + "                {\r\n"
//				+ "                    \"Violation\": \"Local variable 'score' could be declared final\",\r\n"
//				+ "                    \"Filename\": \"UndergraduateStudent.java\",\r\n"
//				+ "                    \"Classname\": \"UndergraduateStudent\",\r\n"
//				+ "                    \"MethodName\": \"generateResult\",\r\n"
//				+ "                    \"Rule\": \"\\nA local variable assigned only once can be declared final.\\n      \",\r\n"
//				+ "                    \"LineNo\": 70,\r\n" + "                    \"Tips\": \"\"\r\n"
//				+ "                },\r\n" + "                {\r\n"
//				+ "                    \"Violation\": \"Local variable 'average' could be declared final\",\r\n"
//				+ "                    \"Filename\": \"UndergraduateStudent.java\",\r\n"
//				+ "                    \"Classname\": \"UndergraduateStudent\",\r\n"
//				+ "                    \"MethodName\": \"generateResult\",\r\n"
//				+ "                    \"Rule\": \"\\nA local variable assigned only once can be declared final.\\n      \",\r\n"
//				+ "                    \"LineNo\": 73,\r\n" + "                    \"Tips\": \"\"\r\n"
//				+ "                }\r\n" + "            ]\r\n" + "        },\r\n" + "        \"procedural\": [{\r\n"
//				+ "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@2e6e6e50\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@20a98435\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@20a98435\",\r\n"
//				+ "                \"tcid\": \"tc1572930424256_5667\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@35e76c0e\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@3b20851a\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@3b20851a\",\r\n"
//				+ "                \"tcid\": \"tc1572930424256_3667\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@79c6b780\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@77014710\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@77014710\",\r\n"
//				+ "                \"tcid\": \"tc1572930424256_8149\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@3f1b4570\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@31dcad3b\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@31dcad3b\",\r\n"
//				+ "                \"tcid\": \"tc1572930424256_7289\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@60a75f3d\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@6e60ac81\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@6e60ac81\",\r\n"
//				+ "                \"tcid\": \"tc1572930424256_8951\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@79dfa032\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@771850a1\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@771850a1\",\r\n"
//				+ "                \"tcid\": \"tc1572930424310_4306\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@7f586590\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@719f95d3\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@719f95d3\",\r\n"
//				+ "                \"tcid\": \"tc1572930424310_6991\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@5f9340f2\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@5154a4a8\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@5154a4a8\",\r\n"
//				+ "                \"tcid\": \"tc1572930424310_7341\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@4dce3eb4\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@4309c8a5\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@4309c8a5\",\r\n"
//				+ "                \"tcid\": \"tc1572930424310_2602\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@165562db\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@18928fb9\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@18928fb9\",\r\n"
//				+ "                \"tcid\": \"tc1572930424310_3356\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@26dc27d3\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@281bcca0\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@281bcca0\",\r\n"
//				+ "                \"tcid\": \"tc1572930424350_8396\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@4df3fa23\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@43341c35\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@43341c35\",\r\n"
//				+ "                \"tcid\": \"tc1572930424350_6114\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@6cb85212\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@627fa02d\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@627fa02d\",\r\n"
//				+ "                \"tcid\": \"tc1572930424350_5145\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@5175a93a\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@5fb24cbc\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@5fb24cbc\",\r\n"
//				+ "                \"tcid\": \"tc1572930424350_6224\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.GraduateStudent@187e7dc6\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.GraduateStudent@16b98161\",\r\n"
//				+ "                \"tcactual\": \"abstractex.GraduateStudent@16b98161\",\r\n"
//				+ "                \"tcid\": \"tc1572930424350_1585\",\r\n"
//				+ "                \"tcclass\": \"GraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@2ffdce39\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@213a246e\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@213a246e\",\r\n"
//				+ "                \"tcid\": \"tc1572930424412_6812\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@30a96b4a\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@3e6e82f7\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@3e6e82f7\",\r\n"
//				+ "                \"tcid\": \"tc1572930424412_1438\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@7b05ffe8\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@75c20f20\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@75c20f20\",\r\n"
//				+ "                \"tcid\": \"tc1572930424412_6998\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@2e63ea4a\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@20a4002e\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@20a4002e\",\r\n"
//				+ "                \"tcid\": \"tc1572930424412_1502\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@b12da50\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@5d5249a\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@5d5249a\",\r\n"
//				+ "                \"tcid\": \"tc1572930424412_9816\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@2377e47d\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@2db00fbb\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@2db00fbb\",\r\n"
//				+ "                \"tcid\": \"tc1572930424440_5263\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@4f52ea80\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@41951cc2\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@41951cc2\",\r\n"
//				+ "                \"tcid\": \"tc1572930424440_5976\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@35daae4\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@d9a4527\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@d9a4527\",\r\n"
//				+ "                \"tcid\": \"tc1572930424440_4119\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@7856c258\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@769122fa\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@769122fa\",\r\n"
//				+ "                \"tcid\": \"tc1572930424440_9943\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@7afb2afd\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@743cda0a\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@743cda0a\",\r\n"
//				+ "                \"tcid\": \"tc1572930424440_1743\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@38f2475a\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@3635afec\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@3635afec\",\r\n"
//				+ "                \"tcid\": \"tc1572930424470_7335\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@3f0ae3e7\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@31cd0bae\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@31cd0bae\",\r\n"
//				+ "                \"tcid\": \"tc1572930424470_5672\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@45de2aa3\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@4b19cdb0\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@4b19cdb0\",\r\n"
//				+ "                \"tcid\": \"tc1572930424470_3212\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@c894eac\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@24ea095\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@24ea095\",\r\n"
//				+ "                \"tcid\": \"tc1572930424470_6710\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider\",\r\n"
//				+ "                \"tcinput\": \"abstractex.UndergraduateStudent@5500b281\",\r\n"
//				+ "                \"tcexpected\": \"abstractex.UndergraduateStudent@5bc75789\",\r\n"
//				+ "                \"tcactual\": \"abstractex.UndergraduateStudent@5bc75789\",\r\n"
//				+ "                \"tcid\": \"tc1572930424470_6877\",\r\n"
//				+ "                \"tcclass\": \"UndergraduateStudent\",\r\n"
//				+ "                \"tcstatus\": \"pass\",\r\n" + "                \"tcmethod\": \"generateResult\"\r\n"
//				+ "            },\r\n" + "            {\r\n" + "                \"tctype\": \"dataProvider1\",\r\n"
//				+ "                \"tcinput\": \"[I@3192136a\",\r\n" + "                \"tcexpected\": \"78\",\r\n"
//				+ "                \"tcactual\": \"78\",\r\n"
//				+ "                \"tcid\": \"tc1572930424533_7764\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider1\",\r\n"
//				+ "                \"tcinput\": \"[I@7c6e5ad0\",\r\n" + "                \"tcexpected\": \"68\",\r\n"
//				+ "                \"tcactual\": \"68\",\r\n"
//				+ "                \"tcid\": \"tc1572930424533_4470\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider1\",\r\n"
//				+ "                \"tcinput\": \"[I@f67c175\",\r\n" + "                \"tcexpected\": \"99\",\r\n"
//				+ "                \"tcactual\": \"99\",\r\n"
//				+ "                \"tcid\": \"tc1572930424533_3081\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider1\",\r\n"
//				+ "                \"tcinput\": \"[I@2c4f1781\",\r\n" + "                \"tcexpected\": \"54\",\r\n"
//				+ "                \"tcactual\": \"54\",\r\n"
//				+ "                \"tcid\": \"tc1572930424533_8494\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider2\",\r\n"
//				+ "                \"tcinput\": \"[I@30afc95d\",\r\n" + "                \"tcexpected\": null,\r\n"
//				+ "                \"tcactual\": null,\r\n" + "                \"tcid\": \"tc1572930424533_8527\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider2\",\r\n"
//				+ "                \"tcinput\": \"[I@46fe4488\",\r\n" + "                \"tcexpected\": null,\r\n"
//				+ "                \"tcactual\": null,\r\n" + "                \"tcid\": \"tc1572930424533_5386\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider2\",\r\n"
//				+ "                \"tcinput\": \"[I@4b7f32ee\",\r\n" + "                \"tcexpected\": null,\r\n"
//				+ "                \"tcactual\": null,\r\n" + "                \"tcid\": \"tc1572930424533_6207\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider2\",\r\n"
//				+ "                \"tcinput\": \"[I@3a1d9483\",\r\n" + "                \"tcexpected\": null,\r\n"
//				+ "                \"tcactual\": null,\r\n" + "                \"tcid\": \"tc1572930424533_9910\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider1\",\r\n"
//				+ "                \"tcinput\": \"[I@74bc6f6c\",\r\n" + "                \"tcexpected\": \"78\",\r\n"
//				+ "                \"tcactual\": \"78\",\r\n"
//				+ "                \"tcid\": \"tc1572930424590_5788\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider1\",\r\n"
//				+ "                \"tcinput\": \"[I@5bb6f69a\",\r\n" + "                \"tcexpected\": \"68\",\r\n"
//				+ "                \"tcactual\": \"68\",\r\n"
//				+ "                \"tcid\": \"tc1572930424590_4182\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider1\",\r\n"
//				+ "                \"tcinput\": \"[I@5a967f35\",\r\n" + "                \"tcexpected\": \"99\",\r\n"
//				+ "                \"tcactual\": \"99\",\r\n"
//				+ "                \"tcid\": \"tc1572930424590_8949\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider1\",\r\n"
//				+ "                \"tcinput\": \"[I@3dc4eab5\",\r\n" + "                \"tcexpected\": \"54\",\r\n"
//				+ "                \"tcactual\": \"54\",\r\n"
//				+ "                \"tcid\": \"tc1572930424590_6288\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider2\",\r\n"
//				+ "                \"tcinput\": \"[I@6465b127\",\r\n" + "                \"tcexpected\": null,\r\n"
//				+ "                \"tcactual\": null,\r\n" + "                \"tcid\": \"tc1572930424590_2260\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider2\",\r\n"
//				+ "                \"tcinput\": \"[I@757ba1b4\",\r\n" + "                \"tcexpected\": null,\r\n"
//				+ "                \"tcactual\": null,\r\n" + "                \"tcid\": \"tc1572930424590_2943\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider2\",\r\n"
//				+ "                \"tcinput\": \"[I@445c018c\",\r\n" + "                \"tcexpected\": null,\r\n"
//				+ "                \"tcactual\": null,\r\n" + "                \"tcid\": \"tc1572930424590_6895\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider2\",\r\n"
//				+ "                \"tcinput\": \"[I@5d7b550a\",\r\n" + "                \"tcexpected\": null,\r\n"
//				+ "                \"tcactual\": null,\r\n" + "                \"tcid\": \"tc1572930424591_5483\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider1\",\r\n"
//				+ "                \"tcinput\": \"[I@3f7aa5fe\",\r\n" + "                \"tcexpected\": \"78\",\r\n"
//				+ "                \"tcactual\": \"78\",\r\n"
//				+ "                \"tcid\": \"tc1572930424641_4340\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider1\",\r\n"
//				+ "                \"tcinput\": \"[I@4931f1bb\",\r\n" + "                \"tcexpected\": \"68\",\r\n"
//				+ "                \"tcactual\": \"68\",\r\n"
//				+ "                \"tcid\": \"tc1572930424641_5538\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider1\",\r\n"
//				+ "                \"tcinput\": \"[I@e4be6e3\",\r\n" + "                \"tcexpected\": \"99\",\r\n"
//				+ "                \"tcactual\": \"99\",\r\n"
//				+ "                \"tcid\": \"tc1572930424641_3773\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider1\",\r\n"
//				+ "                \"tcinput\": \"[I@51b825b9\",\r\n" + "                \"tcexpected\": \"54\",\r\n"
//				+ "                \"tcactual\": \"54\",\r\n"
//				+ "                \"tcid\": \"tc1572930424641_2096\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider2\",\r\n"
//				+ "                \"tcinput\": \"[I@66ac2d84\",\r\n" + "                \"tcexpected\": null,\r\n"
//				+ "                \"tcactual\": null,\r\n" + "                \"tcid\": \"tc1572930424641_8974\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider2\",\r\n"
//				+ "                \"tcinput\": \"[I@5d50ffc8\",\r\n" + "                \"tcexpected\": null,\r\n"
//				+ "                \"tcactual\": null,\r\n" + "                \"tcid\": \"tc1572930424641_9957\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider2\",\r\n"
//				+ "                \"tcinput\": \"[I@294ca4e4\",\r\n" + "                \"tcexpected\": null,\r\n"
//				+ "                \"tcactual\": null,\r\n" + "                \"tcid\": \"tc1572930424641_7588\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            },\r\n" + "            {\r\n"
//				+ "                \"tctype\": \"dataProvider2\",\r\n"
//				+ "                \"tcinput\": \"[I@65f87907\",\r\n" + "                \"tcexpected\": null,\r\n"
//				+ "                \"tcactual\": null,\r\n" + "                \"tcid\": \"tc1572930424641_7986\",\r\n"
//				+ "                \"tcclass\": \"Student\",\r\n" + "                \"tcstatus\": \"pass\",\r\n"
//				+ "                \"tcmethod\": \"set\"\r\n" + "            }\r\n" + "        ]\r\n" + "    }\r\n"
//				+ "}";
		JSONObject resp = new JSONObject(responseData);
		Map<String, Object> resultMap = new HashMap<String, Object>();

		// this is done because resultsummary is not sent in case if error.
		if (!resp.isNull("Error") || !resp.isNull("error")) {
			throw new ExerciseCodeVerifcationException("Error occured in JavaEval - Java Execution Service");
		} else {
			JSONObject parsedResponse = utilService.analyseJavaVerificationResponse(resp);
			SubmitDataDTO verifySummary = this.getVerifyStatus(parsedResponse);

			resultMap.put("verifySummary", verifySummary);
			resultMap.put("verifyResult", new ObjectMapper().readValue(parsedResponse.toString(), HashMap.class));
		}
		return resultMap;
	}
}

/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.exercise.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.exercise.bodhi.repo.ClassErrorsSpecification;
import com.infosys.lex.exercise.bodhi.repo.Evaluatedmarks;
import com.infosys.lex.exercise.bodhi.repo.RelationErrorsSpecification;


@Service
public class ClassDiagramUtilServiceImpl implements ClassDiagramUtilService{

	
	/*
	 * 
	 * CLASS DIAGRAM SOLUTION EVALUATOR
	 * (START)--------------------------------------------------------------
	 * 
	 */

	@Override
	public Evaluatedmarks formatAndEvaluateUserSolution(String userSolution, String answerKey, int qpMarks)
			throws JSONException {
		Map<String, Object> formattedDataMap = new HashMap<String, Object>();
		generateArrays(answerKey, false, formattedDataMap);
		generateArrays(userSolution, true, formattedDataMap);

		return evaluateScripts(qpMarks, formattedDataMap);
	}

	// This method is for Generating Arrays over Author Solution to compare with

	private void generateArrays(String answerKey, boolean isUserSolution, Map<String, Object> formattedDataMap)
			throws JSONException {
		// -------------------------------answer key
		// starts-------------------------------//
		String solutionOf = isUserSolution ? "User" : "Author";
		answerKey = answerKey.replaceAll("[\\n\\r\\t\\s]+", "");
		JSONObject dataAnswer = new JSONObject(answerKey);
		String options = String.valueOf(dataAnswer.get("options"));
		// -------------------------------class part
		// starts-------------------------------//
		JSONObject optionJson = new JSONObject(options);

		JSONArray objectArray = optionJson.getJSONArray("classes");
		List<String> classDynamic = new ArrayList<String>();
		List<List<String>> attClassNamesDynamic = new ArrayList<List<String>>();
		List<List<String>> attSpecifiersDynamic = new ArrayList<List<String>>();
		List<List<String>> metClassNamesDynamic = new ArrayList<List<String>>();
		List<List<String>> metSpecifiersDynamic = new ArrayList<List<String>>();
		for (int i = 0; i < objectArray.length(); i++) {
			String objectString = String.valueOf(objectArray.get(i));
			JSONObject elementJson = new JSONObject(objectString);
			String type = String.valueOf(elementJson.get("type"));
			String name = String.valueOf(elementJson.get("name"));

			if (type == null || type.isEmpty())
				throw new InvalidDataInputException("invalid.solution.input", new Object[] { solutionOf });
			else
				type = type.trim();

			if (type.toLowerCase().equals("class")) {
				if (!(name == null)) {
					name = name.trim();
					if (name.isEmpty())
						continue;
					if (!classDynamic.contains(name)) {
						classDynamic.add(name);
						attClassNamesDynamic.add(new ArrayList<String>());
						metClassNamesDynamic.add(new ArrayList<String>());
						attSpecifiersDynamic.add(new ArrayList<String>());
						metSpecifiersDynamic.add(new ArrayList<String>());
					}
				} else {
					throw new InvalidDataInputException("invalid.solution.input", new Object[] { solutionOf });
				}
			}
		}
		int classIndex = 0;
		for (int i = 0; i < objectArray.length(); i++) {
			String objectString = String.valueOf(objectArray.get(i));
			JSONObject elementJson = new JSONObject(objectString);
			String type = String.valueOf(elementJson.get("type"));
			String name = String.valueOf(elementJson.get("name"));
			String belongsTo = String.valueOf(elementJson.get("belongsTo"));
			if (belongsTo.trim().isEmpty())
				continue;
			String access = String.valueOf(elementJson.get("access"));

			if (type == null || type.isEmpty())
				throw new InvalidDataInputException("invalid.solution.input", new Object[] { solutionOf });
			else
				type = type.trim();
			if (type.toLowerCase().equals("class"))
				continue;
			if (type.toLowerCase().equals("attribute")) {
				if (!(name == null || name.isEmpty() || access == null)) {

					if (!classDynamic.contains(belongsTo))
						throw new InvalidDataInputException("invalid.solution.input", new Object[] { solutionOf });
					name = name.trim();
					access = access.trim();
					belongsTo = belongsTo.trim();
					if (classDynamic.contains(belongsTo)) {
						classIndex = classDynamic.indexOf(belongsTo);
					} else {
						throw new InvalidDataInputException("invalid.solution.input", new Object[] { solutionOf });
					}
					attClassNamesDynamic.get(classIndex).add(name);
					attSpecifiersDynamic.get(classIndex).add(access);

				} else {
					throw new InvalidDataInputException("invalid.solution.input", new Object[] { solutionOf });
				}
			} else if (type.toLowerCase().equals("method")) {
				// For methods

				if (!(name == null || name.isEmpty() || access == null || !classDynamic.contains(belongsTo))) {
					name = name.trim();
					access = access.trim();
					belongsTo = belongsTo.trim();
					classIndex = classDynamic.indexOf(belongsTo);

					if (classDynamic.contains(belongsTo)) {
						classIndex = classDynamic.indexOf(belongsTo);
					} else {
						throw new InvalidDataInputException("invalid.solution.input", new Object[] { solutionOf });
					}

					metClassNamesDynamic.get(classIndex).add(name);
					metSpecifiersDynamic.get(classIndex).add(access);

				} else {
					throw new InvalidDataInputException("invalid.solution.input", new Object[] { solutionOf });
				}
			}

			else {
				throw new InvalidDataInputException("invalid.solution.input", new Object[] { solutionOf });
			}

		}

		// -------------------------------relation part
		// starts-------------------------------//
		JSONArray relationsArray = optionJson.getJSONArray("relations");
		List<String> relationshipDynamic = new ArrayList<String>();
		String relationString = "";
		for (int i = 0; i < relationsArray.length(); i++) {
			JSONObject dataRelation = relationsArray.getJSONObject(i);
			String source = (String.valueOf(dataRelation.get("source"))).trim();
			String relation = (String.valueOf(dataRelation.get("relation"))).trim();
			String target = (String.valueOf(dataRelation.get("target"))).trim();
			relationString = source + " " + relation + " " + target;
			relationshipDynamic.add(relationString);

		}
		// -------------------------------relation part
		// ends-------------------------------//

		if (isUserSolution) {
			formattedDataMap.put("user_attClassNames", attClassNamesDynamic);
			formattedDataMap.put("user_attAccessSpecifiers", attSpecifiersDynamic);
			formattedDataMap.put("user_classNames", classDynamic);
			formattedDataMap.put("user_metClassNames", metClassNamesDynamic);
			formattedDataMap.put("user_metAccessSpecifiers", metSpecifiersDynamic);
			formattedDataMap.put("user_relationship", relationshipDynamic);

		} else {
			formattedDataMap.put("attClassNames", attClassNamesDynamic);
			formattedDataMap.put("attAccessSpecifiers", attSpecifiersDynamic);
			formattedDataMap.put("classNames", classDynamic);
			formattedDataMap.put("metClassNames", metClassNamesDynamic);
			formattedDataMap.put("metAccessSpecifiers", metSpecifiersDynamic);
			formattedDataMap.put("relationship", relationshipDynamic);

		}

		// -------------------------------answer key
		// ends-------------------------------//
	}

	/*
	 * This method evaluates the answer and returns the marks.
	 * 
	 */

	@SuppressWarnings("unchecked")
	private Evaluatedmarks evaluateScripts(float qpMarks, Map<String, Object> formattedDataMap) {
		Evaluatedmarks evaluatedMarks = new Evaluatedmarks();

		evaluatedMarks.setClassErrorsSpecification(new ArrayList<ClassErrorsSpecification>());
		evaluatedMarks.setRelationErrorsSpecification(new ArrayList<RelationErrorsSpecification>());
		evaluatedMarks.setMarks(qpMarks);
		List<String> classNames = (List<String>) formattedDataMap.get("classNames");
		List<String> user_classNames = (List<String>) formattedDataMap.get("user_classNames");
		List<List<String>> attClassNames = (List<List<String>>) formattedDataMap.get("attClassNames");
		List<List<String>> metClassNames = (List<List<String>>) formattedDataMap.get("metClassNames");
		List<List<String>> attAccessSpecifiers = (List<List<String>>) formattedDataMap.get("attAccessSpecifiers");
		List<List<String>> metAccessSpecifiers = (List<List<String>>) formattedDataMap.get("metAccessSpecifiers");
		List<List<String>> user_attClassNames = (List<List<String>>) formattedDataMap.get("user_attClassNames");
		List<List<String>> user_metClassNames = (List<List<String>>) formattedDataMap.get("user_metClassNames");
		List<List<String>> user_attAccessSpecifiers = (List<List<String>>) formattedDataMap
				.get("user_attAccessSpecifiers");
		List<List<String>> user_metAccessSpecifiers = (List<List<String>>) formattedDataMap
				.get("user_metAccessSpecifiers");
		List<String> relationship = (List<String>) formattedDataMap.get("relationship");
		List<String> user_relationship = (List<String>) formattedDataMap.get("user_relationship");

		float marksPerClassAndRelations = qpMarks / (classNames.size() + relationship.size());
		if (!(classNames.size() != 0 && user_classNames.size() == 0)) {
			for (int classIndex = 0; classIndex < classNames.size(); classIndex++) {
				String className = classNames.get(classIndex);
				// attributes check
				if (user_classNames.contains(classNames.get(classIndex))) {
					int attCount = attClassNames.get(classIndex).size();
					float eachAttMarks = marksPerClassAndRelations / (2 * attCount);

					// evaluating attributes
					int userClassIndex = user_classNames.indexOf(classNames.get(classIndex));
					Map<String, List<String>> compareList = new HashMap<String, List<String>>();
					compareList.put("solutionList", attClassNames.get(classIndex));
					compareList.put("userSolutionList", user_attClassNames.get(userClassIndex));
					compareList.put("solutionAccessSpecifiers", attAccessSpecifiers.get(classIndex));
					compareList.put("userSolutionAccessSpecifiers", user_attAccessSpecifiers.get(userClassIndex));
					propertiesEvaluate(evaluatedMarks, eachAttMarks, compareList, false, className);

					// evaluating methods
					int metCount = metClassNames.get(classIndex).size();
					float eachMetMarks = marksPerClassAndRelations / (2 * metCount);

					compareList.put("solutionList", metClassNames.get(classIndex));
					compareList.put("userSolutionList", user_metClassNames.get(userClassIndex));
					compareList.put("solutionAccessSpecifiers", metAccessSpecifiers.get(classIndex));
					compareList.put("userSolutionAccessSpecifiers", user_metAccessSpecifiers.get(userClassIndex));
					propertiesEvaluate(evaluatedMarks, eachMetMarks, compareList, true, className);

				} else {
					evaluatedMarks.setMarks(evaluatedMarks.getMarks() - marksPerClassAndRelations);
					evaluatedMarks.getClassErrorsSpecification()
							.add(new ClassErrorsSpecification(className, marksPerClassAndRelations, "Missing"));
				}

			}
		} else {
			evaluatedMarks.setMarks(evaluatedMarks.getMarks() - marksPerClassAndRelations * classNames.size());
			evaluatedMarks.getClassErrorsSpecification().add(new ClassErrorsSpecification("Classes",
					marksPerClassAndRelations * classNames.size(), "All Missing"));

		}

		for (String className : user_classNames) {
			if (!classNames.contains(className)) {
				evaluatedMarks.setMarks(evaluatedMarks.getMarks() - marksPerClassAndRelations / 2);
				evaluatedMarks.getClassErrorsSpecification()
						.add(new ClassErrorsSpecification(className, marksPerClassAndRelations / 2, "Incorrect"));
			}
		}

		// evaluates relationship between class
		if (!(relationship.size() != 0 && user_relationship.size() == 0)) {
			List<String> duplicates = new ArrayList<String>(findDuplicates(user_relationship));

			for (String relation : relationship) {
				if (user_relationship.contains(relation)) {
					if (duplicates.contains(relation)) {
						boolean matchFound = false;
						while (user_relationship.contains(relation)) {
							if (matchFound) {
								evaluatedMarks.setMarks(evaluatedMarks.getMarks() - marksPerClassAndRelations / 2);
								evaluatedMarks.getRelationErrorsSpecification().add(new RelationErrorsSpecification(
										relation, marksPerClassAndRelations / 2, "Duplicate"));
							} else
								matchFound = true;
							user_relationship.remove(relation);
						}
					} else
						user_relationship.remove(relation);
				} else {
					evaluatedMarks.setMarks(evaluatedMarks.getMarks() - marksPerClassAndRelations / 2);
					evaluatedMarks.getRelationErrorsSpecification()
							.add(new RelationErrorsSpecification(relation, marksPerClassAndRelations / 2, "Missing"));
				}
			}
			for (String relation : user_relationship) {
				evaluatedMarks.setMarks(evaluatedMarks.getMarks() - marksPerClassAndRelations / 2);
				evaluatedMarks.getRelationErrorsSpecification()
						.add(new RelationErrorsSpecification(relation, marksPerClassAndRelations / 2, "Incorrect"));
			}

		} else {
			evaluatedMarks.setMarks(evaluatedMarks.getMarks() - marksPerClassAndRelations * relationship.size());
			evaluatedMarks.getRelationErrorsSpecification().add(new RelationErrorsSpecification("Relationships",
					marksPerClassAndRelations * relationship.size(), "All Missing"));
		}

		if (evaluatedMarks.getMarks() < 0) {
			evaluatedMarks.setMarks(0.0f);
			evaluatedMarks.setMarksPercent(0.0f);
		} else {
			evaluatedMarks.setMarks((float) (Math.round(evaluatedMarks.getMarks() * 100.0) / 100.0));
			evaluatedMarks
					.setMarksPercent((float) (Math.round(evaluatedMarks.getMarks() * 100.0 * 100.0 / qpMarks) / 100.0));
		}

		return evaluatedMarks;
	}

	private void propertiesEvaluate(Evaluatedmarks evaluatedMarks, float marksDeducted,
			Map<String, List<String>> compareList, boolean isMethod, String className) {
		float marks = evaluatedMarks.getMarks();
		List<ClassErrorsSpecification> classErrors = evaluatedMarks.getClassErrorsSpecification();

		int userSolIndex;
		// this condition checks whether user solution has any properties(methods/class)
		// when actual solution has properties
		if (!(compareList.get("solutionList").size() != 0 && compareList.get("userSolutionList").size() == 0)) {
			List<String> duplicates = new ArrayList<String>(findDuplicates(compareList.get("userSolutionList")));

			for (String name : compareList.get("solutionList")) {
				if (compareList.get("userSolutionList").contains(name)) {
					int solIndex = compareList.get("solutionList").indexOf(name);

					// if there are duplicates we check if the solution is correct if not they
					// marked as "Incorrect" if solution is correct and
					// and we already found the correct solution it is marked "Duplicate" . In both
					// case we remove the property from given list.
					if (duplicates.contains(name)) {
						boolean matchFound = false;
						while (compareList.get("userSolutionList").contains(name)) {
							userSolIndex = compareList.get("userSolutionList").indexOf(name);
							if (!compareList.get("solutionAccessSpecifiers").get(solIndex)
									.equals(compareList.get("userSolutionAccessSpecifiers").get(userSolIndex))) {

								marks -= marksDeducted / 2;
								if (isMethod)
									classErrors.add(new ClassErrorsSpecification(className,
											compareList.get("userSolutionAccessSpecifiers").get(userSolIndex),
											marksDeducted / 2, "Incorrect", name));
								else
									classErrors.add(new ClassErrorsSpecification(className, name,
											compareList.get("userSolutionAccessSpecifiers").get(userSolIndex),
											marksDeducted / 2, "Incorrect"));

							} else {
								if (matchFound) {

									marks -= marksDeducted / 2;
									addErrors(classErrors, isMethod, marksDeducted / 2, "Duplicate", name, className);

								} else
									matchFound = true;
							}
							compareList.get("userSolutionList").remove(userSolIndex);
							compareList.get("userSolutionAccessSpecifiers").remove(userSolIndex);
						}
					} else {
						userSolIndex = compareList.get("userSolutionList").indexOf(name);

						if (!compareList.get("solutionAccessSpecifiers").get(solIndex)
								.equals(compareList.get("userSolutionAccessSpecifiers").get(userSolIndex))) {
							marks -= marksDeducted / 2;
							if (isMethod)
								classErrors.add(new ClassErrorsSpecification(className,
										compareList.get("userSolutionAccessSpecifiers").get(userSolIndex),
										marksDeducted / 2, "Incorrect", name));
							else
								classErrors.add(new ClassErrorsSpecification(className, name,
										compareList.get("userSolutionAccessSpecifiers").get(userSolIndex),
										marksDeducted / 2, "Incorrect"));

						}
						compareList.get("userSolutionList").remove(userSolIndex);
						compareList.get("userSolutionAccessSpecifiers").remove(userSolIndex);
					}
				} else {

					marks -= marksDeducted;
					addErrors(classErrors, isMethod, marksDeducted, "Missing", name, className);

				}
			}

			for (String name : compareList.get("userSolutionList")) {

				marks -= marksDeducted / 2;
				addErrors(classErrors, isMethod, marksDeducted / 2, "Incorrect", name, className);

			}
		} else {
			// when user solutio list of properties is empty.
			marks -= marksDeducted * compareList.get("solutionList").size();
			if (isMethod)
				classErrors.add(new ClassErrorsSpecification(className,
						marksDeducted * compareList.get("solutionList").size(), "All Missing", "Methods"));

			else

				classErrors.add(new ClassErrorsSpecification(className, "Attributes",
						marksDeducted * compareList.get("solutionList").size(), "All Missing"));

		}

		evaluatedMarks.setMarks(marks);

	}

	private void addErrors(List<ClassErrorsSpecification> classErrors, boolean isMethod, float marksDeducted,
			String message, String propName, String className) {
		if (isMethod)
			classErrors.add(new ClassErrorsSpecification(className, marksDeducted, message, propName));
		else
			classErrors.add(new ClassErrorsSpecification(className, propName, marksDeducted, message));

	}

	// return duplicates
	private <T> Set<T> findDuplicates(Collection<T> collection) {

		Set<T> duplicates = new LinkedHashSet<>();
		Set<T> uniques = new HashSet<>();

		for (T t : collection) {
			if (!uniques.add(t)) {
				duplicates.add(t);
			}
		}

		return duplicates;
	}

	/*
	 * 
	 * CLASS DIAGRAM SOLUTION EVALUATOR
	 * (END)----------------------------------------------------
	 * 
	 */

}

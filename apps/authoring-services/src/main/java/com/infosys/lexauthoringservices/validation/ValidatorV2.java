package com.infosys.lexauthoringservices.validation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.infosys.lexauthoringservices.util.LexConstants;

public class ValidatorV2 {

	public static void validate(Map<String, Object> contentMeta, Map<String, Object> validateMap, Set<String> errors) {

		String operationName = validateMap.get(ValidationConstants.OPERATION.get()).toString();
		String propertyName = validateMap.get(ValidationConstants.PROPERTY.get()).toString();
		String dataType = validateMap.get(ValidationConstants.DATA_TYPE.get()).toString();
		String identifier = (String) contentMeta.get(LexConstants.IDENTIFIER);

		if (!notExistsValidations(contentMeta, errors, propertyName, dataType))
			return;

		if (!dataTypeValidations(contentMeta, errors, propertyName, dataType))
			return;

		if (operationName.equals(Operations.NOT_NULL.get()))
			notNullValidations(contentMeta, errors, propertyName, dataType);

		else if (operationName.equals(Operations.EQUALS.get()))
			equalsValidation(contentMeta, validateMap, errors, dataType, propertyName);

		else if (operationName.equals(Operations.NOT_EQUALS.get()))
			notEqualsValidations(contentMeta, validateMap, errors, dataType, propertyName);

		else if (operationName.equals(Operations.CONTAINS.get()))
			containsValidations(contentMeta, validateMap, errors, propertyName);

		else if (operationName.equals(Operations.NOT_CONTAINS.get()))
			notContainsValidations(contentMeta, validateMap, errors, propertyName);

		else if (operationName.equals(Operations.GREATER_THAN.get())) 
			greaterThanValidations(contentMeta, validateMap, errors, propertyName);

		else if (operationName.equals(Operations.LESS_THAN.get()))
			lessThanValidations(contentMeta, validateMap, errors, propertyName);

		else if (operationName.equals(Operations.IN.get()))
			inOperationValidation(contentMeta, validateMap, errors, propertyName);

		else if (operationName.equals(Operations.EMPTY.get()))
			emptyValidations(contentMeta, errors, propertyName, dataType);

		else
			System.out.println("Invalid operation type " + validateMap.toString());
	}

	private static void equalsValidation(Map<String, Object> contentMeta, Map<String, Object> validateMap,
			Set<String> errorMaps, String dataType, String propertyName) {

		// transitive reference operator (*)
		if (validateMap.get(ValidationConstants.VALUE.get()).toString().startsWith("*")) {
			String comparatorPropertyName = validateMap.get(ValidationConstants.VALUE.get()).toString().substring(1);
			if (!transitiveEqualityComparsion(propertyName, comparatorPropertyName, contentMeta, dataType)) {
				errorMaps.add(propertyName + " is set to " + contentMeta.get(propertyName) + " should be equal to "
						+ comparatorPropertyName + "'s value");
			}

		} else if (!contentMeta.get(propertyName).equals(validateMap.get(ValidationConstants.VALUE.get()))) {
			errorMaps.add(propertyName + " is set to " + contentMeta.get(propertyName) + " should be equal to "
					+ validateMap.get(ValidationConstants.VALUE.get()));
		}
	}

	private static void notEqualsValidations(Map<String, Object> contentMeta, Map<String, Object> validateMap,
			Set<String> errorMaps, String dataType, String propertyName) {

		// transitive reference operator (*)
		if (validateMap.get(ValidationConstants.VALUE.get()).toString().startsWith("*")) {
			String comparatorPropertyName = validateMap.get(ValidationConstants.VALUE.get()).toString().substring(1);
			if (transitiveEqualityComparsion(propertyName, comparatorPropertyName, contentMeta, dataType)) {
				errorMaps.add(propertyName + " is set to " + contentMeta.get(propertyName) + " should not be equal to "
						+ comparatorPropertyName + "'s value");
			}

		} else if (contentMeta.get(propertyName).equals(validateMap.get(ValidationConstants.VALUE.get()))) {
			errorMaps.add(propertyName + " is set to " + contentMeta.get(propertyName) + " should not be equal to "
					+ validateMap.get(ValidationConstants.VALUE.get()));
		}
	}

	private static boolean transitiveEqualityComparsion(String propertyName, String comparatorPropertyName,
			Map<String, Object> contentMeta, String dataType) {

		if (dataType.equals(DataTypes.LONG.get())) {
			if (contentMeta.get(propertyName)
					.equals(Long.parseLong(contentMeta.get(comparatorPropertyName).toString()))) {
				return true;
			}
		}

		if (contentMeta.get(propertyName).equals(contentMeta.get(comparatorPropertyName))) {
			return true;
		}

		return false;
	}

	private static boolean dataTypeValidations(Map<String, Object> contentMeta, Set<String> errors, String propertyName,
			String dataType) {

		if (dataType.equals(DataTypes.STRING.get()) && contentMeta.containsKey(propertyName)
				&& !contentMeta.get(propertyName).getClass().equals(String.class)) {
			errors.add(propertyName + " must be of type String");
			return false;

		} else if (dataType.equals(DataTypes.LONG.get()) && contentMeta.containsKey(propertyName)
				&& !contentMeta.get(propertyName).getClass().equals(Long.class)) {
			errors.add(propertyName + " must be of type long");
			return false;

		} else if (dataType.equals(DataTypes.BOOLEAN.get()) && contentMeta.containsKey(propertyName)
				&& !(contentMeta.get(propertyName).getClass().equals(Boolean.class))) {
			errors.add(propertyName + " must be of type Boolean");
			return false;

		} else if (dataType.equals(DataTypes.LIST.get()) && contentMeta.containsKey(propertyName)
				&& (contentMeta.get(propertyName) instanceof List == false)) {
			errors.add(propertyName + " must be of type List");
			return false;
		}

		return true;
	}

	// will have to convert integer to long from validate map if dataType has int
	// right now only dataTypes is List and Strings
	@SuppressWarnings("unchecked")
	private static void inOperationValidation(Map<String, Object> contentMeta, Map<String, Object> validateMap,
			Set<String> errors, String propertyName) {

		if (contentMeta.get(propertyName) instanceof List) {
			List<Object> properties = (List<Object>) contentMeta.get(propertyName);
			for (Object property : properties) {
				if (!((List<Object>) validateMap.get(ValidationConstants.VALUE.get())).contains(property)) {
					errors.add(propertyName + " contains " + property + " should only be from "
							+ validateMap.get(ValidationConstants.VALUE.get()));
				}
			}

		} else if (!((List<Object>) validateMap.get(ValidationConstants.VALUE.get()))
				.contains(contentMeta.get(propertyName))) {
			errors.add(propertyName + " should be one of these " + validateMap.get(ValidationConstants.VALUE.get()));
		}
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private static void greaterThanValidations(Map<String, Object> contentMeta, Map<String, Object> validateMap,
			Set<String> errors, String propertyName) {
		String identifier = (String) contentMeta.get(LexConstants.IDENTIFIER);
		if (contentMeta.get(propertyName) instanceof Long && !((Long) contentMeta.get(propertyName) > Long
				.parseLong(validateMap.get(ValidationConstants.VALUE.get()).toString()))) {
			errors.add(propertyName + " is set to " + contentMeta.get(propertyName) + " should be greater than "
					+ validateMap.get(ValidationConstants.VALUE.get()));

		} else if (contentMeta.get(propertyName) instanceof List && !(((List<Object>) contentMeta.get(propertyName))
				.size() > (Long) validateMap.get(ValidationConstants.VALUE.get()))) {
			errors.add(propertyName + " should contains greater than "
					+ validateMap.get(ValidationConstants.VALUE.get()) + " value(s)");
		}
	}

	@SuppressWarnings("unchecked")
	private static void lessThanValidations(Map<String, Object> contentMeta, Map<String, Object> validateMap,
			Set<String> errors, String propertyName) {

		if (contentMeta.get(propertyName) instanceof Long && !((Long) contentMeta.get(propertyName) < Long
				.parseLong(validateMap.get(ValidationConstants.VALUE.get()).toString()))) {

			errors.add(propertyName + " is set to " + contentMeta.get(propertyName) + " should be less than "
					+ validateMap.get(ValidationConstants.VALUE.get()));

		} else if (contentMeta.get(propertyName) instanceof List && !(((List<Object>) contentMeta.get(propertyName))
				.size() < (Long) validateMap.get(ValidationConstants.VALUE.get()))) {

			errors.add(propertyName + " should contain less than " + validateMap.get(ValidationConstants.VALUE.get())
					+ " values");
		}
	}

	private static void containsValidations(Map<String, Object> contentMeta, Map<String, Object> validateMap,
			Set<String> errorMaps, String propertyName) {

		// only supports Strings for now
		if (!contentMeta.get(propertyName).toString()
				.contains(validateMap.get(ValidationConstants.VALUE.get()).toString())) {
			errorMaps.add(propertyName + " should contain " + validateMap.get(ValidationConstants.VALUE.get()));
		}
	}

	private static void notContainsValidations(Map<String, Object> contentMeta, Map<String, Object> validateMap,
			Set<String> errorMaps, String propertyName) {
		// only supports Strings for now
		if (contentMeta.get(propertyName).toString()
				.contains(validateMap.get(ValidationConstants.VALUE.get()).toString())) {
			errorMaps.add(propertyName + " cannot contain " + validateMap.get(ValidationConstants.VALUE.get()));
		}
	}

	@SuppressWarnings("unchecked")
	private static boolean notNullValidations(Map<String, Object> contentMeta, Set<String> errors, String propertyName,
			String dataType) {

		if (dataType.equals(DataTypes.LONG.get()) || dataType.equals(DataTypes.STRING.get())
				|| dataType.equals(DataTypes.BOOLEAN.get())) {
			if (!contentMeta.containsKey(propertyName) || contentMeta.get(propertyName).toString().trim().isEmpty()) {
				errors.add(propertyName + " is mandatory");
				return false;
			}

		} else if (dataType.equals(DataTypes.LIST.get())) {
			if (!contentMeta.containsKey(propertyName) || ((List<Object>) contentMeta.get(propertyName)).isEmpty()) {
				errors.add(propertyName + " is mandatory");
				return false;
			}
		}
		return true;
	}

	private static boolean notExistsValidations(Map<String, Object> contentMeta, Set<String> errors,
			String propertyName, String dataType) {

		if (dataType.equals(DataTypes.LONG.get()) || dataType.equals(DataTypes.STRING.get())
				|| dataType.equals(DataTypes.BOOLEAN.get())) {
			if (!contentMeta.containsKey(propertyName)) {
				errors.add(propertyName + " is not present is the meta");
				return false;
			}

		} else if (dataType.equals(DataTypes.LIST.get())) {
			if (!contentMeta.containsKey(propertyName)) {
				errors.add(propertyName + " is not present is the meta");
				return false;
			}
		}
		return true;
	}

	// only supports string for now
	@SuppressWarnings("unchecked")
	private static boolean emptyValidations(Map<String, Object> contentMeta, Set<String> errors, String propertyName,
			String dataType) {

		if (dataType.equals(DataTypes.STRING.get())) {
			if (!contentMeta.get(propertyName).toString().trim().equals("")) {
				errors.add(propertyName + " should be empty");
				return false;
			}
		}

		if (dataType.equals(DataTypes.LIST.get())) {
			if (((List<Object>) contentMeta.get(propertyName)).size() > 0) {
				errors.add(propertyName + " should be empty");
			}
		}
		return true;
	}
}
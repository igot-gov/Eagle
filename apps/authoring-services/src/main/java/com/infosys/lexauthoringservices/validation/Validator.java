package com.infosys.lexauthoringservices.validation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.client.RestTemplate;

public class Validator {

	private static RestTemplate restTemplate = new RestTemplate();

	public static enum ValidationConstants {
		OPERATION("operation"), PROPERTY("property"), DATA_TYPE("dataType"), VALUE("value");

		private String validationConstant;

		public String get() {
			return validationConstant;
		}

		private ValidationConstants(String validationConstant) {
			this.validationConstant = validationConstant;
		}
	}

	public static enum Operations {

		EQUALS("equals"), NOT_EQUALS("notequals"), GREATER_THAN("greaterthan"), LESS_THAN("lessthan"),
		NOT_NULL("notnull"), IN("in"), HIT_URL("hiturl"), CONTAINS("contains"), NOT_CONTAINS("notcontains");

		private String operation;

		public String get() {
			return operation;
		}

		private Operations(String operation) {
			this.operation = operation;
		}
	}

	public static enum DataTypes {

		STRING("String"), INTEGER("Integer"), LIST("List");

		private String dataType;

		public String get() {
			return dataType;
		}

		private DataTypes(String dataType) {
			this.dataType = dataType;
		}
	}

	public static void validatev2(Map<String, Object> contentMeta, Map<String, Object> validateMap,
			Set<String> errorMaps) {

		String operationName = validateMap.get(ValidationConstants.OPERATION.get()).toString();
		String propertyName = validateMap.get(ValidationConstants.PROPERTY.get()).toString();
		String dataType = validateMap.get(ValidationConstants.DATA_TYPE.get()).toString();

		if (!dataTypeValidations(contentMeta, errorMaps, propertyName, dataType)) {
			return;
		}

		if (operationName.equals(Operations.NOT_NULL.get())) {
			notNullValidations(dataType, contentMeta, propertyName, errorMaps);
		}
	}

	private static void notNullValidations(String dataType, Map<String, Object> contentMeta, String propertyName,
			Set<String> errorMaps) {
		if (dataType.equals(DataTypes.INTEGER.get()) || dataType.equals(DataTypes.STRING.get())) {
			if (!contentMeta.containsKey(propertyName) || contentMeta.get(propertyName).toString().isEmpty()) {
				errorMaps.add(propertyName + " cannot be empty");
			}

		} else if (dataType.equals(DataTypes.LIST.get())) {
			if (!contentMeta.containsKey(propertyName) || ((List<Object>) contentMeta.get(propertyName)).isEmpty()) {
				errorMaps.add(propertyName + " cannot be empty");
			}
		}

	}

	@SuppressWarnings("unchecked")
	public static void validate(Map<String, Object> contentMeta, Map<String, Object> validateMap,
			Set<String> errorMaps) {

		String operationName = validateMap.get(ValidationConstants.OPERATION.get()).toString();
		String propertyName = validateMap.get(ValidationConstants.PROPERTY.get()).toString();
		String dataType = validateMap.get(ValidationConstants.DATA_TYPE.get()).toString();

		dataTypeValidations(contentMeta, errorMaps, propertyName, dataType);

		if (operationName.equals(Operations.NOT_NULL.get())) {

			if (dataType.equals(DataTypes.INTEGER.get()) || dataType.equals(DataTypes.STRING.get())) {
				if (!contentMeta.containsKey(propertyName) || contentMeta.get(propertyName).toString().isEmpty()) {
					errorMaps.add(propertyName + " cannot be empty");
				}

			} else if (dataType.equals(DataTypes.LIST.get())) {
				if (!contentMeta.containsKey(propertyName)
						|| ((List<Object>) contentMeta.get(propertyName)).isEmpty()) {
					errorMaps.add(propertyName + " cannot be empty");
				}
			}

		} else if (operationName.equals(Operations.NOT_CONTAINS.get())) {

			if (!contentMeta.containsKey(propertyName) || contentMeta.get(propertyName).toString().isEmpty()) {
				errorMaps.add(propertyName + " cannot be empty");

			} else if (contentMeta.get(propertyName).toString()
					.contains(validateMap.get(ValidationConstants.VALUE.get()).toString())) {
				errorMaps.add(propertyName + " cannot contain " + validateMap.get(ValidationConstants.VALUE.get()));
			}

		} else if (operationName.equals(Operations.EQUALS.get())) {

			if (!contentMeta.containsKey(propertyName) || contentMeta.get(propertyName) == null
					|| contentMeta.get(propertyName).toString().isEmpty()) {
				errorMaps.add(propertyName + " cannot be empty");

			} else if (!contentMeta.get(propertyName).equals(validateMap.get(ValidationConstants.VALUE.get()))) {
				errorMaps.add(propertyName + " is set to " + contentMeta.get(propertyName) + " should be equal to "
						+ validateMap.get(ValidationConstants.VALUE.get()));
			}

		} else if (operationName.equals(Operations.NOT_EQUALS.get())) {

			if (!contentMeta.containsKey(propertyName) || contentMeta.get(propertyName) == null
					|| contentMeta.get(propertyName).toString().isEmpty()) {
				errorMaps.add(propertyName + " cannot be empty");

			} else if (contentMeta.get(propertyName).equals(validateMap.get(ValidationConstants.VALUE.get()))) {

				errorMaps.add(propertyName + " is set to " + contentMeta.get(propertyName) + " should not be equal to "
						+ validateMap.get(ValidationConstants.VALUE.get()));

			} else if (validateMap.get(ValidationConstants.VALUE.get()).toString().startsWith("*")) {

				String comparatorPropertyName = validateMap.get(ValidationConstants.VALUE.get()).toString()
						.substring(1);
				if (contentMeta.get(propertyName).equals(contentMeta.get(comparatorPropertyName))) {
					errorMaps.add(propertyName + " should not be equal to " + comparatorPropertyName);
				}
			}

		} else if (operationName.equals(Operations.GREATER_THAN.get())) {

			if (!contentMeta.containsKey(propertyName) || contentMeta.get(propertyName).toString().isEmpty()) {
				errorMaps.add(propertyName + " cannot be empty");

			} else if (contentMeta.get(propertyName) instanceof Integer && !((int) contentMeta
					.get(propertyName) > (int) validateMap.get(ValidationConstants.VALUE.get()))) {
				errorMaps.add(propertyName + " is set to " + contentMeta.get(propertyName) + " should be greater than "
						+ validateMap.get(ValidationConstants.VALUE.get()));

			} else if (contentMeta.get(propertyName) instanceof List
					&& ((List<Object>) contentMeta.get(propertyName)).size() <= (int) validateMap.get(propertyName)) {

			}

		} else if (operationName.equals(Operations.IN.get())) {

			if (!contentMeta.containsKey(propertyName) || contentMeta.get(propertyName).toString().isEmpty()) {
				errorMaps.add(propertyName + " cannot be empty");

			} else if (contentMeta.get(propertyName) instanceof List) {

				List<Object> properties = (List<Object>) contentMeta.get(propertyName);
				for (Object property : properties) {
					if (!((List<Object>) validateMap.get(ValidationConstants.VALUE.get())).contains(property)) {
						errorMaps.add(propertyName + " contains " + property + " should only be from "
								+ validateMap.get(ValidationConstants.VALUE.get()));
					}
				}

			} else if (!((List<Object>) validateMap.get(ValidationConstants.VALUE.get()))
					.contains(contentMeta.get(propertyName))) {
				errorMaps.add(
						propertyName + " should be one of these " + validateMap.get(ValidationConstants.VALUE.get()));
			}
		}
//		} else if (operationName.equals(Operations.HIT_URL.get())) {
//			String url = contentMeta.get(validateMap.get(ValidationConstants.PROPERTY.get())).toString();
//			ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
//
//			if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
//				errorMaps.add(propertyName + " is not accessible");
//			}
//		}
	}

	private static boolean dataTypeValidations(Map<String, Object> contentMeta, Set<String> errorMaps,
			String propertyName, String dataType) {

		if (dataType.equals(DataTypes.STRING.get()) && contentMeta.containsKey(propertyName)
				&& !contentMeta.get(propertyName).getClass().equals(String.class)) {
			errorMaps.add(propertyName + " must be of type String");
			return false;

		} else if (dataType.equals(DataTypes.INTEGER.get()) && contentMeta.containsKey(propertyName)
				&& !contentMeta.get(propertyName).getClass().equals(Integer.class)) {
			errorMaps.add(propertyName + " must be of type Integer");
			return false;

		} else if (dataType.equals(DataTypes.LIST.get()) && contentMeta.containsKey(propertyName)
				&& (contentMeta.get(propertyName) instanceof List == false)) {
			errorMaps.add(propertyName + " must be of type List");
			return false;
		}

		return true;
	}
}
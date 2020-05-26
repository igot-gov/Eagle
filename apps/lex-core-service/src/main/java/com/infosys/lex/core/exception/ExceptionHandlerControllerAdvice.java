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
package com.infosys.lex.core.exception;

import java.io.IOException;
import java.lang.reflect.MalformedParametersException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.common.service.LoggerService;
import com.infosys.lex.core.logger.LexLogger;

@EnableWebMvc
@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	LoggerService logService;

	private LexLogger logger = new LexLogger(getClass().getName());

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@ExceptionHandler(MalformedParametersException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ClientErrors handleException(final MalformedParametersException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("internal.error", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles exception for all invalid urls
	 * 
	 * @param exception
	 * @return
	 */
	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	public ClientErrors requestHandlingNoHandlerFound(final NoHandlerFoundException exception) {
		ClientErrors errors = new ClientErrors("method.missing", "Method not found");

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles exception for all invalid urls
	 * 
	 * @param exception
	 * @return
	 */
	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public ClientErrors noContentHandler(final NoContentException exception) {
		ClientErrors errors = new ClientErrors("data.missing", "not data found");

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * 
	 * @param httpStatusCodeException
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@ExceptionHandler
	@ResponseBody
	public ResponseEntity<?> httpStatusCodeHandler(final HttpStatusCodeException httpStatusCodeException)
			throws JsonParseException, JsonMappingException, IOException {
		Object responseMap = null;
		String response = null;
		try {
			response = httpStatusCodeException.getResponseBodyAsString();

			responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception ex) {
			responseMap = response;
		}
		return new ResponseEntity<Object>(responseMap, httpStatusCodeException.getStatusCode());

	}

	/**
	 * Handles all non caught exceptions
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */
	@ExceptionHandler(Throwable.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ClientErrors handleException(final Exception exception, final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("internal.error", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles all non caught exceptions
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ClientErrors handleIOException(final Exception exception, final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("internal.error", exception.getMessage());
		exception.printStackTrace();
		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles all validation failure exceptions from DTOs
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({ MethodArgumentNotValidException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ClientErrors handleMethodArgumentExceptions(MethodArgumentNotValidException ex) {

		String message;
		ClientErrors errors = new ClientErrors();
		BindingResult result = ex.getBindingResult();

		List<FieldError> fieldErrors = result.getFieldErrors();
		for (FieldError error : fieldErrors) {
			message = error.getDefaultMessage();
			errors.addError(error.getField(), message);
		}

		List<ObjectError> globalErrors = result.getGlobalErrors();
		for (ObjectError error : globalErrors) {
			message = error.getDefaultMessage();
			errors.addError(error.getObjectName(), message);
		}

		// log the exception
		logger.error(ex);

		return errors;
	}

	/**
	 * Handles all invalid data exceptions
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ InvalidDataInputException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ClientErrors HandleInvalidDataInputException(final InvalidDataInputException exception,
			final HttpServletRequest request) {
		String message = exception.getMessage();
		if (message == null || message.isEmpty())
			message = messageSource.getMessage(exception.getCode(), exception.getParams(), "invalid input data",
					LocaleContextHolder.getLocale());
		ClientErrors errors = new ClientErrors(exception.getCode(), message);

		// log the exception
		logger.error(exception);
		return errors;
	}

	/**
	 * Handles all invalid resource exceptions
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ ResourceNotFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public @ResponseBody ClientErrors HandleResourceNotFoundException(final ResourceNotFoundException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("resource.missing", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;

	}

	/**
	 * Handles all class cast exceptions
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ ClassCastException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ClientErrors classCastExceptionHandler(final ClassCastException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("internal.error", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles all illegal arguments exceptions
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ IllegalArgumentException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ClientErrors illegalArgumanetExceptionHandler(final IllegalArgumentException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("bad.request", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles all algorithm exceptions
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ NoSuchAlgorithmException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ClientErrors NoSuchAlgorithmExceptionHandler(final NoSuchAlgorithmException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("internal.error", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles all type mismatch exceptions
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ TypeMismatchException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ClientErrors typeMismatchExceptionHandler(final TypeMismatchException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("internal.error", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles all application logic exceptions
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ ApplicationLogicError.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ClientErrors internalServerErrorHandler(final ApplicationLogicError exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("internal.error", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles all illegal access exceptions
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ IllegalAccessException.class })
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public @ResponseBody ClientErrors IllegalAccessExceptionHandler(final IllegalAccessException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("illegal.access", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles all parse exceptions
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ ParseException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ClientErrors parseExceptionHandler(final ParseException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("internal.error", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles all parse exceptions
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ ConflictErrorException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ClientErrors conflictExceptionHandler(final ConflictErrorException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("internal.error", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles all Servlet Binding exceptions
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ ServletRequestBindingException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ClientErrors AccessExceptionHandler(final ServletRequestBindingException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("internal.error", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}

	@ExceptionHandler({ AccessForbidenError.class })
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public @ResponseBody ClientErrors AccessForbidenErrorHandler(final AccessForbidenError exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("access.forbidden", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}

	@ExceptionHandler({ BadRequestException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ClientErrors BadRequstExceptionHandler(final BadRequestException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("userId.NotFound", exception.getMessage());

		logger.error(exception);

		return errors;
	}

	/**
	 * 
	 */
	@ExceptionHandler({ ConstraintViolationException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ClientErrors constraintViolationExceptionHandler(final ConstraintViolationException ex) {

		String message;
		String path;
		ClientErrors errors = new ClientErrors();
		Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
		for (ConstraintViolation<?> violation : violations) {

			message = violation.getMessage();
			path =  violation.getPropertyPath().toString();
			message += " {"+path+"}";
			errors.addError("Validation Error", message);
		}

		// log the exception
		logger.error(ex);

		return errors;

	}

	@ExceptionHandler({ DBErrorException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ClientErrors DBErrorExceptionHandler(final DBErrorException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("internal.error", exception.getMessage());
		logger.error(exception.getDbException());

		return errors;
	}
	
	@ExceptionHandler({ RestClientException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ClientErrors RestClientExceptionHandler(final RestClientException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("restCall.failed.error", exception.getMessage());
		logger.error(exception);

		return errors;
	}

}
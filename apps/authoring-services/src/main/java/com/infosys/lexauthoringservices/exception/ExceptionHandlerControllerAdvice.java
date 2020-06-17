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
package com.infosys.lexauthoringservices.exception;

import java.io.IOException;
import java.lang.reflect.MalformedParametersException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.util.LexLogger;

@EnableWebMvc
@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

	@Autowired
	LexLogger logService;

	@Autowired
	private LexLogger logger;

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
		ClientErrors errors = new ClientErrors("client.error", "invalid url");

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles exception for all badRequestExceptions thrown
	 * 
	 * @param exception
	 * @return
	 */
	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ClientErrors badRequestHandler(final BadRequestException exception) {
		ClientErrors errors = new ClientErrors("client.error", exception.getMessage());

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
	public ResponseEntity<Map<String, Object>> httpStatusCodeHandler(
			final HttpStatusCodeException httpStatusCodeException)
			throws JsonParseException, JsonMappingException, IOException {

		String response = httpStatusCodeException.getResponseBodyAsString();

		Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
		});

		return new ResponseEntity<Map<String, Object>>(responseMap, httpStatusCodeException.getStatusCode());

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

	@ExceptionHandler({ ConflictErrorException.class })
	@ResponseStatus(HttpStatus.CONFLICT)
	public @ResponseBody ClientErrors conflictExceptionHandler(final ConflictErrorException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors(exception.getMessage(), exception.getConflicts());

		// log the exception
		logger.error(exception);

		return errors;
	}
}

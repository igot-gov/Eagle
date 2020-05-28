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

package com.infosys.lex.notification.exception;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import org.apache.kafka.common.network.InvalidReceiveException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.infosys.lex.notification.util.LexNotificationLogger;

@EnableWebMvc
@ControllerAdvice
//TODO There are too many handlers with same code. You can combile handler with {Exception1, Exception2} syntax 
public class ExceptionHandlerControllerAdvice {

	@Autowired
	MessageSource messageSource;

	private LexNotificationLogger logger = new LexNotificationLogger(getClass().getName());

	/**
	 * Handles all validation failure exceptions from DTOs
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({ MethodArgumentNotValidException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
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

		return errors;
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ClientErrors requestHandlingNoHandlerFound(final InvalidReceiveException exception) {
		ClientErrors errors = new ClientErrors("invalid.request", exception.getMessage());
		logger.error(exception);
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
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody ClientErrors HandleInvalidDataInputException(final InvalidDataInputException exception,
			final HttpServletRequest request) {

		String message = exception.getMessage();
		if (message == null || message.isEmpty()) {
			message = messageSource.getMessage(exception.getCode(), exception.getParams(), "invalid input data",
					LocaleContextHolder.getLocale());
		}
		ClientErrors errors = new ClientErrors(exception.getCode(), message);
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
	 * Handles all non caught exceptions
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ClientErrors handleException(final Exception exception, final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("internal.error", "internal Server Error");

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
		ClientErrors errors = new ClientErrors("internal.error", "Internal Server Error");

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

	@ExceptionHandler({ ApplicationLogicException.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ClientErrors internalServerErrorHandler(final ApplicationLogicException exception,
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

	@ExceptionHandler({ AuthenticationException.class })
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public @ResponseBody ClientErrors conflictExceptionHandler(final AuthenticationException exception,
			final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("UNAUTHORIZED", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles all missing parameters exceptions in requests
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */

	@ExceptionHandler({ MissingServletRequestParameterException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ClientErrors missingServletRequestParameterExceptionHandler(
			final MissingServletRequestParameterException exception, final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("bad.request", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}

	/**
	 * Handles all missing request headers exceptions in requests
	 * 
	 * @param exception
	 * @param request
	 * @return
	 */

	@ExceptionHandler({ ServletRequestBindingException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ClientErrors ServletRequestBindingExceptionHandler(
			final ServletRequestBindingException exception, final HttpServletRequest request) {
		ClientErrors errors = new ClientErrors("bad.request", exception.getMessage());

		// log the exception
		logger.error(exception);

		return errors;
	}
}

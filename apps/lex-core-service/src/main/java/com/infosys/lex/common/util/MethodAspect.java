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
package com.infosys.lex.common.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.infosys.lex.core.logger.LexLogger;

@Aspect
@Configuration
public class MethodAspect {

	private LexLogger logger = new LexLogger(getClass().getName());

	/**
	 * logs input, output and performance of all repositories
	 * 
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.infosys..repo.*Repository*..*(..))")
	public Object aroundRepo(ProceedingJoinPoint point) throws Throwable {

		long time = System.currentTimeMillis();
		ObjectMapper ow = new ObjectMapper();

		// log the input
		Map<String, Object> message = new HashMap<>();
		message.put("event", "Repository Start");
		message.put("method", point.getSignature().toString());
		message.put("args", point.getArgs());
		logger.info(ow.writeValueAsString(message));

		// execute the method
		Object result = point.proceed();

		// log the response
		message = new HashMap<>();
		message.put("event", "Repository Response");
		message.put("method", point.getSignature().toString());
		message.put("response", result);
		logger.debug(ow.writeValueAsString(message));

		// log the time taken
		time = System.currentTimeMillis() - time;
		message = new HashMap<>();
		message.put("event", "Repository Performance");
		message.put("method", point.getSignature().toString());
		message.put("time", time);
		logger.performance(ow.writeValueAsString(message));

		return result;
	}

	/**
	 * logs input, output and performance of all services
	 * 
	 * IMPORTANT NOTE: excluding the log service from the aspect is necessary to
	 * avoid infinite loop between service and around wrapper.
	 * 
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.infosys..service.*Service*..*(..))")
	public Object aroundServices(ProceedingJoinPoint point) throws Throwable {

		long time = System.currentTimeMillis();
		ObjectMapper ow = new ObjectMapper();

		// log the input
		Map<String, Object> message = new HashMap<>();
		message.put("event", "Method Start");
		message.put("method", point.getSignature().toString());
		message.put("args", point.getArgs());
		logger.info(ow.writeValueAsString(message));

		// execute the method
		Object result = point.proceed();

		// log the response
		message = new HashMap<>();
		message.put("event", "Method Response");
		message.put("method", point.getSignature().toString());
		message.put("response", result);
		logger.debug(ow.writeValueAsString(message));

		// log the time taken
		time = System.currentTimeMillis() - time;
		message = new HashMap<>();
		message.put("event", "Method Performance");
		message.put("method", point.getSignature().toString());
		message.put("time", time);
		logger.performance(ow.writeValueAsString(message));

		return result;
	}

	@Around("execution(* com.infosys..controller.*Controller*..*(..))")
	public Object aroundController(ProceedingJoinPoint point) throws Throwable {

		long time = System.currentTimeMillis();
		ObjectMapper ow = new ObjectMapper();

		// log the input
		Map<String, Object> message = new HashMap<>();
		message.put("event", "Controller Start");
		message.put("method", point.getSignature().toString());
		message.put("args", point.getArgs());
		logger.info(ow.writeValueAsString(message));

		// execute the method
		Object result = point.proceed();

		// log the response
		message = new HashMap<>();
		message.put("event", "Controller Response");
		message.put("method", point.getSignature().toString());
		message.put("response", result);
		logger.debug(ow.writeValueAsString(message));

		// log the time taken
		time = System.currentTimeMillis() - time;
		message = new HashMap<>();
		message.put("event", "Controller Performance");
		message.put("method", point.getSignature().toString());
		message.put("time", time);
		message.put("controllerMapping", getUrlMapping(point));

		logger.performance(ow.writeValueAsString(message));

		return result;
	}

	@Pointcut("execution(* org.springframework.web.client.RestTemplate.*(..))")
	public void pointcut() {
	}

	@Around("pointcut()")
	public Object aroundRest(ProceedingJoinPoint point) throws Throwable {

		long time = System.currentTimeMillis();
		ObjectMapper ow = new ObjectMapper();

		// log the input
		Map<String, Object> message = new HashMap<>();
		message.put("event", "External Rest Call Start");
		message.put("method", point.getSignature().toString());

		Object[] modifiedArgs = this.removeHeaders(point);
		message.put("args", modifiedArgs);
		logger.info(ow.writeValueAsString(message));

		// execute the method
		Object result = point.proceed();

		// log the response
		message = new HashMap<>();
		message.put("event", "External Rest Call Response");
		message.put("method", point.getSignature().toString());
		message.put("response", result);
		logger.debug(ow.writeValueAsString(message));

		// log the time taken
		time = System.currentTimeMillis() - time;
		message = new HashMap<>();
		message.put("event", "External Rest Call Performance");
		message.put("method", point.getSignature().toString());
		message.put("time", time);
		logger.performance(ow.writeValueAsString(message));

		return result;
	}

	private Object[] removeHeaders(ProceedingJoinPoint point) {

		Object[] args = point.getArgs();
		Object[] modifiedArgs = new Object[args.length];
		int i = 0;
		for (Object arg : args) {
			if (arg instanceof HttpEntity) {
				@SuppressWarnings("rawtypes")
				HttpEntity<Object> entity = new HttpEntity<Object>(((HttpEntity) arg).getBody());
				modifiedArgs[i] = entity;
				i++;
			} else {
				modifiedArgs[i] = arg;
				i++;
			}
		}
		return modifiedArgs;
	}

	private String getUrlMapping(ProceedingJoinPoint point) throws JsonProcessingException {

		String url = ThreadContext.get("req.headUrl");

		if (url != null && !url.isEmpty()) {
			Object[] paramValue = point.getArgs();
			String[] paramNames = ((CodeSignature) point.getSignature()).getParameterNames();
			if (paramValue != null) {
				for (int i = 0; i < paramValue.length; i++) {
					if (paramValue[i] != null && (paramValue[i] instanceof Boolean || paramValue[i] instanceof String
							|| paramValue[i] instanceof Integer || paramValue[i] instanceof Double)) {
						String paramName = "{" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, paramNames[i])
								+ "}";
						url = url.replaceAll(paramValue[i].toString(), paramName);

					}
				}
			}
		}
		return url;
	}

}
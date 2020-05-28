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

package com.infosys.lex.notification.aspect;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.infosys.lex.notification.util.LexNotificationLogger;

@Aspect
@Configuration
public class MethodAspect {

	private LexNotificationLogger logger = new LexNotificationLogger(getClass().getName());

	/**
	 * logs input, output and performance of all repositories
	 * 
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	/**
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.infosys.lex.repository..*(..)) && !execution(* com.infosys.lex.notification.filter..*(..))")
	public Object aroundRepo(ProceedingJoinPoint point) throws Throwable {

		long time = System.currentTimeMillis();
		ObjectMapper ow = new ObjectMapper();

		// log the input
		Map<String, Object> message = new HashMap<>();
		message.put("event", "Method start");
		message.put("method", point.getSignature().toString());
		message.put("args", point.getArgs());
		logger.info(ow.writeValueAsString(message));

		// execute the method
		Object result = point.proceed();

		// log the response
		message = new HashMap<>();
		message.put("event", "Method response");
		message.put("method", point.getSignature().toString());
		message.put("response", result);
		logger.info(ow.writeValueAsString(message));

		// log the time taken
		time = System.currentTimeMillis() - time;
		message = new HashMap<>();
		message.put("event", "Repository performace");
		message.put("method", point.getSignature().toString());
		message.put("time", time);
		logger.info(ow.writeValueAsString(message));

		return result;

	}

	/**
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.infosys.lex.service..*(..)) && !execution(* com.infosys.lex.notification.service.LoggerService.*(..)) "
			+ "&& !execution(* com.infosys.lex.notification.filter..*(..))")
	public Object aroundServices(ProceedingJoinPoint point) throws Throwable {

		long time = System.currentTimeMillis();
		ObjectMapper ow = new ObjectMapper();

		// log the input
		Map<String, Object> message = new HashMap<>();
		message.put("event", "Method start");
		message.put("method", point.getSignature().toString());
		message.put("args", point.getArgs());
		logger.info(ow.writeValueAsString(message));

		// execute the method
		Object result = point.proceed();

		// log the response
		message = new HashMap<>();
		message.put("event", "Method response");
		message.put("method", point.getSignature().toString());
		message.put("response", result);
		logger.info(ow.writeValueAsString(message));

		// log the time taken
		time = System.currentTimeMillis() - time;
		message = new HashMap<>();
		message.put("event", "Service performace");
		message.put("method", point.getSignature().toString());
		message.put("time", time);
		logger.info(ow.writeValueAsString(message));

		return result;
	}

	/**
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.infosys.lex.controller..*(..)) && !execution(* com.infosys.lex.notification.filter..*(..))")
	public Object aroundController(ProceedingJoinPoint point) throws Throwable {
		long time = System.currentTimeMillis();
		ObjectMapper ow = new ObjectMapper();

		// log the input
		Map<String, Object> message = new HashMap<>();
		message.put("event", "Method start");
		message.put("method", point.getSignature().toString());
		message.put("args", point.getArgs());
		logger.info(ow.writeValueAsString(message));

		// execute the method
		Object result = point.proceed();

		// log the response
		message = new HashMap<>();
		message.put("event", "Method response");
		message.put("method", point.getSignature().toString());
		message.put("response", result);
		logger.info(ow.writeValueAsString(message));

		// log the time taken
		time = System.currentTimeMillis() - time;
		message = new HashMap<>();
		message.put("event", "Controller performace");
		message.put("method", point.getSignature().toString());
		message.put("time", time);
		logger.info(ow.writeValueAsString(message));

		return result;
	}
	
	/**
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.infosys.lex.consumer..*(..)) && !execution(* com.infosys.lex.notification.filter..*(..))")
	public Object aroundConsumer(ProceedingJoinPoint point) throws Throwable {
		long time = System.currentTimeMillis();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

		// log the input
		Map<String, Object> message = new HashMap<>();
		message.put("event", "Method start");
		message.put("method", point.getSignature().toString());
		message.put("args", point.getArgs());
		logger.info(ow.writeValueAsString(message));

		// execute the method
		Object result = point.proceed();

		// log the response
		message = new HashMap<>();
		message.put("event", "Method response");
		message.put("method", point.getSignature().toString());
		message.put("response", result);
		logger.info(ow.writeValueAsString(message));

		// log the time taken
		time = System.currentTimeMillis() - time;
		message = new HashMap<>();
		message.put("event", "Consumer performace");
		message.put("method", point.getSignature().toString());
		message.put("time", time);
		logger.info(ow.writeValueAsString(message));

		return result;
	}
	
	
}
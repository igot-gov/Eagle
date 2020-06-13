//package com.infosys.lexauthoringservices.util;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.context.annotation.Configuration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//
//@Aspect
//@Configuration
//public class MethodAspect {
//
//	private LexLogger logger = new LexLogger(getClass().getName());
//
//	/**
//	 * logs input, output and performance of all repositories
//	 *
//	 * @param point
//	 * @return
//	 * @throws Throwable
//	 */
//	@Around("execution(* com.infosys..repository.*Repository*..*(..))")
//	public Object aroundRepo(ProceedingJoinPoint point) throws Throwable {
//
//		long time = System.currentTimeMillis();
//		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//
//		// log the input
//		Map<String, Object> message = new HashMap<>();
//		message.put("event", "Repository Start");
//		message.put("method", point.getSignature().toString());
//		message.put("args", point.getArgs());
//		logger.info(ow.writeValueAsString(message));
//
//		// execute the method
//		Object result = point.proceed();
//
//		// log the response
//		message = new HashMap<>();
//		message.put("event", "Repository Response");
//		message.put("method", point.getSignature().toString());
//		message.put("response", result);
//		logger.debug(ow.writeValueAsString(message));
//
//		// log the time taken
//		time = System.currentTimeMillis() - time;
//		message = new HashMap<>();
//		message.put("event", "Repository Performance");
//		message.put("method", point.getSignature().toString());
//		message.put("time", time);
//		logger.performance(ow.writeValueAsString(message));
//
//		return result;
//	}
//
//	/**
//	 * logs input, output and performance of all services
//	 *
//	 * IMPORTANT NOTE: excluding the log service from the aspect is necessary to
//	 * avoid infinite loop between service and around wrapper.
//	 *
//	 * @param point
//	 * @return
//	 * @throws Throwable
//	 */
//	@Around("execution(* com.infosys..service.*Service*..*(..))")
//	public Object aroundServices(ProceedingJoinPoint point) throws Throwable {
//
//		long time = System.currentTimeMillis();
//		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//
//		// log the input
//		Map<String, Object> message = new HashMap<>();
//		message.put("event", "Method Start");
//		message.put("method", point.getSignature().toString());
//		message.put("args", point.getArgs());
//		logger.info(ow.writeValueAsString(message));
//
//		// execute the method
//		Object result = point.proceed();
//
//		// log the response
//		message = new HashMap<>();
//		message.put("event", "Method Response");
//		message.put("method", point.getSignature().toString());
//		message.put("response", result);
//		logger.debug(ow.writeValueAsString(message));
//
//		// log the time taken
//		time = System.currentTimeMillis() - time;
//		message = new HashMap<>();
//		message.put("event", "Method Performance");
//		message.put("method", point.getSignature().toString());
//		message.put("time", time);
//		logger.performance(ow.writeValueAsString(message));
//
//		return result;
//	}
//
//	@Around("execution(* com.infosys..controller.*Controller*..*(..))")
//	public Object aroundController(ProceedingJoinPoint point) throws Throwable {
//
//		long time = System.currentTimeMillis();
//		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//
//		// log the input
//		Map<String, Object> message = new HashMap<>();
//		message.put("event", "Controller Start");
//		message.put("method", point.getSignature().toString());
//		message.put("args", point.getArgs());
//		logger.info(ow.writeValueAsString(message));
//
//		// execute the method
//		Object result = point.proceed();
//
//		// log the response
//		message = new HashMap<>();
//		message.put("event", "Controller Response");
//		message.put("method", point.getSignature().toString());
//		message.put("response", result);
//		logger.debug(ow.writeValueAsString(message));
//
//		// log the time taken
//		time = System.currentTimeMillis() - time;
//		message = new HashMap<>();
//		message.put("event", "Controller Performance");
//		message.put("method", point.getSignature().toString());
//		message.put("time", time);
//		logger.performance(ow.writeValueAsString(message));
//
//		return result;
//	}
//
//}

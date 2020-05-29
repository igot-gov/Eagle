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

package com.infosys.lex.notification.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Throwables;

public class LexNotificationLogger {

	private Logger logger;

	public LexNotificationLogger(String className) {
		this.logger = LogManager.getLogger(className);
	}

	public void debug(String message) {
		logger.log(Level.DEBUG, message);
	}

	public void info(String message) {
		logger.log(Level.INFO, message);
	}

	public void warn(String message) {
		logger.log(Level.WARN, message);
	}

	public void fatal(Exception exception) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			Map<String, Object> message = new HashMap<>();
			message.put("event", exception.getClass());
			message.put("message", exception.getMessage());
			message.put("trace", Throwables.getStackTraceAsString(exception));
			logger.log(Level.FATAL, ow.writeValueAsString(message));
		} catch (Exception e) {
			logger.log(Level.FATAL, "{\"event\":\"" + exception.getClass() + "\",\"message\":\""
					+ exception.getMessage() + "\",\"trace\":\"" + Throwables.getStackTraceAsString(exception) + "\"}");
		}
	}

	public void fatal(String message) {
		logger.log(Level.FATAL, message);
	}

	public void error(Exception exception) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

		// log the exception
		try {
			Map<String, Object> message = new HashMap<>();
			message.put("event", exception.getClass());
			message.put("message", exception.getMessage());
			message.put("trace", Throwables.getStackTraceAsString(exception));
			logger.log(Level.ERROR, ow.writeValueAsString(message));
		} catch (Exception e) {
			logger.log(Level.ERROR, "{\"event\":\"" + exception.getClass() + "\",\"message\":\""
					+ exception.getMessage() + "\",\"trace\":\"" + Throwables.getStackTraceAsString(exception) + "\"}");
		}
	}

	public void error(String error) {
		logger.log(Level.ERROR, error);
	}

	public void trace(String message) {
		logger.log(Level.TRACE, message);
	}

	public void performance(String message) {
		logger.log(Level.forName("PERF", 350), message);
	}
}

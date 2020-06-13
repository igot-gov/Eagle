package com.infosys.lexauthoringservices.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Throwables;

@Component
public class LexLogger {

	private Logger logger;

	public LexLogger() {
		this.logger = LogManager.getLogger();
	}

	public LexLogger(String className) {
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

	public void error(String message) {

		logger.log(Level.ERROR, message);
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
			logger.log(Level.ERROR,
					"{\"event\":\"" + exception.getClass() + "\", \"message\":\"" + exception.getMessage()
							+ "\", \"trace\":\"" + Throwables.getStackTraceAsString(exception) + "\"}");
		}
	}

	public void fatal(Exception exception) {

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

		// log the exception
		try {
			Map<String, Object> message = new HashMap<>();
			message.put("event", exception.getClass());
			message.put("message", exception.getMessage());
			message.put("trace", Throwables.getStackTraceAsString(exception));
			logger.log(Level.FATAL, ow.writeValueAsString(message));
		} catch (Exception e) {
			logger.log(Level.FATAL,
					"{\"event\":\"" + exception.getClass() + "\", \"message\":\"" + exception.getMessage()
							+ "\", \"trace\":\"" + Throwables.getStackTraceAsString(exception) + "\"}");
		}
	}

	public void trace(String message) {

		logger.log(Level.TRACE, message);
	}

	public void performance(String message) {

		Level performance = Level.forName("PERF", 350);
		logger.log(performance, message);
	}

}

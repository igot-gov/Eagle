package com.infosys.lexauthoringservices.exception;

import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@ResponseBody
public class ConflictErrorException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	String message;

	private Map<String, Set<String>> conflicts;

	public ConflictErrorException(String message, Map<String, Set<String>> conflicts) {
		this.message = message;
		this.conflicts = conflicts;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Set<String>> getConflicts() {
		return conflicts;
	}

	public void setConflicts(Map<String, Set<String>> conflicts) {
		this.conflicts = conflicts;
	}
}

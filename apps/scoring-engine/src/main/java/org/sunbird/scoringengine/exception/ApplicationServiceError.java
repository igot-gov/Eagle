
package org.sunbird.scoringengine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
@ResponseBody
public class ApplicationServiceError extends RuntimeException {

	private static final long serialVersionUID = 1L;

	String message;

	public ApplicationServiceError(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

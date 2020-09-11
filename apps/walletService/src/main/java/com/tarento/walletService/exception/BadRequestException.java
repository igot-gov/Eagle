package com.tarento.walletService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@ResponseBody
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    String message;

    public BadRequestException(String message) {
        this.message = message;
    }

    public BadRequestException(String message, Throwable e) {
        super(message, e);
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

package com.tarento.walletService.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    Logger logger = LogManager.getLogger(ExceptionHandlerControllerAdvice.class);

    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ClientErrors BadRequstExceptionHandler(final BadRequestException exception,
                                           final HttpServletRequest request) {
        ClientErrors errors = new ClientErrors("userId.NotFound", exception.getMessage());

        logger.error(exception);

        return errors;
    }

}

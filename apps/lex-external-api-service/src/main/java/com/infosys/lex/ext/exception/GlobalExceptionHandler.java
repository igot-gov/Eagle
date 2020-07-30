package com.infosys.lex.ext.exception;

import org.sunbird.common.models.response.Response;
import org.sunbird.common.responsecode.ResponseCode;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.sunbird.common.models.response.ResponseParams;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

@RestControllerAdvice
public class GlobalExceptionHandler{
     
	@ExceptionHandler(ClassCastException.class)
	@ResponseBody
    public ResponseEntity<Response> classCastExceptionHandler(Exception ex) {
        ex.printStackTrace();
        Response response = new Response();
        ResponseParams responseParams = new ResponseParams();
		responseParams.setErrmsg("class casting exception occurred");
		responseParams.setErr("ClassCastException");
		responseParams.setStatus("Internal Server Error");
		response.setParams(responseParams);
		ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.internalError.getErrorCode());
		responseCode.setResponseCode(ResponseCode.SERVER_ERROR.getResponseCode());
		//responseCode.setErrorMessage("Something went wrong, are you passing request in accordance with the contract");
		HttpHeaders responseHeaders = new HttpHeaders();
		return new ResponseEntity<>(response,responseHeaders,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseBody
	public ResponseEntity<Response> illegalArgumanetExceptionHandler(Exception ex)
	{
		ex.printStackTrace();
		Response response = new Response();
		ResponseParams responseParams = new ResponseParams();
		responseParams.setErrmsg("illegal arguments to methods passed exception occurred");
		responseParams.setErr("Illegal Argument Passed");
		responseParams.setStatus("Internal Server Error");
		response.setParams(responseParams);
		ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.internalError.getErrorCode());
		responseCode.setResponseCode(ResponseCode.SERVER_ERROR.getResponseCode());
		HttpHeaders responseHeaders = new HttpHeaders();
		return new ResponseEntity<>(response,responseHeaders,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(NoSuchAlgorithmException.class)
	@ResponseBody
	public ResponseEntity<Response> NoSuchAlgorithmExceptionHandler()
	{
		Response response = new Response();
		ResponseParams responseParams = new ResponseParams();
		responseParams.setErrmsg("no such class found exception occurred");
		responseParams.setErr("NoSuchClassException");
		responseParams.setStatus("Internal Server Error");
		response.setParams(responseParams);
		ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.internalError.getErrorCode());
		responseCode.setResponseCode(ResponseCode.SERVER_ERROR.getResponseCode());
		HttpHeaders responseHeaders = new HttpHeaders();
		return new ResponseEntity<>(response,responseHeaders,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(TypeMismatchException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorInfo typeMismatchExceptionHandler(HttpServletRequest request,Exception ex)
	{
		return new ErrorInfo(request.getRequestURI(),ex.toString());
	}
	
	@ExceptionHandler(ApplicationLogicError.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorInfo internalServerErrorHandler(HttpServletRequest request,Exception ex)
	{
		return new ErrorInfo(request.getRequestURI(),ex.toString());
	}
	
	@ExceptionHandler(IllegalAccessException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorInfo IllegalAccessExceptionHandler(HttpServletRequest request,Exception ex)
	{
		return new ErrorInfo(request.getRequestURI(),ex.toString());
	}
	
	
	
	
	@ExceptionHandler({ResourceNotFoundException.class,BadRequestException.class})
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorInfo badRequestExceptionHandler(HttpServletRequest request,Exception ex)
	{
		return new ErrorInfo(request.getRequestURI(),ex.getMessage());
	}
	
	@ExceptionHandler(ParseException.class)
	@ResponseBody
    public ResponseEntity<Response> parseExceptionHandler(Exception ex) {
        ex.printStackTrace();
        Response response = new Response();
        ResponseParams responseParams = new ResponseParams();
		responseParams.setErrmsg("parsing exception occurred");
		responseParams.setErr("ParseException");
		responseParams.setStatus("Internal Server Error");
		response.setParams(responseParams);
		ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.internalError.getErrorCode());
		responseCode.setResponseCode(ResponseCode.SERVER_ERROR.getResponseCode());
		//responseCode.setErrorMessage("Something went wrong, are you passing request in accordance with the contract");
		HttpHeaders responseHeaders = new HttpHeaders();
		return new ResponseEntity<>(response,responseHeaders,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
}

package com.infosys.lex.ext.exception;


public class ErrorInfo {


	String errorMsg;
	String ex;
	
	public ErrorInfo(String errorMsg,String ex)
	{
		this.errorMsg=errorMsg;
		this.ex= ex;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getEx() {
		return ex;
	}

	public void setEx(String ex) {
		this.ex = ex;
	}
	
	
	
	

    


}

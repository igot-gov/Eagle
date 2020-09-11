package com.tarento.walletService.exception;

public class ClientError {

    private String code;

    private String message;

    /**
     * Constructor
     */
    public ClientError() {
        super();
    }

    /**
     * Constructor with parameters
     *
     * @param code
     * @param message
     */
    public ClientError(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    /**
     * set code parameter
     *
     * @param value
     */
    public void setCode(String value) {
        code = value;

    }

    /**
     * set message parameter
     *
     * @param msg
     */
    public void setMessage(String msg) {
        message = msg;

    }

    /**
     * get message parameter
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * get code parameter
     *
     * @return
     */
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "ClientErrorInfo [code=" + code + ", message=" + message + "]";
    }

}
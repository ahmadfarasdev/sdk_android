package com.mobucks.androidsdk.logger.utils.net.exceptions;

public class HttpFailureException extends  Exception{
    private final int httpCode;
    public HttpFailureException(int httpCode) {
        this.httpCode=httpCode;
    }

    public HttpFailureException(String detailMessage,int httpCode) {
        super(detailMessage);
        this.httpCode=httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }
}

package com.netcracker.hotelbe.exception;

import org.springframework.http.HttpStatus;

public class CustomResponseEntityException extends RuntimeException {

    private HttpStatus httpStatus;

    public CustomResponseEntityException(String message, HttpStatus httpStatus){
        super(message);
        this.httpStatus = httpStatus;
    }

    public CustomResponseEntityException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public CustomResponseEntityException(String message) {
        super(message);
    }

    public CustomResponseEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}

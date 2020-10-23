package com.sv.core.exception;

/**
 * AppException that can be used by all
 * referring libraries
 */
public class AppException extends RuntimeException {

    public AppException() {
        this("Unknown error occurred.");
    }

    public AppException(String msg) {
        super(msg);
    }

    public AppException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}

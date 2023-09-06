package com.isariev.paymentservice.exception;

public class ConvertIdException extends RuntimeException {
    public ConvertIdException() {
    }

    public ConvertIdException(String message) {
        super(message);
    }

    public ConvertIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertIdException(Throwable cause) {
        super(cause);
    }

    public ConvertIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

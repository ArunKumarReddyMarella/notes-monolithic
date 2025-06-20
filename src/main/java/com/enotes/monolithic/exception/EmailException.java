package com.enotes.monolithic.exception;

public class EmailException extends RuntimeException {
    public EmailException(String failedToSendEmail) {
        super(failedToSendEmail);
    }
}

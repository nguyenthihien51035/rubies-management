package com.example.rubiesmanagement.exception;

public class NotFoundException extends BusinessException {
    public NotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}

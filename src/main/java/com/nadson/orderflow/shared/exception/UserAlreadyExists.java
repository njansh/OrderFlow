package com.nadson.orderflow.shared.exception;

public class UserAlreadyExists extends BusinessRuleException {
    public UserAlreadyExists(String message) {
        super(message);
    }
}

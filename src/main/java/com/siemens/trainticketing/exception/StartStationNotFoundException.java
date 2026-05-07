package com.siemens.trainticketing.exception;

public class StartStationNotFoundException extends RuntimeException {
    public StartStationNotFoundException(String message) {
        super(message);
    }
}
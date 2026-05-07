package com.siemens.trainticketing.exception;

public class EndStationNotFoundException extends RuntimeException {
    public EndStationNotFoundException(String message) {
        super(message);
    }
}
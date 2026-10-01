package com.hospital.appointment.exception;

public class InvalidRecordRequestException extends RuntimeException {
    public InvalidRecordRequestException(String message) {
        super(message);
    }
}

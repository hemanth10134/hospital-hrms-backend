package com.hospital.manpower.exception;

/**
 * Thrown when an operation is attempted that is not valid for the current
 * state of a manpower plan or position request (e.g. editing a submitted plan).
 */
public class InvalidPlanStateException extends RuntimeException {

    public InvalidPlanStateException(String message) {
        super(message);
    }
}

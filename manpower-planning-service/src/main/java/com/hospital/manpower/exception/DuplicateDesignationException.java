package com.hospital.manpower.exception;

/**
 * Thrown when the same designation appears more than once in a single plan's
 * designation-wise staffing list.
 */
public class DuplicateDesignationException extends RuntimeException {

    public DuplicateDesignationException(String message) {
        super(message);
    }
}

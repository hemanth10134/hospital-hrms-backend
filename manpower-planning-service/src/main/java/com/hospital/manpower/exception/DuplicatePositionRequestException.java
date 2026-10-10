package com.hospital.manpower.exception;

/**
 * Thrown when a new position request is raised for a department + designation
 * that already has an open (pending/under review) request.
 */
public class DuplicatePositionRequestException extends RuntimeException {

    public DuplicatePositionRequestException(String message) {
        super(message);
    }
}

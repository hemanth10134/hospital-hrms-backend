package com.hospital.manpower.exception;

/**
 * Thrown when creating a new designation master record whose code already
 * exists for the organization.
 */
public class DesignationAlreadyExistsException extends RuntimeException {

    public DesignationAlreadyExistsException(String message) {
        super(message);
    }
}

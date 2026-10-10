package com.hospital.manpower.exception;

/**
 * Thrown when creating a master record (designation, location, hospital,
 * department) whose name or code already exists under the same parent.
 */
public class DuplicateReferenceDataException extends RuntimeException {

    public DuplicateReferenceDataException(String message) {
        super(message);
    }
}

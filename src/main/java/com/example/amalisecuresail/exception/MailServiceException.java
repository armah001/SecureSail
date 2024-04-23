package com.example.amalisecuresail.exception;


import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom exception for MailService related errors.
 */
public class MailServiceException extends RuntimeException {

    private final String message;
    private final int status;
    private final String error;
    private final String path;


    /**
     * Constructs a MailServiceException with specified details.
     *
     * @param message The description of the exception.
     * @param status The HTTP status code.
     * @param error The error type or details.
     * @param path The path or identifier associated with the exception.
     */
    public MailServiceException(String message, int status, String error, String path) {
        this.message = message;
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }

    /**
     * Constructs a MailServiceException for field-specific errors.
     *
     * @param missingFields List of field names that are required but missing.
     */
    public MailServiceException(List<String> missingFields) {
        this("Fields " + formatFieldNames(missingFields) + " are required", 400, "Bad Request", "/mail/send");
    }
    private static String formatFieldNames(List<String> fieldNames) {
        return fieldNames.stream().collect(Collectors.joining(", ", "'", "'"));
    }
}

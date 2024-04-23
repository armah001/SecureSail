package com.example.amalisecuresail.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Data Transfer Object (DTO) for responses related to email operations.
 */
public class MailResponse {


    private String message;
    private int status;
    private String error;
    private String path;

    /**
     * Constructs a MailResponse with specified details.
     *
     * @param message The response message or outcome description.
     * @param status The status code of the operation.
     * @param error Error details, if any.
     * @param path The path or identifier for the operation.
     */
    public MailResponse(String message, int status, String error, String path) {
        this.message = message;
        this.status = status;
        this.error = error;
        this.path = path;
    }
}

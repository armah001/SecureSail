package com.example.amalisecuresail.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the response structure of application.
 */

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private final Status status;
    private Object data;

    /**
     * Class representing a response status information
     */
    @AllArgsConstructor
    @Data
    public static class Status {
        private int code;
        private Object message;
    }

}


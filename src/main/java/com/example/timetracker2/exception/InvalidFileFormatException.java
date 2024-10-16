package com.example.timetracker2.exception;

public class InvalidFileFormatException extends RuntimeException {
    public InvalidFileFormatException (String message) {
        super(message);
    }
}

package com.example.timetracker2.exception;

public class EmptyFileException extends RuntimeException {
    public EmptyFileException (String message) {
        super(message);
    }
}

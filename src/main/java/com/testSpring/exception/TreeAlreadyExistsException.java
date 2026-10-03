package com.testSpring.exception;

public class TreeAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TreeAlreadyExistsException(int id) {
        super("Tree already exists with id: " + id);
    }
}
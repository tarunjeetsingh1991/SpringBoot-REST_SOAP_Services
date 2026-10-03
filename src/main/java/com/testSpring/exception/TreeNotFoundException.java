package com.testSpring.exception;

public class TreeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TreeNotFoundException(int id) {
        super("Tree not found with id: " + id);
    }
}
package com.aayushatharva.sourcecenginequerycacher.exceptions;

public class InvalidConfigPathException extends RuntimeException {

    public InvalidConfigPathException(String path, Throwable cause) {
        super(String.format("Couldn't load config from path: %s", path), cause);
    }
}

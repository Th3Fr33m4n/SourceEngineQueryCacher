package com.aayushatharva.sourcecenginequerycacher.exceptions;

public class ConfigNotFoundException extends RuntimeException {
    public ConfigNotFoundException() {
        super("The config file was not found");
    }
}

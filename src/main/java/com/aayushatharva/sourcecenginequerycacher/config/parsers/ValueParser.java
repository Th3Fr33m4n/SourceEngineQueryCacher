package com.aayushatharva.sourcecenginequerycacher.config.parsers;

public interface ValueParser {
    String getValue(String key);
    boolean hasKey(String key);
}

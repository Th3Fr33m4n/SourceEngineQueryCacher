package com.aayushatharva.sourcecenginequerycacher.config.parsers;

import java.util.Properties;

public class PropertiesValueParser implements ValueParser {
    private Properties properties;

    public PropertiesValueParser(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getValue(String key) {
        return properties.getProperty(key);
    }

    @Override
    public boolean hasKey(String key) {
        return properties.containsKey(key);
    }
}

package com.aayushatharva.sourcecenginequerycacher.config.parsers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PropertiesValueParserTests {

    @Mock
    private Properties properties;
    @InjectMocks
    private PropertiesValueParser propertiesValueParser;

    @Test
    public void testGetValue() {
        when(properties.getProperty("version")).thenReturn("1.2");
        when(properties.getProperty("another_var")).thenReturn("redacted");

        var result = propertiesValueParser.getValue("version");
        var result2 = propertiesValueParser.getValue("another_var");

        assertEquals(result, "1.2");
        assertEquals(result2, "redacted");

        verify(properties).getProperty("version");
        verify(properties).getProperty("another_var");
        verifyNoMoreInteractions(properties);
    }

    @Test
    public void testGetMissingValue() {
        var result = propertiesValueParser.getValue("version");

        assertNull(result);

        verify(properties).getProperty("version");
        verifyNoMoreInteractions(properties);
    }
}

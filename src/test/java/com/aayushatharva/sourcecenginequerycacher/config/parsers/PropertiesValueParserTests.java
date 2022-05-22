package com.aayushatharva.sourcecenginequerycacher.config.parsers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
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
        when(properties.containsKey("present_key")).thenReturn(true);
        when(properties.containsKey("missing_key")).thenReturn(false);

        var result = propertiesValueParser.getValue("version");
        var result2 = propertiesValueParser.getValue("another_var");
        var result3 = propertiesValueParser.hasKey("missing_key");
        var result4 = propertiesValueParser.hasKey("present_key");

        assertEquals(result, "1.2");
        assertEquals(result2, "redacted");
        assertFalse(result3);
        assertTrue(result4);

        verify(properties).getProperty("version");
        verify(properties).getProperty("another_var");
        verify(properties).containsKey("present_key");
        verify(properties).containsKey("missing_key");
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

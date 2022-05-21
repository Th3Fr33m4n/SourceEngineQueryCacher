package com.aayushatharva.sourcecenginequerycacher.config.parsers;

import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CmdValueParserTests {

    @Mock
    private CommandLine cmd;
    @InjectMocks
    private CmdValueParser cmdValueParser;

    @Test
    public void testGetValue() {
        when(cmd.getOptionValue("version")).thenReturn("1.2");
        when(cmd.getOptionValue("another_var")).thenReturn("redacted");

        var result = cmdValueParser.getValue("version");
        var result2 = cmdValueParser.getValue("another_var");

        assertEquals(result, "1.2");
        assertEquals(result2, "redacted");

        verify(cmd).getOptionValue("version");
        verify(cmd).getOptionValue("another_var");
        verifyNoMoreInteractions(cmd);
    }

    @Test
    public void testGetMissingValue() {
        var result = cmdValueParser.getValue("version");

        assertNull(result);

        verify(cmd).getOptionValue("version");
        verifyNoMoreInteractions(cmd);
    }
}

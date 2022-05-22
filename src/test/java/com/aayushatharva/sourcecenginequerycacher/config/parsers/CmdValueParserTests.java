package com.aayushatharva.sourcecenginequerycacher.config.parsers;

import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
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
        when(cmd.hasOption("true_key")).thenReturn(true);
        when(cmd.hasOption("missing_key")).thenReturn(false);


        var result = cmdValueParser.getValue("version");
        var result2 = cmdValueParser.getValue("another_var");
        var result3 = cmdValueParser.hasKey("missing_key");
        var result4 = cmdValueParser.hasKey("true_key");

        assertEquals(result, "1.2");
        assertEquals(result2, "redacted");
        assertFalse(result3);
        assertTrue(result4);

        verify(cmd).getOptionValue("version");
        verify(cmd).getOptionValue("another_var");
        verify(cmd).hasOption("missing_key");
        verify(cmd).hasOption("true_key");
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

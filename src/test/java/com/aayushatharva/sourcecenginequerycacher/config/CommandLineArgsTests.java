package com.aayushatharva.sourcecenginequerycacher.config;

import org.junit.jupiter.api.Test;

import static com.aayushatharva.sourcecenginequerycacher.factories.CmdArgsFactory.buildArgList;
import static com.aayushatharva.sourcecenginequerycacher.factories.CmdArgsFactory.buildShortArgList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandLineArgsTests {

    @Test
    public void testcmdArgs() {
        var cmdArgs = CommandLineArgs.parse(buildArgList());
        assertTrue(cmdArgs.hasOption("help"));
        assertEquals(cmdArgs.getOptionValue("config"), "/path/to/config/Cacher.conf");
        assertEquals(cmdArgs.getOptionValue("threads"), "1");
        assertTrue(cmdArgs.hasOption("ppsStats"));
        assertTrue(cmdArgs.hasOption("bpsStats"));
        assertEquals(cmdArgs.getOptionValue("gameUpdateInterval"), "2");
        assertEquals(cmdArgs.getOptionValue("gameUpdateTimeout"), "3");
        assertEquals(cmdArgs.getOptionValue("maxChallengeCodes"), "4");
        assertEquals(cmdArgs.getOptionValue("challengeCacheCleanerInterval"), "5");
        assertEquals(cmdArgs.getOptionValue("challengeTTL"), "6");
        assertEquals(cmdArgs.getOptionValue("challengeCacheConcurrency"), "7");
        assertEquals(cmdArgs.getOptionValue("gameIp"), "0.0.0.0");
        assertEquals(cmdArgs.getOptionValue("gamePort"), "8");
        assertEquals(cmdArgs.getOptionValue("bind"), "0.0.0.0");
        assertEquals(cmdArgs.getOptionValue("port"), "9");
        assertEquals(cmdArgs.getOptionValue("receiveBufSize"), "10");
        assertEquals(cmdArgs.getOptionValue("sendBufSize"), "11");
        assertEquals(cmdArgs.getOptionValue("receiveAllocatorBufSize"), "12");
    }

    @Test
    public void testCmdShortArgs() {
        var cmdArgs = CommandLineArgs.parse(buildShortArgList());
        assertTrue(cmdArgs.hasOption("help"));
        assertEquals(cmdArgs.getOptionValue("config"), "/path/to/config/Cacher.conf");
        assertEquals(cmdArgs.getOptionValue("threads"), "1");
        assertTrue(cmdArgs.hasOption("ppsStats"));
        assertTrue(cmdArgs.hasOption("bpsStats"));
        assertEquals(cmdArgs.getOptionValue("receiveBufSize"), "2");
        assertEquals(cmdArgs.getOptionValue("sendBufSize"), "3");
        assertEquals(cmdArgs.getOptionValue("receiveAllocatorBufSize"), "4");
    }
}

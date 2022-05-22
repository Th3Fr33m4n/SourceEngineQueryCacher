package com.aayushatharva.sourcecenginequerycacher.config;

import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static com.aayushatharva.sourcecenginequerycacher.factories.CmdArgsFactory.buildArgList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ConfigTests {

    @Test
    public void testConfigFromFile() {
        var cmd = mock(CommandLine.class);
        Config.setup(cmd);
        assertNotNull(Config.threads);
        assertNotNull(Config.ppaStats);
        assertNotNull(Config.bpsStats);
        assertNotNull(Config.gameUpdateInterval);
        assertNotNull(Config.gameUpdateTimeout);
        assertNotNull(Config.maxChallengeCodes);
        assertNotNull(Config.challengeCacheCleanerInterval);
        assertNotNull(Config.challengeTTL);
        assertNotNull(Config.challengeCacheConcurrency);
        assertNotNull(Config.gameServer);
        assertNotNull(Config.localServer);
        assertNotNull(Config.receiveBufSize);
        assertNotNull(Config.sendBufSize);
        assertNotNull(Config.receiveAllocatorBufSize);
    }

    @Test
    public void testConfigFromCmdArgs() {
        var cmd = CommandLineArgs.parse(buildArgList());
        Config.setup(cmd);
        var svAddr = new InetSocketAddress("0.0.0.0", 9);
        var gameAddr = new InetSocketAddress("0.0.0.0", 8);
        assertEquals(Config.threads, 1);
        assertTrue(Config.ppaStats);
        assertTrue(Config.bpsStats);
        assertEquals(Config.gameUpdateInterval, 2);
        assertEquals(Config.gameUpdateTimeout, 3);
        assertEquals(Config.maxChallengeCodes, 4);
        assertEquals(Config.challengeCacheCleanerInterval, 5);
        assertEquals(Config.challengeTTL, 6);
        assertEquals(Config.challengeCacheConcurrency, 7);
        assertEquals(Config.localServer, svAddr);
        assertEquals(Config.gameServer, gameAddr);
        assertEquals(Config.receiveBufSize, 10);
        assertEquals(Config.sendBufSize, 11);
        assertEquals(Config.receiveAllocatorBufSize, 12);
    }

    @Test
    public void testConfigFromDefaults() {
        var cmd = CommandLineArgs.parse(buildArgList());
        Config.setup(cmd);
        var svAddr = new InetSocketAddress("0.0.0.0", 9);
        var gameAddr = new InetSocketAddress("0.0.0.0", 8);
        assertEquals(Config.threads, 1);
        assertTrue(Config.ppaStats);
        assertTrue(Config.bpsStats);
        assertEquals(Config.gameUpdateInterval, 2);
        assertEquals(Config.gameUpdateTimeout, 3);
        assertEquals(Config.maxChallengeCodes, 4);
        assertEquals(Config.challengeCacheCleanerInterval, 5);
        assertEquals(Config.challengeTTL, 6);
        assertEquals(Config.challengeCacheConcurrency, 7);
        assertEquals(Config.localServer, svAddr);
        assertEquals(Config.gameServer, gameAddr);
        assertEquals(Config.receiveBufSize, 10);
        assertEquals(Config.sendBufSize, 11);
        assertEquals(Config.receiveAllocatorBufSize, 12);
    }
}

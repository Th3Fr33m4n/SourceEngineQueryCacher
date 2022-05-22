package com.aayushatharva.sourcecenginequerycacher.factories;

public class CmdArgsFactory {

    public static String[] buildArgList() {
        return new String[] {
                "-help",
                "-config", "/path/to/config/Cacher.conf",
                "-threads", "1",
                "-ppsStats", "-bpsStats",
                "-gameUpdateInterval", "2",
                "-gameUpdateTimeout", "3",
                "-maxChallengeCodes", "4",
                "-challengeCacheCleanerInterval", "5",
                "-challengeTTL", "6",
                "-challengeCacheConcurrency", "7",
                "-gameIp", "0.0.0.0",
                "-gamePort", "8",
                "-bind", "0.0.0.0",
                "-port", "9",
                "-receiveBufSize", "10",
                "-sendBufSize", "11",
                "-receiveAllocatorBufSize", "12"
        };
    }

    public static String[] buildShortArgList() {
        return new String[] {
                "-h",
                "-c", "/path/to/config/Cacher.conf",
                "-t", "1",
                "-p",
                "-b",
                "-r", "2",
                "-s", "3",
                "-a", "4"
        };
    }
}

package com.aayushatharva.sourcecenginequerycacher.config;

import lombok.SneakyThrows;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class CommandLineArgs {
    private static final Options options;

    static {
        options = new Options()
                /*General Configuration*/
                .addOption("h", "help", false, "Display Usages")
                .addOption("c", "config", true, "Configuration File Path")
                .addOption("w", "threads", true, "Number of Threads")
                .addOption("p", "ppsStats", false, "Enable Packets per Second Stats")
                .addOption("b", "bpsStats", false, "Enable Bits per Second Stats")

                .addOption("gameUpdateInterval", true, "Game Server Update rate in Milliseconds")
                .addOption("gameUpdateTimeout", true, "Game Server Update Socket Timeout in Milliseconds")

                /* Challenge Code */
                .addOption("maxChallengeCodes", true, "Maximum Challenge Codes to be saved")
                .addOption("challengeCacheCleanerInterval", true, "Challenge Code Cache Cleaner Interval in Milliseconds")
                .addOption("challengeTTL", true, "Maximum Validity of Challenge Code in Milliseconds")
                .addOption("challengeCacheConcurrency", true, "Challenge Code Cache Concurrency")

                /* IP Addresses and Ports */
                .addOption("gameIp", true, "Game Server IP Address")
                .addOption("gamePort", true, "Game Server Port")
                .addOption("bind", true, "Local Server IP Address on which Cacher Server will bind and listen")
                .addOption("port", true, "Local Server Port on which Cacher Server will bind and listen")

                /* Buffers */
                .addOption("r", "receiveBufSize", true, "Server Receive Buffer Size")
                .addOption("s", "sendBufSize", true, "Server Send Buffer Size")
                .addOption("a", "receiveAllocatorBufSize", true, "Fixed Receive ByteBuf Allocator Buffer Size");
    }

    @SneakyThrows
    public static CommandLine parse(String[] args) {
        var parser = new DefaultParser();
        return parser.parse(options, args);
    }

    public static Options get() {
        return options;
    }
}

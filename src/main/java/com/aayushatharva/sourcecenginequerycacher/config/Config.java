package com.aayushatharva.sourcecenginequerycacher.config;

import com.aayushatharva.sourcecenginequerycacher.config.parsers.CmdValueParser;
import com.aayushatharva.sourcecenginequerycacher.config.parsers.PropertiesValueParser;
import com.aayushatharva.sourcecenginequerycacher.config.parsers.ValueParser;
import com.aayushatharva.sourcecenginequerycacher.exceptions.ConfigNotFoundException;
import com.aayushatharva.sourcecenginequerycacher.exceptions.InvalidConfigPathException;
import lombok.SneakyThrows;
import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

public final class Config {

    private static final Logger logger = LogManager.getLogger(Config.class);
    private static final String DEFAULT_CONFIG_NAME = "./Cacher.conf";

    public static Integer threads;
    public static Long gameUpdateInterval;
    public static Integer gameUpdateSocketTimeout;
    public static Long maxChallengeCodes;
    public static Long challengeCodeCacheCleanerInterval;
    public static Long challengeCodeTTL;
    public static int challengeCodeCacheConcurrency;

    // IP Addresses and Ports
    public static InetSocketAddress localServer;
    public static InetSocketAddress gameServer;

    // Buffers
    public static Integer receiveBufferSize;
    public static Integer sendBufferSize;
    public static Integer fixedReceiveAllocatorBufferSize;

    // Stats
    public static boolean stats_PPS;
    public static boolean stats_bPS;

    public static void setup(CommandLine cmd) {
        try {
            var configPath = resolveConfigPath(cmd);
            loadValuesFromConfig(configPath);
            logger.debug(String.format("Loaded config from: %s", configPath));
        } catch (ConfigNotFoundException e) {
            loadValuesFromCmdline(cmd);
        }

        if (logger.isDebugEnabled()) {
            displayConfig();
        }
    }

    private static boolean fileExists(String path) {
        if (path != null) {
            var configFile = new File(path);
            return configFile.exists() && !configFile.isDirectory();
        }
        return false;
    }

    private static String resolveConfigPath(CommandLine cmd) {
        //If `config` Parameter is present, parse the config file and load configuration.
        var configPath = cmd.getOptionValue("config");
        if (!fileExists(configPath)); {
            //If config parameter is not set or the file doesn't exist, try searching in the app dir.
            configPath = Paths.get(DEFAULT_CONFIG_NAME).normalize().toAbsolutePath().toString();

            if (!fileExists(configPath)) {
                throw new ConfigNotFoundException();
            }

            return configPath;
        }
    }

    private static void loadValuesFromConfig(String path) {
        try (var fis = new FileInputStream(path)) {
            var properties = new Properties();
            properties.load(fis);
            var parser = new PropertiesValueParser(properties);
            loadValues(parser);
            properties.clear();
        } catch (IOException e) {
            throw new InvalidConfigPathException(path, e);
        }
    }

    private static void loadValuesFromCmdline(CommandLine commandLine) {
        var parser = new CmdValueParser(commandLine);
        loadValues(parser);
    }

    private static void loadValues(ValueParser parser) {
        var gameServerAddress = getIPAddressProperty(parser, "gameIp", () -> InetAddress.getLoopbackAddress());
        var gameServerPort = getIntProperty(parser, "gamePort", 27015);
        var localServerAddress = getIPAddressProperty(parser, "bind", () -> InetAddress.getLoopbackAddress());
        var localPort = getIntProperty(parser, "port", 9110);

        threads = getIntProperty(parser, "threads", 2);
        stats_PPS = getBooleanProperty(parser, "ppsStats", true);
        stats_bPS = getBooleanProperty(parser, "bpsStats", true);
        gameUpdateInterval = getLongProperty(parser, "gameUpdateInterval", 2000);
        gameUpdateSocketTimeout = getIntProperty(parser, "gameUpdateTimeout", 1000);
        maxChallengeCodes = getLongProperty(parser, "maxChallengeCodes", 100000);
        challengeCodeCacheCleanerInterval = getLongProperty(parser, "challengeCacheCleanerInterval", 1000);
        challengeCodeTTL = getLongProperty(parser, "challengeTTL", 5000);
        challengeCodeCacheConcurrency = getIntProperty(parser, "challengeCacheConcurrency", 8);
        gameServer = new InetSocketAddress(gameServerAddress, gameServerPort);
        localServer = new InetSocketAddress(localServerAddress, localPort);
        receiveBufferSize = getIntProperty(parser, "receiveBufSize", 65535);
        sendBufferSize = getIntProperty(parser,"sendBufSize", 65535);
        fixedReceiveAllocatorBufferSize = getIntProperty(parser, "receiveAllocatorBufSize", 65535);
    }

    private static int getIntProperty(ValueParser parser, String key, int defaultValue) {
        return Optional.ofNullable(parser.getValue(key))
                .map(Integer::parseInt)
                .orElse(defaultValue);
    }

    private static long getLongProperty(ValueParser parser, String key, long defaultValue) {
        return Optional.ofNullable(parser.getValue(key))
                .map(Long::parseLong)
                .orElse(defaultValue);
    }

    private static boolean getBooleanProperty(ValueParser parser, String key, boolean defaultValue) {
        return Optional.ofNullable(parser.getValue(key))
                .map(Boolean::parseBoolean)
                .orElse(defaultValue);
    }

    private static InetAddress getIPAddressProperty(ValueParser parser, String key, Supplier defaultValue) {
        return Optional.ofNullable(parser.getValue(key))
                .map(Config::getIpByName)
                .orElseGet(defaultValue);
    }

    @SneakyThrows
    private static InetAddress getIpByName(String ip) {
        return InetAddress.getByName(ip);
    }

    private static void displayConfig() {
        logger.atDebug().log("----------------- CONFIGURATION -----------------");
        logger.atDebug().log("Threads: " + threads);
        logger.atDebug().log("PPS: " + stats_PPS);
        logger.atDebug().log("bPS: " + stats_bPS);

        logger.atDebug().log("GameUpdateInterval: " + gameUpdateInterval);
        logger.atDebug().log("GameUpdateSocketTimeout: " + gameUpdateSocketTimeout);

        logger.atDebug().log("MaxChallengeCode: " + maxChallengeCodes);
        logger.atDebug().log("ChallengeCodeCacheCleanerInterval: " + challengeCodeCacheCleanerInterval);
        logger.atDebug().log("ChallengeCodeCacheConcurrency: " + challengeCodeCacheConcurrency);

        logger.atDebug().log("LocalServerIPAddress: " + localServer.getAddress().getHostAddress());
        logger.atDebug().log("LocalServerPort: " + localServer.getPort());
        logger.atDebug().log("GameServerIPAddress: " + gameServer.getAddress().getHostAddress());
        logger.atDebug().log("GameServerPort: " + gameServer.getPort());

        logger.atDebug().log("ReceiveBufferSize: " + receiveBufferSize);
        logger.atDebug().log("SendBufferSize: " + sendBufferSize);
        logger.atDebug().log("FixedReceiveAllocatorBufferSize: " + fixedReceiveAllocatorBufferSize);
        logger.atDebug().log("-------------------------------------------------");
    }
}

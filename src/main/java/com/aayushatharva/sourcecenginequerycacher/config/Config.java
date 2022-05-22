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
    public static Integer gameUpdateTimeout;
    public static Long maxChallengeCodes;
    public static Long challengeCacheCleanerInterval;
    public static Long challengeTTL;
    public static int challengeCacheConcurrency;

    // IP Addresses and Ports
    public static InetSocketAddress localServer;
    public static InetSocketAddress gameServer;

    // Buffers
    public static Integer receiveBufSize;
    public static Integer sendBufSize;
    public static Integer receiveAllocatorBufSize;

    // Stats
    public static boolean ppaStats;
    public static boolean bpsStats;

    public static void setup(CommandLine cmd) {
        var properties = new Properties();
        loadProperties(cmd, properties);
        loadValues(new CmdValueParser(cmd), new PropertiesValueParser(properties));
        clearProperties(properties);

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

    private static void loadProperties(CommandLine cmd, Properties properties) {
        try {
            var configPath = resolveConfigPath(cmd);
            try (var fis = new FileInputStream(configPath)) {
                properties.load(fis);
            } catch (IOException e) {
                throw new InvalidConfigPathException(configPath, e);
            }
        } catch (ConfigNotFoundException e) {}
    }

    private static void clearProperties(Properties properties) {
        properties.clear();
    }

    private static void loadValues(CmdValueParser cmdArgs, PropertiesValueParser props) {
        var gameServerAddress = getIPAddressProperty(cmdArgs, props, "gameIp", () -> InetAddress.getLoopbackAddress());
        var gameServerPort = getIntProperty(cmdArgs, props, "gamePort", 27015);
        var localServerAddress = getIPAddressProperty(cmdArgs, props, "bind", () -> InetAddress.getLoopbackAddress());
        var localPort = getIntProperty(cmdArgs, props, "port", 9110);

        threads = getIntProperty(cmdArgs, props, "threads", 2);
        ppaStats = getBooleanProperty(cmdArgs, props, "ppsStats", true);
        bpsStats = getBooleanProperty(cmdArgs, props, "bpsStats", true);
        gameUpdateInterval = getLongProperty(cmdArgs, props, "gameUpdateInterval", 2000);
        gameUpdateTimeout = getIntProperty(cmdArgs, props, "gameUpdateTimeout", 1000);
        maxChallengeCodes = getLongProperty(cmdArgs, props, "maxChallengeCodes", 100000);
        challengeCacheCleanerInterval = getLongProperty(cmdArgs, props, "challengeCacheCleanerInterval", 1000);
        challengeTTL = getLongProperty(cmdArgs, props, "challengeTTL", 5000);
        challengeCacheConcurrency = getIntProperty(cmdArgs, props, "challengeCacheConcurrency", 8);
        gameServer = new InetSocketAddress(gameServerAddress, gameServerPort);
        localServer = new InetSocketAddress(localServerAddress, localPort);
        receiveBufSize = getIntProperty(cmdArgs, props, "receiveBufSize", 65535);
        sendBufSize = getIntProperty(cmdArgs, props,"sendBufSize", 65535);
        receiveAllocatorBufSize = getIntProperty(cmdArgs, props, "receiveAllocatorBufSize", 65535);
    }

    private static Optional<String> readPropertyWithFallback(ValueParser main, ValueParser fallback, String key) {
        return Optional.ofNullable(main.getValue(key))
                .or(() -> Optional.ofNullable(fallback.getValue(key)));
    }

    private static int getIntProperty(ValueParser main, ValueParser fallback, String key, int defaultValue) {
        return readPropertyWithFallback(main, fallback, key)
                .map(Integer::valueOf)
                .orElse(defaultValue);
    }

    private static long getLongProperty(ValueParser main, ValueParser fallback, String key, long defaultValue) {
        return readPropertyWithFallback(main, fallback, key)
                .map(Long::parseLong)
                .orElse(defaultValue);
    }

    private static boolean getBooleanProperty(ValueParser main, ValueParser fallback, String key, boolean defaultValue) {
        boolean prop;
        if (main.hasKey(key)) {
            prop = true;
        } else if (fallback.hasKey(key)) {
            prop = Boolean.valueOf(fallback.getValue(key));
        } else {
            prop = defaultValue;
        }

        return prop;
    }

    private static InetAddress getIPAddressProperty(ValueParser main, ValueParser fallback, String key, Supplier defaultValue) {
        return readPropertyWithFallback(main, fallback, key)
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
        logger.atDebug().log("PPS: " + ppaStats);
        logger.atDebug().log("bPS: " + bpsStats);

        logger.atDebug().log("GameUpdateInterval: " + gameUpdateInterval);
        logger.atDebug().log("GameUpdateSocketTimeout: " + gameUpdateTimeout);

        logger.atDebug().log("MaxChallengeCode: " + maxChallengeCodes);
        logger.atDebug().log("ChallengeCodeCacheCleanerInterval: " + challengeCacheCleanerInterval);
        logger.atDebug().log("ChallengeCodeCacheConcurrency: " + challengeCacheConcurrency);

        logger.atDebug().log("LocalServerIPAddress: " + localServer.getAddress().getHostAddress());
        logger.atDebug().log("LocalServerPort: " + localServer.getPort());
        logger.atDebug().log("GameServerIPAddress: " + gameServer.getAddress().getHostAddress());
        logger.atDebug().log("GameServerPort: " + gameServer.getPort());

        logger.atDebug().log("ReceiveBufferSize: " + receiveBufSize);
        logger.atDebug().log("SendBufferSize: " + sendBufSize);
        logger.atDebug().log("FixedReceiveAllocatorBufferSize: " + receiveAllocatorBufSize);
        logger.atDebug().log("-------------------------------------------------");
    }
}

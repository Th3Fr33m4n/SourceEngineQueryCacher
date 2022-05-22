package com.aayushatharva.sourcecenginequerycacher.cache;

import com.aayushatharva.sourcecenginequerycacher.Main;
import com.aayushatharva.sourcecenginequerycacher.config.Config;
import com.aayushatharva.sourcecenginequerycacher.utils.ByteBufUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.buffer.ByteBuf;

import java.time.Duration;

public final class CacheHub {

    /**
     * <p> ByteBuf for `A2S_INFO` Packet. </p>
     */
    public static final ByteBuf A2S_INFO = Main.BYTE_BUF_ALLOCATOR.buffer();

    /**
     * <p> ByteBuf for `A2S_PLAYER` Packet. </p>
     */
    public static final ByteBuf A2S_PLAYER = Main.BYTE_BUF_ALLOCATOR.buffer();

    /**
     * <p> ByteBuf for `A2S_RULES` Packet. </p>
     */
    public static final ByteBuf A2S_RULES = Main.BYTE_BUF_ALLOCATOR.buffer();

    /**
     * Challenge Code Cache
     */
    public static Cache<String, String> CHALLENGE_CACHE;

    private static CacheCleaner cacheCleaner;
    public static boolean isComplete() {
        var isAnyCacheEmpty = isEmpty(A2S_INFO) ||
                isEmpty(A2S_PLAYER) ||
                isEmpty(A2S_RULES);
        return !isAnyCacheEmpty;
    }
    private static boolean isEmpty(ByteBuf cache) {
        return cache == null || cache.readableBytes() == 0;
    }

    public static void shutdown() {
        if (cacheCleaner != null) {
            cacheCleaner.shutdown();
        }
        flush();
        release();
    }

    public static void flush() {
        if (CHALLENGE_CACHE != null) {
            CacheHub.CHALLENGE_CACHE.invalidateAll();
            CacheHub.CHALLENGE_CACHE.cleanUp();
        }
        CacheHub.A2S_INFO.clear();
        CacheHub.A2S_PLAYER.clear();
        CacheHub.A2S_RULES.clear();
    }

    private static void release() {
        ByteBufUtils.safeRelease(CacheHub.A2S_INFO);
        ByteBufUtils.safeRelease(CacheHub.A2S_PLAYER);
        ByteBufUtils.safeRelease(CacheHub.A2S_RULES);
    }

    public static void init() {
        if (cacheCleaner == null) {
            cacheCleaner = new CacheCleaner();
            cacheCleaner.start();
        }

        if (CHALLENGE_CACHE == null) {
            CHALLENGE_CACHE = CacheBuilder.newBuilder()
                    .maximumSize(Config.maxChallengeCodes)
                    .expireAfterWrite(Duration.ofMillis(Config.challengeTTL))
                    .concurrencyLevel(Config.challengeCacheConcurrency)
                    .build();
        }
    }
}

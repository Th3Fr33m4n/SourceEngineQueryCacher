package com.aayushatharva.sourcecenginequerycacher;

import com.aayushatharva.sourcecenginequerycacher.gameserver.a2sinfo.InfoClient;
import com.aayushatharva.sourcecenginequerycacher.gameserver.a2splayer.PlayerClient;
import com.aayushatharva.sourcecenginequerycacher.cache.CacheCleaner;
import com.aayushatharva.sourcecenginequerycacher.cache.CacheHub;
import com.aayushatharva.sourcecenginequerycacher.config.Config;
import com.aayushatharva.sourcecenginequerycacher.server.Handler;
import com.aayushatharva.sourcecenginequerycacher.server.Stats;
import com.aayushatharva.sourcecenginequerycacher.utils.ByteBufUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.unix.UnixChannelOption;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static final ByteBufAllocator BYTE_BUF_ALLOCATOR = PooledByteBufAllocator.DEFAULT;
    public static EventLoopGroup eventLoopGroup;
    private static Stats stats;
    private static CacheCleaner cacheCleaner;
    private static InfoClient infoClient;
    private static PlayerClient playerClient;

    public static void main(String[] args) {
        try {
            Config.setup(args);

            // Use Epoll when available
            if (!Epoll.isAvailable()) {
                // Epoll is requested but Epoll is not available so we'll throw error and shut down.
                System.err.println("Epoll Transport is not available, shutting down...");
                System.exit(1);
            }

            eventLoopGroup = new EpollEventLoopGroup(Config.Threads);
            bindChannelsAndSync(bootstrap());
            initComponents();
            start();
        } catch (Exception ex) {
            logger.atError().withThrowable(ex).log("Error while Initializing");
        }
    }

    private static Bootstrap bootstrap() {
        return new Bootstrap()
                .group(eventLoopGroup)
                .channelFactory(() -> new EpollDatagramChannel(InternetProtocolFamily.IPv4))
                .option(ChannelOption.ALLOCATOR, BYTE_BUF_ALLOCATOR)
                .option(ChannelOption.SO_SNDBUF, Config.SendBufferSize)
                .option(ChannelOption.SO_RCVBUF, Config.ReceiveBufferSize)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())
                .option(UnixChannelOption.SO_REUSEPORT, true)
                .option(EpollChannelOption.UDP_GRO, true) // Enable UDP GRO
                .handler(new Handler());
    }

    private static void bindChannelsAndSync(Bootstrap bootstrap) throws InterruptedException {
        var channels = new ArrayList<ChannelFuture>();

        for (int i = 0; i < Config.Threads; i++) {
            var channelFuture = bootstrap.bind(
                            Config.LocalServer.getAddress(),
                            Config.LocalServer.getPort())
                    .addListener((ChannelFutureListener)Main::defaultListener);

            channels.add(channelFuture);
        }

        // Wait for all bind sockets to start
        for (var channel : channels) {
            channel.sync();
        }
    }

    private static void defaultListener(ChannelFuture future) {
        if (future.isSuccess()) {
            logger.info("Server Started on Address: {}:{}",
                    ((InetSocketAddress) future.channel().localAddress()).getAddress().getHostAddress(),
                    ((InetSocketAddress) future.channel().localAddress()).getPort());
        } else {
            logger.error("Caught Error While Starting Server", future.cause());
            System.err.println("Shutting down...");
            System.exit(1);
        }
    }

    private static void initComponents() {
        stats = new Stats();
        cacheCleaner = new CacheCleaner();
        infoClient = new InfoClient("A2SInfoClient");
        playerClient = new PlayerClient("A2SPlayerClient");
    }

    private static void start() {
        stats.start();
        CacheHub.init();
        infoClient.start();
        playerClient.start();
    }

    /**
     * Shutdown everything
     */
    public void shutdown() throws ExecutionException, InterruptedException {
        var future = eventLoopGroup.shutdownGracefully();
        infoClient.shutdown();
        playerClient.shutdown();
        stats.shutdown();
        CacheHub.flushAndClose();
        future.get();
    }
}

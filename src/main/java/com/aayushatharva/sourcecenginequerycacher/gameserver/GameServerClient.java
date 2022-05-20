package com.aayushatharva.sourcecenginequerycacher.gameserver;

import com.aayushatharva.sourcecenginequerycacher.Main;
import com.aayushatharva.sourcecenginequerycacher.config.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import org.apache.logging.log4j.Logger;

public abstract class GameServerClient extends Thread {
    private boolean keepRunning = true;
    private SimpleChannelInboundHandler<DatagramPacket> handler;

    protected GameServerClient(String name, SimpleChannelInboundHandler<DatagramPacket> handler) {
        super(name);
        this.handler = handler;
    }

    protected abstract Logger getLogger();
    protected abstract ByteBuf getData();

    @SuppressWarnings("BusyWait")
    public void run() {
        try {
            var bootstrap = new Bootstrap()
                    .group(Main.eventLoopGroup)
                    .channelFactory(EpollDatagramChannel::new)
                    .option(ChannelOption.ALLOCATOR, Main.BYTE_BUF_ALLOCATOR)
                    .option(ChannelOption.SO_SNDBUF, Config.sendBufferSize)
                    .option(ChannelOption.SO_RCVBUF, Config.receiveBufferSize)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(Config.fixedReceiveAllocatorBufferSize))
                    .handler(handler);

            var channel = bootstrap.connect(Config.gameServer).sync().channel();

            while (keepRunning) {
                channel.writeAndFlush(getData()).sync();
                sleep(Config.gameUpdateInterval);
            }

            channel.closeFuture().sync();
        } catch (Exception ex) {
            getLogger().atError().withThrowable(ex).log("Error occurred");
        }
    }

    public void shutdown() {
        this.interrupt();
        keepRunning = false;
    }
}

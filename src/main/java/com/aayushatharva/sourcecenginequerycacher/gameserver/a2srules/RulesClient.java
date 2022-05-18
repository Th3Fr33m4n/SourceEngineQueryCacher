package com.aayushatharva.sourcecenginequerycacher.gameserver.a2srules;

import com.aayushatharva.sourcecenginequerycacher.Main;
import com.aayushatharva.sourcecenginequerycacher.config.Config;
import com.aayushatharva.sourcecenginequerycacher.constants.Packets;
import com.aayushatharva.sourcecenginequerycacher.gameserver.a2sinfo.InfoClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.epoll.EpollDatagramChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RulesClient extends Thread {

    private static final Logger logger = LogManager.getLogger(InfoClient.class);
    private boolean keepRunning = true;

    public RulesClient(String name) {
        super(name);
    }

    @SuppressWarnings("BusyWait")
    public void run() {
        try {
            var bootstrap = new Bootstrap()
                    .group(Main.eventLoopGroup)
                    .channelFactory(EpollDatagramChannel::new)
                    .option(ChannelOption.ALLOCATOR, Main.BYTE_BUF_ALLOCATOR)
                    .option(ChannelOption.SO_SNDBUF, Config.SendBufferSize)
                    .option(ChannelOption.SO_RCVBUF, Config.ReceiveBufferSize)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(Config.FixedReceiveAllocatorBufferSize))
                    .handler(new RulesHandler());

            var channel = bootstrap.connect(Config.GameServer).sync().channel();

            while (keepRunning) {
                channel.writeAndFlush(Packets.A2S_RULES_CHALLENGE_REQUEST_2.retainedDuplicate()).sync();
                sleep(Config.GameUpdateInterval);
            }

            channel.closeFuture().sync();
        } catch (Exception ex) {
            logger.atError().withThrowable(ex).log("Error occurred");
        }
    }

    public void shutdown() {
        this.interrupt();
        keepRunning = false;
    }
}

package com.aayushatharva.sourcecenginequerycacher.server;

import com.aayushatharva.sourcecenginequerycacher.cache.CacheHub;
import com.aayushatharva.sourcecenginequerycacher.config.Config;
import com.aayushatharva.sourcecenginequerycacher.server.handlers.A2SHandlerChain;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.aayushatharva.sourcecenginequerycacher.constants.Packets.*;
import static io.netty.channel.ChannelHandler.Sharable;

@Sharable
public final class Handler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger logger = LogManager.getLogger(Handler.class);

    private final A2SHandlerChain handlerChain = new A2SHandlerChain();

    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) {
        incrementStats(datagramPacket);

        if (!CacheHub.isComplete()) {
            logger.error("Dropping query request because Cache is not ready. A2S_INFO: {}, A2S_PLAYER: {}, A2S_RULES: {}",
                    CacheHub.A2S_INFO, CacheHub.A2S_PLAYER, CacheHub.A2S_RULES);
            return;
        }

        //Packet size not matching any known request will be dropped.
        if (hasValidLength(datagramPacket)) {
            handlerChain.apply(ctx, datagramPacket);
        }

        dropLog(datagramPacket);
    }

    private void incrementStats(DatagramPacket packet) {
        if (Config.ppaStats) {
            Stats.incrementPPS();
        }

        if (Config.bpsStats) {
            Stats.incrementBPS(packet.content().readableBytes());
        }
    }

    private boolean hasValidLength(DatagramPacket packet) {
        var contentLength = packet.content().readableBytes();
        return contentLength == A2S_INFO_REQUEST_LENGTH ||
                contentLength == A2S_PLAYER_REQUEST_LENGTH ||
                contentLength == A2S_RULES_REQUEST_LENGTH;
    }

    private void dropLog(DatagramPacket datagramPacket) {
        logger.debug("Dropping Packet of Length {} bytes from {}:{}", datagramPacket.content().readableBytes(),
                datagramPacket.sender().getAddress().getHostAddress(), datagramPacket.sender().getPort());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Caught Error", cause);
    }
}

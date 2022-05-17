package com.aayushatharva.sourcecenginequerycacher.gameserver.a2splayer;

import com.aayushatharva.sourcecenginequerycacher.cache.CacheHub;
import com.aayushatharva.sourcecenginequerycacher.constants.Packets;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.aayushatharva.sourcecenginequerycacher.utils.PacketUtils.matchesA2SChallengeResponse;
import static com.aayushatharva.sourcecenginequerycacher.utils.PacketUtils.matchesA2SPlayerResponse;

final class PlayerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger logger = LogManager.getLogger(PlayerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) {
        if (matchesA2SChallengeResponse(datagramPacket)) {
            var responseBuf = ctx.alloc().buffer()
                    .writeBytes(Packets.A2S_PLAYER_REQUEST_HEADER.retainedDuplicate())
                    .writeBytes(datagramPacket.content().slice(5, 4));

            ctx.writeAndFlush(responseBuf);
        } else if (matchesA2SPlayerResponse(datagramPacket)) {
            // Set new Packet Data
            CacheHub.A2S_PLAYER.clear().writeBytes(datagramPacket.content());

            logger.debug("New A2SPlayer Update Cached Successfully");
        } else {
            logger.error("Received unsupported A2S Player Response from Game Server: {}", ByteBufUtil.hexDump(datagramPacket.content()));
        }
    }
}

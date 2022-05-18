package com.aayushatharva.sourcecenginequerycacher.gameserver.a2srules;

import com.aayushatharva.sourcecenginequerycacher.cache.CacheHub;
import com.aayushatharva.sourcecenginequerycacher.constants.Packets;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.aayushatharva.sourcecenginequerycacher.utils.PacketUtils.matchesA2SChallengeResponse;
import static com.aayushatharva.sourcecenginequerycacher.utils.PacketUtils.matchesA2SRulesResponse;

final class RulesHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger logger = LogManager.getLogger(RulesHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) {
        if (matchesA2SChallengeResponse(datagramPacket)) {
            var responseBuf = ctx.alloc().buffer()
                    .writeBytes(Packets.A2S_RULES_REQUEST_HEADER.retainedDuplicate())
                    .writeBytes(datagramPacket.content().slice(Packets.A2S_HEADER_LENGTH, Packets.A2S_CHALLENGE_LENGTH));

            ctx.writeAndFlush(responseBuf);
        } else if (matchesA2SRulesResponse(datagramPacket)) {
            // Set new Packet Data
            CacheHub.A2S_RULES.clear().writeBytes(datagramPacket.content());

            logger.debug("New A2SRules Update Cached Successfully");
        } else {
            logger.error("Received unsupported A2S Rules Response from Game Server: {}", ByteBufUtil.hexDump(datagramPacket.content()));
        }
    }
}

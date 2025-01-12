package com.aayushatharva.sourcecenginequerycacher.server;

import com.aayushatharva.sourcecenginequerycacher.cache.CacheHub;
import com.aayushatharva.sourcecenginequerycacher.config.Config;
import com.aayushatharva.sourcecenginequerycacher.constants.Packets;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static com.aayushatharva.sourcecenginequerycacher.constants.Packets.A2S_INFO_REQUEST_LENGTH;
import static com.aayushatharva.sourcecenginequerycacher.constants.Packets.A2S_PLAYER_REQUEST_LENGTH;
import static com.aayushatharva.sourcecenginequerycacher.utils.HexUtils.toHexString;
import static com.aayushatharva.sourcecenginequerycacher.utils.PacketUtils.matchesA2SPlayerChallengeRequest;
import static com.aayushatharva.sourcecenginequerycacher.utils.PacketUtils.matchesA2SPlayerRequestHeader;
import static io.netty.channel.ChannelHandler.Sharable;

@Sharable
public final class Handler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger logger = LogManager.getLogger(Handler.class);

    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) {
        incrementStats(datagramPacket);

        if (!CacheHub.isComplete()) {
            logger.error("Dropping query request because Cache is not ready. A2S_INFO: {}, A2S_PLAYER: {}",
                    CacheHub.A2S_INFO, CacheHub.A2S_PLAYER);
            return;
        }

        /*
         * Packet size not matching any known request will be dropped.
         */
        if (hasValidLength(datagramPacket)) {
            if (ByteBufUtil.equals(Packets.A2S_INFO_REQUEST, datagramPacket.content())) {
                sendA2SInfoResponse(ctx, datagramPacket);
                return;
            } else if (matchesA2SPlayerRequestHeader(datagramPacket)) {

                /*
                 * 1. Packets equals to `A2S_PLAYER_CHALLENGE_REQUEST_1` or `A2S_PLAYER_CHALLENGE_REQUEST_2`
                 * then we'll send response of A2S_Player Challenge Packet.
                 *
                 * 2. Validate A2S_Player Challenge Response and send A2S_Player Packet.
                 */
                if (matchesA2SPlayerChallengeRequest(datagramPacket)) {
                    sendA2SChallenge(ctx, datagramPacket);
                } else {
                    sendA2SPlayerResponse(ctx, datagramPacket, ByteBufUtil.getBytes(datagramPacket.content()));
                }
                return;
            }
        }

        dropLog(datagramPacket);
    }

    private void incrementStats(DatagramPacket packet) {
        if (Config.Stats_PPS) {
            Stats.PPS.incrementAndGet();
        }

        if (Config.Stats_bPS) {
            Stats.BPS.addAndGet(packet.content().readableBytes());
        }
    }

    private boolean hasValidLength(DatagramPacket packet) {
        var contentLength = packet.content().readableBytes();
        return contentLength == A2S_INFO_REQUEST_LENGTH ||
                contentLength == A2S_PLAYER_REQUEST_LENGTH;
    }

    private void sendA2SInfoResponse(ChannelHandlerContext ctx, DatagramPacket datagramPacket) {
        ctx.writeAndFlush(new DatagramPacket(CacheHub.A2S_INFO.retainedDuplicate(), datagramPacket.sender()));
    }

    private void sendA2SChallenge(ChannelHandlerContext ctx, DatagramPacket datagramPacket) {
        var challenge = ChallengeGenerator.generateRandomChallenge();
        // Add Challenge to Cache
        CacheHub.CHALLENGE_CACHE.put(toHexString(challenge), datagramPacket.sender().getAddress().getHostAddress());

        // Send A2S PLAYER CHALLENGE Packet
        var byteBuf = ctx.alloc().buffer();
        byteBuf.writeBytes(Packets.A2S_CHALLENGE_RESPONSE.retainedDuplicate());
        byteBuf.writeBytes(challenge);
        ctx.writeAndFlush(new DatagramPacket(byteBuf, datagramPacket.sender()));
    }

    private void sendA2SPlayerResponse(ChannelHandlerContext ctx, DatagramPacket datagramPacket, byte[] Packet) {
        // Look for Challenge Code in Cache and load Client IP Address Value from it.
        String ipAddressOfClient = CacheHub.CHALLENGE_CACHE.getIfPresent(toHexString(Arrays.copyOfRange(Packet, 5, 9)));

        // If Client IP Address Value is not NULL it means we found the Challenge and now we'll validate it.
        if (ipAddressOfClient != null) {
            // Invalidate Cache since we found Challenge
            CacheHub.CHALLENGE_CACHE.invalidate(toHexString(Arrays.copyOfRange(Packet, 5, 9)));

            // Match Client Current IP Address against Cache Stored Client IP Address
            if (ipAddressOfClient.equals(datagramPacket.sender().getAddress().getHostAddress())) {
                ctx.writeAndFlush(new DatagramPacket(CacheHub.A2S_PLAYER.retainedDuplicate(), datagramPacket.sender()));
            }
        } else {
            logger.debug("Invalid Challenge Code received from {}:{} [REQUEST DROPPED]",
                    datagramPacket.sender().getAddress().getHostAddress(), datagramPacket.sender().getPort());
        }
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

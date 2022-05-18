package com.aayushatharva.sourcecenginequerycacher.server.handlers;

import com.aayushatharva.sourcecenginequerycacher.cache.CacheHub;
import com.aayushatharva.sourcecenginequerycacher.constants.Packets;
import com.aayushatharva.sourcecenginequerycacher.server.ChallengeGenerator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.apache.logging.log4j.Logger;

import static com.aayushatharva.sourcecenginequerycacher.utils.HexUtils.toHexString;
import static com.aayushatharva.sourcecenginequerycacher.utils.PacketUtils.*;

public abstract class A2SHandler {

    protected abstract Logger getLogger();
    protected abstract boolean matchesChallengeRequest(DatagramPacket datagramPacket);
    protected abstract ByteBuf getData();
    protected abstract boolean shouldValidateChallenge();

    public abstract boolean canHandle(DatagramPacket datagramPacket);
    public void handle(ChannelHandlerContext ctx, DatagramPacket datagramPacket) {
        if (shouldValidateChallenge()) {
            if (matchesChallengeRequest(datagramPacket)) {
                sendA2SChallenge(ctx, datagramPacket);
            } else {
                sendDataWithValidChallenge(ctx, datagramPacket, getData());
            }
        } else {
            sendData(ctx, datagramPacket, getData());
        }
    }

    private void sendA2SChallenge(ChannelHandlerContext ctx, DatagramPacket datagramPacket) {
        var challenge = ChallengeGenerator.generateRandomChallenge();
        // Add Challenge to Cache
        CacheHub.CHALLENGE_CACHE.put(toHexString(challenge), datagramPacket.sender().getAddress().getHostAddress());

        // Send A2S CHALLENGE Packet
        var byteBuf = ctx.alloc().buffer();
        byteBuf.writeBytes(Packets.A2S_CHALLENGE_RESPONSE.retainedDuplicate());
        byteBuf.writeBytes(challenge);
        ctx.writeAndFlush(new DatagramPacket(byteBuf, datagramPacket.sender()));
    }

    private void sendDataWithValidChallenge(ChannelHandlerContext ctx, DatagramPacket datagramPacket, ByteBuf responseData) {
        // Look for Challenge Code in Cache and load Client IP Address Value from it.
        var challenge = toHexString(getChallengeFromA2SRequest(datagramPacket));
        var ipAddressOfClient = CacheHub.CHALLENGE_CACHE.getIfPresent(challenge);

        // If Client IP Address Value is not NULL it means we found the Challenge and now we'll validate it.
        if (ipAddressOfClient != null) {
            // Invalidate Cache since we found Challenge
            CacheHub.CHALLENGE_CACHE.invalidate(challenge);

            // Match Client Current IP Address against Cache Stored Client IP Address
            if (ipAddressOfClient.equals(datagramPacket.sender().getAddress().getHostAddress())) {
                sendData(ctx, datagramPacket, responseData);
            }
        } else {
            getLogger().debug("Invalid Challenge Code received from {}:{} [REQUEST DROPPED]",
                    datagramPacket.sender().getAddress().getHostAddress(), datagramPacket.sender().getPort());
        }
    }

    private void sendData(ChannelHandlerContext ctx, DatagramPacket datagramPacket, ByteBuf responseData) {
        ctx.writeAndFlush(new DatagramPacket(responseData, datagramPacket.sender()));
    }
}

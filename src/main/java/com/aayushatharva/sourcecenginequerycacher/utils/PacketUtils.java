package com.aayushatharva.sourcecenginequerycacher.utils;

import com.aayushatharva.sourcecenginequerycacher.constants.Packets;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.socket.DatagramPacket;

import java.util.Arrays;

import static com.aayushatharva.sourcecenginequerycacher.constants.Packets.A2S_CHALLENGE_LENGTH;
import static com.aayushatharva.sourcecenginequerycacher.constants.Packets.A2S_HEADER_LENGTH;

public class PacketUtils {

    public static boolean matchesA2SPlayerChallengeRequest(DatagramPacket packet) {
        var content = packet.content();
        return ByteBufUtil.equals(content, Packets.A2S_PLAYER_CHALLENGE_REQUEST_1) ||
                ByteBufUtil.equals(content, Packets.A2S_PLAYER_CHALLENGE_REQUEST_2);
    }

    public static boolean matchesA2SPlayerRequestHeader(DatagramPacket packet) {
        return ByteBufUtil.equals(Packets.A2S_PLAYER_REQUEST_HEADER,
                packet.content().slice(0, A2S_HEADER_LENGTH));
    }

    public static boolean matchesA2SRulesRequestHeader(DatagramPacket packet) {
        return ByteBufUtil.equals(Packets.A2S_RULES_REQUEST_HEADER,
                packet.content().slice(0, A2S_HEADER_LENGTH));
    }

    public static boolean matchesA2SRulesChallengeRequest(DatagramPacket packet) {
        var content = packet.content();
        return ByteBufUtil.equals(content, Packets.A2S_RULES_CHALLENGE_REQUEST_1) ||
                ByteBufUtil.equals(content, Packets.A2S_RULES_CHALLENGE_REQUEST_2);
    }

    public static boolean matchesA2SChallengeResponse(DatagramPacket packet) {
        return ByteBufUtil.equals(Packets.A2S_CHALLENGE_RESPONSE,
                packet.content().slice(0, A2S_HEADER_LENGTH));
    }

    public static boolean matchesA2SPlayerResponse(DatagramPacket packet) {
        return ByteBufUtil.equals(Packets.A2S_PLAYER_RESPONSE_HEADER,
                packet.content().slice(0, A2S_HEADER_LENGTH));
    }

    public static boolean matchesA2SRulesResponse(DatagramPacket packet) {
        return ByteBufUtil.equals(Packets.A2S_RULES_RESPONSE_HEADER,
                packet.content().slice(0, A2S_HEADER_LENGTH));
    }

    public static byte[] getChallengeFromA2SRequest(DatagramPacket packet) {
        var content = ByteBufUtil.getBytes(packet.content());
        return Arrays.copyOfRange(content, A2S_HEADER_LENGTH, A2S_HEADER_LENGTH + A2S_CHALLENGE_LENGTH);
    }
}

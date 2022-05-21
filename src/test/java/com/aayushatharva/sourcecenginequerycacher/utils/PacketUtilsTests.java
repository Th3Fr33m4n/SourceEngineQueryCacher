package com.aayushatharva.sourcecenginequerycacher.utils;

import com.aayushatharva.sourcecenginequerycacher.constants.Packets;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.channel.socket.DatagramPacket;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PacketUtilsTests {

    @Test
    public void testA2SInfoRequest() {
        var packet = new DatagramPacket(Packets.A2S_INFO_REQUEST, new InetSocketAddress(27015));
        assertTrue(PacketUtils.matchesA2SInfoRequest(packet));
    }

    @Test
    public void testA2SPlayerChallengeRequest() {
        var packet = new DatagramPacket(Packets.A2S_PLAYER_CHALLENGE_REQUEST_1, new InetSocketAddress(27015));
        var packet2 = new DatagramPacket(Packets.A2S_PLAYER_CHALLENGE_REQUEST_1, new InetSocketAddress(27015));
        assertTrue(PacketUtils.matchesA2SPlayerChallengeRequest(packet));
        assertTrue(PacketUtils.matchesA2SPlayerChallengeRequest(packet2));
    }

    @Test
    public void testA2SPlayerRequestHeader() {
        var packet = new DatagramPacket(Packets.A2S_PLAYER_REQUEST_HEADER, new InetSocketAddress(27015));
        assertTrue(PacketUtils.matchesA2SPlayerRequestHeader(packet));
    }

    @Test
    public void testA2SRulesRequestHeader() {
        var packet = new DatagramPacket(Packets.A2S_RULES_REQUEST_HEADER, new InetSocketAddress(27015));
        assertTrue(PacketUtils.matchesA2SRulesRequestHeader(packet));
    }

    @Test
    public void testA2SRulesChallengeRequest() {
        var packet = new DatagramPacket(Packets.A2S_RULES_CHALLENGE_REQUEST_1, new InetSocketAddress(27015));
        var packet2 = new DatagramPacket(Packets.A2S_RULES_CHALLENGE_REQUEST_2, new InetSocketAddress(27015));
        assertTrue(PacketUtils.matchesA2SRulesChallengeRequest(packet));
        assertTrue(PacketUtils.matchesA2SRulesChallengeRequest(packet2));
    }

    @Test
    public void testA2SChallengeResponse() {
        var packet = new DatagramPacket(Packets.A2S_CHALLENGE_RESPONSE, new InetSocketAddress(27015));
        assertTrue(PacketUtils.matchesA2SChallengeResponse(packet));
    }

    @Test
    public void testA2SPlayerResponse() {
        var packet = new DatagramPacket(Packets.A2S_PLAYER_RESPONSE_HEADER, new InetSocketAddress(27015));
        assertTrue(PacketUtils.matchesA2SPlayerResponse(packet));
    }

    @Test
    public void testA2SRulesResponse() {
        var packet = new DatagramPacket(Packets.A2S_RULES_RESPONSE_HEADER, new InetSocketAddress(27015));
        assertTrue(PacketUtils.matchesA2SRulesResponse(packet));
    }

    @Test
    public void testGetChallengeFromRequest() {
        var challenge = new byte[] {5, 10, 15, 20};
        var byteBuf = new UnpooledDirectByteBuf(new UnpooledByteBufAllocator(true),9,9);
        var requestWithChallenge = byteBuf
                .writeBytes(Packets.A2S_PLAYER_REQUEST_HEADER.retainedDuplicate())
                .writeBytes(challenge);

        var packet = new DatagramPacket(requestWithChallenge, new InetSocketAddress(27015));
        var result = PacketUtils.getChallengeFromA2SRequest(packet);
        assertArrayEquals(challenge, result);
    }
}

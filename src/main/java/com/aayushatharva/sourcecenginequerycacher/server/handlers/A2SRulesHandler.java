package com.aayushatharva.sourcecenginequerycacher.server.handlers;

import com.aayushatharva.sourcecenginequerycacher.cache.CacheHub;
import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.aayushatharva.sourcecenginequerycacher.utils.PacketUtils.matchesA2SRulesChallengeRequest;
import static com.aayushatharva.sourcecenginequerycacher.utils.PacketUtils.matchesA2SRulesRequestHeader;

public class A2SRulesHandler extends A2SHandler {
    private static final Logger logger = LogManager.getLogger(A2SRulesHandler.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected boolean matchesChallengeRequest(DatagramPacket datagramPacket) {
        return matchesA2SRulesChallengeRequest(datagramPacket);
    }

    @Override
    public boolean canHandle(DatagramPacket datagramPacket) {
        return matchesA2SRulesRequestHeader(datagramPacket);
    }

    @Override
    protected ByteBuf getData() {
        return CacheHub.A2S_RULES.retainedDuplicate();
    }

    @Override
    protected boolean shouldValidateChallenge() {
        return true;
    }
}

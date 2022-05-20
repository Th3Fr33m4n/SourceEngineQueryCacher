package com.aayushatharva.sourcecenginequerycacher.gameserver.a2srules;

import com.aayushatharva.sourcecenginequerycacher.Main;
import com.aayushatharva.sourcecenginequerycacher.config.Config;
import com.aayushatharva.sourcecenginequerycacher.constants.Packets;
import com.aayushatharva.sourcecenginequerycacher.gameserver.GameServerClient;
import com.aayushatharva.sourcecenginequerycacher.gameserver.a2sinfo.InfoClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.epoll.EpollDatagramChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RulesClient extends GameServerClient {

    private static final Logger logger = LogManager.getLogger(InfoClient.class);
    private boolean keepRunning = true;

    public RulesClient() {
        super("A2SRulesClient", new RulesHandler());
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected ByteBuf getData() {
        return Packets.A2S_RULES_CHALLENGE_REQUEST_2.retainedDuplicate();
    }
}

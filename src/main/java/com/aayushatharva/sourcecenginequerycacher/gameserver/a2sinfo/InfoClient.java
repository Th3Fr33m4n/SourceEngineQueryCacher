package com.aayushatharva.sourcecenginequerycacher.gameserver.a2sinfo;

import com.aayushatharva.sourcecenginequerycacher.Main;
import com.aayushatharva.sourcecenginequerycacher.config.Config;
import com.aayushatharva.sourcecenginequerycacher.constants.Packets;
import com.aayushatharva.sourcecenginequerycacher.gameserver.GameServerClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.epoll.EpollDatagramChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class InfoClient extends GameServerClient {

    private static final Logger logger = LogManager.getLogger(InfoClient.class);

    public InfoClient() {
        super("A2SInfoClient", new InfoHandler());
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected ByteBuf getData() {
        return Packets.A2S_INFO_REQUEST.retainedDuplicate();
    }
}

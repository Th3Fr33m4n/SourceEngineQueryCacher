package com.aayushatharva.sourcecenginequerycacher.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.util.List;

public class A2SHandlerChain {

    private final List<A2SHandler> handlerChain = List.of(
            new A2SInfoHandler(),
            new A2SPlayerHandler(),
            new A2SRulesHandler()
    );

    public void apply(ChannelHandlerContext ctx, DatagramPacket datagramPacket) {
        for (var handler : handlerChain) {
            if (handler.canHandle(datagramPacket)) {
                handler.handle(ctx, datagramPacket);
                break;
            }
        }
    }
}

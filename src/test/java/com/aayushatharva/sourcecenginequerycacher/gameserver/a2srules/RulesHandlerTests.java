package com.aayushatharva.sourcecenginequerycacher.gameserver.a2srules;

import com.aayushatharva.sourcecenginequerycacher.cache.CacheHub;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static com.aayushatharva.sourcecenginequerycacher.constants.Packets.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RulesHandlerTests {

    private RulesHandler rulesHandler;
    private InetSocketAddress addr;
    private ByteBufAllocator allocator;
    private ChannelHandlerContext ctx;

    @BeforeEach
    public void setUp() {
        ctx = mock(ChannelHandlerContext.class);
        rulesHandler = new RulesHandler();
        addr = new InetSocketAddress("127.0.0.1", 27015);
        allocator = UnpooledByteBufAllocator.DEFAULT;
    }

    @AfterEach
    public void tearDown() {
        CacheHub.flush();
    }

    @Test
    public void testRulesHandlerMatchingChallenge() {
        var maxPacketSize = A2S_HEADER_LENGTH + A2S_CHALLENGE_LENGTH;
        var packet = new DatagramPacket(A2S_CHALLENGE_RESPONSE.retainedDuplicate(), addr);
        var request = new byte[maxPacketSize];
        var contentArr = new byte[] {0,0,0,0,0,1,2,3,4};
        var expectedRequest = new byte[] {0,0,0,0,0,1,2,3,4};

        A2S_CHALLENGE_RESPONSE.getBytes(0, contentArr);
        A2S_RULES_REQUEST_HEADER.getBytes(0, expectedRequest);

        when(ctx.alloc()).thenReturn(allocator);
        when(ctx.writeAndFlush(any(Object.class))).thenAnswer(invocation -> {
            var args = invocation.getArguments();
            var buff = (ByteBuf) args[0];
            buff.getBytes(0, request);
            return null;
        });

        assertEquals(CacheHub.A2S_RULES.readableBytes(), 0);

        rulesHandler.channelRead0(ctx, packet);

        assertEquals(CacheHub.A2S_RULES.readableBytes(), 0);
        assertArrayEquals(expectedRequest, request);
        verify(ctx).alloc();
        verify(ctx).writeAndFlush(any(ByteBuf.class));
        verifyNoMoreInteractions(ctx);
    }

    @Test
    public void testRulesHandlerMatchingRules() {
        var content = allocator.directBuffer()
                .writeBytes(A2S_RULES_RESPONSE_HEADER.retainedDuplicate())
                .writeBytes(new byte[] {1,2,3,4,5,6,7,8,9})
                .asReadOnly();

        var contentArr = new byte[14];
        var storedContent = new byte[14];

        content.getBytes(0, contentArr);

        var packet = new DatagramPacket(content.retainedDuplicate(), addr);

        assertEquals(CacheHub.A2S_RULES.readableBytes(), 0);

        rulesHandler.channelRead0(ctx, packet);

        assertEquals(CacheHub.A2S_RULES.readableBytes(), content.readableBytes());
        CacheHub.A2S_RULES.getBytes(0, storedContent);
        assertArrayEquals(contentArr, storedContent);
        verifyNoInteractions(ctx);
    }
}

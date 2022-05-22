package com.aayushatharva.sourcecenginequerycacher.gameserver.a2sinfo;

import com.aayushatharva.sourcecenginequerycacher.cache.CacheHub;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

public class InfoHandlerTests {

    private InfoHandler infoHandler;
    private InetSocketAddress addr;
    private ByteBufAllocator allocator;
    @Mock
    private ChannelHandlerContext ctx;

    @BeforeEach
    public void setUp() {
        ctx = mock(ChannelHandlerContext.class);
        infoHandler = new InfoHandler();
        addr = new InetSocketAddress("127.0.0.1", 27015);
        allocator = UnpooledByteBufAllocator.DEFAULT;
    }

    @AfterEach
    public void tearDown() {
        CacheHub.flush();
    }

    @Test
    public void testInfoHandler() {
        var contentArr = new byte[]{-1, -1, -1, -1, -1};
        var content = allocator.directBuffer()
                .writeBytes(contentArr)
                .asReadOnly();
        var packet = new DatagramPacket(content.retainedDuplicate(), addr);

        assertEquals(CacheHub.A2S_INFO.readableBytes(), 0);

        infoHandler.channelRead0(ctx, packet);

        assertEquals(CacheHub.A2S_INFO.readableBytes(), 5);
        var result = new byte[5];
        CacheHub.A2S_INFO.getBytes(0, result);
        assertArrayEquals(result, contentArr);
        verifyNoInteractions(ctx);
    }
}

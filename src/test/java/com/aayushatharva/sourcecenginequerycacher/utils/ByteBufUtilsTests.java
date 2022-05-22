package com.aayushatharva.sourcecenginequerycacher.utils;

import io.netty.buffer.ByteBuf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ByteBufUtilsTests {

    @Mock
    private ByteBuf byteBuf;

    @Test
    public void testSafeReleaseWithUsedRef() {
        when(byteBuf.refCnt()).thenReturn(1);
        ByteBufUtils.safeRelease(byteBuf);
        verify(byteBuf).refCnt();
        verify(byteBuf).release();
    }

    @Test
    public void testSafeReleaseWithoutRefs() {
        when(byteBuf.refCnt()).thenReturn(0);
        ByteBufUtils.safeRelease(byteBuf);
        verify(byteBuf).refCnt();
        verifyNoMoreInteractions(byteBuf);
    }
}

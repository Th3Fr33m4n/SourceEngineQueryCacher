package com.aayushatharva.sourcecenginequerycacher.constants;

import com.aayushatharva.sourcecenginequerycacher.Main;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class Packets {

    /**
     * FFFFFFFF41
     */
    public static final ByteBuf A2S_CHALLENGE_RESPONSE = Main.BYTE_BUF_ALLOCATOR.directBuffer()
            .writeBytes(new byte[]{-1, -1, -1, -1, 65})
            .asReadOnly();

    /**
     * FFFFFFFF54536F7572636520456E67696E6520517565727900
     */
    public static final ByteBuf A2S_INFO_REQUEST = Main.BYTE_BUF_ALLOCATOR.directBuffer()
            .writeBytes(new byte[]{-1, -1, -1, -1, 84, 83, 111,
                    117, 114, 99, 101, 32, 69, 110, 103, 105, 110, 101,
                    32, 81, 117, 101, 114, 121, 0})
            .asReadOnly();

    /**
     * FFFFFFFF49
     */
    public static final ByteBuf A2S_INFO_RESPONSE_HEADER = Main.BYTE_BUF_ALLOCATOR.directBuffer()
            .writeBytes(new byte[]{-1, -1, -1, -1, 73})
            .asReadOnly();
    
    /**
     * FFFFFFFF6D
     */
    public static final ByteBuf A2S_INFO_RESPONSE_HEADER_2 = Main.BYTE_BUF_ALLOCATOR.directBuffer()
            .writeBytes(new byte[]{-1, -1, -1, -1, 109})
            .asReadOnly();

    /**
     * FFFFFFFF5500000000
     */
    public static final ByteBuf A2S_PLAYER_CHALLENGE_REQUEST_1 = Main.BYTE_BUF_ALLOCATOR.directBuffer()
            .writeBytes(new byte[]{-1, -1, -1, -1, 85, 0, 0, 0, 0})
            .asReadOnly();

    /**
     * FFFFFFFF55FFFFFFFF
     */
    public static final ByteBuf A2S_PLAYER_CHALLENGE_REQUEST_2 = Main.BYTE_BUF_ALLOCATOR.directBuffer()
            .writeBytes(new byte[]{-1, -1, -1, -1, 85, -1, -1, -1, -1})
            .asReadOnly();

    /**
     * FFFFFFFF55
     */
    public static final ByteBuf A2S_PLAYER_REQUEST_HEADER = Main.BYTE_BUF_ALLOCATOR.directBuffer()
            .writeBytes(new byte[]{-1, -1, -1, -1, 85})
            .asReadOnly();

    /**
     * FFFFFFFF44
     */
    public static final ByteBuf A2S_PLAYER_RESPONSE_HEADER = Main.BYTE_BUF_ALLOCATOR.directBuffer()
            .writeBytes(new byte[]{-1, -1, -1, -1, 68})
            .asReadOnly();

    /**
     * FFFFFFFF5600000000
     */
    public static final ByteBuf A2S_RULES_CHALLENGE_REQUEST_1 = Main.BYTE_BUF_ALLOCATOR.directBuffer()
            .writeBytes(new byte[] {-1, -1, -1, -1, 86, 0, 0, 0, 0})
            .asReadOnly();

    /**
     * FFFFFFFF56FFFFFFFF
     */
    public static final ByteBuf A2S_RULES_CHALLENGE_REQUEST_2 = Main.BYTE_BUF_ALLOCATOR.directBuffer()
            .writeBytes(new byte[] {-1, -1, -1, -1, 86, -1, -1, -1, -1})
            .asReadOnly();

    /**
     * FFFFFFFF56
     */
    public static final ByteBuf A2S_RULES_REQUEST_HEADER = Main.BYTE_BUF_ALLOCATOR.directBuffer()
            .writeBytes(new byte[] {-1, -1, -1, -1, 86})
            .asReadOnly();

    /**
     * FFFFFFFF45
     */
    public static final ByteBuf A2S_RULES_RESPONSE_HEADER = Main.BYTE_BUF_ALLOCATOR.directBuffer()
            .writeBytes(new byte[] {-1, -1, -1, -1, 69})
            .asReadOnly();

    /**
     * FFFFFFFE45
     */
    public static final ByteBuf A2S_RULES_RESPONSE_HEADER_SPLIT = Main.BYTE_BUF_ALLOCATOR.directBuffer()
            .writeBytes(new byte[] {-1, -1, -1, -2, 69})
            .asReadOnly();

    public static final int A2S_HEADER_LENGTH = 5;
    public static final int A2S_CHALLENGE_LENGTH = 4;
    public static final int A2S_INFO_REQUEST_LENGTH = A2S_INFO_REQUEST.readableBytes();
    public static final int A2S_PLAYER_REQUEST_LENGTH = A2S_PLAYER_CHALLENGE_REQUEST_1.readableBytes();
}

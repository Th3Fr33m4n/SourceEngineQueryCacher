package com.aayushatharva.sourcecenginequerycacher.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HexUtilsTests {

    @Test
    public void validateToHexString() {
        var bytes = new byte[]{-1, -1, -1, -1, 73};
        var expectedString = "FFFFFFFF49";

        var result = HexUtils.toHexString(bytes);

        assertEquals(expectedString, result);
    }
}

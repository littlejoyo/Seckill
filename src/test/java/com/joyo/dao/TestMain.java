package com.joyo.dao;

import org.junit.Test;

public class TestMain {
    @Test
    public void testByte()
    {
        String str = "abc";
        byte[] bytes = str.getBytes();
        System.out.println(bytes.length);

    }
}

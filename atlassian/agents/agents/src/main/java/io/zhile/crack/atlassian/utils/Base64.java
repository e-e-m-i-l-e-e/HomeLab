/*
 * Decompiled with CFR 0.152.
 */
package io.zhile.crack.atlassian.utils;

public class Base64 {
    public static byte[] decode(String val) {
        return java.util.Base64.getDecoder().decode(val);
    }

    public static String encode(byte[] data) {
        return java.util.Base64.getEncoder().encodeToString(data);
    }
}


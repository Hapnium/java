package com.hapnium.core;

import java.util.Base64;

public class DatabaseUtil {
    public static String encode(String data) {
        return encode(data.getBytes());
    }

    public static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static String decodeData(String encodedData) {
        return new String(decode(encodedData));
    }

    public static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}

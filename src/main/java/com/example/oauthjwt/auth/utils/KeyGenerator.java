package com.example.oauthjwt.auth.utils;

import java.util.UUID;

public final class KeyGenerator {

    public static String generateKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

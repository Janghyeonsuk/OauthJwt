package com.example.oauthjwt.auth.cookie;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class Aes256 {

    private static final String ALGORITHM = "AES";
    private static final Key SECRET_KEY = generateKey();

    // 데이터 암호화
    public static String encrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
            byte[] encrypted = cipher.doFinal(data);
            return Base64.getEncoder().encodeToString(encrypted); // Base64로 인코딩하여 문자열로 반환
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }

    // 데이터 복호화
    public static byte[] decrypt(byte[] encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
            return cipher.doFinal(Base64.getDecoder().decode(encryptedData)); // Base64로 디코딩 후 복호화
        } catch (Exception e) {
            throw new RuntimeException("AES decryption failed", e);
        }
    }

    // AES Secret Key 생성
    private static Key generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256); // AES256을 위한 키 크기 설정
            SecretKey secretKey = keyGen.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate AES key", e);
        }
    }
}


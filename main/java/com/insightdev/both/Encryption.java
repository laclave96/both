package com.insightdev.both;

import android.util.Base64;

import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int AES_KEY_LENGTH = 128;

    public static String getSecretAESKeyAsString() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(AES_KEY_LENGTH, new SecureRandom()); // The AES key size in number of bits
        SecretKey secKey = generator.generateKey();
        String encodedKey = Base64.encodeToString(secKey.getEncoded(), Base64.DEFAULT);
        return encodedKey;
    }

    // Encrypt text using AES key
    public static String encryptTextUsingAES(String plainText, String aesKeyString) throws Exception {
        byte[] decodedKey = Base64.decode(aesKeyString, Base64.DEFAULT);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        byte[] iv = Tools.getRandomNonce();

        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] cipherText = aesCipher.doFinal(plainText.getBytes());

        byte[] cipherTextWithIv = ByteBuffer.allocate(iv.length + cipherText.length)
                .put(iv)
                .put(cipherText)
                .array();

        return  Base64.encodeToString(cipherTextWithIv,Base64.DEFAULT);
    }

    // Decrypt text using AES key
    public static String decryptTextUsingAES(String encryptedText, String aesKeyString) throws Exception {

        byte[] decodedKey = Base64.decode(aesKeyString, Base64.DEFAULT);
        byte[] cText = Base64.decode(encryptedText, Base64.DEFAULT);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        byte[] iv  = Arrays.copyOfRange(cText, 0, IV_LENGTH_BYTE);
        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        aesCipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] bytePlainText = aesCipher.doFinal(cText,IV_LENGTH_BYTE,(cText.length - IV_LENGTH_BYTE));

        return new String(bytePlainText);
    }

    // Get RSA keys. Uses key size of 2048.
    public static Map<String, Object> generateRSAKeys() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, new SecureRandom());

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put("private", privateKey);
        keys.put("public", publicKey);
        return keys;
    }

    public static String decryptText(String encryptedText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.decode(encryptedText, Base64.DEFAULT)));
    }

    // Encrypt AES Key using RSA private key
    public static String encryptText(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.encodeToString(cipher.doFinal(plainText.getBytes()), Base64.DEFAULT);
    }
}

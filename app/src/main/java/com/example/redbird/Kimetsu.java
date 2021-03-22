package com.example.redbird;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class Kimetsu {
    static Cipher cipher;
    byte[] arrayBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    static SecretKey key;
    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec ks;
    private SecretKeyFactory skf;


    public Kimetsu() throws Exception {
        myEncryptionKey = "ThisIsSpartaThisIsSparta";
        myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
        ks = new DESedeKeySpec(arrayBytes);
        skf = SecretKeyFactory.getInstance(myEncryptionScheme);
        cipher = Cipher.getInstance(myEncryptionScheme);
        key = skf.generateSecret(ks);
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public static void main(String[] args) throws Exception {
//        /*
//         create key
//         If we need to generate a new key use a KeyGenerator
//         If we have existing plaintext key use a SecretKeyFactory
//        */
//        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//        keyGenerator.init(128); // block size is 128bits
//        SecretKey secretKey = keyGenerator.generateKey();
//
//        /*
//          Cipher Info
//          Algorithm : for the encryption of electronic data
//          mode of operation : to avoid repeated blocks encrypt to the same values.
//          padding: ensuring messages are the proper length necessary for certain ciphers
//          mode/padding are not used with stream cyphers.
//         */
//        cipher = Cipher.getInstance("AES"); //SunJCE provider AES algorithm, mode(optional) and padding schema(optional)
//
//        String plainText = "AES Symmetric Encryption Decryption";
//        System.out.println("Plain Text Before Encryption: " + plainText);
//
//        String encryptedText = encrypt(plainText, secretKey);
//        System.out.println("Encrypted Text After Encryption: " + encryptedText);
//
//        String decryptedText = decrypt(encryptedText, secretKey);
//        System.out.println("Decrypted Text After Decryption: " + decryptedText);
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String plainText)
            throws Exception {
        byte[] plainTextByte = plainText.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        return encryptedText;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String encryptedText)
            throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }
}
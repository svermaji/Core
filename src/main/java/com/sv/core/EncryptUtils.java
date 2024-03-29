package com.sv.core;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * From https://www.quickprogrammingtips.com/java/how-to-securely-store-passwords-in-java.html
 */
public class EncryptUtils {

    // Get a encrypted password using PBKDF2 hash algorithm
    public static String getEncryptedPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String algorithm = "PBKDF2WithHmacSHA1";
        int derivedKeyLength = 160; // for SHA1
        int iterations = 20000; // NIST specifies 10000

        byte[] saltBytes = Base64.getDecoder().decode(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, iterations, derivedKeyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

        byte[] encBytes = f.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(encBytes);
    }

    // Returns base64 encoded salt
    public static String getNewSalt() throws NoSuchAlgorithmException {
        // Don't use Random!
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        // NIST recommends minimum 4 bytes. We use 8.
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        EncryptUtils e = new EncryptUtils();
        String salt = e.getNewSalt();
        System.out.println(salt);
        System.out.println(e.getEncryptedPassword("password", salt));
    }

}

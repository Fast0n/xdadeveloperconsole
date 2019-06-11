package com.fast0n.xdalabsconsole.Classes;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public void createKeys() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        this.publicKey = kp.getPublic();
        this.privateKey = kp.getPrivate();
    }

    public void importPublicKey(byte[] binaryKey) throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(binaryKey);
        this.publicKey = kf.generatePublic(keySpecX509);
    }

    public void importPrivateKey(byte[] binaryKey) throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(binaryKey);
        this.privateKey = kf.generatePrivate(keySpecPKCS8);
    }

    public byte[] encrypt(String plainText) throws Exception {

        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, this.publicKey);

        return encryptCipher.doFinal(plainText.getBytes());
    }

    public String decrypt(byte[] binary) throws Exception {

        Cipher decriptCipher = Cipher.getInstance("RSA");
        decriptCipher.init(Cipher.DECRYPT_MODE, this.privateKey);

        return new String(decriptCipher.doFinal(binary));
    }

}

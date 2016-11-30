package com.example.zacharius.sma;

import android.content.Context;
import android.util.Base64;
import android.util.Base64.*;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Created by zacharius on 11/30/16.
 */
public class Crypto
{
    public static KeyPair keygen(){
        KeyPairGenerator keyGen = null;
        try{
            SecureRandom rando = new SecureRandom();
            rando.generateSeed(1024);
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024, rando);

            KeyPair key = keyGen.generateKeyPair();
            return key;

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }

    public static String encrypt(byte[] plaintext, PublicKey key){
        Cipher cipher = null;

        try{
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.encodeToString(cipher.doFinal(plaintext), Base64.DEFAULT);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }



    }

    public static String decrypt(String ciphertext, PrivateKey key)
    {
        Cipher cipher = null;
        byte[] cipherBytes = Base64.decode(ciphertext, Base64.DEFAULT);

        try
        {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(cipherBytes));

        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static PublicKey stringToPublicKey(String s) {



        byte[] c = null;
        KeyFactory keyFact = null;
        PublicKey returnKey = null;

        try {
            c = Base64.decode(s,Base64.DEFAULT);
            keyFact = KeyFactory.getInstance("DSA", "SUN");
        } catch (Exception e) {
            System.out.println("Error in Keygen");
            e.printStackTrace();
        }


        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(c);
        try {
            returnKey = keyFact.generatePublic(x509KeySpec);
        } catch (Exception e) {

            System.out.println("Error in Keygen2");
            e.printStackTrace();

        }

        return returnKey;

    }
    public static PrivateKey stringToPrivateKey(String s) {

        byte[] c = null;
        KeyFactory keyFact = null;
        PrivateKey returnKey = null;

        try {

            c = Base64.decode(s, Base64.DEFAULT);
            keyFact = KeyFactory.getInstance("DSA", "SUN");
        } catch (Exception e) {

            System.out.println("Error in first try catch of stringToPrivateKey");
            e.printStackTrace();
        }


        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(c);
        try {   //the next line causes the crash
            returnKey = keyFact.generatePrivate(x509KeySpec);
        } catch (Exception e) {

            System.out.println("Error in stringToPrivateKey");
            e.printStackTrace();
        }

        return returnKey;

    }

    public static String publicKeyToString(PublicKey p) {

        byte[] publicKeyBytes = p.getEncoded();

        return Base64.encodeToString(publicKeyBytes, Base64.DEFAULT);

    }
    public static String privateKeyToString(PrivateKey p) {

        byte[] privateKeyBytes = p.getEncoded();
        return Base64.encodeToString(privateKeyBytes, Base64.DEFAULT);
    }
}

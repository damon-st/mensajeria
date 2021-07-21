package com.damon.messenger.util;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static final String ALGO ="AES";
    private byte[] keyValue;

    public AES(String key){
        keyValue = key.getBytes();
    }
    public String encrypt(String Data)throws Exception{
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());

        byte[] encryptedValue =  Base64.encode(encVal,Base64.DEFAULT);
        String textFinal = new String(encryptedValue, StandardCharsets.UTF_8);
        return textFinal;
    }

    public String decrypt(String encryptedData) throws Exception{
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decordedValue = Base64.decode(encryptedData,Base64.DEFAULT);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }


    private Key generateKey() throws Exception{
        Key key = new SecretKeySpec(keyValue,ALGO);
        return key;
    }

}

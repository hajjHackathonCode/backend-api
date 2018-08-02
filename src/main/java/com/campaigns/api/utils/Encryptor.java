package com.campaigns.api.utils;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service("encryptor")
public class Encryptor
{
    private String algorithm;
    private SecretKey secretKey;

    public String encrypt(String plain) throws Exception
    {
        Cipher cipher = Cipher.getInstance(algorithm);
        byte[] plainPasswordBytes = plain.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedPasswordBytes = cipher.doFinal(plainPasswordBytes);
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(encryptedPasswordBytes);
    }

    public String decrypt(String encoded) throws Exception
    {
        try
        {
            Cipher cipher = Cipher.getInstance(algorithm);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] encryptedPasswordBytes = decoder.decode(encoded);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedPasswordBytes);
            return new String(decryptedBytes);
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
            throw ex;
        }
    }

    public String getAlgorithm()
    {
        return algorithm;
    }

    public void setAlgorithm(String algorithm)
    {
        this.algorithm = algorithm;
    }

    public SecretKey getSecretKey() throws Exception
    {
        return secretKey;
    }

    public void setSecretKey(String secretCode) throws Exception
    {
        byte[] decode = Base64.getDecoder().decode(secretCode.getBytes());
        this.secretKey = new SecretKeySpec(decode, algorithm);
    }


}

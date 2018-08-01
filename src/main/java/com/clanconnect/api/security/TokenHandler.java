package com.clanconnect.api.security;

import com.clanconnect.api.utils.Utils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.io.Serializable;


public class TokenHandler
{

    public String createTokenForSerializable(Serializable serializable, String secret) throws IOException
    {
        String json = Utils.transferSerializableToJsonString(serializable);
        return Jwts.builder().setSubject(json).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public <T extends Serializable> T parseSerializableFromToken(String token, Class<T> clazz, String secret) throws IOException
    {
        String decodedToken = Utils.transferBase64StringToString(token);
        String userJson = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(decodedToken)
                .getBody()
                .getSubject();
        return Utils.transferJsonStringToSerializable(userJson, clazz);
    }
}

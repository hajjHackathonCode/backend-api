package com.clanconnect.api.authentication;

import com.clanconnect.api.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


final class AuthenticationTokenHandler
{

    private final String secret;
    private final AuthenticationService authService;

    AuthenticationTokenHandler(String secret, AuthenticationService authService)
    {
        this.secret = Base64.getEncoder().encodeToString(secret.getBytes());
        this.authService = authService;
    }

    /**
     * @return UserEntityBean from the parsed token
     */
    User parseUserFromToken(String token)
    {
        try
        {
            // parse the token and get the user name
            String username = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
            return authService.loadUserByUsername(username);
        }
        catch (Exception ex)
        {
            throw new BadCredentialsException("Bad Token", ex);
        }
    }

    /**
     * @return user token
     */
    String createTokenForUser(User user)
    {
        // create a token for the user.
        Date now = new Date();

        // the validity of the token is 30 days
        Date expiration = new Date(now.getTime() + TimeUnit.DAYS.toMillis(30l));
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

}

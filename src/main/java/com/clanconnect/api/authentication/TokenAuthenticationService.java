package com.clanconnect.api.authentication;

import com.clanconnect.api.model.User;
import com.clanconnect.api.utils.Encryptor;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class TokenAuthenticationService
{

    private static final String AUTH_HEADER_NAME = "Authorization";
    private final AuthenticationTokenHandler tokenHandler;

    public TokenAuthenticationService(AuthenticationService userService, Encryptor encryptor, @Value("${app.secret}") String secret) throws Exception
    {
//        secret = encryptor.decrypt(secret);
        tokenHandler = new AuthenticationTokenHandler(secret, userService);
    }

    public String addAuthentication(UserAuthentication authentication) throws IOException
    {
        // to generate a token
        final User user = authentication.getDetails();
        return tokenHandler.createTokenForUser(user);
    }

    Authentication getAuthentication(HttpServletRequest request) throws BadCredentialsException
    {
        // get the token from client request
        String token = request.getHeader(AUTH_HEADER_NAME);

        // validate the token
        if (!ESAPI.validator().isValidInput("HTTP header value: " + AUTH_HEADER_NAME, token, "HTTPHeaderValue", 5000, true))
        {
            throw new BadCredentialsException("Invalid Token");
        }
        if (token == null || !token.startsWith("Bearer "))
        {
            throw new BadCredentialsException("No JWT token found in request headers");
        }

        final User user = tokenHandler.parseUserFromToken(token.substring(7));
        if (user != null)
        {
            return new UserAuthentication(user);
        }

        throw new BadCredentialsException("Missing Token");
    }
}

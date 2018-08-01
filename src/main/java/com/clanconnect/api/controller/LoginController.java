/**
 *
 */
package com.clanconnect.api.controller;

import com.clanconnect.api.authentication.AuthenticationService;
import com.clanconnect.api.authentication.TokenAuthenticationService;
import com.clanconnect.api.authentication.UserAuthentication;
import com.clanconnect.api.model.Credential;
import com.clanconnect.api.model.LoginResponse;
import com.clanconnect.api.model.UserInfo;
import com.clanconnect.api.validator.CredentialValidator;
import com.clanconnect.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LoginController
{

    private CredentialValidator credentialValidator;
    private AuthenticationService userDetailsService;
    private AuthenticationManager authenticationManager;
    private TokenAuthenticationService tokenAuthenticationService;

    @Value("${api.version}")
    private String apiVersion;

    @Autowired
    public LoginController(CredentialValidator credentialValidator, AuthenticationService userDetailsService,
                           AuthenticationManager authenticationManager, TokenAuthenticationService tokenAuthenticationService)
    {
        this.credentialValidator = credentialValidator;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @InitBinder
    private void initBinder(WebDataBinder binder)
    {
        binder.setValidator(credentialValidator);
    }

    @RequestMapping(value = {"/api/login"}, method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> getLoginToken(@RequestBody @Validated Credential credential, BindingResult result, HttpServletRequest request) throws Exception
    {
        if (result.hasErrors())
        {
            throw new ServletRequestBindingException("'username', 'password' or 'uuid' is't valid");
        }
        try
        {
            // create locked user
            Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credential.getUsername().trim().toLowerCase(), credential.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //load the user info
            User user = userDetailsService.loadUserByUsername(credential.getUsername().trim().toLowerCase());

            //Generate a login token
            String token = tokenAuthenticationService.addAuthentication(new UserAuthentication(user));

            //Return the security token object with the user info and categories
            LoginResponse loginResponse = new LoginResponse(token, apiVersion, new UserInfo(user.getFirstName(), user.getLastName(), user.getUsername()));
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        }
        catch (BadCredentialsException e)
        {
            //create audit entry for user login
            e.printStackTrace();
            throw new BadCredentialsException("Failed to authenticate user: " + credential.getUsername(), e);
        }
    }
}

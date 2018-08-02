package com.campaigns.api.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class RestLoginFilter extends AbstractAuthenticationProcessingFilter
{

    private final TokenAuthenticationService tokenAuthenticationService;

    public RestLoginFilter(String urlMapping, TokenAuthenticationService tokenAuthenticationService, AuthenticationManager authManager)
    {
        super(urlMapping);
        this.tokenAuthenticationService = tokenAuthenticationService;

        //set the success and failure event handler
        this.setAuthenticationSuccessHandler(new RestUrlAuthenticationSuccessHandler());
        this.setAuthenticationFailureHandler(new RestUrlAuthenticationFailureHandler());
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException
    {
        //authenticate the user using the token authentication service
        Authentication authentication = tokenAuthenticationService.getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response)
    {
        //all the end points requires the user to be authenticated except the login end point and registration
        return !("/api/login".equals(new UrlPathHelper().getPathWithinApplication(request)))
                && request.getRequestURI().contains("/api") && super.requiresAuthentication(request, response);
    }
}
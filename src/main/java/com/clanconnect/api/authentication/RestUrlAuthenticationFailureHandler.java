package com.clanconnect.api.authentication;

import org.owasp.esapi.filters.SecurityWrapperResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler
{


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException
    {
    	
    	SecurityWrapperResponse secureResponse = new SecurityWrapperResponse(response);
    	response.setContentType("application/json");
    	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    	secureResponse.getOutputStream().println("{ \"error\": \"Authentication Failure\" }");
    }
}

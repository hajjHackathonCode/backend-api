package com.campaigns.api.authentication;


import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestUrlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler
{
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response)
    {
        //determine the end point and return the requested URL
        String context = request.getContextPath();
        String fullURL = request.getRequestURI();
        return fullURL.substring(fullURL.indexOf(context) + context.length());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException
    {
        //forward to the requested end point
        String url = determineTargetUrl(httpServletRequest, httpServletResponse);
        httpServletRequest.getRequestDispatcher(url).forward(httpServletRequest, httpServletResponse);
    }


}

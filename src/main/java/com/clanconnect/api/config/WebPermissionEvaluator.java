package com.clanconnect.api.config;

import com.clanconnect.api.model.Authority;
import com.clanconnect.api.model.User;
import com.clanconnect.api.repository.UserRepository;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

public class WebPermissionEvaluator implements PermissionEvaluator
{
    private UserRepository authProvider;
    private String adminGroup;

    public WebPermissionEvaluator(UserRepository authProvider, String adminGroup)
    {
        this.authProvider = authProvider;
        this.adminGroup = adminGroup;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission)
    {
        try
        {
            if (permission == null)
                return false;
            String role = permission.toString();
            if ("admin".equalsIgnoreCase(role))
                role = adminGroup;
            User user = this.authProvider.findByUsername(authentication.getName());
            for (Authority authority : user.getAuthorities())
            {
                if (authority != null && authority.getAuthority().contains(role))
                {
                    return true;
                }
            }
            return false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission)
    {
        return false;
    }
}

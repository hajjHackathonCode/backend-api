package com.clanconnect.api.authentication;

import com.clanconnect.model.User;
import com.clanconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService//, AuthenticationManager
{

    private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    private UserRepository userRepository;

    @Autowired
    public AuthenticationService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public final User loadUserByUsername(String username) throws UsernameNotFoundException
    {
        //load the user from the repository
        final User user = userRepository.findByUsername(username);
        if (user == null)
        {
            throw new UsernameNotFoundException("user not found");
        }
        detailsChecker.check(user);
        return user;
    }

    //@Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        return null;
    }
}

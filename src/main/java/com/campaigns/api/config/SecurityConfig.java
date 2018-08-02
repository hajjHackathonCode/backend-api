package com.campaigns.api.config;

import com.campaigns.api.authentication.AuthenticationService;
import com.campaigns.api.authentication.TokenAuthenticationService;
import com.campaigns.api.security.ExceptionAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{


    @Configuration
//    @EnableMongoAuditing
    @Order(1)
    public static class ApiSecurityConfig extends WebSecurityConfigurerAdapter
    {
        private final ExceptionAuthenticationEntryPoint exceptionAuthenticationEntryPoint;
        private final TokenAuthenticationService tokenAuthenticationService;
        private final AuthenticationService authenticationService;

        @Autowired
        public ApiSecurityConfig(ExceptionAuthenticationEntryPoint exceptionAuthenticationEntryPoint, TokenAuthenticationService tokenAuthenticationService, AuthenticationService authenticationService)
        {
            this.exceptionAuthenticationEntryPoint = exceptionAuthenticationEntryPoint;
            this.tokenAuthenticationService = tokenAuthenticationService;
            this.authenticationService = authenticationService;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception
        {

            // to secure APIs
//            http.csrf().disable().exceptionHandling().authenticationEntryPoint(exceptionAuthenticationEntryPoint).and()
//                    // session management configuration
//                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
//                    // allow anonymous POSTs to login
//                    .antMatchers(HttpMethod.POST, "/api/login").permitAll()
//                    .antMatchers("/admin/login", "/admin", "/admin/").permitAll()
//                    // all other request need to be authenticated
//                    .antMatchers("/admin/**").hasRole("ADMIN")
//                    .antMatchers("/api/**").hasRole("USER")
//                    //.authenticated().and()
//                    .and()
//                    // Custom JWT based authentication
//                    .addFilterBefore(new RestLoginFilter("/api/**", tokenAuthenticationService, authenticationService), UsernamePasswordAuthenticationFilter.class)
//                    // add custom CORS filter
//                    .addFilterBefore(new CORSFilter(), ChannelProcessingFilter.class);

            //to allow all APIs
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).and().httpBasic()
                    .disable()
                    .authorizeRequests()
                    .antMatchers("/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .csrf()
                    .disable();
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception
        {
            return super.authenticationManagerBean();
        }

//        @Bean
//        public AuditorAware<String> AuditorProvider()
//        {
//            return () -> SecurityContextHolder.getContext().getAuthentication().getName();
//        }
    }
}

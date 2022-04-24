package com.backend.apiserver.security;

import com.backend.apiserver.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
@AllArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * TokenProvider
     */
    private JwtUtils jwtUtils;

    /**
     * UserDetailsService
     */
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // get jwt from request
        String jwt = jwtUtils.parseJwt(request);

        if (StringUtils.hasText(jwt) && jwtUtils.validateToken(jwt)) {
            // get username from jwt
            String username = jwtUtils.getUsername(jwt);
            // get user info by username from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (Objects.nonNull(userDetails)) {
                // If user is valid, set set information to Security Context
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}

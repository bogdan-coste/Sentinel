package com.sentinel.security.filter;

import com.sentinel.entity.UserEntity;
import com.sentinel.entity.UserLoginEntity;
import com.sentinel.service.UserLoginSessionService;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CookieAuthenticationFilter implements Filter {

    @PostConstruct
    public void init() {
        log.info("CookieAuthenticationFilter bean initialized");
    }

    private static final Logger log = LoggerFactory.getLogger(CookieAuthenticationFilter.class);

    private final UserLoginSessionService sessionService;

    public CookieAuthenticationFilter(UserLoginSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        System.out.println(">>> CookieAuthenticationFilter executed for URI: " + ((HttpServletRequest) request).getRequestURI());

        log.info("=== CookieAuthenticationFilter invoked for URI: {} ===", ((HttpServletRequest) request).getRequestURI());

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        log.debug("Filter invoked for path: {}", path);

        if ("/api/users/login".equals(path) || "/api/users/register".equals(path) || "/api/users/verify".equals(path)) {
            log.debug("Public endpoint, skipping authentication");
            log.info("Before chain.doFilter, authentication = {}", SecurityContextHolder.getContext().getAuthentication());
            chain.doFilter(request, response);
            log.info("After chain.doFilter, authentication = {}", SecurityContextHolder.getContext().getAuthentication());
            return;
        }

        Cookie[] cookies = httpRequest.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION_TOKEN".equals(cookie.getName())) {
                    token = cookie.getValue();
                    log.debug("Found SESSION_TOKEN cookie with value: {}", token);
                    break;
                }
            }
        } else {
            log.debug("No cookies present in request");
        }

        if (token != null) {
            log.debug("Attempting to validate token: {}", token);
            Optional<UserLoginEntity> sessionOpt = sessionService.findValidSession(token);
            if (sessionOpt.isPresent()) {
                UserEntity userEntity = sessionOpt.get().getUserEntity();
                log.info("Valid session for user: {} (email: {})", userEntity.getUsername(), userEntity.getEmail());

                Authentication auth = new UsernamePasswordAuthenticationToken(userEntity, null, List.of());

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);

                httpRequest.setAttribute("SPRING_SECURITY_CONTEXT", context);

                httpRequest.setAttribute("authenticatedUser", userEntity);

                log.info("Authentication set in SecurityContextHolder: {}", context.getAuthentication());
            }else {
                log.warn("Token not found or expired: {}", token);
            }
        } else {
            log.debug("No SESSION_TOKEN cookie found");
        }

        chain.doFilter(request, response);
        log.info("After chain.doFilter, authentication = {}", SecurityContextHolder.getContext().getAuthentication());
    }
}
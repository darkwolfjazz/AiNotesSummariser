package com.NotesSummary.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class DebugRequestFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            System.out.println("🔐 DEBUG FILTER --> Authenticated user: " + auth.getName());
            System.out.println("🔐 DEBUG FILTER --> Authorities: " + auth.getAuthorities());
        } else {
            System.out.println("🔐 DEBUG FILTER --> No authentication found");
        }

        filterChain.doFilter(request, response);
    }

    }

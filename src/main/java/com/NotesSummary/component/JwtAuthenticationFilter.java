package com.NotesSummary.component;

import com.NotesSummary.service.CustomerDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private CustomerDetailService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader=request.getHeader("Authorization");
        String jwt;
        String username;
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        jwt=authHeader.substring(7);
        System.out.println("JWT :" +jwt);
        username=jwtUtil.extractUsername(jwt);
        System.out.println("Username from token:" +username);
        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails=userDetailsService.loadUserByUsername(username);
            System.out.println("Authorities:"+userDetails.getAuthorities());
            System.out.println("Is token valid:"+jwtUtil.validateToken(jwt,userDetails));

            if(jwtUtil.validateToken(jwt,userDetails)){
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
                        userDetails,null,userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Auth Token :" +authToken);
            }
        }
        filterChain.doFilter(request,response);
    }
//temp change
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/user/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                path.startsWith("/swagger-resources/") ||
                path.startsWith("/webjars/") ||
                path.equals("/login");
    }
}

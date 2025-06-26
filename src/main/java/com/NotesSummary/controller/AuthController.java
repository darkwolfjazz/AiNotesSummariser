package com.NotesSummary.controller;

import com.NotesSummary.component.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public Map<String,String>login(@RequestBody Map<String,String>user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.get("email"), user.get("password"))
            );
            var userDetails = userDetailsService.loadUserByUsername(user.get("email"));
            String token = jwtUtil.generateToken(userDetails.getUsername());
            return Map.of(
                    "token", token,
                    "message", "Login Successful"
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("message","Invalid credentials")).getBody();
        }
    }

}

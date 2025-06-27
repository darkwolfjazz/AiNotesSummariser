package com.NotesSummary.config;

import com.NotesSummary.component.DebugRequestFilter;
import com.NotesSummary.component.JwtAuthenticationFilter;
import com.NotesSummary.service.CustomerDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class AppConfig {



  @Autowired
  private JwtAuthenticationFilter jwtFilter;

  @Autowired
  private DebugRequestFilter debugRequestFilter;

  @Autowired
  private CustomerDetailService userDetailService;

  @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf-> csrf.disable())
            .authorizeHttpRequests(auth->
                    auth.requestMatchers("/api/user/**","/login",
                                    "/swagger-ui/**", "/v3/api-docs/**",
                                    "/swagger-resources/**", "/webjars/**").permitAll()
                            .requestMatchers(HttpMethod.POST,"/api/summary/**").hasAuthority("ROLE_USER")
                            .anyRequest().authenticated()
            )
            .sessionManagement(sess->sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider(userDetailService))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(debugRequestFilter, JwtAuthenticationFilter.class);
    return http.build();

}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



}
